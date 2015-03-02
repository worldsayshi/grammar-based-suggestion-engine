package com.findwise.crescent.rest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.findwise.crescent.model.*;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.GregorianCalendar;

/**
 * A simple Vasttrafik REST client, uses the model classes as Jackson databind
 * POJOs.
 *
 * @author mrb
 *
 */
public class VasttrafikRestClient {

    private static final String LOCATION_NAME_URL = "http://api.vasttrafik.se/bin/rest.exe/v1/location.name";
    private static final String TRIP_URL = "http://api.vasttrafik.se/bin/rest.exe/v1/trip";
    private static final String AUTH_KEY = "9e3bd1d1-6904-4b61-9336-b9d05c4e549d";
    private static final String URL_APPENDED_PART = "?authKey=" + AUTH_KEY
            + "&format=json";
    private final VasttrafikCacheService cacheService;

    public VasttrafikRestClient(VasttrafikCacheService cacheService) {
        this.cacheService = cacheService;
    }

    /**
     * Get the locations for given user input.
     *
     * @param input
     * @return
     */
    public LocationList getLocationList(String input) {
        String url = LOCATION_NAME_URL + URL_APPENDED_PART + "&input=" + input;
        ObjectMapper mapper = new ObjectMapper();
        try {
            LocationListWrapper llw = mapper.readValue(new URL(url),
                    LocationListWrapper.class);
            return llw.getLocationList();
        } catch (IOException ex) {
            Logger.getLogger(VasttrafikRestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Find connections from src to dest for the given date.
     *
     * @param src
     * @param dest
     * @param date may be null (will search using current time)
     * @return
     */
    public TripList findConnections(VasttrafikQuery params) {
        String url = buildTripUrl(params);
        url = url.replaceAll("\\s+", "%20");
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        try {
            TripListWrapper tlw = mapper.readValue(new URL(url),
                    TripListWrapper.class);
            return tlw.getTripList();
        } catch (IOException ex) {
            Logger.getLogger(VasttrafikRestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private String buildTripUrl(VasttrafikQuery params) {
        StringBuilder sb = new StringBuilder(TRIP_URL + URL_APPENDED_PART);
        Location src = params.from;
        Location dest = params.to;
        if (src instanceof StopLocation) {
            sb.append("&originId=").append(((StopLocation) src).getId());
        } else {
            sb.append("&originCoordName=").append(src.getName());
            sb.append("&originCoordLat=").append(src.getLatitude());
            sb.append("&originCoordLong=").append(src.getLongitude());
        }
        if (dest instanceof StopLocation) {
            sb.append("&destId=").append(((StopLocation) dest).getId());
        } else {
            sb.append("&destCoordName=").append(dest.getName());
            sb.append("&destCoordLat=").append(dest.getLatitude());
            sb.append("&destCoordLong=").append(dest.getLongitude());
        }
        if (params.date != null) {
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
            String dateFormatted = sdfDate.format(params.date);
            String timeFormatted = sdfTime.format(params.date);
            sb.append("&date=").append(dateFormatted);
            sb.append("&time=").append(timeFormatted);
        }
        if (!params.isDepartureDate) {
            sb.append("&searchForArrival=1");
        }

        for (MeansOfTransport mean : MeansOfTransport.values()) {
            if (!params.usedTransportMeans.contains(mean)) {
                for (String paramName : mean.getRestParamNames()) {
                    sb.append("&");
                    sb.append(paramName);
                    sb.append("=0");
                }
            }
        }

        return sb.toString();
    }

    /**
     * Get the best match based on idx value.
     *
     * @param input
     * @return
     */
    public Location getBestMatch(String input) {
        LocationList ll = accessCache(input);
        if (ll == null) {
            return null;
        }
        StopLocation sl = ll.getStopLocations().get(0);
        CoordLocation cl = ll.getCoordLocations().get(0);
        if (cl == null || cl.getIdx() > sl.getIdx()) {
            return sl;
        }
        return cl;
    }

    /**
     * Get the best, i.e. the first match (bus or tram stop in this case).
     *
     * @param input
     * @return
     */
    public StopLocation getBestMatchStop(String input) {
        LocationList ll = accessCache(input);
        if (ll == null) {
            return null;
        }
        return ll.getStopLocations().get(0);
    }

    /**
     * Get the best, i.e. the first match (address, point of interest...)
     *
     * @param input
     * @return
     */
    public CoordLocation getBestMatchCoord(String input) {
        LocationList ll = accessCache(input);
        if (ll == null) {
            return null;
        }
        return ll.getCoordLocations().get(0);
    }

    private StopLocation getBestMatchStopNonCached(String input) {
        LocationList ll = getLocationList(input);
        return ll.getStopLocations().get(0);
    }

    private CoordLocation getBestMatchCoordNonCached(String input) {
        LocationList ll = getLocationList(input);
        return ll.getCoordLocations().get(0);
    }

    private LocationList accessCache(String input) {
        LocationList ll = cacheService.getLocationList(input);
        if (ll == null) {
            ll = getLocationList(input);
            cacheService.writeLocationsToSolr(ll, input);
        } else if (ll.isEmpty()) {
            return null; // not sure
        }
        return ll;
    }

    public static void main(String[] args) {
        VasttrafikCacheService cs = new VasttrafikCacheService();
        VasttrafikRestClient client = new VasttrafikRestClient(cs);

        Calendar c = GregorianCalendar.getInstance();
        c.set(GregorianCalendar.HOUR_OF_DAY,
                c.get(GregorianCalendar.HOUR_OF_DAY) + 3);

//        for (int i = 0; i < 100; i++) {
//            long m = System.currentTimeMillis();
////            Location src = client.getBestMatchStopNonCached("Svingeln");
////            Location dest = client.getBestMatchStopNonCached("Centralstationen");
//            Location src = client.getBestMatchStop("Svingeln");
//            Location dest = client.getBestMatchStop("Centralstationen");
////            System.out.println(client.findConnections(new VasttrafikQuery(src, dest, null, true, EnumSet.allOf(MeansOfTransport.class))));
//            client.findConnections(new VasttrafikQuery(src, dest, null, true, EnumSet.allOf(MeansOfTransport.class)));
//            System.out.println((System.currentTimeMillis() - m));
//        }
        populateCache(client);
    }

    private static void populateCache(VasttrafikRestClient client) {
        String[] stations = {"Akkas Gata", "Alekärrsgatan", "Alelyckan", "Allebyvägen", "Allhelgonakyrkan", "Almedal", "Amalia Jönssons Gata", "Amhult", "Andalen", "Anekdotgatan", "Angered Centrum", "Angered Garaget", "Angereds Kyrka", "Angereds Storåsväg", "Angeredsvinkeln", "Annedalskyrkan", "Anneholmsgatan", "Anticimex", "Aprikosgatan", "Aprilgatan", "Arendal Norra", "Arendal Portal", "Arendal Skans", "Arendal Terminalen", "Arendals Allé", "Arendalsvägen", "Armlängdsgatan", "Aröds Äng", "Askesby", "Askims Kyrka", "Askims Stationsväg", "Askims Svartmosse", "Askims Torg", "Askimsbadet", "Asperö Norra", "Asperö Östra", "Assmundtorp", "Astronomgatan", "Atmosfärgatan", "Axel Dahlströms Torg", "Backa", "Backa Bergö", "Backa Kyrkogata", "Backadalsmotet", "Backered", "Backereds Skola", "Backvägen", "Bagaregårdsskolan", "Balamundi", "Balladgatan", "Bassås", "Batterivägen", "Beatrice Lesslies Gata", "Bellevue", "Bensingatan", "Bergegården", "Berghöjdsgatan", "Bergkristallsgatan", "Bergsgårdsvägen", "Bergsjödalen", "Bergsjösvängen", "Bergsprängaregatan", "Bergums Kyrka", "Beryllgatan", "Berzeliigatan", "Bessemergatan", "Betaniagatan", "Beväringsgatan", "Billdal", "Billdals Gård", "Billdals Kyrka", "Bingared", "Bjurslätts Torg", "Bjurslättsliden", "Bjurslättsskolan", "Björkhöjdsgatan", "Björkrisvägen", "Björkuddsgatan", "Björlanda Kile", "Björlanda Kyrka", "Björnsonsgatan", "Björnåsgatan", "Björsared", "Bli", "Blåsutgatan", "Bockhornsgatan", "Bokekullsgatan", "Bolmörtsgatan", "Bolsten", "Botaniska Trädgården", "Bovallstrandsgatan", "Brandkärr", "Bredfjällsgatan", "Briljantgatan", "Brottkärr", "Brovägen", "Brungatan", "Brunnehagen", "Brunnsbotorget", "Brunnsgatan", "Brunnsparken", "Brunstorp", "Brunstorpsnäs", "Bräcke Östergård", "Brända Berget", "Brännö Husvik", "Brännö Rödsten", "Bur", "Burmabacken", "Burmans Gata", "Byfogdegatan", "Bygdegården", "Båtsman Grås Gata", "Båtsman Hisings Gata", "Båtsman Kapers Gata", "Bäckebol Köpcentrum", "Bäckebol Norra", "Bäckebol Södra", "Bäckebols Gård", "Bäckebolsmotet", "Bäckedalsvägen", "Bäckeliden", "Bärby", "Bärby Korsväg", "Bärby Norra", "Bögatan", "Calvert", "Carlandersplatsen", "Centralstationen", "Chalmers", "Chalmers Tvärgata", "Chapmans Torg", "Cold Stores", "Dalagärde", "Dalavägen", "Danaplatsen", "Datavägen", "Datavägen Södra", "Datavägen Östra", "Delabäcken", "Delsjömotet", "Deltavägen", "Depå Frihamnen", "Djurgårdsskolan", "Dockeredsvägen", "Doktor Forselius Gata", "Doktor Fries Torg", "Doktor Sydows Gata", "Doktor Weltzins Gata", "Doktor Westrings Gata", "Domkyrkan", "Donsö", "Ejdergatan", "Ekedal", "Ekeredsvägen", "Eketrägatan", "Eketrägatan Vändslinga", "Ekmanska", "Eliebergsvägen", "Elisedal", "Ellered", "Ellesbo", "Enebacken", "Engdahlsgatan", "Eriksbergs Färjeläge", "Eriksbergstorget", "Eriksbo Skola", "Eriksbo Västergärde", "Eriksbo Östergärde", "Eriksdal", "Exportgatan", "F O Peterssons Gata", "Fagerängsgatan", "Falutorget", "Finlandsvägen", "Fiskebäcks Hamn", "Fiskebäcks Småbåtshamn", "Fiskebäcksskolan", "Fjäderharvsgatan", "Fjällbo", "Fjällbruden", "Fjällgatan", "Fjällhavren", "Fjällkåpan", "Fjällskolan", "Fjällsyran", "Framnäsgatan", "Fridhems Kyrkogård", "Friedländers Gata", "Frihamnen", "Frimästaregatan", "Friskväderstorget", "Fräntorp", "Frölunda Smedja", "Frölunda Torg", "Frölunda Torg Vändslinga", "Furåsvägen/Trekanten", "Fyrktorget", "Fyrskeppsvägen", "Fyrspannvägen", "Fågelvägen", "Fältspatsgatan", "Fänkålsgatan", "Färgfabrikerna", "Galileis Gata", "Gamla Hangaren", "Gamla Lillebyvägen", "Gamla Torslandavägen", "Gamlestaden station", "Gamlestadstorget", "Gamlestadstorget Ers.hpl", "Garaget Högstena", "Gasverket", "Gerrebacka Gård", "Getebergsäng", "Gillegatan", "Gjutegården", "Gjutjärnsgatan", "Glöstorpsvägen", "Godhemsgatan", "Godsgatan", "Gossbydalsvägen", "Granhäll", "Granliden", "Grevebergsgatan", "Grevegårdsvägen", "Grimbo", "Gropegårdsgatan", "Gropens Gård", "Gråbacka", "Gråberget", "Gräddgatan", "Gröna Viken", "Grönsakstorget", "Gunnaredsvägen", "Gunnesby Bro", "Gunnesby Kalsbogård", "Gunnesby Skola", "Gunnesgärde", "Gunnilse Skola", "Gustavsplatsen", "Gårdsten Centrum", "Gårdstensliden", "Gåsmossen", "Gärdsås Torg", "Gärdsåsgatan", "Göddered", "Göpåsgatan", "Göpåstunneln", "Götaplatsen", "Göteborg C", "Hagakyrkan", "Hagelvädersgatan", "Hagen", "Halleröd", "Halleskärsgatan", "Hammarkullen", "Hammarkullens Väg", "Hammarvägen", "Hamnefjäll", "Hamneviksvägen", "Handbollsvägen", "Handelshögskolan", "Hantverksvägen", "Hasselbacken", "Heden", "Helgereds Gårdar", "Hembygdsgatan", "Herkulesgatan", "Herrgårdsgatan", "Hildedalsgatan", "Hindersmässogatan", "Hinnebäcksgatan", "Hinsholmen", "Hjalmar Brantingsplatsen", "Hjuvik", "Hjällbo", "Hjällbovallen", "Hogenskildsgatan", "Holmvägen", "Hornkamsgatan", "Hovdalen", "Hovås Nedre", "Hovåsskolan", "Hults Bro", "Hultåsen", "Häggvägen", "Häljered", "Hällsviksvägen", "Härlanda", "Hästebäck", "Hög", "Högen", "Högsbo", "Högsbogatan", "Högsboleden", "Högstena", "Högstensgatan", "IAC Group", "ID-kontrollen", "Importgatan", "Indiska Oceanen", "Industrivägen", "Ingeborgsgatan", "Ingebäcks By", "Innegården", "Ivarsbergsmotet", "J A Wettergrens Gata", "Jaegerdorffsplatsen", "Januarigatan", "Johannesberg", "Jungmansgatan", "Juteskärsgatan", "Järntorget", "Järnvågen", "Jättestensskolan", "Kaggeledstorget", "Kallebäck", "Kallebäcksvägen", "Kallhed", "Kalshed", "Kalvemossen", "Kanelgatan", "Kannebäck", "Kapellplatsen", "Kaprifolgatan", "Kaptensgatan", "Kavåsvägen", "Keplers Gata", "Kippholmen", "Klangfärgsgatan", "Klara", "Klare Mosse", "Klareberg", "Klintens Väg", "Klippan", "Klippans Färjeläge", "Klobergslyckan", "Klåveskärsgatan", "Knapehall", "Knarrholmen", "Knipared", "Kobbegården", "Kobbegårdsvägen", "Komettorget", "Kornhalls Färja", "Korsvägen", "Kortedala torg", "Krabbeliderna", "Kransen", "Kringlekullen", "Kristinedal", "Krokebacksgatan", "Kruthusgatan", "Kryddnejlikegatan", "Kulörgatan", "Kungsportsplatsen", "Kungssten", "Kustgatan", "Kvarnabäcken", "Kviberg", "Kvillängen", "Kvisljungeby", "Kyrkbytorget", "Kålltorp", "Kårholmen", "Källö", "Käringberget", "Kärra Centrum", "Kärra Kyrka", "Kärralundsgatan", "Kärramotet", "Kärrlyckan", "Köpcentrum 421", "Köpcentrum 421", "Köpstadsö", "Körkarlens Gata", "Körvelgatan", "Lana", "Lantmannagatan", "Lantmilsgatan", "Lefflersgatan", "Legendgatan", "Lemmingsgatan", "Lexbyvägen", "Lilla Bommen", "Lilla Bommens Hamn", "Lilla Oxhagen", "Lilla Varholmen", "Lilleby Båthamn", "Lilleby Kronogård", "Lilleby Skolväg", "Lillebybadet", "Lillebyvägen", "Lillekärr Norra", "Lillekärr Södra", "Lillhagens Station", "Lillhagsparken Norra", "Lillhagsparken Södra", "Lillövägen", "Lindareverkstaden", "Lindholmen", "Lindholmspiren", "Linnarhults Ind Omr", "Linnarhultsvägen", "Linnéplatsen", "Lisa Sass Gata", "Liseberg", "Liseberg Station", "Lona Knapes Gata", "Lundby Gamla Kyrka", "Lunnagården", "Lyckhem", "Lyse", "Långavallsgatan", "Långedrag", "Långrevsvägen", "Låssbyvägen", "Länsmanstorget", "Lärje", "Lärje Hed", "Lärjeholm", "Lönnrunan", "Lövgärdets Centrum", "Lövviksvägen", "Madbäcksvägen", "Madängsgatan", "Majvallen", "Majvik", "Manufakturgatan", "Mariaplan", "Marieholmsgatan", "Marken Norra", "Marklandsgatan", "Masthugget", "Masthuggstorget", "Medicinaregatan", "Mejramgatan", "Melongatan", "Merkuriusgatan", "Mildvädersgatan", "Minelundsvägen", "Mossen", "Munkebäcksmotet", "Munkebäckstorget", "Munspelsgatan", "Musikvägen", "Mystängen", "Nils Ericson Terminalen", "Nobelgatan", "Nobelplatsen", "Nolereds Skola", "Nolvik", "Nordbakels", "Nordhagsvägen", "Nordstan", "Nordviksgatan", "Nordängen", "Norra Gubberogatan", "Norra Långebergsgatan", "Norskog", "Norumsgärde", "Norumshöjd", "Nya Lundenskolan", "Nya Varvsallén", "Nylösegatan", "Nymilsgatan", "Nymånegatan", "Näsbovägen", "Olivedalsgatan", "Olof Asklunds Gata", "Olofsberg", "Olofstorp", "Olofstorp Västra", "Olshammarsgatan", "Olskrokstorget", "Opaltorget", "Orion", "Orrebacksgatan", "Orustgatan", "Oscar Fredriks Kyrka", "Oslogatan", "Ostindiegatan", "Otterbäck", "Ovädersgatan", "Packhusplatsen", "Paradisgatan", "Peppargatan", "Persiljegatan", "Pilbågsgatan", "Pilegården", "Pilegårdsvägen", "Pilgatan", "Pingstvägen", "Plastteknik", "Polhemsplatsen", "Positivgatan", "Prinsgatan", "Prytzgatan", "Prästgårdsgatan", "Pumpgatan", "Påvelunds Centrum", "Pärlstickaregatan", "Pölsebo", "Qvidingsgatan", "Radiomotet", "Radiotorget", "Radiovägen", "Ragnhildsholmen", "Ramberget", "Rambergsvallen", "Ramnebacken", "Rannebergen Centrum", "Rappedalen", "Redbergsplatsen", "Regnbågsgatan", "Rengatan", "Ridhästvägen", "Ringestensvägen", "Risbindaregatan", "Roddföreningen", "Rosenhill", "Rosenlund", "Rud", "Runskriftsgatan", "Runslingan", "Runstavsgatan", "Ruskvädersgatan", "Rya Skog", "Rymdtorget Buss", "Rymdtorget Spårvagn", "Rågården", "Rödbo idrottsplats", "Rödgatan", "Rönning", "Rösbo", "Saffransgatan", "Sahlgrenska", "Sahlgrenska Huvudentré", "Sahlgrenska Norra Vändslinga", "Sahlgrenska Södra", "Saltholmen", "Saltholmens Brygga", "Salviagatan", "Sanatoriegatan", "Sandarna", "Sandeslättsgatan", "Sandvik", "Sandås", "Sankt Jörgens Allé", "Sankt Jörgens Park", "Sankt Sigfrids Plan", "Sannaplan", "Sannegårdshamnen", "Saturnusgatan", "Scandinavium", "Sehlstedtsgatan", "Selma Lagerlöfs Torg", "Seminariegatan", "Sikgatan", "Sillvik", "Simonsvägen", "Siriusgatan 106", "Siriusgatan 2", "Siriusgatan 30", "Siriusgatan 54", "Siriusgatan 78", "Sisjö Kullegata", "Sjumansholmen", "Sjupundsgatan", "Sjögången", "Sjömarksstigen", "Skarsbo", "Skeppsbron", "Skeppsbyggaregatan", "Skeppstad", "SKF", "SKF R-porten", "Skimmelvägen", "Skintebo", "Skogabergsgatan", "Skogome", "Skogomevägen", "Skogsbrynet", "Skogsrydsgatan", "Skra Bro", "Skräddaregården", "Skräppekärr Östra", "Skålvisered", "Skårs Kyrka", "Skårsplatsen", "Skälltorpsvägen", "Skändla Norra", "Skändla Rös", "Skändla Södra", "Skärvallsgatan", "Slottsberget", "Slottsskogsvallen", "Smaragdgatan", "Smedjeholm", "Smidesgatan", "Smyckegatan", "Smörgatan", "Smörkärnegatan", "Smörslottsgatan", "Snarberget", "Snipen", "Solrosgatan", "Solstrålegatan", "Solängen", "Spadegatan", "Spaldingsgatan", "Spåntorget", "Spänstvägen", "Stabbetorget", "Steken", "Sten Sturegatan", "Stena Metall", "Stenared", "Stenebyvägen", "Stenkolsgatan", "Stenskärsgatan", "Stigbergstorget", "Stigs Center", "Stockholmsgatan", "Stora Arödsgatan", "Stora Björn", "Stora Förö", "Stora Holms Gård", "Stora Mysternavägen", "Stora Oxhagen", "Stora Åvägen", "Storegården", "Storås", "Storås Industriväg", "Strömmensbergsstigen", "Ströms Väg", "Studiegången", "Styrsö Bratten", "Styrsö Skäret", "Styrsö Tången", "Stålverksgatan", "Stötekärrsvägen", "Svartedalsgatan", "Swedenborgsplatsen", "Svingeln", "Svärdslundsvägen", "Sydatlanten", "Synhållsgatan", "Synneröd", "Syrhåla", "Syster Estrids Gata", "Sägengatan", "Sälöfjordsgatan", "Sändaregatan", "Säterigatan", "Säve Flygplats", "Säve Kvarn", "Säve Station", "Sävedal", "Sävenäs Lokstation", "Sävenäs Station", "Sävviken", "Södermalmsgatan", "Södra Deltavägen", "Sörhallstorget", "Sörredsvägen", "Tagene", "Taklöksvägen", "Teleskopgatan", "Temperaturgatan", "Terminalvägen", "Thorskog", "Tidningshuset", "Timber Trading", "Timjansgatan", "Tingstadsmotet", "Tingstadsvass", "Tingstadsvägen", "Tingvallsvägen", "Toftaåsgatan", "Toleredsgatan", "Tolvskillingsgatan", "Tomtebacken", "Tomtebacksgatan", "Toredamm", "Torp", "Torpamotet", "Torsgatan", "Torslanda Mellangård", "Torslanda Torg", "Torslandakrysset", "Trafikövningsbanan", "Tranered", "Trankärrsgatan", "Trollåsen", "Trollängen", "Trulsegården", "Tryckerigatan", "Trädet", "Trädgårdsgärdet", "Trälåsvägen", "Trämjölsfabriken", "Trätorget", "Tuve Centrum", "Tuve Prästgård", "Tvärhandsgatan", "Tycho Brahes Gata", "Tångenvägen", "Tångudden", "Töpelsgatan", "Uggledal", "Ugglegården", "Ullevi Norra", "Ullevi Södra", "Ungbrodersgatan", "Uppegård", "Utlandagatan", "Vagnhallen Gårda", "Vagnhallen Majorna", "Valand", "Valborgsmässogatan", "Varbergsgatan", "Vargö", "Varmfrontsgatan", "Varpemossen", "Vasa Viktoriagatan", "Vasaplatsen", "Vassgatan", "Vassgången", "Waterloogatan", "Vattenverket", "Wavrinskys Plats", "Vegagatan", "Welandergatan", "Vendernas Gata", "Viadukten", "Vidblicksgatan", "Wieselgrensgatan", "Wieselgrensplatsen", "Vikhall", "Vikingsgatan", "Volvo Bulycke", "Volvo City", "Volvo IT", "Volvo Stenebygården", "Volvo Torslanda PV", "Volvo Torslanda PVH", "Volvo Torslanda RA", "Volvo Torslanda TA", "Volvo Torslanda TK", "Volvo Torslanda TLA", "Volvo Torslanda TN", "Volvo Torslanda TS", "Volvo Tuve L A", "Volvo Tuve L B", "Volvohallen", "Vrångö", "Vågmästareplatsen", "Vårbäcksvägen", "Vårvindsgatan", "Vårväderstorget", "Väderilsgatan", "Vädermotet", "Välen", "Västes Gata", "Västra Frölunda Kyrka", "Västra Lindås", "Vättleskolan", "Åkareplatsen", "Åkerberget", "Åkered", "Åketorpsgatan", "Ålandsgatan", "Årbogatan", "Åseby", "Älvsborgshamnen", "Älvsborgsplan", "Änghagen", "Ängås", "Ättehögsgatan", "Önnereds Brygga", "Önskevädersgatan", "Östergården", "Östergärde", "Österlyckan", "Östes Gata", "Östra Lindås", "Östra Sjukhuset", "Överåsvallen", "Övre Hällsvik", "Öxnäs"};

        for (String s : stations) {
            s = s.replaceAll("\\s+", "%20");
            client.getBestMatch(s);
        }
        // and... that's all!
    }

}
