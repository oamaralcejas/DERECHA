package lu.svv.saa.linklaters.dpa.utils;

public interface PARAMETERS_VALUES {
	// Abelia, Akebie, Asiminier, Aucuba, Bech, Berdorf, Bertrange, Bettembourg, Bettendorf, Bissen, Biwer, Blageon, Brugmansia,
	// Calocedre, Consdorf, Contern, Diekirch, Differdange, Dippach, Ell, Feulen, Goesdorf, Habscht, Heffimgen, Helperknapp, Idesia,
	// Kalopanax, Kayl, Kehlen, Larochette, Linklaters 19, Linklaters 20, Linklaters 25, Linklaters 26, Linklaters 28, Linklaters 29,
	// Linklaters 31, Linklaters 32, Linklaters 34, Linklaters 35, Linklaters 37, Linklaters 38, Linklaters 39, Linklaters 40,
	// Linklaters 41, Linklaters 43, Linklaters 44, Linklaters 45, Linklaters 46, Linklaters 47, Linklaters 49, Linklaters 50,
	// Linklaters 51, Linklaters 52, Linklaters 53, Linklaters 56, Linklaters 57, Linklaters 58, Linklaters 59, Linklaters 60,
	// Linklaters 61, Linklaters 62, Linklaters 63, Linklaters 64, Linklaters 65, Linklaters 66, Linklaters 68, Linklaters 69,
	// Linklaters 71, Linklaters 72, Linklaters 73, Linklaters 76, Linklaters 77, Linklaters 78, Linklaters 79, Linklaters 80,
	// Linklaters 81, Linklaters 82, Linklaters 83, Linklaters 84, Lintgen, Lorentzweiler, Mehonia, Mersch, Mondercange, Niederanven,
	// Nommern, Online 1, Online 10, Online 100, Online 102, Online 108, Online 11, Online 112, Online 114, Online 116, Online 117,
	// Online 118, Online 119, Online 12, Online 120, Online 121, Online 122, Online 124, Online 126, Online 127, Online 13, Online 131,
	// Online 132, Online 133, Online 134, Online 135, Online 136, Online 137, Online 138, Online 139, Online 140, Online 142, Online 143,
	// Online 15, Online 16, Online 17, Online 18, Online 19, Online 20, Online 24, Online 25, Online 27, Online 28, Online 29, Online 3,
	// Online 30, Online 32, Online 34, Online 35, Online 37, Online 39, Online 4, Online 5, Online 52, Online 54, Online 55, Online 57,
	// Online 58, Online 6, Online 61, Online 66, Online 67, Online 68, Online 7, Online 70, Online 71, Online 73, Online 74, Online 75,
	// Online 76, Online 77, Online 78, Online 79, Online 8, Online 80, Online 85, Online 87, Online 90, Online 92, Online 94, Online 97,
	// Parc Hosingen, Preizerdaul, Putscheid, Ramboutan, Reisdorf, Roeser, Rumelange, Saeul, Sanem, Schifflange, Strassen, Tandel,
	// Useldange, Vichten, Wahl, Waldbredimus, Wiltz, Winseler, Wormeldange, Zelkova
	static String PATH_INPUT = "src/main/resources/input/";
	static String PATH_OUTPUT = "src/main/resources/output/";
	static String PATH_EXCEL = "src/main/resources/xlsx/";
	static String PATH_CSV = "src/main/resources/csv/";
	static String PATH_XML_VN = "src/main/resources/verbnet/";
	static final String MAIN_ACTOR = "Main Actor";
	static final String MAIN_ACTION = "Main Action";
	static final String BENEFICIARY_TARGET = "Beneficiary/Target";
	static final String OBJECT = "Object";
	static final String SECONDARY_ACTOR = "Secondary Actor";
	static final String SECONDARY_ACTION = "Secondary Action";
	static final String CONDITION = "Condition";
	static final String CONSTRAINT = "Constraint";
	static final String TEMPORAL_CHARACTERISTIC = "Temporal Characteristic";
	static final String REASON = "Reason";
	static final String EVENT = "Event";
	static final String REFERENCE = "Reference";
	static final String ATTRIBUTE = "Attribute";
	static final String LOCATION = "Location";
	final static int SENTENCE_ID_INDEX = 0;
	final static int MAIN_ACTOR_INDEX = 0;
	final static int MAIN_ACTION_INDEX = 1;
	final static int BENEFICIARY_TARGET_INDEX = 2;
	final static int OBJECT_INDEX = 3;
	final static int SECONDARY_ACTOR_INDEX = 4;
	final static int SECONDARY_ACTION_INDEX = 5;
	final static int CONDITION_INDEX = 6;
	final static int CONSTRAINT_INDEX = 7;
	final static int TEMPORAL_CHARACTERISTIC_INDEX = 8;
	final static int REASON_INDEX = 9;
	final static int EVENT_INDEX = 10;
	final static int REFERENCE_INDEX = 11;
	final static int ATTRIBUTE_INDEX = 12;
	final static int LOCATION_INDEX = 13;
	final static int REQUIREMENT_1_INDEX = 1;
	final static int MATCHING_DEGREE_1_INDEX = 2;
	final static int REQUIREMENT_2_INDEX = 3;
	final static int MATCHING_DEGREE_2_INDEX = 4;
	final static int REQUIREMENT_3_INDEX = 5;
	final static int MATCHING_DEGREE_3_INDEX = 6;
	final static double THRESHOLD_LSR = 0.5;
	final static double THRESHOLD_REQ = 0.68;
	final static double THRESHOLD_MAN = 0.92;
	final static double THRESHOLD_SEN = 0.7;
}
