package com.chs.readytonote.ui

import android.app.Application
import androidx.lifecycle.*
import com.chs.readytonote.util.Constants
import com.chs.readytonote.entities.Label
import com.chs.readytonote.entities.LabelCheck
import com.chs.readytonote.repository.NoteRepository
import com.chs.readytonote.entities.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : ViewModel() {

    var selectUI: String = Constants.DEFAULT_MODE
    var currentList: List<Note> = listOf()

    private val repository by lazy {
        NoteRepository(application)
    }

    private val _noteLiveData = MutableLiveData<List<Note>>()
    val noteLiveData: LiveData<List<Note>> get() = _noteLiveData

    private val _labelLiveData = MutableLiveData<List<Label>>()
    val labelLiveData: LiveData<List<Label>> get() = _labelLiveData

    fun getAllNote() {
        viewModelScope.launch {
            repository.getNotes().catch {
                _noteLiveData.value = listOf()
            }.collect {
                currentList = it
                _noteLiveData.value = it
            }
        }
    }

    fun insertNote(note: Note): LiveData<Long> {
        val lastId = MutableLiveData<Long>()
        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.insertNote(note)
            lastId.postValue(id)
        }
        return lastId
    }

    fun searchNotes(searchWord: String) {
        viewModelScope.launch {
            if (searchWord.length >= 2) {
                repository.searchNotes(searchWord).catch {
                    _noteLiveData.value = listOf()
                }.collect {
                    _noteLiveData.value = it
                }
            } else {
                _noteLiveData.value = currentList
            }
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    fun getAllLabel() {
        viewModelScope.launch {
            repository.getLabels().catch {
                _labelLiveData.value = listOf()
            }.collect {
                _labelLiveData.value = it
            }
        }
    }

    fun searchLabel(keyword: String) {
        viewModelScope.launch {
            repository.searchLabel(keyword).catch {
                _labelLiveData.value = listOf()
            }.collect {
                if (it.isNotEmpty()) {
                    _labelLiveData.value = it
                } else {
                    _labelLiveData.value = listOf(Label(keyword))
                }
            }
        }
    }

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
}