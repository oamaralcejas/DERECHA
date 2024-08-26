package lu.svv.saa.linklaters.dpa.analysis;

import static org.apache.uima.fit.util.JCasUtil.select;
import static org.apache.uima.fit.util.JCasUtil.selectCovered;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.component.JCasAnnotator_ImplBase;

import lu.svv.saa.linklaters.dpa.type.LegalSemanticRole;
import lu.svv.saa.linklaters.dpa.type.MySentence;
import lu.svv.saa.linklaters.dpa.utils.PARAMETERS_VALUES;

public class LSRLEvaluator extends JCasAnnotator_ImplBase implements PARAMETERS_VALUES {

	public static final String PARAM_INPUT_PATH = "inputPath";
	  @ConfigurationParameter(name = PARAM_INPUT_PATH, mandatory = true,
	      description = "Input path for ground truth")
	 private String inputPath;
	
	HashMap<String, List<String>> goldstandard = new HashMap<String, List<String>>();
	HashMap<String, List<String>> predictions = new HashMap<String, List<String>>();
	HashMap<String, String> sentenceTexts = new HashMap<String, String>();
	
	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {	
		// complete evaluator
		for (MySentence mysentence : select(aJCas, MySentence.class)) {
			// loop over LegalSemanticRole type in the sentence
			System.out.printf("%n== Sentence_" + mysentence.getID() + "==%n");
			System.out.println(mysentence.getText());

			for (LegalSemanticRole lsr : selectCovered(LegalSemanticRole.class, mysentence)) {
				// print the semantic roles and their respective text spans
				try {
					System.out.println(lsr.getCoveredText() + " has the semantic role <" + lsr.getSemanticRole() + ">");
				} catch (StringIndexOutOfBoundsException e) {
					System.out.println("Exception occurred . . . . . . . . Role: " + lsr.getSemanticRole());
				}
			}
			System.out.println();
		}
		// loop over the sentences
		for (MySentence mys : select(aJCas, MySentence.class)) {
			List<String> LSRs = new ArrayList<String>();
			String ID = "", senText = "", mActor = "", mAction = "", BT = "", Object = "", sActor = "", sAction = "", Condition = "", Constraint = "", TCh = "", Reason = "", Event = "", Reference = "", Attribute = "", Location = "";
			// loop over MySentence to obtain the sentence ID and text
			ID = "S" + mys.getID();
			//-----------------------------------------------------------------
			// New
			//-----------------------------------------------------------------
			//senText = mys.getText();
			senText = mys.getText().replace(";", ",");
			if(senText.endsWith(" "))
				senText = senText.substring(0, senText.length()-1);
			//-----------------------------------------------------------------
			// New
			//-----------------------------------------------------------------
			//System.out.println("Sentence " + ID + ": " + senText + "\n");
			// loop over LegalSemanticRole to obtain the Legal Semantic Roles
			for (LegalSemanticRole lsr : selectCovered(LegalSemanticRole.class, mys)) {
				try {
					/*System.out.println("Sentence " + ID + ": " + senText + "\n");
					System.out.println("Semantic Role: " + lsr.getSemanticRole());
					System.out.println("Semantic Role: " + lsr.getCoveredText() + "\n");*/
					if(lsr.getSemanticRole() == "Main Actor")
						mActor = mActor + lsr.getCoveredText() + " ";
					else if(lsr.getSemanticRole() == "Main Action")
						mAction = mAction + lsr.getCoveredText() + " ";
					else if(lsr.getSemanticRole() == "Beneficiary/Target")
						BT = BT + lsr.getCoveredText() + " ";
					else if(lsr.getSemanticRole() == "Object")
						Object = Object + lsr.getCoveredText() + " ";
					else if(lsr.getSemanticRole() == "Secondary Actor")
						sActor = sActor + lsr.getCoveredText() + " ";
					else if(lsr.getSemanticRole() == "Secondary Action")
						sAction = sAction + lsr.getCoveredText() + " ";
					else if(lsr.getSemanticRole() == "Condition")
						Condition = Condition + lsr.getCoveredText() + " ";
					else if(lsr.getSemanticRole() == "Constraint")
						Constraint = Constraint + lsr.getCoveredText() + " ";
					else if(lsr.getSemanticRole() == "Temporal Characteristic")
						TCh = TCh + lsr.getCoveredText() + " ";
					else if(lsr.getSemanticRole() == "Reason")
						Reason = Reason + lsr.getCoveredText() + " ";
					else if(lsr.getSemanticRole() == "Event")
						Event = Event + lsr.getCoveredText() + " ";
					else if(lsr.getSemanticRole() == "Reference")
						Reference = Reference + lsr.getCoveredText() + " ";
					else if(lsr.getSemanticRole() == "Attribute")
						Attribute = Attribute + lsr.getCoveredText() + " ";
					else if(lsr.getSemanticRole() == "Location")
						Location = Location + lsr.getCoveredText() + " ";
				} catch (StringIndexOutOfBoundsException e) {
					/*System.out.println("Exception occurred . . . . . . . . Sentence: " + senText);
					System.out.println("Exception occurred . . . . . . . . ID: " + ID);
					System.out.println("Exception occurred . . . . . . . . Role: " + lsr.getSemanticRole() + "\n");*/
				}
									
			}
			if(!mActor.isEmpty()) {
				mActor = mActor.replaceAll("\\s+"," ");
				mActor = mActor.substring(0, mActor.length()-1);
				LSRs.add(mActor);
			}
			else
				LSRs.add("NA");
			
			if(!mAction.isEmpty()) {
				mAction = mAction.replaceAll("\\s+"," ");
				mAction = mAction.substring(0, mAction.length()-1);
				LSRs.add(mAction);
			}
			else
				LSRs.add("NA");
			
			if(!BT.isEmpty()) {
				BT = BT.replaceAll("\\s+"," ");
				BT = BT.substring(0, BT.length()-1);
				LSRs.add(BT);
			}
			else
				LSRs.add("NA");
			
			if(!Object.isEmpty()) {
				Object = Object.replaceAll("\\s+"," ");
				Object = Object.substring(0, Object.length()-1);
				LSRs.add(Object);
			}
			else
				LSRs.add("NA");
			
			if(!sActor.isEmpty()) {
				sActor = sActor.replaceAll("\\s+"," ");
				sActor = sActor.substring(0, sActor.length()-1);
				LSRs.add(sActor);
			}
			else
				LSRs.add("NA");
			
			if(!sAction.isEmpty()) {
				sAction = sAction.replaceAll("\\s+"," ");
				sAction = sAction.substring(0, sAction.length()-1);
				LSRs.add(sAction);
			}
			else
				LSRs.add("NA");
			
			if(!Condition.isEmpty()) {
				Condition = Condition.replaceAll("\\s+"," ");
				Condition = Condition.substring(0, Condition.length()-1);
				LSRs.add(Condition);
			}
			else
				LSRs.add("NA");
			
			if(!Constraint.isEmpty()) {
				Constraint = Constraint.replaceAll("\\s+"," ");
				Constraint = Constraint.substring(0, Constraint.length()-1);
				LSRs.add(Constraint);
			}
			else
				LSRs.add("NA");
			
			if(!TCh.isEmpty()) {
				TCh = TCh.replaceAll("\\s+"," ");
				TCh = TCh.substring(0, TCh.length()-1);
				LSRs.add(TCh);
			}
			else
				LSRs.add("NA");
			
			if(!Reason.isEmpty()) {
				Reason = Reason.replaceAll("\\s+"," ");
				Reason = Reason.substring(0, Reason.length()-1);
				LSRs.add(Reason);
			}
			else
				LSRs.add("NA");
			
			if(!Event.isEmpty()) {
				Event = Event.replaceAll("\\s+"," ");
				Event = Event.substring(0, Event.length()-1);
				LSRs.add(Event);
			}
			else
				LSRs.add("NA");
			
			if(!Reference.isEmpty()) {
				Reference = Reference.replaceAll("\\s+"," ");
				Reference = Reference.substring(0, Reference.length()-1);
				LSRs.add(Reference);
			}
			else
				LSRs.add("NA");
			
			if(!Attribute.isEmpty()) {
				Attribute = Attribute.replaceAll("\\s+"," ");
				Attribute = Attribute.substring(0, Attribute.length()-1);
				LSRs.add(Attribute);
			}
			else
				LSRs.add("NA");
			
			if(!Location.isEmpty()) {
				Location = Location.replaceAll("\\s+"," ");
				Location = Location.substring(0, Location.length()-1);
				LSRs.add(Location);
			}
			else
				LSRs.add("NA");
			
			sentenceTexts.put(ID, senText);
			//-----------------------------------------------------------------
			// New
			//-----------------------------------------------------------------
			//predictions.put(ID, LSRs);
			predictions.put(senText, LSRs);
			//-----------------------------------------------------------------
			// New
			//-----------------------------------------------------------------
			/*System.out.println("Sentence " + ID + ": " + senText);
			System.out.println("Legal Semantic Roles: " + LSRs + "\n");
			System.out.println("mActor: " + mActor);
			System.out.println("mAction: " + mAction);
			System.out.println("BT: " + BT);
			System.out.println("Object: " + Object);
			System.out.println("sActor: " + sActor);
			System.out.println("sAction: " + sAction);
			System.out.println("Condition: " + Condition);
			System.out.println("Constraint: " + Constraint);
			System.out.println("TCh: " + TCh);
			System.out.println("Reason: " + Reason);
			System.out.println("Event: " + Event);
			System.out.println("Reference: " + Reference);
			System.out.println("Attribute: " + Attribute);
			System.out.println("Location: " + Location + "\n");*/
		}
		try {
			//System.out.println("The path of the csv document is: " + inputPath);
			ReadGT(inputPath + ".csv");
			//ReadGT(PATH_CSV + DOC + ".csv");
			evaluate();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	
	//Reading the Ground Truth
	@SuppressWarnings("resource")
	void ReadGT(String path) throws Exception {
		//Reading the CSV file
		File csvFile = new File(path);
		if (csvFile.isFile()) {
			// create BufferedReader and read data from csv
			BufferedReader csvReader = new BufferedReader(new FileReader(path));
			String row = csvReader.readLine();
			while ((row = csvReader.readLine()) != null) {						
				String[] fields = row.split(",");
				if(fields.length!=15)
					throw new Exception("Error parsing the following sentence: \"" + row + "\"");
				List<String> GTLSRs = new ArrayList<String>();
				String id = fields[0].replace(";", ",");
				for (int i=1;i<15;i++) {
					fields[i] = fields[i].replace(";", ",");
					GTLSRs.add(fields[i]);
				}
				goldstandard.put(id, GTLSRs);
				//System.out.println("Sentence ID: " + id);
				//System.out.println("Sentence LSRs: " + GTLSRs + "\n");
			}
			csvReader.close();
		 }
	}
	
	private void evaluate() throws Exception {
		String ID  = "";
		List<String> groundtruthLSRs = new ArrayList<String>();
		List<String> predictedLSRs = new ArrayList<String>();
		String sentenceText = "";
		int TP = 0, TN = 0, FP = 0, FN = 0, Counter = 0, RS = 0, RPS = 0, TRS = 46;
		int MAR_TP = 0, MAN_TP = 0, SAR_TP = 0, SAN_TP = 0, BT_TP = 0, OBJ_TP = 0, CN_TP = 0, CT_TP = 0, TCH_TP = 0, RN_TP = 0, ET_TP = 0, RE_TP = 0, AE_TP = 0, LN_TP = 0;
		int MAR_TN = 0, MAN_TN = 0, SAR_TN = 0, SAN_TN = 0, BT_TN = 0, OBJ_TN = 0, CN_TN = 0, CT_TN = 0, TCH_TN = 0, RN_TN = 0, ET_TN = 0, RE_TN = 0, AE_TN = 0, LN_TN = 0;
		int MAR_FP = 0, MAN_FP = 0, SAR_FP = 0, SAN_FP = 0, BT_FP = 0, OBJ_FP = 0, CN_FP = 0, CT_FP = 0, TCH_FP = 0, RN_FP = 0, ET_FP = 0, RE_FP = 0, AE_FP = 0, LN_FP = 0;
		int MAR_FN = 0, MAN_FN = 0, SAR_FN = 0, SAN_FN = 0, BT_FN = 0, OBJ_FN = 0, CN_FN = 0, CT_FN = 0, TCH_FN = 0, RN_FN = 0, ET_FN = 0, RE_FN = 0, AE_FN = 0, LN_FN = 0;
		float A = 0, P = 0, R = 0, F1 = 0, RS_M = 0, RS_RPS_M = 0;
		boolean flag = false;
		for (Entry<String, List<String>> entry : goldstandard.entrySet()) {
			ID = entry.getKey();
			groundtruthLSRs = entry.getValue();
			predictedLSRs = predictions.get(ID);
			sentenceText = sentenceTexts.get(ID);
			List<String> toshow = new ArrayList<String>();
			HashMap<String, List<String>> LSRsToShow = new HashMap<String, List<String>>();
			//System.out.println("Sentence " + ID + ": ");	// + sentenceText
			//System.out.println("Sentence LSRs: " + groundtruthLSRs);
			//System.out.println("Predicted LSRs: " + predictedLSRs + "\n");
			//In case that the sentence doesn't have any LSR
			if(predictedLSRs == null) {
				System.out.println("...");
		        //System.out.println("No legal semantic role was identified for the sentence " + ID);	// + ":\t" + sentenceText
		        //System.in.read();
		        //throw new Exception("No legal semantic role was identified for the sent: " + sentenceText);
		    }
			else {
				//Compare the predicted LSRs against the groundtruth LSRs
				System.out.println("Sentence " + ID + ": ");	// + sentenceText
				//---------------------------------------------------------------------------------------------------------------------------------------
				//Main Actor
				//---------------------------------------------------------------------------------------------------------------------------------------
				String mainactr = groundtruthLSRs.get(MAIN_ACTOR_INDEX).substring(0, groundtruthLSRs.get(MAIN_ACTOR_INDEX).length() - 1);
				if(groundtruthLSRs.get(MAIN_ACTOR_INDEX).endsWith(" "))
					groundtruthLSRs.set(MAIN_ACTOR_INDEX, mainactr);
				mainactr = predictedLSRs.get(MAIN_ACTOR_INDEX).substring(0, predictedLSRs.get(MAIN_ACTOR_INDEX).length() - 1);
				if(predictedLSRs.get(MAIN_ACTOR_INDEX).endsWith(" "))
					predictedLSRs.set(MAIN_ACTOR_INDEX, mainactr);
				
				if(groundtruthLSRs.get(MAIN_ACTOR_INDEX).equalsIgnoreCase(predictedLSRs.get(MAIN_ACTOR_INDEX)) && groundtruthLSRs.get(MAIN_ACTOR_INDEX).equalsIgnoreCase("NA")) {
					System.out.println("There is no role to match");
					TN++;
					MAR_TN++;
				}
				else if((groundtruthLSRs.get(MAIN_ACTOR_INDEX).equalsIgnoreCase(predictedLSRs.get(MAIN_ACTOR_INDEX))) || (groundtruthLSRs.get(MAIN_ACTOR_INDEX).toLowerCase().contains(predictedLSRs.get(MAIN_ACTOR_INDEX).toLowerCase()) && !predictedLSRs.get(MAIN_ACTOR_INDEX).equalsIgnoreCase("NA")) || (predictedLSRs.get(MAIN_ACTOR_INDEX).toLowerCase().contains(groundtruthLSRs.get(MAIN_ACTOR_INDEX).toLowerCase()) && !groundtruthLSRs.get(MAIN_ACTOR_INDEX).equalsIgnoreCase("NA")) || (StringUtils.getJaroWinklerDistance(predictedLSRs.get(MAIN_ACTOR_INDEX).toLowerCase(), groundtruthLSRs.get(MAIN_ACTOR_INDEX).toLowerCase())>THRESHOLD_LSR)) {
					System.out.println("The text for the role Main Actor matches");
					TP++;
					MAR_TP++;
				}
				else if(!groundtruthLSRs.get(MAIN_ACTOR_INDEX).equalsIgnoreCase(predictedLSRs.get(MAIN_ACTOR_INDEX)) && groundtruthLSRs.get(MAIN_ACTOR_INDEX).equalsIgnoreCase("NA")) {
					System.out.println("The text for the role Main Actor does not match");
					FP++;
					MAR_FP++;
					Counter++;
					flag = true;
					toshow.add(groundtruthLSRs.get(MAIN_ACTOR_INDEX));
					toshow.add(predictedLSRs.get(MAIN_ACTOR_INDEX));
					LSRsToShow.put("Main Actor", toshow);
					toshow = new ArrayList<String>();
				}
				else if(!groundtruthLSRs.get(MAIN_ACTOR_INDEX).equalsIgnoreCase(predictedLSRs.get(MAIN_ACTOR_INDEX))) {
					System.out.println("The text for the role Main Actor does not match");
					FN++;
					MAR_FN++;
					Counter++;
					flag = true;
					toshow.add(groundtruthLSRs.get(MAIN_ACTOR_INDEX));
					toshow.add(predictedLSRs.get(MAIN_ACTOR_INDEX));
					LSRsToShow.put("Main Actor", toshow);
					toshow = new ArrayList<String>();
				}
				//---------------------------------------------------------------------------------------------------------------------------------------
				//Main Actor
				//---------------------------------------------------------------------------------------------------------------------------------------
				
				//---------------------------------------------------------------------------------------------------------------------------------------
				//Main Action
				//---------------------------------------------------------------------------------------------------------------------------------------
				String mainactn = groundtruthLSRs.get(MAIN_ACTION_INDEX).substring(0, groundtruthLSRs.get(MAIN_ACTION_INDEX).length() - 1);
				if(groundtruthLSRs.get(MAIN_ACTION_INDEX).endsWith(" "))
					groundtruthLSRs.set(MAIN_ACTION_INDEX, mainactn);
				mainactn = predictedLSRs.get(MAIN_ACTION_INDEX).substring(0, predictedLSRs.get(MAIN_ACTION_INDEX).length() - 1);
				if(predictedLSRs.get(MAIN_ACTION_INDEX).endsWith(" "))
					predictedLSRs.set(MAIN_ACTION_INDEX, mainactn);
				
				if(groundtruthLSRs.get(MAIN_ACTION_INDEX).equalsIgnoreCase(predictedLSRs.get(MAIN_ACTION_INDEX)) && groundtruthLSRs.get(MAIN_ACTION_INDEX).equalsIgnoreCase("NA")) {
					System.out.println("There is no role to match");
					TN++;
					MAN_TN++;
				}
				else if((groundtruthLSRs.get(MAIN_ACTION_INDEX).equalsIgnoreCase(predictedLSRs.get(MAIN_ACTION_INDEX))) || (groundtruthLSRs.get(MAIN_ACTION_INDEX).toLowerCase().contains(predictedLSRs.get(MAIN_ACTION_INDEX).toLowerCase()) && !predictedLSRs.get(MAIN_ACTION_INDEX).equalsIgnoreCase("NA")) || (predictedLSRs.get(MAIN_ACTION_INDEX).toLowerCase().contains(groundtruthLSRs.get(MAIN_ACTION_INDEX).toLowerCase()) && !groundtruthLSRs.get(MAIN_ACTION_INDEX).equalsIgnoreCase("NA")) || (StringUtils.getJaroWinklerDistance(predictedLSRs.get(MAIN_ACTION_INDEX).toLowerCase(), groundtruthLSRs.get(MAIN_ACTION_INDEX).toLowerCase())>THRESHOLD_LSR)) {
					System.out.println("The text for the role Main Action matches");
					TP++;
					MAN_TP++;
				}
				else if(!groundtruthLSRs.get(MAIN_ACTION_INDEX).equalsIgnoreCase(predictedLSRs.get(MAIN_ACTION_INDEX)) && groundtruthLSRs.get(MAIN_ACTION_INDEX).equalsIgnoreCase("NA")) {
					System.out.println("The text for the role Main Action does not match");
					FP++;
					MAN_FP++;
					Counter++;
					flag = true;
					toshow.add(groundtruthLSRs.get(MAIN_ACTION_INDEX));
					toshow.add(predictedLSRs.get(MAIN_ACTION_INDEX));
					LSRsToShow.put("Main Action", toshow);
					toshow = new ArrayList<String>();
				}
				else if(!groundtruthLSRs.get(MAIN_ACTION_INDEX).equalsIgnoreCase(predictedLSRs.get(MAIN_ACTION_INDEX))) {
					System.out.println("The text for the role Main Action does not match");
					FN++;
					MAN_FN++;
					Counter++;
					flag = true;
					toshow.add(groundtruthLSRs.get(MAIN_ACTION_INDEX));
					toshow.add(predictedLSRs.get(MAIN_ACTION_INDEX));
					LSRsToShow.put("Main Action", toshow);
					toshow = new ArrayList<String>();
				}
				//---------------------------------------------------------------------------------------------------------------------------------------
				//Main Action
				//---------------------------------------------------------------------------------------------------------------------------------------
				
				//---------------------------------------------------------------------------------------------------------------------------------------
				//Beneficiary/Target
				//---------------------------------------------------------------------------------------------------------------------------------------
				String bentar = groundtruthLSRs.get(BENEFICIARY_TARGET_INDEX).substring(0, groundtruthLSRs.get(BENEFICIARY_TARGET_INDEX).length() - 1);
				if(groundtruthLSRs.get(BENEFICIARY_TARGET_INDEX).endsWith(" "))
					groundtruthLSRs.set(BENEFICIARY_TARGET_INDEX, bentar);
				bentar = predictedLSRs.get(BENEFICIARY_TARGET_INDEX).substring(0, predictedLSRs.get(BENEFICIARY_TARGET_INDEX).length() - 1);
				if(predictedLSRs.get(BENEFICIARY_TARGET_INDEX).endsWith(" "))
					predictedLSRs.set(BENEFICIARY_TARGET_INDEX, bentar);
				
				if(groundtruthLSRs.get(BENEFICIARY_TARGET_INDEX).equalsIgnoreCase(predictedLSRs.get(BENEFICIARY_TARGET_INDEX)) && groundtruthLSRs.get(BENEFICIARY_TARGET_INDEX).equalsIgnoreCase("NA")) {
					System.out.println("There is no role to match");
					TN++;
					BT_TN++;
				}
				else if((groundtruthLSRs.get(BENEFICIARY_TARGET_INDEX).equalsIgnoreCase(predictedLSRs.get(BENEFICIARY_TARGET_INDEX))) || (groundtruthLSRs.get(BENEFICIARY_TARGET_INDEX).toLowerCase().contains(predictedLSRs.get(BENEFICIARY_TARGET_INDEX).toLowerCase()) && !predictedLSRs.get(BENEFICIARY_TARGET_INDEX).equalsIgnoreCase("NA")) || (predictedLSRs.get(BENEFICIARY_TARGET_INDEX).toLowerCase().contains(groundtruthLSRs.get(BENEFICIARY_TARGET_INDEX).toLowerCase()) && !groundtruthLSRs.get(BENEFICIARY_TARGET_INDEX).equalsIgnoreCase("NA")) || (StringUtils.getJaroWinklerDistance(predictedLSRs.get(BENEFICIARY_TARGET_INDEX).toLowerCase(), groundtruthLSRs.get(BENEFICIARY_TARGET_INDEX).toLowerCase())>THRESHOLD_LSR)) {
					System.out.println("The text for the role Beneficiary/Target matches");
					TP++;
					BT_TP++;
				}
				else if(!groundtruthLSRs.get(BENEFICIARY_TARGET_INDEX).equalsIgnoreCase(predictedLSRs.get(BENEFICIARY_TARGET_INDEX)) && groundtruthLSRs.get(BENEFICIARY_TARGET_INDEX).equalsIgnoreCase("NA")) {
					System.out.println("The text for the role Beneficiary/Target does not match");
					FP++;
					BT_FP++;
					Counter++;
					flag = true;
					toshow.add(groundtruthLSRs.get(BENEFICIARY_TARGET_INDEX));
					toshow.add(predictedLSRs.get(BENEFICIARY_TARGET_INDEX));
					LSRsToShow.put("Beneficiary/Target", toshow);
					toshow = new ArrayList<String>();
				}
				else if(!groundtruthLSRs.get(BENEFICIARY_TARGET_INDEX).equalsIgnoreCase(predictedLSRs.get(BENEFICIARY_TARGET_INDEX))) {
					System.out.println("The text for the role Beneficiary/Target does not match");
					FN++;
					BT_FN++;
					Counter++;
					flag = true;
					toshow.add(groundtruthLSRs.get(BENEFICIARY_TARGET_INDEX));
					toshow.add(predictedLSRs.get(BENEFICIARY_TARGET_INDEX));
					LSRsToShow.put("Beneficiary/Target", toshow);
					toshow = new ArrayList<String>();
				}
				//---------------------------------------------------------------------------------------------------------------------------------------
				//Beneficiary/Target
				//---------------------------------------------------------------------------------------------------------------------------------------
				
				//---------------------------------------------------------------------------------------------------------------------------------------
				//Object
				//---------------------------------------------------------------------------------------------------------------------------------------
				String object = groundtruthLSRs.get(OBJECT_INDEX).substring(0, groundtruthLSRs.get(OBJECT_INDEX).length() - 1);
				if(groundtruthLSRs.get(OBJECT_INDEX).endsWith(" "))
					groundtruthLSRs.set(OBJECT_INDEX, object);
				object = predictedLSRs.get(OBJECT_INDEX).substring(0, predictedLSRs.get(OBJECT_INDEX).length() - 1);
				if(predictedLSRs.get(OBJECT_INDEX).endsWith(" "))
					predictedLSRs.set(OBJECT_INDEX, object);
				
				if(groundtruthLSRs.get(OBJECT_INDEX).equalsIgnoreCase(predictedLSRs.get(OBJECT_INDEX)) && groundtruthLSRs.get(OBJECT_INDEX).equalsIgnoreCase("NA")) {
					System.out.println("There is no role to match");
					TN++;
					OBJ_TN++;
				}
				else if((groundtruthLSRs.get(OBJECT_INDEX).equalsIgnoreCase(predictedLSRs.get(OBJECT_INDEX))) || (groundtruthLSRs.get(OBJECT_INDEX).toLowerCase().contains(predictedLSRs.get(OBJECT_INDEX).toLowerCase()) && !predictedLSRs.get(OBJECT_INDEX).equalsIgnoreCase("NA")) || (predictedLSRs.get(OBJECT_INDEX).toLowerCase().contains(groundtruthLSRs.get(OBJECT_INDEX).toLowerCase()) && !groundtruthLSRs.get(OBJECT_INDEX).equalsIgnoreCase("NA")) || (StringUtils.getJaroWinklerDistance(predictedLSRs.get(OBJECT_INDEX).toLowerCase(), groundtruthLSRs.get(OBJECT_INDEX).toLowerCase())>THRESHOLD_LSR)) {
					System.out.println("The text for the role Object matches");
					TP++;
					OBJ_TP++;
				}
				else if(!groundtruthLSRs.get(OBJECT_INDEX).equalsIgnoreCase(predictedLSRs.get(OBJECT_INDEX)) && groundtruthLSRs.get(OBJECT_INDEX).equalsIgnoreCase("NA")) {
					System.out.println("The text for the role Object does not match");
					FP++;
					OBJ_FP++;
					Counter++;
					flag = true;
					toshow.add(groundtruthLSRs.get(OBJECT_INDEX));
					toshow.add(predictedLSRs.get(OBJECT_INDEX));
					LSRsToShow.put("Object", toshow);
					toshow = new ArrayList<String>();
				}
				else if(!groundtruthLSRs.get(OBJECT_INDEX).equalsIgnoreCase(predictedLSRs.get(OBJECT_INDEX))) {
					System.out.println("The text for the role Object does not match");
					FN++;
					OBJ_FN++;
					Counter++;
					flag = true;
					toshow.add(groundtruthLSRs.get(OBJECT_INDEX));
					toshow.add(predictedLSRs.get(OBJECT_INDEX));
					LSRsToShow.put("Object", toshow);
					toshow = new ArrayList<String>();
				}
				//---------------------------------------------------------------------------------------------------------------------------------------
				//Object
				//---------------------------------------------------------------------------------------------------------------------------------------
				
				//---------------------------------------------------------------------------------------------------------------------------------------
				//Condition
				//---------------------------------------------------------------------------------------------------------------------------------------
				String condition = groundtruthLSRs.get(CONDITION_INDEX).substring(0, groundtruthLSRs.get(CONDITION_INDEX).length() - 1);
				if(groundtruthLSRs.get(CONDITION_INDEX).endsWith(" "))
					groundtruthLSRs.set(CONDITION_INDEX, condition);
				condition = predictedLSRs.get(CONDITION_INDEX).substring(0, predictedLSRs.get(CONDITION_INDEX).length() - 1);
				if(predictedLSRs.get(CONDITION_INDEX).endsWith(" "))
					predictedLSRs.set(CONDITION_INDEX, condition);
				
				if(groundtruthLSRs.get(CONDITION_INDEX).equalsIgnoreCase(predictedLSRs.get(CONDITION_INDEX)) && groundtruthLSRs.get(CONDITION_INDEX).equalsIgnoreCase("NA")) {
					System.out.println("There is no role to match");
					TN++;
					CN_TN++;
				}
				else if((groundtruthLSRs.get(CONDITION_INDEX).equalsIgnoreCase(predictedLSRs.get(CONDITION_INDEX))) || (groundtruthLSRs.get(CONDITION_INDEX).toLowerCase().contains(predictedLSRs.get(CONDITION_INDEX).toLowerCase()) && !predictedLSRs.get(CONDITION_INDEX).equalsIgnoreCase("NA")) || (predictedLSRs.get(CONDITION_INDEX).toLowerCase().contains(groundtruthLSRs.get(CONDITION_INDEX).toLowerCase()) && !groundtruthLSRs.get(CONDITION_INDEX).equalsIgnoreCase("NA")) || (StringUtils.getJaroWinklerDistance(predictedLSRs.get(CONDITION_INDEX).toLowerCase(), groundtruthLSRs.get(CONDITION_INDEX).toLowerCase())>THRESHOLD_LSR)) {
					System.out.println("The text for the role Condition matches");
					TP++;
					CN_TP++;
				}
				else if(!groundtruthLSRs.get(CONDITION_INDEX).equalsIgnoreCase(predictedLSRs.get(CONDITION_INDEX)) && groundtruthLSRs.get(CONDITION_INDEX).equalsIgnoreCase("NA")) {
					System.out.println("The text for the role Condition does not match");
					FP++;
					CN_FP++;
					Counter++;
					toshow.add(groundtruthLSRs.get(CONDITION_INDEX));
					toshow.add(predictedLSRs.get(CONDITION_INDEX));
					LSRsToShow.put("Condition", toshow);
					toshow = new ArrayList<String>();
				}
				else if(!groundtruthLSRs.get(CONDITION_INDEX).equalsIgnoreCase(predictedLSRs.get(CONDITION_INDEX))) {
					System.out.println("The text for the role Condition does not match");
					FN++;
					CN_FN++;
					Counter++;
					toshow.add(groundtruthLSRs.get(CONDITION_INDEX));
					toshow.add(predictedLSRs.get(CONDITION_INDEX));
					LSRsToShow.put("Condition", toshow);
					toshow = new ArrayList<String>();
				}
				//---------------------------------------------------------------------------------------------------------------------------------------
				//Condition
				//---------------------------------------------------------------------------------------------------------------------------------------
				
				//---------------------------------------------------------------------------------------------------------------------------------------
				//Constraint
				//---------------------------------------------------------------------------------------------------------------------------------------
				String constraint = groundtruthLSRs.get(CONSTRAINT_INDEX).substring(0, groundtruthLSRs.get(CONSTRAINT_INDEX).length() - 1);
				if(groundtruthLSRs.get(CONSTRAINT_INDEX).endsWith(" "))
					groundtruthLSRs.set(CONSTRAINT_INDEX, constraint);
				constraint = predictedLSRs.get(CONSTRAINT_INDEX).substring(0, predictedLSRs.get(CONSTRAINT_INDEX).length() - 1);
				if(predictedLSRs.get(CONSTRAINT_INDEX).endsWith(" "))
					predictedLSRs.set(CONSTRAINT_INDEX, constraint);
				
				if(groundtruthLSRs.get(CONSTRAINT_INDEX).equalsIgnoreCase(predictedLSRs.get(CONSTRAINT_INDEX)) && groundtruthLSRs.get(CONSTRAINT_INDEX).equalsIgnoreCase("NA")) {
					System.out.println("There is no role to match");
					TN++;
					CT_TN++;
				}
				else if((groundtruthLSRs.get(CONSTRAINT_INDEX).equalsIgnoreCase(predictedLSRs.get(CONSTRAINT_INDEX))) || (groundtruthLSRs.get(CONSTRAINT_INDEX).toLowerCase().contains(predictedLSRs.get(CONSTRAINT_INDEX).toLowerCase()) && !predictedLSRs.get(CONSTRAINT_INDEX).equalsIgnoreCase("NA")) || (predictedLSRs.get(CONSTRAINT_INDEX).toLowerCase().contains(groundtruthLSRs.get(CONSTRAINT_INDEX).toLowerCase()) && !groundtruthLSRs.get(CONSTRAINT_INDEX).equalsIgnoreCase("NA")) || (StringUtils.getJaroWinklerDistance(predictedLSRs.get(CONSTRAINT_INDEX).toLowerCase(), groundtruthLSRs.get(CONSTRAINT_INDEX).toLowerCase())>THRESHOLD_LSR)) {
					System.out.println("The text for the role Constraint matches");
					TP++;
					CT_TP++;
				}
				else if(!groundtruthLSRs.get(CONSTRAINT_INDEX).equalsIgnoreCase(predictedLSRs.get(CONSTRAINT_INDEX)) && groundtruthLSRs.get(CONSTRAINT_INDEX).equalsIgnoreCase("NA")) {
					System.out.println("The text for the role Constraint does not match");
					FP++;
					CT_FP++;
					Counter++;
					toshow.add(groundtruthLSRs.get(CONSTRAINT_INDEX));
					toshow.add(predictedLSRs.get(CONSTRAINT_INDEX));
					LSRsToShow.put("Constraint", toshow);
					toshow = new ArrayList<String>();
				}
				else if(!groundtruthLSRs.get(CONSTRAINT_INDEX).equalsIgnoreCase(predictedLSRs.get(CONSTRAINT_INDEX))) {
					System.out.println("The text for the role Constraint does not match");
					FN++;
					CT_FN++;
					Counter++;
					toshow.add(groundtruthLSRs.get(CONSTRAINT_INDEX));
					toshow.add(predictedLSRs.get(CONSTRAINT_INDEX));
					LSRsToShow.put("Constraint", toshow);
					toshow = new ArrayList<String>();
				}
				//---------------------------------------------------------------------------------------------------------------------------------------
				//Constraint
				//---------------------------------------------------------------------------------------------------------------------------------------
				
				//---------------------------------------------------------------------------------------------------------------------------------------
				//Temporal Characteristic
				//---------------------------------------------------------------------------------------------------------------------------------------
				String tempchar = groundtruthLSRs.get(TEMPORAL_CHARACTERISTIC_INDEX).substring(0, groundtruthLSRs.get(TEMPORAL_CHARACTERISTIC_INDEX).length() - 1);
				if(groundtruthLSRs.get(TEMPORAL_CHARACTERISTIC_INDEX).endsWith(" "))
					groundtruthLSRs.set(TEMPORAL_CHARACTERISTIC_INDEX, tempchar);
				tempchar = predictedLSRs.get(TEMPORAL_CHARACTERISTIC_INDEX).substring(0, predictedLSRs.get(TEMPORAL_CHARACTERISTIC_INDEX).length() - 1);
				if(predictedLSRs.get(TEMPORAL_CHARACTERISTIC_INDEX).endsWith(" "))
					predictedLSRs.set(TEMPORAL_CHARACTERISTIC_INDEX, tempchar);
				
				if(groundtruthLSRs.get(TEMPORAL_CHARACTERISTIC_INDEX).equalsIgnoreCase(predictedLSRs.get(TEMPORAL_CHARACTERISTIC_INDEX)) && groundtruthLSRs.get(TEMPORAL_CHARACTERISTIC_INDEX).equalsIgnoreCase("NA")) {
					System.out.println("There is no role to match");
					TN++;
					TCH_TN++;
				}
				else if((groundtruthLSRs.get(TEMPORAL_CHARACTERISTIC_INDEX).equalsIgnoreCase(predictedLSRs.get(TEMPORAL_CHARACTERISTIC_INDEX))) || (groundtruthLSRs.get(TEMPORAL_CHARACTERISTIC_INDEX).toLowerCase().contains(predictedLSRs.get(TEMPORAL_CHARACTERISTIC_INDEX).toLowerCase()) && !predictedLSRs.get(TEMPORAL_CHARACTERISTIC_INDEX).equalsIgnoreCase("NA")) || (predictedLSRs.get(TEMPORAL_CHARACTERISTIC_INDEX).toLowerCase().contains(groundtruthLSRs.get(TEMPORAL_CHARACTERISTIC_INDEX).toLowerCase()) && !groundtruthLSRs.get(TEMPORAL_CHARACTERISTIC_INDEX).equalsIgnoreCase("NA")) || (StringUtils.getJaroWinklerDistance(predictedLSRs.get(TEMPORAL_CHARACTERISTIC_INDEX).toLowerCase(), groundtruthLSRs.get(TEMPORAL_CHARACTERISTIC_INDEX).toLowerCase())>THRESHOLD_LSR)) {
					System.out.println("The text for the role Temporal Characteristic matches");
					TP++;
					TCH_TP++;
				}
				else if(!groundtruthLSRs.get(TEMPORAL_CHARACTERISTIC_INDEX).equalsIgnoreCase(predictedLSRs.get(TEMPORAL_CHARACTERISTIC_INDEX)) && groundtruthLSRs.get(TEMPORAL_CHARACTERISTIC_INDEX).equalsIgnoreCase("NA")) {
					System.out.println("The text for the role Temporal Characteristic does not match");
					FP++;
					TCH_FP++;
					Counter++;
					toshow.add(groundtruthLSRs.get(TEMPORAL_CHARACTERISTIC_INDEX));
					toshow.add(predictedLSRs.get(TEMPORAL_CHARACTERISTIC_INDEX));
					LSRsToShow.put("Temporal Characteristic", toshow);
					toshow = new ArrayList<String>();
				}
				else if(!groundtruthLSRs.get(TEMPORAL_CHARACTERISTIC_INDEX).equalsIgnoreCase(predictedLSRs.get(TEMPORAL_CHARACTERISTIC_INDEX))) {
					System.out.println("The text for the role Temporal Characteristic does not match");
					FN++;
					TCH_FN++;
					Counter++;
					toshow.add(groundtruthLSRs.get(TEMPORAL_CHARACTERISTIC_INDEX));
					toshow.add(predictedLSRs.get(TEMPORAL_CHARACTERISTIC_INDEX));
					LSRsToShow.put("Temporal Characteristic", toshow);
					toshow = new ArrayList<String>();
				}
				//---------------------------------------------------------------------------------------------------------------------------------------
				//Temporal Characteristic
				//---------------------------------------------------------------------------------------------------------------------------------------
				
				//---------------------------------------------------------------------------------------------------------------------------------------
				//Reason
				//---------------------------------------------------------------------------------------------------------------------------------------
				String reason = groundtruthLSRs.get(REASON_INDEX).substring(0, groundtruthLSRs.get(REASON_INDEX).length() - 1);
				if(groundtruthLSRs.get(REASON_INDEX).endsWith(" "))
					groundtruthLSRs.set(REASON_INDEX, reason);
				reason = predictedLSRs.get(REASON_INDEX).substring(0, predictedLSRs.get(REASON_INDEX).length() - 1);
				if(predictedLSRs.get(REASON_INDEX).endsWith(" "))
					predictedLSRs.set(REASON_INDEX, reason);
				
				if(groundtruthLSRs.get(REASON_INDEX).equalsIgnoreCase(predictedLSRs.get(REASON_INDEX)) && groundtruthLSRs.get(REASON_INDEX).equalsIgnoreCase("NA")) {
					System.out.println("There is no role to match");
					TN++;
					RN_TN++;
				}
				else if((groundtruthLSRs.get(REASON_INDEX).equalsIgnoreCase(predictedLSRs.get(REASON_INDEX))) || (groundtruthLSRs.get(REASON_INDEX).toLowerCase().contains(predictedLSRs.get(REASON_INDEX).toLowerCase()) && !predictedLSRs.get(REASON_INDEX).equalsIgnoreCase("NA")) || (predictedLSRs.get(REASON_INDEX).toLowerCase().contains(groundtruthLSRs.get(REASON_INDEX).toLowerCase()) && !groundtruthLSRs.get(REASON_INDEX).equalsIgnoreCase("NA")) || (StringUtils.getJaroWinklerDistance(predictedLSRs.get(REASON_INDEX).toLowerCase(), groundtruthLSRs.get(REASON_INDEX).toLowerCase())>THRESHOLD_LSR)) {
					System.out.println("The text for the role Reason matches");
					TP++;
					RN_TP++;
				}
				else if(!groundtruthLSRs.get(REASON_INDEX).equalsIgnoreCase(predictedLSRs.get(REASON_INDEX)) && groundtruthLSRs.get(REASON_INDEX).equalsIgnoreCase("NA")) {
					System.out.println("The text for the role Reason does not match");
					FP++;
					RN_FP++;
					Counter++;
					toshow.add(groundtruthLSRs.get(REASON_INDEX));
					toshow.add(predictedLSRs.get(REASON_INDEX));
					LSRsToShow.put("Reason", toshow);
					toshow = new ArrayList<String>();
				}
				else if(!groundtruthLSRs.get(REASON_INDEX).equalsIgnoreCase(predictedLSRs.get(REASON_INDEX))) {
					System.out.println("The text for the role Reason does not match");
					FN++;
					RN_FN++;
					Counter++;
					toshow.add(groundtruthLSRs.get(REASON_INDEX));
					toshow.add(predictedLSRs.get(REASON_INDEX));
					LSRsToShow.put("Reason", toshow);
					toshow = new ArrayList<String>();
				}
				//---------------------------------------------------------------------------------------------------------------------------------------
				//Reason
				//---------------------------------------------------------------------------------------------------------------------------------------
				
				//---------------------------------------------------------------------------------------------------------------------------------------
				//Event
				//---------------------------------------------------------------------------------------------------------------------------------------
				String event = groundtruthLSRs.get(EVENT_INDEX).substring(0, groundtruthLSRs.get(EVENT_INDEX).length() - 1);
				if(groundtruthLSRs.get(EVENT_INDEX).endsWith(" "))
					groundtruthLSRs.set(EVENT_INDEX, event);
				event = predictedLSRs.get(EVENT_INDEX).substring(0, predictedLSRs.get(EVENT_INDEX).length() - 1);
				if(predictedLSRs.get(EVENT_INDEX).endsWith(" "))
					predictedLSRs.set(EVENT_INDEX, event);
				
				if(groundtruthLSRs.get(EVENT_INDEX).equalsIgnoreCase(predictedLSRs.get(EVENT_INDEX)) && groundtruthLSRs.get(EVENT_INDEX).equalsIgnoreCase("NA")) {
					System.out.println("There is no role to match");
					TN++;
					ET_TN++;
				}
				else if((groundtruthLSRs.get(EVENT_INDEX).equalsIgnoreCase(predictedLSRs.get(EVENT_INDEX))) || (groundtruthLSRs.get(EVENT_INDEX).toLowerCase().contains(predictedLSRs.get(EVENT_INDEX).toLowerCase()) && !predictedLSRs.get(EVENT_INDEX).equalsIgnoreCase("NA")) || (predictedLSRs.get(EVENT_INDEX).toLowerCase().contains(groundtruthLSRs.get(EVENT_INDEX).toLowerCase()) && !groundtruthLSRs.get(EVENT_INDEX).equalsIgnoreCase("NA")) || (StringUtils.getJaroWinklerDistance(predictedLSRs.get(EVENT_INDEX).toLowerCase(), groundtruthLSRs.get(EVENT_INDEX).toLowerCase())>THRESHOLD_LSR)) {
					System.out.println("The text for the role Event matches");
					TP++;
					ET_TP++;
				}
				else if(!groundtruthLSRs.get(EVENT_INDEX).equalsIgnoreCase(predictedLSRs.get(EVENT_INDEX)) && groundtruthLSRs.get(EVENT_INDEX).equalsIgnoreCase("NA")) {
					System.out.println("The text for the role Event does not match");
					FP++;
					ET_FP++;
					Counter++;
					toshow.add(groundtruthLSRs.get(EVENT_INDEX));
					toshow.add(predictedLSRs.get(EVENT_INDEX));
					LSRsToShow.put("Event", toshow);
					toshow = new ArrayList<String>();
				}
				else if(!groundtruthLSRs.get(EVENT_INDEX).equalsIgnoreCase(predictedLSRs.get(EVENT_INDEX))) {
					System.out.println("The text for the role Event does not match");
					FN++;
					ET_FN++;
					Counter++;
					toshow.add(groundtruthLSRs.get(EVENT_INDEX));
					toshow.add(predictedLSRs.get(EVENT_INDEX));
					LSRsToShow.put("Event", toshow);
					toshow = new ArrayList<String>();
				}
				//---------------------------------------------------------------------------------------------------------------------------------------
				//Event
				//---------------------------------------------------------------------------------------------------------------------------------------
				
				//---------------------------------------------------------------------------------------------------------------------------------------
				//Reference
				//---------------------------------------------------------------------------------------------------------------------------------------
				String reference = groundtruthLSRs.get(REFERENCE_INDEX).substring(0, groundtruthLSRs.get(REFERENCE_INDEX).length() - 1);
				if(groundtruthLSRs.get(REFERENCE_INDEX).endsWith(" "))
					groundtruthLSRs.set(REFERENCE_INDEX, reference);
				reference = predictedLSRs.get(REFERENCE_INDEX).substring(0, predictedLSRs.get(REFERENCE_INDEX).length() - 1);
				if(predictedLSRs.get(REFERENCE_INDEX).endsWith(" "))
					predictedLSRs.set(REFERENCE_INDEX, reference);
				
				if(groundtruthLSRs.get(REFERENCE_INDEX).equalsIgnoreCase(predictedLSRs.get(REFERENCE_INDEX)) && groundtruthLSRs.get(REFERENCE_INDEX).equalsIgnoreCase("NA")) {
					System.out.println("There is no role to match");
					TN++;
					RE_TN++;
				}
				else if((groundtruthLSRs.get(REFERENCE_INDEX).equalsIgnoreCase(predictedLSRs.get(REFERENCE_INDEX))) || (groundtruthLSRs.get(REFERENCE_INDEX).toLowerCase().contains(predictedLSRs.get(REFERENCE_INDEX).toLowerCase()) && !predictedLSRs.get(REFERENCE_INDEX).equalsIgnoreCase("NA")) || (predictedLSRs.get(REFERENCE_INDEX).toLowerCase().contains(groundtruthLSRs.get(REFERENCE_INDEX).toLowerCase()) && !groundtruthLSRs.get(REFERENCE_INDEX).equalsIgnoreCase("NA")) || (StringUtils.getJaroWinklerDistance(predictedLSRs.get(REFERENCE_INDEX).toLowerCase(), groundtruthLSRs.get(REFERENCE_INDEX).toLowerCase())>THRESHOLD_LSR)) {
					System.out.println("The text for the role Reference matches");
					TP++;
					RE_TP++;
				}
				else if(!groundtruthLSRs.get(REFERENCE_INDEX).equalsIgnoreCase(predictedLSRs.get(REFERENCE_INDEX)) && groundtruthLSRs.get(REFERENCE_INDEX).equalsIgnoreCase("NA")) {
					System.out.println("The text for the role Reference does not match");
					FP++;
					RE_FP++;
					Counter++;
					toshow.add(groundtruthLSRs.get(REFERENCE_INDEX));
					toshow.add(predictedLSRs.get(REFERENCE_INDEX));
					LSRsToShow.put("Reference", toshow);
					toshow = new ArrayList<String>();
				}
				else if(!groundtruthLSRs.get(REFERENCE_INDEX).equalsIgnoreCase(predictedLSRs.get(REFERENCE_INDEX))) {
					System.out.println("The text for the role Reference does not match");
					FN++;
					RE_FN++;
					Counter++;
					toshow.add(groundtruthLSRs.get(REFERENCE_INDEX));
					toshow.add(predictedLSRs.get(REFERENCE_INDEX));
					LSRsToShow.put("Reference", toshow);
					toshow = new ArrayList<String>();
				}
				//---------------------------------------------------------------------------------------------------------------------------------------
				//Reference
				//---------------------------------------------------------------------------------------------------------------------------------------
				
				//---------------------------------------------------------------------------------------------------------------------------------------
				//Location
				//---------------------------------------------------------------------------------------------------------------------------------------
				//System.out.println("\n");
				if(Counter == 0) {
					System.out.println("\nThe requirement was satisfied!\n");
					RS++;
				}
				/*else if (Counter != 0 && !flag) {
					System.out.println("\nThe requirement was satisfied!\n");
					RS++;
					for (Entry<String, List<String>> ent : LSRsToShow.entrySet()) {
						System.out.println("For the LSR: " + ent.getKey());
						List<String> values = ent.getValue();
						System.out.println("The LSR in the GT is: " + values.get(0));
						System.out.println("The LSR identified was: " + values.get(1) + "\n");
					}
				}*/
				else if (Counter < 3) {
					System.out.println("\nThe requirement was PARTIALLY satisfied!\n");
					RPS++;
					for (Entry<String, List<String>> ent : LSRsToShow.entrySet()) {
						System.out.println("For the LSR: " + ent.getKey());
						List<String> values = ent.getValue();
						System.out.println("The LSR in the GT is: " + values.get(0));
						System.out.println("The LSR identified was: " + values.get(1) + "\n");
					}
				}
				else {
					System.out.println("\nThe requirement was NOT satisfied!\n");
					for (Entry<String, List<String>> ent : LSRsToShow.entrySet()) {
						System.out.println("For the LSR: " + ent.getKey());
						List<String> values = ent.getValue();
						System.out.println("The LSR in the GT is: " + values.get(0));
						System.out.println("The LSR identified was: " + values.get(1) + "\n");
					}
				}
				Counter = 0;
				flag = false;
			}
			
		}
		
		//Adjusting the evaluation to the identification of missing legal semantic roles
		//------------------------------------------------------------------------------
		int exch;
		exch = MAR_TP;
		MAR_TP = MAR_TN;
		MAR_TN = exch;
		exch = MAR_FP;
		MAR_FP = MAR_FN;
		MAR_FN = exch;
		
		exch = MAN_TP;
		MAN_TP = MAN_TN;
		MAN_TN = exch;
		exch = MAN_FP;
		MAN_FP = MAN_FN;
		MAN_FN = exch;
		
		exch = BT_TP;
		BT_TP = BT_TN;
		BT_TN = exch;
		exch = BT_FP;
		BT_FP = BT_FN;
		BT_FN = exch;
		
		exch = OBJ_TP;
		OBJ_TP = OBJ_TN;
		OBJ_TN = exch;
		exch = OBJ_FP;
		OBJ_FP = OBJ_FN;
		OBJ_FN = exch;
		
		exch = SAR_TP;
		SAR_TP = SAR_TN;
		SAR_TN = exch;
		exch = SAR_FP;
		SAR_FP = SAR_FN;
		SAR_FN = exch;
		
		exch = SAN_TP;
		SAN_TP = SAN_TN;
		SAN_TN = exch;
		exch = SAN_FP;
		SAN_FP = SAN_FN;
		SAN_FN = exch;
		
		exch = CN_TP;
		CN_TP = CN_TN;
		CN_TN = exch;
		exch = CN_FP;
		CN_FP = CN_FN;
		CN_FN = exch;
		
		exch = CT_TP;
		CT_TP = CT_TN;
		CT_TN = exch;
		exch = CT_FP;
		CT_FP = CT_FN;
		CT_FN = exch;
		
		exch = TCH_TP;
		TCH_TP = TCH_TN;
		TCH_TN = exch;
		exch = TCH_FP;
		TCH_FP = TCH_FN;
		TCH_FN = exch;
		
		exch = RN_TP;
		RN_TP = RN_TN;
		RN_TN = exch;
		exch = RN_FP;
		RN_FP = RN_FN;
		RN_FN = exch;
		
		exch = ET_TP;
		ET_TP = ET_TN;
		ET_TN = exch;
		exch = ET_FP;
		ET_FP = ET_FN;
		ET_FN = exch;
		
		exch = RE_TP;
		RE_TP = RE_TN;
		RE_TN = exch;
		exch = RE_FP;
		RE_FP = RE_FN;
		RE_FN = exch;
		
		exch = AE_TP;
		AE_TP = AE_TN;
		AE_TN = exch;
		exch = AE_FP;
		AE_FP = AE_FN;
		AE_FN = exch;
		
		exch = LN_TP;
		LN_TP = LN_TN;
		LN_TN = exch;
		exch = LN_FP;
		LN_FP = LN_FN;
		LN_FN = exch;
		
		exch = TP;
		TP = TN;
		TN = exch;
		exch = FP;
		FP = FN;
		FN = exch;
		//------------------------------------------------------------------------------
		
		System.out.printf("%n--Legal Semantic Roles Analysis--%n");
		/*System.out.println("Main Actor:");
		P = (float)(MAR_TP*100)/(MAR_TP+MAR_FP);
		R = (float)MAR_TP*100/(MAR_TP+MAR_FN);
		A = (float)(MAR_TP+MAR_TN)*100/(MAR_TP+MAR_TN+MAR_FP+MAR_FN);
		System.out.println("Total TPs: " + MAR_TP);
		System.out.println("Total TNs: " + MAR_TN);
		System.out.println("Total FPs: " + MAR_FP);
		System.out.println("Total FNs: " + MAR_FN);
		System.out.println("Precision: " + P);
		System.out.println("Recall: " + R);
		System.out.println("Accuracy: " + A + "\n");
		
		System.out.println("Main Action:");
		P = (float)(MAN_TP*100)/(MAN_TP+MAN_FP);
		R = (float)MAN_TP*100/(MAN_TP+MAN_FN);
		A = (float)(MAN_TP+MAN_TN)*100/(MAN_TP+MAN_TN+MAN_FP+MAN_FN);
		System.out.println("Total TPs: " + MAN_TP);
		System.out.println("Total TNs: " + MAN_TN);
		System.out.println("Total FPs: " + MAN_FP);
		System.out.println("Total FNs: " + MAN_FN);
		System.out.println("Precision: " + P);
		System.out.println("Recall: " + R);
		System.out.println("Accuracy: " + A + "\n");
		
		System.out.println("Beneficiary/Target:");
		P = (float)(BT_TP*100)/(BT_TP+BT_FP);
		R = (float)BT_TP*100/(BT_TP+BT_FN);
		A = (float)(BT_TP+BT_TN)*100/(BT_TP+BT_TN+BT_FP+BT_FN);
		System.out.println("Total TPs: " + BT_TP);
		System.out.println("Total TNs: " + BT_TN);
		System.out.println("Total FPs: " + BT_FP);
		System.out.println("Total FNs: " + BT_FN);
		System.out.println("Precision: " + P);
		System.out.println("Recall: " + R);
		System.out.println("Accuracy: " + A + "\n");
		
		System.out.println("Object:");
		P = (float)(OBJ_TP*100)/(OBJ_TP+OBJ_FP);
		R = (float)OBJ_TP*100/(OBJ_TP+OBJ_FN);
		A = (float)(OBJ_TP+OBJ_TN)*100/(OBJ_TP+OBJ_TN+OBJ_FP+OBJ_FN);
		System.out.println("Total TPs: " + OBJ_TP);
		System.out.println("Total TNs: " + OBJ_TN);
		System.out.println("Total FPs: " + OBJ_FP);
		System.out.println("Total FNs: " + OBJ_FN);
		System.out.println("Precision: " + P);
		System.out.println("Recall: " + R);
		System.out.println("Accuracy: " + A + "\n");
		
		System.out.println("Secondary Actor:");
		P = (float)(SAR_TP*100)/(SAR_TP+SAR_FP);
		R = (float)SAR_TP*100/(SAR_TP+SAR_FN);
		A = (float)(SAR_TP+SAR_TN)*100/(SAR_TP+SAR_TN+SAR_FP+SAR_FN);
		System.out.println("Total TPs: " + SAR_TP);
		System.out.println("Total TNs: " + SAR_TN);
		System.out.println("Total FPs: " + SAR_FP);
		System.out.println("Total FNs: " + SAR_FN);
		System.out.println("Precision: " + P);
		System.out.println("Recall: " + R);
		System.out.println("Accuracy: " + A + "\n");
		
		System.out.println("Secondary Action:");
		P = (float)(SAN_TP*100)/(SAN_TP+SAN_FP);
		R = (float)SAN_TP*100/(SAN_TP+SAN_FN);
		A = (float)(SAN_TP+SAN_TN)*100/(SAN_TP+SAN_TN+SAN_FP+SAN_FN);
		System.out.println("Total TPs: " + SAN_TP);
		System.out.println("Total TNs: " + SAN_TN);
		System.out.println("Total FPs: " + SAN_FP);
		System.out.println("Total FNs: " + SAN_FN);
		System.out.println("Precision: " + P);
		System.out.println("Recall: " + R);
		System.out.println("Accuracy: " + A + "\n");
		
		System.out.println("Condition:");
		P = (float)(CN_TP*100)/(CN_TP+CN_FP);
		R = (float)CN_TP*100/(CN_TP+CN_FN);
		A = (float)(CN_TP+CN_TN)*100/(CN_TP+CN_TN+CN_FP+CN_FN);
		System.out.println("Total TPs: " + CN_TP);
		System.out.println("Total TNs: " + CN_TN);
		System.out.println("Total FPs: " + CN_FP);
		System.out.println("Total FNs: " + CN_FN);
		System.out.println("Precision: " + P);
		System.out.println("Recall: " + R);
		System.out.println("Accuracy: " + A + "\n");
		
		System.out.println("Constraint:");
		P = (float)(CT_TP*100)/(CT_TP+CT_FP);
		R = (float)CT_TP*100/(CT_TP+CT_FN);
		A = (float)(CT_TP+CT_TN)*100/(CT_TP+CT_TN+CT_FP+CT_FN);
		System.out.println("Total TPs: " + CT_TP);
		System.out.println("Total TNs: " + CT_TN);
		System.out.println("Total FPs: " + CT_FP);
		System.out.println("Total FNs: " + CT_FN);
		System.out.println("Precision: " + P);
		System.out.println("Recall: " + R);
		System.out.println("Accuracy: " + A + "\n");
		
		System.out.println("Temporal Characteristic:");
		P = (float)(TCH_TP*100)/(TCH_TP+TCH_FP);
		R = (float)TCH_TP*100/(TCH_TP+TCH_FN);
		A = (float)(TCH_TP+TCH_TN)*100/(TCH_TP+TCH_TN+TCH_FP+TCH_FN);
		System.out.println("Total TPs: " + TCH_TP);
		System.out.println("Total TNs: " + TCH_TN);
		System.out.println("Total FPs: " + TCH_FP);
		System.out.println("Total FNs: " + TCH_FN);
		System.out.println("Precision: " + P);
		System.out.println("Recall: " + R);
		System.out.println("Accuracy: " + A + "\n");
		
		System.out.println("Reason:");
		P = (float)(RN_TP*100)/(RN_TP+RN_FP);
		R = (float)RN_TP*100/(RN_TP+RN_FN);
		A = (float)(RN_TP+RN_TN)*100/(RN_TP+RN_TN+RN_FP+RN_FN);
		System.out.println("Total TPs: " + RN_TP);
		System.out.println("Total TNs: " + RN_TN);
		System.out.println("Total FPs: " + RN_FP);
		System.out.println("Total FNs: " + RN_FN);
		System.out.println("Precision: " + P);
		System.out.println("Recall: " + R);
		System.out.println("Accuracy: " + A + "\n");
		
		System.out.println("Event:");
		P = (float)(ET_TP*100)/(ET_TP+ET_FP);
		R = (float)ET_TP*100/(ET_TP+ET_FN);
		A = (float)(ET_TP+ET_TN)*100/(ET_TP+ET_TN+ET_FP+ET_FN);
		System.out.println("Total TPs: " + ET_TP);
		System.out.println("Total TNs: " + ET_TN);
		System.out.println("Total FPs: " + ET_FP);
		System.out.println("Total FNs: " + ET_FN);
		System.out.println("Precision: " + P);
		System.out.println("Recall: " + R);
		System.out.println("Accuracy: " + A + "\n");
		
		System.out.println("Reference:");
		P = (float)(RE_TP*100)/(RE_TP+RE_FP);
		R = (float)RE_TP*100/(RE_TP+RE_FN);
		A = (float)(RE_TP+RE_TN)*100/(RE_TP+RE_TN+RE_FP+RE_FN);
		System.out.println("Total TPs: " + RE_TP);
		System.out.println("Total TNs: " + RE_TN);
		System.out.println("Total FPs: " + RE_FP);
		System.out.println("Total FNs: " + RE_FN);
		System.out.println("Precision: " + P);
		System.out.println("Recall: " + R);
		System.out.println("Accuracy: " + A + "\n");
		
		System.out.println("Attribute:");
		P = (float)(AE_TP*100)/(AE_TP+AE_FP);
		R = (float)AE_TP*100/(AE_TP+AE_FN);
		A = (float)(AE_TP+AE_TN)*100/(AE_TP+AE_TN+AE_FP+AE_FN);
		System.out.println("Total TPs: " + AE_TP);
		System.out.println("Total TNs: " + AE_TN);
		System.out.println("Total FPs: " + AE_FP);
		System.out.println("Total FNs: " + AE_FN);
		System.out.println("Precision: " + P);
		System.out.println("Recall: " + R);
		System.out.println("Accuracy: " + A + "\n");
		
		System.out.println("Location:");
		P = (float)(LN_TP*100)/(LN_TP+LN_FP);
		R = (float)LN_TP*100/(LN_TP+LN_FN);
		A = (float)(LN_TP+LN_TN)*100/(LN_TP+LN_TN+LN_FP+LN_FN);
		System.out.println("Total TPs: " + LN_TP);
		System.out.println("Total TNs: " + LN_TN);
		System.out.println("Total FPs: " + LN_FP);
		System.out.println("Total FNs: " + LN_FN);
		System.out.println("Precision: " + P);
		System.out.println("Recall: " + R);
		System.out.println("Accuracy: " + A + "\n");*/
		
		System.out.println("Total:");
		P = (float)(TP*100)/(TP+FP);
		R = (float)TP*100/(TP+FN);
		A = (float)(TP+TN)*100/(TP+TN+FP+FN);
		F1 = (float)(P*R)*2/(P+R);
		System.out.println("Total TPs: " + TP);
		System.out.println("Total TNs: " + TN);
		System.out.println("Total FPs: " + FP);
		System.out.println("Total FNs: " + FN);
		System.out.println("Precision: " + P);
		System.out.println("Recall: " + R);
		System.out.println("Accuracy: " + A);
		System.out.println("F1 Measure: " + F1);
		
		String header1 = "\n--Legal Semantic Roles Analysis--\n";
		String header2 = "TP\tTN\tFP\tFN\tPrec\tRec\tAcu\tF1\n";
		String doc = inputPath.replace(PATH_CSV, "");
		BufferedWriter writer = new BufferedWriter(new FileWriter(PATH_OUTPUT + doc.replace("csv", "txt")));
		//System.out.println("Document name: " + doc);
		//BufferedWriter writer = new BufferedWriter(new FileWriter(PATH_OUTPUT + DOC.replace("csv", "txt")));
		writer.write(header1);
		writer.write(header2);
		writer.write(TP + "\t" + TN + "\t" + FP + "\t" + FN + "\t" + P + "\t" + R + "\t" + A + "\t" + F1 + "\n");
		writer.close();
	}
	
	public static double findSimilarity(String x, String y) {
		  double maxLength = Double.max(x.length(), y.length());
		  //return (maxLength - StringUtils.getLevenshteinDistance(x, y)) / maxLength;
		  return (maxLength - StringUtils.getFuzzyDistance(x, y, Locale.ENGLISH)) / maxLength;
	  }
	
	
}
