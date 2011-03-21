/*
 * Copyright: (c) 2004-2011 Mayo Foundation for Medical Education and 
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
package org.cts2.internal.model.uri.restrict;

import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.cts2.core.VersionTagReference;
import org.cts2.service.core.NameOrURI;

/**
 * The Interface EntityDescriptionRestrictionHandler.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public interface EntityDescriptionRestrictionHandler extends NonIterableBasedResolvingRestrictionHandler<CodedNodeSet> {

	/**
	 * Restrict to code systems.
	 *
	 * @param codeSystems the code systems
	 * @param tag the tag
	 * @return the restriction
	 */
	public Restriction<CodedNodeSet> restrictToCodeSystems(NameOrURI codeSystems, VersionTagReference tag);
	
	/**
	 * Restrict to code system versions.
	 *
	 * @param codeSystemVersions the code system versions
	 * @return the restriction
	 */
	public Restriction<CodedNodeSet> restrictToCodeSystemVersions(NameOrURI codeSystemVersions);
}