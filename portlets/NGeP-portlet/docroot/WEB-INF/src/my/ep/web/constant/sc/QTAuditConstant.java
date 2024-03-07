/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.constant.sc;

import my.ep.web.backingbean.sc.supplierproposal.SPDetailBackingBean;

/**
 * QTConstants for constants for sourcing.
 * 
 * @author Ahmad Adli
 * @since 7.3.2024
 */
public interface QTAuditConstant {

	public static final String AU_TYPE_CREATE = "A";
	public static final String AU_TYPE_MODIFY = "U";
	public static final String AU_TYPE_DELETE = "D";
	
	public static final String AU_SECTION_GI = "GI";
	public static final String AU_SECTION_BSV = "BSV";
	public static final String AU_SECTION_SL = "SL";
	public static final String AU_SECTION_DOA = "DOA";
	public static final String AU_SECTION_DO = "DO";
	public static final String AU_SECTION_SC = "SC";
	public static final String AU_SECTION_EC = "EC";
	public static final String AU_SECTION_OC = "OC";
	public static final String AU_SECTION_TEC = "TEC";
	public static final String AU_SECTION_FEC = "FEC";
	public static final String AU_SECTION_QC = "QC";
	public static final String AU_SECTION_PB = "PB";

	public static final Long AU_FIELD_GI_PUBLISHING_PERIOD = 1L; 
	public static final Long AU_FIELD_GI_PROPOSAL_VALIDITY = 2L; 
	public static final Long AU_FIELD_BSV_DESCRIPTION = 3L;
	public static final Long AU_FIELD_BSV_DATE_DAY_TIME = 4L; 
	public static final Long AU_FIELD_BSV_LOCATION = 5L;
	public static final Long AU_FIELD_BSV_ADDRESS_1 = 6L;
	public static final Long AU_FIELD_BSV_ADDRESS_2 = 7L;
	public static final Long AU_FIELD_BSV_ADDRESS_3 = 8L;
	public static final Long AU_FIELD_BSV_POST_CODE = 9L;
	public static final Long AU_FIELD_BSV_STATE = 10L;
	public static final Long AU_FIELD_BSV_DIVISION = 11L;
	public static final Long AU_FIELD_BSV_DISTRICT = 12L;
	public static final Long AU_FIELD_BSV_CITY_TOWN = 13L;
	public static final Long AU_FIELD_BSV_COORDINATING_OFFICER = 14L;
	public static final Long AU_FIELD_BSV_MAXIMUM_ATTENDANCE = 15L;
	public static final Long AU_FIELD_BSV_STATUS = 16L;
	public static final Long AU_FIELD_SL_COMPANY_NAME = 17L;
	public static final Long AU_FIELD_DOA_DESK_OFFICER_ASSISTANT_NAME = 18L;
	public static final Long AU_FIELD_DO_NEW_ASSIGNED_DESK_OFFICER = 19L;
	public static final Long AU_FIELD_SC_NAME = 20L;
	public static final Long AU_FIELD_SC_IDENTIFICATION = 21L;
	public static final Long AU_FIELD_SC_ROLE = 22L;
	public static final Long AU_FIELD_EC_NAME = 23L;
	public static final Long AU_FIELD_EC_IDENTIFICATION = 24L;
	public static final Long AU_FIELD_EC_ROLE = 25L;
	public static final Long AU_FIELD_OC_NAME = 26L;
	public static final Long AU_FIELD_OC_IDENTIFICATION = 27L;
	public static final Long AU_FIELD_OC_ROLE = 28L;
	public static final Long AU_FIELD_TEC_NAME = 29L;
	public static final Long AU_FIELD_TEC_IDENTIFICATION = 30L;
	public static final Long AU_FIELD_TEC_ROLE = 31L;
	public static final Long AU_FIELD_FEC_NAME = 32L;
	public static final Long AU_FIELD_FEC_IDENTIFICATION = 33L;
	public static final Long AU_FIELD_FEC_ROLE = 34L;
	public static final Long AU_FIELD_QC_NAME = 35L;
	public static final Long AU_FIELD_QC_IDENTIFICATION = 36L;
	public static final Long AU_FIELD_QC_ROLE = 37L;
	public static final Long AU_FIELD_PB_NAME = 38L;
	public static final Long AU_FIELD_PB_IDENTIFICATION = 39L;
	public static final Long AU_FIELD_PB_ROLE = 40L;
	
}
