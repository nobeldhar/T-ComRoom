package becker.andy.map2018;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import becker.andy.map2018.models.UserLocation;

public class SetAppointmentActivity extends AppCompatActivity {

    private static final String TAG = "SetAppointmentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_appointment);
        try {
            Intent i=getIntent();
            UserLocation u = (UserLocation) i.getSerializableExtra(getString(R.string.appointment_extra));
            TextView textView = findViewById(R.id.text_name);
            textView.setText(u.getUser().getUserName());
        }catch (NullPointerException n){
            Log.d(TAG, "onCreate: "+n.getMessage());
        }
    }
}
