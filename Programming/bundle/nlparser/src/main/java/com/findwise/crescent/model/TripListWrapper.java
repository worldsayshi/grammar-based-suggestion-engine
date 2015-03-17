package com.findwise.crescent.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TripListWrapper {

    @JsonProperty("TripList")
    private TripList tripList;

    public TripList getTripList() {
        for (Trip trip : tripList.getTrip()) {
            trip.setTripTime(String.valueOf(calculateTripTime(trip)));
            trip.setTransfers(String.valueOf(calculateTransfersCount(trip)));
            removeTransferStages(trip);
        }
        return tripList;
    }
    

    private int calculateTripTime(Trip trip) {
        
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm");
        
        try {
            if (trip.getLegList().isEmpty()) {
                return 0;
            }

            Date start = sdf.parse(trip.getLegList().get(0).getOrigin().getDate() + " " + trip.getLegList().get(0).getOrigin().getTime());
            Date end = sdf.parse(trip.getLegList().get(trip.getLegList().size() - 1).getDestination().getDate() + " " + trip.getLegList().get(trip.getLegList().size() - 1).getDestination().getTime());

            return (int) ((end.getTime() - start.getTime()) / 60000);
        } catch (ParseException ex) {
            Logger.getLogger(TripListWrapper.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    private int calculateTransfersCount(Trip trip) {
        return (trip.getLegList().size() - 1) / 2;
    }
    
    private void removeTransferStages(Trip trip){
        Iterator<Leg> iterator = trip.getLegList().iterator();
        
        int index = 0;
        
        while(iterator.hasNext()){
            Leg leg = iterator.next();
            
            if(leg.getOrigin().getName().equals(leg.getDestination().getName())){
                iterator.remove();
            }
                    
            index++;        
        }
    }

    public void setTripList(TripList tripList) {
        this.tripList = tripList;
    }
}
