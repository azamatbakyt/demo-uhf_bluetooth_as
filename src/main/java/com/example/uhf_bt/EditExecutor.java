package com.example.uhf_bt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.uhf_bt.Models.Executor;
import com.example.uhf_bt.Models.TagData;

public class EditExecutor extends AppCompatActivity {

    private EditText setUserRole,
            setUserName,
            setUserNote;
    private Button save_user;

    private DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_executor);
        init();
    }

    private void init(){
        Intent intent = getIntent();
        String username = intent.getStringExtra("userToEdit");

        setUserRole = this.findViewById(R.id.editUserRole);
        setUserName = this.findViewById(R.id.editNameOfUser);
        setUserNote = this.findViewById(R.id.editUserNote);
        save_user = this.findViewById(R.id.edit_user_btn);
        db = new DataBase(this);



        if (db.getUserByName(username) != null) {
            Executor user = db.getUserByName(username);
            setUserRole.setText(user.getRole());
            setUserName.setText(user.getName());
            setUserNote.setText(user.getNotation());

            save_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user.setRole(setUserRole.getText().toString());
                    user.setName(setUserName.getText().toString());
                    user.setNotation(setUserNote.getText().toString());

                    if (db.updateUser(user, username)) {
                        Toast.makeText(EditExecutor.this, "User successfully updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EditExecutor.this, "Couldn't update user", Toast.LENGTH_SHORT).show();
                    }

                    finish();
                }
            });
        } else {
            // Обработка случая, когда пользователь не найден
            Toast.makeText(EditExecutor.this, "User not found", Toast.LENGTH_SHORT).show();
            // Возможно, здесь также можно добавить перенаправление на другой экран или логику обработки отсутствия пользователя
        }
        if (db != null) {
            db.close();
        }



    }
}