package my.ep.web.backingbean.ss.bsvattendance;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.ss.quationtender.IQTBriefingSO;
import my.ep.biz.service.ss.quationtender.IQTDetailSO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.ss.quationtender.BriefingReqDTO;
import my.ep.dto.ss.quationtender.BriefingResDTO;
import my.ep.dto.ss.quationtender.QuationTenderReqDTO;
import my.ep.dto.ss.quationtender.QuationTenderResDTO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.ss.quationtender.ScQt;
import my.ep.persistence.entity.ss.quationtender.ScQtBriefing;
import my.ep.persistence.entity.ss.quationtender.ScQtBriefingAttendance;
import my.ep.persistence.entity.ss.quationtender.ScQtBriefingRegistration;
import my.ep.type.pm.RoleCode;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.ss.bsvattendance.BSVAttendanceManagedBean;
import my.ep.web.ss.quationtender.common.QTConstants;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;

/**
 * @author sri Briefing Attendance Backing Bean
 */
@ManagedBean(name = "bsvAttendanceBackingBean")
@RequestScoped
public class BSVAttendanceBackingBean extends
		BaseBackingBean<BSVAttendanceManagedBean> {
	public static Logger log = Logger.getLogger(BSVAttendanceBackingBean.class);
	
	private static final String NAV_TO_BSV_ATTENDANCE_MAIN_PAGE = "bsvAttendanceRegisterMain.xhtml?faces-redirect=true";

	public BSVAttendanceBackingBean() {
		
		retrieveBriefingSiteVisitList();
		// retrieveUserListByRole();
	}

	/**
	 * retrieving Briefing SiteVisit List
	 */
	public void retrieveBriefingSiteVisitList() {
		log.info("** retrieveBriefingSiteVisitList **");
		IQTBriefingSO qTBriefingSO = null;
		IQTDetailSO qtSo = null;
		
		try {
			qTBriefingSO = BusinessDelegate.getInstance().getServiceObject(
					QTConstants.SS_MODULE, IQTBriefingSO.class);
			qtSo = BusinessDelegate.getInstance().getServiceObject("ss",
					IQTDetailSO.class);
			
			// Briefing Details Listing
			BriefingReqDTO reqDTO = new BriefingReqDTO();
			reqDTO.setQtId(146L);
			reqDTO.setBriefingType("B");

			BriefingResDTO resDTO = qTBriefingSO
					.retrieveBriefingSiteVisitList(reqDTO);

			List<ScQtBriefing> briefingDetailslist = resDTO
					.getBriefingResultBean().getResultList();

			this.getManagedBean().setBriefingDetailsList(briefingDetailslist);

			// Site Visit Listing
			reqDTO.setBriefingType("S");

			resDTO = qTBriefingSO.retrieveBriefingSiteVisitList(reqDTO);

			List<ScQtBriefing> sitevisitlist = resDTO.getBriefingResultBean()
					.getResultList();

			this.getManagedBean().setSiteVisitDetailsList(sitevisitlist);

			// getting Briefing Same As Site Visit value
			QuationTenderReqDTO reqQtDTO = new QuationTenderReqDTO();
			reqQtDTO.setQtId(146L);

			QuationTenderResDTO resQtDTO = qtSo.retriveQtById(reqQtDTO);

			ScQt scQt = resQtDTO.getQtDetail();

			if (scQt.isSameBsv()) {

				this.getManagedBean().setBriefSameAsSiteVisit("Yes");

				this.getManagedBean().setVisibleState(false);

			} else {

				this.getManagedBean().setBriefSameAsSiteVisit("No");

				this.getManagedBean().setVisibleState(true);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							ex.toString(), ex.getMessage()));
		} finally {
			qTBriefingSO = null;
			qtSo = null;
		}

	}

	/**
	 * Attendance By Company popup
	 */
	public void attendanceByCompany() {
		log.info("** attendanceByCompany **");

		List<ScQtBriefing> selectedBriefinglist = this.getManagedBean()
				.getSelectedBriefingDetails();

		List<ScQtBriefing> selectedSiteVisitlist = this.getManagedBean()
				.getSelectedSiteVisitDetails();

		this.getManagedBean().setSelectedBriefingDetails(selectedBriefinglist);

		this.getManagedBean()
				.setSelectedSiteVisitDetails(selectedSiteVisitlist);
	}

	/**
	 * Attendance Registration popup
	 */
	public void attendanceRegistration() {
		log.info("** attendanceRegistration **");
		IQTBriefingSO qTBriefingSO = null;
		
		try {
			qTBriefingSO = BusinessDelegate.getInstance().getServiceObject(
					QTConstants.SS_MODULE, IQTBriefingSO.class);
			
			if (getManagedBean().getScQtBriefing() != null) {

				ScQtBriefing selectedBriefing = this.getManagedBean()
						.getScQtBriefing();

				BriefingReqDTO reqDTO = new BriefingReqDTO();

				reqDTO.setQtBriefingId(selectedBriefing.getQtBriefingId());

				BriefingResDTO resDTO = qTBriefingSO
						.retrieveBriefingRegistrationList(reqDTO);

				List<ScQtBriefingRegistration> briefRegList = resDTO
						.getBriefingRegResultBean().getResultList();

				this.getManagedBean().setBriefingRegistrationList(briefRegList);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			qTBriefingSO = null;
		}
	}

	/**
	 * Adding new row for Company List in Briefing Attendance Registration
	 */
	public void addCompany() {
		log.info("** add Company **");
		ScQtBriefingRegistration attendacelist = new ScQtBriefingRegistration();
		try {
			List<ScQtBriefingRegistration> attendaceByComplist = getManagedBean()
					.getBriefingRegistrationList();
			if (null == attendaceByComplist) {
				attendaceByComplist = new ArrayList<ScQtBriefingRegistration>();
			}

			attendaceByComplist.add(attendacelist);
			this.getManagedBean().setBriefingRegistrationList(
					attendaceByComplist);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Adding new row for Attendance List for Company in in Briefing Attendance Registration
	 */
	public void addAttendaceForComp() {
		log.info("** add Attendance for Company  **");
		ScQtBriefingAttendance attendacelist = new ScQtBriefingAttendance();
		try {
			List<ScQtBriefingAttendance> attendaceDetails = getManagedBean()
					.getBriefingAttendanceList();
			if (null == attendaceDetails) {
				attendaceDetails = new ArrayList<ScQtBriefingAttendance>();
			}
			attendaceDetails.add(attendacelist);
			this.getManagedBean().setBriefingAttendanceList(attendaceDetails);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Adding new row for Registration in Briefing Attendance By Company
	 */
	public void addRegistration() {
		log.info("** add Registration **");
		ScQtBriefingAttendance attendacelist = new ScQtBriefingAttendance();
		try {
			List<ScQtBriefingAttendance> attendaceDetails = getManagedBean()
					.getBriefingAttendanceList();
			if (null == attendaceDetails) {
				attendaceDetails = new ArrayList<ScQtBriefingAttendance>();
			}
			attendaceDetails.add(attendacelist);
			this.getManagedBean().setBriefingAttendanceList(attendaceDetails);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * onRowSelect event - For datatable row selection for Attendance List For Company 
	 * in Attendance Registration popup 
	 */
	public void onRowSelect(SelectEvent event) { 
		IQTBriefingSO qTBriefingSO = null;
		log.info("** onRowSelecte event  **");
		qTBriefingSO = BusinessDelegate.getInstance().getServiceObject(
				QTConstants.SS_MODULE, IQTBriefingSO.class);
		ScQtBriefingRegistration briefReg = this.getManagedBean()
				.getSelectedbriefRegistration();
		System.out.println("selected briefReg "
				+ briefReg.getSmSupplier().getSupplierName());

		// Attendance List For Company listing 
		BriefingReqDTO reqDTO = new BriefingReqDTO();
		reqDTO.setQtBriefingRegistrationId(briefReg.getQtBriefingRegistrationId());

		BriefingResDTO resDTO = qTBriefingSO.retrieveBriefingAttendanceList(reqDTO);
		List<ScQtBriefingAttendance> briefingAttList = resDTO.getBriefingAttResultBean().getResultList();
		this.getManagedBean().setBriefingAttendanceList(briefingAttList);

	}
	

	/**
	 * getting Approver drop down listing
	 */
	public void retrieveUserListByRole() {
		IUserSO userSO = null;
		try {
			userSO = BusinessDelegate.getInstance().getServiceObject(
					QTConstants.PM_MODULE, IUserSO.class);
			
			// Get Approver Users
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setRoleCode(RoleCode.APPROVER.value());

			UserResDTO resDTO = userSO.retrieveUserListByRole(reqDTO);
			List<PmUser> appNamelist = resDTO.getUserList();
			System.out.println("Approver List: " + appNamelist.size());
			this.getManagedBean().setApproverNames(appNamelist);

		} catch (Exception ex) {
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							ex.toString(), ex.getMessage()));
		} finally {
			userSO = null;
		}

	}
	
	
	/**
	 * Cancel button for Bsv Registration
	 */
	public String cancelBriefing() {
		log.info("** cancel Button **");
		return NAV_TO_BSV_ATTENDANCE_MAIN_PAGE;

		

	}
	
	

}
