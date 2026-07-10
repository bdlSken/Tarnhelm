package cn.ac.lz233.tarnhelm.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import cn.ac.lz233.tarnhelm.R
import cn.ac.lz233.tarnhelm.logic.module.meta.SanitizationHistoryEntry
import cn.ac.lz233.tarnhelm.util.ktx.getString
import org.json.JSONArray
import java.text.DateFormat
import java.util.Date

class SanitizationHistoryAdapter(
    private val entries: List<SanitizationHistoryEntry>,
) : RecyclerView.Adapter<SanitizationHistoryAdapter.ViewHolder>() {

    private val dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val timestampTextView: AppCompatTextView = view.findViewById(R.id.timestampTextView)
        val originalUrlTextView: AppCompatTextView = view.findViewById(R.id.originalUrlTextView)
        val cleanedUrlTextView: AppCompatTextView = view.findViewById(R.id.cleanedUrlTextView)
        val removedParametersTextView: AppCompatTextView = view.findViewById(R.id.removedParametersTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sanitization_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]
        holder.timestampTextView.text = dateFormat.format(Date(entry.timestamp))
        holder.originalUrlTextView.text = entry.originalUrl
        holder.cleanedUrlTextView.text = entry.cleanedUrl
        val removed = JSONArray(entry.removedParametersJson).let { array ->
            buildList {
                for (i in 0 until array.length()) add(array.getString(i))
            }
        }
        holder.removedParametersTextView.text = if (removed.isEmpty()) {
            R.string.historyItemNoParametersRemoved.getString()
        } else {
            removed.joinToString(", ")
        }
    }

    override fun getItemCount() = entries.size
}
