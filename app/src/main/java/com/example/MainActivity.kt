package com.example

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.databinding.ActivityMainBinding
import com.kdownloader.KDownloader

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    val REQUEST_WRITE_PERMISSION = 101
    val STORAGE_PERMISSION_REQUEST_CODE = 10
    private lateinit var kDownloader: KDownloader
    private lateinit var dirPath: String
    private var urlCounterMap: MutableMap<String, Int> = mutableMapOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        kDownloader = KDownloader.create(this)

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        }
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )
        }

        val itemList = mutableListOf<DownloadedFile>()

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = DownloadedFilesAdapter(this)
        recyclerView.adapter = adapter





        binding.btnDownload.setOnClickListener {
            val url = binding.url.text.toString().trim()

            dirPath = Environment.getExternalStorageDirectory().path + "/Download"
            val uri = Uri.parse(url)
            var fileName = uri.lastPathSegment ?: "defaultFileName"
            val counter = urlCounterMap[url] ?: 0
            fileName = if (counter!! >= 1) {
                "${
                    uri.lastPathSegment?.substringBeforeLast(
                        '.',
                        ""
                    )
                }($counter).${fileName.substringAfterLast('.', "")}"
            } else {
                "${uri.lastPathSegment?.substringBeforeLast('.', "")}.${
                    fileName.substringAfterLast(
                        '.',
                        ""
                    )
                }"
            }

            val requestBuilder = when (fileName.substringAfterLast('.', "").toLowerCase()) {
                "mp4" -> kDownloader.newRequestBuilder(url, dirPath, fileName)
                "bin" -> kDownloader.newRequestBuilder(url, dirPath, fileName)
                "jpg", "png", "jpeg" -> kDownloader.newRequestBuilder(url, dirPath, fileName)
                "pdf" -> kDownloader.newRequestBuilder(url, dirPath, fileName)
                "exe" -> kDownloader.newRequestBuilder(url, dirPath, fileName)
                "gif" -> kDownloader.newRequestBuilder(url, dirPath, fileName)
                "mp3" -> kDownloader.newRequestBuilder(url, dirPath, fileName)
                else -> {
                    Toast.makeText(this, "Unsupported file type", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }.build()
            itemList.add(DownloadedFile(url=url, request = requestBuilder, fileName = fileName))
            adapter.addDownloadTask(DownloadedFile(url=url, request = requestBuilder, fileName = fileName))
            binding.url.text.clear()

        }
        requestPermission()
    }




    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_WRITE_PERMISSION
            )
        } else {

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            STORAGE_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && permissions[0] == android.Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    }
                }
            }

            else -> {

            }
        }
    }
}

