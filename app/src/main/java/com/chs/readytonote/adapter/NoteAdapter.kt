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
import com.chs.readytonote.viewmodel.GlideApp
import com.chs.readytonote.R
import com.chs.readytonote.databinding.ItemContainerNoteBinding
import com.chs.readytonote.entities.Note
import java.util.*
import kotlin.concurrent.schedule


class NoteAdapter (
    private val clickListener: (note: Note, position: Int) -> Unit,
    private val checkClickListener: (checkList: MutableMap<Int, Note>) -> Unit,
    private val longClickListener: (chkState: Boolean) -> Unit
) : ListAdapter<Note, NoteAdapter.NoteViewHolder>(NoteDiffUtilCallback()) {

    private lateinit var temp: MutableList<Note>
    private lateinit var timerTask: TimerTask
    private val checkList: MutableMap<Int, Note> by lazy { mutableMapOf() }
    private val searchList: List<Note> by lazy { currentList }
    private var checkBox: Boolean = false
    private var isSelectModeOn: Boolean = false

    inner class NoteViewHolder(private val binding: ItemContainerNoteBinding)
        : RecyclerView.ViewHolder(binding.root) {
            fun bind() {
                binding.model = getItem(adapterPosition)
                binding.txtDateTime.text = getItem(adapterPosition)
                    .dateTime!!.split("년 ")[1]
                if(getItem(adapterPosition).color != "#333333") {
                    binding.layoutNote.setCardBackgroundColor(
                        Color.parseColor(getItem(adapterPosition).color))
                }

                var gradientDrawable = binding.txtLabel.background as GradientDrawable
                if(getItem(adapterPosition).color=="#FDBE3B") {
                    binding.txtTitle.setTextColor(Color.parseColor("#000000"))
                    binding.txtSubtitle.setTextColor(Color.parseColor("#000000"))
                    binding.txtDateTime.setTextColor(Color.parseColor("#000000"))
                    binding.txtLabel.setTextColor(Color.parseColor("#000000"))
                    gradientDrawable.setStroke(2,
                        Color.parseColor("#000000"))
                } else {
                    gradientDrawable.setStroke(2,
                        binding.root.context.getColor(R.color.colorWhite))
                }

                if(getItem(adapterPosition).label.isNullOrEmpty()) {
                    binding.txtLabel.visibility = View.GONE
                }

                if(getItem(adapterPosition).imgPath!!.isNotEmpty()) {
                    binding.imageNote.visibility = View.VISIBLE
                    GlideApp.with(binding.root)
                        .load(getItem(adapterPosition).imgPath)
                        .placeholder(R.color.colorNoteDefaultColor)
                        .into(binding.imageNote)
                } else {
                    binding.imageNote.visibility = View.GONE
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
        val inflate = LayoutInflater.from(parent.context)
        val view = ItemContainerNoteBinding.inflate(inflate, parent, false)
        val viewHolder = NoteViewHolder(view)
        view.layoutNote.setOnClickListener {
            if (checkBox) {
                view.imgCheck.apply {
                    isActivated = !this.isActivated
                }
                if (view.imgCheck.isActivated) {
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
                if(!view.imgCheck.isActivated)
                    view.imgCheck.isActivated = true
            }
            return@setOnLongClickListener true
        }
        return viewHolder
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemId(position: Int): Long = getItem(position).id.toLong()

    fun search(search: String) {
        timerTask = Timer().schedule(500) {
            if (search.isNotEmpty()) {
                temp = mutableListOf()
                for (note in searchList) {
                    if(note.title!!.toLowerCase().contains(search.toLowerCase()) ||
                        note.subtitle!!.toLowerCase().contains(search.toLowerCase())) {
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