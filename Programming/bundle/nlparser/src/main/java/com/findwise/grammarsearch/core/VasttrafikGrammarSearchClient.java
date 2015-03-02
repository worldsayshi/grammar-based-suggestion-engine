package com.findwise.grammarsearch.core;

import com.findwise.crescent.model.Location;
import com.findwise.crescent.model.TripList;
import com.findwise.crescent.rest.MeansOfTransport;
import com.findwise.crescent.rest.VasttrafikCacheService;
import com.findwise.crescent.rest.VasttrafikQuery;
import com.findwise.crescent.rest.VasttrafikRestClient;
import java.util.*;
import org.grammaticalframework.pgf.*;

/**
 *
 * @author per.fredelius
 */
public class VasttrafikGrammarSearchClient implements GrammarSearchClient<TripList> {

    private PGF pgf;
    
    public VasttrafikGrammarSearchClient (PGF pgf) {
        this.pgf = pgf;
        Map<String, Concr> languages = pgf.getLanguages();
        
        for (Concr language : languages.values()) {
            language.addLiteral("Symb", new NercLiteralCallback());
        }
    }
    
    // Move to VasttrafikGrammarSearchClient
    VasttrafikCacheService cacheService = new VasttrafikCacheService();
    VasttrafikRestClient vasttrafikclient = new VasttrafikRestClient(cacheService);
    
    // Move to VasttrafikGrammarSearchClient
    @Override
    public TripList performQuery (String question, 
            String lang /* = "VasttrafikEngConcat"*/) throws ParseError {
        
        List<Expr> exprs = parseToExpression(question,lang);
        if(exprs.isEmpty()){
            throw new Error("TODO");
        }
        Expr expr = exprs.get(0);
        Concr apiLang = pgf.getLanguages().get("VasttrafikApi");
        String apiLinearization = apiLang.linearize(expr);
        VasttrafikQuery q = parseVasttrafikApi(apiLinearization);
        if(q.from==null || q.to==null){
            // Not complete query - no route
            TripList tripList = new TripList();
            tripList.setTrip(new ArrayList());
            return tripList;
        }
        return vasttrafikclient.findConnections(q);
    }
    
    // Move to VasttrafikGrammarSearchClient
    List<Expr> parseToExpression(String question, String parseLang) throws ParseError {
        Iterable<ExprProb> exprProbs;
        List<Expr> expressions = new ArrayList<>();
        exprProbs = pgf.getLanguages()
                .get(parseLang)
                .parse(
                        pgf.getStartCat(), question);
        for (ExprProb exprProb : exprProbs) {
            Expr expr = exprProb.getExpr();
            expressions.add(expr);
        }
        return expressions;
    }
    
    
    private VasttrafikQuery buildQuery (Map<String,String> inputParams){
                Date date = new Date(System.currentTimeMillis() + Integer.parseInt(inputParams.get("offset")) * 60000);
        boolean departingDate = inputParams.get("reference").equals("departure");
        EnumSet<MeansOfTransport> usedTransportMeans = EnumSet.noneOf(MeansOfTransport.class);
        if(inputParams.get("mean").equals("all") || inputParams.get("mean").equals("train")) {usedTransportMeans.add(MeansOfTransport.Train);}
        if(inputParams.get("mean").equals("all") || inputParams.get("mean").equals("bus")) {usedTransportMeans.add(MeansOfTransport.Bus);}
        if(inputParams.get("mean").equals("all") || inputParams.get("mean").equals("boat")) {usedTransportMeans.add(MeansOfTransport.Boat);}
        if(inputParams.get("mean").equals("all") || inputParams.get("mean").equals("tram")) {usedTransportMeans.add(MeansOfTransport.Tram);}
        
        // changed getBestMatchStop to getBestMatch, wondering if it's gonna impact the search 
        Location src = inputParams.containsKey("from") ? vasttrafikclient.getBestMatch(inputParams.get("from")) : null;
        Location dest = inputParams.containsKey("to") ? vasttrafikclient.getBestMatch(inputParams.get("to")) : null;
                
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

    @Override
    public List<String> getLanguages() {
        return new ArrayList<>(pgf.getLanguages().keySet());
    }
}
