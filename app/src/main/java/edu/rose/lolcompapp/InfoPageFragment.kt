package edu.rose.lolcompapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_info_page.*

class InfoPageFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

        create_team_btn.setOnClickListener {
            val ft = supportFragmentManager.beginTransaction()
            val fragment = InfoPageFragment()
            ft.replace(R.id.fragment_container, fragment)
            ft.commit()
        }

        edit_info_btn.setOnClickListener {

        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_info_page, container, false)

        return view
    }
}