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
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
//#30504 start
import my.ep.biz.service.pm.common.zone.IZoneSO;
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
import my.ep.dto.pm.common.zone.ZoneReqDTO;
import my.ep.dto.sm.attachment.SmAttachmentReqDTO;
import my.ep.dto.sm.attachment.SmAttachmentResDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.revision.SmApplRevisionReqDTO;
import my.ep.dto.sm.revision.SmApplRevisionResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.dto.sm.siteVisit.SiteVisitReqDTO;
import my.ep.dto.sm.siteVisit.SiteVisitResDTO;
import my.ep.dto.sm.smRemarks.SmRemaksResDTO;
import my.ep.dto.sm.smRemarks.SmRemarksReqDTO;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.jaxb.sm.mofregdata.SMMOFRegDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmDocument;
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
import my.ep.persistence.entity.sm.SmSvTemplate;
import my.ep.persistence.entity.sm.SmSvWorker;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.sitevisit.SvoSiteVisitManagedBean;
import my.ep.web.util.AddressUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.util.sm.helper.VmUtil;
import my.ep.web.util.template.DocumentGenerator;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;

import com.liferay.portal.util.PortalUtil;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;

@ManagedBean(name = "svoSiteVisitBackingBean")
@RequestScoped
public class SvoSiteVisitBackingBean extends BaseBackingBean<SvoSiteVisitManagedBean> {
	private static final long serialVersionUID = 1L;

	public static Logger log = Logger.getLogger(SvoSiteVisitManagedBean.class);

	private String tmpPath = null;
	private String tmpFolder = "SiteVistTemplate" + "/";
	private String fileInitName;
	private String templateDesc;
	private Locale locale = new Locale("en");
	private final String templateLangCode = "en";
	private final String attachmentType = "SVD";
	
	private StreamedContent file;
	
	public SvoSiteVisitBackingBean() {
		SmCommonUtil.setMDCUserId();
	}
	
	/**
	 * set the selectedSmSvFacility by template type
	 * @param templateType
	 */
	private void setSelectedSmSvFacility(SmSvTemplate svTemplate){
		//getManagedBean().getSelectedSvTemplate().setSmSvFacility(getManagedBean().getSmSvTask().getSmSvAddress().getSmAddress().getSmFacilities().get(0));
		log.info("*******setSelectedSmSvFacility*********" + getManagedBean().getSelectedSvTemplate().getSvTemplateId());
		if(getManagedBean().getSelectedSvTemplate().getSmSvFacility() == null){
			//log.info("smFacility 0 - " +getManagedBean().getSmSvTask().getSmSvAddress().getSmAddress().getAddressId());
			SmFacility smFacility = retrieveFacilityByAddress(getManagedBean().getSmSvTask().getSmSvAddress().getSiteRefId());
			SmSvFacility smSvFacility = getManagedBean().getSelectedSvTemplate().getSmSvFacility();

			if(smFacility != null){
				if(smSvFacility == null){
					smSvFacility = new SmSvFacility();
				}

				smSvFacility.setOwnershipType(smFacility.getOwnershipType());
				//				smSvFacility.setOverallSqft(formatter.format(BigDecimal.ZERO));
				//				smSvFacility.setEnclosedSqft(formatter.format(BigDecimal.ZERO));
				if(smFacility.getAreaSquareFeet()!=null){
					//smSvFacility.setOverallSqft(formatter.format(smFacility.getAreaSquareFeet()));
					//smSvFacility.setEnclosedSqft(formatter.format(smFacility.getAreaSquareFeet()));

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
				//NumberFormat formatter = new DecimalFormat("#0.00");
				smSvFacility = new SmSvFacility();
				//smSvFacility.setOverallSqft(formatter.format(BigDecimal.ZERO));
				//smSvFacility.setEnclosedSqft(formatter.format(BigDecimal.ZERO));

				smSvFacility.setSmSvTemplate(svTemplate);
				getManagedBean().getSelectedSvTemplate().setSmSvFacility(smSvFacility);
			}
		}
	}

//	}

	
	/**
	 * retrieve Site Visit Task info
	 * @param viewMode
	 */
	public void retrieveSiteVisitInfo(String viewMode){
		//29430
		if(!getManagedBean().isFromReport())
			this.getManagedBean().setActiveIndex(0);
		
		SiteVisitReqDTO reqDTO = new SiteVisitReqDTO();
		reqDTO.setSvTaskId(getManagedBean().getSvTaskId());
		ISiteVisitSO reqSO = (ISiteVisitSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISiteVisitSO.class);
		SmSvTask svoTask = reqSO.retrieveSiteVisitInfoBySvTaskId(reqDTO).getSmSvTask();
		log.info("*********SV Task***********" + svoTask.getSvTaskId());
		
		if(StringUtils.isNotBlank(viewMode)){
			getManagedBean().setViewMode(true);
		}
		
		if(svoTask != null){
			this.getManagedBean().setSmSvTask(svoTask);
			SmSvTask smSvTask = this.getManagedBean().getSmSvTask();
			//		this.getManagedBean().getSmSvTask().getsm
			log.info("*****this.getManagedBean().getSmSvTask().getSmSvFinChecklists().size()" + smSvTask.getSmSvFinChecklists().size());
			if(smSvTask.getSmSvFinChecklists().size() < 1){
				log.info("******getSmSvFinChecklists() == null******");
				List<PmParameterDesc> pmParamDesc = new ArrayList<PmParameterDesc>();
				pmParamDesc = SmCommonUtil.retrieveParameterDescList(ParameterTypeConst.SVO_FIN_CHECKLIST, new FacesScopes().getUserLocale().getLanguage());
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

			if(smSvTask.getSmSvBankInfos().size() < 1){
				log.info("######### SmSvBankInfo #####");
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
			//24964 - start
			if(smSvTask.getSmSvTemplate().getSmSvComment() == null){
				smSvTask.getSmSvTemplate().setSmSvComment(new SmSvComment());
				smSvTask.getSmSvTemplate().getSmSvComment().setSmSvTemplate(smSvTask.getSmSvTemplate());
			}
			//24964 - end
			
			//#28774 - Start
			if(smSvTask.getSmSvAssistantOfficers() == null){
				smSvTask.setSmSvAssistantOfficers(new ArrayList<SmSvAssistantOfficer>());
				smSvTask.getSmSvAssistantOfficers();
				for(SmSvAssistantOfficer smSvAssistantOfficer : smSvTask.getSmSvAssistantOfficers()){
					smSvAssistantOfficer.setSmSvTask(this.getManagedBean().getSmSvTask());
				}
			}
			//#28774 - End
			
			for(SmPersonnel contactPerson : smSvTask.getSmAppl().getSmPersonnels()){
				if(contactPerson.isContactPerson()){
					this.getManagedBean().setContactPerson(contactPerson);
					break;
				}
			}
			
			//Set Template Map for index of tabMenu
/*			this.getManagedBean().setIndexPageMap(new HashMap<Integer,SmSvTemplate>());
			int i = viewMode.equalsIgnoreCase("V") ? 1 : 3;
		
			log.info("iCount before------" + i);
			getManagedBean().getIndexPageMap().put(i, smSvTask.getSmSvTemplate());
			
			if(viewMode.equalsIgnoreCase("V")){
				this.getManagedBean().setViewMode(true);
			}*/
	
			this.getManagedBean().setLastIndex(5);
			
			getTotalBODMgmtWorkerEquityOwner();
			chequeSignatoryInfo();
			//getAllApprovedCategoryCode();
			List<SmSupplierCategory> suppCatLs = retrieveActiveCategoryCode(smSvTask.getSmAppl().getSmSupplier().getSmAppl().getApplId());
			this.getManagedBean().setCategoryApplicable(suppCatLs);
			
			//List<SmSupplierCategory> catLs = retrieveAllApprovedCategoryCode(smSvTask.getSmAppl().getApplId());
		
			//retrieve Supplier GST Info
			SmGstSupplier gstSupp = SmCommonUtil.getSmGstSupplierBySupplierIdAndBranchCode(getManagedBean().
					getSmSvTask().getSmAppl().getSmSupplier().getSupplierId(), null, null,true);
			getManagedBean().setSmGstSupplier(gstSupp);
			
			//populateSmSvAddresses();
			//groupCategoryCodeByType(svoTask.getSmSvTemplate());
			
			getManagedBean().setSelectedSvTemplate(smSvTask.getSmSvTemplate());
			getManagedBean().setBusinessTypeDesc(getBusinessTypeDesc());
			getManagedBean().setCompanyAddress(getCompanyAddressInLine());
			if(getManagedBean().getSmSvTask().getPmCategoryL3() != null){
				getManagedBean().setSiteVisitNo(getSvTaskCountByApplIdAndCategoryId());
				List<SmSupplierCategory> catLs = retrieveSupplierCategoryByApplIdAndCategoryL3Id();
				if(catLs.size() > 0){
					getManagedBean().setSupplierCategory(catLs.get(0));
				}
				else{
					if(suppCatLs.size() > 0){
						for(SmSupplierCategory sCat : suppCatLs){
							if(sCat.getPmCategoryL3().getCategoryL3Id() == getManagedBean().getSmSvTask().getPmCategoryL3().getCategoryL3Id()){
								getManagedBean().setSupplierCategory(sCat);
								break;
							}
						}
					}
				}
				
				setMachineList();
			}
			
			if(smSvTask.getSmSvDocLicenses() == null || smSvTask.getSmSvDocLicenses().size() < 1){
				if(getManagedBean().getSmSvTask().getPmCategoryL3() != null){
				retrieveAndSetSmSvDocLicense(); //retrieve and set SmSvDocLicense (Additional License)
				}
			}
			
			SmMofAccount mofAccount =  SmApplicationUtil.getActiveMofAccount(smSvTask.getSmAppl().getSmSupplier().getSmMofAccounts(),
					smSvTask.getSmAppl().getSupplierType());
			getManagedBean().setMofNo(StringUtils.isNotBlank(mofAccount.getMofNo())?mofAccount.getMofNo() : "");

			if (smSvTask.getSmSvTemplate().getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_MANUFACTURING)) {
				if (smSvTask.getSmAppl().getSmManufacturingDtls().size() > 0) {
					getManagedBean().setManufacturingDtlLs(smSvTask.getSmAppl().getSmManufacturingDtls());
				}
			}

			if(smSvTask.getSmSvTemplate().getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_DOCKYARD) || 
					smSvTask.getSmSvTemplate().getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_WORKSHOP) ||
					smSvTask.getSmSvTemplate().getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_PRINTING)){

				setSelectedSmSvFacility(smSvTask.getSmSvTemplate());
			}
			
			retrievUploadedSiteVisitReport(getManagedBean().getSmSvTask().getSmSvTemplate());
			
			log.info("ref Type>>>>>>>>." + smSvTask.getSmSvAddress().getSiteRefId());
			log.info("ref id>>>>>>>>." + smSvTask.getSmSvAddress().getSiteType());
			log.info("SV address id>>>>>>>>." + smSvTask.getSmSvAddress().getSvAddressId());
			
			SmCommonUtil.getInstance().setSmSvAddressInfoBySiteTypeAndRefId(smSvTask.getSmSvAddress().getSiteType(),
					smSvTask.getSmSvAddress().getSiteRefId(),smSvTask.getSmSvAddress(), smSvTask.getSmAppl().getApplId());
			//smSvTask.getSmSvAddress().setAddress(getSmAddressById(smSvTask.getSmSvAddress().getSiteRefId()));
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
		log.info("*******changeActiveIndex********" + index);
		this.getManagedBean().setActiveIndex(index);
		if(svTemplate != null){
//			this.getManagedBean().setSelectedSvTemplate(svTemplate);
			//SmSvTemplate svTemplate = this.getManagedBean().getSmSvTemplateMap().get(catType);
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
					log.info(">>>>>svTemplate.getSmSvWorkers() IS NULL");
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
				
				//preparePrintingAcc(svTemplate);
			}
			
			if(svTemplate.getPmParameter()!=null && svTemplate.getPmParameter().getParameterCode()!=null){
				setSelectedSmSvFacility(svTemplate);
			}
			
		}
/*		else{
			if(index == this.getManagedBean().getLastIndex()){
				retrievUploadedSiteVisitReport(getManagedBean().getSmSvTask().getSmSvTemplate());
			}
		}*/
	}
	
	/**
	 * add interviewee to the List
	 */
	public void addInterviewee(){
		log.info("*******addInterviewee**********");
		SmSvTemplate svTemplate = this.getManagedBean().getSelectedSvTemplate();
		

		SmSvInterviewee interviewee = new SmSvInterviewee();
		interviewee.setSmSvTemplate(svTemplate);
		this.getManagedBean().getSelectedSvTemplate().getSmSvInterviewees().add(interviewee);

	}
	
	/**
	 * remove selected Interviewee
	 */
	public void removeInterviewee(){
		//#24964 - start
		if(getManagedBean().getDuplicateInterviewees() == null)
			getManagedBean().setDuplicateInterviewees(new ArrayList<>(getManagedBean().getSelectedSvTemplate().getSmSvInterviewees()));
		//#24964 - end
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
		log.info("prepareEquipmentLicenseInsurance>>>>>" + svTemplate.getSvTemplateId());
		log.info("Equipment Ls>>>>>" + svTemplate.getSmSvEquipments().size());
		
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
				log.info("***Equipment Param Is NUll*****" );
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
						//svTemplate.getEquipmentMain().add(equipMainObj);
						svTemplate.getSmSvEquipments().add(equipMainObj);
					}
					
					for(PmMachineSection panelSpraySection : machineSectionPS){
						SmSvEquipment equipBindingObj = new SmSvEquipment();
						equipBindingObj.setPmMachineSection(panelSpraySection);
						equipBindingObj.setPmMachine(panelSpraySection.getPmMachine());
						equipBindingObj.setSmSvTemplate(svTemplate);
						//svTemplate.getEquipmentMain().add(equipMainObj);
						svTemplate.getSmSvEquipments().add(equipBindingObj);
					}
					
					log.info("after merge>>>>>" + svTemplate.getSmSvEquipments().size() );
					if(svTemplate.getSmSvEquipments().size() > 0){
						for(SmSvEquipment svEquipment : svTemplate.getSmSvEquipments()){
							log.info("section type= " + svEquipment.getPmMachineSection().getSection().getParameterCode());
							
							if(svEquipment.getPmMachineSection().getSection().getParameterCode().equals(ParameterConst.EQUIPMENT_MAINTENANCE_OVERHAUL)){
								svTemplate.getEquipmentMain().add(svEquipment);
							}else if(svEquipment.getPmMachineSection().getSection().getParameterCode().equals(ParameterConst.EQUIPMENT_PANEL_SPRAY)){
								svTemplate.getEquipmentBeating().add(svEquipment);
							}
						}
					}
					
					//log.info("getEquipmentMain()>>>>>>" + svTemplate.getEquipmentMain().size());
					//log.info("getEquipmentBeating()>>>>>>" + svTemplate.getEquipmentBeating().size());
					
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
		log.info("**********addWorkshopWorker*****");
		SmSvTemplate svTemplate = this.getManagedBean().getSelectedSvTemplate();
		List<SmSvWorker> workshopWorkerLs = svTemplate.getSmSvWorkers();
		SmSvWorker worker = new SmSvWorker();
		worker.setSmSvTemplate(svTemplate);
		
		workshopWorkerLs.add(worker);
	}
	
	/**
	 * remove selected worker from the list
	 */
	public void removeWorkshopWorker(){
		//this.getManagedBean().getSelectedInterviewees() != null && 
		if(getManagedBean().getSelectedWorkers() != null && this.getManagedBean().getSelectedWorkers().length > 0){
			for (SmSvWorker svWorker : this.getManagedBean().getSelectedWorkers()) {
				log.info("---removeSvWorker---" + svWorker);
				this.getManagedBean().getSelectedSvTemplate().getSmSvWorkers().remove(svWorker);
			}
		}
		this.getManagedBean().setSelectedWorkers(null);
	}
	
	/**
	 * add new financialDoc to the List
	 */
	public void addFinancialDoc(){
		log.info("**********addFinancialDoc*****");
		SmSvTemplate svTemplate = this.getManagedBean().getSelectedSvTemplate();
		List<SmSvManufacturingDoc> addFinancialDocLs = svTemplate.getAdditionalDocLs();
		SmSvManufacturingDoc addFinancialDoc = new SmSvManufacturingDoc();
		addFinancialDoc.setSmSvTemplate(svTemplate);
		
		addFinancialDocLs.add(addFinancialDoc);

	}
	
	/**
	 * remove financialDoc from the List
	 */
	public void removeFinancialDoc(){
		if(this.getManagedBean().getSelectedAddFinancialDoc() != null && this.getManagedBean().getSelectedAddFinancialDoc().length > 0){
			for (SmSvManufacturingDoc addFinDoc : this.getManagedBean().getSelectedAddFinancialDoc()) {
				log.info("---remove FinancialDoc---" + addFinDoc);
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
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	/**
	 * format equity Amount 
	 * @param equityAmount
	 * @return
	 */
	private String formatEquity(BigDecimal equityAmount) {
		DecimalFormat formatter = new DecimalFormat("###,##0.00");
		return formatter.format(equityAmount);
	}
	
	/**
	 * calculate Equity Percentage
	 * @param equityAmt
	 * @return String
	 */
	public String calculateEquityPercentage(String equityAmt){
		double eqAmt =  (equityAmt == null || equityAmt == "" ? new Double(0) : new Double(equityAmt));
		double paidUpCapital = this.getManagedBean().getSmSvTask().getSmAppl().getSmCompanyBasic().getPaidUpCapital().doubleValue();
		
		Double percentage = (eqAmt * 100) / paidUpCapital;
		
		DecimalFormat formatter = new DecimalFormat("###,##0.00");
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
		int ttlBOD = 0, ttlMgmt = 0, ttlWorker = 0, ttlEquityOwner = 0;
		int dirBumi = 0, dirNonBumi = 0, dirNonMalaysian = 0;
		int mgmtBumi = 0, mgmtNonBumi = 0, mgmtNonMalaysian = 0;
		int wrkBumi = 0, wrkNonBumi = 0, wrkNonMalaysian = 0;
		
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
				if(personnel.isBumi() != null && personnel.isBumi()){//24964
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
				if(personnel.isBumi() != null && personnel.isBumi()){//24964
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

				if(personnel.isBumi() != null &&  personnel.isBumi()){//24964
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
		BigDecimal convertedDirNo = new BigDecimal(dirNo);
		BigDecimal ttlDirector = new BigDecimal(this.getManagedBean().getTotalBOD());
		BigDecimal percentage = new BigDecimal(0.00);
		
		try{
			percentage = convertedDirNo.divide(ttlDirector, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
		}catch(ArithmeticException e) {
			percentage = new BigDecimal(0.00).setScale(2);
		}
		return percentage;
	}

	/**
	 * calculate percentage of Management
	 * @param mgmtNo
	 * @return BigDecimal
	 */
	public BigDecimal calculateMgmtPercentage(int mgmtNo){
		BigDecimal convertedMgmtNo = new BigDecimal(mgmtNo);
		BigDecimal ttlMgmt= new BigDecimal(this.getManagedBean().getTotalMgmt());
		BigDecimal percentage = new BigDecimal(0.00);

		try{
			percentage = convertedMgmtNo.divide(ttlMgmt, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
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
		BigDecimal convertedWrkNo = new BigDecimal(workerNo);
		BigDecimal ttlWorker= new BigDecimal(this.getManagedBean().getTotalWorker());
		BigDecimal percentage = new BigDecimal(0.00);

		try{
			percentage = convertedWrkNo.divide(ttlWorker, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
		}catch (ArithmeticException e) {
			percentage = new BigDecimal(0.00).setScale(2);
		}
		return percentage;
	}
	
	/**
	 * calculate  Percentage of ChequeSignatory
	 * @param chequeSignatory
	 * @return
	 */
	public BigDecimal calculatChequeSignatoryPercentage(int chequeSignatory){
		BigDecimal convertedCheqNo = new BigDecimal(chequeSignatory);
		BigDecimal ttlCheqSign= new BigDecimal(this.getManagedBean().getTotalCheqSign());
		BigDecimal percentage = new BigDecimal(0.00);

		try{
			percentage = convertedCheqNo.divide(ttlCheqSign, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
		}catch (ArithmeticException e) {
			percentage = new BigDecimal(0.00).setScale(2);
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
		LinkedHashSet<String> totalCheckingList = new LinkedHashSet<String>();
		LinkedHashSet<String> bumiCheckingList = new LinkedHashSet<String>();
		LinkedHashSet<String> nonBumiCheckingList = new LinkedHashSet<String>();
		LinkedHashSet<String> nonMyCheckingList = new LinkedHashSet<String>();
		
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
		if(getManagedBean().getSelectedPrintWorks() != null && this.getManagedBean().getSelectedPrintWorks().length > 0){
			for (SmSvPrintwork printWork : this.getManagedBean().getSelectedPrintWorks()) {
				this.getManagedBean().getSelectedSvTemplate().getSmSvPrintworks().remove(printWork);
			}
		}
	}
	
	/**
	 * View Site Visit report
	 */
	public void viewSiteVisitReport(){
		//this.getManagedBean().setIndexPageMap(new HashMap<Integer,String>());
		retrieveSiteVisitInfo("V");
		/*
		int i = 1;
		for (Map.Entry<String, SmSvTemplate> entry : this.getManagedBean().getSmSvTemplateMap().entrySet()) {
			this.getManagedBean().getIndexPageMap().put(i, entry.getKey());
			i++;
		}*/
	}
	
	/**
	 * control the menu in review site visit report
	 * @param index
	 * @param catType
	 */
	public void viewActiveIndex(int index,SmSvTemplate svTemplate){
		log.info("Index-" + index);
		getManagedBean().getLastIndex();
		
		this.getManagedBean().setActiveIndex(index);
		if(svTemplate != null){
			this.getManagedBean().setSelectedSvTemplate(svTemplate);
			//SmSvTemplate svTemplate = this.getManagedBean().getSelectedSvTemplate();
			
			//String catCode = svTemplate.getPmParameter().getParameterCode();
			
			if(svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_DOCKYARD) || 
					svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_WORKSHOP)){

				prepareEquipmentLicenseInsurance(svTemplate);
			}
			
			if(svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_MANUFACTURING)){
				this.prepareFinancialDoc(svTemplate);
			}
			
			if(svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_PRINTING)){
				//preparePrintingInfoView();
				//preparePrintingAcc(svTemplate);
				prepareEquipmentLicenseInsurance(svTemplate);
			}
			
			
			/*if(svTemplate.getSmSvMachines() != null){
				log.info("***svTemplate.getSmSvMachines()>>>" + svTemplate.getSmSvMachines().size());
				if(svTemplate.getSelectedSmMachine() == null){
//					svTemplate.setSelectedSmMachine(new SmMachineDtl[svTemplate.getSmSvTemplateAddresses().size()]);
					svTemplate.setSelectedSmMachine(new SmMachineDtl[svTemplate.getSmSvMachines().size()]);
				}
				SmMachineDtl[] selectedMachine = svTemplate.getSelectedSmMachine();
				for(int macCount = 0; macCount < svTemplate.getSmSvMachines().size(); macCount++){
					selectedMachine[macCount] = svTemplate.getSmSvMachines().get(macCount).getSmMachineDtl();
//					log.info("****selectedAdd*******" + svTemplate.getSmSvTemplateAddresses().get(macCount).getSmSvAddress());
//					macCount++;
				}
			}*/
			
			log.info("---------------viewActiveIndex End-----------");
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
		//Locale locale = new Locale("en");
		
		tmpPath = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);
		
		tmpPath = tmpPath + "/" + tmpFolder;
		
		File dir = new File(tmpPath);
		if(!dir.exists()) dir.mkdirs();
		
		//String lblApplType = SmCommonUtil.getApplTypeDesc(svTask.getSmAppl().getApplType());
		//String applType = ResourceBundleUtils.getMessage(lblApplType,ResourceBundleUtils.BUNDLE_COMMON_LABELS,locale);
		
		String lblTemplate = "lbl.sm.sitevisit.category.categorycode_"+templateType;
		templateDesc = ResourceBundleUtils.getMessage(lblTemplate,ResourceBundleUtils.BUNDLE_COMMON_LABELS,locale);
		
		fileInitName = svTask.getSvTaskId() + "_" + templateDesc + "_";
		
		// combine all pdf into 1
		String cover = commonSiteVisitCover(svTask);
		//String preDetails = commonPreparationDetails(svTask,templateType);
		String preDetails = commonPreparationDetails(svTask,review);
		String compInfo = commonSiteVisitCompanyInfo(svTask, review);
		String sectionA = commonSiteVisitSectionA(svTask);
		String sectionB = commonSiteVisitSectionB(svTask, review);
		//String sectionC = commonSiteVisitSectionC(svTask,svTemplate);
		String sectionC = commonSiteVisitSectionC(svTask,review);
		//String sectionD = commonSiteVisitSectionD(svTask,templateType);
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
        log.info("****tmpFolder*******" + tmpFolder);
        log.info("****tempPath******" + tmpPath);
        log.info("****File Name*******" + fileName);
        
        
        try {
        	file = commonDownload(tmpFolder+fileName, fileName);
        	
        } catch (IOException e) {
        	e.printStackTrace();
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
        
        log.info("**********END downloadSiteVisitTemplate***********");
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
		log.info("**********commonSiteVisitCover*************");
		VelocityContext context = new VelocityContext();
		
		log.info(">>>>>>" + svTask.getSvTaskId());
		String lblApplType = SmCommonUtil.getApplTypeDesc(svTask.getSmAppl().getApplType());
		//Locale locale = new Locale("en");
		String applType = ResourceBundleUtils.getMessage(lblApplType,ResourceBundleUtils.BUNDLE_COMMON_LABELS,locale);
		SmAddress compAddress = svTask.getSmSvAddress().getAddress();
		List<SmMofAccount> mofAccountLs = svTask.getSmAppl().getSmSupplier().getSmMofAccounts();
		
		if(getManagedBean().getBpmPojo() != null){
			String poLoginId = ((SMMOFRegDataType)getManagedBean().getBpmPojo()).getPoUsers();

			if(StringUtils.isNotBlank(poLoginId)){
				PmUser poUser = PmCommonUtil.retrieveUserByLoginId(poLoginId);

				if(poUser != null){
					context.put("poName", poUser.getUserName());
				}
			}
		}
		context.put("hostUrl", getLogoHeaderPath());
		context.put("companyName", svTask.getSmAppl().getSmSupplier().getCompanyName());
		context.put("applNo", svTask.getSmAppl().getApplNo());
		context.put("applType", applType);
		context.put("templateType",templateDesc);
		context.put("compAddress", compAddress);
		context.put("addressFormatter", new AddressUtils());
		context.put("mofRegistrationNo", mofAccountLs.size() > 0 ? mofAccountLs.get(0).getMofNo() : null);
		context.put("siteVisitNo", getManagedBean().getSiteVisitNo());
		
		//#29781 - Start
		//String accptDate = "";
		/*
		StringBuilder SvO = new StringBuilder();
		if(svTask.getSmSvOfficers() != null && !svTask.getSmSvOfficers().isEmpty()){
			for(SmSvOfficer smSvOfficer : svTask.getSmSvOfficers()){
				SvO.append(smSvOfficer.getPmUser().getUserName());
			}
		}else{
			SvO.append(" ");
		}

		StringBuilder SvOA = new StringBuilder();
		int SmSvAssistantOfficersCounter = 0;
		if(svTask.getSmSvAssistantOfficers() != null && !svTask.getSmSvAssistantOfficers().isEmpty()){
			for(SmSvAssistantOfficer smSvAssistantOfficer : svTask.getSmSvAssistantOfficers()){
				if(SmSvAssistantOfficersCounter < 1 ){
					SvOA.append(smSvAssistantOfficer.getName());
				}
				SmSvAssistantOfficersCounter++;
			}
		}else{
			SvO.append(" ");
		}
		context.put("svOfficers", SvO.toString());
		context.put("svAOfficer", SvOA.toString());
		*/
		List<String> svOfficers = new ArrayList<String>();
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
		log.info("**********commonPreparationDetails*************");
		String vmPreDetails = "/sm/sitevisit/sv-predetails.vm";
//		String strTemplate = "lbl.sm.sitevisit.category.categorycode_" + templateType;
//		Locale locale = new Locale("en");
//		String lblTemplate = ResourceBundleUtils.getMessage(strTemplate,ResourceBundleUtils.BUNDLE_COMMON_LABELS,locale);
		
		VelocityContext context = new VelocityContext();
		
		context.put("companyName", svTask.getSmAppl().getSmSupplier().getCompanyName());	
		context.put("applNo", svTask.getSmAppl().getApplNo());
		context.put("businessRegistrationNo",  svTask.getSmAppl().getSmSupplier().getRegNo());
		context.put("visitingPurpose", templateDesc.toUpperCase());
		context.put("catApplicable", getManagedBean().getSupplierCategory());
		context.put("telNo", svTask.getSmAppl().getSmCompanyBasic().getPhoneNo());
		context.put("latitude", svTask.getSmSvAddress().getAddress().getMapLatitude());
		context.put("longitute", svTask.getSmSvAddress().getAddress().getMapLongitude());
		
		if(this.getManagedBean().getContactPerson() != null){
			context.put("contactPersonName", this.getManagedBean().getContactPerson().getName());
			context.put("contactPersonDesignation", this.getManagedBean().getContactPerson().getDesignation());
		}
		
		SmAddress svAddress = svTask.getSmSvAddress().getAddress();
		
		context.put("smVmUtil", new VmUtil());
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
			//context.put("gstEndDate", getManagedBean().getSmGstSupplier().getGstEndDate());
		}
		
		

		String fileName = fileInitName + "predetails.pdf";
//		/CR 28774: Static Code Analysis Start
//		String file = generateToPdf(vmPreDetails, context, fileName);
		
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
		log.info("**********commonSiteVisitCompanyInfo*************");
		VelocityContext context = new VelocityContext();
		String vmCompInfo= "/sm/sitevisit/sv-companyinfo.vm";
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
		//#28774 - Start
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
		
		context.put("companyName", svTask.getSmAppl().getSmSupplier().getCompanyName());
		context.put("busRegNo", svTask.getSmAppl().getSmSupplier().getRegNo());
		context.put("mofNo",StringUtils.isNotBlank(mofAccount.getMofNo())?mofAccount.getMofNo() : "");
		context.put("applNo",svTask.getSmAppl().getApplNo());
		context.put("templateType",templateDesc);
		context.put("smVmUtil", VmUtil.getInstance());
		context.put("finalInfoDate", DateUtils.dateToString("dd/MM/yyyy",svTask.getSmAppl().getChangedDate()));
		
		if(review){
			//context.put("", value)
			log.info("Company Info - Interviewee - " + svTask.getSmSvTemplate().getSmSvInterviewees());
			context.put("intRepresentatives", svTask.getSmSvTemplate().getSmSvInterviewees());
		}
		
		
		String fileName = fileInitName + "compInfo.pdf";
		String file = generateToPdf(vmCompInfo, context, fileName);
		
		return file;
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
		context.put("smVmUtil", VmUtil.getInstance());
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
			log.info("EquityCompanies-------" +  svTask.getSmAppl().getSmEquityCompanies().size());
			context.put("equityCompanies", svTask.getSmAppl().getSmEquityCompanies());
			authorizedCapitalFlag = true;
		}
		context.put("authorizedCapitalFlag", authorizedCapitalFlag);
		
		String fileName = fileInitName + "sectionA.pdf";
		//CR 28774: Static Code Analysis 
		return generateToPdf(vmEquity, context, fileName);
	}
	
	/**
	 * Generate Site Visit Template - Section B
	 * @param svTask
	 * @return String
	 * @throws Exception
	 */
	private String commonSiteVisitSectionB(SmSvTask svTask, boolean review) throws Exception{
		VelocityContext context = new VelocityContext();
		String vmEquity= "/sm/sitevisit/sv-section-b.vm";

		List<String> finLsItem = new ArrayList<String>();
		List<SmSvFinChecklist> finCheckList = new ArrayList<SmSvFinChecklist>();
		List<PmParameterDesc> pmParamDesc = new ArrayList<PmParameterDesc>();
		pmParamDesc = SmCommonUtil.retrieveParameterDescList(ParameterTypeConst.SVO_FIN_CHECKLIST, templateLangCode);
		
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
		
/*		for(SmSvBankInfo svBankInfo : svTask.getSmSvBankInfos()){
			String e = StringEscapeUtils.escapeHtml(svBankInfo.getSmSupplierBank().getPmFinancialOrg().getFinOrgName());
			log.info("String e>>>>" + e);
			svBankInfo.getSmSupplierBank().getPmFinancialOrg().setFinOrgName(e);
		}
*/
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
		log.info("commonSiteVisitSectionC..");
		
		SmSvTemplate svTemplate = svTask.getSmSvTemplate();
		String templateType = svTemplate.getPmParameter().getParameterCode();
	
		VelocityContext context = new VelocityContext();
		String vmSectionC = "/sm/sitevisit/sv-section-c.vm";
		
		//context.put("escapeTool", new EscapeTool()); 
		
/*		if(templateType.equals(SupplierManagementConst.CAT_TYPE_INSURANCE) || templateType.equals(SupplierManagementConst.CAT_TYPE_BUMIPUTERA) || 
				templateType.equals(SupplierManagementConst.CAT_TYPE_READING_MATERIAL_FROM_OVERSEA) || templateType.equals(SupplierManagementConst.CAT_TYPE_TRANSPORTATION) || 
				templateType.equals(templateType.equals(SupplierManagementConst.CAT_TYPE_TRAVEL))){

			SmSupplierCategory suppCat = getManagedBean().getSupplierCategory();
			context.put("categoryCodeLs", getManagedBean().getSupplierCategory());
			//context.put("templateType",templateDesc);
			//context.put("templateCode", templateType);
			//context.put("templateDesc", value);
		}else */
		if(templateType.equals(SupplierManagementConst.CAT_TYPE_DOCKYARD) || templateType.equals(SupplierManagementConst.CAT_TYPE_WORKSHOP)){
			List<PmParameterDesc> licenseInsuranceLs = SmCommonUtil.retrieveParameterDescList(ParameterTypeConst.DOCKYARD_WORKSHOP_LICENSE_INS, templateLangCode);
			
			SmFacility smFacility = retrieveFacilityByAddress(svTask.getSmSvAddress().getSiteRefId());
			String lblOwnerShip = null;
			if(smFacility != null){
				if(smFacility.getOwnershipType().equalsIgnoreCase("O")){
					lblOwnerShip = "lbl.sm.facility.own";
				}else{
					lblOwnerShip ="lbl.sm.facility.rent";
				}
			}
			
			//String lblOwnerShip =  smFacility.getOwnershipType().equalsIgnoreCase("O") ? "lbl.sm.facility.own" : "lbl.sm.facility.rent";
			String preLocOwnerShip = lblOwnerShip != null ? ResourceBundleUtils.getMessage(lblOwnerShip,ResourceBundleUtils.BUNDLE_COMMON_LABELS,locale) : "";

			DecimalFormat formatter = new DecimalFormat("###,##0.00");
			String preLocPurchasePrice = smFacility != null && smFacility.getPurchasePrice() != null ? formatter.format(smFacility.getPurchasePrice()).toString() : null;
			String preLocRentalRate = smFacility != null &&  smFacility.getRentalRate() != null ? formatter.format(smFacility.getRentalRate()).toString() : null;
			
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
				log.info("licenseInsurance size-"+ dockyardLicense.size());
				
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
			//SmSvTemplate manufacturingTemplate = null;
			
			if(review){
				context.put("financialDocLs", svTask.getSmSvTemplate().getSmSvManufacturingDocs());
			}else{
				List<PmParameterDesc> financialDocLs = SmCommonUtil.retrieveParameterDescList(ParameterTypeConst.MANUFACTURING_FINANCIAL_DOC, templateLangCode);
				context.put("financialDocLs", financialDocLs);
				context.put("additionalDocLs", "");
			}
			context.put("machineLs", svTask.getSmAppl().getSmMachineDtls());
			context.put("manufacturingLs", svTask.getSmAppl().getSmManufacturingDtls());
			
		}else if(templateType.equals(SupplierManagementConst.CAT_TYPE_PRINTING)){
			log.info("Section C - Printing >>>>");
			
			List<PmParameterDesc> licenseInsuranceLs = SmCommonUtil.retrieveParameterDescList(ParameterTypeConst.PRINTING_LICENSE_INS, templateLangCode);
			SmFacility smFacility = retrieveFacilityByAddress(svTask.getSmSvAddress().getSiteRefId());
			
			String lblOwnerShip = null;
			if(smFacility != null){
				if(smFacility.getOwnershipType().equalsIgnoreCase("O")){
					lblOwnerShip = "lbl.sm.facility.own";
				}else{
					lblOwnerShip ="lbl.sm.facility.rent";
				}
			}
			//String lblOwnerShip =  smFacility.getOwnershipType().equalsIgnoreCase("O") ? "lbl.sm.facility.own" : "lbl.sm.facility.rent";
			String preLocOwnerShip = lblOwnerShip != null ? ResourceBundleUtils.getMessage(lblOwnerShip,ResourceBundleUtils.BUNDLE_COMMON_LABELS,locale) : "";

			DecimalFormat formatter = new DecimalFormat("###,##0.00");
			String preLocPurchasePrice = smFacility != null && smFacility.getPurchasePrice() != null ? formatter.format(smFacility.getPurchasePrice()).toString() : null;
			String preLocRentalRate = smFacility != null && smFacility.getRentalRate() != null ? formatter.format(smFacility.getRentalRate()).toString() : null;
			
			context.put("ownerShipType", preLocOwnerShip);
			context.put("preLocPurchasePrice", preLocPurchasePrice);
			context.put("preLocRentalRate", preLocRentalRate);
			
			context.put("licenseInsuranceLs", review ? svTask.getSmSvTemplate().getSmSvLicenseInsurances() : licenseInsuranceLs);
			
			if(getManagedBean().getSupplierCategory() != null){
				//Map<String, List<PmMachineCategory>> printingCatMap = new HashMap<String,List<PmMachineCategory>>();
				//Map<String, List<SmMachineDtl>> printMachineMap = new HashMap<String,List<SmMachineDtl>>();

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
					log.info("-----Section C - Contain printing cat------");

					String categoryCodeDesc = catCode + " " + catL3.getCategoryName();
					log.info("purchaseMethodMap>>>>>" + getAllPurchaseMethod());
					
					context.put("purchaseMethodMap", getAllPurchaseMethod());
					if(review){
						context.put("pCat", categoryCodeDesc);
						context.put("printingMachineLs", svTask.getSmSvTemplate().getSmSvMachines());
					}else{
						List<PmMachineCategory> pmMachineCat = retrievePrintingAcc(lv1Id,lv2Id,lv3Id); //temp disable
						context.put("pCat", categoryCodeDesc);
						context.put("printingMachineLs", pmMachineCat);
						
						log.info("SmSvMachines>>>>" + svTask.getSmSvTemplate().getSmSvMachines().size());
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
		
		log.info("****Template Type****" + templateType);
		context.put("smVmUtil", new VmUtil());
		context.put("hostUrl", getLogoHeaderPath());
		context.put("templateType",templateDesc);
		context.put("templateCode", templateType);
		context.put("review", review);
		context.put("svTask", svTask);
		//#28774 - START 
		context.put("categoryCodeLs", this.getManagedBean().getCategoryApplicable());
		context.put("smVmUtil", new VmUtil());
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
		
		log.info("templateType>>>>" + templateType);
		//context.put("escapeTool", new EscapeTool()); 
		
		if(templateType.equals(SupplierManagementConst.CAT_TYPE_INSURANCE) || templateType.equals(SupplierManagementConst.CAT_TYPE_BUMIPUTERA) || 
				templateType.equals(SupplierManagementConst.CAT_TYPE_READING_MATERIAL_FROM_OVERSEA) || templateType.equals(SupplierManagementConst.CAT_TYPE_TRANSPORTATION) || 
				templateType.equals(SupplierManagementConst.CAT_TYPE_TRAVEL)){

			//context.put("escapeTool", new EscapeTool()); 
			String officeOwnership = this.getManagedBean().getSmSvTask().getSmSvOffice().getSmFacility().getOwnershipType();
			BigDecimal officePrice =  this.getManagedBean().getSmSvTask().getSmSvOffice().getSmFacility().getPurchasePrice();
			BigDecimal officeRentalRate = this.getManagedBean().getSmSvTask().getSmSvOffice().getSmFacility().getRentalRate();

			log.info("officePrice------->" + officePrice);
			log.info("officeRentalRate------->" + officeRentalRate);
			log.info("OfficeOwnership----->" + officeOwnership);
			
			DecimalFormat formatter = new DecimalFormat("###,##0.00");
			String svOfficePurchasePrice = officePrice != null ? formatter.format(officePrice).toString() : null;
			String svOfficeRentalRate = officeRentalRate != null ? formatter.format(officeRentalRate).toString() : null;
			
//			context.put("templateType",templateDesc);
//			context.put("templateCode", templateType);
			context.put("officeOwnership", officeOwnership);
			context.put("officePrice", svOfficePurchasePrice);
			context.put("officeRentalRate", svOfficeRentalRate);
		
			if(review){
				context.put("svTask", svTask);
				log.info("sv officer count-" + svTask.getSmSvOfficers().size());
			}
			
		}
		//#28774 - START
		context.put("categoryCodeLs", this.getManagedBean().getCategoryApplicable());
		context.put("smVmUtil", new VmUtil());
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
		
		//context.put("escapeTool", new EscapeTool()); 
		String officeOwnership = this.getManagedBean().getSmSvTask().getSmSvOffice().getSmFacility().getOwnershipType();
		BigDecimal officePrice =  this.getManagedBean().getSmSvTask().getSmSvOffice().getSmFacility().getPurchasePrice();
		BigDecimal officeRentalRate = this.getManagedBean().getSmSvTask().getSmSvOffice().getSmFacility().getRentalRate();

		log.info("officePrice------->" + officePrice);
		log.info("officeRentalRate------->" + officeRentalRate);
		
		DecimalFormat formatter = new DecimalFormat("###,##0.00");
		String svOfficePurchasePrice = officePrice != null ? formatter.format(officePrice).toString() : null;
		String svOfficeRentalRate = officeRentalRate != null ? formatter.format(officeRentalRate).toString() : null;
		
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
		log.info("-------addAdditionalMachine---------");
		
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
		//24964
		getManagedBean().setLatestAdtMachine(null);
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
		getManagedBean().getSelectedSvTemplate().setSelectedAdtMachines(null);
	}
	
	/**
	 * add Additional Machine To List
	 */
	public void addAdditionalMachineToList(){
		SmSvAdtMachine adtMachine = this.getManagedBean().getSelectedAdtMachine();
		this.getManagedBean().getSelectedSvTemplate().getSmSvAdtMachines().add(adtMachine);
		RequestContext.getCurrentInstance().execute("adtMacDlg.hide()");
		log.info("ADT MACHINE SIZE......." + this.getManagedBean().getSelectedSvTemplate().getSmSvAdtMachines().size());
		
		this.getManagedBean().setSelectedAdtMachine(new SmSvAdtMachine());
	}
	
	/**
	 * save Edited AdditionalMachine
	 */
	public void saveAdditionalMachine(){
		log.info("-------saveAdditionalMachine--------");
		
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
		//this.getManagedBean().setSelectedAdtMachine(new SmSvAdtMachine());
	}
	
	/**
	 * edit Additional Machine
	 * @param SmSvAdtMachine adtMachine
	 */
	public void editMachine(Object obj){
		/**
		getManagedBean().setOriAdtMachine(adtMachine);
		
		SmSvAdtMachine svAdtMachine = (SmSvAdtMachine) adtMachine.cloneEntity();
		this.getManagedBean().setSelectedAdtMachine(svAdtMachine);
		this.getManagedBean().getSelectedAdtMachine().setNewMachine(false);**/
		
		if(obj instanceof SmSvAdtMachine){
			getManagedBean().setOriSelectedMachine((SmSvAdtMachine) obj);
			SmSvAdtMachine svAdtMachine = (SmSvAdtMachine) ((SmSvAdtMachine) obj).cloneEntity();
			this.getManagedBean().setSelectedAdtMachine(svAdtMachine);
			this.getManagedBean().getSelectedAdtMachine().setNewMachine(false);
			//24964
			getManagedBean().setLatestAdtMachine(getSVOSmSvAdtMachine(svAdtMachine));
		}else if (obj instanceof SmSvMachine) {
			getManagedBean().setOriSelectedMachine((SmSvMachine) obj);
			//24964
			SmSvMachine svMachine = (SmSvMachine) ((SmSvMachine) obj).cloneEntity();
			this.getManagedBean().setSelectedSvMachine(svMachine);
			//24964
			getManagedBean().setLatestSvMachine(getSVOSmSvMachine((SmSvMachine) obj));
		} else {
			getManagedBean().setOriSelectedManufacturingDtl((SmManufacturingDtl) obj);
			SmManufacturingDtl manufacDtl = (SmManufacturingDtl) ((SmManufacturingDtl) obj).cloneEntity();
			this.getManagedBean().setSelectedManufacturer(manufacDtl);
			this.getManagedBean().getSelectedManufacturer().setBrandNew(false);
			//24964
			getManagedBean().setLatestManufacturer(getSVOSmManufacturingDtl(manufacDtl));
		}
	}
	
	/**
	 * save Site Visit Report
	 * @param action
	 * @return String - xhtml
	 */
	public String saveSiteVisitReport(String action){
		log.info("***********saveSiteVisitReport***********");
		SiteVisitReqDTO reqDTO = new SiteVisitReqDTO();
		SmSvTask svTask = this.getManagedBean().getSmSvTask();
		
		//#30550 - start
		if(getManagedBean().getSelectedSvTemplate().getSmSvInterviewees() == null ||
				getManagedBean().getSelectedSvTemplate().getSmSvInterviewees().isEmpty()){
			throw new EpException(SmCommonUtil.getMessage("lbl.sm.required.msg",
					new String[] { SmCommonUtil.getMessage("lbl.sm.sv.templ.interview.representative", null) }));
		}
		//#30550 - end
		
		//#24964 - start
		if(getManagedBean().getDuplicateInterviewees() != null){
			for(SmSvInterviewee dupe : getManagedBean().getDuplicateInterviewees()){
				for(SmSvInterviewee intvw : getManagedBean().getSelectedSvTemplate().getSmSvInterviewees()){
					if(intvw.getName().equals(dupe.getName())){
						dupe.setDesignation(intvw.getDesignation());
						getManagedBean().getSelectedSvTemplate().getSmSvInterviewees().remove(intvw);
						getManagedBean().getSelectedSvTemplate().getSmSvInterviewees().add(0, dupe);
						break;
					}
				}
			}
		}
		//#24964 - end

		SmSvTemplate svTemplate = svTask.getSmSvTemplate();
		//for(SmSvTemplate svTemplate : svTask.getSmSvTemplates()){
		//for Manufacturing Template. Add additional doc to SmSvManufacturingDOC
		if(svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_MANUFACTURING)){
			if(svTemplate.getAdditionalDocLs() != null && svTemplate.getAdditionalDocLs().size() > 0){
				for(SmSvManufacturingDoc mfDoc : svTemplate.getAdditionalDocLs()){
					if(mfDoc.isBrandNew()){
						log.info("*********is Brandnew*******");
						svTemplate.getSmSvManufacturingDocs().add(mfDoc);
					}
				}

			}
			if (svTemplate.getSmSvTask().getSmAppl().getSmManufacturingDtls().size() > 0) {
				MofRegistrationReqDTO mofReqDTO = new MofRegistrationReqDTO();
				mofReqDTO.setSmAppl(getManagedBean().getSmSvTask().getSmAppl());
				SmServiceLookup.getMofRegistrationSO().updateSmAppl(mofReqDTO);
			}
		}

		//Add/ Remove SmSvMachine
		if(svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_DOCKYARD) || 
				svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_WORKSHOP) ||
				svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_PRINTING) || 
				svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_MANUFACTURING)){
			//setSelectedMachineToSmSvMachineLs(svTemplate);
			//setMachineList(svTemplate);
		}

		//validate duplicate Workers
		if(svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_WORKSHOP)){
			validateDuplicateWorker(svTemplate.getSmSvWorkers());
		}
		
		//validate assistant officer
		//#28774 - Start
		if(svTask.getSmSvAssistantOfficers()==null){
			svTask.setSmSvAssistantOfficers(null);
		}//START 24964
		else{
			for(SmSvAssistantOfficer smSvAssistantOfficer : svTask.getSmSvAssistantOfficers())
			{
				log.info("**************SV Ass. Officer*************");
				log.info("name -> " + smSvAssistantOfficer.getName());
				smSvAssistantOfficer.setNameEnterBySvo(smSvAssistantOfficer.getName());
			}
		}
		//#28774 - End
		//END 24964
		try{
			//if Action equals to SUBMIT, set the BPM Info
			if(action.equals(SupplierManagementConst.BPM_SUBMIT)){
				// #30504 start
				String currPoLoginId = ((SMMOFRegDataType) getManagedBean().getBpmPojo()).getPoUsers();
				PmUser currPo = PmCommonUtil.retrieveUserByLoginId(currPoLoginId);
				log.info("---Curent PO: "+currPo.getUserName());

				if (currPo.getPmUserOrgs().get(0).getRecordStatusFlg() == 0) {
					IZoneSO zoneReqSO = (IZoneSO) BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT, IZoneSO.class);
					ZoneReqDTO zoneReqDTO = new ZoneReqDTO();
					zoneReqDTO.setStateId(svTask.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0)
							.getSmAddress().getPmState().getStateId());
					PmUser newPo = SmCommonUtil.getInstance().retrievePOWithLowestWeightage(
							svTask.getSmAppl().getSupplierType(), svTask.getSmAppl().getApplType(),
							svTask.getSmAppl().getApplCategoryType(),
							zoneReqSO.retrieveZoneByStateId(zoneReqDTO).getPmZone().getZoneCode());  
					//#30504 Re-open start
					currPo = newPo;
					SMMOFRegDataType smObj = (SMMOFRegDataType) this.getManagedBean().getBpmPojo();
					smObj.setPoUsers(newPo.getLoginId());
					this.getManagedBean().setBpmPojo(smObj);

				}
				log.info("---Curent PO Latest: "+currPo.getUserName());
				
				// #30504 end
				Object token = getFacesScopes().getBPMAuthenticationCtxToken();
				reqDTO.setAppId(this.getManagedBean().getAppId());
				reqDTO.setToken(token);
				reqDTO.setOutcome(SupplierManagementConst.BPM_SUBMIT);
				reqDTO.setTaskId(this.getManagedBean().getTaskId());
				reqDTO.setBpmPojo(this.getManagedBean().getBpmPojo());
				reqDTO.setPoName(currPo.getUserName());
				//#30504 Re-open end
			}

			reqDTO.setSmSvTask(svTask);
			reqDTO.setChangedBy(new FacesScopes().getUserSessionBean().getUserId());
			ISiteVisitSO reqSO = (ISiteVisitSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISiteVisitSO.class);
			SiteVisitResDTO resDTO = reqSO.saveOrSubmitSiteVisitReport(reqDTO);
			
			FacesContext fc = FacesContext.getCurrentInstance();
			if (resDTO.isSuccessful()) {
				if(action.equalsIgnoreCase(SupplierManagementConst.BPM_SUBMIT)){
					//#24964 - START
					log.info("START TROUBLESHOOT REVISION - #24964 SVO SER File");
					SiteVisitReqDTO siteVisitReqDTO = new SiteVisitReqDTO();
					siteVisitReqDTO.setSmSvTask(svTask);
					siteVisitReqDTO.setSvTaskId(svTask.getSvTaskId());
					
					SmSvTask svoTask = reqSO.retrieveSiteVisitInfoBySvTaskId(siteVisitReqDTO).getSmSvTask();
					
					String serType = "SVO";
					if(!SmCommonUtil.isSerFileExist(svTask.getSmAppl().getApplNo(), serType+"_"+svTask.getSvTaskId())){
						SmCommonUtil.createSerFileSVO(svoTask, serType);
					}
					log.info("END TROUBLESHOOT REVISION - #24964 SVO SER File");
					//#24964 - END
					
					fc.addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,MessageUtils.getMessage(MessageConst.INFO_721210,
							new String[]{this.getManagedBean().getSmSvTask().getSmAppl().getApplNo()},
							MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
				}else{
					fc.addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,
							MessageUtils.getMessage(MessageConst.INFO_700004, new String[]{this.getManagedBean().getSmSvTask().getSmAppl().getApplNo(), ""}, 
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale()))); 				  
				}			
				renewSesionObject();
				RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
				
				return "/xhtml/common/acknowledge.xhtml";
			} else {
				RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
			}
		}catch(Exception ex){
			RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
			throw new EpException(ex.getMessage());
		}
		
		return null;
	}
	
	/**
	 * check wheather user has svo role
	 * @return
	 */
	private String getUserRole() {	
		FacesScopes fs = new FacesScopes();
		if(fs.hasRoleUser(RoleCode.UPS_SVO.value()))
			return RoleCode.UPS_SVO.value();		
		else 
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
	 * set the selectedMachine to SmSvMachine
	 */
	/*private void setSelectedMachineToSmSvMachineLs(SmSvTemplate svTemplate){
		if(svTemplate.getSmSvMachines().size() > 0){
			log.info("====Start Size=====" + svTemplate.getSmSvMachines().size());
			// put existing smSvMachine to Map
			Map<Long, SmSvMachine> svMachineMap = new HashMap<Long, SmSvMachine>();
			Map<Long, SmMachineDtl> selectedMachineMap = new HashMap<Long, SmMachineDtl>();
			List<SmSvMachine> machineRemoveLs = new ArrayList<SmSvMachine>();

			for(SmSvMachine machine : svTemplate.getSmSvMachines()){
				svMachineMap.put(machine.getSmMachineDtl().getMachineDtlId(), machine);
			}

			for(SmMachineDtl selectedMachine : svTemplate.getSelectedSmMachine()){
				if(!svMachineMap.containsKey(selectedMachine.getMachineDtlId())){
					SmSvMachine svMachine = new SmSvMachine();
					svMachine.setSmSvTemplate(svTemplate);
					svMachine.setSmMachineDtl(selectedMachine);
					svTemplate.getSmSvMachines().add(svMachine);
				}

				selectedMachineMap.put(selectedMachine.getMachineDtlId(), selectedMachine);
			}

			//remove the unselected Machine from List
			for(SmSvMachine svMachine : svTemplate.getSmSvMachines()){
				if(!selectedMachineMap.containsKey(svMachine.getSmMachineDtl().getMachineDtlId())){
					machineRemoveLs.add(svMachine);
					log.info("remove machine - template ID ====" + svTemplate.getSvTemplateId());
					log.info("remove machine - SmMachineDt ID ====" + svMachine.getSmMachineDtl().getMachineDtlId());
					log.info("remove machine - SmSvMachine ID ====" + svMachine.getSvMachineId());
				}
			}

			svTemplate.getSmSvMachines().removeAll(machineRemoveLs);
			log.info("====end Size=====" + svTemplate.getSmSvMachines().size());
		}else{
			if(svTemplate.getSelectedSmMachine() != null && svTemplate.getSelectedSmMachine().length > 0){
				log.info("selected Machine is not null>>>>>");
				for(SmMachineDtl smMachineDtl : svTemplate.getSelectedSmMachine()){
					SmSvMachine svMachine = new SmSvMachine();
					svMachine.setSmSvTemplate(svTemplate);
					svMachine.setSmMachineDtl(smMachineDtl);
					svTemplate.getSmSvMachines().add(svMachine);
				}
			}
		}
	}*/
	
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
	
/*	*//**
	 * Retrieve Category Type
	 *//*
	private String retrieveCategoryTypeByLvl3Id(Long catL3Id){
		ICategoryCodeSO reqSO = (ICategoryCodeSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICategoryCodeSO.class);
		CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
		reqDTO.setCategoryL3Id(catL3Id);
		
		CategoryCodeResDTO resDTO = reqSO.retrieveCategoryL3DtlById(reqDTO);
		
		return resDTO.getCategoryL3().getCategoryTypeCode().getParameterCode();
		
	}*/
	
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
			log.info("******siteVisiteReportUpload******");
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
		log.info("setTemplateUpload>>>>>" + getManagedBean().getSelectedAttachmentTemplate().getSvTemplateId());
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
/*		Long templateId = getManagedBean().getSelectedAttachmentTemplId();*/
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

	public void signBoardOnChange(){
		if(!getManagedBean().getSmSvTask().getSmSvOffice().isSignboardValid()){
			
			getManagedBean().getSmSvTask().getSmSvOffice().setSignboardRemark(null);
		}
	}
	
	public void shareWithOnChange(){
		if(!getManagedBean().getSmSvTask().getSmSvOffice().isShare()){
			
			getManagedBean().getSmSvTask().getSmSvOffice().setShareWithRemark(null);
		}
	}
	
	/**
	 * review site visit report
	 */
	public void reviewSiteVisitReport(){
		getManagedBean().setViewMode(true);
		//getManagedBean().setAppId(1261306L); Manufacturing
		//getManagedBean().setAppId(1259470L);
		//SmSvTask svoTask = retrieveSiteVisitInfoByViewMode(viewMode);
		SiteVisitReqDTO reqDTO = new SiteVisitReqDTO();
		ISiteVisitSO reqSO = (ISiteVisitSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISiteVisitSO.class);
		reqDTO.setAppId(this.getManagedBean().getAppId());
		
		SmAppl appl = reqSO.retrieveSiteVisitInfoByAppId(reqDTO).getSmAppl();
		getManagedBean().setSmAppl(appl);
		
		for(SmSvTask smSvTask : appl.getSmSvTasks()){
			SmCommonUtil.getInstance().setSmSvAddressInfoBySiteTypeAndRefId(smSvTask.getSmSvAddress().getSiteType(),
					smSvTask.getSmSvAddress().getSiteRefId(),smSvTask.getSmSvAddress(), appl.getApplId());
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
	
	/**
	 * set as selected report task
	 * @param svTask
	 */
	public void selectReportTask(SmSvTask svTask){
		getManagedBean().setSvTaskId(svTask.getSvTaskId());
		//getManagedBean().setSelectedSvTask(svTask);
		retrieveSiteVisitInfo("V");
		RequestContext.getCurrentInstance().update("@form");
		//24964 - start
		String serType = "SVO";
		SmSvTask svoTask = getManagedBean().getSmSvTask();
		if(!SmCommonUtil.isSerFileExist(svoTask.getSmAppl().getApplNo(), serType+"_"+svoTask.getSvTaskId())){
			SmCommonUtil.createSerFileSVO(svoTask, serType);
		}
		if(getManagedBean().getSmSvtaskSVO() == null 
			|| getManagedBean().getSmSvTask().getSvTaskId() != getManagedBean().getSmSvtaskSVO().getSvTaskId()){
			getManagedBean().setSmSvtaskSVO(getPrevSvTask(
					getManagedBean().getSmSvTask().getSmAppl().getApplNo(), 
					getManagedBean().getSmSvTask().getSvTaskId(), "SVO"));
		
			log.info("#24964 - TR TROUBLESHOOT METHOD retrieveSiteVisitInfo");
			log.info("#24964 - SVTASK ID" + getManagedBean().getSmSvtaskSVO().getSvTaskId());
		}
		//24964 - end
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
		//#29430 - start
		//#24964 - start
		if(svTask == null){
			svTask = getManagedBean().getSmSvTask();
			try{
				SiteVisitReqDTO reqDTO = new SiteVisitReqDTO();
				reqDTO.setSmSvTask(svTask);
				reqDTO.setChangedBy(new FacesScopes().getUserSessionBean().getUserId());
				((ISiteVisitSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISiteVisitSO.class))
						.saveOrSubmitSiteVisitReport(reqDTO);
			}catch(Exception ex){
				throw new EpException(ex.getMessage());
			}
		}
		//#24964 - end
		getManagedBean().setFromReport(true);
		getManagedBean().setSvTaskId(svTask.getSvTaskId());
		retrieveSiteVisitInfo("V");
		getManagedBean().setFromReport(false);
		//#29430 - end
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
		
		log.info("getSvTaskCountByApplIdAndCategoryId>>>>>>>>>>>" + resDTO.getSiteVisitCount());
		
		return resDTO.getSiteVisitCount();
	}
	
	public List<SmSupplierCategory> retrieveSupplierCategoryByApplIdAndCategoryL3Id(){
		SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
		reqDTO.setApplId(getManagedBean().getSmSvTask().getSmAppl().getApplId());
		reqDTO.setCategoryId(getManagedBean().getSmSvTask().getPmCategoryL3().getCategoryL3Id());
		SupplierSearchResDTO resDTO = SmServiceLookup.getSupplierSearchSO().retrievePendingApprovalCategoryByApplIdAndCategoryId(reqDTO);
		
		return resDTO.getSmSupplierCategoryList();
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
		//24964
		getManagedBean().setSelectedSvMachine(new SmSvMachine());
		getManagedBean().getSelectedSvMachine().setSmMachineDtl(new SmMachineDtl());
		RequestContext.getCurrentInstance().execute("macDlg.hide()");
	}
	
	public String naviToAcknowledgement(){
		return CommonUIConst.ACKNOWLEDGE_PAGE;
	}
	
	private SmAddress getSmAddressById(Long addressId){
		SmAddress address = SmCommonUtil.getInstance().getSmAddressById(addressId);
		return address;
	}
	
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
		Map<String, String> purchaseMethodMap = new HashMap<String,String>();
		List<PmParameterDesc> purchaseMethodLs = SmCommonUtil.retrieveParameterDescList(ParameterTypeConst.PURCHASING_METHOD, "en");
		
		//CR 28774: Static Code Analysis
		if(!purchaseMethodLs.isEmpty()){
			for(PmParameterDesc pmDesc : purchaseMethodLs){
				purchaseMethodMap.put(pmDesc.getPmParameter().getParameterCode(),pmDesc.getCodeName());
			}
		}
		
		return purchaseMethodMap;
	}

	public void addAdditionalManufacturing() {
		this.getManagedBean().setSelectedManufacturer(new SmManufacturingDtl());
		this.getManagedBean().getSmSvTask().getSmAppl().getSmManufacturingDtls();

		SmManufacturingDtl manufacDtl = new SmManufacturingDtl();
		manufacDtl.setRevNo(1);
		manufacDtl.setBrandNew(true);
		manufacDtl.setSmAppl(getManagedBean().getSmSvTask().getSmAppl());
		this.getManagedBean().setSelectedManufacturer(manufacDtl);
		//24964
		getManagedBean().setLatestManufacturer(null);

	}

	public void saveSmManufacturingDtl() {
		SmManufacturingDtl manufacturingDtl = getManagedBean().getSelectedManufacturer();
		SmManufacturingDtl oriManufacturingDtl = (SmManufacturingDtl) getManagedBean().getOriSelectedManufacturingDtl();

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
		//CR 28774: Static Code Analysis
		/*
		if (!getManagedBean().getSelectedManufacturingList().isEmpty()) {
			EntityHelper.compareAndRemoveEntityFromList(getManagedBean().getSelectedManufacturingList(), getManagedBean().getSmSvTask().getSmAppl().getSmManufacturingDtls(),
					"manufacturingDtlId");
			getManagedBean().getSelectedManufacturingList().clear();
		} else {
			throw new EpException(MessageConst.ERROR_722534, new String[] { "Manufacturer" });
		}
		*/
		//#24964 - start
		if(getManagedBean().getSelectedManufacturingList() == null) return;
		for (SmManufacturingDtl man : getManagedBean().getSelectedManufacturingList()){
			getManagedBean().getSmSvTask().getSmAppl().getSmManufacturingDtls().remove(man);
		}
		getManagedBean().setSelectedManufacturingList(null);
		//#24964 - end
	}
	
	// #24964 - start
	public void saveSiteVisitReportByPO(){		
		log.info("***********saveSiteVisitReportByPO***********");
		SiteVisitReqDTO reqDTO = new SiteVisitReqDTO();
		SmSvTask svTask = this.getManagedBean().getSmSvTask();	
		
		//validate assistant officer
		//#28774 - Start
		if(svTask.getSmSvAssistantOfficers()==null){
			svTask.setSmSvAssistantOfficers(null);
		}
		//#28774 - End
		if(svTask.getSmSvTemplate()!=null){
			if(svTask.getSmSvTemplate().getSmSvInterviewees()== null || svTask.getSmSvTemplate().getSmSvInterviewees().size()<=0){
				throw new EpException(SmCommonUtil.getMessage("lbl.sm.required.msg",
						new String[] { SmCommonUtil.getMessage("lbl.sm.sv.templ.interview.representative", null) }));
			}else{
				for(SmSvInterviewee interviewee : svTask.getSmSvTemplate().getSmSvInterviewees()){
					if(StringUtils.isBlank(interviewee.getName()) || StringUtils.isBlank(interviewee.getDesignation())){
						return;
					}
				}
			}
		}

		reqDTO.setSmSvTask(svTask);
		reqDTO.setChangedBy(new FacesScopes().getUserSessionBean().getUserId());
		ISiteVisitSO reqSO = (ISiteVisitSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISiteVisitSO.class);
		reqSO.saveOrSubmitSiteVisitReport(reqDTO);
		//#24964 - start
		String statusId = svTask.getSmAppl().getPmStatus().getStatusId().toString();
		if(statusId.equals(SupplierManagementConst.PM_STATUS_PENDING_REVIEW_SITE_VISIT_REPORT)
				|| statusId.equals(SupplierManagementConst.PM_STATUS_PENDING_RE_REVIEW )){
			SmCommonUtil.createSerFileSVO(svTask, "PO");
		}
		//retrieveSiteVisitInfo("V");
		//#24964 - end
		RequestContext.getCurrentInstance().execute("poReportDlg.hide()");
	} 
	
	public boolean showEditAssistant(SmSvAssistantOfficer assistantOfficer){
		boolean edit = false;
		if(assistantOfficer == null){
			return false;
		}
		if(assistantOfficer.getName() != null && assistantOfficer.getNameEnterBySvo() != null){
			if(!assistantOfficer.getName().equalsIgnoreCase(assistantOfficer.getNameEnterBySvo())){
				edit = true;
			}
		}
		return edit;
	}
	
	// #24964 - end
	
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
		//#24964 - start
		if(fs.hasRoleUser(RoleCode.UPS_PO.value())){
			log.info("TR Troubleshoot #24964 === 1");
			if(getManagedBean().getSmSvTask().getSmAppl().getPmStatus().getStatusId() == SupplierManagementConst.PM_STATUS_PENDING_SITE_VISIT_REPORT_AFTER_APPROVED 
				|| getManagedBean().getSmSvTask().getSmAppl().getPmStatus().getStatusId().equals(SupplierManagementConst.PM_STATUS_PENDING_REVIEW_SITE_VISIT_REPORT)) {
					log.info("TR Troubleshoot #24964 === 2");
					return false;
			}
		}
		//#24964 - end
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
	
	//#24964 - start
	public static SmSvTask getPrevSvTask(String applNo, long svTaskId, String role){
		try {
			String fileName = SmCommonUtil.readSerFileSvTask(applNo, svTaskId, role);
			if (!StringUtils.isBlank(fileName)) {
				SmApplRevisionReqDTO reqDTO = new SmApplRevisionReqDTO();
				reqDTO.setDeSerFileName(fileName);
				SmApplRevisionResDTO resDTO = SmServiceLookup
						.getSmApplRevisionSO().readSmSvTask(reqDTO);
				if (resDTO.getSmAppl().getSmSvTasks() != null) {
					log.info("#24964 - TR TROUBLESHOOT METHOD getSVOTask");
					log.info("SVTASK ID - " + resDTO.getSmAppl().getSmSvTasks().get(0).getSvTaskId());
					return resDTO.getSmAppl().getSmSvTasks().get(0);
				}
			}
		} catch (Exception ex) {}
		return null;
	}
	
	private static SmAppl getTempSmAppl(String applNo){
		try {
			String fileName = SmCommonUtil.getSerializedFileFullDirByApplNo(applNo, "SUP");
			if (!StringUtils.isBlank(fileName)) {
				SmApplRevisionReqDTO reqDTO = new SmApplRevisionReqDTO();
				reqDTO.setDeSerFileName(fileName);
				SmApplRevisionResDTO resDTO = SmServiceLookup
						.getSmApplRevisionSO().readApplicationHist(reqDTO);
				if (resDTO.getSmAppl() != null) {
					return resDTO.getSmAppl();
				}
			}
		} catch (Exception ex) {}
		return null;
	}
	
	public SmSvLicenseInsurance getSVOLicense(SmSvLicenseInsurance license){
		if(license == null || getManagedBean().getSmSvtaskSVO() == null ||
			getManagedBean().getSmSvtaskSVO().getSmSvTemplate().getSmSvLicenseInsurances() == null){
				return null;
		}
		return (SmSvLicenseInsurance) EntityHelper.getEntityByMultiplePropertiesFromList(license, 
				getManagedBean().getSmSvtaskSVO().getSmSvTemplate().getSmSvLicenseInsurances(),
				new String[] { "svLicenseInsuranceId" }, null, null);
	}
	
	public SmSvDocLicense getSVOAdtLicense(SmSvDocLicense license){
		if(license == null || getManagedBean().getSmSvtaskSVO() == null ||
			getManagedBean().getSmSvtaskSVO().getSmSvDocLicenses() == null){
				return null;
		}
		return (SmSvDocLicense) EntityHelper.getEntityByMultiplePropertiesFromList(license, 
				getManagedBean().getSmSvtaskSVO().getSmSvDocLicenses(),
				new String[] { "svDocLicenseId" }, null, null);
	}
	
	public SmSvEquipment getSVOSmSvEquipment(SmSvEquipment equipment){
		if(equipment == null || getManagedBean().getSmSvtaskSVO() == null ||
				getManagedBean().getSmSvtaskSVO().getSmSvTemplate().getSmSvEquipments() == null){
					return null;
		}
		return (SmSvEquipment) EntityHelper.getEntityByMultiplePropertiesFromList(equipment, 
				 getManagedBean().getSmSvtaskSVO().getSmSvTemplate().getSmSvEquipments(),
				new String[] { "svEquipmentId" }, null, null);
	}
	
	public SmSvWorker getSVOSmSvWorker(SmSvWorker worker){
		if(worker == null || getManagedBean().getSmSvtaskSVO() == null ||
				getManagedBean().getSmSvtaskSVO().getSmSvTemplate().getSmSvWorkers() == null){
					return null;
		}
		return (SmSvWorker) EntityHelper.getEntityByMultiplePropertiesFromList(worker, 
				getManagedBean().getSmSvtaskSVO().getSmSvTemplate().getSmSvWorkers(),
				new String[] { "svWorkerId" }, null, null);
	}
	
	public SmSvPrintwork getSVOSmSvPrintwork(SmSvPrintwork printwork){
		if(printwork == null || getManagedBean().getSmSvtaskSVO() == null ||
				getManagedBean().getSmSvtaskSVO().getSmSvTemplate().getSmSvPrintworks() == null){
					return null;
		}
		return (SmSvPrintwork) EntityHelper.getEntityByMultiplePropertiesFromList(printwork, 
				getManagedBean().getSmSvtaskSVO().getSmSvTemplate().getSmSvPrintworks(),
				new String[] { "svPrintworkId" }, null, null);
	}
	
	public SmSvMachine getSVOSmSvMachine(SmSvMachine machine){
		if(machine == null || getManagedBean().getSmSvtaskSVO() == null ||
				getManagedBean().getSmSvtaskSVO().getSmSvTemplate().getSmSvMachines() == null){
					return null;
		}
		return (SmSvMachine) EntityHelper.getEntityByMultiplePropertiesFromList(machine, 
				getManagedBean().getSmSvtaskSVO().getSmSvTemplate().getSmSvMachines(),
				new String[] { "svMachineId" }, null, null);
	}
	
	public SmSvManufacturingDoc getSVOSmSvManufacturingDoc(SmSvManufacturingDoc finDoc){
		if(finDoc == null || getManagedBean().getSmSvtaskSVO() == null ||
				getManagedBean().getSmSvtaskSVO().getSmSvTemplate().getSmSvManufacturingDocs() == null){
					return null;
		}
		return (SmSvManufacturingDoc) EntityHelper.getEntityByMultiplePropertiesFromList(finDoc, 
				getManagedBean().getSmSvtaskSVO().getSmSvTemplate().getSmSvManufacturingDocs(),
				new String[] { "svManufacturingDocId" }, null, null);
	}
	
	public boolean isSVOSmSvMachineDifferent(SmSvMachine machine){
		if(getManagedBean().getSmSvtaskSVO() == null) return false;
		SmSvMachine svoMachine = getSVOSmSvMachine(machine);
		if(svoMachine == null) return true;
		return SmCommonUtil.isHasDifferences(machine.getSmMachineDtl(), svoMachine.getSmMachineDtl(), null);
	}
	
	public SmSvAdtMachine getSVOSmSvAdtMachine(SmSvAdtMachine machine){
		if(machine == null || getManagedBean().getSmSvtaskSVO() == null ||
				getManagedBean().getSmSvtaskSVO().getSmSvTemplate().getSmSvAdtMachines() == null){
					return null;
		}
		return (SmSvAdtMachine) EntityHelper.getEntityByMultiplePropertiesFromList(machine, 
				getManagedBean().getSmSvtaskSVO().getSmSvTemplate().getSmSvAdtMachines(),
				new String[] { "svAdtMachineId" }, null, null);
	}
	
	public boolean isSVOSmSvAdtMachineDifferent(SmSvAdtMachine machine){
		if(getManagedBean().getSmSvtaskSVO() == null) return false;
		SmSvAdtMachine svoMachine = getSVOSmSvAdtMachine(machine);
		return SmCommonUtil.isHasDifferences(machine, svoMachine, null);
	}
	
	
	public SmManufacturingDtl getSVOSmManufacturingDtl(SmManufacturingDtl manu){
		if(manu == null || getManagedBean().getSmSvtaskSVO() == null ||
				getManagedBean().getSmSvtaskSVO().getSmAppl().getSmManufacturingDtls() == null){
					return null;
		}
		return (SmManufacturingDtl) EntityHelper.getEntityByMultiplePropertiesFromList(manu, 
				getManagedBean().getSmSvtaskSVO().getSmAppl().getSmManufacturingDtls(),
				new String[] { "manufacturingDtlId" }, null, null);
	}
	
	public boolean isSVOSmManufacturingDtlDifferent(SmManufacturingDtl manu){
		if(getManagedBean().getSmSvtaskSVO() == null) return false;
		SmManufacturingDtl svoManu = getSVOSmManufacturingDtl(manu);
		return SmCommonUtil.isHasDifferences(manu, svoManu, null);
	}
	
	public void mapSmSvBankSVOWithPO(List<SmSvBankInfo> svoSmSvBankInfoLs){
		log.info("=============#24964 - execute mapSmSvBankSVOWithPO=======================");
		
		if(svoSmSvBankInfoLs.isEmpty() || svoSmSvBankInfoLs == null){
			log.info("SVO SmSvBank List id null or empty");
		}
		
		if(!svoSmSvBankInfoLs.isEmpty() || svoSmSvBankInfoLs != null){
			for(SmSvBankInfo smSvBankInfo : getManagedBean().getSmSvTask().getSmSvBankInfos()){
				log.info("poBank - "+smSvBankInfo.getSvBankInfoId());
				for(SmSvBankInfo svoSmSvBankInfo : svoSmSvBankInfoLs){
					log.info("svoBank - "+svoSmSvBankInfo.getSvBankInfoId());
					if(smSvBankInfo != null & svoSmSvBankInfo != null){
						if(smSvBankInfo.getSvBankInfoId().equals(svoSmSvBankInfo.getSvBankInfoId())){
							log.info("poBank is equal svoBank");
							smSvBankInfo.setSvoSmSvBankInfo(svoSmSvBankInfo);
							log.info("test read after set id = " + smSvBankInfo.getSvoSmSvBankInfo().getSvBankInfoId());
						}
					}
				}
			}
		}
		log.info("=============#24964 - finished mapSmSvBankSVOWithPO=======================");
	}
	
	public void mapfinLsItemSVOWithPO(List<SmSvFinChecklist> svoSmSvFinChecklists){
		log.info("=============#24964 - execute mapfinLsItemSVOWithPO=======================");
		
		if(svoSmSvFinChecklists.isEmpty() || svoSmSvFinChecklists == null){
			log.info("SVO SmSvFinChecklist List id null or empty");
		}
		
		if(!svoSmSvFinChecklists.isEmpty() || svoSmSvFinChecklists != null){
			for(SmSvFinChecklist smSvFinChecklist : getManagedBean().getSmSvTask().getSmSvFinChecklists()){
				log.info("poCheckList - "+smSvFinChecklist.getSvFinChecklistId());
				for(SmSvFinChecklist svoSmSvFinChecklist : svoSmSvFinChecklists){
					log.info("svoCheckList - "+svoSmSvFinChecklist.getSvFinChecklistId());
					if(smSvFinChecklist != null && svoSmSvFinChecklist != null){
						if(smSvFinChecklist.getSvFinChecklistId().equals(svoSmSvFinChecklist.getSvFinChecklistId())){
							log.info("poCheckList is equal svoCheckList");
							smSvFinChecklist.setSvoSmSvFinChecklist(svoSmSvFinChecklist);
							log.info("test read after set id = " + smSvFinChecklist.getSvoSmSvFinChecklist().getSvFinChecklistId());
						}
					}
				}
			}
		}
		log.info("=============#24964 - finished mapfinLsItemSVOWithPO=======================");
	}
	
	public void mapSmSvIntervieweesSVOWithPO(List<SmSvInterviewee> svoSmSvIntervieweeLs){
		log.info("=============#24964 - execute mapSmSvIntervieweesSVOWithPO=======================");
		
		if(svoSmSvIntervieweeLs.isEmpty() || svoSmSvIntervieweeLs == null){
			log.info("SVO SmSvInterviewee List id null or empty");
		}

		if(!svoSmSvIntervieweeLs.isEmpty() || svoSmSvIntervieweeLs != null){
			for(SmSvInterviewee smSvInterviewee : getManagedBean().getSmSvTask().getSmSvTemplate().getSmSvInterviewees()){
				log.info("poInterviewee - "+smSvInterviewee.getSvIntervieweeId());
				for(SmSvInterviewee svoSmSvInterviewee : svoSmSvIntervieweeLs){
					log.info("svoInterviewee - "+svoSmSvInterviewee.getSvIntervieweeId());
					if(smSvInterviewee != null && svoSmSvInterviewee != null){
						if(smSvInterviewee.getSvIntervieweeId()  == svoSmSvInterviewee.getSvIntervieweeId()){
							log.info("poInterviewee is equal svoInterviewee");
							smSvInterviewee.setSvoSmSvInterviewee(svoSmSvInterviewee);
							log.info("test read after set id = " + smSvInterviewee.getSvoSmSvInterviewee().getSvIntervieweeId());
						}
					}
				}
			}
		}
		log.info("=============#24964 - finished mapSmSvIntervieweesSVOWithPO=======================");
	}
	//#24964 - end
	
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
}
