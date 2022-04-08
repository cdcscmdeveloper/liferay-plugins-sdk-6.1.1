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

package my.ep.web.backingbean.sc.dpcustomerservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.ISourcingIntegrationSO;
import my.ep.biz.service.sc.actor.IScActorSO;
import my.ep.biz.service.sc.directpurchase.IDpCustomerServiceSO;
import my.ep.biz.service.sc.directpurchase.IQuoteSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.DPStatusConstants;
import my.ep.constants.BpmConstants;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.SourcingIntegrationReqDTO;
import my.ep.dto.sc.SourcingIntegrationResDTO;
import my.ep.dto.sc.actor.ScActorReqDTO;
import my.ep.dto.sc.directpurchase.DpCustomerServiceReqDTO;
import my.ep.dto.sc.directpurchase.DpCustomerServiceResDTO;
import my.ep.dto.sc.directpurchase.QuoteReqDTO;
import my.ep.dto.sc.directpurchase.QuoteResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sc.common.ScActor;
import my.ep.persistence.entity.sc.directpurchase.ScQuote;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.type.aut.SearchOperator;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.directpurchase.DpCustomerServiceSearchBean;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.quatationtender.SourcingCustomerServiceBean;
import my.ep.web.managedbean.sc.dpcustomerservice.DPCustomerServiceManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * The Class DPCustomerServiceBackingBean.
 * 
 * @author Garikapati Mangaiah.
 */

@ManagedBean(name = "dpCustomerServiceBB")
@ViewScoped
public class DPCustomerServiceBackingBean extends
		SourcingBaseBackingBean<DPCustomerServiceManagedBean> implements
		Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger log = EpLogger
			.getLogger(DPCustomerServiceBackingBean.class);
	// private IPagingDataModel<Object[]> dpCustomerServiceSearchModel = new
	// DPCustomerServiceSearchDataModel();
	private static final String NAV_DP_CUSTOMER_SERVICE_VIEW = "/xhtml/sc/directpurchase/customerservice/dpCustomerServiceView.xhtml";
	private static final String NAV_DP_CUSTOMER_SERVICE_SEARCH_VIEW = "/xhtml/sc/directpurchase/customerservice/dpCustomerServiceMainView.xhtml";
	private static final String LOI_LOA_DOC_TYPE = "PR";

	/**
	 * Instantiates a new DP CustomerService backing bean.
	 */
	public DPCustomerServiceBackingBean() {

	}

	/**
	 * Method to search the DPCustomer Service data.
	 * 
	 * @throws Exception
	 */
	public void dpCustomerServiceSearchAction() throws Exception {
		try {
			DpCustomerServiceSearchBean dpCustomerService = getManagedBean()
					.getDpCustServiceSearchBean();
			PmOrgProfile orgProfileCode = dpCustomerService
					.getOrgProfilePtjCode();
			if (null == orgProfileCode
					|| StringUtils.isBlank(orgProfileCode.getOrgCode())
					|| StringUtils.isEmpty(orgProfileCode.getOrgCode())) {
				throw new EpException(MessageConst.ERROR_760443);
			}

			if (StringUtils.isBlank(dpCustomerService.getDocumentType())
					|| StringUtils.isEmpty(dpCustomerService.getDocumentType())) {
				throw new EpException(MessageConst.ERROR_760444);
			}
			this.getManagedBean().getDpCustomerServiceSearchModel()
					.reloadData();
		} finally {

		}
	}

	/**
	 * set user orgProfile data.
	 * 
	 */
	public void setorgProfileData() {
		try {
			DpCustomerServiceSearchBean dpCustomerService = getManagedBean()
					.getDpCustServiceSearchBean();
			PmOrgProfile orgProfileCode = dpCustomerService
					.getOrgProfilePtjCode();
			this.getManagedBean().getDpCustServiceSearchBean()
					.setOrgProfilePtjname(orgProfileCode);
		} finally {

		}

	}

	/**
	 * Method to Reset the DPCustomer Service data.
	 */
	public void dpCustomerServicereset() {
		try {
			getManagedBean().getDpCustServiceSearchBean().setOrgProfilePtjCode(
					new PmOrgProfile());
			getManagedBean().getDpCustServiceSearchBean().setOrgProfilePtjname(
					new PmOrgProfile());
			getManagedBean().getDpCustServiceSearchBean().setCreatedDateFrom(
					null);
			getManagedBean().getDpCustServiceSearchBean()
					.setCreatedDateTo(null);
			getManagedBean().getDpCustServiceSearchBean().setDocumentNo(" ");
			getManagedBean().getDpCustServiceSearchBean().setDocumentType(" ");
			getManagedBean().getDpCustServiceSearchBean().setDpStatus(" ");
			getManagedBean().getDpCustServiceSearchBean().setDpTitle(" ");
			this.getManagedBean().getDpCustomerServiceSearchModel().resetData();
		} finally {

		}
	}

	/**
	 * Search status desc.
	 * 
	 * @return the list
	 * @throws NamingException
	 *             the naming exception
	 */
	public SelectItem[] searchStatusDesc() throws NamingException {
		return super
				.getStatusDropdownValues(ModuleCodeConst.SUPPLIER_SELECTION
						.toUpperCase(), getManagedBean()
						.getDpCustServiceSearchBean().getDocumentType());
	}

	private class DPCustomerServiceSearchDataModel extends
			PagingDataModel<Object[]> {

		public DPCustomerServiceSearchDataModel(String componentId) {
			super(componentId, false);
		}

		@Override
		public ISearchResultBean<Object[]> pageChangeListener(
				ISearchBean<Object[]> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			try {
				DpCustomerServiceSearchBean dpCustomerservice = getManagedBean()
						.getDpCustServiceSearchBean();
				if (null != dpCustomerservice) {
					String documentType = dpCustomerservice.getDocumentType();
					String paramPtj = "";
					String paramPtjCode = "";
					String paramTitle = "";
					if (ParameterConst.DOCTYPE_REQUEST_NOTE
							.equals(documentType)) {
						paramPtj = "pmOrgProfile.orgProfileId";
						paramPtjCode = "pmOrgProfile.orgCode";
						paramTitle = "title";
						getManagedBean().setDpDcoumentDescription(
								ParameterConst.RN_TITLE);
					} else if (ParameterConst.DOCTYPE_SIMPLE_QUOTE
							.equals(documentType)) {
						paramPtj = "requestNotes.pmOrgProfile.orgProfileId";
						paramPtjCode = "requestNotes.pmOrgProfile.orgCode";
						paramTitle = "title";
						getManagedBean().setDpDcoumentDescription(
								ParameterConst.SQ_TITLE);
					} else if (ParameterConst.DOCTYPE_PURCHASE_INQUIRY
							.equals(documentType)) {
						paramPtj = "scRequestSupplierItems.requestNoteItem.requestNote.pmOrgProfile.orgProfileId";
						paramPtjCode = "scRequestSupplierItems.requestNoteItem.requestNote.pmOrgProfile.orgCode";
						paramTitle = "title";
						getManagedBean().setDpDcoumentDescription(
								ParameterConst.PI_TITLE);
					} else if (ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT
							.equals(documentType)) {
						paramPtj = "pmOrgProfile.orgProfileId";
						paramPtjCode = "pmOrgProfile.orgCode";
						paramTitle = "title";
						getManagedBean().setDpDcoumentDescription(
								ParameterConst.PD_TITLE);
					} else if (ParameterConst.DOCTYPE_LETTER_OF_AWARD
							.equals(documentType)) {
						paramPtj = "scLoiLoa.pmOrgProfile.orgProfileId";
						paramPtjCode = "scLoiLoa.pmOrgProfile.orgCode";
						paramTitle = "scLoiLoa.title";
						getManagedBean().setDpDcoumentDescription(
								ParameterConst.LA_TITLE);
					}
					PmOrgProfile orgProfileCode = dpCustomerservice
							.getOrgProfilePtjCode();
					PmOrgProfile orgProfileptjname = dpCustomerservice
							.getOrgProfilePtjname();
					/*
					 * if(StringUtils.isNotBlank(orgProfileCode.getOrgCode()) &&
					 * StringUtils.isNotEmpty(orgProfileCode.getOrgCode())){
					 * searchBean.getDataFilterBeans().add(new
					 * SearchFilterBean(paramPtjCode,
					 * SearchOperator.OPERATOR_EQUALS.value(),
					 * orgProfileCode.getOrgCode()));
					 * 
					 * }
					 */
					if (StringUtils.isNotBlank(orgProfileptjname.getOrgName())
							&& StringUtils.isNotEmpty(orgProfileptjname
									.getOrgName())) {
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean(paramPtj,
										SearchOperator.OPERATOR_EQUALS.value(),
										orgProfileptjname.getOrgProfileId()));
					}

					if (StringUtils.isNotBlank(dpCustomerservice.getDpTitle())
							&& StringUtils.isNotEmpty(dpCustomerservice
									.getDpTitle())) {
						SearchFilterBean filterBean = new SearchFilterBean(
								paramTitle,
								SearchOperator.OPERATOR_LIKE.value(),
								dpCustomerservice.getDpTitle(), true);
						searchBean.getDataFilterBeans().add(filterBean);
					}

					if (StringUtils.isNotBlank(documentType)
							&& StringUtils.isNotEmpty(documentType)) {
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean("docType",
										SearchOperator.OPERATOR_EQUALS.value(),
										documentType));
						if (documentType
								.equalsIgnoreCase(ParameterConst.DOCTYPE_LETTER_OF_AWARD)) {
							searchBean.getDataFilterBeans().add(
									new SearchFilterBean("MOD",
											SearchOperator.OPERATOR_EQUALS
													.value(),
											ModuleCodeConst.DIRECT_PURCHASE));
						}
					}

					if (StringUtils.isNotBlank(dpCustomerservice
							.getDocumentNo())
							&& StringUtils.isNotEmpty(dpCustomerservice
									.getDocumentNo())) {
						searchBean
								.getDataFilterBeans()
								.add(new SearchFilterBean("docNo",
										SearchOperator.OPERATOR_EQUALS.value(),
										dpCustomerservice.getDocumentNo(), true));

					}
					Date fromCreatedDate = dpCustomerservice
							.getCreatedDateFrom();
					Date toCreatedDate = dpCustomerservice.getCreatedDateTo();
					if (null != fromCreatedDate || toCreatedDate != null) {
						Calendar c1 = Calendar.getInstance();
						c1.setTime(fromCreatedDate);
						Calendar fromDate = Calendar.getInstance();
						fromDate.set(c1.get(Calendar.YEAR),
								c1.get(Calendar.MONTH),
								c1.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
						fromDate.set(Calendar.MILLISECOND, 0);
						fromCreatedDate = fromDate.getTime();
						if (toCreatedDate != null) {
							Calendar c2 = Calendar.getInstance();
							c2.setTime(toCreatedDate);
							Calendar toDate = Calendar.getInstance();
							toDate.set(c2.get(Calendar.YEAR),
									c2.get(Calendar.MONTH),
									c2.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
							toDate.set(Calendar.MILLISECOND, 0);
							toCreatedDate = toDate.getTime();
						}
						SearchFilterBean bean = new SearchFilterBean();
						bean.setSearchField("createdDate");
						bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN
								.value());
						SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
								fromCreatedDate, toCreatedDate);
						bean.setSearchValue(betweenVal);
						// searchFilterBean.add(bean);
						searchBean.getDataFilterBeans().add(bean);
					}
					if (StringUtils.isNotBlank(dpCustomerservice.getDpStatus())
							&& StringUtils.isNotEmpty(dpCustomerservice
									.getDpStatus())) {
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean("status",
										SearchOperator.OPERATOR_EQUALS.value(),
										Long.valueOf(dpCustomerservice
												.getDpStatus())));
					}

				}
				SourcingIntegrationReqDTO reqDTO = new SourcingIntegrationReqDTO();
				reqDTO.setSourcingDocSearchBean(searchBean);
				SourcingIntegrationResDTO resDTO = getSourcingIntegrationSO()
						.searchSourcingDocument(reqDTO);
				return resDTO.getSourcingDocSearchResultBean();

			} finally {

			}

		}

	}

	private ISourcingIntegrationSO getSourcingIntegrationSO() {
		return (ISourcingIntegrationSO) EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						ISourcingIntegrationSO.class);
	}

	/**
	 * View doc.
	 * 
	 * @param dpObj
	 *            the dp obj
	 */
	public void viewDoc(Object[] dpObj, String url) {
		List<String> docNumList = new ArrayList<String>();
		List<String> userIds = new ArrayList<String>();
		try {
			String documentDesc = getManagedBean().getDpDcoumentDescription();
			DpCustomerServiceSearchBean customer = this.getManagedBean()
					.getDpCustServiceSearchBean();
			IPagingDataModel<Object[]> customerService = getManagedBean()
					.getDpCustomerServiceSearchModel();
			renewSesionObject();
			getManagedBean().setDpCustomerServiceSearchModel(customerService);
			this.getManagedBean().setDpCustServiceSearchBean(customer);
			getManagedBean().setDpDcoumentDescription(documentDesc);
			log.info("viewDoc");
			log.info("url" + url);
			this.getManagedBean().setOfficerName(" ");

			long docId = (long) dpObj[0];
			String docNo = (String) dpObj[1];
			String docType = (String) dpObj[3];
			long statusId = (long) dpObj[4];
			String docTitle = (String) dpObj[5];
			this.getManagedBean().setViewOriginalUrl(url);
			String ministryName = retrieveMinistryByOrgProfileId(this
					.getManagedBean().getDpCustServiceSearchBean()
					.getOrgProfilePtjname().getOrgProfileId());
			String kampulanPTJ = retrieveJabatanByOrgProfileId(this
					.getManagedBean().getDpCustServiceSearchBean()
					.getOrgProfilePtjname().getOrgProfileId());
			this.getManagedBean().setMinistryName(ministryName);
			this.getManagedBean().setKampulanPtjName(kampulanPTJ);
			this.getManagedBean().setDocNumber(docNo);
			this.getManagedBean().setDocTitle(docTitle);
			this.getManagedBean().setStatusId(new Long(statusId));
			BpmRequestDTO reqDTO = new BpmRequestDTO();

			if (DPStatusConstants.SQ_COMPLETED.equals(statusId)
					|| DPStatusConstants.RN_APPROVAL.equals(statusId)) {
				if (ParameterConst.DOCTYPE_SIMPLE_QUOTE.equals(docType)) {
					QuoteReqDTO quoteReqDTO = new QuoteReqDTO();
					Long quoteId = Long.valueOf(docId);
					quoteReqDTO.setQuoteId(quoteId);
					quoteReqDTO.setFetchSubEntity(true);
					QuoteResDTO quoteResDTO = getQuoteSO().getQuoteDtl(
							(quoteReqDTO));
					ScQuote scQuote = quoteResDTO.getQuoteBidDtl();
					if (null != scQuote && null != scQuote.getRequestNotes()) {
						if (null != scQuote.getRequestNotes().get(0)
								.getRequestNoteId()) {
							docId = scQuote.getRequestNotes().get(0)
									.getRequestNoteId();
						}
					}
				}
				List<ScLoa> loaList = retriveLoadListDetails(docId, docType,
						docNo);
				if (CollectionUtils.isNotEmpty(loaList)) {
					for (ScLoa loa : loaList) {
						log.info("============backing bean dp loa number====="
								+ loa.getLoaNo());
						docNumList.add(loa.getLoaNo());
					}
				}
			}
			docNumList.add(docNo);

			String bpmUserLogin = EpPropertiesUtils.getInstance().getProperty(
					EpPropertiesConst.BPM_PRINCIPAL);

			Object token = getBpmSO().authenticate(bpmUserLogin);
			reqDTO.setUserId(bpmUserLogin);
			reqDTO.setDocNumberList(docNumList);
			reqDTO.setCtxToken(token);
			reqDTO.setState(BpmConstants.STATE_ASSIGNED);
			BpmResponseDTO resDTO = getBpmSO().getTasksByDocNumberList(reqDTO);
			if (null != resDTO && null != resDTO.getWorklistVO()
					&& null != resDTO.getWorklistVO().getWorklistItem()
					&& resDTO.getWorklistVO().getWorklistItem().size() > 0) {
				this.getManagedBean().setCustomerServiceEntity(
						new ArrayList<SourcingCustomerServiceBean>());
				for (WorklistItemVO worklistItem : resDTO.getWorklistVO()
						.getWorklistItem()) {

					if (CollectionUtils.isNotEmpty(worklistItem.getAssignees())) {
						// custServiceEntity.setOfficerName(worklistItem.getAssignees().get(0));
						for (int i = 0; i < worklistItem.getAssignees().size(); i++) {
							SourcingCustomerServiceBean custServiceEntity = new SourcingCustomerServiceBean();
							IUserSO userSO = (IUserSO) BusinessDelegate
									.getInstance().getServiceObject(
											ModuleCodeConst.PROFILE_MANAGEMENT,
											IUserSO.class);
							UserReqDTO reqUserDTO = new UserReqDTO();
							reqUserDTO.setLoginId(worklistItem.getAssignees()
									.get(i));
							UserResDTO userResDTO = userSO
									.retrieveUserByLoginId(reqUserDTO);
							if (null != userResDTO
									&& null != userResDTO.getPmUser()) {
								log.info("====user name==="
										+ userResDTO.getPmUser().getUserName());
								custServiceEntity.setOfficerName(userResDTO
										.getPmUser().getUserName());
							}
							reqDTO = new BpmRequestDTO();
							reqDTO.setCtxToken(token);
							reqDTO.setTaskId(worklistItem.getTaskId());
							PmStatusDesc statusDesc = getPmStatusDesc(worklistItem
									.getDocStatus());
							log.info("display doc numner"
									+ worklistItem.getDocNumber());
							custServiceEntity.setDocumentNum(worklistItem
									.getDocNumber());
							if (null != statusDesc) {
								custServiceEntity.setStatusDesc(statusDesc
										.getStatusName());
								custServiceEntity
										.setDocumentType(getParameterDesc(
												ParameterTypeConst.DOCUMENT_TYPES,
												statusDesc.getPmStatus()
														.getDocType()));
								custServiceEntity.setStatus(statusDesc
										.getPmStatus().getStatusId());
							}
							getManagedBean().getCustomerServiceEntity().add(
									custServiceEntity);

						}
					}
					/*
					 * this.getManagedBean().setOfficerName(userIds.toString());
					 * if
					 * (StringUtils.isNotBlank(this.getManagedBean().getOfficerName
					 * ()) &&
					 * StringUtils.isNotEmpty(this.getManagedBean().getOfficerName
					 * ())){ String replaceString=
					 * this.getManagedBean().getOfficerName().substring(1,
					 * this.getManagedBean
					 * ().getOfficerName().length()-1).trim();
					 * this.getManagedBean().setOfficerName(replaceString); }
					 */
					log.info("========staus====" + worklistItem.getState());
				}
			}
			if (DPStatusConstants.SQ_PENDING_SUPPLIER_RESPONSE.equals(statusId)) {
				if (null == getManagedBean().getCustomerServiceEntity()) {
					getManagedBean().setCustomerServiceEntity(
							new ArrayList<SourcingCustomerServiceBean>());
				}
				SourcingCustomerServiceBean custServiceEntity = new SourcingCustomerServiceBean();
				custServiceEntity.setStatus(statusId);
				getManagedBean().getCustomerServiceEntity().add(
						custServiceEntity);
			}

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, NAV_DP_CUSTOMER_SERVICE_VIEW);
		} finally {
			docNumList = null;
			userIds = null;
		}
	}

	/**
	 * Sets the pm status desc.
	 * 
	 * @param statusId
	 *            - pm status id passed as String
	 */
	private PmStatusDesc getPmStatusDesc(Integer docStatus) {

		StatusReqDTO reqDTO = new StatusReqDTO();
		reqDTO.setStatusId(new Long(docStatus));
		reqDTO.setLanguageCode(new FacesScopes().getUserLocale().getLanguage());
		IStatusSO reqSO = (IStatusSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStatusSO.class);
		StatusResDTO resDTO = reqSO.retrieveStatusDescById(reqDTO);
		if (resDTO != null && resDTO.getPmStatusDesc() != null) {
			return resDTO.getPmStatusDesc();
		}
		return null;
	}

	/**
	 * Gets the quote so.
	 * 
	 * @return the quote so
	 */
	private IQuoteSO getQuoteSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQuoteSO.class);
	}

	private List<ScLoa> retriveLoadListDetails(Long docId, String docType,
			String docNum) {
		ISearchBean<ScLoa> searchBean = new SearchBean<ScLoa>();
		IDpCustomerServiceSO dpCustomerServiceSO = null;
		try {
			dpCustomerServiceSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IDpCustomerServiceSO.class);
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("scLoiLoa.docType",
							SearchOperator.OPERATOR_EQUALS.value(),
							LOI_LOA_DOC_TYPE));
			DpCustomerServiceReqDTO customerServiceReqDto = new DpCustomerServiceReqDTO();
			DpCustomerServiceResDTO resDTO = new DpCustomerServiceResDTO();
			customerServiceReqDto.setSearchBean(searchBean);
			customerServiceReqDto.setDocId(docId);
			customerServiceReqDto.setDocType(docType);
			customerServiceReqDto.setDocNum(docNum);
			resDTO = dpCustomerServiceSO
					.retriveLoaDetailsByQuotes(customerServiceReqDto);
			return resDTO.getCustomerSearchhResultList();
		} finally {
			dpCustomerServiceSO = null;
		}
	}

	/**
	 * Gets the user so.
	 * 
	 * @return the user so
	 */
	private IBpmSO getBpmSO() {
		return (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(
				IBpmSO.class);
	}

	/**
	 * Retrieve ScActor by qtId and roleCode
	 * 
	 * @param qtId
	 *            the QT id
	 * @param roleCode
	 *            the roleCode
	 * @return the scActor.
	 */
	private ScActor getActorByQtIdAndRoleCode(Long docId, String roleCode,
			String docType) {
		IScActorSO actorSO = null;
		try {
			actorSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IScActorSO.class);

			ScActorReqDTO reqDTO = new ScActorReqDTO();
			reqDTO.setDocType(docType);
			reqDTO.setDocId(docId);
			reqDTO.setRoleCode(roleCode);

			ScActor scActor = actorSO.retrieveActorByTypeIdAndRoleCode(reqDTO)
					.getScActor();

			if (scActor == null) {
				scActor = new ScActor();
			}
			return scActor;
		} finally {
			actorSO = null;
		}
	}

	/**
	 * navigate to original page
	 * 
	 * @return navigation to original page.
	 */
	public String navigateToCurrentPage() {
		// QTDetailBackingBean qtBackingBean = new QTDetailBackingBean();
		return getManagedBean().getViewOriginalUrl();
	}

	/**
	 * Gets the doc type qt loa dropdown values.
	 * 
	 * @return the doc type qt loa dropdown values
	 */
	public SelectItem[] getDocTypeDPCustomerServiceDropdownValues() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> dpCustomerServiceDoctypes = new ArrayList<String>();
		dpCustomerServiceDoctypes.add(ParameterConst.DOCTYPE_SIMPLE_QUOTE);
		dpCustomerServiceDoctypes.add(ParameterConst.DOCTYPE_REQUEST_NOTE);
		dpCustomerServiceDoctypes.add(ParameterConst.DOCTYPE_PURCHASE_INQUIRY);
		dpCustomerServiceDoctypes
				.add(ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT);
		dpCustomerServiceDoctypes.add(ParameterConst.DOCTYPE_LETTER_OF_AWARD);

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_IN.value(),
						dpCustomerServiceDoctypes));
		SelectItem[] items = getDropdownCodeValuesBySearchBean(
				ParameterTypeConst.DOCUMENT_TYPES, searchBean);
		return items;
	}

	/**
	 * navigate to CustomerService search page.
	 * 
	 * 
	 */
	public void preRenderView() {
		log.info("DP customer service preRenderView");
		renewSesionObject();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null, NAV_DP_CUSTOMER_SERVICE_SEARCH_VIEW);
	}

	/**
	 * @return the dpCustomerServiceSearchModel
	 */
	public IPagingDataModel<Object[]> getDpCustomerServiceSearchModel() {
		IPagingDataModel<Object[]> customerService = getManagedBean()
				.getDpCustomerServiceSearchModel();
		if (null == customerService) {
			customerService = new DPCustomerServiceSearchDataModel(
					"dpCustomerServiceSearchResult");
			getManagedBean().setDpCustomerServiceSearchModel(customerService);
		}
		return customerService;
	}

}
