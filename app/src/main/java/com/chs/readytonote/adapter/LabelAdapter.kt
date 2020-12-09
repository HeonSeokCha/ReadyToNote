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
import kotlinx.android.synthetic.main.item_add_label.view.*
import kotlinx.android.synthetic.main.item_container_label.view.*
import kotlinx.android.synthetic.main.layout_label.view.*
import java.util.*
import kotlin.concurrent.schedule

class LabelAdapter(
    private val clickListener: (label: Label,position: Int) -> Unit,
    private val addClickListener: (title: String) -> Unit,
): ListAdapter<Label, RecyclerView.ViewHolder>(LabelDiffUtilCallback()) {

    class LabelViewHolder(val binding:ItemContainerLabelBinding):RecyclerView.ViewHolder(binding.root)
    class LabelAddViewHolder(binding:ItemAddLabelBinding):RecyclerView.ViewHolder(binding.root)

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
        val view = LayoutInflater.from(parent.context)
            .inflate(viewType, parent, false)
        when(viewType) {
            R.layout.item_container_label -> {
                viewholder = LabelViewHolder(ItemContainerLabelBinding.bind(view))
            }
            R.layout.item_add_label -> {
                viewholder = LabelAddViewHolder(ItemAddLabelBinding.bind(view))
            }
        }
        return viewholder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is LabelViewHolder -> {
                holder.binding.model = getItem(position)
                if(getItem(position).checked) {
                    selectPosition = position
                }
                holder.itemView.setOnClickListener {
                    holder.itemView.txtLabelTitle.apply {
                        when {
                            selectPosition == -1 -> {
                                currentList[position].checked = true
                                selectPosition = position
                            }
                            selectPosition == position -> {
                                currentList[position].checked = false
                                selectPosition = -1
                            }
                            selectPosition != -1 -> {
                                for(i in currentList.indices) {
                                    currentList[i].checked = false
                                }
                                selectPosition = position
                                currentList[position].checked = true
                            }
                        }
                    }
                    notifyDataSetChanged()
                    clickListener.invoke(getItem(position),position)
                }
            }
            is LabelAddViewHolder -> {
                holder.itemView.checkedTextView.text = "'${getItem(position).title}' 라벨 만들기"
                holder.itemView.setOnClickListener {
                    addClickListener.invoke(getItem(0).title!!)
                    labelAdd = false
                }
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
                    temp.add(Label(note_id = 0,search,false))
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