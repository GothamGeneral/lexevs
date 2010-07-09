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
package org.lexevs.dao.database.service.property;

import java.util.ArrayList;
import java.util.List;

import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Exceptions.LBRevisionException;
import org.LexGrid.commonTypes.Property;
import org.LexGrid.versions.types.ChangeType;
import org.lexevs.dao.database.access.association.AssociationDao;
import org.lexevs.dao.database.access.codingscheme.CodingSchemeDao;
import org.lexevs.dao.database.access.entity.EntityDao;
import org.lexevs.dao.database.access.property.PropertyDao;
import org.lexevs.dao.database.access.property.PropertyDao.PropertyType;
import org.lexevs.dao.database.access.property.batch.PropertyBatchInsertItem;
import org.lexevs.dao.database.access.versions.VersionsDao;
import org.lexevs.dao.database.access.versions.VersionsDao.EntryStateType;
import org.lexevs.dao.database.service.RevisableAbstractDatabaseService;
import org.lexevs.dao.database.service.RevisableAbstractDatabaseService.ParentUidReferencingId;
import org.lexevs.dao.database.service.error.DatabaseErrorIdentifier;
import org.lexevs.dao.database.service.event.property.PropertyUpdateEvent;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * The Class VersionableEventPropertyService.
 * 
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 * @author <a href="mailto:rao.ramachandra@mayo.edu">Ramachandra Rao (satya)</a>
 */
public class VersionableEventPropertyService extends RevisableAbstractDatabaseService<Property,ParentUidReferencingId>
		implements PropertyService {
	
	@Override
	protected void doInsertDependentChanges(
			ParentUidReferencingId id, Property revisedEntry)
			throws LBException {
		//
	}

	@Override
	protected boolean entryStateExists(ParentUidReferencingId id,
			String entryStateUid) {
		String codingSchemeUri = id.getCodingSchemeUri();
		String version = id.getCodingSchemeVersion();
		
		String codingSchemeUid = this.getCodingSchemeUId(codingSchemeUri,
				version);
		
		PropertyDao propertyDao = getDaoManager().getPropertyDao(codingSchemeUri, version);
	
		return propertyDao.entryStateExists(codingSchemeUid, entryStateUid);
	}

	@Override
	protected Property getCurrentEntry(ParentUidReferencingId id,
			String entryUId) {
		String codingSchemeUri = id.getCodingSchemeUri();
		String version = id.getCodingSchemeVersion();
	
		PropertyDao propertyDao = getDaoManager().getPropertyDao(codingSchemeUri, version);
	
		String codingSchemeId = this.getCodingSchemeUId(codingSchemeUri,
				version);
		
		return propertyDao.getPropertyByUid(codingSchemeId, entryUId);
	}

	@Override
	protected String getCurrentEntryStateUid(
			ParentUidReferencingId id, String entryUid) {
		String codingSchemeUri = id.getCodingSchemeUri();
		String version = id.getCodingSchemeVersion();
		
		String codingSchemeUid = this.getCodingSchemeUId(codingSchemeUri,
				version);
		
		return this.getDaoManager().getPropertyDao(codingSchemeUri, version).
			getEntryStateUId(codingSchemeUid, entryUid);
	}

	@Override
	protected String getEntryUid(ParentUidReferencingId id,
			Property entry) {
		String codingSchemeUri = id.getCodingSchemeUri();
		String version = id.getCodingSchemeVersion();
		
		PropertyDao propertyDao = getDaoManager().getPropertyDao(codingSchemeUri, version);
		
		String codingSchemeUid = this.getCodingSchemeUId(codingSchemeUri,
				version);
		
		return propertyDao.getPropertyUIdByPropertyIdAndName(
				codingSchemeUid, 
				id.getParentUid(), 
				entry.getPropertyId(), 
				entry.getPropertyName());
	}

	@Override
	protected void insertIntoHistory(ParentUidReferencingId id,
			Property currentEntry, String entryUId) {
		String codingSchemeUri = id.getCodingSchemeUri();
		String version = id.getCodingSchemeVersion();
		
		PropertyDao propertyDao = getDaoManager().getPropertyDao(codingSchemeUri, version);
		
		String codingSchemeUid = this.getCodingSchemeUId(codingSchemeUri,
				version);
		
		propertyDao.insertHistoryProperty(codingSchemeUid, entryUId, currentEntry);
	}
	
	@Override
	protected Property addDependentAttributesByRevisionId(
			ParentUidReferencingId id, String entryUid, Property entry) {
		//no dependent attributes on a Property
		return entry;
	}

	@Override
	protected Property getHistoryEntryByRevisionId(ParentUidReferencingId id,
			String entryUid, String revisionId) {
		String codingSchemeUid = this.getCodingSchemeUId(
				id.getCodingSchemeUri(),
				id.getCodingSchemeVersion()
				);
		return this.getPropertyDao(id).getHistoryPropertyByRevisionId(codingSchemeUid, entryUid, revisionId);
	}

	@Override
	protected String getLatestRevisionId(ParentUidReferencingId id,
			String entryUId) {
		String codingSchemeUid = this.getCodingSchemeUId(
				id.getCodingSchemeUri(),
				id.getCodingSchemeVersion()
				);
		
		return this.getPropertyDao(id).getLatestRevision(codingSchemeUid, entryUId);
	}

	@Override
	protected String updateEntityVersionableAttributes(
			ParentUidReferencingId id, String entryUId,
			Property revisedEntity) {
		String codingSchemeUri = id.getCodingSchemeUri();
		String version = id.getCodingSchemeVersion();
		
		PropertyDao propertyDao = getDaoManager().getPropertyDao(codingSchemeUri, version);
		
		String codingSchemeUid = this.getCodingSchemeUId(codingSchemeUri,
				version);
		
		return propertyDao.updatePropertyVersionableAttrib(codingSchemeUid, entryUId, revisedEntity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.lexevs.dao.database.service.property.PropertyService#
	 * insertBatchEntityProperties(java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.util.List)
	 */
	@Override
	@DatabaseErrorIdentifier(errorCode=INSERT_BATCH_PROPERTY_ERROR)
	public void insertBatchEntityProperties(String codingSchemeUri,
			String version, String entityCode, String entityCodeNamespace,
			List<Property> items) {

		String codingSchemeId = this.getCodingSchemeUId(codingSchemeUri,
				version);
		String entityId = this.getDaoManager().getEntityDao(codingSchemeUri,
				version).getEntityUId(codingSchemeId, entityCode,
				entityCodeNamespace);

		this.getDaoManager().getPropertyDao(codingSchemeUri, version)
				.insertBatchProperties(codingSchemeId, PropertyType.ENTITY,
						this.propertyListToBatchInsertList(entityId, items));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.lexevs.dao.database.service.property.PropertyService#
	 * insertCodingSchemeProperty(java.lang.String, java.lang.String,
	 * org.LexGrid.commonTypes.Property)
	 */
	@Override
	@DatabaseErrorIdentifier(errorCode=INSERT_CODINGSCHEME_PROPERTY_ERROR)
	public void insertCodingSchemeProperty(String codingSchemeUri,
			String version, Property property) {
		String codingSchemeUId = this.getCodingSchemeUId(codingSchemeUri,
				version);

		this.doInsertProperty(codingSchemeUri, version, codingSchemeUId, property, PropertyType.CODINGSCHEME);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.lexevs.dao.database.service.property.PropertyService#insertEntityProperty
	 * (java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * org.LexGrid.commonTypes.Property)
	 */
	@Override
	@DatabaseErrorIdentifier(errorCode=INSERT_ENTITY_PROPERTY_ERROR)
	public void insertEntityProperty(String codingSchemeUri, String version,
			String entityCode, String entityCodeNamespace, Property property) {
		String codingSchemeUId = this.getCodingSchemeUId(codingSchemeUri,
				version);
		String entityUId = this.getDaoManager().getEntityDao(codingSchemeUri,
				version).getEntityUId(codingSchemeUId, entityCode,
				entityCodeNamespace);

		this.doInsertProperty(codingSchemeUri, version, entityUId, property, PropertyType.ENTITY);
		
		this.firePropertyUpdateEvent(new PropertyUpdateEvent(
				codingSchemeUri,
				version, 
				entityCode,
				entityCodeNamespace, 
				property));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.lexevs.dao.database.service.property.PropertyService#
	 * insertRelationProperty(java.lang.String, java.lang.String,
	 * java.lang.String, org.LexGrid.commonTypes.Property)
	 */
	@Override
	@DatabaseErrorIdentifier(errorCode=INSERT_RELATION_PROPERTY_ERROR)
	public void insertRelationProperty(String codingSchemeUri, String version,
			String relationContainerName, Property property) {

		String codingSchemeUId = this.getCodingSchemeUId(codingSchemeUri,
				version);
		String relationUId = this.getDaoManager().getAssociationDao(
				codingSchemeUri, version).getRelationUId(codingSchemeUId,
				relationContainerName);

		this.doInsertProperty(codingSchemeUri, version, relationUId, property, PropertyType.RELATION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.lexevs.dao.database.service.property.PropertyService#
	 * removeCodingSchemeProperty(java.lang.String, java.lang.String,
	 * org.LexGrid.commonTypes.Property)
	 */
	@Override
	@DatabaseErrorIdentifier(errorCode=REMOVE_CODINGSCHEME_PROPERTY_ERROR)
	public void removeCodingSchemeProperty(String codingSchemeUri,
			String version, Property property) {

		String codingSchemeUId = 
			this.getCodingSchemeUId(codingSchemeUri, version);

		this.doRemoveProperty(codingSchemeUri, version, codingSchemeUId, property, PropertyType.CODINGSCHEME);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.lexevs.dao.database.service.property.PropertyService#removeEntityProperty
	 * (java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * org.LexGrid.commonTypes.Property)
	 */
	@Override
	@DatabaseErrorIdentifier(errorCode=REMOVE_ENTITY_PROPERTY_ERROR)
	public void removeEntityProperty(String codingSchemeUri, String version,
			String entityCode, String entityCodeNamespace, Property property) {

		String codingSchemeUId = this.getCodingSchemeUId(codingSchemeUri,
				version);

		String entityUId = this.getDaoManager().getEntityDao(codingSchemeUri,
				version).getEntityUId(codingSchemeUId, entityCode,
				entityCodeNamespace);

		this.doRemoveProperty(codingSchemeUri, version, entityUId, property, PropertyType.ENTITY);

		this.firePropertyUpdateEvent(new PropertyUpdateEvent(
				codingSchemeUri,
				version, 
				entityCode,
				entityCodeNamespace, 
				property));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.lexevs.dao.database.service.property.PropertyService#
	 * removeRelationProperty(java.lang.String, java.lang.String,
	 * java.lang.String, org.LexGrid.commonTypes.Property)
	 */
	@Override
	@DatabaseErrorIdentifier(errorCode=REMOVE_RELATION_PROPERTY_ERROR)
	public void removeRelationProperty(String codingSchemeUri, String version,
			String relationContainerName, Property property) {

		String codingSchemeUId = this.getCodingSchemeUId(codingSchemeUri,
				version);

		String relationUId = this.getDaoManager().getAssociationDao(
				codingSchemeUri, version).getRelationUId(codingSchemeUId,
				relationContainerName);

		this.doRemoveProperty(codingSchemeUri, version, relationUId, property, PropertyType.RELATION);
	}
	
	protected void doInsertProperty(
			String codingSchemeUri, 
			String version,
			final String parentUid,
			final Property property, 
			final PropertyType propertyType) {
		
		String codingSchemeUId = this.getCodingSchemeUId(codingSchemeUri,
				version);

		this.getDaoManager().getPropertyDao(codingSchemeUri, version)
			.insertProperty(codingSchemeUId, parentUid,
					propertyType, property);
	}
	
	protected void doRemoveProperty(
			String codingSchemeUri, 
			String version,
			final String parentUid,
			final Property property, 
			final PropertyType propertyType) {

		final String codingSchemeUId = this.getCodingSchemeUId(codingSchemeUri,
				version);

		final PropertyDao propertyDao = getDaoManager().getPropertyDao(
				codingSchemeUri, version);

		final VersionsDao versionsDao = getDaoManager().getVersionsDao(
				codingSchemeUri, version);

		final String propertyUId = propertyDao.getPropertyUIdByPropertyIdAndName(codingSchemeUId,
				parentUid, property.getPropertyId(), property
						.getPropertyName());
		
		try {
			this.removeEntry(new ParentUidReferencingId(codingSchemeUri,version,parentUid), 
					property, EntryStateType.PROPERTY, new DeleteTemplate() {

				@Override
				public void delete() {
					/*
					versionsDao.deleteAllEntryStateEntriesByEntryUId(codingSchemeUId,
							propertyUId);
					*/
					/* 2. Remove property. */
					propertyDao.removePropertyByUId(codingSchemeUId, propertyUId);
				}
			});
		} catch (LBException e) {
			throw new RuntimeException(e);
		}
	}

	protected void doReviseProperty(
			String codingSchemeUri, 
			String version,
			final String parentUid,
			final Property property, 
			final PropertyType propertyType) throws LBException {
		
		if (validRevision(new ParentUidReferencingId(codingSchemeUri, version, parentUid), property)) {

			ChangeType changeType = property.getEntryState().getChangeType();

			if (changeType == ChangeType.NEW) {

				this.doInsertProperty(codingSchemeUri, version, parentUid, property, propertyType);
			} else if (changeType == ChangeType.REMOVE) {

				this.doRemoveProperty(codingSchemeUri, version, parentUid, property, propertyType);
			} else if (changeType == ChangeType.MODIFY) {

				this.doUpdateProperty(codingSchemeUri, version, parentUid, property, propertyType);
			} else if (changeType == ChangeType.VERSIONABLE) {
				this.doInsertDependentChanges(
						new ParentUidReferencingId(codingSchemeUri, version, parentUid), property);
			}
		}
	}
	
	public List<Property> resolvePropertiesOfCodingSchemeByRevision(
			String codingSchemeURI,
			String version,
			String revisionId){
		String codingSchemeUid = this.getCodingSchemeUId(codingSchemeURI,
				version);
		return this.doResolvePropertiesOfParentByRevision(codingSchemeURI, version, codingSchemeUid, revisionId);	
	}
	
	public List<Property> resolvePropertiesOfEntityByRevision(
			String codingSchemeURI,
			String version, 
			String entityCode, 
			String entityCodeNamespace,
			String revisionId){
		EntityDao entityDao = this.getDaoManager().getEntityDao(codingSchemeURI, version);
		
		String codingSchemeUid = this.getCodingSchemeUId(codingSchemeURI,
				version);
		
		String entityUid = entityDao.getEntityUId(codingSchemeUid, entityCode, entityCodeNamespace);

		return this.doResolvePropertiesOfParentByRevision(codingSchemeURI, version, entityUid, revisionId);		
	}
	
	
	
	@Override
	public List<Property> resolvePropertiesOfRelationByRevision(
			String codingSchemeURI, String version, String relationsName,
			String revisionId) {
		AssociationDao associationDao = 
			this.getDaoManager().getAssociationDao(codingSchemeURI, version);
		
		String codingSchemeUid = this.getCodingSchemeUId(codingSchemeURI,
				version);
		
		String relationsUid = associationDao.getRelationUId(codingSchemeUid, relationsName);
		
		return this.doResolvePropertiesOfParentByRevision(codingSchemeURI, version, relationsUid, revisionId);		
	}

	protected List<Property> doResolvePropertiesOfParentByRevision(
			String codingSchemeUri, 
			String version,
			String parentUid,
			String revisionId) {
		
		String codingSchemeUid = this.getCodingSchemeUId(codingSchemeUri,
				version);

		PropertyDao propertyDao = getDaoManager().getPropertyDao(
				codingSchemeUri, version);

		List<String> propertyUids = 
			propertyDao.getAllHistoryPropertyUidsOfParentByRevisionId(
				codingSchemeUid, parentUid, revisionId);
		
		List<Property> returnList = new ArrayList<Property>();
		
		for(String propertyUid : propertyUids) {
			Property prop;
			try {
				prop = this.resolveEntryByRevision(new ParentUidReferencingId(codingSchemeUri,version,parentUid), propertyUid, revisionId);
			} catch (LBRevisionException e) {
				throw new RuntimeException(e);
			}
			if(prop != null) {
				returnList.add(prop);
			}
		}
		
		return returnList;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.lexevs.dao.database.service.property.PropertyService#
	 * reviseCodingSchemeProperty(java.lang.String, java.lang.String,
	 * org.LexGrid.commonTypes.Property)
	 */
	@Override
	public void reviseCodingSchemeProperty(String codingSchemeUri,
			String version, Property property) throws LBException {
		
		String parentUid = this.getCodingSchemeUId(codingSchemeUri, version);
		
		this.doReviseProperty(codingSchemeUri, version, parentUid, property, PropertyType.CODINGSCHEME);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.lexevs.dao.database.service.property.PropertyService#reviseEntityProperty
	 * (java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * org.LexGrid.commonTypes.Property)
	 */
	@Override
	public void reviseEntityProperty(String codingSchemeUri, String version,
			String entityCode, String entityCodeNamespace, Property property)
			throws LBException {
		
		String codingSchemeUid = this.getCodingSchemeUId(codingSchemeUri, version);
		
		String parentUid = this.getDaoManager().getEntityDao(codingSchemeUri, version).
			getEntityUId(codingSchemeUid, entityCode, entityCodeNamespace);

		this.doReviseProperty(codingSchemeUri, version, parentUid, property, PropertyType.ENTITY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.lexevs.dao.database.service.property.PropertyService#
	 * reviseRelationProperty(java.lang.String, java.lang.String,
	 * java.lang.String, org.LexGrid.commonTypes.Property)
	 */
	@Override
	public void reviseRelationProperty(String codingSchemeUri, String version,
			String relationContainerName, Property property) throws LBException {

		String codingSchemeUid = this.getCodingSchemeUId(codingSchemeUri, version);
		
		String parentUid = this.getDaoManager().getAssociationDao(
				codingSchemeUri, version).
					getRelationUId(
						codingSchemeUid,
						relationContainerName);

		this.doReviseProperty(codingSchemeUri, version, parentUid, property, PropertyType.RELATION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.lexevs.dao.database.service.property.PropertyService#
	 * updateCodingSchemeProperty(java.lang.String, java.lang.String,
	 * org.LexGrid.commonTypes.Property)
	 */
	@Override
	@DatabaseErrorIdentifier(errorCode=UPDATE_CODINGSCHEME_PROPERTY_ERROR)
	public void updateCodingSchemeProperty(String codingSchemeUri,
			String version, Property property) {
		String codingSchemeUId = this.getCodingSchemeUId(codingSchemeUri, version);

		try {
			this.doUpdateProperty(codingSchemeUri, version, codingSchemeUId, property, PropertyType.CODINGSCHEME);
		} catch (LBException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.lexevs.dao.database.service.property.PropertyService#updateEntityProperty
	 * (java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * org.LexGrid.commonTypes.Property)
	 */
	@Transactional
	@Override
	@DatabaseErrorIdentifier(errorCode=UPDATE_ENTITY_PROPERTY_ERROR)
	public void updateEntityProperty(String codingSchemeUri, String version,
			String entityCode, String entityCodeNamespace, Property property) {
		String codingSchemeUId = this.getCodingSchemeUId(codingSchemeUri, version);

		try {
			this.doUpdateProperty(codingSchemeUri, version, codingSchemeUId, property, PropertyType.ENTITY);
		} catch (LBException e) {
			throw new RuntimeException(e);
		}

		this.firePropertyUpdateEvent(new PropertyUpdateEvent(
				codingSchemeUri,
				version, 
				entityCode,
				entityCodeNamespace, 
				property));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.lexevs.dao.database.service.property.PropertyService#
	 * updateRelationProperty(java.lang.String, java.lang.String,
	 * java.lang.String, org.LexGrid.commonTypes.Property)
	 */
	@Override
	@DatabaseErrorIdentifier(errorCode=UPDATE_ENTITY_PROPERTY_ERROR)
	public void updateRelationProperty(String codingSchemeUri, String version,
			String relationContainerName, Property property) {

		String codingSchemeUId = this.getCodingSchemeUId(codingSchemeUri, version);

		try {
			this.doUpdateProperty(codingSchemeUri, version, codingSchemeUId, property, PropertyType.RELATION);
		} catch (LBException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected void doUpdateProperty(
			String codingSchemeUri, 
			String version,
			final String parentUid,
			final Property property, 
			final PropertyType propertyType) throws LBException {
		Assert.notNull(property);
		Assert.notNull(property.getPropertyId());
		Assert.notNull(property.getPropertyName());
		
		final ParentUidReferencingId id = 
			new ParentUidReferencingId(codingSchemeUri, version, parentUid);
		
		final PropertyDao propertyDao = getDaoManager().getPropertyDao(codingSchemeUri, version);
		final CodingSchemeDao codingSchemeDao = getDaoManager().getCodingSchemeDao(codingSchemeUri, version);
		
		final String codingSchemeUId = codingSchemeDao.
			getCodingSchemeUIdByUriAndVersion(codingSchemeUri, version);
	
		final String propertyUid = getEntryUid(id, property);
		
		this.updateEntry(id, property, EntryStateType.PROPERTY, new UpdateTemplate() {

			@Override
			public String update() {
				
				return propertyDao.updateProperty(codingSchemeUId, parentUid, propertyUid, propertyType, property);
				
			}
		});
	}

	/**
	 * Property list to batch insert list.
	 * 
	 * @param parentId
	 *            the parent id
	 * @param props
	 *            the props
	 * 
	 * @return the list< property batch insert item>
	 */
	protected List<PropertyBatchInsertItem> propertyListToBatchInsertList(
			String parentId, List<Property> props) {
		List<PropertyBatchInsertItem> returnList = new ArrayList<PropertyBatchInsertItem>();
		for (Property prop : props) {
			returnList.add(new PropertyBatchInsertItem(parentId, prop));
		}
		return returnList;
	}
	
	private PropertyDao getPropertyDao(ParentUidReferencingId id) {
		return this.getDaoManager().getPropertyDao(id.getCodingSchemeUri(), id.getCodingSchemeVersion());
	}
}
