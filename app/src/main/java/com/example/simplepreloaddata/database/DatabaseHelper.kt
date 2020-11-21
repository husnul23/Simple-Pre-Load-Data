package com.example.simplepreloaddata.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns._ID
import com.example.simplepreloaddata.database.DatabaseContract.MahasiswaColumns.Companion.NAMA
import com.example.simplepreloaddata.database.DatabaseContract.MahasiswaColumns.Companion.NIM
import com.example.simplepreloaddata.database.DatabaseContract.TABLE_NAME

internal class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "dbmahasiswa"
        private const val DATABASE_VERSION = 1
        private val CREATE_TABLE_MAHASISWA =
            "create table $TABLE_NAME ($_ID integer primary key autoincrement, $NAMA text not null, $NIM text not null);"

    }

    override fun onCreate(p0: SQLiteDatabase) {
        p0.execSQL(CREATE_TABLE_MAHASISWA)
    }

    override fun onUpgrade(p0: SQLiteDatabase, p1: Int, p2: Int) {
        p0.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(p0)
    }
}