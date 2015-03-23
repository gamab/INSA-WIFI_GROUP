package com.example.gb.connectapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by UT on 10/03/2015.
 */
public class BDD extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "PTut.db";
    // TOGGLE THIS NUMBER FOR UPDATING TABLES AND DATABASE
    private static final int DATABASE_VERSION = 1;

    private static final String TAG = "BDD";

    BDD(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @SuppressLint("LongLogTag")
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // CREATE Network TABLE
        Log.w("====>CREATE Network TABLE"," with ID="+NetworkTable.ID+"  SSID="+NetworkTable.SSID+"  PresharedKey="+NetworkTable.PresharedKey+"!!");
        db.execSQL("CREATE TABLE " + NetworkTable.TABLE_NAME + " (" + NetworkTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + NetworkTable.SSID + " TEXT,"
                + NetworkTable.PresharedKey + " TEXT);");

        // CREATE Network TABLE
        Log.w("====>CREATE QoS TABLE"," with ID="+QosTable.ID+"  Note="+QosTable.Note+"  TimeStamp="+QosTable.Time+"!!");
        db.execSQL("CREATE TABLE " + QosTable.TABLE_NAME + " (" + QosTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + QosTable.Note + " Integer,"
                + QosTable.Time + " TEXT);");

        // CREATE Join MAPPING TABLE
        Log.w("====>CREATE Join MAPPING TABLE"," with ID="+Join.ID+"  Network_ID="+Join.Network_ID+"  Qos_ID="+Join.Qos_ID+"!!");
        db.execSQL("CREATE TABLE " + Join.TABLE_NAME + " (" + Join.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Join.Network_ID + " INTEGER,"
                + Join.Qos_ID + " INTEGER);");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,int newVersion)
    {
        Log.w("====>LOG_TAG", "Upgrading database from version " + oldVersion + " to " + newVersion + ",which will destroy all old data");
        // KILL PREVIOUS TABLES IF UPGRADED
        db.execSQL("DROP TABLE IF EXISTS " + NetworkTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QosTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Join.TABLE_NAME);
        // CREATE NEW INSTANCE OF SCHEMA
        onCreate(db);
    }

    public void deleteEverything() {
        SQLiteDatabase db = getWritableDatabase();
        onUpgrade(db,1,2);
        //onCreate(db);
    }

    // WRAPPER METHOD FOR ADDING A Network
    @SuppressLint("LongLogTag")
    public long addNetwork(String SSID, String PresharedKey)
    {
        Log.w("====>WRAPPER METHOD FOR ADDING A Network"," with SSID="+SSID+"  PresharedKey="+PresharedKey+"!!");
        // CREATE A CONTENTVALUE OBJECT
        ContentValues cv = new ContentValues();
        cv.put(NetworkTable.SSID, SSID);
        cv.put(NetworkTable.PresharedKey, PresharedKey);
        // RETRIEVE WRITEABLE DATABASE AND INSERT
        SQLiteDatabase sd = getWritableDatabase();
        long result = sd.insert(NetworkTable.TABLE_NAME,null, cv);
        Cursor cursor = sd.rawQuery("SELECT * FROM " + NetworkTable.TABLE_NAME,null);
        while (cursor.moveToNext()) {
            Log.d(TAG,cursor.getString(0) + " " + cursor.getString(1) + " " + cursor.getString(2));
        }
        cursor.close();
        return result;
    }
    // WRAPPER METHOD FOR ADDING A Qos
    @SuppressLint("LongLogTag")
    public long addQos(String note, String time)
    {
        Log.w("====>WRAPPER METHOD FOR ADDING A Qos"," with Note="+note+"  Time="+time+"!!");
        ContentValues cv = new ContentValues();
        cv.put(QosTable.Note, note);
        cv.put(QosTable.Time, time);
        SQLiteDatabase sd = getWritableDatabase();
        long result = sd.insert(QosTable.TABLE_NAME,null, cv);
        return result;
    }

    // WRAPPER METHOD FOR ENROLLING A Setting INTO A Network
    @SuppressLint("LongLogTag")
    public boolean enrollSettingClass(int netId, int qosId)
    {
        Log.w("====>WRAPPER METHOD FOR ENROLLING A Setting INTO A Network"," with Network_ID="+netId+"  Qos_ID="+qosId+"!!");
        ContentValues cv = new ContentValues();
        cv.put(Join.Network_ID, netId);
        cv.put(Join.Qos_ID, qosId);
        SQLiteDatabase sd = getWritableDatabase();
        long result = sd.insert(Join.TABLE_NAME,Join.Qos_ID, cv);
        return (result >= 0);
    }

    // GET ALL Settings Qos of one Network
    @SuppressLint("LongLogTag")
    public void getSettingsForNetwork(int netId)
    {
        SQLiteDatabase sd = getWritableDatabase();

        Cursor cursor = sd.rawQuery("SELECT * FROM " + NetworkTable.TABLE_NAME+" WHERE "+NetworkTable.ID+"="+netId,null);
        Cursor cursor2 = sd.rawQuery("SELECT "+Join.Qos_ID+" FROM " + Join.TABLE_NAME+" WHERE "+Join.Network_ID+"="+netId,null);
        Cursor cursor3 = sd.rawQuery("SELECT * FROM " + QosTable.TABLE_NAME+" WHERE "+QosTable.ID+"="+cursor2.getString(0),null);

        while (cursor3.moveToNext()) {
            Log.w("GET ALL Settings Qos of one Network","SSID= "+cursor.getString(1) + "-- Note= " + cursor3.getString(1) + "-- Time= " + cursor3.getString(2));
        }
        cursor.close();
        cursor2.close();
        cursor3.close();
    }


    // GET ALL Network by mark
    @SuppressLint("LongLogTag")
    public String getNetworkByMark()
    {
        SQLiteDatabase sd = getWritableDatabase();
        int Note=0;
        String SSID="";

        Cursor cursor2 = sd.rawQuery("SELECT * FROM " + Join.TABLE_NAME,null);
        while (cursor2.moveToNext()) {
            Cursor cursor3 = sd.rawQuery("SELECT "+QosTable.Note+" FROM " + QosTable.TABLE_NAME+" WHERE "+QosTable.ID+"="+cursor2.getString(2),null);
            Cursor cursor = sd.rawQuery("SELECT * FROM " + NetworkTable.TABLE_NAME+" WHERE "+NetworkTable.ID+"="+cursor2.getString(1),null);
            if(Integer.parseInt(cursor3.getString(0)) >= Note)
            {
               SSID= cursor.getString(1);
               Note=Integer.parseInt(cursor3.getString(0));
            }
            Log.w("GET NETWORK BY MARK","SSID= "+cursor.getString(1) + "-- Note= " + cursor3.getString(0));
            cursor.close();
            cursor3.close();
        }
        cursor2.close();
        return SSID;
    }


    // GET ALL Network by mark
    @SuppressLint("LongLogTag")
    public String getPresharKeyBySSID(String ssid)
    {
        SQLiteDatabase sd = getWritableDatabase();

        String Key="";
        Cursor cursor2 = sd.rawQuery("SELECT "+NetworkTable.PresharedKey+" FROM " + NetworkTable.TABLE_NAME+" WHERE "+NetworkTable.SSID+"="+ssid,null);

        cursor2.close();
        return Key;
    }



    // GET ALL Networks FOR A GIVEN setting Qos
    @SuppressLint("LongLogTag")
    public void getNetworkForQos(int qosId)
    {
        SQLiteDatabase sd = getWritableDatabase();

        Cursor cursor = sd.rawQuery("SELECT * FROM " + QosTable.TABLE_NAME+" WHERE "+QosTable.ID+"="+qosId,null);
        Cursor cursor2 = sd.rawQuery("SELECT "+Join.Network_ID+" FROM " + Join.TABLE_NAME+" WHERE "+Join.Qos_ID+"="+qosId,null);
        Cursor cursor3 = sd.rawQuery("SELECT * FROM " + NetworkTable.TABLE_NAME+" WHERE "+NetworkTable.ID+"="+cursor2.getString(0),null);

        while (cursor3.moveToNext()) {
            Log.w("GET ALL Networks FOR A GIVEN setting Qos","Note= "+cursor.getString(1) + "-- Time= " + cursor.getString(2) + "-- SSID= " + cursor3.getString(1));
        }
        cursor.close();
        cursor2.close();
        cursor3.close();
    }

    public Set<Integer> getSettingQosByNoteForNetwork(int netId,int note)
    {
        SQLiteDatabase sd = getWritableDatabase();
        // WE ONLY NEED TO RETURN COURSE IDS
        String[] cols = new String[] { Join.Qos_ID };
        String[] selectionArgs = new String[] {
                String.valueOf(netId) };
        // QUERY CLASS MAP FOR STUDENTS IN COURSE
        Cursor c = sd.query(Join.TABLE_NAME, cols,Join.Network_ID + "= ?", selectionArgs, null,null, null);
        Set<Integer> returnIds = new HashSet<Integer>();
        while (c.moveToNext())
        {
            int id = c.getInt(c.getColumnIndex
                    (Join.Qos_ID));
            returnIds.add(id);
        }

        // MAKE SECOND QUERY
        cols = new String[] { QosTable.ID };
        selectionArgs = new String[] { String.valueOf(note) };
        c = sd.query(QosTable.TABLE_NAME, cols,QosTable.Note + "= ?", selectionArgs, null, null, null);
        Set<Integer> gradeIds = new HashSet<Integer>();
        while (c.moveToNext()) {
            int id = c.getInt(c.getColumnIndex(QosTable.ID));
            gradeIds.add(id);
        }
        // RETURN INTERSECTION OF ID SETS
        returnIds.retainAll(gradeIds);
        return returnIds;
    }

    // METHOD FOR SAFELY REMOVING A Setting Qos
    @SuppressLint("LongLogTag")
    public boolean removeSettingQos(int qosId)
    {
        Log.w("====>METHOD FOR SAFELY REMOVING A Setting Qos","where qosID="+qosId+"!!");
        SQLiteDatabase sd = getWritableDatabase();
        String[] whereArgs = new String[]
                {
                        String.valueOf(qosId)
                };
        // DELETE ALL CLASS MAPPINGS Setting Qos IS SIGNED UP FOR
        sd.delete(Join.TABLE_NAME, Join.Qos_ID + "= ? ", whereArgs);
        // THEN DELETE STUDENT
        int result = sd.delete(QosTable.TABLE_NAME,QosTable.ID + "= ? ", whereArgs);
        return (result > 0);
    }

    // METHOD FOR SAFELY REMOVING A network
    @SuppressLint("LongLogTag")
    public boolean removeNetwork(int netId)
    {
        Log.w("====>METHOD FOR SAFELY REMOVING A network","where netID="+netId+"!!");
        SQLiteDatabase sd = getWritableDatabase();
        String[] whereArgs = new String[]
                {
                        String.valueOf(netId)
                };

        // MAKE SURE YOU REMOVE Network FROM ALL Settings Qos ENROLLED
        sd.delete(Join.TABLE_NAME, Join.Network_ID +"= ? ", whereArgs);
        // THEN DELETE COURSE
        int result = sd.delete(NetworkTable.TABLE_NAME,NetworkTable.ID + "= ? ", whereArgs);
        return (result > 0);
    }

}
