import json
import pika
from flask import Blueprint, request, jsonify, session
from flask_sqlalchemy import SQLAlchemy
import secrets

from database import db
from Models import Account
routes = Blueprint("routes", __name__)

@routes.route('/register', methods=["POST"])
def register():
    data = request.get_json() or {}
    for field in ("username", "password", "balance","location"):
        if field not in data:
            return jsonify({"error": f"Missing field: {field}"}), 400

    if Account.query.filter_by(username=data["username"]).first():
        return jsonify({"error": "Username already exists"}), 400

    user = Account(
        username=data["username"],
        password=data["password"],
        balance=data["balance"],
        role="Customer",
        location=data["location"]
    )
    db.session.add(user)
    db.session.commit()
    session["username"]   = user.username
    session["id"]=user.id
    session["user_role"]  = user.role
    return jsonify({"message": f"Registered {user.username}"}), 201



@routes.route('/login', methods=["POST"])
def login():
    data = request.get_json() or {}
    for field in ("username", "password"):
        if field not in data:
            return jsonify({"error": f"Missing field: {field}"}), 400

    user = Account.query.filter_by(username=data["username"]).first()
    if not user or user.password != data["password"]:
        return jsonify({"error": "Invalid credentials"}), 401

    session["username"]  = user.username
    session["id"]=user.id
    session["user_role"] = user.role
    return jsonify({"message": "Login successful"}), 200


@routes.route('/logout', methods=['POST'])
def logout():
    session.clear()
    return jsonify({"message": "Logged out successfully"}), 200


@routes.route('/me', methods=['GET'])
def me():
    user_id = session.get("id")
    if not user_id:
        return jsonify({"error": "Not authenticated"}), 401

    user = Account.query.get(user_id)
    if not user:
        session.clear()
        return jsonify({"error": "User no longer exists"}), 401

    return jsonify({
        "id": user.id,
        "username": user.username,
        "role": user.role
    }), 200
    
    
@routes.route('/list_all', methods=["GET"])
def list_all_accounts():
    accounts = Account.query.all()
    return jsonify([account.to_json() for account in accounts]), 200


@routes.route('/list_all_customer', methods=["GET"])
def list_all_customers():
    id=session.get("id")
    if not id:
        return jsonify({"error": "Not logged in"}), 401
    user = Account.query.filter_by(id=id).first()
    if user.role !='Admin':
        return jsonify({"error": "Not an admin"}), 403
    accounts = Account.query.filter_by(role="Customer").all()
    return jsonify([account.to_json() for account in accounts]), 200


@routes.route('/list_all_companies', methods=["GET"])
def list_all_companies():
    id=session.get("id")
    if not id:
        return jsonify({"error": "Not logged in"}), 401
    user = Account.query.filter_by(id=id).first()
    if user.role !='Admin':
        return jsonify({"error": "Not an admin"}), 403
    accounts = Account.query.filter_by(role="Company").all()
    return jsonify([account.to_json() for account in accounts]), 200


@routes.route('/create_company',methods=["POST"])
def create_company():
    data = request.get_json() or {}
    for field in ("username","location"):
        if field not in data:
            return jsonify({"error": f"Missing field: {field}"}), 400
    id=session.get("id")
    if not id:
        return jsonify({"error": "Not logged in"}), 401
    user = Account.query.filter_by(id=id).first()
    if user.role !='Admin':
        return jsonify({"error": "Not an admin"}), 403
    Company = Account.query.filter_by(username=data["username"]).first()
    if Company:
        return jsonify({"error": "There is a company with the same username "}), 401
    Company = Account(
        username=data["username"],
        password=secrets.token_urlsafe(8),
        location=data["location"],
        role="Company"
    )
    db.session.add(Company)
    db.session.commit()
    return jsonify({"message": "Company created successfully",
    "username": Company.username,
    "password": Company.password}), 200
    
    

@routes.route('/get_location',methods=["GET"])
def location():
    id=session.get("id")
    if not id:
        return jsonify({"error": "Not logged in"}), 401
    user = Account.query.filter_by(id=id).first()
    location=user.location
    shipping = Account.query.filter_by(location=location,role="shipping").first()
    if not shipping:
        return jsonify({"error": "No shipping company in your area"}), 404
    return jsonify({
        "id":       shipping.id,
        "username": shipping.username,
        "location": shipping.location,
        "balance":  shipping.balance
    }),200
        
           
RABBITMQ_PARAMS = pika.ConnectionParameters(
    host="localhost",
    port=5672,
    credentials=pika.PlainCredentials("guest", "guest")
)
EXCHANGE     = "order_exchange"
ROUTING_KEY  = "customer"
QUEUE_NAME   = "customer_request_queue"

def on_customer_request(ch, method, props, body, flask_app):
    """
    Callback for each incoming RPC request from OrderService.
    Parses JSON {customerId, cost, timestamp}, checks/deducts balance,
    and replies "YES"/"NO" to props.reply_to.
    """
    with flask_app.app_context():
        try:
            min_charge=10.0
            payload = json.loads(body)
            print(payload)
            cid = payload.get("customerId")
            cost = payload.get("cost", 0)
            deduct = payload.get("deductCost")
            acct = Account.query.get(cid)
            ok = (acct is not None and acct.balance >= cost and cost>min_charge)
            print(ok)
            if ok and deduct=="true":   
                print(f"[CustomerRPC] Deducting {cost} from {acct.username}")
                acct.balance -= cost
                db.session.commit()
            elif ok and deduct=="false":
                print(f"[CustomerRPC] No stock {acct.username}")
                acct.balance += cost
                db.session.commit()
            elif not ok:
                print(f"[CustomerRPC] Insufficient funds for customer {cid}")
                db.session.rollback()
            else:
                print(f"[CustomerRPC] Balance check passed for customer {cid}")

            response = "true" if ok else "false"  # Java client expects YES/NO
        except Exception as e:
            print(f"[CustomerRPC] Error: {e}")
            db.session.rollback()
            response = "false"
    
    # Only try to reply if we have a valid reply_to queue
    if props.reply_to and isinstance(props.reply_to, (str, bytes)):
        try:
            # Send RPC reply
            ch.basic_publish(
                exchange="",
                routing_key=props.reply_to,
                properties=pika.BasicProperties(
                    correlation_id=props.correlation_id,
                    content_type="text/plain"
                ),
                body=response
            )
            print(f"[CustomerRPC] Replied: {response} to {props.reply_to}")
        except Exception as e:
            print(f"[CustomerRPC] Failed to send reply: {e}")
    else:
        print(f"[CustomerRPC] No valid reply_to queue, can't respond (reply_to={props.reply_to})")
    
    # Always acknowledge the request
    ch.basic_ack(delivery_tag=method.delivery_tag)

def start_customer_rpc_server(flask_app):
    """
    Declares exchange, queue, binding, and begins consuming.
    Runs forever in its own thread.
    """
    connection = pika.BlockingConnection(RABBITMQ_PARAMS)
    channel = connection.channel()
    

    # 1) Declare direct exchange + queue + bind
    channel.exchange_declare(exchange=EXCHANGE,
                             exchange_type="direct",
                             durable=True)
    channel.queue_declare(queue=QUEUE_NAME, durable=True)
    channel.queue_bind(queue=QUEUE_NAME,
                       exchange=EXCHANGE,
                       routing_key=ROUTING_KEY)

    # 2) Fair dispatch
    channel.basic_qos(prefetch_count=1)

    # 3) Create a callback wrapper that includes the Flask app
    def callback_wrapper(ch, method, props, body):
        return on_customer_request(ch, method, props, body, flask_app)

    # 4) Start consuming with the wrapped callback
    channel.basic_consume(
        queue=QUEUE_NAME,
        on_message_callback=callback_wrapper
    )
    print(f"[CustomerRPC] Listening on {QUEUE_NAME} ...")
    channel.start_consuming()
