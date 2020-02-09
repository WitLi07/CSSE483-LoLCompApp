package edu.rose.lolcompapp

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.comp_cardview.view.*
import kotlinx.android.synthetic.main.info_page_fragment_cardview.view.*

class CompPageFragmentViewHolder : RecyclerView.ViewHolder {

    val cardTitle: TextView = itemView.comp_name
    lateinit var adapter: CompPageFragmentAdapter
    lateinit var context: Context
    val spin1:Spinner = itemView.spinner_1
    val spin2:Spinner = itemView.spinner_2
    val spin3:Spinner = itemView.spinner_3
    val spin4:Spinner = itemView.spinner_4
    val spin5:Spinner = itemView.spinner_5


    constructor(itemView: View, adapter: CompPageFragmentAdapter, context: Context) : super(itemView) {
        this.context = context
        this.adapter = adapter
    }

    fun bind(comp : Comp) {
        cardTitle.text = comp.name

        for(i in 1..5) {
            for((ind, name) in comp.users.withIndex()) {
                when (i) {
                    1 -> {
                        if(name.lane.equals("top")) {
                            var ad : ArrayAdapter<String> = ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item, name.preferedChampions)
                            spin1.adapter = ad
                            spin1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                                override fun onNothingSelected(parent: AdapterView<*>?) {

                                }

                                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                    var select = parent?.getItemAtPosition(position).toString()
                                    var temp = Comp(comp.uid, comp.name, select, comp.mid, comp.sup, comp.ad, comp.jungle, comp.users)
                                    adapter.edit(adapterPosition, temp)
                                }

                            }
                        }
                    }
                    2 -> {
                        if(name.lane.equals("mid")) {
                            var ad : ArrayAdapter<String> = ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item, name.preferedChampions)
                            spin1.adapter = ad
                            spin1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                                override fun onNothingSelected(parent: AdapterView<*>?) {

                                }

                                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                    var select = parent?.getItemAtPosition(position).toString()
                                    var temp = Comp(comp.uid, comp.name, comp.top, select, comp.sup, comp.ad, comp.jungle, comp.users)
                                    adapter.edit(adapterPosition, temp)
                                }

                            }
                        }
                    }
                    3 -> {
                        if(name.lane.equals("sup")) {
                            var ad : ArrayAdapter<String> = ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item, name.preferedChampions)
                            spin1.adapter = ad
                            spin1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                                override fun onNothingSelected(parent: AdapterView<*>?) {

                                }

                                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                    var select = parent?.getItemAtPosition(position).toString()
                                    var temp = Comp(comp.uid, comp.name, comp.top, comp.mid, select, comp.ad, comp.jungle, comp.users)
                                    adapter.edit(adapterPosition, temp)
                                }

                            }
                        }
                    }
                    4 -> {
                        if(name.lane.equals("ad")) {
                            var ad : ArrayAdapter<String> = ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item, name.preferedChampions)
                            spin1.adapter = ad
                            spin1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                                override fun onNothingSelected(parent: AdapterView<*>?) {

                                }

                                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                    var select = parent?.getItemAtPosition(position).toString()
                                    var temp = Comp(comp.uid, comp.name, comp.top, comp.mid, comp.sup, select, comp.jungle, comp.users)
                                    adapter.edit(adapterPosition, temp)
                                }

                            }
                        }
                    }
                    5 -> {
                        if(name.lane.equals("jungle")) {
                            var ad : ArrayAdapter<String> = ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item, name.preferedChampions)
                            spin1.adapter = ad
                            spin1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                                override fun onNothingSelected(parent: AdapterView<*>?) {

                                }

                                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                    var select = parent?.getItemAtPosition(position).toString()
                                    var temp = Comp(comp.uid, comp.name, comp.top, comp.mid, comp.sup, comp.ad, select, comp.users)
                                    adapter.edit(adapterPosition, temp)
                                }

                            }
                        }
                    }
                }
            }
        }
    }
}