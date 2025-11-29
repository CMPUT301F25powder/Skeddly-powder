class ParticipantList:
    def __init__(self, ticketIds: list[str], max: int) -> None:
        self.ticketIds = ticketIds
        self.max = max

    @staticmethod
    def from_dict(source):
        return ParticipantList(source['ticketIds'], source['max'])

    def to_dict(self):
        return {
            'ticketIds': self.ticketIds,
            'max': self.max
        }
