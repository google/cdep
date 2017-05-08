// Copyright 2016 Google Inc. All Rights Reserved.

#ifndef FIREBASE_STORAGE_CLIENT_CPP_SRC_INCLUDE_FIREBASE_STORAGE_COMMON_H_
#define FIREBASE_STORAGE_CLIENT_CPP_SRC_INCLUDE_FIREBASE_STORAGE_COMMON_H_

namespace firebase {
namespace storage {

/// Error code returned by Firebase Storage C++ functions.
enum Error {
  /// The operation was a success, no error occurred.
  kErrorNone = 0,
  /// An unknown error occurred.
  kErrorUnknown,
  /// No object exists at the desired reference.
  kErrorObjectNotFound,
  /// No bucket is configured for Firebase Storage.
  kErrorBucketNotFound,
  /// No project is configured for Firebase Storage.
  kErrorProjectNotFound,
  /// Quota on your Firebase Storage bucket has been exceeded.
  kErrorQuotaExceeded,
  /// User is unauthenticated.
  kErrorUnauthenticated,
  /// User is not authorized to perform the desired action.
  kErrorUnauthorized,
  /// The maximum time limit on an operation (upload, download, delete, etc.)
  /// has been exceeded.
  kErrorRetryLimitExceeded,
  /// File on the client does not match the checksum of the file recieved by the
  /// server.
  kErrorNonMatchingChecksum,
  /// Size of the downloaded file exceeds the amount of memory allocated for the
  /// download.
  kErrorDownloadSizeExceeded,
  /// User cancelled the operation.
  kErrorCancelled,
};

/// @brief Get the human-readable error message corresponding to an error code.
///
/// @param[in] error Error code to get the error message for.
///
/// @returns Statically-allocated string describing the error.
const char* GetErrorMessage(Error error);

}  // namespace storage
}  // namespace firebase


#endif  // FIREBASE_STORAGE_CLIENT_CPP_SRC_INCLUDE_FIREBASE_STORAGE_COMMON_H_
