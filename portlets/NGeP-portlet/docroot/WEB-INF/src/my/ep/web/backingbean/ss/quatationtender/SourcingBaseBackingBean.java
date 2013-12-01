/**
 * 
 */
package my.ep.web.backingbean.ss.quatationtender;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.primefaces.model.UploadedFile;

import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.ss.quationtender.ScAttachment;
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

	protected void uploadAttachment(UploadedFile file, ScAttachment attachment) {
		String fileName = file.getFileName();
		byte[] fileBytes = file.getContents();
		Timestamp date = new Timestamp(System.currentTimeMillis());
		
		attachment.setCreatedBy(1L);
		attachment.setCreatedDate(date);
		attachment.setChangedBy(1L);

		attachment.setFileName(fileName);
		attachment.setFileDesc("File uploded for Fulfilment Req Order");
		File theDir = new File("\\upload\\");

		// if the directory does not exist, create it
		if (!theDir.exists()) {

			theDir.mkdir();
			System.out.println("creating directory: " + theDir);
		}
		attachment.setFilePath(theDir + "\\" + fileName);
		System.out.println("=======get File path===="
				+ attachment.getFilePath());
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(theDir + "\\" + fileName);
			fos.write(fileBytes);
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
