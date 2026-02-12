package com.zachvlat.ai_wallpaper;

import java.util.List;

public class Wallpaper {
    private String title;
    private List<String> tags;
    private String thumbnail_url;
    private String original_url;
    private String detail_page;
    private int engagement_count;
    private String slug;
    private String content_id;

    // Default constructor for Gson
    public Wallpaper() {}

    public String getTitle() {
        return title;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getThumbnailUrl() {
        return thumbnail_url;
    }

    public String getFullResolutionUrl() {
        return original_url;
    }

    public String getDetail_page() {
        return detail_page;
    }

    public int getEngagement_count() {
        return engagement_count;
    }

    public String getSlug() {
        return slug;
    }

    public String getContent_id() {
        return content_id;
    }
}
