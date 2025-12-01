class PersonalInformation:
    def __init__(self, email: str, name: str, phoneNumber: str):
        self.email = email
        self.name = name
        self.phoneNumber = phoneNumber

    @staticmethod
    def from_dict(source):
        return PersonalInformation(source["email"], source["name"], source["phoneNumber"])

    def to_dict(self):
        return {"email": self.email, "name": self.name, "phoneNumber": self.phoneNumber}
