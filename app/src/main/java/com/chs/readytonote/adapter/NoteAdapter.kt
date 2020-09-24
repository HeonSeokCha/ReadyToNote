package com.chs.readytonote.adapter

import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chs.readytonote.R
import com.chs.readytonote.databinding.ItemContainerNoteBinding
import com.chs.readytonote.entities.Note
import kotlinx.android.synthetic.main.item_container_note.view.*
import java.util.*
import kotlin.concurrent.schedule

class NoteAdapter(private var item: MutableList<Note>,
                  private val clickListener: (note:Note,position:Int) -> Unit,
                  private val longClickListener: (note:Note) -> Unit)
    : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {
    class NoteViewHolder(val binding: ItemContainerNoteBinding)
        : RecyclerView.ViewHolder(binding.root)

    private lateinit var timerTask: Timer
    private val searchList: MutableList<Note> by lazy { item }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_container_note,parent,false)
        val viewHolder = NoteViewHolder(ItemContainerNoteBinding.bind(view))
        view.setOnClickListener {
            clickListener.invoke(item[viewHolder.adapterPosition],viewHolder.adapterPosition)
        }
        view.setOnLongClickListener {
            longClickListener.invoke(item[viewHolder.adapterPosition])
            return@setOnLongClickListener true
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.binding.model = item[position]
        var gradientDrawable: GradientDrawable = (holder.itemView.layoutNote.background as GradientDrawable)
        if (item[position].color != "") {
            gradientDrawable.setColor(Color.parseColor(item[position].color))
        } else {
            gradientDrawable.setColor(Color.parseColor("#333333"))
        }

        if(! item[position].imgPath.isNullOrEmpty()) {
            val options = BitmapFactory.Options()
            options.inSampleSize = 2
            holder.itemView.imageNote.setImageBitmap(
                BitmapFactory.decodeFile(item[position].imgPath, options))
            holder.itemView.imageNote.visibility = View.VISIBLE
        } else {
            holder.itemView.imageNote.visibility = View.GONE
        }
    }

    override fun getItemCount() = item.size

    fun search(searchKeyword: String) {
        timerTask.schedule(500){
            if (searchKeyword.isNotEmpty()) {
                var temp = mutableListOf<Note>()
                for (i in searchList) {
                    if(i.title!!.toLowerCase().contains(searchKeyword.toLowerCase())
                        || i.subtitle!!.toLowerCase().contains(searchKeyword.toLowerCase())) {
                        temp.add(i)
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
        if(::timerTask.isInitialized) {
            timerTask.cancel()
        }
    }
}