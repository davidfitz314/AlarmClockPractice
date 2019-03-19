package com.example.alarmclockpractice;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class SetAlarm extends AppCompatActivity {
    private EditText hourText;
    private EditText minuteText;
    private String mHourTime;
    private String mMinuteTime;
    private Boolean mAmTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);
        hourText = findViewById(R.id.hour_input_view);
        minuteText = findViewById(R.id.minute_input_view);
        mHourTime = hourText.getText().toString();
        mMinuteTime = minuteText.getText().toString();

        if (getIntent().getStringExtra("HOUR_IN") != null){
            hourText.setText(getIntent().getStringExtra("HOUR_IN"));
            minuteText.setText(getIntent().getStringExtra("MINUTE_IN"));
            Boolean tempAm = getIntent().getBooleanExtra("AM_IN", true);
            if (!tempAm){
                mAmTime = false;
                RadioButton amRadio = (RadioButton) findViewById(R.id.pm_choice);
                amRadio.setChecked(true);
            }
        }
    }

    public void amPmChoice(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()){
            case R.id.am_choice:
                if (checked) {
                    mAmTime = true;
                }
                break;
            case R.id.pm_choice:
                if (checked) {
                    mAmTime = false;
                }
                break;
        }
    }

    public void saveNewAlarm(View view) {
        mHourTime = hourText.getText().toString();
        mMinuteTime = minuteText.getText().toString();
        if (Integer.valueOf(mHourTime) > 12 || Integer.valueOf(mHourTime) < 1){
            Toast.makeText(getApplicationContext(), "Error: 'hour' value is out of range: "+mHourTime, Toast.LENGTH_SHORT).show();
        } else if (Integer.valueOf(mMinuteTime) > 59 || Integer.valueOf(mMinuteTime) < 0){
            Toast.makeText(getApplicationContext(), "Error: 'Minute' value is out of range: "+mMinuteTime, Toast.LENGTH_SHORT).show();
        } else {
            if (getIntent().getStringExtra("HOUR_IN")!=null){
                mHourTime = hourText.getText().toString();
                mMinuteTime = minuteText.getText().toString();
                getIntent().putExtra("HOUR_TIME", mHourTime);
                getIntent().putExtra("MINUTE_TIME", mMinuteTime);
                getIntent().putExtra("AM_TIME", mAmTime);
                //getIntent().putExtra("POSITION", getIntent().getIntExtra("POSITION", -1));
                setResult(Activity.RESULT_OK, getIntent());
                finish();

            } else {
                mHourTime = hourText.getText().toString();
                mMinuteTime = minuteText.getText().toString();
                //check the times to verify they are within the prespecified range
                //finish and store results in clock times object and return to previous screen.
                Intent resultIntent = new Intent();
                resultIntent.putExtra("HOUR_TIME", mHourTime);
                resultIntent.putExtra("MINUTE_TIME", mMinuteTime);
                resultIntent.putExtra("AM_TIME", mAmTime);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        }
    }

    public void cancelAlarm(View view) {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

}
