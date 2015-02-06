/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.findwise.crescent.rest;

import java.util.Date;

/**
 *
 * @author marcin.goss
 */
public class VasttrafikQuery {

    public final String from;
    public final String to;
    public final Date date;
    public final boolean departingDate;
    public final boolean useTrain;
    public final boolean useBus;
    public final boolean useTram;
    public final boolean useBoat;

    public VasttrafikQuery(String from, String to, Date date, boolean departingDate, boolean useTrain, boolean useBus, boolean useTram, boolean useBoat) {
        this.from = from;
        this.to = to;
        this.date = date;
        this.departingDate = departingDate;
        this.useTrain = useTrain;
        this.useBus = useBus;
        this.useTram = useTram;
        this.useBoat = useBoat;
    }
}
