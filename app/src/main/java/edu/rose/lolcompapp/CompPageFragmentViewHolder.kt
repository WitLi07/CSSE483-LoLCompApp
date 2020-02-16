package edu.rose.lolcompapp

import android.content.Context
import android.view.View
import android.widget.*
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.comp_cardview.view.*
import kotlinx.android.synthetic.main.info_page_fragment_cardview.view.*
import org.w3c.dom.Text

class CompPageFragmentViewHolder(itemView: View, adapter: CompPageFragmentAdapter) : RecyclerView.ViewHolder(itemView) {

    val cardTitle: TextView = itemView.comp_name
    private val storageRef = FirebaseStorage.getInstance()
        .reference
        .child("champImages")
    val top: ImageView = itemView.comp_top
    val mid:ImageView = itemView.comp_mid
    val sup:ImageView = itemView.comp_sup
    val adc:ImageView = itemView.comp_adc
    val jg:ImageView = itemView.comp_jg

    init {
        itemView.setOnLongClickListener {
            adapter.update(adapterPosition)
            true
        }
    }

    fun bind(comp : Comp) {
        cardTitle.text = comp.name

        storageRef.child(comp.top + ".png").downloadUrl.addOnCompleteListener {
            val url = it.result
            Picasso.get().load(url).into(top)
        }

        storageRef.child(comp.mid + ".png").downloadUrl.addOnCompleteListener {
            val url = it.result
            Picasso.get().load(url).into(mid)
        }

        storageRef.child(comp.sup + ".png").downloadUrl.addOnCompleteListener {
            val url = it.result
            Picasso.get().load(url).into(sup)
        }

        storageRef.child(comp.ad + ".png").downloadUrl.addOnCompleteListener {
            val url = it.result
            Picasso.get().load(url).into(adc)
        }
        storageRef.child(comp.jungle + ".png").downloadUrl.addOnCompleteListener {
            val url = it.result
            Picasso.get().load(url).into(jg)
        }

//        for(i in 1..5) {
//            for((ind, name) in users.withIndex()) {
//                when (i) {
//                    1 -> {
//                        if(name.lane.equals("top")) {
//                            var ad : ArrayAdapter<String> = ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item, name.preferedChampions)
//                            spin1.adapter = ad
//                            spin1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
//                                override fun onNothingSelected(parent: AdapterView<*>?) {
//
//                                }
//
//                                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                                    var select = parent?.getItemAtPosition(position).toString()
//                                    var temp = Comp(comp.uid, comp.name, select, comp.mid, comp.sup, comp.ad, comp.jungle, comp.users)
//                                    adapter.edit(adapterPosition, temp)
//                                }
//
//                            }
//                        }
//                    }
//                    2 -> {
//                        if(name.lane.equals("mid")) {
//                            var ad : ArrayAdapter<String> = ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item, name.preferedChampions)
//                            spin2.adapter = ad
//                            spin2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
//                                override fun onNothingSelected(parent: AdapterView<*>?) {
//
//                                }
//
//                                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                                    var select = parent?.getItemAtPosition(position).toString()
//                                    var temp = Comp(comp.uid, comp.name, comp.top, select, comp.sup, comp.ad, comp.jungle, comp.users)
//                                    adapter.edit(adapterPosition, temp)
//                                }
//
//                            }
//                        }
//                    }
//                    3 -> {
//                        if(name.lane.equals("sup")) {
//                            var ad : ArrayAdapter<String> = ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item, name.preferedChampions)
//                            spin3.adapter = ad
//                            spin3.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
//                                override fun onNothingSelected(parent: AdapterView<*>?) {
//
//                                }
//
//                                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                                    var select = parent?.getItemAtPosition(position).toString()
//                                    var temp = Comp(comp.uid, comp.name, comp.top, comp.mid, select, comp.ad, comp.jungle, comp.users)
//                                    adapter.edit(adapterPosition, temp)
//                                }
//
//                            }
//                        }
//                    }
//                    4 -> {
//                        if(name.lane.equals("ad")) {
//                            var ad : ArrayAdapter<String> = ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item, name.preferedChampions)
//                            spin4.adapter = ad
//                            spin4.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
//                                override fun onNothingSelected(parent: AdapterView<*>?) {
//
//                                }
//
//                                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                                    var select = parent?.getItemAtPosition(position).toString()
//                                    var temp = Comp(comp.uid, comp.name, comp.top, comp.mid, comp.sup, select, comp.jungle, comp.users)
//                                    adapter.edit(adapterPosition, temp)
//                                }
//
//                            }
//                        }
//                    }
//                    5 -> {
//                        if(name.lane.equals("jungle")) {
//                            var ad : ArrayAdapter<String> = ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item, name.preferedChampions)
//                            spin5.adapter = ad
//                            spin5.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
//                                override fun onNothingSelected(parent: AdapterView<*>?) {
//
//                                }
//
//                                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                                    var select = parent?.getItemAtPosition(position).toString()
//                                    var temp = Comp(comp.uid, comp.name, comp.top, comp.mid, comp.sup, comp.ad, select, comp.users)
//                                    adapter.edit(adapterPosition, temp)
//                                }
//
//                            }
//                        }
//                    }
//                }
//            }
//        }

    }
}