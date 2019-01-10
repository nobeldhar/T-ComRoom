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
import becker.andy.map2018.SetAppointmentActivity;
import becker.andy.map2018.models.Requests;
import becker.andy.map2018.models.UserLocation;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.MyViewHolder> {

    ArrayList<UserLocation> mUserlocations;
    Context context;

    public RequestsAdapter(ArrayList<UserLocation> mUserlocations, Context context) {
        this.mUserlocations = mUserlocations;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=LayoutInflater.from(context).inflate(R.layout.request,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {

        myViewHolder.name.setText(mUserlocations.get(i).getRequests().getStudentName());
        myViewHolder.institute.setText(mUserlocations.get(i).getRequests().getInstitution());
        myViewHolder.dept.setText(mUserlocations.get(i).getRequests().getDepartment());
        myViewHolder.y_s.setText(mUserlocations.get(i).getRequests().getYear_Semester());
        myViewHolder.sub.setText(mUserlocations.get(i).getRequests().getSubject());
        myViewHolder.set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserLocation u= mUserlocations.get(i);
                Intent intent=new Intent(context,SetAppointmentActivity.class);
                intent.putExtra(context.getString(R.string.studentId_intent_extra),u.getRequests().getStudent_id());
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
        TextView y_s;
        TextView sub;
        Button set;
        Button delete;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.request_name);
            institute=itemView.findViewById(R.id.request_institute);
            dept=itemView.findViewById(R.id.request_dept);
            y_s=itemView.findViewById(R.id.request_ys);
            sub=itemView.findViewById(R.id.request_sub);
            set=itemView.findViewById(R.id.request_Set_appointment);
            delete=itemView.findViewById(R.id.request_delete);
        }
    }
}
