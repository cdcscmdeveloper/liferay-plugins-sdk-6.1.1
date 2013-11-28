package my.ep.web.backingbean.ss.quatationtender;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.common.categorys.ICategorysSO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.ss.quationtender.IQTDetailSO;
import my.ep.dto.ss.quationtender.QuationTenderReqDTO;
import my.ep.dto.ss.quationtender.QuationTenderResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.ss.quationtender.ScQtApproval;
import my.ep.persistence.entity.ss.quationtender.ScQtCheckList;
import my.ep.persistence.entity.ss.quationtender.ScQtDoc;
import my.ep.persistence.entity.tpl.doc.TplDocMaster;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.bean.ss.quatationtender.TabBean;
import my.ep.web.managedbean.ss.quationtender.QTDetailManagedBean;
import my.ep.web.ss.quationtender.common.QTConstants;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
/**
 * @author SudheerKumar
 *
 */
@ManagedBean(name="qTDocumentsBB")
@RequestScoped
public class QTDocumentsBackingBean extends SourcingBaseBackingBean<QTDetailManagedBean> {
	private static final Logger log=EpLogger.getLogger(QTDocumentsBackingBean.class);
	
	private static final String MODULE_NAME = "ss";
	private IQTDetailSO detailSO = null;
	
	public QTDocumentsBackingBean(){
		
		IQTDetailSO detailSO = BusinessDelegate.getInstance().getServiceObject(
				MODULE_NAME, IQTDetailSO.class);

		
		this.detailSO = detailSO;
		
		ScQtCheckList qtCheckList=new ScQtCheckList();
		qtCheckList.setScQtDoc(new ScQtDoc());
		this.getManagedBean().setQtCheckList(qtCheckList);
	}
		
	/*	ScQtDoc qtDoc=new ScQtDoc();
		qtDoc.setTplDocMaster(new TplDocMaster());
		this.getManagedBean().setQtDoc(qtDoc);
   	
	}
	public void addDocuments(){
	 List<ScQtDoc> documents=this.getManagedBean().getScQtDocs();
	 if(documents == null){
		 documents= new ArrayList<ScQtDoc>();
		 
	 }
	this.getManagedBean().setQtDoc(null);
	ScQtDoc qd=new ScQtDoc();
	
	this.getManagedBean().setQtDoc(qd);
	documents.add(this.getManagedBean().getQtDoc());
	this.getManagedBean().setScQtDocs(documents);
	
	 
	} */

		public void retrivedoc(){
		QuationTenderReqDTO tenderReqDTO= new QuationTenderReqDTO();
		tenderReqDTO.setQtDetailId(146l);
		QuationTenderResDTO dto=detailSO.retriveQTDocDetails(tenderReqDTO);
		this.getManagedBean().setScQtChecklists(dto.getDocuments());
		
	
	}
}
	



