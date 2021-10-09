package com.chs.readytonote.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chs.readytonote.databinding.ItemContainerLabelBinding
import com.chs.readytonote.entities.Label

class LabelAdapter(
    private val clickListener: (LabelTitle: String, checked: Boolean) -> Unit
) : ListAdapter<Label, LabelAdapter.LabelViewHolder>(LabelDiffUtilCallback()) {

    internal var selectLabelPosition: Int = -1


    inner class LabelViewHolder(private val binding: ItemContainerLabelBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            currentList.forEachIndexed { index, label ->
                if (label.checked) {
                    selectLabelPosition = index
                }
            }

            binding.root.setOnClickListener {
                if (currentList[layoutPosition].checked) {// 체크되어있다면
                    selectLabelPosition = -1
                    currentList[layoutPosition].checked = false
                    notifyItemChanged(layoutPosition)
                } else { //아니면
                    if (selectLabelPosition != -1) { // 기존에 클릭이 되있는 아이템이 있다면
                        currentList[selectLabelPosition].checked = false
                        notifyItemChanged(selectLabelPosition)
                    }
                    currentList[layoutPosition].checked = true
                    selectLabelPosition = layoutPosition
                    notifyItemChanged(layoutPosition)
                }
                clickListener.invoke(
                    currentList[layoutPosition].title!!,
                    currentList[layoutPosition].checked
                )
            }
        }

        fun bind(label: Label) {
            binding.model = label
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelViewHolder {
        val view =
            ItemContainerLabelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LabelViewHolder(view)
    }

    override fun onBindViewHolder(holder: LabelViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}