package edu.rose.lolcompapp

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Comp(
    var uid: String = "",
    var name: String = "",
    var top: String = "",
    var mid: String = "",
    var sup: String = "",
    var ad: String = "",
    var jungle: String = "",
    var users: ArrayList<User> = arrayListOf()
) : Parcelable {
    @get:Exclude
    var id: String = ""

    @ServerTimestamp
    var lastTouched: Timestamp? = null

    companion object {
        const val LAST_TOUCHED_KEY = "lastTouched"
        const val UID_KEY = "uid"
        fun fromSnapshot(snapsht: DocumentSnapshot): Comp {
            val comp = snapsht.toObject(Comp::class.java)!!
            comp.id = snapsht.id
            return comp
        }
    }
}