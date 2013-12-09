package my.ep.web.backingbean.ss.loiloa;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.ss.quationtender.IQTAwardSO;
import my.ep.biz.service.ss.quationtender.IQTSpecSO;
import my.ep.dto.ss.quationtender.AwardSupplierReqDTO;
import my.ep.dto.ss.quationtender.AwardSupplierResDTO;
import my.ep.dto.ss.specification.SpecificationReqDTO;
import my.ep.dto.ss.specification.SpecificationResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.ss.quationtender.ScLoa;
import my.ep.persistence.entity.ss.quationtender.ScLoi;
import my.ep.persistence.entity.ss.quationtender.ScQt;
import my.ep.persistence.entity.ss.quationtender.ScQtProposalRanking;
import my.ep.persistence.entity.ss.quationtender.ScQtSpec;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ss.quatationtender.QTDetailBackingBean;
import my.ep.web.managedbean.ss.loiloa.LOILOADetailManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * @author Viswanath
 * 
 */
@ManagedBean(name = "loiLoaDetailBackingBean")
@RequestScoped
public class LOILOADetailsBackingBean extends
		BaseBackingBean<LOILOADetailManagedBean> {
	public static Logger log = EpLogger
			.getLogger(LOILOADetailsBackingBean.class);

	private static final String MODULE_NAME = "ss";

	public LOILOADetailsBackingBean() {
		log.info("inside LOILOADetailsBackingBean constructor");
		retrieveLoiLoaDetailsList();

		retrieveLOILOAList();

		retrieveRecommendedSupplierList();

		QTDetailBackingBean qtDetailBB = new QTDetailBackingBean();
		ScQt scQt = new ScQt();

		scQt.setMinistryOrgProfile(new PmOrgProfile());
		scQt.setDepartmentOrgProfile(new PmOrgProfile());
		scQt.setPmOrgProfile(new PmOrgProfile());

		qtDetailBB.getManagedBean().setQt(scQt);
		
		qtDetailBB.getManagedBean().getQt().setQtId(146L);
		
		
		System.out.println("Quotation No : " + qtDetailBB.getManagedBean().getQt().getQtNo());

		/* List<SmSupplier> list = new ArrayList<SmSupplier>(); */

		SmAppl smAppl = new SmAppl();
		smAppl.setApplId(22);

	}

	/**
	 * retrieving Recommended Supplier List
	 */
	private void retrieveRecommendedSupplierList() {
		debugLogger("retrieveRecommendedSupplierList");

/*		
		getManagedBean().setSmMofAccount(new SmMofAccount());
		getManagedBean().getSmMofAccount().setMofNo("MOF000001");
*/
		getManagedBean().setScLoi(new ScLoi());

		getManagedBean().setScLoa(new ScLoa());

		/*
		 * List<SmSupplier> list = new ArrayList<SmSupplier>();
		 * 
		 * if (getManagedBean().getScQtProposal() == null) {
		 * getManagedBean().setScQtProposal(new ScQtProposal()); IQTProposalSO
		 * proposalSO = BusinessDelegate.getInstance()
		 * .getServiceObject(MODULE_NAME, IQTProposalSO.class); ProposalReqDTO
		 * reqDTO = new ProposalReqDTO(); ProposalResDTO resDTO = null;
		 * reqDTO.setQtSupplierId(1L); resDTO =
		 * proposalSO.retrieveProposalDtlByQtSupplierId(reqDTO);
		 * resDTO.getScQtProposal().setScQtProposalChecklists(null);
		 * 
		 * SmSupplier smSupplier = resDTO.getScQtProposal().getScQtSupplier()
		 * .getSmSupplier(); list.add(smSupplier);
		 * 
		 * this.getManagedBean().setSmSupplierList(list);
		 * 
		 * 
		 * if (resDTO.getScQtProposal() != null) {
		 * getManagedBean().setScQtProposal(resDTO.getScQtProposal());
		 * 
		 * }
		 * 
		 * }
		 */

	}

	@PostConstruct
	public void init() {

	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	/**
	 * retrieving LOI LOA Details List
	 */
	private void retrieveLoiLoaDetailsList() {
		debugLogger("retrieveLoiLoaDetailsList");
		if (getManagedBean().getScQtSpec() == null) {
			getManagedBean().setScQtSpec(new ScQtSpec());
			IQTSpecSO specSO = BusinessDelegate.getInstance().getServiceObject(
					MODULE_NAME, IQTSpecSO.class);
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			SpecificationResDTO resDTO = null;
			reqDTO.setQtId(146L);
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

	}

	/**
	 * retrieving LOI LOA List
	 */
	private void retrieveLOILOAList() {
		debugLogger("retrieveLOILOAList");

		IQTAwardSO iqtAwardSO = null;
		try {
			iqtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					MODULE_NAME, IQTAwardSO.class);
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setQtId(146L);
			AwardSupplierResDTO resDTO = iqtAwardSO.retrieveLOILOAList(reqDTO);

			List<ScQtProposalRanking> list = resDTO
					.getProposalRankingResultBean().getResultList();
			System.out.println("list: " + list.size());
			getManagedBean().setScQtProposalRankingList(list);
			/*System.out.println("Mof No : "
					+ getManagedBean().getScQtProposalRankingList().get(0)
							.getScQtProposal().getScQtSupplier()
							.getSmSupplier().getSmMofAccounts().get(0)
							.getMofNo());*/

		} catch (Exception ex) {
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							ex.toString(), ex.getMessage()));
		}
	}

	/*
	 * public String createLOAAction(SmSupplier smSupplier ) {
	 * debugLogger("createLOAAction");
	 * 
	 * getManagedBean().setSmSupplier(new SmSupplier());
	 * getManagedBean().getSmSupplier().setSupplierName("Award Type");
	 * smSupplier
	 * .setSupplierName(getManagedBean().getSmSupplier().getSupplierName());
	 * 
	 * getManagedBean().setSmMofAccount(new SmMofAccount());
	 * getManagedBean().getSmMofAccount().setMofNo("MOF000001");
	 * 
	 * getManagedBean().setScLoa(new ScLoa());
	 * getManagedBean().getScLoa().setLoaDuration(14);
	 * 
	 * if (getManagedBean().getScQtSpec() == null) {
	 * getManagedBean().setScQtSpec(new ScQtSpec()); IQTSpecSO specSO =
	 * BusinessDelegate.getInstance().getServiceObject( MODULE_NAME,
	 * IQTSpecSO.class); SpecificationReqDTO reqDTO = new SpecificationReqDTO();
	 * SpecificationResDTO resDTO = null; reqDTO.setQtId(144L); resDTO =
	 * specSO.retrieveSpecDetails(reqDTO); if (resDTO.getScQtSpec() != null) {
	 * 
	 * getManagedBean().setScQtSpec(new ScQtSpec()); //
	 * getManagedBean().getScQtSpec().setAwardType("L");
	 * getManagedBean().getScQtSpec().setAwardType(
	 * resDTO.getScQtSpec().getAwardType()); System.out.println(" Award Type : "
	 * + getManagedBean().getScQtSpec().getAwardType()); } }
	 * 
	 * QTDetailBackingBean qtDetailBB = new QTDetailBackingBean(); ScQt scQt =
	 * new ScQt();
	 * 
	 * scQt.setMinistryOrgProfile(new PmOrgProfile());
	 * scQt.setDepartmentOrgProfile(new PmOrgProfile());
	 * scQt.setPmOrgProfile(new PmOrgProfile());
	 * 
	 * qtDetailBB.getManagedBean().setQt(scQt);
	 * 
	 * PmUser user = new FacesScopes().getUserSessionBean();
	 * getManagedBean().setPmUser(user);
	 * 
	 * System.out.println(" User Name : " +
	 * getManagedBean().getPmUser().getUserName());
	 * //System.out.println("Fax : " +
	 * getManagedBean().getPmUser().getPmUserAddresses
	 * ().get(0).getPmAddress().getFaxNo());
	 * 
	 * 
	 * return QTConstants.LOA_PAGE + "?faces-redirect=true";
	 * 
	 * }
	 */
}
