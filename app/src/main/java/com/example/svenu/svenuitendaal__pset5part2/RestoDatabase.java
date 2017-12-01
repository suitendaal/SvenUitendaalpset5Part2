package com.example.svenu.svenuitendaal__pset5part2;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by svenu on 29-11-2017.
 */

public class RestoDatabase extends SQLiteOpenHelper {
    private static RestoDatabase instance;

    private static final String TABLE_NAME = "resto";
    public static final String COL1 = "_id";
    public static final String COL2 = "name";
    public static final String COL3 = "price";
    public static final String COL4 = "amount_ordered";
    public static final String COL5 = "imageUrl";

    private RestoDatabase(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "create table " + TABLE_NAME + " (" + COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL2 + " TEXT, " + COL3 + " FLOAT, " + COL4 + " INTEGER, " + COL5 + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public static RestoDatabase getInstance(Context context) {
        if(instance == null) {
            // call the private constructor
            instance = new RestoDatabase(context);
        }
        return instance;
    }

    public Cursor selectAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public void delete(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + COL1 + " = " + id + ";";
        db.execSQL(query);
    }

    public boolean addItem(String name, float price, String imageUrl, int amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL2 + " = '" + name + "';";
        Cursor data = db.rawQuery(query, null);

        if (data.getCount() > 0) {
            String new_query = "UPDATE " + TABLE_NAME + " SET " + COL4 + " = " + COL4 + " + " + amount + " WHERE " + COL2 + " = '" + name + "';";
            db.execSQL(new_query);
            return true;
        }

        else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL2, name);
            contentValues.put(COL3, price);
            contentValues.put(COL4, 1);
            contentValues.put(COL5, imageUrl);

            long result = db.insert(TABLE_NAME, null, contentValues);

            if (result == -1) {
                return false;
            } else {
                return true;
            }
        }
    }

    public boolean addItem(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + COL4 + " = " + COL4 + " + 1 WHERE " + COL1 + " = '" + id + "';";
        db.execSQL(query);
        return true;
    }

    public void clear() {
        SQLiteDatabase db = this.getWritableDatabase();
        onUpgrade(db, 0, 0);
    }

    public void deleteOneItem(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL1 + " = " + id + ";";
        Cursor data = db.rawQuery(query, null);

        int amount = data.getInt(data.getColumnIndex(COL4));

        if (amount == 1) {
            String new_query = "DELETE FROM " + TABLE_NAME + " WHERE " + COL1 + " = " + id;
            db.execSQL(new_query, null);
        }
        else {
            String new_query = "UPDATE " + TABLE_NAME + " SET " + COL4 + " = " + COL4 + " - 1 WHERE " + COL1 + " = " + id;
            db.execSQL(new_query, null);
        }
    }
}
