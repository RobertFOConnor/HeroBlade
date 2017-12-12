package ie.ul.postgrad.socialanxietyapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ie.ul.postgrad.socialanxietyapp.Avatar;
import ie.ul.postgrad.socialanxietyapp.MoodEntry;
import ie.ul.postgrad.socialanxietyapp.game.ConsumedLocation;
import ie.ul.postgrad.socialanxietyapp.game.InventoryItemArray;
import ie.ul.postgrad.socialanxietyapp.game.Player;
import ie.ul.postgrad.socialanxietyapp.game.Stats;
import ie.ul.postgrad.socialanxietyapp.game.StepEntry;
import ie.ul.postgrad.socialanxietyapp.game.SurveyAnswer;
import ie.ul.postgrad.socialanxietyapp.game.factory.ItemFactory;
import ie.ul.postgrad.socialanxietyapp.game.factory.WeaponFactory;
import ie.ul.postgrad.socialanxietyapp.game.item.ChestItem;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponItem;

/**
 * Created by Robert on 14-Mar-17.
 * <p>
 * SQL database helper class for interaction with the database.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 68;

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
    private static final String PLAYERS_COLUMN_BASE_SWORD_ID = "sword_id";

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

    private static final String AVATAR_TABLE_NAME = "avatar";
    private static final String AVATAR_COLUMN_PLAYER_ID = "player_id";
    private static final String AVATAR_COLUMN_SKIN_COLOR = "skin_color";
    private static final String AVATAR_COLUMN_HAIR_TYPE = "hair_type";
    private static final String AVATAR_COLUMN_HAIR_COLOR = "hair_color";
    private static final String AVATAR_COLUMN_SHIRT_COLOR = "shirt_color";

    private static final String LOCATION_TABLE_NAME = "locations";
    private static final String LOCATION_COLUMN_PLAYER_ID = "player_id";
    private static final String LOCATION_COLUMN_LAT = "lat";
    private static final String LOCATION_COLUMN_LNG = "lng";
    private static final String LOCATION_COLUMN_TYPE = "type";
    private static final String LOCATION_COLUMN_LAST_TIME_VISITED = "timeofvisit";

    private static final String SURVEY_TABLE_NAME = "survey";
    private static final String SURVEY_COLUMN_PLAYER_ID = "player_id";
    private static final String SURVEY_COLUMN_QUESTION = "question";
    private static final String SURVEY_COLUMN_ANSWER = "answer";
    private static final String SURVEY_COLUMN_DATE = "date";

    private static final String STATS_TABLE_NAME = "player_stats";
    private static final String STATS_COLUMN_PLAYER_ID = "player_id";
    private static final String STATS_COLUMN_WINS = "win_count";
    private static final String STATS_COLUMN_CHESTS_OPENED = "chests_opened";
    private static final String STATS_TOTAL_STEPS = "total_steps";

    private static final String MOOD_TABLE_NAME = "mood";
    private static final String MOOD_COLUMN_PLAYER_ID = "player_id";
    private static final String MOOD_RATING = "rating";
    private static final String MOOD_DESCRIPTION = "description";
    private static final String MOOD_DATE_TIME = "date_time";

    private static final String[] TABLES = {USER_TABLE_NAME, PLAYERS_TABLE_NAME, ITEM_TABLE_NAME, WEAPON_TABLE_NAME, CHEST_TABLE_NAME, TRAVEL_TABLE_NAME, AVATAR_TABLE_NAME, LOCATION_TABLE_NAME, SURVEY_TABLE_NAME, STATS_TABLE_NAME, MOOD_TABLE_NAME};

    private Context context;
    private SQLiteDatabase db;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + USER_TABLE_NAME + " (id text primary key, name text, email text, password text)");
        db.execSQL("CREATE TABLE " + PLAYERS_TABLE_NAME + " (id text primary key, name text, xp integer, level integer, money integer, max_health integer, curr_health integer, villages_found integer, win_count integer, sword_id text)");
        db.execSQL("CREATE TABLE " + ITEM_TABLE_NAME + " (player_id text, item_id integer, quantity integer)");
        db.execSQL("CREATE TABLE " + WEAPON_TABLE_NAME + " (player_id text, weapon_uuid text, weapon_id integer, curr_health integer, equipped integer)");
        db.execSQL("CREATE TABLE " + CHEST_TABLE_NAME + " (player_id text, chest_uuid text, chest_id integer, distance_left real)");
        db.execSQL("CREATE TABLE " + TRAVEL_TABLE_NAME + " (player_id text, creation_date text, step_count integer, distance integer)");
        db.execSQL("CREATE TABLE " + AVATAR_TABLE_NAME + " (player_id text, shirt_color integer, skin_color integer, hair_type integer, hair_color integer)");
        db.execSQL("CREATE TABLE " + LOCATION_TABLE_NAME + " (player_id text, lat real, lng real, type integer, timeofvisit long)");
        db.execSQL("CREATE TABLE " + SURVEY_TABLE_NAME + " (player_id text, question integer, answer integer, date text)");
        db.execSQL("CREATE TABLE " + STATS_TABLE_NAME + " (player_id text, win_count integer, chests_opened integer, total_steps integer)");
        db.execSQL("CREATE TABLE " + MOOD_TABLE_NAME + " (player_id text, rating integer, description text, date_time text)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (String table : TABLES) {
            db.execSQL("DROP TABLE IF EXISTS " + table);
        }
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void insertUser(String id, String name, String email, String password) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_COLUMN_ID, id);
        contentValues.put(USER_COLUMN_NAME, name);
        contentValues.put(USER_COLUMN_EMAIL, email);
        contentValues.put(USER_COLUMN_PASSWORD, password);
        db.insert(USER_TABLE_NAME, null, contentValues);

    }

    public void insertPlayer(Player player) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PLAYERS_COLUMN_ID, player.getId());
        contentValues.put(PLAYERS_COLUMN_NAME, player.getName());
        contentValues.put(PLAYERS_COLUMN_XP, player.getXp());
        contentValues.put(PLAYERS_COLUMN_LEVEL, player.getLevel());
        contentValues.put(PLAYERS_COLUMN_MONEY, player.getMoney());
        contentValues.put(PLAYERS_COLUMN_MAX_HEALTH, player.getMaxHealth());
        contentValues.put(PLAYERS_COLUMN_CURR_HEALTH, player.getCurrHealth());
        contentValues.put(PLAYERS_COLUMN_BASE_SWORD_ID, player.getBaseSword());
        db.insert(PLAYERS_TABLE_NAME, null, contentValues);

    }

    public void insertAvatar(Avatar avatar) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(AVATAR_COLUMN_PLAYER_ID, getPlayer().getId());
        contentValues.put(AVATAR_COLUMN_SHIRT_COLOR, avatar.getShirtColor());
        contentValues.put(AVATAR_COLUMN_SKIN_COLOR, avatar.getSkinColor());
        contentValues.put(AVATAR_COLUMN_HAIR_TYPE, avatar.getHairtype());
        contentValues.put(AVATAR_COLUMN_HAIR_COLOR, avatar.getHairColor());
        db.insert(AVATAR_TABLE_NAME, null, contentValues);

    }

    public void insertItem(String player_id, int item_id, int quantity) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ITEM_COLUMN_PLAYER_ID, player_id);
        contentValues.put(ITEM_COLUMN_ID, item_id);
        contentValues.put(ITEM_COLUMN_QUANTITY, quantity);
        db.insert(ITEM_TABLE_NAME, null, contentValues);

    }

    public void insertWeapon(String player_id, WeaponItem weapon) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(WEAPON_COLUMN_PLAYER_ID, player_id);
        contentValues.put(WEAPON_COLUMN_UUID, weapon.getUUID());
        contentValues.put(WEAPON_COLUMN_ID, weapon.getId());
        contentValues.put(WEAPON_COLUMN_CURR_HEALTH, weapon.getCurrHealth());
        if (weapon.isEquipped()) {
            contentValues.put(WEAPON_COLUMN_EQUIPPED, 1);
        } else {
            contentValues.put(WEAPON_COLUMN_EQUIPPED, 0);
        }
        db.insert(WEAPON_TABLE_NAME, null, contentValues);

    }

    public void insertChest(String player_id, ChestItem chest) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CHEST_COLUMN_PLAYER_ID, player_id);
        contentValues.put(CHEST_COLUMN_UUID, chest.getUID());
        contentValues.put(CHEST_COLUMN_ID, chest.getId());
        contentValues.put(CHEST_COLUMN_DISTANCE_LEFT, chest.getCurrDistance());
        db.insert(CHEST_TABLE_NAME, null, contentValues);

    }

    public void insertLocation(String player_id, double lat, double lng, int type, long visitTime) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LOCATION_COLUMN_PLAYER_ID, player_id);
        contentValues.put(LOCATION_COLUMN_LAT, lat);
        contentValues.put(LOCATION_COLUMN_LNG, lng);
        contentValues.put(LOCATION_COLUMN_TYPE, type);
        contentValues.put(LOCATION_COLUMN_LAST_TIME_VISITED, visitTime);
        db.insert(LOCATION_TABLE_NAME, null, contentValues);

    }

    public void insertSurveyAnswer(String player_id, int question, int answer) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SURVEY_COLUMN_PLAYER_ID, player_id);
        contentValues.put(SURVEY_COLUMN_QUESTION, question);
        contentValues.put(SURVEY_COLUMN_ANSWER, answer);
        contentValues.put(SURVEY_COLUMN_DATE, DateFormat.getDateTimeInstance().format(new Date()));
        db.insert(SURVEY_TABLE_NAME, null, contentValues);

    }

    public void insertMoodRating(String player_id, int rating, String description) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MOOD_COLUMN_PLAYER_ID, player_id);
        contentValues.put(MOOD_RATING, rating);
        contentValues.put(MOOD_DESCRIPTION, description);
        contentValues.put(MOOD_DATE_TIME, DateFormat.getDateTimeInstance().format(new Date()));
        db.insert(MOOD_TABLE_NAME, null, contentValues);

    }

    public void insertStats(String id) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(STATS_COLUMN_PLAYER_ID, id);
        contentValues.put(STATS_COLUMN_CHESTS_OPENED, 0);
        contentValues.put(STATS_COLUMN_WINS, 0);
        contentValues.put(STATS_TOTAL_STEPS, 0);
        db.insert(STATS_TABLE_NAME, null, contentValues);

    }


    public void updatePlayer(Player player) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PLAYERS_COLUMN_NAME, player.getName());
        contentValues.put(PLAYERS_COLUMN_XP, player.getXp());
        contentValues.put(PLAYERS_COLUMN_LEVEL, player.getLevel());
        contentValues.put(PLAYERS_COLUMN_MONEY, player.getMoney());
        contentValues.put(PLAYERS_COLUMN_MAX_HEALTH, player.getMaxHealth());
        contentValues.put(PLAYERS_COLUMN_CURR_HEALTH, player.getCurrHealth());
        db.update(PLAYERS_TABLE_NAME, contentValues, PLAYERS_COLUMN_ID + " = ? ", new String[]{player.getId()});

    }

    public void updateAvatar(Avatar avatar) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(AVATAR_COLUMN_SHIRT_COLOR, avatar.getShirtColor());
        contentValues.put(AVATAR_COLUMN_SKIN_COLOR, avatar.getSkinColor());
        contentValues.put(AVATAR_COLUMN_HAIR_TYPE, avatar.getHairtype());
        contentValues.put(AVATAR_COLUMN_HAIR_COLOR, avatar.getHairColor());
        db.update(AVATAR_TABLE_NAME, contentValues, AVATAR_COLUMN_PLAYER_ID + " = ? ", new String[]{getPlayer().getId()});

    }

    public void updateItem(String player_id, int item_id, int quantity) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ITEM_COLUMN_PLAYER_ID, player_id);
        contentValues.put(ITEM_COLUMN_ID, item_id);
        contentValues.put(ITEM_COLUMN_QUANTITY, quantity);
        db.update(ITEM_TABLE_NAME, contentValues, ITEM_COLUMN_ID + " = ? ", new String[]{Integer.toString(item_id)});

    }

    public void updateWeapon(String player_id, WeaponItem weapon) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(WEAPON_COLUMN_PLAYER_ID, player_id);
        contentValues.put(WEAPON_COLUMN_UUID, weapon.getUUID());
        contentValues.put(WEAPON_COLUMN_ID, weapon.getId());
        contentValues.put(WEAPON_COLUMN_CURR_HEALTH, weapon.getCurrHealth());
        if (weapon.isEquipped()) {
            contentValues.put(WEAPON_COLUMN_EQUIPPED, 1);
        } else {
            contentValues.put(WEAPON_COLUMN_EQUIPPED, 0);
        }
        db.update(WEAPON_TABLE_NAME, contentValues, WEAPON_COLUMN_UUID + " = ? ", new String[]{weapon.getUUID()});
    }

    public void updateChest(String player_id, ChestItem chest) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CHEST_COLUMN_PLAYER_ID, player_id);
        contentValues.put(CHEST_COLUMN_UUID, chest.getUID());
        contentValues.put(CHEST_COLUMN_ID, chest.getId());
        contentValues.put(CHEST_COLUMN_DISTANCE_LEFT, chest.getCurrDistance());
        db.update(CHEST_TABLE_NAME, contentValues, CHEST_COLUMN_UUID + " = ? ", new String[]{chest.getUID()});
    }

    public void updateLocation(String player_id, double lat, double lng, int type, long time) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LOCATION_COLUMN_PLAYER_ID, player_id);
        contentValues.put(LOCATION_COLUMN_LAT, lat);
        contentValues.put(LOCATION_COLUMN_LNG, lng);
        contentValues.put(LOCATION_COLUMN_TYPE, type);
        contentValues.put(LOCATION_COLUMN_LAST_TIME_VISITED, time);
        db.update(LOCATION_TABLE_NAME, contentValues, "lat=? and lng=?", new String[]{String.valueOf(lat), String.valueOf(lng)});
    }

    public void updateStats(String player_id, Stats stats) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(STATS_COLUMN_PLAYER_ID, player_id);
        contentValues.put(STATS_COLUMN_WINS, stats.getWins());
        contentValues.put(STATS_COLUMN_CHESTS_OPENED, stats.getChestsOpened());
        contentValues.put(STATS_TOTAL_STEPS, stats.getTotalSteps());
    }

    public Avatar getAvatar(String player_id) {
        db = this.getReadableDatabase();
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

    public ArrayList<SurveyAnswer> getSurveyAnswers() {
        ArrayList<SurveyAnswer> answers = new ArrayList<>();
        db = this.getReadableDatabase();
        Cursor res = getSurveyData(getPlayer().getId());
        res.moveToFirst();

        while (!res.isAfterLast()) {
            answers.add(new SurveyAnswer(res.getInt(res.getColumnIndex(SURVEY_COLUMN_QUESTION)), res.getInt(res.getColumnIndex(SURVEY_COLUMN_ANSWER)), res.getString(res.getColumnIndex(SURVEY_COLUMN_DATE))));
            res.moveToNext();
        }
        res.close();
        return answers;
    }

    public ArrayList<MoodEntry> getMoodEntries() {
        ArrayList<MoodEntry> entries = new ArrayList<>();
        db = this.getReadableDatabase();
        Cursor res = getMoodData(getPlayer().getId());
        res.moveToFirst();

        while (!res.isAfterLast()) {
            entries.add(new MoodEntry(res.getInt(res.getColumnIndex(MOOD_RATING)), res.getString(res.getColumnIndex(MOOD_DESCRIPTION)), res.getString(res.getColumnIndex(MOOD_DATE_TIME))));
            res.moveToNext();
        }
        res.close();
        return entries;
    }

    public Cursor getSurveyData(String player_id) {
        return getTableData(SURVEY_TABLE_NAME, SURVEY_COLUMN_PLAYER_ID, player_id);
    }

    private Cursor getMoodData(String player_id) {
        return getTableData(MOOD_TABLE_NAME, MOOD_COLUMN_PLAYER_ID, player_id);
    }

    public Cursor getInventoryData(int id) {
        return getTableData(ITEM_TABLE_NAME, ITEM_COLUMN_ID, String.valueOf(id));
    }

    public Cursor getWeaponsData(String uuid) {
        return getTableData(WEAPON_TABLE_NAME, WEAPON_COLUMN_UUID, uuid);
    }

    private Cursor getTableData(String tableName, String idCol, String id) {
        try {
            db = this.getReadableDatabase();
            return db.rawQuery(selectAllQuery(tableName) + " WHERE " + idCol + "='" + id + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Cursor getLocationData(double lat, double lng) {
        db = this.getReadableDatabase();
        return db.rawQuery(selectAllQuery(LOCATION_TABLE_NAME) + " WHERE " + LOCATION_COLUMN_LAT + "='" + String.valueOf(lat) + "'" + " AND " + LOCATION_COLUMN_LNG + "='" + String.valueOf(lng) + "'", null);
    }

    public ConsumedLocation getLocation(double lat, double lng) {
        Cursor res = getLocationData(lat, lng);
        if (res.moveToFirst()) {
            return new ConsumedLocation(Double.parseDouble(res.getString(res.getColumnIndex(LOCATION_COLUMN_LAT))), Double.parseDouble(res.getString(res.getColumnIndex(LOCATION_COLUMN_LNG))), Integer.parseInt(res.getString(res.getColumnIndex(LOCATION_COLUMN_TYPE))), Long.parseLong(res.getString(res.getColumnIndex(LOCATION_COLUMN_LAST_TIME_VISITED))));
        }
        return null;
    }


    public boolean userExists(String id) {
        boolean exists = false;
        db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectAllQuery(USER_TABLE_NAME) + " WHERE " + USER_COLUMN_ID + "='" + id + "'", null);

        if (c.moveToFirst()) {
            exists = true;
        }
        c.close();
        return exists;
    }

    public Player getPlayer() {
        db = this.getReadableDatabase();
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
                    Integer.parseInt(res.getString(res.getColumnIndex(PLAYERS_COLUMN_CURR_HEALTH))),
                    res.getString(res.getColumnIndex(PLAYERS_COLUMN_BASE_SWORD_ID)));
            res.moveToNext();
        }
        res.close();
        return player;
    }

    public InventoryItemArray getItems() {
        InventoryItemArray array_list = new InventoryItemArray();

        db = this.getReadableDatabase();
        Cursor res = db.rawQuery(selectAllQuery(ITEM_TABLE_NAME), null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            array_list.put(Integer.parseInt(res.getString(res.getColumnIndex(ITEM_COLUMN_ID))), Integer.parseInt(res.getString(res.getColumnIndex(ITEM_COLUMN_QUANTITY))));
            res.moveToNext();
        }
        res.close();
        return array_list;
    }

    public ArrayList<ConsumedLocation> getLocations() {
        ArrayList<ConsumedLocation> locations = new ArrayList<>();
        db = this.getReadableDatabase();
        Cursor res = db.rawQuery(selectAllQuery(LOCATION_TABLE_NAME), null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            locations.add(new ConsumedLocation(Double.parseDouble(res.getString(res.getColumnIndex(LOCATION_COLUMN_LAT))), Double.parseDouble(res.getString(res.getColumnIndex(LOCATION_COLUMN_LNG))), Integer.parseInt(res.getString(res.getColumnIndex(LOCATION_COLUMN_TYPE))), Long.parseLong(res.getString(res.getColumnIndex(LOCATION_COLUMN_LAST_TIME_VISITED)))));
            res.moveToNext();
        }
        res.close();
        return locations;
    }


    public ArrayList<WeaponItem> getWeapons() {
        ArrayList<WeaponItem> array_list = new ArrayList<>();

        db = this.getReadableDatabase();
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

        db = this.getReadableDatabase();
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

    public Stats getStats() {
        db = this.getReadableDatabase();
        Cursor res = db.rawQuery(selectAllQuery(STATS_TABLE_NAME), null);
        res.moveToFirst();
        Stats stats = new Stats();

        while (!res.isAfterLast()) {
            stats = new Stats(
                    Integer.parseInt(res.getString(res.getColumnIndex(STATS_COLUMN_WINS))),
                    Integer.parseInt(res.getString(res.getColumnIndex(STATS_COLUMN_CHESTS_OPENED))),
                    Integer.parseInt(res.getString(res.getColumnIndex(STATS_TOTAL_STEPS))));
            res.moveToNext();
        }
        res.close();
        return stats;
    }

    public boolean insertStepsEntry(float totalDistance, int steps) {

        boolean isDateAlreadyPresent = false;
        boolean createSuccessful = false;
        int currentDateStepCounts = 0;
        String todayDate = getTodaysDate();
        String selectQuery = "SELECT " + TRAVEL_COLUMN_STEPS_COUNT + " FROM " + TRAVEL_TABLE_NAME + " WHERE " + TRAVEL_COLUMN_CREATION_DATE + " = '" + todayDate + "'";
        try {
            db = this.getReadableDatabase();
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
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(TRAVEL_COLUMN_PLAYER_ID, 1);
            values.put(TRAVEL_COLUMN_CREATION_DATE, todayDate);
            values.put(TRAVEL_COLUMN_DISTANCE, totalDistance);
            if (isDateAlreadyPresent) {
                values.put(TRAVEL_COLUMN_STEPS_COUNT, steps + currentDateStepCounts);
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
            System.out.println("STEPS: " + (steps + currentDateStepCounts));
            System.out.println("DATE: " + todayDate);
            System.out.println("DIST: " + totalDistance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return createSuccessful;
    }

    private String getTodaysDate() {
        Calendar mCalendar = Calendar.getInstance();
        return String.valueOf(mCalendar.get(Calendar.MONTH)) + "/" + String.valueOf(mCalendar.get(Calendar.DAY_OF_MONTH)) + "/" + String.valueOf(mCalendar.get(Calendar.YEAR));
    }

    public int getSteps() {
        int steps = 0;
        String selectQuery = selectAllQuery(TRAVEL_TABLE_NAME) + " WHERE " + TRAVEL_COLUMN_PLAYER_ID + "=" + 1;
        try {
            db = this.getReadableDatabase();
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

    public ArrayList<StepEntry> getDailySteps() {

        ArrayList<StepEntry> steps = new ArrayList<>();
        String selectQuery = selectAllQuery(TRAVEL_TABLE_NAME) + " WHERE " + TRAVEL_COLUMN_PLAYER_ID + "=" + 1;
        try {
            db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                steps.add(new StepEntry(c.getInt((c.getColumnIndex(TRAVEL_COLUMN_STEPS_COUNT))), c.getString(c.getColumnIndex(TRAVEL_COLUMN_CREATION_DATE))));
            }
            while (c.moveToNext()) {
                steps.add(new StepEntry(c.getInt((c.getColumnIndex(TRAVEL_COLUMN_STEPS_COUNT))), c.getString(c.getColumnIndex(TRAVEL_COLUMN_CREATION_DATE))));
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
            db = this.getReadableDatabase();
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

    public void deleteItem(Integer id) {
        db = this.getWritableDatabase();
        db.delete(ITEM_TABLE_NAME,
                ITEM_COLUMN_ID + " = ? ",
                new String[]{Integer.toString(id)});
    }

    public void deleteWeapon(String UUID) {
        db = this.getWritableDatabase();
        db.delete(WEAPON_TABLE_NAME,
                WEAPON_COLUMN_UUID + " = ? ",
                new String[]{UUID});
    }

    public void removeChest(String UUID) {
        db = this.getWritableDatabase();
        db.delete(CHEST_TABLE_NAME,
                CHEST_COLUMN_UUID + " = ? ",
                new String[]{UUID});
    }

    private String getTableAsString(SQLiteDatabase db, String tableName) {
        StringBuilder tableString = new StringBuilder(String.format("Table %s:\n", tableName));
        Cursor allRows = db.rawQuery(selectAllQuery(tableName), null);
        if (allRows.moveToFirst()) {
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String name : columnNames) {
                    tableString.append(String.format("%s: %s\n", name,
                            allRows.getString(allRows.getColumnIndex(name))));
                }
                tableString.append("\n");

            } while (allRows.moveToNext());
        }
        allRows.close();
        return tableString.toString();
    }

    public void printAllTables() {
        for (String table : TABLES) {
            System.out.println(table);
            System.out.println(getTableAsString(this.getReadableDatabase(), table));
            System.out.println();
        }
    }

    public void clearTables() {
        db = this.getWritableDatabase();
        for (String table : TABLES) {
            db.execSQL("delete from " + table);
        }
    }
}
