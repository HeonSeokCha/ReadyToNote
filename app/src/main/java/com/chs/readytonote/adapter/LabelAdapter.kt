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
    private val clickListener: (label: Label) -> Unit,
    private val addClickListener: (title: String) -> Unit,
): ListAdapter<Label, RecyclerView.ViewHolder>(LabelDiffUtilCallback()) {

    inner class LabelViewHolder(val binding:ItemContainerLabelBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            if(getItem(adapterPosition).checked) {
                selectPosition = adapterPosition
            }
            binding.root.setOnClickListener {
                binding.txtLabelTitle.apply {
                    when {
                        selectPosition == -1 -> {
                            currentList[adapterPosition].checked = true
                            selectPosition = adapterPosition
                        }
                        selectPosition == adapterPosition -> {
                            currentList[adapterPosition].checked = false
                            selectPosition = -1
                        }
                        selectPosition != -1 -> {
                            for(i in currentList.indices) {
                                currentList[i].checked = false
                            }
                            selectPosition = adapterPosition
                            currentList[adapterPosition].checked = true
                        }
                    }
                }
                clickListener.invoke(getItem(adapterPosition))
                notifyDataSetChanged()
            }
        }
    }
    inner class LabelAddViewHolder(val binding:ItemAddLabelBinding):RecyclerView.ViewHolder(binding.root) {
        fun Addbind() {
            binding.checkedTextView.text = "'${getItem(adapterPosition).title}' 라벨 만들기"
            binding.root.setOnClickListener {
                addClickListener.invoke(getItem(0).title!!)
                labelAdd = false
            }
        }
    }

    private var selectPosition: Int = -1
    private var labelAdd: Boolean = false
    private lateinit var temp: MutableList<Label>
    private lateinit var timerTask: TimerTask
    private lateinit var viewholder: RecyclerView.ViewHolder
    private val searchList by lazy { currentList }


    override fun getItemViewType(position: Int): Int {
        return if(::temp.isInitialized && labelAdd) {
            R.layout.item_add_label
        } else {
            R.layout.item_container_label
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        when(viewType) {
            R.layout.item_container_label -> {
                val view = ItemContainerLabelBinding.inflate(layoutInflater,parent,false)
                viewholder = LabelViewHolder(view)
            }
            R.layout.item_add_label -> {
                val view = ItemAddLabelBinding.inflate(layoutInflater,parent,false)
                viewholder = LabelAddViewHolder(view)
            }
        }
        return viewholder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is LabelViewHolder -> {
                holder.binding.model = getItem(position)
                holder.bind()
            }
            is LabelAddViewHolder -> {
                holder.Addbind()
            }
        }
    }

    override fun getItemId(position: Int): Long = getItem(position).id.toLong()

    fun searchLabel(search: String) {
        temp = mutableListOf()
        timerTask = Timer().schedule(500) {
            if(search.isNotEmpty()) {
                for (label in searchList) {
                    if (label.title!!.toLowerCase().contains(search.toLowerCase())) {
                        temp.add(label)
                    }
                }
                labelAdd = if(temp.isEmpty()) {
                    temp.add(Label(search,false))
                    submitList(temp)
                    true
                } else {
                    submitList(temp)
                    false
                }
            } else {
                labelAdd = false
                temp.clear()
            }
            Handler(Looper.getMainLooper()).post {
                notifyDataSetChanged()
            }
        }
    }

    fun cancelTimer() {
        if(::timerTask.isInitialized)
            timerTask.cancel()
    }
}