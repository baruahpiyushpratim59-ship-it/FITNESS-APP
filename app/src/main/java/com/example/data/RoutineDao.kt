package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.RoutineItem
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {
    @Query("SELECT * FROM routine_items WHERE dateString = :date ORDER BY id ASC")
    fun getRoutinesForDate(date: String): Flow<List<RoutineItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(item: RoutineItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutines(items: List<RoutineItem>)

    @Update
    suspend fun updateRoutine(item: RoutineItem)

    @Query("DELETE FROM routine_items WHERE id = :id")
    suspend fun deleteRoutineById(id: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM routine_items WHERE dateString = :date)")
    suspend fun hasRoutinesForDate(date: String): Boolean

    @Query("SELECT * FROM routine_items")
    fun getAllRoutines(): Flow<List<RoutineItem>>
}
