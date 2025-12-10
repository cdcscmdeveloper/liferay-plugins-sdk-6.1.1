package my.ep.web.backingbean.pm.common.potaskweightage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.pm.common.audit.IAuditPMSO;
import my.ep.biz.service.pm.common.poroletask.IPoRoleTaskSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.AuditConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.dto.pm.common.poroletask.PoRoleTaskReqDTO;
import my.ep.dto.pm.common.poroletask.PoRoleTaskResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmPoRoleTask;
import my.ep.persistence.entity.pm.common.PmPoWeightage;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.type.pm.RoleCode;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.mapbeans.AuditPMRoleTaskWeightageBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.pm.common.potaskweightage.POTaskWeightageManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

@ManagedBean(name = "poTaskWeightageBB")
@RequestScoped
public class POTaskWeightageBackingBean extends BaseBackingBean<POTaskWeightageManagedBean>{
	private static final long serialVersionUID = 1L;
	public static Logger log = Logger.getLogger(POTaskWeightageBackingBean.class);
	private static final String PO_TASK_WEIGHTAGE_MAIN = "/xhtml/pm/potaskweightage/poTaskWeightageMain.xhtml";
	private static final String PO_TASK_WEIGHTAGE_DTL = "/xhtml/pm/potaskweightage/poTaskWeightageDtl.xhtml";
	
	/**
	 * PreRender of POTaskWeightage
	 */
	public void preRender(){
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		renewSesionObject();
		
		List<PmUserOrg> activeOrg = PmCommonUtil.retrieveActiveOrg(pmUser.getUserId());
		getManagedBean().setRecordStatus(DbValueConst.RECORD_STATUS_ACTIVE);
		getManagedBean().setPoStatus(DbValueConst.RECORD_STATUS_ACTIVE);
		if(activeOrg.size() > 0){
			PmUserOrg userOrg = activeOrg.get(0);
			PmZone pmZone = userOrg.getPmOrgProfile().getPmZone();
			
			if(pmZone != null){
				if(StringUtils.isNotBlank(pmZone.getZoneCode())){
					getManagedBean().setZoneCode(pmZone.getZoneCode());
					//getManagedBean().setRoleCode(RoleCode.UPS_PO.value());
				}else{
					getManagedBean().setZoneCode(null);
					//getManagedBean().setRoleCode(RoleCode.UPS_PO.value());
				}
			}else{
				getManagedBean().setZoneCode(null);
			}
			
			getManagedBean().setRoleCode(RoleCode.UPS_PO.value());
			
			retrievePOUsers();
		}
		
		prepareApplSelectionList();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, PO_TASK_WEIGHTAGE_MAIN);
	}
	
	public String editUser(PmUser poUser){
		log.info("**********editUser**********");
		getManagedBean().setSelectedUser(poUser);
		getManagedBean().setExistingRoleTaskMap(new HashMap<Long,PmPoRoleTask>());
		
		if(poUser.getPmPoRoleTasks().size() > 0){
			for(PmPoRoleTask roleTask : poUser.getPmPoRoleTasks()){
				PmPoRoleTask currentRoleTask = (PmPoRoleTask)roleTask.cloneEntity(); 
				getManagedBean().getExistingRoleTaskMap().put(currentRoleTask.getPoRoleTaskdId(), currentRoleTask);
			}
		}
		log.info("**********getExistingRoleTaskLs**********" + getManagedBean().getExistingRoleTaskMap().size());
		return PO_TASK_WEIGHTAGE_DTL;
	}
	
	public void deleteRoleTask(PmPoRoleTask roleTask){
		getManagedBean().getSelectedUser().getPmPoRoleTasks().remove(roleTask);
	}
	
	public void validateAddRoleTask(boolean newRoleTask){
		if(getManagedBean().getSelectedUser().getPmPoRoleTasks().size() > 0){
			List<String> rlTaskStrLs = new ArrayList<String>(); 
			for(PmPoRoleTask rt : getManagedBean().getSelectedUser().getPmPoRoleTasks()){
				String str = rt.getSupplierType() + rt.getApplType() + rt.getCategoryType();
				if(rlTaskStrLs.contains(str)){
					String lbl = "lbl.pm.po.task.role.task";
					String desc = SmCommonUtil.getMessage(lbl, null);
					throw new EpException(MessageConst.ERROR_700027, new String[] {desc});
				}else{
					rlTaskStrLs.add(str);
				}
			}
		}
		
		if(newRoleTask){
			PmPoRoleTask roleTask = new PmPoRoleTask();
			roleTask.setPmUser(getManagedBean().getSelectedUser());
			roleTask.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
			getManagedBean().getSelectedUser().getPmPoRoleTasks().add(roleTask);
		}
	}
	
	public String getApplDesc(String applType){
		String label = "lbl.pm.po.task.appltype_" + applType;
		/*Locale locale = new FacesScopes().getUserLocale();
		String desc = ResourceBundleUtils.getMessage(
				label,ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale);*/
		String desc = SmCommonUtil.getMessage(label,null);
		
		return desc;
	}
	
	public String returnToPoUserMain(){
		retrievePOUsers();
		return PO_TASK_WEIGHTAGE_MAIN;
	}
	
	
	
	private void prepareApplSelectionList(){
		List<PmParameterDesc> applSelectionLs = new ArrayList<PmParameterDesc>();
		applSelectionLs = ParameterHelper.getInstance().getParameterDescList(ParameterTypeConst.SUPPLIER_APPLICATION_TYPES);
		if(applSelectionLs.size() > 0){
			List<PmParameterDesc> removeList = new ArrayList<PmParameterDesc>();
			for(PmParameterDesc pDesc : applSelectionLs){
				if(pDesc.getPmParameter().getParameterCode().equalsIgnoreCase(ParameterConst.BASIC_ACCOUNT) || 
						pDesc.getPmParameter().getParameterCode().equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_CANCEL)){
					removeList.add(pDesc);
				}
			}
			
			if(removeList.size() > 0 ){
				applSelectionLs.removeAll(removeList);
			}
		}
		getManagedBean().setApplTypeSelection(applSelectionLs);
		
	}

	public void retrievePOUsers(){
		PoRoleTaskReqDTO reqDTO = new PoRoleTaskReqDTO();
		reqDTO.setRoleCode(getManagedBean().getRoleCode());
		reqDTO.setZoneCode(getManagedBean().getZoneCode());
		
		if(getManagedBean().getUserName() != null){
			reqDTO.setUserName(getManagedBean().getUserName());
		}
		
		if(getManagedBean().getIdentificationNo() != null){
			reqDTO.setIdentificationNo(getManagedBean().getIdentificationNo());
		}
		
		reqDTO.setRecordStatus(getManagedBean().getRecordStatus());
		reqDTO.setPoStatus(getManagedBean().getPoStatus());
		//reqDTO.setByWeightage(true);
		/*	reqDTO.setSupplierType("K");
			reqDTO.setApplicationType("A");
			reqDTO.setCategoryType("N");*/
		
		/*log.info("getManagedBean().getRecordStatus()>>>>>>" + getManagedBean().getRecordStatus());
		log.info("getManagedBean().getPoStatus()>>>>>>" + getManagedBean().getPoStatus());*/
		
		IPoRoleTaskSO reqSO = (IPoRoleTaskSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IPoRoleTaskSO.class);
		PoRoleTaskResDTO resDTO = reqSO.retrievePOUserListByRoleZone(reqDTO);
		
		if (resDTO.isSuccessful()) {
			log.info("POTaskWeightage user size>>>>>" + resDTO.getUserList().size());
			if(resDTO.getUserList().size() > 0){
				for(PmUser user : resDTO.getUserList()){
					if(user.getPmPoWeightage() == null){
						PmPoWeightage weightage = new PmPoWeightage();
						weightage.setPmUser(user);
						user.setPmPoWeightage(weightage);
					}else{
						PmPoWeightage usrWeightage = user.getPmPoWeightage();
						user.getPmPoWeightage().setWeightEnter(usrWeightage.getNewWeight());
					}
				}
			}
			
			getManagedBean().setUserLs(resDTO.getUserList());
		}
	}
	
	public void resetPOTaskWeightageSearch(){
		getManagedBean().setUserName(null);
		getManagedBean().setIdentificationNo(null);
		getManagedBean().setRecordStatus(DbValueConst.RECORD_STATUS_ACTIVE);
		getManagedBean().setPoStatus(DbValueConst.RECORD_STATUS_ACTIVE);
	}
	
	public void updateUser(){
		log.info("<<<<<Update seller>>>>>");
		PmUser selectedUser = getManagedBean().getSelectedUser();
		
		//set to new weightage
		
		validateAddRoleTask(false);
		PoRoleTaskReqDTO reqDTO = new PoRoleTaskReqDTO();
		//contructTrackingDiary(reqDTO);
		
		//selectedUser.getPmPoWeightage().setNewWeight(selectedUser.getPmPoWeightage().getWeightEnter());
		log.info("BB newWeight>>>>" +selectedUser.getPmPoWeightage().getNewWeight());
		log.info("BB getWeightEnter>>>>" +selectedUser.getPmPoWeightage().getWeightEnter());
		reqDTO.setPoUser(selectedUser);
		reqDTO.setRoleCode(PmCommonUtil.getUserRoleValue());
		reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
		reqDTO.setExistingRoleTaskMap(getManagedBean().getExistingRoleTaskMap());
		
		IPoRoleTaskSO reqSO = (IPoRoleTaskSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IPoRoleTaskSO.class);
		
		PoRoleTaskResDTO resDTO = reqSO.updateRoleTask(reqDTO);
		
		/************ AUDIT START #28895-35509 *************/
		AuditPMBackingBeanTemplate auTemplate = new AuditPMRoleTaskWeightageBackingBean();
		IAuditPMSO pmauditSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IAuditPMSO.class);
		if (resDTO.isSuccessful() && pmauditSO.isAuditConfig(AuditConst.PM_AUD_CONF_TASK_WEIGHTAGE)) {
				auTemplate.auditSaveData(getManagedBean(), AuditConst.PM_AUD_TASK_WEIGHTAGE);
		}
		/************ AUDIT END #28895-35509 *************/
		if(resDTO.getUser() != null){
			PmUser user = resDTO.getUser();
			if(resDTO.getUser().getPmPoWeightage() == null){
				PmPoWeightage weightage = new PmPoWeightage();
				weightage.setPmUser(user);
				user.setPmPoWeightage(weightage);
			}else{
				PmPoWeightage usrWeightage = user.getPmPoWeightage();
				user.getPmPoWeightage().setWeightEnter(usrWeightage.getNewWeight());
			}
		}
	
		editUser(resDTO.getUser());
		
		String msg = SmCommonUtil.getOutputMsgBundle(MessageConst.INFO_100002,null);
		
		FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,msg));
		
	}
	
	public void resetWeightage(){
		getManagedBean().getSelectedUser().getPmPoWeightage().setWeightEnter(getManagedBean().getSelectedUser().getPmPoWeightage().getNewWeight());
	}
	
	public void supplierTypeOnChange(PmPoRoleTask roleTask){
		roleTask.setApplType(null);
		roleTask.setCategoryType(null);
	}
	
	public void applTypeOnChange(PmPoRoleTask roleTask){
		roleTask.setCategoryType(null);
	}
	
	public List<PmParameterDesc> applTypeDropDown(PmPoRoleTask roleTask){
		List<PmParameterDesc> applTypeLs = new ArrayList<PmParameterDesc>();
		
		if(StringUtils.isNotBlank(roleTask.getSupplierType())){
			for(PmParameterDesc pmDesc : getManagedBean().getApplTypeSelection()){
				String parameterCode = pmDesc.getPmParameter().getParameterCode();
				if(roleTask.getSupplierType().equalsIgnoreCase(SupplierManagementConst.SUPP_TYPE_CONSULTANT)){
					if(ProfileManagementConst.CONSULTANT_APPL_LIST.contains(parameterCode)){
						applTypeLs.add(pmDesc);
					}
				}else{
					applTypeLs.add(pmDesc);
				}
			}
		}
		
		log.info("<<<<<<<<<<applTypeLs>>>>>>>" + applTypeLs.size());
		
		return applTypeLs;

	}
	
	public boolean disabledCategory(PmPoRoleTask roleTask){
		if(StringUtils.isNotBlank(roleTask.getSupplierType()) && 
				StringUtils.isNotBlank(roleTask.getApplType())){
			if(roleTask.getSupplierType().equalsIgnoreCase(ParameterConst.SUPPLIER_TYPE_CONTRACTOR)){
				if(ProfileManagementConst.CONTRACTOR_EXCLUDE_APPL_LIST.contains(roleTask.getApplType())){
					return true;
				}
			}else{
				return true;
			}
		}else{
			return true;
		}
		
		return false;
	}
}
