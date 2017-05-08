// Copyright 2016 Google Inc. All Rights Reserved.

#ifndef FIREBASE_APP_CLIENT_CPP_SRC_INCLUDE_FIREBASE_LOG_H_
#define FIREBASE_APP_CLIENT_CPP_SRC_INCLUDE_FIREBASE_LOG_H_

/// @brief Namespace that encompasses all Firebase APIs.
namespace firebase {

/// @cond FIREBASE_APP_INTERNAL

/// @brief Levels used when logging messages.
enum LogLevel {
  /// Verbose Log Level
  kLogLevelVerbose = 0,
  /// Debug Log Level
  kLogLevelDebug,
  /// Info Log Level
  kLogLevelInfo,
  /// Warning Log Level
  kLogLevelWarning,
  /// Error Log Level
  kLogLevelError,
  /// Assert Log Level
  kLogLevelAssert,
};

/// @endcond

}  // namespace firebase

#endif  // FIREBASE_APP_CLIENT_CPP_SRC_INCLUDE_FIREBASE_LOG_H_
