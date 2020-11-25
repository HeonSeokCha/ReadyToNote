package com.chs.readytonote.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.chs.readytonote.dao.TodoDao
import com.chs.readytonote.database.NotesDatabases
import com.chs.readytonote.entities.Note
import com.chs.readytonote.entities.Todo

class TodoRepository (application: Application){
    private val dao: TodoDao by lazy {
        val db = NotesDatabases.getInstance(application)
        db.todoDao()
    }

    fun getNotes(targetNote: Int): LiveData<MutableList<Todo>> =
        dao.getAllTodo(targetNote)

    suspend fun insert(todo: Todo) {
        dao.insertTodo(todo)
    }

    suspend fun delete(todo: Todo) {
        dao.deleteTodo(todo)
    }

    suspend fun allDelete() {
        dao.deleteTodoAll()
    }
}