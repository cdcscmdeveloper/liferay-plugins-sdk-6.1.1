package my.ep.web.backingbean.bpm.errorlist;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.jaxb.error.data.ErrorDataType;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.bpm.errorlist.BPMErrorListManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.pm.helper.StatusHelper;

import org.apache.log4j.Logger;

import com.liferay.compat.portal.util.PortalUtil;

@ManagedBean(name = "bpmErrorListBackingBean")
@RequestScoped
public class BPMErrorListBackingBean extends BaseBackingBean<BPMErrorListManagedBean>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(BPMErrorListBackingBean.class);

	private ISearchBean<PmParameterDesc> docTypeSearchBean;

	public ISearchBean<PmParameterDesc> getDocTypeSearchBean() {
		if (this.docTypeSearchBean == null) {
			this.docTypeSearchBean = super.createSearchBean(
					PmParameterDesc.class, "docTypeId");
		}

		return this.docTypeSearchBean;
	}

	public void preRenderErrorView(){
		log.info("............................preRenderErrorView");

		Object token = getBPMAuthenticationCtxToken();
		this.getManagedBean().setToken(token);
		this.getManagedBean().setViewBy("");
		this.getManagedBean().setDocNo("");
		this.getManagedBean().setDocNoGroup("");
		this.getManagedBean().setErrorDesc("");
		this.getManagedBean().setErrorDescGroup("");

		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);

		String docTypeId = originalServletRequest.getParameter("docTypeId");

		if(docTypeId != null && !docTypeId.isEmpty()){
			this.getManagedBean().setDocType(docTypeId);
			originalServletRequest.getParameterMap().remove("docTypeId");
		}
		else{
			this.getManagedBean().setDocType("");
		}
		
		this.getManagedBean().getErrorListDataModel().reloadData();
		
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "errorListView");
	}

	public Object getBPMAuthenticationCtxToken() {
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);
		Object ctxToken = bpmSO.authenticate("weblogic");
		return ctxToken;
	}

	public void refreshNormalErrorList(){
		this.getManagedBean().getErrorListDataModel().reloadData();
	}

	public String applicationStatus(Integer docInt){
		PmStatusDesc pmStatusDesc = StatusHelper.getInstance().getStatusDescById(docInt.longValue());

		if(log.isDebugEnabled()){
			log.debug("docInt:" + docInt);
		}

		return pmStatusDesc != null ? pmStatusDesc.getStatusName() : "Review";
	}
	
	public void submitError(String taskId, String title){
		log.info("............................submitTask");
		
		FacesScopes facesScopes = new FacesScopes();
		PmUser user = facesScopes.getUserSessionBean();
		
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);

		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setTaskId(taskId);
		reqDTO.setCtxToken(getManagedBean().getToken());
		reqDTO.setOutcome(BpmOutcomeConst.BPM_SUBMIT);

		BpmResponseDTO resDTO = bpmSO.updateTask(reqDTO);


		this.getManagedBean().getErrorListDataModel().reloadData();

		if(resDTO.isSuccessful()){
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
							.getMessage(
									MessageConst.INFO_700034,
									new String[] {title},
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
			
			log.info(title+" | "+user.getUserName()+" | "+DateUtils.getCurrentDate());
		}

	}
	
	public String refreshError(){
		
		this.getManagedBean().getErrorListDataModel().resetData();
		
		return "errorListView?faces-redirect=true";
	}

	public void onClickPayload(String title,String taskId) throws JAXBException{
		
		this.getManagedBean().setErrorMessageTitle("");
		this.getManagedBean().setPayloadContent("");
		this.getManagedBean().setErrorMessageTitle(title);
		
		ErrorDataType pojo = null;
		
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);
		

		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setCtxToken(getManagedBean().getToken());
		reqDTO.setTaskId(taskId);
		
		BpmResponseDTO resDTO = bpmSO.getTaskDetails(reqDTO);

		if(resDTO != null){
			pojo = (ErrorDataType) resDTO.getPojo();
		}
		
		this.getManagedBean().setPayloadContent(pojo.getErrorDesc());
		
		refreshError();
	}
	/*
	public String printFieldValue(Object obj){
		
		String value = "";
		
		for(Field field : obj.getClass().getDeclaredFields()){
		
			field.setAccessible(true);
			String name = field.getName();
			Object object = null;
			
			try {
				object = field.get(obj);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			
			value += name+"\t: "+object+"<br />";
		
		}
		
		return value;
	}
*/
}
