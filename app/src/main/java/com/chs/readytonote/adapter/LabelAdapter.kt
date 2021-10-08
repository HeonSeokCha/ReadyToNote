package com.chs.readytonote.adapter

import android.util.Log
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

    private var selectLabelPosition: Int = -1

    interface LabelClickListener {
        fun clickListener(LabelTitle: String, checked: Boolean)
        fun addClickListener(title: String)
    }

    inner class LabelViewHolder(private val binding: ItemContainerLabelBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            if (checkedLabelTitle != null) {
                selectLabelPosition = currentList.indexOfFirst { it.title == checkedLabelTitle }
                currentList[selectLabelPosition!!].checked = true
            }
        }

        fun bind(label: Label, position: Int) {
            binding.model = label
            binding.root.setOnClickListener {
                binding.txtLabelTitle.isChecked = currentList[layoutPosition].checked
                for (i in currentList.indices) {
                    currentList[i].checked = i == layoutPosition
                }
                clickListener.clickListener(
                    getItem(layoutPosition).title!!,
                    getItem(layoutPosition).checked
                )
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelViewHolder {
        val view =
            ItemContainerLabelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LabelViewHolder(view)
    }

    override fun onBindViewHolder(holder: LabelViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }
}