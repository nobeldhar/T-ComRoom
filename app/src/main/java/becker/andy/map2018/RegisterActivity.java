package becker.andy.map2018;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import becker.andy.map2018.classes.PrefConfig;
import becker.andy.map2018.fragments.RegChooseFragment;
import becker.andy.map2018.fragments.StuRegFragment;
import becker.andy.map2018.fragments.TeachRegFragment;
import becker.andy.map2018.retrofit.ApiClient;
import becker.andy.map2018.retrofit.ApiInterface;

public class RegisterActivity extends AppCompatActivity implements RegChooseFragment.PerformRegFragmentListener {

    //preconfig
    public static PrefConfig prefConfig;
    public static ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        prefConfig=new PrefConfig(this);
        apiInterface=ApiClient.getApiClient().create(ApiInterface.class);


      getSupportFragmentManager().beginTransaction()
               .add(R.id.reg_fragment_container, new RegChooseFragment()).commit();

    }

    @Override
    public void performStuReg() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.reg_fragment_container, new StuRegFragment()).addToBackStack(null).commit();
    }

    @Override
    public void performTeachReg() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.reg_fragment_container, new TeachRegFragment()).addToBackStack(null).commit();

    }
}
