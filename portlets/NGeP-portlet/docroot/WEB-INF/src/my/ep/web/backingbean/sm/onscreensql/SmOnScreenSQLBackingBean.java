package my.ep.web.backingbean.sm.onscreensql;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import my.ep.constant.MessageConst;
import my.ep.dto.sm.scheduler.SmSchedulerResDTO;
import my.ep.exception.EpException;
import my.ep.util.MessageUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmServiceLookup;

@ManagedBean(name = "smOnScreenSQLBackingBean")
@ViewScoped
public class SmOnScreenSQLBackingBean implements Serializable{
	private static final long serialVersionUID = 1L;
	public static Logger log = Logger.getLogger(SmOnScreenSQLBackingBean.class);
 
	private String sqlStmt;
	private ResultSet rs;
	private String sqlAction;
	
	private final String SQL_SELECT = "SELECT";
	private final String SQL_UPDATE = "UPDATE";
	private final String SQL_INSERT = "INSERT";
	
	private List<Map<String, Object>> dataRowLs;

	public String getSqlStmt() {
		return sqlStmt;
	}

	public void setSqlStmt(String sqlStmt) {
		this.sqlStmt = sqlStmt;
	}
	
	public ResultSet getRs() {
		return rs;
	}

	public void setRs(ResultSet rs) {
		this.rs = rs;
	}
	
	public List<Map<String, Object>> getDataRowLs() {
		return dataRowLs;
	}

	public void setDataRowLs(List<Map<String, Object>> dataRowLs) {
		this.dataRowLs = dataRowLs;
	}

	public String getSqlAction() {
		return sqlAction;
	}

	public void setSqlAction(String sqlAction) {
		this.sqlAction = sqlAction;
	}
	
	public String getSqlSelect() {
		return SQL_SELECT;
	}

	public String getSqlUpdate() {
		return SQL_UPDATE;
	}

	public String getSqlInsert() {
		return SQL_INSERT;
	}

	public SmOnScreenSQLBackingBean(){
		//System.getProperties().setProperty("oracle.jdbc.J2EE13Compliant", "true");
	}
	
	public void submitSQL(){
		rs = null;
		//String sql = "Select * from sm_complaint";
		//System.getProperties().setProperty("oracle.jdbc.J2EE13Compliant", "true");
	
		if(StringUtils.isNotBlank(sqlStmt)){
			String sql = sqlStmt.trim().toUpperCase();
			String replacedSql = sql.replace(";", "");
			sqlAction = replacedSql.split(" ")[0];
			
			if(sqlAction.equalsIgnoreCase(SQL_INSERT) || sqlAction.equalsIgnoreCase(SQL_UPDATE) || 
					sqlAction.equalsIgnoreCase(SQL_SELECT)){
				SmSchedulerResDTO resDTO = null;
				
				if(sqlAction.equalsIgnoreCase(SQL_UPDATE) && !replacedSql.contains("WHERE")){
					throw new EpException("Sql Update must contains WHERE condition.");
				}
				
				try {
					resDTO = SmServiceLookup.getSmSchedulerSO().submitSQL(sqlStmt, sqlAction.toUpperCase());
					String msgCode = null;
					//sqlAction = sqlAction.toUpperCase();
					
					switch(sqlAction){
					case SQL_INSERT:
						//msgCode = MessageConst.INFO_721013;
						msgCode = "Data has been inserted successfully.";
						break;
					case SQL_SELECT:
						//msgCode = MessageConst.INFO_721014;
						msgCode = "SQL executed Successful.";
						break;
					case SQL_UPDATE:
						//msgCode = MessageConst.INFO_721012;
						msgCode = "Data has been updated successfully.";
						break;
					}
					
					FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
							FacesMessage.SEVERITY_INFO,null,msgCode));
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				dataRowLs = resDTO.getDataRowLs();
			}else{
				throw new EpException("Invalid Action.");
			}
			
		}
		
		
/*		try{
			log.info("*******ResultSetMetaData************");
			ResultSetMetaData columns = rs.getMetaData();
			headers = new ArrayList<String>();
			int i = 0;
			while (i < columns.getColumnCount()) {
				i++;
				headers.add(columns.getColumnName(i));
			}
		}catch(Exception ex){
			log.info(ex.getMessage());
		}*/
		/*if(rs != null){
			while (rs.next()) {
			    Object row = rs.getString(columnIndex);
			}
		}*/
	}
}
