package com.isafemobile.test.sensor.heartrate.main

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler

class DatabaseManager private constructor() {
    private var hrTime: Long = 0L

    private var mHrObserver: HRObserver? = null
    private var mContext: Context? = null

    fun setContext(context: Context?) {
        if (mContext == null) mContext = context
    }

    fun registerHRObserver(handler: Handler?) {
        mHrObserver = HRObserver(handler)
        mContext!!.contentResolver.registerContentObserver(
            MetaData.HR_TABLE_URI, true,
            mHrObserver!!
        )
    }

    fun unregisterHRObserver() {
        if (mHrObserver == null) return
        mContext!!.contentResolver.unregisterContentObserver(mHrObserver!!)
    }

    @SuppressLint("Range")
    fun loadHR(): Int {
        val resolver = mContext!!.contentResolver

        val cursor = resolver.query(MetaData.HR_TABLE_URI, null, null, null, null)
        var current = 0
        if (cursor == null) {
            return current
        }
        if (cursor.moveToFirst()) {
            current = cursor.getInt(cursor.getColumnIndex(MetaData.HR.CURRENT))
            hrTime = cursor.getString(cursor.getColumnIndex(MetaData.HR.UPDATE_TIME)).toLong()
        }
        cursor.close()
        return current
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var mDatabaseInstance: DatabaseManager? = null
        val instance: DatabaseManager
            get() {
                if (mDatabaseInstance == null) {
                    synchronized(DatabaseManager::class.java) {
                        mDatabaseInstance = DatabaseManager()
                    }
                }
                return mDatabaseInstance!!
            }
    }
}
