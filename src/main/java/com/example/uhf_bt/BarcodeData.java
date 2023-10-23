package com.example.uhf_bt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.uhf_bt.Models.TagData;

import java.util.List;

public class BarcodeData extends AppCompatActivity {

    private TextView nomenclatureBarcode;
    private TextView barcode;
    private TextView descriptionBarcode;
    private TextView typeInfo;
    private TextView amountBarcode;

    private Button edit_data;
    private Button back_btn;

    private DataBase db;
    private String barcodeNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_data);

        nomenclatureBarcode = this.findViewById(R.id.nomenclatureBarcode);
        descriptionBarcode = this.findViewById(R.id.descriptionBarcode);
        typeInfo = this.findViewById(R.id.typeInfo);
        amountBarcode = this.findViewById(R.id.amountBarcode);

        barcode = this.findViewById(R.id.barcode);
        edit_data = (Button) this.findViewById(R.id.edit_data);
        back_btn = this.findViewById(R.id.back_btn);
        db = new DataBase(this);
        Intent intent = getIntent();
        barcodeNumber = intent.getStringExtra("barcode");
        barcode.setText(barcodeNumber);

        edit_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(BarcodeData.this, AddingBarcodeData.class);
                intent1.putExtra("barcodeEdit", barcodeNumber);
                startActivity(intent1);
            }
        });

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
        barcodeNumber = intent.getStringExtra("barcode");
        showBarcodeData(barcodeNumber);
    }

    private void showBarcodeData(final String barcode){
        List<TagData> barcodeData = db.getDataByEpcForInventory(barcode);
        if (!barcodeData.isEmpty()){
            TagData barcodeTag = barcodeData.get(0);
            String barcodeDescription = barcodeTag.getDescription();
            String barcodeNomenclature = barcodeTag.getNomenclature();
            String barcodeType = barcodeTag.getType();
            String barcodeAmount = String.valueOf(barcodeTag.getAmount());

            descriptionBarcode.setText(barcodeDescription != null ? barcodeDescription : "Нет  данных");
            nomenclatureBarcode.setText(barcodeNomenclature != null ? barcodeNomenclature : "Нет  данных");
            typeInfo.setText("Type: " + barcodeType != null ? barcodeType : "Нет  данных");
            amountBarcode.setText("Amount: " + barcodeAmount);
        } else {
            descriptionBarcode.setText("Данные не найдены");
            nomenclatureBarcode.setText("Данные не найдены");
            typeInfo.setText("Данные не найдены");
            amountBarcode.setText("Данные не найдены");
        }
    }




}