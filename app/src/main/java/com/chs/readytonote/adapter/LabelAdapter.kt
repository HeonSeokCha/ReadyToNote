package com.chs.readytonote.adapter

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chs.readytonote.R
import com.chs.readytonote.databinding.ItemAddLabelBinding
import com.chs.readytonote.databinding.ItemContainerLabelBinding
import com.chs.readytonote.entities.Label
import java.util.*
import kotlin.concurrent.schedule

class LabelAdapter(
    private val clickListener: LabelClickListener
) : ListAdapter<Label, RecyclerView.ViewHolder>(LabelDiffUtilCallback()) {

    interface LabelClickListener {
        fun clickListener(label: Label)
        fun addClickListener(title: String)
    }

    inner class LabelViewHolder(private val binding: ItemContainerLabelBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                binding.txtLabelTitle.isChecked = true
                clickListener.clickListener(getItem(layoutPosition))
            }
        }

        fun bind(label: Label) {
            binding.model = label
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            ItemContainerLabelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LabelViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LabelViewHolder -> {
                holder.bind(getItem(position))
            }
        }
    }
}