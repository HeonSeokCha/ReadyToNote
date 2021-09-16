package com.chs.readytonote.ui

import android.app.Application
import androidx.lifecycle.*
import com.chs.readytonote.DataStoreModule
import com.chs.readytonote.entities.Label
import com.chs.readytonote.entities.LabelCheck
import com.chs.readytonote.repository.NoteRepository
import com.chs.readytonote.entities.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel(
    application: Application
) : ViewModel() {

    private val repository by lazy {
        NoteRepository(application)
    }

    private val dataStore by lazy {
        DataStoreModule(application)
    }

    val noteLiveData: LiveData<List<Note>> = repository.getNotes().asLiveData()


    fun insertNote(note: Note): LiveData<Long> {
        val lastId = MutableLiveData<Long>()
        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.insertNote(note)
            lastId.postValue(id)
        }
        return lastId
    }

    fun searchNotes(searchWord: String) = repository.searchNotes(searchWord).asLiveData()

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    fun getAllLabel() = repository.getLabels().asLiveData()

    fun insertLabel(label: Label) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertLabel(label)
    }

    fun getCheckLabel(noteId: Int) = repository.getCheckLabel(noteId).asLiveData()

    fun insertCheckLabel(labelCheck: LabelCheck) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertCheckLabel(labelCheck)
    }

    fun deleteCheckLabel(noteId: Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteCheckLabel(noteId)
    }

    fun updateCheckLabel(labelCheck: LabelCheck) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateCheckLabel(labelCheck)
    }

    fun setUiMode(uiStatus: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.setUIStatus(uiStatus)
        }
    }

    fun destroyView() {

    }
}