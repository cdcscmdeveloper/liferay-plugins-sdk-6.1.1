/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.trackingdiary;

import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.sc.ISourcingIntegrationSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sc.SourcingIntegrationReqDTO;
import my.ep.dto.sc.SourcingIntegrationResDTO;
import my.ep.exception.EpException;
import my.ep.type.aut.SearchOperator;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;

/**
 * Managed Bean for Fulfillment document
 * 
 * @author Alvin
 * @since 2/11/2013
 */
@ManagedBean(name = "dpTrackingDiaryManagedBean")
@SessionScoped
public class DpTrackingDiaryManagedBean extends BaseManagedBean {
	private static Logger log = Logger.getLogger(DpTrackingDiaryManagedBean.class);

	private static final long serialVersionUID = 1L;
	
	/** The search document type. */
	private String searchDocumentType; 
	
	/** The search doc no. */
	private String searchDocNo;
	
	/** The search document no. */
	private String searchDocumentNo;
	
	/** The search document title. */
	private String searchDocumentTitle;
	
	/** The search status id. */
	private Long searchStatusId;
	
	/** The search document from duration. */
	private Date searchDocumentFromDuration;
	
	private Date searchDocumentToDuration;
	
	/** The dp obj. */
	private Object[] dpObj = new Object[6];
	
	/** The from page url. */
	private String fromPageUrl;
	
	/** The dp document data model. */
	private IPagingDataModel<Object[]> dpDocumentDataModel = new DocumentPagingDataModel();
	

	/**
	 * The Class DocumentPagingDataModel.
	 */
	private class DocumentPagingDataModel extends PagingDataModel<Object[]> {
		private static final long serialVersionUID = 1L;

		public DocumentPagingDataModel() {
			super("dpDocumentTableID",false);
		}
		@Override
		public ISearchResultBean<Object[]> pageChangeListener(ISearchBean<Object[]> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			try {
				if(null == getSearchDocumentType()){ 
					throw new EpException(MessageConst.ERROR_740023);
				}

				if(getSearchDocumentType()!=null && getSearchDocumentType()!="") {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("docType",SearchOperator.OPERATOR_EQUALS.value(),getSearchDocumentType()));
					if(getSearchDocumentType().equalsIgnoreCase("LA")) {
						System.out.println("LA");
						searchBean.getDataFilterBeans().add(new SearchFilterBean("MOD",SearchOperator.OPERATOR_EQUALS.value(),ModuleCodeConst.DIRECT_PURCHASE));
					}
				}
				if(getSearchDocumentNo()!=null && getSearchDocumentNo()!="") {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("docNo",SearchOperator.OPERATOR_EQUALS.value(),getSearchDocumentNo()));
				}
				if(getSearchStatusId()!=null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("status",SearchOperator.OPERATOR_EQUALS.value(),getSearchStatusId()));
				}
				if(getSearchDocumentFromDuration()!=null && getSearchDocumentToDuration()!=null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("createdDate",SearchOperator.OPERATOR_BETWEEN.value(), 
							new SearchBetweenValue<Date>(
							new Date(System.currentTimeMillis() + (30*24*60*60*1000)),
							new Date(System.currentTimeMillis()))));
				}
				ISourcingIntegrationSO sourcingIntegrationSO = (ISourcingIntegrationSO) EpEjbBusinessDelegate.getInstance().getServiceObject("sc", ISourcingIntegrationSO.class);
				SourcingIntegrationReqDTO reqDTO = new SourcingIntegrationReqDTO();
				reqDTO.setSourcingDocSearchBean(searchBean);
				SourcingIntegrationResDTO resDTO = sourcingIntegrationSO.searchSourcingDocument(reqDTO);
				return resDTO.getSourcingDocSearchResultBean();
			} catch (Exception e) {
				//start - Sonarqube DP Vulnerability Minor Ruzaimah [22/4/2024]
				log.error("Error : " + e.getMessage());
				//stop - Sonarqube DP Vulnerability Minor Ruzaimah [22/4/2024]
			}
			return null;
		}
	}


	/**
	 * @return the searchDocumentType
	 */
	public String getSearchDocumentType() {
		return searchDocumentType;
	}


	/**
	 * @param searchDocumentType the searchDocumentType to set
	 */
	public void setSearchDocumentType(String searchDocumentType) {
		this.searchDocumentType = searchDocumentType;
	}


	/**
	 * @return the searchDocNo
	 */
	public String getSearchDocNo() {
		return searchDocNo;
	}


	/**
	 * @param searchDocNo the searchDocNo to set
	 */
	public void setSearchDocNo(String searchDocNo) {
		this.searchDocNo = searchDocNo;
	}


	/**
	 * @return the searchDocumentNo
	 */
	public String getSearchDocumentNo() {
		return searchDocumentNo;
	}


	/**
	 * @param searchDocumentNo the searchDocumentNo to set
	 */
	public void setSearchDocumentNo(String searchDocumentNo) {
		this.searchDocumentNo = searchDocumentNo;
	}


	/**
	 * @return the searchDocumentTitle
	 */
	public String getSearchDocumentTitle() {
		return searchDocumentTitle;
	}


	/**
	 * @param searchDocumentTitle the searchDocumentTitle to set
	 */
	public void setSearchDocumentTitle(String searchDocumentTitle) {
		this.searchDocumentTitle = searchDocumentTitle;
	}


	/**
	 * @return the searchStatusId
	 */
	public Long getSearchStatusId() {
		return searchStatusId;
	}


	/**
	 * @param searchStatusId the searchStatusId to set
	 */
	public void setSearchStatusId(Long searchStatusId) {
		this.searchStatusId = searchStatusId;
	}


	/**
	 * @return the searchDocumentFromDuration
	 */
	public Date getSearchDocumentFromDuration() {
		return searchDocumentFromDuration;
	}


	/**
	 * @param searchDocumentFromDuration the searchDocumentFromDuration to set
	 */
	public void setSearchDocumentFromDuration(Date searchDocumentFromDuration) {
		this.searchDocumentFromDuration = searchDocumentFromDuration;
	}


	/**
	 * @return the searchDocumentToDuration
	 */
	public Date getSearchDocumentToDuration() {
		return searchDocumentToDuration;
	}


	/**
	 * @param searchDocumentToDuration the searchDocumentToDuration to set
	 */
	public void setSearchDocumentToDuration(Date searchDocumentToDuration) {
		this.searchDocumentToDuration = searchDocumentToDuration;
	}


	/**
	 * @return the dpObj
	 */
	public Object[] getDpObj() {
		return dpObj;
	}


	/**
	 * @param dpObj the dpObj to set
	 */
	public void setDpObj(Object[] dpObj) {
		this.dpObj = dpObj;
	}


	/**
	 * @return the fromPageUrl
	 */
	public String getFromPageUrl() {
		return fromPageUrl;
	}


	/**
	 * @param fromPageUrl the fromPageUrl to set
	 */
	public void setFromPageUrl(String fromPageUrl) {
		this.fromPageUrl = fromPageUrl;
	}


	/**
	 * @return the dpDocumentDataModel
	 */
	public IPagingDataModel<Object[]> getDpDocumentDataModel() {
		return dpDocumentDataModel;
	}


	/**
	 * @param dpDocumentDataModel the dpDocumentDataModel to set
	 */
	public void setDpDocumentDataModel(
			IPagingDataModel<Object[]> dpDocumentDataModel) {
		this.dpDocumentDataModel = dpDocumentDataModel;
	}

	/**
	 * Reset document data model.
	 */
	public void resetDocumentDataModel() {
		this.dpDocumentDataModel = new DocumentPagingDataModel();
	}

}
