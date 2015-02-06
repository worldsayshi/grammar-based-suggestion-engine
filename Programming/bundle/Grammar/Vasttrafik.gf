abstract Vasttrafik = Symbol ** {

	 flags startcat = Query;

	cat
	  Query ;
	  Location ;
	  Time ;
	  Action ;

	fun
	  -- FromQuery : Location -> Query ;
	  -- ToQuery : Location -> Query ;
	  SimpleQuery : Location -> Location -> Action -> Time -> Query ;
	  Station : Symb -> Location ;
	  Depart, Arrive: Action;
	  Now, Soon, Tomorrow : Time;
	  -- GeoLocation : Symb -> Location ;
}