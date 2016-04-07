package my.ep.web.backingbean.bpm.tasksearch;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.bpm.tasklist.BPMAssignedTaskSearchManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.pm.helper.StatusHelper;
import my.ep.web.util.sm.helper.SmCommonUtil;

import org.apache.log4j.Logger;

@ManagedBean(name = "bpmAssignedTaskSearchBackingBean")
@RequestScoped
public class BPMAssignedTaskSearchBackingBean extends BaseBackingBean<BPMAssignedTaskSearchManagedBean> {

	/**
	 * 
	 */

	private static Logger log = Logger.getLogger(BPMAssignedTaskSearchBackingBean.class);

	private String language = new FacesScopes().getUserLocale().getLanguage();

	public void searchAction() {
		log.info("*********searchAction*********");

		if (((getManagedBean().getDocNo() == null) || (getManagedBean().getDocNo() != null && getManagedBean().getDocNo().trim().isEmpty()))
				&& (getManagedBean().getDocType() == null)
				&& (getManagedBean().getSearchFromCreatedDate() == null && getManagedBean().getSearchToCreatedDate() == null)) {
			getManagedBean().resetTaskListDataModel();
			throw new EpException(MessageConst.ERROR_716006);
		}

		this.getManagedBean().getTaskListDataModel().reloadData();
	}

	public void findDocType() {

		log.info("*********findDocType*********");

		List<PmParameterDesc> result = SmCommonUtil.retrieveParameterDescList(ParameterTypeConst.DOCUMENT_TYPES, language);

		this.getManagedBean().setDocTypeList(result);
	}

	public void findDocStatus() {
		if (this.getManagedBean().getDocType() != null) {
				log.info("*********findDocStatus*********");
				log.info("For Doc Type Id:" + this.getManagedBean().getDocType());

				IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject("pm", IStatusSO.class);

				StatusReqDTO reqDTO = new StatusReqDTO();
				reqDTO.setDocType(this.getManagedBean().getDocType());
				reqDTO.setLanguageCode(language);

				StatusResDTO resDTO = statusSO.retrieveStatusDescListByDocType(reqDTO);

				List<SelectItem> docStatusList = new ArrayList<SelectItem>();
				for (PmStatusDesc docStatus : resDTO.getStatusDescList()) {
					SelectItem si = new SelectItem();
					si.setLabel(docStatus.getStatusName());
					si.setValue(docStatus.getPmStatus().getStatusId());
					log.info("statusId" + si.getValue());
					log.info("statusName" + si.getLabel());
					docStatusList.add(si);
				}
				this.getManagedBean().setDocStatusList(docStatusList);
		} else {
			this.getManagedBean().setDocStatusList(null);
		}
	}

	public void reset(){
		this.getManagedBean().setDocNo(null);
		this.getManagedBean().setDocType(null);
		this.getManagedBean().setDocStatus(0);
		this.getManagedBean().setSearchFromCreatedDate(null);
		this.getManagedBean().setSearchToCreatedDate(null);

		this.getManagedBean().setDocStatusList(null);

		this.getManagedBean().resetTaskListDataModel();
	}

	public String getDocStatus(Integer docInt) {
		PmStatusDesc pmStatusDesc = StatusHelper.getInstance().getStatusDescById(docInt.longValue());

		if (log.isDebugEnabled()) {
			log.debug("docInt:" + docInt);	
		}
		return pmStatusDesc != null ? pmStatusDesc.getStatusName() : "Review";
	}

	public String getAssigneeNamesString(List<String> assignees) {
		StringBuilder assigneeNamesString = new StringBuilder();

		log.info("No of Assignees: " + assignees.size());
		try {
			for (int i = 0; i < assignees.size(); i++) {			
				PmUser pmUser = PmCommonUtil.retrieveUserByLoginId(assignees.get(i));
				if (pmUser != null) {
					String userName = pmUser.getUserName();

//					log.info("AssigneeLoginId: " + assignees.get(i));
//					log.info("AssigneeName: " + userName);

//					assigneeNamesString.append("[");
					assigneeNamesString.append(userName);
//					assigneeNamesString.append("]");
					if (i+1 < assignees.size()) {
						assigneeNamesString.append(", ");
					}
				}
			}
			log.info("AssigneeNamesString: " + assigneeNamesString.toString());
			return assigneeNamesString.toString();
		} catch (Exception e) {
			log.info(e.getMessage());
			throw new EpException(e.getMessage());
		}
	}
}
