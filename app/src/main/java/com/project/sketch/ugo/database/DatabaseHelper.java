package com.project.sketch.ugo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.project.sketch.ugo.utils.Constants;

import java.util.ArrayList;

/**
 * Created by Developer on 2/1/18.
 */

public class DatabaseHelper extends SQLiteOpenHelper{

    // initialize the database name...
    private static final String DATABASE_NAME = "CAINCABS.db";
    // initialize the database version...
    private static final int DATABASE_VERSION = 1;




    public static final String TABLE_FAVOURITE = "TABLE_FAVOURITE";

    public static final String ID = "ID";
    public static final String TYPE = "TYPE";
    public static final String TITLE = "TITLE";
    public static final String ADDRESS = "ADDRESS";
    public static final String LATITUDE = "LATITUDE";
    public static final String LONGITUDE = "LONGITUDE";







    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE_FAVOURITE = "create table if not exists " + TABLE_FAVOURITE + " ( "
                + ID + " integer primary key autoincrement, "
                + TYPE + " text not null, "
                + TITLE + " text not null, "
                + ADDRESS + " text not null, "
                + LATITUDE + " text not null, "
                + LONGITUDE + " text not null);";

        db.execSQL(CREATE_TABLE_FAVOURITE.trim());
        System.out.println(CREATE_TABLE_FAVOURITE);

        Log.d(Constants.TAG, "CREATE_TABLE_FAVOURITE  > "+CREATE_TABLE_FAVOURITE);


    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITE);

        onCreate(db);
    }


///////////////////////////////////////////////////////////////////////


    public void insertContactData(FavDataModel data) {

        SQLiteDatabase dbase = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.TYPE, data.getTYPE());
        values.put(DatabaseHelper.TITLE, data.getTITLE());
        values.put(DatabaseHelper.ADDRESS, data.getADDRESS());
        values.put(DatabaseHelper.LATITUDE, data.getLATITUDE());
        values.put(DatabaseHelper.LONGITUDE, data.getLONGITUDE());

        dbase.insert(TABLE_FAVOURITE, null, values);
        dbase.close();

        Log.d(Constants.TAG, "TABLE_FAVOURITE inserted value");

    }


    public ArrayList<FavDataModel> getFavAddress(){

        SQLiteDatabase dbase = this.getReadableDatabase();
        ArrayList<FavDataModel> dataArrayList = new ArrayList<>();
        FavDataModel favDataModel;

        Cursor cursor = dbase.query(TABLE_FAVOURITE, null, null, null, null, null, null);

        Log.d(Constants.TAG, "cursor.getCount()  > "+cursor.getCount());

        while (cursor.moveToNext()){

            String ID = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ID));
            String TYPE = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TYPE));
            String TITLE = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TITLE));
            String ADDRESS = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ADDRESS));
            String LATITUDE = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LATITUDE));
            String LONGITUDE = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LONGITUDE));

            favDataModel = new FavDataModel();

            favDataModel.setID(ID);
            favDataModel.setTYPE(TYPE);
            favDataModel.setTITLE(TITLE);
            favDataModel.setADDRESS(ADDRESS);
            favDataModel.setLATITUDE(LATITUDE);
            favDataModel.setLONGITUDE(LONGITUDE);

            dataArrayList.add(favDataModel);

        }
        cursor.close();
        dbase.close();

        Log.d(Constants.TAG, "TABLE_FAVOURITE fetching value");

        return dataArrayList;

    }


    public void deleteDataUsingId(String id){
        SQLiteDatabase dbase = this.getReadableDatabase();
        //WHERE clause
        String selection1 = DatabaseHelper.ID + " = ?" ;
        //WHERE clause arguments
        String[] selectionArgs1 = {id};

        dbase.delete(DatabaseHelper.TABLE_FAVOURITE, selection1, selectionArgs1);
        dbase.close();

        Log.d(Constants.TAG, "Delete successful");

    }


}
