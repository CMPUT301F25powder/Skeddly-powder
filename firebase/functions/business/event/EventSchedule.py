class EventSchedule:
    def __init__(self, startTime: int, endTime: int, regStart: int, regEnd: int):
        self.startTime = startTime
        self.endTime = endTime
        self.regStart = regStart
        self.regEnd = regEnd

    @staticmethod
    def from_dict(source):
        return EventSchedule(
            startTime=source['startTime'],
            endTime=source['endTime'],
            regStart=source['regStart'],
            regEnd=source['regEnd']
        )

    def to_dict(self):
        return {
            'startTime': self.startTime,
            'endTime': self.endTime,
            'regStart': self.regStart,
            'regEnd': self.regEnd
        }
