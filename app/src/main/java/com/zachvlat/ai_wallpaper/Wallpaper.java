package com.zachvlat.ai_wallpaper;

public class Wallpaper {
    private String url;
    private String thumbnailUrl;
    private String fullResolutionUrl;

    public Wallpaper(String url) {
        this.url = url;
        this.thumbnailUrl = generateThumbnailUrl(url);
        this.fullResolutionUrl = url; // Original URL is full resolution
    }

    public String getUrl() {
        return url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getFullResolutionUrl() {
        return fullResolutionUrl;
    }

    public void setUrl(String url) {
        this.url = url;
        this.thumbnailUrl = generateThumbnailUrl(url);
        this.fullResolutionUrl = url;
    }

    /**
     * Generate thumbnail URL by adding size parameters
     * For cdn.dreampix.ai, we can add width/height parameters
     */
    private String generateThumbnailUrl(String originalUrl) {
        if (originalUrl.contains("cdn.dreampix.ai")) {
            // Add size parameters for thumbnail (300x500 for good grid performance)
            return originalUrl + "?w=300&h=500&fit=crop&auto=format";
        }
        // For other URLs, return original
        return originalUrl;
    }
}
