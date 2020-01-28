package mysongs.com.allgo.mysongs;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyDBName.db";
    public static final String songs_TABLE_NAME = "songs";
    public static final String songs_COLUMN_ID = "id";
    public static final String songs_COLUMN_NAME = "name";
    public static final String songs_COLUMN_language = "link";
    public static final String songs_COLUMN_mood = "language";
    public static final String songs_COLUMN_CITY = "mood";
    public static final String songs_COLUMN_link = "playlist";
    public static final String songs_COLUMN_NVISITS = "nvisits";
    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table songs " +
                        "(id integer primary key, name text,link text,language text, mood text,playlist text,nvisits integer)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS songs");
        onCreate(db);
    }

    public boolean insertContact(String name, String link, String language, String mood, String playlist, int nvisits) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("link", link);
        contentValues.put("language", language);
        contentValues.put("mood", mood);
        contentValues.put("playlist", playlist);
        contentValues.put("nvisits", nvisits);
        db.insert("songs", null, contentValues);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from songs where id=" + id + "", null);
        return res;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, songs_TABLE_NAME);
        return numRows;
    }

    public boolean updateContact(Integer id, String name, String link, String language, String mood, String playlist, int nvisits) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("link", link);
        contentValues.put("language", language);
        contentValues.put("mood", mood);
        contentValues.put("playlist", playlist);
        contentValues.put("nvisits",nvisits);
        db.update("songs", contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public Integer deleteContact(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("songs",
                "id = ? ",
                new String[]{Integer.toString(id)});
    }

    public ArrayList<String> getAllCotacts() {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from songs", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            array_list.add(res.getString(res.getColumnIndex(songs_COLUMN_NAME)));
            res.moveToNext();
        }
        return array_list;
    }
}