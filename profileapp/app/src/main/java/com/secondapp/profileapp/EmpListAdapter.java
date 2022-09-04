package com.secondapp.profileapp;
/** An Adapter object acts as a bridge between an AdapterView and the
 * underlying data for that view. The Adapter provides access to the data items.
 * The Adapter is also responsible for making a View for each item in the data set.
 * */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.MenuItem;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.secondapp.profileapp.db.AppDatabase;
import com.secondapp.profileapp.db.Employee;
import com.secondapp.profileapp.db.EmployeeDao;

import java.util.List;

public class EmpListAdapter extends RecyclerView.Adapter<EmpListAdapter.MyViewHolder>  {

    private Context context;
    private List<Employee> employeeList;
    private Object EmpViewMap;


    //int listLayoutRes;

    public EmpListAdapter(Context context){
        this.context = context;
    }


    // I am saying put the list into this object

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



        holder.bt_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateStudent(position);
                Toast.makeText(context.getApplicationContext(), view.getId()+"Edit Opotion", Toast.LENGTH_SHORT).show();
            }
        });

        holder.bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // i have to red first what we have here // next find it in the data base // and deleted
                deleteEmployeeRecord(position);
                Toast.makeText(context.getApplicationContext(), view.getId()+"Delete Opotion", Toast.LENGTH_SHORT).show();
            }
        });

        holder.bt_showAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, EmpViewMap.class);
                intent.putExtra("emp_address",employeeList.get(position).empAddress);
                context.startActivity(intent);

                //View_DataBase.showAddressMap(position);
                Toast.makeText(context.getApplicationContext(), employeeList.get(position).empAddress+"Show address button", Toast.LENGTH_SHORT).show();


            }
        });


    }

    private void showAddressMap(int position) {


        /**
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.emp_address, null);
        builder.setView(view);
        // show activity
        final Employee employeeUpdate = employeeList.get(position);
        final AlertDialog dialog = builder.create();
        //String Emp_addressFDB = employeeList.get(position).empAddress;
        //final MapView mapView = view.findViewById(R.id.map);


        //EmpViewMap.setAddress(Emp_addressFDB);
        dialog.show();
        //dialog.dismiss();*/
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
        ImageView bt_edit;
        ImageView bt_delete;
        ImageView bt_showAddress;
        public MyViewHolder(View view){
            super(view);
            tv_Name = view.findViewById(R.id.tv_Name);
            tv_LastN = view.findViewById(R.id.tv_LastN);
            tv_EmployeeID = view.findViewById(R.id.tv_EmployeeID);
            tv_Email = view.findViewById(R.id.tv_Email);
            tv_Address = view.findViewById(R.id.tv_Address);
            bt_edit = view.findViewById(R.id.bt_edit);
            bt_delete = view.findViewById(R.id.bt_delete);
            bt_showAddress = view.findViewById(R.id.bt_showAddress);





        }

    }

    private void updateStudent(int position) {

        AppDatabase db = AppDatabase.getDbInstance(context);
        EmployeeDao dao = db.employeeDao();
        EmployeeRepository repo = new EmployeeRepositoryImpl(dao);
        final Employee employeeUpdate = employeeList.get(position);

        // open new activity
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_update_emp, null);
        builder.setView(view);

        //bring edit text
        final EditText editTextName = view.findViewById(R.id.et_firstName);
        final EditText editTextLast = view.findViewById(R.id.et_lastName);
        final EditText editTextempID = view.findViewById(R.id.et_empId);
        final EditText editTextEmail = view.findViewById(R.id.et_email);
        final EditText editTextEmploAdd = view.findViewById(R.id.et_address);
        final Button buttonUpdate = view.findViewById(R.id.bt_updateButton);



        //set current edit text with current values on current position
        editTextName.setText(employeeList.get(position).firstName);
        editTextLast.setText(employeeList.get(position).lastName);
        editTextempID.setText(employeeList.get(position).empId);
        editTextEmail.setText(employeeList.get(position).email);
        editTextEmploAdd.setText(employeeList.get(position).empAddress);

        // show activity
        final AlertDialog dialog = builder.create();
        dialog.show();
        Toast.makeText(context.getApplicationContext(), "before click", Toast.LENGTH_SHORT).show();



        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context.getApplicationContext(), "you are here", Toast.LENGTH_SHORT).show();
                // Variables declaration
                String firstName, lastName, email, empId, empAddress;

                // conversion to string inputs
                firstName = editTextName.getText().toString();
                lastName = editTextLast.getText().toString();
                empId = editTextempID.getText().toString();
                email = editTextEmail.getText().toString();
                empAddress = editTextEmploAdd.getText().toString();

                //validation code isempty data is correct


                // call and update employee in the database
                employeeUpdate.setFirstName(firstName);
                employeeUpdate.setLastName(lastName);
                employeeUpdate.setEmpId(empId);
                employeeUpdate.setEmail(email);
                employeeUpdate.setEmpAddress(empAddress);

                repo.updateEmployee(employeeUpdate);
                recreateApp((AppCompatActivity) context);
                dialog.dismiss();
            }
        });
    }
    public void deleteEmployeeRecord(int position){
        AppDatabase db = AppDatabase.getDbInstance(context);
        EmployeeDao dao = db.employeeDao();
        EmployeeRepository repo = new EmployeeRepositoryImpl(dao);
        /**LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(listLayoutRes, null);*/

        //getting employee of the specified position
        final Employee employeeDelete = employeeList.get(position);
        repo.deleteEmployee(employeeDelete);
        recreateApp((AppCompatActivity) context);

        /**Button buttonDelete = view.findViewById(R.id.bt_delete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(context.getApplicationContext(), "you did it", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });*/

    }

    public void finishapp(AppCompatActivity appCompatActivity) {
        appCompatActivity.finish();

    }
    public void recreateApp(AppCompatActivity appCompatActivity) {
        appCompatActivity.recreate();

    }



}
