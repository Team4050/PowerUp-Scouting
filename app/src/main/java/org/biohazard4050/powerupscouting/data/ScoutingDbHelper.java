package org.biohazard4050.powerupscouting.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.biohazard4050.powerupscouting.data.ScoutingContract.ScoutingData;

public class ScoutingDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "scouting.db";

    private static final String SQL_CREATE_PRIMARY_DATA_TABLE =
            "CREATE TABLE " + ScoutingData.TABLE_PRIMARY_DATA + " (" +
                    ScoutingData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ScoutingData.COLUMN_INFO_SCOUTER_NAME + " TEXT," +
                    ScoutingData.COLUMN_INFO_MATCH_NUMBER + " INTEGER," +
                    ScoutingData.COLUMN_INFO_REMATCH_IND + " TEXT," +
                    ScoutingData.COLUMN_INFO_TEAM_NUMBER + " TEXT," +
                    ScoutingData.COLUMN_INFO_ALLIANCE_COLOR + " TEXT," +
                    ScoutingData.COLUMN_INFO_STATION_POSITION + " TEXT," +
                    ScoutingData.COLUMN_INFO_ROBOT_POSITION + " TEXT," +
                    ScoutingData.COLUMN_AUTO_BASELINE_IND + " TEXT," +
                    ScoutingData.COLUMN_AUTO_SWITCH_IND + " TEXT," +
                    ScoutingData.COLUMN_AUTO_SCALE_IND + " TEXT," +
                    ScoutingData.COLUMN_TELE_RED_EXCHANGE + " INTEGER," +
                    ScoutingData.COLUMN_TELE_RED_SWITCH + " INTEGER," +
                    ScoutingData.COLUMN_TELE_SCALE + " INTEGER," +
                    ScoutingData.COLUMN_TELE_BLUE_SWITCH + " INTEGER," +
                    ScoutingData.COLUMN_TELE_BLUE_EXCHANGE + " INTEGER," +
                    ScoutingData.COLUMN_TELE_AVE_CUBE_TIME + " TEXT," +
                    ScoutingData.COLUMN_EOM_END_STATE + " TEXT," +
                    ScoutingData.COLUMN_EOM_PENALTY_YELLOW + " TEXT," +
                    ScoutingData.COLUMN_EOM_PENALTY_RED + " TEXT," +
                    ScoutingData.COLUMN_EOM_PENALTY_FOUL + " TEXT," +
                    ScoutingData.COLUMN_EOM_PENALTY_DISABLED + " TEXT," +
                    ScoutingData.COLUMN_MATCH_NOTES + " TEXT)";

    private static final String SQL_DELETE_PRIMARY_DATA_TABLE =
            "DROP TABLE IF EXISTS " + ScoutingData.TABLE_PRIMARY_DATA;

    private static final String SQL_CREATE_STAGING_DATA_TABLE =
            "CREATE TABLE " + ScoutingData.TABLE_STAGING_DATA + " (" +
                    ScoutingData.COLUMN_INFO_SCOUTER_NAME + " TEXT," +
                    ScoutingData.COLUMN_INFO_MATCH_NUMBER + " INTEGER," +
                    ScoutingData.COLUMN_INFO_REMATCH_IND + " TEXT," +
                    ScoutingData.COLUMN_INFO_TEAM_NUMBER + " TEXT," +
                    ScoutingData.COLUMN_INFO_ALLIANCE_COLOR + " TEXT," +
                    ScoutingData.COLUMN_INFO_STATION_POSITION + " TEXT," +
                    ScoutingData.COLUMN_INFO_ROBOT_POSITION + " TEXT," +
                    ScoutingData.COLUMN_AUTO_BASELINE_IND + " TEXT," +
                    ScoutingData.COLUMN_AUTO_SWITCH_IND + " TEXT," +
                    ScoutingData.COLUMN_AUTO_SCALE_IND + " TEXT," +
                    ScoutingData.COLUMN_TELE_RED_EXCHANGE + " INTEGER," +
                    ScoutingData.COLUMN_TELE_RED_SWITCH + " INTEGER," +
                    ScoutingData.COLUMN_TELE_SCALE + " INTEGER," +
                    ScoutingData.COLUMN_TELE_BLUE_SWITCH + " INTEGER," +
                    ScoutingData.COLUMN_TELE_BLUE_EXCHANGE + " INTEGER," +
                    ScoutingData.COLUMN_TELE_AVE_CUBE_TIME + " TEXT," +
                    ScoutingData.COLUMN_EOM_END_STATE + " TEXT," +
                    ScoutingData.COLUMN_EOM_PENALTY_YELLOW + " TEXT," +
                    ScoutingData.COLUMN_EOM_PENALTY_RED + " TEXT," +
                    ScoutingData.COLUMN_EOM_PENALTY_FOUL + " TEXT," +
                    ScoutingData.COLUMN_EOM_PENALTY_DISABLED + " TEXT," +
                    ScoutingData.COLUMN_MATCH_NOTES + " TEXT," +
                    ScoutingData.COLUMN_FINALIZED_IND + " TEXT)";

    private static final String SQL_DELETE_STAGING_DATA_TABLE =
            "DROP TABLE IF EXISTS " + ScoutingData.TABLE_STAGING_DATA;

    public ScoutingDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PRIMARY_DATA_TABLE);
        db.execSQL(SQL_CREATE_STAGING_DATA_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_PRIMARY_DATA_TABLE);
        db.execSQL(SQL_DELETE_STAGING_DATA_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
