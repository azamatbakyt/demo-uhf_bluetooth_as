package com.example.uhf_bt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.uhf_bt.Models.Facility;
import com.example.uhf_bt.Models.Premise;
import com.example.uhf_bt.Models.TagData;

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
        List<Facility> facilities = db.getFacilities();
        save_premise = findViewById(R.id.save_premise);
        ArrayAdapter<Facility> facilityArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, facilities);
        spnPremiseGet.setAdapter(facilityArrayAdapter);
        spnPremiseGet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                save_premise.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // This code will be executed when the "save_premise" button is clicked
                        Facility selectedFacility = (Facility) spnPremiseGet.getSelectedItem();
                        if (selectedFacility != null) {
                            int id = selectedFacility.getId();
                            Premise premise = new Premise();
                            premise.setName(setPremiseName.getText().toString());
                            premise.setNote(setPremiseNote.getText().toString());
                            if (db.insertPremise(premise, id)) {
                                Toast.makeText(InsertPremise.this, "Данные добавлены", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(InsertPremise.this, "Ошибка при добавлении данных", Toast.LENGTH_SHORT).show();
                            }
                            db.close();
                            finish();
                        } else {
                            Toast.makeText(InsertPremise.this, "Выберите объект первого уровня (facility)", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                Toast.makeText(InsertPremise.this, "You have to choose", Toast.LENGTH_LONG).show();
            }
        });


    }
}