# Welcome to Cloud Functions for Firebase for Python!
# To get started, simply uncomment the below code or create your own.
# Deploy with `firebase deploy`

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
import delete
import utility
import inactive_users

# For cost control, you can set the maximum number of containers that can be
# running at the same time. This helps mitigate the impact of unexpected
# traffic spikes by instead downgrading performance. This limit is a per-function
# limit. You can override the limit for each function using the max_instances
# parameter in the decorator, e.g. @https_fn.on_request(max_instances=5).
set_global_options(max_instances=10)

app = initialize_app()
#
#
# @https_fn.on_request()
# def on_request_example(req: https_fn.Request) -> https_fn.Response:
#     return https_fn.Response("Hello world!")


@on_document_deleted(document="users/{userId}")
def cleanup_deleted_user(event: Event[DocumentSnapshot|None]) -> None:
    user_id: str = event.params["userId"]

    delete.delete_user(user_id)

    inactive_users.accountcleanup(None)


@on_document_deleted(document="events/{eventId}")
def cleanup_deleted_event(event: Event[DocumentSnapshot|None]) -> None:
    event_id: str = event.params["eventId"]

    # Delete all tickets that reference them
    firestore_client: google.cloud.firestore.Client = firestore.client()
    delete.delete_tickets_with_event_id(event_id, firestore_client)


@on_document_deleted(document="tickets/{ticketId}")
def cleanup_deleted_ticket(event: Event[DocumentSnapshot|None]) -> None:
    ticket_id: str = event.params["ticketId"]

    # Delete all notifications that reference them
    firestore_client: google.cloud.firestore.Client = firestore.client()
    delete.delete_notifications_with_ticket_id(ticket_id, firestore_client)


# @on_document_updated(document="users/{userId}")
# def myfunction(event: Event[Change[DocumentSnapshot]]) -> None:
#    user_id: str = event.params["userId"]
#    old_data = event.data.before.to_dict()
#    new_data = event.data.after.to_dict()
#
#    if old_data["personalInformation"] == new_data["personalInformation"]:
#        # Nothing to do
#        return
#
#    # Write new personal information to all tickets
#    firestore_client: google.cloud.firestore.Client = firestore.client()
#
#    docs = (
#        firestore_client.collection("tickets")
#        .where(filter=FieldFilter("userId", "==", user_id))
#        .stream()
#    )
#
#    for doc in docs:
#        data = {"personalInformation": new_data["personalInformation"]}
#        firestore_client.collection("tickets").document(doc.id).update(data)
