/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.loa;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.sc.quationtender.IQTAwardSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.dto.sc.quationtender.AwardSupplierReqDTO;
import my.ep.dto.sc.quationtender.AwardSupplierResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.awardsupplierdata.SCAwardSupplierDataType;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoi;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoa;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
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
			SCAwardSupplierDataType dpDataType = getBpmTaskDetails(SCAwardSupplierDataType.class);
			
			Long sourceId = Long.valueOf(dpDataType.getSourceId());
			
			//Long sourceId = 146L;//LOA record Existed
			//Long sourceId = 3762L; // no LOA record
			
			LOADetailBackingBean loaDetailBackingBean = new LOADetailBackingBean();
			loaDetailBackingBean.renewSesionObject();
			loaDetailBackingBean.getManagedBean().setSourceId(sourceId);
			loaDetailBackingBean.getManagedBean().setPayLoad(dpDataType);
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
			reqDTO.setDocId(qtId);
			reqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);

			AwardSupplierResDTO resDTO = qtAwardSO.retrieveLOILOAList(reqDTO);
			
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
	
	public void generateSuppLoiLoaList() {
		IQTAwardSO qtAwardSO = null;
		try {
			qtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
			SuppLoiLoaBean suppLoaLoi = null;
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			AwardSupplierResDTO resDTO = null;
			List<SuppLoiLoaBean> suppLoiLoaList = new ArrayList<>();
			if (getManagedBean().getSelectedLoiLoa() != null) {
				for (ScLoi scLoi : getManagedBean().getSelectedLoiLoa()
						.getScLois()) {
					suppLoaLoi = new SuppLoiLoaBean();
					suppLoaLoi.setLoiLoaNo(scLoi.getLoiNo());
					suppLoaLoi.setType(SourcingConstants.SUPP_LOI_TYPE);
					suppLoaLoi.setStatus(scLoi.getPmStatus());
					suppLoaLoi.setScLoi(scLoi);
					suppLoiLoaList.add(suppLoaLoi);
				}

				for (ScLoa scLoa : getManagedBean().getSelectedLoiLoa()
						.getScloas()) {
					suppLoaLoi = new SuppLoiLoaBean();
					suppLoaLoi.setLoiLoaNo(scLoa.getLoaNo());
					suppLoaLoi.setType(SourcingConstants.SUPP_LOA_TYPE);
					suppLoaLoi.setStatus(scLoa.getPmStatus());
					reqDTO.setLoaId(scLoa.getLoaId());
					resDTO = qtAwardSO.retrieveLOADtlByLoaId(reqDTO);
					suppLoaLoi.setScLoa(resDTO.getScLoa());
					suppLoiLoaList.add(suppLoaLoi);

				}

				getManagedBean().setSuppLoiLoaList(suppLoiLoaList);
			}
		} catch (Exception e) {
			throw new EpException(e.toString());
		} finally {
			qtAwardSO = null;
		}
	}
	
	public void showItemDetailsPopUps(SuppLoiLoaBean suppLoiLoaBean) {
		IQTAwardSO qtAwardSO = null;
		try {
			qtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);

			String language = new FacesScopes().getUserLocale().getLanguage();

			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			AwardSupplierResDTO resDTO = null;
			reqDTO.setLanguageCode(language);

			if (suppLoiLoaBean.getScLoa() != null) {
				reqDTO.setScLoa(suppLoiLoaBean.getScLoa());
				resDTO = qtAwardSO.retrieveLOADtl(reqDTO);
			} else if (suppLoiLoaBean.getScLoi() != null) {
				reqDTO.setScLoi(suppLoiLoaBean.getScLoi());
				resDTO = qtAwardSO.retrieveLOIDtl(reqDTO);
			}

			if (resDTO != null) {
				if (resDTO.getScLoa() != null) {
					suppLoiLoaBean.setScLoa(resDTO.getScLoa());
					suppLoiLoaBean.setScLoiLoaItems(resDTO.getScLoa()
							.getScLoiLoaItems());
				} else if (resDTO.getScLoi() != null) {
					suppLoiLoaBean.setScLoi(resDTO.getScLoi());
					suppLoiLoaBean.setScLoiLoaItems(resDTO.getScLoi()
							.getScLoiLoaItems());
				}
			}

			getManagedBean().setSelectedSuppLoiLoaBean(suppLoiLoaBean);

		} catch (Exception e) {
			throw new EpException(e.toString());
		} finally {
			qtAwardSO = null;
		}
	}
}