// Copyright 2016 Google Inc. All Rights Reserved.

#ifndef FIREBASE_DATABASE_CLIENT_CPP_SRC_INCLUDE_FIREBASE_DATABASE_TRANSACTION_H_
#define FIREBASE_DATABASE_CLIENT_CPP_SRC_INCLUDE_FIREBASE_DATABASE_TRANSACTION_H_

#include "firebase/database/common.h"
#include "firebase/database/data_snapshot.h"
#include "firebase/database/mutable_data.h"
#include "firebase/internal/common.h"
#include "firebase/variant.h"

namespace firebase {
namespace database {

/// Specifies whether the transaction succeeded or not.
enum TransactionResult {
  /// The transaction was successful, the MutableData was updated.
  kTransactionResultSuccess,
  /// The transaction did not succeed. Any changes to the MutableData
  /// will be discarded.
  kTransactionResultAbort,
};

/// Your own transaction handler, which the Firebase Realtime Database library
/// may call multiple times to apply changes to the data, and should return
/// success or failure depending on whether it succeeds.
///
/// This function will be called, _possibly multiple times_, with the current
/// data at this location. The function is responsible for inspecting that data
/// and modifying it as desired, then returning a TransactionResult specifying
/// either that the MutableData was modified to a desired new state, or that the
/// transaction should be aborted. Whenever this function is called, the
/// MutableData passed in must be modified from scratch.
///
/// Since this function may be called repeatedly for the same transaction, be
/// extremely careful of any side effects that may be triggered by this
/// function. In addition, this function is called from within the Firebase
/// Realtime Database library's run loop, so care is also required when
/// accessing data that may be in use by other threads in your application.
///
/// Best practices for this function are to ONLY rely on the data passed in.
///
/// @param[in] data Mutable data, which the callback can edit.
///
/// @returns The callback should return kTransactionResultSuccess if the data
/// was modified, or kTransactionResultAbort if it was unable to modify the
/// data. If the callback returns kTransactionResultAbort, the RunTransaction()
/// call will return the kErrorTransactionAbortedByUser error code.
///
/// @note If you want a callback to be triggered when the transaction is
/// finished, you can use the Future<DataSnapshot> value returned by the method
/// running the transaction, and call Future::OnCompletion() to register a
/// callback to be called when the transaction either succeeds or fails.
typedef TransactionResult(DoTransaction)(MutableData* data);

}  // namespace database
}  // namespace firebase

#endif  // FIREBASE_DATABASE_CLIENT_CPP_SRC_INCLUDE_FIREBASE_DATABASE_TRANSACTION_H_
