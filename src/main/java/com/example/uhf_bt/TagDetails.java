package com.example.uhf_bt;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uhf_bt.Models.TagData;

import java.io.FileOutputStream;
import java.util.List;


public class TagDetails extends AppCompatActivity {

    private TextView epc,
            description,
            inventory_number,
            nomenclature;
    private Button edit_data,
            back_btn;
    private DataBase dataBase;

    public String tagData;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_details);

        epc = findViewById(R.id.epc);
        this.edit_data = (Button) findViewById(R.id.edit_data);
        this.description = (TextView) findViewById(R.id.description);
        this.inventory_number = findViewById(R.id.inventory_number);
        this.nomenclature = findViewById(R.id.nomenclature);

        back_btn = findViewById(R.id.back_btn);
        dataBase = new DataBase(this);

        // setting up textView(EPC)
        Intent intent = getIntent();
        tagData = intent.getStringExtra("tagData");
        epc.setText(tagData);

        // setting up saving button with new layout
        edit_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(TagDetails.this, AddingData.class);
                intent1.putExtra("tagData", tagData);
                startActivity(intent1);
            }
        });

        // setting up button for finishing action with tag
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        tagData = intent.getStringExtra("tagData");
        showDataByEpc(tagData);
    }


    private void showDataByEpc(final String epc) {
        List<TagData> tagDataList = dataBase.getDataByEpc(epc);

        if (tagDataList != null && !tagDataList.isEmpty()) {
            TagData tagData = tagDataList.get(0);

            String descriptionText = tagData.getDescription();
            String inventoryNumberText = tagData.getInventoryNumber();
            String nomenclatureText = tagData.getNomenclature();

            description.setText(descriptionText != null ? descriptionText : "Нет  данных");
            inventory_number.setText(inventoryNumberText != null ? inventoryNumberText : "Нет данных");
            nomenclature.setText(nomenclatureText != null ? nomenclatureText : "Нет данных");
        } else {
            description.setText("Данные не найдены");
            inventory_number.setText("Данные не найдены");
            nomenclature.setText("Данные не найдены");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}


