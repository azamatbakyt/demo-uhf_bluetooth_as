package com.example.uhf_bt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.uhf_bt.Models.Executor;

public class SettingsOfUser extends AppCompatActivity {

    private EditText setUserRole,
            setUserName,
            setUserNote;
    private Button save_user;

    private DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_of_user);

        setUserRole = this.findViewById(R.id.setUserRole);
        setUserName = this.findViewById(R.id.setNameOfUser);
        setUserNote = this.findViewById(R.id.setNote);
        save_user = this.findViewById(R.id.save_user);
        db = new DataBase(this);
        save_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Executor executor = new Executor();
                executor.setRole(setUserRole.getText().toString());
                executor.setName(setUserName.getText().toString());
                executor.setNotation(setUserNote.getText().toString());
                if (db.insertUser(executor)) {
                    Toast.makeText(SettingsOfUser.this, "Данные добавлены", Toast.LENGTH_SHORT).show();
                }
                db.close();
                finish();
                Toast.makeText(SettingsOfUser.this, "Данные добавлены", Toast.LENGTH_SHORT).show();
            }
        });

    }



}