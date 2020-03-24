package com.project.myhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="MyHelperDB.db";
    public static final String TABLE_NAME="Messages";
    public static final String COL_1="title";
    public static final String COL_2="body";
    public static final String COL_3="dateTime";

    public static final String TABLE_NAME2="BlockedContacts";
    public static final String TABLE_NAME2_COL_1="PhNo";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table Messages " +
                        "(id integer primary key, title text,body text,dateTime text)"
        );

        db.execSQL(
                "create table BlockedContacts "+
                        "(PhNo Text primary key)"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Messages");
        db.execSQL("DROP TABLE IF EXISTS BlockedContacts");
        onCreate(db);
    }

    public void insertIntoMessages(String title, String body, String dateTime) {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("title",title);
        contentValues.put("body",body);
        contentValues.put("dateTime",dateTime);
        db.insert("Messages",null,contentValues);
    }

    public boolean insertIntoBlockedContacts(String PhNo) {

        SQLiteDatabase db=this.getWritableDatabase();
        String Query="Select * from BlockedContacts where PhNo = "+PhNo;
        Cursor cursor=db.rawQuery(Query,null);
        if(cursor.getCount()!=0){
            cursor.close();
            return false;
        }
        ContentValues contentValues=new ContentValues();
        contentValues.put("PhNo",PhNo);
        db.insert("BlockedContacts", null, contentValues);
        cursor.close();
        return true;
    }

    public void deleteIntoBlockedContacts(String number) {
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete("BlockedContacts", "PhNo = ? ", new String[] {number});
    }

    public Integer deleteIntoMessages(Integer id) {
        SQLiteDatabase db=this.getWritableDatabase();
        return db.delete("Messages",
                "id = ? ",
                new String[] { Integer.toString(id)});
    }

    public ArrayList<String>getAllMessages() {
        ArrayList<String>array_list=new ArrayList<String>();
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("select * from Messages",null);
        res.moveToFirst();

        while(!res.isAfterLast()){
            array_list.add(res.getString(res.getColumnIndex(COL_1)));
            array_list.add(res.getString(res.getColumnIndex(COL_2)));
            array_list.add(res.getString(res.getColumnIndex(COL_3)));
            res.moveToNext();
        }
        res.close();
        return array_list;
    }

    public ArrayList<String>getAllBlockedContacts() {
        ArrayList<String>array_list=new ArrayList<String>();
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("select * from BlockedContacts",null);
        res.moveToFirst();

        while(!res.isAfterLast()){
            array_list.add(res.getString(res.getColumnIndex(TABLE_NAME2_COL_1)));
            res.moveToNext();
        }
        res.close();
        return array_list;
    }

    public void deleteAllMessage() {
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from Messages");
    }
}
