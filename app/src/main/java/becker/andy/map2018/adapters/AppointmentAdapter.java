package becker.andy.map2018.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import becker.andy.map2018.R;
import becker.andy.map2018.ShowAppointmentActivity;
import becker.andy.map2018.models.Appointment;
import becker.andy.map2018.models.Requests;

public class AppointmentAdapter  extends RecyclerView.Adapter<AppointmentAdapter.MyViewHolder>{
    List<Appointment> mAppointments;
    Context context;

    public AppointmentAdapter(List<Appointment> mUserlocations, Context context) {
        this.mAppointments = mUserlocations;
        this.context = context;
    }

    @NonNull
    @Override
    public AppointmentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=LayoutInflater.from(context).inflate(R.layout.appointment,viewGroup,false);
        return new AppointmentAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentAdapter.MyViewHolder myViewHolder, final int i) {

        myViewHolder.name.setText(mAppointments.get(i).getStudentName());
        myViewHolder.institute.setText(mAppointments.get(i).getInstitution());
        myViewHolder.dept.setText(mAppointments.get(i).getDepartment());
        myViewHolder.y_s.setText(mAppointments.get(i).getYear_Semester());
        myViewHolder.sub.setText(mAppointments.get(i).getSubject());
        myViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context,ShowAppointmentActivity.class)
                        .putExtra(context.getString(R.string.showAppointment_extra),mAppointments.get(i).getStudent_id()));
            }
        });


    }

    @Override
    public int getItemCount() {
        return mAppointments.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView institute;
        TextView dept;
        TextView y_s;
        TextView sub;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.appointment_name);
            institute=itemView.findViewById(R.id.appointment_institute);
            dept=itemView.findViewById(R.id.appointment_dept);
            y_s=itemView.findViewById(R.id.appointment_ys);
            sub=itemView.findViewById(R.id.appointment_sub);
            cardView=itemView.findViewById(R.id.appointment_model);

        }
    }
}
