package my.ep.web.backingbean.sm.rejectionacknowlegdementletter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.sm.rejectionacknowlegdementletter.RejectionAckLetterManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

@ManagedBean(name = "rejectionAckLetterBB")
@RequestScoped
public class RejectionAckLetterBackingBean extends BaseBackingBean<RejectionAckLetterManagedBean>{
	public static Logger log = Logger.getLogger(RejectionAckLetterBackingBean.class);
	private static final String REJECTION_ACK_MAIN = "/xhtml/sm/rejectionackletter/rejectionAckMain.xhtml";
	private static final String REJECTION_ACK_DET = "/xhtml/sm/rejectionackletter/rejectionAckLetterInfo.xhtml";
			
	public void prerenderRejectAckLetter(){
		log.info("*****prerenderRejectAckLetter********");
		FacesContext fc = FacesContext.getCurrentInstance();
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		List<PmUserOrg> activeOrg = PmCommonUtil.retrieveActiveOrg(pmUser.getUserId());
		if(activeOrg.size() > 0){
			PmUserOrg userOrg = activeOrg.get(0);
			PmZone pmZone = userOrg.getPmOrgProfile().getPmZone();
			getManagedBean().setZoneId(pmZone.getZoneId());
		}
		resetSearch();
		getManagedBean().getRejectionAckDataModel().updatePagingResults(null);
		getManagedBean().setSearchAction(false);
		getManagedBean().setRegStatusDescList(retrieveRegistrationStatusList());
		
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null,REJECTION_ACK_MAIN);
	}
	
	public void searchAction(){
		getManagedBean().setSearchAction(true);
		this.getManagedBean().getRejectionAckDataModel().reloadData();
	}
	
	public void printRejectionAcknowledgementLetter() throws IOException{
		String applId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("applId"); 

		PmUser pmUser = new FacesScopes().getUserSessionBean();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.SM_APPL_ID, applId + "");
		paramMap.put(ReportConst.PM_USER_ID, pmUser.getUserId() + "");

		generateReport(ReportConst.SM_REJECTION_ACK_LETTER, paramMap);
	}
	
	/**
	 * Retrieve all registration status list
	 * 
	 * @return List<PmStatusDesc>
	 */
	public List<PmStatusDesc> retrieveRegistrationStatusList() {
		StatusReqDTO reqDTO = new StatusReqDTO();
		reqDTO.setLanguageCode(new FacesScopes().getUserLocale().getLanguage());
		StatusResDTO resDTO = null;
		resDTO = SmServiceLookup.getStatusSO().retrieveRegStatusList(reqDTO);
		if (resDTO != null) {
			return resDTO.getStatusDescList();
		} else {
			return null;
		}
	}
	
	public void navigateToApplInfo(SmAppl appl){
		getManagedBean().setSelectedAppl(appl);
		
		if(appl.getSmSupplier().getSmMofAccounts().size() > 0){
			appl.getSmSupplier().setSmMofAccount(appl.getSmSupplier().getSmMofAccounts().get(0));
		}
		
		FacesContext fc = FacesContext.getCurrentInstance();
		
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, REJECTION_ACK_DET);
	}
	
	public void navigateToResult(){
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, REJECTION_ACK_MAIN);
	}
	
	public void resetSearch() {
		getManagedBean().setCompanyName(StringUtils.EMPTY);
		getManagedBean().setRegNo(StringUtils.EMPTY);
		getManagedBean().setMofNo(StringUtils.EMPTY);
		getManagedBean().setSupplierType(StringUtils.EMPTY);
		getManagedBean().setRegStatusId(null);
		getManagedBean().setApplType(StringUtils.EMPTY);
		getManagedBean().setApplNo(StringUtils.EMPTY);
	}
}
