

/* First created by JCasGen Fri Oct 01 10:22:03 CEST 2021 */
package lu.svv.saa.linklaters.dpa.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Fri Oct 01 10:22:03 CEST 2021
 * XML source: /Users/sallam.abualhaija/Documents/Workspace-Eclipse/lu.svv.saa.linklaters.dpa/src/main/resources/desc/type/LegalSemanticRole.xml
 * @generated */
public class LegalSemanticRole extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(LegalSemanticRole.class);
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
  protected LegalSemanticRole() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public LegalSemanticRole(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public LegalSemanticRole(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public LegalSemanticRole(JCas jcas, int begin, int end) {
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
  //* Feature: semanticRole

  /** getter for semanticRole - gets the semantic role of some text chunk
   * @generated
   * @return value of the feature 
   */
  public String getSemanticRole() {
    if (LegalSemanticRole_Type.featOkTst && ((LegalSemanticRole_Type)jcasType).casFeat_semanticRole == null)
      jcasType.jcas.throwFeatMissing("semanticRole", "lu.svv.saa.linklaters.dpa.type.LegalSemanticRole");
    return jcasType.ll_cas.ll_getStringValue(addr, ((LegalSemanticRole_Type)jcasType).casFeatCode_semanticRole);}
    
  /** setter for semanticRole - sets the semantic role of some text chunk 
   * @generated
   * @param v value to set into the feature 
   */
  public void setSemanticRole(String v) {
    if (LegalSemanticRole_Type.featOkTst && ((LegalSemanticRole_Type)jcasType).casFeat_semanticRole == null)
      jcasType.jcas.throwFeatMissing("semanticRole", "lu.svv.saa.linklaters.dpa.type.LegalSemanticRole");
    jcasType.ll_cas.ll_setStringValue(addr, ((LegalSemanticRole_Type)jcasType).casFeatCode_semanticRole, v);}    
  }

    