/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.loa;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.sc.loiloa.LOIDetailBackingBean;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.loa.QtLOAManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;

// TODO: Auto-generated Javadoc
/**
 * The Class QtLOABackingBean.
 * @author kenny
 * @since 10/09/2014
 */
@ManagedBean
@RequestScoped
public class QtLOABackingBean extends SourcingBaseBackingBean<QtLOAManagedBean> {
	
	/** The log. */
	public static Logger log = EpLogger.getLogger(QtLOABackingBean.class);

	/**
	 * Instantiates a new qt loa backing bean.
	 */
	public QtLOABackingBean() {
	}

	/**
	 * Debug logger.
	 *
	 * @param message the message
	 */
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
	}

	/**
	 * render the view based on the task id
	 * page navigation to QT Loa List
	 */
	public void preRenderView() {
		debugLogger("preRenderView");
		try {
			SCAwardSupplierDataType dpDataType = getBpmTaskDetails(SCAwardSupplierDataType.class, null);

			Long sourceId = Long.valueOf(dpDataType.getSourceId());

			//Long sourceId = 146L;//LOA record Existed
			//Long sourceId = 3762L; // no LOA record

			LOADetailBackingBean loaDetailBackingBean = new LOADetailBackingBean();
			LOIDetailBackingBean lOIDetailBackingBean = new LOIDetailBackingBean();
			loaDetailBackingBean.renewSesionObject();
			lOIDetailBackingBean.renewSesionObject();

			loaDetailBackingBean.getManagedBean().setSourceId(sourceId);
			loaDetailBackingBean.getManagedBean().setPayLoad(dpDataType);

			lOIDetailBackingBean.getManagedBean().setSourceId(sourceId);
			lOIDetailBackingBean.getManagedBean().setPayLoad(dpDataType);

			retrieveLOAList(sourceId);
			if(getManagedBean().getSuppLoiLoaMap() != null && getManagedBean().getSuppLoiLoaMap().size()>0){
				loaDetailBackingBean.getManagedBean().setSuppLoiLoaMap(getManagedBean().getSuppLoiLoaMap());
			}

			generateSuppLoiLoaList();
			
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
			.handleNavigation(fc, null, QTConstants.QT_LOA_LIST_PAGE);
		} catch (Exception e) {
			handleException(e);
		}
	}

	/**
	 * retrieving LOI LOA List from database.
	 *
	 * @param qtId the qt id
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
				getManagedBean().setSuppLoiLoaMap(resDTO.getLoiLoaSupplierMap());
				if (loiLoaList != null && loiLoaList.size() > 0) {
					getManagedBean().setScLoiLoaList(loiLoaList);
					getManagedBean().setSelectedLoiLoa(loiLoaList.get(0));
					
					getLoiLoaListTitle(getManagedBean().getSelectedLoiLoa().getSmSupplier());
				}	
			}

		} finally {
			qtAwardSO = null;
		}
	}
	
	/**
	 * Generate supplier loi loa list based on selected loi loa in in Recommended Supplier List.
	 */
	public void generateSuppLoiLoaList() {
		IQTAwardSO qtAwardSO = null;
		try {
			qtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			AwardSupplierResDTO resDTO = null;
			List<Long> loiLoaList = new ArrayList<Long>();
			if (getManagedBean().getSelectedLoiLoa() != null) {
				Long supplierId = getManagedBean().getSelectedLoiLoa().getSmSupplier().getSupplierId();

				for(ScLoiLoa loiLoa : getManagedBean().getSuppLoiLoaMap().get(supplierId)) {
					loiLoaList.add(loiLoa.getLoiLoaId());
				}
				
				reqDTO.setLoiLoaIds(loiLoaList);
				resDTO = qtAwardSO.getLoiAndLoaByLoiLoaList(reqDTO);
				if(!CollectionUtils.isEmpty(resDTO.getScLoas()) ||
						!CollectionUtils.isEmpty(resDTO.getScLois())) {
					getManagedBean().setSuppLoiLoaList(
							prepareLoiLoaList(resDTO.getScLois(), resDTO.getScLoas()));
				}
				else {
					getManagedBean().setSuppLoiLoaList(new ArrayList<SuppLoiLoaBean>());
				}
				getLoiLoaListTitle(getManagedBean().getSelectedLoiLoa().getSmSupplier());/*
				for(ScLoiLoa loiLoa : getManagedBean().getSuppLoiLoaMap().get(supplierId)) {
					for (ScLoi scLoi : loiLoa.getScLois()) {
						suppLoaLoi = new SuppLoiLoaBean();
						suppLoaLoi.setLoiLoaNo(scLoi.getLoiNo());
						suppLoaLoi.setType(SourcingConstants.SUPP_LOI_TYPE);
						suppLoaLoi.setStatus(scLoi.getScWorkFlowStatus().getPmStatus());
						suppLoaLoi.setScLoi(scLoi);
						suppLoiLoaList.add(suppLoaLoi);
					}

					for (ScLoa scLoa : loiLoa.getScloas()) {
						suppLoaLoi = new SuppLoiLoaBean();
						suppLoaLoi.setLoiLoaNo(scLoa.getLoaNo());
						suppLoaLoi.setType(SourcingConstants.SUPP_LOA_TYPE);
						suppLoaLoi.setStatus(scLoa.getPmStatus());
						reqDTO.setLoaId(scLoa.getLoaId());
						resDTO = qtAwardSO.retrieveLOADtlByLoaId(reqDTO);
						suppLoaLoi.setScLoa(resDTO.getScLoa());
						suppLoiLoaList.add(suppLoaLoi);
					}
				}
*/
				getLoiLoaListTitle(getManagedBean().getSelectedLoiLoa().getSmSupplier());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(e.toString());
		} finally {
			qtAwardSO = null;
		}
	}
	
	/**
	 * On row rec supplier action.
	 *
	 * @param event the event
	 */
	public void onRowRecSupplierAction(SelectEvent event) {
		IQTAwardSO qtAwardSO = null;
		ScLoiLoa scLoiLoa = (ScLoiLoa) event.getObject();
		try {
			qtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
			
			SmSupplier supplier = scLoiLoa.getSmSupplier();
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			getLoiLoaListTitle(supplier);
			
			List<Long> loiLoaList = new ArrayList<Long>();
			
			Long supplierId = supplier.getSupplierId();
			for(ScLoiLoa loiLoa : getManagedBean().getSuppLoiLoaMap().get(supplierId)) {
				loiLoaList.add(loiLoa.getLoiLoaId());
			}
			
			reqDTO.setLoiLoaIds(loiLoaList);
			AwardSupplierResDTO resDTO = qtAwardSO.getLoiAndLoaByLoiLoaList(reqDTO);
			if(!CollectionUtils.isEmpty(resDTO.getScLoas()) ||
					!CollectionUtils.isEmpty(resDTO.getScLois())) {
				getManagedBean().setSuppLoiLoaList(
						prepareLoiLoaList(resDTO.getScLois(), resDTO.getScLoas()));
			}
			else {
				getManagedBean().setSuppLoiLoaList(new ArrayList<SuppLoiLoaBean>());
			}
			getLoiLoaListTitle(getManagedBean().getSelectedLoiLoa().getSmSupplier());
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(e.toString());
		} finally {
			qtAwardSO = null;
		}
	}
	
	private List<SuppLoiLoaBean> prepareLoiLoaList(List<ScLoi> loiList, List<ScLoa> loaList) {
		List<SuppLoiLoaBean> suppLoiLoaList = new ArrayList<>();
		SuppLoiLoaBean suppLoaLoi = null;
		for (ScLoa scLoa : loaList) {
			suppLoaLoi = new SuppLoiLoaBean();
			suppLoaLoi.setLoiLoaNo(scLoa.getLoaNo());
			suppLoaLoi.setType(SourcingConstants.SUPP_LOA_TYPE);
			suppLoaLoi.setStatus(scLoa.getPmStatus());
			suppLoaLoi.setScLoa(scLoa);
			suppLoiLoaList.add(suppLoaLoi);
		}

		for (ScLoi scLoi : loiList) {
			suppLoaLoi = new SuppLoiLoaBean();
			suppLoaLoi.setLoiLoaNo(scLoi.getLoiNo());
			suppLoaLoi.setType(SourcingConstants.SUPP_LOI_TYPE);
			suppLoaLoi.setStatus(scLoi.getScWorkFlowStatus().getPmStatus());
			suppLoaLoi.setScLoi(scLoi);
			suppLoiLoaList.add(suppLoaLoi);
		}
		return suppLoiLoaList;
	}
	
	/**
	 * Gets the clauses title.
	 *
	 * @param deduction the deduction
	 * @return the clauses title
	 */
	private void getLoiLoaListTitle(SmSupplier smSupplier){
		StringBuilder title  = new StringBuilder();
		title.append("Title");
		title.append(":");
		title.append(" ");
		if(smSupplier != null){
			title.append(smSupplier.getCompanyName());
			title.append(">"); 
			title.append(" ");
		}
		Locale locale = new FacesScopes().getUserLocale();
		title.append(ResourceBundleUtils.getMessage("lbl.sc.loiloa.loiloalist",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
		getManagedBean().setLoiLoaListTitle(title.toString());
	}
	
	/**
	 * Show item details in item details pop up.
	 *
	 * @param suppLoiLoaBean the supp loi loa bean
	 */
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
			e.printStackTrace();
			throw new EpException(e.toString());
		} finally {
			qtAwardSO = null;
		}
	}
}