package com.chs.readytonote.adapter

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.chs.readytonote.entities.Note

class NoteDetailsLookup(private val recyclerView: RecyclerView): ItemDetailsLookup<String>() {
    override fun getItemDetails(e: MotionEvent): ItemDetails<String>? {
        val view = recyclerView.findChildViewUnder(e.x, e.y)
        if(view != null) {
            return (recyclerView.getChildViewHolder(view) as NoteAdapter.NoteViewHolder)
                .getItemDetails()
        }
        return null
    }
}