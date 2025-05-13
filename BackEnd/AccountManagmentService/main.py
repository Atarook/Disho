from flask import Flask
import os 
from flask_cors import CORS # Importing CORS for Cross-Origin Resource Sharing
import sqlite3
from database import db
from dotenv import load_dotenv
load_dotenv()



app=Flask(__name__)
CORS(app)
app.config["SQLALCHEMY_DATABASE_URI"] = os.environ.get("DATABASE_URL")
app.config["SQLALCHEMY_TRACK_MODIFICATIONS"]=False
app.config["SECRET_KEY"]=os.environ.get("SECRET_KEY")
db.init_app(app)
from routes import routes
app.register_blueprint(routes)
from Models import Account
with app.app_context():
    db.create_all()
    admin_exists = Account.query.filter_by(username='Admin').first()
    if not admin_exists:
        admin = Account(username='Admin', password='Admin123', role='Admin', balance=9999999999)
        db.session.add(admin)
        db.session.commit()
if __name__=="__main__":
    app.run(debug=True)