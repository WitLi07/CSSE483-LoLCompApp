package edu.rose.lolcompapp

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import edu.rose.lolcompapp.Constants.TAG
import kotlinx.android.synthetic.main.add_teammate_model.view.*
import kotlinx.android.synthetic.main.fragment_team_page.*
import kotlinx.android.synthetic.main.fragment_team_page.view.*
import org.w3c.dom.Text

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
    private lateinit var listenerRegistration: ListenerRegistration
    fun addSnapshotListener() {
        listenerRegistration = playerInfoRef
            .addSnapshotListener { querySnapshot, e ->
                if (e != null) {
                    Log.w(Constants.TAG, "listen error", e)
                } else {
                    processSnapshotChanges(querySnapshot!!)
                }
            }
    }

    private fun processSnapshotChanges(querySnapshot: QuerySnapshot) {
        updateUI()

    }

    private fun updateUI() {
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
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        updateUI()
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
        return view
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
                        team!!.add(User.fromSnapshot(snp).uid)
                        teamRef.update("users", team)
                    }
                }.addOnFailureListener {
                    Log.d(TAG, "Cant find the player")
                }
            updateUI()
        }

        builder.setNegativeButton(android.R.string.cancel){dialog, which ->
            updateUI()
        }
        builder.create().show()
    }
}