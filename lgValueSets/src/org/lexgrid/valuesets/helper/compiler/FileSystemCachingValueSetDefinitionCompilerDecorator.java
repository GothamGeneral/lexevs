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
package org.lexgrid.valuesets.helper.compiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.lexevs.locator.LexEvsServiceLocator;
import org.lexevs.logging.LoggerFactory;
import org.lexgrid.valuesets.helper.VSDServiceHelper;

/**
 * The Class FileSystemCachingValueSetDefinitionCompilerDecorator.
 * 
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public class FileSystemCachingValueSetDefinitionCompilerDecorator extends AbstractCachingValueSetDefinitionCompilerDecorator {

	private static String COMPILED_VS_DIR = "compiledVSDefinitions";

	private static String COMPILED_VS_FILE_EXTENSION = ".cvd";
	
	private static int MAX_IN_CACHE = 500;

	/**
	 * Instantiates a new caching value set definition compiler decorator.
	 * 
	 * @param delegate the delegate
	 */
	public FileSystemCachingValueSetDefinitionCompilerDecorator(ValueSetDefinitionCompiler delegate, VSDServiceHelper vsdServiceHelper){
		super(delegate, vsdServiceHelper);
		
		File cacheLocation = new File(this.getDiskStorePath());
		
		if(! cacheLocation.exists()){
			LoggerFactory.getLogger().info("Creating Initial Compiled Value Set Definition Store at: " + cacheLocation.getPath());
			
			if(cacheLocation.mkdir()){
				LoggerFactory.getLogger().info("Initial Compiled Value Set Definition Store Creation Success.");
			} else {
				LoggerFactory.getLogger().info("Initial Compiled Value Set Definition Store Creation Failed... caching will not be used.");
			}
		}
	}
	
	
	/**
	 * Persist coded node set.
	 * 
	 * @param uuid the uuid
	 * @param cns the cns
	 * 
	 * @throws Exception the exception
	 */
	protected void persistCodedNodeSet(int uuid, CodedNodeSet cns) {
		try {
			File cachedCnsFile = new File(this.getDiskStorePath() + File.separator + this.getFileName(uuid));
			
			if(cachedCnsFile.exists()){
				LoggerFactory.getLogger().info("Compiled Value Set Definition already cached.");
				
				return;
			} 
			
			this.deleteOldestFile();
			
			ObjectOutput out = new ObjectOutputStream(new FileOutputStream(this.getDiskStorePath()	+ File.separator + this.getFileName(uuid))); 
			out.writeObject(cns); 
			out.close();
		} catch (Exception e) {
			LoggerFactory.getLogger().warn("There was an error persisting the Compiled Value Set Definition." +
					" Caching will not be used for this Value Set Definition.", e);
		}
	}
	
	private String getFileName(int uuid){
		String uuidString = String.valueOf(uuid);
		
		uuidString = uuidString.replaceAll("-", "n");
		
		return uuidString + COMPILED_VS_FILE_EXTENSION;
	}

	/**
	 * Retrieve coded node set.
	 * 
	 * @param uuid the uuid
	 * 
	 * @return the coded node set
	 * 
	 * @throws Exception the exception
	 */
	protected CodedNodeSet retrieveCodedNodeSet(int uuid) {

		try {
			
			File cachedCnsFile = new File(this.getDiskStorePath() + File.separator + this.getFileName(uuid));
			if(! cachedCnsFile.exists()){
				LoggerFactory.getLogger().info("Compiled Value Set Definition cache miss.");
				
				return null;
			} else {
				LoggerFactory.getLogger().info("Compiled Value Set Definition cache hit.");
				
				FileUtils.touch(cachedCnsFile);	
			}
			
			ObjectInput in = new ObjectInputStream(new FileInputStream(cachedCnsFile)); 
			CodedNodeSet cns;
			try {
				cns = (CodedNodeSet)in.readObject();
			} catch (Exception e) {
				LoggerFactory.getLogger().warn("Compiled Value Set Definition was found, but it is invalid or corrupted. Removing...", e);
				if(! FileUtils.deleteQuietly(cachedCnsFile)){
					FileUtils.forceDeleteOnExit(cachedCnsFile);
				}
				
				throw e;
			}
			
			in.close();
			
			if(cns == null) {
				return null;
			} else {
				return cns;
			}
		} catch (Exception e) {
			LoggerFactory.getLogger().warn("There was an error retrieving the Compiled Value Set Definition from the Cache." +
					" Caching will not be used for this Value Set Definition.", e);
			
			return null;
		} 
	}
	
	@SuppressWarnings("unchecked")
	private void deleteOldestFile(){
		File[] files = new File(getDiskStorePath()).listFiles();

		if(files != null && files.length > 0 && files.length >= MAX_IN_CACHE){

			Arrays.sort(files,LastModifiedFileComparator.LASTMODIFIED_COMPARATOR);
			
			try {
				if(! files[0].delete()){
					files[0].deleteOnExit();
				}
			} catch (Exception e) {
				LoggerFactory.getLogger().warn("There was an error evicting a Compiled Value Set Definition from the Cache.", e);
			}	
		}
	}

     /**
      * Gets the disk store path.
      * 
      * @return the disk store path
      */
     private String getDiskStorePath() {
    	 String configPath = 
    			LexEvsServiceLocator.getInstance().getSystemResourceService().
    				getSystemVariables().getAutoLoadIndexLocation();
    	 
    	 return configPath.substring(0, configPath.lastIndexOf(File.separator) + 1) + COMPILED_VS_DIR;
     }
}
