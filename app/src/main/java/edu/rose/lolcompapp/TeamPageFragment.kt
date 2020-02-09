package edu.rose.lolcompapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import edu.rose.lolcompapp.Constants.TAG
import kotlinx.android.synthetic.main.add_teammate_model.view.*
import kotlinx.android.synthetic.main.fragment_team_page.view.*

private const val ARG_UID = "UID"
private const val ARG_TEAM = "TEAM"

class TeamPageFragment(
    var uid: String,
    var teamRef: DocumentReference,
    var team: ArrayList<String>
) : Fragment() {
    val playerInfoRef = FirebaseFirestore
        .getInstance()
        .collection("users")

    private val storageRef = FirebaseStorage.getInstance()
        .reference
        .child("champImages")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_team_page, container, false)

        view.findViewById<Button>(R.id.view_comp_btn).setOnClickListener {

            val ft = activity?.supportFragmentManager?.beginTransaction()
            val fragment = CompPageFragment()
            ft?.replace(R.id.fragment_container, fragment)
            ft?.addToBackStack("team")
            ft?.commit()
        }

        view.findViewById<Button>(R.id.add_teammate_btn).setOnClickListener {
            showAddDialog()
        }

        playerInfoRef.document(uid).get().addOnSuccessListener {
            view.team_page_name_1.text = it["gamename"] as String
            view.team_page_lane_1.text = it["lane"] as String
            val champs = it["preferedChampions"] as ArrayList<String>


            for ((i, name) in champs.withIndex()) {
                var imgId: String = "team_page_image_view_1_" + (i + 1)
                val img: ImageView = view.findViewById(
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
        }

        builder.setNegativeButton(android.R.string.cancel, null)
        builder.create().show()
    }
}