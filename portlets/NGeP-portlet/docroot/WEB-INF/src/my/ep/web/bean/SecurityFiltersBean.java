package my.ep.web.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.ep.persistence.entity.aut.AutScreenMetadata;

/**
 * @author Kenny
 * @created 03.12.2012
 *
 */
public class SecurityFiltersBean {
	// Map to keep  Screen Filters
	// The key is screen id with Map object
	// Lastly the key is component id with screenAuth in the Map object
	private HashMap<String, Map<String, String>> authScreenFilters;
	
	// Map to keep Data Filters
	// The key is screen id with Map object
	// Lastly the key is component id with collection of AutScreenMetadata in the Map object	
	private Map<String, Map<String, List<AutScreenMetadata>>> authDataFilters;

	public SecurityFiltersBean() {
		authScreenFilters = new HashMap<String, Map<String, String>>();
		authDataFilters = new HashMap<String, Map<String, List<AutScreenMetadata>>>();		
	}

	/**
	 * @return the authScreenFilters
	 */
	public HashMap<String, Map<String, String>> getAuthScreenFilters() {
		return authScreenFilters;
	}

	/**
	 * @param authScreenFilters the authScreenFilters to set
	 */
	public void setAuthScreenFilters(
			HashMap<String, Map<String, String>> authScreenFilters) {
		this.authScreenFilters = authScreenFilters;
	}

	/**
	 * @return the authDataFilters
	 */
	public Map<String, Map<String, List<AutScreenMetadata>>> getAuthDataFilters() {
		return authDataFilters;
	}

	/**
	 * @param authDataFilters the authDataFilters to set
	 */
	public void setAuthDataFilters(
			Map<String, Map<String, List<AutScreenMetadata>>> authDataFilters) {
		this.authDataFilters = authDataFilters;
	}
}
