/*
 * Copyright: (c) 2004-2010 Mayo Foundation for Medical Education and 
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
package org.lexgrid.loader.meta.integration.dataload;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.concepts.Definition;
import org.junit.Before;
import org.junit.Test;
import org.lexgrid.loader.rrf.constants.RrfLoaderConstants;
import org.lexgrid.loader.test.util.DataTestUtils;

/**
 * The Class DefinitionQualifiersDataTestIT.
 * 
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public class DefinitionQualifiersDataTestIT extends DataLoadTestBase {

	/** The test entity with definition. */
	private Definition definition;

	
	@Before
	public void buildTestEntity() throws Exception {
		cns.restrictToCodes(Constructors.createConceptReferenceList("C0000039"));
		ResolvedConceptReference rcr1 = cns.resolveToList(null, null, null, -1).getResolvedConceptReference()[0];
	
		Definition[] defs = rcr1.getEntity().getDefinition();
		
		assertTrue(defs.length == 1);
		
		definition = defs[0];
	}
	
	@Test
	public void testDefinitionNotNull() throws Exception {	
		assertNotNull(definition);
	}
	
	/*
	 * gforge: 21723 mcturk 8/17/2009
	 * ATUI, SUPPRESS, CVF, SATAUI, column values will 
	 * be loaded as property qualifiers on the 
	 * Definition type property derived from MRDEF column.
	 */
	@Test
	public void testDefinitionWithPropertyQualifierAui() throws Exception {	
		assertTrue(DataTestUtils.isQualifierNameAndValuePresentInProperty(RrfLoaderConstants.AUI_QUALIFIER, "A4222344", definition));
	}
	
	@Test
	public void testDefinitionWithPropertyQualifierAtui() throws Exception {	
		assertTrue(DataTestUtils.isQualifierNameAndValuePresentInProperty(RrfLoaderConstants.ATUI_QUALIFIER, "AT22533916", definition));
	}
	
	@Test
	public void testDefinitionWithPropertyQualifierSuppress() throws Exception {	
		assertTrue(DataTestUtils.isQualifierNameAndValuePresentInProperty(RrfLoaderConstants.SUPPRESS_QUALIFIER, "N", definition));
	}
	
	@Test
	public void testDefinitionWithPropertyQualifierCvf() throws Exception {
		// AT22533916	
		assertTrue(DataTestUtils.isQualifierNameAndValuePresentInProperty(RrfLoaderConstants.CVF_QUALIFIER, "cvfVal", definition));
	}
	
	public void testDefinitionWithPropertyQualifierSataui() throws Exception {
		// AT22533916	
		assertTrue(DataTestUtils.isQualifierNameAndValuePresentInProperty(RrfLoaderConstants.SATUI_QUALIFIER, "satauiVal", definition));
	}
}