package com.findwise.crescent.rest;

import java.util.Date;
import java.util.EnumSet;
import java.util.Set;

/**
 *
 * @author marcin.goss
 */
public class VasttrafikQuery {

    public final String from;
    public final String to;
    public final Date date;
    public final boolean departingDate;
    public final EnumSet<MeansOfTransport> usedTransportMeans;

    public VasttrafikQuery(String from, String to, Date date, boolean departingDate, EnumSet<MeansOfTransport> usedTransportMeans) {
        this.from = from;
        this.to = to;
        this.date = date;
        this.departingDate = departingDate;
        this.usedTransportMeans = usedTransportMeans;
    }
}
