package edu.rose.lolcompapp

import android.app.Activity
import android.view.View
import android.widget.AdapterView

class Spinner : Activity(), AdapterView.OnItemSelectedListener {

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
         parent.getItemAtPosition(pos)
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
    }
}