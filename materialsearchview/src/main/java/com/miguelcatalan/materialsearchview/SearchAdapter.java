package com.miguelcatalan.materialsearchview;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Suggestions Adapter.
 *
 * @author Miguel Catalan Bañuls
 */
public class SearchAdapter extends BaseAdapter implements Filterable {

    private ArrayList<SuggestionItem> data;
    private SuggestionItem[] suggestions;
    private LayoutInflater inflater;
    private CharSequence mConstraint;
    private boolean ellipsize;

    public SearchAdapter(Context context, SuggestionItem[] suggestions) {
        inflater = LayoutInflater.from(context);
        data = new ArrayList<>();
        this.suggestions = suggestions;
    }

    public SearchAdapter(Context context, SuggestionItem[] suggestions, boolean ellipsize) {
        inflater = LayoutInflater.from(context);
        data = new ArrayList<>();
        this.suggestions = suggestions;
        this.ellipsize = ellipsize;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                mConstraint = constraint;

                // Retrieve the autocomplete results.
                List<SuggestionItem> searchData = new ArrayList<>();

                if (TextUtils.isEmpty(constraint)) {
                    for (SuggestionItem suggestionItem : suggestions) {
                        if (suggestionItem.isSticky()) {
                            searchData.add(suggestionItem);
                        }
                    }
                } else {
                    for (SuggestionItem suggestionItem : suggestions) {
                        String suggestionText = suggestionItem.getText();
                        if (suggestionItem.isSticky() || suggestionText.toLowerCase().contains(constraint.toString().toLowerCase())) {
                            searchData.add(suggestionItem);
                        }
                    }
                }

                // Assign the data to the FilterResults
                filterResults.values = searchData;
                filterResults.count = searchData.size();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.values != null) {
                    data = (ArrayList<SuggestionItem>) results.values;
                    notifyDataSetChanged();
                }
            }
        };
        return filter;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public SuggestionItem getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SuggestionsViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.suggest_item, parent, false);
            viewHolder = new SuggestionsViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (SuggestionsViewHolder) convertView.getTag();
        }

        SuggestionItem currentListData = getItem(position);

        CharSequence suggestionText = fromHtml(currentListData.getText().replace(mConstraint, "<b>"+mConstraint+"</b>"));
        if (mConstraint.length() > 0) {
            CharSequence contraintCapital = String.valueOf(mConstraint.charAt(0)).toUpperCase() + mConstraint.subSequence(1, mConstraint.length());
            suggestionText = fromHtml(currentListData.getText().replace(contraintCapital, "<b>"+contraintCapital+"</b>"));
        }

        viewHolder.textView.setText(suggestionText);
        viewHolder.imageView.setImageResource(currentListData.getIconRes());
        if (ellipsize) {
            viewHolder.textView.setSingleLine();
            viewHolder.textView.setEllipsize(TextUtils.TruncateAt.END);
        }

        return convertView;
    }

    private CharSequence fromHtml(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(text);
        }
    }

    private class SuggestionsViewHolder {

        TextView textView;
        ImageView imageView;

        public SuggestionsViewHolder(View convertView) {
            textView = (TextView) convertView.findViewById(R.id.suggestion_text);
            imageView = (ImageView) convertView.findViewById(R.id.suggestion_icon);
        }
    }
}