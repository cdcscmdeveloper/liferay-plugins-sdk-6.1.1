/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.util.fl.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.ep.persistence.entity.ct.dtl.zone.CtContractZone;
import my.ep.persistence.entity.ct.dtl.zone.CtContractZoneDtl;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
import my.ep.web.constant.sc.QTConstants;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 * The Class LocationSingleton.
 * 
 * @author Giribabu
 * @since 22/9/2014
 */
public class LocationSingleton {
	public static final String STATE_LEVEL = "S";
	public static final String DIVISION_LEVEL = "V";
	public static final String DISTRICT_LEVEL = "D";
	private static LocationSingleton instance = null;
	private Map<Long, List<PmState>> mapStates = new HashMap<Long, List<PmState>>();

	private LocationSingleton() {
	}

	public static LocationSingleton getInstance() {
		if (instance == null) {
			instance = new LocationSingleton();
		}
		return instance;
	}


	public void updateMapStates(Long countryId, List<PmState> pmStates) {
		mapStates.put(countryId, pmStates);
	}

	public Boolean isInitialization(Long countryId) {
		if (mapStates.get(countryId) != null
				&& mapStates.get(countryId).size() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * Constuct tree node.
	 *
	 * @param tplSpecZoneDtl the tpl spec zone dtl
	 * @param specLocs the spec locs
	 * @return the tree node
	 */
	public TreeNode constuctTreeNode(TplSpecZoneDtl tplSpecZoneDtl,
			List<TplSpecZoneLoc> specLocs) {
		TreeNode root = new DefaultTreeNode(QTConstants.NODE_ROOT, null);
		if (tplSpecZoneDtl != null) {
			TreeNode zoneNode = new DefaultTreeNode(QTConstants.NODE_ZONE,
					tplSpecZoneDtl, root);
			List<PmState> states = new ArrayList<PmState>();
			List<PmDivision> divisions = new ArrayList<PmDivision>();
			List<PmDistrict> districts = new ArrayList<PmDistrict>();
			TreeNode stateNode = null;
			TreeNode divisonNode = null;
			for (TplSpecLoc locCriteria : specLocs) {
				PmState pmState = locCriteria.getPmState();

				if (null != pmState && !states.contains(pmState)) {
					stateNode = new DefaultTreeNode(QTConstants.NODE_STATE,
							locCriteria, zoneNode);
				}
				PmDivision division = locCriteria.getPmDivision();

				if (null != division && !divisions.contains(pmState)) {
					divisonNode = new DefaultTreeNode(
							QTConstants.NODE_DIVISION, locCriteria, stateNode);
					divisions.add(division);
				}
				PmDistrict pmDistrict = locCriteria.getPmDistrict();

				if (null != pmDistrict && !districts.contains(pmDistrict)) {
					if (divisonNode == null) {
						new DefaultTreeNode(QTConstants.NODE_DISTRICT,
								locCriteria, stateNode);
					} else {
						new DefaultTreeNode(QTConstants.NODE_DISTRICT,
								locCriteria, divisonNode);
					}
					districts.add(pmDistrict);
				}
				states.add(pmState);
			}
		} else {

		}
		return root;
	}
	
	/**
	 * Constuct contract tree node.
	 *
	 * @param zone the zone
	 * @return the tree node
	 */
	public TreeNode constuctContractTreeNode(CtContractZone zone) {
		TreeNode root = new DefaultTreeNode(QTConstants.NODE_ROOT, null);
		if (zone != null) {
			TreeNode zoneNode = new DefaultTreeNode(QTConstants.NODE_ZONE,
					zone, root);
			List<PmState> states = new ArrayList<PmState>();
			List<PmDivision> divisions = new ArrayList<PmDivision>();
			List<PmDistrict> districts = new ArrayList<PmDistrict>();
			TreeNode stateNode = null;
			TreeNode divisonNode = null;
			 for (CtContractZoneDtl locCriteria : zone.getCtContractZoneDtls()) {
				PmState pmState = locCriteria.getState();

				if (null != pmState && !states.contains(pmState)) {
					stateNode = new DefaultTreeNode(QTConstants.NODE_STATE,
							locCriteria, zoneNode);
				}
				PmDivision division = locCriteria.getDivision();

				if (null != division && !divisions.contains(pmState)) {
					divisonNode = new DefaultTreeNode(
							QTConstants.NODE_DIVISION, locCriteria, stateNode);
					divisions.add(division);
				}
				PmDistrict pmDistrict = locCriteria.getDistrict();

				if (null != pmDistrict && !districts.contains(pmDistrict)) {
					if (divisonNode == null) {
						if(stateNode!=null) {
							new DefaultTreeNode(QTConstants.NODE_DISTRICT,
								locCriteria, stateNode);
						} else {
							new DefaultTreeNode(QTConstants.NODE_DISTRICT,
									locCriteria, zoneNode);
						}
					} else {
						new DefaultTreeNode(QTConstants.NODE_DISTRICT,
								locCriteria, divisonNode);
					}
					districts.add(pmDistrict);
				}
				states.add(pmState);
			}
		} else {

		}
		return root;
	}
}
