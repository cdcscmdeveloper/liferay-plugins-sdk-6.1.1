package my.ep.web.managedbean.ss.procure;

public class ProcureList { 
    private int number;
    private String descript;
    private String modes;
    private double amount;
    private String planstatus;
    private String creations;
    private String dates;
    private int days;
    private String tender;
    private String action;
    

    public int getNumber() {
        return number;
    } 
    public void setPlanNumber(int number) {
        this.number = number;
    } 
    public String getDescript() {
        return descript;
    } 
    public void setDescript(String descript) {
        this.descript = descript;
    } 
    public String getModes() {
        return modes;
    } 
    public void setModes(String modes) {
        this.modes = modes;
    }
    public double getAmount() {
        return amount;
    } 
    public void setPlannedAmount(double amount) {
        this.amount = amount;
    }
    public String getPlanstatus() {
        return planstatus;
    } 
    public void setPlanStatus(String planstatus) {
        this.planstatus = planstatus;
    }
    public String getCreations() {
        return creations;
    } 
    public void setCreations(String creations) {
        this.creations = creations;
    }
    public String getDates() {
        return dates;
    } 
    public void setDates(String dates) {
        this.dates = dates;
    }
    public int getDays() {
        return days;
    } 
    public void setDays(int days) {
        this.days = days;
    }
    public String getTender() {
        return tender;
    } 
    public void setTender(String tender) {
        this.tender = tender;
    }
    public String getAction() {
        return action;
    } 
    public void setAction(String action) {
        this.action = action;
    }   
}