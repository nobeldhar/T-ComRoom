package becker.andy.map2018.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jgabrielfreitas.core.BlurImageView;

import becker.andy.map2018.LoginActivity;
import becker.andy.map2018.MainActivity;
import becker.andy.map2018.R;
import becker.andy.map2018.RegisterActivity;
import becker.andy.map2018.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeachRegFragment extends Fragment {

    private BlurImageView blurImageView;
    private ImageView imageView;
    //text_fields
    private EditText mEmail;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private EditText mUserName;
    private EditText mInstitution;
    private EditText mDepartment;
    private EditText mPhone;
    //buttons
    private Button mButtonReg;
    private ImageButton mHaveAcc;
    //progressbar
    private ProgressBar mProgressBar;

    public TeachRegFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_teach_reg, container, false);

        blurImageView=view.findViewById(R.id.bookBlurImageViewteach);
        blurImageView.setBlur(2);
        imageView=view.findViewById(R.id.white_imageteach);
        imageView.setAlpha(.7f);

        mEmail=view.findViewById(R.id.register_email_teach_reg);
        mPassword=view.findViewById(R.id.register_password_teach_reg);
        mConfirmPassword=view.findViewById(R.id.confirm_password_teach_reg);
        mUserName=view.findViewById(R.id.teach_register_name);
        mInstitution=view.findViewById(R.id.teach_register_insti);
        mDepartment=view.findViewById(R.id.teach_register_department);
        mPhone=view.findViewById(R.id.teach_register_phone);

        mButtonReg=view.findViewById(R.id.btn_teach_register);
        mHaveAcc=view.findViewById(R.id.btn_have_acc_teach);
        mHaveAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),LoginActivity.class));
            }
        });

        mProgressBar=view.findViewById(R.id.progressBarRegister_teach);

        mButtonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerTeacher();
            }
        });
        return  view;
    }

    private void registerTeacher() {
        mProgressBar.setVisibility(View.VISIBLE);
        String email=mEmail.getText().toString().trim();
        String pass=mPassword.getText().toString().trim();
        String conPass=mConfirmPassword.getText().toString().trim();
        String name=mUserName.getText().toString().trim();
        String insti=mInstitution.getText().toString().trim();
        String dept=mDepartment.getText().toString().trim();

        String phone=mPhone.getText().toString().trim();

        if(!pass.equals(conPass)){
            Toast.makeText(getActivity(),"Passwords Don't match", Toast.LENGTH_LONG).show();
            mProgressBar.setVisibility(View.GONE);
            return;
        }

        if(email.equals("") || pass.equals("") || conPass.equals("") || name.equals("") ||
                 insti.equals("") || dept.equals("") ||
                phone.equals("")){

            Toast.makeText(getActivity(),"Every Field Have to be Fullfilled",Toast.LENGTH_LONG).show();
            mProgressBar.setVisibility(View.GONE);
            return;
        }

        Call<User> call=RegisterActivity.apiInterface.performRegisterTeacher(email,pass,name,insti,dept,phone);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                mProgressBar.setVisibility(View.GONE);
                if(response.isSuccessful()){
                    if(response.body().getResponse().equals("ok")){

                        RegisterActivity.prefConfig.writeLoginStatus(true);
                        RegisterActivity.prefConfig.writeUserId(response.body().getUserId());
                        RegisterActivity.prefConfig.writeEmail(response.body().getEmail());
                        RegisterActivity.prefConfig.writeUser(response.body().getUser());
                        RegisterActivity.prefConfig.writeInsti(response.body().getInstitution());
                        startActivity(new Intent(getActivity(),MainActivity.class));
                    }else {
                        Log.d(TAG, "onResponse: error");
                    }
                }else {
                    Toast.makeText(getActivity(),"Registration Failed",Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onResponse: failed");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(),t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

}
