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
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.ss.quationtender.ScLoa;
import my.ep.persistence.entity.ss.quationtender.ScQt;
import my.ep.persistence.entity.ss.quationtender.ScQtProposalRanking;
import my.ep.persistence.entity.ss.quationtender.ScQtSpec;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.RoleCode;
import my.ep.type.ss.LoiLoaDocType;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.ss.loiloa.LOADetailManagedBean;
import my.ep.web.ss.quationtender.common.QTConstants;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.event.TabChangeEvent;

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
		getManagedBean().setScLoa(new ScLoa());
	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	@PostConstruct
	public void init() {
/*		if (getManagedBean().getQt() == null) {
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

		}
*/
	}

	public void onTabChangeAction(TabChangeEvent event) {
		String tabTitle = event.getTab().getTitle();

		if (QTConstants.SC_LOA_TAB.equalsIgnoreCase(tabTitle)) {
		} else if (QTConstants.SC_PERFORMANCE_BOND_TAB
				.equalsIgnoreCase(tabTitle)) {
		} else if (QTConstants.SC_PAYMENT_TAB.equalsIgnoreCase(tabTitle)) {
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

	public String createLOAAction(ScQtProposalRanking scQtProposalRanking, String docType) {
		debugLogger("createLOAAction");
		IQTAwardSO iqtAwardSO = null;
		IAddressSO addressSO;
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
			
			AwardSupplierResDTO resDTO = iqtAwardSO.retrieveLOADtl(reqDTO);
			
			ScLoa scLoa = resDTO.getScLoa();
			
			if (scLoa != null) {
				getManagedBean().setScLoa(scLoa);
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

				getManagedBean().getScLoa().setSupplierName(
						getManagedBean().getScQtProposalRanking().getScQtProposal()
								.getScQtSupplier().getSmSupplier()
								.getSupplierName());

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

				if (getManagedBean().getScQtSpec() != null) {
					getManagedBean().setScQtSpec(new ScQtSpec());
					IQTSpecSO specSO = BusinessDelegate.getInstance()
							.getServiceObject(MODULE_NAME, IQTSpecSO.class);
					SpecificationReqDTO specReqDTO = new SpecificationReqDTO();
					SpecificationResDTO specResDTO = null;
					specReqDTO.setQtId(146L);
					specResDTO = specSO.retrieveSpecDetails(specReqDTO);
					if (specResDTO.getScQtSpec() != null) {

						getManagedBean().setScQtSpec(new ScQtSpec());
						getManagedBean().getScQtSpec().setFulfilmentType(
								specResDTO.getScQtSpec().getFulfilmentType());
						getManagedBean().getScQtSpec().setContractDuration(
								specResDTO.getScQtSpec().getContractDuration());
					}
				}

				PmUser user = new FacesScopes().getUserSessionBean();
				getManagedBean().setPmUser(user);
				getManagedBean().getScLoa().setOfficierName(user.getUserName());
				getManagedBean().getScLoa().setOfficerDesignation(
						user.getDesignation());

				addressSO = BusinessDelegate.getInstance().getServiceObject("pm",
						IAddressSO.class);
				AddressReqDTO addReqDTO = new AddressReqDTO();
				addReqDTO.setUserId(user.getUserId());
				addReqDTO.setAddressType(AddressType.BILLING_ADDRESS.value());

				AddressResDTO addResDTO = addressSO.retrieveAddressTypeList(addReqDTO);

				List<PmAddressType> addList = addResDTO.getAddressTypeList();
				addList.get(0).getPmAddress().getAddress1();
				addList.get(0).getPmAddress().getAddress2();
				addList.get(0).getPmAddress().getAddress3();
				addList.get(0).getPmAddress().getAddress4();

				getManagedBean().setAddress(
						addList.get(0).getPmAddress().getAddress1() + ","
								+ addList.get(0).getPmAddress().getAddress2() + ","
								+ addList.get(0).getPmAddress().getAddress3() + ","
								+ addList.get(0).getPmAddress().getAddress4());

				getManagedBean().getScLoa().setOfficerAddress(
						addList.get(0).getPmAddress().getAddress1());
				getManagedBean().getScLoa().setOfficerFaxNo(
						addList.get(0).getPmAddress().getFaxNo());
				getManagedBean().getScLoa().setOfficerPhoneNo(
						addList.get(0).getPmAddress().getPhoneNo());
				getManagedBean().getScLoa().setOfficerEmail(
						addList.get(0).getPmAddress().getEmail());
			}


		} catch (Exception e) {
			e.printStackTrace();
		}

		return QTConstants.LOA_PAGE;
	}
}
