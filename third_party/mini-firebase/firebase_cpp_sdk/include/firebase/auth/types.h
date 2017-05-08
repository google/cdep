// Copyright 2016 Google Inc. All Rights Reserved.

#ifndef FIREBASE_AUTH_CLIENT_CPP_INCLUDE_FIREBASE_AUTH_TYPES_H_
#define FIREBASE_AUTH_CLIENT_CPP_INCLUDE_FIREBASE_AUTH_TYPES_H_

namespace firebase {
namespace auth {

/// All possible error codes from asynchronous calls.
enum AuthError {
  /// Success.
  kAuthErrorNone = 0,

  /// Function will be implemented in a later revision of the API.
  kAuthErrorUnimplemented = -1,

  /// For error details,
  /// @if cpp_examples
  /// call Future::ErrorMessage().
  /// @endif
  kAuthErrorFailure = 1,
};

}  // namespace auth
}  // namespace firebase

#endif  // FIREBASE_AUTH_CLIENT_CPP_INCLUDE_FIREBASE_AUTH_TYPES_H_
