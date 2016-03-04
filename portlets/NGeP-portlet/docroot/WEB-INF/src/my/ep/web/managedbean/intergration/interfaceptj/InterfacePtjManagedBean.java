/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.managedbean.intergration.interfaceptj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.context.RequestContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfacePtjSO;
import my.ep.dto.pm.common.interfaceptj.InterfacePtjReqDTO;
import my.ep.dto.pm.common.interfaceptj.InterfacePtjResDTO;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.pm.integration.PmInterfacePtj;
import my.ep.web.backingbean.integration.interfaceptj.InterfacePtjBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * The Class CommonManagedBean.
 */
@ManagedBean(name = "interfacePtjMB")
@SessionScoped
public class InterfacePtjManagedBean extends BaseManagedBean {
	
	private boolean refreshFlag = true;
	private List<PmInterfacePtj> cloneLs;
	private boolean checkBoxAll;

	private IPagingDataModel<PmInterfacePtj> searchDataModel = new SearchPagingDataModel();
	



	private class SearchPagingDataModel extends
	PagingDataModel<PmInterfacePtj> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new search paging data model.
		 */
		public SearchPagingDataModel() {
			super("interfacePtjListId");
			// TODO Auto-generated constructor stub
		}

		/**
		 * page Change Listener.
		 * 
		 * @param searchBean
		 *            (ISearchBean<VCmSupplierItem> searchBean(mandatory))
		 * @return ISearchBean<VCmSupplierItem>
		 */
		@Override
		protected ISearchResultBean<PmInterfacePtj> pageChangeListener(
				ISearchBean<PmInterfacePtj> searchBean) {

			/*if () {
				log.debug("******SearchResultBean******");
				return new SearchResultBean();
			}*/

			IInterfacePtjSO ptjSO = (IInterfacePtjSO) BusinessDelegate
					.getInstance().getServiceObject("pm", IInterfacePtjSO.class);

			try {

				InterfacePtjReqDTO reqDTO = new InterfacePtjReqDTO();
				InterfacePtjBackingBean bb = new InterfacePtjBackingBean();
				
				reqDTO.setViewSearchBean(searchBean);
				
				InterfacePtjResDTO resDTO = ptjSO.searchGfMasPtj(reqDTO);
				System.out.println("getTotalRecord:"+resDTO.getViewGfMasPtjResultBean().getTotalRecord());
				
				
				
				Map tempMap = new HashMap();
				for (int i = 0; i < resDTO.getViewGfMasPtjResultBean()
						.getResultList().size(); i++) {
					PmInterfacePtj ptj = resDTO.getViewGfMasPtjResultBean()
							.getResultList().get(i);

					if (bb.getManagedBean().getCloneLs() != null
							&& bb.getManagedBean().getCloneLs()
									.size() > 0) {

						for (PmInterfacePtj obj : bb.getManagedBean()
								.getCloneLs()) {

							if (ptj.getInterfacePtjId() == obj
									.getInterfacePtjId()) {

								if (!tempMap.containsKey(obj
										.getInterfacePtjId())) {
									ptj.setSelected(true);
									tempMap.put(obj.getInterfacePtjId(),
											obj.getInterfacePtjId());
								}
							}
						}

					}

				}

				System.out
						.println("bb.getManagedBean().isRefreshDraftSuppItemFlag()"
								+ bb.getManagedBean()
										.isRefreshFlag());
				if (bb.getManagedBean().isRefreshFlag()) {
					RequestContext.getCurrentInstance().execute(
							"refreshPtj();");
				}
				
				
				

				return resDTO.getViewGfMasPtjResultBean();
			} catch (Exception e) {
				e.printStackTrace();
				return new SearchResultBean();
			} finally {
				ptjSO = null;
			}
		}

	}



	/**
	 * @return the searchDataModel
	 */
	public IPagingDataModel<PmInterfacePtj> getSearchDataModel() {
		return searchDataModel;
	}



	/**
	 * @param searchDataModel the searchDataModel to set
	 */
	public void setSearchDataModel(IPagingDataModel<PmInterfacePtj> searchDataModel) {
		this.searchDataModel = searchDataModel;
	}



	/**
	 * @return the refreshFlag
	 */
	public boolean isRefreshFlag() {
		return refreshFlag;
	}



	/**
	 * @param refreshFlag the refreshFlag to set
	 */
	public void setRefreshFlag(boolean refreshFlag) {
		this.refreshFlag = refreshFlag;
	}



	/**
	 * @return the cloneLs
	 */
	public List<PmInterfacePtj> getCloneLs() {
		if(cloneLs==null)
			cloneLs = new ArrayList<PmInterfacePtj>();
		return cloneLs;
	}



	/**
	 * @param cloneLs the cloneLs to set
	 */
	public void setCloneLs(List<PmInterfacePtj> cloneLs) {
		this.cloneLs = cloneLs;
	}



	/**
	 * @return the checkBoxAll
	 */
	public boolean isCheckBoxAll() {
		return checkBoxAll;
	}



	/**
	 * @param checkBoxAll the checkBoxAll to set
	 */
	public void setCheckBoxAll(boolean checkBoxAll) {
		this.checkBoxAll = checkBoxAll;
	}
	
	
	
	


}
