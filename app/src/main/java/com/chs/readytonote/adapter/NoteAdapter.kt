package com.chs.readytonote.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition

import com.chs.readytonote.GlideApp
import com.chs.readytonote.R
import com.chs.readytonote.databinding.ItemContainerNoteBinding
import com.chs.readytonote.entities.Note
import kotlinx.android.synthetic.main.item_container_note.view.*
import java.util.*
import kotlin.concurrent.schedule


class NoteAdapter(
    private val clickListener: (note: Note, position: Int) -> Unit,
    private val checkClickListener: (checkList: MutableMap<Int, Note>) -> Unit,
    private val longClickListener: (chkState: Boolean) -> Unit
) : ListAdapter<Note, NoteAdapter.NoteViewHolder>(NoteDiffUtilCallback()) {
    class NoteViewHolder(val binding: ItemContainerNoteBinding)
        : RecyclerView.ViewHolder(binding.root)

    private lateinit var temp: MutableList<Note>
    private lateinit var timerTask: TimerTask
    private val checkList: MutableMap<Int, Note> by lazy { mutableMapOf() }
    private val searchList: List<Note> by lazy { currentList }
    private var checkBox: Boolean = false
    private var isSelectModeOn: Boolean = false


    fun editItemMode(chk: Boolean) {
        checkBox = chk
        notifyDataSetChanged()
    }

    fun selectAll(chk: Boolean) {
        isSelectModeOn = if(chk) {
            for(i in currentList.indices)
                checkList[i] = currentList[i]
            true
        } else {
            for(i in currentList.indices)
                checkList.remove(i)
            false
        }
        notifyDataSetChanged()
        checkClickListener.invoke(checkList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_container_note, parent, false)
        val viewHolder = NoteViewHolder(ItemContainerNoteBinding.bind(view))
        view.layoutNote.setOnClickListener {
            if (checkBox) {
                view.img_check.apply {
                    isActivated = !this.isActivated
                }
                if (view.img_check.isActivated) {
                    checkList[viewHolder.adapterPosition] = getItem(viewHolder.adapterPosition)
                } else {
                    checkList.remove(viewHolder.adapterPosition)
                }
                checkClickListener.invoke(checkList)

            } else {
                clickListener.invoke(
                    getItem(viewHolder.adapterPosition),
                    viewHolder.adapterPosition
                )
            }
        }

        view.layoutNote.setOnLongClickListener {
            if(!checkBox) {
                editItemMode(true)
                longClickListener(checkBox)
                if(!view.layoutNote.img_check.isActivated)
                    view.layoutNote.img_check.isActivated = true
            }
            return@setOnLongClickListener true
        }
        return viewHolder
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.binding.model = getItem(position)
        with(holder.itemView) {
            this.txtDateTime.text = getItem(position).dateTime!!.split("년 ")[1]

            if(getItem(position).color != "#333333") {
                this.layoutNote.setCardBackgroundColor(
                    Color.parseColor(getItem(position).color))
            }

            if(getItem(position).color=="#FDBE3B") {
                this.txtTitle.setTextColor(Color.parseColor("#000000"))
                this.txtSubtitle.setTextColor(Color.parseColor("#000000"))
                this.txtDateTime.setTextColor(Color.parseColor("#000000"))
            }

            if(getItem(position).imgPath!!.isNotEmpty()) {
                this.imageNote.visibility = View.VISIBLE
                GlideApp.with(this)
                    .load(getItem(position).imgPath)
                    .placeholder(R.color.colorNoteDefaultColor)
                    .into(this.imageNote)
            } else {
                this.imageNote.visibility = View.GONE
            }
            when {
                checkBox -> {
                    this.img_check.visibility = View.VISIBLE
                    this.img_check.isActivated = isSelectModeOn
                }
                else -> {
                    this.img_check.visibility = View.GONE
                    this.img_check.isActivated = false
                }
            }
        }
    }

    override fun getItemId(position: Int): Long = getItem(position).id.toLong()

    fun search(searchKeyword: String) {
        timerTask = Timer().schedule(500) {
            if (searchKeyword.isNotEmpty()) {
                temp = mutableListOf()
                for (note in searchList) {
                    if(note.title!!.toLowerCase().contains(searchKeyword.toLowerCase()) ||
                        note.subtitle!!.toLowerCase().contains(searchKeyword.toLowerCase())) {
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