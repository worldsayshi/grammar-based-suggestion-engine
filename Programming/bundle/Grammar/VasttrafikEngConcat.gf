concrete VasttrafikEngConcat of Vasttrafik = SymbolEng ** in {

	lincat
	  Query = Str ;
	  Location = Str ;

	lin
	  FromQuery loc = ("Travel from" | "From") ++ loc ;
	  ToQuery loc = ("Travel to" | "Go to" | "To") ++ loc ;
	  SimpleQuery loc1 loc2 = ("Travel from" | "Go from" | "From") ++ loc1 ++ "to" ++ loc2;
	  Station s = s.s;
	  -- GeoLocation s = s.s ;
}