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
package edu.mayo.informatics.indexer.lucene.analyzers;

import java.io.StringReader;

import junit.framework.TestCase;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;

/**
 * Test cases for the WhiteSpaceLowerCaseAnalyzer.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 * @version 1.0 - cvs $Revision: 1.1 $ checked in on $Date: 2005/08/24 15:00:43
 *          $
 */
public class WhiteSpaceLowerCaseAnalyzerTest extends TestCase {
    public void testLowerCaseStopRemoval() throws Exception {
        WhiteSpaceLowerCaseAnalyzer temp = new WhiteSpaceLowerCaseAnalyzer(new String[] { "foo", "bar" },
                new char[] { ',' }, new char[] { '-' });

        String input = new String("A test String foo Foo");

        StringReader reader = new StringReader(input);
        TokenStream result = temp.tokenStream("test", reader);

        Token token = result.next();
        assertTrue(token.termText().equals("a"));
        assertTrue(token.getPositionIncrement() == 1);
        assertTrue(token.startOffset() == 0);
        assertTrue(token.endOffset() == 1);

        token = result.next();
        assertTrue(token.termText().equals("test"));
        assertTrue(token.getPositionIncrement() == 1);
        assertTrue(token.startOffset() == 2);
        assertTrue(token.endOffset() == 6);

        token = result.next();
        assertTrue(token.termText().equals("string"));
        assertTrue(token.getPositionIncrement() == 1);
        assertTrue(token.startOffset() == 7);
        assertTrue(token.endOffset() == 13);

        assertTrue(result.next() == null);
    }

    public void testCharRemoval() throws Exception {
        WhiteSpaceLowerCaseAnalyzer temp = new WhiteSpaceLowerCaseAnalyzer(new String[] { "foo", "bar" },
                new char[] { ',' }, new char[] { '-' });

        String input = new String("foo, test, me");

        StringReader reader = new StringReader(input);
        TokenStream result = temp.tokenStream("test", reader);

        Token token = result.next();
        assertTrue(token.termText().equals("test"));
        assertTrue(token.getPositionIncrement() == 1);
        assertTrue(token.startOffset() == 5);
        assertTrue(token.endOffset() == 10);

        token = result.next();
        assertTrue(token.termText().equals("me"));
        assertTrue(token.getPositionIncrement() == 1);
        assertTrue(token.startOffset() == 11);
        assertTrue(token.endOffset() == 13);

        assertTrue(result.next() == null);
    }

    public void testWhiteSpaceAdditions() throws Exception {
        WhiteSpaceLowerCaseAnalyzer temp = new WhiteSpaceLowerCaseAnalyzer(new String[] { "foo", "bar" },
                new char[] { ',' }, new char[] { '-' });

        String input = new String("foo,- Test-some me-");

        StringReader reader = new StringReader(input);
        TokenStream result = temp.tokenStream("test", reader);

        Token token = result.next();
        assertTrue(token.termText().equals("test"));
        assertTrue(token.getPositionIncrement() == 1);
        assertTrue(token.startOffset() == 6);
        assertTrue(token.endOffset() == 10);

        token = result.next();
        assertTrue(token.termText().equals("some"));
        assertTrue(token.getPositionIncrement() == 1);
        assertTrue(token.startOffset() == 11);
        assertTrue(token.endOffset() == 15);

        token = result.next();
        assertTrue(token.termText().equals("me"));
        assertTrue(token.getPositionIncrement() == 1);
        assertTrue(token.startOffset() == 16);
        assertTrue(token.endOffset() == 18);

        assertTrue(result.next() == null);
    }

}