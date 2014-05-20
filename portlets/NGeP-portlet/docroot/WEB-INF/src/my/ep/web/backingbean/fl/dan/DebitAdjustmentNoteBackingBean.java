/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.dan;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.fl.adjustment.IAdjustmentSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.fl.dandata.FLDanDataType;
import my.ep.persistence.entity.fl.adjust.FlAdjustment;
import my.ep.persistence.entity.fl.adjust.FlAdjustmentItem;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.managedbean.fl.dan.DebitAdjustmentNoteManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.vo.ChargeLineVO;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.liferay.portal.util.PortalUtil;

/**
 * Managed Bean for Debit Adjustment Note
 * 
 * @author KK LIM
 * @since 1/11/2013
 *
 */
@ManagedBean(name = "danBackingBean")
@RequestScoped
public class DebitAdjustmentNoteBackingBean extends BaseBackingBean<DebitAdjustmentNoteManagedBean> {
	private static Logger log = Logger.getLogger(DebitAdjustmentNoteBackingBean.class);
	/**
	 * get faces scopes
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	public String submitAction(String action) throws ParseException {
		System.out.println("--DAN submit--");
		IAdjustmentSO adjustmentSO = null;
		try {
			String token = getFacesScopes().getBPMAuthenticationCtxToken();
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlAdjustment adjustment = getManagedBean().getAdjustment();
			adjustmentSO = (IAdjustmentSO) BusinessDelegate.getInstance().getServiceObject("fl", IAdjustmentSO.class);
			reqDTO.setAdjustment(adjustment);
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)){
				action = "";
			}
			reqDTO.setOutcome(action);
			reqDTO.setTaskId(getManagedBean().getTaskId());
			reqDTO.setToken(token);
			FulfilmentResDTO resDTO = adjustmentSO.saveAdjustment(reqDTO);
			if(resDTO.isSuccessful()) {
				log.info("Record saved successfully");
				String message = "Adjustment Id. : "+adjustment.getAdjustmentId();
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
			adjustmentSO = null;
		}
		return null;
	}
	
	public void preRenderView() {
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		System.out.println("taskId:" + taskId);
		if(taskId != null){


			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
					.getServiceObject(IBpmSO.class);
			String ctx = getFacesScopes().getBPMAuthenticationCtxToken();
			BpmRequestDTO reqDTO = new BpmRequestDTO();

			reqDTO.setCtxToken(ctx);
			reqDTO.setTaskId(taskId);
			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			Object obj = res.getPojo();

			FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
			FulfilmentResDTO resDTO = new FulfilmentResDTO();
			FLDanDataType danObj = (FLDanDataType)obj;
			System.out.println("danObj.getDanId(): "+danObj.getDanId());
			flReqDTO.setDanId(danObj.getDanId());
			IAdjustmentSO adjustmentSO = (IAdjustmentSO) BusinessDelegate.getInstance().getServiceObject("fl",IAdjustmentSO.class);

			resDTO = adjustmentSO.retrieveAdjustmentById(flReqDTO);
			FlAdjustment adjustment = resDTO.getAdjustment();
			populateChargeLineTree(adjustment);
			DebitAdjustmentNoteBackingBean danBB = new DebitAdjustmentNoteBackingBean();
			danBB.getManagedBean().setAdjustment(adjustment);
			danBB.getManagedBean().setTaskId(taskId);
			danBB.getManagedBean().setOutComes(res.getAction());

		}
	}
	
	private void populateChargeLineTree(FlAdjustment adjustment) {
		Map<Long, TreeNode> chargeLineMap = new HashMap<Long, TreeNode>();

		TreeNode root = getManagedBean().getDanChargeLineTree();
		root = new DefaultTreeNode("root", null);
		TreeNode parent = null;
		TreeNode child = null;
		BigDecimal sumOrderedAmt = new BigDecimal(0);
		BigDecimal sumInvoiceAmt = new BigDecimal(0);
		BigDecimal sumDanAmt = new BigDecimal(0);
		
		
		for(FlAdjustmentItem adjustmentItem : adjustment.getFlAdjustmentItem()){
			if(chargeLineMap.containsKey(adjustmentItem.getFlItem().getCmItem().getItemId())){
				parent = chargeLineMap.get(adjustmentItem.getFlItem().getCmItem().getItemId());
				child = new DefaultTreeNode(
						new ChargeLineVO("","","",adjustmentItem.getFlItem().getUnitPrice(),adjustmentItem.getFlInvoiceItem().getOrderedAmt(),adjustmentItem.getFlInvoiceItem().getInvAmt(),adjustmentItem.getAdjustedAmt()),parent);
				
				ChargeLineVO chargeLineVO = (ChargeLineVO)parent.getData();
				sumOrderedAmt = chargeLineVO.getOrderedAmt().add(adjustmentItem.getFlInvoiceItem().getOrderedAmt());
				sumInvoiceAmt = chargeLineVO.getInvoiceAmt().add(adjustmentItem.getFlInvoiceItem().getInvAmt());
				sumDanAmt = chargeLineVO.getDanAmt().add(adjustmentItem.getAdjustedAmt());
				chargeLineVO.setOrderedAmt(sumOrderedAmt);
				chargeLineVO.setInvoiceAmt(sumInvoiceAmt);
				chargeLineVO.setDanAmt(sumDanAmt);
			}else{
				parent = new DefaultTreeNode(
						new ChargeLineVO(adjustmentItem.getFlItem().getCmItem().getExtensionCode(),adjustmentItem.getFlItem().getCmItem().getItemName(),"",adjustmentItem.getFlItem().getUnitPrice(),adjustmentItem.getFlInvoiceItem().getOrderedAmt(),adjustmentItem.getFlInvoiceItem().getInvAmt(),adjustmentItem.getAdjustedAmt()),root);
				child = new DefaultTreeNode(
						new ChargeLineVO("","","",adjustmentItem.getFlItem().getUnitPrice(),adjustmentItem.getFlInvoiceItem().getOrderedAmt(),adjustmentItem.getFlInvoiceItem().getInvAmt(),adjustmentItem.getAdjustedAmt()),parent);
				chargeLineMap.put(adjustmentItem.getFlItem().getCmItem().getItemId(),parent);
			}
		}
		getManagedBean().setDanChargeLineTree(root);
	}

}
