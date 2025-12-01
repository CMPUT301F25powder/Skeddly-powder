from business.user.NotificationSettings import *
from business.user.PersonalInformation import *

class User:
    def __init__(self, notificationSettings: NotificationSettings, personalInformation: PersonalInformation, privilegeLevel: str):
        self.notificationSettings = notificationSettings
        self.personalInformation = personalInformation
        self.privilegeLevel = privilegeLevel
        self.mock = True

    @staticmethod
    def from_dict(source):
        return User(
            notificationSettings=NotificationSettings.from_dict(source['notificationSettings']),
            personalInformation=PersonalInformation.from_dict(source['personalInformation']),
            privilegeLevel=source['privilegeLevel'])

    def to_dict(self):
        return {'notificationSettings': self.notificationSettings.to_dict(),
                'personalInformation': self.personalInformation.to_dict(),
                'privilegeLevel': self.privilegeLevel,
                'mock': self.mock}
