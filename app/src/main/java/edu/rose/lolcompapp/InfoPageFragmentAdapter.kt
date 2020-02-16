package edu.rose.lolcompapp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import edu.rose.lolcompapp.Constants.TAG
import io.karn.notify.Notify
import kotlinx.android.parcel.RawValue

class InfoPageFragmentAdapter(
    var context: Context,
    var uid: String,
    var listener: InfoPageFragment.OnTeamSelectedListener?
) : RecyclerView.Adapter<InfoPageFragmentViewHolder>(), AdapterInterface {

    private var listOfTeams: ArrayList<Team> = arrayListOf()
    private lateinit var listOfTeamsRef: ArrayList<DocumentReference>
    private val userRef = FirebaseFirestore
        .getInstance()
        .collection("users")
        .document(uid)
    private val teamRef = FirebaseFirestore
        .getInstance()
        .collection("teams")
    private var teamLength: Int = 0

    init {
        userRef.get().addOnSuccessListener {
            teamLength = (it["teams"] as ArrayList<DocumentReference>).size
        }
        userRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            val source = if (snapshot != null && snapshot.metadata.hasPendingWrites())
                "Local"
            else
                "Server"

            if (snapshot != null && snapshot.exists()) {
//                Log.d(TAG, "$source data: ${snapshot.data}")
                if (snapshot["teams"] != null) {
                    listOfTeamsRef = snapshot["teams"] as ArrayList<DocumentReference>
                    if (teamLength != listOfTeamsRef.size) {
                        teamLength = listOfTeamsRef.size
                        notifyForTeamAdded()
                    }
                    listOfTeams.clear()
                    for (teamRef in listOfTeamsRef) {
                        teamRef.get().addOnSuccessListener {
                            listOfTeams.add(Team.fromSnapshot(it))
                            notifyItemInserted(listOfTeams.size)
                        }
                    }
                }
            } else {
//                Log.d(TAG, "$source data: null")
            }
        }
    }

    private fun notifyForTeamAdded() {
        Notify
            .with(context!!)
            .content {
                // this: Payload.Content.Default
                title = "Your team page has changed"
                text = "Check you info page"
            }
            .show()
    }

    fun selectTeamAt(adapterPosition: Int) {
        var team = listOfTeams[adapterPosition]
        var teamRef = listOfTeamsRef[adapterPosition]
        listener?.onTeamSelected(team, teamRef)
    }

    override fun remove(adapterPosition: Int) {
        userRef.get().addOnSuccessListener {
            val teamRefs = it["teams"] as ArrayList<DocumentReference>
            val removedTeam = teamRefs.removeAt(adapterPosition)

            userRef.set(
                User(
                    uid!!,
                    it["gamename"] as String,
                    it["lane"] as String,
                    it["preferedChampions"] as ArrayList<String>,
                    teamRefs
                )
            )

            removedTeam.get().addOnSuccessListener {
                val userRemovedList = (it["users"] as ArrayList<String>)
                userRemovedList.remove(uid)
                if (userRemovedList.size == 0)
                    removedTeam.delete()
                else {
                    removedTeam.set(
                        Team(
                            it["uid"] as String,
                            userRemovedList,
                            removedTeam
                        )
                    )
                }

            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoPageFragmentViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.info_page_fragment_cardview, parent, false)
        return InfoPageFragmentViewHolder(
            view,
            this
        )
    }

    override fun getItemCount(): Int = listOfTeams.size

    override fun onBindViewHolder(holder: InfoPageFragmentViewHolder, position: Int) {
        holder.bind(listOfTeams[position])
    }
}