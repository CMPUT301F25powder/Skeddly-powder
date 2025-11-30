# Welcome to Cloud Functions for Firebase for Python!
# To get started, simply uncomment the below code or create your own.
# Deploy with `firebase deploy`
import random
import time
from datetime import timedelta
from typing import Any

from firebase_functions import https_fn, firestore_fn, scheduler_fn
from firebase_functions.options import set_global_options
from firebase_admin import firestore, initialize_app, auth, messaging

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

import business
import delete
import utility
import business.event
from business.ParticipantList import ParticipantList
from business.WaitingList import WaitingList
from business.event.EventDetail import EventDetail
from business.event.EventSchedule import EventSchedule
from business.location.CustomLocation import CustomLocation

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


@on_document_created(document="notifications/{notifId}")
def send_fcm_notification(event: Event[DocumentSnapshot]) -> None:
    notif_id: str = event.params["notifId"]
    data = event.data.to_dict()

    uid_recipient: str = data["recipient"]
    firestore_client: google.cloud.firestore.Client = firestore.client()

    try:
        fcm_token = firestore_client.collection("users").document(uid_recipient).get(["fcmToken"]).get("fcmToken")
    except:
        # No FCM Token so can't send a notification
        print(f"[ERROR] Could not get fcmToken for {uid_recipient}")
        return

    message = messaging.Message(
        notification=messaging.Notification(title=data["title"], body=data["message"]),
        token=fcm_token,
    )

    response = messaging.send(message)
    print(f"[INFO] Sent a message to {uid_recipient}: {response}")


@on_document_deleted(document="users/{userId}")
def cleanup_deleted_user(event: Event[DocumentSnapshot|None]) -> None:
    user_id: str = event.params["userId"]

    firestore_client: google.cloud.firestore.Client = firestore.client()
    delete.delete_user(user_id, firestore_client)
    delete.delete_orphan_users(firestore_client)


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


@on_document_updated(document="users/{userId}")
def update_personal_info(event: Event[Change[DocumentSnapshot]]) -> None:
   user_id: str = event.params["userId"]
   old_data = event.data.before.to_dict()
   new_data = event.data.after.to_dict()

   if old_data["personalInformation"] == new_data["personalInformation"]:
       # Nothing to do
       return

   # Write new personal information to all tickets
   firestore_client: google.cloud.firestore.Client = firestore.client()

   docs = (
       firestore_client.collection("tickets")
       .where(filter=FieldFilter("userId", "==", user_id))
       .stream()
   )

   for doc in docs:
       data = {"userPersonalInfo": new_data["personalInformation"]}
       firestore_client.collection("tickets").document(doc.id).update(data)


@https_fn.on_call()
def http_cleanup_db(req: https_fn.CallableRequest) -> Any:
    try:
        cleanup_orphans()
    except Exception as e:
        return {"successful": False, "message": str(e)}

    return {"successful": True, "message": ""}


# @https_fn.on_call()
# def http_add_mock_events(req: https_fn.CallableRequest) -> Any:
#     firestore_client: google.cloud.firestore.Client = firestore.client()
#     delete_mock_events(firestore_client)
#     try:
#         organizer: str = req.data["organizer"]
#         add_mock_events(organizer, firestore_client)
#     except Exception as e:
#         return {"successful": False, "message": str(e)}
#
#     return {"successful": True, "message": ""}


def cleanup_orphans():
    firestore_client: google.cloud.firestore.Client = firestore.client()

    all_user_ids: list[str] = utility.get_all_ids(firestore_client.collection("users"))
    all_event_ids: list[str] = utility.get_all_ids(firestore_client.collection("events"))
    all_ticket_ids: list[str] = utility.get_all_ids(firestore_client.collection("tickets"))
    all_notification_ids: list[str] = utility.get_all_ids(firestore_client.collection("notifications"))

    for user_id in list(all_user_ids):
        if utility.check_user_orphaned(user_id, firestore_client):
            print(f"Deleting {user_id =}")
            firestore_client.collection("users").document(user_id).delete()
            all_user_ids.remove(user_id)

    for event_id in list(all_event_ids):
        if utility.check_event_orphaned(event_id, all_user_ids, firestore_client):
            print(f"Deleting {event_id =}")
            firestore_client.collection("events").document(event_id).delete()
            all_event_ids.remove(event_id)

    for ticket_id in list(all_ticket_ids):
        if utility.check_ticket_orphaned(ticket_id, all_user_ids, all_event_ids, firestore_client):
            print(f"Deleting {ticket_id =}")
            firestore_client.collection("tickets").document(ticket_id).delete()
            all_ticket_ids.remove(ticket_id)

    for notification_id in list(all_notification_ids):
        if utility.check_notification_orphaned(notification_id, all_user_ids, all_ticket_ids, firestore_client):
            print(f"Deleting {notification_id =}")
            firestore_client.collection("notifications").document(notification_id).delete()
            all_notification_ids.remove(notification_id)


def delete_mock_events(firestore_client: google.cloud.firestore.Client) -> None:
    docs = (
        firestore_client.collection("events")
        .where("mock", "==", True)
        .stream()
    )

    for doc in docs:
        firestore_client.collection("events").document(doc.id).delete()


def add_mock_events(organizer: str, firestore_client: google.cloud.firestore.Client):
    categories = ["Indoor", "Outdoor", "In-person", "Virtual", "Hybrid",
     "Arts & Crafts", "Physical activity"]
    one_year: int = 31536000
    one_hour: int = 3600
    for i in range(10):
        eventDetail: EventDetail = EventDetail(f"mockEvent{i}", f"mockDescription{i}", f"mockEntryCriteria{i}", [random.choice(categories)])

        curTime: int = round(time.time())
        eventSchedule: EventSchedule = EventSchedule(curTime + one_year, curTime + one_year + one_hour, curTime, curTime + one_year - one_hour)

        location: CustomLocation = CustomLocation(53.5249325023001, -113.52196070411124)

        waitingList = WaitingList([], 100)
        participantList = ParticipantList([], 50)

        event: business.event.Event = business.event.Event(eventDetail, eventSchedule, location, organizer, waitingList, participantList, False, "")

        firestore_client.collection("events").add(event.to_dict())
