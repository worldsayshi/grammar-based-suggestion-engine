--# -coding= utf8

concrete InstrucsPL of Instrucs = SymbolEng ** {
 lincat
  Instruction = Str ;
  Internal, External, Resource = Str ;
  InternalRelation, ExternalRelation, ResourceRelation = Str ;
  SkillExpr, OrganizationExpr, LocationExpr, ModuleExpr = Str ;

 lin
  -- Instructions
  InstrucInternal internal relation = internal ++ 
                                   "która" ++ relation ;
  InstrucExternal external relation = external ++ 
                                   "który" ++ relation ;
  InstrucResource resource' relation = resource' ++ 
                                   "w którym" ++ relation ;

  -- Subjects
  People = "osoba" ;
  Customer = "klient" ;
  Project = "projekt" ;

  -- Relations
  Know_R obj = "zna" ++ obj;
  UseExt_R obj = "używa" ++ obj ;
  UseRes_R obj = "używa się" ++ obj ;
  WorkWith_R obj = "pracuje z" ++ obj ;
  WorkIn_R obj = "pracuje w" ++ obj ;

  -- Boolean operators for relations
  InternalAnd s1 s2 = s1 ++ "i" ++ s2 ;
  InternalOr s1 s2 = s1 ++ "lub" ++ s2 ;

  ExternalAnd s1 s2 = s1 ++ "i" ++ s2 ;
  ExternalOr s1 s2 = s1 ++ "lub" ++ s2 ;

  ResourceAnd s1 s2 = s1 ++ "i" ++ s2 ;
  ResourceOr s1 s2 = s1 ++ "lub" ++ s2 ; 

  -- Unknown names
  Skill s = s.s ;
  Organization s = s.s ;
  Location s = s.s ;
  Module s = s.s ;

  -- Boolean operators for names
  And_S s1 s2 = s1 ++ "oraz" ++ s2 ;
  Or_S s1 s2 = s1 ++ "albo" ++ s2 ;
	  
  And_O s1 s2 = s1 ++ "oraz" ++ s2 ;
  Or_O s1 s2 = s1 ++ "albo" ++ s2 ;
	  
  And_L s1 s2 = s1 ++ "oraz" ++ s2 ;
  Or_L s1 s2 = s1 ++ "albo" ++ s2 ;
	  
  And_M s1 s2 = s1 ++ "oraz" ++ s2 ;
  Or_M s1 s2 = s1 ++ "albo" ++ s2 ;
}
