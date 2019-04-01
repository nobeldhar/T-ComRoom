package becker.andy.map2018.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import becker.andy.map2018.LoginActivity;
import becker.andy.map2018.R;
import becker.andy.map2018.adapters.AppointmentAdapter;
import becker.andy.map2018.models.Appointment;
import becker.andy.map2018.models.Requests;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class AppointmentsFragment extends Fragment {

    List<Appointment> mAppointments;
    RecyclerView mRecyclerView;


    public AppointmentsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_appointments, container, false);
        mRecyclerView = view.findViewById(R.id.appointment_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        getAppointments();

        return view;
    }

    private void getAppointments() {
        int teacher_id = LoginActivity.prefConfig.readUserId();
        Call<List<Appointment>> call = LoginActivity.apiInterface.getAppointments(teacher_id);
        call.enqueue(new Callback<List<Appointment>>() {
            @Override
            public void onResponse(Call<List<Appointment>> call, Response<List<Appointment>> response) {
                if (response.isSuccessful()) {
                    mAppointments = response.body();
                    setAdapter();
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
            AppointmentAdapter appointmentAdapter = new AppointmentAdapter(mAppointments, getActivity());
            mRecyclerView.setAdapter(appointmentAdapter);
        } else {
            mRecyclerView.setVisibility(View.GONE);
        }

    }

}
