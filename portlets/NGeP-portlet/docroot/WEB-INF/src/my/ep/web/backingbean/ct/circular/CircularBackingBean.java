/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.circular;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.circular.ICircularSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.ct.circular.CircularReqDTO;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.circular.cc.CentralContractBackingBean;
import my.ep.web.backingbean.ct.circular.panel.PanelBackingBean;
import my.ep.web.managedbean.ct.circular.CircularManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * Circular Backingbean
 * 
 * @author kieyun
 * @Created 23/12/2014
 */
@ManagedBean
@ViewScoped
public class CircularBackingBean extends BaseBackingBean<CircularManagedBean>{
	
	public void searchCircularListing() throws Exception {
		try {
			if ((getManagedBean().getSearchCircularNo() == null || getManagedBean().getSearchCircularNo().length() == 0) && 
				(getManagedBean().getSearchCircularName() == null || getManagedBean().getSearchCircularName().length() == 0) &&
				(getManagedBean().getSearchPhysicalCircularNo() == null || getManagedBean().getSearchPhysicalCircularNo().length() == 0) && 
				(getManagedBean().getSearchCircularDocType() == null) && (getManagedBean().getSearchStatus() == null)) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_WARN, null, MessageUtils.getMessage(MessageConst.WARN_755000,
							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			}
			else {
				getManagedBean().getCircularListingPagingDataModel().reloadData();
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), "circular search failed."));
		}			
	}

	public SelectItem[] getCircularDocTypeValues() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> circularDocTypes = new ArrayList<String>();
		circularDocTypes.add(ParameterConst.DOCTYPE_CENTRAL_CONTRACT_CIRCULAR);
		circularDocTypes.add(ParameterConst.DOCTYPE_PANEL_CIRCULAR);
		searchBean.getDataFilterBeans().add(new SearchFilterBean("pmParameter.parameterCode", SearchOperator.OPERATOR_IN.value(), circularDocTypes));
		return getDropdownCodeValuesBySearchBean(ParameterTypeConst.DOCUMENT_TYPES, searchBean);
	}
	
	public SelectItem[] getCircularStatusValues() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> statusList = new ArrayList<String>();
		statusList.add(ParameterConst.RECORD_STATUS_ACTIVE);
		statusList.add(ParameterConst.RECORD_STATUS_CANCLED);
		searchBean.getDataFilterBeans().add(new SearchFilterBean("pmParameter.parameterCode", SearchOperator.OPERATOR_IN.value(), statusList));
		return getDropdownCodeValuesBySearchBean(ParameterTypeConst.RECORD_STATUS, searchBean);
	}
	
	public void searchReset(){
		this.getManagedBean().setSearchCircularNo(null);
		this.getManagedBean().setSearchCircularName(null);
		this.getManagedBean().setSearchPhysicalCircularNo(null);
		this.getManagedBean().setSearchCircularDocType(null);
		this.getManagedBean().setSearchStatus(null);
		getManagedBean().getCircularListingPagingDataModel().reloadData();
	}
	
	public String gotoPage(Long circularVerId, String docType) {
		ICircularSO circularSO = null;
		try {
			circularSO = (ICircularSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, ICircularSO.class);
			
			CircularReqDTO reqDTO = new CircularReqDTO();
			reqDTO.setCircularVerId(circularVerId);
			if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_PANEL_CIRCULAR)) {
				PanelBackingBean ppBB = new PanelBackingBean();
				ppBB.getManagedBean().setSelectionOfZone(false);
				ppBB.getManagedBean().setSelectedPanelItems(null);
				ppBB.getManagedBean().setSelectedPanelZone(null);
				ppBB.getManagedBean().setSelectedTabIndex(0);
				ppBB.getManagedBean().setAction("view");
				ppBB.getManagedBean().setUpdateAction(false);
				ppBB.getManagedBean().setViewContract(true);
				ppBB.getManagedBean().setOutComes(new String[]{});
				ppBB.setPanelCircularVar(circularSO.retrieveCircularVerById(reqDTO));
				ppBB.getManagedBean().setFromCircularNotice(true);
				return "../panel/circularBase.xhtml?faces-redirect=true";
			} else {
				CentralContractBackingBean ccBB = new CentralContractBackingBean();
				ccBB.getManagedBean().setSelectedCircularContract(null);
				ccBB.getManagedBean().setSelectedCircularItem(null);
				ccBB.getManagedBean().setSelectedZoneItems(null);
				ccBB.getManagedBean().setSelectedTabIndex(0);
				ccBB.getManagedBean().setAction("view");
				ccBB.getManagedBean().setViewContract(true);
				ccBB.getManagedBean().setUpdateAction(false);
				ccBB.getManagedBean().setOutComes(new String[]{});
				ccBB.setContractCircularVar(circularSO.retrieveCircularVerById(reqDTO));	
				ccBB.getManagedBean().setFromCircularNotice(true);
				return "../centralcontract/circularBase.xhtml?faces-redirect=true";
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			circularSO = null;
		}
		return null;
	}
	
}
