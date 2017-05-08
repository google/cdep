// Copyright 2016 Google Inc. All rights reserved.

#ifndef FIREBASE_MESSAGING_CLIENT_CPP_INCLUDE_FIREBASE_MESSAGING_H_
#define FIREBASE_MESSAGING_CLIENT_CPP_INCLUDE_FIREBASE_MESSAGING_H_

#include <stdint.h>
#include <map>
#include <string>
#include <vector>
#include "firebase/app.h"
#include "firebase/internal/common.h"

#if !defined(DOXYGEN) && !defined(SWIG)
FIREBASE_APP_REGISTER_CALLBACKS_REFERENCE(messaging);
#endif  // !defined(DOXYGEN) && !defined(SWIG)

namespace firebase {

/// @brief Firebase Cloud Messaging API.
///
/// Firebase Cloud Messaging allows you to send data from your server to your
/// users' devices, and receive messages from devices on the same connection
/// if you're using a XMPP server.
///
/// The FCM service handles all aspects of queueing of messages and delivery
/// to client applications running on target devices.
namespace messaging {

/// Used for messages that display a notification.
///
/// On android, this requires that the app is using the Play Services client
/// library.
struct Notification {
  /// Indicates notification title. This field is not visible on iOS phones
  /// and tablets.
  std::string title;

  /// Indicates notification body text.
  std::string body;

  /// Indicates notification icon. Sets value to myicon for drawable resource
  /// myicon.
  std::string icon;

  /// Indicates a sound to play when the device receives the notification.
  /// Supports default, or the filename of a sound resource bundled in the
  /// app.
  ///
  /// Android sound files must reside in /res/raw/, while iOS sound files
  /// can be in the main bundle of the client app or in the Library/Sounds
  /// folder of the app’s data container.
  std::string sound;

  /// Indicates the badge on the client app home icon. iOS only.
  std::string badge;

  /// Indicates whether each notification results in a new entry in the
  /// notification drawer on Android. If not set, each request creates a new
  /// notification. If set, and a notification with the same tag is already
  /// being shown, the new notification replaces the existing one in the
  /// notification drawer.
  std::string tag;

  /// Indicates color of the icon, expressed in \#rrggbb format. Android only.
  std::string color;

  /// The action associated with a user click on the notification.
  ///
  /// On Android, if this is set, an activity with a matching intent filter is
  /// launched when user clicks the notification.
  ///
  /// If set on iOS, corresponds to category in APNS payload.
  std::string click_action;

  /// Indicates the key to the body string for localization.
  ///
  /// On iOS, this corresponds to "loc-key" in APNS payload.
  ///
  /// On Android, use the key in the app's string resources when populating this
  /// value.
  std::string body_loc_key;

  /// Indicates the string value to replace format specifiers in body string
  /// for localization.
  ///
  /// On iOS, this corresponds to "loc-args" in APNS payload.
  ///
  /// On Android, these are the format arguments for the string resource. For
  /// more information, see [Formatting strings][1].
  ///
  /// [1]:
  /// https://developer.android.com/guide/topics/resources/string-resource.html#FormattingAndStyling
  std::vector<std::string> body_loc_args;

  /// Indicates the key to the title string for localization.
  ///
  /// On iOS, this corresponds to "title-loc-key" in APNS payload.
  ///
  /// On Android, use the key in the app's string resources when populating this
  /// value.
  std::string title_loc_key;

  /// Indicates the string value to replace format specifiers in title string
  /// for localization.
  ///
  /// On iOS, this corresponds to "title-loc-args" in APNS payload.
  ///
  /// On Android, these are the format arguments for the string resource. For
  /// more information, see [Formatting strings][1].
  ///
  /// [1]:
  /// https://developer.android.com/guide/topics/resources/string-resource.html#FormattingAndStyling
  std::vector<std::string> title_loc_args;
};

/// @brief Data structure used to send messages to, and receive messages from,
/// cloud messaging.
struct Message {
  /// Initialize the message.
  Message() : time_to_live(0), notification(nullptr) {}

  /// Destructor.
  ~Message() { delete notification; }

  /// Copy constructor. Makes a deep copy of this Message.
  Message(const Message& other) : notification(nullptr) { *this = other; }

  /// Copy assignment operator. Makes a deep copy of this Message.
  Message& operator=(const Message& other) {
    this->to = other.to;
    this->collapse_key = other.collapse_key;
    this->data = other.data;
    this->raw_data = other.raw_data;
    this->message_id = other.message_id;
    this->message_type = other.message_type;
    this->priority = other.priority;
    this->time_to_live = other.time_to_live;
    this->error = other.error;
    this->error_description = other.error_description;
    delete this->notification;
    if (other.notification) {
      this->notification = new Notification(*other.notification);
    } else {
      this->notification = nullptr;
    }
    return *this;
  }

  /// Authenticated ID of the sender. This is a project number in most cases.
  ///
  /// This field is only used for downstream messages received through
  /// Listener::OnMessage().
  std::string from;

  /// This parameter specifies the recipient of a message.
  ///
  /// For example it can be a registration token, a topic name, a IID or project
  /// ID.
  ///
  /// This field is used for both upstream messages sent with
  /// firebase::messaging:Send() and downstream messages received through
  /// Listener::OnMessage(). For upstream messages,
  /// PROJECT_ID@gcm.googleapis.com or the more general IID format are accepted.
  std::string to;

  /// The collapse key used for collapsible messages.
  ///
  /// This field is only used for downstream messages received through
  /// Listener::OnMessage().
  std::string collapse_key;

  /// The metadata, including all original key/value pairs. Includes some of the
  /// HTTP headers used when sending the message. `gcm`, `google` and `goog`
  /// prefixes are reserved for internal use.
  ///
  /// This field is used for both upstream messages sent with
  /// firebase::messaging::Send() and downstream messages received through
  /// Listener::OnMessage().
  std::map<std::string, std::string> data;

  /// Binary payload. For webpush and non-json messages, this is the body of the
  /// request entity.
  ///
  /// This field is only used for downstream messages received through
  /// Listener::OnMessage().
  std::string raw_data;

  /// Message ID. This can be specified by sender. Internally a hash of the
  /// message ID and other elements will be used for storage. The ID must be
  /// unique for each topic subscription - using the same ID may result in
  /// overriding the original message or duplicate delivery.
  ///
  /// This field is used for both upstream messages sent with
  /// firebase::messaging::Send() and downstream messages received through
  /// Listener::OnMessage().
  std::string message_id;

  /// Equivalent with a content-type.
  /// CCS uses "ack", "nack" for flow control and error handling.
  /// "control" is used by CCS for connection control.
  ///
  /// This field is only used for downstream messages received through
  /// Listener::OnMessage().
  std::string message_type;

  /// Priority level. Defines values are "normal" and "high".
  /// By default messages are sent with normal priority.
  ///
  /// This field is only used for downstream messages received through
  /// Listener::OnMessage().
  std::string priority;

  /// Time to live, in seconds.
  ///
  /// This field is only used for downstream messages received through
  /// Listener::OnMessage().
  int32_t time_to_live;

  /// Error code. Used in "nack" messages for CCS, and in responses from the
  /// server.
  /// See the CCS specification for the externally-supported list.
  ///
  /// This field is only used for downstream messages received through
  /// Listener::OnMessage().
  std::string error;

  /// Human readable details about the error.
  ///
  /// This field is only used for downstream messages received through
  /// Listener::OnMessage().
  std::string error_description;

  /// Optional notification to show. This only set if a notification was
  /// received with this message, otherwise it is null.
  ///
  /// The notification is only guaranteed to be valid during the call to
  /// Listener::OnMessage(). If you need to keep it around longer you will need
  /// to make a copy of either the Message or Notification. Copying the Message
  /// object implicitly makes a deep copy of the notification (allocated with
  /// new) which is owned by the Message.
  ///
  /// This field is only used for downstream messages received through
  /// Listener::OnMessage().
  Notification* notification;
};

/// @brief Base class used to recieve messages from Firebase Cloud Messaging.
///
/// You need to override base class methods to handle any events required by the
/// application. Methods are invoked asynchronously and may be invoked on other
/// threads.
class Listener {
 public:
  virtual ~Listener();

  /// Called on the client when a message arrives.
  ///
  /// @param[in] message The data describing this message.
  virtual void OnMessage(const Message& message) = 0;

  /// Called on the client when a registration token arrives. This function
  /// will eventually be called in response to a call to
  /// firebase::messaging::Initialize(...).
  ///
  /// @param[in] token The registration token.
  virtual void OnTokenReceived(const char* token) = 0;
};

/// @brief Initialize Firebase Cloud Messaging.
///
/// After Initialize is called, the implementation may call functions on the
/// Listener provided at any time.
///
/// @param[in] app The Firebase App object for this application.
/// @param[in] listener A Listener object that listens for events from the
///            Firebase Cloud Messaging servers.
///
/// @return kInitResultSuccess if initialization succeeded, or
/// kInitResultFailedMissingDependency on Android if Google Play services is
/// not available on the current device.
InitResult Initialize(const App& app, Listener* listener);

/// @brief Terminate Firebase Cloud Messaging.
///
/// Frees resources associated with Firebase Cloud Messaging.
///
/// @note On Android, the services will not be shut down by this method.
void Terminate();

/// @brief Set the listener for events from the Firebase Cloud Messaging
/// servers.
///
/// A listener must be set for the application to receive messages from
/// the Firebase Cloud Messaging servers.  The implementation may call functions
/// on the Listener provided at any time.
///
/// @param[in] listener A Listener object that listens for events from the
///            Firebase Cloud Messaging servers.
///
/// @return Pointer to the previously set listener.
Listener* SetListener(Listener* listener);

/// Send an upstream ("device to cloud") message. You can only use the upstream
/// feature if your FCM implementation uses the XMPP-based Cloud Connection
/// Server. The current limits for max storage time and number of outstanding
/// messages per application are documented in the [FCM Developers Guide].
///
/// [FCM Developers Guide]: https://firebase.google.com/docs/cloud-messaging/
///
/// @param[in] message The message to send upstream.
void Send(const Message& message);

/// @brief Subscribe to receive all messages to the specified topic.
///
/// Subscribes an app instance to a topic, enabling it to receive messages
/// sent to that topic.
///
/// Call this function from the main thread. FCM is not thread safe.
///
/// @param topic The topic to subscribe to. Should be of the form
///              `"/topics/<topic-name>"`.
void Subscribe(const char* topic);

/// @brief Unsubscribe from a topic.
///
/// Unsubscribes an app instance from a topic, stopping it from receiving
/// any further messages sent to that topic.
///
/// Call this function from the main thread. FCM is not thread safe.
///
/// @param topic The topic to unsubscribe from.
void Unsubscribe(const char* topic);

}  // namespace messaging
}  // namespace firebase

#endif  // FIREBASE_MESSAGING_CLIENT_CPP_INCLUDE_FIREBASE_MESSAGING_H_
