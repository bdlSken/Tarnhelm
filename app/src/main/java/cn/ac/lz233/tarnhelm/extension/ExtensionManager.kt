package cn.ac.lz233.tarnhelm.extension

import android.os.Build
import android.util.Log
import cn.ac.lz233.tarnhelm.App
import cn.ac.lz233.tarnhelm.extension.api.ITarnhelmExt
import cn.ac.lz233.tarnhelm.extension.exception.InvalidExtensionException
import cn.ac.lz233.tarnhelm.util.ktx.getExtPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.math.BigInteger
import java.security.MessageDigest

object ExtensionManager {

    private val mExtensionManagerService by lazy { ExtensionManagerService(App.context) }

    fun init() {
        mExtensionManagerService.init()
        Log.d("ExtensionManager", "Total installed extension: ${mExtensionManagerService.getInstalledExtensions().size}")
    }

    fun getInstalledExtensions() = mExtensionManagerService.getInstalledExtensions()

    fun getRunningExtensions() = mExtensionManagerService.getRunningExtensions()

    @Throws(InvalidExtensionException::class)
    suspend fun installExtension(stream: InputStream) = withContext(Dispatchers.IO) {
        val byteArrays = stream.readBytes()
        stream.close()
        val classLoader = MemoryDexLoader.createClassLoaderWithDex(byteArrays, mExtensionManagerService.extensionClassLoaderParent)
        val extInfo = findExtensionInfo(classLoader)!!
        Log.d("ExtensionManager", "Extension info found: $extInfo, extension id: ${extInfo.id()}")

        validateExtInfo(extInfo)

        val extId = extInfo.id()
        if (mExtensionManagerService.getInstalledExtensions().any { it.id == extId }) {
            val existing = mExtensionManagerService.getInstalledExtensions().first { it.id == extId }
            mExtensionManagerService.uninstallExtension(existing)
        }

        val extPath = File(extInfo.getExtPath(App.context), "ext.dex")
        Log.d("ExtensionManager", "Installing extension (id:$extId) to $extPath")

        extPath.parentFile?.mkdirs()
        extPath.writeBytes(byteArrays)

        mExtensionManagerService.registerExtension(ExtensionRecord.fromExtInfo(extInfo))
        Log.d("ExtensionManager", "Extension (id:$extId) installed successfully")
    }

    private fun validateExtInfo(extInfo: ITarnhelmExt.ExtInfo) {
        val extId = extInfo.id()
        val range = CharRange('a', 'z') + CharRange('A', 'Z') + CharRange('0', '9') + '.'
        if (extId.any { it !in range } || extId.length > 255) {
            throw InvalidExtensionException("Invalid extension id(length:${extId.length}): $extId")
        }
        if (extInfo.minTarnhelmSdkVersion() > ExtensionManagerService.EXT_SDK_VERSION) {
            throw InvalidExtensionException("Extension requires Tarnhelm SDK ${extInfo.minTarnhelmSdkVersion()}, host is ${ExtensionManagerService.EXT_SDK_VERSION}")
        }
        if (extInfo.minAndroidSdkVersion() > Build.VERSION.SDK_INT) {
            throw InvalidExtensionException("Extension requires Android API ${extInfo.minAndroidSdkVersion()}, device is ${Build.VERSION.SDK_INT}")
        }
    }

    fun toMD5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

    private fun findExtensionInfo(classLoader: ClassLoader): ITarnhelmExt.ExtInfo? {
        try {
            val realEntry = ExtensionRecord.loadEntryClass(classLoader).getDeclaredConstructor().newInstance()
            return (realEntry as ITarnhelmExt).extensionInfo()
        } catch (e: Exception) {
            throw InvalidExtensionException("Loading invalid extension", e)
        }
    }

    @Throws(RuntimeException::class)
    fun uninstallExtension(extRecord: ExtensionRecord) {
        mExtensionManagerService.uninstallExtension(extRecord)
    }

    @Throws(RuntimeException::class)
    fun enableExtension(extRecord: ExtensionRecord) {
        mExtensionManagerService.enableExtension(extRecord)
    }

    @Throws(RuntimeException::class)
    fun disableExtension(extRecord: ExtensionRecord) {
        mExtensionManagerService.disableExtension(extRecord)
    }

    suspend fun requestHandleString(extRecord: ExtensionRecord, charSequence: CharSequence) = withContext(Dispatchers.IO) {
        mExtensionManagerService.requestHandleString(extRecord, charSequence)
    }

    fun handleStringBlocking(extRecord: ExtensionRecord, charSequence: CharSequence): String =
        runBlocking { requestHandleString(extRecord, charSequence) }

    fun applyExtensions(input: String): Pair<String, List<String>> {
        var result = input
        val applied = mutableListOf<String>()
        for (ext in getRunningExtensions()) {
            if (ext.regexes.isNotEmpty() && ext.regexes.none { runCatching { Regex(it).containsMatchIn(result) }.getOrDefault(false) }) continue
            result = handleStringBlocking(ext, result)
            applied.add(ext.name)
        }
        return result to applied
    }

    suspend fun requestCheckUpdate(extRecord: ExtensionRecord) = withContext(Dispatchers.IO) {
        mExtensionManagerService.requestCheckUpdate(extRecord)
    }
}