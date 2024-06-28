package com.example.lab6;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ImageView imageView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);

        new CatImages().execute();
    }

    private class CatImages extends AsyncTask<String, Integer, String> {

        private Bitmap catImage;

        @Override
        protected String doInBackground(String... strings) {
            while (true) {
                try {
                    Log.d(TAG, "Fetching cat image JSON...");
                    String jsonString = downloadJson("https://cataas.com/cat?json=true");
                    Log.d(TAG, "JSON Response: " + jsonString);
                    JSONObject jsonObject = new JSONObject(jsonString);
                    String id = jsonObject.getString("_id");
                    String url = "https://cataas.com/cat/" + id;
                    Log.d(TAG, "Downloading cat image from URL: " + url);

                    catImage = downloadImage(url);

                    for (int i = 0; i < 100; i++) {
                        publishProgress(i);
                        Thread.sleep(30);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error in background task", e);
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);

            if (values[0] == 0 && catImage != null) {
                imageView.setImageBitmap(catImage);
            }
        }

        private String downloadJson(String urlString) throws IOException {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            return stringBuilder.toString();
        }

        private Bitmap downloadImage(String urlString) throws IOException {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            return BitmapFactory.decodeStream(inputStream);
        }
    }
}