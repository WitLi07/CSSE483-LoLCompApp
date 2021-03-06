package edu.rose.lolcompapp

import android.os.Parcelable
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class Team(
    var uid: String = "",
    var users: ArrayList<String> = arrayListOf(),
    var myRef: @RawValue DocumentReference? = null
) : Parcelable {
    @get:Exclude
    @IgnoredOnParcel
    var id: String = ""


    companion object {
        const val LAST_TOUCHED_KEY = "lastTouched"
        const val UID_KEY = "uid"
        fun fromSnapshot(snapsht: DocumentSnapshot): Team {
            val team = snapsht.toObject(Team::class.java)
            team?.id = snapsht.id
            return team!!
        }
    }
}