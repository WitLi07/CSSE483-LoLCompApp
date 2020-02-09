package edu.rose.lolcompapp.info_page_recycler_view_support_classes

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.rose.lolcompapp.Team
import kotlinx.android.synthetic.main.info_page_fragment_cardview.view.*

class InfoPageFragmentViewHolder : RecyclerView.ViewHolder {
    var numberOfTeams: Int = 0
    val cardTitle: TextView = itemView.info_page_card_view_title

    constructor(itemView: View, adapter: InfoPageFragmentAdapter) : super(itemView) {

        numberOfTeams = 0

        itemView.setOnClickListener {
            adapter.selectPicAt(adapterPosition)
        }
    }

    fun bind(team: Team) {
        numberOfTeams++
        cardTitle.text = "Team $numberOfTeams"
    }
}