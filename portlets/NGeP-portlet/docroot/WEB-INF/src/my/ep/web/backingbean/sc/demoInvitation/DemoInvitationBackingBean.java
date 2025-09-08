package my.ep.web.backingbean.sc.demoInvitation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.sc.actor.IScActorSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTDemoSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTEvaluationSO;
import my.ep.biz.service.sc.quationtender.IQTSpecSO;
import my.ep.biz.service.tpl.spec.ITemplateSpecSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.QTFieldConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.sc.actor.ScActorReqDTO;
import my.ep.dto.sc.actor.ScActorResDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
import my.ep.dto.sc.quationtender.DemoReqDTO;
import my.ep.dto.sc.quationtender.DemoResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sc.quationtender.SpecificationReqDTO;
import my.ep.dto.sc.quationtender.SpecificationResDTO;
import my.ep.dto.sm.selfrequestsupplieradmin.SelfRequestSupplierAdminReqDTO;
import my.ep.dto.tpl.spec.TemplateSpecReqDTO;
import my.ep.dto.tpl.spec.TemplateSpecResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.sc.evaluationdata.SCEvaluationDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.quationtender.ScCommittee;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtApproval;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtApproverSettings;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtDemo;
import my.ep.persistence.entity.sc.quationtender.ScQtDemoByItemScore;
import my.ep.persistence.entity.sc.quationtender.ScQtDemoContact;
import my.ep.persistence.entity.sc.quationtender.ScQtDemoItem;
import my.ep.persistence.entity.sc.quationtender.ScQtDemoSchedule;
import my.ep.persistence.entity.sc.quationtender.ScQtDemoSupplier;
import my.ep.persistence.entity.sc.quationtender.ScQtProposal;
import my.ep.persistence.entity.sc.quationtender.ScQtRemark;
import my.ep.persistence.entity.tpl.spec.TplSpecAnswer;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.quatationtender.CreateQtBackingBean;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.sc.common.ZonesBean;
import my.ep.web.bean.sc.quatationtender.TabBean;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.demoInvitation.DemoInvitationManageBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sc.helper.LocationSingleton;
import my.ep.web.util.sc.helper.QTFieldMapper;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.TreeNode;

/**
 * The Class DemoInvitationBackingBean. #28784
 * 
 * @author:Amir
 */
@ManagedBean(name = "demoInvBB")
@ViewScoped
public class DemoInvitationBackingBean extends SourcingBaseBackingBean<DemoInvitationManageBean> implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String menuGeneralInfo = "MenuGeneralInfo";

	private static final String menuItemList = "MenuItemList";

	private static final String menuDemoDetails = "MenuDemoDetails";

	boolean savedData = false;

	boolean isRendered = false;

	String action = null;

	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	public String getMenuGeneralInfo() {
		return menuGeneralInfo;
	}

	public String getMenuItemList() {
		return menuItemList;
	}

	public String getMenuDemoDetails() {
		return menuDemoDetails;
	}

	/**
	 * Inits the.
	 */
	@PostConstruct
	private void init() {
	}

	public void prerenderView() {
		try {
			renewSesionObject();
			SCEvaluationDataType evaluationDataType = getBpmTaskDetails(SCEvaluationDataType.class, null);
			Long currentStatusId = this.getManagedBean().getCurrentStatusId();
			String committeeType = ((SCEvaluationDataType) evaluationDataType).getCommitteeType();
			Long qtId = evaluationDataType.getQtId().longValue();
			retrieveQuotationTenderDetails(qtId, committeeType);//remove long sqube G9

			this.getManagedBean().setReadOnly(false);
			this.getManagedBean().setActiveIndex(0);
			this.getManagedBean().setCurrentStatusId(currentStatusId);

			FacesContext context = FacesContext.getCurrentInstance();
			context.getApplication().getNavigationHandler()
					.handleNavigation(context, null, "/xhtml/sc/demoInvitation/demoInvitationMain.xhtml");
		} catch (Exception e) {
			handleException(e);
		}
	}

	/**
	 * @param menuItem
	 * @return
	 */
	public boolean displayMenuItem(String menuItem) {
		Set<String> menuSet = new HashSet<String>();
		menuSet.addAll(Arrays.asList(getMenuGeneralInfo(), // 0
				getMenuItemList(), // 1
				getMenuDemoDetails() // 2
				));

		return menuSet.contains(menuItem);

	}

	/**
	 * Qt details tab change.
	 * 
	 * @param activeIndex
	 *            the active index
	 */
	public void demoTabChange(Integer activeIndex) {

		int previousIndex = this.getManagedBean().getActiveIndex();

		getManagedBean().getQt().setMitiApproval(false);
		getManagedBean().getQt().setOtherApproval(false);
		getManagedBean().getQt().setFCApproval(false);
		boolean isReadOnly = getManagedBean().isReadOnly();
		try {

			if (previousIndex != activeIndex) {
				if (!isReadOnly) {
					switch (previousIndex) {
					case 0:
						break;
					case 1:
						break;
					case 2:
						break;
					}
				}
				switch (activeIndex) {
				case 0:
					retrieveQuotationTenderDetails(this.getManagedBean().getQt().getQtId(), null);
					break;
				case 1:
					retrieveSpecItemList();
					break;
				case 2:
					retrieveDemoDetailsData();
					break;
				}

				this.getManagedBean().setActiveIndex(activeIndex);
			}
		} catch (Exception e) {
			//Sonarqube QT Vulnerability Minor #44
			SLAUtils.getInstance().logSLAError("QT", "DemoTabChange",
					this.getManagedBean().getQt().getQtId().toString(), "demoTabChange", getUserIdSessionFromLoginId(),
					e);
		}
	}

	private void retrieveDemoDetailsData() {
		try {

			List<ScQtProposal> qualifiedSupp = checkQualifiedSupplier();

			if (qualifiedSupp.isEmpty()) {
				this.getManagedBean().setQualifiedSupplier(false);
				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("democancel.show();");

			} else {
				IQTDemoSO qtDemoSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDemoSO.class);
				DemoReqDTO reqDto = new DemoReqDTO();
				this.getManagedBean().setDisableSaveAndSubmitButton(false);
				reqDto.setScQt(this.getManagedBean().getQt());
				DemoResDTO resDto = qtDemoSO.retrieveScQtDemoByQtNo(reqDto);

				if (resDto.getScQtDemo() != null) {
					ScQtDemo scQtDemo = new ScQtDemo();
					scQtDemo = resDto.getScQtDemo();
					// #28784 reeval bug - Start
					boolean isReEvaluateDemo = false;
					//No need to check if demo exist or not since already check above
					isReEvaluateDemo = checkExistingDemoSupplier(scQtDemo.getScQtDemoSupplierList());
					if (isReEvaluateDemo) {

						DemoReqDTO demoReqDTO = new DemoReqDTO();
						demoReqDTO.setExistedData(false);
						scQtDemo.setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
						demoReqDTO.setScQtDemo(scQtDemo);

						qtDemoSO.saveDemoInvitation(demoReqDTO);

					} else {

						savedData = true;

						this.getManagedBean().setDemoInvBy(scQtDemo.getDemoCategoryId().getParameterCode());

						if (this.getManagedBean().getDemoInvBy().equalsIgnoreCase(SourcingConstants.ALL_ITEM)) {
							/*
							 * BUG 28784 : DEMO INVITATION COMPARATOR
							 */
							reqDto = new DemoReqDTO();
							reqDto.setScQtDemoSupplierList(scQtDemo.getScQtDemoSupplierList());
							scQtDemo.setScQtDemoSupplierList(qtDemoSO.sortDemoItem(reqDto, SourcingConstants.ALL_ITEM)
									.getScQtDemoSupplierList());
							this.getManagedBean().setAllItemList(scQtDemo.getScQtDemoSupplierList());
						} else if (this.getManagedBean().getDemoInvBy().equalsIgnoreCase(SourcingConstants.BY_ITEM)) {
							/*
							 * BUG 28784 : DEMO INVITATION COMPARATOR
							 */
							reqDto = new DemoReqDTO();
							reqDto.setScQtDemoItemList(scQtDemo.getScQtDemoItemList());
							scQtDemo.setScQtDemoItemList(qtDemoSO.sortDemoItem(reqDto, SourcingConstants.BY_ITEM)
									.getScQtDemoItemList());
							/*
							 * BUG 28784 : DEMO INVITATION END
							 */
							this.getManagedBean().setByItemList(scQtDemo.getScQtDemoItemList());
						}

						List<ScQtDemoContact> ct = scQtDemo.getScQtDemoContactList();
						Collections.sort(scQtDemo.getScQtDemoContactList(), new PresentationDemoContactListComparator());//sort contact list
						this.getManagedBean().setScQtDemoContactList(ct);
						this.getManagedBean().setDemoRemark(scQtDemo.getDemoInvRemark());
						this.getManagedBean().setScQtDemo(scQtDemo);
					}
					// #28784 reeval bug - End

				}
			}
		} catch (Exception e) {
			//Sonarqube QT Vulnerability Minor #44
			SLAUtils.getInstance().logSLAError("QT", "RetrieveDemoDetails",
					this.getManagedBean().getQt().getQtId().toString(), "retrieveDemoDetailsData",
					getUserIdSessionFromLoginId(), e);
		}
	}

	private void retrieveSpecItemList() {

		IQTSpecSO specSo = null;
		SpecificationResDTO resDTO = null;

		try {
			List<ScQtCheckList> localcheckList = new ArrayList<ScQtCheckList>();
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			specSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IQTSpecSO.class);
			reqDTO.setScQt(this.getManagedBean().getQt());
			reqDTO.setSpecType(SourcingConstants.TECHNICAL_DOCTYPE);
			resDTO = specSo.retrieveAllDemoCheckLists(reqDTO);

			if (null != resDTO.getTechnicalCheckList()) {
				for (ScQtCheckList checklist : resDTO.getTechnicalCheckList()) {
					if (null != checklist.getPmDocument()) {
						if (!SourcingConstants.DOC_DEF_KEY_SERVICE_SCOPE.equalsIgnoreCase(checklist.getPmDocument()
								.getDocDefinitionKey())) {
							localcheckList.add(checklist);
						}
					} else {
						for (TplSpecQuestion q : checklist.getScQtDoc().getTplSpecMaster().getTplSpecQuestions()) {
							if (q.isItemReqDemo()) {
								localcheckList.add(checklist);
								break;
							}
						}
					}
				}
			}
			this.getManagedBean().setScQtCheckSt2techProposal(localcheckList);
			if (CollectionUtils.isNotEmpty(this.getManagedBean().getScQtCheckSt2techProposal())) {
				checklistSorting(this.getManagedBean().getScQtCheckSt2techProposal());
			}

		} catch (Exception e) {
			//Sonarqube QT Vulnerability Minor #44
			SLAUtils.getInstance().logSLAError("QT", "RetrieveSpecItemList",
					this.getManagedBean().getQt().getQtId().toString(), "retrieveSpecItemList",
					getUserIdSessionFromLoginId(), e);
		}

	}

	private void checklistSorting(List<ScQtCheckList> checkList) {
		Collections.sort(checkList, new Comparator<ScQtCheckList>() {

			public int compare(ScQtCheckList checklist1, ScQtCheckList checklist2) {

				return new CompareToBuilder().append(checklist1.getChecklistSeq(), checklist2.getChecklistSeq())
						.append(checklist1.getCreatedDate(), checklist2.getCreatedDate()).toComparison();
			}
		});
	}

	/**
	 * Retrieve quotation tender details.
	 * 
	 * @param qtId
	 *            the qt id
	 * @param committeeType
	 * @param docType
	 *            the doc type
	 */
	public void retrieveQuotationTenderDetails(Long qtId, String committeeType) {
		if (null != qtId) {
			log.info("ReportBackingBean-retriveUserRoleByUserId");
			IUserSO userSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IUserSO.class);
			UserReqDTO reqDTO = new UserReqDTO();
			List<String> authoriztionList = new ArrayList<String>();
			if (null != new FacesScopes().getUserSessionBean().getUserId()) {
				reqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
				reqDTO.setLanguageCode(ReportConst.ENG_LANG);
				UserResDTO userResDTO = userSO.retrieveUserRoleDescListByUserId(reqDTO);
				List<PmRoleDesc> roleDesc = userResDTO.getRoleDescList();
				if (CollectionUtils.isNotEmpty(roleDesc)) {
					for (PmRoleDesc pmRoleDesc : roleDesc) {
						pmRoleDesc.getPmRole().getRoleCode();
						if (!authoriztionList.contains(pmRoleDesc.getPmRole().getRoleCode())) {
							authoriztionList.add(pmRoleDesc.getPmRole().getRoleCode());
						}

						RoleCode[] r = { RoleCode.SC_SECRETARY };
						if (userHasRole(r)) {
							getManagedBean().setSecretaryValidate(true);
						}
					}
				}
			}
			QuationTenderReqDTO reqQtDTO = new QuationTenderReqDTO();
			reqQtDTO.setQtId(qtId);
			reqQtDTO.setApprovalCategory(ParameterConst.APPROVAL_CATEGORY_COMMITTEEE_APOINTMENT);
			QuationTenderResDTO resDTO = getQTDetailSO().retrieveApprovalDtlsByQtIdNType(reqQtDTO);
			this.getManagedBean().setCurrentApproval(resDTO.getQtApproval());
			IQTDetailSO QtDetailsSO;
			try {
				QtDetailsSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTDetailSO.class);
				QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
				tenderReqDTO.setMaintanceFlow(this.getManagedBean().isMaintanceFlow());
				tenderReqDTO.setQtId(qtId);
				tenderReqDTO.setBdgDtlsReq(true);
				QuationTenderResDTO tenderResDTO = null;
				ScQt scQt = this.getManagedBean().getQt();
				if (committeeType != null) {
					this.getManagedBean().setCommitteeType(committeeType);
				}
				if (null == scQt
						|| (scQt.isZonal() && null != this.getManagedBean().getZoneGeneralTrees() && this
								.getManagedBean().getZoneGeneralTrees().isEmpty())) {

					long startTime = System.currentTimeMillis();
					tenderResDTO = QtDetailsSO.retriveDemoQTDetails(tenderReqDTO);
					SLAUtils.getInstance().logHiExecInfo("QT", "QTDetailBackingBean.retrieveQuationTenderDetails",
							startTime);

					if (tenderResDTO.getQtDetail() != null) {
						scQt = tenderResDTO.getQtDetail();
						getManagedBean().setQt(scQt);
						this.getManagedBean().setPanel(scQt.isPanel());
						
						getManagedBean().getCtContract().setContractNo(
								this.getManagedBean().getQt().getExistingContractNo());

						if (this.getManagedBean().getQt().isZonal()) {
							if (null != this.getManagedBean().getQt().getZoneLocationLevel()) {
								this.getManagedBean().setStateCodes(
										this.getManagedBean().getQt().getZoneLocationLevel().getParameterCode());
							}

							if (scQt.getScQtSupplierCriteria() != null
									&& scQt.getScQtSupplierCriteria().getQtZoneCriterias() != null) {
								List<TplSpecZoneDtl> specZoneDtls = scQt.getScQtSupplierCriteria().getQtZoneCriterias()
										.getTplSpecZoneDtls();
								populateZoneDetailIntoTreeNode(scQt, specZoneDtls);

								if (CollectionUtils.isNotEmpty(specZoneDtls)) {
									List<ZonesBean> zoneBeanLst = new ArrayList<ZonesBean>();
									for (TplSpecZoneDtl specZoneDtl : specZoneDtls) {
										ZonesBean bean = new ZonesBean();
										bean.setTplSpecZoneDtl(specZoneDtl);
										zoneBeanLst.add(bean);
									}
									this.getManagedBean().setBeans(zoneBeanLst);
								}
							}
						}
						if (scQt.getObtainSpecialApproval() != null && !scQt.getObtainSpecialApproval()) {
							this.getManagedBean().setPtjSpecialApproval(
									getPtjSpecialApprovalByOrgProfileId(scQt.getPmOrgProfile().getOrgProfileId()));

						} else {
							this.getManagedBean().setPtjSpecialApproval(scQt.getPtjSpecialApproval());
						}

						// #28784
						if (scQt.getEvaluationType().equalsIgnoreCase(ParameterConst.EVALUATION_TYPES_TWO_TIER)) {
							this.getManagedBean().setDemoType(true);
						}

					}

					this.getManagedBean().setAmendMentBean(tenderResDTO.getAmendMentBean());
					if (this.getManagedBean().getGosodos() == null) {
						this.getManagedBean().setGosodos(tenderResDTO.getBudgetDtls());
					}
					if (null != getManagedBean().getQt() && null == getManagedBean().getQt().getProcurementTypeCatId()) {
						getManagedBean().getQt().setProcurementTypeCatId(new PmParameter());
					}

				} else {
					if (scQt.getEstimatedAmount() == null) {
						BigDecimal estimatedAmount = QtDetailsSO.getdecryptedEstAmount(qtId);
						scQt.setEstimatedAmount(estimatedAmount);
						scQt.setDecryptedEstAmt(estimatedAmount);
					}

					if (scQt.getTmpExpectedIntendedPrice() == null) {
						BigDecimal tmpExpectedIntendedPrice = QtDetailsSO.getDecryptedExpectedIntendedPrice(qtId);
						scQt.setTmpExpectedIntendedPrice(tmpExpectedIntendedPrice);
						scQt.setDecExpectedIntendedPrice(tmpExpectedIntendedPrice);
					}
				}
			} catch (Exception e) {
				//Sonarqube QT Vulnerability Minor #44
				SLAUtils.getInstance().logSLAError("QT", "RetrieveQuotationTenderDetails",
						this.getManagedBean().getQt().getQtId().toString(), "retrieveQuotationTenderDetails",
						getUserIdSessionFromLoginId(), e);
			} finally {
				QtDetailsSO = null;
			}

		}
	}
	
	private void populateZoneDetailIntoTreeNode(ScQt scQt, List<TplSpecZoneDtl> specZoneDtls) {
		List<TreeNode> zoneGeneralTrees = getManagedBean().getZoneGeneralTrees();
		
		this.getManagedBean().setCtzones(Boolean.TRUE);
		this.getManagedBean().setTemplateSpecZoneDtls(specZoneDtls);
		if (zoneGeneralTrees == null) {
			zoneGeneralTrees = new ArrayList<TreeNode>();
			getManagedBean().setZoneGeneralTrees(zoneGeneralTrees);
		} else {
			zoneGeneralTrees.clear();	
		}
		
		boolean zoneCoverageReadonly = enableFieldByStatusNFiledName(QTFieldConst.FIELD_QT_ZONE_COVERAGE);
		List<TreeNode> zoneSuppQualTrees = null;
		if (zoneCoverageReadonly) {
			zoneSuppQualTrees = getManagedBean().getZoneSuppQualTrees();
			if (zoneSuppQualTrees == null) {
				zoneSuppQualTrees = new ArrayList<TreeNode>();
				getManagedBean().setZoneSuppQualTrees(zoneSuppQualTrees);
			} else {
				zoneSuppQualTrees.clear();	
			}	
		} else {
			getManagedBean().setZoneSuppQualTrees(null);
		}	
		if (scQt.isZonal() &&  scQt.isPanel() ){
		  new CreateQtBackingBean().retrieveZoneGeneralTreesPanel(scQt, specZoneDtls);
		}
		
		if (CollectionUtils.isNotEmpty(specZoneDtls)) {
			for (TplSpecZoneDtl zone : specZoneDtls) {
				if (CollectionUtils.isNotEmpty(zone.getTplSpecZoneLocs())) {
					List<TplSpecZoneLoc> specZoneLocs = new ArrayList<TplSpecZoneLoc>();
					
					for (TplSpecZoneLoc specLoc : zone.getTplSpecZoneLocs()) {
						if (specLoc.isZonalLocation()) {
							specZoneLocs.add(specLoc);
						}
					}
					
					if (scQt.isZonal() &&  scQt.isPanel() )
					{
						zoneGeneralTrees.add(
								LocationSingleton.getInstance().constuctTreeNodeWithDisableChild(
										zone, specZoneLocs));
						
						if (zoneSuppQualTrees != null) {
							zoneSuppQualTrees.add(
									LocationSingleton.getInstance().constuctTreeNodeWithDisableChild(
											zone, zone.getTplSpecZoneLocs()));}
				
					}else{
						zoneGeneralTrees.add(
								LocationSingleton.getInstance().constuctTreeNode(
										zone, specZoneLocs));
						
						if (zoneSuppQualTrees != null) {
							zoneSuppQualTrees.add(
									LocationSingleton.getInstance().constuctTreeNode(
											zone, zone.getTplSpecZoneLocs()));	
						}
					}
				}
			}
		}
	}
	
	public boolean enableFieldByStatusNFiledName(String fieldName) {
		Long statusId = this.getManagedBean().getCurrentStatusId();

		return enableFieldByStatusNFiledName(fieldName, statusId, 
				getManagedBean().isMaintanceFlow(), 
				!getManagedBean().isReadOnly());

	}
	
	private boolean enableFieldByStatusNFiledName(String fieldName,
			Long statusId, boolean isMaintance, boolean isEditable) {

		List<String> values = QTFieldMapper.getEnableFieldsByStatus(statusId,
				isMaintance, isEditable);
		if (CollectionUtils.isNotEmpty(values)) {
			if (values.contains(fieldName)) {
				return false;
			}

		}
		return true;

	}

	public void onchangeInvBy(String item) {
		try {

			IQTDemoSO qtDemoSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDemoSO.class);
			IQTSpecSO specSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IQTSpecSO.class);
			ITemplateSpecSO templateSpecSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.COMMON_COMPONENT, ITemplateSpecSO.class);
			IScActorSO actorSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IScActorSO.class);

			SelfRequestSupplierAdminReqDTO reqSupDTO = new SelfRequestSupplierAdminReqDTO();
			List<ScQtDemoContact> demoContactList = new ArrayList<ScQtDemoContact>();
			List<ScQtDemoSupplier> demoSuppList = new ArrayList<ScQtDemoSupplier>();
			List<ScQtDemoItem> demoItemList = new ArrayList<ScQtDemoItem>();
			ScQtDemoByItemScore demoByItemScore = new ScQtDemoByItemScore();
			ScQtDemoSchedule scQtDemoSchedule = new ScQtDemoSchedule();
			ScQtDemoSupplier demoSupplier = new ScQtDemoSupplier();
			ScQtDemoItem demoItem = new ScQtDemoItem();
			PmParameter pmParam = new PmParameter();
			ScQtDemo qtDemo = new ScQtDemo();
			DemoReqDTO reqDto = new DemoReqDTO();
			reqDto.setScQt(this.getManagedBean().getQt());
			DemoResDTO resDto = qtDemoSO.retrieveScQtDemoByQtNo(reqDto);
			if (resDto.getScQtDemo() != null && resDto.getScQtDemo().getDemoId() != null) {
				savedData = true;
			}

			if (savedData
					&& this.getManagedBean().getDemoInvBy()
							.equalsIgnoreCase(resDto.getScQtDemo().getDemoCategoryId().getParameterCode())) {
				if (this.getManagedBean().getDemoInvBy().equalsIgnoreCase(SourcingConstants.ALL_ITEM)) {
					/*
					 * BUG 28784 : DEMO INVITATION COMPARATOR
					 */
					reqDto = new DemoReqDTO();
					reqDto.setScQtDemoSupplierList(resDto.getScQtDemo().getScQtDemoSupplierList());
					resDto.setScQtDemoSupplierList(qtDemoSO.sortDemoItem(reqDto, SourcingConstants.ALL_ITEM)
							.getScQtDemoSupplierList());
					this.getManagedBean().setAllItemList(resDto.getScQtDemo().getScQtDemoSupplierList());
					/*
					 * BUG 28784: DEMO INVITATION END
					 */
				} else if (this.getManagedBean().getDemoInvBy().equalsIgnoreCase(SourcingConstants.BY_ITEM)) {
					/*
					 * BUG 28784 : DEMO INVITATION COMPARATOR
					 */
					reqDto = new DemoReqDTO();
					reqDto.setScQtDemoItemList(resDto.getScQtDemo().getScQtDemoItemList());
					resDto.getScQtDemo().setScQtDemoItemList(
							qtDemoSO.sortDemoItem(reqDto, SourcingConstants.BY_ITEM).getScQtDemoItemList());
					/*
					 * BUG 28784 : DEMO INVITATION END
					 */
					this.getManagedBean().setByItemList(resDto.getScQtDemo().getScQtDemoItemList());
				}
				Collections.sort(resDto.getScQtDemo().getScQtDemoContactList(), new PresentationDemoContactListComparator());//sort contact list
				qtDemo = resDto.getScQtDemo();
				this.getManagedBean().setScQtDemoContactList(resDto.getScQtDemo().getScQtDemoContactList());
				this.getManagedBean().setDemoRemark(resDto.getScQtDemo().getDemoInvRemark());
				demoSuppList = resDto.getScQtDemo().getScQtDemoSupplierList();
				demoItemList = resDto.getScQtDemo().getScQtDemoItemList();

			} else if (!savedData) {
				if (this.getManagedBean().getDemoInvBy().equalsIgnoreCase(SourcingConstants.ALL_ITEM)) {
					List<ScQtDemoByItemScore> demoDemoByItemList = new ArrayList<ScQtDemoByItemScore>();
					List<ScQtProposal> qualifiedSupp = new ArrayList<ScQtProposal>();
					SpecificationReqDTO reqDTO = new SpecificationReqDTO();
					List<TplSpecQuestion> specQuestionList = new ArrayList<TplSpecQuestion>();
					demoSuppList = new ArrayList<ScQtDemoSupplier>();
					demoItemList = new ArrayList<ScQtDemoItem>();
					qtDemo = new ScQtDemo();

					reqDTO.setScQt(this.getManagedBean().getQt());
					reqDTO.setSpecType(SourcingConstants.TECHNICAL_DOCTYPE);
					SpecificationResDTO resDTO = specSo.retrieveAllDemoCheckLists(reqDTO);
					for (ScQtCheckList t : resDTO.getTechnicalCheckList()) {
						TemplateSpecReqDTO specReqDTO = new TemplateSpecReqDTO();
						specReqDTO.setSpecMaster(t.getScQtDoc() != null ? t.getScQtDoc().getTplSpecMaster() : null);
						specReqDTO.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION_AND_ASNWER);//#28784 remove supplier without spec answer
						specReqDTO.setDemoSpecView(true);
						TemplateSpecResDTO templateresDTO = templateSpecSO.getTemplateMaster(specReqDTO);
						specQuestionList.addAll(templateresDTO.getSpecMaster().getTplSpecQuestions());
					}

					pmParam = getParameterByTypeAndCode(ParameterTypeConst.ALL_BY_ITEM, SourcingConstants.ALL_ITEM);

					qualifiedSupp = checkQualifiedSupplier();
					
					qualifiedSupp = checkQualifiedSupplierZoneAndItem(qualifiedSupp, specQuestionList);//#28784 remove supplier without spec answer

					qtDemo.setQtId(this.getManagedBean().getQt());
					qtDemo.setDemoCategoryId(pmParam);
					Collections.sort(qualifiedSupp, new QualifiedSupplierComparator());//sort supplier list

					for (ScQtProposal p : qualifiedSupp) {
						scQtDemoSchedule = new ScQtDemoSchedule();
						demoSupplier = new ScQtDemoSupplier();
						demoSupplier.setScQtDemo(qtDemo);
						demoSupplier.setDemoNo(qtDemoSO.generateDemoNo());
						reqSupDTO.setMofNo(p.getMofNo());
						demoSupplier.setSupplierId(SmServiceLookup.getSelfRequestSupplierAdminSO()
								.retrieveSupplierByMofNo(reqSupDTO).getSupplier());
						demoSupplier.setSupplierCode(p.getSupplierNo());
						scQtDemoSchedule.setScQtDemoSupplier(demoSupplier);
						demoSupplier.setScQtDemoSchedule(scQtDemoSchedule);
						demoSuppList.add(demoSupplier);
					}

					this.getManagedBean().setAllItemList(demoSuppList);

					for (TplSpecQuestion s : specQuestionList) {
						demoItem = new ScQtDemoItem();
						demoItem.setScQtDemo(qtDemo);
						demoItem.setTplSpecQuestion(s);
						demoItem.setItemName(s.getClItem());
						demoItemList.add(demoItem);
					}

					for (ScQtDemoItem i : demoItemList) {
						demoDemoByItemList = new ArrayList<>();
						for (ScQtDemoSupplier s : demoSuppList) {
							demoByItemScore = new ScQtDemoByItemScore();
							scQtDemoSchedule = new ScQtDemoSchedule();
							demoByItemScore.setScQtDemoItem(i);
							demoByItemScore.setScQtDemoSupplier(s);
							demoByItemScore.setSupplierId(s.getSupplierId());
							scQtDemoSchedule.setScQtDemoSupplier(s);
							scQtDemoSchedule.setScQtDemoByItemScore(demoByItemScore);
							demoByItemScore.setScQtDemoSchedule(scQtDemoSchedule);
							demoDemoByItemList.add(demoByItemScore);
						}
						i.setScQtDemoByItemScoreList(demoDemoByItemList);
					}
					this.getManagedBean().setByItemList(demoItemList);

				} else if (this.getManagedBean().getDemoInvBy().equalsIgnoreCase(SourcingConstants.BY_ITEM)) {
					List<ScQtDemoByItemScore> demoDemoByItemList = new ArrayList<ScQtDemoByItemScore>();
					List<TplSpecQuestion> specQuestionList = new ArrayList<TplSpecQuestion>();
					List<ScQtProposal> qualifiedSupp = new ArrayList<ScQtProposal>();
					SpecificationReqDTO reqDTO = new SpecificationReqDTO();
					demoSuppList = new ArrayList<ScQtDemoSupplier>();
					demoItemList = new ArrayList<ScQtDemoItem>();
					qtDemo = new ScQtDemo();

					reqDTO.setScQt(this.getManagedBean().getQt());
					reqDTO.setSpecType(SourcingConstants.TECHNICAL_DOCTYPE);
					SpecificationResDTO resDTO = specSo.retrieveAllDemoCheckLists(reqDTO);
					for (ScQtCheckList t : resDTO.getTechnicalCheckList()) {
						TemplateSpecReqDTO specReqDTO = new TemplateSpecReqDTO();
						specReqDTO.setSpecMaster(t.getScQtDoc() != null ? t.getScQtDoc().getTplSpecMaster() : null);
						specReqDTO.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION_AND_ASNWER);//#28784 remove supplier without spec answer
						specReqDTO.setDemoSpecView(true);
						specReqDTO.setQuestionType(TemplateSpecConst.QUESTION_TYPE_ITEM);// #28784
						TemplateSpecResDTO templateresDTO = templateSpecSO.getTemplateMaster(specReqDTO);
						specQuestionList.addAll(templateresDTO.getSpecMaster().getTplSpecQuestions());
					}

					pmParam = getParameterByTypeAndCode(ParameterTypeConst.ALL_BY_ITEM, SourcingConstants.BY_ITEM);
					qtDemo.setQtId(this.getManagedBean().getQt());
					qtDemo.setDemoCategoryId(pmParam);
					qualifiedSupp = checkQualifiedSupplier();
					qualifiedSupp = checkQualifiedSupplierZoneAndItem(qualifiedSupp, specQuestionList);//#28784 remove supplier without spec answer
					Collections.sort(qualifiedSupp, new QualifiedSupplierComparator());//sort supplier list

					for (ScQtProposal p : qualifiedSupp) {
						demoSupplier = new ScQtDemoSupplier();
						demoSupplier.setScQtDemo(qtDemo);
						demoSupplier.setDemoNo(qtDemoSO.generateDemoNo());
						reqSupDTO.setMofNo(p.getMofNo());
						demoSupplier.setSupplierId(SmServiceLookup.getSelfRequestSupplierAdminSO()
								.retrieveSupplierByMofNo(reqSupDTO).getSupplier());
						demoSupplier.setSupplierCode(p.getSupplierNo());
						demoSuppList.add(demoSupplier);
					}

					for (TplSpecQuestion s : specQuestionList) {
						demoItem = new ScQtDemoItem();
						demoItem.setScQtDemo(qtDemo);
						demoItem.setTplSpecQuestion(s);
						demoItem.setItemName(s.getClItem());
						demoItemList.add(demoItem);
					}

					for (ScQtDemoItem i : demoItemList) {
						demoDemoByItemList = new ArrayList<>();
						for (ScQtDemoSupplier s : demoSuppList) {
							demoByItemScore = new ScQtDemoByItemScore();
							scQtDemoSchedule = new ScQtDemoSchedule();

							demoByItemScore.setScQtDemoItem(i);
							demoByItemScore.setScQtDemoSupplier(s);
							demoByItemScore.setSupplierId(s.getSupplierId());
							scQtDemoSchedule.setScQtDemoSupplier(s);
							scQtDemoSchedule.setScQtDemoByItemScore(demoByItemScore);
							demoByItemScore.setScQtDemoSchedule(scQtDemoSchedule);
							demoDemoByItemList.add(demoByItemScore);
						}
						i.setScQtDemoByItemScoreList(demoDemoByItemList);
					}
					this.getManagedBean().setByItemList(demoItemList);

				}

				ScActorReqDTO reqDTO = new ScActorReqDTO();
				reqDTO.setDocId(this.getManagedBean().getQt().getQtId());
				reqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);
				reqDTO.setRoleCode(RoleCode.DESK_OFFICER.value());
				ScActorResDTO resActorDto = actorSO.retrieveActorByTypeIdAndRoleCode(reqDTO);
				UserResDTO resDTO = retrieveUserByUserId(resActorDto.getScActor().getUserId());

				ScQtDemoContact demoCtc = new ScQtDemoContact();
				demoCtc.setUserName(resDTO.getPmUser().getUserName());
				demoCtc.setPmUser(resDTO.getPmUser());
				demoCtc.setTelephoneNo(resDTO.getPmUser().getPhoneNo());
				demoCtc.setFaxNo(resDTO.getPmUser().getFaxNo());
				demoCtc.setEmail(resDTO.getPmUser().getEmail());
				demoCtc.setDeskofficer(true);
				demoCtc.setScQtDemo(qtDemo);
				demoContactList.add(demoCtc);
				Collections.sort(demoContactList, new PresentationDemoContactListComparator());//sort contact list
				this.getManagedBean().setScQtDemoContactList(demoContactList);
				this.getManagedBean().setCurrentOfficer(resDTO.getPmUser().getUserName());

			} else if (savedData
					&& !this.getManagedBean().getDemoInvBy()
							.equalsIgnoreCase(resDto.getScQtDemo().getDemoCategoryId().getParameterCode())) {
				qtDemo = resDto.getScQtDemo();
				demoItemList = new ArrayList<ScQtDemoItem>();
				List<ScQtDemoByItemScore> demoDemoByItemList = new ArrayList<ScQtDemoByItemScore>();

				/*
				 * BUG 28784 : DEMO INVITATION COMPARATOR
				 */
				reqDto = new DemoReqDTO();
				reqDto.setScQtDemoSupplierList(resDto.getScQtDemo().getScQtDemoSupplierList());
				reqDto.setScQtDemoItemList(resDto.getScQtDemo().getScQtDemoItemList());
				demoSuppList
						.addAll(qtDemoSO.sortDemoItem(reqDto, SourcingConstants.ALL_ITEM).getScQtDemoSupplierList());
				demoItemList.addAll(qtDemoSO.sortDemoItem(reqDto, SourcingConstants.BY_ITEM).getScQtDemoItemList());
				/*
				 * BUG 28784 : DEMO INVITATION END
				 */

				demoContactList.addAll(resDto.getScQtDemo().getScQtDemoContactList());

				if (demoSuppList.isEmpty() && this.getManagedBean().getTempDemoSuppList() != null) {
					demoSuppList.addAll(this.getManagedBean().getTempDemoSuppList());
				}

				if (this.getManagedBean().getDemoInvBy().equalsIgnoreCase(SourcingConstants.ALL_ITEM)) {
					pmParam = getParameterByTypeAndCode(ParameterTypeConst.ALL_BY_ITEM, SourcingConstants.ALL_ITEM);

					for (ScQtDemoSupplier s : demoSuppList) {
						scQtDemoSchedule = new ScQtDemoSchedule();
						scQtDemoSchedule.setScQtDemoSupplier(s);
						scQtDemoSchedule.setScQtDemoByItemScore(null);
						s.setScQtDemoSchedule(scQtDemoSchedule);
					}

					this.getManagedBean().setAllItemList(demoSuppList);
					/*
					 * BUG 28784 : DEMO INVITATION END
					 */
				} else {
					pmParam = getParameterByTypeAndCode(ParameterTypeConst.ALL_BY_ITEM, SourcingConstants.BY_ITEM);
					if (demoItemList.isEmpty() && !this.getManagedBean().getTempDemoItemList().isEmpty()) {
						demoItemList.addAll(this.getManagedBean().getTempDemoItemList());
					}

					for (ScQtDemoItem i : demoItemList) {
						demoDemoByItemList.addAll(i.getScQtDemoByItemScoreList());
					}

					for (ScQtDemoSupplier s : demoSuppList) {
						for (ScQtDemoByItemScore sc : demoDemoByItemList) {
							if (sc.getScQtDemoSupplier().getDemoSupplierId().equals(s.getDemoSupplierId())) {
								scQtDemoSchedule = new ScQtDemoSchedule();
								scQtDemoSchedule.setScQtDemoSupplier(s);
								scQtDemoSchedule.setScQtDemoByItemScore(sc);
								sc.setScQtDemoSchedule(scQtDemoSchedule);
							}
						}
					}

					getManagedBean().setByItemList(demoItemList);
				}
			}

			qtDemo.setScQtDemoSupplierList(demoSuppList);
			qtDemo.setScQtDemoItemList(demoItemList);
			Collections.sort(demoContactList, new PresentationDemoContactListComparator());//sort contact list
			qtDemo.setScQtDemoContactList(demoContactList);
			qtDemo.setDemoCategoryId(pmParam);
			this.getManagedBean().setScQtDemo(qtDemo);

		} catch (Exception e) {
			//Sonarqube QT Vulnerability Minor #44
			SLAUtils.getInstance().logSLAError("QT", "OnChangeInvByButton",
					this.getManagedBean().getQt().getQtId().toString(), "onchangeInvBy",
					getUserIdSessionFromLoginId(), e);
		}
	}

	private UserResDTO retrieveUserByUserId(Long userId) {

		IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
				IUserSO.class);

		UserReqDTO userReqDTO = new UserReqDTO();
		userReqDTO.setUserId(userId);
		UserResDTO resDTO = userSO.retrieveUserByUserId(userReqDTO);

		return resDTO;
	}

	public Long retirveStatusId() {
		if (null != this.getManagedBean().getTaskName()) {
			return this.getManagedBean().getCurrentStatusId();
		}
		return null;
	}

	public void demoSpectechnicalChecklist(int index, ScQtCheckList checkList, String specType) {
		try {

			if (QTConstants.TEMPLETE_SPEC_CODE.equalsIgnoreCase(checkList.getDefinationType().getParameterCode())) {
				ITemplateSpecSO templateSpecSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.COMMON_COMPONENT, ITemplateSpecSO.class);
				TemplateSpecReqDTO specReqDTO = new TemplateSpecReqDTO();
				specReqDTO.setSpecMaster(checkList.getScQtDoc().getTplSpecMaster());
				specReqDTO.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
				TemplateSpecResDTO templateresDTO = null;
				specReqDTO.setDemoSpecView(true);
				templateresDTO = templateSpecSO.getTemplateMaster(specReqDTO);
				TemplateSpecBean templateSpecBean = getManagedBean().getTemplateSpecBean();
				if (null != templateresDTO) {
					checkList.getScQtDoc().setTplSpecMaster(templateresDTO.getSpecMaster());
				}

				checkList.getScQtDoc().getTplSpecMaster().setTechnicalSpecQuestionsToDelete(null);
				templateSpecBean.setSpecMaster(checkList.getScQtDoc().getTplSpecMaster());
				this.getManagedBean().setTemplateSpecBean(templateSpecBean);

			}
		} catch (Exception e) {
			//Sonarqube QT Vulnerability Minor #44
			SLAUtils.getInstance().logSLAError("QT", "GetDemoSpectechnicalChecklist",
					this.getManagedBean().getQt().getQtId().toString(), "demoSpectechnicalChecklist",
					getUserIdSessionFromLoginId(), e);
		}

	}

	public String demoInvitationCancel() {
		IQTDetailSO detailSO;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IQTDetailSO.class);
			QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
			ScQt scQt = this.getManagedBean().getQt();
		
			quationTenderReqDTO.setQtId(scQt.getQtId());
			quationTenderReqDTO.setApprovalCategory(ParameterConst.APPROVAL_CATEGORY_FINALIZATION_COMMITTEE);
			QuationTenderResDTO resDTO = detailSO.retrieveApprovalsByQtIdNType(quationTenderReqDTO);
			this.getManagedBean().setQt(resDTO.getQtDetail());
			ScQtApproval qtApproval = resDTO.getQtApproval();

			List<ScQtApprovalRemark> qtRemarks = initializeRemarks(qtApproval, this.getManagedBean().getQt()
					.getCurrentWorkFlowStatus(), qtApproval.getScQtApprovalRemarks(), ScQtApprovalRemark.class);
			for (ScQtApprovalRemark qtRemark : qtRemarks) {
				qtRemark.setEditable(false);
			}
			qtApproval.setScQtApprovalRemarks(qtRemarks);
			TabBean tabBean = new TabBean(null, null, qtApproval, retrieveFinalizationCommitteeType());
			this.getManagedBean().setSelectedApprovalTabBean(tabBean);

			return "/xhtml/sc/demoInvitation/demoInvitationCancel.xhtml";

		} finally {
			detailSO = null;
		}
	}

	public List<SelectItem> getAllByItemDropDown() {
		List<SelectItem> newItems = new ArrayList<SelectItem>();
		SelectItem[] items = getDropdownCodeValues(ParameterTypeConst.ALL_BY_ITEM);
		for (SelectItem s : items) {
			if (s.getValue() != null) {
				newItems.add(s);
			}
		}
		Collections.sort(newItems, new SortComparator());
		return newItems;
	}

	private static class SortComparator implements Comparator<SelectItem> {
		public int compare(SelectItem s1, SelectItem s2) {
			return s1.getLabel().compareToIgnoreCase(s2.getLabel());
		}
	}

	public void addNewContactPerson() {
		ScQtDemoContact demoContact = new ScQtDemoContact();
		List<ScQtDemoContact> demoConList = new ArrayList<ScQtDemoContact>();

		demoContact.setScQtDemo(this.getManagedBean().getScQtDemo());
		demoConList = this.getManagedBean().getScQtDemoContactList();
		demoConList.add(demoContact);
		this.getManagedBean().setScQtDemoContactList(demoConList);
	}

	public void deleteContactPerson() {
		IQTDemoSO qtDemoSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
				IQTDemoSO.class);

		List<ScQtDemoContact> selectedDemoContact = new ArrayList<ScQtDemoContact>();
		List<ScQtDemoContact> demoConList = this.getManagedBean().getScQtDemoContactList();
		selectedDemoContact = this.getManagedBean().getSelectedContactList();

		for (ScQtDemoContact c : selectedDemoContact) {
			if (c.isDeskofficer()) {
				throw new EpException(MessageConst.ERROR_760526);
			} else if (c.getDemoContactId() != null) {
				qtDemoSO.removeContactPerson(c);
			}
			demoConList.remove(c);
		}
		this.getManagedBean().setScQtDemoContactList(demoConList);
		if (this.getManagedBean().getScQtDemoContactList().size() == 1) {
			this.getManagedBean().setSelectedContactList(null);
			this.getManagedBean().setSelectAll(false);
		}
	}

	public DemoResDTO saveDemoInvitation() {
		IQTDemoSO qtDemoSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
				IQTDemoSO.class);
		DemoReqDTO reqDto = new DemoReqDTO();
		ScQtDemo scQtDemo = this.getManagedBean().getScQtDemo();		
		String process = BpmOutcomeConst.BPM_SAVE;
		if (scQtDemo == null) {
			throw new EpException(MessageConst.ERROR_760531);
		}
		this.getManagedBean().setTempDemoSuppList(scQtDemo.getScQtDemoSupplierList());
		this.getManagedBean().setTempDemoItemList(scQtDemo.getScQtDemoItemList());

		scQtDemo.setDemoInvRemark(this.getManagedBean().getDemoRemark());
		// #28784 - Start
		scQtDemo.setScQtDemoItemList(getManagedBean().getByItemList());
		scQtDemo.setScQtDemoSupplierList(getManagedBean().getAllItemList());
		scQtDemo.setScQtDemoContactList(getManagedBean().getScQtDemoContactList());
	    // #28784 - Start
		reqDto.setScQtDemo(scQtDemo);
		reqDto.setScQt(this.getManagedBean().getQt());
		DemoResDTO resDto = qtDemoSO.retrieveScQtDemoByQtNo(reqDto);

		if (resDto.getScQtDemo() != null) {
			if (scQtDemo.getDemoCategoryId().getParameterId() == null) {
				scQtDemo.setDemoCategoryId(resDto.getScQtDemo().getDemoCategoryId());
			}

			if (scQtDemo.getDemoCategoryId() != null
					&& !resDto.getScQtDemo().getDemoCategoryId().getParameterCode()
							.equalsIgnoreCase(this.getManagedBean().getDemoInvBy())) {
				if (this.getManagedBean().getDemoInvBy().equalsIgnoreCase(SourcingConstants.ALL_ITEM)) {
					scQtDemo.setScQtDemoItemList(null);
					reqDto.setDemoItemToBeDeleted(resDto.getScQtDemo().getScQtDemoItemList());					
				} else if (this.getManagedBean().getDemoInvBy().equalsIgnoreCase(SourcingConstants.BY_ITEM)) {
					scQtDemo.setScQtDemoSupplierList(null);
					reqDto.setSupplierScheduleToBeDeleted(resDto.getScQtDemo().getScQtDemoSupplierList());				
				}
			}
			validateDemoInvitationFields(process);
			reqDto.setExistedData(true);
			resDto = qtDemoSO.saveDemoInvitation(reqDto);

		} else {
			if (this.getManagedBean().getDemoInvBy().equalsIgnoreCase(SourcingConstants.ALL_ITEM)) {
				for (ScQtDemoItem item : reqDto.getScQtDemo().getScQtDemoItemList()) {
					for (ScQtDemoByItemScore score : item.getScQtDemoByItemScoreList()) {
						score.setScQtDemoSchedule(null);
					}
				}
			}
			resDto = qtDemoSO.saveDemoInvitation(reqDto);
		}

		if (resDto.isSuccessful()) {
			resDto = qtDemoSO.retrieveScQtDemoByQtNo(reqDto);
			this.getManagedBean().setScQtDemo(resDto.getScQtDemo());
			this.getManagedBean().setScQtDemoContactList(resDto.getScQtDemo().getScQtDemoContactList());
			
			/*
			 * BUG 28784 : DEMO INVITATION COMPARATOR 
			 */
			reqDto = new DemoReqDTO();
			reqDto.setScQtDemoItemList(resDto.getScQtDemo().getScQtDemoItemList());		
			reqDto.setScQtDemoSupplierList(resDto.getScQtDemo().getScQtDemoSupplierList());
			resDto.getScQtDemo().setScQtDemoItemList(qtDemoSO.sortDemoItem(reqDto,SourcingConstants.BY_ITEM).getScQtDemoItemList());
			resDto.getScQtDemo().setScQtDemoSupplierList(qtDemoSO.sortDemoItem(reqDto,SourcingConstants.ALL_ITEM).getScQtDemoSupplierList());
			this.getManagedBean().setAllItemList(resDto.getScQtDemo().getScQtDemoSupplierList());	
			this.getManagedBean().setByItemList(resDto.getScQtDemo().getScQtDemoItemList());
			/*
			 * BUG 28784 : DEMO INVITATION END
			 */			
					
			if (action == null) {
				successMessage(MessageConst.INFO_700004, getMessageCommon("lbl.demo.save.msg"), this.getManagedBean()
						.getQt().getQtNo(), Boolean.FALSE);
			}
			resDto.setSuccessful(true);
			return resDto;
		}

		return null;
	}

	public String submitDemoInvitation() {
		IQTDemoSO qtDemoSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
				IQTDemoSO.class);
		DemoReqDTO reqDto = new DemoReqDTO();
		action = BpmOutcomeConst.BPM_SUBMIT;
		validateDemoInvitationFields(action);//#28784 - move position
		DemoResDTO resDTO = saveDemoInvitation();		
		if (resDTO.isSuccessful()) {
			reqDto.setScQt(this.getManagedBean().getQt());
			resDTO = qtDemoSO.retrieveScQtDemoByQtNo(reqDto);
			String outcome = BpmOutcomeConst.BPM_SUBMIT;
			resDTO.setPmUser(new FacesScopes().getUserSessionBean());
			resDTO.setOutcome(outcome);
			resDTO.setScQt(this.getManagedBean().getQt());
			resDTO = demoUpdateBpmTask(resDTO, outcome);
			if (resDTO.isSuccessful()) {
				resolveAcknowledgement(resDTO.getScQt());
				renewSesionObject();
				return CommonUIConst.ACKNOWLEDGE_PAGE;
			}
		}
		return null;
	}

	private void validateDemoInvitationFields(String process) {
		ScQtDemo scQtDemo = this.getManagedBean().getScQtDemo();
		if (scQtDemo != null && process.equals(BpmOutcomeConst.BPM_SUBMIT)) {
			if (this.getManagedBean().getDemoInvBy().equalsIgnoreCase(SourcingConstants.ALL_ITEM)) {
				for (ScQtDemoSupplier sup : scQtDemo.getScQtDemoSupplierList()) {
					if (sup.getScQtDemoSchedule().getDemoLocation() == ""
							|| sup.getScQtDemoSchedule().getDemoStartDate() == null
							|| sup.getScQtDemoSchedule().getDemoEndDate() == null) {
						action = null;//#28784 - Reset action
						throw new EpException(MessageConst.ERROR_760531);
					}
				}

			} else if (this.getManagedBean().getDemoInvBy().equalsIgnoreCase(SourcingConstants.BY_ITEM)) {
				for (ScQtDemoItem item : scQtDemo.getScQtDemoItemList()) {
					for (ScQtDemoByItemScore score : item.getScQtDemoByItemScoreList()) {
						if (score.getScQtDemoSchedule().getDemoLocation() == ""
								|| score.getScQtDemoSchedule().getDemoStartDate() == null
								|| score.getScQtDemoSchedule().getDemoEndDate() == null) {
							action = null;//#28784 - Reset action
							throw new EpException(MessageConst.ERROR_760541);
						}
					}
				}
			}
		}
	}

	private void resolveAcknowledgement(ScQt scQt) {
		String message = MessageUtils.getMessage(getMessgeConstant("SUBMIT"), new String[] {
				getMessageCommon("lbl.demo.presentation.submit"), scQt.getQtNo() },
				MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale());

		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null, message));

	}

	public DemoResDTO demoUpdateBpmTask(DemoResDTO resDTO, String outcome) {
		try {
			FacesScopes facesScopes = new FacesScopes();
			String taskName = null;
			String taskId = null;
			String languageCode = new FacesScopes().getUserLocale().getLanguage();
			resDTO.setLanguageCode(languageCode);
			IQTDemoSO qtDemoSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDemoSO.class);
			QuationTenderReqDTO qtResDTO = new QuationTenderReqDTO();
			qtResDTO.setQtdetail(resDTO.getScQt());
			if (!resDTO.getOutcome().equals(BpmOutcomeConst.BPM_APPROVE)
					&& !resDTO.getOutcome().equals(BpmOutcomeConst.BPM_SUBMIT)) {
				getQTDetailSO().saveQTDtl(qtResDTO);// for cancelation only
			}

			DemoReqDTO demoReqDTO = new DemoReqDTO();
			String committeeType = (this.getManagedBean().getCommitteeType() != null ? this.getManagedBean()
					.getCommitteeType() : ParameterConst.DOCTYPE_TEC_COMMITTE);
			demoReqDTO.setCommitteType(committeeType);
			ScQt scqt = (this.getManagedBean().getQt() != null ? this.getManagedBean().getQt() : resDTO.getScQt());
			demoReqDTO.setQtId(scqt.getQtId());

			DemoResDTO resNewDTO = qtDemoSO.prepareCommiteeDtls(demoReqDTO);
			ScQtCommittee qtCommittee = resNewDTO.getQtCommittee();

			if (this.getManagedBean().getPayLoad() == null) {
				this.getManagedBean().setPayLoad(resDTO.getPayLoad());
			}

			if (null != qtCommittee) {
				this.getManagedBean().setScQtCommittee(qtCommittee);

				ScCommittee sccommittee = resNewDTO.getQtCommittee().getScCommittee();
				List<ScCommitteeMember> localList = new ArrayList<ScCommitteeMember>();
				if (null != sccommittee.getScCommitteeMembers() && !sccommittee.getScCommitteeMembers().isEmpty()) {
					localList.addAll(sccommittee.getScCommitteeMembers());
				}

				Collections.sort(localList);
				this.getManagedBean().setCommitteeMemberActivity(localList);

				Long loggedInUserId = getFacesScopes().getUserSessionBean().getUserId();
				UserResDTO userResDTO = retrieveUserByUserId(loggedInUserId);
				resDTO.setCurrentOfficer(userResDTO.getPmUser().getUserName());
				if (resDTO.getScQt() == null) {
					resDTO.setScQt(scqt);
				}

				if (this.getManagedBean().getTaskId() == null || this.getManagedBean().getTaskId() == null) {
					taskId = resDTO.getTaskId();
					taskName = resDTO.getTaskName();

				} else {
					taskId = this.getManagedBean().getTaskId();
					taskName = this.getManagedBean().getTaskName();
				}

				resDTO.setPmUser(userResDTO.getPmUser());
				demoReqDTO.setTaskId(taskId);
				demoReqDTO.setTaskName(taskName);// #25037 & #28784
				demoReqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
				demoReqDTO.setPayLoad(this.getManagedBean().getPayLoad());
				demoReqDTO.setPmUser(facesScopes.getUserSessionBean());
				demoReqDTO.setOutcome(outcome);
				resDTO = qtDemoSO.updateBPMAction(resDTO, demoReqDTO);

				renewSesionObject();
			}

		} catch (Exception e) {
			//Sonarqube QT Vulnerability Minor #44
			SLAUtils.getInstance().logSLAError("QT", "DemoUpdateBpmTask",
					this.getManagedBean().getQt().getQtId().toString(), "demoUpdateBpmTask",
					getUserIdSessionFromLoginId(), e);
		}

		return resDTO;
	}

	public List<ScQtProposal> checkQualifiedSupplier() {
		List<ScQtProposal> qualifiedSupp = null;
		IQTEvaluationSO evaluationSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
		try {
			qualifiedSupp = evaluationSO.getQualifiedSuppliersBasedOnStage(this.getManagedBean().getQt().getQtId(),
					SourcingConstants.EVAL_STAGE_TECHNICAL, true);
		} catch (Exception e) {
			//Sonarqube QT Vulnerability Minor #44
			SLAUtils.getInstance().logSLAError("QT", "CheckQualifiedSupplier",
					this.getManagedBean().getQt().getQtId().toString(), "checkQualifiedSupplier",
					getUserIdSessionFromLoginId(), e);
		}

		return qualifiedSupp;
	}

	public void demoCancel() {
		retrieveQuotationTenderDetails(this.getManagedBean().getQt().getQtId(), this.getManagedBean()
				.getCommitteeType());
		this.getManagedBean().setShowCancelButton(false);
		this.getManagedBean().setActiveIndex(0);
	}

	public SelectItem[] retrieveFinalizationCommitteeType() {

		ScQt scQt = getManagedBean().getQt();
		SelectItem[] finalizationSecratary = getManagedBean().getFinalizationSecratary();
		ICommitteSO committeSO;
		try {
			committeSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					ICommitteSO.class);

			CommitteReqDTO reqDTO = new CommitteReqDTO();
			reqDTO.setQtId(scQt.getQtId());
			CommitteResDTO resDTO = committeSO.retrieveFinalizationCommitteeType(reqDTO);

			List<PmParameter> finalizationCommTypes = resDTO.getFinalizationSecratry();
			if (CollectionUtils.isNotEmpty(finalizationCommTypes)) {
				finalizationSecratary = new SelectItem[finalizationCommTypes.size()];
				for (int i = 0; i < finalizationCommTypes.size(); i++) {
					String commTypeDesc = getParameterDesc(finalizationCommTypes.get(i),
							new FacesScopes().getUserLocale());
					finalizationSecratary[i] = new SelectItem(finalizationCommTypes.get(i).getParameterId(),
							commTypeDesc);
				}
				getManagedBean().setFinalizationSecratary(finalizationSecratary);
			}

		} finally {
			committeSO = null;
		}

		return finalizationSecratary;
	}

	public List<PmUser> retrievePmUserSecratryDtl(String committeeType) {
		log.info(":::retrievePmUserSecratryDtl() committeeType: " + committeeType);
		this.getManagedBean().setPmUsersList(null);
		ICommitteSO committeSO;

		try {
			committeSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					ICommitteSO.class);

			CommitteReqDTO reqDTO = new CommitteReqDTO();
			reqDTO.setPmOrgProfile(getManagedBean().getQt().getPmOrgProfile());
			reqDTO.setCommitteeType(committeeType);

			CommitteResDTO resDTO = committeSO.retrieveFcSecretaryUsersByCommType(reqDTO);
			this.getManagedBean().setPmUsersList(resDTO.getUserList());

			return resDTO.getUserList();

		} finally {
			committeSO = null;
		}
	}

	public void changePbSecretaryCommType(ScQtApproverSettings approverSettings) {
		Long commTypeId = approverSettings.getCommitteeType().getParameterId();
		if (null != commTypeId) {
			PmParameter commTypeCode = ParameterHelper.getInstance().getPmParameterById(commTypeId);
			approverSettings.setCommitteeType(commTypeCode);

			getManagedBean().getSelectedApprovalTabBean().getScQtApproval().getApproverSettings().get(0)
					.setPmUser(new PmUser());
		}
	}

	public void updateUserIdToSettings(AjaxBehaviorEvent event) {
		Long id = (Long) event.getComponent().getAttributes().get("value");
		List<PmUser> userList = this.getManagedBean().getPmUsersList();
		for (PmUser user : userList) {
			if (user.getUserId().equals(id)) {
				this.getManagedBean().getSelectedApprovalTabBean().getScQtApproval().getApproverSettings().get(0)
						.setPmUser(user);
				break;
			}
		}
	}

	public void submitQtCancelDetails() {
		try {
			DemoResDTO resDTO = new DemoResDTO();
			ScQt qt = this.getManagedBean().getQt();
			String ackMsg = null;
			if (isQTStatusPublished(qt.getQtStatus().getStatusId())) {
				qt.setScQtCommittees(null);
				qt.setScQtBriefings(null);
				qt.setScQtStatus(null);

				ScQtApproval approval = this.getManagedBean().getSelectedApprovalTabBean().getScQtApproval();
				List<ScQtApprovalRemark> remarks = approval.getScQtApprovalRemarks();
				if (null != remarks) {
					for (int i = 0; i < remarks.size(); i++) {
						String userRemark = remarks.get(i).getRemark();
						remarks.get(i).setDocId(qt.getQtId());
						if (null == userRemark || "".equals(userRemark)) {
							throw new EpException(
									MessageConst.ERROR_100022,
									new String[] { ResourceBundleUtils.getMessage("lbl.sc.remark",
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()) });
						}
					}
				}

				List<ScQtApproval> scQtApprovals = new ArrayList<ScQtApproval>();
				scQtApprovals.add(approval);
				qt.setScQtApprovals(scQtApprovals);
				resDTO.setScQt(qt);
				resDTO.setPmUser(new FacesScopes().getUserSessionBean());

				ackMsg = MessageConst.INFO_700052;
			} else {
				List<ScQtRemark> remarks = qt.getScQtRemarks();
				if (null != remarks) {
					for (int i = 0; i < remarks.size(); i++) {
						String userRemark = remarks.get(i).getRemark();
						if (null == userRemark || "".equals(userRemark)) {
							throw new EpException(
									MessageConst.ERROR_100022,
									new String[] { ResourceBundleUtils.getMessage("lbl.sc.remark",
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()) });
						}
					}
				}
				finalizeRemarks(qt.getScQtRemarks());
				qt.setScQtStatus(null);
				qt.setScQtCommittees(null);
				qt.setScQtApprovals(null);
				qt.setScQtBriefings(null);
				resDTO.setScQt(qt);
				resDTO.setPmUser(new FacesScopes().getUserSessionBean());

				ackMsg = MessageConst.INFO_700031;
			}

			String outcome = BpmOutcomeConst.BPM_CANCEL;
			resDTO.setOutcome(outcome);
			resDTO = demoUpdateBpmTask(resDTO, outcome);

			if (resDTO.isSuccessful()) {
				successMessage(ackMsg, ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_QUOTATION,
						qt.getQtNo(), Boolean.TRUE);
			}
		} finally {
		}

	}

	public void handleFileUpload(FileUploadEvent event) throws FileNotFoundException, IOException {
		if (event.getFile() != null) {
			ScAttachment scAttachment = null;
			ScQtApprovalAttachment attachment = new ScQtApprovalAttachment();
			ScQtApproval scQtApproval = this.getManagedBean().getCurrentApproval();

			List<ScQtApprovalAttachment> approvalAttachments = scQtApproval.getScQtApprovalAttachments();
			if (null == approvalAttachments) {
				approvalAttachments = new ArrayList<ScQtApprovalAttachment>();
			}
			attachment.setScQtApproval(scQtApproval);
			attachment.setDocNo(scQtApproval.getScQt().getQtNo());
			approvalAttachments.add(attachment);
			scQtApproval.setScQtApprovalAttachments(approvalAttachments);
			scAttachment = attachment;
			uploadAttachment(event.getFile(), scAttachment);
		}
	}
	
	//#28784 reeval bug - Start
	private boolean checkExistingDemoSupplier(List<ScQtDemoSupplier> demoSupplier) {
		boolean demoInvalid = false;
		List<Long> qualifiedSupp = null;
		IQTEvaluationSO evaluationSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
		try {
			qualifiedSupp = evaluationSO.retrieveQualifiedSupplierListBasedOnStage(this.getManagedBean().getQt()
					.getQtId(), SourcingConstants.EVAL_STAGE_TECHNICAL, true);
			List<Long> supplierList = new ArrayList<>();
			if (!qualifiedSupp.isEmpty()) {
				if (null != demoSupplier && !demoSupplier.isEmpty()) {
					// compare demo supplier list and qualified supplier
					if (demoSupplier.size() != qualifiedSupp.size()) {
						demoInvalid = true;
					} else {
						for (ScQtDemoSupplier supplier : demoSupplier) {
							if (qualifiedSupp.contains(supplier.getSupplierId().getSupplierId())) {
								// no changes
							} else {
								demoInvalid = true;
							}
							supplierList.add(supplier.getSupplierId().getSupplierId());
						}

						if (!supplierList.isEmpty()) {
							for (Long supp : qualifiedSupp) {
								if (supplierList.contains(supp)) {
									// no changes
								} else {
									demoInvalid = true;
								}
							}
						}
					}
				}
			}

		} catch (Exception e) {
			//Sonarqube QT Vulnerability Minor #44
			SLAUtils.getInstance().logSLAError("QT", "CheckExistingDemoSupplier",
					this.getManagedBean().getQt().getQtId().toString(), "checkExistingDemoSupplier",
					getUserIdSessionFromLoginId(), e);
		}

		return demoInvalid;
	}
	//#28784 reeval bug - End
	
	//#28784 remove supplier without spec answer - Start
	private List<ScQtProposal> checkQualifiedSupplierZoneAndItem(List<ScQtProposal> qualifiedSupp, List<TplSpecQuestion> specQuestionList) {
		List<Long> supplierWithAnswer = new ArrayList<>();
		List<ScQtProposal> supplierWithoutAnswer = new ArrayList<>();
		
		for (TplSpecQuestion ques : specQuestionList) {
			for (TplSpecAnswer ans : ques.getTplSpecAnswers()) {
				if (!supplierWithAnswer.contains(ans.getTplSpecSuppAnswer().getSmSupplier().getSupplierId())) {
					supplierWithAnswer.add(ans.getTplSpecSuppAnswer().getSmSupplier().getSupplierId());
					log.info("supp Id: " + ans.getTplSpecSuppAnswer().getSmSupplier().getSupplierId());
				}
			}
		}
		
		for (ScQtProposal proposal : qualifiedSupp) {
			if (!supplierWithAnswer.contains(proposal.getScQtSupplier().getSmSupplier().getSupplierId())) {
				supplierWithoutAnswer.add(proposal);
			}
		}
		
		if (CollectionUtils.isNotEmpty(supplierWithoutAnswer)) {
			qualifiedSupp.removeAll(supplierWithoutAnswer);
		}
		
		return qualifiedSupp;
	}
	//#28784 remove supplier without spec answer - End
	
	private static class PresentationDemoContactListComparator implements Comparator<ScQtDemoContact> {
		public int compare(ScQtDemoContact s1, ScQtDemoContact s2) {
			return s1.getDemoContactId().compareTo(s2.getDemoContactId());
		}
	}
	
	private static class QualifiedSupplierComparator implements Comparator<ScQtProposal> {
		public int compare(ScQtProposal s1, ScQtProposal s2) {
			return s1.getSupplierNo().compareTo(s2.getSupplierNo());
		}
	}
	
	/**
	 * Update list of contact person to be delete
	 * 
	 * @param cont
	 * @param selectAll
	 */
	public void addToBeRemoveContactPerson(ScQtDemoContact cont, boolean selectAll) {
		List<ScQtDemoContact> selectedDemoContact = new ArrayList<>();
		List<ScQtDemoContact> demoConList = this.getManagedBean().getScQtDemoContactList();
		if (this.getManagedBean().getSelectedContactList() == null) {
			this.getManagedBean().setSelectedContactList(new ArrayList<ScQtDemoContact>());
		}
		if (selectAll && this.getManagedBean().isSelectAll()) {
			setSelectAllSupplier(demoConList, selectedDemoContact);
		} else if (selectAll && !this.getManagedBean().isSelectAll()) {
			setUnselectAllSupplier(demoConList, selectedDemoContact);
		} else {
			if (cont.isDisableRowSelection()) {
				this.getManagedBean().getSelectedContactList().add(cont);
			} else {
				this.getManagedBean().getSelectedContactList().remove(cont);
			}
		}
	}
	
	private void setSelectAllSupplier(List<ScQtDemoContact> demoConList, List<ScQtDemoContact> selectedDemoContact) {
		for (ScQtDemoContact c : demoConList) {
			if (c.getPmUser() == null) {
				c.setDisableRowSelection(true);
				selectedDemoContact.add(c);
			}
		}
		this.getManagedBean().setSelectedContactList(selectedDemoContact);
	}
	
	private void setUnselectAllSupplier(List<ScQtDemoContact> demoConList, List<ScQtDemoContact> selectedDemoContact) {
		for (ScQtDemoContact c : demoConList) {
			if (c.getPmUser() == null) {
				c.setDisableRowSelection(false);
			}
		}
		this.getManagedBean().setSelectedContactList(selectedDemoContact);
	}
}