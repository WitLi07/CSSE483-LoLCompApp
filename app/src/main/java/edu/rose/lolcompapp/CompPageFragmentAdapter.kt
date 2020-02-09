package edu.rose.lolcompapp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.add_comp_model.view.*

class CompPageFragmentAdapter(var context: Context, var uid: String, var team: DocumentReference) :
    RecyclerView.Adapter<CompPageFragmentViewHolder>() {

    private var listOfComps: ArrayList<Comp> = arrayListOf()
    private val compRef = team
        .collection("comps")

    private lateinit var listenerRegistration: ListenerRegistration

    private val userRef = FirebaseFirestore
        .getInstance()
        .collection("users")

    private var users: ArrayList<User> = arrayListOf()
    lateinit var userIds: ArrayList<String>

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
        // Snapshots has documents and documentChanges which are flagged by type,
        // so we can handle C,U,D differently.
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

//        team.get().addOnSuccessListener {
//            userIds = it["users"] as ArrayList<String>
//            userRef.get().addOnSuccessListener {
//                for(id in userIds) {
//                    for(doc in it.documents) {
//                        val temp = User.fromSnapshot(doc)
//                        if(id.equals(temp.id)) {
//                            users.add(temp)
//                        }
//                    }
//                }
//            }
//        }
    }

    init {
//        compRef.get().addOnSuccessListener {
//            for (doc in it.documents) {
//                val comp = Comp.fromSnapshot(doc)
//                listOfComps.add(comp)
//            }
//        }
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
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompPageFragmentViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.comp_cardview, parent, false)
        return CompPageFragmentViewHolder(
            view,
            this,
            context,
            users
        )
    }

    override fun getItemCount(): Int = listOfComps.size

    override fun onBindViewHolder(holder: CompPageFragmentViewHolder, position: Int) {
        holder.bind(listOfComps[position])
    }

    fun edit(position: Int, comp: Comp) {
        listOfComps[position] = comp
        val temp = compRef.document(listOfComps[position].uid)
        temp.set(listOfComps[position])
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

    fun update() {

    }
}