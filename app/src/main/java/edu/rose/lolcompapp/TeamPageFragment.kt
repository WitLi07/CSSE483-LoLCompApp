package edu.rose.lolcompapp

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import edu.rose.lolcompapp.Constants.TAG
import kotlinx.android.synthetic.main.add_teammate_model.view.*
import kotlinx.android.synthetic.main.fragment_info_page.*
import kotlinx.android.synthetic.main.fragment_team_page.*
import kotlinx.android.synthetic.main.fragment_team_page.view.*
import org.w3c.dom.Text
import android.content.Intent
import android.net.Uri
import android.widget.*


private const val ARG_UID = "UID"
private const val ARG_TEAM = "TEAM"

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
            Log.w(TAG, "updateUI failed, context = null")
            return
        }
        clearScreen()
        for ((j, player) in team.withIndex()) {
            Log.w(TAG, "updating ${team[j]}")
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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_team_page, container, false)
        rootView = view
        view.findViewById<Button>(R.id.view_comp_btn).setOnClickListener {

            val ft = activity?.supportFragmentManager?.beginTransaction()
            val fragment = CompPageFragment(uid, teamRef, team)
            ft?.replace(R.id.fragment_container, fragment)
            ft?.addToBackStack("team")
            ft?.commit()
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
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d(TAG, "Current data: ${snapshot.data}")

                team.clear()
                team.addAll(snapshot!!["users"] as ArrayList<String>)
                updateUI()
            } else {
                Log.d(TAG, "Current data: null")
            }
        }
    }


    fun showAddDialog() {
        val builder = AlertDialog.Builder(context!!)

        val view = LayoutInflater.from(context!!).inflate(R.layout.add_teammate_model, null, false)
        builder.setView(view)


        builder.setPositiveButton(android.R.string.ok) { _, _ ->

            val name = view.add_teammate_edit_text.text
            playerInfoRef.whereEqualTo("gamename", name.toString()).get()
                .addOnSuccessListener {
                    for (snp in it) {
                        Log.d(TAG, "Added")
                        team!!.add(User.fromSnapshot(snp).uid)
                        teamRef.update("users", team)
                    }
//                    updateUI()
                }.addOnFailureListener {
                    Log.d(TAG, "Cant find the player")
                }
        }

        builder.setNegativeButton(android.R.string.cancel, null)
        builder.create().show()
    }
}