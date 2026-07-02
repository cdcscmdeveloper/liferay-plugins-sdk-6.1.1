package my.ep.web.bean.sc.quatationtender;

public class WeightageScoreBean {
	private double technicalTotalScore;
	private double financialTotalScore;
	private double recommendationTechnicalScore;
	private double recommendationFinancialScore;
	private double recommendationTotalScore;
//	private double overallTecRecommendation;
//	private double overallFinRecommendation;
	
	public WeightageScoreBean(double technicalTotalScore, double financialTotalScore,
			double recommendationTechnicalScore, double recommendationFinancialScore, double recommendationTotalScore){
//			double overallTecRecommendation, double overallFinRecommendation) {
		
		this.technicalTotalScore = technicalTotalScore;
		this.financialTotalScore = financialTotalScore;
		this.recommendationTechnicalScore = recommendationTechnicalScore;
		this.recommendationFinancialScore = recommendationFinancialScore;
		this.recommendationTotalScore = recommendationTotalScore;
//		this.setOverallTecRecommendation(overallTecRecommendation);
//		this.setOverallFinRecommendation(overallFinRecommendation);
		
	}
	public double getTechnicalTotalScore() {
		return technicalTotalScore;
	}
	public void setTechnicalTotalScore(double technicalTotalScore) {
		this.technicalTotalScore = technicalTotalScore;
	}
	public double getFinancialTotalScore() {
		return financialTotalScore;
	}
	public void setFinancialTotalScore(double financialTotalScore) {
		this.financialTotalScore = financialTotalScore;
	}
	public double getRecommendationTechnicalScore() {
		return recommendationTechnicalScore;
	}
	public void setRecommendationTechnicalScore(double recommendationTechnicalScore) {
		this.recommendationTechnicalScore = recommendationTechnicalScore;
	}
	public double getRecommendationFinancialScore() {
		return recommendationFinancialScore;
	}
	public void setRecommendationFinancialScore(double recommendationFinancialScore) {
		this.recommendationFinancialScore = recommendationFinancialScore;
	}
	public double getRecommendationTotalScore() {
		return recommendationTotalScore;
	}
	public void setRecommendationTotalScore(double recommendationTotalScore) {
		this.recommendationTotalScore = recommendationTotalScore;
	}
//	public double getOverallTecRecommendation() {
//		return overallTecRecommendation;
//	}
//	public void setOverallTecRecommendation(double overallTecRecommendation) {
//		this.overallTecRecommendation = overallTecRecommendation;
//	}
//	public double getOverallFinRecommendation() {
//		return overallFinRecommendation;
//	}
//	public void setOverallFinRecommendation(double overallFinRecommendation) {
//		this.overallFinRecommendation = overallFinRecommendation;
//	}
	
}
