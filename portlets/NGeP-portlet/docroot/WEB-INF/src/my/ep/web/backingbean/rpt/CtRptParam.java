package my.ep.web.backingbean.rpt;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.service.rpt.IRPTRequestSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.common.report.RPTRequestResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.persistence.logging.LogKey;
import my.ep.type.report.EnumReportType;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

public class CtRptParam extends RptBackingBean{

}
