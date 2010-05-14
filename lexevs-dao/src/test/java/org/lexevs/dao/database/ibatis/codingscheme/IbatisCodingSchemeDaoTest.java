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
package org.lexevs.dao.database.ibatis.codingscheme;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.commonTypes.EntityDescription;
import org.LexGrid.commonTypes.Source;
import org.LexGrid.commonTypes.Text;
import org.LexGrid.naming.Mappings;
import org.LexGrid.naming.SupportedCodingScheme;
import org.LexGrid.naming.SupportedHierarchy;
import org.LexGrid.naming.SupportedSource;
import org.LexGrid.util.sql.lgTables.SQLTableConstants;
import org.LexGrid.versions.EntryState;
import org.LexGrid.versions.types.ChangeType;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;
import org.lexevs.dao.test.LexEvsDbUnitTestBase;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class IbatisCodingSchemeDaoTest.
 * 
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
@Transactional
public class IbatisCodingSchemeDaoTest extends LexEvsDbUnitTestBase {

	/** The ibatis coding scheme dao. */
	@Resource
	private IbatisCodingSchemeDao ibatisCodingSchemeDao;

	@Test
	@Transactional
	public void testInsertSupportedHierarchy() throws SQLException{
		JdbcTemplate template = new JdbcTemplate(this.getDataSource());
		
		template.execute("Insert into codingScheme (codingSchemeGuid, codingSchemeName, codingSchemeUri, representsVersion) " +
			"values ('csguid', 'csname', 'csuri', 'csversion')");
		
		SupportedHierarchy hier = new SupportedHierarchy();
		hier.setLocalId("test");
		hier.setAssociationNames(new String[] {"test", "test2", "test3"});
	
		ibatisCodingSchemeDao.insertURIMap("csguid", hier);
		
		Map map = template.queryForMap("Select * from cssupportedattrib");

		Iterator itr = map.keySet().iterator();
		
		while(itr.hasNext()) {
			System.out.println(map.get(itr.next()));
		}
		
		template.queryForObject("Select * from cssupportedattrib", new RowMapper(){

			public Object mapRow(ResultSet rs, int arg1) throws SQLException {
				
				assertEquals("test,test2,test3", rs.getString(7));

				return true;
			}
		});
		
	}
	
	@Test
	@Transactional
	public void testInsertCodingScheme() throws SQLException{
		CodingScheme cs = new CodingScheme();
		
		final Timestamp effectiveDate = new Timestamp(1l);
		final Timestamp expirationDate = new Timestamp(2l);
		
		cs.setCodingSchemeName("csName");
		cs.setCodingSchemeURI("uri");
		cs.setRepresentsVersion("1.2");
		cs.setFormalName("csFormalName");
		cs.setDefaultLanguage("lang");
		cs.setApproxNumConcepts(22l);
		
		EntityDescription ed = new EntityDescription();
		ed.setContent("cs Description");
		cs.setEntityDescription(ed);
		
		Text copyright = new Text();
		copyright.setContent("cs Copyright");
		cs.setCopyright(copyright);
		
		cs.setIsActive(false);
		
		cs.setOwner("cs owner");
		
		cs.setStatus("testing");
		
		cs.setEffectiveDate(effectiveDate);
		cs.setExpirationDate(expirationDate);

		EntryState es = new EntryState();
		es.setChangeType(ChangeType.REMOVE);
		es.setRelativeOrder(22l);
		cs.setEntryState(es);
		
		String id = ibatisCodingSchemeDao.insertCodingScheme(cs, null, true);

		JdbcTemplate template = new JdbcTemplate(this.getDataSource());
		final String[] keys = (String[]) template.queryForObject("Select * from CodingScheme", new RowMapper(){

			public Object mapRow(ResultSet rs, int arg1) throws SQLException {
				String csId = rs.getString(1);
				assertTrue(rs.getString(2).equals("csName"));
				assertTrue(rs.getString(3).equals("uri"));
				assertTrue(rs.getString(4).equals("1.2"));
				assertTrue(rs.getString(5).equals("csFormalName"));
				assertTrue(rs.getString(6).equals("lang"));
				assertTrue(rs.getLong(7) == 22l);
				assertTrue(rs.getString(8).equals("cs Description"));
				assertTrue(rs.getString(9).equals("cs Copyright"));
				assertTrue(rs.getBoolean(10) == false);
				assertTrue(rs.getString(11).equals("cs owner"));
				assertTrue(rs.getString(12).equals("testing"));
				assertTrue(rs.getTimestamp(13).equals(effectiveDate));
				assertTrue(rs.getTimestamp(14).equals(expirationDate));
				
				//TODO: Test with a Release GUID
				//assertTrue(rs.getString(15).equals("someReleaseGuid"));

				String csEntryState = rs.getString(16);
				
				String[] keys = new String[]{csEntryState, csId};
				return keys;
			}
		});
		
		assertEquals(id, keys[1]);
		
		template.queryForObject("Select * from EntryState", new RowMapper(){

			public Object mapRow(ResultSet rs, int arg1) throws SQLException {
				assertTrue(rs.getString(1) + " - " + keys[0], rs.getString(1).equals(keys[0]));
				assertTrue(rs.getString(2) + " - " + keys[1], rs.getString(2).equals(keys[1]));
				assertEquals(rs.getString(3), "codingScheme");
				assertEquals(rs.getString(4), ChangeType.REMOVE.toString());
				assertEquals(rs.getLong(5), 22l);
				
				//TODO: Test with a Revision GUID
				//TODO: Test with a Previous Revision GUID
				//TODO: Test with a Previous EntryState GUID
				
				return null;
			}
		});
	}
	
	/**
	 * Test insert uri map.
	 * 
	 * @throws SQLException the SQL exception
	 */
	@Test
	public void testInsertURIMap() throws SQLException{
		JdbcTemplate template = new JdbcTemplate(this.getDataSource());
		
		CodingScheme codingScheme = new CodingScheme();
		codingScheme.setCodingSchemeName("name");
		codingScheme.setCodingSchemeURI("uri");
		final String csKey = ibatisCodingSchemeDao.insertCodingScheme(codingScheme, null, true);
		
		SupportedCodingScheme cs = new SupportedCodingScheme();
		cs.setContent("supported cs");
		cs.setIsImported(true);
		cs.setLocalId("cs");
		cs.setUri("uri://test");

		ibatisCodingSchemeDao.insertURIMap(csKey, cs);
		
		
		
		template.queryForObject("Select * from csSupportedAttrib", new RowMapper(){

			public Object mapRow(ResultSet rs, int arg1) throws SQLException {
				
				assertNotNull(rs.getString(1));
				assertEquals(rs.getString(2), csKey);
				assertEquals(rs.getString(3), SQLTableConstants.TBLCOLVAL_SUPPTAG_CODINGSCHEME);
				assertEquals(rs.getString(4), "cs");
				assertEquals(rs.getString(5), "uri://test");
				assertEquals(rs.getString(6), "supported cs");
				assertEquals(true, rs.getBoolean(10));

				return true;
			}
		});
	}
	
	@Test
	public void testInsertOrUpdateURIMap() throws SQLException{
		JdbcTemplate template = new JdbcTemplate(this.getDataSource());
		
		CodingScheme codingScheme = new CodingScheme();
		codingScheme.setCodingSchemeName("name");
		codingScheme.setCodingSchemeURI("uri");
		final String csKey = ibatisCodingSchemeDao.insertCodingScheme(codingScheme, null, true);
		
		SupportedCodingScheme cs = new SupportedCodingScheme();
		cs.setContent("supported cs");
		cs.setIsImported(true);
		cs.setLocalId("cs");
		cs.setUri("uri://test");

		ibatisCodingSchemeDao.insertOrUpdateURIMap(csKey, cs);
	
		template.queryForObject("Select * from csSupportedAttrib", new RowMapper(){

			public Object mapRow(ResultSet rs, int arg1) throws SQLException {
				
				assertNotNull(rs.getString(1));
				assertEquals(rs.getString(2), csKey);
				assertEquals(rs.getString(3), SQLTableConstants.TBLCOLVAL_SUPPTAG_CODINGSCHEME);
				assertEquals(rs.getString(4), "cs");
				assertEquals(rs.getString(5), "uri://test");
				assertEquals(rs.getString(6), "supported cs");
				assertEquals(rs.getBoolean(10), true);

				return true;
			}
		});
	}
	
	/**
	 * Test insert coding scheme source.
	 * 
	 * @throws SQLException the SQL exception
	 */
	@Test
	@Transactional
	public void testInsertCodingSchemeSource() throws SQLException{
		CodingScheme codingScheme = new CodingScheme();
		codingScheme.setCodingSchemeName("name");
		codingScheme.setCodingSchemeURI("uri");
		final String csKey = ibatisCodingSchemeDao.insertCodingScheme(codingScheme, null, true);
		
		Source source = new Source();
		source.setContent("a source");
		source.setSubRef("sub ref");
		source.setRole("source role");
		
		ibatisCodingSchemeDao.insertCodingSchemeSource(csKey, source);
		
		JdbcTemplate template = new JdbcTemplate(this.getDataSource());
		
		template.queryForObject("Select * from csMultiAttrib", new RowMapper(){

			public Object mapRow(ResultSet rs, int arg1) throws SQLException {
				
				assertNotNull(rs.getString(1));
				assertEquals(rs.getString(2), csKey);
				assertEquals(rs.getString(3), SQLTableConstants.TBLCOLVAL_SOURCE);
				assertEquals(rs.getString(4), "a source");
				assertEquals(rs.getString(5), "sub ref");
				assertEquals(rs.getString(6), "source role");

				return true;
			}
		});
	}
	
	@Test
	public void testUpdateCodingSchemeSource() throws SQLException {
		JdbcTemplate template = new JdbcTemplate(this.getDataSource());
		template.execute("Insert into codingScheme (codingSchemeGuid, codingSchemeName, codingSchemeUri, representsVersion) " +
			"values ('csguid', 'csname', 'csuri', 'csversion')");
	
		template.execute("Insert into csmultiattrib " +
			"values ('csmaguid', 'csguid', 'source', 'a source', 'subRef', 'role', null)");
		
		Source source = new Source();
		source.setContent("a source");
		source.setSubRef("updatedSubRef");
		source.setRole("updated role");
		
		ibatisCodingSchemeDao.insertOrUpdateCodingSchemeSource("csguid", source);
		
		assertEquals(1, template.queryForInt("select count(*) from csmultiattrib"));
		
		template.queryForObject("Select * from csmultiattrib", new RowMapper(){

			public Object mapRow(ResultSet rs, int arg1) throws SQLException {
				
				assertEquals(rs.getString(4), "a source");
				assertEquals(rs.getString(5), "updatedSubRef");
				assertEquals(rs.getString(6), "updated role");

				return true;
			}
		});
	}
	
	/**
	 * Test insert coding scheme local name.
	 * 
	 * @throws SQLException the SQL exception
	 */
	@Test
	@Transactional
	public void testInsertCodingSchemeLocalName() throws SQLException{
		CodingScheme codingScheme = new CodingScheme();
		codingScheme.setCodingSchemeName("name");
		codingScheme.setCodingSchemeURI("uri");
		final String csKey = ibatisCodingSchemeDao.insertCodingScheme(codingScheme, null, true);
		
		final String localName = "LocalName";
		
		ibatisCodingSchemeDao.insertCodingSchemeLocalName(csKey, localName);
		
		JdbcTemplate template = new JdbcTemplate(this.getDataSource());
		
		template.queryForObject("Select * from csMultiAttrib", new RowMapper(){

			public Object mapRow(ResultSet rs, int arg1) throws SQLException {
				
				assertNotNull(rs.getString(1));
				assertEquals(rs.getString(2), csKey);
				assertEquals(rs.getString(3), SQLTableConstants.TBLCOLVAL_LOCALNAME);
				assertEquals(rs.getString(4), localName);

				return true;
			}
		});
	}
	
	/**
	 * Test get coding scheme entry state.
	 * 
	 * @throws SQLException the SQL exception
	 */
	@Test
	@Transactional
	public void testGetCodingSchemeEntryState() throws SQLException{
		
		CodingScheme cs = new CodingScheme();
		
		cs.setCodingSchemeName("csName");
		cs.setCodingSchemeURI("uri");
		cs.setRepresentsVersion("1.2");
		cs.setFormalName("csFormalName");
		cs.setDefaultLanguage("lang");
		cs.setApproxNumConcepts(22l);
		
		EntryState es = new EntryState();
		es.setChangeType(ChangeType.NEW);
		es.setRelativeOrder(1l);
		cs.setEntryState(es);
		
		ibatisCodingSchemeDao.insertCodingScheme(cs, null, true);
		
		CodingScheme returnedCs = ibatisCodingSchemeDao.getCodingSchemeByNameAndVersion("csName", "1.2");
		
		assertNotNull(returnedCs);
		assertNotNull(returnedCs.getEntryState());	
	}
	
	/**
	 * Test insert mappings.
	 * 
	 * @throws SQLException the SQL exception
	 */
	@Test
	@Transactional
	public void testInsertMappings() throws SQLException{
		
		CodingScheme cs = new CodingScheme();
		
		cs.setCodingSchemeName("csName");
		cs.setCodingSchemeURI("uri");
		cs.setRepresentsVersion("1.2");
		cs.setFormalName("csFormalName");
		cs.setDefaultLanguage("lang");
		cs.setApproxNumConcepts(22l);
		
		EntryState es = new EntryState();
		es.setChangeType(ChangeType.NEW);
		es.setRelativeOrder(1l);
		cs.setEntryState(es);
		
		Mappings mappings = new Mappings();
		SupportedCodingScheme scs = new SupportedCodingScheme();
		scs.setContent("scs");
		scs.setUri("uri");
		scs.setLocalId("scs");
		
		SupportedSource ss = new SupportedSource();
		ss.setContent("scs");
		ss.setUri("uri");
		ss.setLocalId("id");
		
		mappings.addSupportedCodingScheme(scs);
		mappings.addSupportedSource(ss);
		
		cs.setMappings(mappings);
		
		ibatisCodingSchemeDao.insertCodingScheme(cs, null, true);
		
		CodingScheme returnedCs = ibatisCodingSchemeDao.getCodingSchemeByNameAndVersion("csName", "1.2");
		
		assertNotNull(returnedCs);
		assertNotNull(returnedCs.getMappings());	
	}
	

	
	/**
	 * Test distinct namespaces.
	 */
	@Test
	@Transactional
	public void testUpdateUriMap() {
		JdbcTemplate template = new JdbcTemplate(this.getDataSource());
		
		template.execute("Insert into codingScheme (codingSchemeGuid, codingSchemeName, codingSchemeUri, representsVersion) " +
			"values ('csguid', 'csname', 'csuri', 'csversion')");
		
		template.execute("Insert into cssupportedattrib " +
			"values ('cssaguid', 'csguid', 'CodingScheme', 'id', 'uri', null, null, null, null, null, null, null, null, null, null, null)");
		
		
		SupportedCodingScheme scs = new SupportedCodingScheme();
		scs.setIsImported(true);
		scs.setLocalId("id");
		scs.setUri("changedUri");
		
		ibatisCodingSchemeDao.insertOrUpdateURIMap("csguid", scs);
		
		assertEquals(1, template.queryForInt("Select count(*) from cssupportedattrib"));
		
		template.queryForObject("Select * from cssupportedattrib", new RowMapper(){

			public Object mapRow(ResultSet rs, int arg1) throws SQLException {
				
				assertEquals("changedUri", rs.getString(5));
				assertTrue(rs.getBoolean(10));

				return true;
			}
		});
	}
	
	/**
	 * Test distinct namespaces.
	 */
	@Test
	@Transactional
	public void testDeleteUriMapsOfCodingScheme() {
		JdbcTemplate template = new JdbcTemplate(this.getDataSource());
		
		template.execute("Insert into codingScheme (codingSchemeGuid, codingSchemeName, codingSchemeUri, representsVersion) " +
			"values ('csguid', 'csname', 'csuri', 'csversion')");
		
		template.execute("Insert into cssupportedattrib " +
			"values ('cssaguid1', 'csguid', 'CodingScheme', 'id1', 'uri1', null, null, null, null, null, null, null, null, null, null, null)");
		
		template.execute("Insert into cssupportedattrib " +
			"values ('cssaguid2', 'csguid', 'CodingScheme', 'id2', 'uri2', null, null, null, null, null, null, null, null, null, null, null)");

		assertEquals(2, template.queryForInt("Select count(*) from cssupportedattrib"));
		
		ibatisCodingSchemeDao.deleteCodingSchemeMappings("csguid");
		
		assertEquals(0, template.queryForInt("Select count(*) from cssupportedattrib"));
	}
	
	/**
	 * Test get coding scheme local name.
	 * 
	 * @throws SQLException the SQL exception
	 */
	@Test
	@Transactional
	public void testGetCodingSchemeLocalName() throws SQLException{
		
		CodingScheme cs = new CodingScheme();
		
		cs.setCodingSchemeName("csName");
		cs.setCodingSchemeURI("uri");
		cs.setRepresentsVersion("1.2");
		cs.setFormalName("csFormalName");
		cs.setDefaultLanguage("lang");
		cs.setApproxNumConcepts(22l);
		
		cs.addLocalName("localName");
		
		ibatisCodingSchemeDao.insertCodingScheme(cs, null, true);
		
		CodingScheme returnedCs = ibatisCodingSchemeDao.getCodingSchemeByNameAndVersion("csName", "1.2");
		
		assertNotNull(returnedCs);
		assertEquals(returnedCs.getLocalNameCount(), 1);
		assertEquals(returnedCs.getLocalName(0), "localName");
	}
	
	/**
	 * Test get coding scheme multiple local names.
	 * 
	 * @throws SQLException the SQL exception
	 */
	@Test
	@Transactional
	public void testGetCodingSchemeMultipleLocalNames() throws SQLException{
		CodingScheme cs = new CodingScheme();
		
		cs.setCodingSchemeName("csName");
		cs.setCodingSchemeURI("uri");
		cs.setRepresentsVersion("1.2");
		cs.setFormalName("csFormalName");
		cs.setDefaultLanguage("lang");
		cs.setApproxNumConcepts(22l);
		
		cs.addLocalName("localName1");
		cs.addLocalName("localName2");
		
		ibatisCodingSchemeDao.insertCodingScheme(cs, null, true);
		
		CodingScheme returnedCs = ibatisCodingSchemeDao.getCodingSchemeByNameAndVersion("csName", "1.2");
		
		assertNotNull(returnedCs);
		assertEquals(returnedCs.getLocalNameCount(), 2);
		assertTrue(ArrayUtils.contains(returnedCs.getLocalName(), "localName1"));
		assertTrue(ArrayUtils.contains(returnedCs.getLocalName(), "localName2"));
		
		assertEquals(0, returnedCs.getSourceCount());
	}

	/**
	 * Test get coding scheme source.
	 * 
	 * @throws SQLException the SQL exception
	 */
	@Test
	@Transactional
	public void testGetCodingSchemeSource() throws SQLException{
		
		CodingScheme cs = new CodingScheme();
		
		cs.setCodingSchemeName("csName");
		cs.setCodingSchemeURI("uri");
		cs.setRepresentsVersion("1.2");
		cs.setFormalName("csFormalName");
		cs.setDefaultLanguage("lang");
		cs.setApproxNumConcepts(22l);
		
		Source source = new Source();
		source.setContent("a source");
		
		Source source2 = new Source();
		source2.setContent("another source");
		
		cs.addSource(source);
		cs.addSource(source2);
		
		ibatisCodingSchemeDao.insertCodingScheme(cs, null, true);
		
		CodingScheme css = new CodingScheme();
		
		css.setCodingSchemeName("csName");
		css.setCodingSchemeURI("uri2");
		css.setRepresentsVersion("1.3");
		css.setFormalName("csFormalName");
		css.setDefaultLanguage("lang");
		css.setApproxNumConcepts(22l);
		
		ibatisCodingSchemeDao.insertCodingScheme(css, null, true);
		
		CodingScheme returnedCs = ibatisCodingSchemeDao.getCodingSchemeByNameAndVersion("csName", "1.2");
		
		assertNotNull(returnedCs);
		assertEquals(2, returnedCs.getSourceCount());
	}
	
	/**
	 * Test get coding scheme by name and version.
	 * 
	 * @throws SQLException the SQL exception
	 */
	@Test
	@Transactional
	public void testGetCodingSchemeByNameAndVersion() throws SQLException{
		
		CodingScheme cs = new CodingScheme();
		
		cs.setCodingSchemeName("csName");
		cs.setCodingSchemeURI("uri");
		cs.setRepresentsVersion("1.2");
		cs.setFormalName("csFormalName");
		cs.setDefaultLanguage("lang");
		cs.setApproxNumConcepts(22l);
		
		cs.addLocalName("localName");
		
		ibatisCodingSchemeDao.insertCodingScheme(cs, null, true);
		
		CodingScheme returnedCs = ibatisCodingSchemeDao.getCodingSchemeByNameAndVersion("csName", "1.2");
		
		assertNotNull(returnedCs);
	}
	
	@Test
	public void testGetCodingSchemeByIdEntryState() throws SQLException{
		JdbcTemplate template = new JdbcTemplate(this.getDataSource());
			template.execute("Insert into codingScheme (codingSchemeGuid, codingSchemeName, codingSchemeUri, representsVersion, entrystateguid) " +
				"values ('csguid', 'csname', 'csuri', 'csversion', 'esguid')");
		
		template.execute("Insert into revision (revisionguid, revisionId, revAppliedDate) " +
				"values ('rguid', 'rid', NOW() )");
		
		template.execute("Insert into entrystate (entrystateguid, entryguid, entrytype, changetype, relativeorder) " +
				"values ('esguid', 'csguid', 'cs', 'NEW', '0')");
		
		CodingScheme returnedCs = ibatisCodingSchemeDao.getCodingSchemeByNameAndVersion("csname", "csversion");
		
		EntryState es = returnedCs.getEntryState();
		
		assertNotNull(es);
	}

	@Test
	public void testGetMappings() {
		JdbcTemplate template = new JdbcTemplate(this.getDataSource());
		
		template.execute("Insert into codingScheme (codingSchemeGuid, codingSchemeName, codingSchemeUri, representsVersion) " +
		"values ('cs-guid', 'csname', 'csuri', 'csversion')");

		template.execute("Insert into cssupportedattrib (csSuppAttribGuid, codingSchemeGuid, supportedAttributeTag, id, assnCodingScheme, assnEntityCode, assnNamespace) " +
		"values ('cssa-guid', 'cs-guid', 'Association', 'test-assoc', 'csname', 'ae-code', 'ae-codens')");
		
		Mappings mappings = ibatisCodingSchemeDao.getMappings("cs-guid");
		
		assertNotNull(mappings);
		assertEquals(1, mappings.getSupportedAssociationCount());
	}
	
	/**
	 * Test get coding scheme id by uri and version.
	 * 
	 * @throws SQLException the SQL exception
	 */
	@Test
	@Transactional
	public void testGetCodingSchemeIdByUriAndVersion() throws SQLException{
		
		CodingScheme cs = new CodingScheme();
		
		cs.setCodingSchemeName("csName");
		cs.setCodingSchemeURI("uri");
		cs.setRepresentsVersion("1.2");
		cs.setFormalName("csFormalName");
		cs.setDefaultLanguage("lang");
		cs.setApproxNumConcepts(22l);
		
		cs.addLocalName("localName");
		
		ibatisCodingSchemeDao.insertCodingScheme(cs, null, true);
		
		String id = ibatisCodingSchemeDao.getCodingSchemeUIdByUriAndVersion("uri", "1.2");
		
		assertNotNull(id);
	}
	
	/**
	 * Test get coding scheme id by name and version.
	 * 
	 * @throws SQLException the SQL exception
	 */
	@Test
	@Transactional
	public void testGetCodingSchemeIdByNameAndVersion() throws SQLException{
		
		CodingScheme cs = new CodingScheme();
		
		cs.setCodingSchemeName("csName");
		cs.setCodingSchemeURI("uri");
		cs.setRepresentsVersion("1.2");
		cs.setFormalName("csFormalName");
		cs.setDefaultLanguage("lang");
		cs.setApproxNumConcepts(22l);
		
		cs.addLocalName("localName");
		
		ibatisCodingSchemeDao.insertCodingScheme(cs, null, true);
		
		String id = ibatisCodingSchemeDao.getCodingSchemeUIdByNameAndVersion("csName", "1.2");
		
		assertNotNull(id);
	}
	
	/**
	 * Test get coding scheme summary by uri and version.
	 * 
	 * @throws SQLException the SQL exception
	 */
	@Test
	@Transactional
	public void testGetCodingSchemeSummaryByUriAndVersion() throws SQLException{
		
		CodingScheme cs = new CodingScheme();
		
		cs.setCodingSchemeName("csName");
		cs.setCodingSchemeURI("uri");
		cs.setRepresentsVersion("1.2");
		cs.setFormalName("csFormalName");
		cs.setDefaultLanguage("lang");
		cs.setApproxNumConcepts(22l);
		
		EntityDescription des = new EntityDescription();
		des.setContent("description");
		cs.setEntityDescription(des);
		
		cs.addLocalName("localName");
		
		ibatisCodingSchemeDao.insertCodingScheme(cs, null, true);
		
		CodingSchemeSummary summary = ibatisCodingSchemeDao.getCodingSchemeSummaryByUriAndVersion("uri", "1.2");
		
		assertEquals("uri", summary.getCodingSchemeURI());
		assertEquals("1.2", summary.getRepresentsVersion());
		assertEquals("csFormalName", summary.getFormalName());
		assertEquals("csName", summary.getLocalName());
		assertEquals("description", summary.getCodingSchemeDescription().getContent());
	}
	
	/**
	 * Test update coding scheme by id.
	 * 
	 * @throws SQLException the SQL exception
	 */
	@Test
	@Transactional
	public void testUpdateCodingSchemeById() throws SQLException {
		JdbcTemplate template = new JdbcTemplate(this.getDataSource());
		template.execute("Insert into codingScheme (codingSchemeGuid, codingSchemeName, codingSchemeUri, representsVersion, approxNumConcepts) " +
			"values ('csguid', 'csname', 'csuri', 'csversion', '1234')");
		
		int count1 = template.queryForInt("select count(*) from codingscheme");
		assertEquals(1, count1);
		
		long preUpdateConcepts = template.queryForLong("select approxNumConcepts from codingscheme where codingSchemeGuid = 'csguid'");
		assertEquals(1234l, preUpdateConcepts);
		
		CodingScheme newCs = new CodingScheme();
		newCs.setApproxNumConcepts(11111l);
		
		ibatisCodingSchemeDao.updateCodingScheme("csguid", newCs);
		
		int count2 = template.queryForInt("select count(*) from codingscheme");
		assertEquals(1, count2);
		
		long postUpdateConcepts = template.queryForLong("select approxNumConcepts from codingscheme where codingSchemeGuid = 'csguid'");
		assertEquals(11111l, postUpdateConcepts);
	}
	
	/**
	 * Test distinct property names.
	 */
	@Test
	@Transactional
	public void testDistinctPropertyNames() {
		JdbcTemplate template = new JdbcTemplate(this.getDataSource());
		template.execute("Insert into property (propertyGuid, referenceGuid, referenceType, propertyName, propertyValue, propertyType) " +
			"values ('pguid1', 'eguid', 'entity', 'pname1', 'pvalue', 'presentation')");
		
		template.execute("Insert into property (propertyGuid, referenceGuid, referenceType, propertyName, propertyValue, propertyType) " +
			"values ('pguid2', 'eguid', 'entity', 'pname2', 'pvalue', 'presentation')");
		
		template.execute("Insert into codingScheme (codingSchemeGuid, codingSchemeName, codingSchemeUri, representsVersion) " +
			"values ('csguid', 'csname', 'csuri', 'csversion')");
		
		template.execute("Insert into entity (entityGuid, codingSchemeGuid, entityCode, entityCodeNamespace) " +
			"values ('eguid', 'csguid', 'ecode', 'ens')");
		
		
		List<String> pnames = this.ibatisCodingSchemeDao.getDistinctPropertyNamesOfCodingScheme("csguid");
		
		assertEquals(2, pnames.size());
		
		assertTrue(pnames.contains("pname1"));
		assertTrue(pnames.contains("pname2"));
	}
	
	/**
	 * Test distinct entity types.
	 */
	@Test
	@Transactional
	public void testDistinctEntityTypes() {
		JdbcTemplate template = new JdbcTemplate(this.getDataSource());
		
		template.execute("Insert into codingScheme (codingSchemeGuid, codingSchemeName, codingSchemeUri, representsVersion) " +
			"values ('csguid', 'csname', 'csuri', 'csversion')");
		
		template.execute("Insert into entity (entityGuid, codingSchemeGuid, entityCode, entityCodeNamespace) " +
			"values ('eguid1', 'csguid', 'ecode1', 'ens1')");
		
		template.execute("Insert into entityType " +
			"values ('eguid1', 'etype1')");
		
		template.execute("Insert into entity (entityGuid, codingSchemeGuid, entityCode, entityCodeNamespace) " +
			"values ('eguid2', 'csguid', 'ecode2', 'ens2')");
	
		template.execute("Insert into entityType " +
			"values ('eguid2', 'etype2')");
		
		List<String> etypes = this.ibatisCodingSchemeDao.getDistinctEntityTypesOfCodingScheme("csguid");
		
		assertEquals(2, etypes.size());
		
		assertTrue(etypes.contains("etype1"));
		assertTrue(etypes.contains("etype2"));
	}
	
	/**
	 * Test distinct namespaces.
	 */
	@Test
	@Transactional
	public void testDistinctNamespaces() {
		JdbcTemplate template = new JdbcTemplate(this.getDataSource());
		
		template.execute("Insert into codingScheme (codingSchemeGuid, codingSchemeName, codingSchemeUri, representsVersion) " +
			"values ('csguid', 'csname', 'csuri', 'csversion')");
		
		template.execute("Insert into entity (entityGuid, codingSchemeGuid, entityCode, entityCodeNamespace) " +
			"values ('eguid1', 'csguid', 'ecode1', 'ens1')");
		
		template.execute("Insert into entityType " +
			"values ('eguid1', 'etype1')");
		
		template.execute("Insert into entity (entityGuid, codingSchemeGuid, entityCode, entityCodeNamespace) " +
			"values ('eguid2', 'csguid', 'ecode2', 'ens2')");
	
		template.execute("Insert into entityType " +
			"values ('eguid2', 'etype2')");
		
		List<String> etypes = this.ibatisCodingSchemeDao.getDistinctNamespacesOfCodingScheme("csguid");
		
		assertEquals(2, etypes.size());
		
		assertTrue(etypes.contains("ens1"));
		assertTrue(etypes.contains("ens2"));
	}
	
	/**
	 * Test distinct formats.
	 */
	@Test
	@Transactional
	public void testDistinctFormats() {
		JdbcTemplate template = new JdbcTemplate(this.getDataSource());

		template.execute("Insert into property (propertyGuid, referenceGuid, referenceType, propertyName, propertyValue, propertyType, format) " +
			"values ('pguid1', 'eguid', 'entity', 'pname1', 'pvalue', 'presentation', 'format1')");
		
		template.execute("Insert into property (propertyGuid, referenceGuid, referenceType, propertyName, propertyValue, propertyType, format) " +
			"values ('pguid2', 'eguid', 'entity', 'pname2', 'pvalue', 'presentation', 'format2')");
		
		template.execute("Insert into property (propertyGuid, referenceGuid, referenceType, propertyName, propertyValue, propertyType) " +
			"values ('pguid3', 'eguid', 'entity', 'pname3', 'pvalue', 'presentation')");
		
		template.execute("Insert into codingScheme (codingSchemeGuid, codingSchemeName, codingSchemeUri, representsVersion) " +
			"values ('csguid', 'csname', 'csuri', 'csversion')");
		
		template.execute("Insert into entity (entityGuid, codingSchemeGuid, entityCode, entityCodeNamespace) " +
			"values ('eguid', 'csguid', 'ecode', 'ens')");
		
		List<String> formats = this.ibatisCodingSchemeDao.getDistinctFormatsOfCodingScheme("csguid");
		
		assertEquals(2, formats.size());
		
		assertTrue(formats.contains("format1"));
		assertTrue(formats.contains("format2"));
	}
	
	/**
	 * Test distinct languages.
	 */
	@Test
	@Transactional
	public void testDistinctLanguages() {
		JdbcTemplate template = new JdbcTemplate(this.getDataSource());

		template.execute("Insert into property (language, propertyGuid, referenceGuid, referenceType, propertyName, propertyValue, propertyType, format) " +
			"values ('en', 'pguid1', 'eguid', 'entity', 'pname1', 'pvalue', 'presentation', 'format1')");
		
		template.execute("Insert into property (language, propertyGuid, referenceGuid, referenceType, propertyName, propertyValue, propertyType, format) " +
			"values ('fr', 'pguid2', 'eguid', 'entity', 'pname2', 'pvalue', 'presentation', 'format2')");
		
		template.execute("Insert into codingScheme (defaultLanguage, codingSchemeGuid, codingSchemeName, codingSchemeUri, representsVersion) " +
			"values ('ge', 'csguid', 'csname', 'csuri', 'csversion')");
		
		template.execute("Insert into entity (entityGuid, codingSchemeGuid, entityCode, entityCodeNamespace) " +
			"values ('eguid', 'csguid', 'ecode', 'ens')");
		
		List<String> langs = this.ibatisCodingSchemeDao.getDistinctLanguagesOfCodingScheme("csguid");
		
		assertEquals(3, langs.size());
		
		assertTrue(langs.contains("fr"));
		assertTrue(langs.contains("ge"));
		assertTrue(langs.contains("en"));
	}
	
	/**
	 * Test distinct property qualifier types.
	 */
	@Test
	@Transactional
	public void testDistinctPropertyQualifierTypes() {
		JdbcTemplate template = new JdbcTemplate(this.getDataSource());

		template.execute("Insert into property (language, propertyGuid, referenceGuid, referenceType, propertyName, propertyValue, propertyType, format) " +
			"values ('en', 'pguid1', 'eguid', 'entity', 'pname1', 'pvalue', 'presentation', 'format1')");
		
		template.execute("Insert into property (language, propertyGuid, referenceGuid, referenceType, propertyName, propertyValue, propertyType, format) " +
			"values ('fr', 'pguid2', 'eguid', 'entity', 'pname2', 'pvalue', 'presentation', 'format2')");
		
		template.execute("Insert into propertymultiattrib (propMultiAttribGuid, propertyGuid, attributeType, attributeId) " +
			"values ('pmaguid1', 'pguid1', 'type1', 'name1')");
		
		template.execute("Insert into propertymultiattrib (propMultiAttribGuid, propertyGuid, attributeType, attributeId) " +
			"values ('pmaguid2', 'pguid2', 'type2', 'name2')");
		
		template.execute("Insert into codingScheme (defaultLanguage, codingSchemeGuid, codingSchemeName, codingSchemeUri, representsVersion) " +
			"values ('ge', 'csguid', 'csname', 'csuri', 'csversion')");
		
		template.execute("Insert into entity (entityGuid, codingSchemeGuid, entityCode, entityCodeNamespace) " +
			"values ('eguid', 'csguid', 'ecode', 'ens')");
		
		List<String> types = this.ibatisCodingSchemeDao.getDistinctPropertyQualifierTypesOfCodingScheme("csguid");
		
		assertEquals(2, types.size());
		
		assertTrue(types.contains("type1"));
		assertTrue(types.contains("type2"));
	}
	
	/**
	 * Test distinct property qualifier names.
	 */
	@Test
	@Transactional
	public void testDistinctPropertyQualifierNames() {
		JdbcTemplate template = new JdbcTemplate(this.getDataSource());

		template.execute("Insert into property (language, propertyGuid, referenceGuid, referenceType, propertyName, propertyValue, propertyType, format) " +
			"values ('en', 'pguid1', 'eguid', 'entity', 'pname1', 'pvalue', 'presentation', 'format1')");
		
		template.execute("Insert into property (language, propertyGuid, referenceGuid, referenceType, propertyName, propertyValue, propertyType, format) " +
			"values ('fr', 'pguid2', 'eguid', 'entity', 'pname2', 'pvalue', 'presentation', 'format2')");
		
		template.execute("Insert into propertymultiattrib (propMultiAttribGuid, propertyGuid, attributeType, attributeId) " +
			"values ('pmaguid1', 'pguid1', 'type1', 'name1')");
		
		template.execute("Insert into propertymultiattrib (propMultiAttribGuid, propertyGuid, attributeType, attributeId) " +
			"values ('pmaguid2', 'pguid2', 'type2', 'name2')");
		
		template.execute("Insert into codingScheme (defaultLanguage, codingSchemeGuid, codingSchemeName, codingSchemeUri, representsVersion) " +
			"values ('ge', 'csguid', 'csname', 'csuri', 'csversion')");
		
		template.execute("Insert into entity (entityGuid, codingSchemeGuid, entityCode, entityCodeNamespace) " +
			"values ('eguid', 'csguid', 'ecode', 'ens')");
		
		List<String> names = this.ibatisCodingSchemeDao.getDistinctPropertyQualifierNamesOfCodingScheme("csguid");
		
		assertEquals(2, names.size());
		
		assertTrue(names.contains("name1"));
		assertTrue(names.contains("name2"));
	}
}
