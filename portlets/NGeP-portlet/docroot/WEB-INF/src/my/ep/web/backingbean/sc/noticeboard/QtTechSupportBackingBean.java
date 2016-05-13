package my.ep.web.backingbean.sc.noticeboard;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.service.sc.quationtender.IQTNoticeBoardSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sc.quationtender.NoticeBoardReqDTO;
import my.ep.factory.EpLogger;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sc.noticeboard.QtTechSupportManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@ManagedBean(name = "QtTechSupportBB")
@RequestScoped
public class QtTechSupportBackingBean extends
		BaseBackingBean<QtTechSupportManagedBean> {

	public static Logger log = EpLogger
			.getLogger(QtTechSupportBackingBean.class);

	public void preRenderSchedulerManager() {
		log.info("inside prerender ");
		renewSesionObject();
		retrieveFailedQts();
	}

	private void retrieveFailedQts(){
		IQTNoticeBoardSO noticeBoardSO;
		try {
			// Publish QT's scheduled for today and by this hour
			noticeBoardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTNoticeBoardSO.class);
			NoticeBoardReqDTO reqDTO = new NoticeBoardReqDTO();
		}
		finally{
			
		}
	}

}
