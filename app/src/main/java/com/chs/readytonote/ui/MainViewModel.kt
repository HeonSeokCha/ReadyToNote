package com.chs.readytonote.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.chs.readytonote.util.Constants
import com.chs.readytonote.entities.Label
import com.chs.readytonote.repository.NoteRepository
import com.chs.readytonote.entities.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : ViewModel() {

    var selectUI: String = Constants.DEFAULT_MODE
    var currentNoteList: List<Note> = listOf()
    var currentLabelList: ArrayList<Label> = arrayListOf()

    private val repository by lazy {
        NoteRepository(application)
    }

    private val _noteLiveData = MutableLiveData<List<Note>>()
    val noteLiveData: LiveData<List<Note>> get() = _noteLiveData

    private val _labelLiveData = MutableLiveData<List<Label>>()
    val labelLiveData: LiveData<List<Label>> get() = _labelLiveData

    fun getAllNote() {
        viewModelScope.launch {
            repository.getNotes().catch { e ->
                Log.e("NoteCatch", e.message.toString())
                _noteLiveData.value = listOf()
            }.collect {
                currentNoteList = it
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

    fun updateNote(note: Note) {
        viewModelScope.launch {
            repository.updateNote(note)
        }
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
                _noteLiveData.value = currentNoteList
            }
        }
    }

    fun checkMode(state: Boolean) {
        currentNoteList.forEach {
            it.showSelected = state
        }
        _noteLiveData.value = currentNoteList
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    fun checkDeleteNote(selectList: List<Int>) {
        viewModelScope.launch {
            selectList.forEach { notesId ->
                repository.deleteNote(currentNoteList.find { it.id == notesId }!!)
            }
        }
    }

    fun getAllLabel() {
        viewModelScope.launch {
            repository.getLabels().catch { e ->
                Log.e("LabelCatch", e.message.toString())
                _labelLiveData.value = listOf()
            }.collect {
                _labelLiveData.value = it
                currentLabelList.clear()
                currentLabelList.addAll(it)
            }
        }
    }

    fun searchLabel(keyword: String) {
        viewModelScope.launch {
            repository.searchLabel(keyword).catch { e ->
                Log.e("LabelSearchCatch", e.message.toString())
                _labelLiveData.value = listOf()
            }.collect {
                _labelLiveData.value = it
            }
        }
    }

    fun insertLabel(label: Label) = viewModelScope.launch {
        repository.insertLabel(label)
        currentLabelList.add(label)
        _labelLiveData.value = currentLabelList
    }
}