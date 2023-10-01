package com.example.myapplicationloc;

import static android.content.Context.MODE_PRIVATE;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DB_Handler extends SQLiteOpenHelper {
    


    private static String TABLE_NAME="newsData";
    private static String ID="id";
    private static String BATTERY_PERCENTAGE="battery_percentage";
    private static String LOCATION="location";
    private static String TIME_STAMP="time_stamp";
    private static String INTERNET_CONNECTION="internet_connection";
    private static String BATTERY_CHARGING="battery_charging";



    public DB_Handler(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query="CREATE TABLE " + TABLE_NAME +"("
                +ID  + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                +BATTERY_PERCENTAGE + "TEXT,"
                +LOCATION  + "TEXT,"
                +TIME_STAMP + "TEXT,"
                +BATTERY_CHARGING + "TEXT,"
                +INTERNET_CONNECTION + "TEXT)";
        db.execSQL(query);
    }

    public void addData(String battery_percentage, String location, String time_stamp,String battery_charging,String internet_connection){

        SQLiteDatabase db=this.getWritableDatabase();


        ContentValues values=new ContentValues();

        values.put(BATTERY_PERCENTAGE,battery_percentage);
        values.put(LOCATION,location);
        values.put(TIME_STAMP,time_stamp);
        values.put(BATTERY_CHARGING,battery_charging);
        values.put(INTERNET_CONNECTION,internet_connection);

        db.insert(TABLE_NAME,null,values);
    }

    public String getDataFromSQL(int id){
        SQLiteDatabase db=this.getReadableDatabase();
        String battery_percentage = null;
        String battery_charging = null;
        String time_stamp = null;
        String location = null;
        String internet_connection = null;
        Cursor cursor=db.rawQuery(" SELECT * FROM " +TABLE_NAME,null);
        while (cursor.moveToNext()){


        }
        return battery_charging;
    }






    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }
}
