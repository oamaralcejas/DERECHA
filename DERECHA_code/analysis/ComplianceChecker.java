package lu.svv.saa.linklaters.dpa.analysis;

import static org.apache.uima.fit.util.JCasUtil.select;
import static org.apache.uima.fit.util.JCasUtil.selectCovered;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;

import lu.svv.saa.linklaters.dpa.type.ComplianceResults;
import lu.svv.saa.linklaters.dpa.type.LegalSemanticRole;
import lu.svv.saa.linklaters.dpa.type.MySentence;
import lu.svv.saa.linklaters.dpa.utils.MatchingRoles;
import lu.svv.saa.linklaters.dpa.utils.PARAMETERS_VALUES;
import lu.svv.saa.linklaters.dpa.utils.GDPRequirementsReader;

public class ComplianceChecker extends JCasAnnotator_ImplBase implements PARAMETERS_VALUES {
	
	public static final String ENRICHMENT = "enrichment";
	  @ConfigurationParameter(name = ENRICHMENT, mandatory = true,
	      description = "Level of the text enrichment", defaultValue = "0")
	private int enrichment;
	
	HashMap<String, List<HashMap<String, String>>> groundTruth = new HashMap<String, List<HashMap<String, String>>>();
	List<HashMap<String, String>> sentenceLSRs;
	String keyLSRs;
	private GDPRequirementsReader gtr;
	private MatchingRoles mr;
	boolean role_match = false;
	boolean maFlag = false, othFlag = false;
	String rm = "", rmr = "";
	String mAr = "", mAn = "", Bt = "", Ot = "", sAr = "", sAn = "", Cn = "", Ct = "", Tch = "", Rn = "", Et = "", Re = "", Ae = "", Ln = "";
	double counter = 0, confDeg = 0, sim = 0;
	
	@Override
	  public void initialize(final UimaContext context) throws ResourceInitializationException {
	    super.initialize(context);
	    gtr = new GDPRequirementsReader();
	    mr = new MatchingRoles();
	  }
	
	@SuppressWarnings("null")
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		try {
			groundTruth = gtr.ReadingGoldStandard();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("Elements in IDs: " + IDs.size() + "\n");
		// Start matching the rest of LegalSemanticRoles between what is identified in the sentence and what is in the GS
		//if(IDs != null) {
		for (MySentence mys : select(aJCas, MySentence.class)) {
			mAr = ""; mAn = ""; Bt = ""; Ot = ""; sAr = ""; sAn = ""; Cn = ""; Ct = ""; Tch = ""; Rn = ""; Et = ""; Re = ""; Ae = ""; Ln = "";
			//System.out.printf("%n== Sentence " + mys.getID() + "==%n");
		    //System.out.println(mys.getCoveredText());
			for (LegalSemanticRole lsr : selectCovered(LegalSemanticRole.class, mys)) {
				//System.out.printf("%n== Semantic Role " + lsr.getSemanticRole() + "==%n");
			    //System.out.println(lsr.getCoveredText());
				if(lsr.getSemanticRole() == MAIN_ACTOR)
					mAr = mAr + lsr.getCoveredText() + " ";	
				else if(lsr.getSemanticRole() == MAIN_ACTION)
					mAn = mAn + lsr.getCoveredText() + " ";
				else if(lsr.getSemanticRole() == BENEFICIARY_TARGET)
					Bt = Bt + lsr.getCoveredText() + " ";
				else if(lsr.getSemanticRole() == OBJECT)
					Ot = Ot + lsr.getCoveredText() + " ";
				/*else if(lsr.getSemanticRole() == SECONDARY_ACTOR)
					sAr = sAr + lsr.getCoveredText() + " ";
				else if(lsr.getSemanticRole() == SECONDARY_ACTION)
					sAn = sAn + lsr.getCoveredText() + " ";*/
				else if(lsr.getSemanticRole() == CONDITION)
					Cn = Cn + lsr.getCoveredText() + " ";
				else if(lsr.getSemanticRole() == CONSTRAINT)
					Ct = Ct + lsr.getCoveredText() + " ";
				else if(lsr.getSemanticRole() == TEMPORAL_CHARACTERISTIC)
					Tch = Tch + lsr.getCoveredText() + " ";
				else if(lsr.getSemanticRole() == REASON)
					Rn = Rn + lsr.getCoveredText() + " ";
				else if(lsr.getSemanticRole() == EVENT)
					Et = Et + lsr.getCoveredText() + " ";
				else if(lsr.getSemanticRole() == REFERENCE)
					Re = Re + lsr.getCoveredText() + " ";
				/*else if(lsr.getSemanticRole() == ATTRIBUTE)
					Ae = Ae + lsr.getCoveredText() + " ";
				else if(lsr.getSemanticRole() == LOCATION)
					Ln = Ln + lsr.getCoveredText() + " ";*/
			}
			mAr = mAr.replaceAll("\\s+"," ");
			mAn = mAn.replaceAll("\\s+"," ");
			Bt = Bt.replaceAll("\\s+"," ");
			Ot = Ot.replaceAll("\\s+"," ");
			/*sAr = sAr.replaceAll("\\s+"," ");
			sAn = sAn.replaceAll("\\s+"," ");*/
			Cn = Cn.replaceAll("\\s+"," ");
			Ct = Ct.replaceAll("\\s+"," ");
			Tch = Tch.replaceAll("\\s+"," ");
			Rn = Rn.replaceAll("\\s+"," ");
			Et = Et.replaceAll("\\s+"," ");
			Re = Re.replaceAll("\\s+"," ");
			/*Ae = Ae.replaceAll("\\s+"," ");
			Ln = Ln.replaceAll("\\s+"," ");*/
			for (Entry<String, List<HashMap<String, String>>> entry : groundTruth.entrySet()) {
				//System.out.println("Key: " + entry.getKey());
				//System.out.println("Value: " + entry.getValue() + "\n");
				sentenceLSRs = entry.getValue();
				rm = "";
				rmr = "";
				counter = 0;
				sim = 0;
				for(int j=0; j<sentenceLSRs.size(); j++){
					//System.out.println("Role and Span: " + sentenceLSRs.get(j));
					HashMap<String, String> RoleSpan = sentenceLSRs.get(j);
					ComparingRoles(RoleSpan);
					//System.out.println("These are the missing roles: " + rmr + " Cicle: " + j);
					//System.out.println("These is the value of the counter: " + counter + " Cicle: " + j + "\n");
				}
				// Generating the compliance results
				if(maFlag && othFlag) {
					confDeg = confidenceDegree(entry.getKey());
					//if(confDeg!=0)
					newComplianceResults(aJCas, mys.getCoveredText(), "S" + mys.getID(), entry.getKey(), rmr, rm, confDeg, mys.getBegin(), mys.getEnd());
					maFlag = false;
					othFlag = false;
					/*System.out.println("Sentence: " + mys.getCoveredText());
					System.out.println("Met requirement: " + entry.getKey());
					System.out.println("Matching degree: " + rm);
					System.out.println("Missing roles: " + rmr);
					System.out.println("This is the value of the counter: " + counter + "\n");*/
				}
			}
		}
	}
	
	// Comparing the GS and the identified roles
	private void ComparingRoles(HashMap<String, String> RoleSpan) {
		for (Entry<String, String> ientry : RoleSpan.entrySet()) {
			if(ientry.getKey().equalsIgnoreCase(MAIN_ACTION) && !ientry.getValue().toLowerCase().equalsIgnoreCase("na")) {
				try {
					maFlag = mr.RoleMatcher(mAn, ientry.getValue(), true, enrichment);
					//System.out.println("The value of enrichment is: " + enrichment + "\n");
					//System.out.println("ientry.getValue(): " + ientry.getValue());
					//System.out.println("mAn: " + mAn);
					//System.out.println("genFlag: " + genFlag + "\n");
					/*if(genFlag) {
						System.out.println("mAn: " + mAn);
						System.out.println("ientry.getValue(): " + ientry.getValue());
					}*/
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if(ientry.getKey().equalsIgnoreCase(MAIN_ACTOR) && !ientry.getValue().toLowerCase().equalsIgnoreCase("na")) {
				try {
					role_match = mr.RoleMatcher(mAr, ientry.getValue(), false, enrichment);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (role_match) {
					othFlag = true;
					counter++;
					//sim += 0.85;
				}
				else {
					if (rmr.isEmpty())
						rmr = rmr + MAIN_ACTOR;
					else
						rmr = rmr + ";" + MAIN_ACTOR;
				}
			}
			else if(ientry.getKey().equalsIgnoreCase(BENEFICIARY_TARGET) && !ientry.getValue().toLowerCase().equalsIgnoreCase("na")) {
				try {
					role_match = mr.RoleMatcher(Bt, ientry.getValue(), false, enrichment);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (role_match) {
					othFlag = true;
					counter++;
					//sim += 0.85;
				}
				else {
					if (rmr.isEmpty())
						rmr = rmr + BENEFICIARY_TARGET;
					else
						rmr = rmr + ";" + BENEFICIARY_TARGET;
				}
			}
			else if(ientry.getKey().equalsIgnoreCase(OBJECT) && !ientry.getValue().toLowerCase().equalsIgnoreCase("na")) {
				try {
					role_match = mr.RoleMatcher(Ot, ientry.getValue(), false, enrichment);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (role_match) {
					othFlag = true;
					counter++;
					//sim += 0.85;
				}
				else {
					if (rmr.isEmpty())
						rmr = rmr + OBJECT;
					else
						rmr = rmr + ";" + OBJECT;
				}
			}
			else if(ientry.getKey().equalsIgnoreCase(CONDITION) && !ientry.getValue().toLowerCase().equalsIgnoreCase("na")) {
				try {
					role_match = mr.RoleMatcher(Cn, ientry.getValue(), false, enrichment);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (role_match) {
					othFlag = true;
					counter++;
					//sim += 0.8;
				}
				else {
					if (rmr.isEmpty())
						rmr = rmr + CONDITION;
					else
						rmr = rmr + ";" + CONDITION;
				}
			}
			else if(ientry.getKey().equalsIgnoreCase(CONSTRAINT) && !ientry.getValue().toLowerCase().equalsIgnoreCase("na")) {
				try {
					role_match = mr.RoleMatcher(Ct, ientry.getValue(), false, enrichment);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (role_match) {
					othFlag = true;
					counter++;
					//sim += 0.8;
				}
				else {
					if (rmr.isEmpty())
						rmr = rmr + CONSTRAINT;
					else
						rmr = rmr + ";" + CONSTRAINT;
				}
			}
			else if(ientry.getKey().equalsIgnoreCase(TEMPORAL_CHARACTERISTIC) && !ientry.getValue().toLowerCase().equalsIgnoreCase("na")) {
				try {
					role_match = mr.RoleMatcher(Tch, ientry.getValue(), false, enrichment);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (role_match) {
					othFlag = true;
					counter++;
					//sim += 0.8;
				}
				else {
					if (rmr.isEmpty())
						rmr = rmr + TEMPORAL_CHARACTERISTIC;
					else
						rmr = rmr + ";" + TEMPORAL_CHARACTERISTIC;
				}
			}
			else if(ientry.getKey().equalsIgnoreCase(REASON) && !ientry.getValue().toLowerCase().equalsIgnoreCase("na")) {
				try {
					role_match = mr.RoleMatcher(Rn, ientry.getValue(), false, enrichment);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (role_match) {
					othFlag = true;
					counter++;
					//sim += 0.8;
				}
				else {
					if (rmr.isEmpty())
						rmr = rmr + REASON;
					else
						rmr = rmr + ";" + REASON;
				}
			}
			else if(ientry.getKey().equalsIgnoreCase(EVENT) && !ientry.getValue().toLowerCase().equalsIgnoreCase("na")) {
				try {
					role_match = mr.RoleMatcher(Et, ientry.getValue(), false, enrichment);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (role_match) {
					othFlag = true;
					counter++;
					//sim += 0.8;
				}
				else {
					if (rmr.isEmpty())
						rmr = rmr + EVENT;
					else
						rmr = rmr + ";" + EVENT;
				}
			}
			else if(ientry.getKey().equalsIgnoreCase(REFERENCE) && !ientry.getValue().toLowerCase().equalsIgnoreCase("na")) {
				try {
					role_match = mr.RoleMatcher(Re, ientry.getValue(), false, enrichment);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (role_match) {
					othFlag = true;
					counter++;
					//sim += 0.8;
				}
				else {
					if (rmr.isEmpty())
						rmr = rmr + REFERENCE;
					else
						rmr = rmr + ";" + REFERENCE;
				}
			}
			//System.out.println("ientry.getKey(): " + ientry.getKey());
			//System.out.println("ientry.getValue(): " + ientry.getValue());
			//System.out.println("genFlag: " + genFlag + "\n");
		}
	}
	
	// Calculating the ConfidenceDegree
	private double confidenceDegree(String str) {
			double cd = 0;
			if(str.equalsIgnoreCase("R40") || str.equalsIgnoreCase("R42")) {
				/*if(counter == 0)
					cd = 0.0;
				else*/
				cd = (counter + 1)/3;	//cd = (sim + 1)/3;
				if(counter < 2)
					rm = "Partial";
				else
					rm = "Total";
			}
			else if(str.equalsIgnoreCase("R12") || str.equalsIgnoreCase("R22") || str.equalsIgnoreCase("R26")
			|| str.equalsIgnoreCase("R37") || str.equalsIgnoreCase("R38") || str.equalsIgnoreCase("R43")
			|| str.equalsIgnoreCase("R44")) {
				/*if(counter == 0)
					cd = 0.0;
				else*/
				cd = (counter + 1)/4;	//cd = (sim + 1)/4;
				if(counter < 3)
					rm = "Partial";
				else
					rm = "Total";
			}
			else if(str.equalsIgnoreCase("R10") || str.equalsIgnoreCase("R15") || str.equalsIgnoreCase("R16")
			|| str.equalsIgnoreCase("R18") || str.equalsIgnoreCase("R20") || str.equalsIgnoreCase("R21")
			|| str.equalsIgnoreCase("R24") || str.equalsIgnoreCase("R28") || str.equalsIgnoreCase("R29")
			|| str.equalsIgnoreCase("R31") || str.equalsIgnoreCase("R32") || str.equalsIgnoreCase("R33")
			|| str.equalsIgnoreCase("R34") || str.equalsIgnoreCase("R39") || str.equalsIgnoreCase("R41")
			|| str.equalsIgnoreCase("R46")) {
				/*if(counter == 0)
					cd = 0.0;
				else*/
				cd = (counter + 1)/5;	//cd = (sim + 1)/5;
				if(counter < 4)
					rm = "Partial";
				else
					rm = "Total";
			}
			else if(str.equalsIgnoreCase("R11") || str.equalsIgnoreCase("R17") || str.equalsIgnoreCase("R23")
			|| str.equalsIgnoreCase("R25") || str.equalsIgnoreCase("R27") || str.equalsIgnoreCase("R30")) {
				/*if(counter == 0)
					cd = 0.0;
				else*/
				cd = (counter + 1)/6;	//cd = (sim + 1)/6;
				if(counter < 5)
					rm = "Partial";
				else
					rm = "Total";
			}
			else if(str.equalsIgnoreCase("R13") || str.equalsIgnoreCase("R35") || str.equalsIgnoreCase("R36")
			|| str.equalsIgnoreCase("R45")) {
				/*if(counter == 0)
					cd = 0.0;
				else*/
				cd = (counter + 1)/7;	//cd = (sim + 1)/7;
				if(counter < 6)
					rm = "Partial";
				else
					rm = "Total";
			}
			else if(str.equalsIgnoreCase("R19")) {
				/*if(counter == 0)
					cd = 0.0;
				else*/
				cd = (counter + 1)/8;	//cd = (sim + 1)/8;
				if(counter < 7)
					rm = "Partial";
				else
					rm = "Total";
			}
			//System.out.println("The CD value: " + cd);
			return cd;
		}
	
	// The newComplianceResults generator
	private void newComplianceResults(JCas jcas, String st,String sid, String rid, String rmr, String rmd, double cd,int begin, int end) {
		ComplianceResults cr = new ComplianceResults(jcas);
		cr.setSentenceText(st);
		cr.setSentenceID(sid);
		cr.setRequirementID(rid);
		cr.setMissingRoles(rmr);
		cr.setMatchingDegree(rmd);
		cr.setConfidenceDegree(cd);
		cr.setBegin(begin);
		cr.setEnd(end);
		cr.addToIndexes();
	}
		
}
