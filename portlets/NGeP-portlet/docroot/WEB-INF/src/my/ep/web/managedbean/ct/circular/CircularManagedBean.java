/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.ct.circular;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.circular.ICircularSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.ct.circular.CircularReqDTO;
import my.ep.dto.ct.circular.CircularResDTO;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.type.aut.SearchOperator;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * Circular Managed Bean
 * 
 * @author kieyun
 * @Created 23/12/2014
 */
@ManagedBean
@SessionScoped
public class CircularManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	
	private String searchCircularNo;
	private String searchCircularName;
	private String searchPhysicalCircularNo;
	private String searchCircularDocType;
	private String searchStatus;
	private IPagingDataModel<CtCircularVer> circularListingPagingDataModel = new CircularListingPagingDataModel();
	
	private class CircularListingPagingDataModel extends PagingDataModel<CtCircularVer> {
		private static final long serialVersionUID = 1L;

		public CircularListingPagingDataModel() {
			super("circularVerDt");
		}
		
		@Override
		public ISearchResultBean<CtCircularVer> pageChangeListener(ISearchBean<CtCircularVer> searchBean) {
			try {				
				if(getSearchCircularNo()!=null && getSearchCircularNo().length()>0) {
					SearchFilterBean filterBean = new SearchFilterBean("ctCircular.circularNo",	SearchOperator.OPERATOR_LIKE.value(),getSearchCircularNo());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);	
				}
				if(getSearchCircularName()!=null && getSearchCircularName().length()>0) {
					SearchFilterBean filterBean = new SearchFilterBean("name",
													SearchOperator.OPERATOR_LIKE.value(),getSearchCircularName());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if(getSearchPhysicalCircularNo()!=null && getSearchPhysicalCircularNo().length()>0) {
					SearchFilterBean filterBean = new SearchFilterBean("circularNo",
													SearchOperator.OPERATOR_LIKE.value(),getSearchPhysicalCircularNo());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if(getSearchCircularDocType()!=null) {
					SearchFilterBean filterBean = new SearchFilterBean("docType", SearchOperator.OPERATOR_EQUALS.value(),getSearchCircularDocType());
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if(getSearchStatus()!=null) {
					SearchFilterBean filterBean = new SearchFilterBean("ctCircular.recordStatus", SearchOperator.OPERATOR_EQUALS.value(),getSearchStatus());
					searchBean.getDataFilterBeans().add(filterBean);
				}
				
			    CircularReqDTO reqDTO = new CircularReqDTO();
				reqDTO.setCircularVerSearchBean(searchBean);
				
				ICircularSO circularSO = (ICircularSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,ICircularSO.class);
				CircularResDTO resDTO = circularSO.searchLatestCircularVer(reqDTO);
				if(resDTO.isSuccessful()) {
					return resDTO.getCircularVerResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
			}
			return null;
		}
	}
	
	public String getSearchCircularNo() {
		return searchCircularNo;
	}
	public void setSearchCircularNo(String searchCircularNo) {
		this.searchCircularNo = searchCircularNo;
	}
	public String getSearchCircularName() {
		return searchCircularName;
	}
	public void setSearchCircularName(String searchCircularName) {
		this.searchCircularName = searchCircularName;
	}
	public String getSearchPhysicalCircularNo() {
		return searchPhysicalCircularNo;
	}
	public void setSearchPhysicalCircularNo(String searchPhysicalCircularNo) {
		this.searchPhysicalCircularNo = searchPhysicalCircularNo;
	}
	public String getSearchCircularDocType() {
		return searchCircularDocType;
	}
	public void setSearchCircularDocType(String searchCircularDocType) {
		this.searchCircularDocType = searchCircularDocType;
	}
	public String getSearchStatus() {
		return searchStatus;
	}
	public void setSearchStatus(String searchStatus) {
		this.searchStatus = searchStatus;
	}
	public IPagingDataModel<CtCircularVer> getCircularListingPagingDataModel() {
		return circularListingPagingDataModel;
	}
	public void setCircularListingPagingDataModel(
			IPagingDataModel<CtCircularVer> circularListingPagingDataModel) {
		this.circularListingPagingDataModel = circularListingPagingDataModel;
	}
	
	
}
