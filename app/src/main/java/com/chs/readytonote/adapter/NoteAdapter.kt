package com.chs.readytonote.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
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

    private val checkList: MutableMap<Int, Note> by lazy { mutableMapOf() }
    private var checkBox: Boolean = false
    private var isSelectModeOn: Boolean = false

    interface ClickListener {
        fun clickListener(note: Note, position: Int)
        fun checkClickListener(checkList: MutableMap<Int, Note>) // todo Map 써야하는 이유는?
        fun longClickListener()
    }

    inner class NoteViewHolder(private val binding: ItemContainerNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.layoutNote.setOnClickListener {
                if (checkBox) {
                    binding.btnCheck.apply {
                        isChecked = !this.isChecked
                    }
                    if (binding.btnCheck.isActivated) {
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
                clickListener.longClickListener()
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
                    binding.btnCheck.apply {
                        isVisible = true
                        isChecked = isSelectModeOn
                    }
                }
                else -> {
                    binding.btnCheck.apply {
                        isVisible = false
                        isChecked = false
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view =
            ItemContainerNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemId(position: Int): Long = getItem(position).id.toLong()
}