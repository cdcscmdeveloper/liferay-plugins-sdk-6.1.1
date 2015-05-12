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
import my.ep.dto.ct.ContractIntegrationReqDTO;
import my.ep.dto.ct.ContractIntegrationResDTO;
import my.ep.persistence.entity.ct.VCtDocument;
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
@ManagedBean(name = "ctTrackingDiaryManagedBean")
@SessionScoped
public class CtTrackingDiaryManagedBean extends BaseManagedBean {
	private static Logger log = Logger.getLogger(CtTrackingDiaryManagedBean.class);

	private static final long serialVersionUID = 1L;
	
	private String searchTrasactionType; 
	private String searchViewType;
	private String searchContractNo;
	private String searchLoaNo;
	private String searchPhysLoaNo;
	private String searchPhysContractNo;
	private Long searchStatusId;
	private Date searchContractFromDuration;
	private Date searchContractToDuration;
	private VCtDocument vCtDocument;
	private IPagingDataModel<VCtDocument> ctDocumentDataModel = new CtDocumentPagingDataModel();
	
	private class CtDocumentPagingDataModel extends PagingDataModel<VCtDocument> {
		private static final long serialVersionUID = 1L;

		public CtDocumentPagingDataModel() {
			super("ctDocListDt",false);
		}
		@Override
		public ISearchResultBean<VCtDocument> pageChangeListener(ISearchBean<VCtDocument> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			try {
//				if(null == getSearchTrasactionType()){ 
//					throw new EpException(MessageConst.ERROR_740023);
//				}else if(null ==getSearchViewType() ){
//					throw new EpException(MessageConst.ERROR_740023);
//				}
				System.out.println("ISearchResultBean<VCtDocument>");
	
				if(getSearchTrasactionType()!=null && getSearchTrasactionType()!="") {
					System.out.println("transactionType");
					searchBean.getDataFilterBeans().add(new SearchFilterBean("transactionType",SearchOperator.OPERATOR_EQUALS.value(),getSearchTrasactionType()));
				}
//				if(getSearchViewType()!=null) {
//					System.out.println("group");
				// Contract always is search by group
					searchBean.getDataFilterBeans().add(new SearchFilterBean("group",SearchOperator.OPERATOR_EQUALS.value(),true));
//				}
				if(getSearchContractNo()!=null && getSearchContractNo()!="") {
					System.out.println("docNo" + getSearchContractNo() );
					searchBean.getDataFilterBeans().add(new SearchFilterBean("docNo",SearchOperator.OPERATOR_LIKE.value(),getSearchContractNo()));
				}
				if(getSearchPhysContractNo()!=null && getSearchPhysContractNo()!="") {
					System.out.println("physicalDocNo");
					searchBean.getDataFilterBeans().add(new SearchFilterBean("physicalDocNo",SearchOperator.OPERATOR_LIKE.value(),getSearchPhysContractNo()));
				}
				if(getSearchLoaNo()!=null && getSearchLoaNo()!="") {
					System.out.println("loaNo");
					searchBean.getDataFilterBeans().add(new SearchFilterBean("loaNo",SearchOperator.OPERATOR_LIKE.value(),getSearchLoaNo()));
				}
				if(getSearchPhysLoaNo()!=null && getSearchPhysLoaNo()!="") {
					System.out.println("loaPhysicalNo");
					searchBean.getDataFilterBeans().add(new SearchFilterBean("loaPhysicalNo",SearchOperator.OPERATOR_LIKE.value(),getSearchPhysLoaNo()));
				}	
//				if(getSearchContractFromDuration()!=null) {
//					System.out.println("searchContractFromDuration");
//					searchBean.getDataFilterBeans().add(new SearchFilterBean("searchContractFromDuration",SearchOperator.OPERATOR_LIKE.value(),getSearchContractFromDuration()));
//				}
//				if(getSearchContractFromDuration()!=null) {
//					System.out.println("searchContractToDuration");
//					searchBean.getDataFilterBeans().add(new SearchFilterBean("searchContractToDuration",SearchOperator.OPERATOR_LIKE.value(),getSearchContractFromDuration()));
//				}	
				if(getSearchStatusId()!=null) {
					System.out.println("statusId");
					searchBean.getDataFilterBeans().add(new SearchFilterBean("statusId",SearchOperator.OPERATOR_EQUALS.value(),getSearchStatusId()));
				}
				IContractIntegrationSO contractIntegrationSO = (IContractIntegrationSO) EpEjbBusinessDelegate.getInstance().getServiceObject("ct", IContractIntegrationSO.class);
				ContractIntegrationReqDTO reqDTO = new ContractIntegrationReqDTO();
				
//				searchBean.getDataFilterBeans().add(new SearchFilterBean("docType",SearchOperator.OPERATOR_EQUALS.value(),"RC"));
				reqDTO.setCtDocumentSearchBean(searchBean);
				ContractIntegrationResDTO resDTO = contractIntegrationSO.searchContractDocument(reqDTO);
				System.out.println("Result : " +resDTO.getCtDocumentSearchResultBean().getTotalRecord());
				System.out.println("pageChangeListener"+ resDTO.getCtDocumentSearchResultBean().getTotalRecord());
				return resDTO.getCtDocumentSearchResultBean();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	public String getSearchTrasactionType() {
		return searchTrasactionType;
	}

	public void setSearchTrasactionType(String searchTrasactionType) {
		this.searchTrasactionType = searchTrasactionType;
	}

	public String getSearchViewType() {
		return searchViewType;
	}

	public void setSearchViewType(String searchViewType) {
		this.searchViewType = searchViewType;
	}

	public String getSearchContractNo() {
		return searchContractNo;
	}

	public void setSearchContractNo(String searchContractNo) {
		this.searchContractNo = searchContractNo;
	}

	public String getSearchLoaNo() {
		return searchLoaNo;
	}

	public void setSearchLoaNo(String searchLoaNo) {
		this.searchLoaNo = searchLoaNo;
	}

	public Long getSearchStatusId() {
		return searchStatusId;
	}

	public void setSearchStatusId(Long searchStatusId) {
		this.searchStatusId = searchStatusId;
	}

	public IPagingDataModel<VCtDocument> getCtDocumentDataModel() {
		return ctDocumentDataModel;
	}
	public void setCtDocumentDataModel(
			IPagingDataModel<VCtDocument> ctDocumentDataModel) {
		this.ctDocumentDataModel = ctDocumentDataModel;
	}
	/**
	 * @return the searchContractToDuration
	 */
	public Date getSearchContractToDuration() {
		return searchContractToDuration;
	}

	/**
	 * @param searchContractToDuration the searchContractToDuration to set
	 */
	public void setSearchContractToDuration(Date searchContractToDuration) {
		this.searchContractToDuration = searchContractToDuration;
	}

	/**
	 * @return the searchContractFromDuration
	 */
	public Date getSearchContractFromDuration() {
		return searchContractFromDuration;
	}

	/**
	 * @param searchContractFromDuration the searchContractFromDuration to set
	 */
	public void setSearchContractFromDuration(Date searchContractFromDuration) {
		this.searchContractFromDuration = searchContractFromDuration;
	}

	/**
	 * @return the searchPhysLoaNo
	 */
	public String getSearchPhysLoaNo() {
		return searchPhysLoaNo;
	}
	/**
	 * @return the searchPhysContractNo
	 */
	public String getSearchPhysContractNo() {
		return searchPhysContractNo;
	}
	/**
	 * @param searchPhysLoaNo the searchPhysLoaNo to set
	 */
	public void setSearchPhysLoaNo(String searchPhysLoaNo) {
		this.searchPhysLoaNo = searchPhysLoaNo;
	}
	/**
	 * @param searchPhysContractNo the searchPhysContractNo to set
	 */
	public void setSearchPhysContractNo(String searchPhysContractNo) {
		this.searchPhysContractNo = searchPhysContractNo;
	}

	public VCtDocument getvCtDocument() {
		return vCtDocument;
	}

	public void setvCtDocument(VCtDocument vCtDocument) {
		this.vCtDocument = vCtDocument;
	}

	public void resetDocumentDataModel() {
		this.ctDocumentDataModel = new CtDocumentPagingDataModel();
	}

}
