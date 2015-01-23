/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.trackingdiary;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.IContractIntegrationSO;
import my.ep.biz.service.sc.ISourcingIntegrationSO;
import my.ep.constant.MessageConst;
import my.ep.dto.ct.ContractIntegrationReqDTO;
import my.ep.dto.ct.ContractIntegrationResDTO;
import my.ep.dto.sc.SourcingIntegrationReqDTO;
import my.ep.dto.sc.SourcingIntegrationResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.sc.directpurchase.ScPrInquiry;
import my.ep.persistence.entity.sc.directpurchase.ScPurchaseInDefault;
import my.ep.persistence.entity.sc.directpurchase.ScQuote;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNote;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.RoleCode;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;

import org.apache.log4j.Logger;

/**
 * Managed Bean for Fulfillment document
 * 
 * @author Alvin
 * @since 2/11/2013
 */
@ManagedBean(name = "scTrackingDiaryManagedBean")
@SessionScoped
public class ScTrackingDiaryManagedBean extends BaseManagedBean {
	private static Logger log = Logger.getLogger(ScTrackingDiaryManagedBean.class);

	private static final long serialVersionUID = 1L;
	
	private String searchDocumentType; 
	private String searchDocumentNo;
	private String searchDocumentTitle;
	private Long searchStatusId;
	private Date searchDocumentFromDuration;
	private Date searchDocumentToDuration;
	private IPagingDataModel<Object[]> scRNDocumentDataModel = new ScDocumentPagingDataModel();
//	  Doctype    table
//	   RN         ScRequestNote
//	   PD         ScPurchaseInDefault
//	   PI          ScPrInquiry
//	   SQ         ScQuote	
	private class ScDocumentPagingDataModel extends PagingDataModel<Object[]> {
		private static final long serialVersionUID = 1L;

		public ScDocumentPagingDataModel() {
			super("scDocumentTableID",false);
		}
		@Override
		public ISearchResultBean<Object[]> pageChangeListener(ISearchBean<Object[]> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			try {
//				if(null == getSearchTrasactionType()){ 
//					throw new EpException(MessageConst.ERROR_740023);
//				}else if(null ==getSearchViewType() ){
//					throw new EpException(MessageConst.ERROR_740023);
//				}
				System.out.println("ISearchResultBean<ScRequestNote>");
	
				if(getSearchDocumentType()!=null && getSearchDocumentType()!="") {
					System.out.println("transactionType");
					searchBean.getDataFilterBeans().add(new SearchFilterBean("docType",SearchOperator.OPERATOR_LIKE.value(),getSearchDocumentType()));
				}
				if(getSearchDocumentNo()!=null && getSearchDocumentNo()!="") {
					System.out.println("getSearchDocumentNo");
					searchBean.getDataFilterBeans().add(new SearchFilterBean("docNo",SearchOperator.OPERATOR_EQUALS.value(),getSearchDocumentNo()));
				}
				if(getSearchStatusId()!=null) {
					System.out.println("getSearchDocumentNo");
					searchBean.getDataFilterBeans().add(new SearchFilterBean("status",SearchOperator.OPERATOR_LIKE.value(),getSearchStatusId()));
				}
				if(getSearchDocumentFromDuration()!=null && getSearchDocumentToDuration()!=null) {
					System.out.println("getSearchDocumentNo");
					searchBean.getDataFilterBeans().add(new SearchFilterBean("createdDate",SearchOperator.OPERATOR_BETWEEN.value(), 
							new SearchBetweenValue<Date>(
							new Date(System.currentTimeMillis() + (30*24*60*60*1000)),
							new Date(System.currentTimeMillis()))));
				}
				ISourcingIntegrationSO sourcingIntegrationSO = (ISourcingIntegrationSO) EpEjbBusinessDelegate.getInstance().getServiceObject("sc", ISourcingIntegrationSO.class);
				SourcingIntegrationReqDTO reqDTO = new SourcingIntegrationReqDTO();
				reqDTO.setSourcingDocSearchBean(searchBean);
				SourcingIntegrationResDTO resDTO = sourcingIntegrationSO.searchSourcingDocument(reqDTO);
				System.out.println("Result : "+resDTO.getSourcingDocSearchResultBean().getResultList().size());
				return resDTO.getSourcingDocSearchResultBean();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public String getSearchDocumentType() {
		return searchDocumentType;
	}

	public void setSearchDocumentType(String searchDocumentType) {
		this.searchDocumentType = searchDocumentType;
	}

	public String getSearchDocumentNo() {
		return searchDocumentNo;
	}

	public void setSearchDocumentNo(String searchDocumentNo) {
		this.searchDocumentNo = searchDocumentNo;
	}

	public String getSearchDocumentTitle() {
		return searchDocumentTitle;
	}

	public void setSearchDocumentTitle(String searchDocumentTitle) {
		this.searchDocumentTitle = searchDocumentTitle;
	}

	public Long getSearchStatusId() {
		return searchStatusId;
	}

	public void setSearchStatusId(Long searchStatusId) {
		this.searchStatusId = searchStatusId;
	}

	public Date getSearchDocumentFromDuration() {
		return searchDocumentFromDuration;
	}

	public void setSearchDocumentFromDuration(Date searchDocumentFromDuration) {
		this.searchDocumentFromDuration = searchDocumentFromDuration;
	}

	public Date getSearchDocumentToDuration() {
		return searchDocumentToDuration;
	}

	public void setSearchDocumentToDuration(Date searchDocumentToDuration) {
		this.searchDocumentToDuration = searchDocumentToDuration;
	}

	public IPagingDataModel<Object[]> getScRNDocumentDataModel() {
		return scRNDocumentDataModel;
	}

	public void setScRNDocumentDataModel(IPagingDataModel<Object[]> scRNDocumentDataModel) {
		this.scRNDocumentDataModel = scRNDocumentDataModel;
	}

}
