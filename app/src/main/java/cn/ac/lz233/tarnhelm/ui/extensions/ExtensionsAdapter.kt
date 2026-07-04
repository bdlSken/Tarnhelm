package cn.ac.lz233.tarnhelm.ui.extensions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import cn.ac.lz233.tarnhelm.R
import cn.ac.lz233.tarnhelm.extension.ExtensionManager
import cn.ac.lz233.tarnhelm.extension.ExtensionRecord
import cn.ac.lz233.tarnhelm.util.ktx.openUrl
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.materialswitch.MaterialSwitch

class ExtensionsAdapter(
    private val extensions: MutableList<ExtensionRecord>,
    private val onChanged: () -> Unit,
) : RecyclerView.Adapter<ExtensionsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: MaterialCardView = view.findViewById(R.id.extensionContentCardView)
        val enableSwitch: MaterialSwitch = view.findViewById(R.id.extensionEnableSwitch)
        val nameText: AppCompatTextView = view.findViewById(R.id.nameContentTextView)
        val descriptionText: AppCompatTextView = view.findViewById(R.id.descriptionContentTextView)
        val regexesText: AppCompatTextView = view.findViewById(R.id.regexesContentTextView)
        val authorText: AppCompatTextView = view.findViewById(R.id.authorContentTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_extension, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ext = extensions[position]
        holder.nameText.text = ext.name
        holder.descriptionText.text = ext.description ?: "—"
        holder.regexesText.text = if (ext.regexes.isEmpty()) "—" else ext.regexes.joinToString("\n")
        holder.authorText.text = ext.author ?: "—"
        holder.enableSwitch.setOnCheckedChangeListener(null)
        holder.enableSwitch.isChecked = ext.enabled
        holder.enableSwitch.setOnCheckedChangeListener { _, checked ->
            runCatching {
                if (checked) ExtensionManager.enableExtension(ext) else ExtensionManager.disableExtension(ext)
                ext.enabled = checked
            }.onFailure {
                holder.enableSwitch.isChecked = !checked
            }
            onChanged()
        }
        holder.card.setOnClickListener {
            MaterialAlertDialogBuilder(holder.itemView.context)
                .setTitle(ext.name)
                .setMessage(ext.description)
                .setPositiveButton(R.string.extensionsCheckUpdate) { _, _ ->
                    ext.extensionURL?.openUrl()
                }
                .setNeutralButton(R.string.extensionsUninstall) { _, _ ->
                    val index = extensions.indexOfFirst { it.id == ext.id }
                    if (index >= 0) {
                        ExtensionManager.uninstallExtension(ext)
                        extensions.removeAt(index)
                        notifyItemRemoved(index)
                        onChanged()
                    }
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        }
    }

    override fun getItemCount() = extensions.size

    fun refresh(list: List<ExtensionRecord>) {
        extensions.clear()
        extensions.addAll(list)
        notifyDataSetChanged()
    }
}
