package com.chs.readytonote.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.chs.readytonote.entities.Todo

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo WHERE note_id = :targetId")
    fun getAllTodo(targetId: Int): LiveData<MutableList<Todo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(note: Todo)

    @Delete()
    suspend fun deleteTodo(note: Todo)

    @Query("DELETE FROM todo")
    suspend fun deleteTodoAll() : Int
}