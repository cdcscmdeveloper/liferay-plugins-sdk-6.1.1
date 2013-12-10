package my.ep.web.backingbean.ss.loiloa;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.ss.quationtender.IQTAwardSO;
import my.ep.biz.service.ss.quationtender.IQTSpecSO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.ss.quationtender.AwardSupplierReqDTO;
import my.ep.dto.ss.quationtender.AwardSupplierResDTO;
import my.ep.dto.ss.specification.SpecificationReqDTO;
import my.ep.dto.ss.specification.SpecificationResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCurrency;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.ss.quationtender.ScBondDtl;
import my.ep.persistence.entity.ss.quationtender.ScLoa;
import my.ep.persistence.entity.ss.quationtender.ScQt;
import my.ep.persistence.entity.ss.quationtender.ScQtItem;
import my.ep.persistence.entity.ss.quationtender.ScQtProposalRanking;
import my.ep.persistence.entity.ss.quationtender.ScQtSpec;
import my.ep.type.pm.RoleCode;
import my.ep.type.ss.LoiLoaDocType;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.ss.loiloa.LOADetailManagedBean;
import my.ep.web.ss.quationtender.common.QTConstants;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * @author Viswanath
 * 
 */
@ManagedBean(name = "lOADetailBackingBean")
@RequestScoped
public class LOADetailBackingBean extends BaseBackingBean<LOADetailManagedBean> {
	public static Logger log = EpLogger.getLogger(LOADetailBackingBean.class);

	private static final String MODULE_NAME = "ss";

	public LOADetailBackingBean() {
		log.info("inside LOADetailBackingBean constructor");
		getManagedBean().setScQtSpec(new ScQtSpec());
	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	public void addPerformanceBond() {
		debugLogger("addPerformanceBond");
		ScBondDtl scBondDtl = new ScBondDtl();
		try {

			List<ScBondDtl> bondDtlList = getManagedBean().getScBondDtlList();
			if (null == bondDtlList) {
				bondDtlList = new ArrayList<ScBondDtl>();
			}
			bondDtlList.add(scBondDtl);
			this.getManagedBean().setScBondDtlList(bondDtlList);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
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

	public String createLOAAction(ScQtProposalRanking scQtProposalRanking,
			String docType) {
		debugLogger("createLOAAction");
		IQTAwardSO iqtAwardSO = null;
//		IAddressSO addressSO;
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

			AwardSupplierResDTO resDTO = iqtAwardSO.retrieveLOADtl(reqDTO);
			
			ScQt scQt = resDTO.getScQt();
			if (scQt != null) {
				System.out.println("Qt Id and Qt No : " + scQt.getQtId() + "|" + scQt.getQtNo());
			}
			
			List<ScQtItem> itemList = resDTO.getItemResultBean().getResultList();
			System.out.println("ItemList: " + itemList.size());
			
			ScLoa scLoa = resDTO.getScLoa();
			
			if (scLoa != null) {
				System.out.println("Loi Id : " + scLoa.getLoaId());
				getManagedBean().setScLoa(scLoa);
				getManagedBean().setScQtProposalRanking(scQtProposalRanking);
				getManagedBean().getScLoa().setSupplierName(
						getManagedBean().getScQtProposalRanking()
								.getScQtProposal().getScQtSupplier()
								.getSmSupplier().getSupplierName());

				getManagedBean().setSmMofAccount(new SmMofAccount());
				getManagedBean().getSmMofAccount().setMofNo("MOF000001");
				
				getManagedBean().setQt(scQt);
				
				PmUser user = new FacesScopes().getUserSessionBean();
				getManagedBean().getScLoa().setCreatedBy(user.getUserId());
				getManagedBean().getScLoa().setChangedBy(user.getUserId());
				getManagedBean().getScLoa().setCreatedDate(new Timestamp(System.currentTimeMillis()));
				
				getManagedBean().setScQtItemList(itemList);
			} else {
				
				getManagedBean().setQt(new ScQt());
				getManagedBean().setScLoa(new ScLoa());
				
				getManagedBean().setScQtProposalRanking(scQtProposalRanking);

				getManagedBean().getScLoa().setSupplierName(
						getManagedBean().getScQtProposalRanking()
								.getScQtProposal().getScQtSupplier()
								.getSmSupplier().getSupplierName());
				
				getManagedBean().getScLoa().setLoaType("Standard");
				getManagedBean().getScLoa().setLoaDuration(14);
				getManagedBean().getScLoa().setBondDuration(30);
				
				getManagedBean().setSmMofAccount(new SmMofAccount());
				getManagedBean().getSmMofAccount().setMofNo("MOF000001");
				
				if (getManagedBean().getScQtSpec() != null) {
					getManagedBean().setScQtSpec(new ScQtSpec());
					IQTSpecSO specSO = BusinessDelegate.getInstance()
							.getServiceObject(MODULE_NAME, IQTSpecSO.class);
					SpecificationReqDTO specReqDTO = new SpecificationReqDTO();
					SpecificationResDTO specResDTO = null;
					specReqDTO.setQtId(1L);
					specResDTO = specSO.retrieveSpecDetails(specReqDTO);
					if (specResDTO.getScQtSpec() != null) {

						getManagedBean().setScQtSpec(specResDTO.getScQtSpec());
						getManagedBean().getScQtSpec().setFulfilmentType(
								specResDTO.getScQtSpec().getFulfilmentType());
						getManagedBean().getScQtSpec().setContractDuration(
								specResDTO.getScQtSpec().getContractDuration());
						getManagedBean().setPmCurrency(new PmCurrency());
						getManagedBean().getScQtSpec().getPmCurrency().setCurrencyCode(specResDTO.getScQtSpec().getPmCurrency().getCurrencyCode());
						getManagedBean().getScQtSpec().getPmCurrency().setCurrencyName(specResDTO.getScQtSpec().getPmCurrency().getCurrencyName());
					}
				}

				PmUser user = new FacesScopes().getUserSessionBean();
				getManagedBean().setPmUser(user);
				getManagedBean().getScLoa().setOfficierName(user.getUserName());
				getManagedBean().getScLoa().setOfficerDesignation(
						user.getDesignation());

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return QTConstants.LOA_PAGE;
	}

	/**
	 * Method to save the LOA details
	 */
	public void saveLOADetailsAction() {
		debugLogger("saveLOADetailsAction");
		IQTAwardSO iqtAwardSO = null;
		IStatusSO statusSO = null;
		try {

			iqtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					MODULE_NAME, IQTAwardSO.class);
			AwardSupplierReqDTO awardSupplierReqDTO = new AwardSupplierReqDTO();
			awardSupplierReqDTO.setDocType(LoiLoaDocType.DOCTYPE_QUOTATION_TENDER.value());

			statusSO = BusinessDelegate.getInstance().getServiceObject("pm",
					IStatusSO.class);

			ScLoa scLoa = this.getManagedBean().getScLoa();
			scLoa.setIssueDate(new Date());

			// Setting the pm status for LOI details
			StatusReqDTO reqDTO = new StatusReqDTO();
			reqDTO.setStatusId(1l);
			StatusResDTO pmStatus = statusSO.retrieveStatusByStatusId(reqDTO);
			PmStatus status = pmStatus.getPmStatus();
			scLoa.setPmStatus(status);

			awardSupplierReqDTO.setScLoa(scLoa);

			AwardSupplierResDTO awardSupplierResDTO = iqtAwardSO
					.saveLOADtl(awardSupplierReqDTO);

			this.getManagedBean().setScLoa(awardSupplierResDTO.getScLoa());

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"SuccessFully Saved", "LOA"));

		} catch (Exception ex) {
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							ex.toString(), ex.getMessage()));
		}
	}
}
