package com.chs.readytonote

import android.app.Application
import androidx.lifecycle.LiveData
import com.chs.readytonote.dao.NoteDao
import com.chs.readytonote.database.NotesDatabases
import com.chs.readytonote.entities.Note

class NoteRepository (application: Application){

    private var dao:NoteDao
    init{
        val db =  NotesDatabases.getInstance(application)!!
        dao = db.todoDao()
    }

    fun getNotes() = dao.getAllNotes()

    suspend fun insert(note:Note){
        dao.insertNote(note)
    }

    suspend fun delete(note:Note){
        dao.deleteNote(note)
    }

    suspend fun allDelete(){
        dao.deleteAll()
    }
}