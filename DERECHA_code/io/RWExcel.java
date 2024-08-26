package lu.svv.saa.linklaters.dpa.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;

public class RWExcel extends JCasAnnotator_ImplBase {
	
	public static final String PARAM_INPUT_PATH = "inputPath";
	  @ConfigurationParameter(name = PARAM_INPUT_PATH, mandatory = true,
	      description = "Input path for ground truth")
	 private String inputPath;
	  
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		// TODO Auto-generated method stub
		try {
			//WriteExcel(PATH_EXCEL + "test.xlsx");
			ReadExcel(inputPath);
		} catch (UIMAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static void WriteExcel(String filename) throws UIMAException, IOException {
		//Blank workbook
		XSSFWorkbook workbook = new XSSFWorkbook(); 
		
		//Create a blank sheet
		XSSFSheet sheet = workbook.createSheet("GDPR Requirement's Description");
		 
		//This data needs to be written (Object[])
		Map<String, Object[]> data = new TreeMap<String, Object[]>();
		data.put("1", new Object[] {"ID", "Requirement", "Description"});
		data.put("2", new Object[] {"R1", "Porcessor's Obligation 1", "PO1"});
		data.put("3", new Object[] {"R2", "Porcessor's Obligation 2", "PO2"});
		data.put("4", new Object[] {"R3", "Porcessor's Obligation 3", "PO3"});
		data.put("5", new Object[] {"R4", "Porcessor's Obligation 4", "PO4"});
		 
		//Iterate over data and write to sheet
		Set<String> keyset = data.keySet();
		int rownum = 0;
		for (String key : keyset) {
			Row row = sheet.createRow(rownum++);
			Object [] objArr = data.get(key);
			int cellnum = 0;
			for (Object obj : objArr) {
				Cell cell = row.createCell(cellnum++);
		    	if(obj instanceof String)
		    		cell.setCellValue((String)obj);
		    	else if(obj instanceof Integer)
		        	cell.setCellValue((Integer)obj);
			}
		}
		try {
			//Write the workbook in file system
			FileOutputStream out = new FileOutputStream(new File(filename));
			workbook.write(out);
			out.close();
			System.out.println("test.xlsx successfully written");
		} 
		catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	@SuppressWarnings("null")
	private static void ReadExcel(String filename) throws UIMAException, IOException {
		BufferedWriter writer = null;
		BufferedWriter writer2 = null;
		HashMap<String, List<String>> requirementID = new HashMap<String, List<String>>();
		HashMap<String, String> sentenceID = new HashMap<String, String>();
		HashMap<String, String> mainActor = new HashMap<String, String>();
		HashMap<String, String> mainAction = new HashMap<String, String>();
		HashMap<String, String> beneficiaryTarget = new HashMap<String, String>();
		HashMap<String, String> Obj = new HashMap<String, String>();
		HashMap<String, String> secondaryActor = new HashMap<String, String>();
		HashMap<String, String> secondaryAction = new HashMap<String, String>();
		HashMap<String, String> Cond = new HashMap<String, String>();
		HashMap<String, String> Const = new HashMap<String, String>();
		HashMap<String, String> temporalCharacteristic = new HashMap<String, String>();
		HashMap<String, String> Reas = new HashMap<String, String>();
		HashMap<String, String> Eve = new HashMap<String, String>();
		HashMap<String, String> Ref = new HashMap<String, String>();
		HashMap<String, String> Attr = new HashMap<String, String>();
		HashMap<String, String> Loc = new HashMap<String, String>();
		String header = "ID,Main actor,Main action,Beneficiary/Target,Object,Secondary actor,Secondary action,Condition,Constraint,Temporal Characteristic,Reason,Event,Reference,Attribute,Location\n";
		String header2 = "ID,Requirement1,Matching degree1,Requirement2,Matching degree2,Requirement3,Matching degree3\n";
		String key  = "", sID = "", mar = "", man = "", bt = "", ot = "", sar = "", san = "", cn = "", ct = "", tch = "", rn = "", et = "", re = "", ae = "", ln = "";
		List<String> rID = new ArrayList<String>();

		try
		{
			FileInputStream file = new FileInputStream(new File(filename));
			writer = new BufferedWriter(new FileWriter(filename.replace("xlsx", "csv") + ".csv"));
			writer2 = new BufferedWriter(new FileWriter(filename.replace("xlsx", "csv")));	//writer2 = new BufferedWriter(new FileWriter(filename.replace("xlsx", "csv") + ".csv"));
			//Create Workbook instance holding reference to .xlsx file
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			//Get first/desired sheet from the workbook
			XSSFSheet sheet = workbook.getSheetAt(0);			
			//Iterate through each rows one by one
			Iterator<Row> rowIterator = sheet.iterator();
			
			while (rowIterator.hasNext()) {
				String mActor = "", mAction = "", BT = "", Object = "", sActor = "", sAction = "", Condition = "", Constraint = "", TCh = "", Reason = "", Event = "", Reference = "", Attribute = "", Location = "";
				List<String> IDs = new ArrayList<String>();
				Row row = rowIterator.next();
				int rowIndex = row.getRowNum();
				//For each row, iterate through all the columns
				Iterator<Cell> cellIterator = row.cellIterator();
				String previoscellValue = "", sentenceText = "";
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					int columnIndex = cell.getColumnIndex();
					if (rowIndex != 0) {
						//-----------------------------------------------------------------
						// New
						//-----------------------------------------------------------------
						if (columnIndex == 2) {
							sentenceText = cell.getStringCellValue().replaceAll(",", ";");
							if(sentenceText.endsWith(" "))
								sentenceText = sentenceText.substring(0, sentenceText.length()-1);
		                }
						//-----------------------------------------------------------------
						// New
						//-----------------------------------------------------------------
						if (columnIndex == 3 || columnIndex == 5 || columnIndex == 7) {
							IDs.add(cell.getStringCellValue().substring(0, 3).replaceAll(" ", ""));
		                }
						else if (columnIndex == 4 || columnIndex == 6 || columnIndex == 8) {
							IDs.add(cell.getStringCellValue());
		                }
							
						else if (columnIndex > 10) {
							if(cell.getStringCellValue().equalsIgnoreCase("Main actor")) {
								mActor = mActor + previoscellValue + " ";
							}
							else if(cell.getStringCellValue().equalsIgnoreCase("Main action")) {
								mAction = mAction + previoscellValue + " ";
							}
							else if(cell.getStringCellValue().equalsIgnoreCase("Beneficiary/Target")) {
								BT = BT + previoscellValue + " ";
							}
							else if(cell.getStringCellValue().equalsIgnoreCase("Object")) {
								Object = Object + previoscellValue + " ";
							}
							else if(cell.getStringCellValue().equalsIgnoreCase("Secondary actor")) {
								sActor = sActor + previoscellValue + " ";
							}
							else if(cell.getStringCellValue().equalsIgnoreCase("Secondary action")) {
								sAction = sAction + previoscellValue + " ";
							}
							else if(cell.getStringCellValue().equalsIgnoreCase("Condition")) {
								Condition = Condition + previoscellValue + " ";
							}
							else if(cell.getStringCellValue().equalsIgnoreCase("Constraint")) {
								Constraint = Constraint + previoscellValue + " ";
							}
							else if(cell.getStringCellValue().equalsIgnoreCase("Temporal Characteristic")) {
								TCh = TCh + previoscellValue + " ";
							}
							else if(cell.getStringCellValue().equalsIgnoreCase("Reason")) {
								Reason = Reason + previoscellValue + " ";
							}
							else if(cell.getStringCellValue().equalsIgnoreCase("Event")) {
								Event = Event + previoscellValue + " ";
							}
							else if(cell.getStringCellValue().equalsIgnoreCase("Reference")) {
								Reference = Reference + previoscellValue + " ";
							}
							else if(cell.getStringCellValue().equalsIgnoreCase("Attribute")) {
								Attribute = Attribute + previoscellValue + " ";
							}
							else if(cell.getStringCellValue().equalsIgnoreCase("Location")) {
								Location = Location + previoscellValue  + " ";
							}
							previoscellValue = cell.getStringCellValue();
							previoscellValue = previoscellValue.replaceAll(",", ";");
						}
					}
				}
				if (rowIndex != 0) {
					//-----------------------------------------------------------------
					// New
					//-----------------------------------------------------------------
					//requirementID.put("S" + Integer.toString(rowIndex), IDs);
					//sentenceID.put(Integer.toString(rowIndex), "S" + Integer.toString(rowIndex));
					requirementID.put(sentenceText, IDs);
					sentenceID.put(Integer.toString(rowIndex), sentenceText);
					//-----------------------------------------------------------------
					// New
					//-----------------------------------------------------------------
					mainActor.put(Integer.toString(rowIndex), mActor);
					mainAction.put(Integer.toString(rowIndex), mAction);
					beneficiaryTarget.put(Integer.toString(rowIndex), BT);
					Obj.put(Integer.toString(rowIndex), Object);
					secondaryActor.put(Integer.toString(rowIndex), sActor);
					secondaryAction.put(Integer.toString(rowIndex), sAction);
					Cond.put(Integer.toString(rowIndex), Condition);
					Const.put(Integer.toString(rowIndex), Constraint);
					temporalCharacteristic.put(Integer.toString(rowIndex), TCh);
					Reas.put(Integer.toString(rowIndex), Reason);
					Eve.put(Integer.toString(rowIndex), Event);
					Ref.put(Integer.toString(rowIndex), Reference);
					Attr.put(Integer.toString(rowIndex), Attribute);
					Loc.put(Integer.toString(rowIndex), Location);
				}
			}
			file.close();
			writer.write(header);
			for (Entry<String, String> entry : sentenceID.entrySet()) {
				key = entry.getKey();
				sID = entry.getValue();
				mar = mainActor.get(key);
				man = mainAction.get(key);				
				bt = beneficiaryTarget.get(key);
				ot = Obj.get(key);
				sar = secondaryActor.get(key);
				san = secondaryAction.get(key);
				cn = Cond.get(key);
				ct = Const.get(key);
				tch = temporalCharacteristic.get(key);
				rn = Reas.get(key);
				et = Eve.get(key);
				re = Ref.get(key);
				ae = Attr.get(key);
				ln = Loc.get(key);
				if(mar.isBlank())
					mar = "NA";
				mar = mar.replaceAll(",", ";");
				if(man.isBlank())
					man = "NA";
				man = man.replaceAll(",", ";");
				if(bt.isBlank())
					bt = "NA";
				bt.replaceAll(",", ";");
				if(ot.isBlank())
					ot = "NA";
				ot = ot.replaceAll(",", ";");
				if(sar.isBlank())
					sar = "NA";
				sar = sar.replaceAll(",", ";");
				if(san.isBlank())
					san = "NA";
				san = san.replaceAll(",", ";");
				if(cn.isBlank())
					cn = "NA";
				cn = cn.replaceAll(",", ";");
				if(ct.isBlank())
					ct = "NA";
				ct = ct.replaceAll(",", ";");
				if(tch.isBlank())
					tch = "NA";
				tch = tch.replaceAll(",", ";");
				if(rn.isBlank())
					rn = "NA";
				rn = rn.replaceAll(",", ";");
				if(et.isBlank())
					et = "NA";
				et = et.replaceAll(",", ";");
				if(re.isBlank())
					re = "NA";
				re = re.replaceAll(",", ";");
				if(ae.isBlank())
					ae = "NA";
				ae = ae.replaceAll(",", ";");
				if(ln.isBlank())
					ln = "NA";
				ln = ln.replaceAll(",", ";");
				//This question is for removing the sentences not annotated at all
				if(mar != "NA" || man != "NA" || bt != "NA" || ot != "NA" || sar != "NA" || san != "NA" || cn != "NA" || ct != "NA" || tch != "NA" || rn != "NA" || et != "NA" || re != "NA" || ae != "NA" || ln != "NA")
					writer.write(sID + "," + mar + "," + man + "," + bt + "," + ot + "," + sar + "," + san + "," + cn + "," + ct + "," + tch + "," + rn + "," + et + "," + re + "," + ae + "," + ln + "\n");
			}
			writer.close();
			writer2.write(header2);
			for (Entry<String, List<String>> entry : requirementID.entrySet()) {
				key = entry.getKey();
				rID = entry.getValue();
				writer2.write(key + ",");
				for (int i=0;i<rID.size();i++) {
					if (i<5)
						writer2.write(rID.get(i) + ",");
					else
						writer2.write(rID.get(i));
				}
				if(rID.size()==4)
					writer2.write("NA,NA");
				else if(rID.size()==2)
					writer2.write("NA,NA,NA,NA");
				else if(rID.size()==0)
					writer2.write("NA,NA,NA,NA,NA,NA");
				writer2.write("\n");
			}
			writer2.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
}
