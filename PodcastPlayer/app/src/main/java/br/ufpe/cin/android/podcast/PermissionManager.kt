package br.ufpe.cin.android.podcast

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionManager(private val activity: AppCompatActivity) {

    companion object {

        private var INSTANCE: PermissionManager? = null

        fun create(activity: AppCompatActivity) {
            synchronized(PermissionManager::class) {
                INSTANCE = PermissionManager(activity)
            }
        }

        fun getInstance(): PermissionManager {
            return INSTANCE!!
        }

        private const val PERMISSION_REQUEST_CODE = 200

        val EXTERNAL_STORAGE = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

    }

    fun requestPermissions(permissions: Array<String>) {
        val listPermissionsNeeded = ArrayList<String>()

        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    activity.applicationContext,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                listPermissionsNeeded.add(permission)
            }
        }

        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                activity,
                listPermissionsNeeded.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        }
    }

}
