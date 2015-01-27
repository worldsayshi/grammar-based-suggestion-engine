abstract Instrucs = Symbol ** {
	
	flags startcat = Instruction;
	
	cat
	  Instruction ;
	  Internal ;
	  External ; Resource ;
	  InternalRelation ; ExternalRelation ; ResourceRelation ;
	  SkillExpr ; OrganizationExpr ; LocationExpr ; ModuleExpr ;

	fun
	  -- Instructions
	  InstrucInternal : Internal -> InternalRelation -> Instruction ;
	  InstrucExternal : External -> ExternalRelation -> Instruction ;
	  InstrucResource : Resource -> ResourceRelation -> Instruction ;

	  -- Subjects
	  People : Internal ;
	  Customer : External ;
	  Project : Resource ;

      -- Relations
	  Know_R : SkillExpr -> InternalRelation ;
	  UseExt_R : ModuleExpr -> ExternalRelation ;
	  UseRes_R : ModuleExpr -> ResourceRelation ;
	  WorkIn_R : LocationExpr -> InternalRelation ;
	  WorkWith_R : OrganizationExpr -> InternalRelation ;

	  -- Boolean operators for relations
	  InternalAnd : InternalRelation -> InternalRelation -> InternalRelation ;
	  InternalOr : InternalRelation -> InternalRelation -> InternalRelation ;

	  ExternalAnd : ExternalRelation -> ExternalRelation -> ExternalRelation ;
  	  ExternalOr : ExternalRelation -> ExternalRelation -> ExternalRelation ;
 	  
  	  ResourceAnd : ResourceRelation -> ResourceRelation -> ResourceRelation ;
  	  ResourceOr : ResourceRelation -> ResourceRelation -> ResourceRelation ;

  	  -- Unknown names
	  Skill : Symb -> SkillExpr ;
	  Organization : Symb -> OrganizationExpr ;
	  Module: Symb -> ModuleExpr ;
	  Location : Symb -> LocationExpr ;

	  -- Boolean operators for Organizations
      And_S : SkillExpr -> SkillExpr -> SkillExpr ;
      Or_S : SkillExpr -> SkillExpr -> SkillExpr ;

      And_O : OrganizationExpr -> OrganizationExpr -> OrganizationExpr ;
      Or_O : OrganizationExpr -> OrganizationExpr -> OrganizationExpr ;

      And_L : LocationExpr -> LocationExpr -> LocationExpr ;
      Or_L : LocationExpr -> LocationExpr -> LocationExpr ;

      And_M : ModuleExpr -> ModuleExpr -> ModuleExpr ;
      Or_M : ModuleExpr -> ModuleExpr -> ModuleExpr ;
}
