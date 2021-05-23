package com.example.naver_movie_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    /*
     * 두 개의 fragment를 생성
     * 그리고 main fragment에서 클릭하면 웹뷰 액티비티를 만들어서 보여줌
     * 종료하면 main fragment로 복귀
     */

    BottomNavigationView bottomNavigationView;
    Fragment_Home fragmentHome;
    Fragment_Wishlist fragmentWishlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // 프래그먼트 생성
        fragmentHome = new Fragment_Home();
        fragmentWishlist = new Fragment_Wishlist();

        // 처음 열리는 프래그먼트 지정
        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragmentHome).commitAllowingStateLoss();

        // bottomNavigation에서 아이콘 클릭 시 열리는 fragment 지정
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.page_home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragmentHome).commitAllowingStateLoss();
                    return true;
                case R.id.page_wishList:
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragmentWishlist).commitAllowingStateLoss();
                    return true;
                default:
                    return false;
            }
        });

        String key = BuildConfig.Kofic_Api_Key;
        String weekGb = "0"; // 주간: 0, 주말(금~일): 1, 주중(월~목): 2
        Calendar cal = Calendar.getInstance();

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH + 1);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        String today = year + String.valueOf(month) + day;

        // 영화진흥위원회에서 주간 박스오피스 순위를 json 타입으로 가져옴
        String kobisApiURL = "http://kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchWeeklyBoxOfficeList.json?key=" + key + "&targetDt=" + today + "&weekGb=" + weekGb;
        RestAPITask rat = new RestAPITask(kobisApiURL);
        try {
            ArrayList<String> result = rat.execute().get();

            // imageSrc, title, director, actors, rating
            JSONArray naverApiResult = new JSONArray(result.get(1));

            ArrayList<RecyclerViewItem> homeDataSet = new ArrayList<>();

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

            Bundle bundle = new Bundle();
            bundle.putSerializable("homeDataSet", homeDataSet);
            fragmentHome.setArguments(bundle);
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }
    }

    public static class RestAPITask extends AsyncTask<String, Void, ArrayList<String>> {
        protected String mURL;

        public RestAPITask(String url) {
            mURL = url;
        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            final String clientId = BuildConfig.X_Naver_Client_Id;
            final String clientSecret = BuildConfig.X_Naver_Client_Secret;
            ArrayList<String> resultArr = new ArrayList<>();

            try {
                URL url = new URL(mURL);
                HttpURLConnection koficConnection = (HttpURLConnection) url.openConnection();
                koficConnection.setRequestMethod("GET");

                // Get InputStream
                StringBuilder stringBufferForKofic = new StringBuilder();
                StringBuilder stringBufferForNaver = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(koficConnection.getInputStream(), StandardCharsets.UTF_8));
                String line;

                while ((line = br.readLine()) != null) {
                    stringBufferForKofic.append(line);
                }

                String koficResult = stringBufferForKofic.toString();

                JSONObject jsonObject = new JSONObject(koficResult);
                JSONObject boxOfficeResult = jsonObject.getJSONObject("boxOfficeResult");
                JSONArray weeklyBoxOfficeList = boxOfficeResult.getJSONArray("weeklyBoxOfficeList");

                stringBufferForNaver.append("[");

                for (int i = 0; i < weeklyBoxOfficeList.length(); i++) {
                    JSONObject item = weeklyBoxOfficeList.getJSONObject(i);
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

                resultArr.add(stringBufferForKofic.toString());
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
}