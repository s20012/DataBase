package com.example.databasetest.customclass

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context?): SQLiteOpenHelper(context, DBNAME, null, version) {
    companion object {
        private const val DBNAME = "DBSample.sqlite"
        private const val version = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("create table memos (id integer primary key, title text, content text, day text)")
        }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }

    override fun onOpen(db: SQLiteDatabase?) {
        super.onOpen(db)
    }
}