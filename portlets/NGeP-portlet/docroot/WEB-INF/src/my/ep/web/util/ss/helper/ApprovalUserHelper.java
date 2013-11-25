package my.ep.web.util.ss.helper;

import java.util.ArrayList;
import java.util.List;

import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.ss.directpurchase.ScApprovalUser;

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
}
