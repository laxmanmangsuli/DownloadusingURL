package com.example

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.databinding.ItemDownloadedFileBinding
import com.kdownloader.KDownloader
import com.kdownloader.Status
import com.kdownloader.internal.DownloadRequest

class DownloadedFilesAdapter(private val context: Context) :
    RecyclerView.Adapter<DownloadedFilesAdapter.ViewHolder>() {

    private var downloadedFiles = mutableListOf<DownloadedFile>()

    fun addDownloadTask(task: DownloadedFile) {
        downloadedFiles.add(task)
        notifyItemInserted(downloadedFiles.size - 1)
    }

    class ViewHolder(val binding: ItemDownloadedFileBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDownloadedFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val kDownloader = KDownloader.create(context)
        val enqueue = downloadedFiles[position].request
        handleDownloadRequest(position, holder, enqueue, kDownloader)
    }

    override fun getItemCount(): Int {
        return downloadedFiles.size
    }

    private fun handleDownloadRequest(position: Int, holder: ViewHolder, enqueue: DownloadRequest, kDownloader: KDownloader) {
        var downloadId = downloadedFiles[position].id ?: 0
        holder.binding.fileName6.text = downloadedFiles[position].fileName
        holder.binding.startCancelButton6.setOnClickListener {
            if (holder.binding.startCancelButton6.text == "Start") {
                downloadId = kDownloader.enqueue(enqueue,
                    onStart = {
                        holder.binding.status6.text = "Started"
                        holder.binding.startCancelButton6.text = "Cancel"
                        holder.binding.resumePauseButton6.isEnabled = true
                        holder.binding.resumePauseButton6.visibility = View.VISIBLE
                        downloadedFiles[position].id = downloadId
                    },
                    onPause = {
                        holder.binding.status6.text = "Paused"
                    },
                    onProgress = {
                        holder.binding.status6.text = "In Progress"
                        holder.binding.progressBar6.progress = it
                        holder.binding.progressText6.text = "$it%"
                    },
                    onCompleted = {
                        holder.binding.status6.text = "Completed"
                        holder.binding.progressText6.text = "100%"
                        holder.binding.startCancelButton6.visibility = View.GONE
                        holder.binding.resumePauseButton6.visibility = View.GONE
                    },
                    onError = {
                        holder.binding.status6.text = "Error : $it"
                        holder.binding.resumePauseButton6.visibility = View.GONE
                        holder.binding.progressBar6.progress = 0
                        holder.binding.progressText6.text = "0%"
                    }
                )
            } else {
                kDownloader.cancel(downloadId)
                holder.binding.startCancelButton6.text = "Start"
            }
        }

        holder.binding.resumePauseButton6.setOnClickListener {
            if (kDownloader.status(downloadId) == Status.PAUSED) {
                holder.binding.resumePauseButton6.text = "Pause"
                kDownloader.resume(downloadId)
            } else {
                holder.binding.resumePauseButton6.text = "Resume"
                kDownloader.pause(downloadId)
            }
        }
    }
}
