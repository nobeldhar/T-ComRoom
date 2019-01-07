package becker.andy.map2018;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import becker.andy.map2018.models.UserLocation;

import static becker.andy.map2018.R.string.appointment_extra;

public class SetAppointmentActivity extends AppCompatActivity {

    private static final String TAG = "SetAppointmentActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_appointment);
        try {
            Intent i=getIntent();
            UserLocation u =i.getParcelableExtra(getString(R.string.appointment_extra));
            TextView textView = findViewById(R.id.student_name);
            textView.setText(u.getRequests().getStudentName());
        }catch (NullPointerException n){
            Log.d(TAG, "onCreate: "+n.getMessage());
        }

    }
}
