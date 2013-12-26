package my.ep.web.backingbean.sm;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.biz.service.sm.reviewApplication.IReviewApplicationSO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.dto.sm.reviewApplication.ReviewApplicationReqDTO;
import my.ep.dto.sm.reviewApplication.ReviewApplicationResDTO;
import my.ep.persistence.entity.pm.common.PmBank;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplSectionReview;
import my.ep.persistence.entity.sm.SmAssociateCompany;
import my.ep.persistence.entity.sm.SmChequeSignatory;
import my.ep.persistence.entity.sm.SmContractExperience;
import my.ep.persistence.entity.sm.SmEquityCompany;
import my.ep.persistence.entity.sm.SmFacility;
import my.ep.persistence.entity.sm.SmMachineDtl;
import my.ep.persistence.entity.sm.SmManufacturingDtl;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.persistence.entity.sm.SmProjectExperience;
import my.ep.persistence.entity.sm.SmStaffAcademic;
import my.ep.persistence.entity.sm.SmStaffTotal;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.persistence.entity.sm.SmSupplierCert;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.SmApplicationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.event.TabChangeEvent;

@ManagedBean(name = "backingBean")
@RequestScoped
public class SmApplicationBackingBean extends
		BaseBackingBean<SmApplicationManagedBean> {

	public static Logger log = Logger.getLogger(SmApplicationBackingBean.class);

	private int currentTab;
	private int outputTab;

	public SmApplicationBackingBean() {

	}

	public String proceedToApplication(String applicationType) {
		return proceedToApplication(applicationType, Boolean.FALSE);
	}

	public String proceedToApplication(String applicationType, boolean fromBpm) {
		System.out
				.println("**** ENTER Create Application Type Of  *************"
						+ applicationType + "Application type = >>"
						+ getManagedBean().getSmAppl().getSupplierType());
		IMofRegistrationSO mofRegistrationSO = null;

		try {
			mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							IMofRegistrationSO.class);
			MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
			SmSupplier smApplSupplier = retriveSupplierById((long) 3)
					.getSmSupplier();
			if (smApplSupplier == null) {
				System.out.println(" ******* NO RECORD FOUND IN DB *****");
				smApplSupplier = new SmSupplier();
				smApplSupplier.setSupplierName("My Sample Supplier");
				smApplSupplier.setBusinessType("1");
				smApplSupplier.setRegNo("12345");
				smApplSupplier.setEstablishDate(new Date());
				smApplSupplier.setWebSite("www.samplecompany.com");
			}
			System.out.println("Retrieved Value ==="
					+ smApplSupplier.getSupplierName() + " ==== tax num == "
					+ smApplSupplier.getIncomeTaxNo() + " === EST DATE ==== "
					+ smApplSupplier.getEstablishDate());
			System.out.println("******** SIZE ********** "
					+ smApplSupplier.getSmAppls().size());
			SmAppl smAppl = new SmAppl();
			// TO Use the private generate application num
			if (applicationType.equalsIgnoreCase("N")) {
				smAppl.setApplNo("KN-20102013-1111");
			} else if (applicationType.equalsIgnoreCase("B")) {
				smAppl.setApplNo("KB-25102013-2222");
			} else if (applicationType.equalsIgnoreCase("U")) {
				smAppl.setApplNo(" KU-28102013-3333");
			} else if (applicationType.equalsIgnoreCase("A")) {
				smAppl.setApplNo(" KA-28102013-4444");
			} else if (applicationType.equalsIgnoreCase("R")) {
				smAppl.setApplNo(" KR-31102013-5555");
			} else if (applicationType.equalsIgnoreCase("C")) {
				smAppl.setApplNo(" KC-01112013-6666");
			} else {
				smAppl.setApplNo("UNKNOWNAPPL");
			}
			smApplSupplier.setEstablishDate(new Date());
			smAppl.setSmSupplier(smApplSupplier);
			if (!applicationType.equalsIgnoreCase("N")) {
				// mofRegAppl.setSmAddress(smAddress);
				// 1- For Contractor, 2 - For Consultant
				smAppl.setSupplierType("2");
			} else {
				smAppl.setSupplierType(getManagedBean().getSmAppl()
						.getSupplierType());
			}
			smAppl.setApplType(applicationType);
			// TODO Set default not null values -- Start insert appl ---
			smAppl.setStatusId(20001);
			// sm.setSmAddress(new SmAddress());
			// SmAddress smAddress = setDefaultAddr();
			// getManagedBean().getSmSupplier().setSmAppl(mofRegAppl);
			smAppl.setCreatedBy(1l);
			if (smApplSupplier.getSmAppls().contains(smAppl)) {
				System.out
						.println(" *** Already existing record its a duplicate only update **** ");
				smApplSupplier.getSmAppls().set(
						smApplSupplier.getSmAppls().indexOf(smAppl), smAppl);
			} else {
				System.out
						.println("****** INSERT A FRESH RECORD OF APPL TO SUUPPLIER ******** ");
				smApplSupplier.getSmAppls().add(smAppl);
			}
			if (applicationType.equals("N") && !fromBpm) {
				System.out
						.println(" **** START INSERTING THE NEW APPLN FOR MOF REG ****");
				reqDto.setSmSupplier(smApplSupplier);
				MofRegistrationResDTO smResDTO = new MofRegistrationResDTO();
				smResDTO = mofRegistrationSO.updateSupplierProfile(reqDto);
				smApplSupplier = smResDTO.getSmSupplier();
			}
			getManagedBean().setSmSupplier(smApplSupplier);
			// TODO Set default not null values -- End insert appl ---
			// TODo Remove hard code later
			if (getManagedBean().isApproverUser()
					|| getManagedBean().isPoUser()) {
				System.out.println("**** LOGIN IS PO or APPROVER *******");
				smAppl.setSupportingDocMode("O");
				smAppl.setSupplierType("2");
			}
			smAppl = defaultSmAppl(smAppl);
			smAppl.setSmStaffTotals(getStaffList(smAppl.getSupplierType()));
			smAppl.setCurrentActiveTab(0);
			getManagedBean().setSmAppl(smAppl);
			getManagedBean().setTabActiveIndex(0);
			getManagedBean().setOfficeFacility(new SmFacility());

			System.out
					.println(" **** APPLICATION TYPE **** " + applicationType);
			if (fromBpm) {
				getManagedBean().setMaxReviewCount(
						assignMaxReviewCount(applicationType));
				// TODO increase the count by 1 for if status is review after
				// site visit
				if (smAppl.getStatusId() == 11) {
					getManagedBean().setMaxReviewCount(
							getManagedBean().getMaxReviewCount() + 1);
				}
				if (smAppl.getSmApplSectionReviews() == null) {
					smAppl.setSmApplSectionReviews(new ArrayList<SmApplSectionReview>());
				}
				if (getManagedBean().isPoUser()) {
					smAppl.getSmApplSectionReviews().add(
							getManagedBean().getPoSecReview());
				}

				if (getManagedBean().isApproverUser()) {
					smAppl.getSmApplSectionReviews().add(
							getManagedBean().getApprvrSecReview());
				}
			}
			return setNavigationForAppl(applicationType);

		} catch (Exception e) {
			System.out.println(" **** Unknown Expection ******* ");
			e.printStackTrace();
			if (applicationType.equalsIgnoreCase("B")) {
				return SupplierManagementConst.DEFAULT_BUMI_APPLN;
			} else if (applicationType.equalsIgnoreCase("N")) {
				return SupplierManagementConst.DEFAULT_MOF_REG;
			} else if (applicationType.equalsIgnoreCase("U")) {
				return SupplierManagementConst.DEFAULT_UPDATE_MOF_APPLN;
			} else if (applicationType.equalsIgnoreCase("A")) {
				return SupplierManagementConst.DEFAULT_ADDTNLCAT_MOF_APPLN;
			} else if (applicationType.equalsIgnoreCase("R")) {
				return SupplierManagementConst.DEFAULT_RENEW_MOF_APPLN;
			} else if (applicationType.equalsIgnoreCase("C")) {
				return SupplierManagementConst.DEFAULT_CANCEL_MOF_APPLN;
			} else {
				return null;
			}

		} finally {
			mofRegistrationSO = null;
		}
	}

	// TODO Remove the method added only for demo purpose
	public void assignTaskListSampleForDemo(String bpmApplType) {

		IMofRegistrationSO mofRegistrationSO = null;

		try {
			mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							IMofRegistrationSO.class);
			MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
			SmSupplier smApplSupplier = retriveSupplierById((long) 3)
					.getSmSupplier();
			if (smApplSupplier == null) {
				smApplSupplier = new SmSupplier();
				smApplSupplier.setSupplierName("My Sample Supplier");
				smApplSupplier.setBusinessType("1");
				smApplSupplier.setRegNo("12345");
				smApplSupplier.setEstablishDate(new Date());
				smApplSupplier.setWebSite("www.samplecompany.com");
			}

			SmAppl bpmSmAppl = smApplSupplier.getSmAppl();
			if (bpmSmAppl == null) {
				bpmSmAppl = new SmAppl();
			}
			if (bpmApplType.equalsIgnoreCase("N")) {
				bpmSmAppl.setApplNo("KN-20102013-1111");
			} else if (bpmApplType.equalsIgnoreCase("B")) {
				bpmSmAppl.setApplNo("KB-25102013-2222");
			} else if (bpmApplType.equalsIgnoreCase("U")) {
				bpmSmAppl.setApplNo(" KU-28102013-3333");
			} else if (bpmApplType.equalsIgnoreCase("A")) {
				bpmSmAppl.setApplNo(" KA-28102013-4444");
			} else if (bpmApplType.equalsIgnoreCase("R")) {
				bpmSmAppl.setApplNo(" KR-31102013-5555");
			} else if (bpmApplType.equalsIgnoreCase("C")) {
				bpmSmAppl.setApplNo(" KC-01112013-6666");
			} else {
				bpmSmAppl.setApplNo("UNKNOWNAPPL");
			}

			bpmSmAppl.setSmSupplier(smApplSupplier);
			// mofRegAppl.setSmAddress(smAddress);
			// 1- For Contractor, 2 - For Consultant
			bpmSmAppl.setSupplierType("2");
			bpmSmAppl.setApplType(bpmApplType);
			// TODO Set default not null values -- Start insert appl ---
			bpmSmAppl.setStatusId(20001);
			// sm.setSmAddress(new SmAddress());
			// SmAddress smAddress = setDefaultAddr();
			// getManagedBean().getSmSupplier().setSmAppl(mofRegAppl);
			bpmSmAppl.setCreatedBy(1l);
			boolean isNewAppln = true;
			for (int i = 0; i < smApplSupplier.getSmAppls().size(); i++) {
				SmAppl app = smApplSupplier.getSmAppls().get(i);
				if (bpmSmAppl.getApplId() == app.getApplId()) {
					System.out.println("****** Update Application******** ");
					isNewAppln = false;
					smApplSupplier.getSmAppls().set(i, bpmSmAppl);
					break;
				}
			}
			if (isNewAppln) {
				System.out
						.println("****** INSERT A FRESH RECORD OF APPL TO SUUPPLIER ******** ");
				smApplSupplier.getSmAppls().add(bpmSmAppl);
			}

			getManagedBean().setSmSupplier(smApplSupplier);
			// TODO Set default not null values -- End insert appl ---

			bpmSmAppl = defaultSmAppl(bpmSmAppl);
			bpmSmAppl
					.setSmStaffTotals(getStaffList(bpmSmAppl.getSupplierType()));
			getManagedBean().setSmAppl(bpmSmAppl);
			getManagedBean().setTabActiveIndex(0);
			getManagedBean().setOfficeFacility(new SmFacility());
			// TODo Remove hard code later
			if (getManagedBean().isApproverUser()
					|| getManagedBean().isPoUser()) {
				System.out.println("**** LOGIN IS PO or APPROVER *******");
				getManagedBean().getSmAppl().setSupportingDocMode("O");
			}

		} catch (Exception e) {
			System.out.println(" **** Unknown Expection ******* ");
			e.printStackTrace();

		} finally {
			mofRegistrationSO = null;
		}
	}

	public String deletePendingMofApplication() {

		try {
			SmSupplier smSupplier = retriveSupplierById((long) 3)
					.getSmSupplier();
			if (smSupplier == null) {
				smSupplier = new SmSupplier();
				smSupplier.setSupplierName("My Sample Supplier");
				smSupplier.setBusinessType("1");
				smSupplier.setRegNo("12345");
				smSupplier.setEstablishDate(new Date());
				smSupplier.setWebSite("www.samplecompany.com");
			}
			// TODO the smappl is supposed to be get the latest application from
			// sm supplier
			SmAppl smAppl = new SmAppl();
			smAppl.setApplNo("KB-25102013-1234");
			smAppl.setStatusId(2);
			smAppl.setApplType("B");

			getManagedBean().setSmSupplier(smSupplier);
			// TODO Set default not null values -- End insert appl ---

			smAppl = defaultSmAppl(smAppl);
			getManagedBean().setSmAppl(smAppl);
			return SupplierManagementConst.PROCEED_DEL_PENDING_MOF_APPLN;

		} catch (Exception e) {
			System.out.println(" **** Unknown Expection ******* ");
			e.printStackTrace();
			return SupplierManagementConst.DEFAULT_DEL_PENDING_MOF_APPLN;
		}
	}

	public void updateSmAppl() throws Exception {
		try {
			System.out.println("**** ENTER ACTION METHOD BEAN *************"
					+ currentTab + "  " + outputTab);
			// TODO Un comment during iteration2
			// boolean isNewAppln = true;
			// for (int i = 0; i < getManagedBean().getSmSupplier().getSmAppls()
			// .size(); i++) {
			// SmAppl app = getManagedBean().getSmSupplier().getSmAppls()
			// .get(i);
			// if (getManagedBean().getSmAppl().getApplId() == app.getApplId())
			// {
			// System.out.println("****** Update Application******** ");
			// isNewAppln = false;
			// getManagedBean().getSmSupplier().getSmAppls()
			// .set(i, getManagedBean().getSmAppl());
			// break;
			// }
			// }
			// if (isNewAppln) {
			// System.out
			// .println("****** INSERT A FRESH RECORD OF APPL TO SUUPPLIER ******** ");
			// getManagedBean().getSmSupplier().getSmAppls()
			// .add(getManagedBean().getSmAppl());
			// }
			// TODO to be confirmed whether reqd to save the appln as draft
			if (getManagedBean().getSmAppl().getCurrentActiveTab() < outputTab) {
				getManagedBean().getSmAppl().setCurrentActiveTab(outputTab);
			} else {
				System.out.println(" ****** REMAIN PREV VALUE ********");
			}
			getManagedBean().setTabActiveIndex(outputTab);
			// return SupplierManagementConst.PROCEED_MOF_REG;

		} catch (Exception e) {
			System.out.println("this is error");
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Sample error message",
							"PrimeFaces makes no mistakes"));
			getManagedBean().setTabActiveIndex(currentTab);
			// return SupplierManagementConst.PROCEED_MOF_REG;
		}

	}

	public void createNewApplication() throws Exception {
		try {
			System.out.println("**** New BEAN  *************"
					+ getManagedBean().getSmSupplier().getSmAppls().size());
			System.out.println("**** Latest Appl Id   *************"
					+ getManagedBean().getSmSupplier().getSmAppls().get(0)
							.getApplId());
			SmAppl app = getManagedBean().getSmSupplier().getSmAppls().get(0);
			System.out.println(" ******* New Objectr == >> " + app.getApplId()
					+ " Name == " + app.getApplNo());
			getManagedBean().setSmAppl(app);
			// TODO to be confirmed whether reqd to save the appln as draft
			getManagedBean().setTabActiveIndex(outputTab);

		} catch (Exception e) {
			System.out.println("this is error");
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Sample error message",
							"PrimeFaces makes no mistakes"));
			getManagedBean().setTabActiveIndex(currentTab);
		}

	}

	public void setTabValue(int currentTab, int outputTab) {
		System.out.println(" ***** Tab Values *** " + currentTab + outputTab);
		this.currentTab = currentTab;
		this.outputTab = outputTab;
		getManagedBean().setTabActiveIndex(outputTab);
	}

	public void assignReviewSupportingNav(String navType) {
		int currentPage = getManagedBean().getTabActiveIndex();
		try {
			this.currentTab = currentPage;
			System.out
					.println("********** ENTER THE PRE RENDER FOR SUPP DOXS ***** "
							+ currentPage + "  , " + navType);
			getManagedBean().getSmAppl().setSupportingDocMode("O");
			if (navType.equalsIgnoreCase("next")) {
				System.out.println("** MY NEXT PAGE ***");
				this.outputTab = currentPage + 1;
			} else if (navType.equalsIgnoreCase("prev")) {
				System.out.println("** MY PREV PAGE ***");
				this.outputTab = currentPage - 1;
			} else {
				this.outputTab = currentPage;
			}
		} catch (Exception e) {
			this.currentTab = currentPage;
			this.outputTab = currentPage;
		}
	}

	public void officeFacilitySelected(AjaxBehaviorEvent e) {
		System.out.println(" ******* On Change Office Facility ***** "
				+ getManagedBean().getOfficeFacility().getOwnershipType());
		System.out.println(" **** Values In loop *********"
				+ getManagedBean().getOfficeFacility().getRentalRate());
	}

	public void svcSelected(AjaxBehaviorEvent e) {
		System.out.println(" ******* On Change Office Facility ***** "
				+ getManagedBean().getPoSiteVisitType());

	}

	// -- Process Branches Start
	public void addOrUpdateBranchToSmAppl() {

		if (getManagedBean().getSmAppl().getSmSupplierBranches()
				.contains(getManagedBean().getSelectedBranch())) {

			System.out.println(" **** THIS IS AN UPDATE For Branches******"
					+ getManagedBean().getSelectedBranch().getSmAddress()
							.getPhoneNo());
		} else {
			getManagedBean().getSmAppl().getSmSupplierBranches()
					.add(getManagedBean().getSelectedBranch());
		}
	}

	public void assignSelBranch(SmSupplierBranch branchBean) {
		getManagedBean().setSelectedBranch(branchBean);
	}

	public void assignNewBranchBean() {
		getManagedBean().setSelectedBranch(new SmSupplierBranch());
		getManagedBean().getSelectedBranch().setSmAddress((setDefaultAddr()));
	}

	public void deleteSelectedBranches() {
		FacesContext.getCurrentInstance()
				.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Sample info message", "Delete Branch ::: "
										+ getManagedBean()
												.getSelectedBranches().size()));
		System.out.println("*** Delete Branches ******* "
				+ getManagedBean().getSelectedBranches().size());
	}

	// -- Process Branches End

	// -- Process Associated Companies Start
	public void addOrUpdateAssocToSmAppl() {

		if (getManagedBean().getSmAppl().getSmAssociateCompanies()
				.contains(getManagedBean().getSelectedAssocCompany())) {

			System.out.println(" **** THIS IS AN UPDATE Assoc Companies******");
		} else {
			getManagedBean().getSmAppl().getSmAssociateCompanies()
					.add(getManagedBean().getSelectedAssocCompany());
		}
	}

	public void assignSelAssoc(SmAssociateCompany assocBean) {
		getManagedBean().setSelectedAssocCompany(assocBean);
	}

	public void assignNewAssocBean() {
		getManagedBean().setSelectedAssocCompany(new SmAssociateCompany());
		getManagedBean().getSelectedAssocCompany().setSmAddress(
				(setDefaultAddr()));
	}

	// -- Process Associated Companies End

	// -- Process Certs Start
	public void addOrUpdateCertsToSmAppl() throws ParseException {
		if (getManagedBean().getSmAppl().getSmSupplierCerts()
				.contains(getManagedBean().getSelectedCert())) {

			System.out.println(" **** THIS IS AN UPDATE Certs******");
		} else {
			getManagedBean().getSmAppl().getSmSupplierCerts()
					.add(getManagedBean().getSelectedCert());
		}
	}

	public void assignSelCert(SmSupplierCert certBean) {
		getManagedBean().setSelectedCert(certBean);
	}

	public void assignNewCert() {
		getManagedBean().setSelectedCert(new SmSupplierCert());
	}

	// -- Process Certss End
	// -- Process Contract Projs Start
	public void addOrUpdateContractProjsToSmAppl() {
		if (getManagedBean().getSmAppl().getSmContractExperiences()
				.contains(getManagedBean().getSelectedContractProj())) {

			System.out
					.println(" **** THIS IS AN UPDATE FOR Contract Prjs******");
		} else {
			getManagedBean().getSmAppl().getSmContractExperiences()
					.add(getManagedBean().getSelectedContractProj());
		}
	}

	public void assignSelContractProj(SmContractExperience contractBean) {
		getManagedBean().setSelectedContractProj(contractBean);
	}

	public void assignNewContractProj() {
		getManagedBean().setSelectedContractProj(new SmContractExperience());
	}

	// -- Process Contract Projs End

	// -- Process Consultant Projs Start
	public void addOrUpdateConsultantProjsToSmAppl() {
		if (getManagedBean().getSmAppl().getSmProjectExperiences()
				.contains(getManagedBean().getSelectedConstProj())) {

			System.out
					.println(" **** THIS IS AN UPDATE FOR Consultant Prjs******");
		} else {
			getManagedBean().getSmAppl().getSmProjectExperiences()
					.add(getManagedBean().getSelectedConstProj());
		}
	}

	public void assignSelConsultantProj(SmProjectExperience consultantBean) {
		getManagedBean().setSelectedConstProj(consultantBean);
	}

	public void assignNewConsultantProj() {
		getManagedBean().setSelectedConstProj(new SmProjectExperience());
	}

	// -- Process Consultant Projs End

	// -- Process Equity Companies Start
	public void addOrUpdateEquityCompanyToSmAppl() {

		if (getManagedBean().getSmAppl().getSmEquityCompanies()
				.contains(getManagedBean().getSelectedEquityCompany())) {

			System.out
					.println(" **** THIS IS AN UPDATE Equity Companies******");
		} else {
			System.out.println("****** ADD EQUIRY ********* ");
			getManagedBean().getSmAppl().getSmEquityCompanies()
					.add(getManagedBean().getSelectedEquityCompany());
		}
	}

	public void assignSelEquiCompany(SmEquityCompany equiCompBean) {
		getManagedBean().setSelectedEquityCompany(equiCompBean);
	}

	public void assignNewEquiCompBean() {
		getManagedBean().setSelectedEquityCompany(new SmEquityCompany());
		getManagedBean().getSelectedEquityCompany().setSmAddress(
				(setDefaultAddr()));
	}

	// -- Process Equity Companies End

	// -- Process Category Codes Start

	public void changeParentCategory(AjaxBehaviorEvent e) {
		String selPmCatL1 = getManagedBean().getSelectedParentCat();
		if (selPmCatL1 == null) {
			PmCategoryL1 p = new PmCategoryL1();
			p.setPmCategoryL2s(new ArrayList<PmCategoryL2>());
			getManagedBean().getSelectedCategory().setPmCategoryL1(p);
		} else {
			for (PmCategoryL1 p : getManagedBean().getDefaultParentCatList()) {
				if (p.getCategoryL1Code().equalsIgnoreCase(selPmCatL1)) {
					if (p.getPmCategoryL2s() == null) {
						p.setPmCategoryL2s(new ArrayList<PmCategoryL2>());
					}
					getManagedBean().getSelectedCategory().setPmCategoryL1(p);

				}
			}
		}
		PmCategoryL2 p2 = new PmCategoryL2();
		p2.setPmCategoryL3s(new ArrayList<PmCategoryL3>());
		getManagedBean().getSelectedCategory().setPmCategoryL2(
				new PmCategoryL2());
		getManagedBean().getSelectedCategory().setPmCategoryL3(
				new PmCategoryL3());
		getManagedBean().setSelectedChildCat(null);
		getManagedBean().setSelectedSubChildCat(null);

	}

	public void changeChildCategory(AjaxBehaviorEvent e) {
		String selPmCatL2 = getManagedBean().getSelectedChildCat();
		if (selPmCatL2 == null) {
			PmCategoryL2 p2 = new PmCategoryL2();
			p2.setPmCategoryL3s(new ArrayList<PmCategoryL3>());
			getManagedBean().getSelectedCategory().setPmCategoryL2(p2);
		} else {
			for (PmCategoryL2 p2 : getManagedBean().getSelectedCategory()
					.getPmCategoryL1().getPmCategoryL2s()) {
				if (p2.getCategoryL2Code().equalsIgnoreCase(selPmCatL2)) {
					if (p2.getPmCategoryL3s() == null) {
						p2.setPmCategoryL3s(new ArrayList<PmCategoryL3>());
					}
					getManagedBean().getSelectedCategory().setPmCategoryL2(p2);

				}
			}
		}
		getManagedBean().getSelectedCategory().setPmCategoryL3(
				new PmCategoryL3());
		getManagedBean().setSelectedSubChildCat(null);

	}

	public void changeSubChildCategory(AjaxBehaviorEvent e) {
		String selPmCatL3 = getManagedBean().getSelectedSubChildCat();
		if (selPmCatL3 == null) {
			System.out.println(" --- Selected is a null --- ");
			PmCategoryL3 p3 = new PmCategoryL3();
			getManagedBean().getSelectedCategory().setPmCategoryL3(p3);
		} else {
			for (PmCategoryL3 p3 : getManagedBean().getSelectedCategory()
					.getPmCategoryL2().getPmCategoryL3s()) {
				if (p3.getCategoryL3Code().equalsIgnoreCase(selPmCatL3)) {
					getManagedBean().getSelectedCategory().setPmCategoryL3(p3);

				}
			}
		}

	}

	public void changeCatEntryModeValues(AjaxBehaviorEvent e) {
		System.out.println("**** ENTER THE CHANGE OF CATEGORY TYPE **** "
				+ getManagedBean().getSelectedCategory().getEntryMode());
		resetSelectedCategoryParams();

	}

	/**
	 * resetting all the values
	 * 
	 * @param e
	 */
	private void resetSelectedCategoryParams() {
		getManagedBean().setSelectedSubChildCat(null);
		getManagedBean().setSelectedChildCat(null);
		getManagedBean().setSelectedParentCat(null);
		PmCategoryL1 p = new PmCategoryL1();
		p.setPmCategoryL2s(new ArrayList<PmCategoryL2>());
		getManagedBean().getSelectedCategory().setPmCategoryL1(p);
		PmCategoryL2 p2 = new PmCategoryL2();
		p2.setPmCategoryL3s(new ArrayList<PmCategoryL3>());
		getManagedBean().getSelectedCategory().setPmCategoryL2(p2);
		PmCategoryL3 p3 = new PmCategoryL3();
		getManagedBean().getSelectedCategory().setPmCategoryL3(p3);
	}

	public void assignNewCategoryCode() {
		getManagedBean().setSelectedCategory(new SmSupplierCategory());
		resetSelectedCategoryParams();
	}

	public void saveSelectedCategory() {
		System.out.println(" ******* Save From Selection ****"
				+ getManagedBean().getSelectedCategory().getCategoryType());
		getManagedBean().getSmAppl().getSmSupplierCategories()
				.add(getManagedBean().getSelectedCategory());
	}

	// TODO needs many validations and SO retrieve the PMcat1,2,3 details - To
	// be impl in future
	public void saveEnteredCategory() {
		System.out.println(" ******* Save From Entry ****"
				+ getManagedBean().getSelectedCategory().getCategoryType());
		PmCategoryL1 p = new PmCategoryL1();
		p.setCategoryL1Code(getManagedBean().getSelectedParentCat());
		p.setCategoryName("ParentSample");
		getManagedBean().getSelectedCategory().setPmCategoryL1(p);
		PmCategoryL2 p2 = new PmCategoryL2();
		p2.setCategoryL2Code(getManagedBean().getSelectedChildCat());
		p2.setCategoryName("ChildSample");
		getManagedBean().getSelectedCategory().setPmCategoryL2(p2);
		PmCategoryL3 p3 = new PmCategoryL3();
		p3.setCategoryL3Code(getManagedBean().getSelectedSubChildCat());
		p3.setCategoryName("SubChildSample");
		getManagedBean().getSelectedCategory().setPmCategoryL3(p3);
		getManagedBean().getSmAppl().getSmSupplierCategories()
				.add(getManagedBean().getSelectedCategory());

	}

	// -- Process Category Codes End
	// factory start
	public void addOrUpdateFactoryToSmAppl() {

		if (getManagedBean().getSmAppl().getSmFacilities()
				.contains(getManagedBean().getSelectedFactoryFacility())) {

			System.out.println(" **** THIS IS AN UPDATE For Factoy******"
					+ getManagedBean().getSelectedFactoryFacility()
							.getSmAddress().getPhoneNo());
		} else {
			System.out.println("-- Before Save --"
					+ getManagedBean().getSelectedFactoryFacility()
							.getSmAddress().getAddress1());
			getManagedBean().getSmAppl().getSmFacilities()
					.add(getManagedBean().getSelectedFactoryFacility());
		}
	}

	public void assignSelFactory(SmFacility facilityBean) {
		getManagedBean().setSelectedFactoryFacility(facilityBean);
	}

	public void assignNewFactoryBean() {
		getManagedBean().setSelectedFactoryFacility(new SmFacility());
		getManagedBean().getSelectedFactoryFacility().setSmAddress(
				(setDefaultAddr()));
	}

	public void factoryOwnerTypeSel(AjaxBehaviorEvent e) {
		System.out.println("Selected Owner type === "
				+ getManagedBean().getSelectedFactoryFacility()
						.getOwnershipType());
	}

	// factory end

	// manufacturer start
	public void addOrUpdateManufacturerToSmAppl() {

		if (getManagedBean().getSmAppl().getSmManufacturingDtls()
				.contains(getManagedBean().getSelectedManufacturer())) {

			System.out.println(" **** THIS IS AN UPDATE For manufacturer******"
					+ getManagedBean().getSelectedManufacturer().getBrand());
		} else {

			getManagedBean().getSmAppl().getSmManufacturingDtls()
					.add(getManagedBean().getSelectedManufacturer());
		}
	}

	public void assignSelManufacturer(SmManufacturingDtl manBean) {
		getManagedBean().setSelectedManufacturer(manBean);
	}

	public void assignNewManufacturerBean() {
		getManagedBean().setSelectedManufacturer(new SmManufacturingDtl());
	}

	// manufacturer end

	// machinery start
	public void addOrUpdateMachineToSmAppl() {

		if (getManagedBean().getSmAppl().getSmMachineDtls()
				.contains(getManagedBean().getSelectedMachine())) {

			System.out.println(" **** THIS IS AN UPDATE For machinery ******"
					+ getManagedBean().getSelectedMachine().getBrandName());
		} else {

			getManagedBean().getSmAppl().getSmMachineDtls()
					.add(getManagedBean().getSelectedMachine());
		}
	}

	public void assignSelMachinery(SmMachineDtl macBean) {
		getManagedBean().setSelectedMachine(macBean);
	}

	public void assignNewMachineryBean() {
		getManagedBean().setSelectedMachine(new SmMachineDtl());
	}

	// manufacturer end

	// -- Process Share Holder Personnel Start
	public void addOrUpdateShareHolderToSmAppl() throws ParseException {

		if (getManagedBean().getSmapplAuthPersonnels().contains(
				getManagedBean().getSelectedShareHolder())) {
			System.out.println(" *** ALREADY EXIST ******");
			if (!getManagedBean().getSelectedShareHolder().isAuthorized())
				getManagedBean().getSmapplAuthPersonnels().remove(
						getManagedBean().getSelectedShareHolder());
		} else {
			System.out.println(" *** NOT EXIST ******");
			if (getManagedBean().getSelectedShareHolder().isAuthorized())
				getManagedBean().getSmapplAuthPersonnels().add(
						getManagedBean().getSelectedShareHolder());
		}

		if (getManagedBean().getSmAppl().getSmPersonnels()
				.contains(getManagedBean().getSelectedShareHolder())) {

			System.out.println(" **** THIS IS AN UPDATE Personnel ******");
		} else {
			// TODO Staff academics and addr settings start
			getManagedBean().getSelectedShareHolder().getSmPersonnelAddresses()
					.clear();
			getManagedBean().getSelectedShareHolder().getSmPersonnelAddresses()
					.add(getManagedBean().getShareHolderMailAddr());
			getManagedBean().getSelectedShareHolder().getSmPersonnelAddresses()
					.add(getManagedBean().getShareHolderPermAddr());
			getManagedBean().getSelectedShareHolder().getSmStaffAcademics()
					.clear();
			getManagedBean().getSelectedShareHolder().getSmStaffAcademics()
					.add(getManagedBean().getShareHolderAcademics());
			// TODO Staff academics and addr settings end

			getManagedBean().getSmAppl().getSmPersonnels()
					.add(getManagedBean().getSelectedShareHolder());
			getManagedBean().getSmapplShareHolders().add(
					getManagedBean().getSelectedShareHolder());
		}
	}

	public void assignSelShareHolder(SmPersonnel shareHolderBean) {
		for (SmPersonnelAddress addr : shareHolderBean
				.getSmPersonnelAddresses()) {
			if (addr.getAddressType().equalsIgnoreCase("I")) {
				getManagedBean().setShareHolderPermAddr(addr);
			}
			if (addr.getAddressType().equalsIgnoreCase("R")) {
				getManagedBean().setShareHolderMailAddr(addr);
			}
		}
		for (SmStaffAcademic stf : shareHolderBean.getSmStaffAcademics()) {
			getManagedBean().setShareHolderAcademics(stf);
		}
		getManagedBean().setSelectedShareHolder(shareHolderBean);
	}

	public void assignNewShareHolder() {
		System.out.println("******* ADD NEW SHARE *************");
		SmPersonnelAddress perm = new SmPersonnelAddress();
		perm.setAddressType("I");
		perm.setSmAddress(setDefaultAddr());
		getManagedBean().setShareHolderPermAddr(perm);

		SmPersonnelAddress mail = new SmPersonnelAddress();
		mail.setAddressType("R");
		mail.setSmAddress(setDefaultAddr());
		getManagedBean().setShareHolderMailAddr(mail);

		getManagedBean().setShareHolderAcademics(new SmStaffAcademic());

		SmPersonnel shr = new SmPersonnel();
		shr.setEquityOwner(true);
		shr.setSmPersonnelAddresses(new ArrayList<SmPersonnelAddress>());
		shr.setSmStaffAcademics(new ArrayList<SmStaffAcademic>());
		getManagedBean().setSelectedShareHolder(shr);
	}

	// -- Process Share Holder Personnel End

	// -- Process Personnel Start
	public void addOrUpdatePersonnelToSmAppl() throws ParseException {
		// if (getManagedBean().getSmapplAuthPersonnels().contains(
		// getManagedBean().getSelectedPersonnel())
		// && !getManagedBean().getSelectedPersonnel().isAuthorized()) {
		// getManagedBean().getSmapplAuthPersonnels().remove(
		// getManagedBean().getSelectedPersonnel());
		// } else if (!getManagedBean().getSmapplAuthPersonnels().contains(
		// getManagedBean().getSelectedPersonnel())
		// && getManagedBean().getSelectedPersonnel().isAuthorized()) {
		// getManagedBean().getSmapplAuthPersonnels().add(
		// getManagedBean().getSelectedPersonnel());
		// }
		if (getManagedBean().getSmapplAuthPersonnels().contains(
				getManagedBean().getSelectedPersonnel())) {
			System.out.println(" *** ALREADY EXIST ******");
			if (!getManagedBean().getSelectedPersonnel().isAuthorized())
				getManagedBean().getSmapplAuthPersonnels().remove(
						getManagedBean().getSelectedPersonnel());
		} else {
			System.out.println(" *** NOT EXIST ******");
			if (getManagedBean().getSelectedPersonnel().isAuthorized())
				getManagedBean().getSmapplAuthPersonnels().add(
						getManagedBean().getSelectedPersonnel());
		}

		if (getManagedBean().getSmAppl().getSmPersonnels()
				.contains(getManagedBean().getSelectedPersonnel())) {

			System.out.println(" **** THIS IS AN UPDATE Certs******");
		} else {
			// TODO Staff academics and addr settings start
			getManagedBean().getSelectedPersonnel().getSmPersonnelAddresses()
					.clear();
			getManagedBean().getSelectedPersonnel().getSmPersonnelAddresses()
					.add(getManagedBean().getPersonnelMailAddr());
			getManagedBean().getSelectedPersonnel().getSmPersonnelAddresses()
					.add(getManagedBean().getPersonnelPermAddr());
			getManagedBean().getSelectedPersonnel().getSmStaffAcademics()
					.clear();
			getManagedBean().getSelectedPersonnel().getSmStaffAcademics()
					.add(getManagedBean().getPersonnelAcademics());
			// TODO Staff academics and addr settings end

			getManagedBean().getSmAppl().getSmPersonnels()
					.add(getManagedBean().getSelectedPersonnel());
		}
	}

	public void assignSelPersonnel(SmPersonnel personnelBean) {
		for (SmPersonnelAddress addr : personnelBean.getSmPersonnelAddresses()) {
			if (addr.getAddressType().equalsIgnoreCase("I")) {
				getManagedBean().setPersonnelPermAddr(addr);
			}
			if (addr.getAddressType().equalsIgnoreCase("R")) {
				getManagedBean().setPersonnelMailAddr(addr);
			}
		}
		for (SmStaffAcademic stf : personnelBean.getSmStaffAcademics()) {
			getManagedBean().setPersonnelAcademics(stf);
		}
		getManagedBean().setSelectedPersonnel(personnelBean);
	}

	public void assignNewPersonnel() {
		System.out.println("******* ADD NEW Personnel *************");
		SmPersonnelAddress perm = new SmPersonnelAddress();
		perm.setAddressType("I");
		perm.setSmAddress(setDefaultAddr());
		getManagedBean().setPersonnelPermAddr(perm);

		SmPersonnelAddress mail = new SmPersonnelAddress();
		mail.setAddressType("R");
		mail.setSmAddress(setDefaultAddr());
		getManagedBean().setPersonnelMailAddr(mail);

		getManagedBean().setPersonnelAcademics(new SmStaffAcademic());

		SmPersonnel shr = new SmPersonnel();
		shr.setEquityOwner(false);
		shr.setSmPersonnelAddresses(new ArrayList<SmPersonnelAddress>());
		shr.setSmStaffAcademics(new ArrayList<SmStaffAcademic>());
		getManagedBean().setSelectedPersonnel(shr);
	}

	// -- Process Personnel End

	// Process Bank Info Start
	public void addOrUpdateBankInfoToSmAppl() {
		// TODO to be changed later
		getManagedBean().getSelectedBank().getSmChequeSignatories().clear();
		getManagedBean().setSelectedSignatoryBankAcct(new SmSupplierBank());
		for (String str : getManagedBean().getChequeSingNames()) {
			for (SmPersonnel sm : getManagedBean().getSmAppl()
					.getSmPersonnels()) {
				if (sm.getName().equals(str)) {
					SmChequeSignatory ch = new SmChequeSignatory();
					ch.setSmPersonnel(sm);

					getManagedBean().getSelectedBank().getSmChequeSignatories()
							.add(ch);
				}
			}
		}

		if (getManagedBean().getSmAppl().getSmSupplierBanks()
				.contains(getManagedBean().getSelectedBank())) {

			System.out.println(" **** THIS IS AN UPDATE Bank******");
			System.out.println("Before Update ===== "
					+ getManagedBean().getSelectedBank()
							.getSmChequeSignatories().size());
		} else {
			System.out.println("Before add ===== "
					+ getManagedBean().getSelectedBank()
							.getSmChequeSignatories().size());
			getManagedBean().getSmAppl().getSmSupplierBanks()
					.add(getManagedBean().getSelectedBank());
		}
	}

	public void assignSelBank(SmSupplierBank shareBankBean) {
		getManagedBean().getChequeSingNames().clear();
		System.out.println("******* SIZE ********** "
				+ shareBankBean.getSmChequeSignatories().size());
		for (SmChequeSignatory ch : shareBankBean.getSmChequeSignatories()) {
			SmPersonnel pr = ch.getSmPersonnel();
			getManagedBean().getChequeSingNames().add(pr.getName());

		}
		shareBankBean.getSmChequeSignatories().clear();

		getManagedBean().setSelectedBank(shareBankBean);
	}

	public void viewSignatories(SmSupplierBank signAcctBean) {
		getManagedBean().setSelectedSignatoryBankAcct(signAcctBean);
	}

	public void assignNewBank() {
		System.out.println("******* ADD NEW Bank Info *************");
		getManagedBean().setChequeSingNames(new ArrayList<String>());
		SmSupplierBank bank = new SmSupplierBank();
		bank.setSmAddress(setDefaultAddr());
		bank.setPmbank(new PmBank());
		bank.setSmChequeSignatories(new ArrayList<SmChequeSignatory>());
		getManagedBean().setSelectedBank(bank);
	}

	// -- Process Bank Info End
	/**
	 * Test BPM Connect
	 */
	public boolean initialBpmTask() {
		IMofRegistrationSO regSo = null;
		try {
			regSo = (IMofRegistrationSO) BusinessDelegate.getInstance()
					.getServiceObject("sm", IMofRegistrationSO.class);
			return regSo.initialBpmTask();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			regSo = null;
		}

	}

	/**
	 * Make a call to the BPM for MofRegistration
	 */
	public void initiateSmApplicationTask() {
		IMofRegistrationSO regSo = null;
		try {
			// TODO to be used while doing the iteration2
			// SmAppl bpmAppl = getManagedBean().getSmSupplier().getSmAppls()
			// .get(0);
			SmAppl bpmAppl = getManagedBean().getSmAppl();
			System.out.println(" **** SUPPORTING DOC MODE ****"
					+ bpmAppl.getSupportingDocMode());
			if (bpmAppl.getApplType().equalsIgnoreCase("N")) {
				System.out.println(" **** BPM FROM MOF REG CALLED ******");
				bpmAppl.setApplId((long) 111);
			} else if (bpmAppl.getApplType().equalsIgnoreCase("B")) {
				System.out.println(" **** BPM FROM BUMI APPL CALLED ******");
				bpmAppl.setApplId((long) 222);
			} else if (bpmAppl.getApplType().equalsIgnoreCase("U")) {
				System.out.println(" **** BPM FROM UPDATE APPL CALLED ******");
				bpmAppl.setApplId((long) 333);
			} else if (bpmAppl.getApplType().equalsIgnoreCase("A")) {
				System.out
						.println(" **** BPM FROM ADDITIONAL CATEGORY CODE APPL CALLED ******");
				bpmAppl.setApplId((long) 444);
			} else if (bpmAppl.getApplType().equalsIgnoreCase("R")) {
				System.out
						.println(" **** BPM FROM RENEWAL APPLICATION IS CALLED ******");
				bpmAppl.setApplId((long) 555);
			} else if (bpmAppl.getApplType().equalsIgnoreCase("C")) {
				System.out
						.println(" **** BPM FROM CANCEL REG APPLICATION IS CALLED ******");
				bpmAppl.setApplId((long) 666);
			} else {
				bpmAppl.setApplId((long) 777);
			}
			if (bpmAppl.getSupportingDocMode() == null) {
				bpmAppl.setSupportingDocMode("O");
			}
			System.out.println(" ***** VALUE OF SELECTED OBJECT ***** "
					+ getManagedBean().getSmSupplier().getSmAppls().size()
					+ "  ---  " + bpmAppl.getApplId());
			MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();

			// TODO Remove the hard code later
			reqDTO.setOutcome("SUBMIT");
			reqDTO.setApplId(this.getManagedBean().getSmAppl().getApplId());
			reqDTO.setSupplierId(bpmAppl.getSmSupplier().getSupplierId());
			reqDTO.setAppType(bpmAppl.getApplType());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setSmAppl(bpmAppl);
			reqDTO.setUserName(new FacesScopes().getUserScreenName());

			if (this.getManagedBean().getSmAppl().getSupportingDocMode()
					.equalsIgnoreCase("O")) {
				reqDTO.setOnlineDoc(true);
			} else {
				reqDTO.setOnlineDoc(false);
			}

			MofRegistrationResDTO resDTO;
			regSo = (IMofRegistrationSO) BusinessDelegate.getInstance()
					.getServiceObject("sm", IMofRegistrationSO.class);
			resDTO = regSo.initiateSmApplicationTask(reqDTO);
		} finally {
			regSo = null;
		}

	}

	public String submitBumiApplToBpm() {
		try {
			initiateSmApplicationTask();
			return SupplierManagementConst.BUMI_APPL_SUCCESS;
		} catch (Exception e) {
			return SupplierManagementConst.BUMI_APPL_FAIL;
		}
	}

	public String submitRenewMofApplToBpm() {
		try {
			initiateSmApplicationTask();
			return SupplierManagementConst.RENEW_MOF_APPL_SUCCESS;
		} catch (Exception e) {
			return SupplierManagementConst.RENEW_MOF_APPL_FAIL;
		}
	}

	public String submitAddnlCategoryApplToBpm() {
		try {
			initiateSmApplicationTask();
			return SupplierManagementConst.ADDNLCAT_APPL_SUCCESS;
		} catch (Exception e) {
			return SupplierManagementConst.ADDNLCAT_APPL_FAIL;
		}
	}

	public String submitMofApplToBpm() {
		try {
			initiateSmApplicationTask();
			return SupplierManagementConst.MOF_APPL_SUCCESS;
		} catch (Exception e) {
			return SupplierManagementConst.MOF_APPL_FAIL;
		}
	}

	public String submitCancelMofApplToBpm() {
		System.out.println(" Call Cancel ---------------->>>>>>>>>>");
		try {
			initiateSmApplicationTask();
			return SupplierManagementConst.CANCEL_MOF_APPL_SUCCESS;
		} catch (Exception e) {
			return SupplierManagementConst.PROCEED_CANCEL_MOF_APPLN;
		}
	}

	public String submitDeletePendingMofApplToBpm() {
		IMofRegistrationSO regSo = null;
		try {

			SmAppl bpmAppl = getManagedBean().getSmAppl();
			System.out.println("*** DELTE PENDING MOF STATUS === >>> "
					+ bpmAppl.getStatusId() + " -- CURRENT APPL TYPE --- "
					+ bpmAppl.getApplType());
			if (bpmAppl.getStatusId() == 1) {
				getManagedBean().setDelPendAckMsg(" is delete successfully.");
				// TODO directly delete the pending record from the DB
			} else {
				// TODO remove hard code id later
				bpmAppl.setApplId(7898);
				MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
				reqDTO.setApplId(this.getManagedBean().getSmAppl().getApplId());
				// TODO remove hard code later
				reqDTO.setOutcome("SUBMIT");
				reqDTO.setSupplierId(this.getManagedBean().getSmAppl()
						.getSmSupplier().getSupplierId());
				reqDTO.setAppType(this.getManagedBean().getSmAppl()
						.getApplType());
				reqDTO.setTaskId(this.getManagedBean().getTaskId());
				reqDTO.setUserName(new FacesScopes().getUserScreenName());

				if (this.getManagedBean().getSmAppl().getSupportingDocMode() == "O") {
					reqDTO.setOnlineDoc(true);
				} else {
					reqDTO.setOnlineDoc(false);
				}

				MofRegistrationResDTO resDTO;
				regSo = (IMofRegistrationSO) BusinessDelegate.getInstance()
						.getServiceObject("sm", IMofRegistrationSO.class);
				resDTO = regSo.initiateSmDelPendingApplicationTask(reqDTO);
				getManagedBean()
						.setDelPendAckMsg(
								" is submitted successfully. Please wait for further notification.");
			}

			return SupplierManagementConst.DELETE_PENDING_MOF_APPL_SUCCESS;
		} catch (Exception e) {
			return SupplierManagementConst.PROCEED_DEL_PENDING_MOF_APPLN;
		} finally {
			regSo = null;
		}
	}

	public String submitPOorApproverReviewToBPM(String res) {
		IReviewApplicationSO reqSO = null;
		try {
			log.info("----------------------Submit PO / Approver Decision For BPM-------------------------"
					+ res);
			ReviewApplicationReqDTO reqDTO = new ReviewApplicationReqDTO();
			reqSO = (IReviewApplicationSO) BusinessDelegate.getInstance()
					.getServiceObject("sm", IReviewApplicationSO.class);
			reqDTO.setApplId(this.getManagedBean().getSmAppl().getApplId());
			reqDTO.setOutcome(res);
			reqDTO.setSuppId(this.getManagedBean().getSmAppl().getSmSupplier()
					.getSupplierId());
			reqDTO.setAppType(this.getManagedBean().getSmAppl().getApplType());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setUserName(new FacesScopes().getUserScreenName());

			if (this.getManagedBean().getSmAppl().getSupportingDocMode()
					.equalsIgnoreCase("O")) {
				reqDTO.setOnlineDoc(true);
			} else {
				reqDTO.setOnlineDoc(false);
			}

			ReviewApplicationResDTO resDTO = reqSO
					.submitPOorApproverReviewToBPM(reqDTO);
			if (getManagedBean().isApproverUser()) {
				System.out.println(" **** Final Decision from Approver ****");
				if (res.equalsIgnoreCase("Approve")) {
					getManagedBean()
							.setDecisionAckMsg(
									" is APPROVED.A notification is sent to the respective supplier.");
				} else if (res.equalsIgnoreCase("Reject")) {
					getManagedBean()
							.setDecisionAckMsg(
									" is REJECTED.A notification is sent to the respective supplier.");
				}
				return SupplierManagementConst.APPROVER_DECISION_ACK;
			} else if (getManagedBean().isPoUser()) {
				System.out.println(" **** Final Decision from PO ****");
				if (res.equalsIgnoreCase("Approve")) {
					getManagedBean()
							.setDecisionAckMsg(
									" is APPROVED.A notification is sent to the respective supplier.");
				} else if (res.equalsIgnoreCase("Reject")) {
					getManagedBean()
							.setDecisionAckMsg(
									" is REJECTED.A notification is sent to the respective supplier.");
				} else if (res.equalsIgnoreCase("Escalate")) {
					getManagedBean()
							.setDecisionAckMsg(
									" is Escalated to the Apporver.Please wait for further notification.");
				}
				return SupplierManagementConst.PO_DECISION_ACK;

			} else {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"Sample error message",
								"Un Authorized user to perform this action"));
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_ERROR,
									"Sample error message",
									"There is problem in submitting the decision.Please contact the administrator or retry later"));
			return null;
		} finally {
			reqSO = null;
		}

	}

	public void changeVerifyAll(Boolean isVerifiedAll) {
		System.out.println(" **** Enter to  verified all ***** "
				+ isVerifiedAll);
		SmApplSectionReview sec = new SmApplSectionReview();

		if (getManagedBean().isPoUser()) {
			System.out.println(" *** PO VERIFIED ALL *******");
			sec = getManagedBean().getPoSecReview();
			getManagedBean().setPoSecReview(
					setVerificationValues(sec, isVerifiedAll));
		} else if (getManagedBean().isApproverUser()) {
			System.out.println(" *** APPROVER VERIFIED ALL *******");
			sec = getManagedBean().getApprvrSecReview();
			getManagedBean().setApprvrSecReview(
					setVerificationValues(sec, isVerifiedAll));
		}

	}

	public void changeVerifySection(Boolean isSectionVerified) {
		System.out.println(" **** Enter to  verified all ***** "
				+ isSectionVerified);
		SmApplSectionReview sec = new SmApplSectionReview();

		if (getManagedBean().isPoUser()) {
			System.out.println(" *** PO VERIFIED ALL *******");
			sec = getManagedBean().getPoSecReview();
			getManagedBean().setPoSecReview(
					assignVerificationCount(sec, isSectionVerified));
		} else if (getManagedBean().isApproverUser()) {
			System.out.println(" *** APPROVER VERIFIED ALL *******");
			sec = getManagedBean().getApprvrSecReview();
			getManagedBean().setApprvrSecReview(
					assignVerificationCount(sec, isSectionVerified));
		}
	}

	private SmApplSectionReview assignVerificationCount(
			SmApplSectionReview sec, Boolean isSectionVerified) {
		System.out.println("**** Intial Check Count ==== "
				+ sec.getCheckCount());
		if (isSectionVerified) {
			sec.setCheckCount(sec.getCheckCount() + 1);

		} else {
			sec.setCheckCount(sec.getCheckCount() - 1);

		}
		System.out.println("**** Count After checking **** "
				+ sec.getCheckCount());
		if (sec.getCheckCount() >= getManagedBean().getMaxReviewCount()) {
			sec.setVerifiedAll(Boolean.TRUE);
		} else {
			sec.setVerifiedAll(Boolean.FALSE);
		}

		return sec;
	}

	private SmApplSectionReview setVerificationValues(SmApplSectionReview sec,
			Boolean isVerifiedAll) {

		if (isVerifiedAll) {
			sec.setCheckCount(getManagedBean().getMaxReviewCount());

		} else {
			sec.setCheckCount(0);

		}
		sec.setBasicVerified(isVerifiedAll);
		sec.setOfficeFacilityVerfied(isVerifiedAll);
		sec.setStaffVerified(isVerifiedAll);

		sec.setTrnsNetworkVerified(isVerifiedAll);
		sec.setBranchVerified(isVerifiedAll);
		sec.setAssociateVerified(isVerifiedAll);

		sec.setCertVerified(isVerifiedAll);
		sec.setExperienceVerified(isVerifiedAll);
		sec.setCaptialVerified(isVerifiedAll);

		sec.setShareholderVerifed(isVerifiedAll);
		sec.setEquityCompVerifed(isVerifiedAll);
		sec.setPersonnelVerified(isVerifiedAll);

		sec.setBankVerified(isVerifiedAll);
		sec.setCategoryVerified(isVerifiedAll);
		sec.setSitevisitVerified(isVerifiedAll);
		return sec;
	}

	public void onTabChange(TabChangeEvent event) {
		String tabTitle = event.getTab().getTitle();
		System.out.println("Active Tab: " + tabTitle);
		if (tabTitle.equalsIgnoreCase("Company Basic Information")) {
			getManagedBean().setTabActiveIndex(0);
		} else if (tabTitle.equalsIgnoreCase("Branch /Assoc Company")) {
			getManagedBean().setTabActiveIndex(1);
		}
		System.out.println("Active Tab Index === "
				+ getManagedBean().getTabActiveIndex());

	}

	public String submitUpdateMofApplToBpm() {
		try {
			initiateSmApplicationTask();
			return SupplierManagementConst.UPDATE_MOF_APPL_SUCCESS;
		} catch (Exception e) {
			return SupplierManagementConst.UPDATE_MOF_APPL_FAIL;
		}
	}

	private SmAddress setDefaultAddr() {
		SmAddress addr = new SmAddress();
		addr.setPmCity(new PmCity());
		addr.setPmCountry(new PmCountry());
		addr.setPmState(new PmState());
		addr.setPmDistrict(new PmDistrict());

		return addr;
	}

	private List<SmStaffTotal> getStaffList(String supplierType) {
		List<SmStaffTotal> staffList = new ArrayList<SmStaffTotal>();
		SmAppl app = getManagedBean().getSmAppl();
		if (supplierType.equals("1")) {
			SmStaffTotal stf = new SmStaffTotal();
			stf.setStaffCategory("C");
			stf.setSmAppl(app);
			staffList.add(stf);
		} else if (supplierType.equals("2")) {
			SmStaffTotal stf1 = new SmStaffTotal();
			stf1.setStaffCategory("P");
			stf1.setSmAppl(app);
			staffList.add(stf1);
			SmStaffTotal stf2 = new SmStaffTotal();
			stf2.setStaffCategory("S");
			stf2.setSmAppl(app);
			staffList.add(stf2);
			SmStaffTotal stf3 = new SmStaffTotal();
			stf3.setStaffCategory("L");
			stf3.setSmAppl(app);
			staffList.add(stf3);
		}
		return staffList;
	}

	private SmAppl defaultSmAppl(SmAppl appl) {
		// = new SmAppl();
		if (appl.getSmSupplierBranches() == null) {
			appl.setSmSupplierBranches(new ArrayList<SmSupplierBranch>());
		}
		if (appl.getSmAssociateCompanies() == null) {
			appl.setSmAssociateCompanies(new ArrayList<SmAssociateCompany>());
		}
		if (appl.getSmSupplierCerts() == null) {
			appl.setSmSupplierCerts(new ArrayList<SmSupplierCert>());
		}
		if (appl.getSmContractExperiences() == null) {
			appl.setSmContractExperiences(new ArrayList<SmContractExperience>());
		}
		if (appl.getSmProjectExperiences() == null) {
			appl.setSmProjectExperiences(new ArrayList<SmProjectExperience>());
		}
		if (appl.getSmEquityCompanies() == null) {
			appl.setSmEquityCompanies(new ArrayList<SmEquityCompany>());
		}
		if (appl.getSmSupplierCategories() == null) {
			appl.setSmSupplierCategories(new ArrayList<SmSupplierCategory>());
		}
		if (appl.getSmFacilities() == null) {
			appl.setSmFacilities(new ArrayList<SmFacility>());
		}
		if (appl.getSmManufacturingDtls() == null) {
			appl.setSmManufacturingDtls(new ArrayList<SmManufacturingDtl>());
		}
		if (appl.getSmMachineDtls() == null) {
			appl.setSmMachineDtls(new ArrayList<SmMachineDtl>());
		}
		if (appl.getSmPersonnels() == null) {
			appl.setSmPersonnels(new ArrayList<SmPersonnel>());
		}
		if (appl.getSmSupplierBanks() == null) {
			appl.setSmSupplierBanks(new ArrayList<SmSupplierBank>());
		}

		return appl;
	}

	private MofRegistrationResDTO retriveSupplierById(long id) {
		IMofRegistrationSO mofRegistrationSO = null;

		try {
			mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							IMofRegistrationSO.class);
			MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
			reqDto.setSupplierId(id);
			MofRegistrationResDTO resDTO = mofRegistrationSO
					.retrieveSupplierProfile(reqDto);
			return resDTO;

		} catch (Exception e) {
			return new MofRegistrationResDTO();
		} finally {

		}
	}

	private String generateApplicationNum(String applType) {
		return null;

	}

	private int assignMaxReviewCount(String appType) {
		int i = 0;
		switch (appType) {

		case "N":
			i = SupplierManagementConst.NEW_APPL_REVIEW_MAXCOUNT;
			break;
		case "A":
			i = SupplierManagementConst.ADDLN_CATCODES_APPL_REVIEW_MAXCOUNT;
			break;
		case "B":
			i = SupplierManagementConst.BUMI_APPL_REVIEW_MAXCOUNT;
			break;
		case "U":
			i = SupplierManagementConst.UPDATE_MOF_APPL_REVIEW_MAXCOUNT;
			break;
		case "R":
			i = SupplierManagementConst.RENEW_APPL_REVIEW_MAXCOUNT;
			break;
		default:
			break;

		}
		return i;
	}

	private String setNavigationForAppl(String appType) {
		String s = null;
		switch (appType) {

		case "N":
			s = SupplierManagementConst.PROCEED_MOF_REG;
			break;
		case "A":
			s = SupplierManagementConst.PROCEED_ADDTNLCAT_MOF_APPLN;
			break;
		case "B":
			s = SupplierManagementConst.PROCEED_BUMI_APPLN;
			break;
		case "U":
			s = SupplierManagementConst.PROCEED_UPDATE_MOF_APPLN;
			break;
		case "R":
			s = SupplierManagementConst.PROCEED_RENEW_MOF_APPLN;
			break;
		case "C":
			s = SupplierManagementConst.PROCEED_CANCEL_MOF_APPLN;
			break;
		default:
			break;

		}
		return s;
	}
}
