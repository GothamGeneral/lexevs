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
package org.lexevs.dao.index.lucene.v2010.entity;

import java.util.List;

import org.LexGrid.LexBIG.DataModel.Core.AbsoluteCodingSchemeVersionReference;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.lexevs.dao.index.access.entity.CommonEntityDao;
import org.lexevs.dao.index.lucene.AbstractBaseLuceneIndexTemplateDao;
import org.lexevs.dao.index.lucenesupport.LuceneIndexTemplate;
import org.lexevs.dao.index.version.LexEvsIndexFormatVersion;

import edu.mayo.informatics.indexer.lucene.hitcollector.BestScoreOfEntityHitCollector;

/**
 * The Class LuceneEntityDao.
 * 
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public class SingleTemplateDisposableLuceneCommonEntityDao extends AbstractBaseLuceneIndexTemplateDao implements CommonEntityDao {
	
	private LuceneIndexTemplate template;
	
	private List<AbsoluteCodingSchemeVersionReference> references;

	public SingleTemplateDisposableLuceneCommonEntityDao(
			LuceneIndexTemplate template,
			List<AbsoluteCodingSchemeVersionReference> references){
		this.template = template;
		this.references = references;
	}

	@Override
	public Document getDocumentById(int id) {
		return template.getDocumentById(id);
	}

	@Override
	public String getIndexName() {
		return template.getIndexName();
	}

	@Override
	public List<ScoreDoc> query(Query query) {
		int maxDoc = template.getMaxDoc();

		Filter boundaryDocFilter = this.getBoundaryDocFilterForCodingScheme(references);
		
		DocIdSet docIdSet = template.getDocIdSet(boundaryDocFilter);

		BestScoreOfEntityHitCollector hitCollector = 
			new BestScoreOfEntityHitCollector(docIdSet.iterator(), maxDoc);

		template.search(query, this.getCodingSchemeFilterForCodingScheme(references), hitCollector);
		
		return hitCollector.getResult();
	}

	@Override
	public void optimizeIndex() {
		template.optimize();
	}

	@Override
	protected LuceneIndexTemplate getLuceneIndexTemplate(
			String codingSchemeUri, String version) {
		return this.template;
	}

	@Override
	public List<LexEvsIndexFormatVersion> doGetSupportedLexEvsIndexFormatVersions() {
		throw new UnsupportedOperationException();
	}
}