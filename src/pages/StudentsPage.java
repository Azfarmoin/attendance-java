package pages;

import components.StudentFormDialog;
import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.util.Duration;
import models.Student;
import services.DataService;
import utils.UIUtils;

import java.util.List;

/**
 * StudentsPage - Manage students with search, filter, add, edit, delete
 */
public class StudentsPage {
    private VBox root;
    private DataService ds = DataService.getInstance();
    private VBox tableBody;
    private TextField searchField;
    private ComboBox<String> classFilter;
    private ComboBox<String> sectionFilter;
    private Label countLabel;
    private StackPane contentPane;

    public StudentsPage() {
        root = new VBox(20);
        root.setStyle("-fx-padding: 24px; -fx-background-color: " + UIUtils.BG_DARK + ";");
        buildUI();
        UIUtils.fadeIn(root);
    }

    private void buildUI() {
        root.getChildren().add(buildHeader());
        root.getChildren().add(buildFilters());

        contentPane = new StackPane();
        tableBody = new VBox(0);
        ScrollPane scroll = new ScrollPane(tableBody);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        VBox tableCard = new VBox(0);
        tableCard.setStyle(
            "-fx-background-color: " + UIUtils.BG_CARD + ";" +
            "-fx-border-color: rgba(255,255,255,0.06);" +
            "-fx-border-radius: 18px;" +
            "-fx-background-radius: 18px;"
        );
        tableCard.getChildren().addAll(buildTableHeader(), scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        contentPane.getChildren().add(tableCard);
        VBox.setVgrow(contentPane, Priority.ALWAYS);
        root.getChildren().add(contentPane);

        loadStudents();
    }

    private HBox buildHeader() {
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox left = new VBox(4);
        Label title = UIUtils.heading("👤  Students");
        countLabel = UIUtils.label("Loading...", UIUtils.TEXT_MUTED, 13);
        left.getChildren().addAll(title, countLabel);

        Region sp = UIUtils.spacer();

        Button exportBtn = UIUtils.ghostBtn("📤 Export CSV");
        exportBtn.setOnAction(e -> handleExport());

        Button addBtn = UIUtils.primaryBtn("➕  Add Student", UIUtils.ACCENT_GREEN);
        addBtn.setOnAction(e -> showStudentForm(null));

        header.getChildren().addAll(left, sp, exportBtn, addBtn);
        return header;
    }

    private HBox buildFilters() {
        HBox filters = new HBox(10);
        filters.setAlignment(Pos.CENTER_LEFT);
        filters.setStyle(
            "-fx-background-color: " + UIUtils.BG_CARD + ";" +
            "-fx-border-color: rgba(255,255,255,0.06);" +
            "-fx-border-radius: 14px;" +
            "-fx-background-radius: 14px;" +
            "-fx-padding: 14px 16px;"
        );

        // Search
        searchField = UIUtils.input("🔍  Search by name, roll number...");
        searchField.setPrefWidth(280);
        searchField.textProperty().addListener((obs, old, val) -> loadStudents());

        // Class filter
        List<String> classes = ds.getClasses();
        classFilter = new ComboBox<>();
        classFilter.getItems().add("All Classes");
        classFilter.getItems().addAll(classes);
        classFilter.setValue("All Classes");
        styleCombo(classFilter);
        classFilter.setOnAction(e -> loadStudents());

        // Section filter
        sectionFilter = new ComboBox<>();
        sectionFilter.getItems().addAll("All Sections", "A", "B", "C");
        sectionFilter.setValue("All Sections");
        styleCombo(sectionFilter);
        sectionFilter.setOnAction(e -> loadStudents());

        Button clearBtn = UIUtils.ghostBtn("✕ Clear");
        clearBtn.setOnAction(e -> {
            searchField.clear();
            classFilter.setValue("All Classes");
            sectionFilter.setValue("All Sections");
            loadStudents();
        });

        filters.getChildren().addAll(searchField, classFilter, sectionFilter, clearBtn);
        return filters;
    }

    private void styleCombo(ComboBox<String> cb) {
        cb.setStyle(
            "-fx-background-color: #0a0a14;" +
            "-fx-text-fill: " + UIUtils.TEXT_PRIMARY + ";" +
            "-fx-border-color: rgba(255,255,255,0.07);" +
            "-fx-border-radius: 10px;" +
            "-fx-background-radius: 10px;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 2px 6px;"
        );
        cb.setPrefWidth(150);
    }

    private HBox buildTableHeader() {
        HBox header = new HBox(0);
        header.setStyle(
            "-fx-background-color: rgba(255,255,255,0.025);" +
            "-fx-border-color: rgba(255,255,255,0.04);" +
            "-fx-border-width: 0 0 1px 0;" +
            "-fx-padding: 12px 16px;"
        );

        String[] cols = {"Student", "Roll Number", "Class", "Gender", "Contact", "Status", "Actions"};
        double[] widths = {240, 120, 90, 90, 140, 90, 120};

        for (int i = 0; i < cols.length; i++) {
            Label lbl = new Label(cols[i].toUpperCase());
            lbl.setStyle("-fx-font-size: 9.5px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_MUTED + "; -fx-letter-spacing: 1.2px;");
            lbl.setMinWidth(widths[i]);
            lbl.setPrefWidth(widths[i]);
            header.getChildren().add(lbl);
        }
        return header;
    }

    private void loadStudents() {
        String query  = searchField != null ? searchField.getText() : "";
        String cls    = (classFilter != null && !"All Classes".equals(classFilter.getValue()))
                        ? classFilter.getValue() : null;
        String sect   = (sectionFilter != null && !"All Sections".equals(sectionFilter.getValue()))
                        ? sectionFilter.getValue() : null;

        List<Student> students = ds.searchStudents(query, cls, sect);

        if (countLabel != null) {
            countLabel.setText(students.size() + " student" + (students.size() != 1 ? "s" : "") + " found");
        }

        if (tableBody == null) return;
        tableBody.getChildren().clear();

        if (students.isEmpty()) {
            VBox empty = new VBox(12);
            empty.setAlignment(Pos.CENTER);
            empty.setStyle("-fx-padding: 60px;");
            Label emptyIcon = new Label("🔍");
            emptyIcon.setStyle("-fx-font-size: 40px;");
            Label emptyLabel = new Label("No students found");
            emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");
            Label emptySubLabel = new Label("Try adjusting your search or filters");
            emptySubLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");
            empty.getChildren().addAll(emptyIcon, emptyLabel, emptySubLabel);
            tableBody.getChildren().add(empty);
            return;
        }

        for (int i = 0; i < students.size(); i++) {
            Student st = students.get(i);
            HBox row = buildStudentRow(st, i % 2 == 0);
            tableBody.getChildren().add(row);

            // Staggered animation
            int delay = i * 40;
            row.setOpacity(0);
            PauseTransition pause = new PauseTransition(Duration.millis(delay));
            pause.setOnFinished(e -> {
                FadeTransition ft = new FadeTransition(Duration.millis(250), row);
                ft.setFromValue(0); ft.setToValue(1); ft.play();
            });
            pause.play();
        }
    }

    private HBox buildStudentRow(Student st, boolean even) {
        HBox row = new HBox(0);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle(
            "-fx-background-color: " + (even ? "transparent" : "rgba(255,255,255,0.015)") + ";" +
            "-fx-border-color: rgba(255,255,255,0.03);" +
            "-fx-border-width: 0 0 1px 0;" +
            "-fx-padding: 12px 16px;" +
            "-fx-cursor: hand;"
        );
        row.setOnMouseEntered(e -> row.setStyle(
            "-fx-background-color: rgba(255,255,255,0.035);" +
            "-fx-border-color: rgba(255,255,255,0.03);" +
            "-fx-border-width: 0 0 1px 0;" +
            "-fx-padding: 12px 16px;" +
            "-fx-cursor: hand;"
        ));
        row.setOnMouseExited(e -> row.setStyle(
            "-fx-background-color: " + (even ? "transparent" : "rgba(255,255,255,0.015)") + ";" +
            "-fx-border-color: rgba(255,255,255,0.03);" +
            "-fx-border-width: 0 0 1px 0;" +
            "-fx-padding: 12px 16px;" +
            "-fx-cursor: hand;"
        ));

        // Avatar + Name
        HBox nameCell = new HBox(10);
        nameCell.setAlignment(Pos.CENTER_LEFT);
        nameCell.setMinWidth(240); nameCell.setPrefWidth(240);

        StackPane avatar = new StackPane();
        avatar.setPrefSize(36, 36); avatar.setMinSize(36, 36);
        Circle avBg = new Circle(18);
        avBg.setFill(Color.web(st.getGenderColor() + "22"));
        avBg.setStroke(Color.web(st.getGenderColor() + "55"));
        avBg.setStrokeWidth(1.5);
        Label avTxt = new Label(st.getInitials());
        avTxt.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + st.getGenderColor() + ";");
        avatar.getChildren().addAll(avBg, avTxt);

        VBox nameInfo = new VBox(2);
        Label nameLabel = new Label(st.getName());
        nameLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_PRIMARY + ";");
        Label emailLabel = new Label(st.getEmail() != null ? st.getEmail() : "No email");
        emailLabel.setStyle("-fx-font-size: 10.5px; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");
        nameInfo.getChildren().addAll(nameLabel, emailLabel);

        nameCell.getChildren().addAll(avatar, nameInfo);

        // Roll Number
        Label rollLabel = new Label(st.getRollNumber());
        rollLabel.setMinWidth(120); rollLabel.setPrefWidth(120);
        rollLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + UIUtils.ACCENT_BLUE + "; -fx-font-family: 'Courier New';");

        // Class
        Label classLabel = new Label("Class " + st.getClassName() + "-" + st.getSection());
        classLabel.setMinWidth(90); classLabel.setPrefWidth(90);
        classLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + UIUtils.TEXT_SECONDARY + ";");

        // Gender badge
        HBox genderCell = new HBox(0);
        genderCell.setMinWidth(90); genderCell.setPrefWidth(90);
        String genderColor = st.getGenderColor();
        Label genderLabel = UIUtils.badge(st.getGender() != null ? capitalize(st.getGender()) : "—", genderColor);
        genderCell.getChildren().add(genderLabel);

        // Contact
        Label contactLabel = new Label(st.getContact() != null ? st.getContact() : "—");
        contactLabel.setMinWidth(140); contactLabel.setPrefWidth(140);
        contactLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + UIUtils.TEXT_SECONDARY + ";");

        // Status badge
        HBox statusCell = new HBox(0);
        statusCell.setMinWidth(90); statusCell.setPrefWidth(90);
        Label statusBadge = UIUtils.badge(st.isActive() ? "Active" : "Inactive",
            st.isActive() ? UIUtils.ACCENT_GREEN : UIUtils.TEXT_MUTED);
        statusCell.getChildren().add(statusBadge);

        // Actions
        HBox actionsCell = new HBox(6);
        actionsCell.setAlignment(Pos.CENTER_LEFT);
        actionsCell.setMinWidth(120); actionsCell.setPrefWidth(120);

        Button editBtn = new Button("✏");
        styleActionBtn(editBtn, UIUtils.ACCENT_BLUE);
        editBtn.setOnAction(e -> showStudentForm(st));

        Button deleteBtn = new Button("🗑");
        styleActionBtn(deleteBtn, UIUtils.ACCENT_RED);
        deleteBtn.setOnAction(e -> confirmDelete(st));

        actionsCell.getChildren().addAll(editBtn, deleteBtn);

        row.getChildren().addAll(nameCell, rollLabel, classLabel, genderCell, contactLabel, statusCell, actionsCell);
        return row;
    }

    private void styleActionBtn(Button btn, String color) {
        btn.setStyle(
            "-fx-background-color: " + color + "18;" +
            "-fx-text-fill: " + color + ";" +
            "-fx-border-color: " + color + "33;" +
            "-fx-border-radius: 7px;" +
            "-fx-background-radius: 7px;" +
            "-fx-padding: 5px 9px;" +
            "-fx-cursor: hand;" +
            "-fx-font-size: 13px;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: " + color + "33;" +
            "-fx-text-fill: " + color + ";" +
            "-fx-border-color: " + color + "66;" +
            "-fx-border-radius: 7px;" +
            "-fx-background-radius: 7px;" +
            "-fx-padding: 5px 9px;" +
            "-fx-cursor: hand;" +
            "-fx-font-size: 13px;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: " + color + "18;" +
            "-fx-text-fill: " + color + ";" +
            "-fx-border-color: " + color + "33;" +
            "-fx-border-radius: 7px;" +
            "-fx-background-radius: 7px;" +
            "-fx-padding: 5px 9px;" +
            "-fx-cursor: hand;" +
            "-fx-font-size: 13px;"
        ));
    }

    private void showStudentForm(Student student) {
        StudentFormDialog dialog = new StudentFormDialog(student);
        Student result = dialog.showAndWait();
        if (result != null) {
            if (student == null) {
                ds.addStudent(result);
            } else {
                ds.updateStudent(result);
            }
            loadStudents();
        }
    }

    private void confirmDelete(Student st) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Student");
        alert.setHeaderText("Delete " + st.getName() + "?");
        alert.setContentText("This will also remove all attendance and grade records for this student.");
        alert.getDialogPane().setStyle(
            "-fx-background-color: #0e0e1a;" +
            "-fx-font-size: 13px;"
        );
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                ds.deleteStudent(st.getId());
                loadStudents();
            }
        });
    }

    private void handleExport() {
        List<Student> students = ds.getStudents();
        StringBuilder csv = new StringBuilder("Name,Roll Number,Class,Section,Gender,Email,Contact\n");
        for (Student s : students) {
            csv.append(String.join(",",
                s.getName(), s.getRollNumber(), s.getClassName(),
                s.getSection(), s.getGender() != null ? s.getGender() : "",
                s.getEmail() != null ? s.getEmail() : "",
                s.getContact() != null ? s.getContact() : "")).append("\n");
        }
        try {
            java.io.FileWriter fw = new java.io.FileWriter(System.getProperty("user.home") + "/students_export.csv");
            fw.write(csv.toString()); fw.close();
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Export Successful");
            info.setHeaderText("CSV exported to your home directory");
            info.setContentText("File: students_export.csv");
            info.showAndWait();
        } catch (Exception e) {
            System.err.println("Export error: " + e.getMessage());
        }
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public VBox getRoot() { return root; }
}
