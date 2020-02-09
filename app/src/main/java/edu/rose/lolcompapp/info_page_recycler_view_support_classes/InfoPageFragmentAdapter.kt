package edu.rose.lolcompapp.info_page_recycler_view_support_classes

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import edu.rose.lolcompapp.InfoPageFragment
import edu.rose.lolcompapp.Team

class InfoPageFragmentAdapter(
    var uid: String,
    var listener: InfoPageFragment.OnTeamSelectedListener?
) : RecyclerView.Adapter<InfoPageFragmentViewHolder>() {

    private var listOfTeams = ArrayList<Team>()
    private val teamRef = FirebaseFirestore
        .getInstance()
        .collection("user")
        .document(uid)

    init {
        var listOfTeamsRef: ArrayList<DocumentReference> = arrayListOf()
        teamRef.get().addOnSuccessListener {
            listOfTeamsRef = it["teams"] as ArrayList<DocumentReference>
        }
        for (teamRef in listOfTeamsRef) {
            teamRef.get().addOnSuccessListener {
                listOfTeams.add(Team.fromSnapshot(it))
            }
        }

    }

    fun selectPicAt(adapterPosition: Int) {
        var team = listOfTeams[adapterPosition]
        listener?.onTeamSelected(team)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoPageFragmentViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: InfoPageFragmentViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}