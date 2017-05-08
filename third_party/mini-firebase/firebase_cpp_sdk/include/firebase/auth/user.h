// Copyright 2016 Google Inc. All Rights Reserved.

#ifndef FIREBASE_AUTH_CLIENT_CPP_INCLUDE_FIREBASE_USER_H_
#define FIREBASE_AUTH_CLIENT_CPP_INCLUDE_FIREBASE_USER_H_

#include <string>
#include <vector>

#include "firebase/auth/credential.h"
#include "firebase/auth/types.h"
#include "firebase/future.h"

namespace firebase {
namespace auth {

// Predeclarations.
class Auth;
struct AuthData;

/// @brief Interface implemented by each identity provider.
class UserInfoInterface {
 public:
  virtual ~UserInfoInterface();

  /// Gets the unique user ID for the user.
  ///
  /// @note The user's ID, unique to the Firebase project.
  /// Do NOT use this value to authenticate with your backend server, if you
  /// have one. Use User::Token() instead.
  virtual std::string UID() const = 0;

  /// Gets email associated with the user, if any.
  virtual std::string Email() const = 0;

  /// Gets the display name associated with the user, if any.
  virtual std::string DisplayName() const = 0;

  /// Gets the photo url associated with the user, if any.
  virtual std::string PhotoUrl() const = 0;

  /// Gets the provider ID for the user (For example, "Facebook").
  virtual std::string ProviderId() const = 0;
};

/// @brief Firebase user account object.
///
/// This class allows you to manipulate the profile of a user, link to and
/// unlink from authentication providers, and refresh authentication tokens.
class User : public UserInfoInterface {
 public:
  /// Parameters to the UpdateUserProfile() function.
  ///
  /// For fields you don't want to update, pass NULL.
  /// For fields you want to reset, pass "".
  struct UserProfile {
    /// Construct a UserProfile with no display name or photo URI.
    UserProfile() : display_name(NULL), photo_uri(NULL) {}

    /// User display name.
    const char* display_name;
    /// User photo URI.
    const char* photo_uri;
  };

  /// The Java Web Token (JWT) that can be used to identify the user to
  /// the backend.
  ///
  /// If a current ID token is still believed to be valid (i.e. it has not yet
  /// expired), that token will be returned immediately.
  /// A developer may set the optional forceRefresh flag to get a new ID token,
  /// whether or not the existing token has expired.  For example, a developer
  /// may use this when they have discovered that the token is invalid for some
  /// other reason.
  Future<std::string> Token(bool force_refresh);

  /// Get results of the most recent call to @ref Token.
  Future<std::string> TokenLastResult() const;

  /// Gets the raw refresh token (only for use in advanced scenarios where
  /// manually refresh of tokens is required).
  std::string RefreshToken() const;

  /// Gets the third party profile data associated with this user returned by
  /// the authentication server, if any.
  const std::vector<UserInfoInterface*>& ProviderData() const;

  /// Sets the email address for the user.
  ///
  /// May fail if there is already an email/password-based account for the same
  /// email address.
  Future<void> UpdateEmail(const char* email);

  /// Get results of the most recent call to @ref UpdateEmail.
  Future<void> UpdateEmailLastResult() const;

  /// Attempts to change the password for the current user.
  ///
  /// For an account linked to an Identity Provider (IDP) with no password,
  /// this will result in the account becoming an email/password-based account
  /// while maintaining the IDP link. May fail if the password is invalid,
  /// if there is a conflicting email/password-based account, or if the token
  /// has expired.
  /// To retrieve fresh tokens,
  /// @if cpp_examples
  /// call @ref Reauthenticate.
  /// @endif
  Future<void> UpdatePassword(const char* password);

  /// Get results of the most recent call to @ref UpdatePassword.
  Future<void> UpdatePasswordLastResult() const;

  /// Reauthenticate using a credential.
  ///
  /// @if cpp_examples
  /// Some APIs (for example, @ref UpdatePassword, @ref Delete) require that
  /// the token used to invoke them be from a recent login attempt.
  /// This API takes an existing credential for the user and retrieves fresh
  /// tokens, ensuring that the operation can proceed. Developers can call
  /// this method prior to calling @ref UpdatePassword() to ensure success.
  /// @endif
  ///
  /// Returns an error if the existing credential is not for this user
  /// or if sign-in with that credential failed. The user should remain
  /// signed in even if this method failed. If the developer had held
  /// a reference to that user, the reference will continue to be valid
  /// after this operation.
  Future<void> Reauthenticate(const Credential& credential);

  /// Get results of the most recent call to @ref UpdatePassword.
  Future<void> ReauthenticateLastResult() const;

  /// Initiates email verification for the user.
  Future<void> SendEmailVerification();

  /// Get results of the most recent call to @ref SendEmailVerification.
  Future<void> SendEmailVerificationLastResult() const;

  /// Updates a subset of user profile information.
  Future<void> UpdateUserProfile(const UserProfile& profile);

  /// Get results of the most recent call to @ref UpdateUserProfile.
  Future<void> UpdateUserProfileLastResult() const;

  /// Links the user with the given 3rd party credentials.
  ///
  /// For example, a Facebook login access token, a Twitter token/token-secret
  /// pair.
  /// Status will be an error if the token is invalid, expired, or otherwise
  /// not accepted by the server as well as if the given 3rd party
  /// user id is already linked with another user account or if the current user
  /// is already linked with another id from the same provider.
  Future<User*> LinkWithCredential(const Credential& credential);

  /// Get results of the most recent call to @ref LinkWithCredential.
  Future<User*> LinkWithCredentialLastResult() const;

  /// Unlinks the current user from the provider specified.
  /// Status will be an error if the user is not linked to the given provider.
  Future<User*> Unlink(const char* provider);

  /// Get results of the most recent call to @ref Unlink.
  Future<User*> UnlinkLastResult() const;

  /// Refreshes the data for this user.
  ///
  /// For example, the attached providers, email address, display name, etc.
  Future<void> Reload();

  /// Get results of the most recent call to @ref Reload.
  Future<void> ReloadLastResult() const;

  /// Deletes the user account.
  Future<void> Delete();

  /// Get results of the most recent call to @ref Delete.
  Future<void> DeleteLastResult() const;

  /// Returns true if the email address associated with this user has been
  /// verified.
  bool EmailVerified() const;

  /// Returns true if user signed in anonymously.
  bool Anonymous() const;

  /// Gets the unique user ID for the user.
  ///
  /// @note The user's ID, unique to the Firebase project.
  /// Do NOT use this value to authenticate with your backend server, if you
  /// have one.
  /// @if cpp_examples
  /// Use User::Token() instead.
  /// @endif
  virtual std::string UID() const;

  /// Gets email associated with the user, if any.
  virtual std::string Email() const;

  /// Gets the display name associated with the user, if any.
  virtual std::string DisplayName() const;

  /// Gets the photo url associated with the user, if any.
  virtual std::string PhotoUrl() const;

  /// Gets the provider ID for the user (For example, "Facebook").
  virtual std::string ProviderId() const;

 private:
  friend struct AuthData;

  // Only exists in AuthData. Access via @ref Auth::CurrentUser().
  User(AuthData* auth_data) : auth_data_(auth_data) {}

  // Use the pimpl mechanism to hide data details in the cpp files.
  AuthData* auth_data_;
};

}  // namespace auth
}  // namespace firebase

#endif  // FIREBASE_AUTH_CLIENT_CPP_INCLUDE_FIREBASE_USER_H_
