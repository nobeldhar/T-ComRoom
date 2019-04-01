package becker.andy.map2018;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import becker.andy.map2018.models.Appointment;
import becker.andy.map2018.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowAppiontmentStudent extends AppCompatActivity {

    private TextView mStudentName;
    private TextView mDepartment;
    private TextView mInstitution;
    private TextView mSubject;
    private TextView mDescription;
    private TextView mMessage;
    private TextView mDate;
    private TextView mTime;
    private Button mDeleteButton;
    private ProgressBar mProgressBar;
    private List<Appointment> mAppointments;
    Appointment mAppointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_appiontment_student);

        final int teacher_id = (int) getIntent().getExtras().get(getString(R.string.appointment_extra_student));

        mProgressBar = findViewById(R.id.progressBarShowAppointmetn_student);
        mProgressBar.setVisibility(View.VISIBLE);
        mStudentName = findViewById(R.id.showappointment_student_name);
        mDepartment = findViewById(R.id.showappointment_student_dept);
        mInstitution = findViewById(R.id.showappointment_student_insti);
        mSubject = findViewById(R.id.showappointment_student_subject);
        mDescription = findViewById(R.id.showappointment_student_description);
        mMessage = findViewById(R.id.showappointment_student_message);
        mDate = findViewById(R.id.showappointment_student_date);
        mTime = findViewById(R.id.showappointment_student_time);
        mDeleteButton = findViewById(R.id.btn_succesful_appointment_student);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });
        int student_id = LoginActivity.prefConfig.readUserId();

        Call<List<Appointment>> call = LoginActivity.apiInterface.getAppointmentsStudent(student_id);
        call.enqueue(new Callback<List<Appointment>>() {
            @Override
            public void onResponse(Call<List<Appointment>> call, Response<List<Appointment>> response) {
                if (response.isSuccessful()) {
                    mAppointments = response.body();
                    for (Appointment appointment : mAppointments) {
                        if (appointment.getTeacher_id() == teacher_id) {
                            mAppointment = appointment;
                            inti();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Appointment>> call, Throwable t) {
                Toast.makeText(ShowAppiontmentStudent.this, "Database Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void inti() {
        mProgressBar.setVisibility(View.GONE);
        mStudentName.setText(mAppointment.getTeacherName());
        mDepartment.setText(mAppointment.getDepartment());
        mInstitution.setText(mAppointment.getInstitution());
        mSubject.setText(mAppointment.getSubject());
        mDescription.setText(mAppointment.getDescription());
        mMessage.setText(mAppointment.getMessage());
        mDate.setText(mAppointment.getDate());
        mTime.setText(mAppointment.getTime());
    }

    private void delete() {
        mProgressBar.setVisibility(View.VISIBLE);
        int student_id = LoginActivity.prefConfig.readUserId();
        Call<User> call = LoginActivity.apiInterface.deleteAppointment(mAppointment.getTeacher_id(), student_id);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                mProgressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    mProgressBar.setVisibility(View.GONE);
                    if (response.body().getResponse().equals("ok")) {
                        startActivity(new Intent(ShowAppiontmentStudent.this, MainActivityStudent.class));
                        Toast.makeText(ShowAppiontmentStudent.this, "Appointment removed", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ShowAppiontmentStudent.this, "Appointment not removed", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(ShowAppiontmentStudent.this, "Database Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
