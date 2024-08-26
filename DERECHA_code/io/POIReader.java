package lu.svv.saa.linklaters.dpa.io;



import javax.xml.namespace.QName;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.uimafit.component.JCasCollectionReader_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import lu.svv.saa.linklaters.dpa.type.MyParagraph;
import lu.svv.saa.linklaters.dpa.utils.PARAMETERS_VALUES;

public class POIReader extends JCasCollectionReader_ImplBase implements PARAMETERS_VALUES {
	// the path of the input document
	public static final String PARAM_INPUT_PATH = "inputPath";
	@ConfigurationParameter(name = PARAM_INPUT_PATH, mandatory = true, description = "Input path for the docx file")
	private String inputPath;

	public static final String PARAM_FIRST_LETTER = "firstLetter";
	@ConfigurationParameter(name = PARAM_FIRST_LETTER, description = "The first letter of the document title", defaultValue = "")
	private String firstLetter;

	private Logger logger = Logger.getLogger(getClass());
	private long tic;
	private File document;
	private XWPFDocument docx;
	private StringBuilder documentText;
	private Iterator<IBodyElement> iterator = null;
    private static final String TEXTBOX_NAMESPACE="declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main' declare namespace wps='http://schemas.microsoft.com/office/word/2010/wordprocessingShape' declare namespace v='urn:schemas-microsoft-com:vml'.//*/wps:txbx/w:txbxContent | .//*/v:textbox/w:txbxContent";
    private static final String WORD_SCHEMA="http://schemas.openxmlformats.org/wordprocessingml/2006/main";
	private static String DOT = ".";
	private static String COLON = ":";
	private static String NEW_LINE = "\n";
	private static String TITLE_STYLE = "Title";

	private Pattern patternPuntc =Pattern.compile("\\p{Punct}$");
	private Pattern patternAnyChar = Pattern.compile(".$");
	private Pattern patternEndsWithDot = Pattern.compile("\\.$");
	private Pattern patternDecimalNumberingList = Pattern.compile("^(\\d.?)+\\d?\\)?");// for example 1.2 1. 1.2.3. 1.2) 1) 1
	private Pattern patternUpperLowerLetterList = Pattern
			.compile("^(\\()?[A-Za-z]{1}(\\s)*(\\.|\\)){1}(((\\.)?)([A-Za-z])(\\s*\\))?)*"); // upper/lower letter a) A) a.A) A.a) a. A.
	private Pattern patternLowerRomanLetter = Pattern
			.compile("^(\\()?([ivxcl])+(\\s)*(\\.|\\)){1}(((\\.)?)([ivxcl])+(\\s*\\))?)*"); // lower roman letter ii) ii.ii) ii.
	private Pattern patternUpperRomanLetter = Pattern
			.compile("^(\\()?([IVXCL])+(\\s)*(\\.|\\)){1}(((\\.)?)([IVXCL])+(\\s*\\))?)*"); // upper roman letter II) IV.II) IV.
	private Pattern patternBulletPoints = Pattern.compile("^[\\u2022,\\u2023,\\u25E6,\\u2043,\\u2219]"); // bullet points Bullet (•) Triangular Bullet (‣) White Bullet (◦) Hyphen Bullet (⁃) Bullet Operator (∙)
	private Pattern[] patterns = new Pattern[] { patternDecimalNumberingList, patternUpperLowerLetterList,
			patternLowerRomanLetter, patternUpperRomanLetter, patternBulletPoints };

	/**/
	// @Override
	public void initialize(final UimaContext context) throws ResourceInitializationException {
		super.initialize(context);

		BasicConfigurator.configure();
		this.tic = System.currentTimeMillis();
		this.document = new File(inputPath);
		if (!this.document.isFile())
			throw new ResourceInitializationException(
					new IOException("Dataset path " + this.document + " is expected to be a file!"));
		String lowercaseName = this.document.getName().toLowerCase();
		if (!lowercaseName.startsWith(firstLetter)
				|| !(lowercaseName.endsWith(".docx") || lowercaseName.endsWith(".doc")))
			throw new ResourceInitializationException(
					new IOException("Dataset path " + this.document + " is expected to be a MS-file!"));
		try {
			this.docx = new XWPFDocument(new FileInputStream(document));
			this.iterator = docx.getBodyElementsIterator();
			this.documentText = new StringBuilder();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void getNext(JCas nextCas) throws IOException, CollectionException {
		// get the next document
		// document = new File(inputPath + inputDoc);
		// System.out.println("The document to find in the folder: " +
		// document.getName());
		// System.out.println("The document I want: " + inputDoc + "\n");
		// if(document.getName().equalsIgnoreCase(inputDoc)) { //Needed for only
		// checking the right document
		// docx = new XWPFDocument(new FileInputStream(document));
		// StringBuilder documentText = new StringBuilder();
		// iterator = docx.getBodyElementsIterator();

		this.logger.info("Reading \"" + document.getName() + "\"");
		Set<String> elementText = new LinkedHashSet<String>();
		String header = "";
		boolean headerInTheSet=false;
		while (iterator.hasNext()) {
			IBodyElement element = iterator.next();
			if (element instanceof XWPFParagraph) {
				XWPFParagraph paragraph = (XWPFParagraph) element;
				String paragraphStyle = paragraph.getStyle();
				if(paragraphStyle != null &&  paragraphStyle.equals(TITLE_STYLE)) {
					continue;
				}
				logger.info("paragraph element style: "+paragraph.getStyle());
				logger.info("enumeration format: "+paragraph.getNumFmt());
				logger.info("enumeration depth: "+ paragraph.getNumIlvl());
				logger.info("enumeration level text: "+ paragraph.getNumLevelText());
				logger.info("header previous iteration: "+ header);
				XmlObject[] textBoxObjects = paragraph.getCTP().selectPath(TEXTBOX_NAMESPACE);
				if(textBoxObjects.length > 0) {
					logger.info("num of texboxes "+ textBoxObjects.length);
					elementText.addAll(addTextFromTextBoxes(textBoxObjects, element));
				}
				String text = paragraph.getParagraphText().replaceAll("\\s+", " ").trim();
				if (!text.isEmpty()) {
					//handle bullets
					Matcher matcherAnyChar = patternAnyChar.matcher(text);
					boolean isAnyChar=matcherAnyChar.find();
					boolean isAnEnuratedList = patternEnumeratedListMatcher(text);
					Matcher matcherEndsWithDot = patternEndsWithDot.matcher(text);
					boolean endsWithDot = matcherEndsWithDot.find();
					logger.info("paragraph text: "+text);
					logger.info("isAnyChar: "+ isAnyChar);
					logger.info("is an enumerated list: "+ isAnEnuratedList);
					logger.info("text ends with dot: "+ text.endsWith(DOT));
					logger.info("text ends with colon: "+ text.endsWith(COLON));
					logger.info("text ends with new line: "+ text.endsWith(NEW_LINE));
					
					//logger.info("par ctp: "+paragraph.getCTP());
						
					
					
					if((text.endsWith(DOT) || endsWithDot) && !isAnEnuratedList && paragraph.getNumFmt() == null) {
						logger.info("adding sentence: "+ text);
						elementText.add(text);
						header="";
					}
					else if((paragraph.getNumFmt() != null || isAnEnuratedList)) {
						String sentence = getTextFromEnumeratedItem(text, header);
						headerInTheSet =true;
						logger.info("adding sentence: "+ sentence);
						elementText.add(sentence);
						
					}
					else if((text.endsWith(COLON) || text.endsWith(NEW_LINE) ) && !isAnEnuratedList &&!text.endsWith(DOT)&& !endsWithDot) {
						if((!header.isBlank() || !header.isEmpty()) && headerInTheSet==false) {
							//logger.info("adding sentence: "+ header);
							String sentence = addDot(header);
						    logger.info("adding previous header to the set "+ sentence);
							elementText.add(sentence);
							header = text;
						}else {
							headerInTheSet=false;
							header = text;
							logger.info("new header: "+ header);
						}
					}else {
						text= addDot(text);
						logger.info("adding sentence: "+ text);
						elementText.add(text);
					}
				}
			} // End of paragraph
			else if (element instanceof XWPFTable) {
				XWPFTable table = (XWPFTable) element;
				List<XWPFTableRow> rows = table.getRows();
				for (XWPFTableRow row : rows) {
					// String txtrow = "";
					for (XWPFTableCell cell : row.getTableCells()) {
						String textCell = cell.getText().replaceAll("\\s+", " ");//TBD: a cell can contain a paragraph and enumerated list
						elementText.add(textCell);
					}
				}
			}
			logger.info("------------------------------------");
		}
		writeDebugFile(elementText);
		int offset = 0;

		for (String txt : elementText) {
			if (txt.isEmpty())
				continue;
			// Visualizing the sentences
			// logger.info("text: " + txt);

			MyParagraph para = new MyParagraph(nextCas);
			para.setBegin(offset);
			para.setEnd(offset + txt.length());
			para.setText(txt);

			para.addToIndexes(nextCas);

			offset += txt.length() + 1;

			documentText.append(txt + " ");
		}
		setDocumentMetadata(nextCas, document.getName(), documentText.toString());
	}
	
	private String addDot(String text) {
		Matcher punctMatcher=patternPuntc.matcher(text);
		if(punctMatcher.find()) {
			text=punctMatcher.replaceAll(DOT);
		}else {
			text= text+DOT;
		}
		
			
		return text;
	}

	private void writeDebugFile(Set<String> elementText) throws IOException {
		String fileName = FilenameUtils.removeExtension(document.getName());
		FileWriter debugFileWriter = new FileWriter("src/main/resources/output/"+fileName+"_debugPOIReader.txt");
		int i = 1;
		for(String text : elementText) {
			String sentence = i+ ") "+ text  +"\n";
			logger.info("sentence written "+sentence);
			debugFileWriter.write(sentence);
			i++;
		}
		
		debugFileWriter.close();
		
	}

	private String getTextFromEnumeratedItem(String text, String header) {
		logger.info("the paragraph has an enumeration");
		text= removeNumbering(text);
		if(!text.endsWith(DOT))
			text= text+DOT;
		logger.info("sentence: "+ header+" "+text);
		String sentence = header+" "+text;
		sentence = sentence.replaceAll("\\s+", " ");
		return sentence;
	}
	
	
	private String removeNumbering(String text) {
		logger.info("text to be changed "+text);
		for(Pattern pattern : patterns){
				Matcher matcher = pattern.matcher(text);
				if(matcher.find()) {
					String returnText= matcher.replaceAll("");
					returnText = returnText.replaceAll("\\s+", " ");
					return returnText;
				}
					
		}
		return text;
	}
	
	public boolean patternEnumeratedListMatcher(String text) {
		for(Pattern pattern : patterns){
			Matcher matcher = pattern.matcher(text);
			if(matcher.find())
				return true;
		}
		return false;
	}

	private Set<String> addTextFromTextBoxes(XmlObject[] textBoxObjects,IBodyElement element) {
		Set<String> elementText = new LinkedHashSet<String>();
		for (int i = 0; i < textBoxObjects.length; i++) {
			XWPFParagraph paragraph = null;
			try {
				XmlObject[] paraObjects = textBoxObjects[i].selectChildren(new QName(WORD_SCHEMA, "p"));
				String header = "";
				boolean headerInTheSet=false;
				for (int j = 0; j < paraObjects.length; j++) {
					paragraph = new XWPFParagraph(CTP.Factory.parse(paraObjects[j].xmlText()),((XWPFParagraph) element).getBody());
					String paragraphStyle= paragraph.getStyle();
					if(paragraphStyle != null &&  paragraphStyle.equals(TITLE_STYLE)) {
						continue;
					}
					String text=paragraph.getParagraphText().replaceAll("\\s+", " ").trim();
					if(!text.isEmpty()) {
						Matcher matcherAnyChar = patternAnyChar.matcher(text);
						boolean isAnyChar=matcherAnyChar.find();
						boolean isAnEnuratedList = patternEnumeratedListMatcher(text);
						Matcher matcherEndsWithDot = patternEndsWithDot.matcher(text);
						boolean endsWithDot = matcherEndsWithDot.find();
						
						
						
						if((text.endsWith(DOT) || endsWithDot) && !isAnEnuratedList && paragraph.getNumFmt() == null) {
							logger.info("adding sentence from textbox: "+ text);
							elementText.add(text);
							header="";
						}else if((paragraph.getNumFmt() != null || isAnEnuratedList)) {
							String sentence = getTextFromEnumeratedItem(text, header);
							headerInTheSet =true;
							logger.info("adding sentence: "+ sentence);
							elementText.add(sentence);
							
						}else if((text.endsWith(COLON) || text.endsWith(NEW_LINE) ) && !isAnEnuratedList &&!text.endsWith(DOT)&& !endsWithDot) {
							if((!header.isBlank() || !header.isEmpty()) && headerInTheSet==false) {
								//logger.info("adding sentence: "+ header);
								String sentence = addDot(header);
							    logger.info("adding previous header to the set "+ sentence);
								elementText.add(sentence);
								header = text;
							}else {
								headerInTheSet=false;
								header = text;
								logger.info("new header: "+ header);
							}
						}else {
							text= addDot(text);
							logger.info("adding sentence: "+ text);
							elementText.add(text);
						}
						
						
//						if((paragraph.getNumFmt() != null || isAnEnuratedList)) {
//							elementText.add(getTextFromEnumeratedItem(text, header));
//						}
//						else if((text.endsWith(COLON) || text.endsWith(NEW_LINE)) && !isAnEnuratedList) {
//							header = text;
//						}else if(text.endsWith(DOT)) {
//							logger.info("sentence: "+ text);
//							elementText.add(text);
//							header="";
//						}
					}
				}
			} catch (XmlException e) {
				e.printStackTrace();
			}
		}
		return elementText;
	}

	@Override
	public void close() {
		long toc = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - tic);
		// DecimalFormat df = new DecimalFormat("#.##"); //"1.2"
		logger.info(String.format("Total time for parsing the document: %d sec", toc));
		// logger.info(String.format("%s: %f sec", "Average time for parsing a
		// document", (double)toc/this.getNumDocuments()));
		try {
			String doc = inputPath.replace(PATH_INPUT, "");
			BufferedWriter writer = new BufferedWriter(new FileWriter(PATH_OUTPUT + doc.replace("docx", "txt"), true));
			// BufferedWriter writer = new BufferedWriter(new FileWriter(PATH_OUTPUT +
			// DOC.replace("csv", "txt"), true));
			writer.write("\n\n-- Execution time (seg) --\n" + toc);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Sets the metadata of the current document.
	 *
	 * @param jCas
	 */
	private void setDocumentMetadata(JCas jCas, String name, String text) {
		DocumentMetaData d = DocumentMetaData.create(jCas);
		String language = "en";
		String id = name;
		d.setDocumentId(id);
		d.setLanguage(language);
		jCas.setDocumentLanguage(language);
		jCas.setDocumentText(text);
	}

	

	public boolean hasNext() throws IOException, CollectionException {
		// TODO Auto-generated method stub
		return iterator.hasNext();
	}

	public Progress[] getProgress() {
		// TODO Auto-generated method stub
		return null;
	}
}
