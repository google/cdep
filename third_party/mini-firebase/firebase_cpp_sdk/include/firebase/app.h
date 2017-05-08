// Copyright 2016 Google Inc. All Rights Reserved.

#ifndef FIREBASE_APP_CLIENT_CPP_SRC_INCLUDE_FIREBASE_APP_H_
#define FIREBASE_APP_CLIENT_CPP_SRC_INCLUDE_FIREBASE_APP_H_

#ifdef __ANDROID__
#include <jni.h>
#endif  // __ANDROID__
#include <map>
#include <string>

/// @brief Namespace that encompasses all Firebase APIs.
namespace firebase {

// Predeclarations.
namespace auth {
class Auth;
}  // namespace auth
namespace database {
namespace internal {
class DatabaseInternal;
}  // namespace internal
}  // namespace database
namespace storage {
namespace internal {
class StorageInternal;
}  // namespace internal
}  // namespace storage

/// @brief Reports whether a Firebase module initialized successfully.
enum InitResult {
  /// The given library was successfully initialized.
  kInitResultSuccess = 0,

  /// The given library failed to initialize due to a missing dependency.
  ///
  /// On Android, this typically means that Google Play services is not
  /// available and the library requires it.
  /// @if cpp_examples
  /// Use google_play_services::CheckAvailability() and
  /// google_play_services::MakeAvailable() to resolve this issue.
  /// @endif
  ///
  /// Also, on Android this value can be returned if the Java dependencies of a
  /// Firebase component are not included in the application causing
  /// initialization to fail.  This means that the application's build
  /// environment is not configured correctly.  To resolve the problem,
  /// see the SDK setup documentation for the set of Java dependencies (AARs)
  /// required for the component that failed to initialize.
  kInitResultFailedMissingDependency
};

// This doesn't need to be accessible to the end user, especially in C#.
// Because this is not a class member, it forces C# to create a class
// just to contain this so it's cleaner just not to include it.

/// @brief Default name for firebase::App() objects.
extern const char* kDefaultAppName;

/// @brief Options that control the creation of a Firebase App.
/// @if cpp_examples
/// @see firebase::App
/// @endif
class AppOptions {
 public:
  /// @brief Create AppOptions.
  ///
  /// @if cpp_examples
  /// To create a firebase::App object, the Firebase application identifier
  /// and API key should be set using set_app_id() and set_api_key()
  /// respectively.
  ///
  /// @see firebase::App::Create().
  /// @endif
  AppOptions() {}

  /// Set the Firebase app ID used to uniquely identify an instance of an app.
  ///
  /// This is the mobilesdk_app_id in the Android google-services.json config
  /// file or PROJECT_ID in the GoogleService-Info.plist.
  ///
  /// This only needs to be specified if your application does not include
  /// google-services.json or GoogleService-Info.plist in its resources.
  void set_app_id(const char* id) { app_id_ = id; }

  /// Retrieves the app ID.
  ///
  /// @if cpp_examples
  /// @see set_app_id().
  /// @endif
  ///
  const char* app_id() const { return app_id_.c_str(); }

  /// API key used to authenticate requests from your app.
  ///
  /// For example, "AIzaSyDdVgKwhZl0sTTTLZ7iTmt1r3N2cJLnaDk" used to identify
  /// your app to Google servers.
  ///
  /// This only needs to be specified if your application does not include
  /// google-services.json or GoogleService-Info.plist in its resources.
  void set_api_key(const char* key) { api_key_ = key; }

  /// Get the API key.
  ///
  /// @if cpp_examples
  /// @see set_api_key().
  /// @endif
  ///
  const char* api_key() const { return api_key_.c_str(); }

  /// Set the Firebase Cloud Messaging sender ID.
  ///
  /// For example "012345678901", used to configure Firebase Cloud Messaging.
  ///
  /// This only needs to be specified if your application does not include
  /// google-services.json or GoogleService-Info.plist in its resources.
  void set_messaging_sender_id(const char* sender_id) {
    fcm_sender_id_ = sender_id;
  }

  /// Get the Firebase Cloud Messaging sender ID.
  ///
  /// @if cpp_examples
  /// @see set_messaging_sender_id().
  /// @endif
  ///
  const char* messaging_sender_id() const { return fcm_sender_id_.c_str(); }

  /// Set the database root URL, e.g. @"http://abc-xyz-123.firebaseio.com".
  void set_database_url(const char* url) { database_url_ = url; }

  /// Get database root URL, e.g. @"http://abc-xyz-123.firebaseio.com".
  ///
  const char* database_url() const { return database_url_.c_str(); }

  /// @cond FIREBASE_APP_INTERNAL

  /// Set the tracking ID for Google Analytics, e.g. @"UA-12345678-1".
  void set_ga_tracking_id(const char* id) { ga_tracking_id_ = id; }

  /// Get the tracking ID for Google Analytics,
  ///
  /// @if cpp_examples
  /// @see set_ga_tracking_id().
  /// @endif
  ///
  const char* ga_tracking_id() const { return ga_tracking_id_.c_str(); }

  /// @endcond

  /// Set the Google Cloud Storage bucket name,
  /// e.g. @\"abc-xyz-123.storage.firebase.com\".
  void set_storage_bucket(const char* bucket) { storage_bucket_ = bucket; }

  /// Get the Google Cloud Storage bucket name,
  /// @see set_storage_bucket().
  const char* storage_bucket() const { return storage_bucket_.c_str(); }

  /// @cond FIREBASE_APP_INTERNAL
 private:
  /// API key used to communicate with Google Servers.
  std::string api_key_;
  /// ID of the app.
  std::string app_id_;
  /// Database root URL.
  std::string database_url_;
  /// Google analytics tracking ID.
  std::string ga_tracking_id_;
  /// FCM sender ID.
  std::string fcm_sender_id_;
  /// Google Cloud Storage bucket name.
  std::string storage_bucket_;
  /// @endcond
};

/// @brief Firebase application object.
///
/// firebase::App acts as a conduit for communication between all Firebase
/// services used by an application.
///
/// @if cpp_examples
/// For example:
/// @code
/// #if defined(__ANDROID__)
/// firebase::App::Create(firebase::AppOptions(), jni_env, activity);
/// #else
/// firebase::App::Create(firebase::AppOptions());
/// #endif  // defined(__ANDROID__)
/// @endcode
/// @endif
class App {
 public:
  ~App();

#if !defined(__ANDROID__) || defined(DOXYGEN)
  /// @brief Initializes the default firebase::App with default options.
  ///
  /// @note This method is specific to non-Android implementations.
  ///
  /// @returns New App instance, the App should not be destroyed for the
  /// lifetime of the application.
  static App* Create() { return Create(AppOptions()); }
#endif  // !defined(__ANDROID__) || defined(DOXYGEN)

#if defined(__ANDROID__) || defined(DOXYGEN)
  /// @brief Initializes the default firebase::App with default options.
  ///
  /// @note This method is specific to the Android implementation.
  ///
  /// @param[in] jni_env JNI environment required to allow Firebase services
  /// to interact with the Android framework.
  /// @param[in] activity JNI reference to the Android activity, required to
  /// allow Firebase services to interact with the Android application.
  ///
  /// @returns New App instance. The App should not be destroyed for the
  /// lifetime of the application.
  static App* Create(JNIEnv* jni_env, jobject activity) {
    return Create(AppOptions(), jni_env, activity);
  }
#endif  // defined(__ANDROID__) || defined(DOXYGEN)

#if !defined(__ANDROID__) || defined(DOXYGEN)
  /// @brief Initializes the default firebase::App with the given options.
  ///
  /// @note This method is specific to non-Android implementations.
  ///
  /// Options are copied at initialization time, so changes to the object are
  /// ignored.
  /// @param[in] options Options that control the creation of the App.
  ///
  /// @returns New App instance, the App should not be destroyed for the
  /// lifetime of the application.
  static App* Create(const AppOptions& options);
#endif  // !defined(__ANDROID__) || defined(DOXYGEN)

#if defined(__ANDROID__) || defined(DOXYGEN)
  /// @brief Initializes the default firebase::App with the given options.
  ///
  /// @note This method is specific to the Android implementation.
  ///
  /// Options are copied at initialization time, so changes to the object are
  /// ignored.
  /// @param[in] options Options that control the creation of the App.
  /// @param[in] jni_env JNI environment required to allow Firebase services
  /// to interact with the Android framework.
  /// @param[in] activity JNI reference to the Android activity, required to
  /// allow Firebase services to interact with the Android application.
  ///
  /// @returns New App instance. The App should not be destroyed for the
  /// lifetime of the application.
  static App* Create(const AppOptions& options, JNIEnv* jni_env,
                     jobject activity);
#endif  // defined(__ANDROID__) || defined(DOXYGEN)

#if !defined(__ANDROID__) || defined(DOXYGEN)
  /// @brief Initializes a firebase::App with the given options that operates
  /// on the named app.
  ///
  /// @note This method is specific to non-Android implementations.
  ///
  /// Options are copied at initialization time, so changes to the object are
  /// ignored.
  /// @param[in] options Options that control the creation of the App.
  /// @param[in] name Name of this App instance.  This is only required when
  /// one application uses multiple App instances.
  ///
  /// @returns New App instance, the App should not be destroyed for the
  /// lifetime of the application.
  static App* Create(const AppOptions& options, const char* name);
#endif  // !defined(__ANDROID__) || defined(DOXYGEN)

#if defined(__ANDROID__) || defined(DOXYGEN)
  /// @brief Initializes a firebase::App with the given options that operates
  /// on the named app.
  ///
  /// @note This method is specific to the Android implementation.
  ///
  /// Options are copied at initialization time, so changes to the object are
  /// ignored.
  /// @param[in] options Options that control the creation of the App.
  /// @param[in] name Name of this App instance.  This is only required when
  /// one application uses multiple App instances.
  /// @param[in] jni_env JNI environment required to allow Firebase services
  /// to interact with the Android framework.
  /// @param[in] activity JNI reference to the Android activity, required to
  /// allow Firebase services to interact with the Android application.
  ///
  /// @returns New App instance. The App should not be destroyed for the
  /// lifetime of the application.
  static App* Create(const AppOptions& options, const char* name,
                     JNIEnv* jni_env, jobject activity);
#endif  // defined(__ANDROID__) || defined(DOXYGEN)

  /// Get the default App, or nullptr if none has been created.
  static App* GetInstance();

  /// Get the App with the given name, or nullptr if none have been created.
  static App* GetInstance(const char* name);

#if defined(__ANDROID__) || defined(DOXYGEN)
  /// Get Java virtual machine, retrieved from the initial JNI environment.
  /// @note This method is specific to the Android implementation.
  ///
  /// @returns JNI Java virtual machine object.
  JavaVM* java_vm() const { return java_vm_; }
  /// Get JNI environment, needed for performing JNI calls, set on creation.
  /// This is not trivial as the correct environment needs to retrieved per
  /// thread.
  /// @note This method is specific to the Android implementation.
  ///
  /// @returns JNI environment object.
  JNIEnv* GetJNIEnv() const;
  /// Get a global reference to the Android activity provided to the App on
  /// creation. Also serves as the Context needed for Firebase calls.
  /// @note This method is specific to the Android implementation.
  ///
  /// @returns Global JNI reference to the Android activity used to create
  /// the App.  The reference count of the returned object is not increased.
  jobject activity() const { return activity_; }
#endif  // defined(__ANDROID__) || defined(DOXYGEN)

  /// Get the name of this App instance.
  ///
  /// @returns The name of this App instance.  If a name wasn't provided via
  /// Create(), this returns @ref kDefaultAppName.
  const char* name() const { return name_.c_str(); }

  /// Get options the App was created with.
  ///
  /// @returns Options used to create the App.
  const AppOptions& options() const { return options_; }


 private:
  /// @cond FIREBASE_APP_INTERNAL
  friend class auth::Auth;
  friend class database::internal::DatabaseInternal;
  friend class storage::internal::StorageInternal;

  /// Construct the object.
  App();

#if defined(__ANDROID__) || defined(DOXYGEN)
  /// JNI reference to the Java virtual machine associated with the App's
  /// process.
  /// @note This is specific to Android.
  JavaVM* java_vm_;
  /// Android activity.
  /// @note This is specific to Android.
  jobject activity_;
#endif  // defined(__ANDROID__) || defined(DOXYGEN)

  /// Name of the App instance.
  std::string name_;
  /// Options used to create this App instance.
  AppOptions options_;
  /// Module initialization results.
  std::map<std::string, InitResult> init_results_;
  /// Pointer to other internal data used by this instance.
  void* data_;

  /// @endcond
};

}  // namespace firebase

#endif  // FIREBASE_APP_CLIENT_CPP_SRC_INCLUDE_FIREBASE_APP_H_
