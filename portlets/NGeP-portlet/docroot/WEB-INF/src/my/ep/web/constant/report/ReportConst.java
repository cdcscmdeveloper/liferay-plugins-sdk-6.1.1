/* 
 ***************************************************   
 *                ePerolehan 
 * Product nextgen
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.constant.report;

import java.text.SimpleDateFormat;

/**
 * @Author Jangari Kondal Reddy
 */
public class ReportConst {

	// public static final String MOD_SC_TTL = "lbl.module.supplierSelection";
	public static final String MALAY_LANGUAGE_CODE = "ms";
	public static final String MALAYSIA_COUNTRY_CODE = "MY";
	public static final String ENG_LANGUAGE_CODE = "en";
	public static final String ENG_COUNTRY_CODE = "EN";	
	public static final String MOD_SC_TTL = "lbl.module.quotationtender";
	public static final String MOD_DP_TTL = "lbl.module.directpurchase";
	public static final String MOD_CM_TTL = "lbl.module.catalogueManagement";
	public static final String MOD_FL_TTL = "lbl.module.orderFulfillment";
	public static final String MOD_SM_TTL = "lbl.module.supplierManagement";
	public static final String MOD_CT_TTL = "lbl.module.contractManagement";
	public static final String MOD_PP_TTL = "lbl.module.procurementPlan";
	public static final String SC_MOD_VAL = "SC";
	public static final String DP_MOD_VAL = "DP";
	public static final String CM_MOD_VAL = "CM";
	public static final String FL_MOD_VAL = "FL";
	public static final String SM_MOD_VAL = "SM";
	public static final String CT_MOD_VAL = "CT";
	public static final String PP_MOD_VAL = "PP";
	public static final String FL_SUB_MOD_MINISTRY_VAL="MC";
	public static final String FL_SUB_MOD_CENTRAL_VAL="CC";
	public static final String FL_SUB_MOD_PURCHASE_VAL="PO";
	public static final String FL_SUB_MOD_PANEL_VAL="PA";
	public static final String FL_SUB_MOD_CENTRAL_TREASURY="PKP NO";
	public static final String FL_SUB_MOD_FULLFILLEMENT_RECEIVE="FRN NO";
	
	// SC Module reports start
	public static final String SPPPP_PEMATUHAN_OC_RPT_VAL = "SC_001";
	public static final String KPPP1_PEMATUHAN_OC_RPT_VAL = "SC_002";
	public static final String PDDW_OC_RPT_VAL = "SC_003";
	public static final String AKPP_PEMATUHAN_OC_RPT_VAL = "SC_004";
	public static final String SPPSAP2_TEC_RPT_VAL = "SC_005";
	public static final String KPSAP2_PEMARKAHAN_TEC_RPT_VAL = "SC_006";
	public static final String AKPT_TEC_RPT_VAL = "SC_007";
	public static final String TECSPEC_TEC_RPT_VAL = "SC_008";
	public static final String PSDAK_TEC_RPT_VAL = "SC_009";
	public static final String PSDABK_TEC_RPT_VAL = "SC_010";
	public static final String OVRL_TECRES_WITH_WEI_TEC_RPT_VAL = "SC_012";
	public static final String OVRL_TECRES_WITH_OUT_WEI_TEC_RPT_VAL = "SC_011";
	public static final String SPPSAP2_FEC_RPT_VAL = "SC_013";
	public static final String KPSAP2_PEMARKAHAN_FEC_RPT_VAL = "SC_014";
	public static final String AKPT_FEC_RPT_VAL = "SC_015";
	public static final String HARGA_TAWARAN_FEC_RPT_VAL = "SC_016";
	public static final String KEDUDUKAN_KEWANGAN_FEC_RPT_VAL = "SC_017";
	public static final String PSDAK_JUMLAH_KONTRAK_FEC_RPT_VAL = "SC_018";
	public static final String PSDABK_JUMLAH_KONTRAK_FEC_RPT_VAL = "SC_019";
	public static final String OVRL_FINRS_FEC_RPT_VAL = "SC_020";
	public static final String SPPP1_PEMATUHAN_EC_RPT_VAL = "SC_021";
	public static final String KPP1_PEMATUHAN_EC_RPT_VAL = "SC_022";
	public static final String PDW_EC_RPT_VAL = "SC_023";
	public static final String SPPSAP2_EC_RPT_VAL = "SC_024";
	public static final String KPSAP2_PEMARKAHAN_EC_RPT_VAL = "SC_025";
	public static final String AKPTPH_EC_RPT_VAL = "SC_026";
	public static final String TECSPEC_EC_RPT_VAL = "SC_027";
	public static final String PSDAK_BILANGAN_KONTRAK_EC_RPT_VAL = "SC_028";
	public static final String PSDABK_BILANGAN_KONTRAK_EC_RPT_VAL = "SC_029";
	public static final String HARGA_TAWARAN_EC_RPT_VAL = "SC_030";
	public static final String KEMUDAHAN_KEWANGAN_EC_RPT_VAL = "SC_031";
	public static final String COMP_EXP_GOVT_AGEN_CONTR_EC_RPT_VAL = "SC_032";
	public static final String COMP_EXP_NON_GOVT_AGEN_CONTR_EC_RPT_VAL = "SC_033";
	public static final String OVRL_TECRS_WITH_WEI_EC_RPT_VAL = "SC_035";
	public static final String OVRL_TECRS_WITH_OUT_WEI_EC_RPT_VAL = "SC_034";
	public static final String OVRL_FINRS_EC_RPT_VAL = "SC_036";
	public static final String ANLSIS_EVALUATION_FC_RPT_VAL = "SC_037";
	public static final String SC_QUOT_TENDER_OR_RPT_VAL = "SC_038";
	public static final String SC_SUMMRY_SRC_OR_RPT_VAL = "SC_039";
	public static final String SC_BRIF_SITE_VISIT_OR_RPT_VAL = "SC_040";
	public static final String SC_QUOT_TENDER_MONITOR_OR_RPT_VAL = "SC_041";
	public static final String SC_PRAPOSAL_SUB_LST_OR_RPT_VAL = "SC_042";
	public static final String SC_PRICE_SCHEDULE_OR_RPT_VAL = "SC_043";
	public static final String SC_LETTER_OF_ACCPT_OR_RPT_VAL = "SC_044";
	public static final String SC_COMMITTEE_DTLS_TD_RPT_VAL = "SC_045";
	public static final String SC_PROPOSAL_DTLS_PRODUCT_RPT_VAL = "SC_046";
	public static final String SC_PROPOSAL_DTLS_SERVICE_RPT_VAL = "SC_047";

	public static final String SC_PURCHASE_ITEM_INQUIRY_FINAL_S1_RPT_VAL = "SC_050";
	public static final String SC_PURCHASE_SUMMURY_ITEM_INQUIRY_S1_RPT_VAL = "SC_051";
	public static final String SC_PURCHASE_SUMMURY_ITEM_INQUIRY_SUPPLIER_S1_RPT_VAL = "SC_052";
	public static final String SC_PURCHASE_SUMMURY_ITEM_INQUIRY_REQUISITIONER_S1_RPT_VAL = "SC_053";
	public static final String SC_PURCHASE_SUMMURY_ITEM_INQUIRY_PINO_S1_RPT_VAL = "SC_054";
	public static final String SC_LAPORAN_PURCHASE_DEFAULT_S1_RPT_VAL = "SC_055";
	public static final String SC_SUMMARY_REQUEST_NOTE_S1_RPT_VAL = "SC_056";
	public static final String SC_REQUEST_NOTE_PROCUREMENT_S1_RPT_VAL = "SC_057";
	public static final String SC_REQUEST_NOTE_COMPLETE_S1_RPT_VAL = "SC_058";
	public static final String SC_PURCHASE_INQ_DTL_S1_RPT_VAL = "SC_059";
	public static final String SC_SIMPLE_QUOTE_DTL_STS_COMPLD_S1_RPT_VAL = "SC_060";
	public static final String SC_SIMPLE_QUOTE_SMRY_BY_REQUSNR_S1_RPT_VAL = "SC_061";
	public static final String SC_SIMPLE_QUOTE_SMRY_BY_SUPPL_S1_RPT_VAL = "SC_062";
	public static final String SC_LAPORAN_PERICIAN_SQ_DTL_ITM_EST_PRICE_STS_S1_RPT_VAL = "SC_063";
	public static final String SC_S1_RCNT_PRICE_RPT_VAL = "SC_064";
	public static final String SC_ITEM_PRICING_SUM_RPT_VAL = "SC_065";
	public static final String SC_S1_REQUEST_NOTE_FINAL_RPT_VAL = "SC_066";
	public static final String SC_S1_PURCHASE_DEFAULT_DTLS_RPT_VAL = "SC_067";
	public static final String SC_S1_SIMPLE_QUOTE_PROCESS_RPT_VAL = "SC_068";
	public static final String SC_S1_LOA_RPT_VAL = "SC_069";
	public static final String SC_S1_LOA_CONTENT_RPT_VAL = "SC_070";
	public static final String PYMT_RCPT_RPT_NAME = "lbl.rpt.sc.PYMT_RCPT_RPT_NAME";
	public static final String PYMT_RCPT_RPT_VAL = "PymtRcpt";
	// S1 Report
	// JSP
	// OC
	public static final String SC_OC_RPT_JSP = "SC_OC.jsp";
	public static final String SC_SPPPP_PEMATUHAN_OC_RPT_JSP = "SC_OC_SHLIST_1A.jsp";
	public static final String SC_KPPP1_PEMATUHAN_OC_RPT_JSP = "SC_OC_METHOD_OF_ASSES_1B.jsp";
	
	// FEC
	public static final String SC_FEC_RPT_JSP = "SC_FEC.jsp";
	public static final String SC_SPPSAP2_FEC_RPT_JSP = "SC_FEC_SHLIST_LEVEL2_3A.jsp";
	public static final String SC_KPSAP2_PEMARKAHAN_FEC_RPT_JSP = "SC_FEC_FIN_ASSES_LEVEL2_3B.jsp";
	public static final String SC_AKPT_FEC_RPT_JSP = "SC_FEC_PRICE_EVAL_3C.jsp";
	public static final String SC_FEC_PRICEOFFER_3D = "SC_FEC_PRICEOFFER_3D.jsp";
	public static final String SC_FEC_OTHER_CRITERIA = "SC_FEC_OTHER_CRITERIA.jsp";
	public static final String SC_FEC_OVERALL_FIN_3E = "SC_FEC_OVERALL_FIN_3E.jsp";

	// TEC
	public static final String SC_TEC_RPT_JSP = "SC_TEC.jsp";
	public static final String SC_SPPSAP2_TEC_RPT_JSP = "SC_TEC_SHLISTTECH_EVAL_2A.jsp";
	public static final String SC_KPSAP2_PEMARKAHAN_TEC_RPT_JSP = "SC_TEC_TECHASSES_2B.jsp";
	public static final String SC_AKPT_TEC_RPT_JSP = "SC_TEC_EVAL_TECH_2C.jsp";
	public static final String SC_TEC_TECH_SPEC_WEI = "SC_TEC_TECH_SPEC_WEI.jsp";
	public static final String SC_TEC_TECH_SPEC = "SC_TEC_TECH_SPEC.jsp";
	public static final String SC_TEC_OVERALL_WITH_WEI_2F = "SC_TEC_OVERALL_WITH_WEI_2F.jsp";
	public static final String SC_TEC_OTHER_CRITERIA = "SC_TEC_OTHER_CRITERIA.jsp";
	public static final String SC_EC_OTHER_CRITERIA = "SC_EC_OTHER_CRITERIA.jsp";
	
	// EC
	public static final String SC_EC_RPT_JSP = "SC_ONE_TIER.jsp";
	public static final String SC_SPPP1_PEMATUHAN_EC_RPT_JSP = "SC_EC_SHORTLIST_ASSES_4A.jsp";
	public static final String SC_KPP1_PEMATUHAN_EC_RPT_JSP = "SC_EC_METHOD_OF_ASSES_4B.jsp";
	public static final String SC_KPSAP2_PEMARKAHAN_EC_RPT_JSP = "SC_EC_GEN_SPEC_TECH_4D.jsp";
	public static final String SC_EC_GEN_SPEC_FIN_4D = "SC_EC_GEN_SPEC_FIN_4D.jsp";
	public static final String SC_AKPTPH_EC_RPT_JSP = "SC_EC_PRICE_EVAL_4E.jsp";
	public static final String SC_EC_OVERALLTECH_WITH_WEI_4H = "SC_EC_OVERALLTECH_WITH_WEI_4H.jsp";
	public static final String SC_EC_OVERALL_FIN_4I = "SC_EC_OVERALL_FIN_4I.jsp";
	public static final String SC_EC_TECH_SPEC_WEI = "SC_EC_TECH_SPEC_WEI.jsp";
	public static final String SC_EC_TECH_SPEC = "SC_EC_TECH_SPEC.jsp";
	public static final String SC_EC_PRICEOFFER_4M = "SC_EC_PRICEOFFER_4M.jsp";

	// FC
	public static final String SC_SUPPLIER_FINALIZATION_RPT_JSP = "SC_SUPP_FINAL.jsp";
	public static final String SC_FINANCE_ANALYSIS_EVALUATION_RPT_JSP = "SC_FC_ANALYSIS_EVAL_REP_8A.jsp";

	public static final String SC_PDDW_OC_RPT_JSP = "sc_oc_compliance_docs_1c.jsp";
	public static final String SC_AKPP_PEMATUHAN_OC_RPT_JSP = "sc_oc_first_stage_1d.jsp";
	public static final String SC_TECSPEC_TEC_RPT_JSP = "sc_tec_spec_techpapers_2d.jsp";
	public static final String SC_OVRL_TECRES_WITH_WEI_TEC_RPT_JSP = "sc_tec_overall_with_wei_2f.jsp";
	public static final String SC_OVRL_TECRES_WITH_OUT_WEI_TEC_RPT_JSP = "sc_tec_overall_tech_2e.jsp";
	public static final String SC_HARGA_TAWARAN_FEC_RPT_JSP = "sc_fec_priceoffer_3d.jsp";
	public static final String SC_OVRL_FINRS_FEC_RPT_JSP = "sc_fec_overall_fin_3e.jsp";
	public static final String SC_PDW_EC_RPT_JSP = "sc_ec_method_of_asses_4l.jsp";
	public static final String SC_SPPSAP2_EC_RPT_JSP = "SC_EC_GEN_SPEC_4C.jsp";
	public static final String SC_TECSPEC_EC_RPT_JSP = "sc_ec_tech_spec_4f.jsp";
	public static final String SC_PSDAK_BILANGAN_KONTRAK_EC_RPT_JSP = "sc_ec_exp_govt_4j.jsp";
	public static final String SC_PSDABK_BILANGAN_KONTRAK_EC_RPT_JSP = "sc_ec_exp_non_govt_4k.jsp";
	public static final String SC_HARGA_TAWARAN_EC_RPT_JSP = "sc_ec_priceoffer_4m.jsp";
	public static final String SC_KEMUDAHAN_KEWANGAN_EC_RPT_JSP = "sc_ec_fin_position_4n.jsp";
	public static final String SC_COMP_EXP_GOVT_AGEN_CONTR_EC_RPT_JSP = "sc_fecec_exp_non_govt_6b.jsp";
	public static final String SC_COMP_EXP_NON_GOVT_AGEN_CONTR_EC_RPT_JSP = "sc_ec_exp_non_govt_6b.jsp";
	public static final String SC_OVRL_TECRS_WITH_OUT_WEI_EC_RPT_JSP = "sc_ec_overall_tech_4g.jsp";
	public static final String SC_OVRL_TECRS_WITH_WEI_EC_RPT_JSP = "sc_ec_overalltech_with_wei_4h.jsp";
	public static final String SC_OVRL_FINRS_EC_RPT_JSP = "sc_ec_overall_fin_4i.jsp";
	public static final String SC_COMP_EXP_GOVT_AGEN_NO_OF_CONTR_RPT_JSP = "sc_tecec_exp_govt_5a.jsp";
	public static final String SC_COMP_EXP_NON_GOVT_AGEN_NO_OF_CONTR_RPT_JSP = "sc_tecec_exp_non_govt_5b.jsp";
	public static final String SC_COMP_EXP_GOVT_AGEN_CONTR_RPT_JSP = "sc_fecec_exp_govt_6a.jsp";
	public static final String SC_COMP_EXP_NON_GOVT_AGEN_CONTR_RPT_JSP = "sc_fecec_exp_non_govt_6b.jsp";
	public static final String SC_FINANCE_POSITION_RPT_JSP = "sc_fecec_fin_position_6c.jsp";

	public static final String SC_RECENT_PRICE_RPT_JSP = "SC_S1_RCNT_PRICE.jsp";
	public static final String SC_MARKET_RESEARCH_RPT_JSP = "SC_ITEM_PRICING_SUM.jsp";
	// SC Module reports start
	// CM Module reports start
	public static final String LST_PUB_CAT_ITM_RPT_VAL = "CM_001";
	public static final String LST_APPR_CAT_ITM_RPT_VAL = "CM_002";
	public static final String LST_REJ_CAT_ITM_RPT_VAL = "CM_003";
	public static final String LST_PEND_APPR_CAT_ITM_RPT_VAL = "CM_004";
	public static final String LST_EXP_CAT_ITM_RPT_VAL = "CM_005";
	public static final String TOT_CAT_ITM_STS_RPT_VAL = "CM_006";
	public static final String TOT_CAT_ITM_UNSPSC_CODE_RPT_VAL = "CM_007";
	public static final String LST_CAT_ITM_STD_PRC_RPT_VAL = "CM_008";
	public static final String LST_CAT_ITM_TIER_PRC_RPT_VAL = "CM_009";
	public static final String LST_CAT_ITM_ZON_PRC_RPT_VAL = "CM_010";
	public static final String LST_CAT_ITM_ZON_TIER_PRC_RPT_VAL = "CM_011";
	public static final String LST_PEND_CODE_ASGN_AGNCY_RPT_VAL = "CM_012";
	public static final String SUM_REJ_CAT_ITM_RPT_VAL = "CM_013";
	public static final String TOT_CAT_ITM_APPR_REJ_RPT_VAL = "CM_014";
	public static final String LST_UNSPSC_CODE_REQ_RPT_VAL = "CM_015";
	public static final String LST_NEW_EXT_CODE_CREA_RPT_VAL = "CM_016";
	public static final String LST_ITM_CODE_RPT_VAL = "CM_017";
	// CM Module reports end
	// FL Module reports start
	// Ministry
	public static final String DEL_ORD_REJ_RPT_MIN_NAME = "lbl.rpt.fl.DEL_ORD_REJ_RPT_MIN_NAME";
	public static final String DEL_ORD_REJ_RPT_MIN_VAL = "DeliveryOrderRejectionReport-Min";
	public static final String YEP_RPT_MIN_NAME = "lbl.rpt.fl.YEP_RPT_MIN_NAME";
	public static final String YEP_RPT_MIN_VAL = "YEPReport-Min";
	public static final String YEP_AUTO_CMT_CON_ORD_RPT_MIN_NAME = "lbl.rpt.fl.YEP_AUTO_CMT_CON_ORD_RPT_MIN_NAME";
	public static final String YEP_AUTO_CMT_CON_ORD_RPT_MIN_VAL = "YEP–AutoBudgetCommitmentforContractOrder-Min";
	public static final String DEL_ORD_REJ_RPT_CEN_PEN_NAME = "lbl.rpt.fl.DEL_ORD_REJ_RPT_CEN_PEN_NAME";
	public static final String DEL_ORD_REJ_RPT_CEN_PEN_VAL = "DeliveryOrderRejectionReport-Cen/Pan";
	public static final String DED_RPT_CEN_PEN_NAME = "lbl.rpt.fl.DED_RPT_CEN_PEN_NAME";
	public static final String DED_RPT_CEN_PEN_VAL = "DeductionReport-Cen/Pan";
	public static final String YEP_RPT_CEN_PEN_NAME = "lbl.rpt.fl.YEP_RPT_CEN_PEN_NAME";
	public static final String YEP_RPT_CEN_PEN_VAL = "YEPReport-Cen/Pan";
	public static final String YEP_AUTO_CMT_CON_ORD_RPT_CEN_PEN_NAME = "lbl.rpt.fl.YEP_AUTO_CMT_CON_ORD_RPT_CEN_PEN_NAME";
	public static final String YEP_AUTO_CMT_CON_ORD_RPT_CEN_PEN_VAL = "YEP–AutoBudgetCommitmentforContractOrder-Cen/Pan";
	public static final String DEL_ORD_REJ_RPT_PO_NAME = "lbl.rpt.fl.DEL_ORD_REJ_RPT_PO_NAME";
	public static final String DEL_ORD_REJ_RPT_PO_VAL = "DeliveryOrderRejectionReport-PO";
	public static final String YEP_RPT_PO_NAME = "lbl.rpt.fl.YEP_RPT_PO_NAME";
	public static final String YEP_RPT_PO_VAL = "YEPReport-PO";
	public static final String YEP_AP58A_RPT_PO_NAME = "lbl.rpt.fl.YEP_AP58A_RPT_PO_NAME";
	public static final String YEP_AP58A_RPT_PO_VAL = "YEPReport–AP58A-PO";

	public static final String CON_REQ_RPT_MIN_VAL = "FL_001";
	public static final String CON_ORD_RPT_MIN_VAL = "FL_002";
	public static final String DEL_ORD_RPT_MIN_VAL = "FL_003";
	public static final String FL_REC_NOTE_RPT_MIN_VAL = "FL_004";
	public static final String LATE_DEL_RPT_MIN_VAL = "FL_005";
	public static final String SHP_INSTR_RPT_MIN_VAL = "FL_006";
	public static final String STP_INSTR_DEL_RPT_MIN_VAL = "FL_007";
	public static final String INV_RPT_VIEW_SUPL_MIN_VAL = "FL_008";
	public static final String INV_RPT_VIEW_PTJ_MIN_VAL = "FL_009";
	public static final String PYMT_MATCH_RPT_MIN_VAL = "FL_010";
	public static final String DED_RPT_MIN_VAL = "FL_011";
	public static final String CON_REQ_CANC_RPT_MIN_VAL = "FL_012";
	public static final String CON_ORD_CANC_RPT_MIN_VAL = "FL_013";
	public static final String DEL_ORD_CANC_RPT_MIN_VAL = "FL_014";
	public static final String FL_REC_NOTE_CANC_RPT_MIN_VAL = "FL_015";
	public static final String INV_CANC_RPT_MIN_VAL = "FL_016";
	public static final String SUP_PERF_RPT_MIN_VAL = "FL_017";
	// Central-Panel
	public static final String CON_REQ_RPT_CEN_PEN_VAL = "FL_018";
	public static final String CON_ORD_RPT_CEN_PEN_VAL = "FL_019";
	public static final String DEL_ORD_RPT_CEN_PEN_VAL = "FL_020";
	public static final String FL_REC_NOTE_RPT_CEN_PEN_VAL = "FL_021";
	public static final String LATE_DEL_RPT_CEN_PEN_VAL = "FL_022";
	public static final String SHP_INSTR_RPT_CEN_PEN_VAL = "FL_058";
	public static final String STP_INSTR_DEL_RPT_CEN_PEN_VAL = "FL_023";
	public static final String INV_RPT_VIEW_SUPL_CEN_PEN_VAL = "FL_024";
	public static final String INV_RPT_VIEW_PTJ_CEN_PEN_VAL = "FL_025";
	public static final String PYMT_MATCH_RPT_CEN_PEN_VAL = "FL_026";
	public static final String CON_REQ_CANC_RPT_CEN_PEN_VAL = "FL_027";
	public static final String CON_ORD_CANC_RPT_CEN_PEN_VAL = "FL_028";
	public static final String DEL_ORD_CANC_RPT_CEN_PEN_VAL = "FL_029";
	public static final String FL_REC_NOTE_CANC_RPT_CEN_PEN_VAL = "FL_030";
	public static final String INV_CANC_RPT_CEN_PEN_VAL = "FL_031";
	public static final String SUP_PERF_RPT_CEN_PEN_VAL = "FL_032";
	public static final String SUMM_RPT_PAN_ITM_VAL = "FL_033";
	// PO
	public static final String PUR_REQ_RPT_PO_VAL = "FL_034";
	public static final String PUR_ORD_RPT_PO_VAL = "FL_035";
	public static final String EMR_PROCUR_RPT_PO_VAL = "FL_036";
	public static final String PUR_ORD_REJ_RPT_PO_VAL = "FL_037";
	public static final String DEL_ORD_RPT_PO_VAL = "FL_038";
	public static final String FL_REC_NOTE_RPT_PO_VAL = "FL_039";
	public static final String LATE_DEL_RPT_PO_VAL = "FL_040";
	public static final String STP_INSTR_DEL_RPT_PO_VAL = "FL_041";
	public static final String INV_RPT_VIEW_SUPL_PO_VAL = "FL_042";
	public static final String INV_RPT_VIEW_PTJ_PO_VAL = "FL_043";
	public static final String PYMT_MATCH_RPT_PO_VAL = "FL_044";
	public static final String PUR_REQ_CANC_RPT_PO_VAL = "FL_045";
	public static final String PUR_ORD_CANC_RPT_PO_VAL = "FL_046";
	public static final String DEL_ORD_CANC_RPT_PO_VAL = "FL_047";
	public static final String FL_REC_NOTE_CANC_RPT_PO_VAL = "FL_048";
	public static final String INV_CANC_RPT_PO_VAL = "FL_049";
	public static final String FL_STP_INSTR_INIT_PTJ_RPT_FL_MIN_VAL = "FL_050";
	public static final String FL_STP_INSTR_INIT_PTJ_RPT_FL_CEN_PEN_VAL = "FL_051";
	public static final String FL_STP_INSTR_INIT_PTJ_RPT_FL_PO_VAL = "FL_052";
	public static final String FL_STP_INSTR_INIT_SUP_RPT_FL_MIN_VAL = "FL_053";
	public static final String FL_STP_INSTR_INIT_SUP_RPT_FL_CEN_PEN_VAL = "FL_054";
	public static final String FL_STP_INSTR_INIT_SUP_RPT_FL_PO_VAL = "FL_055";

	// PP
	public static final String PP_PROCUREMENT_PTJ_LEVEL_RPT_VAL = "PP_001";
	public static final String PP_PROCUREMENT_MINISTRY_LEVEL_RPT_VAL = "PP_002";
	public static final String PP_PROCUREMENT_BKP_LEVEL_RPT_VAL = "PP_003";
	public static final String PP_EXPENDITURE_PLAN_PTJ_LEVEL_RPT_VAL = "PP_004";
	public static final String PP_EXPEND_MINISTRY_BY_VOT_PTJ_LEVEL_RPT_VAL = "PP_005";
	public static final String PP_EXPEND_MINISTRY_BY_GOSO_PTJ_LEVEL_RPT_VAL = "PP_006";
	public static final String PP_EXPEND_MINISTRY_BY_GOSO_VOT_LEVEL_RPT_VAL = "PP_007";
	public static final String PP_EXPENDITURE_BPK_LEVEL_RPT_VAL = "PP_008";
	public static final String PP_EXPENDITURE_UNPLANNED_LEVEL_RPT_VAL = "PP_009";
	public static final String PP_NGEP_PP_DETAIL_PROCUREMENT = "PP_DETAIL_PROCUREMENT.jsp";
	public static final String PP_NGEP_PP_DETAIL_EXPENDITURE = "PP_DETAIL_EXPENDITURE.jsp";
	
	// PM
	public static final String PM_LIST_OF_FINACIAL_INSTITUTIONS_RPT_VAL = "PM_001";
	public static final String PM_LIST_OF_ORGANIZATION_RPT_VAL = "PM_002";
	public static final String PM_LIST_OF_PROFILES_RPT_VAL = "PM_003";
	public static final String PM_LIST_OF_GOVERNMENT_SELLERS_RPT_VAL = "PM_004";
	public static final String PM_LIST_OF_EXEMPTED_ITEMS_BY_SUPPLIER_RPT_VAL = "PM_005";
	public static final String PM_LIST_OF_EXEMPTED_ITEMS_RPT_VAL = "PM_006";
	public static final String PM_LIST_OF_EXEMPTED_CONTRACTED_ITEMS_RPT_VAL = "PM_007";
	public static final String PM_BPK_USER_PROFILE_RPT_VAL = "PM_008";
	public static final String PM_BPK_ADMIN_PROFILE_RPT_VAL = "PM_009";
	
	//Tracking Diary - JSP
	public static final String PM_TRACKING_DAIRY_REPORT_NAME = "TRACKING_DAIRY.jsp";

	// JSP
	public static final String FL_NGEP_PURCHASE_ORDER = "FL_NGEP_PURCHASE_ORDER.jsp";
	public static final String FL_NGEP_DELIVERY = "FL_NGEP_DELIVERY.jsp";
	public static final String FL_NGEP_FRN = "FL_NGEP_FRN.jsp";
	public static final String FL_NGEP_INVOICE = "FL_NGEP_INVOICE.jsp";
	public static final String FL_NGEP_TAX_INVOICE = "FL_NGEP_TAX_INVOICE.jsp";
	public static final String FL_NGEP_PAYMENT_MATCH = "FL_NGEP_PAYMENT_MATCH.jsp";
	public static final String FL_NGEP_CREDIT_NOTE = "FL_NGEP_CREDIT_NOTE.jsp";
	public static final String FL_NGEP_DEBIT_NOTE = "FL_NGEP_DEBIT_NOTE.jsp";
	public static final String FL_NGEP_COGSTR = "FL_NGEP_COGSTR.jsp";
	public static final String FL_NGEP_STOP_DELIVERY = "FL_NGEP_SDI.jsp";

	// FL Module reports end
	// SM Module report start
	/*
	 * public static final String SM_LST_OF_APP_RPT_NAME =
	 * "lbl.rpt.sm.SM_LST_OF_APP_RPT_NAME"; public static final String
	 * SM_LST_OF_APP_RPT_VAL = "LstOfApplication"; public static final String
	 * SM_LST_OF_SUPP_DOC_PEND_SUB_NO_DAYS_LAPS_RPT_NAME =
	 * "lbl.rpt.sm.SM_LST_OF_SUPP_DOC_PEND_SUB_NO_DAYS_LAPS_RPT_NAME"; public
	 * static final String SM_LST_OF_SUPP_DOC_PEND_SUB_NO_DAYS_LAPS_RPT_VAL =
	 * "LstOfflnSupportDocPendSubmission(NoOfDaysLapse)"; public static final
	 * String SM_LST_OF_VIRT_CERT_RLSD_ON_DAY_RPT_NAME =
	 * "lbl.rpt.sm.SM_LST_OF_VIRT_CERT_RLSD_ON_DAY_RPT_NAME"; public static
	 * final String SM_LST_OF_VIRT_CERT_RLSD_ON_DAY_RPT_VAL =
	 * "VirtCertiReleasedLstOnTheDayReport"; public static final String
	 * SM_PRFL_OF_REG_COMP_MIN_FIN_RPT_NAME =
	 * "lbl.rpt.sm.SM_PRFL_OF_REG_COMP_MIN_FIN_RPT_NAME";
	 */

	/*
	 * public static final String
	 * SM_LST_OF_COMP_SUPP_TYPE_BUMI_PUT_STS_CAT_RPT_NAME =
	 * "lbl.rpt.sm.SM_LST_OF_COMP_SUPP_TYPE_BUMI_PUT_STS_CAT_RPT_NAME"; public
	 * static final String SM_LST_OF_COMP_SUPP_TYPE_BUMI_PUT_STS_CAT_RPT_VAL =
	 * "LstOfCompBasedSuppTypeBumiputeraStsCatRpt"; public static final String
	 * SM_LST_OF_COMP_DIRS_OWNS_CEO_OR_EQU_RPT_NAME =
	 * "lbl.rpt.sm.SM_LST_OF_COMP_DIRS_OWNS_CEO_OR_EQU_RPT_NAME"; public static
	 * final String SM_LST_OF_COMP_DIRS_OWNS_CEO_OR_EQU_RPT_VAL =
	 * "LstOfCompDirectorsOwnersCEOEquivalentRpt"; public static final String
	 * SM_LST_OF_DIRS_OWNS_CEO_OR_EQU_BELONG_MOR_THAN_ONE_COMP_RPT_NAME =
	 * "lbl.rpt.sm.SM_LST_OF_DIRS_OWNS_CEO_OR_EQU_BELONG_MOR_THAN_ONE_COMP_RPT_NAME"
	 * ; public static final String
	 * SM_LST_OF_DIRS_OWNS_CEO_OR_EQU_BELONG_MOR_THAN_ONE_COMP_RPT_VAL =
	 * "LstOfDirOwnCEOEquivalentbelongMoreThanOneCompRpt"; public static final
	 * String SM_LST_OF_COMP_SORT_ALPHABETIC_RPT_NAME =
	 * "lbl.rpt.sm.SM_LST_OF_COMP_SORT_ALPHABETIC_RPT_NAME"; public static final
	 * String SM_LST_OF_COMP_SORT_ALPHABETIC_RPT_VAL =
	 * "LstOfCompSortedAlphabetReport(A-Z,ActiveOrInactiveComp)"; public static
	 * final String SM_LST_STATISTIC_COMP_BASE_COMP_TYPE_CAPITAL_RPT_NAME =
	 * "lbl.rpt.sm.SM_LST_STATISTIC_COMP_BASE_COMP_TYPE_CAPITAL_RPT_NAME";
	 * public static final String
	 * SM_LST_STATISTIC_COMP_BASE_COMP_TYPE_CAPITAL_RPT_VAL =
	 * "StatListOfCompBasedOnCompTypeAndCapitalRpt"; public static final String
	 * SM_LST_PERSON_OR_STAFF_MORE_ONE_COMP_RPT_NAME =
	 * "lbl.rpt.sm.SM_LST_PERSON_OR_STAFF_MORE_ONE_COMP_RPT_NAME"; public static
	 * final String SM_LST_PERSON_OR_STAFF_MORE_ONE_COMP_RPT_VAL =
	 * "LstOfPerson/StaffsBelongMoreThanOneCompanyRpt"; public static final
	 * String SM_LST_MOF_REG_CANCEL_REQ_SUPP_AUTO_CANC_STM_RPT_NAME =
	 * "lbl.rpt.sm.SM_LST_MOF_REG_CANCEL_REQ_SUPP_AUTO_CANC_STM_RPT_NAME";
	 * public static final String
	 * SM_LST_MOF_REG_CANCEL_REQ_SUPP_AUTO_CANC_STM_RPT_VAL =
	 * "ListMOF-regCancellation(ReqBySupp/AutoCancelledByTheSystem)"; public
	 * static final String SM_LST_OF_PAYMENT_PROCESS_REG_FEE_RPT_NAME =
	 * "lbl.rpt.sm.SM_LST_OF_PAYMENT_PROCESS_REG_FEE_RPT_NAME"; public static
	 * final String SM_LST_OF_PAYMENT_PROCESS_REG_FEE_RPT_VAL =
	 * "LstOfPaymentForProcessingAndRegistrationFee"; public static final String
	 * SM_CAT_STATIC_RPT_NAME = "lbl.rpt.sm.SM_CAT_STATIC_RPT_NAME";
	 */

	public static final String SM_CAT_STATIC_BY_YEAR_RPT_NAME = "lbl.rpt.sm.SM_CAT_STATIC_BY_YEAR_RPT_NAME";
	public static final String SM_CAT_STATIC_BY_STATE_RPT_NAME = "lbl.rpt.sm.SM_CAT_STATIC_BY_STATE_RPT_NAME";
	public static final String SM_APPL_VERIFY_RPT_NAME = "lbl.rpt.sm.SM_APPL_VERIFY_RPT_NAME";
	public static final String SM_APPL_APPROVED_RPT_NAME = "lbl.rpt.sm.SM_APPL_APPROVED_RPT_NAME";
	public static final String SM_APPL_REJECTED_RPT_NAME = "lbl.rpt.sm.SM_APPL_REJECTED_RPT_NAME";
	public static final String SM_LST_APPL_PEND_SITE_VISIT_RPT_NAME = "lbl.rpt.sm.SM_LST_APPL_PEND_SITE_VISIT_RPT_NAME";
	public static final String SM_TOT_NUM_YEARLY_APPL_RPT_NAME = "lbl.rpt.sm.SM_TOT_NUM_YEARLY_APPL_RPT_NAME";
	public static final String SM_LST_PRE_EXP_COMP_RPT_NAME = "lbl.rpt.sm.SM_LST_PRE_EXP_COMP_RPT_NAME";
	public static final String SM_LST_PRE_EXP_COMP_RPT_VAL = "LstOfPre-ExpiryCompRpt/ExpiredCompRpt";
	public static final String SM_LST_MONTH_WISE_RPT_NAME = "lbl.rpt.sm.SM_LST_MONTH_WISE_RPT_NAME";
	public static final String SM_APPL_VERI_PERFORM_CLNT_CHARTER_ACHIVE_RPT_NAME = "lbl.rpt.sm.SM_APPL_VERI_PERFORM_CLNT_CHARTER_ACHIVE_RPT_NAME";
	public static final String SM_LST_DAILY_NEW_APPL_RPT_NAME = "lbl.rpt.sm.SM_LST_DAILY_NEW_APPL_RPT_NAME";
	public static final String SM_LST_COMP_ADDR_RPT_NAME = "lbl.rpt.sm.SM_LST_COMP_ADDR_RPT_NAME";
	public static final String SM_BPK_USER_PROF_RPT_NAME = "lbl.rpt.sm.SM_BPK_USER_PROF_RPT_NAME";
	public static final String SM_BPK_ADMIN_PROF_RPT_NAME = "lbl.rpt.sm.SM_BPK_ADMIN_PROF_RPT_NAME";
	public static final String SM_ONLN_APPL_COMP_INFO_ACCESS_RPT_NAME = "lbl.rpt.sm.SM_ONLN_APPL_COMP_INFO_ACCESS_RPT_NAME";
	public static final String SM_ONLN_APPL_COMP_INFO_ACCESS_RPT_VAL = "OnlineApplCompInfoAccessRpt";
	public static final String SM_AGENCY_STAT_SUMM_RPT_NAME = "lbl.rpt.sm.SM_AGENCY_STAT_SUMM_RPT_NAME";
	public static final String SM_AGENCY_STAT_SUMM_RPT_VAL = "AgencyStatisticalSummaryReport";
	public static final String SM_QUESTIONAIRE_RPT_NAME = "lbl.rpt.sm.SM_QUESTIONAIRE_RPT_NAME";
	public static final String SM_PAYMNT_RECPT_UPN_PAYMNT_RPT_NAME = "lbl.rpt.sm.SM_PAYMNT_RECPT_UPN_PAYMNT_RPT_NAME";
	public static final String SM_PAYMNT_RECPT_UPN_PAYMNT_RPT_VAL = "PaymentReceipts(UponPayment)";
	public static final String SM_LAPORAN_TERIMAAN_HARIAN_RPT_NAME = "lbl.rpt.sm.SM_LAPORAN_TERIMAAN_HARIAN_RPT_NAME";
	public static final String SM_LST_DAILY_APPL_SUBM_BASIC_ACC_MOF_REG_RPT_NAME = "lbl.rpt.sm.SM_LST_DAILY_APPL_SUBM_BASIC_ACC_MOF_REG_RPT_NAME";
	public static final String SM_LST_DAILY_APPL_SUBM_BASIC_ACC_MOF_REG_RPT_VAL = "LstOfDailyApplSubmittedRpt(BasicAcc/MOFReg)";
	public static final String SM_LST_DISCPLNRY_ACTN_IMPOS_AGAINST_SUPPL_RPT_NAME = "lbl.rpt.sm.SM_LST_DISCPLNRY_ACTN_IMPOS_AGAINST_SUPPL_RPT_NAME";
	public static final String SM_LST_COMPLAINTS_AGAINST_SUPPL_RPT_NAME = "lbl.rpt.sm.SM_LST_COMPLAINTS_AGAINST_SUPPL_RPT_NAME";
	public static final String SM_REPORT_RESIT = "Tarikh Resit";
	public static final String SM_REPORT_TRANSACTION = "Tarikh Transaksi";

	public static final String SM_REG_COMPANY_IN_MOF_RPT_VAL = "SM_001";
	public static final String SM_CATEGORY_STATISTIC_RPT_VAL = "SM_002";
	public static final String SM_CAT_STATIC_BY_YEAR_RPT_VAL = "SM_003";
	public static final String SM_CAT_STATIC_BY_STATE_RPT_VAL = "SM_004";
	public static final String SM_APPLICATION_VERIFICATION_RPT_VAL = "SM_005";
	public static final String SM_APPLICATION_APPROVED_RPT_VAL = "SM_006";
	public static final String SM_APPLICATION_REJECTED_RPT_VAL = "SM_007";
	public static final String SM_APPL_PEND_SITE_VISIT_RPT_VAL = "SM_008";
	public static final String SM_TOT_NUM_YEARLY_APPL_RPT_VAL = "SM_009";
	public static final String SM_LST_MONTH_WISE_RPT_VAL = "SM_010";
	public static final String SM_APPL_CLIENT_CHARTER_RPT_VAL = "SM_011";
	public static final String SM_LST_DAILY_NEW_APPL_RPT_VAL = "SM_012";
	public static final String SM_LST_COMP_ADDR_RPT_VAL = "SM_013";
	public static final String SM_SUPPL_STAT_BY_CATEGORY_RPT_VAL = "SM_014";
	public static final String SM_TOT_NUM_DAILY_APPL_RPT_VAL = "SM_015";
	public static final String SM_SUPPL_STAT_RPT_VAL = "SM_016";
	public static final String SM_BPK_USER_PROF_RPT_VAL = "SM_017";
	public static final String SM_BPK_ADMIN_PROF_RPT_VAL = "SM_018";
	public static final String SM_QUESTIONAIRE_RPT_VAL = "SM_019";
	public static final String SM_LAPORAN_TERIMAAN_HARIAN_RPT_VAL = "SM_020";
	public static final String SM_LIST_OF_PRE_EXPIRY_COMPANY_RPT_VAL = "SM_021";
	public static final String SM_REG_COMPANY_IN_MOF_DET_RPT_VAL = "SM_022";
	public static final String SM_REG_COMPANY_IN_MOF_PROJ_RPT_VAL = "SM_023";
	public static final String SM_REG_COMPANY_IN_MOF_CONT_RPT_VAL = "SM_024";
	public static final String SM_REG_COMPANY_IN_MOF_DET_CONT_RPT_VAL = "SM_025";
	public static final String SM_REG_COMPANY_IN_MOF_PROJ_CONT_RPT_VAL = "SM_026";
	public static final String SM_PAY_REGIS_FEE_RPT_VAL = "SM_027";
	public static final String SM_QUESTIONNAIRE_RESPONSE_RPT_VAL = "SM_028";
	public static final String SM_SITE_VISIT_APPL_REQUIRE_RPT_VAL = "SM_029";
	public static final String SM_SITE_VISIT_APPL_OFFICER_RPT_VAL = "SM_030";
	public static final String SM_SITE_VISIT_APPL_PERFORMED_RPT_VAL = "SM_031";
	public static final String SM_LIST_OF_APPLICATION_RPT_VAL = "SM_042";
	public static final String SM_LIST_OFFLINE_SUPP_DOC_RPT_VAL = "SM_043";
	public static final String SM_VIRTUAL_DAY_REPORT_RPT_VAL = "SM_044";
	public static final String SM_LIST_OF_COMPANY_SUPTYPE_RPT_VAL = "SM_045";
	public static final String SM_LIST_OF_COMP_DIR_OWN_CEO_RPT_VAL = "SM_046";
	public static final String SM_LIST_OF_DIR_OWN_CEO_COMPANY_RPT_VAL = "SM_047";
	public static final String SM_LIST_OF_COMP_SORT_ALPHA_RPT_VAL = "SM_048";
	public static final String SM_STATISTICS_COMPANYTYPE_CAP_RPT_VAL = "SM_049";
	public static final String SM_LIST_OF_PER_MORE_COMPANY_RPT_VAL = "SM_050";
	public static final String SM_LIST_OF_MOF_CANCELLATION_RPT_VAL = "SM_051";
	public static final String SM_COMPANY_ADDRESS_LIST_RPT_VAL = "SM_052";
	public static final String SM_BPK_USER_PROFILE_RPT_VAL = "SM_053";
	public static final String SM_BPK_ADMIN_PROFILE_RPT_VAL = "SM_054";
	public static final String SM_DAILY_EARNING_RECEIPT = "SM_055";
	public static final String SM_DELETE_PENDING_APPL = "SM_056";
	public static final String SM_SUPPLIER_PERFORMANCE = "SM_057";
	public static final String SM_PERSON_BRANCH_BANK = "SM_058";
	public static final String SM_THRESHOLD = "SM_059";
	public static final String SM_LIST_OF_USERS = "SM_060";
	public static final String SM_DAILY_APP_SUBMITTED = "SM_061";
	public static final String SM_THRESHOLD_VALUE = "SM_062";
	public static final String SM_LIST_OF_APPL_STATUS = "SM_063";
	public static final String SM_AGENCY_STATISTICAL = "SM_064";
	public static final String SM_LIST_OF_GST_RGSTRD_SUPPLIER = "SM_065";
	public static final String SM_COLLECTION_AND_TRANSACTION_DATE_RPT_VAL = "SM_071";
	public static final String SM_COLLECTION_PAYMENT_METHODS_RPT_VAL = "SM_072";
	public static final String SM_COLLECTION_CLASSIFICATION_CODE_RPT_VAL = "SM_073";
	public static final String SM_REVENUE_COLLECTION_RPT_VAL = "SM_074";
	public static final String SM_LIST_DAILY_RECIPT_RPT_VAL = "SM_075";
	public static final String SM_CATEGORY_OVERLAPPING_RPT_VAL = "SM_076";

	// SM - virtual certificate
	public static final String SM_VIRTUAL_VERIFICATION_jsp = "SM_VIRTUAL_VERIFICATION.jsp";
	public static final String SM_VIRTUAL_VERIFICATION_BUMI_jsp = "SM_VIRTUAL_VERIFICATION_BUMI.jsp";

	// SM - Supplier Profile
	public static final String SM_SUPPLIER_PROFILE_REPORT = "SM_APPLICATION_DETAILS.jsp";
	public static final String SM_SUPPLIER_UNREGIS_REPORT = "SM_APPL_DETAILS_UNREGIS.jsp";

	// CT Module reports start
	public static final String CT_LST_OF_CONTRACT_USERS_ALL_RPT_VAL = "CT_001";
	public static final String CT_LST_OF_CONTRACT_USERS_CONTRCT_NO_RPT_VAL = "CT_002";
	public static final String CT_LST_OF_CONTRACT_ALL_RPT_VAL = "CT_003";
	public static final String CT_LST_OF_CONTRACT_ACTIVE_RPT_VAL = "CT_004";
	public static final String CT_LST_OF_CONTRACT_INACTIVE_RPT_VAL = "CT_005";
	public static final String CT_CONTRACT_MONITORING_RPT_VAL = "CT_006";
	public static final String CT_LST_OF_ITEM_PRODUCT_RPT_VAL = "CT_007";
	public static final String CT_LST_OF_ITEM_SERVICE_RPT_VAL = "CT_008";
	public static final String CT_LST_OF_AMENDMENT_RPT_VAL = "CT_009";
	public static final String CT_LST_OF_UPDATE_RPT_VAL = "CT_010";
	public static final String CT_LST_OF_SUSPENSION_RPT_VAL = "CT_011";
	public static final String CT_LST_OF_TERMINATION_RPT_VAL = "CT_012";
	public static final String CT_LST_OF_SUPPLIER_RPT_VAL = "CT_013";
	public static final String CT_LST_OF_AGREEMENT_RPT_VAL = "CT_014";
	public static final String CT_LST_OF_SUPPLAGREEMENT_RPT_VAL = "CT_015";
	public static final String CT_LST_OF_CIRCULAR_RPT_VAL = "CT_016";
	public static final String CT_LST_OF_PANEL_CIRCULAR_NO_RPT_VAL = "CT_017";
	public static final String CT_FULFILMENT_STATUS_RPT_VAL = "CT_018";
	public static final String CT_CONTRACT_TRANSACTION_MONITORING_CC_RPT_VAL = "CT_019";
	public static final String CT_CONTRACT_UTILIZATION_MONITORING_CC_RPT_VAL = "CT_020";
	public static final String CT_STATEMENT_OF_ORDER_REPORT_CC_PANEL_RPT_VAL = "CT_021";
	public static final String CT_LIST_OF_CONTRACT_CC_RPT_VAL = "CT_022";
	
	// /CT-VALUES
	public static final String CT_TERMINATION_TYPE = "TERMINATE";

	// /sm

	public static final String AMP_SM_TO_DATE_EQ = "&PTODATE=";
	public static final String AMP_SM_FROM_DATE_EQ = "&PFROMDATE=";
	public static final String AMP_SM_MOF_ACC_END_TO_DATE_EQ = "&PEXTODATE=";
	public static final String AMP_SM_MOF_ACC_END_FROM_DATE_EQ = "&PEXFROMDATE=";
	public static final String AMP_SM_REP_TYPE_EQ = "&PTYPE=";
	public static final String AMP_SM_PAYMENT_TYPE_METHOD_EQ = "&PCHANNELNAME=";
	public static final String AMP_SM_OCDE_TYPE = "&PKOD=";
	public static final String AMP_SM_SUP_NAME_EQ = "&PCOMPANYNAME=";
	public static final String AMP_SM_MOF_REG_FEE_TYPE_EQ = "&PPAYTYPE1=";
	public static final String AMP_SM_PROCESSING_FEE_TYPE_EQ = "&PPAYTYPE2=";
	public static final String AMP_SM_SOFTCERTIFICATE_TYPE_EQ = "&PPAYTYPE3=";
	public static final String AMP_SM_REG_TYPE_EQ = "&PREGTYPE=";
	public static final String AMP_SM_MOF_REG_NO_EQ = "&PMOFNO=";
	public static final String AMP_SM_USER_ID_EQ = "&PUSER=";
	public static final String AMP_SM_REPORT_ID_EQ = "&PREPORTID=";
	public static final String AMP_SM_APPL_STATUS_EQ = "&PAPPLSTATUS=";
	public static final String AMP_SM_COMP_NAME_EQ = "&PCOMPANYNAME=";
	public static final String AMP_SM_MAIN_CATEGORY_EQ = "&PCATEGORYCODE=";
	public static final String AMP_SM_SUB_CATEGORY_EQ = "&PSUBCATEGORYCODE=";
	public static final String AMP_SM_SUB_DIV_CATEGORY_EQ = "&PSUBCATEGORYDIVISION=";
	public static final String AMP_SM_REG_STATUS_EQ = "&PREGSTATUSID=";
	public static final String AMP_SM_STATE_NAME_EQ = "&PSTATEID=";
	public static final String AMP_SM_DISTRICT_NAME_EQ = "&PDISTRICTID=";
	public static final String AMP_SM_YEAR_FROM_EQ = "&PYEARFROM=";
	public static final String AMP_SM_YEAR_TO_EQ = "&PYEARTO=";
	public static final String AMP_SM_SUPPLIER_TYPE_EQ = "&PSUPPTYPE=";
	public static final String AMP_SM_SUPPLIER_ZONE_NAME_EQ = "&PSUPPZONE=";
	public static final String AMP_SM_YEAR_EQ = "&PYEAR=";
	public static final String AMP_SM_APPL_TYPE_EQ = "&PAPPLTYPE=";
	public static final String AMP_SM_REPORT_TYPE_EQ = "&PREPORTTYPE=";
	public static final String AMP_SM_PRO_OFFICERNAME_EQ = "&PPROOFFICERNAME=";
	public static final String AMP_SM_RECEIPT_NO_EQ = "&PRECEIPTNO=";
	public static final String AMP_SM_PAYMENT_MENTHOD_EQ = "&PPAYMENTMENTHOD=";
	public static final String AMP_SM_PRO_TYPE_EQ = "&PPROCESSINGTYPE=";
	public static final String AMP_SM_APPLICATION_NO_EQ = "&PAPPLNO=";
	public static final String AMP_SM_SVO_NAME_EQ = "&PSVOUSERID=";
	public static final String AMP_SM_MOF_CERTIFICATE_SERIAL_NUMBER_EQ = "";
	public static final String AMP_SM_ROLE_EQ = "&PROLE=";
	public static final String AMP_SM_EP_NO_EQ = "&PEPNO=";
	public static final String AMP_SM_SUPP_DOC_MODE_EQ = "&PSUPPDOCMODE=";
	public static final String AMP_SM_SUPP_DOC_STATUS_EQ = "&PSUPPDOC=";
	public static final String AMP_SM_REG_NO_EQ = "&PREGNO=";
	public static final String AMP_SM_MOF_REG_NO_FRM_EQ = "&PFROMMOFNO=";
	public static final String AMP_SM_MOF_REG_NO_TO_EQ = "&PTOMOFNO=";
	public static final String AMP_SM_PAIDUP_CPTL_R_ACLTD_EQ = "&PUC_OR_AS=";
	public static final String AMP_SM_PAIDUP_CPTL_FROM_EQ = "&PFRPAIDUPCAPITAL=";
	public static final String AMP_SM_PAIDUP_CPTL_TO_EQ = "&PTOPAIDUPCAPITAL=";
	public static final String AMP_SM_PAYMENT_TYPE_EQ = "&PPAYTYPE=";
	public static final String AMP_SM_PERSONNEL_ROLE_EQ = "&PPERSONNELROLE=";
	public static final String AMP_SM_IDENT_TYPE_EQ = "&PIDENTYPE=";
	public static final String AMP_SM_IDENT_NO_EQ = "&PIDENNO=";
	public static final String AMP_SM_CANCEL_BY_EQ = "&PCANCELBY=";
	public static final String AMP_SM_BUS_TYPE_BY_EQ = "&PBUSTYPE=";
	public static final String AMP_SM_BUS_REG_TYPE_BY_EQ = "&PREGSTATUS=";
	public static final String AMP_SM_FROM_YEAR_EQ = "&PFROMYEAR=";
	public static final String AMP_SM_TO_YEAR_EQ = "&PTOYEAR=";
	public static final String AMP_SM_USER_NAME_EQ = "&PUSERNAME=";
	public static final String AMP_SM_REQ_ENQ_DATE_EQ = "&PDATE=";
	public static final String AMP_SM_PRFL_STS_EQ = "&PPRSTAT=";
	public static final String AMP_SM_GST_REGNO_EQ = "&PGSTREGNO=";
	public static final String AMP_SM_GST_STS_EQ = "&PGSTSTAT=";
	public static final String AMP_SM_PAYMENT_TO_EQ = "&PPAYMENTTO=";
	public static final String PEND_PRPR_SITE_VISIT = "Pending Prepare Site Visit Report";
	public static final String SITE_VISIT_COMPLTD = "Site Visit Report Completed";
	public static final String SM_REPORT_TYPE_RECEIVED = "RECEIVED";
	public static final String SM_REPORT_TYPE_APPROVED = "APPROVED";
	public static final String SM_REPORT_TYPE_REJECTED = "REJECTED";	
	public static final String KAMA = ",";
	public static final String SUPPL_CONSULT_TYPE = "J";
	public static final String SUPPL_CONTRACT_TYPE = "K";
	public static final String SUPPL_ALL_TYPE = "All";
	public static final String SUPPL_CONSULT_MOF_NO = "465-";
	public static final String SUPPL_CONTRACT_MOF_NO = "357-";
	public static final String PARAM_MODULE_TYPE = "REP";
	public static final String LA_VALUE="LA";
	public static final String AMP_EP_NO_EQ = "&epno=";

	/** The Constant AMP_TO_DATE_EQ. */
	public static final String AMP_TO_DATE_EQ = "&todt=";
	public static final String AMP_FROM_DATE_EQ = "&frdt=";
	public static final String AMP_SUPP_DOC_EQ = "&supportingdocci=";
	public static final String AMP_REG_NO_EQ = "&registrationno=";
	public static final String AMP_SUPP_DOC_STATUS_MODE_EQ = "&supportingdocmode=";
	public static final String AMP_DOC_STS_NAME_EQ = "&statusname=";
	public static final String AMP_APPLICATION_NO_EQ = "&applicationno=";
	public static final String AMP_USER_PRINTED_BY_EQ = "&printedby=";
	public static final String AMP_ZONE_NAME_EQ = "&zonename=";
	public static final String AMP_SUPPLIER_TYPE_EQ = "&suppliertype=";
	public static final String AMP_REG_MOF_FROM_EQ = "&frmofno=";
	public static final String AMP_REG_MOF_TO_EQ = "&tomofno=";
	public static final String AMP_COMP_NAME_EQ = "&companyname=";
	public static final String AMP_REG_TYPE_EQ = "&registrationtype=";
	public static final String AMP_REG_STATUS_EQ = "&registrationstatus=";
	public static final String AMP_PUC_N_AS_EQ = "&puc_or_as=";
	public static final String AMP_FRM_PUC_EQ = "&frpaidupcapital=";
	public static final String AMP_TO_PUC_EQ = "&topaidupcapital=";
	public static final String AMP_ACMLTD_FRM_TOT_SHARE_EQ = "&frtotalshare=";
	public static final String AMP_ACMLTD_TO_TOT_SHARE_EQ = "&tototalshare=";

	public static final String AMP_ROLE_EQ = "&role=";
	public static final String AMP_FRM_EXPIRY_DT_EQ = "&frexpdt=";
	public static final String AMP_TO_EXPIRY_DT_EQ = "&toexpdt=";
	public static final String AMP_IC_NO_EQ = "&icno=";
	public static final String AMP_PASSPORT_NO_EQ = "&ppno=";
	public static final String AMP_FRM_ESTABLISH_DT_EQ = "&frestablishdt=";
	public static final String AMP_TO_ESTABLISH_DT_EQ = "&toestablishdt=";
	public static final String AMP_STATE_NAME_EQ = "&statename=";
	public static final String AMP_FRM_APPL_DT_EQ = "&frapplicationdt=";
	public static final String AMP_TO_APPL_DT_EQ = "&toapplicationdt=";
	public static final String AMP_PAYMENT_TYPE_EQ = "&paymenttype=";
	public static final String AMP_APPL_TYPE_EQ = "&applicationtype=";
	public static final String AMP_APPL_STATUS_EQ = "&applicationstatus=";
	public static final String AMP_MOF_NO_EQ = "&mofno=";
	public static final String AMP_ZONE_ID_EQ = "zoneid=";
	public static final String AMP_FRM_PRNTD_DT_EQ = "printeddatefrom=";
	public static final String AMP_TO_PRNTD_DT_EQ = "printeddateto=";
	public static final String AMP_REPORT_ID_EQ = "reportid=";
	public static final String AMP_FROM_RELEASE_DATE_EQ = "&releasedate=";
	public static final String AMP_USER_ACCESS_ROLE_EQ = "&LOGIN=";
	//
	public static final String EMPTY_VAL = "";
	public static final String ZERO_VAL = "0";
	public static final String ONE_VAL = "1";

	public static final String AMP_REPORT_NAME = "&report=";
	public static final String AMP_QUOTE_ID_EQ = "&qtid=";
	public static final String AMP_ITM_ID_EQ = "&itemid=";
	public static final String AMP_ITM_NAME_EQ = "&itemname=";
	public static final String AMP_ITM_TYPE_EQ = "&itemtype=";
	public static final String AMP_ITM_STATUS_EQ = "&itemstatus=";
	public static final String AMP_UNSPSC_CODE_EQ = "&unspsccode=";
	public static final String AMP_UNSPSC_TIT_EQ = "&unspsctitle=";
	public static final String AMP_EXT_CODE_EQ = "&extentioncode=";
	public static final String AMP_ITM_CAT_EQ = "&itemcategory=";
	public static final String AMP_PROOF_IDNT_EQ = "&proofidentity=";
	public static final String AMP_BUSI_REG_NO_EQ = "&businessregno=";
	public static final String AMP_PTJ_EQ = "&ptj=";
	public static final String AMP_CONTR_NO_EQ = "&contractno=";
	public static final String AMP_SST_EQ = "&sst=";
	public static final String AMP_JABATAN_EQ = "&jabatan=";
	public static final String AMP_MINISTRY_EQ = "&ministry=";
	public static final String AMP_SUPPLIER_EQ = "&supplier=";
	public static final String AMP_SUPPLIER_ID_EQ = "&supplierid=";
	public static final String AMP_STATUS_EQ = "&status=";
	public static final String AMP_DURATION_EQ = "&duration=";
	public static final String AMP_REC_OFFCR_EQ = "&recievingofficer=";
	public static final String AMP_DEL_ADDR_EQ = "&deliveryaddress=";
	public static final String AMP_FL_REC_NOTE_NO_EQ = "&flrecnoteno=";
	public static final String AMP_CONTR_TYPE_EQ = "&contracttype=";
	public static final String AMP_REQSNR_EQ = "&requisitioner=";
	public static final String AMP_MTO_CONTR_EQ = "&mtocontract=";
	public static final String AMP_INVOICE_NO_EQ = "&invoiceno=";
	public static final String AMP_DEDUCT_TYPE_EQ = "&deductiontype=";
	public static final String AMP_CR_NO_EQ = "&crno=";
	public static final String AMP_APPROVER_NAME_EQ = "&approvername=";
	public static final String AMP_DOC_TYPE_EQ = "&documenttype=";
	public static final String AMP_CRITERIA_EQ = "&criteria=";
	public static final String AMP_SCORING_EQ = "&scoring=";
	public static final String AMP_PKP_NO_EQ = "&pkpno=";
	public static final String AMP_DEL_ORDER_NO_EQ = "&deliveryorderno=";
	public static final String AMP_ITM_CODE_EQ = "&itemcode=";
	public static final String AMP_PO_AMT_EQ = "&poamount=";
	public static final String AMP_PR_NO_EQ = "&prno=";
	public static final String AMP_PMO_EQ = "&pmo=";
	public static final String AMP_EXT_TYPE_EQ = "&PTYPE=";
	public static final String AMP_EXT_BRAND_EQ = "&PBRAND=";
	public static final String AMP_EXT_MEASURMENT_EQ = "&PMEAS=";
	public static final String AMP_EXT_COLOUR_EQ = "&PCOLOR=";

	public static final String AMP_CREAT_FRM_DT_EQ = "&CREAFRDT=";
	public static final String AMP_CREAT_TO_DT_EQ = "&CREATODT=";
	public static final String AMP_PROCURE_METHOD_EQ = "&PM=";
	public static final String AMP_PROCURE_STATUS_EQ = "&STATUS=";
	public static final String AMP_DESK_OFFCR_EQ = "&DOUSERID=";
	public static final String AMP_PROCURE_PTJ_EQ = "&ORGPROFILEID=";
	public static final String AMP_LOGIN_EQ = "&LOGIN=";
	public static final String AMP_PUB_FRM_DT_EQ = "&PUBFRDT=";
	public static final String AMP_PUB_TO_DT_EQ = "&PUBTODT=";
	public static final String AMP_PREPORT_ID_EQ = "&PREPORTID=";
	public static final String AMP_PUSER_ID_EQ = "&PUSER=";
	// cm
	public static final String CM_AMP_ITM_NAME_EQ = "&PITEMNAME=";
	public static final String CM_AMP_MOF_NO_EQ = "&PSUPPMOFNO=";
	public static final String CM_AMP_IC_NO_EQ = "&PICNO=";
	public static final String CM_AMP_BUSI_REG_NO_EQ = "&PREGNO=";
	public static final String CM_AMP_EP_NO_EQ = "&PEPNO=";
	public static final String CM_AMP_TO_DATE_EQ = "&PTODATE=";
	public static final String CM_AMP_FROM_DATE_EQ = "&PFROMDATE=";
	public static final String CM_AMP_COMP_NAME_EQ = "&PCOMPANYNAME=";
	public static final String CM_AMP_EXT_CODE_EQ = "&PEXTENSIONCODE=";
	public static final String CM_AMP_ITEM_TYPE_EQ = "&PITEMTYPE=";
	public static final String CM_AMP_REG_TYPE_EQ = "&PREGISTRATIONTYPE=";
	public static final String CM_AMP_SUPPOR_ADMIN_EQ = "&PSUPPORADMIN=";
	public static final String CM_AMP_ITEM_STATUS_EQ = "&PITEMSTATUS=";
	public static final String CM_AMP_REPORTID_EQ = "&PREPORTID=";
	public static final String CM_AMP_USER_EQ = "&PUSER=";
	public static final String CM_AMP_UNSPSC_CODE_EQ = "&PUNSPSCCODE=";
	public static final String CM_AMP_UNSPSC_TITLE_EQ = "&PUNSPSCTITLE=";
	public static final String CM_AMP_EXTENSION_EQ = "&PEXTENSION=";
	public static final String CM_AMP_PGROUP_EQ = "&PGROUP=";
	public static final String CM_AMP_PEXT_EQ = "&PEXT=";
	public static final String CM_AMP_REQUEST_NO_EQ = "&PREQUESTNO=";
	// sc
	public static final String SC_AMP_REQUISENER_EQ = "&REQUIUSRID=";
	public static final String SC_AMP_SUPPLIER_EQ = "&SUPP=";
	public static final String SC_AMP_STATUS_EQ = "&STATUS=";
	public static final String SC_AMP_PI_NO_EQ = "&PINO=";
	public static final String SC_AMP_PRCESS_RPT_GROUP_EQ = "&PGROUP=";
	public static final String SC_AMP_QT_NO_EQ = "&QUOTENO=";
	public static final String SC_AMP_PROC_TYPE_EQ = "&PROTYPE=";
	public static final String SC_AMP_PID_NO_EQ = "&PIDNO=";
	public static final String SC_AMP_CON_TYPE_EQ = "&CONTYPE=";
	public static final String SC_AMP_CON_NO_EQ = "&CONNO=";
	public static final String SC_AMP_CON_M_EQ = "&CONNM=";
	public static final String SC_AMP_RQ_NO_EQ = "&RQNO=";
	public static final String SC_AMP_APPROVER_NAIK_TARAF_EQ = "&APPROVER_NAIK_TARAF=";
	public static final String SC_AMP_APPROVERNT_EQ = "&APPROVERNT=";
	public static final String SC_AMP_APPROVER_EQ = "&APPROVER=";
	public static final String SC_AMP_DESK_OFFICER_EQ = "&DESK_OFFICER=";
	public static final String SC_AMP_SIMPL_QUOTE_TYPE_EQ = "&SQTYPE=";
	public static final String SC_AMP_LOA_NO_EQ = "&LOANUM=";
	public static final String DESKOFFR = "DESKOFFR";
	public static final String APPROVERNT = "APPROVERNT";
	public static final String REQUISITIONER = "REQUISITIONER";
	// fl
	public static final String FL_AMP_FROM_DATE_EQ = "&PFROMDATE=";
	public static final String FL_AMP_TO_DATE_EQ = "&PTODATE=";
	public static final String FL_AMP_COTRACT_ORDER_EQ = "&PCONO=";
	public static final String FL_AMP_COTRACT_REQUEST_EQ = "&PCRNO=";
	public static final String FL_AMP_PURCHASE_ORDER_EQ = "&PPONO=";
	public static final String FL_AMP_TREASURY_CIRCULAR_EQ = "&PKPNO=";
	public static final String FL_AMP_FULLFILLEMENT_RECEIVE_EQ = "&FRNNO=";
	public static final String FL_AMP_COTRACT_TYPE_EQ = "&PCONTYPE=";
	public static final String FL_AMP_DELI_ADDR_EQ = "&PDOADD=";
	public static final String FL_AMP_DELI_ORD_NO_EQ = "&PDONO=";
	public static final String FL_AMP_SUP_DELI_ORD_NO_EQ = "&PSUPPDONO=";
	public static final String FL_AMP_PKP_NO_EQ = "&PPKPNO=";
	public static final String FL_AMP_FL_REC_NOTE_EQ = "&PFRNNO=";
	public static final String FL_AMP_FL_REC_OFF_EQ = "&PRECOFF=";
	public static final String FL_AMP_STATUS_EQ = "&PSTATUS=";
	public static final String FL_AMP_APPROVER_NAME_EQ = "&PAPPROVER=";
	public static final String FL_AMP_REQUI_NAME_EQ = "&PREQUEST=";
	public static final String FL_AMP_ACKNOWLEDGE_NAME_EQ = "&PACKOFF=";
	public static final String FL_AMP_PAYMENT_NAME_EQ = "&PPAYOFF=";
	public static final String FL_AMP_PHY_CONTRACT_NAME_EQ = "&PPHYCON=";
	public static final String FL_AMP_PHY_LOA_NAME_EQ = "&PPHYLOA=";
	public static final String FL_AMP_CONTRACT_NAME_EQ = "&PCRNO=";	
	public static final String FL_AMP_SUPPL_NAME_EQ = "&PSUPP=";
	public static final String FL_AMP_MINISTRY_EQ = "&PMINIS=";
	public static final String FL_AMP_PEGAWAI_PENGAWAI_EQ = "&PFPP";
	public static final String FL_AMP_KUMPULAN_EQ = "&PFJA";
	public static final String FL_AMP_PTJ_NAME_EQ = "&PPTJ=";
	public static final String FL_AMP_SST_NO_EQ = "&PLOA=";
	public static final String FL_AMP_CONTRACT_NO_EQ = "&PCONTRACTNO=";
	public static final String FL_AMP_CONTRACT_SYSTEM_EQ = "&PPKPNAME=";
	public static final String FL_AMP_REPORT_ID_EQ = "&PREPORTID=";
	public static final String FL_AMP_USER_ID_EQ = "&PUSER=";
	public static final String FL_AMP_STOP_INSTR_NO_EQ = "&PSINO=";
	public static final String FL_AMP_INV_NO_EQ = "&PINVNO=";
	public static final String FL_AMP_SUPPL_INV_NO_EQ = "&PSUPPINVNO=";
	public static final String FL_AMP_PAYMENT_ADV_NO_EQ = "&PPANO=";
	public static final String FL_AMP_ITM_NAME_EQ = "&PITEM=";
	public static final String FL_AMP_DOC_TYPE_EQ = "&PDOCTYPE=";
	// CT
	public static final String CT_AMP_CONTRACT_NO_EQ = "&PCONTRACTNO=";
	public static final String CT_AMP_PHYSICALCONTRACT_NO_EQ = "&PPHYSICALCONTRACTNO=";
	public static final String CT_AMP_AGREEMENT_EQ = "&PAGREEMENT=";
	public static final String CT_AMP_STATUS_EQ = "&PSTATUS=";
	public static final String CT_AMP_GROUP_EQ = "&PGROUP=";
	public static final String CT_GROUP_VAL_ALL = "ALL";
	public static final String CT_GROUP_VAL_CNTRNO = "CONTRACT_NO";
	public static final String CT_GROUP_VAL_ACTIVE = "ACTIVE";
	public static final String CT_GROUP_VAL_INACTIVE = "INACTIVE";
	public static final String CT_AMP_COMPLETE_EQ = "&PCOMPLETE=";
	public static final String CT_AMP_PPRODUCTSERVICE_EQ = "&PPRODUCTSERVICE=";
	public static final String CT_AMP_PLOANO_EQ = "&PLOANO=";
	public static final String CT_AMP_PLOAPHYSICALNO_EQ = "&PLOAPHYSICALNO=";
	public static final String CT_AMP_PMINISTRY_EQ = "&PMINIS=";
	public static final String CT_AMP_PPEGAIPENGAWAI_EQ = "&PFPP=";
	public static final String CT_AMP_PKUMPULAN_EQ = "&PFJA=";
	
	/*
	 * public static final String CT_AMP_PPEGAWAI_PENGAWAI_EQ="&PFPP"; public
	 * static final String CT_AMP_PKUMPULAN_EQ="&PFJA";
	 */
	public static final String CT_AMP_PPTJNAME_EQ = "&PPTJ=";
	public static final String CT_AMP_PROCUREMENTMODE_EQ = "&PPROCUREMENTMODE=";
	public static final String CT_AMP_USERTYPE_EQ = "&PUSERTYPE=";
	public static final String CT_AMP_SUBTYPE_EQ = "&PUSERSUBTYPE=";
	public static final String CT_AMP_CIRCULAR_NO_EQ = "&PCIRCULARNO=";
	public static final String CT_AMP_CIRCULAR_NAME_EQ = "&PCIRCULARNAME=";
	public static final String CT_AMP_CIRCULAR_TYPE_EQ = "&PCIRCULARTYPE=";
	public static final String CT_AMP_CIRCULAR_FROM_DATE_EQ = "&PCIRCEFFFROMDATE=";
	public static final String CT_AMP_CIRCULAR_TO_DATE_EQ = "&PCIRCEFFTODATE=";
	public static final String CT_AMP_SUPPLIER_NAME_EQ = "&PSUPPLIERNAME=";
	public static final String CT_AMP_TERMINATION_TYPE_EQ = "&PTERMINATIONTYPE=";
	// PM
	public static final String PM_AMP_SERVICE_TYPE_EQ = "&SERVICETYPEID=";
	public static final String PM_AMP_FINORG_NAME_EQ = "&FINORGNAME=";
	public static final String PM_AMP_BIZREG_NO_EQ = "&BIZREGNO=";
	public static final String PM_AMP_STATUS_EQ = "&PSTATUS=";
	public static final String AMP_PM_FROM_DATE_EQ = "&PFROMDATE=";
	public static final String AMP_PM_TO_DATE_EQ = "&PTOMDATE=";
	public static final String AMP_PM_ORG_CATEGORY_EQ = "&PCATTYPE=";
	public static final String AMP_PM_EP_NO_EQ = "&PEPNO=";
	public static final String AMP_PM_ORG_TYPE_EQ = "&PORGTYPE=";
	public static final String AMP_PM_ORG_NAME_EQ = "&PORGNAME=";
	public static final String AMP_PM_ORG_CODE_EQ = "&PORGCODE=";
	public static final String AMP_PM_ITEM_NAME_EQ = "&PITEMNAME=";
	public static final String AMP_PM_ITEM_CODE_EQ = "&PITEMCODE=";
	public static final String AMP_PM_COMPANY_NAME_EQ = "&PCOMPANYNAME=";
	public static final String AMP_PM_IDENTIFICATION_NO_EQ = "&PIDENNO=";
	public static final String AMP_PM_USERNAME_EQ = "&PUSERNAME=";
	public static final String AMP_PM_ROLE_CODE_EQ = "&PROLECODE=";
	public static final String AMP_PM_CONTRACT_NO_EQ = "&PCONTRACTNO=";
	public static final String AMP_PM_PHYSICAL_CONTRACT_NO_EQ = "&PPHYSICALCON=";
	public static final String AMP_PM_LOA_NO_EQ = "&PLOA =";
	public static final String AMP_PM_PHYSICAL_LOA_NO_EQ = "&PPHYSICALLOA=";
	public static final String AMP_PM_SUPP_ZONE_EQ = "&PSUPPZONE=";
	public static final String AMP_PM_SUPP_PROFILE_STATUS_EQ = "&PPRSTAT=";

	public static final String FEDERAL_GOVT = "1";
	public static final String OTHERS = "2";

	public static final String EXC_PREV_URL = "http://ngepdevrps01.ngep.com:8891/reports/rwservlet?server=rptsvr_ngepdevrps01_asinst_2";
	public static final String EXC_NEXT_URL = "&destype=cache&desformat=pdf&userid=ngep_dev/ngep_dev@ngepdb";
	public static final String PLZ_SEL_ONE_RPT_SEARCH = "lbl.rpt.search.PLZ_SEL_ONE_RPT_SEARCH";
	public static final String PLZ_SEL_ONE_RPT_CRITERIA_SEARCH = "lbl.rpt.search.criteria.PLZ_SEL_ONE_RPT_CRITERIA_SEARCH";
	public static final String PLZ_ENTER_ONE_RPT_CRITERIA_SEARCH = "lbl.rpt.search.criteria.PLZ_ENTER_ONE_RPT_CRITERIA_SEARCH";
	public static final String PLZ_ENTER_ONE_RPT_CRITERIA_SEARCH_MALAY = "Sila masukkan sekurang-kurangnya satu kriteria untuk penjanaan";
	public static final String CATLOG_ADMIN = "Catalogue Administrator";
	public static final String SUPPL = "Supplier";
	public static final String CODIF_ADMIN = "Codification Administrator";
	public static final String CODIF_WORK_GROUP = "Codification Working Group";
	public static final String URL_RPT_DATE_FORMAT = "dd/MM/YYYY";
	public static final String URL_RPT_DATE_ANOTHER_FORMAT = "dd-MMM-yyyy";
	public static final String FORM_NEW_TAB = "_blank";
	public static final String FORM_SELF = "_self";
	public static final String RPT_REG_BASIC = "Basic";
	public static final String RPT_REG_MOF = "MOF";
	public static final String RPT_REG_ALL = "All";
	public static final String RPT_REG_BASIC_TO_MOF = "UPGRADE FROM BASIC TO MOF ACCOUNT";
	public static final String PLZ_SEL_BOTH_DTS_RPT_SEARCH = "lbl.rpt.search.date.PLZ_SEL_BOTH_DTS_RPT_SEARCH";
	public static final String NOT_VALID_QOTATION_NO = "lbl.rpt.search.criteria.NOT_VALID_QUOTATION_NO";
	public static final String NUMERIC_CHAR_VALID_ONLY = "lbl.rpt.search.criteria.NUMERIC_CHAR_VALID_ONLY";
	public static final String NUMERIC_CHAR_PATTERN_ONLY = "^[a-zA-Z0-9]+$";
	public static final String NUMERIC_PATTERN_ONLY = "^[0-9]+$";
	public static final String SUPPLIER_USER = "SUPPLIER_USER";
	public static final String SUPPLIER_ADMIN = "SUPPLIER_ADMIN";
	public static final String UPS_SDO = "UPS_SDO";
	public static final String UPS_SDO_HQ = "UPS_SDO_HQ";
	public static final String UPS_SDO_SA = "UPS_SDO_SA";
	public static final String UPS_SDO_SK = "UPS_SDO_SK";
	public static final String UPS_PO = "UPS_PO";
	public static final String UPS_PO_HQ = "UPS_PO_HQ";
	public static final String UPS_PO_SA = "UPS_PO_SA";
	public static final String UPS_PO_SK = "UPS_PO_SK";
	public static final String AGENCY = "AGENCY";
	public static final String EP_COORDINATOR = "EP_COORDINATOR";
	public static final String UPS_SVC_HQ = "UPS_SVC_HQ";
	public static final String UPS_SVC_SA = "UPS_SVC_SA";
	public static final String UPS_SVC_SK = "UPS_SVC_SK";
	public static final String UPS_SVC = "UPS_SVC";
	public static final String UPS_SVO_HQ = "UPS_SVO_HQ";
	public static final String UPS_SVO_SA = "UPS_SVO_SA";
	public static final String UPS_SVO_SK = "UPS_SVO_SK";
	public static final String UPS_SVO = "UPS_SVO";
	public static final String DISC_SECRETARY_HQ = "DISC_SECRETARY_HQ";
	public static final String DISC_SECRETARY_SA = "DISC_SECRETARY_SA";
	public static final String DISC_SECRETARY_SK = "DISC_SECRETARY_SK";
	public static final String DISC_SECRETARY = "DISC_SECRETARY";
	public static final String NO_URL_PROVIDED_FOR_THIS_RPT = "lbl.rpt.search.criteria.NO_URL_PROVIDED_FOR_THIS_RPT";
	public static final String RPT_IN_VALID = "reportInValid";
	public static final String RPT_TRUE = "true";
	public static final String RPT_FALSE = "false";
	public static final String TO_DT_GTR_THN_FRM_DT = "lbl.rpt.search.date.TO_DT_GTR_THN_FRM_DT";
	public static final String AMP_NO_OF_DAYS_EQ = "&noofdays=";
	public static final String RECENT_PRICE_RPT_PREV_URL = "http://ngepdevrps01.ngep.com:8891/reports/rwservlet?&report=";
	public static final String RECENT_PRICE_RPT_NEXT_URL = "&destype=cache&desformat=pdf&cmdkey=key1";

	public static final String PARAM_SPACE = " ";
	public static final String PARAM_IFON = "-";
	public static final String PARAM_SEPARATOR = "&";
	public static final String VALUE_SEPARATOR = "=";
	public static final SimpleDateFormat REPORT_DATE_TIME = new SimpleDateFormat(
			"yyMMddHHmmss");

	// Report Parameter Key
	public static final String PARAM_KEY_REPORT = "report";
	public static final String PARAM_KEY_ITEM_ID = "itemid";
	public static final String PARAM_KEY_SUPPLIER_ID = "supplierid";
	public static final String PARAM_KEY_CM_SUPPLIER_ITEM_ID = "suppitemid";
	public static final String PARAM_KEY_UOM_ID = "uomid";
	public static final String PARAM_KEY_USER_ID = "puser";
	public static final String PARAM_KEY_NO_OF_DAYS = "noofdays";
	public static final String PARAM_DOC_NO = "PNO"; 
	public static final String PARAM_USER_ID = "PUSER";
	public static final String PARAM_QUOTE_ID_EQ = "qtid";
	public static final String PARAM_KEY_REPORT_ID = "preportid";
	public static final String PARAM_RELIEF_ORDER_DOC_NO = "PRELIEFORDERNO";
	public static final String PARAM_DOC_ID = "PDOCID"; 
	public static final String PARAM_DOC_TYPE = "PDOCTYPE"; 
	public static final String PARAM_ESTIMATED_AMOUNT = "PESTAMT";
	

	// Report parameter key for s1 market research report
	public static final String PARAM_BIZ_REG_NO = "PBIZREGNO";
	public static final String PARAM_DISTRICT_ID = "PDISTRICT";
	public static final String PARAM_STATE_ID = "PSTATEID";
	public static final String PARAM_ITEM_CODE = "PITEMCODE";
	public static final String PARAM_ITEM_NAME = "PITEMNAME";
	public static final String PARAM_MOF_NO = "PMOFNO";
	public static final String PARAM_REQUEST_ITEM_ID = "PREQUESTITEMID";
	public static final String PARAM_IS_SAVED_FLAG = "PSAVE";
	public static final String PARAM_SUPPLIER_NAME = "PSUPP";
	public static final String PARAM_UOM_ID = "PUOMID";
	public static final String PARAM_EP_NO = "PEPNO";
	public static final String PARAM_SUPP_ITEM_ID = "PSUPPITEMID";
	public static final String NO_REPORT_ID = "This Report, is not available...";
	public static final String SLASH_REPRT_VIEW_SERVLET = "/ReportViewServlet";
	public static final String GENERATED_URL = "generatedUrl";
	public static final String REPORT_VIEW_URL = "reportViewUrl";
	public static final String PM_MODULE_SER_OBJ = "pm";
	public static final String RPT_MODULE_SER_OBJ = "rpt";
	public static final String RPT_EIFON = " - ";
	public static final String ENG_LANG = "en";
	public static final String MALAY_LANG = "ms";
	public static final String LABEL_LANG = "lbl.rpt.language";
	public static final String APPROVER_NAIK_TARAF = "APPROVER_NAIK_TARAF";
	public static final String APPROVER = "APPROVER";
	public static final String RN_APPROVER = "RN_APPROVER";
	public static final String DESK_OFFICER = "DESK_OFFICER";
	public static final String SUB_MOD_S1 = "S1";
	public static final String BPK_APPROVER = "BPK_APPROVER";
	public static final String COMMITTEE_APPROVER = "COMMITTEE_APPROVER";
	public static final String PUBLICATION_APPROVER = "PUBLICATION_APPROVER";
	public static final String PARAM_QUANTITY = "PQTY";

	// Supplier Management
	public static final String DOTJSP = ".jsp";
	public static final String CONTENT_DISPOSITION = "Content-Disposition";
	public static final String ATTACHMENTNFILENAMEEQUIFON = "attachment; filename=\"";
	public static final String UNDERSCORE = "_";
	public static final String DOTPDFEIFON = ".pdf\"";
	public static final String CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";
	public static final String BINARY = "binary";
	public static final String APPLICATIONEIFONPDF = "application/pdf";
	// SM - JSP
	public static final String SM_PAYMENT_RECEIPT_JSP = "sm_payment_receipt.jsp";

	// SM - Report Parameter Key
	public static final String PARAM_PAYMENT_ID = "pmntid";
	public static final String URL_NOT_CONNECTED = "lbl.rpt.search.URL_NOT_CONNECTED";
	public static final String REPORTS_NOT_AVAILABLE_BY_MODULE = "lbl.rpt.search.REPORTS_NOT_AVAILABLE_BY_MODULE";
	public static final String REPORT_PAGE = "reportcontentmain";
	public static final String PLZ_PROV_VALID_UNSPCCODE = "lbl.rpt.search.PLZ_PROV_VALID_UNSPCCODE";
	public static final String EXT_TYPE = "Type";
	public static final String EXT_BRAND = "Brand";
	public static final String EXT_MEASUREMENT = "Measurement";
	public static final String SM_PAYMENT_GOV = "Government";
	public static final String SM_PAYMENT_CDC = "CDC";

	/** The Constant EXT_COLOUR. */
	public static final String EXT_COLOUR = "Colour";
	public static final String ROLE_CODE_SUP = "SUP";
	public static final String ROLE_CODE_MIN_CONTRACT_ADMIN = "MIN_CONTRACT_ADMIN";
	public static final String ROLE_CODE_BPK_CCA = "BPK_CCA";
	public static final String ROLE_CODE_BPK_PCA = "BPK_PCA";
	public static final String ROLE_CODE_COLLECTOR_CDC = "COLLECTOR_CDC";
	public static final String ROLE_CODE_COLLECTOR_GOV = "COLLECTOR_GOV";
	public static final String ROLE_CODE_BPK_OFFICER = "BPK_OFFICER";
	public static final String ROLE_CODE_REQUISITIONER = "REQUISITIONER";
	public static final String ROLE_CODE_APPROVER = "APPROVER";
	public static final String ROLE_CODE_FL_APPROVER = "FL_APPROVER";
	public static final String ROLE_CODE_CT_APPROVER = "CT_APPROVER";
	public static final String ROLE_CODE_RN_APPROVER = "RN_APPROVER";
	public static final String ROLE_CODE_RECEIVING_OFFICER = "RECEIVING_OFFICER";
	public static final String ROLE_CODE_ACKNOWLEDGE_OFFICER = "ACKNOWLEDGE_OFFICER";
	public static final String ROLE_CODE_PAY_OFFICER = "PAY_OFFICER";
	public static final String ROLE_CODE_PLAN_APPROVER = "PLAN_APPROVER";
	public static final String ROLE_CODE_FULFILMENT_APPROVER = "FULFILMENT_APPROVER";
	public static final String STATUS_CANCELLED="Cancelled";

	public static final String ROLE_CODE_UPS_PO = "UPS_PO";
	public static final String ROLE_CODE_UPS_APPROVER = "UPS_APPROVER";
	public static final String ROLE_CODE_UPS_ADMIN = "UPS_ADMIN";
	public static final String ROLE_CODE_UPS_SVO = "UPS_SVO";
	public static final String ROLE_CODE_UPS_SVC = "UPS_SVC";
	public static final String ROLE_CODE_UPS_SDO = "UPS_SDO";
	public static final String ROLE_CODE_EP_ADMIN = "EP_ADMIN";
	public static final String ROLE_CODE_PTJ_ADMIN = "PTJ_ADMIN";
	public static final String ROLE_CODE_MOF_SUPPLIER_ADMIN = "MOF_SUPPLIER_ADMIN";
	public static final String ROLE_CODE_MOF_SUPPLIER_USER = "MOF_SUPPLIER_USER";
	public static final String ROLE_CODE_BASIC_SUPPLIER_USER = "BASIC_SUPPLIER_USER";
	public static final String ROLE_CODE_BASIC_SUPPLIER_ADMIN = "BASIC_SUPPLIER_ADMIN";

	public static final String DOC_TYPE_CR = "CR";
	public static final String DOC_TYPE_CO = "CO";
	public static final String DOC_TYPE_DO = "DO";
	public static final String DOC_TYPE_FN = "FN";
	public static final String DOC_TYPE_IN = "IN";
	public static final String DOC_TYPE_PA = "PA";
	public static final String DOC_TYPE_DN = "DN";
	public static final String DOC_TYPE_PO = "PO";
	public static final String DOC_TYPE_PR = "PR";
	public static final String DOC_TYPE_SD = "SD";
	public static final String DOC_TYPE_FRN = "FRN";
	public static final String MINISTRY_CONTRACT_ADMIN = "Ministry Contract Admin";
	public static final String MIN_CONTRACT_ADMIN = "MIN_CONTRACT_ADMIN";
	public static final String APPROVER_LBL = "Approver";
	public static final String PARAM_CODE_TYPE_FPJ = "FPJ";
	public static final String PARAM_CODE_TYPE_FMI = "FMI";
	public static final String BUDGET_APPROVER = "BUDGET_APPROVER";
	// ERROR N EXCEPTIONS
	public static final String ERR_999000_MORTHAN_TEN = "lbl.rpt.ERR_999000_MORTHAN_TEN";
	public static final String TO_DT_SHOULD_NOT_MORE_THAN_ONE_YEAR = "lbl.rpt.ERR_999001_TO_DT_SHOULD_NOT_MORE_THAN_ONE_YEAR";
	
}
