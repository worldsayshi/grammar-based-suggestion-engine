concrete VasttrafikEngConcat of Vasttrafik = SymbolEng ** {

	lincat
	  Query = Str ;
	  Location = Str ;
	  Time = Str ;
	  Mean = Str ;
	  Mode = Str ;
	  Offset = Str ;
		
	lin
	  -- SimpleQuery loc1 loc2 action time = ("Travel from" | "Go from" | "From") ++ loc1 ++ "to" ++ loc2 ++ action ++ time;
	  -- Station s = s.s;
	  -- Depart = "departing";
	  -- Arrive = "arriving";
	  -- Now = "now";
	  -- Soon = "soon";
	  -- Tomorrow = "tomorrow";
	  
	  MainQuery mean loc1 loc2 time = mean ++ loc1 ++ "to" ++ loc2 ++ time;
	  
	  AllMeans = ("Travel from" | "Go from" | "Get from" | "From" );
	  Bus = ("Travel by bus from" | "Go by bus from" | "Get by bus from" | "Bus from" );
	  Train = ("Travel by train from" | "Go by train from" | "Get by train from" | "Train from" );
	  Tram = ("Travel by tram from" | "Go by tram from" | "Get by tram from" | "Tram from" );
	  Boat = ("Travel by boat from" | "Go by boat from" | "Get by boat from" | "Boat from" );
	  
	  Station s = s.s;
	  
	  NoTime = "";
	  RelativeTime mode offset = mode ++ offset;
	  Departing = ("departing" | "leaving");
	  Arriving = "arriving";
	  Now = "now";
	  Quarter = "in fifteen minutes";
	  Half = "in thirty minutes";
	  ThreeQuarters = "in fourty-five minutes";
	  One = "in one hour";
	  Two = "in two hours";
	  Three = "in three hours";
	  Four = "in four hours";
	  Five = "in five hours";
}