package edu.rose.lolcompapp

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.Spinner
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.lukle.clickableareasimage.ClickableArea
import at.lukle.clickableareasimage.ClickableAreasImage
import at.lukle.clickableareasimage.OnClickableAreaClickedListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import edu.rose.lolcompapp.Constants.TAG
import kotlinx.android.parcel.RawValue
import kotlinx.android.synthetic.main.change_info_model.view.*
import kotlinx.android.synthetic.main.change_lane_info_map_model.view.*
import uk.co.senab.photoview.PhotoViewAttacher


private const val ARG_UID = "UID"

class InfoPageFragment(context: Context) : Fragment(), AdapterView.OnItemSelectedListener,
    OnClickableAreaClickedListener<Lane> {
    interface OnTeamSelectedListener {
        fun onTeamSelected(team: Team, teamRef: DocumentReference)
    }

    private var uid: String? = null
    private var rootView: View? = null
    private var listener: OnTeamSelectedListener? = null
    private var mapTitle: TextView? = null
    private var mapTitleLane: String? = null


    val playerInfoRef = FirebaseFirestore
        .getInstance()
        .collection("users")

    private val storageRef = FirebaseStorage.getInstance()
        .reference
        .child("champImages")

    private val mapStorageRef = FirebaseStorage.getInstance()
        .reference
        .child("lolMap")


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
        val callback: ItemTouchHelper.Callback = SimpleItemTouchHelperCallback(adapter, rootView!!)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(RV)
        RV.adapter = adapter


        rootView!!.findViewById<Button>(R.id.create_team_btn).setOnClickListener {
            val teamRef = FirebaseFirestore
                .getInstance()
                .collection("teams")
                .add(Team(uid!!, arrayListOf(uid!!)))
                .addOnCompleteListener {
                    val that = it

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
                            it["users"] as ArrayList<String>
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

        attachSnapshotListener()

        return rootView
    }

    private fun attachSnapshotListener() {
        val playerInfoDocRef = playerInfoRef.document(uid!!)

        playerInfoDocRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d(TAG, "Current data: ${snapshot.data}")

                updateUI(snapshot)
            } else {
                Log.d(TAG, "Current data: null")
            }
        }
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

    private fun updateUI(snapshot: DocumentSnapshot) {
        if (rootView!! == null)
            return
        clearScreen()
        val gameName = (snapshot["gamename"] ?: "") as String
        val lane = (snapshot["lane"] ?: "") as String
        val champs =
            (snapshot["preferedChampions"] ?: arrayListOf<String>()) as ArrayList<String>
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

            storageRef.child(name + ".png").downloadUrl.addOnCompleteListener {
                val url = it.result
                Picasso.get().load(url).into(img)
            }
        }

    }

    private fun clearScreen() {
        rootView!!.findViewById<TextView>(R.id.gamename_value).text = "unknown"
        rootView!!.findViewById<TextView>(R.id.lane_value).text = "unknown"
        for (i in 1..4) {
            var imgId: String = "cham" + (i)
            val img: ImageView = rootView!!.findViewById(
                resources.getIdentifier(
                    imgId,
                    "id",
                    activity!!.packageName
                )
            )
            img.setImageResource(R.drawable.question_mark_icon)
        }
    }


    fun showEditDialog() {
        val playerInfoDocRef = playerInfoRef.document(uid!!)

        val builder = AlertDialog.Builder(context!!)

        val view = LayoutInflater.from(context!!)
            .inflate(R.layout.change_info_model, null, false)
        builder.setView(view)

        val changeLaneButton = view.findViewById<Button>(R.id.lane_edit_btn)

        var teams: ArrayList<@RawValue DocumentReference>? = null
        playerInfoDocRef.get().addOnSuccessListener { snapshot: DocumentSnapshot ->


            val gameName = (snapshot["gamename"] ?: "") as String
            val lane = (snapshot["lane"] ?: "") as String
            val preferedChampion = (snapshot["preferedChampions"] ?: "") as ArrayList<String>
            teams = (snapshot["teams"] ?: "") as ArrayList<DocumentReference>

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

        changeLaneButton.setOnClickListener {
            mapStorageRef.child("lolMap.png").downloadUrl.addOnCompleteListener {
                val builder1 = AlertDialog.Builder(context!!)

                val mapView = LayoutInflater.from(context!!)
                    .inflate(R.layout.change_lane_info_map_model, null, false)
                builder1.setView(mapView)

                val url = it.result
                val that = this
                val image = mapView.change_lane_imageView
                mapTitle = mapView.change_lane_title
                Picasso.get().load(url).into(image, object : Callback {
                    override fun onSuccess() {

                        val clickableAreasImage =
                            ClickableAreasImage(PhotoViewAttacher(image), that)
                        val clickableAreas: MutableList<ClickableArea<*>> = ArrayList()
                        clickableAreas.add(
                            ClickableArea<Any?>(
                                76,
                                69,
                                25,
                                25,
                                Lane("Top")
                            )
                        )
                        clickableAreas.add(
                            ClickableArea<Any?>(
                                175,
                                155,
                                25,
                                25,
                                Lane("Mid")
                            )
                        )
                        clickableAreas.add(
                            ClickableArea<Any?>(
                                182,
                                236,
                                25,
                                25,
                                Lane("Jg")
                            )
                        )
                        clickableAreas.add(
                            ClickableArea<Any?>(
                                317,
                                242,
                                25,
                                25,
                                Lane("Sup")
                            )
                        )
                        clickableAreas.add(
                            ClickableArea<Any?>(
                                281,
                                256,
                                25,
                                25,
                                Lane("Adc")
                            )
                        )
                        clickableAreasImage.setClickableAreas(clickableAreas)


                    }

                    override fun onError(e: Exception?) {

                    }
                })
                builder1.setNegativeButton(android.R.string.cancel, null)
                builder1.setPositiveButton(android.R.string.ok) { _, _ ->
                    view.lane_edit_text.setText(mapTitleLane)
                }
                builder1.show()

            }
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


            playerInfoDocRef.set(User(uid!!, gameName, lane, prefChampsArray, teams!!))

            rootView!!.findViewById<TextView>(R.id.gamename_value).text = gameName
            rootView!!.findViewById<TextView>(R.id.lane_value).text = lane

        }

        builder.setNegativeButton(android.R.string.cancel, null)
        builder.create().show()

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

    }


    override fun onClickableAreaTouched(item: Lane?) {
        if (item is Lane) {
            mapTitleLane = item.lane
            mapTitle!!.text = "Lane Selected: $mapTitleLane"
        }
    }

}
