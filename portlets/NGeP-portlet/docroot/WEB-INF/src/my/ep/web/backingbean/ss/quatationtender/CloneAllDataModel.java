/**
 * 
 */
package my.ep.web.backingbean.ss.quatationtender;

import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

import my.ep.web.type.ss.quationtender.CloneAll;

/**
 * @author Sudheer
 *
 */
public class CloneAllDataModel extends ListDataModel<CloneAll> implements SelectableDataModel<CloneAll> {
	public CloneAllDataModel(){
		
	}
   public CloneAllDataModel(List<CloneAll> cloneValues){
		super(cloneValues);
	}

	@Override
	public CloneAll getRowData(String rowKey) {
		 List<CloneAll> cloneAllValues = (List<CloneAll>) getWrappedData();  
         
	        for(CloneAll cloneValue : cloneAllValues) {  
	            if(cloneValue.getValue().equals(rowKey))  
	                return cloneValue;  
	        }
		return null;
	}

	@Override
	public Object getRowKey(CloneAll arg0) {
		// TODO Auto-generated method stub
		return arg0.getValue();
	}  

}
