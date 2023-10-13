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
import com.example.uhf_bt.InsertFacility;
import com.example.uhf_bt.InsertPremise;
import com.example.uhf_bt.MainActivity;
import com.example.uhf_bt.Models.Premise;
import com.example.uhf_bt.R;
import com.example.uhf_bt.SettingsOfUser;

import java.util.List;

public class SetUser extends Fragment {

    private Spinner spnFacility;
    private Spinner spnPremise;
    private Spinner spnUser;

    private Button setSettingsOfUser,
            setFacility,
            setPremise;

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

    private void init(View view) {
        // facility layout
        spnFacility = (Spinner) view.findViewById(R.id.spnFacility);
        setFacility = view.findViewById(R.id.btnSetFacility);
        // premise layout
        spnPremise = (Spinner) view.findViewById(R.id.spnPremise);
        setPremise = view.findViewById(R.id.btnSetPremise);
        // user layout
        setSettingsOfUser = view.findViewById(R.id.btnSetSettings);
        spnUser = (Spinner) view.findViewById(R.id.spnUser);
        db = new DataBase(getActivity());
        setSettingsOfUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SettingsOfUser.class);
                startActivity(intent);
            }
        });

        setFacility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(context, InsertFacility.class);
                startActivity(intent2);
            }
        });

        setPremise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(context, InsertPremise.class);
                startActivity(intent3);
            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = (MainActivity) getActivity();
    }

    public void fillSpinner() {
        List<String> facilities = db.getFacilities();
        ArrayAdapter<String> facilityAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, facilities);

        // Получаем выбранный элемент из спиннера spnFacility
        String selectedFacility = (String) spnFacility.getSelectedItem();

        if (selectedFacility != null) {
            // Убеждаемся, что строка не равна null, прежде чем преобразовывать ее в целое число
            int id = Integer.parseInt(selectedFacility);

            // Используйте id для дальнейших действий
            List<String> premises = db.getRoomsByObject(id);
            ArrayAdapter<String> premiseAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, premises);
            spnPremise.setAdapter(premiseAdapter);
        }

        List<String> executors = db.getUsers();
        ArrayAdapter<String> executorAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, executors);

        spnFacility.setAdapter(facilityAdapter);
        spnUser.setAdapter(executorAdapter);
    }


    @Override
    public void onResume() {
        super.onResume();
        fillSpinner();
    }
}