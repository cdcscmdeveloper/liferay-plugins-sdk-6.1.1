/*
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.report.fl;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.faces.model.SelectItem;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.context.RequestContext;

import my.ep.biz.service.pm.authorization.orgprofile.IPtjReorgSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.agoffice.IAgOfficeSO;
import my.ep.biz.service.rpt.IPMReportSO;
import my.ep.biz.service.rpt.IRPTRequestDtlSO;
import my.ep.biz.service.rpt.IRPTRequestSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.authorization.orgprofile.PtjReorgReqDTO;
import my.ep.dto.pm.authorization.orgprofile.PtjReorgResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.agoffice.AgOfficeReqDTO;
import my.ep.dto.pm.common.agoffice.AgOfficeResDTO;
import my.ep.dto.pm.common.report.RPTRequestDtlReqDTO;
import my.ep.dto.pm.common.report.RPTRequestReqDTO;
import my.ep.dto.pm.common.report.RPTRequestResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmPtjReorg;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.report.ReportBaseBackingBean;
import my.ep.web.constant.report.FlReportConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.fl.FlReportManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;
/**
 * Backing Bean for FlReport.
 *
 * @author KK LIM
 * @since 26/4/2017
 */
public class FlReportBackingBean<T extends FlReportManagedBean> extends ReportBaseBackingBean<T>  {

	private static final long serialVersionUID = 1L;

	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(FlReportBackingBean.class);

	
	public FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	/**
	 * retrieve report list by category
	 */
	public void retrieveReportListByCategory(String reportCategory) {
		retrieveReportListByModuleCodeAndSubModule(
				ModuleCodeConst.ORDER_FULFILLMENT.toUpperCase(),
				reportCategory);
	}
	
	/**
	 * On change of report name
	 * 
	 */
	public void onChangeReportName(){

		log.info("onChangeReportName");	
		
		if (StringUtils.isNotBlank(getManagedBean().getReportCode())) {
			retrieveReportValueFromDb();
		}
	}
	
	/**
	 * generate FL report
	*/
	public void generateFlReport(LinkedHashMap<String, String> params){
		IRPTRequestSO rptRequestSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,IRPTRequestSO.class);		
		RPTRequestReqDTO reqDTO = new RPTRequestReqDTO();
		params.put(FlReportConst.PARAM_KEY_REPORT, getManagedBean().getReportName());
		params.put(FlReportConst.PARAM_FL_REPORT_ID_EQ, getManagedBean().getReportCode());
		params.put(FlReportConst.PARAM_USER_ID, getFacesScopes().getUserSessionBean().getUserId().toString());
		reqDTO.setDefaultOutputType(this.getManagedBean().getDefaultOutputType());
		reqDTO.setTempAccess(this.getManagedBean().isTempAccess());
		reqDTO.setParametersMap(params);
		reqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
		RPTRequestResDTO resDTO=null;
		try {
			resDTO = rptRequestSO.generateReport(reqDTO);
			
			//R5 #14076 start
			RPTRequestDtlReqDTO restDtlDTO = new RPTRequestDtlReqDTO();
			restDtlDTO.getRptRequestDtl().setRequestId(resDTO.getRequestId());
			//temporary code for log data
			log.info("28379: url: "+resDTO.getReportUrl());
			
			//1 = Contract No 
			//2 = Contract Request No || Invoice No(Supplier)
			//3 = Purchase Order No ||  Contract Order No
			//4 = Purchase Request No || Delivery Order No || Fulfilment Received Note  No || Stop Delivery Instruction No || Invoice No
			
			if(params.get(FlReportConst.FL_RPT_CONTRACT_NO) != null){	
				restDtlDTO.getRptRequestDtl().setDocumentNo01(params.get(FlReportConst.FL_RPT_CONTRACT_NO));
			}	
			
			if(params.get(FlReportConst.FL_RPT_CONTRACT_REQ_NO) != null){	
				restDtlDTO.getRptRequestDtl().setDocumentNo02(params.get(FlReportConst.FL_RPT_CONTRACT_REQ_NO));
			} 
			if(params.get(FlReportConst.FL_RPT_SUPPL_INV_NO) != null){	
				restDtlDTO.getRptRequestDtl().setDocumentNo02(params.get(FlReportConst.FL_RPT_SUPPL_INV_NO));
			} 
			
			if(params.get(FlReportConst.FL_RPT_PURCHASE_ORDER) != null){	
				restDtlDTO.getRptRequestDtl().setDocumentNo03(params.get(FlReportConst.FL_RPT_PURCHASE_ORDER));
			}
			
			if(params.get(FlReportConst.FL_RPT_COTRACT_ORDER) != null){	
				restDtlDTO.getRptRequestDtl().setDocumentNo03(params.get(FlReportConst.FL_RPT_COTRACT_ORDER));
			}
			
			if(params.get(FlReportConst.FL_RPT_DELI_ORD_NO) != null){
				restDtlDTO.getRptRequestDtl().setDocumentNo04(params.get(FlReportConst.FL_RPT_DELI_ORD_NO));
			}
			if(params.get(FlReportConst.FL_RPT_FRN_NO) != null){
				restDtlDTO.getRptRequestDtl().setDocumentNo04(params.get(FlReportConst.FL_RPT_FRN_NO));
			}
			if(params.get(FlReportConst.FL_RPT_STOP_INSTR_NO) != null){
				restDtlDTO.getRptRequestDtl().setDocumentNo04(params.get(FlReportConst.FL_RPT_STOP_INSTR_NO));
			}		
			if(params.get(FlReportConst.FL_RPT_INV_NO) != null){	
				restDtlDTO.getRptRequestDtl().setDocumentNo04(params.get(FlReportConst.FL_RPT_INV_NO));
			}
			if(params.get(FlReportConst.FL_RPT_PURCHASE_REQUEST) != null){	
				restDtlDTO.getRptRequestDtl().setDocumentNo04(params.get(FlReportConst.FL_RPT_PURCHASE_REQUEST));
			}

			if(restDtlDTO.getRptRequestDtl().getDocumentNo01() != null || restDtlDTO.getRptRequestDtl().getDocumentNo02() != null || restDtlDTO.getRptRequestDtl().getDocumentNo03() != null || restDtlDTO.getRptRequestDtl().getDocumentNo04() != null ){
				addRptRequestDetail(restDtlDTO);	
			}
			//R5 #14076 end
				
		} catch (Exception e) {
			e.printStackTrace();
		}	
		getPopUpMsg(resDTO.getRequestId() != null ? resDTO.getRequestId().toString() : "");
		RequestContext.getCurrentInstance().execute("confirmationId.show()");
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
	/**
	 * reset search criteria values
	*/
	public void resetSearchCriteriaValues() {
		if(getManagedBean().isSupplier() || hasAuditGenUserRole()){
			getManagedBean().setMinistry(null);
			getManagedBean().setPtjGroup(null);
			getManagedBean().setPtj(null);
		}
		 getManagedBean().setSelectedYear(null); 
		 getManagedBean().setContractNo(null); 
		 getManagedBean().setPhysicalContractNo(null); 
		 getManagedBean().setContractRequestNo(null); 
		 getManagedBean().setPanelName(null); 
		 getManagedBean().setLoaNo(null); 
		 getManagedBean().setPhysicalLoaNo(null); 
		 getManagedBean().setPurchaseOrderNo(null); 
		 getManagedBean().setPkpNo(null); 
		 getManagedBean().setGroup(null); 
		 getManagedBean().setRequisitioner(null); 
		 getManagedBean().setApprover(null); 
		 getManagedBean().setReceivingOfficer(null); 
		 getManagedBean().setAcknowledgeOfficer(null); 
		 getManagedBean().setPayOfficer(null); 
		 getManagedBean().setStatusDescId(null); 
		 getManagedBean().setSelectedOrgProfile(null);//supplier name
		
	}
	
	/**
	 * The method used for generating a list of available display type of status
	 * as selection.
	 * 
	 * @return the selection list of display type
	 */
	public SelectItem[] getStatusOptions() {
		return PmCommonUtil.getRecordStatusOptions();
	}
	
	/**
	 * get the Pm zone list
	 * 
	 */
	public SelectItem[] getPmZoneList() {
		return PmCommonUtil.getPmZoneListOptions();
	}
	
	/**
	 * Get Ministry condition for construct the LOV listing
	 * @return String
	 */
	public String getMinistryCondition() {
		String condition = "";
		retrieveParameterCodeType();
		if(hasGovernmentUserRole()) {
			IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IUserSO.class);
			IPtjReorgSO ptjReorgSO = (IPtjReorgSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IPtjReorgSO.class);
			
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
			UserResDTO userResDTO = userSO.retrieveUserOrgListByUserId(userReqDTO);
			PmOrgProfile orgProfile = null;
			if (userResDTO.getUserOrgList() != null	&& userResDTO.getUserOrgList().size() > 0) {
				orgProfile = userResDTO.getUserOrgList().get(0).getPmOrgProfile();
			}
			condition = "orgProfileId^"+retrieveMinistryOrgProfile(orgProfile.getOrgProfileId()).getOrgProfileId();
			
			PtjReorgReqDTO ptjReqDTO = new PtjReorgReqDTO();
			ptjReqDTO.setDeepLevel(true);
			ptjReqDTO.setOrgProfileId(orgProfile.getOrgProfileId());
			PtjReorgResDTO ptjResDTO = ptjReorgSO.retrieveOldPtjByNewPtjId(ptjReqDTO);
			PmOrgProfile ministry = null;
			for(PmPtjReorg pmPtjReorg : ptjResDTO.getPtjReorgList()) {
				ministry = retrieveMinistryOrgProfile(pmPtjReorg.getOldPtjId());
				if(ministry!=null) {
					condition = condition + "*" + "orgProfileId^" + ministry.getOrgProfileId();
				}
			}
		} 
		return condition;
	}
	
	
	/**
	 * Get PTJ group condition for construct the LOV listing
	 * @return String
	 */
	public String constuctConditionFieldsForPTJGroup() {	
		String condition = "";
		if(getManagedBean().getMinistry() != null){
			condition = OrgProfileUtil.getFromToSearchBeanParentString(OrganizationType.DEPARTMENT.value(), OrganizationType.MINISTRY.value())+"^"+
					this.getManagedBean().getMinistry().getOrgProfileId();
		}
		
		if(hasGovernmentUserRole()) {
			IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IUserSO.class);
			IPtjReorgSO ptjReorgSO = (IPtjReorgSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IPtjReorgSO.class);
			
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
			UserResDTO userResDTO = userSO.retrieveUserOrgListByUserId(userReqDTO);
			PmOrgProfile orgProfile = null;
			if (userResDTO.getUserOrgList() != null	&& userResDTO.getUserOrgList().size() > 0) {
				orgProfile = userResDTO.getUserOrgList().get(0).getPmOrgProfile();
			}
			String kumpulanPtjList = retrieveJabatanOrgProfile(orgProfile.getOrgProfileId()).getOrgProfileId()+"";
			PtjReorgReqDTO ptjReqDTO = new PtjReorgReqDTO();
			ptjReqDTO.setDeepLevel(true);
			ptjReqDTO.setOrgProfileId(orgProfile.getOrgProfileId());
			PtjReorgResDTO ptjResDTO = ptjReorgSO.retrieveOldPtjByNewPtjId(ptjReqDTO);
			PmOrgProfile kumpulanPtj = null;
			for(PmPtjReorg pmPtjReorg : ptjResDTO.getPtjReorgList()) {
				kumpulanPtj = retrieveJabatanOrgProfile(pmPtjReorg.getOldPtjId());
				if(kumpulanPtj!=null) {
					kumpulanPtjList = kumpulanPtjList + "," + kumpulanPtj.getOrgProfileId();
				}
			}
			condition = condition + "|orgProfileId^"+kumpulanPtjList+"^9";
		}else if(hasAuditGenUserRole() && condition.trim().length() > 0){
			return condition;
		}
		return condition;
	}
	
	/**
	 * Get PTJ condition for construct the LOV listing
	 * @return String
	 */
	public String getPTJCondition() {
		String condition = "";
		if(getManagedBean().getPtjGroup() != null){
			condition = OrgProfileUtil.getSearchBeanParentString(OrganizationType.DEPARTMENT.value())+
					"^"+this.getManagedBean().getPtjGroup().getOrgProfileId();
		}
		
		if(hasGovernmentUserRole()) {
			IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IUserSO.class);
			IPtjReorgSO ptjReorgSO = (IPtjReorgSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IPtjReorgSO.class);
			
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
			UserResDTO userResDTO = userSO.retrieveUserOrgListByUserId(userReqDTO);
			PmOrgProfile orgProfile = null;
			if (userResDTO.getUserOrgList() != null	&& userResDTO.getUserOrgList().size() > 0) {
				orgProfile = userResDTO.getUserOrgList().get(0).getPmOrgProfile();
			}
			String ptjList = orgProfile.getOrgProfileId()+"";
			PtjReorgReqDTO ptjReqDTO = new PtjReorgReqDTO();
			ptjReqDTO.setDeepLevel(true);
			ptjReqDTO.setOrgProfileId(orgProfile.getOrgProfileId());
			PtjReorgResDTO ptjResDTO = ptjReorgSO.retrieveOldPtjByNewPtjId(ptjReqDTO);
			for(PmPtjReorg pmPtjReorg : ptjResDTO.getPtjReorgList()) {
				ptjList = ptjList + "," + pmPtjReorg.getOldPtjId();
			}
			condition = condition + "|orgProfileId^"+ptjList+"^9";
		}else if(hasAuditGenUserRole() && condition.trim().length() > 0){
			return condition;
		}
		return condition;
	}
	
	/**
	 * The method used reset PTJ/PTJ Group profile value once select the Ministry
	 */
	public void resetPtjGroupProfile() {
		getManagedBean().setPtj(null);
		getManagedBean().setPtjGroup(null);
	}
	
	/**
	 * The method used reset PTJ profile value once select the Ministry
	 */
	public void resetPtjProfile() {
		getManagedBean().setPtj(null);
	}
	
	/**
	 * Check is it login user is Government User
	 * 
	 * @return true, if user is Government User
	 */
	protected boolean hasGovernmentUserRole() {
		if (getFacesScopes().hasRoleUser(RoleCode.REQUISITIONER.value()))
			return true;
		else if (getFacesScopes().hasRoleUser(RoleCode.FL_APPROVER.value()))
			return true;
		else if (getFacesScopes().hasRoleUser(RoleCode.RECEIVING_OFFICER.value()))
			return true;
		else if (getFacesScopes().hasRoleUser(RoleCode.ACKNOWLEDGE_OFFICER.value()))
			return true;		
		else if (getFacesScopes().hasRoleUser(RoleCode.PAY_OFFICER.value()))
			return true;
		else if (getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value()))
			return true;
		else
			return false;		
	}
	
	/**
	 * Check is it login user is Government User
	 * 
	 * @return true, if user is Government User
	 */
	public boolean hasAuditGenUserRole() {
		if (getFacesScopes().hasRoleUser(RoleCode.AUDITOR_GENERAL.value()))
			return true;
		else if (getFacesScopes().hasRoleUser(RoleCode.BPK_AUDITOR.value()))
			return true;
		else
			return false;		
	}
	
	
	/**
	 * Checks if is supplier role.
	 *
	 * @return true, if is supplier role
	 */
	public boolean hasSupplierRole(){
		if(getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())) {
			return Boolean.TRUE;
		} else if(getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())) {
			return Boolean.TRUE;
		} else if(getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())) {
			return Boolean.TRUE;
		} else if(getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value())) {
			return Boolean.TRUE;
		} else if(getFacesScopes().hasRoleUser(RoleCode.FL_USER.value())) {
			return Boolean.TRUE;
		} else if(getFacesScopes().hasRoleUser(RoleCode.GOVT_SELLER.value())) {
			return Boolean.TRUE;
		} else if(getFacesScopes().hasRoleUser(RoleCode.G2G_SUPPLIER_ADMIN.value())) {
			return Boolean.TRUE;
		} else if(getFacesScopes().hasRoleUser(RoleCode.G2G_SUPPLIER_USER.value())) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	
	/**
	 * Gets the default ptj and ministry search values based on user role.
	 *
	 */
	public void defaultPtjAndMinistryValues() {
		if (getFacesScopes().hasRoleUser(RoleCode.AUDITOR_GENERAL.value()) || getFacesScopes().hasRoleUser(RoleCode.BPK_AUDITOR.value()) ||
			getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER.value()) || (hasSupplierRole() && !hasGovernmentUserRole())) {
		} else {
			IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IUserSO.class);
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
			UserResDTO userResDTO = userSO.retrieveUserOrgListByUserId(userReqDTO);
			PmOrgProfile orgProfile = null;
			if (userResDTO.getUserOrgList() != null	&& userResDTO.getUserOrgList().size() > 0) {
				orgProfile = userResDTO.getUserOrgList().get(0).getPmOrgProfile();
			}
			if (getFacesScopes().hasRoleUser(RoleCode.REQUISITIONER.value())
					|| getFacesScopes().hasRoleUser(RoleCode.FL_APPROVER.value())
					|| getFacesScopes().hasRoleUser(RoleCode.RECEIVING_OFFICER.value())
					|| getFacesScopes().hasRoleUser(RoleCode.ACKNOWLEDGE_OFFICER.value())
					|| getFacesScopes().hasRoleUser(RoleCode.PAY_OFFICER.value())) {
				this.getManagedBean().setPtj(orgProfile);
				this.getManagedBean().setMinistry(retrieveMinistryOrgProfile(orgProfile.getOrgProfileId()));
				this.getManagedBean().setPtjGroup(retrieveJabatanOrgProfile(orgProfile.getOrgProfileId()));
			} else if (getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value())) {
				this.getManagedBean().setPtj(null);
				this.getManagedBean().setMinistry(orgProfile);
			}
		}
	}
	
	/**
	 * set the default date from and date to
	 *
	 */
	public void setDefaultDateFromAndDateTo() {
		if (getManagedBean().getFromDate() == null
				|| getManagedBean().getToDate() == null) {
			Calendar fromDate = Calendar.getInstance();
			fromDate.set(fromDate.get(Calendar.YEAR), 00, 01, 0, 00, 0);
			getManagedBean().setFromDate(fromDate.getTime());
			getManagedBean().setToDate(new Date());
		}
	}
	
	/**
	  * The method used to disable the ministry Lov
	  * if login user is FL User
	  * or internal auditor.
	  * 
	  * @return true, if login user is FL User/internal auditor
	  *         false, if login user is not User/internal auditor
	*/	
	public boolean disableMinistryLov() {
		return (hasGovernmentUserRole()	|| getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value()) );
	}
	
	/**
	  * The method used to disable the ptj Lov
	  * if login user is FL User
	  * or ministry is null.
	  * 
	  * @return true, if login user is FL User/ministry is null
	  *         false, if login user is not User/ministry is null
	*/	
	
	public boolean disablePtjLov() {
		return (hasGovernmentUserRole() || getManagedBean().getPtjGroup() == null );
	}
	
	/**
	  * The method used to disable the ptj Lov
	  * if login user is FL User
	  * or ministry is null.
	  * 
	  * @return true, if login user is FL User/ministry is null
	  *         false, if login user is not User/ministry is null
	*/	
	
	public boolean disablePtjGroupLov() {
		return (hasGovernmentUserRole() || getManagedBean().getMinistry() == null );
	}
	
	/**
	 * get the status drop down values
	*/
	public SelectItem[]  getStatusDropDownValues(String docType){
		return getStatusDropdownValues(ModuleCodeConst.ORDER_FULFILLMENT, docType);
	}
	
	/**
	 * get the requisitioner list
	 */
	public List<SelectItem> getRequisitionerList(Long userId,Long ptjId){
		if (null != userId) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,IPMReportSO.class);
			List<String> ptLst = reportSO.retrieveHighRoleCodeByUser(userId);
			if (CollectionUtils.isNotEmpty(ptLst)) {
				for (int i = 0; i < ptLst.size(); i++) {
					getManagedBean().getHighRoleCodeList().add(ptLst.get(i));
				}
			}
			
			if(getManagedBean().getRequisitionerList().isEmpty() || getManagedBean().getRequisitionerList() == null){
				getManagedBean().setRequisitionerList(new ArrayList<SelectItem>());
			}
			getManagedBean().getRequisitionerList().clear();
			if ((CollectionUtils.isNotEmpty(getManagedBean().getHighRoleCodeList()))
					&& (getManagedBean().getHighRoleCodeList().contains(RoleCode.REQUISITIONER.value()))) {
				List<Object[]> rlcdLst = reportSO.retrieveUserListByREQUISITIONER(userId,RoleCode.REQUISITIONER.value());
				if (CollectionUtils.isNotEmpty(rlcdLst)) {
					for (Object[] object : rlcdLst) {
						SelectItem selectItem = new SelectItem();
						selectItem.setLabel(object[0].toString());
						selectItem.setValue(object[1]);
						getManagedBean().getRequisitionerList().add(selectItem);
					}
				}
			}else {
				List<Object[]> rlcdLst = reportSO.retrieveUserListByAdminOrBKPRoleCode(userId,RoleCode.REQUISITIONER.value(),ptjId);
				if (CollectionUtils.isNotEmpty(rlcdLst)) {
					for (Object[] object : rlcdLst) {
						SelectItem selectItem = new SelectItem();
						selectItem.setLabel(object[0].toString());
						selectItem.setValue(object[1]);
						getManagedBean().getRequisitionerList().add(selectItem);
					}
				}
			}
		}
		return getManagedBean().getRequisitionerList();
	}
	
	/**
	 * get the approver list
	 */
	public List<SelectItem> getApproverList(Long userId,Long ptjId){
		if (null != userId) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ReportConst.RPT_MODULE_SER_OBJ, IPMReportSO.class);
			List<String> ptLst = reportSO.retrieveHighRoleCodeByUser(userId);
			if (CollectionUtils.isNotEmpty(ptLst)) {
				for (int i = 0; i < ptLst.size(); i++) {
					getManagedBean().getHighRoleCodeList().add(ptLst.get(i));
				}
			}

			if(getManagedBean().getApproverList().isEmpty() || getManagedBean().getApproverList() == null){
				getManagedBean().setApproverList(new ArrayList<SelectItem>());
			}
			getManagedBean().getApproverList().clear();
			if (CollectionUtils.isNotEmpty(getManagedBean().getHighRoleCodeList())
					&& (getManagedBean().getHighRoleCodeList().contains(RoleCode.REQUISITIONER.value()))) {
				return null;
			}else {
				List<Object[]> rlcdLst = reportSO.retrieveUserListByAdminOrBKPRoleCode(userId,RoleCode.FL_APPROVER.value(), ptjId);
				if (CollectionUtils.isNotEmpty(rlcdLst)) {
					for (Object[] object : rlcdLst) {
						SelectItem selectItem = new SelectItem();
						selectItem.setLabel(object[0].toString());
						selectItem.setValue(object[1]);
						getManagedBean().getApproverList().add(selectItem);
					}
				}
			}
		}
		return getManagedBean().getApproverList();
	}
	
	/**
	 * get the receiving officer list
	 */
	public List<SelectItem> getReceivingOfficerList(Long userId,Long ptjId){
		if (null != userId) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ReportConst.RPT_MODULE_SER_OBJ, IPMReportSO.class);
			List<String> ptLst = reportSO.retrieveHighRoleCodeByUser(userId);
			if (CollectionUtils.isNotEmpty(ptLst)) {
				for (int i = 0; i < ptLst.size(); i++) {
					getManagedBean().getHighRoleCodeList().add(ptLst.get(i));
				}
			}

			if(getManagedBean().getReceivingOfficerList().isEmpty() || getManagedBean().getReceivingOfficerList() == null){
				getManagedBean().setReceivingOfficerList(new ArrayList<SelectItem>());
			}
			getManagedBean().getReceivingOfficerList().clear();
			if (CollectionUtils.isNotEmpty(getManagedBean().getHighRoleCodeList())
					&& (getManagedBean().getHighRoleCodeList().contains(RoleCode.REQUISITIONER.value()))) {
				return null;
			}else {
				List<Object[]> rlcdLst = reportSO.retrieveUserListByAdminOrBKPRoleCode(userId,RoleCode.RECEIVING_OFFICER.value(), ptjId);
				if (CollectionUtils.isNotEmpty(rlcdLst)) {
					for (Object[] object : rlcdLst) {
						SelectItem selectItem = new SelectItem();
						selectItem.setLabel(object[0].toString());
						selectItem.setValue(object[1]);
						getManagedBean().getReceivingOfficerList().add(selectItem);
					}
				}
			}
		}
		return getManagedBean().getReceivingOfficerList();
	} 
	
	/**
	 * get the acknowledge officer list
	 */
	public List<SelectItem> getAcknowledgeOfficerList(Long userId,Long ptjId){
		if (null != userId) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ReportConst.RPT_MODULE_SER_OBJ, IPMReportSO.class);
			List<String> ptLst = reportSO.retrieveHighRoleCodeByUser(userId);
			if (CollectionUtils.isNotEmpty(ptLst)) {
				for (int i = 0; i < ptLst.size(); i++) {
					getManagedBean().getHighRoleCodeList().add(ptLst.get(i));
				}
			}

			if(getManagedBean().getAcknowledgeOfficerList().isEmpty() || getManagedBean().getAcknowledgeOfficerList() == null){
				getManagedBean().setAcknowledgeOfficerList(new ArrayList<SelectItem>());
			}
			getManagedBean().getAcknowledgeOfficerList().clear();
			if (CollectionUtils.isNotEmpty(getManagedBean().getHighRoleCodeList())
					&& (getManagedBean().getHighRoleCodeList().contains(RoleCode.REQUISITIONER.value()))) {
				return null;
			}else {
				List<Object[]> rlcdLst = reportSO.retrieveUserListByAdminOrBKPRoleCode(userId,RoleCode.ACKNOWLEDGE_OFFICER.value(), ptjId);
				if (CollectionUtils.isNotEmpty(rlcdLst)) {
					for (Object[] object : rlcdLst) {
						SelectItem selectItem = new SelectItem();
						selectItem.setLabel(object[0].toString());
						selectItem.setValue(object[1]);
						getManagedBean().getAcknowledgeOfficerList().add(selectItem);
					}
				}
			}
		}
		return getManagedBean().getAcknowledgeOfficerList();
	} 
	
	/**
	 * get the payOfficer list
	 *
	 */
	public List<SelectItem> getPayOfficerList(Long userId,Long ptjId){
		if (null != userId) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ReportConst.RPT_MODULE_SER_OBJ, IPMReportSO.class);
			List<String> ptLst = reportSO.retrieveHighRoleCodeByUser(userId);
			if (CollectionUtils.isNotEmpty(ptLst)) {
				for (int i = 0; i < ptLst.size(); i++) {
					getManagedBean().getHighRoleCodeList().add(ptLst.get(i));
				}
			}

			if(getManagedBean().getPayOfficerList().isEmpty() || getManagedBean().getPayOfficerList() == null){
				getManagedBean().setPayOfficerList(new ArrayList<SelectItem>());
			}
			getManagedBean().getPayOfficerList().clear();
			if (CollectionUtils.isNotEmpty(getManagedBean().getHighRoleCodeList())
					&& (getManagedBean().getHighRoleCodeList().contains(RoleCode.REQUISITIONER.value()))) {
				return null;
			}else {
				List<Object[]> rlcdLst = reportSO.retrieveUserListByAdminOrBKPRoleCode(userId,RoleCode.PAY_OFFICER.value(), ptjId);
				if (CollectionUtils.isNotEmpty(rlcdLst)) {
					for (Object[] object : rlcdLst) {
						SelectItem selectItem = new SelectItem();
						selectItem.setLabel(object[0].toString());
						selectItem.setValue(object[1]);
						getManagedBean().getPayOfficerList().add(selectItem);
					}
				}
			}
		}
		return getManagedBean().getPayOfficerList();
	} 
	
	/**
	 * retrieveParameterCodeType - Retrieve parameter code type.
	 */
	public void retrieveParameterCodeType() {
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			String parameterCodeType = getParameterCodeTypeString();
			if (StringUtils.isNotEmpty(parameterCodeType)
					&& (parameterCodeType.equals(ReportConst.PARAM_CODE_TYPE_FPJ)
							|| parameterCodeType.equals(ReportConst.PARAM_CODE_TYPE_FMI))) {
				getManagedBean().setSupplier(false);
			} else if (StringUtils.isNotEmpty(parameterCodeType)
					&& (parameterCodeType.equals(ReportConst.ROLE_CODE_SUP))) {
				getManagedBean().setSupplier(true);
				retrieveSupplierCompName();
			} 
		}
	}
	
	/**
	 * getParameterCodeTypeString - Gets the parameter code type string.
	 * 
	 * @return the parameterCodeType string
	 */
	public String getParameterCodeTypeString() {
		log.info("ReportBackingBean-getParameterCodeTypeString");
		Long userId = new FacesScopes().getUserSessionBean().getUserId();
		String parameterCodeType = null;
		if (null != userId) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ReportConst.RPT_MODULE_SER_OBJ, IPMReportSO.class);
			List<Object[]> paramcodeLst = reportSO.retrieveParameterCodeByUserid(getFacesScopes().getUserSessionBean().getUserId());
			if (CollectionUtils.isNotEmpty(paramcodeLst)) {
				for (Object[] object : paramcodeLst) {
					parameterCodeType = object[0].toString();
				}
			}
		}
		return parameterCodeType;
	}
	
	/**
	 * retrieveSupplierCompName - Retrieve supplier company name.
	 */
	private void retrieveSupplierCompName() {
		IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ReportConst.RPT_MODULE_SER_OBJ, IPMReportSO.class);
		List<Object[]> supplCompNameLst = reportSO.retrieveSupplierCompName(getFacesScopes().getUserSessionBean().getUserId());
		if (CollectionUtils.isNotEmpty(supplCompNameLst)) {
			for (Object[] object : supplCompNameLst) {
				getManagedBean().setSupplierCompName(object[0].toString());
				getManagedBean().setSupplierId(object[1].toString());
			}
		}
		
	}
	
	// #29554 start
	/**
	 * generate report in excel
	 */
	public void generateFLExcelReport(String reportCode, LinkedHashMap<String, String> params) {
		IRPTRequestSO rptRequestSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ReportConst.RPT_MODULE_SER_OBJ, IRPTRequestSO.class);
		log.info("29554: start generateFLExcelReport");
		params.put(FlReportConst.PARAM_KEY_REPORT, getManagedBean().getReportName());
		params.put(FlReportConst.FL_RPT_REPORT_ID, getManagedBean().getReportCode());
		params.put(FlReportConst.PARAM_USER_ID, getFacesScopes().getUserSessionBean().getUserId().toString());

		RPTRequestReqDTO reqDTO = new RPTRequestReqDTO();
		reqDTO.setDefaultOutputType(this.getManagedBean().getDefaultOutputType());
		reqDTO.setTempAccess(this.getManagedBean().isTempAccess());
		reqDTO.setParametersMap(params);
		reqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());

		RPTRequestResDTO resDTO = new RPTRequestResDTO();
		try {
			log.info("29554: start generateFLExcelReport SO");
			resDTO = rptRequestSO.generateReportInExcel(reqDTO);
		} catch (Exception e) {
			e.printStackTrace();
		}

		switch (reportCode) {
		case "FL_071": // Service fee based on Accounting report
			try {
				log.info("29554: start getServiceFeeBasedOnAccountingReport");
				getServiceFeeBasedOnAccountingReport(params, resDTO.getRequestId());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			break;

		case "FL_072": // Accumulated service fee
			try {
				log.info("29554: start getAccumulatedServiceFee");
				getAccumulatedServiceFee(params, resDTO.getRequestId());
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}

		getPopUpMsg(resDTO.getRequestId() != null ? resDTO.getRequestId().toString() : "");
		RequestContext.getCurrentInstance().execute("confirmationId.show()");
	}

	public void getServiceFeeBasedOnAccountingReport(LinkedHashMap<String, String> params, BigDecimal requestId)
			throws Exception {
		log.info("29554: start getServiceFeeBasedOnAccountingReport");
		IAgOfficeSO agOfficeSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IAgOfficeSO.class);

		List<Object[]> reportList = new ArrayList<Object[]>();
		List<Object[]> summaryReportList = new ArrayList<Object[]>();
		Map<Integer, Object[]> fiList = new TreeMap<Integer, Object[]>();
		Map<Integer, Object[]> summaryFiList = new TreeMap<Integer, Object[]>();
		String[] sheetName = { "Laporan", "Ringkasan" };
		String[] rowHeaderItems = { "Pejabat Perakaunan", "Kadar Fi Perkhidmatan (%)" };
		ArrayList<String> columnHeaderList = new ArrayList<String>(Arrays.asList("Bil", "Tarikh", "No. Dokumen",
				"Kategori Jenis Perolehan", "Kod Kumpulan PTJ", "PTJ Pembayar", "Kadar Fi Perkhidmatan (%)", "Perihal",
				"Nilai Transaksi Termasuk CBP (RM)", "Nilai Potongan (RM)", "CBP (RM)", "Perkhidmatan Fi (RM)",
				"Bayaran Kepada Pembekal (RM)", "Status"));
		ArrayList<String> columnsSummaryList = new ArrayList<String>(Arrays.asList("Bil", "Pejabat Perakaunan",
				"Kadar Fi Perkhidmatan (%)", "Bil. Pesanan Kerajaan", "Nilai Transaksi Termasuk CBP (RM)",
				"Nilai Potongan (RM)", "CBP (RM)", "Fi Perkhidmatan (RM)", "Bayaran Kepada Pembekal (RM)"));

		String reportTitle = "LAPORAN FI PERKHIDMATAN BERDASARKAN PEJABAT PERAKAUNAN BAGI TEMPOH "
				+ params.get(FlReportConst.FL_RPT_TRANSACTION_FROM_DATE) + " HINGGA "
				+ params.get(FlReportConst.FL_RPT_TRANSACTION_TO_DATE) + " SEPERTI PADA "
				+ DateUtils.dateToString("dd/MM/yyyy", new Date()) + " BAGI TAHUN KEWANGAN "
				+ DateUtils.dateToString("yyyy", new Date());

		log.info("29554: start get data");
		reportList = generateServiceFeeBasedOnAccountingReport(params, "R");
		summaryReportList = generateServiceFeeBasedOnAccountingReport(params, "S");
		// sort data
		int fiCount = 0;
		BigDecimal summaryTotalAmt = BigDecimal.ZERO;
		BigDecimal summaryDeductionAmt = BigDecimal.ZERO;
		BigDecimal summaryGSTAmt = BigDecimal.ZERO;
		BigDecimal summaryServiceFeeAmt = BigDecimal.ZERO;
		BigDecimal summaryAmtPayableSupplier = BigDecimal.ZERO;
		if (!reportList.isEmpty()) {
			for (int m = 0; m < reportList.size(); m++) {
				Object[] row = (Object[]) reportList.get(m);
				BigDecimal amtTransIncCBP = row[16] != null ? (BigDecimal) row[16] : BigDecimal.ZERO;
				BigDecimal amtDeduction = row[19] != null ? (BigDecimal) row[19] : BigDecimal.ZERO;
				BigDecimal amtCBP = row[20] != null ? (BigDecimal) row[20] : BigDecimal.ZERO;
				BigDecimal amtServiceFee = row[14] != null ? (BigDecimal) row[14] : BigDecimal.ZERO;
				BigDecimal amtSupplier = row[17] != null ? (BigDecimal) row[17] : BigDecimal.ZERO;
				// summary sheet calculation
				summaryTotalAmt = (summaryTotalAmt).add(amtTransIncCBP);
				summaryDeductionAmt = (summaryDeductionAmt).add(amtDeduction);
				summaryGSTAmt = (summaryGSTAmt).add(amtCBP);
				summaryServiceFeeAmt = (summaryServiceFeeAmt).add(amtServiceFee);
				summaryAmtPayableSupplier = (summaryAmtPayableSupplier).add(amtSupplier);
				// end summary sheet calculation
				fiList.put(fiCount, new Object[] {
						// need to recheck query
						row[6].toString(), // POCO DATE
						row[3].toString(), // POCO NO
						row[8].toString(), // PROCUMENT CATEGORY
						row[10].toString(), // PTJ CODE
						row[9].toString(), // PTJ NAME
						row[15].toString(), // SERVICE FEE
						row[11].toString(), // POCO TITLE
						amtTransIncCBP, // TOTAL AMOUNT
						amtDeduction, // DEDUCTION AMOUNT
						amtCBP, // GST AMOUNT
						amtServiceFee, // SERVICE FEE AMOUNT
						amtSupplier, // AMT PAYABLE SUPPLIER
						row[21].toString() // POCO STATUS
						});
				fiCount++;
			}
		}
		// sort data summary
		BigDecimal accsumTotalAmtWithGST = BigDecimal.ZERO;
		BigDecimal accsumTotalDeduc = BigDecimal.ZERO;
		BigDecimal accsumGST = BigDecimal.ZERO;
		BigDecimal accsumServiceFee = BigDecimal.ZERO;
		BigDecimal accsumPayableToSupplier = BigDecimal.ZERO;
		int accsumpoco = 0;
		if (!summaryReportList.isEmpty()) {
			int summaryfiCount = 0;
			for (int m = 0; m < summaryReportList.size(); m++) {
				Object[] row = (Object[]) summaryReportList.get(m);

				BigDecimal sumTotalAmtWithGST = row[4] != null ? (BigDecimal) row[4] : BigDecimal.ZERO;
				BigDecimal sumTotalDeduc = row[6] != null ? (BigDecimal) row[6] : BigDecimal.ZERO;
				BigDecimal sumGST = row[7] != null ? (BigDecimal) row[7] : BigDecimal.ZERO;
				BigDecimal sumServiceFee = row[3] != null ? (BigDecimal) row[3] : BigDecimal.ZERO;
				BigDecimal sumPayableToSupplier = row[5] != null ? (BigDecimal) row[5] : BigDecimal.ZERO;
				int sumpoco = Integer.parseInt(row[2].toString());

				// summary calculation
				accsumTotalAmtWithGST = (accsumTotalAmtWithGST).add(sumTotalAmtWithGST);
				accsumTotalDeduc = (accsumTotalDeduc).add(sumTotalDeduc);
				accsumGST = (accsumGST).add(sumGST);
				accsumServiceFee = (accsumServiceFee).add(sumServiceFee);
				accsumPayableToSupplier = (accsumPayableToSupplier).add(sumPayableToSupplier);
				accsumpoco += sumpoco;
				// end summary calculation

				summaryFiList.put(summaryfiCount, new Object[] { row[8].toString() + " - " + row[0].toString(), // PEJABAT
																												// PERAKAUNAN
						row[1].toString(), // SERVICE FEE
						sumpoco, // TOTAL POCO
						sumTotalAmtWithGST, // NILAI TRANSAKSI CBP
						sumTotalDeduc, // NILAI POTONGAN
						sumGST, // CBP
						sumServiceFee, // FI PERKHIDMATAN
						sumPayableToSupplier // BAYARAN KEPADA PEMBEKAL
						});
				summaryfiCount++;
			}

		}
		// get accounting office name
		String PmAccHeader = "";

		if (params.get("POFFICECODE") != null) {
			String PmOfficeName = "";
			String PmOfficeCode = params.get("POFFICECODE");
			AgOfficeReqDTO agOfficeReqDTO = new AgOfficeReqDTO();
			agOfficeReqDTO.setAgOfficeCode(params.get("POFFICECODE"));
			AgOfficeResDTO agOfficee = agOfficeSO.retrieveAgOfficeByOfficeCode(agOfficeReqDTO);
			if (agOfficee.getAgOffice() != null) {
				PmOfficeName = agOfficee.getAgOffice().getPmAgValidity().getOfficeName();
			}
			PmAccHeader = PmOfficeCode + " " + PmOfficeName;
		} else {
			PmAccHeader = "All";
		}
		// start create sheet "Laporan"
		Workbook wb = new XSSFWorkbook();
		CreationHelper createHelper = wb.getCreationHelper();
		Sheet sheetReportList = wb.createSheet(sheetName[0]);
		Row titleRowSheetList = sheetReportList.createRow(1);
		Cell cellTitleRow = titleRowSheetList.createCell(1);
		cellTitleRow.setCellValue(reportTitle);
		cellTitleRow.setCellStyle(titleHeaderStyle(wb, true, true, true, true, true, false));
		CellRangeAddress cellZoneTitle = new CellRangeAddress(1, 1, 1, columnHeaderList.size());
		sheetReportList.addMergedRegion(cellZoneTitle);

		boxedRegion(cellTitleRow, cellZoneTitle, sheetReportList);

		// Setting Row Items - Pejabat akaun/FI
		String KadarFi = "";
		if (params.get("PSFONEOFF").equals("N") && params.get("PSFPERIOD").equals("Y")) {
			KadarFi = "0.4";
		} else if (params.get("PSFONEOFF").equals("Y") && params.get("PSFPERIOD").equals("N")) {
			KadarFi = "0.8";
		} else {
			KadarFi = "All";
		}
		// add gap row
		sheetReportList.createRow(sheetReportList.getLastRowNum() + 1);
		// start header report
		for (int i = 1; i <= rowHeaderItems.length; i++) {
			Row itemsDtlRow = sheetReportList.createRow(sheetReportList.getLastRowNum() + 1);
			Cell cellItemsDtl = itemsDtlRow.createCell(1);
			cellItemsDtl.setCellValue(rowHeaderItems[i - 1]);
			cellItemsDtl.setCellStyle(titleHeaderStyle(wb, true, false, false, false, true, true));

			Cell cellItemsVal = itemsDtlRow.createCell(4);
			if (i == 1)
				cellItemsVal.setCellValue(PmAccHeader);
			if (i == 2)
				cellItemsVal.setCellValue(KadarFi);
			cellItemsVal.setCellStyle(titleHeaderStyle(wb, false, false, false, false, true, false));
			sheetReportList.addMergedRegion(new CellRangeAddress(i + 2, i + 2, 1, 3));
			sheetReportList.addMergedRegion(new CellRangeAddress(i + 2, i + 2, 4, columnHeaderList.size()));

			boxedRegion(cellItemsDtl, new CellRangeAddress(i + 2, i + 2, 1, 3), sheetReportList);
			boxedRegion(cellItemsVal, new CellRangeAddress(i + 2, i + 2, 4, columnHeaderList.size()), sheetReportList);
		}

		// start header table
		Row headerRowList = sheetReportList.createRow(sheetReportList.getLastRowNum() + 2);
		for (int i = 0; i < columnHeaderList.size(); i++) {
			Cell cell = headerRowList.createCell(i + 1);
			cell.setCellValue(columnHeaderList.get(i).toString());
			cell.setCellStyle(columnStyle(wb, createHelper, true, true, true, false, false, true, true));
		}
		// Resize all columns
		for (int i = 1; i < columnHeaderList.size() + 1; i++) {
			if (i == 1)
				sheetReportList.setColumnWidth(i, 8 * 256);// Col : Bil
			else if (i == 2)
				sheetReportList.setColumnWidth(i, 17 * 256); // Col : date
			else if (i == 3)
				sheetReportList.setColumnWidth(i, 19 * 256); // Col : poco num
			else if (i == 4)
				sheetReportList.setColumnWidth(i, 20 * 256); // Col : type
			else if (i == 5)
				sheetReportList.setColumnWidth(i, 15 * 256); // Col : ptj code
			else if (i == 6)
				sheetReportList.setColumnWidth(i, 50 * 256); // Col : ptj name
			else if (i == 7)
				sheetReportList.setColumnWidth(i, 15 * 256); // Col : service
																// fee
			else if (i == 8)
				sheetReportList.setColumnWidth(i, 40 * 256); // Col : title
			else if (i == 9)
				sheetReportList.setColumnWidth(i, 30 * 256); // Col : amount
																// with gst
			else if (i == 10)
				sheetReportList.setColumnWidth(i, 15 * 256); // Col : deduction
																// amt
			else if (i == 11)
				sheetReportList.setColumnWidth(i, 15 * 256); // Col : gst amt
			else if (i == 12)
				sheetReportList.setColumnWidth(i, 30 * 256); // Col : service
																// fee amt
			else if (i == 13)
				sheetReportList.setColumnWidth(i, 30 * 256); // Col : supplier
																// payable amt
			else if (i == 14)
				sheetReportList.setColumnWidth(i, 22 * 256); // Col : Status
		}

		// Start : Populate Record
		if (!reportList.isEmpty()) {
			Set<Integer> keyIdFi = fiList.keySet();
			int rowCount = sheetReportList.getLastRowNum() + 1;
			int bilRecord = 1;

			for (Integer key : keyIdFi) {
				Row row = sheetReportList.createRow(rowCount++);
				Object[] objectArr = fiList.get(key);

				int cellid = 2;
				Cell cellBil = row.createCell(1);
				cellBil.setCellValue(bilRecord++);
				cellBil.setCellStyle(columnStyle(wb, createHelper, false, false, true, false, false, false, false));

				for (int x = 0; x < objectArr.length; x++) {
					Cell cell = row.createCell(cellid);
					if (objectArr[x] instanceof Date) {
						cell.setCellValue((Date) objectArr[x]);
						cell.setCellStyle(columnStyle(wb, createHelper, false, false, true, true, false, false, false));
					} else if (objectArr[x] instanceof BigDecimal) {
						cell.setCellValue(((BigDecimal) objectArr[x]).doubleValue());
						cell.setCellStyle(columnStyle(wb, createHelper, false, false, false, false, true, false, false));
					} else {
						if ("null".equalsIgnoreCase((String) objectArr[x]))
							cell.setCellValue("");
						else
							cell.setCellValue((String) objectArr[x]);
						if (x == 4 || x == 6 || x == 12) {
							cell.setCellStyle(columnStyle(wb, createHelper, false, false, false, false, false, true,
									false));
						} else {
							cell.setCellStyle(columnStyle(wb, createHelper, false, false, true, false, false, true,
									false));
						}
					}
					cellid++;
				}
			}
			// summary report sheet
			int rowSummary = sheetReportList.getLastRowNum() + 1;
			Row row = sheetReportList.createRow(rowSummary);

			Cell cellSummary = row.createCell(1);
			cellSummary.setCellValue("Jumlah Keseluruhan (RM)");
			cellSummary.setCellStyle(titleHeaderStyle(wb, true, true, false, false, true, true));
			CellRangeAddress cellZone = new CellRangeAddress(rowSummary, rowSummary, 1, 8);
			sheetReportList.addMergedRegion(cellZone);

			boxedRegion(cellSummary, cellZone, sheetReportList);

			// summary report sheet cell - total amount with gst
			Cell cellSummaryTotalAmt = row.createCell(9);
			cellSummaryTotalAmt.setCellValue((summaryTotalAmt).doubleValue());
			cellSummaryTotalAmt.setCellStyle(columnStyle(wb, createHelper, false, false, false, false, true, false,
					false));
			// summary report sheet cell - deduction amount
			Cell cellSummaryDeductionAmt = row.createCell(10);
			cellSummaryDeductionAmt.setCellValue((summaryDeductionAmt).doubleValue());
			cellSummaryDeductionAmt.setCellStyle(columnStyle(wb, createHelper, false, false, false, false, true, false,
					false));
			// summary report sheet cell - gst amount
			Cell cellSummaryGSTAmt = row.createCell(11);
			cellSummaryGSTAmt.setCellValue((summaryGSTAmt).doubleValue());
			cellSummaryGSTAmt
					.setCellStyle(columnStyle(wb, createHelper, false, false, false, false, true, false, false));
			// summary report sheet cell - service fee amount
			Cell cellSummaryServiceFeeAmt = row.createCell(12);
			cellSummaryServiceFeeAmt.setCellValue((summaryServiceFeeAmt).doubleValue());
			cellSummaryServiceFeeAmt.setCellStyle(columnStyle(wb, createHelper, false, false, false, false, true,
					false, false));
			// summary report sheet cell - accumulate amount payable supplier
			Cell cellSummaryAmtPayableSupplier = row.createCell(13);
			cellSummaryAmtPayableSupplier.setCellValue((summaryAmtPayableSupplier).doubleValue());
			cellSummaryAmtPayableSupplier.setCellStyle(columnStyle(wb, createHelper, false, false, true, false, true,
					false, false));

			CellRangeAddress cellZoneSummaryAmt = new CellRangeAddress(rowSummary, rowSummary, 13, 14);
			sheetReportList.addMergedRegion(cellZoneSummaryAmt);

			boxedRegion(cellSummaryAmtPayableSupplier, cellZoneSummaryAmt, sheetReportList);
		}
		if (reportList.isEmpty()) {
			Row noRecordRow = sheetReportList.createRow(sheetReportList.getLastRowNum() + 1);
			Cell cellNoRecord = noRecordRow.createCell(1);
			cellNoRecord.setCellValue("Rekod tidak dijumpai");
			cellNoRecord.setCellStyle(titleHeaderStyle(wb, false, false, false, false, true, false));
			sheetReportList.addMergedRegion(new CellRangeAddress(sheetReportList.getLastRowNum(), sheetReportList
					.getLastRowNum(), 1, columnHeaderList.size()));
			boxedRegion(cellNoRecord,
					new CellRangeAddress(sheetReportList.getLastRowNum(), sheetReportList.getLastRowNum(), 1,
							columnHeaderList.size()), sheetReportList);
		}

		// create sheet summary report
		Sheet sheetReportSummary = wb.createSheet(sheetName[1]);

		Row titleRowSheetSummary = sheetReportSummary.createRow(1);
		Cell cellTitleRowSummary = titleRowSheetSummary.createCell(1);
		cellTitleRowSummary.setCellValue(reportTitle);
		cellTitleRowSummary.setCellStyle(titleHeaderStyle(wb, true, true, true, true, true, false));
		CellRangeAddress cellZoneTitleSummary = new CellRangeAddress(1, 1, 1, columnsSummaryList.size());
		sheetReportSummary.addMergedRegion(cellZoneTitleSummary);

		boxedRegion(cellTitleRowSummary, cellZoneTitleSummary, sheetReportSummary);

		// add gap row
		sheetReportSummary.createRow(sheetReportSummary.getLastRowNum() + 1);
		// add row ringkasan
		int rowSummaryRingkasan = sheetReportSummary.getLastRowNum() + 1;
		Row summaryRowRingkasan = sheetReportSummary.createRow(rowSummaryRingkasan);
		Cell summaryCellRingkasan = summaryRowRingkasan.createCell(1);
		summaryCellRingkasan.setCellValue("RINGKASAN LAPORAN FI PERKHIDMATAN");
		summaryCellRingkasan.setCellStyle(titleHeaderStyle(wb, true, false, true, true, false, false));
		sheetReportSummary.addMergedRegion(new CellRangeAddress(rowSummaryRingkasan, rowSummaryRingkasan, 1,
				columnsSummaryList.size()));

		// add gap row
		sheetReportSummary.createRow(sheetReportSummary.getLastRowNum() + 1);

		// start header table
		Row headerRowListSummary = sheetReportSummary.createRow(sheetReportSummary.getLastRowNum() + 1);
		for (int i = 0; i < columnsSummaryList.size(); i++) {
			Cell cell = headerRowListSummary.createCell(i + 1);
			cell.setCellValue(columnsSummaryList.get(i).toString());
			cell.setCellStyle(columnStyle(wb, createHelper, true, true, true, false, false, true, true));
		}

		// Resize all columns
		for (int i = 1; i < columnsSummaryList.size() + 1; i++) {
			if (i == 1)
				sheetReportSummary.setColumnWidth(i, 8 * 256); // Col : Bil
			else if (i == 2)
				sheetReportSummary.setColumnWidth(i, 50 * 256); // Col : ptj
																// code/name
			else if (i == 3)
				sheetReportSummary.setColumnWidth(i, 15 * 256); // Col : service
																// fee rate
			else if (i == 4)
				sheetReportSummary.setColumnWidth(i, 15 * 256); // Col : total
																// po/co
			else if (i == 5)
				sheetReportSummary.setColumnWidth(i, 30 * 256); // Col : total
																// amount with
																// gst
			else if (i == 6)
				sheetReportSummary.setColumnWidth(i, 15 * 256); // Col : total
																// deduction amt
			else if (i == 7)
				sheetReportSummary.setColumnWidth(i, 15 * 256); // Col : total
																// gst amt
			else if (i == 8)
				sheetReportSummary.setColumnWidth(i, 30 * 256); // Col : total
																// service fee
																// amt
			else if (i == 9)
				sheetReportSummary.setColumnWidth(i, 30 * 256); // Col : total
																// amt payable
																// supplier
		}
		// start populate record summary
		if (!summaryReportList.isEmpty()) {
			Set<Integer> keyIdFi = summaryFiList.keySet();
			int rowCountSummary = sheetReportSummary.getLastRowNum() + 1;
			int bilRecord = 1;

			for (Integer key : keyIdFi) {
				Row row = sheetReportSummary.createRow(rowCountSummary++);
				Object[] objectArr = summaryFiList.get(key);

				int cellid = 2;
				Cell cellBil = row.createCell(1);
				cellBil.setCellValue(bilRecord++);
				cellBil.setCellStyle(columnStyle(wb, createHelper, false, false, true, false, false, false, false));

				for (int x = 0; x < objectArr.length; x++) {
					Cell cell = row.createCell(cellid);
					if (objectArr[x] instanceof BigDecimal) {
						cell.setCellValue(((BigDecimal) objectArr[x]).doubleValue());
						cell.setCellStyle(columnStyle(wb, createHelper, false, false, false, false, true, false, false));
					} else if (objectArr[x] instanceof Integer) {
						cell.setCellValue(((Integer) objectArr[x]).intValue());
						cell.setCellStyle(columnStyle(wb, createHelper, false, false, true, false, false, false, false));
					} else {
						if ("null".equalsIgnoreCase((String) objectArr[x]))
							cell.setCellValue("");
						else
							cell.setCellValue((String) objectArr[x]);
						cell.setCellStyle(columnStyle(wb, createHelper, false, false, true, false, false, true, false));
					}
					cellid++;
				}
			}

			// summary summary
			int rowSummaryS = sheetReportSummary.getLastRowNum() + 1;
			Row row = sheetReportSummary.createRow(rowSummaryS);

			Cell cellSummary = row.createCell(1);
			cellSummary.setCellValue("Jumlah Keseluruhan (RM)");
			cellSummary.setCellStyle(titleHeaderStyle(wb, true, true, false, false, true, true));
			CellRangeAddress cellZone = new CellRangeAddress(rowSummaryS, rowSummaryS, 1, 3);
			sheetReportSummary.addMergedRegion(cellZone);

			boxedRegion(cellSummary, cellZone, sheetReportSummary);

			// summary report sheet cell - total poco
			Cell cellSummaryTotalPOCO = row.createCell(4);
			cellSummaryTotalPOCO.setCellValue(((Integer) accsumpoco).intValue());
			cellSummaryTotalPOCO.setCellStyle(columnStyle(wb, createHelper, false, false, true, false, false, false,
					false));

			// summary report total amt
			Cell cellSummaryTotalAmt = row.createCell(5);
			cellSummaryTotalAmt.setCellValue((accsumTotalAmtWithGST).doubleValue());
			cellSummaryTotalAmt.setCellStyle(columnStyle(wb, createHelper, false, false, false, false, true, false,
					false));

			// summary report total deduct
			Cell cellSummaryTotalDeduct = row.createCell(6);
			cellSummaryTotalDeduct.setCellValue((accsumTotalDeduc).doubleValue());
			cellSummaryTotalDeduct.setCellStyle(columnStyle(wb, createHelper, false, false, false, false, true, false,
					false));

			// summary report total gst
			Cell cellSummaryTotalGST = row.createCell(7);
			cellSummaryTotalGST.setCellValue((accsumGST).doubleValue());
			cellSummaryTotalGST.setCellStyle(columnStyle(wb, createHelper, false, false, false, false, true, false,
					false));

			// summary report total service fee
			Cell cellSummaryTotalServiceFee = row.createCell(8);
			cellSummaryTotalServiceFee.setCellValue((accsumServiceFee).doubleValue());
			cellSummaryTotalServiceFee.setCellStyle(columnStyle(wb, createHelper, false, false, false, false, true,
					false, false));

			// summary report total pay supplier
			Cell cellSummaryTotalPaySupplier = row.createCell(9);
			cellSummaryTotalPaySupplier.setCellValue((accsumPayableToSupplier).doubleValue());
			cellSummaryTotalPaySupplier.setCellStyle(columnStyle(wb, createHelper, false, false, false, false, true,
					false, false));

		}

		if (summaryReportList.isEmpty()) {
			Row noRecordRow = sheetReportSummary.createRow(sheetReportSummary.getLastRowNum() + 1);
			Cell cellNoRecord = noRecordRow.createCell(1);
			cellNoRecord.setCellValue("Rekod tidak dijumpai");
			cellNoRecord.setCellStyle(titleHeaderStyle(wb, false, false, false, false, true, false));
			sheetReportSummary.addMergedRegion(new CellRangeAddress(sheetReportSummary.getLastRowNum(),
					sheetReportSummary.getLastRowNum(), 1, columnsSummaryList.size()));
			boxedRegion(cellNoRecord,
					new CellRangeAddress(sheetReportSummary.getLastRowNum(), sheetReportSummary.getLastRowNum(), 1,
							columnsSummaryList.size()), sheetReportSummary);
		}

		try {
			String path = EpPropertiesUtils.getInstance().getProperty(
					EpPropertiesConst.DOCUMENT_REPOSITORY_SHARED_REPORT_DIR);
			String fullPath = path + "/" + requestId + ".xlsx";
			// String fullPath = "D://reports//dev//"+requestId+".xlsx";
			// log.info("FULL PATH (CREATE FILE) == "+fullPath);
			FileOutputStream out = new FileOutputStream(new File(fullPath));
			wb.write(out);
			out.close();
			wb.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void getAccumulatedServiceFee(LinkedHashMap<String, String> params, BigDecimal requestId) throws Exception {
		log.info("29554: start getAccumulatedServiceFee");

		List<Object[]> reportList = new ArrayList<Object[]>();
		Map<Integer, Object[]> fiList = new TreeMap<Integer, Object[]>();
		String sheetName = "Laporan";
		ArrayList<String> columnHeaderList = new ArrayList<String>(Arrays.asList("Bil", "Pejabat Perakaunan",
				"Jan(RM)", "Feb(RM)", "Mac(RM)", "April(RM)", "Mei(RM)", "Jun(RM)", "Julai(RM)", "Ogos(RM)",
				"Sept(RM)", "Okt(RM)", "Nov(RM)", "Dis(RM)", "Jumlah(RM)"));

		String reportTitle = "LAPORAN FI PERKHIDMATAN TERKUMPUL BAGI TEMPOH "
				+ params.get(FlReportConst.FL_RPT_TRANSACTION_FROM_DATE) + " HINGGA "
				+ params.get(FlReportConst.FL_RPT_TRANSACTION_TO_DATE) + " SEPERTI PADA "
				+ DateUtils.dateToString("dd/MM/yyyy", new Date()) + " BAGI TAHUN KEWANGAN "
				+ DateUtils.dateToString("yyyy", new Date());

		log.info("29554: start get data");
		// need to change data
		reportList = generateAccumulatedServiceFee(params);
		// sort data
		int fiCount = 0;
		BigDecimal janTotal = BigDecimal.ZERO;
		BigDecimal febTotal = BigDecimal.ZERO;
		BigDecimal macTotal = BigDecimal.ZERO;
		BigDecimal aprTotal = BigDecimal.ZERO;
		BigDecimal mayTotal = BigDecimal.ZERO;
		BigDecimal junTotal = BigDecimal.ZERO;
		BigDecimal julyTotal = BigDecimal.ZERO;
		BigDecimal augTotal = BigDecimal.ZERO;
		BigDecimal sepTotal = BigDecimal.ZERO;
		BigDecimal octTotal = BigDecimal.ZERO;
		BigDecimal novTotal = BigDecimal.ZERO;
		BigDecimal decTotal = BigDecimal.ZERO;
		BigDecimal totalAll = BigDecimal.ZERO;
		if (!reportList.isEmpty()) {

			for (int m = 0; m < reportList.size(); m++) {
				Object[] row = (Object[]) reportList.get(m);

				BigDecimal jan = row[3] != null ? (BigDecimal) row[3] : BigDecimal.ZERO;
				BigDecimal feb = row[4] != null ? (BigDecimal) row[4] : BigDecimal.ZERO;
				BigDecimal mac = row[5] != null ? (BigDecimal) row[5] : BigDecimal.ZERO;
				BigDecimal apr = row[6] != null ? (BigDecimal) row[6] : BigDecimal.ZERO;
				BigDecimal may = row[7] != null ? (BigDecimal) row[7] : BigDecimal.ZERO;
				BigDecimal jun = row[8] != null ? (BigDecimal) row[8] : BigDecimal.ZERO;
				BigDecimal july = row[9] != null ? (BigDecimal) row[9] : BigDecimal.ZERO;
				BigDecimal aug = row[10] != null ? (BigDecimal) row[10] : BigDecimal.ZERO;
				BigDecimal sep = row[11] != null ? (BigDecimal) row[11] : BigDecimal.ZERO;
				BigDecimal oct = row[12] != null ? (BigDecimal) row[12] : BigDecimal.ZERO;
				BigDecimal nov = row[13] != null ? (BigDecimal) row[13] : BigDecimal.ZERO;
				BigDecimal dec = row[14] != null ? (BigDecimal) row[14] : BigDecimal.ZERO;
				BigDecimal totalRow = jan.add(feb).add(mac).add(apr).add(may).add(jun).add(july).add(aug).add(sep)
						.add(oct).add(nov).add(dec);
				// calculate total each month n total all
				janTotal = (janTotal).add(jan);
				febTotal = (febTotal).add(feb);
				macTotal = (macTotal).add(mac);
				aprTotal = (aprTotal).add(apr);
				mayTotal = (mayTotal).add(may);
				junTotal = (junTotal).add(jun);
				julyTotal = (julyTotal).add(july);
				augTotal = (augTotal).add(aug);
				sepTotal = (sepTotal).add(sep);
				octTotal = (octTotal).add(oct);
				novTotal = (novTotal).add(nov);
				decTotal = (decTotal).add(dec);
				totalAll = (totalAll).add(totalRow);
				// end calculate

				fiList.put(fiCount, new Object[] { row[1].toString() + " - " + row[2].toString(),
						(BigDecimal) row[3] != null ? (BigDecimal) row[3] : "-", // jan
						(BigDecimal) row[4] != null ? (BigDecimal) row[4] : "-", // feb
						(BigDecimal) row[5] != null ? (BigDecimal) row[5] : "-", // mar
						(BigDecimal) row[6] != null ? (BigDecimal) row[6] : "-", // apr
						(BigDecimal) row[7] != null ? (BigDecimal) row[7] : "-", // may
						(BigDecimal) row[8] != null ? (BigDecimal) row[8] : "-", // jun
						(BigDecimal) row[9] != null ? (BigDecimal) row[9] : "-", // jul
						(BigDecimal) row[10] != null ? (BigDecimal) row[10] : "-", // aug
						(BigDecimal) row[11] != null ? (BigDecimal) row[11] : "-", // sept
						(BigDecimal) row[12] != null ? (BigDecimal) row[12] : "-", // oct
						(BigDecimal) row[13] != null ? (BigDecimal) row[13] : "-", // nov
						(BigDecimal) row[14] != null ? (BigDecimal) row[14] : "-", // dec
						totalRow.doubleValue() != 0.00 ? totalRow : "-"

				});
				fiCount++;
			}
		}
		// start create sheet "Laporan"
		Workbook wb = new XSSFWorkbook();
		CreationHelper createHelper = wb.getCreationHelper();
		Sheet sheetReportList = wb.createSheet(sheetName);
		Row titleRowSheetList = sheetReportList.createRow(1);
		Cell cellTitleRow = titleRowSheetList.createCell(1);
		cellTitleRow.setCellValue(reportTitle);
		cellTitleRow.setCellStyle(titleHeaderStyle(wb, true, true, true, true, true, false));
		CellRangeAddress cellZoneTitle = new CellRangeAddress(1, 1, 1, columnHeaderList.size());
		sheetReportList.addMergedRegion(cellZoneTitle);

		boxedRegion(cellTitleRow, cellZoneTitle, sheetReportList);

		// start header table
		Row headerRowList = sheetReportList.createRow(sheetReportList.getLastRowNum() + 2);
		for (int i = 0; i < columnHeaderList.size(); i++) {
			Cell cell = headerRowList.createCell(i + 1);
			cell.setCellValue(columnHeaderList.get(i).toString());
			cell.setCellStyle(columnStyle(wb, createHelper, true, true, true, false, false, true, true));
		}
		// Resize all columns
		for (int i = 1; i < columnHeaderList.size() + 1; i++) {
			if (i == 1)
				sheetReportList.setColumnWidth(i, 8 * 256);// Col : Bil
			else if (i == 2)
				sheetReportList.setColumnWidth(i, 50 * 256); // Col : ptj
			else if (i == 3)
				sheetReportList.setColumnWidth(i, 15 * 256); // Col : jan
			else if (i == 4)
				sheetReportList.setColumnWidth(i, 15 * 256); // Col : feb
			else if (i == 5)
				sheetReportList.setColumnWidth(i, 15 * 256); // Col : mac
			else if (i == 6)
				sheetReportList.setColumnWidth(i, 15 * 256); // Col : april
			else if (i == 7)
				sheetReportList.setColumnWidth(i, 15 * 256); // Col : mei
			else if (i == 8)
				sheetReportList.setColumnWidth(i, 15 * 256); // Col : jun
			else if (i == 9)
				sheetReportList.setColumnWidth(i, 15 * 256); // Col : july
			else if (i == 10)
				sheetReportList.setColumnWidth(i, 15 * 256); // Col : aug
			else if (i == 11)
				sheetReportList.setColumnWidth(i, 15 * 256); // Col : sept
			else if (i == 12)
				sheetReportList.setColumnWidth(i, 15 * 256); // Col : oct
			else if (i == 13)
				sheetReportList.setColumnWidth(i, 15 * 256); // Col : nov
			else if (i == 14)
				sheetReportList.setColumnWidth(i, 15 * 256); // Col : dec
			else if (i == 15)
				sheetReportList.setColumnWidth(i, 30 * 256); // Col : jumlah
		}

		// Start : Populate Record
		if (!reportList.isEmpty()) {
			Set<Integer> keyIdFi = fiList.keySet();
			int rowCount = sheetReportList.getLastRowNum() + 1;
			int bilRecord = 1;

			for (Integer key : keyIdFi) {
				Row row = sheetReportList.createRow(rowCount++);
				Object[] objectArr = fiList.get(key);

				int cellid = 2;
				Cell cellBil = row.createCell(1);
				cellBil.setCellValue(bilRecord++);
				cellBil.setCellStyle(columnStyle(wb, createHelper, false, false, true, false, false, false, false));

				for (int x = 0; x < objectArr.length; x++) {
					Cell cell = row.createCell(cellid);
					if (objectArr[x] instanceof Date) {
						cell.setCellValue((Date) objectArr[x]);
						cell.setCellStyle(columnStyle(wb, createHelper, false, false, true, true, false, false, false));
					} else if (objectArr[x] instanceof BigDecimal) {
						cell.setCellValue(((BigDecimal) objectArr[x]).doubleValue());
						cell.setCellStyle(columnStyle(wb, createHelper, false, false, false, false, true, false, false));
					} else {
						if ("null".equalsIgnoreCase((String) objectArr[x]))
							cell.setCellValue("");
						else
							cell.setCellValue((String) objectArr[x]);

						cell.setCellStyle(columnStyle(wb, createHelper, false, false, true, false, false, true, false));

					}
					cellid++;
				}
			}
			// summary report sheet
			int rowSummary = sheetReportList.getLastRowNum() + 1;
			Row row = sheetReportList.createRow(rowSummary);

			Cell cellSummary = row.createCell(1);
			cellSummary.setCellValue("Jumlah Keseluruhan (RM)");
			cellSummary.setCellStyle(titleHeaderStyle(wb, true, true, false, false, true, true));
			CellRangeAddress cellZone = new CellRangeAddress(rowSummary, rowSummary, 1, 2);
			sheetReportList.addMergedRegion(cellZone);

			boxedRegion(cellSummary, cellZone, sheetReportList);

			// summary report sheet cell - jan
			Cell cellSummaryTotalJan = row.createCell(3);
			cellSummaryTotalJan.setCellValue((janTotal).doubleValue());
			cellSummaryTotalJan.setCellStyle(columnStyle(wb, createHelper, false, false, false, false, true, false,
					false));

			// summary report sheet cell - feb
			Cell cellSummaryTotalFeb = row.createCell(4);
			cellSummaryTotalFeb.setCellValue((febTotal).doubleValue());
			cellSummaryTotalFeb.setCellStyle(columnStyle(wb, createHelper, false, false, false, false, true, false,
					false));

			// summary report sheet cell - mar
			Cell cellSummaryTotalMac = row.createCell(5);
			cellSummaryTotalMac.setCellValue((macTotal).doubleValue());
			cellSummaryTotalMac.setCellStyle(columnStyle(wb, createHelper, false, false, false, false, true, false,
					false));

			// summary report sheet cell - april
			Cell cellSummaryTotalApr = row.createCell(6);
			cellSummaryTotalApr.setCellValue((aprTotal).doubleValue());
			cellSummaryTotalApr.setCellStyle(columnStyle(wb, createHelper, false, false, false, false, true, false,
					false));

			// summary report sheet cell - may
			Cell cellSummaryTotalMay = row.createCell(7);
			cellSummaryTotalMay.setCellValue((mayTotal).doubleValue());
			cellSummaryTotalMay.setCellStyle(columnStyle(wb, createHelper, false, false, false, false, true, false,
					false));

			// summary report sheet cell - june
			Cell cellSummaryTotalJun = row.createCell(8);
			cellSummaryTotalJun.setCellValue((junTotal).doubleValue());
			cellSummaryTotalJun.setCellStyle(columnStyle(wb, createHelper, false, false, false, false, true, false,
					false));

			// summary report sheet cell - july
			Cell cellSummaryTotalJuly = row.createCell(9);
			cellSummaryTotalJuly.setCellValue((julyTotal).doubleValue());
			cellSummaryTotalJuly.setCellStyle(columnStyle(wb, createHelper, false, false, false, false, true, false,
					false));

			// summary report sheet cell - aug
			Cell cellSummaryTotalAug = row.createCell(10);
			cellSummaryTotalAug.setCellValue((augTotal).doubleValue());
			cellSummaryTotalAug.setCellStyle(columnStyle(wb, createHelper, false, false, false, false, true, false,
					false));

			// summary report sheet cell - sept
			Cell cellSummaryTotalSep = row.createCell(11);
			cellSummaryTotalSep.setCellValue((sepTotal).doubleValue());
			cellSummaryTotalSep.setCellStyle(columnStyle(wb, createHelper, false, false, false, false, true, false,
					false));

			// summary report sheet cell - oct
			Cell cellSummaryTotalOct = row.createCell(12);
			cellSummaryTotalOct.setCellValue((octTotal).doubleValue());
			cellSummaryTotalOct.setCellStyle(columnStyle(wb, createHelper, false, false, false, false, true, false,
					false));

			// summary report sheet cell - nov
			Cell cellSummaryTotalNov = row.createCell(13);
			cellSummaryTotalNov.setCellValue((novTotal).doubleValue());
			cellSummaryTotalNov.setCellStyle(columnStyle(wb, createHelper, false, false, false, false, true, false,
					false));

			// summary report sheet cell - dec
			Cell cellSummaryTotalDec = row.createCell(14);
			cellSummaryTotalDec.setCellValue((decTotal).doubleValue());
			cellSummaryTotalDec.setCellStyle(columnStyle(wb, createHelper, false, false, false, false, true, false,
					false));

			// summary report sheet cell - total
			Cell cellSummaryTotalAll = row.createCell(15);
			cellSummaryTotalAll.setCellValue((totalAll).doubleValue());
			cellSummaryTotalAll.setCellStyle(columnStyle(wb, createHelper, false, false, false, false, true, false,
					false));
		}
		if (reportList.isEmpty()) {
			Row noRecordRow = sheetReportList.createRow(sheetReportList.getLastRowNum() + 1);
			Cell cellNoRecord = noRecordRow.createCell(1);
			cellNoRecord.setCellValue("Rekod tidak dijumpai");
			cellNoRecord.setCellStyle(titleHeaderStyle(wb, false, false, false, false, true, false));
			sheetReportList.addMergedRegion(new CellRangeAddress(sheetReportList.getLastRowNum(), sheetReportList
					.getLastRowNum(), 1, columnHeaderList.size()));
			boxedRegion(cellNoRecord,
					new CellRangeAddress(sheetReportList.getLastRowNum(), sheetReportList.getLastRowNum(), 1,
							columnHeaderList.size()), sheetReportList);
		}
		try {
			String path = EpPropertiesUtils.getInstance().getProperty(
					EpPropertiesConst.DOCUMENT_REPOSITORY_SHARED_REPORT_DIR);
			String fullPath = path + "/" + requestId + ".xlsx";
			// String fullPath = "D://reports//dev//"+requestId+".xlsx";
			// log.info("FULL PATH (CREATE FILE) == "+fullPath);
			FileOutputStream out = new FileOutputStream(new File(fullPath));
			wb.write(out);
			out.close();
			wb.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<Object[]> generateServiceFeeBasedOnAccountingReport(LinkedHashMap<String, String> params, String type) {
		IRPTRequestSO irptRequestSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ReportConst.RPT_MODULE_SER_OBJ, IRPTRequestSO.class);
		return irptRequestSO.generateServiceFeeBasedOnAccountingReport(params, type);
	}

	public List<Object[]> generateAccumulatedServiceFee(LinkedHashMap<String, String> params) {
		IRPTRequestSO irptRequestSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ReportConst.RPT_MODULE_SER_OBJ, IRPTRequestSO.class);
		return irptRequestSO.generateAccumulatedServiceFee(params);
	}

	public void boxedRegion(Cell cell, CellRangeAddress cellZone, Sheet sheet) {
		RegionUtil.setBorderBottom(cell.getCellStyle().getBorderBottomEnum().getCode(), cellZone, sheet);
		RegionUtil.setBorderTop(cell.getCellStyle().getBorderTopEnum().getCode(), cellZone, sheet);
		RegionUtil.setBorderLeft(cell.getCellStyle().getBorderLeftEnum().getCode(), cellZone, sheet);
		RegionUtil.setBorderRight(cell.getCellStyle().getBorderRightEnum().getCode(), cellZone, sheet);
	}

	public CellStyle titleHeaderStyle(Workbook wb, Boolean isBold, Boolean isAlignCenter, Boolean isWrapTxt,
			Boolean isVAlignTop, Boolean isBoxed, Boolean isShade) {
		Font titleReportFont = wb.createFont();
		if (isBold)
			titleReportFont.setBold(true);
		else
			titleReportFont.setBold(false);
		titleReportFont.setFontHeightInPoints((short) 10);
		titleReportFont.setFontName("Arial");

		CellStyle titleReportCellStyle = wb.createCellStyle();
		titleReportCellStyle.setFont(titleReportFont);
		if (isAlignCenter)
			titleReportCellStyle.setAlignment(HorizontalAlignment.CENTER);
		if (isWrapTxt)
			titleReportCellStyle.setWrapText(true);
		if (isVAlignTop)
			titleReportCellStyle.setVerticalAlignment(VerticalAlignment.TOP);
		else
			titleReportCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

		if (isBoxed) {
			titleReportCellStyle.setBorderTop(BorderStyle.THIN);
			titleReportCellStyle.setBorderBottom(BorderStyle.THIN);
			titleReportCellStyle.setBorderLeft(BorderStyle.THIN);
			titleReportCellStyle.setBorderRight(BorderStyle.THIN);
		}
		if (isShade) {
			titleReportCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			titleReportCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		}

		return titleReportCellStyle;
	}

	public CellStyle columnStyle(Workbook wb, CreationHelper createHelper, Boolean isCellListHeader, Boolean isBold,
			Boolean isHAlignCenter, Boolean isFormatDate, Boolean isFormatDecimalNumber, Boolean isWrapTxt,
			Boolean isVAlignCenter) {

		Font contentFont = wb.createFont();

		contentFont.setFontHeightInPoints((short) 10);
		contentFont.setFontName("Arial");
		if (isBold)
			contentFont.setBold(true);
		else
			contentFont.setBold(false);

		CellStyle cellStyle = wb.createCellStyle();

		if (isCellListHeader) {
			cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		}
		if (isHAlignCenter)
			cellStyle.setAlignment(HorizontalAlignment.CENTER);
		if (isFormatDate)
			cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
		if (isFormatDecimalNumber)
			cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.00"));
		if (isWrapTxt)
			cellStyle.setWrapText(true);
		if (isVAlignCenter)
			cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		else
			cellStyle.setVerticalAlignment(VerticalAlignment.TOP);

		cellStyle.setFont(contentFont);
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);

		return cellStyle;
	}
	// #29554 end
}