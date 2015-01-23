package com.findwise.grammarsearch.web;

import com.findwise.grammarsearch.SearchConfig;
import javax.ws.rs.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author per.fredelius
 */
@Path("/")
public class JSONService {
    
    
    public JSONService () {
        ApplicationContext ctx
                = new AnnotationConfigApplicationContext(SearchConfig.class);
        ctx.getBeansOfType(null)
    }
}
