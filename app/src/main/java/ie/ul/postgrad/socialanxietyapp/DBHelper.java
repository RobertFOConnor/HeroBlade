package ie.ul.postgrad.socialanxietyapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import ie.ul.postgrad.socialanxietyapp.game.InventoryItemArray;
import ie.ul.postgrad.socialanxietyapp.game.Player;

/**
 * Created by Robert on 14-Mar-17.
 * <p>
 * SQL databse helper class for interaction with the database.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "AnxietyApp.db";
    public static final String PLAYERS_TABLE_NAME = "players";
    public static final String PLAYERS_COLUMN_ID = "id";
    public static final String PLAYERS_COLUMN_NAME = "name";
    public static final String PLAYERS_COLUMN_EMAIL = "email";
    public static final String PLAYERS_COLUMN_XP = "xp";
    public static final String PLAYERS_COLUMN_LEVEL = "level";
    public static final String PLAYERS_COLUMN_MONEY = "money";

    public static final String INVENTORY_TABLE_NAME = "inventory";
    public static final String INVENTORY_COLUMN_PLAYER_ID = "player_id";
    public static final String INVENTORY_COLUMN_ID = "item_id";
    public static final String INVENTORY_COLUMN_QUANTITY = "quantity";
    public static final String INVENTORY_COLUMN_DAMAGE = "damage";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + PLAYERS_TABLE_NAME + " (id integer primary key, name text, email text, xp integer, level integer, money integer)");
        db.execSQL("CREATE TABLE " + INVENTORY_TABLE_NAME + " (player_id integer key, item_id integer, quantity integer)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PLAYERS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + INVENTORY_TABLE_NAME);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public boolean insertPlayer(String name, String email, int xp, int level, int money) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PLAYERS_COLUMN_NAME, name);
        contentValues.put(PLAYERS_COLUMN_EMAIL, email);
        contentValues.put(PLAYERS_COLUMN_XP, xp);
        contentValues.put(PLAYERS_COLUMN_LEVEL, level);
        contentValues.put(PLAYERS_COLUMN_MONEY, money);
        db.insert(PLAYERS_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean insertItem(int player_id, int item_id, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(INVENTORY_COLUMN_PLAYER_ID, player_id);
        contentValues.put(INVENTORY_COLUMN_ID, item_id);
        contentValues.put(INVENTORY_COLUMN_QUANTITY, quantity);
        db.insert(INVENTORY_TABLE_NAME, null, contentValues);
        return true;
    }

    public Player getPlayer(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(PLAYERS_TABLE_NAME, new String[] { PLAYERS_COLUMN_ID,
                        PLAYERS_COLUMN_NAME, PLAYERS_COLUMN_EMAIL, PLAYERS_COLUMN_XP, PLAYERS_COLUMN_LEVEL, PLAYERS_COLUMN_MONEY }, PLAYERS_COLUMN_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        return new Player(cursor.getString(1), cursor.getString(2), Integer.parseInt(cursor.getString(3)), Integer.parseInt(cursor.getString(4)), Integer.parseInt(cursor.getString(5)));
    }

    public Cursor getInventoryData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + INVENTORY_TABLE_NAME + " WHERE " + INVENTORY_COLUMN_ID + "=" + id, null);
    }

    public int numberOfPlayers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, PLAYERS_TABLE_NAME);
    }

    public boolean updatePlayer(Player player) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PLAYERS_COLUMN_NAME, player.getName());
        contentValues.put(PLAYERS_COLUMN_EMAIL, player.getEmail());
        contentValues.put(PLAYERS_COLUMN_XP, player.getXp());
        contentValues.put(PLAYERS_COLUMN_LEVEL, player.getLevel());
        contentValues.put(PLAYERS_COLUMN_MONEY, player.getMoney());
        db.update(PLAYERS_TABLE_NAME, contentValues, PLAYERS_COLUMN_ID + " = ? ", new String[]{Integer.toString(1)});
        return true;
    }

    public boolean updateItem(Integer player_id, int item_id, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(INVENTORY_COLUMN_PLAYER_ID, player_id);
        contentValues.put(INVENTORY_COLUMN_ID, item_id);
        contentValues.put(INVENTORY_COLUMN_QUANTITY, quantity);
        db.update(INVENTORY_TABLE_NAME, contentValues, INVENTORY_COLUMN_ID + " = ? ", new String[]{Integer.toString(item_id)});
        return true;
    }

    public Integer deletePlayer(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(PLAYERS_TABLE_NAME,
                PLAYERS_COLUMN_ID + " = ? ",
                new String[]{Integer.toString(id)});
    }

    public Integer deleteItem(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(INVENTORY_TABLE_NAME,
                INVENTORY_COLUMN_ID + " = ? ",
                new String[]{Integer.toString(id)});
    }

    public ArrayList<String> getAllPlayers() {
        ArrayList<String> array_list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + PLAYERS_TABLE_NAME, null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            array_list.add(res.getString(res.getColumnIndex(PLAYERS_COLUMN_NAME)));
            res.moveToNext();
        }
        res.close();
        return array_list;
    }

    public InventoryItemArray getInventory() {
        InventoryItemArray array_list = new InventoryItemArray();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + INVENTORY_TABLE_NAME, null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            array_list.put(Integer.parseInt(res.getString(res.getColumnIndex(INVENTORY_COLUMN_ID))), Integer.parseInt(res.getString(res.getColumnIndex(INVENTORY_COLUMN_QUANTITY))));
            res.moveToNext();
        }
        res.close();
        return array_list;
    }
}
