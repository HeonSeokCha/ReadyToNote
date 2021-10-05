package com.chs.readytonote.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chs.readytonote.databinding.ItemContainerLabelBinding
import com.chs.readytonote.entities.Label

class LabelAdapter(
    private val checkedLabelTitle: String?,
    private val clickListener: LabelClickListener
) : ListAdapter<Label, LabelAdapter.LabelViewHolder>(LabelDiffUtilCallback()) {

    interface LabelClickListener {
        fun clickListener(LabelTitle: String, checked: Boolean)
        fun addClickListener(title: String)
    }

    inner class LabelViewHolder(private val binding: ItemContainerLabelBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                binding.txtLabelTitle.isChecked = !binding.txtLabelTitle.isChecked
                clickListener.clickListener(
                    getItem(layoutPosition).title!!,
                    binding.txtLabelTitle.isChecked
                )
            }
        }

        fun bind(label: Label) {
            if (checkedLabelTitle != null) {
                if (checkedLabelTitle == label.title) {
                    label.checked = true
                }
            }
            binding.model = label
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelViewHolder {
        val view =
            ItemContainerLabelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LabelViewHolder(view)
    }

    override fun onBindViewHolder(holder: LabelViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }
}