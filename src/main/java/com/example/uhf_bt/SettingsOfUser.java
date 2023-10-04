package com.example.uhf_bt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.uhf_bt.Models.TagData;

public class SettingsOfUser extends AppCompatActivity {

    private EditText set_facility,
            set_premise,
            set_executor;
    private Button save_user;

    private DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_of_user);

        set_facility = this.findViewById(R.id.setFacility);
        set_premise = this.findViewById(R.id.setPremise);
        set_executor = this.findViewById(R.id.setExecutor);
        save_user = this.findViewById(R.id.save_user);
        db = new DataBase(this);
        save_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TagData tagData = new TagData();
                tagData.setFacility(set_facility.getText().toString());
                tagData.setPremise(set_premise.getText().toString());
                tagData.setExecutor(set_executor.getText().toString());
                if (db.insertUser(tagData)) {
                    Toast.makeText(SettingsOfUser.this, "Данные добавлены", Toast.LENGTH_SHORT).show();
                }
                db.close();
                finish();
                Toast.makeText(SettingsOfUser.this, "Данные добавлены", Toast.LENGTH_SHORT).show();
            }
        });

    }



}