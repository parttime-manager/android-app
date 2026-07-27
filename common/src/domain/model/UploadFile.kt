package com.example.ptmanagercommon.domain.model

data class UploadFile(val name: String, val contentType: String?, val bytes: ByteArray, val fieldName: String = "file")
