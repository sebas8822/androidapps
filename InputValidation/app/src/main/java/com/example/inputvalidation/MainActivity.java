package com.example.inputvalidation;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private EditText std_name;
    private EditText std_mark;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        std_name=(EditText)findViewById(R.id.std_name);
        std_mark=(EditText)findViewById(R.id.std_mark);
    }
    public void display(View view)
    {
        String sname=std_name.getText().toString();
        String smark=std_mark.getText().toString();
        String msg="";
        boolean isInputValid=true;

        //input validation
        if(sname.equals("") && smark.equals(""))
        {
            msg="Both name and mark fields are empty. Please enter valid name and mark.";
            isInputValid=false;
        }
        else{
            if(sname.equals(""))
            {
                msg="Name is empty. Please enter a valid name.";
                isInputValid=false;
            }
            if(smark.equals(""))
            {
                msg="Mark is empty. Please enter a valid mark.";
                isInputValid=false;
            }
            else{
                int mark=Integer.parseInt(smark);
                if(mark<0 || mark>100){
                    msg="Mark is invalid. Please enter a valid mark.";
                    isInputValid=false;
                }
            }
        }
        //displaying message
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do nothing
            }
        });

        if(isInputValid) {
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //do nothing
                }
            });
            msg="Hi "+sname+", your mark is "+smark;
            builder.setMessage(msg).setTitle("Confirmation");
        }else{
            builder.setMessage(msg).setTitle("Warning...");
        }

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}

