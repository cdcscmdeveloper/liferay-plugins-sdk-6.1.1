package my.ep.web.managedbean.trackingdiary;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.authorization.usergroup.IUserGroupSO;
import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.biz.service.pm.common.certbody.ICertBodySO;
import my.ep.biz.service.pm.common.document.IDocumentSO;
import my.ep.biz.service.pm.common.educationfield.IEducationFieldSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.pm.common.uom.IUomSO;
import my.ep.biz.service.trackingdiary.ITrackingDiarySO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.authorization.usergroup.UserGroupReqDTO;
import my.ep.dto.pm.authorization.usergroup.UserGroupResDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeReqDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;
import my.ep.dto.pm.common.certbody.CertBodyReqDTO;
import my.ep.dto.pm.common.certbody.CertBodyResDTO;
import my.ep.dto.pm.common.document.DocumentReqDTO;
import my.ep.dto.pm.common.document.DocumentResDTO;
import my.ep.dto.pm.common.educationfield.EducationFieldReqDTO;
import my.ep.dto.pm.common.educationfield.EducationFieldResDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgReqDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgResDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryReqDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryResDTO;
import my.ep.dto.pm.common.uom.UomReqDTO;
import my.ep.dto.pm.common.uom.UomResDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmCertBody;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmEduField;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmTrackingDiary;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.type.aut.SearchOperator;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.OrganisationSearchBean;
import my.ep.web.bean.pm.SupportDocumentSearchBean;
import my.ep.web.bean.pm.UnitOfMeasurmentSearchBean;
import my.ep.web.bean.pm.UserGroupSearchBean;
import my.ep.web.bean.pm.UserProfilesSearchBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.profcertbody.ProfCertifiedBodySearchBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@ManagedBean(name = "pmTrackingDiaryManagedBean")
@SessionScoped
public class PmTrackingDiaryManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger
			.getLogger(PmTrackingDiaryManagedBean.class);
	private String functionType;
	private String transactionType;
	private Date fromDate;
	private Date toDate;
	// Paging for listing
	private IPagingDataModel<PmUser> userDataModel = new UserPagingDataModel();
	private IPagingDataModel<PmOrgProfile> userOrgDataModel = new UserOrgPagingDataModel();
	private IPagingDataModel<PmUom> uomDataModel = new UOMPagingDataModel();
	private IPagingDataModel<PmEduField> eduDataModel = new EduPagingDataModel();
	private IPagingDataModel<PmCertBody> pcbDataModel = new PcbPagingDataModel();
	private IPagingDataModel<PmDocument> pmDocumentDataModel = new PmDocumentDataModel();
	private IPagingDataModel<PmFinancialOrg> financialOrgDataModel = new FinancialOrgPagingDataModel();
	private IPagingDataModel<PmUserGroup> userGroupDataModel = new USGPagingDataModel();

	/** The search category. */
	private IPagingDataModel<Object[]> categoryDataModel = new SearchCategoryPagingDataModel();
	
	private SupportDocumentSearchBean supportDocSearchBean;
	private OrganisationSearchBean organisationSearchBean;
	private UserProfilesSearchBean userProfileSearchBean;
	private UnitOfMeasurmentSearchBean uomSearchBean;
	private ProfCertifiedBodySearchBean pcbSearchBean;
	private UserGroupSearchBean userGroupSearchBean;
	private String eduFieldName;
	private String financialOrgName;
	private boolean orgProfileRequired;
	// private PmOrgProfile selectedOrgProfile;
	// private PmUser selectedUser;
	private boolean multiRuleType = false;
	private String selectedDocType;
	private String selectedDocType2;
	private Long selectedDocId;
	private List<PmZone> zoneList;
	private String backURL;
	
	public String getBackURL() {
		return backURL;
	}

	public void setBackURL(String backURL) {
		this.backURL = backURL;
	}

	/** The category code. */
	private String categoryCode;
	
	/** The category desc. */
	private String categoryDesc;
	
	/** The pm category level. */
	private Integer pmCategoryLevel;

	public String getTransactionType() {
		return transactionType;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public IPagingDataModel<Object[]> getCategoryDataModel() {
		return categoryDataModel;
	}

	public void setCategoryDataModel(IPagingDataModel<Object[]> categoryDataModel) {
		this.categoryDataModel = categoryDataModel;
	}

	public String getCategoryDesc() {
		return categoryDesc;
	}

	public Integer getPmCategoryLevel() {
		return pmCategoryLevel;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public void setCategoryDesc(String categoryDesc) {
		this.categoryDesc = categoryDesc;
	}

	public void setPmCategoryLevel(Integer pmCategoryLevel) {
		this.pmCategoryLevel = pmCategoryLevel;
	}

	public IPagingDataModel<PmUserGroup> getUserGroupDataModel() {
		return userGroupDataModel;
	}

	public UserGroupSearchBean getUserGroupSearchBean() {
		
		if(userGroupSearchBean == null){
			userGroupSearchBean = new UserGroupSearchBean();
		}
		return userGroupSearchBean;
	}

	public void setUserGroupDataModel(
			IPagingDataModel<PmUserGroup> userGroupDataModel) {
		this.userGroupDataModel = userGroupDataModel;
	}

	public void setUserGroupSearchBean(UserGroupSearchBean userGroupSearchBean) {
		this.userGroupSearchBean = userGroupSearchBean;
	}

	public IPagingDataModel<PmFinancialOrg> getFinancialOrgDataModel() {
		return financialOrgDataModel;
	}

	public void setFinancialOrgDataModel(
			IPagingDataModel<PmFinancialOrg> financialOrgDataModel) {
		this.financialOrgDataModel = financialOrgDataModel;
	}

	public String getFinancialOrgName() {
		return financialOrgName;
	}

	public void setFinancialOrgName(String financialOrgName) {
		this.financialOrgName = financialOrgName;
	}

	public IPagingDataModel<PmCertBody> getPcbDataModel() {
		return pcbDataModel;
	}

	public ProfCertifiedBodySearchBean getPcbSearchBean() {
		return pcbSearchBean;
	}

	public void setPcbDataModel(IPagingDataModel<PmCertBody> pcbDataModel) {
		this.pcbDataModel = pcbDataModel;
	}

	public void setPcbSearchBean(ProfCertifiedBodySearchBean pcbSearchBean) {
		this.pcbSearchBean = pcbSearchBean;
	}

	public String getEduFieldName() {
		return eduFieldName;
	}

	public void setEduFieldName(String eduFieldName) {
		this.eduFieldName = eduFieldName;
	}

	public IPagingDataModel<PmEduField> getEduDataModel() {
		return eduDataModel;
	}

	public void setEduDataModel(IPagingDataModel<PmEduField> eduDataModel) {
		this.eduDataModel = eduDataModel;
	}

	public String getSelectedDocType() {
		return selectedDocType;
	}

	public void setSelectedDocType(String selectedDocType) {
		this.selectedDocType = selectedDocType;
	}

	public IPagingDataModel<PmDocument> getPmDocumentDataModel() {
		return pmDocumentDataModel;
	}

	public SupportDocumentSearchBean getSupportDocSearchBean() {
		if (supportDocSearchBean == null) {
			supportDocSearchBean = new SupportDocumentSearchBean();
		}
		return supportDocSearchBean;
	}

	public void setPmDocumentDataModel(
			IPagingDataModel<PmDocument> pmDocumentDataModel) {
		this.pmDocumentDataModel = pmDocumentDataModel;
	}

	public void setSupportDocSearchBean(
			SupportDocumentSearchBean supportDocSearchBean) {
		this.supportDocSearchBean = supportDocSearchBean;
	}

	public List<PmZone> getZoneList() {
		if (zoneList == null) {
			this.zoneList = new ArrayList<PmZone>();
		}
		return zoneList;
	}

	public void setZoneList(List<PmZone> zoneList) {
		this.zoneList = zoneList;
	}

	public String getSelectedDocType2() {
		return selectedDocType2;
	}

	public void setSelectedDocType2(String selectedDocType2) {
		this.selectedDocType2 = selectedDocType2;
	}

	public boolean isMultiRuleType() {
		return multiRuleType;
	}

	public void setMultiRuleType(boolean multiRuleType) {
		this.multiRuleType = multiRuleType;
	}

	public Long getSelectedDocId() {
		return selectedDocId;
	}

	public void setSelectedDocId(Long selectedDocId) {
		this.selectedDocId = selectedDocId;
	}

	public IPagingDataModel<PmTrackingDiary> getSearchTrackingDiaryDataModel() {
		return searchTrackingDiaryDataModel;
	}

	public void setSearchTrackingDiaryDataModel(
			IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel) {
		this.searchTrackingDiaryDataModel = searchTrackingDiaryDataModel;
	}

	private IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel = new SearchTrackingDiaryPagingDataModel();

	public String getFunctionType() {
		return functionType;
	}

	public void setFunctionType(String functionType) {
		this.functionType = functionType;
	}

	public OrganisationSearchBean getOrganisationSearchBean() {
		if (organisationSearchBean == null) {
			organisationSearchBean = new OrganisationSearchBean();
		}
		return organisationSearchBean;
	}

	public void setOrganisationSearchBean(
			OrganisationSearchBean organisationSearchBean) {
		
		this.organisationSearchBean = organisationSearchBean;
	}

	public UserProfilesSearchBean getUserProfileSearchBean() {
		return userProfileSearchBean;
	}

	public void setUserProfileSearchBean(
			UserProfilesSearchBean userProfileSearchBean) {
		this.userProfileSearchBean = userProfileSearchBean;
	}

	public IPagingDataModel<PmUser> getUserDataModel() {
		return userDataModel;
	}

	public void setUserDataModel(IPagingDataModel<PmUser> userDataModel) {
		this.userDataModel = userDataModel;
	}

	public IPagingDataModel<PmOrgProfile> getUserOrgDataModel() {
		return userOrgDataModel;
	}

	public void setUserOrgDataModel(
			IPagingDataModel<PmOrgProfile> userOrgDataModel) {
		this.userOrgDataModel = userOrgDataModel;
	}

	public boolean isOrgProfileRequired() {
		return orgProfileRequired;
	}

	public void setOrgProfileRequired(boolean orgProfileRequired) {
		this.orgProfileRequired = orgProfileRequired;
	}

	public UnitOfMeasurmentSearchBean getUomSearchBean() {
		if (uomSearchBean == null) {
			uomSearchBean = new UnitOfMeasurmentSearchBean();
		}
		return uomSearchBean;
	}

	public void setUomSearchBean(UnitOfMeasurmentSearchBean uomSearchBean) {
		this.uomSearchBean = uomSearchBean;
	}

	public IPagingDataModel<PmUom> getUomDataModel() {
		return uomDataModel;
	}

	public void setUomDataModel(IPagingDataModel<PmUom> uomDataModel) {
		this.uomDataModel = uomDataModel;
	}

	/**
	 * reset result bean fields
	 */
	public void resetOrgResultBean() {
		this.setUserOrgDataModel(new UserOrgPagingDataModel());
	}

	/**
	 * reset result bean fields
	 */
	public void resetUserResultBean() {
		this.setUserDataModel(new UserPagingDataModel());
	}

	/**
	 * reset result bean fields
	 */
	public void resetUOMResultBean() {
		this.setUomDataModel(new UOMPagingDataModel());
	}
	
	/**
	 * reset result bean fields
	 */
	public void resetUSGResultBean() {
		this.setUserGroupDataModel(new USGPagingDataModel());
	}

	/**
	 * reset result bean fields
	 */
	public void resetEduResultBean() {
		this.setEduDataModel(new EduPagingDataModel());
	}

	/**
	 * reset result bean fields
	 */
	public void resetPcbResultBean() {
		this.setPcbDataModel(new PcbPagingDataModel());
	}

	/**
	 * reset result bean fields
	 */
	public void resetDocResultBean() {
		this.setPmDocumentDataModel(new PmDocumentDataModel());
	}
	
	/**
	 * reset result bean fields
	 */
	public void resetCatResultBean() {
		this.setCategoryDataModel(new SearchCategoryPagingDataModel());
	}
	
	/**
	 * reset result bean fields
	 */
	public void resetFOrgResultBean() {
		this.setFinancialOrgDataModel(new FinancialOrgPagingDataModel());
	}
	
	// pagination for user profile search list
	private class UserPagingDataModel extends PagingDataModel<PmUser> {
		private static final long serialVersionUID = 1L;

		public UserPagingDataModel() {
			super("dtUserProfileResult");
		}

		@Override
		protected ISearchResultBean<PmUser> pageChangeListener(
				ISearchBean<PmUser> searchBean) {
			log.info("pageChangeListener");
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			boolean isSearchBeanEmpty = true;
			IUserSO userSO;
			try {

				if (getUserProfileSearchBean().getUserName() != null
						&& !getUserProfileSearchBean().getUserName().equals("")) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("userName",
									SearchOperator.OPERATOR_LIKE.value(),
									getUserProfileSearchBean().getUserName(),
									true));
					isSearchBeanEmpty = false;
				}
				if (getUserProfileSearchBean().getIdentificationNo() != null
						&& !getUserProfileSearchBean().getIdentificationNo()
								.equals("")) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("identificationNo",
									SearchOperator.OPERATOR_LIKE.value(),
									getUserProfileSearchBean()
											.getIdentificationNo(), true));
					isSearchBeanEmpty = false;
				}
				if (!isSearchBeanEmpty) {
					userSO = BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

					UserReqDTO userReqDTO = new UserReqDTO();

					userReqDTO.setUserSearchBean(searchBean);
					UserResDTO resDto = userSO.searchUserProfile(userReqDTO);

					return resDto.getUserResultBean();
				} else {
					return new SearchResultBean();
				}
			} finally {
				userSO = null;
			}
		}
	}
	/**
	 * The Class SearchCategoryPagingDataModel.
	 */
	private class SearchCategoryPagingDataModel extends
			PagingDataModel<Object[]> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new search category paging data model.
		 */
		public SearchCategoryPagingDataModel() {
			super("dtCatCodeResult");

		}

		public ISearchResultBean<Object[]> pageChangeListener(
				ISearchBean<Object[]> searchBean) {
			ICategoryCodeSO categoryCodeSO;
			try {

				categoryCodeSO = (ICategoryCodeSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								ICategoryCodeSO.class);
				CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
				if (getPmCategoryLevel() != null
						&& getPmCategoryLevel().intValue() > 0) {
					
					reqDTO.setPmCategoryLevel(getPmCategoryLevel());
					
				if (getCategoryCode() != null && !getCategoryCode().isEmpty()) {
					switch(getPmCategoryLevel()){
					case 1 : { reqDTO.setMainCatCode(getCategoryCode()); break;}
					case 2 : {reqDTO.setSubCatCode(getCategoryCode()); break;}
					case 3 : {reqDTO.setSubDivCode(getCategoryCode()); break;}
					}
				}
				if (getCategoryDesc() != null && !getCategoryDesc().isEmpty()) {
					switch(getPmCategoryLevel()){
					case 1 : {reqDTO.setMainCatDesc(getCategoryDesc()); break;}
					case 2 : {reqDTO.setSubCatDesc(getCategoryDesc()); break;}
					case 3 : {reqDTO.setSubDivDesc(getCategoryDesc()); break;}
					}
				}

					reqDTO.setSearchBeanObject(searchBean);
					CategoryCodeResDTO resDTO = categoryCodeSO
							.searchAllCategory(reqDTO);

					if (resDTO.isSuccessful()) {
			
						return resDTO.getCategoryResultBean();
					}
				}else {
					return new SearchResultBean();
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				categoryCodeSO = null;
			}

			return null;

		}
	}
	// /////////////////////////////////////////////////
	// pagination for user org search list
	private class UserOrgPagingDataModel extends PagingDataModel<PmOrgProfile> {
		private static final long serialVersionUID = 1L;

		public UserOrgPagingDataModel() {
			super("dtOrgProfileResult");
		}

		@Override
		protected ISearchResultBean<PmOrgProfile> pageChangeListener(
				ISearchBean<PmOrgProfile> searchBean) {
			log.info("pageChangeListener");
			try {
				if (getOrganisationSearchBean().getOrgProfile() != null)
//					log.info("......getOrganisationSearchBean().getOrgProfile().getOrgProfileId()="
//							+ getOrganisationSearchBean().getOrgProfile()
//									.getOrgProfileId());
				
				if (getOrganisationSearchBean().getOrgType() != null
						&& getOrganisationSearchBean().getOrgName() != null) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("orgTypeCode.parameterId",
									SearchOperator.OPERATOR_EQUALS.value(),
									getOrganisationSearchBean().getOrgType()
									.getParameterId()));
			
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean("pmOrgValidity.orgName",
										SearchOperator.OPERATOR_LIKE.value(),
										getOrganisationSearchBean().getOrgName()));
						
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean("recordStatus",
										SearchOperator.OPERATOR_EQUALS.value(),1));
						
					OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
					reqDTO.setOrgProfileSearchBean(searchBean);
					IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
							.getServiceObject(
									ModuleCodeConst.PROFILE_MANAGEMENT,
									IOrgProfileSO.class);
					OrgProfileResDTO resDto = orgProfileSO
							.searchOrgProfile(reqDTO);
					// log.info("......resDto.getPmOrgProfile()="+resDto.getPmOrgProfile());
					if (resDto != null && resDto.isSuccessful()) {
						// SearchResultBean<PmOrgProfile> searchResultBean = new
						// SearchResultBean<PmOrgProfile>();
						// List<PmOrgProfile> resultList= new
						// ArrayList<PmOrgProfile>();
						// resultList.add(resDto.getPmOrgProfile());
						// searchResultBean.setTotalRecord(1);
						// searchResultBean.setResultList(resultList);
						return resDto.getOrgProfileResultBean();
					}
				} else {
					return new SearchResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

	}
	
	// pagination for UserGroup search list
		private class USGPagingDataModel extends PagingDataModel<PmUserGroup> {
			private static final long serialVersionUID = 1L;

			public USGPagingDataModel() {
				super("dtUSGResult");
			}

			@Override
			protected ISearchResultBean<PmUserGroup> pageChangeListener(
					ISearchBean<PmUserGroup> searchBean) {
				log.info("pageChangeListener");
				if (log.isDebugEnabled()) {
					log.debug("pageChangeListener");
				}
				IUserGroupSO groupSO;
				try {
				if (getUserGroupSearchBean().getPtjName() != null
							&& !"".equalsIgnoreCase(getUserGroupSearchBean().getPtjName())) {
					SearchFilterBean codesSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean
							.setSearchField("pmOrgProfile.pmOrgValidity.orgName");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getUserGroupSearchBean().getPtjName());
					searchBean.getDataFilterBeans().add(codesSearchBean);
					
					if (getUserGroupSearchBean().getGroupCode() != null
							&& !"".equalsIgnoreCase(getUserGroupSearchBean().getGroupCode())) {
						codesSearchBean = new SearchFilterBean(
								SearchOperator.OPERATOR_LIKE.value());
						codesSearchBean.setSearchField("groupCode");
						codesSearchBean.setSearchValue(getUserGroupSearchBean().getGroupCode());
						codesSearchBean.setCaseInsensitive(true);
						searchBean.getDataFilterBeans().add(codesSearchBean);
					}

					if (getUserGroupSearchBean().getGroupName() != null
							&& !"".equalsIgnoreCase(getUserGroupSearchBean().getGroupName())) {
						codesSearchBean = new SearchFilterBean(
								SearchOperator.OPERATOR_LIKE.value());
						codesSearchBean.setSearchField("groupName");
						codesSearchBean.setSearchValue(getUserGroupSearchBean().getGroupName());
						codesSearchBean.setCaseInsensitive(true);
						searchBean.getDataFilterBeans().add(codesSearchBean);
					}


						UserGroupReqDTO reqDTO = new UserGroupReqDTO();
						reqDTO.setUserGroupSearchBean(searchBean);
						 groupSO = EpEjbBusinessDelegate.getInstance()
								.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
										IUserGroupSO.class);
						UserGroupResDTO resDTO = groupSO.retrieveUserGroups(reqDTO);
						ISearchResultBean<PmUserGroup> iSearchResultBean = resDTO
								.getUserGroupResultBean();
						return iSearchResultBean;

					} else {
						return new SearchResultBean();
					}
				} finally {
					groupSO = null;
				}
			}
		}
	// pagination for UOM search list
	private class UOMPagingDataModel extends PagingDataModel<PmUom> {
		private static final long serialVersionUID = 1L;

		public UOMPagingDataModel() {
			super("dtUOMResult");
		}

		@Override
		protected ISearchResultBean<PmUom> pageChangeListener(
				ISearchBean<PmUom> searchBean) {
			log.info("pageChangeListener");
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			boolean isSearchBeanEmpty = true;
			IUomSO uomSO;
			try {

				if (getUomSearchBean().getUOMCode() != null
						&& !getUomSearchBean().getUOMCode().equals("")) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("uomCode",
									SearchOperator.OPERATOR_LIKE.value(),
									getUomSearchBean().getUOMCode(), true));
					isSearchBeanEmpty = false;
				}
				if (getUomSearchBean().getUOMName() != null
						&& !getUomSearchBean().getUOMName().equals("")) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("uomName",
									SearchOperator.OPERATOR_LIKE.value(),
									getUomSearchBean().getUOMName(), true));
					isSearchBeanEmpty = false;
				}
				if (!isSearchBeanEmpty) {
					uomSO = BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT, IUomSO.class);

					UomReqDTO uomReqDTO = new UomReqDTO();

					uomReqDTO.setMeasurementSearchBean(searchBean);
					UomResDTO resDto = uomSO
							.retrieveMeasurementBySearch(uomReqDTO);

					return resDto.getMeasurementSearchResultBean();
				} else {
					return new SearchResultBean();
				}
			} finally {
				uomSO = null;
			}
		}
	}

	// pagination for Education fields search list
	private class EduPagingDataModel extends PagingDataModel<PmEduField> {
		private static final long serialVersionUID = 1L;

		public EduPagingDataModel() {
			super("dtEduResult");
		}

		@Override
		protected ISearchResultBean<PmEduField> pageChangeListener(
				ISearchBean<PmEduField> searchBean) {
			log.info("pageChangeListener");
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			boolean isSearchBeanEmpty = true;
			IEducationFieldSO eduSO;
			try {

				if (getEduFieldName() != null && !getEduFieldName().equals("")) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("fieldName",
									SearchOperator.OPERATOR_LIKE.value(),
									getEduFieldName(), true));
					isSearchBeanEmpty = false;
				}
				if (!isSearchBeanEmpty) {
					eduSO = BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT,
							IEducationFieldSO.class);

					EducationFieldReqDTO eduReqDTO = new EducationFieldReqDTO();

					eduReqDTO.setEducationSearchBean(searchBean);
					EducationFieldResDTO resDto = eduSO
							.retrieveEducationBySearch(eduReqDTO);

					return resDto.getEducationSearchResultBean();
				} else {
					return new SearchResultBean();
				}
			} finally {
				eduSO = null;
			}
		}
	}

	// pagination for Professional/Certified Body search list
	private class PcbPagingDataModel extends PagingDataModel<PmCertBody> {
		private static final long serialVersionUID = 1L;

		public PcbPagingDataModel() {
			super("dtPcbResult");
		}

		@Override
		protected ISearchResultBean<PmCertBody> pageChangeListener(
				ISearchBean<PmCertBody> searchBean) {
			log.info("pageChangeListener");
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			boolean isSearchBeanEmpty = true;
			ICertBodySO pcbSO;
			try {

				if (getPcbSearchBean().getBodyName() != null
						&& !getPcbSearchBean().getBodyName().equals("")) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("bodyName",
									SearchOperator.OPERATOR_LIKE.value(),
									getPcbSearchBean().getBodyName(), true));
					isSearchBeanEmpty = false;
				}
				if (!isSearchBeanEmpty) {
					pcbSO = BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT,
							ICertBodySO.class);

					CertBodyReqDTO pcbReqDTO = new CertBodyReqDTO();

					pcbReqDTO.setCertBodySearchBean(searchBean);
					CertBodyResDTO resDto = pcbSO.retrieveBodyList(pcbReqDTO);

					return resDto.getCertBodyResultBean();
				} else {
					return new SearchResultBean();
				}
			} finally {
				pcbSO = null;
			}
		}
	}

	private class PmDocumentDataModel extends PagingDataModel<PmDocument> {
		private static final long serialVersionUID = 1L;

		public PmDocumentDataModel() {
			super("dtSupportDocResult");
		}

		@Override
		public ISearchResultBean<PmDocument> pageChangeListener(
				ISearchBean<PmDocument> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}

			try {
				// ISearchBean<PmDocument> searchBeanData =
				// getSearchBeanData(searchBean);
				DocumentReqDTO reqDTO = new DocumentReqDTO();
				reqDTO.setDocumentSearchBean(searchBean);
				reqDTO.setDocCategoryCode(getSupportDocSearchBean()
						.getDocCategoryParamCode());
				reqDTO.setDocName(getSupportDocSearchBean().getDocName());
				reqDTO.setZoneId(getSupportDocSearchBean().getPmZone());
				IDocumentSO reqSO = (IDocumentSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								IDocumentSO.class);
				DocumentResDTO resDTO = reqSO.retrieveDocumentList(reqDTO);
				if (resDTO.isSuccessful()) {
					return resDTO.getDocumentResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}
	}

	private class FinancialOrgPagingDataModel extends
			PagingDataModel<PmFinancialOrg> {

		private static final long serialVersionUID = 1L;

		public FinancialOrgPagingDataModel() {

			super("dtFOrgResult");
		}

		public ISearchResultBean<PmFinancialOrg> pageChangeListener(
				ISearchBean<PmFinancialOrg> searchBean) {
			
			boolean isSearchBeanEmpty = true;
			IFinancialOrgSO financialOrgSO;
			try {

				FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
				if (getFinancialOrgName() != null
						&& getFinancialOrgName().trim().length() > 0) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("finOrgName",
									SearchOperator.OPERATOR_LIKE.value(),
									getFinancialOrgName(), true));
					isSearchBeanEmpty = false;
				}
				if (!isSearchBeanEmpty) {
				reqDTO.setFinancialSearchBean(searchBean);
				financialOrgSO = (IFinancialOrgSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								IFinancialOrgSO.class);
				FinancialOrgResDTO resDto = financialOrgSO
						.searchFinancialOrgData(reqDTO);

				if (resDto.isSuccessful()) {
					return resDto.getFinaceialOrgResultBean();
				}
			} else {
				return new SearchResultBean();
			}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

	}

	private class SearchTrackingDiaryPagingDataModel extends
			PagingDataModel<PmTrackingDiary> {

		private static final long serialVersionUID = 1L;

		public SearchTrackingDiaryPagingDataModel() {
			super("trackingDiaryTableID");
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ISearchResultBean<PmTrackingDiary> pageChangeListener(
				ISearchBean<PmTrackingDiary> searchBean) {
			boolean isSearchBeanEmpty = true;
			TrackingDiaryReqDTO reqDTO = new TrackingDiaryReqDTO();

//			log.info("...............isMultiRuleType()=" + isMultiRuleType());
			log.info("...............getSelectedDocType()="
					+ getSelectedDocType());
//			if (isMultiRuleType() && getSelectedDocType2() != null
//					&& !getSelectedDocType2().isEmpty()) {
//
//				searchBean.getDataFilterBeans().add(
//						new SearchMultiFilterBean(new SearchFilterBean(
//								"docType", SearchOperator.OPERATOR_EQUALS
//										.value(), getSelectedDocType()),
//								new SearchFilterBean("docType",
//										SearchOperator.OPERATOR_EQUALS.value(),
//										getSelectedDocType2(),
//										SearchCondition.CONDITION_OR.value())));
//			} else {
			if (getSelectedDocType() != null) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("docType",
								SearchOperator.OPERATOR_EQUALS.value(),
								getSelectedDocType()));
				isSearchBeanEmpty = false;
			}
			if (getSelectedDocId() != null) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("docId",
								SearchOperator.OPERATOR_EQUALS.value(),
								getSelectedDocId()));
			}
//			if(getFromDate() != null){
//				searchBean.getDataFilterBeans().add(
//						new SearchFilterBean("actionedDate",
//								SearchOperator.OPERATOR_GREATER_EQUAL.value(),
//								getFromDate()));
//			searchBean.getDataFilterBeans().add(
//					new SearchMultiFilterBean(new SearchFilterBean(
//							"actionedDate", SearchOperator.OPERATOR_GREATER_EQUAL
//									.value(), getFromDate()),
//							new SearchFilterBean("actionedDate",
//									SearchOperator.OPERATOR_LESS_EQUAL.value(),
//									getToDate(),
//									SearchCondition.CONDITION_AND.value())));
//				isSearchBeanEmpty = false;
//			}
//			if(getToDate() != null){
//				Calendar calendar = Calendar.getInstance();
//			    calendar.setTime(getToDate());
//			    calendar.set(Calendar.HOUR_OF_DAY, 23);
//			    calendar.set(Calendar.MINUTE, 59);
//			    calendar.set(Calendar.SECOND, 0);
//			    
//			    Date toDate = new Timestamp(calendar.getTime().getTime());
//				searchBean.getDataFilterBeans().add(
//						new SearchFilterBean("actionedDate",
//								SearchOperator.OPERATOR_LESS_EQUAL.value(),
//								toDate));		
//				isSearchBeanEmpty = false;
//			}
			System.out.println("TrackingDiaryReqDTO-docType"
					+ getSelectedDocType());
			System.out
					.println("TrackingDiaryReqDTO-docId" + getSelectedDocId());
             if(!isSearchBeanEmpty){
			reqDTO.setSearchBean(searchBean);
			TrackingDiaryResDTO resDTO = new TrackingDiaryResDTO();
			try {
				ITrackingDiarySO trackingDiarySO = (ITrackingDiarySO) BusinessDelegate
						.getInstance().getServiceObject("cc",
								ITrackingDiarySO.class);
				resDTO = trackingDiarySO
						.retrieveTrackingDiaryBySearchBean(reqDTO);
				// if(isMultiRuleType() && getSelectedDocType2() != null &&
				// !getSelectedDocType2().isEmpty()){
				// SearchResultBean<PmTrackingDiary> searchResultBean = new
				// SearchResultBean<PmTrackingDiary>();
				// int totalRecordResult1 =
				// resDTO.getResultBean().getTotalRecord();
				// searchResultBean.setResultList(resDTO.getResultBean().getResultList());
				// //retrive second round of Tracking Diary with
				// selectedDocType2
				// TrackingDiaryReqDTO reqDTO2 = new TrackingDiaryReqDTO();
				// searchBean.getDataFilterBeans().add(
				// new SearchFilterBean("docType",
				// SearchOperator.OPERATOR_EQUALS.value(),
				// getSelectedDocType2()));
				// reqDTO2.setSearchBean(searchBean);
				// TrackingDiaryResDTO resDTO2 = new TrackingDiaryResDTO();
				// trackingDiarySO = (ITrackingDiarySO) BusinessDelegate
				// .getInstance().getServiceObject("cc",
				// ITrackingDiarySO.class);
				// resDTO2 = trackingDiarySO
				// .retrieveTrackingDiaryBySearchBean(reqDTO2);
				// searchResultBean.setTotalRecord(resDTO2.getResultBean().getTotalRecord()
				// + totalRecordResult1);
				// searchResultBean.getResultList().addAll(resDTO2.getResultBean().getResultList());//add
				// this result to previous list
				// return searchResultBean;
				// }
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
             
			// System.out.println("resDTO.getResultBean()====================================: "+
			// resDTO.getResultBean().getTotalRecord());
			return resDTO.getResultBean();
             }else{
            	 return new SearchResultBean();
             }

		}
	}

}
