package edu.rose.lolcompapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentReference

class CompPageFragment(uid: String, var team: DocumentReference, var users: ArrayList<String>) :
    Fragment() {

    private val uid: String = uid


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_comp_page, container, false)
        val adapter = CompPageFragmentAdapter(context!!, uid, team)

        val recycle = view!!.findViewById(R.id.comp_recycler) as RecyclerView
        recycle.layoutManager = LinearLayoutManager(context)
        recycle.setHasFixedSize(true)

        val callback: ItemTouchHelper.Callback = SimpleItemTouchHelperCallback(adapter, view!!)
        val touchHelper = ItemTouchHelper(callback)

        touchHelper.attachToRecyclerView(recycle)

        recycle.adapter = adapter
        adapter.addSnapshotListener()
        view.findViewById<Button>(R.id.add_comp_btn).setOnClickListener {
            adapter.showAddDialog()
        }

        return view
    }
}