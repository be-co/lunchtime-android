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
package com.tbaehr.lunchtime.view;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tbaehr.lunchtime.R;
import com.tbaehr.lunchtime.model.Offer;

import java.util.List;

import static android.view.animation.AnimationUtils.loadAnimation;
import static com.tbaehr.lunchtime.model.Offer.formatPrize;

/**
 * Created by timo.baehr@gmail.com on 29.12.2016.
 */
public class HorizontalSliderView extends LinearLayout {

    private static final int INGREDIENTS_PADDING_IN_PIXELS = 4;

    private static final int MAX_ITEM_WIDTH = 150;

    private static final double MIN_ITEM_NUMBER = 2.7;

    private View rootView;

    private TextView titleTextView, descriptionTextView;

    private LinearLayout sliderViewContainer;

    private FrameLayout sliderHeader;

    private Animation fadeOutAnimation;

    private OnSliderItemClickListener listener;

    public interface OnSliderHeaderClickListener {
        void onSliderHeaderClick();
    }

    public interface OnSliderItemClickListener {
        void onSliderItemClick(Offer offer, View view);
    }

    public HorizontalSliderView(Context context, String title, String shortDescription, List<Offer> offers, OnSliderHeaderClickListener headerClickListener, OnSliderItemClickListener listener) {
        super(context);
        init(context, title, shortDescription, offers, headerClickListener, listener);
    }

    public HorizontalSliderView(Context context, String title, String shortDescription, List<Offer> offers, AttributeSet attrs, OnSliderHeaderClickListener headerClickListener, OnSliderItemClickListener listener) {
        super(context, attrs);
        init(context, title, shortDescription, offers, headerClickListener, listener);
    }

    private void init(Context context, String title, String shortDescription, List<Offer> offers, final OnSliderHeaderClickListener headerClickListener, OnSliderItemClickListener listener) {
        rootView = inflate(context, R.layout.horizontal_slider_view, this);
        this.setOrientation(LinearLayout.VERTICAL);

        this.listener = listener;

        titleTextView = (TextView) rootView.findViewById(R.id.title);
        titleTextView.setText(title);

        descriptionTextView = (TextView) rootView.findViewById(R.id.short_description);
        descriptionTextView.setText(shortDescription);

        setOffers(context, offers);

        sliderHeader = (FrameLayout) rootView.findViewById(R.id.slider_header);
        sliderHeader.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                headerClickListener.onSliderHeaderClick();
            }
        });
    }

    public void setOffers(Context context, List<Offer> offers) {
        sliderViewContainer = (LinearLayout) rootView.findViewById(R.id.slider_view_container);
        sliderViewContainer.removeAllViews();

        if (offers.isEmpty()) {
            TextView noOffersView = (TextView) rootView.findViewById(R.id.no_offers);
            noOffersView.setVisibility(View.VISIBLE);
            return;
        }

        for (Offer offer : offers) {
            View offerView = createItemView(context, offer);
            sliderViewContainer.addView(offerView);
        }
        sliderViewContainer.post(new Runnable() {
            @Override
            public void run() {
                int titleMaxLines = 1;
                int descriptionMaxLines = 1;
                for (int viewIndex = 0; viewIndex < sliderViewContainer.getChildCount(); viewIndex++) {
                    View offerView = sliderViewContainer.getChildAt(viewIndex);
                    TextView titleView = (TextView) offerView.findViewById(R.id.title);
                    if (titleView.getLineCount() > titleMaxLines) {
                        titleMaxLines = titleView.getLineCount();
                    }
                    TextView descriptionView = (TextView) offerView.findViewById(R.id.description);
                    if (descriptionView.getLineCount() > descriptionMaxLines) {
                        descriptionMaxLines = descriptionView.getLineCount();
                    }
                }
                for (int viewIndex = 0; viewIndex < sliderViewContainer.getChildCount(); viewIndex++) {
                    View offerView = sliderViewContainer.getChildAt(viewIndex);
                    TextView titleView = (TextView) offerView.findViewById(R.id.title);
                    titleView.setMinLines(titleMaxLines);
                    titleView.refreshDrawableState();

                    TextView descriptionView = (TextView) offerView.findViewById(R.id.description);
                    descriptionView.setMinLines(descriptionMaxLines);
                    descriptionView.refreshDrawableState();
                }
            }
        });

    }

    private View createItemView(Context context, final Offer offer) {
        View itemView = inflate(context, R.layout.offer_view, null);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = (int) (displaymetrics.widthPixels / MIN_ITEM_NUMBER * displaymetrics.density);
        if (width > MAX_ITEM_WIDTH * displaymetrics.density) {
            width = (int) (MAX_ITEM_WIDTH * displaymetrics.density);
        }
        itemView.setLayoutParams(new LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT));

        /*ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.setImageDrawable(context.getDrawable(offer.getDrawableRes()));
        } else {
            imageView.setImageDrawable(context.getResources().getDrawable(offer.getDrawableRes()));
        }*/

        TextView titleView = (TextView) itemView.findViewById(R.id.title);
        titleView.setText(offer.getTitle());

        TextView descriptionView = (TextView) itemView.findViewById(R.id.description);
        String description = offer.getDescription();
        if (description.length() > 0) {
            descriptionView.setText(description);
        } else {
            descriptionView.setVisibility(View.GONE);
        }

        TextView prizeView = (TextView) itemView.findViewById(R.id.prize);
        prizeView.setText(formatPrize(offer.getPrize()));

        TextView openingTimeView = (TextView) itemView.findViewById(R.id.availability);
        openingTimeView.setText(offer.getOpeningTimeShortDescription(context));

        LinearLayout ingredients = (LinearLayout) itemView.findViewById(R.id.ingredients);
        for (Offer.Ingredient ingredient : offer.getIngredients()) {
            ImageView tagView = new ImageView(context);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tagView.setImageDrawable(context.getDrawable(ingredient.getDrawableResource()));
            } else {
                tagView.setImageDrawable(context.getResources().getDrawable(ingredient.getDrawableResource()));
            }
            tagView.setPadding(INGREDIENTS_PADDING_IN_PIXELS, 0, INGREDIENTS_PADDING_IN_PIXELS, 0);
            ingredients.addView(tagView);
        }

        itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // Longer click response time (500ms)
                fadeOutAnimation = loadAnimation(getContext(), R.anim.fade_out_500ms);
                final View userFeedbackItemView = view.findViewById(R.id.userFeedbackAdded);
                userFeedbackItemView.setVisibility(View.VISIBLE);
                fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        //;
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        userFeedbackItemView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        //;
                    }
                });
                userFeedbackItemView.setAnimation(fadeOutAnimation);

                listener.onSliderItemClick(offer, view);
            }
        });

        return itemView;
    }

}
