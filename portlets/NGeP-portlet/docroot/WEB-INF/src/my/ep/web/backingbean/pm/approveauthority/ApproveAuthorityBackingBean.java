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
import my.ep.dto.pm.common.approveauthority.AuthorityCommitteeReqDTO;
import my.ep.dto.pm.common.approveauthority.AuthorityCommitteeResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmAuthority;
import my.ep.persistence.entity.pm.common.PmAuthorityCommittee;
import my.ep.persistence.entity.pm.common.PmRemarkAuthority;
import my.ep.util.ResourceBundleUtils;
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
	private static final Logger log = EpLogger.getLogger(ApproveAuthorityBackingBean.class);

	private static final String NAV_TO_MAIN = "approveAuthorityMain";
	
	//Tab values
	public static final int TAB_JAWATANKUASA_PEROLEHAN = 0;
	public static final int TAB_PENGURUSAN_TERTINGGI = 1;

	public ApproveAuthorityBackingBean() {
		//ApproveAythorityBackingBean constructor
	}

	/**
	 * This preLoadApproveAuthority function to load data from pm_authority
	 * and display to user
	 */
	public void preLoadApproveAuthority() {
		log.info("preLoadApproveAuthority...");
		
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
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * This loadCommitteeList method is to load data from pm_authority_committee
	 * and display to user
	 * @param eventTab the tab where the user has clicked
	 */
	public void loadCommitteeList(TabChangeEvent eventTab) {
		log.info("start loadComListMenu");
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
	 * This addNewCommittee method is to create new row before user 
	 * add new committee
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
	 * This saveNewCommittee method is to add new committee to pm_authority_committee
	 */
	public void saveNewCommittee(){
		log.info("saveNewCommittee");
		
		try{
			List<PmAuthorityCommittee> authCommitteeList = this.getManagedBean().getListAuthCommittee();
			
			if(authCommitteeList != null && !authCommitteeList.isEmpty()){
				saveAuthCommittee(authCommitteeList);
			}		
		} catch (Exception e){
			throw e;
		}
	}
	
	private void saveAuthCommittee(List<PmAuthorityCommittee> authCommitteeList){
		log.info("saveNewCommittee");
		
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
	
	private void reloadApproveAuthority(PmAuthorityCommittee authCommittee){
		log.info("reloadApproveAuthority");

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
		} catch (Exception e) {
			log.error(e);
		}
		
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_MAIN);
	}
	
	private void validateMessage(PmAuthorityCommittee authCommittee){
		log.info("validateMessage");
		
		if(StringUtils.isBlank(authCommittee.getCommitteeName())){
			throw new EpException(MessageConst.ERROR_775199, 
					new String[]{PmCommonUtil.getResourceBundleLabels("lbl.pm.approveAuthority.name")});
		}	
		
		if(!authCommittee.isBrandNew() && StringUtils.isBlank(this.getManagedBean().getRemark())){
			throw new EpException(MessageConst.ERROR_775199, 
					new String[]{PmCommonUtil.getResourceBundleLabels("lbl.pm.approveAuthority.remark")});
		}
	}
	
	public void setCommittee(PmAuthorityCommittee authCommittee){
		this.getManagedBean().setUpdateAuthCommittee(authCommittee);
		this.getManagedBean().setPrevCommitteeDetail(authCommittee);
		this.getManagedBean().setRemark("");
		RequestContext.getCurrentInstance().execute("updateCommitteeDialog.show()");
	}
	
	/**
	 * This updateAuthCommittee method is to update committee at pm_authority_committee
	 */
	public void updateAuthCommittee(){
		log.info("updateAuthCommittee");
		
		try{
			//update pm_authority_committee
			PmAuthorityCommittee authCommittee = this.getManagedBean().getUpdateAuthCommittee();
			
			if(authCommittee != null && authCommittee.getAuthCommitteeId() != null){                
				validateMessage(authCommittee);				
				authCommittee.setChangedBy(getUserIdSessionFromLoginId());
												
				//save to pm_record_authority
				PmRemarkAuthority remarkAuthority = new PmRemarkAuthority();
				remarkAuthority.setPmAuthorityCommittee(authCommittee);
				remarkAuthority.setRemark(this.getManagedBean().getRemark());
				remarkAuthority.setOldStatus(this.getManagedBean().getPrevCommitteeDetail().getRecordStatusFlg());
				remarkAuthority.setOldCommitteeName(this.getManagedBean().getPrevCommitteeDetail().getCommitteeName());
				remarkAuthority.setCreatedBy(this.getManagedBean().getPrevCommitteeDetail().getChangedBy());
				remarkAuthority.setCreatedDate(this.getManagedBean().getPrevCommitteeDetail().getChangedDate());
				remarkAuthority.setChangedBy(getUserIdSessionFromLoginId());
				remarkAuthority.setChangedDate(new Timestamp(new Date().getTime()));
				
				AuthorityCommitteeReqDTO reqDTO = new AuthorityCommitteeReqDTO();
				reqDTO.setAuthCommittee(authCommittee);
				reqDTO.setRemarkAuth(remarkAuthority);
				reqDTO.setUser(getFacesScopes().getUserSessionBean());
				reqDTO.setUserRoleValue(PmCommonUtil.getUserRoleValue());
				
				IApproveAuthorityCommitteeSO authCommitteeSO = EpEjbBusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IApproveAuthorityCommitteeSO.class);
				AuthorityCommitteeResDTO resDTO = authCommitteeSO.saveOrUpdateAuthorityCommittee(reqDTO);

				if(resDTO.isSuccessful()){
					PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(), FacesMessage.SEVERITY_INFO,
							MessageConst.INFO_775198, new String[]{resDTO.getAuthorityCommittee().getCommitteeName()});
				}
			}	
		} catch (Exception e){
			throw e;
		}	
	}
	
	/**
	 * This closePopup method is to close popup dialog
	 */
	public void closePopup(){
		log.info("closePopup");
		reloadApproveAuthority(this.getManagedBean().getUpdateAuthCommittee());
	}

	private FacesScopes getFacesScopes() {
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

	private void setAuthorityData(int tabId){
		int newtabId = tabId + 1;//tab view start with 0, in order to query from db, the id need to add 1
		
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
	
	private List<PmAuthorityCommittee> checkNullObj(List<PmAuthorityCommittee> pmCom){
		List<PmAuthorityCommittee> newComList = new ArrayList<PmAuthorityCommittee>();
		for(PmAuthorityCommittee com : pmCom){
			if(com.getAuthCommitteeId() != null)
				newComList.add(com);
		}
		return newComList;
	}
}
