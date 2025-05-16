# test_log_publish.py
import pika
LOG_EXCHANGE = "log_exchange"
connection = pika.BlockingConnection(pika.ConnectionParameters("localhost"))
channel = connection.channel()
channel.exchange_declare(exchange=LOG_EXCHANGE, exchange_type="topic", durable=True)
channel.basic_publish(exchange=LOG_EXCHANGE, routing_key="Error", body="Test error log")
connection.close()