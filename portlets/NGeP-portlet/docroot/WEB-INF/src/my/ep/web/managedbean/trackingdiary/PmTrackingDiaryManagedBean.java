package my.ep.web.managedbean.trackingdiary;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.trackingdiary.ITrackingDiarySO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.authorization.userorg.UserOrgReqDTO;
import my.ep.dto.pm.authorization.userorg.UserOrgResDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryReqDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryResDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmTrackingDiary;
import my.ep.type.aut.SearchOperator;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.OrganisationSearchBean;
import my.ep.web.bean.pm.UserProfilesSearchBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@ManagedBean(name = "pmTrackingDiaryManagedBean")
@SessionScoped
public class PmTrackingDiaryManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger
			.getLogger(PmTrackingDiaryManagedBean.class);
	private String functionType;
	// Paging for listing
	private IPagingDataModel<PmUser> userDataModel = new UserPagingDataModel();
	private IPagingDataModel<PmOrgProfile> userOrgDataModel = new UserOrgPagingDataModel();
	private OrganisationSearchBean organisationSearchBean;
	private UserProfilesSearchBean userProfileSearchBean;
	private boolean orgProfileRequired;
	// private PmOrgProfile selectedOrgProfile;
	// private PmUser selectedUser;
	private boolean multiRuleType = false;
	private String selectedDocType;
	private String selectedDocType2;
	private Long selectedDocId;

	public String getSelectedDocType() {
		return selectedDocType;
	}

	public void setSelectedDocType(String selectedDocType) {
		this.selectedDocType = selectedDocType;
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
		log.info("..............setOrganisationSearchBean...............");
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
					log.info("......getOrganisationSearchBean().getOrgProfile().getOrgProfileId()="
							+ getOrganisationSearchBean().getOrgProfile()
									.getOrgProfileId());
				if (getOrganisationSearchBean().getOrgProfile() != null
						&& getOrganisationSearchBean().getOrgProfile()
								.getOrgProfileId() != null) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("orgProfileId",
									SearchOperator.OPERATOR_EQUALS.value(),
									getOrganisationSearchBean().getOrgProfile()
											.getOrgProfileId()));

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

			TrackingDiaryReqDTO reqDTO = new TrackingDiaryReqDTO();
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("docType",
							SearchOperator.OPERATOR_EQUALS.value(),
							getSelectedDocType()));
			if (getSelectedDocId() != null) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("docId",
								SearchOperator.OPERATOR_EQUALS.value(),
								getSelectedDocId()));
			}
			System.out.println("TrackingDiaryReqDTO-docType"
					+ getSelectedDocType());
			System.out
					.println("TrackingDiaryReqDTO-docId" + getSelectedDocId());

			reqDTO.setSearchBean(searchBean);
			TrackingDiaryResDTO resDTO = new TrackingDiaryResDTO();
			try {
				ITrackingDiarySO trackingDiarySO = (ITrackingDiarySO) BusinessDelegate
						.getInstance().getServiceObject("cc",
								ITrackingDiarySO.class);
				resDTO = trackingDiarySO
						.retrieveTrackingDiaryBySearchBean(reqDTO);
				if(isMultiRuleType() && getSelectedDocType2() != null && !getSelectedDocType2().isEmpty()){
					 SearchResultBean<PmTrackingDiary> searchResultBean = new SearchResultBean<PmTrackingDiary>();
					 int totalRecordResult1 = resDTO.getResultBean().getTotalRecord();
					 searchResultBean.setResultList(resDTO.getResultBean().getResultList());
					 //retrive second round of Tracking Diary with selectedDocType2
						TrackingDiaryReqDTO reqDTO2 = new TrackingDiaryReqDTO();
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean("docType",
										SearchOperator.OPERATOR_EQUALS.value(),
										getSelectedDocType2()));
						reqDTO2.setSearchBean(searchBean);
						TrackingDiaryResDTO resDTO2 = new TrackingDiaryResDTO();
						trackingDiarySO = (ITrackingDiarySO) BusinessDelegate
								.getInstance().getServiceObject("cc",
										ITrackingDiarySO.class);
						resDTO2 = trackingDiarySO
								.retrieveTrackingDiaryBySearchBean(reqDTO2);
						searchResultBean.setTotalRecord(resDTO2.getResultBean().getTotalRecord() + totalRecordResult1);
						searchResultBean.getResultList().addAll(resDTO2.getResultBean().getResultList());//add this result to previous list
					 return searchResultBean; 
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// System.out.println("resDTO.getResultBean()====================================: "+
			// resDTO.getResultBean().getTotalRecord());
			return resDTO.getResultBean();

		}
	}

}
