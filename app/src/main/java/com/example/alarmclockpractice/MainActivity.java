package com.example.alarmclockpractice;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    private LinkedList<ClockTimes> mAlarms = new LinkedList<ClockTimes>();
    public CoordinatorLayout coordinatorLayout;
    private Switch onOffButton;
    private RecyclerView mRecyclerView;
    private ClockAdapter mAdapter;
    private static final int REQUEST_CODE_NEW_ALARM = 1;
    private static final int REQUEST_CODE_EDIT_ALARM = 2;

    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.alarmclockpractice";



    //alarm clock set up
    AlarmManager alarmManager;
    private PendingIntent pendingIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        //set up shared pref for saving data
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PreferenceManager.setDefaultValues(this, R.xml.pref_data_sync, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String switch_pref = sharedPreferences.getString(SettingsActivity.KEY_PREF_EXAMPLE, "-1");
        Toast.makeText(getApplicationContext(), switch_pref, Toast.LENGTH_LONG).show();

        //default single alarm for testing purposes
        ClockTimes testClock = new ClockTimes("8", "45", false);
        mAlarms.add(testClock);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newAlarmIntent = new Intent(getApplicationContext(), SetAlarm.class);
                startActivityForResult(newAlarmIntent, REQUEST_CODE_NEW_ALARM);
            }
        });

        //recall sharedpreferences data
        if (mPreferences.contains("ListAlams")) {
            Gson gson = new Gson();
            String json = mPreferences.getString("ListAlarms", null);
            if (json != null) {
                mAlarms = gson.fromJson(json, mAlarms.getClass());
            }
        }

        //recall any saved instance state data

        mRecyclerView = findViewById(R.id.recycler_view);

        updateAdapter();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.setOnItemClickListener(new ClockAdapter.ClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                ClockTimes updateClock = mAdapter.getClockAtPosition(position);
                Intent updateIntent = new Intent(getApplicationContext(), SetAlarm.class);
                updateIntent.putExtra("HOUR_IN", updateClock.getmHour());
                updateIntent.putExtra("MINUTE_IN", updateClock.getmMinute());
                updateIntent.putExtra("AM_IN", updateClock.getmAm());
                updateIntent.putExtra("POSITION", position);
                startActivityForResult(updateIntent, REQUEST_CODE_EDIT_ALARM);
            }
        });

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                //delete any swiped alarms here
                alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent mTimeIntent = new Intent(getApplicationContext(), AlarmReciever.class);
                pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, mTimeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                alarmManager.cancel(pendingIntent);

                mAlarms.remove(viewHolder.getAdapterPosition());
                mAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        });
        helper.attachToRecyclerView(mRecyclerView);

        //alarm manager
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

    }

    public void updateAdapter(){
        mAdapter = new ClockAdapter(getApplicationContext(), mAlarms);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void setmAlarms(ClockTimes mClock_in){
        long time;
        Calendar calendar = Calendar.getInstance();
        int hourTest = Integer.valueOf(mClock_in.getmHour());
        int minuteTest = Integer.valueOf(mClock_in.getmMinute());
        Log.d("TIME", "hour "+hourTest+" MINUTE "+minuteTest);

        if (mClock_in.getmAm()) {
            int current_hour = Integer.valueOf(mClock_in.getmHour())-1;
            calendar.set(Calendar.HOUR_OF_DAY, current_hour);
        } else {
            int current_hour = Integer.valueOf(mClock_in.getmHour());
            current_hour += 12;
            calendar.set(Calendar.HOUR_OF_DAY, current_hour);
        }
        calendar.set(Calendar.MINUTE, Integer.valueOf(mClock_in.getmMinute()));

        Log.d("TIME", ""+calendar.get(Calendar.HOUR_OF_DAY));

        Intent mTimeIntent = new Intent(this, AlarmReciever.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, mTimeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        time=(calendar.getTimeInMillis()-(calendar.getTimeInMillis()%60000));
        if(System.currentTimeMillis()>time)
        {
            if (calendar.AM_PM == 0)
                time = time + (1000*60*60*12);
            else
                time = time + (1000*60*60*24);
        }

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 10000, pendingIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_NEW_ALARM && resultCode == RESULT_OK){
            ClockTimes newClock = new ClockTimes(data.getStringExtra("HOUR_TIME"), data.getStringExtra("MINUTE_TIME"), data.getBooleanExtra("AM_TIME", true));
            mAlarms.add(newClock);
            updateAdapter();
            setmAlarms(newClock);
            Snackbar.make(coordinatorLayout, "Alarm Set", Snackbar.LENGTH_LONG).show();
        }
        if (requestCode == REQUEST_CODE_EDIT_ALARM && resultCode == RESULT_OK){
            int position = data.getIntExtra("POSITION", -1);
            if (position != -1){
                ClockTimes temp = mAlarms.get(position);
                temp.setmHour(data.getStringExtra("HOUR_TIME"));
                temp.setmMinute(data.getStringExtra("MINUTE_TIME"));
                temp.setmAm(data.getBooleanExtra("AM_TIME", true));
                mAlarms.set(position, temp);
                updateAdapter();
                setmAlarms(temp);
                Snackbar.make(coordinatorLayout, "Alarm Set", Snackbar.LENGTH_LONG).show();
            } else {
                ClockTimes newClock = new ClockTimes(data.getStringExtra("HOUR_TIME"), data.getStringExtra("MINUTE_TIME"), data.getBooleanExtra("AM_TIME", true));
                mAlarms.add(newClock);
                updateAdapter();
                setmAlarms(newClock);
                Snackbar.make(coordinatorLayout, "Alarm Set", Snackbar.LENGTH_LONG).show();
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        //save activity state
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mAlarms);
        preferencesEditor.putString("ListAlarms", json);
        preferencesEditor.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (pendingIntent != null)
            alarmManager.cancel(pendingIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        alarmManager.cancel(pendingIntent);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
