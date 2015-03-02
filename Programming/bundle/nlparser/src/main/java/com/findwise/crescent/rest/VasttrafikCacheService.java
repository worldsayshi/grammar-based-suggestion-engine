package com.findwise.crescent.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.findwise.crescent.model.CoordLocation;
import com.findwise.crescent.model.LocationList;
import com.findwise.crescent.model.StopLocation;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;

/**
 * Vasttrafik caching service.
 *
 * @author mrb
 */
public class VasttrafikCacheService implements Runnable {

    private static final String DEFAULT_CACHE_FILEPATH = "cache.json";
    private static final long DEFAULT_SLEEP_TIME_MILIS = 10000;

    private static final String ID_FIELD = "id";
    private static final String NAME_FIELD = "name";
    private static final String PHONETIC_FIELD = "phonetic";
    private static final String TYPE_FIELD = "type";
    private static final String LOCATION_LAT_D_FIELD = "location_lat_d";
    private static final String LOCATION_LON_D_FIELD = "location_lon_d";
    private static final String LOCATION_INDEX_I_FIELD = "location_index_i";
    private static final String LOCATION_TYPE_S_FIELD = "location_type_s";

    private Map<String, Boolean> cache = Collections.synchronizedMap(new TreeMap<String, Boolean>());
    private final ObjectMapper mapper = new ObjectMapper();
    private final SolrServer solrServer = new HttpSolrServer("http://localhost:4567/solr-instrucs/names");
    private final Thread thread;

    public VasttrafikCacheService() {
        thread = new Thread(this);
        thread.start();
    }

    private synchronized void readCache(String filepath) {
        try {
            cache = mapper.readValue(new File(filepath), Map.class);
        } catch (IOException ex) {
            Logger.getLogger(VasttrafikCacheService.class.getName()).log(Level.SEVERE, null, ex);
        }
        cache = Collections.synchronizedMap(new TreeMap<String, Boolean>());
    }

    private void writeCache(String filepath) {
        try {
            synchronized (cache) {
                mapper.writeValue(new File(filepath), cache);
            }
        } catch (IOException ex) {
            Logger.getLogger(VasttrafikCacheService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Used by the REST client to retrieve the location list from the cache.
     *
     * @param input
     * @return
     */
    public LocationList getLocationList(String input) {
        synchronized (cache) {
            if (cache.containsKey(input)) {
                if (cache.get(input)) {
                    return readLocationFromSolr(input);
                } else {
                    return LocationList.emptyLocationList();
                }
            }
            return null;
        }
    }

    private LocationList readLocationFromSolr(String input) {
        LocationList ll = new LocationList();
        List<StopLocation> stops = new LinkedList<>();
        List<CoordLocation> coords = new LinkedList<>();

        SolrQuery sq = new SolrQuery();
        sq.setQuery(input);
        QueryResponse response;
        try {
            response = solrServer.query(sq);
            for (SolrDocument sd : response.getResults()) {
                if ("Station".equals(sd.get(TYPE_FIELD))) {
                    StopLocation sl = new StopLocation();
                    sl.setName((String) sd.get(NAME_FIELD));
                    sl.setId((String) sd.get(ID_FIELD));
                    sl.setLatitude((double) sd.get(LOCATION_LAT_D_FIELD));
                    sl.setLongitude((double) sd.get(LOCATION_LON_D_FIELD));
                    sl.setIdx((int) sd.get(LOCATION_INDEX_I_FIELD));
                    stops.add(sl);
                } else if ("Coords".equals(sd.get(TYPE_FIELD))) {
                    CoordLocation cl = new CoordLocation();
                    cl.setName((String) sd.get(NAME_FIELD));
                    cl.setLatitude((double) sd.get(LOCATION_LAT_D_FIELD));
                    cl.setLongitude((double) sd.get(LOCATION_LON_D_FIELD));
                    cl.setIdx((int) sd.get(LOCATION_INDEX_I_FIELD));
                    cl.setType((String) sd.get(LOCATION_TYPE_S_FIELD));
                    coords.add(cl);
                }
            }
        } catch (SolrServerException ex) {
            Logger.getLogger(VasttrafikCacheService.class.getName()).log(Level.SEVERE, null, ex);
        }
        ll.setStopLocations(stops);
        ll.setCoordLocations(coords);
        return ll;
    }

    /**
     * Used by the REST client to write locations got from API to our cache.
     *
     * @param ll
     * @param input
     */
    void writeLocationsToSolr(LocationList ll, String input) {
        try {
            int numberOfLocations = 0;
            for (StopLocation sl : ll.getStopLocations()) {
                SolrQuery sq = new SolrQuery();
                sq.setQuery("id:" + sl.getId());
                final QueryResponse res = solrServer.query(sq);
                if (res.getResults().isEmpty()) {
                    SolrInputDocument document = new SolrInputDocument();
                    document.addField(ID_FIELD, sl.getId());
                    document.addField(NAME_FIELD, sl.getName());
                    document.addField(PHONETIC_FIELD, sl.getName());
                    document.addField(TYPE_FIELD, "Station");
                    document.addField(LOCATION_LAT_D_FIELD, sl.getLatitude());
                    document.addField(LOCATION_LON_D_FIELD, sl.getLongitude());
                    document.addField(LOCATION_INDEX_I_FIELD, sl.getIdx());
                    solrServer.add(document);
                }
                ++numberOfLocations;
            }
            for (CoordLocation cl : ll.getCoordLocations()) {
                SolrQuery sq = new SolrQuery();
                final String id = "COORD_" + cl.getType() + "_" + cl.getName();
                sq.setQuery("id:" + id); // FIXME wild guess actually
                final QueryResponse res = solrServer.query(sq);
                if (res.getResults().isEmpty()) {
                    SolrInputDocument document = new SolrInputDocument();
                    document.addField(ID_FIELD, id);
                    document.addField(NAME_FIELD, cl.getName());
                    document.addField(PHONETIC_FIELD, cl.getName());
                    document.addField(TYPE_FIELD, "Coord");
                    document.addField(LOCATION_LAT_D_FIELD, cl.getLatitude());
                    document.addField(LOCATION_LON_D_FIELD, cl.getLongitude());
                    document.addField(LOCATION_INDEX_I_FIELD, cl.getIdx());
                    document.addField(LOCATION_TYPE_S_FIELD, cl.getType());
                    solrServer.add(document);
                }
                ++numberOfLocations;
            }
            solrServer.commit();
            synchronized (cache) {
                if (numberOfLocations > 0) {
                    cache.put(input, Boolean.TRUE);
                } else {
                    cache.put(input, Boolean.FALSE);
                }
            }
        } catch (SolrServerException | IOException ex) {
            Logger.getLogger(VasttrafikCacheService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        readCache(DEFAULT_CACHE_FILEPATH);
        while (true) {
            writeCache(DEFAULT_CACHE_FILEPATH);
            Logger.getLogger(VasttrafikCacheService.class.getName()).log(Level.INFO, "Cache written to " + DEFAULT_CACHE_FILEPATH + ", going to sleep for {0} milis", DEFAULT_SLEEP_TIME_MILIS);
            try {
                thread.sleep(DEFAULT_SLEEP_TIME_MILIS);
            } catch (InterruptedException ex) {
                Logger.getLogger(VasttrafikCacheService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
