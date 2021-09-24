package com.chs.readytonote.adapter

import androidx.recyclerview.widget.DiffUtil
import com.chs.readytonote.entities.Label

class LabelDiffUtilCallback : DiffUtil.ItemCallback<Label>() {
    override fun areItemsTheSame(oldItem: Label, newItem: Label): Boolean {
        return oldItem.title == newItem.title

    }

    override fun areContentsTheSame(oldItem: Label, newItem: Label): Boolean {
        return oldItem == newItem
    }
}