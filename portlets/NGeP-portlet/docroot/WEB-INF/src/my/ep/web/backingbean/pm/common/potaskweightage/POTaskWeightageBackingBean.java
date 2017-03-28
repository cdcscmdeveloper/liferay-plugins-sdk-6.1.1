package my.ep.web.backingbean.pm.common.potaskweightage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmPoRoleTask;
import my.ep.persistence.entity.pm.common.PmPoWeightage;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.type.pm.RoleCode;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.pm.common.potaskweightage.POTaskWeightageManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;

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
		if(activeOrg.size() > 0){
			PmUserOrg userOrg = activeOrg.get(0);
			PmZone pmZone = userOrg.getPmOrgProfile().getPmZone();
			
			getManagedBean().setZoneCode(pmZone.getZoneCode());
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
		
		if(getManagedBean().getSelectedRoleTask() == null){
			getManagedBean().setSelectedRoleTask(new PmPoRoleTask());
		}
		
		//getExistingTaskSet();
		
		return PO_TASK_WEIGHTAGE_DTL;
	}
	
	public void editRoleTask(PmPoRoleTask roleTask){
		//setExistingTask();
		PmPoRoleTask currentRoleTask = (PmPoRoleTask)roleTask.cloneEntity();  
		getManagedBean().setSelectedRoleTask(currentRoleTask);
		getManagedBean().setExistingRoleTask(roleTask);
		
	}
	
	public void addRoleTask(){
		PmPoRoleTask roleTask = new PmPoRoleTask();
		roleTask.setPmUser(getManagedBean().getSelectedUser());
		roleTask.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
		getManagedBean().setSelectedRoleTask(roleTask);
	}
	
/*	public void saveRoleTask(){
		PmUser usr = getManagedBean().getSelectedUser();
		updateUser(usr);
		
		RequestContext.getCurrentInstance().execute("roleTaskDlg.hide();");
		String msg = SmCommonUtil.getOutputMsgBundle(MessageConst.INFO_770127, 
				new String[] {SmCommonUtil.getMessage("lbl.pm.po.task", null)});
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null,msg));
	}*/
	
	public void saveRoleTask(){
		PmUser usr = getManagedBean().getSelectedUser();
		PmPoRoleTask selectedRoleTask = getManagedBean().getSelectedRoleTask();
		//String msg = null;
		String selectedRoleTaskStr = selectedRoleTask.getSupplierType() + selectedRoleTask.getApplType() + selectedRoleTask.getCategoryType();
		if(selectedRoleTask.getPoRoleTaskdId() == null){
			checkDuplicateRoleTask(selectedRoleTaskStr);
			if(usr.getPmPoRoleTasks() == null){
				usr.setPmPoRoleTasks(new ArrayList<PmPoRoleTask>());
			}
			usr.getPmPoRoleTasks().add(getManagedBean().getSelectedRoleTask());
				
/*			updateUser(usr);*/
			addRoleTask();
			clearRoleTask();
			//RequestContext.getCurrentInstance().execute("roleTaskDlg.hide();");
				
			/*msg = SmCommonUtil.getOutputMsgBundle(MessageConst.INFO_770127, 
					new String[] {SmCommonUtil.getMessage("lbl.pm.po.task", null)});*/
		}else{
			if(!compareUpdateRoleTask()){
				checkDuplicateRoleTask(selectedRoleTaskStr);
				
				PmPoRoleTask currentRoleTask = getManagedBean().getExistingRoleTask();
				currentRoleTask.setApplType(selectedRoleTask.getApplType());
				currentRoleTask.setSupplierType(selectedRoleTask.getSupplierType());
				currentRoleTask.setCategoryType(selectedRoleTask.getCategoryType());
				currentRoleTask.setRecordStatusFlg(selectedRoleTask.getRecordStatusFlg());
				
				//updateUser(usr);
			}
			
			//RequestContext.getCurrentInstance().execute("roleTaskDlg.hide();");
/*			msg = SmCommonUtil.getOutputMsgBundle(MessageConst.INFO_770127, 
					new String[] {SmCommonUtil.getMessage("lbl.pm.po.task", null)});*/
		}
		
		RequestContext.getCurrentInstance().execute("roleTaskDlg.hide();");
		
		/*if(msg != null){
			RequestContext.getCurrentInstance().update("roleTaskPnl");
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,msg));
		}*/
	}
	
	
	
	/*public void deleteRoleTask(){
		if(getManagedBean().getSelectedRoleTasks().size() > 0){
			getManagedBean().getSelectedUser().getPmPoRoleTasks().removeAll(getManagedBean().getSelectedRoleTasks());
			updateUser(getManagedBean().getSelectedUser());
		}
	}*/
	
	public void clearRoleTask(){
		PmPoRoleTask roleTask = getManagedBean().getSelectedRoleTask();
		roleTask.setRecordStatusFlg(null);
		roleTask.setApplType(null);
		roleTask.setCategoryType(null);
		roleTask.setApplType(null);
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
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setRoleCode(getManagedBean().getRoleCode());
		reqDTO.setZoneCode(getManagedBean().getZoneCode());
		
		if(getManagedBean().getUserName() != null){
			reqDTO.setUserName(getManagedBean().getUserName());
		}
		
		if(getManagedBean().getIdentificationNo() != null){
			reqDTO.setIdentificationNo(getManagedBean().getIdentificationNo());
		}
		
		//reqDTO.setByWeightage(true);
		/*	reqDTO.setSupplierType("K");
			reqDTO.setApplicationType("A");
			reqDTO.setCategoryType("N");*/
		
		
		IUserSO reqSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		UserResDTO resDTO = reqSO.retrievePOUserListByRoleZone(reqDTO);
		
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
	}
	
	public void updateUser(){
		log.info("<<<<<Update seller>>>>>");
		PmUser selectedUser = getManagedBean().getSelectedUser();
		
		//set to new weightage
		selectedUser.getPmPoWeightage().setNewWeight(selectedUser.getPmPoWeightage().getWeightEnter());
		
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setPmUser(selectedUser);
		IUserSO reqSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		UserResDTO resDTO = reqSO.updateUser(reqDTO);
		
		//getManagedBean().setSelectedUser(resDTO.getPmUser());
		//RequestContext.getCurrentInstance().update("pnlPoRoleTask");
		
		String msg = SmCommonUtil.getOutputMsgBundle(MessageConst.INFO_770127, 
					new String[] {SmCommonUtil.getMessage("lbl.pm.po.task", null)});
		
		FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,msg));

	}
	
	public void resetWeightage(){
		getManagedBean().getSelectedUser().getPmPoWeightage().setWeightEnter(getManagedBean().getSelectedUser().getPmPoWeightage().getNewWeight());
	}
	
	public void supplierTypeOnChange(){
		PmPoRoleTask roleTask = getManagedBean().getSelectedRoleTask();
		roleTask.setApplType(null);
		roleTask.setCategoryType(null);
	}
	
	public void applTypeOnChange(){
		PmPoRoleTask roleTask = getManagedBean().getSelectedRoleTask();
		roleTask.setCategoryType(null);
	}
	
	public List<PmParameterDesc> applTypeDropDown(){
		List<PmParameterDesc> applTypeLs = new ArrayList<PmParameterDesc>();
		
		if(StringUtils.isNotBlank(getManagedBean().getSelectedRoleTask().getSupplierType())){
			for(PmParameterDesc pmDesc : getManagedBean().getApplTypeSelection()){
				String parameterCode = pmDesc.getPmParameter().getParameterCode();
				if(getManagedBean().getSelectedRoleTask().
						getSupplierType().equalsIgnoreCase(SupplierManagementConst.SUPP_TYPE_CONTRACTOR)){
					if(!parameterCode.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.APPL_TYPE_UPDATE) || 
							!parameterCode.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.APPL_TYPE_BUMI)){
						applTypeLs.add(pmDesc);
					}
				}else{
					if(!parameterCode.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.APPL_TYPE_UPDATE) || 
							!parameterCode.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.APPL_TYPE_MOF)){
						applTypeLs.add(pmDesc);
					}
				}
			}
		}
		
		log.info("<<<<<<<<<<applTypeLs>>>>>>>" + applTypeLs.size());
		
		return applTypeLs;

	}
	
	public boolean disabledCategory(){
		PmPoRoleTask roleTask = getManagedBean().getSelectedRoleTask();
		if(StringUtils.isNotBlank(roleTask.getSupplierType()) && 
				StringUtils.isNotBlank(roleTask.getApplType())){
			if(roleTask.getSupplierType().equalsIgnoreCase(SupplierManagementConst.SUPP_TYPE_CONTRACTOR)){
				if(roleTask.getApplType().equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.APPL_TYPE_UPDATE) || 
						roleTask.getApplType().equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.APPL_TYPE_BUMI)){
					return true;
				}
			}else{
				if(roleTask.getApplType().equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.APPL_TYPE_UPDATE) || 
						roleTask.getApplType().equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.APPL_TYPE_MOF)){
					return true;
				}
			}
		}else{
			return true;
		}
		
		return false;
	}
	
	
	/*public void savePOWeightage(){
		if(getManagedBean().getUserLs().size() > 0){
			List<PmUser> userChangeLs = new ArrayList<PmUser>();
			
			for(PmUser user : getManagedBean().getUserLs()){
				if(user.getPmPoWeightage().getNewWeight() != 
						user.getPmPoWeightage().getWeightEnter()){
					userChangeLs.add(user);
				}
			}
			
			log.info("******change List*********" + userChangeLs.size());
			if(userChangeLs.size() > 0){
				UserReqDTO reqDTO = new UserReqDTO();
				reqDTO.setUserLs(userChangeLs);
				IUserSO reqSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				UserResDTO  resDTO = reqSO.updateUserLs(reqDTO);
				
				if(resDTO.isSuccessful()){
					String msg = SmCommonUtil.getOutputMsgBundle(MessageConst.INFO_770127, 
							new String[] {SmCommonUtil.getMessage("lbl.pm.po.task.weightage ", null)});
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, null,msg));
				}
			}
		}
	}*/
	
/*	public void resetPOWeightage(){
		if(getManagedBean().getUserLs().size() > 0){
			for(PmUser user : getManagedBean().getUserLs()){
				user.getPmPoWeightage().setNewWeight(user.getPmPoWeightage().getWeightEnter());
			}
		}
	}*/
	
	private boolean compareUpdateRoleTask(){
		PmPoRoleTask existing = getManagedBean().getExistingRoleTask();
		PmPoRoleTask selected = getManagedBean().getSelectedRoleTask();
		
		if(existing.getPoRoleTaskdId().longValue() == selected.getPoRoleTaskdId().longValue()){
			if(existing.getApplType().equalsIgnoreCase(selected.getApplType()) && 
					existing.getSupplierType().equalsIgnoreCase(selected.getSupplierType())){
				
/*				if(existing.getCategoryType() == null && selected.getCategoryType() == null){
					return true;
				}*/
				
				if(StringUtils.isNotBlank(existing.getCategoryType()) && StringUtils.isNotBlank(selected.getCategoryType())){
					if(existing.getCategoryType().equalsIgnoreCase(selected.getCategoryType())){
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	private Set<String> getExistingTaskSet(){
		Set<String> roleExist = new HashSet<String>();
		if(getManagedBean().getSelectedUser().getPmPoRoleTasks().size() > 0){
			for(PmPoRoleTask roleTask : getManagedBean().getSelectedUser().getPmPoRoleTasks()){
				String roleTaskStr = roleTask.getSupplierType() + roleTask.getApplType() + roleTask.getCategoryType();
				roleExist.add(roleTaskStr);
			}
		
		}
		
		return roleExist;
	}
	
	
	private void checkDuplicateRoleTask(String taskRoleStr){
		Set<String> roleTaskSet = getExistingTaskSet();
		
		if(roleTaskSet.contains(taskRoleStr)){
			String lbl = "lbl.pm.po.task.role.task";
			String desc = SmCommonUtil.getMessage(lbl, null);
			throw new EpException(MessageConst.ERROR_700027, new String[] {desc});
		}
	}
}
