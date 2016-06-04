package visitor.app.model;

/**
 * Created by user on 1/5/16.
 */
public class Visitor {

    int id;
    String name;    //--show in list
    String category;  //--Category corresponds to Company Name.
    String mobile;
    String email;
    String notes;
    String date;    //--show in list
    String prodint;     //--show in list

    public Visitor()
    {
        id = -1;
        name = "";
        category = "";
        mobile = "";
        email = "";
        notes = "";
        date = "";
        prodint = "";
    }
}
