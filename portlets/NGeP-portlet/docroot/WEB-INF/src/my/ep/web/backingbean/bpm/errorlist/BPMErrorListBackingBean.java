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
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.bpm.errorlist.BPMErrorListManagedBean;
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
	
	public void submitError(String taskId){
		log.info("............................submitTask");
		
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);
		
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setTaskId(taskId);
		reqDTO.setCtxToken(getBPMAuthenticationCtxToken());
		
		BpmResponseDTO resDTO = bpmSO.getTaskDetails(reqDTO);

		if(resDTO.getPojo() != null){
			reqDTO.setOutcome(BpmOutcomeConst.BPM_SUBMIT);
			reqDTO.setPojo(resDTO.getPojo());
		}
		
		resDTO = bpmSO.updateTask(reqDTO);
		
		this.getManagedBean().getErrorListDataModel().reloadData();
		
		if(resDTO.isSuccessful()){
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
							.getMessage(
									MessageConst.INFO_700034,
									new String[] {},
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
		}
		
	}

	public void onClickPayload(String taskId) throws JAXBException{

		Object payload = null;
		String payLoadString = null;
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);

		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setCtxToken(getBPMAuthenticationCtxToken());
		reqDTO.setTaskId(taskId);
		BpmResponseDTO resDTO = bpmSO.getTaskDetails(reqDTO);

		if(resDTO.getPojo() != null){
			payload = resDTO.getPojo();
			payLoadString = Parser.getInstance().parseObj(payload);
		}
		
		this.getManagedBean().setPayloadContent(payLoadString);

	}

}
