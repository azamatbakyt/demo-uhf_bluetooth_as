package com.example.uhf_bt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import java.util.List;

public class EditPremise extends AppCompatActivity {

    private Spinner spnFacilities;
    private EditText editPremiseName;
    private EditText editPremiseNote;
    private Button edit_premise;
    private DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_premise);

        init();
    }

    private void init(){
        Intent intent = getIntent();
        String premiseName = intent.getStringExtra("premiseName");

        spnFacilities = findViewById(R.id.spnFacilities);
        editPremiseName = findViewById(R.id.editPremiseName);
        editPremiseNote = findViewById(R.id.editPremiseNote);
        edit_premise = findViewById(R.id.edit_premise_button);
        db = new DataBase(this);

        List<Facility> facilities = db.getFacilities();
        ArrayAdapter<Facility> facilityArrayAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, facilities);

        spnFacilities.setAdapter(facilityArrayAdapter);
        Premise existPremise = db.getPremiseByName(premiseName);
        spnFacilities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                parent.getItemAtPosition(position);
                Facility selectedFacility = (Facility) parent.getItemAtPosition(position);
                if (existPremise != null){
                    editPremiseName.setText(existPremise.getName());
                    editPremiseNote.setText(existPremise.getNote());

                    edit_premise.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            existPremise.setName(editPremiseName.getText().toString());
                            existPremise.setNote(editPremiseNote.getText().toString());

                            if (db.updatePremise(existPremise, premiseName)) {
                                Toast.makeText(EditPremise.this, "Premise successfully updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(EditPremise.this, "Couldn't update premise", Toast.LENGTH_SHORT).show();
                            }
                            db.close();
                            finish();
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(EditPremise.this, "This field can't be empty", Toast.LENGTH_SHORT).show();
            }
        });




    }
}