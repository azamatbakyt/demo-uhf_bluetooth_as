package com.example.uhf_bt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.uhf_bt.Models.TagData;

import java.util.ArrayList;
import java.util.List;

public class DataBase extends SQLiteOpenHelper {

    private static final String db_name = "rfidCounter";
    private static final int db_version = 11;
    private static final String db_table = "marking";

    //columns
    private static final String db_id = "ID";
    private static final String db_epc = "epc";
    private static final String db_type = "Type";
    private static final String db_description = "description";
    private static final String db_inventory_number = "inventory_number";
    private static final String db_nomenclature = "nomenclature";
    private final static String db_amount = "Amount";

    // Table #2
    private final static String db_table_inventory = "inventory_table";
    private final static String db_executor = "executor";
    private final static String db_facility = "facility";
    private final static String db_premise = "premise";
    private final static String db_date = "date";

    private final static String db_table_relations = "object_room_relations";
    private final static String db_object_id = "object_id";
    private final static String db_room_id = "room_id";


    public DataBase(@Nullable Context context) {
        super(context, db_name, null, db_version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String query = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s INTEGER);",
                    db_table, db_id, db_epc, db_type, db_description, db_inventory_number, db_nomenclature, db_amount);
            String queryInventory = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT," +
                            " %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s INTEGER, %s TEXT, %s TEXT, %s TEXT, %s TEXT)",
                    db_table_inventory, db_id, db_epc, db_type, db_description, db_inventory_number, db_nomenclature, db_amount,
                    db_facility, db_premise, db_date, db_executor);
            String queryRelations = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s INTEGER);",
                    db_table_relations, db_id, db_object_id, db_room_id);
            db.execSQL(query);
            db.execSQL(queryInventory);
            db.execSQL(queryRelations);
            Log.d("DataBase", "onCreate called");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = String.format("DROP TABLE IF EXISTS %s", db_table);
        db.execSQL(query);
        onCreate(db);
    }

    public boolean insertEPC(TagData tagData, String epc) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(db_epc, epc);
        values.put(db_type, "EPC");
        values.put(db_description, tagData.getDescription());
        values.put(db_inventory_number, tagData.getInventoryNumber());
        values.put(db_nomenclature, tagData.getNomenclature());
        values.put(db_amount, "1");

        long result = db.insert(db_table, null, values);
        long result2 = db.insert(db_table_inventory, null, values);
        db.close();
        if (result == -1 && result2 == -1)
            return false;
        else
            return true;

    }


    public List<TagData> getDataByEpc(String epc) {
        List<TagData> tagDataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT description, inventory_number, nomenclature FROM " + db_table + " WHERE " + db_epc + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{epc});

        if (cursor.moveToFirst()) {
            do {
                String description = cursor.getString(cursor.getColumnIndex(db_description));
                String inventory_number = cursor.getString(cursor.getColumnIndex(db_inventory_number));
                String nomenclature = cursor.getString(cursor.getColumnIndex(db_nomenclature));
                TagData tagData = new TagData(description, inventory_number, nomenclature);
                tagDataList.add(tagData);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return tagDataList;
    }


    public List<TagData> getAll() {
        List<TagData> data = new ArrayList<>();
        String select = "SELECT * FROM " + db_table_inventory;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(select, null);
        if (cursor.moveToFirst()) {
            do {
                TagData tag = new TagData(
                        "" + cursor.getString(cursor.getColumnIndex(db_id)),
                        "" + cursor.getString(cursor.getColumnIndex(db_epc)),
                        "" + cursor.getString(cursor.getColumnIndex(db_type)),
                        "" + cursor.getString(cursor.getColumnIndex(db_description)),
                        "" + cursor.getString(cursor.getColumnIndex(db_inventory_number)),
                        "" + cursor.getString(cursor.getColumnIndex(db_nomenclature)),
                        1,
                        "" + cursor.getString(cursor.getColumnIndex(db_facility)),
                        "" + cursor.getString(cursor.getColumnIndex(db_premise)),
                        "" + cursor.getString(cursor.getColumnIndex(db_date)),
                        "" + cursor.getString(cursor.getColumnIndex(db_executor))
                );
                // add tag to list
                data.add(tag);

            } while (cursor.moveToNext());
        }
        db.close();
        return data;
    }

    public boolean updateData(TagData tagData, String epc) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(db_description, tagData.getDescription());
        values.put(db_inventory_number, tagData.getInventoryNumber());
        values.put(db_nomenclature, tagData.getNomenclature());

        String whereClause = db_epc + "=?";
        String[] whereArgs = {epc};

        int result = db.update(db_table, values, whereClause, whereArgs);

        db.close();

        return result > 0;
    }

    public List<TagData> get1C() {
        List<TagData> data = new ArrayList<>();
        String select = "SELECT * FROM " + db_table;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(select, null);
        if (cursor.moveToFirst()) {
            do {
                TagData tag = new TagData(
                        "" + cursor.getString(cursor.getColumnIndex(db_nomenclature)),
                        "" + cursor.getString(cursor.getColumnIndex(db_epc)),
                        "" + cursor.getString(cursor.getColumnIndex(db_description)),
                        "" + cursor.getString(cursor.getColumnIndex(db_type)),
                        1
                );
                // add tag to list
                data.add(tag);

            } while (cursor.moveToNext());
        }
        db.close();
        return data;
    }

    public boolean insertUser(TagData tagData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(db_executor, tagData.getExecutor());
        values.put(db_facility, tagData.getFacility());
        values.put(db_premise, tagData.getPremise());
        long result = db.insert(db_table_inventory, null, values);
        db.close();
        if (result == -1)
            return false;
        else
            return true;
    }

    public List<String> getFacilities(){
        List<String> facilities = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String select = "SELECT " + db_facility + " FROM " + db_table_inventory;
        Cursor cursor = db.rawQuery(select, null);
        if (cursor.moveToFirst()) {
            do {
                String facilityName = cursor.getString(cursor.getColumnIndex(db_facility));
                facilities.add(facilityName);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return facilities;
    }

    public List<String> getPremises(){
        List<String> premises = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String select = "SELECT " + db_premise + " FROM " + db_table_inventory;
        Cursor cursor = db.rawQuery(select, null);
        if (cursor.moveToFirst()) {
            do {
                String premiseName = cursor.getString(cursor.getColumnIndex(db_premise));
                premises.add(premiseName);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return premises;
    }

    public List<String> getExecutors(){
        List<String> executors = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String select = "SELECT " + db_executor + " FROM " + db_table_inventory;
        Cursor cursor = db.rawQuery(select, null);
        if (cursor.moveToFirst()){
            do {
                String executor = cursor.getString(cursor.getColumnIndex(db_executor));
                executors.add(executor);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return executors;
    }


    public List<Integer> getRoomsForObjectId(int objectId) {
        List<Integer> roomIds = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + db_room_id + " FROM " + db_table_relations + " WHERE " + db_object_id + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(objectId)});

        if (cursor.moveToFirst()) {
            do {
                int roomId = cursor.getInt(cursor.getColumnIndex(db_room_id));
                roomIds.add(roomId);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return roomIds;
    }

    public boolean insertObjectRoomRelation(int objectId, int roomId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(db_object_id, objectId);
        values.put(db_room_id, roomId);

        long result = db.insert(db_table_relations, null, values);
        db.close();

        return result != -1;
    }


}
