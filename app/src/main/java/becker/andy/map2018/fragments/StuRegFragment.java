package becker.andy.map2018.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jgabrielfreitas.core.BlurImageView;

import becker.andy.map2018.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StuRegFragment extends Fragment {

    private BlurImageView blurImageView;
    private ImageView imageView;


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


        return view;
    }

}
