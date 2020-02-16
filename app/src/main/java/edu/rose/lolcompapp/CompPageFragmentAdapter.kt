package edu.rose.lolcompapp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.add_comp_model.view.*

class CompPageFragmentAdapter(var context: Context, var uid: String, var team: DocumentReference) :
    RecyclerView.Adapter<CompPageFragmentViewHolder>(), AdapterView.OnItemSelectedListener, AdapterInterface {


    private var listOfComps: ArrayList<Comp> = arrayListOf()
    private val compRef = team
        .collection("comps")

    private lateinit var listenerRegistration: ListenerRegistration

    private val userRef = FirebaseFirestore
        .getInstance()
        .collection("users")

    private var users: ArrayList<User> = arrayListOf()
    lateinit var userIds: ArrayList<String>

    private var lane_user_map: HashMap<String, User> = HashMap()

    fun addSnapshotListener() {
        listenerRegistration = compRef
//            .orderBy(Comp.LAST_TOUCHED_KEY, Query.Direction.ASCENDING)
            .addSnapshotListener { querySnapshot, e ->
                if (e != null) {
                    Log.w(Constants.TAG, "listen error", e)
                } else {
                    processSnapshotChanges(querySnapshot!!)
                }
            }
    }

    private fun processSnapshotChanges(querySnapshot: QuerySnapshot) {
        for (documentChange in querySnapshot.documentChanges) {
            val comp = Comp.fromSnapshot(documentChange.document)
            when (documentChange.type) {
                DocumentChange.Type.ADDED -> {
                    Log.d(Constants.TAG, "Adding $comp")
                    listOfComps.add(0, comp)
                    notifyItemInserted(0)
                }
                DocumentChange.Type.REMOVED -> {
                    Log.d(Constants.TAG, "Removing $comp")
                    val index = listOfComps.indexOfFirst { it.uid == comp.uid }
                    listOfComps.removeAt(index)
                    notifyItemRemoved(index)
                }
                DocumentChange.Type.MODIFIED -> {
                    Log.d(Constants.TAG, "Modifying $comp")
                    val index = listOfComps.indexOfFirst { it.uid == comp.uid }
                    listOfComps[index] = comp
                    notifyItemChanged(index)
                }
            }
        }

    }

    init {
        team.get().addOnSuccessListener {
            userIds = it["users"] as ArrayList<String>
            userRef.get().addOnSuccessListener {
                for(id in userIds) {
                    for(doc in it.documents) {
                        val temp = User.fromSnapshot(doc)
                        if(id.equals(temp.uid)) {
                            users.add(temp)
                        }
                    }
                }

                for(u in users) {
                    when (u.lane) {
                        "Top" -> lane_user_map.put("Top", u)
                        "Mid" -> lane_user_map.put("Mid", u)
                        "Jg" -> lane_user_map.put("Jg", u)
                        "Sup" -> lane_user_map.put("Sup", u)
                        "Adc" -> lane_user_map.put("Adc", u)
                    }
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompPageFragmentViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.comp_cardview, parent, false)
        return CompPageFragmentViewHolder(
            view,
            this
        )
    }

    override fun getItemCount(): Int = listOfComps.size

    override fun onBindViewHolder(holder: CompPageFragmentViewHolder, position: Int) {
        holder.bind(listOfComps[position])
    }


    fun showAddDialog() {
        val builder = AlertDialog.Builder(context!!)

        val view = LayoutInflater.from(context!!).inflate(R.layout.add_comp_model, null, false)
        builder.setView(view)

        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            val newTitle = view.add_comp_edit_text.text.toString()
            Log.d(Constants.TAG, "User ids ${userIds}")

            val temp = Comp(uid, newTitle, "", "", "", "", "", userIds)
            compRef.add(temp)
        }

        builder.setNegativeButton(android.R.string.cancel, null)
        builder.show()
    }

    fun update(pos: Int) {
        val compDocRef = compRef.document(listOfComps[pos].id)
        val builder = android.app.AlertDialog.Builder(context)

        builder.setTitle("Update the Comp")

        val view = LayoutInflater.from(context).inflate(R.layout.change_comp_model, null, false)
        builder.setView(view)

        val AA0 : ArrayAdapter<String> = ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item, lane_user_map["Top"]!!.preferedChampions)
        val AA1 : ArrayAdapter<String> = ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item, lane_user_map["Mid"]!!.preferedChampions)
        val AA2 : ArrayAdapter<String> = ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item, lane_user_map["Sup"]!!.preferedChampions)
        val AA3 : ArrayAdapter<String> = ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item, lane_user_map["Adc"]!!.preferedChampions)
        val AA4 : ArrayAdapter<String> = ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item, lane_user_map["Jg"]!!.preferedChampions)

        val spin1: Spinner = view.findViewById(R.id.comp_lane_spinner_1)
        spin1.adapter = AA0
        spin1.onItemSelectedListener = this
        val spin2: Spinner = view.findViewById(R.id.comp_lane_spinner_2)
        spin2.adapter = AA1
        spin2.onItemSelectedListener = this
        val spin3: Spinner = view.findViewById(R.id.comp_lane_spinner_3)
        spin3.adapter = AA2
        spin3.onItemSelectedListener = this
        val spin4: Spinner = view.findViewById(R.id.comp_lane_spinner_4)
        spin4.adapter = AA3
        spin4.onItemSelectedListener = this
        val spin5: Spinner = view.findViewById(R.id.comp_lane_spinner_5)
        spin5.adapter = AA4
        spin5.onItemSelectedListener = this



        compDocRef.get().addOnSuccessListener { snapshot: DocumentSnapshot ->
            val top = (snapshot["top"] ?: "") as String
            val mid = (snapshot["mid"] ?: "") as String
            val jg = (snapshot["jungle"] ?: "") as String
            val sup = (snapshot["sup"] ?: "") as String
            val adc = (snapshot["ad"] ?: "") as String

            if(!top.equals("")) {
                spin1.setSelection(lane_user_map["Top"]!!.preferedChampions.indexOf(top))
            }
            if(!mid.equals("")) {
                spin2.setSelection(lane_user_map["Mid"]!!.preferedChampions.indexOf(mid))
            }
            if(!sup.equals("")) {
                spin3.setSelection(lane_user_map["Sup"]!!.preferedChampions.indexOf(sup))
            }
            if(!adc.equals("")) {
                spin4.setSelection(lane_user_map["Adc"]!!.preferedChampions.indexOf(adc))
            }
            if(!jg.equals("")) {
                spin5.setSelection(lane_user_map["Jg"]!!.preferedChampions.indexOf(jg))
            }


            builder.setPositiveButton(android.R.string.ok) { _, _ ->
                val top_mod = spin1.selectedItem.toString()
                val mid_mod = spin2.selectedItem.toString()
                val sup_mod = spin3.selectedItem.toString()
                val adc_mod = spin4.selectedItem.toString()
                val jg_mod = spin5.selectedItem.toString()

                compDocRef.set(Comp(uid, listOfComps[pos].name, top_mod, mid_mod, sup_mod, adc_mod, jg_mod))
            }

            builder.setNegativeButton(android.R.string.cancel, null)
            builder.create().show()

        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    override fun remove(pos: Int) {
        compRef.document(listOfComps[pos].id).delete()
    }

}