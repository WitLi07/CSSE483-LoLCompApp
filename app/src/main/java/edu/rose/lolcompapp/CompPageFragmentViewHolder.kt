package edu.rose.lolcompapp

import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.comp_cardview.view.*

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
    }
}