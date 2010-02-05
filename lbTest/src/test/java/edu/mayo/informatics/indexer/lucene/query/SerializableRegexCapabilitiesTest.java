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
package edu.mayo.informatics.indexer.lucene.query;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.LexGrid.LexBIG.Impl.function.LexBIGServiceTestCase;

/**
 * The Class SerializableRegexCapabilitiesTest.
 * 
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public class SerializableRegexCapabilitiesTest extends LexBIGServiceTestCase {

    /** The serializable regex capabilities. */
    private SerializableRegexCapabilities serializableRegexCapabilities;
    
    /* (non-Javadoc)
     * @see org.LexGrid.LexBIG.Impl.function.LexBIGServiceTestCase#getTestID()
     */
    @Override
    protected String getTestID() {
        return "SerializableRegexCapabilities Test";
    }
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp(){
        serializableRegexCapabilities = new SerializableRegexCapabilities();
    }
    
    /**
     * Test is serializable.
     * 
     * @throws Exception the exception
     */
    public void testIsSerializable() throws Exception{
        ObjectOutputStream out = new ObjectOutputStream(new OutputStream(){

            @Override
            public void write(int b) throws IOException {
               //no-op
            }      
        });
        out.writeObject(serializableRegexCapabilities);
        out.close();
    }
}
