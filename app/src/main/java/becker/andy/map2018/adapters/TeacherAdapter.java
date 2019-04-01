package becker.andy.map2018.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import becker.andy.map2018.R;
import becker.andy.map2018.RequestAppointmentActivity;
import becker.andy.map2018.SetAppointmentActivity;
import becker.andy.map2018.models.UserLocation;
import becker.andy.map2018.models.UserLocationStudent;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.MyViewHolder> {
    ArrayList<UserLocationStudent> mUserlocations;
    Context context;

    public TeacherAdapter(ArrayList<UserLocationStudent> mUserlocations, Context context) {
        this.mUserlocations = mUserlocations;
        this.context = context;
    }

    @NonNull
    @Override
    public TeacherAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.teacher, viewGroup, false);
        return new TeacherAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherAdapter.MyViewHolder myViewHolder, final int i) {

        myViewHolder.name.setText(mUserlocations.get(i).getTeacher().getTeacherName());
        myViewHolder.institute.setText(mUserlocations.get(i).getTeacher().getInstitution());
        myViewHolder.dept.setText(mUserlocations.get(i).getTeacher().getDepartment());
        myViewHolder.set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserLocationStudent u = mUserlocations.get(i);
                Intent intent = new Intent(context, RequestAppointmentActivity.class);
                intent.putExtra(context.getString(R.string.teacher_id_intent_extra), u.getTeacher().getTeacher_id());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUserlocations.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView institute;
        TextView dept;

        Button set;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.teacher_name);
            institute = itemView.findViewById(R.id.teacher_institute);
            dept = itemView.findViewById(R.id.teacher_dept);
            set = itemView.findViewById(R.id.teacher_Set_appointment);

        }
    }
}
