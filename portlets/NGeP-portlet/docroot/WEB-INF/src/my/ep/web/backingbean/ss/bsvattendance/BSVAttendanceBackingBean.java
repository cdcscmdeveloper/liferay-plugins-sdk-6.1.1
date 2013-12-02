package my.ep.web.backingbean.ss.bsvattendance;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.ss.quationtender.IQTBriefingSO;
import my.ep.biz.service.ss.quationtender.IQTDetailSO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.ss.quationtender.BriefingReqDTO;
import my.ep.dto.ss.quationtender.BriefingResDTO;
import my.ep.dto.ss.quationtender.QuationTenderReqDTO;
import my.ep.dto.ss.quationtender.QuationTenderResDTO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.ss.quationtender.ScQt;
import my.ep.persistence.entity.ss.quationtender.ScQtBriefing;
import my.ep.persistence.entity.ss.quationtender.ScQtBriefingAttendance;
import my.ep.persistence.entity.ss.quationtender.ScQtBriefingRegistration;
import my.ep.type.pm.RoleCode;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.ss.bsvattendance.BSVAttendanceManagedBean;
import my.ep.web.ss.quationtender.common.QTConstants;
import my.ep.web.util.FacesScopes;
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

	public BSVAttendanceBackingBean() throws NamingException  {
		retrieveBriefingSiteVisitList();
	    retrieveUserListByRole();
	    getPmStatus();
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
				 System.out.println("======briefRegList======"+briefRegList.size());
				 
				 List<ScQtBriefingRegistration> list =new ArrayList<ScQtBriefingRegistration>();
				 for(ScQtBriefingRegistration scqtbriefReg :briefRegList){
					 scqtbriefReg.setSmMofAccount(new SmMofAccount());
					 list.add(scqtbriefReg);
				 }
				 
				 this.getManagedBean().setBriefingRegistrationList(list);
				 
				
				// drop down listing for mof nor and sm supplier 
				List<SmMofAccount> mofNors =new  ArrayList<SmMofAccount>();
				SmMofAccount SmMofAccount1 =new  SmMofAccount();
				SmMofAccount1.setMofNo("MOF000001");
				SmMofAccount1.setMofAccountId(27);
				mofNors.add(SmMofAccount1);
				
				SmMofAccount SmMofAccount2 =new  SmMofAccount();
				SmMofAccount2.setMofNo("MOF000002");
				SmMofAccount2.setMofAccountId(27);
				mofNors.add(SmMofAccount2);
				 this.getManagedBean().setSmMofAccounts(mofNors);
				 
				 List<SmSupplier> smSuppliers = new ArrayList<SmSupplier>();
				 SmSupplier SmSupplier=new SmSupplier();
				 SmSupplier.setSupplierId(27l);
				 SmSupplier.setSupplierName("Redah Jaya Sdn Bhd");
				 smSuppliers.add(SmSupplier);
				 
				 SmSupplier SmSupplier1=new SmSupplier();
				 SmSupplier1.setSupplierId(99l);
				 SmSupplier1.setSupplierName("MDEC malaysia");
				 smSuppliers.add(SmSupplier1);
				 this.getManagedBean().setSmSuppliers(smSuppliers);
		
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
		try {
			 ScQtBriefingRegistration briefReg = new ScQtBriefingRegistration();
			 briefReg.setSmSupplier(new SmSupplier());
			 briefReg.setSmMofAccount(new SmMofAccount());
			 briefReg.setBrandNew(false);
			List<ScQtBriefingRegistration> attendaceforComp = getManagedBean()
					.getBriefingRegistrationList();
			
			
			if (null == attendaceforComp) {
				attendaceforComp = new ArrayList<ScQtBriefingRegistration>();
			}
			
			attendaceforComp.add(briefReg);
			
			this.getManagedBean().setBriefingRegistrationList(
					attendaceforComp);
			this.getManagedBean().setBriefingRegistrationList(attendaceforComp);
			
			
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
	 * Method To call to save Briefing Site Visit Details
	 */
	public void saveBriefSiteVisit() {
		try {
			IQTBriefingSO qTBriefingSO = BusinessDelegate.getInstance().getServiceObject(
					QTConstants.SS_MODULE, IQTBriefingSO.class);
			BriefingReqDTO reqDTO = new BriefingReqDTO();
			List<ScQtBriefing> briefingDetails=new ArrayList<ScQtBriefing>();
			List<ScQtBriefing> siteVisitDetails=new ArrayList<ScQtBriefing>();
			
			// Save Briefing Details Status
			if (getManagedBean().getBriefingDetailsList() != null){
			    System.out.println("briefing list size"+ getManagedBean().getBriefingDetailsList().size());
			    
			    for (ScQtBriefing scQtBriefing:getManagedBean().getBriefingDetailsList()) {
			    	scQtBriefing.setScQtBriefingRegistrations(null);
			    	briefingDetails.add(scQtBriefing);
				 }
			       
			        reqDTO.setQtBriefingList(briefingDetails);
				   qTBriefingSO.saveBriefingSiteVisitList(reqDTO);
			  }
			
			// Save Site visit Details Status
	         if (getManagedBean().getSiteVisitDetailsList() != null){
			     System.out.println("site visit list size"+ getManagedBean().getSiteVisitDetailsList().size());
			    
			    for (ScQtBriefing scQtSitevisit:getManagedBean().getSiteVisitDetailsList()) {
			    	scQtSitevisit.setScQtBriefingRegistrations(null);
			    	siteVisitDetails.add(scQtSitevisit);
				}
			    
			        reqDTO.setQtBriefingList(siteVisitDetails);
				   qTBriefingSO.saveBriefingSiteVisitList(reqDTO);
			}
			
		      FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,"Record saved successfully.","Briefing Site Visit "));

		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException();
		}

	}
	
	/**
	 * Method To call to save Briefing Registration
	 */
	public void saveBrifingRegistration() {
		try {
			IQTBriefingSO qTBriefingSO = BusinessDelegate.getInstance().getServiceObject(
					QTConstants.SS_MODULE, IQTBriefingSO.class);
			BriefingReqDTO reqDTO = new BriefingReqDTO();
			List<ScQtBriefing> briefingDetails=new ArrayList<ScQtBriefing>();
			
			//  Briefing Registration 
			if (getManagedBean().getScQtBriefing() != null){
				
				ScQtBriefing scQtBrief=getManagedBean().getScQtBriefing() ;
				scQtBrief.setScQtBriefingRegistrations(getManagedBean().getBriefingRegistrationList());
				
			    briefingDetails.add(scQtBrief);
			    reqDTO.setQtBriefingList(briefingDetails);
			    	 
				qTBriefingSO.saveBriefingSiteVisitList(reqDTO);
			     FacesContext.getCurrentInstance().addMessage(null,
				    new FacesMessage(FacesMessage.SEVERITY_INFO,"Record saved successfully.","Briefing Registration. "));
		      }  
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException();
		}

	}
	
	/**
	 * Drop down for Briefing Site Visit DataTables
	 */
	public void getPmStatus() {
		StatusReqDTO reqDTO = new StatusReqDTO();
		String language = new FacesScopes().getUserLocale().getLanguage();
		IStatusSO statusSO = BusinessDelegate.getInstance()
				.getServiceObject("pm", IStatusSO.class);
	try{
		reqDTO.setModuleCode(QTConstants.BSV_MOCULE_CODE);
		reqDTO.setDocType(QTConstants.BSV_DOC_TYPE);
		reqDTO.setLanguageCode(language);
	
		StatusResDTO resDTO = statusSO.retrieveStatusDescList(reqDTO);
		
		List<PmStatusDesc> pmStatuslist = resDTO.getStatusDescList();
		
		System.out.println("list: " + pmStatuslist.size());
		
		this.getManagedBean().setPmStatus(pmStatuslist);
		
	} finally {
		statusSO = null;
	}
	}

}
