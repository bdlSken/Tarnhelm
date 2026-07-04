package cn.ac.lz233.tarnhelm.ui.extensions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ac.lz233.tarnhelm.R
import cn.ac.lz233.tarnhelm.databinding.ActivityExtensionsBinding
import cn.ac.lz233.tarnhelm.extension.ExtensionManager
import cn.ac.lz233.tarnhelm.extension.exception.InvalidExtensionException
import cn.ac.lz233.tarnhelm.ui.SecondaryBaseActivity
import cn.ac.lz233.tarnhelm.util.ktx.getString
import cn.ac.lz233.tarnhelm.util.ktx.openUrl
import com.google.android.material.snackbar.Snackbar
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class ExtensionsActivity : SecondaryBaseActivity() {

    private val binding by lazy { ActivityExtensionsBinding.inflate(layoutInflater) }
    private val extensions = mutableListOf<cn.ac.lz233.tarnhelm.extension.ExtensionRecord>()
    private lateinit var adapter: ExtensionsAdapter

    private val onExtInstallExceptionHandler = CoroutineExceptionHandler { _, exception ->
        val message = when (exception) {
            is InvalidExtensionException -> exception.message ?: R.string.extensionsInstallFailedToast.getString()
            else -> R.string.extensionsInstallFailedToast.getString()
        }
        Snackbar.make(binding.root, message, Toast.LENGTH_LONG).show()
    }

    private val selectFileCallback = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val fileUri = result.data?.data ?: return@registerForActivityResult
            contentResolver.openInputStream(fileUri)?.let { stream ->
                launch(onExtInstallExceptionHandler) {
                    ExtensionManager.installExtension(stream)
                    Snackbar.make(binding.root, R.string.extensionsInstallSuccessToast, Toast.LENGTH_SHORT).show()
                    refreshList()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar = binding.toolbar
        setContentView(binding.root)
        setSupportActionBar(toolbar)

        adapter = ExtensionsAdapter(extensions) { refreshList() }
        binding.extensionsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.extensionsRecyclerView.adapter = adapter

        binding.openWebImageView.setOnClickListener {
            "https://tarnhelm.project.ac.cn".openUrl()
        }

        binding.importFab.setOnClickListener { startImport() }
        binding.importFab.setOnLongClickListener {
            launch(onExtInstallExceptionHandler) {
                assets.open("extensions/example.dex").use { ExtensionManager.installExtension(it) }
                Snackbar.make(binding.root, R.string.extensionsInstallSuccessToast, Toast.LENGTH_SHORT).show()
                refreshList()
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    private fun refreshList() {
        adapter.refresh(ExtensionManager.getInstalledExtensions())
        binding.extensionsRecyclerView.visibility = if (extensions.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun startImport() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) listOf() else listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        PermissionX.init(this)
            .permissions(permissions)
            .request { allGranted, _, _ ->
                if (allGranted) {
                    selectFileCallback.launch(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "*/*"
                    })
                } else {
                    Snackbar.make(binding.root, R.string.backupRequestPermissionFailedToast, Toast.LENGTH_SHORT).show()
                }
            }
    }

    companion object {
        fun actionStart(context: Context) = context.startActivity(Intent(context, ExtensionsActivity::class.java))
    }
}
