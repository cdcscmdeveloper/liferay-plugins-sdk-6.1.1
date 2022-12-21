/**
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.util.sc.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import my.ep.biz.service.cm.catalogue.IZoneMaintenanceSO;
import my.ep.biz.service.tpl.spec.ISpecZoneLocSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.cm.catalogue.ZoneMaintenanceReqDTO;
import my.ep.dto.cm.catalogue.ZoneMaintenanceResDTO;
import my.ep.dto.tpl.spec.SpecZoneLocReqDTO;
import my.ep.dto.tpl.spec.SpecZoneLocResDTO;
import my.ep.persistence.entity.cm.catalog.CmSupplierZone;
import my.ep.persistence.entity.cm.catalog.CmSupplierZoneDTL;
import my.ep.persistence.entity.tpl.spec.TplSpecZone;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * 
 * SourcingZoneHelper class is used to construct Zones and Locations
 * 
 * @author VidyaSagarReddy
 * @since 22.11.2013
 * 
 */
public class SourcingZoneHelper {
	private static final String ZONE_SEPARATOR = "_";
	private static SourcingZoneHelper instance = null;

	private SourcingZoneHelper() {
	}

	/**
	 * To get SourcingZoneHelper instance
	 * 
	 * @return SourcingZoneHelper instance
	 */
	public static SourcingZoneHelper getInstance() {
		if (instance == null) {
			instance = new SourcingZoneHelper();
		}
		return instance;
	}

	/**
	 * To construct Zones
	 * 
	 * @param supplierZone
	 * @param zoneDtlMap
	 * @param specZone
	 * @param brandNew
	 * @return TplSpecZoneDtl instance
	 */
	public TplSpecZoneDtl createSpecZone(CmSupplierZone supplierZone,
			Map<String, TplSpecZoneDtl> zoneDtlMap, TplSpecZone specZone,
			boolean brandNew) {
		IZoneMaintenanceSO zoneMaintenanceSO = (IZoneMaintenanceSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.CATALOGUE_MANAGEMENT,
						IZoneMaintenanceSO.class);
		ZoneMaintenanceReqDTO reqDTO = new ZoneMaintenanceReqDTO();
		TplSpecZoneDtl specZoneDtl = null;
		if (supplierZone != null && supplierZone.getSuppZoneId() > 0) {

			reqDTO.setCmSupplierZoneId(supplierZone.getSuppZoneId());
			ZoneMaintenanceResDTO resDTO = zoneMaintenanceSO
					.getSupplierZoneById(reqDTO);
			CmSupplierZone CmSupplierZone = resDTO.getSelectedCmSupplierZone();

			if (CmSupplierZone.getCmSupplierZoneDtls() != null
					&& CmSupplierZone.getCmSupplierZoneDtls().size() > 0) {
				StringBuilder zoneKey = new StringBuilder();
				String suppZoneName = CmSupplierZone.getSuppZoneName();

				zoneKey.append(CmSupplierZone.getSuppZoneId());
				zoneKey.append(ZONE_SEPARATOR);
				zoneKey.append(suppZoneName);

				if (zoneDtlMap.size() > 0) {

					if (!brandNew) {
						putDBSpecZoneDtlsIntoMap(zoneDtlMap, specZone);
					}
				}
				specZoneDtl = zoneDtlMap.get(zoneKey.toString());

				if (specZoneDtl == null) {
					specZoneDtl = new TplSpecZoneDtl();
					specZoneDtl.setZoneName(suppZoneName);
					specZoneDtl.setSupplierZone(CmSupplierZone);
					specZoneDtl.setTplSpecZone(specZone);

					List<TplSpecZoneLoc> zoneLocs = new ArrayList<TplSpecZoneLoc>();
					for (CmSupplierZoneDTL zoneDtl : CmSupplierZone
							.getCmSupplierZoneDtls()) {
						TplSpecZoneLoc specZoneLoc = new TplSpecZoneLoc();
						specZoneLoc.setPmDistrict(zoneDtl.getPmDistrict());
						specZoneLoc.setPmState(zoneDtl.getPmState());
						specZoneLoc.setPmDivision(zoneDtl.getPmDivision());
						specZoneLoc.setTplSpecZoneDtl(specZoneDtl);
						zoneLocs.add(specZoneLoc);
					}
					specZoneDtl.setTplSpecZoneLocs(zoneLocs);

					zoneDtlMap.put(zoneKey.toString(), specZoneDtl);
				}
			}
		}
		return specZoneDtl;
	}

	private void putDBSpecZoneDtlsIntoMap(
			Map<String, TplSpecZoneDtl> zoneDtlMap, TplSpecZone zone) {
		ISpecZoneLocSO specZoneLocSO = null;

		try {
			specZoneLocSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.COMMON_COMPONENT, ISpecZoneLocSO.class);
			SpecZoneLocReqDTO reqDTO = new SpecZoneLocReqDTO();
			reqDTO.setDocType(ParameterConst.DOCTYPE_REQUEST_NOTE);
			reqDTO.setDocId(zone.getSpecZoneId());
			// reqDTO.setDocId(3391L);
			SpecZoneLocResDTO resDTO = specZoneLocSO
					.retrieveZoneDtlListByDocIdNType(reqDTO);

			StringBuilder dbZoneKey = null;
			List<TplSpecZoneDtl> dbSpecZoneDtls = resDTO.getZonesList();

			for (TplSpecZoneDtl dbSpecZoneDtl : dbSpecZoneDtls) {
				dbZoneKey = new StringBuilder();
				dbZoneKey.append(dbSpecZoneDtl.getSupplierZone()
						.getSuppZoneId());
				dbZoneKey.append(ZONE_SEPARATOR);
				dbZoneKey.append(dbSpecZoneDtl.getZoneName());

				zoneDtlMap.put(dbZoneKey.toString(), dbSpecZoneDtl);
			}
		} finally {
			specZoneLocSO = null;
		}
	}
}
