import pika
LOG_EXCHANGE = "log_exchange"

def admin_error_log_consumer(app):
    connection = pika.BlockingConnection(pika.ConnectionParameters("localhost"))
    channel = connection.channel()
    channel.exchange_declare(exchange=LOG_EXCHANGE, exchange_type="topic", durable=True)
    channel.queue_declare(queue="admin_error_logs", durable=True)
    # Bind to all error logs from any service
    channel.queue_bind(exchange=LOG_EXCHANGE, queue="admin_error_logs", routing_key="*_Error")

    def callback(ch, method, properties, body):
        print("ADMIN LOG ALERT:", body.decode())
        # Here you can add logic to send an email, SMS, etc.

    channel.basic_consume(queue="admin_error_logs", on_message_callback=callback, auto_ack=True)
    print("Waiting for error logs...")
    channel.start_consuming()

