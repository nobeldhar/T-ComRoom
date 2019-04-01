package becker.andy.map2018.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import becker.andy.map2018.R;
import becker.andy.map2018.adapters.RequestsAdapter;
import becker.andy.map2018.models.UserLocation;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    ArrayList<UserLocation> mUserlocations = new ArrayList<>();
    RecyclerView mRecyclerView;

    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_requests, container, false);
        mRecyclerView = view.findViewById(R.id.request_recycler);
        mUserlocations = getArguments().getParcelableArrayList(getString(R.string.userlocations_array));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RequestsAdapter requestsAdapter = new RequestsAdapter(mUserlocations, getActivity());
        mRecyclerView.setAdapter(requestsAdapter);
        return view;
    }

}
