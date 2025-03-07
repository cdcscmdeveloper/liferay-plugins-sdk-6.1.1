package my.ep.web.backingbean.cm.codification;

import java.io.FileInputStream;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.cm.codification.IRequestCodeSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sm.mofAccount.IMofAccountSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.biz.service.trackingdiary.ITrackingDiarySO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.cm.CmConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.constants.CDProcessNameConst;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.dto.cm.codification.RequestCodeReqDTO;
import my.ep.dto.cm.codification.RequestCodeResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryReqDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryResDTO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.cd.requestdata.CDRequestDataType;
import my.ep.persistence.entity.cm.catalog.CmBrand;
import my.ep.persistence.entity.cm.catalog.CmColor;
import my.ep.persistence.entity.cm.catalog.CmMeasurement;
import my.ep.persistence.entity.cm.catalog.CmRecommendCode;
import my.ep.persistence.entity.cm.catalog.CmRemark;
import my.ep.persistence.entity.cm.catalog.CmRequestCode;
import my.ep.persistence.entity.cm.catalog.CmType;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmTrackingDiary;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.bpm.tasklist.BPMTaskListBackingBean;
import my.ep.web.managedbean.cm.codification.UnspscCodeManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.cm.helper.CmApplicationUtil;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.liferay.portal.util.PortalUtil;

@ManagedBean(name = "unspscCodeBackingBean")
@RequestScoped
public class UnspscCodeBackingBean extends
BaseBackingBean<UnspscCodeManagedBean>implements Serializable {

	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(UnspscCodeBackingBean.class);

	private SearchFilterBean colorFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean typeFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean brandFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean measurementFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_EQUALS.value());
	
	
	
	private SearchFilterBean unspscIdFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_EQUALS.value());

	public UnspscCodeBackingBean() {
		if (MDC.get(LogKey.USER_ID) == null) {
			MDC.put(LogKey.USER_ID, 1L);
		}
	}

	public boolean isCatalogueAdmin() {
		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser("CM_ADMIN");
	}

	public boolean isSupplierUser() {
		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser("SUPPLIER_USER");
	}

	public boolean isSupplierAdmin() {
		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser("SUPPLIER_ADMIN");
	}

	public boolean isCodeAdmin() {
		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser("CD_OFFICER");
	}

	public boolean isRequisitioner() {
		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser("REQUISITIONER");
	}

	public boolean isCdCommittee() {
		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser("CD_COMMITTEE");
	}

	public boolean isSupplierTemp() {
		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser(RoleCode.SUPPLIER_TEMP.value());
	}



	public boolean isSupplierUserOrTempSupplierAdmin() {
		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser("SUPPLIER_USER")
				|| fs.hasRoleUser("SUPPLIER_TEMP")
				|| fs.hasRoleUser("SUPPLIER_ADMIN");
	}

	public boolean isAllSupplier() {

		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser("SUPPLIER_ADMIN")
				|| fs.hasRoleUser("SUPPLIER_USER")
				|| fs.hasRoleUser("MOF_SUPPLIER_USER")
				|| fs.hasRoleUser("MOF_SUPPLIER_ADMIN")
				|| fs.hasRoleUser("BASIC_SUPPLIER_USER")
				|| fs.hasRoleUser("BASIC_SUPPLIER_ADMIN")
				|| fs.hasRoleUser("SUPPLIER_TEMP")
				|| fs.hasRoleUser("G2G_USER")
				|| fs.hasRoleUser("G2G_ADMIN")
				|| fs.hasRoleUser("GOVT_SELLER")
				|| fs.hasRoleUser("CM_USER");
	}
	
	//20200605@azza - Bug #28303 - Start 
		 public boolean isAllSupplierandRequisitioner() {
	        
			FacesScopes fs = new FacesScopes();
			//20200527@Jacky - Enhancement27626 - Start  
			if (!fs.hasRoleUser("REQUISITIONER")){
			return fs.hasRoleUser("SUPPLIER_ADMIN")
					|| fs.hasRoleUser("SUPPLIER_USER")
					|| fs.hasRoleUser("MOF_SUPPLIER_USER")
					|| fs.hasRoleUser("MOF_SUPPLIER_ADMIN")
					|| fs.hasRoleUser("BASIC_SUPPLIER_USER")
					|| fs.hasRoleUser("BASIC_SUPPLIER_ADMIN")
					|| fs.hasRoleUser("SUPPLIER_TEMP")
					|| fs.hasRoleUser("G2G_USER")
					|| fs.hasRoleUser("G2G_ADMIN")
					|| fs.hasRoleUser("GOVT_SELLER")
					|| fs.hasRoleUser("CM_USER");
				
			}
			 return false;
			//20200527@Jacky - Enhancement27626 - End
		}
		//20200605@azza - Bug #28303 - End

	public boolean isAllSupplierWithoutTemp() {

		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser("SUPPLIER_ADMIN")
				|| fs.hasRoleUser("SUPPLIER_USER")
				|| fs.hasRoleUser("MOF_SUPPLIER_USER")
				|| fs.hasRoleUser("MOF_SUPPLIER_ADMIN")
				|| fs.hasRoleUser("BASIC_SUPPLIER_USER")
				|| fs.hasRoleUser("BASIC_SUPPLIER_ADMIN")
				|| fs.hasRoleUser("GOVT_SELLER");
	}


	public String submitForRequest(String action){


		log.info("action:" + action);

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
				.getInstance()
				.getServiceObject("cm", ICodificationSO.class);

		IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

		FacesScopes face = new FacesScopes();
		Object token = face.getBPMAuthenticationCtxToken();

		CodificationReqDTO reqDTO = new CodificationReqDTO();
		log.info("new:"
				+ this.getManagedBean().getRequestCode().isBrandNew());

		ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
				.getInstance().getServiceObject("sm",
						ISupplierSearchSO.class);
		SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();

		PmUser pmUser = new FacesScopes().getUserSessionBean();
		supplierReqDTO.setUserId(pmUser.getUserId());
		supplierReqDTO.setAllStatuses(true);
		// this block is to identify the request from Catalogue Admin or
		// Supplier
		if (isAllSupplier()) {
			SupplierSearchResDTO smResDTO = supplierSearchSO
					.retriveSupplierProfiles(supplierReqDTO);

			if (smResDTO.getResultBean().getResultList().isEmpty()) {
				throw new EpException(MessageConst.ERROR_721017);
			}
			log.info("getSmSupplier: "
					+ smResDTO.getResultBean().getResultList().get(0));
			this.getManagedBean()
			.getRequestCode()
			.setSmSupplier(
					smResDTO.getResultBean().getResultList().get(0));

			reqDTO.setProcessId(CDProcessNameConst.UNSPSC_CODE_REQUEST_PROCESS_ID);

			log.info("normal unspsc request");
		} else {
			reqDTO.setProcessId(CDProcessNameConst.CA_UNSPSC_CODE_REQUEST_PROCESS_ID);
			log.info("skip CA unspsc request");
		}

		// if(smResDTO.getResultBean().getResultList() == null){
		// FacesContext facesContext = FacesContext.getCurrentInstance();
		// facesContext.addMessage(null, new
		// FacesMessage(FacesMessage.SEVERITY_ERROR,
		// "user's profile not found!"," "));
		// return "unspscRequestForm";
		// }

		if (this.getManagedBean().getRequestCode().getItemType()
				.equals("S")) {// for services clear extension fields
			this.getManagedBean().getRequestCode().setTypeName(null);
			this.getManagedBean().getRequestCode().setBrandName(null);
			this.getManagedBean().getRequestCode().setMeasurementName(null);
			this.getManagedBean().getRequestCode().setColorName(null);
		}else{
			this.getManagedBean().getRequestCode().setRequestBrand(true);
			this.getManagedBean().getRequestCode().setRequestColor(true);
			this.getManagedBean().getRequestCode().setRequestMeasurement(true);
			this.getManagedBean().getRequestCode().setRequestType(true);
		}

		reqDTO.setRequestCode(this.getManagedBean().getRequestCode());
		reqDTO.setTaskId(this.getManagedBean().getTaskId());
		reqDTO.setOutCome(action);

		reqDTO.setToken(token);			
		CodificationResDTO resDTO = null;

		if (action.equalsIgnoreCase("SUBMIT")) {				
			if (this.getManagedBean().getRequestCode().isBrandNew()) {					
				log.info("Tracking diary for UNSPSC Code Request---submitForRequest()");
				reqDTO.setTrackingStatus(true);


				IStatusSO statusSO = (IStatusSO) BusinessDelegate
						.getInstance()
						.getServiceObject("pm", IStatusSO.class);

				StatusReqDTO statusReqDTO = new StatusReqDTO();

				statusReqDTO.setStatusId(15011L);
				StatusResDTO statusResDTO = statusSO.retrieveStatusByStatusId(statusReqDTO);

				/////// set at SO tracking dairy ActionDesc

				String actionDesc = "Kod Item "+reqDTO.getRequestCode().getDocNo()+" telah diserah kepada Pentadbir Katalog untuk semakan";
				reqDTO.setTrackingDairyVO(CmApplicationUtil.assignTrackingDiaryValuesForUNSPSCExtension(actionDesc, statusResDTO.getPmStatus()!=null?statusResDTO.getPmStatus():new PmStatus(),CmConst.CU_DocType));


				if(isCatalogueAdmin()){
					log.info("**isCatalogueAdmin***");
					UserReqDTO userReqDTO = new UserReqDTO();
					userReqDTO.setRoleCode(RoleCode.CD_OFFICER.value());
					UserResDTO userResDTO = userSO.retrieveUserListByRole(userReqDTO);

					if(userResDTO.getUserList()!=null && userResDTO.getUserList().size()>0){
						for(PmUser user :userResDTO.getUserList()){
							log.info("user.getLoginId():"+user.getLoginId());
							reqDTO.getCdAdminList().add(user.getLoginId());
						}
					}else{
						throw new EpException("User no found!");
					}


				}else{
					log.info("**supplier***");
					UserReqDTO userReqDTO = new UserReqDTO();
					userReqDTO.setRoleCode(RoleCode.CM_ADMIN.value());
					UserResDTO userResDTO = userSO.retrieveUserListByRole(userReqDTO);

					if(userResDTO.getUserList()!=null && userResDTO.getUserList().size()>0){
						for(PmUser user :userResDTO.getUserList()){
							log.info("user.getLoginId():"+user.getLoginId());
							reqDTO.getCmAdminList().add(user.getLoginId());
						}
					}else{
						throw new EpException("User no found!");
					}

				}




				resDTO = unspscSO.addCodificationRequest(reqDTO);

				if(resDTO.isSuccessful()){
					FacesContext facesContext = FacesContext
							.getCurrentInstance();
					facesContext
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_INFO,
									null,
									MessageUtils
									.getMessage(
											MessageConst.INFO_710013, new String[] {resDTO.getRequestCode().getDocNo()}, 
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes()
											.getUserLocale())));
					log.debug("***************BB end*****************");
					// return "unspscRequestForm";
					return "BPMtesting";
				}
			} else {
				log.info("***update***");
				resDTO = unspscSO.updateCodificationRequest(reqDTO);
			}

		}

		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null,
						MessageUtils.getMessage(MessageConst.INFO_710013, 
								new String[] {resDTO.getRequestCode().getDocNo()},
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));

		return "BPMtesting";

	}

	public String bpmTesting(String action){




		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
				.getInstance()
				.getServiceObject("cm", ICodificationSO.class);
		IStatusSO statusSO = (IStatusSO) BusinessDelegate
				.getInstance()
				.getServiceObject("pm", IStatusSO.class);

		IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		FacesScopes face = new FacesScopes();
		Object token = face.getBPMAuthenticationCtxToken();

		CodificationReqDTO reqDTO = new CodificationReqDTO();
		log.info("new:"
				+ this.getManagedBean().getRequestCode().isBrandNew());

		reqDTO.setToken(token);
		reqDTO.setTaskId(this.getManagedBean().getTaskId());
		reqDTO.setOutCome(action);
		
		if (action.equalsIgnoreCase("REJECT") || action.equalsIgnoreCase("REVISE")  ) {
			if(this.getManagedBean().getSelectedRemarks().get(0).getRemark() == null || this.getManagedBean().getSelectedRemarks().get(0).getRemark().trim().length()<=0 ){
				String error = action.equalsIgnoreCase("REJECT") ? MessageConst.ERROR_716024 : MessageConst.ERROR_716025;
				FacesContext facesContext = FacesContext
						.getCurrentInstance();
				facesContext
				.addMessage(
						null,
						new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								null,
								MessageUtils
								.getMessage(
										error,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes()
										.getUserLocale())));

				return facesContext.getViewRoot().getViewId();
			}
		}
		
		if (action.equalsIgnoreCase("RECOMMEND_TO_SUPPLIER")) {

			if (!(this.getManagedBean().getRequestCode()
					.getCmRecommendCodes() != null && this.getManagedBean()
					.getRequestCode().getCmRecommendCodes().size() > 0)) {
				log.info("**********warning***************");

				FacesContext facesContext = FacesContext
						.getCurrentInstance();
				facesContext
				.addMessage(
						null,
						new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								null,
								MessageUtils
								.getMessage(
										MessageConst.ERROR_715005,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes()
										.getUserLocale())));

				return facesContext.getViewRoot().getViewId();
			}

			for (CmRecommendCode unspsc : this.getManagedBean()
					.getRequestCode().getCmRecommendCodes()) {

				unspsc.setCmRequestCode(this.getManagedBean()
						.getRequestCode());	
			}

		}

		log.info("CmRecommendCodes size: "
				+ this.getManagedBean().getRequestCode()
				.getCmRecommendCodes().size());
		reqDTO.setRequestCode(this.getManagedBean().getRequestCode());

		CodificationResDTO resDTO = null;
		StatusReqDTO statusReqDTO = new StatusReqDTO();



		if (this.getManagedBean().getRequestCode().isBrandNew()) {
			log.info("tracking diary for UNSPSC CODE REQUEST By CatalogAdmin:addCodificationRequest");
			reqDTO.setTrackingStatus(true);


			reqDTO.setCatalogueAdminRequest(true);
			statusReqDTO.setStatusId(15011L);
			StatusResDTO statusResDTO = statusSO.retrieveStatusByStatusId(statusReqDTO);

			///////actionDesc set at SO
			reqDTO.setTrackingDairyVO(CmApplicationUtil.assignTrackingDiaryValuesForUNSPSCExtension(CmConst.UNSPSC_CODE_REQUEST_ADMIN, statusResDTO.getPmStatus()!=null?statusResDTO.getPmStatus(): new PmStatus(),CmConst.CU_DocType));


			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setRoleCode(RoleCode.CD_OFFICER.value());
			UserResDTO userResDTO = userSO.retrieveUserListByRole(userReqDTO);

			if(userResDTO.getUserList()!=null && userResDTO.getUserList().size()>0){
				for(PmUser user :userResDTO.getUserList()){
					log.info("user.getLoginId():"+user.getLoginId());
					reqDTO.getCdAdminList().add(user.getLoginId());
				}
			}else{
				throw new EpException("User no found!");
			}
			if(!this.isAllSupplier()){
				reqDTO.setRemark(this.getManagedBean().getSelectedRemarks().get(0));
			}
			resDTO = unspscSO.addCodificationRequest(reqDTO);

			FacesContext facesContext = FacesContext.getCurrentInstance();   
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
					MessageUtils.getMessage(MessageConst.INFO_710016,							//20190830@emy - CR15028
							MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));

			return "unspscRequestForm";

		} else {
			log.info("***update***");
			log.info("size:"
					+ reqDTO.getRequestCode().getCmRecommendCodes().size());
			
			//[#15028] to remove CmRecommendCodes object list if action equal to REJECT
			if(action.equalsIgnoreCase("REJECT") 
				&& (reqDTO.getRequestCode().getCmRecommendCodes() != null) 
					&& (reqDTO.getRequestCode().getCmRecommendCodes().size() > 0)){
				reqDTO.getRequestCode().getCmRecommendCodes().clear();
				//log.info("[#15028] size after: " + reqDTO.getRequestCode().getCmRecommendCodes().size());
			}				
			
			PmUser pmUser = new FacesScopes().getUserSessionBean();
			reqDTO.setUserName(pmUser.getUserName()!=null?pmUser.getUserName():"SYS");
			
			if(!action.equalsIgnoreCase(CmConst.ACTIONSTATUS)) {
				log.info("tracking diary for status:bpmTesting");			
				reqDTO.setTrackingStatus(true);
				//log.info("[#15028] calling UNSPSCCodeRequestReviewByUser...");
				UNSPSCCodeRequestReviewByUser(reqDTO,face,action,statusSO,statusReqDTO);					
			}
			if(!this.isAllSupplier()){
				reqDTO.setRemark(this.getManagedBean().getSelectedRemarks().get(0));
			}
			//log.info("[#15028] calling unspscSO.updateCodificationRequest...");
			resDTO = unspscSO.updateCodificationRequest(reqDTO);
		}
		
		//20190830@emy - CR15028 - start
		
		if (action.equalsIgnoreCase("RECOMMEND_TO_SUPPLIER")) {
			
			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(
					null,	
					new FacesMessage(FacesMessage.SEVERITY_INFO, null, 				// *** new added
							MessageUtils.getMessage(MessageConst.INFO_710017,new String[] {resDTO.getRequestCode().getDocNo()},
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
		}
		else if (action.equalsIgnoreCase("REJECT")){
			
			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(
					null,	
					new FacesMessage(FacesMessage.SEVERITY_INFO, null, 				// *** new added - reject
							MessageUtils.getMessage(MessageConst.INFO_710018,new String[] {resDTO.getRequestCode().getDocNo()},
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));

		}else if (action.equalsIgnoreCase("REVISE")  ) {
		
			
			if (this.getManagedBean().isCodeExtension()){
			
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(
						null,	
						new FacesMessage(FacesMessage.SEVERITY_INFO, null, 				
								MessageUtils.getMessage(MessageConst.INFO_710021,new String[] {resDTO.getRequestCode().getDocNo()},
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));	
			}else{
				
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(
						null,	
						new FacesMessage(FacesMessage.SEVERITY_INFO, null, 				
								MessageUtils.getMessage(MessageConst.INFO_710020,new String[] {resDTO.getRequestCode().getDocNo()},
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
			}
			
			
		}else{
			
			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(
					null,	
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.INFO_710015,new String[] {resDTO.getRequestCode().getDocNo()},
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
			
		}

		//20190830@emy - CR15028 - end

		return "BPMtesting";

	}

	public PmTrackingDiary getLatestDiaryRecord(CodificationReqDTO reqDTO, List<SearchFilterBean> filters){
		ITrackingDiarySO trackingDiarySO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.COMMON_COMPONENT, ITrackingDiarySO.class);
		TrackingDiaryReqDTO diaryReqDTO = new TrackingDiaryReqDTO();
		TrackingDiaryResDTO diaryResDTO = new TrackingDiaryResDTO();
		ISearchBean<PmTrackingDiary> searchBean = new SearchBean<PmTrackingDiary>();
		for(SearchFilterBean diaryFilterBean: filters){
			searchBean.getDataFilterBeans().add(diaryFilterBean);
		}

		diaryReqDTO.setSearchBean(searchBean);
		diaryReqDTO.setSupplierId(null);
		diaryResDTO = trackingDiarySO.retrieveTrackingDiaryBySearchBean(diaryReqDTO);
		log.info("diary res dto " + diaryResDTO.getResultBean());
		return diaryResDTO.getResultBean().getResultList().size() > 0 ? diaryResDTO.getResultBean().getResultList().get(0): null;
	}

	
	public void UNSPSCCodeRequestReviewByUser(CodificationReqDTO reqDTO,FacesScopes face,String action, IStatusSO statusSO, StatusReqDTO statusReqDTO ){

		IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

		UserReqDTO userReqDTO = new UserReqDTO();

		String actionDesc = "";
		if(action.equalsIgnoreCase("SUBMIT")){
			if (face.hasRoleUser(CmConst.CM_ADMIN)){		
				log.info("Tracking diary for UNSPSC Code Review by Catalog_Admin");

				statusReqDTO.setStatusId(15014L);
				StatusResDTO statusResDTO = statusSO.retrieveStatusByStatusId(statusReqDTO);
				actionDesc = "Kod Item "+reqDTO.getRequestCode().getDocNo()+" telah diserah kepada Pentadbir Kodifikasi untuk semakan";
				reqDTO.setTrackingDairyVO(CmApplicationUtil.assignTrackingDiaryValuesForUNSPSCExtension(actionDesc, statusResDTO.getPmStatus()!=null?statusResDTO.getPmStatus(): new PmStatus(),CmConst.CU_DocType));

				userReqDTO.setRoleCode(RoleCode.CD_OFFICER.value());
				UserResDTO userResDTO = userSO.retrieveUserListByRole(userReqDTO);

				if(userResDTO.getUserList()!=null && userResDTO.getUserList().size()>0){
					for(PmUser user :userResDTO.getUserList()){
						log.info("user.getLoginId():"+user.getLoginId());
						reqDTO.getCdAdminList().add(user.getLoginId());
					}
				}else{
					throw new EpException("User no found!");
				}


			}
			else if (face.hasRoleUser(CmConst.CODIFICATION_OFFICER)) {
				log.info("Tracking diary for UNSPSC Code Review CODIFICATION_OFFICER");
				statusReqDTO.setStatusId(15004L);
				StatusResDTO statusResDTO = statusSO.retrieveStatusByStatusId(statusReqDTO);
				actionDesc = "Kod Item "+reqDTO.getRequestCode().getDocNo()+" telah diserah kepada CDC Working Committee untuk kelulusan. ";
				reqDTO.setTrackingDairyVO(CmApplicationUtil.assignTrackingDiaryValuesForUNSPSCExtension(actionDesc, statusResDTO.getPmStatus()!=null?statusResDTO.getPmStatus(): new PmStatus(),CmConst.CU_DocType));

				userReqDTO.setRoleCode(RoleCode.CD_COMMITTEE.value());
				UserResDTO userResDTO = userSO.retrieveUserListByRole(userReqDTO);

				if(userResDTO.getUserList()!=null && userResDTO.getUserList().size()>0){
					for(PmUser user :userResDTO.getUserList()){
						log.info("user.getLoginId():"+user.getLoginId());
						reqDTO.getCdcList().add(user.getLoginId());
					}
				}else{
					throw new EpException("User no found!");
				}
			}

		}else if(action.equalsIgnoreCase("REJECT")){
				boolean isExtension = this.getManagedBean().isCodeExtension();
				statusReqDTO.setStatusId(isExtension ? 15009L : 15017L);
				StatusResDTO statusResDTO = statusSO.retrieveStatusByStatusId(statusReqDTO);
				actionDesc = "Kod Item "+reqDTO.getRequestCode().getDocNo()+" telah ditolak oleh "+reqDTO.getUserName();
				reqDTO.setTrackingDairyVO(CmApplicationUtil.assignTrackingDiaryValuesForUNSPSCExtension(actionDesc, statusResDTO.getPmStatus()!=null?statusResDTO.getPmStatus(): new PmStatus(),CmConst.CU_DocType));
		} else if(action.equalsIgnoreCase("REVISE")) {
			//finding out origin of task by checking diary - well, if you could find elegant solution for this
			List <SearchFilterBean> list = new ArrayList<SearchFilterBean>();
			SearchFilterBean docNoFilterBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			SearchFilterBean docTypeFilterBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			SearchFilterBean roleFilterBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			docNoFilterBean.setSearchField("docId");
			docNoFilterBean.setSearchValue(reqDTO.getRequestCode().getRequestId());
			list.add(docNoFilterBean);
			docTypeFilterBean.setSearchField("docType");
			docTypeFilterBean.setSearchValue(CmConst.CU_DocType);
			list.add(docTypeFilterBean);
			roleFilterBean.setSearchField("roleCode");
			roleFilterBean.setSearchValue(RoleCode.CD_OFFICER.value());
			list.add(roleFilterBean);

			PmTrackingDiary previousRecord = getLatestDiaryRecord(reqDTO, list);

			statusReqDTO.setStatusId(previousRecord != null ? 15016L: this.getManagedBean().isCodeExtension() ? 15008L: 15018L);
			String actioner = previousRecord != null ? "Pentadbir Kodifikasi" : "Pentadbir Katalog";

			StatusResDTO statusResDTO = statusSO.retrieveStatusByStatusId(statusReqDTO);
			actionDesc = "Kod Item "+reqDTO.getRequestCode().getDocNo()+" telah diserah kepada "+ actioner +" untuk kaji semula oleh CDC Working Committee";
			reqDTO.setTrackingDairyVO(CmApplicationUtil.assignTrackingDiaryValuesForUNSPSCExtension(actionDesc, statusResDTO.getPmStatus()!=null?statusResDTO.getPmStatus(): new PmStatus(),CmConst.CU_DocType));

		} else if(action.equalsIgnoreCase("RECOMMEND_TO_SUPPLIER")) {//recommend code
			if (face.hasRoleUser(CmConst.CM_ADMIN)){		
				statusReqDTO.setStatusId(15015L);
				StatusResDTO statusResDTO = statusSO.retrieveStatusByStatusId(statusReqDTO);
				actionDesc = "Kod Item "+reqDTO.getRequestCode().getDocNo()+" telah disyorkan oleh "+reqDTO.getUserName();
				reqDTO.setTrackingDairyVO(CmApplicationUtil.assignTrackingDiaryValuesForUNSPSCExtension(actionDesc, statusResDTO.getPmStatus()!=null?statusResDTO.getPmStatus(): new PmStatus(),CmConst.CU_DocType));

				userReqDTO.setRoleCode(RoleCode.CD_OFFICER.value());
				UserResDTO userResDTO = userSO.retrieveUserListByRole(userReqDTO);

				if(userResDTO.getUserList()!=null && userResDTO.getUserList().size()>0){
					for(PmUser user :userResDTO.getUserList()){
						log.info("user.getLoginId():"+user.getLoginId());
						reqDTO.getCdAdminList().add(user.getLoginId());
					}
				}else{
					throw new EpException("User no found!");
				}

			}			
			else if (face.hasRoleUser(CmConst.CODIFICATION_OFFICER)) {
				log.info("Tracking diary for UNSPSC Code Recomended To Supplier by CODIFICATION_OFFICER");

				statusReqDTO.setStatusId(15013L);
				StatusResDTO statusResDTO = statusSO.retrieveStatusByStatusId(statusReqDTO);
				actionDesc = "Kod Item "+reqDTO.getRequestCode().getDocNo()+" telah disyorkan oleh "+reqDTO.getUserName();
				reqDTO.setTrackingDairyVO(CmApplicationUtil.assignTrackingDiaryValuesForUNSPSCExtension(actionDesc, statusResDTO.getPmStatus()!=null?statusResDTO.getPmStatus(): new PmStatus(),CmConst.CU_DocType));

				userReqDTO.setRoleCode(RoleCode.CD_COMMITTEE.value());
				UserResDTO userResDTO = userSO.retrieveUserListByRole(userReqDTO);

				if(userResDTO.getUserList()!=null && userResDTO.getUserList().size()>0){
					for(PmUser user :userResDTO.getUserList()){
						log.info("user.getLoginId():"+user.getLoginId());
						reqDTO.getCdcList().add(user.getLoginId());
					}
				}else{
					throw new EpException("User no found!");
				}
			}else{
				statusReqDTO.setStatusId(15015L);
				StatusResDTO statusResDTO = statusSO.retrieveStatusByStatusId(statusReqDTO);
				actionDesc = "Kod Item "+reqDTO.getRequestCode().getDocNo()+" telah disyorkan oleh "+reqDTO.getUserName();
				reqDTO.setTrackingDairyVO(CmApplicationUtil.assignTrackingDiaryValuesForUNSPSCExtension(actionDesc, statusResDTO.getPmStatus()!=null?statusResDTO.getPmStatus(): new PmStatus(),CmConst.CU_DocType));
				userReqDTO.setRoleCode(RoleCode.CD_OFFICER.value());
			}
		}
	}
	public String selectSubmitRecommend(String action) {


		this.getManagedBean().getRequestCode().setCmRecommendCodes(null);
		if(this.getManagedBean().getSelectedRecommendCode().getUnspscTitle()!=null && this.getManagedBean().getSelectedRecommendCode().getUnspscCode()!=null){
			this.getManagedBean().getRequestCode().getCmRecommendCodes().add(this.getManagedBean().getSelectedRecommendCode());
		}
		log.info("getUnspscTitle:"+this.getManagedBean().getSelectedRecommendCode().getUnspscTitle());

		System.out
		.println("this.getManagedBean().getRequestCode().getCmRecommendCodes().size()"
				+ this.getManagedBean().getRequestCode()
				.getCmRecommendCodes().size());
		if (this.getManagedBean().getRequestCode().getCmRecommendCodes()
				.size() > 1 || this.getManagedBean().getRequestCode().getCmRecommendCodes()
				.size() < 1) {
			System.out
			.println("***************more than 1 or no select*****************");
			FacesContext facesContext = FacesContext.getCurrentInstance();

			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(
									MessageConst.ERROR_715006,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));

			return "selectRecommendUnspscCode";
		}

		CodificationBackingBean codeBB = new CodificationBackingBean();
		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
				.getInstance()
				.getServiceObject("cm", ICodificationSO.class);
		FacesScopes face = new FacesScopes();
		Object token = face.getBPMAuthenticationCtxToken();

		CodificationReqDTO reqDTO = new CodificationReqDTO();
		log.info("new:"
				+ this.getManagedBean().getRequestCode().isBrandNew());

		reqDTO.setToken(token);
		reqDTO.setTaskId(this.getManagedBean().getTaskId());
		reqDTO.setOutCome(action);
		reqDTO.setProcessId(CDProcessNameConst.UNSPSC_CODE_REQUEST_PROCESS_ID);

		reqDTO.setRequestCode(this.getManagedBean().getRequestCode());

		CodificationResDTO resDTO = null;

		log.info("***update***");
		log.info("size:"
				+ reqDTO.getRequestCode().getCmRecommendCodes().size());

		log.info("tracking diary for Catalog_Admin:selectSubmitRecommend");
		reqDTO.setTrackingStatus(true);

		IStatusSO statusSO = (IStatusSO) BusinessDelegate
				.getInstance()
				.getServiceObject("pm", IStatusSO.class);

		StatusReqDTO statusReqDTO = new StatusReqDTO();
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		statusReqDTO.setStatusId(15015L);
		StatusResDTO statusResDTO = statusSO.retrieveStatusByStatusId(statusReqDTO);
		String actionDesc ="Kod Item "+this.getManagedBean().getRequestCode().getDocNo()+" telah disyorkan oleh "+(pmUser.getUserName()!=null?pmUser.getUserName():"SYS");
		reqDTO.setTrackingDairyVO(CmApplicationUtil.assignTrackingDiaryValuesForUNSPSCExtension(actionDesc, statusResDTO.getPmStatus()!=null?statusResDTO.getPmStatus():new PmStatus(),CmConst.CU_DocType));

		resDTO = unspscSO.updateCodificationRequest(reqDTO);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null,
						MessageUtils.getMessage(MessageConst.INFO_710001,
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));


		return "BPMtesting";

	}

	public void uploadUNSPSC(FileUploadEvent event) throws Exception {

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);

		UploadedFile item = event.getFile();

		FileInputStream file = (FileInputStream) item.getInputstream();

		HSSFWorkbook workbook = new HSSFWorkbook(file);

		CodificationReqDTO reqDTO = new CodificationReqDTO();
		HSSFSheet segment = workbook.getSheetAt(2);

		log.info("*******segment***********");

		long key = 0;
		long code = 0;
		String title = null;
		String desc = null;

		int lastRowNum = segment.getLastRowNum() + 1;
		for (int i = 1; i < lastRowNum; i++) {
			CmUnspsc unspsc = new CmUnspsc();
			Row row = segment.getRow(i);

			if (row.getCell(0) != null) {
				key = (long) row.getCell(0).getNumericCellValue();
				unspsc.setUnspscKey(key);

			}

			if (row.getCell(1) != null) {
				code = (long) row.getCell(1).getNumericCellValue();

				unspsc.setUnspscCode(String.valueOf(code));
			}

			if (row.getCell(2) != null) {
				title = row.getCell(2).getStringCellValue();
				unspsc.setUnspscTitle(title);

			}

			if (row.getCell(3) != null) {
				desc = row.getCell(3).getStringCellValue();
				unspsc.setUnspscDescription(desc);

			}

			unspsc.setUnspscLevel(0);
			reqDTO.setCmUnspsc(unspsc);
			reqDTO.getUnspscList().add(reqDTO.getCmUnspsc());

		}

		CodificationResDTO resDTO = unspscSO.saveUnspsc(reqDTO);
		log.info("*******end Family***********");

		// /////////////////////////////////////////////////////////////////////

		HSSFSheet family = workbook.getSheetAt(3);

		long familyKey = 0;
		long familyCode = 0;
		String familyTitle = null;
		String familyDesc = null;

		int lastRowNumFamily = family.getLastRowNum() + 1;
		for (int i = 1; i < lastRowNumFamily; i++) {
			CmUnspsc unspsc = new CmUnspsc();
			Row row = family.getRow(i);

			if (row.getCell(0) != null) {
				familyKey = (long) row.getCell(0).getNumericCellValue();

				unspsc.setUnspscKey(familyKey);
			}

			if (row.getCell(1) != null) {
				familyCode = (long) row.getCell(1).getNumericCellValue();

				String parentCode = getParentCode(familyCode);

				unspsc.setUnspscCode(String.valueOf(familyCode));
				unspsc.setUnspscParentCode(parentCode);

			}

			if (row.getCell(2) != null) {
				familyTitle = row.getCell(2).getStringCellValue();

				unspsc.setUnspscTitle(familyTitle);
			}

			if (row.getCell(3) != null) {
				familyDesc = row.getCell(3).getStringCellValue();
				unspsc.setUnspscDescription(familyDesc);
			}

			unspsc.setUnspscLevel(1);
			reqDTO.setCmUnspsc(unspsc);
			reqDTO.getFamilyList().add(reqDTO.getCmUnspsc());

		}
		resDTO = unspscSO.saveFamilyList(reqDTO);

		// /////////////////////////////////////////////////////////////////////

		HSSFSheet classes = workbook.getSheetAt(4);

		long classKey = 0;
		long classCode = 0;
		String classTitle = null;
		String classDesc = null;

		int lastRowNumClass = classes.getLastRowNum() + 1;
		for (int i = 1; i < lastRowNumClass; i++) {
			CmUnspsc unspsc = new CmUnspsc();
			Row row = classes.getRow(i);

			if (row.getCell(0) != null) {
				classKey = (long) row.getCell(0).getNumericCellValue();
				unspsc.setUnspscKey(classKey);

			}

			if (row.getCell(1) != null) {

				classCode = (long) row.getCell(1).getNumericCellValue();

				String parentCode = getParentCode(classCode);

				unspsc.setUnspscCode(String.valueOf(classCode));
				unspsc.setUnspscParentCode(parentCode);
			}

			if (row.getCell(2) != null) {
				classTitle = row.getCell(2).getStringCellValue();
				unspsc.setUnspscTitle(classTitle);
			}

			if (row.getCell(3) != null) {
				classDesc = row.getCell(3).getStringCellValue();
				unspsc.setUnspscDescription(classDesc);
			}

			unspsc.setUnspscLevel(2);
			reqDTO.setCmUnspsc(unspsc);
			reqDTO.getClassList().add(reqDTO.getCmUnspsc());

		}

		resDTO = unspscSO.saveClassList(reqDTO);

		HSSFSheet commodity = workbook.getSheetAt(5);

		long commodityKey = 0;
		long commodityCode = 0;
		String commodityTitle = null;
		String commodityDesc = null;

		int lastRowNumCommodity = commodity.getLastRowNum() + 1;

		for (int i = 1; i < lastRowNumCommodity; i++) {
			CmUnspsc unspsc = new CmUnspsc();
			Row row = commodity.getRow(i);

			if (row.getCell(6) != null
					&& row.getCell(6).toString().trim().length() > 0) {

				commodityKey = (long) row.getCell(6).getNumericCellValue();

				unspsc.setUnspscKey(commodityKey);

			}

			if (row.getCell(7) != null
					&& row.getCell(7).toString().trim().length() > 0) {

				commodityCode = (long) row.getCell(7).getNumericCellValue();

				String parentCode = getParentCode(commodityCode);

				unspsc.setUnspscCode(String.valueOf(commodityCode));
				unspsc.setUnspscParentCode(parentCode);
			}

			if (row.getCell(8) != null
					&& row.getCell(8).toString().trim().length() > 0) {
				commodityTitle = row.getCell(8).getStringCellValue();

				unspsc.setUnspscTitle(commodityTitle);

			}

			if (row.getCell(9) != null
					&& row.getCell(9).toString().trim().length() > 0) {
				commodityDesc = row.getCell(9).getStringCellValue();

				unspsc.setUnspscDescription(commodityDesc);

			}

			unspsc.setUnspscLevel(3);
			reqDTO.setCmUnspsc(unspsc);
			reqDTO.getCommodityList().add(reqDTO.getCmUnspsc());

		}

		resDTO = unspscSO.saveCommodityList(reqDTO);

		file.close();

	}

	public static String getParentCode(long code) {

		String parent = null;

		String check68 = (String) String.valueOf(code).substring(6, 8);
		String check46 = (String) String.valueOf(code).substring(4, 6);
		String check24 = (String) String.valueOf(code).substring(2, 4);

		if (!check68.equalsIgnoreCase("00")) {
			log.debug("commodity");
			parent = (String) String.valueOf(code).substring(0, 6) + "00";

			log.debug("parent:" + parent);
		} else {
			if (!check46.equalsIgnoreCase("00")) {
				log.debug("class");
				parent = (String) String.valueOf(code).substring(0, 4) + "0000";
				log.debug("parent:" + parent);
			} else {
				if (!check24.equalsIgnoreCase("00")) {
					log.debug("family");
					parent = (String) String.valueOf(code).substring(0, 2)
							+ "000000";
					log.debug("parent:" + parent);
				} else {
					log.debug("segment");
				}

			}

		}

		return parent;
	}

	public Long checkUnspscLevel(long code) {

		long level = 0;

		String check68 = (String) String.valueOf(code).substring(6, 8);
		String check46 = (String) String.valueOf(code).substring(4, 6);
		String check24 = (String) String.valueOf(code).substring(2, 4);

		if (!check68.equalsIgnoreCase("00")) {
			log.debug("commodity");
			level = 3;

		} else {
			if (!check46.equalsIgnoreCase("00")) {
				log.debug("class");
				level = 2;
			} else {
				if (!check24.equalsIgnoreCase("00")) {
					log.debug("family");
					level = 1;
				} else {
					log.debug("segment");
					level = 0;
				}

			}

		}

		return level;
	}

	public String navigateToSearch() {
		log.info("*****navigateToSearch******");

		if (this.getManagedBean().getRequestCode().getCmRecommendCodes() != null
				&& this.getManagedBean().getRequestCode().getCmRecommendCodes()
				.size() > 0) {

			FacesContext facesContext = FacesContext.getCurrentInstance();

			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.ERROR_715001,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));

			return "reviewUnspscCode?faces-redirect=true";
		}

		// clear search fileds and result
		CodificationBackingBean bb = new CodificationBackingBean();
		bb.getManagedBean().setAutoCompleteUnspscCode(new CmUnspsc());
		bb.getManagedBean().getTitleFilterBean().setSearchValue(null);
		bb.getManagedBean().getSearchDataModel().resetData();



		return "verifyUnspscSearch?faces-redirect=true";
	}

	public String navigateCodeAdminToSearch() {
		log.info("*****navigateCodeAdminToSearch******");

		return "verifyUnspscSearch?faces-redirect=true";
	}

	public String createTempCode() {

		log.info("ID:" + this.getManagedBean().getRequestCode().getRequestId());

		return "unspscSingleUpload?faces-redirect=true";
	}

	public String savetempCode(String outcome) {

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		FacesScopes face = new FacesScopes();
		Object token = face.getBPMAuthenticationCtxToken();
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		CodificationResDTO resDTO = null;
		CodificationBackingBean bb = new CodificationBackingBean();
		PmUser pmUser = new FacesScopes().getUserSessionBean();

		IStatusSO statusSO = (IStatusSO) BusinessDelegate
				.getInstance()
				.getServiceObject("pm", IStatusSO.class);

		StatusReqDTO statusReqDTO = new StatusReqDTO();



		if (outcome.equalsIgnoreCase("SUBMIT")) {
			log.info("***this.getManagedBean().isNewSegment()"
					+ this.getManagedBean().isNewSegment());

			if (this.getManagedBean().getSelectedLovUnspscSegment()
					.getUnspscCode().isEmpty()
					|| this.getManagedBean().getSelectedLovUnspscfamily()
					.getUnspscCode().isEmpty()
					|| this.getManagedBean().getSelectedLovUnspscClass()
					.getUnspscCode().isEmpty()
					|| this.getManagedBean().getSelectedLovUnspsc()
					.getUnspscCode().isEmpty()) {

				FacesContext
				.getCurrentInstance()
				.addMessage(
						null,
						new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								null,
								MessageUtils
								.getMessage(
										MessageConst.ERROR_715002,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes()
										.getUserLocale())));
				RequestContext.getCurrentInstance().execute("extensionConfirmation.hide();");
				return "reviewBeforeCreateTempCode";

			}





			boolean isDigitSegment = this.getManagedBean().getSelectedLovUnspscSegment().getUnspscCode().matches("\\d{8}"); 
			boolean isDigitFamily = this.getManagedBean().getSelectedLovUnspscfamily().getUnspscCode().matches("\\d{8}"); 
			boolean isDigitClass = this.getManagedBean().getSelectedLovUnspscClass().getUnspscCode().matches("\\d{8}"); 
			boolean isDigitCommodity = this.getManagedBean().getSelectedLovUnspsc().getUnspscCode().matches("\\d{8}"); 

			if(!(isDigitSegment && isDigitFamily && isDigitClass && isDigitCommodity)){

				FacesContext
				.getCurrentInstance()
				.addMessage(
						null,
						new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								null,
								MessageUtils
								.getMessage(
										MessageConst.ERROR_715066,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes()
										.getUserLocale())));
				RequestContext.getCurrentInstance().execute("extensionConfirmation.hide();");
				return "reviewBeforeCreateTempCode";

			}



			if (this.getManagedBean().getSelectedLovUnspscSegment()
					.isBrandNew()) {

				reqDTO.setUnspscCode(this.getManagedBean()
						.getSelectedLovUnspscSegment().getUnspscCode());
				resDTO = unspscSO.searchUnspscObj(reqDTO);
				if (resDTO.getUnspscObj() == null) {// so its new unspsc,
					// save it
					CmUnspsc unspsc = new CmUnspsc();
					unspsc.setUnspscCode(this.getManagedBean()
							.getSelectedLovUnspscSegment().getUnspscCode());
					unspsc.setUnspscKey(this.getManagedBean()
							.getSelectedLovUnspscSegment().getUnspscKey());
					unspsc.setUnspscTitle(this.getManagedBean()
							.getSelectedLovUnspscSegment().getUnspscTitle());
					unspsc.setUnspscTitleEng(this.getManagedBean()
							.getSelectedLovUnspscSegment().getUnspscTitleEng());
					unspsc.setUnspscDescription(this.getManagedBean()
							.getSelectedLovUnspscSegment()
							.getUnspscDescription());
					unspsc.setUnspscLevel(0);

					reqDTO.setCmUnspsc(unspsc);
					reqDTO.getUnspscList().add(reqDTO.getCmUnspsc());
				}else{
					FacesContext facesContext = FacesContext
							.getCurrentInstance();

					facesContext
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_ERROR,
									null,
									MessageUtils
									.getMessage(
											MessageConst.ERROR_716014, new String[] {this.getManagedBean().getSelectedLovUnspscSegment().getUnspscCode()},
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes()
											.getUserLocale())));
					RequestContext.getCurrentInstance().execute("extensionConfirmation.hide();");
					return "reviewBeforeCreateTempCode";


				}

				// resDTO = unspscSO.saveUnspsc(reqDTO);
			} else {
				reqDTO.setUnspscCode(this.getManagedBean()
						.getSelectedLovUnspscSegment().getUnspscCode());
				resDTO = unspscSO.searchUnspscObj(reqDTO);
				if (resDTO.getUnspscObj() == null) {// so its new unspsc,
					// save it
					CmUnspsc unspsc = new CmUnspsc();
					unspsc.setUnspscCode(this.getManagedBean()
							.getSelectedLovUnspscSegment().getUnspscCode());
					unspsc.setUnspscKey(this.getManagedBean()
							.getSelectedLovUnspscSegment().getUnspscKey());
					unspsc.setUnspscTitle(this.getManagedBean()
							.getSelectedLovUnspscSegment().getUnspscTitle());
					unspsc.setUnspscTitleEng(this.getManagedBean()
							.getSelectedLovUnspscSegment().getUnspscTitleEng());
					unspsc.setUnspscDescription(this.getManagedBean()
							.getSelectedLovUnspscSegment()
							.getUnspscDescription());
					unspsc.setUnspscLevel(0);

					reqDTO.setCmUnspsc(unspsc);
					reqDTO.getUnspscList().add(reqDTO.getCmUnspsc());
				}else{
					if(this.getManagedBean().getRequestCode().getReviewedTempCode()!=null){
						if(StringUtils.rightPad(this.getManagedBean().getSelectedLovUnspscSegment().getUnspscCode().substring(0,2),8,'0').equals(StringUtils.rightPad(this.getManagedBean().getRequestCode().getReviewedTempCode().substring(0,2),8,'0'))){
							resDTO.getUnspscObj().setUnspscTitle(this.getManagedBean()
									.getSelectedLovUnspscSegment().getUnspscTitle());
							resDTO.getUnspscObj().setUnspscTitleEng(this.getManagedBean()
									.getSelectedLovUnspscSegment().getUnspscTitleEng());
							resDTO.getUnspscObj().setUnspscDescription(this.getManagedBean()
									.getSelectedLovUnspscSegment().getUnspscDescription());
							reqDTO.getUnspscList().add(resDTO.getUnspscObj());
						}
					}
				}
			}
			// //// start family

			if (this.getManagedBean().getSelectedLovUnspscfamily()
					.isBrandNew()) {

				reqDTO.setUnspscCode(this.getManagedBean()
						.getSelectedLovUnspscfamily().getUnspscCode());
				resDTO = unspscSO.searchUnspscObj(reqDTO);
				if (resDTO.getUnspscObj() == null) {// so its new unspsc,
					// save it
					CmUnspsc unspsc = new CmUnspsc();
					System.out
					.println("this.getManagedBean().getSelectedLovUnspscfamily().getUnspscCode()"
							+ this.getManagedBean()
							.getSelectedLovUnspscfamily()
							.getUnspscCode());
					unspsc.setUnspscCode(this.getManagedBean()
							.getSelectedLovUnspscfamily().getUnspscCode());
					unspsc.setUnspscKey(this.getManagedBean()
							.getSelectedLovUnspscfamily().getUnspscKey());
					unspsc.setUnspscTitle(this.getManagedBean()
							.getSelectedLovUnspscfamily().getUnspscTitle());
					unspsc.setUnspscTitleEng(this.getManagedBean()
							.getSelectedLovUnspscfamily().getUnspscTitleEng());
					unspsc.setUnspscDescription(this.getManagedBean()
							.getSelectedLovUnspscfamily()
							.getUnspscDescription());
					unspsc.setUnspscLevel(1);
					unspsc.setUnspscParentCode(this.getManagedBean()
							.getSelectedLovUnspscSegment().getUnspscCode());

					reqDTO.setCmUnspsc(unspsc);
					reqDTO.getFamilyList().add(reqDTO.getCmUnspsc());
					// resDTO = unspscSO.saveFamilyList(reqDTO);
				}else{

					FacesContext facesContext = FacesContext
							.getCurrentInstance();

					facesContext
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_ERROR,
									null,
									MessageUtils
									.getMessage(
											MessageConst.ERROR_716014, new String[] {this.getManagedBean().getSelectedLovUnspscfamily().getUnspscCode()},
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes()
											.getUserLocale())));
					RequestContext.getCurrentInstance().execute("extensionConfirmation.hide();");
					return "reviewBeforeCreateTempCode";
				}
			} else {
				reqDTO.setUnspscCode(this.getManagedBean()
						.getSelectedLovUnspscfamily().getUnspscCode());
				resDTO = unspscSO.searchUnspscObj(reqDTO);
				if (resDTO.getUnspscObj() == null) {// so its new unspsc,
					// save it
					CmUnspsc unspsc = new CmUnspsc();
					unspsc.setUnspscCode(this.getManagedBean()
							.getSelectedLovUnspscfamily().getUnspscCode());
					unspsc.setUnspscKey(this.getManagedBean()
							.getSelectedLovUnspscfamily().getUnspscKey());
					unspsc.setUnspscTitle(this.getManagedBean()
							.getSelectedLovUnspscfamily().getUnspscTitle());
					unspsc.setUnspscTitleEng(this.getManagedBean()
							.getSelectedLovUnspscfamily().getUnspscTitleEng());
					unspsc.setUnspscDescription(this.getManagedBean()
							.getSelectedLovUnspscfamily()
							.getUnspscDescription());
					unspsc.setUnspscLevel(1);
					unspsc.setUnspscParentCode(this.getManagedBean()
							.getSelectedLovUnspscSegment().getUnspscCode());

					reqDTO.setCmUnspsc(unspsc);
					reqDTO.getFamilyList().add(reqDTO.getCmUnspsc());
				}else{
					if(this.getManagedBean().getRequestCode().getReviewedTempCode()!=null){
						if( StringUtils.rightPad(this.getManagedBean().getSelectedLovUnspscfamily().getUnspscCode().substring(0,4),8,'0').equals(StringUtils.rightPad(this.getManagedBean().getRequestCode().getTempCode().substring(0,2).concat( this.getManagedBean().getRequestCode().getReviewedTempCode().substring(2,4)),8,'0'))){
							resDTO.getUnspscObj().setUnspscTitle(this.getManagedBean()
									.getSelectedLovUnspscfamily().getUnspscTitle());
							resDTO.getUnspscObj().setUnspscTitleEng(this.getManagedBean()
									.getSelectedLovUnspscfamily().getUnspscTitleEng());
							resDTO.getUnspscObj().setUnspscDescription(this.getManagedBean()
									.getSelectedLovUnspscfamily().getUnspscDescription());
							reqDTO.getFamilyList().add(resDTO.getUnspscObj());
						}
					}
				}
			}
			// ///start class

			if (this.getManagedBean().getSelectedLovUnspscClass()
					.isBrandNew()) {

				reqDTO.setUnspscCode(this.getManagedBean()
						.getSelectedLovUnspscClass().getUnspscCode());
				resDTO = unspscSO.searchUnspscObj(reqDTO);
				if (resDTO.getUnspscObj() == null) {// so its new unspsc,
					// save it
					CmUnspsc unspsc = new CmUnspsc();
					System.out
					.println("this.getManagedBean().getSelectedLovUnspscClass().getUnspscCode()"
							+ this.getManagedBean()
							.getSelectedLovUnspscClass()
							.getUnspscCode());
					unspsc.setUnspscCode(this.getManagedBean()
							.getSelectedLovUnspscClass().getUnspscCode());
					unspsc.setUnspscKey(this.getManagedBean()
							.getSelectedLovUnspscClass().getUnspscKey());
					unspsc.setUnspscTitle(this.getManagedBean()
							.getSelectedLovUnspscClass().getUnspscTitle());
					unspsc.setUnspscTitleEng(this.getManagedBean()
							.getSelectedLovUnspscClass().getUnspscTitleEng());
					unspsc.setUnspscDescription(this.getManagedBean()
							.getSelectedLovUnspscClass().getUnspscDescription());
					unspsc.setUnspscLevel(2);
					unspsc.setUnspscParentCode(this.getManagedBean()
							.getSelectedLovUnspscfamily().getUnspscCode());

					reqDTO.setCmUnspsc(unspsc);
					reqDTO.getClassList().add(reqDTO.getCmUnspsc());
					// resDTO = unspscSO.saveClassList(reqDTO);
				}else{

					FacesContext facesContext = FacesContext
							.getCurrentInstance();

					facesContext
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_ERROR,
									null,
									MessageUtils
									.getMessage(
											MessageConst.ERROR_716014, new String[] {this.getManagedBean().getSelectedLovUnspscClass().getUnspscCode()},
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes()
											.getUserLocale())));
					RequestContext.getCurrentInstance().execute("extensionConfirmation.hide();");
					return "reviewBeforeCreateTempCode";
				}

			} else {
				reqDTO.setUnspscCode(this.getManagedBean()
						.getSelectedLovUnspscClass().getUnspscCode());
				resDTO = unspscSO.searchUnspscObj(reqDTO);
				if (resDTO.getUnspscObj() == null) {// so its new unspsc,
					// save it
					CmUnspsc unspsc = new CmUnspsc();
					unspsc.setUnspscCode(this.getManagedBean()
							.getSelectedLovUnspscClass().getUnspscCode());
					unspsc.setUnspscKey(this.getManagedBean()
							.getSelectedLovUnspscClass().getUnspscKey());
					unspsc.setUnspscTitle(this.getManagedBean()
							.getSelectedLovUnspscClass().getUnspscTitle());
					unspsc.setUnspscTitleEng(this.getManagedBean()
							.getSelectedLovUnspscClass().getUnspscTitleEng());
					unspsc.setUnspscDescription(this.getManagedBean()
							.getSelectedLovUnspscClass()
							.getUnspscDescription());
					unspsc.setUnspscLevel(2);
					unspsc.setUnspscParentCode(this.getManagedBean()
							.getSelectedLovUnspscfamily().getUnspscCode());

					reqDTO.setCmUnspsc(unspsc);
					reqDTO.getClassList().add(reqDTO.getCmUnspsc());
				}else{
					if(this.getManagedBean().getRequestCode().getReviewedTempCode()!=null){
						if(StringUtils.rightPad(this.getManagedBean().getSelectedLovUnspscClass().getUnspscCode().substring(0,6),8,'0').equals(StringUtils.rightPad(this.getManagedBean().getRequestCode().getTempCode().substring(0,4).concat(this.getManagedBean().getRequestCode().getReviewedTempCode().substring(4,6)),8,'0'))){
							resDTO.getUnspscObj().setUnspscTitle(this.getManagedBean()
									.getSelectedLovUnspscClass().getUnspscTitle());
							resDTO.getUnspscObj().setUnspscTitleEng(this.getManagedBean()
									.getSelectedLovUnspscClass().getUnspscTitleEng());
							resDTO.getUnspscObj().setUnspscDescription(this.getManagedBean()
									.getSelectedLovUnspscClass().getUnspscDescription());
							reqDTO.getClassList().add(resDTO.getUnspscObj());
						}
					}
				}
			}
			// //start commodity
			System.out
			.println(".....................this.getManagedBean().getSelectedLovUnspsc().isBrandNew()="
					+ this.getManagedBean().getSelectedLovUnspsc()
					.isBrandNew());
			if (this.getManagedBean().getSelectedLovUnspsc().isBrandNew()) {
				reqDTO.setUnspscCode(this.getManagedBean()
						.getSelectedLovUnspsc().getUnspscCode());
				resDTO = unspscSO.searchUnspscObj(reqDTO);
				if (resDTO.getUnspscObj() == null) {// so its new unspsc,
					// save it
					CmUnspsc unspsc = new CmUnspsc();
					unspsc.setUnspscCode(this.getManagedBean()
							.getSelectedLovUnspsc().getUnspscCode());
					unspsc.setUnspscKey(this.getManagedBean()
							.getSelectedLovUnspsc().getUnspscKey());
					unspsc.setUnspscTitle(this.getManagedBean()
							.getSelectedLovUnspsc().getUnspscTitle());
					unspsc.setUnspscTitleEng(this.getManagedBean()
							.getSelectedLovUnspsc().getUnspscTitleEng());
					unspsc.setUnspscDescription(this.getManagedBean()
							.getSelectedLovUnspsc().getUnspscDescription());
					unspsc.setUnspscLevel(3);
					unspsc.setUnspscParentCode(this.getManagedBean()
							.getSelectedLovUnspscClass().getUnspscCode());

					reqDTO.setCmUnspsc(unspsc);
					reqDTO.getCommodityList().add(reqDTO.getCmUnspsc());

					// resDTO = unspscSO.saveCommodityList(reqDTO);
				}else{

					FacesContext facesContext = FacesContext
							.getCurrentInstance();

					facesContext
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_ERROR,
									null,
									MessageUtils
									.getMessage(
											MessageConst.ERROR_716014, new String[] {this.getManagedBean().getSelectedLovUnspsc().getUnspscCode()},
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes()
											.getUserLocale())));
					RequestContext.getCurrentInstance().execute("extensionConfirmation.hide();");
					return "reviewBeforeCreateTempCode";
				}
			} else {
				reqDTO.setUnspscCode(this.getManagedBean()
						.getSelectedLovUnspsc().getUnspscCode());
				resDTO = unspscSO.searchUnspscObj(reqDTO);
				if (resDTO.getUnspscObj() == null) {// so its new unspsc,
					// save it
					CmUnspsc unspsc = new CmUnspsc();
					unspsc.setUnspscCode(this.getManagedBean()
							.getSelectedLovUnspsc().getUnspscCode());
					unspsc.setUnspscKey(this.getManagedBean()
							.getSelectedLovUnspsc().getUnspscKey());
					unspsc.setUnspscTitle(this.getManagedBean()
							.getSelectedLovUnspsc().getUnspscTitle());
					unspsc.setUnspscTitleEng(this.getManagedBean()
							.getSelectedLovUnspsc().getUnspscTitleEng());
					unspsc.setUnspscDescription(this.getManagedBean()
							.getSelectedLovUnspsc().getUnspscDescription());
					unspsc.setUnspscLevel(3);
					unspsc.setUnspscParentCode(this.getManagedBean()
							.getSelectedLovUnspscClass().getUnspscCode());

					reqDTO.setCmUnspsc(unspsc);
					reqDTO.getCommodityList().add(reqDTO.getCmUnspsc());
				}else{
					if(this.getManagedBean().getRequestCode().getReviewedTempCode()!=null){
						if( this.getManagedBean().getSelectedLovUnspsc().getUnspscCode().equals(this.getManagedBean().getRequestCode().getTempCode().substring(0,6).concat(this.getManagedBean().getRequestCode().getReviewedTempCode().substring(6,8)))){
							
							resDTO.getUnspscObj().setUnspscTitle(this.getManagedBean()
									.getSelectedLovUnspsc().getUnspscTitle());
							resDTO.getUnspscObj().setUnspscTitleEng(this.getManagedBean()
									.getSelectedLovUnspsc().getUnspscTitleEng());
							resDTO.getUnspscObj().setUnspscDescription(this.getManagedBean()
									.getSelectedLovUnspsc().getUnspscDescription());
							reqDTO.getCommodityList().add(resDTO.getUnspscObj());
						}
					}else{
					FacesContext facesContext = FacesContext
							.getCurrentInstance();

					facesContext
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_ERROR,
									null,
									MessageUtils
									.getMessage(
											MessageConst.ERROR_716014, new String[] {this.getManagedBean().getSelectedLovUnspsc().getUnspscCode()},
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes()
											.getUserLocale())));
					RequestContext.getCurrentInstance().execute("extensionConfirmation.hide();");
					return "reviewBeforeCreateTempCode";

					}
				}
			}

			System.out
			.println("...................	reqDTO.getCommodityList().size="
					+ reqDTO.getCommodityList().size());

			String check68 = (String) String.valueOf(
					this.getManagedBean().getSelectedLovUnspsc()
					.getUnspscCode()).substring(6, 8);
			String check46 = (String) String.valueOf(
					this.getManagedBean().getSelectedLovUnspscClass()
					.getUnspscCode()).substring(4, 6);
			String check24 = (String) String.valueOf(
					this.getManagedBean().getSelectedLovUnspscfamily()
					.getUnspscCode()).substring(2, 4);
			String check02 = (String) String.valueOf(
					this.getManagedBean().getSelectedLovUnspscSegment()
					.getUnspscCode()).substring(0, 2);

			String se = reqDTO.getUnspscList().size() > 0 ?  reqDTO.getUnspscList().get(0).getUnspscCode().substring(0,2) :"00";
			String fa = reqDTO.getFamilyList().size() > 0 ?  reqDTO.getFamilyList().get(0).getUnspscCode().substring(2,4) :"00";
			String cl = reqDTO.getClassList().size() > 0 ?  reqDTO.getClassList().get(0).getUnspscCode().substring(4,6) :"00";
			String co = reqDTO.getCommodityList().size() > 0 ?  reqDTO.getCommodityList().get(0).getUnspscCode().substring(6,8) :"00";

			String tempCode = check02 + check24 + check46 + check68;
			String reviewedTempCode = se+fa+cl+co;

			this.getManagedBean().getRequestCode().setTempCode(tempCode);
			this.getManagedBean().getRequestCode().setReviewedTempCode(reviewedTempCode);
			this.getManagedBean().getRequestCode().setTempTitle(this.getManagedBean().getSelectedLovUnspsc().getUnspscTitle());

			if (bb.productCode(tempCode)) {

				this.getManagedBean().getRequestCode().setRequestType(true);
				this.getManagedBean().getRequestCode()
				.setRequestBrand(true);
				this.getManagedBean().getRequestCode()
				.setRequestColor(true);
				this.getManagedBean().getRequestCode()
				.setRequestMeasurement(true);
			}

			reqDTO.setRequestCode(this.getManagedBean().getRequestCode());
			reqDTO.setToken(token);
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setOutCome(outcome);
			log.info("**********savetempCode************");

			//reqDTO.setCodeCommittees("user003,user051");
			reqDTO.setService(!bb.productCode(tempCode));

			if (!(this.getManagedBean().getSelectedLovUnspsc()
					.getUnspscTitle() != null && this.getManagedBean()
					.getSelectedLovUnspsc().getUnspscTitle().trim()
					.length() > 0)
					|| !(this.getManagedBean().getSelectedLovUnspscClass()
							.getUnspscTitle() != null && this
							.getManagedBean().getSelectedLovUnspscClass()
							.getUnspscTitle().trim().length() > 0)
							|| !(this.getManagedBean().getSelectedLovUnspscfamily()
									.getUnspscTitle() != null && this
									.getManagedBean().getSelectedLovUnspscfamily()
									.getUnspscTitle().trim().length() > 0)
									|| !(this.getManagedBean()
											.getSelectedLovUnspscSegment().getUnspscTitle() != null && this
											.getManagedBean().getSelectedLovUnspscSegment()
											.getUnspscTitle().trim().length() > 0) 
							
											|| !(this.getManagedBean().getSelectedLovUnspsc()
													.getUnspscTitleEng() != null && this.getManagedBean()
													.getSelectedLovUnspsc().getUnspscTitleEng().trim()
													.length() > 0)
													|| !(this.getManagedBean().getSelectedLovUnspscClass()
															.getUnspscTitleEng() != null && this
															.getManagedBean().getSelectedLovUnspscClass()
															.getUnspscTitleEng().trim().length() > 0)
															|| !(this.getManagedBean().getSelectedLovUnspscfamily()
																	.getUnspscTitleEng() != null && this
																	.getManagedBean().getSelectedLovUnspscfamily()
																	.getUnspscTitleEng().trim().length() > 0)
																	|| !(this.getManagedBean()
																			.getSelectedLovUnspscSegment().getUnspscTitleEng() != null && this
																			.getManagedBean().getSelectedLovUnspscSegment()
																			.getUnspscTitleEng().trim().length() > 0)) {

				FacesContext facesContext = FacesContext
						.getCurrentInstance();

				facesContext
				.addMessage(
						null,
						new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								null,
								MessageUtils
								.getMessage(
										MessageConst.ERROR_715008,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes()
										.getUserLocale())));
				RequestContext.getCurrentInstance().execute("extensionConfirmation.hide();");
				return "reviewBeforeCreateTempCode";

			}
			// checking hierarchy of segment, family, class and commodity
			if(!validateUnspscCodeHierarchy(this.getManagedBean()
					.getSelectedLovUnspscSegment().getUnspscCode(), this
					.getManagedBean().getSelectedLovUnspscfamily()
					.getUnspscCode(), this.getManagedBean()
					.getSelectedLovUnspscClass().getUnspscCode(), this
					.getManagedBean().getSelectedLovUnspsc()
					.getUnspscCode())){
				return "reviewBeforeCreateTempCode";
			}
			log.info("tracking diary for Codification_Officer:savetempCode");
			reqDTO.setTrackingStatus(true);

			statusReqDTO.setStatusId(15004L);
			String actionDesc ="Kod Item "+this.getManagedBean().getRequestCode().getDocNo()+" telah diserah kepada CDC Working Committee untuk kelulusan. ";
			if(reqDTO.isService()){
				statusReqDTO.setStatusId( this.getManagedBean().isRenderV2() ? 15005L :15004L);
				actionDesc =  this.getManagedBean().isRenderV2() ? 
						"Kod Item "+this.getManagedBean().getRequestCode().getDocNo()+" telah diiklankan oleh "+pmUser.getUserName() :
						"Kod Item "+this.getManagedBean().getRequestCode().getDocNo()+" telah diserah kepada Pentadbir Kodifikasi untuk diiklankan." ;
			}
			StatusResDTO statusResDTO = statusSO.retrieveStatusByStatusId(statusReqDTO);
			reqDTO.setTrackingDairyVO(CmApplicationUtil.assignTrackingDiaryValuesForUNSPSCExtension(actionDesc, statusResDTO.getPmStatus()!=null?statusResDTO.getPmStatus():new PmStatus(),CmConst.CE_DocType));	

			UserReqDTO userReqDTO = new UserReqDTO();
			
			if(reqDTO.isService() && !this.getManagedBean().isRenderV2()){
				userReqDTO.setRoleCode(RoleCode.CD_OFFICER.value());
				UserResDTO userResDTO = userSO.retrieveUserListByRole(userReqDTO);
				if(userResDTO.getUserList()!=null && userResDTO.getUserList().size()>0){
					for(PmUser user :userResDTO.getUserList()){
						log.info("user.getLoginId():"+user.getLoginId());
						reqDTO.getCdAdminList().add(user.getLoginId());
					}
				}else{
					throw new EpException("User no found!");
				}
			}else{
				userReqDTO.setRoleCode(RoleCode.CD_COMMITTEE.value());
				UserResDTO userResDTO = userSO.retrieveUserListByRole(userReqDTO);
				if(userResDTO.getUserList()!=null && userResDTO.getUserList().size()>0){
					for(PmUser user :userResDTO.getUserList()){
						log.info("user.getLoginId():"+user.getLoginId());
						reqDTO.getCdcList().add(user.getLoginId());
					}
				}else{
					throw new EpException("User no found!");
				}
			}

			if(!this.isAllSupplier()){
				reqDTO.setRemark(this.getManagedBean().getSelectedRemarks().get(0));
			}			
			if(reqDTO.isService() && this.getManagedBean().isRenderV2()){
				log.info("Send for publishing.");
				reqDTO.setPojo(this.getManagedBean().getPayLoad());
				reqDTO.setToken(token);
				resDTO = unspscSO.publishCodificationRequest(reqDTO);
			}else{
				log.info("Send to CDC for ext endorsement.");
				resDTO = unspscSO.updateCodificationRequest(reqDTO);
			}
		} else if (outcome.equalsIgnoreCase("RECOMMEND_TO_SUPPLIER")) {

			if (!(this.getManagedBean().getRequestCode()
					.getCmRecommendCodes() != null && this.getManagedBean()
					.getRequestCode().getCmRecommendCodes().size() > 0)) {
				log.info("**********warning***************");

				FacesContext facesContext = FacesContext
						.getCurrentInstance();

				facesContext
				.addMessage(
						null,
						new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								null,
								MessageUtils
								.getMessage(
										MessageConst.ERROR_715003,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes()
										.getUserLocale())));
				RequestContext.getCurrentInstance().execute("extensionConfirmation.hide();");
				return "reviewBeforeCreateTempCode";
			}

			for (CmRecommendCode code : this.getManagedBean()
					.getRequestCode().getCmRecommendCodes()) {

				code.setCmRequestCode(this.getManagedBean()
						.getRequestCode());

			}

			reqDTO.setRequestCode(this.getManagedBean().getRequestCode());
			reqDTO.setToken(token);
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setOutCome(outcome);
			log.info("tracking diary for UNSPSC Code Recomended to Supplier by Codification_Officer");
			reqDTO.setTrackingStatus(true);


			statusReqDTO.setStatusId(15013L);
			StatusResDTO statusResDTO = statusSO.retrieveStatusByStatusId(statusReqDTO);

			String actionDesc = "Kod Item "+reqDTO.getRequestCode().getDocNo()+" telah disyorkan oleh "+(pmUser.getUserName()!=null?pmUser.getUserName():"SYS");
			reqDTO.setTrackingDairyVO(CmApplicationUtil.assignTrackingDiaryValuesForUNSPSCExtension(actionDesc, statusResDTO.getPmStatus()!=null?statusResDTO.getPmStatus():new PmStatus(),CmConst.CU_DocType));	
			
			if(!this.isAllSupplier()){
				reqDTO.setRemark(this.getManagedBean().getSelectedRemarks().get(0));
			}			
			resDTO = unspscSO.updateCodificationRequest(reqDTO);

		}

		//20190830@emy - CR15028 - start
		
		if (outcome.equalsIgnoreCase("SUBMIT")) {
			
			
			if(reqDTO.isService() && this.getManagedBean().isRenderV2()){
				
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
								.getMessage(MessageConst.INFO_710017,new String[] {reqDTO.getRequestCode().getDocNo()},
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
			}else{
				
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
								.getMessage(MessageConst.INFO_710019,new String[] {resDTO.getRequestCode().getDocNo()},
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
			}
			
		}
		else if (outcome.equalsIgnoreCase("RECOMMEND_TO_SUPPLIER")){
			
			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
							.getMessage(MessageConst.INFO_710017,new String[] {resDTO.getRequestCode().getDocNo()},
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
			
		}
		
		//20190820@emy - CR15028 - end
		
		RequestContext.getCurrentInstance().execute("extensionConfirmation.hide();");
		
		return "BPMtesting";

	}

	public boolean validateUnspscCodeHierarchy(String segnment, String family,
			String classC, String comodity) {
		String segmentIdentifier = segnment.substring(0, 2);
		String familyIdentifier = family.substring(0, 4);
		String classIdentifier = classC.substring(0, 6);

		FacesContext facesContext = FacesContext.getCurrentInstance();

		if (!family.startsWith(segmentIdentifier)) {
			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.ERROR_716011,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
			return false;
		} else if (!classC.startsWith(familyIdentifier)) {
			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.ERROR_716011,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
			return false;
		} else if (!comodity.startsWith(classIdentifier)) {
			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.ERROR_716011,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
			return false;
		}
		//checking codes validity
		if(!segnment.substring(2).equals("000000")){
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.ERROR_716013,
									new String[] { ResourceBundleUtils.getMessage("lbl.cm.segment",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()) },
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
			return false;
		}
		if(!family.substring(4).equals("0000")){
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.ERROR_716013,
									new String[] { ResourceBundleUtils.getMessage("lbl.cm.family",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()) },
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
			return false;
		}
		if(!classC.substring(6).equals("00")){
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.ERROR_716013,
									new String[] { ResourceBundleUtils.getMessage("lbl.cm.class",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()) },
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
			return false;
		}
		// checking for not redundant segment, familiy, class and commodity
		segmentIdentifier = segmentIdentifier + "00";
		if (segmentIdentifier.equals(familyIdentifier)) {
			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.ERROR_716012,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
			return false;
		}
		familyIdentifier = familyIdentifier + "00";
		if (familyIdentifier.equals(classIdentifier)) {
			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.ERROR_716012,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
			return false;
		}
		classIdentifier = classIdentifier + "00";
		if (classIdentifier.equals(comodity)) {
			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.ERROR_716012,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
			return false;
		}

		return true;

	}

	public String createExtCode() {

		log.info("**************************");
		// this.getManagedBean().getRequestCode()

		return "extensionCodeSingleCreation.xhtml";
	}

	public void addMoreRequest() {
		log.info(".................addMoreRequest...................");
		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);

		CodificationReqDTO reqDTO = new CodificationReqDTO();
		// reqDTO.setParentCode(this.getManagedBean().getUnspscCode());
		reqDTO.setUnspscCode(this.getManagedBean().getSelectedLovUnspsc()
				.getUnspscCode());

		CodificationResDTO resDTO = codificationSO.searchCommodityObj(reqDTO);
		this.getManagedBean().setEditMode(false);
		this.getManagedBean()
		.getRequestExtCode()
		.setTempCode(
				this.getManagedBean().getSelectedLovUnspsc()
				.getUnspscCode());
		this.getManagedBean()
		.getRequestExtCode()
		.setTempTitle(
				this.getManagedBean().getSelectedLovUnspsc()
				.getUnspscTitle());
		this.getManagedBean().setTypeList(resDTO.getCommodityObj().getCmType());
		this.getManagedBean().setBrandList(
				resDTO.getCommodityObj().getCmBrand());
		this.getManagedBean().setMeasurementList(
				resDTO.getCommodityObj().getCmMeasurement());
		resDTO = codificationSO.searchColor(reqDTO);
		this.getManagedBean().setColorList(resDTO.getColorList());

		//20210405@Jacky - Enhancement#28576 - Start
		log.info("TYPE BRAND MEASUREMENT and COLOR has Been Set By Commodity");
		
		this.getManagedBean().setRequestExtCode(new CmRequestCode());
		this.getManagedBean()
		.getRequestExtCode()
		.setTempCode(
				this.getManagedBean().getSelectedLovUnspsc()
				.getUnspscCode());
		this.getManagedBean()
		.getRequestExtCode()
		.setTempTitle(
				this.getManagedBean().getSelectedLovUnspsc()
				.getUnspscTitle());
		clearForm();
		this.getManagedBean().setTypeDisable(true);
		this.getManagedBean().setBrandDisable(true);
		this.getManagedBean().setMeasurementDisable(true);
		this.getManagedBean().setColorDisable(true);

		log.info("CmType : " + resDTO.getCommodityObj().getCmType().size());
		log.info("CmType : " + resDTO.getCommodityObj().getCmType());
		log.info("CmBrand : " + resDTO.getCommodityObj().getCmBrand().size());
		log.info("CmMeasurement : " + resDTO.getCommodityObj().getCmMeasurement().size());
		//20210405@Jacky - Enhancement#28576 - End
	}

	public String submitToBPM() {

		IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

		UserReqDTO userReqDTO = new UserReqDTO();

		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (this.getManagedBean().getRequestExtCodeList() == null
				|| this.getManagedBean().getRequestExtCodeList().isEmpty()) {
			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.ERROR_715004,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
			return null;
		}
		
		
		if (this.getManagedBean().getRequestExtCodeList() != null && this.getManagedBean().getRequestExtCodeList().size()>0) {
			
			
			for(CmRequestCode code : this.getManagedBean().getRequestExtCodeList()){
				
				if(StringUtils.isBlank(code.getTypeName()) || StringUtils.isBlank(code.getBrandName())
						||StringUtils.isBlank(code.getColorName())||StringUtils.isBlank(code.getMeasurementName())){
					facesContext.addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									MessageUtils.getMessage(MessageConst.ERROR_716020,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
					return null;
				}
				
			}
			
			
			
		}
		
		

		Map brandMap = new HashMap();
		Map typeMap = new HashMap();
		Map meaMap = new HashMap();
		Map colorMap = new HashMap();
		for (CmRequestCode code :this.getManagedBean().getRequestExtCodeList()) {

			////////////////check brand
			if(code.isRequestBrand()){
				if(!brandMap.containsKey(code.getBrandName())){
					log.info("********1st time create new************");

					brandMap.put(code.getBrandName(),code.getBrandName());
				}else{
					facesContext.addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									MessageUtils.getMessage(MessageConst.ERROR_715061,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
					return null;

				}
			}

			/////check type 

			if(code.isRequestType()){
				if(!typeMap.containsKey(code.getTypeName())){
					log.info("********1st time create new************");

					typeMap.put(code.getTypeName(),code.getTypeName());
				}else{
					facesContext.addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									MessageUtils.getMessage(MessageConst.ERROR_715061,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
					return null;

				}
			}

			//// check measurement

			if(code.isRequestMeasurement()){
				if(!meaMap.containsKey(code.getMeasurementName())){
					log.info("********1st time create new************");

					meaMap.put(code.getMeasurementName(),code.getMeasurementName());
				}else{
					facesContext.addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									MessageUtils.getMessage(MessageConst.ERROR_715061,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
					return null;

				}
			}

			/// check color

			if(code.isRequestColor()){
				if(!colorMap.containsKey(code.getColorName())){
					log.info("********1st time create new************");

					colorMap.put(code.getColorName(),code.getColorName());
				}else{
					facesContext.addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									MessageUtils.getMessage(MessageConst.ERROR_715061,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
					return null;

				}
			}


		}




		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);

		FacesScopes face = new FacesScopes();
		Object token = face.getBPMAuthenticationCtxToken();



		CodificationReqDTO reqDTO = new CodificationReqDTO();

		ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
				.getInstance().getServiceObject("sm", ISupplierSearchSO.class);
		SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();

		PmUser pmUser = new FacesScopes().getUserSessionBean();
		supplierReqDTO.setUserId(pmUser.getUserId());
		supplierReqDTO.setAllStatuses(true);
		String actionDesc = "";
		SupplierSearchResDTO smResDTO = supplierSearchSO
				.retriveSupplierProfiles(supplierReqDTO);

		if(smResDTO.getResultBean()==null){
			throw new EpException("Fail to get SmSupplier");
		}

		log.info("getSmSupplier"
				+ smResDTO.getResultBean().getResultList().get(0));


		for (CmRequestCode code : this.getManagedBean().getRequestExtCodeList()) {
			code.setSmSupplier(smResDTO.getResultBean().getResultList().get(0));			

		}

		reqDTO.setRequestExtList(this.getManagedBean().getRequestExtCodeList());
		reqDTO.setToken(token);		

		log.info("Tracking diary for Extension Code---submitToBPM()");	


		IStatusSO statusSO = (IStatusSO) BusinessDelegate
				.getInstance()
				.getServiceObject("pm", IStatusSO.class);

		StatusReqDTO statusReqDTO = new StatusReqDTO();

		statusReqDTO.setStatusId(15001L);
		StatusResDTO statusResDTO = statusSO.retrieveStatusByStatusId(statusReqDTO);


		if(face.hasRoleUser(CmConst.CM_ADMIN)){
			reqDTO.setTrackingStatus(true);

			actionDesc = "Kod Lanjutan "+reqDTO.getRequestCode().getDocNo()+" telah diserah kepada Pentadbir Kodifikasi untuk semakan";
			reqDTO.setTrackingDairyVO(CmApplicationUtil.assignTrackingDiaryValuesForUNSPSCExtension(actionDesc, statusResDTO.getPmStatus()!=null?statusResDTO.getPmStatus():new PmStatus(),CmConst.CE_DocType));	

		}
		else {
			reqDTO.setTrackingStatus(true);
			actionDesc = "Kod Lanjutan "+reqDTO.getRequestCode().getDocNo()+" telah diserah kepada Pentadbir Katalog untuk semakan";
			reqDTO.setTrackingDairyVO(CmApplicationUtil.assignTrackingDiaryValuesForUNSPSCExtension(actionDesc, statusResDTO.getPmStatus()!=null?statusResDTO.getPmStatus():new PmStatus(),CmConst.CE_DocType));				
		}

		if(isCatalogueAdmin()){
			log.info("**isCatalogueAdmin***");

			userReqDTO.setRoleCode(RoleCode.CD_OFFICER.value());
			UserResDTO userResDTO = userSO.retrieveUserListByRole(userReqDTO);

			if(userResDTO.getUserList()!=null && userResDTO.getUserList().size()>0){
				for(PmUser user :userResDTO.getUserList()){
					log.info("user.getLoginId():"+user.getLoginId());
					reqDTO.getCdAdminList().add(user.getLoginId());
				}
			}else{
				throw new EpException("No user found!");
			}


		}else{
			log.info("**supplier***");

			userReqDTO.setRoleCode(RoleCode.CM_ADMIN.value());
			UserResDTO userResDTO = userSO.retrieveUserListByRole(userReqDTO);

			if(userResDTO.getUserList()!=null && userResDTO.getUserList().size()>0){
				for(PmUser user :userResDTO.getUserList()){
					log.info("user.getLoginId():"+user.getLoginId());
					reqDTO.getCmAdminList().add(user.getLoginId());
				}
			}else{
				throw new EpException("No user found!");
			}
		}
		CodificationResDTO resDTO = unspscSO.addExtensionRequest(reqDTO);		

		if(resDTO.isSuccessful()){
			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
							.getMessage(MessageConst.INFO_710013,new String[] {resDTO.getSaveExtList().get(0).getDocNo()},
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
		}

		return "BPMtesting";
	}

	public void addNewRequest() {
		log.info("...............addNewRequest...................");
		/*
		 * ICodificationSO unspscSO = (ICodificationSO)
		 * BusinessDelegate.getInstance
		 * ().getServiceObject("cm",ICodificationSO.class); CodificationReqDTO
		 * reqDTO = new CodificationReqDTO(); CodificationResDTO resDTO = null;
		 */

		// ///check that there be at least one new request
		if (!this.getManagedBean().getRequestExtCode().isRequestType()
				&& !this.getManagedBean().getRequestExtCode().isRequestBrand()
				&& !this.getManagedBean().getRequestExtCode()
				.isRequestMeasurement()
				&& !this.getManagedBean().getRequestExtCode().isRequestColor()) {
			throw new EpException(MessageConst.ERROR_716010);

		}
		// //////////checking for redundancy
		if (this.getManagedBean().getRequestExtCode().isRequestType()
				&& !this.getManagedBean().getRequestExtCode().getTypeName()
				.isEmpty()) {
			checkingTypeRedundency(this.getManagedBean().getRequestExtCode()
					.getTypeName(), false);
		}
		if (this.getManagedBean().getRequestExtCode().isRequestBrand()
				&& !this.getManagedBean().getRequestExtCode().getBrandName()
				.isEmpty()) {
			checkingBrandRedundency(this.getManagedBean().getRequestExtCode()
					.getBrandName(), false);
		}
		if (this.getManagedBean().getRequestExtCode().isRequestMeasurement()
				&& !this.getManagedBean().getRequestExtCode()
				.getMeasurementName().isEmpty()) {
			checkingMeasurmentRedundency(this.getManagedBean()
					.getRequestExtCode().getMeasurementName(), false);
		}
		if (this.getManagedBean().getRequestExtCode().isRequestColor()
				&& !this.getManagedBean().getRequestExtCode().getColorName()
				.isEmpty()) {
			checkingColorRedundency(this.getManagedBean().getRequestExtCode()
					.getColorName(), false);
		}

		/*
		 * if (this.getManagedBean().isTypeRedundancy()) { throw new
		 * EpException(MessageConst.ERROR_716009, new String[] {
		 * ResourceBundleUtils.getMessage( "lbl.cm.type",
		 * ResourceBundleUtils.BUNDLE_COMMON_LABELS, new
		 * FacesScopes().getUserLocale()) }); } if
		 * (this.getManagedBean().isBarndRedundancy()) { throw new
		 * EpException(MessageConst.ERROR_716009, new String[] {
		 * ResourceBundleUtils.getMessage( "lbl.cm.brand",
		 * ResourceBundleUtils.BUNDLE_COMMON_LABELS, new
		 * FacesScopes().getUserLocale()) }); } if
		 * (this.getManagedBean().isMeasurRedundancy()) { FacesContext
		 * facesContext = FacesContext.getCurrentInstance();
		 * 
		 * throw new EpException(MessageConst.ERROR_716009, new String[] {
		 * ResourceBundleUtils.getMessage( "lbl.cm.measurement",
		 * ResourceBundleUtils.BUNDLE_COMMON_LABELS, new
		 * FacesScopes().getUserLocale()) }); } if
		 * (this.getManagedBean().isColorRedundancy()) { FacesContext
		 * facesContext = FacesContext.getCurrentInstance();
		 * 
		 * throw new EpException(MessageConst.ERROR_716009, new String[] {
		 * ResourceBundleUtils.getMessage( "lbl.cm.color",
		 * ResourceBundleUtils.BUNDLE_COMMON_LABELS, new
		 * FacesScopes().getUserLocale()) }); }
		 */
		// type
		if ((!this.getManagedBean().getRequestExtCode().isRequestType() && this
				.getManagedBean().getSelectedType() == null)
				|| (this.getManagedBean().getRequestExtCode().isRequestType() && this
						.getManagedBean().getRequestExtCode().getTypeName()
						.isEmpty())) {

			throw new EpException(MessageConst.ERROR_100022,
					new String[] { ResourceBundleUtils.getMessage(
							"lbl.cm.type",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()) });
		} else {

			if (!this.getManagedBean().getRequestExtCode().isRequestType()) {
				this.getManagedBean()
				.getRequestExtCode()
				.setTypeName(
						this.getManagedBean().getSelectedType()
						.getTypeName());
				this.getManagedBean()
				.getRequestExtCode()
				.setTypeId(
						this.getManagedBean().getSelectedType()
						.getTypeId());
			}
		}
		// brand
		if ((!this.getManagedBean().getRequestExtCode().isRequestBrand() && this
				.getManagedBean().getSelectedBrand() == null)
				|| (this.getManagedBean().getRequestExtCode().isRequestBrand() && this
						.getManagedBean().getRequestExtCode().getBrandName()
						.isEmpty())) {

			throw new EpException(MessageConst.ERROR_100022,
					new String[] { ResourceBundleUtils.getMessage(
							"lbl.cm.brand",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()) });
		} else {

			if (!this.getManagedBean().getRequestExtCode().isRequestBrand()) {
				this.getManagedBean()
				.getRequestExtCode()
				.setBrandName(
						this.getManagedBean().getSelectedBrand()
						.getBrandName());
				this.getManagedBean()
				.getRequestExtCode()
				.setBrandId(
						this.getManagedBean().getSelectedBrand()
						.getBrandId());
			}
		}
		// color
		if ((!this.getManagedBean().getRequestExtCode().isRequestColor() && this
				.getManagedBean().getSelectedColor() == null)
				|| (this.getManagedBean().getRequestExtCode().isRequestColor() && this
						.getManagedBean().getRequestExtCode().getColorName()
						.isEmpty())) {

			throw new EpException(MessageConst.ERROR_100022,
					new String[] { ResourceBundleUtils.getMessage(
							"lbl.cm.color",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()) });
		} else {
			if (!this.getManagedBean().getRequestExtCode().isRequestColor()) {
				this.getManagedBean()
				.getRequestExtCode()
				.setColorName(
						this.getManagedBean().getSelectedColor()
						.getColorName());
				this.getManagedBean()
				.getRequestExtCode()
				.setColorId(
						this.getManagedBean().getSelectedColor()
						.getColorId());
			}
		}
		// measurement
		if ((!this.getManagedBean().getRequestExtCode().isRequestMeasurement() && this
				.getManagedBean().getSelectedMeasurement() == null)
				|| (this.getManagedBean().getRequestExtCode()
						.isRequestMeasurement() && this.getManagedBean()
						.getRequestExtCode().getMeasurementName().isEmpty())) {

			throw new EpException(MessageConst.ERROR_100022,
					new String[] { ResourceBundleUtils.getMessage(
							"lbl.cm.measurement",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()) });
		} else {
			if (!this.getManagedBean().getRequestExtCode()
					.isRequestMeasurement()) {
				this.getManagedBean()
				.getRequestExtCode()
				.setMeasurementId(
						this.getManagedBean().getSelectedMeasurement()
						.getMeasurementId());
				this.getManagedBean()
				.getRequestExtCode()
				.setMeasurementName(
						this.getManagedBean().getSelectedMeasurement()
						.getMeasurementName());
			}
		}

		if(this.getManagedBean().isEditMode()){
			this.getManagedBean().getRequestExtCodeList().set(this.getManagedBean().getEditModeIndex(), this.getManagedBean().getRequestExtCode());
		}else{
			this.getManagedBean().getRequestExtCodeList().add(this.getManagedBean().getRequestExtCode());
		}
		clearForm();

		RequestContext.getCurrentInstance().execute("extensionPopUp.hide();");
	}

	public void clearForm() {
		log.info("...............clearForm...................");
		this.getManagedBean().setSelectedType(null);
		this.getManagedBean().setSelectedBrand(null);
		this.getManagedBean().setSelectedColor(null);
		this.getManagedBean().setSelectedMeasurement(null);
	}
	public void onDDSelected(CmRequestCode code, String attr){ //onchange listener
		if(attr.equals("type")){
			code.setTypeRemark(null);
			code.setReviewedTypeName(code.getTypeName());
		}else if(attr.equals("brand")){
			code.setBrandRemark(null);
			code.setReviewedBrandName(code.getBrandName());
		}else if(attr.equals("color")){
			code.setColorRemark(null);
			code.setReviewedColorName(code.getColorName());
		}else if(attr.equals("measurement")){
			code.setMeasurementRemark(null);
			code.setReviewedMeasurementName(code.getMeasurementName());
		}
	}
	
	public boolean  isExistingExt(String attr, String lhsCompare){ //do we need live check?
		lhsCompare = lhsCompare.trim();
		
		if(attr.equals("type")){
			for(CmType type: this.getManagedBean().getTypeList()){
				if(lhsCompare.equals(type.getTypeName())){
					return true;
				}
			}
		}else if(attr.equals("brand")){
			for(CmBrand brand: this.getManagedBean().getBrandList()){
				if(lhsCompare.equals(brand.getBrandName())){
					return true;
				}
			}
		}else if(attr.equals("color")){
			for(CmColor color: this.getManagedBean().getColorList()){
				if(lhsCompare.equals(color.getColorName())){
					return true;
				}
			}
		}else if(attr.equals("measurement")){
			for(CmMeasurement measurement: this.getManagedBean().getMeasurementList()){
				if(lhsCompare.equals(measurement.getMeasurementName())){
					return true;
				}
			}
		}
		return false;
	}
	public String submitToReviewExt(String action) {
		log.info("***action***" + action);
		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);

		FacesScopes face = new FacesScopes();
		Object token = face.getBPMAuthenticationCtxToken();
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		CodificationResDTO resDTO = null;

		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);

		BpmRequestDTO bpmReqDTO = new BpmRequestDTO();

		bpmReqDTO.setCtxToken(token);
		bpmReqDTO.setTaskId(this.getManagedBean().getTaskId());
		BpmResponseDTO res = bpmSO.getTaskDetails(bpmReqDTO);
		Object obj = res.getPojo();

		CodificationReqDTO reqUnspscDTO = new CodificationReqDTO();
		boolean isDDTypeSelected = false;
		boolean isDDBrandSelected = false;
		boolean isDDMeasurementSelected = false;
		boolean isDDColorSelected = false;

			if (this.getManagedBean().getReviewExtCodeList() != null
					&& this.getManagedBean().getReviewExtCodeList().size() > 0) {

				for (CmRequestCode code : this.getManagedBean()
						.getReviewExtCodeList()) {
					
					
					reqUnspscDTO.setUnspscCode(code.getTempCode());
					CodificationResDTO unspscDTO = unspscSO.searchUnspscObj(reqUnspscDTO);
					
					long unspscId = unspscDTO.getUnspscObj().getUnspscId();
					
					

					if (code.getColorNameDD() != null) {
						ISearchBean<CmColor> searchBeanColor = new SearchBean<CmColor>();
						colorFilterBean.setSearchField("colorId");
						colorFilterBean.setSearchValue(code.getColorNameDD());
						searchBeanColor.getDataFilterBeans().add(colorFilterBean);

						reqDTO.setColorSearchBean(searchBeanColor);

						resDTO = unspscSO.searchColorObj(reqDTO);
						if(isCatalogueAdmin()){
							code.setColorName(resDTO.getColorList().get(0)
									.getColorName());
						}else if(isCdCommittee()){
							isDDColorSelected = true;
							code.setReviewedColorName(resDTO.getColorList().get(0)
									.getColorName());
						}
						code.setColorId(code.getColorNameDD());
						code.setRequestColor(false);
						

					}else{

						if(!action.equalsIgnoreCase("PUBLISH") && !action.equalsIgnoreCase("OK") && isCatalogueAdmin()  && code.isRequestColor()){

							ISearchBean<CmColor> searchBeanColor = new SearchBean<CmColor>();
							colorFilterBean.setSearchField("colorName");
							colorFilterBean.setSearchValue(code.getColorName().trim());
							searchBeanColor.getDataFilterBeans().add(colorFilterBean);
	
							reqDTO.setColorSearchBean(searchBeanColor);
	
							resDTO = unspscSO.searchColorObj(reqDTO);
							
							RequestContext.getCurrentInstance().execute("extensionConfirmation.hide()");
							if(resDTO.getColorList()!=null && resDTO.getColorList().size()>0){
								throw new EpException(MessageUtils
										.getMessage(MessageConst.ERROR_715068, new String[] { ""
												+ code.getColorName() },
												MessageUtils.BUNDLE_COMMON_MESSAGES,
												new FacesScopes().getUserLocale()));
							}
						}

					}

					if (code.getBrandNameDD() != null) {

						ISearchBean<CmBrand> searchBeanColor = new SearchBean<CmBrand>();
						brandFilterBean.setSearchField("brandId");
						brandFilterBean.setSearchValue(code.getBrandNameDD());
						searchBeanColor.getDataFilterBeans().add(brandFilterBean);

						reqDTO.setBrandSearchBean(searchBeanColor);

						resDTO = unspscSO.searchBrandObj(reqDTO);
						if(isCatalogueAdmin()){
							code.setBrandName(resDTO.getBrandList().get(0)
									.getBrandName());
						}else if(isCdCommittee()){
							isDDBrandSelected = true;
							code.setReviewedBrandName(resDTO.getBrandList().get(0)
									.getBrandName());
						}
						code.setBrandId(code.getBrandNameDD());
						code.setRequestBrand(false);
					}else{

						if(!action.equalsIgnoreCase("PUBLISH") && !action.equalsIgnoreCase("OK") && isCatalogueAdmin()  && code.isRequestBrand()){

							ISearchBean<CmBrand> searchBeanColor = new SearchBean<CmBrand>();
							brandFilterBean.setSearchField("brandName");
							brandFilterBean.setSearchValue(code.getBrandName().trim());
							searchBeanColor.getDataFilterBeans().add(brandFilterBean);
							
							
							unspscIdFilterBean.setSearchField("unspscId.unspscId");
							unspscIdFilterBean.setSearchValue(unspscId);
							searchBeanColor.getDataFilterBeans().add(unspscIdFilterBean);
	
							reqDTO.setBrandSearchBean(searchBeanColor);
	
							resDTO = unspscSO.searchBrandObj(reqDTO);
	
							if(resDTO.getBrandList()!=null &&resDTO.getBrandList().size()>0){
	
								RequestContext.getCurrentInstance().execute("extensionConfirmation.hide()");
								throw new EpException(MessageUtils
										.getMessage(MessageConst.ERROR_715068, new String[] { ""
												+ code.getBrandName() },
												MessageUtils.BUNDLE_COMMON_MESSAGES,
												new FacesScopes().getUserLocale()));
							}
						}

					}

					if (code.getTypeNameDD() != null) {

						ISearchBean<CmType> searchBeanColor = new SearchBean<CmType>();
						typeFilterBean.setSearchField("typeId");
						typeFilterBean.setSearchValue(code.getTypeNameDD());
						searchBeanColor.getDataFilterBeans().add(typeFilterBean);

						reqDTO.setTypeSearchBean(searchBeanColor);

						resDTO = unspscSO.searchTypeObj(reqDTO);
						if(isCatalogueAdmin()){
							code.setTypeName(resDTO.getTypeList().get(0).getTypeName());
						}else if(isCdCommittee()){
							isDDTypeSelected = true;
							code.setReviewedTypeName(resDTO.getTypeList().get(0).getTypeName());
						}
						code.setTypeId(code.getTypeNameDD());
						code.setRequestType(false);
					}else{

						if(!action.equalsIgnoreCase("PUBLISH")&& !action.equalsIgnoreCase("OK") && isCatalogueAdmin()  && code.isRequestType()){

							ISearchBean<CmType> searchBeanColor = new SearchBean<CmType>();
							typeFilterBean.setSearchField("typeName");
							typeFilterBean.setSearchValue(code.getTypeName().trim());
							searchBeanColor.getDataFilterBeans().add(typeFilterBean);
							
							
							unspscIdFilterBean.setSearchField("unspscId.unspscId");
							unspscIdFilterBean.setSearchValue(unspscId);
							searchBeanColor.getDataFilterBeans().add(unspscIdFilterBean);
							
	
							reqDTO.setTypeSearchBean(searchBeanColor);
	
							resDTO = unspscSO.searchTypeObj(reqDTO);
	
							RequestContext.getCurrentInstance().execute("extensionConfirmation.hide()");
							if(resDTO.getTypeList()!=null && resDTO.getTypeList().size()>0){
								throw new EpException(MessageUtils
										.getMessage(MessageConst.ERROR_715068, new String[] { ""
												+ code.getTypeName() },
												MessageUtils.BUNDLE_COMMON_MESSAGES,
												new FacesScopes().getUserLocale()));
							}
						}
					}

					if (code.getMeasurementNameDD() != null) {

						ISearchBean<CmMeasurement> searchBeanColor = new SearchBean<CmMeasurement>();
						measurementFilterBean.setSearchField("measurementId");
						measurementFilterBean.setSearchValue(code
								.getMeasurementNameDD());
						searchBeanColor.getDataFilterBeans().add(
								measurementFilterBean);

						reqDTO.setMeasurementSearchBean(searchBeanColor);

						resDTO = unspscSO.searchMeasurementObj(reqDTO);
						if(isCatalogueAdmin()){
							code.setMeasurementName(resDTO.getMeasurementList().get(0)
									.getMeasurementName());
						}else if(isCdCommittee()){
							isDDMeasurementSelected = true;
							code.setReviewedMeasurementName(resDTO.getMeasurementList().get(0)
									.getMeasurementName());
						}
						code.setMeasurementId(code.getMeasurementNameDD());
						code.setRequestMeasurement(false);
					}else{

						if(!action.equalsIgnoreCase("PUBLISH")&& !action.equalsIgnoreCase("OK") && isCatalogueAdmin()  && code.isRequestMeasurement()){

							ISearchBean<CmMeasurement> searchBeanColor = new SearchBean<CmMeasurement>();
							measurementFilterBean.setSearchField("measurementName");
							measurementFilterBean.setSearchValue(code.getMeasurementName().trim());
							searchBeanColor.getDataFilterBeans().add(measurementFilterBean);
							
							
							unspscIdFilterBean.setSearchField("unspscId.unspscId");
							unspscIdFilterBean.setSearchValue(unspscId);
							searchBeanColor.getDataFilterBeans().add(unspscIdFilterBean);
	
							reqDTO.setMeasurementSearchBean(searchBeanColor);
	
							resDTO = unspscSO.searchMeasurementObj(reqDTO);
	
							RequestContext.getCurrentInstance().execute("extensionConfirmation.hide()");
							if(resDTO.getMeasurementList()!=null && resDTO.getMeasurementList().size()>0){
								throw new EpException(MessageUtils
										.getMessage(MessageConst.ERROR_715068, new String[] { ""
												+ code.getMeasurementName() },
												MessageUtils.BUNDLE_COMMON_MESSAGES,
												new FacesScopes().getUserLocale()));
							}
						}


					}

					reqDTO.getRequestExtList().add(code);

				}
			}
	


		reqDTO.setTaskId(this.getManagedBean().getTaskId());
		reqDTO.setOutCome(action);
		reqDTO.setToken(token);
		reqDTO.setPojo(obj);

		log.info("action" + action);


		IStatusSO statusSO = (IStatusSO) BusinessDelegate
				.getInstance()
				.getServiceObject("pm", IStatusSO.class);

		StatusReqDTO statusReqDTO = new StatusReqDTO();
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		if(!this.isAllSupplier()){
			reqDTO.setRemark(this.getManagedBean().getSelectedRemarks().get(0));
		}

		if (action.equalsIgnoreCase("PUBLISH")) {

			reqDTO.setUnspscCode(this.getManagedBean().getReviewExtCodeList()
					.get(0).getTempCode());
			resDTO = unspscSO.searchCommodityObj(reqDTO);

			reqDTO.setCmUnspsc(resDTO.getCommodityObj());
			reqDTO.setUnspscId(resDTO.getCommodityObj().getUnspscId());

			log.info("Tracking diary for Extension Code Published by Code_Officer");
			reqDTO.setTrackingStatus(true);



			statusReqDTO.setStatusId(15005L);
			StatusResDTO statusResDTO = statusSO.retrieveStatusByStatusId(statusReqDTO);
			String actionDesc ="Kod Item "+ this.getManagedBean().getReviewExtCodeList().get(0).getDocNo()+" telah diiklankan oleh "+pmUser.getUserName();
			reqDTO.setTrackingDairyVO(CmApplicationUtil.assignTrackingDiaryValuesForUNSPSCExtension(actionDesc, statusResDTO.getPmStatus()!=null?statusResDTO.getPmStatus():new PmStatus(),CmConst.CE_DocType));

			resDTO = unspscSO.publishExtensionRequest(reqDTO);

		}

		if (!action.equalsIgnoreCase("PUBLISH")) {

			for (CmRequestCode code : this.getManagedBean()
					.getReviewExtCodeList()) {

				log.info("code.isRemarkRequired"
						+ code.isRemarkRequired());
				log.info("code.isRemarkRequiredII"
						+ code.isRemarkRequiredII());
				log.info("******getReviewedBrandNameII********"
						+ code.getReviewedBrandNameII());
				if (code.getReviewedBrandNameII() != null 	&& code.getReviewedBrandNameII().trim().length() > 0) {
					// //////checking for redundant values
					if(!action.equalsIgnoreCase(CmConst.ACTIONSTATUS)){
						if (!isCodeAdmin()) {
							if (code.isRequestType()) {
								checkingTypeRedundency(code.getReviewedTypeNameII(), false);
							}
							if (code.isRequestBrand()) {
								checkingBrandRedundency(code
										.getReviewedBrandNameII(), false);
							}
							if (code.isRequestMeasurement()) {
								checkingMeasurmentRedundency(code
										.getReviewedMeasurementNameII(), false);
							}
							if (code.isRequestColor()) {
								checkingColorRedundency(code
										.getReviewedColorNameII(), false);
							}
						}
						// //////////////////////////////////
						if ((code.getTypeRemarkII() != null && code
								.getTypeRemarkII().trim().length() > 0)
								|| (code.getBrandRemarkII() != null && code
								.getBrandRemarkII().trim().length() > 0)
								|| (code.getMeasurementRemarkII() != null && code
								.getMeasurementRemarkII().trim().length() > 0)
								|| (code.getColorRemarkII() != null && code
								.getColorRemarkII().trim().length() > 0)) {
							log.info("remark added!!");
							code.setRemarkRequiredII(false);
						}
					}else{
						code.setRemarkRequiredII(false);
					}
					

					if (code.isRemarkRequiredII()) {

						FacesContext facesContext = FacesContext
								.getCurrentInstance();
						facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils
								.getMessage(MessageConst.ERROR_716007,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
						return "reviewApproveExtensionCodeII";
					}
				} else {
					log.info("******else********");
					// //////checking for redundant values
					if(!action.equalsIgnoreCase(CmConst.ACTIONSTATUS)){
						if (code.isRequestType() && !isDDTypeSelected && code.getReviewedTypeName()!=null) {
							checkingTypeRedundency(code.getReviewedTypeName().trim(), true);
						}
						if (code.isRequestBrand()  &&  !isDDBrandSelected && code.getReviewedBrandName()!=null) {
							checkingBrandRedundency(code.getReviewedBrandName().trim(), true);
						}
						if (code.isRequestMeasurement() && !isDDMeasurementSelected && code.getReviewedMeasurementName()!=null) {
							checkingMeasurmentRedundency(code.getReviewedMeasurementName().trim(), true);
						}
						if (code.isRequestColor()  && !isDDColorSelected && code.getReviewedColorName()!=null) {
							checkingColorRedundency(code.getReviewedColorName().trim(), true);
						}

						if( !isDDMeasurementSelected || !isDDColorSelected || !isDDBrandSelected || !isDDTypeSelected){
							if ((code.getTypeRemark() != null && code.getTypeRemark()
									.trim().length() > 0)
									|| (code.getBrandRemark() != null && code
									.getBrandRemark().trim().length() > 0 )
									|| (code.getMeasurementRemark() != null && code
									.getMeasurementRemark().trim().length() > 0  )
									|| (code.getColorRemark() != null && code
									.getColorRemark().trim().length() > 0) ) {
								log.info("remark added!!");
								code.setRemarkRequired(false);
							}
						}
					}else{
						code.setRemarkRequired(false);
					}

					if (code.isRemarkRequired() ) {
						log.info("remark  error invoked");
						throw new EpException(
								MessageConst.ERROR_716007,
								new String[] { ResourceBundleUtils
										.getMessage(
												null,
												ResourceBundleUtils.BUNDLE_COMMON_LABELS,
												new FacesScopes()
												.getUserLocale()) });
					}
				}
				if(!action.equalsIgnoreCase(CmConst.ACTIONSTATUS))
				{   
					log.info("tracking diary for ExtensionCodeReviewStatus: submitToReviewExt");
					reqDTO.setTrackingStatus(true);			
					reqDTO.setUserName(pmUser.getUserName());
					getExtensionCodeReviewStatus(reqDTO,face, statusSO, statusReqDTO );
				}
				if(this.getManagedBean().isRenderV2()){
					log.info("Reviewed item type " + code.getReviewedTypeName() + " and is new request? :" + code.isRequestType());
					log.info("Reviewed item brand " + code.getReviewedBrandName() + " and is new request? :" + code.isRequestBrand());
					log.info("Reviewed item measurement " + code.getReviewedMeasurementName() + " and is new request? :" + code.isRequestMeasurement());
					log.info("Reviewed item color " + code.getReviewedColorName() + " and is new request? :" + code.isRequestColor());
				}
			}
			if(this.getManagedBean().isRenderV2() && isCdCommittee()){ //publish only on CDComittee
				reqDTO.setTrackingStatus(true);
				statusReqDTO.setStatusId(15005L);
				StatusResDTO statusResDTO = statusSO.retrieveStatusByStatusId(statusReqDTO);
				String actionDesc ="Kod Item "+ this.getManagedBean().getReviewExtCodeList().get(0).getDocNo()+" telah diiklankan oleh "+pmUser.getUserName();
				reqDTO.setTrackingDairyVO(CmApplicationUtil.assignTrackingDiaryValuesForUNSPSCExtension(actionDesc, statusResDTO.getPmStatus()!=null?statusResDTO.getPmStatus():new PmStatus(),CmConst.CE_DocType));

				resDTO = unspscSO.publishCodificationRequest(reqDTO);
			}else{
				resDTO = unspscSO.updateExtensionRequest(reqDTO);
				
			}
		}

		//20190830@emy CR15028 - start
		
		if(this.getManagedBean().isRecommendExtension()){

			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
							.getMessage(MessageConst.INFO_710017,new String[] {this.getManagedBean().getReviewExtCodeList().get(0).getDocNo()}  ,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
			
		}else if(this.getManagedBean().isRenderV2() && isCdCommittee()){ 
			
			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
							.getMessage(MessageConst.INFO_710017,new String[] {this.getManagedBean().getReviewExtCodeList().get(0).getDocNo()},
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
		}
		else{
			
			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
							.getMessage(MessageConst.INFO_710019,new String[] {this.getManagedBean().getReviewExtCodeList().get(0).getDocNo()}, 
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
		
		};

		//20190830@emy CR15028 - end
		
		RequestContext.getCurrentInstance().execute("extensionConfirmation.hide()");

		log.info("***************b4 return**************");
		return "/xhtml/common/acknowledge.xhtml";
	}


	public void getExtensionCodeReviewStatus(CodificationReqDTO reqDTO,FacesScopes face, IStatusSO statusSO, StatusReqDTO statusReqDTO ){

		IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

		if (face.hasRoleUser(CmConst.CM_ADMIN)){		
			log.info("Tracking diary for Extension Code Creation or Extension Code Review by Catalog_Admin");
			//#22008 - changed status and action/desc string
			String actionDesc;
			if(this.getManagedBean().isRecommendExtension()){
				log.info("Recommend extension to supplier.");
				statusReqDTO.setStatusId(15002L);
				actionDesc = "Kod lanjutan "+this.getManagedBean().getReviewExtCodeList().get(0).getDocNo()+" telah disyorkan oleh " + reqDTO.getUserName();
			}else{
				log.info("Submit extension for endorsement.");
				statusReqDTO.setStatusId(15004L);
				actionDesc = "Kod Lanjutan "+this.getManagedBean().getReviewExtCodeList().get(0).getDocNo()+" telah diserah kepada Jawatankuasa Kodifikasi (CDC) untuk kelulusan";
			};
			
			StatusResDTO statusResDTO = statusSO.retrieveStatusByStatusId(statusReqDTO);
			reqDTO.setTrackingDairyVO(CmApplicationUtil.assignTrackingDiaryValuesForUNSPSCExtension(actionDesc, statusResDTO.getPmStatus()!=null?statusResDTO.getPmStatus():new PmStatus(),CmConst.CE_DocType));

			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setRoleCode(RoleCode.CD_COMMITTEE.value());
			UserResDTO userResDTO = userSO.retrieveUserListByRole(userReqDTO);

			if(userResDTO.getUserList()!=null && userResDTO.getUserList().size()>0){
				for(PmUser user :userResDTO.getUserList()){
					log.info("user.getLoginId():"+user.getLoginId());
					reqDTO.getCdcList().add(user.getLoginId());
				}
			}else{
				throw new EpException("No user found!");
			}

		}
		// #37924: 070325 - Start
		else if (face.hasRoleUser(CmConst.CD_COMMITTEE)) {
			log.info("Tracking diary for Extension Code Creation or Extension Code Review by CD_COMMITTE");
			statusReqDTO.setStatusId(15025L);
			StatusResDTO statusResDTO = statusSO
					.retrieveStatusByStatusId(statusReqDTO);
			String actionDesc = "Kod Lanjutan "
					+ this.getManagedBean().getReviewExtCodeList().get(0)
							.getDocNo()
					+ " telah diserah kepada Jawatankuasa Kodifikasi (eP) untuk perakuan.";
			reqDTO.setTrackingDairyVO(CmApplicationUtil
					.assignTrackingDiaryValuesForUNSPSCExtension(
							actionDesc,
							statusResDTO.getPmStatus() != null ? statusResDTO
									.getPmStatus() : new PmStatus(),
							CmConst.CE_DocType));

		}
		// #37924: 070325 - End
	}
	
	private int compareFirstChoice(String code1, String code2, int last){
		if((code1.equals("000") || code1.equals("00")) && (code2.equals("000") || code2.equals("00"))){
			return 0;
		}else if((code1.equals("000") || code1.equals("00"))){
			return -1;
		}else if((code2.equals("000") || code2.equals("00"))){
			return 1;
		}
		return last;
	}; 
	
	private void arrangeOutcomeButton(){
		String[] actions=this.getManagedBean().getAction();
		if(this.getManagedBean().getAction()!=null){
			for(int i = 0;i<actions.length;i++){
				String temp;
				if(actions[i].equalsIgnoreCase("SUBMIT")){
					temp = actions[0];
					actions[0]= actions[i];
					actions[i]=temp;
				}else	if(actions[i].equalsIgnoreCase("RECOMMEND_TO_SUPPLIER")){
					temp = actions[1];
					actions[1]= actions[i];
					actions[i]=temp;
				}
			}
		}
		this.getManagedBean().setAction(actions);
	}
	public void preRenderExtensionView() {
		log.info("**********preRenderExtensionView**************");

		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IUserSO.class);
		UserReqDTO userReqDTO = new UserReqDTO();

		BPMTaskListBackingBean bb = new BPMTaskListBackingBean();
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		String claim = originalServletRequest.getParameter("claim");
		log.info("taskId:" + taskId);
		log.info("claim:" + claim);

		Boolean b = Boolean.valueOf(claim);
		bb.getManagedBean().setClaim(b);
		bb.getManagedBean().setTaskId(taskId);

		FacesScopes face = new FacesScopes();
		Object token = face.getBPMAuthenticationCtxToken();

		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);


		this.getManagedBean().setTaskId(taskId);
		BpmRequestDTO reqDTO = new BpmRequestDTO();

		reqDTO.setCtxToken(token);
		reqDTO.setTaskId(taskId);
		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
		Object obj = res.getPojo();
		this.getManagedBean().setPayLoad(obj);

		CDRequestDataType unspsc = (CDRequestDataType) obj;

		log.info("********isPublished******"+unspsc.isPublished());

		IRequestCodeSO requestSO = (IRequestCodeSO) BusinessDelegate
				.getInstance().getServiceObject("cm", IRequestCodeSO.class);
		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		RequestCodeReqDTO codeDTO = new RequestCodeReqDTO();

		CodificationReqDTO reqCodeDTO = new CodificationReqDTO();
		RequestCodeResDTO resDTO = null;
		this.getManagedBean().setReviewExtCodeList(null);

		if (unspsc.getItemId() != null && unspsc.getItemId().size() > 0) {
			for (String itemId : unspsc.getItemId()) {
				log.info("itemId:" + itemId);

				if(itemId!=null && itemId.trim().length()>0){


					codeDTO.setRequestId(new Long(itemId));
					resDTO = requestSO.retrieveUnspscCodeReview(codeDTO);
					this.getManagedBean().getReviewExtCodeList()
					.add(resDTO.getRequestCode());

					this.getManagedBean().setRequestCode(resDTO.getRequestCode());
				}else{
					codeDTO.setRequestId(new Long(unspsc.getRequestId()));
					resDTO = requestSO.retrieveUnspscCodeReview(codeDTO);
					this.getManagedBean().getReviewExtCodeList()
					.add(resDTO.getRequestCode());
					this.getManagedBean().setRequestCode(resDTO.getRequestCode());
				}
			}
		} else {
			codeDTO.setRequestId(new Long(unspsc.getRequestId()));
			resDTO = requestSO.retrieveUnspscCodeReview(codeDTO);
			this.getManagedBean().getReviewExtCodeList()
			.add(resDTO.getRequestCode());
			this.getManagedBean().setRequestCode(resDTO.getRequestCode());
		}
		
		CodificationReqDTO remarkReqDTO = new CodificationReqDTO();
		remarkReqDTO.setRequestCode(this.getManagedBean().getRequestCode());
		CodificationResDTO remarkResDTO = codificationSO.getCodificationRemarks(remarkReqDTO);
		remarkResDTO.setRequestCode(this.getManagedBean().getRequestCode());
		loadRemark(remarkResDTO, res);
		
		reqCodeDTO.setUnspscCode(resDTO.getRequestCode().getTempCode());
		CodificationResDTO resCodeDTO = codificationSO
				.searchCommodityObj(reqCodeDTO);
		
		//20210201@Jacky - Enhancement#28576 - Start
		log.info("Start #28576 In here: ");
		
		this.getManagedBean().setTypeList(
				resCodeDTO.getCommodityObj().getCmType());
		this.getManagedBean().setBrandList(
				resCodeDTO.getCommodityObj().getCmBrand());
		this.getManagedBean().setMeasurementList(
				resCodeDTO.getCommodityObj().getCmMeasurement());
		resCodeDTO = codificationSO.searchColor(reqCodeDTO);
		this.getManagedBean().setColorList(resCodeDTO.getColorList());

		log.info("Type Unspsc: " + this.getManagedBean().getTypeList().size());
		log.info("Brand Unspsc: " + this.getManagedBean().getBrandList().size());
		log.info("Measurement Unspsc: " + this.getManagedBean().getMeasurementList().size());
		log.info("Color Unspsc: " + this.getManagedBean().getColorList().size());
		//20210201@Jacky - Enhancement#28576 - End

		for (CmRequestCode code : this.getManagedBean().getReviewExtCodeList()) {
			
			if (res.getTaskName().equalsIgnoreCase("Approve and Publish Extension Code") || res.getTaskName().equalsIgnoreCase("Approve Extension Code")) {				code.setReviewedColorName(code.getColorName());
				code.setReviewedBrandName(code.getBrandName());
				code.setReviewedTypeName(code.getTypeName());
				code.setReviewedMeasurementName(code.getMeasurementName());
				code.setReviewedColorName(code.getColorName());

				//init dropdown if input was same
				for(CmColor color: this.getManagedBean().getColorList()){
					if(StringUtils.isNotBlank(code.getReviewedColorName())){
						if(code.getReviewedColorName().trim().equals(color.getColorName().trim())){
							code.setColorNameDD(color.getColorId());
							break;
						}
					}
				}
				for(CmBrand brand: this.getManagedBean().getBrandList()){
					if(StringUtils.isNotBlank(code.getReviewedBrandName())){
						if(code.getReviewedBrandName().trim().equals(brand.getBrandName().trim())){
							code.setBrandNameDD(brand.getBrandId());
							break;
						}
					}
				}
				for(CmType type: this.getManagedBean().getTypeList()){
					if(StringUtils.isNotBlank(code.getReviewedTypeName())){
						if(code.getReviewedTypeName().trim().equals(type.getTypeName().trim())){
							code.setTypeNameDD(type.getTypeId());
							break;
						}
					}
				}
				for(CmMeasurement measurement: this.getManagedBean().getMeasurementList()){
					if(StringUtils.isNotBlank(code.getMeasurementName())){
						if(code.getReviewedMeasurementName().trim().equals(measurement.getMeasurementName().trim())){
							code.setMeasurementNameDD(measurement.getMeasurementId());
							break;
						}
					}
				}
			}
			if (res.getTaskName().equalsIgnoreCase("Endorse Extension Code")) {
				code.setReviewedColorNameII(code.getReviewedColorName());
				code.setReviewedBrandNameII(code.getReviewedBrandName());
				code.setReviewedTypeNameII(code.getReviewedTypeName());
				code.setReviewedMeasurementNameII(code.getReviewedMeasurementName());
			}
			
			code.setRemarkRequired(false);
			code.setRemarkRequiredII(false);
			code.setAddedRemark(false);

		}

		this.getManagedBean().setAction(res.getAction());
		this.getManagedBean().setTaskId(taskId);
		this.getManagedBean().setTaskName(res.getTaskName());
		arrangeOutcomeButton();

		Collections.sort(this.getManagedBean().getTypeList(), new Comparator<CmType>() {
		    @Override
		    public int compare(CmType t1, CmType t2) {
		    	//20210201@Jacky - Enhancement#28576 - Start
				log.info("Start #28576 In here Compare Part: ");
				log.info("CmType one Here: " + t1.getTypeName());
				log.info("CmType Two Here: " + t2.getTypeName());
				//20210201@Jacky - Enhancement#28576 - End
		        return compareFirstChoice(t1.getTypeCode(), t2.getTypeCode(), t1.getTypeName().trim().compareToIgnoreCase(t2.getTypeName().trim()));
		    }

		});
		Collections.sort(this.getManagedBean().getBrandList(), new Comparator<CmBrand>() {
		    @Override
		    public int compare(CmBrand t1, CmBrand t2) {
		        return compareFirstChoice(t1.getBrandCode(), t2.getBrandCode(), t1.getBrandName().trim().compareToIgnoreCase(t2.getBrandName().trim()));
		    }

		});
		Collections.sort(this.getManagedBean().getMeasurementList(), new Comparator<CmMeasurement>() {
		    @Override
		    public int compare(CmMeasurement t1, CmMeasurement t2) {
		        return compareFirstChoice(t1.getMeasurementCode(), t2.getMeasurementCode(), t1.getMeasurementName().trim().compareToIgnoreCase(t2.getMeasurementName().trim()));
		    }

		});
		Collections.sort(this.getManagedBean().getColorList(), new Comparator<CmColor>() {
		    @Override
		    public int compare(CmColor t1, CmColor t2) {
		        return compareFirstChoice(t1.getColorCode(), t2.getColorCode(), t1.getColorName().trim().compareToIgnoreCase(t2.getColorName().trim()));
		    }

		});
		IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IStatusSO.class);
		String languageCode = new FacesScopes().getUserLocale().getLanguage();

		StatusReqDTO reqStatusDTO = new StatusReqDTO();

		reqStatusDTO.setLanguageCode(languageCode);
		reqStatusDTO.setStatusId(res.getDocStatus().longValue());
		StatusResDTO resStatusDTO = statusSO
				.retrieveStatusDescById(reqStatusDTO);

		this.getManagedBean().setDocStatus(
				resStatusDTO.getPmStatusDesc() != null ? resStatusDTO
						.getPmStatusDesc().getStatusName() : "Review");
		assertView("Review Extension Code", obj);
	}


	public void claimTask()  {
		log.info("**********claimTask");

		BPMTaskListBackingBean bb = new BPMTaskListBackingBean();
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);



		FacesScopes face = new FacesScopes();
		Object token = face.getBPMAuthenticationCtxToken();

		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setTaskId(this.getManagedBean().getTaskId());
		reqDTO.setCtxToken(token);
		bpmSO.claimTask(reqDTO);


		bb.getManagedBean().getTaskListGroupDataModel().reloadData();
		bb.getManagedBean().getTaskListMyTaskDataModel().reloadData();
		this.getManagedBean().setClaim(true);

		//return "/xhtml/bpmtasklist/taskListView.xhtml?faces-redirect=true";


	}
	
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	public String userImagePathWithDefaultImage(String loginId){
		log.info("userImagePathWithDefaultImage");
		String imagePath = "";
	
		imagePath = userImagePath(loginId);	
		if(StringUtils.isBlank(imagePath)){
			imagePath = ProfileManagementConst.DEFAULT_PORTRAIT;
		}
		return imagePath;
	}
	
	private void loadRemark(CodificationResDTO remarkResDTO, BpmResponseDTO res){
		List<CmRemark> cmRemarkList = new ArrayList<CmRemark>();
		CmRemark newRemark = new CmRemark();
		newRemark.setBrandNew(true);
		newRemark.setEditable(true);
		newRemark.setCreatedBy(getFacesScopes().getUserSessionBean().getUserId());
		newRemark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		newRemark.setChangedBy(newRemark.getCreatedBy());
		newRemark.setChangedDate(newRemark.getCreatedDate());
		newRemark.setCreatedUsername(getFacesScopes().getUserSessionBean().getUserName());
		newRemark.setUserId(getFacesScopes().getUserSessionBean().getLoginId());
		newRemark.setDocId(remarkResDTO.getRequestCode().getRequestId());
		newRemark.setDocNo(res.getDocNumber());
		newRemark.setDocType(res.getDocType());
		
		if(!isAllSupplier()){
			cmRemarkList.add(newRemark);
			if( remarkResDTO.getRemarkList()!=null &&  remarkResDTO.getRemarkList().size()>0){
				for(CmRemark remark: remarkResDTO.getRemarkList()){
					remark.setBrandNew(false);
					cmRemarkList.add(remark);
				}
			}
		}else{
			List<CmRemark> remarkList = getLastRemarkBeforeCompletion(this.getManagedBean().getRequestCode());
			if(remarkList!=null && remarkList.size() > 0){
				for(CmRemark remark: remarkList){
					remark.setBrandNew(false);
					cmRemarkList.add(remark);
				}
			}
		}

		this.getManagedBean().setSelectedRemarks(cmRemarkList);
	}
	
	private void navigateTo(int outcomeCheck, String url){
		FacesContext fc = FacesContext.getCurrentInstance();
		this.getManagedBean().setRenderV2(this.getManagedBean().getAction().length==outcomeCheck);
		log.info("Rendering V2 status = " + this.getManagedBean().isRenderV2());
		log.info("Task name = " + this.getManagedBean().getTaskName());
		log.info("View rendered = " + url);
		fc.getApplication().getNavigationHandler()
		.handleNavigation(fc, null, url);
	}
	private void assertView(String processGroup, Object obj){
		log.info("Process group = " + processGroup);

		String taskName = this.getManagedBean().getTaskName();
		if (taskName.equalsIgnoreCase("Create Temporary Code")) {
			navigateTo(3, "reviewBeforeCreateTempCode");
		}else if (taskName.equalsIgnoreCase("Revise Temporary Code")){
			navigateTo(3, "reviewBeforeCreateTempCode");
		} else if (taskName.equalsIgnoreCase("Create Extension Code")){
			navigateTo(0, "reviewBeforeCreateExtCode");
		} else if (taskName.equalsIgnoreCase("Select Recommended")){
			navigateTo(0, "selectRecommendUnspscCode");
		}else  if (taskName.equalsIgnoreCase("Review Request")){
			navigateTo(4, "reviewUnspscCode");
		}else  if (taskName.equalsIgnoreCase("Revise Request")){
			navigateTo(4, "reviewUnspscCode");
		}else if (taskName.equalsIgnoreCase("Approve and Publish Extension Code")) {
			navigateTo(3, "reviewApproveExtensionCode");
		}else if (taskName.equalsIgnoreCase("Approve Extension Code")) {
			navigateTo(0, "reviewApproveExtensionCode");
		} else if (taskName.equalsIgnoreCase("Endorse Extension Code")) {
			navigateTo(0, "reviewApproveExtensionCodeII");
		} else if (taskName.equalsIgnoreCase("Publish Extension Code")) {
			navigateTo(0, "reviewApproveExtensionCodeII");
		} else if (taskName.equalsIgnoreCase("Publish Result Notification")) {
//			if(processGroup.equalsIgnoreCase("Review Extension Code")){
//				if (((CDRequestDataType)obj).isPublished()) {
					navigateTo(0, "resultNotification.xhtml");
//				} else {
//					navigateTo(0, "reviewExtensionCodeFlow2");
//				}
//			}else{
//				navigateTo(0, "reviewApproveExtensionCodeII.xhtml");
//			}
		}else if(taskName.equalsIgnoreCase("Recommend Result Notification")){
			if(processGroup.equalsIgnoreCase("Review Extension Code")){
				if(this.getManagedBean().getRequestCode().getCmRecommendCodes().size()>0){
					navigateTo(0, "unspscResultNotification");
				}else{
					navigateTo(0, "reviewExtensionCodeFlow2");
				}
			}else{
				navigateTo(0, "unspscResultNotification");
			}
		}else if(taskName.equalsIgnoreCase("Review Extension Code") || taskName.equalsIgnoreCase("Revise Extension Code")) {
			navigateTo(4, "reviewExtensionCodeFlow");
		}
	}
	
	public void checkTempCodeForReset(){
		String compareType = "P";
		if(StringUtils.isNotBlank(this.getManagedBean().getRequestCode().getTempCode()) &&
				StringUtils.isNotBlank(this.getManagedBean().getRequestCode().getReviewedTempCode()) &&
					this.getManagedBean().getTaskName().equalsIgnoreCase("Revise Temporary Code")){
			if(Integer.parseInt(this.getManagedBean().getRequestCode().getTempCode().substring(0,1))>=7){
				compareType = "S";
			}
			if(compareType.equalsIgnoreCase(this.getManagedBean().getRequestCode().getItemType())){
				CodificationReqDTO lovReqDTO = new CodificationReqDTO();
				CodificationResDTO lovResDTO = null;
				ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
						.getInstance().getServiceObject("cm", ICodificationSO.class);

				//segment
				lovReqDTO.setUnspscCode(StringUtils.rightPad(this.getManagedBean().getRequestCode().getTempCode().substring(0,2), 8, '0'));
				lovResDTO = unspscSO.searchUnspscObj(lovReqDTO);
				setUnvailableIfNull(lovResDTO.getUnspscObj());
				this.getManagedBean().setSelectedLovUnspscSegment(lovResDTO.getUnspscObj());
				//family
				lovReqDTO.setUnspscCode(StringUtils.rightPad(this.getManagedBean().getRequestCode().getTempCode().substring(0,4), 8, '0'));
				lovResDTO = unspscSO.searchUnspscObj(lovReqDTO);
				setUnvailableIfNull(lovResDTO.getUnspscObj());
				this.getManagedBean().setSelectedLovUnspscfamily(lovResDTO.getUnspscObj());
				//class
				lovReqDTO.setUnspscCode(StringUtils.rightPad(this.getManagedBean().getRequestCode().getTempCode().substring(0,6), 8, '0'));
				lovResDTO = unspscSO.searchUnspscObj(lovReqDTO);
				setUnvailableIfNull(lovResDTO.getUnspscObj());
				this.getManagedBean().setSelectedLovUnspscClass(lovResDTO.getUnspscObj());
				//com
				lovReqDTO.setUnspscCode(this.getManagedBean().getRequestCode().getTempCode());
				lovResDTO = unspscSO.searchUnspscObj(lovReqDTO);
				setUnvailableIfNull(lovResDTO.getUnspscObj());
				this.getManagedBean().setSelectedLovUnspsc(lovResDTO.getUnspscObj());
				checkIfLovEditable(null);

			}else{
				this.getManagedBean().setSelectedLovUnspscSegment(null);
				this.getManagedBean().setSelectedLovUnspscfamily(null);;
				this.getManagedBean().setSelectedLovUnspscClass(null);
				this.getManagedBean().setSelectedLovUnspsc(null);
				checkIfLovEditable(null);
			}
		}
	}	
	
	public void setUnvailableIfNull(CmUnspsc obj){//hack on empty english title for cmunspsc
		if(obj.getUnspscTitleEng()==null){
			obj.setUnspscTitleEng(obj.getUnspscTitle());
		}
	}
	public void checkIfLovEditable(CmUnspsc obj){
		if(StringUtils.isNotBlank(this.getManagedBean().getRequestCode().getTempCode()) && StringUtils.isNotBlank(this.getManagedBean().getRequestCode().getReviewedTempCode())){
			String rtc = this.getManagedBean().getRequestCode().getReviewedTempCode();
			String tc = this.getManagedBean().getRequestCode().getTempCode();

			boolean checkSe =  this.getManagedBean().getSelectedLovUnspscSegment() !=null ? StringUtils.rightPad(this.getManagedBean().getSelectedLovUnspscSegment().getUnspscCode().substring(0,2),8,'0').equals(StringUtils.rightPad(rtc.substring(0,2),8,'0')) : true;
			boolean checkFa = this.getManagedBean().getSelectedLovUnspscfamily() !=null ? StringUtils.rightPad(this.getManagedBean().getSelectedLovUnspscfamily().getUnspscCode().substring(0,4),8,'0').equals(StringUtils.rightPad(tc.substring(0,2).concat(rtc.substring(2,4)),8,'0')) : true ;
			boolean checkCl =  this.getManagedBean().getSelectedLovUnspscClass() !=null ? StringUtils.rightPad(this.getManagedBean().getSelectedLovUnspscClass().getUnspscCode().substring(0,6),8,'0').equals(StringUtils.rightPad(tc.substring(0,4).concat(rtc.substring(4,6)),8,'0')) : true ;
			boolean checkCo = this.getManagedBean().getSelectedLovUnspsc() != null ? this.getManagedBean().getSelectedLovUnspsc().getUnspscCode().equals(tc.substring(0,6).concat(rtc.substring(6,8))) : true ;
			
			this.getManagedBean().setDisableLovSegment(!checkSe);
			this.getManagedBean().setDisableLovFamily(!checkFa);
			this.getManagedBean().setDisableLovClass(!checkCl);
			this.getManagedBean().setDisableLovCommodity(!checkCo);
		}
	}
	
	public void preRenderView() {
		log.debug("**********preRenderView**************");
		this.getManagedBean().setSelectedLovUnspscSegment(null);
		this.getManagedBean().setSelectedLovUnspscfamily(null);
		this.getManagedBean().setSelectedLovUnspscClass(null);
		this.getManagedBean().setSelectedLovUnspsc(null);
		this.getManagedBean().setDisableRevise(false);
		
		this.getManagedBean().setDisableLovClass(false);
		this.getManagedBean().setDisableLovCommodity(false);
		this.getManagedBean().setDisableLovFamily(false);
		this.getManagedBean().setDisableLovSegment(false);

		BPMTaskListBackingBean bb = new BPMTaskListBackingBean();
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		String claim = originalServletRequest.getParameter("claim");
		log.info("taskId:" + taskId);
		log.info("claim:" + claim);

		Boolean b = Boolean.valueOf(claim);
		bb.getManagedBean().setClaim(b);
		bb.getManagedBean().setTaskId(taskId);


		FacesScopes face = new FacesScopes();
		Object token = face.getBPMAuthenticationCtxToken();
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);

		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setCtxToken(token);
		reqDTO.setTaskId(taskId);

		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
		Object obj = res.getPojo();
		this.getManagedBean().setPayLoad(obj);
		CDRequestDataType unspsc = (CDRequestDataType) obj;

		IRequestCodeSO requestSO = (IRequestCodeSO) BusinessDelegate
				.getInstance().getServiceObject("cm", IRequestCodeSO.class);
		RequestCodeReqDTO codeDTO = new RequestCodeReqDTO();
		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		CodificationReqDTO remarkReqDTO = new CodificationReqDTO();

		this.getManagedBean().setReviewExtCodeList(null);

		if (unspsc.getItemId() != null && unspsc.getItemId().size() > 0) {
			for (String itemId : unspsc.getItemId()) {
				log.info("itemId:" + itemId);

				codeDTO.setRequestId(new Long(itemId));
				RequestCodeResDTO resDTO = requestSO
						.retrieveUnspscCodeReview(codeDTO);
				this.getManagedBean().getReviewExtCodeList()
				.add(resDTO.getRequestCode());
				this.getManagedBean().setRequestCode(resDTO.getRequestCode());
			}
		} else {
			codeDTO.setRequestId(new Long(unspsc.getRequestId()));
			RequestCodeResDTO resDTO = requestSO
					.retrieveUnspscCodeReview(codeDTO);
			this.getManagedBean().getReviewExtCodeList()
			.add(resDTO.getRequestCode());
			this.getManagedBean().setRequestCode(resDTO.getRequestCode());
		}
		remarkReqDTO.setRequestCode(this.getManagedBean().getRequestCode());
		CodificationResDTO remarkResDTO = codificationSO.getCodificationRemarks(remarkReqDTO);
		remarkResDTO.setRequestCode(this.getManagedBean().getRequestCode());
		loadRemark(remarkResDTO, res);

		this.getManagedBean().setTaskName(res.getTaskName());
		this.getManagedBean().setTaskId(taskId);
		this.getManagedBean().setAction(res.getAction());
		arrangeOutcomeButton();

		IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IStatusSO.class);
		String languageCode = new FacesScopes().getUserLocale().getLanguage();

		StatusReqDTO reqStatusDTO = new StatusReqDTO();
		reqStatusDTO.setLanguageCode(languageCode);
		reqStatusDTO.setStatusId(res.getDocStatus().longValue());
		StatusResDTO resStatusDTO = statusSO
				.retrieveStatusDescById(reqStatusDTO);

		this.getManagedBean().setDocStatus(
				resStatusDTO.getPmStatusDesc() != null ? resStatusDTO
						.getPmStatusDesc().getStatusName() : "Review");
		
		if(StringUtils.isNotBlank(this.getManagedBean().getRequestCode().getTempCode()) && 
				StringUtils.isNotBlank(this.getManagedBean().getRequestCode().getReviewedTempCode()) && 
					res.getTaskName().equalsIgnoreCase("Revise Temporary Code")){
			this.getManagedBean().setDisableRevise(true);
			CodificationReqDTO lovReqDTO = new CodificationReqDTO();
			CodificationResDTO lovResDTO = null;
			ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
					.getInstance().getServiceObject("cm", ICodificationSO.class);

			lovReqDTO.setUnspscCode(StringUtils.rightPad(this.getManagedBean().getRequestCode().getTempCode().substring(0,2), 8, '0'));
			lovResDTO = unspscSO.searchUnspscObj(lovReqDTO);
			setUnvailableIfNull(lovResDTO.getUnspscObj());
			this.getManagedBean().setSelectedLovUnspscSegment(lovResDTO.getUnspscObj());

			lovReqDTO.setUnspscCode(StringUtils.rightPad(this.getManagedBean().getRequestCode().getTempCode().substring(0,4), 8, '0'));
			lovResDTO = unspscSO.searchUnspscObj(lovReqDTO);
			setUnvailableIfNull(lovResDTO.getUnspscObj());
			this.getManagedBean().setSelectedLovUnspscfamily(lovResDTO.getUnspscObj());

			lovReqDTO.setUnspscCode(StringUtils.rightPad(this.getManagedBean().getRequestCode().getTempCode().substring(0,6), 8, '0'));
			lovResDTO = unspscSO.searchUnspscObj(lovReqDTO);
			setUnvailableIfNull(lovResDTO.getUnspscObj());
			this.getManagedBean().setSelectedLovUnspscClass(lovResDTO.getUnspscObj());

			lovReqDTO.setUnspscCode(this.getManagedBean().getRequestCode().getTempCode());
			lovResDTO = unspscSO.searchUnspscObj(lovReqDTO);
			setUnvailableIfNull(lovResDTO.getUnspscObj());
			this.getManagedBean().setSelectedLovUnspsc(lovResDTO.getUnspscObj());
			checkIfLovEditable(null);
		}
		assertView("Review UNSPSC Code",obj);
	}

	public SearchFilterBean getColorFilterBean() {
		return colorFilterBean;
	}

	public void setColorFilterBean(SearchFilterBean colorFilterBean) {
		this.colorFilterBean = colorFilterBean;
	}

	public SearchFilterBean getTypeFilterBean() {
		return typeFilterBean;
	}

	public void setTypeFilterBean(SearchFilterBean typeFilterBean) {
		this.typeFilterBean = typeFilterBean;
	}

	public SearchFilterBean getBrandFilterBean() {
		return brandFilterBean;
	}

	public void setBrandFilterBean(SearchFilterBean brandFilterBean) {
		this.brandFilterBean = brandFilterBean;
	}

	public SearchFilterBean getMeasurementFilterBean() {
		return measurementFilterBean;
	}

	public void setMeasurementFilterBean(SearchFilterBean measurementFilterBean) {
		this.measurementFilterBean = measurementFilterBean;
	}

	public String backToReviewUnspsc() {

		return "reviewBeforeCreateTempCode?faces-redirect=true";
	}

	public String backToCodificationSearch() {

		return "codificationView?faces-redirect=true";
	}

	public String requestExtensionCode() {

		if (this.getManagedBean().getRequestExtCodeList() != null) {
			this.getManagedBean().getRequestExtCodeList().clear();
		}

		CodificationBackingBean codeBB = new CodificationBackingBean();

		this.getManagedBean()
		.getSelectedLovUnspsc()
		.setUnspscCode(
				codeBB.getManagedBean().getSelectedUNSPSCObj()
				.getUnspscCode());
		this.getManagedBean()
		.getSelectedLovUnspsc()
		.setUnspscTitle(
				codeBB.getManagedBean().getSelectedUNSPSCObj()
				.getUnspscTitle());
		return "extensionCodeRequestForm?faces-redirect=true";
	}

	public String backToExtensionSearch() {

		return "extensionSearch?faces-redirect=true";
	}

	public void removeRecommendCode(int index) {

		this.getManagedBean().getRequestCode().getCmRecommendCodes()
		.remove(index);

	}

	public void removeExtensionRecord() {

		this.getManagedBean().getRequestExtCodeList().remove(0);// there is only
		// one record

	}

	/*public void addNewBpmRemark() {

		CmRequestRemark remark = new CmRequestRemark();
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		log.info("pmUser:" + pmUser);
		if (pmUser != null) {
			log.info("pmUser.getUserName():" + pmUser.getUserName());
			remark.setCreatedUser(pmUser.getUserName());
		}

		System.out
		.println("remark.getCreatedUser():" + remark.getCreatedUser());

		this.getManagedBean().getReviewExtCodeList().get(0)
		.getCmRequestRemarks().add(remark);

		for (CmRequestCode code : this.getManagedBean().getReviewExtCodeList()) {
			code.setRemarkRequired(false);
			code.setRemarkRequiredII(false);
			code.setAddedRemark(true);
		}

	}*/

	/*public void selectRecommendCode() {

		log.info("this.getManagedBean().getSelectedRecommendCode():"+this.getManagedBean().getSelectedRecommendCode().isBrandNew());
		log.info("this.getManagedBean().getSelectedRecommendCode()title:"+this.getManagedBean().getSelectedRecommendCode().getUnspscTitle());


		this.getManagedBean().getRequestCode().getCmRecommendCodes().set(0, this.getManagedBean().getSelectedRecommendCode());

		log.info("size:"+this.getManagedBean().getRequestCode().getCmRecommendCodes().size());
		if(this.getManagedBean().getRequestCode().getCmRecommendCodes()!=null && this.getManagedBean().getRequestCode().getCmRecommendCodes().size()>1){
			this.getManagedBean().getRequestCode().getCmRecommendCodes().remove(1);
		}

		for(CmRecommendCode code1: this.getManagedBean().getRequestCode().getCmRecommendCodes()){
			log.info("getUnspscTitle:"+code1.getUnspscTitle());
		}

	}
	 */
	public String requestUnspscCode() {
		renewSesionObject();

		if (this.getManagedBean().getRequestExtCodeList() != null) {
			this.getManagedBean().getRequestExtCodeList().clear();
		}
		this.getManagedBean().getRequestCode().setItemType("P");
		return "unspscRequestForm?faces-redirect=true";
	}

	public List<CmType> autoCompleteType(String query) {

		log.info("query:::::::::::::::::::::::::::::: " + query);
		List<CmType> suggestions = new ArrayList<CmType>();

		//20210407@Jacky - Enhancement#28576 - Start
		log.info("this.getManagedBean().getTypeList() = "
				+ this.getManagedBean().getTypeList().size());

		for (CmType type : this.getManagedBean().getTypeList()) {
		//20210407@Jacky - Enhancement#28576 - End
			//20210201@Jacky - Enhancement#28576 - Start
			log.info("Current Type is = " + type.getTypeName());
			//20210201@Jacky - Enhancement#28576 - End
			if (type.getTypeName() != null) {

				if (type.getTypeName().toLowerCase()
						.startsWith(query.toLowerCase())) {
					suggestions.add(type);
				}
			}
			//20210201@Jacky - Bug#30013 - Start
			if(type.getTypeCode() != null){
				if(type.getTypeCode().toLowerCase().startsWith(query.toLowerCase())){
					suggestions.add(type);
				}
			}
			//20210201@Jacky - Bug#30013 - Start
		}

		return suggestions;
	}

	public List<CmBrand> autoCompleteBrand(String query) {

		log.info("query:::::::::::::::::::::::::::::: " + query);
		List<CmBrand> suggestions = new ArrayList<CmBrand>();
		//20210407@Jacky - Enhancement#28576 - Start
		for (CmBrand brand : this.getManagedBean().getBrandList()) {
		//20210407@Jacky - Enhancement#28576 - Start
			//20210201@Jacky - Enhancement#28576 - Start
			log.info("Current Brand is = " + brand.getBrandName());
			//20210201@Jacky - Enhancement#28576 - End
			if (brand.getBrandName() != null) {
				if (brand.getBrandName().toLowerCase()
						.startsWith(query.toLowerCase())) {
					suggestions.add(brand);
				}
			}
			//20210201@Jacky - Bug#30013 - Start
			if(brand.getBrandCode() != null){
				if(brand.getBrandCode().toLowerCase().startsWith(query.toLowerCase())){
					suggestions.add(brand);
				}
			}
			//20210201@Jacky - Bug#30013 - End
		}

		return suggestions;
	}

	public List<CmMeasurement> autoCompleteMeasurement(String query) {

		log.info("query:::::::::::::::::::::::::::::: " + query);
		List<CmMeasurement> suggestions = new ArrayList<CmMeasurement>();
		//20210407@Jacky - Enhancement#28576 - Start
		for (CmMeasurement brand : this.getManagedBean().getMeasurementList()) {
		//20210407@Jacky - Enhancement#28576 - Start
			//20210201@Jacky - Enhancement#28576 - Start
			log.info("Current Measurement is = " + brand.getMeasurementName());
			//20210201@Jacky - Enhancement#28576 - End
			if (brand.getMeasurementName() != null) {
				if (brand.getMeasurementName().toLowerCase()
						.startsWith(query.toLowerCase())) {
					suggestions.add(brand);
				}
			}
			//20210201@Jacky - Bug#30013 - Start
			if(brand.getMeasurementCode() != null){
				if(brand.getMeasurementCode().toLowerCase().startsWith(query.toLowerCase())){
					suggestions.add(brand);
				}
			}
			//20210201@Jacky - Bug#30013 - End
		}

		return suggestions;
	}

	public List<CmColor> autoCompleteColor(String query) {

		log.info("query:::::::::::::::::::::::::::::: " + query);
		List<CmColor> suggestions = new ArrayList<CmColor>();

		for (CmColor color : this.getManagedBean().getColorList()) {
			if (color.getColorName() != null) {
				if (color.getColorName().toLowerCase()
						.startsWith(query.toLowerCase())) {
					suggestions.add(color);
				}
			}
			//20210201@Jacky - Bug#30013 - Start
			if(color.getColorCode() != null){
				if(color.getColorCode().toLowerCase().startsWith(query.toLowerCase())){
					suggestions.add(color);
				}
			}
			//20210201@Jacky - Bug#30013 - End
		}

		return suggestions;
	}

	public boolean checkRemark(String param1, String param2) {

		if (!param1.equalsIgnoreCase(param2)) {
			return true;
		}
		return false;
	}

	public void requestNewColor() {

		if (this.getManagedBean().getRequestExtCode().isRequestColor()) {
			this.getManagedBean().setColorDisable(false);
		} else {
			this.getManagedBean().setColorDisable(true);
			this.getManagedBean().getRequestExtCode().setColorName("");
		}
	}

	public void requestNewType() {

		if (this.getManagedBean().getRequestExtCode().isRequestType()) {
			this.getManagedBean().setTypeDisable(false);

		} else {
			this.getManagedBean().setTypeDisable(true);
			this.getManagedBean().getRequestExtCode().setTypeName("");
		}
	}

	public void requestNewBrand() {

		if (this.getManagedBean().getRequestExtCode().isRequestBrand()) {
			this.getManagedBean().setBrandDisable(false);
		} else {
			this.getManagedBean().setBrandDisable(true);
			this.getManagedBean().getRequestExtCode().setBrandName("");
		}
	}

	public void requestNewMeasurement() {

		if (this.getManagedBean().getRequestExtCode().isRequestMeasurement()) {
			this.getManagedBean().setMeasurementDisable(false);
		} else {
			this.getManagedBean().setMeasurementDisable(true);
			this.getManagedBean().getRequestExtCode().setMeasurementName("");
		}
	}

	public void preRenderUnspscRequestView() {
		// clear requestCode object
		// this.getManagedBean().setRequestCode(new CmRequestCode());

		renewSesionObject();

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
		.handleNavigation(fc, null, "unspscRequestFormByMenu");
	}

	public int productOrServiceOperator() {
		if (this.getManagedBean().getRequestCode() != null) {
			if (this.getManagedBean().getRequestCode().getItemType()
					.equalsIgnoreCase("P")) {
				return 5; // means less than (7000000)
			} else {
				return 4; // means greater than (7000000)
			}
		}
		return -1;
	}

	public void checkingTypeRedundency(String typeInput, boolean isSelectFromList) {
		log.info(".....checking redundancy for typeInput=" + typeInput);
		this.getManagedBean().setTypeRedundancy(false);
		if (typeInput != null && !typeInput.equals("")) {
			for (CmType type : this.getManagedBean().getTypeList()) {
				if (!StringUtils.isBlank(type.getTypeName())) {
					if (type.getTypeName().toLowerCase()
							.equals(typeInput.toLowerCase())) {
						this.getManagedBean().setTypeRedundancy(true);
						if(isSelectFromList){
							throw new EpException(MessageUtils
									.getMessage(MessageConst.ERROR_715068, new String[] { ""
											+ typeInput },
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale()));
						}
						throw new EpException(
								MessageConst.ERROR_716009,
								new String[] { ResourceBundleUtils
										.getMessage(
												"lbl.cm.type",
												ResourceBundleUtils.BUNDLE_COMMON_LABELS,
												new FacesScopes()
												.getUserLocale()) });
					}
				}
			}
		}
	}

	public void checkingBrandRedundency(String brandInput,  boolean isSelectFromList) {
		log.info(".....checking redundancy for brandInput=" + brandInput);
		this.getManagedBean().setBarndRedundancy(false);
		if (brandInput != null && !brandInput.equals("")) {
			for (CmBrand brand : this.getManagedBean().getBrandList()) {
				if (!StringUtils.isBlank(brand.getBrandName())) {
					if (brand.getBrandName().toLowerCase()
							.equals(brandInput.toLowerCase())) {

						this.getManagedBean().setBarndRedundancy(true);
						if(isSelectFromList){
							throw new EpException(MessageUtils
									.getMessage(MessageConst.ERROR_715068, new String[] { ""
											+ brandInput },
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale()));
						}
						throw new EpException(
								MessageConst.ERROR_716009,
								new String[] { ResourceBundleUtils
										.getMessage(
												"lbl.cm.brand",
												ResourceBundleUtils.BUNDLE_COMMON_LABELS,
												new FacesScopes()
												.getUserLocale()) });
					}
				}
			}
		}
	}

	public void checkingMeasurmentRedundency(String MeasurmentInput,  boolean isSelectFromList) {
		log.info(".....checking redundancy for MeasurmentInput="
				+ MeasurmentInput);
		this.getManagedBean().setMeasurRedundancy(false);
		if (MeasurmentInput != null && !MeasurmentInput.equals("")) {
			for (CmMeasurement measur : this.getManagedBean()
					.getMeasurementList()) {

				if (!StringUtils.isBlank(measur.getMeasurementName())) {
					if (measur.getMeasurementName().toLowerCase()
							.equals(MeasurmentInput.toLowerCase())) {
						this.getManagedBean().setMeasurRedundancy(true);
						if(isSelectFromList){
							throw new EpException(MessageUtils
									.getMessage(MessageConst.ERROR_715068, new String[] { ""
											+ MeasurmentInput },
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale()));
						}
						throw new EpException(
								MessageConst.ERROR_716009,
								new String[] { ResourceBundleUtils
										.getMessage(
												"lbl.cm.measurement",
												ResourceBundleUtils.BUNDLE_COMMON_LABELS,
												new FacesScopes()
												.getUserLocale()) });
					}
				}
			}
		}
	}

	public String backToPendingCodified() {
		return "searchUnspscToAssign";
	}

	public void checkingColorRedundency(String colorInput,  boolean isSelectFromList) {
		log.info(".....checking redundancy for colorInput=" + colorInput);
		log.info("colorList " + this.getManagedBean().getColorList());
		this.getManagedBean().setColorRedundancy(false);
		if (colorInput != null && !colorInput.equals("")) {
			for (CmColor color : this.getManagedBean().getColorList()) {
				if (!StringUtils.isBlank(color.getColorName())) {
					if (color.getColorName().toLowerCase()
							.equals(colorInput.toLowerCase())) {
						this.getManagedBean().setColorRedundancy(true);
						log.info("this.getManagedBean().isColorRedundancy()="
								+ this.getManagedBean().isColorRedundancy());
						if(isSelectFromList){
							throw new EpException(MessageUtils
									.getMessage(MessageConst.ERROR_715068, new String[] { ""
											+ colorInput },
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale()));
						}
						throw new EpException(
								MessageConst.ERROR_716009,
								new String[] { ResourceBundleUtils
										.getMessage(
												"lbl.cm.color",
												ResourceBundleUtils.BUNDLE_COMMON_LABELS,
												new FacesScopes()
												.getUserLocale()) });

					}
				}
			}
			log.info("end no exception");
		}
	}


	public void checkRecomendOrSubmit(){

		Locale locale = new FacesScopes().getUserLocale();
		boolean colorRecommend = false;
		boolean brandRecommend = false;
		boolean typeRecommend = false;
		boolean meaRecommend = false;

		if (this.getManagedBean().getReviewExtCodeList() != null
				&& this.getManagedBean().getReviewExtCodeList().size() > 0) {

			for (CmRequestCode code : this.getManagedBean().getReviewExtCodeList()) {

				if (code.getColorNameDD() != null && code.isRequestColor()) { 

					colorRecommend = true;

				}else if (!code.isRequestColor()){
					colorRecommend = true;
				}

				if (code.getBrandNameDD() != null) {

					brandRecommend = true;


				}else if (!code.isRequestBrand()){
					brandRecommend = true;

				}

				if (code.getTypeNameDD() != null ) {

					typeRecommend = true;

				}else if (!code.isRequestType()){

					typeRecommend = true;
				}

				if (code.getMeasurementNameDD() != null) {

					meaRecommend = true;

				}else if (!code.isRequestMeasurement()){

					meaRecommend = true;
				}

				if(colorRecommend && brandRecommend && typeRecommend && meaRecommend){
					this.getManagedBean().setRecommendExtension(true);
					this.getManagedBean().setExtensionConfirmationMsg(ResourceBundleUtils.getMessage("lbl.cm.extension.confirmation.recommend",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));

				}else{
					this.getManagedBean().setRecommendExtension(false);
					this.getManagedBean().setExtensionConfirmationMsg(ResourceBundleUtils.getMessage("lbl.cm.extension.confirmation.proceed",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));

				}

			}
		}
		
		log.info("getExtensionConfirmationMsg:"+this.getManagedBean().getExtensionConfirmationMsg());


	}


	public String retrieveMofNo(long supplierId, String supplierType){

		MofAccountReqDTO reqDTO = new MofAccountReqDTO();
		String mofNo = "N/A";
		IMofAccountSO mofSO = (IMofAccountSO) BusinessDelegate.getInstance().getServiceObject("sm", IMofAccountSO.class);
		reqDTO.setSupplierId(supplierId);
		reqDTO.setSupplierType(supplierType);
		MofAccountResDTO resDTO = mofSO.retriveMOFAccountBySupplierIdAndType(reqDTO);

		if( resDTO.getMofAccount()!=null){
			mofNo = resDTO.getMofAccount().getMofNo();
		}

		return mofNo;
	}


	public void editExtensionRequest(int index){

		this.getManagedBean().setEditMode(true);
		this.getManagedBean().setEditModeIndex(index);
		if(this.getManagedBean().getRequestExtCodeList()!=null && this.getManagedBean().getRequestExtCodeList().size()>0){
			CmRequestCode code = this.getManagedBean().getRequestExtCodeList().get(index);


			ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
					.getInstance().getServiceObject("cm", ICodificationSO.class);

			CodificationReqDTO reqDTO = new CodificationReqDTO();
			reqDTO.setUnspscCode(this.getManagedBean().getSelectedLovUnspsc()
					.getUnspscCode());

			CodificationResDTO resDTO = codificationSO.searchCommodityObj(reqDTO);

			this.getManagedBean()
			.getRequestExtCode()
			.setTempCode(
					this.getManagedBean().getSelectedLovUnspsc()
					.getUnspscCode());
			this.getManagedBean()
			.getRequestExtCode()
			.setTempTitle(
					this.getManagedBean().getSelectedLovUnspsc()
					.getUnspscTitle());
			this.getManagedBean().setTypeList(resDTO.getCommodityObj().getCmType());
			this.getManagedBean().setBrandList(
					resDTO.getCommodityObj().getCmBrand());
			this.getManagedBean().setMeasurementList(
					resDTO.getCommodityObj().getCmMeasurement());
			resDTO = codificationSO.searchColor(reqDTO);
			this.getManagedBean().setColorList(resDTO.getColorList());

			this.getManagedBean().setRequestExtCode(code);



			if(code.isRequestBrand()){
				log.info("********isRequestBrand*****"+this.getManagedBean().getRequestExtCode().isRequestBrand());
				this.getManagedBean().setSelectedBrand(null);
				this.getManagedBean().setBrandDisable(false);  //text box

			}else{
				this.getManagedBean().setSelectedBrand(new CmBrand());
				this.getManagedBean().getSelectedBrand().setBrandName(code.getBrandName());
				this.getManagedBean().getSelectedBrand().setBrandId(code.getBrandId());
				this.getManagedBean().setBrandDisable(true);  //text box
				this.getManagedBean().getRequestExtCode().setBrandName("");
			}


			if(code.isRequestType()){

				this.getManagedBean().setSelectedType(null);
				this.getManagedBean().setTypeDisable(false);  //text box

			}else{
				this.getManagedBean().setSelectedType(new CmType());
				this.getManagedBean().getSelectedType().setTypeName(code.getTypeName());
				this.getManagedBean().getSelectedType().setTypeId(code.getTypeId());
				this.getManagedBean().setTypeDisable(true);  //text box
				this.getManagedBean().getRequestExtCode().setTypeName("");

			}

			if(code.isRequestColor()){

				this.getManagedBean().setSelectedColor(null);
				this.getManagedBean().setColorDisable(false);  //text box


			}else{
				this.getManagedBean().setSelectedColor(new CmColor());
				this.getManagedBean().getSelectedColor().setColorName(code.getColorName());
				this.getManagedBean().getSelectedColor().setColorId(code.getColorId());
				this.getManagedBean().setColorDisable(true);  //text box
				this.getManagedBean().getRequestExtCode().setColorName("");

			}


			if(code.isRequestMeasurement()){

				this.getManagedBean().setSelectedMeasurement(null);
				this.getManagedBean().setMeasurementDisable(false);  //text box

			}else{
				this.getManagedBean().setSelectedMeasurement(new CmMeasurement());
				this.getManagedBean().getSelectedMeasurement().setMeasurementName(code.getMeasurementName());
				this.getManagedBean().getSelectedMeasurement().setMeasurementId(code.getMeasurementId());
				this.getManagedBean().setMeasurementDisable(true);  //text box
				this.getManagedBean().getRequestExtCode().setMeasurementName("");

			}

		}
	}

	public void deleteExtensionRequest(int index){



		this.getManagedBean().getRequestExtCodeList().remove(index);

	}

	/**
	 * @return the unspscIdFilterBean
	 */
	public SearchFilterBean getUnspscIdFilterBean() {
		return unspscIdFilterBean;
	}

	/**
	 * @param unspscIdFilterBean the unspscIdFilterBean to set
	 */
	public void setUnspscIdFilterBean(SearchFilterBean unspscIdFilterBean) {
		this.unspscIdFilterBean = unspscIdFilterBean;
	}
	
	public List<CmRemark> getLastRemarkBeforeCompletion(CmRequestCode requestCode){
		log.info("Passed request : " +requestCode.getDocNo());
		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		reqDTO.setRequestCode(requestCode);
		CodificationResDTO resDTO = codificationSO.getLastRemarkBeforeCompletion(reqDTO);
		return resDTO.getRemarkList();
	}
	









}
