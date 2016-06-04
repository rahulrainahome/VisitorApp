package visitor.app.model;

/**
 * Created by user on 1/5/16.
 */
public class Visitor {

    public int id;
    public String name;    //--show in list
    public String category;  //--Category corresponds to Company Name.
    public String mobile;
    public String email;
    public String notes;
    public String date;    //--show in list
    public String prodint;     //--show in list

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
