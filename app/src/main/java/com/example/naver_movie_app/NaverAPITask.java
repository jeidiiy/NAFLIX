package com.example.naver_movie_app;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class NaverAPITask extends AsyncTask<String, Void, ArrayList<String>> {
    protected String mURL;

    public NaverAPITask(String url) {
        mURL = url;
    }

    @Override
    protected ArrayList<String> doInBackground(String... params) {
        final String clientId = BuildConfig.X_Naver_Client_Id;
        final String clientSecret = BuildConfig.X_Naver_Client_Secret;
        ArrayList<String> resultArr = new ArrayList<>();

        try {
            URL url = new URL(mURL);
            HttpURLConnection kobisConnection = (HttpURLConnection) url.openConnection();
            kobisConnection.setRequestMethod("GET");

            // Get InputStream
            StringBuilder stringBufferForKobis = new StringBuilder();
            StringBuilder stringBufferForNaver = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(kobisConnection.getInputStream(), StandardCharsets.UTF_8));
            String line;

            while ((line = br.readLine()) != null) {
                stringBufferForKobis.append(line);
            }

            String kobisResult = stringBufferForKobis.toString();

            JSONObject jsonObject = new JSONObject(kobisResult);
            JSONObject boxOfficeResult = jsonObject.getJSONObject("boxOfficeResult");
//            JSONArray weeklyBoxOfficeList = boxOfficeResult.getJSONArray("weeklyBoxOfficeList");
            JSONArray dailyBoxOfficeList = boxOfficeResult.getJSONArray("dailyBoxOfficeList");

            stringBufferForNaver.append("[");

            for (int i = 0; i < dailyBoxOfficeList.length(); i++) {
                JSONObject item = dailyBoxOfficeList.getJSONObject(i);
                String title = URLEncoder.encode(item.getString("movieNm"), "UTF-8");
                String naverApiUrl = "https://openapi.naver.com/v1/search/movie.json?query=" + title + "&display=" + 1;
                URL naverURL = new URL(naverApiUrl);
                HttpURLConnection naverApiConnection = (HttpURLConnection) naverURL.openConnection();
                naverApiConnection.setRequestMethod("GET");
                naverApiConnection.setRequestProperty("X-Naver-Client-Id", clientId);
                naverApiConnection.setRequestProperty("X-Naver-Client-Secret", clientSecret);

                BufferedReader br2 = new BufferedReader(new InputStreamReader(naverApiConnection.getInputStream(), StandardCharsets.UTF_8));

                String line2;

                while ((line2 = br2.readLine()) != null) {
                    stringBufferForNaver.append(line2);
                }

                stringBufferForNaver.append(",");

            }
            stringBufferForNaver.deleteCharAt(stringBufferForNaver.length() - 1);
            stringBufferForNaver.append("]");

            resultArr.add(stringBufferForKobis.toString());
            resultArr.add(stringBufferForNaver.toString());

            return resultArr;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return resultArr;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<String> result) {
    }
}