package lu.svv.saa.linklaters.dpa.io;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.util.JCasUtil.select;
import static org.apache.uima.fit.util.JCasUtil.selectCovered;

import org.apache.uima.UIMAException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.semantics.type.SemPred;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.Chunk;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.constituent.ROOT;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import de.tudarmstadt.ukp.dkpro.core.clearnlp.ClearNlpSemanticRoleLabeler;
import de.tudarmstadt.ukp.dkpro.core.io.penntree.PennTreeUtils;
import de.tudarmstadt.ukp.dkpro.core.matetools.MateLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpChunker;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;
import de.tudarmstadt.ukp.dkpro.core.snowball.SnowballStemmer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordNamedEntityRecognizer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordParser;
import lu.svv.saa.linklaters.dpa.type.ComplianceResults;
import lu.svv.saa.linklaters.dpa.type.MySentence;


public class AnnotationWriter extends JCasConsumer_ImplBase {
private AnalysisEngine SRLPipeline;

	@Override
	public void initialize(final UimaContext context) throws ResourceInitializationException { 
		super.initialize(context);
		SRLPipeline = createEngine(createEngineDescription(createEngineDescription(OpenNlpSegmenter.class),
				createEngineDescription(MateLemmatizer.class), createEngineDescription(SnowballStemmer.class),
				createEngineDescription(OpenNlpPosTagger.class),
				createEngineDescription(StanfordNamedEntityRecognizer.class),
				createEngineDescription(OpenNlpChunker.class),
				createEngineDescription(StanfordParser.class, StanfordParser.PARAM_MAX_SENTENCE_LENGTH, 100)
		// , createEngineDescription(ClearNlpSemanticRoleLabeler.class)
		));
	}

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
	  
	  JCas pcas = null;
    for (MySentence sentence : select(aJCas, MySentence.class)) {
      pcas = textParsing(sentence.getCoveredText());
      System.out.printf("%n== Sentence_" + sentence.getID() + "==%n");
      System.out.println(sentence.getCoveredText());
      //System.out.println("The problem is here: " + sentence.getID());
      System.out.printf("  %-16s %-10s %-10s %-10s %-10s %n", "TOKEN", "LEMMA", "STEM", "CPOS", "POS");
      /*for (Token token : selectCovered(Token.class, sentence)) {
        System.out.printf("  %-16s %-10s %-10s %-10s %-10s %n", token.getCoveredText(),
            token.getLemma() != null ? token.getLemma().getValue() : "",
            token.getStem() != null ? token.getStem().getValue() : "",
            token.getPos().getClass().getSimpleName(), token.getPos().getPosValue());
      }*/
      
      for (Token token : select(pcas, Token.class)) {
          System.out.printf("  %-16s %-10s %-10s %-10s %-10s %n", token.getCoveredText(),
              token.getLemma() != null ? token.getLemma().getValue() : "",
              token.getStem() != null ? token.getStem().getValue() : "",
              token.getPos().getClass().getSimpleName(), token.getPos().getPosValue());
        }

      System.out.printf("%n  -- Named Entities --%n");
      System.out.printf("  %-16s %-10s%n", "ENTITY-TYPE", "ENTITY-VALUE");
      for (NamedEntity ne : selectCovered(NamedEntity.class, sentence)) {
        System.out.printf("  %-16s %-10s%n", ne.getValue(), ne.getCoveredText());
      }

      System.out.printf("%n  -- Constituents --%n");
      /*for (ROOT root : selectCovered(ROOT.class, sentence)) {
        System.out.printf("  %s%n", PennTreeUtils.toPennTree(PennTreeUtils.convertPennTree(root)));
      }*/
      for (ROOT root : select(pcas, ROOT.class)) {
          System.out.printf("  %s%n", PennTreeUtils.toPennTree(PennTreeUtils.convertPennTree(root)));
        }

      System.out.printf("%n  -- Chunks --%n");
      System.out.printf("  %-10s %-10s %n", "Chunk", "Value");
      for (Chunk chunk : selectCovered(Chunk.class, sentence)) {
        System.out.printf("  %-10s %-10s %n", chunk.getChunkValue(), chunk.getCoveredText());
      }

      System.out.printf("%n  -- Dependency relations --%n");
      System.out.printf("  %-16s %-10s %-10s %n", "DEPREL", "DEP", "GOV");
      /*for (Dependency dep : selectCovered(Dependency.class, sentence)) {
        System.out.printf("  %-16s %-10s %-10s %n", dep.getDependencyType(),
            dep.getDependent().getCoveredText(), dep.getGovernor().getCoveredText());
      }*/
      for (Dependency dep : select(pcas, Dependency.class)) {
          System.out.printf("  %-16s %-10s %-10s %n", dep.getDependencyType(),
              dep.getDependent().getCoveredText(), dep.getGovernor().getCoveredText());
        }
      
      //System.out.printf("%n  -- Semantic structure --%n");
      /*for (SemPred pred : selectCovered(SemPred.class, sentence)) {
          System.out.printf("  %-10s  ", pred.getCategory());
         for (int i=0;i<pred.getArguments().size();i++){
            System.out.printf(" %2s: %-8s", pred.getArguments(i).getRole()
                , pred.getArguments(i).getTarget().getCoveredText());
          }
          System.out.printf("%n");
      }*/
      /*for (SemPred pred : select(pcas, SemPred.class)) {
          System.out.printf("  %-10s  ", pred.getCategory());
         for (int i=0;i<pred.getArguments().size();i++){
            System.out.printf(" %2s: %-8s", pred.getArguments(i).getRole()
                , pred.getArguments(i).getTarget().getCoveredText());
          }
          System.out.printf("%n");
      }*/
    }

    /*System.out.printf("%n== Coreference chains (for the whole document) ==%n");
    for (CoreferenceChain chain : select(cas, CoreferenceChain.class)) {
      CoreferenceLink link = chain.getFirst();
      while (link.getNext() != null) {
        System.out.printf("-> %s (%s) ", link.getCoveredText(), link.getReferenceType());
        link = link.getNext();
      }
      System.out.printf("%n");
    }*/
    
  }

  @Override
  public void collectionProcessComplete() {
    // nothing
  }
  
	// Applying the parsing pipeline
  public JCas textParsing(String text) {
    JCas tcas = null;
    try {
      tcas = JCasFactory.createJCas();
      tcas.setDocumentLanguage("en");
      tcas.setDocumentText(text);
      SRLPipeline.process(tcas);
    } catch (UIMAException e) {
      e.printStackTrace();
    }
    return tcas;
  }

}
