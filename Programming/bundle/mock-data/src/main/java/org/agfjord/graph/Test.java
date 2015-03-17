/*
 */
package org.agfjord.graph;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;
import com.mdimension.jchronic.Chronic;
import com.mdimension.jchronic.Options;
import com.mdimension.jchronic.utils.Span;
import java.util.Date;
import java.util.List;

/**
 *
 * @author marcin.goss
 */
public class Test {

    public static void main(String[] args) {

        String[] texts = {
            "on monday at one",
            "on tuesday",
            "tomorrow at six",
            "the day after tomorrow at ten pm",
            "on the 13th of march",
            "next saturday",
            "yesterday at six am",
            "yesterday at six",
            "on monday at 13",
            "tomorrow at 6",
            "the day after tomorrow at 10 pm",
            "yesterday at 6 am",
            "yesterday at 6",
            "at ten past one",
            "at 13:10",
            "16",
            "six",
            "thursday"
        };

        Parser natty = new Parser();
        for (String text : texts) {
            System.out.println(text);

            //natty
            StringBuilder sb = new StringBuilder();
            for (DateGroup gr : natty.parse(text)) {
                for (Date date : gr.getDates()) {
                    sb.append(" ");
                    sb.append(date.toString());
                }
            }
            System.out.println("Natty:" + sb.toString());
            
            //chronic
            Span span = Chronic.parse(text);
            if(span!=null){
                System.out.println("Chronic: " + span.getBeginCalendar().getTime().toString());
            }
            else{
                System.out.println("Chronic:");
            }

            System.out.println("");
        }
    }
}
