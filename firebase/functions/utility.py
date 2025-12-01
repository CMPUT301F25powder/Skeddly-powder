from firebase_functions import https_fn, firestore_fn
from firebase_functions.options import set_global_options
from firebase_admin import firestore, initialize_app

from firebase_functions.firestore_fn import (
    on_document_created,
    on_document_deleted,
    on_document_updated,
    on_document_written,
    Event,
    Change,
    DocumentSnapshot,
    DocumentReference,
)

from google.cloud.firestore_v1 import FieldFilter, Or, CollectionReference
from google.cloud.firestore_v1.field_path import FieldPath

import google.cloud.firestore


def update_all_events(firestore_client: google.cloud.firestore.Client) -> None:
    """
    Updates all events cached array of ticket ids by querying the ticket collection.
    :param firestore_client: The firestore client to use
    """
    docs = (
        firestore_client.collection("events")
        .stream()
    )

    for doc in docs:
        waiting_ids, final_ids = get_ticket_ids_for_event(doc.id, firestore_client)
        data = {"waitingList.ticketIds": waiting_ids, "participantList.ticketIds": final_ids}
        firestore_client.collection("events").document(doc.id).update(data)


def get_ticket_ids_for_event(eventId: str, firestore_client: google.cloud.firestore.Client) -> tuple[list[str], list[str]]:
    """
    Retrieves all the ticket ids that correspond to a given event.
    :param eventId: The event id to fetch the tickets for
    :param firestore_client: The firestore client to use
    :return: A tuple of lists is returned with the first corresponding to the waiting list
    and the second corresponding to the final list.
    """
    tickets_waiting = (
        firestore_client.collection("tickets")
        .where(filter=FieldFilter("eventId", "==", eventId))
        .where(filter=FieldFilter("status", "==", "WAITING"))
        .stream()
    )

    tickets_final = (
        firestore_client.collection("tickets")
        .where(filter=FieldFilter("eventId", "==", eventId))
        .where(filter=FieldFilter("status", "!=", "WAITING"))
        .stream()
    )

    return [ticket.id for ticket in tickets_waiting], [ticket.id for ticket in tickets_final]


def get_all_ids(collection: CollectionReference) -> list[str]:
    """
    Gets all the ids that are inside a given collection.
    :param collection: The reference to the collection to get the tickets for
    :return: A list of ids inside the collection
    """
    docs = (
        collection
        .stream()
    )

    return [doc.id for doc in docs]


def check_user_orphaned(user_id: str, firestore_client: google.cloud.firestore.Client) -> bool:
    """
    Checks whether a given user is considered orphan. A user is orphaned when all values in their personal
    information are blank.
    :param user_id: The user id to check if they are orphaned.
    :param firestore_client: The firestore client to use
    :return: True if they are orphaned. False otherwise.
    """
    document_reference: DocumentReference = firestore_client.collection("users").document(user_id)
    document_snapshot: DocumentSnapshot = document_reference.get(["personalInformation"])

    if document_snapshot.exists:
        try:
            personal_info: dict[str, str] = document_snapshot.get("personalInformation")
        except KeyError:
            return True

        unique_values: set[str] = set(personal_info.values())

        if len(unique_values) <= 0 or len(unique_values) == 1 and unique_values.pop() == "":
            return True
    else:
        return True

    return False


def check_event_orphaned(event_id: str, valid_uids: list[str], firestore_client: google.cloud.firestore.Client) -> bool:
    """
    Checks whether a given event is considered orphan. An event is orphaned when their organizer does not exist.
    :param event_id: The event id to check if they are orphaned.
    :param valid_uids: A list of valid user ids.
    :param firestore_client: The firestore client to use
    :return: True if it is orphaned. False otherwise.
    """
    document_reference: DocumentReference = firestore_client.collection("events").document(event_id)
    document_snapshot: DocumentSnapshot = document_reference.get(["organizer"])

    if document_snapshot.exists:
        try:
            organizer_id: str = document_snapshot.get("organizer")
        except KeyError:
            return True

        if organizer_id not in valid_uids:
            return True
    else:
        return True

    return False


def check_ticket_orphaned(ticket_id: str, valid_uids: list[str], valid_event_ids: list[str], firestore_client: google.cloud.firestore.Client) -> bool:
    """
    Checks whether a given ticket is considered orphaned. A ticket is orphaned if their event or user does not exist.
    :param ticket_id: The ticket id to check if it is orphaned
    :param valid_uids: A list of valid user ids
    :param valid_event_ids: A list of valid event ids
    :param firestore_client: The firestore client to use
    :return: True if it is orphaned. False otherwise.
    """
    document_reference: DocumentReference = firestore_client.collection("tickets").document(ticket_id)
    document_snapshot: DocumentSnapshot = document_reference.get(["userId", "eventId"])

    if document_snapshot.exists:
        try:
            user_id: str = document_snapshot.get("userId")
            event_id: str = document_snapshot.get("eventId")
        except KeyError:
            return True

        if user_id not in valid_uids or event_id not in valid_event_ids:
            return True
    else:
        return True

    return False


def check_notification_orphaned(notification_id: str, valid_uids: list[str], valid_tids: list[str], firestore_client: google.cloud.firestore.Client) -> bool:
    """
    Checks whether a given notification is considered orphaned. A notification is orphaned when their ticket or user
    does not exist.
    :param notification_id: The notification id to check if it is orphaned
    :param valid_uids: A list of valid user ids
    :param valid_tids: A list of valid tickets ids
    :param firestore_client: The firestore client to use
    :return: True if it is orphaned. False otherwise.
    """
    document_reference: DocumentReference = firestore_client.collection("notifications").document(notification_id)
    document_snapshot: DocumentSnapshot = document_reference.get(["recipient", "ticketId"])

    if document_snapshot.exists:
        try:
            recipient_id: str = document_snapshot.get("recipient")
            ticket_id: str = document_snapshot.get("ticketId")
        except KeyError:
            return True

        if recipient_id not in valid_uids or ticket_id not in valid_tids:
            return True
    else:
        return True

    return False
