package com.chs.readytonote.adapter

import androidx.recyclerview.widget.DiffUtil
import com.chs.readytonote.entities.Note

class NoteDiffUtilCallback: DiffUtil.ItemCallback<Note>() {
    override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem.dateTime == newItem.dateTime
    }

    override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem == newItem
    }
}