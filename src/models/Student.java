package models;

/**
 * Student model - represents a student in the system
 */
public class Student {
    private String id;
    private String name;
    private String rollNumber;
    private String className; // "10", "11", "12"
    private String section;   // "A", "B"
    private String gender;    // "male", "female", "other"
    private String email;
    private String contact;
    private String parentName;
    private String parentContact;
    private String address;
    private String dateOfBirth;
    private boolean isActive;
    private String createdAt;

    public Student() {
        this.isActive = true;
        this.section = "A";
    }

    public Student(String id, String name, String rollNumber, String className,
                   String section, String gender, String email, String contact) {
        this.id = id;
        this.name = name;
        this.rollNumber = rollNumber;
        this.className = className;
        this.section = section;
        this.gender = gender;
        this.email = email;
        this.contact = contact;
        this.isActive = true;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getParentName() { return parentName; }
    public void setParentName(String parentName) { this.parentName = parentName; }

    public String getParentContact() { return parentContact; }
    public void setParentContact(String parentContact) { this.parentContact = parentContact; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    // Helper methods
    public String getInitials() {
        if (name == null || name.isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, 1).toUpperCase();
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }

    public String getClassSection() {
        return "Class " + className + "-" + section;
    }

    public String getGenderColor() {
        if ("female".equalsIgnoreCase(gender)) return "#ff8aaa";
        if ("other".equalsIgnoreCase(gender)) return "#b48aff";
        return "#60aaff";
    }

    public String getFirstName() {
        if (name == null || name.isEmpty()) return "";
        String[] parts = name.split(" ");
        return parts[0];
    }
    @Override
    public String toString() {
        return name + " (" + rollNumber + ")";
    }
}
