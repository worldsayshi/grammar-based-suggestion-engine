concrete VasttrafikApi of Vasttrafik = SymbolEng ** {

	lincat
	  Query = Str ;
	  Location = Str ;
	  Time = Str ;
	  Mean = Str ;
	  TimeReferencePoint = Str ;
	  TimeOffset = Str ;

	lin
	
	  MainQuery mean loc1 loc2 time = time ++ "mean" ++ ":" ++ mean ++ ";" ++ "from" ++ ":" ++ loc1 ++ ";" ++ "to" ++ ":" ++ loc2;
	  
	  AllMeans = "all";
	  Bus = "bus";
	  Train = "train";
	  Tram = "tram";
	  Boat = "boat";
	  
	  Station s = s.s;
	  
	  NoTime = "reference : departing ; offset : 0 ;" ;
	  RelativeTime reference offset = "reference" ++ ":" ++  reference ++ ";" ++ "offset" ++ ":" ++ offset ++ ";" ;
	  Departing = "departure";
	  Arriving = "arrival";
	  Now = "0";
	  Quarter = "15";
	  Half = "30";
	  ThreeQuarters = "45";
	  One = "60";
	  Two = "120";
	  Three = "180";
	  Four = "240";
	  Five = "300";
}