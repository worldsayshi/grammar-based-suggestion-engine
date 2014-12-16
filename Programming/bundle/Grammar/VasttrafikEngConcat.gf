concrete VasttrafikEngConcat of Vasttrafik = SymbolEng ** open Templating in {

	lincat
	  Query = Str ;
	  Location = Str ;

	lin
	  FromQuery loc = ("Travel from" | "Go from" | "From") ++ loc ;
	  ToQuery loc = ("Travel to" | "Go to" | "To") ++ loc ;
	  SimpleQuery loc1 loc2 = ("Travel from" | "Go from" | "From") ++ loc1 ++ "to" ++ loc2;
	  Station s = s.s;
	  -- GeoLocation s = s.s ;
}