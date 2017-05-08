// Copyright 2016 Google Inc. All Rights Reserved.

#ifndef FIREBASE_STORAGE_CLIENT_CPP_SRC_INCLUDE_FIREBASE_STORAGE_METADATA_H_
#define FIREBASE_STORAGE_CLIENT_CPP_SRC_INCLUDE_FIREBASE_STORAGE_METADATA_H_

#include <map>
#include <string>
#include <vector>

#include "firebase/internal/common.h"

namespace firebase {
namespace storage {

namespace internal {
class MetadataInternal;
class StorageReferenceInternal;
}  // namespace internal

class StorageReference;

/// @brief Metadata stores default attributes such as size and content type.
///
/// Metadata for a StorageReference. You may also store custom metadata key
/// value pairs. Metadata values may be used to authorize operations using
/// declarative validation rules.
class Metadata {
 public:
  /// @brief Default constructor.
  Metadata();

  /// @brief Copy constructor.
  ///
  /// @param[in] other Metadata to copy from.
  Metadata(const Metadata& other);

  /// @brief Copy assignment operator.
  ///
  /// @param[in] other Metadata to copy from.
  ///
  /// @returns Reference to the destination Metadata.
  Metadata& operator=(const Metadata& other);

#if defined(FIREBASE_USE_MOVE_OPERATORS) || defined(DOXYGEN)
  /// @brief Move constructor. Moving is an efficient operation for Metadata.
  ///
  /// @param[in] other Metadata to move from.
  Metadata(Metadata&& other);

  /// @brief Move assignment operator. Moving is an efficient operation for
  /// Metadata.
  ///
  /// @param[in] other Metadata to move from.
  ///
  /// @returns Reference to the destination Metadata.
  Metadata& operator=(Metadata&& other);
#endif  // defined(FIREBASE_USE_MOVE_OPERATORS) || defined(DOXYGEN)

  ~Metadata();

  /// @brief Return the owning Google Cloud Storage bucket for the
  /// StorageReference.
  ///
  /// @returns The owning Google Cloud Storage bucket for the StorageReference.
  const char* bucket() const;

  /// @brief Set the Cache Control setting of the StorageReference.
  ///
  /// @see https://tools.ietf.org/html/rfc7234#section-5.2
  void set_cache_control(const char* cache_control);

  /// @brief Set the Cache Control setting of the StorageReference.
  ///
  /// @see https://tools.ietf.org/html/rfc7234#section-5.2
  void set_cache_control(const std::string& cache_control) {
    set_cache_control(cache_control.c_str());
  }

  /// @brief Return the Cache Control setting of the StorageReference.
  ///
  /// @returns The Cache Control setting of the StorageReference.
  ///
  /// @see https://tools.ietf.org/html/rfc7234#section-5.2
  const char* cache_control() const;

  /// @brief Set the content disposition of the StorageReference.
  ///
  /// @see https://tools.ietf.org/html/rfc6266
  void set_content_disposition(const char* disposition);

  /// @brief Set the content disposition of the StorageReference.
  ///
  /// @see https://tools.ietf.org/html/rfc6266
  void set_content_disposition(const std::string& disposition) {
    set_content_disposition(disposition.c_str());
  }

  /// @brief Return the content disposition of the StorageReference.
  ///
  /// @returns The content disposition of the StorageReference.
  ///
  /// @see https://tools.ietf.org/html/rfc6266
  const char* content_disposition() const;

  /// @brief Set the content encoding for the StorageReference.
  ///
  /// @see https://tools.ietf.org/html/rfc2616#section-14.11
  void set_content_encoding(const char* encoding);

  /// @brief Set the content encoding for the StorageReference.
  ///
  /// @see https://tools.ietf.org/html/rfc2616#section-14.11
  void set_content_encoding(const std::string& encoding) {
    set_content_encoding(encoding.c_str());
  }

  /// @brief Return the content encoding for the StorageReference.
  ///
  /// @returns The content encoding for the StorageReference.
  ///
  /// @see https://tools.ietf.org/html/rfc2616#section-14.11
  const char* content_encoding() const;

  /// @brief Set the content language for the StorageReference.
  ///
  /// @see https://tools.ietf.org/html/rfc2616#section-14.12
  void set_content_language(const char* language);

  /// @brief Set the content language for the StorageReference.
  ///
  /// @see https://tools.ietf.org/html/rfc2616#section-14.12
  void set_content_language(const std::string& language) {
    set_content_language(language.c_str());
  }

  /// @brief Return the content language for the StorageReference.
  ///
  /// @returns The content language for the StorageReference.
  ///
  /// @see https://tools.ietf.org/html/rfc2616#section-14.12
  const char* content_language() const;

  /// @brief Set the content type of the StorageReference.
  ///
  /// @see https://tools.ietf.org/html/rfc2616#section-14.17
  void set_content_type(const char* type);

  /// @brief Set the content type of the StorageReference.
  ///
  /// @see https://tools.ietf.org/html/rfc2616#section-14.17
  void set_content_type(const std::string& type) {
    set_content_type(type.c_str());
  }

  /// @brief Return the content type of the StorageReference.
  ///
  /// @returns The content type of the StorageReference.
  ///
  /// @see https://tools.ietf.org/html/rfc2616#section-14.17
  const char* content_type() const;

  /// @brief Return the time the StorageReference was created in milliseconds
  /// since the epoch.
  ///
  /// @returns The time the StorageReference was created in milliseconds since
  /// the epoch.
  int64_t creation_time() const;

  /// @brief Return a map of custom metadata key value pairs.
  ///
  /// The pointer returned is only valid during the lifetime of the Metadata
  /// object that owns it.
  ///
  /// @returns The keys for custom metadata.
  std::map<std::string, std::string>* custom_metadata() const;

  /// @brief Returns a long lived download URL with a revokable token.
  ///
  /// This can be used to share the file with others, but can be revoked by a
  /// developer in the Firebase Console if desired.
  ///
  /// @returns A long lived download URL with a revokable token.
  const char* download_url() const;

  /// @brief Return all unguessable Urls that can be used to download the
  /// StorageReference.
  ///
  /// @returns All unguessable Urls that can be used to download the
  /// StorageReference.
  std::vector<std::string> download_urls() const;

  /// @brief Return a version String indicating what version of the
  /// StorageReference.
  ///
  /// @returns A version String indicating what version of the StorageReference.
  int64_t generation() const;

  /// @brief Return a version String indicating the version of this
  /// StorageMetadata.
  ///
  /// @returns A version String indicating the version of this StorageMetadata.
  int64_t metadata_generation() const;

  /// @brief Return a simple name of the StorageReference object.
  ///
  /// @returns A simple name of the StorageReference object.
  const char* name() const;

  /// @brief Return the path of the StorageReference object.
  ///
  /// @returns The path of the StorageReference object.
  const char* path() const;

  /// @brief Return the associated StorageReference for which this metadata
  /// belongs to.
  ///
  /// @returns The associated StorageReference for which this metadata belongs.
  /// to.
  StorageReference GetReference() const;

  /// @brief Return the stored Size in bytes of the StorageReference object.
  ///
  /// @returns The stored Size in bytes of the StorageReference object.
  int64_t size_bytes() const;

  /// @brief Return the time the StorageReference was last updated in
  /// milliseconds since the epoch.
  ///
  /// @return The time the StorageReference was last updated in milliseconds
  /// since the epoch.
  int64_t updated_time() const;

  /// @brief Returns true if this Metadata is valid, false if it is not
  /// valid. An invalid Metadata is one that is not associated with any files.
  ///
  /// @returns true if this Metadata is valid, false if this Metadata is
  /// invalid.
  bool is_valid() const;

 private:
  /// @cond FIREBASE_APP_INTERNAL
  friend class StorageReference;
  friend class internal::StorageReferenceInternal;

  Metadata(internal::MetadataInternal* internal);

  internal::MetadataInternal* internal_;
  /// @endcond
};

}  // namespace storage
}  // namespace firebase

#endif  // FIREBASE_STORAGE_CLIENT_CPP_SRC_INCLUDE_FIREBASE_STORAGE_METADATA_H_
