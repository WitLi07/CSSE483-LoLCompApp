package edu.rose.lolcompapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.add_comp_model.view.*

class CompPageFragment(uid: String, var team: DocumentReference, var users: ArrayList<String>) :
    Fragment() {

    private val usersList: ArrayList<String> = users
    private val uid: String = uid

    private val compRef = team
        .collection("comps")




//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        userRef.get().addOnSuccessListener {
//            for(user in users) {
//                for(doc in it.documents) {
//                    val temp = User.fromSnapshot(doc)
//                    if(user.equals(temp.id)) {
//                        usersList.add(temp)
//                    }
//                }
//            }
//        }
//    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        val view = inflater.inflate(R.layout.fragment_comp_page, container, false)
        val adapter = CompPageFragmentAdapter(context!!, uid, team)

        val recycle = view!!.findViewById(R.id.comp_recycler) as RecyclerView
        recycle.layoutManager = LinearLayoutManager(context)
        recycle.setHasFixedSize(true)
        recycle.adapter = adapter
        adapter.addSnapshotListener()
        view.findViewById<Button>(R.id.add_comp_btn).setOnClickListener {
            adapter.showAddDialog()
        }


        return view
    }

//    fun showAddDialog() {
//        val builder = AlertDialog.Builder(context!!)
//
//        val view = LayoutInflater.from(context!!).inflate(R.layout.add_comp_model, null, false)
//        builder.setView(view)
//
//        builder.setPositiveButton(android.R.string.ok) { _, _ ->
//            val newTitle = view.add_comp_edit_text.text.toString()
//            val temp = Comp(uid, newTitle, "", "", "", "", "", usersList)
//            compRef.add(temp)
//        }
//
//        builder.setNegativeButton(android.R.string.cancel, null)
//        builder.create().show()
//    }
}