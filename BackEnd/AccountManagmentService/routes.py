from flask import Blueprint, request,jsonify, session
from Models import Account
from database import db
import secrets

routes = Blueprint('routes', __name__)



@routes.route('/register',methods=["POST"])
def register():
    try:
        data=request.json
        username=data.get("username")
        password=data.get("password")
        balance=data.get("balance")
        required_fields=["username","password","balance"]
        for field in required_fields:
            if field not in data:
                return jsonify({"error":f"The following is missing: {field}"}),400
        existing_user=Account.query.filter_by(username=username).first()
        if existing_user:
            return jsonify({"error":"User_Name already exists"}),400
        Customer=Account(username=username,password=password,balance=balance,role="Customer")
        db.session.add(Customer)
        db.session.commit()
        session["username"]=username
        session["user_role"]="Customer"
        return jsonify({"message":f"Account registered successfully with the name : {username}"}),201      
    except Exception as E:
        return jsonify({"error": str(E)}), 500     
@routes.route('/login',methods=["POST"])
def login():
    try :
        data=request.json
        username=data.get("username")
        password=data.get("password")        
        required_fields=["username","password"]
        for field in required_fields:
            if field not in data:
                return jsonify({"error":f"The following is missing: {field}"},400)  
        existing_user=Account.query.filter_by(username=username).first()
        if existing_user:
            if existing_user.password==password:
                session["username"]=username
                session["user_role"]=existing_user.role
                return jsonify({"message":"Login successful"}),200
            else:
                return jsonify({"error":"Invalid password"}),401
        else: 
            return jsonify({"error":"User not found"}),404
                
    except Exception as E:
        print(E)
        return jsonify({"error":"An error occurred"}),500


@routes.route('/create_company',methods=["POST"])
def create_company():
    try:
        if session["user_role"]=="Admin":
             data=request.json
             username=data.get("username")
             required_fields=["username"]
             if  "username" not in data:
                 return jsonify({"error":f"The following is missing: username"}),400
             existing_Company=Account.query.filter_by(username=username).first()
             if existing_Company:
                return jsonify({"error":"User_Name already exists"}),400
             auto_password=secrets.token_urlsafe(8)
             company=Account(username=username,password=auto_password,balance=0,role="Company")
             db.session.add(company)
             db.session.commit()
             return jsonify({"message":f"Account registered successfully with the name : {username}"}),201      
        else:
             return jsonify({"error":"Permission Denied"}),403      
    except Exception as E:
        return jsonify({"error": str(E)}), 500 


@routes.route('/list_all_cust_accounts',methods=["GET"])
def List_all_accounts():
    try:
        if session["user_role"]=="Admin":
            accounts=Account.query.filter_by(role="Customer").all()
            account_list=[account.to_json() for account in accounts]
            return jsonify(account_list),200
        else:
            return jsonify({"error":"You are not authorized to view this page"}),403
    except Exception as E:
        return jsonify({"error": str(E)}), 500
@routes.route('/list_all_companies',methods=["GET"])
def List_all_accounts_comapnies():
    try:
        if session["user_role"]=="Admin":
            accounts=Account.query.filter_by(role="Company").all()
            account_list=[account.to_json() for account in accounts]
            return jsonify(account_list),200
        else:
            return jsonify({"error":"You are not authorized to view this page"}),403
    except Exception as E:
        return jsonify({"error": str(E)}), 500
    
    
    
