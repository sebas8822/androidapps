package com.secondapp.profileapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.secondapp.profileapp.db.Employee;

import java.util.List;

public class EmpListAdapter extends RecyclerView.Adapter<EmpListAdapter.MyViewHolder> {

    private Context context;
    private List<Employee> employeeList;

    public EmpListAdapter(Context context){
        this.context = context;
    }

    public void setEmployeeList(List<Employee> employeeList) {
        this.employeeList = employeeList;

    }



    @NonNull
    @Override
    public EmpListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_row,parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull EmpListAdapter.MyViewHolder holder, int position) {
        holder.tv_Name.setText(this.employeeList.get(position).firstName);
        holder.tv_LastN.setText(this.employeeList.get(position).lastName);
        holder.tv_EmployeeID.setText(this.employeeList.get(position).empId);
        holder.tv_Email.setText(this.employeeList.get(position).email);
        holder.tv_Address.setText(this.employeeList.get(position).empAddress);
    }

    @Override
    public int getItemCount() {
        return this.employeeList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_Name;
        TextView tv_LastN;
        TextView tv_EmployeeID;
        TextView tv_Email;
        TextView tv_Address;
        public MyViewHolder(View view){
            super(view);
            tv_Name = view.findViewById(R.id.tv_Name);
            tv_LastN = view.findViewById(R.id.tv_LastN);
            tv_EmployeeID = view.findViewById(R.id.tv_EmployeeID);
            tv_Email = view.findViewById(R.id.tv_Email);
            tv_Address = view.findViewById(R.id.tv_Address);

        }

    }
}
