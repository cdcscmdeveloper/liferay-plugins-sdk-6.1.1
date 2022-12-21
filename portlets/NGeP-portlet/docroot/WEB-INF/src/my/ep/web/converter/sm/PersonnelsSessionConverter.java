package my.ep.web.converter.sm;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import my.ep.helper.EntityHelper;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.util.sm.helper.SmCommonUtil;

import org.apache.commons.lang.StringUtils;

/**
 * Convert SmPersonnel object for auto complete usage purpose and the source of
 * the personnel list is from session context map with key map is
 * 'personnelList'
 * 
 */
@FacesConverter("personnelsSessionConverter")
public class PersonnelsSessionConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		if (arg2 != null && arg2.trim().length() > 0
				&& StringUtils.isNumeric(arg2)) {
			@SuppressWarnings("unchecked")
			List<SmPersonnel> personnels = (List<SmPersonnel>) SmCommonUtil
					.getExternalContextSessionMapValue(SupplierManagementConst.KEY_MAP_PERSONNEL_LIST);
			SmPersonnel personnel = (SmPersonnel) EntityHelper
					.getEntityBySinglePropertyFromList(Long.parseLong(arg2),
							personnels, "personnelId");
			return personnel;
		} else {
			return null;
		}
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		if (arg2 != null && arg2 instanceof SmPersonnel) {
			SmPersonnel personnel = (SmPersonnel) arg2;
			Long personnelId = personnel.getPersonnelId();
			return personnelId != null ? String.valueOf(personnelId)
					: StringUtils.EMPTY;
		}
		return StringUtils.EMPTY;
	}

}
