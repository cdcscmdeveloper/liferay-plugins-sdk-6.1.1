/**
 * 
 */
package my.ep.web.backingbean.sc.specification;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtWeightage;

import org.apache.commons.lang.StringUtils;

/**
 * @author ACER
 *
 */
@FacesConverter(value = "specificationConverter")
public class SpecificationConverter implements  Converter {

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String id) {
		// TODO Auto-generated method stub
		ScQtCheckList scQtChecklist = new ScQtCheckList();

		

		ScQtWeightage weightage = scQtChecklist
				.getScQtWeightage();
		// SupplierManager manager = EjbInitializer.getSupplierManager();
		    if (StringUtils.isEmpty(id)
		            || !org.apache.commons.lang.math.NumberUtils.isNumber(id)) {
		        return null;
		    } else {
		    	ScQtCheckList sb = null;
		        try {
		          //  sb = convertToPojo((Supplier) manager.find(Integer.valueOf(id)));
		        } catch (Exception  e) {
		            e.printStackTrace();
		        }
		        return sb;
		    }
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		// TODO Auto-generated method stub
		return null;
	}

}
