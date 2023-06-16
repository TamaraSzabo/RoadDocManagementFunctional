package com.example.roaddocmanagement.workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import java.util.*

class ExpirationCheckWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        val db = FirebaseFirestore.getInstance()

        val expiredDocumentsQuery = db.collection("documents")
            .whereLessThan("expirationDate", getNearingExpirationDate())

        expiredDocumentsQuery.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val userId = document.getString("userId")
                    val documentName = document.getString("name")
                    val fcmToken = userId?.let {
                        if (documentName != null) {
                            getFCMTokenFromUserId(it, documentName )
                        }
                    }

                    document.getString("documentName")
                        ?.let {
                            if (fcmToken != null) {
                                sendExpirationNotification(fcmToken.toString(), it)
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("ExpirationCheckWorker", "Failed to query documents", e)
            }

        return Result.success()
    }

    private fun getNearingExpirationDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 5)
        return calendar.time
    }

    private fun getFCMTokenFromUserId(userId: String, documentName: String) {
        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("users")

        usersCollection
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val userDocument = querySnapshot.documents[0]
                    val fcmToken = userDocument.getString("fcmToken")
                    if (fcmToken != null) {
                        // Use the retrieved FCM token
                        sendExpirationNotification(fcmToken, documentName)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("getFCMTokenFromUserId", "Failed to retrieve FCM token", e)
            }
    }

    private fun sendExpirationNotification(fcmToken: String?, documentName: String) {
        val data = HashMap<String, String>()
        data["title"] = "Document Expiration"
        data["body"] = "$documentName is about to expire in a few days."

        val message = RemoteMessage.Builder(fcmToken.toString())
            .setMessageId(java.util.UUID.randomUUID().toString())
            .setData(data)
            .build()

        try {
            FirebaseMessaging.getInstance().send(message)
        } catch (e: Exception) {
            Log.e("sendExpirationNotification", "Failed to send notification", e)
        }
    }
}
