package com.example.uhf_bt;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.uhf_bt.Models.Executor;
import com.example.uhf_bt.Models.Facility;
import com.example.uhf_bt.Models.Premise;
import com.example.uhf_bt.Models.TagData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataBase extends SQLiteOpenHelper {

    private static final String db_name = "rfidCounter";
    private static final int db_version = 82;
    private static final String db_table = "marking";

    //columns
    private static final String db_id = "id";
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
    private final static String db_date = "dateTime";


    //user table
    private final static String dbUserTable = "executors";
    private final static String db_user_id = "id";
    private final static String db_user_role = "role";
    private final static String db_user_name = "name";
    private final static String db_user_note = "note";


    // facility table
    private final static String db_facilityTable = "facilities";
    private final static String db_facility_id = "facility_id";
    private final static String db_facility_name = "name";
    private final static String db_facility_address = "address";
    private final static String db_facility_note = "note";


    // premise table
    private final static String db_premiseTable = "premises";
    private final static String db_premise_id = "id";
    private final static String db_premise_name = "name";
    private final static String db_premise_note = "note";

    // inventory table from 1c
    public final static String db_table_inventory_1c = "inventory_table_1c";
    public final static String db_table_1c_id = "inventory_table_1c_id";
    public final static String db_table_1c_epc = "inventory_table_1c_epc";
    public final static String db_table_1c_type = "inventory_table_1c_type";
    public final static String db_table_1c_description = "inventory_table_1c_description";

    public final static String db_table_1c_inventory_number = "db_table_1c_inventory_number";
    public final static String db_table_1c_nomenclature = "db_table_1c_nomenclature";
    public final static String db_table_1c_amount = "db_table_1c_amount";
    public final static String db_table_1c_facility = "db_table_1c_facility";
    public final static String db_table_1c_premise = "db_table_1c_premise";
    public final static String db_table_1c_datetime = "db_table_1c_datetime";
    public final static String db_table_1c_executor = "db_table_1c_executor";

    // result table
    private final static String db_table_result = "result_table";
    private final static String resultTable_id = "id";
    private final static String resultTable_epc = "epc";
    private final static String resultTable_type = "type";
    private final static String resultTable_description = "description";

    private final static String resultTable_inventory_number = "inventory_number";
    private final static String resultTable_nomenclature = "nomenclature";
    private final static String resultTable_amount = "amount";
    private final static String resultTable_facility = "facility";
    private final static String resultTable_premise = "premise";
    private final static String resultTable_dateTime = "dateTime";
    private final static String resultTable_executor = "executor";


    public DataBase(@Nullable Context context) {
        super(context, db_name, null, db_version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {

            String query = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s INTEGER);",
                    db_table, db_id, db_epc, db_type, db_description, db_inventory_number, db_nomenclature, db_amount);
            String queryInventory = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT," +
                            " %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s INTEGER, %s TEXT, %s TEXT, %s TEXT, %s TEXT);",
                    db_table_inventory, db_id, db_epc, db_type, db_description, db_inventory_number, db_nomenclature, db_amount,
                    db_facility, db_premise, db_date, db_executor);

            String queryFacility = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT);",
                    db_facilityTable,
                    db_facility_id, db_facility_name, db_facility_address, db_facility_note);

            String queryPremise = "CREATE TABLE premises " + "(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "facility_id INTEGER NOT NULL, " +
                    "name TEXT NOT NULL, " +
                    "note TEXT, " +
                    "FOREIGN KEY(facility_id) REFERENCES facilities(facility_id) ON DELETE CASCADE" +
                    ");";

            String queryUser = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT);",
                    dbUserTable,
                    db_user_id, db_user_role, db_user_name, db_user_note);

            String queryInventory1c = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT," +
                            " %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s INTEGER, %s TEXT, %s TEXT, %s TEXT, %s TEXT)",
                    db_table_inventory_1c, db_table_1c_id, db_table_1c_epc, db_table_1c_type, db_table_1c_description, db_table_1c_inventory_number, db_table_1c_nomenclature, db_table_1c_amount,
                    db_table_1c_facility, db_table_1c_premise, db_table_1c_datetime, db_table_1c_executor);
            String queryResult = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT," +
                            " %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s INTEGER, %s TEXT, %s TEXT, %s TEXT, %s TEXT)",
                    db_table_result, resultTable_id, resultTable_epc, resultTable_type, resultTable_description, resultTable_inventory_number, resultTable_nomenclature, resultTable_amount,
                    resultTable_facility, resultTable_premise, resultTable_dateTime, resultTable_executor);


            db.execSQL(query);
            db.execSQL(queryUser);
            db.execSQL(queryFacility);
            db.execSQL(queryInventory);
            db.execSQL(queryPremise);
            db.execSQL(queryInventory1c);
            db.execSQL(queryResult);
            Log.d("DataBase", "onCreate called");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String queryMarking = String.format("DROP TABLE IF EXISTS %s", db_table);
        String queryUsers = String.format("DROP TABLE IF EXISTS %s", dbUserTable);
        String queryInventory = String.format("DROP TABLE IF EXISTS %s", db_table_inventory);
        String queryFacility = String.format("DROP TABLE IF EXISTS %s", db_facilityTable);
        String queryPremise = String.format("DROP TABLE IF EXISTS %s", db_premiseTable);
        String query1c = String.format("DROP TABLE IF EXISTS %s", db_table_inventory_1c);
        String queryResult = String.format("DROP TABLE IF EXISTS %s", db_table_result);
        db.execSQL(queryMarking);
        db.execSQL(queryFacility);
        db.execSQL(queryInventory);
        db.execSQL(queryPremise);
        db.execSQL(queryUsers);
        db.execSQL(query1c);
        db.execSQL(queryResult);
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
                + db_table_inventory + " WHERE " + db_epc + " = ?";
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
                        LocalDateTime.now(),
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
        values.put(db_amount, tagData.getAmount());
        String whereClause = db_epc + "=?";
        String[] whereArgs = {epc};

        int result = db.update(db_table, values, whereClause, whereArgs);

        db.close();

        return result > 0;
    }

    public boolean updateDataInInventory(TagData tagData, String epc) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(db_description, tagData.getDescription());
        values.put(db_inventory_number, tagData.getInventoryNumber());
        values.put(db_nomenclature, tagData.getNomenclature());
        values.put(db_amount, tagData.getAmount());
        String whereClause = db_epc + "=?";
        String[] whereArgs = {epc};

        int result = db.update(db_table_inventory, values, whereClause, whereArgs);

        db.close();

        return result > 0;
    }


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

    public boolean deleteUser(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String delete = db_user_name + " = ?";
        String[] cause = {name};

        int result1 = db.delete(dbUserTable, delete, cause);
//        int result2 = db.delete(db_table_inventory, delete, cause);

        return result1 > 0 ;

    }

    public List<Executor> getUsers() {
        List<Executor> executors = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String select = "SELECT * FROM " + dbUserTable;
        Cursor cursor = db.rawQuery(select, null);
        if (cursor.moveToFirst()) {
            do {
                String role = cursor.getString(cursor.getColumnIndex(db_user_role));
                String name = cursor.getString(cursor.getColumnIndex(db_user_name));
                String note = cursor.getString(cursor.getColumnIndex(db_user_note));
                Executor executor = new Executor(role, name, note);

                executors.add(executor);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return executors;
    }

    public Executor getUserByName(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        String select = "SELECT * FROM " + dbUserTable + " WHERE " + db_user_name + " =?";
        Cursor cursor = db.rawQuery(select, new String[]{username});
        Executor user = null;

        if (cursor.moveToFirst()) {
            user = new Executor();
            user.setRole(cursor.getString(cursor.getColumnIndex(db_user_role)));
            user.setName(cursor.getString(cursor.getColumnIndex(db_user_name)));
            user.setNotation(cursor.getString(cursor.getColumnIndex(db_user_note)));
        }

        cursor.close();
        // Не закрывайте базу данных здесь, оставьте это за пределами метода
        return user;
    }

    public boolean updateUser(Executor user, String username){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(db_user_role, user.getName());
        values.put(db_user_name, user.getName());
        values.put(db_user_note, user.getNotation());

        String whereClause = db_user_name + "=?";
        String[] whereArgs = {username};

        int result = db.update(dbUserTable, values, whereClause, whereArgs);
        return result > 0;
    }

    // facility settings
    public boolean insertFacility(Facility facility) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valuesFacility = new ContentValues();
        ContentValues valuesForRelations = new ContentValues();
        valuesFacility.put(db_facility_name, facility.getName());
        valuesFacility.put(db_facility_address, facility.getAddress());
        valuesFacility.put(db_facility_note, facility.getNote());

        ContentValues valuesForInventory = new ContentValues();
        valuesForInventory.put(db_facility, facility.getName());
        long result = db.insert(db_facilityTable, null, valuesFacility);
        long result2 = db.insert(db_table_inventory, null, valuesForInventory);
        if (result == -1 && result2 == -1)
            return false;
        else
            return true;
    }

    public boolean deleteFacility(Facility facilityId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String delete = db_facility_id + " = ?";
        String[] cause = {String.valueOf(facilityId.getId())};
        int result1 = db.delete(db_facilityTable, delete, cause);
//        int result2 = db.delete(db_table_inventory, delete, cause);

        return result1 > 0 ;
    }

    public List<Facility> getFacilities() {
        List<Facility> facilities = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String select = "SELECT " + db_facility_id + ", " + db_facility_name + " FROM " + db_facilityTable;
        Cursor cursor = db.rawQuery(select, null);
        if (cursor.moveToFirst()) {
            do {
                Facility facility = new Facility();
                facility.setId(cursor.getInt(cursor.getColumnIndex(db_facility_id)));
                facility.setName(cursor.getString(cursor.getColumnIndex(db_facility_name)));
                facilities.add(facility);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return facilities;
    }

    public boolean updateFacility(Facility facility, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(db_facility_name, facility.getName());
        values.put(db_facility_address, facility.getAddress());
        values.put(db_facility_note, facility.getNote());

        String whereClause = db_facility_id + "=?";
        String[] whereArgs = {String.valueOf(id)};

        long result = db.update(db_facilityTable, values, whereClause, whereArgs);
        db.close();
        return result > 0;
    }
    public Facility getFacilityById(String id) {
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT * FROM " + db_facilityTable + " WHERE " + db_facility_id + "=?";
        Cursor cursor = database.rawQuery(query, new String[]{id});

        Facility facility = null;

        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex(db_facility_name));
            String address = cursor.getString(cursor.getColumnIndex(db_facility_address));
            String note = cursor.getString(cursor.getColumnIndex(db_facility_note));

            facility = new Facility(name, address, note);
        }

        cursor.close();
        database.close();

        return facility;
    }

    public boolean insertPremise(Premise premise, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valuesPremises = new ContentValues();
        ContentValues valuesForInventory = new ContentValues();
        valuesPremises.put(db_facility_id, id);
        valuesPremises.put(db_premise_name, premise.getName());
        valuesPremises.put(db_premise_note, premise.getNote());
        valuesForInventory.put(db_premise, premise.getName());

        long result = db.insert(db_premiseTable, null, valuesPremises);
        long result2 = db.insert(db_table_inventory, null, valuesForInventory);
        if (result == -1 && result2 == -1)
            return false;
        else
            return true;

    }

    public Premise getPremiseByName(String premiseName){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + db_premiseTable + " WHERE " + db_premise_name + " =?";
        Cursor cursor = db.rawQuery(query, new String[]{premiseName});
        Premise premise = null;
        if (cursor.moveToFirst()){
            do {
                String name = cursor.getString(cursor.getColumnIndex(db_premise_name));
                String note = cursor.getString(cursor.getColumnIndex(db_premise_note));
                premise = new Premise();
                premise.setName(name);
                premise.setNote(note);
            } while(cursor.moveToNext());
        }


        cursor.close();
        db.close();

        return premise;


    }

    public boolean updatePremise(Premise premise, String premiseName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(db_premise_name, premise.getName());
        values.put(db_premise_note, premise.getNote());
        String whereClause = db_premise_name + "=?";
        String[] whereArgs = {premiseName};

        long result = db.update(db_premiseTable,  values, whereClause ,whereArgs);
        db.close();
        return result > 0;
    }

    public boolean deletePremise(Premise premiseName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String delete = db_premise_name + " = ?";
        String[] cause = {premiseName.getName()};

        int result1 = db.delete(db_premiseTable, delete, cause);
        db.close();
        return result1 > 0;
    }

    public boolean insertPremise(Premise premise) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valuesPremises = new ContentValues();
        ContentValues valuesForInventory = new ContentValues();
        valuesPremises.put(db_premise_name, premise.getName());
        valuesPremises.put(db_premise_note, premise.getNote());
        valuesForInventory.put(db_premise, premise.getName());

        long result = db.insert(db_premiseTable, null, valuesPremises);
        long result2 = db.insert(db_table_inventory, null, valuesForInventory);
        if (result == -1 && result2 == -1)
            return false;
        else
            return true;
    }

    public List<Premise> getPremisesByFacility(int facilityId) {
        List<Premise> premises = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String select = "SELECT " + db_premise_name + " FROM " + db_premiseTable +
                " WHERE " + db_facility_id + " = " + facilityId;
        Cursor cursor = db.rawQuery(select, null);

        if (cursor.moveToFirst()) {
            do {
                Premise premise = new Premise();
                premise.setName(cursor.getString(cursor.getColumnIndex(db_premise_name)));
                premises.add(premise);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return premises;
    }

    public boolean insertBarcode(TagData barcode, String barcodeData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(db_nomenclature, barcode.getNomenclature());
        values.put(db_amount, barcode.getAmount());
        values.put(db_description, barcode.getDescription());
        values.put(db_epc, barcodeData);
        values.put(db_type, barcode.getType());
        values.put(db_inventory_number, barcode.getInventoryNumber());
        values.put(db_date, String.valueOf(barcode.getDateTimeFormatter()));
        ;
        long result = db.insert(db_table_inventory, null, values);
        if (result == -1)
            return false;
        else
            return true;
    }


    public void importCSVToDatabase(Context context, String csvFileName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String csvFilePath = "/storage/SQLiteBackup/SQLite_Backup.csv";

        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(csvFilePath);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                // Разделение строки CSV и вставление данные в базу данных
                String[] values = line.split(",");
                ContentValues contentValues = new ContentValues();
                contentValues.put(db_table_1c_id, values[0]);
                contentValues.put(db_table_1c_epc, values[1]);
                contentValues.put(db_table_1c_type, values[2]);
                contentValues.put(db_table_1c_description, values[3]);
                contentValues.put(db_table_1c_inventory_number, values[4]);
                contentValues.put(db_table_1c_nomenclature, values[5]);
                contentValues.put(db_table_1c_amount, values[6]);
                contentValues.put(db_table_1c_facility, values[7]);
                contentValues.put(db_table_1c_premise, values[8]);
                contentValues.put(db_table_1c_datetime, values[9]);
                contentValues.put(db_table_1c_executor, values[10]);

                db.insert(db_table_inventory_1c, null, contentValues);
            }

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<TagData> getDataFrom1C() {
        List<TagData> tagList1C = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + db_table_inventory_1c;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex(db_table_1c_id));
                String epc = cursor.getString(cursor.getColumnIndex(db_table_1c_epc));
                String type = cursor.getString(cursor.getColumnIndex(db_table_1c_type));
                String description = cursor.getString(cursor.getColumnIndex(db_table_1c_description));
                String inventory_number = cursor.getString(cursor.getColumnIndex(db_table_1c_inventory_number));
                String nomenclature = cursor.getString(cursor.getColumnIndex(db_table_1c_nomenclature));
                String amount = cursor.getString(cursor.getColumnIndex(db_table_1c_amount));
                String facility = cursor.getString(cursor.getColumnIndex(db_table_1c_facility));
                String premise = cursor.getString(cursor.getColumnIndex(db_table_1c_premise));
                String dateTime = cursor.getString(cursor.getColumnIndex(db_table_1c_datetime));
                String executor = cursor.getString(cursor.getColumnIndex(db_table_1c_executor));

                TagData tagData = new TagData(
                        id, epc, type, description, inventory_number, nomenclature, Integer.parseInt(amount),
                        facility, premise, dateTime, executor
                );

                tagList1C.add(tagData);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return tagList1C;

    }

    public boolean importDataFrom1C(TagData tagData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(db_table_1c_id, tagData.getId());
        values.put(db_table_1c_epc, tagData.getEpc());
        values.put(db_table_1c_type, tagData.getType());
        values.put(db_table_1c_description, tagData.getDescription());
        values.put(db_table_1c_inventory_number, tagData.getInventoryNumber());
        values.put(db_table_1c_nomenclature, tagData.getNomenclature());
        values.put(db_table_1c_amount, tagData.getAmount());
        values.put(db_table_1c_facility, tagData.getAmount());
        values.put(db_table_1c_premise, tagData.getPremise());
        values.put(db_table_1c_datetime, String.valueOf(tagData.getDateTimeFormatter()));
        values.put(db_table_1c_executor, tagData.getExecutor());
        long result = db.insert(db_table_inventory_1c, null, values);
        db.close();
        if (result == -1)
            return false;
        else
            return true;

    }

    public void refreshResultTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        String joinQuery = "INSERT INTO result_table (epc, type, description, inventory_number, nomenclature, Amount, executor, facility, premise, dateTime, executor)" +
                " SELECT " +
                "  inventory_table.epc, " +
                "  inventory_table.type, " +
                "  inventory_table.description, " +
                "  inventory_table.inventory_number, " +
                "  inventory_table.nomenclature, " +
                "  (inventory_table.Amount - inventory_table_1c.db_table_1c_amount) AS amount, " +
                "  inventory_table.executor, " +
                "  inventory_table.facility, " +
                "  inventory_table.premise, " +
                "  inventory_table.dateTime, " +
                "  inventory_table.executor" +
                " FROM " +
                "  inventory_table " +
                " LEFT JOIN " +
                "  inventory_table_1c " +
                " ON " +
                "  inventory_table.epc = inventory_table_1c.inventory_table_1c_epc;";

        db.execSQL(joinQuery);
    }

    public List<TagData> getResult() {
        List<TagData> resultData = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();


        String selectQuery = "SELECT * FROM " + db_table_result;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                TagData tagData = new TagData();
                // Затем вы можете продолжить извлекать данные и добавлять их в список
                tagData.setId(cursor.getString(cursor.getColumnIndex(resultTable_id)));
                tagData.setEpc(cursor.getString(cursor.getColumnIndex(resultTable_epc)));
                tagData.setType(cursor.getString(cursor.getColumnIndex(resultTable_type)));
                tagData.setDescription(cursor.getString(cursor.getColumnIndex(resultTable_description)));
                tagData.setInventoryNumber(cursor.getString(cursor.getColumnIndex(resultTable_inventory_number)));
                tagData.setNomenclature(cursor.getString(cursor.getColumnIndex(resultTable_nomenclature)));
                tagData.setAmount(cursor.getInt(cursor.getColumnIndex(resultTable_amount)));
                tagData.setFacility(cursor.getString(cursor.getColumnIndex(resultTable_facility)));
                tagData.setPremise(cursor.getString(cursor.getColumnIndex(resultTable_premise)));
                String dateTimeString = cursor.getString(cursor.getColumnIndex(resultTable_dateTime));
                tagData.setDateTimeFormatter(LocalDateTime.parse(dateTimeString));
                tagData.setExecutor(cursor.getString(cursor.getColumnIndex(resultTable_executor)));

                resultData.add(tagData);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return resultData;
    }

    public ArrayList<HashMap<String, String>> getAllProducts() {

        ArrayList<HashMap<String, String>> productList;
        productList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT * FROM " + db_table_inventory_1c;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {

            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("a", cursor.getString(0));
                map.put("b", cursor.getString(1));
                map.put("c", cursor.getString(2));
                map.put("d", cursor.getString(3));
                map.put("e", cursor.getString(4));
                map.put("f", cursor.getString(5));
                map.put("g", cursor.getString(6));
                map.put("h", cursor.getString(7));
                map.put("i", cursor.getString(8));
                map.put("j", cursor.getString(9));
                map.put("k", cursor.getString(10));

                productList.add(map);
                Log.e("dataofList", cursor.getString(0) + "," + cursor.getString(1) + "," + cursor.getString(2));
            } while (cursor.moveToNext());
        }
        return productList;

    }

}

