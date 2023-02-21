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

// THIS FUNCTION IS FOR THR CHECK THER WEATHER BUTOON TO GET THE ENTERED DATA AND BEGIN DOWNLAODING IN BACKGROUND
    public void find_weather(View view)
    {

        Log.i("cityname", CityName.getText().toString());


       //THIS 2 LINE OF CODE IS TO HIDE THE KEYBOARD AFTER USER INPUT
        InputMethodManager mgr= (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(CityName.getWindowToken(), 0);





        try {

            //THIS  LINE OF CODE IS FOR THE URL TO ENCODE IF THERE IS A SPCEIAL CHARACTER IN URL IT CONVERTS IT INTO ITS URL EQUIVALENT

            String encodedCityName= URLEncoder.encode( CityName.getText().toString(), "UTF-8");


            //THIS BEGINS THE API COLLECTION FROM THE USL USING DOWNLOAD TASK.EXECUTE (DOWNLOAD TASK IS THE CLASS DEFINED BELOW) task is the object
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


    //THIS CLASS DOWNLOADS THE JSON DATA FROM THE GIVEN URL USING ASYNC CLASS IN BACKGROUND
    public  class DownloadTask extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... urls){


            URL url; //CREATE A URL TYPE
            String result="";
            HttpURLConnection urlConnection= null; // CREATES A URL CONNECTION

            try {
                //CONVERTS THE ENTERED STRING INTO URL TYPE
                url= new URL(urls[0]);

                //OPENS A CONNECTION TO THE GIVEN URL
                urlConnection= (HttpURLConnection) url.openConnection();

                //RECEIVES OUTPUT OR REPLY FROM THE GIVEN URL
                InputStream in= urlConnection.getInputStream();

                //CREATES A READER OBJECT TO READ DATA FROM THE REPLY SENT BY  URL
                InputStreamReader reader= new InputStreamReader(in);

                // READS THE CHARACTER SINGLE FROM THE REPLY BY THE URL 1 CHARACTER AT A TIME
                int data= reader.read();

                //WHILE THE DATA DOESNT STOP COMING IT KEEPS ON READING
                while(data!=-1)
                {

                    //CONVERTS THE INT VALUE OF CHARACTER TO CHAR ORIGNAL CHARACTER
                    char current= (char) data;

                    //APPEND THE CHARACTER TO STRING
                    result+=current;

                    //MOVE THE READER
                    data= reader.read();
                }
                return  result;

            }catch (Exception e)
            {
                //TOAST IF THE INPUT IS WRONG
                Toast.makeText(getApplicationContext(),"could not find weather",Toast.LENGTH_LONG);
            }


            return null;
        }
//AFTER RECEIVING THE TEXT WHAT TO DO IS DONE BY ONPOSTEXECUTE
        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);


            try{


                String message="";

                //CREATE A JSONOBJECT FROM THE RESULT STRING
                JSONObject jsonObject= new JSONObject(result);

                //IT IS LIKE DICTION WHEN ENTERED WEATHER WE GET THE VALUE ASSOCIATED WITH IT
                String weather_info= jsonObject.getString("weather");


                Log.i("weather info", weather_info);

                //GET ALL THE OBJECTS IN THE JSON DATA
                JSONArray arr= new JSONArray(weather_info);

                //ITERATE OVER THE JSON OBJECTS IN JSON DATA IF MULTIPLE OBJECTS RECEIVED
                for (int i = 0; i < arr.length() ; i++) {

                    //CREATE AN OBJECT FOR EVERY PART
                    JSONObject jsonpart= arr.getJSONObject(i);

                    String main="";
                    String description="";

                    //GET THE VALUE ASSOCIATED WITH MAIN IN JSON OBJCET
                    main=jsonpart.getString("main");
                    //GET THE VALUE ASSOCIATED WITH MAIN IN JSON OBJCET
                    description=jsonpart.getString("description");
                    //PRINT BOTH
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