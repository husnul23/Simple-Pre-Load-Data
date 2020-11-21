package com.example.simplepreloaddata.services

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import com.example.simplepreloaddata.R
import com.example.simplepreloaddata.database.MahasiswaHelper
import com.example.simplepreloaddata.model.MahasiswaModel
import com.example.simplepreloaddata.pref.AppPreference
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception

class DataManagerService : Service() {

    private val TAG = DataManagerService::class.java.simpleName
    private var mActivityMessenger: Messenger? = null

    companion object {
        const val PREPARATION_MESSAGE = 0
        const val UPDATE_MESSAGE = 1
        const val SUCCESS_MESSAGE = 2
        const val FAILED_MESSAGE = 3
        const val CANCEL_MESSAGE = 4
        const val ACTIVITY_HANDLER = "activity_handler"
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    private fun getData(): Boolean {
        val mahasiswaHelper = MahasiswaHelper.getInstance(applicationContext)
        val appPreference = AppPreference(applicationContext)
        val firstRun = appPreference.firstRun as Boolean
        if (firstRun) {

            val mahasiswaModels = preLoadRaw()
            mahasiswaHelper.open()
            var progress = 30.0
            publishProgress(progress.toInt())
            val progressMaxInsert = 80.0
            val progressDiff = (progressMaxInsert - progress) / mahasiswaModels.size
            var isInsertSuccess: Boolean

            // Gunakan ini untuk insert query dengan menggunakan standar query
            try {
                for (model in mahasiswaModels) {
                    mahasiswaHelper.insert(model)
                    progress += progressDiff
                    publishProgress(progress.toInt())
                }
                isInsertSuccess = true
                appPreference.firstRun = false
            } catch (e: Exception) {
                Log.e(TAG, "doInBackground: Exception")
                isInsertSuccess = false
            }

            // akhir dari standar query
            mahasiswaHelper.close()
            publishProgress(MAX_PROGRESS.toInt())
            return isInsertSuccess
        } else {
            try {
                synchronized(this) {
                    publishProgress(50)
                    publishProgress(MAX_PROGRESS.toInt())
                    return true
                }
            } catch (e: Exception) {
                return false
            }
        }
    }
    private fun publishProgress(progress: Int) {
        try {
            val message = Message.obtain(null, UPDATE_MESSAGE)
            val bundle = Bundle()
            bundle.putLong("KEY_PROGRESS", progress.toLong())
            message.data = bundle
            mActivityMessenger?.send(message)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }
    private fun preLoadRaw(): ArrayList<MahasiswaModel> {
        val mahasiswaModels = ArrayList<MahasiswaModel>()
        var line: String?
        val reader: BufferedReader
        try {
            val rawText = resources.openRawResource(R.raw.data_mahasiswa)
            reader = BufferedReader(InputStreamReader(rawText))
            do {
                line = reader.readLine()
                val splitstr = line.split("\t".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val mahasiswaModel: MahasiswaModel
                mahasiswaModel = MahasiswaModel()
                mahasiswaModel.name = splitstr[0]
                mahasiswaModel.nim = splitstr[1]
                mahasiswaModels.add(mahasiswaModel)
            } while (line != null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mahasiswaModels
    }
}
