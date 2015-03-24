package com.example.gb.connectapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

/**
 * Created by UT on 10/03/2015.
 */
public class BDD extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "PTut.db";
    // TOGGLE THIS NUMBER FOR UPDATING TABLES AND DATABASE
    private static final int DATABASE_VERSION = 1;

    private static final String TAG = "BDD";
    private static final String TAG1 = "Network --";
    private static final String TAG2 = "Qos --";
    private static final String TAG3 = "Jointure --";

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
                + Join.Qos_ID + " INTEGER,"
                +"FOREIGN KEY ( "+Join.Network_ID+") REFERENCES "+NetworkTable.TABLE_NAME+" ("+NetworkTable.ID+"),"
                +"FOREIGN KEY ( "+Join.Qos_ID+") REFERENCES "+QosTable.TABLE_NAME+" ("+QosTable.ID+"));");
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
            Log.d(TAG1,cursor.getString(0) + " " + cursor.getString(1) + " " + cursor.getString(2));
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
        Cursor cursor = sd.rawQuery("SELECT * FROM " + QosTable.TABLE_NAME,null);
        while (cursor.moveToNext()) {
            Log.d(TAG2,cursor.getString(0) + " " + cursor.getString(1) + " " + cursor.getString(2));
        }
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
        Cursor cursor = sd.rawQuery("SELECT * FROM " + Join.TABLE_NAME,null);
        while (cursor.moveToNext()) {
            Log.d(TAG3,cursor.getString(0) + " " + cursor.getString(1) + " " + cursor.getString(2));
        }
        return (result >= 0);
    }

    // GET ALL Settings Qos of one Network
    @SuppressLint("LongLogTag")
    public void printDatabase()
    {
        SQLiteDatabase sd = getWritableDatabase();

        String query = "SELECT " + NetworkTable.TABLE_NAME + "." + NetworkTable.ID + "," +
                        NetworkTable.TABLE_NAME + "." + NetworkTable.SSID + "," +
                        NetworkTable.TABLE_NAME + "." + NetworkTable.PresharedKey + ","+
                        QosTable.TABLE_NAME + "." + QosTable.ID + ","+
                        QosTable.TABLE_NAME + "." + QosTable.Note + ","+
                        QosTable.TABLE_NAME + "." + QosTable.Time ;

        query += "FROM " + NetworkTable.TABLE_NAME
                + " NATURAL JOIN " + Join.TABLE_NAME
                + " NATURAL JOIN " + QosTable.TABLE_NAME
                + " ";

        query += "ORDER BY " + QosTable.TABLE_NAME + "." + QosTable.Note + " DESC;";

        Cursor cursor = sd.rawQuery(query,null);
        Log.w("Database ", "ID_Network      ||   SSID     ||  Presharedkey    ||  ID_Qos    ||  Note   ||   Timestamp ");
        while (cursor.moveToNext()) {

            Log.w("Database ",  cursor.getString(0)+"      ||   "+cursor.getString(1)+"     ||  "+cursor.getString(2)+"    ||  "+cursor.getString(3)+"    ||  "+cursor.getString(4)+"   ||   "+cursor.getString(5)+" ");
        }
        cursor.close();
    }

    // GET ALL Network by mark
    @SuppressLint("LongLogTag")
    public NetworkDesc getNetworkByNote()
    {
        NetworkDesc nd = new NetworkDesc(null,null);
        SQLiteDatabase sd = getWritableDatabase();
        int Note=0;
        String query = "SELECT " + NetworkTable.TABLE_NAME + "." + NetworkTable.SSID + "," +
                NetworkTable.TABLE_NAME + "." + NetworkTable.PresharedKey + " ";
        query += "FROM " + NetworkTable.TABLE_NAME
                + " NATURAL JOIN " + Join.TABLE_NAME
                + " NATURAL JOIN " + QosTable.TABLE_NAME
                + " ";
        query += "ORDER BY " + QosTable.TABLE_NAME + "." + QosTable.Note + " DESC;";

        Cursor cursor = sd.rawQuery(query,null);
        if (cursor.moveToNext()) {
            nd = new NetworkDesc(cursor.getString(0), cursor.getString(1));
            Log.w("GET NETWORK BY MARK", "SSID= " + nd.getmName() + "-- PASS= " + nd.getmPass());
        }
        cursor.close();
        return nd;
    }

    @SuppressLint("LongLogTag")
    public NetworkDesc getNetworkByNoteFrom(ArrayList<String> ssids)
    {
        NetworkDesc nd = new NetworkDesc(null,null);
        SQLiteDatabase sd = getWritableDatabase();
        int Note=0;
        String query = "SELECT " + NetworkTable.TABLE_NAME + "." + NetworkTable.SSID + "," +
                NetworkTable.TABLE_NAME + "." + NetworkTable.PresharedKey + " ";

        query += "FROM " + NetworkTable.TABLE_NAME
                + " NATURAL JOIN " + Join.TABLE_NAME
                + " NATURAL JOIN " + QosTable.TABLE_NAME
                + " ";

        //Now check wifis from the list
        query += "WHERE ";
        //Go through the list but does not put "OR" after the last network
        ListIterator<String> it = ssids.listIterator();
        int i = 0;
        while (it.hasNext() && i < ssids.size() - 1) {
            query += NetworkTable.TABLE_NAME + "." + NetworkTable.SSID + "=\"" + it.next() + "\" OR ";
            i++;
        }
        query += NetworkTable.TABLE_NAME + "." + NetworkTable.SSID + "=\"" + it.next() + "\" ";


        query += "ORDER BY " + QosTable.TABLE_NAME + "." + QosTable.Note + " DESC;";

        Log.d(TAG,"Query is : " + query);

        Cursor cursor = sd.rawQuery(query,null);
        if (cursor.moveToNext()) {
            nd = new NetworkDesc(cursor.getString(0), cursor.getString(1));
            Log.w("GET NETWORK BY MARK", "SSID= " + nd.getmName() + "-- PASS= " + nd.getmPass());
        }
        cursor.close();
        return nd;
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
        // THEN DELETE QOS
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
        // THEN DELETE Network
        int result = sd.delete(NetworkTable.TABLE_NAME,NetworkTable.ID + "= ? ", whereArgs);
        return (result > 0);
    }

}
