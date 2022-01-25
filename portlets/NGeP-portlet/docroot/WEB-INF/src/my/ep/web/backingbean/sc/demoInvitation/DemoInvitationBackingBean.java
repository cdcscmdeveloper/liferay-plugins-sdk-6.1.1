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
import my.ep.constant.sc.QTStatusConst;
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
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
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
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

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
			retrieveQuotationTenderDetails(new Long(qtId), committeeType);
			retrieveDemoDetailsData();

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
			e.printStackTrace();
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
				reqDto.setScQt(this.getManagedBean().getQt());
				DemoResDTO resDto = qtDemoSO.retrieveScQtDemoByQtNo(reqDto);

				if (resDto.getScQtDemo() != null) {
					ScQtDemo scQtDemo = new ScQtDemo();
					scQtDemo = resDto.getScQtDemo();
					savedData = true;

					this.getManagedBean().setDemoInvBy(scQtDemo.getDemoCategoryId().getParameterCode());

					if (this.getManagedBean().getDemoInvBy().equalsIgnoreCase(SourcingConstants.ALL_ITEM)) {
						this.getManagedBean().setAllItemList(scQtDemo.getScQtDemoSupplierList());

					} else if (this.getManagedBean().getDemoInvBy().equalsIgnoreCase(SourcingConstants.BY_ITEM)) {
						this.getManagedBean().setByItemList(scQtDemo.getScQtDemoItemList());
					}

					List<ScQtDemoContact> ct = scQtDemo.getScQtDemoContactList();
					for (ScQtDemoContact c : ct) {
						if (c.isDeskofficer()) {
							c.setDisableRowSelection(true);
						}
					}
					// log.info("*****contact list = " + ct.size());
					this.getManagedBean().setScQtDemoContactList(ct);
					this.getManagedBean().setDemoRemark(scQtDemo.getDemoInvRemark());
					this.getManagedBean().setScQtDemo(scQtDemo);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
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
			e.printStackTrace();
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
			reqQtDTO.setApprovalCategory(ParameterConst.APPROVAL_CATEGORY_COMMITTEEE_APOINTMENT);// later
																									// change
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
						Long currentStatus = this.getManagedBean().getQt().getQtStatus().getStatusId();
						this.getManagedBean().setCurrentStatusId(currentStatus);
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
				e.printStackTrace();
			} finally {
				QtDetailsSO = null;
			}

		}
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

			if (savedData
					&& this.getManagedBean().getDemoInvBy()
							.equalsIgnoreCase(resDto.getScQtDemo().getDemoCategoryId().getParameterCode())) {
				if (this.getManagedBean().getDemoInvBy().equalsIgnoreCase(SourcingConstants.ALL_ITEM)) {
					this.getManagedBean().setAllItemList(resDto.getScQtDemo().getScQtDemoSupplierList());

				} else if (this.getManagedBean().getDemoInvBy().equalsIgnoreCase(SourcingConstants.BY_ITEM)) {
					this.getManagedBean().setByItemList(resDto.getScQtDemo().getScQtDemoItemList());
				}

				this.getManagedBean().setScQtDemoContactList(resDto.getScQtDemo().getScQtDemoContactList());
				this.getManagedBean().setDemoRemark(resDto.getScQtDemo().getDemoInvRemark());

			} else if (savedData
					&& !this.getManagedBean().getDemoInvBy()
							.equalsIgnoreCase(resDto.getScQtDemo().getDemoCategoryId().getParameterCode())
					|| !savedData) {
				if (this.getManagedBean().getDemoInvBy().equalsIgnoreCase(SourcingConstants.ALL_ITEM)) {
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
						specReqDTO.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
						specReqDTO.setDemoSpecView(true);
						TemplateSpecResDTO templateresDTO = templateSpecSO.getTemplateMaster(specReqDTO);
						specQuestionList.addAll(templateresDTO.getSpecMaster().getTplSpecQuestions());
					}

					pmParam = getParameterByTypeAndCode(ParameterTypeConst.ALL_BY_ITEM, SourcingConstants.ALL_ITEM);

					if (savedData) {
						demoSuppList.addAll(resDto.getScQtDemo().getScQtDemoSupplierList());
						qtDemo = resDto.getScQtDemo();
						qtDemo.setDemoCategoryId(pmParam);
						qtDemo.setScQtDemoItemList(null);

						for (ScQtDemoSupplier s : demoSuppList) {
							scQtDemoSchedule = new ScQtDemoSchedule();
							s.setScQtDemoSchedule(scQtDemoSchedule);
							s.setScQtDemoByItemScoreList(null);
							scQtDemoSchedule.setScQtDemoSupplier(s);
						}

					} else {
						qualifiedSupp = checkQualifiedSupplier();

						qtDemo.setQtId(this.getManagedBean().getQt());
						qtDemo.setDemoCategoryId(pmParam);

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
					}

					this.getManagedBean().setAllItemList(demoSuppList);
					
					if (savedData) {
						demoItemList.addAll(resDto.getScQtDemo().getScQtDemoItemList());
					} else {
						for (TplSpecQuestion s : specQuestionList) {
							demoItem = new ScQtDemoItem();
							demoItem.setScQtDemo(qtDemo);
							demoItem.setTplSpecQuestion(s);
							demoItem.setItemName(s.getClSpecDetail());
							demoItemList.add(demoItem);
						}
					}

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
						specReqDTO.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
						specReqDTO.setDemoSpecView(true);
						TemplateSpecResDTO templateresDTO = templateSpecSO.getTemplateMaster(specReqDTO);
						specQuestionList.addAll(templateresDTO.getSpecMaster().getTplSpecQuestions());
					}

					pmParam = getParameterByTypeAndCode(ParameterTypeConst.ALL_BY_ITEM, SourcingConstants.BY_ITEM);

					if (savedData) {
						demoSuppList.addAll(resDto.getScQtDemo().getScQtDemoSupplierList());
						qtDemo = resDto.getScQtDemo();
						qtDemo.setDemoCategoryId(pmParam);
					} else {
						qtDemo.setQtId(this.getManagedBean().getQt());
						qtDemo.setDemoCategoryId(pmParam);

						qualifiedSupp = checkQualifiedSupplier();

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
					}

					if (savedData) {
						demoItemList.addAll(resDto.getScQtDemo().getScQtDemoItemList());
					} else {
						for (TplSpecQuestion s : specQuestionList) {
							demoItem = new ScQtDemoItem();
							demoItem.setScQtDemo(qtDemo);
							demoItem.setTplSpecQuestion(s);
							demoItem.setItemName(s.getClItem());
							demoItemList.add(demoItem);
						}
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

				if (!savedData) {
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
					demoCtc.setDisableRowSelection(true);
					demoCtc.setDeskofficer(true);
					demoCtc.setScQtDemo(qtDemo);
					demoContactList.add(demoCtc);
					this.getManagedBean().setScQtDemoContactList(demoContactList);
					this.getManagedBean().setCurrentOfficer(resDTO.getPmUser().getUserName());
				} else {
					demoContactList.addAll(resDto.getScQtDemo().getScQtDemoContactList());
				}
			}

			qtDemo.setScQtDemoSupplierList(demoSuppList);
			qtDemo.setScQtDemoItemList(demoItemList);
			qtDemo.setScQtDemoContactList(demoContactList);
			this.getManagedBean().setScQtDemo(qtDemo);

		} catch (Exception e) {
			e.printStackTrace();
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

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String demoInvitationCancel() {
		IQTDetailSO detailSO;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IQTDetailSO.class);
			QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
			ScQt scQt = this.getManagedBean().getQt();

			if (isQTStatusGreaterThanOrEquals(scQt.getQtStatus().getStatusId(),
					QTStatusConst.QT_PENDING_RESCHEDULE_PUBLICATION)) {
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
			} else {
				List<ScQtRemark> remarks = null;
				if (CollectionUtils.isNotEmpty(scQt.getScQtRemarks())
						&& StringUtils.isNotEmpty(scQt.getScQtRemarks().get(0).getRemark())) {
					remarks = scQt.getScQtRemarks();
				}

				scQt.setScQtRemarks(initializeRemarks(scQt, this.getManagedBean().getQt().getCurrentWorkFlowStatus(),
						scQt.getScQtRemarks(), ScQtRemark.class));
			}
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
	}

	public DemoResDTO saveDemoInvitation() {
		IQTDemoSO qtDemoSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
				IQTDemoSO.class);
		DemoReqDTO reqDto = new DemoReqDTO();
		ScQtDemo scQtDemo = this.getManagedBean().getScQtDemo();
		if (scQtDemo == null) {
			throw new EpException(MessageConst.ERROR_760531);
		}
		scQtDemo.setDemoInvRemark(this.getManagedBean().getDemoRemark());
		reqDto.setScQtDemo(scQtDemo);
		reqDto.setScQt(this.getManagedBean().getQt());
		DemoResDTO resDto = qtDemoSO.retrieveScQtDemoByQtNo(reqDto);

		if (resDto.getScQtDemo() != null) {
			if (this.getManagedBean().getDemoInvBy().equalsIgnoreCase(SourcingConstants.ALL_ITEM)) {
				scQtDemo.setScQtDemoItemList(null);
			} else if (this.getManagedBean().getDemoInvBy().equalsIgnoreCase(SourcingConstants.BY_ITEM)) {
				scQtDemo.setScQtDemoSupplierList(null);
			}

			if (!resDto.getScQtDemo().getDemoCategoryId().getParameterCode()
					.equalsIgnoreCase(scQtDemo.getDemoCategoryId().getParameterCode())) {
				if (resDto.getScQtDemo().getDemoCategoryId().getParameterCode()
						.equalsIgnoreCase(SourcingConstants.BY_ITEM)) {
					reqDto.setDemoItemToBeDeleted(resDto.getScQtDemo().getScQtDemoItemList());
				} else if (resDto.getScQtDemo().getDemoCategoryId().getParameterCode()
						.equalsIgnoreCase(SourcingConstants.ALL_ITEM)) {
					reqDto.setSupplierScheduleToBeDeleted(resDto.getScQtDemo().getScQtDemoSupplierList());
				}

			}
			reqDto.setExistedData(true);
			resDto = qtDemoSO.saveDemoInvitation(reqDto);

		} else {
			resDto = qtDemoSO.saveDemoInvitation(reqDto);
		}

		if (resDto.isSuccessful()) {
			this.getManagedBean().setScQtDemo(resDto.getScQtDemo());
			this.getManagedBean().setScQtDemoContactList(resDto.getScQtDemo().getScQtDemoContactList());
			this.getManagedBean().setAllItemList(resDto.getScQtDemo().getScQtDemoSupplierList());
			this.getManagedBean().setByItemList(resDto.getScQtDemo().getScQtDemoItemList());
			if (action == null) {
				successMessage(MessageConst.INFO_700004, getMessageCommon("lbl.demo.save.msg"), this.getManagedBean()
						.getQt().getQtNo(), Boolean.FALSE);
			}

			return resDto;
		}

		return null;
	}

	public String submitDemoInvitation() {
		IQTDemoSO qtDemoSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
				IQTDemoSO.class);
		DemoReqDTO reqDto = new DemoReqDTO();
		action = BpmOutcomeConst.BPM_SUBMIT;
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
				resolveAcknowledgement(resDTO.getScQt(), resDTO);
				renewSesionObject();
				return CommonUIConst.ACKNOWLEDGE_PAGE;
			}
		}
		return null;
	}

	private void resolveAcknowledgement(ScQt scQt, DemoResDTO resDTO) {
		String message = null;

		if (StringUtils.isEmpty(message)) {
			message = MessageUtils.getMessage(getMessgeConstant("SUBMIT"), new String[] {
					getMessageCommon("lbl.demo.presentation.submit"), scQt.getQtNo() },
					MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale());
		}
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null, message));

	}

	public DemoResDTO demoUpdateBpmTask(DemoResDTO resDTO, String outcome) {
		try {
			FacesScopes facesScopes = new FacesScopes();
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
			
			if(this.getManagedBean().getPayLoad() == null){				
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
				resDTO.setPmUser(userResDTO.getPmUser());
				demoReqDTO.setTaskId(this.getManagedBean().getTaskId());
				demoReqDTO.setTaskName(this.getManagedBean().getTaskName());// #25037
				demoReqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
				demoReqDTO.setPayLoad(this.getManagedBean().getPayLoad());
				demoReqDTO.setPmUser(facesScopes.getUserSessionBean());
				demoReqDTO.setOutcome(outcome);
				resDTO = qtDemoSO.updateBPMAction(resDTO, demoReqDTO);

				renewSesionObject();
			}

		} catch (Exception e) {
			e.printStackTrace();
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
			e.printStackTrace();
		}

		return qualifiedSupp;
	}

	public void demoCancel() {
		retrieveQuotationTenderDetails(this.getManagedBean().getQt().getQtId(), this.getManagedBean()
				.getCommitteeType());
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
}