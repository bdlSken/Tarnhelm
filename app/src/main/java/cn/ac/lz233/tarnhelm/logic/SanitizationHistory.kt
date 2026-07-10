package cn.ac.lz233.tarnhelm.logic

import cn.ac.lz233.tarnhelm.App
import cn.ac.lz233.tarnhelm.logic.module.meta.SanitizationHistoryEntry
import org.json.JSONArray

object SanitizationHistory {
    private const val MAX_ENTRIES = 200

    fun record(originalUrl: String, cleanedUrl: String, removedParameters: List<String>, appliedRules: List<String>) {
        if (originalUrl == cleanedUrl) return
        App.sanitizationHistoryDao.insert(
            SanitizationHistoryEntry(
                timestamp = System.currentTimeMillis(),
                originalUrl = originalUrl,
                cleanedUrl = cleanedUrl,
                removedParametersJson = JSONArray(removedParameters).toString(),
                appliedRulesJson = JSONArray(appliedRules).toString(),
            )
        )
        App.sanitizationHistoryDao.trimToLimit(MAX_ENTRIES)
    }
}
