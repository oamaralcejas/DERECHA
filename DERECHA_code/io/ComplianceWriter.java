package lu.svv.saa.linklaters.dpa.io;

import static org.apache.uima.fit.util.JCasUtil.select;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.component.JCasAnnotator_ImplBase;

import lu.svv.saa.linklaters.dpa.type.ComplianceResults;
import lu.svv.saa.linklaters.dpa.utils.PARAMETERS_VALUES;


public class ComplianceWriter extends JCasAnnotator_ImplBase  implements PARAMETERS_VALUES {
	private static final DecimalFormat df = new DecimalFormat("0.00");
	
	public static final String PARAM_INPUT_PATH = "inputPath";
	  @ConfigurationParameter(name = PARAM_INPUT_PATH, mandatory = true,
	      description = "Input path for ground truth")
	 private String inputPath;
	
	 double r10, r11, r12, r13, r15, r16, r17, r18, r19, r20, r21, r22, r23, r24, r25, r26, r27, r28, r29, r30, r31, r32, r33, r34, r35,
	 		r36, r37, r38, r39, r40, r41, r42, r43, r44, r45, r46;
	 double lcr10, lcr11, lcr12, lcr13, lcr15, lcr16, lcr17, lcr18, lcr19, lcr20, lcr21, lcr22, lcr23, lcr24, lcr25, lcr26, lcr27, lcr28,
	  		lcr29, lcr30, lcr31, lcr32, lcr33, lcr34, lcr35;
	 int r10c=0, r11c=0, r12c=0, r13c=0, r15c=0, r16c=0, r17c=0, r18c=0, r19c=0, r20c=0, r21c=0, r22c=0, r23c=0, r24c=0, r25c=0, r26c=0,
		 r27c=0, r28c=0, r29c=0, r30c=0, r31c=0, r32c=0, r33c=0, r34c=0, r35c=0, r36c, r37c, r38c, r39c, r40c, r41c, r42c, r43c, r44c,
		 r45c, r46c;
	 int lcr10c=0, lcr11c=0, lcr12c=0, lcr13c=0, lcr15c=0, lcr16c=0, lcr17c=0, lcr18c=0, lcr19c=0, lcr20c=0, lcr21c=0, lcr22c=0, lcr23c=0,
		 lcr24c=0, lcr25c=0, lcr26c=0, lcr27c=0, lcr28c=0, lcr29c=0, lcr30c=0, lcr31c=0, lcr32c=0, lcr33c=0, lcr34c=0, lcr35c=0;
	
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
	  HashMap<String, List<String>> groundTruth = new HashMap<String, List<String>>();
	  boolean break_flag = false;
	  boolean cr_flag = false;
	  int tp_tc = 0;
	  boolean tn_flag = true;
	  int tn = 0, tp = 0, fp = 0, fn = 0;
	  List<String> pWIr = new ArrayList<String>();
	  List<String> pRIr = new ArrayList<String>();
	  List<String> wIr = new ArrayList<String>();
	  int req_count = 0, cd_b_01 = 0, cd_b_02 = 0, cd_b_03 = 0, cd_b_04 = 0, cd_b_05 = 0;
	  try {
		  String doc = inputPath.replace(PATH_CSV, "");
		  BufferedWriter writer = new BufferedWriter(new FileWriter(PATH_OUTPUT + doc.replace(".csv", "_cd_info.txt")));
		  //BufferedWriter writer = new BufferedWriter(new FileWriter(PATH_OUTPUT + DOC.replace(".csv", "_cd_info.txt")));
		  writer.write("\n Identified Sentences:\n");
		  writer.close();
	  } catch (IOException e1) {
		  // TODO Auto-generated catch block
		  e1.printStackTrace();
	  }
	  System.out.printf("%n----------------------------------------------%n");
	  //System.out.println("Lower Confidence Sentences:");
	  for (ComplianceResults crs : select(aJCas, ComplianceResults.class)) {
		  System.out.printf("%n--Sentence " + crs.getSentenceID() + ": " + crs.getSentenceText() + "--%n");
		  System.out.println("  Requirement: " + crs.getRequirementID());
		  System.out.println("  Confidence Degree: " + crs.getConfidenceDegree());
		  System.out.println("  Matching Degree: " + crs.getMatchingDegree());
		  System.out.println("  Missing Roles: " + crs.getMissingRoles());
		  cdallRequirements(crs.getRequirementID(), crs.getConfidenceDegree());
		  try {
			  String doc = inputPath.replace(PATH_CSV, "");
			  BufferedWriter writer = new BufferedWriter(new FileWriter(PATH_OUTPUT + doc.replace(".csv", "_cd_info.txt"), true));
			  //BufferedWriter writer = new BufferedWriter(new FileWriter(PATH_OUTPUT + DOC.replace(".csv", "_cd_info.txt"), true));
			  writer.write("\n--Sentence " + crs.getSentenceID() + ": " + crs.getSentenceText() + "--\n");
			  writer.write("  Requirement: " + crs.getRequirementID() + "\n");
			  writer.write("  Confidence Degree: " + crs.getConfidenceDegree() + "\n");
			  writer.write("  Matching Degree: " + crs.getMatchingDegree() + "\n");
			  writer.write("  Missing Roles: " + crs.getMissingRoles() + "\n");
			  writer.close();
		  } catch (IOException e) {
			  // TODO Auto-generated catch block
			  e.printStackTrace();
		  }
		  if(crs.getConfidenceDegree() < 0.1)
			  cd_b_01++;
		  if(crs.getConfidenceDegree() < 0.2 & crs.getConfidenceDegree() >= 0.1) {
			  cd_b_02++;
			  //lcdsRequirements(crs.getRequirementID(), crs.getConfidenceDegree());
		  }
		  if(crs.getConfidenceDegree() < 0.3 & crs.getConfidenceDegree() >= 0.2)
			  cd_b_03++;
		  if(crs.getConfidenceDegree() < 0.4 & crs.getConfidenceDegree() >= 0.3)
			  cd_b_04++;
		  if(crs.getConfidenceDegree() < 0.5 & crs.getConfidenceDegree() >= 0.4)
			  cd_b_05++;
	  }
	  //System.out.println("\nRequirements Confidence Degree using low confidence degree sentences:");
	  //printinglcdrequirements();
	  System.out.println("\nRequirements Confidence Degree using all sentences:");
	  printingallcdrequirements();
	  System.out.println("\nNumber of sentences with confidence degree < 0.1: " + cd_b_01);
	  System.out.println("\nNumber of sentences with confidence degree < 0.2 & >= 0.1: " + cd_b_02);
	  System.out.println("\nNumber of sentences with confidence degree < 0.3 & >= 0.2: " + cd_b_03);
	  System.out.println("\nNumber of sentences with confidence degree < 0.4 & >= 0.3: " + cd_b_04);
	  System.out.println("\nNumber of sentences with confidence degree < 0.5 & >= 0.4: " + cd_b_05);
	  try {
		  String doc = inputPath.replace(PATH_CSV, "");
		  BufferedWriter writer = new BufferedWriter(new FileWriter(PATH_OUTPUT + doc.replace(".csv", "_cd_info.txt"), true));
		  //BufferedWriter writer = new BufferedWriter(new FileWriter(PATH_OUTPUT + DOC.replace(".csv", "_cd_info.txt"), true));
		  writer.write("\nNumber of sentences with confidence degree <= 0.1: " + cd_b_01 + "\n");
		  writer.write("Number of sentences with confidence degree < 0.2 & >= 0.1: " + cd_b_02 + "\n");
		  writer.write("Number of sentences with confidence degree < 0.3 & >= 0.2: " + cd_b_03 + "\n");
		  writer.write("Number of sentences with confidence degree < 0.4 & >= 0.3: " + cd_b_04 + "\n");
		  writer.write("Number of sentences with confidence degree < 0.5 & >= 0.4: " + cd_b_05 + "\n");
		  /*writer.write("\nRequirements Confidence Degree using low confidence degree sentences:\n");
	  	  writer.write("R10\tR11\tR12\tR13\tR15\tR16\tR17\tR18\tR19\tR20\tR21\tR22\tR23\tR24\tR25\tR26\tR27\tR28\tR29\tR30\tR31\tR32\tR33\tR34\tR35\n");
	  	  writer.write(df.format(lcr10/lcr10c) + "\t" + df.format(lcr11/lcr11c) + "\t" + df.format(lcr12/lcr12c) + "\t" + df.format(lcr13/lcr13c)
	  	  + "\t" + df.format(lcr15/lcr15c) + "\t" + df.format(lcr16/lcr16c) + "\t" + df.format(lcr17/lcr17c) + "\t" + df.format(lcr18/lcr18c)
	  	  + "\t" + df.format(lcr19/lcr19c) + "\t" + df.format(lcr20/lcr20c) + "\t" + df.format(lcr21/lcr21c) + "\t" + df.format(lcr22/lcr22c)
	  	  + "\t" + df.format(lcr23/lcr23c) + "\t" + df.format(lcr24/lcr24c) + "\t" + df.format(lcr25/lcr25c) + "\t" + df.format(lcr26/lcr26c)
	  	  + "\t" + df.format(lcr27/lcr27c) + "\t" + df.format(lcr28/lcr28c) + "\t" + df.format(lcr29/lcr29c) + "\t" + df.format(lcr30/lcr30c)
	  	  + "\t" + df.format(lcr31/lcr31c) + "\t" + df.format(lcr32/lcr32c) + "\t" + df.format(lcr33/lcr33c) + "\t" + df.format(lcr34/lcr34c)
	  	  + "\t" + df.format(lcr35/lcr35c) + "\n");*/
		  writer.write("\nRequirements Confidence Degree using all sentences:\n");
		  writer.write("R10\tR11\tR12\tR13\tR15\tR16\tR17\tR18\tR19\tR20\tR21\tR22\tR23\tR24\tR25\tR26\tR27\tR28\tR29\tR30"
		  		+ "\tR31\tR32\tR33\tR34\tR35\tR36\tR37\tR38\tR39\tR40\tR41\tR42\tR43\tR44\tR45\tR46\n");
		  writer.write(df.format(r10/r10c) + "\t" + df.format(r11/r11c) + "\t" + df.format(r12/r12c) + "\t" + df.format(r13/r13c)
	  	  + "\t" + df.format(r15/r15c) + "\t" + df.format(r16/r16c) + "\t" + df.format(r17/r17c) + "\t" + df.format(r18/r18c)
	  	  + "\t" + df.format(r19/r19c) + "\t" + df.format(r20/r20c) + "\t" + df.format(r21/r21c) + "\t" + df.format(r22/r22c)
	  	  + "\t" + df.format(r23/r23c) + "\t" + df.format(r24/r24c) + "\t" + df.format(r25/r25c) + "\t" + df.format(r26/r26c)
	  	  + "\t" + df.format(r27/r27c) + "\t" + df.format(r28/r28c) + "\t" + df.format(r29/r29c) + "\t" + df.format(r30/r30c)
	  	  + "\t" + df.format(r31/r31c) + "\t" + df.format(r32/r32c) + "\t" + df.format(r33/r33c) + "\t" + df.format(r34/r34c)
	  	  + "\t" + df.format(r35/r35c) + "\t" + df.format(r36/r36c) + "\t" + df.format(r37/r37c) + "\t" + df.format(r38/r38c)
	  	  + "\t" + df.format(r39/r39c) + "\t" + df.format(r40/r40c) + "\t" + df.format(r41/r41c) + "\t" + df.format(r42/r42c)
	  	  + "\t" + df.format(r43/r43c) + "\t" + df.format(r44/r44c) + "\t" + df.format(r45/r45c) + "\t" + df.format(r46/r46c)
	  	  + "\n");
		  writer.close();
	  } catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	  }
	  System.out.printf("%n----------------------------------------------%n");
	  
	  try {
		  groundTruth = ReadingGoldStandard();
	  } catch (Exception e) {
		  // TODO Auto-generated catch block
		  e.printStackTrace();
	  }
	  for (Entry<String, List<String>> entry : groundTruth.entrySet()) {
		  String sID = entry.getKey();
		  List<String> rs_mds = entry.getValue();
		  for(int i=0;i<rs_mds.size();i++) {
			  if(!rs_mds.get(i).equalsIgnoreCase("NA")) {
				  break_flag = true;
				  break;
			  }
		  }
		  if(break_flag) {
			  break_flag = false;
			  //System.out.println("  Sentence ID: " + sID);
			  //System.out.println("  Requirements & Matching Degrees: " + rs_mds + "\n");
			  for (ComplianceResults crs : select(aJCas, ComplianceResults.class)) {
				  //if(crs.getSentenceText().equalsIgnoreCase(sID) || crs.getSentenceText().toLowerCase().contains(sID.toLowerCase()) || sID.toLowerCase().contains(crs.getSentenceText().toLowerCase())) {
				  if(crs.getSentenceText().equalsIgnoreCase(sID)) {
					  cr_flag = true;
					  //System.out.printf("%n-- Sentence ID: " + crs.getSentenceID() + " --%n");
					  //System.out.println("  Sentence Text: " + crs.getSentenceText());
					  //System.out.println("  GT Sentence Text: " + sID);
					  //System.out.println("  Requirement ID: " + crs.getRequirementID());
					  //System.out.println("  Matching Degree: " + crs.getMatchingDegree());
					  for(int i=0;i<rs_mds.size();i++) {
						  //System.out.println("  Requirement ID: " + crs.getRequirementID());
						  //System.out.println("  Requirement || Matching Degree: " + rs_mds.get(i));
						  if(crs.getRequirementID().equalsIgnoreCase(rs_mds.get(i)) && crs.getMatchingDegree().equalsIgnoreCase(rs_mds.get(i+1))) {
							  tp_tc = 2;
							  if(!pRIr.contains(crs.getRequirementID()))
								  pRIr.add(crs.getRequirementID());
						  }
						  else if(crs.getRequirementID().equalsIgnoreCase(rs_mds.get(i))) {
							  tp_tc = 1;
							  if(!pRIr.contains(crs.getRequirementID()))
								  pRIr.add(crs.getRequirementID());
						  }
					  }
				  }
				  else {
					  for(int i=0;i<rs_mds.size();i++) {
						  if(crs.getRequirementID().equalsIgnoreCase(rs_mds.get(i))) {
							  if(!pWIr.contains(crs.getRequirementID()))
								  pWIr.add(crs.getRequirementID());
						  }
					  }
				  }
			  }
			  if(cr_flag) {
				  if(tp_tc == 2) {
					  //System.out.println("  In Sentence: " + sID + " the Requirement and the Matching Degree are right...TP\n");
					  tp++;
					  tp_tc = 0;
				  }
				  else if(tp_tc == 1) {
					  //System.out.println("  In Sentence: " + sID + " only the Requirement is right...TP\n");
					  tp++;
					  tp_tc = 0;
				  }
				  else {
					  String temp = rs_mds.get(0);
					  temp = temp.replace("R", "");
					  if(Integer.valueOf(temp) < 10 || Integer.valueOf(temp) > 46){
						  //System.out.println("  The Sentence: " + sID + " is a TN\n");
						  tn++;
					  }
					  else {
						  //System.out.println("  The Sentence: " + sID + " is a FN\n");
						  fn++;
					  }
				  }
			  }
			  else {
				  String temp = rs_mds.get(0);
				  temp = temp.replace("R", "");
				  if(Integer.valueOf(temp) < 10 || Integer.valueOf(temp) > 46){
					  //System.out.println("  The Sentence: " + sID + " is a TN\n");
					  tn++;
				  }
				  else {
					  //System.out.println("  The Sentence: " + sID + " is a FN\n");
					  fn++;
				  }
			  }
		  }
		  else {
			  for (ComplianceResults crs : select(aJCas, ComplianceResults.class)) {
				  if(crs.getSentenceText().equalsIgnoreCase(sID)) {
					  tn_flag = false;
					  break;
				  }
			  }
			  if(tn_flag) {
				  //System.out.println("  The Sentence: " + sID + " is a TN\n");
				  tn++;
			  }
			  else {
				  //System.out.println("  The Sentence: " + sID + " is a FP\n");
				  tn_flag = true;
				  fp++;
			  }
		  }
	  }
	  
	  //Adjusting the evaluation to the identification of missing requirements
	  //----------------------------------------------------------------------
	  int exch;
	  exch = tp;
	  tp = tn;
	  tn = exch;
	  exch = fp;
	  fp = fn;
	  fn = exch;
	  //----------------------------------------------------------------------
	  
	  float P = (float)(tp*100)/(tp+fp);
	  float R = (float)(tp*100)/(tp+fn);
	  float A = (float)(tp+tn)*100/(tp+tn+fp+fn);
	  float F1 = (float)(P*R)*2/(P+R);
	  /*System.out.printf("%n--Compliance Requirements Analysis--%n");
	  System.out.println("The number of TP: " + tp);
	  System.out.println("The number of TN: " + tn);
	  System.out.println("The number of FP: " + fp);
	  System.out.println("The number of FN: " + fn);
	  System.out.println("Precision: " + P);
	  System.out.println("Recall: " + R);
	  System.out.println("Accuracy: " + A);
	  System.out.println("F1 Measure: " + F1 + "\n");*/
	  
	  //---------------------------------------------------------------------------------------------------
	  // Evaluating the extraction of requirements
	  //---------------------------------------------------------------------------------------------------
	  List<String> reqMtm = Arrays.asList("R10","R11","R12","R13","R15","R16","R17","R18","R19",
			  							  "R20","R21","R22","R23","R24","R25","R26","R27","R28","R29","R36");
	  List<String> reqNMtm = Arrays.asList("R30","R31","R32","R33","R34","R35","R37","R38","R39",
			  							   "R40","R41","R42","R43","R44","R45","R46");
	  List<String> reqM = new ArrayList<String>();
	  List<String> reqNM = new ArrayList<String>();
	  List<String> fNr = new ArrayList<String>();
	  List<String> fPr = new ArrayList<String>();
	  List<String> tNr = new ArrayList<String>();
	  String header0;
	  boolean mr_flag = false, nmr_flag = false;
	  int TP = 0, TN = 0, FP = 0, FN = 0;
	  float p, r, a, f1, f2;
	  for (ComplianceResults crs : select(aJCas, ComplianceResults.class)) {
		  if(!reqM.contains(crs.getRequirementID()))
			  reqM.add(crs.getRequirementID());
	  }
	  for (int i=0;i<reqMtm.size();i++) {
		  if(!reqM.contains(reqMtm.get(i))) {
			  reqNM.add(reqMtm.get(i));
			  mr_flag = true;
		  }
	  }
	  for (int i=0;i<reqNMtm.size();i++) {
		  if(!reqM.contains(reqNMtm.get(i))) {
			  reqNM.add(reqNMtm.get(i));
			  nmr_flag = true;
		  }
	  }
	  if(mr_flag) {
		  System.out.println("\nThe DPA is not GDPR compliant!\n");
		  header0 = "\n-- DPA Compliance Analysis --\nThe DPA is not GDPR compliant!\n";
	  }
	  else if (nmr_flag) {
		  System.out.println("\nThe DPA might not be GDPR compliant!\n");
		  header0 = "\n-- DPA Compliance Analysis --\nThe DPA might not be GDPR compliant!\n";
	  }
	  else {
		  System.out.println("\nThe DPA is GDPR compliant!\n");
		  header0 = "\n-- DPA Compliance Analysis --\nThe DPA is GDPR compliant!\n";
	  }
	  /*System.out.println("List of met requirements:");
	  System.out.println(reqM + "\n");
	  System.out.println("List of not met requirements:");
	  System.out.println(reqNM + "\n");
	  System.out.println("Requirements wrongly identified in any sentence:");
	  System.out.println(pWIr + "\n");
	  System.out.println("Requirements correctly identified in any sentence:");
	  System.out.println(pRIr + "\n");*/
	  String header1 = "\n\n-- Requirements Compliance Analysis --\n";
	  String header2 = "TP\tTN\tFP\tFN\tPrec(%)\tRec(%)\tAcu(%)\tF1(%)\tF2(%)\n";
	  String header3 = "-- Requirements Metrics --\n";
	  try {
		  String doc = inputPath.replace(PATH_CSV, "");
		  BufferedWriter writer2 = new BufferedWriter(new FileWriter(PATH_OUTPUT + doc.replace("csv", "txt"), true));
		  //BufferedWriter writer2 = new BufferedWriter(new FileWriter(PATH_OUTPUT + DOC.replace("csv", "txt"), true));
		  writer2.write(header0);
		  /*writer.write("Number of not met requirements:\t" + reqNM.size() + "\n");
		  if(mr_flag || nmr_flag) {
			  writer.write("List of not met requirements:\t");
			  for (int i=0;i<reqNM.size();i++)
				  writer.write(reqNM.get(i) + "\t");
		  }
		  if(!reqM.isEmpty()) {
			  writer.write("\nNumber of met requirements:\t" + reqM.size() + "\n");
			  writer.write("List of met requirements:\t");
			  for (int i=0;i<reqM.size();i++)
				  writer.write(reqM.get(i) + "\t");
		  }*/
		  
		  //----------------------------------------------------------------------------------------------------------------
		  //Evaluation by groups rather than requirements
		  //----------------------------------------------------------------------------------------------------------------
		  /*boolean GSPD = false, GSC = false, GPI = false, GAC = false, GNDB = false, GRPD = false, GTPD = false, GLD = false;
		  boolean GSPD_TP = false, GSC_TP = false, GPI_TP = false, GAC_TP = false, GNDB_TP = false, GRPD_TP = false, GTPD_TP = false, GLD_TP = false;
		  String SPD = "Nothing yet", SC = "Nothing yet", PI = "Nothing yet", AC = "Nothing yet", NDB = "Nothing yet";
		  String RPD = "Nothing yet", TPD = "Nothing yet", LD = "Nothing yet";
		  int tp_bg = 0, tn_bg = 0, fp_bg = 0, fn_bg = 0;
		  float p_bg, r_bg, a_bg, fm_bg;
		  List<String> all_entries = new ArrayList<String>();
		  //Identification of True Positives
		  for (Entry<String, List<String>> entry : groundTruth.entrySet()) {
			  List<String> rs_mds = entry.getValue();
			  for(int i=0;i<rs_mds.size();i++) {
				  all_entries.add(rs_mds.get(i));
			  }
		  }
		//Group Sub-contracting
		  if((!all_entries.contains("R10") || !all_entries.contains("R11") || !all_entries.contains("R15") || !all_entries.contains("R27")
		  || !all_entries.contains("R28")) && (!reqM.contains("R10") || !reqM.contains("R11") || !reqM.contains("R15") || !reqM.contains("R27")
		  || !reqM.contains("R28"))) {
			  GSC_TP = true;
		  }
		  //Group Processing on Instructions
		  if((!all_entries.contains("R12") || !all_entries.contains("R13") 	//|| !all_entries.contains("R14")
		   || !all_entries.contains("R33")) && (!reqM.contains("R12") || !reqM.contains("R13") 	//|| !reqM.contains("R14")
		   || !reqM.contains("R33"))) {
			  GPI_TP = true;
		  }
		  //Group Security of Personal Data
		  if((!all_entries.contains("R16") || !all_entries.contains("R18") || !all_entries.contains("R25") || !all_entries.contains("R29")
		   || !all_entries.contains("R31") || !all_entries.contains("R32")) && (!reqM.contains("R16") || !reqM.contains("R18")
		   || !reqM.contains("R25") || !reqM.contains("R29") || !reqM.contains("R31") || !reqM.contains("R32"))) {
			  GSPD_TP = true;
		  }
		  //Group Assisting Controller
		  if((!all_entries.contains("R17") || !all_entries.contains("R19") || !all_entries.contains("R20") || !all_entries.contains("R21")
		   || !all_entries.contains("R22")) && (!reqM.contains("R17") || !reqM.contains("R19") || !reqM.contains("R20")
		   || !reqM.contains("R21") || !reqM.contains("R22"))) {
			  GAC_TP = true;
		  }
		  //About Incident
		  if((!all_entries.contains("R24") || !all_entries.contains("R26") || !all_entries.contains("R34"))
		    && (!reqM.contains("R24") || !reqM.contains("R26") || !reqM.contains("R34"))) {
			  GNDB_TP = true;
		  }
		  //Removing Personal Data
		  if(!all_entries.contains("R23") && !reqM.contains("R23")) {
			  GRPD_TP = true;
		  }
		  //Transfer of Personal Data
		  if(!all_entries.contains("R30") && !reqM.contains("R30")) {
			  GTPD_TP = true;
		  }
		  //Liability for Damages
		  if(!all_entries.contains("R35") && !reqM.contains("R35")) {
			  GLD_TP = true;
		  }

		  //Identification of True Negatives
		  for (Entry<String, List<String>> entry : groundTruth.entrySet()) {
			  List<String> rs_mds = entry.getValue();
			  //Group Sub-contracting
			  if((rs_mds.contains("R10") || rs_mds.contains("R11") || rs_mds.contains("R15") || rs_mds.contains("R27")
				|| rs_mds.contains("R28")) && (reqM.contains("R10") || reqM.contains("R11") || reqM.contains("R15")
				|| reqM.contains("R27") || reqM.contains("R28"))) {
				  GSC = true;
			  }
			  //Group Processing on Instructions
			  if((rs_mds.contains("R12") || rs_mds.contains("R13")	//|| rs_mds.contains("R14")
			   || rs_mds.contains("R33")) && (reqM.contains("R12") || reqM.contains("R13") 	//|| reqM.contains("R14")
			   || reqM.contains("R33"))) {
				  GPI = true;
			  }
			  //Group Security of Personal Data
			  if((rs_mds.contains("R16") || rs_mds.contains("R18") || rs_mds.contains("R25") || rs_mds.contains("R29")
			   || rs_mds.contains("R31") || rs_mds.contains("R32")) && (reqM.contains("R16") || reqM.contains("R18")
			   || reqM.contains("R25") || reqM.contains("R29") || reqM.contains("R31") || reqM.contains("R32"))) {
				  GSPD = true;
			  }
			  //Group Assisting Controller
			  if((rs_mds.contains("R17") || rs_mds.contains("R19") || rs_mds.contains("R20") || rs_mds.contains("R21")
			   || rs_mds.contains("R22")) && (reqM.contains("R17") || reqM.contains("R19") || reqM.contains("R20")
			   || reqM.contains("R21") || reqM.contains("R22"))) {
				  GAC = true;
			  }
			  //About Incident
			  if((rs_mds.contains("R24") || rs_mds.contains("R26") || rs_mds.contains("R34"))
			    && (reqM.contains("R24") || reqM.contains("R26") || reqM.contains("R34"))) {
				  GNDB = true;
			  }
			  //Removing Personal Data
			  if(rs_mds.contains("R23") && reqM.contains("R23")) {
				  GRPD = true;
			  }
			  //Transfer of Personal Data
			  if(rs_mds.contains("R30") && reqM.contains("R30")) {
				  GTPD = true;
			  }
			  //Liability for Damages
			  if(rs_mds.contains("R35") && reqM.contains("R35")) {
				  GLD = true;
			  }
		  }
		  
		  if(!GSC_TP) {
			  if (GSC) {
				  SC = "True Negative";
				  tn_bg++;
			  }
			  else {
				  if(reqM.contains("R10") || reqM.contains("R11") || reqM.contains("R15") 
				  || reqM.contains("R27") || reqM.contains("R28")) {
					  SC = "False Negative";
					  fn_bg++;
				  }
				  else {
					  SC = "False Positive";
					  fp_bg++;
				  }
			  }
		  }
		  else {
			  SC = "True Positive";
			  tp_bg++;
		  }
		  
		  if(!GPI_TP) {
			  if (GPI) {
				  PI = "True Negative";
				  tn_bg++;
			  }
			  else {
				  if(reqM.contains("R12") || reqM.contains("R13") 	//|| reqM.contains("R14")
						  				  || reqM.contains("R33")
						  				  ) {
					  PI = "False Negative";
					  fn_bg++;
				  }
				  else {
					  PI = "False Positive";
					  fp_bg++;
				  }
			  }
		  }
		  else {
			  PI = "True Positive";
			  tp_bg++;
		  }
		  
		  if(!GSPD_TP) {
			  if (GSPD) {
				  SPD = "True Negative";
				  tn_bg++;
			  }
			  else {
				  if(reqM.contains("R16") || reqM.contains("R18") || reqM.contains("R25")
				  || reqM.contains("R29") || reqM.contains("R31") || reqM.contains("R32")
				  ) {
					  SPD = "False Negative";
					  fn_bg++;
				  }
				  else {
					  SPD = "False Positive";
					  fp_bg++;
				  }
			  }
		  }
		  else {
			  SPD = "True Positive";
			  tp_bg++;
		  }
		  
		  if(!GAC_TP) {
			  if (GAC) {
				  AC = "True Negative";
				  tn_bg++;
			  }
			  else {
				  if(reqM.contains("R17") || reqM.contains("R19") || reqM.contains("R20")
						  				  || reqM.contains("R21") || reqM.contains("R22")) {
					  AC = "False Negative";
					  fn_bg++;
				  }
				  else {
					  AC = "False Positive";
					  fp_bg++;
				  }
			  }
		  }
		  else {
			  AC = "True Positive";
			  tp_bg++;
		  }
		  
		  if(!GNDB_TP) {
			  if (GNDB) {
				  NDB = "True Negative";
				  tn_bg++;
			  }
			  else {
				  if(reqM.contains("R24") || reqM.contains("R26") || reqM.contains("R34")) {
					  NDB = "False Negative";
					  fn_bg++;
				  }
				  else {
					  NDB = "False Positive";
					  fp_bg++;
				  }
			  }
		  }
		  else {
			  NDB = "True Positive";
			  tp_bg++;
		  }
		  
		  if(!GRPD_TP) {
			  if (GRPD) {
				  RPD = "True Negative";
				  tn_bg++;
			  }
			  else {
				  if(reqM.contains("R23")) {
					  RPD = "False Negative";
					  fn_bg++;
				  }
				  else {
					  RPD = "False Positive";
					  fp_bg++;
				  }
			  }
		  }
		  else {
			  RPD = "True Positive";
			  tp_bg++;
		  }
		  
		  if(!GTPD_TP) {
			  if (GTPD) {
				  TPD = "True Negative";
				  tn_bg++;
			  }
			  else {
				  if(reqM.contains("R30")) {
					  TPD = "False Negative";
					  fn_bg++;
				  }
				  else {
					  TPD = "False Positive";
					  fp_bg++;
				  }
			  } 
		  }
		  else {
			  TPD = "True Positive";
			  tp_bg++;
		  }
		  
		  if(!GLD_TP) {
			  if (GLD) {
				  LD = "True Negative";
				  tn_bg++;
			  }
			  else {
				  if(reqM.contains("R35")) {
					  LD = "False Negative";
					  fn_bg++;
				  }
				  else {
					  LD = "False Positive";
					  fp_bg++;
				  }
			  }
		  }
		  else {
			  LD = "True Positive";
			  tp_bg++;
		  }
		  
		  p_bg = (float)(tp_bg*100)/(tp_bg+fp_bg);
		  r_bg = (float)(tp_bg*100)/(tp_bg+fn_bg);
		  a_bg = (float)(tp_bg+tn_bg)*100/(tp_bg+tn_bg+fp_bg+fn_bg);
		  fm_bg = (float)(p_bg*r_bg)*3/(2*p_bg+r_bg);*/
		  
		  /*System.out.println("Sub-contracting: " + SC);
		  System.out.println("Processing on Instructions : " + PI);
		  System.out.println("Security of Personal Data : " + SPD);
		  System.out.println("Assisting Controller : " + AC);
		  System.out.println("About Incident : " + NDB);
		  System.out.println("Removing Personal Data : " + RPD);
		  System.out.println("Transfer of Personal Data : " + TPD);
		  System.out.println("Liability for Damages : " + LD + "\n");
		  System.out.println("TPs: " + tp_bg);
		  System.out.println("FPs: " + fp_bg);
		  System.out.println("FNs: " + fn_bg);
		  System.out.println("TNs: " + tn_bg);
		  System.out.println("Precision: " + p_bg);
		  System.out.println("Recall: " + r_bg);
		  System.out.println("Acurracy: " + a_bg);
		  System.out.println("F2 Measure: " + fm_bg + "\n");*/
		  //----------------------------------------------------------------------------------------------------------------
		  //In development...evaluation by groups rather than requirements
		  //----------------------------------------------------------------------------------------------------------------
		  
		  for (Entry<String, List<String>> entry : groundTruth.entrySet()) {
			  List<String> rs_mds = entry.getValue();
			  for(int i=0;i<rs_mds.size();i++) {
				  for(int j=0;j<pWIr.size();j++) {
					  if(rs_mds.get(i).equalsIgnoreCase(pWIr.get(j)) && !pRIr.contains(pWIr.get(j)) && !wIr.contains(pWIr.get(j))) {
						  wIr.add(pWIr.get(j));
						  req_count++;
					  }
				  }
			  }
		  }
		  for(int i=0;i<reqM.size();i++) {
			  if(!wIr.contains(reqM.get(i)) && !pRIr.contains(reqM.get(i))) {
				  fPr.add(reqM.get(i));
				  FP++;
			  }
		  }
		  for (Entry<String, List<String>> entry : groundTruth.entrySet()) {
			  List<String> rs_mds = entry.getValue();
			  for(int i=0;i<reqNM.size();i++) {
				  for(int j=0;j<rs_mds.size();j++) {
					  if(rs_mds.get(j).equalsIgnoreCase(reqNM.get(i)) && !fNr.contains(reqNM.get(i))) {
						  fNr.add(reqNM.get(i));
						  FN++;
					  }
				  }
			  }
		  }
		  for(int i=0;i<reqNM.size();i++) {
			  if(!fNr.contains(reqNM.get(i))) {
				  tNr.add(reqNM.get(i));
				  TN++;
			  }
		  }
		  //System.out.println("Number of requirements identified in the wrong sentence:\t" + req_count);
		  //System.out.println("Requirements identified in the wrong sentence:\t" + wIr + "\n");
		  writer2.write("Number of requirements identified in the wrong sentence:\t" + req_count + "\n");
		  writer2.write("Requirements identified in the wrong sentence:\t");
		  for(int i=0;i<wIr.size();i++) {
			  writer2.write(wIr.get(i) + "\t");
		  }
		  writer2.write("\n\n" + header3);
		  writer2.write(header2);
		  TP = pRIr.size()+req_count;
		  //Adjusting the evaluation to the identification of missing requirements
		  //----------------------------------------------------------------------
		  exch = TP;
		  TP = TN;
		  TN = exch;
		  exch = FP;
		  FP = FN;
		  FN = exch;
		  //----------------------------------------------------------------------
		  //Adjusting to only mandatory requirements
		  //----------------------------------------------------------------------
		  /*for (int i=0;i<reqNMtm.size();i++) {
			  if(tNr.contains(reqNMtm.get(i))) {
				  tNr.remove(tNr.indexOf(reqNMtm.get(i)));
				  TP--;
			  }
			  if(pRIr.contains(reqNMtm.get(i))) {
				  pRIr.remove(pRIr.indexOf(reqNMtm.get(i)));
				  TN--;
			  }
			  if(wIr.contains(reqNMtm.get(i))) {
				  wIr.remove(wIr.indexOf(reqNMtm.get(i)));
				  TN--;
			  }
			  if(fNr.contains(reqNMtm.get(i))) {
				  fNr.remove(fNr.indexOf(reqNMtm.get(i)));
				  FP--;
			  }
			  if(fPr.contains(reqNMtm.get(i))) {
				  fPr.remove(fPr.indexOf(reqNMtm.get(i)));
				  FN--;
			  }
		  }*/
		  //----------------------------------------------------------------------
		  p = (float)(TP*100)/(TP+FP);
		  r = (float)(TP*100)/(TP+FN);
		  a = (float)(TP+TN)*100/(TP+TN+FP+FN);
		  f1 = (float)(p*r)*2/(p+r);
		  f2 = (float)(p*r)*3/(2*p+r);
		  System.out.println(TP + " TPs:\t" + tNr);
		  System.out.println(FP + " FPs:\t" + fNr);
		  System.out.println(FN + " FNs:\t" + fPr);
		  System.out.println(TN + " TNs:\t" + pRIr + wIr);
		  System.out.println("Precision: " + p);
		  System.out.println("Recall: " + r);
		  System.out.println("Acurracy: " + a);
		  System.out.println("F1 Measure: " + f1 + "\n");
		  writer2.write(TP + "\t" + TN + "\t" + FP + "\t" + FN + "\t" + df.format(p) + "\t" + df.format(r) 
			  + "\t" + df.format(a) + "\t" + df.format(f1) + "\t" + df.format(f2) + "\n");
		  writer2.write("TPs:\t");
		  for(int i=0;i<tNr.size();i++) {
			  writer2.write(tNr.get(i) + "\t");
		  }
		  writer2.write("\nTNs:\t");
		  for(int i=0;i<pRIr.size();i++) {
			  writer2.write(pRIr.get(i) + "\t");
		  }
		  for(int i=0;i<wIr.size();i++) {
			  writer2.write(wIr.get(i) + "\t");
		  }
		  writer2.write("\nFPs:\t");
		  for(int i=0;i<fNr.size();i++) {
			  writer2.write(fNr.get(i) + "\t");
		  }
		  writer2.write("\nFNs:\t");
		  for(int i=0;i<fPr.size();i++) {
			  writer2.write(fPr.get(i) + "\t");
		  }
		  /*writer.write(header1);
		  writer.write(header2);
		  writer.write(tp + "\t" + tn + "\t" + fp + "\t" + fn + "\t" + df.format(P) + "\t" + df.format(R) 
		  				  + "\t" + df.format(A) + "\t" + df.format(F1) + "\n");*/
		  /*writer2.write("\n\nTPsbg\tTNsbg\tFPsbg\tFNsbg\tPrebg(%)\tRecbg(%)\tAcubg(%)\tF2mbg(%)\n");
		  writer2.write(tp_bg + "\t" + fp_bg + "\t" + fn_bg + "\t" + tn_bg + "\t" + df.format(p_bg) + "\t" + df.format(r_bg) + "\t" + df.format(a_bg) + "\t" + df.format(fm_bg) + "\n");
		  writer2.write("Sub-contracting: " + SC + "\n");
		  writer2.write("Processing on Instructions : " + PI + "\n");
		  writer2.write("Security of Personal Data : " + SPD + "\n");
		  writer2.write("Assisting Controller : " + AC + "\n");
		  writer2.write("About Incident : " + NDB + "\n");
		  writer2.write("Removing Personal Data : " + RPD + "\n");
		  writer2.write("Transfer of Personal Data : " + TPD + "\n");
		  writer2.write("Liability for Damages : " + LD);*/
		  writer2.close();
	  } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	  }
  }
  
  public HashMap<String, List<String>> ReadingGoldStandard() throws Exception {
		HashMap<String, List<String>> goldstandard = new HashMap<String, List<String>>();
		//Reading the CSV file
		//System.out.println("Testing document path: " + inputPath);
		File csvFile = new File(inputPath);
		//File csvFile = new File(PATH_CSV + DOC);	//inputPath
		if (csvFile.isFile()) {
			// create BufferedReader and read data from csv
			BufferedReader csvReader = new BufferedReader(new FileReader(inputPath));
			//BufferedReader csvReader = new BufferedReader(new FileReader(PATH_CSV + DOC));	//inputPath
			String row = csvReader.readLine();
			while ((row = csvReader.readLine()) != null) {						
				String[] fields = row.split(",");
				if(fields.length!=7)
					throw new Exception("Error parsing the following sentence: \"" + row + "\"");
				//Getting for every requirement, the ID and the matching degree
				List<String> groundTruth = new ArrayList<String>();
				List<String> temp = new ArrayList<String>();
				groundTruth.add(fields[REQUIREMENT_1_INDEX]);
				groundTruth.add(fields[MATCHING_DEGREE_1_INDEX]);
				groundTruth.add(fields[REQUIREMENT_2_INDEX]);
				groundTruth.add(fields[MATCHING_DEGREE_2_INDEX]);
				groundTruth.add(fields[REQUIREMENT_3_INDEX]);
				groundTruth.add(fields[MATCHING_DEGREE_3_INDEX]);
				goldstandard.put(fields[SENTENCE_ID_INDEX].replace(";", ","),groundTruth);
			}
			csvReader.close();
		}
		return goldstandard;
	}

  //Confidence Degree of all Requirements
  private void cdallRequirements(String req, double score) {
	  if(req.equalsIgnoreCase("R10")) {
		  r10 += score;
		  r10c++;
	  }
	  else if(req.equalsIgnoreCase("R11")) {
		  r11 += score;
		  r11c++;
	  }
	  else if(req.equalsIgnoreCase("R12")) {
		  r12 += score;
		  r12c++;
	  }
	  else if(req.equalsIgnoreCase("R13")) {
		  r13 += score;
		  r13c++;
	  }
	  else if(req.equalsIgnoreCase("R15")) {
		  r15 += score;
		  r15c++;
	  }
	  else if(req.equalsIgnoreCase("R16")) {
		  r16 += score;
		  r16c++;
	  }
	  else if(req.equalsIgnoreCase("R17")) {
		  r17 += score;
		  r17c++;
	  }
	  else if(req.equalsIgnoreCase("R18")) {
		  r18 += score;
		  r18c++;
	  }
	  else if(req.equalsIgnoreCase("R19")) {
		  r19 += score;
		  r19c++;
	  }
	  else if(req.equalsIgnoreCase("R20")) {
		  r20 += score;
		  r20c++;
	  }
	  else if(req.equalsIgnoreCase("R21")) {
		  r21 += score;
		  r21c++;
	  }
	  else if(req.equalsIgnoreCase("R22")) {
		  r22 += score;
		  r22c++;
	  }
	  else if(req.equalsIgnoreCase("R23")) {
		  r23 += score;
		  r23c++;
	  }
	  else if(req.equalsIgnoreCase("R24")) {
		  r24 += score;
		  r24c++;
	  }
	  else if(req.equalsIgnoreCase("R25")) {
		  r25 += score;
		  r25c++;
	  }
	  else if(req.equalsIgnoreCase("R26")) {
		  r26 += score;
		  r26c++;
	  }
	  else if(req.equalsIgnoreCase("R27")) {
		  r27 += score;
		  r27c++;
	  }
	  else if(req.equalsIgnoreCase("R28")) {
		  r28 += score;
		  r28c++;
	  }
	  else if(req.equalsIgnoreCase("R29")) {
		  r29 += score;
		  r29c++;
	  }
	  else if(req.equalsIgnoreCase("R30")) {
		  r30 += score;
		  r30c++;
	  }
	  else if(req.equalsIgnoreCase("R31")) {
		  r31 += score;
		  r31c++;
	  }
	  else if(req.equalsIgnoreCase("R32")) {
		  r32 += score;
		  r32c++;
	  }
	  else if(req.equalsIgnoreCase("R33")) {
		  r33 += score;
		  r33c++;
	  }
	  else if(req.equalsIgnoreCase("R34")) {
		  r34 += score;
		  r34c++;
	  }
	  else if(req.equalsIgnoreCase("R35")) {
		  r35 += score;
		  r35c++;
	  }
	  else if(req.equalsIgnoreCase("R36")) {
		  r36 += score;
		  r36c++;
	  }
	  else if(req.equalsIgnoreCase("R37")) {
		  r37 += score;
		  r37c++;
	  }
	  else if(req.equalsIgnoreCase("R38")) {
		  r38 += score;
		  r38c++;
	  }
	  else if(req.equalsIgnoreCase("R39")) {
		  r39 += score;
		  r39c++;
	  }
	  else if(req.equalsIgnoreCase("R40")) {
		  r40 += score;
		  r40c++;
	  }
	  else if(req.equalsIgnoreCase("R41")) {
		  r41 += score;
		  r41c++;
	  }
	  else if(req.equalsIgnoreCase("R42")) {
		  r42 += score;
		  r42c++;
	  }
	  else if(req.equalsIgnoreCase("R43")) {
		  r43 += score;
		  r43c++;
	  }
	  else if(req.equalsIgnoreCase("R44")) {
		  r44 += score;
		  r44c++;
	  }
	  else if(req.equalsIgnoreCase("R45")) {
		  r45 += score;
		  r45c++;
	  }
	  else if(req.equalsIgnoreCase("R46")) {
		  r46 += score;
		  r46c++;
	  }
  }
  
  private void printingallcdrequirements() {
	  if(r10c!=0) {
		  System.out.println("R10: " + df.format(r10/r10c));
	  }
	  if(r11c!=0) {
		  System.out.println("R11: " + df.format(r11/r11c));
	  }
	  if(r12c!=0) {
		  System.out.println("R12: " + df.format(r12/r12c));
	  }
	  if(r13c!=0) {
		  System.out.println("R13: " + df.format(r13/r13c));
	  }
	  if(r15c!=0) {
		  System.out.println("R15: " + df.format(r15/r15c));
	  }
	  if(r16c!=0) {
		  System.out.println("R16: " + df.format(r16/r16c));
	  }
	  if(r17c!=0) {
		  System.out.println("R17: " + df.format(r17/r17c));
	  }
	  if(r18c!=0) {
		  System.out.println("R18: " + df.format(r18/r18c));
	  }
	  if(r19c!=0) {
		  System.out.println("R19: " + df.format(r19/r19c));
	  }
	  if(r20c!=0) {
		  System.out.println("R20: " + df.format(r20/r20c));
	  }
	  if(r21c!=0) {
		  System.out.println("R21: " + df.format(r21/r21c));
	  }
	  if(r22c!=0) {
		  System.out.println("R22: " + df.format(r22/r22c));
	  }
	  if(r23c!=0) {
		  System.out.println("R23: " + df.format(r23/r23c));
	  }
	  if(r24c!=0) {
		  System.out.println("R24: " + df.format(r24/r24c));
	  }
	  if(r25c!=0) {
		  System.out.println("R25: " + df.format(r25/r25c));
	  }
	  if(r26c!=0) {
		  System.out.println("R26: " + df.format(r26/r26c));
	  }
	  if(r27c!=0) {
		  System.out.println("R27: " + df.format(r27/r27c));
	  }
	  if(r28c!=0) {
		  System.out.println("R28: " + df.format(r28/r28c));
	  }
	  if(r29c!=0) {
		  System.out.println("R29: " + df.format(r29/r29c));
	  }
	  if(r30c!=0) {
		  System.out.println("R30: " + df.format(r30/r30c));
	  }
	  if(r31c!=0) {
		  System.out.println("R31: " + df.format(r31/r31c));
	  }
	  if(r32c!=0) {
		  System.out.println("R32: " + df.format(r32/r32c));
	  }
	  if(r33c!=0) {
		  System.out.println("R33: " + df.format(r33/r33c));
	  }
	  if(r34c!=0) {
		  System.out.println("R34: " + df.format(r34/r34c));
	  }
	  if(r35c!=0) {
		  System.out.println("R35: " + df.format(r35/r35c));
	  }
	  if(r36c!=0) {
		  System.out.println("R36: " + df.format(r36/r36c));
	  }
	  if(r37c!=0) {
		  System.out.println("R37: " + df.format(r37/r37c));
	  }
	  if(r38c!=0) {
		  System.out.println("R38: " + df.format(r38/r38c));
	  }
	  if(r39c!=0) {
		  System.out.println("R39: " + df.format(r39/r39c));
	  }
	  if(r40c!=0) {
		  System.out.println("R40: " + df.format(r40/r40c));
	  }
	  if(r41c!=0) {
		  System.out.println("R41: " + df.format(r41/r41c));
	  }
	  if(r42c!=0) {
		  System.out.println("R42: " + df.format(r42/r42c));
	  }
	  if(r43c!=0) {
		  System.out.println("R43: " + df.format(r43/r43c));
	  }
	  if(r44c!=0) {
		  System.out.println("R44: " + df.format(r44/r44c));
	  }
	  if(r45c!=0) {
		  System.out.println("R45: " + df.format(r45/r45c));
	  }
	  if(r46c!=0) {
		  System.out.println("R46: " + df.format(r46/r46c));
	  }
  }
  
  //Low Confidence Degree Requirements based on Low Confidence Degree Sentences
  private void lcdsRequirements(String req,double lcd) {
	  if(req.equalsIgnoreCase("R10")) {
		  lcr10 += lcd;
		  lcr10c++;
	  }
	  else if(req.equalsIgnoreCase("R11")) {
		  lcr11 += lcd;
		  lcr11c++;
	  }
	  else if(req.equalsIgnoreCase("R12")) {
		  lcr12 += lcd;
		  lcr12c++;
	  }
	  else if(req.equalsIgnoreCase("R13")) {
		  lcr13 += lcd;
		  lcr13c++;
	  }
	  else if(req.equalsIgnoreCase("R15")) {
		  lcr15 += lcd;
		  lcr15c++;
	  }
	  else if(req.equalsIgnoreCase("R16")) {
		  lcr16 += lcd;
		  lcr16c++;
	  }
	  else if(req.equalsIgnoreCase("R17")) {
		  lcr17 += lcd;
		  lcr17c++;
	  }
	  else if(req.equalsIgnoreCase("R18")) {
		  lcr18 += lcd;
		  lcr18c++;
	  }
	  else if(req.equalsIgnoreCase("R19")) {
		  lcr19 += lcd;
		  lcr19c++;
	  }
	  else if(req.equalsIgnoreCase("R20")) {
		  lcr20 += lcd;
		  lcr20c++;
	  }
	  else if(req.equalsIgnoreCase("R21")) {
		  lcr21 += lcd;
		  lcr21c++;
	  }
	  else if(req.equalsIgnoreCase("R22")) {
		  lcr22 += lcd;
		  lcr22c++;
	  }
	  else if(req.equalsIgnoreCase("R23")) {
		  lcr23 += lcd;
		  lcr23c++;
	  }
	  else if(req.equalsIgnoreCase("R24")) {
		  lcr24 += lcd;
		  lcr24c++;
	  }
	  else if(req.equalsIgnoreCase("R25")) {
		  lcr25 += lcd;
		  lcr25c++;
	  }
	  else if(req.equalsIgnoreCase("R26")) {
		  lcr26 += lcd;
		  lcr26c++;
	  }
	  else if(req.equalsIgnoreCase("R27")) {
		  lcr27 += lcd;
		  lcr27c++;
	  }
	  else if(req.equalsIgnoreCase("R28")) {
		  lcr28 += lcd;
		  lcr28c++;
	  }
	  else if(req.equalsIgnoreCase("R29")) {
		  lcr29 += lcd;
		  lcr29c++;
	  }
	  else if(req.equalsIgnoreCase("R30")) {
		  lcr30 += lcd;
		  lcr30c++;
	  }
	  else if(req.equalsIgnoreCase("R31")) {
		  lcr31 += lcd;
		  lcr31c++;
	  }
	  else if(req.equalsIgnoreCase("R32")) {
		  lcr32 += lcd;
		  lcr32c++;
	  }
	  else if(req.equalsIgnoreCase("R33")) {
		  lcr33 += lcd;
		  lcr33c++;
	  }
	  else if(req.equalsIgnoreCase("R34")) {
		  lcr34 += lcd;
		  lcr34c++;
	  }
	  else if(req.equalsIgnoreCase("R35")) {
		  lcr35 += lcd;
		  lcr35c++;
	  }
  }
  
  private void printinglcdrequirements() {
	  if(lcr10c!=0) {
		  System.out.println("R10: " + df.format(lcr10/lcr10c));
	  }
	  if(lcr11c!=0) {
		  System.out.println("R11: " + df.format(lcr11/lcr11c));
	  }
	  if(lcr12c!=0) {
		  System.out.println("R12: " + df.format(lcr12/lcr12c));
	  }
	  if(lcr13c!=0) {
		  System.out.println("R13: " + df.format(lcr13/lcr13c));
	  }
	  if(lcr15c!=0) {
		  System.out.println("R15: " + df.format(lcr15/lcr15c));
	  }
	  if(lcr16c!=0) {
		  System.out.println("R16: " + df.format(lcr16/lcr16c));
	  }
	  if(lcr17c!=0) {
		  System.out.println("R17: " + df.format(lcr17/lcr17c));
	  }
	  if(lcr18c!=0) {
		  System.out.println("R18: " + df.format(lcr18/lcr18c));
	  }
	  if(lcr19c!=0) {
		  System.out.println("R19: " + df.format(lcr19/lcr19c));
	  }
	  if(lcr20c!=0) {
		  System.out.println("R20: " + df.format(lcr20/lcr20c));
	  }
	  if(lcr21c!=0) {
		  System.out.println("R21: " + df.format(lcr21/lcr21c));
	  }
	  if(lcr22c!=0) {
		  System.out.println("R22: " + df.format(lcr22/lcr22c));
	  }
	  if(lcr23c!=0) {
		  System.out.println("R23: " + df.format(lcr23/lcr23c));
	  }
	  if(lcr24c!=0) {
		  System.out.println("R24: " + df.format(lcr24/lcr24c));
	  }
	  if(lcr25c!=0) {
		  System.out.println("R25: " + df.format(lcr25/lcr25c));
	  }
	  if(lcr26c!=0) {
		  System.out.println("R26: " + df.format(lcr26/lcr26c));
	  }
	  if(lcr27c!=0) {
		  System.out.println("R27: " + df.format(lcr27/lcr27c));
	  }
	  if(lcr28c!=0) {
		  System.out.println("R28: " + df.format(lcr28/lcr28c));
	  }
	  if(lcr29c!=0) {
		  System.out.println("R29: " + df.format(lcr29/lcr29c));
	  }
	  if(lcr30c!=0) {
		  System.out.println("R30: " + df.format(lcr30/lcr30c));
	  }
	  if(lcr31c!=0) {
		  System.out.println("R31: " + df.format(lcr31/lcr31c));
	  }
	  if(lcr32c!=0) {
		  System.out.println("R32: " + df.format(lcr32/lcr32c));
	  }
	  if(lcr33c!=0) {
		  System.out.println("R33: " + df.format(lcr33/lcr33c));
	  }
	  if(lcr34c!=0) {
		  System.out.println("R34: " + df.format(lcr34/lcr34c));
	  }
	  if(lcr35c!=0) {
		  System.out.println("R35: " + df.format(lcr35/lcr35c));
	  }
  }
  
}
