package my.ep.web.backingbean.sm;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplAssociateCompany;
import my.ep.persistence.entity.sm.SmApplContractExperience;
import my.ep.persistence.entity.sm.SmApplFacility;
import my.ep.persistence.entity.sm.SmApplProjectExperience;
import my.ep.persistence.entity.sm.SmApplStaffTotal;
import my.ep.persistence.entity.sm.SmApplSupplierBranch;
import my.ep.persistence.entity.sm.SmApplSupplierCert;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.SmApplicationManagedBean;

import org.apache.log4j.Logger;

@ManagedBean(name = "backingBean")
@RequestScoped
public class SmApplicationBackingBean extends
		BaseBackingBean<SmApplicationManagedBean> {

	public static Logger log = Logger.getLogger(SmApplicationBackingBean.class);

	private static final String PROCEED_MOF_REG = "mofRegBasePage";

	private int currentTab;
	private int outputTab;

	public SmApplicationBackingBean() {

	}

	SmAppl sm;

	public String proceedMofRegistration() {
		System.out.println("**** ENTER BACKIN BEAN *************");
		SmAppl appl = new SmAppl();
		appl.setApplId(12727);
		appl.setApplNo(" KN-01102013-0011");
		// 1- For Contractor
		// 2 - For Consultant
		appl.setSupplierType("2");
		List<SmApplStaffTotal> staffList = new ArrayList<SmApplStaffTotal>();
		if (appl.getSupplierType().equals("1")) {
			SmApplStaffTotal stf = new SmApplStaffTotal();
			stf.setStaffCategory("C");
			staffList.add(stf);
		} else if (appl.getSupplierType().equals("2")) {
			SmApplStaffTotal stf1 = new SmApplStaffTotal();
			stf1.setStaffCategory("P");
			staffList.add(stf1);
			SmApplStaffTotal stf2 = new SmApplStaffTotal();
			stf2.setStaffCategory("S");
			staffList.add(stf2);
			SmApplStaffTotal stf3 = new SmApplStaffTotal();
			stf3.setStaffCategory("L");
			staffList.add(stf3);
		}
		appl.setSmApplStaffTotals(staffList);
		appl.setApplType("NM");
		if (appl.getSmApplSupplierBranches() == null) {
			appl.setSmApplSupplierBranches(new ArrayList<SmApplSupplierBranch>());
		}
		if (appl.getSmApplAssociateCompanies() == null) {
			appl.setSmApplAssociateCompanies(new ArrayList<SmApplAssociateCompany>());
		}
		if (appl.getSmApplSupplierCerts() == null) {
			appl.setSmApplSupplierCerts(new ArrayList<SmApplSupplierCert>());
		}
		if (appl.getSmApplContractExperiences() == null) {
			appl.setSmApplContractExperiences(new ArrayList<SmApplContractExperience>());
		}
		if (appl.getSmApplProjectExperiences() == null) {
			appl.setSmApplProjectExperiences(new ArrayList<SmApplProjectExperience>());
		}
		getManagedBean().setSmAppl(appl);
		getManagedBean().setTabActiveIndex(0);
		getManagedBean().setOfficeFacility(new SmApplFacility());
		System.out.println("**** ENTER BACKIN BEAN *************"
				+ getManagedBean().getSmAppl().getApplType());
		return PROCEED_MOF_REG;
	}

	public void updateSmAppl() throws Exception {
		try {
			System.out.println("**** ENTER ACTION METHOD BEAN *************"
					+ currentTab + "  " + outputTab);

			// sm.setApplId(13131);
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
		this.currentTab = currentTab;
		this.outputTab = outputTab;
	}

	public void officeFacilitySelected(AjaxBehaviorEvent e) {
		System.out.println(" ******* On Change Office Facility ***** "
				+ getManagedBean().getOfficeFacility().getOwnershipType());
		System.out.println(" **** Values In loop *********"
				+ getManagedBean().getOfficeFacility().getRentalRate());
	}

	// -- Process Branches Start
	public void addOrUpdateBranchToSmAppl() {

		if (getManagedBean().getSmAppl().getSmApplSupplierBranches()
				.contains(getManagedBean().getSelectedBranch())) {

			System.out.println(" **** THIS IS AN UPDATE For Branches******"
					+ getManagedBean().getSelectedBranch().getPhone());
		} else {
			getManagedBean().getSmAppl().getSmApplSupplierBranches()
					.add(getManagedBean().getSelectedBranch());
		}
	}

	public void assignSelBranch(SmApplSupplierBranch branchBean) {
		getManagedBean().setSelectedBranch(branchBean);
	}

	public void assignNewBranchBean() {
		getManagedBean().setSelectedBranch(new SmApplSupplierBranch());
	}

	// -- Process Branches End

	// -- Process Associated Companies Start
	public void addOrUpdateAssocToSmAppl() {

		if (getManagedBean().getSmAppl().getSmApplAssociateCompanies()
				.contains(getManagedBean().getSelectedAssocCompany())) {

			System.out.println(" **** THIS IS AN UPDATE Assoc Companies******");
		} else {
			getManagedBean().getSmAppl().getSmApplAssociateCompanies()
					.add(getManagedBean().getSelectedAssocCompany());
		}
	}

	public void assignSelAssoc(SmApplAssociateCompany assocBean) {
		getManagedBean().setSelectedAssocCompany(assocBean);
	}

	public void assignNewAssocBean() {
		getManagedBean().setSelectedAssocCompany(new SmApplAssociateCompany());
	}

	// -- Process Associated Companies End

	// -- Process Certs Start
	public void addOrUpdateCertsToSmAppl() throws ParseException {
		if (getManagedBean().getSmAppl().getSmApplSupplierCerts()
				.contains(getManagedBean().getSelectedCert())) {

			System.out.println(" **** THIS IS AN UPDATE Certs******");
		} else {
			getManagedBean().getSmAppl().getSmApplSupplierCerts()
					.add(getManagedBean().getSelectedCert());
		}
	}

	public void assignSelCert(SmApplSupplierCert certBean) {
		getManagedBean().setSelectedCert(certBean);
	}

	public void assignNewCert() {
		getManagedBean().setSelectedCert(new SmApplSupplierCert());
	}

	// -- Process Certss End
	// -- Process Contract Projs Start
	public void addOrUpdateContractProjsToSmAppl() {
		if (getManagedBean().getSmAppl().getSmApplContractExperiences()
				.contains(getManagedBean().getSelectedContractProj())) {

			System.out
					.println(" **** THIS IS AN UPDATE FOR Contract Prjs******");
		} else {
			getManagedBean().getSmAppl().getSmApplContractExperiences()
					.add(getManagedBean().getSelectedContractProj());
		}
	}

	public void assignSelContractProj(SmApplContractExperience contractBean) {
		getManagedBean().setSelectedContractProj(contractBean);
	}

	public void assignNewContractProj() {
		getManagedBean()
				.setSelectedContractProj(new SmApplContractExperience());
	}

	// -- Process Contract Projs End

	// -- Process Consultant Projs Start
	public void addOrUpdateConsultantProjsToSmAppl() {
		if (getManagedBean().getSmAppl().getSmApplProjectExperiences()
				.contains(getManagedBean().getSelectedConstProj())) {

			System.out
					.println(" **** THIS IS AN UPDATE FOR Consultant Prjs******");
		} else {
			getManagedBean().getSmAppl().getSmApplProjectExperiences()
					.add(getManagedBean().getSelectedConstProj());
		}
	}

	public void assignSelConsultantProj(SmApplProjectExperience consultantBean) {
		getManagedBean().setSelectedConstProj(consultantBean);
	}

	public void assignNewConsultantProj() {
		getManagedBean().setSelectedConstProj(new SmApplProjectExperience());
	}
	// -- Process Consultant Projs End
}
