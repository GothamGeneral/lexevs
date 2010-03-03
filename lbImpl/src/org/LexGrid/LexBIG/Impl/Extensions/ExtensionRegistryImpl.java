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
package org.LexGrid.LexBIG.Impl.Extensions;

import java.util.Hashtable;
import java.util.Iterator;

import org.LexGrid.LexBIG.DataModel.Collections.ExtensionDescriptionList;
import org.LexGrid.LexBIG.DataModel.Collections.SortDescriptionList;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.ExtensionDescription;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.SortDescription;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Extensions.Extendable;
import org.LexGrid.LexBIG.Extensions.ExtensionRegistry;
import org.LexGrid.LexBIG.Extensions.Export.Exporter;
import org.LexGrid.LexBIG.Extensions.Generic.GenericExtension;
import org.LexGrid.LexBIG.Extensions.Load.Loader;
import org.LexGrid.LexBIG.Extensions.Query.Filter;
import org.LexGrid.LexBIG.Extensions.Query.Search;
import org.LexGrid.LexBIG.Extensions.Query.Sort;
import org.LexGrid.annotations.LgClientSideSafe;
import org.lexevs.logging.LgLoggerIF;
import org.lexevs.logging.LoggerFactory;
import org.lexevs.system.utility.MyClassLoader;

/**
 * Implementation of the LexGrid Extension Registry.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 * @author <A HREF="mailto:erdmann.jesse@mayo.edu">Jesse Erdmann</A>
 * @version subversion $Revision: $ checked in on $Date: $
 */
public class ExtensionRegistryImpl implements ExtensionRegistry {
    private static final long serialVersionUID = -3808853582949198668L;

    private static ExtensionRegistryImpl er_;

    private Hashtable<String, ExtensionDescription> filterExtensions_ = new Hashtable<String, ExtensionDescription>();
    private Hashtable<String, Filter> filterCache_ = new Hashtable<String, Filter>();
    private Hashtable<String, ExtensionDescription> genericExtensions_ = new Hashtable<String, ExtensionDescription>();
    private Hashtable<String, ExtensionDescription> loadExtensions_ = new Hashtable<String, ExtensionDescription>();
    private Hashtable<String, ExtensionDescription> exportExtensions_ = new Hashtable<String, ExtensionDescription>();
    private Hashtable<String, SortDescription> sortAlgorithms_ = new Hashtable<String, SortDescription>();
    private Hashtable<String, Sort> sortAlgorithmCache_ = new Hashtable<String, Sort>();
    private Hashtable<String, ExtensionDescription> searchExtensions_ = new Hashtable<String, ExtensionDescription>();
    private Hashtable<String, Search> searchCache_ = new Hashtable<String, Search>();

    private ExtensionRegistryImpl(){
        for(ExtensionDescription ed :
                MyClassLoader.instance().getExtensionDescriptions()){
            if(ed.getExtensionBaseClass().equals(Search.class.getName())){
                try {
                    this.registerSearchExtension(ed);
                } catch (LBParameterException e) {
                   this.getLogger().warn("Could not load Extension: " + ed.getName(), e);
                }
            }
            if(ed.getExtensionBaseClass().equals(Loader.class.getName())){
                try {
                    this.registerLoadExtension(ed);
                } catch (LBParameterException e) {
                   this.getLogger().warn("Could not load Extension: " + ed.getName(), e);
                }
            }
            if(ed.getExtensionBaseClass().equals(GenericExtension.class.getName())){
                try {
                    this.registerGenericExtension(ed);
                } catch (LBParameterException e) {
                   this.getLogger().warn("Could not load Extension: " + ed.getName(), e);
                }
            }
            if(ed.getExtensionBaseClass().equals(Filter.class.getName())){
                try {
                    this.registerFilterExtension(ed);
                } catch (LBParameterException e) {
                   this.getLogger().warn("Could not load Extension: " + ed.getName(), e);
                }
            }
        }
    }

    protected LgLoggerIF getLogger() {
        return LoggerFactory.getLogger();
    }

    @LgClientSideSafe
    public static ExtensionRegistryImpl instance() {
        if (er_ == null) {
            er_ = new ExtensionRegistryImpl();
        }
        return er_;
    }

    @LgClientSideSafe
    public ExtensionDescription getExportExtension(String name) {
        return exportExtensions_.get(name);
    }

    @LgClientSideSafe
    public ExtensionDescriptionList getExportExtensions() {
        ExtensionDescriptionList edl = new ExtensionDescriptionList();
        Iterator<ExtensionDescription> iter = exportExtensions_.values().iterator();
        while (iter.hasNext()) {
            edl.addExtensionDescription(iter.next());
        }
        return edl;
    }

    @LgClientSideSafe
    public ExtensionDescription getFilterExtension(String name) {
        return filterExtensions_.get(name);
    }

    @LgClientSideSafe
    public Filter getFilter(String name) throws LBParameterException {
        if (name == null || name.length() == 0) {
            throw new LBParameterException("The filter name is required");
        }

        Filter f = filterCache_.get(name);
        if (f == null) {
            throw new LBParameterException("No registered filter could be found with that name", "name", name);
        } else {
            return f;
        }
    }

    @LgClientSideSafe
    public ExtensionDescriptionList getFilterExtensions() {
        ExtensionDescriptionList edl = new ExtensionDescriptionList();
        Iterator<ExtensionDescription> iter = filterExtensions_.values().iterator();
        while (iter.hasNext()) {
            edl.addExtensionDescription(iter.next());
        }
        return edl;
    }
    
    @LgClientSideSafe
    public ExtensionDescriptionList getSearchExtensions() {
        ExtensionDescriptionList edl = new ExtensionDescriptionList();
        Iterator<ExtensionDescription> iter = searchExtensions_.values().iterator();
        while (iter.hasNext()) {
            edl.addExtensionDescription(iter.next());
        }
        return edl;
    }
    
    @LgClientSideSafe
    public ExtensionDescription getSearchExtension(String name) {
        return searchExtensions_.get(name);
    }
    
    @LgClientSideSafe
    public Search getSearchAlgorithm(String name) throws LBParameterException {
        if (name == null || name.length() == 0) {
            throw new LBParameterException("The search name is required");
        }
        
        Search s = searchCache_.get(name);

        if (s == null) {
            throw new LBParameterException("No registered search algorithm could be found with that name", "name", name);
        } else {
            return s;
        }
    }

    @LgClientSideSafe
    public ExtensionDescription getGenericExtension(String name) {
        return genericExtensions_.get(name);
    }

    @LgClientSideSafe
    public ExtensionDescriptionList getGenericExtensions() {
        ExtensionDescriptionList edl = new ExtensionDescriptionList();
        Iterator<ExtensionDescription> iter = genericExtensions_.values().iterator();
        while (iter.hasNext()) {
            edl.addExtensionDescription(iter.next());
        }
        return edl;
    }

    @LgClientSideSafe
    public <T extends Extendable> T getGenericExtension(String extensionName, Class<T> extensionClass) 
        throws LBParameterException {
        ExtensionDescription ed = getGenericExtension(extensionName);
        try {
             Class<T> clazz = (Class<T>) Class.forName(ed.getExtensionClass(), true, MyClassLoader.instance());
             return clazz.newInstance();
        } catch (Exception e) {
            getLogger().error("Problem creating Generic Extension " + extensionName, e);
            throw new LBParameterException(
            "Could not instantiate the specified Generic Extension.  See log files for more details.  Will not be registered.");
        }
    }

    @LgClientSideSafe
    public ExtensionDescription getIndexExtension(String name) {
        // not supported now.
        return null;
    }

    @LgClientSideSafe
    public ExtensionDescriptionList getIndexExtensions() {
        // not supported now.
        return new ExtensionDescriptionList();
    }

    @LgClientSideSafe
    public ExtensionDescription getLoadExtension(String name) {
        return loadExtensions_.get(name);
    }

    @LgClientSideSafe
    public ExtensionDescriptionList getLoadExtensions() {
        ExtensionDescriptionList edl = new ExtensionDescriptionList();
        Iterator<ExtensionDescription> iter = loadExtensions_.values().iterator();
        while (iter.hasNext()) {
            edl.addExtensionDescription(iter.next());
        }
        return edl;
    }

    @LgClientSideSafe
    public SortDescription getSortExtension(String name) {
        return sortAlgorithms_.get(name);
    }

    @LgClientSideSafe
    public SortDescriptionList getSortExtensions() {
        SortDescriptionList sdl = new SortDescriptionList();
        Iterator<SortDescription> iter = sortAlgorithms_.values().iterator();
        while (iter.hasNext()) {
            sdl.addSortDescription(iter.next());
        }
        return sdl;
    }

    @LgClientSideSafe
    public Sort getSortAlgorithm(String name) throws LBParameterException {
        if (name == null || name.length() == 0) {
            throw new LBParameterException("The sort name is required");
        }
        
        Sort s = sortAlgorithmCache_.get(name);

        if (s == null) {
            throw new LBParameterException("No registered sort algorithm could be found with that name", "name", name);
        } else {
            return s;
        }
    }

    @LgClientSideSafe
    public void registerExportExtension(ExtensionDescription description) throws LBParameterException {
        ExtensionDescription sd = getExportExtension(description.getName());
        if (sd != null) {
            throw new LBParameterException("An Export Extension by that name is already registered");
        } else {
            try {
                MyClassLoader temp = MyClassLoader.instance();
                ((Exporter) temp.loadClass(description.getExtensionClass()).newInstance()).getName();
            } catch (ClassCastException e) {
                throw new LBParameterException("The provided Export Extension does not implement '"
                        + Exporter.class.getName() + "'.");
            } catch (Exception e) {
                getLogger().error("Problem creating ExportExtension " + description.getName(), e);
                throw new LBParameterException(
                        "Could not instantiate the specified Export Extension.  See log files for more details.  Will not be registered.");
            }
            exportExtensions_.put(description.getName(), description);
        }
    }

    @LgClientSideSafe
    public void registerFilterExtension(ExtensionDescription description) throws LBParameterException {
        ExtensionDescription sd = getFilterExtension(description.getName());
        if (sd != null) {
            throw new LBParameterException("A Filter Extension by that name is already registered");
        } else {
            Filter filter;
            try {
                MyClassLoader temp = MyClassLoader.instance();
                filter = (Filter) temp.loadClass(description.getExtensionClass()).newInstance();
                filter.getName();
            } catch (ClassCastException e) {
                throw new LBParameterException("The provided Filter Extension does not implement '"
                        + Filter.class.getName() + "'.");
            } catch (Exception e) {
                getLogger().error("Problem creating Filter Extension " + description.getName(), e);
                throw new LBParameterException(
                        "Could not instantiate the specified Filter Extension.  See log files for more details.  Will not be registered.");
            }
            filterExtensions_.put(description.getName(), description);
            filterCache_.put(description.getName(), filter);
        }
    }

    @LgClientSideSafe
    public void registerGenericExtension(ExtensionDescription description) throws LBParameterException {
        ExtensionDescription sd = getGenericExtension(description.getName());
        if (sd != null) {
            throw new LBParameterException("A Generic Extension by that name is already registered");
        } else {
            try {
                MyClassLoader temp = MyClassLoader.instance();
                ((GenericExtension) temp.loadClass(description.getExtensionClass()).newInstance()).getName();
            } catch (ClassCastException e) {
                throw new LBParameterException("The provided Generic Extension does not implement '"
                        + GenericExtension.class.getName() + "'.");
            } catch (Exception e) {
                getLogger().error("Problem creating Generic Extension " + description.getName(), e);
                throw new LBParameterException(
                        "Could not instantiate the specified Generic Extension.  See log files for more details.  Will not be registered.");
            }
            genericExtensions_.put(description.getName(), description);
        }
    }

    @LgClientSideSafe
    public void registerIndexExtension(ExtensionDescription description) throws LBParameterException {
        // not supported
        throw new java.lang.UnsupportedOperationException("Index extensions are not yet supported.");
    }

    @LgClientSideSafe
    public void registerLoadExtension(ExtensionDescription description) throws LBParameterException {
        ExtensionDescription sd = getLoadExtension(description.getName());
        if (sd != null) {
            throw new LBParameterException("A Load Extension by that name is already registered");
        } else {
            try {
                MyClassLoader temp = MyClassLoader.instance();
                ((Loader) temp.loadClass(description.getExtensionClass()).newInstance()).getName();
            } catch (ClassCastException e) {
                throw new LBParameterException("The provided Load Extension does not implement '"
                        + Loader.class.getName() + "'.");
            } catch (Exception e) {
                getLogger().error("Problem creating LoadExtension " + description.getName(), e);
                throw new LBParameterException(
                        "Could not instantiate the specified LoadExtension.  See log files for more details.  Will not be registered.");
            }
            loadExtensions_.put(description.getName(), description);
        }
    }

    @LgClientSideSafe
    public void registerSortExtension(SortDescription description) throws LBParameterException {
        ExtensionDescription sd = getSortExtension(description.getName());
        if (sd != null) {
            throw new LBParameterException("A SortDescription by that name is already registered");
        }
        else {
            Sort s = null;
            try {
                MyClassLoader temp = MyClassLoader.instance();
                s = (Sort) temp.loadClass(description.getExtensionClass()).newInstance();
            } catch (ClassCastException e) {
                throw new LBParameterException("The provided Sort Extension does not implement '"
                        + Sort.class.getName() + "'.");
            } catch (Exception e) {
                getLogger().error("Problem creating SortExtension " + description.getName(), e);
                throw new LBParameterException(
                        "Could not instantiate the specified SortExtension.  See log files for more details");
            }
            sortAlgorithmCache_.put(description.getName(), s);
            sortAlgorithms_.put(description.getName(), description);
        }
    }
    
    @LgClientSideSafe
    public void registerSearchExtension(ExtensionDescription description) throws LBParameterException {
        ExtensionDescription sd = getSearchExtension(description.getName());
        if (sd != null) {
            throw new LBParameterException("A ExtensionDescription by that name is already registered");
        }
        else {
            Search s = null;
            try {
                MyClassLoader temp = MyClassLoader.instance();
                s = (Search) temp.loadClass(description.getExtensionClass()).newInstance();
            } catch (ClassCastException e) {
                throw new LBParameterException("The provided Search Extension does not implement '"
                        + Search.class.getName() + "'.");
            } catch (Exception e) {
                getLogger().error("Problem creating SearchExtension " + description.getName(), e);
                throw new LBParameterException(
                        "Could not instantiate the specified SearchExtension.  See log files for more details");
            }
            searchCache_.put(description.getName(), s);
            searchExtensions_.put(description.getName(), description);
        }
    }

    @LgClientSideSafe
    public void unregisterExportExtension(String name) throws LBParameterException {
        ExtensionDescription ed = exportExtensions_.remove(name);
        if (ed == null) {
            throw new LBParameterException("No registered export extension was found with that name", "name", name);
        }
    }

    @LgClientSideSafe
    public void unregisterFilterExtension(String name) throws LBParameterException {
        ExtensionDescription ed = filterExtensions_.remove(name);
        if (ed == null) {
            throw new LBParameterException("No registered filter extension was found with that name", "name", name);
        }
    }

    @LgClientSideSafe
    public void unregisterGenericExtension(String name) throws LBParameterException {
        ExtensionDescription ed = genericExtensions_.remove(name);
        if (ed == null) {
            throw new LBParameterException("No registered generic extension was found with that name", "name", name);
        }
    }

    @LgClientSideSafe
    public void unregisterIndexExtension(String name) throws LBParameterException {
        // not yet supported
        throw new LBParameterException("No registered load extension was found with that name", "name", name);
    }

    @LgClientSideSafe
    public void unregisterLoadExtension(String name) throws LBParameterException {
        ExtensionDescription ed = loadExtensions_.remove(name);
        if (ed == null) {
            throw new LBParameterException("No registered load extension was found with that name", "name", name);
        }
    }

    @LgClientSideSafe
    public void unregisterSortExtension(String name) throws LBParameterException {
        SortDescription sd = sortAlgorithms_.remove(name);
        if (sd == null) {
            throw new LBParameterException("No registered sort algorithm was found with that name", "name", name);
        }
    }
    
    @LgClientSideSafe
    public void unregisterSearchExtension(String name) throws LBParameterException {
        ExtensionDescription sd = searchExtensions_.remove(name);
        if (sd == null) {
            throw new LBParameterException("No registered search extension was found with that name", "name", name);
        }
    }

}