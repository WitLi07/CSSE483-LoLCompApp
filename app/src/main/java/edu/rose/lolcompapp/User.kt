package edu.rose.lolcompapp

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var uid: String = "",
    var gamename: String = "",
    var lane: String = "",
    var preferedChampions: ArrayList<String> = arrayListOf(),
    var teams: String = ""
) : Parcelable {
    @get:Exclude
    var id: String = ""

    @ServerTimestamp
    var lastTouched: Timestamp? = null

    companion object {
        const val LAST_TOUCHED_KEY = "lastTouched"
        const val UID_KEY = "uid"
        fun fromSnapshot(snapsht: DocumentSnapshot): User {
            val user = snapsht.toObject(User::class.java)!!
            user.id = snapsht.id
            return user
        }
    }
}
