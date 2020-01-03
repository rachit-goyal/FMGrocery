package in.aaaos.fmgrocery;

import java.util.ArrayList;

public class Variations {
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }

    ArrayList<String> options;
}
