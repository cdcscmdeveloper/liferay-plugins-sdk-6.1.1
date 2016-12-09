package my.ep.web.backingbean.pm.common.poworkload;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmPoWorkload;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.pm.common.poworkload.PoWorkloadManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sm.helper.SmRuleHelper;
import my.ep.web.util.sm.helper.SmServiceLookup;

@ManagedBean(name = "poWorkloadBB")
@ViewScoped
public class PoWorkloadBackingBean extends BaseBackingBean<PoWorkloadManagedBean>{
	private static final long serialVersionUID = 1L;
	private static final String PO_WORK_LOAD_MAIN_XHTML = "/xhtml/pm/poworkload/poWorkloadMain.xhtml";
	public static Logger log = Logger.getLogger(PoWorkloadBackingBean.class);

	public void preRenderView(){
		retrieveAllPO();
		
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null, PO_WORK_LOAD_MAIN_XHTML);

	}
	
	public void editUser(PmUser user){
		UserReqDTO userReqDTO = new UserReqDTO();
		userReqDTO.setUserId(user.getUserId());
		PmUser usr = SmServiceLookup.getUserSO().retrieveUserByUserId(userReqDTO).getPmUser();
		
		getManagedBean().setSelectedUser(usr);
		
		log.info("**********User************" + user.getUserId());
		log.info("**********SelectedUser************" + getManagedBean().getSelectedUser().getUserId());
		getUserExistingApplType();
	}
	
	public void savePoWorkload(){
		setPoWorkLoad();
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setPmUser(getManagedBean().getSelectedUser());
		SmServiceLookup.getUserSO().updateUser(reqDTO);
		
		FacesContext.getCurrentInstance().addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null,
						MessageUtils.getMessage(MessageConst.INFO_770126,
								new String[] {
										getManagedBean().getSelectedUser().getUserName()},
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));
	}
	
	private void setPoWorkLoad(){
		PmUser selectedUser = getManagedBean().getSelectedUser();
		
		List<String> currApplType = selectedUser.getCurrentPoWorkloadApplTypes();
		List<String> currSuppType = selectedUser.getCurrentPoWorkloadSupplierTypes();
		List<String> currCatType = selectedUser.getCurrentPoWorkloadCategoryTypes();
		
		List<String> selectedApplType = selectedUser.getSelectedPoWorkloadApplTypes();
		List<String> selectedSuppType = selectedUser.getSelectedPoWorkloadSupplierTypes();
		List<String> selectedCatType = selectedUser.getSelectedPoWorkloadCategoryTypes();
		
/*		selectedApplType.removeAll(currApplType);
		currApplType.removeAll(selectedApplType);
		
		selectedSuppType.removeAll(currSuppType);
		currSuppType.removeAll(selectedSuppType);
		
		selectedCatType.removeAll(currCatType);
		currCatType.removeAll(selectedCatType);*/
		
/*		log.info("currApplType>>>>>>>" + currApplType);
		log.info("selectedApplType>>>>>>>" + selectedApplType);
		
		log.info("currSuppType>>>>>>>" + currSuppType);
		log.info("selectedSuppType>>>>>>>" + selectedSuppType);
		
		log.info("currCatType>>>>>>>" + currCatType);
		log.info("selectedCatType>>>>>>>" + selectedCatType);*/
		
/*		log.info("selectedSuppType size>>>>>>>" + selectedSuppType.size());
		log.info("selectedCatType size>>>>>>>" + selectedCatType.size());*/
		//List<PmPoWorkload> workLoadToBeRemoved = new ArrayList<PmPoWorkload>();
		List<PmPoWorkload> workLoadToBeAdded= new ArrayList<PmPoWorkload>();
		
		

		for(PmPoWorkload workload :getManagedBean().getSelectedUser().getPmPoWorkloads()){
			if(workload.getApplType() != null){
				if(!selectedApplType.contains(workload.getApplType())){
					//workLoadToBeRemoved.add(workload);
					workload.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
				}
			}
			
			if(workload.getSupplierType() != null){
				if(!selectedSuppType.contains(workload.getSupplierType())){
					//workLoadToBeRemoved.add(workload);
					workload.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
				}
			}
			
			if(workload.getCategoryType() != null){
				if(!selectedCatType.contains(workload.getCategoryType())){
					//workLoadToBeRemoved.add(workload);
					workload.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
				}
			}
		}

		if(selectedApplType.size() > 0){
			for(String applType : selectedApplType){
				if(!currApplType.contains(applType)){
					PmPoWorkload poWorkLoad = new PmPoWorkload();
					poWorkLoad.setApplType(applType);
					poWorkLoad.setPmUser(getManagedBean().getSelectedUser());
					workLoadToBeAdded.add(poWorkLoad);
				}
			}
		}
		
		if(selectedSuppType.size() > 0){
			for(String supplierType : selectedSuppType){
				if(!currSuppType.contains(supplierType)){
					PmPoWorkload poWorkLoad = new PmPoWorkload();
					poWorkLoad.setSupplierType(supplierType);
					poWorkLoad.setPmUser(getManagedBean().getSelectedUser());
					workLoadToBeAdded.add(poWorkLoad);
				}
			}
		}
		
		if(selectedCatType.size() > 0){
			for(String categoryType : selectedCatType){
				if(!currCatType.contains(categoryType)){
					PmPoWorkload poWorkLoad = new PmPoWorkload();
					poWorkLoad.setCategoryType(categoryType);
					poWorkLoad.setPmUser(getManagedBean().getSelectedUser());
					workLoadToBeAdded.add(poWorkLoad);
				}
			}
		}
		//log.info("PmPoWorkload be4 removed>>>>>" + getManagedBean().getSelectedUser().getPmPoWorkloads().size());
		//getManagedBean().getSelectedUser().getPmPoWorkloads().removeAll(workLoadToBeRemoved);
		//log.info("PmPoWorkload after removed>>>>>" + getManagedBean().getSelectedUser().getPmPoWorkloads().size());
		
		//log.info("PmPoWorkload to be added >>>>" + workLoadToBeAdded.size());
		getManagedBean().getSelectedUser().getPmPoWorkloads().addAll(workLoadToBeAdded);
		//log.info("PmPoWorkload after added>>>>>" + getManagedBean().getSelectedUser().getPmPoWorkloads().size());
		
	}
	
	private void getUserExistingApplType(){
		PmUser selectedUser = getManagedBean().getSelectedUser();
		
		selectedUser.setSelectedPoWorkloadApplTypes(new ArrayList<String>());
		selectedUser.setSelectedPoWorkloadSupplierTypes(new ArrayList<String>());
		selectedUser.setSelectedPoWorkloadCategoryTypes(new ArrayList<String>());
		
		selectedUser.setCurrentPoWorkloadApplTypes(new ArrayList<String>());
		selectedUser.setCurrentPoWorkloadSupplierTypes(new ArrayList<String>());
		selectedUser.setCurrentPoWorkloadCategoryTypes(new ArrayList<String>());
		
		if(getManagedBean().getSelectedUser().getPmPoWorkloads().size() > 0){

			for(PmPoWorkload poWorkload : getManagedBean().getSelectedUser().getPmPoWorkloads()){
				if(poWorkload.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE){
					if(poWorkload.getApplType() != null){
						selectedUser.getCurrentPoWorkloadApplTypes().add(poWorkload.getApplType());
						selectedUser.getSelectedPoWorkloadApplTypes().add(poWorkload.getApplType());
					}
					
					if(poWorkload.getSupplierType() != null){
						selectedUser.getCurrentPoWorkloadSupplierTypes().add(poWorkload.getSupplierType());
						selectedUser.getSelectedPoWorkloadSupplierTypes().add(poWorkload.getSupplierType());
					}
					
					if(poWorkload.getCategoryType() != null){
						selectedUser.getCurrentPoWorkloadCategoryTypes().add(poWorkload.getCategoryType());
						selectedUser.getSelectedPoWorkloadCategoryTypes().add(poWorkload.getCategoryType());
					}
				}
			}
			
			log.info("Cat Type>>>>>" + selectedUser.getSelectedPoWorkloadCategoryTypes().size());
			log.info("Supp Type>>>>>" + selectedUser.getSelectedPoWorkloadSupplierTypes().size());
			log.info("Appl Type>>>>>" + selectedUser.getSelectedPoWorkloadApplTypes().size());
		}
	}
	
	private void retrieveAllPO(){
		Map<String, List<PmUser>> zoneUserMap = SmServiceLookup.getPoWorkloadSO().retrieveAllPOUser().getZoneUserMap();
		
		getManagedBean().setZoneUserMap(zoneUserMap);
		prepareApplSelectionList();
	}
	
	private void prepareApplSelectionList(){
		List<PmParameterDesc> applSelectionLs = new ArrayList<PmParameterDesc>();
		applSelectionLs = ParameterHelper.getInstance().getParameterDescList(ParameterTypeConst.SUPPLIER_APPLICATION_TYPES);
		
		getManagedBean().setApplTypeSelection(applSelectionLs);
	}
}
