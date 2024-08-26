

/* First created by JCasGen Mon Oct 18 11:52:06 CEST 2021 */
package lu.svv.saa.linklaters.dpa.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Mon Oct 18 11:52:06 CEST 2021
 * XML source: /Users/orlando.amaralcejas/eclipse-workspace/LinkLater/lu.svv.saa.linklaters.dpa/src/main/resources/desc/type/MySentence.xml
 * @generated */
public class MySentence extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(MySentence.class);
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
  protected MySentence() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public MySentence(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public MySentence(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public MySentence(JCas jcas, int begin, int end) {
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
  //* Feature: ID

  /** getter for ID - gets the ID of the sentence
   * @generated
   * @return value of the feature 
   */
  public String getID() {
    if (MySentence_Type.featOkTst && ((MySentence_Type)jcasType).casFeat_ID == null)
      jcasType.jcas.throwFeatMissing("ID", "lu.svv.saa.linklaters.dpa.type.MySentence");
    return jcasType.ll_cas.ll_getStringValue(addr, ((MySentence_Type)jcasType).casFeatCode_ID);}
    
  /** setter for ID - sets the ID of the sentence 
   * @generated
   * @param v value to set into the feature 
   */
  public void setID(String v) {
    if (MySentence_Type.featOkTst && ((MySentence_Type)jcasType).casFeat_ID == null)
      jcasType.jcas.throwFeatMissing("ID", "lu.svv.saa.linklaters.dpa.type.MySentence");
    jcasType.ll_cas.ll_setStringValue(addr, ((MySentence_Type)jcasType).casFeatCode_ID, v);}    
   
    
  //*--------------*
  //* Feature: Text

  /** getter for Text - gets the text of the sentence
   * @generated
   * @return value of the feature 
   */
  public String getText() {
    if (MySentence_Type.featOkTst && ((MySentence_Type)jcasType).casFeat_Text == null)
      jcasType.jcas.throwFeatMissing("Text", "lu.svv.saa.linklaters.dpa.type.MySentence");
    return jcasType.ll_cas.ll_getStringValue(addr, ((MySentence_Type)jcasType).casFeatCode_Text);}
    
  /** setter for Text - sets the text of the sentence 
   * @generated
   * @param v value to set into the feature 
   */
  public void setText(String v) {
    if (MySentence_Type.featOkTst && ((MySentence_Type)jcasType).casFeat_Text == null)
      jcasType.jcas.throwFeatMissing("Text", "lu.svv.saa.linklaters.dpa.type.MySentence");
    jcasType.ll_cas.ll_setStringValue(addr, ((MySentence_Type)jcasType).casFeatCode_Text, v);}    
  }

    