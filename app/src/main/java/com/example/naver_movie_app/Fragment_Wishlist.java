package com.example.naver_movie_app;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Fragment_Wishlist extends Fragment {
    SQLiteOpenHelper sqlHelper;
    SQLiteDatabase sqlDB;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.fragment_wish_list, container, false);
        RecyclerView wishlistRecyclerView = view.findViewById(R.id.wishlistRecyclerView);
        WishlistAdapter wishlistAdapter;
        RecyclerView.LayoutManager wishlistRecyclerViewLayoutManager = new LinearLayoutManager(getContext());
        ArrayList<RecyclerViewItem> wishlistDataSet = new ArrayList<>();
        RecyclerViewItem item;

        wishlistRecyclerView.setLayoutManager(wishlistRecyclerViewLayoutManager);

        sqlHelper = new MovieDBHelper(getContext());
        sqlDB = sqlHelper.getWritableDatabase();

        Cursor cursor;
        cursor = sqlDB.rawQuery("SELECT * FROM movie;", null);

        String imageSrc;
        String title;
        String director;
        String actors;
        int rating;
        String link;
        while (cursor.moveToNext()) {
            imageSrc = cursor.getString(0);
            title = cursor.getString(1);
            director = cursor.getString(2);
            actors = cursor.getString(3);
            rating = cursor.getInt(4);
            link = cursor.getString(5);
            item = new RecyclerViewItem(imageSrc, title, director, actors, rating, link);
            wishlistDataSet.add(item);
        }

        cursor.close();
        sqlHelper.close();

        wishlistAdapter = new WishlistAdapter(wishlistDataSet);

        wishlistAdapter.setOnItemClickListener((view1, position) -> {
            Intent intent = new Intent(view1.getContext(), MovieWebViewActivity.class);
            intent.putExtra("url", wishlistDataSet.get(position).getLink());
            startActivity(intent);
        });
        wishlistAdapter.setOnItemLongClickListener((view1, position) -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(view1.getContext());

            builder.setTitle("찜 목록에서 제거");
            builder.setMessage("찜 목록에서 지우시겠습니까?");
            builder.setPositiveButton("예", (dialog, which) -> {
                sqlHelper = new MovieDBHelper(getContext());
                sqlDB = sqlHelper.getWritableDatabase();

                String imageSrc1 = wishlistDataSet.get(position).getImageSrc();
                String query = "DELETE FROM movie WHERE imageSrc = '" + imageSrc1 + "';";

                sqlDB.execSQL(query);

                Toast.makeText(view1.getContext(), "찜 목록에서 제거했습니다.", Toast.LENGTH_SHORT).show();

                assert getFragmentManager() != null;
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(this).attach(this).commit();
            });
            builder.setNegativeButton("아니오", (dialog, which) -> Toast.makeText(view1.getContext(), "취소했습니다.", Toast.LENGTH_SHORT).show());
            builder.show();
        });
        wishlistRecyclerView.setAdapter(wishlistAdapter);

        return view;
    }
}