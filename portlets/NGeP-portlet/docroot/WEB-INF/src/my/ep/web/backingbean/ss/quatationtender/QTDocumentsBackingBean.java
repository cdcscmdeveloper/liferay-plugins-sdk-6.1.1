package my.ep.web.backingbean.ss.quatationtender;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.service.ss.quationtender.IQTDetailSO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.ss.quationtender.ScQtCheckList;
import my.ep.persistence.entity.ss.quationtender.ScQtDoc;
import my.ep.web.managedbean.ss.quationtender.QTDetailManagedBean;
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
}
	



