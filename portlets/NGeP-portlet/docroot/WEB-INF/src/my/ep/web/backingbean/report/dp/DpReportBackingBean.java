/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.backingbean.report.dp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;//#29558
import java.util.Iterator;//#29558
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;//#29558

import javax.faces.model.SelectItem;
import javax.naming.NamingException;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.rpt.IPMReportSO;
import my.ep.biz.service.rpt.IRPTRequestDtlSO;
import my.ep.biz.service.rpt.IRPTRequestSO;
import my.ep.constant.DropDownLabelConst;//#29558
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;//#29558
import my.ep.constant.sc.DPStatusConstants;//#29558
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.report.RPTRequestDtlReqDTO;
import my.ep.dto.pm.common.report.RPTRequestReqDTO;
import my.ep.dto.pm.common.report.RPTRequestResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.common.PmStatusDesc;//#29558
import my.ep.util.ResourceBundleUtils;//#29558
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.report.ReportBaseBackingBean;
import my.ep.web.constant.report.DpReportConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.dp.DpReportManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.pm.helper.StatusHelper;//#29558

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

/**
 * The Class DpReportBackingBean.
 *
 * @author AnJie
 * @since 25.04.2017
 */
public abstract class DpReportBackingBean<T extends DpReportManagedBean>
		extends ReportBaseBackingBean<T> {
	
	private static final long serialVersionUID = 1L;
	
	/** The log. */
	private static final Logger log = EpLogger
			.getLogger(DpReportBackingBean.class);

	/**
	 * To generate report according to params that passed in
	 * @param params
	 */
	public void generateDpReport(LinkedHashMap<String, String> params) {
		IRPTRequestSO rptRequestSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IRPTRequestSO.class);
		params.put(DpReportConst.PARAM_KEY_REPORT, getManagedBean()
				.getReportName());
		params.put(DpReportConst.PARAM_REPORT_ID, getManagedBean()
				.getReportCode());
		params.put(DpReportConst.PARAM_USER_ID, getFacesScopes()
				.getUserSessionBean().getUserId().toString());
		
		String userRoleLogin = ReportConst.EMPTY_VAL;
		List<SelectItem> approverNaikTaraf = retrieveApprovarNaikTarafDPS1();
		List<String> authorizationList = retriveUserRoleByUserId();
		if (approverNaikTaraf.size() > 0) {
			userRoleLogin = ReportConst.APPROVERNT;
		} else if (authorizationList.contains(ReportConst.RN_APPROVER)) {
			userRoleLogin = ReportConst.APPROVER;
		} else if (authorizationList.contains(ReportConst.REQUISITIONER)) {
			userRoleLogin = ReportConst.REQUISITIONER;
		}
		//if (!userRoleLogin.equals(ReportConst.REQUISITIONER)) {
			if (StringUtils.isNotEmpty(getManagedBean().getScRequisitioner())) {
				params.put(DpReportConst.PARAM_REQUISITIONER_ID, getManagedBean().getScRequisitioner());
			}
		//}
		//if (StringUtils.isNotEmpty(userRoleLogin)) {
			params.put(DpReportConst.PARAM_USER_ACCESS_ROLE, userRoleLogin);
		//}
		//if(StringUtils.isNotEmpty(getManagedBean().getScRequisitioner())){
			params.put(DpReportConst.PARAM_DESK_OFFICER, getManagedBean().getScRequisitioner());	
		//}
		//if(StringUtils.isNotEmpty(getManagedBean().getScPtj())){
			params.put(DpReportConst.PARAM_PTJ, getManagedBean().getScPtj());	
		//}

		RPTRequestReqDTO reqDTO = new RPTRequestReqDTO();
		reqDTO.setDefaultOutputType(this.getManagedBean()
				.getDefaultOutputType());
		reqDTO.setTempAccess(this.getManagedBean().isTempAccess());
		reqDTO.setParametersMap(params);
		
		try {
			RPTRequestResDTO resDTO = rptRequestSO.generateReport(reqDTO);
			
			//1 = Item Inquiry No. || Simple Quote No.
			//2 = LOA No
			//3 = Purchase In Default No.
			//4 = Request Note No.
			
			//R5 #14076 start	
			RPTRequestDtlReqDTO restDtlDTO = new RPTRequestDtlReqDTO();
			restDtlDTO.getRptRequestDtl().setRequestId(resDTO.getRequestId());
			
			if(params.get(DpReportConst.PARAM_PI_NO) != null){	
				restDtlDTO.getRptRequestDtl().setDocumentNo01(params.get(DpReportConst.PARAM_PI_NO));
			}
			if(params.get(DpReportConst.PARAM_SQ_NO) != null){	
				restDtlDTO.getRptRequestDtl().setDocumentNo01(params.get(DpReportConst.PARAM_SQ_NO));
			}
			if(params.get(DpReportConst.PARAM_LOA_NUM) != null){	
				restDtlDTO.getRptRequestDtl().setDocumentNo02(params.get(DpReportConst.PARAM_LOA_NUM));
			}
			if(params.get(DpReportConst.PARAM_PID_NO) != null){	
				restDtlDTO.getRptRequestDtl().setDocumentNo03(params.get(DpReportConst.PARAM_PID_NO));
			}
			if(params.get(DpReportConst.PARAM_RN_NO) != null){	
				restDtlDTO.getRptRequestDtl().setDocumentNo04(params.get(DpReportConst.PARAM_RN_NO));
			}
			
			if(restDtlDTO.getRptRequestDtl().getDocumentNo01() != null || restDtlDTO.getRptRequestDtl().getDocumentNo02() != null || restDtlDTO.getRptRequestDtl().getDocumentNo03() != null || restDtlDTO.getRptRequestDtl().getDocumentNo04() != null ){
				addRptRequestDetail(restDtlDTO);	
			}
			//R5 #14076 end
			
			getPopUpMsg(String.valueOf(resDTO.getRequestId()));
			RequestContext.getCurrentInstance().execute(
					"rptConfirmationDlg.show()");
		} catch (Exception e) {
			//start - Sonarqube DP Vulnerability Minor Ruzaimah [22/4/2024]
			// TODO Auto-generated catch block
			log.error("Error : " + e.getMessage());
			//stop - Sonarqube DP Vulnerability Minor Ruzaimah [22/4/2024]
		}
	}
	
	//R5 #14076 start
		public void addRptRequestDetail(RPTRequestDtlReqDTO restDtlDTO){
					
			IRPTRequestDtlSO rptRequestDtlSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,IRPTRequestDtlSO.class);
			if(restDtlDTO.getRptRequestDtl().getCreatedBy() == null){
				restDtlDTO.getRptRequestDtl().setCreatedBy(getUserIdSessionFromLoginId()); //#31849 add log key
			}
			rptRequestDtlSO.saveRPTRequestDtlDTO(restDtlDTO);
	}
	//end
	
	//TODO to have in base class
	public FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	//TODO to have in base class
	public void setDefaultDateFromAndDateTo() {
		if (getManagedBean().getDateFrom() == null
				|| getManagedBean().getDateTo() == null) {
			Calendar fromDate = Calendar.getInstance();
			fromDate.set(fromDate.get(Calendar.YEAR), 00, 01, 0, 00, 0);
			getManagedBean().setDateFrom(fromDate.getTime());
			getManagedBean().setDateTo(new Date());
		}
	}
	
	/**
	 * Search status desc.
	 *
	 * @return the list
	 * @throws NamingException the naming exception
	 */
	public SelectItem[] searchStatusDesc(String docType) throws NamingException {
		return super.getStatusDropdownValues(
				ModuleCodeConst.SUPPLIER_SELECTION.toUpperCase(), 
				docType);
	}

	public void onChangeReportName() {
		log.info("onChangeReportName");
		resetSearch();
		
		//26059 - start
		ItemExceedThresholdRptBackingBean itemExceedThresholdRptBackingBean = new ItemExceedThresholdRptBackingBean();
		itemExceedThresholdRptBackingBean.resetSearch();
		//26059 - end
		
		if (StringUtils.isNotBlank(getManagedBean().getReportCode())) {
			retrieveReportValueFromDb();
		}
	}
	
	/**
	 * retrieveApprovarNaikTarafDPS1 - Retrieve approvar naik taraf for dp s1,
	 * based on user id.
	 * 
	 * @return the approverNayakTarafList
	 */
	public List<SelectItem> retrieveApprovarNaikTarafDPS1() {
		log.info("ReportBackingBean-retrieveApprovarNaikTarafDPS1");
		List<SelectItem>	approverNayakTarafList = new ArrayList<SelectItem>();
		Long loginUserId = new FacesScopes().getUserSessionBean().getUserId();
		if (null != loginUserId) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<Object[]> aprnktrfLst = reportSO
					.retrieveApprovarNaikTarafDPS1(loginUserId);
			if (CollectionUtils.isNotEmpty(aprnktrfLst)) {
				for (Object[] object : aprnktrfLst) {
					SelectItem selectItem = new SelectItem();
					selectItem.setLabel(object[0].toString());
					selectItem.setValue(object[1]);
					approverNayakTarafList.add(selectItem);
				}
			}
		}
		return approverNayakTarafList;
	}
	
	/**
	 * retriveUserRoleByUserId - Retreive userRole by user id, based on module
	 * name.
	 * 
	 * @param moduleName
	 *            the module name
	 * @return the authoriztionList
	 */
	public List<String> retriveUserRoleByUserId() {
		log.info("ReportBackingBean-retriveUserRoleByUserId");
		IUserSO userSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		List<String> authoriztionList=new ArrayList<String>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			reqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
			// reqDTO.setUserId((long) 240500);
			reqDTO.setLanguageCode(ReportConst.ENG_LANG);
			UserResDTO userResDTO = userSO
					.retrieveUserRoleDescListByUserId(reqDTO);
			List<PmRoleDesc> roleDesc = userResDTO.getRoleDescList();
			if (CollectionUtils.isNotEmpty(roleDesc)) {
				for (PmRoleDesc pmRoleDesc : roleDesc) {
					pmRoleDesc.getPmRole().getRoleCode();
					if (!authoriztionList.contains(pmRoleDesc.getPmRole()
							.getRoleCode())) {
						authoriztionList.add(pmRoleDesc.getPmRole()
								.getRoleCode());
					}
				}
			}
		}
		return authoriztionList;
	}
	
	/**
	 * PTJ DropDown Show or Hide Based on LoginUser.
	 */
	public boolean retrievePtjRoles(){
		log.info("retrievePtjRoles" + this.getManagedBean().isPtjEnable());
		if(!this.getManagedBean().isPtjEnable()) {
			List<SelectItem> approvalNaikTaraf = retrieveApprovarNaikTarafDPS1();
			if(null != approvalNaikTaraf && approvalNaikTaraf.size() > 0) {
				log.info("applovalListSize" + approvalNaikTaraf.size());
				this.getManagedBean().setPtjEnable(true);
			}
		}
		return this.getManagedBean().isPtjEnable();
	}
	
	/**
	 * Requisitioner DropDown Show or Hide Based on LoginUser.
	 */
	public boolean retrieveUserRoles(){
		log.info("retriveUserRoles" + this.getManagedBean().isRequisitionerEnable());
		if(!this.getManagedBean().isRequisitionerEnable()){
			List<String> userRoles =	retriveUserRoleByUserId();
			List<SelectItem> approverNaikTaraf = retrieveApprovarNaikTarafDPS1();
			if((CollectionUtils.isNotEmpty(approverNaikTaraf)) || 
					((null != userRoles) && userRoles.contains(ReportConst.RN_APPROVER))) {
				this.getManagedBean().setRequisitionerEnable(true);	
				return this.getManagedBean().isRequisitionerEnable();		
			}
		}
		return this.getManagedBean().isRequisitionerEnable();	
	}

	/**
	 * equisitionerList - Retrieve requisitioner, based on user id and
	 * organization profile id.
	 * 
	 * @return the requisitionerList
	 */
	public List<SelectItem> retrieveRequisitioner() {
		log.info("ReportBackingBean-retrieveRequisitioner");
		List<SelectItem> scRequisitionerList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			Long orgProfileId = null;
			/*if (StringUtils.isNotEmpty(this.getScPtj())) {
				orgProfileId = Long.parseLong(this.getScPtj());
			}*/
			List<Object[]> reqLst = reportSO.retrieveRequisitioner(
					new FacesScopes().getUserSessionBean().getUserId(),
					orgProfileId);
			if (CollectionUtils.isNotEmpty(reqLst)) {
				for (Object[] object : reqLst) {
					SelectItem selectItem = new SelectItem();
					selectItem.setLabel(object[0].toString());
					selectItem.setValue(object[1]);
					scRequisitionerList.add(selectItem);
				}
			}
		}
		return scRequisitionerList;
	}
	
	public abstract void resetSearch();

	public abstract LinkedHashMap<String,String> getparams();
	
	public abstract void validateCriteria();
	
	// 29558 - Start (Harun) Change option from single status to multiple 
	/**
	 * reportStatusDropdownValuesSelectManyMenu - Retrieve multiple report status by doc type
	 * name.
	 * 
	 * statusValuesRemove(List) 
	 * for Report Status Need to remove
	 * 
	 * hashStatusid(HashMap)
	 * for Retrieve statusId for pass as report parameter
	 * 
	 * statusValues(List)
	 * for Display statusName or Status Desc at Status option menu
	 * 
	 * @param docType
	 *            the Document Type
	 * @return the statusValues(List) - contain PmStatusDesc.getStatusName()
	 */
	public List<String> reportStatusDropdownValuesSelectManyMenu(String docType) {
		List<String> statusValues = new ArrayList<>();
		List<String> statusValuesRemove = new ArrayList<>();
		List<String> statusRemove = null;
		boolean checkRemove = false;
		Map<String, String> hashStatusid = new HashMap<>();
		
		String[] removeReportDoctype = { ParameterConst.DOCTYPE_LETTER_OF_AWARD };
		Long[] removeStatus = {DPStatusConstants.LOA_QT_CLOSED_BY_AGENCY_DUE_TO_VALIDITY_EXPIRED,
				DPStatusConstants.LOA_QT_CANCELLED_DUE_TO_VALIDITY_EXPIRED_MOF_APPROVAL_REQUIRED,
				DPStatusConstants.LOA_CANCELLED_NOT_AGREE_EXTENSION_VALIDITY};
	

		List<PmStatusDesc> statusDescs = StatusHelper.getInstance()
				.getStatusDescList(ModuleCodeConst.SUPPLIER_SELECTION.toUpperCase(), docType, null);
	
	
		if (CollectionUtils.isNotEmpty(statusDescs)) {
			for (Iterator<PmStatusDesc> it = statusDescs.iterator(); it.hasNext();) {
				PmStatusDesc pmStatusDesc = it.next();
				statusValues.add(String.valueOf(pmStatusDesc.getStatusName()));
				hashStatusid.put(pmStatusDesc.getStatusName(), String.valueOf(pmStatusDesc.getPmStatus().getStatusId()));

				boolean flagAddRemove = addToRemoveStatus(removeStatus,pmStatusDesc);
				if(flagAddRemove){
					statusValuesRemove.add(String.valueOf(pmStatusDesc.getStatusName()));
				}
			}
		}
	
		for (String strDoctypeRpt : removeReportDoctype) {
			if (docType.equalsIgnoreCase(strDoctypeRpt)) {
				statusRemove = new ArrayList<>(statusValues);
				checkRemove = true;
				break;
			} else {
				checkRemove = false;
			}
		}
			
		if (checkRemove) {
			for (Iterator<String> it = statusRemove.iterator(); it.hasNext();) {
				String reportStatus = it.next();
				boolean flagRemove = removeReportStatus(statusValuesRemove, reportStatus);
				if(flagRemove){
					statusValues.remove(reportStatus);
					hashStatusid.remove(reportStatus);
				}
			}
		}
		this.getManagedBean().setHash_scStatusManyMenuId(hashStatusid);

		return statusValues;
	}
	
	/**
	 * onSelectAll - For select /unselect all select option
	 * 
	 * 
	 * @param docType
	 *            the Doc Type
	 * @return the checkAll(boolean)
	 */
	public void onSelectAll(String docType) {
		boolean checkAll = false;
		List<String> statusList = new ArrayList<>(this.getManagedBean()
				.getScStatusManyMenuTemp());
	
		this.getManagedBean().setScStatusManyMenu(new ArrayList<String>());
	
		for (String stsvalue : statusList) {
			if (stsvalue.equals(getStatusDesc(DropDownLabelConst.LBL_RPT_ALL))) {	
				List<String> allList = reportStatusDropdownValuesSelectManyMenu(docType);
	
				List<SelectItem> items = new ArrayList<>();
				for (String strSelectItem : allList) {
					items.add(new SelectItem(strSelectItem));
					this.getManagedBean().getScStatusManyMenuTemp().add(strSelectItem);
				}
					
				if(CollectionUtils.sizeIsEmpty(this.getManagedBean().getScStatusManyMenu())){
					addToScStatusManyMenu(stsvalue);
				}
				checkAll = true;
				break;
			} else {
				checkAll = false;
			}
		}
		
		if (!checkAll) {
	        processRemainingStatusList(statusList);
	    }
	
	}
	
	private boolean addToRemoveStatus(Long[] removeStatus, PmStatusDesc pmStatusDesc) {
		boolean flagRemoveStatus =  false;
		for (Long removeStat : removeStatus) {
			if (removeStat.equals(pmStatusDesc.getPmStatus().getStatusId())) {
				flagRemoveStatus = true;
			}
		}
		return flagRemoveStatus;
	}
	
	
	private boolean removeReportStatus(List<String> statusValuesRemove, String reportStatus) {
		boolean flagRemove =  false;
		for (String strRemoveSts : statusValuesRemove) {
			if (strRemoveSts.equalsIgnoreCase(reportStatus)) {
				flagRemove = true;
			}
		}
		return flagRemove;
	}
	
	
	private void addToScStatusManyMenu(String stsvalue) {
		for (Map.Entry<String, String> setHashMapStatus : this.getManagedBean().getHash_scStatusManyMenuId().entrySet()) {
			if(setHashMapStatus.getKey().equalsIgnoreCase(stsvalue)){
		    	this.getManagedBean().getScStatusManyMenu().add(setHashMapStatus.getValue());
		    }
		}
	}
	
	private void processRemainingStatusList(List<String> statusList) {
		List<SelectItem> items = new ArrayList<>();
		for (String strSelectItem : statusList) {
			items.add(new SelectItem(strSelectItem));
			
	        for (Map.Entry<String, String> setHashMapStatus : this.getManagedBean().getHash_scStatusManyMenuId().entrySet()) {
	            if (setHashMapStatus.getKey().equalsIgnoreCase(strSelectItem)) {
	                this.getManagedBean().getScStatusManyMenu().add(setHashMapStatus.getValue());
	            }
	        }
	    }
	}
	
	
	/**
	 * isDisabled - For enable/disabled select option
	 * 
	 * 
	 * @param item
	 * @param reportCode
	 *            the Item code and Report code
	 * @return the boolean 
	 */
	public boolean isDisabled(String item,String reportCode) {
		List<String> allList = reportStatusDropdownValuesSelectManyMenu(reportCode);
		boolean flagDisabled =  false;
	
		if(this.getManagedBean().getScStatusManyMenuTemp() != null 
				&& this.getManagedBean().getScStatusManyMenuTemp().size() > 0){
			for (String stsvalue : this.getManagedBean().getScStatusManyMenuTemp()) {
				if (stsvalue.equals(getStatusDesc(DropDownLabelConst.LBL_RPT_ALL))) {		
					flagDisabled =  processDisabledItem(allList, item);
				}
			}
		}else{
			return flagDisabled;
		}
		return flagDisabled;
	}
	
	/**
	 * getStatusDesc - Retrieve status description
	 * 
	 * 
	 * @param label
	 *            the label
	 * @return the String 
	 */
	public String getStatusDesc(String label) {
		return ResourceBundleUtils.getMessage(label,
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale());
	}
	
	private boolean processDisabledItem(List<String> allList, String item) {
		for (String strSelectItem : allList) {
			if(strSelectItem.equalsIgnoreCase(item)){
				return strSelectItem.equals(item);
			}
		}
		return false;
	}
	//29558 - End
	
}
