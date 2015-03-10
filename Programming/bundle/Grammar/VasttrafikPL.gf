--# -coding= utf8

concrete VasttrafikPL of Vasttrafik = SymbolEng ** {

	lincat
	  Query = Str ;
	  Location = Str ;
	  Time = Str ;
	  Mean = Str ;
	  TimeReferencePoint = Str ;
	  TimeOffset = Str ;
		
	lin
	
	  MainQuery mean loc1 loc2 time = mean ++ loc1 ++ "do" ++ loc2 ++ time;
	  
	  AllMeans = ("Podróż z" | "Z" );
	  Bus = ("Podróż autobusem z" | "Autobus z" );
	  Train = ("Podróż pociągiem z" | "Pociąg z" );
	  Tram = ("Podróż tramwajem z" | "Tramwaj z" );
	  Boat = ("Podróż łodzią z" | "Łódź z" );
	  
	  Station s = s.s;
	  
	  NoTime = "";
	  RelativeTime reference offset = reference ++ offset;
	  Departing = ("wyjazd" | "odjazd" | "");
	  Arriving = ("przyjazd" | "dojazd");
	  Now = "teraz";
	  Quarter = "za piętnaście minut";
	  Half = "za trzydzieści minut";
	  ThreeQuarters = "za czterdzieści pięc minut";
	  One = "za godzinę";
	  Two = "za dwie godziny";
	  Three = "za trzy godziny";
	  Four = "za cztery godziny";
	  Five = "za pięć godzin";
}