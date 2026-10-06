package pages;
import javafx.stage.Stage;

import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.util.Duration;
import models.Grade;
import models.Student;
import models.Subject;
import services.DataService;
import utils.UIUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * GradesPage - View and manage student grades
 */
public class GradesPage {
    private VBox root;
    private DataService ds = DataService.getInstance();
    private VBox tableBody;
    private ComboBox<String> classFilter;
    private ComboBox<String> subjectFilter;
    private TextField searchField;

    public GradesPage() {
        root = new VBox(20);
        root.setStyle("-fx-padding: 24px; -fx-background-color: " + UIUtils.BG_DARK + ";");
        buildUI();
        UIUtils.fadeIn(root);
    }

    private void buildUI() {
        root.getChildren().add(buildHeader());
        root.getChildren().add(buildSummaryCards());
        root.getChildren().add(buildFilters());
        root.getChildren().add(buildTable());
    }

    private HBox buildHeader() {
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        VBox left = new VBox(4);
        Label title = UIUtils.heading("🎓  Grades");
        Label sub = UIUtils.subheading("Track academic performance and compute letter grades");
        left.getChildren().addAll(title, sub);
        Region sp = UIUtils.spacer();
        Button exportBtn = UIUtils.ghostBtn("📤 Export Report");
        exportBtn.setOnAction(e -> exportGradesCSV());
        header.getChildren().addAll(left, sp, exportBtn);
        return header;
    }

    private HBox buildSummaryCards() {
        HBox row = new HBox(14);
        List<Grade> grades = ds.getGrades();

        long total = grades.size();
        long passed = grades.stream().filter(Grade::isPassed).count();
        long failed = grades.stream().filter(g -> !g.isPassed()).count();
        double avgPct = grades.stream().mapToDouble(Grade::getFinalPercentage).average().orElse(0);

        // Count grade distribution
        Map<String, Long> gradeCount = new LinkedHashMap<>();
        gradeCount.put("A+/A", grades.stream().filter(g -> g.getLetterGrade() != null && (g.getLetterGrade().startsWith("A"))).count());
        gradeCount.put("B+/B", grades.stream().filter(g -> g.getLetterGrade() != null && g.getLetterGrade().startsWith("B")).count());
        gradeCount.put("C+/C", grades.stream().filter(g -> g.getLetterGrade() != null && g.getLetterGrade().startsWith("C")).count());
        gradeCount.put("D/F",  grades.stream().filter(g -> g.getLetterGrade() != null && (g.getLetterGrade().equals("D") || g.getLetterGrade().equals("F"))).count());

        Object[][] cards = {
            {"📝", "Total Records",  String.valueOf(total),          UIUtils.ACCENT_BLUE},
            {"✅", "Passed",         String.valueOf(passed),         UIUtils.ACCENT_GREEN},
            {"❌", "Failed",         String.valueOf(failed),         UIUtils.ACCENT_RED},
            {"📊", "Average Score",  String.format("%.1f%%", avgPct), UIUtils.ACCENT_PURPLE},
        };

        for (Object[] c : cards) {
            VBox card = new VBox(6);
            card.setStyle(
                "-fx-background-color: " + UIUtils.BG_CARD + ";" +
                "-fx-border-color: " + c[3] + "33;" +
                "-fx-border-radius: 14px; -fx-background-radius: 14px;" +
                "-fx-padding: 16px;" +
                "-fx-effect: dropshadow(gaussian, " + c[3] + "22, 14, 0, 0, 3);"
            );
            HBox.setHgrow(card, Priority.ALWAYS);
            Label icon = new Label((String) c[0]);
            icon.setStyle("-fx-font-size: 22px;");
            Label val = new Label((String) c[2]);
            val.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + c[3] + ";");
            Label lbl = new Label((String) c[1]);
            lbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + UIUtils.TEXT_MUTED + "; -fx-font-weight: bold;");
            card.getChildren().addAll(icon, val, lbl);
            UIUtils.addCardHover(card);
            row.getChildren().add(card);
        }
        return row;
    }

    private HBox buildFilters() {
        HBox filters = new HBox(10);
        filters.setAlignment(Pos.CENTER_LEFT);
        filters.setStyle(
            "-fx-background-color: " + UIUtils.BG_CARD + ";" +
            "-fx-border-color: rgba(255,255,255,0.06);" +
            "-fx-border-radius: 14px; -fx-background-radius: 14px;" +
            "-fx-padding: 14px 16px;"
        );

        searchField = UIUtils.input("🔍  Search student...");
        searchField.setPrefWidth(230);
        searchField.textProperty().addListener((o, ov, nv) -> loadGrades());

        classFilter = UIUtils.comboBox("All Classes", "10", "11", "12");
        classFilter.setValue("All Classes");
        classFilter.setPrefWidth(140);
        classFilter.setOnAction(e -> loadGrades());

        List<String> subjectNames = ds.getSubjects().stream().map(Subject::getName).collect(Collectors.toList());
        subjectFilter = new ComboBox<>();
        subjectFilter.getItems().add("All Subjects");
        subjectFilter.getItems().addAll(subjectNames);
        subjectFilter.setValue("All Subjects");
        subjectFilter.setPrefWidth(180);
        subjectFilter.setStyle(
            "-fx-background-color: #0a0a14;" +
            "-fx-border-color: rgba(255,255,255,0.07);" +
            "-fx-border-radius: 10px; -fx-background-radius: 10px;" +
            "-fx-font-size: 13px; -fx-padding: 2px 6px;"
        );
        subjectFilter.setOnAction(e -> loadGrades());

        Button clearBtn = UIUtils.ghostBtn("✕ Clear");
        clearBtn.setOnAction(e -> {
            searchField.clear();
            classFilter.setValue("All Classes");
            subjectFilter.setValue("All Subjects");
            loadGrades();
        });

        filters.getChildren().addAll(searchField, classFilter, subjectFilter, clearBtn);
        return filters;
    }

    private VBox buildTable() {
        VBox tableCard = new VBox(0);
        tableCard.setStyle(
            "-fx-background-color: " + UIUtils.BG_CARD + ";" +
            "-fx-border-color: rgba(255,255,255,0.06);" +
            "-fx-border-radius: 18px; -fx-background-radius: 18px;"
        );
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        // Header row
        HBox header = new HBox(0);
        header.setStyle(
            "-fx-background-color: rgba(255,255,255,0.025);" +
            "-fx-padding: 12px 16px;" +
            "-fx-border-color: rgba(255,255,255,0.04);" +
            "-fx-border-width: 0 0 1px 0;"
        );
        String[] cols   = {"Student", "Roll No.", "Subject", "Assessments", "Score", "Grade", "GPA", "Status"};
        double[] widths = {200, 110, 160, 140, 100, 70, 60, 90};
        for (int i = 0; i < cols.length; i++) {
            Label lbl = new Label(cols[i].toUpperCase());
            lbl.setMinWidth(widths[i]); lbl.setPrefWidth(widths[i]);
            lbl.setStyle("-fx-font-size: 9.5px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_MUTED + "; -fx-letter-spacing: 1.2px;");
            header.getChildren().add(lbl);
        }

        tableBody = new VBox(0);
        ScrollPane scroll = new ScrollPane(tableBody);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scroll.setPrefHeight(420);

        tableCard.getChildren().addAll(header, scroll);
        loadGrades();
        return tableCard;
    }

    private void loadGrades() {
        List<Grade> grades = ds.getGrades();

        // Filter
        String query = searchField != null ? searchField.getText().toLowerCase() : "";
        String cls   = (classFilter != null && !"All Classes".equals(classFilter.getValue())) ? classFilter.getValue() : null;
        String subj  = (subjectFilter != null && !"All Subjects".equals(subjectFilter.getValue())) ? subjectFilter.getValue() : null;

        List<Grade> filtered = grades.stream()
            .filter(g -> query.isEmpty() ||
                (g.getStudentName() != null && g.getStudentName().toLowerCase().contains(query)) ||
                (g.getRollNumber() != null && g.getRollNumber().toLowerCase().contains(query)))
            .filter(g -> cls == null || cls.equals(g.getClassName()))
            .filter(g -> subj == null || subj.equals(g.getSubjectName()))
            .sorted(Comparator.comparing(Grade::getStudentName, Comparator.nullsLast(Comparator.naturalOrder())))
            .collect(Collectors.toList());

        if (tableBody == null) return;
        tableBody.getChildren().clear();

        if (filtered.isEmpty()) {
            VBox empty = new VBox(10);
            empty.setAlignment(Pos.CENTER);
            empty.setStyle("-fx-padding: 50px;");
            Label lbl = new Label("🎓  No grade records found");
            lbl.setStyle("-fx-font-size: 15px; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");
            empty.getChildren().add(lbl);
            tableBody.getChildren().add(empty);
            return;
        }

        for (int i = 0; i < filtered.size(); i++) {
            Grade g = filtered.get(i);
            boolean even = i % 2 == 0;
            HBox row = buildGradeRow(g, even);
            tableBody.getChildren().add(row);

            int delay = Math.min(i * 30, 600);
            row.setOpacity(0);
            PauseTransition pt = new PauseTransition(Duration.millis(delay));
            pt.setOnFinished(e -> {
                FadeTransition ft2 = new FadeTransition(Duration.millis(250), row);
                ft2.setFromValue(0); ft2.setToValue(1); ft2.play();
            });
            pt.play();
        }
    }

    private HBox buildGradeRow(Grade g, boolean even) {
        HBox row = new HBox(0);
        row.setAlignment(Pos.CENTER_LEFT);
        String baseBg = even ? "transparent" : "rgba(255,255,255,0.012)";
        row.setStyle(
            "-fx-background-color: " + baseBg + ";" +
            "-fx-padding: 12px 16px;" +
            "-fx-border-color: rgba(255,255,255,0.03);" +
            "-fx-border-width: 0 0 1px 0; -fx-cursor: hand;"
        );
        row.setOnMouseEntered(e -> row.setStyle(
            "-fx-background-color: rgba(255,255,255,0.035);" +
            "-fx-padding: 12px 16px;" +
            "-fx-border-color: rgba(255,255,255,0.03);" +
            "-fx-border-width: 0 0 1px 0; -fx-cursor: hand;"
        ));
        row.setOnMouseExited(e -> row.setStyle(
            "-fx-background-color: " + baseBg + ";" +
            "-fx-padding: 12px 16px;" +
            "-fx-border-color: rgba(255,255,255,0.03);" +
            "-fx-border-width: 0 0 1px 0; -fx-cursor: hand;"
        ));
        row.setOnMouseClicked(e -> showGradeDetail(g));

        // Student name + class
        VBox nameCell = new VBox(2);
        nameCell.setMinWidth(200); nameCell.setPrefWidth(200);
        Label nameLabel = new Label(g.getStudentName() != null ? g.getStudentName() : "—");
        nameLabel.setStyle("-fx-font-size: 12.5px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_PRIMARY + ";");
        Label classLabel = new Label("Class " + g.getClassName());
        classLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");
        nameCell.getChildren().addAll(nameLabel, classLabel);

        // Roll number
        Label rollLabel = mkCell(g.getRollNumber(), 110, UIUtils.ACCENT_BLUE);
        rollLabel.setStyle("-fx-font-size: 11.5px; -fx-text-fill: " + UIUtils.ACCENT_BLUE + "; -fx-font-family: 'Courier New'; -fx-min-width: 110px; -fx-pref-width: 110px;");

        // Subject
        Label subjectLabel = mkCell(g.getSubjectName(), 160, UIUtils.TEXT_SECONDARY);

        // Assessment progress bar
        HBox assessCell = new HBox(8);
        assessCell.setMinWidth(140); assessCell.setPrefWidth(140);
        assessCell.setAlignment(Pos.CENTER_LEFT);
        int numAssessments = g.getAssessments().size();
        VBox barBox = new VBox(3);
        barBox.setAlignment(Pos.CENTER_LEFT);
        Label assessLabel = new Label(numAssessments + " assessment" + (numAssessments != 1 ? "s" : ""));
        assessLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + UIUtils.TEXT_SECONDARY + ";");
        // mini progress bar
        StackPane barBg = new StackPane();
        barBg.setPrefSize(90, 5); barBg.setMinSize(90, 5);
        Rectangle barTrack = new Rectangle(90, 5, Color.web("rgba(255,255,255,0.06)"));
        barTrack.setArcWidth(5); barTrack.setArcHeight(5);
        double pct = g.getFinalPercentage() / 100.0;
        Rectangle barFill = new Rectangle(Math.max(0, pct * 90), 5, Color.web(g.getGradeColor()));
        barFill.setArcWidth(5); barFill.setArcHeight(5);
        barBg.getChildren().addAll(barTrack, barFill);
        StackPane.setAlignment(barFill, Pos.CENTER_LEFT);
        barBox.getChildren().addAll(assessLabel, barBg);
        assessCell.getChildren().add(barBox);

        // Final percentage
        Label scoreLabel = new Label(String.format("%.1f%%", g.getFinalPercentage()));
        scoreLabel.setMinWidth(100); scoreLabel.setPrefWidth(100);
        scoreLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + g.getGradeColor() + ";");

        // Letter grade badge
        HBox gradeCell = new HBox(0);
        gradeCell.setMinWidth(70); gradeCell.setPrefWidth(70);
        gradeCell.setAlignment(Pos.CENTER_LEFT);
        String lg = g.getLetterGrade() != null ? g.getLetterGrade() : "N/A";
        Label gradeBadge = new Label(lg);
        gradeBadge.setStyle(
            "-fx-background-color: " + g.getGradeColor() + "22;" +
            "-fx-text-fill: " + g.getGradeColor() + ";" +
            "-fx-border-color: " + g.getGradeColor() + "55;" +
            "-fx-border-radius: 8px; -fx-background-radius: 8px;" +
            "-fx-padding: 4px 10px; -fx-font-size: 12px; -fx-font-weight: bold;"
        );
        gradeCell.getChildren().add(gradeBadge);

        // GPA
        Label gpaLabel = mkCell(String.format("%.1f", g.getGpaPoints()), 60, UIUtils.ACCENT_CYAN);
        gpaLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.ACCENT_CYAN + "; -fx-min-width: 60px; -fx-pref-width: 60px;");

        // Status badge
        HBox statusCell = new HBox(0);
        statusCell.setMinWidth(90); statusCell.setPrefWidth(90);
        statusCell.setAlignment(Pos.CENTER_LEFT);
        Label statusBadge = UIUtils.badge(g.isPassed() ? "Passed" : "Failed", g.isPassed() ? UIUtils.ACCENT_GREEN : UIUtils.ACCENT_RED);
        statusCell.getChildren().add(statusBadge);

        row.getChildren().addAll(nameCell, rollLabel, subjectLabel, assessCell, scoreLabel, gradeCell, gpaLabel, statusCell);
        return row;
    }

    private void showGradeDetail(Grade g) {
        Stage detailStage = new Stage();
        detailStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        detailStage.setTitle("Grade Detail — " + g.getStudentName());

        VBox root = new VBox(20);
        root.setStyle("-fx-background-color: #0e0e1a; -fx-padding: 28px;");
        root.setPrefWidth(520);

        // Header
        HBox header = new HBox(14);
        header.setAlignment(Pos.CENTER_LEFT);
        StackPane avatarPane = new StackPane();
        avatarPane.setPrefSize(52, 52); avatarPane.setMinSize(52, 52);
        Circle avBg = new Circle(26);
        avBg.setFill(Color.web(UIUtils.ACCENT_PURPLE + "22"));
        avBg.setStroke(Color.web(UIUtils.ACCENT_PURPLE + "55")); avBg.setStrokeWidth(1.5);
        Label avTxt = new Label(g.getStudentName() != null && !g.getStudentName().isEmpty() ? g.getStudentName().substring(0,1).toUpperCase() : "?");
        avTxt.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.ACCENT_PURPLE + ";");
        avatarPane.getChildren().addAll(avBg, avTxt);

        VBox titleBox = new VBox(4);
        Label nameLabel = new Label(g.getStudentName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_PRIMARY + ";");
        Label subLabel = new Label(g.getSubjectName() + "  ·  Class " + g.getClassName() + "  ·  " + g.getRollNumber());
        subLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");
        titleBox.getChildren().addAll(nameLabel, subLabel);
        header.getChildren().addAll(avatarPane, titleBox);

        // Overall score card
        HBox scoreRow = new HBox(12);
        Object[][] scoreCards = {
            {"Final %",     String.format("%.1f%%", g.getFinalPercentage()), g.getGradeColor()},
            {"Grade",       g.getLetterGrade() != null ? g.getLetterGrade() : "N/A", g.getGradeColor()},
            {"GPA Points",  String.format("%.1f", g.getGpaPoints()), UIUtils.ACCENT_CYAN},
            {"Attendance",  String.format("%.0f%%", g.getAttendancePercentage()), UIUtils.ACCENT_BLUE},
        };
        for (Object[] sc : scoreCards) {
            VBox card = new VBox(4);
            card.setAlignment(Pos.CENTER);
            card.setStyle(
                "-fx-background-color: " + sc[2] + "10;" +
                "-fx-border-color: " + sc[2] + "33;" +
                "-fx-border-radius: 12px; -fx-background-radius: 12px;" +
                "-fx-padding: 14px;"
            );
            HBox.setHgrow(card, Priority.ALWAYS);
            Label val = new Label((String) sc[1]);
            val.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + sc[2] + ";");
            Label lbl = new Label((String) sc[0]);
            lbl.setStyle("-fx-font-size: 10px; -fx-text-fill: " + UIUtils.TEXT_MUTED + "; -fx-font-weight: bold;");
            card.getChildren().addAll(val, lbl);
            scoreRow.getChildren().add(card);
        }

        // Assessments breakdown
        Label assessTitle = new Label("Assessment Breakdown");
        assessTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_PRIMARY + ";");

        VBox assessList = new VBox(8);
        for (Grade.Assessment a : g.getAssessments()) {
            HBox assessRow = new HBox(12);
            assessRow.setAlignment(Pos.CENTER_LEFT);
            assessRow.setStyle(
                "-fx-background-color: rgba(255,255,255,0.025);" +
                "-fx-background-radius: 10px; -fx-padding: 10px 14px;"
            );
            Label typeLabel = UIUtils.badge(a.getType(), UIUtils.ACCENT_BLUE);
            Label titleLabel = new Label(a.getTitle());
            titleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + UIUtils.TEXT_PRIMARY + ";");
            Region sp = UIUtils.spacer();
            Label scoreLabel = new Label(String.format("%.0f / %.0f", a.getObtainedMarks(), a.getTotalMarks()));
            scoreLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_PRIMARY + ";");
            String pctStr = String.format("%.1f%%", a.getPercentage());
            String pctColor = a.getPercentage() >= 80 ? UIUtils.ACCENT_GREEN : a.getPercentage() >= 60 ? UIUtils.ACCENT_YELLOW : UIUtils.ACCENT_RED;
            Label pctLabel = UIUtils.badge(pctStr, pctColor);
            Label weightLabel = new Label("Wt: " + (int) a.getWeightage() + "%");
            weightLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");
            assessRow.getChildren().addAll(typeLabel, titleLabel, sp, scoreLabel, pctLabel, weightLabel);
            assessList.getChildren().add(assessRow);
        }

        Button closeBtn = UIUtils.primaryBtn("Close", UIUtils.ACCENT_PURPLE);
        closeBtn.setOnAction(e -> detailStage.close());
        HBox btnRow = new HBox(0);
        btnRow.setAlignment(Pos.CENTER_RIGHT);
        btnRow.getChildren().add(closeBtn);

        root.getChildren().addAll(header, UIUtils.divider(), scoreRow, UIUtils.divider(), assessTitle, assessList, UIUtils.divider(), btnRow);

        javafx.scene.Scene scene = new javafx.scene.Scene(root);
        try { scene.getStylesheets().add(getClass().getResource("/styles/dark.css").toExternalForm()); } catch (Exception ignored) {}
        detailStage.setScene(scene);
        detailStage.show();
    }

    private Label mkCell(String text, double width, String color) {
        Label l = new Label(text != null ? text : "—");
        l.setMinWidth(width); l.setPrefWidth(width);
        l.setStyle("-fx-font-size: 12.5px; -fx-text-fill: " + color + ";");
        return l;
    }

    private void exportGradesCSV() {
        List<Grade> grades = ds.getGrades();
        StringBuilder csv = new StringBuilder("Student,Roll Number,Subject,Class,Final %,Letter Grade,GPA,Passed\n");
        for (Grade g : grades) {
            csv.append(String.join(",",
                safe(g.getStudentName()), safe(g.getRollNumber()), safe(g.getSubjectName()),
                safe(g.getClassName()),
                String.format("%.1f", g.getFinalPercentage()),
                safe(g.getLetterGrade()),
                String.format("%.1f", g.getGpaPoints()),
                String.valueOf(g.isPassed())
            )).append("\n");
        }
        try {
            java.io.FileWriter fw = new java.io.FileWriter(System.getProperty("user.home") + "/grades_export.csv");
            fw.write(csv.toString()); fw.close();
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Export OK"); info.setHeaderText("Grades exported");
            info.setContentText("File: ~/grades_export.csv"); info.showAndWait();
        } catch (Exception e) { System.err.println("Export error: " + e.getMessage()); }
    }

    private String safe(String s) { return s != null ? s.replace(",", ";") : ""; }

    public VBox getRoot() { return root; }
}
