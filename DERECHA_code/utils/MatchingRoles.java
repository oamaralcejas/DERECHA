package lu.svv.saa.linklaters.dpa.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.HirstStOnge;
import edu.cmu.lti.ws4j.impl.JiangConrath;
import edu.cmu.lti.ws4j.impl.LeacockChodorow;
import edu.cmu.lti.ws4j.impl.Lesk;
import edu.cmu.lti.ws4j.impl.Lin;
import edu.cmu.lti.ws4j.impl.Path;
import edu.cmu.lti.ws4j.impl.Resnik;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;
import edu.mit.jverbnet.data.IMember;
import edu.mit.jverbnet.data.IVerbClass;
import edu.mit.jverbnet.index.VerbIndex;

public class MatchingRoles implements PARAMETERS_VALUES{
	
	private static final DecimalFormat df = new DecimalFormat("0.00");
	private WordNetQuery wnq = new WordNetQuery();

	public boolean RoleMatcher(String IT, String GT, boolean bool, int enrichment) throws Exception {
		String[] mv = {"The ", "can ", "could ", "may ", "might ", "shall ", "should ", "will ", "would ", "must ", 
		"and ", "for ", "of ", "or ", " to", " out", " the", "into ", "fully ", "immediately ", "\"", ","};
		Set <String> vnSynonyms = new LinkedHashSet<String>();
		Set <String> wnSynonyms = new LinkedHashSet<String>();
		Set <String> wnHypnyms = new LinkedHashSet<String>();
		String[] GTL = {};
		String[] ITL = {} ;
		for(int i=0;i<mv.length;i++) {
			GT = GT.replaceAll(mv[i], "");
			IT = IT.replaceAll(mv[i], "");
		}
		GT = GT.replaceAll("\\s+"," ");
		IT = IT.replaceAll("\\s+"," ");
		
		boolean flag1 = Overlap_JWDistance(IT, GT, bool);
		if(bool) {
			GTL = GT.split(" ");
			ITL = IT.split(" ");
			/*for(int i=0;i<GTL.length;i++)
				System.out.println("GTL[" + i + "]: " + GTL[i]);
			for(int i=0;i<ITL.length;i++)
				System.out.println("ITL[" + i + "]: " + ITL[i]);*/
			if(enrichment != 0) {
				vnSynonyms = Synonyms(GTL);	//Getting the synonyms from VerbNet
			}
			if(enrichment == 2) {
				//wnSynonyms = wnq.getSynonyms(GT, false);
				wnSynonyms = wnq.getSynonyms(GTL, false);	//Getting the synonyms from WordNet
				//wnSynonyms = wnq.getSynonyms(wnSynonyms, false);
			}
			else if(enrichment == 3) {
				//wnSynonyms = wnq.getSynonyms(GT, false);
				//wnHypnyms = wnq.getRelatedWords(GT, false);
				wnSynonyms = wnq.getSynonyms(GTL, false);	//Getting the synonyms from WordNet
				wnHypnyms = wnq.getRelatedWords(GTL, true);	//Getting the hypernyms and hyponyms from WordNet
				//wnSynonyms = wnq.getSynonyms(wnSynonyms, false);
				//wnHypnyms = wnq.getRelatedWords(wnSynonyms, true);
			}
			boolean flag2 = Relatedness(ITL, vnSynonyms, wnSynonyms, wnHypnyms);	//Calculating the relatedness in WordNet
			return (flag1 || flag2);
		}
		else
			return flag1;
	}
	
	private Set <String> Synonyms (String[] GTL) throws IOException {
		// look the verb up in verbnet
		File directory = new File(PATH_XML_VN);
		final String[] files = directory.list();
		Set <String> synonyms = new LinkedHashSet<String>();
		for(int i=0;i<GTL.length;i++) {
			synonyms.add(GTL[i]);
			boolean found = false;
			for (final String filename : files) {
				if (filename.contains(GTL[i])) {
					GTL[i] = filename.replace(".xml", "");
					found = true;
				}
			}
			if (found) {
				URL url = new URL("file", null, PATH_XML_VN);
				VerbIndex index = new VerbIndex(url);
				index.open();
				IVerbClass v = index.getRootVerb(GTL[i]);
				if (v != null) {
					List<IMember> members = v.getMembers();
					for (IMember m : members) {
						//System.out.println("Member: " + m.getName());
						synonyms.add(m.getName());
					}
				}
				index.close();
			}
		}
		//System.out.println("VerbNet synonyms: " + synonyms);
		return synonyms;
	}
	
	private boolean Overlap_JWDistance (String IT, String GT, boolean bool) {
		
		double temp = StringUtils.getJaroWinklerDistance(IT, GT);
			if(IT.isEmpty() && GT.equalsIgnoreCase("NA")) {
				//System.out.println("TN: there is no role to match");
				return true;
			}
			else if((GT.equalsIgnoreCase(IT)) || (GT.toLowerCase().contains(IT.toLowerCase()) && (!IT.equalsIgnoreCase("NA") && !IT.isBlank() && !IT.isEmpty())) || (IT.toLowerCase().contains(GT.toLowerCase()) && (!GT.equalsIgnoreCase("NA") && !GT.isBlank() && !GT.isEmpty()))) {
				//System.out.println("TP: the text for the role Main Action matches");
				return true;
			}
			else if(temp > THRESHOLD_REQ) {
				//System.out.println("TP: the text for the role matches");
				//System.out.println("\nIT: " + IT);
				//System.out.println("GT: " + GT);
				//System.out.println("Similarity Jaro-Winkler: " + temp + "\n");
				return true;
			}
			else if(!GT.equalsIgnoreCase(IT) && GT.equalsIgnoreCase("NA")) {
				//System.out.println("FP: the text for the role does not match");
				return false;
			}
			else if(!GT.equalsIgnoreCase(IT)) {
				//System.out.println("FN: the text for the role does not match");
				return false;
			}
			else {
				System.out.println("Error: This case shouln't exist");
				return false;
			}
			
	}
	
	private boolean Relatedness (String[] spdIT, Set <String> vnSynonyms, Set <String> wnSynonyms, Set <String> wnHypnyms) {
		//Check the relatedness in WordNet
		ILexicalDatabase db = new NictWordNet();
		WS4JConfiguration.getInstance().setMFS(true);
		RelatednessCalculator rc = new WuPalmer(db);	// WuPalmer, Resnik, Path, Lin, Lesk, LeacockChodorow, JiangConrath, HirstStOnge
		double maxScore = 0.0;
		int count = 0;
		
		//Calculating relatedness for the examples
		/*System.out.println("------------------------------");
		double score = rc.calcRelatednessOfWords("employ", "engage");
		System.out.println("The relatedness between employ and engage is: " + score);
		score = rc.calcRelatednessOfWords("hire", "engage");
		System.out.println("The relatedness between hire and engage is: " + score);
		score = rc.calcRelatednessOfWords("engage", "engage");
		System.out.println("The relatedness between engage and engage is: " + score);
		System.out.println("------------------------------\n");*/
				
		//-------------------------------------------------------------------------------
		// Relatedness with the GT
		//-------------------------------------------------------------------------------
		/*for(int i=0;i<spdGT.length;i++) {
			for(int j=0;j<spdIT.length;j++) {
				if(!spdIT[j].isBlank()) {
					if(rc.calcRelatednessOfWords(spdIT[j], spdGT[i])>1.0)
						maxScore = maxScore + 1.0;
						//maxScore = 1.0;
					else
						maxScore = maxScore + rc.calcRelatednessOfWords(spdIT[j], spdGT[i]);
						//maxScore = rc.calcRelatednessOfWords(spdIT[j], spdGT[i]);
					count++;
				}
			}
		}*/
		//-------------------------------------------------------------------------------
		// Relatedness with the synonyms from VerbNet
		//-------------------------------------------------------------------------------
		for(String syn : vnSynonyms) {
			for(int j=0;j<spdIT.length;j++) {
				if(rc.calcRelatednessOfWords(spdIT[j], syn)>1.0)
					maxScore = maxScore + 1.0;
					//maxScore = 1.0;
				else
					maxScore = maxScore + rc.calcRelatednessOfWords(spdIT[j], syn);
					//maxScore = rc.calcRelatednessOfWords(spdIT[j], syn);
				count++;
			}
		}
		//-------------------------------------------------------------------------------
		// Relatedness with the synonyms from WordNet
		//-------------------------------------------------------------------------------
		for(String syn : wnSynonyms) {
			for(int j=0;j<spdIT.length;j++) {
				if(rc.calcRelatednessOfWords(spdIT[j], syn)>1.0)
					maxScore = maxScore + 1.0;
					//maxScore = 1.0;
				else
					maxScore = maxScore + rc.calcRelatednessOfWords(spdIT[j], syn);
					//maxScore = rc.calcRelatednessOfWords(spdIT[j], syn);
				count++;
			}
		}
		//-------------------------------------------------------------------------------
		// Relatedness with the hypernyms and hyponyms from WordNet
		//-------------------------------------------------------------------------------
		for(String syn : wnHypnyms) {
			for(int j=0;j<spdIT.length;j++) {
				if(rc.calcRelatednessOfWords(spdIT[j], syn)>1.0)
					maxScore = maxScore + 1.0;
					//maxScore = 1.0;
				else
					maxScore = maxScore + rc.calcRelatednessOfWords(spdIT[j], syn);
					//maxScore = rc.calcRelatednessOfWords(spdIT[j], syn);
				count++;
			}
		}
		
		maxScore = maxScore/count;
		//System.out.println("The score is: " + maxScore);
		//System.out.println("------------------------------\n");
        if (maxScore > 0.8)
			return true;
		else
			return false;
	}
	
}
