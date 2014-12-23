/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.circular;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.circular.ICircularSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.ct.circular.CircularReqDTO;
import my.ep.dto.ct.circular.CircularResDTO;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.ct.circular.CircularManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * Circular Backingbean
 * 
 * @author kieyun
 * @Created 23/12/2014
 */
@ManagedBean
@RequestScoped
public class CircularBackingBean extends BaseBackingBean<CircularManagedBean>{
	
	private static Logger log = Logger.getLogger(CircularBackingBean.class);
	private IPagingDataModel<CtCircularVer> circularListingPagingDataModel = new CircularListingPagingDataModel();
	
	private class CircularListingPagingDataModel extends PagingDataModel<CtCircularVer> {
		private static final long serialVersionUID = 1L;

		public CircularListingPagingDataModel() {
			super("circularVerDt");
		}
		
		@Override
		public ISearchResultBean<CtCircularVer> pageChangeListener(ISearchBean<CtCircularVer> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			try {				
				if(getManagedBean().getSearchCircularNo()!=null && getManagedBean().getSearchCircularNo().length()>0) {
					SearchFilterBean filterBean = new SearchFilterBean("ctCircular.circularNo",
													SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getSearchCircularNo());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);	
				}
				if(getManagedBean().getSearchCircularName()!=null && getManagedBean().getSearchCircularName().length()>0) {
					SearchFilterBean filterBean = new SearchFilterBean("name",
													SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getSearchCircularName());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if(getManagedBean().getSearchPhysicalCircularNo()!=null && getManagedBean().getSearchPhysicalCircularNo().length()>0) {
					SearchFilterBean filterBean = new SearchFilterBean("circularNo",
													SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getSearchPhysicalCircularNo());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}	
				
			    CircularReqDTO reqDTO = new CircularReqDTO();
				reqDTO.setCircularVerSearchBean(searchBean);
				
				ICircularSO circularSO = (ICircularSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,ICircularSO.class);
				CircularResDTO resDTO = circularSO.searchCircularVer(reqDTO);
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
	
	public void searchCircularListing() throws Exception {
		try {
			if ((getManagedBean().getSearchCircularNo() == null || getManagedBean().getSearchCircularNo().length() == 0) && 
				(getManagedBean().getSearchCircularName() == null || getManagedBean().getSearchCircularName().length() == 0) &&
				(getManagedBean().getSearchPhysicalCircularNo() == null || getManagedBean().getSearchPhysicalCircularNo().length() == 0)) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_WARN, null, MessageUtils.getMessage(MessageConst.WARN_755000,
							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			}
			else {
				circularListingPagingDataModel.reloadData();
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), "circular search failed."));
		}			
	}

	public void searchReset(){
		this.getManagedBean().setSearchCircularNo(null);
		this.getManagedBean().setSearchCircularName(null);
		this.getManagedBean().setSearchPhysicalCircularNo(null);
	}

	public IPagingDataModel<CtCircularVer> getCircularListingPagingDataModel() {
		return circularListingPagingDataModel;
	}

	public void setCircularListingPagingDataModel(
			IPagingDataModel<CtCircularVer> circularListingPagingDataModel) {
		this.circularListingPagingDataModel = circularListingPagingDataModel;
	}
}
