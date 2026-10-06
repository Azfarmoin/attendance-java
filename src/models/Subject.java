package models;

import java.util.List;

/**
 * Subject model
 */
public class Subject {
    private String id;
    private String name;
    private String code;
    private List<String> classes;
    private int creditHours;

    public Subject() {}
    public Subject(String id, String name, String code, int creditHours) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.creditHours = creditHours;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public List<String> getClasses() { return classes; }
    public void setClasses(List<String> classes) { this.classes = classes; }
    public int getCreditHours() { return creditHours; }
    public void setCreditHours(int creditHours) { this.creditHours = creditHours; }

    @Override
    public String toString() { return name; }
}
