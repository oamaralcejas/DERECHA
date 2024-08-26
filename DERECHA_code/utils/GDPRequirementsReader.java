package lu.svv.saa.linklaters.dpa.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

@SuppressWarnings("resource")
public class GDPRequirementsReader implements PARAMETERS_VALUES {
	  
	public HashMap<String, List<HashMap<String, String>>> ReadingGoldStandard() throws Exception {
		HashMap<String, List<HashMap<String, String>>> goldStandard = new HashMap<String, List<HashMap<String, String>>>();
		//Reading the CSV file
		File csvFile = new File(PATH_CSV + "GDPRequirements.csv");
		if (csvFile.isFile()) {
			// create BufferedReader and read data from csv
			BufferedReader csvReader = new BufferedReader(new FileReader(PATH_CSV + "GDPRequirements.csv"));
			String row = csvReader.readLine();
			while ((row = csvReader.readLine()) != null) {						
				String[] fields = row.split(",");
				if(fields.length!=15)
					throw new Exception("Error parsing the following sentence: \"" + row + "\"");
				//Getting for every requirement, the text corresponding to each role
				String id = fields[SENTENCE_ID_INDEX];
				String mActor = fields[MAIN_ACTOR_INDEX + 1];
				mActor = mActor.replace(";", ",");
				String mAction = fields[MAIN_ACTION_INDEX + 1];
				mAction = mAction.replace(";", ",");
				String BT = fields[BENEFICIARY_TARGET_INDEX + 1];
				BT = BT.replace(";", ",");
				String Object = fields[OBJECT_INDEX + 1];
				Object = Object.replace(";", ",");
				String sActor = fields[SECONDARY_ACTOR_INDEX + 1];
				sActor = sActor.replace(";", ",");
				String sAction = fields[SECONDARY_ACTION_INDEX + 1];
				sAction = sAction.replace(";", ",");
				String Condition = fields[CONDITION_INDEX + 1];
				Condition = Condition.replace(";", ",");
				String Constraint = fields[CONSTRAINT_INDEX + 1];
				Constraint = Constraint.replace(";", ",");
				String TCh = fields[TEMPORAL_CHARACTERISTIC_INDEX + 1];
				TCh = TCh.replace(";", ",");
				String Reason = fields[REASON_INDEX + 1];
				Reason = Reason.replace(";", ",");
				String Event = fields[EVENT_INDEX + 1];
				Event = Event.replace(";", ",");
				String Reference = fields[REFERENCE_INDEX + 1];
				Reference = Reference.replace(";", ",");
				String Attribute = fields[ATTRIBUTE_INDEX + 1];
				Attribute = Attribute.replace(";", ",");
				String Location = fields[LOCATION_INDEX + 1];
				Location = Location.replace(";", ",");
				//Inside HashMap...Key = Role, Value = Text span
				HashMap<String, String> mar = new HashMap<String, String>();
				HashMap<String, String> man = new HashMap<String, String>();
				HashMap<String, String> bt = new HashMap<String, String>();
				HashMap<String, String> ot = new HashMap<String, String>();
				HashMap<String, String> sar = new HashMap<String, String>();
				HashMap<String, String> san = new HashMap<String, String>();
				HashMap<String, String> cn = new HashMap<String, String>();
				HashMap<String, String> ct = new HashMap<String, String>();
				HashMap<String, String> tch = new HashMap<String, String>();
				HashMap<String, String> rn = new HashMap<String, String>();
				HashMap<String, String> et = new HashMap<String, String>();
				HashMap<String, String> re = new HashMap<String, String>();
				HashMap<String, String> ae = new HashMap<String, String>();
				HashMap<String, String> ln = new HashMap<String, String>();
				List<HashMap<String, String>> hmList = new ArrayList<HashMap<String, String>>();
				mar.put(MAIN_ACTOR, mActor);
				hmList.add(mar);
				man.put(MAIN_ACTION, mAction);
				hmList.add(man);
				bt.put(BENEFICIARY_TARGET, BT);
				hmList.add(bt);
				ot.put(OBJECT, Object);
				hmList.add(ot);
				sar.put(SECONDARY_ACTOR, sActor);
				hmList.add(sar);
				san.put(SECONDARY_ACTION, sAction);
				hmList.add(san);
				cn.put(CONDITION, Condition);
				hmList.add(cn);
				ct.put(CONSTRAINT, Constraint);
				hmList.add(ct);
				tch.put(TEMPORAL_CHARACTERISTIC, TCh);
				hmList.add(tch);
				rn.put(REASON, Reason);
				hmList.add(rn);
				et.put(EVENT, Event);
				hmList.add(et);
				re.put(REFERENCE, Reference);
				hmList.add(re);
				ae.put(ATTRIBUTE, Attribute);
				hmList.add(ae);
				ln.put(LOCATION, Location);
				hmList.add(ln);
				//Outside HashMap...Key = Sentence ID, Value = Inside HashMap
				goldStandard.put(id, hmList);
				//System.out.printf("%n== ID: " + id + "==%n");
				//System.out.printf("%n== List: " + hmList + "==%n");
			}
			csvReader.close();
		}
		/*for (Entry<String, List<HashMap<String, String>>> eentry : goldStandard.entrySet()) {
			System.out.printf("Key: " + eentry.getKey());
			System.out.printf(" List of elements: " + eentry.getValue() + "\n");
		}*/
		return goldStandard;
	}
}
