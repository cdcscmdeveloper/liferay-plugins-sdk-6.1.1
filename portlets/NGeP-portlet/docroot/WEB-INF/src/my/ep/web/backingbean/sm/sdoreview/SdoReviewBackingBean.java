package my.ep.web.backingbean.sm.sdoreview;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.service.sm.sdoreview.ISdoReviewSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.dto.sm.sdoreview.SdoReviewReqDTO;
import my.ep.dto.sm.sdoreview.SdoReviewResDTO;
import my.ep.jaxb.sm.mofregdata.SMMOFRegDataType;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.sdoreview.SdoReviewManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@ManagedBean(name = "sdoReviewBackingBean")
@RequestScoped
public class SdoReviewBackingBean extends BaseBackingBean<SdoReviewManagedBean> {

	public static Logger log = Logger.getLogger(SdoReviewBackingBean.class);

	public SdoReviewBackingBean() {

	}

	public void viewSupportingDocument() {
		//this.getManagedBean().setAppId(22);
		log.info("----------appID------------" + this.getManagedBean().getAppId());
		
		SdoReviewReqDTO reqDTO = new SdoReviewReqDTO();
		ISdoReviewSO reqSO = (ISdoReviewSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", ISdoReviewSO.class);
		reqDTO.setApplId(this.getManagedBean().getAppId());
		SdoReviewResDTO resDTO = reqSO.retrieveApplById(reqDTO);
//		
//		String[] arr = new String[2]; // 10 is the length of array.
//		arr[0] = "INQUIRY";
//		arr[1] = "VERIFY";
//		
//		this.getManagedBean().setBpmAction(arr);
		this.getManagedBean().setSmAppl(resDTO.getSmAppl());
		log.info("----------------------supportingDocMode-------------------------"
				+ resDTO.getSmAppl().getSupportingDocMode());

		
	}

	public void submitSDOReviewAction(String res) {

		if (res.equalsIgnoreCase("VERIFIED")) {
			log.info("----------------------submitSDOReviewAction-------------------------"
					+ res);
			SdoReviewReqDTO reqDTO = new SdoReviewReqDTO();
			ISdoReviewSO reqSO = (ISdoReviewSO) BusinessDelegate.getInstance()
					.getServiceObject("sm", ISdoReviewSO.class);
			reqDTO.setApplId(this.getManagedBean().getAppId());
			reqDTO.setOutcome(res);
			reqDTO.setSuppId(this.getManagedBean().getSmAppl().getSmSupplier().getSupplierId());
			reqDTO.setAppType(this.getManagedBean().getSmAppl().getApplType());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			
			if(this.getManagedBean().getSmAppl().getSupportingDocMode() == "S"){
				reqDTO.setOnlineDoc(true);
			}else if(this.getManagedBean().getSmAppl().getSupportingDocMode() == "H"){
				reqDTO.setOnlineDoc(false);
			}
			
			SdoReviewResDTO resDTO = reqSO.submitBPMSdoReview(reqDTO);

		}

		// if (res.equalsIgnoreCase("VERIFIED")) {
		// log.info("-----------Task Id------------------" +
		// this.getManagedBean().getTaskId());
		// System.out.println("initiateTask******");
		// IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
		// .getServiceObject(IBpmSO.class);
		// String ctx = bpmSO.authenticate("khai", "welcome1");
		// BpmRequestDTO reqDTO = new BpmRequestDTO();
		//
		// reqDTO.setCtxToken(ctx);
		// reqDTO.setProcessId("Supplier_Management.MOFSupplierRegistration");
		// reqDTO.setTaskId(this.getManagedBean().getTaskId());
		// reqDTO.setOutcome(res);
		//
		// log.info("-----------reqDTO Task Id------------------" +
		// reqDTO.getTaskId());
		// SMMOFRegDataType smObj = new SMMOFRegDataType();
		// smObj.setAppId(String.valueOf(this.getManagedBean().getAppId()));
		// reqDTO.setPojo(smObj);
		//
		// //BpmResponseDTO bpmRes = bpmSO.initiateAndUpdateTask(reqDTO);
		// BpmResponseDTO bpmRes = bpmSO.updateTask(reqDTO);
		//
		// log.info("-----------Token------------------" + ctx);
		// log.info("-----------After Intial-Task Id------------------" +
		// bpmRes.getTaskId());
		// log.info("-----------isSuccessful------------"
		// + bpmRes.isSuccessful());
		// log.info("-----------ErrorReason------------"
		// + bpmRes.getErrorReason());
		// }
	}
}
