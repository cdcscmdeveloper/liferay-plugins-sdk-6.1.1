package my.ep.web.backingbean.ss.loiloa;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.ss.quationtender.IQTAwardSO;
import my.ep.biz.service.ss.quationtender.IQTDetailSO;
import my.ep.biz.service.ss.quationtender.IQTSpecSO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.ss.quationtender.AwardSupplierReqDTO;
import my.ep.dto.ss.quationtender.AwardSupplierResDTO;
import my.ep.dto.ss.quationtender.QuationTenderReqDTO;
import my.ep.dto.ss.quationtender.QuationTenderResDTO;
import my.ep.dto.ss.specification.SpecificationReqDTO;
import my.ep.dto.ss.specification.SpecificationResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.ss.quationtender.ScLoi;
import my.ep.persistence.entity.ss.quationtender.ScNegoLocation;
import my.ep.persistence.entity.ss.quationtender.ScNegoTerm;
import my.ep.persistence.entity.ss.quationtender.ScQt;
import my.ep.persistence.entity.ss.quationtender.ScQtCriteria;
import my.ep.persistence.entity.ss.quationtender.ScQtProposalRanking;
import my.ep.persistence.entity.ss.quationtender.ScQtSpec;
import my.ep.persistence.entity.ss.quationtender.ScTerm;
import my.ep.persistence.entity.ss.quationtender.ScTermDesc;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.RoleCode;
import my.ep.type.ss.LoiLoaDocType;
import my.ep.type.ss.TermType;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.ss.loiloa.LOIDetailManagedBean;
import my.ep.web.ss.quationtender.common.QTConstants;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.event.TabChangeEvent;

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

		// getManagedBean().setQt(new ScQt());

		
		getManagedBean().setScLoi(new ScLoi());
		getManagedBean().getScLoi().setReqApproval(true);

		/*
		 * getManagedBean().setSmSupplier(new SmSupplier());
		 * getManagedBean().getSmSupplier().setSupplierName("Award Type");
		 * 
		 * getManagedBean().setSmMofAccount(new SmMofAccount());
		 * getManagedBean().getSmMofAccount().setMofNo("MOF000001");
		 * 
		 * getManagedBean().setScLoi(new ScLoi());
		 * getManagedBean().getScLoi().setReqApproval(true);
		 * 
		 * System.out.println(" Is Approval : " +
		 * getManagedBean().getScLoi().isReqApproval());
		 * 
		 * if (getManagedBean().getScQtSpec() == null) {
		 * getManagedBean().setScQtSpec(new ScQtSpec()); IQTSpecSO specSO =
		 * BusinessDelegate.getInstance().getServiceObject( MODULE_NAME,
		 * IQTSpecSO.class); SpecificationReqDTO reqDTO = new
		 * SpecificationReqDTO(); SpecificationResDTO resDTO = null;
		 * reqDTO.setQtId(144L); resDTO = specSO.retrieveSpecDetails(reqDTO); if
		 * (resDTO.getScQtSpec() != null) {
		 * 
		 * getManagedBean().setScQtSpec(new ScQtSpec()); //
		 * getManagedBean().getScQtSpec().setAwardType("L");
		 * getManagedBean().getScQtSpec().setAwardType(
		 * resDTO.getScQtSpec().getAwardType());
		 * System.out.println(" Award Type : " +
		 * getManagedBean().getScQtSpec().getAwardType()); } }
		 * 
		 * getManagedBean().setScQtCriteria(new ScQtCriteria());
		 * 
		 * getManagedBean().getScQtCriteria().setMultiLocality(true);
		 * 
		 * if (getManagedBean().getScQtCriteria().isMultiLocality()) {
		 * getManagedBean().setStatus("1"); } else {
		 * getManagedBean().setStatus("0"); }
		 */
	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	@PostConstruct
	public void init() {
		/*
		 * if (getManagedBean().getQt() == null) { getManagedBean().setQt(new
		 * ScQt());
		 * 
		 * IQTDetailSO QtDetailsSO = BusinessDelegate.getInstance()
		 * .getServiceObject(MODULE_NAME, IQTDetailSO.class);
		 * System.out.println("manga Details Object" + QtDetailsSO);
		 * QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
		 * 
		 * tenderReqDTO.setQtTndrNo("qt123");
		 * 
		 * QuationTenderResDTO tenderResDTO = new QuationTenderResDTO();
		 * 
		 * tenderResDTO = QtDetailsSO.retriveQtByQtNumber(tenderReqDTO);
		 * 
		 * System.out.println("====tenderResDTO.getQtDetail()." +
		 * tenderResDTO.getQtDetail().getQtNo());
		 * getManagedBean().getQt().setFileNo(
		 * tenderResDTO.getQtDetail().getFileNo());
		 * 
		 * }
		 */
	}

	public void onTabChangeAction(TabChangeEvent event) {
		String tabTitle = event.getTab().getTitle();

		if (QTConstants.SC_LOI_TAB.equalsIgnoreCase(tabTitle)) {

		} else if (QTConstants.SC_AWARD_TAB.equalsIgnoreCase(tabTitle)) {
			retrieveAwardDetailsTabDtl();
		} else if (QTConstants.SC_NEGOTIATION_TAB.equalsIgnoreCase(tabTitle)) {
			retrieveNegoLocationTabDtl();
		}

	}

	/**
	 * 
	 */
	private void retrieveAwardDetailsTabDtl() {
		IQTSpecSO specSO = null;
		try {
			getManagedBean().setScQtSpec(new ScQtSpec());
			if (getManagedBean().getScQtSpec() == null) {
				getManagedBean().setScQtSpec(new ScQtSpec());
				specSO = BusinessDelegate.getInstance().getServiceObject(
						MODULE_NAME, IQTSpecSO.class);
				SpecificationReqDTO reqDTO = new SpecificationReqDTO();
				SpecificationResDTO resDTO = null;
				reqDTO.setQtId(144L);
				resDTO = specSO.retrieveSpecDetails(reqDTO);
				if (resDTO.getScQtSpec() != null) {
					getManagedBean().setScQtSpec(new ScQtSpec());
					// getManagedBean().getScQtSpec().setAwardType("L");
					getManagedBean().getScQtSpec().setAwardType(
							resDTO.getScQtSpec().getAwardType());
					System.out.println(" Award Type : "
							+ getManagedBean().getScQtSpec().getAwardType());
				}
			}

			getManagedBean().setScQtCriteria(new ScQtCriteria());

			getManagedBean().getScQtCriteria().setMultiLocality(true);

			if (getManagedBean().getScQtCriteria().isMultiLocality()) {
				getManagedBean().setStatus("1");
			} else {
				getManagedBean().setStatus("0");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	private void retrieveNegoLocationTabDtl() {
		IQTAwardSO iqtAwardSO = null;
		try {
			iqtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					MODULE_NAME, IQTAwardSO.class);
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setDocType(LoiLoaDocType.DOCTYPE_QUOTATION_TENDER.value());
			reqDTO.setDocId(1L);

			String language = new FacesScopes().getUserLocale().getLanguage();
			reqDTO.setLanguageCode(language);

			// String docType = reqDTO.getDocType();

			AwardSupplierResDTO resDTO = iqtAwardSO.retrieveLOIDtl(reqDTO);

			ScLoi scLoi = resDTO.getScLoi();

			List<ScNegoLocation> negoLocList = resDTO.getNegoLocResultBean()
					.getResultList();
			System.out.println("list: " + negoLocList.size());

			List<ScNegoTerm> negoTermList = resDTO.getNegoTermResultBean()
					.getResultList();
			System.out.println("negoTermlist: " + negoTermList.size());

			System.out.println(" Term Decription : "
					+ negoTermList.get(0).getScTerm().getScTermDescs().get(0)
							.getTermDesc());

			if (scLoi != null) {
				getManagedBean().setNegoLocationList(negoLocList);
				getManagedBean().setNegoTermsList(negoTermList);

			} else {
				System.out.println(" No Information ");
			}

		} catch (Exception e) {
			e.printStackTrace();
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
		IAddressSO addressSO = null;
		List<SmSupplierBranch> bList = new ArrayList<SmSupplierBranch>();
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

			// getManagedBean().setScLoi(resDTO.getScLoi());
			ScLoi scLoi = resDTO.getScLoi();

			if (scLoi != null) {
				getManagedBean().setScLoi(scLoi);
				getManagedBean().setScQtProposalRanking(scQtProposalRanking);
				getManagedBean().getScLoi().setSupplierName(
						getManagedBean().getScQtProposalRanking()
								.getScQtProposal().getScQtSupplier()
								.getSmSupplier().getSupplierName());
				/*
				 * System.out.println("Mof No : " +
				 * getManagedBean().getScQtProposalRanking()
				 * .getScQtProposal().getScQtSupplier()
				 * .getSmSupplier().getSmMofAccounts().get(0).getMofNo());
				 */

				getManagedBean().setSmMofAccount(new SmMofAccount());
				getManagedBean().getSmMofAccount().setMofNo("MOF000001");

				getManagedBean().setQt(new ScQt());

				IQTDetailSO QtDetailsSO = BusinessDelegate.getInstance()
						.getServiceObject(MODULE_NAME, IQTDetailSO.class);
				QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();

				tenderReqDTO.setQtTndrNo("qt123");

				QuationTenderResDTO tenderResDTO = new QuationTenderResDTO();

				tenderResDTO = QtDetailsSO.retriveQtByQtNumber(tenderReqDTO);

				getManagedBean().getQt().setFileNo(
						tenderResDTO.getQtDetail().getFileNo());

			} else {
				getManagedBean().setQt(new ScQt());

				IQTDetailSO QtDetailsSO = BusinessDelegate.getInstance()
						.getServiceObject(MODULE_NAME, IQTDetailSO.class);
				System.out.println("manga Details Object" + QtDetailsSO);
				QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();

				tenderReqDTO.setQtTndrNo("qt123");

				QuationTenderResDTO tenderResDTO = new QuationTenderResDTO();

				tenderResDTO = QtDetailsSO.retriveQtByQtNumber(tenderReqDTO);

				System.out.println("====tenderResDTO.getQtDetail()."
						+ tenderResDTO.getQtDetail().getQtNo());
				getManagedBean().getQt().setFileNo(
						tenderResDTO.getQtDetail().getFileNo());

				getManagedBean().setScQtProposalRanking(scQtProposalRanking);

				getManagedBean().getScLoi().setSupplierName(
						getManagedBean().getScQtProposalRanking()
								.getScQtProposal().getScQtSupplier()
								.getSmSupplier().getSupplierName());

				/*
				 * System.out.println("Mof No : " +
				 * getManagedBean().getScQtProposalRanking()
				 * .getScQtProposal().getScQtSupplier()
				 * .getSmSupplier().getSmMofAccounts().get(0).getMofNo());
				 */

				SmSupplierBranch branch = new SmSupplierBranch();
				SmSupplierBranch branch1 = new SmSupplierBranch();
				branch.setBranchName("HQ");
				branch1.setBranchName("SET");
				bList.add(branch);
				bList.add(branch1);
				getManagedBean().setBranchsList(bList);

				branch.setSmAppl(getManagedBean().getScQtProposalRanking()
						.getScQtProposal().getScQtSupplier().getSmSupplier()
						.getSmAppl());

				PmUser user = new FacesScopes().getUserSessionBean();
				getManagedBean().setPmUser(user);
				getManagedBean().getScLoi().setOfficierName(user.getUserName());
				getManagedBean().getScLoi().setOfficerDesignation(
						user.getDesignation());
				addressSO = BusinessDelegate.getInstance().getServiceObject(
						"pm", IAddressSO.class);
				AddressReqDTO addReqDTO = new AddressReqDTO();
				addReqDTO.setUserId(user.getUserId());
				addReqDTO.setAddressType(AddressType.BILLING_ADDRESS.value());

				AddressResDTO addResDTO = addressSO
						.retrieveAddressTypeList(addReqDTO);

				List<PmAddressType> addList = addResDTO.getAddressTypeList();
				addList.get(0).getPmAddress().getAddress1();
				addList.get(0).getPmAddress().getAddress2();
				addList.get(0).getPmAddress().getAddress3();
				addList.get(0).getPmAddress().getAddress4();

				getManagedBean().setAddress(
						addList.get(0).getPmAddress().getAddress1() + ","
								+ addList.get(0).getPmAddress().getAddress2()
								+ ","
								+ addList.get(0).getPmAddress().getAddress3()
								+ ","
								+ addList.get(0).getPmAddress().getAddress4());
				System.out
						.println("Address : " + getManagedBean().getAddress());
				getManagedBean().getScLoi().setOfficerAddress(
						addList.get(0).getPmAddress().getAddress1());
				getManagedBean().getScLoi().setOfficerFaxNo(
						addList.get(0).getPmAddress().getFaxNo());
				getManagedBean().getScLoi().setOfficerPhoneNo(
						addList.get(0).getPmAddress().getPhoneNo());

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return QTConstants.LOI_PAGE;
	}
	/*
	 * public String createLOIAction(ScQtProposalRanking scQtProposalRanking) {
	 * debugLogger("createLOIAction"); IAddressSO addressSO;
	 * 
	 * List<SmSupplierBranch> bList = new ArrayList<SmSupplierBranch>(); try {
	 * 
	 * getManagedBean().setScQtProposalRanking(scQtProposalRanking);
	 * 
	 * getManagedBean().getScLoi().setSupplierName(
	 * getManagedBean().getScQtProposalRanking().getScQtProposal()
	 * .getScQtSupplier().getSmSupplier() .getSupplierName());
	 * 
	 * 
	 * SmSupplierBranch branch = new SmSupplierBranch(); SmSupplierBranch
	 * branch1 = new SmSupplierBranch(); branch.setBranchName("HQ");
	 * branch1.setBranchName("SET"); bList.add(branch); bList.add(branch1);
	 * getManagedBean().setBranchsList(bList);
	 * 
	 * branch.setSmAppl(getManagedBean().getScQtProposalRanking().getScQtProposal
	 * () .getScQtSupplier().getSmSupplier().getSmAppl());
	 * 
	 * if (getManagedBean().getScQtSpec() != null) {
	 * getManagedBean().setScQtSpec(new ScQtSpec()); IQTSpecSO specSO =
	 * BusinessDelegate.getInstance() .getServiceObject(MODULE_NAME,
	 * IQTSpecSO.class); SpecificationReqDTO reqDTO = new SpecificationReqDTO();
	 * SpecificationResDTO resDTO = null; reqDTO.setQtId(146L); resDTO =
	 * specSO.retrieveSpecDetails(reqDTO); if (resDTO.getScQtSpec() != null) {
	 * 
	 * getManagedBean().setScQtSpec(new ScQtSpec()); //
	 * getManagedBean().getScQtSpec().setAwardType("L");
	 * getManagedBean().getScQtSpec().setFulfilmentType(
	 * resDTO.getScQtSpec().getFulfilmentType());
	 * System.out.println(" Fulfilment Type : " + getManagedBean().getScQtSpec()
	 * .getFulfilmentType()); } }
	 * 
	 * PmUser user = new FacesScopes().getUserSessionBean();
	 * getManagedBean().setPmUser(user);
	 * getManagedBean().getScLoi().setOfficierName(user.getUserName());
	 * getManagedBean().getScLoi().setOfficerDesignation(
	 * user.getDesignation());
	 * 
	 * addressSO = BusinessDelegate.getInstance().getServiceObject("pm",
	 * IAddressSO.class); AddressReqDTO reqDTO = new AddressReqDTO();
	 * reqDTO.setUserId(user.getUserId());
	 * reqDTO.setAddressType(AddressType.BILLING_ADDRESS.value());
	 * 
	 * AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);
	 * 
	 * List<PmAddressType> list = resDTO.getAddressTypeList();
	 * list.get(0).getPmAddress().getAddress1();
	 * list.get(0).getPmAddress().getAddress2();
	 * list.get(0).getPmAddress().getAddress3();
	 * list.get(0).getPmAddress().getAddress4();
	 * 
	 * getManagedBean().setAddress( list.get(0).getPmAddress().getAddress1() +
	 * "," + list.get(0).getPmAddress().getAddress2() + "," +
	 * list.get(0).getPmAddress().getAddress3() + "," +
	 * list.get(0).getPmAddress().getAddress4());
	 * System.out.println("Address : " + getManagedBean().getAddress());
	 * getManagedBean().getScLoi().setOfficerAddress(
	 * list.get(0).getPmAddress().getAddress1());
	 * getManagedBean().getScLoi().setOfficerFaxNo(
	 * list.get(0).getPmAddress().getFaxNo());
	 * getManagedBean().getScLoi().setOfficerPhoneNo(
	 * list.get(0).getPmAddress().getPhoneNo());
	 * 
	 * getManagedBean().setScQtCriteria(new ScQtCriteria());
	 * 
	 * getManagedBean().getScQtCriteria().setMultiLocality(true);
	 * 
	 * if (getManagedBean().getScQtCriteria().isMultiLocality()) {
	 * getManagedBean().setStatus("1"); } else {
	 * getManagedBean().setStatus("0"); } } catch (Exception e) {
	 * e.printStackTrace(); }
	 * 
	 * return QTConstants.LOI_PAGE; }
	 */
}
