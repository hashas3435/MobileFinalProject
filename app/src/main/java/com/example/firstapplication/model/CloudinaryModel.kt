package com.example.firstapplication.model

import android.graphics.Bitmap
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.cloudinary.android.policy.GlobalUploadPolicy
import com.cloudinary.android.policy.UploadPolicy
import com.example.firstapplication.BuildConfig
import com.example.firstapplication.base.MyApplication
import com.example.firstapplication.base.StringCallback
import com.example.firstapplication.utils.extensions.toFile

class CloudinaryModel {
    init {
        val config = mapOf(
            "cloud_name" to BuildConfig.CLOUDINARY_CLOUD_NAME,
            "api_key" to BuildConfig.CLOUDINARY_API_KEY,
            "api_secret" to BuildConfig.CLOUDINARY_API_SECRET
        )

        MyApplication.Globals.context?.let {
            MediaManager.init(it, config)
            MediaManager.get().globalUploadPolicy = GlobalUploadPolicy.Builder()
                .maxConcurrentRequests(3)
                .networkPolicy(UploadPolicy.NetworkType.UNMETERED)
                .build()
        }
    }

    fun uploadBitmap(bitmap: Bitmap, name: String, callback: StringCallback) {
        val context = MyApplication.Globals.context ?: return
        val file = bitmap.toFile(context, name)

        MediaManager.get().upload(file.path)
            .option(
                "folder",
                "images"
            ) // Optional: Specify a folder in your Cloudinary account
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {}

                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}

                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val publicUrl = resultData["secure_url"] as? String ?: ""
                    callback(publicUrl)
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    Log.e(
                        "CloudinaryModel",
                        "Failed to upload image $name { code: ${error?.code}, description: ${error?.description} }"
                    )
                    callback(null)
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
            })
            .dispatch()
    }
}
