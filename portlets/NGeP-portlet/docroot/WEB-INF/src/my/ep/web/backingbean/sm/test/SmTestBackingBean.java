package my.ep.web.backingbean.sm.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;

import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.web.util.lookupservice.BusinessDelegate;

@ManagedBean(name = "smTestBackingBean")
@ViewScoped
public class SmTestBackingBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4466798455679399622L;
	private List<SmPersonnel> smPersonnelPendingActivationBasicList;
	private List<SmPersonnel> smPersonnelPendingActivationUpgradeMofDraftList;

	@PostConstruct
	public void initApplication() {
		IPersonnelSO smPersonnelSO = (IPersonnelSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IPersonnelSO.class);
		PersonnelResDTO personnelResBasicDto = new PersonnelResDTO();
		personnelResBasicDto = smPersonnelSO
				.testPendingActivationBasicAccount();

		PersonnelResDTO personnelResNotBasicDto = new PersonnelResDTO();
		personnelResNotBasicDto = smPersonnelSO
				.testPendingActivationUpgradeMofDraftAccount();

		smPersonnelPendingActivationBasicList = new ArrayList<SmPersonnel>();
		smPersonnelPendingActivationUpgradeMofDraftList = new ArrayList<SmPersonnel>();
		if (personnelResBasicDto.getSmPersonnelList() != null
				&& !personnelResBasicDto.getSmPersonnelList().isEmpty()) {
			smPersonnelPendingActivationBasicList = personnelResBasicDto
					.getSmPersonnelList();
		}

		if (personnelResNotBasicDto.getSmPersonnelList() != null
				&& !personnelResNotBasicDto.getSmPersonnelList().isEmpty()) {
			smPersonnelPendingActivationUpgradeMofDraftList = personnelResNotBasicDto
					.getSmPersonnelList();
		}
	}

	public void execute() {
		
	}

	public List<SmPersonnel> getSmPersonnelPendingActivationBasicList() {
		return smPersonnelPendingActivationBasicList;
	}

	public void setSmPersonnelPendingActivationBasicList(
			List<SmPersonnel> smPersonnelPendingActivationBasicList) {
		this.smPersonnelPendingActivationBasicList = smPersonnelPendingActivationBasicList;
	}

	public List<SmPersonnel> getSmPersonnelPendingActivationUpgradeMofDraftList() {
		return smPersonnelPendingActivationUpgradeMofDraftList;
	}

	public void setSmPersonnelPendingActivationUpgradeMofDraftList(
			List<SmPersonnel> smPersonnelPendingActivationUpgradeMofDraftList) {
		this.smPersonnelPendingActivationUpgradeMofDraftList = smPersonnelPendingActivationUpgradeMofDraftList;
	}

}
