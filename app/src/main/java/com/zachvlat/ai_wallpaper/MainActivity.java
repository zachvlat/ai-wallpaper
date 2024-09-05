package com.zachvlat.ai_wallpaper;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private WallpaperAdapter adapter;
    private List<Wallpaper> wallpaperList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WallpaperAdapter(this, wallpaperList);
        recyclerView.setAdapter(adapter);

        fetchWallpapers();
    }

    private void fetchWallpapers() {
        new Thread(() -> {
            try {
                URL url = new URL("https://gist.githubusercontent.com/zachvlat/4009ec76cbaa9c5134b3831dc91a4414/raw/018bd30b8a95376d7e641c6e16926f1ea811cd16/fullImageUrls.json");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                reader.close();
                connection.disconnect();

                // Parse the JSON array
                Type listType = new TypeToken<List<String>>() {}.getType();
                List<String> urls = new Gson().fromJson(stringBuilder.toString(), listType);

                // Convert to Wallpaper objects
                wallpaperList.clear();
                for (String urlStr : urls) {
                    wallpaperList.add(new Wallpaper(urlStr));
                }

                runOnUiThread(() -> adapter.notifyDataSetChanged());

            } catch (Exception e) {
                Log.e("MainActivity", "Error fetching wallpapers", e);
            }
        }).start();
    }
}