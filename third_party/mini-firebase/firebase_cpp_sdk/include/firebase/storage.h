// Copyright 2016 Google Inc. All Rights Reserved.

#ifndef FIREBASE_STORAGE_CLIENT_CPP_SRC_INCLUDE_FIREBASE_STORAGE_H_
#define FIREBASE_STORAGE_CLIENT_CPP_SRC_INCLUDE_FIREBASE_STORAGE_H_

#include <string>

#include "firebase/app.h"
#include "firebase/storage/common.h"
#include "firebase/storage/controller.h"
#include "firebase/storage/listener.h"
#include "firebase/storage/metadata.h"
#include "firebase/storage/storage_reference.h"

namespace firebase {

/// Namespace for the Firebase Storage C++ SDK.
namespace storage {

namespace internal {
class StorageInternal;
}  // namespace internal

class StorageReference;

/// @brief Entry point for the Firebase Storage C++ SDK.
///
/// To use the SDK, call firebase::storage::Storage::GetInstance() to
/// obtain an instance of Storage, then use GetReference() to obtain references
/// to child blobs. From there you can upload data with
/// StorageReference::PutStream(), get data via StorageReference::GetStream().
class Storage {
 public:
  /// @brief Destructor. You may delete an instance of Storage when
  /// you are finished using it, to shut down the Storage library.
  ~Storage();

  /// @brief Get an instance of Storage corresponding to the given App.
  ///
  /// Firebase Storage uses firebase::App to communicate with Firebase
  /// Authentication to authenticate users to the server backend.
  ///
  /// @param[in] app An instance of firebase::App. Firebase Storage will use
  /// this to communicate with Firebase Authentication.
  /// @param[out] init_result_out Optional: If provided, write the init result
  /// here. Will be set to kInitResultSuccess if initialization succeeded, or
  /// kInitResultFailedMissingDependency on Android if Google Play services is
  /// not available on the current device.
  ///
  /// @returns An instance of Storage corresponding to the given App.
  static Storage* GetInstance(::firebase::App* app,
                              InitResult* init_result_out = nullptr);

  /// @brief Get the firease::App that this Storage was created with.
  ///
  /// @returns The firebase::App this Storage was created with.
  ::firebase::App* app();

  /// @brief Get a StorageReference to the root of the database.
  StorageReference GetReference() const;

  /// @brief Get a StorageReference for the specified path.
  StorageReference GetReference(const char* path) const;
  /// @brief Get a StorageReference for the specified path.
  StorageReference GetReference(const std::string& path) const;

  /// @brief Get a StorageReference for the provided URL.
  StorageReference GetReferenceFromUrl(const char* url) const;
  /// @brief Get a StorageReference for the provided URL.
  StorageReference GetReferenceFromUrl(const std::string& url) const;

  /// @brief Returns the maximum time in seconds to retry a download if a
  /// failure occurs.
  double max_download_retry_time();
  /// @brief Sets the maximum time to retry a download if a failure occurs.
  /// Defaults to 600 seconds (10 minutes).
  void set_max_download_retry_time(double max_transfer_retry_seconds);

  /// @brief Returns the maximum time to retry an upload if a failure occurs.
  double max_upload_retry_time();
  /// @brief Sets the maximum time to retry an upload if a failure occurs.
  /// Defaults to 600 seconds (10 minutes).
  void set_max_upload_retry_time(double max_transfer_retry_seconds);

  /// @brief Returns the maximum time to retry operations other than upload
  /// and download if a failure occurs.
  double max_operation_retry_time();
  /// @brief Sets the maximum time to retry operations other than upload and
  /// download if a failure occurs. Defaults to 120 seconds (2 minutes).
  void set_max_operation_retry_time(double max_transfer_retry_seconds);

 private:
  /// @cond FIREBASE_APP_INTERNAL
  Storage(::firebase::App* app);
  Storage(const Storage& src);
  Storage& operator=(const Storage& src);

  internal::StorageInternal* internal_;
  /// @endcond
};

}  // namespace storage
}  // namespace firebase

#endif  // FIREBASE_STORAGE_CLIENT_CPP_SRC_INCLUDE_FIREBASE_STORAGE_H_
