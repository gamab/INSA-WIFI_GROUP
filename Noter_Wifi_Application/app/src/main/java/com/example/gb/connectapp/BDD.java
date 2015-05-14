package com.example.gb.connectapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.ListIterator;

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


    /**
     * Permet la création de la base de données, notamment des tables :
     * Network, QoS et Jointure, Jointure matérialise l'association entre un réseau et la QoS associée
     * @param db la database du système.
     */
    @SuppressLint("LongLogTag")
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // CREATE Network TABLE
        Log.w("====>CREATE Network TABLE"," with ID="+NetworkTable.ID+
                "  SSID="+NetworkTable.SSID+
                "  BSSID="+NetworkTable.BSSID+
                "  PresharedKey="+NetworkTable.PresharedKey+"!!");
        db.execSQL("CREATE TABLE " + NetworkTable.TABLE_NAME + " (" + NetworkTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + NetworkTable.SSID + " TEXT,"
                + NetworkTable.BSSID + " TEXT,"
                + NetworkTable.PresharedKey + " TEXT);");

        // CREATE Qos TABLE
        Log.w("====>CREATE QoS TABLE"," with ID="+QosTable.ID+"  Note="+QosTable.Note+"  Heure_deb="+QosTable.H_deb+"  Heure_fin="+QosTable.H_fin+"!!");
        db.execSQL("CREATE TABLE " + QosTable.TABLE_NAME + " ("
                + QosTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + QosTable.Note + " Integer,"
                + QosTable.H_deb + " TEXT,"
                + QosTable.H_fin + " TEXT);");

        // CREATE Join MAPPING TABLE
        Log.w("====>CREATE Join MAPPING TABLE"," with ID="+ JoinTable.ID+"  Network_ID="+ JoinTable.Network_ID+"  Qos_ID="+ JoinTable.Qos_ID+"!!");
        db.execSQL("CREATE TABLE " + JoinTable.TABLE_NAME + " (" + JoinTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + JoinTable.Network_ID + " INTEGER,"
                + JoinTable.Qos_ID + " INTEGER,"
                +"FOREIGN KEY ( "+ JoinTable.Network_ID+") REFERENCES "+NetworkTable.TABLE_NAME+" ("+NetworkTable.ID+"),"
                +"FOREIGN KEY ( "+ JoinTable.Qos_ID+") REFERENCES "+QosTable.TABLE_NAME+" ("+QosTable.ID+"));");
    }

    /**
     * Permet de réinitialiser la base de données
     * @param db la base de données à reinit
     * @param oldVersion Inutilisé
     * @param newVersion Inutilisé
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,int newVersion)
    {
        Log.w("====>LOG_TAG", "Upgrading database from version " + oldVersion + " to " + newVersion + ",which will destroy all old data");
        // KILL PREVIOUS TABLES IF UPGRADED
        db.execSQL("DROP TABLE IF EXISTS " + NetworkTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QosTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + JoinTable.TABLE_NAME);
        // CREATE NEW INSTANCE OF SCHEMA
        onCreate(db);
    }

    /**
     * Permet de réinitialiser la base de données. Ne fait qu'appeler onUpgrade()
     */
    public void deleteEverything() {
        SQLiteDatabase db = getWritableDatabase();
        onUpgrade(db,1,2);
        //onCreate(db);
    }

    // WRAPPER METHOD FOR ADDING A Network

    /**
     * Permet d'ajouter un réseau à la Base de données
     * @param SSID Le SSID du réseau
     * @param PresharedKey La clé associée à ce réseau
     * @return l'ID de la ligne nouvellement insérée ou -1 s'il y a eu erreur
     */
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

        return result;
    }

    /**
     * Permet d'ajouter un réseau à la Base de données
     * @param SSID Le SSID du réseau
     * @param BSSID Le BSSID du réseau
     * @param PresharedKey La clé associée à ce réseau
     * @return l'ID de la ligne nouvellement insérée ou -1 s'il y a eu erreur
     */
    @SuppressLint("LongLogTag")
    public long addNetwork(String SSID,String BSSID, String PresharedKey)
    {
        Log.w("====>WRAPPER METHOD FOR ADDING A Network"," with SSID="+SSID+"  PresharedKey="+PresharedKey+"!!");
        // CREATE A CONTENTVALUE OBJECT
        ContentValues cv = new ContentValues();
        cv.put(NetworkTable.SSID, SSID);
        cv.put(NetworkTable.BSSID, BSSID);
        cv.put(NetworkTable.PresharedKey, PresharedKey);
        // RETRIEVE WRITEABLE DATABASE AND INSERT
        SQLiteDatabase sd = getWritableDatabase();
        long result = sd.insert(NetworkTable.TABLE_NAME,null, cv);

        return result;
    }


    // WRAPPER METHOD FOR ADDING A Qos

    /**
     * Permet d'ajouter une entrée à la table QoS, correspondant à une note et une heure
     * @param note correspond à la note donnée par l'utilisateur à un réseau
     * @param time_deb correspond à l'horaire de connexion debut à ce réseau
     * @param time_fin correspond à l'horaire de connexion fin à ce réseau
     * @return l'ID de la ligne nouvellement insérée ou -1 s'il y a eu erreur
     */
    @SuppressLint("LongLogTag")
    public long addQos(String note, String time_deb,String time_fin)
    {
        Log.w("====>WRAPPER METHOD FOR ADDING A Qos"," with Note="+note+"  Time_deb="+time_deb+"  Time_fin="+time_fin+"!!");
        ContentValues cv = new ContentValues();
        cv.put(QosTable.Note, note);
        cv.put(QosTable.H_deb, time_deb);
        cv.put(QosTable.H_fin, time_fin);
        SQLiteDatabase sd = getWritableDatabase();
        long result = sd.insert(QosTable.TABLE_NAME,null, cv);

        return result;
    }

    // WRAPPER METHOD FOR ENROLLING A Setting INTO A Network

    /**
     * Permet de mettre en relation Réseau et QoS
     * @param netId l'id du réseau dans la table network
     * @param qosId l'id de qos dans la table qos
     * @return true si la ligne a été insérée, false sinon
     */
    @SuppressLint("LongLogTag")
    public boolean enrollSettingClass(int netId, int qosId)
    {
        Log.w("====>WRAPPER METHOD FOR ENROLLING A Setting INTO A Network"," with Network_ID="+netId+"  Qos_ID="+qosId+"!!");
        ContentValues cv = new ContentValues();
        cv.put(JoinTable.Network_ID, netId);
        cv.put(JoinTable.Qos_ID, qosId);
        SQLiteDatabase sd = getWritableDatabase();
        long result = sd.insert(JoinTable.TABLE_NAME, JoinTable.Qos_ID, cv);

        return (result >= 0);
    }

    /**
     * Permet l'affichage de la jonction de la table network avec la table QoS dans les log
     */
    @SuppressLint("LongLogTag")
    public void printDatabase()
    {
        SQLiteDatabase sd = getWritableDatabase();

        String query = "SELECT " + NetworkTable.TABLE_NAME + "." + NetworkTable.ID + ", " +
                NetworkTable.TABLE_NAME + "." + NetworkTable.SSID + ", " +
                NetworkTable.TABLE_NAME + "." + NetworkTable.BSSID + ", " +
                NetworkTable.TABLE_NAME + "." + NetworkTable.PresharedKey + ", "+
                QosTable.TABLE_NAME + "." + QosTable.ID + ", "+
                QosTable.TABLE_NAME + "." + QosTable.Note + ", "+
                QosTable.TABLE_NAME + "." + QosTable.H_deb + ", "+
                QosTable.TABLE_NAME + "." + QosTable.H_fin + " ";

        query += "FROM " + NetworkTable.TABLE_NAME
                + " NATURAL JOIN " + JoinTable.TABLE_NAME
                + " NATURAL JOIN " + QosTable.TABLE_NAME
                + " ";

        query += "ORDER BY " + QosTable.TABLE_NAME + "." + QosTable.Note + " DESC;";

        Cursor cursor = sd.rawQuery(query,null);
        Log.d(TAG, "ID_Network  ||  SSID  ||  BSSID  ||  Presharedkey  ||  ID_Qos  ||  Note ||  Heure_deb  ||  Heure_fin ");
        while (cursor.moveToNext()) {

            Log.d(TAG,  cursor.getString(0)+
                    "   ||   " + cursor.getString(1) +
                    "   ||   " + cursor.getString(2)+
                    "   ||   " + cursor.getString(3)+
                    "   ||   " + cursor.getString(4)+
                    "   ||   " + cursor.getString(5)+
                    "   ||   " + cursor.getString(6)+
                    "   ||   " + cursor.getString(7)+" ");
        }
        cursor.close();
    }

    /**
     * Permet de récupérer le meilleur réseau de la base de données
     * @return le meilleur réseau de la base de donnée
     */
    @SuppressLint("LongLogTag")
    public NetworkDesc getNetworkByNote()
    {
        NetworkDesc nd = new NetworkDesc(null,null,null);
        SQLiteDatabase sd = getWritableDatabase();

        String query = "SELECT " + NetworkTable.TABLE_NAME + "." + NetworkTable.SSID + "," +
                NetworkTable.TABLE_NAME + "." + NetworkTable.BSSID +
                NetworkTable.TABLE_NAME + "." + NetworkTable.PresharedKey + " ";
        query += "FROM " + NetworkTable.TABLE_NAME
                + " NATURAL JOIN " + JoinTable.TABLE_NAME
                + " NATURAL JOIN " + QosTable.TABLE_NAME
                + " ";
        query += "ORDER BY " + QosTable.TABLE_NAME + "." + QosTable.Note + " DESC;";

        Cursor cursor = sd.rawQuery(query,null);
        if (cursor.moveToNext()) {
            nd.setmName(cursor.getString(0));
            nd.setmBSSID(cursor.getString(1));
            nd.setmPass(cursor.getString(2));
            Log.w("GET NETWORK BY MARK", "SSID= " + nd.getmName() + "-- BSSID= "  + nd.getmBSSID() + "-- PASS= " + nd.getmPass());
        }

        cursor.close();
        return nd;
    }

    /**
     * Permet de récupérer le meilleur réseau de la base de données parmi une liste de réseaux
     * @param networks la liste de réseaux
     * @return le meilleur réseau
     */
    @SuppressLint("LongLogTag")
    public NetworkDesc getNetworkByNoteFrom(ArrayList<NetworkDesc> networks)
    {
        NetworkDesc nd = new NetworkDesc(null,null, null);
        NetworkDesc curNd;
        SQLiteDatabase sd = getWritableDatabase();

        String query = "SELECT " +
                NetworkTable.TABLE_NAME + "." + NetworkTable.SSID + "," +
                NetworkTable.TABLE_NAME + "." + NetworkTable.BSSID + "," +
                NetworkTable.TABLE_NAME + "." + NetworkTable.PresharedKey + " ";

        query += "FROM " + NetworkTable.TABLE_NAME
                + " NATURAL JOIN " + JoinTable.TABLE_NAME
                + " NATURAL JOIN " + QosTable.TABLE_NAME
                + " ";

        //Now check wifis from the list
        query += "WHERE ";
        //Go through the list but does not put "OR" after the last network
        ListIterator<NetworkDesc> it = networks.listIterator();
        int i = 0;
        while (it.hasNext() && i < networks.size() - 1) {
            curNd = it.next();
            query += "(" + NetworkTable.TABLE_NAME + "." + NetworkTable.SSID + "=\"" + curNd.getmName() + "\"" +
                    " AND " + NetworkTable.TABLE_NAME + "." + NetworkTable.BSSID + "=\"" + curNd.getmBSSID() + "\") OR ";
            i++;
        }
        curNd = it.next();
        query += "(" + NetworkTable.TABLE_NAME + "." + NetworkTable.SSID + "=\"" + curNd.getmName() + "\"" +
                " AND " + NetworkTable.TABLE_NAME + "." + NetworkTable.BSSID + "=\"" + curNd.getmBSSID() + "\") ";


        query += "ORDER BY " + QosTable.TABLE_NAME + "." + QosTable.Note + " DESC;";

        Log.d(TAG,"Query is : " + query);

        Cursor cursor = sd.rawQuery(query,null);
        if (cursor.moveToNext()) {
            nd.setmName(cursor.getString(0));
            nd.setmBSSID(cursor.getString(1));
            nd.setmPass(cursor.getString(2));
            Log.w("GET NETWORK BY MARK", "SSID= " + nd.getmName() + "-- BSSID= "  + nd.getmBSSID() + "-- PASS= " + nd.getmPass());
        }
        cursor.close();
        return nd;
    }


    /**
     * Permet de supprimer une entrée QoS de la table QoS
     * @param qosId l'id de l'entrée à supprimer
     * @return si ça s'est bien passé ou non
     */
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
        sd.delete(JoinTable.TABLE_NAME, JoinTable.Qos_ID + "= ? ", whereArgs);
        // THEN DELETE QOS
        int result = sd.delete(QosTable.TABLE_NAME,QosTable.ID + "= ? ", whereArgs);
        return (result > 0);
    }


    /**
     * Permet de supprimer une entrée de la table networks
     * @param netId l'id de l'entrée à supprimer
     * @return si ça s'est bien passé ou non
     */
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
        sd.delete(JoinTable.TABLE_NAME, JoinTable.Network_ID +"= ? ", whereArgs);
        // THEN DELETE Network
        int result = sd.delete(NetworkTable.TABLE_NAME,NetworkTable.ID + "= ? ", whereArgs);
        return (result > 0);
    }

    /**
     * Permet de récupérer le meilleur réseau wifi en fonction de l'heure locale au téléphone
     * @return une description de ce wifi
     */
    public NetworkDesc getBestNetworkForCurrentTime() {
        Log.d(TAG,"====>METHOD FOR GETTING THE BEST NETWORK AVAILABLE FOR CURRENT TIME");

        SQLiteDatabase sd = getWritableDatabase();

        String query = "SELECT " +
                NetworkTable.TABLE_NAME + "." + NetworkTable.SSID + "," +
                NetworkTable.TABLE_NAME + "." + NetworkTable.BSSID + "," +
                NetworkTable.TABLE_NAME + "." + NetworkTable.PresharedKey + " ";

        query += "FROM " + NetworkTable.TABLE_NAME
                + " NATURAL JOIN " + JoinTable.TABLE_NAME
                + " NATURAL JOIN " + QosTable.TABLE_NAME
                + " ";

        //Now check wifis from the list
        query += "WHERE " +
                "time('now','localtime') <= " + QosTable.TABLE_NAME + "." + QosTable.H_fin + " AND " +
                "time('now','localtime') >= " + QosTable.TABLE_NAME + "." + QosTable.H_deb + " ";

        query += "ORDER BY " + QosTable.TABLE_NAME + "." + QosTable.Note + " DESC ";
        query += "LIMIT 1;";

        Log.d(TAG,"Query is : " + query);

        Cursor cursor = sd.rawQuery(query,null);
        NetworkDesc nd = null;
        Log.d(TAG,"Res is :");
        if (cursor.moveToNext()) {
            nd = new NetworkDesc(cursor.getString(0),cursor.getString(1),cursor.getString(2));

        }

        cursor.close();

        return nd;
    }
}
