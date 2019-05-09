package my.ep.rest.pm;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.type.pm.RoleCode;
import my.ep.web.util.sm.helper.SmServiceLookup;

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
		try {
			log.info("#25278 Before Sync");
			pbChairpersonList = retrievePBChairpersonList();
			/*if(pbChairpersonList != null && !pbChairpersonList.isEmpty())
			{
				log.info("------ PB Chairperson ------");
				for(PmUser chairperson: pbChairpersonList) {
					loginId = chairperson.getLoginId();
					userId = chairperson.getUserId();
					reqDTO = new UserReqDTO();
					reqDTO.setLoginId(loginId);
					reqDTO.setUserId(userId);
					reqDTO.setSupplierUser(false);
					try {
						resDTO = userSO.syncLiferayAndOimRoleFromPm(reqDTO);
					} catch(Exception e) {
						System.out.println(loginId + " : Failed");
					}
					
					log.info(loginId + " : " + resDTO.isSuccessful());
				}
			}*/
			pbMemberList = retrievePBMemberList();
			if(pbMemberList != null && !pbMemberList.isEmpty())
			/*{
				log.info("------ PB Member ------");
				for(PmUser member: pbMemberList) {
					loginId = member.getLoginId();
					userId = member.getUserId();
					reqDTO = new UserReqDTO();
					reqDTO.setLoginId(loginId);
					reqDTO.setUserId(userId);
					reqDTO.setSupplierUser(false);
					try {
						resDTO = userSO.syncLiferayAndOimRoleFromPm(reqDTO);
					} catch(Exception e) {
						log.info(loginId + " : Failed");
					}
					
					log.info(loginId + " : " + resDTO.isSuccessful());
				}
			}*/
			pbSecretaryList = retrievePBSecretaryList();
			if(pbSecretaryList != null && !pbSecretaryList.isEmpty())
			{
				log.info("------ PB Secretary ------");
				for(PmUser secretary: pbSecretaryList) {
					loginId = secretary.getLoginId();
					log.info("LoginId: " + loginId);
					userId = secretary.getUserId();
					log.info("UserId: " + userId);
					reqDTO = new UserReqDTO();
					reqDTO.setLoginId(loginId);
					reqDTO.setUserId(userId);
					reqDTO.setSupplierUser(false);
					try {
						resDTO = SmServiceLookup.getUserSO().syncLiferayAndOimRoleFromPm(reqDTO);
						log.info(loginId + " : " + resDTO.isSuccessful());
						break;
					} catch(Exception e) {
						log.info(loginId + " : Failed");
						break;
					}
					
					//System.out.println(loginId + " : " + resDTO.isSuccessful());
				}
			}
			log.info("#25014 - Finished Sync");
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
		roleCodes.add(RoleCode.PPB_CHAIRPERSON.value());;
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
		roleCodes.add(RoleCode.PPB_MEMBER.value());;
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
		roleCodes.add(RoleCode.PPB_SECRETARY.value());;
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
	
}
