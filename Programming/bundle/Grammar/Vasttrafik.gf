abstract Vasttrafik = Symbol ** {

	 flags startcat = Query;

	cat
	  Query ;
	  Location ;
	  Time ;
	  Mean ;
	  TimeReferencePoint;
	  TimeOffset;

	fun
	 
	  MainQuery : Mean -> Location -> Location -> Time -> Query;
	  Bus,Tram,Boat,Train,AllMeans : Mean;
	  Station: Symb -> Location;
	  NoTime: Time;
	  RelativeTime: TimeReferencePoint -> TimeOffset -> Time;
	  Arriving, Departing: TimeReferencePoint;
	  Now ,Quarter, Half, ThreeQuarters, One, Two, Three, Four, Five: TimeOffset;	  
}