/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.circular.panel.group.item;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.circular.IPanelGroupItemSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.ct.circular.PanelGroupItemReqDTO;
import my.ep.dto.ct.circular.PanelGroupItemResDTO;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItemGroup;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.ct.circular.panel.group.item.PanelGroupItemManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * Panel Group Item Backing Bean
 * 
 * @author Alam
 * @since 2/6/2015
 */
@ManagedBean(name = "panelGroupItemBackingBean") 
@ViewScoped
public class PanelGroupItemBackingBean extends BaseBackingBean<PanelGroupItemManagedBean> {
	
	private static final String NAV_TO_MAIN =  "/xhtml/ct/circular/grouppanelitem/panelItemMain.xhtml";
	public static final String NAV_TO_MAINTAIN = "/xhtml/ct/circular/grouppanelitem/panelItemGroupMaintenance.xhtml";
  
	private IPagingDataModel<CtPanelItemGroup> panelGroupItemDataModel = new PanelGroupItemPagingDataModel();	


	/**
	 * The method call by page to display list of status
	 * for panel group item
	 *
	 * @return SelectItem list  
	 */
	 public static SelectItem[] getStatusList() {
			SelectItem[] items =  new SelectItem[3];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()));
		
			items[1] = new SelectItem(DbValueConst.RECORD_STATUS_ACTIVE, ResourceBundleUtils.getMessage(
					"lbl.ct.active", ResourceBundleUtils.BUNDLE_COMMON_LABELS,	new FacesScopes().getUserLocale()));
			
			items[2] = new SelectItem(DbValueConst.RECORD_STATUS_SUSPENDED, ResourceBundleUtils.getMessage(
					"lbl.ct.inactive", ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()));
		  
			return items;
		}
	 
	 /**
		 * The method used to update panel group item list
		 * @return back to maintenance page
		 */
	 public String goToMaintaincePage(CtPanelItemGroup panelGroup){
		   getManagedBean().setCtPanelItemGroup(panelGroup);
		   return NAV_TO_MAINTAIN;  
	  }
	 
	 /**
		 * The method used to create panel group item list
		 * @return back to maintenance page
		 */
	  public String createpanelGroupItem(){
		   CtPanelItemGroup panelItemGroup= new CtPanelItemGroup();
		    getManagedBean().setCtPanelItemGroup(panelItemGroup);
		     return NAV_TO_MAINTAIN;
	   }
	 
	  /**
		 * The method used to render page on previous page
		 * @return back to search page
		 */
	    public String redirectItemSearch() {
			  return NAV_TO_MAIN;
		 }	
	 
	    /**
		 * The method used to validate / save / update the 
		 * panel item group with confirmation message
		 * @return the navigate URL 
		 * @throws Exception  if an exception occurred
		 * @see    Exception
		 */
	   public String savepanelGroupItemDtls(){
	 	 
		    CtPanelItemGroup panelGroup= getManagedBean().getCtPanelItemGroup();
		 
		    if(panelGroup.isBrandNew() && isExistPanelGroupItem(panelGroup.getGroupName())){
			         
		    	        FacesContext.getCurrentInstance().addMessage("panelItemId", new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.ERROR_750032, new String[] {panelGroup.getGroupName()},
							     MessageUtils.BUNDLE_COMMON_MESSAGES,new FacesScopes().getUserLocale())));
		    	        return NAV_TO_MAINTAIN;
		   	}
		   
		   PanelGroupItemReqDTO reqDTO = new PanelGroupItemReqDTO();
		   reqDTO.setCtPanelItemGroup(panelGroup);
		   IPanelGroupItemSO panelGroupItemSO= (IPanelGroupItemSO) EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IPanelGroupItemSO.class);
		
		   PanelGroupItemResDTO resDTO= panelGroupItemSO.saveOrUpdatePanelGroupItemDtls(reqDTO);
		   getManagedBean().setCtPanelItemGroup(resDTO.getCtPanelItemGroup());
		   if(resDTO.isSuccessful()){
		
			       if (panelGroup.isBrandNew()) {
				               FacesContext.getCurrentInstance().addMessage("panelItemId", 	new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							       MessageUtils.getMessage(MessageConst.INFO_758002, new String[] { resDTO.getCtPanelItemGroup().getGroupName()},
									   MessageUtils.BUNDLE_COMMON_MESSAGES,new FacesScopes().getUserLocale())));
				
			       }else{
					      FacesContext.getCurrentInstance().addMessage("groupIdMsg",new FacesMessage(FacesMessage.SEVERITY_INFO, null,
									MessageUtils.getMessage(MessageConst.INFO_758003, new String[] {resDTO.getCtPanelItemGroup().getGroupName(), null},
											MessageUtils.BUNDLE_COMMON_MESSAGES,new FacesScopes().getUserLocale())));
				   }
			}
		
		    if(!panelGroup.isBrandNew()){
			      return NAV_TO_MAINTAIN;
		
		     }else{
		          return NAV_TO_MAIN;
		     }
	    }
		 
	   /**
		 * The method used to validate panel item group 
		 * @return boolean value
		 */
		 public boolean isExistPanelGroupItem(String groupItemName) {
		
			    IPanelGroupItemSO panelGroupItemSO = (IPanelGroupItemSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IPanelGroupItemSO.class);
			    PanelGroupItemReqDTO reqDTO = new PanelGroupItemReqDTO();
				reqDTO.setGroupItemName(groupItemName);
				PanelGroupItemResDTO resDTO = panelGroupItemSO.retrievePanelGroupItemByGroupName(reqDTO);
				
				if (resDTO.getCtPanelItemGroup() != null) {
					     return true;
			
				} else {
					    return false;
				}
		 }
	  
		 /**
			 * The method used to reset the search criteria for 
			 * panel group item.
			 */
	     public void resetPanelGroupItemSearch(){
		 
		        getManagedBean().setLazy(false);
	            getManagedBean().setGroupName(new String());
	            getManagedBean().setStatus(null);
	            panelGroupItemDataModel = new  PanelGroupItemPagingDataModel();
	     }
	 
	     /**
	 	 * The method used to clear the previous viewed information before
	 	 * rendering the item group list on  screen.
	 	 */
	     public void preLoadPanelGroupItem(){
			     renewSesionObject();
			     FacesContext fc = FacesContext.getCurrentInstance();
			     fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_MAIN);
	     }
	 
	     /**
	 	 * The method used for searching the panel item group
	 	 * A warning message will display on screen when searching
	 	 * with empty search criteria.
	 	 * @throws Exception  if an exception occurred
	 	 * @see    Exception
	 	 */
	     public void searchPanelGroupItem(){
		       getManagedBean().setLazy(true);
		       panelGroupItemDataModel = new  PanelGroupItemPagingDataModel();
	      }
	 
	     /**
	 	 * @return the panelGroupItemDataModel
	 	 */
	      public IPagingDataModel<CtPanelItemGroup> getPanelGroupItemDataModel() {
		      return panelGroupItemDataModel;
	      }

	      /**
	  	 * @param panelGroupItemDataModel the panelGroupItemDataModel to set
	  	 */
	     public void setPanelGroupItemDataModel(
			  IPagingDataModel<CtPanelItemGroup> panelGroupItemDataModel) {
		      this.panelGroupItemDataModel = panelGroupItemDataModel;
	      }
	
	     /**
			 * Pagination for panel group item search list
			 * @return list of item group
		 */
	     private class PanelGroupItemPagingDataModel extends PagingDataModel<CtPanelItemGroup> {
			     private static final long serialVersionUID = 1L;

			     public PanelGroupItemPagingDataModel() {
				       super("panelGroupItemTabel", getManagedBean().isLazy());
		     	 }
			     
			     /*
					 * (non-Javadoc)
					 * 
					 * @see
					 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
					 * .biz.bean.search.ISearchBean)
					 */
			    @Override
			     public ISearchResultBean<CtPanelItemGroup> pageChangeListener(ISearchBean<CtPanelItemGroup> searchBean) {

			    	    try {	
					
					             ISearchBean<CtPanelItemGroup> searchBeanData = getSearchBeanData(searchBean);
					             PanelGroupItemReqDTO reqDTO = new PanelGroupItemReqDTO();
				                 reqDTO.setCtPanelItemGroupSearchBean(searchBeanData);
					
					             IPanelGroupItemSO reqSO = (IPanelGroupItemSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IPanelGroupItemSO.class);	
					             PanelGroupItemResDTO resDTO = reqSO.retrievePanelGroupItemBySearch(reqDTO);
					             getManagedBean().setLazy(true);
					
					             if (resDTO.isSuccessful()) {
						                return resDTO.getCtPanelItemGroupSearchResultBean();
					              }
				      } catch (Exception e) {
					        e.printStackTrace();
				      }
				  return null;
			}
		}
	      
	      /**
	     	 * This method used to construct the query for search criteria
	  	     * on the base of information
	  	 **/
	    private ISearchBean<CtPanelItemGroup> getSearchBeanData(ISearchBean<CtPanelItemGroup> searchBean){

			     if (getManagedBean().getGroupName() != null && !"".equalsIgnoreCase(getManagedBean().getGroupName())) {
				          SearchFilterBean codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
				          codesSearchBean.setSearchField("groupName");
				          codesSearchBean.setCaseInsensitive(true);
				          codesSearchBean.setSearchValue(getManagedBean().getGroupName());
				          searchBean.getDataFilterBeans().add(codesSearchBean);
			  
			     }  if(getManagedBean().getStatus() != null 	&& !"".equals(getManagedBean().getStatus())) {
				       SearchFilterBean codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
				       codesSearchBean.setSearchField("recordStatusFlg");
				       codesSearchBean.setSearchValue(getManagedBean().getStatus());
				       searchBean.getDataFilterBeans().add(codesSearchBean);
			    
		         } else {
				       List<String> activeAndInActiveStatus = new ArrayList<String>();
				       activeAndInActiveStatus.add(ParameterConst.RECORD_STATUS_INACTIVE);
				       activeAndInActiveStatus.add(ParameterConst.RECORD_STATUS_ACTIVE);
				       searchBean.getDataFilterBeans().add(new SearchFilterBean("recordStatusFlg",SearchOperator.OPERATOR_IN.value(), activeAndInActiveStatus));
			     }
			  return searchBean;
	     }	 
}
