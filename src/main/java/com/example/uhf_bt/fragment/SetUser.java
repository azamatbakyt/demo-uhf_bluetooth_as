package com.example.uhf_bt.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.uhf_bt.DataBase;
import com.example.uhf_bt.MainActivity;
import com.example.uhf_bt.R;
import com.example.uhf_bt.SettingsOfUser;

import java.util.List;

public class SetUser extends Fragment {

    private Spinner spnFacility;
    private Spinner spnPremise;
    private Spinner spnUser;

    private Button setSettings;

    MainActivity context;
    private DataBase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_set_user, container, false);
        init(view);

        fillSpinner();
        return view;
    }
    private void init(View view){
        // facility layout
        spnFacility = (Spinner) view.findViewById(R.id.spnFacility);
        // premise layout
        spnPremise = (Spinner) view.findViewById(R.id.spnPremise);
        // user layout
        setSettings = view.findViewById(R.id.btnSetSettings);
        spnUser = (Spinner) view.findViewById(R.id.spnUser);
        db = new DataBase(getActivity());
        setSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SettingsOfUser.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = (MainActivity) getActivity();
    }

    public void fillSpinner(){
        List<String> facilities = db.getFacilities();
        List<String> premises = db.getPremises();
        List<String> executors = db.getExecutors();

        ArrayAdapter<String> facilityAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, facilities);
        ArrayAdapter<String> premiseAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, premises);
        ArrayAdapter<String> executorAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, executors);

        spnFacility.setAdapter(facilityAdapter);
        spnPremise.setAdapter(premiseAdapter);
        spnUser.setAdapter(executorAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        fillSpinner();
    }
}