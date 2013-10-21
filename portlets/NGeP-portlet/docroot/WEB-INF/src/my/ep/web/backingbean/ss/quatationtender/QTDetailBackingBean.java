package my.ep.web.backingbean.ss.quatationtender;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ValueChangeEvent;

import my.ep.biz.service.pm.common.categorys.ICategorysSO;
import my.ep.biz.service.pm.common.object.IObjectSO;
import my.ep.biz.service.ss.quationtender.IQTDetailSO;
import my.ep.dto.pm.common.categorys.CategorysReqDTO;
import my.ep.dto.pm.common.categorys.CategorysResDTO;
import my.ep.dto.pm.common.object.ObjectReqDTO;
import my.ep.dto.pm.common.object.ObjectResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmDo;
import my.ep.persistence.entity.pm.common.PmGo;
import my.ep.persistence.entity.pm.common.PmSo;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.ss.quationtender.QTDetailManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * @Author VidyasagarReddy
 */

@ManagedBean(name = "qTDetailBB")
@RequestScoped
public class QTDetailBackingBean extends BaseBackingBean<QTDetailManagedBean> {

	private static final Logger log = EpLogger
			.getLogger(QTDetailBackingBean.class);

	private static final String MODULE_NAME = "ss";
	private IQTDetailSO detailSO = null;
	private IObjectSO objectSO = null;
	private ICategorysSO categorysSO = null;

	public QTDetailBackingBean() {

		log.info("inside constructor");

	}

	/**
	 * Creating the SO after bean object creation. instead of calling from
	 * constructor
	 */
	@PostConstruct
	public void initializeObejcts() {
		IQTDetailSO detailSO = BusinessDelegate.getInstance().getServiceObject(
				MODULE_NAME, IQTDetailSO.class);
		IObjectSO objectSO = BusinessDelegate.getInstance().getServiceObject(
				"pm", IObjectSO.class);
		ICategorysSO categorysSO = BusinessDelegate.getInstance()
				.getServiceObject("pm", ICategorysSO.class);
		this.detailSO = detailSO;
		this.objectSO = objectSO;
		this.categorysSO = categorysSO;
		log.info("successfully look up the so object" + detailSO);
	}

	/*
	 * Method to load for Qt details screen information
	 */
	public void retriveQtDetails() {
		List<PmGo> goList = this.getManagedBean().getGoList();
		// Make a SO call only once to get the data if it is empty other wise
		// skip the so call
		if (goList == null || goList.isEmpty()) {
			ObjectResDTO resDTO = objectSO.retrieveGOList();
			this.getManagedBean().setGoList(resDTO.getPmGoList());
		}
	}

	/*
	 * Method to load the categorieslist for suppliers screen
	 */
	public void retriveQtCriteria() {
		List<PmCategoryL1> l1categoryList = this.getManagedBean()
				.getCategoryL1s();
		if (l1categoryList != null && !l1categoryList.isEmpty()) {
			CategorysResDTO catResDTO = categorysSO.retiveCategoryL1List();
			if (catResDTO.getCategoryL1List() != null
					&& !catResDTO.getCategoryL1List().isEmpty()) {
				this.getManagedBean().setCategoryL1s(
						catResDTO.getCategoryL1List());
			}
		}
	}

	/*
	 * To add empty row to gosodo table
	 */
	public void addNewGoSoDoItem() {
		log.info("begin add gosodo item method");

		log.info("End of add gosodo item method");
	}

	/*
	 * Onchange of gos to fetch the so's list
	 */
	public void handleGoChange(ValueChangeEvent changeEvent) {
		log.info("inside handle go change method");
		PmGo pmGo = (PmGo) changeEvent.getNewValue();
		if (pmGo != null && pmGo.getGoId() != null) {
			// get the so list and set it to managed bean property
			ObjectReqDTO reqDTO = new ObjectReqDTO();
			reqDTO.setGoId(pmGo.getGoId());
			List<PmSo> solist = objectSO.retrieveSOListByGOCode(pmGo.getGoId());
			List<PmSo> oldsolist = this.getManagedBean().getSoList();
			oldsolist.clear();
			this.getManagedBean().setSoList(solist);
		}
		log.info("end of handle go change method");
	}

	/*
	 * OnChange of So's get the do's list
	 */
	public void handleSoChange(ValueChangeEvent changeEvent) {
		log.info("inside handle so change method");
		PmSo pmSo = (PmSo) changeEvent.getNewValue();
		if (pmSo != null && pmSo.getSoId() != null) {
			// get the do list and set it to managed bean property
			List<PmDo> dolist = objectSO.retrieveDOListBySOCode(pmSo.getSoId());
			List<PmDo> olddolist = this.getManagedBean().getDoList();
			olddolist.clear();
			this.getManagedBean().setDoList(dolist);
		}
		log.info("end  of handle go change method");
	}

	/*
	 * Method to call onchange of L1category
	 */
	public void handleL2CategoryChange(ValueChangeEvent changeEvent) {
		PmCategoryL1 categoryL1 = (PmCategoryL1) changeEvent.getNewValue();
		if (null != categoryL1 && categoryL1.getCategoryL1Id() != null) {
			CategorysReqDTO reqDTO = new CategorysReqDTO();
			reqDTO.setL1CategoryId(categoryL1.getCategoryL1Id());
			CategorysResDTO categorysResDTO = categorysSO
					.retiveCategoryL2ListByL1CategoryId(reqDTO);
			List<PmCategoryL2> catL2List = categorysResDTO.getCategoryL2List();
			this.getManagedBean().getCategoryL2s().clear();
			this.getManagedBean().setCategoryL2s(catL2List);
		}
	}
	/*
	 * Method to call onchange of L2category
	 */
	public void handleL3CategoryChange(ValueChangeEvent changeEvent) {

		PmCategoryL2 categoryL2 = (PmCategoryL2) changeEvent.getNewValue();
		if (null != categoryL2 && categoryL2.getCategoryL2Id() != null) {
			CategorysReqDTO reqDTO = new CategorysReqDTO();
			reqDTO.setL2CategoryId(categoryL2.getCategoryL2Id());
			CategorysResDTO categorysResDTO = categorysSO
					.retiveCategoryL3ListByL2CategoryId(reqDTO);
			List<PmCategoryL3> catL3List = categorysResDTO.getCategoryL3List();
			this.getManagedBean().getCategoryL3s().clear();
			this.getManagedBean().setCategoryL3s(catL3List);
		}
	}
}
