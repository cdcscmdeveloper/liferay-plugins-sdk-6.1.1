package my.ep.web.backingbean.sm.sdoreview;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.sm.sdoreview.ISdoReviewSO;
import my.ep.dto.sm.sdoreview.SdoReviewReqDTO;
import my.ep.dto.sm.sdoreview.SdoReviewResDTO;
import my.ep.persistence.entity.sm.SmApplSupportDoc;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmRemark;
import my.ep.persistence.logging.LogKey;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.sdoreview.SdoReviewManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

@ManagedBean(name = "sdoReviewBackingBean")
@RequestScoped
public class SdoReviewBackingBean extends BaseBackingBean<SdoReviewManagedBean> {

	public static Logger log = Logger.getLogger(SdoReviewBackingBean.class);

	public SdoReviewBackingBean() {
		if (MDC.get(LogKey.USER_ID) == null) {
			MDC.put(LogKey.USER_ID, 1L);
		}
	}
	
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	public void viewSupportingDocument() {
		//this.getManagedBean().setAppId(1006);
		log.info("----------appID------------"+ this.getManagedBean().getAppId());

		SdoReviewReqDTO reqDTO = new SdoReviewReqDTO();
		ISdoReviewSO reqSO = (ISdoReviewSO) BusinessDelegate.getInstance().getServiceObject("sm", ISdoReviewSO.class);
		reqDTO.setApplId(this.getManagedBean().getAppId());
		SdoReviewResDTO resDTO = reqSO.retrieveApplById(reqDTO);
		this.getManagedBean().setSmAppl(resDTO.getSmAppl());
		
		log.info("----------------------------------" + resDTO.getSmAppl().getSmPersonnels().size());
		for(SmPersonnel sp : resDTO.getSmAppl().getSmPersonnels()){
			if(sp.isContactPerson()){
				this.getManagedBean().setSmPersonnel(sp);
				break;
			}
		}
		this.getManagedBean().setCompanyAdd(this.getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress());
		this.getManagedBean().setSmRemark(new SmRemark());
		
		log.info("----------Supporting document-------------" +  this.getManagedBean().getSmAppl().getSmApplSupportDocs().size());

	}

	public void testSave(){
		for(SmApplSupportDoc sd : this.getManagedBean().getSmAppl().getSmApplSupportDocs()){
			log.info("********SuppDoc" + sd.getApplSupportDocId() + " - received - " + sd.isReceived() + " -Verified -" + sd.isVerified());
		}
		
		log.info("*****received dat- " + this.getManagedBean().getSmAppl().getSdoReceivedDate());
		log.info("****Remark" + this.getManagedBean().getSmRemark().getRemark());
	}
	
	public String submitSDOReviewAction(String res) {
		//String bpmUsrName = getFacesScopes.getUserScreenName();
		try{
			String token = getFacesScopes().getBPMAuthenticationCtxToken();
			SdoReviewReqDTO reqDTO = new SdoReviewReqDTO();
			ISdoReviewSO reqSO = (ISdoReviewSO) BusinessDelegate.getInstance().getServiceObject("sm", ISdoReviewSO.class);
			reqDTO.setApplId(this.getManagedBean().getAppId());
			reqDTO.setOutcome(res);
			reqDTO.setTaskId(this.getManagedBean().getTaskId());

			log.info("-----------SDO----------" + reqDTO.getOutcome());
			log.info("-----------SDO Review----------" + reqDTO.getTaskId());

			//reqDTO.setBpmUserName(bpmUsrName);
			reqDTO.setBpmPojo(this.getManagedBean().getBpmPojo());
			reqDTO.setSmAppl(this.getManagedBean().getSmAppl());
			reqDTO.setToken(token);


			SmRemark smRmk = this.getManagedBean().getSmRemark();
			smRmk.setDocType(SupplierManagementConst.SM_RMK_DOC_TYPE_SVO);
			smRmk.setDocId(this.getManagedBean().getSmAppl().getApplId());

			reqDTO.setSmRemark(smRmk);
			SdoReviewResDTO resDTO = reqSO.submitBPMSdoReview(reqDTO);

			if (resDTO.isSuccessful()) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Application has been updated successfully", ""));
				return "/xhtml/sm/disciplinaryAction/outputMsg";
			} else {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ""));
			}
		}catch(Exception ex){
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR,"Failed to process.", ""));
		}
		
		return null;
	}
}
