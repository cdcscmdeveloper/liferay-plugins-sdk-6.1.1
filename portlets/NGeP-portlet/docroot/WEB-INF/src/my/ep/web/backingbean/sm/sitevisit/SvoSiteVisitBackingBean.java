package my.ep.web.backingbean.sm.sitevisit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.sm.remarks.ISmRemarksSO;
import my.ep.biz.service.sm.siteVisit.ISiteVisitSO;
import my.ep.dto.sm.siteVisit.SiteVisitReqDTO;
import my.ep.dto.sm.siteVisit.SiteVisitResDTO;
import my.ep.dto.sm.smRemarks.SmRemaksResDTO;
import my.ep.dto.sm.smRemarks.SmRemarksReqDTO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sm.SmChequeSignatory;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmRemark;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSvBankInfo;
import my.ep.persistence.entity.sm.SmSvComment;
import my.ep.persistence.entity.sm.SmSvFinChecklist;
import my.ep.persistence.entity.sm.SmSvInterviewee;
import my.ep.persistence.entity.sm.SmSvOffice;
import my.ep.persistence.entity.sm.SmSvTask;
import my.ep.persistence.entity.sm.SmSvTemplate;
import my.ep.persistence.logging.LogKey;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.sitevisit.SvoSiteVisitManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmCommonUtil;

import org.apache.log4j.Logger;

@ManagedBean(name = "svoSiteVisitBackingBean")
@RequestScoped
public class SvoSiteVisitBackingBean extends
		BaseBackingBean<SvoSiteVisitManagedBean> {
	public static Logger log = Logger.getLogger(SvoSiteVisitManagedBean.class);
//	public static String NaviToView = "siteVisitDetails?faces-redirect=true";
	//public static String NaviToSiteVisitTemplate = "submitOnlineRpt?faces-redirect=true";
	public static String NaviToSiteVisitTemplate = "submitOnlineRpt";
	public static String NaviToSiteVisitPreDetails = "submitOnlineRptPreDetails";
	public static String NaviToSiteVisitView = "siteVisitView";

	public SvoSiteVisitBackingBean() {
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		if (pmUser != null && pmUser.getUserId() != null) {
			System.out.println("***userId: " + pmUser.getUserId());

			new LogKey(pmUser.getUserId()); 
		}else{
			new LogKey(1L); 
		}
	}

	public void retrieveSiteVisitInfo(){
		this.getManagedBean().setAppId(1009);
		System.out.println("--------retrieveSiteVisitInfo--------" + this.getManagedBean().getAppId());
		SiteVisitReqDTO reqDTO = new SiteVisitReqDTO();
		//		reqDTO.setAppId(this.getManagedBean().getAppId());
		reqDTO.setAppId(this.getManagedBean().getAppId());
		ISiteVisitSO reqSO = (ISiteVisitSO) BusinessDelegate.getInstance().getServiceObject("sm", ISiteVisitSO.class);
		SiteVisitResDTO resDTO = reqSO.retrieveSiteVisitInfoByAppId(reqDTO);
		
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
			//			
			//			
			//			for (Map.Entry<String, SmSvTemplate> entry : this.getManagedBean().getSmSvTemplateMap().entrySet()) {
			//				System.out.println("Key : " + entry.getKey() + " Value : "
			//						+ entry.getValue().getPmParameter().getParameterCode());
			//			}
		}
		
		if(smSvTask.getSmSvComment() == null){
			smSvTask.setSmSvComment(new SmSvComment());
			smSvTask.getSmSvComment().setSmSvTask(this.getManagedBean().getSmSvTask());
		}

		if(smSvTask.getSmSvOffice() == null){
			smSvTask.setSmSvOffice(new SmSvOffice());
			smSvTask.getSmSvOffice().setSmSvTask(this.getManagedBean().getSmSvTask());
		}

		getTotalBODMgmtWorker();
		chequeSignatoryInfo();

		System.out.println("*********categories**" +this.getManagedBean().getSmSvTask().getSmSvCategories().size());

	}

	public String submitSiteVisitReport(){
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
		
	}
	
	public void changeActiveIndex(int index,String catType){
		System.out.println("*******changeActiveIndex********" + index);
		this.getManagedBean().setActiveIndex(index);
		System.out.println("*******Category pass********" + this.getManagedBean().getSmSvTask().getSmSvCategories().get(0).getPassed()+"****"+ this.getManagedBean().getSmSvTask().getSmSvCategories().get(0).getPmCategoryL3().getCategoryL3Code());
		System.out.println("*******changeActiveIndex CatType********" + catType.length());
		if(catType.length() > 0){
//			this.getManagedBean().setSelectedSvTemplate(new SmSvTemplate());
			this.getManagedBean().setSelectedSvTemplate(this.getManagedBean().getSmSvTemplateMap().get(catType));
			
			//this.getManagedBean().setSelectedSmSvAddress(this.getManagedBean().getSelectedSvTemplate().getSmSvAddress());
		}
//		System.out.println("**********SvAddress Selected SV*********" + this.getManagedBean().getSelectedSvTemplate().getSvTemplateId());
	}
	
	public void addInterviewee(){
		System.out.println("*******getSelectedSvTemplate**********" + this.getManagedBean().getSelectedSvTemplate().getSvTemplateId());
		SmSvInterviewee obj = new SmSvInterviewee();
		obj.setSmSvAddress(this.getManagedBean().getSelectedSvTemplate().getSmSvAddress());
		this.getManagedBean().getSelectedSvTemplate().getSmSvAddress().getSmSvInterviewees().add(obj);
		
		System.out.println("******after******"+this.getManagedBean().getSelectedSvTemplate().getSmSvAddress().getSmSvInterviewees().size());
	}
	
	public void removeInterviewee(){
		if(this.getManagedBean().getSelectedInterviewees() != null && this.getManagedBean().getSelectedInterviewees().length > 0){
			log.info("---removeSupplierBank 1---");
			for (SmSvInterviewee interviewee : this.getManagedBean().getSelectedInterviewees()) {
				log.info("---removeSupplierBank---" + interviewee);
				this.getManagedBean().getSelectedSvTemplate().getSmSvAddress().getSmSvInterviewees().remove(interviewee);		
			}
		}
		
		this.getManagedBean().setSelectedInterviewees(null);
	}
	
	public void selectSvAddress(){
		System.out.println("******preSetSelectedAddress*******" + this.getManagedBean().getSelectedSvTemplate().getSmSvAddress().getSvAddressId());
		System.out.println("******preSetSelectedAddress add1*******" +this.getManagedBean().getSelectedSvTemplate().getSmSvAddress().getSmAddress().getAddress1());
		
		this.getManagedBean().getSelectedSvTemplate().getSmSvAddress().setSmSvInterviewees(new ArrayList<SmSvInterviewee>());
	}
	
//	public void testGetValue(){
////		this.getManagedBean().getSmSvTask().getSmSvOffice().isOwned()
//		SmSvTemplate svTemp = this.getManagedBean().getSmSvTemplateMap().get("L");
//		System.out.println("*****Sv Address*****" + svTemp.getSmSvAddress().getSmAddress().getAddress1());
//		System.out.println("*****Sv Interviewee size*****" + svTemp.getSmSvAddress().getSmSvInterviewees().size());
//	}
	
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	public void test(){
		for(SmSvFinChecklist finList : this.getManagedBean().getSmSvTask().getSmSvFinChecklists()){
			System.out.println("Finlist>>>>>>>" + finList.isValid() + "----" + finList.getPmParameter().getParameterId());
		}
		
		FacesScopes fs = new FacesScopes();
		//if(fs.hasRoleUser(SupplierManagementConst.SVO_HQ) || fs.hasRoleUser(SupplierManagementConst.SVO_SK) || fs.hasRoleUser(SupplierManagementConst.SVO_SA)){
		System.out.println("*****Has User Role********");
		SiteVisitReqDTO reqDTO = new SiteVisitReqDTO();
		reqDTO.setUserRole(SupplierManagementConst.SVO_HQ);
		reqDTO.setSmSvTask(this.getManagedBean().getSmSvTask());
		reqDTO.setSmRemarkMap(this.getManagedBean().getRemarkMap());
		ISiteVisitSO reqSO = (ISiteVisitSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", ISiteVisitSO.class);
		SiteVisitResDTO resDTO = reqSO.submitSiteVisitReport(reqDTO);

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
			retrieveSiteVisitInfo();
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
}
