package com.example.uhf_bt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.uhf_bt.Models.TagData;

import org.w3c.dom.Text;

import java.util.List;

public class AddingBarcodeData extends AppCompatActivity {

    private TextView barcodeData;
    private EditText add_nomenclature;
    private EditText add_description;
    private EditText add_amount;
    private Button save_btn;
    private DataBase dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_barcode_data);
        save_btn = this.findViewById(R.id.save_barcode);
        add_description = this.findViewById(R.id.add_descriptionBarcode);
        add_amount = this.findViewById(R.id.add_amountBarcode);
        add_nomenclature = this.findViewById(R.id.add_nomenclatureBarcode);
        barcodeData = this.findViewById(R.id.barcodeData);
        dataBase = new DataBase(this);
        final Intent intent = getIntent();
        final String barcodeNumber = intent.getStringExtra("barcodeEdit");
        this.barcodeData.setText(barcodeNumber);

        if (!(dataBase.getDataByEpcForInventory(barcodeNumber).isEmpty())) {

            List<TagData> tagDatalist = dataBase.getDataByEpcForInventory(barcodeNumber);
            final TagData existBarcode = tagDatalist.get(0);

            add_nomenclature.setText(existBarcode.getNomenclature());
            add_amount.setText(String.valueOf(existBarcode.getAmount()));
            add_description.setText(existBarcode.getDescription());


            save_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String updatedDescription = add_description.getText().toString();
                    String updatedAmount = add_amount.getText().toString();
                    String updatedNomenclature = add_nomenclature.getText().toString();

                    existBarcode.setAmount(Integer.parseInt(updatedAmount));
                    existBarcode.setDescription(updatedDescription);
                    existBarcode.setNomenclature(updatedNomenclature);
                    if (dataBase.updateDataInInventory(existBarcode, barcodeNumber)) {
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
                    TagData barcode = new TagData();
                    barcode.setEpc(barcodeNumber);
                    barcode.setType("Barcode");
                    barcode.setDescription(add_description.getText().toString());
                    barcode.setAmount(Integer.parseInt(add_amount.getText().toString()));
                    barcode.setNomenclature(add_nomenclature.getText().toString());
                    barcode.setInventoryNumber("3213111");
                    String message;
                    if (dataBase.insertBarcode(barcode, barcodeNumber)) {
                        message = "Данные успешно добавлены в базу данных";
                    } else {
                        message = "Не удалось добавить данные в базу данных";
                    }
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
            });
        }


    }
}