package my.ep.web.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.ep.persistence.entity.pm.authorization.PmScreenRestr;

/**
 * @author Kenny
 * @created 03.12.2012
 * 
 */
public class SecurityFiltersBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Map to keep Screen Filters
	// The key is screen id with Map object
	// Lastly the key is component id with screenAuth in the Map object
	private HashMap<String, Map<String, String>> authScreenFilters;

	// Map to keep Data Filters
	// The key is screen id with Map object
	// Lastly the key is component id with collection of AutScreenMetadata in
	// the Map object

	private Map<String, Map<String, List<PmScreenRestr>>> autDataFilters;

	public SecurityFiltersBean() {
		authScreenFilters = new HashMap<String, Map<String, String>>();
		autDataFilters = new HashMap<String, Map<String, List<PmScreenRestr>>>();
	}

	/**
	 * @return the authScreenFilters
	 */
	public HashMap<String, Map<String, String>> getAuthScreenFilters() {
		return authScreenFilters;
	}

	/**
	 * @param authScreenFilters
	 *            the authScreenFilters to set
	 */
	public void setAuthScreenFilters(
			HashMap<String, Map<String, String>> authScreenFilters) {
		this.authScreenFilters = authScreenFilters;
	}

	public Map<String, Map<String, List<PmScreenRestr>>> getAutDataFilters() {
		return autDataFilters;
	}

	public void setAutDataFilters(
			Map<String, Map<String, List<PmScreenRestr>>> autDataFilters) {
		this.autDataFilters = autDataFilters;
	}

	
}
