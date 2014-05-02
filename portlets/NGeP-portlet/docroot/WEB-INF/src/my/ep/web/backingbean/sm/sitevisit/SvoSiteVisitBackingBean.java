package my.ep.web.backingbean.sm.sitevisit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.sm.siteVisit.ISiteVisitSO;
import my.ep.dto.sm.siteVisit.SiteVisitReqDTO;
import my.ep.dto.sm.siteVisit.SiteVisitResDTO;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sm.SmRemark;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSvAddress;
import my.ep.persistence.entity.sm.SmSvBankInfo;
import my.ep.persistence.entity.sm.SmSvFinChecklist;
import my.ep.persistence.entity.sm.SmSvInterviewee;
import my.ep.persistence.entity.sm.SmSvTask;
import my.ep.persistence.entity.sm.SmSvTemplate;
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

	}

	public void retrieveSiteVisitInfo(){
//		if(this.getManagedBean().getSmSvTask() == null){
//			this.getManagedBean().setAppId(1009);
			System.out.println("--------retrieveSiteVisitInfo--------" + this.getManagedBean().getAppId());
			SiteVisitReqDTO reqDTO = new SiteVisitReqDTO();
			//		reqDTO.setAppId(this.getManagedBean().getAppId());
			reqDTO.setAppId(this.getManagedBean().getAppId());
			ISiteVisitSO reqSO = (ISiteVisitSO) BusinessDelegate.getInstance()
					.getServiceObject("sm", ISiteVisitSO.class);
			SiteVisitResDTO resDTO = reqSO.retrieveSiteVisitInfoByAppId(reqDTO);

			this.getManagedBean().setSmSvTask(resDTO.getSmSvTask());
			SmSvTask smSvTask = this.getManagedBean().getSmSvTask();
			//		this.getManagedBean().getSmSvTask().getsm
			System.out.println("*****this.getManagedBean().getSmSvTask().getSmSvFinChecklists().size()" + smSvTask.getSmSvFinChecklists().size());
			if(smSvTask.getSmSvFinChecklists().size() < 1){
				System.out.println("******getSmSvFinChecklists() == null******");
				List<PmParameterDesc> pmParamDesc = new ArrayList<PmParameterDesc>();
				this.getManagedBean().setRemarkMap(new HashMap<Long,SmRemark>());
				pmParamDesc = SmCommonUtil.retrieveParameterDescList(SupplierManagementConst.SVO_FIN_CHECKLIST, "en");
				for(PmParameterDesc pmdesc : pmParamDesc){
					SmSvFinChecklist svCL = new SmSvFinChecklist();
					svCL.setPmParameter(pmdesc.getPmParameter());
					svCL.setSmSvTask(smSvTask);

					pmdesc.getPmParameter().setPmParameterDescs(new ArrayList<PmParameterDesc>());
					pmdesc.getPmParameter().getPmParameterDescs().add(pmdesc);

					SmRemark remarkObj = new SmRemark();
					remarkObj.setDocType("FCL");
					this.getManagedBean().getRemarkMap().put(pmdesc.getPmParameter().getParameterId(), remarkObj);

					svCL.setSmSvTask(smSvTask);
					smSvTask.getSmSvFinChecklists().add(svCL);
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
		}

//	}
	
	public String submitSiteVisitReport(String action){
		/*Map map2 = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap();
		String action = (String) map2.get("name1");
		*/
		log.info("----------------------submitSiteVisitReport start----------------" +  action);
		SiteVisitReqDTO reqDTO = new SiteVisitReqDTO();
		ISiteVisitSO reqSO = (ISiteVisitSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", ISiteVisitSO.class);
		
		String bpmUsrName = new FacesScopes().getUserScreenName();
		
		reqDTO.setAppId(this.getManagedBean().getAppId());
		reqDTO.setOutcome(action);
		reqDTO.setTaskId(this.getManagedBean().getTaskId());
		reqDTO.setBpmUserName(bpmUsrName);
		//reqDTO.setAppType(this.getManagedBean().getAppType());
		reqDTO.setBpmPojo(this.getManagedBean().getBpmPojo());
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
		
		System.out.println("*******changeActiveIndex CatType********" + catType.length());
		if(catType.length() > 0){
			this.getManagedBean().setSelectedSvTemplate(this.getManagedBean().getSmSvTemplateMap().get(catType));
			
			System.out.println("******SELECTED TEMPLATE" + this.getManagedBean().getSelectedSvTemplate().getSvTemplateId());
			
//			if(this.getManagedBean().getSelectedSvTemplate().getSmSvAddress() == null){
//				System.out.println("**********SvAddress Null*********" + this.getManagedBean().getSelectedSvTemplate().getSmSvAddress());
//				//System.out.println("**********SvAddress Class Null*********" + this.getManagedBean().getSelectedSvTemplate().getSmSvAddress().getClass());
//				this.getManagedBean().getSelectedSvTemplate().setSmSvAddress(new SmSvAddress());
//				//this.getManagedBean().getSelectedSvTemplate().getSmSvAddress().setSmSvTask(this.getManagedBean().getSmSvTask());
//				System.out.println("**********SvAddress Added*********");
//				System.out.println("**********SvAddress Added*********" + this.getManagedBean().getSelectedSvTemplate().getSmSvAddress());
//			}
//			
//			if(this.getManagedBean().getSelectedSvTemplate().getSmSvAddress().getSmSvInterviewees() == null){
//				// || this.getManagedBean().getSelectedSvTemplate().getSmSvAddress().getSmSvInterviewees().isEmpty()
//				System.out.println("**********getSmSvInterviewees Null*********");
//				this.getManagedBean().getSelectedSvTemplate().getSmSvAddress().setSmSvInterviewees(new ArrayList<SmSvInterviewee>());
//			}
		}
		System.out.println("**********SvAddress Selected SV*********" + this.getManagedBean().getSelectedSvTemplate());
		//System.out.println("**********SvAddress Added ENd*********" + this.getManagedBean().getSelectedSvTemplate().getSmSvAddress());
		//System.out.println("**********SvAddress getSmSvInterviewees*********" + this.getManagedBean().getSelectedSvTemplate().getSmSvAddress().getSmSvInterviewees());
		
	}
	
	public void addInterviewee(){
		System.out.println("*******addInterviewee**********");
		System.out.println("**********addInterviewee Selected SV*********" + this.getManagedBean().getSelectedSvTemplate());
		
		if(this.getManagedBean().getSelectedSvTemplate().getSmSvAddress() == null){
			//System.out.println("**********SvAddress Null*********" + this.getManagedBean().getSelectedSvTemplate().getSmSvAddress());
			//System.out.println("**********SvAddress Class Null*********" + this.getManagedBean().getSelectedSvTemplate().getSmSvAddress().getClass());
			this.getManagedBean().getSelectedSvTemplate().setSmSvAddress(new SmSvAddress());
			this.getManagedBean().getSelectedSvTemplate().setSmSvTask(this.getManagedBean().getSmSvTask());
			
			//this.getManagedBean().getSelectedSvTemplate().getSmSvAddress().setSmSvTask(this.getManagedBean().getSmSvTask());
			//System.out.println("**********SvAddress Added*********");
			//System.out.println("**********SvAddress Added*********" + this.getManagedBean().getSelectedSvTemplate().getSmSvAddress());
		}
		
		//this.getManagedBean().getSelectedSmSvAddress().setSmAddress(smAddress);
//		
//		if(this.getManagedBean().getSelectedSvTemplate().getSmSvAddress().getSmSvInterviewees() == null){
//			// || this.getManagedBean().getSelectedSvTemplate().getSmSvAddress().getSmSvInterviewees().isEmpty()
//			System.out.println("**********getSmSvInterviewees Null*********");
//			this.getManagedBean().getSelectedSvTemplate().getSmSvAddress().setSmSvInterviewees(new ArrayList<SmSvInterviewee>());
//		}
		System.out.println("******b4******"+this.getManagedBean().getSelectedSvTemplate().getSmSvAddress().getSmSvInterviewees().size());
		
		//System.out.println("*******getSelectedSvTemplate**********" + this.getManagedBean().getSelectedSvTemplate());
		SmSvInterviewee obj =new SmSvInterviewee();
		this.getManagedBean().getSelectedSvTemplate().getSmSvAddress().getSmSvInterviewees().add(obj);
		
		System.out.println("******after******"+this.getManagedBean().getSelectedSvTemplate().getSmSvAddress().getSmSvInterviewees().size());
	}
	
	public void test(){
		//		for(SmSvFinChecklist finList : this.getManagedBean().getSmSvTask().getSmSvFinChecklists()){
		//			SmRemark rmk = this.getManagedBean().getRemarkMap().get(finList.getPmParameter().getParameterId());
		//			
		//		}
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
		//}
	}
	

}
