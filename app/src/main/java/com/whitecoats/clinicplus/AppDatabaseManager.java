package com.whitecoats.clinicplus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vaibhav on 19-02-2018.
 */

public class AppDatabaseManager extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "pfa_clinic_db";

    // Contacts table name
    private static final String TABLE_CONTACTS = "user_details";
    private static final String DOC_TABLE = "doctor_details";
    private static final String DOC_SERVICE_TABLE = "doctor_service";
    private static final String MENU_ITEM = "menu_table";
    private static final String CART_ITEM = "cart_item";



    public AppDatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
//        Log.d("Creating Table", "**********");
        String CREATE_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                 + "pid INTEGER PRIMARY KEY, pname TEXT, phno TEXT, gender INTEGER, email TEXT, bloodtype TEXT, bday TEXT," +
                "token TEXT, isDoctorOnly INTEGER)";
        db.execSQL(CREATE_TABLE);

        CREATE_TABLE = "CREATE TABLE " + DOC_TABLE + "("
                + "did INTEGER PRIMARY KEY, dname TEXT, dphno TEXT, dgender INTEGER, demail TEXT, dlang TEXT, dexp TEXT)";
        db.execSQL(CREATE_TABLE);

        CREATE_TABLE = "CREATE TABLE " + DOC_SERVICE_TABLE + "("
                + "did TEXT, productid TEXT, serviceid TEXT, saasid TEXT, servicename TEXT, serviceadd TEXT, serviceprice TEXT)";
        db.execSQL(CREATE_TABLE);

        CREATE_TABLE = "CREATE TABLE " + MENU_ITEM + "("
                + "interfaceid TEXT, menuitem TEXT)";
        db.execSQL(CREATE_TABLE);

        CREATE_TABLE = "CREATE TABLE " + CART_ITEM + "("
                + "testid INT, testname VARCHAR(50), testdesp VARCHAR(50), testcollectplace VARCHAR(20), testprice VARCHAR(20), testcenterid INT, testcentername VARCHAR(100), testcollectmode INT)";
        db.execSQL(CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
//        db.execSQL("DROP TABLE IF EXISTS " + DOC_TABLE);
//        db.execSQL("DROP TABLE IF EXISTS " + DOC_SERVICE_TABLE);
//        db.execSQL("DROP TABLE IF EXISTS " + MENU_ITEM);
        db.execSQL("DROP TABLE IF EXISTS " + CART_ITEM);

        String CREATE_TABLE = "CREATE TABLE " + CART_ITEM + "("
                + "testid INT, testname VARCHAR(50), testdesp VARCHAR(50), testcollectplace VARCHAR(20), testprice VARCHAR(20), testcenterid INT, testcentername VARCHAR(100), testcollectmode INT)";
        db.execSQL(CREATE_TABLE);

        // Create tables again
//        onCreate(db);
    }

    // Adding new contact
    void addPatient(AppUserManager userManager) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("pid", userManager.getUserId());
        values.put("pname", userManager.getUserName());
        values.put("phno", userManager.getUserPHno());
        values.put("gender", userManager.getUserGender());
        values.put("email", userManager.getUserEmail());
        values.put("bloodtype", userManager.getBloodType());
        values.put("bday", userManager.getUserBday());
        values.put("token", userManager.getToken());
        values.put("isDoctorOnly", userManager.getIsDoctorOnly());

        // Inserting Row
        long status = db.insert(TABLE_CONTACTS, null, values);
//        Log.d("Database Insert", status + "");
        db.close(); // Closing database connection
    }

    public int updatePatient(AppUserManager userManager) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("pid", userManager.getUserId());
        values.put("pname", userManager.getUserName());
        values.put("phno", userManager.getUserPHno());
        values.put("gender", userManager.getUserGender());
        values.put("email", userManager.getUserEmail());
        values.put("bloodtype", userManager.getBloodType());
        values.put("bday", userManager.getUserBday());

        // updating row
        return db.update(TABLE_CONTACTS, values, "token = ?",
                new String[] { userManager.getToken() });
    }

    public int deletePatient(AppUserManager userManager) {
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = "token=?";
        String[] whereArgs = new String[] { userManager.getToken() };

        return db.delete(TABLE_CONTACTS, whereClause, whereArgs);
    }

    // Getting single contact
    public List<AppUserManager> getUserData() {
        List<AppUserManager> userManagerList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
//        Log.d("Database Query", cursor.toString());

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                AppUserManager userManager = new AppUserManager(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
                        cursor.getString(2), Integer.parseInt(cursor.getString(3)), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6), cursor.getString(7), Integer.parseInt(cursor.getString(8)));

                // Adding contact to list
                userManagerList.add(userManager);
//                Log.d("Database Query", userManagerList.toString());
            } while (cursor.moveToNext());
        }

        cursor.close();

        // return contact list
        return userManagerList;
    }

    public void addDoctor(AppDoctorManager doctorManager) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("did", doctorManager.getDocId());
        values.put("dname", doctorManager.getDocName());
        values.put("dphno", doctorManager.getDocPHno());
        values.put("dgender", doctorManager.getDocGender());
        values.put("demail", doctorManager.getDocEmail());
        values.put("dlang", doctorManager.getDocLang());
        values.put("dexp", doctorManager.getDocExp());


        // Inserting Row
        long status = db.insert(DOC_TABLE, null, values);
//        Log.d("Database Insert", status + "");
        db.close(); // Closing database connection
    }

    // Getting single contact
    public List<AppDoctorManager> getDoctorData() {
        List<AppDoctorManager> userManagerList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + DOC_TABLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
//        Log.d("Database Query", cursor.toString());

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                AppDoctorManager userManager = new AppDoctorManager(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
                        cursor.getString(2), Integer.parseInt(cursor.getString(3)), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6));

                // Adding contact to list
                userManagerList.add(userManager);
//                Log.d("Database Query", userManagerList.toString());
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        // return contact list
        return userManagerList;
    }

//    //adding doctor service
//    public void addService(AppDocServiceManager docServiceManager) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put("did", docServiceManager.getDocId());
//        values.put("productid", docServiceManager.getProductId());
//        values.put("serviceid", docServiceManager.getServiceId());
//        values.put("saasid", docServiceManager.getInternalSaasId());
//        values.put("servicename", docServiceManager.getServiceName());
//        values.put("serviceprice", docServiceManager.getServicePrice());
//        values.put("serviceadd", docServiceManager.getServiceAddress());
//
//
//        // Inserting Row
//        long status = db.insert(DOC_SERVICE_TABLE, null, values);
//        Log.d("Database Insert", status + "");
//        db.close(); // Closing database connection
//    }
//
//    public List<AppDocServiceManager> getDocService(String docId) {
//        List<AppDocServiceManager> docServiceManagers = new ArrayList<>();
//        // Select All Query
//        String selectQuery = "SELECT  * FROM " + DOC_SERVICE_TABLE + " WHERE did=" + docId;
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//        Log.d("Database Query", cursor.toString());
//
//        // looping through all rows and adding to list
//        if (cursor.moveToFirst()) {
//            do {
//                AppDocServiceManager docServiceManager = new AppDocServiceManager(cursor.getString(1), cursor.getString(2),
//                        cursor.getString(3), cursor.getString(4), cursor.getString(5),
//                        cursor.getString(6), cursor.getString(0));
//
//                // Adding contact to list
//                docServiceManagers.add(docServiceManager);
////                Log.d("Database Query", docServiceManagers.toString());
//            } while (cursor.moveToNext());
//        }
//
//        cursor.close();
//
//        // return contact list
//        return docServiceManagers;
//    }
//
//    public List<AppDocServiceManager> getServiceDetail(String serviceName, String docId) {
//        List<AppDocServiceManager> docServiceManagers = new ArrayList<>();
//        // Select All Query
//        String selectQuery = "SELECT  * FROM " + DOC_SERVICE_TABLE + " WHERE did=" + docId + " AND servicename='" + serviceName + "'";
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//        Log.d("Database Query", cursor.toString());
//
//        // looping through all rows and adding to list
//        if (cursor.moveToFirst()) {
//            do {
//                AppDocServiceManager docServiceManager = new AppDocServiceManager(cursor.getString(1), cursor.getString(2),
//                        cursor.getString(3), cursor.getString(4), cursor.getString(5),
//                        cursor.getString(6), cursor.getString(0));
//
//                // Adding contact to list
//                docServiceManagers.add(docServiceManager);
////                Log.d("Database Query", docServiceManagers.toString());
//            } while (cursor.moveToNext());
//        }
//
//        cursor.close();
//
//        // return contact list
//        return docServiceManagers;
//    }
//
//    public boolean deleteService(String serviceName) {
//        boolean state = false;
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        state = db.delete(DOC_SERVICE_TABLE, "servicename='" + serviceName + "'", null) > 0;
//
//        return state;
//    }
//
//    //adding menu item
//    public void addMenu(AppDocServiceManager docServiceManager) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put("interfaceid", appConfigClass.appOrigin);
//        values.put("menuitem", docServiceManager.getMenuItem());
//
//
//        // Inserting Row
//        long status = db.insert(MENU_ITEM, null, values);
//        Log.d("Database Insert", status + "");
//        db.close(); // Closing database connection
//    }
//
//    public List<AppDocServiceManager> getMenuItem(String interfaceId) {
//        List<AppDocServiceManager> docServiceManagers = new ArrayList<>();
//        // Select All Query
//        String selectQuery = "SELECT  * FROM " + MENU_ITEM + " WHERE interfaceid=" + interfaceId;
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//        Log.d("Database Query", cursor.toString());
//
//        // looping through all rows and adding to list
//        if (cursor.moveToFirst()) {
//            do {
//                AppDocServiceManager docServiceManager = new AppDocServiceManager(cursor.getString(0), cursor.getString(1));
//
//                // Adding contact to list
//                docServiceManagers.add(docServiceManager);
////                Log.d("Database Query", docServiceManagers.toString());
//            } while (cursor.moveToNext());
//        }
//
//        cursor.close();
//
//        // return contact list
//        return docServiceManagers;
//    }
//
//    public void addCartItem(AppCartManager appCartManager) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put("testid", appCartManager.getTestId());
//        values.put("testname", appCartManager.getTestName());
//        values.put("testprice", appCartManager.getTestPrice());
//        values.put("testcollectplace", appCartManager.getTestCollectPlace());
//        values.put("testdesp", appCartManager.getTestDesp());
//        values.put("testcenterid", appCartManager.getTestCenterId());
//        values.put("testcentername", appCartManager.getTestCenterName());
//        values.put("testcollectmode", appCartManager.getTestCollectPlaceMode());
//
//        // Inserting Row
//        long status = db.insert(CART_ITEM, null, values);
//        Log.d("Database Insert", status + "");
//        db.close(); // Closing database connection
//    }
//
//    public List<AppCartManager> getCartItem() {
//        List<AppCartManager> appCartManagers = new ArrayList<>();
//        // Select All Query
//        String selectQuery = "SELECT  * FROM " + CART_ITEM;
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//        Log.d("Database Query", cursor.toString());
//
//        // looping through all rows and adding to list
//        if (cursor.moveToFirst()) {
//            do {
//                AppCartManager appCartManager = new AppCartManager(cursor.getInt(0), cursor.getString(1), cursor.getString(4), cursor.getString(2), cursor.getString(3), cursor.getInt(5), cursor.getString(6), cursor.getInt(7));
//
//                // Adding contact to list
//                appCartManagers.add(appCartManager);
////                Log.d("Database Query", docServiceManagers.toString());
//            } while (cursor.moveToNext());
//        }
//
//        cursor.close();
//
//        // return contact list
//        return appCartManagers;
//    }
//
//    public boolean removeCartItem(String testid) {
//        boolean state = false;
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        state = db.delete(CART_ITEM, "testid='" + testid + "'", null) > 0;
//
//        return state;
//    }
//
//    public boolean clearCart() {
//        boolean state = false;
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        state = db.delete(CART_ITEM, null, null) > 0;
//
//        return state;
//    }
//
//    public int updateTestCollectionMode(int testID, int mode) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put("testcollectmode", mode);
//
//        // updating row
//        return db.update(CART_ITEM, values, "testid = " + testID, null);
//    }
}
