class Notification:
    def __init__(self, title: str, message: str, recipient: str, ticketId: str, timestamp: int, type: str, status: str, read: bool):
        self.title = title
        self.message = message
        self.recipient = recipient
        self.ticketId = ticketId
        self.timestamp = timestamp
        self.type = type
        self.status = status
        self.read = read

    @staticmethod
    def from_dict(source):
        return Notification(
            title=source["title"],
            message=source["message"],
            recipient=source["recipient"],
            ticketId=source["ticketId"],
            timestamp=source["timestamp"],
            type=source["type"],
            status=source["status"],
            read=source["read"]
        )

    def to_dict(self):
        return {
            "title": self.title,
            "message": self.message,
            "recipient": self.recipient,
            "ticketId": self.ticketId,
            "timestamp": self.timestamp,
            "type": self.type,
            "status": self.status,
            "read": self.read
        }
