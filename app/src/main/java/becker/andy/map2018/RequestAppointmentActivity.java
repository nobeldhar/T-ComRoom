package becker.andy.map2018;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import becker.andy.map2018.models.Teacher;
import becker.andy.map2018.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestAppointmentActivity extends AppCompatActivity {

    Teacher mTeacher;

    //widgets
    private TextView mStudentName;
    private TextView mDepartment;
    private TextView mInstitution;
    private EditText mSubject;
    private EditText mDescription;
    private Button mButtonRequestAppoinment;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_appointment);

        mProgressBar = findViewById(R.id.progressBar_request);
        mProgressBar.setVisibility(View.VISIBLE);
        mStudentName = findViewById(R.id.appointment_request_name);
        mDepartment = findViewById(R.id.appointment_request_dept);
        mInstitution = findViewById(R.id.appointment_request_insti);
        mSubject = findViewById(R.id.appointment_request_subject);
        mDescription = findViewById(R.id.appointment_request_description);
        mButtonRequestAppoinment = findViewById(R.id.btn_appointment_request);
        mButtonRequestAppoinment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestAppointment();
            }
        });

        final int teacher_id = (int) getIntent().getExtras().get(getString(R.string.teacher_id_intent_extra));

        String institution = LoginActivity.prefConfig.readInsti();

        Call<List<Teacher>> call = LoginActivity.apiInterface.getTeachers(institution);
        call.enqueue(new Callback<List<Teacher>>() {
            @Override
            public void onResponse(Call<List<Teacher>> call, Response<List<Teacher>> response) {
                if (response.isSuccessful()) {
                    List<Teacher> teacherList = response.body();
                    for (Teacher teacher : teacherList) {
                        if (teacher.getTeacher_id() == teacher_id) {
                            mTeacher = teacher;
                            init();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Teacher>> call, Throwable t) {
                Toast.makeText(RequestAppointmentActivity.this, "Database Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void requestAppointment() {
        mProgressBar.setVisibility(View.VISIBLE);
        int student_id = LoginActivity.prefConfig.readUserId();
        int teacher_id = mTeacher.getTeacher_id();
        String subject = mSubject.getText().toString().trim();
        String description = mSubject.getText().toString().trim();
        if (subject.equals("")) {
            mProgressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Have to give a subject at least!", Toast.LENGTH_LONG).show();
            return;
        }

        Call<User> call = LoginActivity.apiInterface.requestAppointment(teacher_id, student_id, subject, description);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    if (response.body().getResponse().equals("ok")) {
                        Toast.makeText(RequestAppointmentActivity.this, "Request sent!", Toast.LENGTH_LONG).show();
                        finish();
                        startActivity(new Intent(RequestAppointmentActivity.this, MainActivityStudent.class));
                    } else {
                        Toast.makeText(RequestAppointmentActivity.this, "Database Error: Request not sent", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(RequestAppointmentActivity.this, "Database Error: Request not sent", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(RequestAppointmentActivity.this, "Database Error: Request not sent", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void init() {
        mProgressBar.setVisibility(View.GONE);
        mStudentName.setText(mTeacher.getTeacherName());
        mDepartment.setText(mTeacher.getDepartment());
        mInstitution.setText(mTeacher.getInstitution());

    }
}
