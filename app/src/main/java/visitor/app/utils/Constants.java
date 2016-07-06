package visitor.app.utils;


import java.util.ArrayList;

import visitor.app.model.Visitor;

public class Constants {

    //Database and cache.
    public static String dbname = "visitordb";
    public static String pref_prod = "prodInt";
    public static String pref_docs = "docsAtt";
    public static String SMS_ACTIVE = "sms_active";

    //Selected Visitor item.
    public static Visitor selectedVisitor = null;

    //Flags and indicators
    public static boolean CHANGE = false;

    //List docs attachments
    public static ArrayList<String> attachDocs = new ArrayList<String>();

    //SMS pattern
    public static final String ACTIVATION_VALIDATION_MSG = "PRO_CUSTOMER_OFFER_VALID_8888";
    public static final String ACTIVATION_VALICATION_NUMBER = "+919673465221";


}
