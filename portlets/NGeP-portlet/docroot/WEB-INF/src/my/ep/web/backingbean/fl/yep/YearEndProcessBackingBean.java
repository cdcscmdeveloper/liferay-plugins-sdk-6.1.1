package my.ep.web.backingbean.fl.yep;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.fl.FlAttachment;
import my.ep.persistence.entity.fl.FlRemark;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.type.pm.RoleCode;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBaseBackingBean;
import my.ep.web.backingbean.fl.po.FulfilmentOrderBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.constant.report.CommonMethod;
import my.ep.web.managedbean.fl.yep.YearEndProcessManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.StatusHelper;

/**
 * Backing bean for year end process
 * 
 * @Author basri
 * @since 10/09/2019
 * */

@ManagedBean(name = "yearEndProcessBackingBean")
@ViewScoped
public class YearEndProcessBackingBean extends
		FulfilmentBaseBackingBean<YearEndProcessManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String SEARCH_YEP = "/xhtml/fl/yep/search/searchYearEndProcess.xhtml?faces-redirect=true";
	
	/** The Constant NAV_TO_MAINTAIN_PO. */
	public static final String NAV_TO_MAINTAIN_PO = "MaintainPO";
	
	/** The Constant NAV_TO_MAINTAIN_CO. */
	public static final String NAV_TO_MAINTAIN_CO = "MaintainCO";

	/**
	 * The method used to clear the previous viewed information before rendering
	 * the contract search screen.
	 */
	public void preRenderSearch(boolean trackingDiary) {
		renewSesionObject();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, SEARCH_YEP);
	}

	/**
	 * Selected order type.
	 * 
	 * @param e
	 *  the e
	 */
	public void selectedDocType(AjaxBehaviorEvent e) {
		String docType = (String) e.getComponent().getAttributes().get("value");

		// reset everything to false
		this.getManagedBean().setCheckDocType(false);
		resetSearchCriteria();
		if (docType != null) {
			if (docType.equals(ParameterConst.DOCTYPE_CONTRACT_ORDER)) {
				this.getManagedBean().setCheckDocType(true);
			}
		}
		getManagedBean().setSearchDocType(docType);
	}

	/**
	 * Reset search criteria.
	 */
	private void resetSearchCriteria() {
		/** to be implemented */
	}

	/**
	 * retrieve financial year options for current year and past 7 years
	 * 
	 * @return financial year options
	 */
	public static List<SelectItem> getFinancialYearOpt() {
		int years = 0;
		List<SelectItem> financialYearOpt = new ArrayList<SelectItem>();
		for (Integer i = 0; i < 7; i++) {
			years = CommonMethod.getYear(-i);
			financialYearOpt
					.add(new SelectItem(years, Integer.toString(years)));
		}
		return financialYearOpt;
	}
	
	/**
	 * Get status select items by module and doc type for dropdown.
	 * 
	 * @param moduleCode
	 *            the module code
	 * @param docType
	 *            the document type
	 * @return array of SelectItem
	 */
	public SelectItem[] getStatusDropdownValues(String moduleCode,
			String docType) {
		
		
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();

		try {
			List<PmStatusDesc> statusDescs = StatusHelper.getInstance()
					.getStatusDescList(moduleCode, docType, null);
			
			List<PmStatusDesc> statusDescsYep = new ArrayList<PmStatusDesc>();
			
				for (PmStatusDesc statusDesc : statusDescs) {
				
				if(statusDesc.getPmStatus().getStatusId()==41000
					||statusDesc.getPmStatus().getStatusId()==41005
					||statusDesc.getPmStatus().getStatusId()==41010
					||statusDesc.getPmStatus().getStatusId()==41015
					||statusDesc.getPmStatus().getStatusId()==41020
					||statusDesc.getPmStatus().getStatusId()==41025
					||statusDesc.getPmStatus().getStatusId()==41400
					||statusDesc.getPmStatus().getStatusId()==41410
					||statusDesc.getPmStatus().getStatusId()==41500
					||statusDesc.getPmStatus().getStatusId()==41505
					||statusDesc.getPmStatus().getStatusId()==41510
					||statusDesc.getPmStatus().getStatusId()==41515
					||statusDesc.getPmStatus().getStatusId()==41520
					||statusDesc.getPmStatus().getStatusId()==41525
					||statusDesc.getPmStatus().getStatusId()==41900
					||statusDesc.getPmStatus().getStatusId()==41910){
					
					statusDescsYep.add(statusDesc);
				}
			}
				
				
			items = new SelectItem[statusDescsYep.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale()));
			int i = 1;
			for (PmStatusDesc statusDescYep : statusDescsYep) {
				
					items[i++] = new SelectItem(statusDescYep.getPmStatus()
							.getStatusId(), statusDescYep.getStatusName());
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}

		return items;
	}
	
	
	public void searchYep(){
		
		getManagedBean().getFlYepDataModel().reloadData();
		
	}
	
	/**
	 * Search reset.
	 */
	public void searchReset(){
		renewSesionObject();
	}
	
	
	public String navigateToPage(){
		
		IFulfilmentRequestSO fulfilmentRequestSO = null;
		IFulfilmentOrderSO fulfilmentOrderSO = null;
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		
		String navgPage = "xhtml/fl/yep/search/searchYearEndProcess.xhtml";
		String docType = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("docType");
		String flDocId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("docId");
		String buttonVal = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("buttonVal");
		String statusDesc = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("statusDesc");
		
		
		 if(docType.equals(ParameterConst.DOCTYPE_PURCHASE_ORDER)){
				fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
				reqDTO.setDocType(ParameterConst.DOCTYPE_PURCHASE_ORDER);
				reqDTO.setFlOrderId(Long.valueOf(flDocId));
				FulfilmentResDTO resDTO = fulfilmentOrderSO.retrieveFulfilmentOrderById(reqDTO);			
				fulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentRequestSO.class);

				FulfilmentOrderBackingBean flOrderBB=new FulfilmentOrderBackingBean();
				flOrderBB.renewSesionObject();
				flOrderBB.getManagedBean().setButtonVal(buttonVal);
				String[] outcome = new String[1];
				if(BpmOutcomeConst.BPM_CANCEL.equalsIgnoreCase(buttonVal)){
					outcome[0] = BpmOutcomeConst.BPM_CANCEL;
					flOrderBB.getManagedBean().setOutComes(outcome);
				}
				flOrderBB.getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
				flOrderBB.getManagedBean().setSelectedTabIndex(0);
				flOrderBB.getManagedBean().setPmUser(reqDTO.getPmUser());
				flOrderBB.getManagedBean().setFulfilmentOrder(resDTO.getFlOrder());
				flOrderBB.getManagedBean().setRemoveFactoring(false);
				flOrderBB.getManagedBean().setDocMaintainRemarks(true);
				
				flOrderBB.getManagedBean().setBackToPrevious("searchYearEndProcess");
				if(resDTO.getFlOrder().isFactored()) {
					flOrderBB.getManagedBean().setFactoringCheck("true");
					flOrderBB.getFactoringCompanyDtl();
				}
				if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_VIEW)){
					flBB.initializeRemarkAndAttachement(null, resDTO.getRemarkList(), resDTO.getAttachmentList());
					flBB.getManagedBean().setHideRemarks(true);
				} else if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE) || buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
					flBB.initializeRemarkAndAttachement(null, new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
					flBB.getManagedBean().setDocMaintainRemarks(true);
				} else {
					flBB.initializeRemarkAndAttachement(null, new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
				}
				
				navgPage=NAV_TO_MAINTAIN_PO;
			}else if(docType.equals(ParameterConst.DOCTYPE_CONTRACT_ORDER)){
				fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
				reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_ORDER);
				reqDTO.setFlOrderId(Long.valueOf(flDocId));
				FulfilmentResDTO coResDTO=fulfilmentOrderSO.retrieveFulfilmentOrderById(reqDTO);
				
				FulfilmentOrderBackingBean flOrderBB=new FulfilmentOrderBackingBean();
				flOrderBB.renewSesionObject();
				flOrderBB.getManagedBean().setButtonVal(buttonVal);
				flOrderBB.getManagedBean().setSelectedTabIndex(0);
				flOrderBB.getManagedBean().setFlRequest(coResDTO.getFlOrder().getFlFulfilmentRequest());
				flOrderBB.getManagedBean().setFulfilmentOrder(coResDTO.getFlOrder());
				flOrderBB.getManagedBean().setPmUser(reqDTO.getPmUser());
				flOrderBB.getManagedBean().setBackToPrevious("searchYearEndProcess");
				
				if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_VIEW)){
					flBB.initializeRemarkAndAttachement(null, coResDTO.getRemarkList(), coResDTO.getAttachmentList());
					flBB.getManagedBean().setHideRemarks(true);
				} else if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE) || buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
					flBB.initializeRemarkAndAttachement(null, new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
				} else {
					flBB.initializeRemarkAndAttachement(null, new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
				}	
				
				navgPage=NAV_TO_MAINTAIN_CO;
			}
		 
		 return navgPage;
	}

}
