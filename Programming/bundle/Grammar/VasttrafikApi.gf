concrete VasttrafikApi of Vasttrafik = SymbolEng ** {

	lincat
	  Query = Str ;
	  Location = Str ;

	lin
	  FromQuery loc = "from" ++ ":" ++ loc ;
	  ToQuery loc = "to" ++ ":" ++ loc ;
	  SimpleQuery loc1 loc2 = "from" ++ ":" ++ loc1 ++ ";" ++ "to" ++ ":" ++ loc2;
	  Station s = s.s;
	  -- GeoLocation s = s.s ;
}