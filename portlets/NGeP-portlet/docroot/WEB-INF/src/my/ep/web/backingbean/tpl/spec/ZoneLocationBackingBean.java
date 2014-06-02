/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.tpl.spec;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.biz.service.tpl.spec.ISpecZoneLocSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.dto.tpl.spec.SpecZoneLocReqDTO;
import my.ep.dto.tpl.spec.SpecZoneLocResDTO;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.tpl.spec.ZoneLocationManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sc.helper.LocationSingleton;
import my.ep.web.util.sc.helper.SourcingLocationHelper;

import org.apache.log4j.Logger;
import org.primefaces.model.TreeNode;

/**
 * Zone Location Backing Bean
 * 
 * @author Kenny
 * @since 31.05.2014
 */
@ManagedBean
@ViewScoped
public class ZoneLocationBackingBean extends BaseBackingBean<ZoneLocationManagedBean> {

	private static Logger log = Logger.getLogger(ZoneLocationBackingBean.class);
	
	private void debugLogger(Object message) {
        if(log.isDebugEnabled()) {
            log.debug(message);
        }
    }

	public ZoneLocationBackingBean() {
	}
	
	/**
	 * @param tplSpecZoneDtl the tplSpecZoneDtl to set
	 */
	public void setTplSpecZoneDtl(TplSpecZoneDtl tplSpecZoneDtl) {
		debugLogger("setTplSpecZoneDtl");
		
		ISpecZoneLocSO specZoneLocSO = null;
		try {			
			specZoneLocSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.COMMON_COMPONENT, ISpecZoneLocSO.class);
			
			SpecZoneLocReqDTO reqDTO = new SpecZoneLocReqDTO();
			reqDTO.setDocType(TemplateSpecConst.SPEC_LOC_DOC_TYPE_ZONE);
			reqDTO.setDocId(tplSpecZoneDtl.getSpecZoneId());
			
			SpecZoneLocResDTO resDTO = specZoneLocSO.retrieveLocationDtlsByDocIdNType(reqDTO);
			List<TplSpecLoc> tplSpecLocs = resDTO.getLocationsList();
			
			List<PmState> states = SourcingLocationHelper.getStateFromLocationDTO(tplSpecLocs);
			List<PmDivision> divisions = SourcingLocationHelper.getDivisionFromLocationDTO(tplSpecLocs);
			List<PmDistrict> districts = SourcingLocationHelper.getDistrictFromLocationDTO(tplSpecLocs);
			List<PmCity> cities = SourcingLocationHelper.getCityFromLocationDTO(tplSpecLocs);
			
			TreeNode locationTree = LocationSingleton.getInstance().buildLocationTree(
					tplSpecZoneDtl, 
					states, 
					districts, 
					divisions, 
					cities, 
					TplSpecZoneLoc.class);
			
			getManagedBean().setTplSpecZoneDtl(tplSpecZoneDtl);
			getManagedBean().setLocationTree(locationTree);
		} finally {
			specZoneLocSO = null;
		}
	}
}

