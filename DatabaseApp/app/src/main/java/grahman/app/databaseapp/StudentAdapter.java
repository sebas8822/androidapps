package grahman.app.databaseapp;
/**This manage the data in the screen view/ take the data and display in the way I want
 *
 * */



import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class StudentAdapter extends ArrayAdapter<Student> {

    Context mCtx;
    int listLayoutRes;
    List<Student> studentList;
    SQLiteDatabase mDatabase;

    public StudentAdapter(Context mCtx, int listLayoutRes, List<Student> studentList, SQLiteDatabase mDatabase) {
        super(mCtx, listLayoutRes, studentList);

        this.mCtx = mCtx;
        this.listLayoutRes = listLayoutRes;
        this.studentList = studentList;
        this.mDatabase = mDatabase;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(listLayoutRes, null);

        //getting employee of the specified position
        final Student student = studentList.get(position);


        //getting views
        TextView textViewName = view.findViewById(R.id.textViewName);
        TextView textViewDept = view.findViewById(R.id.textViewDepartment);
        TextView textViewMark = view.findViewById(R.id.textViewMark);
        TextView textViewEnrolDate = view.findViewById(R.id.textViewEnrolDate);

        //adding data to views
        textViewName.setText(student.getName());
        textViewDept.setText(student.getDept());
        textViewMark.setText(String.valueOf(student.getMark()));
        textViewEnrolDate.setText(student.getEnrolDate());

        //we will use these buttons later for update and delete operation
        Button buttonDelete = view.findViewById(R.id.buttonDeleteStudent);
        Button buttonEdit = view.findViewById(R.id.buttonEditStudent);
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateStudent(student);
            }
        });
        //the delete operation
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
                builder.setTitle("Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String sql = "DELETE FROM students WHERE id = ?";
                        mDatabase.execSQL(sql, new Integer[]{student.getId()});
                        reloadStudentsFromDatabase();
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

        return view;
    }

    private void updateStudent(final Student student) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);

        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.dialog_update_student, null);
        builder.setView(view);


        final EditText editTextName = view.findViewById(R.id.editTextName);
        final EditText editTextMark = view.findViewById(R.id.editTextMark);
        final Spinner spinnerDepartment = view.findViewById(R.id.spinnerDepartment);

        editTextName.setText(student.getName());
        editTextMark.setText(String.valueOf(student.getMark()));

        final AlertDialog dialog = builder.create();
        dialog.show();

        view.findViewById(R.id.buttonUpdateStudent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();
                String mark = editTextMark.getText().toString().trim();
                String dept = spinnerDepartment.getSelectedItem().toString();

                if (name.isEmpty()) {
                    editTextName.setError("Name can't be blank");
                    editTextName.requestFocus();
                    return;
                }

                if (mark.isEmpty()) {
                    editTextMark.setError("Mark can't be blank");
                    editTextMark.requestFocus();
                    return;
                }

                String sql = "UPDATE students \n" +
                        "SET name = ?, \n" +
                        "department = ?, \n" +
                        "mark = ? \n" +
                        "WHERE id = ?;\n";

                mDatabase.execSQL(sql, new String[]{name, dept, mark, String.valueOf(student.getId())});
                Toast.makeText(mCtx, "Student Updated", Toast.LENGTH_SHORT).show();
                reloadStudentsFromDatabase();

                dialog.dismiss();
            }
        });


    }

    private void reloadStudentsFromDatabase() {
        Cursor cursorStudents = mDatabase.rawQuery("SELECT * FROM students", null);
        if (cursorStudents.moveToFirst()) {
            studentList.clear();
            do {
                studentList.add(new Student(
                        cursorStudents.getInt(0),
                        cursorStudents.getString(1),
                        cursorStudents.getString(2),
                        cursorStudents.getString(3),
                        cursorStudents.getDouble(4)
                ));
            } while (cursorStudents.moveToNext());
        }
        cursorStudents.close();
        notifyDataSetChanged();
    }

}
