package grahman.app.databaseapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String DATABASE_NAME = "student_db"; // constant capital letters

    TextView textViewViewStudents;
    EditText editTextName, editTextMark;
    Spinner spinnerDepartment;

    SQLiteDatabase mDatabase; // obeject

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         // link to the layout id names
        textViewViewStudents = (TextView) findViewById(R.id.textViewViewStudents);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextMark = (EditText) findViewById(R.id.editTextMark);
        spinnerDepartment = (Spinner) findViewById(R.id.spinnerDepartment);

        findViewById(R.id.buttonAddStudent).setOnClickListener(this);
        textViewViewStudents.setOnClickListener(this);

        //creating a database
        mDatabase = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        createStudentTable();
    }

    //this method will create the table
    //as we are going to call this method everytime we will launch the application
    //I have added IF NOT EXISTS to the SQL
    //so it will only create the table when the table is not already created
    private void createStudentTable() {
        mDatabase.execSQL(
                "CREATE TABLE IF NOT EXISTS students (\n" +
                        "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "    name TEXT NOT NULL,\n" +
                        "    department TEXT NOT NULL,\n" +
                        "    enroldate datetime NOT NULL,\n" +
                        "    mark double NOT NULL\n" +
                        ");"
        );
    }

    //this method will validate the name and mark
    //dept does not need validation as it is a spinner and it cannot be empty
    private boolean inputsAreCorrect(String name, String mark) {
        if (name.isEmpty()) {
            editTextName.setError("Please enter a name");
            editTextName.requestFocus();
            return false;
        }

        if (mark.isEmpty() || Integer.parseInt(mark) < 0|| Integer.parseInt(mark) > 100) {
            editTextMark.setError("Please enter a valid mark");
            editTextMark.requestFocus();
            return false;
        }
        return true;
    }

    //In this method we will do the create operation
    private void addStudent() {
        String name = editTextName.getText().toString().trim();
        String mark = editTextMark.getText().toString().trim();
        String dept = spinnerDepartment.getSelectedItem().toString();

        //getting the current time for enrolment date
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String enrolDate = sdf.format(cal.getTime());



        //validating the inptus
        if (inputsAreCorrect(name, mark)) {

            //using the same method execsql for inserting values
            //this time it has two parameters
            //first is the sql string and second is the parameters that is to be binded with the query
            ContentValues  values = new ContentValues( ); // create the string to push into the table into the database
            values.put("name",name);
            values.put("department",dept);
            values.put("enroldate",enrolDate);
            values.put("mark",mark);
            long stdid=mDatabase.insert("students","",values);

            Toast.makeText(this, "Student Added Successfully", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonAddStudent:

                addStudent();

                break;
            case R.id.textViewViewStudents:

                startActivity(new Intent(this, StudentActivity.class));

                break;
        }
    }
}
