/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1 . 00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.backingbean.cm.catalogue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.cm.catalogue.IZoneMaintenanceSO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.division.IDivisionSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.MessageConst;
import my.ep.dto.cm.catalogue.ZoneMaintenanceReqDTO;
import my.ep.dto.cm.catalogue.ZoneMaintenanceResDTO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.division.DivisionReqDTO;
import my.ep.dto.pm.common.division.DivisionResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.cm.catalog.CmSupplierZone;
import my.ep.persistence.entity.cm.catalog.CmSupplierZoneDTL;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sm.SmDisciplinaryAction;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.cm.catalogue.ZoneMaintenanceManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.cm.helper.CmApplicationUtil;
import my.ep.web.util.cm.helper.CmSupplierZoneSingleton;
import my.ep.web.util.cm.helper.CmSupplierZoneStateDistrictDTO;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.TreeNode;


/**
 * The Class ZoneMaintenanceBackingBean.
 * @author hketan
 */
@ManagedBean(name = "zoneMaintenanceBackingBean")
@ViewScoped
public class ZoneMaintenanceBackingBean extends
BaseBackingBean<ZoneMaintenanceManagedBean> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** The log. */
	private static Logger log = Logger.getLogger(ZoneMaintenanceBackingBean.class);
	
	//[#26282] disciplinary action - start
	//Navigation Details
	private static final String NAV_TO_DA = "/xhtml/cm/shared/applicationView/cmDisciplinaryActionViewOnly.xhtml";
	//[#26282] disciplinary action - end

	/**
	 * Instantiates a new zone maintenance backing bean.
	 */
	public ZoneMaintenanceBackingBean() {
		log.debug("********************ZoneMaintenanceBackingBean Constructor***************");
	}

	/**
	 * Pre render zone view.
	 */
	public void preRenderZoneView(){
		log.info("********************ZoneMaintenanceBackingBean PreRender***************");
		getManagedBean().setCurrentSupplier(getCurrentUserSupplier());
		showZonePopup();
		this.getManagedBean().setAllZoneSelected(false);
		getManagedBean().setSelectedSupplierZone(null);
		CountryReqDTO reqDTO = new CountryReqDTO();
		reqDTO.setCountryCode("MY");
		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance().getServiceObject("pm",ICountrySO.class);
		CountryResDTO resDTO = reqSO.retrieveCountryByCode(reqDTO);
		getManagedBean().setDefaultCountryId(resDTO.getPmCountry().getCountryId());
		
		//[#26282] disciplinary action - start
		checkForDisciplinaryAction();
		//[#26282] disciplinary action - end

		FacesContext fc = FacesContext.getCurrentInstance();		
		
		//[#26282] disciplinary action - start
		if(this.getManagedBean().isDaCancelReg() || this.getManagedBean().isDaBlacklistOwner()) {
			//disciplinary action page
			fc.getApplication().getNavigationHandler()
			.handleNavigation(fc, null, NAV_TO_DA);
		} else {
			fc.getApplication().getNavigationHandler()
			.handleNavigation(fc, null, "cmSupplierZoneMaintenance");
		}
		//[#26282] disciplinary action - end

	}

	/**
	 * Gets the zone.
	 * 
	 * @param zoneLocation
	 *            (TreeNode zoneLocation(mandatory))
	 * @return CmSupplierZone
	 */
	private CmSupplierZone getZone(TreeNode zoneLocation) {
		CmSupplierZone result = null;
		if (zoneLocation.getData() instanceof CmSupplierZoneStateDistrictDTO) {
			CmSupplierZoneStateDistrictDTO cmDTO = (CmSupplierZoneStateDistrictDTO) zoneLocation.getData();
			if (cmDTO.getCmSupplierZone() != null) {
				result = cmDTO.getCmSupplierZone();
			} else {
				if (zoneLocation.getChildren() != null
						&& zoneLocation.getChildren().size() > 0) {
					for (TreeNode childLocationNode : zoneLocation
							.getChildren()) {
						result = getZone(childLocationNode);
					}
				}
			}
		} else {
			if (zoneLocation.getChildren() != null
					&& zoneLocation.getChildren().size() > 0) {
				for (TreeNode childLocationNode : zoneLocation.getChildren()) {
					result = getZone(childLocationNode);
				}
			}
		}
		return result;
	}

	/**
	 * Delete tree nodes.
	 * 
	 * @param rootZoneLocation
	 *           (TreeNode rootZoneLocation(mandatory))
	 */
	public void deleteTreeNodes() {
		//final DataTable d = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("form:zoneDetailsDataTable");
		Iterator<CmSupplierZone> ite = getManagedBean().getCmSupplierZoneList().iterator();
		List<CmSupplierZone> deleteList = new ArrayList<CmSupplierZone>();
		if(getManagedBean().getDeletedCmSupplierZoneList() != null && getManagedBean().getDeletedCmSupplierZoneList().size() > 0){
			deleteList = getManagedBean().getDeletedCmSupplierZoneList();
		}
		while(ite.hasNext()){
			CmSupplierZone cmZone = ite.next(); 
			for(CmSupplierZone zone: this.getManagedBean().getTempDeletedSupplierZoneList()){
				if(cmZone.getSuppZoneName().equalsIgnoreCase(zone.getSuppZoneName())){
					if(cmZone.getCmPricings() != null && cmZone.getCmPricings().size() > 0){
						throw new EpException(MessageConst.ERROR_716005);
					}
					deleteList.add(cmZone);
					ite.remove();
					break;
				}
			}
		}
	
		getManagedBean().setDeletedCmSupplierZoneList(deleteList);	
		//log.info("the delete list ===> " + this.getManagedBean().getDeletedCmSupplierZoneList().size());
		getManagedBean().setZoneTrees(CmSupplierZoneSingleton.getInstance().buildZoneSDTree(this.getManagedBean().getCmSupplierZoneList()));
		RequestContext.getCurrentInstance().execute("deleteConfirmation.hide();");
	}

	/**
	 * Delete Cmsupplier zones.
	 */
	public void deleteCmSupplierZones(){
		log.debug("********************ZoneMaintenanceBackingBean deleteCmSupplierZones***************");
		IZoneMaintenanceSO zoneMaintenanceSO = null;
		zoneMaintenanceSO = (IZoneMaintenanceSO) BusinessDelegate
				.getInstance().getServiceObject("cm", IZoneMaintenanceSO.class);
		ZoneMaintenanceReqDTO reqDTO = new ZoneMaintenanceReqDTO();
		reqDTO.setCmSupplierZoneList(getManagedBean().getDeletedCmSupplierZoneList());
		ZoneMaintenanceResDTO resDTO = new ZoneMaintenanceResDTO(); 
		if(getManagedBean().getDeletedCmSupplierZoneList().size() > 0)
			resDTO = zoneMaintenanceSO.delete(reqDTO);

		getManagedBean().setZoneTrees(CmSupplierZoneSingleton.getInstance().buildZoneSDTree(this.getManagedBean().getCmSupplierZoneList()));
		if (resDTO.isSuccessful()) {
			log.info("SuccessMMMMMMMMMMMMMMMMM");
			this.getManagedBean().setDeletedCmSupplierZoneList(null);
		} else {
			log.info("Not SuccessMMMMMMMMMMMMMMMMM");
			throw new EpException(MessageConst.ERROR_716001);
			
			
			
		}
	}

	/**
	 * save CmSupplierZones .
	 * 
	 * @return string
	 */
	public String saveCmSupplierZones() {
		log.debug("********************ZoneMaintenanceBackingBean saveCmSupplierZones***************");
		assignValReqdToFalse();
		/* First delete any records that are to be deleted */
		if(getManagedBean().getDeletedCmSupplierZoneList() != null && getManagedBean().getDeletedCmSupplierZoneList().size()>0)
			deleteCmSupplierZones();
		IZoneMaintenanceSO zoneMaintenanceSO = null;
		zoneMaintenanceSO = (IZoneMaintenanceSO) BusinessDelegate
				.getInstance().getServiceObject("cm", IZoneMaintenanceSO.class);
		ZoneMaintenanceReqDTO reqDTO = new ZoneMaintenanceReqDTO();
		reqDTO.setCmSupplierZoneList(getManagedBean().getCmSupplierZoneList());
		log.info("Current Size:" + getManagedBean().getCmSupplierZoneList().size());
		
		Map checkRepeatMap = new HashMap();
		if(getManagedBean().getCmSupplierZoneList()!=null && getManagedBean().getCmSupplierZoneList().size()>0){
			for(CmSupplierZone zone : getManagedBean().getCmSupplierZoneList()){
				
				for(CmSupplierZoneDTL zoneDet : zone.getCmSupplierZoneDtls()){
					if(!checkRepeatMap.containsKey(zoneDet.getPmDistrict().getDistrictId())){
						checkRepeatMap.put(zoneDet.getPmDistrict().getDistrictId(), zoneDet.getPmDistrict().getDistrictId());
					}else{
						throw new EpException(MessageUtils
								.getMessage(
										MessageConst.ERROR_716015, new String[] {zoneDet.getPmDistrict().getDistrictName()},
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes()
										.getUserLocale()));
						
					}
				}
				
			}
		}
		
		ZoneMaintenanceResDTO resDTO = new ZoneMaintenanceResDTO();
		if(getManagedBean().getCmSupplierZoneList().size() > 0)
			resDTO = zoneMaintenanceSO.save(reqDTO);
		else 
			resDTO.setSuccessful(true);

		if(resDTO.isSuccessful()){
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
					FacesMessage.SEVERITY_INFO,
					null,
					MessageUtils.getMessage(MessageConst.INFO_711001, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));

		}
		
		System.out.println("*****return*******");
		return "/xhtml/common/acknowledge.xhtml";

	}

	/**
	 * Edits the zone.
	 * 
	 * @param rootZoneLocation
	 *            (TreeNode rootZoneLocation(mandatory))
	 * @param zoneIndex
	 *            (int zoneIndex(mandatory))
	 */
	public void editZone(TreeNode rootZoneLocation, int zoneIndex) {
		getManagedBean().setEditMode(true);
		//		List<CmSupplierZoneStateDistrictDTO> detailDTOs = getDetails(rootZoneLocation);
		CmSupplierZone zone = getZone(rootZoneLocation);
		//		List<PmState> states = SourcingZoneDetailHelper
		//				.getStateFromDetailDTOs(detailDTOs);
		//		List<PmDivision> divisions = SourcingZoneDetailHelper
		//				.getDivisionFromDetailDTOs(detailDTOs);
		//		List<PmDistrict> districts = SourcingZoneDetailHelper
		//				.getDistrictFromDetailDTOs(detailDTOs);

		List<PmState> states = getPmStates(rootZoneLocation.getChildren());
		List<PmDivision> divisions = getPmDivisions(rootZoneLocation.getChildren());
		List<PmDistrict> districts = getPmDistricts(rootZoneLocation.getChildren());
		getManagedBean().setRootZoneTree(
				CmSupplierZoneSingleton.getInstance().buildTree(getManagedBean().getDefaultCountryId(),	states, districts, divisions));
		int i = zoneIndex;
		this.getManagedBean().setZoneIndex(i);
		getManagedBean().setZoneTrees(CmSupplierZoneSingleton.getInstance().buildZoneSDTree(this.getManagedBean().getCmSupplierZoneList()));
		disableSelectedDistrict(districts);
		for(CmSupplierZone suppZone : getManagedBean().getCmSupplierZoneList()){
			if((zone.getSuppZoneId() != 0 && (zone.getSuppZoneId() == suppZone.getSuppZoneId())) || zone.getSuppZoneName().equalsIgnoreCase(suppZone.getSuppZoneName())){
				getManagedBean().setSelectedSupplierZone(suppZone);
				getManagedBean().setZoneName(suppZone.getSuppZoneName());
			}
		}
	}

	/**
	 * Adds the new zone.
	 */
	public void addNewZone() {
		log.debug("********************ZoneMaintenanceBackingBean addNewZone***************");
		this.getManagedBean().setSelectedNodes(null);
		getManagedBean().setEditMode(false);
		assignValReqdToTrue();
		getManagedBean().setSelectedSupplierZone(new CmSupplierZone());
		getManagedBean().getSelectedSupplierZone().setSuppZoneName(null);
		getManagedBean().setRootZoneTree(
				CmSupplierZoneSingleton.getInstance().buildCmSupplierZoneTree(getManagedBean().getDefaultCountryId(), null, null, null));
		disableSelectedDistrict(null);
	}

	/**
	 * To Show Zone Popup.
	 */
	public void showZonePopup() {
		SmSupplier smSupplier = getManagedBean().getCurrentSupplier();
		if(smSupplier != null && smSupplier.getSupplierId() != null){
			IZoneMaintenanceSO zoneMaintenanceSO = (IZoneMaintenanceSO) BusinessDelegate
					.getInstance().getServiceObject("cm", IZoneMaintenanceSO.class);
			ZoneMaintenanceReqDTO reqDTO = new ZoneMaintenanceReqDTO();
			reqDTO.setSupplierId(smSupplier.getSupplierId());
			ZoneMaintenanceResDTO resDTO = zoneMaintenanceSO.getCmSupplierZonesBySupplier(reqDTO);
			this.getManagedBean().setCmSupplierZoneList(resDTO.getCmSupplierZones());
		} else {
			this.getManagedBean().setCmSupplierZoneList(new ArrayList<CmSupplierZone>());
		}
		
		Collections.sort(this.getManagedBean().getCmSupplierZoneList(), new Comparator<CmSupplierZone>() {
			  @Override
		        public int compare(CmSupplierZone o1, CmSupplierZone o2) {
				      return o1.getSuppZoneName().compareToIgnoreCase(o2.getSuppZoneName());
		        }
		});
	
		this.getManagedBean().setDeletedCmSupplierZoneList(null);
		this.getManagedBean().setZoneTrees(CmSupplierZoneSingleton.getInstance().buildZoneSDTree(this.getManagedBean().getCmSupplierZoneList()));
		this.getManagedBean().setRootZoneTree(CmSupplierZoneSingleton.getInstance().buildCmSupplierZoneTree(getManagedBean().getDefaultCountryId(), null, null, null));
		
		disableSelectedDistrict(null);
		
		if(this.getManagedBean().getZoneTrees()!=null && this.getManagedBean().getZoneTrees().size()>0){
			for(TreeNode tree: this.getManagedBean().getZoneTrees()){
				CmSupplierZoneStateDistrictDTO zoneDTO =  (CmSupplierZoneStateDistrictDTO)tree.getChildren().get(0).getData();
				CmSupplierZone zone = zoneDTO.getCmSupplierZone();
				if(zone.getCmPricings() != null && zone.getCmPricings().size() > 0){
					zone.setSelectable(false);
				}
			}
		}
	}


	/**
	 * Gets the current user supplier.
	 * 
	 * @return SmSupplier
	 */
	private SmSupplier getCurrentUserSupplier() {
		SmSupplier smSupp = new SmSupplier();
		try {
			SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
			PmUser pmUser = new FacesScopes().getUserSessionBean();
			supplierSearchReqDTO.setUserId(pmUser.getUserId());
			ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							ISupplierSearchSO.class);
			SupplierSearchResDTO smResDTO = supplierSearchSO
					.retriveSupplierProfiles(supplierSearchReqDTO);
			smSupp = (SmSupplier) smResDTO.getResultBean().getResultList()
					.get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return smSupp;
	}

	/*	private List<CmSupplierZoneStateDistrictDTO> getDetails(TreeNode zoneDetail) {
		List<CmSupplierZoneStateDistrictDTO> results = new ArrayList<CmSupplierZoneStateDistrictDTO>();
		if (zoneDetail.isLeaf()) {
			CmSupplierZoneStateDistrictDTO cmSupplierZoneStateDistrictDTO = (CmSupplierZoneStateDistrictDTO) zoneDetail.getData();
			results.add(cmSupplierZoneStateDistrictDTO);
		} else {
			for (TreeNode childrendDetailNode : zoneDetail.getChildren()) {
				results.addAll(getDetails(childrendDetailNode));
			}
		}
		return results;
	}
	 */	
	/**
	 * Gets the pm states.
	 * 
	 * @param  treeNodes
	 *         (List<TreeNode> treeNodes(mandatory))
	 * @return List<PmState>
	 */
	private List<PmState> getPmStates(List<TreeNode> treeNodes) {
		List<PmState> results = new ArrayList<PmState>();
		for (TreeNode rootNode : treeNodes) {
			for (TreeNode treeNode : rootNode.getChildren()) {
				if (treeNode.getType().equals("state")) {
					CmSupplierZoneStateDistrictDTO locationDTO = (CmSupplierZoneStateDistrictDTO) treeNode.getData();
					if (locationDTO.getState() != null) {
						results.add(locationDTO.getState());
					}
				}
			}
		}
		return results;
	}

	/**
	 * Gets the Pm divisions.
	 * 
	 * @param treeNodes
	 *           (List<TreeNode> treeNodes(mandatory))
	 * @return List<PmDivision>
	 */
	private List<PmDivision> getPmDivisions(List<TreeNode> treeNodes) {
		List<PmDivision> results = new ArrayList<PmDivision>();
		for (TreeNode rootNode : treeNodes) {
			for (TreeNode treeNode : rootNode.getChildren()) {
				for (TreeNode childNode : treeNode.getChildren()) {
					if (childNode.getType().equals("division")) {
						CmSupplierZoneStateDistrictDTO locationDTO = (CmSupplierZoneStateDistrictDTO) childNode.getData();
						if (locationDTO.getDivision() != null) {
							results.add(locationDTO.getDivision());
						}
					}
				}
			}
		}
		return results;
	}

	/**
	 * Gets the Pm districts.
	 * 
	 * @param treeNodes
	 *            List<TreeNode> treeNodes(mandatory))
	 * @return List<PmDistrict>
	 */
	private List<PmDistrict> getPmDistricts(List<TreeNode> treeNodes) {
		List<PmDistrict> results = new ArrayList<PmDistrict>();
		for (TreeNode treeNode : treeNodes) {
			if (treeNode.isLeaf()) {
				CmSupplierZoneStateDistrictDTO locationDTO = (CmSupplierZoneStateDistrictDTO) treeNode.getData();
				if (locationDTO.getDistrict() != null) {
					results.add(locationDTO.getDistrict());
				}
			} else {
				if (treeNode.getChildren() != null) {
					results.addAll(getPmDistricts(treeNode.getChildren()));
				}
			}
		}
		return results;
	}


	/**
	 * Update zone list on pop up close.
	 */
	public void updateZoneListOnPopUpClose(){
		log.debug("********************ZoneMaintenanceBackingBean updateZoneListOnPopUpClose***************");
		CmSupplierZone selZone = getManagedBean().getSelectedSupplierZone();
		if(selZone.getSuppZoneName().isEmpty()) {
			log.info(111);
			throw new EpException(MessageConst.ERROR_716003);
		} else {
			if(!getManagedBean().getCmSupplierZoneList().contains(selZone)){
				for(CmSupplierZone zone : getManagedBean().getCmSupplierZoneList()){
					if(selZone.getSuppZoneName().equalsIgnoreCase(zone.getSuppZoneName())){
						log.info(222);
						throw new EpException(MessageConst.ERROR_716004);
					}
				}
			}
		}

		if(selZone.getSuppZoneId() == 0 && !getManagedBean().getCmSupplierZoneList().contains(selZone)){
			log.info(333);
			selZone = new CmSupplierZone();
			selZone.setSuppZoneName(getManagedBean().getSelectedSupplierZone().getSuppZoneName());
			if(selZone.getSmSupplier() == null){
				log.info(444);
				selZone.setSmSupplier(getManagedBean().getCurrentSupplier());
			}
			if(selZone.getSuppZoneDesc() == null){
				log.info(555);
				selZone.setSuppZoneDesc(selZone.getSuppZoneName());
			}
		}
		TreeNode[] selectedNodes = getManagedBean().getSelectedNodes();
		List<CmSupplierZoneDTL> newCmSupplierZoneDTLs = new ArrayList<CmSupplierZoneDTL>();
		List<Long> existingStates = new ArrayList<Long>();
		List<Long> existingDistricts = new ArrayList<Long>();
		List<Long> existingDivisions = new ArrayList<Long>();
		List<CmSupplierZoneDTL> cmDtls = new ArrayList<CmSupplierZoneDTL>(); 
		if(selZone.getCmSupplierZoneDtls() != null) {
			log.info(666);
			cmDtls.addAll(selZone.getCmSupplierZoneDtls()); 
		}
		for(CmSupplierZoneDTL cmDtl : cmDtls){
			log.info(777);
			if(cmDtl.getPmState() != null)
				existingStates.add(cmDtl.getPmState().getStateId());
			if(cmDtl.getPmDistrict() != null)
				existingDistricts.add(cmDtl.getPmDistrict().getDistrictId());
			if(cmDtl.getPmDivision() != null)
				existingDivisions.add(cmDtl.getPmDivision().getDivisionId());
		}

		/*
		 * New zone details added*
		 */
		List<Long> selectedStates = new ArrayList<Long>();
		List<Long> selectedDistricts = new ArrayList<Long>();
		List<Long> selectedDivisions = new ArrayList<Long>();
		for(TreeNode selectedNode : selectedNodes) {
			log.info(888);
			if(selectedNode.getType().equals("state") && selectedNode.getChildCount() == 0) {
				log.info(999);
				CmSupplierZoneStateDistrictDTO stateNodeDto = (CmSupplierZoneStateDistrictDTO) selectedNode.getData();
				if(stateNodeDto.getState() != null){
					log.info(1010);
					selectedStates.add(stateNodeDto.getState().getStateId());
					if(!existingStates.contains(stateNodeDto.getState().getStateId())){
						log.info(1111);
						CmSupplierZoneDTL cmDtl = new CmSupplierZoneDTL();
						cmDtl.setCmSupplierZone(selZone);
						IStateSO stateSO = (IStateSO)BusinessDelegate.getInstance().getServiceObject("pm",IStateSO.class);
						StateReqDTO reqStateDTO = new StateReqDTO();
						reqStateDTO.setStateId(stateNodeDto.getState().getStateId());
						StateResDTO resStateDTO = stateSO.retriveStateByID(reqStateDTO);
						PmState newState = resStateDTO.getPmState();
						cmDtl.setPmState(newState);
						newCmSupplierZoneDTLs.add(cmDtl);
					}
				}
			} else if(selectedNode.getType().equals("division") && selectedNode.getChildCount() == 0){
				log.info(1212);
				CmSupplierZoneStateDistrictDTO stateNodeDto2 = (CmSupplierZoneStateDistrictDTO) selectedNode.getParent().getData();
				CmSupplierZoneDTL cmDtl5 = new CmSupplierZoneDTL();
				cmDtl5.setCmSupplierZone(selZone);
				PmState newState2 = new PmState();
				if(stateNodeDto2.getState() != null){
					log.info(1313);
					selectedStates.add(stateNodeDto2.getState().getStateId());
					if(!existingStates.contains(stateNodeDto2.getState().getStateId())){
						IStateSO stateSO = (IStateSO)BusinessDelegate.getInstance().getServiceObject("pm",IStateSO.class);
						StateReqDTO reqStateDTO = new StateReqDTO();
						reqStateDTO.setStateId(stateNodeDto2.getState().getStateId());
						StateResDTO resStateDTO = stateSO.retriveStateByID(reqStateDTO);
						newState2 = resStateDTO.getPmState();
					}
				}
				CmSupplierZoneStateDistrictDTO divisionNodeDto2 = (CmSupplierZoneStateDistrictDTO) selectedNode.getData();
				if(divisionNodeDto2.getDivision() != null){
					log.info(1414);
					selectedDivisions.add(divisionNodeDto2.getDivision().getDivisionId());
					if(!existingDivisions.contains(divisionNodeDto2.getDivision().getDivisionId())){
						log.info(1515);
						CmSupplierZoneDTL cmDtl6 = new CmSupplierZoneDTL();
						cmDtl6.setCmSupplierZone(selZone);
						cmDtl6.setPmState(newState2);
						IDivisionSO divisionSO = (IDivisionSO)BusinessDelegate.getInstance().getServiceObject("pm",IDivisionSO.class);
						DivisionReqDTO reqDivisionDTO = new DivisionReqDTO();
						reqDivisionDTO.setDivisionId(divisionNodeDto2.getDivision().getDivisionId());
						DivisionResDTO resDivisionDTO = divisionSO.retrieveDivisionDtlById(reqDivisionDTO);
						PmDivision newDivision2 = resDivisionDTO.getPmDivision();
						if(selectedNode.getChildren() != null){
							log.info(1616);
							for(TreeNode dCh : selectedNode.getChildren()){
								if(dCh.getType().equals("district")){
									log.info(1717);
									CmSupplierZoneStateDistrictDTO districtNodeDto3 = (CmSupplierZoneStateDistrictDTO) dCh.getData();
									if(districtNodeDto3.getDistrict() != null){
										log.info(1818);
										selectedDistricts.add(districtNodeDto3.getDistrict().getDistrictId());
										if(!existingDistricts.contains(districtNodeDto3.getDistrict().getDistrictId())){
											log.info(1919);
											CmSupplierZoneDTL cmDtl7 = new CmSupplierZoneDTL();
											cmDtl7.setCmSupplierZone(selZone);
											cmDtl7.setPmState(newState2);
											cmDtl7.setPmDivision(newDivision2);
											IDistrictSO districtSO = (IDistrictSO)BusinessDelegate.getInstance().getServiceObject("pm",IDistrictSO.class);
											DistrictReqDTO reqDistrictDTO = new DistrictReqDTO();
											reqDistrictDTO.setDistrictId(districtNodeDto3.getDistrict().getDistrictId());
											DistrictResDTO resDistrictDTO = districtSO.retriveDistrictDtlByID(reqDistrictDTO);
											PmDistrict newDistrict3 = resDistrictDTO.getPmDistrict();
											cmDtl7.setPmDistrict(newDistrict3);
											newCmSupplierZoneDTLs.add(cmDtl7);
										}
									}
								}
							}
						} else {
							log.info(2020);
							cmDtl6.setPmDivision(newDivision2);
							newCmSupplierZoneDTLs.add(cmDtl6);
						}
					}
				}
			} else if(selectedNode.getType().equals("district")) {
				log.info(2121);
				if(selectedNode.getParent().getType().equals("state")){
					log.info(2222);
					CmSupplierZoneStateDistrictDTO stateNodeDto3 = (CmSupplierZoneStateDistrictDTO) selectedNode.getParent().getData();
					CmSupplierZoneDTL cmDtl8 = new CmSupplierZoneDTL();
					cmDtl8.setCmSupplierZone(selZone);
					PmState newState3 = new PmState();
					if(stateNodeDto3.getState() != null){
						log.info(2323);
						selectedStates.add(stateNodeDto3.getState().getStateId());
						if(!existingStates.contains(stateNodeDto3.getState().getStateId())){
							log.info(2424);
							IStateSO stateSO = (IStateSO)BusinessDelegate.getInstance().getServiceObject("pm",IStateSO.class);
							StateReqDTO reqStateDTO = new StateReqDTO();
							reqStateDTO.setStateId(stateNodeDto3.getState().getStateId());
							StateResDTO resStateDTO = stateSO.retriveStateByID(reqStateDTO);
							newState3 = resStateDTO.getPmState();
						}
					}
					CmSupplierZoneStateDistrictDTO districtNodeDto4 = (CmSupplierZoneStateDistrictDTO) selectedNode.getData();
					if(districtNodeDto4.getDistrict() != null){
						log.info(2525);
						selectedDistricts.add(districtNodeDto4.getDistrict().getDistrictId());
						if(!existingDistricts.contains(districtNodeDto4.getDistrict().getDistrictId())){
							log.info(2626);
							IDistrictSO districtSO = (IDistrictSO)BusinessDelegate.getInstance().getServiceObject("pm",IDistrictSO.class);
							DistrictReqDTO reqDistrictDTO = new DistrictReqDTO();
							reqDistrictDTO.setDistrictId(districtNodeDto4.getDistrict().getDistrictId());
							DistrictResDTO resDistrictDTO = districtSO.retriveDistrictDtlByID(reqDistrictDTO);
							PmDistrict newDistrict4 = resDistrictDTO.getPmDistrict();
							if(newState3.getStateId() == null){
								log.info(2727);
								newState3 = newDistrict4.getPmState();
							}
							CmSupplierZoneDTL cmDtl9 = new CmSupplierZoneDTL();
							cmDtl9.setCmSupplierZone(selZone);
							cmDtl9.setPmState(newState3);
							cmDtl9.setPmDistrict(newDistrict4);
							newCmSupplierZoneDTLs.add(cmDtl9);
						}
					} else {
						log.info(2828);
						cmDtl8.setPmState(newState3);
						newCmSupplierZoneDTLs.add(cmDtl8);
					}
				} else if(selectedNode.getParent().getType().equals("division")){
					log.info(2929);
					CmSupplierZoneStateDistrictDTO stateNodeDto4 = (CmSupplierZoneStateDistrictDTO) selectedNode.getParent().getParent().getData();
					CmSupplierZoneDTL cmDtl10 = new CmSupplierZoneDTL();
					cmDtl10.setCmSupplierZone(selZone);
					PmState newState4 = new PmState();
					if(stateNodeDto4.getState() != null){
						log.info(3030);
						selectedStates.add(stateNodeDto4.getState().getStateId());
						if(!existingStates.contains(stateNodeDto4.getState().getStateId())){
							log.info(3131);
							IStateSO stateSO = (IStateSO)BusinessDelegate.getInstance().getServiceObject("pm",IStateSO.class);
							StateReqDTO reqStateDTO = new StateReqDTO();
							reqStateDTO.setStateId(stateNodeDto4.getState().getStateId());
							StateResDTO resStateDTO = stateSO.retriveStateDetailsByID(reqStateDTO);
							newState4 = resStateDTO.getPmState();
							log.info("*******State4:" + newState4.getStateName());
						}
					}
					CmSupplierZoneStateDistrictDTO divisionNodeDto3 = (CmSupplierZoneStateDistrictDTO) selectedNode.getParent().getData();
					PmDivision newDivision3 = new PmDivision();
					if(divisionNodeDto3.getDivision() != null){
						log.info(3232);
						selectedDivisions.add(divisionNodeDto3.getDivision().getDivisionId());
						if(!existingDivisions.contains(divisionNodeDto3.getDivision().getDivisionId())){
							log.info(3333);
							IDivisionSO divisionSO = (IDivisionSO)BusinessDelegate.getInstance().getServiceObject("pm",IDivisionSO.class);
							DivisionReqDTO reqDivisionDTO = new DivisionReqDTO();
							reqDivisionDTO.setDivisionId(divisionNodeDto3.getDivision().getDivisionId());
							DivisionResDTO resDivisionDTO = divisionSO.retrieveDivisionDtlById(reqDivisionDTO);
							newDivision3 = resDivisionDTO.getPmDivision();
						}
					}
					CmSupplierZoneStateDistrictDTO districtNodeDto5 = (CmSupplierZoneStateDistrictDTO) selectedNode.getData();
					if(districtNodeDto5.getDistrict() != null){
						log.info(3434);
						selectedDistricts.add(districtNodeDto5.getDistrict().getDistrictId());
						if(!existingDistricts.contains(districtNodeDto5.getDistrict().getDistrictId())){
							log.info(3535);
							IDistrictSO districtSO = (IDistrictSO)BusinessDelegate.getInstance().getServiceObject("pm",IDistrictSO.class);
							DistrictReqDTO reqDistrictDTO = new DistrictReqDTO();
							reqDistrictDTO.setDistrictId(districtNodeDto5.getDistrict().getDistrictId());
							DistrictResDTO resDistrictDTO = districtSO.retriveDistrictDtlByID(reqDistrictDTO);
							PmDistrict newDistrict5 = resDistrictDTO.getPmDistrict();
							if(newState4.getStateId() == null){
								log.info(3636);
								newState4 = newDistrict5.getPmState();
							}
							CmSupplierZoneDTL cmDtl11 = new CmSupplierZoneDTL();
							cmDtl11.setCmSupplierZone(selZone);
							cmDtl11.setPmState(newState4);
							cmDtl11.setPmDivision(newDivision3);
							cmDtl11.setPmDistrict(newDistrict5);
							newCmSupplierZoneDTLs.add(cmDtl11);
						}
					} else {
						log.info(3737);
						cmDtl10.setPmDivision(newDivision3);
						cmDtl10.setPmState(newState4);
						newCmSupplierZoneDTLs.add(cmDtl10);
					}
				}
			}
		}

		/*
		 * Existing unselected details removed*
		 */
		List<Long> unSelectedStates = new ArrayList<Long>();
		List<Long> unSelectedDistricts = new ArrayList<Long>();
		List<Long> unSelectedDivisions = new ArrayList<Long>();
		/*
		 * Add new details to the current list of details and then remove unselected details
		 */		
		if(selZone.getCmSupplierZoneDtls() != null){
			log.info(3838);
			log.info("Count Before add" + selZone.getCmSupplierZoneDtls().size());
			selZone.getCmSupplierZoneDtls().addAll(newCmSupplierZoneDTLs);
		} else {
			log.info(3939);
			selZone.setCmSupplierZoneDtls(newCmSupplierZoneDTLs);
		}
		for(Long st : existingStates){
			if(!selectedStates.contains(st)){
				unSelectedStates.add(st);
			}
		}
		for(Long dt : existingDistricts){
			if(!selectedDistricts.contains(dt)){
				unSelectedDistricts.add(dt);
			}
		}
		for(Long dv : existingDivisions){
			if(!selectedDivisions.contains(dv)){
				unSelectedDivisions.add(dv);
			}
		}
		log.info("Size sel lists:" + selectedStates.size() + ":" + selectedDistricts.size());
		if(unSelectedStates.size() > 0 || unSelectedDistricts.size() > 0 || unSelectedDivisions.size() > 0){
			log.info(4040);
			log.info("Size rem lists:" + existingStates.size() + ":" + existingDistricts.size());
			if(selZone.getCmSupplierZoneDtls() != null){
				log.info(4141);
				Iterator<CmSupplierZoneDTL> ite = selZone.getCmSupplierZoneDtls().iterator();
				while(ite.hasNext()){
					CmSupplierZoneDTL cmDtl = ite.next();
					if(unSelectedStates.size() > 0 && unSelectedStates.contains(cmDtl.getPmState().getStateId())){	
						ite.remove();
						log.info("Removed 1");
					} else if(unSelectedDistricts.size() > 0 && (cmDtl.getPmDistrict() != null) && unSelectedDistricts.contains(cmDtl.getPmDistrict().getDistrictId())){
						ite.remove();
						log.info("Removed 2");
					} else if(unSelectedDivisions.size() > 0 && (cmDtl.getPmDivision() != null) && unSelectedDivisions.contains(cmDtl.getPmDivision().getDivisionId())){
						ite.remove();
						log.info("Removed 3");
					}
				}	
			}
		}
		log.info("Count After" + selZone.getCmSupplierZoneDtls().size());
		if(!getManagedBean().getCmSupplierZoneList().contains(selZone))
			getManagedBean().getCmSupplierZoneList().add(selZone);
		getManagedBean().setSelectedSupplierZone(null);
		getManagedBean().setZoneTrees(CmSupplierZoneSingleton.getInstance().buildZoneSDTree(this.getManagedBean().getCmSupplierZoneList()));
	}

	/**
	 * Assign val reqd to true.
	 */
	private void assignValReqdToTrue() {
		getManagedBean().setShowParentMsg(false);
		getManagedBean().setValueReqd(true);
	}

	/**
	 * Assign val reqd to false.
	 */
	private void assignValReqdToFalse() {
		getManagedBean().setShowParentMsg(true);
		getManagedBean().setValueReqd(false);
	}
	
	public void deleteSelectedZones() {
		List<CmSupplierZone> deleteList = new ArrayList<CmSupplierZone>();
		for(TreeNode tree: this.getManagedBean().getZoneTrees()){
			CmSupplierZoneStateDistrictDTO zoneDTO =  (CmSupplierZoneStateDistrictDTO)tree.getChildren().get(0).getData();
			CmSupplierZone zone = zoneDTO.getCmSupplierZone();
			if(zone.isSelected()){
				if(zone.getCmPricings() != null && zone.getCmPricings().size() > 0){
					throw new EpException(MessageConst.ERROR_716005);
				}
				deleteList.add(zone);
			}
		}
			this.getManagedBean().setTempDeletedSupplierZoneList(deleteList);	
			RequestContext.getCurrentInstance().execute("deleteConfirmation.show();");
	}
	
	public boolean  isDeleteListEmpty(){
		boolean check = true;
		for(TreeNode tree: this.getManagedBean().getZoneTrees()){
			CmSupplierZoneStateDistrictDTO zoneDTO =  (CmSupplierZoneStateDistrictDTO)tree.getChildren().get(0).getData();
			CmSupplierZone zone = zoneDTO.getCmSupplierZone();
			if(zone.isSelected()){
				check = false;
				break;
			}
		}
		return check;
	}
	
	public CmSupplierZone getCurrentZone(TreeNode node){
		CmSupplierZoneStateDistrictDTO zoneDTO =  (CmSupplierZoneStateDistrictDTO)node.getChildren().get(0).getData();
		return zoneDTO.getCmSupplierZone();
	}
	public List<Long> getListOfDisableDistrict(List<Long> currentlySelectedIds){
		List<Long> disabledDistrictList = new ArrayList<Long>();
		for(CmSupplierZone zone: this.getManagedBean().getCmSupplierZoneList()){
			for(CmSupplierZoneDTL zoneDtl : zone.getCmSupplierZoneDtls()){
				if(zoneDtl.getPmDistrict() != null){
					Long id = zoneDtl.getPmDistrict().getDistrictId();
					if(!currentlySelectedIds.contains(id)){
						disabledDistrictList.add(id);
					}
				}
			}
		}
		return disabledDistrictList;
	}
	public void disableSelectedDistrict(List<PmDistrict> currentlySelectedDistricts){
		List <Long> currentlySelectedId = new ArrayList<Long>();
		if(currentlySelectedDistricts!=null && currentlySelectedDistricts.size()>0){
			for(PmDistrict dist: currentlySelectedDistricts){
				currentlySelectedId.add(dist.getDistrictId());
			}
		}
		this.getManagedBean().setDisabledDistrictId(this.getListOfDisableDistrict(currentlySelectedId));
	
		for(TreeNode node: this.getManagedBean().getRootZoneTree().getChildren()){ //state
			int count = 0;
			int size = 0;
			for(TreeNode disOrDiv: node.getChildren()){// division or district
				if(disOrDiv.getType().equalsIgnoreCase("division")){
					size = disOrDiv.getChildren().size();
					for(TreeNode dis: disOrDiv.getChildren()){
						CmSupplierZoneStateDistrictDTO dto = (CmSupplierZoneStateDistrictDTO)dis.getData();
						dto.getDistrict().setSelectable(true);
						if(this.getManagedBean().getDisabledDistrictId().contains(dto.getDistrict().getDistrictId())){
							dto.getDistrict().setSelectable(false);
							dis.setSelectable(false);
							count++;
						}					
					}
					CmSupplierZoneStateDistrictDTO dto = (CmSupplierZoneStateDistrictDTO)disOrDiv.getData();
					dto.getDivision().setSelectable(true);
					if(count>=size){
						dto.getDivision().setSelectable(false);
						disOrDiv.setSelectable(false);
					}
				}else{
					size = node.getChildren().size();
					CmSupplierZoneStateDistrictDTO dto = (CmSupplierZoneStateDistrictDTO)disOrDiv.getData();
					dto.getDistrict().setSelectable(true);
					if(this.getManagedBean().getDisabledDistrictId().contains(dto.getDistrict().getDistrictId())){
						dto.getDistrict().setSelectable(false);
						disOrDiv.setSelectable(false);
						count++;
					}
				}
			}
			CmSupplierZoneStateDistrictDTO dto = (CmSupplierZoneStateDistrictDTO)node.getData();
			dto.getState().setSelectable(true);
			if(count>=size){
				dto.getState().setSelectable(false);
				node.setSelectable(false);
			}
		}
		RequestContext.getCurrentInstance().update(":form:treeZoneLocationPopup");
	}
	
	//[#26282]
	public void checkForDisciplinaryAction() {
		
		Long supplierId = null;
		
		if(getManagedBean().getCurrentSupplier() != null 
				&& getManagedBean().getCurrentSupplier().getSupplierId() != null) {			
			supplierId = getManagedBean().getCurrentSupplier().getSupplierId();
		}
		
		//check for disciplinary action (DA)	
		List<SmDisciplinaryAction> disciplinaryActList = CmApplicationUtil.checkForDisciplinaryAction(supplierId);
		
		if(disciplinaryActList != null && disciplinaryActList.size() > 0) {
			SmDisciplinaryAction disciplinaryAct = disciplinaryActList.get(0);
			
			log.info("[#26282] - DA - Warning: " + disciplinaryAct.isWarning());
			log.info("[#26282] - DA - Suspend Registration: " + disciplinaryAct.isSuspendRegistration());
			log.info("[#26282] - DA - Cancel Registration: " + disciplinaryAct.isCancelRegistration());
			log.info("[#26282] - DA - Blacklist  Owner/Director of Company: " + disciplinaryAct.isBlacklistOwner());
			
			//disciplinary action - Warning
			if(disciplinaryAct.isWarning()) {
				this.getManagedBean().setDaWarning(disciplinaryAct.isWarning());
			}
			//disciplinary action - Suspend Registration
			if(disciplinaryAct.isSuspendRegistration()) {
				this.getManagedBean().setDaSuspendReg(disciplinaryAct.isSuspendRegistration());
			}
			//disciplinary action - Cancel Registration
			if(disciplinaryAct.isCancelRegistration()) {
				this.getManagedBean().setDaCancelReg(disciplinaryAct.isCancelRegistration());
			}
			//disciplinary action - Blacklist  Owner/Director of Company
			if(disciplinaryAct.isBlacklistOwner()) {
				this.getManagedBean().setDaBlacklistOwner(disciplinaryAct.isBlacklistOwner());
			}
		} else {
			log.info("[#26282] - Disciplinary action does not imposed on supplier");
		}		 
	}
}
