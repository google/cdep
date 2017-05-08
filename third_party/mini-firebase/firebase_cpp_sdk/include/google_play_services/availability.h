// Copyright 2016 Google Inc. All Rights Reserved.
#ifndef FIREBASE_APP_CLIENT_CPP_SRC_INCLUDE_GOOGLE_PLAY_SERVICES_AVAILABILITY_H_
#define FIREBASE_APP_CLIENT_CPP_SRC_INCLUDE_GOOGLE_PLAY_SERVICES_AVAILABILITY_H_

#if defined(__ANDROID__) || defined(DOXYGEN)

#if !defined(SWIG_BUILD)
#include <jni.h>
#endif
#include "firebase/future.h"

/// @brief Google Play services APIs included with the Firebase C++ SDK.
/// These APIs are Android-specific.
namespace google_play_services {

/// @brief Possible availability states for Google Play services.
enum Availability {
  /// Gooogle Play services are available.
  kAvailabilityAvailable,

  /// Google Play services is disabled in Settings.
  kAvailabilityUnavailableDisabled,

  /// Google Play services is invalid.
  kAvailabilityUnavailableInvalid,

  /// Google Play services is not installed.
  kAvailabilityUnavailableMissing,

  /// Google Play services does not have the correct permissions.
  kAvailabilityUnavailablePermissions,

  /// Google Play services need to be updated.
  kAvailabilityUnavailableUpdateRequired,

  /// Google Play services is currently updating.
  kAvailabilityUnavailableUpdating,

  /// Some other error occurred.
  kAvailabilityUnavailableOther,
};

/// @brief Check whether Google Play services is available on this device.
///
/// @return True if Google Play services is available and up-to-date, false if
/// not. If false was returned, you can call MakeAvailable() to attempt to
/// resolve the issue.
///
/// @see MakeAvailable()
///
/// @note This function is Android-specific.
Availability CheckAvailability(JNIEnv* env, jobject activity);

/// @brief Attempt to make Google Play services available, by installing,
/// updating, activating, or whatever else needs to be done.
///
/// @return A future result. When completed, the Error will be 0 if Google Play
/// services are now available, or nonzero if still unavailable.
///
/// @note This function is Android-specific.
::firebase::Future<void> MakeAvailable(JNIEnv* env, jobject activity);

/// @brief Get the future result from the most recent call to MakeAvailable().
///
/// @return The future result from the most recent call to MakeAvailable(). When
/// completed, the Error will be 0 if Google Play services are now available, or
/// nonzero if still unavailable.
///
/// @see MakeAvailable()
///
/// @note This function is Android-specific.
::firebase::Future<void> MakeAvailableLastResult();

}  // namespace google_play_services

#endif  // defined(__ANDROID__) || defined(DOXYGEN)

#endif  // FIREBASE_APP_CLIENT_CPP_SRC_INCLUDE_GOOGLE_PLAY_SERVICES_AVAILABILITY_H_
