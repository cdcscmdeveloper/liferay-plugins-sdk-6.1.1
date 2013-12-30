package my.ep.web.backingbean.ss.bsvattendance;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.ss.quationtender.IQTBriefingSO;
import my.ep.biz.service.ss.quationtender.IQTDetailSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.ss.quationtender.BriefingReqDTO;
import my.ep.dto.ss.quationtender.BriefingResDTO;
import my.ep.dto.ss.quationtender.QuationTenderReqDTO;
import my.ep.dto.ss.quationtender.QuationTenderResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.sc.sitevisitproposaldata.SCSiteVisitProposalDataType;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.ss.quationtender.ScQt;
import my.ep.persistence.entity.ss.quationtender.ScQtBriefing;
import my.ep.persistence.entity.ss.quationtender.ScQtBriefingAttendance;
import my.ep.persistence.entity.ss.quationtender.ScQtBriefingRegistration;
import my.ep.type.pm.RoleCode;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.ss.quatationtender.QTDetailBackingBean;
import my.ep.web.backingbean.ss.quatationtender.SourcingBaseBackingBean;
import my.ep.web.constant.SourcingConst;
import my.ep.web.managedbean.ss.bsvattendance.BSVAttendanceManagedBean;
import my.ep.web.ss.quationtender.common.QTConstants;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import com.liferay.portal.util.PortalUtil;

/**
 * @author sri Briefing Attendance Backing Bean
 */
@ManagedBean(name = "bsvAttendanceBackingBean")
@RequestScoped
public class BSVAttendanceBackingBean extends
		SourcingBaseBackingBean<BSVAttendanceManagedBean> {
	private static final String NAV_TO_BSV_ATTENDANCE_MAIN_PAGE = "bsvAttendanceRegisterMain";
	public static Logger log = Logger.getLogger(BSVAttendanceBackingBean.class);

	public BSVAttendanceBackingBean() throws NamingException  {
		if (getManagedBean().getScQt() == null) {
			retrieveBriefingSiteVisitList(146L);	
		}
	}

	/**
	 * retrieving Briefing SiteVisit List
	 */
	public void retrieveBriefingSiteVisitList(Long qtId) {
		log.info("** retrieveBriefingSiteVisitList **");
		IQTBriefingSO qTBriefingSO = null;
		
		try {
			qTBriefingSO = BusinessDelegate.getInstance().getServiceObject(
					QTConstants.SS_MODULE, IQTBriefingSO.class);
			String languageCode = new FacesScopes().getUserLocale().getLanguage();
			
			BriefingReqDTO reqDTO = new BriefingReqDTO();
			reqDTO.setQtId(qtId);
			reqDTO.setLanguageCode(languageCode);

			BriefingResDTO resDTO = qTBriefingSO
					.retrieveBriefingSiteVisitDtl(reqDTO);
			
			// getting Briefing Same As Site Visit value
			ScQt scQt = resDTO.getScQt();
			getManagedBean().setScQt(scQt);

			// Briefing Details Listing
			List<ScQtBriefing> briefingDetailslist = resDTO
					.getBriefingResultBean().getResultList();
			getManagedBean().setBriefingDetailsList(briefingDetailslist);

			// Site Visit Listing
			List<ScQtBriefing> sitevisitlist = resDTO.getSiteVisitResultBean()
					.getResultList();
			getManagedBean().setSiteVisitDetailsList(sitevisitlist);
			
			// Status list
			getManagedBean().setPmStatus(resDTO.getStatusDescList());
			
			ScQtBriefingRegistration scQtBriefingRegistration = new ScQtBriefingRegistration();
			scQtBriefingRegistration.setSmSupplier(new SmSupplier());
			scQtBriefingRegistration.setSmMofAccount(new SmMofAccount());
			getManagedBean().setScQtBriefingRegistration(scQtBriefingRegistration);
		} catch (Exception ex) {
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							ex.toString(), ex.getMessage()));
		} finally {
			qTBriefingSO = null;
		}

	}

	/**
	 * Attendance By Company popup
	 */
	public void attendanceByCompany() {
		log.info("** attendanceByCompany **");

		retriveSupplierList();
		
		getManagedBean().setBriefingAttendanceList(null);
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
				 
				 List<ScQtBriefingRegistration> list =new ArrayList<ScQtBriefingRegistration>();
				 for(ScQtBriefingRegistration scqtbriefReg :briefRegList){
					 scqtbriefReg.setSmMofAccount(new SmMofAccount());
					 list.add(scqtbriefReg);
				 }
				 
				 this.getManagedBean().setBriefingRegistrationList(list);
				
				 retriveSupplierList();
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
			ScQtBriefing scQtBriefing = getManagedBean().getScQtBriefing();
			 ScQtBriefingRegistration briefReg = new ScQtBriefingRegistration();
			 briefReg.setSmSupplier(new SmSupplier());
			 briefReg.setSmMofAccount(new SmMofAccount());
			briefReg.setScQtBriefing(scQtBriefing);
			 
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
			ScQtBriefingRegistration selectedbriefRegistration = getManagedBean().getSelectedbriefRegistration();
			List<ScQtBriefingAttendance> attendaceDetails = selectedbriefRegistration.getScQtBriefingAttendances();
			if (null == attendaceDetails) {
				attendaceDetails = new ArrayList<ScQtBriefingAttendance>();
				selectedbriefRegistration.setScQtBriefingAttendances(attendaceDetails);
			}
			attendacelist.setScQtBriefingRegistration(selectedbriefRegistration);
			attendaceDetails.add(attendacelist);
	
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
	 * getting Approver drop down listing
	 */
	public List<PmUser> getApproverNames() {
		List<PmUser> approverUsers = this.getManagedBean().getApproverNames();
		
		if (approverUsers == null) {
			IUserSO userSO = null;
			try {
				userSO = BusinessDelegate.getInstance().getServiceObject(
						QTConstants.PM_MODULE, IUserSO.class);
				
				// Get Approver Users
				UserReqDTO reqDTO = new UserReqDTO();
				reqDTO.setRoleCode(RoleCode.APPROVER.value());

				UserResDTO resDTO = userSO.retrieveUserListByRole(reqDTO);
				approverUsers = resDTO.getUserList();
				System.out.println("Approver List: " + approverUsers.size());
				this.getManagedBean().setApproverNames(approverUsers);

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

		return approverUsers;
	}
	
	/**
	 * Method To call to save Briefing Site Visit Details
	 */
	public void saveBriefSiteVisit() {
		IQTBriefingSO qTBriefingSO = null;
		try {
			qTBriefingSO = BusinessDelegate.getInstance().getServiceObject(
					QTConstants.SS_MODULE, IQTBriefingSO.class);
			List<ScQtBriefing> briefingDetails=new ArrayList<ScQtBriefing>();
			
			// Save Briefing Details Status
			List<ScQtBriefing> list = getManagedBean().getBriefingDetailsList();
			if (list != null){
			    System.out.println("briefing list size"+ list.size());
			    briefingDetails.addAll(list);
			}
			
			// Save Site visit Details Status
			list = getManagedBean().getSiteVisitDetailsList();
	        if (list != null){
			     System.out.println("site visit list size"+ list.size());
			     briefingDetails.addAll(list);
			}
	        
	        for (ScQtBriefing scQtBriefing : briefingDetails) {
	        	scQtBriefing.setScQtBriefingRegistrations(null);
			}
	        
	        if (briefingDetails.size() > 0) {
	        	BriefingReqDTO reqDTO = new BriefingReqDTO();
	        	reqDTO.setQtBriefingList(briefingDetails);
		        qTBriefingSO.saveQTDtl(reqDTO);
		        
		        ScQt scQt = getManagedBean().getScQt();
		        
		        renewSesionObject();
		        
		        retrieveBriefingSiteVisitList(scQt.getQtId());
	        }
	        FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,"Record saved successfully.","Briefing Site Visit "));

		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException();
		} finally {
			qTBriefingSO = null;
		}

	}
	
	/**
	 * Method To call to save Briefing Registration
	 */
	public void saveBriefingAttendance() {
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
			    	 
				qTBriefingSO.saveQTDtl(reqDTO);
				
				ScQt scQt = getManagedBean().getScQt();
				
				renewSesionObject();
				
		        retrieveBriefingSiteVisitList(scQt.getQtId());
		        
			     FacesContext.getCurrentInstance().addMessage(null,
				    new FacesMessage(FacesMessage.SEVERITY_INFO,"Record saved successfully.","Briefing Registration. "));
		      }  
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException();
		}

	}
	
	/**
	 * Method To call to save Briefing Registration by company
	 */
	public void saveBriefingAttendanceByCompany() {
		IQTBriefingSO qTBriefingSO;
		try {
			qTBriefingSO = BusinessDelegate.getInstance().getServiceObject(
					QTConstants.SS_MODULE, IQTBriefingSO.class);
			BriefingReqDTO reqDTO = new BriefingReqDTO();
			List<ScQtBriefing> briefingDetails = new ArrayList<ScQtBriefing>();
			List<ScQtBriefing> selectedBriefingDetails = getManagedBean().getSelectedBriefingDetails();
			List<ScQtBriefing> selectedSiteVisitDetails = getManagedBean().getSelectedSiteVisitDetails();
			
			if (selectedBriefingDetails != null){
				briefingDetails.addAll(selectedBriefingDetails);
			}
			
			if (selectedSiteVisitDetails != null){
				briefingDetails.addAll(selectedSiteVisitDetails);
			}
			
			if (briefingDetails.size() > 0) {
				ScQtBriefingRegistration briefingRegistration = getManagedBean().getScQtBriefingRegistration();
				List<ScQtBriefingAttendance> briefingAttendanceList = getManagedBean().getBriefingAttendanceList();
				List<ScQtBriefingRegistration> scQtBriefingRegistrations = null;
				List<ScQtBriefingAttendance> scQtBriefingAttendances = null;
				
				for (ScQtBriefing scQtBriefing : briefingDetails) {
					ScQtBriefingRegistration scQtBriefingRegistration = (ScQtBriefingRegistration)BeanUtils
							.cloneBean(briefingRegistration);
					scQtBriefingRegistration.setScQtBriefing(scQtBriefing);
					
					scQtBriefingRegistrations = new ArrayList<ScQtBriefingRegistration>();
					scQtBriefingRegistrations.add(scQtBriefingRegistration);
					scQtBriefing.setScQtBriefingRegistrations(scQtBriefingRegistrations);
					
					scQtBriefingAttendances = new ArrayList<ScQtBriefingAttendance>();
					for (ScQtBriefingAttendance briefingAttendance : briefingAttendanceList) {
						ScQtBriefingAttendance scQtBriefingAttendance = (ScQtBriefingAttendance)BeanUtils
								.cloneBean(briefingAttendance);
						
						scQtBriefingAttendance.setScQtBriefingRegistration(scQtBriefingRegistration);
						scQtBriefingAttendances.add(scQtBriefingAttendance);
					}
					scQtBriefingRegistration.setScQtBriefingAttendances(scQtBriefingAttendances);
				}
				
			    reqDTO.setQtBriefingList(briefingDetails);
			    	 
				qTBriefingSO.saveQTDtl(reqDTO);
				
				ScQt scQt = getManagedBean().getScQt();
				
				renewSesionObject();
				
		        retrieveBriefingSiteVisitList(scQt.getQtId());

		        FacesContext.getCurrentInstance().addMessage(null,
					    new FacesMessage(FacesMessage.SEVERITY_INFO,"Record saved successfully.","Briefing Registration. "));
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException();
		} finally {
			qTBriefingSO = null;
		}

	}
	
	private void retriveSupplierList() {
		System.out.println("retriveSupplierList");
		// drop down listing for mof nor and sm supplier 
		List<SmMofAccount> mofNors =new  ArrayList<SmMofAccount>();
		SmMofAccount SmMofAccount1 =new  SmMofAccount();
		SmMofAccount1.setMofNo("MOF000002");
		SmMofAccount1.setMofAccountId(2);
		mofNors.add(SmMofAccount1);
		
		List<SmSupplier> smSuppliers = new ArrayList<SmSupplier>();
		SmSupplier SmSupplier=new SmSupplier();
		SmSupplier.setSupplierId(27l);
		SmSupplier.setSupplierName("Redah Jaya Sdn Bhd");
		List<SmMofAccount> smMofAccounts1 = new ArrayList<SmMofAccount>();
		smMofAccounts1.add(SmMofAccount1);
		SmMofAccount1.setSmSupplier(SmSupplier);
		SmSupplier.setSmMofAccounts(smMofAccounts1);
		smSuppliers.add(SmSupplier);
		
		SmMofAccount SmMofAccount2 =new  SmMofAccount();
		SmMofAccount2.setMofNo("MOF000004");
		SmMofAccount2.setMofAccountId(4);
		mofNors.add(SmMofAccount2);
		this.getManagedBean().setSmMofAccounts(mofNors);
		 
		SmSupplier SmSupplier1=new SmSupplier();
		SmSupplier1.setSupplierId(99l);
		SmSupplier1.setSupplierName("MDEC malaysia");
		smSuppliers.add(SmSupplier1);
		List<SmMofAccount> smMofAccounts2 = new ArrayList<SmMofAccount>();
		SmMofAccount2.setSmSupplier(SmSupplier1);
		smMofAccounts2.add(SmMofAccount2);
		SmSupplier.setSmMofAccounts(smMofAccounts2);
		this.getManagedBean().setSmSuppliers(smSuppliers);
	}
	
	public void preRenderView() {
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		this.getManagedBean().setTaskId(taskId);

		log.info("taskId : " + taskId);
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);
		// TODO
		FacesScopes facesScopes = new FacesScopes();
		String ctx = facesScopes.getBPMAuthenticationCtxToken();
		// String ctx = bpmSO.authenticate("roslan", "welcome1");
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setCtxToken(ctx);
		reqDTO.setTaskId(taskId);
		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
		Object obj = res.getPojo();
		this.getManagedBean().setOutComes(res.getAction());
		SCSiteVisitProposalDataType siteVisitDatatype = (SCSiteVisitProposalDataType) obj;
		this.getManagedBean().setSiteVisitPayLoad(siteVisitDatatype);
		IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(QTConstants.SS_MODULE, IQTDetailSO.class);
		QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
		tenderReqDTO.setQtId(new Long(siteVisitDatatype.getQtId())
				.longValue());
		QuationTenderResDTO tenderResDTO = detailSO
				.retriveQTDetails(tenderReqDTO);
		QTDetailBackingBean detailBackingBean = new QTDetailBackingBean();
		detailBackingBean.getManagedBean().setQt(tenderResDTO.getQtDetail());
		populateParentOrgProfile(tenderResDTO.getQtDetail());
		retrieveBriefingSiteVisitList(siteVisitDatatype.getQtId().longValue());
		
				FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(
						fc,
						null,
						NAV_TO_BSV_ATTENDANCE_MAIN_PAGE
								);
	}

public String updateBpmTask(String action) {
		System.out.println("Succeesfully update the task");
		BriefingReqDTO briefingReqDTO = prepareBriefingDtls();
		briefingReqDTO.setOutcome(action);
		briefingReqDTO.setTaskId(this.getManagedBean().getTaskId());
		FacesScopes facesScopes = new FacesScopes();
		String ctx = facesScopes.getBPMAuthenticationCtxToken();
		briefingReqDTO.setToken(ctx);
		briefingReqDTO.setPayLoad(this.getManagedBean().getSiteVisitPayLoad());
		IQTBriefingSO briefingSO = getBriefingSO();
		try{
		BriefingResDTO resDTO = briefingSO.updateBPMAction(briefingReqDTO);
		if (resDTO.isSuccessful()) {

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Record saved successfully.", " Type. : "

					));

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/shared/acknowledge.xhtml");

		}else{
			throw new EpException("");
		}
		}
		catch(Exception ex){
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Unable to process now",""

					));
		}
		return null;
	}

	private IQTBriefingSO getBriefingSO() {
		return EpEjbBusinessDelegate.getInstance().getServiceObject(
				SourcingConst.SOURCING_MODULE, IQTBriefingSO.class);

	}
	private BriefingReqDTO prepareBriefingDtls(){
		List<ScQtBriefing> briefingDetails=new ArrayList<ScQtBriefing>();
		
		// Save Briefing Details Status
		List<ScQtBriefing> list = getManagedBean().getBriefingDetailsList();
		if (list != null){
		    System.out.println("briefing list size"+ list.size());
		    briefingDetails.addAll(list);
		}
		
		// Save Site visit Details Status
		list = getManagedBean().getSiteVisitDetailsList();
        if (list != null){
		     System.out.println("site visit list size"+ list.size());
		     briefingDetails.addAll(list);
		}
        
        for (ScQtBriefing scQtBriefing : briefingDetails) {
        	scQtBriefing.setScQtBriefingRegistrations(null);
		}
        
               	BriefingReqDTO reqDTO = new BriefingReqDTO();
        	reqDTO.setQtBriefingList(briefingDetails);
	
        
        return reqDTO;
	}
	
}
