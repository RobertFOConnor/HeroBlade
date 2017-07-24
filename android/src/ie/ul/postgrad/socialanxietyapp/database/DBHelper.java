package ie.ul.postgrad.socialanxietyapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

import ie.ul.postgrad.socialanxietyapp.Avatar;
import ie.ul.postgrad.socialanxietyapp.game.InventoryItemArray;
import ie.ul.postgrad.socialanxietyapp.game.Player;
import ie.ul.postgrad.socialanxietyapp.game.item.ChestItem;
import ie.ul.postgrad.socialanxietyapp.game.item.ItemFactory;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponFactory;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponItem;

/**
 * Created by Robert on 14-Mar-17.
 * <p>
 * SQL database helper class for interaction with the database.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 41;

    private static final String DATABASE_NAME = "AnxietyApp.db";

    private static final String USER_TABLE_NAME = "user";
    private static final String USER_COLUMN_ID = "id";
    private static final String USER_COLUMN_NAME = "name";
    private static final String USER_COLUMN_EMAIL = "email";
    private static final String USER_COLUMN_PASSWORD = "password";

    private static final String PLAYERS_TABLE_NAME = "players";
    private static final String PLAYERS_COLUMN_ID = "id";
    private static final String PLAYERS_COLUMN_NAME = "name";
    private static final String PLAYERS_COLUMN_LEVEL = "level";
    private static final String PLAYERS_COLUMN_XP = "xp";
    private static final String PLAYERS_COLUMN_MONEY = "money";
    private static final String PLAYERS_COLUMN_MAX_HEALTH = "max_health";
    private static final String PLAYERS_COLUMN_CURR_HEALTH = "curr_health";

    private static final String ITEM_TABLE_NAME = "item";
    private static final String ITEM_COLUMN_PLAYER_ID = "player_id";
    private static final String ITEM_COLUMN_ID = "item_id";
    private static final String ITEM_COLUMN_QUANTITY = "quantity";

    private static final String WEAPON_TABLE_NAME = "weapon_inventory";
    private static final String WEAPON_COLUMN_PLAYER_ID = "player_id";
    private static final String WEAPON_COLUMN_UUID = "weapon_uuid";
    private static final String WEAPON_COLUMN_ID = "weapon_id";
    private static final String WEAPON_COLUMN_CURR_HEALTH = "curr_health";
    private static final String WEAPON_COLUMN_EQUIPPED = "equipped";

    private static final String CHEST_TABLE_NAME = "chest_inventory";
    private static final String CHEST_COLUMN_PLAYER_ID = "player_id";
    private static final String CHEST_COLUMN_UUID = "chest_uuid";
    private static final String CHEST_COLUMN_ID = "chest_id";
    private static final String CHEST_COLUMN_DISTANCE_LEFT = "distance_left";

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
    private static final String AVATAR_COLUMN_SHIRT_COLOR = "shirt_color";
    private Context context;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + USER_TABLE_NAME + " (id text primary key, name text, email text, password text)");
        db.execSQL("CREATE TABLE " + PLAYERS_TABLE_NAME + " (id text primary key, name text, xp integer, level integer, money integer, max_health integer, curr_health integer)");
        db.execSQL("CREATE TABLE " + ITEM_TABLE_NAME + " (player_id text key, item_id integer, quantity integer)");
        db.execSQL("CREATE TABLE " + WEAPON_TABLE_NAME + " (player_id text key, weapon_uuid text, weapon_id integer, curr_health integer, equipped integer)");
        db.execSQL("CREATE TABLE " + CHEST_TABLE_NAME + " (player_id text key, chest_uuid text, chest_id integer, distance_left real)");
        db.execSQL("CREATE TABLE " + TRAVEL_TABLE_NAME + " (player_id text key, creation_date text, step_count integer, distance integer)");
        db.execSQL("CREATE TABLE " + AVATAR_TABLE_NAME + " (player_id text key, shirt_color integer, skin_color integer, hair_type integer, hair_color integer)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PLAYERS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ITEM_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WEAPON_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CHEST_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TRAVEL_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AVATAR_TABLE_NAME);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public boolean insertUser(String id, String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_COLUMN_ID, id);
        contentValues.put(USER_COLUMN_NAME, name);
        contentValues.put(USER_COLUMN_EMAIL, email);
        contentValues.put(USER_COLUMN_PASSWORD, password);
        db.insert(USER_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean insertPlayer(Player player) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PLAYERS_COLUMN_ID, player.getId());
        contentValues.put(PLAYERS_COLUMN_NAME, player.getName());
        contentValues.put(PLAYERS_COLUMN_XP, player.getXp());
        contentValues.put(PLAYERS_COLUMN_LEVEL, player.getLevel());
        contentValues.put(PLAYERS_COLUMN_MONEY, player.getMoney());
        contentValues.put(PLAYERS_COLUMN_MAX_HEALTH, player.getMaxHealth());
        contentValues.put(PLAYERS_COLUMN_CURR_HEALTH, player.getCurrHealth());
        db.insert(PLAYERS_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean insertAvatar(Avatar avatar) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(AVATAR_COLUMN_PLAYER_ID, getPlayer().getId());
        contentValues.put(AVATAR_COLUMN_SHIRT_COLOR, avatar.getShirtColor());
        contentValues.put(AVATAR_COLUMN_SKIN_COLOR, avatar.getSkinColor());
        contentValues.put(AVATAR_COLUMN_HAIR_TYPE, avatar.getHairtype());
        contentValues.put(AVATAR_COLUMN_HAIR_COLOR, avatar.getHairColor());
        db.insert(AVATAR_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean insertItem(String player_id, int item_id, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ITEM_COLUMN_PLAYER_ID, player_id);
        contentValues.put(ITEM_COLUMN_ID, item_id);
        contentValues.put(ITEM_COLUMN_QUANTITY, quantity);
        db.insert(ITEM_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean insertWeapon(String player_id, String UUID, int weapon_id, int curr_health, boolean equipped) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(WEAPON_COLUMN_PLAYER_ID, player_id);
        contentValues.put(WEAPON_COLUMN_UUID, UUID);
        contentValues.put(WEAPON_COLUMN_ID, weapon_id);
        contentValues.put(WEAPON_COLUMN_CURR_HEALTH, curr_health);
        if (equipped) {
            contentValues.put(WEAPON_COLUMN_EQUIPPED, 1);
        } else {
            contentValues.put(WEAPON_COLUMN_EQUIPPED, 0);
        }
        db.insert(WEAPON_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean insertChest(String player_id, String UUID, int chest_id, float distance_left) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CHEST_COLUMN_PLAYER_ID, player_id);
        contentValues.put(CHEST_COLUMN_UUID, UUID);
        contentValues.put(CHEST_COLUMN_ID, chest_id);
        contentValues.put(CHEST_COLUMN_DISTANCE_LEFT, distance_left);
        db.insert(CHEST_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean updatePlayer(Player player) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PLAYERS_COLUMN_NAME, player.getName());
        contentValues.put(PLAYERS_COLUMN_XP, player.getXp());
        contentValues.put(PLAYERS_COLUMN_LEVEL, player.getLevel());
        contentValues.put(PLAYERS_COLUMN_MONEY, player.getMoney());
        contentValues.put(PLAYERS_COLUMN_MAX_HEALTH, player.getMaxHealth());
        contentValues.put(PLAYERS_COLUMN_CURR_HEALTH, player.getCurrHealth());
        db.update(PLAYERS_TABLE_NAME, contentValues, PLAYERS_COLUMN_ID + " = ? ", new String[]{player.getId()});
        return true;
    }

    public boolean updateAvatar(Avatar avatar) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(AVATAR_COLUMN_SHIRT_COLOR, avatar.getShirtColor());
        contentValues.put(AVATAR_COLUMN_SKIN_COLOR, avatar.getSkinColor());
        contentValues.put(AVATAR_COLUMN_HAIR_TYPE, avatar.getHairtype());
        contentValues.put(AVATAR_COLUMN_HAIR_COLOR, avatar.getHairColor());
        db.update(AVATAR_TABLE_NAME, contentValues, AVATAR_COLUMN_PLAYER_ID + " = ? ", new String[]{getPlayer().getId()});
        return true;
    }

    public boolean updateItem(String player_id, int item_id, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ITEM_COLUMN_PLAYER_ID, player_id);
        contentValues.put(ITEM_COLUMN_ID, item_id);
        contentValues.put(ITEM_COLUMN_QUANTITY, quantity);
        db.update(ITEM_TABLE_NAME, contentValues, ITEM_COLUMN_ID + " = ? ", new String[]{Integer.toString(item_id)});
        return true;
    }

    public boolean updateWeapon(String player_id, String UUID, int weapon_id, int curr_health, boolean equipped) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(WEAPON_COLUMN_PLAYER_ID, player_id);
        contentValues.put(WEAPON_COLUMN_UUID, UUID);
        contentValues.put(WEAPON_COLUMN_ID, weapon_id);
        contentValues.put(WEAPON_COLUMN_CURR_HEALTH, curr_health);
        if (equipped) {
            contentValues.put(WEAPON_COLUMN_EQUIPPED, 1);
        } else {
            contentValues.put(WEAPON_COLUMN_EQUIPPED, 0);
        }
        db.update(WEAPON_TABLE_NAME, contentValues, WEAPON_COLUMN_UUID + " = ? ", new String[]{UUID});
        return true;
    }

    public boolean updateChest(String player_id, String UID, int chest_id, float distance_left) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CHEST_COLUMN_PLAYER_ID, player_id);
        contentValues.put(CHEST_COLUMN_UUID, UID);
        contentValues.put(CHEST_COLUMN_ID, chest_id);
        contentValues.put(CHEST_COLUMN_DISTANCE_LEFT, distance_left);
        db.update(CHEST_TABLE_NAME, contentValues, CHEST_COLUMN_UUID + " = ? ", new String[]{UID});
        return true;
    }

    public Avatar getAvatar(String player_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(AVATAR_TABLE_NAME, new String[]{AVATAR_COLUMN_PLAYER_ID, AVATAR_COLUMN_SHIRT_COLOR, AVATAR_COLUMN_SKIN_COLOR,
                        AVATAR_COLUMN_HAIR_TYPE, AVATAR_COLUMN_HAIR_COLOR}, AVATAR_COLUMN_PLAYER_ID + "=?",
                new String[]{player_id}, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
        }
        Avatar avatar = new Avatar(cursor.getInt(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4));
        cursor.close();
        db.close();
        return avatar;
    }


    public Cursor getInventoryData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(selectAllQuery(ITEM_TABLE_NAME) + " WHERE " + ITEM_COLUMN_ID + "=" + id, null);
    }

    public Cursor getWeaponsData(String uuid) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(selectAllQuery(WEAPON_TABLE_NAME) + " WHERE " + WEAPON_COLUMN_UUID + "='" + uuid + "'", null);
    }


    public boolean userExists() {
        SQLiteDatabase db = this.getReadableDatabase();
        long cnt = DatabaseUtils.queryNumEntries(db, USER_TABLE_NAME);
        db.close();
        return cnt > 0;
    }

    public Player getPlayer() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(selectAllQuery(PLAYERS_TABLE_NAME), null);
        res.moveToFirst();
        Player player = new Player();

        while (!res.isAfterLast()) {
            player = new Player(res.getString(res.getColumnIndex(PLAYERS_COLUMN_ID)),
                    res.getString(res.getColumnIndex(PLAYERS_COLUMN_NAME)),
                    Integer.parseInt(res.getString(res.getColumnIndex(PLAYERS_COLUMN_XP))),
                    Integer.parseInt(res.getString(res.getColumnIndex(PLAYERS_COLUMN_LEVEL))),
                    Integer.parseInt(res.getString(res.getColumnIndex(PLAYERS_COLUMN_MONEY))),
                    Integer.parseInt(res.getString(res.getColumnIndex(PLAYERS_COLUMN_MAX_HEALTH))),
                    Integer.parseInt(res.getString(res.getColumnIndex(PLAYERS_COLUMN_CURR_HEALTH))));
            res.moveToNext();
        }
        res.close();
        return player;
    }

    public InventoryItemArray getItems() {
        InventoryItemArray array_list = new InventoryItemArray();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(selectAllQuery(ITEM_TABLE_NAME), null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            array_list.put(Integer.parseInt(res.getString(res.getColumnIndex(ITEM_COLUMN_ID))), Integer.parseInt(res.getString(res.getColumnIndex(ITEM_COLUMN_QUANTITY))));
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
            WeaponItem weaponItem = WeaponFactory.buildWeapon(context, Integer.parseInt(res.getString(res.getColumnIndex(WEAPON_COLUMN_ID))));
            weaponItem.setCurrHealth(Integer.parseInt(res.getString(res.getColumnIndex(WEAPON_COLUMN_CURR_HEALTH))));
            weaponItem.setUUID(res.getString(res.getColumnIndex(WEAPON_COLUMN_UUID)));
            weaponItem.setEquipped(Integer.parseInt(res.getString(res.getColumnIndex(WEAPON_COLUMN_EQUIPPED))) == 1);
            array_list.add(weaponItem);
            res.moveToNext();
        }
        res.close();
        return array_list;
    }

    public ArrayList<ChestItem> getChests() {
        ArrayList<ChestItem> array_list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(selectAllQuery(CHEST_TABLE_NAME), null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            ChestItem chestItem = (ChestItem) ItemFactory.buildItem(context, Integer.parseInt(res.getString(res.getColumnIndex(CHEST_COLUMN_ID))));
            chestItem.setCurrDistance(Float.parseFloat(res.getString(res.getColumnIndex(CHEST_COLUMN_DISTANCE_LEFT))));
            chestItem.setUID(res.getString(res.getColumnIndex(CHEST_COLUMN_UUID)));
            array_list.add(chestItem);
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

    public Integer deleteItem(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(ITEM_TABLE_NAME,
                ITEM_COLUMN_ID + " = ? ",
                new String[]{Integer.toString(id)});
    }

    public Integer deleteWeapon(String UUID) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(WEAPON_TABLE_NAME,
                WEAPON_COLUMN_UUID + " = ? ",
                new String[]{UUID});
    }

    public Integer removeChest(String UUID) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(CHEST_TABLE_NAME,
                CHEST_COLUMN_UUID + " = ? ",
                new String[]{UUID});
    }

    public String getTableAsString(SQLiteDatabase db, String tableName) {
        Log.d("TAG", "getTableAsString called");
        String tableString = String.format("Table %s:\n", tableName);
        Cursor allRows = db.rawQuery(selectAllQuery(tableName), null);
        if (allRows.moveToFirst()) {
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String name : columnNames) {
                    tableString += String.format("%s: %s\n", name,
                            allRows.getString(allRows.getColumnIndex(name)));
                }
                tableString += "\n";

            } while (allRows.moveToNext());
        }
        return tableString;
    }

    public void printAllTables() {
        System.out.println("USER_TABLE");
        System.out.println(getTableAsString(this.getReadableDatabase(), USER_TABLE_NAME));

        System.out.println("PLAYER_TABLE");
        System.out.println(getTableAsString(this.getReadableDatabase(), PLAYERS_TABLE_NAME));

        System.out.println("AVATAR_TABLE");
        System.out.println(getTableAsString(this.getReadableDatabase(), AVATAR_TABLE_NAME));
    }
}
