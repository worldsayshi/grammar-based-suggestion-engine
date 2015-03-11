concrete VasttrafikSweConcat of Vasttrafik = SymbolEng ** {

	lincat
	  Query = Str ;
	  Location = Str ;
	  Time = Str ;
	  Mean = Str ;
	  TimeReferencePoint = Str ;
	  TimeOffset = Str ;
		
	lin
	
	  MainQuery mean loc1 loc2 time = mean ++ loc1 ++ "till" ++ loc2 ++ time;
	  
	  AllMeans = ("Resa från" | "Åka från" | "Från" );
	  Bus = ("Åka med buss från" | "Åka buss från" | "Ta bussen från" | "Buss från" );
	  Train = ("Åka med tåg från" | "Åka tåg från" | "Ta tåget från" | "Tåg från" );
	  Tram = ("Åka med spårvagn från" | "Åka spårvagn från" | "Ta spårvagnen från" | "Spårvagn från" );
	  Boat = ("Åka med båt från" | "Åka båt från" | "Ta båt från" | "Båt från" );
	  
	  Station s = s.s;
	  
	  NoTime = "";
	  RelativeTime reference offset = reference ++ offset;
	  Departing = ("avreser" | "lämnar");
	  Arriving = ("ankommer" | "kommer fram");
	  Now = "nu";
	  Quarter = ("om femton minuter" | "om en kvart");
	  Half = ("om en halvtimme" | "om trettio minuter");
	  ThreeQuarters = ("om fyrtiofem minuter" | "om en trekvart");
	  One = "om en timme";
	  Two = "om två timmar";
	  Three = "om tre timmar";
	  Four = "om fyra timmar";
	  Five = "om fem timmar";
}