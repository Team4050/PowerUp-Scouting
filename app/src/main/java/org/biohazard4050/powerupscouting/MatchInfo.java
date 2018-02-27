package org.biohazard4050.powerupscouting;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.biohazard4050.powerupscouting.data.ScoutingContract.ScoutingData;
import org.biohazard4050.powerupscouting.data.ScoutingDbHelper;

public class MatchInfo extends AppCompatActivity {

    private static final int COLOR_PRIMARY_DARK = 0xFF303F9F;

    private TextView scouterTextView;
    private TextView matchTextView;
    private TextView teamTextView;

    private String scouterName = "";
    private String matchNumber = "";
    private String rematchInd = ScoutingData.CHECKBOX_UNCHECKED;
    private String teamNumber = "";
    private String allianceColor = "";
    private String stationPosition = "";
    private String robotPosition = "";

    private boolean scouterEntered = false;
    private boolean matchEntered = false;
    private boolean teamEntered = false;
    private boolean allianceSelected = false;
    private boolean stationSelected = false;
    private boolean positionSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_info);

        EditText scouterEditText = (EditText) findViewById(R.id.scouterNameEditText);
        EditText matchEditText = (EditText) findViewById(R.id.matchNumberEditText);
        EditText teamEditText = (EditText) findViewById(R.id.teamNumberEditText);

        scouterEditText.addTextChangedListener(scouterWatcher);
        matchEditText.addTextChangedListener(matchWatcher);
        teamEditText.addTextChangedListener(teamWatcher);

        scouterTextView = (TextView) findViewById(R.id.scouterNameTextView);
        matchTextView = (TextView) findViewById(R.id.matchNumberTextView);
        teamTextView = (TextView) findViewById(R.id.teamNumberTextView);

        setTitle(getString(R.string.app_name));
    }

    public void onCheckboxClicked(View view) {
        if (((CheckBox) view).isChecked()) {
            rematchInd = ScoutingData.CHECKBOX_CHECKED;
        } else {
            rematchInd = ScoutingData.CHECKBOX_UNCHECKED;
        }
    }

    public void onButtonClicked(View view) {
        boolean validationError = false;
        String errorMessage = "";

        if (!scouterEntered || !matchEntered || !teamEntered ||
            !allianceSelected || !stationSelected || !positionSelected) {
            validationError = true;
            errorMessage += "\r\nYOU MUST COMPLETE THE ENTIRE FORM\r\n" +
                    ">> Look for fields where the label is red <<\r\n";
        }

        int matchCount = existingMatchRecords();

        if (rematchInd == ScoutingData.CHECKBOX_CHECKED) {
            if (matchCount == 0) {
                validationError = true;
                errorMessage += "\r\n!! NO PREVIOUS MATCH EXISTS !!\r\n" +
                        ">> Uncheck the Rematch box or change the match number <<\r\n";
            }
        } else {
            if (matchCount > 0) {
                validationError = true;
                errorMessage += "\r\n!! MATCH NUMBER EXISTS !!\r\n" +
                        "Check the Rematch box or change the match number <<\r\n";
            }
        }

        if (validationError) {
            Toast missingDataToast = Toast.makeText(getApplicationContext(),
                    errorMessage, Toast.LENGTH_SHORT);
            missingDataToast.setGravity(Gravity.CENTER, 0, 150);
            missingDataToast.show();
        } else {
            // Grab scouter name
            EditText inputTxt = (EditText) findViewById(R.id.scouterNameEditText);
            scouterName = inputTxt.getText().toString();

            // Grab match number
            inputTxt = (EditText) findViewById(R.id.matchNumberEditText);
            matchNumber = inputTxt.getText().toString();

            // Grab team number
            inputTxt = (EditText) findViewById(R.id.teamNumberEditText);
            teamNumber = inputTxt.getText().toString();

            writeToDB();

            Intent theIntent = new Intent(this, Autonomous.class);

            theIntent.putExtra("MATCH_NUMBER", matchNumber);
            theIntent.putExtra("TEAM_NUMBER", teamNumber);
            theIntent.putExtra("ALLIANCE_COLOR", allianceColor);

            startActivity(theIntent);
        }
    }

    private int existingMatchRecords() {
        int matchCount = 0;

        ScoutingDbHelper scoutingDbHelper = new ScoutingDbHelper(this);
        SQLiteDatabase scoutingDb = scoutingDbHelper.getReadableDatabase();

        String[] projection = {ScoutingData.COLUMN_INFO_MATCH_NUMBER};

        String tempMatchNumber = ((EditText) findViewById(R.id.matchNumberEditText)).getText().toString();

        String[] matchNumberCriteria = {tempMatchNumber};

        Cursor cursor = scoutingDb.query(ScoutingData.TABLE_PRIMARY_DATA, projection,
                ScoutingData.COLUMN_INFO_MATCH_NUMBER + " = ?", matchNumberCriteria,
                null, null, null);

        try {
            matchCount = cursor.getCount();

        } finally {
            cursor.close();
        }

        scoutingDb.close();

        return matchCount;
    }

    public void onRadioButtonClicked(View view) {
        boolean isChecked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.allianceRedRadioButton:
                if (isChecked) {
                    allianceColor = ScoutingData.ALLIANCE_RED;
                    if (!allianceSelected) {
                        allianceSelected = true;
                        ((TextView) findViewById(R.id.allianceTextView)).setTextColor(COLOR_PRIMARY_DARK);
                    }
                }
                break;
            case R.id.allianceBlueRadioButton:
                if (isChecked) {
                    allianceColor = ScoutingData.ALLIANCE_BLUE;
                    if (!allianceSelected) {
                        allianceSelected = true;
                        ((TextView) findViewById(R.id.allianceTextView)).setTextColor(COLOR_PRIMARY_DARK);
                    }
                }
                break;
            case R.id.stationFarRadioButton:
                if (isChecked) {
                    stationPosition = ScoutingData.POSITION_FAR;
                    if (!stationSelected) {
                        stationSelected = true;
                        ((TextView) findViewById(R.id.stationTextView)).setTextColor(COLOR_PRIMARY_DARK);
                    }
                }
                break;
            case R.id.stationMiddleRadioButton:
                if (isChecked) {
                    stationPosition = ScoutingData.POSITION_MIDDLE;
                    if (!stationSelected) {
                        stationSelected = true;
                        ((TextView) findViewById(R.id.stationTextView)).setTextColor(COLOR_PRIMARY_DARK);
                    }
                }
                break;
            case R.id.stationNearRadioButton:
                if (isChecked) {
                    stationPosition = ScoutingData.POSITION_NEAR;
                    if (!stationSelected) {
                        stationSelected = true;
                        ((TextView) findViewById(R.id.stationTextView)).setTextColor(COLOR_PRIMARY_DARK);
                    }
                }
                break;
            case R.id.robotFarRadioButton:
                if (isChecked) {
                    robotPosition = ScoutingData.POSITION_FAR;
                    if (!positionSelected) {
                        positionSelected = true;
                        ((TextView) findViewById(R.id.positionTextView)).setTextColor(COLOR_PRIMARY_DARK);
                    }
                }
                break;
            case R.id.robotMiddleRadioButton:
                if (isChecked) {
                    robotPosition = ScoutingData.POSITION_MIDDLE;
                    if (!positionSelected) {
                        positionSelected = true;
                        ((TextView) findViewById(R.id.positionTextView)).setTextColor(COLOR_PRIMARY_DARK);
                    }
                }
                break;
            case R.id.robotNearRadioButton:
                if (isChecked) {
                    robotPosition = ScoutingData.POSITION_NEAR;
                    if (!positionSelected) {
                        positionSelected = true;
                        ((TextView) findViewById(R.id.positionTextView)).setTextColor(COLOR_PRIMARY_DARK);
                    }
                }
                break;
        }
    }

    private void setRequiredTextColor(int fieldLength, TextView fieldLabel) {
        if (fieldLength == 0) {
            fieldLabel.setTextColor(Color.RED);
        } else {
            fieldLabel.setTextColor(COLOR_PRIMARY_DARK);
        }
    }

    private void writeToDB() {
        boolean insertRow = true;

        ScoutingDbHelper scoutingDbHelper = new ScoutingDbHelper(this);
        SQLiteDatabase scoutingDb = scoutingDbHelper.getWritableDatabase();

        String[] projection = {
                ScoutingData.COLUMN_INFO_SCOUTER_NAME
        };

        Cursor cursor = scoutingDb.query(ScoutingData.TABLE_STAGING_DATA, projection, null, null, null, null, null);

        try {
            if (cursor.getCount() > 0) {
                insertRow = false;
            }
        } finally {
            cursor.close();
        }

        ContentValues values = new ContentValues();
        values.put(ScoutingData.COLUMN_INFO_SCOUTER_NAME, scouterName);
        values.put(ScoutingData.COLUMN_INFO_MATCH_NUMBER, matchNumber);
        values.put(ScoutingData.COLUMN_INFO_REMATCH_IND, rematchInd);
        values.put(ScoutingData.COLUMN_INFO_TEAM_NUMBER, teamNumber);
        values.put(ScoutingData.COLUMN_INFO_ALLIANCE_COLOR, allianceColor);
        values.put(ScoutingData.COLUMN_INFO_STATION_POSITION, stationPosition);
        values.put(ScoutingData.COLUMN_INFO_ROBOT_POSITION, robotPosition);
        values.put(ScoutingData.COLUMN_FINALIZED_IND, ScoutingData.CHECKBOX_UNCHECKED);

        String toastMessage = "NO_ERROR";

        if (insertRow) {
            long newRowId = scoutingDb.insert(ScoutingData.TABLE_STAGING_DATA, null, values);
            if (newRowId == 0) {
                toastMessage = "Error with saving match";
            }
        } else {
            int updateCount = scoutingDb.update(ScoutingData.TABLE_STAGING_DATA, values, null, null);
            if (updateCount < 1) {
                toastMessage = "Error updating match";
            }
        }

        if (!toastMessage.equals("NO_ERROR")) {
            Toast missingDataToast = Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT);
            missingDataToast.setGravity(Gravity.CENTER, 0, 0);
            missingDataToast.show();
        }

        scoutingDb.close();
    }

    private final TextWatcher scouterWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            scouterEntered = (s.length() > 0);
            setRequiredTextColor(s.length(), scouterTextView);
        }
    };

    private final TextWatcher matchWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            matchEntered = (s.length() > 0);
            setRequiredTextColor(s.length(), matchTextView);
        }
    };

    private final TextWatcher teamWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            teamEntered = (s.length() > 0);
            setRequiredTextColor(s.length(), teamTextView);
        }
    };

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
