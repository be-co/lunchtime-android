package com.tbaehr.lunchtime;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.tbaehr.lunchtime.model.Offer;
import com.tbaehr.lunchtime.model.Offers;
import com.tbaehr.lunchtime.model.Restaurant;
import com.tbaehr.lunchtime.utils.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
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

    private static final String KEY_NEARBY_OFFERS = "nearby_offers_%s";

    private static final String KEY_RESTAURANT = "restaurant_%s";

    private static final String KEY_OFFER = "offer_%s";

    private static final String KEY_OFFER_UPDATED = "offer_updated_%s";

    private static final String URI_NEARBY_RESTAURANTS = "http://www.c-c-w.de/fileadmin/ccw/user_upload/android/json/nearby_restaurants_%s.json";

    private static final String URI_RESTAURANT = "http://www.c-c-w.de/fileadmin/ccw/user_upload/android/json/restaurant_%s.json";

    private static final String URI_OFFER = "http://www.c-c-w.de/fileadmin/ccw/user_upload/android/json/offers_%s.json";

    public interface OfferLoadJobListener {
        void onDownloadStarted();

        void onDownloadFailed(String message);

        void onNewOffersDownloaded(List<Offers> offersList);
    }

    public Restaurant getRestaurant(String restaurantID) {
        return createRestaurant("restaurant_" + restaurantID + ".json");
    }

    public void syncOffers(final OfferLoadJobListener callback) {
        final String uriSync = String.format(URI_NEARBY_RESTAURANTS, "weiterstadt");
        final String keySync = String.format(KEY_NEARBY_OFFERS, "weiterstadt");

        new AsyncTask<Void, Void, Void>() {
            private boolean dataSetChanged = false;
            @Override
            protected Void doInBackground(Void... params) {
                // Try to download restaurants json from server
                String jsonDownloaded = downloadTextFromServer(uriSync);

                // Server answered with json -> save to cache and update offers
                if (jsonDownloaded != null) {
                    storeToCache(keySync, jsonDownloaded);
                    Map<String, String> nearbyRestaurantKeys = parseNearbyRestaurantKeys(jsonDownloaded);
                    for (String restaurantKey : nearbyRestaurantKeys.keySet()) {
                        dataSetChanged = dataSetChanged || updateOffers(restaurantKey, nearbyRestaurantKeys.get(restaurantKey), callback);
                    }
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (dataSetChanged) {
                    List<Offers> offersList = null;
                    offersList = loadOffersFromCache();
                    callback.onNewOffersDownloaded(offersList);
                }
                super.onPostExecute(aVoid);
            }
        }.execute();
    }

    public List<Offers> loadOffersFromCache() {
        final String keySync = String.format(KEY_NEARBY_OFFERS, "weiterstadt");
        List<Offers> offersList = new ArrayList<>();
        String jsonNearbyRestaurantsCached = loadFromCache(keySync);
        if (jsonNearbyRestaurantsCached != null) {
            Map<String, String> nearbyRestaurantKeys = parseNearbyRestaurantKeys(jsonNearbyRestaurantsCached);
            for (String restaurantKey : nearbyRestaurantKeys.keySet()) {
                Offers offers = loadOffersFromCache(restaurantKey);
                if (offers != null) {
                    offersList.add(offers);
                }
            }
        }
        return offersList;
    }

    private Offers loadOffersFromCache(String restaurantKey) {
        final String keyRestaurant = String.format(KEY_OFFER, restaurantKey);
        String jsonOffers = loadFromCache(keyRestaurant);

        if (jsonOffers != null) {
            try {
                return parseOffersFromJson(jsonOffers);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private boolean updateOffers(String restaurantKey, String dateUpdated, final OfferLoadJobListener callback) {
        final String uriRestaurantOffers = String.format(URI_OFFER, restaurantKey);
        final String keyUpdated = String.format(KEY_OFFER_UPDATED, restaurantKey);

        Date cachedDate = DateUtils.createDateFromString(loadFromCache(keyUpdated));
        Date downloadDate = DateUtils.createDateFromString(dateUpdated);

        String jsonOffers;
        if (downloadDate != null && (cachedDate == null || downloadDate.after(cachedDate))) {
            callback.onDownloadStarted();
            jsonOffers = downloadTextFromServer(uriRestaurantOffers);
            if (jsonOffers != null) {
                try {
                    Offers offers = parseOffersFromJson(jsonOffers);
                    if (offers != null) {
                        storeToCache(String.format(KEY_OFFER, restaurantKey), jsonOffers);
                        storeToCache(keyUpdated, dateUpdated);
                        return true;
                    }
                } catch (JSONException jsonException) {
                    String message = "Failed to download " + restaurantKey + " updated "+ dateUpdated;
                    callback.onDownloadFailed(message);
                }
            }
        }
        return false;
    }

    private Map<String, String> parseNearbyRestaurantKeys(@NonNull String json) {
        Map<String, String> nearbyRestaurants = new HashMap<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int index = 0; index < jsonArray.length(); index++) {
                JSONObject restaurant = jsonArray.getJSONObject(index);
                String key = restaurant.getString("key");
                String lastUpdated = restaurant.getString("lastUpdated");
                nearbyRestaurants.put(key, lastUpdated);
            }
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }

        return nearbyRestaurants;
    }

    private Offers parseOffersFromJson(@NonNull String json) throws JSONException {
        Offers offersResult = null;
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
        offersResult = new Offers(restaurantTitle, restaurantDescription, offers);

        return offersResult;
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

    @Deprecated
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
            // TODO: Do not use parseJsonFromAssets, fetch from server instead
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
        if (contains(title, "Lasagne", "Rippchen", "Wildgulasch", "Hack", "bratwürstchen", "wurst", "Schinken", "Jäger", "Schwein", "Speck", "Leber", "Schnitzel", "Carne", "Hacksteak", "Frikadelle", "frikadelle", "Bolognese", "Lende", "Gulasch", "Geschnetzeltes", "Fleisch", "Krustenbraten")) {
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
