package my.ep.web.backingbean.sm.supportingdocinquiry;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.sm.supportingdocinquiry.ISupportingDocInquirySO;
import my.ep.constant.MessageConst;
import my.ep.dto.sm.supportingdocinquiry.SupportingDocInquiryReqDTO;
import my.ep.dto.sm.supportingdocinquiry.SupportingDocInquiryResDTO;
import my.ep.exception.EpException;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.supportingdocinquiry.SupportingDocInquiryManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmCommonUtil;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "supportingDocInquiryBackingBean")
@RequestScoped
public class SupportingDocInquiryBackingBean extends BaseBackingBean<SupportingDocInquiryManagedBean> {

	private static Logger log = Logger.getLogger(SupportingDocInquiryBackingBean.class);

	public SupportingDocInquiryBackingBean(){

	}
	
	public void viewDocInquiry(){
		log.info("**********viewDocInquiry*********");
		ISupportingDocInquirySO reqSO = (ISupportingDocInquirySO) BusinessDelegate.getInstance().getServiceObject("sm", ISupportingDocInquirySO.class);
		SupportingDocInquiryReqDTO reqDTO = new SupportingDocInquiryReqDTO();
		//this.getManagedBean().setAppId(1009L);
		reqDTO.setAppId(this.getManagedBean().getAppId());
		
		SupportingDocInquiryResDTO resDTO = reqSO.retrieveInquiryByAppId(reqDTO);
		this.getManagedBean().setSmApplInquiry(resDTO.getSmApplInquiry());
		
	}
	
	public String submitInquiryResponse(){
		log.info("**********submitInquiryResponse*********");
		try{
			log.info("**********submitInquiryResponse*********");
			ISupportingDocInquirySO reqSO = (ISupportingDocInquirySO) BusinessDelegate.getInstance().getServiceObject("sm", ISupportingDocInquirySO.class);
			SupportingDocInquiryReqDTO reqDTO = new SupportingDocInquiryReqDTO();
			String token = new FacesScopes().getBPMAuthenticationCtxToken();
			
			Date currDate = new Date();
			
			log.info("**********Current Date*********" + currDate);
			this.getManagedBean().getSmApplInquiry().setResponseDate(currDate);
			reqDTO.setToken(token);
			reqDTO.setBpmPojo(this.getManagedBean().getBpmPojo());
			reqDTO.setOutcome(SupplierManagementConst.BPM_SUBMIT);
			reqDTO.setSmApplInquiry(this.getManagedBean().getSmApplInquiry());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());

			SupportingDocInquiryResDTO resDTO = reqSO.updateInquiry(reqDTO);
			//SupportingDocInquiryResDTO resDTO = new SupportingDocInquiryResDTO();
			//resDTO.setSuccessful(true);
			if(resDTO.isSuccessful()){
				log.info("*****is Success******");
				RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
				return navigateToAck();
			}
		}catch(Exception ex){
			throw new EpException(ex.getMessage());
		}
		
		return "";
	}
	
	public String navigateToAck(){
		FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,MessageUtils.getMessage(MessageConst.INFO_721102,
		MessageUtils.BUNDLE_COMMON_MESSAGES,
		new FacesScopes().getUserLocale())));

		return CommonUIConst.ACKNOWLEDGE_PAGE;
	}
	
	
	public void preRenderView(){
		log.info("**********preRenderView*********");
		if(this.getManagedBean().getSmApplInquiry() == null){
			//this.getManagedBean().setAppId(2765L); //TODO
			viewDocInquiry();
		}
	}
	
	public String getsupplierApplTypeDesc(String applType){
		return SmCommonUtil.getApplTypeDesc(applType);
	}
	
}
