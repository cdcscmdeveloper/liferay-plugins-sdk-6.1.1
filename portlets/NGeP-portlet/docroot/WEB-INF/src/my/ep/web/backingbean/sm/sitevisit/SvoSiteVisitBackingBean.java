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
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.application.ViewHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import my.ep.biz.service.sm.remarks.ISmRemarksSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.biz.service.sm.siteVisit.ISiteVisitSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.dto.sm.siteVisit.SiteVisitReqDTO;
import my.ep.dto.sm.siteVisit.SiteVisitResDTO;
import my.ep.dto.sm.smRemarks.SmRemaksResDTO;
import my.ep.dto.sm.smRemarks.SmRemarksReqDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmChequeSignatory;
import my.ep.persistence.entity.sm.SmFacility;
import my.ep.persistence.entity.sm.SmMachineDtl;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmRemark;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.persistence.entity.sm.SmSvAddress;
import my.ep.persistence.entity.sm.SmSvAdtMachine;
import my.ep.persistence.entity.sm.SmSvBankInfo;
import my.ep.persistence.entity.sm.SmSvCategory;
import my.ep.persistence.entity.sm.SmSvComment;
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
import my.ep.persistence.entity.sm.SmSvTemplateAddress;
import my.ep.persistence.entity.sm.SmSvWorker;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.sitevisit.SvoSiteVisitManagedBean;
import my.ep.web.util.AddressUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.VmUtil;
import my.ep.web.util.template.DocumentGenerator;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;

@ManagedBean(name = "svoSiteVisitBackingBean")
@RequestScoped
public class SvoSiteVisitBackingBean extends BaseBackingBean<SvoSiteVisitManagedBean> {
	public static Logger log = Logger.getLogger(SvoSiteVisitManagedBean.class);

	private List<String> printingCodeLs = Arrays.asList(SupplierManagementConst.P221601, SupplierManagementConst.P221602, 
			SupplierManagementConst.P221603, SupplierManagementConst.P221604, SupplierManagementConst.P221605,  
			SupplierManagementConst.P221607, SupplierManagementConst.P221609,SupplierManagementConst.P221610,
			SupplierManagementConst.P221611,SupplierManagementConst.P221612,SupplierManagementConst.P221613,SupplierManagementConst.P221614,
			SupplierManagementConst.P222003);

	private String tmpPath = null;
	private String tmpFolder = "SiteVistTemplate" + "/";
	private String fileInitName;
	private String templateDesc;
	private Locale locale = new Locale("en");
	private final String templateLangCode = "en";
	
	private StreamedContent file;
	
	public SvoSiteVisitBackingBean() {

	}
	
	private void populateSmSvAddresses(){
		ISiteVisitSO reqSO = (ISiteVisitSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISiteVisitSO.class);
		SiteVisitReqDTO reqDTO = new SiteVisitReqDTO();
		SiteVisitResDTO resDTO = new SiteVisitResDTO();
		getManagedBean().setSmSvFacilityMap(new HashMap<String, SmFacility>());
		
		Map<String, SmFacility> facilityMap = new HashMap<String,SmFacility>();
		
		Map<String, ArrayList<String>> categoryFacilityMap = new HashMap<String,ArrayList<String>>();
		
		//get facility by category
		for(SmSvCategory category :  getManagedBean().getSmSvTask().getSmSvCategories()){
			if(categoryFacilityMap.get(category.getPmCategoryL3().getCategoryTypeCode().getParameterCode()) == null){
				categoryFacilityMap.put(category.getPmCategoryL3().getCategoryTypeCode().getParameterCode(), new ArrayList<String>());
			}
			
			categoryFacilityMap.get(category.getPmCategoryL3().getCategoryTypeCode().getParameterCode()).add(category.getPmCategoryL3().getFacilityType());
			
			//categoryFacilityMap.put(category.getPmCategoryL3().getCategoryTypeCode().getParameterCode(), category.getPmCategoryL3().getFacilityType());
		}
		
		getManagedBean().setSmSvAddressMap(new HashMap<String, List<SmSvAddress>>());
		for(SmSvAddress smSvAddress : getManagedBean().getSmSvTask().getSmSvAddresses()){
			reqDTO.setSmAddressId(smSvAddress.getSmAddress().getAddressId());
			resDTO = reqSO.retrieveSmFacilityByAddressId(reqDTO);
			
			// to group the related facility by template
			if(resDTO.getSmFacility() != null && resDTO.getSmFacilityType() != null){
				
				//store all facilities. use it when no matched facility
				facilityMap.put(resDTO.getSmFacilityType(), resDTO.getSmFacility());
				
				for(Map.Entry<String, SmSvTemplate> svTemplate : getManagedBean().getSmSvTemplateMap().entrySet()){
					if(getManagedBean().getSmSvAddressMap().get(svTemplate.getKey()) == null){
						getManagedBean().getSmSvAddressMap().put(svTemplate.getKey(), new ArrayList<SmSvAddress>());
					}			
					
					//find the matched facility address and put in the MAP by template type
					if(categoryFacilityMap.get(svTemplate.getKey()) != null){
						
						for(String fac : categoryFacilityMap.get(svTemplate.getKey())){
							if(resDTO.getSmFacilityType().equalsIgnoreCase(fac)){
								log.info("**************fac*************" + fac);
								log.info("******Address Map******" + getManagedBean().getSmSvAddressMap().size());
								
								getManagedBean().getSmSvAddressMap().get(svTemplate.getKey()).add(smSvAddress);
								
								if(svTemplate.getKey().equalsIgnoreCase(SupplierManagementConst.CAT_TYPE_WORKSHOP) || 
										svTemplate.getKey().equalsIgnoreCase(SupplierManagementConst.CAT_TYPE_PRINTING) || 
										svTemplate.getKey().equalsIgnoreCase(SupplierManagementConst.CAT_TYPE_DOCKYARD)){
									//set the 
									if(getManagedBean().getSmSvFacilityMap().get(svTemplate.getKey()) == null){
										getManagedBean().getSmSvFacilityMap().put(svTemplate.getKey(), resDTO.getSmFacility());
									}
								}
							}
						}
					}
					
					if(resDTO.getSmFacilityType().equalsIgnoreCase(SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE) || 
							resDTO.getSmFacilityType().equalsIgnoreCase(SupplierManagementConst.SM_FACILITY_TYPE_HOMEOFFICE)){
						int count = 0;
						for(SmSvAddress svAddress : getManagedBean().getSmSvAddressMap().get(svTemplate.getKey())){
							if(svAddress.getSvAddressId() == smSvAddress.getSvAddressId()){
								count++;
							}
						}
						
						if(count < 1){
							getManagedBean().getSmSvAddressMap().get(svTemplate.getKey()).add(smSvAddress);
						}
					}
					
				}
				
			}
		}
	}
	
	private void setSelectedSvTemplateAddresses(String templateType){
		List<SmSvAddress> selectedAddress = getManagedBean().getSmSvAddressMap().get(templateType);
		getManagedBean().getSmSvTask().setSmSvAddresses(selectedAddress);
	}
	
	private void setSelectedSmSvFacility(String templateType){
		log.info("*******setSelectedSmSvFacility*********");
		SmFacility smFacility = getManagedBean().getSmSvFacilityMap().get(templateType);
		SmSvFacility smSvFacility = null;
		if(getManagedBean().getSelectedSvTemplate().getSmSvFacility()!=null 
				&& getManagedBean().getSelectedSvTemplate().getSmSvFacility().getSvFacilityId()!=null){
			log.info("***already set smsvfacility***");
		}else{
/*			log.info("allFacilityMap****" + this.getManagedBean().getAllFacilityMap().size());
			if(smFacility == null){
				// randomly assign a facility
				//String key = getManagedBean().getFacilityTypeAddressMap().get(getManagedBean().getSmSvTask().getSmSvAddresses().get(0).getSvAddressId());
				smFacility = this.getManagedBean().getAllFacilityMap().entrySet().iterator().next().getValue();

				log.info("******SmFacility******" + smFacility.getFacilityType());
			}*/
			
			if(smFacility != null){
				NumberFormat formatter = new DecimalFormat("#0.00");
				smSvFacility = new SmSvFacility();
				smSvFacility.setOwnershipType(smFacility.getOwnershipType());
				smSvFacility.setOverallSqft(formatter.format(BigDecimal.ZERO));
				smSvFacility.setEnclosedSqft(formatter.format(BigDecimal.ZERO));
				if(smFacility.getAreaSquareFeet()!=null){
					smSvFacility.setOverallSqft(formatter.format(smFacility.getAreaSquareFeet()));
					smSvFacility.setEnclosedSqft(formatter.format(smFacility.getAreaSquareFeet()));
				}
				if(smFacility.getOwnershipType().equals("R")){
					smSvFacility.setPurchaseRentPrice(smFacility.getRentalRate());
				}else{
					smSvFacility.setPurchaseRentPrice(smFacility.getPurchasePrice());
				}
				smSvFacility.setSmSvTemplate(this.getManagedBean().getSmSvTemplateMap().get(templateType));
				getManagedBean().getSelectedSvTemplate().setSmSvFacility(smSvFacility);
			}else{
				NumberFormat formatter = new DecimalFormat("#0.00");
				smSvFacility = new SmSvFacility();
				smSvFacility.setOverallSqft(formatter.format(BigDecimal.ZERO));
				smSvFacility.setEnclosedSqft(formatter.format(BigDecimal.ZERO));
		
				smSvFacility.setSmSvTemplate(this.getManagedBean().getSmSvTemplateMap().get(templateType));
				getManagedBean().getSelectedSvTemplate().setSmSvFacility(smSvFacility);
			}
		}
	}
	
	public void retrieveSiteVisitInfo(String viewMode){
		//this.getManagedBean().setAppId(1257118);
		//this.getManagedBean().setAppId(1257442);
		//this.getManagedBean().setAppId(1257676); //FT
		//this.getManagedBean().setAppId(1257345);//FT
		//this.getManagedBean().setAppId(1257944);//FT
		
		this.getManagedBean().setActiveIndex(0);
		
		log.info("--------retrieveSiteVisitInfo--------" + this.getManagedBean().getAppId());
		SiteVisitReqDTO reqDTO = new SiteVisitReqDTO();
		reqDTO.setAppId(this.getManagedBean().getAppId());
		ISiteVisitSO reqSO = (ISiteVisitSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISiteVisitSO.class);
		SiteVisitResDTO resDTO = reqSO.retrieveSiteVisitInfoByAppId(reqDTO);

		if(resDTO.getSmSvTask() != null){
			this.getManagedBean().setSmSvTask(resDTO.getSmSvTask());
			SmSvTask smSvTask = this.getManagedBean().getSmSvTask();
			//		this.getManagedBean().getSmSvTask().getsm
			log.info("*****this.getManagedBean().getSmSvTask().getSmSvFinChecklists().size()" + smSvTask.getSmSvFinChecklists().size());
			if(smSvTask.getSmSvFinChecklists().size() < 1){
				log.info("******getSmSvFinChecklists() == null******");
				List<PmParameterDesc> pmParamDesc = new ArrayList<PmParameterDesc>();
				pmParamDesc = SmCommonUtil.retrieveParameterDescList(SupplierManagementConst.SVO_FIN_CHECKLIST, new FacesScopes().getUserLocale().getLanguage());
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

			if(this.getManagedBean().getSmSvTemplateMap() == null){
				this.getManagedBean().setSmSvTemplateMap(new HashMap<String, SmSvTemplate>());
				for(SmSvTemplate svTemp : smSvTask.getSmSvTemplates()){
					this.getManagedBean().getSmSvTemplateMap().put(svTemp.getPmParameter().getParameterCode(),svTemp);
				}
			}

			if(smSvTask.getSmSvOffice() == null){
				smSvTask.setSmSvOffice(new SmSvOffice());
				smSvTask.getSmSvOffice().setSmSvTask(this.getManagedBean().getSmSvTask());
			}
			
			for(SmPersonnel contactPerson : smSvTask.getSmAppl().getSmPersonnels()){
				if(contactPerson.isContactPerson()){
					this.getManagedBean().setContactPerson(contactPerson);
					break;
				}
			}
			
			//Set Template Map for index of tabMenu
			this.getManagedBean().setIndexPageMap(new HashMap<Integer,String>());
			int i = viewMode.equalsIgnoreCase("V") ? 1 : 4;
			for (Map.Entry<String, SmSvTemplate> entry : this.getManagedBean().getSmSvTemplateMap().entrySet()) {
				this.getManagedBean().getIndexPageMap().put(i, entry.getKey());
				i++;
			}
			
			if(!viewMode.equalsIgnoreCase("V")){
				this.getManagedBean().setLastIndex(i);
			}
			
			getTotalBODMgmtWorkerEquityOwner();
			chequeSignatoryInfo();
			//getAllApprovedCategoryCode();
			List<SmSupplierCategory> suppCatLs = retrieveActiveCategoryCode(smSvTask.getSmAppl().getSmSupplier().getSmAppl().getApplId());
			this.getManagedBean().setCategoryApplicable(suppCatLs);
			
			List<SmSupplierCategory> catLs = retrieveAllApprovedCategoryCode(smSvTask.getSmAppl().getApplId());
			
			this.getManagedBean().setSvTaskCategories(new ArrayList<SmSupplierCategory>());
			for(SmSvCategory taskCat : this.getManagedBean().getSmSvTask().getSmSvCategories()){
				log.info("Supp Category L3>>>>>>" + taskCat.getPmCategoryL3().getCategoryL3Id());
				
				INNER:
				for(SmSupplierCategory suppCat : catLs){
					log.info("suppCat L3>>>>>" +   suppCat.getPmCategoryL3().getCategoryL3Id());
					if(taskCat.getPmCategoryL3().getCategoryL3Id() == suppCat.getPmCategoryL3().getCategoryL3Id()){
						this.getManagedBean().getSvTaskCategories().add(suppCat);
						break INNER;
					}
				}
			}
		
			
			populateSmSvAddresses();
			groupCategoryCodeByType();
		}
	}
	
	public void rptPreview(String catType){
		log.info("-------rpt--------" + catType);
		this.getManagedBean().setSelectedSvTemplate(this.getManagedBean().getSmSvTemplateMap().get(catType));
		
		SmSvTemplate svTemplate = this.getManagedBean().getSelectedSvTemplate();
		//svTemplate.setSmSvTemplateAddresses(new ArrayList<SmSvTemplateAddress>());
		
		/*if(svTemplate.getSelectedAddresses() != null && svTemplate.getSelectedAddresses().length > 0){
			log.info("selected Address is not null>>>>>");
			for(SmSvAddress selectedAdd : svTemplate.getSelectedAddresses()){
				SmSvTemplateAddress svTempAdd = new SmSvTemplateAddress();
				svTempAdd.setSmSvTemplate(svTemplate);
				svTempAdd.setSmSvAddress(selectedAdd);
				svTemplate.getSmSvTemplateAddresses().add(svTempAdd);
			}
		}*/
		
		if(svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_DOCKYARD) || 
				svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_WORKSHOP) ||
				svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_PRINTING) || 
				svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_MANUFACTURING)){
			svTemplate.setSmSvMachines(new ArrayList<SmSvMachine>());
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
		
		if(svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_DOCKYARD) || 
				svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_WORKSHOP) ||
				svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_PRINTING)){
			if(svTemplate.getSmSvFacility() == null){
				setSelectedSmSvFacility(svTemplate.getPmParameter().getParameterCode());
			}
		}
		//log.info("---svTemplate selected ADD----------" + this.getManagedBean().getSelectedSvTemplate().getSmSvTemplateAddresses().size());
	}
	
	public void changeActiveIndex(int index,String catType){
		log.info("*******changeActiveIndex********" + index);
		this.getManagedBean().setActiveIndex(index);
		//log.info("*******Category pass********" + this.getManagedBean().getSmSvTask().getSmSvCategories().get(0).getPassed()+"****"+ this.getManagedBean().getSmSvTask().getSmSvCategories().get(0).getPmCategoryL3().getCategoryL3Code());
		//log.info("*******changeActiveIndex CatType********" + catType.length());
		if(catType.length() > 0){
			this.getManagedBean().setSelectedSvTemplate(this.getManagedBean().getSmSvTemplateMap().get(catType));
			SmSvTemplate svTemplate = this.getManagedBean().getSmSvTemplateMap().get(catType);
			
			String catCode = svTemplate.getPmParameter().getParameterCode();
			if(svTemplate.getSmSvInterviewees() == null){
				svTemplate.setSmSvInterviewees(new ArrayList<SmSvInterviewee>());
			}
			
			if(svTemplate.getSmSvComment() == null){
				svTemplate.setSmSvComment(new SmSvComment());
				svTemplate.getSmSvComment().setSmSvTemplate(svTemplate);
			}
			
			if(svTemplate.getSmSvTemplateAddresses() == null){
				svTemplate.setSmSvTemplateAddresses(new ArrayList<SmSvTemplateAddress>());
			}
			
			if(svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_DOCKYARD) || 
					svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_WORKSHOP) ||
					svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_PRINTING)){
				if(svTemplate.getSmSvFacility() == null){
					svTemplate.setSmSvFacility(new SmSvFacility());
				}
				//svTemplate.getSmSvFacility().setSmSvTemplate(svTemplate);
			}
			
			if(svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_DOCKYARD)){
				this.prepareEquipmentLicenseInsurance(svTemplate, catCode);
				
			}else if(svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_WORKSHOP)){
				this.prepareEquipmentLicenseInsurance(svTemplate, catCode);
				
				if(svTemplate.getSmSvWorkers() == null){
					log.info(">>>>>svTemplate.getSmSvWorkers() IS NULL");
					svTemplate.setSmSvWorkers(new ArrayList<SmSvWorker>()); 
				}
			}else if(svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_MANUFACTURING)){
				this.prepareFinancialDoc(svTemplate);
				
				log.info("-----FIN LS SIZE----" + svTemplate.getFinancialDocLs().size());
				
			}else if(svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_PRINTING)){
				
				if(svTemplate.getSmSvPrintworks() == null){
					svTemplate.setSmSvPrintworks(new ArrayList<SmSvPrintwork>());				
				}
				
				if(svTemplate.getSmSvLicenseInsurances() == null){
					svTemplate.setSmSvLicenseInsurances(new ArrayList<SmSvLicenseInsurance>());
				}
				
				if(svTemplate.getSmSvLicenseInsurances().size() < 1){
					prepareEquipmentLicenseInsurance(svTemplate,catCode);
				}
				
				preparePrintingAcc(svTemplate);
			}
			
			if(svTemplate.getPmParameter()!=null && svTemplate.getPmParameter().getParameterCode()!=null){
				setSelectedSmSvFacility(svTemplate.getPmParameter().getParameterCode());
			}
			
		}

	}
	
	public void addInterviewee(){
		log.info("*******addInterviewee**********");
		SmSvTemplate svTemplate = this.getManagedBean().getSelectedSvTemplate();
		

		SmSvInterviewee interviewee = new SmSvInterviewee();
		interviewee.setSmSvTemplate(svTemplate);
		this.getManagedBean().getSelectedSvTemplate().getSmSvInterviewees().add(interviewee);

	}
	
	public void removeInterviewee(){
		log.info("*******removeInterviewee**********");
		if(this.getManagedBean().getSelectedInterviewees() != null && this.getManagedBean().getSelectedInterviewees().length > 0){
			for (SmSvInterviewee interviewee : this.getManagedBean().getSelectedInterviewees()) {
				log.info("---removeSupplierBank---" + interviewee);
				this.getManagedBean().getSelectedSvTemplate().getSmSvInterviewees().remove(interviewee);		
			}
		}
		
		this.getManagedBean().setSelectedInterviewees(null);
	}
	
	private void prepareEquipmentLicenseInsurance(SmSvTemplate svTemplate, String catType){
		String strLicenseIns = null;
		if(catType.equals(SupplierManagementConst.CAT_TYPE_DOCKYARD) || catType.equals(SupplierManagementConst.CAT_TYPE_WORKSHOP)){
			String strEquipMaint = null;
			String strEquipBeating = null;
			strLicenseIns = ParameterTypeConst.DOCKYARD_WORKSHOP_LICENSE_INS;

			if(catType.equals(SupplierManagementConst.CAT_TYPE_DOCKYARD)){
				strEquipMaint = ParameterTypeConst.DOCKYARD_EQUIPMENT_MAINTENANCE;
				strEquipBeating = ParameterTypeConst.DOCKYARD_EQUIPMENT_BEATING;
			}else if(catType.equals(SupplierManagementConst.CAT_TYPE_WORKSHOP)){
				strEquipMaint = ParameterTypeConst.WORKSHOP_EQUIPMENT_MAINTENANCE;
				strEquipBeating = ParameterTypeConst.WORKSHOP_EQUIPMENT_BEATING;
			}
			
			if(svTemplate.getSmSvEquipments().size() > 0){
				svTemplate.setEquipmentMain(new ArrayList<SmSvEquipment>());
				svTemplate.setEquipmentBeating(new ArrayList<SmSvEquipment>());

				for(SmSvEquipment svEquipment : svTemplate.getSmSvEquipments()){
					if(svEquipment.getPmParameter().getParameterType().equals(strEquipMaint)){
						svTemplate.getEquipmentMain().add(svEquipment);
					}else if(svEquipment.getPmParameter().getParameterType().equals(strEquipBeating)){
						svTemplate.getEquipmentBeating().add(svEquipment);
					}
				}
			}else{
				log.info("***Equipment Param Is NUll*****" );
				
				if(svTemplate.getSmSvEquipments().size() < 1){
					List<PmParameterDesc> equipMaintenanceLs = SmCommonUtil.retrieveParameterDescList(strEquipMaint, new FacesScopes().getUserLocale().getLanguage());
					List<PmParameterDesc> equipPanelBeatingLs = SmCommonUtil.retrieveParameterDescList(strEquipBeating, new FacesScopes().getUserLocale().getLanguage());

					svTemplate.setEquipmentMain(new ArrayList<SmSvEquipment>());
					svTemplate.setEquipmentBeating(new ArrayList<SmSvEquipment>());
					
					if(svTemplate.getSmSvEquipments() == null){
						svTemplate.setSmSvEquipments(new ArrayList<SmSvEquipment>());
					}
		
					for(PmParameterDesc equipMainParamDesc : equipMaintenanceLs){
						SmSvEquipment equipMainObj = new SmSvEquipment();
						equipMainObj.setPmParameter(equipMainParamDesc.getPmParameter());
						equipMainObj.setSmSvTemplate(svTemplate);
						//svTemplate.getEquipmentMain().add(equipMainObj);
						svTemplate.getSmSvEquipments().add(equipMainObj);
					}

					for(PmParameterDesc equipPanelParamDesc : equipPanelBeatingLs){
						SmSvEquipment equipBindingObj = new SmSvEquipment();
						equipBindingObj.setPmParameter(equipPanelParamDesc.getPmParameter());
						equipBindingObj.setSmSvTemplate(svTemplate);
						//svTemplate.getEquipmentBeating().add(equipBindingObj);
						svTemplate.getSmSvEquipments().add(equipBindingObj);
					}
					
					if(svTemplate.getSmSvEquipments().size() > 0){
						for(SmSvEquipment svEquipment : svTemplate.getSmSvEquipments()){
							if(svEquipment.getPmParameter().getParameterType().equalsIgnoreCase(strEquipMaint)){
								svTemplate.getEquipmentMain().add(svEquipment);
							}else if(svEquipment.getPmParameter().getParameterType().equalsIgnoreCase(strEquipBeating)){
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
		if(catType.equals(SupplierManagementConst.CAT_TYPE_DOCKYARD) || catType.equals(SupplierManagementConst.CAT_TYPE_WORKSHOP) 
				|| catType.equals(SupplierManagementConst.CAT_TYPE_PRINTING)){
			if(svTemplate.getSmSvLicenseInsurances().size() < 1){
				List<PmParameterDesc> dockyardLicenceInsurance = SmCommonUtil.retrieveParameterDescList(strLicenseIns, new FacesScopes().getUserLocale().getLanguage());
				for(PmParameterDesc dockWorkLicenseIns : dockyardLicenceInsurance){
					SmSvLicenseInsurance svLicenseInsurance = new SmSvLicenseInsurance();
					svLicenseInsurance.setPmParameter(dockWorkLicenseIns.getPmParameter());
					svLicenseInsurance.setSmSvTemplate(svTemplate);
					svTemplate.getSmSvLicenseInsurances().add(svLicenseInsurance);
				}
			}
		}
	}
	
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
	
	public void addWorkshopWorker(){
		log.info("**********addWorkshopWorker*****");
		SmSvTemplate svTemplate = this.getManagedBean().getSelectedSvTemplate();
		List<SmSvWorker> workshopWorkerLs = svTemplate.getSmSvWorkers();
		SmSvWorker worker = new SmSvWorker();
		worker.setSmSvTemplate(svTemplate);
		
		workshopWorkerLs.add(worker);
	}
	
	public void removeWorkshopWorker(){
		//this.getManagedBean().getSelectedInterviewees() != null && 
		log.info("**********removeWorkshopWorker*****" + this.getManagedBean().getSelectedWorkers().length);
		if(this.getManagedBean().getSelectedWorkers().length > 0){
			for (SmSvWorker svWorker : this.getManagedBean().getSelectedWorkers()) {
				log.info("---removeSvWorker---" + svWorker);
				this.getManagedBean().getSelectedSvTemplate().getSmSvWorkers().remove(svWorker);
			}
		}
		this.getManagedBean().setSelectedWorkers(null);
	}
	
	public void addFinancialDoc(){
		log.info("**********addFinancialDoc*****");
		SmSvTemplate svTemplate = this.getManagedBean().getSelectedSvTemplate();
		List<SmSvManufacturingDoc> addFinancialDocLs = svTemplate.getAdditionalDocLs();
		SmSvManufacturingDoc addFinancialDoc = new SmSvManufacturingDoc();
		addFinancialDoc.setSmSvTemplate(svTemplate);
		
		addFinancialDocLs.add(addFinancialDoc);

	}
	
	public void removeFinancialDoc(){
		//l && 
		log.info("**********removeFinancialDoc*****");
		if(this.getManagedBean().getSelectedAddFinancialDoc() != null && this.getManagedBean().getSelectedAddFinancialDoc().length > 0){
			for (SmSvManufacturingDoc addFinDoc : this.getManagedBean().getSelectedAddFinancialDoc()) {
				log.info("---remove FinancialDoc---" + addFinDoc);
				this.getManagedBean().getSelectedSvTemplate().getAdditionalDocLs().remove(addFinDoc);
				this.getManagedBean().getSelectedSvTemplate().getSmSvManufacturingDocs().remove(addFinDoc);
			}
			
			log.info("*********FinancialDoc Size********" + this.getManagedBean().getSelectedSvTemplate().getSmSvManufacturingDocs().size());
		}
	}
	
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	private String formatEquity(BigDecimal equityAmount) {
		DecimalFormat formatter = new DecimalFormat("###,##0.00");
		return formatter.format(equityAmount);
	}
	
	public String calculateEquityPercentage(String equityAmt){
		double eqAmt =  (equityAmt == null || equityAmt == "" ? new Double(0) : new Double(equityAmt));
		double paidUpCapital = this.getManagedBean().getSmSvTask().getSmAppl().getSmCompanyBasic().getPaidUpCapital().doubleValue();
		
		Double percentage = (eqAmt * 100) / paidUpCapital;
		
		DecimalFormat formatter = new DecimalFormat("###,##0.00");
		formatter.setRoundingMode(RoundingMode.HALF_UP);
		return formatter.format(percentage);

	}
	
	public void preRenderView(){
		if(this.getManagedBean().getSmSvTask() == null){
			retrieveSiteVisitInfo("");
		}
	}
	
	public void getTotalBODMgmtWorkerEquityOwner(){
		int ttlBOD = 0, ttlMgmt = 0, ttlWorker = 0, ttlEquityOwner = 0;
		int dirBumi = 0, dirNonBumi = 0, dirNonMalaysian = 0;
		int mgmtBumi = 0, mgmtNonBumi = 0, mgmtNonMalaysian = 0;
		int wrkBumi = 0, wrkNonBumi = 0, wrkNonMalaysian = 0;
		
		this.getManagedBean().setBodLs(new ArrayList<SmPersonnel>());
		this.getManagedBean().setMgmtLs(new ArrayList<SmPersonnel>());
		this.getManagedBean().setEquityOwnerLs(new ArrayList<SmPersonnel>());

		for(SmPersonnel personnel : this.getManagedBean().getSmSvTask().getSmAppl().getSmPersonnels()){
			if(personnel.isDirector()){
				ttlBOD++;
				if(personnel.isBumi()){
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
				if(personnel.isBumi()){
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

				if(personnel.isBumi()){
					wrkBumi++;
				}else {
					if(personnel.getPmCountry().getCountryCode().equalsIgnoreCase(SupplierManagementConst.MALAYSIA_CODE)){
						wrkNonBumi++;
					}else{
						wrkNonMalaysian++;
					}
				}
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
	
	private void chequeSignatoryInfo(){
		log.info("****cheque signatory**" + this.getManagedBean().getSmSvTask().getSmSvBankInfos().size());
		int totalChequeSignatory = 0;
		int cheqBumi = 0,cheqNonBumi = 0, cheqNonMalaysian = 0;
		
		if(this.getManagedBean().getSmSvTask().getSmSvBankInfos() != null){
			for(SmSvBankInfo svBankInfo : this.getManagedBean().getSmSvTask().getSmSvBankInfos()){
				if(svBankInfo.getSmSupplierBank().getSmChequeSignatories().size() > 0){
					for(SmChequeSignatory chequeSignatory : svBankInfo.getSmSupplierBank().getSmChequeSignatories()){
						totalChequeSignatory++;
						if(chequeSignatory.getSmPersonnel().isBumi()){
							cheqBumi++;
						}else{ 
							if(chequeSignatory.getSmPersonnel().getPmCountry().getCountryCode().equalsIgnoreCase(SupplierManagementConst.MALAYSIA_CODE)){
								cheqNonBumi++;
							}else{
								cheqNonMalaysian++;
							}
						}
					}
				}
			}
		}
		
		this.getManagedBean().setTotalCheqSign(totalChequeSignatory);
		this.getManagedBean().setCheqBumi(cheqBumi);
		this.getManagedBean().setCheqNonBumi(cheqNonBumi);
		this.getManagedBean().setCheqNonMalaysian(cheqNonMalaysian);
	}
	
	private List<PmParameterDesc> retrievePrintingAcc(String catLvlCode){
		String strParam = null;

		if(catLvlCode.equals(SupplierManagementConst.P221601)){
			strParam = ParameterTypeConst.PRINTING_221601;
		}else if(catLvlCode.equals(SupplierManagementConst.P221602)){
			strParam = ParameterTypeConst.PRINTING_221602;
		}else if(catLvlCode.equals(SupplierManagementConst.P221603)){
			strParam = ParameterTypeConst.PRINTING_221603;
		}else if(catLvlCode.equals(SupplierManagementConst.P221604)){
			strParam = ParameterTypeConst.PRINTING_221604;
		}else if(catLvlCode.equals(SupplierManagementConst.P221605)){
			strParam = ParameterTypeConst.PRINTING_221605;
		}else if(catLvlCode.equals(SupplierManagementConst.P221607)){
			strParam = ParameterTypeConst.PRINTING_221607;
		}else if(catLvlCode.equals(SupplierManagementConst.P221609)){
			strParam = ParameterTypeConst.PRINTING_221609;
		}else if(catLvlCode.equals(SupplierManagementConst.P221610)){
			strParam = ParameterTypeConst.PRINTING_221610;
		}else if(catLvlCode.equals(SupplierManagementConst.P221611)){
			strParam = ParameterTypeConst.PRINTING_221611;
		}else if(catLvlCode.equals(SupplierManagementConst.P221612)){
			strParam = ParameterTypeConst.PRINTING_221612;
		}else if(catLvlCode.equals(SupplierManagementConst.P221613)){
			strParam = ParameterTypeConst.PRINTING_221613;
		}else if(catLvlCode.equals(SupplierManagementConst.P221614)){
			strParam = ParameterTypeConst.PRINTING_221614;
		}else if(catLvlCode.equals(SupplierManagementConst.P222003)){
			strParam = ParameterTypeConst.PRINTING_222003;
		}

		List<PmParameterDesc> pList = new ArrayList<PmParameterDesc>();
		if(strParam != null){
			pList = SmCommonUtil.retrieveParameterDescList(strParam, new FacesScopes().getUserLocale().getLanguage());
			 log.info("userLocale------>" + new FacesScopes().getUserLocale().getLanguage());
		}
		
		return pList;
	}
	
	public void addPrintWork(){
		SmSvPrintwork printWork = new SmSvPrintwork();
		printWork.setSmSvTemplate(this.getManagedBean().getSelectedSvTemplate());
		
		this.getManagedBean().getSelectedSvTemplate().getSmSvPrintworks().add(printWork);
	}
	
	public void removePrintWork(){
		log.info("---removePrintWork---");
		if(this.getManagedBean().getSelectedPrintWorks().length > 0){
			for (SmSvPrintwork printWork : this.getManagedBean().getSelectedPrintWorks()) {
				this.getManagedBean().getSelectedSvTemplate().getSmSvPrintworks().remove(printWork);
			}
		}
	}
	
	
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
	
	public void viewActiveIndex(int index,String catType){
		this.getManagedBean().setActiveIndex(index);
		if(catType.length() > 0){
			this.getManagedBean().setSelectedSvTemplate(this.getManagedBean().getSmSvTemplateMap().get(catType));
			SmSvTemplate svTemplate = this.getManagedBean().getSelectedSvTemplate();
			
			String catCode = svTemplate.getPmParameter().getParameterCode();
			
			if(svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_DOCKYARD) || 
					svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_WORKSHOP)){

				prepareEquipmentLicenseInsurance(svTemplate, catCode);
			}
			
			if(svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_MANUFACTURING)){
				this.prepareFinancialDoc(svTemplate);
			}
			
			if(svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_PRINTING)){
				//preparePrintingInfoView();
				preparePrintingAcc(svTemplate);
				prepareEquipmentLicenseInsurance(svTemplate, catCode);
			}
			
			
			if(svTemplate.getSmSvMachines() != null){
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
			}
			
			log.info("---------------viewActiveIndex End-----------");
		}
	}
	
	public List<SmSupplierCategory> retrieveActiveCategoryCode(Long applId){
		SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
		ISupplierSearchSO reqSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);
		reqDTO.setApplId(applId);
		
		SupplierSearchResDTO resDTO = reqSO.retrieveSupplierActiveCategoryCodeByApplId(reqDTO);
		
		return resDTO.getSmSupplierCategoryList();
	}
	
	public List<SmSupplierCategory> retrieveAllApprovedCategoryCode(Long applId){
		SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
		ISupplierSearchSO reqSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);
		reqDTO.setApplId(applId);
		
		SupplierSearchResDTO resDTO = reqSO.retrieveCategoryCodeByApplId(reqDTO);
		
		return resDTO.getSmSupplierCategoryList();
	}
	
	public String getsupplierApplTypeDesc(String applType){
		return SmCommonUtil.getApplTypeDesc(applType);
	}
	
	/*public void testTemplate() throws Exception{
		VelocityContext context = new VelocityContext();
		String vmCover = "/sm/sitevisit/sv-main.vm";
		String vmPreDetails = "/sm/sitevisit/sv-predetails.vm";
		String vmCompInfo= "/sm/sitevisit/sv-companyinfo.vm";
		
		String vmDockyard = "/sm/sitevisit/sv-dockyard.vm";
		String cover = DocumentGenerator.getInstance().getHtmlFromVelocity(vmCover, context);
		String preparationDetails = DocumentGenerator.getInstance().getHtmlFromVelocity(vmPreDetails, context);
		String compInfo = DocumentGenerator.getInstance().getHtmlFromVelocity(vmCompInfo, context);
		String dockyard = DocumentGenerator.getInstance().getHtmlFromVelocity(vmDockyard, context);
		String newPathFile = "E:\\HKS\\";
		//String tmpPath = System.getProperty("java.io.tmpdir"); 
		
		File tempFile1 = new File(newPathFile +  "cover.pdf");
		File tempFile2 = new File(newPathFile +  "predetails.pdf");
		File tempFile3 = new File(newPathFile +  "compInfo.pdf");
		File tempFile4 =  new File(newPathFile +  "dockyard.pdf");
		OutputStream os1 = new FileOutputStream(tempFile1);
		OutputStream os2 = new FileOutputStream(tempFile2);
		OutputStream os3 = new FileOutputStream(tempFile3);
		OutputStream os4 = new FileOutputStream(tempFile4);
    	DocumentGenerator.getInstance().generatePdf(os1, cover);
    	DocumentGenerator.getInstance().generatePdf(os2, preparationDetails);
    	DocumentGenerator.getInstance().generatePdf(os3, compInfo);
    	DocumentGenerator.getInstance().generatePdf(os4, dockyard);
	}*/
	
	public void downloadSiteVisitTemplate(String templateType) throws Exception{
		SmSvTask svTask = this.getManagedBean().getSmSvTask();
        
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
		String preDetails = commonPreparationDetails(svTask,templateType);
		String compInfo = commonSiteVisitCompanyInfo(svTask);
		String sectionA = commonSiteVisitSectionA(svTask);
		String sectionB = commonSiteVisitSectionB(svTask);
		String sectionC = commonSiteVisitSectionC(svTask,templateType);
		String sectionD = commonSiteVisitSectionD(svTask,templateType);
		
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
		mergeList.add(new FileInputStream(svCover));
		mergeList.add(new FileInputStream(svPreDetails));
		mergeList.add(new FileInputStream(svCompInfo));
		mergeList.add(new FileInputStream(svSectionA));
		mergeList.add(new FileInputStream(svSectionB));
		mergeList.add(new FileInputStream(svSectionC));
		mergeList.add(new FileInputStream(svSectionD));
		
		if(isSectionE){
			String sectionE = commonSiteVisitSectionE(svTask,templateType);
			svSectionE = new File(sectionE);
			mergeList.add(new FileInputStream(svSectionE));
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
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        
        log.info("**********END downloadSiteVisitTemplate***********");
        if(finalOutputFile.delete()){
        	 log.info("**********Deleted***********");
        }else{
        	 log.info("**********Failed to Delete***********");
        }
	}
	
	private String commonSiteVisitCover(SmSvTask svTask) throws Exception{
		log.info("**********commonSiteVisitCover*************");
		VelocityContext context = new VelocityContext();
		
		log.info(">>>>>>" + svTask.getSvTaskId());
		String lblApplType = SmCommonUtil.getApplTypeDesc(svTask.getSmAppl().getApplType());
		//Locale locale = new Locale("en");
		String applType = ResourceBundleUtils.getMessage(lblApplType,ResourceBundleUtils.BUNDLE_COMMON_LABELS,locale);
		
		context.put("hostUrl", getLogoHeaderPath());
		context.put("companyName", svTask.getSmAppl().getSmSupplier().getCompanyName());
		context.put("applNo", svTask.getSmAppl().getApplNo());
		context.put("applType", applType);
		context.put("templateType",templateDesc);

		String vmCover = "/sm/sitevisit/sv-main.vm";
		String fileName = fileInitName + "cover.pdf";
		
		String file = generateToPdf(vmCover, context, fileName);
		/*File tempFile1 = new File(tmpPath +  "cover.pdf");
		OutputStream os1 = new FileOutputStream(tempFile1);

		DocumentGenerator.getInstance().generatePdfFromVelocity(vmCover, context, os1);*/
		
		return file;

	}
	
	private String commonPreparationDetails(SmSvTask svTask, String templateType) throws Exception{
		log.info("**********commonPreparationDetails*************");
		String vmPreDetails = "/sm/sitevisit/sv-predetails.vm";
//		String strTemplate = "lbl.sm.sitevisit.category.categorycode_" + templateType;
//		Locale locale = new Locale("en");
//		String lblTemplate = ResourceBundleUtils.getMessage(strTemplate,ResourceBundleUtils.BUNDLE_COMMON_LABELS,locale);
		
		VelocityContext context = new VelocityContext();
		
		context.put("companyName", svTask.getSmAppl().getSmSupplier().getCompanyName());	
		context.put("applNo", svTask.getSmAppl().getApplNo());
		context.put("businessRegistrationNo",  svTask.getSmAppl().getSmSupplier().getRegNo());
		context.put("visitingPurpose", templateDesc);
		context.put("catApplicable", this.getManagedBean().getSvTaskCategories());
		context.put("telNo", svTask.getSmAppl().getSmCompanyBasic().getPhoneNo());
		
		if(this.getManagedBean().getContactPerson() != null){
			context.put("contactPersonName", this.getManagedBean().getContactPerson().getName());
			context.put("contactPersonDesignation", this.getManagedBean().getContactPerson().getDesignation());
		}
		
		context.put("smVmUtil", new VmUtil());
		context.put("hostUrl", getLogoHeaderPath());
		context.put("templateType",templateDesc);
		context.put("svAddresses", this.getManagedBean().getSmSvAddressMap().get(templateType));
		context.put("addressFormatter", new AddressUtils());

		String fileName = fileInitName + "predetails.pdf";
		
		String file = generateToPdf(vmPreDetails, context, fileName);
		
		return file;
		
	}
	
	private String commonSiteVisitCompanyInfo(SmSvTask svTask) throws Exception{
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
		context.put("hostUrl", getLogoHeaderPath());
		context.put("accptDate", accptDate);
		context.put("svOfficers", sb.toString());
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
		
		
		String fileName = fileInitName + "compInfo.pdf";
		String file = generateToPdf(vmCompInfo, context, fileName);
		
		return file;
	}
	
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

		// SIR #3108
		context.put("higherManagementNames", this.getManagedBean().getHigherManagementNames());
		context.put("managementNames", this.getManagedBean().getManagementNames());	
		log.info("Business type***************>" + svTask.getSmAppl().getSmSupplier().getBusinessType());
		
		String businessType = svTask.getSmAppl().getSmSupplier().getBusinessType();
		if(businessType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_LLP) 
				|| businessType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_SDN_BHD)
				|| businessType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_BHD)){
			log.info("EquityCompanies-------" +  svTask.getSmAppl().getSmEquityCompanies().size());
			context.put("equityCompanies", svTask.getSmAppl().getSmEquityCompanies());
		}
		
		String fileName = fileInitName + "sectionA.pdf";
		String file = generateToPdf(vmEquity, context, fileName);
		
		return file;
	}
	
	private String commonSiteVisitSectionB(SmSvTask svTask) throws Exception{
		VelocityContext context = new VelocityContext();
		String vmEquity= "/sm/sitevisit/sv-section-b.vm";
		//context.put("escapeTool", new EscapeTool());  
		//#{finLsItem.pmParameter.pmParameterDescs[0].codeName}
		
		List<String> finLsItem = new ArrayList<String>();
		
		for(SmSvFinChecklist finCheckList : svTask.getSmSvFinChecklists()){
			finLsItem.add(finCheckList.getPmParameter().getPmParameterDescs().get(0).getCodeName());
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
		
		context.put("financialCheckList", finLsItem);
		
		context.put("templateType",templateDesc);
		
		String fileName = fileInitName + "sectionB.pdf";
		String file = generateToPdf(vmEquity, context, fileName);
		
		return file;
	}
	
	private String commonSiteVisitSectionC(SmSvTask svTask, String templateType) throws Exception{
		log.info("commonSiteVisitSectionC..");
		
		VelocityContext context = new VelocityContext();
		String vmSectionC = "/sm/sitevisit/sv-section-c.vm";
		
		//context.put("escapeTool", new EscapeTool()); 
		
		if(templateType.equals(SupplierManagementConst.CAT_TYPE_INSURANCE) || templateType.equals(SupplierManagementConst.CAT_TYPE_BUMIPUTERA) || 
				templateType.equals(SupplierManagementConst.CAT_TYPE_READING_MATERIAL_FROM_OVERSEA) || templateType.equals(SupplierManagementConst.CAT_TYPE_TRANSPORTATION) || 
				templateType.equals(templateType.equals(SupplierManagementConst.CAT_TYPE_TRAVEL))){

			List<SmSupplierCategory> catLs = retrieveActiveCategoryCode(svTask.getSmAppl().getSmSupplier().getSmAppl().getApplId());
			context.put("categoryCodeLs", catLs);
			//context.put("templateType",templateDesc);
			//context.put("templateCode", templateType);
			//context.put("templateDesc", value);
		}else if(templateType.equals(SupplierManagementConst.CAT_TYPE_DOCKYARD) || templateType.equals(SupplierManagementConst.CAT_TYPE_WORKSHOP)){
			List<PmParameterDesc> licenseInsuranceLs = SmCommonUtil.retrieveParameterDescList(ParameterTypeConst.DOCKYARD_WORKSHOP_LICENSE_INS, templateLangCode);
//			List<PmParameterDesc>  equipmentMaintLs = new ArrayList<PmParameterDesc>();
//			List<PmParameterDesc>  equipmentBeatingLs = new ArrayList<PmParameterDesc>();
			String eqMaintParam = null;
			String eqBeatingParam = null;
			
			SmFacility smFacility = this.getManagedBean().getSmSvFacilityMap().get(templateType);
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
			
			if(templateType.equals(SupplierManagementConst.CAT_TYPE_DOCKYARD)){
				eqMaintParam = ParameterTypeConst.DOCKYARD_EQUIPMENT_MAINTENANCE;
				eqBeatingParam = ParameterTypeConst.DOCKYARD_EQUIPMENT_BEATING;
			}else{
				eqMaintParam = ParameterTypeConst.WORKSHOP_EQUIPMENT_MAINTENANCE;
				eqBeatingParam = ParameterTypeConst.WORKSHOP_EQUIPMENT_BEATING;
			}
			
			List<PmParameterDesc>  equipmentMaintLs = SmCommonUtil.retrieveParameterDescList(eqMaintParam, templateLangCode);
			List<PmParameterDesc>  equipmentBeatingLs = SmCommonUtil.retrieveParameterDescList(eqBeatingParam, templateLangCode);
			
			context.put("licenseInsurance", licenseInsuranceLs);
			context.put("equipmentMaintenance", equipmentMaintLs);
			context.put("equipmentBeating", equipmentBeatingLs);
			context.put("machineLs", svTask.getSmAppl().getSmMachineDtls());
			
		}else if(templateType.equals(SupplierManagementConst.CAT_TYPE_MANUFACTURING)){
			//SmSvTemplate manufacturingTemplate = null;
			
			List<PmParameterDesc> financialDocLs = SmCommonUtil.retrieveParameterDescList(ParameterTypeConst.MANUFACTURING_FINANCIAL_DOC, templateLangCode);
			
			context.put("financialDocLs", financialDocLs);
			context.put("additionalDocLs", "");
			context.put("machineLs", svTask.getSmAppl().getSmMachineDtls());
			
		}else if(templateType.equals(SupplierManagementConst.CAT_TYPE_PRINTING)){
			List<PmParameterDesc> licenseInsuranceLs = SmCommonUtil.retrieveParameterDescList(ParameterTypeConst.PRINTING_LICENSE_INS, templateLangCode);
			SmFacility smFacility = this.getManagedBean().getSmSvFacilityMap().get(templateType);
			
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
			
			context.put("licenseInsuranceLs", licenseInsuranceLs);
			
			if(this.getManagedBean().getSmSvTask().getSmSvCategories().size() > 0){
				Map<String, List<PmParameterDesc>> printingCatMap = new HashMap<String,List<PmParameterDesc>>();
				for(SmSvCategory svCat : this.getManagedBean().getSmSvTask().getSmSvCategories()){
					String lv1 = svCat.getPmCategoryL1().getCategoryL1Code();
					String lv2 = svCat.getPmCategoryL2().getCategoryL2Code();
					String lv3 = svCat.getPmCategoryL3().getCategoryL3Code();

					String catCodeStr = lv1 + lv2 + lv3;
					log.info("-----Section C - ------" + catCodeStr);
					if(printingCodeLs.contains(catCodeStr)){
						log.info("-----Section C - Contain printing cat------");
						
						List<PmParameterDesc> pmDescList = retrievePrintingAcc(catCodeStr);
						//Locale locale = new Locale("en");
						String lblCategoryCodeDesc= "lbl.sm.sitevisit.printing." + catCodeStr;
						String categoryCodeDesc = ResourceBundleUtils.getMessage(lblCategoryCodeDesc,ResourceBundleUtils.BUNDLE_COMMON_LABELS,locale);
						
						printingCatMap.put(categoryCodeDesc, pmDescList);
					}
				}
				
				context.put("printingCat", printingCatMap);
			}
		}
		
		if(templateType.equals(SupplierManagementConst.CAT_TYPE_WORKSHOP) || 
				templateType.equals(SupplierManagementConst.CAT_TYPE_DOCKYARD) || 
				templateType.equals(SupplierManagementConst.CAT_TYPE_MANUFACTURING)){
			//retrieve Purchase Method && apply puchase method to map
			Map<String, String> purchaseMethodMap = new HashMap<String,String>();
			List<PmParameterDesc> purchaseMethodLs = SmCommonUtil.retrieveParameterDescList(ParameterTypeConst.PURCHASING_METHOD, "en");
			
			if(purchaseMethodLs.size() > 0){
				for(PmParameterDesc pmDesc : purchaseMethodLs){
					purchaseMethodMap.put(pmDesc.getPmParameter().getParameterCode(),pmDesc.getCodeName());
				}
			}
			
			context.put("purchaseMethodMap", purchaseMethodMap);
		}
		
		log.info("****Template Type****" + templateType);
		context.put("smVmUtil", new VmUtil());
		context.put("hostUrl", getLogoHeaderPath());
		context.put("templateType",templateDesc);
		context.put("templateCode", templateType);
		
		String fileName = fileInitName + "sectionC.pdf";
		String file = generateToPdf(vmSectionC, context, fileName);
		return file;
	}
	
	private String commonSiteVisitSectionD(SmSvTask svTask, String templateType) throws Exception{
		VelocityContext context = new VelocityContext();
		String vmSectionD = "/sm/sitevisit/sv-section-d.vm";
		
		//context.put("escapeTool", new EscapeTool()); 
		
		if(templateType.equals(SupplierManagementConst.CAT_TYPE_INSURANCE) || templateType.equals(SupplierManagementConst.CAT_TYPE_BUMIPUTERA) || 
				templateType.equals(SupplierManagementConst.CAT_TYPE_READING_MATERIAL_FROM_OVERSEA) || templateType.equals(SupplierManagementConst.CAT_TYPE_TRANSPORTATION) || 
				templateType.equals(templateType.equals(SupplierManagementConst.CAT_TYPE_TRAVEL))){

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
			
		}else if(templateType.equals(SupplierManagementConst.CAT_TYPE_DOCKYARD) || templateType.equals(SupplierManagementConst.CAT_TYPE_WORKSHOP) || 
				templateType.equals(SupplierManagementConst.CAT_TYPE_MANUFACTURING) || templateType.equals(SupplierManagementConst.CAT_TYPE_PRINTING)){
			List<SmSupplierCategory> catLs = retrieveActiveCategoryCode(svTask.getSmAppl().getSmSupplier().getSmAppl().getApplId());
			context.put("categoryCodeLs", catLs);
		}
		
		context.put("hostUrl", getLogoHeaderPath());
		context.put("templateType",templateDesc);
		context.put("templateCode", templateType);
		
		String fileName = fileInitName + "sectionD.pdf";
		String file = generateToPdf(vmSectionD, context, fileName);
		
		return file;
	}
	
	private String commonSiteVisitSectionE(SmSvTask svTask, String templateType) throws Exception{
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
		context.put("templateCode", templateType);
		context.put("officeOwnership", officeOwnership);
		context.put("officePrice", svOfficePurchasePrice);
		context.put("officeRentalRate", svOfficeRentalRate);
		
		String fileName = fileInitName + "sectionE.pdf";
		String file = generateToPdf(vmSectionD, context, fileName);
		
		return file;
	}
	
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

	public StreamedContent getFile() {
		return file;
	}

	public String getTemplateNameFromLabel(String templateType){
		Locale locale = new FacesScopes().getUserLocale();
		
		String lblTemplate = "lbl.sm.sitevisit.category.categorycode_"+templateType;
		String templDesc = ResourceBundleUtils.getMessage(lblTemplate,ResourceBundleUtils.BUNDLE_COMMON_LABELS,locale);
		return templDesc;
	}
	
	
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
			if (smAddress.getAddress4() != null && smAddress.getAddress4().length() > 0) {
				address.append(smAddress.getAddress4());
			}
					
		}else{
			address.append("");
		}
		return address.toString();
	}
	
	public void submitDialogShow(){
		RequestContext.getCurrentInstance().execute("submissionConfirmDlg.show();");
	}
	
	public void addAdditionalMachine(){
		log.info("-------addAdditionalMachine---------");
		
		//Additional Machine for DOCKTARD, MANUFACTURING and WORKSHOP
		SmSvTemplate svTemplate = this.getManagedBean().getSelectedSvTemplate();
		if(svTemplate.getSmSvAdtMachines() == null){
			svTemplate.setSmSvAdtMachines(new ArrayList<SmSvAdtMachine>());
		}
		SmSvAdtMachine adtMachine = new SmSvAdtMachine();
		adtMachine.setSmSvTemplate(svTemplate);
		
		this.getManagedBean().setSelectedAdtMachine(adtMachine);
		this.getManagedBean().getSelectedAdtMachine().setNewMachine(true);
		//svTemplate.getSmSvAdtMachines().add(new SmSvAdtMachine());
	}
	
	public void removeAdditionalMachine(){
		log.info("---removePrintWork---");
		SmSvTemplate svTemplate = this.getManagedBean().getSelectedSvTemplate();
		if(svTemplate.getSelectedAdtMachines().length > 0){
			for (SmSvAdtMachine adtMachine : svTemplate.getSelectedAdtMachines()) {
				svTemplate.getSmSvAdtMachines().remove(adtMachine);
			}
		}
	}
	
	public void addAdditionalMachineToList(){
		SmSvAdtMachine adtMachine = this.getManagedBean().getSelectedAdtMachine();
		this.getManagedBean().getSelectedSvTemplate().getSmSvAdtMachines().add(adtMachine);
		RequestContext.getCurrentInstance().execute("adtMacDlg.hide()");
		log.info("ADT MACHINE SIZE......." + this.getManagedBean().getSelectedSvTemplate().getSmSvAdtMachines().size());
		
		this.getManagedBean().setSelectedAdtMachine(new SmSvAdtMachine());
	}
	
	public void saveAdditionalMachine(){
		log.info("-------saveAdditionalMachine--------");
		this.getManagedBean().setSelectedAdtMachine(new SmSvAdtMachine());
	}
	
	public void editAdditionalMachine(SmSvAdtMachine adtMachine){
		this.getManagedBean().setSelectedAdtMachine(adtMachine);
		this.getManagedBean().getSelectedAdtMachine().setNewMachine(false);
	}

	public void groupCategoryCodeByType(){
		if(this.getManagedBean().getSmSvTask().getSmSvCategories().size() > 0){
			for(SmSvCategory cat : this.getManagedBean().getSmSvTask().getSmSvCategories()){
				SmSvTemplate template = this.getManagedBean().getSmSvTemplateMap().get(cat.getPmCategoryL3().getCategoryTypeCode().getParameterCode());
				if(template != null){
					if(template.getGroupedCategory() == null){
						template.setGroupedCategory(new ArrayList<SmSvCategory>());
					}
					template.getGroupedCategory().add(cat);
				}
			}
		}
	}
	
	private void preparePrintingAcc(SmSvTemplate svTemplate){
		if(this.getManagedBean().getSmSvTask().getSmSvCategories().size() > 0){
			for(SmSvCategory svCat : this.getManagedBean().getSmSvTask().getSmSvCategories()){
				String lv1 = svCat.getPmCategoryL1().getCategoryL1Code();
				String lv2 = svCat.getPmCategoryL2().getCategoryL2Code();
				String lv3 = svCat.getPmCategoryL3().getCategoryL3Code();
								
				String catCodeStr = lv1 + lv2 + lv3;
				
				if(printingCodeLs.contains(catCodeStr)){
					if(this.getManagedBean().getPrintingCatLs() == null){
						this.getManagedBean().setPrintingCatLs(new ArrayList<String>());
					}

					if(!this.getManagedBean().getPrintingCatLs().contains(catCodeStr)){
						this.getManagedBean().getPrintingCatLs().add(catCodeStr);
						log.info("Printing---->" + catCodeStr);
					}

					if(svCat.getSmSvPrintmachines() == null || svCat.getSmSvPrintmachines().size() < 1){
						List<PmParameterDesc> pmDescList = retrievePrintingAcc(catCodeStr);

						if(svCat.getSmSvPrintmachines() == null){
							svCat.setSmSvPrintmachines(new ArrayList<SmSvPrintmachine>());
						}

						if(pmDescList.size() > 0){
							/*if(this.getManagedBean().getPrintingParamMap() == null){
								this.getManagedBean().setPrintingParamMap(new HashMap<Long, PmParameterDesc>());
							}*/
							
							for(PmParameterDesc pmDesc : pmDescList){
								SmSvPrintmachine svPrintMac = new SmSvPrintmachine();
								svPrintMac.setSmSvTemplate(svTemplate);
								svPrintMac.setSmSvCategory(svCat);
								svPrintMac.setPmParameter(pmDesc.getPmParameter());
								svCat.getSmSvPrintmachines().add(svPrintMac);
							}
						}
					}
					
					if(this.getManagedBean().getPrintingCatMap() == null){
						this.getManagedBean().setPrintingCatMap(new HashMap<String, SmSvCategory>());
					}
					this.getManagedBean().getPrintingCatMap().put(catCodeStr, svCat);	

				}
			}
		}
	}
	
	public String saveSiteVisitReport(String action){
		log.info("***********saveSiteVisitReport***********");
		SiteVisitReqDTO reqDTO = new SiteVisitReqDTO();
		SmSvTask svTask = this.getManagedBean().getSmSvTask();
		
		// To ensure at least 1 address to be selected when action equal to "SUBMIT"
		if(action.equals(SupplierManagementConst.BPM_SUBMIT)){
			for(SmSvTemplate smSvTemplate : svTask.getSmSvTemplates()){
				//smSvTemplate.setSmSvTemplateAddresses(new ArrayList<SmSvTemplateAddress>());
				String tmpDesc = getTemplateNameFromLabel(smSvTemplate.getPmParameter().getParameterCode());

				if(smSvTemplate.getSelectedAddresses() == null){
					log.info("-----svTemplate.getSelectedAddresses IS NULL-----" );
					RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
					throw new EpException(MessageConst.ERROR_721301, new String[]{tmpDesc});
				}

				if(smSvTemplate.getSelectedAddresses().length < 1){
					RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
					throw new EpException(MessageConst.ERROR_721301, new String[]{tmpDesc});
				}
			}
		}
		
		for(SmSvTemplate svTemplate : svTask.getSmSvTemplates()){
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
			}
			
			//to add/ remove svTemplateAddress based on the selectedAddress
			if(svTemplate.getSmSvTemplateAddresses().size() > 0){
				if(svTemplate.getSelectedAddresses().length < 1){
					svTemplate.getSmSvTemplateAddresses().clear();
				}else{
					//convert existing template adddress to map
					Map<Long, SmSvTemplateAddress> templateAddMap = new HashMap<Long,SmSvTemplateAddress>();
					for(SmSvTemplateAddress templateAdd : svTemplate.getSmSvTemplateAddresses()){
						templateAddMap.put(templateAdd.getSmSvAddress().getSvAddressId(), templateAdd);
					}
					
					//comvert selectedAdd to map
					Map<Long, SmSvAddress> selectedAddMap = new HashMap<Long,SmSvAddress>();
					for(SmSvAddress templateAdd : svTemplate.getSelectedAddresses()){
						selectedAddMap.put(templateAdd.getSvAddressId(), templateAdd);
					}
					
					//remove the non selected add from template address
					for(SmSvTemplateAddress templateAdd : svTemplate.getSmSvTemplateAddresses()){
						if(selectedAddMap.get(templateAdd.getSmSvAddress().getSvAddressId()) == null){
							svTemplate.getSmSvTemplateAddresses().remove(templateAdd);
						}
					}
					
					//add the new selected add to template address
					for(SmSvAddress selectedAdd : svTemplate.getSelectedAddresses()){
						if(templateAddMap.get(selectedAdd.getSvAddressId()) == null){
							SmSvTemplateAddress svTempAdd = new SmSvTemplateAddress();
							svTempAdd.setSmSvTemplate(svTemplate);
							svTempAdd.setSmSvAddress(selectedAdd);
							svTemplate.getSmSvTemplateAddresses().add(svTempAdd);
						}
					}
				}
			}else{
				if(svTemplate.getSelectedAddresses() != null && svTemplate.getSelectedAddresses().length > 0){
					for(SmSvAddress selectedAdd : svTemplate.getSelectedAddresses()){
						SmSvTemplateAddress svTempAdd = new SmSvTemplateAddress();
						svTempAdd.setSmSvTemplate(svTemplate);
						svTempAdd.setSmSvAddress(selectedAdd);
						svTemplate.getSmSvTemplateAddresses().add(svTempAdd);
					}
				}
			}
		}
		
		//if Action equals to SUBMIT, set the BPM Info
		if(action.equals(SupplierManagementConst.BPM_SUBMIT)){
			String token = getFacesScopes().getBPMAuthenticationCtxToken();
			reqDTO.setAppId(this.getManagedBean().getAppId());
			reqDTO.setToken(token);
			reqDTO.setOutcome(SupplierManagementConst.BPM_SUBMIT);
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setBpmPojo(this.getManagedBean().getBpmPojo());
			reqDTO.setTrackingDairyVO(SmCommonUtil.assignTrackingDiaryValues(SupplierManagementConst.SVO_SUBMIT_REPORT, 
					this.getManagedBean().getSmSvTask().getSmAppl().getApplId(), this.getManagedBean().getSmSvTask().getSmAppl().getApplNo(), getUserRole()));
		}
		
		reqDTO.setSmSvTask(svTask);
		ISiteVisitSO reqSO = (ISiteVisitSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISiteVisitSO.class);
		SiteVisitResDTO resDTO = reqSO.saveOrSubmitSiteVisitReport(reqDTO);
		
		FacesContext fc = FacesContext.getCurrentInstance();
		if (resDTO.isSuccessful()) {
			
			if(action.equalsIgnoreCase(SupplierManagementConst.BPM_SUBMIT)){
				fc.addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,MessageUtils.getMessage(MessageConst.INFO_721210,
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
			return "";
		}
	}
	
	private String getUserRole() {	
		FacesScopes fs = new FacesScopes();
		if(fs.hasRoleUser(RoleCode.UPS_SVO.value()))
			return RoleCode.UPS_SVO.value();		
		else 
			return null;
	}
	
	public void svEquipmentOnChange(SmSvEquipment svEquipment){
		//if equipment is unchecked, set the qty to null
		if(!svEquipment.isEquipmentAvailable()){
			svEquipment.setEquipmentQty(null);
			refreshPage();
		}
	}
	
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
	
	public String displayAddress(SmAddress svAddress){
		return AddressUtils.getAddress(svAddress);
	}

	private void refreshPage() {
		FacesContext fc = FacesContext.getCurrentInstance();
		String refreshpage = fc.getViewRoot().getViewId();
		ViewHandler ViewH =fc.getApplication().getViewHandler();
		UIViewRoot UIV = ViewH.createView(fc,refreshpage);
		UIV.setViewId(refreshpage);
		fc.setViewRoot(UIV);
	}
	
	private String getLogoHeaderPath(){
		StringBuilder path = new StringBuilder();
		FacesScopes fs = new FacesScopes();
		
		//path.append("http://");
		path.append(fs.getRequest().getServerName());
		
		if(fs.getRequest().getServerPort() > 0){
			path.append(":");
			path.append(fs.getRequest().getServerPort());
		}		
		
		log.info("********HOST URL*********" +  path.toString());
		
		return path.toString();
	}
}
