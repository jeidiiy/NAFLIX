package com.example.naver_movie_app;

import android.app.AlertDialog;
import android.content.Intent;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Fragment_Home extends Fragment {

    SQLiteDatabase sqlDB;
    SQLiteOpenHelper sqlHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        RecyclerView homeRecyclerView = view.findViewById(R.id.homeRecyclerView);
        HomeAdapter homeAdapter;
        RecyclerView.LayoutManager homeRecyclerViewLayoutManager = new LinearLayoutManager(getContext());

        homeRecyclerView.setLayoutManager(homeRecyclerViewLayoutManager);

        assert getArguments() != null;
        ArrayList<RecyclerViewItem> homeDataSet = (ArrayList<RecyclerViewItem>) getArguments().getSerializable("homeDataSet");

        homeAdapter = new HomeAdapter(homeDataSet);
        homeAdapter.setOnItemClickListener((view1, position) -> {
            Intent intent = new Intent(view1.getContext(), MovieWebViewActivity.class);
            intent.putExtra("url", homeDataSet.get(position).getLink());
            startActivity(intent);
        });
        homeAdapter.setOnItemLongClickListener((view1, position) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(view1.getContext());

            builder.setTitle("찜 목록 추가");
            builder.setMessage("찜 목록에 추가하시겠습니까?");
            builder.setPositiveButton("예", (dialog, which) -> {
                sqlHelper = new MovieDBHelper(getContext());
                sqlDB = sqlHelper.getWritableDatabase();

                String imageSrc = homeDataSet.get(position).getImageSrc();
                String title = homeDataSet.get(position).getTitle();
                String director = homeDataSet.get(position).getDirector();
                String actors = homeDataSet.get(position).getActors();
                int rating = homeDataSet.get(position).getRating();
                String link = homeDataSet.get(position).getLink();

                String query = "INSERT INTO movie VALUES ( '"
                        + imageSrc + "' , '" + title + "' , '" + director + "' , '"
                        + actors + "' , " + rating + " , '" + link + "');";

                sqlDB.execSQL(query);
                sqlDB.close();

                Toast.makeText(view1.getContext(), "찜 목록에 추가했습니다.", Toast.LENGTH_SHORT).show();
            });
            builder.setNegativeButton("아니오", (dialog, which) -> Toast.makeText(view1.getContext(), "취소했습니다.", Toast.LENGTH_SHORT).show());
            builder.show();
        });
        homeRecyclerView.setAdapter(homeAdapter);

        return view;
    }
}