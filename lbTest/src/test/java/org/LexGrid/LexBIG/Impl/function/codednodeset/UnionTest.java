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
package org.LexGrid.LexBIG.Impl.function.codednodeset;

import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;
import org.LexGrid.LexBIG.Impl.function.LexBIGServiceTestCase;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.SearchDesignationOption;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.util.PrintUtility;

/**
 * The Class UnionTest.
 * 
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public class UnionTest extends BaseCodedNodeSetTest {
    
    /** The cns2. */
    private CodedNodeSet cns2;
    
    /** The cns3. */
    private CodedNodeSet cns3;
    
    /* (non-Javadoc)
     * @see org.LexGrid.LexBIG.Impl.function.codednodeset.BaseCodedNodeSetTest#getTestID()
     */
    @Override
    protected String getTestID() {
        return "CodedNodeSet Union Test";
    }
    
    /* (non-Javadoc)
     * @see org.LexGrid.LexBIG.Impl.function.codednodeset.BaseCodedNodeSetTest#setUp()
     */
    @Override
    public void setUp(){
        super.setUp();
        try {
            cns2 = lbs.getCodingSchemeConcepts(LexBIGServiceTestCase.AUTO_SCHEME, null);
            cns3 = lbs.getCodingSchemeConcepts(LexBIGServiceTestCase.AUTO_SCHEME, null);
        } catch (LBException e) {
          fail(e.getMessage());
        }
    }
    
    /**
     * Test union.
     * 
     * @throws LBException the LB exception
     */
    public void testUnion() throws LBException {
        
        cns.restrictToCodes(Constructors.createConceptReferenceList(new String[] { "005" }, LexBIGServiceTestCase.AUTO_SCHEME));

        cns2.restrictToCodes(Constructors.createConceptReferenceList(new String[] { "Ford" }, LexBIGServiceTestCase.AUTO_SCHEME));

        CodedNodeSet cnsUnion = cns.union(cns2);

        ResolvedConceptReference[] rcr = cnsUnion.resolveToList(null, null, null, 50).getResolvedConceptReference();

        assertTrue("Length: " + rcr.length, rcr.length == 2);
        assertTrue(contains(rcr, "005", LexBIGServiceTestCase.AUTO_SCHEME));
        assertTrue(contains(rcr, "Ford", LexBIGServiceTestCase.AUTO_SCHEME));
    }
    
    /**
     * Test union with added restriction.
     * 
     * @throws LBException the LB exception
     */
    public void testUnionWithAddedRestriction() throws LBException {
        
        cns.restrictToCodes(Constructors.createConceptReferenceList(new String[] { "005" }, LexBIGServiceTestCase.AUTO_SCHEME));

        cns2.restrictToCodes(Constructors.createConceptReferenceList(new String[] { "Ford" }, LexBIGServiceTestCase.AUTO_SCHEME));

        CodedNodeSet cns3 = cns.union(cns2);
        
        cns3.restrictToCodes(Constructors.createConceptReferenceList("005"));

        ResolvedConceptReference[] rcr = cns3.resolveToList(null, null, null, 50).getResolvedConceptReference();

        assertTrue("Actual Length: " + rcr.length, rcr.length == 1);
        assertTrue(contains(rcr, "005", LexBIGServiceTestCase.AUTO_SCHEME));
    }

    /**
     * Test union to self.
     * 
     * @throws LBException the LB exception
     */
    public void testUnionToSelf() throws LBException {

        cns.restrictToCodes(Constructors.createConceptReferenceList(new String[] { "005" }, LexBIGServiceTestCase.AUTO_SCHEME));

        CodedNodeSet union = cns.union(cns);

        ResolvedConceptReference[] rcr = union.resolveToList(null, null, null, 50).getResolvedConceptReference();

        assertTrue(rcr.length == 1);
        assertTrue(contains(rcr, "005", LexBIGServiceTestCase.AUTO_SCHEME));
    }
    
    /**
     * Test union cross coding scheme.
     * 
     * @throws LBException the LB exception
     */
    public void testUnionCrossCodingScheme() throws LBException {

        cns.restrictToCodes(Constructors.createConceptReferenceList(new String[] { "005" }, LexBIGServiceTestCase.AUTO_SCHEME));

        CodedNodeSet gmCns = lbs.getCodingSchemeConcepts(LexBIGServiceTestCase.PARTS_SCHEME, null);
        gmCns.restrictToCodes(Constructors.createConceptReferenceList("P0001"));
        
        CodedNodeSet union = cns.union(gmCns);

        ResolvedConceptReference[] rcr = union.resolveToList(null, null, null, 50).getResolvedConceptReference();

        assertTrue(rcr.length == 2);
        assertTrue(contains(rcr, "005", LexBIGServiceTestCase.AUTO_SCHEME));
        assertTrue(contains(rcr, "P0001", LexBIGServiceTestCase.PARTS_SCHEME));
    }
    
    /**
     * Test union to a union.
     * 
     * @throws LBException the LB exception
     */
    public void testUnionToAUnion() throws LBException {

        cns.restrictToCodes(Constructors.createConceptReferenceList(new String[] { "005" }, LexBIGServiceTestCase.AUTO_SCHEME));
        
        cns2.restrictToCodes(Constructors.createConceptReferenceList(new String[] { "Ford" }, LexBIGServiceTestCase.AUTO_SCHEME));
        
        cns3.restrictToCodes(Constructors.createConceptReferenceList(new String[] { "GM" }, LexBIGServiceTestCase.AUTO_SCHEME));

        CodedNodeSet union = cns.union(cns2);
        
        CodedNodeSet superUnion = union.union(cns3);

        ResolvedConceptReference[] rcr = superUnion.resolveToList(null, null, null, 50).getResolvedConceptReference();

        assertEquals(3,rcr.length);
        assertTrue(contains(rcr, "005", LexBIGServiceTestCase.AUTO_SCHEME));
        assertTrue(contains(rcr, "Ford", LexBIGServiceTestCase.AUTO_SCHEME));
        assertTrue(contains(rcr, "GM", LexBIGServiceTestCase.AUTO_SCHEME));
    }
    
    /**
     * Test union with a union.
     * 
     * @throws LBException the LB exception
     */
    public void testUnionWithAUnion() throws LBException {

        cns.restrictToCodes(Constructors.createConceptReferenceList(new String[] { "005" }, LexBIGServiceTestCase.AUTO_SCHEME));
        
        cns2.restrictToCodes(Constructors.createConceptReferenceList(new String[] { "Ford" }, LexBIGServiceTestCase.AUTO_SCHEME));
        
        cns3.restrictToCodes(Constructors.createConceptReferenceList(new String[] { "GM" }, LexBIGServiceTestCase.AUTO_SCHEME));

        CodedNodeSet union = cns.union(cns2);
        
        CodedNodeSet superUnion = cns3.union(union);

        ResolvedConceptReference[] rcr = superUnion.resolveToList(null, null, null, 50).getResolvedConceptReference();

        assertEquals(3,rcr.length);
        assertTrue(contains(rcr, "005", LexBIGServiceTestCase.AUTO_SCHEME));
        assertTrue(contains(rcr, "Ford", LexBIGServiceTestCase.AUTO_SCHEME));
        assertTrue(contains(rcr, "GM", LexBIGServiceTestCase.AUTO_SCHEME));
    }
    
    public void testMultipleUnions() throws Exception {
    	CodedNodeSet cns1 = 
			lbs.getNodeSet(LexBIGServiceTestCase.AUTO_SCHEME, null, null);
		cns1 = cns1.restrictToCodes(Constructors.createConceptReferenceList("005"));
		
		CodedNodeSet cns2 = 
			lbs.getNodeSet(LexBIGServiceTestCase.AUTO_SCHEME, null, null);
		cns2 = cns2.restrictToCodes(Constructors.createConceptReferenceList("A0001"));
		
		CodedNodeSet cns3 = 
			lbs.getNodeSet(LexBIGServiceTestCase.AUTO_SCHEME, null, null);
		cns3 = cns3.restrictToCodes(Constructors.createConceptReferenceList("C0001"));
		
		CodedNodeSet cns4 = 
			lbs.getNodeSet(LexBIGServiceTestCase.AUTO_SCHEME, null, null);
		cns4 = cns4.restrictToCodes(Constructors.createConceptReferenceList("Ford"));

		
		CodedNodeSet cns5 = cns1.union(cns2).union(cns3).union(cns4);
		
		assertEquals(4, cns5.resolve(null, null, null).numberRemaining());
    }
    
    public void testMultipleUnionsWithRestriction() throws Exception {
    	CodedNodeSet cns1 = 
			lbs.getNodeSet(LexBIGServiceTestCase.AUTO_SCHEME, null, null);
		cns1 = cns1.restrictToCodes(Constructors.createConceptReferenceList("005"));
		
		CodedNodeSet cns2 = 
			lbs.getNodeSet(LexBIGServiceTestCase.AUTO_SCHEME, null, null);
		cns2 = cns2.restrictToCodes(Constructors.createConceptReferenceList("A0001"));
		
		CodedNodeSet cns3 = 
			lbs.getNodeSet(LexBIGServiceTestCase.AUTO_SCHEME, null, null);
		cns3 = cns3.restrictToCodes(Constructors.createConceptReferenceList("C0001"));
		
		CodedNodeSet cns4 = 
			lbs.getNodeSet(LexBIGServiceTestCase.AUTO_SCHEME, null, null);
		cns4 = cns4.restrictToCodes(Constructors.createConceptReferenceList("Ford"));

		
		CodedNodeSet cns5 = cns1.union(cns2).union(cns3).union(cns4);
		cns5 = cns5.restrictToCodes(Constructors.createConceptReferenceList("Ford"));
		
		assertEquals(1, cns5.resolve(null, null, null).numberRemaining());
    }
    
    public void testUnionWithCodesAndPropertyRestriction() throws Exception {
    	CodedNodeSet cns1 = 
			lbs.getNodeSet(LexBIGServiceTestCase.AUTO_SCHEME, null, null);
		cns1 = cns1.restrictToMatchingDesignations("General", SearchDesignationOption.ALL, "LuceneQuery", null);
		
		CodedNodeSet cns2 = 
			lbs.getNodeSet(LexBIGServiceTestCase.PARTS_SCHEME, null, null);

		cns2 = cns2.restrictToCodes(Constructors.createConceptReferenceList(new String[] {"P0001", "R0001"}));
		
		assertEquals(3, cns1.union(cns2).resolve(null, null, null).numberRemaining());
    }
    
    public void testUnionWithCodesAndPropertyRestrictionUnionOtherWay() throws Exception {
    	CodedNodeSet cns1 = 
			lbs.getNodeSet(LexBIGServiceTestCase.AUTO_SCHEME, null, null);
		cns1 = cns1.restrictToMatchingDesignations("General", SearchDesignationOption.ALL, "LuceneQuery", null);
		
		CodedNodeSet cns2 = 
			lbs.getNodeSet(LexBIGServiceTestCase.PARTS_SCHEME, null, null);

		cns2 = cns2.restrictToCodes(Constructors.createConceptReferenceList(new String[] {"P0001", "R0001"}));
		
		assertEquals(3, cns2.union(cns1).resolve(null, null, null).numberRemaining());
    }
}