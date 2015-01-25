package com.findwise.grammarsearch.core;

import com.findwise.crescent.model.StopLocation;
import com.findwise.crescent.model.TripList;
import com.findwise.crescent.rest.VasttrafikRestClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.grammaticalframework.pgf.Concr;
import org.grammaticalframework.pgf.Expr;
import org.grammaticalframework.pgf.ExprProb;
import org.grammaticalframework.pgf.NercLiteralCallback;
import org.grammaticalframework.pgf.PGF;
import org.grammaticalframework.pgf.ParseError;

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
    VasttrafikRestClient vasttrafikclient = new VasttrafikRestClient();
    
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
        StopLocation from = vasttrafikclient.getBestMatchStop(q.from);
        StopLocation to = vasttrafikclient.getBestMatchStop(q.to);
        return vasttrafikclient.findConnections(from, to, null);
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
        return new VasttrafikQuery(map.get("from"),map.get("to"));
    }
    
    // Move to VasttrafikGrammarSearchClient
    public class VasttrafikQuery {
        public final String from;
        public final String to;
        public VasttrafikQuery (String from, String to) {
            this.from=from;
            this.to=to;
        }
        
    }
    
}
