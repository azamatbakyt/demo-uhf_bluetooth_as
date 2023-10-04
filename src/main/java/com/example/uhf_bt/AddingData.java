package com.example.uhf_bt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uhf_bt.Models.TagData;

import java.util.List;

public class AddingData extends AppCompatActivity {

    private TextView epc;

    private EditText add_description,
                     add_inventory_number,
                     add_nomenclature;

    private Button save_btn;
    private DataBase dataBase;

    private TagDetails tagDetails;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_adding_data);


        this.save_btn = this.findViewById(R.id.save_btn);
        this.add_description = this.findViewById(R.id.add_description);
        this.add_inventory_number = this.findViewById(R.id.add_inventory_number);
        this.add_nomenclature = this.findViewById(R.id.add_nomenclature);
        this.epc = this.findViewById(R.id.epc);
        this.dataBase = new DataBase(this);

        final Intent intent = getIntent();
        final String tag = intent.getStringExtra("tagData");
        this.epc.setText(tag);
        if (!(dataBase.getDataByEpc(tag).isEmpty())) {
            List<TagData> tagDataList = dataBase.getDataByEpc(tag);
            final TagData existingTagData = tagDataList.get(0);

            add_description.setText(existingTagData.getDescription());
            add_inventory_number.setText(existingTagData.getInventoryNumber());
            add_nomenclature.setText(existingTagData.getNomenclature());

            save_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String updatedDescription = add_description.getText().toString();
                    String updatedInventoryNumber = add_inventory_number.getText().toString();
                    String updatedNomenclature = add_nomenclature.getText().toString();

                    existingTagData.setDescription(updatedDescription);
                    existingTagData.setInventoryNumber(updatedInventoryNumber);
                    existingTagData.setNomenclature(updatedNomenclature);

                    if (dataBase.updateData(existingTagData, tag)) {
                        Toast.makeText(getApplicationContext(), "Данные успешно обновлены", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Не удалось обновить данные", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            save_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TagData tagData = new TagData(add_description.getText().toString(),
                            add_inventory_number.getText().toString(),
                            add_nomenclature.getText().toString());
                    String message;
                    if (dataBase.insertEPC(tagData, tag.toString())) {
                        message = "Данные успешно добавлены в базу данных";
                    } else {
                        message = "Не удалось добавить данные в базу данных";
                    }
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        }



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}