package cn.ac.lz233.tarnhelm.logic.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cn.ac.lz233.tarnhelm.logic.module.meta.SanitizationHistoryEntry

@Dao
interface SanitizationHistoryDao {
    @Insert
    fun insert(entry: SanitizationHistoryEntry)

    @Query("SELECT * FROM SanitizationHistoryEntry ORDER BY timestamp DESC")
    fun getAll(): List<SanitizationHistoryEntry>

    @Query("SELECT count(*) FROM SanitizationHistoryEntry")
    fun getCount(): Int

    @Query("DELETE FROM SanitizationHistoryEntry WHERE id NOT IN (SELECT id FROM SanitizationHistoryEntry ORDER BY timestamp DESC LIMIT :limit)")
    fun trimToLimit(limit: Int)
}
