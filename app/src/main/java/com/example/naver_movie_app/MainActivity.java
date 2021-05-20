package com.example.naver_movie_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);

        // 프래그먼트 생성
        fragmentHome = new Fragment_Home();
        fragmentWishlist = new Fragment_Wishlist();

        // 처음 열리는 프래그먼트 지정
        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragmentHome).commitAllowingStateLoss();

        // bottomNavigation에서 아이콘 클릭 시 열리는 fragment 지정
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {
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
            }
        });
    }
}