package my.ep.web.scheduler.job.sm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import my.ep.constant.EpPropertiesConst;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.dto.sm.personnelauthorization.PersonnelAuthorizationReqDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;

public class CheckBasicAccountExpirationJob implements Job {
	public static Logger log = EpLogger.getLogger(CheckBasicAccountExpirationJob.class);

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
		updateLiferayCommentsForAllSupplierUsers();
		
		sendExpirationReminder();
		
		terminateExpiredBasicSupplier();
	}
	
	/**
	 * to set the supplier role in Liferay's user's comments section
	 * this reminder only run based on the date configured in ngep.properties 
	 */
	private void updateLiferayCommentsForAllSupplierUsers() {
		
	}
	
	/**
	 * to send notification to all basic supplier users 
	 * who nearing to the 1 year not login period.
	 */
	private void sendExpirationReminder() {
		List<User> userList = retrieveNearingExpiredBasicSupplierUsersFromLiferay();
		for(User user : userList) {
//			sendNotification(user.getScreenName(), "NOTIFICATION_ID", "Param List");
		}
	}
	
	private void sendNotification(String userId, String notificationId, List<String> params) {
		// build the send notification logic here
	}
	
	/**
	 * retrieve expired basic_supplier_admin,
	 * check the other login users in the company also expired
	 * if expired as well then terminate the basic account. 
	 */
	private void terminateExpiredBasicSupplier() {
		log.info("In terminateExpiredBasicSupplier");
		
		List<User> userList = retrieveExpiredBasicSupplierAdminFromLiferay();

		for(User user : userList) {
			log.info("Expired BASIC_SUPPLIER_ADMIN: " + user.getScreenName());
			
			PersonnelReqDTO reqDTO = new PersonnelReqDTO(); 
			reqDTO.setLoginId(user.getScreenName());
			PersonnelResDTO resDTO = SmServiceLookup.getPersonnelSO().retrieveRelatedPersonnelsByLoginId(reqDTO);
			
			if(resDTO.getSmPersonnelList().size() == 1) {
				if(resDTO.getSmPersonnelList().get(0).getPmUser().getLoginId().equals(user.getScreenName())) {
	 				// TODO: terminate this supplier, as this supplier only has 1 user that already not login more than 1 year	
					PersonnelAuthorizationReqDTO authReqDTO = new PersonnelAuthorizationReqDTO();
					authReqDTO.setLoginId(user.getScreenName());
					SmServiceLookup.getPersonnelAuthorizationSO().terminateBasicAccount(authReqDTO);
				}
			} else if(resDTO.getSmPersonnelList().size() > 1) {
				int count = 0;
				
				for(SmPersonnel personnel : resDTO.getSmPersonnelList()) {
					try {
						if(!resDTO.getSmPersonnelList().get(0).getPmUser().getLoginId().equals(user.getScreenName())) {
							User otherSupplierUser = UserLocalServiceUtil.getUserByScreenName(
									Long.valueOf(EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.PORTAL_COMPANY_ID)), 
									personnel.getPmUser().getLoginId());
							
							Date current = new Date();
							Date oneYearBefore = new Date(current.getYear() - 1, current.getMonth(), current.getDate(), 0, 0, 0);
							if(otherSupplierUser.getLastLoginDate().after(oneYearBefore)) {
								count++;
							}
						}
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (PortalException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SystemException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}					
				}
				
				if(count == (resDTO.getSmPersonnelList().size())) {
					// TODO: terminate this supplier as all the user already not login more than 1 year
					// sendNotification(String supplierAdminUserId, String terminateNotificationId, List<String> params)
					PersonnelAuthorizationReqDTO authReqDTO = new PersonnelAuthorizationReqDTO();
					authReqDTO.setLoginId(user.getScreenName());
					SmServiceLookup.getPersonnelAuthorizationSO().terminateBasicAccount(authReqDTO);
				}
			}
		}
	}
	
	/**
	 * retrieve list of expired BASIC SUPPLIER ADMIN from Liferay
	 * because only Liferay has the last login date.
	 * @return
	 */
	private List<User> retrieveExpiredBasicSupplierAdminFromLiferay() {
		log.info("In terminateExpiredBasicSupplier");
		
		Date current = new Date();
		Date oneYearBefore = new Date(current.getYear() - 1, current.getMonth(), current.getDate(), 0, 0, 0);
		log.info("1 year before: " + oneYearBefore);
				
		DynamicQuery dynamicQuery = UserLocalServiceUtil.dynamicQuery();
		
		// if last login date is not null, last login date is 1 year before
		Criterion criterion1 = null;
		criterion1 = RestrictionsFactoryUtil.isNotNull("lastLoginDate");
		criterion1 = RestrictionsFactoryUtil.and(criterion1, RestrictionsFactoryUtil.le("lastLoginDate", oneYearBefore));
		
		// if last login date is null, creation date is 1 year before.
		Criterion criterion2 = null;
		criterion2 = RestrictionsFactoryUtil.isNull("lastLoginDate");
		criterion2 = RestrictionsFactoryUtil.and(criterion2, RestrictionsFactoryUtil.le("createDate", oneYearBefore));
		
		// user's comments contain "BASIC_SUPPLIER_ADMIN"
		Criterion criterion3 = null;
		criterion3 = RestrictionsFactoryUtil.like("comments", "%BASIC_SUPPLIER_ADMIN%");
		
//		Criterion criterion4 = null;
//		criterion4 = RestrictionsFactoryUtil.like("screenName", "812345678916");
		
		Criterion criterionAll = null;
		criterionAll = RestrictionsFactoryUtil.or(criterion1, criterion2);
		criterionAll = RestrictionsFactoryUtil.and(criterionAll, criterion3);
		
//		criterionAll = RestrictionsFactoryUtil.and(criterion4, criterion3);
		dynamicQuery.add(criterionAll);
		
		List<User> userList = new ArrayList<User>();
		try {
			userList = UserLocalServiceUtil.dynamicQuery(dynamicQuery);	
			log.info("number of expired BASIC_SUPPLIER_ADMIN: " + userList.size());
		} catch(SystemException se) {
			log.error(se.getMessage());
		}
		
		return userList;
	}
	
	/**
	 * retrieve all supplier users that are nearing to expire from Liferay.
	 * @return
	 */
	private List<User> retrieveNearingExpiredBasicSupplierUsersFromLiferay() {
		log.info("In terminateExpiredBasicSupplier");
		
		Date current = new Date();
		Date oneYearBefore = new Date(current.getYear(), current.getMonth() -3, current.getDate());
		log.info("3 months before: " + oneYearBefore);
				
		DynamicQuery dynamicQuery = UserLocalServiceUtil.dynamicQuery();
		
		// if last login date is not null, last login date is 1 year before
		Criterion criterion1 = null;
		criterion1 = RestrictionsFactoryUtil.isNotNull("lastLoginDate");
		criterion1 = RestrictionsFactoryUtil.and(criterion1, RestrictionsFactoryUtil.eq("lastLoginDate", oneYearBefore));
		
		// if last login date is null, creation date is 1 year before.
		Criterion criterion2 = null;
		criterion2 = RestrictionsFactoryUtil.isNull("lastLoginDate");
		criterion2 = RestrictionsFactoryUtil.and(criterion2, RestrictionsFactoryUtil.eq("createDate", oneYearBefore));
		
		// user's comments contain "BASIC_SUPPLIER_ADMIN"
		Criterion criterion3 = null;
		criterion3 = RestrictionsFactoryUtil.like("comments", "%BASIC_SUPPLIER_%");

		// user's comments contain "BASIC_SUPPLIER_ADMIN"
		Criterion criterion4 = null;
		criterion4 = RestrictionsFactoryUtil.like("comments", "%CM_USER%");
		
		Criterion criterionDate = RestrictionsFactoryUtil.or(criterion1, criterion2);
		Criterion criterionRole = RestrictionsFactoryUtil.or(criterion3, criterion4);
		
		Criterion criterionAll = RestrictionsFactoryUtil.and(criterionDate, criterionRole);
		
		List<User> userList = new ArrayList<User>();
		try {
			userList = UserLocalServiceUtil.dynamicQuery(dynamicQuery);	
			log.info("number of nearing expired BASIC SUPPLIER users: " + userList.size());
		} catch(SystemException se) {
			log.error(se.getMessage());
		}
		
		return userList;
	}

}
