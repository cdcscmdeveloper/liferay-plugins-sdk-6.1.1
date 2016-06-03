/**
 * Copyright © 2016 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.supplierintivitation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.service.sc.supplierintivitation.ISCSupplierInvitationSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.sc.DPStatusConstants;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.dto.sc.supplierintivitation.ScSupplierInvitationReqDTO;
import my.ep.dto.sc.supplierintivitation.ScSupplierInvitationResDTO;
import my.ep.type.aut.SearchOperator;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.sc.supplierinvitation.SCSupplierInvitationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

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
	private static final String ALL_LABEL_KEY = "lbl.txt.all";
	
	@PostConstruct
	private void init() {
		
		if (this.getManagedBean().getInvitationTypeMap() == null) {
			
			Map<Long, String> invitationTypesMap = new LinkedHashMap<Long, String>();
			invitationTypesMap.put(1L, getLabelName(ALL_LABEL_KEY));
			invitationTypesMap.put(QTStatusConst.QT_PROPOSAL_PREPARATION,getLabelName(QT_LABEL_KEY));
			invitationTypesMap.put(QTStatusConst.QT_BRIEFING_SITE_VISIT,
					getStatusDescById(QTStatusConst.QT_BRIEFING_SITE_VISIT));
			invitationTypesMap.put(QTStatusConst.SF_PENDING_BIDDING,
					getLabelName(BIDDING_LABEL_KEY));
			invitationTypesMap.put(DPStatusConstants.SQ_PENDING_SUPPLIER_RESPONSE,
					getLabelName(SQ_LABEL_KEY));
			this.getManagedBean().setInvitationTypeMap(invitationTypesMap);
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
			if(QTStatusConst.QT_BRIEFING_SITE_VISIT.equals(selectedInvitationType)){
				List<Long> qtStatusIds = new ArrayList<Long>();
				qtStatusIds.add(QTStatusConst.QT_BRIEFING_SITE_VISIT);
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("qtStatus.STATUS_ID",
								SearchOperator.OPERATOR_IN.value(), qtStatusIds));
				sortOrder(searchBean,false);
				
			} else if(QTStatusConst.QT_PROPOSAL_PREPARATION.equals(selectedInvitationType)){
				List<Long> qtStatusIds = new ArrayList<Long>();
				qtStatusIds.add(QTStatusConst.QT_PROPOSAL_PREPARATION);
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("qtStatus.STATUS_ID",
								SearchOperator.OPERATOR_IN.value(), qtStatusIds));
				sortOrder(searchBean,false);
				
			} else if(DPStatusConstants.SQ_PENDING_SUPPLIER_RESPONSE.equals(selectedInvitationType)){
				List<Long> sqStatusIds = new ArrayList<Long>();
				sqStatusIds.add(DPStatusConstants.SQ_PENDING_SUPPLIER_RESPONSE);
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("sqStatus.STATUS_ID",
								SearchOperator.OPERATOR_IN.value(), sqStatusIds));
				sortOrder(searchBean,true);
				
			} else if(QTStatusConst.SF_PENDING_BIDDING.equals(selectedInvitationType)){
				List<Long> bidStatusIds = new ArrayList<Long>();
				bidStatusIds.add(QTStatusConst.SF_PENDING_BIDDING);
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("bidStatus.STATUS_ID",
								SearchOperator.OPERATOR_IN.value(), bidStatusIds));
				sortOrder(searchBean,false);
			}
			
			ISCSupplierInvitationSO dashBoardSO = null;
			ScSupplierInvitationResDTO resDTO = new ScSupplierInvitationResDTO();
			try {
				dashBoardSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								ISCSupplierInvitationSO.class);
				Long userId = new FacesScopes().getUserSessionBean().getUserId();
				if(userId != null){
					ScSupplierInvitationReqDTO reqDTO = new ScSupplierInvitationReqDTO();
					reqDTO.setUserId(userId);
					reqDTO.setSearchBean(searchBean);
					 resDTO = dashBoardSO.retrieveSupplierInviteQtsByUserId(reqDTO);
				}
				
			} finally{
				dashBoardSO = null;
			}
			
			return resDTO.getSupplierInvitationList();
		}
	}

	
	/**
	 * Construct the URL based on status id
	 * 
	 * @param statusId
	 * @param id
	 * @return url string
	 */
	public String getNavigationUrl(Long statusId,Long id){
		
		StringBuilder url = new StringBuilder();
			if (QTStatusConst.SF_PENDING_BIDDING.equals(statusId)) {

				url.append(
						EpPropertiesUtils.getInstance().getProperty(
								EpPropertiesConst.PORTAL_APPLICATION_URL))
						.append(SourcingConstants.NAV_PAGE_ONLINE_BIDDING)
						.append(SourcingConstants.NAV_PARAM_BID_SCHEDULE_ID)
						.append(SourcingConstants.NAV_PARAM_SEPARATOR)
						.append(id);
			} else if (QTStatusConst.QT_PROPOSAL_PREPARATION.equals(statusId)) {

				url.append(
						EpPropertiesUtils.getInstance().getProperty(
								EpPropertiesConst.PORTAL_APPLICATION_URL))
						.append(SourcingConstants.NAV_PAGE_SUPPLIER_PROPOSAL)
						.append(id);
			} else if (DPStatusConstants.SQ_PENDING_SUPPLIER_RESPONSE
					.equals(statusId)) {

				url.append(
						EpPropertiesUtils.getInstance().getProperty(
								EpPropertiesConst.PORTAL_APPLICATION_URL))
						.append(SourcingConstants.NAV_PAGE_SUPPLIER_RESPONSE_SIMPLE_QUOTE)
						.append(id);

			}
		
		return url.toString();
		
	}
	
	/**
	 * Returns label value based on status id
	 * 
	 * @param statusId
	 * @return label labelValue
	 */
	public String labelValue(Long statusId) {
		
		String labelValue = "";
		if (QTStatusConst.SF_PENDING_BIDDING.equals(statusId)) {
			labelValue = getLabelName(BIDDING_LABEL_KEY);
		} else if (QTStatusConst.QT_PROPOSAL_PREPARATION.equals(statusId)) {
			labelValue = getLabelName(QT_LABEL_KEY);
		} else if (DPStatusConstants.SQ_PENDING_SUPPLIER_RESPONSE
				.equals(statusId)) {
			labelValue = getLabelName(SQ_LABEL_KEY);
		} else if (QTStatusConst.QT_BRIEFING_SITE_VISIT.equals(statusId)) {
			labelValue = getStatusDescById(statusId);
		}
		return labelValue;
	}
	
	private String getLabelName(String labelName){
		
		return ResourceBundleUtils.getMessage(
				labelName, 
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale());
	}
	
	private void sortOrder(ISearchBean<Object[]> searchBean,
			boolean isSimplequote) {

		if (isSimplequote) {
			searchBean.getSortColumns().add(
					new SearchOrderBean(
							"to_char(quote.CREATED_DATE,'yyyy-MM-dd')", false));
		} else {
			searchBean.getSortColumns().add(
					new SearchOrderBean(
							"to_char(qt.CREATED_DATE,'yyyy-MM-dd')", false));
		}

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
