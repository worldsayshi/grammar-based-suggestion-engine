abstract Vasttrafik = Symbol ** {

	 flags startcat = Query;

	cat
	  Query ;
	  Location ;

	fun
	  -- FromQuery : Location -> Query ;
	  -- ToQuery : Location -> Query ;
	  SimpleQuery : Location -> Location -> Query ;
	  Station : Symb -> Location ;
	  -- GeoLocation : Symb -> Location ;
}