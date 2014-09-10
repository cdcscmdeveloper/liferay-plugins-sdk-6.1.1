/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.loa;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.sc.directpurchase.IPurchaseRequestSO;
import my.ep.biz.service.sc.quationtender.IQTAwardSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.sc.directpurchase.PurchaseRequestReqDTO;
import my.ep.dto.sc.directpurchase.PurchaseRequestResDTO;
import my.ep.dto.sc.quationtender.AwardSupplierReqDTO;
import my.ep.dto.sc.quationtender.AwardSupplierResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.sc.directpurchase.ScPurchaseRequest;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoa;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.managedbean.sc.loa.DpLOAManagedBean;
import my.ep.web.managedbean.sc.loa.QtLOAManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@ManagedBean
@RequestScoped
public class QtLOABackingBean extends SourcingBaseBackingBean<QtLOAManagedBean> {
	public static Logger log = EpLogger.getLogger(QtLOABackingBean.class);

	public QtLOABackingBean() {
	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
	}

	public void preRenderView() {
		debugLogger("preRenderView");
		try {
			Long sourceId = 1450L;//LOA record Existed
			//Long sourceId = 3025L; // no LOA record
			LOADetailBackingBean loaDetailBackingBean = new LOADetailBackingBean();
			loaDetailBackingBean.renewSesionObject();
			loaDetailBackingBean.getManagedBean().setSourceId(sourceId);
			retrieveLOAList(sourceId);
			
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, QTConstants.QT_LOA_LIST_PAGE);
		} catch (Exception e) {
			handleException(e);
		}
	}
	
	/**
	 * retrieving LOI LOA List
	 */
	public void retrieveLOAList(Long qtId) {
		debugLogger("retrieveLOAList");

		IQTAwardSO qtAwardSO = null;
		try {
			qtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
			
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setQtId(qtId);
			reqDTO.setDocType("QT");

			AwardSupplierResDTO resDTO = qtAwardSO.retrieveLOILOAListByDocTypeAndQtId(reqDTO);
			
			if (resDTO.isSuccessful()) {
				getManagedBean().setQt(resDTO.getScQt());
				List<ScLoiLoa> loiLoaList = resDTO.getLoiLoas();
				if (loiLoaList != null && loiLoaList.size() > 0) {
					getManagedBean().setScLoiLoaList(loiLoaList);
				}	
			}

		} finally {
			qtAwardSO = null;
		}
	}
}