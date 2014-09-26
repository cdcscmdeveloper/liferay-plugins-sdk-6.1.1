/**
 * 
 */
package my.ep.web.backingbean;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.constant.EpPropertiesConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.exceptionhandler.EpExceptionHandler;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.AddressUtils;
import my.ep.web.util.EpPortletUtil;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;
import my.ep.web.util.SecurityFilterUtil;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.StatusHelper;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;


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
					if (null != managedBeanAnnotation){
						managedBeanName = managedBeanAnnotation.name()/* + getConcatId()*/;
					}
					
					if (StringUtils.isBlank(managedBeanName)) {
						managedBeanName = StringUtils.uncapitalize(managedBeanClazz.getSimpleName());
					}
				}
			}
		//}
		
		if (log.isDebugEnabled()){
			log.debug("managedBeanName:" + managedBeanName);
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
	
	public void renewSesionObject(){
		if (log.isDebugEnabled()){
			log.debug("In renewSesionObject");
		}
		
		try{
			FacesScopes facesScopes = new FacesScopes();
			@SuppressWarnings("unchecked")
			T tmpBean = (T)facesScopes.removeFromSession(managedBeanName);
			
			log.info("remove managedBean from session:" + tmpBean);
			
			managedBean = null;
		} catch(Exception ex){
			log.error("Failed to renew managed bean: " + managedBeanName, ex);
		}
		
		//return managedBean;
	}
	
	/**
	 * @return the managedBean
	 */
	public T getManagedBean() {		
		if (managedBean == null && null !=managedBeanName){
			/*FacesScopes facesScopes = new FacesScopes();
			@SuppressWarnings("unchecked")
			T tmpBean = (T)facesScopes.getSessionScope(managedBeanName);
			if(null == tmpBean){
				managedBean = createNewSesionObject();
			}else{
				managedBean = tmpBean;
			}*/

			FacesContext facesContext = FacesContext.getCurrentInstance();
			managedBean = facesContext.getApplication().evaluateExpressionGet(
					facesContext, "#{" + managedBeanName + "}", managedBeanClazz);
			
			if (log.isDebugEnabled()) {
				log.debug("managedBean:" + managedBean);
			}
		}
		
		return managedBean;
		
	}

	/*private String getConcatId(){
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
	}*/
	
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
		if(username!=null && username.equals(facesScopes.getUserScreenName())) {
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
	
	public <ENTITY_CLASS extends BaseEntity> void selectAll(boolean selected, List<ENTITY_CLASS> list) {
		if (list != null) {
			for (ENTITY_CLASS entity : list) {
				entity.setSelected(selected);
			}
		}
	}
	
	public <ENTITY_CLASS extends BaseEntity> void deleteSelectedRowsAction(List<ENTITY_CLASS> list) {
		if (log.isDebugEnabled()) {
			log.debug("deleteSelectedRowsAction");
		}
		
		if (list != null) {
			List<ENTITY_CLASS> toBeRemovedList = new ArrayList<ENTITY_CLASS>();
			
			for (ENTITY_CLASS entity : list) {
				if (entity.isSelected()) {
					toBeRemovedList.add(entity);
				}
			}
			
			deleteRowsAction(list, toBeRemovedList);
		}
	}
	
	public <ENTITY_CLASS> void deleteRowAction(List<ENTITY_CLASS> list, ENTITY_CLASS toBeRemoved) {
		if (log.isDebugEnabled()) {
			log.debug("deleteRowAction");
		}
		
		if (list != null && toBeRemoved != null) {
			list.remove(toBeRemoved);	
		}
	}
	
	public <ENTITY_CLASS> void deleteRowsAction(List<ENTITY_CLASS> list, List<ENTITY_CLASS> toBeRemovedList) {
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
	protected <RETURN_CLASS> ISearchBean<RETURN_CLASS> createSearchBean(Class<RETURN_CLASS> classz, String componentId) {
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
		FacesScopes face = new FacesScopes();
		try {
			List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance()
					.getParameterDescListBySearchBean(parameterType, searchBean);
			
		    items = new SelectItem[parameterDescs.size()+1];
		    items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
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
		FacesScopes face = new FacesScopes();
		
		try {
			List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance()
					.getParameterDescListBySearchBean(parameterType, searchBean);
			
		    items = new SelectItem[parameterDescs.size()+1];
		    
		    items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
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
		if (parameter != null && StringUtils.isNotBlank(parameter.getParameterType())) {
			return ParameterHelper.getInstance().getParameterDesc(
					parameter.getParameterType(),
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
		return ParameterHelper.getInstance().getParameterDesc(
				parameterType, parameterCode);
	}
	
	/**
	 * Get parameter by parameter type and code.
	 * 
	 * @param parameterType
	 * 		the parameter type
	 * @param parameterCode
	 * 		the parameter code
	 * @return the PmParameter instance
	 */
	public PmParameter getParameterByTypeAndCode(String parameterType, String parameterCode) {
		return ParameterHelper.getInstance().getParameterByTypeAndCode(parameterType, parameterCode);
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
		FacesScopes face = new FacesScopes();
		
		try {
			List<PmStatusDesc> statusDescs = StatusHelper.getInstance()
					.getStatusDescList(moduleCode, docType);
			
		    items = new SelectItem[statusDescs.size()+1];
		    items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
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
		return getRadioButtonCodeValuesBySearchBean(parameterType, null);
	}
	
	public SelectItem[] getRadioButtonCodeValuesBySearchBean(String parameterType, ISearchBean<PmParameterDesc> searchBean) {
		SelectItem[] items = null;
		try {
			List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance()
					.getParameterDescListBySearchBean(parameterType, searchBean);
			
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
	
	public SelectItem[] getRadioButtonValues(String parameterType) {
		SelectItem[] items = null;
		try {
			List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance()
					.getParameterDescList(parameterType);
			
		    items = new SelectItem[parameterDescs.size()];
		    int i = 0;
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
	
	public void commonUploadFile(FileUploadEvent event, String path){

//		String tempDir = "C://Ngep//doc//"+moduleName+"//";
		String tempDir = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);
		tempDir += "/" + path; 
		
		String uploadDir = "";
		
		UploadedFile file = event.getFile();
		
		uploadDir = tempDir + file.getFileName();
		
		genFile(uploadDir, file.getContents());

	}
	
	public StreamedContent commonDownload(String path, String displayFileName) throws IOException {
		String filePath = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);
		filePath += "/" + path;
		
		PortletResponse resp = (PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse response = PortalUtil.getHttpServletResponse(resp);
		
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
//		response.setContentType("application/vnd.ms-excel");
//		response.setHeader("Content-Transfer-Encoding", "binary");
		response.setHeader("Content-Disposition", "attachment; filename="+displayFileName);
		response.setContentType("application/octet-stream");
		response.flushBuffer();
		
		FileInputStream docInputStream = new FileInputStream(new File(filePath));
		
		OutputStream out = response.getOutputStream();
		byte[] buffer = new byte[4096];
		int bytesRead;
		while ((bytesRead = docInputStream.read(buffer)) != -1) {
			out.write(buffer, 0, bytesRead);
			buffer = new byte[4096];
		}
		
		return null;
	}
	
	/**
	 * @param messageCode
	 * @param docType
	 * @param docTypeCode
	 * @param docNo
	 */
	public void successMessage(String messageCode, String docType, String docTypeCode, String docNo){
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
				FacesMessage.SEVERITY_INFO, null, 
				MessageUtils.getMessage(messageCode, 
						new String[]{getParameterDesc(docType, docTypeCode), docNo}, 
						MessageUtils.BUNDLE_COMMON_MESSAGES,new FacesScopes().getUserLocale())));
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);	
	}

	public void genFile(String uploadDir, byte[] content){

		File dir = new File(uploadDir);
		log.info(" dir.exists()"+ dir.exists());		  
		if(!dir.exists()) {
			dir.mkdirs();
		}
		
		try {	
			
			BufferedOutputStream bufferedOutputStream =new BufferedOutputStream(new FileOutputStream(uploadDir));

			bufferedOutputStream.write(content);

			if (bufferedOutputStream != null) {
				bufferedOutputStream.flush();
				bufferedOutputStream.close();
			}



		}catch(Exception e) {
			log.error(e);
		}


	}
	
	public String getFullAddress(SmAddress smAddress) {
		return AddressUtils.getFullAddress(smAddress);
	}
	
	public String getFullAddress(PmAddress pmAddress) {
		return AddressUtils.getFullAddress(pmAddress);
	}
	
	public void handleException(Exception exception) {
		ExceptionHandler expHandler = FacesContext.getCurrentInstance().getExceptionHandler();
		if (expHandler instanceof EpExceptionHandler) {
			((EpExceptionHandler)expHandler).handleException(exception);
		}
	}
	
}