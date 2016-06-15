package my.ep.web.backingbean.ct.dashboard;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.ct.dashboard.ContractDashboardManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

@ManagedBean(name="contractDashboardBackingBean")
@ViewScoped
public class ContractDashboardBackingBean extends BaseBackingBean<ContractDashboardManagedBean>{

	private IPagingDataModel<CtContract> ctDashboardDataModel  = new ctDashboardPagingDataModel();
	public ContractDashboardBackingBean(){
	}
	
	@PostConstruct
	public void init(){
		ctDashboardDataModel.reloadData();
	}
	
	public SelectItem[] getCtExpiryDaysMenu() {		
			SelectItem[] items = null;
			
			try {
			  	items = new SelectItem[2];
			   /* items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", 
			    		ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
	*/	    	 items[0] = new SelectItem(new Long(1), "less than 30 days");
		    	 items[1] = new SelectItem(new Long(2), "between 90 to 120 days");
			} catch (Exception e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
			}
		    return items;
		}
	
		public static Date addDays(Date date, int days)
			{
			    Calendar cal = Calendar.getInstance();
			    cal.setTime(date);
			    cal.add(Calendar.DATE, days); 
			    return cal.getTime();
			}
		
		public void filterCtExpDetails(){
			    ctDashboardDataModel.reloadData();
			}
			
	/**
		 * Pagination for available contract search list
		 */
		private class ctDashboardPagingDataModel extends PagingDataModel<CtContract> {
			private static final long serialVersionUID = 1L;

			public ctDashboardPagingDataModel() {
				super("tblCtExpId", true);
			}
			
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
			 * .biz.bean.search.ISearchBean)
			 */
			@Override
			public ISearchResultBean<CtContract> pageChangeListener(ISearchBean<CtContract> searchBean) {
				IContractSO contractSO = null;
				try {
					    
					if (getManagedBean().getCtExpInDays() != null) {
						    if(getManagedBean().getCtExpInDays().intValue() ==1){
						     	searchBean.getDataFilterBeans().add(new SearchFilterBean("latestContractVer.ctContractValue.expDate",SearchOperator.OPERATOR_LESS.value(), addDays(new Date(), 30)));		
						     	searchBean.getDataFilterBeans().add(new SearchFilterBean("latestContractVer.ctContractValue.expDate",SearchOperator.OPERATOR_GREATER_EQUAL.value(), new Date()));	
						    }else {
						    	searchBean.getDataFilterBeans().add(new SearchFilterBean("latestContractVer.ctContractValue.expDate",SearchOperator.OPERATOR_LESS.value(), addDays(new Date(), 120)));		
						     	searchBean.getDataFilterBeans().add(new SearchFilterBean("latestContractVer.ctContractValue.expDate",SearchOperator.OPERATOR_GREATER_EQUAL.value(), addDays(new Date(), 90)));	
						    } 
					} else {
					
						searchBean.getDataFilterBeans().add(new SearchFilterBean("latestContractVer.ctContractValue.expDate",SearchOperator.OPERATOR_LESS.value(), addDays(new Date(), 30)));		
				     	searchBean.getDataFilterBeans().add(new SearchFilterBean("latestContractVer.ctContractValue.expDate",SearchOperator.OPERATOR_GREATER_EQUAL.value(), new Date()));	
			
					}
					
					contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
					ContractReqDTO reqDTO = new ContractReqDTO();
					reqDTO.setContractSearchBean(searchBean);
					ContractResDTO resDTO = contractSO.searchDashBoardContract(reqDTO);
						
					if (resDTO.isSuccessful()) {
						return resDTO.getContractResultBean();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		}


	public IPagingDataModel<CtContract> getCtDashboardDataModel() {
		return ctDashboardDataModel;
	}


	public void setCtDashboardDataModel(
			IPagingDataModel<CtContract> ctDashboardDataModel) {
		this.ctDashboardDataModel = ctDashboardDataModel;
	}

}
