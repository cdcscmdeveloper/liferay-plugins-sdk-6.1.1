/**
 * 
 */
package my.ep.web.backingbean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.BpmConstants;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.role.RoleReqDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmReason;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.bpm.tasklist.BPMTaskListBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.exceptionhandler.EpExceptionHandler;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.AddressUtils;
import my.ep.web.util.EpPortletUtil;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.SecurityFilterUtil;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.ReasonHelper;
import my.ep.web.util.pm.helper.StatusHelper;
import my.ep.web.util.sm.helper.SmServiceLookup;

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
	public BaseBackingBean() {
		if (log.isDebugEnabled()) {
			log.debug("In BaseBackingBean constructor");

		}
		Class<?> clazz = this.getClass();
		Type type = clazz.getGenericSuperclass();
		// while (true) {
		if (log.isDebugEnabled()) {
			log.debug("#######Clazz = " + clazz);
			log.debug("#######type = " + type);
		}

		/*
		 * if (clazz == null) { break; }
		 */
		if (type != null && type instanceof ParameterizedType) {
			this.managedBeanClazz = (Class<T>) ((ParameterizedType) type)
					.getActualTypeArguments()[0];
			if (null != managedBeanClazz) {
				ManagedBean managedBeanAnnotation = managedBeanClazz
						.getAnnotation(ManagedBean.class);
				if (null != managedBeanAnnotation) {
					managedBeanName = managedBeanAnnotation.name()/*
																 * +
																 * getConcatId()
																 */;
				}

				if (StringUtils.isBlank(managedBeanName)) {
					managedBeanName = StringUtils.uncapitalize(managedBeanClazz
							.getSimpleName());
				}
			}
		}
		// }

		if (log.isDebugEnabled()) {
			log.debug("managedBeanName:" + managedBeanName);
		}
		
//		new FacesScopes().extendLiferaySession();
	}

	/**
	 * @return the managedBeanClazz
	 */
	public Class<T> getManagedBeanClazz() {
		return managedBeanClazz;
	}

	/**
	 * @param managedBeanClazz
	 *            the managedBeanClazz to set
	 */
	public void setManagedBeanClazz(Class<T> managedBeanClazz) {
		this.managedBeanClazz = managedBeanClazz;
	}

	public void renewSesionObject() {
		if (log.isDebugEnabled()) {
			log.debug("In renewSesionObject");
		}

		try {
			FacesScopes facesScopes = new FacesScopes();
			@SuppressWarnings("unchecked")
			T tmpBean = (T) facesScopes.removeFromSession(managedBeanName);

			log.info("remove managedBean from session:" + tmpBean);

			managedBean = null;
		} catch (Exception ex) {
			log.error("Failed to renew managed bean: " + managedBeanName, ex);
		}

		// return managedBean;
	}

	/**
	 * @return the managedBean
	 */
	public T getManagedBean() {
		if (managedBean == null && null != managedBeanName) {
			/*
			 * FacesScopes facesScopes = new FacesScopes();
			 * 
			 * @SuppressWarnings("unchecked") T tmpBean =
			 * (T)facesScopes.getSessionScope(managedBeanName); if(null ==
			 * tmpBean){ managedBean = createNewSesionObject(); }else{
			 * managedBean = tmpBean; }
			 */

			FacesContext facesContext = FacesContext.getCurrentInstance();
			managedBean = facesContext.getApplication().evaluateExpressionGet(
					facesContext, "#{" + managedBeanName + "}",
					managedBeanClazz);

			if (log.isDebugEnabled()) {
				log.debug("managedBean:" + managedBean);
			}
		}

		return managedBean;

	}

	/*
	 * private String getConcatId(){ String concatId = "";
	 * 
	 * 
	 * FacesScopes facesScope = new FacesScopes();
	 * 
	 * String portletId =
	 * (String)facesScope.getRequestScope(WebKeys.PORTLET_ID);
	 * 
	 * String pageId = null; ThemeDisplay themeDisplay =
	 * (ThemeDisplay)facesScope.getRequestScope(WebKeys.THEME_DISPLAY); if
	 * (themeDisplay!=null){ pageId = String.valueOf(themeDisplay.getPlid());
	 * facesScope.setSessionScope("title",
	 * themeDisplay.getPortletDisplay().getTitle()); }
	 * 
	 * if (StringUtils.isNotBlank(portletId) && StringUtils.isNotBlank(pageId)){
	 * concatId = "_" + portletId + "_" + pageId; }
	 * 
	 * return concatId; }
	 */

	/* Utility method */
	public String userImagePath(String userId) {
		FacesScopes facesScopes = new FacesScopes();
		String imagePath = "";
		if (userId != null) {
			User user = null;
			ThemeDisplay themeDisplay = (ThemeDisplay) facesScopes.getRequest()
					.getAttribute(WebKeys.THEME_DISPLAY);
			try {
				user = UserLocalServiceUtil.getUserByScreenName(
						themeDisplay.getCompanyId(), userId);
				if (user != null) {
					imagePath = "/.."
							+ user.getPortraitURL((ThemeDisplay) facesScopes
									.getRequest().getAttribute(
											WebKeys.THEME_DISPLAY));
				}
			} catch (Exception ex) {
				log.error(ex.getMessage(), ex);
			}
		}

		return imagePath;

	}

	public boolean compareUserName(String username) {
		FacesScopes facesScopes = new FacesScopes();
		if (username != null
				&& username.equals(facesScopes.getUserScreenName())) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * protected <RETURN_CLASS extends BaseEntity> List<RETURN_CLASS>
	 * toEntityBeans(List<? extends ValueBean<RETURN_CLASS>> valueBeans) {
	 * List<RETURN_CLASS> list = new ArrayList<RETURN_CLASS>();
	 * 
	 * for (ValueBean<RETURN_CLASS> valueBean : valueBeans) {
	 * list.add(valueBean.getEntity()); }
	 * 
	 * return list; }
	 * 
	 * public <ENTITY_CLASS extends BaseEntity> void
	 * addListBySelectedList(List<ENTITY_CLASS> list, List<ENTITY_CLASS>
	 * selectedList) { for (ENTITY_CLASS entity : selectedList) { if
	 * (entity.isSelected()) { entity.setSelected(false);
	 * 
	 * list.add(entity); } } }
	 * 
	 * public <ENTITY_CLASS extends BaseEntity> void
	 * deleteRowsAction(DataValueBean<ENTITY_CLASS> dataValueBean) {
	 * List<ENTITY_CLASS> displayedList = dataValueBean.getDisplayedList();
	 * List<ENTITY_CLASS> removedList = dataValueBean.getRemovedList();
	 * List<ENTITY_CLASS> listToBeRemoved = new ArrayList<ENTITY_CLASS>();
	 * 
	 * for (ENTITY_CLASS entity : displayedList) { if (entity.isSelected()) {
	 * entity.setDeleted(true);
	 * 
	 * listToBeRemoved.add(entity); } }
	 * 
	 * // remove from display list. displayedList.removeAll(listToBeRemoved);
	 * 
	 * // added into removed list. removedList.addAll(listToBeRemoved); }
	 * 
	 * public <ENTITY_CLASS extends BaseEntity> void selectAllAction(boolean
	 * selectAll, List<ENTITY_CLASS> list) { for (ENTITY_CLASS valueBean : list)
	 * { valueBean.setSelected(selectAll); } }
	 */

	public <ENTITY_CLASS extends BaseEntity> void selectAll(boolean selected,
			List<ENTITY_CLASS> list) {
		if (list != null) {
			for (ENTITY_CLASS entity : list) {
				entity.setSelected(selected);
			}
		}
	}

	public <ENTITY_CLASS extends BaseEntity> void deleteSelectedRowsAction(
			List<ENTITY_CLASS> list) {
		if (log.isDebugEnabled()) {
			log.debug("deleteSelectedRowsAction");
		}
		try {
			if (list != null) {
				List<ENTITY_CLASS> toBeRemovedList = new ArrayList<ENTITY_CLASS>();

				for (ENTITY_CLASS entity : list) {
					if (entity.isSelected()) {
						toBeRemovedList.add(entity);
					}
				}
				if (toBeRemovedList.size() == 0) {
					throw new EpException(MessageConst.ERROR_760146);
				}

				deleteRowsAction(list, toBeRemovedList);
			} else {
				throw new EpException(MessageConst.ERROR_760146);
			}
		} finally {

		}
	}

	public <ENTITY_CLASS> void deleteRowAction(List<ENTITY_CLASS> list,
			ENTITY_CLASS toBeRemoved) {
		if (log.isDebugEnabled()) {
			log.debug("deleteRowAction");
		}

		if (list != null && toBeRemoved != null) {
			list.remove(toBeRemoved);
		}
	}

	public <ENTITY_CLASS> void deleteRowsAction(List<ENTITY_CLASS> list,
			List<ENTITY_CLASS> toBeRemovedList) {
		if (log.isDebugEnabled()) {
			log.debug("deleteRowAction");
		}

		if (list != null && toBeRemovedList != null) {
			list.removeAll(toBeRemovedList);
		}
	}

	public <ENTITY_CLASS extends BaseEntity> void markDeleteSelectedRowsAction(
			List<ENTITY_CLASS> list, List<ENTITY_CLASS> markDeletedList) {
		if (log.isDebugEnabled()) {
			log.debug("markDeleteSelectedRowsAction");
		}
		try {
			if (list != null) {
				if (markDeletedList == null) {
					markDeletedList = new ArrayList<ENTITY_CLASS>();
				}

				for (ENTITY_CLASS entity : list) {
					if (entity.isSelected()) {
						entity.setDeleted(true);
						markDeletedList.add(entity);
					}
				}
				if (markDeletedList.size() == 0) {
					throw new EpException(MessageConst.ERROR_760146);
				}

				deleteRowsAction(list, markDeletedList);
			} else {
				throw new EpException(MessageConst.ERROR_760146);
			}
		} finally {

		}
	}

	/**
	 * Create search bean.
	 * 
	 * @param classz
	 *            the return class type.
	 * @param componentId
	 *            the component id.
	 * @return ISearchBean instance.
	 */
	protected <RETURN_CLASS> ISearchBean<RETURN_CLASS> createSearchBean(
			Class<RETURN_CLASS> classz, String componentId) {
		return new SearchBean<RETURN_CLASS>(
				EpPortletUtil.getLoginUserId(FacesContext.getCurrentInstance()),
				SecurityFilterUtil.getDataFilterScreenMetadatas(componentId));
	}

	/**
	 * Get parameter id select items by parameter type for dropdown.
	 * 
	 * @param parameterType
	 *            the parameter type
	 * @return array of SelectItem
	 */
	public SelectItem[] getDropdownValues(String parameterType) {
		return getDropdownValuesBySearchBean(parameterType, null);
	}

	/**
	 * Get parameter id select items by parameter type and search bean for
	 * dropdown.
	 * 
	 * @param parameterType
	 *            the parameter type
	 * @param searchBean
	 *            the search bean instance
	 * @return array of SelectItem
	 */
	public SelectItem[] getDropdownValuesBySearchBean(String parameterType,
			ISearchBean<PmParameterDesc> searchBean) {
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();
		try {
			List<PmParameterDesc> parameterDescs = ParameterHelper
					.getInstance().getParameterDescListBySearchBean(
							parameterType, searchBean);

			items = new SelectItem[parameterDescs.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale()));
			int i = 1;
			for (PmParameterDesc parameterDesc : parameterDescs) {
				items[i++] = new SelectItem(parameterDesc.getPmParameter()
						.getParameterId(), parameterDesc.getCodeName());
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
	
	/**
	 * Populate values for Reason Drop down base on reason category code
	 * @param categoryCode
	 *            the reason category code
	 *            
	 * @return array of SelectItem
	 */
	public SelectItem[] getReasonCodeDropdownValues(String categoryCode) {
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();
		try {
			List<PmReasonDesc> reasonDescs = ReasonHelper
					.getInstance().getReasonDescList(categoryCode);

			items = new SelectItem[reasonDescs.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale()));
			int i = 1;
			for (PmReasonDesc reasonDesc : reasonDescs) {
				items[i++] = new SelectItem(reasonDesc.getPmReason()
						.getReasonCode(), reasonDesc.getReasonDesc());
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
	
	/**
	 * Get reason by reason category code and reason code
	 * 
	 * @param parameterType
	 *            the parameter type
	 * @param parameterCode
	 *            the parameter code
	 * @return the PmParameter instance
	 */
	public PmReason getReasonByCategoryAndCode(String categoryCode,
			String reasonCode) {
		PmParameter reasonCategory = getParameterByTypeAndCode( 
				ParameterTypeConst.REASON_CATEGORY, categoryCode);
		return ReasonHelper.getInstance().getReasonByCategoryAndCode(
				reasonCategory.getParameterId(), reasonCode);
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
	 * Get parameter code select items by parameter type and search bean for
	 * dropdown.
	 * 
	 * @param parameterType
	 *            the parameter type
	 * @param searchBean
	 *            the search bean instance
	 * @return array of SelectItem
	 */
	public SelectItem[] getDropdownCodeValuesBySearchBean(String parameterType,
			ISearchBean<PmParameterDesc> searchBean) {
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();

		try {
			List<PmParameterDesc> parameterDescs = ParameterHelper
					.getInstance().getParameterDescListBySearchBean(
							parameterType, searchBean);

			items = new SelectItem[parameterDescs.size() + 1];

			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale()));
			int i = 1;
			for (PmParameterDesc parameterDesc : parameterDescs) {
				items[i++] = new SelectItem(parameterDesc.getPmParameter()
						.getParameterCode(), parameterDesc.getCodeName());
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

	/**
	 * Get Parameter description by parameter.
	 * 
	 * @param parameter
	 *            the PmParameter instance.
	 * @return parameter description
	 */
	public String getParameterDesc(PmParameter parameter) {
		if (parameter != null
				&& StringUtils.isNotBlank(parameter.getParameterType())) {
			return ParameterHelper.getInstance().getParameterDesc(
					parameter.getParameterType(), parameter.getParameterCode());
		} else {
			return null;
		}
	}
	
	/**
	 * Get reason description by reason.
	 * 
	 * @param reason
	 *            the PmReason instance.
	 * @return reason description
	 */
	public String getReasonDesc(PmReason pmReason) {
		if (pmReason != null) {
			return ReasonHelper.getInstance().getReasonDescStringById(
					pmReason.getReasonId());
		} else {
			return null;
		}
	}

	/**
	 * Get Parameter description by type and code.
	 * 
	 * @param parameterType
	 *            the parameter type
	 * @param parameterCode
	 *            the parameter code
	 * @return parameter description
	 */
	public String getParameterDesc(String parameterType, String parameterCode) {
		return ParameterHelper.getInstance().getParameterDesc(parameterType,
				parameterCode);
	}

	/**
	 * Get parameter by parameter type and code.
	 * 
	 * @param parameterType
	 *            the parameter type
	 * @param parameterCode
	 *            the parameter code
	 * @return the PmParameter instance
	 */
	public PmParameter getParameterByTypeAndCode(String parameterType,
			String parameterCode) {
		return ParameterHelper.getInstance().getParameterByTypeAndCode(
				parameterType, parameterCode);
	}

	/**
	 * Check if the given procurement category is medical or not
	 * 
	 * @param pmProcCategory
	 *            the pmProcCategory type
	 * @return procurement category is medical or not
	 */
	public boolean isMedicalProcurementCategory(PmParameter pmProcCategory) {
		if ((pmProcCategory != null)
				&& (ParameterConst.PROCUREMENT_CATEGORY_UBATAN_DAN_BAHAN_KIMIA
						.equals(pmProcCategory.getParameterCode()))) {
			return true;
		}
		return false;
	}

	/**
	 * Get status select items by module and doc type for dropdown.
	 * 
	 * @param parameterType
	 *            the parameter type
	 * @return array of SelectItem
	 */
	public SelectItem[] getStatusDropdownValues(String moduleCode,
			String docType) {
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();

		try {
			List<PmStatusDesc> statusDescs = StatusHelper.getInstance()
					.getStatusDescList(moduleCode, docType);

			items = new SelectItem[statusDescs.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale()));
			int i = 1;
			for (PmStatusDesc statusDesc : statusDescs) {
				items[i++] = new SelectItem(statusDesc.getPmStatus()
						.getStatusId(), statusDesc.getStatusName());
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

	/**
	 * Get Status description by status id.
	 * 
	 * @param statusId
	 *            the status id.
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
	 *            the parameter type
	 * @return array of SelectItem
	 */
	public SelectItem[] getRadioButtonCodeValues(String parameterType) {
		return getRadioButtonCodeValuesBySearchBean(parameterType, null);
	}

	public SelectItem[] getRadioButtonCodeValuesBySearchBean(
			String parameterType, ISearchBean<PmParameterDesc> searchBean) {
		SelectItem[] items = null;
		try {
			List<PmParameterDesc> parameterDescs = ParameterHelper
					.getInstance().getParameterDescListBySearchBean(
							parameterType, searchBean);

			items = new SelectItem[parameterDescs.size()];
			int i = 0;
			for (PmParameterDesc parameterDesc : parameterDescs) {
				items[i++] = new SelectItem(parameterDesc.getPmParameter()
						.getParameterCode(), parameterDesc.getCodeName());
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

	public SelectItem[] getRadioButtonValues(String parameterType) {
		SelectItem[] items = null;
		try {
			List<PmParameterDesc> parameterDescs = ParameterHelper
					.getInstance().getParameterDescList(parameterType);

			items = new SelectItem[parameterDescs.size()];
			int i = 0;
			for (PmParameterDesc parameterDesc : parameterDescs) {
				items[i++] = new SelectItem(parameterDesc.getPmParameter()
						.getParameterId(), parameterDesc.getCodeName());
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

	public void commonUploadFile(FileUploadEvent event, String path) {

		// String tempDir = "C://Ngep//doc//"+moduleName+"//";
		String tempDir = EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);
		tempDir += "/" + path;

		//String uploadDir = "";

		UploadedFile file = event.getFile();

		// uploadDir = tempDir + StringUtils.replace(" ", file.getFileName(),
		// "-");

		// genFile(uploadDir, file.getContents());
		genFile(tempDir, file);

	}

	public StreamedContent commonDownload(String path, String displayFileName)
			throws IOException {
		String filePath = EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);
		filePath += "/" + path;

		PortletResponse resp = (PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse response = PortalUtil.getHttpServletResponse(resp);

		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		// response.setContentType("application/vnd.ms-excel");
		// response.setHeader("Content-Transfer-Encoding", "binary");
		response.setHeader("Content-Disposition", "attachment; filename="
				+ displayFileName);
		response.setContentType("application/octet-stream");
		response.flushBuffer();

		InputStream docInputStream = null;
		try {
			URLConnection uc = new URL(filePath).openConnection();
			docInputStream = new BufferedInputStream(uc.getInputStream());
		} catch (Exception e) {
			// Cannot open connection, maybe local file
			docInputStream = new FileInputStream(filePath);
		}

		OutputStream out = response.getOutputStream();
		byte[] buffer = new byte[4096];
		int bytesRead;
		while ((bytesRead = docInputStream.read(buffer)) != -1) {
			out.write(buffer, 0, bytesRead);
			buffer = new byte[4096];
		}
		out.close();
		docInputStream.close();
		return null;
	}

	/**
	 * Success message.
	 *
	 * @param messageCode the message code
	 * @param docType the doc type
	 * @param docTypeCode the doc type code
	 * @param docNo the doc no
	 * @param isAcknowledgeRequired the is acknowledge required
	 */
	public void successMessage(String messageCode, String docType,
			String docTypeCode, String docNo, boolean isAcknowledgeRequired) {
		FacesContext.getCurrentInstance().addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
						.getMessage(
								messageCode,
								new String[] {
										getParameterDesc(docType, docTypeCode),
										docNo },
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
		
		if(isAcknowledgeRequired){
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
			.handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);
		}
	}
	public void genFile(String uploadDir, UploadedFile file) {

		File dir = new File(uploadDir);
		log.info(" dir.exists()" + dir.exists());
		if (!dir.exists()) {
			log.info("before make directory");
			dir.mkdirs();
			log.info("after make directory");
		}

		try {
			String finalFileName = StringUtils.replace(file.getFileName(), " ",
					"-");
			log.info("finalFileName: " + finalFileName);

			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
					new FileOutputStream(uploadDir + "/" + finalFileName));

			bufferedOutputStream.write(file.getContents());

			if (bufferedOutputStream != null) {
				bufferedOutputStream.flush();
				bufferedOutputStream.close();
			}

		} catch (Exception e) {
			log.error(e);
		}

	}

	protected void generateReport(String reportFile,
			Map<String, String> paramMap) throws IOException {
		String reportUrl = generateReportURL(reportFile, paramMap);

		HttpURLConnection urlConnection = null;
		URL obj = new URL(reportUrl);
		urlConnection = (HttpURLConnection) obj.openConnection();

		BufferedInputStream buf = new BufferedInputStream(
				urlConnection.getInputStream());

		PortletResponse portletResponse = (PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse res = PortalUtil
				.getHttpServletResponse(portletResponse);
		int position = reportFile.lastIndexOf(".jsp");
		if (position != -1) {
			reportFile = reportFile.substring(0, position);
		}

		res.setHeader("Content-Disposition",
				"attachment; filename=\"" + reportFile + "_"
						+ ReportConst.REPORT_DATE_TIME.format(new Date())
						+ ".pdf\"");//
		res.setHeader("Content-Transfer-Encoding", "binary");
		res.setContentType("application/pdf");
		res.flushBuffer();

		OutputStream out = res.getOutputStream();

		byte[] b = new byte[128];
		int size;
		while ((size = buf.read(b, 0, b.length)) != -1) {
			out.write(b, 0, size);
		}
		buf.close();
		out.close();
	}

	protected String generateReportURL(String reportFile,
			Map<String, String> paramMap) {
		EpPropertiesUtils propertiesUtils = EpPropertiesUtils.getInstance();
		String excPrevUrl = propertiesUtils
				.getProperty(EpPropertiesConst.EXC_PREV_URL);
		String excNextUrl = propertiesUtils
				.getProperty(EpPropertiesConst.EXC_NEXT_URL);

		StringBuilder reportUrl = new StringBuilder(excPrevUrl);
		reportUrl.append(ReportConst.PARAM_SEPARATOR);
		reportUrl.append(ReportConst.PARAM_KEY_REPORT);
		reportUrl.append(ReportConst.VALUE_SEPARATOR);
		reportUrl.append(reportFile);

		for (Entry<String, String> params : paramMap.entrySet()) {
			reportUrl.append(ReportConst.PARAM_SEPARATOR);
			reportUrl.append(params.getKey());
			reportUrl.append(ReportConst.VALUE_SEPARATOR);
			reportUrl.append(params.getValue());
		}
		reportUrl.append(excNextUrl);

		if (log.isDebugEnabled()) {
			log.debug("reportUrl: " + reportUrl);
		}
		return reportUrl.toString();
	}

	public String getFullAddress(SmAddress smAddress) {
		return AddressUtils.getFullAddress(smAddress);
	}

	public String getSMFullAddress(SmAddress smAddress) {
		return AddressUtils.getSmFullFormattedAddress(smAddress);
	}

	public String getFullAddress(PmAddress pmAddress) {
		return AddressUtils.getFullAddress(pmAddress);
	}

	public void handleException(Exception exception) {
		ExceptionHandler expHandler = FacesContext.getCurrentInstance()
				.getExceptionHandler();
		if (expHandler instanceof EpExceptionHandler) {
			((EpExceptionHandler) expHandler).handleException(exception);
		}
	}

	public String escapeXhtml(String input) {
		String output = "";
		if (input != null) {
			output = StringUtils.replace(input, "&", "&#38;");
		}
		return output;
	}

	public String getShortFileName(String filename) {
		if (filename != null && filename.length() > 15) {
			filename = filename.substring(0, 15);
			filename += "...";
		}
		return filename;
	}
	
	public String retrieveJabatanByOrgProfileId(Long orgProfileId){
		IOrgProfileSO profileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(orgProfileId);
		OrgProfileResDTO resDTO = new OrgProfileResDTO();
		resDTO = profileSO.retrieveJabatanByOrgProfileId(reqDTO);
		String jabatan = "";
		if(resDTO.getPmOrgProfile() != null){
			jabatan = resDTO.getPmOrgProfile().getOrgName();
		}
		return jabatan;
	}
	
	public String retrievePegawaiPengawalByOrgProfileId(Long orgProfileId){
		IOrgProfileSO profileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(orgProfileId);
		OrgProfileResDTO resDTO = new OrgProfileResDTO();
		resDTO = profileSO.retrievePegawaiPengawalByOrgProfileId(reqDTO);
		String pagawaiPengawal = "";
		if(resDTO.getPmOrgProfile() != null){
			pagawaiPengawal = resDTO.getPmOrgProfile().getOrgName();
		}
		return pagawaiPengawal;
	}
	
	public String retrieveMinistryCodeByOrgProfileId(Long orgProfileId){
		IOrgProfileSO profileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(orgProfileId);
		return profileSO.retrieveMinistryByOrgProfileId(reqDTO).getPmOrgProfile().getOrgCode();
	}
	
	public String retrieveMinistryByOrgProfileId(Long orgProfileId){
		IOrgProfileSO profileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(orgProfileId);
		PmOrgProfile orgProfile = profileSO.retrieveMinistryByOrgProfileId(reqDTO).getPmOrgProfile();
		if(orgProfile!=null) {
			return orgProfile.getOrgName();
		} else {
			log.info("Ministry not found : "+orgProfileId);
			return "";
		}
	}
	
	public PmOrgProfile retrieveMinistryOrgProfile(Long orgProfileId){
		IOrgProfileSO profileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(orgProfileId);
		return profileSO.retrieveMinistryByOrgProfileId(reqDTO).getPmOrgProfile();
	}
	
	public PmOrgProfile retrieveJabatanOrgProfile(Long orgProfileId){
		IOrgProfileSO profileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(orgProfileId);
		PmOrgProfile pmOrgProfile = profileSO.retrieveJabatanByOrgProfileId(reqDTO).getPmOrgProfile();
		if(pmOrgProfile==null) {
			pmOrgProfile = new PmOrgProfile();
			pmOrgProfile.setOrgProfileId(-1L);
		}
		return pmOrgProfile;
	}
	
	public PmOrgProfile retrievePegawaiPegawalOrgProfile(Long orgProfileId){
		IOrgProfileSO profileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(orgProfileId);
		return profileSO.retrievePegawaiPengawalByOrgProfileId(reqDTO).getPmOrgProfile();
	}
	
	/**
	 * Get eP Role description by role code
	 * 
	 * @param String
	 *            roleCode
	 * @return String
	 */
	public String getEpRoleDescByRoleCode(String roleCode) {
		String retVal = "";
		RoleReqDTO reqDTO = new RoleReqDTO();
		reqDTO.setRoleCode(roleCode);
		reqDTO.setLanguage(new FacesScopes().getUserLocale().getLanguage());
		PmRoleDesc roleDesc = SmServiceLookup.getRoleSO()
				.retrieveRoleDescByRoleCode(reqDTO).getPmRoleDesc();
		if (roleDesc != null && StringUtils.isNotBlank(roleDesc.getRoleName())) {
			retVal = roleDesc.getRoleName();
		}
		return retVal;
	}
	
	/**
	 * Download file.
	 */
	public void downloadFile(String filePath) throws IOException {
		File file = new File(filePath);
		log.info("download file>>>>");
		OutputStream out = null;
		try{
			PortletResponse  portletResponse = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();  
			HttpServletResponse res = com.liferay.portal.util.PortalUtil.getHttpServletResponse(portletResponse);
			res.setHeader( "Content-Disposition", "attachment; filename=\"" + file.getName() + "\"" );//
			res.setHeader( "Content-Transfer-Encoding", "binary" );
			res.setContentType("application/octet-stream");
			res.flushBuffer();
			out = res.getOutputStream();   
			if(file.getPath()!=null) {
				log.info("file path not null>>>>");
				InputStream input = null;
				try {
					URLConnection uc = new URL(file.getPath()).openConnection();
					input = new BufferedInputStream(uc.getInputStream()); 
				} catch (Exception e) {
					log.info("It's Local file>>>>");
					// Cannot open connection, maybe local file
					input = new FileInputStream(file.getPath());
				}
				byte[] buffer = new byte[4096];
				int bytesRead;
				while ((bytesRead = input.read( buffer )) != -1){ 
					out.write( buffer, 0, bytesRead ); buffer = new byte[4096]; 
				}
				input.close();			
			} else {
				//out.write(attachment.getFileByte());
			}

			FacesContext.getCurrentInstance().responseComplete();
		}catch(Exception ex){
			log.error("download exception>>>>" + ex.getMessage());
		} finally {
			out.flush();
			out.close(); 
		}
	}
	


	/**
	 * To get BpmTask Details.
	 * 
	 * @param clazz
	 * @return Object payLoadInstance
	 */
	@SuppressWarnings("unchecked")
	protected <BPM_DATA_TYPE> BPM_DATA_TYPE getBpmTaskDetails(
			Class<BPM_DATA_TYPE> clazz, String[] additionalButtons) {
		FacesScopes facesScopes = new FacesScopes();
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(facesScopes.getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);

		String taskId = originalServletRequest.getParameter("taskId");

		IBpmSO bpmSO;

		try {
			bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
					.getServiceObject(IBpmSO.class);

			BpmRequestDTO reqDTO = new BpmRequestDTO();
			reqDTO.setCtxToken(facesScopes.getBPMAuthenticationCtxToken());
			reqDTO.setTaskId(taskId);

			BpmResponseDTO resDTO = bpmSO.getTaskDetails(reqDTO);

			this.renewSesionObject();
			
			boolean isAssignedState = BpmConstants.STATE_ASSIGNED.equals(resDTO.getStatus());
			Object payLoad = resDTO.getPojo();
			 if(null!=resDTO.isGroupTask() && resDTO.isGroupTask()==true){
		            System.out.println("====groupTask not null==="+resDTO.isGroupTask());
					BPMTaskListBackingBean bb = new BPMTaskListBackingBean();
					String claim = originalServletRequest.getParameter("claim");
					System.out.println("claim:" + claim);
					Boolean b = Boolean.valueOf(claim);
					if(null!=b && b==false){
						this.getManagedBean().setReadOnly(true);
					}else{
						this.getManagedBean().setReadOnly(false);
					}
					bb.getManagedBean().setClaim(b);
					bb.getManagedBean().setTaskId(taskId);
					bb.getManagedBean().setGroupTask(true);
		            }else{
		            	System.out.println("====groupTask null==="+resDTO.isGroupTask());
		            	this.getManagedBean().setReadOnly(false);
		            	this.getManagedBean().setGroupTask(false);
		            }
			
			this.getManagedBean().setTaskId(taskId);
			this.getManagedBean().setAssignedState(isAssignedState);
			if (isAssignedState) {
				String[] outComes = null;
				if (additionalButtons != null && additionalButtons.length > 0) {
					int i = 0;
					outComes = new String[additionalButtons.length + resDTO.getAction().length];
					
					for (String additionalButton : additionalButtons) {
						outComes[i++] = additionalButton;
					}
				
					for (String action : resDTO.getAction()) {
						outComes[i++] = action;
					}
				} else {
					outComes = resDTO.getAction();
				}				
				this.getManagedBean().setOutComes(outComes);	
			}
			this.getManagedBean().setCurrentStatusId(
					Long.valueOf(resDTO.getDocStatus()));
			this.getManagedBean().setTaskName(resDTO.getTaskName());
			this.getManagedBean().setPayLoad(payLoad);
			this.getManagedBean().setProcessName(resDTO.getProcessName());

			return (BPM_DATA_TYPE) payLoad;

		} finally {
			bpmSO = null;
		}
	}
}