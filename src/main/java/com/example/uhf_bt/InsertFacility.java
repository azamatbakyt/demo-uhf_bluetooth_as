package com.example.uhf_bt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.uhf_bt.Models.Facility;

import java.util.List;

public class InsertFacility extends AppCompatActivity {

    private EditText setFacilityName;
    private EditText setFacilityAddress;
    private EditText setFacilityNote;
    private Button save_facility;
    private DataBase db;
    private int facilityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_facility);
        init();
    }

    private void init() {
        Intent intent = getIntent();
        facilityId = intent.getIntExtra("facility_id", -1);

        setFacilityName = findViewById(R.id.setFacilityName);
        setFacilityAddress = findViewById(R.id.setFacilityAddress);
        setFacilityNote = findViewById(R.id.setFacilityNote);
        save_facility = findViewById(R.id.save_facility);
        db = new DataBase(this);


        if ((db.getFacilityById(String.valueOf(facilityId))) != null) {
            Facility existFacility = db.getFacilityById(String.valueOf(facilityId));
            setFacilityName.setText(existFacility.getName());
            setFacilityAddress.setText(existFacility.getAddress());
            setFacilityNote.setText(existFacility.getNote());

            save_facility.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = setFacilityName.getText().toString();
                    String address = setFacilityAddress.getText().toString();
                    String note = setFacilityNote.getText().toString();

                    existFacility.setName(name);
                    existFacility.setAddress(address);
                    existFacility.setNote(note);

                    if (db.updateFacility(existFacility, facilityId)) {
                        Toast.makeText(getApplicationContext(), "Данные успешно обновлены", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getApplicationContext(), "Не удалось обновить данные", Toast.LENGTH_LONG).show();
                    }
                    finish();
                }
            });
        } else {
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
}