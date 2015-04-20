package com.findwise.grammarsearch.core;

import com.findwise.crescent.model.Location;
import com.findwise.crescent.model.TripList;
import com.findwise.crescent.rest.MeansOfTransport;
import com.findwise.crescent.rest.VasttrafikQuery;
import com.findwise.crescent.rest.VasttrafikRestClient;
import java.util.*;
import org.grammaticalframework.pgf.*;

/**
 *
 * @author per.fredelius
 */
public class VasttrafikGrammarSearchClient implements GrammarSearchClient<TripList> {

    
    public VasttrafikGrammarSearchClient () {
    }
    
    VasttrafikRestClient vasttrafikclient = new VasttrafikRestClient();
    
    @Override
    public TripList performQuery (String question, 
            String lang, String apiQuery) throws ParseError {
        
        VasttrafikQuery q = parseVasttrafikApi(apiQuery);
        if(q.from==null || q.to==null){
            // Not complete query - no route
            TripList tripList = new TripList();
            tripList.setTrip(new ArrayList());
            return tripList;
        }
        return vasttrafikclient.findConnections(q);
    }
       
    private VasttrafikQuery buildQuery (Map<String,String> inputParams){
                Date date = new Date(System.currentTimeMillis() + Integer.parseInt(inputParams.get("offset")) * 60000);
        boolean departingDate = inputParams.get("reference").equals("departure");
        EnumSet<MeansOfTransport> usedTransportMeans = EnumSet.noneOf(MeansOfTransport.class);
        if(inputParams.get("mean").equals("all") || inputParams.get("mean").equals("train")) {usedTransportMeans.add(MeansOfTransport.Train);}
        if(inputParams.get("mean").equals("all") || inputParams.get("mean").equals("bus")) {usedTransportMeans.add(MeansOfTransport.Bus);}
        if(inputParams.get("mean").equals("all") || inputParams.get("mean").equals("boat")) {usedTransportMeans.add(MeansOfTransport.Boat);}
        if(inputParams.get("mean").equals("all") || inputParams.get("mean").equals("tram")) {usedTransportMeans.add(MeansOfTransport.Tram);}
        
        Location src = inputParams.containsKey("from") ? vasttrafikclient.getBestMatchStop(inputParams.get("from")) : null;
        Location dest = inputParams.containsKey("to") ? vasttrafikclient.getBestMatchStop(inputParams.get("to")) : null;
                
        return new VasttrafikQuery(src,dest,date, departingDate, usedTransportMeans);
    }
    
    // Move to VasttrafikGrammarSearchClient
    private VasttrafikQuery parseVasttrafikApi (String apiLinearization) {
        String[] rows = apiLinearization.split(";");
        Map<String,String> map = new HashMap<>();
        for (String row : rows) {
            String[] cols = row.split(":");
            if(cols.length==2){
                map.put(cols[0].trim(), cols[1].trim());
            }
        }
        
        return buildQuery(map);
    }    
}
