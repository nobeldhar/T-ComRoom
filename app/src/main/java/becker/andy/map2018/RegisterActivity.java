package becker.andy.map2018;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import becker.andy.map2018.fragments.StuRegFragment;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.reg_fragment_container, new StuRegFragment()).commit();
    }
}
