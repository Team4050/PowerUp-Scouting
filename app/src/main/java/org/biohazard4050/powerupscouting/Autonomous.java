package org.biohazard4050.powerupscouting;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import org.biohazard4050.powerupscouting.data.ScoutingContract.ScoutingData;
import org.biohazard4050.powerupscouting.data.ScoutingDbHelper;

public class Autonomous extends AppCompatActivity {

    private String crossedBaseline = ScoutingData.CHECKBOX_UNCHECKED;
    private String cubeToSwitch = ScoutingData.CHECKBOX_UNCHECKED;
    private String cubeToScale = ScoutingData.CHECKBOX_UNCHECKED;

    private String matchNumber;
    private String teamNumber;
    private String allianceColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autonomous);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            if (extras != null) {
                matchNumber = extras.getString("MATCH_NUMBER");
                teamNumber = extras.getString("TEAM_NUMBER");
                allianceColor = extras.getString("ALLIANCE_COLOR");

                TextView titleText = (TextView) findViewById(R.id.titleTextView);

                if (allianceColor.equals(ScoutingData.ALLIANCE_RED)) {
                    titleText.setTextColor(Color.RED);
                } else {
                    titleText.setTextColor(Color.BLUE);
                }
            }
        }

        setTitle("Autonomous [ Match: " + matchNumber + " | Team: " + teamNumber + " (" +
                allianceColor + ") ]");
    }

    public void onCheckboxClicked(View view) {
        boolean isChecked = ((CheckBox) view).isChecked();

        switch (view.getId()) {
            case R.id.baselineCheckbox:
                crossedBaseline = (isChecked ? ScoutingData.CHECKBOX_CHECKED : ScoutingData.CHECKBOX_UNCHECKED);
                break;
            case R.id.switchCheckbox:
                cubeToSwitch = (isChecked ? ScoutingData.CHECKBOX_CHECKED : ScoutingData.CHECKBOX_UNCHECKED);
                break;
            case R.id.scaleCheckbox:
                cubeToScale = (isChecked ? ScoutingData.CHECKBOX_CHECKED : ScoutingData.CHECKBOX_UNCHECKED);
                break;
        }
    }

    public void onButtonClicked(View view) {
        writeToDB();

        Intent theIntent = new Intent(Autonomous.this, TeleOp.class);

        theIntent.putExtra("MATCH_NUMBER", matchNumber);
        theIntent.putExtra("TEAM_NUMBER", teamNumber);
        theIntent.putExtra("ALLIANCE_COLOR", allianceColor);

        startActivity(theIntent);
    }

    private void writeToDB() {
        ScoutingDbHelper scoutingDbHelper = new ScoutingDbHelper(this);
        SQLiteDatabase scoutingDb = scoutingDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ScoutingData.COLUMN_AUTO_BASELINE_IND, crossedBaseline);
        values.put(ScoutingData.COLUMN_AUTO_SWITCH_IND, cubeToSwitch);
        values.put(ScoutingData.COLUMN_AUTO_SCALE_IND, cubeToScale);

        int updateCount = scoutingDb.update(ScoutingData.TABLE_STAGING_DATA, values, null, null);

        if (updateCount == 0) {
            Toast missingDataToast = Toast.makeText(getApplicationContext(), "Error updating match", Toast.LENGTH_SHORT);
            missingDataToast.setGravity(Gravity.CENTER, 0, 0);
            missingDataToast.show();
        }

        scoutingDb.close();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            ScoutingDbHelper scoutingDbHelper = new ScoutingDbHelper(this);
            SQLiteDatabase scoutingDb = scoutingDbHelper.getReadableDatabase();

            String[] projection = {
                    ScoutingData.COLUMN_FINALIZED_IND
            };

            String finalized = "";

            Cursor cursor = scoutingDb.query(ScoutingData.TABLE_STAGING_DATA, projection, null, null, null, null, null);

            try {
                int finalizedIndex = cursor.getColumnIndex(ScoutingData.COLUMN_FINALIZED_IND);

                while (cursor.moveToNext()) {
                    finalized = cursor.getString(finalizedIndex);
                }
            } finally {
                cursor.close();
            }

            scoutingDb.close();

            if (finalized.equals(ScoutingData.CHECKBOX_CHECKED)) {
                this.finish();
            }
        }
    }
}
