package edu.rose.lolcompapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_info_page.*

class InfoPageFragment(context: Context) : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_info_page, container, false)

        view.findViewById<Button>(R.id.create_team_btn).setOnClickListener {

            val ft = activity?.supportFragmentManager?.beginTransaction()
            val fragment = TeamPageFragment()
            ft?.replace(R.id.fragment_container, fragment)
            ft?.addToBackStack("info")
            ft?.commit()
        }

        view.findViewById<Button>(R.id.edit_info_btn).setOnClickListener {
            showEditDialog()
        }


        return view
    }

    fun showEditDialog() {
//        val titleRef = FirebaseFirestore
//            .getInstance()
//            .collection("settings")
//            .document("settings")

        val builder = AlertDialog.Builder(context!!)

        val view = LayoutInflater.from(context!!).inflate(R.layout.change_info_model, null, false)
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