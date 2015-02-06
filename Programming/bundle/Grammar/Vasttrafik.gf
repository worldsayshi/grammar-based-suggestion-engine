abstract Vasttrafik = Symbol ** {

	 flags startcat = Query;

	cat
	  Query ;
	  Location ;
	  Time ;
	  Mean ;
	  Mode;
	  Offset;

	fun
	  -- SimpleQuery : Location -> Location -> Action -> Time -> Query ;
	  -- Station : Symb -> Location ;
	  -- Depart, Arrive: Action;
	  -- Now, Soon, Tomorrow : Time;
	  
	  MainQuery : Mean -> Location -> Location -> Time -> Query;
	  Bus,Tram,Boat,Train,AllMeans : Mean;
	  Station: Symb -> Location;
	  NoTime: Time;
	  RelativeTime: Mode -> Offset -> Time;
	  Arriving, Departing: Mode;
	  Now ,Quarter, Half, ThreeQuarters, One, Two, Three, Four, Five: Offset;
	  
	  
	  
}