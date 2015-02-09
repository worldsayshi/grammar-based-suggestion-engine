package com.findwise.crescent.rest;

import com.findwise.crescent.model.Location;
import java.util.Date;
import java.util.EnumSet;
import java.util.Set;

/**
 *
 * @author marcin.goss
 */
public class VasttrafikQuery {

    public final Location from;
    public final Location to;
    public final Date date;
    public final boolean isDepartureDate;
    public final EnumSet<MeansOfTransport> usedTransportMeans;

    public VasttrafikQuery(Location from, Location to, Date date, boolean isDepartureDate, EnumSet<MeansOfTransport> usedTransportMeans) {
        this.from = from;
        this.to = to;
        this.date = date;
        this.isDepartureDate = isDepartureDate;
        this.usedTransportMeans = usedTransportMeans;
    }
}
