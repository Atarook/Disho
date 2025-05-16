from database import db

class Account(db.Model):
    id=db.Column(db.Integer,primary_key=True,autoincrement=True)
    username=db.Column(db.String(50),unique=True,nullable=False)
    password=db.Column(db.String(100),nullable=False)
    balance=db.Column(db.Float,nullable=True,default=0)
    location=db.Column(db.String(100),nullable=False,default="None")
    shipping_fees = db.Column(db.Float, default=   0.0)
    role=db.Column(db.Enum('Admin','Customer','Company','Shipping',name='role_enum'),nullable=False)
    def to_json(self):
        return{
            "id":self.id,
            "username":self.username,
            "password":self.password,
            "balance":self.balance,
            "role":self.role,
            "location":self.location,
            "shipping_fees":self.shipping_fees
        }
    

