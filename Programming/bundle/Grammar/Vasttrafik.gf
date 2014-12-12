abstract Vasttrafik = Symbol ** {

	 flags startcat = Query;

	cat
	  Query ;
	  Location ;

	fun
	  SimpleQuery : Location -> Location -> Query ;
	  Station : Symb -> Location ;
	  -- GeoLocation : Symb -> Location ;
}