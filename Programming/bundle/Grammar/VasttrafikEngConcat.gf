concrete VasttrafikEngConcat of Vasttrafik = SymbolEng ** {

	lincat
	  Query = Str ;
	  Location = Str ;

	lin
	  SimpleQuery loc1 loc2 = ("Go from" | "From") ++ loc1 ++ "to" ++ loc2 ;
	  Station s = s.s ;
	  GeoLocation s = s.s ;
}