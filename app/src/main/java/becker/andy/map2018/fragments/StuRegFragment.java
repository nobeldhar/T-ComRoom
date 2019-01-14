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
import becker.andy.map2018.MainActivityStudent;
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
public class StuRegFragment extends Fragment {

    private BlurImageView blurImageView;
    private ImageView imageView;
    //text_fields
    private EditText mEmail;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private EditText mUserName;
    private EditText mInstitution;
    private EditText mRegNo;
    private EditText mDepartment;
    private EditText mYear_Semester;
    private EditText mPhone;
    //buttons
    private Button mButtonReg;
    private ImageButton mHaveAcc;
    //progressbar
    private ProgressBar mProgressBar;


    public StuRegFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_stu_reg, container, false);
        blurImageView=view.findViewById(R.id.bookBlurImageViewUp);
        blurImageView.setBlur(2);
        imageView=view.findViewById(R.id.white_imageUp);
        imageView.setAlpha(.7f);

        mEmail=view.findViewById(R.id.register_email_stu_reg);
        mPassword=view.findViewById(R.id.register_password_stu_reg);
        mConfirmPassword=view.findViewById(R.id.confirm_password_stu_reg);
        mUserName=view.findViewById(R.id.stu_register_name);
        mInstitution=view.findViewById(R.id.stu_register_insti);
        mRegNo=view.findViewById(R.id.stu_register_reg);
        mDepartment=view.findViewById(R.id.stu_register_department);
        mYear_Semester=view.findViewById(R.id.stu_register_year);
        mPhone=view.findViewById(R.id.stu_register_phone);

        mButtonReg=view.findViewById(R.id.btn_stu_register);
        mHaveAcc=view.findViewById(R.id.btn_have_acc);
        mHaveAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),LoginActivity.class));
            }
        });

        mProgressBar=view.findViewById(R.id.progressBarRegister_stu);

        mButtonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerStudent();
            }
        });


        return view;
    }

    private void registerStudent() {
        mProgressBar.setVisibility(View.VISIBLE);
        String email=mEmail.getText().toString().trim();
        String pass=mPassword.getText().toString().trim();
        String conPass=mConfirmPassword.getText().toString().trim();
        String name=mUserName.getText().toString().trim();
        final String reg=mRegNo.getText().toString().trim();
        String insti=mInstitution.getText().toString().trim();
        String dept=mDepartment.getText().toString().trim();
        String year=mYear_Semester.getText().toString().trim();
        String phone=mPhone.getText().toString().trim();

        if(!pass.equals(conPass)){
            Toast.makeText(getActivity(),"Passwords Don't match", Toast.LENGTH_LONG).show();
            return;
        }

        if(email.equals("") || pass.equals("") || conPass.equals("") || name.equals("") ||
                reg.equals("") || insti.equals("") || dept.equals("") || year.equals("") ||
                phone.equals("")){

            Toast.makeText(getActivity(),"Every Field Have to be Fullfilled",Toast.LENGTH_LONG).show();
            return;
        }

        Call<User> call=LoginActivity.apiInterface.performRegisterStudent(email,pass,name,insti,dept,reg,year,phone);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                mProgressBar.setVisibility(View.GONE);
                if(response.isSuccessful()){
                    if(response.body().getResponse().equals("ok")){

                        LoginActivity.prefConfig.writeLoginStatus(true);
                        LoginActivity.prefConfig.writeUserId(response.body().getUserId());
                        LoginActivity.prefConfig.writeEmail(response.body().getEmail());
                        LoginActivity.prefConfig.writeUser(response.body().getUser());
                        LoginActivity.prefConfig.writeInsti(response.body().getInstitution());
                        finish();
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

    private void finish() {
        finish();
        startActivity(new Intent(getActivity(),MainActivityStudent.class));

    }

}
