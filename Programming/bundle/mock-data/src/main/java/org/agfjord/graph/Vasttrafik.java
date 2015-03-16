package org.agfjord.graph;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.solr.client.solrj.SolrServerException;
import org.grammaticalframework.pgf.ParseError;

/**
 *
 * @author per.fredelius
 */
public class Vasttrafik {
    
    public void run() throws SolrServerException, ParseError, MalformedURLException {
        String grammmar_dir = System.getProperty("grammar.dir");
        System.out.println(grammmar_dir);

        //URL url = new File(System.getProperty("grammar.dir"),"Vasttrafik.pgf").toURI().toURL();
        /*this.getClass()
                .getClassLoader()
                .getResource("Vasttrafik.pgf");*/
        // Clear the indexes
        
        
		try {
            DataImportSolr solrData = new DataImportSolr();
            
            String abs_grammar_name = "Vasttrafik";
            //
			Grammar grammar = new Grammar(grammmar_dir,abs_grammar_name);
            
            Set<String> asts = grammar.generateAbstractSyntaxTreesFromShell();
            
            System.out.println(asts);
            System.out.println();
            System.out.println("LIN");
            
            String[] languages = {"EngConcat", "SweConcat", "PL"};

            for (String lang : languages) {

                List<Set<String>> linearizations =
                        grammar.generateLinearizations(asts,
                        abs_grammar_name + lang + ".gf",
                        abs_grammar_name + lang);
                System.out.println(linearizations);
                System.out.println();
                System.out.println("INS");
                List<Instruction> instrucs = grammar.createInstrucs(asts, linearizations, abs_grammar_name + lang);
                System.out.println(instrucs);

                solrData.deleteLinearizationsOfLang(abs_grammar_name + lang);
                solrData.addInstrucsToSolr(instrucs);

                solrData.deleteNamesOfAbsLang(abs_grammar_name);
                // Add temp stations:
                solrData.importNames("Station", createTempStations(), abs_grammar_name);

            }
            
            
            
            //solrData.addInstrucsToSolr(instrucs);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void main(String[] args) 
            throws ParseError, IOException, SolrServerException {
        System.out.println();
        System.out.println("MAIN");
        new Vasttrafik().run();
        //System.out.println(System.getProperty("java.class.path"));
        System.out.println();
        System.out.println();
        
    }
    
    // Temporary list of stations in gothenburg
    // From here: http://www.vasttrafik.se/#!/Reseinformation/sa-har-gar-det-till/kommuners-hallplatser-och-zoner/hallplatser-inom-zon-goteborg/
    static String[] stations = {"Akkas Gata","Alekärrsgatan","Alelyckan","Allebyvägen","Allhelgonakyrkan","Almedal","Amalia Jönssons Gata","Amhult","Andalen","Anekdotgatan","Angered Centrum","Angered Garaget","Angereds Kyrka","Angereds Storåsväg","Angeredsvinkeln","Annedalskyrkan","Anneholmsgatan","Anticimex","Aprikosgatan","Aprilgatan","Arendal Norra","Arendal Portal","Arendal Skans","Arendal Terminalen","Arendals Allé","Arendalsvägen","Armlängdsgatan","Aröds Äng","Askesby","Askims Kyrka","Askims Stationsväg","Askims Svartmosse","Askims Torg","Askimsbadet","Asperö Norra","Asperö Östra","Assmundtorp","Astronomgatan","Atmosfärgatan","Axel Dahlströms Torg","Backa","Backa Bergö","Backa Kyrkogata","Backadalsmotet","Backered","Backereds Skola","Backvägen","Bagaregårdsskolan","Balamundi","Balladgatan","Bassås","Batterivägen","Beatrice Lesslies Gata","Bellevue","Bensingatan","Bergegården","Berghöjdsgatan","Bergkristallsgatan","Bergsgårdsvägen","Bergsjödalen","Bergsjösvängen","Bergsprängaregatan","Bergums Kyrka","Beryllgatan","Berzeliigatan","Bessemergatan","Betaniagatan","Beväringsgatan","Billdal","Billdals Gård","Billdals Kyrka","Bingared","Bjurslätts Torg","Bjurslättsliden","Bjurslättsskolan","Björkhöjdsgatan","Björkrisvägen","Björkuddsgatan","Björlanda Kile","Björlanda Kyrka","Björnsonsgatan","Björnåsgatan","Björsared","Bli","Blåsutgatan","Bockhornsgatan","Bokekullsgatan","Bolmörtsgatan","Bolsten","Botaniska Trädgården","Bovallstrandsgatan","Brandkärr","Bredfjällsgatan","Briljantgatan","Brottkärr","Brovägen","Brungatan","Brunnehagen","Brunnsbotorget","Brunnsgatan","Brunnsparken","Brunstorp","Brunstorpsnäs","Bräcke Östergård","Brända Berget","Brännö Husvik","Brännö Rödsten","Bur","Burmabacken","Burmans Gata","Byfogdegatan","Bygdegården","Båtsman Grås Gata","Båtsman Hisings Gata","Båtsman Kapers Gata","Bäckebol Köpcentrum","Bäckebol Norra","Bäckebol Södra","Bäckebols Gård","Bäckebolsmotet","Bäckedalsvägen","Bäckeliden","Bärby","Bärby Korsväg","Bärby Norra","Bögatan","Calvert","Carlandersplatsen","Centralstationen","Chalmers","Chalmers Tvärgata","Chapmans Torg","Cold Stores","Dalagärde","Dalavägen","Danaplatsen","Datavägen","Datavägen Södra","Datavägen Östra","Delabäcken","Delsjömotet","Deltavägen","Depå Frihamnen","Djurgårdsskolan","Dockeredsvägen","Doktor Forselius Gata","Doktor Fries Torg","Doktor Sydows Gata","Doktor Weltzins Gata","Doktor Westrings Gata","Domkyrkan","Donsö","Ejdergatan","Ekedal","Ekeredsvägen","Eketrägatan","Eketrägatan Vändslinga","Ekmanska","Eliebergsvägen","Elisedal","Ellered","Ellesbo","Enebacken","Engdahlsgatan","Eriksbergs Färjeläge","Eriksbergstorget","Eriksbo Skola","Eriksbo Västergärde","Eriksbo Östergärde","Eriksdal","Exportgatan","F O Peterssons Gata","Fagerängsgatan","Falutorget","Finlandsvägen","Fiskebäcks Hamn","Fiskebäcks Småbåtshamn","Fiskebäcksskolan","Fjäderharvsgatan","Fjällbo","Fjällbruden","Fjällgatan","Fjällhavren","Fjällkåpan","Fjällskolan","Fjällsyran","Framnäsgatan","Fridhems Kyrkogård","Friedländers Gata","Frihamnen","Frimästaregatan","Friskväderstorget","Fräntorp","Frölunda Smedja","Frölunda Torg","Frölunda Torg Vändslinga","Furåsvägen/Trekanten","Fyrktorget","Fyrskeppsvägen","Fyrspannvägen","Fågelvägen","Fältspatsgatan","Fänkålsgatan","Färgfabrikerna","Galileis Gata","Gamla Hangaren","Gamla Lillebyvägen","Gamla Torslandavägen","Gamlestaden station","Gamlestadstorget","Gamlestadstorget Ers.hpl","Garaget Högstena","Gasverket","Gerrebacka Gård","Getebergsäng","Gillegatan","Gjutegården","Gjutjärnsgatan","Glöstorpsvägen","Godhemsgatan","Godsgatan","Gossbydalsvägen","Granhäll","Granliden","Grevebergsgatan","Grevegårdsvägen","Grimbo","Gropegårdsgatan","Gropens Gård","Gråbacka","Gråberget","Gräddgatan","Gröna Viken","Grönsakstorget","Gunnaredsvägen","Gunnesby Bro","Gunnesby Kalsbogård","Gunnesby Skola","Gunnesgärde","Gunnilse Skola","Gustavsplatsen","Gårdsten Centrum","Gårdstensliden","Gåsmossen","Gärdsås Torg","Gärdsåsgatan","Göddered","Göpåsgatan","Göpåstunneln","Götaplatsen","Göteborg C","Hagakyrkan","Hagelvädersgatan","Hagen","Halleröd","Halleskärsgatan","Hammarkullen","Hammarkullens Väg","Hammarvägen","Hamnefjäll","Hamneviksvägen","Handbollsvägen","Handelshögskolan","Hantverksvägen","Hasselbacken","Heden","Helgereds Gårdar","Hembygdsgatan","Herkulesgatan","Herrgårdsgatan","Hildedalsgatan","Hindersmässogatan","Hinnebäcksgatan","Hinsholmen","Hjalmar Brantingsplatsen","Hjuvik","Hjällbo","Hjällbovallen","Hogenskildsgatan","Holmvägen","Hornkamsgatan","Hovdalen","Hovås Nedre","Hovåsskolan","Hults Bro","Hultåsen","Häggvägen","Häljered","Hällsviksvägen","Härlanda","Hästebäck","Hög","Högen","Högsbo","Högsbogatan","Högsboleden","Högstena","Högstensgatan","IAC Group","ID-kontrollen","Importgatan","Indiska Oceanen","Industrivägen","Ingeborgsgatan","Ingebäcks By","Innegården","Ivarsbergsmotet","J A Wettergrens Gata","Jaegerdorffsplatsen","Januarigatan","Johannesberg","Jungmansgatan","Juteskärsgatan","Järntorget","Järnvågen","Jättestensskolan","Kaggeledstorget","Kallebäck","Kallebäcksvägen","Kallhed","Kalshed","Kalvemossen","Kanelgatan","Kannebäck","Kapellplatsen","Kaprifolgatan","Kaptensgatan","Kavåsvägen","Keplers Gata","Kippholmen","Klangfärgsgatan","Klara","Klare Mosse","Klareberg","Klintens Väg","Klippan","Klippans Färjeläge","Klobergslyckan","Klåveskärsgatan","Knapehall","Knarrholmen","Knipared","Kobbegården","Kobbegårdsvägen","Komettorget","Kornhalls Färja","Korsvägen","Kortedala torg","Krabbeliderna","Kransen","Kringlekullen","Kristinedal","Krokebacksgatan","Kruthusgatan","Kryddnejlikegatan","Kulörgatan","Kungsportsplatsen","Kungssten","Kustgatan","Kvarnabäcken","Kviberg","Kvillängen","Kvisljungeby","Kyrkbytorget","Kålltorp","Kårholmen","Källö","Käringberget","Kärra Centrum","Kärra Kyrka","Kärralundsgatan","Kärramotet","Kärrlyckan","Köpcentrum 421","Köpcentrum 421","Köpstadsö","Körkarlens Gata","Körvelgatan","Lana","Lantmannagatan","Lantmilsgatan","Lefflersgatan","Legendgatan","Lemmingsgatan","Lexbyvägen","Lilla Bommen","Lilla Bommens Hamn","Lilla Oxhagen","Lilla Varholmen","Lilleby Båthamn","Lilleby Kronogård","Lilleby Skolväg","Lillebybadet","Lillebyvägen","Lillekärr Norra","Lillekärr Södra","Lillhagens Station","Lillhagsparken Norra","Lillhagsparken Södra","Lillövägen","Lindareverkstaden","Lindholmen","Lindholmspiren","Linnarhults Ind Omr","Linnarhultsvägen","Linnéplatsen","Lisa Sass Gata","Liseberg","Liseberg Station","Lona Knapes Gata","Lundby Gamla Kyrka","Lunnagården","Lyckhem","Lyse","Långavallsgatan","Långedrag","Långrevsvägen","Låssbyvägen","Länsmanstorget","Lärje","Lärje Hed","Lärjeholm","Lönnrunan","Lövgärdets Centrum","Lövviksvägen","Madbäcksvägen","Madängsgatan","Majvallen","Majvik","Manufakturgatan","Mariaplan","Marieholmsgatan","Marken Norra","Marklandsgatan","Masthugget","Masthuggstorget","Medicinaregatan","Mejramgatan","Melongatan","Merkuriusgatan","Mildvädersgatan","Minelundsvägen","Mossen","Munkebäcksmotet","Munkebäckstorget","Munspelsgatan","Musikvägen","Mystängen","Nils Ericson Terminalen","Nobelgatan","Nobelplatsen","Nolereds Skola","Nolvik","Nordbakels","Nordhagsvägen","Nordstan","Nordviksgatan","Nordängen","Norra Gubberogatan","Norra Långebergsgatan","Norskog","Norumsgärde","Norumshöjd","Nya Lundenskolan","Nya Varvsallén","Nylösegatan","Nymilsgatan","Nymånegatan","Näsbovägen","Olivedalsgatan","Olof Asklunds Gata","Olofsberg","Olofstorp","Olofstorp Västra","Olshammarsgatan","Olskrokstorget","Opaltorget","Orion","Orrebacksgatan","Orustgatan","Oscar Fredriks Kyrka","Oslogatan","Ostindiegatan","Otterbäck","Ovädersgatan","Packhusplatsen","Paradisgatan","Peppargatan","Persiljegatan","Pilbågsgatan","Pilegården","Pilegårdsvägen","Pilgatan","Pingstvägen","Plastteknik","Polhemsplatsen","Positivgatan","Prinsgatan","Prytzgatan","Prästgårdsgatan","Pumpgatan","Påvelunds Centrum","Pärlstickaregatan","Pölsebo","Qvidingsgatan","Radiomotet","Radiotorget","Radiovägen","Ragnhildsholmen","Ramberget","Rambergsvallen","Ramnebacken","Rannebergen Centrum","Rappedalen","Redbergsplatsen","Regnbågsgatan","Rengatan","Ridhästvägen","Ringestensvägen","Risbindaregatan","Roddföreningen","Rosenhill","Rosenlund","Rud","Runskriftsgatan","Runslingan","Runstavsgatan","Ruskvädersgatan","Rya Skog","Rymdtorget Buss","Rymdtorget Spårvagn","Rågården","Rödbo idrottsplats","Rödgatan","Rönning","Rösbo","Saffransgatan","Sahlgrenska","Sahlgrenska Huvudentré","Sahlgrenska Norra Vändslinga","Sahlgrenska Södra","Saltholmen","Saltholmens Brygga","Salviagatan","Sanatoriegatan","Sandarna","Sandeslättsgatan","Sandvik","Sandås","Sankt Jörgens Allé","Sankt Jörgens Park","Sankt Sigfrids Plan","Sannaplan","Sannegårdshamnen","Saturnusgatan","Scandinavium","Sehlstedtsgatan","Selma Lagerlöfs Torg","Seminariegatan","Sikgatan","Sillvik","Simonsvägen","Siriusgatan 106","Siriusgatan 2","Siriusgatan 30","Siriusgatan 54","Siriusgatan 78","Sisjö Kullegata","Sjumansholmen","Sjupundsgatan","Sjögången","Sjömarksstigen","Skarsbo","Skeppsbron","Skeppsbyggaregatan","Skeppstad","SKF","SKF R-porten","Skimmelvägen","Skintebo","Skogabergsgatan","Skogome","Skogomevägen","Skogsbrynet","Skogsrydsgatan","Skra Bro","Skräddaregården","Skräppekärr Östra","Skålvisered","Skårs Kyrka","Skårsplatsen","Skälltorpsvägen","Skändla Norra","Skändla Rös","Skändla Södra","Skärvallsgatan","Slottsberget","Slottsskogsvallen","Smaragdgatan","Smedjeholm","Smidesgatan","Smyckegatan","Smörgatan","Smörkärnegatan","Smörslottsgatan","Snarberget","Snipen","Solrosgatan","Solstrålegatan","Solängen","Spadegatan","Spaldingsgatan","Spåntorget","Spänstvägen","Stabbetorget","Steken","Sten Sturegatan","Stena Metall","Stenared","Stenebyvägen","Stenkolsgatan","Stenskärsgatan","Stigbergstorget","Stigs Center","Stockholmsgatan","Stora Arödsgatan","Stora Björn","Stora Förö","Stora Holms Gård","Stora Mysternavägen","Stora Oxhagen","Stora Åvägen","Storegården","Storås","Storås Industriväg","Strömmensbergsstigen","Ströms Väg","Studiegången","Styrsö Bratten","Styrsö Skäret","Styrsö Tången","Stålverksgatan","Stötekärrsvägen","Svartedalsgatan","Swedenborgsplatsen","Svingeln","Svärdslundsvägen","Sydatlanten","Synhållsgatan","Synneröd","Syrhåla","Syster Estrids Gata","Sägengatan","Sälöfjordsgatan","Sändaregatan","Säterigatan","Säve Flygplats","Säve Kvarn","Säve Station","Sävedal","Sävenäs Lokstation","Sävenäs Station","Sävviken","Södermalmsgatan","Södra Deltavägen","Sörhallstorget","Sörredsvägen","Tagene","Taklöksvägen","Teleskopgatan","Temperaturgatan","Terminalvägen","Thorskog","Tidningshuset","Timber Trading","Timjansgatan","Tingstadsmotet","Tingstadsvass","Tingstadsvägen","Tingvallsvägen","Toftaåsgatan","Toleredsgatan","Tolvskillingsgatan","Tomtebacken","Tomtebacksgatan","Toredamm","Torp","Torpamotet","Torsgatan","Torslanda Mellangård","Torslanda Torg","Torslandakrysset","Trafikövningsbanan","Tranered","Trankärrsgatan","Trollåsen","Trollängen","Trulsegården","Tryckerigatan","Trädet","Trädgårdsgärdet","Trälåsvägen","Trämjölsfabriken","Trätorget","Tuve Centrum","Tuve Prästgård","Tvärhandsgatan","Tycho Brahes Gata","Tångenvägen","Tångudden","Töpelsgatan","Uggledal","Ugglegården","Ullevi Norra","Ullevi Södra","Ungbrodersgatan","Uppegård","Utlandagatan","Vagnhallen Gårda","Vagnhallen Majorna","Valand","Valborgsmässogatan","Varbergsgatan","Vargö","Varmfrontsgatan","Varpemossen","Vasa Viktoriagatan","Vasaplatsen","Vassgatan","Vassgången","Waterloogatan","Vattenverket","Wavrinskys Plats","Vegagatan","Welandergatan","Vendernas Gata","Viadukten","Vidblicksgatan","Wieselgrensgatan","Wieselgrensplatsen","Vikhall","Vikingsgatan","Volvo Bulycke","Volvo City","Volvo IT","Volvo Stenebygården","Volvo Torslanda PV","Volvo Torslanda PVH","Volvo Torslanda RA","Volvo Torslanda TA","Volvo Torslanda TK","Volvo Torslanda TLA","Volvo Torslanda TN","Volvo Torslanda TS","Volvo Tuve L A","Volvo Tuve L B","Volvohallen","Vrångö","Vågmästareplatsen","Vårbäcksvägen","Vårvindsgatan","Vårväderstorget","Väderilsgatan","Vädermotet","Välen","Västes Gata","Västra Frölunda Kyrka","Västra Lindås","Vättleskolan","Åkareplatsen","Åkerberget","Åkered","Åketorpsgatan","Ålandsgatan","Årbogatan","Åseby","Älvsborgshamnen","Älvsborgsplan","Änghagen","Ängås","Ättehögsgatan","Önnereds Brygga","Önskevädersgatan","Östergården","Östergärde","Österlyckan","Östes Gata","Östra Lindås","Östra Sjukhuset","Överåsvallen","Övre Hällsvik","Öxnäs"};

    private ArrayList<Map<String, Object>> createTempStations() {
        ArrayList<Map<String,Object>> dataStations = new ArrayList<>();
        for (String station : stations) {
            HashMap<String, Object> dataStation = new HashMap<String, Object>();
            dataStation.put("name", station);
            dataStation.put("count", new Long(1));
            dataStation.put("type", "Location");
            dataStations.add(dataStation);
        }
        return dataStations;
    }
}
