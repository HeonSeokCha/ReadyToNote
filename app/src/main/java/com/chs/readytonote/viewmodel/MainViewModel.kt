package com.chs.readytonote.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.chs.readytonote.repository.NoteRepository
import com.chs.readytonote.entities.Note
import com.chs.readytonote.entities.Todo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application):AndroidViewModel(application) {

    private var repository: NoteRepository = NoteRepository(application)

    fun getAllNotes() = repository.getNotes()
    fun getALlTodos() = repository.getTodos()

    fun insertNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertNote(note)
    }

    fun insertTodo(todo: Todo) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertTodo(todo)
    }

    fun deleteNote(note:Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteNote(note)
    }

    fun deleteTodo(todo: Todo) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteTodo(todo)
    }

    fun allDelete() = viewModelScope.launch(Dispatchers.IO) {
        repository.allDelete()
    }
}