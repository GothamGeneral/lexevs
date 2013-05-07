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
package org.LexGrid.LexBIG.Impl.function.query;

// LexBIG Test ID: T1_FNC_15	TestAttributeValueMatch

import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Impl.function.LexBIGServiceTestCase;
import org.LexGrid.LexBIG.Impl.testUtility.ServiceHolder;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.PropertyType;

public class TestAttributeValueMatch extends LexBIGServiceTestCase {
    final static String testID = "T1_FNC_15";

    @Override
    protected String getTestID() {
        return testID;
    }

    private boolean matchAttributeValue(String prop, String value) throws LBException {

        CodedNodeSet cns = ServiceHolder.instance().getLexBIGService().getCodingSchemeConcepts(THES_SCHEME, null);
        LocalNameList lnl = new LocalNameList();
        lnl.addEntry(prop);
        CodedNodeSet matches = cns.restrictToMatchingProperties(lnl, null, value, "startsWith", null);
        int count = matches.resolveToList(null, null, null, 0).getResolvedConceptReferenceCount();
        return (count > 0);
    }

    private boolean matchAttributeValueType(PropertyType prop, String value) throws LBException {

        CodedNodeSet cns = ServiceHolder.instance().getLexBIGService().getCodingSchemeConcepts(THES_SCHEME, null);
        CodedNodeSet matches = cns.restrictToMatchingProperties(null, new PropertyType[] { prop }, value, "startsWith",
                null);
        int count = matches.resolveToList(null, null, null, 0).getResolvedConceptReferenceCount();
        return (count > 0);
    }

    public void testT1_FNC_15a() throws LBException {
        assertTrue(matchAttributeValue("dDEFINITION", "<def"));
    }

    public void testT1_FNC_15b() throws LBException {
        assertFalse(matchAttributeValue("dDEFINITION", "vx"));
    }

    public void testT1_FNC_15c() throws LBException {
        assertTrue(matchAttributeValueType(PropertyType.DEFINITION, "<def"));
    }

    public void testT1_FNC_15d() throws LBException {
        assertFalse(matchAttributeValueType(PropertyType.DEFINITION, "vx"));
    }

}