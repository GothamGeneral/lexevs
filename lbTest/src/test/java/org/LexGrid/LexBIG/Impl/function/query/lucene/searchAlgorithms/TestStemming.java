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
package org.LexGrid.LexBIG.Impl.function.query.lucene.searchAlgorithms;

import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.SearchDesignationOption;

/**
 * The Class TestStemming.
 * 
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public class TestStemming extends BaseSearchAlgorithmTest {

    /** The algorithm. */
    private static String algorithm = "StemmedLuceneQuery";

    /** The match code. */
    private static String matchCode = "A0001";

    /*
     * (non-Javadoc)
     * 
     * @seeorg.LexGrid.LexBIG.Impl.function.query.lucene.searchAlgorithms.
     * BaseSearchAlgorithmTest#getTestID()
     */
    @Override
    protected String getTestID() {
        return "Lucene stemmedLuceneQuery tests";
    }

    /**
     * Test stemmed lucene query match.
     * 
     * @throws Exception the exception
     */
    public void testStemmedLuceneQueryMatch() throws Exception {
        CodedNodeSet cns = super.getAutosCodedNodeSet();
        cns.restrictToMatchingDesignations("Automobiles", SearchDesignationOption.PREFERRED_ONLY, algorithm, null);

        ResolvedConceptReference[] rcrl = cns.resolveToList(null, null, null, -1).getResolvedConceptReference();

        assertTrue("Length: " + rcrl.length, rcrl.length == 1);

        assertTrue(checkForMatch(rcrl, matchCode));
    }

    /**
     * Test stemmed lucene query match other than plural.
     * 
     * @throws Exception the exception
     */
    public void testStemmedLuceneQueryMatchOtherThanPlural() throws Exception {
        CodedNodeSet cns = super.getAutosCodedNodeSet();

        // Note 'Automobiled'... cool word huh?
        cns.restrictToMatchingDesignations("Automobiled", SearchDesignationOption.PREFERRED_ONLY, algorithm, null);

        ResolvedConceptReference[] rcrl = cns.resolveToList(null, null, null, -1).getResolvedConceptReference();

        assertTrue("Length: " + rcrl.length, rcrl.length == 1);

        assertTrue(checkForMatch(rcrl, matchCode));
    }

    /**
     * Test stemmed lucene query case insensitive.
     * 
     * @throws Exception the exception
     */
    public void testStemmedLuceneQueryCaseInsensitive() throws Exception {
        CodedNodeSet cns = super.getAutosCodedNodeSet();
        cns.restrictToMatchingDesignations("automobiles", SearchDesignationOption.PREFERRED_ONLY, algorithm, null);

        ResolvedConceptReference[] rcrl = cns.resolveToList(null, null, null, -1).getResolvedConceptReference();

        assertTrue("Length: " + rcrl.length, rcrl.length == 1);

        assertTrue(checkForMatch(rcrl, matchCode));
    }
}