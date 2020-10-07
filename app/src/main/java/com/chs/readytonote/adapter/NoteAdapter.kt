package com.chs.readytonote.adapter

import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
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
        fun getItemDetails(): ItemDetailsLookup.ItemDetails<String> =
            object : ItemDetailsLookup.ItemDetails<String>() {
                override fun getPosition(): Int = adapterPosition
                override fun getSelectionKey(): String? = itemId.toString()
            }
    }


    private lateinit var timerTask: Timer
    private lateinit var temp:MutableList<Note>
    private val searchList: MutableList<Note> by lazy { currentList }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_container_note, parent, false)
        val viewHolder = NoteViewHolder(ItemContainerNoteBinding.bind(view))
        view.setOnClickListener {
            clickListener.invoke(getItem(viewHolder.adapterPosition), viewHolder.adapterPosition,viewHolder.itemView)
        }
        return viewHolder
    }
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.binding.model = getItem(position)
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