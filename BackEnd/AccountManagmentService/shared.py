import threading

# Shared array for logs
array_logs = []
array_logs_lock = threading.Lock()

def admin_notify_callback(ch, method, properties, body):
    msg = body.decode()
    with array_logs_lock:  # Ensure thread-safe access
        array_logs.append(msg)
    print("ADMIN ALERT:", msg)


log_alerts = []
def callback(ch, method, properties, body):
    msg = body.decode()
    with array_logs_lock:  # Ensure thread-safe access
        log_alerts.append(msg)
    print("ADMIN LOG ALERT:", msg)
    # socketio.emit('admin_log', {'message': msg})
    # Here you can add logic to send an email, SMS, etc.