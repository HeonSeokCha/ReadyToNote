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
    private val clickListener: (title: String, checked: Boolean) -> Unit,
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

        when(viewholder) {
            is LabelViewHolder -> {
                view.setOnClickListener {
                    if(selectPosition != viewholder.adapterPosition) {
                        view.txtLabelTitle.apply {
                            isChecked = !this.isChecked
                        }
                    } else {
                        view.txtLabelTitle.apply {
                            isChecked = !this.isChecked
                        }
                    }
                    selectPosition = viewholder.adapterPosition
                    clickListener.invoke(getItem(viewholder.adapterPosition).title!!,
                        view.txtLabelTitle.isChecked)
                }
            }
            is LabelAddViewHolder -> {
                view.setOnClickListener {
                    addClickListener.invoke(getItem(0).title!!)
                    labelAdd = false
                }
            }
        }
        return viewholder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is LabelViewHolder -> {
                holder.binding.model = getItem(position)
            }
            is LabelAddViewHolder -> {
                holder.itemView.checkedTextView.text = "'${getItem(position).title}' 라벨 만들기"
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
            } else if(search.isEmpty() && temp.isNotEmpty()){
                labelAdd = false
                submitList(searchList)
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