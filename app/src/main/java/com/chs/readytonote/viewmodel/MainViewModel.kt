 package com.chs.readytonote.viewmodel

import android.app.Application
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.*
import androidx.loader.content.CursorLoader
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.chs.readytonote.entities.Label
import com.chs.readytonote.entities.LabelCheck
import com.chs.readytonote.repository.NoteRepository
import com.chs.readytonote.entities.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModelFactory(
    private val application: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}

class MainViewModel(application: Application):AndroidViewModel(application) {

    private var repository: NoteRepository = NoteRepository(application)

    fun getAllNotes() = repository.getNotes()

    fun insertNote(note: Note): LiveData<Long> {
        val lastId = MutableLiveData<Long>()
        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.insertNote(note)
            lastId.postValue(id)
        }
        return lastId
    }

    fun searchNotes(searchWord: String) = repository.searchNotes(searchWord)

    fun deleteNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteNote(note)
    }

    fun getAllLabel() = repository.getLabels()

    fun insertLabel(label: Label) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertLabel(label)
    }

    fun getCheckLabel(noteId: Int) = repository.getCheckLabel(noteId)

    fun insertCheckLabel(labelCheck: LabelCheck) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertCheckLabel(labelCheck)
    }

    fun deleteCheckLabel(noteId: Int) = viewModelScope.launch(Dispatchers.IO){
        repository.deleteCheckLabel(noteId)
    }

    fun updateCheckLabel(labelCheck: LabelCheck) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateCheckLabel(labelCheck)
    }

}
