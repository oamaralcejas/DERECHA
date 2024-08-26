
/* First created by JCasGen Tue Nov 23 11:33:54 CET 2021 */
package lu.svv.saa.linklaters.dpa.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Thu Feb 10 15:44:53 CET 2022
 * @generated */
public class ComplianceResults_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = ComplianceResults.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("lu.svv.saa.linklaters.dpa.type.ComplianceResults");
 
  /** @generated */
  final Feature casFeat_requirementID;
  /** @generated */
  final int     casFeatCode_requirementID;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getRequirementID(int addr) {
        if (featOkTst && casFeat_requirementID == null)
      jcas.throwFeatMissing("requirementID", "lu.svv.saa.linklaters.dpa.type.ComplianceResults");
    return ll_cas.ll_getStringValue(addr, casFeatCode_requirementID);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setRequirementID(int addr, String v) {
        if (featOkTst && casFeat_requirementID == null)
      jcas.throwFeatMissing("requirementID", "lu.svv.saa.linklaters.dpa.type.ComplianceResults");
    ll_cas.ll_setStringValue(addr, casFeatCode_requirementID, v);}
    
  
 
  /** @generated */
  final Feature casFeat_sentenceText;
  /** @generated */
  final int     casFeatCode_sentenceText;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getSentenceText(int addr) {
        if (featOkTst && casFeat_sentenceText == null)
      jcas.throwFeatMissing("sentenceText", "lu.svv.saa.linklaters.dpa.type.ComplianceResults");
    return ll_cas.ll_getStringValue(addr, casFeatCode_sentenceText);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSentenceText(int addr, String v) {
        if (featOkTst && casFeat_sentenceText == null)
      jcas.throwFeatMissing("sentenceText", "lu.svv.saa.linklaters.dpa.type.ComplianceResults");
    ll_cas.ll_setStringValue(addr, casFeatCode_sentenceText, v);}
    
  
 
  /** @generated */
  final Feature casFeat_missingRoles;
  /** @generated */
  final int     casFeatCode_missingRoles;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getMissingRoles(int addr) {
        if (featOkTst && casFeat_missingRoles == null)
      jcas.throwFeatMissing("missingRoles", "lu.svv.saa.linklaters.dpa.type.ComplianceResults");
    return ll_cas.ll_getStringValue(addr, casFeatCode_missingRoles);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setMissingRoles(int addr, String v) {
        if (featOkTst && casFeat_missingRoles == null)
      jcas.throwFeatMissing("missingRoles", "lu.svv.saa.linklaters.dpa.type.ComplianceResults");
    ll_cas.ll_setStringValue(addr, casFeatCode_missingRoles, v);}
    
  
 
  /** @generated */
  final Feature casFeat_matchingDegree;
  /** @generated */
  final int     casFeatCode_matchingDegree;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getMatchingDegree(int addr) {
        if (featOkTst && casFeat_matchingDegree == null)
      jcas.throwFeatMissing("matchingDegree", "lu.svv.saa.linklaters.dpa.type.ComplianceResults");
    return ll_cas.ll_getStringValue(addr, casFeatCode_matchingDegree);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setMatchingDegree(int addr, String v) {
        if (featOkTst && casFeat_matchingDegree == null)
      jcas.throwFeatMissing("matchingDegree", "lu.svv.saa.linklaters.dpa.type.ComplianceResults");
    ll_cas.ll_setStringValue(addr, casFeatCode_matchingDegree, v);}
    
  
 
  /** @generated */
  final Feature casFeat_sentenceID;
  /** @generated */
  final int     casFeatCode_sentenceID;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getSentenceID(int addr) {
        if (featOkTst && casFeat_sentenceID == null)
      jcas.throwFeatMissing("sentenceID", "lu.svv.saa.linklaters.dpa.type.ComplianceResults");
    return ll_cas.ll_getStringValue(addr, casFeatCode_sentenceID);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSentenceID(int addr, String v) {
        if (featOkTst && casFeat_sentenceID == null)
      jcas.throwFeatMissing("sentenceID", "lu.svv.saa.linklaters.dpa.type.ComplianceResults");
    ll_cas.ll_setStringValue(addr, casFeatCode_sentenceID, v);}
    
  
 
  /** @generated */
  final Feature casFeat_confidenceDegree;
  /** @generated */
  final int     casFeatCode_confidenceDegree;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public double getConfidenceDegree(int addr) {
        if (featOkTst && casFeat_confidenceDegree == null)
      jcas.throwFeatMissing("confidenceDegree", "lu.svv.saa.linklaters.dpa.type.ComplianceResults");
    return ll_cas.ll_getDoubleValue(addr, casFeatCode_confidenceDegree);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setConfidenceDegree(int addr, double v) {
        if (featOkTst && casFeat_confidenceDegree == null)
      jcas.throwFeatMissing("confidenceDegree", "lu.svv.saa.linklaters.dpa.type.ComplianceResults");
    ll_cas.ll_setDoubleValue(addr, casFeatCode_confidenceDegree, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public ComplianceResults_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_requirementID = jcas.getRequiredFeatureDE(casType, "requirementID", "uima.cas.String", featOkTst);
    casFeatCode_requirementID  = (null == casFeat_requirementID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_requirementID).getCode();

 
    casFeat_sentenceText = jcas.getRequiredFeatureDE(casType, "sentenceText", "uima.cas.String", featOkTst);
    casFeatCode_sentenceText  = (null == casFeat_sentenceText) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_sentenceText).getCode();

 
    casFeat_missingRoles = jcas.getRequiredFeatureDE(casType, "missingRoles", "uima.cas.String", featOkTst);
    casFeatCode_missingRoles  = (null == casFeat_missingRoles) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_missingRoles).getCode();

 
    casFeat_matchingDegree = jcas.getRequiredFeatureDE(casType, "matchingDegree", "uima.cas.String", featOkTst);
    casFeatCode_matchingDegree  = (null == casFeat_matchingDegree) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_matchingDegree).getCode();

 
    casFeat_sentenceID = jcas.getRequiredFeatureDE(casType, "sentenceID", "uima.cas.String", featOkTst);
    casFeatCode_sentenceID  = (null == casFeat_sentenceID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_sentenceID).getCode();

 
    casFeat_confidenceDegree = jcas.getRequiredFeatureDE(casType, "confidenceDegree", "uima.cas.Double", featOkTst);
    casFeatCode_confidenceDegree  = (null == casFeat_confidenceDegree) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_confidenceDegree).getCode();

  }
}



    