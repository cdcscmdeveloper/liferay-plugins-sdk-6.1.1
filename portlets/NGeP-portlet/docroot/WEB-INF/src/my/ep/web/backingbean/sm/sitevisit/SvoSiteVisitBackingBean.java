package my.ep.web.backingbean.sm.sitevisit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.sm.remarks.ISmRemarksSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.biz.service.sm.siteVisit.ISiteVisitSO;
import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.dto.sm.siteVisit.SiteVisitReqDTO;
import my.ep.dto.sm.siteVisit.SiteVisitResDTO;
import my.ep.dto.sm.smRemarks.SmRemaksResDTO;
import my.ep.dto.sm.smRemarks.SmRemarksReqDTO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sm.SmChequeSignatory;
import my.ep.persistence.entity.sm.SmMachineDtl;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmRemark;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSvAddress;
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
import my.ep.persistence.entity.sm.SmSvPrintmachine;
import my.ep.persistence.entity.sm.SmSvPrintwork;
import my.ep.persistence.entity.sm.SmSvTask;
import my.ep.persistence.entity.sm.SmSvTemplate;
import my.ep.persistence.entity.sm.SmSvTemplateAddress;
import my.ep.persistence.entity.sm.SmSvWorker;
import my.ep.persistence.logging.LogKey;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.sitevisit.SvoSiteVisitManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmCommonUtil;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "svoSiteVisitBackingBean")
@RequestScoped
public class SvoSiteVisitBackingBean extends BaseBackingBean<SvoSiteVisitManagedBean> {
	public static Logger log = Logger.getLogger(SvoSiteVisitManagedBean.class);
//	public static String NaviToView = "siteVisitDetails?faces-redirect=true";
	public static String NaviToSiteVisitTemplate = "submitOnlineRpt";
	public static String NaviToSiteVisitPreDetails = "submitOnlineRptPreDetails";
	public static String NaviToSiteVisitView = "siteVisitView";
	private static List<String> printingCodeLs = Arrays.asList(SupplierManagementConst.P221601, SupplierManagementConst.P221602, 
			SupplierManagementConst.P221603, SupplierManagementConst.P221604, SupplierManagementConst.P221605,  
			SupplierManagementConst.P221607, SupplierManagementConst.P221609,SupplierManagementConst.P221610,
			SupplierManagementConst.P221611,SupplierManagementConst.P221612,SupplierManagementConst.P221613,SupplierManagementConst.P221614,
			SupplierManagementConst.P222003);

	public SvoSiteVisitBackingBean() {
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		if (pmUser != null && pmUser.getUserId() != null) {
			System.out.println("***userId: " + pmUser.getUserId());

			new LogKey(pmUser.getUserId()); 
		}else{
			new LogKey(1L); 
		}
	}
	
	public void retrieveSiteVisitInfo(String viewMode){
		this.getManagedBean().setAppId(1009);
		System.out.println("--------retrieveSiteVisitInfo--------" + this.getManagedBean().getAppId());
		SiteVisitReqDTO reqDTO = new SiteVisitReqDTO();
		//reqDTO.setAppId(1009);
		reqDTO.setAppId(this.getManagedBean().getAppId());
		ISiteVisitSO reqSO = (ISiteVisitSO) BusinessDelegate.getInstance().getServiceObject("sm", ISiteVisitSO.class);
		SiteVisitResDTO resDTO = reqSO.retrieveSiteVisitInfoByAppId(reqDTO);

		if(resDTO.getSmSvTask() != null){
			this.getManagedBean().setSmSvTask(resDTO.getSmSvTask());
			SmSvTask smSvTask = this.getManagedBean().getSmSvTask();
			//		this.getManagedBean().getSmSvTask().getsm
			System.out.println("*****this.getManagedBean().getSmSvTask().getSmSvFinChecklists().size()" + smSvTask.getSmSvFinChecklists().size());
			if(smSvTask.getSmSvFinChecklists().size() < 1){
				System.out.println("******getSmSvFinChecklists() == null******");
				List<PmParameterDesc> pmParamDesc = new ArrayList<PmParameterDesc>();
				this.getManagedBean().setRemarkMap(new HashMap<Long,SmRemark>());
				pmParamDesc = SmCommonUtil.retrieveParameterDescList(SupplierManagementConst.SVO_FIN_CHECKLIST, new FacesScopes().getUserLocale().getLanguage());
				for(PmParameterDesc pmdesc : pmParamDesc){
					SmSvFinChecklist svCL = new SmSvFinChecklist();
					svCL.setPmParameter(pmdesc.getPmParameter());
					svCL.setSmSvTask(smSvTask);

					pmdesc.getPmParameter().setPmParameterDescs(new ArrayList<PmParameterDesc>());
					pmdesc.getPmParameter().getPmParameterDescs().add(pmdesc);

					SmRemark remarkObj = new SmRemark();
					remarkObj.setDocType(SupplierManagementConst.SVO_FIN_CHECKLIST);
					this.getManagedBean().getRemarkMap().put(pmdesc.getPmParameter().getParameterId(), remarkObj);

					svCL.setSmSvTask(smSvTask);
					smSvTask.getSmSvFinChecklists().add(svCL);
				}
			}else{
				System.out.println("****FIN LIST MORE THAN 1******" + smSvTask.getSmSvFinChecklists().size());
				this.getManagedBean().setRemarkMap(new HashMap<Long,SmRemark>());
				for(SmSvFinChecklist finCheckList : smSvTask.getSmSvFinChecklists()){
					SmRemark remark = retrieveSmRemark(SupplierManagementConst.SVO_FIN_CHECKLIST,finCheckList.getSvFinChecklistId());

					if(remark == null){
						System.out.println("***Remark is null");
						remark = new SmRemark();
						remark.setDocType(SupplierManagementConst.SVO_FIN_CHECKLIST);
					}

					this.getManagedBean().getRemarkMap().put(finCheckList.getPmParameter().getParameterId(),remark); 
				}
			}

			if(smSvTask.getSmSvBankInfos().size() < 1){
				System.out.println("######### SmSvBankInfo #####");
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
			int i = viewMode.equals("V") ? 1 : 4;
			for (Map.Entry<String, SmSvTemplate> entry : this.getManagedBean().getSmSvTemplateMap().entrySet()) {
				this.getManagedBean().getIndexPageMap().put(i, entry.getKey());
				i++;
			}
			
			if(!viewMode.equals("V")){
				this.getManagedBean().setLastIndex(i);
			}
			
			getTotalBODMgmtWorker();
			chequeSignatoryInfo();
			//getAllApprovedCategoryCode();
			retrieveActiveCategoryCode(smSvTask.getSmAppl().getSmSupplier().getSmAppl().getApplId());

			System.out.println("*********categories**" +this.getManagedBean().getSmSvTask().getSmSvCategories().size());
		}
	}
	
	public void rptPreview(String catType){
		System.out.println("-------rpt--------" + catType);
		this.getManagedBean().setSelectedSvTemplate(this.getManagedBean().getSmSvTemplateMap().get(catType));
	}

	/*public String submitSiteVisitReport(){
		log.info("----------------------submitSiteVisitReport start----------------");
		SiteVisitReqDTO reqDTO = new SiteVisitReqDTO();
		ISiteVisitSO reqSO = (ISiteVisitSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", ISiteVisitSO.class);
		String token = getFacesScopes().getBPMAuthenticationCtxToken();
		reqDTO.setAppId(this.getManagedBean().getAppId());
		reqDTO.setToken(token);
		reqDTO.setOutcome(SupplierManagementConst.BPM_SUBMIT);
		reqDTO.setTaskId(this.getManagedBean().getTaskId());
		reqDTO.setBpmPojo(this.getManagedBean().getBpmPojo());
		
		reqDTO.setUserRole(SupplierManagementConst.SVO_HQ);
		reqDTO.setSmSvTask(this.getManagedBean().getSmSvTask());
		reqDTO.setSmRemarkMap(this.getManagedBean().getRemarkMap());

		SiteVisitResDTO resDTO = reqSO.submitSiteVisitReport(reqDTO);
		
		if (resDTO.isSuccessful()) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Site Visit Report has been submitted.", ""));
			return "/xhtml/sm/sitevisit/outputMsg";
		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ""));
			return "";
		}
		
	}*/
	
	public void changeActiveIndex(int index,String catType){
		System.out.println("*******changeActiveIndex********" + index);
		this.getManagedBean().setActiveIndex(index);
		//System.out.println("*******Category pass********" + this.getManagedBean().getSmSvTask().getSmSvCategories().get(0).getPassed()+"****"+ this.getManagedBean().getSmSvTask().getSmSvCategories().get(0).getPmCategoryL3().getCategoryL3Code());
		//System.out.println("*******changeActiveIndex CatType********" + catType.length());
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
			
//			if(svTemplate.getSmSvFacility() == null){
//				svTemplate.setSmSvFacility(new SmSvFacility());
//			}
			
			if(svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_DOCKYARD) || 
					svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_WORKSHOP) ||
					svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_PRINTING)){
				if(svTemplate.getSmSvFacility() == null){
					log.info("**********dsfdsfdsfsdfdsf*******");
					svTemplate.setSmSvFacility(new SmSvFacility());
				}
				svTemplate.getSmSvFacility().setSmSvTemplate(svTemplate);
			}
			
			if(svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_DOCKYARD)){
				this.prepareEquipmentLicenseInsurance(svTemplate, catCode);
				
			}else if(svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_WORKSHOP)){
				this.prepareEquipmentLicenseInsurance(svTemplate, catCode);
				
				if(svTemplate.getSmSvWorkers() == null){
					System.out.println(">>>>>svTemplate.getSmSvWorkers() IS NULL");
					svTemplate.setSmSvWorkers(new ArrayList<SmSvWorker>()); 
				}
			}else if(svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_MANUFACTURING)){
				if(svTemplate.getFinancialDocLs() == null){
					System.out.println(">>>>>FinancialDocLs IS NULL");
					svTemplate.setFinancialDocLs(new ArrayList<SmSvManufacturingDoc>());
				}
				
				if(svTemplate.getAdditionalDocLs() == null){
					svTemplate.setAdditionalDocLs(new ArrayList<SmSvManufacturingDoc>());
				}
				
				if(svTemplate.getFinancialDocParamMap() == null){
					svTemplate.setFinancialDocParamMap(new HashMap<Long,PmParameterDesc>());
					this.prepareFinancialDoc(svTemplate);
				}
				
				System.out.println("-----FIN LS SIZE----" + svTemplate.getFinancialDocLs().size());
				
			}else if(svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_PRINTING)){
/*				if(svTemplate.getSmSvFacility() == null){
					svTemplate.setSmSvFacility(new SmSvFacility());
				}*/
				
				if(svTemplate.getSmSvPrintworks() == null){
					svTemplate.setSmSvPrintworks(new ArrayList<SmSvPrintwork>());				
				}
				
				if(svTemplate.getSmSvLicenseInsurances() == null){
					svTemplate.setSmSvLicenseInsurances(new ArrayList<SmSvLicenseInsurance>());
				}
				
				if(svTemplate.getSmSvLicenseInsurances().size() < 1){
					prepareEquipmentLicenseInsurance(svTemplate,catCode);
				}
				
				//svTemplate.getSmSvFacility().setSmSvTemplate(svTemplate);
				
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
								System.out.println("Printing---->" + catCodeStr);
							}
							/*
							if(this.getManagedBean().getSmSvTask().getSmSvCategories() == null){
								this.getManagedBean().getSmSvTask().setSmSvCategories(new ArrayList<SmSvCategory>());
							}*/

							if(svCat.getSmSvPrintmachines() == null || svCat.getSmSvPrintmachines().size() < 1){
								List<PmParameterDesc> pmDescList = retrievePrintingAcc(catCodeStr);

								if(svCat.getSmSvPrintmachines() == null){
									svCat.setSmSvPrintmachines(new ArrayList<SmSvPrintmachine>());
								}

								if(pmDescList.size() > 0){
									if(this.getManagedBean().getPrintingParamMap() == null){
										this.getManagedBean().setPrintingParamMap(new HashMap<Long, PmParameterDesc>());
									}
									
									for(PmParameterDesc pmDesc : pmDescList){
										SmSvPrintmachine svPrintMac = new SmSvPrintmachine();
										svPrintMac.setSmSvTemplate(svTemplate);
										svPrintMac.setSmSvCategory(svCat);
										svPrintMac.setPmParameter(pmDesc.getPmParameter());
										svCat.getSmSvPrintmachines().add(svPrintMac);
										if(!this.getManagedBean().getPrintingParamMap().containsKey(pmDesc.getPmParameter().getParameterId())){
											this.getManagedBean().getPrintingParamMap().put(pmDesc.getPmParameter().getParameterId(), pmDesc);
											log.info("------this.getManagedBean().getPrintingParamMap()----" + this.getManagedBean().getPrintingParamMap().size());
										}
										
										log.info("printing pmdesc->>>>>>>" + pmDesc.getCodeName());
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
			
			//log.info("printingParamMap>>>>>>" + this.getManagedBean().getPrintingParamMap().size());
		}
//		System.out.println("**********SvAddress Selected SV*********" + this.getManagedBean().getSelectedSvTemplate().getSvTemplateId());
	}
	
	public void addInterviewee(){
		System.out.println("*******addInterviewee**********");
		SmSvTemplate svTemplate = this.getManagedBean().getSelectedSvTemplate();
		

		SmSvInterviewee interviewee = new SmSvInterviewee();
		interviewee.setSmSvTemplate(svTemplate);
		this.getManagedBean().getSelectedSvTemplate().getSmSvInterviewees().add(interviewee);

	}
	
	public void removeInterviewee(){
		System.out.println("*******removeInterviewee**********");
		if(this.getManagedBean().getSelectedInterviewees() != null && this.getManagedBean().getSelectedInterviewees().length > 0){
			for (SmSvInterviewee interviewee : this.getManagedBean().getSelectedInterviewees()) {
				log.info("---removeSupplierBank---" + interviewee);
				this.getManagedBean().getSelectedSvTemplate().getSmSvInterviewees().remove(interviewee);		
			}
		}
		
		this.getManagedBean().setSelectedInterviewees(null);
	}
	
	private void prepareEquipmentLicenseInsurance(SmSvTemplate svTemplate, String catType){
		if(catType.equals(SupplierManagementConst.CAT_TYPE_DOCKYARD) || catType.equals(SupplierManagementConst.CAT_TYPE_WORKSHOP)){
			String strEquipMaint = null;
			String strEquipBeating = null;
			String strLicenseIns = ParameterTypeConst.DOCKYARD_WORKSHOP_LICENSE_INS;

			if(catType.equals(SupplierManagementConst.CAT_TYPE_DOCKYARD)){
				strEquipMaint = ParameterTypeConst.DOCKYARD_EQUIPMENT_MAINTENANCE;
				strEquipBeating = ParameterTypeConst.DOCKYARD_EQUIPMENT_BEATING;
			}else if(catType.equals(SupplierManagementConst.CAT_TYPE_WORKSHOP)){
				strEquipMaint = ParameterTypeConst.WORKSHOP_EQUIPMENT_MAINTENANCE;
				strEquipBeating = ParameterTypeConst.WORKSHOP_EQUIPMENT_BEATING;
			}
			
			if(svTemplate.getEquipmentParamMap() == null){
				System.out.println("***Equipment Param Is NUll*****" );
				List<PmParameterDesc> equipMaintenanceLs = SmCommonUtil.retrieveParameterDescList(strEquipMaint, new FacesScopes().getUserLocale().getLanguage());
				List<PmParameterDesc> equipPanelBeatingLs = SmCommonUtil.retrieveParameterDescList(strEquipBeating, new FacesScopes().getUserLocale().getLanguage());

				svTemplate.setEquipmentMain(new ArrayList<SmSvEquipment>());
				svTemplate.setEquipmentBeating(new ArrayList<SmSvEquipment>());
				svTemplate.setEquipmentParamMap(new HashMap<Long,PmParameterDesc>());

				for(PmParameterDesc equipMainParamDesc : equipMaintenanceLs){
					SmSvEquipment equipMainObj = new SmSvEquipment();
					equipMainObj.setPmParameter(equipMainParamDesc.getPmParameter());
					equipMainObj.setSmSvTemplate(svTemplate);
					System.out.println("Equipment Maintenance>>>>>" + equipMainParamDesc.getCodeName());
					svTemplate.getEquipmentMain().add(equipMainObj);
					svTemplate.getEquipmentParamMap().put(equipMainParamDesc.getPmParameter().getParameterId(), equipMainParamDesc);
				}

				for(PmParameterDesc equipPanelParamDesc : equipPanelBeatingLs){
					SmSvEquipment equipBindingObj = new SmSvEquipment();
					equipBindingObj.setPmParameter(equipPanelParamDesc.getPmParameter());
					equipBindingObj.setSmSvTemplate(svTemplate);
					System.out.println("Equipment Beating>>>>>" + equipPanelParamDesc.getCodeName());
					svTemplate.getEquipmentBeating().add(equipBindingObj);
					svTemplate.getEquipmentParamMap().put(equipPanelParamDesc.getPmParameter().getParameterId(), equipPanelParamDesc);
				}

				List<PmParameterDesc> dockyardLicenceInsurance = SmCommonUtil.retrieveParameterDescList(strLicenseIns, new FacesScopes().getUserLocale().getLanguage());
				svTemplate.setLicenseInsuranceParamMap(new HashMap<Long, PmParameterDesc>());
				for(PmParameterDesc dockWorkLicenseIns : dockyardLicenceInsurance){
					SmSvLicenseInsurance svLicenseInsurance = new SmSvLicenseInsurance();
					svLicenseInsurance.setPmParameter(dockWorkLicenseIns.getPmParameter());
					svLicenseInsurance.setSmSvTemplate(svTemplate);
					svTemplate.getSmSvLicenseInsurances().add(svLicenseInsurance);

					svTemplate.getLicenseInsuranceParamMap().put(dockWorkLicenseIns.getPmParameter().getParameterId(), dockWorkLicenseIns);
				}
			}
		}else if(catType.equals(SupplierManagementConst.CAT_TYPE_PRINTING)){
			if(svTemplate.getLicenseInsuranceParamMap() == null){
				List<PmParameterDesc> printingLicenceInsurance = SmCommonUtil.retrieveParameterDescList(ParameterTypeConst.PRINTING_LICENSE_INS, new FacesScopes().getUserLocale().getLanguage());
				svTemplate.setLicenseInsuranceParamMap(new HashMap<Long, PmParameterDesc>());
				for(PmParameterDesc printingLicenseIns : printingLicenceInsurance){
					SmSvLicenseInsurance svLicenseInsurance = new SmSvLicenseInsurance();
					svLicenseInsurance.setPmParameter(printingLicenseIns.getPmParameter());
					svLicenseInsurance.setSmSvTemplate(svTemplate);
					svTemplate.getSmSvLicenseInsurances().add(svLicenseInsurance);

					svTemplate.getLicenseInsuranceParamMap().put(printingLicenseIns.getPmParameter().getParameterId(), printingLicenseIns);
				}
			}
		}
	}
	
	private void prepareFinancialDoc(SmSvTemplate svTemplate){
		List<PmParameterDesc> financialDocLs = SmCommonUtil.retrieveParameterDescList(ParameterTypeConst.MANUFACTURING_FINANCIAL_DOC, new FacesScopes().getUserLocale().getLanguage());
		
		for(PmParameterDesc financeParamDesc : financialDocLs){
			//System.out.println("Financial Doc>>>>>" + financeParamDesc.getCodeName());
			SmSvManufacturingDoc financeDoc = new SmSvManufacturingDoc();
			financeDoc.setPmParameter(financeParamDesc.getPmParameter());
			financeDoc.setSmSvTemplate(svTemplate);
			
			svTemplate.getFinancialDocLs().add(financeDoc);
			svTemplate.getFinancialDocParamMap().put(financeParamDesc.getPmParameter().getParameterId(), financeParamDesc);
		}
	}	
	
	public void addWorkshopWorker(){
		System.out.println("**********addWorkshopWorker*****");
		SmSvTemplate svTemplate = this.getManagedBean().getSelectedSvTemplate();
		List<SmSvWorker> workshopWorkerLs = svTemplate.getSmSvWorkers();
		SmSvWorker worker = new SmSvWorker();
		worker.setSmSvTemplate(svTemplate);
		
		workshopWorkerLs.add(worker);
	}
	
	public void removeWorkshopWorker(){
		//this.getManagedBean().getSelectedInterviewees() != null && 
		System.out.println("**********removeWorkshopWorker*****" + this.getManagedBean().getSelectedWorkers().length);
		if(this.getManagedBean().getSelectedWorkers().length > 0){
			for (SmSvWorker svWorker : this.getManagedBean().getSelectedWorkers()) {
				log.info("---removeSvWorker---" + svWorker);
				this.getManagedBean().getSelectedSvTemplate().getSmSvWorkers().remove(svWorker);
			}
		}
		
		this.getManagedBean().setSelectedWorkers(null);
	}
	
	public void addFinancialDoc(){
		System.out.println("**********addFinancialDoc*****");
		SmSvTemplate svTemplate = this.getManagedBean().getSelectedSvTemplate();
		List<SmSvManufacturingDoc> addFinancialDocLs = svTemplate.getAdditionalDocLs();
		SmSvManufacturingDoc addFinancialDoc = new SmSvManufacturingDoc();
		addFinancialDoc.setSmSvTemplate(svTemplate);
		
		addFinancialDocLs.add(addFinancialDoc);

	}
	
	public void removeFinancialDoc(){
		//l && 
		System.out.println("**********removeFinancialDoc*****");
		if(this.getManagedBean().getSelectedAddFinancialDoc() != null && this.getManagedBean().getSelectedAddFinancialDoc().length > 0){
			for (SmSvManufacturingDoc addFinDoc : this.getManagedBean().getSelectedAddFinancialDoc()) {
				log.info("---remove FinancialDoc---" + addFinDoc);
				this.getManagedBean().getSelectedSvTemplate().getAdditionalDocLs().remove(addFinDoc);
			}
		}
	}
	
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	public String submitSiteVisitReport(){
		SiteVisitReqDTO reqDTO = new SiteVisitReqDTO();
		
		SmSvTask svTask = this.getManagedBean().getSmSvTask();
		for(SmSvTemplate svTemplate : svTask.getSmSvTemplates()){
			if(svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_DOCKYARD)){

				svTemplate.setSmSvMachines(new ArrayList<SmSvMachine>());
				svTemplate.setSmSvEquipments(new ArrayList<SmSvEquipment>());
				
				if(svTemplate.getEquipmentMain() != null){
					svTemplate.getSmSvEquipments().addAll(svTemplate.getEquipmentMain());
				}

				if(svTemplate.getEquipmentBeating() != null){
					svTemplate.getSmSvEquipments().addAll(svTemplate.getEquipmentBeating());
				}

			}else if(svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_WORKSHOP)){
				svTemplate.setSmSvMachines(new ArrayList<SmSvMachine>());
				svTemplate.setSmSvEquipments(new ArrayList<SmSvEquipment>());

				if(svTemplate.getEquipmentMain() != null){
					svTemplate.getSmSvEquipments().addAll(svTemplate.getEquipmentMain());
				}

				if(svTemplate.getEquipmentBeating() != null){
					svTemplate.getSmSvEquipments().addAll(svTemplate.getEquipmentBeating());
				}
				
			}else if(svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_MANUFACTURING)){
				svTemplate.setSmSvManufacturingDocs(new ArrayList<SmSvManufacturingDoc>());
				
				if(svTemplate.getFinancialDocLs() != null){
					svTemplate.getSmSvManufacturingDocs().addAll(svTemplate.getFinancialDocLs());
				}
				
				if(svTemplate.getAdditionalDocLs() != null){
					svTemplate.getSmSvManufacturingDocs().addAll(svTemplate.getAdditionalDocLs());
				}
				
				if(svTemplate.getEquipmentMain() != null){
					svTemplate.getSmSvEquipments().addAll(svTemplate.getEquipmentMain());
				}

				if(svTemplate.getEquipmentBeating() != null){
					svTemplate.getSmSvEquipments().addAll(svTemplate.getEquipmentBeating());
				}
			}
			
			if(svTemplate.getSmSvTemplateAddresses() == null){
				svTemplate.setSmSvTemplateAddresses(new ArrayList<SmSvTemplateAddress>());
			}
			
			if(svTemplate.getSelectedAddresses() != null && svTemplate.getSelectedAddresses().length > 0){
				for(SmSvAddress selectedAdd : svTemplate.getSelectedAddresses()){
					SmSvTemplateAddress svTempAdd = new SmSvTemplateAddress();
					svTempAdd.setSmSvTemplate(svTemplate);
					svTempAdd.setSmSvAddress(selectedAdd);
					svTemplate.getSmSvTemplateAddresses().add(svTempAdd);
				}
			}
			
			if(svTemplate.getSelectedSmMachine() != null){
				for(SmMachineDtl smMachineDtl : svTemplate.getSelectedSmMachine()){
					System.out.println("-------smMachineDtl()---------" + smMachineDtl.getBrandName());
					SmSvMachine svMachine = new SmSvMachine();
					svMachine.setSmSvTemplate(svTemplate);
					svMachine.setSmMachineDtl(smMachineDtl);
					svMachine.setValid(true);
					svTemplate.getSmSvMachines().add(svMachine);
				}
			}
		}
		
		String token = getFacesScopes().getBPMAuthenticationCtxToken();
		reqDTO.setAppId(this.getManagedBean().getAppId());
		reqDTO.setToken(token);
		reqDTO.setOutcome(SupplierManagementConst.BPM_SUBMIT);
		reqDTO.setTaskId(this.getManagedBean().getTaskId());
		reqDTO.setBpmPojo(this.getManagedBean().getBpmPojo());
		
		reqDTO.setUserRole(RoleCode.UPS_SVO.value());
		reqDTO.setSmSvTask(this.getManagedBean().getSmSvTask());
		reqDTO.setSmRemarkMap(this.getManagedBean().getRemarkMap());
		ISiteVisitSO reqSO = (ISiteVisitSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", ISiteVisitSO.class);
		SiteVisitResDTO resDTO = reqSO.submitSiteVisitReport(reqDTO);
		
		FacesContext fc = FacesContext.getCurrentInstance();
		if (resDTO.isSuccessful()) {
			renewSesionObject();
			fc.addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,MessageUtils.getMessage(MessageConst.INFO_721210,
					MessageUtils.BUNDLE_COMMON_MESSAGES,
					new FacesScopes().getUserLocale())));
			RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
			return "/xhtml/sm/sitevisit/outputMsg";
		} else {
			RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
			fc.addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR,null,MessageUtils.getMessage(MessageConst.EP_MAJ_00001,
					MessageUtils.BUNDLE_COMMON_MESSAGES,
					new FacesScopes().getUserLocale())));
			return "";
		}
	}
	
	public BigDecimal calculateEquityPercentage(String equityAmt){
		BigDecimal eqAmt =  (BigDecimal)(equityAmt == null || equityAmt == "" ? new BigDecimal(0) : new BigDecimal(equityAmt));
		BigDecimal paidUpCapital = this.getManagedBean().getSmSvTask().getSmAppl().getSmCompanyBasic().getPaidUpCapital();
		//eqAmt.divide(paidUpCapital, 3, RoundingMode.CEILING);
//		BigDecimal eqPercentage = eqAmt.divide(paidUpCapital, 3, RoundingMode.CEILING).multiply();
		BigDecimal percentage = eqAmt.divide(paidUpCapital, 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100));

		return percentage;
	}
	
	public void preRenderView(){
		if(this.getManagedBean().getSmSvTask() == null){
			retrieveSiteVisitInfo("");
		}
	}
	
	public void getTotalBODMgmtWorker(){
		int ttlBOD = 0, ttlMgmt = 0, ttlWorker = 0;
		int dirBumi = 0, dirNonBumi = 0, dirNonMalaysian = 0;
		int mgmtBumi = 0, mgmtNonBumi = 0, mgmtNonMalaysian = 0;
		int wrkBumi = 0, wrkNonBumi = 0, wrkNonMalaysian = 0;
		
		for(SmPersonnel personnel : this.getManagedBean().getSmSvTask().getSmAppl().getSmPersonnels()){
			if(personnel.isDirector()){
				ttlBOD++;
				if(personnel.isBumi()){
					dirBumi++;
				}else if(!personnel.isBumi() && personnel.getPmCountry().getCountryCode().equalsIgnoreCase(SupplierManagementConst.MALAYSIA_CODE)){
					dirNonBumi++;
				}
				
				if(!personnel.getPmCountry().getCountryCode().equalsIgnoreCase(SupplierManagementConst.MALAYSIA_CODE)){
					dirNonMalaysian++;
				}
				
				if(this.getManagedBean().getBodLs() == null){
					this.getManagedBean().setBodLs(new ArrayList<SmPersonnel>());
				}
				
				this.getManagedBean().getBodLs().add(personnel);
			}

			if(personnel.getPmStaffRole() != null){
				if(personnel.getPmStaffRole().getParameterCode().equals(SupplierManagementConst.PERSONNEL_MANAGEMENT_STAFF_CONSULTANT_PARAMCODE) || 
						personnel.getPmStaffRole().getParameterCode().equals(SupplierManagementConst.PERSONNEL_MANAGEMENT_STAFF_CONTRACTOR_PARAMCODE)){
					ttlMgmt++;
					if(personnel.isBumi()){
						mgmtBumi++;
					}else if(!personnel.isBumi() && personnel.getPmCountry().getCountryCode().equalsIgnoreCase(SupplierManagementConst.MALAYSIA_CODE)){
						mgmtNonBumi++;
					}

					if(!personnel.getPmCountry().getCountryCode().equalsIgnoreCase(SupplierManagementConst.MALAYSIA_CODE)){
						mgmtNonMalaysian++;
					}
					
					if(this.getManagedBean().getMgmtLs()== null){
						this.getManagedBean().setMgmtLs(new ArrayList<SmPersonnel>());
					}
					
					this.getManagedBean().getMgmtLs().add(personnel);
				}

				if(personnel.getPmStaffRole().getParameterCode() == SupplierManagementConst.PERSONNEL_PROF_STAFF_CONSULTANT_PARAMCODE || 
						personnel.getPmStaffRole().getParameterCode() == SupplierManagementConst.PERSONNEL_SEMIPROF_STAFF_CONSULTANT_PARAMCODE ||
						personnel.getPmStaffRole().getParameterCode() == SupplierManagementConst.PERSONNEL_CLERICAL_STAFF_CONSULTANT_PARAMCODE){
					ttlWorker++;

					if(personnel.isBumi()){
						mgmtBumi++;
					}else if(!personnel.isBumi() && personnel.getPmCountry().getCountryCode().equalsIgnoreCase(SupplierManagementConst.MALAYSIA_CODE)){
						mgmtNonBumi++;
					}

					if(!personnel.getPmCountry().getCountryCode().equalsIgnoreCase(SupplierManagementConst.MALAYSIA_CODE)){
						mgmtNonMalaysian++;
					}
				}
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
	}

	public BigDecimal calculateDirectorPercentage(int dirNo){
		BigDecimal convertedDirNo = new BigDecimal(dirNo);
		BigDecimal ttlDirector = new BigDecimal(this.getManagedBean().getTotalBOD());
		BigDecimal percentage = new BigDecimal(0.00);
		
		try{
			percentage = convertedDirNo.divide(ttlDirector, 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
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
		ISmRemarksSO reqSO = (ISmRemarksSO)BusinessDelegate.getInstance().getServiceObject("sm", ISmRemarksSO.class);
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
		System.out.println("****cheque signatory**" + this.getManagedBean().getSmSvTask().getSmSvBankInfos().size());
		int totalChequeSignatory = 0;
		int cheqBumi = 0,cheqNonBumi = 0, cheqNonMalaysian = 0;
		
		if(this.getManagedBean().getSmSvTask().getSmSvBankInfos() != null){
			for(SmSvBankInfo svBankInfo : this.getManagedBean().getSmSvTask().getSmSvBankInfos()){
				if(svBankInfo.getSmSupplierBank().getSmChequeSignatories().size() > 0){
					for(SmChequeSignatory chequeSignatory : svBankInfo.getSmSupplierBank().getSmChequeSignatories()){
						totalChequeSignatory++;
						if(chequeSignatory.getSmPersonnel().isBumi()){
							cheqBumi++;
						}else if(!chequeSignatory.getSmPersonnel().isBumi() && chequeSignatory.getSmPersonnel().getPmCountry().getCountryCode().equalsIgnoreCase(SupplierManagementConst.MALAYSIA_CODE)){
							cheqNonBumi++;
						}
						
						if(!chequeSignatory.getSmPersonnel().getPmCountry().getCountryCode().equalsIgnoreCase(SupplierManagementConst.MALAYSIA_CODE)){
							cheqNonMalaysian++;
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
			 System.out.println("userLocale------>" + new FacesScopes().getUserLocale().getLanguage());
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

				prepareEquipmentLicenseInsuranceView(svTemplate, catCode);
			}
			
			if(svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_MANUFACTURING)){
				prepareManufacturingFinancialDocView(svTemplate, catCode);
			}
			
			if(svTemplate.getPmParameter().getParameterCode().equals(SupplierManagementConst.CAT_TYPE_PRINTING)){
				preparePrintingInfoView();
				prepareEquipmentLicenseInsuranceView(svTemplate, catCode);
			}
			
			
			if(svTemplate.getSmSvMachines() != null){
				System.out.println("***svTemplate.getSmSvMachines()>>>" + svTemplate.getSmSvMachines().size());
				if(svTemplate.getSelectedSmMachine() == null){
					svTemplate.setSelectedSmMachine(new SmMachineDtl[svTemplate.getSmSvTemplateAddresses().size()]);
				}
				SmMachineDtl[] selectedMachine = svTemplate.getSelectedSmMachine();
				for(int macCount = 0; macCount < svTemplate.getSmSvMachines().size(); svTemplate.getSmSvMachines()){
					selectedMachine[macCount] = svTemplate.getSmSvMachines().get(macCount).getSmMachineDtl();
					log.info("****selectedAdd*******" + svTemplate.getSmSvTemplateAddresses().get(macCount).getSmSvAddress());
					macCount++;
				}
			}
			
			log.info("---------------viewActiveIndex End-----------");
		}
	}
	
	private void prepareEquipmentLicenseInsuranceView(SmSvTemplate svTemplate, String catType){
		System.out.println("***********prepareEquipmentLicenseInsuranceView*************");
		if(catType.equals(SupplierManagementConst.CAT_TYPE_DOCKYARD) || catType.equals(SupplierManagementConst.CAT_TYPE_WORKSHOP)){
			String strEquipMaint = null;
			String strEquipBeating = null;
			List<PmParameterDesc> dockyardLicenceInsurance = SmCommonUtil.retrieveParameterDescList(ParameterTypeConst.DOCKYARD_WORKSHOP_LICENSE_INS, new FacesScopes().getUserLocale().getLanguage());


			if(svTemplate.getLicenseInsuranceParamMap() == null){
				svTemplate.setLicenseInsuranceParamMap(new HashMap<Long, PmParameterDesc>());
				for(PmParameterDesc licenseInsuranceDesc : dockyardLicenceInsurance){
					svTemplate.getLicenseInsuranceParamMap().put(licenseInsuranceDesc.getPmParameter().getParameterId(), licenseInsuranceDesc);
				}
			}

			if(svTemplate.getEquipmentParamMap() == null){
				log.info("*********getEquipmentParamMap IS NULL**********");
				log.info("**************catType************" + catType);
				if(catType.equals(SupplierManagementConst.CAT_TYPE_DOCKYARD)){
					strEquipMaint = ParameterTypeConst.DOCKYARD_EQUIPMENT_MAINTENANCE;
					strEquipBeating = ParameterTypeConst.DOCKYARD_EQUIPMENT_BEATING;
				}else if(catType.equals(SupplierManagementConst.CAT_TYPE_WORKSHOP)){
					strEquipMaint = ParameterTypeConst.WORKSHOP_EQUIPMENT_MAINTENANCE;
					strEquipBeating = ParameterTypeConst.WORKSHOP_EQUIPMENT_BEATING;
				}

				List<PmParameterDesc> equipMaintenanceLs = SmCommonUtil.retrieveParameterDescList(strEquipMaint, new FacesScopes().getUserLocale().getLanguage());
				List<PmParameterDesc> equipPanelBeatingLs = SmCommonUtil.retrieveParameterDescList(strEquipBeating, new FacesScopes().getUserLocale().getLanguage());

				log.info("-----------equipMaintenanceLs---------" + equipMaintenanceLs.size());
				log.info("-----------equipPanelBeatingLs---------" + equipPanelBeatingLs.size());
				//if(svTemplate.getEquipmentParamMap() == null){
				svTemplate.setEquipmentParamMap(new HashMap<Long,PmParameterDesc>());
				for(PmParameterDesc equipMainParamDesc : equipMaintenanceLs){
					svTemplate.getEquipmentParamMap().put(equipMainParamDesc.getPmParameter().getParameterId(), equipMainParamDesc);
					for(SmSvEquipment svEquipMain : svTemplate.getSmSvEquipments()){
						if(equipMainParamDesc.getPmParameter().getParameterId() == svEquipMain.getPmParameter().getParameterId()){
							log.info("**********svEquipMain equal********" + equipMainParamDesc.getPmParameter().getParameterId() +" = " +svEquipMain.getPmParameter().getParameterId());
							if(svTemplate.getEquipmentMain() == null){
								svTemplate.setEquipmentMain(new ArrayList<SmSvEquipment>());
							}
							svTemplate.getEquipmentMain().add(svEquipMain);
						}
					}
				}

				for(PmParameterDesc equipPanelParamDesc : equipPanelBeatingLs){
					svTemplate.getEquipmentParamMap().put(equipPanelParamDesc.getPmParameter().getParameterId(), equipPanelParamDesc);
					for(SmSvEquipment svEquipBeating : svTemplate.getSmSvEquipments()){
						if(equipPanelParamDesc.getPmParameter().getParameterId() == svEquipBeating.getPmParameter().getParameterId()){
							log.info("**********svEquipBeating equal********" + equipPanelParamDesc.getPmParameter().getParameterId() +" = " +svEquipBeating.getPmParameter().getParameterId());
							if(svTemplate.getEquipmentBeating() == null){
								svTemplate.setEquipmentBeating(new ArrayList<SmSvEquipment>());
							}
							svTemplate.getEquipmentBeating().add(svEquipBeating);
						}
					}
				}
				//}
			}
		}else if(catType.equals(SupplierManagementConst.CAT_TYPE_PRINTING)){
			List<PmParameterDesc> printingLicenceInsurances = SmCommonUtil.retrieveParameterDescList(ParameterTypeConst.PRINTING_LICENSE_INS, new FacesScopes().getUserLocale().getLanguage());
			if(svTemplate.getLicenseInsuranceParamMap() == null){
				svTemplate.setLicenseInsuranceParamMap(new HashMap<Long, PmParameterDesc>());
				for(PmParameterDesc licenseInsuranceDesc : printingLicenceInsurances){
					svTemplate.getLicenseInsuranceParamMap().put(licenseInsuranceDesc.getPmParameter().getParameterId(), licenseInsuranceDesc);
				}
			}
		}
	}
	

	private void prepareManufacturingFinancialDocView(SmSvTemplate svTemplate, String catType){
		log.info("*******prepareManufacturingFinancialDocView*************");
		
		if(svTemplate.getFinancialDocParamMap() == null){
			List<PmParameterDesc> manufacturingFinDocLs = SmCommonUtil.retrieveParameterDescList(ParameterTypeConst.MANUFACTURING_FINANCIAL_DOC, new FacesScopes().getUserLocale().getLanguage());
			svTemplate.setFinancialDocParamMap(new HashMap<Long,PmParameterDesc>());

			OUTER:  //outer label
			for(SmSvManufacturingDoc manufacturingDoc : svTemplate.getSmSvManufacturingDocs()){
				if(manufacturingDoc.getPmParameter() != null){
					INNER: //inner label
					for(PmParameterDesc manufacturingFinDocDesc : manufacturingFinDocLs){
						if(!svTemplate.getFinancialDocParamMap().containsKey(manufacturingFinDocDesc.getPmParameter().getParameterId())){
							svTemplate.getFinancialDocParamMap().put(manufacturingFinDocDesc.getPmParameter().getParameterId(), manufacturingFinDocDesc);
						}
						
						if(manufacturingFinDocDesc.getPmParameter().getParameterId() == manufacturingDoc.getPmParameter().getParameterId()){
							log.info("***----EQUAL----***");
							if(svTemplate.getFinancialDocLs() == null){
								svTemplate.setFinancialDocLs(new ArrayList<SmSvManufacturingDoc>());
							}
							
							svTemplate.getFinancialDocLs().add(manufacturingDoc);
							break INNER;
						}
					}
				}else{
					if(svTemplate.getAdditionalDocLs() == null){
						svTemplate.setAdditionalDocLs(new ArrayList<SmSvManufacturingDoc>());
					}
					svTemplate.getAdditionalDocLs().add(manufacturingDoc);
				}
			}
		}
		
		
	}
	
	private void preparePrintingInfoView(){
		if(this.getManagedBean().getPrintingCatLs() == null){
			this.getManagedBean().setPrintingCatLs(new ArrayList<String>());
		}
		
		
		for(SmSvCategory svCat : this.getManagedBean().getSmSvTask().getSmSvCategories()){
			String lv1 = svCat.getPmCategoryL1().getCategoryL1Code();
			String lv2 = svCat.getPmCategoryL2().getCategoryL2Code();
			String lv3 = svCat.getPmCategoryL3().getCategoryL3Code();
							
			String catCodeStr = lv1 + lv2 + lv3;
			
			if(printingCodeLs.contains(catCodeStr)){
				if(!this.getManagedBean().getPrintingCatLs().contains(catCodeStr)){
					this.getManagedBean().getPrintingCatLs().add(catCodeStr);
					log.info("Printing---->" + catCodeStr);
				}
				
				if(this.getManagedBean().getPrintingParamMap() == null){
					this.getManagedBean().setPrintingParamMap(new HashMap<Long,PmParameterDesc>());
				}
				
				if(svCat.getSmSvPrintmachines() != null || svCat.getSmSvPrintmachines().size() >= 1){
/*					log.info("Printing---->" + catCodeStr);
					log.info("Printing machine size---->" + svCat.getSmSvPrintmachines().size());*/
					
					
					//OUTER: //outer label
						for(SmSvPrintmachine printMachine : svCat.getSmSvPrintmachines()){
							log.info("printMachine>>>>>>>>" + printMachine);
							List<PmParameterDesc> pmDescList = retrievePrintingAcc(catCodeStr);
							
							if(pmDescList.size() > 0){
								//INNER: //inner label
									for(PmParameterDesc pmDesc : pmDescList){
										if(!this.getManagedBean().getPrintingParamMap().containsKey(pmDesc.getPmParameter().getParameterId())){
											this.getManagedBean().getPrintingParamMap().put(pmDesc.getPmParameter().getParameterId(), pmDesc);
										}
										
/*										if(pmDesc.getPmParameter().getParameterId() == printMachine.getPmParameter().getParameterId()){
											break INNER;
										}*/
									}
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
	
	public void retrieveActiveCategoryCode(Long applId){
		SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
		ISupplierSearchSO reqSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject("sm", ISupplierSearchSO.class);
		reqDTO.setApplId(applId);
		
		SupplierSearchResDTO resDTO = reqSO.retrieveSupplierActiveCategoryCodeByApplId(reqDTO);
		
		this.getManagedBean().setCategoryApplicable(resDTO.getSmSupplierCategoryList());
		
	}
	
	public String getsupplierApplTypeDesc(String applType){
		return SmCommonUtil.getApplTypeDesc(applType);
	}
}
