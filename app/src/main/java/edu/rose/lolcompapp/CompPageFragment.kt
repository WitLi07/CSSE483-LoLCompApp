package edu.rose.lolcompapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class CompPageFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_comp_page, container, false)

//        view.findViewById<Button>(R.id.view_comp_btn).setOnClickListener {
//
//            val ft = activity?.supportFragmentManager?.beginTransaction()
//            val fragment = CompPageFragment()
//            ft?.replace(R.id.fragment_container, fragment)
//            ft?.addToBackStack("team")
//            ft?.commit()
//        }


        return view
    }
}