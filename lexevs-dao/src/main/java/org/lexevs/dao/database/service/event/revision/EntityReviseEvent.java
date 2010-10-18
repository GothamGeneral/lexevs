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
package org.lexevs.dao.database.service.event.revision;

import org.LexGrid.concepts.Entity;

public class EntityReviseEvent extends ReviseEvent<Entity>{
	
	private String codingSchemeUri;
	private String codingSchemeVersion;
	
	public String getCodingSchemeUri() {
		return codingSchemeUri;
	}
	public void setCodingSchemeUri(String codingSchemeUri) {
		this.codingSchemeUri = codingSchemeUri;
	}
	public String getCodingSchemeVersion() {
		return codingSchemeVersion;
	}
	public void setCodingSchemeVersion(String codingSchemeVersion) {
		this.codingSchemeVersion = codingSchemeVersion;
	}
}