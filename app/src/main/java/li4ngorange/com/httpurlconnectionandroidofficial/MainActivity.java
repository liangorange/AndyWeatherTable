package li4ngorange.com.httpurlconnectionandroidofficial;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.quickconnectfamily.json.JSONException;
import org.quickconnectfamily.json.JSONInputStream;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private static final String DEBUG_TAG = "HttpExample";
    private EditText urlText;
    private TextView textView;
    private ListView weatherList;
    private ArrayList<HashMap> cityList;
    private ArrayList<HashMap> displayList;

    // For getting the json file from assets
    AssetManager assetManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         assetManager = getAssets();

        urlText = (EditText) findViewById(R.id.myUrl);
        textView = (TextView) findViewById(R.id.myText);
        weatherList = (ListView) findViewById(R.id.listWeather);

        // Initialize the ArrayList to store list of cities
        cityList = new ArrayList<>();

        // Initialize the ArrayList to store user defined cities
        displayList = new ArrayList<>();

        // Using an separate thread to read the JSON file, and parse it to the ArrayList
        new Thread(new Runnable() {
            @Override
            public void run() {
                readFromFile();
                System.out.println("Total item in the cityList: " + cityList.size());
            }
        }).start();
    }


    // When user clicks button, calls AsyncTask
    // Before attempting to fetch the URL, makes sure that there is a network connection
    public void myClickHandler(View view) {

        // Get the URL from UI's text field
        // String stringUrl = urlText.getText().toString();

        // Check the network connection
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        String inputCityName = urlText.getText().toString();
        Object cityID = 0;
        Boolean isFound = false;

        if (inputCityName != null) {

            for (HashMap map : cityList) {

                String dataName = ((String)map.get("name")).toLowerCase();

                if (dataName.equals(inputCityName.toLowerCase())) {
                    cityID = map.get("_id");
                    System.out.println("ID: " + cityID);
                    isFound = true;
                }
            }

            if (!isFound) {
                textView.setText("Sorry, the city is not in the list...");
            } else {


                final String stringUrl = "http://api.openweathermap.org/data/2.5/weather?id=" + cityID +"&mode=json&units=imperial&appid=3915d84f579774d93c20a82f0e50329a";

                final Handler myHandler = new Handler();

                final ListAdapter weatherInfo = new CustomApater(this, displayList);


                if (networkInfo != null && networkInfo.isConnected()) {

                    System.out.println("The network connection status is good");

                    new Thread(new Runnable() {

                        HashMap city = new HashMap();

                        @Override
                        public void run() {

                            try {

                                city = downloadUrltoHash(stringUrl);

                                displayList.add(city);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                            myHandler.post(new Runnable() {
                                @Override
                                public void run() {

                                    weatherList.setAdapter(weatherInfo);

                                    textView.setText("Status: Connected!");

                                    // textView.setText("City Name: " + city.get("name") + "\nCurrent Temperature: " + currentTemp
                                    // + "\nMinimum Temperature: " + minTemp + "\nMaximum Temperature: " + maxTemp);

                                }
                            });
                        }

                    }).start();

                } else {
                    textView.setText("Status: No network connection avaiable");
                }

            }
        }
    }


    private HashMap downloadUrltoHash(String myurl) throws IOException {

        InputStream is = null;

        try {

            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            // Starts the query
            conn.connect();

            // getResponseCode() returns the connection's status code.
            // This is a useful way of getting additional information about the connection
            // A status code of 200 indicates success
            int response = conn.getResponseCode();
            Log.d(DEBUG_TAG, "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            // String contentAsString = readIt(is, len);
            String contentAsString = "";
            HashMap tempMap = readToHash(is);
            System.out.println("City name: " + tempMap.get("name"));
            return tempMap;


            // Makes sure that the InputStream is closed after the app is
            // finished using it
        } finally {
            if (is != null) {
                is.close();
            }
        }

    }


    public void readFromFile() {
        try {
            // FileInputStream fin = new FileInputStream("majorCity.json");

            InputStream fin = assetManager.open("majorCity.json");

            JSONInputStream jin = new JSONInputStream(fin);

            cityList = (ArrayList<HashMap>) jin.readObject();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public HashMap readToHash(InputStream stream) throws IOException {

        JSONInputStream inputStream = new JSONInputStream(stream);

        System.out.println("Input Stream: " + stream);

        HashMap tempHash = new HashMap();

        try {
            tempHash = (HashMap)inputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("The City: " + (String)tempHash.get("name"));

        return tempHash;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}





/*

// Uses AsyncTask to create a task away from the main UI thread. This task takes a
    // URL string and uses it to create an HttpUrlConnection. Once the connection has
    // been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: param[0] is the url
            try {
                // The downloadUrl() method takes a URL string as a parameter and uses it to create a URL object
                // URL object is used to establish an HttpURLConnection
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid";
            }
        }

        // onPostExecute displays the results of the AsyncTask
        @Override
        protected void onPostExecute(String result) {
            textView.setText(result);
        }
    }



    // Given an URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string
    private String downloadUrl(String myurl) throws IOException {

        InputStream is = null;

        // Only display the first 500 characters of the retrieved
        // web page content
        int len = 500;

        try {

            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();


            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);


// Test Code: Make the timeout to be negative number, it still works!!!
try {
        conn.setReadTimeout(-1000);
        conn.setConnectTimeout(-1000);
        } catch (Exception e) {
        e.printStackTrace();
        }


        conn.setRequestMethod("GET");



            try {
                conn.setRequestMethod("POST");
            } catch (Exception e) {
                e.printStackTrace();
            }



        conn.setDoInput(true);

        // Starts the query
        conn.connect();

        // getResponseCode() returns the connection's status code.
        // This is a useful way of getting additional information about the connection
        // A status code of 200 indicates success
        int response = conn.getResponseCode();
        Log.d(DEBUG_TAG, "The response is: " + response);
        is = conn.getInputStream();

        // Convert the InputStream into a string
        // String contentAsString = readIt(is, len);
        String contentAsString = "";
        HashMap tempMap = readToHash(is);
        System.out.println("City name: " + tempMap.get("name"));
        return contentAsString;


        // Makes sure that the InputStream is closed after the app is
        // finished using it
        } finally {
        if (is != null) {
        is.close();
        }
        }

        }



// Reads an InputStream and converts it to a String
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {

        Reader reader = null;

        reader = new InputStreamReader(stream, "UTF-8");

        char[] buffer = new char[len];
        reader.read(buffer);

        return new String(buffer);

    }

 */