package org.lexgrid.loader.rxn.data.property;

import java.util.Comparator;

import org.lexgrid.loader.rrf.data.property.MrrankUtility;
import org.lexgrid.loader.rrf.model.Mrconso;

public class MrconsoPropertyComparator implements Comparator<Mrconso>{

	/** The mrrank utility. */
	private MrrankUtility mrrankUtility;
	
	/** The default language. */
	private String defaultLanguage = "ENG";
	
	/** The is pref. */
	private String isPref = "Y";
	
	/** The language comparator. */
	Comparator<Mrconso> languageComparator = new LanguageComparator();
	
	/** The is pref comparator. */
	Comparator<Mrconso> isPrefComparator = new IsPrefComparator();
	
	/** The mrrank comparator. */
	Comparator<Mrconso> mrrankComparator = new MrrankComparator();
	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Mrconso o1, Mrconso o2) {
		int mrrankCompare = mrrankComparator.compare(o1, o2);
		if(mrrankCompare != 0){
			return mrrankCompare;
		}
		
		int languageCompare = languageComparator.compare(o1, o2);
		if(languageCompare != 0){
			return languageCompare;
		}
		
		int isPrefCompare = isPrefComparator.compare(o1, o2);
		if(isPrefCompare != 0){
			return isPrefCompare;
		}
		
		return 0;
	}
	
	/**
	 * Checks if is default language.
	 * 
	 * @param o the o
	 * 
	 * @return true, if is default language
	 */
	private boolean isDefaultLanguage(Mrconso o){
		return o.getLat().equals(defaultLanguage);
	}
	
	/**
	 * Checks if is pref.
	 * 
	 * @param pref the pref
	 * 
	 * @return true, if is pref
	 */
	private boolean isPref(String pref){
		return pref.equals(isPref);
	}
	
	/**
	 * Are same language.
	 * 
	 * @param o1 the o1
	 * @param o2 the o2
	 * 
	 * @return true, if successful
	 */
	private boolean areSameLanguage(Mrconso o1, Mrconso o2){
		if(o1.getLat().equals(o2.getLat())){
			return true;
		}
		return false;
	}
	
	
	/**
	 * The Class LanguageComparator.
	 * 
	 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
	 */
	private class LanguageComparator implements Comparator<Mrconso> {
		
		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Mrconso o1, Mrconso o2) {

				if(areSameLanguage(o1, o2)){
					return 0;
				}
				if(isDefaultLanguage(o1)){
					return 1;
				}
				if(isDefaultLanguage(o2)){
					return -1;
				}
				return o1.getLat().compareTo(o2.getLat());
			}	
	}
	
	/**
	 * The Class IsPrefComparator.
	 * 
	 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
	 */
	private class IsPrefComparator implements Comparator<Mrconso> {
		
		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Mrconso o1, Mrconso o2) {
			
				if(isPref(o1.getIspref()) == isPref(o1.getIspref())){
					return 0;
				}
				if(isPref(o1.getIspref())){
					return 1;
				}	
				if(isPref(o2.getIspref())){
					return -1;
				}
				return 0;
			}	
	}
	
	/**
	 * The Class MrrankComparator.
	 * 
	 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
	 */
	private class MrrankComparator implements Comparator<Mrconso> {
		
		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Mrconso o1, Mrconso o2) {
			//There doesn't have to be a Mrrank.
//			if(mrrankUtility == null){
//				return 0;
//			}
//			int rank1 = mrrankUtility.getRank(sab, o1.getRepresentationalForm());
//			int rank2 = mrrankUtility.getRank(sab, o2.getRepresentationalForm());
//
//			if(rank1 == rank2){
//				return 0;
//			} else if(rank1 < rank2){
//				return -1;
//			} else {
//				return 1;
//			}
			return 0;
		}

	}

	/**
	 * Gets the mrrank utility.
	 * 
	 * @return the mrrank utility
	 */
	public MrrankUtility getMrrankUtility() {
		return mrrankUtility;
	}

	/**
	 * Sets the mrrank utility.
	 * 
	 * @param mrrankUtility the new mrrank utility
	 */
	public void setMrrankUtility(MrrankUtility mrrankUtility) {
		this.mrrankUtility = mrrankUtility;
	}

	/**
	 * Gets the sab.
	 * 
	 * @return the sab
	 */
//	public String getSab() {
//		return sab;
//	}
//
//	/**
//	 * Sets the sab.
//	 * 
//	 * @param sab the new sab
//	 */
//	public void setSab(String sab) {
//		this.sab = sab;
//	}

	/**
	 * Gets the default language.
	 * 
	 * @return the default language
	 */
	public String getDefaultLanguage() {
		return defaultLanguage;
	}

	/**
	 * Sets the default language.
	 * 
	 * @param defaultLanguage the new default language
	 */
	public void setDefaultLanguage(String defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}
	
	

}