package cn.ac.lz233.tarnhelm.logic.module.meta

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SanitizationHistoryEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val originalUrl: String,
    val cleanedUrl: String,
    val removedParametersJson: String,
    val appliedRulesJson: String,
)
