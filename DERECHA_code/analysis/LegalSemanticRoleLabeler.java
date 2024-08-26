package lu.svv.saa.linklaters.dpa.analysis;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.util.JCasUtil.select;
import static org.apache.uima.fit.util.JCasUtil.selectCovered;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.uima.UIMAException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.component.JCasAnnotator_ImplBase;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.semantics.type.SemPred;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.Chunk;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import de.tudarmstadt.ukp.dkpro.core.matetools.MateLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpChunker;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;
import de.tudarmstadt.ukp.dkpro.core.snowball.SnowballStemmer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordNamedEntityRecognizer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordParser;
import lu.svv.saa.linklaters.dpa.type.LegalSemanticRole;
import lu.svv.saa.linklaters.dpa.type.MySentence;
import lu.svv.saa.linklaters.dpa.utils.PARAMETERS_VALUES;

public class LegalSemanticRoleLabeler extends JCasAnnotator_ImplBase implements PARAMETERS_VALUES {

	private AnalysisEngine SRLPipeline;
	Pattern pattern;

	JCas pcas = null;
	JCas tcas = null;

	@Override
	public void initialize(final UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		/* OLD PIPELINE REPLACED
		 * SRLPipeline =
		 * createEngine(createEngineDescription(createEngineDescription(OpenNlpSegmenter
		 * .class), createEngineDescription(MateLemmatizer.class),
		 * createEngineDescription(OpenNlpPosTagger.class),
		 * createEngineDescription(StanfordParser.class),
		 * createEngineDescription(ClearNlpSemanticRoleLabeler.class)));
		 */
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

		for (MySentence sentence : select(aJCas, MySentence.class)) {
			// TODO: take chunks from the NLP pipeline
			// Hint: play with the sentence offsets + the chunk offsets to get the right begin and end for the semantic role
			pcas = textParsing(sentence.getCoveredText());

			// Identify main action
			String root = identifyMainAction(pcas, aJCas, sentence);

			// Identify main actor
			identifyMainActor(pcas, aJCas, sentence, root);

			// Identify object and beneficiary/target
			identifyObjectBeneficary(pcas, aJCas, sentence, root);

			// Identify condition
			identifyCondition(pcas, aJCas, sentence);

			// Identify constraint
			identifyConstraint(pcas, aJCas, sentence);

			// Identify temporal characteristic
			identifyTime(pcas, aJCas, sentence);

			// Identify reason
			identifyReason(pcas, aJCas, sentence, root);

			// Identify event
			identifySituation(pcas, aJCas, sentence);

			// Identify reference
			identifyReference(pcas, aJCas, sentence);
		}

	}

	private String identifyMainAction(JCas pcas, JCas aJCas, MySentence sentence) {
		// find the root verb in the sentence (There is an exception for the verb to be
		// that must be fixed!!)
		String root = "", internal_root = "";
		String patternString = "";
		int begin = 1000000;
		int end = 0;
		boolean root_flag = false;
		for (Dependency dep : select(pcas, Dependency.class)) {
			if (dep.getDependencyType().equals("root")) {
				internal_root = dep.getDependent().getCoveredText();
				root = dep.getDependent().getCoveredText();
				break;
			}
		}
		for (Dependency dep : select(pcas, Dependency.class)) {
			if (dep.getDependencyType().equals("cop") && dep.getGovernor().getCoveredText().equalsIgnoreCase(root)) {
				internal_root = dep.getDependent().getCoveredText();
				root = dep.getDependent().getCoveredText() + " " + root;
				break;
			}
			else if (dep.getDependencyType().equals("acomp") && dep.getGovernor().getCoveredText().equalsIgnoreCase(root)) {
				internal_root = root;
				root = root + " " + dep.getDependent().getCoveredText();
				break;
			}
		}
		if (!internal_root.equalsIgnoreCase("")) {
			//System.out.println("\n" + root + " is the root of the sentence: " + sentence.getCoveredText() + "\n");
			// find the chunk containing that root verb
		    // TODO: Apply in all matching calls - Avoid being trapped by reserved characters in regex like ., [
			patternString = "\\b" + internal_root.replaceAll("[^a-zA-Z\\s]", "").strip() + "\\b"; // match full words only
			pattern = Pattern.compile(patternString);
			for (Chunk chunk : selectCovered(Chunk.class, sentence)) {
				Matcher matcher = pattern.matcher(chunk.getCoveredText().replaceAll("[^a-zA-Z\\s]", "").strip()); // Remove special characters
				// assign ``main action'' to the chunk that contains the verb
				if (matcher.find() && (chunk.getChunkValue().equalsIgnoreCase("VP")
						//|| chunk.getChunkValue().equalsIgnoreCase("ADJP")
						)) {
					begin = chunk.getBegin();
					end = chunk.getEnd();
					//newLegalSemanticRole(aJCas, chunk.getBegin(), chunk.getEnd(), MAIN_ACTION);
					root_flag = true;
					//break;
				}
				if (chunk.getChunkValue().equalsIgnoreCase("ADJP") && root_flag) {
					end = chunk.getEnd();
					newLegalSemanticRole(aJCas, begin, end, MAIN_ACTION);
					root_flag = false;
					break;
				}
				else if (root_flag) {
					newLegalSemanticRole(aJCas, begin, end, MAIN_ACTION);
					root_flag = false;
					break;
				}
			}
		}
		//System.out.println("Root: " + root);
		//System.out.println("Internal root: " + internal_root);
		return root;
	}

	private void identifyMainActor(JCas pcas, JCas aJCas, MySentence sentence, String root) {
		// find the subject of the root verb in the sentence
		String nsubj = "";
		String patternString = "";

		for (Dependency dep : select(pcas, Dependency.class)) {
			if (dep.getDependencyType().equals("nsubj") && root.contains(dep.getGovernor().getCoveredText())) {
				nsubj = dep.getDependent().getCoveredText();
			}
		}
		if (!nsubj.equalsIgnoreCase("")) {
			// find the chunk containing the main actor
			patternString = "\\b" + nsubj.replaceAll("[^a-zA-Z\\s]", "").strip() + "\\b"; // match full words only
			pattern = Pattern.compile(patternString);
			for (Chunk chunk : selectCovered(Chunk.class, sentence)) {
				Matcher matcher = pattern.matcher(chunk.getCoveredText().replaceAll("[^a-zA-Z\\s]", "").strip());
				// assign ``main actor'' to the chunk that contains the subject
				if (matcher.find()) {
					newLegalSemanticRole(aJCas, chunk.getBegin(), chunk.getEnd(), MAIN_ACTOR);
					break;
				}
			}
		}

	}

	private void identifySecondaryAction(JCas pcas, JCas aJCas, MySentence sentence, String root) {
		// find the verb phrase of the sentence not containing the root verb
		List<Object> vp = new ArrayList<Object>();
		String patternString = "";
		String adv = "";
		boolean ch_flag = false, sa_flag = false;
		int begin = 1000000;

		for (Chunk chunk : selectCovered(Chunk.class, sentence)) {
			if (chunk.getChunkValue().equalsIgnoreCase("VP") && !chunk.getCoveredText().contains(root)
					&& !chunk.getCoveredText().contains("demonstrate")) {
				for (int x = 0; x < chunk.getCoveredText().length(); x++) {
					char c = chunk.getCoveredText().charAt(x);
					if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == ' ')) // if is not a letter or
																							// space
						ch_flag = true;
				}
				if (!ch_flag) {
					vp.add(chunk.getCoveredText());
					// newLegalSemanticRole(aJCas, chunk.getBegin(), chunk.getEnd(),
					// SECONDARY_ACTION);
					begin = chunk.getBegin();
					sa_flag = true;
					// break;
				}
			}
			// Adding the span
			else if (chunk.getChunkValue().equalsIgnoreCase("PP") && sa_flag)
				break;
			else if (chunk.getChunkValue().equalsIgnoreCase("NP") && sa_flag
					&& !chunk.getCoveredText().contains("level")) {
				newLegalSemanticRole(aJCas, begin, chunk.getEnd(), SECONDARY_ACTION);
				sa_flag = false;
				break;
			}
		}

		// New cases
		for (Dependency dep : select(pcas, Dependency.class)) {
			// First new case
			if (dep.getDependencyType().equals("prepc_without") && dep.getGovernor().getCoveredText().equalsIgnoreCase(root)) {
				adv = dep.getDependent().getCoveredText();
			}
			// Second new case
			else if (dep.getDependencyType().equals("vmod")) {
				adv = dep.getDependent().getCoveredText();
			}
			// Third new case
			else if (dep.getDependencyType().equals("aux")) {
				adv = dep.getDependent().getCoveredText();
			}
			// Fourth new case
			else if (dep.getDependencyType().equals("rcmod")) {
				adv = dep.getDependent().getCoveredText();
			}
			// Sixth new case
			else if (dep.getDependencyType().equals("ccomp")) {
				adv = dep.getDependent().getCoveredText();
			}
		}
		
		if (!adv.isEmpty()) {
			// find the chunk containing the secondary action
			patternString = "\\b" + adv.replaceAll("[^a-zA-Z\\s]", "").strip() + "\\b"; // match full words only
			pattern = Pattern.compile(patternString);
			for (Chunk chunk : selectCovered(Chunk.class, sentence)) {
				Matcher matcher = pattern.matcher(chunk.getCoveredText().replaceAll("[^a-zA-Z\\s]", "").strip());
				// assign ``secondary action'' to the chunk that contains the adverb
				if (matcher.find()) {
					newLegalSemanticRole(aJCas, chunk.getBegin(), chunk.getEnd(), SECONDARY_ACTION);
					break;
				}
			}
		}
	}

	private void identifySecondaryActor(JCas pcas, JCas aJCas, MySentence sentence, String root) {
		// find the subject of the root verb in the sentence
		List<String> sa = new ArrayList<String>();
		List<String> markers_nsa = Arrays.asList("processing", "law", "public", "instruction", "document", "record",
				"cause", "result", "office", "data", "measure", "it");
		String governor = "", dependent = "", patternString = "";
		boolean nsa_flag = false;
		for (Dependency dep : select(pcas, Dependency.class)) {
			if ((dep.getDependencyType().equals("nsubj") || dep.getDependencyType().equals("agent"))
					&& !dep.getGovernor().getCoveredText().contains(root)
					&& !sa.contains(dep.getDependent().getCoveredText()))
				sa.add(dep.getDependent().getCoveredText());
			else if ((dep.getDependencyType().equals("prep_from") || dep.getDependencyType().equals("prep_of"))
					&& !dep.getGovernor().getCoveredText().contains(root)) {
				governor = dep.getGovernor().getCoveredText();
				dependent = dep.getDependent().getCoveredText();
				for (Dependency depen : select(pcas, Dependency.class)) {
					if ((depen.getDependencyType().equals("prep_on")
							|| depen.getDependencyType().equals("prep_without"))
							&& depen.getCoveredText().equals(governor)) {
						sa.add(dependent);
						break;
					}
				}
			}
			else if (dep.getDependencyType().equals("prep_between")) // && dep.getGovernor().getCoveredText().equalsIgnoreCase("agreed")
				sa.add(dep.getDependent().getCoveredText());
		}
		// find the chunk containing the secondary actor
		if (!sa.isEmpty()) {
			for (int i = 0; i < sa.size(); i++) {
				patternString = "\\b" + sa.get(i).replaceAll("[^a-zA-Z\\s]", "").strip() + "\\b"; // match full words only
				pattern = Pattern.compile(patternString);
				for (Chunk chunk : selectCovered(Chunk.class, sentence)) {
					Matcher matcher = pattern.matcher(chunk.getCoveredText().replaceAll("[^a-zA-Z\\s]", "").strip());
					// assign ``secondary actor'' to the chunk that contains the subject
					if (matcher.find() && chunk.getChunkValue().equalsIgnoreCase("NP")) {
						for (int j = 0; j < markers_nsa.size(); j++) {
							if (chunk.getCoveredText().toLowerCase().contains(markers_nsa.get(j))) {
								nsa_flag = true;
								break;
							}
						}
						if (!nsa_flag) {
							newLegalSemanticRole(aJCas, chunk.getBegin(), chunk.getEnd(), SECONDARY_ACTOR);
							break;
						} else
							nsa_flag = false;
					}
				}
			}
		}
	}

	private void identifyObjectBeneficary(JCas pcas, JCas aJCas, MySentence sentence, String root) {
		// find all the objects of the sentence
		String patternString = "";
		List<String> markers_obj1 = Arrays.asList("engage", "process", "take", "return", "delete", "forget", 
		"eliminate", "remove", "return", "give back", "hand_out", "demonstrate", "contribute", 
		"impose", "obligate", "oblige", "transfer", "move", "send", "communicate", "implement", "comply", "fulfil", "ensure", 
		"assure", "trust", "guarantee", "insure", "secure", "reinsure", "consult", "notify", "pursuant", "disclose");
		List<String> markers_obj2 = Arrays.asList("commit", "take", "seek", "search", "look", "request", "solicit", "ask", "carry");
		List<String> markers_bt1 = Arrays.asList("inform", "report", "assist", "help", "aid", "support", "help out", "remain", "notify");
		List<String> markers_bt2 = Arrays.asList("provide", "give", "supply");
		/*List<String> markers_obj1 = Arrays.asList("engage", "process", "take", "return", "delete", "contribute",
				"demonstrate", "impose", "transfer", "implement", "comply", "fulfill", "ensure", "consult",
				"notify", "communicate", "pursuant", "disclose");
		List<String> markers_obj2 = Arrays.asList("commit", "take", "seek", "carry");
		List<String> markers_bt1 = Arrays.asList("inform", "assist", "remain", "notify");
		List<String> markers_bt2 = Arrays.asList("provide");*/
		List<Object> dobjs = new ArrayList<Object>();
		List<String> objs_flags = new ArrayList<String>();

		for (Dependency dep : select(pcas, Dependency.class)) {
			if (dep.getDependencyType().equals("dobj")) {
				// case 0
				if (dep.getDependent().getCoveredText().equalsIgnoreCase("data") && dep.getGovernor().getCoveredText().equalsIgnoreCase("transferred")) {
					dobjs.add(dep.getDependent().getCoveredText());
					objs_flags.add("Object");
				}
				// case 1
				if (markers_obj2.contains(dep.getGovernor().getCoveredText())) {
					dobjs.add(dep.getDependent().getCoveredText());
					objs_flags.add("Object");
				}
				else if (markers_obj1.contains(dep.getGovernor().getCoveredText())) {
					dobjs.add(dep.getDependent().getCoveredText());
					objs_flags.add("Object");
				}
				if (markers_bt1.contains(dep.getGovernor().getCoveredText())) {
					dobjs.add(dep.getDependent().getCoveredText());
					objs_flags.add("BT");
				}
				else if (markers_bt2.contains(dep.getGovernor().getCoveredText())) {
					dobjs.add(dep.getDependent().getCoveredText());
					objs_flags.add("BT");
				}
			}
		}
		
		for (Dependency dep : select(pcas, Dependency.class)) {
			// case 4
			if (dep.getDependencyType().equals("vmod") && dep.getGovernor().getCoveredText().equalsIgnoreCase("right")) {
				dobjs.add(dep.getDependent().getCoveredText());
				objs_flags.add("Object");
			}
			// case 5
			if (dep.getDependencyType().equals("prep_to") && dep.getGovernor().getCoveredText().equalsIgnoreCase(root)) {
				dobjs.add(dep.getDependent().getCoveredText());
				objs_flags.add("Object");
				break;
			}
			// case 6
			if (dep.getDependencyType().equals("prep_of") && dep.getGovernor().getCoveredText().equalsIgnoreCase(root)) {
				dobjs.add(dep.getDependent().getCoveredText());
				objs_flags.add("Object");
				break;
			}
			// case 7
			if (dep.getDependencyType().equals("prepc_in") && dep.getGovernor().getCoveredText().equalsIgnoreCase(root)) {
				dobjs.add(dep.getDependent().getCoveredText());
				objs_flags.add("Object");
				break;
			}
			//--------------------------------------------------
			if (dep.getDependencyType().equals("prep_to") && (dep.getGovernor().getCoveredText().equalsIgnoreCase("available")
					|| dep.getGovernor().getCoveredText().equalsIgnoreCase("contribute"))) {
				dobjs.add(dep.getDependent().getCoveredText());
				objs_flags.add("Object");
				break;
			}
			//--------------------------------------------------
			// case 8
			if (dep.getDependencyType().equals("prep_to") && dep.getGovernor().getCoveredText().equalsIgnoreCase("liable")) {
				dobjs.add(dep.getDependent().getCoveredText());
				objs_flags.add("BT");
				break;
			}
			// case 9
			if (dep.getDependencyType().equals("prep_of") && dep.getGovernor().getCoveredText().equalsIgnoreCase("controller")) {
				dobjs.add(dep.getDependent().getCoveredText());
				objs_flags.add("Object");
				break;
			}
			// case 10
			if (dep.getDependencyType().equals("prep_with") && dep.getGovernor().getCoveredText().equalsIgnoreCase("complied")) {
				dobjs.add(dep.getDependent().getCoveredText());
				objs_flags.add("Object");
				break;
			}
			// case 11
			if (dep.getDependencyType().equals("nn") && dep.getGovernor().getCoveredText().equalsIgnoreCase("information")) {
				dobjs.add(dep.getDependent().getCoveredText());
				objs_flags.add("BT");
				break;
			} 
			// case 12
			if (dep.getDependencyType().equals("nn") && dep.getGovernor().getCoveredText().equalsIgnoreCase("data")) {
				dobjs.add(dep.getGovernor().getCoveredText());
				objs_flags.add("Object");
				break;
			}
			// case 13
			if (dep.getDependencyType().equals("dobj") && dep.getGovernor().getCoveredText().equalsIgnoreCase(root)
					&& !root.equalsIgnoreCase("document") && !root.equalsIgnoreCase("seek") && !root.equalsIgnoreCase("carry")
					&& !root.equalsIgnoreCase("have")) {
				dobjs.add(dep.getDependent().getCoveredText());
				objs_flags.add("BT");
				break;
			}
		}
		// find the chunk containing the object or beneficiary/target
		dobjs = dobjs.stream().distinct().collect(Collectors.toList());
		if (!dobjs.isEmpty()) {
			for (int i = 0; i < dobjs.size(); i++) {
				//dobj casted here to enable the use of replaceAll
				patternString = "\\b" + ((String) dobjs.get(i)).replaceAll("[^a-zA-Z\\s]", "").strip() + "\\b"; // match full words only
				pattern = Pattern.compile(patternString);
				for (Chunk chunk : selectCovered(Chunk.class, sentence)) {
					Matcher matcher = pattern.matcher(chunk.getCoveredText().replaceAll("[^a-zA-Z\\s]", "").strip());
					// assign ``object or beneficiary/target'' to the chunk that contains the object
					if (matcher.find()) {
						if (objs_flags.get(i).equalsIgnoreCase("Object"))
							newLegalSemanticRole(aJCas, chunk.getBegin(), chunk.getEnd(), OBJECT);
						if (objs_flags.get(i).equalsIgnoreCase("BT"))
							newLegalSemanticRole(aJCas, chunk.getBegin(), chunk.getEnd(), BENEFICIARY_TARGET);
						break;
					}
				}
			}
		}
		
		dobjs = new ArrayList<Object>();
		objs_flags = new ArrayList<String>();
		for (Dependency dep : select(pcas, Dependency.class)) {
			if (dep.getDependencyType().equalsIgnoreCase("nsubj") && !root.contains(dep.getGovernor().getCoveredText())) {
				dobjs.add(dep.getDependent().getCoveredText());
				objs_flags.add("BT");
			}
			if (dep.getDependencyType().equals("prep_on") && dep.getGovernor().getCoveredText().equalsIgnoreCase(root)) {
				dobjs.add(dep.getDependent().getCoveredText());
				objs_flags.add("BT");
				break;
			}
			if (dep.getDependencyType().equals("prep_to") && dep.getGovernor().getCoveredText().equalsIgnoreCase("data")) {
				dobjs.add(dep.getDependent().getCoveredText());
				objs_flags.add("BT");
				break;
			}
		}
		dobjs = dobjs.stream().distinct().collect(Collectors.toList());
		if (!dobjs.isEmpty()) {
			for (int i = 0; i < dobjs.size(); i++) {
				//dobj casted here to enable the use of replaceAll
				patternString = "\\b" + ((String) dobjs.get(i)).replaceAll("[^a-zA-Z\\s]", "").strip() + "\\b"; // match full words only
				pattern = Pattern.compile(patternString);
				for (Chunk chunk : selectCovered(Chunk.class, sentence)) {
					Matcher matcher = pattern.matcher(chunk.getCoveredText().replaceAll("[^a-zA-Z\\s]", "").strip());
					// assign ``object or beneficiary/target'' to the chunk that contains the object
					if (matcher.find()) {
						if (objs_flags.get(i).equalsIgnoreCase("Object"))
							newLegalSemanticRole(aJCas, chunk.getBegin(), chunk.getEnd(), OBJECT);
						if (objs_flags.get(i).equalsIgnoreCase("BT"))
							newLegalSemanticRole(aJCas, chunk.getBegin(), chunk.getEnd(), BENEFICIARY_TARGET);
						break;
					}
				}
			}
		}
		
	}

	private void identifyCondition(JCas pcas, JCas aJCas, MySentence sentence) {
		// find the PP | SBAR | ADVP phrase (chunk) containing the markers
		List<String> markers_c = Arrays.asList("if", "as", "once", "case", "where", "wherein", "when", "whenever");
		//List<String> markers_c = Arrays.asList("if", "case", "where", "when");
		boolean nc_flag = false, flag_first = false;
		int begin = 1000000;
		int end = 0;

		for (Chunk chunk : selectCovered(Chunk.class, sentence)) {
			for (int i = 0; i < markers_c.size(); i++) {
				if ((chunk.getChunkValue().equalsIgnoreCase("PP") || chunk.getChunkValue().equalsIgnoreCase("SBAR")
						|| chunk.getChunkValue().equalsIgnoreCase("ADVP") || chunk.getChunkValue().equalsIgnoreCase("NP")
						|| chunk.getChunkValue().equalsIgnoreCase("VP")) && !chunk.getCoveredText().toLowerCase().contains("fy")
						&& !chunk.getCoveredText().toLowerCase().contains("fi") 
						&& chunk.getCoveredText().toLowerCase().contains(markers_c.get(i))) {
					// newLegalSemanticRole(aJCas, chunk.getBegin(), chunk.getEnd(), CONDITION);
					if (chunk.getBegin() < begin)
						begin = chunk.getBegin();
					nc_flag = true;
					//break;
				}
				// find the PP, VP and NP phrases (chunk) containing to include as part of the
				// condition
				else if ((chunk.getChunkValue().equalsIgnoreCase("PP") || chunk.getChunkValue().equalsIgnoreCase("VP")) && nc_flag) {
					end = chunk.getEnd();
					//break;
				} else if (nc_flag) {
					end = chunk.getEnd();
					newLegalSemanticRole(aJCas, begin, end, CONDITION);
					nc_flag = false;
					break;
				} if (nc_flag && chunk.getEnd() == sentence.getEnd() - 1) {
					end = chunk.getEnd();
					newLegalSemanticRole(aJCas, begin, end, CONDITION);
					nc_flag = false;
					break;
				}
			}
		}

	}

	private void identifyConstraint(JCas pcas, JCas aJCas, MySentence sentence) {
		// find the PP | VP | SBAR phrase (chunk) containing the markers
		List<String> markers_ct = Arrays.asList("without", "on", "according to", "in accordance with", "along", "by", "under", "unless");
		//List<String> markers_ct = Arrays.asList("without", "on", "by", "under", "unless");
		boolean c_flag = false;
		int begin = 1000000;
		int end = 0;

		for (Chunk chunk : selectCovered(Chunk.class, sentence)) {
			for (int i = 0; i < markers_ct.size(); i++) {
				if ((chunk.getChunkValue().equalsIgnoreCase("PP") || chunk.getChunkValue().equalsIgnoreCase("VP")
						|| chunk.getChunkValue().equalsIgnoreCase("SBAR"))
						&& chunk.getCoveredText().toLowerCase().equalsIgnoreCase(markers_ct.get(i))) {
					// newLegalSemanticRole(aJCas, chunk.getBegin(), chunk.getEnd(), CONSTRAINT);
					if (chunk.getBegin() < begin) {
						begin = chunk.getBegin();
						end = chunk.getEnd();
					}
					c_flag = true;
					break;
				} else if ((chunk.getChunkValue().equalsIgnoreCase("NP")
						|| chunk.getChunkValue().equalsIgnoreCase("PP")) && c_flag)
					end = chunk.getEnd();
				else if (c_flag) {
					newLegalSemanticRole(aJCas, begin, end, CONSTRAINT);
					c_flag = false;
					break;
				}
				if (c_flag && (chunk.getEnd() == sentence.getEnd() - 1 || chunk.getEnd() == sentence.getEnd() - 2)) {
					newLegalSemanticRole(aJCas, begin, end, CONSTRAINT);
					c_flag = false;
					break;
				}
			}
		}

	}

	private void identifyTime(JCas pcas, JCas aJCas, MySentence sentence) {
		// find the NP phrase (chunk) containing the markers
		List<String> markers_tch = Arrays.asList("after", "later", "afterward", "later on", "prior", "anterior", 
		"before", "earlier", "as long as", "as soon as");
		//List<String> markers_tch = Arrays.asList("after", "prior", "before", "as long as", "as soon as");
		int begin = 1000000;
		int end = 0;
		boolean np_flag = false;

		for (Chunk chunk : selectCovered(Chunk.class, sentence)) {
			for (int i = 0; i < markers_tch.size(); i++) {
				if ((chunk.getChunkValue().equalsIgnoreCase("NP") || chunk.getChunkValue().equalsIgnoreCase("CONJP"))
						&& chunk.getCoveredText().toLowerCase().contains(markers_tch.get(i)))
					newLegalSemanticRole(aJCas, chunk.getBegin(), chunk.getEnd(), TEMPORAL_CHARACTERISTIC);
			}
		}
		// find the PP or ADVP phrase (chunk) containing the markers
		for (Chunk chunk : selectCovered(Chunk.class, sentence)) {
			for (int j = 0; j < markers_tch.size(); j++) {
				if ((chunk.getChunkValue().equalsIgnoreCase("PP") || chunk.getChunkValue().equalsIgnoreCase("ADVP"))
						&& chunk.getCoveredText().toLowerCase().contains(markers_tch.get(j))) {
					if (chunk.getBegin() < begin) {
						begin = chunk.getBegin();
						end = chunk.getEnd();
					}
					np_flag = true;
					break;
				} else if ((chunk.getChunkValue().equalsIgnoreCase("NP") || chunk.getChunkValue().equalsIgnoreCase("PP")
						|| chunk.getChunkValue().equalsIgnoreCase("VP")
						|| chunk.getChunkValue().equalsIgnoreCase("ADJP")) && np_flag)
					end = chunk.getEnd();
				else if (np_flag) {
					newLegalSemanticRole(aJCas, begin, end, TEMPORAL_CHARACTERISTIC);
					np_flag = false;
				}
				if (np_flag && (chunk.getEnd() == sentence.getEnd() - 1 || chunk.getEnd() == sentence.getEnd() - 2)) {
					newLegalSemanticRole(aJCas, begin, end, TEMPORAL_CHARACTERISTIC);
					np_flag = false;
				}
			}
		}
	}

	private void identifyReason(JCas pcas, JCas aJCas, MySentence sentence, String root) {
		// find all the aux in the sentence
		List<String> aux = new ArrayList<String>();
		//List<String> markers_nr = Arrays.asList("process", "result", "commit", "be", "has", "object", "terminate", "suspend");
		boolean pp_flag = false, r_flag = false, add_flag = false, xcomp_flag = false, vpto_flag = false;
		String xcomp = "";
		boolean ch_flag = false;
		int begin = 1000000;
		int end = 0;

		for (Chunk chunk : selectCovered(Chunk.class, sentence)) {
			if (chunk.getChunkValue().equalsIgnoreCase("VP") && chunk.getCoveredText().contains("to")) {
				begin = chunk.getBegin();
				pp_flag = true;
				break;
			}
			if (pp_flag) {
				end = chunk.getEnd();
				newLegalSemanticRole(aJCas, begin, end, REASON);
				vpto_flag = true;
				pp_flag = false;
				break;
			}
		}
		
		for (Dependency dep : select(pcas, Dependency.class)) {
			if (dep.getDependencyType().equals("aux") && dep.getGovernor().getCoveredText().contains(root)) {
				aux.add(dep.getDependent().getCoveredText());
			}
		}
		// find the verb phrase (chunk) not containing the root
		for (Chunk chunk : selectCovered(Chunk.class, sentence)) {
			if (chunk.getChunkValue().equalsIgnoreCase("VP") && !chunk.getCoveredText().contains(root)) {
				for (int x = 0; x < chunk.getCoveredText().length(); x++) {
					char c = chunk.getCoveredText().charAt(x);
					if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == ' ')) // if is not a letter or
																							// space
						ch_flag = true;
				}
				if (!ch_flag) {
					for (int i = 0; i < aux.size(); i++) {
						if (chunk.getCoveredText().contains(aux.get(i))) {
							begin = chunk.getBegin();
							// newLegalSemanticRole(aJCas, chunk.getBegin(), chunk.getEnd(), REASON);
							add_flag = true;
						}
					}
				}
			} else if (chunk.getChunkValue().equalsIgnoreCase("NP") && add_flag) {
				newLegalSemanticRole(aJCas, begin, chunk.getEnd(), REASON);
				add_flag = false;
				r_flag = true;
			}
		}
		
		// New case
		if (!r_flag) {
			r_flag = true;
			for (Dependency dep : select(pcas, Dependency.class)) {
				if (dep.getDependencyType().equalsIgnoreCase("xcomp")) {
					xcomp = dep.getDependent().getCoveredText();
				}
			}
			if (xcomp.equalsIgnoreCase("mitigate")) {
				for (Chunk chunk : selectCovered(Chunk.class, sentence)) {
					if (chunk.getChunkValue().equalsIgnoreCase("VP") && chunk.getCoveredText().contains(xcomp)) {
						begin = chunk.getBegin();
						xcomp_flag = true;
						// break;
					} else if (chunk.getChunkValue().equalsIgnoreCase("NP") && xcomp_flag) {
						newLegalSemanticRole(aJCas, begin, chunk.getEnd(), REASON);
						xcomp_flag = false;
						break;
					} else
						xcomp_flag = false;
				}
			}
			if(!vpto_flag) {
				// find the for PP phrase (chunk) followed by a NP (chunk)
				for (Chunk chunk : selectCovered(Chunk.class, sentence)) {
					if (chunk.getChunkValue().equalsIgnoreCase("PP") && chunk.getCoveredText().equalsIgnoreCase("for")) {
						begin = chunk.getBegin();
						pp_flag = true;
						// break;
					} else if (chunk.getChunkValue().equalsIgnoreCase("NP") && pp_flag) {
						newLegalSemanticRole(aJCas, begin, chunk.getEnd(), REASON);
						pp_flag = false;
						break;
					} else
						pp_flag = false;
				}
			}
		}
	}

	private void identifySituation(JCas pcas, JCas aJCas, MySentence sentence) {
		// find the NP phrase (chunk) containing the markers
		List<String> markers_e = Arrays.asList("addition", "replacement", "request", "petition", "quest", "solicitation", 
		"destruction", "devastation", "loss", "alternation", "disclosure", "revelation", "access", "entree", "accession", 
		"admission", "admittance", "adherence", "attachment", "adhesion", "certification", "vary", "change", "exchange", 
		"alter", "modification", "modify", "substitution", "termination", "expiration", "expiry", "ending", "conclusion");
		/*List<String> markers_e = Arrays.asList("addition", "replacement", "request", "destruction", "loss", "alternation", 
		"disclosure", "access", "adherence", "certification", "commit", "vary", "request", "termination", "expiry", "change");*/
		boolean e_flag = false;
		int begin = 1000000;
		int end = 0;

		for (Chunk chunk : selectCovered(Chunk.class, sentence)) {
			for (int i = 0; i < markers_e.size(); i++) {
				if ((chunk.getChunkValue().equalsIgnoreCase("NP") || chunk.getChunkValue().equalsIgnoreCase("VP"))
						&& chunk.getCoveredText().contains(markers_e.get(i))) {
					if (chunk.getBegin() < begin) {
						begin = chunk.getBegin();
						end = chunk.getEnd();
					}
					e_flag = true;
					break;
				} else if ((chunk.getChunkValue().equalsIgnoreCase("NP") || chunk.getChunkValue().equalsIgnoreCase("PP")
						|| chunk.getChunkValue().equalsIgnoreCase("VP")) && e_flag)
					end = chunk.getEnd();
				else if (e_flag) {
					newLegalSemanticRole(aJCas, begin, end, EVENT);
					e_flag = false;
					break;
				}
				if (e_flag && chunk.getEnd() == sentence.getEnd() - 1) {
					newLegalSemanticRole(aJCas, begin, end, EVENT);
					e_flag = false;
					break;
				}
			}
		}
		for (Chunk chunk : selectCovered(Chunk.class, sentence)) {
			if (chunk.getChunkValue().equalsIgnoreCase("NP") && chunk.getCoveredText().contains("breach")) {
				newLegalSemanticRole(aJCas, chunk.getBegin(), chunk.getEnd(), EVENT);
			}
		}

	}

	private void identifyReference(JCas pcas, JCas aJCas, MySentence sentence) {
		// find the NP phrase (chunk) containing the markers
		List<String> markers_ref = Arrays.asList("law", "jurisprudence", "article", "gdpr", "dpa", "agreement", 
		"legislation", "statute law", "international law", "statutory law", "statement", "covenant", "contract");
		//List<String> markers_ref = Arrays.asList("law", "article", "gdpr", "dpa", "agreement", "legislation");
		for (Chunk chunk : selectCovered(Chunk.class, sentence)) {
			for (int i = 0; i < markers_ref.size(); i++) {
				if (chunk.getChunkValue().equalsIgnoreCase("NP") && !chunk.getCoveredText().toLowerCase().contains("lawful")
						&& chunk.getCoveredText().toLowerCase().contains(markers_ref.get(i))) {
					newLegalSemanticRole(aJCas, chunk.getBegin(), chunk.getEnd(), REFERENCE);
					break;
				}	
			}
		}

	}

	private void identifyAttribute(JCas pcas, JCas aJCas, MySentence sentence) {
		// find the ADJP phrase (chunk) containing the marker
		List<String> markers_att = Arrays.asList("full");

		for (Chunk chunk : selectCovered(Chunk.class, sentence)) {
			for (int i = 0; i < markers_att.size(); i++) {
				if ((chunk.getChunkValue().equalsIgnoreCase("NP") || chunk.getChunkValue().equalsIgnoreCase("ADJP"))
						&& chunk.getCoveredText().toLowerCase().contains(markers_att.get(i)))
					newLegalSemanticRole(aJCas, chunk.getBegin(), chunk.getEnd(), ATTRIBUTE);
			}
		}
	}

	private void identifyLocation(JCas pcas, JCas aJCas, MySentence sentence) {
		// find the PP or ADJP phrase (chunk) containing the markers
		List<String> markers_loc = Arrays.asList("outside");	//, "contrary", "in"
		boolean l_flag = false;
		int begin = 1000000;
		int end = 0;
		
		for (Chunk chunk : selectCovered(Chunk.class, sentence)) {
			for (int i=0;i<markers_loc.size();i++) {
				if ((chunk.getChunkValue().equalsIgnoreCase("PP") || chunk.getChunkValue().equalsIgnoreCase("ADJP")
						|| chunk.getChunkValue().equalsIgnoreCase("NP")) && chunk.getCoveredText().toLowerCase().contains(markers_loc.get(i))) {
					if (chunk.getBegin() < begin) {
						begin = chunk.getBegin();
						end = chunk.getEnd();
					}
					l_flag = true;
				} else if ((chunk.getChunkValue().equalsIgnoreCase("PP") || chunk.getChunkValue().equalsIgnoreCase("NP")) && l_flag) {
					end = chunk.getEnd();
				} else if (l_flag) {
					newLegalSemanticRole(aJCas, begin, end, LOCATION);
					l_flag = false;
					break;
				} if (l_flag && chunk.getEnd() == sentence.getEnd() - 1) {
					newLegalSemanticRole(aJCas, begin, end, LOCATION);
					l_flag = false;
					break;
				}
			}	
		}
	}

	// Applying the parsing pipeline
	public JCas textParsing(String text) {
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

	// Create the LegalSemanticRole type and push it to the aJCas object
	private void newLegalSemanticRole(JCas aJCas, int begin, int end, String role) {
		if (end > begin) {
			LegalSemanticRole lsr = new LegalSemanticRole(aJCas);
			lsr.setBegin(begin);
			lsr.setEnd(end);
			lsr.setSemanticRole(role);
			lsr.addToIndexes();
		}
	}

}
