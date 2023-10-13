package com.example.uhf_bt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.uhf_bt.Models.Facility;

public class InsertFacility extends AppCompatActivity {

    private EditText setFacilityName;
    private EditText setFacilityAddress;
    private EditText setFacilityNote;
    private Button save_facility;
    private DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_facility);
        init();
    }

    private void init(){
        setFacilityName = findViewById(R.id.setFacilityName);
        setFacilityAddress = findViewById(R.id.setFacilityAddress);
        setFacilityNote = findViewById(R.id.setFacilityNote);
        save_facility = findViewById(R.id.save_facility);
        db = new DataBase(this);
        save_facility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Facility facility = new Facility();
                facility.setName(setFacilityName.getText().toString());
                facility.setAddress(setFacilityAddress.getText().toString());
                facility.setNote(setFacilityNote.getText().toString());
                if (db.insertFacility(facility)) {
                    Toast.makeText(InsertFacility.this, "Данные добавлены", Toast.LENGTH_SHORT).show();
                }
                db.close();
                finish();
                Toast.makeText(InsertFacility.this, "Данные добавлены", Toast.LENGTH_SHORT).show();
            }
        });
    }



}