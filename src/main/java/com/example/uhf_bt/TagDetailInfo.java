package com.example.uhf_bt;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.uhf_bt.Models.TagData;

import java.util.List;

public class TagDetailInfo extends AppCompatActivity {
    private TextView epc;
    private TextView type;
    private TextView description;
    private TextView inventory_number;
    private TextView nomenclature;
    private TextView amount;
    private TextView facility;
    private TextView premise;
    private TextView executor;

    private Button back_btn;
    private DataBase db;
    private String tagData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_detail_info);

        epc = this.findViewById(R.id.epcDetail);
        type = this.findViewById(R.id.EpcType);
        description = this.findViewById(R.id.EpcDescription);
        inventory_number = this.findViewById(R.id.EpcInventoryNumber);
        nomenclature = this.findViewById(R.id.EpcNomenclature);
        amount = this.findViewById(R.id.EpcAmount);
        facility = this.findViewById(R.id.EpcFacility);
        premise = this.findViewById(R.id.EpcPremise);
        executor = this.findViewById(R.id.EpcExecutor);
        back_btn = this.findViewById(R.id.backBtn);
        db = new DataBase(this);

        Intent intent = getIntent();
        tagData = intent.getStringExtra("tag");
        epc.setText(tagData);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        List<TagData> tagDataList = db.getDataByEpcForInventory(epc);
        if (tagDataList != null && !tagDataList.isEmpty()) {
            TagData tagData = tagDataList.get(0);

            String typeText = tagData.getType();
            String descriptionText = tagData.getDescription();
            String inventoryNumberText = tagData.getInventoryNumber();
            String nomenclatureText = tagData.getNomenclature();
            int amountEpc = tagData.getAmount();
            String facilityText = tagData.getFacility();
            String premiseText = tagData.getPremise();
            String executorText = tagData.getExecutor();

            type.setText("Type: " + typeText != null ? typeText : "Нет  данных");
            description.setText(descriptionText != null ? descriptionText : "Нет  данных");
            inventory_number.setText(inventoryNumberText != null ? inventoryNumberText : "Нет  данных");
            nomenclature.setText(nomenclatureText != null ? nomenclatureText : "Нет  данных");
            amount.setText("Amount: " + amountEpc);
            facility.setText(facilityText != null ? facilityText : "Нет  данных");
            premise.setText(premiseText != null ? premiseText : "Нет  данных");
            executor.setText(executorText != null ? executorText : "Нет  данных");


        } else {
            type.setText("Type:" + "Данные не найдены");
            description.setText("Данные не найдены");
            inventory_number.setText("Данные не найдены");
            nomenclature.setText("Данные не найдены");
            nomenclature.setText("Данные не найдены");
            amount.setText("Amount: " + "Данные не найдены");
            facility.setText("Данные не найдены");
            premise.setText("Данные не найдены");
            executor.setText("Данные не найдены");

        }

    }
}