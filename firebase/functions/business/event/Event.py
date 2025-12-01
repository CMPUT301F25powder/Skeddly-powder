from business.event.EventDetail import *
from business.event.EventSchedule import *
from business.ParticipantList import ParticipantList
from business.WaitingList import WaitingList
from business.location.CustomLocation import CustomLocation


class Event:
    def __init__(self, eventDetails: EventDetail, eventSchedule: EventSchedule, location: CustomLocation,
                 organizer: str, waitingList: WaitingList, participantList: ParticipantList, logLocation: bool,
                 imageb64: str) -> None:
        self.eventDetails = eventDetails
        self.eventSchedule = eventSchedule
        self.location = location
        self.organizer = organizer
        self.waitingList = waitingList
        self.participantList = participantList
        self.logLocation = logLocation
        self.imageb64 = imageb64
        self.mock = True

    @staticmethod
    def from_dict(source):
        return Event(
            eventDetails=EventDetail.from_dict(source['detail']),
            eventSchedule=EventSchedule.from_dict(source['schedule']),
            location=CustomLocation.from_dict(source['location']),
            organizer=source['organizer'],
            waitingList=WaitingList.from_dict(source['waitingList']),
            participantList=ParticipantList.from_dict(source['participantList']),
            logLocation=source['logLocation'],
            imageb64=source['imageb64']
        )

    def to_dict(self):
        return {
            'eventDetails': self.eventDetails.to_dict(),
            'eventSchedule': self.eventSchedule.to_dict(),
            'location': self.location.to_dict(),
            'organizer': self.organizer,
            'waitingList': self.waitingList.to_dict(),
            'participantList': self.participantList.to_dict(),
            'logLocation': self.logLocation,
            'imageb64': self.imageb64,
            'mock': self.mock
        }
