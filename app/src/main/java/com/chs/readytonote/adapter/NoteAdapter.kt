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
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule


class NoteAdapter(
    private val clickListener: ClickListener
) : ListAdapter<Note, NoteAdapter.NoteViewHolder>(NoteDiffUtilCallback()) {

    private var checkList: ArrayList<Int> = arrayListOf()
    private var checkBox: Boolean = false
    internal var isSelectModeOn: Boolean = false

    interface ClickListener {
        fun clickListener(note: Note, position: Int)
        fun checkClickListener(checkList: List<Int>)
        fun longClickListener()
    }

    inner class NoteViewHolder(private val binding: ItemContainerNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.layoutNote.setOnClickListener {
                if (isSelectModeOn) {
                    binding.btnCheck.isChecked = !binding.btnCheck.isChecked
                    if (binding.btnCheck.isChecked) {
                        checkList.add(currentList[layoutPosition].id)
                    } else {
                        checkList.remove(currentList[layoutPosition].id)
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
                isSelectModeOn = true
                notifyDataSetChanged()
                return@setOnLongClickListener true
            }
        }

        fun bind(note: Note) {
            with(binding) {
                this.model = note
                this.btnCheck.isVisible = note.showSelected
                this.model = note
                this.btnCheck.isVisible = note.showSelected
                if (note.labelTitle.isNullOrEmpty()) {
                    this.txtLabel.visibility = View.GONE
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