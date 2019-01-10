package becker.andy.map2018;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import becker.andy.map2018.models.Requests;
import becker.andy.map2018.models.User;
import becker.andy.map2018.models.UserLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static becker.andy.map2018.R.string.appointment_extra;

public class SetAppointmentActivity extends AppCompatActivity {

    private static final String TAG = "SetAppointmentActivity";

    Requests mStudent;

    //widgets
    private TextView mStudentName;
    private TextView mDepartment;
    private TextView mRegistration;
    private TextView mYearSemester;
    private TextView mInstitution;
    private TextView mSubject;
    private TextView mDescription;
    private EditText mMessage;
    private EditText mDate;
    private EditText mTime;
    private Button mSetAppointment;
    private ProgressBar mProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_appointment);

        mProgressBar=findViewById(R.id.progressBarAppointmetn);
        mProgressBar.setVisibility(View.VISIBLE);
        mStudentName=findViewById(R.id.student_name);
        mRegistration=findViewById(R.id.student_reg);
        mYearSemester=findViewById(R.id.student_ys);
        mDepartment=findViewById(R.id.student_dept);
        mInstitution=findViewById(R.id.student_insti);
        mSubject=findViewById(R.id.student_subject);
        mDescription=findViewById(R.id.student_description);
        mMessage=findViewById(R.id.student_message);
        mDate=findViewById(R.id.student_date);
        mTime=findViewById(R.id.student_time);
        mSetAppointment=findViewById(R.id.btn_set_appointment);
        mSetAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAppointment();
            }
        });

        final int userId= (int) getIntent().getExtras().get(getString(R.string.studentId_intent_extra));

        Call<List<Requests>> call=LoginActivity.apiInterface.getRequests(LoginActivity.prefConfig.readUserId());
        call.enqueue(new Callback<List<Requests>>() {
            @Override
            public void onResponse(Call<List<Requests>> call, Response<List<Requests>> response) {
                if(response.isSuccessful()){
                    ArrayList<Requests> requests= (ArrayList<Requests>) response.body();
                    for(Requests request: requests){
                        if(request.getStudent_id()== userId){
                            mStudent=request;
                            init();
                            mProgressBar.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Requests>> call, Throwable t) {

                Toast.makeText(SetAppointmentActivity.this,"Database Connection Failed: "+t.getMessage(),Toast.LENGTH_LONG).show();

            }
        });

    }

    private void setAppointment() {
        mProgressBar.setVisibility(View.VISIBLE);
        String message=mMessage.getText().toString().trim();
        String date=mDate.getText().toString().trim();
        String time=mTime.getText().toString().trim();
        String subject=mStudent.getSubject();
        String description=mStudent.getDescription();
        if(date.equals("") || time.equals("")){
            mProgressBar.setVisibility(View.GONE);
            Toast.makeText(this,"Have to give a date and time", Toast.LENGTH_LONG).show();
            return;
        }
        int teacher_id=LoginActivity.prefConfig.readUserId();
        Call<User>call =LoginActivity.apiInterface.setAppointment(teacher_id,mStudent.getStudent_id(),
                subject,description,message,date,time);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.body().getResponse().equals("ok")){
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(SetAppointmentActivity.this,"Appointment has been set!",Toast.LENGTH_LONG).show();
                }else if(response.body().getResponse().equals("erro")){
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(SetAppointmentActivity.this,"Appointment has been set but request hasn't been deleted!",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(SetAppointmentActivity.this,"Appointment hasn't been set!",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(SetAppointmentActivity.this,"Database Error: "+t.getMessage(),Toast.LENGTH_LONG).show();

            }
        });


    }

    private void init() {
        mStudentName.setText(mStudent.getStudentName());
        mRegistration.setText(mStudent.getReg());
        mYearSemester.setText(mStudent.getYear_Semester());
        mDepartment.setText(mStudent.getDepartment());
        mInstitution.setText(mStudent.getInstitution());
        mSubject.setText(mStudent.getSubject());
        mDescription.setText(mStudent.getDescription());
    }


}
