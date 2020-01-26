package edu.rose.lolcompapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

class TeamPageFragment:Fragment() {

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

        return view
    }

    fun showAddDialog() {
        val builder = AlertDialog.Builder(context!!)

        val view = LayoutInflater.from(context!!).inflate(R.layout.add_teammate_model, null, false)
        builder.setView(view)

//        titleRef.get().addOnSuccessListener { snapshot: DocumentSnapshot ->
//            // prepopulate from firestore
//            val author = (snapshot["title"] ?: "") as String
//            view.edit_text.setText(author)
//        }

//        builder.setPositiveButton(android.R.string.ok) { _, _ ->
//            val newTitle = view.edit_text.text.toString() // update local title
////            titleRef.set(mapOf<String, String>(Pair("title", newTitle))) // update firestore
//        }

        builder.setNegativeButton(android.R.string.cancel, null)
        builder.create().show()
    }
}