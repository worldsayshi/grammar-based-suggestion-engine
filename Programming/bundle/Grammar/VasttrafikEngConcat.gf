concrete VasttrafikEngConcat of Vasttrafik = SymbolEng ** {

	lincat
	  Query = Str ;
	  Location = Str ;
	  Time = Str ;
	  Action = Str ;
		
	lin
	  -- FromQuery loc = ("Travel from" | "From") ++ loc ;
	  -- ToQuery loc = ("Travel to" | "Go to" | "To") ++ loc ;
	  SimpleQuery loc1 loc2 action time = ("Travel from" | "Go from" | "From") ++ loc1 ++ "to" ++ loc2 ++ action ++ time;
	  Station s = s.s;
	  Depart = "departing";
	  Arrive = "arriving";
	  Now = "now";
	  Soon = "soon";
	  Tomorrow = "tomorrow";
	  -- GeoLocation s = s.s ;
}