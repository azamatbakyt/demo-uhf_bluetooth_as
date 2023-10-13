package com.example.uhf_bt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.uhf_bt.Models.Premise;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InsertPremise extends AppCompatActivity {

    private Spinner spnPremiseGet;
    private EditText setPremiseName;
    private EditText setPremiseNote;
    private Button save_premise;
    private DataBase db;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_premise);
        init();
    }

    private void init() {
        spnPremiseGet = findViewById(R.id.spnPremiseGet);
        setPremiseName = findViewById(R.id.setPremiseName);
        setPremiseNote = findViewById(R.id.setPremiseNote);
        db = new DataBase(this);
        List<String> facilities = db.getFacilities();
        save_premise = findViewById(R.id.save_premise);
        ArrayAdapter<String> facilitites = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, facilities);
        spnPremiseGet.setAdapter(facilitites);
        save_premise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Premise premise = new Premise();
                String selectedFacilityWithId = spnPremiseGet.getSelectedItem().toString();
                String[] parts = selectedFacilityWithId.split(" - ");

                if (parts.length == 2) {
                    int facilityId = Integer.parseInt(parts[1]);
                    premise.setId(facilityId);
                    premise.setName(setPremiseName.getText().toString());
                    premise.setNote(setPremiseNote.getText().toString());
                    if (db.insertPremise(premise) && db.insertObjectRoomRelation(facilityId, premise.getId())) {
                        Toast.makeText(InsertPremise.this, "Данные добавлены", Toast.LENGTH_SHORT).show();
                    }
                    db.close();
                    finish();
                    Toast.makeText(InsertPremise.this, "Данные добавлены", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}