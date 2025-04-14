/**
 * 
 */
package my.ep.web.backingbean;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import javax.imageio.ImageIO;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;//#31849  #32682
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.language.ILanguageSO;
import my.ep.biz.service.pm.common.uom.IUomSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTEvaluationSO;
import my.ep.biz.service.sm.businessnature.ISupplierBusinessNatureSO;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.biz.service.sm.vendorcode.IVendorCodeSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.BpmConstants;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.role.RoleReqDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;//#31849  #32682
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.language.LanguageReqDTO;
import my.ep.dto.pm.common.language.LanguageResDTO;
import my.ep.dto.pm.common.uom.UomReqDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.sc.quationtender.EvaluationReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sm.blacklistDirector.BlacklistDirectorReqDTO;
import my.ep.dto.sm.blacklistDirector.BlacklistDirectorResDTO;
import my.ep.dto.sm.businessnature.SupplierBusinessNatureReqDTO;
import my.ep.dto.sm.businessnature.SupplierBusinessNatureResDTO;
import my.ep.dto.sm.common.ssm.SsmReqDTO;
import my.ep.dto.sm.common.ssm.SsmResDTO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.ct.CtAddress;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmReason;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.persistence.entity.pp.procurementplan.PpAddress;
import my.ep.persistence.entity.sc.common.ScAddress;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmEquityCompany;
import my.ep.persistence.entity.sm.SmMachineDtl;
import my.ep.persistence.entity.sm.SmPersonBlacklist;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSapVendorCode;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.sm.SmSupplierBusiness;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.persistence.logging.LogKey;//#31849  #32682
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.ReasonCategory;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.LoggerUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.bpm.tasklist.BPMTaskListBackingBean;
import my.ep.web.backingbean.sm.applicationView.ViewSuppProfileBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.sc.SLALogConstant;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.exceptionhandler.EpExceptionHandler;
import my.ep.web.format.AttachmentDateFormat;
import my.ep.web.format.AttachmentFormat;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.UserTasks;
import my.ep.web.managedbean.sm.postsupplierregistration.PostSupplierRegistrationManagedBean;
import my.ep.web.util.AddressUtils;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.EpPortletUtil;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.SecurityFilterUtil;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.ReasonHelper;
import my.ep.web.util.pm.helper.StatusHelper;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.util.sm.helper.VmUtil;
import my.ep.web.util.template.DocumentGenerator;
import my.ep.web.vo.BlacklistOwnerDirectorVO;
import my.ep.web.vo.DisciplinaryActionVO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
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
public abstract class BaseBackingBean<T extends BaseManagedBean> implements Serializable {
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(BaseBackingBean.class);
	private static final int MAX_LENGTH = 53; //#29418

	private Class<T> managedBeanClazz;
	private String managedBeanName;
	private transient T managedBean;
	private static final String UTF = "UTF-8";
	//27346 - start
	String fileInitName;
	//private String tmpPath = null;
	//private String tmpFolder = "SsmInfoTemplate" + "/";
	//private StreamedContent file;
	//27346 -  end
	
	@SuppressWarnings("unchecked")
	public BaseBackingBean() {
		if (log.isDebugEnabled()) {
			log.debug("In BaseBackingBean constructor");
		}
		
		Class<?> clazz = this.getClass();
		Type type = clazz.getGenericSuperclass();

		if (log.isDebugEnabled()) {
			log.debug("#######Clazz = " + clazz);
			log.debug("#######type = " + type);
		}

		if (type != null && type instanceof ParameterizedType) {
			this.managedBeanClazz = (Class<T>) ((ParameterizedType) type).getActualTypeArguments()[0];
			if (null != managedBeanClazz) {
				ManagedBean managedBeanAnnotation = managedBeanClazz.getAnnotation(ManagedBean.class);
				if (null != managedBeanAnnotation) {
					managedBeanName = managedBeanAnnotation.name();
				}

				if (StringUtils.isBlank(managedBeanName)) {
					managedBeanName = StringUtils.uncapitalize(managedBeanClazz.getSimpleName());
				}
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("managedBeanName:" + managedBeanName);
		}

		// new FacesScopes().extendLiferaySession();
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
			managedBean = facesContext.getApplication().evaluateExpressionGet(facesContext, "#{" + managedBeanName + "}", managedBeanClazz);

			log.info("managedBean:" + managedBean);
		}
//		
//		//20210721@fadhli - #31062 Added user activity logging similar to the one added in FacesScopes - Start
//		UserActivityUtil.getInstance().logUserActivity(this.getClass().getName() + "#getManagedBean()");
//		//20210721@fadhli - #31062 Added user activity logging similar to the one added in FacesScopes - End
//		
		return managedBean;

	}

	/**
	 * User image path.
	 *
	 * @param userId the user id
	 * @return the string
	 */
	public String userImagePath(String userId) {
		FacesScopes facesScopes = new FacesScopes();
		String imagePath = "";
		if (userId != null) {
			User user = null;
			ThemeDisplay themeDisplay = (ThemeDisplay) facesScopes.getRequest().getAttribute(WebKeys.THEME_DISPLAY);
			try {
				user = UserLocalServiceUtil.getUserByScreenName(themeDisplay.getCompanyId(), userId);
				if (user != null) {
					imagePath = "/.." + user.getPortraitURL((ThemeDisplay) facesScopes.getRequest().getAttribute(WebKeys.THEME_DISPLAY));
				}
			} catch (Exception ex) {
				log.error(ex.getMessage(), ex);
			}
		}

		return imagePath;

	}

	/**
	 * Compare user name.
	 *
	 * @param username the username
	 * @return true, if successful
	 */
	public boolean compareUserName(String username) {
		FacesScopes facesScopes = new FacesScopes();
		/*
		if (username != null && username.equals(facesScopes.getUserScreenName())) {
			return true;
		} else {
			return false;
		}
		*/
		return (username != null && username.equals(facesScopes.getUserScreenName()));
	}

	/**
	 * Claim bpm task.
	 */
	public void claimBpmTask() {

		String taskId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(ContractConst.TASK_ID);

		claimTaskByTaskId(taskId);

	}
	
	/**
	 * Claim bpm task group
	 */
	public void claimBpmTaskGroup(String taskId) {

		log.info("Group**********taskId=" + taskId);

		claimTaskByTaskId(taskId);

	}

	/**
	 * Release bpm task.
	 */
	public void releaseBpmTask() {
		String taskId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(ContractConst.TASK_ID);
		releaseTaskByTaskId(taskId);
	}

	/**
	 * Claim BPM task by taskId
	 * 
	 * @param String
	 *            taskId
	 */
	public void claimTaskByTaskId(String taskId) {
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
		FacesScopes face = new FacesScopes();
		Object token = face.getBPMAuthenticationCtxToken();

		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setTaskId(taskId);
		reqDTO.setCtxToken(token);
		bpmSO.claimTask(reqDTO);
	}

	/**
	 * Release BPM task by taskId
	 * 
	 * @param String
	 *            taskId
	 */
	public void releaseTaskByTaskId(String taskId) {
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
		FacesScopes face = new FacesScopes();
		Object token = face.getBPMAuthenticationCtxToken();

		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setTaskId(taskId);
		reqDTO.setCtxToken(token);
		bpmSO.releaseTask(reqDTO);
	}
	
	/**
	 * Select all.
	 *
	 * @param <ENTITY_CLASS> the generic type
	 * @param selected the selected
	 * @param list the list
	 */
	public <ENTITY_CLASS extends BaseEntity> void selectAll(boolean selected, List<ENTITY_CLASS> list) {
		if (list != null) {
			for (ENTITY_CLASS entity : list) {
				entity.setSelected(selected);
			}
		}
	}

	/**
	 * Delete selected rows action.
	 *
	 * @param <ENTITY_CLASS> the generic type
	 * @param list the list
	 */
	public <ENTITY_CLASS extends BaseEntity> void deleteSelectedRowsAction(List<ENTITY_CLASS> list) {
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

	/**
	 * Delete row action.
	 *
	 * @param <ENTITY_CLASS> the generic type
	 * @param list the list
	 * @param toBeRemoved the to be removed
	 */
	public <ENTITY_CLASS> void deleteRowAction(List<ENTITY_CLASS> list, ENTITY_CLASS toBeRemoved) {
		if (log.isDebugEnabled()) {
			log.debug("deleteRowAction");
		}

		if (list != null && toBeRemoved != null) {
			list.remove(toBeRemoved);
		}
	}

	/**
	 * Delete rows action.
	 *
	 * @param <ENTITY_CLASS> the generic type
	 * @param list the list
	 * @param toBeRemovedList the to be removed list
	 */
	public <ENTITY_CLASS> void deleteRowsAction(List<ENTITY_CLASS> list, List<ENTITY_CLASS> toBeRemovedList) {
		if (log.isDebugEnabled()) {
			log.debug("deleteRowAction");
		}

		if (list != null && toBeRemovedList != null) {
			list.removeAll(toBeRemovedList);
		}
	}

	/**
	 * Mark delete selected rows action.
	 *
	 * @param <ENTITY_CLASS> the generic type
	 * @param list the list
	 * @param markDeletedList the mark deleted list
	 */
	public <ENTITY_CLASS extends BaseEntity> void markDeleteSelectedRowsAction(List<ENTITY_CLASS> list, List<ENTITY_CLASS> markDeletedList) {
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
	protected <RETURN_CLASS> ISearchBean<RETURN_CLASS> createSearchBean(Class<RETURN_CLASS> classz, String componentId) {
		return new SearchBean<RETURN_CLASS>(EpPortletUtil.getLoginUserId(FacesContext.getCurrentInstance()), SecurityFilterUtil.getDataFilterScreenMetadatas(componentId));
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
	public SelectItem[] getDropdownObjectValuesBySearchBean(String parameterType, ISearchBean<Object[]> searchBean) {
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();
		try {
			List<Object[]> objects = ParameterHelper.getInstance().getParameterDescListDDBySearchBean(parameterType, searchBean);

			items = new SelectItem[objects.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
			int i = 1;
			for (Object[] object : objects) {
				items[i++] = new SelectItem((Long) object[1], (String) object[0]);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		}

		return items;
	}

	public SelectItem[] getDropdownUsers(List<PmUser> pmUsers) {
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();

		try {
			items = new SelectItem[pmUsers.size() + 1];

			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
			int i = 1;
			for (PmUser pmUser : pmUsers) {
				items[i++] = new SelectItem(pmUser.getUserId(), pmUser.getUserName());
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		}
		return items;
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
	public SelectItem[] getDropdownValuesBySearchBean(String parameterType, ISearchBean<PmParameterDesc> searchBean) {
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();
		try {
			List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance().getParameterDescListBySearchBean(parameterType, searchBean);

			items = new SelectItem[parameterDescs.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
			int i = 1;
			for (PmParameterDesc parameterDesc : parameterDescs) {
				items[i++] = new SelectItem(parameterDesc.getPmParameter().getParameterId(), parameterDesc.getCodeName());
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		}

		return items;
	}
	
	/** #21695 FARID START*/	
	/**
	 * Get parameter id select items by parameter type and search bean for
	 * dropdown disable/hide sample when partial offer true.
	 * 
	 * @param parameterType
	 *            the parameter type
	 * @param searchBean
	 *            the search bean instance
	 * @return array of SelectItem
	 */
	public SelectItem[] getDropdownValuesBySearchBeanForPartial(String parameterType, ISearchBean<PmParameterDesc> searchBean) {
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();
		try {
			List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance().getParameterDescListBySearchBean(parameterType, searchBean);

			items = new SelectItem[parameterDescs.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
			int i = 1;
			for (PmParameterDesc parameterDesc : parameterDescs) {
				items[i++] = new SelectItem(parameterDesc.getPmParameter().getParameterId(), parameterDesc.getCodeName());
			}
		} catch (Exception e) {
			// Sonarqube issues #21695
			SLAUtils.getInstance().logSLAError(SLALogConstant.COMMON_MODULE, SLALogConstant.COMMON_BASE_BACKINGBEAN, "", SLALogConstant.QT_GET_DROPDOWNVALUES_SEARCHBEAN_FOR_PARTIAL,
					getUserIdSessionFromLoginId(), e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		}

		return items;
	}
	/** #21695 FARID END*/
	
	/**
	 * Populate values for Reason Drop down base on reason category code
	 * 
	 * @param categoryCode
	 *            the reason category code
	 * 
	 * @return array of SelectItem
	 */
	public SelectItem[] getReasonCodeDropdownValues(String categoryCode) {
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();
		try {
			List<PmReasonDesc> reasonDescs = ReasonHelper.getInstance().getReasonDescList(categoryCode);

			items = new SelectItem[reasonDescs.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
			int i = 1;
			for (PmReasonDesc reasonDesc : reasonDescs) {
				items[i++] = new SelectItem(reasonDesc.getPmReason().getReasonCode(), reasonDesc.getReasonDesc());
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
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
	public PmReason getReasonByCategoryAndCode(String categoryCode, String reasonCode) {
		PmParameter reasonCategory = getParameterByTypeAndCode(ParameterTypeConst.REASON_CATEGORY, categoryCode);
		return ReasonHelper.getInstance().getReasonByCategoryAndCode(reasonCategory.getParameterId(), reasonCode);
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
	public SelectItem[] getDropdownCodeValuesBySearchBean(String parameterType, ISearchBean<PmParameterDesc> searchBean) {
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();

		try {
			List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance().getParameterDescListBySearchBean(parameterType, searchBean);

			items = new SelectItem[parameterDescs.size() + 1];

			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
			int i = 1;
			for (PmParameterDesc parameterDesc : parameterDescs) {
				items[i++] = new SelectItem(parameterDesc.getPmParameter().getParameterCode(), parameterDesc.getCodeName());
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		}

		return items;
	}
	
	//#27601 - START
	/**
	 * Get parameter code select items by parameter type for dropdown.
	 * 
	 * @param parameterType
	 * @return array of SelectItem
	 */
	public SelectItem[] getDropdownCodeValuesContractorCooperative(String parameterType) {
		return getDropdownCodeValuesBySearchBeanContractorCooperative(parameterType, null);
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
	public SelectItem[] getDropdownCodeValuesBySearchBeanContractorCooperative(String parameterType,
			ISearchBean<PmParameterDesc> searchBean) {
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();

		try {
			List<PmParameterDesc> parameterDescs = ParameterHelper
					.getInstance().getParameterDescListBySearchBean(
							parameterType, searchBean);

			if((parameterType.equalsIgnoreCase(ParameterTypeConst.SUPPLIER_BUSINESS_TYPE) || 
					parameterType.equalsIgnoreCase(ParameterTypeConst.SUPPLIER_BUSINESS_TYPE_CONTRACTOR))){
				
				items = new SelectItem[parameterDescs.size()];
			} else {
				items = new SelectItem[parameterDescs.size() + 1];
			}
			
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale()));
			int i = 1;
			for (PmParameterDesc parameterDesc : parameterDescs) {
				
				if((parameterType.equalsIgnoreCase(ParameterTypeConst.SUPPLIER_BUSINESS_TYPE) || 
						parameterType.equalsIgnoreCase(ParameterTypeConst.SUPPLIER_BUSINESS_TYPE_CONTRACTOR)) && 
						parameterDesc.getPmParameter().getParameterCode().equalsIgnoreCase("P")){
				} else {
					items[i++] = new SelectItem(parameterDesc.getPmParameter()
							.getParameterCode(), parameterDesc.getCodeName());	
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}
		return items;
	}
	//#27601 - END

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
	 * Get Parameter description by parameter and Locale.
	 * 
	 * @param parameter
	 *            the PmParameter instance.
	 * @param locale
	 *            the Locale instance.
	 * 
	 * @return parameter description
	 */
	public String getParameterDesc(PmParameter parameter, Locale locale) {
		if (parameter != null
				&& StringUtils.isNotBlank(parameter.getParameterType())) {
			if (locale == null) {
				locale = new FacesScopes().getUserLocale();
			}
			return ParameterHelper.getInstance().getParameterDesc(
					parameter.getParameterType(), parameter.getParameterCode(),
					locale);
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
	 * Get reason description by reason.
	 * 
	 * @param reason
	 *            the PmReason instance.
	 * @return reason description
	 */
	public PmReasonDesc getReasonDescByReasonId(Long reasonId) {
		if (reasonId != null) {
			return ReasonHelper.getInstance().getReasonDescById(reasonId);
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
	 * Get parameter descs by parameter type and search bean for dropdown.
	 * 
	 * @param parameterType
	 *            the parameter type
	 * @param searchBean
	 *            the search bean instance
	 * @return List<PmParameterDesc>
	 */
	public List<PmParameterDesc> getParameterDescsByTypeAndSearchBean(
			String parameterType, ISearchBean<PmParameterDesc> searchBean) {
		List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance()
				.getParameterDescListBySearchBean(parameterType, searchBean);
		return parameterDescs;
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
		return getStatusDropdownValuesBySearchBean(moduleCode,
				docType,
				null);
	}
	
	public SelectItem[] getStatusDropdownValuesWithExclusions(String moduleCode,
			String docType,Long... excludes) {
		return getStatusDropdownValuesBySearchBean(moduleCode,
				docType,
				null,excludes);
	}
	
	
	public SelectItem[] getStatusDropdownValuesBySearchBean(String moduleCode,
			String docType,SearchBean<PmStatusDesc> searchBean,Long... excludes) {
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();

		try {
			List<PmStatusDesc> statusDescs = StatusHelper.getInstance()
					.getStatusDescList(moduleCode, docType, searchBean);

			items = new SelectItem[(statusDescs.size()-excludes.length) + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale()));
			int i = 1;
			for (PmStatusDesc statusDesc : statusDescs) {
				if(!Arrays.asList(excludes).contains(statusDesc.getPmStatus().getStatusId())){
					items[i++] = new SelectItem(statusDesc.getPmStatus()
							.getStatusId(), statusDesc.getStatusName());
				}	
			}
		} catch (Exception e) {
			//Sonarqube QT Vulnerability Minor Danial [3/8/2023] START			 
			SLAUtils.getInstance().logSLAError(SLALogConstant.COMMON_MODULE, SLALogConstant.COMMON_BASE_BACKINGBEAN, "",
					SLALogConstant.COMMON_GET_STATUSDROPDOWNVALUES_SEARCHBEAN, getUserIdSessionFromLoginId(), e);
			//Sonarqube QT Vulnerability Minor Danial [3/8/2023] END
			
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}

		return items;
	}
	
	/**
	 * Get status select items by module and doc type for dropdown.
	 * 
	 * @param moduleCode
	 *            the module code
	 * @param docType
	 *            the document type
	 * @param searchBean
	 *            the SearchBean instance
	 * @return array of SelectItem
	 */
	public SelectItem[] getStatusDropdownValuesBySearchBean(String moduleCode,
			String docType, SearchBean<PmStatusDesc> searchBean) {
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();

		try {
			List<PmStatusDesc> statusDescs = StatusHelper.getInstance()
					.getStatusDescList(moduleCode, docType, searchBean);

			items = new SelectItem[statusDescs.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale()));
			int i = 1;
			for (PmStatusDesc statusDesc : statusDescs) {
				//start - 36615 - revert to previous code
				items[i++] = new SelectItem(statusDesc.getPmStatus()
						.getStatusId(), statusDesc.getStatusName());
				//end - end
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}

		return items;
	}

	/**
	 * Get status select items by statusIds.
	 * [This is a temporary solution, as report is to be revamped.]
	 * 
	 * @param statusId
	 *            the status id.
	 */
	
	public SelectItem[] getStatusDropdownValuesByIds(Long... statusIds) {
	
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();

		try {
		
			items = new SelectItem[statusIds.length + 1];
			
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale()));
			
			for (int i = 1; i <= statusIds.length; i++) {
			
				PmStatusDesc statusDesc = StatusHelper.getInstance()
						.getStatusDescById(statusIds[i - 1]);
				
				items[i] = new SelectItem(statusDesc.getPmStatus()
						.getStatusId(), statusDesc.getStatusName());
			}
			
		} catch (Exception e) {
			
			log.error(e.getMessage());
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
	
	/** #21695 Farid start */	
	public SelectItem[] getRadioButtonCodeValuesForPartial(String parameterType,String disabled) {
		return getRadioButtonCodeValuesBySearchBeanForPartial(parameterType, null, disabled);
	}
	/** #21695  END*/

	/**
	 * Gets the radio button code values by search bean.
	 *
	 * @param parameterType the parameter type
	 * @param searchBean the search bean
	 * @return the radio button code values by search bean
	 */
	public SelectItem[] getRadioButtonCodeValuesBySearchBean(
			String parameterType, ISearchBean<PmParameterDesc> searchBean) {
		SelectItem[] items = null;
		try {
			List<PmParameterDesc> parameterDescs = ParameterHelper
					.getInstance().getParameterDescListBySearchBean(
							parameterType, searchBean);

			items = new SelectItem[parameterDescs.size()];
			int i = 0;
			String codeName = "";//33849
			for (PmParameterDesc parameterDesc : parameterDescs) {
				//33849-Start
				if(parameterDesc.getPmParameter().getParameterCode().equalsIgnoreCase("CO")){
					codeName = ResourceBundleUtils.getMessage(
							"lbl.rpt.drpdown.contractOrderlbl", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale());
					items[i++] = new SelectItem(parameterDesc.getPmParameter()
							.getParameterCode(), codeName);
				}else{
					items[i++] = new SelectItem(parameterDesc.getPmParameter()
							.getParameterCode(), parameterDesc.getCodeName());
				}
				//33849-End
			}
		} catch (Exception e) {
			//Sonarqube issues
			SLAUtils.getInstance().logSLAError(SLALogConstant.COMMON_MODULE, SLALogConstant.COMMON_BASE_BACKINGBEAN, "",
					SLALogConstant.QT_GET_RADIOBUTTONCODEVALUES_SEARCHBEAN, getUserIdSessionFromLoginId(), e);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}

		return items;
	}

	/** #21695 Farid start */	
	//disable = TRUE,
	public SelectItem[] getRadioButtonCodeValuesBySearchBeanForPartial(
			String parameterType, ISearchBean<PmParameterDesc> searchBean,String disable) {
		SelectItem[] items = null;
		try {
			List<PmParameterDesc> parameterDescs = ParameterHelper
					.getInstance().getParameterDescListBySearchBean(
							parameterType, searchBean);

			items = new SelectItem[parameterDescs.size()];
			int i = 0;
			for (PmParameterDesc parameterDesc : parameterDescs) {
				if("TRUE,".contains(parameterDesc.getPmParameter()
						.getParameterCode())){
					items[i++] = new SelectItem(parameterDesc.getPmParameter()
							.getParameterCode(), parameterDesc.getCodeName(),"",true);
				}else{
					items[i++] = new SelectItem(parameterDesc.getPmParameter()
							.getParameterCode(), parameterDesc.getCodeName(),"",false);
				}
				
			}
		} catch (Exception e) {
			//Sonarqube issues #21695
			SLAUtils.getInstance().logSLAError(SLALogConstant.COMMON_MODULE, SLALogConstant.COMMON_BASE_BACKINGBEAN,
					"", SLALogConstant.QT_GET_RADIOBUTTONCODEVALUES_SEARCHBEAN, getUserIdSessionFromLoginId(), e);
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		}

		return items;
	}
	/** #21695  END*/
	
	
	/**
	 * Gets the radio button values.
	 *
	 * @param parameterType the parameter type
	 * @return the radio button values
	 */
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
			log.error(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}

		return items;
	}

	/**
	 * Common upload file.
	 *
	 * @param event the event
	 * @param path the path
	 */
	public void commonUploadFile(FileUploadEvent event, String path) {

		// String tempDir = "C://Ngep//doc//"+moduleName+"//";
		String tempDir = EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);
		tempDir += "/" + path;

		// String uploadDir = "";

		UploadedFile file = event.getFile();

		// uploadDir = tempDir + StringUtils.replace(" ", file.getFileName(),
		// "-");

		// genFile(uploadDir, file.getContents());
		genFile(tempDir, file);

	}

	/**
	 * Common download.
	 *
	 * @param path the path
	 * @param displayFileName the display file name
	 * @return the streamed content
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public StreamedContent commonDownload(String path, String displayFileName)
			throws IOException {
		
		String filePath = EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);
		
		filePath += "/" + path;
		commonDownloadUtil(filePath, displayFileName);
		
		return null;
	}
	
	public void commonDownloadUtil(String filePath, String displayFileName) throws IOException {
		log.info("START TO DOWNLOAD FILE");
		PortletResponse resp = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse response = PortalUtil.getHttpServletResponse(resp);

		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		// response.setContentType("application/vnd.ms-excel");
		// response.setHeader("Content-Transfer-Encoding", "binary");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + displayFileName + "\"");
		// response.setContentType("application/octet-stream");
		response.setContentType(getMimeType(displayFileName));
		response.flushBuffer();

		InputStream docInputStream = null;
		try {
			URLConnection uc = new URL(filePath).openConnection();
			docInputStream = new BufferedInputStream(uc.getInputStream());
			log.info("ZAIM getURL " + uc.getURL().toString());
			log.info("filePath----" + filePath);
		} catch (Exception e) {
			// Cannot open connection, maybe local file
			docInputStream = new FileInputStream(filePath);
			log.info("docInputStream===" + docInputStream);
			log.info("Error found while downloading file " + filePath + " : " + e);
			log.error(e);
			log.error(e.getMessage());

		}

		OutputStream out = null;
		try {

			out = response.getOutputStream();
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = docInputStream.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
				buffer = new byte[4096];
			}
		} catch (Exception e) {
			log.error(e);
		} finally {
			if (out != null) {
				out.close();
			}
			if (docInputStream != null) {
				docInputStream.close();
			}
		}
		log.info("END TO DOWNLOAD FILE");
	}
	
	private String getMimeType(String fileName) {
		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1);
		
		String mimeType = (String)CommonConstants.getFileExtMap().get(fileExt);
		
		return (mimeType != null) ? mimeType : "octet-stream";
	}
	
	public static void main(String[] args) {
		String fileName = "abcde.pdf";
//		fileName.lastIndexOf(".")
//		fileName.substring(fileName.indexOf("."));
		System.out.println(fileName.substring(fileName.lastIndexOf(".") + 1));
	}
	
	/**
	 * Success message.
	 * 
	 * @param messageCode
	 *            the message code
	 * @param docType
	 *            the doc type
	 * @param docTypeCode
	 *            the doc type code
	 * @param docNo
	 *            the doc no
	 * @param isAcknowledgeRequired
	 *            the is acknowledge required
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

		if (isAcknowledgeRequired) {
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);
		}
	}
	
	//25638 - Start
	public void successMessage(String messageCode, String messageCommon, String docNo, boolean isAcknowledgeRequired) {
		FacesContext.getCurrentInstance()
				.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null, 
								MessageUtils.getMessage(messageCode,
										new String[] { messageCommon, docNo }, 
										MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));

		if (isAcknowledgeRequired) {
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);
		}
	}
	//25638 - End

	// 25037 - start
	/**
	 * Return success message by message code.
	 * 
	 * @param messageCode
	 * @param params
	 */
	public void successMessage(String messageCode, String[] params) {
		successMessage(messageCode, params, false);
	}

	/**
	 * Return success message by message code. Navigate to acknowledgment page
	 * if required.
	 * 
	 * @param messageCode
	 * @param params
	 * @param isAcknowledgeRequired
	 */
	public void successMessage(String messageCode, String[] params, boolean isAcknowledgeRequired) {
	// 25037 - end
		FacesContext.getCurrentInstance().addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(messageCode, params,
						MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
		// 25037 - start
		if (isAcknowledgeRequired) {
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);
		}
		// 25037 - end
	}

	/**
	 * Gen file.
	 *
	 * @param uploadDir the upload dir
	 * @param file the file
	 */
	public void genFile(String uploadDir, UploadedFile file) {

		File dir = new File(uploadDir);
		log.info(" dir.exists()" + dir.exists());
		if (!dir.exists()) {
			log.info("before make directory");
			dir.mkdirs();
			log.info("after make directory");
		}
		String finalFileName = StringUtils.replace(file.getFileName(), " ", "-");

		try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(uploadDir + "/" + finalFileName))) {
			
			log.info("finalFileName: " + finalFileName);

			bufferedOutputStream.write(file.getContents());

			if (bufferedOutputStream != null) {
				bufferedOutputStream.flush();
				bufferedOutputStream.close();
			}

		} catch (Exception e) {
			log.error(e);
		}

	}

	/**
	 * Generate report.
	 *
	 * @param reportFile the report file
	 * @param paramMap the param map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void generateReport(String reportFile, Map<String, String> paramMap) throws IOException {
		String reportUrl = generateReportURL(reportFile, paramMap);

		HttpURLConnection urlConnection = null;
		URL obj = new URL(reportUrl);
		urlConnection = (HttpURLConnection) obj.openConnection();

		BufferedInputStream buf = new BufferedInputStream(urlConnection.getInputStream());

		PortletResponse portletResponse = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse res = PortalUtil.getHttpServletResponse(portletResponse);
		int position = reportFile.lastIndexOf(".jsp");
		if (position != -1) {
			reportFile = reportFile.substring(0, position);
		}

		if (paramMap.containsKey(ReportConst.DOC_TYPE_RTF)) {
			res.setHeader("Content-Disposition", "attachment; filename=\"" + reportFile + "_" + ReportConst.REPORT_DATE_TIME.format(new Date()) + ".rtf\"");//
			res.setContentType("application/octet-stream");
		} else {
			res.setHeader("Content-Disposition", "attachment; filename=\"" + reportFile + "_" + ReportConst.REPORT_DATE_TIME.format(new Date()) + ".pdf\"");//
			res.setContentType("application/pdf");
		}

		res.setHeader("Content-Transfer-Encoding", "binary");

		res.flushBuffer();

		OutputStream out = res.getOutputStream();

		byte[] b = new byte[128];
		int size;
		while ((size = buf.read(b, 0, b.length)) != -1) {
			out.write(b, 0, size);
		}
		
		buf.close();
		out.close();
		urlConnection.disconnect();
	}

	/**
	 * Generate report url.
	 *
	 * @param reportFile the report file
	 * @param paramMap the param map
	 * @return the string
	 * @throws IOException 
	 */
	protected String generateReportURL(String reportFile, Map<String, String> paramMap) throws IOException {
		EpPropertiesUtils propertiesUtils = EpPropertiesUtils.getInstance();
		String excPrevUrl = propertiesUtils.getProperty(EpPropertiesConst.EXC_PREV_URL);
		String excNextUrl = propertiesUtils.getProperty(EpPropertiesConst.EXC_NEXT_URL);

		if (paramMap.containsKey(ReportConst.DOC_TYPE_RTF)) {
			excNextUrl = propertiesUtils.getProperty(EpPropertiesConst.EXC_NEXT_URL_RTF);
		}

		if (paramMap.containsKey(ReportConst.PARAM_REPORT_OUTPUT_FORMAT)) {
			int fromIndex = excNextUrl.indexOf(ReportConst.PARAM_REPORT_OUTPUT_FORMAT);
			int endIndex = excNextUrl.indexOf(ReportConst.PARAM_SEPARATOR, fromIndex);
			String excEndUrl = "";
			if (fromIndex >= 0 && endIndex >= 0) {
				excEndUrl = excNextUrl.substring(endIndex);
			}
			excNextUrl = excNextUrl.substring(0, fromIndex - 1) + excEndUrl;
		}

		StringBuilder reportUrl = new StringBuilder(excPrevUrl);
		reportUrl.append(ReportConst.PARAM_SEPARATOR);
		reportUrl.append(ReportConst.PARAM_KEY_REPORT);
		reportUrl.append(ReportConst.VALUE_SEPARATOR);
		reportUrl.append(reportFile);

		for (Entry<String, String> params : paramMap.entrySet()) {
			if (!params.getKey().equals(ReportConst.DOC_TYPE_RTF)) {
				reportUrl.append(ReportConst.PARAM_SEPARATOR);
				reportUrl.append(params.getKey());
				reportUrl.append(ReportConst.VALUE_SEPARATOR);
				reportUrl.append(URLEncoder.encode(params.getValue(), UTF));
			}
		}
		reportUrl.append(excNextUrl);
		log.info("reportUrl: " + reportUrl);
		if (log.isDebugEnabled()) {
			log.debug("reportUrl: " + reportUrl);
		}
		return reportUrl.toString();
	}

	/**
	 * Gets the full address.
	 *
	 * @param smAddress the sm address
	 * @return the full address
	 */
	public String getFullAddress(SmAddress smAddress) {
		return AddressUtils.getFullAddress(smAddress);
	}

	/**
	 * Gets the SM full address.
	 *
	 * @param smAddress the sm address
	 * @return the SM full address
	 */
	public String getSMFullAddress(SmAddress smAddress) {
		return AddressUtils.getSmFullFormattedAddress(smAddress);
	}

	/**
	 * Gets the full address.
	 *
	 * @param pmAddress the pm address
	 * @return the full address
	 */
	public String getFullAddress(PmAddress pmAddress) {
		return AddressUtils.getFullAddress(pmAddress);
	}

	/**
	 * Gets the pp full address.
	 *
	 * @param ppAddress the pp address
	 * @return the pp full address
	 */
	public String getPpFullAddress(PpAddress ppAddress) {
		return AddressUtils.getFullAddress(ppAddress);
	}

	/**
	 * Gets the full address.
	 *
	 * @param ctAddress the ct address
	 * @return the full address
	 */
	public String getFullAddress(CtAddress ctAddress) {
		return AddressUtils.getFullAddress(ctAddress);
	}

	/**
	 * Gets the full address.
	 *
	 * @param scAddress the sc address
	 * @return the full address
	 */
	public String getFullAddress(ScAddress scAddress) {
		return AddressUtils.getFullAddress(scAddress);
	}
	
	/**
	 * Gets the full address.
	 *
	 * @param scAddress the sc address
	 * @return the full address
	 */
	public String getFullFormattedAddress(ScAddress scAddress) {
		return AddressUtils.getFullFormattedAddress(scAddress);
	}

	/**
	 * Handle exception.
	 *
	 * @param exception the exception
	 */
	public void handleException(Exception exception) {
		ExceptionHandler expHandler = FacesContext.getCurrentInstance()
				.getExceptionHandler();
		if (expHandler instanceof EpExceptionHandler) {
			((EpExceptionHandler) expHandler).handleException(exception);
		}
	}

	/**
	 * Escape xhtml.
	 *
	 * @param input the input
	 * @return the string
	 */
	public String escapeXhtml(String input) {
		String output = "";
		if (input != null) {
			output = StringUtils.replace(input, "&", "&#38;");
		}
		return output;
	}

	/**
	 * Gets the short file name.
	 *
	 * @param filename the filename
	 * @return the short file name
	 */
	public String getShortFileName(String filename) {
		if (filename != null && filename.length() > 15) {
			filename = filename.substring(0, 15);
			filename += "...";
		}
		return filename;
	}
	
	//#29418 start
	/**
	 * Gets the medium file name.
	 *
	 * @param filename the filename
	 * @return the short file name
	 */
	public String getMediumFileName(String filename) {
        if (StringUtils.isEmpty(filename) || filename.length() <= MAX_LENGTH) {
            return filename;
        }

        StringBuilder truncatedFilename = new StringBuilder(filename.substring(0, MAX_LENGTH));
        truncatedFilename.append("...");
        
        return truncatedFilename.toString();
    }
	//#29418 end

	/**
	 * Retrieve jabatan by org profile id.
	 *
	 * @param orgProfileId the org profile id
	 * @return the string
	 */
	public String retrieveJabatanByOrgProfileId(Long orgProfileId) {
		IOrgProfileSO profileSO = (IOrgProfileSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(orgProfileId);
		OrgProfileResDTO resDTO = new OrgProfileResDTO();
		resDTO = profileSO.retrieveJabatanByOrgProfileId(reqDTO);
		String jabatan = "";
		if (resDTO.getPmOrgProfile() != null) {
			jabatan = resDTO.getPmOrgProfile().getOrgName();
		}
		return jabatan;
	}

	/**
	 * Retrieve jabatan by org profile id.
	 *
	 * @param orgProfileId the org profile id
	 * @param tranDate the tran date
	 * @return the string
	 */
	public String retrieveJabatanByOrgProfileId(Long orgProfileId, Date tranDate) {
		IOrgProfileSO profileSO = (IOrgProfileSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(orgProfileId);
		reqDTO.setCreateDate(tranDate);
		OrgProfileResDTO resDTO = new OrgProfileResDTO();
		resDTO = profileSO.retrieveJabatanByOrgProfileId(reqDTO);
		String jabatan = "";
		if (resDTO.getPmOrgProfile() != null) {
			jabatan = resDTO.getPmOrgProfile().getOrgName();
		}
		return jabatan;
	}

	/**
	 * Retrieve pegawai pengawal by org profile id.
	 *
	 * @param orgProfileId the org profile id
	 * @return the string
	 */
	public String retrievePegawaiPengawalByOrgProfileId(Long orgProfileId) {
		IOrgProfileSO profileSO = (IOrgProfileSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(orgProfileId);
		OrgProfileResDTO resDTO = new OrgProfileResDTO();
		resDTO = profileSO.retrievePegawaiPengawalByOrgProfileId(reqDTO);
		String pagawaiPengawal = "";
		if (resDTO.getPmOrgProfile() != null) {
			pagawaiPengawal = resDTO.getPmOrgProfile().getOrgName();
		}
		return pagawaiPengawal;
	}

	/**
	 * Retrieve pegawai pengawal by org profile id.
	 *
	 * @param orgProfileId the org profile id
	 * @param tranDate the tran date
	 * @return the string
	 */
	public String retrievePegawaiPengawalByOrgProfileId(Long orgProfileId,
			Date tranDate) {
		IOrgProfileSO profileSO = (IOrgProfileSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(orgProfileId);
		reqDTO.setCreateDate(tranDate);
		OrgProfileResDTO resDTO = new OrgProfileResDTO();
		resDTO = profileSO.retrievePegawaiPengawalByOrgProfileId(reqDTO);
		String pagawaiPengawal = "";
		if (resDTO.getPmOrgProfile() != null) {
			pagawaiPengawal = resDTO.getPmOrgProfile().getOrgName();
		}
		return pagawaiPengawal;
	}

	/**
	 * Retrieve ministry code by org profile id.
	 *
	 * @param orgProfileId the org profile id
	 * @return the string
	 */
	public String retrieveMinistryCodeByOrgProfileId(Long orgProfileId) {
		IOrgProfileSO profileSO = (IOrgProfileSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(orgProfileId);
		return profileSO.retrieveMinistryByOrgProfileId(reqDTO)
				.getPmOrgProfile().getOrgCode();
	}

	/**
	 * Retrieve ministry by org profile id.
	 *
	 * @param orgProfileId the org profile id
	 * @return the string
	 */
	public String retrieveMinistryByOrgProfileId(Long orgProfileId) {
		IOrgProfileSO profileSO = (IOrgProfileSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(orgProfileId);
		PmOrgProfile orgProfile = profileSO.retrieveMinistryByOrgProfileId(
				reqDTO).getPmOrgProfile();
		if (orgProfile != null) {
			return orgProfile.getOrgName();
		} else {
			log.info("Ministry not found : " + orgProfileId);
			return "";
		}
	}

	/**
	 * Retrieve ministry by org profile id.
	 *
	 * @param orgProfileId the org profile id
	 * @param tranDate the tran date
	 * @return the string
	 */
	public String retrieveMinistryByOrgProfileId(Long orgProfileId,
			Date tranDate) {
		IOrgProfileSO profileSO = (IOrgProfileSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(orgProfileId);
		reqDTO.setCreateDate(tranDate);
		PmOrgProfile orgProfile = profileSO.retrieveMinistryByOrgProfileId(
				reqDTO).getPmOrgProfile();
		if (orgProfile != null) {
			return orgProfile.getOrgName();
		} else {
			log.info("Ministry not found : " + orgProfileId);
			return "";
		}
	}

	/**
	 * Retrieve ministry org profile.
	 *
	 * @param orgProfileId the org profile id
	 * @return the pm org profile
	 */
	public PmOrgProfile retrieveMinistryOrgProfile(Long orgProfileId) {
		IOrgProfileSO profileSO = (IOrgProfileSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(orgProfileId);
		return profileSO.retrieveMinistryByOrgProfileId(reqDTO)
				.getPmOrgProfile();
	}

	/**
	 * Retrieve jabatan org profile.
	 *
	 * @param orgProfileId the org profile id
	 * @return the pm org profile
	 */
	public PmOrgProfile retrieveJabatanOrgProfile(Long orgProfileId) {
		IOrgProfileSO profileSO = (IOrgProfileSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(orgProfileId);
		PmOrgProfile pmOrgProfile = profileSO.retrieveJabatanByOrgProfileId(
				reqDTO).getPmOrgProfile();
		if (pmOrgProfile == null) {
			pmOrgProfile = new PmOrgProfile();
			pmOrgProfile.setOrgProfileId(-1L);
		}
		return pmOrgProfile;
	}

	/**
	 * Retrieve pegawai pegawal org profile.
	 *
	 * @param orgProfileId the org profile id
	 * @return the pm org profile
	 */
	public PmOrgProfile retrievePegawaiPegawalOrgProfile(Long orgProfileId) {
		IOrgProfileSO profileSO = (IOrgProfileSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(orgProfileId);
		return profileSO.retrievePegawaiPengawalByOrgProfileId(reqDTO)
				.getPmOrgProfile();
	}

	/**
	 * Retrieve ptj by org profile id.
	 *
	 * @param orgProfileId the org profile id
	 * @param tranDate the tran date
	 * @return the string
	 */
	public String retrievePTJByOrgProfileId(Long orgProfileId, Date tranDate) {
		IOrgProfileSO profileSO = (IOrgProfileSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(orgProfileId);
		reqDTO.setCreateDate(tranDate);
		PmOrgProfile orgProfile = profileSO.retrieveOrgProfileByProfileId(
				reqDTO).getPmOrgProfile();
		if (orgProfile != null) {
			return orgProfile.getOrgName();
		} else {
			log.info("PTJ not found : " + orgProfileId);
			return "";
		}
	}

	/**
	 * Get eP Role description by role code
	 * 
	 * @param String
	 *            roleCode
	 * @return String
	 */
	public String getEpRoleDescByRoleCode(String roleCode) {
		String retVal = SmCommonUtil.getMessage("lbl.txt.na", null);
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
	//start #29956
	/**
	 * Compare all list in params with epRole
	 * 
	 * @param String
	 *            roleCode
        
	 * @return String
	 */
	public Boolean compareSmPersonnelRoleAndStringRole(String[] params, String epRole) {
		Boolean available = false;
		String epRoleDesc = getEpRoleDescByRoleCode(epRole);
		if (params != null && params.length > 0) {
			for (int x=0; x<params.length; x++) {
				if (params[x]!=null && params[x].equals(epRoleDesc)) {
					available = true;
				}
			}
		}
		return available;
	}
	
	/**
	 * Get no eP Role by language
	 * 
	 * @param String
	 *            roleCode
	 * @return String
	 */
	public String getNoEpRoleDescByLanguage() {
		String language = new FacesScopes().getUserLocale().getLanguage();
		String noEp;
		if(language.equalsIgnoreCase("en")){
			noEp = "no EP Role";
		}else{
			noEp = "profil yang tiada peranan";
		}
		return noEp;
	}
	
	/**
	 * Get eP Role for Downgrade Popup by personnel
	 * 
	 * @param String
	 *            roleCode
	 * @return String
	 */
	public String getEpRoleDowngradePopupByPersonnel(List<SmPersonnel> personnelsRole) {
		String[] params = new String[50];
		StringBuilder sb = new StringBuilder();
		int x = 0;
		Boolean noEp = false;
		if (!personnelsRole.isEmpty()) {
			for (SmPersonnel smPersonnel : personnelsRole) {
				int countRole=0;
				if(smPersonnel.isHigherManagement()){
					countRole++;
				}
				if(smPersonnel.isManagement()){
					countRole++;
				}
				if(smPersonnel.isEquityOwner()){
					countRole++;
				}
				if(countRole>1){
					if(smPersonnel.isHigherManagement()){
						smPersonnel.setManagement(false);
						smPersonnel.setEquityOwner(false);
					}else if(smPersonnel.isManagement()){
						smPersonnel.setHigherManagement(false);
						smPersonnel.setEquityOwner(false);
					}
				}
				if (x != 0) {
					for (String roles : params) {
						if(roles!=null){
							if (smPersonnel.getEpRole() == null && !roles.equals(getNoEpRoleDescByLanguage()) && !roles.equals("/ ")
									&& !noEp) {
								params[x] = "/ ";
								sb.append("/ ");
								x++;
								params[x] = getNoEpRoleDescByLanguage();
								sb.append(getNoEpRoleDescByLanguage());
								x++;
								noEp = true;
							} else if (smPersonnel.getEpRole() != null&& !roles.equals(getEpRoleDescByRoleCode(smPersonnel.getEpRole())) 
									&& !compareSmPersonnelRoleAndStringRole(params, smPersonnel.getEpRole()) && !roles.equals("/ ")) {
								params[x] = "/ ";
								sb.append("/ ");
								x++;
								params[x] = getEpRoleDescByRoleCode(smPersonnel.getEpRole());
								sb.append(getEpRoleDescByRoleCode(smPersonnel.getEpRole()));
								x++;
							}
						}
					}
				} else {
					if(smPersonnel.getEpRole() == null){
						params[x] = getNoEpRoleDescByLanguage();
						sb.append(getNoEpRoleDescByLanguage());
						x++;
						noEp = true;
					}else{
						params[x] = getEpRoleDescByRoleCode(smPersonnel.getEpRole());
						sb.append(getEpRoleDescByRoleCode(smPersonnel.getEpRole()));
						x++;
					}
				}
			}
		}
		return sb.toString();
	}
	//end #29956


	/**
	 * Get machine category type
	 * 
	 * @param SmMachineDtl
	 *            machine
	 * @return String
	 */
	public String getMachineCategoryType(SmMachineDtl machine) {
		String retVal = SmCommonUtil.getMessage("lbl.sm.caps.others", null);
		String language = new FacesScopes().getUserLocale().getLanguage();
		if (machine.getCategoryType() != null
				&& !EntityHelper.isCollectionEmpty(machine.getCategoryType()
						.getPmParameterDescs())) {
			for (PmParameterDesc desc : machine.getCategoryType()
					.getPmParameterDescs()) {
				if (desc.getLanguageCode().equals(language)) {
					retVal = desc.getCodeName();
				}
			}
		}
		return retVal;
	}

	/**
	 * Populate local authority when on change business type or district
	 * 
	 * @param SmSupplier
	 *            supplier
	 * @param SmAddress
	 *            smAddress
	 */
	public void populateLocalAuthority(SmSupplier supplier, SmAddress smAddress) {
		getManagedBean().setLocalAuthorityItems(
				SmCommonUtil.getLocalAuthorityItems(supplier.getBusinessType(),
						smAddress));
		if (!SupplierManagementConst.LOCAL_AUTH_BUSINESS_TYPE.contains(supplier
				.getBusinessType())
				|| getManagedBean().getLocalAuthorityItems().size() == 1) {
			supplier.setLocalAuthorityId((Long) getManagedBean()
					.getLocalAuthorityItems().get(0).getValue());
		}
	}

	/**
	 * Get list size
	 * 
	 * @param Collection
	 *            <?> value
	 * @return int
	 */
	public int getListSize(Collection<?> value) {
		return value.size();
	}

	
	/**
	 * Download file.
	 *
	 * @param filePath the file path
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void downloadFile(String filePath) throws IOException {
		File file = new File(filePath);
		log.info("download file>>>>");
		OutputStream out = null;
		try {
			PortletResponse portletResponse = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
			HttpServletResponse res = com.liferay.portal.util.PortalUtil.getHttpServletResponse(portletResponse);
			res.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");//
			res.setHeader("Content-Transfer-Encoding", "binary");
			res.setContentType("application/octet-stream");
			res.flushBuffer();
			out = res.getOutputStream();
			if (file.getPath() != null) {
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
				while ((bytesRead = input.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
					buffer = new byte[4096];
				}
				input.close();
			} else {
				// out.write(attachment.getFileByte());
			}

			FacesContext.getCurrentInstance().responseComplete();
		} catch (Exception ex) {
			log.error("download exception>>>>" + ex.getMessage());
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
		}
	}

	/**
	 * Gets the captcha text image.
	 *
	 * @return the captcha text image
	 */
	public StreamedContent getCaptchaTextImage() {
		StreamedContent graphicText = null;
		ByteArrayOutputStream os = null;
		try {
			// #33136 - start
			BufferedImage bufferedImg = new BufferedImage(110, 25, BufferedImage.TYPE_BYTE_BINARY);
			// #33136 - end
			Graphics2D g2 = bufferedImg.createGraphics();

			String captchaText = RandomStringUtils.randomAlphanumeric(6);
			this.getManagedBean().setCaptchaText(captchaText);

			Font font = new Font("Tahoma", Font.ITALIC, 20);
			g2.setFont(font);

			g2.drawString(captchaText, 15, 20);
			os = new ByteArrayOutputStream();
			ImageIO.write(bufferedImg, "png", os);
			graphicText = new DefaultStreamedContent(new ByteArrayInputStream(os.toByteArray()), "image/png");
			/*
			if (graphicText == null) {
				log.info(">>>>>>>>>>>>Graphic is null!!!");
			} else {
				log.info(">>>>>>>>>>>>Graphic is not null:" + graphicText.getStream());
			}
			*/
		} catch (Exception e) {
			log.error(e);
		} finally {
			try {
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				log.error(e);
			}
		}
		return graphicText;
	}

	/**
	 * Validate captcha.
	 *
	 * @param context the context
	 * @param component the component
	 * @param value the value
	 */
	public void validateCaptcha(FacesContext context, UIComponent component,
			Object value) {
		String skipValidator = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap()
				.get("skipValidator");
		//START 31758
		EpPropertiesUtils prop = EpPropertiesUtils.getInstance();
		String captchaEnable = prop.getProperty("captcha.enabled");
		if(captchaEnable != null && "false".equalsIgnoreCase(captchaEnable)){
			skipValidator = "true";
		}		
		//END 31758
		if (skipValidator == null) {
			String captchaAnswer = (String) value;
			if (captchaAnswer == null || captchaAnswer.length() == 0) {
				log.error("Captcha is null");
				throw new ValidatorException(new FacesMessage(
						FacesMessage.SEVERITY_ERROR, null,
						SmCommonUtil.getOutputMsgBundle(
								MessageConst.ERROR_721046, null)));

			}

			if (captchaAnswer != null && captchaAnswer.length() > 0) {
				if (!captchaAnswer.equals(this.getManagedBean()
						.getCaptchaText())) {
					log.error("Captcha is wrong");
					throw new ValidatorException(new FacesMessage(
							FacesMessage.SEVERITY_ERROR, null,
							SmCommonUtil.getOutputMsgBundle(
									MessageConst.ERROR_721047, null)));
				}
			}
		}
	}

	/**
	 * original default
	 * To get BpmTask Details.
	 * 
	 * @param clazz
	 * @return Object payLoadInstance
	 */
	protected <BPM_DATA_TYPE> BPM_DATA_TYPE getBpmTaskDetails(Class<BPM_DATA_TYPE> clazz, String[] additionalButtons) {
		return getBpmTaskDetails(clazz, additionalButtons, null, null);
	}
	
	
	protected <BPM_DATA_TYPE> BPM_DATA_TYPE getBpmTaskDetails(Class<BPM_DATA_TYPE> clazz, String[] additionalButtons, String theTaskId) {
		return getBpmTaskDetails(clazz, additionalButtons, theTaskId, null);
	}
	
	/**
	 * To get BpmTask Details.
	 * 
	 * @param clazz, additionalButtons(optional), theTaskId(optional), theLoginId(optional)
	 * @return Object payLoadInstance
	 */
	@SuppressWarnings("unchecked")
	protected <BPM_DATA_TYPE> BPM_DATA_TYPE getBpmTaskDetails(Class<BPM_DATA_TYPE> clazz, String[] additionalButtons, String theTaskId, String theLoginId) {
		String taskId = null;
		String taskIdPair = null;// #31422-basri 190821
		FacesScopes facesScopes = new FacesScopes();
		if (StringUtils.isBlank(theTaskId)) {
			HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(facesScopes.getRequest());
			HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);

			taskId = originalServletRequest.getParameter(ContractConst.TASK_ID);
			taskIdPair = originalServletRequest.getParameter("taskIdPair"); // #31422-basri 190821
		} else {
			taskId = theTaskId;
		}

		IBpmSO bpmSO;

		try {
			bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

			BpmRequestDTO reqDTO = new BpmRequestDTO();
			if (theLoginId != null && !StringUtils.isBlank(theLoginId)) {
				reqDTO.setCtxToken(bpmSO.authenticate(theLoginId));
			} else {
				reqDTO.setCtxToken(facesScopes.getBPMAuthenticationCtxToken());
			}
			reqDTO.setTaskId(taskId);

			BpmResponseDTO resDTO = bpmSO.getTaskDetails(reqDTO);

			this.renewSesionObject();

			boolean isAssignedState = BpmConstants.STATE_ASSIGNED.equals(resDTO.getStatus());
			Object payLoad = resDTO.getPojo();
			BPMTaskListBackingBean bb = new BPMTaskListBackingBean();

			if (null != resDTO.isGroupTask() && resDTO.isGroupTask() == true) {
				log.info("====groupTask not null===" + resDTO.isGroupTask());
				Boolean claim = resDTO.isClaimed();
				log.info("claim:" + claim);
				if (null != claim && claim == false) {
					this.getManagedBean().setReadOnly(true);
				} else {
					this.getManagedBean().setReadOnly(false);
				}
				bb.getManagedBean().setClaim(claim);
				bb.getManagedBean().setTaskId(taskId);
				// bb.getManagedBean().setTaskIdPair(taskIdPair); //#31422-basri
				// 190821
				// 21387 - Start
				bb.getManagedBean().setCurrentTaskDocType(resDTO.getDocType());
				bb.getManagedBean().setDocNo(resDTO.getDocNumber());
				// 21387 - End
				bb.getManagedBean().setGroupTask(true);
			} else {
				log.info("====groupTask null===" + resDTO.isGroupTask());
				this.getManagedBean().setReadOnly(false);
				bb.getManagedBean().setClaim(true);
				bb.getManagedBean().setGroupTask(false);
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
				this.getManagedBean().setOutComes(sortOutcomes(outComes));
			}
			this.getManagedBean().setCurrentStatusId(Long.valueOf(resDTO.getDocStatus()));
			this.getManagedBean().setTaskName(resDTO.getTaskName());
			this.getManagedBean().setPayLoad(payLoad);
			this.getManagedBean().setProcessName(resDTO.getProcessName());
			// 25037 - start
			this.getManagedBean().setCurrentTaskDocType(resDTO.getDocType());
			this.getManagedBean().setCurrentTaskCreatedDate(resDTO.getCreateDate());
			// 25037 - end
			// #29007 Start Vinod
			this.getManagedBean().setBpmVersionNo(resDTO.getBpmVersionNo());
			// #29007 End Vinod
			return (BPM_DATA_TYPE) payLoad;

		} finally {
			bpmSO = null;
		}
	}

	private String[] sortOutcomes(String[] outComes) {
		List<String> bpmActions = new ArrayList<String>(Arrays.asList(outComes));

		// Move 'Cancel' button to the last
		if (bpmActions.contains(BpmOutcomeConst.BPM_CANCEL)
				&& !bpmActions.get(bpmActions.size() - 1).equalsIgnoreCase(
						BpmOutcomeConst.BPM_CANCEL)) {
			bpmActions.remove(BpmOutcomeConst.BPM_CANCEL);
			bpmActions.add(BpmOutcomeConst.BPM_CANCEL);
		}

		outComes = new String[bpmActions.size()];
		return bpmActions.toArray(outComes);
	}

	/**
	 * Get Rule Type Description by rule type
	 * 
	 * @param ruleType
	 *            the rule type
	 * @return the rule type description.
	 */
	public String getRuleTypeDesc(String ruleType) {
		IRuleSO ruleSO = null;
		try {
			ruleSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);

			RuleReqDTO reqDTO = new RuleReqDTO();

			reqDTO.setRuleType(ruleType);
			reqDTO.setLanguageCode(new FacesScopes().getUserLocale()
					.getLanguage());

			return ruleSO.getRuleDescByType(reqDTO);

		} finally {
			ruleSO = null;
		}
	}

	/**
	 * Get year drop down
	 * 
	 * @return array of SelectItem
	 */
	public SelectItem[] getYearDropDown() {
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();

		try {
			Calendar now = Calendar.getInstance();
			int currentYear = now.get(Calendar.YEAR);
			int yearRange = CommonConstants.YEAR_RANGE;
			int yearDiff = currentYear - yearRange;

			items = new SelectItem[yearRange + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale()));
			int i = 1;

			for (int y = currentYear; y > yearDiff; y--) {
				items[i++] = new SelectItem(y, String.valueOf(y));
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}

		return items;

	}

	/**
	 * Return the display text as N/A or T/B if it is null or empty.
	 * 
	 * @param text
	 * @return displayText
	 */
	public Object getText(Object text) {
		FacesScopes face = new FacesScopes();
		if (text == null
				|| (text instanceof String && ((String) text).length() == 0)) {
			return ResourceBundleUtils.getMessage("lbl.txt.na",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale());
		} else {
			return text;
		}
	}

	/**
	 * File Upload
	 * 
	 * @param String
	 *            path
	 * @param String
	 *            fileName
	 * @param String
	 *            displayFileName
	 * @return
	 * @throws IOException
	 */
	public StreamedContent commonDownload(String path, String fileName,
			String displayFileName) throws IOException {
		String finalFilePath = path + "/" + fileName;

		return commonDownload(finalFilePath, displayFileName);
	}

	/**
	 * Deleteselected list.
	 *
	 * @param <ENTITY_CLASS> the generic type
	 * @param list the list
	 * @return true, if successful
	 */
	public <ENTITY_CLASS extends BaseEntity> boolean deleteselectedList(
			List<ENTITY_CLASS> list) {
		if (list != null) {
			for (ENTITY_CLASS entity : list) {
				if (entity.isSelected()) {
					return false;
				}

			}
		}
		return true;
	}
	
	/**
	 * Retrieve Unknown Country
	 * @return pmCountry
	 */
	public PmCountry getUnknownCountry(){
		ICountrySO countrySO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);
		CountryReqDTO reqDTO = new CountryReqDTO();
		reqDTO.setCountryCode(ParameterConst.COUNTRY_UNKNOWN_CODE);
		
		PmCountry country = countrySO.retrieveCountryByCode(reqDTO).getPmCountry();

		return country;
	}
	
	/**
	 * Validation on amount as 1GFMAS accept NUM(11,2)
	 * @param label
	 * @param amount
	 */
	protected void validateAmount(String label, BigDecimal amount) {
		if((amount.precision()-amount.scale())>11 || amount.scale()>2) {
			CommonUtils commonUtils = new CommonUtils();
			DecimalFormat nf = new DecimalFormat(commonUtils.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
			throw new EpException(MessageConst.ERROR_100039, new String[]{label+" "+nf.format(amount),nf.format(BigDecimal.ZERO),nf.format(new BigDecimal("99999999999.99"))});
		}
	}
	
	
	/**
	 * Convert string.
	 *
	 * @param label the label
	 * @param length the length
	 * @return the string
	 */
	public String convertString(String label, int length){
		
		String formattedString = "";
		if(label.trim().length()>=length){
			if(label!=null && label.trim().length()>0){
				formattedString = label.substring(0, length);
				
				formattedString = formattedString + "...";
			}
		}else{
			formattedString = label;
		}
		
		
		return formattedString;
	}
	
	/**
	 * Validate email format
	 * 
	 * @param String
	 *            email
	 * @return boolean
	 */
	public boolean isValidEmail(String email) {
		Pattern pattern = Pattern
				.compile("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");
		return pattern.matcher(email).matches();
	}
	
	/**
	 * uom autocompletelist.
	 * 
	 * 
	 *@return the list of PmUom instance 		
	 */
	public List<PmUom> autoCompleteUom(String searchText) {
		IUomSO uomSo = null;
		try {
			uomSo = (IUomSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUomSO.class);
			UomReqDTO reqDTO = new UomReqDTO();
			SearchBean<PmUom> searchBean = new SearchBean<PmUom>();
			searchBean.setNavigatable(true);
			searchBean.setNoOfRowsPerPage(50);
			SearchFilterBean uomNameSFB = new SearchFilterBean(
					"uomName",
					SearchOperator.OPERATOR_LIKE.value(),
					searchText!=null?searchText.toLowerCase():searchText,SearchCondition.CONDITION_OR.value());
			uomNameSFB.setCaseInsensitive(true);
			searchBean.getDataFilterBeans().add(
					new SearchMultiFilterBean(
							new SearchFilterBean(
									"uomCode",
									SearchOperator.OPERATOR_LIKE.value(),
									searchText,true),
							uomNameSFB
							));
			//Bug #21863
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("recordStatusFlg",
							SearchOperator.OPERATOR_EQUALS.value(),
							DbValueConst.RECORD_STATUS_ACTIVE));
					
			searchBean.getSortColumns().add(new SearchOrderBean("uomName"));
			reqDTO.setMeasurementSearchBean(searchBean);
			return uomSo.retrieveMeasurementBySearch(reqDTO)
					.getPmUomList();
		} finally {
			uomSo = null;
		}
	}
	
	/**
	 * getUomCode and description.
	 * 
	 * 
	 *@return uomCodeDesc.		
	 */
	public String retriveUomCodeDesc(PmUom pmUom){
		String uomCodeDesc="";
		if(null!=pmUom){
			if(null!=pmUom.getUomCode() && null!=pmUom.getUomName()){
			uomCodeDesc=pmUom.getUomCode()+" "+" -"+" "+pmUom.getUomName();	
			}
		}
		
	return uomCodeDesc;	
	}
	
	/**
	 * Get Parameter Desc By Param Type And Param Code.
	 * 
	 * @param parameter
	 * @return
	 */
	public PmParameterDesc getParameterDescByParamTypeAndParamCode(PmParameter parameter) {
		if (parameter != null
				&& StringUtils.isNotBlank(parameter.getParameterType())) {
			return ParameterHelper.getInstance().getParameterDescByParamTypeAndParamCode(
					parameter.getParameterType(), parameter.getParameterCode());
		} else {
			return null;
		}
	}
	
	
	protected FacesScopes getFacesScopesGen() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	/**
	 * Determine if user for current session have role
	 * 
	 * @param roleCodes
	 * @return
	 */
	public Boolean userHasRole(RoleCode [] roleCodes){
		FacesScopes faceScopes = new FacesScopes();
		if(roleCodes != null && roleCodes.length > 0){
			for(int i = 0; i< roleCodes.length; i++){
				if(faceScopes.hasRoleUser(roleCodes[i].value())) return true;
			}
		}
		return false;
	}
	
	public boolean isNull(Object o){
		if(o == null){
			return true;
		}
		return false;
	}
	
	//16052019 - SSM INTEGRATION CR - Fahmi
			public boolean businessTypeTovalidateWithSSM(String regNo,String businessType){
				//String regNo = this.getManagedBean().getSmSupp().getRegNo();
				//String businessType = this.getManagedBean().getSmSupp().getBusinessType();
				if(//businessType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_INDIVIDUAL_DEFAULT)
						//|| businessType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PARTNER_DEFAULT)
								//|| businessType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH)
									//	|| businessType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK)
												 businessType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SEMENANJUNG)
														//|| businessType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH)
																//|| businessType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK)
																		|| businessType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SEMENANJUNG)
																				|| businessType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_BHD)
																						//|| businessType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_DEFAULT)
																								|| businessType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_SDN_BHD)){
					return true;
				}else{
					return false;
				}
			}
			public boolean businessTypeIsROB(String businessType){
				//#36710 --START
				if(businessType != null && (businessType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SEMENANJUNG)
						|| businessType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SEMENANJUNG)
																				)){
					//#36710 --END
					return true;
				}else{
					return false;
				}
			}
			public boolean businessTypeIsROC(String businessType){
				//#36710 --START
				if(businessType != null && (businessType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_BHD)
						|| businessType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_SDN_BHD)
																				)){
					//#36710 --END
					return true;
				}else{
					return false;
				}
			}
			//16052019 - SSM INTEGRATION CR
			public String setStatusMessageFromSSM(String statusCode){
				if("A".equalsIgnoreCase(statusCode)){
					return SmCommonUtil.getMessage("lbl.sm.status.active", null);
				}else if("E".equalsIgnoreCase(statusCode)){
					return SmCommonUtil.getMessage("lbl.sm.status.existing", null);
				}else if("L".equalsIgnoreCase(statusCode)){
					return SmCommonUtil.getMessage("lbl.sm.status.expired", null);
				}else if("T".equalsIgnoreCase(statusCode)){
					return SmCommonUtil.getMessage("lbl.sm.status.terminated", null);
				}else if("B".equalsIgnoreCase(statusCode)){ 
				//27382
					return SmCommonUtil.getMessage("lbl.sm.status.b", null);
				}else if("W".equalsIgnoreCase(statusCode)){
					return SmCommonUtil.getMessage("lbl.sm.status.w", null);
				}else if("D".equalsIgnoreCase(statusCode)){
					return SmCommonUtil.getMessage("lbl.sm.status.d", null);
				}else if("R".equalsIgnoreCase(statusCode)){
					return SmCommonUtil.getMessage("lbl.sm.status.r", null);
				}else if("C".equalsIgnoreCase(statusCode)){
					return SmCommonUtil.getMessage("lbl.sm.status.c", null);
				}else if("X".equalsIgnoreCase(statusCode)){
					return SmCommonUtil.getMessage("lbl.sm.status.x", null);
				}else if("Y".equalsIgnoreCase(statusCode)){
					return SmCommonUtil.getMessage("lbl.sm.status.y", null);
				}
				//start #29935
				else if("F".equalsIgnoreCase(statusCode)){
					return SmCommonUtil.getMessage("lbl.sm.status.winding.up.under.cva", null);
				}
				else if("G".equalsIgnoreCase(statusCode)){
					return SmCommonUtil.getMessage("lbl.sm.status.existing.under.jm.under.cva", null);
				}
				else if("J".equalsIgnoreCase(statusCode)){
					return SmCommonUtil.getMessage("lbl.sm.status.existing.under.jm", null);
				}
				else if("V".equalsIgnoreCase(statusCode)){
					return SmCommonUtil.getMessage("lbl.sm.status.existing.under.cva", null);
				}
				//end #29935
				return "";
			}
	
	
	public Boolean userHasUserTask(UserTasks [] uts){
		for(int i = 0; i < uts.length; i++){
			if(userHasUserTask(uts[i])){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * To determine user's UserTasks in getManagedBean()
	 * 
	 * @param ut - userTask to be tested
	 * @return
	 */
	public Boolean userHasUserTask(UserTasks ut){
		if(getManagedBean().getUserTasks()!=null && !CollectionUtils.isEmpty(getManagedBean().getUserTasks())){
			if(getManagedBean().getUserTasks().contains(ut)){
				return true;
			}			
		}
		return false;
	}
	
	public Boolean userHasUserTask(String ut){
		if(getManagedBean().getUserTasks()!=null && !CollectionUtils.isEmpty(getManagedBean().getUserTasks())){
			Object[] t = getManagedBean().getUserTasks().toArray();
			for(int i = 0; i<t.length; i++){
				if(((UserTasks)t[i]).toString().equals(ut)){
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * getUomCode and description.
	 * 
	 * 
	 *@return uomCodeDesc.		
	 */
	public static String retriveUomDescOnly(PmUom pmUom){
		String uomCodeDesc="";
		if(null!=pmUom){
			if(null!=pmUom.getUomCode() && null!=pmUom.getUomName()){
			uomCodeDesc=pmUom.getUomName();	
			}
		}
		
	return uomCodeDesc;	
	}
	/**
	 * getUomCode and description.
	 * 
	 * 
	 *@return uomCodeDesc.		
	 */
	public static String retriveUomCodeOnly(PmUom pmUom){
		String uomCode="";
		if(null!=pmUom){
			if(null!=pmUom.getUomCode() && null!=pmUom.getUomName()){
			uomCode=pmUom.getUomCode();	
			}
		}
		
	return uomCode;	
	}
	
	//16052019 - SSM INTEGRATION CR
	public boolean showGreenIconSsm(Boolean ssmData,boolean isSsmAppl,boolean isNullValue){
		boolean showIcon = false;
		if(isSsmAppl && isNullValue){
			return true;
		}
		return showIcon;
	}
	
	//16052019 - SSM INTEGRATION CR
	public boolean disableFieldSSm(Boolean ssmData,Boolean isSsmAppl,boolean isNullValue){
		boolean disabledField = false;
		if(isSsmAppl == null && ssmData && !isNullValue){
			disabledField = true;
		} else if(isSsmAppl != null && isSsmAppl && ssmData && !isNullValue){
			disabledField = true;
		} else {
			disabledField = false;
		}
		return disabledField;
	}
	
	public void changeRoleAndProceed() {
		//start loop list #28454
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		IPersonnelSO personnelSO = (IPersonnelSO) BusinessDelegate.getInstance().getServiceObject("sm", IPersonnelSO.class);
		this.getManagedBean().setDowngradeRole(true);  //#29956
		for(SmPersonnel personnel : this.getManagedBean().getPersonnelsRole()){
				//27951
				if(!personnel.isClerical() && !personnel.isHigherManagement() && !personnel.isManagement()){
					this.getManagedBean().getPersonnelsRole().clear();
					this.getManagedBean().setCheckRoleSSM(false);
					this.getManagedBean().setDowngradeRole(false); //#29956
					throw new EpException(MessageConst.ERROR_722535); 
				}else{
					//27951
					personnel.setEquityOwner(false);
					personnel.setDirector(false);
					this.getManagedBean().setCheckRoleSSM(true);
					//START 28454
			//	if (this.getManagedBean().isResubmitAppl()) {  #29956
					
						reqDTO.setPersonnelId(personnel.getPersonnelId());			
						PersonnelResDTO resDTO = personnelSO.retrievePersonnelById(reqDTO);
						String[] copyEquityOwner = {"equityOwner","director","clerical","higherManagement","management"};
						EntityHelper.copyEntityPropertiesOnly(personnel,resDTO.getSmPersonnel(),copyEquityOwner);
						resDTO.getSmPersonnel().setSsmData(false);
						resDTO.getSmPersonnel().setEquityOwnerSsm(false); //#29956
						resDTO.getSmPersonnel().setDirectorSsm(false); //#29956
						reqDTO.setSmPersonnel(resDTO.getSmPersonnel());
						//update latest data to active supplier admin
						resDTO = personnelSO.savePersonnelRecord(reqDTO);
						if(!resDTO.isSuccessful()){
							throw new EpException(MessageConst.ERROR_776106);
						}else{
							//close popup
						}
				//	}
					//END 28454
					
				}
				
				

		}
	}

	//SSM CHANGES #27201	
	public SmPersonnel setDesignationSSM(SmPersonnel personnel){
		//Start #27346
		if(personnel.getDesignation() != null || personnel.getSsmPosition() != null){
			String position = "";
			if(personnel.getDesignation() != null && !"".equalsIgnoreCase(personnel.getDesignation())){
				position = personnel.getDesignation();
			}else if(personnel.getSsmPosition() != null && !"".equalsIgnoreCase(personnel.getSsmPosition())){
				position = personnel.getSsmPosition();
			}
			String designation = populateDesignationSSM(position);
		//End #27346
			if(personnel.isSsmData()){
				if(personnel.isEquityOwner()){
					personnel.setDesignation(designation);
				}else if(personnel.isDirector()){
					personnel.setDesignation(designation);
				}else if(personnel.isManagement()){
					personnel.setDesignation(designation);
				}
			}
		}
		return personnel;
	}
	
	public String populateDesignationSSM(String designnationCode){
		 String designation = "";
		 switch(designnationCode.toUpperCase()){
		 case "Q":
			 designation = SmCommonUtil.getMessage("lbl.sm.register.desingnation.altdirector", null);
			 break;
		 case "D":
			 designation = SmCommonUtil.getMessage("lbl.sm.register.desingnation.director",null);
			 break;
		 case "M":
			 designation = SmCommonUtil.getMessage("lbl.sm.register.desingnation.manager", null);
			 break;
		 case "S":
			 designation = SmCommonUtil.getMessage("lbl.sm.register.desingnation.secretary", null);
			 break;
		 case "A":
			 designation = SmCommonUtil.getMessage("lbl.sm.register.desingnation.agent", null);
			 break;
		 case "P":
			 designation = SmCommonUtil.getMessage("lbl.sm.register.desingnation.subscriber", null);
			 break;
		 case "L":
			 designation = SmCommonUtil.getMessage("lbl.sm.register.desingnation.liquidator", null);
			 break;
		 case "R":
			 designation = SmCommonUtil.getMessage("lbl.sm.register.desingnation.receiver", null);
			 break;
		 case "T":
			 designation = SmCommonUtil.getMessage("lbl.sm.register.desingnation.authperson", null);
			 break;
		 default:
			 designation = designnationCode;
			 break;
		 }
		 return designation;
	 }
	//SSM CHANGES END #27201
	
	public void listenerRolePopup(SmPersonnel smPersonnelSSm){
		//27951 & 27239
		if(smPersonnelSSm.isClerical()){
			smPersonnelSSm.setHigherManagement(false);
			smPersonnelSSm.setManagement(false);
		}else{
			smPersonnelSSm.setClerical(false);
		}
	}
	
	
	//SSM #28454
		public void checkSSMValid(SmSupplier smSupplier,PostSupplierRegistrationManagedBean postRegBean){
			if(SmCommonUtil.getInstance().ssmActive() && businessTypeTovalidateWithSSM(smSupplier.getRegNo(),smSupplier.getBusinessType())){
				//checking with ssm
				SsmReqDTO ssmReqDTO = new SsmReqDTO();
				//#29582
				ssmReqDTO.setSmSupp(smSupplier);
				ssmReqDTO.setRegNo(smSupplier.getRegNo());
				ssmReqDTO.setBusinessType(smSupplier.getBusinessType());
				//27329
				ssmReqDTO.setPreRegistration(true);
				//ssmReqDTO.setCheckOnly(true);
				StringBuilder sb = new StringBuilder();
				SsmResDTO ssmResDTO = SmCommonUtil.getInstance().validateWithSSM(ssmReqDTO);
				//27214
				ssmResDTO.setPreRegistration(true);				
				if(ssmResDTO.getSeverity().equalsIgnoreCase("SUCC")){
					//check the business/company registration status in ssm
					this.getManagedBean().setStatusDescFromSSM(setStatusMessageFromSSM(ssmResDTO.getStatusFromSSM()));
					if(!ssmResDTO.getStatusFromSSM().equalsIgnoreCase("A") && !ssmResDTO.getStatusFromSSM().equalsIgnoreCase("E")){
						//27214
						throw new EpException(MessageConst.ERROR_721086);
						//throw new EpException(MessageConst.ERROR_721070,new String[]{SmCommonUtil.getMessage(this.getManagedBean().getStatusDescFromSSM(),null)});
					//START 27329
					}else if(ssmResDTO.getPersonnelIcIssues()!= null && ssmResDTO.getPersonnelIcIssues().size()>0){
						
						sb.append("<br/>");
						int i =0;
						for(SmPersonnel per:ssmResDTO.getPersonnelIcIssues()){
							sb.append(++i + ". ");
							sb.append(ResourceBundleUtils.getMessage("lbl.sm.ssm.myid.detail",new String []{per.getName(),per.getIdentificationNo()},ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
							//sb.append(",  ");
							sb.append("<br/>");
						}
						this.getManagedBean().setStatusDescFromSSM(sb.toString());
						throw new EpException(MessageConst.ERROR_721085,new String[] {sb.toString() });
					// #29582-start
					}else if (ssmResDTO.getSmPersonnelAll() != null && ssmResDTO.getSmPersonnelAll().size() > 0) {
						log.info("ssm status: " + ssmResDTO.getStatusCode());
						boolean errMsg = false;
						int i = 0;
						for (SmPersonnel per : ssmResDTO.getSmPersonnelAll()) {
							if (per.getIdentificationTypeSsm() != null) {
								String ssmTypeDesc = ParameterHelper.getInstance().getParameterDesc(
										ParameterConst.IDENTIFICATION_TYPES_SSM, per.getIdentificationTypeSsm());
								if (ssmTypeDesc == null){
									ssmTypeDesc =ParameterHelper.getInstance().getParameterDesc(
											ParameterConst.IDENTIFICATION_TYPES_SSM, "NA");
								}
								sb.append("<br/>");
								sb.append(++i + ". ");
								sb.append(per.getName() + " ");
								sb.append("("
										+ ResourceBundleUtils.getMessage("lbl.sm.register.identification.no.ssm",
												ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())
										+ " ");
								sb.append(per.getIdentificationNo() + ", ");
								sb.append(ResourceBundleUtils.getMessage("lbl.sm.register.identification.type.ssm",
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()) + " ");
								sb.append(per.getIdentificationTypeSsm() + " = ");
								sb.append(ssmTypeDesc + ")");
								sb.append("<br/>");
								errMsg = true;
							}
						}
						if (errMsg) {
							this.getManagedBean().setStatusDescFromSSM(sb.toString());
							throw new EpException(MessageConst.ERROR_721088, new String[] { sb.toString() });
						}
						
					// #29582- end
					}else if(postRegBean.isResubmitAppl() && !this.getManagedBean().isCheckRoleSSM()){
						//START NEW CHANGES POPUP OWNER UPDATE ROLE		
						SmAppl appl = SmApplicationUtil.getLatesRejectedApplicationBySuppIdAndAppType(postRegBean.getSmSupplier().getSupplierId(), "N");
						if(appl == null){
							return;
						}
						List<SmPersonnel> personnelsRole = postRegBean.getPersonnelsRole();
						if(personnelsRole != null && personnelsRole.size() <=0){
							for(SmPersonnel personnel :  appl.getSmPersonnels()){
								if (personnel.isEquityOwner() || personnel.isDirector()) {
									if(personnel.getEpRole() != null){
										SmPersonnel personnelSSM = (SmPersonnel) EntityHelper.getEntityBySinglePropertyFromList(personnel.getIdentificationNo(),ssmResDTO.getSmPersonnelOwners(),"identificationNo");
										SmPersonnel personnelDirectorSSM = (SmPersonnel) EntityHelper.getEntityBySinglePropertyFromList(personnel.getIdentificationNo(),ssmResDTO.getSmPersonnelDirector(),"identificationNo");
										if(personnelDirectorSSM == null && personnelSSM == null){
											personnelsRole.add(personnel);
										}
									}
								}
							}
							
							if(personnelsRole.size() > 0) {
								postRegBean.setDontGetSsmData(false);
								postRegBean.setSmSupplierSsm(postRegBean.getSmSupplier());	
								postRegBean.setShowPopup(true);
								postRegBean.setPersonnelsRole(personnelsRole);
								//RequestContext.getCurrentInstance().execute("ownerCheckingWidgetVar.show()");
								//return SmApplicationUtil.setStringNavigationForApplTnCPage(applicationType);								
							}
						}else{
							postRegBean.setShowPopup(false);
							//set latest role to personnel
							/*for(SmPersonnel personnel :  personnelsRole){
								SmPersonnel personnelActive = (SmPersonnel) EntityHelper.getEntityBySinglePropertyFromList(personnel.getIdentificationNo(), postRegBean.getSmAppl().getSmPersonnels(), "identificationNo");
								if(personnelActive != null){
									//copy latest data to supplier admin
									String[] copyEquityOwner = {"equityOwner","director","clerical","higherManagement","management","downgradeRoleSsm"};
									EntityHelper.copyEntityPropertiesOnly(personnel,personnelActive,copyEquityOwner);
								}
							}*/
						}
					}
					//END 27329
					
					//START #29956
					if(postRegBean.isResubmitAppl()){
						SmAppl smAppl = SmCommonUtil.getLatestRejectedApplBySupplier(smSupplier);
						displayPopupDowngradeSSMOwner(smAppl, ssmResDTO);
					} else {
						displayPopupDowngradeSSMOwner(postRegBean.getSmSupplier().getSmAppl(), ssmResDTO);
					}
					//END #29956
				} 
			else{					
					SmCommonUtil.ssmError(ssmResDTO);
				}
			}
		}
		//SSM #27264
		
	
	/**
	 * Check if Status less than or equals from status.
	 * 
	 * @param currentStatusId
	 * 		the current status id
	 * @param toStatusId
	 * 		the to status id
	 * @param orderedStatusArr
	 * 		ordered status array
	 * @return	the indicator qt status less than from status.
	 */
	public boolean isStatusLessThanOrEquals(Long currentStatusId, Long toStatusId, Long[] orderedStatusArr) {
		List<Long> orderedStatuses = Arrays.asList(orderedStatusArr);
		if(orderedStatuses.indexOf(currentStatusId)<0) return false;
		if(orderedStatuses.indexOf(toStatusId)<0) return false;
		return orderedStatuses.indexOf(currentStatusId) <= orderedStatuses.indexOf(toStatusId);
	}
	
	
	
	/** #25638 - start **/
	/**
	 * Check if Status less than or equals from status.
	 * 
	 * @param currentStatusId
	 * 		the current status id
	 * @param toStatusId
	 * 		the to status id
	 * @param orderedStatusArr
	 * 		ordered status array
	 * @return	the indicator qt status less than from status.
	 * 
	 * @since #25638
	 */
	public boolean isStatusGreaterThanOrEquals(Long currentStatusId, Long fromStatusId, Long[] orderedStatusArr) {
		List<Long> orderedStatuses = Arrays.asList(orderedStatusArr);
		if(orderedStatuses.indexOf(currentStatusId)<0) return false;
		if(orderedStatuses.indexOf(fromStatusId)<0) return false;
		return orderedStatuses.indexOf(currentStatusId) >= orderedStatuses.indexOf(fromStatusId);
	}	
	/** #25638 - end **/
	
	
	
	/*** 25692#95 - start ***/
	public String getTempManagedBeanName(){
		return managedBeanName+"_Temp";
	}
	
	/**
	 * set reference of managedBean to null. Not removing managedBean from session.
	 */
	public void nullifyManagedBean(){
		this.managedBean = null;
	}
	
	protected String getManagedBeanName(){
		return managedBeanName;
	}
	
	/*** 25692#95 - end ***/

	// #27398 11/03/2020 START
	public Boolean specialDescriptionMandatory(List<SmSupplierCategory> listCategory){
		Boolean mandatory = false;
		for(SmSupplierCategory smSupplierCategory : listCategory){
			if(smSupplierCategory.getPmCategoryL3() != null && smSupplierCategory.getPmCategoryL3().getOtherDescReq() != null && smSupplierCategory.getPmCategoryL3().getOtherDescReq() == 1){
				mandatory = true;
				break;
			}
		}
		return mandatory;
	}
	// #27398 11/03/2020 END
	//START 27239
	public void resetRoleValue() {
		if(this.getManagedBean().getPersonnelsRole() != null){
			this.getManagedBean().getPersonnelsRole().clear();
		}
		
	}
	//END 27239
	//25792 - start
	public String getSAPVendorCode(String epNo, Long branchCode) {

		IVendorCodeSO vendorCodeSO = (IVendorCodeSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
						IVendorCodeSO.class);

		SmSapVendorCode smSAPVendorCode = vendorCodeSO.retrieveSapVendorCodeByEpNoAndBranchCode(epNo, branchCode);
		if(smSAPVendorCode != null) {
			return smSAPVendorCode.getSapVendorCode();
		}
		return "";
	}
	//25792 - end
	
	//27346 - start 
	//FIXME : 16052019 - SSM INTEGRATION CR - start
	public void redirectToViewSSMInfo(String regNo,String businesstype) {
		log.info("redirectToViewSSMInfo---------------------------------------------------Start");
		clearPersonnelList();
		if (this.getManagedBean().getSmAppl() != null && !this.getManagedBean().getSmAppl().getApplType()
				.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF)
				&& !this.getManagedBean().getSmAppl().getApplType()
						.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_CANCEL)
				&& !this.getManagedBean().getSmAppl().getApplType()
						.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_RENEW)) {
			checkEquityOwnerUpdateAllowance(getManagedBean());
		}
		
		SsmReqDTO ssmReqDTO = new SsmReqDTO();
		ssmReqDTO.setRegNo(regNo);
		ssmReqDTO.setBusinessType(businesstype);
		/*if(this.getManagedBean().getSmAppl() != null && SupplierManagementConst.ACC_BASIC.equalsIgnoreCase(this.getManagedBean().getSmAppl().getApplType())){
			ssmReqDTO.setBasicAcc(true);
		}else{
			ssmReqDTO.setBasicAcc(false);
		}*/
		ssmReqDTO.setFromViewSSMInfo(true);
		SsmResDTO ssmResDTO = SmCommonUtil.getInstance().validateWithSSM(ssmReqDTO);
		this.getManagedBean().setCompanyValidWithSSM(false);
		this.getManagedBean().setValidateWithSSM(true);
		if(ssmResDTO.getSeverity().equalsIgnoreCase("SUCC")){
			this.getManagedBean().setCompanyValidWithSSM(true);
			this.getManagedBean().setSmSupplierSsm(new SmSupplier());
			//#24819 - START
			if(this.getManagedBean().getSmSupplier() == null){
				this.getManagedBean().setSmSupplier(new SmSupplier());
				this.getManagedBean().getSmSupplier().setSsmNewRegNo(ssmResDTO.getSsmNewRegNo());
			}
			//#24819 - END
			/*this.getManagedBean().setBusinessTypeParamCode(getManagedBean().getSmAppl().getSupplierType()
					.equals("J") ? ParameterTypeConst.SUPPLIER_BUSINESS_TYPE_CONSULTANT
					: ParameterTypeConst.SUPPLIER_BUSINESS_TYPE);*/
			this.getManagedBean().getSmSupplierSsm().setCompanyName(ssmResDTO.getSmCompanyBasic().getCompanyName());
			this.getManagedBean().getSmSupplierSsm().setRegNo(regNo);
			this.getManagedBean().getSmSupplierSsm().setBusinessType(businesstype);
			this.getManagedBean().setSmCompanyBasicSsm(ssmResDTO.getSmCompanyBasic());
			this.getManagedBean().setSmAddress(ssmResDTO.getSmAddress());
			this.getManagedBean().setShowDetailSSM(true);
			// set owner n director n personnel
			for(SmPersonnel personnel:ssmResDTO.getSmPersonnelAll()){
				personnel = setDesignationSSM(personnel);
				if(personnel.isEquityOwner()){
					this.getManagedBean().getPersonnelsOwnerSsm().add(personnel);
				}
				if(personnel.isDirector()){
					this.getManagedBean().getSmPersonnelDirectorSsm().add(personnel);
				}	
				if(personnel.isManagement() || personnel.isDirector()){
					this.getManagedBean().getPersonnelsSsm().add(personnel);
				}
			}
			// set business nature
			if(ssmResDTO.getSmSupplierBusinessList() != null){
				this.getManagedBean().setSmSupplierBusinessSsm(new ArrayList<SmSupplierBusiness>());
				for(SmSupplierBusiness business:ssmResDTO.getSmSupplierBusinessList()){
					if(business.isSsmData())
						this.getManagedBean().getSmSupplierBusinessSsm().add(business);
				}
			}
			//Set Branch
			if(ssmResDTO.getSmSupplierBranches() != null){
				this.getManagedBean().setSmSupplierBranchSsm(new ArrayList<SmSupplierBranch>());
				for(SmSupplierBranch branch:ssmResDTO.getSmSupplierBranches()){
					if(branch.isSsmData())
						this.getManagedBean().getSmSupplierBranchSsm().add(branch);
				}
			}
			//set equity company
			if(ssmResDTO.getSmEquityCompanys() != null){
				this.getManagedBean().setSmEquityCompaniesSsm(new ArrayList<SmEquityCompany>());
				for(SmEquityCompany company:ssmResDTO.getSmEquityCompanys()){
					if(company.isSsmData())
						this.getManagedBean().getSmEquityCompaniesSsm().add(company);
				}
			}
		}
		//Date date1 =DateUtils.convertStringToDate("yyyy-MM-dd",this.getManagedBean().getSmCompanyBasicSsm().getEstablishDate());
		//Date date2 =DateUtils.convertStringToDate("yyyy-MM-dd",this.getManagedBean().getSmCompanyBasicSsm().getRobExpiryDate());
			
		/*if(showOld){
			for(SmPersonnel personnel:getManagedBean().getSmAppl().getSmPersonnels()){
				if(personnel.isSsmData()){
					if(personnel.isEquityOwner()){
						this.getManagedBean().getPersonnelsOwner().add(personnel);
					}
					if(personnel.isDirector()){
						this.getManagedBean().getSmPersonnelDirector().add(personnel);
					}
					this.getManagedBean().getPersonnelsSsm().add(personnel);
				}				
			}			
		}*/
		/*MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setApplNo(getManagedBean().getSmAppl().getApplNo());
		MofRegistrationResDTO resDTO =  SmServiceLookup.getMofRegistrationSO().retrieveSmApplSSM(reqDTO);
		if(resDTO.getSmAppl().getSmSupplierBranches() != null){
			this.getManagedBean().getSmSupplierBranches().clear();
			for(SmSupplierBranch branch:resDTO.getSmAppl().getSmSupplierBranches()){
				if(branch.isSsmData())
					this.getManagedBean().getSmSupplierBranches().add(branch);
			}
		}
		if(resDTO.getSmAppl().getSmSupplier().getSmSupplierBusinesses() != null){
			this.getManagedBean().getSmSupplierBusinessSsm().clear();
			for(SmSupplierBusiness business:resDTO.getSmAppl().getSmSupplier().getSmSupplierBusinesses()){
				if(business.isSsmData())
					this.getManagedBean().getSmSupplierBusinessSsm().add(business);
			}
		}
		if(resDTO.getSmAppl().getSmEquityCompanies() != null){
			this.getManagedBean().getSmEquityCompany().clear();
			for(SmEquityCompany company:resDTO.getSmAppl().getSmEquityCompanies()){
				if(company.isSsmData())
					this.getManagedBean().getSmEquityCompany().add(company);
			}
		}*/
		//#27165 
		RequestContext.getCurrentInstance().execute("viewssminfoWidgetVar.show()");
		
	}
	
	//#31269 - start
	/**
	 * View SSM information
	 * @param smAppl
	 */
	public void redirectToViewSSMInfo(SmAppl smAppl){
		getManagedBean().setSmAppl(smAppl);
		getManagedBean().setSmSupplier(smAppl.getSmSupplier());
		getManagedBean().setBusinessTypeParamCode(
			smAppl.getSupplierType().equals(SupplierManagementConst.STYPE_CONTRACTOR) 
			? ParameterTypeConst.SUPPLIER_BUSINESS_TYPE	: ParameterTypeConst.SUPPLIER_BUSINESS_TYPE_CONSULTANT);
		clearPersonnelList();
		for(SmPersonnel personnel : getManagedBean().getSmAppl().getSmPersonnels()){
			if(!personnel.isSsmData()) {
				continue;
			}
			
			validateSsmPersonnel(personnel);
			if(personnel.isEquityOwnerSsm()){
				getManagedBean().getPersonnelsOwnerSsm().add(personnel);
			}
			if(personnel.isDirectorSsm()){
				getManagedBean().getSmPersonnelDirectorSsm().add(personnel);
			}
			getManagedBean().getPersonnelsSsm().add(personnel);
		}
		
		getManagedBean().getSmSupplierBranchSsm().clear();
		for(SmSupplierBranch branch : getManagedBean().getSmAppl().getSmSupplierBranches()){
			if(!branch.isSsmData()) {
				continue;
			}
			getManagedBean().getSmSupplierBranchSsm().add(branch);
		}
		
		SupplierBusinessNatureReqDTO reqDTO = new SupplierBusinessNatureReqDTO();
		reqDTO.setSupplierId(getManagedBean().getSmSupplier().getSupplierId());		
		SupplierBusinessNatureResDTO resDTO = BusinessDelegate
				.getInstance().getServiceObject("sm", ISupplierBusinessNatureSO.class)
				.retrieveAllBusinessNatureBySupplierId(reqDTO);
		for(SmSupplierBusiness business : resDTO.getBusinessNatureLs()){
			if(!business.isSsmData()) {
				continue;
			}
			getManagedBean().getSmSupplierBusinessSsm().add(business);
		}
		
		getManagedBean().getSmEquityCompaniesSsm().clear();
		for(SmEquityCompany company : getManagedBean().getSmAppl().getSmEquityCompanies()){
			if(!company.isSsmData()) {
				continue;
			}
			getManagedBean().getSmEquityCompaniesSsm().add(company);
		}
		RequestContext.getCurrentInstance().execute("viewssminfoDlg.show()");
	}
	
	/**
	 * Check to render button view SSM info
	 * @return
	 */
	public boolean renderViewSSMInfo() {
		return getManagedBean().getSmAppl().isApplValidWithSsm() && businessTypeTovalidateWithSSM(null, getManagedBean().getSmAppl().getSmSupplier().getBusinessType());
	}
	
	/**
	 * Validate SSM personnel address
	 * @param personnel
	 * @return
	 */
	public SmPersonnel validateSsmPersonnel(SmPersonnel personnel){
		if(personnel == null || !personnel.isSsmData() 
			|| personnel.getSmPersonnelAddresses() == null) return personnel;
		for(int i=0; i < personnel.getSmPersonnelAddresses().size(); i++){
			if(personnel.getSmPersonnelAddresses().get(i) != null
					&& personnel.getSmPersonnelAddresses().get(i).getSmAddress() != null
					&& personnel.getSmPersonnelAddresses().get(i).getSmAddress().isSsmData()){
				Collections.swap(personnel.getSmPersonnelAddresses(), 0, i);
				break;
			}
		}
		return personnel;
	}
	//#31269 - end
	
	public void clearPersonnelList() {
		this.getManagedBean().getPersonnelsOwnerSsm().clear();
		this.getManagedBean().getSmPersonnelDirectorSsm().clear();
		this.getManagedBean().getSmSupplierBranchSsm().clear();
		this.getManagedBean().getSmSupplierBusinessSsm().clear();
		this.getManagedBean().getPersonnelsSsm().clear();
	}
	
	public static boolean isMofAccEffDatePassedCurrDate(SmSupplier supplier) {
		boolean retVal = false;
		if (supplier != null && supplier.getSupplierId() != null) {
			MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
			reqDTO.setSupplierId(supplier.getSupplierId());
			reqDTO.setParamMonths(6);
			int count = SmServiceLookup
					.getMofRegistrationSO()
					.countMofAccEffDatePassedCurrDateByMonthsAndSupplierId(
							reqDTO).getCount();
			if (count > 0) {
				retVal = true;
			}
		}
		return retVal;
	}

	public static void checkEquityOwnerUpdateAllowance(BaseManagedBean managedBean) {
		// #27165
		managedBean.setDisAllowOwnerEdit(false);
		if (isMofAccEffDatePassedCurrDate(managedBean.getSmSupplier())) {
			managedBean.setDisAllowOwnerEdit(true);
		}
		// managedBean.setDisAllowOwnerEdit(false);
	}
	
	//27346- end
	
	
	//27346 START PRINT REPORT
	/**
	 * download site visit template report in PDF
	 * @param templateType
	 * @throws Exception
	 */
	public void downloadViewSSMInfo() throws Exception{
		printReportVM("ssm","SM_SSMInfo.pdf","/sm/ssm/ssm_info.vm",setDetail());
	}
	
	public VelocityContext setDetail(){
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		VelocityContext context = new VelocityContext();
		FacesScopes face = new FacesScopes();		
		
		context.put("hostUrl", getLogoHeaderPath());
		context.put("title", ResourceBundleUtils.getMessage("lbl.sm.view.ssm.infomation",ResourceBundleUtils.BUNDLE_COMMON_LABELS,face.getUserLocale()));
		context.put("companyInfoSec", ResourceBundleUtils.getMessage("lbl.sm.comp.info.hdr",ResourceBundleUtils.BUNDLE_COMMON_LABELS,face.getUserLocale()));
		context.put("capitalEquityInfolbl", ResourceBundleUtils.getMessage("lbl.sm.equiInfo.panHdr",ResourceBundleUtils.BUNDLE_COMMON_LABELS,face.getUserLocale()));
		context.put("branchInfolbl", ResourceBundleUtils.getMessage("lbl.sm.viewSsm.branch.details",ResourceBundleUtils.BUNDLE_COMMON_LABELS,face.getUserLocale()));
		context.put("ownerInfolbl", ResourceBundleUtils.getMessage("lbl.sm.viewSsm.equiOwner",ResourceBundleUtils.BUNDLE_COMMON_LABELS,face.getUserLocale()));
		context.put("directorInfolbl", ResourceBundleUtils.getMessage("lbl.sm.comp.director.info.hdr",ResourceBundleUtils.BUNDLE_COMMON_LABELS,face.getUserLocale()));
		context.put("capitalEquityCompanieslbl", ResourceBundleUtils.getMessage("lbl.sm.viewSsm.equiCompany",ResourceBundleUtils.BUNDLE_COMMON_LABELS,face.getUserLocale()));
		context.put("personnelInfolbl", ResourceBundleUtils.getMessage("lbl.sm.viewSsm.personnel",ResourceBundleUtils.BUNDLE_COMMON_LABELS,face.getUserLocale()));
		context.put("businessNaturelbl", ResourceBundleUtils.getMessage("lbl.sm.nature.of.biz.hdr",ResourceBundleUtils.BUNDLE_COMMON_LABELS,face.getUserLocale()));
		context.put("companyInfo", this.getManagedBean().getSmSupplierSsm());
		context.put("companyBasic", this.getManagedBean().getSmCompanyBasicSsm());
		context.put("ssmNewRegNo", this.getManagedBean().getSmSupplier().getSsmNewRegNo());	//#24819
		context.put("smAddress", this.getManagedBean().getSmAddress());
		context.put("ownerInfo", this.getManagedBean().getPersonnelsOwnerSsm());
		context.put("directorInfo", this.getManagedBean().getSmPersonnelDirectorSsm());
		context.put("businessNature", this.getManagedBean().getSmSupplierBusinessSsm());
		context.put("branchInfo",this.getManagedBean().getSmSupplierBranchSsm());
		context.put("equityCompanies",this.getManagedBean().getSmEquityCompaniesSsm());
		context.put("personnelInfo",this.getManagedBean().getPersonnelsSsm());
		context.put("businessNatureCode", this.getManagedBean().getSmSupplierBusinessSsm());
		context.put("smVmUtil", VmUtil.getInstance());
		context.put("establishdate", sdf.format(this.getManagedBean().getSmCompanyBasicSsm().getEstablishDate()));
		if(this.getManagedBean().getSmSupplierSsm().getBusinessType().equalsIgnoreCase(SupplierManagementConst.BUSINESS_TYPE_ROB_SOLE_PROPRIETORSHIP)|| this.getManagedBean().getSmSupplierSsm().getBusinessType().equalsIgnoreCase(SupplierManagementConst.BUSINESS_TYPE_ROB_PARTNERSHIP)){
			context.put("robexpdate", sdf.format(this.getManagedBean().getSmCompanyBasicSsm().getRobExpiryDate()));
		}
		return context;
	}
	
	public void printReportVM(String tmpFolder, String fileName, String vmFile, VelocityContext context) throws IOException {
		String filePath = null;
		filePath = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);

		filePath = filePath + "/" + tmpFolder;

		File file = new File(filePath);
		if (!file.exists()) {
			file.mkdirs();
		}
			
		AttachmentFormat attachmentFormat = null;		
		OutputStream os1 = null;
		File finalOutputFile = null;
		try {
			attachmentFormat = new AttachmentDateFormat();
			String newName = attachmentFormat.format(fileName, null);
			filePath = filePath + "/" + newName;
			finalOutputFile = new File(filePath);
			
			os1 = new FileOutputStream(filePath);
			
			DocumentGenerator.getInstance().generatePdfFromVelocity(vmFile, context, os1);
			os1.flush();
			
			this.getManagedBean().setDownloadfile(commonDownload(tmpFolder + "/" + newName, fileName));
			finalOutputFile.delete();
		} catch (Exception e) {
			log.error("File: BaseBackingBean, printReportVM, Message: " + e.getMessage());
		} finally {
			if (os1 != null){
				os1.close();
			}
		}
	}
	/**
	 * get the template logo path 
	 * @return String
	 */
	private String getLogoHeaderPath(){
		return SmCommonUtil.getBasePortalUrl();
	}
	
	//27346 END PRINT REPORT

	
	//27346 - Start
   /* comment for design issue #27346
	private String getLogoHeaderPath(){
		return SmCommonUtil.getBasePortalUrl();
	}
	*/
	
	

	 /* comment for design issue #27346
	private String generateToPdf(String vm, VelocityContext context, String fileName) throws Exception{
		String filePath = tmpPath + fileName;
		
		File tempFile1 = new File(tmpPath +  fileName);
		OutputStream os1 = new FileOutputStream(tempFile1);
		DocumentGenerator.getInstance().generatePdfFromVelocity(vm, context, os1);
		
		os1.flush();
		os1.close();
		
		return filePath;
	}*/
	 
	 /* comment for design issue #27346
	public static void doMerge(List<InputStream> list, OutputStream outputStream)
            throws DocumentException, IOException {
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        document.open();
        PdfContentByte cb = writer.getDirectContent();
         
        for (InputStream in : list) {
            PdfReader reader = new PdfReader(in);
            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                document.newPage();
                //import the page from source pdf
                PdfImportedPage page = writer.getImportedPage(reader, i);
                //add the page to the destination pdf
                cb.addTemplate(page, 0, 0);
            }
            in.close();
        }
        outputStream.flush();
        document.close();
        outputStream.close();
    }	
	
	public void downloadViewSSMInfo() throws Exception{
        
		tmpPath = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);
		
		tmpPath = tmpPath + "/" + tmpFolder;
		
		File dir = new File(tmpPath);
		if(!dir.exists()) dir.mkdirs();
		
		fileInitName = String.valueOf(new Date().getTime());
		//fileInitName = getManagedBean().getSmAppl().getApplId().toString();
		
		// combine all pdf into 1
		String compInfoEquity = compInfoEquity();
		String capitalEquityCompany = CapitalEquityCompany();
		String personnelList = PersonnalList();
		String businessNature = BusinessNature();
		String branch = Branch();
		
		
		File ssmCompInfoEquity = new File(compInfoEquity);
		File ssmCapitalEquityCompany = new File(capitalEquityCompany);
		File ssmPersonnelList = new File(personnelList);
		File ssmBusinessNature = new File(businessNature);
		File ssmBranch = new File(branch);
		

		List<InputStream> mergeList = new ArrayList<InputStream>();  
		FileInputStream fCompInfoEquity  = new FileInputStream(ssmCompInfoEquity);
		FileInputStream fCapitalEquityCompany = new FileInputStream(ssmCapitalEquityCompany);
		FileInputStream fPersonnelList = new FileInputStream(ssmPersonnelList);
		FileInputStream fBusinessNature = new FileInputStream(ssmBusinessNature);
		FileInputStream fBranch = new FileInputStream(ssmBranch);
		
		
		mergeList.add(fCompInfoEquity);
		if(this.getManagedBean().getSmSupplierSsm().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_BHD) || this.getManagedBean().getSmSupplierSsm().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_SDN_BHD)){
		mergeList.add(fCapitalEquityCompany);
		mergeList.add(fPersonnelList);
		}
		mergeList.add(fBusinessNature);
		if(this.getManagedBean().getSmSupplierSsm().getBusinessType().equalsIgnoreCase(SupplierManagementConst.BUSINESS_TYPE_ROB_SOLE_PROPRIETORSHIP)|| this.getManagedBean().getSmSupplierSsm().getBusinessType().equalsIgnoreCase(SupplierManagementConst.BUSINESS_TYPE_ROB_PARTNERSHIP)){
		mergeList.add(fBranch);
		}
		
		String fileName =  "SM_SSMInfo.pdf";
        String latestFile = tmpPath + fileName;
        
        
        OutputStream combined = new FileOutputStream(new File(latestFile));
        doMerge(mergeList, combined);
        combined.flush();
        combined.close();
        
        File finalOutputFile = new File(latestFile);
        
        ssmCompInfoEquity.delete();
        ssmCapitalEquityCompany.delete();
        ssmPersonnelList.delete();
        ssmBusinessNature.delete();
        ssmBusinessNature.delete();
        ssmBranch.delete();
        
        try {
        	file = commonDownload(tmpFolder+fileName, fileName);
        	
        } catch (IOException e) {
        	log.error("File: SvoSiteVisitBackingBean, Method:downloadSiteVisitTemplate, Message: "+e.getMessage());
        }finally{
        	fCompInfoEquity.close();
        	fCapitalEquityCompany.close();
        	fPersonnelList.close();
        	fBusinessNature.close();
        	fBranch.close();
    		
        }
        
        if(finalOutputFile.delete()){
        	 log.info("**********Deleted***********");
        }else{
        	 log.info("**********Failed to Delete***********");
        }
	}
	*/
	
	/* comment for design issue #27346
	private String compInfoEquity() throws Exception{
		VelocityContext context = new VelocityContext();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		FacesScopes face = new FacesScopes();

		context.put("hostUrl", getLogoHeaderPath());
		context.put("title", ResourceBundleUtils.getMessage("lbl.sm.view.ssm.infomation",ResourceBundleUtils.BUNDLE_COMMON_LABELS,face.getUserLocale()));
		context.put("companyInfoSec", ResourceBundleUtils.getMessage("lbl.sm.comp.info.hdr",ResourceBundleUtils.BUNDLE_COMMON_LABELS,face.getUserLocale()));
		context.put("capitalEquityInfolbl", ResourceBundleUtils.getMessage("lbl.sm.equiInfo.panHdr",ResourceBundleUtils.BUNDLE_COMMON_LABELS,face.getUserLocale()));
		context.put("ownerInfolbl", ResourceBundleUtils.getMessage("lbl.sm.viewSsm.equiOwner",ResourceBundleUtils.BUNDLE_COMMON_LABELS,face.getUserLocale()));
		context.put("companyInfo", this.getManagedBean().getSmSupplierSsm());
		context.put("companyBasic", this.getManagedBean().getSmCompanyBasicSsm());
		context.put("smAddress", this.getManagedBean().getSmAddress());
		context.put("ownerInfo", this.getManagedBean().getPersonnelsOwnerSsm());
		context.put("establishdate", sdf.format(this.getManagedBean().getSmCompanyBasicSsm().getEstablishDate()));
		if(this.getManagedBean().getSmSupplierSsm().getBusinessType().equalsIgnoreCase(SupplierManagementConst.BUSINESS_TYPE_ROB_SOLE_PROPRIETORSHIP)|| this.getManagedBean().getSmSupplierSsm().getBusinessType().equalsIgnoreCase(SupplierManagementConst.BUSINESS_TYPE_ROB_PARTNERSHIP)){
		context.put("robexpdate", sdf.format(this.getManagedBean().getSmCompanyBasicSsm().getRobExpiryDate()));
		}
		context.put("smVmUtil", VmUtil.getInstance());
		
	
		String vmCover = "/sm/ssm/ssm_companyinfo.vm";
		String fileName = fileInitName + "companyinfo.pdf";
		
		return generateToPdf(vmCover, context, fileName);
	}
	*/
	/* comment for design issue #27346
	private String CapitalEquityCompany() throws Exception{
		VelocityContext context = new VelocityContext();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		FacesScopes face = new FacesScopes();
		
		context.put("hostUrl", getLogoHeaderPath());
		context.put("title", ResourceBundleUtils.getMessage("lbl.sm.view.ssm.infomation",ResourceBundleUtils.BUNDLE_COMMON_LABELS,face.getUserLocale()));
		context.put("companyInfo", this.getManagedBean().getSmSupplierSsm());
		context.put("smVmUtil", VmUtil.getInstance());
		context.put("capitalEquityCompanieslbl", ResourceBundleUtils.getMessage("lbl.sm.viewSsm.equiCompany",ResourceBundleUtils.BUNDLE_COMMON_LABELS,face.getUserLocale()));
		context.put("equityCompanies",this.getManagedBean().getSmEquityCompaniesSsm());
		
	
		String vmCover = "/sm/ssm/ssm_capitalequityheldbycompany.vm";
		String fileName = fileInitName + "capitalequityheldbycompany.pdf";
		
		return generateToPdf(vmCover, context, fileName);
	}
	*/
	
	/* comment for design issue #27346
	private String PersonnalList() throws Exception{
		VelocityContext context = new VelocityContext();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		FacesScopes face = new FacesScopes();
		
		context.put("hostUrl", getLogoHeaderPath());
		context.put("title", ResourceBundleUtils.getMessage("lbl.sm.view.ssm.infomation",ResourceBundleUtils.BUNDLE_COMMON_LABELS,face.getUserLocale()));
		context.put("personnelInfolbl", ResourceBundleUtils.getMessage("lbl.sm.viewSsm.personnel",ResourceBundleUtils.BUNDLE_COMMON_LABELS,face.getUserLocale()));
		context.put("smVmUtil", VmUtil.getInstance());
		context.put("personnelInfo",this.getManagedBean().getPersonnelsSsm());
	
		String vmCover = "/sm/ssm/ssm_personnelinformation.vm";
		String fileName = fileInitName + "personnelinformation.pdf";
		
		return generateToPdf(vmCover, context, fileName);
	}
	*/
	
	/*  comment for design issue #27346
	private String BusinessNature() throws Exception{
		VelocityContext context = new VelocityContext();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		FacesScopes face = new FacesScopes();
		
		context.put("hostUrl", getLogoHeaderPath());
		context.put("title", ResourceBundleUtils.getMessage("lbl.sm.view.ssm.infomation",ResourceBundleUtils.BUNDLE_COMMON_LABELS,face.getUserLocale()));
		context.put("businessNaturelbl", ResourceBundleUtils.getMessage("lbl.sm.nature.of.biz.hdr",ResourceBundleUtils.BUNDLE_COMMON_LABELS,face.getUserLocale()));
		context.put("businessNature", this.getManagedBean().getSmSupplierBusinessSsm());
		context.put("companyBasic", this.getManagedBean().getSmCompanyBasicSsm());
		context.put("smVmUtil", VmUtil.getInstance());
		context.put("businessNatureCode", this.getManagedBean().getSmSupplierBusinessSsm());

	
		String vmCover = "/sm/ssm/ssm_natureofbusiness.vm";
		String fileName = fileInitName + "natureofbusiness.pdf";
		
		return generateToPdf(vmCover, context, fileName);
	}
	
	private String Branch() throws Exception{
		VelocityContext context = new VelocityContext();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		FacesScopes face = new FacesScopes();
		
		context.put("hostUrl", getLogoHeaderPath());
		context.put("title", ResourceBundleUtils.getMessage("lbl.sm.view.ssm.infomation",ResourceBundleUtils.BUNDLE_COMMON_LABELS,face.getUserLocale()));
		context.put("branchInfolbl", ResourceBundleUtils.getMessage("lbl.sm.viewSsm.branch.details",ResourceBundleUtils.BUNDLE_COMMON_LABELS,face.getUserLocale()));
		context.put("branchInfo",this.getManagedBean().getSmSupplierBranchSsm());
		context.put("smVmUtil", VmUtil.getInstance());
		context.put("companyInfo", this.getManagedBean().getSmSupplierSsm());
		
		String vmCover = "/sm/ssm/ssm_branchesdetail.vm";
		String fileName = fileInitName + "branches.pdf";
		
		return generateToPdf(vmCover, context, fileName);
	}
	//27346 - End
*/
	
	
	//12492 - START
	public void CheckBankAccount(String fromPage) {

		HashSet<String> hsetAccountNo = new HashSet<String>();
		HashSet<Long> hsetAccountName = new HashSet<Long>();

		if(fromPage.equalsIgnoreCase("fromBankInfo")){
			log.info("masuk CheckBankAccount frombankinfo");
			if (this.getManagedBean().getSmAppl().getSmSupplierBanks().size() > 1) {
				for (SmSupplierBank bankName : this.getManagedBean().getSmAppl().getSmSupplierBanks()) {
					if (bankName.getPmFinancialOrg().getFinancialOrgId() != null
							&& hsetAccountName.add(bankName.getPmFinancialOrg().getFinancialOrgId()) == false) {
						for (SmSupplierBank bank : this.getManagedBean().getSmAppl().getSmSupplierBanks()) {
							if (hsetAccountNo.add(bank.getAccountNo()) == false) {
								RequestContext.getCurrentInstance().execute("bankDialogDuplicate.show()");
							}
						}
					}
				}
			}	
		}else if(fromPage.equalsIgnoreCase("fromNext")){
			RequestContext.getCurrentInstance().execute("bankDialogDuplicate.hide()");
			log.info("masuk CheckBankAccountInfoForm from next");
		}
	}
	//12492 - END
	
	//#29594 - START
	public String retrieveMinistryCodeNameByOrgProfileId(Long orgProfileId) {
		IOrgProfileSO profileSO = (IOrgProfileSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(orgProfileId);
		PmOrgProfile orgProfile = profileSO.retrieveMinistryByOrgProfileId(
				reqDTO).getPmOrgProfile();
		if (orgProfile != null) {
			return orgProfile.getOrgCode().concat(" - ").concat(orgProfile.getOrgName());
		} else {
			log.info("Ministry not found : " + orgProfileId);
			return "";
		}
	}
	
	public String retrieveJabatanCodeNameByOrgProfileId(Long orgProfileId) {
		IOrgProfileSO profileSO = (IOrgProfileSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(orgProfileId);
		OrgProfileResDTO resDTO = new OrgProfileResDTO();
		resDTO = profileSO.retrieveJabatanByOrgProfileId(reqDTO);
		String jabatan = "";
		if (resDTO.getPmOrgProfile() != null) {
			jabatan = resDTO.getPmOrgProfile().getOrgCode().concat(" - ").concat(resDTO.getPmOrgProfile().getOrgName());
		}
		return jabatan;
	}
	//#29594 - END
	
	//Enhancement 29418 -start

	/**
	 * To get Actual file path for attachement.
	 * 
	 * @param attachment
	 * @return filePath.
	 */
	protected String getActualFilePath(ScAttachment attachment) {
		String docRepoBaseDir = EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);
		StringBuilder actualFilePath = new StringBuilder(docRepoBaseDir);
		actualFilePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		actualFilePath.append(attachment.getFilePath());

		return actualFilePath.toString();
	}

	/**
	 * Generate report.
	 *
	 * @param reportFile the report file
	 * @param paramMap the param map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void generateReportPDF(String contractNo, List<String> reportFiles, Map<String, Map<String, String>> paramMaps) throws IOException {

		PortletResponse portalresponse = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse response = PortalUtil.getHttpServletResponse(portalresponse);
		response.setContentType("Content-type: text/zip");
		response.setHeader("Content-Disposition", "attachment; filename=" + contractNo + "_Amendment Update.zip");

		Map<String, String> paramMap;

		ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()));

		for (String reportFile : reportFiles) {
			String fileType = "";

			paramMap = paramMaps.get(reportFile);

			if (reportFile.equals(ReportConst.CT_AMENDMENT_UPDATE_RPT_AMENDMENT_DETAIL)) {
				fileType = "_Amendment Detail";
			} else if (reportFile.equals(ReportConst.CT_AMENDMENT_UPDATE_RPT_ZONE)) {
				fileType = "_Zone";
			} else if (reportFile.equals(ReportConst.CT_AMENDMENT_UPDATE_RPT_AGENCY)) {
				fileType = "_Agency";
			} else if (reportFile.equals(ReportConst.CT_AMENDMENT_UPDATE_RPT_ITEM_ZONAL) || reportFile.equals(ReportConst.CT_AMENDMENT_UPDATE_RPT_ITEM_NO_ZONAL)) {
				fileType = "_Item";
			} else if (reportFile.equals(ReportConst.CT_AMENDMENT_UPDATE_RPT_DEDUCTION)) {
				fileType = "_Deduction";
			} else if (reportFile.equals(ReportConst.CT_AMENDMENT_UPDATE_RPT_SCHEDULE)) {
				fileType = "_Schedule";
			}

			zos.putNextEntry(new ZipEntry(contractNo + fileType + ".pdf"));

			String reportUrl = generateReportURL(reportFile, paramMap);

			HttpURLConnection urlConnection = null;
			URL obj = new URL(reportUrl);
			urlConnection = (HttpURLConnection) obj.openConnection();

			BufferedInputStream buf = new BufferedInputStream(urlConnection.getInputStream());

			int size = 0;
			while ((size = buf.read()) != -1) {
				zos.write(size);
			}
			buf.close();
			urlConnection.disconnect();
		}
		zos.closeEntry();
		zos.close();
		response.flushBuffer();
		FacesContext.getCurrentInstance().responseComplete();
	}
	//Enhancement 29418 - end
	
	//#8411 - START
	/**
	 * Get parameter id select items by reason category, parameter type and search bean for
	 * dropdown.
	 * 
	 * @param reasonCategory
	 *            the reason category
	 * @param parameterType
	 *            the parameter type
	 * @param searchBean
	 *            the search bean instance
	 * @return array of SelectItem
	 */
	public SelectItem[] getCategoryTypeDropdownValuesBySearchBean(Integer reasonCategory, String parameterType, ISearchBean<PmParameterDesc> searchBean) {
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();
		List<PmParameterDesc> parameterDescs = ParameterHelper
				.getInstance().getParameterDescListBySearchBean(
						parameterType, searchBean);
		List<PmParameterDesc> newParameterDescs = new ArrayList<PmParameterDesc>();
		try {
			if(reasonCategory == null){
				items = new SelectItem[1];
				items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
						"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						face.getUserLocale()));
			} else {
				if(reasonCategory.equals(ParameterConst.JUSTIFICATION)){// filter for Justification category
					for (PmParameterDesc parameterDesc: parameterDescs) {
						if (!parameterDesc.getPmParameter().getParameterCode().equals(ReasonCategory.SOFTCERT_REJECTION_REASON.value()) 
				        		&& !parameterDesc.getPmParameter().getParameterCode().equals(ReasonCategory.SUPPLIER_REJECTION_REASON.value())) {
							newParameterDescs.add(parameterDesc);
				        }
				    }
				} else if(reasonCategory.equals(ParameterConst.REJECTION_REASON)){// filter for Rejection Reason category
					for (PmParameterDesc parameterDesc: parameterDescs) {
						if (parameterDesc.getPmParameter().getParameterCode().equals(ReasonCategory.SOFTCERT_REJECTION_REASON.value()) 
				        		|| parameterDesc.getPmParameter().getParameterCode().equals(ReasonCategory.SUPPLIER_REJECTION_REASON.value())) {
							newParameterDescs.add(parameterDesc);
				        }
					}
			    }
				int i = 1;
				items = new SelectItem[newParameterDescs.size() + 1];
				items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
						"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						face.getUserLocale()));
				for (PmParameterDesc parameterDesc : newParameterDescs) {
					items[i++] = new SelectItem(parameterDesc.getPmParameter()
							.getParameterId(), parameterDesc.getCodeName());
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}
		return items;
	}
	//#8411 - END
	
	//#29466 - START
	public boolean checkManufacturerCategoryCodes(ScQt scQt){
		boolean manufacturer=false;	
	  	if(null!=scQt&& !scQt.isPanel()){
	  		List<ScQtCategory> qtCategories = null;
	  		QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setQtId(scQt.getQtId());
			qtCategories = getQtDetailSO().getQtCategoryByQtId(reqDTO).getCategories();	
			if(null!=qtCategories && CollectionUtils.isNotEmpty(qtCategories)){
		  		for (ScQtCategory category : qtCategories) {
					if(SourcingConstants.MANUFACTURER_CATEGORY_CODE.equals(category.getPmCategoryL3().getCategoryL3Code())){
				  		manufacturer=true;
						break;
					}
				}
			}
	  	} else{
	  		manufacturer=false;	
	  	}
	  	return manufacturer;
	}

  	private IQTDetailSO getQtDetailSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
	}
  	//#29466 - END
  	
  	//28729
  	/**
	 * Validate allowed email type
	 * 
	 * @param String
	 *            email
	 * @return boolean
	 */
	public boolean isAllowedEmail(String email) {
		boolean allowed = true;
		if(email.toLowerCase().contains("@yahoo.com") || email.contains("@ymail.com") || email.contains("@rocketmail.com")){
			allowed = false;
		}
			
		return allowed;
	}
	
	//#31849, #34090, #32682 start
	/**
	 * Method to return current session userId from user screenName(loginId)
	 * @return userId
	 */
	public Long getUserIdSessionFromLoginId(){
		FacesScopes facesScopes = new FacesScopes();
		Long userIdSession = 1L;
		
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		UserReqDTO userReqDTO = new UserReqDTO();
		userReqDTO.setLoginId(facesScopes.getUserScreenName());
		PmUser pmUser = userSO.retrieveUserByLoginId(userReqDTO).getPmUser();
		
		if (pmUser == null) {
			userIdSession = facesScopes.getUserSessionBean().getUserId();
		} else {
			userIdSession = pmUser.getUserId();
		}
		
		new LogKey(userIdSession);
		return userIdSession;
	}
	//#31849, #34090, #32682 end
	//#34135 START
	/**
	 * Gets the full address.
	 *
	 * @param smAddress the sm address
	 * @return the full address
	 */
	public String getFullBranchAddress(SmAddress smAddress) {
		return AddressUtils.getFullBranchAddress(smAddress);
	}
	//#34135 END
	
	//29021 - start 34865,34866,34867 - 29021 WIP
	/**
	 * Evaluate if QT is a wip document. ScoreSetting less than 7 indicate wip document.
	 *
	 * @param scQt
	 * @return wip flag 
	 */
	public boolean evaluateWIP(ScQt scQt) {
		Integer maxScoreSetting = this.getManagedBean().getWipStatusMax();
		if (maxScoreSetting == null) {
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			reqDTO.setQtId(scQt.getQtId());
			IQTEvaluationSO evaluationSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			maxScoreSetting = evaluationSO.checkWipScoreSettingMax(reqDTO);
			this.getManagedBean().setWipStatusMax(maxScoreSetting);
		}
		return maxScoreSetting < SourcingConstants.SCORE_SETTING_TYPE_TECHNICAL_BY_ITEM;
	}
	//29012 - end 34865,34866,34867 - 29021 WIP
	
	// 33410 - START
	public String setFixedLanguageDisplay(Boolean isFixedDisplay, String languageCode){
		LanguageReqDTO reqDTO = new LanguageReqDTO();
		reqDTO.setLanguageCode(languageCode);
		ILanguageSO languageSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, ILanguageSO.class);
		LanguageResDTO resDTO = languageSO.retrieveLanguageByLanguageCode(reqDTO);
		if(isFixedDisplay){
			return resDTO.getLanguage().getLanguageCode();
		} else {
			return new FacesScopes().getUserLocale().getLanguage();
		}
	}
	// 33410 - END
	
	//#21387 - Start
	/**
	 * Claim bpm task group By Document Number And Module
	 * @param reqDTO
	 * @return resDTO 
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public BpmResponseDTO claimBpmTaskGroupByDocNo(BpmRequestDTO reqDTO) {
		BpmResponseDTO resDTO = new BpmResponseDTO();
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
		try {

			BpmRequestDTO bpmReqDTO = new BpmRequestDTO();
			bpmReqDTO.setDocNumber(reqDTO.getDocNumber());
			bpmReqDTO.setState(BpmConstants.STATE_ASSIGNED);
			bpmReqDTO.setModule(BpmConstants.MODULE_CT);
			bpmReqDTO.setState(BpmConstants.STATE_ASSIGNED);
			resDTO = bpmSO.getTasksByDocNoandModule(bpmReqDTO);
			resDTO.setSuccessful(true);
			return resDTO;
		} catch (Exception e) {
			resDTO.setSuccessful(false);
			log.error(e.getMessage());
			throw new EpException(e.getMessage());
		}
	}
	//#21387 - End
	
	//#28175 - start, 29380 - Site Visit Grouping Enhancement -28775
	//28175 Refactor start
	/**
	 * Returns the supplier category name based on the provided parameters.
	 * 
	 * @param catL3
	 *            the PmCategoryL3 object
	 * @param isSupplierProfile
	 *            whether it's a supplier profile
	 * @param isFullName
	 *            whether to return the full category name
	 * @return the supplier category name
	 */
	public String getSupplierCategoryName(PmCategoryL3 catL3, boolean isSupplierProfile, boolean isFullName) {
		if (catL3 == null) {
			return null;
		}

		SmAppl appl = getManagedBean().getSmAppl();
		String categoryName = getCategoryName(catL3, isFullName);

		if (appl == null || isLatestCatCodeDesc(isSupplierProfile)
				|| EntityHelper.isCollectionEmpty(appl.getSmSupplierCategories())) {
			return categoryName;
		}

		SmSupplierCategory supplierCategory = getSupplierCategory(appl, catL3);
		if (supplierCategory == null) {
			return categoryName;
		}

		return isFullName ? getFullSupplierCategoryName(supplierCategory) : supplierCategory.getCategoryL3Name();
	}

	/**
	 * Returns the category name based on the provided PmCategoryL3 object and
	 * whether to return the full name.
	 * 
	 * @param catL3
	 *            the PmCategoryL3 object
	 * @param isFullName
	 *            whether to return the full category name
	 * @return the category name
	 */
	private String getCategoryName(PmCategoryL3 catL3, boolean isFullName) {
		String categoryName = catL3.getCategoryName();
		if (isFullName) {
			categoryName = String.format("%s / %s / %s", catL3.getPmCategoryL2().getPmCategoryL1().getCategoryName(),
					catL3.getPmCategoryL2().getCategoryName(), categoryName);
		}
		return categoryName;
	}

	/**
	 * Returns the SmSupplierCategory object based on the provided SmAppl object
	 * and PmCategoryL3 object.
	 * 
	 * @param appl
	 *            the SmAppl object
	 * @param catL3
	 *            the PmCategoryL3 object
	 * @return the SmSupplierCategory object
	 */
	private SmSupplierCategory getSupplierCategory(SmAppl appl, PmCategoryL3 catL3) {
		return (SmSupplierCategory) EntityHelper.getEntityBySinglePropertyFromList(catL3.getCategoryL3Id(),
				appl.getSmSupplierCategories(), "pmCategoryL3");
	}

	/**
	 * Returns the full supplier category name based on the provided
	 * SmSupplierCategory object.
	 * 
	 * @param supplierCategory
	 *            the SmSupplierCategory object
	 * @return the full supplier category name
	 */
	private String getFullSupplierCategoryName(SmSupplierCategory supplierCategory) {
		return String.format("%s / %s / %s", supplierCategory.getCategoryL1Name(),
				supplierCategory.getCategoryL2Name(), supplierCategory.getCategoryL3Name());
	}
	//28175 Refactor end
	/**
	 * @param cat
	 * @param appl
	 * @return latest or old category code description
	 */
	public String getSupplierCategoryName(SmSupplierCategory cat, SmAppl appl){
		String catName1 = cat.getPmCategoryL1().getCategoryName();
		String catName2 = cat.getPmCategoryL2().getCategoryName();
		String catName3 = cat.getPmCategoryL3().getCategoryName();
		return isLatestCatCodeDesc(false, appl) ?  
				String.format("%s / %s / %s",catName1,catName2,catName3)
				: getFullSupplierCategoryName(cat);
	}
	
	/**
	 * @param isSupplierProfile
	 * @return
	 */
	public boolean isLatestCatCodeDesc(boolean isSupplierProfile){
		return isLatestCatCodeDesc(isSupplierProfile, getManagedBean().getSmAppl());
	}
	
	/**
	 * @param isSupplierProfile
	 * @param appl
	 * @return
	 */
	public boolean isLatestCatCodeDesc(boolean isSupplierProfile, SmAppl appl){
		if(appl == null){
			return false;
		}
		if (appl.getPmStatus() == null) {
			return isSupplierProfile || appl.isActiveAppl();
		}
		Long status = appl.getPmStatus().getStatusId();
		boolean isRegFee = status.equals(SupplierManagementConst.PM_STATUS_PENDING_PAYMENT)
				|| (status.equals(SupplierManagementConst.PM_STAT_PENDING_PAYMENT) && appl.getPmRegStatus() != null);
		return isSupplierProfile || appl.isActiveAppl() && !isRegFee;
	}
	//#28175, 29380 - Site Visit Grouping Enhancement -28775 - end
	
	//START #29956
	/**
	 * This method manages the popup and role downgrades based on the application and personnel details.
	 * It clears roles, adjusts flags, and triggers UI changes as needed. It also includes logging for TDD validation.
	 *
	 * @param smAppl The application object
	 * @param ssmResDTO The personnel and role data
	 * @return boolean indicating whether the downgrade role is set
	 */
	public boolean displayPopupDowngradeSSMOwner(SmAppl smAppl, SsmResDTO ssmResDTO) {
	    LoggerUtils logger = LoggerUtils.getInstance();
	    // Test Case 1: Clear roles and flags
	    logger.logCodeCoverage("Test Case 1: Start clearing roles and flags");
	    getManagedBean().getPersonnelsRole().clear();
	    getManagedBean().setDowngradeRole(false);
	    getManagedBean().setDisAllowOwnerEdit(false);
	    logger.logCodeCoverage("Test Case 1: Roles and flags cleared");
	    
	    SmSupplier supplier = SmApplicationUtil.getCurrentSupplier();
	    Boolean isMofAccEffDatePassed6MonthCurrDate = SmApplicationUtil.isMofAccEffDatePassedCurrDate(supplier);

	    String applType = smAppl.getApplType();
	    Boolean disallowEditApplType = applType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_UPDATE)
	    		|| applType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES)
	    		|| applType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_BUMI);
	    
	    PersonnelReqDTO reqDto = new PersonnelReqDTO();
	    reqDto.setApplId(getManagedBean().isResubmitAppl() ? smAppl.getApplId() : supplier.getSmAppl().getApplId());
	    PersonnelResDTO resDto = SmServiceLookup.getPersonnelSO().retrieveOwnerOrDirectorByApplId(reqDto);
	    // Test Case 2: Ensure that personnel list is populated based on conditions
	    logger.logCodeCoverage("Test Case 2: Start checking and adding personnel to roles");
	    
	    if (resDto.getSmPersonnelList() != null && !resDto.getSmPersonnelList().isEmpty()) {
	        for (SmPersonnel personnel : resDto.getSmPersonnelList()) {
	            SmPersonnel personnelOwner = (SmPersonnel) EntityHelper.getEntityBySinglePropertyFromList(personnel.getIdentificationNo(), ssmResDTO.getSmPersonnelOwners(), "identificationNo");
	            SmPersonnel personnelDirector = (SmPersonnel) EntityHelper.getEntityBySinglePropertyFromList(personnel.getIdentificationNo(), ssmResDTO.getSmPersonnelDirector(), "identificationNo");

	            Boolean isPersonnelAdminRole = isPersonnelAdminRole(personnel);
	            Boolean isPersonnelOwnerDirectorNull = isPersonnelOwnerDirectorNull(personnelOwner, personnelDirector);

	            Boolean shouldAddPersonnel = isPersonnelOwnerDirectorNull && (!isMofAccEffDatePassed6MonthCurrDate || (isMofAccEffDatePassed6MonthCurrDate && isPersonnelAdminRole));
	            
	            if (shouldAddPersonnel) {
	                getManagedBean().getPersonnelsRole().add(personnel);
	            }
	            
	            if (isPersonnelAdminRole && isMofAccEffDatePassed6MonthCurrDate && disallowEditApplType) {
	                getManagedBean().setDisAllowOwnerEdit(true);
	                break;
	            }
	        }
	    }
	    
	    // Log personnel role population completion
	    logger.logCodeCoverage("Test Case 2: Personnel added to roles list");
	    
	    // Test Case 3: Check if disallow owner edit flag is set correctly
	    logger.logCodeCoverage("Test Case 3: Check if disallow owner edit flag is set");
	    if (getManagedBean().getPersonnelsRole() != null && !getManagedBean().getPersonnelsRole().isEmpty()) {
	    	// Test Case 4: Set downgrade role flag if personnel is added
	    	logger.logCodeCoverage("Test Case 4: Setting downgrade role flag");
	        getManagedBean().setDowngradeRole(true);

	        String epRole = getEpRoleDowngradePopupByPersonnel(getManagedBean().getPersonnelsRole());
	        getManagedBean().setTermAndConditionCheckOwner(SmCommonUtil.getMessage("lbl.sm.term.and.condition.checkowner", new String[]{epRole}));
	        getManagedBean().setTermAndConditionCheckOwnerA(SmCommonUtil.getMessage("lbl.sm.term.and.condition.checkowner.a", new String[]{epRole}));
	        getManagedBean().setTermAndConditionCheckOwnerC(SmCommonUtil.getMessage("lbl.sm.term.and.condition.checkowner.c", new String[]{epRole}));
	        getManagedBean().setTermAndConditionCheckOwnerChangeRole(SmCommonUtil.getMessage("lbl.sm.term.and.condition.checkowner.changerole", new String[]{epRole}));
	        // Log the downgrade role and popup actions
	        logger.logCodeCoverage("Test Case 3: Downgrade role set, displaying term and condition popup");
	        
	        RequestContext.getCurrentInstance().execute("blockWidget.hide()");
	        RequestContext.getCurrentInstance().execute("ownerCheckingWidgetVar.show()");
	        logger.logCodeCoverage("Test Case 4: Downgrade role flag set and UI updated");
	    }
	 // Log downgrade role setting completion
	    logger.logCodeCoverage("Test Case 4: Method displayPopupDowngradeSSMOwner completed");
	    return getManagedBean().isDowngradeRole();
	}

	
	public Boolean isPersonnelAdminRole(SmPersonnel smPersonnel){
		return smPersonnel.getEpRole() != null 
				&& my.ep.constant.sm.SupplierManagementConst.SUPPLIER_ADMIN_ROLES.contains(smPersonnel.getEpRole());
	}
	
	public Boolean isPersonnelOwnerDirectorNull(SmPersonnel smPersonnelOwner,SmPersonnel smPersonnelDirector){
		Boolean isPersonnelOwnerDirectorNull = false;
		if(isNull(smPersonnelOwner) && isNull(smPersonnelDirector)){
			isPersonnelOwnerDirectorNull = true;
		}
		return isPersonnelOwnerDirectorNull;
	}
	//END #29956
	
	//#12487 start
	/**
	 * To retrieve PTJ code by org profile ID and created date
	 * @param orgProfileId
	 * @param tranDate
	 * @return Organization Code
	 */
	public String retrievePTJCodeByOrgProfileId(Long orgProfileId, Date tranDate) {
		IOrgProfileSO profileSO =  BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(orgProfileId);
		reqDTO.setCreateDate(tranDate);
		PmOrgProfile orgProfile = profileSO.retrieveOrgProfileByProfileId(
				reqDTO).getPmOrgProfile();
		if (orgProfile != null) {
			return orgProfile.getOrgCode();
		}
			
		return null;
	}
	
	//#12487 end

	//#32304
	/**
	 * Download report of a list of Bumi applications cancelled by system (status: 20210)
	 * @param isSVC
	 * @throws IOException
	 */
	public void downloadCancelledBumiAppl(boolean isSVC) throws IOException{
		Map<String, String> paramMap = new HashMap<>();
		paramMap.put(ReportConst.PARAM_REPORT_ID, isSVC ? "SM_100" : "SM_099");
		paramMap.put(ReportConst.PARAM_USER_ID, new FacesScopes().getUserSessionBean().getUserId().toString());
		paramMap.put("PLANGUANGE", "ms");
		String report = isSVC ? ReportConst.SM_BUMI_APPL_CANCELLED_SVC : ReportConst.SM_BUMI_APPL_CANCELLED;

		generateReport(report, paramMap);
	}
	
	// #28854,#25000 - Start
	/**
	 * Processes the given string by replacing newline characters with HTML line
	 * break tags and encoding the < and > characters to their HTML entity
	 * equivalents. If the string is null, empty or contains only whitespace, it returns null.
	 * 
	 * @param value
	 *            the string to be processed; may be null
	 * @return the processed string value with newline characters replaced by
	 *         &lt;br/&gt; and <, > characters encoded to &lt; and &gt;
	 *         respectively; {@code null} if the input is null or empty
	 */
	public String encodeHtmlStringOrNull(String value) {
		if (value == null || value.trim().isEmpty()) {
			return null;
		}
		// Replace < and > with their HTML entity equivalents
		String formatted = value.replace("<", "&lt;").replace(">", "&gt;");
		// Replace any newline characters (\r\n or \n) with <br/>
		return formatted.replaceAll("(\r\n|\n)", "<br/>");
	}
	// #28854,#25000 - End
	
	// #25000 - Start
	/**
	 * A utility method to format a date.
	 * 
	 * @param date
	 *            the Date object to format
	 * @return the formatted date string, or an empty string if the date is null
	 */
	public String formatDate(Date date) {
		SimpleDateFormat dateformat = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		return date != null ? dateformat.format(date) : "";
	}
	// #25000 - End
	
	/**
	 * Retrieves a list of personnel blacklisted under a specific supplier.
	 *
	 * @param supplierId
	 * @return A list of {@code SmPersonBlacklist} objects representing blacklisted personnel associated with the supplier.
	 */
	public static List<BlacklistOwnerDirectorVO> getSupplierHasPersonnelBlacklist(Long supplierId) {
		List<SmPersonBlacklist> blacklistPersonnel = SmServiceLookup.getBlacklistDirectorSO().retrieveSupplierHasPersonnelBlacklist(supplierId);
		List<BlacklistOwnerDirectorVO> blacklist = new ArrayList<>();
		if (blacklistPersonnel.isEmpty()){
			return blacklist;
		}
		BlacklistDirectorReqDTO reqDTO = new BlacklistDirectorReqDTO();
		for (SmPersonBlacklist per : blacklistPersonnel){
			BlacklistOwnerDirectorVO blacklistOwner = new BlacklistOwnerDirectorVO();
			reqDTO.setDirectorIdNo(per.getIdentificationNo());
			
			BlacklistDirectorResDTO resDTO = SmServiceLookup.getBlacklistDirectorSO()
					.retrieveAllPersonBlacklistByIdentificationNO(reqDTO);
			if (resDTO != null && resDTO.getPersonBlacklist() != null) {
				blacklistOwner.setIcNo(per.getIdentificationNo());
				blacklist.add(blacklistOwner);
			}
		}
		return blacklist;
	}
	
	//26238 - START
	/**
	 * Retrieves the list of active disciplinary actions for the given supplier.
	 * 
	 * @param smSupplier The supplier for whom disciplinary actions are being retrieved.
	 * @return A list of {@link DisciplinaryActionVO} representing the active disciplinary actions
	 *         associated with the given supplier. If an error occurs, an {@link EpException} is thrown.
	 * @throws EpException If there is an issue retrieving disciplinary actions.
	 */
	public static List<DisciplinaryActionVO> onDisplayActiveDisciplinaryAction(SmSupplier smSupplier) {
		try {
			// #30756 start
			smSupplier.setFlagSupplier(true);
			smSupplier.setFlagDASuppProfile(true);
			// #30756 end

			return ViewSuppProfileBackingBean.getDisciplinaryActionVO(smSupplier);
		} catch (Exception e) {
			log.error("An error occurred while handling disciplinary actions for supplier: ", e);
			throw new EpException(MessageConst.ERROR_770045, new String[] { e.getMessage() });
		}
	} 
	//26238 - END
}

