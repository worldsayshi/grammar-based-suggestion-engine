concrete SimpleEng of Simple = open StringOper, ParadigmsEng, SyntaxEng, SymbolEng in {
	lincat
	  Question = Utt ;
	  Subject = IP ;
	  Predicate = VP ;
	  Name = N ;
	  Object = N ;
	  Person = PN;
	  Resource = N ;

	  -- Deprecated
	  --ListPredicate = RS ;
	  --NamePredicate = VP ;
	  --ResPredicate = VP ;
	lin
	  -- Questions
	  Indirect_Q name predicate = mkUtt (
								  	mkS tenses anterior polarity (mkCl (mkNP (every_Det | aPl_Det) (
								  			mkCN name connectingWords -- change to RCl -> RS 
								  		  )
								  	) predicate)) ;
	  Direct_Q subject predicate = mkUtt (mkQCl subject predicate) ;

	  -- How to use 'that' instead of which? Also problem with singular and plural
	  --Name_Q name predicate = mkUtt (mkCN name (mkRS (mkRCl which_RP predicate)));
	  --Conj_Q name ps = mkUtt (mkCN name ps) ; -- works with two predicates
	
	  -- Direct subjects
	  Which_Sg name = mkIP which_IDet name;
	  Which_Pl name = mkIP whichPl_IDet name ;

	  -- Indirect subjects / names
	  Developer_N = mkN "developer" ;
	  Person_N = mkN "person" ;
	  Project_N = mkN "project" ;
	  Customer_N = mkN "customer" ;

      MkPerson s = SymbPN (MkSymb s);
      
	  -- Predicates
	  Know_P object = mkPredicate "know" object;
	  Use_P object = mkPredicate "use" object ;
	  --Work_P object = mkVP (mkV2 (mkV "work") with_Prep) (mkNP object) ;
	  Work_P person = mkVP (mkV2 (mkV "work") with_Prep) (mkNP person) ;
	  
	  
	  
	  -- List predicates
	  --And pred1 pred2 = mkRS and_Conj (mkRSWhich pred1) (mkRSWhich pred2) ;
	  --Or pred1 pred2 = mkRS or_Conj (mkRSWhich pred1) (mkRSWhich pred2) ;

	  -- Resources / objects
	  Java_O = mkN "java" ;
	  Solr_O = mkN "solr" ;
	  
	-- Operations
	oper
	  tenses : Tense = futureTense | pastTense | presentTense | conditionalTense ;
	  connectingWords: NP = mkNP (mkN ("that" | "which")) ;
	  polarity : Pol = positivePol | negativePol ;
	  anterior : Ant = anteriorAnt | simultaneousAnt ;
	  mkPredicate : Str -> N -> VP = \pred, obj -> mkVP (mkV2 (mkV pred)) (mkNP obj) ;
	  mkRSWhich : VP -> RS = \pred -> mkRS (mkRCl which_RP pred) ;
}