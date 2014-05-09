/**
 * 
 */
package my.ep.web.backingbean;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.EpPortletUtil;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.SecurityFilterUtil;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.StatusHelper;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;


/**
 * @author siew mei
 *
 */
public abstract class BaseBackingBean<T extends BaseManagedBean> {
	private static Logger log = Logger.getLogger(BaseBackingBean.class);
	
	private Class<T> managedBeanClazz;
	private String managedBeanName;
	private T managedBean;
	
	@SuppressWarnings("unchecked")
	public BaseBackingBean(){
		if (log.isDebugEnabled()){
			log.debug("In BaseBackingBean constructor");
			
		}
		Class<?> clazz = this.getClass();
		Type type = clazz.getGenericSuperclass();
		//while (true) {
		    if(log.isDebugEnabled()) { 
    			log.debug("#######Clazz = " + clazz);
    			log.debug("#######type = " + type);
		    }

			/*if (clazz == null) {
				break;
			}*/
			if (type != null && type instanceof ParameterizedType) {
				this.managedBeanClazz = (Class<T>) ((ParameterizedType) type)
						.getActualTypeArguments()[0];
				if (null != managedBeanClazz){
					ManagedBean managedBeanAnnotation = managedBeanClazz.getAnnotation(ManagedBean.class);
					System.out.println("managedBeanAnnotation:" +managedBeanAnnotation);
					if (null != managedBeanAnnotation){
						managedBeanName = managedBeanAnnotation.name() + getConcatId();
					}
				}
			}
		//}
		
		
		if (log.isDebugEnabled()){
			log.debug("managedBeanName:" + managedBeanName);
		}
		if (null !=managedBeanName){
			FacesScopes facesScopes = new FacesScopes();
			T tmpBean = (T)facesScopes.getSessionScope(managedBeanName);
			if(null == tmpBean){
				managedBean = renewSesionObject();
			}else{
				managedBean = tmpBean;
			}
		}
		
		if (log.isDebugEnabled()){
			log.debug("managedBean:" + managedBean);
		}
	}
	
	
	/**
	 * @return the managedBeanClazz
	 */
	public Class<T> getManagedBeanClazz() {
		return managedBeanClazz;
	}

	/**
	 * @param managedBeanClazz the managedBeanClazz to set
	 */
	public void setManagedBeanClazz(Class<T> managedBeanClazz) {
		this.managedBeanClazz = managedBeanClazz;
	}
	
	public T renewSesionObject(){
		if (log.isDebugEnabled()){
			log.debug("In renewSesionObject");
		}
		try{
			managedBean = managedBeanClazz.newInstance();
			FacesScopes facesScopes = new FacesScopes();
			facesScopes.setSessionScope(managedBeanName, managedBean);
		}catch(Exception ex){
			log.error("Failed to renew managed bean: " + managedBeanName, ex);
		}
		return managedBean;
	}
	
	/**
	 * @return the managedBean
	 */
	public T getManagedBean() {
		return managedBean;
	}
	
	private String getConcatId(){
		String concatId = "";
		
		
		FacesScopes facesScope = new FacesScopes();
		
		String portletId = (String)facesScope.getRequestScope(WebKeys.PORTLET_ID);
		
		String pageId = null;
		ThemeDisplay themeDisplay = (ThemeDisplay)facesScope.getRequestScope(WebKeys.THEME_DISPLAY);
		if (themeDisplay!=null){
			pageId = String.valueOf(themeDisplay.getPlid());
			facesScope.setSessionScope("title", themeDisplay.getPortletDisplay().getTitle());
		}
		
		if (StringUtils.isNotBlank(portletId) && StringUtils.isNotBlank(pageId)){
			concatId = "_" + portletId + "_" + pageId;
		}
		
		return concatId;
	}
	
	/* Utility method */
	public String userImagePath(String userId){
		FacesScopes facesScopes = new FacesScopes();
		String imagePath = "";
		if (userId != null){
			User user = null;
			ThemeDisplay themeDisplay = (ThemeDisplay)facesScopes.getRequest().getAttribute(WebKeys.THEME_DISPLAY);
			try{
				user = UserLocalServiceUtil.getUserByScreenName(themeDisplay.getCompanyId(), userId);
				if (user!= null){
					imagePath ="/.."+ user.getPortraitURL((ThemeDisplay)facesScopes.getRequest().getAttribute(WebKeys.THEME_DISPLAY));
				}
			}catch(Exception ex){
				log.error( ex.getMessage(), ex);
			}
		}

		return imagePath;

	}
	
	public boolean compareUserName(String username){
		FacesScopes facesScopes = new FacesScopes();
		if(username.equals(facesScopes.getUserScreenName())) {
			return true;
		} else {
			return false;
		}
	}	

	/*protected <RETURN_CLASS extends BaseEntity> List<RETURN_CLASS> toEntityBeans(List<? extends ValueBean<RETURN_CLASS>> valueBeans) {
		List<RETURN_CLASS> list = new ArrayList<RETURN_CLASS>();
		
		for (ValueBean<RETURN_CLASS> valueBean : valueBeans) {
			list.add(valueBean.getEntity());
		}
		
		return list;
	}
	
	public <ENTITY_CLASS extends BaseEntity> void addListBySelectedList(List<ENTITY_CLASS> list, List<ENTITY_CLASS> selectedList) {		
		for (ENTITY_CLASS entity : selectedList) {			
			if (entity.isSelected()) {
				entity.setSelected(false);
				
				list.add(entity);
			}
		}
	}
	
	public <ENTITY_CLASS extends BaseEntity> void deleteRowsAction(DataValueBean<ENTITY_CLASS> dataValueBean) {		
		List<ENTITY_CLASS> displayedList = dataValueBean.getDisplayedList();
		List<ENTITY_CLASS> removedList = dataValueBean.getRemovedList();
		List<ENTITY_CLASS> listToBeRemoved = new ArrayList<ENTITY_CLASS>();
		
		for (ENTITY_CLASS entity : displayedList) {
			if (entity.isSelected()) {
				entity.setDeleted(true);
				
				listToBeRemoved.add(entity);
			}
		}
		
		// remove from display list.
		displayedList.removeAll(listToBeRemoved);
		
		// added into removed list.
		removedList.addAll(listToBeRemoved);
	}
	
	public <ENTITY_CLASS extends BaseEntity> void selectAllAction(boolean selectAll, List<ENTITY_CLASS> list) {		
		for (ENTITY_CLASS valueBean : list) {
			valueBean.setSelected(selectAll);
		}
    }*/
	
	public <ENTITY_CLASS> void deleteRowAction(List<ENTITY_CLASS> list, List<ENTITY_CLASS> toBeRemovedList) {
		if (log.isDebugEnabled()) {
			log.debug("deleteRowAction");
		}
		
		if (list != null && toBeRemovedList != null) {
			list.removeAll(toBeRemovedList);	
		}
	}
	
	/**
	 * Create search bean.
	 * 
	 * @param classz
	 * 			the return class type.
	 * @param componentId
	 * 			the component id.
	 * @return	ISearchBean instance.
	 */
	protected <RETURN_CLASS extends Serializable> ISearchBean<RETURN_CLASS> createSearchBean(Class<RETURN_CLASS> classz, String componentId) {
		return new SearchBean<RETURN_CLASS>(EpPortletUtil.getLoginUserId(FacesContext.getCurrentInstance()),
				SecurityFilterUtil.getDataFilterScreenMetadatas(componentId));
	}
	
	/**
	 * Get parameter id select items by parameter type for dropdown.
	 * 
	 * @param parameterType
	 * 			the parameter type
	 * @return array of SelectItem
	 */
	public SelectItem[] getDropdownValues(String parameterType) {
		return getDropdownValuesBySearchBean(parameterType, null);
	}
	
	/**
	 * Get parameter id select items by parameter type and search bean for dropdown.
	 * 
	 * @param parameterType
	 * 			the parameter type
	 * @param searchBean
	 * 			the search bean instance
	 * @return array of SelectItem
	 */
	public SelectItem[] getDropdownValuesBySearchBean(String parameterType, ISearchBean<PmParameterDesc> searchBean) {
		SelectItem[] items = null;
		
		try {
			List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance()
					.getParameterDescListBySearchBean(parameterType, searchBean);
			
		    items = new SelectItem[parameterDescs.size()+1];
		    items[0] = new SelectItem(null, "- Select One -");
		    int i = 1;
		    for(PmParameterDesc parameterDesc : parameterDescs) {
		    	items[i++] = new SelectItem(
		    			parameterDesc.getPmParameter().getParameterId(), 
		    			parameterDesc.getCodeName());
		    }
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null, 
					new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							e.toString(),
							e.getMessage()));
		}
		
	    return items;
	}
	
	/**
	 * Get parameter code select items by parameter type for dropdown.
	 * 
	 * @param parameterType
	 * @return array of SelectItem
	 */
	public SelectItem[] getDropdownCodeValues(String parameterType) {
		return getDropdownCodeValuesBySearchBean(parameterType, null);
	}
	
	/**
	 * Get parameter code select items by parameter type and search bean for dropdown.
	 * 
	 * @param parameterType
	 * 			the parameter type
	 * @param searchBean
	 * 			the search bean instance
	 * @return array of SelectItem
	 */
	public SelectItem[] getDropdownCodeValuesBySearchBean(String parameterType, ISearchBean<PmParameterDesc> searchBean) {
		SelectItem[] items = null;
		
		try {
			List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance()
					.getParameterDescListBySearchBean(parameterType, searchBean);
			
		    items = new SelectItem[parameterDescs.size()+1];
		    items[0] = new SelectItem(null, "- Select One -");
		    int i = 1;
		    for(PmParameterDesc parameterDesc : parameterDescs) {
		    	items[i++] = new SelectItem(
		    			parameterDesc.getPmParameter().getParameterCode(), 
		    			parameterDesc.getCodeName());
		    }
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null, 
					new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							e.toString(), 
							e.getMessage()));
		}
		
	    return items;
	}
	
	/**
	 * Get Parameter description by parameter.
	 * 
	 * @param parameter
	 * 			the PmParameter instance.
	 * @return parameter description
	 */
	public String getParameterDesc(PmParameter parameter) {
		if (parameter != null) {
			return getParameterDesc(
					parameter.getPmParameterType().getParameterType(),
					parameter.getParameterCode());	
		} else {
			return null;
		}
	}
	
	/**
	 * Get Parameter description by type and code.
	 * 
	 * @param parameterType
	 * 			the parameter type
	 * @param parameterCode
	 * 			the parameter code
	 * @return parameter description
	 */
	public String getParameterDesc(String parameterType, String parameterCode) {		
		String parameterDesc = null;
		
		// Get the parameter description list.
		List<PmParameterDesc> list = ParameterHelper.getInstance()
				.getParameterDescList(parameterType);
		
		for (PmParameterDesc pmParameterDesc : list) {
			// compare matched parameterCode.
			if (pmParameterDesc.getPmParameter().getParameterCode()
					.equals(parameterCode)) {
				parameterDesc = pmParameterDesc.getCodeName();
				break;
			}
		}
		
		return parameterDesc;
	}
	
	/**
	 * Get status select items by module and doc type for dropdown.
	 * 
	 * @param parameterType
	 * 			the parameter type
	 * @return array of SelectItem
	 */
	public SelectItem[] getStatusDropdownValues(String moduleCode, 
			String docType) {
		SelectItem[] items = null;
		
		try {
			List<PmStatusDesc> statusDescs = StatusHelper.getInstance()
					.getStatusDescList(moduleCode, docType);
			
		    items = new SelectItem[statusDescs.size()+1];
		    items[0] = new SelectItem(null, "- Select One -");
		    int i = 1;
		    for(PmStatusDesc statusDesc : statusDescs) {
		    	items[i++] = new SelectItem(
		    			statusDesc.getPmStatus().getStatusId(), 
		    			statusDesc.getStatusName());
		    }
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null, new FacesMessage(
							FacesMessage.SEVERITY_ERROR, 
							e.toString(), 
							e.getMessage()));
		}
		
	    return items;
	}
	
	/**
	 * Get Status description by status id.
	 * 
	 * @param statusId
	 * 			the status id.
	 * @return status description
	 */
	public String getStatusDescById(Long statusId) {		
		String statusDesc = null;
		
		if (statusId != null) {
			// Get the status description.
			PmStatusDesc pmStatusDesc = StatusHelper.getInstance()
					.getStatusDescById(statusId);
			
			if (pmStatusDesc != null) {
				statusDesc = pmStatusDesc.getStatusName();
			}
		}
		
		return statusDesc;
	}
	
	/**
	 * Get parameter code select items by parameter type for radio buttons.
	 * 
	 * @param parameterType
	 * 			the parameter type
	 * @return array of SelectItem
	 */
	public SelectItem[] getRadioButtonCodeValues(String parameterType) {
		SelectItem[] items = null;
		try {
			List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance()
					.getParameterDescList(parameterType);
			
		    items = new SelectItem[parameterDescs.size()];
		    int i = 0;
		    for(PmParameterDesc parameterDesc : parameterDescs) {
		    	items[i++] = new SelectItem(
		    			parameterDesc.getPmParameter().getParameterCode(), 
		    			parameterDesc.getCodeName());
		    }
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null, 
					new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							e.toString(), 
							e.getMessage()));
		}
		
	    return items;
	}
	
}
