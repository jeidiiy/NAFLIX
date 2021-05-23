package com.example.naver_movie_app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class HomeAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private final ArrayList<RecyclerViewItem> dataSet;

    public HomeAdapter(ArrayList<RecyclerViewItem> dataSet) {
        this.dataSet = dataSet;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        return new MyViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        String title = dataSet.get(position).getTitle().replaceAll("</?b>", "");
        String director = dataSet.get(position).getDirector().replaceAll("\\|", ", ");
        // 맨 뒤 콤마 삭제
        if (!director.equals("")) {
            director = director.substring(0, director.length() - 2);
        }

        String actor = dataSet.get(position).getActors().replaceAll("\\|", ", ");
        // 맨 뒤 콤마 삭제
        if (!actor.equals("")) {
            actor = actor.substring(0, actor.length() - 2);
        }

        holder.textViewTitle.setText(title);
        holder.textViewDirector.setText("감독: " + director);
        holder.textViewActors.setText("배우: " + actor);
        holder.textViewRating.setText(String.valueOf(dataSet.get(position).getRating()));
        holder.ratingBar.setRating((float)dataSet.get(position).getRating() / 2);
        AsyncTask<String, Void, Bitmap> bitmap = new DownloadFilesTask().execute(dataSet.get(position).getImageSrc());
        try {
            holder.imageViewPoster.setImageBitmap(bitmap.get());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return this.dataSet.size();
    }

    private static class DownloadFilesTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bmp = null;
            try {
                String img_url = strings[0]; //url of the image
                URL url = new URL(img_url);
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmp;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Bitmap result) {
        }
    }
}
