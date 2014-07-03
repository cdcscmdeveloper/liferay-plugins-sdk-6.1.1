/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.loa;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.sc.directpurchase.IPurchaseRequestSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.sc.directpurchase.PurchaseRequestReqDTO;
import my.ep.dto.sc.directpurchase.PurchaseRequestResDTO;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.directpurchasedata.SCDirectPurchaseDataType;
import my.ep.persistence.entity.sc.directpurchase.ScPurchaseRequest;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.managedbean.sc.loa.DpLOAManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

import com.liferay.portal.util.PortalUtil;

/**
 * @author kenny
 * @since 27.05.2014
 * 
 */
@ManagedBean
@RequestScoped
public class DpLOABackingBean extends
		SourcingBaseBackingBean<DpLOAManagedBean> {
	public static Logger log = EpLogger.getLogger(DpLOABackingBean.class);

	public DpLOABackingBean() {
		retrieveLOAList(3L);
	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
	}
	
	public void preRenderView() {
		debugLogger("preRenderView");
		
		FacesScopes facesScopes = new FacesScopes();
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(facesScopes.getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		
		String taskId = originalServletRequest.getParameter("taskId");
		
		log.info("taskId : " + taskId);

		IBpmSO bpmSO;
		
		try {
			bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
			
			BpmRequestDTO reqDTO = new BpmRequestDTO();
			reqDTO.setCtxToken(facesScopes.getBPMAuthenticationCtxToken());
			reqDTO.setTaskId(taskId);
			
			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			Object obj = res.getPojo();
			
			this.renewSesionObject();
			this.getManagedBean().setTaskId(taskId);
			this.getManagedBean().setOutComes(res.getAction());
			
			SCDirectPurchaseDataType directPurchaseDataType = (SCDirectPurchaseDataType) obj;
			retrieveLOAList(Long.valueOf(directPurchaseDataType.getDpId()));
			
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, QTConstants.DP_LOA_PAGE);
			
		} finally {
			bpmSO = null;
		}
	}

	/**
	 * retrieving LOI LOA List
	 */
	public void retrieveLOAList(Long requestNoteId) {
		debugLogger("retrieveLOAList");

		IPurchaseRequestSO purchaseRequestSO = null;
		try {
			purchaseRequestSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IPurchaseRequestSO.class);
			Long userId = new FacesScopes().getUserSessionBean().getUserId();
			
			PurchaseRequestReqDTO reqDTO = new PurchaseRequestReqDTO();
			reqDTO.setRequestNoteId(requestNoteId);
			List<String> fulfilmentTypes = new ArrayList<String>();
			fulfilmentTypes.add(ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN);
			fulfilmentTypes.add(ParameterConst.FULFILMENT_TYPE_SCHEDULED);
			reqDTO.setFulfillmentTypes(fulfilmentTypes);
			reqDTO.setAssignedTo(userId);

			PurchaseRequestResDTO resDTO = purchaseRequestSO.retrievePurchaseRequestsByReqNote(reqDTO);
			
			if (resDTO.isSuccessful()) {
				List<ScPurchaseRequest> purchaseRequestList = resDTO.getListPurchaseRequest();
				if (purchaseRequestList != null && purchaseRequestList.size() > 0) {
					ScPurchaseRequest purchaseRequest = purchaseRequestList.iterator().next();
					
					getManagedBean().setScRequestNote(purchaseRequest.getRequestNote());
					getManagedBean().setPurchaseRequestList(purchaseRequestList);	
				}	
			}

		} finally {
			purchaseRequestSO = null;
		}
	}
}
