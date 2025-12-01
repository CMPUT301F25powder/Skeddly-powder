class CustomLocation:
    def __init__(self, latitude: float, longitude: float) -> None:
        self.latitude = latitude
        self.longitude = longitude

    @staticmethod
    def from_dict(source):
        return CustomLocation(latitude=source['latitude'], longitude=source['longitude'])

    def to_dict(self):
        return {
            'latitude': self.latitude,
            'longitude': self.longitude
        }
