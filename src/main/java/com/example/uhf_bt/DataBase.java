package com.example.uhf_bt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.uhf_bt.Models.Executor;
import com.example.uhf_bt.Models.Facility;
import com.example.uhf_bt.Models.FacilityAndPremiseRelations;
import com.example.uhf_bt.Models.Premise;
import com.example.uhf_bt.Models.TagData;

import java.util.ArrayList;
import java.util.List;

public class DataBase extends SQLiteOpenHelper {

    private static final String db_name = "rfidCounter";
    private static final int db_version = 21;
    private static final String db_table = "marking";

    //columns
    private static final String db_id = "ID";
    private static final String db_epc = "epc";
    private static final String db_type = "type";
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




    //
    private final static String db_table_relations = "object_room_relations";
    private final static String db_object_id = "object_id";
    private final static String db_room_id = "room_id";

    //user table
    private final static String dbUserTable = "executors";
    private final static String db_user_id = "id";
    private final static String db_user_role = "role";
    private final static String db_user_name = "name";
    private final static String db_user_note = "note";


    // facility table
    private final static String db_facility_table = "facilities";
    private final static String db_facility_id = "id";
    private final static String db_facility_name = "name";
    private final static String db_facility_address = "address";
    private final static String db_facility_note = "note";


    // premise table
    private final static String db_premise_table = "premises";
    private final static String db_premise_id = "id";
    private final static String db_premise_name = "name";
    private final static String db_premise_note = "note";


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
            String queryRelations = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER AUTOINCREMENT, %s INTEGER);",
                    db_table_relations, db_id, db_object_id, db_room_id);

            String queryFacility = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT, %s INTEGER);",
                    db_facility_table,
                    db_facility_id, db_facility_name, db_facility_address, db_facility_note, db_object_id);

            String queryPremise = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s INTEGER);",
                    db_premise_table,
                    db_premise_id, db_premise_name, db_premise_note, db_room_id);

            String queryUser = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT);",
                    dbUserTable,
                    db_user_id, db_user_role, db_user_name, db_user_note);

            db.execSQL(query);
            db.execSQL(queryUser);
            db.execSQL(queryFacility);
            db.execSQL(queryInventory);
            db.execSQL(queryRelations);
            db.execSQL(queryPremise);

            Log.d("DataBase", "onCreate called");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = String.format("DROP TABLE IF EXISTS %s", db_table);
        String queryUsers = String.format("DROP TABLE IF EXISTS %s", dbUserTable);
        String queryRelations = String.format("DROP TABLE IF EXISTS %s", db_table_relations);
        String queryFacility = String.format("DROP TABLE IF EXISTS %s", db_facility_table);
        String queryPremise = String.format("DROP TABLE IF EXISTS %s", db_premise_table);
        db.execSQL(query);
        db.execSQL(queryFacility);
        db.execSQL(queryPremise);
        db.execSQL(queryUsers);
        db.execSQL(queryRelations);
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

    public List<TagData> getDataByEpcForInventory(String epc) {
        List<TagData> tagDataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT type, description, inventory_number, nomenclature, amount, facility, premise, executor FROM "
                + db_table + " WHERE " + db_epc + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{epc});
        if (cursor.moveToFirst()) {
            do {
                String type = cursor.getString(cursor.getColumnIndex(db_type));
                String description = cursor.getString(cursor.getColumnIndex(db_description));
                String inventory_number = cursor.getString(cursor.getColumnIndex(db_inventory_number));
                String nomenclature = cursor.getString(cursor.getColumnIndex(db_nomenclature));
                String amount = cursor.getString(cursor.getColumnIndex(db_amount));
                String facility = cursor.getString(cursor.getColumnIndex(db_facility));
                String premise = cursor.getString(cursor.getColumnIndex(db_premise));
                String executor = cursor.getString(cursor.getColumnIndex(db_executor));

                TagData tagData = new TagData();
                tagData.setType(type);
                tagData.setDescription(description);
                tagData.setInventoryNumber(inventory_number);
                tagData.setNomenclature(nomenclature);
                tagData.setAmount(Integer.parseInt(amount));
                tagData.setFacility(facility);
                tagData.setPremise(premise);
                tagData.setExecutor(executor);

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

//    public boolean insertUser(Facility facility, Premise premise, FacilityAndPremiseRelations relations) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues valuesFacility = new ContentValues();
//        ContentValues valuesPremise = new ContentValues();
//        ContentValues valuesRelations = new ContentValues();
//
//        valuesFacility.put(db_facility_name, tagData.getExecutor());
//        valuesFacility.put(db_facility_description, tagData.getFacility());
//
//        valuesPremise.put(db_premise_name, );
//        valuesPremise.put(db_premise_description, );
//
//        valuesRelations.put(db_object_id, facility.getId());
//        valuesRelations.put(db_room_id, premise.getId());
//
//        long result2 = db.insert(db_table_relations, null, values);
//        db.close();
//        if (result == -1)
//            return false;
//        else
//            return true;
//    }

    public boolean insertUser(Executor executor) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valueUsers = new ContentValues();
        valueUsers.put(db_user_role, executor.getRole());
        valueUsers.put(db_user_name, executor.getName());
        valueUsers.put(db_user_note, executor.getNotation());

        ContentValues valuesForInventory = new ContentValues();
        valuesForInventory.put(db_executor, executor.getName());
        long result = db.insert(dbUserTable, null, valueUsers);
        long result2 = db.insert(db_table_inventory, null, valuesForInventory);

        if (result == -1 && result2 == -1)
            return false;
        else
            return true;
    }

    public List<String> getUsers() {
        List<String> executors = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String select = "SELECT " + db_user_name + " FROM " + dbUserTable;
        Cursor cursor = db.rawQuery(select, null);
        if (cursor.moveToFirst()) {
            do {
                String executor = cursor.getString(cursor.getColumnIndex(db_user_name));
                executors.add(executor);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return executors;
    }

    // facility settings
    public boolean insertFacility(Facility facility) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valuesFacility = new ContentValues();
        ContentValues valuesForRelations = new ContentValues();


        valuesFacility.put(db_facility_name, facility.getName());
        valuesFacility.put(db_facility_address, facility.getAddress());
        valuesFacility.put(db_facility_note, facility.getNote());

        valuesForRelations.put(db_object_id, facility.getId());

        ContentValues valuesForInventory = new ContentValues();
        valuesForInventory.put(db_facility, facility.getName());
        long result = db.insert(db_facility_table, null, valuesFacility);
        long result2 = db.insert(db_table_inventory, null, valuesForInventory);
        long result3 = db.insert(db_table_relations, null, valuesForRelations);
        if (result == -1 && result2 == -1)
            return false;
        else
            return true;
    }

    public List<String> getFacilities() {
        List<String> facilities = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String select = "SELECT " + db_facility_name + " FROM " + db_facility_table;
        Cursor cursor = db.rawQuery(select, null);
        if (cursor.moveToFirst()) {
            do {
                String facility = cursor.getString(cursor.getColumnIndex(db_facility_name));
                facilities.add(facility);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return facilities;
    }

    public boolean insertPremise(Premise premise) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valuesPremises = new ContentValues();
        ContentValues valuesForInventory = new ContentValues();
        ContentValues valuesForRelations = new ContentValues();
        premise.setId(Integer.parseInt(db_object_id));
        valuesPremises.put(db_premise_name, premise.getName());
        valuesPremises.put(db_premise_note, premise.getNote());

        valuesForRelations.put(db_room_id, premise.getId());

        valuesForInventory.put(db_premise, premise.getName());

        long result = db.insert(db_premise_table, null, valuesPremises);
        long result2 = db.insert(db_table_inventory, null, valuesForInventory);
        long result3 = db.insert(db_table_relations, null, valuesForRelations);
        if (result == -1 && result2 == -1)
            return false;
        else
            return true;

    }

    public List<String> getRoomsByObject(int objectId) {
        List<String> rooms = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + db_premise_name + " FROM " + db_premise_table +
                " WHERE " + db_premise_id + " IN (SELECT " + db_room_id + " FROM " + db_table_relations +
                " WHERE " + db_object_id + " = " + objectId + ")";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String roomName = cursor.getString(cursor.getColumnIndex(db_premise_name));
                rooms.add(roomName);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return rooms;

    }

    public boolean insertObjectRoomRelation(int objectId, int roomId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(db_object_id, objectId);
        values.put(db_room_id, roomId);

        long result = db.insert(db_table_relations, null, values);
        db.close();

        if (result == -1){
            return false;
        } else
            return true;
    }

}
