class EventDetail:
    def __init__(self, name: str, description: str, entryCriteria: str, categories: list[str]):
        self.name = name
        self.description = description
        self.entryCriteria = entryCriteria
        self.categories = categories

    @staticmethod
    def from_dict(source):
        return EventDetail(source['name'], source['description'], source['entryCriteria'], source['categories'])

    def to_dict(self):
        return {'name': self.name,
                'description': self.description,
                'entryCriteria': self.entryCriteria,
                'categories': self.categories}
