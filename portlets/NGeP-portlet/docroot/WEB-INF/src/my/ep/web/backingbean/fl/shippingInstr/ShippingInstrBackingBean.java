/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.shippingInstr;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.biz.service.fl.request.IShippingInstructionSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constants.FLProcessNameConst;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.fl.request.FlShippingInstr;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.managedbean.fl.shippingInstr.ShippingInstrManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

import com.liferay.portal.util.PortalUtil;

/**
 * Backing Bean for Shipping Instruction
 * 
 * @author KK LIM
 * @since 28/10/2013
 */

@ManagedBean(name = "shipInstrBackingBean")
@RequestScoped
public class ShippingInstrBackingBean extends BaseBackingBean<ShippingInstrManagedBean>{
	private static Logger log = Logger.getLogger(ShippingInstrBackingBean.class);
	public static final String NAV_TO_CREATE_CONTRACT_REQUEST = "/xhtml/fl/contractrequest/crBasePage.xhtml";
	/**
	 * get faces scopes
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	public ShippingInstrBackingBean(){

	}
	
	/**
	 * save,submit shipping instruction.
	 * <p>submit - save data into database and BPM process will route to the next respective user.
	 * <p>save - save data into database.
	 * @param action the action
	 * navigate to acknowledge page.
	 */

	public String submitShippingAction(String action){
		System.out.println("-- shipping Instr --");
		IShippingInstructionSO shippingInstrSO = null;
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		try {
			Object token = getFacesScopes().getBPMAuthenticationCtxToken();
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlShippingInstr shippingInstr = getManagedBean().getShippingInstr();
			shippingInstrSO = (IShippingInstructionSO) BusinessDelegate.getInstance().getServiceObject("fl",IShippingInstructionSO.class);
			reqDTO.setShippingInstr(shippingInstr);
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)){
				action = "";
			}
			reqDTO.setOutcome(action);
			reqDTO.setTaskId(getManagedBean().getTaskId());
			reqDTO.setToken(token);
			reqDTO.setFlAttachmentList(flBB.getManagedBean().getAttachmentList());
			reqDTO.setFlRemark(flBB.getManagedBean().getRemarkList().get(0));
			FulfilmentResDTO resDTO = shippingInstrSO.saveShippingInstruction(reqDTO);

			if(resDTO.isSuccessful()) {
				log.info("Record saved successfully");
				String message = "Shipping Instr Id. : "+shippingInstr.getShippingInstrId();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Record saved successfully.",message));
				if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { // save action will remain in the same page
					renewSesionObject();
					return "/xhtml/shared/acknowledge.xhtml";
				}
			} else {
				throw new EpException("Failed.");
			}
		} catch (Exception e) {
			log.error("Exception>>", e);
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));

		}finally {
			shippingInstrSO = null;
		}
		return null;
	}

	/**
	 * Pre render view.
	 */
	public void preRenderView() {
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		System.out.println("taskId:" + taskId);

		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);
		Object ctx = getFacesScopes().getBPMAuthenticationCtxToken();
		BpmRequestDTO reqDTO = new BpmRequestDTO();

		reqDTO.setCtxToken(ctx);
		reqDTO.setTaskId(taskId);
		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);

		IFulfilmentRequestSO requestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentRequestSO.class);
		IShippingInstructionSO shippingSO = (IShippingInstructionSO) BusinessDelegate.getInstance().getServiceObject("fl", IShippingInstructionSO.class);
		FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();

		if(res.getTaskName().equalsIgnoreCase(FLProcessNameConst.REVISE_SI_HT) || res.getTaskName().equalsIgnoreCase(FLProcessNameConst.REVIEW_SI_HT)){
			resDTO = shippingSO.retrieveShippingInstruction(flReqDTO);
		}else{
			resDTO = requestSO.retrieveFulfilmentRequest(flReqDTO);
		}
		if(res.getTaskName().equalsIgnoreCase(FLProcessNameConst.REVISE_SI_HT)|| res.getTaskName().equalsIgnoreCase(FLProcessNameConst.REVIEW_SI_HT)){
			getManagedBean().setShippingInstr(resDTO.getShippingInstr());
			getManagedBean().setFlRequest(resDTO.getShippingInstr().getFlFulfilmentRequest());
			getManagedBean().setTaskId(taskId);
			getManagedBean().setOutComes(res.getAction());
		}else if (res.getTaskName().equalsIgnoreCase(FLProcessNameConst.CREATE_SI_HT)){
			FlShippingInstr flSi = new FlShippingInstr();
			flSi.setFlFulfilmentRequest(resDTO.getFlFulfilmentRequest());
			ContractReqDTO contractReqDTO = new ContractReqDTO();
			contractReqDTO.setContractId(resDTO.getFlFulfilmentRequest().getCtContract().getContractId());

			getManagedBean().setShippingInstr(flSi);
			getManagedBean().setTaskId(taskId);
			getManagedBean().setOutComes(res.getAction());
		}
		
		// Get Remark and Attachment
		
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			flBB.getManagedBean().setRemarkList(resDTO.getRemarkList());
			flBB.getManagedBean().setAttachmentList(resDTO.getAttachmentList());
			flBB.initializeRemarkAndAttachement(flBB.getManagedBean().getRemarkList(), flBB.getManagedBean().getAttachmentList());
	}
	
	/**
	 * Page navigation to FLDoc search page in ShippingInstr
	 */
	public String backToPervious() {
		return "flDocSearch";
	}

}
