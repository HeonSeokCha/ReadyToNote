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
): ListAdapter<Label, RecyclerView.ViewHolder>(LabelDiffUtilCallback()) {

    class LabelViewHolder(val binding:ItemContainerLabelBinding):RecyclerView.ViewHolder(binding.root)
    class LabelAddViewHolder(binding:ItemAddLabelBinding):RecyclerView.ViewHolder(binding.root)

    private lateinit var temp: MutableList<Label>
    private lateinit var timerTask: TimerTask
    private lateinit var viewholder: RecyclerView.ViewHolder
    private val searchList by lazy { currentList }


    override fun getItemViewType(position: Int): Int {
        return if(::temp.isInitialized && temp.isEmpty()) {
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
                view.layout_label.setOnClickListener {
                    viewholder.itemView.txtLabelTitle.apply {
                        isChecked = !this.isChecked
                    }
                    clickListener.invoke(getItem(viewholder.adapterPosition).title!!,
                        viewholder.itemView.txtLabelTitle.isChecked)
                }
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
                Log.d("LabelViewHolder","LabelViewHolder")
                holder.binding.model = getItem(position)
            }
            is LabelAddViewHolder -> {
                Log.d("LabelAddViewHolder","LabelAddViewHolder")
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
                    submitList(temp)
                }
            } else submitList(searchList)
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