package cn.ac.lz233.tarnhelm

import android.content.Context
import android.util.Log
import cn.ac.lz233.tarnhelm.extension.ExtensionManager
import cn.ac.lz233.tarnhelm.util.ktx.insertToParameterRules
import cn.ac.lz233.tarnhelm.util.ktx.insertToRedirectRules
import cn.ac.lz233.tarnhelm.util.ktx.insertToRegexRules
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

object BundledContentSeeder {

    private const val TAG = "BundledContentSeeder"
    private const val SOURCE_BUNDLED = 2

    fun seedIfNeeded(context: Context) {
        seedRulesIfEmpty(context)
        seedExtensionsIfEmpty(context)
    }

    private fun seedRulesIfEmpty(context: Context) {
        val total = App.parameterRuleDao.getCount() + App.regexRuleDao.getCount() + App.redirectRuleDao.getCount()
        if (total > 0) return

        val json = context.assets.open("rules/default_rules.json").bufferedReader().use { it.readText() }
        val root = JSONObject(json)
        var count = 0
        root.getJSONArray("parameter").let { arr ->
            for (i in 0 until arr.length()) {
                arr.getJSONObject(i).insertToParameterRules(SOURCE_BUNDLED)
                count++
            }
        }
        root.getJSONArray("regex").let { arr ->
            for (i in 0 until arr.length()) {
                arr.getJSONObject(i).insertToRegexRules(SOURCE_BUNDLED)
                count++
            }
        }
        root.getJSONArray("redirect").let { arr ->
            for (i in 0 until arr.length()) {
                arr.getJSONObject(i).insertToRedirectRules(SOURCE_BUNDLED)
                count++
            }
        }
        Log.i(TAG, "Seeded $count bundled rules")
    }

    private fun seedExtensionsIfEmpty(context: Context) {
        if (ExtensionManager.getInstalledExtensions().isNotEmpty()) return
        runCatching {
            runBlocking {
                context.assets.open("extensions/example.dex").use { stream ->
                    ExtensionManager.installExtension(stream)
                }
            }
            Log.i(TAG, "Installed bundled sample extension")
        }.onFailure { t ->
            Log.e(TAG, "Bundled extension install failed; app will continue", t)
        }
    }
}
