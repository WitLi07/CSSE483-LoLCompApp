package edu.rose.lolcompapp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import edu.rose.lolcompapp.Constants.TAG

class InfoPageFragmentAdapter(
    var context: Context,
    var uid: String,
    var listener: InfoPageFragment.OnTeamSelectedListener?
) : RecyclerView.Adapter<InfoPageFragmentViewHolder>() {

    private var listOfTeams: ArrayList<Team> = arrayListOf()
    private lateinit var listOfTeamsRef: ArrayList<DocumentReference>
    private val teamRef = FirebaseFirestore
        .getInstance()
        .collection("users")
        .document(uid)

    init {
        teamRef.get().addOnSuccessListener {

            listOfTeamsRef = it["teams"] as ArrayList<DocumentReference>

            for (teamRef in listOfTeamsRef) {
                teamRef.get().addOnSuccessListener {
                    listOfTeams.add(Team.fromSnapshot(it))
                    notifyItemInserted(listOfTeams.size)
                }
            }
//            notifyDataSetChanged()
        }
    }

    fun selectTeamAt(adapterPosition: Int) {
        var team = listOfTeams[adapterPosition]
        var teamRef = listOfTeamsRef[adapterPosition]
        listener?.onTeamSelected(team, teamRef)
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