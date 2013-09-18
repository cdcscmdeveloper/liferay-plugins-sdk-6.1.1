package my.ep.web.bean.ss.quatationtender;

import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;


public class QtnTenderDataBean extends ListDataModel<QtnTenderBean> implements SelectableDataModel<QtnTenderBean>{

	
	public QtnTenderDataBean(){
		
	}
	
	public QtnTenderDataBean(List<QtnTenderBean> list ){
		
		super(list);
	}
	@Override
	public QtnTenderBean getRowData(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getRowKey(QtnTenderBean arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
