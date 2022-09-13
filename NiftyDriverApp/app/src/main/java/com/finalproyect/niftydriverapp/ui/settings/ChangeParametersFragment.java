package com.finalproyect.niftydriverapp.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.finalproyect.niftydriverapp.CallBackFragment;
import com.finalproyect.niftydriverapp.R;

public class ChangeParametersFragment extends Fragment {
    CallBackFragment callBackFragment;
    Button bt_UpdateValuesChParameters;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_para, container, false);
        bt_UpdateValuesChParameters = view.findViewById(R.id.bt_UpdateValuesChParameters);
        bt_UpdateValuesChParameters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"Update Values", Toast.LENGTH_LONG).show();

            }
        });




        return view;
    }




}