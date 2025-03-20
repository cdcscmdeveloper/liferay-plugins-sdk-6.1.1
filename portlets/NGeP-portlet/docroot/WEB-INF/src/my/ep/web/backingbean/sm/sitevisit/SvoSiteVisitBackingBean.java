package my.ep.web.backingbean.sm.sitevisit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.application.ViewHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;

import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
//#30504 start
import my.ep.dto.pm.common.zone.ZoneReqDTO;
//#30504 end
import my.ep.biz.service.sm.attachment.ISmAttachmentSO;
import my.ep.biz.service.sm.remarks.ISmRemarksSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.biz.service.sm.siteVisit.ISiteVisitSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.common.categorycode.CategoryCodeReqDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;
import my.ep.dto.pm.common.document.DocumentReqDTO;
import my.ep.dto.sm.attachment.SmAttachmentReqDTO;
import my.ep.dto.sm.attachment.SmAttachmentResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.dto.sm.siteVisit.SiteVisitReqDTO;
import my.ep.dto.sm.siteVisit.SiteVisitResDTO;
import my.ep.dto.sm.smRemarks.SmRemaksResDTO;
import my.ep.dto.sm.smRemarks.SmRemarksReqDTO;
import my.ep.dto.sm.workFlowStatus.SmWorkFlowStatusReqDTO;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.jaxb.sm.mofregdata.SMMOFRegDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmUser;
//#33955 - START
import my.ep.persistence.entity.pm.authorization.PmUserRole;
//#33955 - END
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmMachine;
import my.ep.persistence.entity.pm.common.PmMachineCategory;
import my.ep.persistence.entity.pm.common.PmMachineSection;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplSupportDoc;
import my.ep.persistence.entity.sm.SmAssociateCompany;
import my.ep.persistence.entity.sm.SmAttachment;
import my.ep.persistence.entity.sm.SmChequeSignatory;
import my.ep.persistence.entity.sm.SmFacility;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmMachineCategory;
import my.ep.persistence.entity.sm.SmMachineDtl;
import my.ep.persistence.entity.sm.SmManufacturingDtl;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmRemark;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.persistence.entity.sm.SmSupplierCategoryFacility;
import my.ep.persistence.entity.sm.SmSupplierSystem;
import my.ep.persistence.entity.sm.SmSvAddress;
import my.ep.persistence.entity.sm.SmSvAdtMachine;
import my.ep.persistence.entity.sm.SmSvAssistantOfficer;
import my.ep.persistence.entity.sm.SmSvBankInfo;
import my.ep.persistence.entity.sm.SmSvComment;
import my.ep.persistence.entity.sm.SmSvDocLicense;
import my.ep.persistence.entity.sm.SmSvEquipment;
import my.ep.persistence.entity.sm.SmSvFacility;
import my.ep.persistence.entity.sm.SmSvFinChecklist;
import my.ep.persistence.entity.sm.SmSvInterviewee;
import my.ep.persistence.entity.sm.SmSvLicenseInsurance;
import my.ep.persistence.entity.sm.SmSvMachine;
import my.ep.persistence.entity.sm.SmSvManufacturingDoc;
import my.ep.persistence.entity.sm.SmSvOffice;
import my.ep.persistence.entity.sm.SmSvOfficer;
import my.ep.persistence.entity.sm.SmSvPrintmachine;
import my.ep.persistence.entity.sm.SmSvPrintwork;
import my.ep.persistence.entity.sm.SmSvTask;
import my.ep.persistence.entity.sm.SmSvTaskGroup;
import my.ep.persistence.entity.sm.SmSvTemplate;
import my.ep.persistence.entity.sm.SmSvWorker;
import my.ep.persistence.entity.sm.SmWorkflowStatus;
import my.ep.persistence.logging.LogKey;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.sitevisit.SvoSiteVisitManagedBean;
import my.ep.web.util.AddressUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.util.sm.helper.VmUtil;
import my.ep.web.util.template.DocumentGenerator;

import com.liferay.portal.util.PortalUtil;
@ManagedBean(name = "svoSiteVisitBackingBean")
@RequestScoped
public class SvoSiteVisitBackingBean extends BaseBackingBean<SvoSiteVisitManagedBean> {
	private static final long serialVersionUID = 1L;
	private static final String submissionConfirmDlgConstant = "submissionConfirmDlg.hide()";

	private final static Logger log = Logger.getLogger(SvoSiteVisitManagedBean.class);

	private String tmpPath = null;
	private String tmpFolder = "SiteVistTemplate" + "/";
	private String fileInitName;
	private String templateDesc;
	//28775 - Site Visit Grouping Enhancement - 28775 - start
	private Locale locale = new FacesScopes().getUserLocale();
	private static final String LANG_CODE = new FacesScopes().getUserLocale().getLanguage();
	public static final char LICENSE_PG = 'L';
	public static final char CAT_TYPE_PG = 'C';
	public static final char REGISTERED_PG = 'R';
	public static final char LOCATION_PG = 'O';
	public static final char SUBMIT_PG = 'S';
	private static final String CHANGE_PG = "T";
	private static final String VM_PATH = "/sm/sitevisit/group/";
	private static final String VM_UTIL = "smVmUtil";
	//28775 - Site Visit Grouping Enhancement - 28775 - end
	private static final String attachmentType = "SVD";
	private DecimalFormat formatter = new DecimalFormat("###,##0.00");
	
	private StreamedContent file;
	
	public SvoSiteVisitBackingBean() {
		SmCommonUtil.setMDCUserId();
	}
	
	/**
	 * set the selectedSmSvFacility by template type
	 * @param templateType
	 */
	private void setSelectedSmSvFacility(SmSvTemplate svTemplate){
		if(getManagedBean().getSelectedSvTemplate().getSmSvFacility() == null){
			SmSvAddress svAddress = getManagedBean().getSmSvTask().getSmSvAddress();
			//#28775 - start
			if(svAddress.getAddress() == null){
				SmCommonUtil.getInstance().setSmSvAddressInfoBySiteTypeAndRefId(
						svAddress.getSiteType(), svAddress.getSiteRefId(), svAddress, 
						getManagedBean().getSmSvTask().getSmAppl().getApplId());
			}
			SmFacility smFacility = null;
			if(svAddress.getAddress() != null){
				smFacility = retrieveFacilityByAddress(svAddress.getAddress().getAddressId());
			}
			//#28775 - end
			SmSvFacility smSvFacility = getManagedBean().getSelectedSvTemplate().getSmSvFacility();

			if(smFacility != null){
				if(smSvFacility == null){
					smSvFacility = new SmSvFacility();
				}

				smSvFacility.setOwnershipType(smFacility.getOwnershipType());
				if(smFacility.getAreaSquareFeet()!=null){
					smSvFacility.setOverallSqft(smFacility.getAreaSquareFeet().toString());
					smSvFacility.setEnclosedSqft(smFacility.getAreaSquareFeet().toString());
					smSvFacility.setEnclosedCapacitySqft(smFacility.getAreaSquareFeet().toString());
				}
				if(smFacility.getOwnershipType().equals("R")){
					smSvFacility.setPurchaseRentPrice(smFacility.getRentalRate());
				}else{
					smSvFacility.setPurchaseRentPrice(smFacility.getPurchasePrice());
				}
				smSvFacility.setSmSvTemplate(svTemplate);
				getManagedBean().getSelectedSvTemplate().setSmSvFacility(smSvFacility);
			}else{
				smSvFacility = new SmSvFacility();
				smSvFacility.setSmSvTemplate(svTemplate);
				getManagedBean().getSelectedSvTemplate().setSmSvFacility(smSvFacility);
			}
		}
	}


	
	/**
	 * retrieve Site Visit Task info
	 * @param viewMode
	 */
	public void retrieveSiteVisitInfo(String viewMode){
		//29430, 28775 - Site Visit Grouping Enhancement - 28775 - start
		if(!getManagedBean().isFromReport()){
			this.getManagedBean().setActiveIndex(0);
			this.getManagedBean().setActivePage("0");
		}
		//28775 - Site Visit Grouping Enhancement - 28775 - end
		
		SiteVisitReqDTO reqDTO = new SiteVisitReqDTO();
		reqDTO.setSvTaskId(getManagedBean().getSvTaskId());
		ISiteVisitSO reqSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISiteVisitSO.class);
		SmSvTask svoTask = reqSO.retrieveSiteVisitInfoBySvTaskId(reqDTO).getSmSvTask();
		
		if(StringUtils.isNotBlank(viewMode)){
			getManagedBean().setViewMode(true);
		}
		
		if(svoTask != null){
			this.getManagedBean().setSmSvTask(svoTask);
			SmSvTask smSvTask = this.getManagedBean().getSmSvTask();
			
			if(smSvTask.getSmSvFinChecklists().isEmpty()){
				List<PmParameterDesc> pmParamDesc = SmCommonUtil.retrieveParameterDescList(
						ParameterTypeConst.SVO_FIN_CHECKLIST, new FacesScopes().getUserLocale().getLanguage());
				for(PmParameterDesc pmdesc : pmParamDesc){
					SmSvFinChecklist svCL = new SmSvFinChecklist();
					svCL.setPmParameter(pmdesc.getPmParameter());
					svCL.setSmSvTask(smSvTask);

					pmdesc.getPmParameter().setPmParameterDescs(new ArrayList<PmParameterDesc>());
					pmdesc.getPmParameter().getPmParameterDescs().add(pmdesc);

					svCL.setSmSvTask(smSvTask);
					smSvTask.getSmSvFinChecklists().add(svCL);
				}
			}

			if(smSvTask.getSmSvBankInfos().isEmpty()){
				smSvTask.setSmSvBankInfos(new ArrayList<SmSvBankInfo>());
				for(SmSupplierBank suppBank : smSvTask.getSmAppl().getSmSupplierBanks()){
					SmSvBankInfo bankInfo = new SmSvBankInfo();
					bankInfo.setSmSupplierBank(suppBank);
					bankInfo.setSmSvTask(smSvTask);
					smSvTask.getSmSvBankInfos().add(bankInfo);
				}
			}

			if(smSvTask.getSmSvOffice() == null){
				smSvTask.setSmSvOffice(new SmSvOffice());
				smSvTask.getSmSvOffice().setSmSvTask(this.getManagedBean().getSmSvTask());
			}
			
			//#28774 - Start
			if(smSvTask.getSmSvAssistantOfficers() == null){
				smSvTask.setSmSvAssistantOfficers(new ArrayList<SmSvAssistantOfficer>());
				smSvTask.getSmSvAssistantOfficers();
				for(SmSvAssistantOfficer smSvAssistantOfficer : smSvTask.getSmSvAssistantOfficers()){
					smSvAssistantOfficer.setSmSvTask(this.getManagedBean().getSmSvTask());
				}
			}
			//#28774 - End
			//#24964 - start
			Collections.sort(smSvTask.getSmSvAssistantOfficers(), new Comparator<SmSvAssistantOfficer>() {
				@Override
				public int compare(final SmSvAssistantOfficer s1, final SmSvAssistantOfficer s2) {
					return (s1.getSvAssistantOfficerId().compareTo(s2.getSvAssistantOfficerId()));
				}
			});
			Collections.sort(smSvTask.getSmSvTemplate().getSmSvInterviewees(), new Comparator<SmSvInterviewee>() {
				@Override
				public int compare(final SmSvInterviewee s1, final SmSvInterviewee s2) {
					return Long.compare(s1.getSvIntervieweeId(), s2.getSvIntervieweeId());
				}
			});
			//#24964 - end
			
			for(SmPersonnel contactPerson : smSvTask.getSmAppl().getSmPersonnels()){
				if(contactPerson.isContactPerson()){
					this.getManagedBean().setContactPerson(contactPerson);
					break;
				}
			}
	
			this.getManagedBean().setLastIndex(5);
			
			getTotalBODMgmtWorkerEquityOwner();
			chequeSignatoryInfo();
			//28775 - Site Visit Grouping Enhancement - 28775 - start
			List<SmSupplierCategory> suppCatLs = getManagedBean().getCategoryApplicable();
			if(suppCatLs == null){
				suppCatLs = retrieveActiveCategoryCode(smSvTask.getSmAppl().getSmSupplier().getSmAppl().getApplId());
				getManagedBean().setCategoryApplicable(suppCatLs);
				SmApplicationUtil.sortSupplierCategory(getManagedBean().getCategoryApplicable());
			}
			//28775 - Site Visit Grouping Enhancement - 28775 - end
			//retrieve Supplier GST Info
			SmGstSupplier gstSupp = SmCommonUtil.getSmGstSupplierBySupplierIdAndBranchCode(getManagedBean().
					getSmSvTask().getSmAppl().getSmSupplier().getSupplierId(), null, null,true);
			getManagedBean().setSmGstSupplier(gstSupp);
						
			getManagedBean().setSelectedSvTemplate(smSvTask.getSmSvTemplate());
			getManagedBean().setBusinessTypeDesc(getBusinessTypeDesc());
			getManagedBean().setCompanyAddress(getCompanyAddressInLine());
			//28775 - Site Visit Grouping Enhancement - 28775 - start
			if(!smSvTask.getSmAppl().isSiteVisitGroup() && smSvTask.getPmCategoryL3() != null){
				getManagedBean().setSiteVisitNo(getSvTaskCountByApplIdAndCategoryId());
				//#35426 - start
				List<SmSupplierCategory> catLs = retrieveSupplierCategory(
						smSvTask.getSmAppl().getApplId(), smSvTask.getPmCategoryL3().getCategoryL3Id());
				if(!catLs.isEmpty()){
					getManagedBean().setSupplierCategory(catLs.get(0));
				}
				//28775 - Site Visit Grouping Enhancement - 28775 - end
				setMachineList();
			}
			if(getManagedBean().getSmSvTask().getPmCategoryL3() == null){
				getManagedBean().setSupplierCategory(null);
			}
			//#35426 - end
			if((smSvTask.getSmSvDocLicenses() == null || smSvTask.getSmSvDocLicenses().isEmpty())
					&& getManagedBean().getSmSvTask().getPmCategoryL3() != null){
				retrieveAndSetSmSvDocLicense(); //retrieve and set SmSvDocLicense (Additional License)	
			}
			SmMofAccount mofAccount =  SmApplicationUtil.getActiveMofAccount(smSvTask.getSmAppl().getSmSupplier().getSmMofAccounts(),
					smSvTask.getSmAppl().getSupplierType());
			getManagedBean().setMofNo(StringUtils.isNotBlank(mofAccount.getMofNo())?mofAccount.getMofNo() : "");
			if (smSvTask.getSmSvTemplate().getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_MANUFACTURING)
					&& !smSvTask.getSmAppl().getSmManufacturingDtls().isEmpty()) {
				getManagedBean().setManufacturingDtlLs(smSvTask.getSmAppl().getSmManufacturingDtls());
			}
			if(smSvTask.getSmSvTemplate().getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_DOCKYARD) || 
					smSvTask.getSmSvTemplate().getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_WORKSHOP) ||
					smSvTask.getSmSvTemplate().getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_PRINTING)){

				setSelectedSmSvFacility(smSvTask.getSmSvTemplate());
			}
			//28775 - Site Visit Grouping Enhancement - 28775 - start
			if(getManagedBean().getActivePage() == null)
				getManagedBean().setActivePage("0");
			prepareSvTaskGroup();
			if(getManagedBean().getSmSvTask().getSmSvTemplate().getSmAttachments() == null){
				retrievUploadedSiteVisitReport(getManagedBean().getSmSvTask().getSmSvTemplate());
			}
			//28775 - Site Visit Grouping Enhancement - 28775 - end
			SmCommonUtil.getInstance().setSmSvAddressInfoBySiteTypeAndRefId(smSvTask.getSmSvAddress().getSiteType(),
					smSvTask.getSmSvAddress().getSiteRefId(),smSvTask.getSmSvAddress(), smSvTask.getSmAppl().getApplId());
			//#28774 - start
			log.info("28774---appl status 1:"+getManagedBean().getSmSvTask().getSmAppl().getPmStatus().getStatusId());
			SmWorkflowStatus sts = SmCommonUtil.getCurrentActiveWokFlowStatus(
					getManagedBean().getSmSvTask().getSmAppl().getApplId());
			if (sts != null && sts.getPmStatus() != null){
				getManagedBean().getSmSvTask().getSmAppl().setPmStatus(sts.getPmStatus());
				log.info("28774---appl status 2:"+getManagedBean().getSmSvTask().getSmAppl().getPmStatus().getStatusId());
			}
			//#28774 - end
		}
	}
	
	/**
	 * Report Preview by category Type
	 * @param catType
	 */
	public void rptPreview(SmSvTemplate svTemplate){

		getManagedBean().setSelectedSvTemplate(svTemplate);
		//#29200 CR 14055: Static Code Analysis Start
		if((svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_DOCKYARD) ||  svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_WORKSHOP) || svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_PRINTING)) && svTemplate.getSmSvFacility() == null){
			//if(svTemplate.getSmSvFacility() == null){
				setSelectedSmSvFacility(svTemplate);
			//}
		}
		//#29200 CR 14055: Static Code Analysis End
	}
	
	/**
	 * Change index to control the menu
	 * @param index
	 * @param catType
	 */
	public void changeActiveIndex(int index, SmSvTemplate svTemplate){
		this.getManagedBean().setActiveIndex(index);
		if(svTemplate != null){
			if(svTemplate.getSmSvInterviewees() == null){
				svTemplate.setSmSvInterviewees(new ArrayList<SmSvInterviewee>());
			}
			
			if(svTemplate.getSmSvComment() == null){
				svTemplate.setSmSvComment(new SmSvComment());
				svTemplate.getSmSvComment().setSmSvTemplate(svTemplate);
			}
			
			if(svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_DOCKYARD)){
				this.prepareEquipmentLicenseInsurance(svTemplate);
				
			}else if(svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_WORKSHOP)){
				this.prepareEquipmentLicenseInsurance(svTemplate);
				
				if(svTemplate.getSmSvWorkers() == null){
					svTemplate.setSmSvWorkers(new ArrayList<SmSvWorker>()); 
				}
			}else if(svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_MANUFACTURING)){
				this.prepareFinancialDoc(svTemplate);
				
				//CR 28774: Static Code Analysis Start
				/*
				if (svTemplate.getSmSvTask().getSmAppl().getSmManufacturingDtls().size() > 0) {
					getManagedBean().setManufacturingDtlLs(svTemplate.getSmSvTask().getSmAppl().getSmManufacturingDtls());
				}
				*/
				if (!svTemplate.getSmSvTask().getSmAppl().getSmManufacturingDtls().isEmpty()) {
					getManagedBean().setManufacturingDtlLs(svTemplate.getSmSvTask().getSmAppl().getSmManufacturingDtls());
				}
				//CR 28774: Static Code Analysis End
			}else if(svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_PRINTING)){
				
				if(svTemplate.getSmSvPrintworks() == null){
					svTemplate.setSmSvPrintworks(new ArrayList<SmSvPrintwork>());				
				}
				
				if(svTemplate.getSmSvLicenseInsurances() == null){
					svTemplate.setSmSvLicenseInsurances(new ArrayList<SmSvLicenseInsurance>());
				}
				
				if(svTemplate.getSmSvLicenseInsurances().size() < 1){
					prepareEquipmentLicenseInsurance(svTemplate);
				}
				
			}
			
			if(svTemplate.getPmParameter()!=null && svTemplate.getPmParameter().getParameterCode()!=null){
				setSelectedSmSvFacility(svTemplate);
			}
			
		}
	}
	
	//28775 - Site Visit Grouping Enhancement - 28775 - start
	/**
	 * Change tab
	 * @param page
	 * @param viewFlag
	 */
	public void changePageIndex(String page, boolean viewFlag){
		//9608 - Site Visit Grouping Enhancement - 28775
		if(!viewFlag){
			saveSiteVisitReport(CHANGE_PG);
		}
		getManagedBean().setSelectedSvTemplate(getManagedBean().getSmSvTask().getSmSvTemplate());
		getManagedBean().setActivePage(page);
		int n = getManagedBean().getLicensed() ? 1 : 0;
		switch(page.toUpperCase().charAt(0)){
		case '0':
			changeActiveIndex(0, null);
			break;
		case '1':
			changeActiveIndex(1, null);
			break;
		case LICENSE_PG:
			changeActiveIndex(2, getManagedBean().getSelectedSvTemplate());
			break;
		case CAT_TYPE_PG:
			changeActiveIndex(2+n, getManagedBean().getSelectedSvTemplate());
			break;
		case REGISTERED_PG:
			changeActiveIndex(3+n, null);
			break;
		case LOCATION_PG:
			changeActiveIndex(4+n, getManagedBean().getSelectedSvTemplate());
			break;
		case SUBMIT_PG:
			changeActiveIndex(5+n, getManagedBean().getSelectedSvTemplate());
			break;
		default:
			changeActiveIndex(0, null);
		}
	}
	//28775 - Site Visit Grouping Enhancement - 28775 - end
	
	/**
	 * add interviewee to the List
	 */
	public void addInterviewee(){
		SmSvTemplate svTemplate = this.getManagedBean().getSelectedSvTemplate();
		
		SmSvInterviewee interviewee = new SmSvInterviewee();
		interviewee.setSmSvTemplate(svTemplate);
		this.getManagedBean().getSelectedSvTemplate().getSmSvInterviewees().add(interviewee);

	}
	
	/**
	 * remove selected Interviewee
	 */
	public void removeInterviewee(){
		if(this.getManagedBean().getSelectedInterviewees() != null && this.getManagedBean().getSelectedInterviewees().length > 0){
			for (SmSvInterviewee interviewee : this.getManagedBean().getSelectedInterviewees()) {
				this.getManagedBean().getSelectedSvTemplate().getSmSvInterviewees().remove(interviewee);		
			}
		}
		
		this.getManagedBean().setSelectedInterviewees(null);
	}
	
	/**
	 * retrieve and populate the equipment and License/Insurance
	 * @param svTemplate
	 * @param catType
	 */
	private void prepareEquipmentLicenseInsurance(SmSvTemplate svTemplate){
		
		String strLicenseIns = null;
		String catType = svTemplate.getPmParameter().getParameterCode();
		if(catType.equals(SupplierManagementConst.CAT_TYPE_DOCKYARD) || catType.equals(SupplierManagementConst.CAT_TYPE_WORKSHOP)){
			strLicenseIns = ParameterTypeConst.DOCKYARD_WORKSHOP_LICENSE_INS;
			
			PmParameter equipmentMO = SmCommonUtil.
					retrieveParameterByTypeAndCode(ParameterTypeConst.MACHINE_GROUP_TYPE, ParameterConst.EQUIPMENT_MAINTENANCE_OVERHAUL, new FacesScopes().getUserLocale().getLanguage());
			PmParameter equipmentPS = SmCommonUtil.
					retrieveParameterByTypeAndCode(ParameterTypeConst.MACHINE_GROUP_TYPE, ParameterConst.EQUIPMENT_PANEL_SPRAY, new FacesScopes().getUserLocale().getLanguage());
			
			List<PmMachineSection> machineSectionMO = retrieveMachineSectionBySectionType(equipmentMO.getParameterId(), svTemplate.getPmParameter().getParameterId());
			List<PmMachineSection> machineSectionPS = retrieveMachineSectionBySectionType(equipmentPS.getParameterId(), svTemplate.getPmParameter().getParameterId());
			
			//CR 28774: Static Code Analysis Start
//			if(svTemplate.getSmSvEquipments().size() > 0){
			if(!svTemplate.getSmSvEquipments().isEmpty()){
			//CR 28774: Static Code Analysis End
				
				svTemplate.setEquipmentMain(new ArrayList<SmSvEquipment>());
				svTemplate.setEquipmentBeating(new ArrayList<SmSvEquipment>());

				for(SmSvEquipment svEquipment : svTemplate.getSmSvEquipments()){
					if(svEquipment.getPmMachineSection().getSection().getParameterCode().equals(ParameterConst.EQUIPMENT_MAINTENANCE_OVERHAUL)){
						svTemplate.getEquipmentMain().add(svEquipment);
					}else if(svEquipment.getPmMachineSection().getSection().getParameterCode().equals(ParameterConst.EQUIPMENT_PANEL_SPRAY)){
						svTemplate.getEquipmentBeating().add(svEquipment);
					}
				}
			}else{
				if(svTemplate.getSmSvEquipments().size() < 1){
					svTemplate.setEquipmentMain(new ArrayList<SmSvEquipment>());
					svTemplate.setEquipmentBeating(new ArrayList<SmSvEquipment>());
					
					if(svTemplate.getSmSvEquipments() == null){
						svTemplate.setSmSvEquipments(new ArrayList<SmSvEquipment>());
					}
					
					for(PmMachineSection maintenanceSection : machineSectionMO){
						SmSvEquipment equipMainObj = new SmSvEquipment();
						equipMainObj.setPmMachineSection(maintenanceSection);
						equipMainObj.setPmMachine(maintenanceSection.getPmMachine());
						equipMainObj.setSmSvTemplate(svTemplate);
						svTemplate.getSmSvEquipments().add(equipMainObj);
					}
					
					for(PmMachineSection panelSpraySection : machineSectionPS){
						SmSvEquipment equipBindingObj = new SmSvEquipment();
						equipBindingObj.setPmMachineSection(panelSpraySection);
						equipBindingObj.setPmMachine(panelSpraySection.getPmMachine());
						equipBindingObj.setSmSvTemplate(svTemplate);
						svTemplate.getSmSvEquipments().add(equipBindingObj);
					}
					
//					if(svTemplate.getSmSvEquipments().size() > 0){
					if(!svTemplate.getSmSvEquipments().isEmpty()){
						for(SmSvEquipment svEquipment : svTemplate.getSmSvEquipments()){
							
							if(svEquipment.getPmMachineSection().getSection().getParameterCode().equals(ParameterConst.EQUIPMENT_MAINTENANCE_OVERHAUL)){
								svTemplate.getEquipmentMain().add(svEquipment);
							}else if(svEquipment.getPmMachineSection().getSection().getParameterCode().equals(ParameterConst.EQUIPMENT_PANEL_SPRAY)){
								svTemplate.getEquipmentBeating().add(svEquipment);
							}
						}
					}
					
					
				}
			}
		}
		else if(catType.equals(SupplierManagementConst.CAT_TYPE_PRINTING)){
			strLicenseIns = ParameterTypeConst.PRINTING_LICENSE_INS;
		}
		
		// add to smLicenseInsurance
		//#29200 CR 14055: Static Code Analysis Start
		if((catType.equals(SupplierManagementConst.CAT_TYPE_DOCKYARD) || catType.equals(SupplierManagementConst.CAT_TYPE_WORKSHOP) || catType.equals(SupplierManagementConst.CAT_TYPE_PRINTING)) && svTemplate.getSmSvLicenseInsurances().size() < 1){
			//if(svTemplate.getSmSvLicenseInsurances().size() < 1){
				List<PmParameterDesc> dockyardLicenceInsurance = SmCommonUtil.retrieveParameterDescList(strLicenseIns, new FacesScopes().getUserLocale().getLanguage());
				for(PmParameterDesc dockWorkLicenseIns : dockyardLicenceInsurance){
					SmSvLicenseInsurance svLicenseInsurance = new SmSvLicenseInsurance();
					svLicenseInsurance.setPmParameter(dockWorkLicenseIns.getPmParameter());
					svLicenseInsurance.setSmSvTemplate(svTemplate);
					svTemplate.getSmSvLicenseInsurances().add(svLicenseInsurance);
				}
			//}
		}
		//#29200 CR 14055: Static Code Analysis End
	}
	
	/**
	 * retrieve and populte financial doc - Manufacturing
	 * @param svTemplate
	 */
	private void prepareFinancialDoc(SmSvTemplate svTemplate){
		if(svTemplate.getFinancialDocLs() == null){
			svTemplate.setFinancialDocLs(new ArrayList<SmSvManufacturingDoc>());
		}
		
		if(svTemplate.getAdditionalDocLs() == null){
			svTemplate.setAdditionalDocLs(new ArrayList<SmSvManufacturingDoc>());
		}
		
		if(svTemplate.getSmSvManufacturingDocs() == null){
			svTemplate.setSmSvManufacturingDocs(new ArrayList<SmSvManufacturingDoc>());
		}
		
		if(svTemplate.getSmSvManufacturingDocs().size() < 1){
			List<PmParameterDesc> financialDocLs = SmCommonUtil.retrieveParameterDescList(ParameterTypeConst.MANUFACTURING_FINANCIAL_DOC, new FacesScopes().getUserLocale().getLanguage());
			for(PmParameterDesc financeParamDesc : financialDocLs){
				SmSvManufacturingDoc financeDoc = new SmSvManufacturingDoc();
				financeDoc.setPmParameter(financeParamDesc.getPmParameter());
				financeDoc.setSmSvTemplate(svTemplate);
				svTemplate.getSmSvManufacturingDocs().add(financeDoc);
			}
			
			if(svTemplate.getSmSvManufacturingDocs().size() > 1 && svTemplate.getFinancialDocLs().size() < 1){
				for(SmSvManufacturingDoc manufactDoc : svTemplate.getSmSvManufacturingDocs()){
					svTemplate.getFinancialDocLs().add(manufactDoc);
				}
			}
		}else{
			if(svTemplate.getFinancialDocLs().size() < 1){
				for(SmSvManufacturingDoc manuDoc : svTemplate.getSmSvManufacturingDocs()){
					if(manuDoc.getPmParameter() != null && 
							manuDoc.getPmParameter().getParameterType().equalsIgnoreCase(ParameterTypeConst.MANUFACTURING_FINANCIAL_DOC)){
						svTemplate.getFinancialDocLs().add(manuDoc);
					}else{
						svTemplate.getAdditionalDocLs().add(manuDoc);
					}
				}
			}
		}
		
	}	
	
	/**
	 * add new worker to the List
	 */
	public void addWorkshopWorker(){
		SmSvTemplate svTemplate = this.getManagedBean().getSelectedSvTemplate();
		List<SmSvWorker> workshopWorkerLs = svTemplate.getSmSvWorkers();
		SmSvWorker worker = new SmSvWorker();
		worker.setSmSvTemplate(svTemplate);
		//29379 - Site Visit Grouping Enhancement - 28775
		worker.setSmFacility(getManagedBean().getWorkerFacility());
		workshopWorkerLs.add(worker);
	}
	
	/**
	 * remove selected worker from the list
	 */
	public void removeWorkshopWorker(){
		//28775 - Site Visit Grouping Enhancement - 28775 - start
		SmSvWorker[] selectedWorkers = getManagedBean().getSelectedWorkers();
		if(getManagedBean().getLicensed()){
			selectedWorkers = getManagedBean().getSelectedSvTemplate().getSelectedWorkers();
		}
		if(selectedWorkers != null && selectedWorkers.length > 0){
			for (SmSvWorker svWorker : selectedWorkers) {
				this.getManagedBean().getSelectedSvTemplate().getSmSvWorkers().remove(svWorker);
			}
		}
		//28775 - Site Visit Grouping Enhancement - 28775 - end
		this.getManagedBean().setSelectedWorkers(null);
	}
	
	/**
	 * add new financialDoc to the List
	 */
	public void addFinancialDoc(){
		//28775 - Site Visit Grouping Enhancement - 28775 - start
		SmSvTemplate svTemplate = getManagedBean().getSmSvTask().getSmSvTemplate();
		if(svTemplate.getAdditionalDocLs() == null){
			svTemplate.setAdditionalDocLs(new ArrayList<SmSvManufacturingDoc>());
		}
		SmSvManufacturingDoc addFinancialDoc = new SmSvManufacturingDoc();
		addFinancialDoc.setSmSvTemplate(svTemplate);
		svTemplate.getAdditionalDocLs().add(addFinancialDoc);
		//28775 - Site Visit Grouping Enhancement - 28775 - end
	}
	
	/**
	 * remove financialDoc from the List
	 */
	public void removeFinancialDoc(){
		if(this.getManagedBean().getSelectedAddFinancialDoc() != null && this.getManagedBean().getSelectedAddFinancialDoc().length > 0){
			for (SmSvManufacturingDoc addFinDoc : this.getManagedBean().getSelectedAddFinancialDoc()) {
				this.getManagedBean().getSelectedSvTemplate().getAdditionalDocLs().remove(addFinDoc);
				this.getManagedBean().getSelectedSvTemplate().getSmSvManufacturingDocs().remove(addFinDoc);
			}
		}
		
		getManagedBean().setSelectedAddFinancialDoc(null);
	}
	
	/**
	 * get FacesScopes 
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes() {
//		FacesScopes facesScopes = new FacesScopes();
//		return facesScopes;
		return new FacesScopes();
	}
	
	/**
	 * format equity Amount 
	 * @param equityAmount
	 * @return
	 */
	private String formatEquity(BigDecimal equityAmount) {
		//DecimalFormat formatter = new DecimalFormat("###,##0.00");
		return formatter.format(equityAmount);
	}
	
	/**
	 * calculate Equity Percentage
	 * @param equityAmt
	 * @return String
	 */
	public String calculateEquityPercentage(String equityAmt){
		double eqAmt =  (equityAmt == null || equityAmt == "" ? Double.valueOf(0) : Double.valueOf(equityAmt));
		double paidUpCapital = this.getManagedBean().getSmSvTask().getSmAppl().getSmCompanyBasic().getPaidUpCapital().doubleValue();
		
		Double percentage = (eqAmt * 100) / paidUpCapital;
		
		//DecimalFormat formatter = new DecimalFormat("###,##0.00");
		formatter.setRoundingMode(RoundingMode.HALF_UP);
		return formatter.format(percentage);

	}
	
	//Local testing
	public void preRenderView(){
		if(this.getManagedBean().getSmSvTask() == null){
			retrieveSiteVisitInfo("");
		}
	}
	
	/**
	 * get Total BOD, Mgmt Worker and Equity Owner
	 */
	public void getTotalBODMgmtWorkerEquityOwner(){
		int ttlBOD = 0;
		int ttlMgmt = 0;
		int ttlWorker = 0;
		int ttlEquityOwner = 0;
		int dirBumi = 0;
		int dirNonBumi = 0;
		int dirNonMalaysian = 0;
		int mgmtBumi = 0;
		int mgmtNonBumi = 0;
		int mgmtNonMalaysian = 0;
		int wrkBumi = 0;
		int wrkNonBumi = 0;
		int wrkNonMalaysian = 0;
		
		this.getManagedBean().setBodLs(new ArrayList<SmPersonnel>());
		this.getManagedBean().setMgmtLs(new ArrayList<SmPersonnel>());
		this.getManagedBean().setEquityOwnerLs(new ArrayList<SmPersonnel>());
		this.getManagedBean().setStaffLs(new ArrayList<SmPersonnel>());
		//#29781 - Start
		this.getManagedBean().setHigherManagementNames(new ArrayList<String>());
		this.getManagedBean().setManagementNames(new ArrayList<String>());
		//#29781 - End
		
		for(SmPersonnel personnel : this.getManagedBean().getSmSvTask().getSmAppl().getSmPersonnels()){
			if(personnel.isDirector()){
				ttlBOD++;
				if(personnel.isBumi() != null && personnel.isBumi()){ //#32961
					dirBumi++;
				}else{
					if(!personnel.getPmCountry().getCountryCode().equalsIgnoreCase(SupplierManagementConst.MALAYSIA_CODE)){
						dirNonMalaysian++;
					}else{
						dirNonBumi++;
					}
				}			

				this.getManagedBean().getBodLs().add(personnel);
			}

			if(personnel.isHigherManagement() || personnel.isManagement()){
				ttlMgmt++;
				if(personnel.isBumi() != null && personnel.isBumi()){ //#32961
					mgmtBumi++;
				}else{ 
					if(personnel.getPmCountry().getCountryCode().equalsIgnoreCase(SupplierManagementConst.MALAYSIA_CODE)){
						mgmtNonBumi++;
					}else{
						mgmtNonMalaysian++;
					}
				}


				this.getManagedBean().getMgmtLs().add(personnel);

			}
			
			if(personnel.isHigherManagement()){
				this.getManagedBean().getHigherManagementNames().add(personnel.getName());
			}
			if(personnel.isManagement()) {
				this.getManagedBean().getManagementNames().add(personnel.getName());
			}

			if(personnel.isClerical()){
				ttlWorker++;

				if(personnel.isBumi() != null && personnel.isBumi()){  //#32961
					wrkBumi++;
				}else {
					if(personnel.getPmCountry().getCountryCode().equalsIgnoreCase(SupplierManagementConst.MALAYSIA_CODE)){
						wrkNonBumi++;
					}else{
						wrkNonMalaysian++;
					}
				}
				
				getManagedBean().getStaffLs().add(personnel);
			}
			
			if(personnel.isEquityOwner()){
				ttlEquityOwner ++;
				
				this.getManagedBean().getEquityOwnerLs().add(personnel);
			}
		}

		//total BOD,Mgmt and Worker
		this.getManagedBean().setTotalBOD(ttlBOD);
		this.getManagedBean().setTotalMgmt(ttlMgmt);
		this.getManagedBean().setTotalWorker(ttlWorker);

		this.getManagedBean().setDirBumi(dirBumi);
		this.getManagedBean().setDirNonBumi(dirNonBumi);
		this.getManagedBean().setDirNonMalaysian(dirNonMalaysian);
		this.getManagedBean().setMgmtBumi(mgmtBumi);
		this.getManagedBean().setMgmtNonBumi(mgmtNonBumi);
		this.getManagedBean().setMgmtNonMalaysian(mgmtNonMalaysian);
		this.getManagedBean().setWrkBumi(wrkBumi);
		this.getManagedBean().setWrkNonBumi(wrkNonBumi);
		this.getManagedBean().setWrkNonMalaysian(wrkNonMalaysian);
		
		this.getManagedBean().setTotalEquityOwner(ttlEquityOwner);
		
	}

	/**
	 * calculate percentage of Director
	 * @param dirNo
	 * @return BigDecimal
	 */
	public BigDecimal calculateDirectorPercentage(int dirNo){
		BigDecimal convertedDirNo = BigDecimal.valueOf(dirNo);
		BigDecimal ttlDirector = BigDecimal.valueOf(this.getManagedBean().getTotalBOD());
		BigDecimal percentage = BigDecimal.valueOf(0.00);
		
		try{
			percentage = convertedDirNo.divide(ttlDirector, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
		}catch(ArithmeticException e) {
			percentage = BigDecimal.valueOf(0.00).setScale(2);
		}
		return percentage;
	}

	/**
	 * calculate percentage of Management
	 * @param mgmtNo
	 * @return BigDecimal
	 */
	public BigDecimal calculateMgmtPercentage(int mgmtNo){
		BigDecimal convertedMgmtNo = BigDecimal.valueOf(mgmtNo);
		BigDecimal ttlMgmt= BigDecimal.valueOf(this.getManagedBean().getTotalMgmt());
		BigDecimal percentage = BigDecimal.valueOf(0.00);

		try{
			percentage = convertedMgmtNo.divide(ttlMgmt, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
		} catch (ArithmeticException e) {
			percentage = new BigDecimal(0.00).setScale(2);

		}
		return percentage;
	}

	/**
	 * calculate Percentage of Worker
	 * @param workerNo
	 * @return BigDecimal
	 */
	public BigDecimal calculateWorkerPercentage(int workerNo){
		BigDecimal convertedWrkNo = BigDecimal.valueOf(workerNo);
		BigDecimal ttlWorker= BigDecimal.valueOf(this.getManagedBean().getTotalWorker());
		BigDecimal percentage = BigDecimal.valueOf(0.00);

		try{
			percentage = convertedWrkNo.divide(ttlWorker, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
		}catch (ArithmeticException e) {
			percentage = BigDecimal.valueOf(0.00).setScale(2);
		}
		return percentage;
	}
	
	/**
	 * calculate  Percentage of ChequeSignatory
	 * @param chequeSignatory
	 * @return
	 */
	public BigDecimal calculatChequeSignatoryPercentage(int chequeSignatory){
		BigDecimal convertedCheqNo = BigDecimal.valueOf(chequeSignatory);
		BigDecimal ttlCheqSign= BigDecimal.valueOf(this.getManagedBean().getTotalCheqSign());
		BigDecimal percentage = BigDecimal.valueOf(0.00);

		try{
			percentage = convertedCheqNo.divide(ttlCheqSign, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
		}catch (ArithmeticException e) {
			percentage = BigDecimal.valueOf(0.00).setScale(2);
		}
		return percentage;
	}
	
	/**
	 * @deprecated
	 * retrieve SmRemark
	 * @param docType
	 * @param docNo
	 * @return SmRemark
	 */
	public SmRemark retrieveSmRemark(String docType, Long docNo){
		ISmRemarksSO reqSO = (ISmRemarksSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISmRemarksSO.class);
		SmRemarksReqDTO reqDTO = new SmRemarksReqDTO();
		reqDTO.setDocType(docType);
		reqDTO.setDocId(docNo);
		
		SmRemark rmk = null;
		SmRemaksResDTO resDTO = reqSO.retrieveSmRemarksByDocument(reqDTO);
		
		if(resDTO.getRemarksList().size() > 0){
			rmk = resDTO.getRemarksList().get(0);
		}
		
		return rmk;
	}
	
	/**
	 * get cheque Signatory Info
	 */
	private void chequeSignatoryInfo(){
		int totalChequeSignatory = 0;
		int cheqBumi = 0;
		int cheqNonBumi = 0;
		int cheqNonMalaysian = 0;
		
		// #14055 - start
		LinkedHashSet<String> totalCheckingList = new LinkedHashSet();
		LinkedHashSet<String> bumiCheckingList = new LinkedHashSet();
		LinkedHashSet<String> nonBumiCheckingList = new LinkedHashSet();
		LinkedHashSet<String> nonMyCheckingList = new LinkedHashSet();
		
		if (this.getManagedBean().getSmSvTask().getSmSvBankInfos() != null) {
			for (SmSvBankInfo svBankInfo : this.getManagedBean().getSmSvTask().getSmSvBankInfos()) {
				if (!svBankInfo.getSmSupplierBank().getSmChequeSignatories().isEmpty()) {	
					for (SmChequeSignatory chequeSignatory : svBankInfo.getSmSupplierBank().getSmChequeSignatories()) {
						totalCheckingList.add(chequeSignatory.getSmPersonnel().getIdentificationNo());
						if (chequeSignatory.getSmPersonnel().isBumi()) {
								bumiCheckingList.add(chequeSignatory.getSmPersonnel().getIdentificationNo());
						} else {
							if (chequeSignatory.getSmPersonnel().getPmCountry().getCountryCode()
									.equalsIgnoreCase(SupplierManagementConst.MALAYSIA_CODE)) {
								nonBumiCheckingList.add(chequeSignatory.getSmPersonnel().getIdentificationNo());
							} else {
								nonMyCheckingList.add(chequeSignatory.getSmPersonnel().getIdentificationNo());
							}
						}
					}
				}
			}
		}
		totalChequeSignatory = totalCheckingList.size();
		cheqBumi = bumiCheckingList.size();
		cheqNonBumi = nonBumiCheckingList.size();
		cheqNonMalaysian = nonMyCheckingList.size();
				
		// #14055 - end	
		
		this.getManagedBean().setTotalCheqSign(totalChequeSignatory);
		this.getManagedBean().setCheqBumi(cheqBumi);
		this.getManagedBean().setCheqNonBumi(cheqNonBumi);
		this.getManagedBean().setCheqNonMalaysian(cheqNonMalaysian);
	}
	
	/**
	 * retrieve Printing Accessories by category lvl code
	 * @param catLvlCode
	 * @return List<PmParameterDesc>
	 */
	private List<PmMachineCategory> retrievePrintingAcc(Long catL1Id, Long catL2Id, Long catL3Id){
		ICategoryCodeSO reqSO = (ICategoryCodeSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICategoryCodeSO.class);
		CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
		reqDTO.setCategoryL1Id(catL1Id);
		reqDTO.setCategoryL2Id(catL2Id);
		reqDTO.setCategoryL3Id(catL3Id);
		
		CategoryCodeResDTO resDTO = reqSO.retrieveMachineCategoryByCatIds(reqDTO);
		
		return resDTO.getPmMachineCategories();
	}
	
	/**
	 * add new printing type/ work done to the List
	 */
	public void addPrintWork(){
		SmSvPrintwork printWork = new SmSvPrintwork();
		printWork.setSmSvTemplate(this.getManagedBean().getSelectedSvTemplate());
		
		this.getManagedBean().getSelectedSvTemplate().getSmSvPrintworks().add(printWork);
	}
	
	/**
	 * add selected printing type/ work done from  the List
	 */
	public void removePrintWork(){
		//28775 - Site Visit Grouping Enhancement - 28775 - start
		SmSvPrintwork[] selectedPrintWorks = getManagedBean().getSelectedPrintWorks();
		if(getManagedBean().getLicensed()){
			selectedPrintWorks = getManagedBean().getSelectedSvTemplate().getSelectedPrintWorks();
		}
		if(selectedPrintWorks != null && selectedPrintWorks.length > 0){
			for (SmSvPrintwork printWork : selectedPrintWorks) {
				this.getManagedBean().getSelectedSvTemplate().getSmSvPrintworks().remove(printWork);
			}
		}
		//28775 - Site Visit Grouping Enhancement - 28775 - end
	}
	
	/**
	 * View Site Visit report
	 */
	public void viewSiteVisitReport(){
		retrieveSiteVisitInfo("V");
	}
	
	/**
	 * control the menu in review site visit report
	 * @param index
	 * @param catType
	 */
	public void viewActiveIndex(int index,SmSvTemplate svTemplate){
		getManagedBean().getLastIndex();
		
		this.getManagedBean().setActiveIndex(index);
		if(svTemplate != null){
			this.getManagedBean().setSelectedSvTemplate(svTemplate);
			
			if(svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_DOCKYARD) || 
					svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_WORKSHOP)){

				prepareEquipmentLicenseInsurance(svTemplate);
			}
			
			if(svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_MANUFACTURING)){
				this.prepareFinancialDoc(svTemplate);
			}
			
			if(svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_PRINTING)){
				prepareEquipmentLicenseInsurance(svTemplate);
			}
		}
	}
	
	/**
	 * retriev Active CategoryCode
	 * @param Long applId - application id
	 * @return List<SmSupplierCategory>
	 */
	public List<SmSupplierCategory> retrieveActiveCategoryCode(Long applId){
		SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
		ISupplierSearchSO reqSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);
		reqDTO.setApplId(applId);
		
		SupplierSearchResDTO resDTO = reqSO.retrieveSupplierActiveCategoryCodeByApplId(reqDTO);
		
		return resDTO.getSmSupplierCategoryList();
	}
	
	/**
	 * retrieve all approved Category code by applId
	 * @param applId
	 * @return List<SmSupplierCategory>
	 */
	public List<SmSupplierCategory> retrieveAllApprovedCategoryCode(Long applId){
		SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
		ISupplierSearchSO reqSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);
		reqDTO.setApplId(applId);
		
		SupplierSearchResDTO resDTO = reqSO.retrieveCategoryCodeByApplId(reqDTO);
		
		return resDTO.getSmSupplierCategoryList();
	}
	
	/**
	 * get supplier application type desc
	 * @param applType
	 * @return String
	 */
	public String getsupplierApplTypeDesc(String applType){
		return SmCommonUtil.getApplTypeDesc(applType);
	}
	
	/**
	 * download site visit template report in PDF
	 * @param templateType
	 * @throws Exception
	 */
	public void downloadSiteVisitTemplate(SmSvTask svTask, boolean review) throws Exception{
        String templateType = svTask.getSmSvTemplate().getPmParameter().getParameterCode();
		
		tmpPath = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);
		
		tmpPath = tmpPath + "/" + tmpFolder;
		
		File dir = new File(tmpPath);
		if(!dir.exists()) dir.mkdirs();
		
		String lblTemplate = "lbl.sm.sitevisit.category.categorycode_"+templateType;
		templateDesc = ResourceBundleUtils.getMessage(lblTemplate,ResourceBundleUtils.BUNDLE_COMMON_LABELS,locale);
		
		fileInitName = svTask.getSvTaskId() + "_" + templateDesc + "_";
		//28775 - Site Visit Grouping Enhancement - 28775 - start
		if(getManagedBean().getSmSvTask().getSmAppl().isSiteVisitGroup()){
			downloadSiteVisitTemplateGroup(svTask, review, fileInitName+"SV.pdf");
			return;
		}
		//28775 - Site Visit Grouping Enhancement - 28775 - end
		// combine all pdf into 1
		String cover = commonSiteVisitCover(svTask);
		String preDetails = commonPreparationDetails(svTask,review);
		String compInfo = commonSiteVisitCompanyInfo(svTask, review);
		String sectionA = commonSiteVisitSectionA(svTask);
		String sectionB = commonSiteVisitSectionB(svTask, review);
		String sectionC = commonSiteVisitSectionC(svTask,review);
		String sectionD = commonSiteVisitSectionD(svTask, review);
		
		boolean isSectionE = false;
		
		if(templateType.equalsIgnoreCase(SupplierManagementConst.CAT_TYPE_DOCKYARD) || 
				templateType.equalsIgnoreCase(SupplierManagementConst.CAT_TYPE_WORKSHOP) || 
				templateType.equalsIgnoreCase(SupplierManagementConst.CAT_TYPE_MANUFACTURING) || 
				templateType.equalsIgnoreCase(SupplierManagementConst.CAT_TYPE_PRINTING)) {
			isSectionE = true;
		}
		
		
		File svCover = new File(cover);
		File svPreDetails = new File(preDetails);
		File svCompInfo = new File(compInfo);
		File svSectionA = new File(sectionA);
		File svSectionB = new File(sectionB);
		File svSectionC = new File(sectionC);
		File svSectionD = new File(sectionD);
		
		File svSectionE = null;
		
		List<InputStream> mergeList = new ArrayList<InputStream>();  
		FileInputStream fCover = new FileInputStream(svCover);
		FileInputStream fPreDetails = new FileInputStream(svPreDetails);
		FileInputStream fCompInfo = new FileInputStream(svCompInfo);
		FileInputStream fSectionA = new FileInputStream(svSectionA);
		FileInputStream fSectionB = new FileInputStream(svSectionB);
		FileInputStream fSectionC = new FileInputStream(svSectionC);
		FileInputStream fSectionD = new FileInputStream(svSectionD);
		FileInputStream fSectionE = null;
		
		mergeList.add(fCover);
		mergeList.add(fPreDetails);
		mergeList.add(fCompInfo);
		mergeList.add(fSectionA);
		mergeList.add(fSectionB);
		mergeList.add(fSectionC);
		mergeList.add(fSectionD);
		
		if(isSectionE){
			String sectionE = commonSiteVisitSectionE(svTask,review);
			svSectionE = new File(sectionE);
			fSectionE = new FileInputStream(svSectionE);
			mergeList.add(fSectionE);
		}
		
		String fileName = fileInitName + "SV.pdf";
        String latestFile = tmpPath + fileName;
        
        
        OutputStream combined = new FileOutputStream(new File(latestFile));
        doMerge(mergeList, combined);
        combined.flush();
        combined.close();
        
        File finalOutputFile = new File(latestFile);
        
        svCover.delete();
        svPreDetails.delete();
        svCompInfo.delete();
        svSectionA.delete();
        svSectionB.delete();
        svSectionC.delete();
        svSectionD.delete();
        
        if(isSectionE){
        	svSectionE.delete();
        }
        
        try {
        	file = commonDownload(tmpFolder+fileName, fileName);
        	
        } catch (IOException e) {
        	log.error("File: SvoSiteVisitBackingBean, Method:downloadSiteVisitTemplate, Message: "+e.getMessage());
        }finally{
        	fCover.close();
    		fPreDetails.close();
    		fCompInfo.close();
    		fSectionA.close();
    		fSectionB.close();
    		fSectionC.close();
    		fSectionD.close();
    		if(fSectionE != null){
    			fSectionE.close();
    		}
        }
        
        if(finalOutputFile.delete()){
        	 log.info("**********Deleted***********");
        }else{
        	 log.info("**********Failed to Delete***********");
        }
	}
	
	/**
	 * Generate Site Visit Template - Cover
	 * @param svTask
	 * @return String
	 * @throws Exception
	 */
	private String commonSiteVisitCover(SmSvTask svTask) throws Exception{
		VelocityContext context = new VelocityContext();
		
		String lblApplType = SmCommonUtil.getApplTypeDesc(svTask.getSmAppl().getApplType());
		String applType = ResourceBundleUtils.getMessage(lblApplType,ResourceBundleUtils.BUNDLE_COMMON_LABELS,locale);
		SmAddress compAddress = svTask.getSmSvAddress().getAddress();
		List<SmMofAccount> mofAccountLs = svTask.getSmAppl().getSmSupplier().getSmMofAccounts();
		
		context.put("poName", getPOUsername());	//#36721
		//28775 - Site Visit Grouping Enhancement - 28775 - start
		Long stateId = -1L;
		try {
			stateId = svTask.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0)
					.getSmAddress().getPmState().getStateId();
		} catch (Exception e){
			SLAUtils.getInstance().logSLAError("SM", "SV", svTask.getSmAppl().getApplNo(),
					"SvoSiteVisitBackingBean", (new LogKey()).getUserId(), e);
		}
		context.put("isKlang", SupplierManagementConst.KLANG_VALLEY.contains(stateId));
		context.put(VM_UTIL, new VmUtil());
		//28775 - Site Visit Grouping Enhancement - 28775 - end
		context.put("hostUrl", getLogoHeaderPath());
		context.put("companyName", svTask.getSmAppl().getSmSupplier().getCompanyName());
		context.put("applNo", svTask.getSmAppl().getApplNo());
		context.put("applType", applType);
		context.put("templateType",templateDesc);
		context.put("compAddress", compAddress);
		context.put("addressFormatter", new AddressUtils());
		context.put("mofRegistrationNo", !mofAccountLs.isEmpty() ? mofAccountLs.get(0).getMofNo() : null);
		context.put("siteVisitNo", getManagedBean().getSiteVisitNo());
		
		//#29781 - Start
		//Sonarqube #G22
		List<String> svOfficers = new ArrayList<>();
		for(SmSvOfficer officer : svTask.getSmSvOfficers()){
			svOfficers.add(officer.getPmUser().getUserName());
		}
		for(SmSvAssistantOfficer officer : svTask.getSmSvAssistantOfficers()){
			svOfficers.add(officer.getName());
		}
		context.put("svOfficers", svOfficers);
		//#29781 - End
	
		String vmCover = "/sm/sitevisit/sv-main.vm";
		String fileName = fileInitName + "cover.pdf";
		//CR 28774: Static Code Analysis Start
//		String file = generateToPdf(vmCover, context, fileName);
		
		return generateToPdf(vmCover, context, fileName);
		//CR 28774: Static Code Analysis End
	}
	
	/**
	 * Generate Site Visit Template - Preparation Details
	 * @param svTask
	 * @param templateType
	 * @return String
	 * @throws Exception
	 */
	private String commonPreparationDetails(SmSvTask svTask, boolean review) throws Exception{
		String vmPreDetails = "/sm/sitevisit/sv-predetails.vm";
		
		VelocityContext context = new VelocityContext();
		
		context.put("companyName", svTask.getSmAppl().getSmSupplier().getCompanyName());	
		context.put("applNo", svTask.getSmAppl().getApplNo());
		context.put("businessRegistrationNo",  svTask.getSmAppl().getSmSupplier().getRegNo());
		//24819
		context.put("newBusinessRegistrationNo",svTask.getSmAppl().getSmSupplier().getSsmNewRegNo());
		context.put("visitingPurpose", templateDesc.toUpperCase());
		context.put("catApplicable", getManagedBean().getSupplierCategory());
		context.put("telNo", svTask.getSmAppl().getSmCompanyBasic().getPhoneFull());	//#28775
		context.put("latitude", svTask.getSmSvAddress().getAddress().getMapLatitude());
		context.put("longitute", svTask.getSmSvAddress().getAddress().getMapLongitude());
		
		if(this.getManagedBean().getContactPerson() != null){
			context.put("contactPersonName", this.getManagedBean().getContactPerson().getName());
			context.put("contactPersonDesignation", this.getManagedBean().getContactPerson().getDesignation());
		}
		
		SmAddress svAddress = svTask.getSmSvAddress().getAddress();
		
		context.put(VM_UTIL, new VmUtil());
		context.put("hostUrl", getLogoHeaderPath());
		context.put("templateType",templateDesc);
		context.put("svAddress", svAddress);
		context.put("addressFormatter", new AddressUtils());
		context.put("scheduleDate", svTask.getScheduleDate());
		
		if(review){
			context.put("actualSiteVisitDate", svTask.getActualVisitDate());
		}
		
		if(getManagedBean().getSmGstSupplier() != null){
			context.put("gstRegNo", getManagedBean().getSmGstSupplier().getGstRegNo());
			context.put("gstEffDate", getManagedBean().getSmGstSupplier().getGstEffDate());
		}
		//28775 - Site Visit Grouping Enhancement - 28775
		context.put("isGroup", getManagedBean().getSmSvTask().getSmAppl().isSiteVisitGroup());
		context.put("templates", getManagedBean().getTemplates());
		String fileName = fileInitName + "predetails.pdf";
		
		return generateToPdf(vmPreDetails, context, fileName);
		//CR 28774: Static Code Analysis End
		
	}
	
	/**
	 * Generate Site Visit Template - Company Info
	 * @param svTask
	 * @return String
	 * @throws Exception
	 */
	private String commonSiteVisitCompanyInfo(SmSvTask svTask, boolean review) throws Exception{
		VelocityContext context = new VelocityContext();
		String vmCompInfo= "/sm/sitevisit/sv-companyinfo.vm";
		//28775 - Site Visit Grouping Enhancement - 28775
		context.put(VM_UTIL, new VmUtil());
		String accptDate = "";
		
		SmMofAccount mofAccount =  SmApplicationUtil.getActiveMofAccount(svTask.getSmAppl().getSmSupplier().getSmMofAccounts(),
				svTask.getSmAppl().getSupplierType());
		SmAddress addr = new SmAddress();
		if(svTask.getSmAppl().getSmCompanyBasic() != null &&
				svTask.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses()!= null && 
				!svTask.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().isEmpty()){
			addr = svTask.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress();
			
		}
		
		StringBuilder sb = new StringBuilder();
		int i = 0;
		if(svTask.getSmSvOfficers() != null && !svTask.getSmSvOfficers().isEmpty()){
			for(SmSvOfficer offcr : svTask.getSmSvOfficers()){
				sb.append(++i + ". ");
				sb.append(offcr.getPmUser().getUserName());
				sb.append("<br/>");
			}
			accptDate = DateUtils.dateToString("dd/MM/yyyy", svTask.getSmSvOfficers().get(0).getCreatedDate()) ;
		}else{
			sb.append("");
		}
		//#28774 - Start - Display names of Site Visit Officers
		StringBuilder assistantOfficer = new StringBuilder();
		int aOfficer = 0;
		if(svTask.getSmSvAssistantOfficers() != null  && !svTask.getSmSvAssistantOfficers().isEmpty()){
			for(SmSvAssistantOfficer smSvAssistantOfficer : svTask.getSmSvAssistantOfficers()){
				assistantOfficer.append(++aOfficer + ". ");
				assistantOfficer.append(smSvAssistantOfficer.getName());
				assistantOfficer.append("<br/>");
			}
		}
		//#28774 - End
		context.put("hostUrl", getLogoHeaderPath());
		context.put("accptDate", accptDate);
		context.put("svOfficers", sb.toString());
		//#28774 - Start
		context.put("svAOfficer", assistantOfficer.toString());
		//#28774 - End
		context.put("addrLiines",getAddressLines(addr));
		context.put("postCode", (addr.getPostcode() != null && addr.getPostcode().length() > 0)? addr.getPostcode() : "");
		context.put("townDtls", (addr.getPmCity() != null && addr.getPmCity().getCityName() !=null && addr.getPmCity().getCityName().length() > 0)? addr.getPmCity().getCityName() : "");
		context.put("districtDtls", (addr.getPmDistrict() != null && addr.getPmDistrict().getDistrictName() !=null && addr.getPmDistrict().getDistrictName().length() > 0)? addr.getPmDistrict().getDistrictName() : "");
		context.put("stateDtls", (addr.getPmState() != null && addr.getPmState().getStateName() !=null && addr.getPmState().getStateName().length() > 0)? addr.getPmState().getStateName() : "");
		//#34159 - START 
		boolean isStateAdoptDivision;
		if(addr.getPmState() != null && addr.getPmState().getStateName() !=null && addr.getPmState().getStateName().length() > 0 && addr.getPmState().getAdoptDivision()){
			isStateAdoptDivision = true;
			context.put("isStateAdoptDivision", isStateAdoptDivision);
			context.put("divisionDtls", (addr.getPmDivision() != null && addr.getPmDivision().getDivisionName() != null && addr.getPmDivision().getDivisionName().length() > 0)? addr.getPmDivision().getDivisionName() : "");
		}
		
		//#34159 - END
		
		context.put("companyName", svTask.getSmAppl().getSmSupplier().getCompanyName());
		context.put("busRegNo", svTask.getSmAppl().getSmSupplier().getRegNo());
		context.put("newBusinessRegistrationNo",svTask.getSmAppl().getSmSupplier().getSsmNewRegNo());
		context.put("mofNo",StringUtils.isNotBlank(mofAccount.getMofNo())?mofAccount.getMofNo() : "");
		context.put("applNo",svTask.getSmAppl().getApplNo());
		context.put("templateType",templateDesc);
		context.put(VM_UTIL, VmUtil.getInstance());
		context.put("finalInfoDate", DateUtils.dateToString("dd/MM/yyyy",svTask.getSmAppl().getChangedDate()));
		
		if(review){
			context.put("intRepresentatives", svTask.getSmSvTemplate().getSmSvInterviewees());
		}
		
		
		String fileName = fileInitName + "compInfo.pdf";
		
		return generateToPdf(vmCompInfo, context, fileName);
	}
	
	/**
	 * Generate Site Visit Template -  Section A
	 * @param svTask
	 * @return String
	 * @throws Exception
	 */
	private String commonSiteVisitSectionA(SmSvTask svTask) throws Exception{
		VelocityContext context = new VelocityContext();
		String vmEquity= "/sm/sitevisit/sv-section-a.vm";
		//28775 - Site Visit Grouping Enhancement - 28775
		context.put(VM_UTIL, new VmUtil());
		context.put("hostUrl", getLogoHeaderPath());
		context.put("authorizedCapital", formatEquity(svTask.getSmAppl().getSmCompanyBasic().getAuthorizedCapital()));
		context.put("paidUpCapital", formatEquity(svTask.getSmAppl().getSmCompanyBasic().getPaidUpCapital()));
		context.put("countOwner", this.getManagedBean().getTotalEquityOwner());
		context.put("bumiEquity", formatEquity(svTask.getSmAppl().getSmCompanyBasic().getBumiEquity()));
		context.put("nonBumiEquity", formatEquity(svTask.getSmAppl().getSmCompanyBasic().getNonBumiEquity()));
		context.put("nonMalaysianEquity", formatEquity(svTask.getSmAppl().getSmCompanyBasic().getForeignerEquity()));
		context.put("govtUnitrustEquity", formatEquity(svTask.getSmAppl().getSmCompanyBasic().getGovtUnitrustEquity()));
		context.put("govtEquity", formatEquity(svTask.getSmAppl().getSmCompanyBasic().getGovtEquity()));
		context.put("localCompanyEquity", formatEquity(svTask.getSmAppl().getSmCompanyBasic().getLocalCompanyEquity()));
		context.put("foreignCompanyEquity", formatEquity(svTask.getSmAppl().getSmCompanyBasic().getForeignCompanyEquity()));
		
		context.put("bumiEquityPct", calculateEquityPercentage(svTask.getSmAppl().getSmCompanyBasic().getBumiEquity().toString()));
		context.put("nonBumiEquityPct", calculateEquityPercentage(svTask.getSmAppl().getSmCompanyBasic().getNonBumiEquity().toString()));
		context.put("nonMalaysianEquityPct", calculateEquityPercentage(svTask.getSmAppl().getSmCompanyBasic().getForeignerEquity().toString()));
		context.put("govtUnitrustEquityPct", calculateEquityPercentage(svTask.getSmAppl().getSmCompanyBasic().getGovtUnitrustEquity().toString()));
		context.put("govtEquityPct", calculateEquityPercentage(svTask.getSmAppl().getSmCompanyBasic().getGovtEquity().toString()));
		context.put("localCompanyEquityPct", calculateEquityPercentage(svTask.getSmAppl().getSmCompanyBasic().getLocalCompanyEquity().toString()));
		context.put("foreignCompanyEquityPct", calculateEquityPercentage(svTask.getSmAppl().getSmCompanyBasic().getForeignCompanyEquity().toString()));
		
		context.put("ownerList", this.getManagedBean().getEquityOwnerLs());
		context.put("companyName", svTask.getSmAppl().getSmSupplier().getCompanyName());
		context.put("totalBOD", this.getManagedBean().getTotalBOD());
		context.put("dirBumi", this.getManagedBean().getDirBumi());
		context.put("dirNonBumi", this.getManagedBean().getDirNonBumi());
		context.put("dirNonMalaysian", this.getManagedBean().getDirNonMalaysian());
		
		context.put("dirBumiPct", calculateDirectorPercentage(this.getManagedBean().getDirBumi()));
		context.put("dirNonBumiPct", calculateDirectorPercentage(this.getManagedBean().getDirNonBumi()));
		context.put("dirNonMalaysianPct", calculateDirectorPercentage(this.getManagedBean().getDirNonMalaysian()));
		context.put("bodLs", this.getManagedBean().getBodLs());
		
		context.put("totalMgmt", this.getManagedBean().getTotalMgmt());
		context.put("mgmtBumi", this.getManagedBean().getMgmtBumi());
		context.put("mgmtNonMalaysian", this.getManagedBean().getMgmtNonMalaysian());
		context.put("mgmtNonBumi", this.getManagedBean().getMgmtNonBumi());
		
		context.put("mgmtBumiPct", calculateMgmtPercentage(this.getManagedBean().getMgmtBumi()));
		context.put("mgmtNonMalaysianPct", calculateMgmtPercentage(this.getManagedBean().getMgmtNonMalaysian()));
		context.put("mgmtNonBumiPct",  calculateMgmtPercentage(this.getManagedBean().getMgmtNonBumi()));
		
		context.put("totalWorker", this.getManagedBean().getTotalWorker());
		context.put("workerBumi", this.getManagedBean().getWrkBumi());
		context.put("workerNonBumi", this.getManagedBean().getWrkNonBumi());
		context.put("workerNonMalaysian", this.getManagedBean().getWrkNonMalaysian());
		
		context.put("workerBumiPct", calculateWorkerPercentage(this.getManagedBean().getWrkBumi()));
		context.put("workerNonBumiPct", calculateWorkerPercentage(this.getManagedBean().getWrkNonBumi()));
		context.put("workerNonMalaysianPct", calculateWorkerPercentage(this.getManagedBean().getWrkNonMalaysian()));
		
		context.put("templateType",templateDesc);
		context.put(VM_UTIL, VmUtil.getInstance());
		context.put("staffLs", getManagedBean().getStaffLs());
		
		boolean authorizedCapitalFlag = false;

		// SIR #3108
		//#29781 - start
		/*
		context.put("higherManagementNames", this.getManagedBean().getHigherManagementNames());
		context.put("managementNames", this.getManagedBean().getManagementNames());	
		*/
		context.put("managementLs", getManagedBean().getMgmtLs());
		//#29871 - end
		
		String businessType = svTask.getSmAppl().getSmSupplier().getBusinessType();
		if(businessType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_LLP) 
				|| businessType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_SDN_BHD)
				|| businessType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_BHD)){
			context.put("equityCompanies", svTask.getSmAppl().getSmEquityCompanies());
			authorizedCapitalFlag = true;
		}
		context.put("authorizedCapitalFlag", authorizedCapitalFlag);
		
		String fileName = fileInitName + "sectionA.pdf";
		//CR 28774: Static Code Analysis 
		return generateToPdf(vmEquity, context, fileName);
	}
	
	//#34166 - Start
	/**
	 * To validate before downloading the SV Report.
	 * @param clientId
	 * @param svTask
	 * @param retrieveSvInfo
	 */
	public void validateBeforeDownloadSVReport(String clientId, SmSvTask svTask, boolean retrieveSvInfo) {
		String fieldsName = "";
		SmSvTask svData = new SmSvTask();

		if (retrieveSvInfo) {
			getManagedBean().setSvTaskId(svTask.getSvTaskId());
			retrieveSiteVisitInfo("V");
			svData = getManagedBean().getSmSvTask();
		} else {
			svData = svTask;
		}

		for (SmSvBankInfo bank : svData.getSmSvBankInfos()) {
			if (bank.getRemark() != null && (bank.getRemark().contains("<") || bank.getRemark().contains(">"))) {
				fieldsName = ResourceBundleUtils.getMessage("lbl.sm.sv.bank.remark",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale);
				throw new EpException(SmCommonUtil.getOutputMsgBundle(MessageConst.ERROR_775197,
						new String[] { fieldsName.toLowerCase() }));
			}
			if (bank.getSmSupplierBank().getSignatureInstruction() != null
					&& (bank.getSmSupplierBank().getSignatureInstruction().contains("<") || bank.getSmSupplierBank()
							.getSignatureInstruction().contains(">"))) {
				fieldsName = ResourceBundleUtils.getMessage("lbl.sm.sv.bank.terms.cond",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale);
				throw new EpException(SmCommonUtil.getOutputMsgBundle(MessageConst.ERROR_775197,
						new String[] { fieldsName.toLowerCase() }));
			}
		}

		for (SmSvFinChecklist checklist : svData.getSmSvFinChecklists()) {
			if (checklist.getRemark() != null
					&& (checklist.getRemark().contains("<") || checklist.getRemark().contains(">"))) {
				fieldsName = getParameterDesc(ParameterTypeConst.SVO_FIN_CHECKLIST, checklist.getPmParameter()
						.getParameterCode()) + " remarks";

				throw new EpException(SmCommonUtil.getOutputMsgBundle(MessageConst.ERROR_775197,
						new String[] { fieldsName.toLowerCase() }));
			}
		}

		RequestContext.getCurrentInstance().execute("document.getElementById('" + clientId + "').click()");
	}
	
	/**
	 * To change the character that might give error when downloading the files
	 * @param svTask
	 */
	public void validatingCharacter(SmSvTask svTask) {
		for (SmSvBankInfo bank : svTask.getSmSvBankInfos()) {
			if (bank.getRemark() != null && (bank.getRemark().contains("<") || bank.getRemark().contains(">"))) {
				bank.setRemark(bank.getRemark().replace("<", "&#60;").replace(">", "&#62;"));
			}
			if (bank.getSmSupplierBank().getSignatureInstruction() != null
					&& (bank.getSmSupplierBank().getSignatureInstruction().contains("<") || bank.getSmSupplierBank()
							.getSignatureInstruction().contains(">"))) {
				bank.getSmSupplierBank().setSignatureInstruction(
						bank.getSmSupplierBank().getSignatureInstruction().replace("<", "&#60;").replace(">", "&#62;"));
			}
		}

		for (SmSvFinChecklist checklist : svTask.getSmSvFinChecklists()) {
			if (checklist.getRemark() != null
					&& (checklist.getRemark().contains("<") || checklist.getRemark().contains(">"))) {
				checklist.setRemark(checklist.getRemark().replace("<", "&#60;").replace(">", "&#62;"));

			}
		}

		getManagedBean().setSmSvTask(svTask);
	}
	//#34166 - End
	
	/**
	 * Generate Site Visit Template - Section B
	 * @param svTask
	 * @return String
	 * @throws Exception
	 */
	private String commonSiteVisitSectionB(SmSvTask svTask, boolean review) throws Exception{
		VelocityContext context = new VelocityContext();
		String vmEquity= "/sm/sitevisit/sv-section-b.vm";
		//28775 - Site Visit Grouping Enhancement - 28775
		context.put(VM_UTIL, new VmUtil());
		List<String> finLsItem = new ArrayList<>();
		List<SmSvFinChecklist> finCheckList = new ArrayList<>();
		List<PmParameterDesc> pmParamDesc = SmCommonUtil.retrieveParameterDescList(
				ParameterTypeConst.SVO_FIN_CHECKLIST, LANG_CODE);
		
		if(!review){
			for(PmParameterDesc finChkLs : pmParamDesc){
				finLsItem.add(finChkLs.getCodeName());
			}
			context.put("financialCheckList", finLsItem);
		}else{
			for(SmSvFinChecklist fcLs : svTask.getSmSvFinChecklists()){
				finCheckList.add(fcLs);
			}
			context.put("financialCheckList", finCheckList);
		}
		
		//svoSiteVisitBackingBean.managedBean.smSvTask.smSvBankInfos
		//svoSiteVisitBackingBean.managedBean.totalCheqSign
		context.put("totolCheqSignatory", this.getManagedBean().getTotalCheqSign());
		context.put("bumiCheqSign", this.getManagedBean().getCheqBumi());
		context.put("nonBumiCheqSign", this.getManagedBean().getCheqNonBumi());
		context.put("nonMalaysianCheqSign", this.getManagedBean().getCheqNonMalaysian());
		
		context.put("bumiCheqSignPct", calculatChequeSignatoryPercentage(this.getManagedBean().getCheqBumi()));
		context.put("nonBumiCheqSignPct", calculatChequeSignatoryPercentage(this.getManagedBean().getCheqNonBumi()));
		context.put("nonMalaysianCheqSignPct", calculatChequeSignatoryPercentage(this.getManagedBean().getCheqNonMalaysian()));
		
		context.put("hostUrl", getLogoHeaderPath());
		context.put("bankInfo", svTask.getSmSvBankInfos());
		
		context.put("templateType",templateDesc);
		context.put("review", review);
		
		// #14055 - start
		context.put("malaysiaCode", SupplierManagementConst.COUNTRY_MALAYSIA_CODE);			
		// #14055 - end
		
		String fileName = fileInitName + "sectionB.pdf";
		//String file = generateToPdf(vmEquity, context, fileName);
		
		return generateToPdf(vmEquity, context, fileName);
	}
	
	/**
	 * Generate Site Visit Template - Section C
	 * @param svTask
	 * @param templateType
	 * @return String
	 * @throws Exception
	 */
	private String commonSiteVisitSectionC(SmSvTask svTask, boolean review) throws Exception{
		
		SmSvTemplate svTemplate = svTask.getSmSvTemplate();
		String templateType = svTemplate.getPmParameter().getParameterCode();
	
		VelocityContext context = new VelocityContext();
		String vmSectionC = "/sm/sitevisit/sv-section-c.vm";
		
		if(templateType.equals(SupplierManagementConst.CAT_TYPE_DOCKYARD) || templateType.equals(SupplierManagementConst.CAT_TYPE_WORKSHOP)){
			List<PmParameterDesc> licenseInsuranceLs = SmCommonUtil.retrieveParameterDescList(ParameterTypeConst.DOCKYARD_WORKSHOP_LICENSE_INS, LANG_CODE);
			
			SmFacility smFacility = retrieveFacilityByAddress(svTask.getSmSvAddress().getSiteRefId());
			String lblOwnerShip = null;
			if(smFacility != null){
				if(smFacility.getOwnershipType().equalsIgnoreCase("O")){
					lblOwnerShip = "lbl.sm.facility.own";
				}else{
					lblOwnerShip ="lbl.sm.facility.rent";
				}
			}
			
			String preLocOwnerShip = lblOwnerShip != null ? ResourceBundleUtils.getMessage(lblOwnerShip,ResourceBundleUtils.BUNDLE_COMMON_LABELS,locale) : "";

			//DecimalFormat formatter = new DecimalFormat("###,##0.00");
			String preLocPurchasePrice = smFacility != null && smFacility.getPurchasePrice() != null ? formatter.format(smFacility.getPurchasePrice()) : null;
			String preLocRentalRate = smFacility != null &&  smFacility.getRentalRate() != null ? formatter.format(smFacility.getRentalRate()) : null;
			
			context.put("ownerShipType", preLocOwnerShip);
			context.put("preLocPurchasePrice", preLocPurchasePrice);
			context.put("preLocRentalRate", preLocRentalRate);
			
			PmParameter equipmentMO = SmCommonUtil.
					retrieveParameterByTypeAndCode(ParameterTypeConst.MACHINE_GROUP_TYPE, ParameterConst.EQUIPMENT_MAINTENANCE_OVERHAUL, new FacesScopes().getUserLocale().getLanguage());
			PmParameter equipmentPS = SmCommonUtil.
					retrieveParameterByTypeAndCode(ParameterTypeConst.MACHINE_GROUP_TYPE, ParameterConst.EQUIPMENT_PANEL_SPRAY, new FacesScopes().getUserLocale().getLanguage());
			
			List<PmMachineSection> machineSectionMO = retrieveMachineSectionBySectionType(equipmentMO.getParameterId(), svTemplate.getPmParameter().getParameterId());
			List<PmMachineSection> machineSectionPS = retrieveMachineSectionBySectionType(equipmentPS.getParameterId(), svTemplate.getPmParameter().getParameterId());
			
			if((templateType.equals(SupplierManagementConst.CAT_TYPE_DOCKYARD) || 
					templateType.equals(SupplierManagementConst.CAT_TYPE_WORKSHOP)) && review){
				List<SmSvLicenseInsurance> dockyardLicense = svTask.getSmSvTemplate().getSmSvLicenseInsurances();
				
				context.put("licenseInsurance", dockyardLicense);
				context.put("facility", svTask.getSmSvTemplate().getSmSvFacility());
				
				if(templateType.equals(SupplierManagementConst.CAT_TYPE_WORKSHOP)){
					context.put("workerLs", svTask.getSmSvTemplate().getSmSvWorkers());
				}
			}else{
				context.put("licenseInsurance", licenseInsuranceLs);
			}
			context.put("equipmentMaintenance", 
					review ? filterPSorMOEquipmentBySectionId(svTask.getSmSvTemplate().getSmSvEquipments(),equipmentMO.getParameterId()) : machineSectionMO);
			context.put("equipmentBeating", 
					review ? filterPSorMOEquipmentBySectionId(svTask.getSmSvTemplate().getSmSvEquipments(),equipmentPS.getParameterId()) : machineSectionPS);
			context.put("machineLs", svTask.getSmAppl().getSmMachineDtls());
			
		}else if(templateType.equals(SupplierManagementConst.CAT_TYPE_MANUFACTURING)){
			
			if(review){
				context.put("financialDocLs", svTask.getSmSvTemplate().getSmSvManufacturingDocs());
			}else{
				List<PmParameterDesc> financialDocLs = SmCommonUtil.retrieveParameterDescList(ParameterTypeConst.MANUFACTURING_FINANCIAL_DOC, LANG_CODE);
				context.put("financialDocLs", financialDocLs);
				context.put("additionalDocLs", "");
			}
			context.put("machineLs", svTask.getSmAppl().getSmMachineDtls());
			context.put("manufacturingLs", svTask.getSmAppl().getSmManufacturingDtls());
			
		}else if(templateType.equals(SupplierManagementConst.CAT_TYPE_PRINTING)){
			
			List<PmParameterDesc> licenseInsuranceLs = SmCommonUtil.retrieveParameterDescList(ParameterTypeConst.PRINTING_LICENSE_INS, LANG_CODE);
			SmFacility smFacility = retrieveFacilityByAddress(svTask.getSmSvAddress().getSiteRefId());
			
			String lblOwnerShip = null;
			if(smFacility != null){
				if(smFacility.getOwnershipType().equalsIgnoreCase("O")){
					lblOwnerShip = "lbl.sm.facility.own";
				}else{
					lblOwnerShip ="lbl.sm.facility.rent";
				}
			}
			String preLocOwnerShip = lblOwnerShip != null ? ResourceBundleUtils.getMessage(lblOwnerShip,ResourceBundleUtils.BUNDLE_COMMON_LABELS,locale) : "";

			//DecimalFormat formatter = new DecimalFormat("###,##0.00");
			String preLocPurchasePrice = smFacility != null && smFacility.getPurchasePrice() != null ? formatter.format(smFacility.getPurchasePrice()) : null;
			String preLocRentalRate = smFacility != null && smFacility.getRentalRate() != null ? formatter.format(smFacility.getRentalRate()) : null;
			
			context.put("ownerShipType", preLocOwnerShip);
			context.put("preLocPurchasePrice", preLocPurchasePrice);
			context.put("preLocRentalRate", preLocRentalRate);
			
			context.put("licenseInsuranceLs", review ? svTask.getSmSvTemplate().getSmSvLicenseInsurances() : licenseInsuranceLs);
			
			if(getManagedBean().getSupplierCategory() != null){

				SmSupplierCategory supplierCategory = getManagedBean().getSupplierCategory();
				PmCategoryL3 catL3 = supplierCategory.getPmCategoryL3();
				PmCategoryL2 catL2 = supplierCategory.getPmCategoryL2();
				PmCategoryL1 catL1 = supplierCategory.getPmCategoryL1();
				
				Long lv1Id = catL1.getCategoryL1Id();
				Long lv2Id = catL2.getCategoryL2Id();
				Long lv3Id = catL3.getCategoryL3Id();

				String catCode =  catL1.getCategoryL1Code() + 
						catL2.getCategoryL2Code() + catL3.getCategoryL3Code();

				if(catL3.getCategoryTypeCode().getParameterCode().equalsIgnoreCase(SupplierManagementConst.CAT_TYPE_PRINTING)){

					String categoryCodeDesc = catCode + " " + catL3.getCategoryName();
					
					context.put("purchaseMethodMap", getAllPurchaseMethod());
					if(review){
						context.put("pCat", categoryCodeDesc);
						context.put("printingMachineLs", svTask.getSmSvTemplate().getSmSvMachines());
					}else{
						List<PmMachineCategory> pmMachineCat = retrievePrintingAcc(lv1Id,lv2Id,lv3Id); //temp disable
						context.put("pCat", categoryCodeDesc);
						context.put("printingMachineLs", pmMachineCat);
						
						context.put("machineLs", svTask.getSmSvTemplate().getSmSvMachines());
					}
				}
			}

		}
		
		if(templateType.equals(SupplierManagementConst.CAT_TYPE_WORKSHOP) || 
				templateType.equals(SupplierManagementConst.CAT_TYPE_DOCKYARD) || 
				templateType.equals(SupplierManagementConst.CAT_TYPE_MANUFACTURING) || 
				templateType.equals(SupplierManagementConst.CAT_TYPE_PRINTING)){

			context.put("purchaseMethodMap", getAllPurchaseMethod());
		}
		
		context.put(VM_UTIL, new VmUtil());
		context.put("hostUrl", getLogoHeaderPath());
		context.put("templateType",templateDesc);
		context.put("templateCode", templateType);
		context.put("review", review);
		context.put("svTask", svTask);
		//#28774 - START 
		context.put("categoryCodeLs", this.getManagedBean().getCategoryApplicable());
		context.put(VM_UTIL, new VmUtil());
		//#28874 - END
		
		String fileName = fileInitName + "sectionC.pdf";
		//CR 28774: Static Code Analysis
		return generateToPdf(vmSectionC, context, fileName);
	}
	
	/**
	 * Generate Site Visit Template - Section D
	 * @param svTask
	 * @param templateType
	 * @return String
	 * @throws Exception
	 */
	private String commonSiteVisitSectionD(SmSvTask svTask, boolean review) throws Exception{
		VelocityContext context = new VelocityContext();
		String vmSectionD = "/sm/sitevisit/sv-section-d.vm";
		
		String templateType = svTask.getSmSvTemplate().getPmParameter().getParameterCode();
		
		
		if(templateType.equals(SupplierManagementConst.CAT_TYPE_INSURANCE) || templateType.equals(SupplierManagementConst.CAT_TYPE_BUMIPUTERA) || 
				templateType.equals(SupplierManagementConst.CAT_TYPE_READING_MATERIAL_FROM_OVERSEA) || templateType.equals(SupplierManagementConst.CAT_TYPE_TRANSPORTATION) || 
				templateType.equals(SupplierManagementConst.CAT_TYPE_TRAVEL)){

			String officeOwnership = this.getManagedBean().getSmSvTask().getSmSvOffice().getSmFacility().getOwnershipType();
			BigDecimal officePrice =  this.getManagedBean().getSmSvTask().getSmSvOffice().getSmFacility().getPurchasePrice();
			BigDecimal officeRentalRate = this.getManagedBean().getSmSvTask().getSmSvOffice().getSmFacility().getRentalRate();

			//DecimalFormat formatter = new DecimalFormat("###,##0.00");
			String svOfficePurchasePrice = officePrice != null ? formatter.format(officePrice) : null;
			String svOfficeRentalRate = officeRentalRate != null ? formatter.format(officeRentalRate) : null;
			
			context.put("officeOwnership", officeOwnership);
			context.put("officePrice", svOfficePurchasePrice);
			context.put("officeRentalRate", svOfficeRentalRate);
		
			if(review){
				context.put("svTask", svTask);
			}
			
		}
		//#28774 - START
		context.put("categoryCodeLs", this.getManagedBean().getCategoryApplicable());
		context.put(VM_UTIL, new VmUtil());
		//#28874 - END
		context.put("hostUrl", getLogoHeaderPath());
		context.put("templateType",templateDesc);
		context.put("templateCode", templateType);
		context.put("review", review);
		
		String fileName = fileInitName + "sectionD.pdf";
		//CR 28774: Static Code Analysis
		return generateToPdf(vmSectionD, context, fileName);
	}
	
	/**
	 * Generate Site Visit Template - Section E
	 * @param svTask
	 * @param templateType
	 * @return String
	 * @throws Exception
	 */
	private String commonSiteVisitSectionE(SmSvTask svTask, boolean review) throws Exception{
		VelocityContext context = new VelocityContext();
		String vmSectionD = "/sm/sitevisit/sv-section-e.vm";
		
		String officeOwnership = this.getManagedBean().getSmSvTask().getSmSvOffice().getSmFacility().getOwnershipType();
		BigDecimal officePrice =  this.getManagedBean().getSmSvTask().getSmSvOffice().getSmFacility().getPurchasePrice();
		BigDecimal officeRentalRate = this.getManagedBean().getSmSvTask().getSmSvOffice().getSmFacility().getRentalRate();

		//DecimalFormat formatter = new DecimalFormat("###,##0.00");
		String svOfficePurchasePrice = officePrice != null ? formatter.format(officePrice) : null;
		String svOfficeRentalRate = officeRentalRate != null ? formatter.format(officeRentalRate) : null;
		//28775 - Site Visit Grouping Enhancement - 28775
		context.put(VM_UTIL, new VmUtil());
		context.put("hostUrl", getLogoHeaderPath());
		context.put("templateType",templateDesc);
		context.put("templateCode", svTask.getSmSvTemplate().getPmParameter().getParameterCode());
		context.put("officeOwnership", officeOwnership);
		context.put("officePrice", svOfficePurchasePrice);
		context.put("officeRentalRate", svOfficeRentalRate);
		context.put("review", review);
		
		if(review){
			context.put("svTask", svTask);
		}
		
		String fileName = fileInitName + "sectionE.pdf";
//		/CR 28774: Static Code Analysis
		return generateToPdf(vmSectionD, context, fileName);
	}
	
	/**
	 * Generate Site Visit Template - PDF - Preparation Details
	 * @param svTask
	 * @param templateType
	 * @return String
	 * @throws Exception
	 */
	private String generateToPdf(String vm, VelocityContext context, String fileName) throws Exception{
		String filePath = tmpPath + fileName;
		
		File tempFile1 = new File(tmpPath +  fileName);
		OutputStream os1 = new FileOutputStream(tempFile1);
		DocumentGenerator.getInstance().generatePdfFromVelocity(vm, context, os1);
		
		os1.flush();
		os1.close();
		
		return filePath;
	}
	
	public static void doMerge(List<InputStream> list, OutputStream outputStream)
            throws DocumentException, IOException {
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        document.open();
        PdfContentByte cb = writer.getDirectContent();
        
        for (InputStream in : list) {
            PdfReader reader = new PdfReader(in);
            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                document.newPage();
                //import the page from source pdf
                PdfImportedPage page = writer.getImportedPage(reader, i);
                //add the page to the destination pdf
                cb.addTemplate(page, 0, 0);
            }
            in.close();
        }
        
        outputStream.flush();
        document.close();
        outputStream.close();
    }

	/**
	 * get File
	 * @return StreamedContent
	 */
	public StreamedContent getFile() {
		return file;
	}

	/**
	 * get Template name from Label.properties
	 * @param templateType
	 * @return String
	 */
	public String getTemplateNameFromLabel(String templateType){
		Locale locale = new FacesScopes().getUserLocale();
		
		String lblTemplate = "lbl.sm.sitevisit.category.categorycode_"+templateType;
		//CR 28774: Static Code Analysis
		return ResourceBundleUtils.getMessage(lblTemplate,ResourceBundleUtils.BUNDLE_COMMON_LABELS,locale);
	}
	
	/**
	 * format address 
	 * @param smAddress
	 * @return
	 */
	private String getAddressLines(SmAddress smAddress) {
		StringBuilder address = new StringBuilder();
		if(smAddress!=null) {
			if (smAddress.getAddress1() != null && smAddress.getAddress1().length() > 0) {
				address.append(smAddress.getAddress1());
				address.append("<br />");
			}
			if (smAddress.getAddress2() != null && smAddress.getAddress2().length() > 0) {
				address.append(smAddress.getAddress2());
				address.append("<br />");
			}
			if (smAddress.getAddress3() != null && smAddress.getAddress3().length() > 0) {
				address.append(smAddress.getAddress3());
				address.append("<br />");
			}					
		}else{
			address.append("");
		}
		return address.toString();
	}
	
	/**
	 * show confirmation dialog
	 */
	public void submitDialogShow(){
		RequestContext.getCurrentInstance().execute("submissionConfirmDlg.show();");
	}
	
	/**
	 * add new additional machine
	 */
	public void addAdditionalMachine(){
		
		//Additional Machine for DOCKTARD, MANUFACTURING and WORKSHOP
		SmSvTemplate svTemplate = this.getManagedBean().getSelectedSvTemplate();
		if(svTemplate.getSmSvAdtMachines() == null){
			svTemplate.setSmSvAdtMachines(new ArrayList<SmSvAdtMachine>());
		}
		SmSvAdtMachine adtMachine = new SmSvAdtMachine();
		adtMachine.setValue(new BigDecimal("0.00"));
		adtMachine.setSmSvTemplate(svTemplate);
		
		this.getManagedBean().setSelectedAdtMachine(adtMachine);
		this.getManagedBean().getSelectedAdtMachine().setNewMachine(true);
	}
	
	/**
	 * remove selected additional machine from the list
	 */
	public void removeAdditionalMachine(){
		SmSvTemplate svTemplate = this.getManagedBean().getSelectedSvTemplate();
		if(svTemplate.getSelectedAdtMachines() != null && svTemplate.getSelectedAdtMachines().length > 0){
			for (SmSvAdtMachine adtMachine : svTemplate.getSelectedAdtMachines()) {
				svTemplate.getSmSvAdtMachines().remove(adtMachine);
			}
		}
	}
	
	/**
	 * add Additional Machine To List
	 */
	public void addAdditionalMachineToList(){
		SmSvAdtMachine adtMachine = this.getManagedBean().getSelectedAdtMachine();
		this.getManagedBean().getSelectedSvTemplate().getSmSvAdtMachines().add(adtMachine);
		RequestContext.getCurrentInstance().execute("adtMacDlg.hide()");
		
		this.getManagedBean().setSelectedAdtMachine(new SmSvAdtMachine());
	}
	
	/**
	 * save Edited AdditionalMachine
	 */
	public void saveAdditionalMachine(){
		
		SmSvAdtMachine adtMachine = getManagedBean().getSelectedAdtMachine();
		SmSvAdtMachine oriAdtMachine = (SmSvAdtMachine) getManagedBean().getOriSelectedMachine();
		
		oriAdtMachine.setBrandName(adtMachine.getBrandName());
		oriAdtMachine.setGuarantorName(adtMachine.getGuarantorName());
		oriAdtMachine.setMachineFunction(adtMachine.getMachineFunction());
		oriAdtMachine.setMachineType(adtMachine.getMachineType());
		oriAdtMachine.setPurchaseMethod(adtMachine.getPurchaseMethod());
		oriAdtMachine.setPurchaseMethodRemark(adtMachine.getPurchaseMethodRemark());
		oriAdtMachine.setPurchaseYear(adtMachine.getPurchaseYear());
		oriAdtMachine.setQty(adtMachine.getQty());
		oriAdtMachine.setValue(adtMachine.getValue());
		
		RequestContext.getCurrentInstance().execute("adtMacDlg.hide()");
	}
	
	/**
	 * edit Additional Machine
	 * @param SmSvAdtMachine adtMachine
	 */
	public void editMachine(Object obj){
		if(obj instanceof SmSvAdtMachine){
			getManagedBean().setOriSelectedMachine((SmSvAdtMachine) obj);
			SmSvAdtMachine svAdtMachine = (SmSvAdtMachine) ((SmSvAdtMachine) obj).cloneEntity();
			this.getManagedBean().setSelectedAdtMachine(svAdtMachine);
			this.getManagedBean().getSelectedAdtMachine().setNewMachine(false);
		}else if (obj instanceof SmSvMachine) {
			getManagedBean().setOriSelectedMachine((SmSvMachine) obj);
			//#28775
			SmSvMachine mach = (SmSvMachine) ((SmSvMachine) obj).cloneEntity();
			mach.setSmMachineDtl((SmMachineDtl) mach.getSmMachineDtl().cloneEntity());
			this.getManagedBean().setSelectedSvMachine(mach);
		} else {
			getManagedBean().setOriSelectedManufacturingDtl((SmManufacturingDtl) obj);
			SmManufacturingDtl manufacDtl = (SmManufacturingDtl) ((SmManufacturingDtl) obj).cloneEntity();
			this.getManagedBean().setSelectedManufacturer(manufacDtl);
			this.getManagedBean().getSelectedManufacturer().setBrandNew(false);
		}
	}
	
	/**
	 * save Site Visit Report
	 * @param action
	 * @return String - xhtml
	 */
	public String saveSiteVisitReport(String action){
		
		SiteVisitReqDTO reqDTO = new SiteVisitReqDTO();
		SmSvTask svTask = this.getManagedBean().getSmSvTask();
		//#34166 - Start
		checkRepresentative();
		//#34166 - End

		//#30550, 28775 - Site Visit Grouping Enhancement - 28775 - start
		if(getManagedBean().getSmSvTask().getSmSvTemplate().getSmSvInterviewees() == null ||
				getManagedBean().getSmSvTask().getSmSvTemplate().getSmSvInterviewees().isEmpty()){
			throw new EpException(SmCommonUtil.getMessage("lbl.sm.required.msg",
					new String[] { SmCommonUtil.getMessage("lbl.sm.sv.templ.interview.representative", null) }));
		}
		//#30550, 28775 - Site Visit Grouping Enhancement - 28775 - end

		SmSvTemplate svTemplate = svTask.getSmSvTemplate();
		if(svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_MANUFACTURING)){
			if(svTemplate.getAdditionalDocLs() != null && !svTemplate.getAdditionalDocLs().isEmpty()){
				for(SmSvManufacturingDoc mfDoc : svTemplate.getAdditionalDocLs()){
					if(mfDoc.isBrandNew()){
						svTemplate.getSmSvManufacturingDocs().add(mfDoc);
					}
				}

			}
			
		}
		//validate duplicate Workers
		if(svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_WORKSHOP)){
			validateDuplicateWorker(svTemplate.getSmSvWorkers());
			validateWorkshopWorkers(); //29379 - Site Visit Grouping Enhancement - 28775
		}
		
		try{
			//if Action equals to SUBMIT, set the BPM Info
			if(action.equals(SupplierManagementConst.BPM_SUBMIT)){
				//#32304 - start
				String[] user = getPoApproverLogin();
				((SMMOFRegDataType) getManagedBean().getBpmPojo()).setPoUsers(user[0]);
				reqDTO.setPoName(user[1]);
				Object token = getFacesScopes().getBPMAuthenticationCtxToken();
				reqDTO.setAppId(this.getManagedBean().getAppId());
				reqDTO.setToken(token);
				reqDTO.setOutcome(SupplierManagementConst.BPM_SUBMIT);
				reqDTO.setTaskId(this.getManagedBean().getTaskId());
				reqDTO.setBpmPojo(this.getManagedBean().getBpmPojo());
				//#30504 Re-open end #34795, #32304 end
			}
			//28775 - Site Visit Grouping Enhancement - 28775 - start
			if(svTask.getPmCategoryL3() != null){
				String paramCode = svTask.getPmCategoryL3().getCategoryTypeCode().getParameterCode();
				reqDTO.setUpdateSmAppl(paramCode.equals(SupplierManagementConst.CAT_TYPE_MANUFACTURING)||
						paramCode.equals(SupplierManagementConst.CAT_TYPE_TRAVEL));
			}
			//28775 - Site Visit Grouping Enhancement - 28775 - end
			//28775 - temp start
			if (svTask.getSmSvOffice().getSignboardRemark() != null)
				log.info("28775 Length - Signboard:" + svTask.getSmSvOffice().getSignboardRemark().length());
			if (svTask.getSmSvOffice().getShareWithRemark() != null)
				log.info("28775 Length - ShareWith:" + svTask.getSmSvOffice().getShareWithRemark().length());
			if (svTask.getSmSvOffice().getOfficeArrangement() != null)
				log.info("28775 Length - Arrangement:" + svTask.getSmSvOffice().getOfficeArrangement().length());
			//28775 - temp end
			reqDTO.setSmSvTask(svTask);
			reqDTO.setChangedBy(getUserIdSessionFromLoginId()); // #31849 add log key
			ISiteVisitSO reqSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISiteVisitSO.class);
			SiteVisitResDTO resDTO = reqSO.saveOrSubmitSiteVisitReport(reqDTO);
			
			FacesContext fc = FacesContext.getCurrentInstance();
			if (resDTO.isSuccessful()) {

				if(action.equalsIgnoreCase(SupplierManagementConst.BPM_SUBMIT)){
					//Start 25294  - Site Visit Grouping Enhancement -28775
					String poName = reqDTO.getPoName();			//#32304
					//Start pass second parameter to INFO_721210 #25294  - Site Visit Grouping Enhancement -28775
					fc.addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,
							MessageUtils.getMessage(MessageConst.INFO_721210,
							new String[]{this.getManagedBean().getSmSvTask().getSmAppl().getApplNo(),poName},
							MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
					//End 25294  - Site Visit Grouping Enhancement -28775
					//9608 - Site Visit Grouping Enhancement - 28775 - start, 30550
					return "/xhtml/common/acknowledge.xhtml";
				}else if (!action.equals(CHANGE_PG)){
					String msg = SmCommonUtil.getOutputMsgBundle(MessageConst.INFO_721222, 
							new String[]{SmCommonUtil.getMessage("lbl.sm.application.no", null), 
							getManagedBean().getSmSvTask().getSmAppl().getApplNo()});
					fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null, msg)); 				  
				}			
				getManagedBean().setFromReport(true);
				retrieveSiteVisitInfo("");
				getManagedBean().setFromReport(false);
				changeActiveIndex(getManagedBean().getActiveIndex(), getManagedBean().getSelectedSvTemplate());
				//9608 - Site Visit Grouping Enhancement - 28775 - end, 30550
			} else {
				RequestContext.getCurrentInstance().execute(submissionConfirmDlgConstant);
			}
		}catch(Exception ex){
			RequestContext.getCurrentInstance().execute(submissionConfirmDlgConstant);
			throw new EpException(ex.getMessage());
		}
		
		return null;
	}
	
	
	/**
	 * Listener when check the site visit equipment
	 * @param svEquipment
	 */
	public void svEquipmentOnChange(SmSvEquipment svEquipment){
		//if equipment is unchecked, set the qty to null
		if(!svEquipment.isEquipmentAvailable()){
			svEquipment.setEquipmentQty(null);
			refreshPage();
		}
	}
	
	/**
	 * enabled otherFloor/ other Environment remarks when user check 'Other'
	 */
	public void preLocationFloorEnvOnChange(){
		//SmSvFacility premiseLocation = t.managedBean.selectedSvTemplate.smSvFacility.
		SmSvFacility premiseLocation =  this.getManagedBean().getSelectedSvTemplate().getSmSvFacility();
		
		if(!premiseLocation.isOtherFloor()){
			premiseLocation.setOtherFloorRemark(null);
		}
		
		if(!premiseLocation.isOtherEnv()){
			premiseLocation.setOtherEnvRemark(null);
		}
	}
	
	/**
	 * Listener - svo check or unchecked the print machine
	 * @param printMachine
	 */
	public void printMachineSelectOnChange(SmSvPrintmachine printMachine){
		if(!printMachine.isSelected()){
			printMachine.setBrand(null);
			printMachine.setGuarantor(null);
			printMachine.setQty(null);
			printMachine.setRequisiteYear(null);
			printMachine.setRequisiteMethod(null);
			refreshPage();
		}
	}
	
	/**
	 * get formatted address
	 * @param SmAddress svAddress
	 * @return String
	 */
	public String displayAddress(SmAddress svAddress){
		return AddressUtils.getAddress(svAddress);
	}

	/**
	 * Refresh page - recreate view
	 */
	private void refreshPage() {
		FacesContext fc = FacesContext.getCurrentInstance();
		String refreshpage = fc.getViewRoot().getViewId();
		ViewHandler ViewH =fc.getApplication().getViewHandler();
		UIViewRoot UIV = ViewH.createView(fc,refreshpage);
		UIV.setViewId(refreshpage);
		fc.setViewRoot(UIV);
	}
	
	/**
	 * get the Site visit template logo path 
	 * @return String
	 */
	private String getLogoHeaderPath(){
		//CR 28774: Static Code Analysis
		log.info("getServerUrl()--:"+ getServerUrl());
		return getServerUrl();
		//return SmCommonUtil.getBasePortalUrl();
	}
	
	/**
	 * Validate duplicate Worker
	 */
	private void validateDuplicateWorker(List<SmSvWorker> svWorkers){
		if(svWorkers.size() > 1){
			List<String> workerLs = new ArrayList<String>();
			
			for(SmSvWorker worker : svWorkers){
				if(workerLs.contains(worker.getIdentificationNo())){
					throw new EpException(MessageConst.ERROR_721303);
				}
				workerLs.add(worker.getIdentificationNo());
			}
		}
	}
	
	/**
	 * Retrieve PmMachineSection List by SectionId, CatType
	 */
	private List<PmMachineSection> retrieveMachineSectionBySectionType(Long sectionId, Long catTypeId){
		CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
		reqDTO.setSectionId(sectionId);
		reqDTO.setCategoryTypeId(catTypeId);
		ICategoryCodeSO reqSO = (ICategoryCodeSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICategoryCodeSO.class);
		CategoryCodeResDTO resDTO = reqSO.retrievePmMachineSectionByCategoryTypeAndSection(reqDTO);
		
		return resDTO.getPmMachineSections();
	}
	
	/**
	 * Sitevisit report upload
	 */
	public void siteVisiteReportUpload(FileUploadEvent event) throws Exception {
		if (event.getFile() != null) {
			SmSvTemplate svTemplate = getManagedBean().getSelectedAttachmentTemplate();
			String subFolder = svTemplate.getSvTemplateId().toString();
			String filePath = SmCommonUtil.getUploadFilePath(SupplierManagementConst.SITE_VISIT_REPORT_PATH) + subFolder + "/";
			// #28672 START
			String fileName = String.valueOf((new Date()).getTime()) + "_" + StringUtils.replace(event.getFile().getFileName(), " ", "-"); 
			log.info("fatin test 1st:"+ fileName);
			// #28672 END 
			if(svTemplate.getSmAttachments() == null){
				svTemplate.setSmAttachments(new ArrayList<SmAttachment>());
			}

			SmAttachment attachment = new SmAttachment();
			// #28672 START
			attachment.setFileName(fileName);
			log.info("fatin test 2nd:"+ fileName);
			// #28672 END
			attachment.setFilePath(filePath);
			attachment.setDocId(svTemplate.getSvTemplateId());
			attachment.setDocType(attachmentType);
			svTemplate.getSmAttachments().add(attachment);
			SmCommonUtil.processUpload(filePath, event, fileName);
		}
	}
	
	/**
	 * set Selected Template for upload
	 * @param svTemplate
	 */
	public void setReportUploadTemplate(SmSvTemplate svTemplate){
		getManagedBean().setSelectedAttachmentTemplate(svTemplate);
	}
	
	/**
	 * get RecordStatusActive
	 * @return
	 */
	public int getRecordStatusActive(){
		return DbValueConst.RECORD_STATUS_ACTIVE;
	}
	
	
	/**
	 * remove uploaded site visit attachment
	 * @param templateType
	 * @param attachment
	 */
	public void removeUploadedSvFile(){
		SmAttachment attachment = getManagedBean().getSelectedUploadAttachment();
		
		if(getManagedBean().getSmSvTask().getSmSvTemplate() != null && attachment != null){
			SmSvTemplate svTemplate = getManagedBean().getSmSvTask().getSmSvTemplate();
			if(svTemplate.getSmAttachments() != null && !svTemplate.getSmAttachments().isEmpty()){
				int index = svTemplate.getSmAttachments().indexOf(attachment);
				SmAttachment attach = svTemplate.getSmAttachments().get(index);
				if(attach.getAttachmentId() != null){
					attach.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
				}else{
					svTemplate.getSmAttachments().remove(attachment);
				}
			}

		}
	}
	
	public void showDeleteDialog(Long templateId, SmAttachment attachment){
		String msg = SmCommonUtil.getMessage("lbl.sm.sitevisit.upload.delete.confirm", new String[]{attachment.getFileName()});
		
		getManagedBean().setAttachmentDeleteMsg(msg);
		getManagedBean().setSelectedAttachmentTemplId(templateId);
		getManagedBean().setSelectedUploadAttachment(attachment);
	}
	
	public void attachmentDownload(SmAttachment attachment) throws IOException{
		if(attachment != null){
			//CR 28774: Static Code Analysis
			commonDownload(attachment.getFilePath() + "/" + attachment.getFileName(), attachment.getFileName());
		}
	}

	/**
	 * review site visit report
	 */
	public void reviewSiteVisitReport(){
		getManagedBean().setViewMode(true);
		SiteVisitReqDTO reqDTO = new SiteVisitReqDTO();
		ISiteVisitSO reqSO = (ISiteVisitSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISiteVisitSO.class);
		reqDTO.setAppId(this.getManagedBean().getAppId());
		
		SmAppl appl = reqSO.retrieveSiteVisitInfoByAppId(reqDTO).getSmAppl();
		getManagedBean().setSmAppl(appl);
		
		for(SmSvTask smSvTask : appl.getSmSvTasks()){
			SmCommonUtil.getInstance().setSmSvAddressInfoBySiteTypeAndRefId(smSvTask.getSmSvAddress().getSiteType(),
					smSvTask.getSmSvAddress().getSiteRefId(),smSvTask.getSmSvAddress(), appl.getApplId());
			//28775 - Site Visit Grouping Enhancement - 28775 - start
			setSuppCategory(appl.getApplId(), smSvTask.getPmCategoryL3(), smSvTask.getSmSvTemplate());
			//max loop: 39
			for(SmSvTaskGroup svTGroup : smSvTask.getSmSvTaskGroups()){
				setSuppCategory(appl.getApplId(), svTGroup.getPmCategoryL3(), svTGroup.getSmSvTemplate());
			}
			//28775 - Site Visit Grouping Enhancement - 28775 - end
		}
		
		// sort by code
		Collections.sort(appl.getSmSvTasks(), new Comparator<SmSvTask>() {
			@Override
			public int compare(final SmSvTask s1, final SmSvTask s2) {
				String code1 = StringUtils.EMPTY;
				String code2 = StringUtils.EMPTY;

				if (s1.getPmCategoryL3() != null
						&& s1.getPmCategoryL3().getPmCategoryL2() != null
						&& s1.getPmCategoryL3().getPmCategoryL2()
								.getPmCategoryL1() != null) {
					code1 = s1.getPmCategoryL3().getPmCategoryL2()
							.getPmCategoryL1().getCategoryL1Code()
							+ s1.getPmCategoryL3().getPmCategoryL2()
									.getCategoryL2Code()
							+ s1.getPmCategoryL3().getCategoryL3Code();
				}
				if (s2.getPmCategoryL3() != null
						&& s2.getPmCategoryL3().getPmCategoryL2() != null
						&& s2.getPmCategoryL3().getPmCategoryL2()
								.getPmCategoryL1() != null) {
					code2 = s2.getPmCategoryL3().getPmCategoryL2()
							.getPmCategoryL1().getCategoryL1Code()
							+ s2.getPmCategoryL3().getPmCategoryL2()
									.getCategoryL2Code()
							+ s2.getPmCategoryL3().getCategoryL3Code();
				}

				return code1.compareTo(code2);
			}
		});

	}
	
	//28775 - Site Visit Grouping Enhancement - 28775
	private void setSuppCategory(Long applId, PmCategoryL3 l3, SmSvTemplate template){
		if(applId == null || l3 == null || template == null){
			return;
		}
		List<SmSupplierCategory> catLs = retrieveSupplierCategory(applId, l3.getCategoryL3Id());
		if(catLs == null || catLs.isEmpty()){
			return;
		}
		template.setSuppCategory(catLs.get(0));
	}
	
	/**
	 * set as selected report task
	 * @param svTask
	 */
	public void selectReportTask(SmSvTask svTask){
		getManagedBean().setSvTaskId(svTask.getSvTaskId());
		retrieveSiteVisitInfo("V");
		RequestContext.getCurrentInstance().update("@form");
	}
	
	/**
	 * get Branch or Associated Company Desc (for facility type)
	 * @param addressId
	 * @return String
	 */
	public String getBranchOrAssociateCompFaciclityDescByAddrressId(Long addressId){
		String facilityDesc = null;
		String facilityType = null;
		if(addressId != null){
			if(getManagedBean().getSmSvTask().getSmAppl().getSmSupplierBranches().size() > 0){
				for(SmSupplierBranch supplierBranch : getManagedBean().getSmSvTask().getSmAppl().getSmSupplierBranches()){
					if(supplierBranch.getSmAddress().getAddressId().longValue() == addressId){
						facilityType = "B";
						break;
					}
				}
			}
			
			//CR 28774: Static Code Analysis
			if(facilityType == null && !getManagedBean().getSmSvTask().getSmAppl().getSmAssociateCompanies().isEmpty()){
				//if(getManagedBean().getSmSvTask().getSmAppl().getSmAssociateCompanies().size() > 0){
					for(SmAssociateCompany associateCompany : getManagedBean().getSmSvTask().getSmAppl().getSmAssociateCompanies()){
						if(associateCompany.getSmAddress().getAddressId().longValue() == addressId){
							facilityType = "A";
							break;
						}
					}
				//}
			}
			
			if(facilityType != null){
				if(facilityType.equals("A")){
					facilityDesc = "lbl.sm.siteassign.facility.associate.company";
				}else{
					facilityDesc = "lbl.sm.siteassign.facility.branch";
				}
				
				return SmCommonUtil.getMessage(facilityDesc,null);
			}
		}
		
		return null;
	}
	
	
	private SmFacility retrieveFacilityByAddress(Long addressId){
		SiteVisitReqDTO reqDTO = new SiteVisitReqDTO();
		ISiteVisitSO reqSO = SmServiceLookup.getSiteVisitSO();
		
		reqDTO.setSmAddressId(addressId);
		SiteVisitResDTO resDTO = reqSO.retrieveSmFacilityByAddressId(reqDTO);
		
		return resDTO.getSmFacility();
		
	}
	
	/**
	 * Download PO Approval Site Visit Report
	 * @param svTask
	 * @throws Exception
	 */
	public void downloadPOApprovalSiteVisitReport(SmSvTask svTask) throws Exception{
		//#32963 - start
		if(svTask == null)
			svTask = getManagedBean().getSmSvTask();
		try{
			SiteVisitReqDTO reqDTO = new SiteVisitReqDTO();
			reqDTO.setSmSvTask(svTask);
			reqDTO.setChangedBy(getUserIdSessionFromLoginId()); // #31849 add log key
			((ISiteVisitSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISiteVisitSO.class))
					.saveOrSubmitSiteVisitReport(reqDTO);
		}catch(Exception ex){
			throw new EpException(ex.getMessage());
		}	
		//#32963 - end
		//#29430 - start
		getManagedBean().setFromReport(true);
		getManagedBean().setSvTaskId(svTask.getSvTaskId());
		retrieveSiteVisitInfo(""); //28774
		getManagedBean().setFromReport(false);
		//#29430 - end
		//#34166 - Start
		validatingCharacter(getManagedBean().getSmSvTask());
		//#34166 - End
		downloadSiteVisitTemplate(getManagedBean().getSmSvTask(), true);
	}
	
	/**
	 * Filter MO and PS Equipment
	 */
	private List<SmSvEquipment> filterPSorMOEquipmentBySectionId(List<SmSvEquipment> equipmentLs, Long sectionId){
		List<SmSvEquipment> svEquipmentLs = new ArrayList<SmSvEquipment>();
		
		if(equipmentLs != null && !equipmentLs.isEmpty()){
			for(SmSvEquipment equipment : equipmentLs){
				if(equipment.getPmMachineSection().getSection().getParameterId().longValue() == sectionId.longValue()){
					svEquipmentLs.add(equipment);
				}
			}
		}
		return svEquipmentLs;
	}
	
	public void retrievUploadedSiteVisitReport(SmSvTemplate template){
		//CR 28774: Static Code Analysis Start
		if(template != null && template.getSmAttachments() == null){
				//if(template.getSmAttachments() == null){
						ISmAttachmentSO reqSO = SmServiceLookup.getSmAttachmentSO();
						SmAttachmentReqDTO reqDTO = new SmAttachmentReqDTO();
						reqDTO.setDocType(attachmentType);
						reqDTO.setDocId(template.getSvTemplateId());
						SmAttachmentResDTO resDTO = reqSO.retrieveSmAttachmentListByDocIdAndType(reqDTO);
						
						template.setSmAttachments(resDTO.getAttachmentLs());
				//}
		}
		//CR 28774: Static Code Analysis End
	}
	
	public void onClickClaimButton(){
		claimBpmTask();
		getManagedBean().setClaimTask(true);
	}
	
	public String getBusinessTypeDesc(){
		SmAppl appl = getManagedBean().getSmSvTask().getSmAppl();
		String paramCode = appl.getSmSupplier().getBusinessType();
		if(appl.getSupplierType().equalsIgnoreCase(SupplierManagementConst.STYPE_CONTRACTOR)){
			return SmCommonUtil.
					retrieveParameterDescByTypeAndCodeAndLang(ParameterTypeConst.SUPPLIER_BUSINESS_TYPE_CONTRACTOR,paramCode).getCodeName();
		}else{
			return SmCommonUtil.
					retrieveParameterDescByTypeAndCodeAndLang(ParameterTypeConst.SUPPLIER_BUSINESS_TYPE_CONSULTANT,paramCode).getCodeName();
		}
	}
	
	public String getCompanyAddressInLine(){
		return AddressUtils.getFullAddress(getManagedBean().getSmSvTask().getSmAppl().
				getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress());
	}
	
	public int getSvTaskCountByApplIdAndCategoryId(){
		SiteVisitReqDTO reqDTO = new SiteVisitReqDTO();
		reqDTO.setAppId(getManagedBean().getSmSvTask().getSmAppl().getApplId());
		reqDTO.setCategoryL3Id(getManagedBean().getSmSvTask().getPmCategoryL3().getCategoryL3Id());
		
		ISiteVisitSO siteVisitSO = SmServiceLookup.getSiteVisitSO();
		SiteVisitResDTO resDTO = siteVisitSO.retrieveSiteVisitCountByApplIdAndCategoryL3Id(reqDTO);
		
		
		return resDTO.getSiteVisitCount();
	}
	
	public List<SmMachineCategory> retrieveMachineDtlByApplIdAndCategoryL3Id(Long applId, Long catL3Id){
		SiteVisitReqDTO reqDTO = new SiteVisitReqDTO();
		reqDTO.setAppId(applId);
		reqDTO.setCategoryL3Id(catL3Id);
		SiteVisitResDTO resDTO = SmServiceLookup.getSiteVisitSO().retrieveMachineDtlByApplIdAndCategoryL3Id(reqDTO);
		
		return resDTO.getMachineCategoryLs();
	}
	
	public void setMachineList(){
		if(getManagedBean().getSmSvTask().getSmSvTemplate().getSmSvMachines() == null || 
				getManagedBean().getSmSvTask().getSmSvTemplate().getSmSvMachines().size() < 1){
			
			List<SmMachineCategory> machineCategoryLs = retrieveMachineDtlByApplIdAndCategoryL3Id(getManagedBean().getSmSvTask().getSmAppl().getApplId(),
					getManagedBean().getSmSvTask().getPmCategoryL3().getCategoryL3Id());
			//CR 28774: Static Code Analysis
			if(!machineCategoryLs.isEmpty()){
				for(SmMachineCategory machineCat : machineCategoryLs){
					SmSvMachine svMachine = new SmSvMachine();
					svMachine.setSmMachineDtl(machineCat.getSmMachineDtl());
					svMachine.setSmSvTemplate(getManagedBean().getSmSvTask().getSmSvTemplate());
					
					getManagedBean().getSmSvTask().getSmSvTemplate().getSmSvMachines().add(svMachine);
				}
			}
		}
	}
	
	public void saveSmSvMachine(){
		SmSvMachine svMachine = getManagedBean().getSelectedSvMachine();
		SmSvMachine oriMachine = (SmSvMachine) getManagedBean().getOriSelectedMachine();
		
		oriMachine.getSmMachineDtl().setBrandName(svMachine.getSmMachineDtl().getBrandName());
		oriMachine.getSmMachineDtl().setGuarantorName(svMachine.getSmMachineDtl().getGuarantorName());
		oriMachine.getSmMachineDtl().setMachineFunction(svMachine.getSmMachineDtl().getMachineFunction());
		oriMachine.getSmMachineDtl().setMachineType(svMachine.getSmMachineDtl().getMachineType());
		oriMachine.getSmMachineDtl().setPurchaseMethod(svMachine.getSmMachineDtl().getPurchaseMethod());
		oriMachine.getSmMachineDtl().setPurchaseYear(svMachine.getSmMachineDtl().getPurchaseYear());
		oriMachine.getSmMachineDtl().setQty(svMachine.getSmMachineDtl().getQty());
		oriMachine.getSmMachineDtl().setValue(svMachine.getSmMachineDtl().getValue());
		//28775 - Site Visit Grouping Enhancement - 28775 - start
		getManagedBean().setSelectedSvMachine(new SmSvMachine());
		getManagedBean().getSelectedSvMachine().setSmMachineDtl(new SmMachineDtl());
		//28775 - Site Visit Grouping Enhancement - 28775 - end
		RequestContext.getCurrentInstance().execute("macDlg.hide()");
	}
	
	public String naviToAcknowledgement(){
		return CommonUIConst.ACKNOWLEDGE_PAGE;
	}
	
	/*CR 28774: Static Code Analysis
	private SmAddress getSmAddressById(Long addressId){
		SmAddress address = SmCommonUtil.getInstance().getSmAddressById(addressId);
		return address;
	}
	*/
	
	private void retrieveAndSetSmSvDocLicense(){
		DocumentReqDTO reqDTO = new DocumentReqDTO();
		reqDTO.setApplId(getManagedBean().getSmSvTask().getSmAppl().getApplId());
		//#25158: checking null
		if(getManagedBean().getSupplierCategory() != null && getManagedBean().getSupplierCategory().getPmCategoryL3() != null){
			reqDTO.setCategoryL3Id(getManagedBean().getSupplierCategory().getPmCategoryL3().getCategoryL3Id());
		}
		List<PmDocument> documentLs = SmServiceLookup.getDocumentSO().retrieveLicenseSupportDocByCategoryL3AndApplId(reqDTO).getDocumentList();
		//getManagedBean().getSmSvTask()
		SmSvTask svTask = getManagedBean().getSmSvTask();
		svTask.setSmSvDocLicenses(new ArrayList<SmSvDocLicense>());
		
		//CR 28774: Static Code Analysis
		if(!documentLs.isEmpty()){
			Set<Long> docSet = new HashSet<Long>();
			for(PmDocument doc : documentLs){
				docSet.add(doc.getDocumentId());
			}
			
			//CR 28774: Static Code Analysis
			if(!docSet.isEmpty() && !svTask.getSmAppl().getSmApplSupportDocs().isEmpty()){
				for(SmApplSupportDoc suppDoc :getManagedBean().getSmSvTask().getSmAppl().getSmApplSupportDocs()){
					if(docSet.contains(suppDoc.getPmDocument().getDocumentId())){
						SmSvDocLicense svDocLicense = new SmSvDocLicense();
						svDocLicense.setSmSvTask(svTask);
						svDocLicense.setSmApplSupportDoc(suppDoc);
						svTask.getSmSvDocLicenses().add(svDocLicense);
					}
				}
			}
		}
	}
	
	private Map<String, String> getAllPurchaseMethod(){
		Map<String, String> purchaseMethodMap = new HashMap<>();
		//28775 - Site Visit Grouping Enhancement - 28775
		List<PmParameterDesc> purchaseMethodLs = ParameterHelper.getInstance()
				.getParameterDescList(ParameterTypeConst.PURCHASING_METHOD);
		//CR 28774: Static Code Analysis
		if(!purchaseMethodLs.isEmpty()){
			for(PmParameterDesc pmDesc : purchaseMethodLs){
				purchaseMethodMap.put(pmDesc.getPmParameter().getParameterCode(),pmDesc.getCodeName());
			}
		}
		
		return purchaseMethodMap;
	}

	public void addAdditionalManufacturing() {
		this.getManagedBean().getSmSvTask().getSmAppl().getSmManufacturingDtls();

		SmManufacturingDtl manufacDtl = new SmManufacturingDtl();
		manufacDtl.setRevNo(1);
		manufacDtl.setBrandNew(true);
		manufacDtl.setSmAppl(getManagedBean().getSmSvTask().getSmAppl());
		this.getManagedBean().setSelectedManufacturer(manufacDtl);

	}

	public void saveSmManufacturingDtl() {
		SmManufacturingDtl manufacturingDtl = getManagedBean().getSelectedManufacturer();
		SmManufacturingDtl oriManufacturingDtl = (SmManufacturingDtl) getManagedBean().getOriSelectedManufacturingDtl();
		//#28775
		if (getManagedBean().getManufacturingDtlLs() == null){
			getManagedBean().setManufacturingDtlLs(new ArrayList<SmManufacturingDtl>());
		}
		if (manufacturingDtl.isBrandNew()) {
			manufacturingDtl.setRevNo(1);
			manufacturingDtl.setSmAppl(getManagedBean().getSmSvTask().getSmAppl());
			getManagedBean().getManufacturingDtlLs().add(manufacturingDtl);
			this.getManagedBean().setSelectedManufacturer(new SmManufacturingDtl());
		} else {
			oriManufacturingDtl.setRevNo(1);
			oriManufacturingDtl.setGoodsType(manufacturingDtl.getGoodsType());
			oriManufacturingDtl.setBrand(manufacturingDtl.getBrand());
			oriManufacturingDtl.setProductionCapacity(manufacturingDtl.getProductionCapacity());
			oriManufacturingDtl.setUom(manufacturingDtl.getUom());
		}
		RequestContext.getCurrentInstance().execute("manufacturingDialog.hide()");
	}

	/**
	 * Delete or remove selected manufactures from the SmManufacturingDtl list
	 * of the application.
	 */
	public void removeAdditionalManufacturingDtl() {
		//CR 28774: Static Code Analysis, #28775
		List<SmManufacturingDtl> manufacturingList = getManagedBean().getSelectedManufacturingList();
		if (manufacturingList == null || manufacturingList.isEmpty()){
			throw new EpException(MessageConst.ERROR_722534, new String[] { "Manufacturer" });
		}
		EntityHelper.compareAndRemoveEntityFromList(manufacturingList, 
				getManagedBean().getSmSvTask().getSmAppl().getSmManufacturingDtls(), "manufacturingDtlId");
		
		for (SmManufacturingDtl manufac : manufacturingList){
			getManagedBean().getManufacturingDtlLs().remove(manufac);
		}
		getManagedBean().getSelectedManufacturingList().clear();
	}
	
	
	// #29200 CR 14055: Static Code Analysis
	public String getStatusFromLabel(String tempStatus) {		
		return ResourceBundleUtils.getMessage("lbl.sm.sv.templ." + tempStatus, ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
	}
	// #29200 CR 14055: Static Code Analysis

	//#28774 - Start
	/**
	 * add Assistant Officer
	 */
	public void addSvAsistantOfficer(){
		if(this.getManagedBean().getSmSvTask().getSmSvAssistantOfficers()==null){
			this.getManagedBean().getSmSvTask().setSmSvAssistantOfficers(new ArrayList<SmSvAssistantOfficer>());
		}
		SmSvAssistantOfficer smSvAssistantOfficer = new SmSvAssistantOfficer();
		smSvAssistantOfficer.setSmSvTask(this.getManagedBean().getSmSvTask());
		this.getManagedBean().getSmSvTask().getSmSvAssistantOfficers().add(smSvAssistantOfficer);
	}
	
	/**
	 * Temporary hold Assistant Officer to delete until yes/no choose
	 * @param smSvAssistantOfficer
	 */
	public void selectSvAssistantOfficer(SmSvAssistantOfficer smSvAssistantOfficer){
		this.getManagedBean().setSelectedAssistantOfficer(smSvAssistantOfficer);
		RequestContext.getCurrentInstance().execute("assistingOfficerDeletePopUp.show()");
	}
	
	/**
	 * remove the selected assistant officer(s)
	 */
	public void removeSvAssistantOfficer(){
		if(this.getManagedBean().getSelectedAssistantOfficer() != null){
			this.getManagedBean().getSmSvTask().getSmSvAssistantOfficers().remove(this.getManagedBean().getSelectedAssistantOfficer());
		}
		this.getManagedBean().setSelectedAssistantOfficer(null);
	}
	
	/**
	 * Check current user role to disable or enable the Assistant Officer name input
	 * @return boolean
	 */
	public boolean checkUserRole() {
		FacesScopes fs = new FacesScopes();
		//#24964, #28774 - start
		log.info("TR Troubleshoot #24964 === "+getManagedBean().getSmSvTask().getSmAppl().getPmStatus().getStatusId());
		if(getManagedBean().getSmSvTask().getSmAppl().getPmStatus().getStatusId() == SupplierManagementConst.PM_STATUS_PENDING_SITE_VISIT_REPORT_AFTER_APPROVED
			|| getManagedBean().getSmSvTask().getSmAppl().getPmStatus().getStatusId().equals(SupplierManagementConst.PM_STATUS_PENDING_REVIEW_SITE_VISIT_REPORT)) {
				log.info("TR Troubleshoot #24964 === 2");
				return true;
		}
		//#24964, #28774 - end
		if(this.getManagedBean().isViewMode()){
			log.info("TR Troubleshoot #24964 === 3");
			if(fs.hasRoleUser(RoleCode.UPS_APPROVER.value()) && 
					(this.getManagedBean().getSmSvTask().getSmAppl().getPmStatus().getStatusId()==20116 || 
					 this.getManagedBean().getSmSvTask().getSmAppl().getPmStatus().getStatusId()==20108)){
				log.info("TR Troubleshoot #24964 === 4");
				return true;
			}
				
			if(fs.hasRoleUser(RoleCode.UPS_PO.value()) && this.getManagedBean().getSmSvTask().getSmAppl().getPmStatus().getStatusId()==20103){
				log.info("TR Troubleshoot #24964 === 5");
				return true;
			}
			if(fs.hasRoleUser(RoleCode.UPS_SVO.value()) || 
					   fs.hasRoleUser(RoleCode.UPS_SDO.value()) || 
					   fs.hasRoleUser(RoleCode.UPS_PO.value())  || 
					   fs.hasRoleUser(RoleCode.UPS_APPROVER.value()) ||
					   fs.hasRoleUser(RoleCode.UPS_SVC.value()) ||
					   fs.hasRoleUser(RoleCode.BPK_AUDITOR.value()) ||
					   fs.hasRoleUser(RoleCode.UPS_ADMIN.value())){
						log.info("TR Troubleshoot #24964 === 6");
						return true;
			}
		}
		log.info("TR Troubleshoot #24964 === 7");
		return false;
	}
	
	/**
	 * Check if Assistant Officers is empty and create the first officers
	 */
	public void checkEmptyAssistantOfficers(){
		if(this.getManagedBean().getSmSvTask().getSmSvAssistantOfficers()== null || this.getManagedBean().getSmSvTask().getSmSvAssistantOfficers().isEmpty()){
			SmSvAssistantOfficer smSvAssistantOfficer = new SmSvAssistantOfficer();
			smSvAssistantOfficer.setSmSvTask(this.getManagedBean().getSmSvTask());
			this.getManagedBean().getSmSvTask().getSmSvAssistantOfficers().add(smSvAssistantOfficer);
		}
	}
	//#28774 - End
	
	/**
	 * Gets the server url.
	 *
	 * @return the server url
	 */
	private String getServerUrl() {
		ExternalContext extcontext = FacesContext.getCurrentInstance().getExternalContext();
		PortletRequest portletRequest = (PortletRequest) extcontext.getRequest();
		HttpServletRequest httpServletRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(portletRequest));
		String requestURL = httpServletRequest.getRequestURL().toString();
		String url = requestURL.substring(0, requestURL.length()- httpServletRequest.getRequestURI().length())+ httpServletRequest.getContextPath() + "/";

		return url;
	}
	// #28175 start
	/**
	 * Retrieves the name of a Level 3 category based on the provided category ID and application ID.
	 * 
	 * This method constructs a supplier search request DTO with the given application ID and category ID,
	 * then uses the supplier search service to retrieve a list of supplier categories. It then iterates
	 * over the list to find the category with the matching ID and returns its name.
	 * 
	 * @param categoryL3Id the ID of the Level 3 category
	 * @param applId the ID of the application
	 * @return the name of the Level 3 category
	 */
	public String getSmCategoryL3(Long categoryL3Id, Long applId) {
		SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
		reqDTO.setApplId(applId);
		reqDTO.setCategoryId(categoryL3Id);
		SupplierSearchResDTO resDTO = SmServiceLookup.getSupplierSearchSO().retrieveCategoryByApplIdAndCategoryId(
				reqDTO);
		List<SmSupplierCategory> catLs = resDTO.getSmSupplierCategoryList();
		String categoryL3Name = "";
		// Max Loop 30 records
		for (SmSupplierCategory cat : catLs) {
			if (cat.getPmCategoryL3().getCategoryL3Id().equals(categoryL3Id)) {
				categoryL3Name = cat.getCategoryL3Name();
				break;
			}
		}
		return categoryL3Name;
	}
	// #28175 end	
	//28775 - Site Visit Grouping Enhancement - 28775 - start
	/**
	 * Set template list without duplicate of category codes
	 */
	private void setTemplateList(){
		if(getManagedBean().getSmSvTask().getPmCategoryL3() == null){
			return;
		}
		SmSvTask svTask = getManagedBean().getSmSvTask();
		getManagedBean().setSiteVisitNo(getSvTaskCountByApplIdAndCategoryId());
		List<SmSvTemplate> templates = new ArrayList<>();
		svTask.getSmSvTemplate().setSuppCategory(
				retrieveSupplierCategory(svTask.getPmCategoryL3().getCategoryL3Id()));
		
		templates.add(svTask.getSmSvTemplate());
		String catId = svTask.getPmCategoryL3().getCategoryL3Id() + " ";
		setMachineList();
		if(svTask.getSmAppl().isSiteVisitGroup() && svTask.getSmSvTaskGroups() != null){
			for(SmSvTaskGroup svTGroup : svTask.getSmSvTaskGroups()){
				if(svTGroup.isRedundant()){
					continue;
				}
				Long catL3Id = svTGroup.getPmCategoryL3().getCategoryL3Id();
				if(!catId.contains(catL3Id.toString())){
					setMachineList(svTGroup.getSmSvTemplate(), catL3Id);
					svTGroup.getSmSvTemplate().setSuppCategory(retrieveSupplierCategory(catL3Id));
					templates.add(svTGroup.getSmSvTemplate());
					catId = catId.concat(catL3Id+" ");
				} else {
					svTGroup.setRedundant(true);
				}
			}
		}
		getManagedBean().setTemplates(templates);
	}
	
	/**
	 * Initialize templates for dockyard/workshop/printing/manufacturing/travel category codes
	 */
	private void prepareSvTaskGroup(){
		SmSvTask svTask = getManagedBean().getSmSvTask();
		setTemplateList();
		if(getManagedBean().getSmSvTask().getPmCategoryL3() != null){
			getManagedBean().setParameterDesc(getParameterDesc(
					svTask.getPmCategoryL3().getCategoryTypeCode()));
		//#35426
		} else {
			getManagedBean().getSmSvTask().getSmSvTemplate().setSuppCategory(null);
			getManagedBean().setTemplates(null);
		}
		getManagedBean().setLicensed(false);
		
		if(svTask.getSmAppl().isSiteVisitGroup() && svTask.getPmCategoryL3() != null){
			prepareSpecialParam(svTask);
		}
		if (getManagedBean().getIdentificationTypes() == null) {
			getManagedBean().setIdentificationTypes(SmCommonUtil.getInstance().getPmIdTypeDropdownValues());
		}	
	}
	
	private void prepareSpecialParam(SmSvTask svTask){
		String paramCode = svTask.getPmCategoryL3().getCategoryTypeCode().getParameterCode();
		if(paramCode.equals(SupplierManagementConst.CAT_TYPE_DOCKYARD) || 
				paramCode.equals(SupplierManagementConst.CAT_TYPE_WORKSHOP) ||
				paramCode.equals(SupplierManagementConst.CAT_TYPE_PRINTING)){
			prepareEquipmentLicenseInsurance(svTask.getSmSvTemplate());
			for(SmSvTaskGroup svTGroup : svTask.getSmSvTaskGroups()){
				prepareEquipmentLicenseInsurance(svTGroup.getSmSvTemplate());
			}
			if(paramCode.equals(SupplierManagementConst.CAT_TYPE_WORKSHOP)){
				prepareWorkshopWorkers();
			}
			getManagedBean().setLicensed(true);
			
		} else if(paramCode.equals(SupplierManagementConst.CAT_TYPE_MANUFACTURING)){
			prepareFinancialDoc(svTask.getSmSvTemplate());
			getManagedBean().setLicensed(true);
			
		} else if(paramCode.equals(SupplierManagementConst.CAT_TYPE_TRAVEL)){				
			prepareTravelTemplate();
			getManagedBean().setLicensed(true);
		}
	}
	
	private void prepareTravelTemplate(){
		List<SmSupplierSystem> systems = getManagedBean().getSmSvTask().getSmAppl().getSmSupplierSystems();
		if(getManagedBean().getTravellingSoftwareList() == null){
			getManagedBean().setTravellingSoftwareList(SmCommonUtil.getInstance()
				.getPmMachineByCategoryTypeCode(SupplierManagementConst.CAT_TYPE_TRAVEL));
			boolean otherSys = false;
			for(SmSupplierSystem sys : systems) {	//add other travelling software
				if(sys.getSystem() == null) {
					PmMachine machine = new PmMachine();
					machine.setMachineDesc(sys.getSystemName());
					getManagedBean().getTravellingSoftwareList().add(machine);
					otherSys = true;
					break;
				}
			}
			if(!otherSys) {
				getManagedBean().getTravellingSoftwareList().add(new PmMachine());
			}
		}
		for (PmMachine machine : getManagedBean().getTravellingSoftwareList()) {
			machine.setSelected(false);
			if ((SmSupplierSystem) EntityHelper.getEntityBySinglePropertyFromList(
					machine.getMachineId(), systems, "system") != null) {
				machine.setSelected(true);
			}
		}
	}
	
	/**
	 * @param categoryL3Id
	 * @return SmSupplierCategory based on L3 id
	 */
	private SmSupplierCategory retrieveSupplierCategory(Long categoryL3Id){
		if(categoryL3Id == null){
			return null;
		}
		for(SmSupplierCategory sCat : getManagedBean().getCategoryApplicable()){
			if(sCat.getPmCategoryL3().getCategoryL3Id().equals(categoryL3Id)){
				return sCat;
			}
		}
		List<SmSupplierCategory> catL = retrieveSupplierCategory(
				getManagedBean().getSmSvTask().getSmAppl().getApplId(), categoryL3Id);
		if(catL != null && !catL.isEmpty()){
			return catL.get(0);
		}
		return null;
	}
	
	/**
	 * Retrieve supplier categories by appl
	 * @param applId
	 * @param categoryL3Id
	 * @return
	 */
	public List<SmSupplierCategory> retrieveSupplierCategory(Long applId, Long categoryL3Id){
		SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
		reqDTO.setApplId(applId);
		reqDTO.setCategoryId(categoryL3Id);
		//#35426
		SupplierSearchResDTO resDTO = SmServiceLookup.getSupplierSearchSO().retrieveCategoryByApplIdAndCategoryId(reqDTO);
		
		return resDTO.getSmSupplierCategoryList();
	}
	
	/**
	 * Set machine list
	 * @param template
	 * @param categoryL3Id
	 */
	public void setMachineList(SmSvTemplate template, long categoryL3Id){
		if(EntityHelper.isCollectionEmpty(template.getSmSvMachines())){
			List<SmMachineCategory> machineCategoryLs = retrieveMachineDtlByApplIdAndCategoryL3Id(
					getManagedBean().getSmSvTask().getSmAppl().getApplId(), categoryL3Id);
			template.setSmSvMachines(new ArrayList<SmSvMachine>());
			if(!machineCategoryLs.isEmpty()){
				for(SmMachineCategory machineCat : machineCategoryLs){
					SmSvMachine svMachine = new SmSvMachine();
					svMachine.setSmMachineDtl(machineCat.getSmMachineDtl());
					svMachine.setSmSvTemplate(template);
					template.getSmSvMachines().add(svMachine);
				}
			}
		}
	}
	
	/**
	 * Set template
	 * @param svTemplate
	 */
	public void setSelectedTemplate(SmSvTemplate svTemplate){
		getManagedBean().setSelectedSvTemplate(svTemplate);
	}

	
	/**
	 * Edit travelling software
	 * @param machine
	 */
	public void editTravellingSoftware(PmMachine machine){
		List<SmSupplierSystem> systems = getManagedBean().getSmSvTask().getSmAppl().getSmSupplierSystems();
		SmSupplierSystem travel = (SmSupplierSystem) EntityHelper.getEntityBySinglePropertyFromList(
				machine.getMachineId(), systems, "system");
		if(machine.getMachineId() == null){
			for (SmSupplierSystem system : systems){
				if (system.getSystem() == null){
					travel = system;
					break;
				}
			}
		}
		if(machine.isSelected() || machine.getMachineCode() == null){
			if (travel == null) {
				travel = new SmSupplierSystem();
				travel.setRevNo(1);
				travel.setSmAppl(getManagedBean().getSmSvTask().getSmAppl());
				if (machine.getMachineId() != null) {
					travel.setSystem(machine);
				} else {
					travel.setSystemName(machine.getMachineDesc());
				}
				systems.add(travel);
			} else {
				travel.setSystemName(machine.getMachineDesc());
			}
		} else { //remove unselected machine
			systems.remove(travel);
		}
	}
	
	/**
	 * @param catCode
	 * @return travelling instruction
	 */
	public String showTravellingInstruction(String catCode) {
		return SmCommonUtil.getMessage(
				"lbl.sm.travel.software.instruction", new String[] {catCode});
	}
	
	/**
	 * @param template
	 * @return full category code and descriptions
	 */
	public String getTemplateCategoryCode(SmSvTemplate template) {
		if(template == null || template.getSuppCategory() == null){
			return null;
		}
		SmSupplierCategory cat = template.getSuppCategory();
		String code = cat.getPmCategoryL1().getCategoryL1Code() + cat.getPmCategoryL2().getCategoryL2Code()
				+ cat.getPmCategoryL3().getCategoryL3Code();
		SmAppl appl = getManagedBean().getSmAppl() != null ? getManagedBean().getSmAppl()
				: getManagedBean().getSmSvTask().getSmAppl();
		return code + " - " + getSupplierCategoryName(cat, appl);
	}
	
	private void downloadSiteVisitTemplateGroup(SmSvTask svTask, boolean review, String fileName) throws Exception{
		File svCover = new File(commonSiteVisitCover(svTask));
		File svPreDetails = new File(commonPreparationDetails(svTask,review));
		File svCompInfo = new File(commonSiteVisitCompanyInfo(svTask, review));
		File svSectionA = new File(commonSiteVisitSectionA(svTask));
		File svSectionB = new File(commonSiteVisitSectionB(svTask, review));
		List<File> files = new ArrayList<>(Arrays.asList(svCover, svPreDetails, svCompInfo,
				svSectionA, svSectionB));
		List<InputStream> streams = new ArrayList<>();
		streams.addAll(Arrays.asList(new FileInputStream(svCover), 
				new FileInputStream(svPreDetails), new FileInputStream(svCompInfo),
				new FileInputStream(svSectionA), new FileInputStream(svSectionB)));

		//section C to section E
		char section = 'C';
		String type = getManagedBean().getSmSvTask().getSmSvTemplate().getPmParameter().getParameterCode();
		List<String> specialCatCodes = Arrays.asList(SupplierManagementConst.CAT_TYPE_DOCKYARD, 
				SupplierManagementConst.CAT_TYPE_WORKSHOP, SupplierManagementConst.CAT_TYPE_MANUFACTURING, 
				SupplierManagementConst.CAT_TYPE_PRINTING, SupplierManagementConst.CAT_TYPE_TRAVEL);
		if(specialCatCodes.contains(type)){
			//license
			VelocityContext contextC = new VelocityContext();
			commonHeaderTemplate(review, contextC, section++);
			commonLicenseTemplate(review, contextC);
			File f1 = new File(generateToPdf(VM_PATH+"license.vm", contextC, fileInitName+"C.pdf"));
			files.add(f1);
			streams.add(new FileInputStream(f1));
			
			//equipment
			int i=0;
			for(SmSvTemplate tmp : getManagedBean().getTemplates()){
				VelocityContext contextD = new VelocityContext();
				commonHeaderTemplate(review, contextD, section);
				commonEquipmentTemplate(tmp, review, contextD);
				File f2 = new File(generateToPdf(VM_PATH+"equipment.vm", contextD, fileInitName+i+"D.pdf"));
				files.add(f2);
				streams.add(new FileInputStream(f2));
				i++;
			}
			section++;
		} 
		//category applicable
		VelocityContext contextE = new VelocityContext();
		commonHeaderTemplate(review, contextE, section++);
		commonCategoryTemplate(contextE);
		File f3 = new File(generateToPdf(VM_PATH+"category.vm", contextE, fileInitName+"E.pdf"));
		files.add(f3);
		streams.add(new FileInputStream(f3));
		
		//location
		VelocityContext contextF = new VelocityContext();
		commonHeaderTemplate(review, contextF, section++);
		commonLocationTemplate(review, contextF);
		File f4 = new File(generateToPdf(VM_PATH+"location.vm", contextF, fileInitName+"F.pdf"));
		files.add(f4);
		streams.add(new FileInputStream(f4));

		OutputStream combined = new FileOutputStream(new File(tmpPath+fileName));
		doMerge(streams, combined);
		combined.flush();
		combined.close();
		boolean isDeleted = true;
		for(File f : files){
			isDeleted = f.delete();
		}
		try{
			file = commonDownload(tmpFolder+fileName, fileName);
		}catch (IOException e){
			log.error("SvoBB.downloadSiteVisitTemplateGroup()-"+isDeleted, e);
		}finally{
			for(InputStream in : streams){
				in.close();
			}
		}
	}
	
	private void commonHeaderTemplate(boolean review, VelocityContext context, char section){
		context.put(VM_UTIL, new VmUtil());
		context.put("hostUrl", getLogoHeaderPath());
		context.put("templateCode", getManagedBean().getSmSvTask().getSmSvTemplate().getPmParameter().getParameterCode());
		context.put("templateType",templateDesc);
		context.put("section", section);
		context.put("review", review);
	}
	
	private void commonLocationTemplate(boolean review, VelocityContext context){
		SmSvTask svTask = getManagedBean().getSmSvTask();
		SmSvTemplate svTemplate = svTask.getSmSvTemplate();
		String templateType = svTemplate.getPmParameter().getParameterCode();
		
		String officeOwnership = getManagedBean().getSmSvTask().getSmSvOffice().getSmFacility().getOwnershipType();
		BigDecimal officePrice =  getManagedBean().getSmSvTask().getSmSvOffice().getSmFacility().getPurchasePrice();
		BigDecimal officeRentalRate = getManagedBean().getSmSvTask().getSmSvOffice().getSmFacility().getRentalRate();

		String svOfficePurchasePrice = officePrice != null ? formatter.format(officePrice) : null;
		String svOfficeRentalRate = officeRentalRate != null ? formatter.format(officeRentalRate) : null;
		
		context.put("officeOwnership", officeOwnership);
		context.put("officePrice", svOfficePurchasePrice);
		context.put("officeRentalRate", svOfficeRentalRate);
	
		if(review){
			context.put("svTask", svTask);
			if(templateType.equals(SupplierManagementConst.CAT_TYPE_DOCKYARD) || 
				templateType.equals(SupplierManagementConst.CAT_TYPE_WORKSHOP)){
					context.put("facility", svTemplate.getSmSvFacility());
			}
		}
	}
	
	private void commonEquipmentTemplate(SmSvTemplate template, boolean review, VelocityContext context){
		String templateType = template.getPmParameter().getParameterCode();
		
		if(templateType.equals(SupplierManagementConst.CAT_TYPE_DOCKYARD) || templateType.equals(SupplierManagementConst.CAT_TYPE_WORKSHOP)){
			if(templateType.equals(SupplierManagementConst.CAT_TYPE_WORKSHOP) && review){
				context.put("workerLs", template.getSmSvWorkers());
				context.put("workerIdMap", getIdentificationTypes());
			}
			context.put("equipmentMaintenance", template.getEquipmentMain());
			context.put("equipmentBeating", template.getEquipmentBeating());
			
		}else if(templateType.equals(SupplierManagementConst.CAT_TYPE_MANUFACTURING)){
			context.put("manufacturingLs", getManagedBean().getSmSvTask().getSmAppl().getSmManufacturingDtls());
			
		}else if(templateType.equals(SupplierManagementConst.CAT_TYPE_PRINTING)){
			context.put("printingMachineLs", template.getSmSvPrintworks());	
		}
		
		if(templateType.equals(SupplierManagementConst.CAT_TYPE_TRAVEL)){
			context.put("travelSystems", getManagedBean().getSmSvTask().getSmAppl().getSmSupplierSystems());
		} else {
			context.put("purchaseMethodMap", getAllPurchaseMethod());
			context.put("machineLs", template.getSmSvMachines());
			context.put("addMachineLs", template.getSmSvAdtMachines());
		}
		context.put("tmp", template);
	}
	
	private void commonLicenseTemplate(boolean review, VelocityContext context){
		SmSvTask svTask = getManagedBean().getSmSvTask();
		SmSvTemplate svTemplate = svTask.getSmSvTemplate();
		String templateType = svTemplate.getPmParameter().getParameterCode();
		
		if(templateType.equals(SupplierManagementConst.CAT_TYPE_DOCKYARD) 
				|| templateType.equals(SupplierManagementConst.CAT_TYPE_WORKSHOP)){
			if(review){
				context.put("licenseInsurance", svTask.getSmSvTemplate().getSmSvLicenseInsurances());
			} else {
				List<PmParameterDesc> licenseInsuranceLs = SmCommonUtil.retrieveParameterDescList(
						ParameterTypeConst.DOCKYARD_WORKSHOP_LICENSE_INS, LANG_CODE);
				context.put("licenseInsurance", licenseInsuranceLs);
			}
			
		} else if(templateType.equals(SupplierManagementConst.CAT_TYPE_MANUFACTURING)){
			if(review){
				context.put("financialDocLs", svTask.getSmSvTemplate().getSmSvManufacturingDocs());
			}else{
				List<PmParameterDesc> financialDocLs = SmCommonUtil.retrieveParameterDescList(
						ParameterTypeConst.MANUFACTURING_FINANCIAL_DOC, LANG_CODE);
				context.put("financialDocLs", financialDocLs);
			}
			context.put("additionalDocLs", svTask.getSmSvTemplate().getAdditionalDocLs());
			
		} else if(templateType.equals(SupplierManagementConst.CAT_TYPE_PRINTING)){
			List<PmParameterDesc> licenseInsuranceLs = SmCommonUtil.retrieveParameterDescList(
					ParameterTypeConst.PRINTING_LICENSE_INS, LANG_CODE);
			context.put("licenseInsuranceLs", review ? svTask.getSmSvTemplate().getSmSvLicenseInsurances() : licenseInsuranceLs);
		}
		context.put("adtLicenseLs", svTask.getSmSvDocLicenses());
	}
	
	private void commonCategoryTemplate(VelocityContext context){
		context.put("categoryCodeLs", getManagedBean().getCategoryApplicable());
	}
	//28775 - Site Visit Grouping Enhancement - 28775 - end
	
	//29379 - Site Visit Grouping Enhancement - 28775 - start
	private void prepareWorkshopWorkers(){
		SmSvTask svTask = getManagedBean().getSmSvTask();
		if(svTask.getSmSvTemplate().getSmSvWorkers() != null 
			&& !svTask.getSmSvTemplate().getSmSvWorkers().isEmpty()) {
				return;
		}
		//max loop: 39
		for(SmSvTemplate template : getManagedBean().getTemplates()){
			if(template.getSuppCategory() == null){
				continue;
			}
			//max loop: 52
			for(SmFacility fac : svTask.getSmAppl().getSmFacilities()){
				setWorkshopTemplateWorkers(template, fac);
			}
			//max loop: 33
			for(SmSvWorker worker : template.getSmSvWorkers()){
				worker.setSmSvTemplate(template);
			}
		}
	}
	
	private void setWorkshopTemplateWorkers(SmSvTemplate svTemplate, SmFacility facility){
		if (!EntityHelper.isCollectionEmpty(svTemplate.getSmSvWorkers())){
			return;
		}
		svTemplate.setSmSvWorkers(new ArrayList<SmSvWorker>());
		Long templateL3 = svTemplate.getSuppCategory().getPmCategoryL3().getCategoryL3Id();
		for (SmSupplierCategoryFacility catFac : facility.getSmSupplierCategoryFacilities()) {
			Long facilityL3 = catFac.getPmCategoryL3().getCategoryL3Id();
			if (templateL3.equals(facilityL3)) {
				svTemplate.getSmSvWorkers().addAll(facility.getSmSvWorkers());
				if (getManagedBean().getWorkerFacility() == null) {
					getManagedBean().setWorkerFacility(facility);
				}
				break;
			}
		}
	}
	
	private Map<String, String> getIdentificationTypes(){
		Map<String, String> idTypeMap = new HashMap<>();
		List<PmParameterDesc> idTypeLs = ParameterHelper.getInstance()
				.getParameterDescList(ParameterTypeConst.IDENTIFICATION_TYPES);
		if(idTypeLs.isEmpty()){
			return idTypeMap;
		}
		for(PmParameterDesc pmDesc : idTypeLs){
			idTypeMap.put(pmDesc.getPmParameter().getParameterCode(), pmDesc.getCodeName());
		}
		return idTypeMap;
	}
	
	private void validateWorkshopWorkers(){
		if(!Character.toString(CAT_TYPE_PG).equals(getManagedBean().getActivePage())){
			return;
		}
		final long typeId = 115L;
		for(SmSvTemplate template : getManagedBean().getTemplates()){
			for(SmSvWorker worker : template.getSmSvWorkers()){
				String idNo = worker.getIdentificationNo();
				if(worker.getIdentificationTypeId() != null && worker.getIdentificationTypeId() == typeId 
						&& idNo.length() != SupplierManagementConst.IC_NO_LENGTH){
					throw new EpException(MessageConst.ERROR_722532, new String[] {idNo});
				}
			}
		}
	}
	//29379 - Site Visit Grouping Enhancement - 28775 - end
	
	//#32304 - start
	/**
	 * Retrieve login details of an active PO (site visit before approval)
	 * or active approver(s) (site visit after approval)
	 * @return string arr[2]: arr[0] = login id, arr[1] = username
	 */
	private String[] getPoApproverLogin(){
		String[] user = {"", ""};
		SmAppl appl = getManagedBean().getSmSvTask().getSmAppl();
		SmWorkFlowStatusReqDTO swDTO = new SmWorkFlowStatusReqDTO();
		swDTO.setApplId(appl.getApplId());
		SmWorkflowStatus wf = SmServiceLookup.getSmWorkFlowStatusSO()
				.retrieveSvcWorkflowStatusDuringSiteVisitByDocId(swDTO).getWorkflowStatus();
		boolean isAfterApproval = wf != null && wf.getPmStatus().getStatusId().equals(
					SupplierManagementConst.PM_STATUS_PENDING_SITE_VISIT_AFTER_APPROVED);
		
		user[0] = ((SMMOFRegDataType) getManagedBean().getBpmPojo()).getPoUsers();
		PmUser poApprover = PmCommonUtil.retrieveUserByLoginId(user[0]);
		if(isAfterApproval){
			// find workflow 20108, use changed by to find approver
			SmWorkFlowStatusReqDTO reqDTO1 = new SmWorkFlowStatusReqDTO();
			reqDTO1.setStatusIds(Arrays.asList(SupplierManagementConst.PM_STATUS_PO_ESCALTED_TO_APPROVER));
			reqDTO1.setApplId(getManagedBean().getSmSvTask().getSmAppl().getApplId());
			SmWorkflowStatus wfReview = SmServiceLookup.getSmWorkFlowStatusSO()
					.retrieveWorkflowByDocIdAndMultipleStatus(reqDTO1).getWorkflowStatus();
			if (wfReview != null){
				poApprover = PmCommonUtil.retrieveUserById(wfReview.getChangedBy(), true);
				if(poApprover != null){
					user[0] = poApprover.getLoginId();
				}
			}
		}
		if(poApprover != null){
			user[1] = poApprover.getUserName();
		}
		if(isPoApproverActive(poApprover, isAfterApproval)){
			return user;
		}
		//replace with other active PO/Approver
		ZoneReqDTO zoneReqDTO = new ZoneReqDTO();
		zoneReqDTO.setStateId(appl.getSmCompanyBasic().getSmCompanyAddresses().get(0)
				.getSmAddress().getPmState().getStateId());
		String zone = SmServiceLookup.getZoneSO().retrieveZoneByStateId(zoneReqDTO)
				.getPmZone().getZoneCode();
		if(isAfterApproval){
			user[0] = SmCommonUtil.retrieveLoginIdsByZoneAndRole(zone, SupplierManagementConst.APPROVER_HQ, 
					appl.getSupplierType(), appl.getApplType(), appl.getApplCategoryType());
			int i = 0;
			StringBuilder sb = new StringBuilder("<table>");
			for(String s : user[0].split(",")){
				poApprover = PmCommonUtil.retrieveUserByLoginId(s);
				if(poApprover != null){
					sb.append("<tr><td>" + ++i + ")</td><td>" + poApprover.getUserName() + "</td></tr>");
				}
			}
			sb.append("</table>");
			user[1] = sb.toString();
		
		//site visit before approval
		} else {
			poApprover = SmCommonUtil.getInstance().retrievePOWithLowestWeightage(
					appl.getSupplierType(), appl.getApplType(), appl.getApplCategoryType(), zone);
			if(poApprover != null) {
				user[0] = poApprover.getLoginId();
				user[1] = poApprover.getUserName();
			}
		}
		return user;
	}
	
	private boolean isPoApproverActive(PmUser user, boolean isAfterApproval){
		try{
			//#33955 - START
			for (PmUserRole pmUserRole : user.getPmUserOrgs().get(0).getPmUserRoles()) {
				String role = pmUserRole.getPmRole().getRoleCode();
				if ((role.equals(SupplierManagementConst.PO_HQ) && !isAfterApproval)
					|| (role.equals(SupplierManagementConst.APPROVER_HQ) && isAfterApproval)) {
					return pmUserRole.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE);
				}
			}
			//#33955 - END
		} catch (NullPointerException e){
			log.info("PO/Approver null", e);
		}
		return false;
	}
	
	/**
	 * Validate before showing submit dialog
	 */
	public void validateBeforeSubmitDialog(){
		if(SmCommonUtil.checkCancelBumiApplFromBumiStatus(getManagedBean().getSmSvTask().getSmAppl())){
			RequestContext.getCurrentInstance().execute("smBumiCancelAlert.show()");
			return;
		} 	
		RequestContext.getCurrentInstance().execute("submissionConfirmDlg.show()");
	}
	
	/**
	 * Redirect
	 */
	public void redirectToTaskList(){
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "taskListView");
	}
	//#32304 - end
	
	// #34166 - Start
	public void validateText(FacesContext f, UIComponent c, Object obj) {
		String remarks = (String) obj;
		log.info("This is the remark >>>>>>> " + remarks);
		String fieldsName = (String) c.getAttributes().get("param");
		log.info("This is the second parameter >>>>>>> " + fieldsName);
		if (remarks.contains("<") || remarks.contains(">")) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
					SmCommonUtil.getOutputMsgBundle(MessageConst.ERROR_775191,
							new String[] { fieldsName.toLowerCase() })));
		}
	}
	
	public void checkRepresentative() {
		Locale locale = new FacesScopes().getUserLocale();
		String Name = ResourceBundleUtils.getMessage("lbl.sm.person.composition.name",ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale);
		String Designation = ResourceBundleUtils.getMessage("lbl.sm.userprofile.designation",ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale);
		//28775 - Site Visit Grouping Enhancement - 28775 - start
		SmSvTemplate template = getManagedBean().getSmSvTask().getSmSvTemplate();
		if(template.getSmSvInterviewees() != null){
			
			if(template.getSmSvInterviewees().isEmpty()){
				String representatives = ResourceBundleUtils.getMessage("lbl.sm.companyinfo.intvw.represent",ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale);
				throw new EpException(MessageConst.ERROR_775192, new String[] { representatives });
			}
			
			for(SmSvInterviewee person : template.getSmSvInterviewees()){
				//28775 - Site Visit Grouping Enhancement - 28775 - end
				if(person.getName() == null || person.getName().isEmpty()){
					String capitalizedStr = Name.substring(0, 1).toUpperCase() + Name.substring(1);
					throw new EpException(MessageConst.ERROR_775192, new String[] { capitalizedStr });
				}
				if(person.getDesignation() == null || person.getDesignation().isEmpty()){
					String capitalizedStr = Designation.substring(0, 1).toUpperCase() + Designation.substring(1);
					throw new EpException(MessageConst.ERROR_775192, new String[] { capitalizedStr });
				}
			}
		}
	}
	// #34166 - End
	//#36721
	private String getPOUsername(){
		if(getManagedBean().getBpmPojo() != null){
			String poLoginId = ((SMMOFRegDataType)getManagedBean().getBpmPojo()).getPoUsers();

			if(StringUtils.isNotBlank(poLoginId)){
				PmUser poUser = PmCommonUtil.retrieveUserByLoginId(poLoginId);
				if(poUser != null){
					return poUser.getUserName();
				}
			}
		}
		SmWorkFlowStatusReqDTO reqDTO1 = new SmWorkFlowStatusReqDTO();
		reqDTO1.setStatusIds(Arrays.asList(SupplierManagementConst.PM_STATUS_PENDING_REVIEW_APPLICATION));
		reqDTO1.setApplId(getManagedBean().getSmSvTask().getSmAppl().getApplId());
		SmWorkflowStatus wf = SmServiceLookup.getSmWorkFlowStatusSO()
				.retrieveWorkflowByDocIdAndMultipleStatus(reqDTO1).getWorkflowStatus();
		if (wf != null){
			UserReqDTO reqDTO2 = new UserReqDTO();
			reqDTO2.setUserId(wf.getChangedBy());
			PmUser poUser = SmServiceLookup.getUserSO().retrieveUserByUserId(reqDTO2).getPmUser();
			if (poUser != null){
				return poUser.getUserName();
			}
		}
		return "";
	}
	
}