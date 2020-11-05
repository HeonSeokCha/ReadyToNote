package com.chs.readytonote.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.target.Target
import com.chs.readytonote.GlideApp
import com.chs.readytonote.R
import com.chs.readytonote.databinding.ItemContainerNoteBinding
import com.chs.readytonote.entities.Note
import kotlinx.android.synthetic.main.item_container_note.view.*
import java.util.*
import kotlin.concurrent.schedule


class NoteAdapter(private val clickListener: (note: Note, position: Int) -> Unit,
                  private val checkClickListener: (checkList: MutableMap<Int, Note>) ->Unit,
                  private val longClickListener: (chkState: Boolean) -> Unit,
                ) : ListAdapter<Note, NoteAdapter.NoteViewHolder>(NoteDiffUtilCallback()) {
    class NoteViewHolder(val binding: ItemContainerNoteBinding)
        : RecyclerView.ViewHolder(binding.root)

    private lateinit var temp: MutableList<Note>
    private lateinit var timerTask: Timer
    private val checkList:MutableMap<Int,Note> by lazy { mutableMapOf() }
    private var checkBox: Boolean = false
    private val searchList: List<Note> by lazy { currentList }


    fun editItemMode(chk:Boolean) {
        checkBox = chk
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_container_note, parent, false)
        val viewHolder = NoteViewHolder(ItemContainerNoteBinding.bind(view))
        view.layoutNote.setOnClickListener {
            if(checkBox) {
                view.img_check.apply {
                    isActivated = !this.isActivated
                }
                if(view.img_check.isActivated) {
                    checkList[viewHolder.adapterPosition] = getItem(viewHolder.adapterPosition)
                } else {
                    checkList.remove(viewHolder.adapterPosition)
                }
                checkClickListener.invoke(checkList)

            } else {
                clickListener.invoke(
                    getItem(viewHolder.adapterPosition),
                    viewHolder.adapterPosition,
                )
            }
        }

        view.layoutNote.setOnLongClickListener {
            editItemMode(true)
            longClickListener(checkBox)
            return@setOnLongClickListener true
        }
        return viewHolder
    }
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.binding.model = getItem(position)
        if (getItem(position).color != "") {
            holder.itemView.layoutNote.setCardBackgroundColor(
                Color.parseColor(getItem(position).color))
        } else {
            holder.itemView.layoutNote.setCardBackgroundColor(
                Color.parseColor("#333333"))
        }
        if(getItem(position).imgPath!!.isNotEmpty()) {
            GlideApp.with(holder.itemView).load(getItem(position).imgPath)
                .error(R.drawable.ic_done)
                .into(holder.itemView.imageNote)
            holder.itemView.imageNote.visibility = View.VISIBLE
        } else {
            holder.itemView.imageNote.visibility = View.GONE
        }
        when {
            checkBox -> holder.itemView.img_check.visibility = View.VISIBLE
            else -> holder.itemView.img_check.visibility = View.GONE
        }
    }

    override fun getItemId(position: Int): Long = getItem(position).id.toLong()

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