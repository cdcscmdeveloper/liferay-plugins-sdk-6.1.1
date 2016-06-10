/**
 * Copyright © 2016 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.supplierintivitation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.service.sc.supplierintivitation.ISCSupplierInvitationSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.sc.DPStatusConstants;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.dto.sc.supplierintivitation.ScSupplierInvitationReqDTO;
import my.ep.dto.sc.supplierintivitation.ScSupplierInvitationResDTO;
import my.ep.type.aut.SearchOperator;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.sc.supplierinvitation.SCSupplierInvitationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;

/**
 * @author Hemanth Katta
 *
 */
@ManagedBean(name = "scSupplirInvitationdBB")
@ViewScoped
public class SCSupplierInvitationBackingBean extends  BaseBackingBean<SCSupplierInvitationManagedBean>{
	
	private static Logger log = Logger.getLogger(SCSupplierInvitationBackingBean.class);
	private IPagingDataModel<Object[]> supplierInvitationModel = new SupplierInvitationDataModel();
	private static final String RESULT_PAGE = "/xhtml/sc/supplierinvitation/resultsView.xhtml";
	private static final String BIDDING_LABEL_KEY = "lbl.sc.bidding";
	private static final String SQ_LABEL_KEY = "lbl.sc.sq";
	private static final String QT_LABEL_KEY = "lbl.sc.quotation.tender";
	private static final String QUOTATION_LABEL_KEY = "lbl.sc.quotation";
	private static final String TENDER_LABEL_KEY = "lbl.sc.tender";
	private static final String ALL_LABEL_KEY = "lbl.txt.all";
	private static final String BSV_LABEL_KEY = "lbl.sc.qt.briefing.sv";
	
	@PostConstruct
	private void init() {
		
		if (this.getManagedBean().getUrlMap() == null) {
			
			Map<Long, String> urlMap = new LinkedHashMap<Long, String>();
			urlMap.put(QTStatusConst.QT_PROPOSAL_PREPARATION,
					SourcingConstants.NAV_PAGE_SUPPLIER_PROPOSAL);
			urlMap.put(QTStatusConst.QT_BRIEFING_SITE_VISIT,
					SourcingConstants.NAV_PAGE_BSV_REGISTRATION);
			urlMap.put(QTStatusConst.SF_PENDING_BIDDING,
					SourcingConstants.NAV_PAGE_ONLINE_BIDDING
							+ SourcingConstants.NAV_PARAM_BID_SCHEDULE_ID
							+ SourcingConstants.NAV_PARAM_SEPARATOR);
			urlMap.put(DPStatusConstants.SQ_PENDING_SUPPLIER_RESPONSE,
					SourcingConstants.NAV_PAGE_SUPPLIER_RESPONSE_SIMPLE_QUOTE);
			this.getManagedBean().setUrlMap(urlMap);
		}
	}
	
	public void preRenderView() {
		
		log.info("preRenderView");
		
		supplierInvitationModel.reloadData();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication()
				.getNavigationHandler().handleNavigation(fc, null,RESULT_PAGE);
	}
	
	private class SupplierInvitationDataModel extends PagingDataModel<Object[]> {
		
		public SupplierInvitationDataModel() {
			super("scSuppInviteTblId");
		}
		@Override
		public ISearchResultBean<Object[]> pageChangeListener(
				ISearchBean<Object[]> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			
			Long selectedInvitationType = getManagedBean().getSelectedInvitationType();
			ScSupplierInvitationReqDTO reqDTO = new ScSupplierInvitationReqDTO();
			// BSV
			if(QTStatusConst.QT_BRIEFING_SITE_VISIT.equals(selectedInvitationType)){
				
				reqDTO.setStatusId(QTStatusConst.QT_BRIEFING_SITE_VISIT);
			} else if(QTStatusConst.QT_PROPOSAL_PREPARATION.equals(selectedInvitationType)){
				// QT Proposal
				
				List<Long> qtStatusIds = new ArrayList<Long>();
				qtStatusIds.add(QTStatusConst.QT_PROPOSAL_PREPARATION);
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("qtStatus.STATUS_ID",
								SearchOperator.OPERATOR_IN.value(), qtStatusIds));
				
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("qt.CLOSING_DATE",
								SearchOperator.OPERATOR_GREATER_EQUAL.value(),new Date()));
				
				searchBean.getSortColumns().add(
						new SearchOrderBean(
								"to_char(qt.PUBLISH_DATE,'yyyy-MM-dd')", false));
				
			} else if(DPStatusConstants.SQ_PENDING_SUPPLIER_RESPONSE.equals(selectedInvitationType)){
				// Simple Quote
				
				List<Long> sqStatusIds = new ArrayList<Long>();
				sqStatusIds.add(DPStatusConstants.SQ_PENDING_SUPPLIER_RESPONSE);
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("sqStatus.STATUS_ID",
								SearchOperator.OPERATOR_IN.value(), sqStatusIds));
				
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("quote.END_DATE",
								SearchOperator.OPERATOR_GREATER_EQUAL.value(),new Date()));
				
				searchBean.getSortColumns().add(
						new SearchOrderBean(
								"to_char(quote.CREATED_DATE,'yyyy-MM-dd')", false));
				
			} else if(QTStatusConst.SF_PENDING_BIDDING.equals(selectedInvitationType)){
				// Bidding
				
				List<Long> bidStatusIds = new ArrayList<Long>();
				bidStatusIds.add(QTStatusConst.SF_PENDING_BIDDING);
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("bidStatus.STATUS_ID",
								SearchOperator.OPERATOR_IN.value(), bidStatusIds));
				
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("bidschedule.END_DATE",
								SearchOperator.OPERATOR_GREATER_EQUAL.value(),new Date()));
								
				searchBean.getSortColumns().add(
						new SearchOrderBean(
								"to_char(bidschedule.START_DATE,'yyyy-MM-dd')", false));
				
			}
			
			ISCSupplierInvitationSO invitationSO = null;
			ScSupplierInvitationResDTO resDTO = new ScSupplierInvitationResDTO();
			try {
				invitationSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,ISCSupplierInvitationSO.class);
				Long userId = new FacesScopes().getUserSessionBean().getUserId();
				if(userId != null){
					reqDTO.setUserId(userId);
					reqDTO.setSearchBean(searchBean);
					resDTO = invitationSO.retrieveSupplierInviteQtsByUserId(reqDTO);
				}
				
			} finally{
				invitationSO = null;
			}
			
			return resDTO.getSupplierInvitationList();
		}
	}

	
	/**
	 * Get the URL based on status id
	 * 
	 * @param statusId
	 * @param id
	 * @return url string
	 */
	public void getNavigationUrl(Long statusId,Long id){
		
		log.info("getNavigationUrl");
		
		FacesScopes facesScope = new FacesScopes();
		ThemeDisplay themeDisplay = (ThemeDisplay) facesScope
				.getRequestScope(WebKeys.THEME_DISPLAY);
		try {
			
			String currentURL = themeDisplay.getURLCurrent();
			String friendlyURL = themeDisplay.getLayout().getGroup().getFriendlyURL();
			String commonUrl = currentURL.substring(0,currentURL.indexOf(
					friendlyURL)+ friendlyURL.length());
			String screenUrl = this.getManagedBean().getUrlMap().get(statusId);
			StringBuilder navigationUrl = new StringBuilder();
			navigationUrl.append(commonUrl).append("/")
			         .append(screenUrl).append(id);
			ExternalContext externalContext = FacesContext.getCurrentInstance()
					.getExternalContext();
			externalContext.redirect(navigationUrl.toString());
			log.info("getNavigationUrl =="+navigationUrl.toString());
		} catch (IOException e) {
			log.error(e);
		} catch (PortalException e) {
			log.error(e);
		} catch (SystemException e) {
			log.error(e);
		}
			
	}
	
	/**
	 * Returns label value based on status id
	 * 
	 * @param statusId
	 * @return label labelValue
	 */
	public String labelValue(Long statusId,String ProcurementMode) {
		
		String labelValue = "";
		if (QTStatusConst.SF_PENDING_BIDDING.equals(statusId)) {
			labelValue = getLabelName(BIDDING_LABEL_KEY);
		} else if (QTStatusConst.QT_PROPOSAL_PREPARATION.equals(statusId)) {
			if(ParameterConst.PROCUREMENT_MODE_TENDER.equalsIgnoreCase(ProcurementMode)){
				labelValue = getLabelName(TENDER_LABEL_KEY);
			}else{
				labelValue = getLabelName(QUOTATION_LABEL_KEY);
			}
		} else if (DPStatusConstants.SQ_PENDING_SUPPLIER_RESPONSE
				.equals(statusId)) {
			labelValue = getLabelName(SQ_LABEL_KEY);
		} else if (QTStatusConst.QT_BRIEFING_SITE_VISIT.equals(statusId)) {
			labelValue = getLabelName(BSV_LABEL_KEY);;
		}
		return labelValue;
	}
	
	private String getLabelName(String labelName){
		
		return ResourceBundleUtils.getMessage(
				labelName, 
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale());
	}
	
	/**
	 * Set the InvitationTypes dropdown values
	 * 
	 * @return invitationTypesMap
	 */
	public Map<Long, String> getInvitationTypes(){
			
			Map<Long, String> invitationTypesMap = new LinkedHashMap<Long, String>();
			invitationTypesMap.put(1L, getLabelName(ALL_LABEL_KEY));
			invitationTypesMap.put(QTStatusConst.QT_PROPOSAL_PREPARATION,getLabelName(QT_LABEL_KEY));
			invitationTypesMap.put(QTStatusConst.QT_BRIEFING_SITE_VISIT,getLabelName(BSV_LABEL_KEY));
			invitationTypesMap.put(QTStatusConst.SF_PENDING_BIDDING,
					getLabelName(BIDDING_LABEL_KEY));
			invitationTypesMap.put(DPStatusConstants.SQ_PENDING_SUPPLIER_RESPONSE,
					getLabelName(SQ_LABEL_KEY));
			
			return invitationTypesMap;
	}
	
	public void searchSelectedInvitationType() {
		supplierInvitationModel.reloadData();
	}
	
	/**
	 * @return the supplierInvitationModel
	 */
	public IPagingDataModel<Object[]> getSupplierInvitationModel() {
		return supplierInvitationModel;
	}

	/**
	 * @param supplierInvitationModel the supplierInvitationModel to set
	 */
	public void setSupplierInvitationModel(
			IPagingDataModel<Object[]> supplierInvitationModel) {
		this.supplierInvitationModel = supplierInvitationModel;
	}
	

}
