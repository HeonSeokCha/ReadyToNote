package com.chs.readytonote.adapter

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import com.chs.readytonote.entities.Note

class NoteDiffUtilCallback : DiffUtil.ItemCallback<Note>() {
    override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
        Log.e("areItemsTheSame", "${oldItem.id == newItem.id}")
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
        Log.e("areContentsTheSame", "${oldItem == newItem}")
        return oldItem == newItem
    }
}