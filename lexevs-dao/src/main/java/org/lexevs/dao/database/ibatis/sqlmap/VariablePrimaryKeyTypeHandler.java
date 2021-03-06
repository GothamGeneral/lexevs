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
package org.lexevs.dao.database.ibatis.sqlmap;

import java.sql.SQLException;
import java.sql.Types;

import org.lexevs.dao.database.key.incrementer.PrimaryKeyIncrementer;
import org.lexevs.dao.database.key.incrementer.PrimaryKeyIncrementerFactory;
import org.lexevs.dao.database.key.incrementer.PrimaryKeyIncrementer.KeyType;
import org.springframework.batch.classify.Classifier;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

public class VariablePrimaryKeyTypeHandler implements TypeHandlerCallback {
	
	private PrimaryKeyIncrementer primaryKeyIncrementer;
	
	private SqlTypeClassifier sqlTypeClassifier = new SqlTypeClassifier();

    public void setParameter(ParameterSetter setter, Object parameter) 
        throws SQLException {

    	int sqlType = sqlTypeClassifier.classify(
    			getPrimaryKeyIncrementer().getKeyType());
    	
    	setter.setObject(parameter, sqlType);
    }

/**
     * From DB to Java.
     */
public Object getResult(ResultGetter getter) throws SQLException {
    	
    	KeyType keyType = getPrimaryKeyIncrementer().getKeyType();
        
    	Object result;
    	
    	switch(keyType) {
	    	case VARCHAR : {
	    		result = getter.getString();
	    		break;
	    	}
	    	case BIGINT: {
    			result = getter.getLong();
    			if(getter.wasNull()) {
    				return null;
    			}
	    		break;
	    	}
	    	default: {
	    		throw new RuntimeException("Could not map key");
	    	}
    	}
        
    	return getPrimaryKeyIncrementer().stringValue(result);
    }
    
    private class SqlTypeClassifier implements Classifier<KeyType,Integer>{

		@Override
		public Integer classify(KeyType type) {
			switch(type) {
		    	case BIGINT : {
		    		return Types.BIGINT;
		    	}
		    	case VARCHAR: {
		    		return Types.VARCHAR;
		    	}
		    	default: {
		    		throw new RuntimeException("Could classify Key Type.");
		    	}
			}
		}
    }
    
    private PrimaryKeyIncrementer getPrimaryKeyIncrementer() {
    	if(this.primaryKeyIncrementer == null) {
    		this.primaryKeyIncrementer = PrimaryKeyIncrementerFactory.THREAD_LOCAL.get();
    	}
    	return this.primaryKeyIncrementer;
    }

	@Override
	public Object valueOf(String s) {
		return getPrimaryKeyIncrementer().valueOf(s);
	}

	public void setPrimaryKeyIncrementer(PrimaryKeyIncrementer primaryKeyIncrementer) {
		this.primaryKeyIncrementer = primaryKeyIncrementer;
	}
}