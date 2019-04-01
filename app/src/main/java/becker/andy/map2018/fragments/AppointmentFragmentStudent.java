package becker.andy.map2018.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import becker.andy.map2018.LoginActivity;
import becker.andy.map2018.R;
import becker.andy.map2018.adapters.AppoinmentAdapterStudent;
import becker.andy.map2018.models.Appointment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class AppointmentFragmentStudent extends Fragment {

    List<Appointment> mAppointments;
    RecyclerView mRecyclerView;


    public AppointmentFragmentStudent() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_appointment_fragment_student, container, false);
        mRecyclerView = view.findViewById(R.id.appointment_student_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        getAppointments();

        return view;
    }

    private void getAppointments() {
        int student_id = LoginActivity.prefConfig.readUserId();

        Call<List<Appointment>> call = LoginActivity.apiInterface.getAppointmentsStudent(student_id);
        call.enqueue(new Callback<List<Appointment>>() {
            @Override
            public void onResponse(Call<List<Appointment>> call, Response<List<Appointment>> response) {
                if (response.isSuccessful()) {
                    mAppointments = response.body();
                    if (mAppointments.size() > 0) {
                        setAdapter();
                    }

                }
            }

            @Override
            public void onFailure(Call<List<Appointment>> call, Throwable t) {
                Toast.makeText(getActivity(), "Database Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setAdapter() {
        if (mAppointments.size() > 0) {
            mRecyclerView.setVisibility(View.VISIBLE);
            AppoinmentAdapterStudent appoinmentAdapterStudent = new AppoinmentAdapterStudent(mAppointments, getActivity());
            mRecyclerView.setAdapter(appoinmentAdapterStudent);
        } else {
            mRecyclerView.setVisibility(View.GONE);
        }

    }

}
