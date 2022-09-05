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



    //int listLayoutRes;
    // determine the context

    public EmpListAdapter(Context context){
        this.context = context;
    }


    // I am saying put the list into this object

    public void setEmployeeList(List<Employee> employeeList) {
        this.employeeList = employeeList;

    }
    /**
     * Instantiates a layout XML file into its corresponding View objects. It is never used
     * directly. Instead, use Activity.getLayoutInflater() or Context#getSystemService to retrieve
     * a standard LayoutInflater instance that is already hooked up to the current context and
     * correctly configured for the device you are running on.
     *
     * To create a new LayoutInflater with an additional Factory for your own views, you can use
     * cloneInContext(Context) to clone an existing ViewFactory, and then call
     * setFactory(LayoutInflater.Factory) on it to include your Factory.
     *
     * For performance reasons, view inflation relies heavily on pre-processing of XML files
     * that is done at build time. Therefore, it is not currently possible to use LayoutInflater
     * with an XmlPullParser over a plain XML file at runtime; it only works with an XmlPullParser
     * returned from a compiled resource (R.something file.)*/
    // bring recycleview to the context
    @NonNull
    @Override
    public EmpListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_row,parent, false);

        return new MyViewHolder(view);
    }
    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the RecyclerView.ViewHolder.itemView to reflect the item at the
     * given position.
     *
     * Note that unlike ListView, RecyclerView will not call this method again if the position
     * of the item changes in the data set unless the item itself is invalidated or the new position
     * cannot be determined. For this reason, you should only use the position parameter while
     * acquiring the related data item inside this method and should not keep a copy of it.
     * If you need the position of an item later on (e.g. in a click listener),
     * use RecyclerView.ViewHolder.getBindingAdapterPosition() which will have the updated adapter
     * position. Override onBindViewHolder(ViewHolder, int, List) instead if Adapter can handle
     * efficient partial bind.*/

    //holder: VH: The ViewHolder which should be updated to represent the contents of the item
    // at the given position in the data set.
    //int position: The position of the item within the adapter's data set.
    @Override
    public void onBindViewHolder(@NonNull EmpListAdapter.MyViewHolder holder, int position) {

        //Obtaind specific employee to also cast to other methods
        Employee employeeFL = employeeList.get(position);
        int pot = position ;
        // get the info in determine position in the specific item in the employee array
        holder.tv_Name.setText(this.employeeList.get(position).getFirstName());
        holder.tv_LastN.setText(this.employeeList.get(position).lastName);
        holder.tv_EmployeeID.setText(this.employeeList.get(position).empId);
        holder.tv_Email.setText(this.employeeList.get(position).email);
        holder.tv_Address.setText(this.employeeList.get(position).empAddress);



        holder.bt_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateEmployee(employeeFL);
                //Toast.makeText(context.getApplicationContext(), view.getId()+"Edit Option", Toast.LENGTH_SHORT).show();
            }
        });
        // Delete specific employee
        holder.bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // remove from the database
                        deleteEmployeeRecord(employeeFL);
                        //remove from the list that is already charge in the adapter
                        employeeList.remove(pot);
                        notifyDataSetChanged();
                        //notifyDataSetChanged();
                        //notifyItemRemoved(pot);
                        //notifyItemRangeChanged(pot,1);
                        Toast.makeText(context.getApplicationContext(), "Employee "+employeeFL.getFirstName()+" deleted", Toast.LENGTH_SHORT).show();
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
        });



        holder.bt_showAddress.setOnClickListener(new View.OnClickListener() {


            /**An intent is an abstract description of an operation to be performed. It can be used
             *  with startActivity to launch an Activity, broadcastIntent to send it to any
             *  interested BroadcastReceiver components, and Context.startService(Intent)
             *  or Context.bindService(Intent, ServiceConnection, int) to communicate with
             *  a background Service.

             An Intent provides a facility for performing late runtime binding between the code
             in different applications. Its most significant use is in the launching of activities,
             where it can be thought of as the glue between activities. It is basically a passive
             data structure holding an abstract description of an action to be performed.
             * */



            @Override
            public void onClick(View view) {
                // Init intetnt


                Intent intent = new Intent(context, EmpViewMap.class);
                intent.putExtra("emp_address",employeeFL.empAddress);
                context.startActivity(intent);

                //View_DataBase.showAddressMap(position);
                //Toast.makeText(context.getApplicationContext(), employeeList.get(position).empAddress+"Show address button", Toast.LENGTH_SHORT).show();


            }
        });


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

    private void updateEmployee(Employee employee) {

        AppDatabase db = AppDatabase.getDbInstance(context);
        EmployeeDao dao = db.employeeDao();
        EmployeeRepository repo = new EmployeeRepositoryImpl(dao);


        // open Dialog
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
        editTextName.setText(employee.firstName);
        editTextLast.setText(employee.lastName);
        editTextempID.setText(employee.empId);
        editTextEmail.setText(employee.email);
        editTextEmploAdd.setText(employee.empAddress);

        // show activity
        final AlertDialog dialog = builder.create();
        dialog.show();
        //Toast.makeText(context.getApplicationContext(), "before click", Toast.LENGTH_SHORT).show();



        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context.getApplicationContext(), "you are here", Toast.LENGTH_SHORT).show();
                // Variables declaration
                String firstName, lastName, email, empId, empAddress;

                // conversion to string inputs
                firstName = editTextName.getText().toString();
                lastName = editTextLast.getText().toString();
                empId = editTextempID.getText().toString();
                email = editTextEmail.getText().toString();
                empAddress = editTextEmploAdd.getText().toString();

                // Data entry Validation
                // to use the data validation from MainActivity I change the methods to static but the Toast method is not able to get the context
                // so to obtain the current context was necessary to create a class that makes the context static across the application
                //Toast.makeText(context.getApplicationContext(), MainActivity.validName(firstName,lastName)+ "you are here", Toast.LENGTH_SHORT).show();

                if (MainActivity.validName(firstName,lastName)==true && MainActivity.validEmpId(empId)==true && MainActivity.validEmail(email)== true && MainActivity.validEmployeeAddress(empAddress)== true) {
                    // Update values into the data base
                    employee.setFirstName(firstName);
                    employee.setLastName(lastName);
                    employee.setEmpId(empId);
                    employee.setEmail(email);
                    employee.setEmpAddress(empAddress);

                    dao.updateEmp(employee);
                    Toast.makeText(context.getApplicationContext(), "Data Updated", Toast.LENGTH_SHORT).show();
                    //update values without recreat the whole activity
                    notifyDataSetChanged();
                    //recreateApp((AppCompatActivity) context);
                    dialog.dismiss();

                }else{
                    Toast.makeText(context.getApplicationContext(), "Please check data ", Toast.LENGTH_SHORT).show();

                }




            }
        });
    }
    public void deleteEmployeeRecord(Employee employee){
        // Init DB call specific employee to be deleted
        AppDatabase db = AppDatabase.getDbInstance(context);
        EmployeeDao dao = db.employeeDao();
        //EmployeeRepository repo = new EmployeeRepositoryImpl();


        //getting employee of the specified position

        dao.delete(employee);

        //update values without recreat the whole activity

        // recreate again the activity
        //recreateApp((AppCompatActivity) context);


    }

    public void finishapp(AppCompatActivity appCompatActivity) {
        appCompatActivity.finish();

    }
    public void recreateApp(AppCompatActivity appCompatActivity) {
        appCompatActivity.recreate();

    }



}
