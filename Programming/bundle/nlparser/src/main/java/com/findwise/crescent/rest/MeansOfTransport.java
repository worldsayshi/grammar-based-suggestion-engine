package com.findwise.crescent.rest;

/**
 *
 * @author marcin.goss
 */
public enum MeansOfTransport {
    Boat(new String[]{"useBoat"}),
    Tram(new String[]{"useTram"}),
    Train(new String[]{"useVas","useLDTrain","useRegTrain"}),
    Bus(new String[]{"useBus"});
    
    private final String[] restParamNames;

    private MeansOfTransport(String[] restParamNames) {
        this.restParamNames = restParamNames;
    }

    /**
     * @return the restParamName
     */
    public String[] getRestParamNames() {
        return restParamNames;
    }
}
