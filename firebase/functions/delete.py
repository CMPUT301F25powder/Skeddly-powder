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

import google.cloud.firestore
from google.cloud.firestore_v1 import FieldFilter, Or, CollectionReference
from google.cloud.firestore_v1.field_path import FieldPath

import utility


def delete_collection(collection: CollectionReference, collection_filter: FieldFilter) -> None:
    """
    Deletes from a given collection by the corresponding filter
    :param collection: Reference to the collection to delete
    :param collection_filter: The filter to apply to the collection
    """
    docs = (
        collection
        .where(filter=collection_filter)
        .stream()
    )

    for doc in docs:
        collection.document(doc.id).delete()

def delete_user(user_id: str, firestore_client: google.cloud.firestore.Client) -> None:
    """
    Deletes a user by the given user id
    :param user_id: The user id to delete
    :param firestore_client: The firestore client to use.
    """
    # Delete all tickets that reference them
    delete_tickets_with_user_id(user_id, firestore_client)

    # Delete all their notifications
    delete_notifications_with_user_id(user_id, firestore_client)

    # Delete all their events
    delete_events_with_user_id(user_id, firestore_client)

    # Update all events with new waiting and participant lists
    utility.update_all_events(firestore_client)


def delete_orphan_users(firestore_client: google.cloud.firestore.Client) -> None:
    """
    Cleanup any orphaned users in the database.
    :param firestore_client: The firestore client to use.
    """
    docs = (
        firestore_client.collection("users")
        .stream()
    )

    for doc in docs:
        data = doc.get("personalInformation")

        empty: bool = True
        for val in data.values():
            if len(val) != 0:
                empty = False
                break

        if empty:
            firestore_client.collection("users").document(doc.id).delete()


def delete_tickets_with_user_id(user_id: str, firestore_client: google.cloud.firestore.Client) -> None:
    """
    Delete all tickets that correspond to a user id.
    :param user_id: The user id to delete the tickets of.
    :param firestore_client: The firestore client to use.
    """
    delete_collection(firestore_client.collection("tickets"), FieldFilter("userId", "==", user_id))


def delete_tickets_with_event_id(event_id: str, firestore_client: google.cloud.firestore.Client) -> None:
    """
    Delete all tickets that correspond to a specific event id.
    :param event_id: The event id to delete the tickets of.
    :param firestore_client: The firestore client to use.
    """
    delete_collection(firestore_client.collection("tickets"), FieldFilter("eventId", "==", event_id))


def delete_events_with_user_id(user_id: str, firestore_client: google.cloud.firestore.Client) -> None:
    """
    Delete all events that correspond to a specific user id.
    :param user_id: The user id to delete the events of.
    :param firestore_client: The firestore client to use.
    """
    delete_collection(firestore_client.collection("events"), FieldFilter("organizer", "==", user_id))


def delete_notifications_with_user_id(user_id: str, firestore_client: google.cloud.firestore.Client) -> None:
    """
    Delete all notifications that correspond to a specific user id.
    :param user_id: The user id to delete the notifications of.
    :param firestore_client: The firestore client to use.
    """
    delete_collection(firestore_client.collection("notifications"), FieldFilter("recipient", "==", user_id))


def delete_notifications_with_ticket_id(ticket_id: str, firestore_client: google.cloud.firestore.Client) -> None:
    """
    Delete all notifications that correspond to a specific ticket id.
    :param ticket_id: The ticket id to delete the notifications of.
    :param firestore_client: The firestore client to use.
    """
    delete_collection(firestore_client.collection("notifications"), FieldFilter("ticketId", "==", ticket_id))
