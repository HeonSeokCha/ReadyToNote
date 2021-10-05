package com.chs.readytonote.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chs.readytonote.R
import com.chs.readytonote.databinding.ItemContainerNoteBinding
import com.chs.readytonote.entities.Note
import java.util.*
import kotlin.concurrent.schedule


class NoteAdapter(
    private val clickListener: ClickListener
) : ListAdapter<Note, NoteAdapter.NoteViewHolder>(NoteDiffUtilCallback()) {

    private lateinit var temp: MutableList<Note>
    private lateinit var timerTask: TimerTask
    private val checkList: MutableMap<Int, Note> by lazy { mutableMapOf() }
    private val searchList: List<Note> by lazy { currentList }
    private var checkBox: Boolean = false
    private var isSelectModeOn: Boolean = false

    interface ClickListener {
        fun clickListener(note: Note, position: Int)
        fun checkClickListener(checkList: MutableMap<Int, Note>) // todo Map 써야하는 이유는?
        fun longClickListener(chkState: Boolean)
    }

    inner class NoteViewHolder(private val binding: ItemContainerNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.layoutNote.setOnClickListener {
                if (checkBox) {
                    binding.imgCheck.apply {
                        isActivated = !this.isActivated
                    }
                    if (binding.imgCheck.isActivated) {
                        checkList[layoutPosition] = getItem(layoutPosition)
                    } else {
                        checkList.remove(layoutPosition)
                    }
                    clickListener.checkClickListener(checkList)

                } else {
                    clickListener.clickListener(
                        getItem(layoutPosition),
                        layoutPosition
                    )
                }
            }

            binding.layoutNote.setOnLongClickListener {
                if (!checkBox) {
                    editItemMode(true)
                    clickListener.longClickListener(checkBox)
                    binding.imgCheck.isActivated = !binding.imgCheck.isActivated
                }
                return@setOnLongClickListener true
            }
        }

        fun bind(note: Note) {
            binding.model = note
            if (note.labelTitle.isNullOrEmpty()) {
                binding.txtLabel.visibility = View.GONE
            }
            if (note.imgPath!!.isEmpty()) {
                binding.imageNote.visibility = View.GONE
            } else {
                binding.imageNote.visibility = View.VISIBLE
            }
            when {
                checkBox -> {
                    binding.imgCheck.apply {
                        visibility = View.VISIBLE
                        isActivated = isSelectModeOn
                    }
                }
                else -> {
                    binding.imgCheck.apply {
                        visibility = View.GONE
                        isActivated = false
                    }
                }
            }
        }
    }

    fun editItemMode(chk: Boolean) {
        checkBox = chk
        notifyDataSetChanged()
    }

    fun selectAll(chk: Boolean) {
        isSelectModeOn = if (chk) {
            for (i in currentList.indices)
                checkList[i] = currentList[i]
            true
        } else {
            for (i in currentList.indices)
                checkList.remove(i)
            false
        }
        notifyDataSetChanged()
        clickListener.checkClickListener(checkList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        val view = ItemContainerNoteBinding.inflate(inflate, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemId(position: Int): Long = getItem(position).id.toLong()
}