package com.chs.readytonote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chs.readytonote.R
import com.chs.readytonote.dto.Note
import kotlinx.android.synthetic.main.item_container_note.view.*

class NoteAdapter(private val items:MutableList<Note>):RecyclerView.Adapter<NoteAdapter.NoteViewHolder>(){
    class NoteViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_container_note,parent,false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        with(holder.itemView){
            txtTitle.text = items[position].title
            txtSubtitle.text = items[position].subTitle
            txtDateTime.text = items[position].dateTime
        }
    }

    override fun getItemCount() = items.size
}