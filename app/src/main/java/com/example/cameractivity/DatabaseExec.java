package com.example.cameractivity;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

public class DatabaseExec {
    protected DataBaseHelper mDb;
    protected SQLiteDatabase database;

    public DatabaseExec(@NonNull DataBaseHelper mDb) {
        this.mDb = mDb;
    }

    public void deleteTable(String tableName) {
        mDb.createDataBase();
        database = mDb.getWritableDatabase();
        mDb.openDataBase();
        database.execSQL("DELETE FROM " + tableName);
        database.close();
    }

    public Cursor readShopList() {
        try {
            String sql = "SELECT * FROM ShopList";
            Cursor mCur = database.rawQuery(sql, null);
            mCur.getCount();//Log.e(TAG, "getVec: " + "Empty");
            return mCur;
        } catch (SQLException mSQLException) {
            mSQLException.printStackTrace();
        }
        return null;
    }
}
