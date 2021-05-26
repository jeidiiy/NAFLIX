package com.example.naver_movie_app;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class DataFetchAPI {
    public ArrayList<RecyclerViewItem> fetchMovieData(int count) {
        String key = BuildConfig.Kobis_Api_Key;
        String weekGb = "0"; // 주간: 0, 주말(금~일): 1, 주중(월~목): 2
        ArrayList<RecyclerViewItem> homeDataSet = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, count * 7 * -1);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        String monthStr = month < 10 ? "0" + month : "" + month;
        String dayStr = day < 10 ? "0" + day : "" + day;

        String today = year + monthStr + dayStr;

        // 영화진흥위원회에서 주간 박스오피스 순위를 json 타입으로 가져옴
        String kobisApiURL = "http://kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchWeeklyBoxOfficeList.json?key=" + key + "&targetDt=" + today + "&weekGb=" + weekGb;
        NaverAPITask rat = new NaverAPITask(kobisApiURL);
        try {
            ArrayList<String> result = rat.execute().get();

            Log.d("str", result.get(0));
            // imageSrc, title, director, actors, rating
            JSONArray naverApiResult = new JSONArray(result.get(1));

            // naverAPI에서 imageSrc, title, director, actors, rating 파싱
            for (int i = 0; i < naverApiResult.length(); i++) {
                JSONObject naverApiItem = naverApiResult.getJSONObject(i).getJSONArray("items").getJSONObject(0);

                String imageSrc = naverApiItem.getString("image");
                String title = naverApiItem.getString("title");
                String director = naverApiItem.getString("director");
                String actors = naverApiItem.getString("actor");
                int rating = naverApiItem.getInt("userRating");
                String link = naverApiItem.getString("link");

                homeDataSet.add(new RecyclerViewItem(imageSrc, title, director, actors, rating, link));
            }

            return homeDataSet;
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
            return homeDataSet;
        }
    }
}