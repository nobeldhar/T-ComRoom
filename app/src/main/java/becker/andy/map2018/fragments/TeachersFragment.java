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
import becker.andy.map2018.adapters.TeacherAdapter;
import becker.andy.map2018.models.UserLocation;
import becker.andy.map2018.models.UserLocationStudent;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeachersFragment extends Fragment {
    ArrayList<UserLocationStudent> mUserlocations =new ArrayList<>();
    RecyclerView mRecyclerView;
    TeacherAdapter adapter;

    public TeachersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_teachers, container, false);
        mRecyclerView=view.findViewById(R.id.teacher_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mUserlocations=getArguments().getParcelableArrayList(getString(R.string.userlocations_array));
        if(mUserlocations.size()>0){
            TeacherAdapter teacherAdapter=new TeacherAdapter(mUserlocations,getActivity());
            adapter=teacherAdapter;
        }

        mRecyclerView.setAdapter(adapter);
        return view;
    }

}
