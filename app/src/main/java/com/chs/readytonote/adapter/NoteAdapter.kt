package com.chs.readytonote.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.chs.readytonote.R
import com.chs.readytonote.calcRotate
import com.chs.readytonote.databinding.ItemContainerNoteBinding
import com.chs.readytonote.entities.Note
import kotlinx.android.synthetic.main.item_container_note.view.*
import java.util.*
import kotlin.concurrent.schedule


class NoteAdapter(private var item: MutableList<Note>,
                  private val clickListener: (note: Note, position: Int) -> Unit,
                ) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {
    class NoteViewHolder(val binding: ItemContainerNoteBinding)
        : RecyclerView.ViewHolder(binding.root)

    private lateinit var temp: MutableList<Note>
    private lateinit var timerTask: Timer
    private val searchList: MutableList<Note> by lazy { item }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_container_note, parent, false)
        val viewHolder = NoteViewHolder(ItemContainerNoteBinding.bind(view))
        view.layoutNote.setOnClickListener {
            clickListener.invoke(
                item[viewHolder.adapterPosition],
                viewHolder.adapterPosition,
            )
        }

        view.layoutNote.setOnLongClickListener {
            Toast.makeText(it.context,
                "This Note is ${viewHolder.adapterPosition} position",
                Toast.LENGTH_SHORT).show()
            allSelectItem(viewHolder)
            return@setOnLongClickListener true
        }
        return viewHolder
    }
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.binding.model = item[position]
        var gradientDrawable = (holder.itemView.layoutNote.background as GradientDrawable)
        if (item[position].color != "") {
            gradientDrawable.setColor(Color.parseColor(item[position].color))
        } else {
            gradientDrawable.setColor(Color.parseColor("#333333"))
        }
        if(item[position].imgPath!!.isNotEmpty()) {
            holder.itemView.imageNote.setImageBitmap(
                calcRotate(item[position].imgPath!!,3)
            )
            holder.itemView.imageNote.visibility = View.VISIBLE
        } else {
            holder.itemView.imageNote.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = item.size

    fun search(searchKeyword: String) {
        timerTask = Timer()
        timerTask.schedule(500) {
            if (searchKeyword.isNotEmpty()) {
                temp = mutableListOf()
                for (note in searchList) {
                    if(note.title!!.toLowerCase().contains(searchKeyword.toLowerCase())
                        || note.subtitle!!.toLowerCase().contains(searchKeyword.toLowerCase())) {
                        temp.add(note)
                    }
                    item = temp
                }
            } else item = searchList
            Handler(Looper.getMainLooper()).post {
                notifyDataSetChanged()
            }
        }
    }

    fun cancelTimer() {
        if(::timerTask.isInitialized)
            timerTask.cancel()
    }

    fun allSelectItem(holder: NoteViewHolder) {
        holder.itemView.img_check.visibility = if(holder.itemView.img_check.visibility == View.GONE) {
            View.VISIBLE
        } else View.GONE
    }
}