/**
 * 
 */
package my.ep.web.backingbean.ss.quatationtender;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.ss.quationtender.common.QTConstants;

/**
 * @author VidyasagarReddy
 * 
 */
public abstract class SourcingBaseBackingBean<T extends BaseManagedBean>
		extends BaseBackingBean<T> {
	/**
	 * Method to retrive orgprofiles based on user id and parent org id
	 * 
	 * @param profileReqDTO
	 * @return
	 */
	public List<PmOrgProfile> retriveOrgProfiles(OrgProfileReqDTO profileReqDTO) {

		IOrgProfileSO orgProfileSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(QTConstants.PM_MODULE, IOrgProfileSO.class);
		OrgProfileResDTO resDTO = orgProfileSO
				.retrieveOrgProfileListByOrgType(profileReqDTO);
		return resDTO.getOrgProfileList();
	}

	public List<SelectItem> toOrgProfileSelectItems(
			List<PmOrgProfile> orgProfile) {
		List<SelectItem> orgProfileSelectItems = new ArrayList<SelectItem>();
		orgProfileSelectItems.add(new SelectItem("", "Select One"));
		for (PmOrgProfile pmOrgProfile : orgProfile) {
			orgProfileSelectItems.add(new SelectItem(pmOrgProfile
					.getOrgProfileId().toString(), pmOrgProfile.getOrgCode()));
		}
		return orgProfileSelectItems;
	}

	public List<SelectItem> toSelectItmes(List<PmUser> users) {
		List<SelectItem> userList = new ArrayList<SelectItem>();
		userList.add(new SelectItem("", "SelectOne"));
		for (PmUser user : users) {
			userList.add(new SelectItem(user.getUserId(), user.getUserName()));
		}
		return userList;
	}

}
