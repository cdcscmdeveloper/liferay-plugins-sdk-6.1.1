package my.ep.web.backingbean.pm.category;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.common.categorycode.CategoryCodeReqDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.common.PmBasicCategory;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCategoryRestr;
import my.ep.persistence.entity.pm.common.PmMachine;
import my.ep.persistence.entity.pm.common.PmMachineCategory;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.pm.category.CategoryManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@ManagedBean(name = "categoryBackingBean")
@ViewScoped
//@RequestScoped
public class CategoryBackingBean extends BaseBackingBean<CategoryManagedBean> {
	private static Logger log = Logger.getLogger(CategoryBackingBean.class);

	private IPagingDataModel<Object[]> searchCategory = new SearchCategoryPagingDataModel();
	private IPagingDataModel<PmCategoryL3> searchLOVPagingDataModel = new SearchLOVPagingDataModel();
	private IPagingDataModel<PmMachine> searchLOVMachinePagingDataModel = new SearchLOVMachinePagingDataModel();
	private boolean flag;
	boolean result = false;

	public CategoryBackingBean() {
		
	}
	
	public void SearchCategory() {
		boolean hasSearchValue = false;
		
		if (getManagedBean().getPmCategoryLevel() != null && getManagedBean().getPmCategoryLevel().intValue()>0) {
			hasSearchValue = true;
		}

		if(!hasSearchValue) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
						MessageUtils.getMessage(MessageConst.ERROR_721110,
							new String[]{ResourceBundleUtils.getMessage("lbl.pm.category.level",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())},
							MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
		} 

		searchCategory.reloadData();
	}
	
	private class SearchCategoryPagingDataModel extends PagingDataModel<Object[]> {

		private static final long serialVersionUID = 1L;

		public SearchCategoryPagingDataModel() {
			super("searchCategory");
		}

		public ISearchResultBean<Object[]> pageChangeListener(ISearchBean<Object[]> searchBean) {
			ICategoryCodeSO categoryCodeSO;
			try {
				categoryCodeSO = (ICategoryCodeSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICategoryCodeSO.class);
				CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();

				if (getManagedBean().getMainCatCode() != null) {
					reqDTO.setMainCatCode(getManagedBean().getMainCatCode());
				}
				if (getManagedBean().getMainCatDesc() != null) {
					reqDTO.setMainCatDesc(getManagedBean().getMainCatDesc());
				}
				
				if (getManagedBean().getSubCatCode() != null) {
					reqDTO.setSubCatCode(getManagedBean().getSubCatCode());
				}
				if (getManagedBean().getSubCatDesc() != null) {
					reqDTO.setSubCatDesc(getManagedBean().getSubCatDesc());
				}
				
				if (getManagedBean().getSubDivCode() != null) {
					reqDTO.setSubDivCode(getManagedBean().getSubDivCode());
				}
				if (getManagedBean().getSubDivDesc() != null) {
					reqDTO.setSubDivDesc(getManagedBean().getSubDivDesc());
				}
				
				if (getManagedBean().getPmCategoryLevel() != null && getManagedBean().getPmCategoryLevel().intValue()>0) {
					
					reqDTO.setPmCategoryLevel(getManagedBean().getPmCategoryLevel());
					reqDTO.setSearchBeanObject(searchBean);
					CategoryCodeResDTO resDTO =categoryCodeSO.searchAllCategory(reqDTO);
					
					if (resDTO.isSuccessful()) {
						//resDTO.getCategoryResultBean().getResultList();
						return resDTO.getCategoryResultBean();
					}
				}/*else{
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(MessageConst.INFO_700004,
									new String[]{"",ResourceBundleUtils.getMessage("lbl.pm.sub.category",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())},
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
				}*/
	
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				categoryCodeSO = null;
			}
			return null;
		}
	}
	
	private class SearchLOVPagingDataModel extends PagingDataModel<PmCategoryL3> {

		private static final long serialVersionUID = 1L;

		public SearchLOVPagingDataModel() {
			super("searchLOVPagingDataModel");
		}

		public ISearchResultBean<PmCategoryL3> pageChangeListener(ISearchBean<PmCategoryL3> searchBean) {
			ICategoryCodeSO categoryCodeSO;
			try {
				categoryCodeSO = (ICategoryCodeSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICategoryCodeSO.class);
				CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
				
				/*if (getManagedBean().getSearchValue()[0] != null && !getManagedBean().getSearchValue()[0].equals("")) {
					if(getManagedBean().getSearchFieldValue()[0].equals("1"))
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean("pmCategoryL2.pmCategoryL1.categoryL1Code",
										Integer.parseInt(getManagedBean().getSearchOperatorValue()[i]),
										getManagedBean().getSearchValue()[i]));
				}*/
				
				for(int i=0;i<3;i++){
					if (getManagedBean().getSearchValue()[i] != null && !getManagedBean().getSearchValue()[i].equals("")) {
						if (getManagedBean().getSearchFieldValue()[i] != null && !getManagedBean().getSearchFieldValue()[i].equals("")) {
							if(getManagedBean().getSearchFieldValue()[i].equals("1"))
								searchBean.getDataFilterBeans().add(
										new SearchFilterBean("pmCategoryL2.pmCategoryL1.categoryL1Code",
												Integer.parseInt(getManagedBean().getSearchOperatorValue()[i]),
												getManagedBean().getSearchValue()[i]));
							else if(getManagedBean().getSearchFieldValue()[i].equals("2"))
								searchBean.getDataFilterBeans().add(
										new SearchFilterBean("pmCategoryL2.categoryL2Code",
												Integer.parseInt(getManagedBean().getSearchOperatorValue()[i]),
												getManagedBean().getSearchValue()[i]));
							else if(getManagedBean().getSearchFieldValue()[i].equals("3"))
								searchBean.getDataFilterBeans().add(
										new SearchFilterBean("categoryL3Code",
												Integer.parseInt(getManagedBean().getSearchOperatorValue()[i]),
												getManagedBean().getSearchValue()[i]));
							else if(getManagedBean().getSearchFieldValue()[i].equals("4"))
								searchBean.getDataFilterBeans().add(
										new SearchFilterBean("pmCategoryL2.pmCategoryL1.categoryName",
												Integer.parseInt(getManagedBean().getSearchOperatorValue()[i]),
												getManagedBean().getSearchValue()[i]));
							else if(getManagedBean().getSearchFieldValue()[i].equals("5"))
								searchBean.getDataFilterBeans().add(
										new SearchFilterBean("pmCategoryL2.categoryName",
												Integer.parseInt(getManagedBean().getSearchOperatorValue()[i]),
												getManagedBean().getSearchValue()[i]));
							else if(getManagedBean().getSearchFieldValue()[i].equals("6"))
								searchBean.getDataFilterBeans().add(
										new SearchFilterBean("categoryName",
												Integer.parseInt(getManagedBean().getSearchOperatorValue()[i]),
												getManagedBean().getSearchValue()[i]));
						}
					}
				}
				
				
				reqDTO.setSearchBean(searchBean);
				CategoryCodeResDTO resDTO =categoryCodeSO.searchAllCategoryL3(reqDTO);

				if (resDTO.isSuccessful()) {
					//resDTO.getCategoryResultBean().getResultList();
					return resDTO.getCategoryL3ResultBean();
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				categoryCodeSO = null;
			}
			return null;
		}
	}
	
	private class SearchLOVMachinePagingDataModel extends PagingDataModel<PmMachine> {

		private static final long serialVersionUID = 1L;

		public SearchLOVMachinePagingDataModel() {
			super("searchLOVMachinePagingDataModel");
		}

		public ISearchResultBean<PmMachine> pageChangeListener(ISearchBean<PmMachine> searchBean) {
			ICategoryCodeSO categoryCodeSO;
			try {
				categoryCodeSO = (ICategoryCodeSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICategoryCodeSO.class);
				CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
				
				/*if (getManagedBean().getSearchValue()[0] != null && !getManagedBean().getSearchValue()[0].equals("")) {
					if(getManagedBean().getSearchFieldValue()[0].equals("1"))
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean("pmCategoryL2.pmCategoryL1.categoryL1Code",
										Integer.parseInt(getManagedBean().getSearchOperatorValue()[i]),
										getManagedBean().getSearchValue()[i]));
				}*/
				
				for(int i=0;i<2;i++){
					if (getManagedBean().getSearchValue()[i] != null && !getManagedBean().getSearchValue()[i].equals("")) {
						if (getManagedBean().getSearchFieldValue()[i] != null && !getManagedBean().getSearchFieldValue()[i].equals("")) {
							if(getManagedBean().getSearchFieldValue()[i].equals("1"))
								searchBean.getDataFilterBeans().add(
										new SearchFilterBean("machineCode",
												Integer.parseInt(getManagedBean().getSearchOperatorValue()[i]),
												getManagedBean().getSearchValue()[i].toUpperCase()));
							else if(getManagedBean().getSearchFieldValue()[i].equals("2"))
								searchBean.getDataFilterBeans().add(
										new SearchFilterBean("machineDesc",
												Integer.parseInt(getManagedBean().getSearchOperatorValue()[i]),
												getManagedBean().getSearchValue()[i].toUpperCase()));
						}
					}
				}
				
				
				reqDTO.setSearchMachineBean(searchBean);
				CategoryCodeResDTO resDTO =categoryCodeSO.searchAllPmMachine(reqDTO);

				if (resDTO.isSuccessful()) {
					//resDTO.getCategoryResultBean().getResultList();
					return resDTO.getPmMachineResultBean();
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				categoryCodeSO = null;
			}
			return null;
		}
	}
	
	public String addCategory() {
		this.getManagedBean().setPmCategoryL1(new PmCategoryL1());
		this.getManagedBean().setCatLevel("1");
		this.getManagedBean().setNewCategory(true);
		return "/xhtml/pm/category/categoryl1.xhtml?faces-redirect=true";
	}
	
	public String addSubCategory() {
		PmCategoryL2 pmCategoryL2 = new PmCategoryL2();
		pmCategoryL2.setPmCategoryL1(this.getManagedBean().getPmCategoryL1());
		
		this.getManagedBean().setPmCategoryL2(pmCategoryL2);
		this.getManagedBean().setCatLevel("2");
		this.getManagedBean().setNewCategory(true);
		return "/xhtml/pm/category/categoryl2.xhtml";
	}
	
	public String addSubCategory(Long id) {
		ICategoryCodeSO categoryCodeSO = (ICategoryCodeSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICategoryCodeSO.class);
		try{
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			
			log.info("addSubCategory parent id = "+id);
			
			reqDTO.setCategoryL1Id(id);
			CategoryCodeResDTO resDTO = categoryCodeSO.retrieveCategoryL1DtlById(reqDTO);

			if (resDTO.isSuccessful()) {
				this.getManagedBean().setPmCategoryL1(resDTO.getCategoryL1());
				
				PmCategoryL2 pmCategoryL2 = new PmCategoryL2();
				pmCategoryL2.setPmCategoryL1(this.getManagedBean().getPmCategoryL1());
				this.getManagedBean().setPmCategoryL2(pmCategoryL2);
				this.getManagedBean().setCatLevel("2");
				this.getManagedBean().setNewCategory(true);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			categoryCodeSO = null;
		}
		
		return "/xhtml/pm/category/categoryl2.xhtml?faces-redirect=true";
	}
	
	public String addSubDivision() {
		PmCategoryL3 pmCategoryL3 = new PmCategoryL3();
		pmCategoryL3.setPmCategoryL2(this.getManagedBean().getPmCategoryL2());
		
		pmCategoryL3.setCategoryL3Id(null);
		pmCategoryL3.setSupplierTypeCodeVal(0);
		pmCategoryL3.setCategoryTypeCodeVal(0);
		
		this.getManagedBean().setPmCategoryL3(pmCategoryL3);
		//this.getManagedBean().setPmBasicCategoryList(new ArrayList<PmBasicCategory>());
		//this.getManagedBean().setPmBasicCategoryList(null);
		this.getManagedBean().setCatLevel("3");
		this.getManagedBean().setNewCategory(true);
		reloadSelectionPmCategoryL3();
		return "/xhtml/pm/category/categoryl3.xhtml";
	}
	
	public String addSubDivision(Long id) {
		ICategoryCodeSO categoryCodeSO;
		try{
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			categoryCodeSO = (ICategoryCodeSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICategoryCodeSO.class);

			reqDTO.setCategoryL2Id(id);
			CategoryCodeResDTO resDTO = categoryCodeSO.retrieveCategoryL2DtlById(reqDTO);

			if (resDTO.isSuccessful()) {
				this.getManagedBean().setPmCategoryL2(resDTO.getCategoryL2());
				
				PmCategoryL3 pmCategoryL3 = new PmCategoryL3();
				pmCategoryL3.setPmCategoryL2(this.getManagedBean().getPmCategoryL2());
				
				pmCategoryL3.setCategoryL3Id(null);
				pmCategoryL3.setSupplierTypeCodeVal(0);
				pmCategoryL3.setCategoryTypeCodeVal(0);
				
				this.getManagedBean().setPmCategoryL3(pmCategoryL3);
				//this.getManagedBean().setPmBasicCategoryList(new ArrayList<PmBasicCategory>());
				//this.getManagedBean().setPmBasicCategoryList(null);
				this.getManagedBean().setCatLevel("3");
				this.getManagedBean().setNewCategory(true);
				reloadSelectionPmCategoryL3();
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			categoryCodeSO = null;
		}
		
		return "/xhtml/pm/category/categoryl3.xhtml?faces-redirect=true";
	}
	
	public void addBasicCategory() {
		
		List<SelectItem> searchField = new ArrayList<SelectItem>();
		searchField.add(new SelectItem(1, ResourceBundleUtils.getMessage("lbl.pm.main.category.code",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())));
		searchField.add(new SelectItem(2, ResourceBundleUtils.getMessage("lbl.pm.sub.category.code",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())));
		searchField.add(new SelectItem(3, ResourceBundleUtils.getMessage("lbl.pm.sub.div.code",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())));
		searchField.add(new SelectItem(4, ResourceBundleUtils.getMessage("lbl.pm.main.category.name",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())));
		searchField.add(new SelectItem(5, ResourceBundleUtils.getMessage("lbl.pm.sub.category.name",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())));
		searchField.add(new SelectItem(6, ResourceBundleUtils.getMessage("lbl.pm.sub.div.name",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())));
		
		List<SelectItem> searchOperator = new ArrayList<SelectItem>();
		SelectItem item1 = new SelectItem(SearchOperator.OPERATOR_LIKE.value(), ResourceBundleUtils.getMessage("lbl.operator.equals",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()));
		SelectItem item2 = new SelectItem(SearchOperator.OPERATOR_STARTS_WITH.value(), ResourceBundleUtils.getMessage("lbl.operator.startWith",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()));
		SelectItem item3 = new SelectItem(SearchOperator.OPERATOR_END_WITH.value(), ResourceBundleUtils.getMessage("lbl.operator.endWith",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()));
		searchOperator.add(item1);
		searchOperator.add(item2);
		searchOperator.add(item3);
		
		
		
		this.getManagedBean().setSearchField(searchField);
		this.getManagedBean().setSearchOperator(searchOperator);
		
		
		this.getManagedBean().getPmCategoryL3();
	}
	
	public void addPmMachine() {
		
		List<SelectItem> searchField = new ArrayList<SelectItem>();
		searchField.add(new SelectItem(1, ResourceBundleUtils.getMessage("lbl.pm.machine.code",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())));
		searchField.add(new SelectItem(2, ResourceBundleUtils.getMessage("lbl.pm.machine.desc",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())));
		
		List<SelectItem> searchOperator = new ArrayList<SelectItem>();
		SelectItem item1 = new SelectItem(SearchOperator.OPERATOR_LIKE.value(), ResourceBundleUtils.getMessage("lbl.operator.equals",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()));
		SelectItem item2 = new SelectItem(SearchOperator.OPERATOR_STARTS_WITH.value(), ResourceBundleUtils.getMessage("lbl.operator.startWith",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()));
		SelectItem item3 = new SelectItem(SearchOperator.OPERATOR_END_WITH.value(), ResourceBundleUtils.getMessage("lbl.operator.endWith",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()));
		searchOperator.add(item1);
		searchOperator.add(item2);
		searchOperator.add(item3);
		
		
		this.getManagedBean().setSearchField(searchField);
		this.getManagedBean().setSearchOperator(searchOperator);
		
		
	}
	
	public String editCategory(Long id) {
		ICategoryCodeSO categoryCodeSO = (ICategoryCodeSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICategoryCodeSO.class);
		try{
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			
			log.info("editCategory id = "+id);
			
			reqDTO.setCategoryL1Id(id);
			CategoryCodeResDTO resDTO = categoryCodeSO.retrieveCategoryL1DtlById(reqDTO);

			if (resDTO.isSuccessful()) {
				log.info("resDTO.getCategoryL1().getPmCategoryL2s().size() = "+resDTO.getCategoryL1().getPmCategoryL2s().size());
				
				this.getManagedBean().setCatLevel("1");
				this.getManagedBean().setPmCategoryL1(resDTO.getCategoryL1());
				this.getManagedBean().setNewCategory(false);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			categoryCodeSO = null;
		}
		return "/xhtml/pm/category/categoryl1.xhtml?faces-redirect=true";
	}
	
	public void preRenderPmCategoryl1() {
		log.info("In preRenderPmCategoryl1");
		ICategoryCodeSO categoryCodeSO;
		try{
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			categoryCodeSO = (ICategoryCodeSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICategoryCodeSO.class);

			/*if(this.getManagedBean().isNewCategory()){
				reqDTO.setCategoryL2Id(this.getManagedBean().getPmCategoryL2().getCategoryL2Id());
				CategoryCodeResDTO resDTO = categoryCodeSO.retrieveCategoryL2DtlById(reqDTO);
				reqDTO.setCategoryL1Id(resDTO.getCategoryL2().getPmCategoryL1().getCategoryL1Id());
			}*/
			reqDTO.setCategoryL1Id(this.getManagedBean().getPmCategoryL1().getCategoryL1Id());
			
			CategoryCodeResDTO resDTO = categoryCodeSO.retrieveCategoryL1DtlById(reqDTO);

			if (resDTO.isSuccessful()) {
				this.getManagedBean().setCatLevel("1");
				this.getManagedBean().setPmCategoryL1(resDTO.getCategoryL1());
				this.getManagedBean().setNewCategory(false);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			categoryCodeSO = null;
		}
		//return "/xhtml/pm/category/categoryl1.xhtml";
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, "/xhtml/pm/category/categoryl1.xhtml");
	}
	
	public String editSubCategory(Long id) {
		ICategoryCodeSO categoryCodeSO;
		try{
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			categoryCodeSO = (ICategoryCodeSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICategoryCodeSO.class);

			reqDTO.setCategoryL2Id(id);
			CategoryCodeResDTO resDTO = categoryCodeSO.retrieveCategoryL2DtlById(reqDTO);

			if (resDTO.isSuccessful()) {
				this.getManagedBean().setCatLevel("2");
				this.getManagedBean().setPmCategoryL2(resDTO.getCategoryL2());
				this.getManagedBean().setNewCategory(false);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			categoryCodeSO = null;
		}
		return "/xhtml/pm/category/categoryl2.xhtml?faces-redirect=true";
	}
	
	public String editSubDivision(Long id) {
		log.info("***editSubDivision id***" + id);
		ICategoryCodeSO categoryCodeSO;
		try{
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			categoryCodeSO = (ICategoryCodeSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICategoryCodeSO.class);

			reqDTO.setCategoryL3Id(id);
			CategoryCodeResDTO resDTO = categoryCodeSO.retrieveCategoryL3DtlById(reqDTO);

			if (resDTO.isSuccessful()) {
				log.info("***editSubDivision - Category Name***" + resDTO.getCategoryL3().getCategoryName());
				this.getManagedBean().setCatLevel("3");
				resDTO.getCategoryL3().setBasicCategoryReqValue(resDTO.getCategoryL3().getBasicCategoryReq()==1?true:false);
				resDTO.getCategoryL3().setHomeOperatedValue(resDTO.getCategoryL3().getHomeOperated()==1?true:false);
				this.getManagedBean().setPmCategoryL3(resDTO.getCategoryL3());
				this.getManagedBean().setNewCategory(false);
				reloadSelectionPmCategoryL3();
				for(PmBasicCategory pmBasicCategory: this.getManagedBean().getPmCategoryL3().getPmBasicCategory()){
					reqDTO.setCategoryL3Id(pmBasicCategory.getBasicCategoryL3());
					CategoryCodeResDTO bresDTO = categoryCodeSO.retrieveCategoryL3DtlById(reqDTO);
					pmBasicCategory.setBasicCategoryL1desc(bresDTO.getCategoryL3().getPmCategoryL2().getPmCategoryL1().getCategoryName());
					pmBasicCategory.setBasicCategoryL2desc(bresDTO.getCategoryL3().getPmCategoryL2().getCategoryName());
					pmBasicCategory.setBasicCategoryL3desc(bresDTO.getCategoryL3().getCategoryName());
					pmBasicCategory.setCodeConcatenate(
							bresDTO.getCategoryL3().getPmCategoryL2().getPmCategoryL1().getCategoryL1Code()+
							bresDTO.getCategoryL3().getPmCategoryL2().getCategoryL2Code()+
							bresDTO.getCategoryL3().getCategoryL3Code());
				}
				this.getManagedBean().setPmCategoryL2(resDTO.getCategoryL3().getPmCategoryL2());
				this.getManagedBean().setPmCategoryL3(resDTO.getCategoryL3());
				
				log.info("***editSubDivision - managed bean L3 - Category Name***" +this.getManagedBean().getPmCategoryL3().getCategoryName());
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			categoryCodeSO = null;
		}
		return "/xhtml/pm/category/categoryl3.xhtml?faces-redirect=true";
	}
	
	public String saveCategory() {
		ICategoryCodeSO categoryCodeSO;
		try{
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			categoryCodeSO = (ICategoryCodeSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICategoryCodeSO.class);

			this.getManagedBean().getPmCategoryL1().setCategoryName(this.getManagedBean().getPmCategoryL1().getCategoryName().toUpperCase());
			
			if (!validatePmCategoryL1()){
				return "/xhtml/pm/category/categoryl1.xhtml"; 
			}
			
			reqDTO.setPmCategoryL1(this.getManagedBean().getPmCategoryL1());
			CategoryCodeResDTO resDTO = categoryCodeSO.saveCategory(reqDTO);

			if (resDTO.isSuccessful()) {
				result = resDTO.isSuccessful();
				resetCategory();
				FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
						MessageUtils.getMessage(MessageConst.INFO_700004,
							new String[]{"",ResourceBundleUtils.getMessage("lbl.pm.main.category",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())},
							MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			categoryCodeSO = null;
		}

		return "/xhtml/pm/category/search.xhtml";
	}
	
	public String saveSubCategory() {
		ICategoryCodeSO categoryCodeSO;
		try{
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			categoryCodeSO = (ICategoryCodeSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICategoryCodeSO.class);

			this.getManagedBean().getPmCategoryL2().setCategoryName(this.getManagedBean().getPmCategoryL2().getCategoryName().toUpperCase());
			
			if (!validatePmCategoryL2()){
				return "/xhtml/pm/category/categoryl2.xhtml"; 
			}
			
			reqDTO.setPmCategoryL2(this.getManagedBean().getPmCategoryL2());
			reqDTO.setNewCategory(this.getManagedBean().isNewCategory());
			CategoryCodeResDTO resDTO = categoryCodeSO.saveSubCategory(reqDTO);

			if (resDTO.isSuccessful()) {
				if (this.getManagedBean().isNewCategory()){
					this.getManagedBean().setPmCategoryL2(resDTO.getCategoryL2());
					this.getManagedBean().getPmCategoryL1().getPmCategoryL2s().add(this.getManagedBean().getPmCategoryL2());
					reqDTO.setPmCategoryL1(this.getManagedBean().getPmCategoryL1());
					categoryCodeSO.saveCategory(reqDTO);
				}
				
				//
				
				
				result = resDTO.isSuccessful();
				resetCategory();
				FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
						MessageUtils.getMessage(MessageConst.INFO_700004,
							new String[]{"",ResourceBundleUtils.getMessage("lbl.pm.sub.category",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())},
							MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			categoryCodeSO = null;
		}

		if (this.getManagedBean().isNewCategory()){
			//return editCategory(this.getManagedBean().getPmCategoryL2().getPmCategoryL1().getCategoryL1Id());
			this.getManagedBean().setNewCategory(false);
			//return "/xhtml/pm/category/categoryl1.xhtml"; 
			return "/xhtml/pm/category/search.xhtml";
		}else
			//preRenderSearch();
			return "/xhtml/pm/category/search.xhtml";
		
	}
	
	private void preRenderSearch() {
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, "/xhtml/pm/category/search.xhtml");
	}

	public String saveSubDivision() {
		ICategoryCodeSO categoryCodeSO;
		IParameterSO parameterSO;
		try{
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			categoryCodeSO = (ICategoryCodeSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICategoryCodeSO.class);
			parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IParameterSO.class);

			this.getManagedBean().getPmCategoryL3().setCategoryName(this.getManagedBean().getPmCategoryL3().getCategoryName().toUpperCase());
			this.getManagedBean().getPmCategoryL3().setBasicCategoryReq(this.getManagedBean().getPmCategoryL3().isBasicCategoryReqValue()?1:0);
			this.getManagedBean().getPmCategoryL3().setHomeOperated(this.getManagedBean().getPmCategoryL3().isHomeOperatedValue()?1:0);
			
			if (this.getManagedBean().isNewCategory()){
				ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
				//supplier type
				parameterReqDTO.setParameterId(this.getManagedBean().getPmCategoryL3().getSupplierTypeCodeVal());
				ParameterResDTO parameterResDTO = parameterSO.retrieveParameterById(parameterReqDTO);
				this.getManagedBean().getPmCategoryL3().setSupplierTypeCode(parameterResDTO.getPmParameter());
				
				//Special Category
				parameterReqDTO.setParameterId(this.getManagedBean().getPmCategoryL3().getCategoryTypeCodeVal());
				parameterResDTO = parameterSO.retrieveParameterById(parameterReqDTO);
				this.getManagedBean().getPmCategoryL3().setCategoryTypeCode(parameterResDTO.getPmParameter());
			}
			
			if (!validatePmCategoryL3()){
				return "/xhtml/pm/category/categoryl3.xhtml"; 
			}
			
			//if not category restc, remove exisitng restr
			if(!this.getManagedBean().getPmCategoryL3().isByCompType() && this.getManagedBean().getPmCategoryL3().getPmCategoryRestrs() != null){
				log.info("*******NOT COMPANY RESTRICT*********");
				if(this.getManagedBean().getPmCategoryL3().getPmCategoryRestrs() != null ){
					if(this.getManagedBean().getPmCategoryL3().getPmCategoryRestrs().size() > 0){
						this.getManagedBean().getPmCategoryL3().getPmCategoryRestrs().clear();
					}
				}
			}
			
			//check the duplicate restriction
			if(!checkCategoryRestrDuplicateCompanyType()){
				throw new EpException(MessageConst.ERROR_775006);
			}
			
			//remove basic category if any
			log.info("this.getManagedBean().getSelectedBasicCategory().size() = "+this.getManagedBean().getSelectedBasicCategory().size());
			if(this.getManagedBean().getSelectedBasicCategory() != null && !this.getManagedBean().getSelectedBasicCategory().isEmpty()) {
				CategoryCodeReqDTO categoryCodeReqDTO = new CategoryCodeReqDTO();
				for(PmBasicCategory pmBasicCategory: this.getManagedBean().getSelectedBasicCategory()) {
					
					categoryCodeReqDTO.setPmBasicCategory(pmBasicCategory);
					categoryCodeSO.deleteBasicCategory(categoryCodeReqDTO);
					
					//this.getManagedBean().getPmCategoryL3().getPmBasicCategory().remove(pmBasicCategory);
					
					//reqDTO.setPmCategoryL3(this.getManagedBean().getPmCategoryL3());
					//categoryCodeSO.saveSubDivision(reqDTO);
				}
			}
			
			//remove machine category if any
			log.info("this.getManagedBean().getDeletedpmMachineCategory().size() = "+this.getManagedBean().getDeletedpmMachineCategory().size());
			if(this.getManagedBean().getDeletedpmMachineCategory() != null && !this.getManagedBean().getDeletedpmMachineCategory().isEmpty()) {
				CategoryCodeReqDTO categoryCodeReqDTO = new CategoryCodeReqDTO();
				for(PmMachineCategory pmMachineCategory: this.getManagedBean().getDeletedpmMachineCategory()) {
					
					categoryCodeReqDTO.setPmMachineCategoryId(pmMachineCategory.getMachineCategoryId());
					categoryCodeSO.deleteMachineCategory(categoryCodeReqDTO);
				}
			}
			
			reqDTO.setPmCategoryL3(this.getManagedBean().getPmCategoryL3());
			reqDTO.setNewCategory(this.getManagedBean().isNewCategory());
			CategoryCodeResDTO resDTO = categoryCodeSO.saveSubDivision(reqDTO);

			if (resDTO.isSuccessful()) {
				
				if (this.getManagedBean().isNewCategory()){
					this.getManagedBean().setPmCategoryL3(resDTO.getCategoryL3());
					this.getManagedBean().getPmCategoryL2().getPmCategoryL3s().add(this.getManagedBean().getPmCategoryL3());
					reqDTO.setPmCategoryL2(this.getManagedBean().getPmCategoryL2());
					categoryCodeSO.saveSubCategory(reqDTO);
				}
				
				//reqDTO.setPmCategoryL1(this.getManagedBean().getPmCategoryL1());
				//categoryCodeSO.saveCategory(reqDTO);
				
				result = resDTO.isSuccessful();
				//resetCategory();
				FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
						MessageUtils.getMessage(MessageConst.INFO_700004,
							new String[]{"",ResourceBundleUtils.getMessage("lbl.pm.sub.div",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())},
							MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
			}
		
		}finally {
			categoryCodeSO = null;
			parameterSO = null;
		}

		if (this.getManagedBean().isNewCategory()){
			//return editSubCategory(this.getManagedBean().getPmCategoryL3().getPmCategoryL2().getCategoryL2Id());
			this.getManagedBean().setNewCategory(false);
			//return "/xhtml/pm/category/categoryl2.xhtml";
			return "/xhtml/pm/category/search.xhtml";
		}else
			return "/xhtml/pm/category/search.xhtml";
	}
	
	public void deleteBasicCategory() {
		log.info("In deleteBasicCategory");
		//ICategoryCodeSO categoryCodeSO = (ICategoryCodeSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICategoryCodeSO.class);
		
		if(this.getManagedBean().getSelectedBasicCategory() != null && !this.getManagedBean().getSelectedBasicCategory().isEmpty()) {
			log.info("before this.getManagedBean().getSelectedBasicCategory().size() = "+this.getManagedBean().getSelectedBasicCategory().size());
			//CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			for(PmBasicCategory pmBasicCategory: this.getManagedBean().getSelectedBasicCategory()) {
				
				//reqDTO.setPmBasicCategory(pmBasicCategory);
				//categoryCodeSO.deleteBasicCategory(reqDTO);
				
				this.getManagedBean().getPmCategoryL3().getPmBasicCategory().remove(pmBasicCategory);
				
				//reqDTO.setPmCategoryL3(this.getManagedBean().getPmCategoryL3());
				//categoryCodeSO.saveSubDivision(reqDTO);
			}
		}
		//log.info("deleteBasicCategory this.getManagedBean().getSelectedBasicCategory().size() = "+this.getManagedBean().getSelectedBasicCategory().size());
	}
	
	public void deletePmMachine() {
		log.info("In deletePmMachine");
		//ICategoryCodeSO categoryCodeSO = (ICategoryCodeSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICategoryCodeSO.class);
		
		if(this.getManagedBean().getSelectedpmMachineCategory() != null && !this.getManagedBean().getSelectedpmMachineCategory().isEmpty()) {
			log.info("before this.getManagedBean().getSelectedpmMachineCategory().size() = "+this.getManagedBean().getSelectedpmMachineCategory().size());
			//CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			for(PmMachineCategory pmMachineCategory: this.getManagedBean().getSelectedpmMachineCategory()) {
				
				//reqDTO.setPmBasicCategory(pmBasicCategory);
				//categoryCodeSO.deleteBasicCategory(reqDTO);
				
				this.getManagedBean().getPmCategoryL3().getPmMachineCategory().remove(pmMachineCategory);
				this.getManagedBean().getDeletedpmMachineCategory().add(pmMachineCategory);
				
				//reqDTO.setPmCategoryL3(this.getManagedBean().getPmCategoryL3());
				//categoryCodeSO.saveSubDivision(reqDTO);
			}
		}
		log.info("deletePmMachine this.getManagedBean().getSelectedpmMachineCategory().size() = "+this.getManagedBean().getSelectedpmMachineCategory().size());
	}
	
	public boolean validatePmCategoryL1(){
		ICategoryCodeSO categoryCodeSO = (ICategoryCodeSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICategoryCodeSO.class);
		try{
			if(this.getManagedBean().getPmCategoryL1().getCategoryL1Code()==null || this.getManagedBean().getPmCategoryL1().getCategoryL1Code().isEmpty()){
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
								MessageUtils.getMessage(
										MessageConst.ERROR_715008,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				return false;
			}
			if(this.getManagedBean().getPmCategoryL1().getCategoryName()==null || this.getManagedBean().getPmCategoryL1().getCategoryName().isEmpty()){
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
								MessageUtils.getMessage(
										MessageConst.ERROR_715008,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				return false;
			}
			if(this.getManagedBean().getPmCategoryL1().getCategoryL1Code()!=null){
				try{
					Integer.valueOf(this.getManagedBean().getPmCategoryL1().getCategoryL1Code());
				}catch(NumberFormatException e){
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									MessageUtils.getMessage(
											MessageConst.ERROR_100004,
											new String[]{ResourceBundleUtils.getMessage("lbl.pm.category.code",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())},
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
					return false;
				}
				
			}
			if(this.getManagedBean().isNewCategory()){
				CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
				reqDTO.setMainCatCode(this.getManagedBean().getPmCategoryL1().getCategoryL1Code());
				CategoryCodeResDTO resDTO = categoryCodeSO.retrieveCategoryL1ByCode(reqDTO);
				if(resDTO.getCategoryL1()!=null){
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									MessageUtils.getMessage(
											MessageConst.ERROR_775001,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
					return false;
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			categoryCodeSO = null;
		}
		return true;
	}
	
	public boolean validatePmCategoryL2(){
		ICategoryCodeSO categoryCodeSO = (ICategoryCodeSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICategoryCodeSO.class);
		try{
			if(this.getManagedBean().getPmCategoryL2().getCategoryL2Code()==null || this.getManagedBean().getPmCategoryL2().getCategoryL2Code().isEmpty()){
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
								MessageUtils.getMessage(
										MessageConst.ERROR_715008,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				return false;
			}
			if(this.getManagedBean().getPmCategoryL2().getCategoryName()==null || this.getManagedBean().getPmCategoryL2().getCategoryName().isEmpty()){
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
								MessageUtils.getMessage(
										MessageConst.ERROR_715008,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				return false;
			}
			if(this.getManagedBean().getPmCategoryL2().getCategoryL2Code()!=null){
				try{
					Integer.valueOf(this.getManagedBean().getPmCategoryL2().getCategoryL2Code());
				}catch(NumberFormatException e){
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									MessageUtils.getMessage(
											MessageConst.ERROR_100004,
											new String[]{ResourceBundleUtils.getMessage("lbl.pm.category.code",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())},
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
					return false;
				}
				
			}
			if(this.getManagedBean().isNewCategory()){
				CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
				reqDTO.setCategoryL1Id(this.getManagedBean().getPmCategoryL1().getCategoryL1Id());
				reqDTO.setSubCatCode(this.getManagedBean().getPmCategoryL2().getCategoryL2Code());
				CategoryCodeResDTO resDTO = categoryCodeSO.retrieveCategoryL2ListByCode(reqDTO);
				if(resDTO.getCategoryL2List()!=null && resDTO.getCategoryL2List().size()>0){
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									MessageUtils.getMessage(
											MessageConst.ERROR_775002,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
					return false;
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			categoryCodeSO = null;
		}
		return true;
	}

	public boolean validatePmCategoryL3(){
		ICategoryCodeSO categoryCodeSO = (ICategoryCodeSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICategoryCodeSO.class);
		try{
			if(this.getManagedBean().getPmCategoryL3().getCategoryL3Code()==null || this.getManagedBean().getPmCategoryL3().getCategoryL3Code().isEmpty()){
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
								MessageUtils.getMessage(
										MessageConst.ERROR_715008,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				return false;
			}
			if(this.getManagedBean().getPmCategoryL3().getCategoryName()==null || this.getManagedBean().getPmCategoryL3().getCategoryName().isEmpty()){
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
								MessageUtils.getMessage(
										MessageConst.ERROR_715008,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				return false;
			}
			if(this.getManagedBean().isNewCategory() && this.getManagedBean().getPmCategoryL3().getSupplierTypeCodeVal()==0){
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
								MessageUtils.getMessage(
										MessageConst.ERROR_715008,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				return false;
			}
			if(!this.getManagedBean().isNewCategory() && (this.getManagedBean().getPmCategoryL3().getSupplierTypeCode()==null || this.getManagedBean().getPmCategoryL3().getSupplierTypeCode().getParameterId()==0)){
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
								MessageUtils.getMessage(
										MessageConst.ERROR_715008,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				return false;
			}
			if(this.getManagedBean().isNewCategory() && this.getManagedBean().getPmCategoryL3().getCategoryTypeCodeVal()==0){
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
								MessageUtils.getMessage(
										MessageConst.ERROR_715008,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				return false;
			}
			if(!this.getManagedBean().isNewCategory() && (this.getManagedBean().getPmCategoryL3().getCategoryTypeCode()==null || this.getManagedBean().getPmCategoryL3().getCategoryTypeCode().getParameterId()==0)){
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									MessageUtils.getMessage(
											MessageConst.ERROR_715008,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
					return false;
			}
			if(this.getManagedBean().getPmCategoryL3().getPaidUpCapital()==null){
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
								MessageUtils.getMessage(
										MessageConst.ERROR_715008,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				return false;
			}
			
			//disabled the bumipercentage: by andrew
/*			if(this.getManagedBean().getPmCategoryL3().getBumiPercentage()==null || this.getManagedBean().getPmCategoryL3().getBumiPercentage()==0){
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
								MessageUtils.getMessage(
										MessageConst.ERROR_715008,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				return false;
			}*/
			if(this.getManagedBean().getPmCategoryL3().isBasicCategoryReqValue() && (this.getManagedBean().getPmCategoryL3().getPmBasicCategory()==null || this.getManagedBean().getPmCategoryL3().getPmBasicCategory().size()==0)){
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
								MessageUtils.getMessage(
										MessageConst.ERROR_775004,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				return false;
			//last
			}
			if(this.getManagedBean().getPmCategoryL3().getCategoryL3Code()!=null){
				try{
					Integer.valueOf(this.getManagedBean().getPmCategoryL3().getCategoryL3Code());
				}catch(NumberFormatException e){
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									MessageUtils.getMessage(
											MessageConst.ERROR_100004,
											new String[]{ResourceBundleUtils.getMessage("lbl.pm.category.code",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())},
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
					return false;
				}
				
			}
			/*if(this.getManagedBean().getPmCategoryL3().getPaidUpCapital()!=null){
				try{
					Integer.valueOf(this.getManagedBean().getPmCategoryL3().getPaidUpCapital());
				}catch(NumberFormatException e){
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									MessageUtils.getMessage(
											MessageConst.ERROR_100004,
											new String[]{ResourceBundleUtils.getMessage("lbl.pm.paid.capital",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())},
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
					return false;
				}
			}*/
			if(this.getManagedBean().isNewCategory()){
				CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
				reqDTO.setCategoryL2Id(this.getManagedBean().getPmCategoryL3().getPmCategoryL2().getCategoryL2Id());
				reqDTO.setSubDivCode(this.getManagedBean().getPmCategoryL3().getCategoryL3Code());
				CategoryCodeResDTO resDTO = categoryCodeSO.retrieveCategoryL3ListByCode(reqDTO);
				if(resDTO.getCategoryL3List()!=null && resDTO.getCategoryL3List().size()>0){
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									MessageUtils.getMessage(
											MessageConst.ERROR_775003,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
					return false;
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			categoryCodeSO = null;
		}
		return true;
	}
	
	/*
	public PmCategoryL3 getPmCategoryL3(Long id){
		ICategoryCodeSO categoryCodeSO;
		try{
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			categoryCodeSO = (ICategoryCodeSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICategoryCodeSO.class);

			reqDTO.setCategoryL3Id(id);
			CategoryCodeResDTO resDTO = categoryCodeSO.retrieveCategoryL3DtlById(reqDTO);

			if (resDTO.isSuccessful()) {
				return resDTO.getCategoryL3();
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			categoryCodeSO = null;
		}
		
		return null;
	}
	*/
	
	public void populateToSaveLOV() {
		log.info("In populateToSaveLOV");
		//ICategoryCodeSO categoryCodeSO = (ICategoryCodeSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICategoryCodeSO.class);
		
		if(this.getManagedBean().getSelectedLOV() != null && !this.getManagedBean().getSelectedLOV().isEmpty()) {
			//CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			if(this.getManagedBean().getPmCategoryL3().getPmBasicCategory()==null){
				//if(this.getManagedBean().getPmCategoryL3().getPmBasicCategory().size()==0){
					this.getManagedBean().getPmCategoryL3().setPmBasicCategory(new ArrayList<PmBasicCategory>());
				//}
			}
			
			for(PmCategoryL3 pmCategoryL3: this.getManagedBean().getSelectedLOV()) {
				
				PmBasicCategory pmBasicCategory = new PmBasicCategory();
				pmBasicCategory.setPmCategoryL3(this.getManagedBean().getPmCategoryL3());
				pmBasicCategory.setBasicCategoryL3(pmCategoryL3.getCategoryL3Id());
				pmBasicCategory.setBasicCategoryL2(pmCategoryL3.getPmCategoryL2().getCategoryL2Id());
				pmBasicCategory.setBasicCategoryL1(pmCategoryL3.getPmCategoryL2().getPmCategoryL1().getCategoryL1Id());
				pmBasicCategory.setCreatedDate(new Timestamp( new Date().getTime()));
				
				//for table display
				pmBasicCategory.setBasicCategoryL1desc(pmCategoryL3.getPmCategoryL2().getPmCategoryL1().getCategoryName());
				pmBasicCategory.setBasicCategoryL2desc(pmCategoryL3.getPmCategoryL2().getCategoryName());
				pmBasicCategory.setBasicCategoryL3desc(pmCategoryL3.getCategoryName());
				pmBasicCategory.setCodeConcatenate(
						pmCategoryL3.getPmCategoryL2().getPmCategoryL1().getCategoryL1Code()+
						pmCategoryL3.getPmCategoryL2().getCategoryL2Code()+
						pmCategoryL3.getCategoryL3Code());
				
				this.getManagedBean().getPmCategoryL3().getPmBasicCategory().add(pmBasicCategory);
				//reqDTO.setPmBasicCategory(pmBasicCategory);
				//CategoryCodeResDTO resDTO = categoryCodeSO.saveBasicCategory(reqDTO);
				
				//this.getManagedBean().getPmCategoryL3().getPmBasicCategory().add(resDTO.getPmBasicCategory());
				
				//reqDTO.setPmCategoryL3(this.getManagedBean().getPmCategoryL3());
				//categoryCodeSO.saveSubDivision(reqDTO);
			}
		}
		resetLOV();
		
	}
	
	public void populateToSavePmMachineLOV() {
		log.info("In populateToSavePmMachineLOV");
		//ICategoryCodeSO categoryCodeSO = (ICategoryCodeSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICategoryCodeSO.class);
		
		if(this.getManagedBean().getSelectedpmMachine() != null && !this.getManagedBean().getSelectedpmMachine().isEmpty()) {
			//CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			if(this.getManagedBean().getPmCategoryL3().getPmMachineCategory()==null){
				//if(this.getManagedBean().getPmCategoryL3().getPmBasicCategory().size()==0){
					this.getManagedBean().getPmCategoryL3().setPmMachineCategory(new ArrayList<PmMachineCategory>());
				//}
			}
			
			for(PmMachine pmMachine: this.getManagedBean().getSelectedpmMachine()) {
				
				PmMachineCategory pmMachineCategory = new PmMachineCategory();
				pmMachineCategory.setPmCategoryL3(this.getManagedBean().getPmCategoryL3());
				
				//parameter
				//CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
				//reqDTO.setPmMachineId(pmMachine.getMachineId());
				//CategoryCodeResDTO resDTO = categoryCodeSO.retrievePmMachine(reqDTO);
				pmMachineCategory.setCategoryTypeCode(pmMachine.getCategoryTypeCode());
				
				//log.info("resDTO.getPmMachine().getCategoryTypeCode().getParameterId() "+resDTO.getPmMachine().getCategoryTypeCode().getParameterId());
				
				pmMachineCategory.setCreatedDate(new Timestamp( new Date().getTime()));
				pmMachineCategory.setCategoryL1id(this.getManagedBean().getPmCategoryL3().getPmCategoryL2().getPmCategoryL1().getCategoryL1Id());
				pmMachineCategory.setCategoryL2id(this.getManagedBean().getPmCategoryL3().getPmCategoryL2().getCategoryL2Id());
				pmMachineCategory.setPmMachine(pmMachine);
				
				this.getManagedBean().getPmCategoryL3().getPmMachineCategory().add(pmMachineCategory);
				
				/*PmBasicCategory pmBasicCategory = new PmBasicCategory();
				pmBasicCategory.setPmCategoryL3(this.getManagedBean().getPmCategoryL3());
				pmBasicCategory.setBasicCategoryL3(pmMachine.getCategoryL3Id());
				pmBasicCategory.setBasicCategoryL2(pmMachine.getPmCategoryL2().getCategoryL2Id());
				pmBasicCategory.setBasicCategoryL1(pmMachine.getPmCategoryL2().getPmCategoryL1().getCategoryL1Id());
				pmBasicCategory.setCreatedDate(new Timestamp( new Date().getTime()));
				
				//for table display
				pmBasicCategory.setBasicCategoryL1desc(pmMachine.getPmCategoryL2().getPmCategoryL1().getCategoryName());
				pmBasicCategory.setBasicCategoryL2desc(pmMachine.getPmCategoryL2().getCategoryName());
				pmBasicCategory.setBasicCategoryL3desc(pmMachine.getCategoryName());
				pmBasicCategory.setCodeConcatenate(
						pmMachine.getPmCategoryL2().getPmCategoryL1().getCategoryL1Code()+
						pmMachine.getPmCategoryL2().getCategoryL2Code()+
						pmMachine.getCategoryL3Code());
				
				this.getManagedBean().getPmCategoryL3().getPmBasicCategory().add(pmBasicCategory);*/
			}
		}
		resetPmMachineLOV();
		
	}
	
	public void reloadSelectionPmCategoryL3() {
		IParameterSO parameterSO;
		try{
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IParameterSO.class);
			reqDTO.setLanguageCode(new FacesScopes().getUserLocale().getLanguage());
			
			reqDTO.setParameterType("ST");
			ParameterResDTO resDTO = parameterSO.retrieveParameterDescListByType(reqDTO);
			this.getManagedBean().setSupplierType(resDTO.getParameterDescList());
			
			reqDTO.setParameterType("CAT");
			resDTO = parameterSO.retrieveParameterDescListByType(reqDTO);
			this.getManagedBean().setSpecialCategory(resDTO.getParameterDescList());
			
			//default value for Special Category
			if(this.getManagedBean().isNewCategory()){
				reqDTO.setParameterType("CAT");
				reqDTO.setParameterCode("-");
				resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
				this.getManagedBean().getPmCategoryL3().setCategoryTypeCodeVal(resDTO.getPmParameter().getParameterId());
			}
			
			//default value for Special Group
			if(this.getManagedBean().getPmCategoryL3().isBasicCategoryReqValue()){
				this.getManagedBean().setDisplayCategoryGrouping(true);
			}else{
				this.getManagedBean().setDisplayCategoryGrouping(false);
			}
			
			this.getManagedBean().setSelectedBasicCategory(new ArrayList<PmBasicCategory>() );
			this.getManagedBean().setSelectedpmMachineCategory(new ArrayList<PmMachineCategory>() );
			this.getManagedBean().setDeletedpmMachineCategory(new ArrayList<PmMachineCategory>() );
			this.getManagedBean().setSelectedPmCategoryRestr(new ArrayList<PmCategoryRestr>());
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			parameterSO = null;
		}
	}
	
	public void resetCategory() {
		this.getManagedBean().setMainCatCode(null);
		this.getManagedBean().setMainCatDesc(null);
		this.getManagedBean().setSubCatCode(null);
		this.getManagedBean().setSubCatDesc(null);
		this.getManagedBean().setSubDivCode(null);
		this.getManagedBean().setSubDivDesc(null);
		this.getManagedBean().setPmCategoryLevel(0);
		searchCategory.reloadData();
	}
	
	public void resetLOV() {
		getManagedBean().setSearchValue(new String[3]);
		getManagedBean().setSearchFieldValue(new String[3]);
		getManagedBean().setSearchOperatorValue(new String[3]);
		this.getSearchLOVPagingDataModel().resetData();
		this.getManagedBean().setSelectedLOV(new ArrayList<PmCategoryL3>());
	}
	
	public void resetPmMachineLOV() {
		getManagedBean().setSearchValue(new String[3]);
		getManagedBean().setSearchFieldValue(new String[3]);
		getManagedBean().setSearchOperatorValue(new String[3]);
		this.getSearchLOVMachinePagingDataModel().resetData();
		this.getManagedBean().setSelectedpmMachine(new ArrayList<PmMachine>());
	}
	
	public void updateCategoryLevel(){
		log.info("getManagedBean().getPmCategoryLevel() "+getManagedBean().getPmCategoryLevel());
	}
	
	public void updateSpecialGroup(){
		log.info("getManagedBean().updateSpecialGroup() ");
		if(this.getManagedBean().getPmCategoryL3().isBasicCategoryReqValue()){
			this.getManagedBean().setDisplayCategoryGrouping(true);
		}else{
			this.getManagedBean().setDisplayCategoryGrouping(false);
		}
			
	}
	
	public void updateMachine(){
		if(this.getManagedBean().getPmMachine()!=null){
			log.info("this.getManagedBean().getPmMachine().size() = "+this.getManagedBean().getPmMachine().size());
		
			ICategoryCodeSO categoryCodeSO = (ICategoryCodeSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICategoryCodeSO.class);
			
			for(PmMachine pmMachine : this.getManagedBean().getPmMachine()){
				PmMachineCategory pmMachineCategory = new PmMachineCategory();
				pmMachineCategory.setPmCategoryL3(this.getManagedBean().getPmCategoryL3());
				
				//parameter
				CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
				reqDTO.setPmMachineId(pmMachine.getMachineId());
				CategoryCodeResDTO resDTO = categoryCodeSO.retrievePmMachine(reqDTO);
				pmMachineCategory.setCategoryTypeCode(resDTO.getPmMachine().getCategoryTypeCode());
				
				//log.info("resDTO.getPmMachine().getCategoryTypeCode().getParameterId() "+resDTO.getPmMachine().getCategoryTypeCode().getParameterId());
				
				pmMachineCategory.setCreatedDate(new Timestamp( new Date().getTime()));
				pmMachineCategory.setCategoryL1id(this.getManagedBean().getPmCategoryL3().getPmCategoryL2().getPmCategoryL1().getCategoryL1Id());
				pmMachineCategory.setCategoryL2id(this.getManagedBean().getPmCategoryL3().getPmCategoryL2().getCategoryL2Id());
				pmMachineCategory.setPmMachine(resDTO.getPmMachine());
				
				this.getManagedBean().getPmCategoryL3().getPmMachineCategory().add(pmMachineCategory);
				
				log.info("this.getManagedBean().getPmCategoryL3().getPmMachineCategory().size() "+this.getManagedBean().getPmCategoryL3().getPmMachineCategory().size());
			}
		}
	}
	
	public void search() {
		searchCategory.reloadData();
	}
	
	public void searchLOV() {
		searchLOVPagingDataModel.reloadData();
	}
	
	public void searchPmMachineLOV() {
		searchLOVMachinePagingDataModel.reloadData();
	}
	
	public String backToSearch() {
		//resetCategory();
		return "/xhtml/pm/category/search.xhtml?faces-redirect=true";
	}

	public IPagingDataModel<Object[]> getSearchCategory() {
		return searchCategory;
	}

	public void setSearchCategoryPagingDataModel(
			IPagingDataModel<Object[]> searchCategory) {
		this.searchCategory = searchCategory;
	}
	
	public IPagingDataModel<PmCategoryL3> getSearchLOVPagingDataModel() {
		return searchLOVPagingDataModel;
	}

	public void setSearchLOVPagingDataModel(
			IPagingDataModel<PmCategoryL3> searchLOVPagingDataModel) {
		this.searchLOVPagingDataModel = searchLOVPagingDataModel;
	}
	
	
	
	
	
	
	
	


	
	

	

	public void reset() {
		
	}
	



	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}


	/**
	 * @return the flag
	 */
	public boolean isFlag() {
		return flag;
	}

	/**
	 * @param flag
	 *            the flag to set
	 */
	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public IPagingDataModel<PmMachine> getSearchLOVMachinePagingDataModel() {
		return searchLOVMachinePagingDataModel;
	}

	public void setSearchLOVMachinePagingDataModel(
			IPagingDataModel<PmMachine> searchLOVMachinePagingDataModel) {
		this.searchLOVMachinePagingDataModel = searchLOVMachinePagingDataModel;
	}
	
	public void addCategoryRestr(){
		if(this.getManagedBean().getPmCategoryL3().getPmCategoryRestrs() == null){
			this.getManagedBean().getPmCategoryL3().setPmCategoryRestrs(new ArrayList<PmCategoryRestr>());
		}
		
		PmCategoryRestr restr = new PmCategoryRestr();
		restr.setPmCategoryL3(this.getManagedBean().getPmCategoryL3());
		restr.setCompanyTypeId(new PmParameter());
		
		this.getManagedBean().getPmCategoryL3().getPmCategoryRestrs().add(restr);
		
	}
	
	public void deleteCategoryRestr(){
		if(this.getManagedBean().getSelectedPmCategoryRestr().size() > 0){
			for(PmCategoryRestr restrc : this.getManagedBean().getSelectedPmCategoryRestr()){
				this.getManagedBean().getPmCategoryL3().getPmCategoryRestrs().remove(restrc);
			}
		}
	}

	public List<PmParameterDesc> getPopulateCompanyTypeList() {
		if(this.getManagedBean().getCompanyTypeList()==null || this.getManagedBean().getCompanyTypeList().size() < 1){
			//List<SelectItem> populateCompanyTypeList = new ArrayList<SelectItem>();
			IParameterSO reqSO = (IParameterSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IParameterSO.class);
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setLanguageCode(getFacesScopes().getUserLocale().getLanguage());
			reqDTO.setParameterType(ParameterTypeConst.SUPPLIER_BUSINESS_TYPE);
			ParameterResDTO resDTO = reqSO.retrieveParameterDescListByType(reqDTO);
			
			log.info("*****PopulateCompanyTypeList****" + resDTO.getParameterDescList().size());
			
			List<PmParameterDesc> parameterDesc = resDTO.getParameterDescList();

			this.getManagedBean().setCompanyTypeList(parameterDesc);
		}
		
		return this.getManagedBean().getCompanyTypeList();
	}
	
	public boolean checkCategoryRestrDuplicateCompanyType(){
		
		if(this.getManagedBean().getPmCategoryL3().getPmCategoryRestrs() != null && this.getManagedBean().getPmCategoryL3().getPmCategoryRestrs().size() > 0){
			List<String> companyTypeLs = new ArrayList<String>();
			for(PmCategoryRestr catRestr : this.getManagedBean().getPmCategoryL3().getPmCategoryRestrs()){
				String paramId = catRestr.getCompanyTypeId().getParameterId().toString();
				if(companyTypeLs.contains(paramId)){
					return false;
				}
				companyTypeLs.add(paramId);
			}
		}
		
		return true;
	}
}
