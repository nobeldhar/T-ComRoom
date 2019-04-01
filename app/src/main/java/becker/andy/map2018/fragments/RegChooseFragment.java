package becker.andy.map2018.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import becker.andy.map2018.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegChooseFragment extends Fragment {

    private TextView mStuReg;
    private TextView mTeachReg;

    public interface PerformRegFragmentListener {
        public void performStuReg();

        public void performTeachReg();
    }

    private PerformRegFragmentListener performRegFragmentListener;

    public RegChooseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reg_choose, container, false);

        mStuReg = view.findViewById(R.id.stu_reg);
        mTeachReg = view.findViewById(R.id.teach_reg);

        mStuReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performRegFragmentListener.performStuReg();
            }
        });
        mTeachReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performRegFragmentListener.performTeachReg();
            }
        });


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        performRegFragmentListener = (PerformRegFragmentListener) activity;
    }
}
