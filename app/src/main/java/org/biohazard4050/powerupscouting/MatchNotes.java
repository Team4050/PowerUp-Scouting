package org.biohazard4050.powerupscouting;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonWriter;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.biohazard4050.powerupscouting.data.ScoutingContract.ScoutingData;
import org.biohazard4050.powerupscouting.data.ScoutingDbHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class MatchNotes extends AppCompatActivity {

    private String matchNumber = "???";
    private String teamNumber = "???";
    private String allianceColor = "???";

    private String scouter = "";
    private String match = "";
    private String rematch = "";
    private String team = "";
    private String alliance = "";
    private String position = "";
    private String baseline = "";
    private String autoSwitch = "";
    private String autoScale = "";
    private String redExchange = "";
    private String redSwitch = "";
    private String scale = "";
    private String blueSwitch = "";
    private String blueExchange = "";
    private String endState = "";
    private String penaltyYellow = "";
    private String penaltyRed = "";
    private String penaltyFoul = "";
    private String penaltyDisabled = "";
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
        EditText inputTxt = (EditText) findViewById(R.id.match_comments);
        matchNotes = inputTxt.getText().toString();

        writeToDB();

        readStagingData();
        writePrimaryData();

        String dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        String filename = String.format("%02d", Integer.parseInt(match)) + "_" +
                String.format("%04d", Integer.parseInt(team)) +"_" +
                scouter.replaceAll("[^A-Za-z0-9]", "") + "_" + rematch + ".json";

        try {
            File file = new File(dirPath, filename);
            FileOutputStream outputStream = new FileOutputStream(file, false);
            writeJsonStream(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            Toast missingDataToast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
            missingDataToast.setGravity(Gravity.CENTER, 0, 0);
            missingDataToast.show();
        }

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

    private void readStagingData() {
        ScoutingDbHelper scoutingDbHelper = new ScoutingDbHelper(this);
        SQLiteDatabase scoutingDb = scoutingDbHelper.getReadableDatabase();

        String[] projection = {
                ScoutingData.COLUMN_INFO_SCOUTER_NAME,
                ScoutingData.COLUMN_INFO_MATCH_NUMBER,
                ScoutingData.COLUMN_INFO_REMATCH_IND,
                ScoutingData.COLUMN_INFO_TEAM_NUMBER,
                ScoutingData.COLUMN_INFO_ALLIANCE_COLOR,
                ScoutingData.COLUMN_INFO_STARTING_POSITION,
                ScoutingData.COLUMN_AUTO_BASELINE_IND,
                ScoutingData.COLUMN_AUTO_SWITCH_IND,
                ScoutingData.COLUMN_AUTO_SCALE_IND,
                ScoutingData.COLUMN_TELE_RED_EXCHANGE,
                ScoutingData.COLUMN_TELE_RED_SWITCH,
                ScoutingData.COLUMN_TELE_SCALE,
                ScoutingData.COLUMN_TELE_BLUE_SWITCH,
                ScoutingData.COLUMN_TELE_BLUE_EXCHANGE,
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
            int positionIndex = cursor.getColumnIndex(ScoutingData.COLUMN_INFO_STARTING_POSITION);
            int baselineIndex = cursor.getColumnIndex(ScoutingData.COLUMN_AUTO_BASELINE_IND);
            int autoSwitchIndex = cursor.getColumnIndex(ScoutingData.COLUMN_AUTO_SWITCH_IND);
            int autoScaleIndex = cursor.getColumnIndex(ScoutingData.COLUMN_AUTO_SCALE_IND);
            int redExchangeIndex = cursor.getColumnIndex(ScoutingData.COLUMN_TELE_RED_EXCHANGE);
            int redSwitchIndex = cursor.getColumnIndex(ScoutingData.COLUMN_TELE_RED_SWITCH);
            int scaleIndex = cursor.getColumnIndex(ScoutingData.COLUMN_TELE_SCALE);
            int blueSwitchIndex = cursor.getColumnIndex(ScoutingData.COLUMN_TELE_BLUE_SWITCH);
            int blueExchangeIndex = cursor.getColumnIndex(ScoutingData.COLUMN_TELE_BLUE_EXCHANGE);
            int endStateIndex = cursor.getColumnIndex(ScoutingData.COLUMN_EOM_END_STATE);
            int penaltyYellowIndex = cursor.getColumnIndex(ScoutingData.COLUMN_EOM_PENALTY_YELLOW);
            int penaltyRedIndex = cursor.getColumnIndex(ScoutingData.COLUMN_EOM_PENALTY_RED);
            int penaltyFoulIndex = cursor.getColumnIndex(ScoutingData.COLUMN_EOM_PENALTY_FOUL);
            int penaltyDisabledIndex = cursor.getColumnIndex(ScoutingData.COLUMN_EOM_PENALTY_DISABLED);
            int matchNotesIndex = cursor.getColumnIndex(ScoutingData.COLUMN_MATCH_NOTES);
            int finalizedIndex = cursor.getColumnIndex(ScoutingData.COLUMN_FINALIZED_IND);

            while (cursor.moveToNext()) {
                scouter = cursor.getString(scouterIndex);
                match = cursor.getString(matchIndex);
                rematch = cursor.getString(rematchIndex);
                team = cursor.getString(teamIndex);
                alliance = cursor.getString(allianceIndex);
                position = cursor.getString(positionIndex);
                baseline = cursor.getString(baselineIndex);
                autoSwitch = cursor.getString(autoSwitchIndex);
                autoScale = cursor.getString(autoScaleIndex);
                redExchange = cursor.getString(redExchangeIndex);
                redSwitch = cursor.getString(redSwitchIndex);
                scale = cursor.getString(scaleIndex);
                blueSwitch = cursor.getString(blueSwitchIndex);
                blueExchange = cursor.getString(blueExchangeIndex);
                endState = cursor.getString(endStateIndex);
                penaltyYellow = cursor.getString(penaltyYellowIndex);
                penaltyRed = cursor.getString(penaltyRedIndex);
                penaltyFoul = cursor.getString(penaltyFoulIndex);
                penaltyDisabled = cursor.getString(penaltyDisabledIndex);
                matchNotes = cursor.getString(matchNotesIndex);
            }
        } finally {
            cursor.close();
        }

        scoutingDb.close();
    }

    private void writePrimaryData() {
        ScoutingDbHelper scoutingDbHelper = new ScoutingDbHelper(this);
        SQLiteDatabase scoutingDb = scoutingDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ScoutingData.COLUMN_INFO_SCOUTER_NAME, scouter);
        values.put(ScoutingData.COLUMN_INFO_MATCH_NUMBER, match);
        values.put(ScoutingData.COLUMN_INFO_REMATCH_IND, rematch);
        values.put(ScoutingData.COLUMN_INFO_TEAM_NUMBER, team);
        values.put(ScoutingData.COLUMN_INFO_ALLIANCE_COLOR, alliance);
        values.put(ScoutingData.COLUMN_INFO_STARTING_POSITION, position);
        values.put(ScoutingData.COLUMN_AUTO_BASELINE_IND, baseline);
        values.put(ScoutingData.COLUMN_AUTO_SWITCH_IND, autoSwitch);
        values.put(ScoutingData.COLUMN_AUTO_SCALE_IND, autoScale);
        values.put(ScoutingData.COLUMN_TELE_RED_EXCHANGE, redExchange);
        values.put(ScoutingData.COLUMN_TELE_RED_SWITCH, redSwitch);
        values.put(ScoutingData.COLUMN_TELE_SCALE, scale);
        values.put(ScoutingData.COLUMN_TELE_BLUE_SWITCH, blueSwitch);
        values.put(ScoutingData.COLUMN_TELE_BLUE_EXCHANGE, blueExchange);
        values.put(ScoutingData.COLUMN_EOM_END_STATE, endState);
        values.put(ScoutingData.COLUMN_EOM_PENALTY_YELLOW, penaltyYellow);
        values.put(ScoutingData.COLUMN_EOM_PENALTY_RED, penaltyRed);
        values.put(ScoutingData.COLUMN_EOM_PENALTY_FOUL, penaltyFoul);
        values.put(ScoutingData.COLUMN_EOM_PENALTY_DISABLED, penaltyDisabled);
        values.put(ScoutingData.COLUMN_MATCH_NOTES, matchNotes);

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

    private void writeJsonStream(OutputStream out) throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
        writer.setIndent("  ");

        writer.beginObject();
        writer.name("scouter_name").value(scouter);
        writer.name("match_number").value(match);
        writer.name("is_rematch").value(rematch);
        writer.name("team_number").value(team);
        writer.name("alliance_color").value(alliance);
        writer.name("starting_position").value(position);
        writer.name("auto_baseline").value(baseline);
        writer.name("auto_switch").value(autoSwitch);
        writer.name("auto_scale").value(autoScale);
        writer.name("red_exchange").value(redExchange);
        writer.name("red_switch").value(redSwitch);
        writer.name("scale").value(scale);
        writer.name("blue_switch").value(blueSwitch);
        writer.name("blue_exchange").value(blueExchange);
        writer.name("end_state").value(endState);
        writer.name("penalty_yellow").value(penaltyYellow);
        writer.name("penalty_red").value(penaltyRed);
        writer.name("penalty_foul").value(penaltyFoul);
        writer.name("penalty_disabled").value(penaltyDisabled);
        writer.name("match_notes").value(matchNotes);
        writer.endObject();

        writer.close();
    }
}
