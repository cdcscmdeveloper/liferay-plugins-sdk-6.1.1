/**
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.util.pm.helper;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * Singleton Helper For User.
 * 
 * @author kenny
 * @since 29.12.2013
 * 
 */
public class UserHelper {
	private static Logger log = EpLogger.getLogger(UserHelper.class);

	private static UserHelper instance;

	/**
	 * Get singleton of ParameterHelper
	 * 
	 * @return ParameterHelper singleton
	 */
	public static UserHelper getInstance() {
		if (instance == null) {
			instance = new UserHelper();
		}
		return instance;
	}

	public PmUser getUserByLoginId(String loginId) {
		debugLogger("getUserByLoginId");
		
		PmUser user = null;

		if (loginId != null) {
			IUserSO userSO;
			try {
				userSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

				UserReqDTO reqDTO = new UserReqDTO();
				reqDTO.setLoginId(loginId);

				UserResDTO resDTO = userSO.retrieveUserByLoginId(reqDTO);

				user = resDTO.getPmUser();
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(e.getMessage());
			} finally {
				userSO = null;
			}
		}

		return user;
	}
	
	public PmUser saveUser(PmUser pmUser) {
		IUserSO userSO;
		try {
			userSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setPmUser(pmUser);

			UserResDTO resDTO = userSO.saveUser(reqDTO);
			
			pmUser = resDTO.getPmUser();
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(e.getMessage());
		} finally {
			userSO = null;
		}
		
		return pmUser;
	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
	}
}
