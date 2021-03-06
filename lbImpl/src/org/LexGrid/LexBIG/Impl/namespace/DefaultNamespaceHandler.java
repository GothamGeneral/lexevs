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
package org.LexGrid.LexBIG.Impl.namespace;

import java.util.ArrayList;
import java.util.List;

import org.LexGrid.LexBIG.DataModel.Core.AbsoluteCodingSchemeVersionReference;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.ServiceUtility;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.naming.SupportedCodingScheme;
import org.LexGrid.naming.SupportedNamespace;
import org.apache.commons.lang.StringUtils;
import org.lexevs.cache.annotation.CacheMethod;
import org.lexevs.cache.annotation.Cacheable;
import org.lexevs.dao.database.service.codingscheme.CodingSchemeService;
import org.lexevs.locator.LexEvsServiceLocator;
import org.lexevs.logging.LoggerFactory;
import org.lexevs.registry.model.RegistryEntry;
import org.lexevs.registry.service.Registry;
import org.lexevs.registry.service.Registry.KnownTags;
import org.lexevs.registry.service.Registry.ResourceType;
import org.springframework.util.CollectionUtils;

@Cacheable(cacheName = "DefaultNamespaceHandlerCache")
public class DefaultNamespaceHandler implements NamespaceHandler {

    private static final long serialVersionUID = 7565547967975571009L;
    
    @Override
    @CacheMethod
    public String getCodingSchemeNameForNamespace(String codingSchemeUri, String version, String namespace)
            throws LBParameterException {
        CodingScheme cs = getCodingScheme(codingSchemeUri, version);
        
        if(namespace.equals(cs.getCodingSchemeName())) {
            return cs.getCodingSchemeName();
        }
        
        SupportedNamespace sns = getSupportedNamespace(cs, namespace);
        
        if(sns == null){
            return null;
        } else {
            String equivalentNamespace = 
                sns.getEquivalentCodingScheme();
            if(equivalentNamespace != null ) {
                return equivalentNamespace;
            } else {
                return ServiceUtility.getCodingSchemeName(codingSchemeUri, version);
            }
        }
    }

    @Override
    @CacheMethod
    public AbsoluteCodingSchemeVersionReference
    getCodingSchemeForNamespace(String codingSchemeUri, String version,
            String namespace) throws LBParameterException {
        
        CodingScheme cs = getCodingScheme(codingSchemeUri, version);
        
        SupportedNamespace sns = getSupportedNamespace(cs, namespace);
        
        if(sns == null || 
                StringUtils.isBlank(sns.getEquivalentCodingScheme()) ||
                namespace.equals(cs.getCodingSchemeName())){
            return Constructors.createAbsoluteCodingSchemeVersionReference(codingSchemeUri, version);
        }
        String uri;
        
        SupportedCodingScheme scs = this.getSupportedCodingScheme(cs, sns.getEquivalentCodingScheme());
        
        if(scs != null && StringUtils.isNotBlank(scs.getUri())){
            uri = scs.getUri();
        } else {
            try {
                uri = LexEvsServiceLocator.getInstance().
                    getSystemResourceService().getUriForUserCodingSchemeName(sns.getEquivalentCodingScheme(), null);
            } catch (Exception e) {
                LoggerFactory.getLogger().info("The Equivalent Coding Scheme:" + sns.getEquivalentCodingScheme() + " was not found in the system.");
                return null;
            }
        }
        
        Registry registry = LexEvsServiceLocator.getInstance().getRegistry();
        
        List<RegistryEntry> entries = registry.getAllRegistryEntriesOfTypeAndURI(ResourceType.CODING_SCHEME, uri);
        
        if(CollectionUtils.isEmpty(entries)) {
            return null;
        }
        
        RegistryEntry entry = getProductionEntry(entries);
        
        return 
            Constructors.createAbsoluteCodingSchemeVersionReference(
                    entry.getResourceUri(),
                    entry.getResourceVersion());
        
    }
    
    private RegistryEntry getProductionEntry(List<RegistryEntry> entries) {
        for(RegistryEntry entry : entries) {
            if(entry.getTag() != null && 
                    entry.getTag().equals(KnownTags.PRODUCTION.toString())) {
                return entry;
            }
        }
        return entries.get(0);
    }
    
    private CodingScheme getCodingScheme(String uri, String version) {
        CodingSchemeService service = 
            LexEvsServiceLocator.getInstance().getDatabaseServiceManager().getCodingSchemeService();
        
        return service.getCodingSchemeByUriAndVersion(uri, version);  
    }
    
    private SupportedNamespace getSupportedNamespace(CodingScheme cs, String namespace) {
        for(SupportedNamespace sn : cs.getMappings().getSupportedNamespace()) {
            if(StringUtils.equals(sn.getLocalId(), namespace)) {
                return sn;
            }
        }
        return null;
    }
    
    private SupportedCodingScheme getSupportedCodingScheme(CodingScheme cs, String codingSchemeName) {
        for(SupportedCodingScheme scs : cs.getMappings().getSupportedCodingScheme()) {
            if(StringUtils.equals(scs.getLocalId(), codingSchemeName)) {
                return scs;
            }
        }
        return null;
    }

    @Override
    @CacheMethod
    public List<String> getNamespacesForCodingScheme(
            String codingSchemeUri, 
            String version,
            String codingSchemeNameOfSearchCodingScheme)
            throws LBParameterException {
        List<String> returnList = new ArrayList<String>();
        CodingScheme cs = this.getCodingScheme(codingSchemeUri, version);
       
        for(SupportedNamespace sn : cs.getMappings().getSupportedNamespace()) {
            if(StringUtils.equals(
                    sn.getEquivalentCodingScheme(),
                    codingSchemeNameOfSearchCodingScheme)){
                returnList.add(sn.getLocalId());
            } else {
                if(StringUtils.equals(codingSchemeNameOfSearchCodingScheme, sn.getLocalId())
                        &&
                   StringUtils.equals(codingSchemeNameOfSearchCodingScheme, cs.getCodingSchemeName())
                       &&
                   StringUtils.isBlank(sn.getEquivalentCodingScheme())){
                        returnList.add(sn.getLocalId());
                }
            }
        }
        
        return returnList;   
    }
}