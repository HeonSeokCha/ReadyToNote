package com.chs.readytonote.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chs.readytonote.R
import com.chs.readytonote.calcRotate
import com.chs.readytonote.databinding.ItemContainerNoteBinding
import com.chs.readytonote.entities.Note
import kotlinx.android.synthetic.main.item_container_note.view.*
import java.util.*
import kotlin.concurrent.schedule


class NoteAdapter(private val clickListener: (note: Note, position: Int,view:View) -> Unit)
    : ListAdapter<Note, NoteAdapter.NoteViewHolder>(NoteDiffUtilCallback()) {
    class NoteViewHolder(val binding: ItemContainerNoteBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int = adapterPosition
                override fun getSelectionKey(): Long? = itemId
            }
    }


    private lateinit var timerTask: Timer
    private lateinit var temp:MutableList<Note>
    private val searchList: MutableList<Note> by lazy { currentList }
    private lateinit var selectionTracker: SelectionTracker<Long>
    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_container_note, parent, false)
        val viewHolder = NoteViewHolder(ItemContainerNoteBinding.bind(view))
        view.setOnClickListener {
            if(viewHolder.itemView.txtTitle.isActivated){
                viewHolder.itemView.txtTitle.isActivated = false
            } else {
                clickListener.invoke(getItem(viewHolder.adapterPosition), viewHolder.adapterPosition,viewHolder.itemView)
            }
        }
        view.setOnLongClickListener {
            selectionTracker.select(viewHolder.adapterPosition.toLong())
            viewHolder.itemView.txtTitle.isActivated = selectionTracker.isSelected(viewHolder.adapterPosition.toLong())
            return@setOnLongClickListener true
        }
        return viewHolder
    }
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.binding.model = getItem(position)
        holder
        holder.getItemDetails().position
        var gradientDrawable: GradientDrawable = (holder.itemView.layoutNote.background as GradientDrawable)
        if (getItem(position).color != "") {
            gradientDrawable.setColor(Color.parseColor(getItem(position).color))
        } else {
            gradientDrawable.setColor(Color.parseColor("#333333"))
        }

        if(! getItem(position).imgPath.isNullOrEmpty()) {
            holder.itemView.imageNote.setImageBitmap(
                calcRotate(getItem(position).imgPath!!,2)
            )
            holder.itemView.imageNote.visibility = View.VISIBLE
        } else {
            holder.itemView.imageNote.visibility = View.GONE
        }
    }

    override fun getItemCount() = currentList.size

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun setTracker(selectionTracker: SelectionTracker<Long>){
        this.selectionTracker = selectionTracker
    }

    fun search(searchKeyword: String) {
        timerTask = Timer()
        timerTask.schedule(500){
            if (searchKeyword.isNotEmpty()) {
                temp = mutableListOf()
                for (i in searchList) {
                    if(i.title!!.toLowerCase().contains(searchKeyword.toLowerCase())
                        || i.subtitle!!.toLowerCase().contains(searchKeyword.toLowerCase())) {
                        temp.add(i)
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
        if(::timerTask.isInitialized) {
            timerTask.cancel()
        }
    }
}