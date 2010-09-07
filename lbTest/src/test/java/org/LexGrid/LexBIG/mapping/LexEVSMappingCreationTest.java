/*
 * Copyright: (c) 2004-2009 Mayo Foundation for Medical Education and 
 * Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
 * triple-shield Mayo logo are trademarks and service marks of MFMER.
 *
 * Except as contained in the copyright notice above, or as used to identify 
 * MFMER as the author of this software, the trade names, trademarks, service
 * marks, or product names of the copyright holder shall not be used in
 * advertising, promotion or otherwise in connection with this software without
 * prior written authorization of the copyright holder.
 * 
 * Licensed under the Eclipse Public License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 * 		http://www.eclipse.org/legal/epl-v10.html
 * 
 */
package org.LexGrid.LexBIG.mapping;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import junit.framework.TestCase;

import org.LexGrid.LexBIG.DataModel.Core.AbsoluteCodingSchemeVersionReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;
import org.LexGrid.LexBIG.Impl.LexEVSAuthoringServiceImpl;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.LexBIGService.LexBIGServiceManager;
import org.LexGrid.relations.AssociationSource;
import org.LexGrid.relations.AssociationTarget;
import org.LexGrid.versions.EntryState;
import org.LexGrid.versions.Revision;
import org.LexGrid.versions.types.ChangeType;

public class LexEVSMappingCreationTest extends TestCase {
	
	   LexEVSAuthoringServiceImpl authoring;
	   LexBIGService lbs;
		LexBIGServiceManager lbsm;
		
	   public void setUp(){

		   authoring = new LexEVSAuthoringServiceImpl();
		   lbs = LexBIGServiceImpl.defaultInstance();


		   try {
			lbsm = lbs.getServiceManager(null);
		} catch (LBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   }
	
	public void testCreateNewMapping()throws LBException{
		   EntryState entryState = new EntryState();
		   entryState.setChangeType(ChangeType.NEW);
		   entryState.setContainingRevision(UUID.randomUUID().toString());
		  entryState.setRelativeOrder(new Long(1));
		   AbsoluteCodingSchemeVersionReference mappingCodingScheme = new AbsoluteCodingSchemeVersionReference();
		   mappingCodingScheme.setCodingSchemeURN(MappingTestConstants.MAPPING_SCHEME);
		   mappingCodingScheme.setCodingSchemeVersion(MappingTestConstants.MAPPING_VERSION);
		AbsoluteCodingSchemeVersionReference sourceCodingScheme = new AbsoluteCodingSchemeVersionReference();
		   sourceCodingScheme.setCodingSchemeURN(MappingTestConstants.SOURCE_SCHEME);
		   sourceCodingScheme.setCodingSchemeVersion(MappingTestConstants.SOURCE_VERSION);
		AbsoluteCodingSchemeVersionReference targetCodingScheme = new AbsoluteCodingSchemeVersionReference();
		   targetCodingScheme.setCodingSchemeURN(MappingTestConstants.TARGET_SCHEME);
		   targetCodingScheme.setCodingSchemeVersion(MappingTestConstants.TARGET_VERSION);
		   AssociationSource source = new AssociationSource();
		   source.setSourceEntityCode("E0001");
		   source.setSourceEntityCodeNamespace("GermanMadePartsNamespace");
		   AssociationTarget target = new AssociationTarget();
		   target.setTargetEntityCode("Ford");
		   target.setTargetEntityCodeNamespace("Automobiles");
		   source.setTarget(Arrays.asList(target));
		   
		AssociationSource[] associationSources = new AssociationSource[]{source};
		String associationType = "SY";
		String relationsContainerName = "Mapping_relations";
		Date effectiveDate = null;
		Revision revision = new Revision();

			authoring.createAssociationMapping(entryState, 
					   mappingCodingScheme, 
					   sourceCodingScheme, 
					   targetCodingScheme, 
					   associationSources, 
					   associationType , 
					   relationsContainerName, 
					   effectiveDate, 
					   null,
					   revision,
					   true);
	
	   }
	
	public void testCreateNewMappingWithNewRelationsContainer()throws LBException{
		   EntryState entryState = new EntryState();
		   entryState.setChangeType(ChangeType.NEW);
		   entryState.setContainingRevision(UUID.randomUUID().toString());
		  entryState.setRelativeOrder(new Long(1));
		   AbsoluteCodingSchemeVersionReference mappingCodingScheme = new AbsoluteCodingSchemeVersionReference();
		   mappingCodingScheme.setCodingSchemeURN(MappingTestConstants.MAPPING_SCHEME);
		   mappingCodingScheme.setCodingSchemeVersion(MappingTestConstants.MAPPING_VERSION);
		AbsoluteCodingSchemeVersionReference sourceCodingScheme = new AbsoluteCodingSchemeVersionReference();
		   sourceCodingScheme.setCodingSchemeURN(MappingTestConstants.SOURCE_SCHEME);
		   sourceCodingScheme.setCodingSchemeVersion(MappingTestConstants.SOURCE_VERSION);
		AbsoluteCodingSchemeVersionReference targetCodingScheme = new AbsoluteCodingSchemeVersionReference();
		   targetCodingScheme.setCodingSchemeURN(MappingTestConstants.TARGET_SCHEME);
		   targetCodingScheme.setCodingSchemeVersion(MappingTestConstants.TARGET_VERSION);
		   AssociationSource source = new AssociationSource();
		   source.setSourceEntityCode("E0001");
		   source.setSourceEntityCodeNamespace("GermanMadePartsNamespace");
		   AssociationTarget target = new AssociationTarget();
		   target.setTargetEntityCode("C0001");
		   target.setTargetEntityCodeNamespace("Automobiles");
		   source.setTarget(Arrays.asList(target));
		   
		AssociationSource[] associationSources = new AssociationSource[]{source};
		String associationType = "SY";
		String relationsContainerName = "Mapping_relations_2";
		Date effectiveDate = null;
		Revision revision = new Revision();

			authoring.createAssociationMapping(entryState, 
					   mappingCodingScheme, 
					   sourceCodingScheme, 
					   targetCodingScheme, 
					   associationSources, 
					   associationType , 
					   relationsContainerName, 
					   effectiveDate, 
					   null, 
					   revision,
					   true);
	}
	public void testCreateNewMappingWithNewAssociationPredicate()throws LBException{
			   EntryState entryState = new EntryState();
			   entryState.setChangeType(ChangeType.NEW);
			   entryState.setContainingRevision(UUID.randomUUID().toString());
			  entryState.setRelativeOrder(new Long(1));
			   AbsoluteCodingSchemeVersionReference mappingCodingScheme = new AbsoluteCodingSchemeVersionReference();
			   mappingCodingScheme.setCodingSchemeURN(MappingTestConstants.MAPPING_SCHEME);
			   mappingCodingScheme.setCodingSchemeVersion(MappingTestConstants.MAPPING_VERSION);
			AbsoluteCodingSchemeVersionReference sourceCodingScheme = new AbsoluteCodingSchemeVersionReference();
			   sourceCodingScheme.setCodingSchemeURN(MappingTestConstants.SOURCE_SCHEME);
			   sourceCodingScheme.setCodingSchemeVersion(MappingTestConstants.SOURCE_VERSION);
			AbsoluteCodingSchemeVersionReference targetCodingScheme = new AbsoluteCodingSchemeVersionReference();
			   targetCodingScheme.setCodingSchemeURN(MappingTestConstants.TARGET_SCHEME);
			   targetCodingScheme.setCodingSchemeVersion(MappingTestConstants.TARGET_VERSION);
			   AssociationSource source = new AssociationSource();
			   source.setSourceEntityCode("E0001");
			   source.setSourceEntityCodeNamespace("GermanMadePartsNamespace");
			   AssociationTarget target = new AssociationTarget();
			   target.setTargetEntityCode("C0001");
			   target.setTargetEntityCodeNamespace("Automobiles");
			   source.setTarget(Arrays.asList(target));
			   
			AssociationSource[] associationSources = new AssociationSource[]{source};
			String associationType = "FI";
			String relationsContainerName = "Mapping_relations_2";
			Date effectiveDate = null;
			Revision revision = new Revision();

				authoring.createAssociationMapping(entryState, 
						   mappingCodingScheme, 
						   sourceCodingScheme, 
						   targetCodingScheme, 
						   associationSources, 
						   associationType , 
						   relationsContainerName, 
						   effectiveDate, 
						   null, 
						   revision,
						   true);
	}
}