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

// TODO: Collect timestamp data for cube delivery

public class TeleOp extends AppCompatActivity {

    public int countExchangeBlue = 0;
    public int countExchangeRed = 0;
    public int countScale = 0;
    public int countSwitchBlue = 0;
    public int countSwitchRed = 0;

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
            LinearLayout exchangeLayout = (LinearLayout) findViewById(R.id.ll_exchange_blue);
            exchangeLayout.setVisibility(LinearLayout.INVISIBLE);
        } else {
            countExchangeRed = -1;
            LinearLayout exchangeLayout = (LinearLayout) findViewById(R.id.ll_exchange_red);
            exchangeLayout.setVisibility(LinearLayout.INVISIBLE);
        }

        setTitle("TeleOp [ Match: " + matchNumber + " | Team: " + teamNumber + " (" +
                allianceColor + ") ]");
    }

    public void changeCount(View view) {
        switch (view.getId()) {
            case R.id.btn_dec_exchange_red:
                countExchangeRed = updateCountAndDisplay(countExchangeRed, -1, R.id.tv_exchange_red);
                break;
            case R.id.btn_inc_exchange_red:
                countExchangeRed = updateCountAndDisplay(countExchangeRed, 1, R.id.tv_exchange_red);
                break;
            case R.id.btn_dec_switch_red:
                countSwitchRed = updateCountAndDisplay(countSwitchRed, -1, R.id.tv_switch_red);
                break;
            case R.id.btn_inc_switch_red:
                countSwitchRed = updateCountAndDisplay(countSwitchRed, 1, R.id.tv_switch_red);
                break;
            case R.id.btn_dec_scale:
                countScale = updateCountAndDisplay(countScale, -1, R.id.tv_scale_count);
                break;
            case R.id.btn_inc_scale:
                countScale = updateCountAndDisplay(countScale, 1, R.id.tv_scale_count);
                break;
            case R.id.btn_dec_switch_blue:
                countSwitchBlue = updateCountAndDisplay(countSwitchBlue, -1, R.id.tv_switch_blue);
                break;
            case R.id.btn_inc_switch_blue:
                countSwitchBlue = updateCountAndDisplay(countSwitchBlue, 1, R.id.tv_switch_blue);
                break;
            case R.id.btn_dec_exchange_blue:
                countExchangeBlue = updateCountAndDisplay(countExchangeBlue, -1, R.id.tv_exchange_blue);
                break;
            case R.id.btn_inc_exchange_blue:
                countExchangeBlue = updateCountAndDisplay(countExchangeBlue, 1, R.id.tv_exchange_blue);
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

            if (finalized.equals("Y")) {
                this.finish();
            }
        }
    }
}
