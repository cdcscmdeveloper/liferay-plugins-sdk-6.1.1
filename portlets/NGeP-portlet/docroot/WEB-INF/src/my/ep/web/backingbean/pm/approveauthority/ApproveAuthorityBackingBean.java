package my.ep.web.backingbean.pm.approveauthority;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.service.pm.common.approveauthority.IApproveAuthorityCommitteeSO;
import my.ep.biz.service.pm.common.approveauthority.IApproveAuthoritySO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.approveauthority.AuthorityCommitteeReqDTO;
import my.ep.dto.pm.common.approveauthority.AuthorityCommitteeResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAuthority;
import my.ep.persistence.entity.pm.common.PmAuthorityCommittee;
import my.ep.persistence.entity.pm.common.PmRemarkAuthority;
import my.ep.persistence.logging.LogKey;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.pm.approveauthority.ApproveAuthorityManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;

/**
 * This ApproveAuthorityBackingBean class is to manage MOF Approving Authority page
 */

@ManagedBean(name = "ApproveAuthorityBackingBean")
@ViewScoped
public class ApproveAuthorityBackingBean extends BaseBackingBean<ApproveAuthorityManagedBean> {

	private static final long serialVersionUID = 1L;

	private static final String NAV_TO_MAIN = "approveAuthorityMain";

	private static final String APPROVE_AUTHORITY_BACKING_BEAN = "ApproveAuthorityBackingBean";

	//Tab values
	public static final int TAB_JAWATANKUASA_PEROLEHAN = 0;
	public static final int TAB_PENGURUSAN_TERTINGGI = 1;

	public ApproveAuthorityBackingBean() {
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		if (pmUser != null && pmUser.getUserId() != null) {
			new LogKey(pmUser.getUserId());
		} else {
			new LogKey(1L);
		}
	}

	/**
	 * This preLoadApproveAuthority function to load data from pm_authority and display to user
	 */
	public void preLoadApproveAuthority() {
		renewSesionObject();

		try {
			IApproveAuthoritySO approveAuthoritySo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IApproveAuthoritySO.class);
			
			List<PmAuthority> resultComittee = approveAuthoritySo.getPMAuthorityList();
			this.getManagedBean().setListAuthCode(resultComittee);

			//Initialize for update committee - start
			PmAuthorityCommittee committee = new PmAuthorityCommittee();
			committee.setRecordStatus(true);
			this.getManagedBean().setUpdateAuthCommittee(committee);
			//Initialize for update committee - end

			this.getManagedBean().setSelectedTabIndex(TAB_JAWATANKUASA_PEROLEHAN);
			setAuthorityData(TAB_JAWATANKUASA_PEROLEHAN);

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_MAIN);
		} catch (Exception ex) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.PROFILE_MANAGEMENT,
				ParameterConst.MOF_APPROVING_AUTHORITY, APPROVE_AUTHORITY_BACKING_BEAN,
				"preLoadApproveAuthority", null, ex);
		}
	}
	
	/**
	 * This loadCommitteeList method is to load data from pm_authority_committee and display to user
	 * 
	 * @param eventTab the tab where the user has clicked
	 */
	public void loadCommitteeList(TabChangeEvent eventTab) {
		PmAuthority tabAuth = (PmAuthority) eventTab.getData();
		this.getManagedBean().setAuthCodeDesc(tabAuth.getAuthCodeDesc());
		this.getManagedBean().setAuthority(tabAuth);

		List<PmAuthorityCommittee> newComList = new ArrayList<>();
		for(PmAuthority pmAuth : getManagedBean().getListAuthCode()){
			if(tabAuth.getAuthId().equals(pmAuth.getAuthId())){
				List<PmAuthorityCommittee> pmAuthCom = pmAuth.getPmAuthorityCommittee();
				
				for(PmAuthorityCommittee committee : pmAuthCom){
					newComList.add(committee);
				}

				Collections.sort(newComList, new Comparator<PmAuthorityCommittee>(){
					@Override
					public int compare(PmAuthorityCommittee c1, PmAuthorityCommittee c2){
							return c1.getAuthCommitteeId().compareTo(c2.getAuthCommitteeId());
					}
				});
			}
		}
		this.getManagedBean().setListAuthCommittee(newComList);
	}

	/**
	 * This addNewCommittee method is to create new row before user add new committee
	 */
	public void addNewCommittee() {
		int index = 0;
		if (this.getManagedBean().getListAuthCommittee() == null) {
			this.getManagedBean().setListAuthCommittee(new ArrayList<PmAuthorityCommittee>());
		} else {
			index = this.getManagedBean().getListAuthCommittee().size();
		}
		
		//create new row
		PmAuthorityCommittee authorityComm = new PmAuthorityCommittee();
		authorityComm.setPmAuthority(this.getManagedBean().getAuthority());
		this.getManagedBean().getListAuthCommittee().add(index,authorityComm);
		RequestContext.getCurrentInstance().execute(
				"apprAuthorityTblWidVar.paginator.endLink.click()");
	}

	/**
	 * Returns the list of active and inactive result statuses.
	 *
	 * @return an array of SelectItem containing active and inactive statuses
	 */
	public SelectItem[] getResultStatusList() {
		SelectItem[] items = new SelectItem[2];
		items[0] = new SelectItem(1, ResourceBundleUtils.getMessage("lbl.pm.active",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale()));
		items[1] = new SelectItem(0, ResourceBundleUtils.getMessage("lbl.pm.inactive",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale()));
		return items;
	}
	
	/**
	 * Saves the newly added authority committee records and navigates back to the main page.
	 *
	 * @return the navigation outcome to the main page
	 */
	public String saveNewCommittee(){
		try{
			List<PmAuthorityCommittee> authCommitteeList = this.getManagedBean().getListAuthCommittee();
			
			if(authCommitteeList != null && !authCommitteeList.isEmpty()){
				saveAuthCommittee(authCommitteeList);
			}
		} catch(EpException epx) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
							epx.getMessage(), epx.getParameters(), MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
		} catch (Exception ex){
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.PROFILE_MANAGEMENT,
				ParameterConst.MOF_APPROVING_AUTHORITY, APPROVE_AUTHORITY_BACKING_BEAN,
				"saveNewCommittee", null, ex);
		}
		return NAV_TO_MAIN;
	}

	/**
	 * Validates and saves newly added authority committees.
	 *
	 * @param authCommitteeList the list of authority committees
	 */
	private void saveAuthCommittee(List<PmAuthorityCommittee> authCommitteeList){
		for(PmAuthorityCommittee authCommittee : authCommitteeList){
			if(authCommittee.isBrandNew()){
				validateMessage(authCommittee);
				authCommittee.setCreatedBy(getUserIdSessionFromLoginId());
				authCommittee.setChangedBy(getUserIdSessionFromLoginId());
				authCommittee.setCreatedDate(new Timestamp(System.currentTimeMillis()));
				authCommittee.setChangedDate(new Timestamp(new Date().getTime()));
				
				AuthorityCommitteeReqDTO reqDTO = new AuthorityCommitteeReqDTO();
				reqDTO.setAuthCommittee(authCommittee);
				reqDTO.setUser(getFacesScopes().getUserSessionBean());
				reqDTO.setUserRoleValue(PmCommonUtil.getUserRoleValue());
				IApproveAuthorityCommitteeSO authCommitteeSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IApproveAuthorityCommitteeSO.class);
				AuthorityCommitteeResDTO resDTO = authCommitteeSO.saveOrUpdateAuthorityCommittee(reqDTO);
				
				if(resDTO.isSuccessful()){
					PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(), FacesMessage.SEVERITY_INFO,
							MessageConst.INFO_775197, new String[]{resDTO.getAuthorityCommittee().getCommitteeName()});
					reloadApproveAuthority(authCommittee);
				}
			}
		}
	}
	
	/**
	 * Reloads the authority and committee information after a successful save.
	 *
	 * @param authCommittee the authority committee used to reload the data
	 */
	private void reloadApproveAuthority(PmAuthorityCommittee authCommittee){
		try {
			IApproveAuthoritySO approveAuthoritySo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IApproveAuthoritySO.class);
			
			List<PmAuthority> resultComittee = approveAuthoritySo.getPMAuthorityList();
			this.getManagedBean().setListAuthCode(resultComittee);

			for(PmAuthority pmAuth : getManagedBean().getListAuthCode()){
				
				if(pmAuth.getAuthId().equals(authCommittee.getPmAuthority().getAuthId())){
					this.getManagedBean().setAuthCodeDesc(pmAuth.getAuthCodeDesc());
					this.getManagedBean().setAuthority(pmAuth);
					
					List<PmAuthorityCommittee> authComList = pmAuth.getPmAuthorityCommittee();

					Collections.sort(authComList, new Comparator<PmAuthorityCommittee>(){
						@Override
						public int compare(PmAuthorityCommittee c1, PmAuthorityCommittee c2){
							return c1.getAuthCommitteeId().compareTo(c2.getAuthCommitteeId());
						}
					});

					this.getManagedBean().setListAuthCommittee(pmAuth.getPmAuthorityCommittee());
				}
			}
		} catch (Exception ex) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.PROFILE_MANAGEMENT,
					ParameterConst.MOF_APPROVING_AUTHORITY, APPROVE_AUTHORITY_BACKING_BEAN,
					"reloadApproveAuthority", authCommittee.getAuthCommitteeId(), ex);
		}
	}
	
	/**
	 * Validates the authority committee details before saving.
	 *
	 * @param authCommittee the authority committee to validate
	 */
	private void validateMessage(PmAuthorityCommittee authCommittee){
		if(StringUtils.isBlank(authCommittee.getCommitteeName())){
			throw new EpException(MessageConst.ERROR_775199,
					new String[]{PmCommonUtil.getResourceBundleLabels("lbl.pm.approveAuthority.name")});
		}

		if(!authCommittee.isBrandNew() && StringUtils.isBlank(this.getManagedBean().getRemark())){
			throw new EpException(MessageConst.ERROR_775199,
					new String[]{PmCommonUtil.getResourceBundleLabels("lbl.pm.approveAuthority.remark")});
		}
	}

	/**
	 * Sets the selected committee details for update.
	 *
	 * @param authCommittee the authority committee to be updated
	 */
	public void setCommittee(PmAuthorityCommittee authCommittee){
		PmAuthorityCommittee editCommittee = new PmAuthorityCommittee();
		editCommittee.setAuthCommitteeId(authCommittee.getAuthCommitteeId());
		editCommittee.setCommitteeName(authCommittee.getCommitteeName());
		editCommittee.setRecordStatusFlg(authCommittee.getRecordStatusFlg());

		this.getManagedBean().setUpdateAuthCommittee(editCommittee);
		this.getManagedBean().setPrevCommitteeDetail(authCommittee);
		this.getManagedBean().setRemark("");
	}

	/**
	 * Adds an error message to the specified Faces context.
	 *
	 * @param fc the FacesContext instance
	 * @param msgCode the message code
	 * @param params the parameters used to format the message
	 */
	protected void addFacesMessage(FacesContext fc, String msgCode, Object[] params) {
		fc.addMessage("form:committeeList:updateCommitteeDialog:committeeMsgId",
				new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
				MessageUtils.getMessage(msgCode, params,MessageUtils.BUNDLE_COMMON_MESSAGES,
				new FacesScopes().getUserLocale()) + " (" + msgCode + ")"));
	}
	
	/**
	 * This updateAuthCommittee method is to update committee at pm_authority_committee
	 */
	public void updateAuthCommittee(){
		
		PmAuthorityCommittee popUpCommittee = this.getManagedBean().getUpdateAuthCommittee();
		
		try{
			FacesContext fc = FacesContext.getCurrentInstance();
			boolean hasError = false;
			if(StringUtils.isBlank(popUpCommittee.getCommitteeName())){
				addFacesMessage(fc, MessageConst.ERROR_775199, new String[]{
					PmCommonUtil.getResourceBundleLabels("lbl.pm.approveAuthority.name")});
				fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, "xhtml/pm/approveauthority/authorityPopUpScreen.xhtml");
				hasError = true;
			}

			if(StringUtils.isBlank(this.getManagedBean().getRemark())){
				addFacesMessage(fc, MessageConst.ERROR_775199,
						new String[]{PmCommonUtil.getResourceBundleLabels("lbl.pm.approveAuthority.remark")});
				fc.getApplication().getNavigationHandler()
						.handleNavigation(fc, null, "xhtml/pm/approveauthority/authorityPopUpScreen.xhtml");
				hasError = true;
			}

			if(hasError){
			    return;
			}

			if(popUpCommittee.getAuthCommitteeId() != null){
				PmAuthorityCommittee original = this.getManagedBean().getPrevCommitteeDetail();

				//object to save to db
				PmAuthorityCommittee updateCommittee = this.getManagedBean().getPrevCommitteeDetail();
				updateCommittee.setCommitteeName(popUpCommittee.getCommitteeName());
				updateCommittee.setRecordStatusFlg(popUpCommittee.getRecordStatusFlg());
				updateCommittee.setChangedBy(getUserIdSessionFromLoginId());
				
				//save to pm_record_authority
				PmRemarkAuthority remarkAuthority = new PmRemarkAuthority();
				remarkAuthority.setPmAuthorityCommittee(original);
				remarkAuthority.setRemark(this.getManagedBean().getRemark());
				remarkAuthority.setOldStatus(original.getRecordStatusFlg());
				remarkAuthority.setOldCommitteeName(original.getCommitteeName());
				remarkAuthority.setCreatedBy(original.getChangedBy());
				remarkAuthority.setCreatedDate(original.getChangedDate());
				remarkAuthority.setChangedBy(getUserIdSessionFromLoginId());
				remarkAuthority.setChangedDate(new Timestamp(new Date().getTime()));
				
				AuthorityCommitteeReqDTO reqDTO = new AuthorityCommitteeReqDTO();
				reqDTO.setAuthCommittee(updateCommittee);
				reqDTO.setRemarkAuth(remarkAuthority);
				reqDTO.setUser(getFacesScopes().getUserSessionBean());
				reqDTO.setUserRoleValue(PmCommonUtil.getUserRoleValue());
				
				IApproveAuthorityCommitteeSO authCommitteeSO = EpEjbBusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IApproveAuthorityCommitteeSO.class);
				AuthorityCommitteeResDTO resDTO = authCommitteeSO.saveOrUpdateAuthorityCommittee(reqDTO);

				if(resDTO.isSuccessful()){
					PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(), FacesMessage.SEVERITY_INFO,
							MessageConst.INFO_775198, new String[]{resDTO.getAuthorityCommittee().getCommitteeName()});
					RequestContext.getCurrentInstance().execute("updateCommitteeDialog.hide()");
				}
			}
		} catch (Exception ex){
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.PROFILE_MANAGEMENT,
				ParameterConst.MOF_APPROVING_AUTHORITY, APPROVE_AUTHORITY_BACKING_BEAN,
				"updateAuthCommittee", popUpCommittee.getAuthCommitteeId(), ex);
		}
	}
	
	/**
	 * Resets the committee update popup fields.
	 */
	public void closePopup(){
		this.getManagedBean().getUpdateAuthCommittee().setCommitteeName("");
		this.getManagedBean().getUpdateAuthCommittee().setRecordStatusFlg(1);
		this.getManagedBean().setRemark("");
	}

	private FacesScopes getFacesScopes(){
		return new FacesScopes();
	}
	
	/**
	 * This getJawatankuasaPerolehan method is to get id for TAB_JAWATANKUASA_PEROLEHAN
	 */
	public int getJawatankuasaPerolehan(){
		return TAB_JAWATANKUASA_PEROLEHAN;
	}
	
	/**
	 * This updatePengurusanTertinggiDetail method is to get a list of committee 
	 * for Jawatankuasa Perolehan Kerajaan di Kementerian Kewangan Malaysia
	 */
	public void updateJawatankuasaPerolehanDetail(){
		this.getManagedBean().setSelectedTabIndex(TAB_JAWATANKUASA_PEROLEHAN);
		setAuthorityData(TAB_JAWATANKUASA_PEROLEHAN);
	}
	
	/**
	 * This getJawatankuasaPerolehan method is to get id for TAB_PENGURUSAN_TERTINGGI
	 */
	public int getPengurusanTertinggi(){
		return TAB_PENGURUSAN_TERTINGGI;
	}
	
	/**
	 * This updatePengurusanTertinggiDetail method is to get a list of committee 
	 * for Pengurusan Tertinggi Kementerian Kewangan Malaysia
	 */
	public void updatePengurusanTertinggiDetail(){
		this.getManagedBean().setSelectedTabIndex(TAB_PENGURUSAN_TERTINGGI);
		setAuthorityData(TAB_PENGURUSAN_TERTINGGI);
	}
	
	/**
	 * Sets the authority data based on the selected tab.
	 *
	 * @param tabId the selected tab identifier
	 */
	private void setAuthorityData(int tabId){
		int newtabId = tabId + 1;
		
		for(PmAuthority pmAuth : getManagedBean().getListAuthCode()){
			this.getManagedBean().setAuthIdInString(Long.toString(pmAuth.getAuthId()));
			this.getManagedBean().setAuthCodeDesc(pmAuth.getAuthCodeDesc());
			if(pmAuth.getAuthId().compareTo(new Long(newtabId)) == 0){
				this.getManagedBean().setAuthority(pmAuth);
				this.getManagedBean().setComName(pmAuth.getAuthCodeDesc());
				
				List<PmAuthorityCommittee> authComList = checkNullObj(pmAuth.getPmAuthorityCommittee());
				
				if(authComList != null && !authComList.isEmpty()){
					Collections.sort(authComList, new Comparator<PmAuthorityCommittee>(){
						@Override
						public int compare(PmAuthorityCommittee c1, PmAuthorityCommittee c2){
								return c1.getAuthCommitteeId().compareTo(c2.getAuthCommitteeId());
						}
					});
				}
				this.getManagedBean().setListAuthCommittee(authComList);
			}
		}
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_MAIN);
	}
	
	/**
	 * Filters the committee list by removing records with null committee ID.
	 *
	 * @param pmCom the list of authority committees
	 * @return a filtered list of authority committees
	 */
	private List<PmAuthorityCommittee> checkNullObj(List<PmAuthorityCommittee> pmCom){
		List<PmAuthorityCommittee> newComList = new ArrayList<>();
		for(PmAuthorityCommittee com : pmCom){
			if(com.getAuthCommitteeId() != null)
				newComList.add(com);
		}
		return newComList;
	}
}
