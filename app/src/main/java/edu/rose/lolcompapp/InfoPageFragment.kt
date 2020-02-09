package edu.rose.lolcompapp

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import edu.rose.lolcompapp.Constants.TAG
import kotlinx.android.synthetic.main.change_info_model.view.*
import kotlinx.android.synthetic.main.fragment_info_page.view.*


private const val ARG_UID = "UID"

class InfoPageFragment(context: Context) : Fragment(), AdapterView.OnItemSelectedListener {
    interface OnTeamSelectedListener {
        fun onTeamSelected(team: Team, teamRef: DocumentReference)
    }

    private var uid: String? = null
    private var rootView: View? = null
    private var listener: OnTeamSelectedListener? = null


    val playerInfoRef = FirebaseFirestore
        .getInstance()
        .collection("users")

    private val storageRef = FirebaseStorage.getInstance()
        .reference
        .child("champImages")


    companion object {
        @JvmStatic
        fun newInstance(
            context: Context,
            uid: String
        ): InfoPageFragment {
            return InfoPageFragment(context).apply {
                arguments = Bundle().apply {
                    putString(ARG_UID, uid)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            uid = it.getString(ARG_UID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        rootView = inflater.inflate(R.layout.fragment_info_page, container, false)


        val RV = rootView!!.findViewById(R.id.info_recycler) as RecyclerView
        val adapter = InfoPageFragmentAdapter(context!!, uid!!, listener!!)
        RV.layoutManager = LinearLayoutManager(context)
        RV.setHasFixedSize(true)
        RV.adapter = adapter
//        Log.d(Constants.TAG, "uid : ${adapter}")


        rootView!!.findViewById<Button>(R.id.create_team_btn).setOnClickListener {
            val teamRef = FirebaseFirestore
                .getInstance()
                .collection("teams")
                .add(Team(uid!!, arrayListOf("$uid")))
                .addOnCompleteListener {
                    val that = it
//                    it.result?.get()!!.addOnSuccessListener {
//                        it["myRef"] = that.toString()
//                    }
                    playerInfoRef.document(uid!!).get().addOnSuccessListener {
                        val teamRefList = it["teams"] as ArrayList<DocumentReference>
                        teamRefList.add(that.result!!)
                        playerInfoRef.document(uid!!).set(
                            User(
                                uid!!,
                                it["gamename"] as String,
                                it["lane"] as String,
                                it["preferedChampions"] as ArrayList<String>,
                                teamRefList
                            )
                        )

                    }

                    it.result!!.get().addOnSuccessListener {
                        val ft = activity?.supportFragmentManager?.beginTransaction()
                        val fragment = TeamPageFragment(
                            uid!!,
                            that.result!!,
                            it["users"] as ArrayList<User>
                        )
                        ft?.replace(R.id.fragment_container, fragment)
                        ft?.addToBackStack("team")
                        ft?.commit()
                    }

                }
        }

        rootView!!.findViewById<Button>(R.id.edit_info_btn).setOnClickListener {
            showEditDialog()
        }
        updateUI()
        return rootView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnTeamSelectedListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    fun updateUI() {
        val playerInfoDocRef = playerInfoRef.document(uid!!)
        playerInfoDocRef.get().addOnSuccessListener { snapshot: DocumentSnapshot ->

            val gameName = (snapshot["gamename"] ?: "") as String
            val lane = (snapshot["lane"] ?: "") as String
            val champs = snapshot["preferedChampions"] as ArrayList<String>
            rootView!!.findViewById<TextView>(R.id.gamename_value).text = gameName
            rootView!!.findViewById<TextView>(R.id.lane_value).text = lane
            for ((i, name) in champs.withIndex()) {
                var imgId: String = "cham" + (i + 1)
                val img: ImageView = rootView!!.findViewById(
                    resources.getIdentifier(
                        imgId,
                        "id",
                        activity!!.packageName
                    )
                )
//                Picasso.get().load(storageRef.child(name).downloadUrl.toString()).into(img)
//                Picasso.get().load(storageRef.child("Jax.png").downloadUrl.result).into(img)
                storageRef.child(name + ".png").downloadUrl.addOnCompleteListener {
                    val url = it.result
                    Picasso.get().load(url).into(img)
                }
            }
        }
    }


    fun showEditDialog() {
        val playerInfoDocRef = playerInfoRef.document(uid!!)

        val builder = AlertDialog.Builder(context!!)

        val view = LayoutInflater.from(context!!)
            .inflate(R.layout.change_info_model, null, false)
        builder.setView(view)

        playerInfoDocRef.get().addOnSuccessListener { snapshot: DocumentSnapshot ->


            val gameName = (snapshot["gamename"] ?: "") as String
            val lane = (snapshot["lane"] ?: "") as String
            val preferedChampion = (snapshot["preferedChampions"] ?: "") as ArrayList<String>
            val teams = (snapshot["teams"] ?: "") as ArrayList<DocumentReference>

            Log.d(Constants.TAG, "uid : ${uid}")
            Log.d(Constants.TAG, "gamename : ${gameName}")
            Log.d(Constants.TAG, "lane : ${lane}")
            Log.d(Constants.TAG, "champions : ${preferedChampion.toString()}")
            Log.d(Constants.TAG, "teams : ${teams}")

            view.in_game_username_edit_text.setText(gameName)
            view.lane_edit_text.setText(lane)

            var cham_arr = resources.getStringArray(R.array.champion_name)
            view.champion_image_view_1.setSelection(cham_arr.indexOf(preferedChampion[0]))
            view.champion_image_view_2.setSelection(cham_arr.indexOf(preferedChampion[1]))
            view.champion_image_view_3.setSelection(cham_arr.indexOf(preferedChampion[2]))
            view.champion_image_view_4.setSelection(cham_arr.indexOf(preferedChampion[3]))

        }

        val spinner1: Spinner = view.findViewById(R.id.champion_image_view_1)
        spinner1.onItemSelectedListener = this
        val spinner2: Spinner = view.findViewById(R.id.champion_image_view_2)
        spinner1.onItemSelectedListener = this
        val spinner3: Spinner = view.findViewById(R.id.champion_image_view_3)
        spinner1.onItemSelectedListener = this
        val spinner4: Spinner = view.findViewById(R.id.champion_image_view_4)
        spinner1.onItemSelectedListener = this

        ArrayAdapter.createFromResource(
            context as Context,
            R.array.champion_name,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner

            spinner1.adapter = adapter
            spinner2.adapter = adapter
            spinner3.adapter = adapter
            spinner4.adapter = adapter


        }

        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            val gameName = view.in_game_username_edit_text.text.toString()
            val lane = view.lane_edit_text.text.toString()
            val prefChamp1 = spinner1.selectedItem.toString()
            val prefChamp2 = spinner2.selectedItem.toString()
            val prefChamp3 = spinner3.selectedItem.toString()
            val prefChamp4 = spinner4.selectedItem.toString()
            val prefChampsArray =
                arrayListOf<String>(prefChamp1, prefChamp2, prefChamp3, prefChamp4)


            playerInfoDocRef.set(User(uid!!, gameName, lane, prefChampsArray))

            rootView!!.findViewById<TextView>(R.id.gamename_value).text = gameName
            rootView!!.findViewById<TextView>(R.id.lane_value).text = lane

            updateUI()

        }

        builder.setNegativeButton(android.R.string.cancel, null)
        builder.create().show()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

    }

}
