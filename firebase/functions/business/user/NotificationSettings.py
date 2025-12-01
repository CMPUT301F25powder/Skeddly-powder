class NotificationSettings:
    def __init__(self, administrative: bool, eventUpdate: bool, lotteryStatus: bool) -> None:
        self.administrative = administrative
        self.eventUpdate = eventUpdate
        self.lotteryStatus = lotteryStatus

    @staticmethod
    def from_dict(source):
        return NotificationSettings(source['administrative'], source['eventUpdate'], source['lotteryStatus'])

    def to_dict(self):
        return {'administrative': self.administrative,
                'eventUpdate': self.eventUpdate,
                'lotteryStatus': self.lotteryStatus}
