package com.example.naver_movie_app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class Fragment_Home extends Fragment {

    SQLiteDatabase sqlDB;
    SQLiteOpenHelper sqlHelper;
    ArrayList<RecyclerViewItem> homeDataSet;
    RecyclerView homeRecyclerView;
    HomeAdapter homeAdapter;
    ProgressDialog progressDialog;
    private int lastWeekCount = 2;

    boolean isLoading = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        homeRecyclerView = view.findViewById(R.id.homeRecyclerView);

        RecyclerView.LayoutManager homeRecyclerViewLayoutManager = new LinearLayoutManager(getContext());
        homeRecyclerView.setLayoutManager(homeRecyclerViewLayoutManager);

        assert getArguments() != null;
        homeDataSet = (ArrayList<RecyclerViewItem>) getArguments().getSerializable("homeDataSet");

        initAdapter();
        initScrollListener();

        return view;
    }

    private void initAdapter() {
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
                Cursor cursor = sqlDB.rawQuery("SELECT * FROM movie WHERE imageSrc = '" + imageSrc + "';", null);

                if (cursor.moveToNext()) {
                    Toast.makeText(view1.getContext(), "이미 찜 목록에 있는 영화입니다.", Toast.LENGTH_SHORT).show();
                    cursor.close();
                    sqlDB.close();
                    return;
                }

                cursor.close();

                String title = homeDataSet.get(position).getTitle();
                String director = homeDataSet.get(position).getDirector();
                String actors = homeDataSet.get(position).getActors();
                int rating = homeDataSet.get(position).getRating();
                String link = homeDataSet.get(position).getLink();

                String query = "INSERT INTO movie(imageSrc, title, director, actors, rating, link) VALUES ( '"
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
    }

    private void initScrollListener() {
        homeRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == homeDataSet.size() - 1) {
                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });
    }

    private void loadMore() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("지난 박스오피스 영화 로딩 중...");
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);
        progressDialog.show();

        Handler handler = new Handler();
        handler.post(() -> {
            int scrollPosition = homeDataSet.size();
            homeAdapter.notifyItemRemoved(scrollPosition);
            ArrayList<RecyclerViewItem> data = new DataFetchAPI().fetchMovieData(lastWeekCount);
            homeDataSet.addAll(data);

            LinkedHashSet<RecyclerViewItem> listSet = new LinkedHashSet<>(homeDataSet);
            homeDataSet.clear();
            homeDataSet.addAll(listSet);

            homeAdapter.notifyDataSetChanged();
            progressDialog.dismiss();
            lastWeekCount++;
            isLoading = false;
        });
    }
}