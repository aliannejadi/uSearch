package ch.usi.aliannejadi.usearch.appRendering;

import android.graphics.drawable.Drawable;

/**
 * Created by jacopofidacaro on 25.07.17.
 */

public class AppResult {

    private String title;
    private Drawable image;

    public AppResult(String title, Drawable image) {

        this.title = title;
        this.image = image;

    }

    public String getTitle() {
        return title;
    }

    public Drawable getImage() {
        return image;
    }

}
