package org.biohazard4050.powerupscouting;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonWriter;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import org.biohazard4050.powerupscouting.data.MatchRecord;
import org.biohazard4050.powerupscouting.data.ScoutingContract.ScoutingData;
import org.biohazard4050.powerupscouting.data.ScoutingDbHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

// TODO: Write out only exchange value for alliance color, not "-1" value

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ScoutingDbHelper scoutingDbHelper = new ScoutingDbHelper(this);
        SQLiteDatabase scoutingDb = scoutingDbHelper.getWritableDatabase();
        scoutingDb.delete(ScoutingData.TABLE_STAGING_DATA, null, null);
        scoutingDb.close();
    }

    public void onButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.beginScoutingButton:
                Intent theIntent = new Intent(this, MatchInfo.class);
                startActivity(theIntent);
                break;
            case R.id.dumpDataFileButton:
                writeDataFile();
                break;
        }
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            ScoutingDbHelper scoutingDbHelper = new ScoutingDbHelper(this);
            SQLiteDatabase scoutingDb = scoutingDbHelper.getWritableDatabase();
            scoutingDb.delete(ScoutingData.TABLE_STAGING_DATA, null, null);
            scoutingDb.close();
        }
    }

    private void writeDataFile() {
        String dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        String filename = "powerup_scouting.json";
        String resultMessage = "";

        try {
            ArrayList<MatchRecord> matchDataRecords = new ArrayList<MatchRecord>();

            readMatchDataFromDB(matchDataRecords);

            File file = new File(dirPath, filename);
            FileOutputStream outputStream = new FileOutputStream(file, false);

            JsonWriter writer = new JsonWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.setIndent("  ");

            writer.beginObject();
            writer.name("matches");
            writer.beginArray();

            for (MatchRecord matchRecord : matchDataRecords) {
                writeMatchRecord(matchRecord, writer);
            }

            writer.endArray();
            writer.endObject();
            writer.close();

            outputStream.flush();
            outputStream.close();

            resultMessage = "File Written Successfully";
        } catch (Exception e) {
            resultMessage = e.getMessage();
        }

        Toast missingDataToast = Toast.makeText(getApplicationContext(), resultMessage, Toast.LENGTH_LONG);
        missingDataToast.setGravity(Gravity.CENTER, 0, 0);
        missingDataToast.show();
    }

    private void readMatchDataFromDB(ArrayList<MatchRecord> matchDataRecords) {
        ScoutingDbHelper scoutingDbHelper = new ScoutingDbHelper(this);
        SQLiteDatabase scoutingDb = scoutingDbHelper.getReadableDatabase();

        String[] projection = {
                ScoutingData.COLUMN_INFO_SCOUTER_NAME,
                ScoutingData.COLUMN_INFO_MATCH_NUMBER,
                ScoutingData.COLUMN_INFO_REMATCH_IND,
                ScoutingData.COLUMN_INFO_TEAM_NUMBER,
                ScoutingData.COLUMN_INFO_ALLIANCE_COLOR,
                ScoutingData.COLUMN_INFO_STATION_POSITION,
                ScoutingData.COLUMN_INFO_ROBOT_POSITION,
                ScoutingData.COLUMN_AUTO_BASELINE_IND,
                ScoutingData.COLUMN_AUTO_SWITCH_IND,
                ScoutingData.COLUMN_AUTO_SCALE_IND,
                ScoutingData.COLUMN_TELE_RED_EXCHANGE,
                ScoutingData.COLUMN_TELE_RED_SWITCH,
                ScoutingData.COLUMN_TELE_SCALE,
                ScoutingData.COLUMN_TELE_BLUE_SWITCH,
                ScoutingData.COLUMN_TELE_BLUE_EXCHANGE,
                ScoutingData.COLUMN_TELE_AVE_CUBE_TIME,
                ScoutingData.COLUMN_EOM_END_STATE,
                ScoutingData.COLUMN_EOM_PENALTY_YELLOW,
                ScoutingData.COLUMN_EOM_PENALTY_RED,
                ScoutingData.COLUMN_EOM_PENALTY_FOUL,
                ScoutingData.COLUMN_EOM_PENALTY_DISABLED,
                ScoutingData.COLUMN_MATCH_NOTES
        };

        Cursor cursor = scoutingDb.query(ScoutingData.TABLE_PRIMARY_DATA, projection, null, null, null, null, ScoutingData._ID);

        try {
            int scouterIndex = cursor.getColumnIndex(ScoutingData.COLUMN_INFO_SCOUTER_NAME);
            int matchIndex = cursor.getColumnIndex(ScoutingData.COLUMN_INFO_MATCH_NUMBER);
            int rematchIndex = cursor.getColumnIndex(ScoutingData.COLUMN_INFO_REMATCH_IND);
            int teamIndex = cursor.getColumnIndex(ScoutingData.COLUMN_INFO_TEAM_NUMBER);
            int allianceIndex = cursor.getColumnIndex(ScoutingData.COLUMN_INFO_ALLIANCE_COLOR);
            int stationIndex = cursor.getColumnIndex(ScoutingData.COLUMN_INFO_STATION_POSITION);
            int robotIndex = cursor.getColumnIndex(ScoutingData.COLUMN_INFO_ROBOT_POSITION);
            int baselineIndex = cursor.getColumnIndex(ScoutingData.COLUMN_AUTO_BASELINE_IND);
            int autoSwitchIndex = cursor.getColumnIndex(ScoutingData.COLUMN_AUTO_SWITCH_IND);
            int autoScaleIndex = cursor.getColumnIndex(ScoutingData.COLUMN_AUTO_SCALE_IND);
            int redExchangeIndex = cursor.getColumnIndex(ScoutingData.COLUMN_TELE_RED_EXCHANGE);
            int redSwitchIndex = cursor.getColumnIndex(ScoutingData.COLUMN_TELE_RED_SWITCH);
            int scaleIndex = cursor.getColumnIndex(ScoutingData.COLUMN_TELE_SCALE);
            int blueSwitchIndex = cursor.getColumnIndex(ScoutingData.COLUMN_TELE_BLUE_SWITCH);
            int blueExchangeIndex = cursor.getColumnIndex(ScoutingData.COLUMN_TELE_BLUE_EXCHANGE);
            int averageCubeIndex = cursor.getColumnIndex(ScoutingData.COLUMN_TELE_AVE_CUBE_TIME);
            int endStateIndex = cursor.getColumnIndex(ScoutingData.COLUMN_EOM_END_STATE);
            int penaltyYellowIndex = cursor.getColumnIndex(ScoutingData.COLUMN_EOM_PENALTY_YELLOW);
            int penaltyRedIndex = cursor.getColumnIndex(ScoutingData.COLUMN_EOM_PENALTY_RED);
            int penaltyFoulIndex = cursor.getColumnIndex(ScoutingData.COLUMN_EOM_PENALTY_FOUL);
            int penaltyDisabledIndex = cursor.getColumnIndex(ScoutingData.COLUMN_EOM_PENALTY_DISABLED);
            int matchNotesIndex = cursor.getColumnIndex(ScoutingData.COLUMN_MATCH_NOTES);

            while (cursor.moveToNext()) {
                MatchRecord matchRecord = new MatchRecord();

                matchRecord.scouter = cursor.getString(scouterIndex);
                matchRecord.match = cursor.getString(matchIndex);
                matchRecord.rematch = cursor.getString(rematchIndex);
                matchRecord.team = cursor.getString(teamIndex);
                matchRecord.alliance = cursor.getString(allianceIndex);
                matchRecord.stationPos = cursor.getString(stationIndex);
                matchRecord.robotPos = cursor.getString(robotIndex);
                matchRecord.baseline = cursor.getString(baselineIndex);
                matchRecord.autoSwitch = cursor.getString(autoSwitchIndex);
                matchRecord.autoScale = cursor.getString(autoScaleIndex);
                matchRecord.redExchange = cursor.getString(redExchangeIndex);
                matchRecord.redSwitch = cursor.getString(redSwitchIndex);
                matchRecord.scale = cursor.getString(scaleIndex);
                matchRecord.blueSwitch = cursor.getString(blueSwitchIndex);
                matchRecord.blueExchange = cursor.getString(blueExchangeIndex);
                matchRecord.averageCube = cursor.getString(averageCubeIndex);
                matchRecord.endState = cursor.getString(endStateIndex);
                matchRecord.penaltyYellow = cursor.getString(penaltyYellowIndex);
                matchRecord.penaltyRed = cursor.getString(penaltyRedIndex);
                matchRecord.penaltyFoul = cursor.getString(penaltyFoulIndex);
                matchRecord.penaltyDisabled = cursor.getString(penaltyDisabledIndex);
                matchRecord.matchNotes = cursor.getString(matchNotesIndex);

                matchDataRecords.add(matchRecord);
            }
        } finally {
            cursor.close();
        }

        scoutingDb.close();
    }

    private void writeMatchRecord(MatchRecord matchRecord, JsonWriter writer) throws IOException {
        writer.beginObject();
        writer.name("scouter_name").value(matchRecord.scouter);
        writer.name("match_number").value(matchRecord.match);
        writer.name("is_rematch").value(matchRecord.rematch);
        writer.name("team_number").value(matchRecord.team);
        writer.name("alliance_color").value(matchRecord.alliance);
        writer.name("station_position").value(matchRecord.stationPos);
        writer.name("robot_position").value(matchRecord.robotPos);
        writer.name("auto_baseline").value(matchRecord.baseline);
        writer.name("auto_switch").value(matchRecord.autoSwitch);
        writer.name("auto_scale").value(matchRecord.autoScale);

        if (matchRecord.blueExchange.equals("-1")) {
            writer.name("exchange").value(matchRecord.redExchange);
        } else {
            writer.name("exchange").value(matchRecord.blueExchange);
        }
        writer.name("red_switch").value(matchRecord.redSwitch);
        writer.name("scale").value(matchRecord.scale);
        writer.name("blue_switch").value(matchRecord.blueSwitch);
        writer.name("ave_cube_time").value(matchRecord.averageCube);
        writer.name("end_state").value(matchRecord.endState);
        writer.name("penalty_yellow").value(matchRecord.penaltyYellow);
        writer.name("penalty_red").value(matchRecord.penaltyRed);
        writer.name("penalty_foul").value(matchRecord.penaltyFoul);
        writer.name("penalty_disabled").value(matchRecord.penaltyDisabled);
        writer.name("match_notes").value(matchRecord.matchNotes);
        writer.endObject();
    }
}
