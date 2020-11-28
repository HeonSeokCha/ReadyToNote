package com.chs.readytonote.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chs.readytonote.R
import com.chs.readytonote.databinding.ItemContainerNoteBinding
import com.chs.readytonote.databinding.ItemContainerTodoBinding
import com.chs.readytonote.entities.Todo

class TodoAdapter(
    private val clickListener: (todo: Todo, position: Int) -> Unit
):ListAdapter<Todo,TodoAdapter.TodoViewHolder>(TodoDiffUtilCallback()) {
    class TodoViewHolder(val binding:ItemContainerTodoBinding):
            RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_container_todo, parent, false)
        val viewHolder = TodoViewHolder(ItemContainerTodoBinding.bind(view))
        view.setOnClickListener {
            TODO("todo click..")
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.binding.model = getItem(position)
    }

    override fun getItemId(position: Int): Long = getItem(position).id.toLong()
}