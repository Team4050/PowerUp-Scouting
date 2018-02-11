package org.biohazard4050.powerupscouting;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.biohazard4050.powerupscouting.data.ScoutingContract.ScoutingData;
import org.biohazard4050.powerupscouting.data.ScoutingDbHelper;

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
        Intent theIntent = new Intent(this, MatchInfo.class);
        startActivity(theIntent);
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
}
