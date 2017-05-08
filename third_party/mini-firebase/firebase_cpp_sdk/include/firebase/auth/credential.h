// Copyright 2016 Google Inc. All Rights Reserved.

#ifndef FIREBASE_AUTH_CLIENT_CPP_INCLUDE_FIREBASE_AUTH_CREDENTIAL_H_
#define FIREBASE_AUTH_CLIENT_CPP_INCLUDE_FIREBASE_AUTH_CREDENTIAL_H_

#include <string>

namespace firebase {

// Predeclarations.
class App;

namespace auth {

// Predeclarations.
class Auth;
struct AuthData;
class User;

/// @brief Authentication credentials for an authentication provider.
///
/// An authentication provider is a service that allows you to authenticate
/// a user. Firebase provides email/password authentication, but there are also
/// external authentication providers such as Facebook.
class Credential {
  /// @cond FIREBASE_APP_INTERNAL
  friend class EmailAuthProvider;
  friend class FacebookAuthProvider;
  friend class GitHubAuthProvider;
  friend class GoogleAuthProvider;
  friend class TwitterAuthProvider;
  /// @endcond

 private:
  /// Should only be created by `Provider` classes.
  ///
  /// @see EmailAuthProvider::GetCredential()
  /// @see FacebookAuthProvider::GetCredential()
  /// @see GoogleAuthProvider::GetCredential()
  explicit Credential(void* impl) : impl_(impl) {}

 public:
  ~Credential();

  /// Copy constructor.
  Credential(const Credential& rhs);

  /// Copy a Credential.
  Credential& operator=(const Credential& rhs);

  /// Gets the name of the Identification Provider (IDP) for the credential.
  std::string Provider() const;

  /// @cond FIREBASE_APP_INTERNAL
 protected:
  /// @cond FIREBASE_APP_INTERNAL
  friend class Auth;
  friend class User;
  /// @endcond

  /// Platform-specific implementation.
  /// For example, FIRAuthCredential* on iOS.
  void* impl_;
  /// @endcond
};

/// @brief Use email and password to authenticate.
///
/// Allows developers to use the email and password credentials as they could
/// other auth providers.  For example, this can be used to change passwords,
/// log in, etc.
class EmailAuthProvider {
 public:
  /// Generate a credential from the given email and password.
  ///
  /// @param email E-mail to generate the credential from.
  /// @param password Password to use for the new credential.
  ///
  /// @returns New auth::Credential.
  static Credential GetCredential(const char* email, const char* password);
};

/// @brief Use an access token provided by Facebook to authenticate.
class FacebookAuthProvider {
 public:
  /// Generate a credential from the given Facebook token.
  ///
  /// @param access_token Facebook token to generate the credential from.
  ///
  /// @returns New auth::Credential.
  static Credential GetCredential(const char* access_token);
};

/// @brief Use an access token provided by GitHub to authenticate.
class GitHubAuthProvider {
 public:
  /// Generate a credential from the given GitHub token.
  ///
  /// @param token The GitHub OAuth access token.
  ///
  /// @returns New auth::Credential.
  static Credential GetCredential(const char* token);
};

/// @brief Use an ID token and access token provided by Google to authenticate.
class GoogleAuthProvider {
 public:
  /// Generate a credential from the given Google ID token and/or access token.
  ///
  /// @param id_token Google Sign-In ID token.
  /// @param access_token Google Sign-In access token.
  ///
  /// @returns New auth::Credential.
  static Credential GetCredential(const char* id_token,
                                  const char* access_token);
};

/// @brief Use a token and secret provided by Twitter to authenticate.
class TwitterAuthProvider {
 public:
  /// Generate a credential from the given Twitter token and password.
  ///
  /// @param token The Twitter OAuth token.
  /// @param secret The Twitter OAuth secret.
  ///
  /// @returns New auth::Credential.
  static Credential GetCredential(const char* token, const char* secret);
};

}  // namespace auth
}  // namespace firebase

#endif  // FIREBASE_AUTH_CLIENT_CPP_INCLUDE_FIREBASE_AUTH_CREDENTIAL_H_
