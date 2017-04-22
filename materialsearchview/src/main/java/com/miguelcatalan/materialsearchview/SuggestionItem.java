package com.miguelcatalan.materialsearchview;

/**
 * Created by timob on 14.04.2017.
 */
public class SuggestionItem {

    private boolean mSticky;

    private int suggestionIconRes;

    private String suggestionText;

    public SuggestionItem(boolean mSticky, int suggestionIconRes, String suggestionText) {
        this.mSticky = mSticky;
        this.suggestionIconRes = suggestionIconRes;
        this.suggestionText = suggestionText;
    }

    public boolean isSticky() {
        return mSticky;
    }

    public int getIconRes() {
        return suggestionIconRes;
    }

    public String getText() {
        return suggestionText;
    }
}
