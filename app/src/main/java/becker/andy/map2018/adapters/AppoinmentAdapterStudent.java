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

import java.util.List;

import becker.andy.map2018.R;
import becker.andy.map2018.ShowAppiontmentStudent;
import becker.andy.map2018.ShowAppointmentActivity;
import becker.andy.map2018.models.Appointment;

public class AppoinmentAdapterStudent extends RecyclerView.Adapter<AppoinmentAdapterStudent.MyViewHolder>{
    List<Appointment> mAppointments;
    Context context;

    public AppoinmentAdapterStudent(List<Appointment> mUserlocations, Context context) {
        this.mAppointments = mUserlocations;
        this.context = context;
    }

    @NonNull
    @Override
    public AppoinmentAdapterStudent.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=LayoutInflater.from(context).inflate(R.layout.appointment_student,viewGroup,false);
        return new AppoinmentAdapterStudent.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppoinmentAdapterStudent.MyViewHolder myViewHolder, final int i) {

        myViewHolder.name.setText(mAppointments.get(i).getTeacherName());
        myViewHolder.institute.setText(mAppointments.get(i).getInstitution());
        myViewHolder.dept.setText(mAppointments.get(i).getDepartment());
        myViewHolder.sub.setText(mAppointments.get(i).getSubject());
        myViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context,ShowAppiontmentStudent.class)
                        .putExtra(context.getString(R.string.appointment_extra_student),mAppointments.get(i).getTeacher_id()));
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
            name=itemView.findViewById(R.id.appointment_student_name);
            institute=itemView.findViewById(R.id.appointment_student_institute);
            dept=itemView.findViewById(R.id.appointment_student_dept);
            sub=itemView.findViewById(R.id.appointment_student_sub);
            cardView=itemView.findViewById(R.id.appointment_student_model);

        }
    }
}
