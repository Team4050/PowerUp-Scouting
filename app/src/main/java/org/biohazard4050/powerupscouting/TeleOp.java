package org.biohazard4050.powerupscouting;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.biohazard4050.powerupscouting.data.ScoutingContract.ScoutingData;
import org.biohazard4050.powerupscouting.data.ScoutingDbHelper;

import java.util.Locale;

public class TeleOp extends AppCompatActivity {

    private int countExchangeBlue = 0;
    private int countExchangeRed = 0;
    private int countScale = 0;
    private int countSwitchBlue = 0;
    private int countSwitchRed = 0;
    private String averageCubeTime = "-1.0";

    private float[] secondsToDeliver;
    private int cubeArrayIndex = 0;
    private long previousCubeTime;
    private long currentCubeTime;

    private String matchNumber;
    private String teamNumber;
    private String allianceColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tele_op);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            if (extras != null) {
                matchNumber = extras.getString("MATCH_NUMBER");
                teamNumber = extras.getString("TEAM_NUMBER");
                allianceColor = extras.getString("ALLIANCE_COLOR");
            }
        }

        if (allianceColor.equals("RED")) {
            countExchangeBlue = -1;
            LinearLayout exchangeLayout = (LinearLayout) findViewById(R.id.exchangeBlueLayout);
            exchangeLayout.setVisibility(LinearLayout.INVISIBLE);
        } else {
            countExchangeRed = -1;
            LinearLayout exchangeLayout = (LinearLayout) findViewById(R.id.exchangeRedLayout);
            exchangeLayout.setVisibility(LinearLayout.INVISIBLE);
        }

        setTitle("TeleOp [ Match: " + matchNumber + " | Team: " + teamNumber + " (" +
                allianceColor + ") ]");

        secondsToDeliver = new float[99];
        previousCubeTime = System.currentTimeMillis();
        currentCubeTime  = System.currentTimeMillis();
    }

    public void changeCount(View view) {
        previousCubeTime = currentCubeTime;
        currentCubeTime = System.currentTimeMillis();
        secondsToDeliver[cubeArrayIndex] = (((float) (currentCubeTime - previousCubeTime)) / 1000.0f);
        cubeArrayIndex++;

        switch (view.getId()) {
            case R.id.decExchangeRedButton:
                countExchangeRed = updateCountAndDisplay(countExchangeRed, -1, R.id.exchangeRedCountTextView);
                break;
            case R.id.incExchangeRedButton:
                countExchangeRed = updateCountAndDisplay(countExchangeRed, 1, R.id.exchangeRedCountTextView);
                break;
            case R.id.decSwitchRedButton:
                countSwitchRed = updateCountAndDisplay(countSwitchRed, -1, R.id.switchRedCountTextView);
                break;
            case R.id.incSwitchRedButton:
                countSwitchRed = updateCountAndDisplay(countSwitchRed, 1, R.id.switchRedCountTextView);
                break;
            case R.id.decScaleButton:
                countScale = updateCountAndDisplay(countScale, -1, R.id.scaleCountTextView);
                break;
            case R.id.incScaleButton:
                countScale = updateCountAndDisplay(countScale, 1, R.id.scaleCountTextView);
                break;
            case R.id.decSwitchBlueButton:
                countSwitchBlue = updateCountAndDisplay(countSwitchBlue, -1, R.id.switchBlueCountTextView);
                break;
            case R.id.incSwitchBlueButton:
                countSwitchBlue = updateCountAndDisplay(countSwitchBlue, 1, R.id.switchBlueCountTextView);
                break;
            case R.id.decExchangeBlueButton:
                countExchangeBlue = updateCountAndDisplay(countExchangeBlue, -1, R.id.exchangeBlueCountTextView);
                break;
            case R.id.incExchangeBlueButton:
                countExchangeBlue = updateCountAndDisplay(countExchangeBlue, 1, R.id.exchangeBlueCountTextView);
                break;
            default:
                break;
        }
    }

    // Update count, ensuring 0-99; update display; return new count value.
    private int updateCountAndDisplay(int countVar, int incValue, int itemId) {
        countVar += incValue;

        countVar = ((countVar < 0) ? 0 : countVar);
        countVar = ((countVar > 99) ? 99 : countVar);

        TextView quantityTextView = (TextView) findViewById(itemId);
        quantityTextView.setText("" + countVar);

        return countVar;
    }

    public void onButtonClicked(View view) {
        float totalSeconds = 0;

        if (cubeArrayIndex == 0) {
            averageCubeTime = "0.0";
        } else {
            for (int idx = 0; idx < cubeArrayIndex; idx++)
            {
                totalSeconds += secondsToDeliver[idx];
            }

            float averageSeconds = (totalSeconds / (float) cubeArrayIndex);

            averageCubeTime = String.format(Locale.US,"%.1f", averageSeconds);
        }

        writeToDB();

        Intent theIntent = new Intent(TeleOp.this, EndOfMatch.class);

        theIntent.putExtra("MATCH_NUMBER", matchNumber);
        theIntent.putExtra("TEAM_NUMBER", teamNumber);
        theIntent.putExtra("ALLIANCE_COLOR", allianceColor);

        startActivity(theIntent);
    }

    private void writeToDB() {
        ScoutingDbHelper scoutingDbHelper = new ScoutingDbHelper(this);
        SQLiteDatabase scoutingDb = scoutingDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ScoutingData.COLUMN_TELE_RED_EXCHANGE, countExchangeRed);
        values.put(ScoutingData.COLUMN_TELE_RED_SWITCH, countSwitchRed);
        values.put(ScoutingData.COLUMN_TELE_SCALE, countScale);
        values.put(ScoutingData.COLUMN_TELE_BLUE_SWITCH, countSwitchBlue);
        values.put(ScoutingData.COLUMN_TELE_BLUE_EXCHANGE, countExchangeBlue);
        values.put(ScoutingData.COLUMN_TELE_AVE_CUBE_TIME, averageCubeTime);

        int updateCount = scoutingDb.update(ScoutingData.TABLE_STAGING_DATA, values, null, null);

        if (updateCount == 0) {
            Toast missingDataToast = Toast.makeText(getApplicationContext(), "Error updating match", Toast.LENGTH_LONG);
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
