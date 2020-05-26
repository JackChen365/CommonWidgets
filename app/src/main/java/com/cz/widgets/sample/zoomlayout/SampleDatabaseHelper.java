package com.cz.widgets.sample.zoomlayout;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by cz
 * @date 2020-03-19 13:35
 * @email bingo110@126.com
 * An sample database helper.
 */
public class SampleDatabaseHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME="sample_text";
    public static final String CREATE_SQL = "create table "+TABLE_NAME+"(" +
            "_id integer primary key autoincrement, " +
            "log text)";

    public SampleDatabaseHelper(Context context) {
        super(context, "test.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void delete(){
        try(SQLiteDatabase writableDatabase = getWritableDatabase()){
            writableDatabase.execSQL("delete from "+TABLE_NAME);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}