package Database;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import au.com.bytecode.opencsv.CSVReader;
import stickers.Sticker;
import util.Util;


public class Database extends SQLiteOpenHelper {

    private String DB_PATH = context.getApplicationContext().getPackageName()+"/databases/";
    private final static String DB_NAME = "steps";
    private final static String  TABLE_STICKERS = "stickers";
    private final static  String TABLE_STICKER_COUNT = "stickercount";
    private final static int DB_VERSION = 2;
    private static Database instance;
    private static final AtomicInteger openCounter = new AtomicInteger();

    // Sticker Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_MOVIE = "movie";
    private static final String KEY_IMAGESRC= "img";
    private static final String KEY_POPULARITY = "popularity";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_COUNT = "count";
    private static final String KEY_STATUS = "status";
    private static final String[] COLUMNS = {KEY_ID,KEY_NAME,KEY_MOVIE,KEY_IMAGESRC,KEY_POPULARITY,KEY_DESCRIPTION,KEY_COUNT,KEY_STATUS};
    private static Context context;


    private Database(final Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        //check if this is the first time we run the app
       createdatabase();

    }

    public static synchronized Database getInstance(final Context c) {
        if (instance == null) {
            context = c;
            instance = new Database(c.getApplicationContext());
        }
        openCounter.incrementAndGet();
        return instance;
    }

    @Override
    public void close() {
        if (openCounter.decrementAndGet() == 0) {
            super.close();
        }
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DB_NAME + " (date INTEGER, steps INTEGER)");
        db.execSQL("CREATE TABLE " + TABLE_STICKER_COUNT + " (date INTEGER, stickers INTEGER)");
        db.execSQL("CREATE TABLE "+ TABLE_STICKERS+ "(id INTEGER, name TEXT, movie TEXT, img TEXT, "+
                "popularity TEXT, description TEXT, count INTEGER, status INTEGER  )");
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1) {
            // drop PRIMARY KEY constraint
            db.execSQL("CREATE TABLE " + DB_NAME + "2 (date INTEGER, steps INTEGER)");
            db.execSQL("INSERT INTO " + DB_NAME + "2 (date, steps) SELECT date, steps FROM " +
                    DB_NAME);
            db.execSQL("DROP TABLE " + DB_NAME);
            db.execSQL("ALTER TABLE " + DB_NAME + "2 RENAME TO " + DB_NAME + "");

            // Drop older stickers table if existed
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_STICKERS);

            // create fresh stickers table
            db.execSQL("CREATE TABLE "+ TABLE_STICKERS + "(id INTEGER PRIMARY KEY, name TEXT, movie TEXT, img TEXT, "+
                    "popularity TEXT, description TEXT, count INTEGER, status INTEGER  )");

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_STICKER_COUNT);
            db.execSQL("CREATE TABLE " + TABLE_STICKER_COUNT + " (date INTEGER, stickers INTEGER)");

        }
    }


    public void populateStickers()  {
        //get reference to the asset manager
       AssetManager assetManager = context.getAssets();
        InputStream is = null;
        //try to find the csv with all the stickers information
        try {
            is = assetManager.open("stickers.csv");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.d("hEREE","Exception");
            e.printStackTrace();
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        CSVReader csvReader = new CSVReader(reader);
        //iterate through the file
        try {
            int iteration = 0;
            String [] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {


                //skip fist line
                if(iteration == 0) {iteration++;continue;}

                Sticker sticker = new Sticker();
                sticker.setId(Integer.parseInt(nextLine[0]));
                sticker.setName(nextLine[1]);
                sticker.setMovie(nextLine[2]);
                sticker.setPopularity(nextLine[3]);
                sticker.setImagesrc(nextLine[4]);
                sticker.setDescription(nextLine[5]);
                sticker.setCount(0);
                sticker.setStatus(0);
                //add each sticker in the database
                addSticker(sticker);
            }
        } catch (IOException e) {

            e.printStackTrace();
        }


    }

    public void addSticker(Sticker sticker){
        //for logging
        Log.d("addSticker", sticker.toString());

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_ID, sticker.getId()); // get id
        values.put(KEY_NAME, sticker.getName()); // get name etc, etc
        values.put(KEY_MOVIE, sticker.getMovie());
        values.put(KEY_POPULARITY,sticker.getPopularity());
        values.put(KEY_IMAGESRC, sticker.getImagesrc());
        values.put(KEY_DESCRIPTION, sticker.getDescription());
        values.put(KEY_COUNT,sticker.getCount());
        values.put(KEY_STATUS, sticker.getStatus());


        // 3. insert
        db.insert(TABLE_STICKERS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public void createdatabase(){
        //check if the database exists
        boolean dbexist = checkdatabase();
        //if it doesnt exist populate it
        if(!dbexist)
            populateStickers();
    }


    private boolean checkdatabase() {
        boolean checkdb = false;

        try{
            String myPath = DB_PATH + DB_NAME;
            Log.d("calcualte Path", myPath);
            File db = context.getDatabasePath("steps");
            checkdb = db.exists();
        }
        catch(Exception e){
            Log.d("Check","Database doesn't exist");
        }
        return checkdb;
    }

    public  List<Sticker> getStickersWithCountGreatherOrEqualTo(int count){
        List<Sticker> stickers = new LinkedList<Sticker>();


        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor =
                db.query(TABLE_STICKERS, // a. table
                        COLUMNS, // b. column names
                        " count >= ?", // c. selections
                        new String[]{String.valueOf(count)}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit




        // 3. go over each row, build book and add it to list
        Sticker sticker = null;
        if (cursor.moveToFirst()) {
            do {
                sticker = new Sticker();
                sticker.setId(Integer.parseInt(cursor.getString(0)));
                sticker.setName(cursor.getString(1));
                sticker.setMovie(cursor.getString(2));
                sticker.setImagesrc(cursor.getString(3));
                sticker.setPopularity(cursor.getString(4));
                sticker.setDescription(cursor.getString(5));
                sticker.setCount(Integer.parseInt(cursor.getString(6)));
                sticker.setStatus(Integer.parseInt(cursor.getString(7)));

                // Add stickers
                stickers.add(sticker);
            } while (cursor.moveToNext());
        }

       // Log.d("getStickersWithCountGreatherOrEqualTo()", stickers.toString());

        // return stickers
        return stickers;
    }


    public Sticker getSticker(int id){
        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_STICKERS, // a. table
                        COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build sticker object
        Sticker sticker = new Sticker();
        sticker = new Sticker();
        sticker.setId(Integer.parseInt(cursor.getString(0)));
        sticker.setName(cursor.getString(1));
        sticker.setMovie(cursor.getString(2));
        sticker.setPopularity(cursor.getString(4));
        sticker.setImagesrc(cursor.getString(3));
        sticker.setDescription(cursor.getString(5));
        sticker.setCount(Integer.parseInt(cursor.getString(6)));
        sticker.setStatus(Integer.parseInt(cursor.getString(7)));




        //log
        Log.d("getSticker(" + id + ")", sticker.toString());

        // 5. return book
        return sticker;
    }


    public Sticker getStickerForName(String name){
        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_STICKERS, // a. table
                        COLUMNS, // b. column names
                        " name = ?", // c. selections
                        new String[] { name }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build sticker object
        Sticker sticker = new Sticker();
        sticker = new Sticker();
        sticker.setId(Integer.parseInt(cursor.getString(0)));
        sticker.setName(cursor.getString(1));
        sticker.setMovie(cursor.getString(2));
        sticker.setPopularity(cursor.getString(4));
        sticker.setImagesrc(cursor.getString(3));
        sticker.setDescription(cursor.getString(5));
        sticker.setCount(Integer.parseInt(cursor.getString(6)));
        sticker.setStatus(Integer.parseInt(cursor.getString(7)));




        //log
        Log.d("getStickerForName(" + name + ")", sticker.toString());

        // 5. return book
        return sticker;
    }

    public List<Sticker> getAllStickers() {
        List<Sticker> stickers = new LinkedList<Sticker>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_STICKERS;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        Sticker sticker = null;
        if (cursor.moveToFirst()) {
            do {
                sticker = new Sticker();
                sticker.setId(Integer.parseInt(cursor.getString(0)));
                sticker.setName(cursor.getString(1));
                sticker.setMovie(cursor.getString(2));
                sticker.setImagesrc(cursor.getString(3));
                sticker.setPopularity(cursor.getString(4));
                sticker.setDescription(cursor.getString(5));
                sticker.setCount(Integer.parseInt(cursor.getString(6)));
                sticker.setStatus(Integer.parseInt(cursor.getString(7)));

                // Add stickers
                stickers.add(sticker);
            } while (cursor.moveToNext());
        }

        Log.d("getAllBooks()", stickers.toString());

        // return stickers
        return stickers;
    }







    /**
     * Query the 'steps' table. Remember to close the cursor!
     *
     * @param columns       the colums
     * @param selection     the selection
     * @param selectionArgs the selction arguments
     * @param groupBy       the group by statement
     * @param having        the having statement
     * @param orderBy       the order by statement
     * @return the cursor
     */
    public Cursor query(final String[] columns, final String selection, final String[] selectionArgs, final String groupBy, final String having, final String orderBy, final String limit) {
        return getReadableDatabase()
                .query(DB_NAME, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    /**
     * Inserts a new entry in the database, if there is no entry for the given
     * date yet. Steps should be the current number of steps and it's negative
     * value will be used as offset for the new date. Also adds 'steps' steps to
     * the previous day, if there is an entry for that date.
     * <p/>
     * This method does nothing if there is already an entry for 'date' - use
     * {@link #updateSteps} in this case.
     * <p/>
     *
     *
     * @param date  the date in ms since 1970
     * @param steps the current step value to be used as negative offset for the
     *              new day; must be >= 0
     */
    public void insertNewDay(long date, int steps) {
        getWritableDatabase().beginTransaction();
        try {
            Cursor c = getReadableDatabase().query(DB_NAME, new String[]{"date"}, "date = ?",
                    new String[]{String.valueOf(date)}, null, null, null);
            if (c.getCount() == 0 && steps >= 0) {
                ContentValues values = new ContentValues();
                values.put("date", date);
                // use the negative steps as offset
                values.put("steps", -steps);
                getWritableDatabase().insert(DB_NAME, null, values);

                // add 'steps' to yesterdays count
                date -= 24 * 60 * 60 * 1000;
                updateSteps(date, steps);
            }
            c.close();

            getWritableDatabase().setTransactionSuccessful();
        } finally {
            getWritableDatabase().endTransaction();
        }
    }

    public void insertNewDayStickers(long date, int count) {
        getWritableDatabase().beginTransaction();
        try {
            Cursor c = getReadableDatabase().query(TABLE_STICKER_COUNT, new String[]{"date"}, "date = ?",
                    new String[]{String.valueOf(date)}, null, null, null);
            if (c.getCount() == 0 ) {
                ContentValues values = new ContentValues();
                values.put("date", date);
                // use the negative steps as offset
                values.put("stickers", count);
                getWritableDatabase().insert(TABLE_STICKER_COUNT
                        , null, values);
            }
            c.close();

            getWritableDatabase().setTransactionSuccessful();
        } finally {
            getWritableDatabase().endTransaction();
        }
    }





    /**
     * Adds 'steps' steps to the row for the date 'date'. Won't do anything if
     * there isn't a row for the given date
     *
     * @param date  the date to update the steps for in millis since 1970
     * @param steps the steps to add to the current steps-value for the date
     */
    public void updateSteps(final long date, int steps) {
        getWritableDatabase().execSQL(
                "UPDATE " + DB_NAME + " SET steps = steps + " + steps + " WHERE date = " + date);
    }

    public void updateStickerCount(final long date, int stickers) {
        getWritableDatabase().execSQL(
                "UPDATE " + TABLE_STICKER_COUNT + " SET stickers = stickers + " + stickers + " WHERE date = " + date);
    }

    public void updateStatus(Integer id, int status) {
        getWritableDatabase().execSQL(
                "UPDATE " + TABLE_STICKERS + " SET status = " + status + " WHERE id = " + id);
    }

    public void updateCount(Integer id, String action) {
        if(action.equals("increase")){
            Log.w("Incrase", "increase");
            getWritableDatabase().execSQL(
                    "UPDATE " + TABLE_STICKERS + " SET count = count + " + 1 + " WHERE id = " + id);

        }else  if (action.equals("decrease")){
            Log.w("Decrease","Decrease");
            getWritableDatabase().execSQL(
                    "UPDATE " + TABLE_STICKERS + " SET count = count - " + 1 + " WHERE id = " + id);
        }
    }
    /**
     * Get the total of steps taken without today's value
     *
     * @return number of steps taken, ignoring today
     */
    public int getTotalWithoutToday() {
        Cursor c = getReadableDatabase()
                .query(DB_NAME, new String[]{"SUM(steps)"}, "steps > 0 AND date > 0 AND date < ?",
                        new String[]{String.valueOf(Util.getToday())}, null, null, null);
        c.moveToFirst();
        int re = c.getInt(0);
        c.close();
        return re;
    }

    /**
     * Get the maximum of steps walked in one day
     *
     * @return the maximum number of steps walked in one day
     */
    public int getRecord() {
        Cursor c = getReadableDatabase()
                .query(DB_NAME, new String[]{"MAX(steps)"}, "date > 0", null, null, null, null);
        c.moveToFirst();
        int re = c.getInt(0);
        c.close();
        return re;
    }

    /**
     * Get the maximum of steps walked in one day and the date that happend
     *
     * @return a pair containing the date (Date) in millis since 1970 and the
     * step value (Integer)
     */
    public Pair<Date, Integer> getRecordData() {
        Cursor c = getReadableDatabase()
                .query(DB_NAME, new String[]{"date, steps"}, "date > 0", null, null, null,
                        "steps DESC", "1");
        c.moveToFirst();
        Pair<Date, Integer> p = new Pair<Date, Integer>(new Date(c.getLong(0)), c.getInt(1));
        c.close();
        return p;
    }

    /**
     * Get the number of steps taken for a specific date.
     * <p/>
     * If date is Util.getToday(), this method returns the offset which needs to
     * be added to the value returned by getCurrentSteps() to get todays steps.
     *
     * @param date the date in millis since 1970
     * @return the steps taken on this date or Integer.MIN_VALUE if date doesn't
     * exist in the database
     */
    public int getSteps(final long date) {
        Cursor c = getReadableDatabase().query(DB_NAME, new String[]{"steps"}, "date = ?",
                new String[]{String.valueOf(date)}, null, null, null);
        c.moveToFirst();
        int re;
        if (c.getCount() == 0) re = Integer.MIN_VALUE;
        else re = c.getInt(0);
        c.close();
        return re;
    }

    /**
     * Get the number of stickers won for the specified date
     * <p/>
     * @param date the date in millis since 1970
     * @return the stickers won on this date or Integer.MIN_VALUE if date doesn't
     * exist in the database
     */
    public int getStickerCount(final long date) {
        Cursor c = getReadableDatabase().query(TABLE_STICKER_COUNT, new String[]{"stickers"}, "date = ?",
                new String[]{String.valueOf(date)}, null, null, null);
        c.moveToFirst();
        int re;
        if (c.getCount() == 0) re = Integer.MIN_VALUE;
        else re = c.getInt(0);
        c.close();
        return re;
    }

    /**
     * Get the number of steps taken between 'start' and 'end' date
     * <p/>
     * Note that todays entry might have a negative value, so take care of that
     * if 'end' >= Util.getToday()!
     *
     * @param start start date in ms since 1970 (steps for this date included)
     * @param end   end date in ms since 1970 (steps for this date included)
     * @return the number of steps from 'start' to 'end'. Can be < 0 as todays
     * entry might have negative value
     */
    public int getSteps(final long start, final long end) {
        Cursor c = getReadableDatabase()
                .query(DB_NAME, new String[]{"SUM(steps)"}, "date >= ? AND date <= ?",
                        new String[]{String.valueOf(start), String.valueOf(end)}, null, null, null);
        int re;
        if (c.getCount() == 0) {
            re = 0;
        } else {
            c.moveToFirst();
            re = c.getInt(0);
        }
        c.close();
        return re;
    }

    /**
     * Removes all entries with negative values.
     * <p/>
     * Only call this directly after boot, otherwise it might remove the current
     * day as the current offset is likely to be negative
     */
    public void removeNegativeEntries() {
        getWritableDatabase().delete(DB_NAME, "steps < ?", new String[]{"0"});
    }

    /**
     * Removes invalid entries from the database.
     * <p/>
     * Currently, an invalid input is such with steps >= 200,000
     */
    public void removeInvalidEntries() {
        getWritableDatabase().delete(DB_NAME, "steps >= ?", new String[]{"200000"});
    }

    /**
     * Get the number of 'valid' days (= days with a step value > 0).
     * <p/>
     * The current day is also added to this number, even if the value in the
     * database might still be < 0.
     * <p/>
     * It is safe to divide by the return value as this will be at least 1 (and
     * not 0).
     *
     * @return the number of days with a step value > 0, return will be >= 1
     */
    public int getDays() {
        Cursor c = getReadableDatabase()
                .query(DB_NAME, new String[]{"COUNT(*)"}, "steps > ? AND date < ? AND date > 0",
                        new String[]{String.valueOf(0), String.valueOf(Util.getToday())}, null,
                        null, null);
        c.moveToFirst();
        // todays is not counted yet
        int re = c.getInt(0) + 1;
        c.close();
        return re <= 0 ? 1 : re;
    }

    public int getNumberGluedStickers() {
        Cursor c = getReadableDatabase()
                .query(TABLE_STICKERS, new String[]{"COUNT(*)"}, "status=2",
                       null, null,
                        null, null);
        c.moveToFirst();
        int re = c.getInt(0) ;
        c.close();
        return re;
    }

    public int getNumberRecievedStickers() {
        Cursor c = getReadableDatabase()
                .query(TABLE_STICKERS, new String[]{"COUNT(*)"}, "status=1",
                        null, null,
                        null, null);
        c.moveToFirst();
        int re = c.getInt(0) ;
        c.close();
        return re;
    }


    /**
     * Saves the current 'steps since boot' sensor value in the database.
     *
     * @param steps since boot
     */
    public void saveCurrentSteps(int steps) {
        ContentValues values = new ContentValues();
        values.put("steps", steps);
        if (getWritableDatabase().update(DB_NAME, values, "date = -1", null) == 0) {
            values.put("date", -1);
            getWritableDatabase().insert(DB_NAME, null, values);
        }

        Log.w("saving steps in db: ", Integer.toString(steps));

    }

    /**
     * Reads the latest saved value for the 'steps since boot' sensor value.
     *
     * @return the current number of steps saved in the database or 0 if there
     * is no entry
     */
    public int getCurrentSteps() {
        int re = getSteps(-1);
        return re == Integer.MIN_VALUE ? 0 : re;
    }



    /**
     * Gets the date of the newest entry
     *
     * @return the date in milliseconds since 1970
     */
    public long getLastDay() {
        Cursor c = getReadableDatabase()
                .query(DB_NAME, new String[]{"date"}, null, null, null, null, "date DESC", "1");
        long re;
        if (c.moveToFirst()) {
            re = c.getLong(0);
        } else {
            re = System.currentTimeMillis();
        }
        c.close();
        return re;
    }



}
