package com.example

import com.kdownloader.internal.DownloadRequest

data class DownloadedFile(
    val url: String,
    var id: Int?= null,
    var request: DownloadRequest,
    val fileName: String,

)


