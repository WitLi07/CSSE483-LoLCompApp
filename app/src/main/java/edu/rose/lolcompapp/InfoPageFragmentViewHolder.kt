package edu.rose.lolcompapp

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.info_page_fragment_cardview.view.*

class InfoPageFragmentViewHolder : RecyclerView.ViewHolder {
    val cardTitle: TextView = itemView.info_page_card_view_title

    constructor(itemView: View, adapter: InfoPageFragmentAdapter) : super(itemView) {
        itemView.setOnClickListener {
            adapter.selectTeamAt(adapterPosition)
        }
    }

    fun bind(team: Team) {
        cardTitle.text = "Team"
    }
}