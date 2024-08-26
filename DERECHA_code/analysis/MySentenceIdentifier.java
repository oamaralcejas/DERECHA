package lu.svv.saa.linklaters.dpa.analysis;

import static org.apache.uima.fit.util.JCasUtil.select;
import static org.apache.uima.fit.util.JCasUtil.selectCovered;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.uimafit.component.JCasAnnotator_ImplBase;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import lu.svv.saa.linklaters.dpa.type.MyParagraph;
import lu.svv.saa.linklaters.dpa.type.MySentence;

public class MySentenceIdentifier extends JCasAnnotator_ImplBase {
	int sent_index = 1;

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {

		for (MyParagraph para : select(aJCas, MyParagraph.class)) {
		    // if the paragraph has no sentence
			if (selectCovered(Sentence.class, para).size() == 0) { 
				// Creating my sentence from para
				newMySentence(aJCas, para.getText(), para.getBegin(), para.getEnd());
			} else {
				boolean first = true;
				for (Sentence sentence : selectCovered(Sentence.class, para)) {
					// Get first sentence in the paragraph if wasn't properly captured. 
					if (sentence.getBegin() > para.getBegin() && first) {
						String firstSent = para.getCoveredText().substring(0, sentence.getBegin() - para.getBegin());
						newMySentence(aJCas, firstSent, para.getBegin(), sentence.getBegin() - 1);
						first = false;
					}
					// Creating my sentence from the sentence splitting in DKPro 
					else
						newMySentence(aJCas, sentence.getCoveredText(), sentence.getBegin(), sentence.getEnd());
				}
			}
		}
	}

	private void newMySentence(JCas jcas, String sentence, int begin, int end) {
		MySentence sent = new MySentence(jcas);
		sent.setText(sentence);
		sent.setID(Integer.toString(sent_index));
		sent.setBegin(begin);
		sent.setEnd(end);
		sent.addToIndexes();
		sent_index++;
	}

}
