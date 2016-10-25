/* 
 ***************************************************   
 * Product nextgen ePerolehan 
 * 
 * Release 1.00    
 *    
 * Copyright © 2016 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.converter.sc;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * The Class OrgProfileAutoCompleteConverter.
 * 
 * @author Hemanth Katta
 */
@FacesConverter(value = "orgProfileAutoCompleteConverter")
public class OrgProfileAutoCompleteConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String value) {
		
		if (value != null && value.trim().length() > 0) {
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgProfileId(Long.valueOf(value));
			IOrgProfileSO parameterSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IOrgProfileSO.class);
			OrgProfileResDTO resDTO = parameterSO
					.retrieveOrgProfileByProfileId(reqDTO);
			return resDTO.getPmOrgProfile();
		} else {
			return null;
		}
	}


	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {

		if (arg2 != null && arg2 instanceof PmOrgProfile) {
			return String.valueOf(((PmOrgProfile) arg2).getOrgProfileId());
		}
		return "";
	}
}