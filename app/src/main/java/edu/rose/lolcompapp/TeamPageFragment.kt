package edu.rose.lolcompapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import edu.rose.lolcompapp.Constants.TAG
import kotlinx.android.synthetic.main.add_teammate_model.view.*
import android.content.Intent
import android.net.Uri
import android.widget.*


private const val ARG_UID = "UID"

class TeamPageFragment(
    var uid: String,
    var teamRef: DocumentReference,
    var team: ArrayList<String>
) : Fragment() {

    lateinit var rootView: View
    val playerInfoRef = FirebaseFirestore
        .getInstance()
        .collection("users")

    private val storageRef = FirebaseStorage.getInstance()
        .reference
        .child("champImages")

    companion object {
        @JvmStatic
        fun newInstance(
            uid: String,
            teamRef: DocumentReference,
            team: ArrayList<String>
        ) =
            TeamPageFragment(uid, teamRef, team).apply {
                arguments = Bundle().apply {
                    putString(ARG_UID, uid)
                }
            }
    }

    private fun updateUI() {
        if (context == null || rootView == null) {
            return
        }
        clearScreen()
        for ((j, player) in team.withIndex()) {
            var index = j + 1

            playerInfoRef.document(team[j]).get().addOnSuccessListener {
                var gameId: String = "team_page_name_$index"

                rootView.findViewById<TextView>(
                    resources.getIdentifier(
                        gameId,
                        "id",
                        activity?.packageName
                    )
                ).text = it["gamename"] as String

                var laneId: String = "team_page_lane_$index"
                rootView.findViewById<TextView>(
                    resources.getIdentifier(
                        laneId,
                        "id",
                        activity?.packageName
                    )
                ).text = it["lane"] as String


                val champs = it["preferedChampions"] as ArrayList<String>

                for ((i, name) in champs.withIndex()) {
                    var imgId: String = "team_page_image_view_${index}_" + (i + 1)
                    val img: ImageView = rootView.findViewById(
                        resources.getIdentifier(
                            imgId,
                            "id",
                            activity?.packageName
                        )
                    )
                    storageRef.child(name + ".png").downloadUrl.addOnCompleteListener {
                        val url = it.result
                        Picasso.get().load(url).into(img)
                    }
                }

                val opGGLink = "team_page_link_text_view_$index"
                val linkTextView = rootView.findViewById<TextView>(
                    resources.getIdentifier(
                        opGGLink,
                        "id",
                        activity?.packageName
                    )
                )

                val that = it
                linkTextView.movementMethod
                linkTextView.setOnClickListener {
                    val browserIntent = Intent(Intent.ACTION_VIEW)
                    browserIntent.data =
                        Uri.parse("https://na.op.gg/summoner/userName=${that["gamename"] as String}")
                    startActivity(browserIntent)
                }
            }
        }
    }

    private fun clearScreen() {
        for (index in 1..5) {
            var gameId: String = "team_page_name_$index"

            rootView.findViewById<TextView>(
                resources.getIdentifier(
                    gameId,
                    "id",
                    activity?.packageName
                )
            ).text = "No User added"

            var laneId: String = "team_page_lane_$index"
            rootView.findViewById<TextView>(
                resources.getIdentifier(
                    laneId,
                    "id",
                    activity?.packageName
                )
            ).text = "No User added"

            storageRef.child(".png").downloadUrl.addOnCompleteListener {
                val url = it.result

                for (jndex in 1..4) {
                    var imgId: String = "team_page_image_view_${index}_" + (jndex)
                    val img: ImageView = rootView.findViewById(
                        resources.getIdentifier(
                            imgId,
                            "id",
                            activity?.packageName
                        )
                    )
                    Picasso.get().load(url).into(img)
                }
            }

            val opGGLink = "team_page_link_text_view_$index"
            val linkTextView = rootView.findViewById<TextView>(
                resources.getIdentifier(
                    opGGLink,
                    "id",
                    activity?.packageName
                )
            )

            linkTextView.movementMethod
            linkTextView.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW)
                browserIntent.data =
                    Uri.parse("https://na.op.gg/summoner/userName=Unknown")
                startActivity(browserIntent)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_team_page, container, false)
        rootView = view
        view.findViewById<Button>(R.id.view_comp_btn).setOnClickListener {

            teamRef.get().addOnSuccessListener { snapshot:DocumentSnapshot ->
                val teammates = snapshot["users"] as ArrayList<User>
                if(teammates.size == 5) {
                    val ft = activity?.supportFragmentManager?.beginTransaction()
                    val fragment = CompPageFragment(uid, teamRef, team)
                    ft?.replace(R.id.fragment_container, fragment)
                    ft?.addToBackStack("team")
                    ft?.commit()
                } else {
                    Toast.makeText(context, "Currently, there are no enough players to form a comp", Toast.LENGTH_LONG).show()
                }
            }


        }

        view.findViewById<Button>(R.id.add_teammate_btn).setOnClickListener {
            showAddDialog()
        }

        attachSnapshotListener()
        return view
    }

    private fun attachSnapshotListener() {
        teamRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {

                team.clear()
                team.addAll(snapshot!!["users"] as ArrayList<String>)
                updateUI()
            } else {
            }
        }
    }


    fun showAddDialog() {
        val builder = AlertDialog.Builder(context!!)

        val view = LayoutInflater.from(context!!).inflate(R.layout.add_teammate_model, null, false)
        builder.setView(view)


        builder.setPositiveButton(android.R.string.ok) { _, _ ->

            val name = view.add_teammate_edit_text.text
            Log.d(TAG, "$name")
            playerInfoRef.whereEqualTo("gamename", name.toString()).get()
                .addOnSuccessListener {
                    var addedUser: String? = null
                    for (snp in it) {
                        addedUser = User.fromSnapshot(snp).uid
                        team!!.add(addedUser)
                        teamRef.update("users", team)
                    }
                    Log.d(TAG, "$addedUser")

                    playerInfoRef.document(addedUser!!).get()
                        .addOnSuccessListener {
                            var teamRefList = it["teams"] as ArrayList<DocumentReference>
                            teamRefList.add(
                                FirebaseFirestore
                                    .getInstance()
                                    .collection("teams")
                                    .document(teamRef.id)
                            )
                            playerInfoRef.document(addedUser!!).set(
                                User(
                                    it["uid"] as String,
                                    it["gamename"] as String,
                                    it["lane"] as String,
                                    it["preferedChampions"] as ArrayList<String>,
                                    teamRefList
                                )
                            )
                        }
                }.addOnFailureListener {
                }
        }

        builder.setNegativeButton(android.R.string.cancel, null)
        builder.create().show()
    }
}