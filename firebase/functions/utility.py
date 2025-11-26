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

from google.cloud.firestore_v1 import FieldFilter, Or
from google.cloud.firestore_v1.field_path import FieldPath

import google.cloud.firestore


def update_all_events(firestore_client: google.cloud.firestore.Client) -> None:
    docs = (
        firestore_client.collection("events")
        .stream()
    )

    for doc in docs:
        waiting_ids, final_ids = get_ticket_ids_for_event(doc.id, firestore_client)
        data = {"waitingList.ticketIds": waiting_ids, "participantList.ticketIds": final_ids}
        firestore_client.collection("events").document(doc.id).update(data)


def get_ticket_ids_for_event(eventId: str, firestore_client: google.cloud.firestore.Client) -> tuple[list[str], list[str]]:
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
