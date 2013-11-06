/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.doc;

import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.fl.IFulfilmentDocumentSO;
import my.ep.dto.fl.FlDocumentReqDTO;
import my.ep.dto.fl.FlDocumentResDTO;
import my.ep.persistence.entity.fl.VFlDocument;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.fl.doc.FlDocumentManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * Backingbean for Fulfillment document
 * 
 * @author Giribabu
 * @since 2/11/2013
 */
@ManagedBean(name="flDocumentBackingBean")
@RequestScoped
public class FlDocumentBackingBean extends BaseBackingBean<FlDocumentManagedBean>{

	private static Logger log = Logger.getLogger(FlDocumentBackingBean.class);
	public static final String NAV_TO_MAINTAIN_PR = "MaintainPR";
	public static final String NAV_TO_MAINTAIN_PO = "MaintainPO";
	public static final String NAV_TO_MAINTAIN_CO = "MaintainCO";

	private IPagingDataModel<VFlDocument> flDocumentDataModel = new FlDocumentPagingDataModel();
	
	public void advanceSearch(){
		log.debug("getSearchType:"+this.getManagedBean().getSearchType());
	}

	public void searchDocument(){
		flDocumentDataModel.reloadData();
	}
	
	public String navigateToPage(int index){
//		IFulfilmentDocumentSO flDocumentSO = (IFulfilmentDocumentSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentDocumentSO.class);
//		String flRequestId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("fulfilmentReqId");
		String docType=this.getManagedBean().getFlDocumentList().get(index).getDocType();
		if(docType.equals("PR")){
			return NAV_TO_MAINTAIN_PR;
		}else if(docType.equals("PO")){
			return NAV_TO_MAINTAIN_PO;
		}else if(docType.equals("CO")){
			return NAV_TO_MAINTAIN_CO;			
		}
		return NAV_TO_MAINTAIN_PR;
	}
	
	public void selectedUser(AjaxBehaviorEvent e) {
		// doing some stuff here
		System.out.println("====enter process checked===="+ getManagedBean().getTempLoginId());
		String id = (String) e.getComponent().getAttributes().get("value");
		System.out.println("=======get id value=====" + id);
		getManagedBean().setTempLoginId(id);
	}
	
	public void selectedDoctype(AjaxBehaviorEvent e) {
		// doing some stuff here
		System.out.println("====enter process checked===="
				+ getManagedBean().getTempDocType());
		// String dueDate = (String) ((UIOutput)e.getSource()).getValue();
		String id = (String) e.getComponent().getAttributes().get("value");
		System.out.println("=======get id value=====" + id);
		getManagedBean().setTempDocType(id);
	}

	public IPagingDataModel<VFlDocument> getFlDocumentDataModel() {
		return flDocumentDataModel;
	}

	public void setFlDocumentDataModel(IPagingDataModel<VFlDocument> flDocumentDataModel) {
		this.flDocumentDataModel = flDocumentDataModel;
	}

	private class FlDocumentPagingDataModel extends PagingDataModel<VFlDocument> {
		private static final long serialVersionUID = 1L;

		public FlDocumentPagingDataModel() {
			super("flDocListDt");
		}
		
		@Override
		public ISearchResultBean<VFlDocument> pageChangeListener(ISearchBean<VFlDocument> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			try {				
				searchBean.getDataFilterBeans().add(new SearchFilterBean("orderType",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getSearchOrderType()));

				if(getManagedBean().getSearchDocType()!=null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("docType",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getSearchDocType()));
				}
				if(getManagedBean().getSearchDocNo()!=null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("docNo",SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getSearchDocNo()));
				}
				if(getManagedBean().getSearchContractNo()!=null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("contractNo",SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getSearchContractNo()));
				}
				if(getManagedBean().getSearchCircularNo()!=null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("circularNo",SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getSearchCircularNo()));
				}
				if(getManagedBean().getSearchLoaNo()!=null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("loaNo",SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getSearchLoaNo()));
				}		
				if(getManagedBean().getSearchStartDate()!=null && getManagedBean().getSearchEndDate()!=null) {
					SearchBetweenValue<Date> betweenValue = new SearchBetweenValue<Date>(getManagedBean().getSearchStartDate(), getManagedBean().getSearchEndDate());
					searchBean.getDataFilterBeans().add(new SearchFilterBean("startDate",SearchOperator.OPERATOR_BETWEEN.value(),betweenValue));
					searchBean.getDataFilterBeans().add(new SearchFilterBean("endDate",SearchOperator.OPERATOR_BETWEEN.value(),betweenValue));
				}		
				if(getManagedBean().getSearchProcurementType()!=null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("procurementType",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getSearchProcurementType()));
				}			
				if(getManagedBean().getSearchStatusId()!=null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("status",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getSearchStatusId()));
				}
				
				FlDocumentReqDTO reqDTO = new FlDocumentReqDTO();
				reqDTO.setSearchBean(searchBean);
				
				IFulfilmentDocumentSO flDocumentSO = (IFulfilmentDocumentSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentDocumentSO.class);
				FlDocumentResDTO resDTO = flDocumentSO.searchFulfilmentDoc(reqDTO);
				if(resDTO.isSuccessful()) {
					return resDTO.getResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}	
}
