package models;

/**
 * User model - represents admin, teacher, or student users
 */
public class User {
    private String id;
    private String name;
    private String email;
    private String password;
    private String role; // "admin", "teacher", "student"
    private String avatar; // initials

    public User() {}

    public User(String id, String name, String email, String password, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.avatar = name.substring(0, 1).toUpperCase();
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
        if (name != null && !name.isEmpty()) {
            this.avatar = name.substring(0, 1).toUpperCase();
        }
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getFirstName() {
        if (name == null || name.isEmpty()) return "";
        String[] parts = name.split(" ");
        return parts[0];
    }

    public String getRoleDisplay() {
        if (role == null) return "User";
        return role.substring(0, 1).toUpperCase() + role.substring(1);
    }

    @Override
    public String toString() {
        return name + " (" + role + ")";
    }
}
