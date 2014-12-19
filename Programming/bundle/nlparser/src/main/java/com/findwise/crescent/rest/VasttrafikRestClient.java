package com.findwise.crescent.rest;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.findwise.crescent.model.CoordLocation;
import com.findwise.crescent.model.Location;
import com.findwise.crescent.model.LocationList;
import com.findwise.crescent.model.LocationListWrapper;
import com.findwise.crescent.model.StopLocation;
import com.findwise.crescent.model.TripList;
import com.findwise.crescent.model.TripListWrapper;

/**
 * A simple Vasttrafik REST client, uses the model classes as Jackson databind
 * POJOs.
 * 
 * @author mrb
 * 
 */
public class VasttrafikRestClient {
    private static final String LOCATION_NAME_URL = "http://api.vasttrafik.se/bin/rest.exe/v1/location.name";
    private static final String TRIP_URL = "http://api.vasttrafik.se/bin/rest.exe/v1/trip";
    private static final String AUTH_KEY = "9e3bd1d1-6904-4b61-9336-b9d05c4e549d";
    private static final String URL_APPENDED_PART = "?authKey=" + AUTH_KEY
            + "&format=json";

    /**
     * Get the locations for given user input.
     * 
     * @param input
     * @return
     */
    public LocationList getLocationList(String input) {
        String url = LOCATION_NAME_URL + URL_APPENDED_PART + "&input=" + input;
        ObjectMapper mapper = new ObjectMapper();
        try {
            LocationListWrapper llw = mapper.readValue(new URL(url),
                    LocationListWrapper.class);
            return llw.getLocationList();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Find connections from src to dest for the given date.
     * 
     * @param src
     * @param dest
     * @param date
     *            may be null (will search using current time)
     * @return
     */
    public TripList findConnections(Location src, Location dest, Date date) {
        String url = buildTripUrl(src, dest, date);
        url = url.replaceAll("\\s+", "%20");
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        try {
            TripListWrapper tlw = mapper.readValue(new URL(url),
                    TripListWrapper.class);
            return tlw.getTripList();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String buildTripUrl(Location src, Location dest, Date date) {
        StringBuilder sb = new StringBuilder(TRIP_URL + URL_APPENDED_PART);
        if (src instanceof StopLocation) {
            sb.append("&originId=" + ((StopLocation) src).getId());
        } else {
            sb.append("&originCoordName=" + src.getName());
            sb.append("&originCoordLat=" + src.getLatitude());
            sb.append("&originCoordLong=" + src.getLongitude());
        }
        if (dest instanceof StopLocation) {
            sb.append("&destId=" + ((StopLocation) dest).getId());
        } else {
            sb.append("&destCoordName=" + dest.getName());
            sb.append("&destCoordLat=" + dest.getLatitude());
            sb.append("&destCoordLong=" + dest.getLongitude());
        }
        if (date != null) {
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
            String dateFormatted = sdfDate.format(date);
            String timeFormatted = sdfTime.format(date);
            sb.append("&date=" + dateFormatted);
            sb.append("&time=" + timeFormatted);
        }
        return sb.toString();
    }

    /**
     * Get the best, i.e. the first match (bus or tram stop in this case).
     * 
     * @param input
     * @return
     */
    public StopLocation getBestMatchStop(String input) {
        LocationList ll = getLocationList(input);
        return ll.getStopLocations().get(0);
    }

    /**
     * Get the best, i.e. the first match (address, point of interest...)
     * 
     * @param input
     * @return
     */
    public CoordLocation getBestMatchCoord(String input) {
        LocationList ll = getLocationList(input);
        return ll.getCoordLocations().get(0);
    }

    public static void main(String[] args) {
        VasttrafikRestClient client = new VasttrafikRestClient();
        Location src = client.getBestMatchStop("Svingeln");
        // Location dest = client.getBestMatchCoord("Opaltorget");
        Location dest = client.getBestMatchCoord("Centralstationen");

        Calendar c = GregorianCalendar.getInstance();
        c.set(GregorianCalendar.HOUR_OF_DAY,
                c.get(GregorianCalendar.HOUR_OF_DAY) + 3);

        System.out.println(client.findConnections(src, dest, c.getTime()));
        System.out.println(client.findConnections(src, dest, null));
    }
}
