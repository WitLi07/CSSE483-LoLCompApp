package edu.rose.lolcompapp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*

class CompPageFragmentAdapter(var context: Context, var uid: String, var team: DocumentReference) :
    RecyclerView.Adapter<CompPageFragmentViewHolder>() {

    private var listOfComps: ArrayList<Comp> = arrayListOf()
    private val compRef = team
        .collection("comps")

    private lateinit var listenerRegistration: ListenerRegistration

    fun addSnapshotListener() {
        listenerRegistration = compRef
            .orderBy(Comp.LAST_TOUCHED_KEY, Query.Direction.ASCENDING)
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
                    val index = listOfComps.indexOfFirst { it.id == comp.id }
                    listOfComps.removeAt(index)
                    notifyItemRemoved(index)
                }
                DocumentChange.Type.MODIFIED -> {
                    Log.d(Constants.TAG, "Modifying $comp")
                    val index = listOfComps.indexOfFirst { it.id == comp.id }
                    listOfComps[index] = comp
                    notifyItemChanged(index)
                }
            }
        }
    }

    init {
        compRef.get().addOnSuccessListener {
            for(doc in it.documents) {
                val comp = Comp.fromSnapshot(doc)
                listOfComps.add(comp)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompPageFragmentViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.comp_cardview, parent, false)
        return CompPageFragmentViewHolder(
            view,
            this,
            context
        )
    }

    override fun getItemCount(): Int = listOfComps.size

    override fun onBindViewHolder(holder: CompPageFragmentViewHolder, position: Int) {
        holder.bind(listOfComps[position])
    }

    fun edit(position: Int, comp: Comp) {
        listOfComps[position] = comp
        compRef.document(listOfComps[position].id).set(comp)
    }
}