package my.ep.web.backingbean.ss.loiloa;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.ss.quationtender.IQTAwardSO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.ss.quationtender.AwardSupplierReqDTO;
import my.ep.dto.ss.quationtender.AwardSupplierResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.ss.quationtender.ScLoi;
import my.ep.persistence.entity.ss.quationtender.ScNegoLocation;
import my.ep.persistence.entity.ss.quationtender.ScNegoTerm;
import my.ep.persistence.entity.ss.quationtender.ScQt;
import my.ep.persistence.entity.ss.quationtender.ScQtItem;
import my.ep.persistence.entity.ss.quationtender.ScQtProposalRanking;
import my.ep.persistence.entity.ss.quationtender.ScQtSpec;
import my.ep.persistence.entity.ss.quationtender.ScTerm;
import my.ep.persistence.entity.ss.quationtender.ScTermDesc;
import my.ep.type.pm.RoleCode;
import my.ep.type.ss.LoiLoaDocType;
import my.ep.type.ss.TermType;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.ss.loiloa.LOIDetailManagedBean;
import my.ep.web.ss.quationtender.common.QTConstants;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * @author Viswanath
 * 
 */
@ManagedBean(name = "lOIDetailBackingBean")
@RequestScoped
public class LOIDetailBackingBean extends BaseBackingBean<LOIDetailManagedBean> {
	public static Logger log = EpLogger
			.getLogger(LOILOADetailsBackingBean.class);

	private static final String MODULE_NAME = "ss";

	public LOIDetailBackingBean() {
		log.info("inside LOIDetailBackingBean constructor");

		getManagedBean().setScQtSpec(new ScQtSpec());
	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	@PostConstruct
	public void init() {
	}

	public List<SelectItem> getApproverNames() {
		debugLogger("getApproverNames");
		List<SelectItem> selectItems = new ArrayList<SelectItem>();

		try {
			IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(
					"pm", IUserSO.class);
			List<PmUser> comitteeUsersUsers = new ArrayList<PmUser>();
			if (getManagedBean().getComitteeUsers() == null) {
				UserReqDTO reqDTO = new UserReqDTO();
				reqDTO.setRoleCode(RoleCode.APPROVER.value());
				UserResDTO resDTO = userSO.retrieveUserListByRole(reqDTO);
				comitteeUsersUsers = resDTO.getUserList();
			}
			selectItems.add(new SelectItem(null, "--Select One--"));
			for (int i = 0; i < comitteeUsersUsers.size(); i++) {
				PmUser user = (PmUser) comitteeUsersUsers.get(i);
				selectItems.add(new SelectItem(user.getUserId(), user
						.getUserName()));
			}
			this.getManagedBean().setComitteeUsers(selectItems);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getManagedBean().getComitteeUsers();
	}

	public void addNegotiationLocation() {
		debugLogger("addNegotiationLocation");
		ScNegoLocation negoLocation = new ScNegoLocation();
		try {
			List<ScNegoLocation> negoLocationList = getManagedBean()
					.getNegoLocationList();
			if (null == negoLocationList) {
				negoLocationList = new ArrayList<ScNegoLocation>();
			}
			negoLocationList.add(negoLocation);
			this.getManagedBean().setNegoLocationList(negoLocationList);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void addNegotiationTerm() {
		debugLogger("addNegotiationTerm");
		ScNegoTerm negoTerm = new ScNegoTerm();
		try {
			List<ScNegoTerm> negoTermsList = getManagedBean()
					.getNegoTermsList();
			if (null == negoTermsList) {
				negoTermsList = new ArrayList<ScNegoTerm>();
			}
			negoTermsList.add(negoTerm);
			this.getManagedBean().setNegoTermsList(negoTermsList);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void populatePreDefinedDetails() {
		debugLogger("populatePreDefinedDetails");
		IQTAwardSO qtAwardSO = null;
		try {
			qtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					MODULE_NAME, IQTAwardSO.class);
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setTermType(TermType.TERM_LOI.value());
			reqDTO.setLanguageCode("en");

			AwardSupplierResDTO resDTO = qtAwardSO
					.retrieveTermDescListByTermType(reqDTO);

			List<ScTermDesc> list = resDTO.getTermDescResultBean()
					.getResultList();
			System.out.println("LOI list: " + list.size());

			getManagedBean().setScTermDescList(list);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addPredefinedTerms() {
		debugLogger("addPredefinedTerms");

		ScNegoTerm negoTerms = new ScNegoTerm();

		List<ScNegoTerm> list = getManagedBean().getNegoTermsList();

		List<ScTermDesc> scTermList1 = getManagedBean()
				.getSelectedCheckDetails();

		ScTerm term = new ScTerm();

		negoTerms.setScTerm(term);

		negoTerms.getScTerm().setScTermDescs(scTermList1);

		list.add(negoTerms);

		getManagedBean().setNegoTermsList(list);

	}

	public String createLOIAction(ScQtProposalRanking scQtProposalRanking,
			String docType) {
		debugLogger("createLOIAction");
		IQTAwardSO iqtAwardSO = null;
//		IAddressSO addressSO = null;
//		List<SmSupplierBranch> bList = new ArrayList<SmSupplierBranch>();
		try {

			long id = scQtProposalRanking.getQtProposalRankingId();

			iqtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					MODULE_NAME, IQTAwardSO.class);
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setDocType(LoiLoaDocType.DOCTYPE_QUOTATION_TENDER.value());
			reqDTO.setDocId(id);

			String language = new FacesScopes().getUserLocale().getLanguage();
			reqDTO.setLanguageCode(language);

			docType = reqDTO.getDocType();

			AwardSupplierResDTO resDTO = iqtAwardSO.retrieveLOIDtl(reqDTO);

			ScQt scQt = resDTO.getScQt();
			if (scQt != null) {
				System.out.println("Qt Id and Qt No : " + scQt.getQtId() + "|" + scQt.getQtNo());
			}

			List<ScQtItem> itemList = resDTO.getItemResultBean().getResultList();
			System.out.println("itemlist: " + itemList.size());
			
			List<ScNegoLocation> negoLocationList = resDTO.getNegoLocResultBean().getResultList();
			System.out.println("list: " + negoLocationList.size());
			
			List<ScNegoTerm> negoTermList = resDTO.getNegoTermResultBean().getResultList();
			System.out.println("negoTermlist: " + negoTermList.size());
			
			ScLoi scLoi = resDTO.getScLoi();
			
			if (scLoi != null) {
				System.out.println("Loi Id : " + scLoi.getLoiId());
				getManagedBean().setScLoi(scLoi);
				getManagedBean().setScQtProposalRanking(scQtProposalRanking);
				getManagedBean().getScLoi().setSupplierName(
						getManagedBean().getScQtProposalRanking()
								.getScQtProposal().getScQtSupplier()
								.getSmSupplier().getSupplierName());

				getManagedBean().setSmMofAccount(new SmMofAccount());
				getManagedBean().getSmMofAccount().setMofNo("MOF000001");
				
				getManagedBean().setQt(scQt);
				
				PmUser user = new FacesScopes().getUserSessionBean();
				getManagedBean().getScLoi().setCreatedBy(user.getUserId());
				getManagedBean().getScLoi().setChangedBy(user.getUserId());
				getManagedBean().getScLoi().setCreatedDate(new Timestamp(System.currentTimeMillis()));
				
				getManagedBean().setScQtItemList(itemList);
				
				getManagedBean().setNegoLocationList(negoLocationList);
				getManagedBean().setNegoTermsList(negoTermList);

			} else {
				System.out.println(" Inside if else Condition");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return QTConstants.LOI_PAGE;
	}

	/**
	 * Method to save the LOI details
	 */
	public void saveLOIDetailsAction() {
		debugLogger("saveLOIDetailsAction");
		IStatusSO statusSO = null;
		IQTAwardSO iqtAwardSO = null;
		try {

			iqtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					MODULE_NAME, IQTAwardSO.class);

			AwardSupplierReqDTO awardSupplierReqDTO = new AwardSupplierReqDTO();
			awardSupplierReqDTO
					.setDocType(LoiLoaDocType.DOCTYPE_QUOTATION_TENDER.value());

			statusSO = BusinessDelegate.getInstance().getServiceObject("pm",
					IStatusSO.class);

			ScLoi scLoi = this.getManagedBean().getScLoi();
			scLoi.setIssueDate(new Date());

			// Setting the pm status for LOI details
			StatusReqDTO reqDTO = new StatusReqDTO();
			reqDTO.setStatusId(1l);
			StatusResDTO pmStatus = statusSO.retrieveStatusByStatusId(reqDTO);
			PmStatus status = pmStatus.getPmStatus();
			scLoi.setPmStatus(status);

			List<ScNegoLocation> negoLocations = this.getManagedBean()
					.getNegoLocationList();
			scLoi.setScNegoLocations(negoLocations);

			List<ScNegoTerm> negoTerms = this.getManagedBean()
					.getNegoTermsList();
			scLoi.setScNegoTerms(negoTerms);

			awardSupplierReqDTO.setScLoi(scLoi);

			AwardSupplierResDTO awardSupplierResDTO = iqtAwardSO
					.saveLOIDtl(awardSupplierReqDTO);

			this.getManagedBean().setScLoi(awardSupplierResDTO.getScLoi());

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"SuccessFully Saved", "LOI"));
		} catch (Exception ex) {
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							ex.toString(), ex.getMessage()));
		}
	}
}
