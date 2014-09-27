package my.ep.web.backingbean.sm.sdoreview;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.sm.sdoreview.ISdoReviewSO;
import my.ep.constant.MessageConst;
import my.ep.dto.sm.sdoreview.SdoReviewReqDTO;
import my.ep.dto.sm.sdoreview.SdoReviewResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sm.SmApplInquiry;
import my.ep.persistence.entity.sm.SmApplSupportDoc;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmRemark;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.sdoreview.SdoReviewManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmCommonUtil;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "sdoReviewBackingBean")
@RequestScoped
public class SdoReviewBackingBean extends BaseBackingBean<SdoReviewManagedBean> {

	public static Logger log = Logger.getLogger(SdoReviewBackingBean.class);

	public SdoReviewBackingBean() {
		
	}

	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	public void viewSupportingDocument() {
		//this.getManagedBean().setAppId(1006);
		log.info("----------appID------------"+ this.getManagedBean().getAppId());
		//this.getManagedBean().setAppId(2765); 
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
		this.getManagedBean().setSmApplInquiry(new SmApplInquiry());
		this.getManagedBean().setSelectedInquiryResponse(new SmApplInquiry());
		
		this.getManagedBean().setUnselectedSmApplSuppDocs(new ArrayList<SmApplSupportDoc>());

		for(SmApplSupportDoc applSuppDoc : this.getManagedBean().getSmAppl().getSmApplSupportDocs()){
			if(!applSuppDoc.isReceived()){

				this.getManagedBean().getUnselectedSmApplSuppDocs().add(applSuppDoc);
			}
		}
		retrieveBusinessTypeList();
		log.info("----------Supporting document-------------" +  this.getManagedBean().getSmAppl().getSmApplSupportDocs().size());

	}

	public String submitSDOReviewAction(String resAction) {

		if(resAction != null || resAction != ""){
			if(resAction.equalsIgnoreCase(SupplierManagementConst.BPM_INQUIRY)){
				if(this.getManagedBean().getSmApplInquiry().getInquirySubject() == null 
						|| this.getManagedBean().getSmApplInquiry().getInquirySubject() == ""){ 
					RequestContext.getCurrentInstance().update("inquiryMessageId");
					throw new EpException(MessageConst.ERROR_721104);
				}

				if(this.getManagedBean().getSmApplInquiry().getInquiryText() == null 
						|| this.getManagedBean().getSmApplInquiry().getInquiryText() == ""){
					RequestContext.getCurrentInstance().update("inquiryMessageId");
					throw new EpException(MessageConst.ERROR_721105);

				}
			} else if(resAction.equalsIgnoreCase(SupplierManagementConst.BPM_VERIFY)){
				if(this.getManagedBean().getSmAppl().getSdoReceivedDate() == null 
						|| this.getManagedBean().getSmAppl().getSdoReceivedDate().equals("")){ 
					RequestContext.getCurrentInstance().update("submissionMessageId");
					throw new EpException(MessageConst.ERROR_721106);
				}

				if(this.getManagedBean().getSmRemark().getRemark()== null || this.getManagedBean().getSmRemark().getRemark() == ""){
					RequestContext.getCurrentInstance().update("submissionMessageId");
					throw new EpException(MessageConst.ERROR_721107);
				}

			}

			try{
				String token = getFacesScopes().getBPMAuthenticationCtxToken();
				SdoReviewReqDTO reqDTO = new SdoReviewReqDTO();
				ISdoReviewSO reqSO = (ISdoReviewSO) BusinessDelegate.getInstance().getServiceObject("sm", ISdoReviewSO.class);
				reqDTO.setApplId(this.getManagedBean().getAppId());
				reqDTO.setOutcome(resAction);
				reqDTO.setTaskId(this.getManagedBean().getTaskId());

				log.info("-----------SDO----------" + reqDTO.getOutcome());
				log.info("-----------SDO Review----------" + reqDTO.getTaskId());

				//reqDTO.setBpmUserName(bpmUsrName);
				
				SmRemark smRmk = this.getManagedBean().getSmRemark();
				smRmk.setDocType(SupplierManagementConst.SM_RMK_DOC_TYPE_SVO);
				smRmk.setDocId(this.getManagedBean().getSmAppl().getApplId());
				
				reqDTO.setBpmPojo(this.getManagedBean().getBpmPojo());
				reqDTO.setSmAppl(this.getManagedBean().getSmAppl());
				reqDTO.setToken(token);
				reqDTO.setOutcome(resAction);
				reqDTO.setSmRemark(smRmk);
								
				this.getManagedBean().getSmApplInquiry().setSmAppl(this.getManagedBean().getSmAppl());
				reqDTO.setSmApplInquiry(this.getManagedBean().getSmApplInquiry());
				
				SdoReviewResDTO resDTO = reqSO.submitBPMSdoReview(reqDTO);

				if (resDTO.isSuccessful()) {
					RequestContext.getCurrentInstance().execute("inquiryDlg.hide()");
					RequestContext.getCurrentInstance().execute("submissionDlg.hide()");

					return navigateToAck();
				} else {
					throw new EpException(MessageConst.ERROR_721109);
				}
			}catch(Exception ex){
				throw new EpException(MessageConst.ERROR_721108);
			}
		}
		return null;
	}

	public void showSdoActionDialog(String action){
		if(action.equalsIgnoreCase(SupplierManagementConst.BPM_VERIFY)){
			RequestContext.getCurrentInstance().execute("submissionDlg.show()");
			
		}else if(action.equalsIgnoreCase(SupplierManagementConst.BPM_INQUIRY)){
			List<SmApplSupportDoc> unselectedAplSuppDocLs = this.getManagedBean().getUnselectedSmApplSuppDocs();

			if(this.getManagedBean().getUnselectedSmApplSuppDocs().size() > 0){
				StringBuilder enquiry = new StringBuilder("");
				for(int i= 0; i < unselectedAplSuppDocLs.size(); i++){
					int count = i + 1;
					enquiry.append(count + ". " + unselectedAplSuppDocLs.get(i).getPmDocument().getDocName());
					enquiry.append(System.getProperty("line.separator"));
				}
				System.out.println(">>>>Str>>>");
				System.out.println(enquiry);
				this.getManagedBean().getSmApplInquiry().setInquiryText(enquiry.toString());
			}
			RequestContext.getCurrentInstance().execute("inquiryDlg.show()");
		}
	}
	
	public void inquiryRowSelect(){
		log.info("*************inquiryRowSelect***********"+ this.getManagedBean().getSelectedInquiryResponse().getInquiryText());
		RequestContext.getCurrentInstance().update("resOutPanelId");

	}
	
	public void retrieveBusinessTypeList(){
		List<PmParameterDesc> pmDesc = new ArrayList<PmParameterDesc>();
		pmDesc = SmCommonUtil.retrieveParameterDescList(SupplierManagementConst.SUPPLIER_BUSINESS_TYPE, new FacesScopes().getUserLocale().getLanguage());
		
		System.out.println("**retrieveBusinessTypeList***" + pmDesc.size());
		this.getManagedBean().setBusinessTypeLs(pmDesc);
	}
	
	public String navigateToAck(){
		FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,MessageUtils.getMessage(MessageConst.INFO_721103,
		MessageUtils.BUNDLE_COMMON_MESSAGES,
		new FacesScopes().getUserLocale())));

		return CommonUIConst.ACKNOWLEDGE_PAGE;
	}

	//for local testing purpose
	public void preRenderView(){
		if(this.getManagedBean().getSmAppl() == null){
			viewSupportingDocument();
		}
	}
	
	public void receiveStatusChange(SmApplSupportDoc applSuppDoc){
		log.debug("-----applSuppDoc----" + applSuppDoc.getPmDocument().getDocName());
		List<SmApplSupportDoc> unselectedSuppDocLs = this.getManagedBean().getUnselectedSmApplSuppDocs();
		
		if(applSuppDoc.isReceived()){
			unselectedSuppDocLs.remove(applSuppDoc);
		}else{
			unselectedSuppDocLs.add(applSuppDoc);
		}
		log.debug("-----applSuppDoc size----" + unselectedSuppDocLs.size());
	}
	
	public String getsupplierApplTypeDesc(String applType){
		return SmCommonUtil.getApplTypeDesc(applType);
	}
	
	
}
