package my.ep.web.backingbean.sc.quatationtender;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.quationtender.ScCommittee;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtApproval;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRequest;
import my.ep.persistence.entity.sc.quationtender.ScQtApproverSettings;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtStatus;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplier;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierZone;
import my.ep.persistence.entity.sc.quationtender.ScQtZoneCriteria;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
import my.ep.util.SLAUtils;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CloseEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@ManagedBean(name = "qtMultipleCloneBackingBean")
@RequestScoped
public class QtMultipleCloneBackingBean extends
SourcingBaseBackingBean<QTDetailManagedBean>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The Constant logger. */
	private static final Logger logger = EpLogger
			.getLogger(QtMultipleCloneBackingBean.class);
	
	private TreeNode root;
	private TreeNode[] selectedNodes;
	private boolean showTree = false;
	private int totalClone = 1;
	private static final String ALL = "lbl.txt.all";
	private static final String QT_DETAILS = "lbl.sc.qt.qtn.details";
	private static final String SUPPLIER_QUALIFICATION_CRITERIA = "lbl.sc.supplier.qulification.criteria";
	private static final String COMMITTEE_APPOINTMENT = "lbl.sc.qt.committee.appointment";
	private static final String APPROVAL = "lbl.sc.approval";
	private ScQt selectedScqt;
	
	//25635-start
	/**
	 * Instantiates a new multiple clone backing bean.
	 */
	public QtMultipleCloneBackingBean(){
		this.root = new DefaultTreeNode("Root Node", null);
		this.selectedScqt=new ScQt();
		TreeNode child = new DefaultTreeNode(new QTSelectionNode(getLabel(ALL),0L), this.root);
		child.setParent(this.root);
		child.setExpanded(true);
		TreeNode descendent1 = new DefaultTreeNode(new QTSelectionNode(getLabel(QT_DETAILS),1L), child);
		//25635-start
		descendent1.setSelected(Boolean.TRUE);
		descendent1.setSelectable(Boolean.TRUE);
		//25635-end
		descendent1.setParent(child);
		TreeNode descendent2 = new DefaultTreeNode(new QTSelectionNode(getLabel(SUPPLIER_QUALIFICATION_CRITERIA),2L), child);
		descendent2.setParent(child);
		TreeNode descendent3 = new DefaultTreeNode(new QTSelectionNode(getLabel(COMMITTEE_APPOINTMENT),3L), child);
		descendent3.setParent(child);
		TreeNode descendent4 = new DefaultTreeNode(new QTSelectionNode(getLabel(APPROVAL),4L), child);
		descendent4.setParent(child);
			
	}
	
	public void clonePopupAction(ScQt scQt){
		debugLogger("clonePopupAction");
		this.totalClone=1; 
		selectedScqt=scQt;
		RequestContext.getCurrentInstance().execute("kklik.show()");
		this.getManagedBean().setCloned(false); 
	}
	
	public void addToClone(){
		IQTDetailSO QtDetailsSO;
		try {
			TreeNode[] nodes=this.selectedNodes;		
			if(null==nodes || nodes.length==0){
				throw new EpException(MessageConst.ERROR_760495);
			}
			
			if(this.totalClone>10){//25635
				throw new EpException(MessageConst.ERROR_775151);
			}
			
			if(totalClone>0){
				QtDetailsSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
				QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
				tenderReqDTO.setMaintanceFlow(this.getManagedBean()
						.isMaintanceFlow());
				tenderReqDTO.setQtId(selectedScqt.getQtId());
				tenderReqDTO.setBdgDtlsReq(true);
				
				QuationTenderResDTO tenderResDTO = null;
				/*
				 * TRACE HI-EXEC TIME
				 * Development #29385
				 */
				long startTime = System.currentTimeMillis();
				tenderResDTO = QtDetailsSO.retriveQTDetails(tenderReqDTO);
				SLAUtils.getInstance().logHiExecInfo("QT","QtMultipleCloneBackingBean.addToClone", startTime);
				/*
				 * END
				 */
				ScQt copyScqt = tenderResDTO.getQtDetail();
				selectedScqt.setQtNo(copyScqt.getQtNo());
				int i=0;
				while(i<totalClone){
					ScQt scQt=populateQtnDetails(copyScqt,QtDetailsSO);
					
					for(TreeNode node:nodes){
						QTSelectionNode selection=(QTSelectionNode) node.getData();
						if(selection.getValue().equals(2L)){
							if(null!=copyScqt.getScQtSupplierCriteria()){
								if(!copyScqt.isPanel()){
									scQt=populateSupplierCriteria(scQt,copyScqt,QtDetailsSO);
								}
							}
						}
						if(selection.getValue().equals(3L)){
							scQt=populateCommittee(scQt,copyScqt);
						}
						if(selection.getValue().equals(4L)){
							scQt=populateApprovals(scQt,copyScqt,QtDetailsSO);
						}
					}
					
					QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
					reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
					reqDTO.setUserLocale(new FacesScopes().getUserLocale());
					//QuationTenderResDTO resDTO = new QuationTenderResDTO();
					reqDTO.setQtdetail(scQt);
					reqDTO.setSaveFlow(true);
					reqDTO.setOutcome(BpmOutcomeConst.BPM_SAVE);
					reqDTO.setBrandNewQt(true);
					FacesScopes facesScopes = new FacesScopes();//#9019-start
					reqDTO.setToken(facesScopes
							.getBPMAuthenticationCtxToken());
					reqDTO.setTaskId(this.getManagedBean().getTaskId());
					reqDTO.setPayLoad(this.getManagedBean().getPayLoad());//#9019-End
					QtDetailsSO.saveQTDtl(reqDTO);
					i++;
				}
				
			}
			
		
		
		
		}finally{
			QtDetailsSO=null;
		}
		
		this.getManagedBean().setCloned(true);
		successMessage(MessageConst.INFO_700081, ParameterTypeConst.DOCUMENT_TYPES,
				ParameterConst.DOCTYPE_QUOTATION, selectedScqt.getQtNo(), Boolean.FALSE);
		this.getManagedBean().getQtnTenderSearchBean().setDocType(ParameterConst.DOCTYPE_QUOTATION);
		this.getManagedBean().getQtnTenderSearchBean().setDocNo(null);
		this.getManagedBean().getQtnTenderSearchBean().setQtTitle(null);
		this.getManagedBean().getQtnTenderSearchBean().setQtNo(null);
		this.getManagedBean().getQtnTenderSearchBean().setProcurementMode(null);
		this.getManagedBean().getQtnTenderSearchBean().setFromCreatedDate(null);
		this.getManagedBean().getQtnTenderSearchBean().setToCreatedDate(null);
		this.getManagedBean().getQtnTenderSearchBean().setFromPublishDate(null);
		this.getManagedBean().getQtnTenderSearchBean().setToPublishDate(null);
		this.getManagedBean().getQtnTenderSearchBean().setFromClosingDate(null);
		this.getManagedBean().getQtnTenderSearchBean().setToClosingDate(null);
		this.getManagedBean().getQtnTenderSearchBean().setQtStatus(null);
		this.getManagedBean().getSearchQTDataModel().reloadData();
	//	RequestContext.getCurrentInstance().update("qtsearchid, qtList");
	}
	
	public void closeEvent(CloseEvent event){
		if(this.getManagedBean().isCloned()){
			this.getManagedBean().getQtnTenderSearchBean().setDocType(ParameterConst.DOCTYPE_QUOTATION);
			this.getManagedBean().getQtnTenderSearchBean().setDocNo(null);
			this.getManagedBean().getQtnTenderSearchBean().setQtTitle(null);
			this.getManagedBean().getQtnTenderSearchBean().setQtNo(null);
			this.getManagedBean().getQtnTenderSearchBean().setProcurementMode(null);
			this.getManagedBean().getQtnTenderSearchBean().setFromCreatedDate(null);
			this.getManagedBean().getQtnTenderSearchBean().setToCreatedDate(null);
			this.getManagedBean().getQtnTenderSearchBean().setFromPublishDate(null);
			this.getManagedBean().getQtnTenderSearchBean().setToPublishDate(null);
			this.getManagedBean().getQtnTenderSearchBean().setFromClosingDate(null);
			this.getManagedBean().getQtnTenderSearchBean().setToClosingDate(null);
			this.getManagedBean().getQtnTenderSearchBean().setQtStatus(null);
			this.getManagedBean().getSearchQTDataModel().reloadData();
		}
		
	}
	
	private ScQt populateApprovals(ScQt scQt,ScQt copyScqt,IQTDetailSO QtDetailsSO){
		QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
		copyScqt=retrieveScqtCommittee(copyScqt);
		tenderReqDTO.setQtdetail(copyScqt);
		tenderReqDTO.setAvaliableApprovals(copyScqt.getScQtApprovals());
		tenderReqDTO.setAmendMentBean(this.getManagedBean().getAmendMentBean());
		tenderReqDTO.setDeskOfficer(new FacesScopes().getUserSessionBean());
		QuationTenderResDTO resDTO = QtDetailsSO.retriveQTApprovalDetails(tenderReqDTO);
		List<ScQtApproval> requiredApproval = resDTO.getApproval();
		List<ScQtApproval> approvals=new ArrayList<ScQtApproval>();
		List<ScQtApproval> newApprovals=new ArrayList<ScQtApproval>();
		if(null!=requiredApproval && CollectionUtils.isNotEmpty(requiredApproval)){//25635-start
		for(ScQtApproval approval:requiredApproval){
			if (ParameterConst.APPROVAL_CATEGORY_COMMITTEEE_APOINTMENT
					.equalsIgnoreCase(approval.getApprovalCategory()
							.getParameterCode())) {
				
				approvals.add(approval);
				
			}
		}
		}//25635-End
		
		for(ScQtApproval copyApproval:approvals){
			ScQtApproval newApproval= new ScQtApproval();
			newApproval.setBrandNew(true);
			newApproval.setApprovalCategory(copyApproval.getApprovalCategory());
			newApproval.setRecordStatusFlg(copyApproval.getRecordStatusFlg());
			newApproval.setRecordStatus(copyApproval.isRecordStatus());
			newApproval.setScQt(scQt);
			setUserDetails(newApproval);
			if(copyApproval.getApproverSettings()!=null && !copyApproval.getApproverSettings().isEmpty()){
				List<ScQtApproverSettings> approverSettings=new ArrayList<ScQtApproverSettings>();
				for(ScQtApproverSettings setting:copyApproval.getApproverSettings()){
					ScQtApproverSettings aps=new ScQtApproverSettings();
					aps.setBrandNew(true);
					setUserDetails(aps);
					aps.setCommitteeType(setting.getCommitteeType());
					aps.setDocType(setting.getDocType());
					aps.setPmUser(setting.getPmUser());
					aps.setRecordStatus(setting.isRecordStatus());
					aps.setRecordStatusFlg(setting.getRecordStatusFlg());
					aps.setScQtApproval(newApproval);
					aps.setSelectable(setting.isSelectable());
					aps.setSelected(setting.isSelected());
					aps.setUserSequence(setting.getUserSequence());
					approverSettings.add(aps);
				}
				newApproval.setApproverSettings(approverSettings);
			}
			
			if(copyApproval.getScQtApprovalAttachments()!=null && !copyApproval.getScQtApprovalAttachments().isEmpty()){
				List<ScQtApprovalAttachment> approverAttachments=new ArrayList<ScQtApprovalAttachment>();
				for(ScQtApprovalAttachment attachment:copyApproval.getScQtApprovalAttachments()){
					ScQtApprovalAttachment att=new ScQtApprovalAttachment();
					setUserDetails(att);
					att.setBrandNew(true);
					att.setDocNo(scQt.getQtNo());
					att.setDocType(attachment.getDocType());
					att.setScQtApproval(newApproval);
					try {
						uploadAttachment(att,attachment);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					approverAttachments.add(att);
				}
				newApproval.setScQtApprovalAttachments(approverAttachments);
			}
			
			if(copyApproval.getScQtApprovalDtls()!=null && !copyApproval.getScQtApprovalDtls().isEmpty()){
				List<ScQtApprovalRequest> approverRequest=new ArrayList<ScQtApprovalRequest>();
				for(ScQtApprovalRequest request:copyApproval.getScQtApprovalDtls()){
				if(null!=request.getApprovalType() && null!=request.getApprovalType().getParameterId() && ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE.equalsIgnoreCase(request.getApprovalType().getParameterCode())){//25635-start
					ScQtApprovalRequest req=new ScQtApprovalRequest();
					req.setBrandNew(true);
					setUserDetails(req);
					req.setRecordStatus(request.isRecordStatus());
					req.setRecordStatusFlg(request.getRecordStatusFlg());
					req.setApprovalType(request.getApprovalType());
					req.setScQtApproval(newApproval);
					req.setSelectable(request.isSelectable());
					req.setSelected(request.isSelected());
					approverRequest.add(req);
					}//25635-End
				}
				newApproval.setScQtApprovalDtls(approverRequest);
			}
			
			newApprovals.add(newApproval);
		}
		scQt.setScQtApprovals(newApprovals);
		return scQt;
	}
	
	private void uploadAttachment(ScAttachment copy, ScAttachment origin)
			throws FileNotFoundException, IOException {
		String fileName = origin.getFileName();
		File file = new File(getActualFilePath(origin));
		byte[] fileBytes = Files.readAllBytes(file.toPath());
		// byte[] fileBytes = null;//file.getContents();
		FileOutputStream fos = null;
		try {
			if (fileBytes != null) {
				copy.setFileName(fileName);
				copy.setFileDesc("File uploded");
				populateAttachmentDetails(copy);
				fos = new FileOutputStream(getActualFilePath(copy));
				fos.write(fileBytes);
			}
		} finally {
			if (fos != null) {
				fos.close();
			}
		}
	}
	
	private ScQt populateCommittee(ScQt scQt,ScQt copyScqt){
		String commType = ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE;
		ICommitteSO committeSO;
		CommitteReqDTO reqDTO = new CommitteReqDTO();
		reqDTO.setCommitteeType(commType);
		reqDTO.setQtId(copyScqt.getQtId());
		CommitteResDTO resDTO = null;
		
		try {
			committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
			resDTO = committeSO.retrieveCommitteByQtIdNCommType(reqDTO);
			ScQtCommittee qtCommitte = resDTO.getScQtCommittee();
			if(null!=qtCommitte){
				IParameterSO parameterSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IParameterSO.class);
				ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
				ParameterResDTO parameterResDTO = null;
				
				parameterReqDTO
				.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
		        parameterReqDTO
				.setParameterCode(ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE);
				
				parameterResDTO = parameterSO
						.retrieveParameterByTypeAndCode(parameterReqDTO);
				List<ScQtCommittee> qtCommitteList = new ArrayList<ScQtCommittee>();
				ScQtCommittee clonedQtCom = new ScQtCommittee();
				clonedQtCom.setScQt(scQt);
				setUserDetails(clonedQtCom);
				clonedQtCom.setCommitteeActivities(null);
				clonedQtCom.setQtCommitteRemarks(null);
				clonedQtCom.setScQtMeetings(null);
				
				ScCommittee committee = new ScCommittee();
				committee.setCommitteStatus(getPmStatusByStatusId(QTStatusConst.CMT_PENDING_SUBMISSION));
				committee.setCommitteeType(parameterResDTO.getPmParameter());
				if (ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE
						.equalsIgnoreCase(commType)) {
					committee.setApprovalReq(true);
				}
				setUserDetails(committee);
				committee
						.setScCommitteeMembers(new ArrayList<ScCommitteeMember>());
				
				if(null!=qtCommitte.getScCommittee()){
					List<ScCommitteeMember> toCloneMemberList = new ArrayList<ScCommitteeMember>();
					if(CollectionUtils.isNotEmpty(qtCommitte.getScCommittee().getScCommitteeMembers())){
						toCloneMemberList=qtCommitte.getScCommittee().getScCommitteeMembers();
						for(ScCommitteeMember source:toCloneMemberList){
							ScCommitteeMember cloned = null;
							try {
								cloned = (ScCommitteeMember) BeanUtils
										.cloneBean(source);
							} catch (IllegalAccessException | InstantiationException
									| InvocationTargetException | NoSuchMethodException e) {
								e.printStackTrace();
							}
							if(null!=cloned){
								cloned.setBrandNew(true);
								cloned.setCommitteeMemberId(null);
								setUserDetails(cloned);
								setCommitteeMemberOrgProfile(cloned);
								if(null==cloned.getPmOrgProfile()){//31709-start
									QtnTenderSearchBean qtnTenderSearchBean = getManagedBean().getQtnTenderSearchBean();
									cloned.setPmOrgProfile(qtnTenderSearchBean.getPtjProfile());	
								}//31709-End
								cloned.setChangedDate(null);
								if (cloned.isConsolidate()) {
									this.getManagedBean().setSelectScCommitteMember(
											cloned);
								}
								cloned.setScCommittee(committee);
								committee.getScCommitteeMembers().add(cloned);
							}
						}
						
					}
				}
				clonedQtCom.setScCommittee(committee);
				qtCommitteList.add(clonedQtCom);
				scQt.setScQtCommittees(qtCommitteList);
			}
			
			
		}finally{
			committeSO=null;
		}
		
		
		return scQt;
	}
	
	private ScQt populateSupplierCriteria(ScQt scQt,ScQt copyScqt,IQTDetailSO QtDetailsSO){
		QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
		tenderReqDTO.setQtdetail(copyScqt);
		QuationTenderResDTO resDTO =QtDetailsSO.retriveQTSuppQulCriDetails(tenderReqDTO);
		
		ScQtSupplierCriteria cps =copyScqt.getScQtSupplierCriteria();
		
		ScQtSupplierCriteria supplierCriteria = scQt.getScQtSupplierCriteria();
		if(null==supplierCriteria){
			supplierCriteria = new ScQtSupplierCriteria();
			supplierCriteria.setScQt(scQt);
			supplierCriteria.setBrandNew(true);
			setUserDetails(supplierCriteria);
		}

		if(cps!=null){
			if(cps.getLocationLevel()!=null){
				supplierCriteria.setLocationLevel(cps.getLocationLevel());
			}
			supplierCriteria.setAllState(cps.isAllState());
		}
//		if(copyScqt.isZonal()){
//			ScQtZoneCriteria qtZoneCriterias =new ScQtZoneCriteria();
//			qtZoneCriterias.setBrandNew(true);
//			qtZoneCriterias.setScQtSupplierCriteria(supplierCriteria);
//			
//			if(cps!=null && cps.getQtZoneCriterias()!=null && cps.getQtZoneCriterias().getTplSpecZoneDtls()!=null){
//				qtZoneCriterias.setTplSpecZoneDtls(cps.getQtZoneCriterias().getTplSpecZoneDtls());
//			}
//			supplierCriteria.setQtZoneCriterias(qtZoneCriterias);
//		}
//		
		scQt.setScQtSupplierCriteria(supplierCriteria);
		
		List<ScQtCategory> categoryResults = resDTO
				.getCategories();
		List<ScQtSupplier> restrictResults=resDTO.getRestrictedSuppliers();
		
		if(categoryResults!=null){
			List<ScQtCategory> qtCategoryList = new ArrayList<ScQtCategory>();
			for(ScQtCategory cat:categoryResults){
				ScQtCategory category = new ScQtCategory();
				category.setScQt(scQt);
				category.setPmCategoryL1(cat.getPmCategoryL1());
				category.setPmCategoryL2(cat.getPmCategoryL2());
				category.setPmCategoryL3(cat.getPmCategoryL3());
				category.setPmCategoryL1Name(cat.getPmCategoryL1Name());
				category.setPmCategoryL2Name(cat.getPmCategoryL2Name());
				category.setPmCategoryL3Name(cat.getPmCategoryL3Name());
				qtCategoryList.add(category);
			}
			scQt.setScQtCategorys(qtCategoryList);
		}
		if(restrictResults!=null){
			List<ScQtSupplier> listSupplier=new ArrayList<ScQtSupplier>();
			for(ScQtSupplier supp:restrictResults){
				ScQtSupplier supplier=new ScQtSupplier();
				supplier.setBrandNew(true);
				supplier.setBumi(supp.isBumi());
				supplier.setDisableEditAccount(supp.isDisableEditAccount());
				supplier.setDisableForPindaan(supp.isDisableForPindaan());
				supplier.setDisqualifiedStage(supp.getDisqualifiedStage());
				supplier.setInvitationType(supp.getInvitationType());
				supplier.setJustificationEnabled(supp.isJustificationEnabled());
				supplier.setMofAccountExpDate(supp.getMofAccountExpDate());
				supplier.setReasonForUpdateEnabled(supp.isReasonForUpdateEnabled());
				supplier.setScQt(scQt);
				
//				if(supp.getScQtProposals()!=null && !supp.getScQtProposals().isEmpty()){
//					for(ScQtProposal proposal:supp.getScQtProposals()){
//						
//					}
//				}
				//supplier.setScQtProposals(scQtProposals);
				if(supp.getScQtSupplierZones()!=null && !supp.getScQtSupplierZones().isEmpty()){
					List<ScQtSupplierZone> scQtSupplierZones=new ArrayList<ScQtSupplierZone>();
					for(ScQtSupplierZone zone:supp.getScQtSupplierZones()){
						ScQtSupplierZone z=new ScQtSupplierZone();
						z.setBrandNew(true);
						setUserDetails(z);
						z.setDisableEditAccount(zone.isDisableEditAccount());
						z.setDisableForPindaan(zone.isDisableForPindaan());
						z.setDisqualifiedStage(zone.getDisqualifiedStage());
						z.setJustificationEnabled(zone.isJustificationEnabled());
						z.setReasonForUpdateEnabled(zone.isReasonForUpdateEnabled());
						z.setScQtSupplier(supplier);
						if(scQt.getScQtSupplierCriteria()!=null){
							if(scQt.getScQtSupplierCriteria().getQtZoneCriterias()!=null){
								for(TplSpecZoneDtl tpl:scQt.getScQtSupplierCriteria().getQtZoneCriterias().getTplSpecZoneDtls()){
									if(tpl.getZoneName().equals(zone.getTplSpecZoneDtl().getZoneName())){
										z.setTplSpecZoneDtl(tpl);
									}
								}
							}
						}
						scQtSupplierZones.add(z);
					}
					supplier.setScQtSupplierZones(scQtSupplierZones);
				}
				
				
				
				supplier.setSmMofAccount(supp.getSmMofAccount());
				supplier.setSmSupplier(supp.getSmSupplier());
				supplier.setStatusEnabled(supp.isStatusEnabled());
				supplier.setSupplierAddress(supp.getSupplierAddress());
				supplier.setSupplierMofNo(supp.getSupplierMofNo());
				supplier.setSupplierName(supp.getSupplierName());
				setUserDetails(supplier);
				
				
				
				supp.setScQt(scQt);
				listSupplier.add(supp);
			}
			scQt.setScQtSuppliers(listSupplier);
		}
		
		return scQt;
	}
	
	private ScQt populateQtnDetails(ScQt copyScqt,IQTDetailSO QtDetailsSO){
		ScQt scQt=new ScQt();
		scQt.setFileNo(null);
		scQt.setBrandNew(true);
		scQt.setObtainSpecialApproval(copyScqt
				.getObtainSpecialApproval());
		scQt.setPanel(copyScqt.isPanel());
		scQt.setCircularVer(copyScqt.getCircularVer());
		scQt.setItemGroup(copyScqt.getItemGroup());
		scQt.setQtTitle(copyScqt.getQtTitle());
        //9019 added for disable mandatory checking in QT while clone
		scQt.setCloned(true);
		scQt.setProcurementMode(copyScqt.getProcurementMode());
		scQt.setPartial(copyScqt.isPartial());
		BigDecimal tmpExpectedIntendedPrice = QtDetailsSO
				.getDecryptedExpectedIntendedPrice(copyScqt.getQtId());
		if(null==tmpExpectedIntendedPrice){//31709-start
			tmpExpectedIntendedPrice=copyScqt.getExpectedIntendedPrice();	
		}//31709-End
		scQt.setTmpExpectedIntendedPrice(tmpExpectedIntendedPrice);
	//	scQt.setEstimatedAmount(copyScqt.getEstimatedAmount());
	//	scQt.setDecryptedEstAmt(copyScqt.getDecryptedEstAmt());
	//	scQt.setDecExpectedIntendedPrice(copyScqt.getDecExpectedIntendedPrice());
		
		scQt.setZonal(copyScqt.isZonal());
		ScQtSupplierCriteria cps =copyScqt.getScQtSupplierCriteria();
		ScQtSupplierCriteria supplierCriteria = new ScQtSupplierCriteria();
		supplierCriteria.setScQt(scQt);
		supplierCriteria.setBrandNew(true);
		supplierCriteria.setLocationLevel(new PmParameter());
		supplierCriteria.setScQtLocCriteria(null);
		setUserDetails(supplierCriteria);
		if(copyScqt.isZonal()){
			
			ScQtZoneCriteria qtZoneCriterias =new ScQtZoneCriteria();
			qtZoneCriterias.setBrandNew(true);
			qtZoneCriterias.setScQtSupplierCriteria(supplierCriteria);
			setUserDetails(qtZoneCriterias);
			if(!copyScqt.isPanel()){
				if(cps!=null && cps.getQtZoneCriterias()!=null && cps.getQtZoneCriterias().getTplSpecZoneDtls()!=null){
					List<TplSpecZoneDtl> listTpl=new ArrayList<TplSpecZoneDtl>();
					for(TplSpecZoneDtl tpl:cps.getQtZoneCriterias().getTplSpecZoneDtls()){
						TplSpecZoneDtl tplSpecZoneDtl = new TplSpecZoneDtl();
						tplSpecZoneDtl.setZoneName(tpl.getZoneName());
						tplSpecZoneDtl.setPanelZone(tpl.getPanelZone());
						tplSpecZoneDtl.setTplSpecZone(qtZoneCriterias);
						tplSpecZoneDtl.setBrandNew(true);
						setUserDetails(tplSpecZoneDtl);
						if(tpl.getTplSpecZoneLocs()!=null){
							List<TplSpecZoneLoc> listLoc=new ArrayList<TplSpecZoneLoc>();
							for(TplSpecZoneLoc loc:tpl.getTplSpecZoneLocs()){
								TplSpecZoneLoc l=new TplSpecZoneLoc();
								l.setBrandNew(true);
								l.setTplSpecZoneDtl(tplSpecZoneDtl);
								l.setPmDistrict(loc.getPmDistrict());
								l.setPmState(loc.getPmState());
								l.setPmDivision(loc.getPmDivision());
								l.setZonalLocation(loc.isZonalLocation());
								setUserDetails(l);
								listLoc.add(l);
							}
							tplSpecZoneDtl.setTplSpecZoneLocs(listLoc);
						}
						
						
						listTpl.add(tplSpecZoneDtl);
					}
					qtZoneCriterias.setTplSpecZoneDtls(listTpl);
				}
			}
			
			supplierCriteria.setQtZoneCriterias(qtZoneCriterias);
			
		}
		scQt.setScQtSupplierCriteria(supplierCriteria);
		
		scQt.setProcurementMode(copyScqt.getProcurementMode());
		scQt.setQtType(copyScqt.getQtType());
		scQt.setOpenFor(copyScqt.getOpenFor());
		scQt.setEvaluationType(copyScqt.getEvaluationType());
		scQt.setScheduleType(copyScqt.getScheduleType());
		scQt.setMaxProposal(copyScqt.getMaxProposal());
		scQt.setEvaluateBy(copyScqt.getEvaluateBy());
		
		scQt.setQtType(copyScqt.getQtType());
		scQt.setZoneLocationLevel(copyScqt.getZoneLocationLevel());
		scQt.setProcurementTypeCatId(copyScqt.getProcurementTypeCatId());
		scQt.setPmProcurementCategory(copyScqt.getPmProcurementCategory());
		scQt.setContractType(copyScqt.getContractType());
		scQt.setExistingContractNo(copyScqt.getExistingContractNo());
		scQt.setFulfilmentType(copyScqt.getFulfilmentType());
		scQt.setContractDuration(copyScqt.getContractDuration());
		//scQt.setPublishPeriod(copyScqt.getPublishPeriod());//31799
		//scQt.setQtValidityPeriod(copyScqt.getQtValidityPeriod());//31799
		
		scQt.setSpecApprovalReq(copyScqt.getSpecApprovalReq());
		scQt.setMinistrySpecSecrataryId(copyScqt.getMinistrySpecSecrataryId());
		
		PmStatus status = getPmStatusByStatusId(QTStatusConst.QT_PENDING_QT_SUBMISSION);
		scQt.setQtStatus(status);
		
		ScQtStatus qtStatus = new ScQtStatus();
		qtStatus.setScQt(scQt);
		qtStatus.setPmStatus(status);
		qtStatus.setCurrent(Boolean.TRUE);
		qtStatus.setDocType(ParameterConst.DOCTYPE_QUOTATION);
		scQt.setCurrentWorkFlowStatus(qtStatus);
		List<ScQtStatus> qtStatuses = new ArrayList<ScQtStatus>();
		qtStatuses.add(qtStatus);
		scQt.setScQtStatus(qtStatuses);
		
		scQt.setContractType(copyScqt.getContractType());
		setUserDetails(scQt);
		
		PmOrgProfile pmOrgProfile = getPTJOrgProfileByLoginUser();
		if (pmOrgProfile != null) {
			scQt.setPmOrgProfile(pmOrgProfile);
			scQt.setOwnerOrgProfile(pmOrgProfile);
			scQt.setOrgProfileId(pmOrgProfile.getOrgProfileId());
			scQt.setOwnerOrgProfileId(pmOrgProfile.getOrgProfileId());
		}
		scQt.setJustificationProcValue(copyScqt.getJustificationProcValue());//25635&34545
		
		return scQt;
	
	}
		
	
	
	private ScQt retrieveScqtCommittee(ScQt copyScqt){
		String commType = ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE;
		ICommitteSO committeSO;
		CommitteReqDTO reqDTO = new CommitteReqDTO();
		reqDTO.setCommitteeType(commType);
		reqDTO.setQtId(copyScqt.getQtId());
		CommitteResDTO resDTO = null;
		committeSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
		resDTO = committeSO.retrieveCommitteByQtIdNCommType(reqDTO);
		ScQtCommittee qtCommitte = resDTO.getScQtCommittee();
		
		List<ScQtCommittee> list=new ArrayList<ScQtCommittee>();
		list.add(qtCommitte);
		copyScqt.setScQtCommittees(list);
		return copyScqt;
	}
	
	
	private String getLabel(String label) {
		FacesContext context = FacesContext.getCurrentInstance();
		ResourceBundle bundle = context.getApplication().getResourceBundle(
				context, "lbl");
		String message = bundle.getString(label);
		return message;
	}
	
	private void debugLogger(Object message) {

		if (logger.isDebugEnabled()) {
			logger.debug(message);
		}
		/*
		 * else{ logger.info(message); }
		 */

	}

	public int getTotalClone() {
		return totalClone;
	}

	public void setTotalClone(int totalClone) {
		this.totalClone = totalClone;
	}

	public TreeNode[] getSelectedNodes() {
		return selectedNodes;
	}

	public void setSelectedNodes(TreeNode[] selectedNodes) {
		this.selectedNodes = selectedNodes;
	}
	
	public boolean isShowTree() {
		return showTree;
	}

	public void setShowTree(boolean showTree) {
		this.showTree = showTree;
	}

	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

	public ScQt getSelectedScqt() {
		return selectedScqt;
	}

	public void setSelectedScqt(ScQt selectedScqt) {
		this.selectedScqt = selectedScqt;
	}
	
	/***
	 * 29942-start
	 * checking clone count empty or zero
	 * @param event
	 */
	public void validateQuotationTender(AjaxBehaviorEvent event){
		Integer parametervalue = (Integer) event.getComponent().getAttributes().get("value");
		if(null==parametervalue || parametervalue.equals(0)){
			this.totalClone=1;
		}else if(parametervalue>10){//25635-start
			//this.totalClone=10;	
			throw new EpException(MessageConst.ERROR_775151);
		}//25635-End
		
	}
	//#29942-End
	
	//#9019 - Start
	public void confirmAddToClone(){
//		debugLogger("confirmAddToClone");
//		log.info("confirmAddToClone");
		//this.totalClone=1;
		//#9019 - Start (Harun)
		//        01082022 Reopen Add validation clone for more than 10 QT #note-254 
		if(this.totalClone<=10){
			RequestContext.getCurrentInstance().execute("confirmationClone.show()");
			this.getManagedBean().setCloned(false);
		}
		//#9019 - End (Harun)
		
	}
	//#9019 - End
}
