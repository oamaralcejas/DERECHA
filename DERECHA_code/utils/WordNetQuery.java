package lu.svv.saa.linklaters.dpa.utils;

import static org.apache.uima.fit.util.JCasUtil.select;
import static org.apache.uima.fit.util.JCasUtil.selectCovered;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.uimafit.component.JCasAnnotator_ImplBase;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.IndexWordSet;
import net.sf.extjwnl.data.PointerUtils;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.Word;
import net.sf.extjwnl.data.list.PointerTargetNodeList;
import net.sf.extjwnl.dictionary.Dictionary;

public class WordNetQuery {	//extends JCasAnnotator_ImplBase

	/*@Override
	public void process(JCas cas) throws AnalysisEngineProcessException {
		for (Sentence sent : select(cas, Sentence.class)) {

			for (Token tok : selectCovered(Token.class, sent)) {
				String token = tok.getCoveredText(); // use if the POS is unknown
				//String lemma = tok.getLemmaValue(); // use when the POS is known

				try {
					getSynonyms(token, true);
					getRelatedWords(token, true);
				} catch (JWNLException e) {
					e.printStackTrace();
				}

			}
		}
	}*/

	/****
	 * @throws JWNLException
	 *****/

	// get synonyms
	public Set<String> getSynonyms(String[] tokens, boolean addGloss) throws JWNLException {
		Set<String> synonyms = new LinkedHashSet<String>();
		Dictionary d = Dictionary.getDefaultResourceInstance();
		for (int i=0;i<tokens.length;i++) {
			d.lookupAllIndexWords(tokens[i]);
			// If you know the part of speech of the token, it saves you time
			// IndexWord wnWord = d.lookupIndexWord(POS.NOUN, token);
			// then use wnWord.getSenses();

			// If you don't know the POS tag, then fetch all available words anywhere in the dictionary
			IndexWordSet wnWordSet = d.lookupAllIndexWords(tokens[i]);
			if (wnWordSet != null) {
				if (wnWordSet.size() > 0) {
					//Getting only the first synset
					for (IndexWord w : wnWordSet.getIndexWordCollection()) {
						if (!w.getSenses().get(0).getWords().isEmpty()) {
							for (Word synWord : w.getSenses().get(0).getWords()) {
								synonyms.add(synWord.getLemma());
							}
						}
						//Getting all synsets
						/*if (w.getSenses() != null) {
							System.out.println("the word: " + w.getLemma() + " has " + w.getSenses().size() + " senses as " + w.getPOS());
							for (Synset syn : w.getSenses()) {
								// Adding the synonyms in each synset
								for (Word synWord : syn.getWords()) {
									synonyms.add(synWord.getLemma());
								}

								// Including the gloss might be useful for the matching but can be time-consuming
								if (addGloss) {
									// remove special characters and split by space then add all to the synonyms
									String gloss[] = syn.getGloss().replaceAll("[^a-zA-Z0-9]", " ").replaceAll("\\s+", " ").split(" ");
									
									for (String g : gloss)
										synonyms.add(g);
								}
							}
						} */
						//else
							//System.out.println("the word: " + w.getLemma() + " has no senses in WordNet.");
					} 
				}
				//else
					//System.out.println("the word: " + wnWordSet.getLemma() + " is not found in WordNet.");
			}
		}
		//System.out.println("WordNet synonyms: " + synonyms);	
		return synonyms;
	}

	// get hypernyms and if needed also hyponyms 
	public Set<String> getRelatedWords(String[] tokens, boolean addHyponyms) throws JWNLException { 
		Set<String> related = new LinkedHashSet<String>();
		// Get all of the hypernyms (parents) of each synset
		Dictionary d = Dictionary.getDefaultResourceInstance();
		for (int i=0;i<tokens.length;i++) {
			d.lookupAllIndexWords(tokens[i]);
			IndexWordSet wnWordSet = d.lookupAllIndexWords(tokens[i]);
			
			if (wnWordSet != null) {
				if (wnWordSet.size() > 0)
					for (IndexWord w : wnWordSet.getIndexWordCollection()) {
						//Getting only the first sense
						if (!w.getSenses().get(0).getWords().isEmpty()) {
							PointerTargetNodeList hypernyms = PointerUtils.getDirectHypernyms(w.getSenses().get(0));
							for(int j=0; j<hypernyms.size();++j) {
								for(Word hyperWord : hypernyms.get(j).getSynset().getWords())
									related.add(hyperWord.getLemma());
							}
							if(addHyponyms) {
								PointerTargetNodeList hyponyms =  PointerUtils.getDirectHyponyms(w.getSenses().get(0));
								//hyponyms.print();
								for(int j=0; j<hyponyms.size();++j) {
									for(Word hypoWord : hyponyms.get(j).getSynset().getWords())
										related.add(hypoWord.getLemma());
								}
							}
						}
						//Getting all senses
						/*if (w.getSenses() != null) {
							System.out.println("the word: " + w.getLemma() + " has " + w.getSenses().size() + " senses as " + w.getPOS());
							for (Synset syn : w.getSenses()) {
								PointerTargetNodeList hypernyms = PointerUtils.getDirectHypernyms(syn);
								//hypernyms.print();
								for(int i=0; i<hypernyms.size();++i) {
									for(Word hyperWord : hypernyms.get(i).getSynset().getWords())
										related.add(hyperWord.getLemma());
								}
								if(addHyponyms) {
									PointerTargetNodeList hyponyms =  PointerUtils.getDirectHyponyms(syn);
									//hyponyms.print();
									for(int i=0; i<hyponyms.size();++i) {
										for(Word hypoWord : hyponyms.get(i).getSynset().getWords())
											related.add(hypoWord.getLemma());
									}
								}
							}
						}*/
					}
			}
			/*try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		}
		//System.out.println("WordNet related Words: " + related);
		return related;
	}

	/*@Override
	public void collectionProcessComplete() {
		// nothing
	}*/
}
