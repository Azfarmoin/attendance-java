package components;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import models.Student;
import utils.UIUtils;

/**
 * StudentFormDialog - Modal dialog for adding/editing students
 */
public class StudentFormDialog {
    private Student student;
    private Student result;
    private Stage stage;

    public StudentFormDialog(Student student) {
        this.student = student;
    }

    public Student showAndWait() {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(student == null ? "Add New Student" : "Edit Student");
        stage.setResizable(false);

        VBox root = new VBox(20);
        root.setStyle("-fx-background-color: #0e0e1a; -fx-padding: 28px;");
        root.setPrefWidth(540);

        // Header
        Label titleLabel = new Label(student == null ? "➕  Add New Student" : "✏️  Edit Student");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_PRIMARY + ";");
        Label subLabel = new Label(student == null ? "Fill in the details to enroll a new student" : "Update student information");
        subLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");

        VBox header = new VBox(4);
        header.getChildren().addAll(titleLabel, subLabel);

        // Form fields
        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(14);

        TextField nameField    = UIUtils.input("Full Name *");
        TextField rollField    = UIUtils.input("Roll Number *  (e.g. 10A-001)");
        TextField emailField   = UIUtils.input("Email Address");
        TextField contactField = UIUtils.input("Phone Number");
        TextField parentField  = UIUtils.input("Parent Name");
        TextField parentPhField= UIUtils.input("Parent Phone");
        TextField dobField     = UIUtils.input("Date of Birth  (YYYY-MM-DD)");
        TextField addressField = UIUtils.input("Address");

        ComboBox<String> classCombo   = UIUtils.comboBox("10", "11", "12", "9", "8");
        ComboBox<String> sectionCombo = UIUtils.comboBox("A", "B", "C", "D");
        ComboBox<String> genderCombo  = UIUtils.comboBox("male", "female", "other");

        classCombo.setPromptText("Class *");
        sectionCombo.setPromptText("Section");
        genderCombo.setPromptText("Gender");
        classCombo.setPrefWidth(Double.MAX_VALUE);
        sectionCombo.setPrefWidth(Double.MAX_VALUE);
        genderCombo.setPrefWidth(Double.MAX_VALUE);

        // Pre-fill if editing
        if (student != null) {
            nameField.setText(orEmpty(student.getName()));
            rollField.setText(orEmpty(student.getRollNumber()));
            emailField.setText(orEmpty(student.getEmail()));
            contactField.setText(orEmpty(student.getContact()));
            parentField.setText(orEmpty(student.getParentName()));
            parentPhField.setText(orEmpty(student.getParentContact()));
            dobField.setText(orEmpty(student.getDateOfBirth()));
            addressField.setText(orEmpty(student.getAddress()));
            if (student.getClassName() != null)  classCombo.setValue(student.getClassName());
            if (student.getSection() != null)    sectionCombo.setValue(student.getSection());
            if (student.getGender() != null)     genderCombo.setValue(student.getGender());
        }

        // Layout
        addFormRow(grid, 0, "Full Name *",    nameField,    "Roll Number *", rollField);
        addFormRow(grid, 1, "Class *",        classCombo,   "Section",       sectionCombo);
        addFormRow(grid, 2, "Gender",         genderCombo,  "Date of Birth", dobField);
        addFormRow(grid, 3, "Email",          emailField,   "Contact",       contactField);
        addFormRow(grid, 4, "Parent Name",    parentField,  "Parent Phone",  parentPhField);
        addFormRow(grid, 5, "Address",        addressField, null,            null);

        // Validation label
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + UIUtils.ACCENT_RED + ";");
        errorLabel.setVisible(false);

        // Buttons
        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = UIUtils.ghostBtn("Cancel");
        cancelBtn.setOnAction(e -> stage.close());

        Button saveBtn = UIUtils.primaryBtn(student == null ? "  Add Student  " : "  Save Changes  ", UIUtils.ACCENT_GREEN);
        saveBtn.setOnAction(e -> {
            // Validate
            if (nameField.getText().trim().isEmpty()) {
                errorLabel.setText("Name is required");
                errorLabel.setVisible(true);
                return;
            }
            if (rollField.getText().trim().isEmpty()) {
                errorLabel.setText("Roll number is required");
                errorLabel.setVisible(true);
                return;
            }
            if (classCombo.getValue() == null) {
                errorLabel.setText("Class is required");
                errorLabel.setVisible(true);
                return;
            }

            result = student != null ? student : new Student();
            result.setName(nameField.getText().trim());
            result.setRollNumber(rollField.getText().trim().toUpperCase());
            result.setClassName(classCombo.getValue());
            result.setSection(sectionCombo.getValue() != null ? sectionCombo.getValue() : "A");
            result.setGender(genderCombo.getValue() != null ? genderCombo.getValue() : "male");
            result.setEmail(emailField.getText().trim());
            result.setContact(contactField.getText().trim());
            result.setParentName(parentField.getText().trim());
            result.setParentContact(parentPhField.getText().trim());
            result.setDateOfBirth(dobField.getText().trim());
            result.setAddress(addressField.getText().trim());
            result.setActive(true);

            stage.close();
        });

        buttons.getChildren().addAll(cancelBtn, saveBtn);

        root.getChildren().addAll(header, UIUtils.divider(), grid, errorLabel, UIUtils.divider(), buttons);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/styles/dark.css").toExternalForm());
        stage.setScene(scene);
        stage.showAndWait();
        return result;
    }

    private void addFormRow(GridPane grid, int row, String label1, javafx.scene.Node field1, String label2, javafx.scene.Node field2) {
        VBox col1 = new VBox(5);
        Label lbl1 = new Label(label1);
        lbl1.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_MUTED + "; -fx-letter-spacing: 0.8px;");
        if (field1 instanceof TextField tf) tf.setPrefWidth(Double.MAX_VALUE);
        if (field1 instanceof ComboBox<?>) ((ComboBox<?>)field1).setMaxWidth(Double.MAX_VALUE);
        col1.getChildren().addAll(lbl1, field1);
        GridPane.setHgrow(col1, Priority.ALWAYS);
        grid.add(col1, 0, row);

        if (field2 != null && label2 != null) {
            VBox col2 = new VBox(5);
            Label lbl2 = new Label(label2);
            lbl2.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_MUTED + "; -fx-letter-spacing: 0.8px;");
            if (field2 instanceof TextField tf) tf.setPrefWidth(Double.MAX_VALUE);
            if (field2 instanceof ComboBox<?>) ((ComboBox<?>)field2).setMaxWidth(Double.MAX_VALUE);
            col2.getChildren().addAll(lbl2, field2);
            GridPane.setHgrow(col2, Priority.ALWAYS);
            grid.add(col2, 1, row);
        } else if (field1 != null) {
            GridPane.setColumnSpan(col1, 2);
        }
    }

    private String orEmpty(String s) { return s != null ? s : ""; }
}
