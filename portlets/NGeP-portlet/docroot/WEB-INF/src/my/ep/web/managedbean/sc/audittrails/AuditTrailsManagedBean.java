package my.ep.web.managedbean.sc.audittrails;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.apache.log4j.Logger;

import my.ep.biz.bean.search.BaseSearchFilterBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.sc.quationtender.IQTAuditTrailsSO;
import my.ep.biz.service.trackingdiary.ITrackingDiarySO;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryReqDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryResDTO;
import my.ep.dto.sc.quationtender.AuditTrailsReqDTO;
import my.ep.dto.sc.quationtender.AuditTrailsResDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmTrackingDiary;
import my.ep.persistence.entity.sc.quationtender.ScQtAuditTrails;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.trackingdiary.CdTrackingDiaryBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
//import my.ep.web.managedbean.trackingdiary.PmTrackingDiaryManagedBean.SearchTrackingDiaryPagingDataModel;
import my.ep.web.util.lookupservice.BusinessDelegate;
@ManagedBean(name = "auditTrailsManagedBean")
@SessionScoped
public class AuditTrailsManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	private String moduleSelected="";
	private String previousSelectedModule="";
	private String backUrl="";
	private IPagingDataModel<ScQtAuditTrails> searchAuditTrailsDataModel = new SearchAuditTrailsPagingDataModel();

	public String getModuleSelected() {
		return moduleSelected;
	}
	public void setModuleSelected(String moduleSelected) {
		this.moduleSelected = moduleSelected;
	}
	public String getPreviousSelectedModule() {
		return previousSelectedModule;
	}
	public void setPreviousSelectedModule(String previousSelectedModule) {
		this.previousSelectedModule = previousSelectedModule;
	}
	public String getBackUrl() {
		return backUrl;
	}
	public void setBackUrl(String backUrl) {
		this.backUrl = backUrl;
	}
	
	public IPagingDataModel<ScQtAuditTrails> getSearchAuditTrailsDataModel() {
		return searchAuditTrailsDataModel;
	}

	public void setSearchAuditTrailsDataModel(IPagingDataModel<ScQtAuditTrails> searchAuditTrailsDataModel) {
		this.searchAuditTrailsDataModel = searchAuditTrailsDataModel;
	}
	
	private class SearchAuditTrailsPagingDataModel extends PagingDataModel<ScQtAuditTrails> {

		private static final long serialVersionUID = 1L;

		public SearchAuditTrailsPagingDataModel() {
			super("auditTrailsTableID");
		}

		@Override
//		protected ISearchResultBean<PmTrackingDiary> pageChangeListener(ISearchBean<PmTrackingDiary> searchBean) {
		protected ISearchResultBean<ScQtAuditTrails> pageChangeListener(ISearchBean<ScQtAuditTrails> searchBean) {
			boolean isSearchBeanEmpty = true;
//			TrackingDiaryReqDTO reqDTO = new TrackingDiaryReqDTO();
			/* to be removed */
			AuditTrailsReqDTO reqDTO = new AuditTrailsReqDTO();
			reqDTO.setActionType("C");
			reqDTO.setDocNo("1");
			reqDTO.setDocTypeId(152299L);
			reqDTO.setOrganizationName("Org 1");
			reqDTO.setCreatedBy(2L);
			/* until here */
//			if (getSelectedDocType() == null || getSelectedDocId() == null) {
//				log.info(String.format("PM tracking diary search filter empty[%s=%s][%s=%s]", "docType",
//						getSelectedDocType(), "docId", getSelectedDocId()));
//				return new SearchResultBean<PmTrackingDiary>();
//			}

//			SearchFilterBean searchFilterBean1 = new SearchFilterBean("docType",
//					SearchOperator.OPERATOR_EQUALS.value(), getSelectedDocType());

//			SearchFilterBean searchFilterBean2 = new SearchFilterBean("docId", SearchOperator.OPERATOR_EQUALS.value(),
//					getSelectedDocId(), SearchCondition.CONDITION_AND.value());

//			SearchFilterBean searchFilterBean3 = new SearchFilterBean("groupDocType",
//					SearchOperator.OPERATOR_EQUALS.value(), getSelectedDocType());

//			SearchFilterBean searchFilterBean4 = new SearchFilterBean("groupId",
//					SearchOperator.OPERATOR_EQUALS.value(), getSelectedDocId(), SearchCondition.CONDITION_AND.value());

//			BaseSearchFilterBean searchFilterBeanAnd1 = new SearchMultiFilterBean(searchFilterBean1, searchFilterBean2);
//			BaseSearchFilterBean searchFilterBeanAnd2 = new SearchMultiFilterBean(searchFilterBean3, searchFilterBean4);

//			searchFilterBeanAnd1.setAndOr(SearchCondition.CONDITION_OR.value());
//			searchFilterBeanAnd2.setAndOr(SearchCondition.CONDITION_OR.value());

			if (isSearchBeanEmpty) {

//				TrackingDiaryResDTO resDTO = new TrackingDiaryResDTO();
				AuditTrailsResDTO resDTO = new AuditTrailsResDTO();
				try {
//					ITrackingDiarySO trackingDiarySO = (ITrackingDiarySO) BusinessDelegate.getInstance()
//							.getServiceObject("cc", ITrackingDiarySO.class);
					IQTAuditTrailsSO auditTrailsSO = (IQTAuditTrailsSO) BusinessDelegate.getInstance()
							.getServiceObject("cc", IQTAuditTrailsSO.class);

					// #25790 - Start and #25791
//					if (isReqOrApprover() && ParameterConst.DOCTYPE_SIMPLE_QUOTE.equals(getSelectedDocType())
//							|| isReqOrApprover()
//							&& ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT.equals(getSelectedDocType())
//							|| isReqOrApprover() && ParameterConst.DOCTYPE_REQUEST_NOTE.equals(getSelectedDocType())
//							|| isReqOrApprover() && ParameterConst.DOCTYPE_LETTER_OF_AWARD.equals(getSelectedDocType())
//							|| isReqOrApprover()
//							&& ParameterConst.DOCTYPE_PURCHASE_INQUIRY.equals(getSelectedDocType())
//							|| isReqOrApprover() && ParameterConst.DOCTYPE_QUOTATION.equals(getSelectedDocType())) {
//						String docType = getSelectedDocType();
//						Long docId = getSelectedDocId();
//						resDTO = trackingDiarySO.retrieveTrackingDiaryByGroupDocAndGroupId(docId, docType, reqDTO,
//								searchBean, searchFilterBeanAnd1);
//
//						// #25396 - Start
//						ISearchResultBean<PmTrackingDiary> tempResultBean = resDTO.getResultBean();
//						HashMap<Long, PmTrackingDiary> tempPmTrackingDiary = new HashMap<Long, PmTrackingDiary>();
//						if (isUseViewAsOption()) {
//							if (isViewAsSupplier()) {
//								List<String> listOfSupplierRole = getAllSupplierRole();
//								for (PmTrackingDiary getResultList : resDTO.getResultBean().getResultList()) {
//									if (listOfSupplierRole.contains(getResultList.getRoleCode())) {
//										tempPmTrackingDiary.put(getResultList.getTrackingDiaryId(), getResultList);
//									}
//								}
//								tempResultBean.getResultList().clear();
//								tempResultBean.getResultList().addAll(
//										new ArrayList<PmTrackingDiary>(tempPmTrackingDiary.values()));
//
//								resDTO.setResultBean(tempResultBean);
//							}
//						}
						// #25396 - end

//					} else {
//						searchBean.getDataFilterBeans().add(
//								new SearchMultiFilterBean(searchFilterBeanAnd1, searchFilterBeanAnd2));
//						reqDTO.setSearchBean(searchBean);


//						setSearchBeanDescription(searchBean, searchFilterBean1, searchFilterBean2);
						resDTO = auditTrailsSO.retrieveAuditTrailsBySearchBean(reqDTO);
//					}
					// #25790 - End

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return resDTO.getResultBean();
			} else {
//				return new SearchResultBean<PmTrackingDiary>();
				return new SearchResultBean<ScQtAuditTrails>();
			}
		}
	}

}
