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

import java.util.Map;

/**
 * Created by timo.baehr@gmail.com on 27.12.16.
 */
public class Restaurant {

    private String name;

    private String shortDescription, longDescription;

    private String locationDescription;

    private Map<Integer, int[]> openingTimes;

    private String phoneNumber;

    private String email;

    private String url;

    private String[] photoUrls;

    public Restaurant(String name,
                      String shortDescription,
                      String longDescription,
                      String locationDescription,
                      Map<Integer, int[]> openingTimes,
                      String phoneNumber, String email, String url, String[] photoUrls) {
        this.name = name;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.locationDescription = locationDescription;
        this.openingTimes = openingTimes;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.url = url;
        this.photoUrls = photoUrls;
    }

    public String getName() {
        return name;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public String getOpeningTimeDescription(int weekDay) {
        long openingTime = openingTimes.get(weekDay)[0];
        long hours = openingTime / 60;
        long minutes = openingTime - (hours * 60);
        return ""+ hours + ":" + minutes;
    }

    public String getClosingTimeDescription(int weekDay) {
        long openingTime = openingTimes.get(weekDay)[1];
        long hours = openingTime / 60;
        long minutes = openingTime - (hours * 60);
        return ""+ hours + ":" + minutes;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getUrl() {
        return url;
    }
}
