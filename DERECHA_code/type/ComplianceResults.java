

/* First created by JCasGen Tue Nov 23 11:33:54 CET 2021 */
package lu.svv.saa.linklaters.dpa.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Thu Feb 10 15:44:53 CET 2022
 * XML source: /Users/orlando.amaralcejas/eclipse-workspace/LinkLater/lu.svv.saa.linklaters.dpa_solution/src/main/resources/desc/type/ComplianceResults.xml
 * @generated */
public class ComplianceResults extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(ComplianceResults.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected ComplianceResults() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public ComplianceResults(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public ComplianceResults(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public ComplianceResults(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: requirementID

  /** getter for requirementID - gets the ID of the requirement
   * @generated
   * @return value of the feature 
   */
  public String getRequirementID() {
    if (ComplianceResults_Type.featOkTst && ((ComplianceResults_Type)jcasType).casFeat_requirementID == null)
      jcasType.jcas.throwFeatMissing("requirementID", "lu.svv.saa.linklaters.dpa.type.ComplianceResults");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ComplianceResults_Type)jcasType).casFeatCode_requirementID);}
    
  /** setter for requirementID - sets the ID of the requirement 
   * @generated
   * @param v value to set into the feature 
   */
  public void setRequirementID(String v) {
    if (ComplianceResults_Type.featOkTst && ((ComplianceResults_Type)jcasType).casFeat_requirementID == null)
      jcasType.jcas.throwFeatMissing("requirementID", "lu.svv.saa.linklaters.dpa.type.ComplianceResults");
    jcasType.ll_cas.ll_setStringValue(addr, ((ComplianceResults_Type)jcasType).casFeatCode_requirementID, v);}    
   
    
  //*--------------*
  //* Feature: sentenceText

  /** getter for sentenceText - gets the text of the requirement
   * @generated
   * @return value of the feature 
   */
  public String getSentenceText() {
    if (ComplianceResults_Type.featOkTst && ((ComplianceResults_Type)jcasType).casFeat_sentenceText == null)
      jcasType.jcas.throwFeatMissing("sentenceText", "lu.svv.saa.linklaters.dpa.type.ComplianceResults");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ComplianceResults_Type)jcasType).casFeatCode_sentenceText);}
    
  /** setter for sentenceText - sets the text of the requirement 
   * @generated
   * @param v value to set into the feature 
   */
  public void setSentenceText(String v) {
    if (ComplianceResults_Type.featOkTst && ((ComplianceResults_Type)jcasType).casFeat_sentenceText == null)
      jcasType.jcas.throwFeatMissing("sentenceText", "lu.svv.saa.linklaters.dpa.type.ComplianceResults");
    jcasType.ll_cas.ll_setStringValue(addr, ((ComplianceResults_Type)jcasType).casFeatCode_sentenceText, v);}    
   
    
  //*--------------*
  //* Feature: missingRoles

  /** getter for missingRoles - gets roles not found in the sentence
   * @generated
   * @return value of the feature 
   */
  public String getMissingRoles() {
    if (ComplianceResults_Type.featOkTst && ((ComplianceResults_Type)jcasType).casFeat_missingRoles == null)
      jcasType.jcas.throwFeatMissing("missingRoles", "lu.svv.saa.linklaters.dpa.type.ComplianceResults");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ComplianceResults_Type)jcasType).casFeatCode_missingRoles);}
    
  /** setter for missingRoles - sets roles not found in the sentence 
   * @generated
   * @param v value to set into the feature 
   */
  public void setMissingRoles(String v) {
    if (ComplianceResults_Type.featOkTst && ((ComplianceResults_Type)jcasType).casFeat_missingRoles == null)
      jcasType.jcas.throwFeatMissing("missingRoles", "lu.svv.saa.linklaters.dpa.type.ComplianceResults");
    jcasType.ll_cas.ll_setStringValue(addr, ((ComplianceResults_Type)jcasType).casFeatCode_missingRoles, v);}    
   
    
  //*--------------*
  //* Feature: matchingDegree

  /** getter for matchingDegree - gets the degree of matching (full or partial)
   * @generated
   * @return value of the feature 
   */
  public String getMatchingDegree() {
    if (ComplianceResults_Type.featOkTst && ((ComplianceResults_Type)jcasType).casFeat_matchingDegree == null)
      jcasType.jcas.throwFeatMissing("matchingDegree", "lu.svv.saa.linklaters.dpa.type.ComplianceResults");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ComplianceResults_Type)jcasType).casFeatCode_matchingDegree);}
    
  /** setter for matchingDegree - sets the degree of matching (full or partial) 
   * @generated
   * @param v value to set into the feature 
   */
  public void setMatchingDegree(String v) {
    if (ComplianceResults_Type.featOkTst && ((ComplianceResults_Type)jcasType).casFeat_matchingDegree == null)
      jcasType.jcas.throwFeatMissing("matchingDegree", "lu.svv.saa.linklaters.dpa.type.ComplianceResults");
    jcasType.ll_cas.ll_setStringValue(addr, ((ComplianceResults_Type)jcasType).casFeatCode_matchingDegree, v);}    
   
    
  //*--------------*
  //* Feature: sentenceID

  /** getter for sentenceID - gets 
   * @generated
   * @return value of the feature 
   */
  public String getSentenceID() {
    if (ComplianceResults_Type.featOkTst && ((ComplianceResults_Type)jcasType).casFeat_sentenceID == null)
      jcasType.jcas.throwFeatMissing("sentenceID", "lu.svv.saa.linklaters.dpa.type.ComplianceResults");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ComplianceResults_Type)jcasType).casFeatCode_sentenceID);}
    
  /** setter for sentenceID - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSentenceID(String v) {
    if (ComplianceResults_Type.featOkTst && ((ComplianceResults_Type)jcasType).casFeat_sentenceID == null)
      jcasType.jcas.throwFeatMissing("sentenceID", "lu.svv.saa.linklaters.dpa.type.ComplianceResults");
    jcasType.ll_cas.ll_setStringValue(addr, ((ComplianceResults_Type)jcasType).casFeatCode_sentenceID, v);}    
   
    
  //*--------------*
  //* Feature: confidenceDegree

  /** getter for confidenceDegree - gets Contains the Confidence Degree of the matching
   * @generated
   * @return value of the feature 
   */
  public double getConfidenceDegree() {
    if (ComplianceResults_Type.featOkTst && ((ComplianceResults_Type)jcasType).casFeat_confidenceDegree == null)
      jcasType.jcas.throwFeatMissing("confidenceDegree", "lu.svv.saa.linklaters.dpa.type.ComplianceResults");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((ComplianceResults_Type)jcasType).casFeatCode_confidenceDegree);}
    
  /** setter for confidenceDegree - sets Contains the Confidence Degree of the matching 
   * @generated
   * @param v value to set into the feature 
   */
  public void setConfidenceDegree(double v) {
    if (ComplianceResults_Type.featOkTst && ((ComplianceResults_Type)jcasType).casFeat_confidenceDegree == null)
      jcasType.jcas.throwFeatMissing("confidenceDegree", "lu.svv.saa.linklaters.dpa.type.ComplianceResults");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((ComplianceResults_Type)jcasType).casFeatCode_confidenceDegree, v);}    
  }

    