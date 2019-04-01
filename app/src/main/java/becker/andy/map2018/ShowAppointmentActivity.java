package becker.andy.map2018;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import becker.andy.map2018.models.Appointment;
import becker.andy.map2018.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowAppointmentActivity extends AppCompatActivity {


    private TextView mStudentName;
    private TextView mDepartment;
    private TextView mRegistration;
    private TextView mYearSemester;
    private TextView mInstitution;
    private TextView mSubject;
    private TextView mDescription;
    private TextView mMessage;
    private TextView mDate;
    private TextView mTime;
    private Button mSuccesfulAppointmentButton;
    private ProgressBar mProgressBar;
    private List<Appointment> mAppointments;
    Appointment mAppointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_appointment);

        final int student_id = (int) getIntent().getExtras().get(getString(R.string.showAppointment_extra));

        mProgressBar = findViewById(R.id.progressBarShowAppointmetn);
        mProgressBar.setVisibility(View.VISIBLE);
        mStudentName = findViewById(R.id.showappointment_name);
        mRegistration = findViewById(R.id.showappointment_reg);
        mYearSemester = findViewById(R.id.showappointment_ys);
        mDepartment = findViewById(R.id.showappointment_dept);
        mInstitution = findViewById(R.id.showappointment_insti);
        mSubject = findViewById(R.id.showappointment_subject);
        mDescription = findViewById(R.id.showappointment_description);
        mMessage = findViewById(R.id.showappointment_message);
        mDate = findViewById(R.id.showappointment_date);
        mTime = findViewById(R.id.showappointment_time);
        mSuccesfulAppointmentButton = findViewById(R.id.btn_succesful_appointment);
        mSuccesfulAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAppointment();
            }
        });
        int teacher_id = LoginActivity.prefConfig.readUserId();
        Call<List<Appointment>> call = LoginActivity.apiInterface.getAppointments(teacher_id);
        call.enqueue(new Callback<List<Appointment>>() {
            @Override
            public void onResponse(Call<List<Appointment>> call, Response<List<Appointment>> response) {
                if (response.isSuccessful()) {
                    mAppointments = response.body();
                    for (Appointment appointment : mAppointments) {
                        if (appointment.getStudent_id() == student_id) {
                            mAppointment = appointment;
                            init();
                        }
                    }

                }
            }

            @Override
            public void onFailure(Call<List<Appointment>> call, Throwable t) {
                Toast.makeText(ShowAppointmentActivity.this, "Database Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void init() {
        mProgressBar.setVisibility(View.GONE);
        mStudentName.setText(mAppointment.getStudentName());
        mRegistration.setText(mAppointment.getReg());
        mYearSemester.setText(mAppointment.getYear_Semester());
        mDepartment.setText(mAppointment.getDepartment());
        mInstitution.setText(mAppointment.getInstitution());
        mSubject.setText(mAppointment.getSubject());
        mDescription.setText(mAppointment.getDescription());
        mMessage.setText(mAppointment.getMessage());
        mDate.setText(mAppointment.getDate());
        mTime.setText(mAppointment.getTime());
    }

    private void deleteAppointment() {
        mProgressBar.setVisibility(View.VISIBLE);
        int teacher_id = LoginActivity.prefConfig.readUserId();
        Call<User> call = LoginActivity.apiInterface.deleteAppointment(teacher_id, mAppointment.getStudent_id());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                mProgressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    if (response.body().getResponse().equals("ok")) {
                        startActivity(new Intent(ShowAppointmentActivity.this, MainActivity.class));
                        Toast.makeText(ShowAppointmentActivity.this, "Appointment removed", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ShowAppointmentActivity.this, "Appointment not removed", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                mProgressBar.setVisibility(View.VISIBLE);
                Toast.makeText(ShowAppointmentActivity.this, "Database Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
