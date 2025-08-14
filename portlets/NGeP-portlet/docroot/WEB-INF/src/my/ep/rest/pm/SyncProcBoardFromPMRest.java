package my.ep.rest.pm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.type.pm.RoleCode;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.log4j.Logger;

@Path("/sync")
public class SyncProcBoardFromPMRest {

	public static Logger log = Logger.getLogger(SyncProcBoardFromPMRest.class);
	
	@GET
	@Path("syncProcBoardUser")
	public Response syncLiferayAndOIMfromPMforPB() {
		UserReqDTO reqDTO = null;
		UserResDTO resDTO = null;
		String loginId = null;
		Long userId = null;
		List<PmUser> pbChairpersonList = null;
		List<PmUser> pbMemberList = null;
		List<PmUser> pbSecretaryList = null;
		List<HashMap<String, String>> failedUser = new ArrayList<HashMap<String, String>>();
		List<HashMap<String, String>> successUser = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> user = null;
		int count = 0;
		String liferayRole = null;
		try {
			log.info("#25278 Before Sync");
			pbChairpersonList = retrievePBChairpersonList();
			if(pbChairpersonList != null && !pbChairpersonList.isEmpty())
			{
				log.info("------ PB Chairperson ------");
				for(PmUser chairperson: pbChairpersonList) {
					user = new HashMap<String, String>();
					if(chairperson.getLiferayRoles() != null) {
						for(String roles:chairperson.getLiferayRoles())
						{
							if(liferayRole == null)
								liferayRole = roles;
							else
								liferayRole = liferayRole + "," + roles;
						}
					}
					if(liferayRole != null)
						user.put("Liferay Role", liferayRole);
					else
						user.put("Liferay Role", "-NULL-");
					liferayRole = null;
					loginId = chairperson.getLoginId();
					userId = chairperson.getUserId();
					reqDTO = new UserReqDTO();
					reqDTO.setLoginId(loginId);
					reqDTO.setUserId(userId);
					reqDTO.setSupplierUser(false);
					try {
						count = count + 1;
						resDTO = SmServiceLookup.getUserSO().syncLiferayAndOimRoleFromPm(reqDTO);
						user.put("User", loginId);
						user.put("Result", String.valueOf(resDTO.isSuccessful()));
						user.put("Role","Chairperson");
						successUser.add(user);
						
					} catch(Exception e) {
						user.put("User", loginId);
						user.put("Result", e.getMessage());
						user.put("Role","Chairperson");
						failedUser.add(user);					
					}
					if(count == 10)
						break;
				}
			}
			count = 0;
			liferayRole = null;
			pbMemberList = retrievePBMemberList();
			if(pbMemberList != null && !pbMemberList.isEmpty())
			{
				log.info("------ PB Member ------");
				for(PmUser member: pbMemberList) {
					user = new HashMap<String, String>();
					if(member.getLiferayRoles() != null) {
						for(String roles:member.getLiferayRoles())
						{
							if(liferayRole == null)
								liferayRole = roles;
							else
								liferayRole = liferayRole + "," + roles;
						}
					}
					if(liferayRole != null)
						user.put("Liferay Role", liferayRole);
					else
						user.put("Liferay Role", "-NULL-");
					liferayRole = null;
					loginId = member.getLoginId();
					userId = member.getUserId();
					reqDTO = new UserReqDTO();
					reqDTO.setLoginId(loginId);
					reqDTO.setUserId(userId);
					reqDTO.setSupplierUser(false);
					try {
						count = count + 1;
						resDTO = SmServiceLookup.getUserSO().syncLiferayAndOimRoleFromPm(reqDTO);
						user.put("User", loginId);
						user.put("Result", String.valueOf(resDTO.isSuccessful()));
						user.put("Role","Member");
						successUser.add(user);
					} catch(Exception e) {
						user.put("User", loginId);
						user.put("Result", e.getMessage());
						user.put("Role","Member");
						failedUser.add(user);
					}
					
					if(count == 10)
						break;
				}
			}
			count = 0;
			liferayRole = null;
			pbSecretaryList = retrievePBSecretaryList();
			if(pbSecretaryList != null && !pbSecretaryList.isEmpty())
			{
				log.info("------ PB Secretary ------");
				for(PmUser secretary: pbSecretaryList) {
					user = new HashMap<String, String>();
					if(secretary.getLiferayRoles() != null) {
						for(String roles:secretary.getLiferayRoles())
						{
							if(liferayRole == null)
								liferayRole = roles;
							else
								liferayRole = liferayRole + "," + roles;
						}
					}
					if(liferayRole != null)
						user.put("Liferay Role", liferayRole);
					else
						user.put("Liferay Role", "-NULL-");
					liferayRole = null;
					loginId = secretary.getLoginId();
					userId = secretary.getUserId();
					reqDTO = new UserReqDTO();
					reqDTO.setLoginId(loginId);
					reqDTO.setUserId(userId);
					reqDTO.setSupplierUser(false);
					try {
						count = count + 1;
						resDTO = SmServiceLookup.getUserSO().syncLiferayAndOimRoleFromPm(reqDTO);
						user.put("User", loginId);
						user.put("Result", String.valueOf(resDTO.isSuccessful()));
						user.put("Role","Secretary");
						successUser.add(user);
					} catch(Exception e) {
						user.put("User", loginId);
						user.put("Result", e.getMessage());
						user.put("Role","Secretary");
						failedUser.add(user);
					}
					
					if(count == 10)
						break;
				}
			}
			log.info("List of Success User");
			for(HashMap<String, String> result: successUser) {
				log.info(result.get("User")+ ":" + result.get("Result") + ", " + result.get("Role") + ", " + result.get("Liferay Role"));
			}
			log.info("List of Failed User");
			for(HashMap<String, String> result: failedUser) {
				log.info(result.get("User")+ ":" + result.get("Result") + ", " + result.get("Role") + ", " + result.get("Liferay Role"));
			}
			log.info("#25278 - Finished Sync");
			return Response.status(200).entity("Success sync Procurement Board users !!").build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(406).entity(e.getMessage()).build();
		}
	}
	
	private List<PmUser> retrievePBChairpersonList() {
		UserReqDTO reqDTO = new UserReqDTO();
		UserResDTO resDTO = null;
		List<String> roleCodes = new ArrayList<String>();
		List<PmUser> chairpersonList = null;
		try{		
		roleCodes.add(RoleCode.PPB_CHAIRPERSON.value());
		reqDTO.setRoleCodes(roleCodes);
		resDTO = SmServiceLookup.getUserSO().retrieveUserListByRole(reqDTO);
		chairpersonList = resDTO.getUserList();
		log.info("PB Chairperson: " + chairpersonList.size());
		/*for (PmUser user : chairpersonList) {
			System.out.println(user.getUserId() + "|"
					+ user.getUserName());
		}*/

		} catch(Exception e) {
			e.printStackTrace();
		}
		return chairpersonList;
	}
	
	private List<PmUser> retrievePBMemberList() {
		UserReqDTO reqDTO = new UserReqDTO();
		UserResDTO resDTO = null;
		List<String> roleCodes = new ArrayList<String>();
		List<PmUser> memberList = null;
		try{		
		roleCodes.add(RoleCode.PPB_MEMBER.value());
		reqDTO.setRoleCodes(roleCodes);
		resDTO = SmServiceLookup.getUserSO().retrieveUserListByRole(reqDTO);
		memberList = resDTO.getUserList();
		log.info("PB Member: " + memberList.size());
		/*for (PmUser user : memberList) {
			System.out.println(user.getUserId() + "|"
					+ user.getUserName());
		}*/

		} catch(Exception e) {
			e.printStackTrace();
		}
		return memberList;
	}
	private List<PmUser> retrievePBSecretaryList() {
		UserReqDTO reqDTO = new UserReqDTO();
		UserResDTO resDTO = null;
		List<String> roleCodes = new ArrayList<String>();
		List<PmUser> secretaryList = null;
		try{		
		roleCodes.add(RoleCode.PPB_SECRETARY.value());
		reqDTO.setRoleCodes(roleCodes);
		resDTO = SmServiceLookup.getUserSO().retrieveUserListByRole(reqDTO);
		secretaryList = resDTO.getUserList();
		log.info("PB Secretary: " + secretaryList.size());
		/*for (PmUser user : secretaryList) {
			System.out.println(user.getUserId() + "|"
					+ user.getUserName());
		}*/

		} catch(Exception e) {
			e.printStackTrace();
		}
		return secretaryList;
	}
	
	@GET
	@Path("/user/{loginId}")
	public Response syncLiferayAndOIMFromPMRoleByLoginId(@PathParam("loginId") String loginId) {
		UserReqDTO reqDTO = null;
		UserResDTO resDTO = null;
		Long userId = null;
		try {
			log.info("#25278 Before Sync By Login Id");
			reqDTO = new UserReqDTO();
			reqDTO.setLoginId(loginId);
			resDTO = SmServiceLookup.getUserSO().retrieveUserByLoginId(reqDTO);
			if(resDTO.isSuccessful())
			{
				userId = resDTO.getPmUser().getUserId();
				reqDTO = new UserReqDTO();
				reqDTO.setLoginId(loginId);
				reqDTO.setUserId(userId);
				reqDTO.setSupplierUser(false);
				resDTO = SmServiceLookup.getUserSO().syncLiferayAndOimRoleFromPm(reqDTO);
				log.info("Sync user by login Id: " + resDTO.isSuccessful());
			}
			
			log.info("#25278 Finished Sync By Login Id");			
			return Response.status(200).entity("Success sync Procurement Board users !!").build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(406).entity(e.getMessage()).build();
		}
	}
	///START 32821 TO CREATE USER IF ONLY EXIST IN PM
	@GET
	@Path("/createuser/{loginId}")
	public Response synccreateLiferayAndOIMFromPMRoleByLoginId(@PathParam("loginId") String loginId) {
		UserReqDTO reqDTO = null;
		UserResDTO resDTO = null;
		Long userId = null;
		try {
			log.info("#Start Sync By Login Id");
			reqDTO = new UserReqDTO();
			reqDTO.setLoginId(loginId);
			reqDTO.setDefaultPassword(true);
			resDTO = SmServiceLookup.getUserSO().retrieveUserByLoginId(reqDTO);
			log.info("TB DEV ISLAND: AFTER GET USER");
			if(resDTO.getPmUser() == null){
				log.info("TB DEV ISLAND: GET USER INACTIVE");
				resDTO = SmServiceLookup.getUserSO().retrieveUserByLoginIdInactive(reqDTO);
			}
			if(resDTO.isSuccessful() && resDTO.getPmUser()!=null)
			{	
				userId = resDTO.getPmUser().getUserId();log.info("TB DEV ISLAND:USERID="+userId);
				reqDTO = new UserReqDTO();
				reqDTO.setLoginId(loginId);
				reqDTO.setUserId(userId);
				reqDTO.setDefaultPassword(true);
				reqDTO.setSupplierUser(false);
				log.info("TB DEV ISLAND:START TO SYNC");
				resDTO = SmServiceLookup.getUserSO().createnSyncLiferayAndOimRoleFromPm(reqDTO);
				log.info("End Sync user by login Id: " + resDTO.isSuccessful());
			}else{
				log.info("TB DEV ISLAND:RESPONS IS FAIL TO GET USER OR USER IS NULL");
			}
			
			log.info("Finished Sync By Login Id");			
			return Response.status(200).entity("Success sync Procurement Board users !!").build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(406).entity(e.getMessage()).build();
		}
	}
	///END 32821 TO CREATE USER IF ONLY EXIST IN PM
	// START 32821 - PDS 
	/**
	 * Synchronizes user information from PM to Liferay and OIM based on the provided login ID.
	 * Optionally sets the user's password to the default value if specified.
	 *
	 * <p>This endpoint is typically used to re-sync a user's account details and roles
	 * between systems when discrepancies are detected or after updates in PM.</p>
	 *
	 * <b>HTTP Method:</b> GET  
	 * <b>Path:</b> /createuserv2  
	 *
	 * <b>Query Parameters:</b>
	 * <ul>
	 *   <li><b>loginId</b> (required)  The login ID of the user to synchronize.</li>
	 *   <li><b>setDefaultPassword</b> (optional)  If set to "true" (case-insensitive),
	 *       the user's password will be reset to the system's default password.</li>
	 * </ul>
	 *
	 * <b>Sample URLs:</b>
	 * <pre>
	 *   /createuserv2?loginId=john.doe
	 *   /createuserv2?loginId=john.doe&setDefaultPassword=true
	 *   http://ngepsit.eperolehan.com.my/NGeP-portlet/rest/sync/createuserv2?loginId=3003703
	 *   http://localhost:8080/NGeP-portlet/rest/sync/createuserv2?loginId=3003703
	 * </pre>
	 *
	 * @param loginId the login ID of the user to synchronize (required).
	 * @param setDefaultPassword "true" to reset the password to default; otherwise ignored (optional).
	 * @return HTTP 200 if synchronization is successful, HTTP 406 with an error message if failed.
	 */
	@GET
	@Path("/createuserv2")
	public Response syncLiferayOimByLoginId(
	        @QueryParam("loginId") String loginId,
	        @QueryParam("setDefaultPassword") String setDefaultPassword) {
		UserReqDTO reqDTO = null;
		try {
			log.info("#Start Sync By Login Id");
			reqDTO = new UserReqDTO();
			reqDTO.setLoginId(loginId);
			boolean setDefaultPwd = false;
			if(setDefaultPassword != null && "true".equalsIgnoreCase(setDefaultPassword)){
				setDefaultPwd = true;
			}
			boolean success = SmServiceLookup.getUserSO().syncLiferayOimByLoginId(loginId,setDefaultPwd);
			log.info("Finished Sync By Login Id:"+success);			
			return Response.status(200).entity("Success sync Procurement Board users !!").build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(406).entity(e.getMessage()).build();
		}
	} 
	// End 32821 - PDS  
	
}
