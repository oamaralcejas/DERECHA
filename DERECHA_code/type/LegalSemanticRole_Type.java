
/* First created by JCasGen Fri Oct 01 10:22:03 CEST 2021 */
package lu.svv.saa.linklaters.dpa.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Fri Oct 01 10:22:03 CEST 2021
 * @generated */
public class LegalSemanticRole_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = LegalSemanticRole.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("lu.svv.saa.linklaters.dpa.type.LegalSemanticRole");
 
  /** @generated */
  final Feature casFeat_semanticRole;
  /** @generated */
  final int     casFeatCode_semanticRole;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getSemanticRole(int addr) {
        if (featOkTst && casFeat_semanticRole == null)
      jcas.throwFeatMissing("semanticRole", "lu.svv.saa.linklaters.dpa.type.LegalSemanticRole");
    return ll_cas.ll_getStringValue(addr, casFeatCode_semanticRole);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSemanticRole(int addr, String v) {
        if (featOkTst && casFeat_semanticRole == null)
      jcas.throwFeatMissing("semanticRole", "lu.svv.saa.linklaters.dpa.type.LegalSemanticRole");
    ll_cas.ll_setStringValue(addr, casFeatCode_semanticRole, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public LegalSemanticRole_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_semanticRole = jcas.getRequiredFeatureDE(casType, "semanticRole", "uima.cas.String", featOkTst);
    casFeatCode_semanticRole  = (null == casFeat_semanticRole) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_semanticRole).getCode();

  }
}



    