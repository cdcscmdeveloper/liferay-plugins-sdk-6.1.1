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
package my.ep.web.backingbean.pm.profcertbody;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.certbody.ICertBodySO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.dto.pm.common.certbody.CertBodyReqDTO;
import my.ep.dto.pm.common.certbody.CertBodyResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.common.PmCertBody;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.ProfCertifiedBodySearchBean;
import my.ep.web.managedbean.pm.profcertbody.ProfCertifiedBodyManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

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
	
	private String NAV_TO_MAIN =  "professionalCertifiedBodyMain";

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
	public void reset() {
		debugLogger("resetSearch");
        this.getManagedBean().setProfCertifiedBodySearchBean(new ProfCertifiedBodySearchBean());
        this.getManagedBean().getProfCertifiedBodySearchBean().setBodyCode(null);
        this.getManagedBean().getProfCertifiedBodySearchBean().setStatus(null);
        this.getManagedBean().setIslazy(false);
        profCertBodyModel = new CertBodyPagingDataModel();
	}

	/**
	 * reload to professional certified search page
	 * 
	 */
	public String redirectProfSearch() {
		debugLogger("redirectProfSearch");

		return NAV_TO_MAIN;

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
//		 
//			SelectItem[] items =  new SelectItem[2];
//			items[0] = new SelectItem(1, "Active");
//			items[1] = new SelectItem(0, "Inactive");
		    return PmCommonUtil.getRecordStatusOptions();
		}

	/**
	 * get Professional / Certified body type
	 * 
	 */	
	public SelectItem[] getBodyType() {

		SelectItem[] items =  new SelectItem[2];
		items[0] = new SelectItem(0, ResourceBundleUtils.getMessage(
				"lbl.pm.pc.prof", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale()));
		items[1] = new SelectItem(1, ResourceBundleUtils.getMessage(
				"lbl.pm.pc.cert", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale()));
		return items;//PmCommonUtil.getStatusOptions();
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
			super("searchProfCertResult",getManagedBean().isIslazy());
		}

		@Override
		protected ISearchResultBean<PmCertBody> pageChangeListener(ISearchBean<PmCertBody> searchBean) {
			
			if (null == searchBean) {
				searchBean = new SearchBean<PmCertBody>();
			}			
			if (getManagedBean().getProfCertifiedBodySearchBean().getCertType() != null){
				if(getManagedBean().getProfCertifiedBodySearchBean().getCertType()==1){
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("isCertifiedBody",
									SearchOperator.OPERATOR_EQUALS.value(),1));
				}
				if(getManagedBean().getProfCertifiedBodySearchBean().getCertType()==0){
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("isProfessionalBody",
									SearchOperator.OPERATOR_EQUALS.value(),1));
				}
				
			}/* else if (getManagedBean().getProfCertifiedBodySearchBean().getCertType() == null ) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("isProfessionalBody",
								SearchOperator.OPERATOR_EQUALS.value(),
								getManagedBean().getProfCertifiedBodySearchBean().getCertType()));
			}	*/		
			if (getManagedBean().getProfCertifiedBodySearchBean().getBodyCode() != null
					&& !"".equalsIgnoreCase(getManagedBean().getProfCertifiedBodySearchBean().getBodyCode())) { 
				SearchFilterBean profCertSearchBean = new SearchFilterBean(
						SearchOperator.OPERATOR_LIKE.value());				
			
				profCertSearchBean.setSearchField("bodyCode");
				profCertSearchBean.setCaseInsensitive(true);		
				profCertSearchBean.setSearchValue(getManagedBean().getProfCertifiedBodySearchBean().getBodyCode());
				
				searchBean.getDataFilterBeans().add(profCertSearchBean);
				
			}
			if (getManagedBean().getProfCertifiedBodySearchBean().getBodyCode() != null
					&& !"".equalsIgnoreCase(getManagedBean().getProfCertifiedBodySearchBean().getBodyName())) { 
				SearchFilterBean profCertSearchBean = new SearchFilterBean(
						SearchOperator.OPERATOR_LIKE.value());				
					
				profCertSearchBean.setSearchField("bodyName");
				profCertSearchBean.setCaseInsensitive(true);		
				profCertSearchBean.setSearchValue(getManagedBean().getProfCertifiedBodySearchBean().getBodyName());
						
				searchBean.getDataFilterBeans().add(profCertSearchBean);
						
			}
			if (null != getManagedBean().getProfCertifiedBodySearchBean().getStatus()) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("recordStatusFlg",
								SearchOperator.OPERATOR_EQUALS.value(),
								getManagedBean().getProfCertifiedBodySearchBean().getStatus()));
			}else{
				List<String> activeAndInActiveStatus = new ArrayList<String>();
				activeAndInActiveStatus.add(ParameterConst.RECORD_STATUS_INACTIVE);
				activeAndInActiveStatus.add(ParameterConst.RECORD_STATUS_ACTIVE);
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("recordStatusFlg",
								SearchOperator.OPERATOR_IN.value(), 
								activeAndInActiveStatus
				));
				
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

	/*private boolean isNullOrEmpty(String str) {
		if (null == str || "".equals(str)) {
			return true;
		}
		return false;
	}*/

	public void searchProfessionalCertBody() {
		if(null==getManagedBean().getProfCertifiedBodySearchBean().getCertType()){
			throw new EpException(MessageConst.ERROR_100022,
					new String[] { ResourceBundleUtils.getMessage(
							"lbl.pm.search.profcert",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()) });
		}else{
			getManagedBean().setIslazy(true);
			getProfCertBodyModel().reloadData();
		}
	}
	
	/**
	 * Validation message for professional / certified details
	 * 
	 */
	private void validationMessage(PmCertBody certBody){
		debugLogger("validationMessage");
		/*if(StringUtils.isBlank(certBody.getBodyCode())){
			throw new EpException(MessageConst.ERROR_100022,
					new String[] { ResourceBundleUtils.getMessage(
							"lbl.pm.search.codes",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()) });
		}*/
		
		if(StringUtils.isBlank(certBody.getBodyName())){
			throw new EpException(MessageConst.ERROR_768000,
					new String[] { PmCommonUtil.getResourceBundleLabels("lbl.pm.pc.certifiedby") });
		}	
		
		if(certBody.isCertifiedBody() && StringUtils.isBlank(certBody.getDescription())){
			throw new EpException(MessageConst.ERROR_768001,
					new String[] { PmCommonUtil.getResourceBundleLabels("lbl.pm.pc.certname") });
		}
		
		if(!certBody.isCertifiedBody()&&!certBody.isProfessionalBody()){
			throw new EpException(MessageConst.ERROR_768002,
					new String[] { PmCommonUtil.getResourceBundleLabels("lbl.pm.search.profcert") });
		}
		
		if(DbValueConst.RECORD_STATUS_ACTIVE == certBody.getRecordStatusFlg()){
			ICertBodySO bodySO = null;
			try{
				ISearchBean<PmCertBody> searchBean = new SearchBean<PmCertBody>();
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("bodyName",
								SearchOperator.OPERATOR_EQUALS.value(),
								certBody.getBodyName().toLowerCase().trim(), 
								true));
				
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("recordStatusFlg",
								SearchOperator.OPERATOR_EQUALS.value(),
								DbValueConst.RECORD_STATUS_ACTIVE));
				
				if(!certBody.isBrandNew()){
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("certBodyId",
									SearchOperator.OPERATOR_NOT_EQUALS.value(),
									certBody.getCertBodyId()));
				}
				bodySO = EpEjbBusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								ICertBodySO.class);
				CertBodyReqDTO certBodyReqDTO = new CertBodyReqDTO();
				certBodyReqDTO.setCertBodySearchBean(searchBean);
				CertBodyResDTO certBodyResDTO = bodySO.retrieveBodyList(certBodyReqDTO);
				if(certBodyResDTO.getCertBodyResultBean() != null &&
						certBodyResDTO.getCertBodyResultBean().getResultList() !=null &&
						!certBodyResDTO
						.getCertBodyResultBean().getResultList().isEmpty() ){
					if(certBody.isBrandNew()){
						throw new EpException(MessageConst.ERROR_770221,new String[]{
								PmCommonUtil.getResourceBundleLabels("lbl.pm.search.name"),
								PmCommonUtil.getResourceBundleLabels("lbl.pm.search.name")					
							});
					}else{
						throw new EpException(MessageConst.ERROR_770220, new String[] {
								PmCommonUtil.getResourceBundleLabels("lbl.pm.search.name")
						});
					}
				}
			}finally{
				bodySO = null;
			}
			
		}
		
//		if (certBody.isBrandNew() && certBody.getBodyCode() != null
//				&& !"".equalsIgnoreCase(certBody.getBodyCode())) { 
//			SearchFilterBean profCertSearchBean = new SearchFilterBean(
//					SearchOperator.OPERATOR_EQUALS.value());				
//		
//			profCertSearchBean.setSearchField("bodyCode");
//			profCertSearchBean.setCaseInsensitive(true);		
//			profCertSearchBean.setSearchValue(certBody.getBodyCode());
//			ISearchBean<PmCertBody> searchBean = new SearchBean<PmCertBody>();
//			searchBean.getDataFilterBeans().add(profCertSearchBean);
//			
//			ICertBodySO bodySO = EpEjbBusinessDelegate.getInstance()
//					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
//							ICertBodySO.class);
//			CertBodyReqDTO certBodyReqDTO = new CertBodyReqDTO();
//			certBodyReqDTO.setCertBodySearchBean(searchBean);
//			CertBodyResDTO certBodyResDTO = bodySO.retrieveBodyList(certBodyReqDTO);
//			if(certBodyResDTO.getCertBodyResultBean() != null &&
//					certBodyResDTO.getCertBodyResultBean().getResultList() !=null &&
//					!certBodyResDTO
//					.getCertBodyResultBean().getResultList().isEmpty() ){
//				throw new EpException(MessageConst.ERROR_770160,
//						new String[] {certBody.getBodyCode()});
//			}
//			
//		}
	}		

	/**
	 * to save professional / certified details
	 * 
	 */	
	public String saveCertBodyDtls() {
		debugLogger("saveCertBodyDtls");
		String redirectUrl = null;
		ICertBodySO certBodySO = null;
		IStatusSO statusSO = null;

		PmCertBody certBody = this.getManagedBean().getCertBody();

		validationMessage(certBody);
		
		try {
			
			// PmCertBody olPmCertBody=this.getManagedBean().getOldPmCertBody();
			// PmCertBody pmCertBody = this.getManagedBean().getCertBody();

			if (certBody.isBrandNew()) {
				setUserDetails(certBody);
				generateCertBodyCode();
			}
			statusSO = (IStatusSO) BusinessDelegate.getInstance()
					.getServiceObject("pm", IStatusSO.class);
			certBodySO = EpEjbBusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, ICertBodySO.class);

			String messageId = null;
			// String recordStatusMessageId=null;
			CertBodyReqDTO certBodyReqDTO = new CertBodyReqDTO();
			

			String[] msgParam = new String[] { certBody.getBodyName() };

			if (certBody.isBrandNew()) {
				messageId = MessageConst.TD_81100;
			} else {
				messageId = MessageConst.TD_81101;
				/*
				 * if(olPmCertBody!=null && pmCertBody!=null &&
				 * !olPmCertBody.getRecordStatusFlg
				 * ().equals(pmCertBody.getRecordStatusFlg())){
				 * 
				 * if(certBody.getRecordStatusFlg().equals(DbValueConst.
				 * RECORD_STATUS_ACTIVE)){
				 * 
				 * recordStatusMessageId = MessageConst.TD_81102; } else{
				 * recordStatusMessageId = MessageConst.TD_81103; }
				 * certBodyReqDTO.getTrackDairyVOs().add(PmCommonUtil.
				 * assignTrackingDairyForProfessional(certBody.getCertBodyId(),
				 * certBody
				 * .getBodyCode(),getPmActionDesc(recordStatusMessageId,msgParam
				 * ))); }
				 */
			}
			certBodyReqDTO.setTrackingStatus(true);

			certBodyReqDTO.getTrackDairyVOs().add(
					PmCommonUtil.assignTrackingDairyForProfessional(
							certBody.getCertBodyId(),
							certBody.getBodyCode(),
							certBody.getRecordStatusFlg().longValue(),
							getPmActionDesc(messageId, msgParam)));

			certBodyReqDTO.setCertBody(certBody);

			CertBodyResDTO resDTO = certBodySO
					.saveOrUpdateCertBodyDtls(certBodyReqDTO);
			reloadProfCertData(resDTO.getCertBody());
			PmCertBody oldPmCertBody = (PmCertBody) BeanUtils.cloneBean(resDTO
					.getCertBody());
			this.getManagedBean().setOldPmCertBody(oldPmCertBody);

			this.getManagedBean().setCertBody(resDTO.getCertBody());
			if (resDTO.isSuccessful()) {
				if (certBody.isBrandNew()) {
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_INFO,
											null,
											MessageUtils
													.getMessage(
															MessageConst.INFO_770091,
															new String[] { resDTO
																	.getCertBody()
																	.getBodyName() },
															MessageUtils.BUNDLE_COMMON_MESSAGES,
															new FacesScopes()
																	.getUserLocale())));
					redirectUrl = NAV_TO_MAIN;
				} else {
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_INFO,
											null,
											MessageUtils
													.getMessage(
															MessageConst.INFO_770092,
															MessageUtils.BUNDLE_COMMON_MESSAGES,
															new FacesScopes()
																	.getUserLocale())));
					redirectUrl = null;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			certBodySO = null;
			statusSO = null;
		}

		return redirectUrl;
	}

	/**
	 * get value from search to maintenance page
	 * 
	 */	
	public String  editCertDetials(PmCertBody certBody){
		this.getManagedBean().setCertBody(certBody);
		PmCertBody oldPmCertBody = null;
		try {
			oldPmCertBody = (PmCertBody)BeanUtils.cloneBean(certBody);
		     this.getManagedBean().setOldPmCertBody(oldPmCertBody);		
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "/xhtml/pm/professionalCertifiedBody/professionalCertifiedMaintenance.xhtml";
	}
		
	/**
	 * Reload prof cert data.
	 * 
	 * @param certBody
	 *            the cert body
	 */
	private void  reloadProfCertData(PmCertBody certBody){
		this.getManagedBean().setCertBody(certBody);
		PmCertBody oldPmCertBody = null;
		try {
			oldPmCertBody = (PmCertBody)BeanUtils.cloneBean(certBody);
		     this.getManagedBean().setOldPmCertBody(oldPmCertBody);		
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void preLoadgroupSearch(){
		renewSesionObject();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_MAIN);
	}
	
	public void setUserDetails(BaseEntity baseObject) {
		if (baseObject.isBrandNew()) {
			baseObject.setCreatedBy(1L);
			baseObject.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		}
		baseObject.setChangedBy(1L);
	}
	public void generateCertBodyCode(){
		CertBodyReqDTO reqDTO = new CertBodyReqDTO();
		reqDTO.setCertBodyCode(ProfileManagementConst.CERT_BODY_CODE);
		ICertBodySO certBodySO = (ICertBodySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICertBodySO.class);	
		CertBodyResDTO resDTO = certBodySO.generateCertBodyCode(reqDTO);
		getManagedBean().getCertBody().setBodyCode(resDTO.getCertBodyCode());
		log.info("generated Body Code--------->"+resDTO.getCertBodyCode());
		
	}
	
	/**
	 * Gets the pm action desc.
	 * 
	 * @param messageId
	 *            the message id
	 * @param params
	 *            the params
	 * @return the pm action desc
	 */
	private String getPmActionDesc(String messageId, String[] params) {
		Locale myLocale = new Locale("ms", "MY");
		return MessageUtils.getMessage(messageId, params,
				MessageUtils.BUNDLE_COMMON_MESSAGES, myLocale);
	}

	/**
	 * get YES & NO options //TODO: to load dynamically
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] getYesNoOptions() {
		return PmCommonUtil.getYesNoBooleanOptions();
	}
}
