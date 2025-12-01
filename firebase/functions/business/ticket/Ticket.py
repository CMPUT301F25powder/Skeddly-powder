from business.location.CustomLocation import CustomLocation
from business.user.PersonalInformation import PersonalInformation


class Ticket:
    def __init__(self, eventId, location: CustomLocation, status: str, ticketTime: int, userId: str, userPersonalInfo: PersonalInformation):
        self.eventId = eventId
        self.location = location
        self.status = status
        self.ticketTime = ticketTime
        self.userId = userId
        self.userPersonalInfo = userPersonalInfo

    @staticmethod
    def from_dict(source):
        return Ticket(
            source['eventId'],
            CustomLocation.from_dict(source['location']),
            source['status'],
            source['ticketTime'],
            source['userId'],
            PersonalInformation.from_dict(source['userPersonalInfo']),
        )

    def to_dict(self):
        return {
            'eventId': self.eventId,
            'location': self.location.to_dict(),
            'status': self.status,
            'ticketTime': self.ticketTime,
            'userId': self.userId,
            'userPersonalInfo': self.userPersonalInfo.to_dict()
        }
