import pika
array_logs=[]
def admin_notify_callback(ch, method, properties, body):
    msg = body.decode()
    array_logs.append(msg)    
    print("ADMIN ALERT:", msg)
    
# if(array_logs!=[]):
#     print(f"Missing field: {array_logs[0]}")
def wait(app):
    connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
    channel = connection.channel()
    channel.exchange_declare(exchange="payments_exchange", exchange_type="direct", durable=True)
    channel.queue_declare(queue="admin_payment_failed", durable=True)
    channel.queue_bind(exchange="payments_exchange", queue="admin_payment_failed", routing_key="PaymentFailed")
    channel.basic_consume(queue="admin_payment_failed", on_message_callback=admin_notify_callback, auto_ack=True)
    print("Waiting for PaymentFailed events...")
    channel.start_consuming()
