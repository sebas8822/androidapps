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
import com.finalproyect.niftydriverapp.ui.profile.ProfileFragment;

public class SettingsFragment extends Fragment {
    CallBackFragment callBackFragment;
    Button bt_changeParameterSet, bt_changePasswordSettings,bt_aboutAppSettings,bt_logoutSettings, bt_deleteUserSettings;
    Switch sw_themeDarkMode;
    ImageButton bt_changeImageSettings;
    ImageView im_profileSettings;
    TextView tv_currentName, tv_currentEmailSettings;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        bt_changeParameterSet = view.findViewById(R.id.bt_changeParameterSet);
        bt_changeParameterSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"Button change Parameters", Toast.LENGTH_LONG).show();


                getActivity().getSupportFragmentManager()
                        .beginTransaction().addToBackStack(null)
                        .add(R.id.fragment_container_settings, new ChangeParametersFragment())
                        .commit();
            }
        });




        return view;
    }

    public void setCallBackFragment(CallBackFragment callBackFragment){
        this.callBackFragment = callBackFragment;
    }





}
