package ie.ul.postgrad.socialanxietyapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;

import ie.ul.postgrad.socialanxietyapp.game.InventoryItemArray;
import ie.ul.postgrad.socialanxietyapp.game.Player;
import ie.ul.postgrad.socialanxietyapp.game.item.ItemFactory;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponItem;

/**
 * Created by Robert on 14-Mar-17.
 * <p>
 * SQL databse helper class for interaction with the database.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 10;

    private static final String DATABASE_NAME = "AnxietyApp.db";
    private static final String PLAYERS_TABLE_NAME = "players";
    private static final String PLAYERS_COLUMN_ID = "id";
    private static final String PLAYERS_COLUMN_NAME = "name";
    private static final String PLAYERS_COLUMN_EMAIL = "email";
    private static final String PLAYERS_COLUMN_LEVEL = "level";
    private static final String PLAYERS_COLUMN_XP = "xp";
    private static final String PLAYERS_COLUMN_MONEY = "money";
    private static final String PLAYERS_COLUMN_MAX_HEALTH = "max_health";
    private static final String PLAYERS_COLUMN_CURR_HEALTH = "curr_health";
    private static final String PLAYERS_COLUMN_QUESTS = "quests";

    private static final String INVENTORY_TABLE_NAME = "inventory";
    private static final String INVENTORY_COLUMN_PLAYER_ID = "player_id";
    private static final String INVENTORY_COLUMN_ID = "item_id";
    private static final String INVENTORY_COLUMN_QUANTITY = "quantity";

    private static final String WEAPON_TABLE_NAME = "weapon_inventory";
    private static final String WEAPON_COLUMN_PLAYER_ID = "player_id";
    private static final String WEAPON_COLUMN_UUID = "weapon_uuid";
    private static final String WEAPON_COLUMN_ID = "weapon_id";
    private static final String WEAPON_COLUMN_CURR_HEALTH = "curr_health";

    private static final String TRAVEL_TABLE_NAME = "statistics";
    private static final String TRAVEL_COLUMN_PLAYER_ID = "player_id";
    private static final String TRAVEL_COLUMN_CREATION_DATE = "creation_date";
    private static final String TRAVEL_COLUMN_STEPS_COUNT = "step_count";
    private static final String TRAVEL_COLUMN_DISTANCE = "distance";
    private static final String TRAVEL_COLUMN_TIME_PLAYED = "time_played";

    private static final String AVATAR_TABLE_NAME = "avatar";
    private static final String AVATAR_COLUMN_PLAYER_ID = "player_id";
    private static final String AVATAR_COLUMN_SKIN_COLOR = "skin_color";
    private static final String AVATAR_COLUMN_HAIR_TYPE = "hair_type";
    private static final String AVATAR_COLUMN_HAIR_COLOR = "hair_color";
    private static final String AVATAR_COLUMN_SHIRT_COLOR = "shirt";
    //http://stackoverflow.com/questions/16545378/how-can-i-fetch-data-from-a-web-server-in-an-android-application
    private Context context;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + PLAYERS_TABLE_NAME + " (id integer primary key, name text, email text, xp integer, level integer, money integer, max_health integer, curr_health integer)");
        db.execSQL("CREATE TABLE " + INVENTORY_TABLE_NAME + " (player_id integer key, item_id integer, quantity integer)");
        db.execSQL("CREATE TABLE " + WEAPON_TABLE_NAME + " (player_id integer key, weapon_uuid text, weapon_id integer, curr_health integer)");
        db.execSQL("CREATE TABLE " + TRAVEL_TABLE_NAME + " (player_id integer key, creation_date text, step_count integer, distance integer)");
        db.execSQL("CREATE TABLE " + AVATAR_TABLE_NAME + " (player_id integer key, hair_type integer, hair_color integer)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PLAYERS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + INVENTORY_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TRAVEL_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AVATAR_TABLE_NAME);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public boolean insertAvatar(Avatar avatar) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(AVATAR_COLUMN_PLAYER_ID, 1);
        contentValues.put(AVATAR_COLUMN_HAIR_TYPE, avatar.getHairtype());
        contentValues.put(AVATAR_COLUMN_HAIR_COLOR, avatar.getHairColor());
        db.insert(AVATAR_TABLE_NAME, null, contentValues);

        return true;
    }

    public boolean updateAvatar(Avatar avatar) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(AVATAR_COLUMN_HAIR_TYPE, avatar.getHairtype());
        contentValues.put(AVATAR_COLUMN_HAIR_COLOR, avatar.getHairColor());
        db.update(AVATAR_TABLE_NAME, contentValues, AVATAR_COLUMN_PLAYER_ID + " = ? ", new String[]{Integer.toString(1)});
        return true;
    }

    public Avatar getAvatar(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(AVATAR_TABLE_NAME, new String[]{AVATAR_COLUMN_PLAYER_ID,
                        AVATAR_COLUMN_HAIR_TYPE, AVATAR_COLUMN_HAIR_COLOR}, AVATAR_COLUMN_PLAYER_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
        }
        Avatar avatar = new Avatar(cursor.getInt(1), cursor.getInt(2));
        cursor.close();
        db.close();
        return avatar;
    }

    public boolean insertPlayer(String name, String email, int xp, int level, int money, int maxHealth) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PLAYERS_COLUMN_NAME, name);
        contentValues.put(PLAYERS_COLUMN_EMAIL, email);
        contentValues.put(PLAYERS_COLUMN_XP, xp);
        contentValues.put(PLAYERS_COLUMN_LEVEL, level);
        contentValues.put(PLAYERS_COLUMN_MONEY, money);
        contentValues.put(PLAYERS_COLUMN_MAX_HEALTH, maxHealth);
        contentValues.put(PLAYERS_COLUMN_CURR_HEALTH, maxHealth);
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

    public boolean insertWeapon(int player_id, String UUID, int weapon_id, int curr_health) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(WEAPON_COLUMN_PLAYER_ID, player_id);
        contentValues.put(WEAPON_COLUMN_UUID, UUID);
        contentValues.put(WEAPON_COLUMN_ID, weapon_id);
        contentValues.put(WEAPON_COLUMN_CURR_HEALTH, curr_health);
        db.insert(WEAPON_TABLE_NAME, null, contentValues);
        return true;
    }

    public Player getPlayer(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(PLAYERS_TABLE_NAME, new String[]{PLAYERS_COLUMN_ID,
                        PLAYERS_COLUMN_NAME, PLAYERS_COLUMN_EMAIL, PLAYERS_COLUMN_XP, PLAYERS_COLUMN_LEVEL, PLAYERS_COLUMN_MONEY, PLAYERS_COLUMN_MAX_HEALTH, PLAYERS_COLUMN_CURR_HEALTH}, PLAYERS_COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            Player player = new Player(cursor.getString(1), cursor.getString(2), Integer.parseInt(cursor.getString(3)), Integer.parseInt(cursor.getString(4)), Integer.parseInt(cursor.getString(5)), Integer.parseInt(cursor.getString(6)), Integer.parseInt(cursor.getString(7)));
            cursor.close();
            db.close();
            return player;
        } else {
            return null;
        }
    }

    public Cursor getInventoryData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(selectAllQuery(INVENTORY_TABLE_NAME) + " WHERE " + INVENTORY_COLUMN_ID + "=" + id, null);
    }

    public Cursor getWeaponsData(String uuid) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(selectAllQuery(WEAPON_TABLE_NAME) + " WHERE " + WEAPON_COLUMN_UUID + "='" + uuid + "'", null);
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
        contentValues.put(PLAYERS_COLUMN_MAX_HEALTH, player.getMaxHealth());
        contentValues.put(PLAYERS_COLUMN_CURR_HEALTH, player.getCurrHealth());
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

    public boolean updateWeapon(Integer player_id, String UUID, int weapon_id, int curr_health) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(WEAPON_COLUMN_PLAYER_ID, player_id);
        contentValues.put(WEAPON_COLUMN_ID, UUID);
        contentValues.put(WEAPON_COLUMN_ID, weapon_id);
        contentValues.put(WEAPON_COLUMN_CURR_HEALTH, curr_health);
        db.update(WEAPON_TABLE_NAME, contentValues, WEAPON_COLUMN_UUID + " = ? ", new String[]{UUID});
        return true;
    }

    public Integer deleteItem(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(INVENTORY_TABLE_NAME,
                INVENTORY_COLUMN_ID + " = ? ",
                new String[]{Integer.toString(id)});
    }

    public Integer deleteWeapon(String UUID) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(WEAPON_TABLE_NAME,
                WEAPON_COLUMN_UUID + " = ? ",
                new String[]{UUID});
    }

    public Integer deletePlayer(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(PLAYERS_TABLE_NAME,
                PLAYERS_COLUMN_ID + " = ? ",
                new String[]{Integer.toString(id)});
    }

    public Integer deleteAvatar(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(AVATAR_TABLE_NAME,
                AVATAR_COLUMN_PLAYER_ID + " = ? ",
                new String[]{Integer.toString(id)});
    }

    public InventoryItemArray getInventory() {
        InventoryItemArray array_list = new InventoryItemArray();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(selectAllQuery(INVENTORY_TABLE_NAME), null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            array_list.put(Integer.parseInt(res.getString(res.getColumnIndex(INVENTORY_COLUMN_ID))), Integer.parseInt(res.getString(res.getColumnIndex(INVENTORY_COLUMN_QUANTITY))));
            res.moveToNext();
        }
        res.close();
        return array_list;
    }

    public ArrayList<WeaponItem> getWeapons() {
        ArrayList<WeaponItem> array_list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(selectAllQuery(WEAPON_TABLE_NAME), null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            WeaponItem weaponItem = (WeaponItem) ItemFactory.buildItem(context, Integer.parseInt(res.getString(res.getColumnIndex(WEAPON_COLUMN_ID))));
            weaponItem.setCurrHealth(Integer.parseInt(res.getString(res.getColumnIndex(WEAPON_COLUMN_CURR_HEALTH))));
            weaponItem.setUUID(res.getString(res.getColumnIndex(WEAPON_COLUMN_UUID)));
            array_list.add(weaponItem);
            res.moveToNext();
        }
        res.close();
        return array_list;
    }

    public boolean insertStepsEntry(float totalDistance) {

        boolean isDateAlreadyPresent = false;
        boolean createSuccessful = false;
        int currentDateStepCounts = 0;
        Calendar mCalendar = Calendar.getInstance();
        String todayDate = String.valueOf(mCalendar.get(Calendar.MONTH)) + "/" + String.valueOf(mCalendar.get(Calendar.DAY_OF_MONTH)) + "/" + String.valueOf(mCalendar.get(Calendar.YEAR));
        String selectQuery = "SELECT " + TRAVEL_COLUMN_STEPS_COUNT + " FROM " + TRAVEL_TABLE_NAME + " WHERE " + TRAVEL_COLUMN_CREATION_DATE + " = '" + todayDate + "'";
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {
                    isDateAlreadyPresent = true;
                    currentDateStepCounts = c.getInt((c.getColumnIndex(TRAVEL_COLUMN_STEPS_COUNT)));
                } while (c.moveToNext());
            }
            c.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(TRAVEL_COLUMN_PLAYER_ID, 1);
            values.put(TRAVEL_COLUMN_CREATION_DATE, todayDate);
            values.put(TRAVEL_COLUMN_DISTANCE, totalDistance);
            if (isDateAlreadyPresent) {
                values.put(TRAVEL_COLUMN_STEPS_COUNT, ++currentDateStepCounts);
                int row = db.update(TRAVEL_TABLE_NAME, values,
                        TRAVEL_COLUMN_CREATION_DATE + " = '" + todayDate + "'", null);
                if (row == 1) {
                    createSuccessful = true;
                }
                db.close();
            } else {
                values.put(TRAVEL_COLUMN_STEPS_COUNT, 1);
                long row = db.insert(TRAVEL_TABLE_NAME, null, values);
                if (row != -1) {
                    createSuccessful = true;
                }

            }
            db.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return createSuccessful;
    }

    public int getSteps() {
        int steps = 0;
        String selectQuery = selectAllQuery(TRAVEL_TABLE_NAME) + " WHERE " + TRAVEL_COLUMN_PLAYER_ID + "=" + 1;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                steps = c.getInt((c.getColumnIndex(TRAVEL_COLUMN_STEPS_COUNT)));
            }

            while (c.moveToNext()) {
                steps += c.getInt((c.getColumnIndex(TRAVEL_COLUMN_STEPS_COUNT)));
            }
            c.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return steps;
    }

    public ArrayList<Integer> getDailySteps() {

        ArrayList<Integer> steps = new ArrayList<>();

        String selectQuery = selectAllQuery(TRAVEL_TABLE_NAME) + " WHERE " + TRAVEL_COLUMN_PLAYER_ID + "=" + 1;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                steps.add(c.getInt((c.getColumnIndex(TRAVEL_COLUMN_STEPS_COUNT))));
            }
            while (c.moveToNext()) {
                steps.add(c.getInt((c.getColumnIndex(TRAVEL_COLUMN_STEPS_COUNT))));

            }
            c.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return steps;
    }

    public int getDistance() {
        int distance = 0;
        String selectQuery = selectAllQuery(TRAVEL_TABLE_NAME) + " WHERE " + TRAVEL_COLUMN_PLAYER_ID + "=" + 1;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                distance = c.getInt((c.getColumnIndex(TRAVEL_COLUMN_DISTANCE)));
            }
            while (c.moveToNext()) {
                distance += c.getInt((c.getColumnIndex(TRAVEL_COLUMN_DISTANCE)));
            }
            c.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return distance;
    }

    private String selectAllQuery(String tableName) {
        return "SELECT * FROM " + tableName;
    }
}
