package com.example.todo;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String database = "task";
    private static final String table = "task_list";
    private SQLiteDatabase sqliteDatabase;
    DatabaseHelper(Context context) {
        super(context, database, null,1);
        sqliteDatabase = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + table + " (Id Integer PRIMARY KEY AUTOINCREMENT, taskCheck NUMBER, Tittle NUMBER, Time NUMBER, Date NUMBER, Position NUMBER)");
    }

    void insertIntoDatabase(String tittle, String time, String date) {
        @SuppressLint("Recycle") Cursor allRows  = sqliteDatabase.rawQuery("SELECT * FROM " + table, null);
        ContentValues values = new ContentValues();
        values.put("taskCheck", "");
        values.put("Tittle", tittle);
        values.put("Time", time);
        values.put("Date", date);
        values.put("Position", (allRows.getCount() + 1));
        sqliteDatabase.insert(table, null, values);
    }

    void deleteRow(int rowId) {
        sqliteDatabase.delete(table, "Position="+ (rowId + 1), null);
        @SuppressLint("Recycle") Cursor allRows  = sqliteDatabase.rawQuery("SELECT * FROM " + table, null);
        for(int i = rowId; i <= allRows.getCount(); i++){
            ContentValues values = new ContentValues();
            values.put("Position", i);
            sqliteDatabase.update(table, values, "Position="+ (i + 1), null);
        }
    }
    void taskCheckUpdate(int rowId, String checkBox) {
        ContentValues cv = new ContentValues();
        cv.put("taskCheck", checkBox);
        sqliteDatabase.update(table, cv, "Position="+ (rowId + 1), null);
    }
    Map selectAll() {
        @SuppressLint("Recycle") Cursor allRows  = sqliteDatabase.rawQuery("SELECT * FROM " + table, null);
        ArrayList<String> taskCheck = new ArrayList<>();
        ArrayList<String> tittle = new ArrayList<>();
        ArrayList<String> time = new ArrayList<>();
        ArrayList<String> date = new ArrayList<>();
        if (allRows.moveToFirst()){
            String[] columnNames = allRows.getColumnNames();
            do {
                int counter = 0;
                for (String name: columnNames) {
                    if(counter == 1) {
                        taskCheck.add(allRows.getString(allRows.getColumnIndex(name)));
                    } else if(counter == 2) {
                        tittle.add(allRows.getString(allRows.getColumnIndex(name)));
                    }else if(counter == 3) {
                        time.add(allRows.getString(allRows.getColumnIndex(name)));
                    }else if(counter == 4) {
                        date.add(allRows.getString(allRows.getColumnIndex(name)));
                    }
                    counter++;
                }
            } while (allRows.moveToNext());
        }
        HashMap<String, ArrayList<String>> map =new HashMap();
        map.put("taskCheck",taskCheck);
        map.put("Tittle",tittle);
        map.put("Time",time);
        map.put("Date",date);
        return map;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + table + "");
        onCreate(db);
    }
}
