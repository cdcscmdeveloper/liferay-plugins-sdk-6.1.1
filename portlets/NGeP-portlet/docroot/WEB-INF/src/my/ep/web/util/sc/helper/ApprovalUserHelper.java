package my.ep.web.util.sc.helper;

import java.util.ArrayList;
import java.util.List;

import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.ss.directpurchase.ScApprovalUser;
import my.ep.persistence.entity.ss.directpurchase.ScRequestNoteApprovalUser;

public class ApprovalUserHelper {
	public static ScApprovalUser	createApprovalUser(PmUser user){
		ScApprovalUser	result	=	new ScApprovalUser();
		result.setPmUser(user);
		return result;
	}
	public static	List<ScApprovalUser>	createApprovalUsers(List<PmUser> users){
		List<ScApprovalUser>	results	=	new ArrayList<ScApprovalUser>();
		for(PmUser user: users){
			results.add(createApprovalUser(user));
		}
		return results;
	}
	public	static	List<PmUser>	getListApprovalUser(List<ScRequestNoteApprovalUser> scApprovalUsers){
		List<PmUser>	results	=	new	ArrayList<PmUser>();
		for(ScApprovalUser scApprovalUser: scApprovalUsers){
			results.add(scApprovalUser.getPmUser());
		}
		return results;
	}
}
