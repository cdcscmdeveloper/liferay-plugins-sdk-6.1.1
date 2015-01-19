/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.trackingdiary;

import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

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
	private IPagingDataModel<ScRequestNote> scRNDocumentDataModel = new ScDocumentPagingDataModel();
	private IPagingDataModel<ScPurchaseInDefault> scPDDocumentDataModel = new ScPDDocumentPagingDataModel();
	private IPagingDataModel<ScPrInquiry> scPIDocumentDataModel = new ScPIDocumentPagingDataModel();
	private IPagingDataModel<ScQuote> scSQDocumentDataModel = new ScSQDocumentPagingDataModel();
//	  Doctype    table
//	   RN         ScRequestNote
//	   PD         ScPurchaseInDefault
//	   PI          ScPrInquiry
//	   SQ         ScQuote	
	private class ScDocumentPagingDataModel extends PagingDataModel<ScRequestNote> {
		private static final long serialVersionUID = 1L;

		public ScDocumentPagingDataModel() {
			super("scDocumentTableID",false);
		}
		@Override
		public ISearchResultBean<ScRequestNote> pageChangeListener(ISearchBean<ScRequestNote> searchBean) {
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
					searchBean.getDataFilterBeans().add(new SearchFilterBean("transactionType",SearchOperator.OPERATOR_LIKE.value(),getSearchDocumentType()));
				}
				if(getSearchDocumentNo()!=null && getSearchDocumentNo()!="") {
					System.out.println("getSearchDocumentNo");
					searchBean.getDataFilterBeans().add(new SearchFilterBean("group",SearchOperator.OPERATOR_EQUALS.value(),true));
				}
				if(getSearchStatusId()!=null) {
					System.out.println("getSearchDocumentNo");
					searchBean.getDataFilterBeans().add(new SearchFilterBean("docNo",SearchOperator.OPERATOR_LIKE.value(),getSearchDocumentNo()));
				}
				if(getSearchDocumentTitle()!=null && getSearchDocumentTitle()!="") {
					System.out.println("getSearchDocumentTitle");
					searchBean.getDataFilterBeans().add(new SearchFilterBean("physicalDocNo",SearchOperator.OPERATOR_LIKE.value(),getSearchDocumentTitle()));
				}
				if(getSearchDocumentFromDuration()!=null ) {
					System.out.println("getSearchDocumentFromDuration");
					searchBean.getDataFilterBeans().add(new SearchFilterBean("loaNo",SearchOperator.OPERATOR_LIKE.value(),getSearchDocumentFromDuration()));
				}
				if(getSearchDocumentToDuration()!=null ) {
					System.out.println("getSearchDocumentToDuration");
					searchBean.getDataFilterBeans().add(new SearchFilterBean("loaPhysicalNo",SearchOperator.OPERATOR_LIKE.value(),getSearchDocumentToDuration()));
				}	
				ISourcingIntegrationSO sourcingIntegrationSO = (ISourcingIntegrationSO) EpEjbBusinessDelegate.getInstance().getServiceObject("sc", ISourcingIntegrationSO.class);
				SourcingIntegrationReqDTO reqDTO = new SourcingIntegrationReqDTO();
				ISearchBean<Object[]> searchBeana = new SearchBean<Object[]>();
				searchBeana.getDataFilterBeans().add(new SearchFilterBean("docType",SearchOperator.OPERATOR_EQUALS.value(),"SQ"));
				searchBean.getDataFilterBeans().add(new SearchFilterBean("docNo",SearchOperator.OPERATOR_LIKE.value(),"SQ15000000000"));
				searchBean.getDataFilterBeans().add(new SearchFilterBean("status",SearchOperator.OPERATOR_LIKE.value(),"60850"));
				searchBean.getDataFilterBeans().add(new SearchFilterBean("title",SearchOperator.OPERATOR_LIKE.value(),"TELEFON NOKIO"));
//				searchBean.getDataFilterBeans().add(new SearchFilterBean("createdDate",SearchOperator.OPERATOR_BETWEEN.value(), 
//						new SearchBetweenValue<Date>(
//								new Date(System.currentTimeMillis() + (30*24*60*60*1000)),
//								new Date(System.currentTimeMillis()))));
				reqDTO.setSourcingDocSearchBean(searchBeana);
				SourcingIntegrationResDTO resDTO = sourcingIntegrationSO.searchSourcingDocument(reqDTO);
				System.out.println("Result : "+resDTO.getSourcingDocSearchResultBean().getResultList().size());
//				return (ISearchResultBean<ScRequestNote>)resDTO.getSourcingDocSearchResultBean();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	private class ScPDDocumentPagingDataModel extends PagingDataModel<ScPurchaseInDefault> {
		private static final long serialVersionUID = 1L;

		public ScPDDocumentPagingDataModel() {
			super("scDocumentTableID",false);
		}
		@Override
		public ISearchResultBean<ScPurchaseInDefault> pageChangeListener(ISearchBean<ScPurchaseInDefault> searchBean) {
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
					searchBean.getDataFilterBeans().add(new SearchFilterBean("transactionType",SearchOperator.OPERATOR_LIKE.value(),getSearchDocumentType()));
				}
				if(getSearchDocumentNo()!=null && getSearchDocumentNo()!="") {
					System.out.println("getSearchDocumentNo");
					searchBean.getDataFilterBeans().add(new SearchFilterBean("group",SearchOperator.OPERATOR_EQUALS.value(),true));
				}
				if(getSearchStatusId()!=null) {
					System.out.println("getSearchDocumentNo");
					searchBean.getDataFilterBeans().add(new SearchFilterBean("docNo",SearchOperator.OPERATOR_LIKE.value(),getSearchDocumentNo()));
				}
				if(getSearchDocumentTitle()!=null && getSearchDocumentTitle()!="") {
					System.out.println("getSearchDocumentTitle");
					searchBean.getDataFilterBeans().add(new SearchFilterBean("physicalDocNo",SearchOperator.OPERATOR_LIKE.value(),getSearchDocumentTitle()));
				}
				if(getSearchDocumentFromDuration()!=null ) {
					System.out.println("getSearchDocumentFromDuration");
					searchBean.getDataFilterBeans().add(new SearchFilterBean("loaNo",SearchOperator.OPERATOR_LIKE.value(),getSearchDocumentFromDuration()));
				}
				if(getSearchDocumentToDuration()!=null ) {
					System.out.println("getSearchDocumentToDuration");
					searchBean.getDataFilterBeans().add(new SearchFilterBean("loaPhysicalNo",SearchOperator.OPERATOR_LIKE.value(),getSearchDocumentToDuration()));
				}	
//				if(getSearchLoaNo()!=null && getSearchLoaNo()!="") {
//					System.out.println("searchContractFromDuration");
//					searchBean.getDataFilterBeans().add(new SearchFilterBean("searchContractFromDuration",SearchOperator.OPERATOR_LIKE.value(),getSearchLoaNo()));
//				}
//				if(getSearchPhysLoaNo()!=null && getSearchPhysLoaNo()!="") {
//					System.out.println("searchContractToDuration");
//					searchBean.getDataFilterBeans().add(new SearchFilterBean("searchContractToDuration",SearchOperator.OPERATOR_LIKE.value(),getSearchPhysLoaNo()));
//				}	
//				if(getSearchPhysContractNo()!=null && getSearchPhysContractNo()!="") {
//					System.out.println("statusId");
//					searchBean.getDataFilterBeans().add(new SearchFilterBean("statusId",SearchOperator.OPERATOR_LIKE.value(),getSearchPhysContractNo()));
//				}
//				IContractIntegrationSO contractIntegrationSO = (IContractIntegrationSO) EpEjbBusinessDelegate.getInstance().getServiceObject("ct", IContractIntegrationSO.class);
//				ContractIntegrationReqDTO reqDTO = new ContractIntegrationReqDTO();
//				
////				searchBean.getDataFilterBeans().add(new SearchFilterBean("docType",SearchOperator.OPERATOR_EQUALS.value(),"RC"));
//				reqDTO.setScDocumentSearchBean(searchBean);
//				ContractIntegrationResDTO resDTO = contractIntegrationSO.searchContractDocument(reqDTO);
//				System.out.println("Result : " +resDTO.getScDocumentSearchResultBean().getTotalRecord());
//				System.out.println("pageChangeListener"+ resDTO.getScDocumentSearchResultBean().getTotalRecord());
//				return resDTO.getScDocumentSearchResultBean();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	private class ScPIDocumentPagingDataModel extends PagingDataModel<ScPrInquiry> {
		private static final long serialVersionUID = 1L;

		public ScPIDocumentPagingDataModel() {
			super("scDocumentTableID",false);
		}
		@Override
		public ISearchResultBean<ScPrInquiry> pageChangeListener(ISearchBean<ScPrInquiry> searchBean) {
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
					searchBean.getDataFilterBeans().add(new SearchFilterBean("transactionType",SearchOperator.OPERATOR_LIKE.value(),getSearchDocumentType()));
				}
				if(getSearchDocumentNo()!=null && getSearchDocumentNo()!="") {
					System.out.println("getSearchDocumentNo");
					searchBean.getDataFilterBeans().add(new SearchFilterBean("group",SearchOperator.OPERATOR_EQUALS.value(),true));
				}
				if(getSearchStatusId()!=null) {
					System.out.println("getSearchDocumentNo");
					searchBean.getDataFilterBeans().add(new SearchFilterBean("docNo",SearchOperator.OPERATOR_LIKE.value(),getSearchDocumentNo()));
				}
				if(getSearchDocumentTitle()!=null && getSearchDocumentTitle()!="") {
					System.out.println("getSearchDocumentTitle");
					searchBean.getDataFilterBeans().add(new SearchFilterBean("physicalDocNo",SearchOperator.OPERATOR_LIKE.value(),getSearchDocumentTitle()));
				}
				if(getSearchDocumentFromDuration()!=null ) {
					System.out.println("getSearchDocumentFromDuration");
					searchBean.getDataFilterBeans().add(new SearchFilterBean("loaNo",SearchOperator.OPERATOR_LIKE.value(),getSearchDocumentFromDuration()));
				}
				if(getSearchDocumentToDuration()!=null ) {
					System.out.println("getSearchDocumentToDuration");
					searchBean.getDataFilterBeans().add(new SearchFilterBean("loaPhysicalNo",SearchOperator.OPERATOR_LIKE.value(),getSearchDocumentToDuration()));
				}	
//				if(getSearchLoaNo()!=null && getSearchLoaNo()!="") {
//					System.out.println("searchContractFromDuration");
//					searchBean.getDataFilterBeans().add(new SearchFilterBean("searchContractFromDuration",SearchOperator.OPERATOR_LIKE.value(),getSearchLoaNo()));
//				}
//				if(getSearchPhysLoaNo()!=null && getSearchPhysLoaNo()!="") {
//					System.out.println("searchContractToDuration");
//					searchBean.getDataFilterBeans().add(new SearchFilterBean("searchContractToDuration",SearchOperator.OPERATOR_LIKE.value(),getSearchPhysLoaNo()));
//				}	
//				if(getSearchPhysContractNo()!=null && getSearchPhysContractNo()!="") {
//					System.out.println("statusId");
//					searchBean.getDataFilterBeans().add(new SearchFilterBean("statusId",SearchOperator.OPERATOR_LIKE.value(),getSearchPhysContractNo()));
//				}
//				IContractIntegrationSO contractIntegrationSO = (IContractIntegrationSO) EpEjbBusinessDelegate.getInstance().getServiceObject("ct", IContractIntegrationSO.class);
//				ContractIntegrationReqDTO reqDTO = new ContractIntegrationReqDTO();
//				
////				searchBean.getDataFilterBeans().add(new SearchFilterBean("docType",SearchOperator.OPERATOR_EQUALS.value(),"RC"));
//				reqDTO.setScDocumentSearchBean(searchBean);
//				ContractIntegrationResDTO resDTO = contractIntegrationSO.searchContractDocument(reqDTO);
//				System.out.println("Result : " +resDTO.getScDocumentSearchResultBean().getTotalRecord());
//				System.out.println("pageChangeListener"+ resDTO.getScDocumentSearchResultBean().getTotalRecord());
//				return resDTO.getScDocumentSearchResultBean();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	private class ScSQDocumentPagingDataModel extends PagingDataModel<ScQuote> {
		private static final long serialVersionUID = 1L;

		public ScSQDocumentPagingDataModel() {
			super("scDocumentTableID",false);
		}
		@Override
		public ISearchResultBean<ScQuote> pageChangeListener(ISearchBean<ScQuote> searchBean) {
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
					searchBean.getDataFilterBeans().add(new SearchFilterBean("transactionType",SearchOperator.OPERATOR_LIKE.value(),getSearchDocumentType()));
				}
				if(getSearchDocumentNo()!=null && getSearchDocumentNo()!="") {
					System.out.println("getSearchDocumentNo");
					searchBean.getDataFilterBeans().add(new SearchFilterBean("group",SearchOperator.OPERATOR_EQUALS.value(),true));
				}
				if(getSearchStatusId()!=null) {
					System.out.println("getSearchDocumentNo");
					searchBean.getDataFilterBeans().add(new SearchFilterBean("docNo",SearchOperator.OPERATOR_LIKE.value(),getSearchDocumentNo()));
				}
				if(getSearchDocumentTitle()!=null && getSearchDocumentTitle()!="") {
					System.out.println("getSearchDocumentTitle");
					searchBean.getDataFilterBeans().add(new SearchFilterBean("physicalDocNo",SearchOperator.OPERATOR_LIKE.value(),getSearchDocumentTitle()));
				}
				if(getSearchDocumentFromDuration()!=null ) {
					System.out.println("getSearchDocumentFromDuration");
					searchBean.getDataFilterBeans().add(new SearchFilterBean("loaNo",SearchOperator.OPERATOR_LIKE.value(),getSearchDocumentFromDuration()));
				}
				if(getSearchDocumentToDuration()!=null ) {
					System.out.println("getSearchDocumentToDuration");
					searchBean.getDataFilterBeans().add(new SearchFilterBean("loaPhysicalNo",SearchOperator.OPERATOR_LIKE.value(),getSearchDocumentToDuration()));
				}	
//				if(getSearchLoaNo()!=null && getSearchLoaNo()!="") {
//					System.out.println("searchContractFromDuration");
//					searchBean.getDataFilterBeans().add(new SearchFilterBean("searchContractFromDuration",SearchOperator.OPERATOR_LIKE.value(),getSearchLoaNo()));
//				}
//				if(getSearchPhysLoaNo()!=null && getSearchPhysLoaNo()!="") {
//					System.out.println("searchContractToDuration");
//					searchBean.getDataFilterBeans().add(new SearchFilterBean("searchContractToDuration",SearchOperator.OPERATOR_LIKE.value(),getSearchPhysLoaNo()));
//				}	
//				if(getSearchPhysContractNo()!=null && getSearchPhysContractNo()!="") {
//					System.out.println("statusId");
//					searchBean.getDataFilterBeans().add(new SearchFilterBean("statusId",SearchOperator.OPERATOR_LIKE.value(),getSearchPhysContractNo()));
//				}
//				IContractIntegrationSO contractIntegrationSO = (IContractIntegrationSO) EpEjbBusinessDelegate.getInstance().getServiceObject("ct", IContractIntegrationSO.class);
//				ContractIntegrationReqDTO reqDTO = new ContractIntegrationReqDTO();
//				
////				searchBean.getDataFilterBeans().add(new SearchFilterBean("docType",SearchOperator.OPERATOR_EQUALS.value(),"RC"));
//				reqDTO.setScDocumentSearchBean(searchBean);
//				ContractIntegrationResDTO resDTO = contractIntegrationSO.searchContractDocument(reqDTO);
//				System.out.println("Result : " +resDTO.getScDocumentSearchResultBean().getTotalRecord());
//				System.out.println("pageChangeListener"+ resDTO.getScDocumentSearchResultBean().getTotalRecord());
//				return resDTO.getScDocumentSearchResultBean();
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

	public IPagingDataModel<ScRequestNote> getScRNDocumentDataModel() {
		return scRNDocumentDataModel;
	}

	public void setScRNDocumentDataModel(IPagingDataModel<ScRequestNote> scRNDocumentDataModel) {
		this.scRNDocumentDataModel = scRNDocumentDataModel;
	}

	public IPagingDataModel<ScPurchaseInDefault> getScPDDocumentDataModel() {
		return scPDDocumentDataModel;
	}

	public void setScPDDocumentDataModel(IPagingDataModel<ScPurchaseInDefault> scPDDocumentDataModel) {
		this.scPDDocumentDataModel = scPDDocumentDataModel;
	}

	public IPagingDataModel<ScPrInquiry> getScPIDocumentDataModel() {
		return scPIDocumentDataModel;
	}

	public void setScPIDocumentDataModel(IPagingDataModel<ScPrInquiry> scPIDocumentDataModel) {
		this.scPIDocumentDataModel = scPIDocumentDataModel;
	}

	public IPagingDataModel<ScQuote> getScSQDocumentDataModel() {
		return scSQDocumentDataModel;
	}

	public void setScSQDocumentDataModel(IPagingDataModel<ScQuote> scSQDocumentDataModel) {
		this.scSQDocumentDataModel = scSQDocumentDataModel;
	}

}
