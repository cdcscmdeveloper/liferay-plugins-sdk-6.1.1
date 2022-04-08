/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.bean.pm;

import java.io.Serializable;

/**
 * Search Bean for Location
 * 
 * @author venugopal
 * @since 6/11/2014
 */
public class LocationSearchBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Long countryId;
	private String locationCode;
	private String locationType;
	private String locationName;
	private String recordStatus;
	//Enhancement29642 - Start
	private String additionalLocationType;
	private String additionalLocationName;
	private String recordAdditional;
	//Enhancement29642 - End
	public LocationSearchBean(){}
	
	/**
	 * @return the locationCode
	 */
	public String getLocationCode() {
		return locationCode;
	}

	/**
	 * @param locationCode the locationCode to set
	 */
	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}

	

	/**
	 * @return the locationType
	 */
	public String getLocationType() {
		return locationType;
	}

	/**
	 * @param locationType the locationType to set
	 */
	public void setLocationType(String locationType) {
		this.locationType = locationType;
	}

	/**
	 * @return the locationName
	 */
	public String getLocationName() {
		return locationName;
	}

	/**
	 * @param locationName the locationName to set
	 */
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	/**
	 * @return the recordStatus
	 */
	public String getRecordStatus() {
		return recordStatus;
	}

	/**
	 * @param recordStatus the recordStatus to set
	 */
	public void setRecordStatus(String recordStatus) {
		
		this.recordStatus = recordStatus;
	}

	/**
	 * @return the countryId
	 */
	public Long getCountryId() {
		return countryId;
	}

	/**
	 * @param countryId the countryId to set
	 */
	public void setCountryId(Long countryId) {
		this.countryId = countryId;
	}
	//Enhancement29642 - Start
	/**
	 * @return the additionalLocationType
	 */
	public String getAdditionalLocationType() {
		return additionalLocationType;
	}

	/**
	 * @param additionalLocationType the additionalLocationType to set
	 */
	public void setAdditionalLocationType(String additionalLocationType) {
		this.additionalLocationType = additionalLocationType;
	}

	/**
	 * @return the additionallocationName
	 */
	public String getAdditionalLocationName() {
		return additionalLocationName;
	}

	/**
	 * @param additionallocationName the additionallocationName to set
	 */
	public void setAdditionalLocationName(String additionalLocationName) {
		this.additionalLocationName = additionalLocationName;
	}
	/**
	 * @return the recordAdditional
	 */
	public String getRecordAdditional() {
		return recordAdditional;
	}
	
	/**
	 * @param setRecordAdditional the setRecordAdditional to set
	 */
	public void setRecordAdditional(String recordAdditional) {
		this.recordAdditional= recordAdditional;
	}
	//Enhancement29642 - End
}
