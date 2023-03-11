package com.example.roaddocmanagement.firebase

import android.util.Log
import com.example.roaddocmanagement.activities.RegisterActivity
import com.example.roaddocmanagement.activities.SignInActivity
import com.example.roaddocmanagement.models.User
import com.example.roaddocmanagement.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {
    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterActivity, userInfo: User) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName, "Error registering the account")
            }
    }

    fun signInUser(activity: SignInActivity) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)
                if (loggedInUser != null)
                    activity.signInSuccess(loggedInUser)
            }.addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName, "Error registering the account")
            }

    }

    fun getCurrentUserId(): String {
        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
            if (currentUser != null) {
                currentUserId = currentUser.uid
            }
        return currentUserId
    }
}