package com.chs.readytonote.adapter

import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chs.readytonote.R
import com.chs.readytonote.databinding.ItemContainerNoteBinding
import com.chs.readytonote.entities.Note
import kotlinx.android.synthetic.main.item_container_note.view.*

class NoteAdapter(private val clickListener:(note:Note,position:Int) -> Unit)
    :RecyclerView.Adapter<NoteAdapter.NoteViewHolder>(){
    class NoteViewHolder(val binding:ItemContainerNoteBinding):RecyclerView.ViewHolder(binding.root)

    private var item:List<Note> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_container_note,parent,false)
        val viewHolder = NoteViewHolder(ItemContainerNoteBinding.bind(view))
        return viewHolder
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.binding.model = item[position]
        holder.itemView.layoutNote.setOnClickListener {
            clickListener.invoke(item[position],position)
        }
        var gradientDrawable: GradientDrawable = (holder.itemView.layoutNote.background as GradientDrawable)
        if(item[position].color != ""){
            gradientDrawable.setColor(Color.parseColor(item[position].color))
        } else{
            gradientDrawable.setColor(Color.parseColor("#333333"))
        }

        if(item[position].imgPath != ""){
            holder.itemView.imageNote.setImageBitmap(BitmapFactory.decodeFile(item[position].imgPath))
            holder.itemView.imageNote.visibility = View.VISIBLE
        } else { holder.itemView.imageNote.visibility = View.GONE }
    }

    override fun getItemCount() = item.size

    fun setData(getNote:List<Note>){
        item = getNote
        notifyDataSetChanged()
    }

}