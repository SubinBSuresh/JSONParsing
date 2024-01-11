package com.example.jsonparsing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import kotlinx.coroutines.Delay;

public class MainActivity extends AppCompatActivity {
    // global variable to store connection
    HttpURLConnection connection = null;

    // global variable for storing all the device info parsed from json
    ArrayList<Device> deviceInfo = new ArrayList<>();
    Button btnFetchJSON;
    RecyclerView listRecyclerView;

    ListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnFetchJSON = findViewById(R.id.btn_fetchJSON);
        listRecyclerView = findViewById(R.id.rv_list);
//        String jsonString = "{\"Devices\": [{ \"ID\": 1, \"Name\": \"Bedroom\" }, { \"ID\": 2, \"Name\": \"Lounge\" }, { \"ID\": 3, \"Name\": \"Kitchen\" }, { \"ID\": 4, \"Name\": \"Patio\" }]}";


//        deviceInfo = localStringJSONParser(jsonString);
//        arrayListPrinter(deviceInfo);



        btnFetchJSON.setOnClickListener(v -> {

            // starting function call
            webJSONParser();

            listRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            listAdapter = new ListAdapter(this, deviceInfo);
            listRecyclerView.setAdapter(listAdapter);

        });


        //function to print the arraylist for debugging purpose
        arrayListPrinter(deviceInfo);


    }


    private ArrayList<Device> localStringJSONParser(String jsonString) {
        ArrayList<Device> deviceInfo = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray devicesArray = jsonObject.getJSONArray("Devices");

            for (int i = 0; i < devicesArray.length(); i++) {

                JSONObject deviceObject = devicesArray.getJSONObject(i);
                int deviceID = deviceObject.getInt("ID");
                String deviceName = deviceObject.getString("Name");
                deviceInfo.add(new Device(deviceID, deviceName));
                System.out.println("ID: " + deviceID + ", Name: " + deviceName);
                Log.e("Subin", "ID: " + deviceID + ", Name: " + deviceName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("Error parsing JSON");
            Log.e("subin", "Error parsing json");
        }
        return deviceInfo;
    }


    private void webJSONParser() {

        try {
            urlConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    //fetching data from connection and
    private void fetchData() throws IOException {

        if (connection != null) {
            Thread inputStremThread = new Thread(() -> {
                try {
                    //Retrieves the input stream from the established network connection (connection).
                    // This stream provides a way to read data that's coming from the server.
                    InputStream inputStream = connection.getInputStream();

//                    Calls the readInputStream() method to read the data from the input stream and
//                    store it in a byte array (data).
//                    This method  handles the process of reading bytes from the stream and buffering them.
                    byte[] data = readInputStream(inputStream);

//                    Converts the byte array (data) into a String (jsonString) using the default character encoding
                    String jsonString = new String(data);
                    Log.e("subins", jsonString);


                    //we need to parse the string to get the actual data
                    parseJson(jsonString);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            inputStremThread.start();


        } else {
            Log.e("subin", "connection null");
        }

    }

    private void parseJson(String jsonString) {

        try {
            //Creates a JSONObject from the parsed JSON string (jsonString).
            // This object represents the structured JSON data received from the server.
            JSONObject jsonObject = new JSONObject(jsonString);

            //Extracts a JSONArray named "Devices" from the JSONObject.
            // This array is expected to contain multiple JSON objects, each representing a device.
            JSONArray devicesArray = jsonObject.getJSONArray("Devices");

            // ... (Handle device data as shown in previous responses)
            for (int i = 0; i < devicesArray.length(); i++) {

//                Retrieves the individual device object at index i within the array
                JSONObject deviceObject = devicesArray.getJSONObject(i);

                //Extracts the "ID" value (an integer) from the device object
                int deviceID = deviceObject.getInt("ID");

                //Extracts the "Name" value (a string) from the device object.
                String deviceName = deviceObject.getString("Name");

                //Creates a new Device object using the extracted ID and name, and adds it to the deviceInfo list.
                deviceInfo.add(new Device(deviceID, deviceName));

                System.out.println("ID: " + deviceID + ", Name: " + deviceName);
                Log.e("Subin", "ID: " + deviceID + ", Name: " + deviceName);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private byte[] readInputStream(InputStream inputStream) throws IOException {

//        Creates a ByteArrayOutputStream object named buffer.
//        This object is used to store data in memory as a sequence of bytes.
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;

        //Creates a byte array named data with a size of 1024 bytes.
        // This array will be used to temporarily hold chunks of data read from the input stream before they're written to the buffer.
        byte[] data = new byte[1024];


//       continues as long as there's data available to read from the input stream
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {

            //Writes the bytes that were just read from the input stream (data) into the buffer.
            // Only the bytes that were actually read (nRead) are written to the buffer.
            buffer.write(data, 0, nRead);
        }

        //ensuring that all data written to it is actually stored in the underlying byte array
        buffer.flush();
        return buffer.toByteArray();
    }


    // url connection to the provided url
    private void urlConnection() throws IOException {

        //network operations can be thread blocking, so we need to do the operations on a separate thread other than main thread
        //so we create a new thread and add necessary operations
        Thread networkConnection = new Thread(() -> {

            try {
                //Creates a URL object named url from the specified string, which represents a web address (URL).
                // This URL points to a JSON file containing device information
                URL url = new URL("https://skyegloup-eula.s3.amazonaws.com/heos_app/code_test/devices.json");

                //Opens a connection to the specified URL using the HttpURLConnection class.
                // This class provides methods for interacting with web resources using HTTP.
                //The openConnection() method returns a generic URLConnection object, which is then cast to an HttpURLConnection object to access HTTP-specific features.
                connection = (HttpURLConnection) url.openConnection();

                //Sets the request method for the connection to "GET".
                // This indicates that the client (the code making the request) wants to retrieve data from the server.
                connection.setRequestMethod("GET");

                //Initiates the connection to the server.
                // This involves sending the HTTP request (with the "GET" method) to the server and establishes a communication channel for receiving the response.
                connection.connect();

                //after successful connection we can fetch data
                fetchData();


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        //start the thread,
        // the above thread will be executed only once we call .start()
        networkConnection.start();


        Log.e("subin", "URL connection done");

    }

    private void arrayListPrinter(ArrayList<Device> deviceInfo) {
        for (Device device : deviceInfo) {
            Log.e("subin", String.valueOf(device.getId()));
            Log.e("subin", String.valueOf(device.getName()));

        }
    }

}



