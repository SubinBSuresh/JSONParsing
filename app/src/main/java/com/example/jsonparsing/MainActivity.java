package com.example.jsonparsing;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    // global variable for storing all the device info parsed from json
    ArrayList<Device> deviceInfo = new ArrayList<>();
    Button btnFetchJSON;
    RecyclerView listRecyclerView;

    ListAdapter listAdapter;

    JSONParser jsonParser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        jsonParser = JSONParser.getInstance();
        btnFetchJSON = findViewById(R.id.btn_fetchJSON);
        listRecyclerView = findViewById(R.id.rv_list);


//        deviceInfo = localStringJSONParser(jsonString);
//        arrayListPrinter(deviceInfo);


        btnFetchJSON.setOnClickListener(v -> {

            // starting function call
            jsonParser.webJSONParser();
            deviceInfo = jsonParser.getWebDeviceInfo();
            listRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            listAdapter = new ListAdapter(this, deviceInfo);
            listRecyclerView.setAdapter(listAdapter);

        });


        //function to print the arraylist for debugging purpose
        jsonParser.arrayListPrinter(deviceInfo);


    }


}



