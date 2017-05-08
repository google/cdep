// Copyright 2016 Google Inc. All Rights Reserved.

#ifndef FIREBASE_STORAGE_CLIENT_CPP_SRC_INCLUDE_FIREBASE_STORAGE_LISTENER_H_
#define FIREBASE_STORAGE_CLIENT_CPP_SRC_INCLUDE_FIREBASE_STORAGE_LISTENER_H_

#include "firebase/storage/controller.h"

namespace firebase {
namespace storage {

/// @brief Base class used to receive pause and progress events on a running
/// read or write operation.
///
/// Subclasses of this listener class can be used to receive events about data
/// transfer progress a location. Attach the listener to a location using
/// StorageReference::GetBytes(), StorageReference::GetFile(),
/// StorageReference::PutBytes(), and StorageReference::PutFile(); then
/// OnPaused() will be called whenever the Read or Write operation is paused,
/// and OnProgress() will be called periodically as the transfer makes progress.
class Listener {
 public:
  /// @brief Virtual destructor.
  virtual ~Listener();

  /// @brief The operation was paused.
  ///
  /// @param[in] controller A controller that can be used to check the status
  /// and make changes to the ongoing operation.
  virtual void OnPaused(Controller* controller) = 0;

  /// @brief There has been progress event.
  ///
  /// @param[in] controller A controller that can be used to check the status
  /// and make changes to the ongoing operation.
  virtual void OnProgress(Controller* controller) = 0;
};

}  // namespace storage
}  // namespace firebase

#endif  // FIREBASE_STORAGE_CLIENT_CPP_SRC_INCLUDE_FIREBASE_STORAGE_LISTENER_H_
