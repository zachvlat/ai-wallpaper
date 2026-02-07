package com.zachvlat.ai_wallpaper;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.transition.MaterialContainerTransform;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

public class WallpaperAdapter extends RecyclerView.Adapter<WallpaperAdapter.ViewHolder> {
    private List<Wallpaper> wallpapers;
    private Context context;

    public WallpaperAdapter(Context context, List<Wallpaper> wallpapers) {
        this.context = context;
        this.wallpapers = wallpapers;
        
        // Configure Picasso for better performance
        configurePicasso();
    }
    
    private void configurePicasso() {
        try {
            // Enable memory cache and disk cache for better performance
            Picasso picasso = Picasso.get();
            picasso.setIndicatorsEnabled(false); // Set to true for debugging cache hits
            picasso.setLoggingEnabled(false);   // Set to true for debugging
        } catch (Exception e) {
            // Picasso already configured
        }
    }
    
    private Drawable createBlurPlaceholder() {
        // Create a blur-like placeholder with subtle gradient
        return new android.graphics.drawable.Drawable() {
            @Override
            public void draw(android.graphics.Canvas canvas) {
                // Create blur-like gradient background
                android.graphics.RadialGradient gradient = new android.graphics.RadialGradient(
                    canvas.getWidth() / 2f, canvas.getHeight() / 2f,
                    Math.max(canvas.getWidth(), canvas.getHeight()) / 2f,
                    new int[]{0xFF4A90E2, 0xFF357ABD, 0xFF2968AA},
                    new float[]{0f, 0.5f, 1f},
                    android.graphics.Shader.TileMode.CLAMP
                );
                android.graphics.Paint paint = new android.graphics.Paint();
                paint.setShader(gradient);
                canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
                
                // Add subtle noise effect
                paint.setShader(null);
                paint.setColor(0x22000000);
                for (int i = 0; i < 50; i++) {
                    float x = (float) Math.random() * canvas.getWidth();
                    float y = (float) Math.random() * canvas.getHeight();
                    canvas.drawPoint(x, y, paint);
                }
            }
            
            @Override
            public void setAlpha(int alpha) {}
            
            @Override
            public void setColorFilter(android.graphics.ColorFilter colorFilter) {}
            
            @Override
            public int getOpacity() {
                return android.graphics.PixelFormat.OPAQUE;
            }
        };
    }
    
    private void createLoadingPlaceholder(ImageView imageView) {
        // Set initial blur placeholder
        imageView.setImageDrawable(createBlurPlaceholder());
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

        // Create blur placeholder
        createLoadingPlaceholder(holder.imageView);
        
        // Load image with blur-to-sharp effect
        Picasso.get()
                .load(wallpaper.getThumbnailUrl())
                .resize(300, 500) // Optimal size for grid thumbnails
                .centerCrop()
                .placeholder(createBlurPlaceholder())
                .error(android.R.drawable.ic_menu_report_image)
                .into(holder.imageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        // Animate from blur to sharp
                        holder.imageView.setAlpha(0.7f);
                        holder.imageView.animate()
                                .alpha(1.0f)
                                .setDuration(300)
                                .start();
                    }
                    
                    @Override
                    public void onError(Exception e) {
                        // Keep placeholder on error
                    }
                });

        // Set click listener to open preview dialog
        holder.cardView.setOnClickListener(v -> {
            showWallpaperPreview(wallpaper.getFullResolutionUrl());
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

                            // Show Material 3 Snackbar with better styling
                            View view = ((Activity) context).findViewById(android.R.id.content);
                            Snackbar.make(view, "Wallpaper set successfully!", Snackbar.LENGTH_LONG)
                                    .setAction("View", v -> {
                                        // Optional: Add functionality to view the wallpaper
                                    })
                                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
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
    
    private void showWallpaperPreview(String imageUrl) {
        // Create dialog with custom theme
        Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_wallpaper_preview);
        
        // Set window animations
        if (dialog.getWindow() != null) {
            dialog.getWindow().setWindowAnimations(android.R.style.Animation_Dialog);
        }
        
        // Get views
        ImageView imagePreview = dialog.findViewById(R.id.imagePreview);
        ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
        MaterialButton btnSetWallpaper = dialog.findViewById(R.id.btnSetWallpaper);
        
        
        // Show loading indicator
        progressBar.setVisibility(View.VISIBLE);
        
        // Set blur placeholder for preview
        imagePreview.setImageDrawable(createBlurPlaceholder());
        
        // Load full resolution image with blur-to-sharp effect
        Picasso.get()
                .load(imageUrl)
                .fit()
                .centerInside()
                .placeholder(createBlurPlaceholder())
                .into(imagePreview, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                        // Animate from blur to sharp for preview
                        imagePreview.setAlpha(0.8f);
                        imagePreview.animate()
                                .alpha(1.0f)
                                .setDuration(400)
                                .start();
                    }
                    
                    @Override
                    public void onError(Exception e) {
                        progressBar.setVisibility(View.GONE);
                        showSnackbar("Failed to load image");
                    }
                });
        
        // Set wallpaper button click listener
        btnSetWallpaper.setOnClickListener(v -> {
            btnSetWallpaper.setEnabled(false);
            btnSetWallpaper.setText("Setting...");
            
            setWallpaperFromDialog(imageUrl, dialog, btnSetWallpaper);
        });
        
        
        
        // Show dialog
        dialog.show();
    }
    
    private void setWallpaperFromDialog(String imageUrl, Dialog dialog, MaterialButton button) {
        // Load full resolution image for setting as wallpaper
        Picasso.get()
                .load(imageUrl)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        try {
                            WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
                            wallpaperManager.setBitmap(bitmap);
                            
                            // Close dialog
                            dialog.dismiss();
                            
                            // Show success message
                            View view = ((Activity) context).findViewById(android.R.id.content);
                            Snackbar.make(view, "Wallpaper set successfully!", Snackbar.LENGTH_LONG)
                                    .setAction("View", v -> {
                                        // Optional: Add functionality to view the wallpaper
                                    })
                                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                                    .show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            button.setEnabled(true);
                            button.setText("Set as Wallpaper");
                            showSnackbar("Failed to set wallpaper");
                        }
                    }
                    
                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        button.setEnabled(true);
                        button.setText("Set as Wallpaper");
                        showSnackbar("Failed to load image");
                    }
                    
                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        // Loading state handled by button text change
                    }
                });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            cardView = (MaterialCardView) itemView;
        }
    }
}
