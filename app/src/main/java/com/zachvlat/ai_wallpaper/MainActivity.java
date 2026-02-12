package com.zachvlat.ai_wallpaper;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private WallpaperAdapter adapter;
    private List<Wallpaper> wallpaperList = new ArrayList<>();
    private List<Wallpaper> filteredWallpaperList = new ArrayList<>();
    private EditText searchEditText;
    private ImageView clearSearchButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Apply dynamic color if available (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            DynamicColors.applyToActivityIfAvailable(this);
        }
        
        super.onCreate(savedInstanceState);
        
        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        
        setContentView(R.layout.activity_main);

        // Set up system UI visibility for edge-to-edge
        setupEdgeToEdge();

        // Set up search functionality
        searchEditText = findViewById(R.id.searchEditText);
        clearSearchButton = findViewById(R.id.clearSearch);
        
        // Configure RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns grid
        adapter = new WallpaperAdapter(this, filteredWallpaperList);
        recyclerView.setAdapter(adapter);
        
        // Set up search functionality
        setupSearch();

        fetchWallpapers();

        // Find the FAB and set click listener
        ExtendedFloatingActionButton fabShuffle = findViewById(R.id.fabShuffle);
        fabShuffle.setOnClickListener(v -> {
            // Shuffle the currently displayed (filtered) list
            Collections.shuffle(filteredWallpaperList);

            // Notify the adapter to refresh the RecyclerView
            adapter.notifyDataSetChanged();

            // Optional: Show a small message to the user
            Toast.makeText(MainActivity.this, "Wallpapers shuffled!", Toast.LENGTH_SHORT).show();
        });
    }
    
    private void setupEdgeToEdge() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
            WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            );
        }
    }

    private void fetchWallpapers() {
        new Thread(() -> {
            try {
                URL url = new URL("https://various-files.vercel.app/wallpapers.json");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                reader.close();
                connection.disconnect();

                // Parse the JSON array of wallpaper objects
                Type listType = new TypeToken<List<Wallpaper>>() {}.getType();
                List<Wallpaper> wallpapers = new Gson().fromJson(stringBuilder.toString(), listType);

                // Update the wallpaper list
                wallpaperList.clear();
                wallpaperList.addAll(wallpapers);
                
                // Apply current filter to the new data
                runOnUiThread(() -> {
                    applySearchFilter(searchEditText.getText().toString());
                    adapter.notifyDataSetChanged();
                });

            } catch (Exception e) {
                Log.e("MainActivity", "Error fetching wallpapers", e);
            }
        }).start();
    }
    
    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                clearSearchButton.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
                applySearchFilter(query);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        
        clearSearchButton.setOnClickListener(v -> {
            searchEditText.setText("");
            searchEditText.clearFocus();
        });
    }
    
    private void applySearchFilter(String query) {
        if (query.isEmpty()) {
            filteredWallpaperList.clear();
            filteredWallpaperList.addAll(wallpaperList);
        } else {
            String lowercaseQuery = query.toLowerCase();
            filteredWallpaperList.clear();
            
            for (Wallpaper wallpaper : wallpaperList) {
                if (wallpaper.getTags() != null) {
                    for (String tag : wallpaper.getTags()) {
                        if (tag.toLowerCase().contains(lowercaseQuery)) {
                            filteredWallpaperList.add(wallpaper);
                            break; // Found matching tag, no need to check others
                        }
                    }
                }
            }
        }
        
        adapter.notifyDataSetChanged();
    }
}
