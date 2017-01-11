package com.tbaehr.lunchtime;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;

import com.propaneapps.tomorrow.presenter.BasePresenter;
import com.tbaehr.lunchtime.model.Constants;
import com.tbaehr.lunchtime.model.Offer;
import com.tbaehr.lunchtime.model.Offers;
import com.tbaehr.lunchtime.model.Restaurant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by timo.baehr@gmail.com on 27.12.16.
 */
public class DataProvider {

    private static final String KEY_NEARBY_OFFERS = "nearby_offers";

    private static final String KEY_RESTAURANT = "restaurant_%s$1";

    private static final String KEY_OFFER = "offer_%s$1";

    private static final String URI_NEARBY_RESTAURANTS = "http://www.c-c-w.de/fileadmin/ccw/user_upload/android/json/nearby_restaurants_weiterstadt.json";

    private static final String URI_RESTAURANT = "http://www.c-c-w.de/fileadmin/ccw/user_upload/android/json/restaurant_%s$1.json";

    private static final String URI_OFFER = "http://www.c-c-w.de/fileadmin/ccw/user_upload/android/json/offers_%s$1.json";

    public Restaurant getRestaurant(String restaurantID) {
        return createRestaurant("restaurant_" + restaurantID + ".json");
    }

    private void getTextAsync(final String uri, BasePresenter callback, final String... arguments) {
        new AsyncTask<Void, Void, Void>() {
            String json;
            @Override
            protected Void doInBackground(Void... params) {
                json = downloadTextFromServer(String.format(uri, arguments));
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                // TODO: update the UI by calling the callback (this is executed on UI thread)
                super.onPostExecute(aVoid);
            }
        }.execute();
    }

    private String getText(String cacheKey, String uri, String... arguments) {
        String json = loadFromCache(cacheKey);
        if (json == null) {
            json = downloadTextFromServer(String.format(uri, arguments));
        }

        return json;
    }

    public Map<String, Date> getNearbyRestaurants(double latitude, double longitude, int radius) {
        Map<String, Date> nearbyRestaurants = new HashMap<>();
        try {
            // TODO: Fix network on MAIN_THREAD
            String json = getText(KEY_NEARBY_OFFERS, URI_NEARBY_RESTAURANTS);
            if (json == null) {
                return nearbyRestaurants;
            }
            //String json = parseJsonFromAssets("restaurants/nearby_restaurants_weiterstadt.json");
            storeToCache(KEY_NEARBY_OFFERS, json);

            JSONArray jsonArray = new JSONArray(json);
            for (int index = 0; index < jsonArray.length(); index++) {
                JSONObject restaurant = jsonArray.getJSONObject(index);
                String key = restaurant.getString("key");
                String lastUpdated = restaurant.getString("lastUpdated");
                DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
                try {
                    Date date = dateFormat.parse(lastUpdated);
                    nearbyRestaurants.put(key, date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return nearbyRestaurants;
    }

    public Offers getOffers(String restaurantKey) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        Offers nearbyOffers = null;
        try {
            String json = parseJsonFromAssets("offers/offers_" + restaurantKey + ".json");
            JSONObject restaurantObject = new JSONObject(json);
            String restaurantTitle = restaurantObject.getString("title");
            String restaurantDescription = restaurantObject.getString("description");
            JSONArray restaurantOffersArray = restaurantObject.getJSONArray("offers");
            List<Offer> offers = new ArrayList<>();
            for (int offerIndex = 0; offerIndex < restaurantOffersArray.length(); offerIndex++) {
                JSONObject offerObject = restaurantOffersArray.getJSONObject(offerIndex);
                String offerTitle = offerObject.getString("title");
                String offerDescription = offerObject.getString("description");
                int offerPrize = offerObject.getInt("prize");
                String starts = offerObject.getString("starts");
                String ends = offerObject.getString("ends");
                Offer.Category category = Offer.Category.valueOf(offerObject.getString("category").toUpperCase());
                JSONArray ingredientsArray = offerObject.getJSONArray("ingredients");
                Set<Offer.Ingredient> ingredients = new HashSet<>();
                for (int ingrIndex = 0; ingrIndex < ingredientsArray.length(); ingrIndex++) {
                    ingredients.add(Offer.Ingredient.valueOf(ingredientsArray.getString(ingrIndex)));
                }

                autoSearchForTags(ingredients, offerTitle);
                autoSearchForTags(ingredients, offerDescription);
                Offer offer = new Offer(
                        offerTitle,
                        offerDescription,
                        offerPrize,
                        starts, ends,
                        category,
                        ingredients);
                Offer.ValidationState validationState = offer.getValidationState();
                if (validationState.equals(Offer.ValidationState.NOW_VALID) ||
                        validationState.equals(Offer.ValidationState.SOON_VALID)) {
                    offers.add(offer);
                }
            }
            nearbyOffers = new Offers(restaurantTitle, restaurantDescription, offers);
        } catch (JSONException jsonException) {
            Log.e(this.getClass().getCanonicalName(), jsonException.getMessage());
        }

        return nearbyOffers;
    }

    private String loadFromCache(String key) {
        Context context = LunchtimeApplication.getContext();
        String name = "cache";
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, MODE_PRIVATE);

        return sharedPreferences.getString(key, null);
    }

    private void storeToCache(String key, String value) {
        Context context = LunchtimeApplication.getContext();
        String name = "cache";
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(key, value).apply();
    }

    private String downloadTextFromServer(String path) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            URL url = new URL(path);

            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = in.readLine()) != null) {
                stringBuilder.append(line);
            }
            in.close();
            return stringBuilder.toString();
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
        return null;
    }

    private int[] convertToIntArray(JSONArray array) throws JSONException {
        if (array.length() == 0) {
            return null;
        }

        int[] listOfInts = new int[array.length()];
        for (int i = 0; i < listOfInts.length; i++) {
            listOfInts[i] = array.getInt(i);
        }
        return listOfInts;
    }

    private String[] convertToArray(JSONArray array) throws JSONException {
        if (array.length() == 0) {
            return null;
        }

        String[] listOfStrings = new String[array.length()];
        for (int i = 0; i < listOfStrings.length; i++) {
            listOfStrings[i] = array.getString(i);
        }
        return listOfStrings;
    }

    private String parseJsonFromAssets(String filePath) {
        BufferedReader reader = null;
        String jsonOutput = null;
        try {
            AssetManager assetManager = LunchtimeApplication.getContext().getAssets();
            InputStream is = assetManager.open(filePath);
            reader = new BufferedReader(
                    new InputStreamReader(is, "UTF-8"));

            StringBuilder json = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            jsonOutput = json.toString();
        } catch (IOException ioException) {
            Log.e(this.getClass().getCanonicalName(), ioException.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(this.getClass().getCanonicalName(), "Error closing asset " + filePath);
                }
            }
            return jsonOutput;
        }
    }

    private Restaurant createRestaurant(String fileName) {
        try {
            JSONObject restaurant = new JSONObject(parseJsonFromAssets("restaurants/" + fileName));
            String name = restaurant.getString("title");
            String shortDescription = restaurant.getString("shortDescription");
            String longDescription = restaurant.getString("longDescription");
            String address = restaurant.getString("address");

            JSONObject openingTimesObject = restaurant.getJSONObject("openingTimes");
            JSONArray mondayArray = openingTimesObject.getJSONArray("monday");
            JSONArray tuesdayArray = openingTimesObject.getJSONArray("tuesday");
            JSONArray wednesdayArray = openingTimesObject.getJSONArray("wednesday");
            JSONArray thursdayArray = openingTimesObject.getJSONArray("thursday");
            JSONArray fridayArray = openingTimesObject.getJSONArray("friday");
            JSONArray saturdayArray = openingTimesObject.getJSONArray("saturday");
            JSONArray sundayArray = openingTimesObject.getJSONArray("sunday");
            Map<Integer, int[]> openingTimes = new HashMap<>();
            openingTimes.put(Calendar.MONDAY, convertToIntArray(mondayArray));
            openingTimes.put(Calendar.TUESDAY, convertToIntArray(tuesdayArray));
            openingTimes.put(Calendar.WEDNESDAY, convertToIntArray(wednesdayArray));
            openingTimes.put(Calendar.THURSDAY, convertToIntArray(thursdayArray));
            openingTimes.put(Calendar.FRIDAY, convertToIntArray(fridayArray));
            openingTimes.put(Calendar.SATURDAY, convertToIntArray(saturdayArray));
            openingTimes.put(Calendar.SUNDAY, convertToIntArray(sundayArray));

            String phoneNumber = restaurant.getString("phoneNumber");
            String email = restaurant.getString("email");
            String website = restaurant.getString("website");
            JSONArray photoUrlsObjects = restaurant.getJSONArray("photoUrls");
            String[] photoUrls = convertToArray(photoUrlsObjects);

            Restaurant gehe = new Restaurant(name, shortDescription, longDescription, address,
                    openingTimes, phoneNumber, email, website, photoUrls);

            return gehe;
        } catch (JSONException jsonException) {
            Log.e(this.getClass().getCanonicalName(), jsonException.getMessage());
        }

        return null;
    }

    private void autoSearchForTags(Set<Offer.Ingredient> ingredientList, String title) {
        if (contains(title, "Rippchen", "Wildgulasch", "Hack", "bratwürstchen", "wurst", "Schinken", "Jäger", "Schwein", "Speck", "Leber", "Schnitzel", "Carne", "Hacksteak", "Frikadelle", "frikadelle", "Bolognese", "Lende", "Gulasch", "Geschnetzeltes", "Fleisch", "Krustenbraten")) {
            if (title.contains("Carne")) {
                if (!title.contains("vom Rind")) {
                    ingredientList.add(Offer.Ingredient.PIG);
                }
            } else {
                ingredientList.add(Offer.Ingredient.PIG);
            }
        }
        if (contains(title, "Wildgeschnetzeltes", "Wildgulasch", "Hack", "Rind", "Carne", "Hacksteak", "Bockwurst")) {
            ingredientList.add(Offer.Ingredient.COW);
        }
        if (contains(title, "Geflügel", "Hähnchen", "Huhn", "Hühner", "Coq", "Truthahn")) {
            ingredientList.add(Offer.Ingredient.CHICKEN);
        }
        if (contains(title, "Penne", "Eierknöpfle", "Cavatelli", "Tagliatelle", "Spaghetti", "Spätzle", "Gnocchi", "schmarrn", "Nudel", "nudel", "Semmelknödel", "Nougatknödel", "Schlutzkrapfen", "Klopse", "Baguette", "Pizza")) {
            ingredientList.add(Offer.Ingredient.GLUTEN);
        }
        if (contains(title, "quark", "schmarrn", "Käse", "Sahne", "gratin", "Rahm", "Remoulade", "schmand")) {
            ingredientList.add(Offer.Ingredient.LACTOSE);
        }
        if (contains(title, "Seelachs", "Seezunge", "Matjes", "Lachs", "Forelle", "Fisch", "fisch")) {
            ingredientList.add(Offer.Ingredient.FISH);
        }
        if (contains(title, " Ei", "Ei ", "Eier", "eier", "Spiegelei", "Majonese", "Eierknöpfle", "Tagliatelle", "Spaghetti", "Spätzle", "Nudel", "nudel")) {
            ingredientList.add(Offer.Ingredient.EGG);
        }
    }

    private boolean contains(String text, String... subtext) {
        for (int i = 0; i < subtext.length; i++) {
            if (text.contains(subtext[i])) {
                return true;
            }
        }
        return false;
    }

}
