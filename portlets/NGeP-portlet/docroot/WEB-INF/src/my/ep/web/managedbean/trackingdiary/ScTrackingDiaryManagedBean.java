/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.trackingdiary;

import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.IContractIntegrationSO;
import my.ep.constant.MessageConst;
import my.ep.dto.ct.ContractIntegrationReqDTO;
import my.ep.dto.ct.ContractIntegrationResDTO;
import my.ep.exception.EpException;
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
	private IPagingDataModel<ScRequestNote> scDocumentDataModel = new ScDocumentPagingDataModel();
	
	private class ScDocumentPagingDataModel extends PagingDataModel<ScRequestNote> {
		private static final long serialVersionUID = 1L;

		public ScDocumentPagingDataModel() {
			super("ctDocListDt",false);
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
//	
//				if(getSearchTrasactionType()!=null && getSearchTrasactionType()!="") {
//					System.out.println("transactionType");
//					searchBean.getDataFilterBeans().add(new SearchFilterBean("transactionType",SearchOperator.OPERATOR_LIKE.value(),getSearchContractNo()));
//				}
//				if(getSearchViewType()!=null) {
//					System.out.println("group");
//					searchBean.getDataFilterBeans().add(new SearchFilterBean("group",SearchOperator.OPERATOR_EQUALS.value(),true));
//				}
//				if(getSearchContractNo()!=null && getSearchContractNo()!="") {
//					System.out.println("docNo");
//					searchBean.getDataFilterBeans().add(new SearchFilterBean("docNo",SearchOperator.OPERATOR_LIKE.value(),getSearchContractNo()));
//				}
//				if(getSearchPhysContractNo()!=null && getSearchPhysContractNo()!="") {
//					System.out.println("physicalDocNo");
//					searchBean.getDataFilterBeans().add(new SearchFilterBean("physicalDocNo",SearchOperator.OPERATOR_LIKE.value(),getSearchPhysContractNo()));
//				}
//				if(getSearchLoaNo()!=null && getSearchLoaNo()!="") {
//					System.out.println("loaNo");
//					searchBean.getDataFilterBeans().add(new SearchFilterBean("loaNo",SearchOperator.OPERATOR_LIKE.value(),getSearchLoaNo()));
//				}
//				if(getSearchPhysLoaNo()!=null && getSearchPhysLoaNo()!="") {
//					System.out.println("loaPhysicalNo");
//					searchBean.getDataFilterBeans().add(new SearchFilterBean("loaPhysicalNo",SearchOperator.OPERATOR_LIKE.value(),getSearchPhysLoaNo()));
//				}	
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

	public IPagingDataModel<ScRequestNote> getScDocumentDataModel() {
		return scDocumentDataModel;
	}

	public void setScDocumentDataModel(IPagingDataModel<ScRequestNote> scDocumentDataModel) {
		this.scDocumentDataModel = scDocumentDataModel;
	}

}
