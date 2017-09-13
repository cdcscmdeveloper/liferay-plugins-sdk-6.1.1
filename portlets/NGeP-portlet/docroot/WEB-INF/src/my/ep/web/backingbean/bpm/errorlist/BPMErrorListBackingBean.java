package my.ep.web.backingbean.bpm.errorlist;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
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
	
	private String language = new FacesScopes().getUserLocale().getLanguage();
	
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

}
