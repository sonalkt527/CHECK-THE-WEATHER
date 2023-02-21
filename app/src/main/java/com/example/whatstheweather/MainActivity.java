package com.example.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText CityName;
    TextView resulttext;


    public void find_weather(View view)
    {

        Log.i("cityname", CityName.getText().toString());

        InputMethodManager mgr= (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(CityName.getWindowToken(), 0);
        try {
            String encodedCityName= URLEncoder.encode( CityName.getText().toString(), "UTF-8");
            DownloadTask task= new DownloadTask();
            task.execute("https://api.openweathermap.org/data/2.5/weather?q="+CityName.getText().toString()+"&appid=be6a03831908835c1928787e242fbc0a");


        }catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"could not find weather",Toast.LENGTH_LONG);
        }



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CityName=(EditText) findViewById(R.id.cityname);
        resulttext=(TextView)findViewById(R.id.ResulTextView);
    }

    public  class DownloadTask extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... urls){


            URL url;
            String result="";
            HttpURLConnection urlConnection= null;

            try {
                url= new URL(urls[0]);
                urlConnection= (HttpURLConnection) url.openConnection();
                InputStream in= urlConnection.getInputStream();
                InputStreamReader reader= new InputStreamReader(in);
                int data= reader.read();
                while(data!=-1)
                {
                    char current= (char) data;
                    result+=current;
                    data= reader.read();
                }
                return  result;

            }catch (Exception e)
            {
                Toast.makeText(getApplicationContext(),"could not find weather",Toast.LENGTH_LONG);
            }


            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            try{
                String message="";
                JSONObject jsonObject= new JSONObject(result);
                String weather_info= jsonObject.getString("weather");
                Log.i("weather info", weather_info);
                JSONArray arr= new JSONArray(weather_info);
                for (int i = 0; i < arr.length() ; i++) {
                    JSONObject jsonpart= arr.getJSONObject(i);

                    String main="";
                    String description="";
                    main=jsonpart.getString("main");
                    description=jsonpart.getString("description");
                    if(main!="" && description!="")
                    {
                        message+=main+": "+description+"\r\n";
                    }

                    if(message!="")
                    {
                        resulttext.setText(message);
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"could not find weather",Toast.LENGTH_LONG);
                    }

                }
            }catch(Exception e)
            {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"could not find weather",Toast.LENGTH_LONG);
            }
        }
    }
}