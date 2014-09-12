package my.ep.web.backingbean.pm.profcertbody;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.certbody.ICertBodySO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.certbody.CertBodyReqDTO;
import my.ep.dto.pm.common.certbody.CertBodyResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmCertBody;
import my.ep.type.aut.SearchOperator;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.pm.profcertbody.ProfCertifiedBodyManagedBean;

/**
 * @author DK
 * 
 */
@ManagedBean(name = "profcertifiedbodyBackingBean")
@ViewScoped
public class ProfCertifiedBodyBackingBean extends

BaseBackingBean<ProfCertifiedBodyManagedBean> {
	private static final Logger log = EpLogger
			.getLogger(ProfCertifiedBodyBackingBean.class);

	private IPagingDataModel<PmCertBody> profCertBodyModel = new CertBodyPagingDataModel();

	public ProfCertifiedBodyBackingBean() {
		log.info("inside constructor");

	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	/**
	 * reset professional & cert search detail
	 * 
	 */
	public void resetSearch() {
		debugLogger("resetSearch");
		
        RequestContext.getCurrentInstance().reset("profPanel");

	}

	/**
	 * reload to professional certified search page
	 * 
	 */
	public String redirectProfSearch() {
		debugLogger("redirectProfSearch");

		return "/xhtml/pm/professionalCertifiedBody/professionalCertifiedBodyMain.xhtml";

	}

	/**
	 * Go to creation of new profCert body
	 * 
	 */	
	public String createProfCert() {
		debugLogger("createProfCert");
		PmCertBody certBody = new PmCertBody();
		this.getManagedBean().setCertBody(certBody);
		return "/xhtml/pm/professionalCertifiedBody/professionalCertifiedMaintenance.xhtml";

	}

	/**
	 * get status item dropdown
	 * 
	 */	
	 public SelectItem[] getCertStatusList() {
		 
			SelectItem[] items =  new SelectItem[2];
			items[0] = new SelectItem(1, "Active");
			items[1] = new SelectItem(0, "Inactive");
		    return items;
		}

	/**
	 * get Professional / Certified body type
	 * 
	 */	
	public SelectItem[] getBodyType() {

		SelectItem[] items = null;
			items = new SelectItem[3];
			items[0] = new SelectItem(null, "Select One");
			items[1] = new SelectItem(0, "Professional");
			items[2] = new SelectItem(1, "Certified");

		return items;
	}

	/**
	 * retrieve professional / certified based on selection
	 * 
	 */	
	public void retriveProfCert() {
		log.info("retriveProfCert");
		profCertBodyModel.reloadData();
	}

	public IPagingDataModel<PmCertBody> getProfCertBodyModel() {
		return profCertBodyModel;
	}

	public void setProfCertBodyModel(
			IPagingDataModel<PmCertBody> profCertBodyModel) {
		this.profCertBodyModel = profCertBodyModel;
	}

	/**
	 * pagination for prof/certified list
	 * 
	 */	
	private class CertBodyPagingDataModel extends PagingDataModel<PmCertBody> {

		private static final long serialVersionUID = 1L;

		public CertBodyPagingDataModel() {
			super("searchProfCertResult");
		}

		@Override
		protected ISearchResultBean<PmCertBody> pageChangeListener(
				ISearchBean<PmCertBody> searchBean) {
			if (null == searchBean) {
				searchBean = new SearchBean<PmCertBody>();
			}			
			if (null != getManagedBean().getCertType()) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("isCertifiedBody",
								SearchOperator.OPERATOR_EQUALS.value(),
								getManagedBean().getCertType()));
			}			
			if (!isNullOrEmpty(getManagedBean().getBodyCode())) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("bodyCode",
								SearchOperator.OPERATOR_LIKE.value(),
								getManagedBean().getBodyCode()));
			}
			if (!isNullOrEmpty(getManagedBean().getBodyName())) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("bodyName",
								SearchOperator.OPERATOR_LIKE.value(),
								getManagedBean().getBodyName()));
			}
			if (null != getManagedBean().getStatus()) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("recordStatusFlg",
								SearchOperator.OPERATOR_EQUALS.value(),
								getManagedBean().getStatus()));
			}

			ICertBodySO bodySO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							ICertBodySO.class);
			CertBodyReqDTO certBodyReqDTO = new CertBodyReqDTO();
			certBodyReqDTO.setCertBodySearchBean(searchBean);

			return bodySO.retrieveBodyList(certBodyReqDTO)
					.getCertBodyResultBean();

		}

	}

	private boolean isNullOrEmpty(String str) {
		if (null == str || "".equals(str)) {
			return true;
		}
		return false;
	}

	public void searchProfessionalCertBody() {
		getProfCertBodyModel().reloadData();
	}
	
	/**
	 * Validation message for professional / certified details
	 * 
	 */
	private void validationMessage(PmCertBody certBody){
		debugLogger("validationMessage");
		if(StringUtils.isBlank(certBody.getBodyCode())){
			throw new EpException("Code is Mandatory");
		}
		
		if(StringUtils.isBlank(certBody.getBodyName())){
			throw new EpException("Name is Mandatory");
		}	
		
		if(StringUtils.isBlank(certBody.getDescription())){
			throw new EpException("Description is Mandatory");
		}			
		
	}		

	/**
	 * to save professional / certified details
	 * 
	 */	
	public String saveCertBodyDtls(){
		
	try {	
		PmCertBody certBody =  this.getManagedBean().getCertBody();
		validationMessage(certBody);
		
		ICertBodySO certBodySO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICertBodySO.class);
		
		CertBodyReqDTO certBodyReqDTO= new CertBodyReqDTO();
		certBodyReqDTO.setCertBody(certBody);
		CertBodyResDTO resDTO = certBodySO.saveOrUpdateCertBodyDtls(certBodyReqDTO);
		
		this.getManagedBean().setCertBody(resDTO.getCertBody());
		if(resDTO.isSuccessful()){
			FacesContext
			.getCurrentInstance()
			.addMessage(
					null,
					new FacesMessage(
							FacesMessage.SEVERITY_INFO,"","Saved Successfully"));
		}
      } catch (Exception e) {
		e.printStackTrace();
		FacesContext.getCurrentInstance().addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
						"Record Save failed."));
		throw e;
	}
	
		return "/xhtml/pm/professionalCertifiedBody/professionalCertifiedBodyMain.xhtml";
 }

	/**
	 * get value from search to maintenance page
	 * 
	 */	
	public String  editCertDetials(PmCertBody certBody){
		this.getManagedBean().setCertBody(certBody);
		return "/xhtml/pm/professionalCertifiedBody/professionalCertifiedMaintenance.xhtml";
	}

}
