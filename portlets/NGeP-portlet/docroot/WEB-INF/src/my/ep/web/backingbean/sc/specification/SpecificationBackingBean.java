/**
 * 
 */
package my.ep.web.backingbean.sc.specification;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.circular.ICircularDetailSO;
import my.ep.biz.service.ct.circular.ICircularSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.checkList.ICheckListSO;
import my.ep.biz.service.pm.common.currency.ICurrencySO;
import my.ep.biz.service.pm.common.procurementcategory.IProcurementSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTEvaluationSO;
import my.ep.biz.service.sc.quationtender.IQTSpecSO;
import my.ep.biz.service.sc.quationtender.IQTTemplateSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.dto.ct.circular.CircularDetailReqDTO;
import my.ep.dto.ct.circular.CircularDetailResDTO;
import my.ep.dto.ct.circular.CircularReqDTO;
import my.ep.dto.ct.circular.CircularResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.checkList.CheckListReqDTO;
import my.ep.dto.pm.common.checkList.CheckListResDTO;
import my.ep.dto.pm.common.currency.CurrencyReqDTO;
import my.ep.dto.pm.common.currency.CurrencyResDTO;
import my.ep.dto.pm.common.procurementcategory.ProcurementReqDTO;
import my.ep.dto.pm.common.procurementcategory.ProcurementResDTO;
import my.ep.dto.sc.quationtender.QTTemplateDocReqDTO;
import my.ep.dto.sc.quationtender.QTTemplateDocResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sc.specification.SpecificationReqDTO;
import my.ep.dto.sc.specification.SpecificationResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.sc.specificationdata.SCSpecificationDataType;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCheckListCategory;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCurrency;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmProcurementCategory;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.directpurchase.ScQuoteZone;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListScore;
import my.ep.persistence.entity.sc.quationtender.ScQtCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtDoc;
import my.ep.persistence.entity.sc.quationtender.ScQtFreqSchedule;
import my.ep.persistence.entity.sc.quationtender.ScQtProgessivePayment;
import my.ep.persistence.entity.sc.quationtender.ScQtRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtSpec;
import my.ep.persistence.entity.sc.quationtender.ScQtWeightage;
import my.ep.persistence.entity.sc.quationtender.ScQtZoneCriteria;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.RoleCode;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.directpurchase.LocationDTO;
import my.ep.web.backingbean.sc.quatationtender.QTDetailBackingBean;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.backingbean.sc.templatemaintenance.TemplateMaintenanceBackingBean;
import my.ep.web.backingbean.tpl.spec.TemplateSpecBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.managedbean.sc.specification.SpecificationManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sc.helper.LocationSingleton;
import my.ep.web.util.sc.helper.SourcingLocationHelper;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.TreeNode;

import com.liferay.portal.util.PortalUtil;

/**
 * @author ACER
 * 
 */
@ManagedBean(name = "specificationBackingBean")
@ViewScoped
public class SpecificationBackingBean extends
		SourcingBaseBackingBean<SpecificationManagedBean> {
	private IPagingDataModel<CtCircularVer> searchCircular = new SearchCircularPagingDataModel();

	private List<SpecificationCompilanceBean> selectedCompilenceList;
	private int count;

	private String selctValue;

	public SpecificationBackingBean() {

	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void saveSpecificationDtls() {
		IQTSpecSO specSO;
		try {
			SpecificationReqDTO reqDTO = prepareSpecReq();
			specSO = BusinessDelegate.getInstance().getServiceObject("ss",
					IQTSpecSO.class);
			SpecificationResDTO resDTO = specSO.saveQTDtl(reqDTO);
			if (resDTO.isSuccessful()) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Record saved successfully.", ""

						));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			specSO = null;
		}
	}

	public String newProceedAction() {
		if (this.getManagedBean().isPanel() == false) {
			return "specificationCreation.xhtml?faces-redirect=true";
		}
		if (this.getManagedBean().isPanel() == true) {
			 return "specificationCreation.xhtml?faces-redirect=true";
			//return "searchCircularPanelContract.xhtml?faces-redirect=true";
		}
		return null;
	}

	public List<PmParameterDesc> retrieveParameterDescByType(String type) {
		return ParameterHelper.getInstance().getParameterDescList(type);

	}

	private List<LocationDTO> getLocations(TreeNode zoneLocation) {
		List<LocationDTO> results = new ArrayList<LocationDTO>();
		if (zoneLocation.isLeaf()) {
			LocationDTO locationDTO = (LocationDTO) zoneLocation.getData();
			results.add(locationDTO);
		} else {
			for (TreeNode childrendLocationNode : zoneLocation.getChildren()) {
				results.addAll(getLocations(childrendLocationNode));
			}
		}
		return results;
	}

	private ScQuoteZone getZone(TreeNode zoneLocation) {
		ScQuoteZone result = null;
		if (zoneLocation.getData() instanceof LocationDTO) {
			LocationDTO locationDTO = (LocationDTO) zoneLocation.getData();
			if (locationDTO.getZone() != null) {
				result = locationDTO.getZone();
			} else {
				if (zoneLocation.getChildren() != null
						&& zoneLocation.getChildren().size() > 0) {
					for (TreeNode childLocationNode : zoneLocation
							.getChildren()) {
						result = getZone(childLocationNode);
					}
				}
			}
		} else {
			if (zoneLocation.getChildren() != null
					&& zoneLocation.getChildren().size() > 0) {
				for (TreeNode childLocationNode : zoneLocation.getChildren()) {
					result = getZone(childLocationNode);
				}
			}
		}

		return result;
	}

	public void editZoneLocation(TreeNode rootZoneLocation) {
		List<LocationDTO> locationDTOs = getLocations(rootZoneLocation);
		ScQuoteZone zone = getZone(rootZoneLocation);
		List<PmState> states = SourcingLocationHelper
				.getStateFromLocationDTOs(locationDTOs);
		List<PmDivision> divisions = SourcingLocationHelper
				.getDivisionFromLocationDTOs(locationDTOs);
		List<PmDistrict> districts = SourcingLocationHelper
				.getDistrictFromLocationDTOs(locationDTOs);
		List<PmCity> cities = SourcingLocationHelper
				.getCityFromLocationDTOs(locationDTOs);
		getManagedBean().setRootLocationTree(
				LocationSingleton.getInstance().buildLocationTree(1L, states,
						districts, divisions, cities));
		if (zone != null) {
			getManagedBean().setZoneName(zone.getZoneName());
		}
	}

	public void showZoneLocationPopup() {
		getManagedBean().setRootLocationTree(
				LocationSingleton.getInstance().buildLocationTree(1L, null,
						null, null, null));
	}

	public void showZoneLocationListPopup() {
		getManagedBean().setRootLocationTreeList(
				LocationSingleton.getInstance().buildLocationTree(1L, null,
						null, null, null));
	}

	public void showZoneDeliveryAddressListPopup() {
		getManagedBean().setRootAddressLocationTreeList(
				LocationSingleton.getInstance().buildAddressLocationTree(1L,
						null));
	}

	public void updateZoneLocation() {
		List<LocationDTO> locationDTOs = new ArrayList<LocationDTO>();
		// TreeNode[] listSelectedTreeNode =
		// getManagedBean().getSelectedNodes().clone();
		if (getManagedBean().getSelectedNodes() != null) {
			for (TreeNode treeNode : getManagedBean().getSelectedNodes()) {
				if (!treeNode.getParent().isSelected()) {
					LocationDTO locationDTO = (LocationDTO) treeNode.getData();
					locationDTOs.add(locationDTO);
				}
			}
		}
		ScQuoteZone zone = new ScQuoteZone();
		System.out.println("zone name : " + getManagedBean().getZoneName());
		zone.setZoneName(getManagedBean().getZoneName());
		getManagedBean().setZoneName(null);
		System.out.println("Zone Name : " + zone.getZoneName());

		List<PmState> states = SourcingLocationHelper
				.getStateFromLocationDTOs(locationDTOs);
		List<PmDivision> divisions = SourcingLocationHelper
				.getDivisionFromLocationDTOs(locationDTOs);
		List<PmDistrict> districts = SourcingLocationHelper
				.getDistrictFromLocationDTOs(locationDTOs);
		List<PmCity> cities = SourcingLocationHelper
				.getCityFromLocationDTOs(locationDTOs);

		getManagedBean().getZoneTrees().add(
				LocationSingleton.getInstance().buildZoneTree(zone, states,
						districts, divisions, cities));
		getManagedBean().setRootLocationTree(
				LocationSingleton.getInstance().buildLocationTree(1L, null,
						null, null, null));

		// System.out.println("Zone Tree : " +
		// getManagedBean().getZoneTrees().get(0).getData());
		// System.out.println("Zone Name : " + getManagedBean().getZoneName());
	}

	public void updateZoneLocationList() {
		List<LocationDTO> locationDTOs = new ArrayList<LocationDTO>();
		// TreeNode[] listSelectedTreeNode =
		// getManagedBean().getSelectedNodes().clone();
		if (getManagedBean().getSelectedLocNodes() != null) {
			for (TreeNode treeNode : getManagedBean().getSelectedLocNodes()) {
				if (!treeNode.getParent().isSelected()) {
					LocationDTO locationDTO = (LocationDTO) treeNode.getData();
					locationDTOs.add(locationDTO);
				}
			}
		}
		ScQuoteZone zone = new ScQuoteZone();
		System.out.println("zone name : " + getManagedBean().getZoneName1());
		zone.setZoneName(getManagedBean().getZoneName1());
		getManagedBean().setZoneName(null);
		System.out.println("Zone Name : " + zone.getZoneName());

		List<PmState> states = SourcingLocationHelper
				.getStateFromLocationDTOs(locationDTOs);
		List<PmDivision> divisions = SourcingLocationHelper
				.getDivisionFromLocationDTOs(locationDTOs);
		List<PmDistrict> districts = SourcingLocationHelper
				.getDistrictFromLocationDTOs(locationDTOs);
		List<PmCity> cities = SourcingLocationHelper
				.getCityFromLocationDTOs(locationDTOs);

		getManagedBean().getZoneTreeList().add(
				LocationSingleton.getInstance().buildZoneTree(zone, states,
						districts, divisions, cities));
		getManagedBean().setRootLocationTree(
				LocationSingleton.getInstance().buildLocationTree(1L, null,
						null, null, null));
		System.out.println("Zone Tree : "
				+ getManagedBean().getZoneTrees().size());
		// System.out.println("Zone Name : " + getManagedBean().getZoneName());
	}

	public SpecificationReqDTO prepareSpecReq() {
		SpecificationReqDTO specreqDTO = new SpecificationReqDTO();

		ScQtSpec scqtSpec = this.getManagedBean().getScQtSpec();
		scqtSpec.setApprovalDate(new Date());
		PmParameter parameter = this.getManagedBean().getScQtSpec()
				.getPmProcurementCategory();
		parameter.setParameterId(this.getManagedBean().getProcurementId());
		scqtSpec.setPmProcurementCategory(parameter);
		// scqtSpec.setAwardType("L");
		scqtSpec.setContractDuration(1);
		scqtSpec.getScQt().setQtAmount(
				this.getManagedBean().getScQtSpec().getScQt().getQtAmount());

		List<ScQtCheckList> st1tech = this.getManagedBean()
				.getScQtCheckSt1techProposal();

		List<ScQtCheckList> collectionAll = new ArrayList<ScQtCheckList>();
		if (st1tech != null) {

			collectionAll.addAll(st1tech);
		}

		List<ScQtCheckList> st1TechAdd = this.getManagedBean()
				.getScQtCheckSt1techAdditionalProposalList();

		if (st1TechAdd != null) {

			collectionAll.addAll(st1TechAdd);
		}

		List<ScQtCheckList> st1Fin = this.getManagedBean()
				.getScQtCheckSt1financeProposalList();

		if (st1Fin != null) {

			collectionAll.addAll(st1Fin);
		}
		List<ScQtCheckList> st1FinAdd = this.getManagedBean()
				.getScQtCheckSt1financeAdditionalProposalList();

		if (st1FinAdd != null) {

			collectionAll.addAll(st1FinAdd);
		}
		List<ScQtCheckList> st2Tech = this.getManagedBean()
				.getScQtCheckSt2techProposal();

		if (st2Tech != null) {

			collectionAll.addAll(st2Tech);
		}
		List<ScQtCheckList> st2TechAdd = this.getManagedBean()
				.getScQtCheckSt2techAdditionalProposalList();

		if (st2TechAdd != null) {

			collectionAll.addAll(st2TechAdd);
		}
		List<ScQtCheckList> st2Fin = this.getManagedBean()
				.getScQtCheckSt2financeProposalList();

		if (st2Fin != null) {

			collectionAll.addAll(st2Fin);
		}
		List<ScQtCheckList> st2FinAdd = this.getManagedBean()
				.getScQtCheckSt2financeAdditionalProposalList();

		if (st2FinAdd != null) {

			collectionAll.addAll(st2FinAdd);
		}
		List<ScQtWeightage> technicalWeight = this.getManagedBean()
				.getWeightageTechnical();

		List<ScQtWeightage> weightList = new ArrayList<ScQtWeightage>();

		if (technicalWeight != null) {

			weightList.addAll(technicalWeight);
		}

		List<ScQtWeightage> financialWeight = this.getManagedBean()
				.getWeightageFinancial();

		if (financialWeight != null) {

			weightList.addAll(financialWeight);
		}

		/*
		 * List<ScQtCheckListScore> scoreList = this.getManagedBean()
		 * .getCollectionScoring();
		 * 
		 * if (scoreList != null) {
		 * System.out.println("=======scoring list size===="+scoreList.size());
		 * for(ScQtCheckListScore score:scoreList){ ScQtCheckList
		 * checkList=this.getManagedBean().getScQtChecklist();
		 * checkList.setScQtCheckListScores(scoreList); }
		 * 
		 * }
		 */

		scqtSpec.setScQtCheckLists(collectionAll);
		scqtSpec.setScQtWeightages(weightList);
		specreqDTO.setScQtSpec(scqtSpec);
		// specSO.saveSpecDetails(specreqDTO);

		return specreqDTO;

	}

	@PostConstruct
	public void init() {
		try {
			Date d = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			this.getManagedBean().setApprovalDate(sdf.format(d));
			compilenceList();
			if (getManagedBean().getScQtSpec() == null) {
				this.getManagedBean().setApprovalList(getAppDetails());
				retriveSpecdetails(146L);

				/*
				 * getManagedBean().setScQtSpec(new ScQtSpec()); //
				 * getManagedBean().getScQtSpec().setAwardType("L");
				 * getManagedBean().getScQtSpec().setPmCurrency(new
				 * PmCurrency());
				 * getManagedBean().getScQtSpec().setPmProcurementCategory( new
				 * PmProcurementCategory()); IQTDetailSO QtDetailsSO =
				 * BusinessDelegate.getInstance() .getServiceObject("ss",
				 * IQTDetailSO.class); QuationTenderReqDTO tenderReqDTO = new
				 * QuationTenderReqDTO(); tenderReqDTO.setQtId(146L); //
				 * tenderReqDTO.setQtTndrNo("xxx"); QuationTenderResDTO
				 * tenderResDTO = new QuationTenderResDTO(); tenderResDTO =
				 * QtDetailsSO.retriveQtById(tenderReqDTO); //
				 * QTDetailBackingBean detailBackingBean = new //
				 * QTDetailBackingBean();
				 * getManagedBean().setQt(tenderResDTO.getQtDetail());
				 * populateParentOrgProfile(tenderResDTO.getQtDetail());
				 * getManagedBean().getScQtSpec().setScQt(
				 * tenderResDTO.getQtDetail());
				 * getManagedBean().setScQtRemarks(new ScQtRemark()); //
				 * getManagedBean().setScQtChecklist(new ScQtCheckList());
				 * getManagedBean().getScQtSpec().setPmStatus(new PmStatus());
				 * getManagedBean().setSpecStatus(true);
				 */
			}

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void retriveSpecdetails(Long qtId) {
		try {
			IQTSpecSO specSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSpecSO.class);
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			SpecificationResDTO resDTO = null;
			reqDTO.setQtId(qtId);
			resDTO = specSO.retrieveSpecDetails(reqDTO);
			if (resDTO.getScQtSpec() == null) {

				getManagedBean().setScQtSpec(new ScQtSpec());
				// getManagedBean().getScQtSpec().setAwardType("L");
				getManagedBean().getScQtSpec().setPmCurrency(new PmCurrency());
				getManagedBean().getScQtSpec().setPmProcurementCategory(
						new PmParameter());
				IQTDetailSO QtDetailsSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTDetailSO.class);
				QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
				tenderReqDTO.setQtId(qtId);
				// tenderReqDTO.setQtTndrNo("xxx");
				QuationTenderResDTO tenderResDTO = new QuationTenderResDTO();
				tenderResDTO = QtDetailsSO.retriveQtById(tenderReqDTO);
				getManagedBean().getScQtSpec().setScQt(
						tenderResDTO.getQtDetail());
				//getManagedBean().getScQtSpec().getScQt().setQtType("O");

				getManagedBean().setScQtRemarks(new ScQtRemark());
				// getManagedBean().setScQtChecklist(new ScQtCheckList());
				getManagedBean().getScQtSpec().setPmStatus(new PmStatus());
				if (getManagedBean().getQtCriteria() == null) {
					getManagedBean().setQtCriteria(new ScQtCriteria());
				}

			} else {
				getManagedBean().setScQtSpec(resDTO.getScQtSpec());
				getManagedBean().setScQtCheckSt1techProposal(
						resDTO.getS1TecResultBean().getResultList());
				getManagedBean().setScQtCheckSt1techAdditionalProposalList(
						resDTO.getS1AddTecResultBean().getResultList());
				getManagedBean().setScQtCheckSt1financeProposalList(
						resDTO.getS1FinResultBean().getResultList());
				getManagedBean().setScQtCheckSt1financeAdditionalProposalList(
						resDTO.getS1AddFinResultBean().getResultList());
				getManagedBean().setScQtCheckSt2techProposal(
						resDTO.getS2TecResultBean().getResultList());
				getManagedBean().setScQtCheckSt2techAdditionalProposalList(
						resDTO.getS2AddTecResultBean().getResultList());
				getManagedBean().setScQtCheckSt2financeProposalList(
						resDTO.getS2AddTecResultBean().getResultList());
				getManagedBean().setScQtCheckSt2financeAdditionalProposalList(
						resDTO.getS2AddFinResultBean().getResultList());
				getManagedBean().setCommitteeMemberList(
						resDTO.getCommMemberResultBean().getResultList());
				getManagedBean().setWeightageTechnical(
						resDTO.getTechWeightageResultBean().getResultList());
				getManagedBean().setWeightageFinancial(
						resDTO.getFinWeightageResultBean().getResultList());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void tierEvent(AjaxBehaviorEvent event) {
		String type = (String) event.getComponent().getAttributes()
				.get("value");
		this.getManagedBean().setEvaluationType(type);
	}

	public void onChangeQtType(AjaxBehaviorEvent event) {
		String type = (String) event.getComponent().getAttributes()
				.get("value");
		System.out.println("========type value===" + type);
		this.getManagedBean().getScQtSpec().getScQt().setQtType(type);
	}

	public void typeChange(AjaxBehaviorEvent event) {
		String type = (String) event.getComponent().getAttributes()
				.get("value");
		this.getManagedBean().getScQtCheckSt2techProposal().get(0)
				.setChecklistType(type);
	}

	public void fulfilmentChange(AjaxBehaviorEvent event) {
		String type = (String) event.getComponent().getAttributes()
				.get("value");
		this.getManagedBean().getScQtSpec().setFulfilmentType(type);
	}
	
	public void stateChange(AjaxBehaviorEvent event) {
		boolean state = (boolean) event.getComponent().getAttributes()
				.get("value");
		System.out.println("=======state===="+state);
		this.getManagedBean().getQtCriteria().setAllState(state);
	}

	public void fromChange(AjaxBehaviorEvent event) {
		String formvalue = (String) event.getComponent().getAttributes()
				.get("value");

		this.getManagedBean().setFromMonth(formvalue);
	}
	
	
	
	public String cloneAction(ScQtDoc scqtDoc) {
		IQTTemplateSO qtTemplateSO;
		try {
			QTTemplateDocReqDTO qtTemplateReqDTO = new QTTemplateDocReqDTO();
			TemplateMaintenanceBackingBean maintenancebean=new TemplateMaintenanceBackingBean();

			QTTemplateDocResDTO resDto = null;

			if (maintenancebean.getManagedBean().getScqtDoc() == null) {
				maintenancebean.getManagedBean().setScqtDoc(new ScQtDoc());

			}
			if (maintenancebean.getManagedBean().getScqtDoc().getScQt() == null) {
				maintenancebean.getManagedBean().getScqtDoc().setScQt(new ScQt());
			}
			if (maintenancebean.getManagedBean().getScqtDoc().getTplSpecMaster() == null) {

				maintenancebean.getManagedBean().getScqtDoc().setTplSpecMaster(
						new TplSpecMaster());

			}

			if (maintenancebean.getManagedBean().getScqtDoc().getTplSpecMaster()
					.getProcurementCategory() == null) {

				maintenancebean.getManagedBean().getScqtDoc().getTplSpecMaster()
						.setProcurementCategory(new PmParameter());

			}
			if (maintenancebean.getManagedBean().getScqtDoc().getTplSpecMaster()
					.getItemTypeCode() == null) {

				maintenancebean.getManagedBean().getScqtDoc().getTplSpecMaster()
						.setItemTypeCode(new PmParameter());

			}
			if (maintenancebean.getManagedBean().getScqtDoc().getTplSpecMaster()
					.getSpecTypeCode() == null) {

				maintenancebean.getManagedBean().getScqtDoc().getTplSpecMaster()
						.setSpecTypeCode(new PmParameter());

			}
			if (maintenancebean.getManagedBean().getScqtDoc().getTplSpecMaster()
					.getOrgProfile() == null) {

				maintenancebean.getManagedBean().getScqtDoc().getTplSpecMaster()
						.setOrgProfile(new PmOrgProfile());

			}
			if (maintenancebean.getManagedBean().getScqtDoc().getTplSpecMaster()
					.getTemplateTypeCode() == null) {

				maintenancebean.getManagedBean().getScqtDoc().getTplSpecMaster()
						.setTemplateTypeCode(new PmParameter());

			}

			if (maintenancebean.getManagedBean().getScqtDoc().getTplSpecMaster().getStatus() == null) {

				maintenancebean.getManagedBean().getScqtDoc().getTplSpecMaster()
						.setStatus(new PmStatus());

			}
			if (maintenancebean.getManagedBean().getScqtDoc().getScQtZoneCriteria() == null) {
				maintenancebean.getManagedBean().getScqtDoc().setScQtZoneCriteria(
						new ScQtZoneCriteria());
			}
			TemplateSpecBackingBean specBB = new TemplateSpecBackingBean();
			TemplateSpecBean templateSpecBean =specBB. getManagedBean().getTemplateSpecBean();
			TplSpecMaster specMaster1 = new TplSpecMaster();
			specMaster1.setSpecMasterId(new Long(scqtDoc.getTplSpecMaster()
					.getSpecMasterId()));
			// specMaster.setSpecMasterId(1L);
			templateSpecBean.setContentType(
					TemplateSpecConst.CONTENT_TYPE_QUESTION);
			templateSpecBean.setSpecMaster(specMaster1);
			specBB.retrieveTemplateSpecMaster(templateSpecBean);
			// scqtDoc.getTplSpecMaster().getProcurementCategory()
			// .getParameterId()
			if (maintenancebean.getManagedBean().getScqtDoc().getScQtCheckList() == null) {
				maintenancebean.getManagedBean().getScqtDoc()
						.setScQtCheckList(new ScQtCheckList());
			}
			maintenancebean.getManagedBean()
					.getScqtDoc()
					.getTplSpecMaster()
					.getProcurementCategory()
					.setParameterId(
							scqtDoc.getTplSpecMaster().getProcurementCategory()
									.getParameterId());
			/*
			 * scqtDoc.getTplSpecMaster().getItemTypeCode() .getParameterId()
			 */
			maintenancebean.getManagedBean()
					.getScqtDoc()
					.getTplSpecMaster()
					.getItemTypeCode()
					.setParameterId(
							scqtDoc.getTplSpecMaster().getItemTypeCode()
									.getParameterId());
			/*
			 * scqtDoc.getTplSpecMaster().getSpecTypeCode() .getParameterId(
			 */
			maintenancebean.getManagedBean()
					.getScqtDoc()
					.getTplSpecMaster()
					.getSpecTypeCode()
					.setParameterId(
							scqtDoc.getTplSpecMaster().getSpecTypeCode()
									.getParameterId());
			maintenancebean.getManagedBean()
					.getScqtDoc()
					.getTplSpecMaster()
					.getStatus()
					.setStatusId(
							scqtDoc.getTplSpecMaster().getStatus()
									.getStatusId());
			maintenancebean.getManagedBean().getScqtDoc().getScQt()
					.setQtNo(scqtDoc.getScQt().getQtNo());

			qtTemplateReqDTO.setQtId(146L);
			qtTemplateReqDTO.setDocType("1");
			qtTemplateReqDTO.setSpecMaster(scqtDoc.getTplSpecMaster());
			qtTemplateReqDTO.setSpecMasterId(scqtDoc.getTplSpecMaster()
					.getSpecMasterId());

			try {
				qtTemplateSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTTemplateSO.class);

				resDto = qtTemplateSO
						.retriveTemplateDocDetails(qtTemplateReqDTO);

				

			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			qtTemplateSO = null;
		}

		return "/xhtml/sc/main/newTemplateMaintenance.xhtml?faces-redirect=true";
	}
	

	public void toChange(AjaxBehaviorEvent event) {
		String toValue = (String) event.getComponent().getAttributes()
				.get("value");
		this.getManagedBean().setToMonth(toValue);
		int n = Integer.parseInt(this.getManagedBean().getFromMonth());
		int j = Integer.parseInt(toValue);
		List<BankingBean> monthList = new ArrayList<BankingBean>();
		String monthvalue = "";
		String amount = "";
		for (int i = n; i <= j; i++) {
			if (i == 0) {

				monthvalue = "January";
				amount = "1000";
			} else if (i == 1) {

				monthvalue = "February";
				amount = "2000";
			} else if (i == 2) {

				monthvalue = "March";
				amount = "3000";
			} else if (i == 3) {

				monthvalue = "April";
				amount = "4000";
			} else if (i == 4) {

				monthvalue = "May";
				amount = "5000";
			} else if (i == 5) {

				monthvalue = "June";
				amount = "6000";
			} else if (i == 6) {

				monthvalue = "July";
				amount = "7000";
			} else if (i == 7) {

				monthvalue = "August";
				amount = "8000";
			} else if (i == 8) {

				monthvalue = "September";
				amount = "9000";
			} else if (i == 9) {

				monthvalue = "October";
				amount = "10000";
			} else if (i == 10) {

				monthvalue = "November";
				amount = "11000";
			} else if (i == 11) {

				monthvalue = "December";
				amount = "12000";
			}
			monthList.add(new BankingBean(monthvalue, amount));
		}
		int totalamount = 0;
		this.getManagedBean().setBankList(monthList);
		for (int i = 0; i < this.getManagedBean().getBankList().size(); i++) {
			BankingBean bean = (BankingBean) this.getManagedBean()
					.getBankList().get(i);
			totalamount = totalamount + Integer.parseInt(bean.getAmount());
			BigDecimal b1 = new BigDecimal(totalamount);
			BigDecimal techWeightage = new BigDecimal(this.getManagedBean()
					.getBankList().size());
			MathContext mc = new MathContext(4);
			BigDecimal techWeightage1 = b1.divide(techWeightage, mc);
			this.getManagedBean().setTotalAmountAvg(techWeightage1);
		}
		this.getManagedBean().setTotalAmount(totalamount);
		System.out.println("====totalAmount==");

	}

	public ScQtCheckList createSpecList(String stage) {
		ScQtSpec scQtSpec = getManagedBean().getScQtSpec();
		ScQtCheckList additionProposalst1 = new ScQtCheckList();

		if (stage.equalsIgnoreCase("1")) {
			additionProposalst1.setScQtWeightage(null);
		} else if (stage.equalsIgnoreCase("2")) {
			additionProposalst1.setScQtWeightage(new ScQtWeightage());
		}
		additionProposalst1.setScQtSpec(scQtSpec);
		return additionProposalst1;
	}

	public void stageOneAdditionalAdd() {

		try {
			List<ScQtCheckList> st1additionalList = getManagedBean()
					.getScQtCheckSt1techAdditionalProposalList();

			if (null == st1additionalList) {
				st1additionalList = new ArrayList<ScQtCheckList>();
			}

			ScQtCheckList additionProposalst1 = createSpecList("1");
			additionProposalst1.setDocType("2");
			additionProposalst1.setEvaluationStage("1");
			additionProposalst1.setChecklistType("UP");
			st1additionalList.add(additionProposalst1);
			// mediumCarsModel = new CarDataModel(st1additionalList);
			this.getManagedBean().setScQtCheckSt1techAdditionalProposalList(
					st1additionalList);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void stageOneFiProposalAdd() {

		try {
			List<ScQtCheckList> st1financeproposalList = getManagedBean()
					.getScQtCheckSt1financeProposalList();
			if (null == st1financeproposalList) {
				st1financeproposalList = new ArrayList<ScQtCheckList>();

			}

			ScQtCheckList st1financeproposalList1 = createSpecList("1");
			st1financeproposalList1.setDocType("3");
			st1financeproposalList1.setEvaluationStage("1");
			st1financeproposalList1.setChecklistType("UP");
			st1financeproposalList.add(st1financeproposalList1);
			this.getManagedBean().setScQtCheckSt1financeProposalList(
					st1financeproposalList);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void stageOneFiAdditioalProposalAdd() {

		try {
			List<ScQtCheckList> st1financeAdditionalproposalList = getManagedBean()
					.getScQtCheckSt1financeAdditionalProposalList();
			if (null == st1financeAdditionalproposalList) {
				st1financeAdditionalproposalList = new ArrayList<ScQtCheckList>();

			}

			ScQtCheckList st1financeAdditionalproposalList1 = createSpecList("1");
			st1financeAdditionalproposalList1.setDocType("4");
			st1financeAdditionalproposalList1.setEvaluationStage("1");
			st1financeAdditionalproposalList1.setChecklistType("UP");
			st1financeAdditionalproposalList
					.add(st1financeAdditionalproposalList1);
			this.getManagedBean().setScQtCheckSt1financeAdditionalProposalList(
					st1financeAdditionalproposalList);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void stageTwoAdditionalAdd() {
		try {
			List<ScQtCheckList> st2additionalList = getManagedBean()
					.getScQtCheckSt2techAdditionalProposalList();
			if (null == st2additionalList) {
				st2additionalList = new ArrayList<ScQtCheckList>();

			}

			ScQtCheckList additionProposalst2 = createSpecList("2");
			additionProposalst2.setDocType("2");
			additionProposalst2.setEvaluationStage("2");
			additionProposalst2.setChecklistType("UP");
			st2additionalList.add(additionProposalst2);
			this.getManagedBean().setScQtCheckSt2techAdditionalProposalList(
					st2additionalList);

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void stageTwoFiProposalAdd() {

		try {
			List<ScQtCheckList> st2financeproposalList = getManagedBean()
					.getScQtCheckSt2financeProposalList();
			if (null == st2financeproposalList) {
				st2financeproposalList = new ArrayList<ScQtCheckList>();

			}

			ScQtCheckList st2financeproposalList2 = createSpecList("2");
			st2financeproposalList2.setDocType("3");
			st2financeproposalList2.setChecklistType("UP");
			st2financeproposalList2.setEvaluationStage("2");
			st2financeproposalList.add(st2financeproposalList2);
			this.getManagedBean().setScoringList(null);
			this.getManagedBean().setScQtCheckSt2financeProposalList(
					st2financeproposalList);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void stageTwoFiAdditioalProposalAdd() {
		try {
			List<ScQtCheckList> st2financeAdditionalproposalList = getManagedBean()
					.getScQtCheckSt2financeAdditionalProposalList();
			if (null == st2financeAdditionalproposalList) {
				st2financeAdditionalproposalList = new ArrayList<ScQtCheckList>();

			}

			ScQtCheckList st2financeAdditionalproposalList1 = createSpecList("2");
			st2financeAdditionalproposalList1.setDocType("4");
			st2financeAdditionalproposalList1.setEvaluationStage("2");
			st2financeAdditionalproposalList1.setChecklistType("UP");
			st2financeAdditionalproposalList
					.add(st2financeAdditionalproposalList1);
			this.getManagedBean().setScQtCheckSt2financeAdditionalProposalList(
					st2financeAdditionalproposalList);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void stageOneTechProposal() {

		try {

			List<ScQtCheckList> compilance = this.getManagedBean()
					.getScQtCheckSt1techProposal();

			List<PmCheckListCategory> selectList = this.getManagedBean()
					.getStageOneComplianceSelect();

			if (compilance == null) {
				compilance = new ArrayList<ScQtCheckList>();
			}

			for (int i = 0; i < selectList.size(); i++) {
				PmCheckListCategory bean = selectList.get(i);
				ScQtCheckList scqtlist = createSpecList("1");

				scqtlist.setTitle(bean.getPmCheckList().getCheckListName());
				// scqtlist.setQtChecklistId(new Long(i));
				scqtlist.setDocType("1");
				scqtlist.setEvaluationStage("1");
				scqtlist.setChecklistType("UP");
				compilance.add(scqtlist);
			}
			getManagedBean().setScQtCheckSt1techProposal(compilance);

		} catch (Exception e) {
			e.printStackTrace();
			// throw new IllegalArgumentException();
		}
	}

	public void stageTwoTechProposal() {

		try {

			List<ScQtCheckList> compilance = this.getManagedBean()
					.getScQtCheckSt2techProposal();
			System.out.println("===========select tech list size====="
					+ this.getManagedBean().getStageTwoTechComplianceSelect());

			List<PmCheckListCategory> selectList = this.getManagedBean()
					.getStageTwoTechComplianceSelect();

			if (compilance == null) {
				compilance = new ArrayList<ScQtCheckList>();
			}

			for (int i = 0; i < selectList.size(); i++) {
				PmCheckListCategory bean = selectList.get(i);
				ScQtCheckList scqtlist = createSpecList("2");
				System.out.println("=========checklist name======"
						+ bean.getPmCheckList().getCheckListName());
				System.out.println("====type==="
						+ bean.getPmCheckList().getChecklistType());
				scqtlist.setTitle(bean.getPmCheckList().getCheckListName());

				compilance.add(scqtlist);
				scqtlist.setDocType("1");
				//scqtlist.setEnable(true);
				scqtlist.setChecklistType(bean.getPmCheckList()
						.getChecklistType());
				scqtlist.setEvaluationStage("2");
			}
			this.getManagedBean().setScoringList(null);
			getManagedBean().setScQtCheckSt2techProposal(compilance);

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void stageTwoTechnicalAddProposal() {
		try {
			List<ScQtCheckList> st2technicalproposalList = getManagedBean()
					.getScQtCheckSt2techProposal();

			if (null == st2technicalproposalList) {
				st2technicalproposalList = new ArrayList<ScQtCheckList>();

			}

			ScQtCheckList st2technicalproposalList1 = createSpecList("2");

			st2technicalproposalList1.setDocType("1");
			// st2technicalproposalList1.setChecklistType("UP");
			st2technicalproposalList1.setEvaluationStage("2");
			// st2technicalproposalList1.setScQtCheckListScores();
			this.getManagedBean().setScoringList(null);
			st2technicalproposalList.add(st2technicalproposalList1);
			this.getManagedBean().setScQtCheckSt2techProposal(
					st2technicalproposalList);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void stageTwotechproposal(AjaxBehaviorEvent e) {

		try {
			String dropdown = (String) e.getComponent().getAttributes()
					.get("value");
			List<ScQtCheckList> compilance = this.getManagedBean()
					.getScQtCheckSt2techProposal();

			setSelctValue(dropdown);
		} catch (Exception e1) {
			throw new IllegalArgumentException();
		}
		// this.getManagedBean().getScQtChecklist().setChecklistType(dropdown);
	}

	public void stageTwoFinanceCheckList() {
		try {

			List<ScQtCheckList> compilance = this.getManagedBean()
					.getScQtCheckSt2financeProposalList();

			List<PmCheckListCategory> selectList = this.getManagedBean()
					.getStageTwoFinComplianceSelect();

			if (compilance == null) {
				compilance = new ArrayList<ScQtCheckList>();
			}

			for (int i = 0; i < selectList.size(); i++) {
				PmCheckListCategory bean = selectList.get(i);
				ScQtCheckList scqtlist = createSpecList("2");
				scqtlist.setTitle(bean.getPmCheckList().getCheckListName());
				scqtlist.setDocType("3");
				//scqtlist.setEnable(true);
				scqtlist.setChecklistType(bean.getPmCheckList()
						.getChecklistType());
				scqtlist.setEvaluationStage("2");
				compilance.add(scqtlist);
			}
			this.getManagedBean().setScoringList(null);
			getManagedBean().setScQtCheckSt2financeProposalList(compilance);

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public ScQtWeightage createWeightList() {
		ScQtSpec scQtSpec = getManagedBean().getScQtSpec();
		ScQtWeightage weightage = new ScQtWeightage();
		weightage.setScQtSpec(scQtSpec);
		return weightage;
	}

	public void weightageTechnical() {

		try {
			List<ScQtWeightage> weightageTechnical = getManagedBean()
					.getWeightageTechnical();
			if (null == weightageTechnical) {
				weightageTechnical = new ArrayList<ScQtWeightage>();

			}
			ScQtWeightage weightage = createWeightList();
			weightage.setSpecType("T");
			weightageTechnical.add(weightage);
			this.getManagedBean().setWeightageTechnical(weightageTechnical);

		} catch (Exception e) {
			e.printStackTrace();
			// throw new IllegalArgumentException();
		}
	}

	public void weightageFinancial() {

		try {

			List<ScQtWeightage> weightFinancial = getManagedBean()
					.getWeightageFinancial();
			if (null == weightFinancial) {
				weightFinancial = new ArrayList<ScQtWeightage>();

			}
			ScQtWeightage weightage = createWeightList();
			weightage.setSpecType("F");
			weightFinancial.add(weightage);
			this.getManagedBean().setWeightageFinancial(weightFinancial);

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void compilenceList() {

		try {

			List<SpecificationCompilanceBean> compilaenceList = new ArrayList<SpecificationCompilanceBean>();

			compilaenceList
					.add(new SpecificationCompilanceBean(1, "Sijil MOF"));
			compilaenceList.add(new SpecificationCompilanceBean(2, "Sijil 29"));
			compilaenceList.add(new SpecificationCompilanceBean(3, "Sijil 49"));
			compilaenceList
					.add(new SpecificationCompilanceBean(4, "Sijil KDN"));
			compilaenceList.add(new SpecificationCompilanceBean(5,
					"Sijil Bumiputera"));

			this.getManagedBean().setCompilaenceList(compilaenceList);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}

	}

	public void stageOnecompilenceList() {
		ICheckListSO checkListSO;
		CheckListResDTO resDTO;
		try {
			/*
			 * System.out.println("=====procuremt id=====" +
			 * this.getManagedBean().getProcurementId());
			 */
			checkListSO = BusinessDelegate.getInstance().getServiceObject("pm",
					ICheckListSO.class);
			CheckListReqDTO reqDTO = new CheckListReqDTO();
			reqDTO.setProcurementMode("134");
			reqDTO.setDocType("1");
			reqDTO.setEvalStage("1");
			resDTO = checkListSO
					.retrieveCheckListByEvalStageAndProcurementMode(reqDTO);
			this.getManagedBean().setStageOneComplianceList(
					resDTO.getCheckListResultBean().getResultList());
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		} finally {
			checkListSO = null;
		}

	}

	public void stageTwoTechcompilenceList() {
		ICheckListSO checkListSO;
		CheckListResDTO resDTO;
		try {
			/*
			 * System.out.println("=====procuremt id=====" +
			 * this.getManagedBean().getProcurementId());
			 */

			checkListSO = BusinessDelegate.getInstance().getServiceObject("pm",
					ICheckListSO.class);
			CheckListReqDTO reqDTO = new CheckListReqDTO();
			reqDTO.setProcurementMode("131");
			reqDTO.setDocType("1");
			// reqDTO.setEvalStage("2");
			resDTO = checkListSO
					.retrieveCheckListByEvalStageAndProcurementMode(reqDTO);

			this.getManagedBean().setStageTwoTechComplianceSelect(
					new ArrayList<PmCheckListCategory>());

			this.getManagedBean().setStageTwoTechComplianceList(
					resDTO.getCheckListResultBean().getResultList());
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		} finally {
			checkListSO = null;
		}

	}

	public void stageTwoFincompilenceList() {
		ICheckListSO checkListSO;
		CheckListResDTO resDTO;
		try {
			/*
			 * System.out.println("=====procuremt id=====" +
			 * this.getManagedBean().getProcurementId());
			 */
			checkListSO = BusinessDelegate.getInstance().getServiceObject("pm",
					ICheckListSO.class);
			CheckListReqDTO reqDTO = new CheckListReqDTO();
			reqDTO.setProcurementMode("131");
			reqDTO.setDocType("3");
			// reqDTO.setEvalStage("2");
			resDTO = checkListSO
					.retrieveCheckListByEvalStageAndProcurementMode(reqDTO);

			this.getManagedBean().setStageTwoFinComplianceSelect(
					new ArrayList<PmCheckListCategory>());

			this.getManagedBean().setStageTwoFinComplianceList(
					resDTO.getCheckListResultBean().getResultList());
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		} finally {
			checkListSO = null;
		}

	}

	public ScQtCheckListScore createCheckList() {
		ScQtCheckList checkList = this.getManagedBean().getScQtChecklist();
		ScQtCheckListScore score = new ScQtCheckListScore();
		score.setCheckList(checkList);
		return score;
	}

	public void addScoring() {

		try {

			List<ScQtCheckListScore> checkListScore = this.getManagedBean()
					.getCheckListScore();
			if (null == checkListScore) {
				checkListScore = new ArrayList<ScQtCheckListScore>();

			}
			ScQtCheckListScore score = createCheckList();

			checkListScore.add(score);
			this.getManagedBean().setCheckListScore(checkListScore);

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void addScoringyears() {

		try {

			List<ScQtCheckListScore> checkListScore = this.getManagedBean()
					.getScoringForYears();
			if (null == checkListScore) {
				checkListScore = new ArrayList<ScQtCheckListScore>();

			}
			ScQtCheckListScore score = createCheckList();

			checkListScore.add(score);
			this.getManagedBean().setScoringForYears(checkListScore);

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void addContratAmount() {

		try {

			List<ScQtCheckListScore> checkListScore = this.getManagedBean()
					.getContractAmount();
			if (null == checkListScore) {
				checkListScore = new ArrayList<ScQtCheckListScore>();

			}
			ScQtCheckListScore score = createCheckList();

			checkListScore.add(score);
			this.getManagedBean().setContractAmount(checkListScore);

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void addBankStatement() {

		try {

			List<ScQtCheckListScore> checkListScore = this.getManagedBean()
					.getBankStamentScore();
			if (null == checkListScore) {
				checkListScore = new ArrayList<ScQtCheckListScore>();

			}
			ScQtCheckListScore score = createCheckList();

			checkListScore.add(score);
			this.getManagedBean().setBankStamentScore(checkListScore);

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void creditFacilityScore() {

		try {

			List<ScQtCheckListScore> checkListScore = this.getManagedBean()
					.getCreditFacility();
			if (null == checkListScore) {
				checkListScore = new ArrayList<ScQtCheckListScore>();

			}
			ScQtCheckListScore score = createCheckList();

			checkListScore.add(score);
			this.getManagedBean().setCreditFacility(checkListScore);

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void scoringCash() {

		try {
			/* System.out.println("=enter addscore method==="); */
			List<ScQtCheckListScore> checkListScore = this.getManagedBean()
					.getScoringCash();
			if (null == checkListScore) {
				checkListScore = new ArrayList<ScQtCheckListScore>();

			}
			ScQtCheckListScore score = createCheckList();

			checkListScore.add(score);
			this.getManagedBean().setScoringCash(checkListScore);

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void addAssetScore() {

		try {

			List<ScQtCheckListScore> checkListScore = this.getManagedBean()
					.getAssetScore();
			if (null == checkListScore) {
				checkListScore = new ArrayList<ScQtCheckListScore>();

			}
			ScQtCheckListScore score = createCheckList();

			checkListScore.add(score);
			this.getManagedBean().setAssetScore(checkListScore);

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void addScheduleDetails() {

		try {
			/* System.out.println("=enter addscore method==="); */
			List<ScQtFreqSchedule> freqSchedule = this.getManagedBean()
					.getScheduleList();
			if (null == freqSchedule) {
				freqSchedule = new ArrayList<ScQtFreqSchedule>();

			}

			int total = freqSchedule.size()
					+ this.getManagedBean().getTotalFrequency();
			/*
			 * if(freqSchedule.size()>this.getManagedBean().getTotalFrequency()){
			 * ScQtFreqSchedule schedule = new ScQtFreqSchedule();
			 * System.out.println("=====greater===");
			 * freqSchedule.add(schedule); }
			 */
			if (freqSchedule.size() > this.getManagedBean().getTotalFrequency()) {
				int listsize = freqSchedule.size();
				int textsize = this.getManagedBean().getTotalFrequency();
				int lesstotal = listsize - textsize;

				freqSchedule = new ArrayList<ScQtFreqSchedule>();
				for (int j = 0; j < textsize; j++) {
					ScQtFreqSchedule schedule = new ScQtFreqSchedule();

					freqSchedule.add(schedule);
				}
			}
			for (int i = freqSchedule.size(); i < this.getManagedBean()
					.getTotalFrequency(); i++) {
				ScQtFreqSchedule schedule = new ScQtFreqSchedule();

				freqSchedule.add(schedule);
			}
			this.getManagedBean().setScheduleList(freqSchedule);

		} catch (Exception e) {
			e.printStackTrace();
			// throw new IllegalArgumentException();
		}
	}

	public void addProgressiveDetails() {

		try {
			/* System.out.println("=enter addscore method==="); */
			List<ScQtProgessivePayment> progressive = this.getManagedBean()
					.getProgressivePaymentList();
			if (null == progressive) {
				progressive = new ArrayList<ScQtProgessivePayment>();

			}
			ScQtProgessivePayment payment = new ScQtProgessivePayment();

			progressive.add(payment);
			this.getManagedBean().setProgressivePaymentList(progressive);
			/*
			 * System.out.println("===exit add score=="+ this.getManagedBean()
			 * .getCheckListScore().size());
			 */
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void acceptScoring() {
		ScQtCheckList checkList = this.getManagedBean().getScQtChecklist();
		List<ScQtCheckListScore> checkListScore = this.getManagedBean()
				.getScoringList();
		if (null != checkListScore) {

			List<ScQtCheckListScore> localList = this.getManagedBean()
					.getCollectionScoring();
			if (localList == null) {
				localList = new ArrayList<ScQtCheckListScore>();
			}
			localList.addAll(checkListScore);
			checkList.setScQtCheckListScores(checkListScore);
			// this.getManagedBean().setCollectionScoring(localList);
		}
	}

	public List<SelectItem> getAppDetails() {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();

		UserReqDTO resDto = null;
		try {
			ScQtZoneCriteria scQtZone;
			IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(
					"pm", IUserSO.class);
			List<PmUser> comitteeUsersUsers = new ArrayList<PmUser>();
			if (getManagedBean().getComitteeUsers() == null) {
				UserReqDTO reqDTO = new UserReqDTO();
				reqDTO.setRoleCode(RoleCode.MSC_COMMITTEE.value());
				UserResDTO resDTO = userSO.retrieveUserListByRole(reqDTO);
				comitteeUsersUsers = resDTO.getUserList();
			}
			selectItems.add(new SelectItem(null, "--Select One--"));
			for (int i = 0; i < comitteeUsersUsers.size(); i++) {
				PmUser user = (PmUser) comitteeUsersUsers.get(i);
				selectItems.add(new SelectItem(user.getUserId(), user
						.getUserName()));
			}
			this.getManagedBean().setComitteeUsers(selectItems);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getManagedBean().getComitteeUsers();
	}

	public List<SelectItem> getProcurementDetails() {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		ProcurementReqDTO reqDTO = new ProcurementReqDTO();
		ProcurementResDTO resDto = null;
		try {

			IProcurementSO procurementSO = BusinessDelegate.getInstance()
					.getServiceObject("pm", IProcurementSO.class);
			System.out.println(procurementSO);
			resDto = procurementSO.retriveProcurementList();
			selectItems.add(new SelectItem(null, "--Select One--"));
			for (int i = 0; i < resDto.getProcurementList().size(); i++) {
				PmProcurementCategory pmProcurement = (PmProcurementCategory) resDto
						.getProcurementList().get(i);
				selectItems.add(new SelectItem(pmProcurement.getCategoryCode(),
						pmProcurement.getCategoryName()));
			}
			this.getManagedBean().setProcurementListDetails(selectItems);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getManagedBean().getProcurementListDetails();
	}

	public List<SelectItem> getCurrencyDetails() {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		CurrencyReqDTO reqDTO = new CurrencyReqDTO();
		CurrencyResDTO resDto = null;
		try {

			ICurrencySO CurrencySO = BusinessDelegate.getInstance()
					.getServiceObject("pm", ICurrencySO.class);
			resDto = CurrencySO.retriveCurrencyList();
			selectItems.add(new SelectItem(null, "--Select One--"));
			for (int i = 0; i < resDto.getCurrencyList().size(); i++) {
				PmCurrency currency = (PmCurrency) resDto.getCurrencyList()
						.get(i);
				selectItems.add(new SelectItem(currency.getCurrencyCode(),
						currency.getCurrencyName()));
			}
			this.getManagedBean().setCurrencyListDetails(selectItems);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getManagedBean().getCurrencyListDetails();
	}

	public void handleFileUpload(FileUploadEvent event) {

		ScQtCheckList scQtChecklist = this.getManagedBean().getScQtChecklist();

		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"The file was uploaded successfully.", null);
		FacesContext.getCurrentInstance().addMessage(null, message);

		ScQtCheckListAttachment attachment = scQtChecklist
				.getScQtCheckListAttachment();
		if (null == attachment) {
			attachment = new ScQtCheckListAttachment();
			attachment.setScQtCheckList(scQtChecklist);
		}
		uploadAttachment(event.getFile(), attachment);

		scQtChecklist.setScQtCheckListAttachment(attachment);
	}

	public void preRenderView() {
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");

		this.getManagedBean().setTaskId(taskId);

		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);
		// TODO
		FacesScopes facesScopes = new FacesScopes();
		String ctx = facesScopes.getBPMAuthenticationCtxToken();
		// String ctx = bpmSO.authenticate("roslan", "welcome1");
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setCtxToken(ctx);
		reqDTO.setTaskId(taskId);
		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
		Object obj = res.getPojo();
		this.getManagedBean().setOutComes(res.getAction());
		SCSpecificationDataType specificationDataType = (SCSpecificationDataType) obj;

		this.getManagedBean().setSpecificationDataType(specificationDataType);

		IQTDetailSO detailSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTDetailSO.class);
		QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
		tenderReqDTO.setQtId(new Long(specificationDataType.getQtId())
				.longValue());
		QuationTenderResDTO tenderResDTO = detailSO
				.retriveQTDetails(tenderReqDTO);
		QTDetailBackingBean detailBackingBean = new QTDetailBackingBean();
		detailBackingBean.getManagedBean().setQt(tenderResDTO.getQtDetail());
		detailBackingBean.populateParentOrgProfile(tenderResDTO.getQtDetail());
		// detailBackingBean.populateParentOrgProfile(tenderResDTO.getQtDetail());
		retriveSpecdetails(specificationDataType.getQtId().longValue());
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(
						fc,
						null,
						"/xhtml/ss/specification/specificationview.xhtml"
								+ "?faces-redirect=true");
	}

	public String submitSpecDetails(String outCome) {

		SpecificationReqDTO specReqDTO = prepareSpecReq();
		FacesScopes facesScopes = new FacesScopes();
		String ctxToken = facesScopes.getBPMAuthenticationCtxToken();
		specReqDTO.setToken(ctxToken);

		specReqDTO.setTaskId(this.getManagedBean().getTaskId());

		specReqDTO.setScSpecificationDataType(this.getManagedBean()
				.getSpecificationDataType());
		specReqDTO.setOutcome(outCome);
		IQTSpecSO iqtSpecSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTSpecSO.class);
		try {
			SpecificationResDTO resDTO = iqtSpecSO.updateBPMAction(specReqDTO);
			if (resDTO.isSuccessful()) {

				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Record saved successfully.", ""

						));

				FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication()
						.getNavigationHandler()
						.handleNavigation(fc, null,
								"/xhtml/shared/acknowledge.xhtml");

			} else {

			}
		} catch (Exception ex) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Sorry We are unable to process.", ""));
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * @return the selctValue
	 */
	public String getSelctValue() {
		return selctValue;
	}

	/**
	 * @param selctValue
	 *            the selctValue to set
	 */
	public void setSelctValue(String selctValue) {
		this.selctValue = selctValue;
	}

	/**
	 * @return the selectedCompilenceList
	 */
	public List<SpecificationCompilanceBean> getSelectedCompilenceList() {
		return selectedCompilenceList;
	}

	/**
	 * @param selectedCompilenceList
	 *            the selectedCompilenceList to set
	 */
	public void setSelectedCompilenceList(
			List<SpecificationCompilanceBean> selectedCompilenceList) {
		this.selectedCompilenceList = selectedCompilenceList;
	}

	public String updateBpmTask(String action) {
		System.out.println("Succeesfully update the task");
		SpecificationReqDTO spReqDTO = prepareSpecReq();
		spReqDTO.setOutcome(action);
		spReqDTO.setTaskId(this.getManagedBean().getTaskId());
		spReqDTO.setPayLoad(this.getManagedBean().getSpecificationDataType());
		FacesScopes facesScopes = new FacesScopes();
		String ctx = facesScopes.getBPMAuthenticationCtxToken();
		spReqDTO.setToken(ctx);
		IQTSpecSO specSO = getSpecSO();
		try {
			SpecificationResDTO resDTO = specSO.updateBPMAction(spReqDTO);
			if (resDTO.isSuccessful()) {

				FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication()
						.getNavigationHandler()
						.handleNavigation(fc, null,
								"/xhtml/shared/acknowledge.xhtml");

			} else {
				throw new EpException("");
			}
		} catch (Exception ex) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Unable to process ", " "

					));
		}
		return null;

	}

	private IQTSpecSO getSpecSO() {
		return BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTSpecSO.class);
	}

	public void handleS2TechChanged(ScQtCheckList checklist) {
		try {

			ScQtWeightage weightage = checklist.getScQtWeightage();

			if (weightage != null) {

				List<ScQtWeightage> weightList = this.getManagedBean()
						.getWeightageTechnical();
				for (ScQtWeightage techweight : weightList) {

					if (techweight.getWeightageTitle().equalsIgnoreCase(
							weightage.getWeightageTitle())) {

						checklist.setScQtWeightage(techweight);

						break;
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void handleS2AddTechChanged(ScQtCheckList checklist) {
		try {

			ScQtWeightage weightage = checklist.getScQtWeightage();

			if (weightage != null) {

				List<ScQtWeightage> weightList = this.getManagedBean()
						.getWeightageTechnical();
				for (ScQtWeightage techweight : weightList) {

					if (techweight.getWeightageTitle().equalsIgnoreCase(
							weightage.getWeightageTitle())) {

						checklist.setScQtWeightage(techweight);

						break;
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void handleS2FinChanged(ScQtCheckList checklist) {

		try {
			ScQtWeightage weightage = checklist.getScQtWeightage();

			if (weightage != null) {

				List<ScQtWeightage> weightList = this.getManagedBean()
						.getWeightageFinancial();
				for (ScQtWeightage techweight : weightList) {

					if (techweight.getWeightageTitle().equalsIgnoreCase(
							weightage.getWeightageTitle())) {

						checklist.setScQtWeightage(techweight);

						break;
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void handleS2AddFinChanged(ScQtCheckList checklist) {

		try {
			ScQtWeightage weightage = checklist.getScQtWeightage();

			if (weightage != null) {

				List<ScQtWeightage> weightList = this.getManagedBean()
						.getWeightageFinancial();
				for (ScQtWeightage techweight : weightList) {

					if (techweight.getWeightageTitle().equalsIgnoreCase(
							weightage.getWeightageTitle())) {

						checklist.setScQtWeightage(techweight);

						break;
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteTechnicalWeightage() {
		List<ScQtWeightage> weightageTechnical = getManagedBean()
				.getWeightageTechnical();
		List<ScQtWeightage> selectTenical = getManagedBean()
				.getWeightageTechnicalSelect();
		if (weightageTechnical.size() == selectTenical.size()) {
			weightageTechnical.clear();
		} else {
			for (ScQtWeightage weightage : selectTenical)
				weightageTechnical.remove(weightage);
		}

	}

	public void deleteFinancialWeightage() {
		List<ScQtWeightage> weightageTechnical = getManagedBean()
				.getWeightageFinancial();
		List<ScQtWeightage> selectTenical = getManagedBean()
				.getWeightageFinancialSelect();
		if (weightageTechnical.size() == selectTenical.size()) {
			weightageTechnical.clear();
		} else {
			for (ScQtWeightage weightage : selectTenical)
				weightageTechnical.remove(weightage);
		}

	}

	public void deleteS1Tech() {
		List<ScQtCheckList> s1Technical = this.getManagedBean()
				.getScQtCheckSt1techProposal();

		List<ScQtCheckList> selectTenical = this.getManagedBean()
				.getS1TechSelect();

		if (s1Technical.size() == selectTenical.size()) {
			s1Technical.clear();
		} else {
			for (ScQtCheckList checkList : selectTenical)
				s1Technical.remove(checkList);
		}

	}

	public void deleteS1TechAdd() {
		List<ScQtCheckList> s1TechnicalAdd = getManagedBean()
				.getScQtCheckSt1techAdditionalProposalList();
		List<ScQtCheckList> selectTenicalAdd = getManagedBean()
				.getS1TechAddSelect();
		if (s1TechnicalAdd.size() == selectTenicalAdd.size()) {
			s1TechnicalAdd.clear();
		} else {
			for (ScQtCheckList checkList : selectTenicalAdd)
				s1TechnicalAdd.remove(checkList);
		}

	}

	public void deleteS1Fin() {
		List<ScQtCheckList> s1Fin = getManagedBean()
				.getScQtCheckSt1financeProposalList();
		List<ScQtCheckList> selectFin = getManagedBean().getS1FinSelect();
		if (s1Fin.size() == selectFin.size()) {
			s1Fin.clear();
		} else {
			for (ScQtCheckList checkList : selectFin)
				s1Fin.remove(checkList);
		}

	}

	public void deleteS1FinAdd() {
		List<ScQtCheckList> s1FinAdd = getManagedBean()
				.getScQtCheckSt1financeAdditionalProposalList();
		List<ScQtCheckList> selectFinAdd = getManagedBean().getS1FinAddSelect();
		if (s1FinAdd.size() == selectFinAdd.size()) {
			s1FinAdd.clear();
		} else {
			for (ScQtCheckList checkList : selectFinAdd)
				s1FinAdd.remove(checkList);
		}

	}

	public void deleteS2Tech() {
		List<ScQtCheckList> s2Technical = getManagedBean()
				.getScQtCheckSt2techProposal();
		List<ScQtCheckList> selects2Tenical = getManagedBean()
				.getS2TechSelect();

		if (s2Technical.size() == selects2Tenical.size()) {
			s2Technical.clear();
		} else {
			for (ScQtCheckList checkList : selects2Tenical)
				s2Technical.remove(checkList);
		}

	}

	public void deleteS2TechAdd() {
		List<ScQtCheckList> s2TechnicalAdd = getManagedBean()
				.getScQtCheckSt2techAdditionalProposalList();
		List<ScQtCheckList> selects2TenicalAdd = getManagedBean()
				.getS2TechAddSelect();
		if (s2TechnicalAdd.size() == selects2TenicalAdd.size()) {
			s2TechnicalAdd.clear();
		} else {
			for (ScQtCheckList checkList : selects2TenicalAdd)
				s2TechnicalAdd.remove(checkList);
		}

	}

	public void deleteS2Fin() {
		List<ScQtCheckList> s2Fin = getManagedBean()
				.getScQtCheckSt2financeProposalList();
		List<ScQtCheckList> selects2Fin = getManagedBean().getS2FinSelect();
		if (s2Fin.size() == selects2Fin.size()) {
			s2Fin.clear();
		} else {
			for (ScQtCheckList checkList : selects2Fin)
				s2Fin.remove(checkList);
		}

	}

	public void deleteS2FinAdd() {
		List<ScQtCheckList> s2FinAdd = getManagedBean()
				.getScQtCheckSt2financeAdditionalProposalList();
		List<ScQtCheckList> selects2FinAdd = getManagedBean()
				.getS2FinAddSelect();
		if (s2FinAdd.size() == selects2FinAdd.size()) {
			s2FinAdd.clear();
		} else {
			for (ScQtCheckList checkList : selects2FinAdd)
				s2FinAdd.remove(checkList);
		}

	}

	public void deleteScoring() {
		List<ScQtCheckListScore> scoringList = getManagedBean()
				.getScoringList();

		List<ScQtCheckListScore> selectScoring = this.getManagedBean()
				.getSelectscoring();

		if (scoringList.size() == selectScoring.size()) {
			scoringList.clear();
		} else {
			for (ScQtCheckListScore scoring : selectScoring)
				scoringList.remove(scoring);
		}

	}

	public void onScoringRowSelect(SelectEvent selectEvent) {
		IQTEvaluationSO evaluationSO;
		try {
			List<String> docType = new ArrayList<String>();
			ScQtCheckListScore score = (ScQtCheckListScore) selectEvent
					.getObject();
			List<ScQtCheckListScore> selectScoring = this.getManagedBean()
					.getSelectscoring();
			if (selectScoring == null) {
				selectScoring = new ArrayList<ScQtCheckListScore>();
			}
			selectScoring.add(score);
			this.getManagedBean().setSelectscoring(selectScoring);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			evaluationSO = null;
		}

	}

	public void circularListSelect(SelectEvent selectEvent) {
		ICircularDetailSO circularDetailSO;
		try {
			CircularDetailReqDTO reqDTO = new CircularDetailReqDTO();
			CtCircularVer circular = (CtCircularVer) selectEvent.getObject();

			circularDetailSO = (ICircularDetailSO) EpEjbBusinessDelegate
					.getInstance().getServiceObject("ct",
							ICircularDetailSO.class);

			reqDTO.setCircularVerId(circular.getCircularVerId());
			CircularDetailResDTO resDTO = circularDetailSO
					.getPanelItemsByCircularVerId(reqDTO);
			for (CtPanelItem panelItem : resDTO.getPanelItemList()) {
				System.out.println(panelItem.getItemName());
			}
			this.getManagedBean().setCirCularNo(circular.getCircularNo());
			this.getManagedBean().setCircularName(circular.getName());
			this.getManagedBean().setPanelItemList(resDTO.getPanelItemList());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

	}

	public void panelPopulate() {
		try {
			this.getManagedBean().getPanelItemList();
			List<CtPanelItem> detailsList = this.getManagedBean()
					.getPanelItemDetailsList();
			if (detailsList == null) {
				detailsList = new ArrayList<CtPanelItem>();
			}
			for (CtPanelItem panelItem : this.getManagedBean()
					.getPanelItemList()) {
				detailsList.add(panelItem);
			}
			this.getManagedBean().setPanelItemDetailsList(detailsList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * public void deleteItemDetails() { List<CtPanelItem> itemDetailsList=
	 * this.getManagedBean().getPanelItemDetailsList();
	 * 
	 * List<CtPanelItem> selectItem =this. getManagedBean()
	 * .getSelectItemDetailsList();
	 * 
	 * if (itemDetailsList.size() == selectItem.size()) {
	 * itemDetailsList.clear(); } else { for (CtPanelItem item : selectItem)
	 * itemDetailsList.remove(item); }
	 * 
	 * }
	 */

	public void techWeightageSum() {
		try {
			int total = 0;
			List<ScQtWeightage> weightageList = this.getManagedBean()
					.getWeightageTechnical();
			if (weightageList == null) {
				weightageList = new ArrayList<ScQtWeightage>();
			}
			for (int i = 0; i < weightageList.size(); i++) {
				ScQtWeightage weight = (ScQtWeightage) weightageList.get(i);
				total = total + weight.getWeightagePercentage().intValue();
				BigDecimal b1 = new BigDecimal(total);
				BigDecimal techWeightage = new BigDecimal(100);
				MathContext mc = new MathContext(4);
				BigDecimal techWeightage1 = b1.divide(techWeightage, mc);
				this.getManagedBean()
						.setTechnicalWeightageScore(techWeightage1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void finWeightageSum() {
		try {
			int total = 0;
			List<ScQtWeightage> weightageList = this.getManagedBean()
					.getWeightageFinancial();
			if (weightageList == null) {
				weightageList = new ArrayList<ScQtWeightage>();
			}
			for (int i = 0; i < weightageList.size(); i++) {
				ScQtWeightage weight = (ScQtWeightage) weightageList.get(i);
				total = total + weight.getWeightagePercentage().intValue();
				BigDecimal b1 = new BigDecimal(total);
				BigDecimal techWeightage = new BigDecimal(100);
				MathContext mc = new MathContext(4);
				BigDecimal techWeightage1 = b1.divide(techWeightage, mc);
				this.getManagedBean()
						.setFinancialWeightageScore(techWeightage1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @return the searchCircular
	 */
	public IPagingDataModel<CtCircularVer> getSearchCircular() {
		return searchCircular;
	}

	/**
	 * @param searchCircular
	 *            the searchCircular to set
	 */
	public void setSearchCircular(IPagingDataModel<CtCircularVer> searchCircular) {
		this.searchCircular = searchCircular;
	}

	public void searchCircularList() {
		searchCircular.reloadData();
	}

	private class SearchCircularPagingDataModel extends
			PagingDataModel<CtCircularVer> {
		private static final long serialVersionUID = 1L;

		public SearchCircularPagingDataModel() {

			super("circularList");

		}

		public ISearchResultBean<CtCircularVer> pageChangeListener(
				ISearchBean<CtCircularVer> searchBean) {
			ICircularSO circularSO;
			CircularResDTO resDto;

			try {
				CircularReqDTO circularReqDTO = new CircularReqDTO();

				if (getManagedBean().getCirCularNo() != null
						&& !"".equalsIgnoreCase(getManagedBean()
								.getCirCularNo())) {
					SearchFilterBean circularBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					circularBean.setSearchField("circularNo");
					circularBean.setSearchValue(getManagedBean()
							.getCirCularNo());
					searchBean.getDataFilterBeans().add(circularBean);

				}

				if (getManagedBean().getCircularName() != null
						&& !"".equalsIgnoreCase(getManagedBean()
								.getCircularName())) {
					SearchFilterBean circularBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					circularBean.setSearchField("name");
					circularBean.setSearchValue(getManagedBean()
							.getCircularName());
					searchBean.getDataFilterBeans().add(circularBean);
				}

				circularReqDTO.setCircularVerSearchBean(searchBean);

				circularSO = EpEjbBusinessDelegate.getInstance()
						.getServiceObject("ct", ICircularSO.class);
				resDto = circularSO.searchCircularVer(circularReqDTO);
				if (resDto.isSuccessful()) {
					return resDto.getCircularVerResultBean();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				circularSO = null;
			}
			return null;

		}
	}

}
