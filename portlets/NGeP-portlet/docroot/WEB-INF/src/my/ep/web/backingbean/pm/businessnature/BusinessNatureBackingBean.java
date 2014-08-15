package my.ep.web.backingbean.pm.businessnature;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.pm.common.businessnature.IBusinessNatureSO;
import my.ep.dto.pm.common.businessnature.BusinessNatureReqDTO;
import my.ep.dto.pm.common.businessnature.BusinessNatureResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmBusinessNature;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.pm.businessnature.BusinessNatureManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * @author DK
 * 
 */
@ManagedBean(name = "businessNatureBackingBean")
@ViewScoped
public class BusinessNatureBackingBean extends BaseBackingBean<BusinessNatureManagedBean> {

	private static final Logger log = EpLogger
			.getLogger(BusinessNatureManagedBean.class);
	
	private IPagingDataModel<PmBusinessNature> businessNatureDataModel = new BusinessNaturePagingDataModel();	

	
	public BusinessNatureBackingBean() {
		log.info("inside constructor");

	}
	
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}
	
	/**
	 * reload to business nature search page 
	 * 
	 */		
	public String redirectBusinessSearch() {
		debugLogger("redirectBusinessSearch");

		return "/xhtml/pm/businessnature/businessNatureMain.xhtml";

	}	
	
	/**
	 * reload to business nature maintain page
	 * 
	 */	
	public String createBusinessNature() {
		debugLogger("createBusinessNature");
		PmBusinessNature pmBusinessNature = new PmBusinessNature();
		this.getManagedBean().setPmBusinessNature(pmBusinessNature);
		
		return "/xhtml/pm/businessnature/businessNatureMaintenance.xhtml";

	}		

	/**
	 * search business nature info based 
	 * on codes and names search
	 */
	private ISearchBean<PmBusinessNature> getSearchBeanData(ISearchBean<PmBusinessNature> searchBean){		

		return searchBean;
	}		
	
	public void retrieveBusinessNature(){
		debugLogger("retrieveBusinessNature");
		
		businessNatureDataModel.reloadData();
		
	}

	public IPagingDataModel<PmBusinessNature> getBusinessNatureDataModel() {
		return businessNatureDataModel;
	}

	public void setBusinessNatureDataModel(
			IPagingDataModel<PmBusinessNature> businessNatureDataModel) {
		this.businessNatureDataModel = businessNatureDataModel;
	}

	private class BusinessNaturePagingDataModel extends PagingDataModel<PmBusinessNature> {
		private static final long serialVersionUID = 1L;

		public BusinessNaturePagingDataModel() {
			super("businessNatureSR");
		}

		@Override
		public ISearchResultBean<PmBusinessNature> pageChangeListener(ISearchBean<PmBusinessNature> searchBean) {

			try {	
				ISearchBean<PmBusinessNature> searchBeanData = getSearchBeanData(searchBean);
				BusinessNatureReqDTO reqDTO = new BusinessNatureReqDTO();
				reqDTO.setBusinessNatureSearchBean(searchBeanData);
				
				IBusinessNatureSO reqSO = (IBusinessNatureSO) BusinessDelegate.getInstance().
						getServiceObject("pm", IBusinessNatureSO.class);	
				BusinessNatureResDTO  resDTO = reqSO.retrieveBusinessNatureBySearch(reqDTO);
				if (resDTO.isSuccessful()) {
					return resDTO.getBusinessNatureSearchResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}
	}

	/**
	 * get business nature info into maintenance page
	 */
	public String goToBusinessMaintaincePage(PmBusinessNature pmBusinessNature) {
		debugLogger("goToBusinessMaintaincePage");
		this.getManagedBean().setPmBusinessNature(pmBusinessNature);

		return "/xhtml/pm/businessnature/businessNatureMaintenance.xhtml";
	}		
	
}
