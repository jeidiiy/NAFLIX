package com.example.naver_movie_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Fragment_Home extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        RecyclerView homeRecyclerView = view.findViewById(R.id.homeRecyclerView);
        RecyclerView.Adapter<MyViewHolder> homeAdapter;
        RecyclerView.LayoutManager homeRecyclerViewLayoutManager = new LinearLayoutManager(getContext());

        homeRecyclerView.setLayoutManager(homeRecyclerViewLayoutManager);

        assert getArguments() != null;
        ArrayList<RecyclerViewItem> homeDataSet = (ArrayList<RecyclerViewItem>) getArguments().getSerializable("homeDataSet");

        homeAdapter = new HomeAdapter(homeDataSet);
        homeRecyclerView.setAdapter(homeAdapter);
        return view;
    }
}