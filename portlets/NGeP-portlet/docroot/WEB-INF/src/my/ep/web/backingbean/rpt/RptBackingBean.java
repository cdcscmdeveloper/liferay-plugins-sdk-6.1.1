package my.ep.web.backingbean.rpt;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import my.ep.biz.bean.search.ISearchBean;

import my.ep.biz.dto.rpt.PMReportReqDTO;
import my.ep.biz.dto.rpt.PMReportResDTO;
import my.ep.biz.dto.rpt.ReportTempInfoReqDTO;
import my.ep.biz.dto.rpt.ReportTempInfoResDTO;

import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.role.IRoleSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.rpt.IPMReportSO;
import my.ep.biz.service.rpt.IReportTempInfoSO;

import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;

import my.ep.constants.CommonConstants;

import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;

import my.ep.dto.pm.common.categorycode.CategoryCodeReqDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;

import my.ep.exception.EpException;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.rpt.PMReport;
import my.ep.persistence.entity.sc.loaloi.ScLoaReport;
import my.ep.persistence.entity.sm.SmSupplierCategory;

import my.ep.type.pm.RoleCode;
import my.ep.type.report.EnumReportType;

import my.ep.util.ResourceBundleUtils;

import my.ep.util.lookupservice.EpEjbBusinessDelegate;

import my.ep.web.backingbean.rpt.model.CMRptViewModel;
import my.ep.web.backingbean.rpt.model.CTRptViewModel;
import my.ep.web.backingbean.rpt.model.FLRptViewModel;
import my.ep.web.backingbean.rpt.model.PMRptViewModel;
import my.ep.web.backingbean.rpt.model.PpRptViewModel;
import my.ep.web.backingbean.rpt.model.SCRptViewModel;
import my.ep.web.backingbean.rpt.model.SMRptViewModel;

import my.ep.web.bean.pm.UserProfilesSearchBean;

import my.ep.web.constant.report.CommonMethod;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.report.RptConst;

import my.ep.web.util.FacesScopes;

import my.ep.web.util.lookupservice.BusinessDelegate;

import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.PmCommonUtil;

@ManagedBean(name = "rptBackingBean")
@SessionScoped
public class RptBackingBean extends RptVariables{

	public RptBackingBean() {
		
		userId = new FacesScopes().getUserSessionBean().getUserId();
		
		strLanguage = ResourceBundleUtils.getMessage(
				ReportConst.LABEL_LANG, ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale());
		
		 moduleValue = "";
		 reportValue = "";
		 reportCategory = "";
		 
	}
	
	/**
	 * Xhtml method
	 * Value selected is stored in moduleValue 
	 */
	
	public List<SelectItem> getAllModuleList() {

		IPMReportSO reportSO = getPMReportSO();
			
		List<Object[]> reportModList = reportSO.retrieveModuleList(strLanguage,
				userId);
		
		return assignListToSelectItemValueLabel(reportModList);
	}

	/**
	 * Xhtml method
	 */

	public List<SelectItem> retrieveReportListByAuthorization() {
			
			reportValue = null;
			reportName = null;
			roleCode = null;
			reportList = new ArrayList<SelectItem>();
			
			List<Object[]> reportModList = new ArrayList<Object[]>();
			
			IPMReportSO reportSO = getPMReportSO();
			
			long userId = Long.valueOf(this.userId);
			
			if(isModuleValueFLOrCT()){

				reportCategoryView = true;
				
				reportCategoryList = retrieveSubModuleReportCategoryList(strLanguage);
				
			} else {
				
				System.out.println("reach here");
				
				reportCategoryView = false;
				
				if(isEnglish()){
				
					reportModList = reportSO
							.retrieveReportListByModuleNUserAuthorization(
									userId, moduleValue);
				}	

				if(isMalay()){
					
					reportModList = reportSO
							.retrieveMalayReportListByModuleNUserAuthorization(
									userId, moduleValue);
				}	
			}
					
			return reportList = assignListToSelectItemLabelValue(reportModList);
					
		
	}	
	
	public List<SelectItem> retrieveReportListBySubModule() {
		
		if(isModuleValueOFOrCT()){
			isCategoryView = true;
		}
		
		IPMReportSO reportSO = getPMReportSO();
		
		List<Object[]> list = new ArrayList<Object[]>();
		
		if(isEnglish()){

			list = reportSO.retrieveReportListBySubModule(
					moduleValue, reportCategory, userId);
		
		} else if(isMalay()){
	
			list = reportSO.retrieveMalayReportListBySubModule(
					moduleValue, reportCategory, userId);
		
		}
		
		return reportList = assignListToSelectItemLabelValue(list);
	}
	
	public List<String> generateYearList() {
		scYearList = this.generateCommonYearList();
		return scYearList;
	}
	
	public void criteriaVariablesByReport() {
		
		if(isModuleValueFLOrCT()){
			reportCategoryView = true;
		}
		
		checkToolTipsConditonForFLSubmodule();

		subModule = null;
		reportName = ReportConst.EMPTY_VAL;
		stateLst = new ArrayList<SelectItem>();
		districtLst = new ArrayList<SelectItem>();
		levOneCatLst = new ArrayList<SelectItem>();
		levTwoCatLst = new ArrayList<SelectItem>();
		levThreeCatLst = new ArrayList<SelectItem>();
		smMofRegistrationFeeList = new ArrayList<SelectItem>();
		smProcessingFeeList = new ArrayList<SelectItem>();
		smSoftCertificateFeeList = new ArrayList<SelectItem>();
		
		IPMReportSO reportSO = getPMReportSO();
		
		PMReportReqDTO reqDTO = new PMReportReqDTO();
		reqDTO.setReportCode(reportValue);
		
		PMReportResDTO rptResDTO = reportSO.retriveReportByrptId(reqDTO);
		
		PMReport pmReport = rptResDTO.getPmReportDtls();
		
		if(pmReport != null){
			subModule = pmReport.getSubModule();
		}
		
		if(isEnglish()){
			reportName = pmReport.getReportTitle();
			reportTitle = pmReport.getReportTitle();
		}
		
		if(isMalay()){
			reportName = pmReport.getReportTitleMalay();
			reportTitle = pmReport.getReportTitleMalay();
		}
	
		reportFileNameJSP = pmReport.getReportFilename();
		reportCode = pmReport.getReportCode();
		reportJsp = pmReport.getReportFilename();
		
		highRoleCodeList = retrieveHighRoleCodeByUser();
		allRoleCodesList = retrieveAllRoleCodesByUser();
		zoneList = retrieveZoneByUser();
		
		isByZonelist = isByZoneListByReportCode(reportValue);
		
		//Set to authoriztionList
		setUserRoleDescListByUserId(moduleValue);
		
		paramList = retrieveParamCodeByUser();
	
		
		/**
			Update the following variables.
			
			rolecodeRquisitionerList 
			rolecodeRecievingOfficerList 
			rolecodeApproverList
			rolecodeAcknowledgeOfficerList 
			rolecodePayOfficerList
		**/
		
		roleCodeUsersSetup();

		if (isModuleValueDP()){
			
			// ZoneList
			retrieveApprovarNaikTarafDPS1();
			
			//scPtjList
			retrievePTJS1();
		}

		if (isModuleValueSS()) {
			
			//approverNayakTarafList
			retrieveApprovarNaikTarafSCS2();
			
			//scPtjList
			retrievePTJS2();
		}

		retrieveRequisitioner();
		retrieveDescOfficer();
		generateYearList();
	
		if(isModuleValueCM()){
			
//			CMRptViewModel model = new CMRptViewModel(this, reportValue);
//			model.criteriaVariablesByReportByCM(authoriztionList, paramList);
			
		} else if(isModuleValueSSOrDP()){
			
			generateMonthList();
			
//			SCRptViewModel model = new SCRptViewModel(this, reportValue);
//			model.enableCriteriaVariablesByReportBySC(authoriztionList, approverNayakTarafList);

		} else if(isModuleValueOF()){
		
			getDocTypeStatusDropDown();
			
//			FLRptViewModel model = new FLRptViewModel(this, reportValue);
//			model.enableCriteriaVariablesByReportByFL(authoriztionList);
//	
		} else if(isModuleValueSM()){
	
			assignNewCompanyExperience(new ArrayList<SmSupplierCategory>());
			
			retrieveUPSSVOUser();
			retrieveIdentificationType();
			retrieveAccumulatedPaidUpCptl();
			retrieveRegStatusList();
			retrievePaymentTypeList();
			retrievePaymentMethod();
			
			SMRptViewModel model = new SMRptViewModel(this, reportValue);
			model.enableCriteriaVariablesByReportBySM(authoriztionList, this);
	
		} else if(isModuleValueCT()){
			
//			CTRptViewModel model = new CTRptViewModel(this, reportValue);
//			
//			model.enableCriteriaVariables();
			
		} else if(isModuleValuePP()){
			
//			PpRptViewModel model = new PpRptViewModel(this, reportValue);
//			
//			model.enableCriteriaVariablesByReportByPP(authoriztionList);
//			
		} else if(isModuleValuePM()){
		
//			PMRptViewModel model = new PMRptViewModel(this, reportValue);
//			
//			model.enableCriteriaVariablesByReportByPM(authoriztionList);
//			
		}
		
		resetReportForm();
		
		if (reportValue.equals(ReportConst.SM_LIST_OF_USERS)) {
			changeSoftCertSelection();
		}
		
		if (reportValue.equals(ReportConst.SM_LIST_OF_DIR_OWN_CEO_COMPANY_RPT_VAL)) {
			supplierTypeConsultant();
		}
		
		getGenBusCompanyData();
		getOrgTypeDropdownValues();
	
		if (pmReport != null) {
			getDefaultValuesFromAndToDate(pmReport.getReportCode()); 
		}

		retrieveDates();
		madatoryIndicator();
		retrieveAllMinistry();
		
		enableSupplierCompNameTextOrDropdown();
		
		retrieveParameterCodeType();
		
		enableCriteriaMenuView = true;
		generateButtonView = false;
		
		// Update
		// smZoneLabelView
		// smZoneView
		// smMandatoryZoneView
		// smMandatoryZoneForSVOSVLView
		// smMandatorySupplierTypeView
		// smSupplierTypeView
		// smZone
					
		zoneByRolecode();
		
		// Update
		// processingOfficerList
		
		retrieveProcessingOfficerList();
		
		// Update
		// serviceTypeList
		
		retrievePMServiceType();
		
		// Update
		// scSimpleQuoteDtlWithoutRequiGroupView
		// scSimpleQuoteDtlGroupView
		
		directPurchaseWithRequisitioner();
	}
	
	private void getGenBusCompanyData() {

	}

	
	public void assignNewCompanyExperience(List<SmSupplierCategory> catList) {
	
		if (CollectionUtils.isEmpty(catList)) {
			catList = new ArrayList<SmSupplierCategory>();
		}
		
		SmSupplierCategory cat = new SmSupplierCategory();
		cat.setPmCategoryL1(new PmCategoryL1());
		PmCategoryL2 catL2 = new PmCategoryL2();
		catL2.setPmCategoryL2s(new ArrayList<PmCategoryL2>());
		cat.setPmCategoryL2(catL2);
		PmCategoryL3 catL3 = new PmCategoryL3();
		catL3.setPmCategoryL3s(new ArrayList<PmCategoryL3>());
		cat.setPmCategoryL3(catL3);
		catList.add(cat);
		
		supplierParentCatList = retrieveParentCategoryListForExperiencePage();
		selectedCatList = catList;
		
		resetChildCatList();
		
		assignValReqdToTrue();
	}

	public SelectItem[] getDocTypeStatusDropDown() {
	
		docTypeStatusItems = null;
	
		String doctype = null;
		
		SelectItem[] tempOneStatusItems = null;
		SelectItem[] tempTwoStatusItems = null;
		
		docTypeStatusItemsList = new ArrayList<SelectItem>();
		
		String[] crReportArr = 	{	ReportConst.CON_REQ_RPT_MIN_VAL,
						   			ReportConst.CON_REQ_RPT_CEN_PEN_VAL
						   		};
		
		String[] coReportArr = 	{	ReportConst.CON_ORD_RPT_MIN_VAL,
									ReportConst.CON_ORD_RPT_CEN_PEN_VAL,
									ReportConst.INV_RPT_VIEW_PTJ_MIN_VAL,
									ReportConst.INV_RPT_VIEW_PTJ_CEN_PEN_VAL
								};
		
		String[] doReportArr = 	{	ReportConst.DEL_ORD_RPT_MIN_VAL,
									ReportConst.DEL_ORD_RPT_CEN_PEN_VAL,
									ReportConst.DEL_ORD_RPT_PO_VAL
								};
		
		String[] fnReportArr = 	{	ReportConst.FL_REC_NOTE_RPT_MIN_VAL,
									ReportConst.FL_REC_NOTE_RPT_CEN_PEN_VAL,
									ReportConst.FL_REC_NOTE_RPT_PO_VAL
								};				
		
		String[] inReportArr = 	{	ReportConst.INV_RPT_VIEW_SUPL_MIN_VAL,
									ReportConst.INV_RPT_VIEW_SUPL_CEN_PEN_VAL,
									ReportConst.INV_RPT_VIEW_SUPL_PO_VAL
								};
		
		String[] paReportArr = 	{	ReportConst.PYMT_MATCH_RPT_MIN_VAL,
									ReportConst.PYMT_MATCH_RPT_CEN_PEN_VAL,
									ReportConst.PYMT_MATCH_RPT_PO_VAL
								};

		String[] dnReportArr =  {	ReportConst.DED_RPT_MIN_VAL
								};
	
		String[] poReportArr =	{	ReportConst.SUMM_RPT_PAN_ITM_VAL,
									ReportConst.PUR_ORD_RPT_PO_VAL,
									ReportConst.INV_RPT_VIEW_PTJ_PO_VAL
								};
		
		String[] prReportArr =  {
									ReportConst.PUR_REQ_RPT_PO_VAL
								};
		
		String[] sdReportArr = 	{	ReportConst.FL_STP_INSTR_INIT_PTJ_RPT_FL_PO_VAL,
									ReportConst.FL_STP_INSTR_INIT_SUP_RPT_FL_MIN_VAL,
									ReportConst.FL_STP_INSTR_INIT_SUP_RPT_FL_CEN_PEN_VAL,
									ReportConst.FL_STP_INSTR_INIT_SUP_RPT_FL_PO_VAL
								};
		
		
		if(reportValue.equals(ReportConst.EMR_PROCUR_RPT_PO_VAL)) {
			
			docTypeStatusItemsList = new ArrayList<SelectItem>();
			
			tempOneStatusItems = getStatusDropdownValues(moduleValue, ReportConst.DOC_TYPE_PO);
			
			tempTwoStatusItems = getStatusDropdownValues(moduleValue, ReportConst.DOC_TYPE_PR);
		
			for (SelectItem item1 : tempOneStatusItems) {
				docTypeStatusItemsList.add(item1);
			}
			
			for (SelectItem item2 : tempTwoStatusItems) {
			
				if ((!item2.getLabel().equals(
						ResourceBundleUtils.getMessage("lbl.txt.select",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								new FacesScopes().getUserLocale())))) {
				
					docTypeStatusItemsList.add(item2);
				}
			}
		}
		
		if (StringUtils.isNotEmpty(doctype)) {
		
			docTypeStatusItems = null;
			docTypeStatusItemsList = new ArrayList<SelectItem>();
			docTypeStatusItems = getStatusDropdownValues(moduleValue, doctype);
			
			for (SelectItem itemCommon : docTypeStatusItems) {
				docTypeStatusItemsList.add(itemCommon);
			}
			
			String[] flReportArr = {
				
				ReportConst.FL_STP_INSTR_INIT_PTJ_RPT_FL_PO_VAL,
				ReportConst.FL_STP_INSTR_INIT_SUP_RPT_FL_MIN_VAL,
				ReportConst.FL_STP_INSTR_INIT_SUP_RPT_FL_CEN_PEN_VAL,
				ReportConst.FL_STP_INSTR_INIT_SUP_RPT_FL_PO_VAL
				
			};
			
			if(isMatch(reportValue, flReportArr)){
			
				for (SelectItem item : docTypeStatusItemsList) {
				
					if (!item.getLabel().equalsIgnoreCase(
							ReportConst.STATUS_CANCELLED))
						docTypeTempStatusItemsList.add(item);
				}
				
				docTypeStatusItemsList = docTypeTempStatusItemsList;
			
				docTypeTempStatusItemsList.clear();
				
			}
		}
		
		return docTypeStatusItems;
	}
	
	public void retrieveParameterCodeType() {
	
		ministryList = new ArrayList<SelectItem>();
		pegaWaiPengawalList = new ArrayList<SelectItem>();
		kumpulanPTJList = new ArrayList<SelectItem>();
		ptjList = new ArrayList<SelectItem>();
		
		getParameterCodeTypeString();
		
		if(parameterCodeType.equals(ReportConst.PARAM_CODE_TYPE_FPJ)) {
			
			retrieveOnlyFPJMinistryList();
			retrievePFPPList();
			retrievePFJAList();
			retrieveFPJPTJList();
			
		} else if (parameterCodeType.equals(ReportConst.PARAM_CODE_TYPE_FMI)) {

//			retrieveFPJMinistryList();
//
//			if(isFlSupplierView){
//				flSupplierTextView = true;
//			}
			
		} else if (parameterCodeType.equals(ReportConst.ROLE_CODE_SUP)) {
			
			retrieveSupplierCompName();
			
//			if ((this.getSupplCompanyList().size() > 0)
//						&& (FlSupplierView())) {
//					getManagedBean().getthis().setFlSupplierDropdownView(
//							true);
//		} else if ((this.getSupplCompanyList().size() == 0)
//						&& (FlSupplierView())) {
//					getManagedBean().getthis()
//							.setFlSupplierTextView(true);
//	
//			} else {
//			
//				if(isFLSupplierView){
//					flSupplierTextView = true;
//				}
//			}
			
		//	paramCodeSupplierGroupDropdown(parameterCodeType);
		}
	}

	public List<SelectItem> retrieveFPJPTJList() {

		ptjList = new ArrayList<SelectItem>();
		
		IPMReportSO reportSO = getPMReportSO();
		
		List<Object[]> ptjLst = reportSO.retrieveFPJPTJList(userId);
	
		if (ptjLst.size() > 2) {
	
			SelectItem selectItem1 = new SelectItem();
	
			selectItem1.setLabel(ResourceBundleUtils.getMessage(
			"lbl.txt.select",
			ResourceBundleUtils.BUNDLE_COMMON_LABELS,
			new FacesScopes().getUserLocale()));
			
			selectItem1.setValue(null);
			ptjList.add(selectItem1);
		}

		for (Object[] object : ptjLst) {
			SelectItem selectItem = new SelectItem();
			selectItem.setLabel(object[0].toString());
			selectItem.setValue(object[1].toString());
			ptjList.add(selectItem);
		}

//	if (ptjList.size() > 2) {
//		this.getManagedBean().setPtjId(
//		(Long.valueOf(ptjList.get(1).getValue().toString())));
//	} else if (ptjList.size() >= 1) {
//		this.getManagedBean().setPtjId(
//		(Long.valueOf(ptjList.get(0).getValue().toString())));
//	}

		return ptjList;
	}

	public List<SelectItem> retrievePFJAList() {
		
		IPMReportSO reportSO = getPMReportSO(); 
				
		List<Object[]> ptjLst = reportSO.retrievePFJAList(lngUserId);
		
		if (CollectionUtils.isNotEmpty(ptjLst)) {
			
			if (ptjLst.size() > 2) {
		
				SelectItem selectItem1 = new SelectItem();
			
				selectItem1.setLabel(ResourceBundleUtils.getMessage(
							"lbl.txt.select",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()));
				selectItem1.setValue(null);
	
				kumpulanPTJList.add(selectItem1);
			}
				
			for (Object[] object : ptjLst) {
					SelectItem selectItem = new SelectItem();
					selectItem.setLabel(object[0].toString());
					selectItem.setValue(object[1].toString());
					kumpulanPTJList.add(selectItem);
				}
			}
		
		return null;
	}

	public List<SelectItem> retrieveOnlyFPJMinistryList() {
	
		IPMReportSO reportSO = getPMReportSO();
		
		List<Object[]> list = reportSO.retrieveFPJMinistryList(userId);
			
		return ministryList = assignListToSelectItem(list);
		
	}
	
	public List<SelectItem> retrieveRequisitioner() {
	
		IPMReportSO reportSO = getPMReportSO();

		long orgProfileId = 0L;
		
		
		
		List<Object[]> list = reportSO.retrieveRequisitioner(userId, orgProfileId);
			
		return scRequisitionerList = assignListToSelectItemValueLabel(list);
	}
	
	
	public Long parseLong(String str){
		
		long no = 0;
		
		try{
		
			no = Long.parseLong(scPtj); 
		
		} catch(Exception ex){
			
		}
		
		return no;
	}
	
	
	public List<SelectItem> retrieveApprovarNaikTarafSCS2() {
		
		IPMReportSO reportSO = getPMReportSO();
		List<Object[]> list = reportSO.retrieveApprovarNaikTarafSCS2(userId);
		
		return approverNayakTarafList = assignListToSelectItemLabelValue(list);
	}
	
	public List<BigDecimal> isByZoneListByReportCode(String reportCode) {

		IPMReportSO reportSO = getPMReportSO();
		
		List<BigDecimal> isByZonelistByReportCodeList = reportSO
				.retrieveIsByZoneByReportCode(reportCode);
		
		List<BigDecimal> list = new ArrayList<BigDecimal>();
		
		if (!isEmpty(isByZonelistByReportCodeList)){
			list.add(isByZonelistByReportCodeList.get(0));
		}
		
		return list;
	}
	
	
	public List<SelectItem> retrieveDescOfficer() {
		
		IPMReportSO reportSO = getPMReportSO();
		
		List<Object[]> list = reportSO.retrieveDeskOfficer(userId, parseLong(scPtj));
			
		return scDescOfficerList = assignListToSelectItemValueLabel(list);
		
	}
	
	public List<SelectItem> retrievePTJS1() {
	
		IPMReportSO reportSO = getPMReportSO();
		List<Object[]> list = reportSO.retrievePTJS1(userId);
		
		return scPtjList = assignListToSelectItem(list);
	}
	
	public List<SelectItem> retrievePTJS2() {
	
		IPMReportSO reportSO = getPMReportSO();
		List<Object[]> list = reportSO.retrievePTJS2(userId);
		
		return scPtjList = assignListToSelectItem(list);
	
	}
	
	public List<SelectItem> generateMonthList() {
		//scMonthList = scReportBackingBean.generateMonthList(strLanguage);
		//return scMonthList;
		return null;
	}
	
	public List<SelectItem> retrieveFPJMinistryList() {
//		log.info("ReportBackingBean-retrieveFPJMinistryList");
//		ministryList = new ArrayList<SelectItem>();
//		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
//			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
//					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
//							IPMReportSO.class);
//			List<Object[]> minstryLst = reportSO
//					.retrieveFPJMinistryList(lngUserId);
//			if (CollectionUtils.isNotEmpty(minstryLst)) {
//				for (Object[] object : minstryLst) {
//					/*
//					 * SelectItem selectItem1 = new SelectItem();
//					 * selectItem1.setLabel
//					 * (ResourceBundleUtils.getMessage("lbl.txt.select",
//					 * ResourceBundleUtils.BUNDLE_COMMON_LABELS, new
//					 * FacesScopes().getUserLocale()));
//					 * selectItem1.setValue(null);
//					 * ministryList.add(selectItem1);
//					 */
//					SelectItem selectItem = new SelectItem();
//					selectItem.setLabel(object[0].toString());
//					selectItem.setValue(object[1].toString());
//					ministryList.add(selectItem);
//					if ((null != object[1].toString())
//							&& (ReportConst.EMPTY_VAL != object[1].toString())) {
//						Long lngMinistry = Long.parseLong(object[1].toString());
//						retrieveFMIPTJListWithArg(lngMinistry);
//					}
//				}
//			}
//		}
//		return ministryList;
		
		return null;
	}
	
	public void enableSupplierCompNameTextOrDropdown() {

//		if(isModuleValueSM(){
//		
//			getParameterCodeTypeString();
//			retrieveSupplierCompName();
//			
//		)
//		
//			
//			if (StringUtils.isNotEmpty(parameterCodeType)
//					&& (parameterCodeType.equals(ReportConst.ROLE_CODE_SUP))
//					&& (this.getReportValue()
//							.equalsIgnoreCase(ReportConst.SM_PERSON_BRANCH_BANK))) {
//			
//				if ((this.getSupplCompanyList().size() > 0)
//						&& (SmSupplierView())) {
//			
//					getManagedBean().getthis()
//							.setSmCompanyNameDropdownView(true);
//                                        getManagedBean().getthis().setSmCommonZoneView(false);
//					getManagedBean().getthis().setSmMandatoryEPAdminPTJAdminZoneView(false);					
//					getManagedBean().getthis().setSmSCPSupplierTypeView(false);
//					getManagedBean().getthis().setSmRegistrationNoView(false);
//				
//				} else if ((this.getSupplCompanyList().size() == 0)
//						&& (SmSupplierView())) {
//					getManagedBean().getthis().setSmCompanyNameView(true);
//				}
//				
//				getManagedBean().getthis().setSmExtraTwoColumnView(true);
//				getManagedBean().getthis().setSmRegistrationTypeView(
//						false);
//				getManagedBean().getthis().setSmNonMandatoryDateView(
//						false);
//				getManagedBean().getthis().setSmMOFRegisteredDateView(
//						false);
//		
//			} else if (StringUtils.isNotEmpty(parameterCodeType)
//					&& (parameterCodeType.equals(ReportConst.ROLE_CODE_SUP))) {
//
//				if ((this.getSupplCompanyList().size() > 0)
//						&& (SmSupplierView())) {
//					
//					getManagedBean().getthis()
//							.setSmCompanyNameDropdownView(true);
//				
//				} else if ((this.getSupplCompanyList().size() == 0)
//						&& (SmSupplierView())) {
//				
//					smCompanyNameView = true;
//				}
//			
//			} else {
//			
//				if (smSupplierView) {
//					smCompanyNameView = true;
//				}
//			}
//		}
	}
	
	public List<SelectItem> retrieveApprovarNaikTarafDPS1() {
		
		IPMReportSO reportSO = getPMReportSO();
		List<Object[]> list = reportSO.retrieveApprovarNaikTarafDPS1(userId);
		
		return zoneList = assignListToSelectItemLabelValue(list);
	}
	
	public String getParameterCodeTypeString() {
	
		IPMReportSO reportSO = getPMReportSO();
		
		List<Object[]> list = reportSO
				.retrieveParameterCodeByUserid(userId);
		
		if(!isEmpty(list)){
			
			Object[] objArr = list.get(0);
			
			return parameterCodeType =  objArr[0].toString();
		}
			
		return "";
	}
	
	public void retrieveSupplierCompName() {
	
//		supplCompanyList = new ArrayList<String>();
//		
//		IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
//				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
//						IPMReportSO.class);
//		
//		List<String> supplCompNameLst = reportSO
//				.retrieveSupplierCompName(lngUserId);
//		
//		if (CollectionUtils.isNotEmpty(supplCompNameLst)) {
//			for (int i = 0; i < supplCompNameLst.size(); i++) {
//				supplCompanyList.add(supplCompNameLst.get(i));
//			}
//		}
//		
//		if (supplCompNameLst.size() > 0) {
//			if (StringUtils.isNotEmpty(this.getModuleValue())
//					&& (this.getModuleValue()
//							.equalsIgnoreCase(ModuleCodeConst.SUPPLIER_MANAGEMENT))) {
//				this.setSmCompanyName(supplCompNameLst.get(0));
//			}
//			if ((StringUtils.isNotEmpty(this.getModuleValue()))
//					&& (this.getModuleValue()
//							.equalsIgnoreCase(ModuleCodeConst.ORDER_FULFILLMENT))) {
//				this.setFlSupplier(supplCompNameLst.get(0));
//			}
//		}
	}

	public void setUserRoleDescListByUserId(String moduleName) {
		
		IUserSO userSO = getUserSO();
		
		UserReqDTO reqDTO = new UserReqDTO();
		
		reqDTO.setUserId(userId);
		reqDTO.setLanguageCode(ReportConst.ENG_LANG);
		
		UserResDTO userResDTO = userSO.retrieveUserRoleDescListByUserId(reqDTO);
		
		List<PmRoleDesc> roleDescList = userResDTO.getRoleDescList();
		
		if (CollectionUtils.isNotEmpty(roleDescList)) {
		
			for (PmRoleDesc pmRoleDesc : roleDescList) {
			
				String roleCode = pmRoleDesc.getPmRole().getRoleCode();
				
				if (!authoriztionList.contains(roleCode)) {
					authoriztionList.add(roleCode);
				}
			}
		}
	}
	
	public List<String> retrieveParamCodeByUser() {
		IPMReportSO reportSO = getPMReportSO();
		return reportSO.retrieveParamCodeByUser(userId);
	}
	
	public void roleCodeUsersSetup() {
		
		IPMReportSO reportSO = getPMReportSO();
		
		Long lngPtjId = null;
		
		if(highRoleCodeList.contains(ReportConst.ROLE_CODE_REQUISITIONER)){
			
			List<Object[]> list = reportSO.retrieveUserListByREQUISITIONER(userId, ReportConst.ROLE_CODE_REQUISITIONER);
			rolecodeRquisitionerList = assignListToSelectItemLabelValue(list);
		
		} else {
			
			if (!isEmpty(selectedPTJ) && selectedPTJ.getOrgProfileId() != null) {
				
				lngPtjId = selectedPTJ.getOrgProfileId();
				
			} else {
				
				lngPtjId = ptjId;
				
			}
			
			String[] strArr1 = {
					ReportConst.CON_REQ_RPT_MIN_VAL,
					ReportConst.CON_REQ_RPT_CEN_PEN_VAL,
					ReportConst.PUR_REQ_RPT_PO_VAL,
					ReportConst.CON_REQ_CANC_RPT_MIN_VAL,
					ReportConst.CON_REQ_CANC_RPT_CEN_PEN_VAL,
					ReportConst.EMR_PROCUR_RPT_PO_VAL
			};
			
			if(isMatch(reportValue, strArr1)){

				List<Object[]> list = reportSO
						.retrieveUserListByAdminOrBKPRoleCode(lngUserId,
								ReportConst.ROLE_CODE_REQUISITIONER,
								lngPtjId);
				
				rolecodeRquisitionerList = assignListToSelectItemLabelValue(list);
			}
			
			String[] strArr2 = {
					ReportConst.FL_REC_NOTE_RPT_MIN_VAL,
					ReportConst.FL_REC_NOTE_RPT_CEN_PEN_VAL,
					ReportConst.LATE_DEL_RPT_CEN_PEN_VAL,
					ReportConst.LATE_DEL_RPT_MIN_VAL,
					ReportConst.FL_REC_NOTE_RPT_PO_VAL
			};
		
			if(isMatch(reportValue, strArr2)){
		
				List<Object[]> list = reportSO
						.retrieveUserListByAdminOrBKPRoleCode(lngUserId,
								ReportConst.ROLE_CODE_RECEIVING_OFFICER,
								lngPtjId);
				
				rolecodeRecievingOfficerList = assignListToSelectItemLabelValue(list);
			}
			
			String[] strArr3 = {
					ReportConst.CON_ORD_RPT_MIN_VAL,
					ReportConst.CON_ORD_RPT_CEN_PEN_VAL,
					ReportConst.PUR_ORD_RPT_PO_VAL,
					ReportConst.PUR_REQ_CANC_RPT_PO_VAL,
					ReportConst.PUR_ORD_CANC_RPT_PO_VAL,
					ReportConst.CON_REQ_CANC_RPT_MIN_VAL,
					ReportConst.CON_REQ_CANC_RPT_CEN_PEN_VAL,
					ReportConst.FL_STP_INSTR_INIT_PTJ_RPT_FL_MIN_VAL,
					ReportConst.FL_STP_INSTR_INIT_PTJ_RPT_FL_CEN_PEN_VAL,
					ReportConst.FL_STP_INSTR_INIT_PTJ_RPT_FL_PO_VAL			
			};
			
			if(isMatch(reportValue, strArr3)){
				
				List<Object[]> list = reportSO
						.retrieveUserListByAdminOrBKPRoleCode(lngUserId,
								ReportConst.ROLE_CODE_FL_APPROVER, lngPtjId);
	
				rolecodeApproverList = assignListToSelectItemValueLabel(list);
			};
			
			String[] strArr4 = {
					ReportConst.FL_REC_NOTE_RPT_PO_VAL,
					ReportConst.LATE_DEL_RPT_PO_VAL,
					ReportConst.FL_REC_NOTE_RPT_MIN_VAL,
					ReportConst.FL_REC_NOTE_RPT_CEN_PEN_VAL,
					ReportConst.FL_REC_NOTE_CANC_RPT_MIN_VAL,
					ReportConst.FL_REC_NOTE_CANC_RPT_CEN_PEN_VAL,
					ReportConst.LATE_DEL_RPT_MIN_VAL,
					ReportConst.LATE_DEL_RPT_CEN_PEN_VAL,
					ReportConst.FL_REC_NOTE_CANC_RPT_PO_VAL
			};
			
			if(isMatch(reportValue, strArr4)){
				
				List<Object[]> list = reportSO
						.retrieveUserListByAdminOrBKPRoleCode(lngUserId,
								ReportConst.ROLE_CODE_ACKNOWLEDGE_OFFICER,
								lngPtjId);
				
				rolecodeAcknowledgeOfficerList = assignListToSelectItemValueLabel(list);
			}			
					}
			String[] strArr5 = {
					ReportConst.PYMT_MATCH_RPT_PO_VAL,
					ReportConst.PYMT_MATCH_RPT_MIN_VAL,
					ReportConst.PYMT_MATCH_RPT_CEN_PEN_VAL};
			
			if(isMatch(reportValue, strArr5)){
				
				List<Object[]> list = reportSO
						.retrieveUserListByAdminOrBKPRoleCode(lngUserId,
								ReportConst.ROLE_CODE_PAY_OFFICER, lngPtjId);
				
				rolecodePayOfficerList = assignListToSelectItemValueLabel(list);
			}

		if(highRoleCodeList.contains(ReportConst.ROLE_CODE_RECEIVING_OFFICER)|| 
		   highRoleCodeList.contains(ReportConst.ROLE_CODE_CT_APPROVER) || 
		   highRoleCodeList.contains(ReportConst.ROLE_CODE_RN_APPROVER)){
				
		   List<Object[]> listAppr = reportSO.retrieveUserListByREQUISITIONER(lngUserId, highRoleCodeList.get(0));
		   rolecodeApproverList = assignListToSelectItemLabelValue(listAppr);
		}
		
		if(highRoleCodeList.contains(ReportConst.ROLE_CODE_RECEIVING_OFFICER)){
			
			List<Object[]> list1 = reportSO
					.retrieveUserListByREQUISITIONER(lngUserId,
							ReportConst.ROLE_CODE_RECEIVING_OFFICER);
			
			rolecodeRecievingOfficerList = assignListToSelectItemLabelValue(list1);
			
			List<Object[]> list2 = reportSO
					.retrieveUserListByREQUISITIONER(lngUserId,
							ReportConst.ROLE_CODE_REQUISITIONER);
			
			rolecodeRquisitionerList = assignListToSelectItemLabelValue(list2);
		}
		
		if(highRoleCodeList.contains(ReportConst.ROLE_CODE_ACKNOWLEDGE_OFFICER)){
			
			List<Object[]> list1 = reportSO
					.retrieveUserListByREQUISITIONER(lngUserId,
							ReportConst.ROLE_CODE_ACKNOWLEDGE_OFFICER);
			
			rolecodeAcknowledgeOfficerList = assignListToSelectItemLabelValue(list1);
			
			List<Object[]> list2 = reportSO
					.retrieveUserListByREQUISITIONER(lngUserId,
							ReportConst.ROLE_CODE_REQUISITIONER);
			
			rolecodeRquisitionerList = assignListToSelectItemLabelValue(list2);
		}
		
		if(highRoleCodeList.contains(ReportConst.ROLE_CODE_PAY_OFFICER)){
	
			List<Object[]> list1 = reportSO
					.retrieveUserListByREQUISITIONER(lngUserId,
							ReportConst.ROLE_CODE_PAY_OFFICER);
			
			rolecodePayOfficerList = assignListToSelectItemLabelValue(list1);
			
			List<Object[]> list2 = reportSO
					.retrieveUserListByREQUISITIONER(lngUserId,
							ReportConst.ROLE_CODE_REQUISITIONER);
			
			rolecodeRquisitionerList = assignListToSelectItemLabelValue(list2);
		}
	}
	
public void roleCodeUsersSetupOnload() {
		
//		IPMReportSO reportSO = getPMReportSO();
//		
//		Long lngPtjId = null;
//		
//		if(highRoleCodeList.contains(ReportConst.ROLE_CODE_REQUISITIONER)){
//			
//			List<Object[]> list = reportSO.retrieveUserListByREQUISITIONER(userId, ReportConst.ROLE_CODE_RECEIVING_OFFICER);
//			rolecodeRquisitionerList = assignListToSelectItemLabelValue(list);
//		
//		} else {
//			
//			lngPtjId = selectedPTJ.getOrgProfileId();
//			
//			String[] strArr1 = {
//					ReportConst.CON_REQ_RPT_CEN_PEN_VAL,
//					ReportConst.PUR_REQ_RPT_PO_VAL,
//					ReportConst.EMR_PROCUR_RPT_PO_VAL
//			};
//			
//			if(isMatch(reportValue, strArr1)){
//
//				List<Object[]> list = reportSO
//						.retrieveUserListByAdminOrBKPRoleCode(userId,
//								ReportConst.ROLE_CODE_REQUISITIONER,
//								lngPtjId);
//				
//				rolecodeRquisitionerList = assignListToSelectItemLabelValue(list);
//			}
//		
//			String[] strArr2 = {
//					ReportConst.FL_REC_NOTE_RPT_MIN_VAL,
//					ReportConst.FL_REC_NOTE_RPT_CEN_PEN_VAL,
//					ReportConst.LATE_DEL_RPT_CEN_PEN_VAL,
//					ReportConst.LATE_DEL_RPT_MIN_VAL,
//					ReportConst.FL_REC_NOTE_RPT_PO_VAL
//			};
//			
//			if(isMatch(reportValue, strArr2)){
//			
//				List<Object[]> list = reportSO
//						.retrieveUserListByAdminOrBKPRoleCode(lngUserId,
//								ReportConst.ROLE_CODE_RECEIVING_OFFICER,
//								lngPtjId);
//			}
//						
//			String[] strArr3 = {			
//					ReportConst.CON_ORD_RPT_MIN_VAL,
//					ReportConst.CON_ORD_RPT_CEN_PEN_VAL,
//					ReportConst.PUR_ORD_RPT_PO_VAL
//			};
//			
//			if(isMatch(reportValue, strArr3)){
//				
//				List<Object[]> list = reportSO
//				.retrieveUserListByAdminOrBKPRoleCode(userId,
//						ReportConst.ROLE_CODE_ACKNOWLEDGE_OFFICER,
//						lngPtjId);
//				
//				rolecodeAcknowledgeOfficerList = assignListToSelectItemLabelValue(list);
//			}
//			
//			String[] strArr3 = {
//									ReportConst.PYMT_MATCH_RPT_PO_VAL,
//									ReportConst.PYMT_MATCH_RPT_MIN_VAL,
//									ReportConst.PYMT_MATCH_RPT_CEN_PEN_VAL
//								};
//			
//			if(isMatch(reportValue, strArr3)){
//				
//				List<Object[]> list = reportSO
//				.retrieveUserListByAdminOrBKPRoleCode(userId,
//						ReportConst.ROLE_CODE_PAY_OFFICER, lngPtjId);
//	
//				rolecodePayOfficerList = assignListToSelectItemValueLabel(list);
//			}
//			
//			String[] strArr4 = {
//									ReportConst.CON_REQ_RPT_MIN_VAL,
//									ReportConst.CON_REQ_RPT_CEN_PEN_VAL,
//									ReportConst.PUR_REQ_RPT_PO_VAL,
//									ReportConst.EMR_PROCUR_RPT_PO_VAL
//								};
//			
//			if(isMatch(reportValue, strArr4)){
//				
//				List<Object[]> list = reportSO
//						.retrieveUserListByREQUISITIONER(userId,
//								highRoleCodeList.get(0));
//				
//				rolecodeApproverList = assignListToSelectItemValueLabel(list);
//			}
//		}
//
//		if(highRoleCodeList.contains(ReportConst.ROLE_CODE_RECEIVING_OFFICER)|| 
//		   highRoleCodeList.contains(ReportConst.ROLE_CODE_CT_APPROVER) || 
//		   highRoleCodeList.contains(ReportConst.ROLE_CODE_RN_APPROVER)){
//				
//		   List<Object[]> listAppr = reportSO.retrieveUserListByREQUISITIONER(lngUserId, highRoleCodeList.get(0));
//		   rolecodeApproverList = assignListToSelectItemLabelValue(listAppr);
//		}
//		
//		if(highRoleCodeList.contains(ReportConst.ROLE_CODE_RECEIVING_OFFICER)){
//			
//			List<Object[]> list1 = reportSO
//					.retrieveUserListByREQUISITIONER(lngUserId,
//							ReportConst.ROLE_CODE_RECEIVING_OFFICER);
//			
//			rolecodeRecievingOfficerList = assignListToSelectItemLabelValue(list1);
//			
//			List<Object[]> list2 = reportSO
//					.retrieveUserListByREQUISITIONER(lngUserId,
//							ReportConst.ROLE_CODE_REQUISITIONER);
//			
//			rolecodeRquisitionerList = assignListToSelectItemLabelValue(list2);
//		}
//		
//		if(highRoleCodeList.contains(ReportConst.ROLE_CODE_ACKNOWLEDGE_OFFICER)){
//			
//			List<Object[]> list1 = reportSO
//					.retrieveUserListByREQUISITIONER(lngUserId,
//							ReportConst.ROLE_CODE_ACKNOWLEDGE_OFFICER);
//			
//			rolecodeAcknowledgeOfficerList = assignListToSelectItemLabelValue(list1);
//			
//			List<Object[]> list2 = reportSO
//					.retrieveUserListByREQUISITIONER(lngUserId,
//							ReportConst.ROLE_CODE_REQUISITIONER);
//			
//			rolecodeRquisitionerList = assignListToSelectItemLabelValue(list2);
//		}
//		
//		if(highRoleCodeList.contains(ReportConst.ROLE_CODE_PAY_OFFICER)){
//	
//			List<Object[]> list1 = reportSO
//					.retrieveUserListByREQUISITIONER(lngUserId,
//							ReportConst.ROLE_CODE_PAY_OFFICER);
//			
//			rolecodePayOfficerList = assignListToSelectItemLabelValue(list1);
//			
//			List<Object[]> list2 = reportSO
//					.retrieveUserListByREQUISITIONER(lngUserId,
//							ReportConst.ROLE_CODE_REQUISITIONER);
//			
//			rolecodeRquisitionerList = assignListToSelectItemLabelValue(list2);
//		}
	}
	
	public boolean isReportValueFL(){
		
		String[] strArr= new String[4];
		
		strArr[0] = ReportConst.CON_REQ_RPT_CEN_PEN_VAL;
		strArr[1] = ReportConst.CON_REQ_CANC_RPT_MIN_VAL;
		strArr[2] = ReportConst.CON_REQ_CANC_RPT_CEN_PEN_VAL;
		strArr[3] = ReportConst.EMR_PROCUR_RPT_PO_VAL;
		
		return isMatch(reportValue, strArr);
	}
	
	public boolean isReportValueFL1(){
	
		String[] strArr= new String[7];
		
		strArr[0] = ReportConst.CON_ORD_RPT_CEN_PEN_VAL;
		strArr[1] = ReportConst.PUR_ORD_RPT_PO_VAL;
		strArr[2] = ReportConst.PUR_REQ_CANC_RPT_PO_VAL;
		strArr[3] = ReportConst.PUR_ORD_CANC_RPT_PO_VAL;
		
		strArr[4] = ReportConst.CON_REQ_CANC_RPT_MIN_VAL;
		strArr[5] = ReportConst.CON_REQ_CANC_RPT_CEN_PEN_VAL;
		strArr[6] = ReportConst.FL_STP_INSTR_INIT_PTJ_RPT_FL_MIN_VAL;
		strArr[7] = ReportConst.FL_STP_INSTR_INIT_PTJ_RPT_FL_CEN_PEN_VAL;
		
		return isMatch(reportValue, strArr);
	}
	
	public boolean isReportValueFL2(){
		
		String[] strArr= new String[5];
		
		strArr[0] = ReportConst.FL_REC_NOTE_RPT_MIN_VAL;
		strArr[1] = ReportConst.FL_REC_NOTE_RPT_CEN_PEN_VAL;
		strArr[2] = ReportConst.LATE_DEL_RPT_CEN_PEN_VAL;
		strArr[3] = ReportConst.LATE_DEL_RPT_MIN_VAL;
		strArr[4] = ReportConst.CON_REQ_CANC_RPT_MIN_VAL;
		
		return isMatch(reportValue, strArr);
	}

	public boolean isReportValueFL3(){
		
		String[] strArr= new String[5];
		
		strArr[0] = ReportConst.FL_REC_NOTE_RPT_PO_VAL;
		strArr[1] = ReportConst.LATE_DEL_RPT_PO_VAL;
		strArr[2] = ReportConst.FL_REC_NOTE_RPT_MIN_VAL;
		strArr[3] = ReportConst.FL_REC_NOTE_RPT_CEN_PEN_VAL;
		strArr[4] = ReportConst.FL_REC_NOTE_CANC_RPT_MIN_VAL;
		strArr[4] = ReportConst.FL_REC_NOTE_CANC_RPT_CEN_PEN_VAL;
		strArr[4] = ReportConst.LATE_DEL_RPT_MIN_VAL;
		strArr[4] = ReportConst.LATE_DEL_RPT_CEN_PEN_VAL;
		strArr[4] = ReportConst.FL_REC_NOTE_CANC_RPT_PO_VAL;
		
		return isMatch(reportValue, strArr);
	}
		
	public boolean isReportValueFL4(){
		
		String[] strArr= new String[3];
		
		strArr[0] = ReportConst.PYMT_MATCH_RPT_PO_VAL;
		strArr[1] = ReportConst.PYMT_MATCH_RPT_MIN_VAL;
		strArr[2] = ReportConst.PYMT_MATCH_RPT_CEN_PEN_VAL;
		
		return isMatch(reportValue, strArr);
	}
	
	public boolean isReportValueFL5(){
		
		List<String> list = new ArrayList<String>();
		
		list.add(ReportConst.CON_REQ_RPT_MIN_VAL);
		list.add(ReportConst.CON_REQ_RPT_CEN_PEN_VAL);
		list.add(ReportConst.PUR_REQ_RPT_PO_VAL);
		list.add(ReportConst.EMR_PROCUR_RPT_PO_VAL);
		
		return isMatch(reportValue, list);
	}
	
	public boolean isReportValueFL6(){
		
		String[] strArr= new String[2];
		
		strArr[0] = ReportConst.ROLE_CODE_CT_APPROVER;
		strArr[1] = ReportConst.ROLE_CODE_RN_APPROVER;
		
		return isMatch(reportValue, strArr);
	}
	
	public boolean isRequisitioner(){
		return highRoleCodeList.contains(ReportConst.ROLE_CODE_REQUISITIONER);
	}
	
	public boolean isReceivingOfficer(){
		return highRoleCodeList.contains(ReportConst.ROLE_CODE_RECEIVING_OFFICER);
	}
	
	public void zoneByRolecode() {

		String[] reportArr = { 
				ReportConst.SM_REG_COMPANY_IN_MOF_RPT_VAL,
				ReportConst.SM_SUPPL_STAT_BY_CATEGORY_RPT_VAL,
				ReportConst.SM_SUPPL_STAT_RPT_VAL,
				ReportConst.SM_QUESTIONAIRE_RPT_VAL,
				ReportConst.SM_REG_COMPANY_IN_MOF_DET_RPT_VAL,
				ReportConst.SM_REG_COMPANY_IN_MOF_PROJ_RPT_VAL,
				ReportConst.SM_REG_COMPANY_IN_MOF_CONT_RPT_VAL,
				ReportConst.SM_REG_COMPANY_IN_MOF_DET_CONT_RPT_VAL,
				ReportConst.SM_REG_COMPANY_IN_MOF_PROJ_CONT_RPT_VAL,
				ReportConst.SM_QUESTIONNAIRE_RESPONSE_RPT_VAL,
				ReportConst.SM_SITE_VISIT_APPL_REQUIRE_RPT_VAL,
				ReportConst.SM_SITE_VISIT_APPL_OFFICER_RPT_VAL,
				ReportConst.SM_SITE_VISIT_APPL_PERFORMED_RPT_VAL,
				ReportConst.SM_LIST_OF_APPLICATION_RPT_VAL,
				ReportConst.SM_LIST_OFFLINE_SUPP_DOC_RPT_VAL,
				ReportConst.SM_LIST_OF_COMPANY_SUPTYPE_RPT_VAL,
				ReportConst.SM_LIST_OF_COMP_DIR_OWN_CEO_RPT_VAL,
				ReportConst.SM_LIST_OF_COMP_SORT_ALPHA_RPT_VAL,
				ReportConst.SM_STATISTICS_COMPANYTYPE_CAP_RPT_VAL,
				ReportConst.SM_LIST_OF_MOF_CANCELLATION_RPT_VAL,
				ReportConst.SM_BPK_USER_PROFILE_RPT_VAL,
				ReportConst.SM_BPK_ADMIN_PROFILE_RPT_VAL,
				ReportConst.SM_DELETE_PENDING_APPL,
				ReportConst.SM_PERSON_BRANCH_BANK,
				ReportConst.SM_THRESHOLD,
				ReportConst.SM_TOT_NUM_YEARLY_APPL_RPT_VAL,
				ReportConst.SM_LST_MONTH_WISE_RPT_VAL,
				ReportConst.SM_APPLICATION_APPROVED_RPT_VAL,
				ReportConst.SM_APPLICATION_REJECTED_RPT_VAL,
				ReportConst.SM_LIST_OF_GST_RGSTRD_SUPPLIER,
				ReportConst.SM_VIRTUAL_DAY_REPORT_RPT_VAL};
		
		String roleArr[] = {
				ReportConst.ROLE_CODE_UPS_PO,
				ReportConst.ROLE_CODE_UPS_APPROVER,
				ReportConst.ROLE_CODE_UPS_ADMIN,
				ReportConst.ROLE_CODE_UPS_SVO,
				ReportConst.ROLE_CODE_UPS_SVC,
				ReportConst.ROLE_CODE_UPS_SDO
			};
		
		String[] smReportArr = { 
			ReportConst.SM_APPL_CLIENT_CHARTER_RPT_VAL,
			ReportConst.SM_LIST_OF_COMP_DIR_OWN_CEO_RPT_VAL,
			ReportConst.SM_LIST_OF_DIR_OWN_CEO_COMPANY_RPT_VAL,
			ReportConst.SM_LIST_OF_COMP_SORT_ALPHA_RPT_VAL,
			ReportConst.SM_PERSON_BRANCH_BANK
		};
				
		String[] smRoleArr = {
			ReportConst.ROLE_CODE_MOF_SUPPLIER_ADMIN,
			ReportConst.ROLE_CODE_MOF_SUPPLIER_USER,
			ReportConst.ROLE_CODE_BASIC_SUPPLIER_USER,
			ReportConst.ROLE_CODE_BASIC_SUPPLIER_ADMIN
		};
		
		if(isModuleValueSM()){
		
			smZoneLabelView = false;
			smZoneView = false;
			smMandatoryZoneView = false;
			smMandatoryZoneForSVOSVLView = false;
			
			if(isMatch(reportValue, reportArr)){
				
				if(isMatch(allRoleCodesList, roleArr)){
					
					if (!isByZonelist.get(0).equals(new BigDecimal(1))) {

						smMandatorySupplierTypeView = false;
						smSupplierTypeView = false;
						
						smZone = stringZone;
						smZoneLabelView = true;
					
					}
				}
				
			} else if(isMatch(reportValue, smRoleArr)){
				
					smMandatorySupplierTypeView = false;
					smSupplierTypeView = false;
			
			} else if (!isByZonelist.get(0).equals(new BigDecimal(1))) {

				smZone = stringZone;
				smZoneLabelView = true;
						
			} else {
		
				if (!smListOfSVOSVLView) {
					
					smZoneView = true;
					smMandatoryZoneView = true;
					
				} else {
					smMandatoryZoneForSVOSVLView = true;
				}
			}
		}
	}
	
	private void checkToolTipsConditonForFLSubmodule() {
		
		if(isReportCategoryCC()){
			toolTipForCC = true;
		} else if(isReportCategoryPO()){
			toolTipForPO = true;
		} else if(isReportCategoryPA()){
			toolTipForPA = true;
		}
	}
	
	public List<String> retrieveHighRoleCodeByUser() {
		IPMReportSO reportSO = getPMReportSO();
		return reportSO.retrieveHighRoleCodeByUser(userId);
	}
	
	public List<String> retrieveAllRoleCodesByUser() {
		IPMReportSO reportSO = getPMReportSO();
		return reportSO.retrieveAllRoleCodesByUser(userId);
	}
	
	public List<SelectItem> retrieveZoneByUser() {
		IPMReportSO reportSO = getPMReportSO();
		List<Object[]> zoneLst = reportSO.retrieveZoneByUser(userId);
		return zoneList = assignListToSelectItemValueLabel(zoneLst);	
	}
	
	public List<SelectItem> retrieveProcessingOfficerList() {
	
		smState = null;
		
		this.retrieveStatesByZoneName(smZone);
		
		districtLst = new ArrayList<SelectItem>();
		
		IPMReportSO reportSO = getPMReportSO();
		
		List<Object[]> list = reportSO
				.retrieveProcessingOfficerList(smZone);
		
		return processingOfficerList = assignListToSelectItemValueLabel(list);
	
	}
	
	public void retrieveStatesByZoneName(String zoneName) {

		StateReqDTO stateReqDTO = new StateReqDTO();
		stateLst = new ArrayList<SelectItem>();

		IStateSO stateSO = BusinessDelegate.getInstance().getServiceObject(
				"pm", IStateSO.class);

		stateReqDTO.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);

		if (stateSO != null && zoneName != null) {
			
			stateReqDTO.setZoneName(zoneName);
			StateResDTO resStateDTO = stateSO
					.retriveStatesListByCountryCodeWithZoneName(stateReqDTO);
			
			for (PmState state : resStateDTO.getStateList()) {
				SelectItem selectItem = new SelectItem();
				selectItem.setLabel(state.getStateName());
				selectItem.setValue(state.getStateId());
				stateLst.add(selectItem);
			}
			
		} else if (stateSO != null) {
			
			StateResDTO resStateDTO = stateSO
					.retriveStatesListByCountryCode(stateReqDTO);
			
			for (PmState state : resStateDTO.getStateList()) {
				SelectItem selectItem = new SelectItem();
				selectItem.setLabel(state.getStateName());
				selectItem.setValue(state.getStateId());
				stateLst.add(selectItem);
			}
		}
	}
	
	public List<SelectItem> retrievePMServiceType() {
	
		IPMReportSO reportSO = getPMReportSO();
		
		List<Object[]> list = reportSO
				.retrievePMServiceType(strLanguage);
		
		return serviceTypeList = assignListToSelectItemValueLabel(list);
		
	}
	
	public void directPurchaseWithRequisitioner() {
	
		if(reportValue.equals(ReportConst.SC_SIMPLE_QUOTE_SMRY_BY_SUPPL_S1_RPT_VAL)){
			scSimpleQuoteDtlWithoutRequiGroupView = false;
			scSimpleQuoteDtlGroupView = false;
		}
				
		if(highRoleCodeList.contains(ReportConst.ROLE_CODE_REQUISITIONER)){
		
			scSimpleQuoteDtlWithoutRequiGroupView = true;
		
		} else {
			
			scSimpleQuoteDtlGroupView  = true;
		
		}
	}
	
	public void resetForm(){
		
	}
	
	public void enableCriteriaMenu() {
	
		if (!isEmptyReportValue()) {
			enableCriteriaMenuView = true;
		}
	}
	
	
	public List<SelectItem> retrieveSubModuleReportCategoryList(String language) {
		
		//reportList = new ArrayList<SelectItem>();
		//reportCategoryList = new ArrayList<SelectItem>();

		//reportName = "";
		//roleCode = "";
		//reportValue = "";

		//resetReportForm();
		
		IPMReportSO reportSO = getPMReportSO();
		
		List<Object[]> reportCatList = reportSO
				.retrieveSubModuleReportCategoryList(language,
						moduleValue, userId);
	
		return assignListToSelectItemLabelValue(reportCatList);
	}
	
	public List<SelectItem> assignListToSelectItemValueLabel(List<Object[]> list){

		List<SelectItem> selectItemList = new ArrayList<SelectItem>();
		
		if (CollectionUtils.isNotEmpty(list)) {
			
			for (Object[] object : list) {
			
				SelectItem selectItem = new SelectItem();
			
				selectItem.setValue(object[0].toString());
				selectItem.setLabel(object[1].toString());
				
				selectItemList.add(selectItem);
				
			}
		}
		
		return selectItemList;
	}	
	
	public List<SelectItem> assignListToSelectItemLabelValue(List<Object[]> list){

		List<SelectItem> selectItemList = new ArrayList<SelectItem>();
		
		if (CollectionUtils.isNotEmpty(list)) {
			
			for (Object[] object : list) {
			
				SelectItem selectItem = new SelectItem();
			
				selectItem.setLabel(object[0].toString());
				selectItem.setValue(object[1].toString());
				
				selectItemList.add(selectItem);
				
			}
		}
		
		return selectItemList;
	}	
	
	public IUserSO getUserSO(){
		return EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
	}
	
	public IPMReportSO getPMReportSO(){
		return EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IPMReportSO.class);
	}
	
	public IStateSO getStateSO(){
		return BusinessDelegate.getInstance().getServiceObject("pm", IStateSO.class);
	}
	
	public IOrgProfileSO getOrgProfileSO(){
		return BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
	}
	
	public IParameterSO getParameterSO(){
		return EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
	}
	
	public Long getUserId(){
		return new FacesScopes().getUserSessionBean().getUserId();
	}
	
	public boolean isModuleValueCM(){
		return moduleValue.equalsIgnoreCase(ReportConst.CM_MOD_VAL);
	}
	
	public boolean isModuleValueDP(){
		return moduleValue.equalsIgnoreCase(ReportConst.DP_MOD_VAL);
	}
	
	public boolean isModuleValueFL(){
		return moduleValue.equalsIgnoreCase(ReportConst.FL_MOD_VAL);
	}
	
	public boolean isModuleValueSM(){
		return moduleValue.equalsIgnoreCase(ReportConst.SM_MOD_VAL);
	}
	
	public boolean isModuleValueCT(){
		return moduleValue.equalsIgnoreCase(ReportConst.CT_MOD_VAL);
	}
	
	public boolean isModuleValuePP(){
		return moduleValue.equalsIgnoreCase(ReportConst.PP_MOD_VAL);
	}
	
	public boolean isModuleValuePM(){
		return moduleValue.equalsIgnoreCase(ReportConst.PM_MODULE_SER_OBJ);
	}
	
	public boolean isModuleValueSS(){
		return moduleValue.equalsIgnoreCase(ReportConst.SC_MOD_VAL);
	}
	
	public boolean isModuleValueOF(){
		return moduleValue.equalsIgnoreCase(ModuleCodeConst.ORDER_FULFILLMENT);
	}
	
	public boolean isModuleValueSSOrDP(){
		return moduleValue.equalsIgnoreCase(ReportConst.SC_MOD_VAL) 
		||moduleValue.equalsIgnoreCase(ReportConst.DP_MOD_VAL);
	}
	
	public boolean isModuleValueFLOrCT(){
		return isModuleValueFL() || isModuleValueCT();
	}
	
	public boolean isModuleValueOFOrCT(){
		return isModuleValueOF() || isModuleValueCT();
	}	
	
	public boolean isModuleValuePPOrCT(){
		return isModuleValuePP() || isModuleValueCT();
	}
	
	public boolean isModuleValuePPOrCMorOF(){
		return isModuleValuePP() || isModuleValueCM() || isModuleValueOF();
	}
		
	public boolean isReportCategoryCC(){
		return reportCategory.equals(
				ReportConst.FL_SUB_MOD_CENTRAL_VAL);
	}
	
	public boolean isReportCategoryPO(){
		return reportCategory.equals(
				ReportConst.FL_SUB_MOD_PURCHASE_VAL);
	}
	
	public boolean isReportCategoryPA(){
		return reportCategory.equals(
				ReportConst.FL_SUB_MOD_PURCHASE_VAL);
	}
	
	public boolean isEmptyModuleValue(){
		return moduleValue.isEmpty();
	}
	
	public boolean isEmptyReportValue(){
		return reportValue.isEmpty();
	}

	//	public boolean isEmptyReportName(){
//		return reportName.isEmpty();
//	}
	
	public boolean isEmptyReportCategory(){
		return reportCategory.isEmpty();
	}
	
//	public boolean isEmptyReportList(){
//		return CollectionUtils.isNotEmpty(reportList);
//	}
//	
//	public boolean isEmptyModuleList(){
//		return CollectionUtils.isNotEmpty(moduleList);
//	}
//	
//	public boolean isEmptyReportCategoryList(){
//		return CollectionUtils.isNotEmpty(reportCategoryList);
//	}
	
	public boolean isEnglish(){
		return strLanguage.equalsIgnoreCase(ReportConst.ENG_LANG);
	}
	
	public boolean isMalay(){
		return strLanguage.equalsIgnoreCase(ReportConst.MALAY_LANG);
	}
	
	public boolean isMatch(String value, String[] matchValue){
		
		for(int i = 0; i < matchValue.length; i++){
			
			if(value.equals(matchValue[i])){
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isMatch(String value, List<String> list){
		
		for(int i = 0; i < list.size(); i++){
			
			if(value.equals(list.get(i))){
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isMatch(List<?> list,  String[] matchValue){
		
		for(int i = 0; i < matchValue.length; i++){
			
			if(list.contains(matchValue[i])){
				return true;
			}
		}
		
		return false;
	}
	
	public void resetReportForm() {
		
		assignNewCompanyExperience(new ArrayList<SmSupplierCategory>());
		
		if (!isEmpty(moduleValue)) {
			
			cmMofRegView = false;
			cmBasicRegView = false;
			smMofRegView = false;
			smBasicRegView = false;
			cmExtentionBrandView = false;
			cmExtentionTypeView = false;
			cmExtentionMeasurmentView = false;
			cmExtentionColourView = false;
			selectedOrgProfile = null;
			//fromDate = null;
			//toDate = null;
			scQuotationId = null;
			scQuotationNo = null;			
			scEstimatedAmount = null;			
			scPaymentId = null;
			loaReportDocumentList = new ArrayList<ScLoaReport>();
			//scLOAIdNo = null;
			cmItemType = null;
			cmItemStatus = null;
			cmUnspscCode = null;
			cmUnspscTitle = null;
			cmCompanyName = null;
			selectedOrgProfile = null;
			cmRegistrationType = null;
			cmItemCategory = null;
			cmItemName = null;
			cmRequestNumber = null;
			cmEpNumber = null;
			cmBusinessRegNo = null;
			cmIcNumber = null;
			cmMofNumber = null;
			cmExtentionCode = null;
			cmExtentionType = null;
			cmExtentionBrand = null;
			cmExtentionMeasurment = null;
			cmExtentionColour = null;
			cmProofIdentity = null;
			cmSupplOrAdmin = null;
			cmGroupOne = null;
			cmExtentionBrand = null;
			cmExtentionType = null;
			cmExtentionMeasurment = null;
			cmExtentionColour = null;
			
			brandList = new ArrayList<SelectItem>();
			extTypeList = new ArrayList<SelectItem>();
			measurmentList = new ArrayList<SelectItem>();
			colorList = new ArrayList<SelectItem>();
			
			flItmType = null;
			commonPtj = null;
			flContractNo = null;
			flContractSystem = null;
			flSst = null;
			flJabatan = null;
			commonMinistry = null;
			commonPegaWaiPengawal = null;
			commonKumpulanPTJ = null;
			commonPtj = null;
			flSupplier = null;
			flStatus = null;
			flDuration = null;
			flRecievingOfficer = null;
			flDeliveryAddress = null;
			flRecNoteNo = null;
			flContractType = null;
			flPhysicalContractNo = null;
			flLoaPhysicalNo = null;
			flContractRequestNo = null;
			flRequisitioner = null;
			flAcknowledgeOfficer = null;
			flPaymentofficer = null;
			flMtoContract = null;
			flInvoiceNo = null;
			flDeductionType = null;
			flCrNo = null;
			flApproverName = null;
			flDocumentType = null;
			flCriteria = null;
			flScoring = null;
			flPkpNo = null;
			flStopInstructionNo = null;
			flInvoiceNo = null;
			flSupplierInvoiceNo = null;
			flPaymentAdviceNo = null;
			flDeliveryOrderNo = null;
			flSupplierDeliveryOrderNo = null;
			flItemCode = null;
			flPoAmount = null;
			flPrNo = null;
			flPmo = null;
			flItemName = null;
			selectedMinistry = null;
			selectedPegwaiPengawal = null;
			selectedKumpulanPTJ = null;
			selectedPTJ = null;
			smApplicationNo = null;
			
			if ((CollectionUtils.isNotEmpty(allRoleCodesList))
					&& (allRoleCodesList
							.contains(ReportConst.ROLE_CODE_EP_ADMIN) || allRoleCodesList
							.contains(ReportConst.ROLE_CODE_PTJ_ADMIN))) {
				smZone = null;
			}
			
			smSupplierType = null;
			smRTReportType = null;
			smSupplierName = null;
			//smMofRegistrationFeeList = new ArrayList<SelectItem>();
			//smProcessingFeeList = new ArrayList<SelectItem>();
			//smSoftCertificateFeeList = new ArrayList<SelectItem>();
			smMofRegNo = null;
			smRegistrationStatus = null;
			//smBuscompTypeRegistrationStatus = null;
			//egistrationStatusListOfBuscompType = new ArrayList<SelectItem>();
			//this.smBuscompanyTypeList = new ArrayList<SelectItem>();
			smBusPaidUpCapitalFrm = null;
			smBusPaidUpCapitalTo = null;
			smcategory = null;
			smCode = null;
			//smBuscompanyType = null;
			smMainCategory = null;
			smSubCategory = null;
			smSubCategoryDivision = null;
			smApplicationStatus = null;
			smPaymentType = null;
			smApplicationType = null;
			smZone = null;
			smState = null;
			smDistrict = null;
			smSVOUser = null;
			smReportType = null;
			smProcessingType = null;
			smReceiptNo = null;
			smProcessingOfficerName = null;
			smPaymentMethod = null;
			smFromMonth = null;
			smToMonth = null;
			smCompanyName = null;
			smPaymentTo = null;
			smRegistrationType = null;
			smCreatedFromYear = null;
			smCreatedToYear = null;
			smYearText = null;
			smMofRegNoFrom = null;
			smMofRegNoTo = null;
			smPaidUpCapitalFrm = null;
			smPaidUpCapitalTo = null;
			smPaidUpCapitalNaccum = null;
			smRole = null;
			smEpNo = null;
			smRegistrationNo = null;
			smSupportingDocumentMode = null;
			smPersonnelRole = null;
			smSupportingDocumentStatus = null;
			smIdentificationType = null;
			smIdentificationNumber = null;
			smCancellationBy = null;
			smThresholdFromYear = null;
			smThresholdToYear = null;
			smRequestExpiryRenewalDate = null;
			smUserName = null;
			smProfileStatus = null;
			smGSTRegisteredStatus = null;
			flContractOrder = null;
			flContractRequest = null;
			flPurchaseOrderNo = null;
			scCreatedToYear = null;
			scCreatedToMonth = null;
			scCreatedFromYear = null;
			scCreatedFromMonth = null;
			scDeskOfficer = null;
			scPtj = null;
			scStatus = null;
			scPublishFromDate = null;
			scPublishToDate = null;
			scSoneCrtdFromDate = null;
			scSoneCrtdToDate = null;
			scRequisenerId = null;
			scSupplier = null;
			scLogin = null;
			scPIno = null;
			scSoneStatus = null;
			scPrcRptGroup = null;
			scPIDtlsPrcRptGroup = null;
			scProcurementType = null;
			scProcessRptQuotationNo = null;
			scCommonStatus = null;
			scPurchaseInDefaultStatus = null;
			scSimpleQuoteStatus = null;
			scRequestNoteStatus = null;
			scLAPRNPURCHDEFAULTGroup = null;
			scSimpleQuoteDtlGroup = null;
			scREQUESTNOTESUMMARYGroup = null;
			scPIDNo = null;
			scContractOrPKPType = null;
			//sscContractOrPKPNo = null;
			scContractOrPKPName = null;
			scRequestQuoteNo = null;
			scSOAStatus = null;
			scSimpleQuoteType = null;
			scLOANo = null;
			scProcureAllList = null;
			scQuotationList = null;
			scTenderList = null;
			scRstrctdTndrList = null;
			ctContractNo = null;
			ctPhysicalContractNo = null;
			ctAgreement = null;
			ctStatus = null;
			ctStatusTwo = null;
			ctTerminationType = null;
			ctComplete = null;
			ctGroupOne = null;
			ctGroupTwo = null;
			ctGroupThree = null;
			ctProductService = null;
			ctLoanNo = null;
			ctLOAPhysicalNo = null;
			ctSubType = null;
			ctUserType = null;
			ctProcurementMode = null;
			ctCircularNo = null;
			ctCircularName = null;
			ctCircularType = null;
			ctCircularEffectiveFromDate = null;
			ctCircularEffectiveToDate = null;
			ctSupplierName = null;
			ctSubTypeList = new ArrayList<SelectItem>();
			flGroupOne = null;
			flGroupTwo = null;
			flGroupThree = null;
			flGroupFour = null;
			flGroupFive = null;
			flGroupSix = null;
			flGroupSeven = null;
			flGroupEight = null;
			flGroupNine = null;
			flGroupTen = null;
			flGroupEleven = null;
			flGroupTwelve = null;
			flGroupThirteen = null;
			flGroupFourteen = null;
			flGroupFifteen = null;
			flGroupSixteen = null;
			flGroupSeventeen = null;
			ppMinistry = null;
			ppPTJ = null;
			ppYear = null;
			//ministryName = null;
			pmServiceType = null;
			pmBusinessRegNo = null;
			pmStatus = null;
			pmFinancialOrgName = null;
			pmEpNo = null;
			pmOrgType = null;
			pmOrgName = null;
			pmOrgCode = null;
			
			selectedItem = null;
			selectedUnspscItem = null;
			//selectedOrgType(new PmParameter());		
			//selectedOrgProfile(new PmOrgProfile());
			//selectedRoleDesc(new PmRoleDesc());
			disabledOrgFields = false;
			pmItemName = null;
			pmItemCode = null;
			pmCompanyName = null;
			selectedPmUser = null;
			pmIdentificationNo = null;
			pmUserName = null;
			selectedPmRoleDesc = null;
			pmRoleCode = null;
			pmContractNo = null;
			pmPhysicalContractNo = null;
			pmLOANo = null;
			pmPhysicalLOANo = null;
			pmZone = null;
			pmProfileStatus = null;
	
			retrieveDates();
			applicationStatusDateChange();
			getCatDropdownStatusList();
			checkLovsAndDefaultDates();
		}
	}
	
	public void retrieveDates() {
		
		SimpleDateFormat outputDateFormat = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		
		Date currentDate = new Date();
		
		String currentYear = outputDateFormat.format(currentDate).substring(6, 10);
				
		if(isModuleValueSS()){
			scCreatedFromYear = currentYear;
			scCreatedToYear = currentYear; 
		}	
	
		if(isModuleValueSS()){
			
			if(isReportValueSM()){
				
				smCreatedFromYearVar = currentYear;
				smCreatedToYearVar = currentYear;
				smThresholdToYear = currentYear;
				smMonthlyRptByYear = currentYear;
		
			}
		}
			
		if(isModuleValueDP()){
			
			scSoneCrtdToDate = currentDate;
			
			Date newDate = new Date();
			
			newDate.setMonth(0);
			newDate.setDate(1);
			
			scSoneCrtdFromDate = newDate;
		}
	}
	
	
	
	private void assignValReqdToTrue() {
	
		showParentMsg = false;
		valueReqd = true;
		
		if(reportValue.equals(ReportConst.SM_CAT_STATIC_BY_STATE_RPT_VAL)) {
			getCatDropdownStatusList();
		} else {
			mainCatValueReqd = true;
			subCatValueReqd = false;
			subCatDivisionValueReqd = false;
		}
	}
	
	private void getCatDropdownStatusList() {
		
		if(isEmpty(smState)){
			mainCatValueReqd = true;
			subCatValueReqd = true;
			subCatDivisionValueReqd = true;
		} else {
			mainCatValueReqd = true;
			subCatValueReqd = false;
			subCatDivisionValueReqd = false;	
		}
	}
	
	private void resetChildCatList() {
		
	}
	
	public boolean isEmpty(String str){
		return StringUtils.isNotEmpty(str);
	}
	
	public void applicationStatusDateChange() {
		
		smMandatoryExpectedSiteVisitDateView = false;
		smMandatoryActualSiteVisitDateView = false;

		if(reportValue.equals(ReportConst.SM_SITE_VISIT_APPL_PERFORMED_RPT_VAL)) {
			
			if(smApplicationStatus.equals(ReportConst.PEND_PRPR_SITE_VISIT)) {
				
				smMandatoryFromDateView = true;
				smMandatoryExpectedSiteVisitDateView = true;
				smMandatoryDateView = false;
				
			} else if (smApplicationStatus.equals(ReportConst.SITE_VISIT_COMPLTD)) {
				
				smMandatoryFromDateView = true;
				smMandatoryActualSiteVisitDateView = true;
				smMandatoryDateView = false;
			}
		}
	}
	
	public boolean isReportValueSM(){
		
		List<String> list = new ArrayList<String>();
		
		list.add(ReportConst.SM_SUPPLIER_PERFORMANCE);
		list.add(ReportConst.SM_THRESHOLD);
		list.add(ReportConst.SM_THRESHOLD_VALUE);
		list.add(ReportConst.SM_LST_MONTH_WISE_RPT_VAL);		
				
		return isMatch(reportValue, list);
	}
	
	private void checkLovsAndDefaultDates() {
		
		if(isModuleValuePPOrCT()){
			//checkEmptyValuesForLovs();
		}
		else if(isModuleValuePPOrCT()){
			//checkEmptyValuesForLovs();
		}
		else if(isModuleValuePPOrCT()){
			//getDefaultValuesFromAndToDate(this.getManagedBean().getReportCode());
		}
	}
	
	public List<SelectItem> retrieveUPSSVOUser() {
		log.info("ReportBackingBean-retrieveUPSSVOUser");
		smUPSSVOUserList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<Object[]> ptLst = reportSO.retrieveUPSSVOUser(lngUserId);
			if (CollectionUtils.isNotEmpty(ptLst)) {
				for (Object[] object : ptLst) {
					SelectItem selectItem = new SelectItem();
					selectItem.setLabel(object[0].toString());
					selectItem.setValue(object[1]);
					smUPSSVOUserList.add(selectItem);
				}
			}
		}
		return smUPSSVOUserList;
	}
	
	public void retrieveIdentificationType() {
		log.info("ReportBackingBean-retrieveIdentificationType");
		identificationTypeList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);

			List<Object[]> identificationTypeLst = reportSO
					.retrieveIdentificationType();
			if (CollectionUtils.isNotEmpty(identificationTypeLst)) {
				for (Object[] object : identificationTypeLst) {
					SelectItem selectItem = new SelectItem();
					selectItem.setValue(object[0].toString());
					selectItem.setLabel(object[1].toString());
					identificationTypeList.add(selectItem);
				}
			}
		}
	}
	
	public void retrieveAccumulatedPaidUpCptl() {
		
		accumulatedPaidUpCptlList = new ArrayList<SelectItem>();
	
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);

			List<Object[]> accumulatedPaidUpCptlLst = reportSO
					.retrieveAccumulatedPaidUpCptl();
			if (CollectionUtils.isNotEmpty(accumulatedPaidUpCptlLst)) {
				for (Object[] object : accumulatedPaidUpCptlLst) {
					SelectItem selectItem = new SelectItem();
					selectItem.setValue(object[0].toString());
					selectItem.setLabel(object[1].toString());
					accumulatedPaidUpCptlList.add(selectItem);
				}
			}
		}
	}
	
	public List<SelectItem> retrieveRegStatusList() {
		
		IPMReportSO reportSO = getPMReportSO();
		List<Object[]> list = reportSO.retrieveRegStatusList(ReportConst.ENG_LANGUAGE_CODE);
		
		return registrationStatusList = assignListToSelectItemValueLabel(list);
	}
	
	public List<SelectItem> retrievePaymentTypeList() {
		log.info("ReportBackingBean-retrievePaymentTypeList");
		paymentTypeList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<Object[]> regStatusList = reportSO
					.retrievePaymentTypeList(strLanguage);
			if (CollectionUtils.isNotEmpty(regStatusList)) {
				for (Object[] object : regStatusList) {
					SelectItem selectItem = new SelectItem();
					selectItem.setValue(object[1].toString());
					selectItem.setLabel(object[0].toString());
					paymentTypeList.add(selectItem);
				}
			}
		}
		return paymentTypeList;
	}
	
	public List<String> retrievePaymentMethod() {
		log.info("ReportBackingBean-retrievePaymentMethod");
		paymentMethodList = new ArrayList<String>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<String> payMethodLst = reportSO.retrievePaymentMethod();
			if (CollectionUtils.isNotEmpty(payMethodLst)) {
				for (int i = 0; i < payMethodLst.size(); i++) {
					paymentMethodList.add(payMethodLst.get(i));
				}
			}
		}
		return paymentMethodList;
	}
	
	public void changeSoftCertSelection() {
	
		smSoftCertExpDateView = false;
		smSoftCertRenewalDateView = false;
	
		smNonMandatoryDateView = true;
		smSoftCertReqDateView = true;
	
		String[] matchArr = {"R", "E", "N"};
		
		
		if(!isMatch(smRequestExpiryRenewalDate, matchArr)){
			smRequestExpiryRenewalDate = "R";
		}
	
	}
	
	public void supplierTypeConsultant() {
	
		if(reportValue.equals(ReportConst.SM_LIST_OF_COMPANY_SUPTYPE_RPT_VAL)){
			
			if (!isEmpty(smSupplierType)) {
				
				if(smSupplierType.equals(ReportConst.SUPPL_CONSULT_TYPE)){
					
					retrieveConsultantBusCompanyData();
					retrieveRegStatusListForContract();
					
					supplierParentCatList = retrievCategoryListBySupplierType(smSupplierType);
			
				}
			
				if(smSupplierType.equals(ReportConst.SUPPL_CONTRACT_TYPE)){
				
					retrieveContractBusCompanyData();
					retrieveRegStatusListForContract();
					
					supplierParentCatList = retrievCategoryListBySupplierType(smSupplierType);
				}
				
			} else {
			
				retrieveAllBusCompanyData();
				retrieveRegStatusListForContract();
				
				for (SmSupplierCategory cat : selectedCatList) {
					
					cat.setPmCategoryL1(new PmCategoryL1());
					cat.setPmCategoryL2(new PmCategoryL2());
					cat.setPmCategoryL3(new PmCategoryL3());
					
				}
				
				supplierParentCatList = retrieveParentCategoryListForExperiencePage();
			}
			
		} 
		
		if(reportValue.equals(ReportConst.SM_REG_COMPANY_IN_MOF_RPT_VAL)){
			
			if (!isEmpty(smSupplierType)) {
				
				if(smSupplierType.equals(ReportConst.SUPPL_CONSULT_TYPE)){
					
					smMofRegNo = ReportConst.SUPPL_CONSULT_MOF_NO;
				
				}
			
				if(smSupplierType.equals(ReportConst.SUPPL_CONTRACT_TYPE)){
					
					smMofRegNo = ReportConst.SUPPL_CONTRACT_MOF_NO;
				}
			
			} else {
				
				smMofRegNo = null;
				
			}
		}
		
		if(reportValue.equals(ReportConst.SM_LIST_OF_DIR_OWN_CEO_COMPANY_RPT_VAL)){

			if (smSupplierType.equalsIgnoreCase(
					ReportConst.SUPPL_CONSULT_TYPE)) {
			
				smMofRegNoFrom = ReportConst.SUPPL_CONSULT_MOF_NO;
				smMofRegNoTo = ReportConst.SUPPL_CONSULT_MOF_NO;
		
			} else if (smSupplierType.equalsIgnoreCase(
					ReportConst.SUPPL_CONTRACT_TYPE)) {
			
				smMofRegNoFrom = ReportConst.SUPPL_CONTRACT_MOF_NO;
				smMofRegNoTo = ReportConst.SUPPL_CONTRACT_MOF_NO;
			}
		}
	}
		
		public List<SelectItem> retrieveConsultantBusCompanyData() {
			
			IPMReportSO reportSO = getPMReportSO();
			List<Object[]> retrieveConsultantList = reportSO.getConsultantBusCompanyData(strLanguage);
			return assignListToSelectItemValueLabel(retrieveConsultantList);	
		}
		
		public List<SelectItem> retrieveContractBusCompanyData() {

			smBusCompanyType = null;
			
			IPMReportSO reportSO = getPMReportSO();
			List<Object[]> retrieveContractList = reportSO.getContractBusCompanyData(strLanguage);
		
			return assignListToSelectItemValueLabel(retrieveContractList);
		}

	public List<SelectItem> retrieveAllBusCompanyData() {
		
		smBusCompanyType = null;
		smBusCompanyPaidUpAccumlatedShareView = true;
		smPaidAccmltdShareCptlSelView = true;
				
		smAccmltdCptlSelView = false;
		smPaidUpCptlSelView = false;
		smAccmltdShareSelView = false;

		IPMReportSO reportSO = getPMReportSO();
		
		List<Object[]> retrieveAllList = reportSO.getAllBusCompanyData(strLanguage);
		
		return assignListToSelectItemValueLabel(retrieveAllList);
	}
	
	public List<PmCategoryL1> retrievCategoryListBySupplierType(
			String supplierType) {
	
		supplierParentCatList = new ArrayList<PmCategoryL1>();
		
		for (SmSupplierCategory cat : selectedCatList) {
			cat.setPmCategoryL1(new PmCategoryL1());
			cat.setPmCategoryL2(new PmCategoryL2());
			cat.setPmCategoryL3(new PmCategoryL3());
		}

		CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
		reqDTO.setSupplierTypeCode(supplierType);

		ICategoryCodeSO reqSO = getCategoryCodeSO();
		
		CategoryCodeResDTO catCodeResDTO = reqSO
				.retrieveCategoryL1ListBySupplierType(reqDTO);
		
		if (catCodeResDTO != null) {
			return catCodeResDTO.getCategoryL1List();
		} else {
			return null;
		}
	}

	public List<PmCategoryL1> retrieveParentCategoryListForExperiencePage() {

		ICategoryCodeSO reqSO = getCategoryCodeSO();
		
		CategoryCodeResDTO catCodeResDTO = reqSO.retrieveCategoryL1List();
		
		if (catCodeResDTO != null) {
			return catCodeResDTO.getCategoryL1List();
		}
		
		return null;
	}
	
	public ICategoryCodeSO getCategoryCodeSO(){
		 return (ICategoryCodeSO) BusinessDelegate
				.getInstance().getServiceObject(ReportConst.PM_MODULE_SER_OBJ,
						ICategoryCodeSO.class);
	}
	
	public SelectItem[] getOrgTypeDropdownValues() {

//		ISearchBean<PmParameterDesc> searchBean1 = null;
//		
//		SelectItem[] federalItems = null;
//		SelectItem[] otherItems = null;
//		
//		pmOrgType = null;
//		itemsList = null;
//
//		if (reportValue.equals(ReportConst.PM_LIST_OF_ORGANIZATION_RPT_VAL)) {
//			
//			if(hasRole(RoleCode.AUDITOR_GENERAL.value()) 
//					|| hasRole(RoleCode.AUDITOR_INTERNAL.value())){
//				
//				SelectItem[] items = new SelectItem[1];
//				
//				items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
//						"lbl.txt.select",
//						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
//						getFacesScopes().getUserLocale()));
//				
//				if(!pmOrgCategory.equals(ReportConst.OTHERS)){
//					pmOrgCategory = ReportConst.FEDERAL_GOVT;
//				}
//				
//				itemsList = items;
//			}
//				
//		} else {
//				if (!isEmpty(pmOrgCategory)
//						&& pmOrgCategory.equals(ReportConst.FEDERAL_GOVT)) {
//					
//					searchBean1 = getOrgTypeSearchBean("orgTypeFederalList");
//
//					if (searchBean1 != null) {
//						searchBean1.getSortColumns();
//
//						itemsList = getDropdownValuesBySearchBean(
//								ParameterTypeConst.ORGANISATION_TYPES,
//								searchBean1);
//					}
//
//				} else if (StringUtils.isNotEmpty(pmOrgCategory)
//						&& pmOrgCategory.equals(ReportConst.OTHERS)) {
//				
//					searchBean1 = getOrgTypeSearchBean("orgTypeOthersList");
//					
//					if (searchBean1 != null) {
//						searchBean1.getSortColumns();
//						itemsList = getDropdownValuesBySearchBean(
//								ParameterTypeConst.ORGANISATION_TYPES,
//								searchBean1);
//					}
//					
//				} else {
//					
//					searchBean1 = getOrgTypeSearchBean("orgTypeFederalList");
//					
//					pmOrgCategory = ReportConst.FEDERAL_GOVT;
//					
//					if (searchBean1 != null) {
//						searchBean1.getSortColumns();
//
//						itemsList = getDropdownValuesBySearchBean(
//								ParameterTypeConst.ORGANISATION_TYPES,
//								searchBean1);
//					}
//				}
//			}
//		
//		return itemsList;
		
		return null;
	}
	
	public SelectItem[] getDropdownValuesBySearchBean(String parameterType,
			ISearchBean<PmParameterDesc> searchBean) {
	
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();
		
		try {
		
			List<PmParameterDesc> parameterDescs = ParameterHelper
					.getInstance().getParameterDescListBySearchBean(
							parameterType, searchBean);

			items = new SelectItem[parameterDescs.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale()));
			
			int i = 1;
			
			for (PmParameterDesc parameterDesc : parameterDescs) {
				items[i++] = new SelectItem(parameterDesc.getPmParameter()
						.getParameterId(), parameterDesc.getCodeName());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}

		return items;
	}
	public FacesScopes getFacesScopes(){
		//return facesScopes;
		return null;
	}
		
	public boolean hasUserId(){
		
		if(userId < 0 ){
			return false; 
		} 
		
		return true;
	}
	
	public boolean hasRole(String roleValue){
		return getFacesScopes().hasRoleUser(roleValue);	
	}
	
	private void getDefaultValuesFromAndToDate(String reportCode) {
		
	}
	
	public void madatoryIndicator() {
	
		if(isModuleValueCM()){

			cmMadatoryIndicatorView = true;
			madatoryIndicatorView = false;

		}
		
		if(isModuleValuePPOrCMorOF()){
					
			cmMadatoryIndicatorView = false;
			madatoryIndicatorView = true;
			instructionInfoView = true;

		}
		
		if (isModuleValueDP()) {
			
			dpInstructionInfoView = true;
			
		} else {
			
			cmMadatoryIndicatorView = false;
			madatoryIndicatorView = true;
			
		}
	
		if(reportValue.equals(ReportConst.SM_LAPORAN_TERIMAAN_HARIAN_RPT_VAL)){
			madatoryIndicatorView = true;
		}	
	}
	
	public List<String> retrieveRoleCodeFromParamTable() {
	
		IPMReportSO reportSO = getPMReportSO();
		
		List<String> list = reportSO.retrieveRoleCodeFromParamTable();
			
		return paramTableRoleCodeList = list;
	}
	
	public void retrieveAllMinistry() {
	
		ptjList = new ArrayList<SelectItem>();
		ministryList = new ArrayList<SelectItem>();
		
		retrieveRoleCodeFromParamTable();
		
		if (paramTableRoleCodeList.contains(highRoleCodeList.get(0))) {
		
			commonLOVBasedRoles = true;
			getParameterCodeTypeString();
			flSupplierDropdownView = false;
			flSupplierTextView = false;
			
			if (parameterCodeType.equals(ReportConst.ROLE_CODE_SUP)) {
				
				retrieveSupplierCompName();
			
				if(!isEmpty(supplCompanyList) && flSupplierView){
				
					flSupplierDropdownView = true;
					
				} else if (supplCompanyList.size() == 0 && flSupplierView) {
					flSupplierTextView = true;
				}
				
				} else {
					
					if (flSupplierView) {
						flSupplierTextView = true;
					}
					
				}
			
				paramCodeSupplierGroupDropdown(parameterCodeType);
				
			} else {
				retrieveParameterCodeType();
			}
	
		if(isModuleValueCM()){
			
			ctSupplierTextView = false;
			ctSupplierDefaultView = false;
			ctGroupFiveWithoutSupplView = false;
			ctGroupFiveWithSupplView = false;
			
			if (reportValue.equals(ReportConst.CT_STATEMENT_OF_ORDER_REPORT_CC_PANEL_RPT_VAL)) {
				
				getParameterCodeTypeString();
				
				if (parameterCodeType.equals(ReportConst.ROLE_CODE_SUP)) {
					
					retrieveSupplierCompName();
					
					if (ctSupplierNameCPView) {
						
						ctSupplierDefaultView = true;
						
					} else if (flSupplierView) {

						ctSupplierTextView = true;
						
					}
					
					if (ctGroupFiveView) {
						
						ctGroupFiveWithoutSupplView = true;
						
					}
					
				} else {
					
					if (ctSupplierNameCPView) {
						
						ctSupplierTextView = true;
					}
					
					if (ctGroupFiveView) {
						
						ctGroupFiveWithSupplView = true;
					}
				}
				
			} else if (reportValue.equals(ReportConst.CT_PANEL_STATEMENT_OF_ORDER_REPORT_CC_PANEL_RPT_VAL)) {
				
				getParameterCodeTypeString();
				
				if (parameterCodeType.equals(ReportConst.ROLE_CODE_SUP)) {
				
					retrieveSupplierCompName();
					
					if (ctSupplierNameCPView) {
						
						ctSupplierDefaultView = true;
					
					} else if (flSupplierView) {
					
						ctSupplierTextView = true;
					}
					
				} else {
					
					if (ctSupplierNameCPView) {
						
						ctSupplierTextView = true;
					}
				}

			}

		}
	}
	
	public List<SelectItem> retrievePFPPList() {

		pegaWaiPengawalList = new ArrayList<SelectItem>();
		
		IPMReportSO reportSO = getPMReportSO();
		
		String ministryId = commonMinistry;
		
		List<Object[]> ptjLst = reportSO.retrievePFPPList(userId, ministryId);
		
		if (CollectionUtils.isNotEmpty(ptjLst)) {
			
			if (ptjLst.size() > 2) {
			
				SelectItem selectItem1 = new SelectItem();
				selectItem1.setLabel(ResourceBundleUtils.getMessage(
						"lbl.txt.select",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
				
				selectItem1.setValue(null);
				pegaWaiPengawalList.add(selectItem1);
			}
				
			for (Object[] object : ptjLst) {
			
				SelectItem selectItem = new SelectItem();
				selectItem.setLabel(object[0].toString());
				selectItem.setValue(object[1].toString());
				pegaWaiPengawalList.add(selectItem);
				
			}
		} 
		
		return pegaWaiPengawalList;
	}
	
	private boolean checkValue(String[] vars){
		
		for(int i = 0; i < vars.length; i++){
			
			if(vars[i].isEmpty()){
				return false;
			}
		}
		
		return true;
	}
	
	private boolean checkDefaultValuesForPP() {
	
		String[] vars = {	commonPtj, 
							commonMinistry, 
							commonPegaWaiPengawal, 
							commonKumpulanPTJ, 
							ppYear};
	
		return checkValue(vars);
	}
	
	private boolean checkDefaultValuesForPM() {
		
		String[] vars = {	pmServiceType,
							pmBusinessRegNo,
							pmStatus,
							pmFinancialOrgName,
							pmEpNo,
							pmOrgCategory,
							pmIdentificationNo,
							pmCompanyName,
							pmOrgType,
							pmContractNo,
							pmPhysicalContractNo,
							pmLOANo,
							pmPhysicalLOANo,
							pmRoleCode,
							pmUserName,
							pmItemCode,
							pmItemName,
							pmOrgCode,
							pmOrgName,
							pmZone,
							pmProfileStatus
						};
	
		return checkValue(vars);
	}
	
	
	
	public IRoleSO getRoleSO(){
		return BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IRoleSO.class);
	}
	
	
	boolean disableOrgFields;
	String parameterCode;
	
	public boolean isOrgProfileFFC(){
		return parameterCode.equals(ParameterConst.ORG_TYPE_FACTORING);
	}
	
	public boolean isOrgProfileSUP(){
		return parameterCode.equals(ParameterConst.ORG_TYPE_SUPPLIER);
	}
	
	public UserProfilesSearchBean getUserProfilesSearchBean(){
		return null;
	}
	
	private void setCurrentActiveOrg() {
	
		if (userId < 0) {
			throw new EpException(MessageConst.ERROR_770149);
		}
		
		boolean isFactoring = hasRole(RoleCode.FACTORING_ADMIN.value());
		
		List<PmUserOrg> userOrgList = PmCommonUtil
				.retrieveActiveOrgWithoutRole(userId, isFactoring);

		if (userOrgList == null || userOrgList.isEmpty()) {
			throw new EpException(MessageConst.ERROR_770105);
		}

		PmUserOrg pmUserOrg = userOrgList.get(0);
		PmOrgProfile pmOrgProfile = pmUserOrg.getPmOrgProfile();
		
		this.currentActiveOrg = pmOrgProfile;
	}

    public PmParameter retrieveParameterById(Long parameterId){
		
		PmParameter pmParameter = null;
		
		if (parameterId != null) {
		
			IParameterSO parameterSO = null;
			
			try {
			
				ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
				parameterReqDTO.setParameterId(parameterId);
				ParameterResDTO parameterResDTO = getParameterSO()
						.retrieveParameterById(parameterReqDTO);
				
				if(parameterResDTO != null && parameterResDTO.isSuccessful()){
					pmParameter = parameterResDTO.getPmParameter();
				}
				
			} finally {
				parameterSO = null;
			}
		}

		return pmParameter;
	
	}
    
	public void selectPtjAction(PmOrgProfile orgProfile){
	
//		this.orgProfile = orgProfile; 
//		
//		if(this.orgProfile != null){
//			this.pmOrgName = orgProfile.getPmOrgValidity().getOrgName();
//		}
	}
	
	private String getRoleCodes(){
	
		RoleCode[] roleCodes = {
			RoleCode.EP_ADMIN,
			RoleCode.EP_ADMIN_1,
			RoleCode.EP_ADMIN_2,
			RoleCode.MINISTRY_ADMIN,
			RoleCode.JABATAN_ADMIN,
			RoleCode.PTJ_ADMIN,
			RoleCode.BPK_ADMIN,
			RoleCode.UPS_ADMIN,
			RoleCode.PROCUREMENT_ADMIN,
			RoleCode.SUPPORT_ADMIN,
			RoleCode.AUDITOR_GENERAL,
			RoleCode.AUDITOR_INTERNAL};
		
		for(int i = 0; i<roleCodes.length; i++){
			if(getFacesScopes().hasRoleUser(roleCodes[i].value())){
				return roleCodes[i].value();
			}
		}
		
		return "";
	}

	public void populateToRoleSaveObject(){
		this.pmRoleCode = "";
	}
	
	public void preRenderView() {

		reportList = new ArrayList<SelectItem>();
		
		FacesContext fc = FacesContext.getCurrentInstance();
		
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, "rptcontentmain");
		
	}
	
	public void paramCodeSupplierGroupDropdown(String parameterCodeType) {
	
		if(parameterCodeType.equals(ReportConst.ROLE_CODE_SUP)) {
			
			if(flGroupOneView){
				
				flGroupOneWithoutSupplView = true;
				
			} else if (flGroupOneAdditionalView) {
				
				flGroupOneWithoutSupplView = true;
		}
			
		if(flGroupTwoView || flGroupTwoExtraView || flGroupTwoContractOrderView){
			
			flGroupTwoWithoutSupplView = true;
			
		}
			
		if(flGroupThreeView){
			
			flGroupThreeWithoutSupplView = true;
			
		}

		if (flGroupThreePaymentMatchView) {
			
			flGroupThreeWithoutSupplView = true;
			
		} else if (flGroupThreeContractOrderView) {
				
			flGroupThreeWithoutSupplView = true;
			
		} else if (flGroupThreeExtraView) {
			
			flGroupThreeWithoutSupplView = true;
			
		} else if (flGroupThreeNewView) {
				
			flGroupThreeWithoutSupplView = true;
					
		} else if (flGroupThreePaymentView) {
		
			flGroupThreeWithoutSupplView = true;
			
			} else if (flGroupThreePaymentCCView) {
				
				flGroupThreeWithoutSupplView = true;
				
			} else if (flGroupThreeAdditionalView) {
				
				flGroupThreeWithoutSupplView = true;
				
			} else if (flGroupFourView) {
				
				flGroupFourWithoutSupplView = true;
				
			} else if (flGroupFiveView) {
				
				flGroupFiveWithoutSupplView = true;
				
			} else if (flGroupSixView) {
				
				flGroupSixWithoutSupplView = true;
				
			} else if (flGroupSixExtraView) {
				
				flGroupSixWithSupplView = true;
				
			} else if (flGroupSixAdditionalView) {
				
				flGroupSixWithoutSupplView = true;
				
			} else if (flGroupSevenView) {
				
				flGroupSevenWithoutSupplView = true;
				
			} else if (flGroupSevenAdditionalView) {
				
				flGroupSevenWithoutSupplView = true;
				
			} else if (flGroupEightView) {
				
				flGroupEightWithoutSupplView = true;
				
			} else if (flGroupNineView) {
				
				flGroupNineWithoutSupplView = true;
				
			} else if (flGroupTenView) {
				
				flGroupTenWithoutSupplView = true;
				
			} else if (flGroupTenAdditionalView) {
				
				flGroupTenWithoutSupplView = true;
			
			} else if (flGroupElevenView) {
				
				flGroupElevenWithoutSupplView = true;
				
			} else if (flGroupTwelveView) {
				
				flGroupTwelveWithoutSupplView = true;
				
			} else if (flGroupTwelveAdditionalView) {
				
				flGroupTwelveWithoutSupplView = true;
				
			} else if (flGroupThirteenView) {
				
				flGroupThirteenWithoutSupplView = true;
				
			} else if (flGroupThirteenAdditionalView) {
				
				flGroupThirteenWithoutSupplView = true;
				
			} else if (flGroupThirteenAckRecView) {
				
				flGroupThirteenWithoutSupplView = true;
				
			} else if (flGroupFourteenView) {
				
				flGroupFourteenWithoutSupplView = true;
				
			} else if (flGroupFourteenExtraView) {
				
				flGroupFourteenWithoutSupplView = true;
				
			} else if (flGroupFourteenAdditionalView) {
				
				flGroupFourteenWithoutSupplView = true;
				
			} else if (flGroupFifteenView) {
				
				flGroupFifteenWithoutSupplView = true;
				
			} else if (flGroupSixteenView) {
				
				flGroupSixteenWithoutSupplView = true;
				
			} else if (flGroupSeventeenView) {
				
				flGroupSeventeenWithoutSupplView = true;
				
			} else if (flGroupEighteenView) {
				
				flGroupEighteenWithoutSupplView = true;
				
			} else if (flGroupEighteenAdditionalView) {
				
				flGroupEighteenWithoutSupplView = true;
				
			} else if (flGroupNineteenView) {
				
				flGroupNineteenWithoutSupplView = true;
				
			} else if (flGroupTwentyView) {
				
				flGroupTwentyWithoutSupplView = true;
						
			} else if (flGroupTwentyOneView) {
				
				flGroupTwentyOneWithoutSupplView = true;
				
			} else if (flGroupTwentyTwoView) {
				
				flGroupTwentyTwoWithoutSupplView = true;
				
			} else if (flGroupTwentyThreeView) {
				
				flGroupTwentyThreeWithoutSupplView = true;
				
			} else if (flGroupTwentyFourView) {
				
				flGroupTwentyFourWithoutSupplView = true;
				
			} else if (flGroupTwentyFiveView) {
				
				flGroupTwentyFiveWithoutSupplView = true;
				
			} else if (flGroupTwentyFiveAdditionalView) {
				
				flGroupTwentyFiveWithoutSupplView = true;
				
			} else if (flGroupTwentySixView) {
		
				flGroupTwentySixWithoutSupplView = true;
				
			} else if (flGroupTwentySixAdditionalView) {
				
				flGroupTwentySixWithoutSupplView = true;
		
			} else if (flGroupTwentySevenView) {
			
				flGroupTwentySevenWithoutSupplView = true;
			
			} else if (flGroupTwentySevenExtraView) {
			
				flGroupTwentySevenWithoutSupplView  = true;
			
			} else if (flGroupTwentySevenAdditionalView) {
				
				flGroupTwentySevenWithoutSupplView = true;
				
			}
			
		} else {
			
			if (flGroupOneView) {
				
				flGroupOneWithSupplView = true;
				
			} else if (flGroupOneAdditionalView) {
				
				flGroupOneWithSupplView =  true;
				
			} else if (flGroupTwoView) {
				
				flGroupTwoWithSupplView = true;
				
			} else if (flGroupTwoExtraView) {
				
				flGroupTwoWithSupplView = true;
				
			} else if (flGroupTwoAdditionalView) 
			{
				flGroupTwoWithSupplView = true;
				
			} else if (flGroupTwoContractOrderView) {
				
				flGroupTwoWithSupplView = true;
				
			} else if (flGroupThreeView) {
				
				flGroupThreeWithSupplView = true;
				
			} else if (flGroupThreePaymentMatchView) {
				
				flGroupThreeWithSupplView = true;
				
			} else if (flGroupThreeContractOrderView) {
				
				flGroupThreeWithSupplView = true;
				
			} else if (flGroupThreeNewView) {
				
				flGroupThreeWithSupplView = true;
				
			} else if (flGroupThreePaymentView) {
				
				flGroupThreeWithSupplView = true;
				
			} else if (flGroupThreePaymentCCView) {
				
				flGroupThreeWithSupplView = true;
				
			} else if (flGroupThreeExtraView) {
				
				flGroupThreeWithSupplView = true;
				
			} else if (flGroupThreeAdditionalView) {
				
				flGroupThreeWithSupplView = true;
				
			} else if (flGroupFourView) {
				
				flGroupFourWithSupplView = true;
				
			} else if (flGroupFiveView) {
				
				flGroupFiveWithSupplView = true;
				
			} else if (flGroupSixView) {
				
				flGroupSixWithSupplView = true;
				
			} else if (flGroupSixExtraView) {
				
				flGroupSixWithSupplView =  true;
				
			} else if (flGroupSixAdditionalView) {
				
				flGroupSixWithSupplView = true;
				
			} else if (flGroupSevenView) {
				
				flGroupSevenWithSupplView = true;
				
			} else if (flGroupSevenAdditionalView) {
				
				flGroupSevenWithSupplView = true;
				
			} else if (flGroupEightView) {
				
				flGroupEightWithSupplView = true;
				
			} else if (flGroupNineView) {
				
				flGroupNineWithSupplView = true;
				
			} else if (flGroupTenView) {
				
				flGroupTenWithSupplView = true;
				
			} else if (flGroupTenAdditionalView) {
				
				flGroupTenWithSupplView = true;
				
			} else if (flGroupElevenView) {
				
				flGroupElevenWithSupplView = true;
				
			} else if (flGroupTwelveView) {
				
				flGroupTwelveWithSupplView = true;
				
			} else if (flGroupTwelveAdditionalView) {
				
				flGroupTwelveWithSupplView = true;
				
			} else if (flGroupThirteenView) {
				
				flGroupThirteenWithSupplView = true;
				
			} else if (flGroupThirteenAdditionalView) {
				
				flGroupThirteenWithSupplView = true;
				
			} else if (flGroupThirteenAckRecView) {
				
				flGroupThirteenWithSupplView = true;
				
			} else if (flGroupFourteenView) {

				flGroupFourteenWithSupplView = true;
				
			} else if (flGroupFourteenExtraView) {
				
				flGroupFourteenWithSupplView = true;
				
			} else if (flGroupFourteenAdditionalView) {
				
				flGroupFourteenWithSupplView = true;
				
			} else if (flGroupFifteenView) {
				
				flGroupFifteenWithSupplView = true;
			
			} else if (flGroupSixteenView) {
				
				flGroupSixteenWithSupplView = true;
				
			} else if (flGroupSeventeenView) {
				
				flGroupSeventeenWithSupplView = true;
				
			} else if (flGroupEighteenView) {
				
				flGroupEighteenWithSupplView = true;
				
			} else if (flGroupEighteenAdditionalView) {
				
				flGroupEighteenWithSupplView = true;
				
			} else if (flGroupNineteenView) {
			
				flGroupNineteenWithSupplView = true;
			
			} else if (flGroupTwentyView) {
				
				flGroupTwentyWithSupplView = true;
				
			} else if (flGroupTwentyOneView) {
			
				flGroupTwentyOneWithSupplView = true;
			
			} else if (flGroupTwentyTwoView) {
				
				flGroupTwentyTwoWithSupplView = true;

			} else if (flGroupTwentyThreeView) {
				
				flGroupTwentyThreeWithSupplView = true;
				
			} else if (flGroupTwentyFourView) {
			
				flGroupTwentyFourWithSupplView = true;
			
			} else if (flGroupTwentyFiveView) {
				
				flGroupTwentyFiveWithSupplView = true;
				
			} else if (flGroupTwentyFiveAdditionalView) {
				
				flGroupTwentyFiveWithSupplView = true;
				
			} else if (flGroupTwentySixView) {
				
				flGroupTwentySixWithSupplView = true;
			
			} else if (flGroupTwentySixAdditionalView) {
			
				flGroupTwentySixWithSupplView = true;
			
			} else if (flGroupTwentySevenView) {
				
				flGroupTwentySevenWithSupplView = true;
				
			} else if (flGroupTwentySevenExtraView) {
				
				flGroupTwentySevenWithSupplView = true;
				
			} else if (flGroupTwentySevenAdditionalView) {
				
				flGroupTwentySevenWithSupplView = true;
				
			}
		}
	}
	
	
	public void changeSupplierTypeAndGetRegStatus() {
	
		if (reportValue.equals(
				ReportConst.SM_CATEGORY_STATISTIC_RPT_VAL)) {

			if (StringUtils.isNotEmpty(this.getSmSupplierType())) {
				if (this.getSmSupplierType().equalsIgnoreCase(
						ReportConst.SUPPL_CONSULT_TYPE)) {
					// retrieveRegStatusListForConsultant();
					retrieveRegStatusListForContract();
					this.setSupplierParentCatList(retrievCategoryListBySupplierType(this
							.getSmSupplierType()));
				} else if (this.getSmSupplierType().equalsIgnoreCase(
						ReportConst.SUPPL_CONTRACT_TYPE)) {
					retrieveRegStatusListForContract();
					this.setSupplierParentCatList(retrievCategoryListBySupplierType(this
							.getSmSupplierType()));
				}
			} else if (!reportValue.equals(
					ReportConst.SM_CATEGORY_OVERLAPPING_RPT_VAL)) {
				// retrieveRegStatusListForAll();
				retrieveRegStatusListForContract();
				for (SmSupplierCategory cat : this.getSelectedCatList()) {
					cat.setPmCategoryL1(new PmCategoryL1());
					cat.setPmCategoryL2(new PmCategoryL2());
					cat.setPmCategoryL3(new PmCategoryL3());
				}
				this.setSupplierParentCatList(retrieveParentCategoryListForExperiencePage());
			}
		}
	}
	
	
	
	private List<String> addingStateList = new ArrayList<String>();
	
	public List<String> getAddingStateList() {
		return addingStateList;
	}

	public void setAddingStateList(List<String> addingStateList) {
		this.addingStateList = addingStateList;
	}
	
	public void retrieveDistrict() {

		districtLst = new ArrayList<SelectItem>();
		addingStateList = new ArrayList<String>();
		
		IDistrictSO districtSO = (IDistrictSO) BusinessDelegate.getInstance()
				.getServiceObject(ReportConst.PM_MODULE_SER_OBJ,
						IDistrictSO.class);
		
		DistrictReqDTO districtReqDTO = new DistrictReqDTO();
		
		if (StringUtils.isNotEmpty(this.getSmState())) {
			districtReqDTO.setStateId(Long.parseLong(this.getSmState()));
			DistrictResDTO districtResDTO = districtSO
					.retriveDistrictListByStateID(districtReqDTO);
			if (CollectionUtils.isNotEmpty(districtResDTO.getDistrictList())) {
				for (PmDistrict pmDistrict : districtResDTO.getDistrictList()) {
					SelectItem selectItem = new SelectItem();
					selectItem.setLabel(pmDistrict.getDistrictName());
					selectItem.setValue(pmDistrict.getDistrictId());
					districtLst.add(selectItem);
				}
			}
			if (this.reportValue
					.equalsIgnoreCase(ReportConst.SM_CAT_STATIC_BY_STATE_RPT_VAL)) {
				this.setMainCatValueReqd(true);
				this.setSubCatValueReqd(true);
				this.setSubCatDivisionValueReqd(true);
			}
		} else {
			this.setMainCatValueReqd(true);
			this.setSubCatValueReqd(false);
			this.setSubCatDivisionValueReqd(false);
		}
		for (SmSupplierCategory cat : this.getSelectedCatList()) {
			cat.setPmCategoryL1(new PmCategoryL1());
			cat.setPmCategoryL2(new PmCategoryL2());
			cat.setPmCategoryL3(new PmCategoryL3());
		}
	}
	
	public void changeRegPaymentTypeAndReceiptDate() {
	
		if(isEmpty(smMofRegistrationFeeList) 
		|| isEmpty(smProcessingFeeList)
		|| isEmpty(smSoftCertificateFeeList)
		&& (this.getFromDate() != null
		&& this.getToDate() != null)){
			this.valuesEmpty = false;
		} else {
			this.valuesEmpty = true;
		}
		
	}
	
	public void checkDuplicateCategoryForProject(SmSupplierCategory newCat) {
		
		for (SmSupplierCategory cat : selectedCatList) {
		
			if ((cat.getPmCategoryL1().getCategoryL1Id() != null)
					&& (newCat.getPmCategoryL1().getCategoryL1Id() != null)
					&& (cat.getPmCategoryL2().getCategoryL2Id() != null)
					&& (newCat.getPmCategoryL2().getCategoryL2Id() != null)
					&& (cat.getPmCategoryL3().getCategoryL3Id() != null)
					&& (newCat.getPmCategoryL3().getCategoryL3Id() != null)) {
				
				if (newCat.equals(cat)) {
				
				} else if (cat.getPmCategoryL1().getCategoryL1Id()
						.equals(newCat.getPmCategoryL1().getCategoryL1Id())
						&& (cat.getPmCategoryL2().getCategoryL2Id()
								.equals(newCat.getPmCategoryL2()
										.getCategoryL2Id()))
						&& (cat.getPmCategoryL3().getCategoryL3Id()
								.equals(newCat.getPmCategoryL3()
										.getCategoryL3Id()))) {
				
					newCat.getPmCategoryL3().setCategoryL3Id(null);
					throw new EpException(MessageConst.ERROR_722533);
				}
			}
		}
	}

	public void deleteSelectedCodes() {

		for (SmSupplierCategory cat : this.getSelectCatDeletionList()) {
			selectedCatList.remove(cat);
		}
		
		if (selectedCatList.size() > 9) {
			enableValue = true;
		} else {
			enableValue = false;
		}
	}
	
	public void assignCategoryForCompExperience() {
		
		SmSupplierCategory cat = new SmSupplierCategory();
	
		if (selectedCatList.size() > 8) {
			enableValue = true;
		} else {
			enableValue = false;
		}
		
		cat.setPmCategoryL1(new PmCategoryL1());
		
		PmCategoryL2 catL2 = new PmCategoryL2();
		catL2.setPmCategoryL2s(new ArrayList<PmCategoryL2>());
		cat.setPmCategoryL2(catL2);
		
		PmCategoryL3 catL3 = new PmCategoryL3();
		catL3.setPmCategoryL3s(new ArrayList<PmCategoryL3>());
		cat.setPmCategoryL3(catL3);
		
		supplierParentCatList = retrieveParentCategoryListForExperiencePage();
		
		resetChildCatList();
		assignValReqdToTrue();
		
		selectedCatList.add(cat);
	}
	
	public void populateExperienceSubChildCategoryByChild(long childId,
			PmCategoryL3 catL3) {
	
		try {
			List<PmCategoryL3> categoryL3s = retrieveExperienceSubChildCategoryList(childId);
			catL3.setPmCategoryL3s(categoryL3s);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static List<PmCategoryL3> retrieveExperienceSubChildCategoryList(
			long categoryL2Id) {
	
		ICategoryCodeSO reqSO = (ICategoryCodeSO) BusinessDelegate
				.getInstance().getServiceObject(ReportConst.PM_MODULE_SER_OBJ,
						ICategoryCodeSO.class);
		
		CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
		reqDTO.setCategoryL2Id(categoryL2Id);
		
		CategoryCodeResDTO resDTO = reqSO.retrieveCategoryL3ListByL2Id(reqDTO);
		
		return resDTO.getCategoryL3List();
	}
	
	public void populateExperienceChildCategoryByParent(long parentId,
			PmCategoryL2 catL2, PmCategoryL3 catL3) {

		if (this.getReportValue().equalsIgnoreCase(
				ReportConst.SM_SITE_VISIT_APPL_REQUIRE_RPT_VAL)) {
			
			String CategoryL1Id = Long.toString(parentId);
			
			if (StringUtils.isNotEmpty(CategoryL1Id)) {
				this.setValuesEmpty(false);
			} else {
				this.setValuesEmpty(true);
			}
		}
		
		try {
		
			List<PmCategoryL2> categoryL2s = retrieveExperienceChildCategoryList(parentId);
			catL2.setPmCategoryL2s(categoryL2s);
			catL3.setPmCategoryL3s(new ArrayList<PmCategoryL3>());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<PmCategoryL2> retrieveExperienceChildCategoryList(
			long categoryL1Id) {
	
		ICategoryCodeSO reqSO = (ICategoryCodeSO) BusinessDelegate
				.getInstance().getServiceObject(ReportConst.PM_MODULE_SER_OBJ,
						ICategoryCodeSO.class);
		
		CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
		reqDTO.setCategoryL1Id(categoryL1Id);
		CategoryCodeResDTO resDTO = reqSO.retrieveCategoryL2ListByL1Id(reqDTO);
		
		return resDTO.getCategoryL2List();
	}
	
	public List<SelectItem> getPaymentTypeMethodList() {
		
		List<SelectItem> paymentTypeMethodList = new ArrayList<SelectItem>();
		SelectItem item = new SelectItem();
		item.setValue(ReportConst.SM_REPORT_CREDIT);
		item.setLabel(ResourceBundleUtils.getMessage(
					"lbl.rpt.credit",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					getFacesScopes().getUserLocale()));
		paymentTypeMethodList.add(item);
		item = new SelectItem();
		String fpx = ResourceBundleUtils.getMessage(
					"lbl.rpt.fpx",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					getFacesScopes().getUserLocale());
		item.setValue(fpx);
		item.setLabel(fpx);
		paymentTypeMethodList.add(item);

		return paymentTypeMethodList;
	}

	public void changeSupplierTypeSelection() {
	
		if (reportValue.equals(ReportConst.SM_STATISTICS_COMPANYTYPE_CAP_RPT_VAL)) {
		
			if (!isEmpty(smSupplierType)) {
				
				if (smSupplierType.equals(ReportConst.SUPPL_CONSULT_TYPE)) {
					retrieveConsultantBusCompanyData();
				} else if (smSupplierType.equals(ReportConst.SUPPL_CONTRACT_TYPE)) {
					retrieveContractBusCompanyData();
				}
				
			} else {
				retrieveAllBusCompanyData();
			}
		}
	}
	
	public void changeBusCompanyType() {
		
		smBusCompanyPaidUpAccumlatedShareView = false;
		smAccmltdCptlSelView = false;
		smPaidUpCptlSelView = false;
		smAccmltdShareSelView = false;
		smPaidAccmltdShareCptlSelView = false;

		if (StringUtils.isNotEmpty(this.smBusCompanyType)) {

			if (this.smBusCompanyType.equals("C")
					|| this.smBusCompanyType.equals("D")
					|| this.smBusCompanyType.equals("E")
					|| this.smBusCompanyType.equals("F")
					|| this.smBusCompanyType.equals("G")
					|| this.smBusCompanyType.equals("H")) {
				
				smBusCompanyPaidUpAccumlatedShareView = true;
				smAccmltdCptlSelView = true;
				
			} else if (smBusCompanyType.equals("I")
					|| smBusCompanyType.equals("J")
					|| smBusCompanyType.equals("B")) {
				
				smBusCompanyPaidUpAccumlatedShareView = true;
				smPaidUpCptlSelView = true;
				
			} else if (smBusCompanyType.equals("K")) {
				
				smBusCompanyPaidUpAccumlatedShareView = true;
				smAccmltdShareSelView = true;
			}
			
		} else {
			
			smBusCompanyPaidUpAccumlatedShareView = true;
			smPaidAccmltdShareCptlSelView = true;
		}
	}
	
	public void changeDateBasedOnRegType() {
		
		smActiveSubmissiveFromDateView = false;
		smActivationDateView = false;
		smSubmissionDateView = false;
		smActSubmisAllDateView = false;
		smMofRegNoTwoView = false;
		smMofRegNoView = false;

		if (!isEmpty(smRegistrationType)) {
			
			if (smRegistrationType.equals(ReportConst.RPT_REG_BASIC)) {
				
				if (reportValue.equals(ReportConst.SM_LST_DAILY_NEW_APPL_RPT_VAL)) {
					
					smMofRegNoTwoView = true;
					smActiveSubmissiveFromDateView = true;
					smActSubmisAllDateView = true;
					
				} else if (reportValue.equals(ReportConst.SM_SUPPLIER_PERFORMANCE)) {
				
					smActiveSubmissiveFromDateView = true;
					smActivationDateView = true;
				}
				
			} else if (smRegistrationType.equals(ReportConst.RPT_REG_MOF)
					|| smRegistrationType.equals(ReportConst.RPT_REG_BASIC_TO_MOF)) {
			
				if (reportValue.equals(ReportConst.SM_LST_DAILY_NEW_APPL_RPT_VAL)) {
			
					smMofRegNoView = true;
					smActiveSubmissiveFromDateView = true;
					smSubmissionDateView = true;

				} else if (reportValue.equals(ReportConst.SM_SUPPLIER_PERFORMANCE)) {
					
					smActiveSubmissiveFromDateView = true;
					smSubmissionDateView = true;
				}
				
			} else if (smRegistrationType.equals(ReportConst.RPT_REG_ALL)) {
			
				if (reportValue.equals(ReportConst.SM_SUPPLIER_PERFORMANCE)) {
					
					smActiveSubmissiveFromDateView = true;
					smActSubmisAllDateView = true;
				}
			}
		}
	}
	
	public void onCompanyNameChangeForLOV() {
		
		if (selectedOrgProfile != null && selectedOrgProfile.getSmSupplier() != null) {
		
			cmCompanyName = selectedOrgProfile.getSmSupplier().getCompanyName();
			pmCompanyName = selectedOrgProfile.getSmSupplier().getCompanyName();
			smCompanyName = selectedOrgProfile.getSmSupplier().getCompanyName();
			smSupplierName = selectedOrgProfile.getSmSupplier().getCompanyName();
		}
	}
	
	public void changeReportType() {

		if (reportValue.equals(ReportConst.SM_TOT_NUM_YEARLY_APPL_RPT_VAL)) {
			
			smMandatoryCreatedFromYearView = false;
			smMandatoryRegisteredReportTypeDateView = false;
			smMandatoryRecievedReportTypeView = false;
			smMandatoryApprovedReportTypeView = false;
			smMandatoryRejectedReportTypeView = false;
			
			if (smReportType.equals(ReportConst.SM_REPORT_TYPE_RECEIVED)) {
			
				smMandatoryCreatedFromYearView = true;
				smMandatoryRecievedReportTypeView = true;
			
			} else if (smReportType.equals(ReportConst.SM_REPORT_TYPE_APPROVED)) {
				
				smMandatoryCreatedFromYearView = true;
				smMandatoryApprovedReportTypeView = true;
			
			} else if (smReportType.equals(ReportConst.SM_REPORT_TYPE_REJECTED)) {
			
				smMandatoryCreatedFromYearView = true;
				smMandatoryRejectedReportTypeView = true;
			}

		}
	}
	
	public void changeRTReportType() {

	smMandatoryReceiptDateView = false;
	smMandatoryTransactionDateView = false;
	smMandatoryRTDateView = false;

	if (smRTReportType.equals(ReportConst.SM_REPORT_RESIT)) {
		
			smMandatoryRTDateView = true;
			smMandatoryReceiptDateView = true;

		} else if (smRTReportType.equals(ReportConst.SM_REPORT_TRANSACTION)) {
			
			smMandatoryRTDateView = true;
			smMandatoryTransactionDateView = true;
		}
	}

	public void changeRTReportTypeForPayment() {
	
		smNonMandatoryReceiptDateView = false;
		smNonMandatoryTransactionDateView = false;

		if (smRTReportType.equals(ReportConst.SM_REPORT_RESIT)) {

			smNonMandatoryReceiptDateView = true;

		} else if (smRTReportType.equals(ReportConst.SM_REPORT_TRANSACTION)) {
			
			smNonMandatoryTransactionDateView = true;
		}
		
//		this.getManagedBean().getRptGenBean()
//				.setSmNonMandatoryTransactionDateView(true);
	}
	
	public void getRgegistartionTypeDates() {

		smNonMandatoryDateView = false;
		smBasicRegisteredDateView = false;
		smMOFRegisteredDateView = false;
		smAllRegisteredDateView = false;
		
		smNonMandatoryDateView = true;
		
		if (smRegistrationType.endsWith(ReportConst.RPT_REG_BASIC)) {
				
			smBasicRegisteredDateView = true;
				
			} else if (smRegistrationType.equals(ReportConst.RPT_REG_MOF)) {
			
				smMOFRegisteredDateView = true;
			}
		} 
	
//		else {
//			
//			smNonMandatoryDateView = true;
//			smAllRegisteredDateView = true;
//		}
//	}

	public void changeGSTRegisterStatus() {
	
		if (StringUtils.isNotEmpty(smGSTRegisteredStatus)
				&& (fromDate != null && toDate != null)) {
			
			valuesEmpty = false;
		
		} else {
		
			valuesEmpty = true;
			
		}
	}
	
	public void onChangeScContractOrPKPType() {
		getIisDPReportsStatus();
	}
	
	public void onChangeScPidNo() {
		getIisDPReportsStatus();
	}
	
	private void getIisDPReportsStatus() {
		if (StringUtils.isNotEmpty(this.getModuleValue())
				&& (this.getModuleValue()
						.equalsIgnoreCase(ModuleCodeConst.DIRECT_PURCHASE))) {

			if (StringUtils.isNotEmpty(this.getReportValue())) {

				switch (EnumReportType.valueOf(this.getReportValue())) {

				case SC_050:
				case SC_051:

					if ((approverNayakTarafList.size() > 0)
							|| ((authoriztionList != null) && (authoriztionList
									.contains(ReportConst.RN_APPROVER)))) {
						if (StringUtils.isNotEmpty(this.getScSupplier())
								|| StringUtils.isNotEmpty(this.getScPIno())
								|| StringUtils.isNotEmpty(this
										.getScCommonStatus())
								|| StringUtils.isNotEmpty(this
										.getScRequisenerId())
								|| StringUtils.isNotEmpty(this.getScPtj())) {
							this.setValuesEmpty(false);
						} else {
							this.setValuesEmpty(true);
						}
					} else if (StringUtils.isNotEmpty(this.getScSupplier())
							|| StringUtils.isNotEmpty(this.getScPIno())
							|| StringUtils.isNotEmpty(this.getScCommonStatus())) {
						this.setValuesEmpty(false);
					} else {
						this.setValuesEmpty(true);
					}
					break;

				case SC_055:

					if ((approverNayakTarafList.size() > 0)
							|| ((authoriztionList != null) && (authoriztionList
									.contains(ReportConst.RN_APPROVER)))) {
						if (StringUtils.isNotEmpty(this
								.getScPurchaseInDefaultStatus())
								|| StringUtils.isNotEmpty(this.getScPIDNo())
								|| StringUtils.isNotEmpty(this
										.getScContractOrPKPType())
								|| StringUtils.isNotEmpty(this
										.getScContractOrPKPName())
								|| StringUtils.isNotEmpty(this
										.getScContractOrPKPNo())
								&& StringUtils.isNotEmpty(this
										.getScRequisenerId())
								|| StringUtils.isNotEmpty(this.getScPtj())) {
							this.setValuesEmpty(false);
						} else {
							this.setValuesEmpty(true);
						}
					} else if (StringUtils.isNotEmpty(this
							.getScPurchaseInDefaultStatus())
							|| StringUtils.isNotEmpty(this.getScPIDNo())
							|| StringUtils.isNotEmpty(this
									.getScContractOrPKPType())
							|| StringUtils.isNotEmpty(this
									.getScContractOrPKPName())
							|| StringUtils.isNotEmpty(this
									.getScContractOrPKPNo())) {
						this.setValuesEmpty(false);
					} else {
						this.setValuesEmpty(true);
					}
					break;

				case SC_067:

					if ((approverNayakTarafList.size() > 0)

							|| ((authoriztionList != null) && (authoriztionList
									.contains(ReportConst.RN_APPROVER)))) {
						if (StringUtils.isNotEmpty(this
								.getScPurchaseInDefaultStatus())
								|| StringUtils.isNotEmpty(this.getScSupplier())
								|| StringUtils.isNotEmpty(this.getScPIDNo())
								|| StringUtils.isNotEmpty(this
										.getScContractOrPKPType())
								|| StringUtils.isNotEmpty(this
										.getScContractOrPKPName())
								|| StringUtils.isNotEmpty(this
										.getScContractOrPKPNo())
								|| StringUtils.isNotEmpty(this
										.getScRequisenerId())
								|| StringUtils.isNotEmpty(this.getScPtj())) {
							this.setValuesEmpty(false);
						} else {
							this.setValuesEmpty(true);
						}
					} else if (StringUtils.isNotEmpty(this
							.getScPurchaseInDefaultStatus())
							|| StringUtils.isNotEmpty(this.getScSupplier())
							|| StringUtils.isNotEmpty(this.getScPIDNo())
							|| StringUtils.isNotEmpty(this
									.getScContractOrPKPType())
							|| StringUtils.isNotEmpty(this
									.getScContractOrPKPName())
							|| StringUtils.isNotEmpty(this
									.getScContractOrPKPNo())) {
						this.setValuesEmpty(false);
					} else {
						this.setValuesEmpty(true);
					}
					break;

				case SC_058:

				case SC_056:

					if ((approverNayakTarafList.size() > 0)
							|| ((authoriztionList != null) && (authoriztionList
									.contains(ReportConst.RN_APPROVER)))) {
						if (StringUtils.isNotEmpty(this.getScSupplier())
								|| StringUtils.isNotEmpty(this
										.getScRequestNoteStatus())
								|| StringUtils.isNotEmpty(this
										.getScProcurementType())
								|| StringUtils.isNotEmpty(this
										.getScRequestQuoteNo())
								|| StringUtils.isNotEmpty(this
										.getScRequisenerId())
								|| StringUtils.isNotEmpty(this.getScPtj())) {
							this.setValuesEmpty(false);
						} else {
							this.setValuesEmpty(true);
						}
					} else if (StringUtils.isNotEmpty(this.getScSupplier())
							|| StringUtils.isNotEmpty(this
									.getScRequestNoteStatus())
							|| StringUtils.isNotEmpty(this
									.getScProcurementType())
							|| StringUtils.isNotEmpty(this
									.getScRequestQuoteNo())) {
						this.setValuesEmpty(false);
					} else {
						this.setValuesEmpty(true);
					}
					break;

				case SC_060:

				case SC_062:

					if ((approverNayakTarafList.size() > 0)
							|| ((authoriztionList != null) && (authoriztionList
									.contains(ReportConst.RN_APPROVER)))) {
						if (StringUtils.isNotEmpty(this.getScSupplier())
								|| StringUtils.isNotEmpty(this
										.getScProcurementType())
								|| StringUtils.isNotEmpty(this
										.getScProcessRptQuotationNo())
								|| StringUtils.isNotEmpty(this
										.getScSimpleQuoteStatus())
								|| StringUtils.isNotEmpty(this
										.getScRequisenerId())
								|| StringUtils.isNotEmpty(this.getScPtj())) {
							this.setValuesEmpty(false);
						} else {
							this.setValuesEmpty(true);
						}
					} else if (StringUtils.isNotEmpty(this.getScSupplier())
							|| StringUtils.isNotEmpty(this
									.getScProcurementType())
							|| StringUtils.isNotEmpty(this
									.getScProcessRptQuotationNo())
							|| StringUtils.isNotEmpty(this
									.getScSimpleQuoteStatus())) {
						this.setValuesEmpty(false);
					} else {
						this.setValuesEmpty(true);
					}
					break;

				case SC_068:
					if (StringUtils.isNotEmpty(this
							.getScProcessRptQuotationNo())) {
						this.setValuesEmpty(false);
					} else {
						this.setValuesEmpty(true);
					}
					break;

				case SC_069:
					if (StringUtils.isNotEmpty(this.getScSupplier())
							|| StringUtils.isNotEmpty(this
									.getScSimpleQuoteType())
							|| StringUtils.isNotEmpty(this.getScSOAStatus())
							|| StringUtils.isNotEmpty(this
									.getScProcessRptQuotationNo())
							|| StringUtils.isNotEmpty(this.getScLOANo())
							|| StringUtils.isNotEmpty(this.getScRequisenerId())
							|| StringUtils.isNotEmpty(this.getScPtj())) {
						this.setValuesEmpty(false);
					} else {
						this.setValuesEmpty(true);
					}
					break;

				default: {
					System.out
							.println("In getIisDPReportsStatus unknown report value" + this.getReportValue());
					log.info("In getIisDPReportsStatus unknown report value \t" + this.getReportValue());
				}
				}

			}
		}

	}
	
	public List<SelectItem> retrieveRegStatusListForAll() {

		smBusCompTypeRegistrationStatus = null;
		
		IPMReportSO reportSO = getPMReportSO();
		List<Object[]> list = reportSO.retrieveRegStatusListForAll(strLanguage);
		
		return registrationStatusListOfBusCompType = assignListToSelectItemValueLabel(list);
	}
	
	public List<SelectItem> retrieveRegStatusListForConsultant() {
	
		registrationStatusListOfBusCompType = new ArrayList<SelectItem>();
		smBusCompTypeRegistrationStatus = null;
		
		IPMReportSO reportSO = getPMReportSO();
		List<Object[]> list = reportSO.retrieveRegStatusListForConsultant(strLanguage);
	
		return registrationStatusListOfBusCompType = assignListToSelectItemValueLabel(list);
	}
	
	public List<SelectItem> retrieveRegStatusListForContract() {
		
		smBusCompTypeRegistrationStatus = null;
	
		IPMReportSO reportSO = getPMReportSO();
		List<Object[]> list = reportSO.retrieveRegStatusListForContract(strLanguage);
	
		return registrationStatusListOfBusCompType = assignListToSelectItemValueLabel(list);
		
	}
	
	
	
	public void onChangeScRequestQuoteNo() {
		getIisDPReportsStatus();
	}

	public void onChangeScPiNo() {
		getIisDPReportsStatus();
	}
	
	public void onChangeScRequestNoteStatus() {
		getIisDPReportsStatus();
	}
	
	public void onChangeScSimpleQuoteStatus() {
		getIisDPReportsStatus();
	}
	
	public void onChangeScCommonStatus() {
		getIisDPReportsStatus();
	}
	
	public void mofRegNoNumericValidation() {
	
		if(reportValue.equals(ReportConst.SM_LIST_OF_COMPANY_SUPTYPE_RPT_VAL)){
			
			if(!isEmpty(smMofRegNoFrom) || !isEmpty(smMofRegNoTo)){
				throw new EpException(MessageConst.ERROR_REPORT_780035);
			}
		}
		
		String ptString1 = ReportConst.NUMERIC_PATTERN_ONLY;
		Pattern pattern1 = Pattern.compile(ptString1);
		
		if (!isEmpty(smMofRegNoFrom)) {
			
			String mofRegNoFrom = null;
		
			if (smMofRegNoFrom.contains(ReportConst.PARAM_IFON)) {
				
				mofRegNoFrom = smMofRegNoFrom.replace(
						ReportConst.PARAM_IFON, ReportConst.EMPTY_VAL);
				
				if (mofRegNoFrom.contains(ReportConst.PARAM_SPACE)) {
					
					mofRegNoFrom = mofRegNoFrom.replace(ReportConst.PARAM_SPACE, ReportConst.EMPTY_VAL);
					
				}
				
			} else {
				
				mofRegNoFrom = smMofRegNoFrom;
				
			}

			Matcher match1 = pattern1.matcher(mofRegNoFrom);
			
			if (!match1.matches()) {
			
				smMofRegNoFrom = null;
				smMofRegNoTo = null;
				
				throw new EpException(MessageConst.ERROR_REPORT_780018);
				
			}
		}
		
		String ptString2 = ReportConst.NUMERIC_PATTERN_ONLY;
		Pattern pattern2 = Pattern.compile(ptString2);
		
		if (!isEmpty(smMofRegNoTo)) {
			
			String mofRegNoTo = null;
			
			if (smMofRegNoTo.contains(ReportConst.PARAM_IFON)) {
				
				mofRegNoTo = smMofRegNoTo.replace(ReportConst.PARAM_IFON, ReportConst.EMPTY_VAL);
				
				if (mofRegNoTo.contains(ReportConst.PARAM_SPACE)) {
					
					mofRegNoTo = mofRegNoTo.replace(ReportConst.PARAM_SPACE, ReportConst.EMPTY_VAL);
				}
				
			} else {
				
				mofRegNoTo = smMofRegNoTo;
				
			}

			Matcher match2 = pattern2.matcher(mofRegNoTo);
			
			if (!match2.matches()) {
			
				smMofRegNoFrom = null;
				smMofRegNoTo = null;
				
				throw new EpException(MessageConst.ERROR_REPORT_780018);
				
			}
		}

		if (reportValue.equals(ReportConst.SM_LIST_OF_COMPANY_SUPTYPE_RPT_VAL)) {
			
			if (!isEmpty(smMofRegNoFrom) && !isEmpty(smMofRegNoTo)) {
				
				valuesEmpty = false;
				
			} else {
				
				valuesEmpty = true;
				
			}
			
		}
	}	
	
	public boolean isFromDateOrToDateEmpty(){
		return fromDate == null || toDate == null;	
	}
	
	public void checkOneYearDifference(Date fromDate, Date toDate) {
	
		SimpleDateFormat outputDateFormat = new SimpleDateFormat(
				CommonConstants.DEFAULT_DATE_FORMAT);
		
		String yearFrom = outputDateFormat.format(fromDate).substring(6, 10);
		String yearTo = outputDateFormat.format(toDate).substring(6, 10);
		String monthFrom = outputDateFormat.format(fromDate).substring(3, 5);
		String monthTo = outputDateFormat.format(toDate).substring(3, 5);
		String dayFrom = outputDateFormat.format(fromDate).substring(0, 2);
		String dayTo = outputDateFormat.format(toDate).substring(0, 2);
		
		int yearFromInt = Integer.parseInt(yearFrom);
		int yearToInt = Integer.parseInt(yearTo);
		int monthFromInt = Integer.parseInt(monthFrom);
		int monthToInt = Integer.parseInt(monthTo);
		int dayFromInt = Integer.parseInt(dayFrom);
		int dayToInt = Integer.parseInt(dayTo);
		
		if (yearToInt == yearFromInt + 1) {
		
			if (monthToInt == monthFromInt) {
			
				if (dayToInt > dayFromInt) {
					throw new EpException(MessageConst.ERROR_REPORT_780011);
				}
				
			} else if (monthToInt > monthFromInt) {
				throw new EpException(MessageConst.ERROR_REPORT_780011);
			}
			
		} else if (yearToInt > yearFromInt + 1) {
			throw new EpException(MessageConst.ERROR_REPORT_780011);
		}
	}
	
	public void checkTwoYearsFieldDifference(String yearFrom, String yearTo) {
		
		int yearFromInt = Integer.parseInt(yearFrom);
		int yearToInt = Integer.parseInt(yearTo);
		
		if (yearToInt > yearFromInt + 1) {
			throw new EpException(MessageConst.ERROR_REPORT_780017);
		}
	}
	
	public void generateReport() {

		String reportArr1[] = {RptConst.SM_LIST_OF_COMP_SORT_ALPHA_RPT_VAL,
							  RptConst.SM_LIST_OF_DIR_OWN_CEO_COMPANY_RPT_VAL,
							  RptConst.SM_LIST_OF_COMP_DIR_OWN_CEO_RPT_VAL,
							  RptConst.SM_LIST_OF_COMPANY_SUPTYPE_RPT_VAL};
							  
		if(isMatch(reportValue, reportArr1)){
			mofRegNoNumericValidation();
		}
		
		String reportArr2[] = {	RptConst.KPPP1_PEMATUHAN_OC_RPT_VAL,
								RptConst.KPP1_PEMATUHAN_EC_RPT_VAL,
								RptConst.PDW_EC_RPT_VAL };
		
		if(!isMatch(reportValue, reportArr2)){
			throw new EpException(MessageConst.ERROR_REPORT_780004);
		};
		
		if (fromDate.after(toDate)) {
			throw new EpException(MessageConst.ERROR_REPORT_780008);
		}
	
		if (toDate.after(fromDate)) {
			checkOneYearDifference(fromDate, toDate);
		}
		
		if (ctCircularEffectiveFromDate.after(ctCircularEffectiveToDate)) {
			throw new EpException(MessageConst.ERROR_REPORT_780008);
		}
		
		if (ctCircularEffectiveFromDate.after(ctCircularEffectiveToDate)) {
			checkOneYearDifference(ctCircularEffectiveFromDate, ctCircularEffectiveToDate);
		}

		if (scSoneCrtdFromDate.after(scSoneCrtdToDate)) {
			throw new EpException(MessageConst.ERROR_REPORT_780008);
		}

		if (this.scSoneCrtdToDate.after(scSoneCrtdFromDate)) {
			checkOneYearDifference(scSoneCrtdFromDate, scSoneCrtdToDate);
		}
		
//		if (StringUtils.isNotEmpty(this.getManagedBean().getReportValue())) {
//				rptGenBean.setReportValueIdVar(RptConst.FL_AMP_REPORT_ID_EQ
//						+ getManagedBean().getReportValue());
//			}

//				rptGenBean.setUserIdVar(RptConst.FL_AMP_USER_ID_EQ
//						+ new FacesScopes().getUserSessionBean().getUserId()
//								.toString());
//			}
	
	
	String[] reportArr3 = {
			RptConst.SM_COLLECTION_AND_TRANSACTION_DATE_RPT_VAL,
			RptConst.SM_REVENUE_COLLECTION_RPT_VAL};	
	
	if(isMatch(reportValue, reportArr3)){
		
		if(isEmpty(smMofRegistrationFeeList) || 
		   isEmpty(smProcessingFeeList)||
		   isEmpty(smSoftCertificateFeeList)){
		
			throw new EpException(MessageConst.ERROR_REPORT_780027);
		}
	}
		
	if(reportValue.equals(RptConst.SM_COLLECTION_AND_TRANSACTION_DATE_RPT_VAL)){

		if (isFromDateOrToDateEmpty()) {
			throw new EpException(MessageConst.ERROR_REPORT_780034);
		}
	}
	
	if(reportValue.equals(RptConst.SM_COMPANY_ADDRESS_LIST_RPT_VAL)){
		throw new EpException(MessageConst.ERROR_REPORT_780036);
	}
	
	if(reportValue.equals(RptConst.SM_LIST_OF_PRE_EXPIRY_COMPANY_RPT_VAL)){
		
		if (isFromDateOrToDateEmpty()) {
			throw new EpException(MessageConst.ERROR_REPORT_780037);
		}
	}
	
	if(reportValue.equals(RptConst.SM_LIST_OF_GST_RGSTRD_SUPPLIER)){
		
		if (isEmpty(smGSTRegisteredStatus)) {
			throw new EpException(MessageConst.ERROR_REPORT_780038);
		}
		
		if (isFromDateOrToDateEmpty()) {
			throw new EpException(MessageConst.ERROR_REPORT_780039);
		}
	}

	if(reportValue.equals(RptConst.SM_VIRTUAL_DAY_REPORT_RPT_VAL)){
		
		if (isFromDateOrToDateEmpty()) {
			throw new EpException(MessageConst.ERROR_REPORT_780040);
		}
	}
	
	if (smThresholdFromYear == null || smThresholdToYear == null) {
		throw new EpException(MessageConst.ERROR_REPORT_780016);
	}
	
	checkOnlyYearFieldDifference(smThresholdFromYear, smThresholdToYear);
	checkOnlyYearFieldDifference(smCreatedFromYear, smCreatedToYear);
	
	String[] reportArr4 = {RptConst.SM_SUPPLIER_PERFORMANCE,
			RptConst.SM_THRESHOLD};
	
	if(isMatch(reportValue, reportArr4)){
		checkTwoYearsFieldDifference(smThresholdFromYear, smThresholdToYear);
	}
	
	if(reportValue.equals(RptConst.SM_CAT_STATIC_BY_YEAR_RPT_VAL)){
		checkTwoYearsFieldDifference(smCreatedFromYear, smCreatedToYear);
	}

	if(reportValue.equals(RptConst.SM_REG_COMPANY_IN_MOF_RPT_VAL)){
		
		if(isEmpty(smMofRegNo)){
			throw new EpException(MessageConst.ERROR_REPORT_780029);
		}
		
		checkTwoYearsFieldDifference(smCreatedFromYear, smCreatedToYear);
	}
	
	if(isModuleValuePP()){

		if (isEmpty(ppYear)) {
			throw new EpException(MessageConst.ERROR_REPORT_780030);
		}
	}

	if(isModuleValuePM()){

		String[] reportArr = {  RptConst.PM_LIST_OF_EXEMPTED_ITEMS_BY_SUPPLIER_RPT_VAL,
					 			RptConst.PM_LIST_OF_EXEMPTED_ITEMS_RPT_VAL,
					 			RptConst.PM_LIST_OF_EXEMPTED_CONTRACTED_ITEMS_RPT_VAL};
		
		switch(EnumReportType.valueOf(reportCode)){
		
			case PM_001:
	
				if (isEmpty(pmServiceType) && 
					isEmpty(pmFinancialOrgName) &&
					isEmpty(pmBusinessRegNo) &&
					isEmpty(pmStatus)) {
					
					throw new EpException(
							MessageConst.ERROR_REPORT_780003);
				}
				
				break;

			case PM_002:
	
				if (isEmpty(pmOrgCategory)) {
					throw new EpException(MessageConst.ERROR_REPORT_780031);
				}
				
				if (isEmpty(pmOrgType)) {
					throw new EpException(MessageConst.ERROR_REPORT_780032);
				}
				
				break;
			
			case PM_003:
				
				if (isEmpty(pmOrgType)) {
					throw new EpException(MessageConst.ERROR_REPORT_780032);
				}
				
				if (isEmpty(pmOrgName)) {
					throw new EpException(MessageConst.ERROR_REPORT_780033);
				}								
	
				break;
		
			case PM_004:
	
				if (isEmpty(pmOrgCode) && 
					isEmpty(pmOrgName) && 
					isEmpty(pmStatus)) {
					
					throw new EpException(MessageConst.ERROR_REPORT_780003);
					
				}
				
				break;
			
			}
		}
	}

	private int deleteDecryptedDataAfterReportGeneration(Long createdBy,String reportCode) {
		
		log.info("deleteDecryptedDataAfterReportGeneration");
		 IReportTempInfoSO reportTempInfoSO =null;
		 
		 reportTempInfoSO = EpEjbBusinessDelegate
					.getInstance().getServiceObject(
							ReportConst.RPT_MODULE_SER_OBJ,
							IReportTempInfoSO.class);

			ReportTempInfoReqDTO reqDTO = new ReportTempInfoReqDTO();
			reqDTO.setCreatedBy(createdBy);
			reqDTO.setReportCode(reportCode);

			ReportTempInfoResDTO resDTO = reportTempInfoSO
					.deleteDecryptedDataByReportCodeAndCreateedBy(reqDTO);
	
		return resDTO.getCount();
	}	
	
	public void checkOnlyYearFieldDifference(String yearFrom, String yearTo) {
		log.info("ReportBackingBean-checkOnlyYearFieldDefference");
		int yearFromInt = Integer.parseInt(yearFrom);
		int yearToInt = Integer.parseInt(yearTo);
		if (yearFromInt > yearToInt) {
			throw new EpException(MessageConst.ERROR_REPORT_780015);
		}
	}
}