import os
from flask_socketio import SocketIO
import threading

from flask import Flask
from flask_cors import CORS
from dotenv import load_dotenv
import logs
from database import db
from Models import Account
import routes  # our Blueprint + RPC server code


load_dotenv()
DATABASE_URL = os.environ.get("DATABASE_URL")
SECRET_KEY   = os.environ.get("SECRET_KEY")

app = Flask(__name__)
# socketio = SocketIO(app, cors_allowed_origins="*")
CORS(app,
     supports_credentials=True,
     resources={r"/*": {"origins": "http://localhost:8000"}})

app.config["SQLALCHEMY_DATABASE_URI"]        = DATABASE_URL
app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] = False
app.config["SECRET_KEY"]                     = SECRET_KEY

# Initialize SQLAlchemy
db.init_app(app)

# Register your routes (HTTP + RPC)
app.register_blueprint(routes.routes)

import Admin
# Create tables & default admin
with app.app_context():
    db.create_all()
    if not Account.query.filter_by(username="Admin").first():
        admin = Account(
            username="Admin",
            password="Admin123",
            role="Admin",
            balance=999999999
        )
        db.session.add(admin)
        db.session.commit()
    

if __name__ == "__main__":
   

    # Pass the app to the RPC server
    rpc_thread = threading.Thread(target=routes.start_customer_rpc_server, args=(app,), daemon=True)
    rpc_thread.start()
    thread2=threading.Thread(target=Admin.wait, args=(app,), daemon=True)
    thread3=threading.Thread(target=logs.admin_error_log_consumer, args=(app,), daemon=True)
  
    thread2.start()
    thread3.start()
    # Start Flask web server
    app.run(debug=True)
    # socketio.run(app, debug=True)

