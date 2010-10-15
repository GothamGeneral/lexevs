package org.LexGrid.LexBIG.Impl.bugs;


import java.util.ArrayList;
import java.util.List;

import org.LexGrid.LexBIG.DataModel.Collections.AssociationList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.AssociatedConcept;
import org.LexGrid.LexBIG.DataModel.Core.Association;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Impl.function.LexBIGServiceTestCase;
import org.LexGrid.LexBIG.Impl.testUtility.ServiceHolder;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeGraph;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.commonTypes.Property;
import org.LexGrid.concepts.Entity;
import org.LexGrid.naming.SupportedAssociation;

public class GForge29924 extends LexBIGServiceTestCase {
    final static String testID = "GForge29924";
    
    @Override
    protected String getTestID() {
        return testID;
    }
    
    /**
     * 
     * 
     * GForge #29924
     * https://gforge.nci.nih.gov/tracker/index.php?func=detail&aid=29924&group_id=491&atid=1850
     * 
     * @throws Throwable
     */
    public void testSupportedAssociationLocalid() throws Throwable {
    	LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
    	CodingSchemeVersionOrTag csvt = Constructors.createCodingSchemeVersionOrTagFromVersion(CAMERA_SCHEME_MANIFEST_VERSION);
    	CodingScheme cs = lbs.resolveCodingScheme(CAMERA_SCHEME_MANIFEST_URN, csvt);
    	for (SupportedAssociation sa :cs.getMappings().getSupportedAssociation()) {
    		assertEquals(sa.getLocalId().contains(" "), false);
    	}
    }
}