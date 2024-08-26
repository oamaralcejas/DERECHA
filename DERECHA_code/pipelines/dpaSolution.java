package lu.svv.saa.linklaters.dpa.pipelines;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;
import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.uimafit.component.xwriter.CASDumpWriter;

import de.tudarmstadt.ukp.dkpro.core.clearnlp.ClearNlpSemanticRoleLabeler;
import de.tudarmstadt.ukp.dkpro.core.matetools.MateLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.matetools.MateSemanticRoleLabeler;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpChunker;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpParser;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;
import de.tudarmstadt.ukp.dkpro.core.snowball.SnowballStemmer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordCoreferenceResolver;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordNamedEntityRecognizer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordParser;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import lu.svv.saa.linklaters.dpa.analysis.ComplianceChecker;
import lu.svv.saa.linklaters.dpa.analysis.LegalSemanticRoleLabeler;
import lu.svv.saa.linklaters.dpa.analysis.MySentenceIdentifier;
import lu.svv.saa.linklaters.dpa.analysis.LSRLEvaluator;
import lu.svv.saa.linklaters.dpa.io.AnnotationWriter;
import lu.svv.saa.linklaters.dpa.io.ComplianceWriter;
import lu.svv.saa.linklaters.dpa.io.POIReader;
import lu.svv.saa.linklaters.dpa.io.RWExcel;
import lu.svv.saa.linklaters.dpa.utils.PARAMETERS_VALUES;

public class dpaSolution implements PARAMETERS_VALUES{
  
  public static void main(String[] args) throws IOException, UIMAException {
	
	String filename, filetitle, doctitle;
	File folder = new File(PATH_INPUT); 
	File[] lista = folder.listFiles();
	int enrichment = 48;
	System.out.println("Choose the level of text enrichment you prefer for the analysis of the document(s):");
	for (int i = 0; i < lista.length; i++)
		System.out.println("* " + lista[i].getName());
	System.out.println("\n0 - For no text enrichment");
	System.out.println("1 - For limited text enrichment");
	System.out.println("2 - For moderate text enrichment");
	System.out.println("3 - For extensive text enrichment\n");
	try {
		enrichment = System.in.read() - enrichment;
	} catch (IOException e) {
		e.printStackTrace();
	}
	if(enrichment < 0 || enrichment > 3) {
		System.out.println("Wrong number entered: " + enrichment);
		System.out.println("No enrichment will be used!\n");
		enrichment = 0;
	}
	else if(enrichment == 0) {
		System.out.println("You entered: " + enrichment);
		System.out.println("No enrichment will be used!\n");
	}
	else if(enrichment == 1) {
		System.out.println("You entered: " + enrichment);
		System.out.println("Limited enrichment will be used!\n");
	}
	else if(enrichment == 2) {
		System.out.println("You entered: " + enrichment);
		System.out.println("Moderate text enrichment will be used!\n");
	}
	else if(enrichment == 3) {
		System.out.println("You entered: " + enrichment);
		System.out.println("Extensive text enrichment will be used!\n");
	}
	for (int i = 0; i < lista.length; i++) {
		runLSRsPipeline(lista[i].getName(), lista[i].getName().replace("docx", "xlsx"), lista[i].getName().replace("docx", "csv"), enrichment);
	}
    
  }
  
  private static void runLSRsPipeline(String filename, String filetitle, String doctitle, int enrichment) throws UIMAException, IOException {
	  
	// Read input
	CollectionReaderDescription WdocReader = createReaderDescription(POIReader.class, POIReader.PARAM_INPUT_PATH, PATH_INPUT + filename);
    
    // Tokenizer
    AnalysisEngineDescription openNLPSegmenter = createEngineDescription(OpenNlpSegmenter.class);
    
    // Sentence identifier
    AnalysisEngineDescription sentence_identifier = createEngineDescription(MySentenceIdentifier.class);
    
    // POS-Tagger
    AnalysisEngineDescription posTagger = createEngineDescription(OpenNlpPosTagger.class);

    // Lemmatizer
    AnalysisEngineDescription lemmatizer = createEngineDescription(MateLemmatizer.class);
    
    // Stemmer
    AnalysisEngineDescription stemmer = createEngineDescription(SnowballStemmer.class);
    
    // Constituency + Dependency Parser
    AnalysisEngineDescription parser = createEngineDescription(StanfordParser.class);
    
    // Chunker
    AnalysisEngineDescription chunker = createEngineDescription(OpenNlpChunker.class);
  
    // NER
    AnalysisEngineDescription ner = createEngineDescription(StanfordNamedEntityRecognizer.class);
    
    // Semantic Role Labeling
    AnalysisEngineDescription srl = createEngineDescription(ClearNlpSemanticRoleLabeler.class);
    
    // Identify Legal Semantic Role Labels 
    AnalysisEngineDescription legal_srl = createEngineDescription(LegalSemanticRoleLabeler.class);
    
    // Coreference resolver
    AnalysisEngineDescription coref = createEngineDescription(StanfordCoreferenceResolver.class);
        
    // Writers   
    AnalysisEngineDescription annWriter = createPrimitiveDescription(AnnotationWriter.class);
    AnalysisEngineDescription compWriter = createPrimitiveDescription(ComplianceWriter.class, ComplianceWriter.PARAM_INPUT_PATH, PATH_CSV + doctitle);
    
    // Ground Truth Reader
    AnalysisEngineDescription rw_xlsx = createPrimitiveDescription(RWExcel.class, RWExcel.PARAM_INPUT_PATH, PATH_EXCEL + filetitle);
    
    // Evaluate LSRL
    AnalysisEngineDescription evaluator = createPrimitiveDescription(LSRLEvaluator.class, LSRLEvaluator.PARAM_INPUT_PATH, PATH_CSV + doctitle);
    
    // Compliance Evaluation
    AnalysisEngineDescription complianceChecker = createPrimitiveDescription(ComplianceChecker.class, ComplianceChecker.ENRICHMENT, enrichment);
    
    // Dump Consumer
    AnalysisEngineDescription dumpConsumer = createPrimitiveDescription(CASDumpWriter.class);
    
    // Pipeline
    SimplePipeline.runPipeline(WdocReader, openNLPSegmenter, sentence_identifier, 
    		posTagger, lemmatizer, stemmer, chunker, ner, annWriter, 
    		legal_srl, rw_xlsx, evaluator, complianceChecker, compWriter
    		);
  }

}
