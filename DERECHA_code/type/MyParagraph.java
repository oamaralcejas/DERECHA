

/* First created by JCasGen Tue Nov 09 11:02:16 CET 2021 */
package lu.svv.saa.linklaters.dpa.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Tue Nov 09 11:03:44 CET 2021
 * XML source: /Users/orlando.amaralcejas/eclipse-workspace/LinkLater/lu.svv.saa.linklaters.dpa/src/main/resources/desc/type/MyParagraph.xml
 * @generated */
public class MyParagraph extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(MyParagraph.class);
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
  protected MyParagraph() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public MyParagraph(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public MyParagraph(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public MyParagraph(JCas jcas, int begin, int end) {
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

  /** getter for ID - gets the ID of the paragraph
   * @generated
   * @return value of the feature 
   */
  public String getID() {
    if (MyParagraph_Type.featOkTst && ((MyParagraph_Type)jcasType).casFeat_ID == null)
      jcasType.jcas.throwFeatMissing("ID", "lu.svv.saa.linklaters.dpa.type.MyParagraph");
    return jcasType.ll_cas.ll_getStringValue(addr, ((MyParagraph_Type)jcasType).casFeatCode_ID);}
    
  /** setter for ID - sets the ID of the paragraph 
   * @generated
   * @param v value to set into the feature 
   */
  public void setID(String v) {
    if (MyParagraph_Type.featOkTst && ((MyParagraph_Type)jcasType).casFeat_ID == null)
      jcasType.jcas.throwFeatMissing("ID", "lu.svv.saa.linklaters.dpa.type.MyParagraph");
    jcasType.ll_cas.ll_setStringValue(addr, ((MyParagraph_Type)jcasType).casFeatCode_ID, v);}    
   
    
  //*--------------*
  //* Feature: Text

  /** getter for Text - gets the text of the paragraph
   * @generated
   * @return value of the feature 
   */
  public String getText() {
    if (MyParagraph_Type.featOkTst && ((MyParagraph_Type)jcasType).casFeat_Text == null)
      jcasType.jcas.throwFeatMissing("Text", "lu.svv.saa.linklaters.dpa.type.MyParagraph");
    return jcasType.ll_cas.ll_getStringValue(addr, ((MyParagraph_Type)jcasType).casFeatCode_Text);}
    
  /** setter for Text - sets the text of the paragraph 
   * @generated
   * @param v value to set into the feature 
   */
  public void setText(String v) {
    if (MyParagraph_Type.featOkTst && ((MyParagraph_Type)jcasType).casFeat_Text == null)
      jcasType.jcas.throwFeatMissing("Text", "lu.svv.saa.linklaters.dpa.type.MyParagraph");
    jcasType.ll_cas.ll_setStringValue(addr, ((MyParagraph_Type)jcasType).casFeatCode_Text, v);}    
  }

    