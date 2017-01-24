/**
 * The MIT License (MIT) Copyright (c) 2017 Timo Bähr
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or s
 * ubstantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.tbaehr.lunchtime.model;

import android.content.Context;
import android.support.annotation.DrawableRes;

import com.tbaehr.lunchtime.R;
import com.tbaehr.lunchtime.utils.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

/**
 * Created by timo.baehr@gmail.com on 29.12.2016.
 */
public class Offer {

    public enum Category {
        BREAKFAST,
        LUNCH,
        DINNER,
        DRINK,
        SNACK;
    }

    public enum ValidationState {
        INVALID,
        NEXT_DAYS_VALID,
        SOON_VALID,
        NOW_VALID,
        OUTDATED;
    }

    public enum Ingredient {
        GLUTEN(R.drawable.ic_gluten),
        LACTOSE(R.drawable.ic_milk),
        EGG(R.drawable.ic_eggs),
        COW(R.drawable.ic_cow),

        // TODO: Rename to PORK
        PIG(R.drawable.ic_pork),
        CHICKEN(R.drawable.ic_chicken),
        FISH(R.drawable.ic_fish);

        @DrawableRes
        int drawableResource;

        Ingredient(@DrawableRes int drawableResource) {
            this.drawableResource = drawableResource;
        }

        public int getDrawableResource() {
            return drawableResource;
        }
    }

    private String title;

    private String description;

    private int prize;

    private Date startDate, endDate;

    private Category category;

    private Set<Ingredient> ingredients;

    //@DrawableRes
    //private int drawableRes;

    public Offer(//@DrawableRes int drawableRes,
                 String title, String description, int prize,
                 String starts, String ends,
                 Category category, Set<Ingredient> ingredients) {
        //this.drawableRes = drawableRes;
        this.title = title;
        this.description = description;
        this.prize = prize;

        startDate = DateUtils.createDateFromString(starts);
        endDate = DateUtils.createDateFromString(ends);

        this.category = category;
        this.ingredients = ingredients;
    }

    public Offer(String title, String description, int prize,
                 String starts, String ends,
                 Category category) {
        this(title, description, prize, starts, ends, category, null);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Offer)) {
            return false;
        }
        Offer other = (Offer) obj;

        return other.getPrize() == prize
                //&& other.drawableRes == drawableRes
                && other.title == title
                && other.description == description
                && other.startDate.equals(startDate)
                && other.endDate.equals(endDate);
    }

    @Override
    public int hashCode() {
        return prize +
                //drawableRes +
                title.hashCode();
    }

    /**
     * @param prize prize in smallest unit, e.g. cents for Euro
     */
    public void setPrize(int prize) {
        this.prize = prize;
    }

    /**
     * @return prize in smallest unit, e.g. cents for Euro
     */
    public int getPrize() {
        return prize;
    }

    public static String formatPrize(int prizeInSmallestUnit) {
        int euros = prizeInSmallestUnit / 100;
        int cents = prizeInSmallestUnit - euros * 100;
        // TODO: Fetch currency
        String prize = String.format("%d,%s€", euros, cents == 0 ? "00" : cents);
        return prize;
    }

    public static String formatMinutes(int timeInMinutes) {
        int hours = timeInMinutes / 60;
        int minutes = timeInMinutes - hours * 60;
        String sMinutes = String.valueOf(minutes);
        sMinutes = sMinutes.length() == 2 ? sMinutes : "0" + sMinutes;
        return hours + ":" + sMinutes;
    }

    /*public int getDrawableRes() {
        return drawableRes;
    }

    public void setDrawableRes(int drawableRes) {
        this.drawableRes = drawableRes;
    }*/

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public ValidationState getValidationState() {
        if (startDate == null || endDate == null) {
            return ValidationState.INVALID;
        }

        Date now = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        int dayOfYearToday = calendar.get(Calendar.DAY_OF_YEAR);

        calendar.setTime(startDate);
        int dayOfYearStart = calendar.get(Calendar.DAY_OF_YEAR);

        if (dayOfYearStart > dayOfYearToday) {
            return ValidationState.NEXT_DAYS_VALID;
        } else if (now.before(startDate)) {
            return ValidationState.SOON_VALID;
        } else if (now.after(startDate) && now.before(endDate)) {
            return ValidationState.NOW_VALID;
        } else {
            return ValidationState.OUTDATED;
        }
    }

    public String getOpeningTimeShortDescription(Context context) {
        Calendar calendar = Calendar.getInstance();
        ValidationState validationState = getValidationState();
        int currentTimeInMinutes;

        switch (validationState) {
            case SOON_VALID:
                calendar.setTimeInMillis(startDate.getTime());
                currentTimeInMinutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
                return context.getString(R.string.starts) + " " + formatMinutes(currentTimeInMinutes);
            case NOW_VALID:
                calendar.setTimeInMillis(endDate.getTime());
                currentTimeInMinutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
                return context.getString(R.string.ends) + " " + formatMinutes(currentTimeInMinutes);
            default:
            case OUTDATED:
                return context.getString(R.string.expired);
        }
    }

    public Category getCategory() {
        return category;
    }

    public Set<Ingredient> getIngredients() {
        return ingredients;
    }

}
