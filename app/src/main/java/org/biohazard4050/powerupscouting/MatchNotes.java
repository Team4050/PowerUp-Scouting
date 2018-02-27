package org.biohazard4050.powerupscouting;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.biohazard4050.powerupscouting.data.MatchRecord;
import org.biohazard4050.powerupscouting.data.ScoutingContract.ScoutingData;
import org.biohazard4050.powerupscouting.data.ScoutingDbHelper;

public class MatchNotes extends AppCompatActivity {

    private String matchNumber = "???";
    private String teamNumber = "???";
    private String allianceColor = "???";

    private String matchNotes = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_notes);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            if (extras != null) {
                matchNumber = extras.getString("MATCH_NUMBER");
                teamNumber = extras.getString("TEAM_NUMBER");
                allianceColor = extras.getString("ALLIANCE_COLOR");

                TextView titleText = (TextView) findViewById(R.id.titleTextView);

                if (allianceColor.equals("RED")) {
                    titleText.setTextColor(Color.RED);
                } else {
                    titleText.setTextColor(Color.BLUE);
                }
            }
        }

        setTitle("Match Notes [ Match: " + matchNumber + " | Team: " + teamNumber + " (" +
                allianceColor + ") ]");
    }

    public void onButtonClicked(View view) {
        EditText inputTxt = (EditText) findViewById(R.id.matchNotesEditText);
        matchNotes = inputTxt.getText().toString();

        writeToDB();

        MatchRecord matchRecord = new MatchRecord();
        readStagingData(matchRecord);
        writePrimaryData(matchRecord);

        this.finish();
    }

    private void writeToDB() {
        ScoutingDbHelper scoutingDbHelper = new ScoutingDbHelper(this);
        SQLiteDatabase scoutingDb = scoutingDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ScoutingData.COLUMN_MATCH_NOTES, matchNotes);
        values.put(ScoutingData.COLUMN_FINALIZED_IND, ScoutingData.CHECKBOX_CHECKED);

        int updateCount = scoutingDb.update(ScoutingData.TABLE_STAGING_DATA, values, null, null);

        if (updateCount == 0) {
            Toast missingDataToast = Toast.makeText(getApplicationContext(), "Error updating match", Toast.LENGTH_SHORT);
            missingDataToast.setGravity(Gravity.CENTER, 0, 0);
            missingDataToast.show();
        }

        scoutingDb.close();
    }

    private void readStagingData(MatchRecord matchRecord) {
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

        Cursor cursor = scoutingDb.query(ScoutingData.TABLE_STAGING_DATA, projection, null, null, null, null, null);

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
            }
        } finally {
            cursor.close();
        }

        scoutingDb.close();
    }

    private void writePrimaryData(MatchRecord matchRecord) {
        ScoutingDbHelper scoutingDbHelper = new ScoutingDbHelper(this);
        SQLiteDatabase scoutingDb = scoutingDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ScoutingData.COLUMN_INFO_SCOUTER_NAME, matchRecord.scouter);
        values.put(ScoutingData.COLUMN_INFO_MATCH_NUMBER, matchRecord.match);
        values.put(ScoutingData.COLUMN_INFO_REMATCH_IND, matchRecord.rematch);
        values.put(ScoutingData.COLUMN_INFO_TEAM_NUMBER, matchRecord.team);
        values.put(ScoutingData.COLUMN_INFO_ALLIANCE_COLOR, matchRecord.alliance);
        values.put(ScoutingData.COLUMN_INFO_STATION_POSITION, matchRecord.stationPos);
        values.put(ScoutingData.COLUMN_INFO_ROBOT_POSITION, matchRecord.robotPos);
        values.put(ScoutingData.COLUMN_AUTO_BASELINE_IND, matchRecord.baseline);
        values.put(ScoutingData.COLUMN_AUTO_SWITCH_IND, matchRecord.autoSwitch);
        values.put(ScoutingData.COLUMN_AUTO_SCALE_IND, matchRecord.autoScale);
        values.put(ScoutingData.COLUMN_TELE_RED_EXCHANGE, matchRecord.redExchange);
        values.put(ScoutingData.COLUMN_TELE_RED_SWITCH, matchRecord.redSwitch);
        values.put(ScoutingData.COLUMN_TELE_SCALE, matchRecord.scale);
        values.put(ScoutingData.COLUMN_TELE_BLUE_SWITCH, matchRecord.blueSwitch);
        values.put(ScoutingData.COLUMN_TELE_BLUE_EXCHANGE, matchRecord.blueExchange);
        values.put(ScoutingData.COLUMN_TELE_AVE_CUBE_TIME, matchRecord.averageCube);
        values.put(ScoutingData.COLUMN_EOM_END_STATE, matchRecord.endState);
        values.put(ScoutingData.COLUMN_EOM_PENALTY_YELLOW, matchRecord.penaltyYellow);
        values.put(ScoutingData.COLUMN_EOM_PENALTY_RED, matchRecord.penaltyRed);
        values.put(ScoutingData.COLUMN_EOM_PENALTY_FOUL, matchRecord.penaltyFoul);
        values.put(ScoutingData.COLUMN_EOM_PENALTY_DISABLED, matchRecord.penaltyDisabled);
        values.put(ScoutingData.COLUMN_MATCH_NOTES, matchRecord.matchNotes);

        try {
            long newRowId = scoutingDb.insertOrThrow(ScoutingData.TABLE_PRIMARY_DATA, null, values);

            if (newRowId == 0) {
                Toast missingDataToast = Toast.makeText(getApplicationContext(), "Error with saving to primary table", Toast.LENGTH_SHORT);
                missingDataToast.setGravity(Gravity.CENTER, 0, 0);
                missingDataToast.show();
            }
        } catch (Exception e) {
            Toast missingDataToast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
            missingDataToast.setGravity(Gravity.CENTER, 0, 0);
            missingDataToast.show();
        }

        scoutingDb.close();
    }
}
