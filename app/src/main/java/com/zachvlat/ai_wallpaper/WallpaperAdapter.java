package com.zachvlat.ai_wallpaper;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

public class WallpaperAdapter extends RecyclerView.Adapter<WallpaperAdapter.ViewHolder> {
    private List<Wallpaper> wallpapers;
    private Context context;

    public WallpaperAdapter(Context context, List<Wallpaper> wallpapers) {
        this.context = context;
        this.wallpapers = wallpapers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_wallpaper, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Wallpaper wallpaper = wallpapers.get(position);

        // Load image into ImageView
        Picasso.get()
                .load(wallpaper.getUrl())
                .resize(270, 460) // Resize to a lower resolution
                .centerCrop()
                .into(holder.imageView);

        // Set click listener
        holder.imageView.setOnClickListener(v -> {
            // Load the animation
            Animation zoomIn = AnimationUtils.loadAnimation(context, R.anim.zoom_in);
            Animation zoomOut = AnimationUtils.loadAnimation(context, R.anim.zoom_out);

            // Start zoom-in animation
            holder.imageView.startAnimation(zoomIn);

            // After the zoom-in animation ends, start the wallpaper setting process
            zoomIn.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    // No-op
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    setWallpaper(wallpaper.getUrl());
                    holder.imageView.startAnimation(zoomOut);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return wallpapers.size();
    }

    private void setWallpaper(String imageUrl) {
        Picasso.get()
                .load(imageUrl)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        try {
                            WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
                            wallpaperManager.setBitmap(bitmap);

                            // Show Snackbar
                            View view = ((Activity) context).findViewById(android.R.id.content);
                            Snackbar.make(view, "Wallpaper set!", Snackbar.LENGTH_LONG)
                                    .setAction("Undo", v -> {
                                        // Optional: Add functionality to undo wallpaper change
                                    })
                                    .show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            showSnackbar("Failed to set wallpaper");
                        }
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        showSnackbar("Failed to load image");
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        // Optionally show a placeholder while loading
                    }
                });
    }

    private void showSnackbar(String message) {
        View view = ((Activity) context).findViewById(android.R.id.content);
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
