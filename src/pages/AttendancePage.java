package pages;

import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.util.Duration;
import models.Attendance;
import models.Student;
import services.DataService;
import services.SessionService;
import utils.UIUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * AttendancePage - Mark attendance and view history with tabs
 */
public class AttendancePage {
    private VBox root;
    private DataService ds = DataService.getInstance();
    private VBox tabContent;
    private String activeTab = "mark";

    public AttendancePage() {
        root = new VBox(20);
        root.setStyle("-fx-padding: 24px; -fx-background-color: " + UIUtils.BG_DARK + ";");
        buildUI();
        UIUtils.fadeIn(root);
    }

    private void buildUI() {
        // Header
        VBox header = new VBox(4);
        Label title = UIUtils.heading("📋  Attendance");
        Label sub = UIUtils.subheading("Mark and track student attendance in real-time");
        header.getChildren().addAll(title, sub);
        root.getChildren().add(header);

        // Tab bar
        root.getChildren().add(buildTabBar());

        // Tab content area
        tabContent = new VBox(0);
        root.getChildren().add(tabContent);
        VBox.setVgrow(tabContent, Priority.ALWAYS);

        showTab("mark");
    }

    private HBox buildTabBar() {
        HBox bar = new HBox(4);
        bar.setStyle(
            "-fx-background-color: " + UIUtils.BG_CARD + ";" +
            "-fx-border-color: rgba(255,255,255,0.06);" +
            "-fx-border-radius: 12px;" +
            "-fx-background-radius: 12px;" +
            "-fx-padding: 5px;"
        );
        bar.setMaxWidth(340);

        String[][] tabs = {
            {"mark",    "📋", "Mark Attendance"},
            {"history", "📅", "View History"},
        };

        for (String[] tab : tabs) {
            Button btn = new Button(tab[1] + "  " + tab[2]);
            btn.setId("tab-" + tab[0]);
            updateTabStyle(btn, tab[0].equals(activeTab));
            btn.setOnAction(e -> showTab(tab[0]));
            bar.getChildren().add(btn);
        }
        return bar;
    }

    private void updateTabStyle(Button btn, boolean active) {
        if (active) {
            btn.setStyle(
                "-fx-background-color: linear-gradient(to right, " + UIUtils.ACCENT_GREEN + ", #78c800);" +
                "-fx-text-fill: #050508;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 9px;" +
                "-fx-padding: 9px 18px;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, " + UIUtils.ACCENT_GREEN + "66, 14, 0, 0, 3);"
            );
        } else {
            btn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: " + UIUtils.TEXT_MUTED + ";" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 9px;" +
                "-fx-padding: 9px 18px;" +
                "-fx-cursor: hand;"
            );
            btn.setOnMouseEntered(ev -> btn.setStyle(
                "-fx-background-color: rgba(255,255,255,0.04);" +
                "-fx-text-fill: " + UIUtils.TEXT_SECONDARY + ";" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 9px;" +
                "-fx-padding: 9px 18px;" +
                "-fx-cursor: hand;"
            ));
            btn.setOnMouseExited(ev -> updateTabStyle(btn, false));
        }
    }

    private void showTab(String tabId) {
        activeTab = tabId;

        // Update tab styles
        root.lookupAll("Button").forEach(node -> {
            if (node.getId() != null && node.getId().startsWith("tab-")) {
                Button btn = (Button) node;
                updateTabStyle(btn, btn.getId().equals("tab-" + tabId));
            }
        });

        tabContent.getChildren().clear();
        if ("mark".equals(tabId)) {
            tabContent.getChildren().add(buildMarkTab());
        } else {
            tabContent.getChildren().add(buildHistoryTab());
        }
        UIUtils.fadeIn(tabContent);
    }

    // ─── MARK ATTENDANCE TAB ────────────────────────────────────────────────

    private VBox buildMarkTab() {
        VBox tab = new VBox(16);

        // Session settings card
        VBox sessionCard = new VBox(14);
        sessionCard.setStyle(
            "-fx-background-color: " + UIUtils.BG_CARD + ";" +
            "-fx-border-color: " + UIUtils.ACCENT_GREEN + "33;" +
            "-fx-border-radius: 16px;" +
            "-fx-background-radius: 16px;" +
            "-fx-padding: 20px;"
        );

        Label sessionTitle = new Label("⚙️  Session Details");
        sessionTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_PRIMARY + ";");

        HBox sessionFields = new HBox(14);
        sessionFields.setAlignment(Pos.CENTER_LEFT);

        // Date picker
        VBox dateBox = new VBox(6);
        Label dateLbl = mkFieldLabel("DATE");
        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setStyle(
            "-fx-background-color: #0a0a14;" +
            "-fx-border-color: rgba(255,255,255,0.07);" +
            "-fx-border-radius: 10px;" +
            "-fx-background-radius: 10px;" +
            "-fx-font-size: 13px;"
        );
        datePicker.setPrefWidth(160);
        dateBox.getChildren().addAll(dateLbl, datePicker);

        // Subject
        VBox subjectBox = new VBox(6);
        Label subjectLbl = mkFieldLabel("SUBJECT");
        ComboBox<String> subjectCombo = UIUtils.comboBox(
            "Mathematics","Physics","Chemistry","Biology","English","Urdu","Computer Science","History");
        subjectCombo.setPromptText("Select subject...");
        subjectCombo.setPrefWidth(200);
        subjectBox.getChildren().addAll(subjectLbl, subjectCombo);

        // Class filter
        VBox classBox = new VBox(6);
        Label classLbl = mkFieldLabel("FILTER CLASS");
        ComboBox<String> classCombo = UIUtils.comboBox("All Classes", "10", "11", "12");
        classCombo.setValue("All Classes");
        classCombo.setPrefWidth(150);
        classBox.getChildren().addAll(classLbl, classCombo);

        sessionFields.getChildren().addAll(dateBox, subjectBox, classBox);
        sessionCard.getChildren().addAll(sessionTitle, sessionFields);

        // Student list card
        VBox listCard = new VBox(0);
        listCard.setStyle(
            "-fx-background-color: " + UIUtils.BG_CARD + ";" +
            "-fx-border-color: rgba(255,255,255,0.06);" +
            "-fx-border-radius: 16px;" +
            "-fx-background-radius: 16px;"
        );

        // Toolbar
        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setStyle("-fx-padding: 14px 16px; -fx-border-color: rgba(255,255,255,0.04); -fx-border-width: 0 0 1px 0;");

        TextField searchField = UIUtils.input("🔍  Search students...");
        searchField.setPrefWidth(220);

        Region sp = UIUtils.spacer();

        Label markAllLabel = new Label("Mark all:");
        markAllLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");

        toolbar.getChildren().addAll(searchField, sp, markAllLabel);

        // Mark-all buttons
        Map<String, String> statusColors = new LinkedHashMap<>();
        statusColors.put("Present", UIUtils.ACCENT_GREEN);
        statusColors.put("Absent",  UIUtils.ACCENT_RED);
        statusColors.put("Late",    UIUtils.ACCENT_YELLOW);

        // Status map: studentId -> status
        Map<String, String> statusMap = new HashMap<>();

        for (Map.Entry<String, String> e : statusColors.entrySet()) {
            Button markBtn = new Button(e.getKey());
            String color = e.getValue();
            markBtn.setStyle(
                "-fx-background-color: " + color + "18;" +
                "-fx-text-fill: " + color + ";" +
                "-fx-border-color: " + color + "44;" +
                "-fx-border-radius: 8px;" +
                "-fx-background-radius: 8px;" +
                "-fx-padding: 6px 12px;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
            );
            toolbar.getChildren().add(markBtn);
        }

        // Summary bar
        HBox summaryBar = new HBox(20);
        summaryBar.setAlignment(Pos.CENTER_LEFT);
        summaryBar.setStyle("-fx-padding: 8px 16px; -fx-background-color: rgba(255,255,255,0.015); -fx-border-color: rgba(255,255,255,0.04); -fx-border-width: 0 0 1px 0;");

        Label totalSummary = new Label("0 students");
        totalSummary.setStyle("-fx-font-size: 11px; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");
        Label presentSummary = new Label("0 Present");
        presentSummary.setStyle("-fx-font-size: 11px; -fx-text-fill: " + UIUtils.ACCENT_GREEN + ";");
        Label absentSummary = new Label("0 Absent");
        absentSummary.setStyle("-fx-font-size: 11px; -fx-text-fill: " + UIUtils.ACCENT_RED + ";");
        Label lateSummary = new Label("0 Late");
        lateSummary.setStyle("-fx-font-size: 11px; -fx-text-fill: " + UIUtils.ACCENT_YELLOW + ";");
        summaryBar.getChildren().addAll(totalSummary, presentSummary, absentSummary, lateSummary);

        // Student rows container
        VBox studentRows = new VBox(0);
        ScrollPane scroll = new ScrollPane(studentRows);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(350);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        // Load students into the list
        Runnable loadStudentList = () -> {
            String clsVal = "All Classes".equals(classCombo.getValue()) ? null : classCombo.getValue();
            String search = searchField.getText().trim();
            List<Student> students = ds.searchStudents(search, clsVal, null);

            // Init status map with defaults
            for (Student s : students) {
                statusMap.putIfAbsent(s.getId(), "Present");
            }

            studentRows.getChildren().clear();
            for (Student st : students) {
                HBox row = buildAttendanceStudentRow(st, statusMap, () -> updateSummary(students, statusMap, totalSummary, presentSummary, absentSummary, lateSummary));
                studentRows.getChildren().add(row);
            }
            updateSummary(students, statusMap, totalSummary, presentSummary, absentSummary, lateSummary);
        };

        // Mark all buttons
        int[] idx = {0};
        for (Map.Entry<String, String> e : statusColors.entrySet()) {
            Button btn = (Button) toolbar.getChildren().get(toolbar.getChildren().size() - statusColors.size() + idx[0]);
            String status = e.getKey();
            btn.setOnAction(ev -> {
                String clsVal = "All Classes".equals(classCombo.getValue()) ? null : classCombo.getValue();
                String search = searchField.getText().trim();
                List<Student> students = ds.searchStudents(search, clsVal, null);
                for (Student s : students) statusMap.put(s.getId(), status);
                loadStudentList.run();
            });
            idx[0]++;
        }

        classCombo.setOnAction(e -> { statusMap.clear(); loadStudentList.run(); });
        searchField.textProperty().addListener((o, ov, nv) -> loadStudentList.run());
        loadStudentList.run();

        // Save button
        HBox saveBar = new HBox(0);
        saveBar.setStyle("-fx-padding: 14px 16px; -fx-border-color: rgba(255,255,255,0.05); -fx-border-width: 1px 0 0 0;");
        saveBar.setAlignment(Pos.CENTER_RIGHT);

        Button saveBtn = UIUtils.primaryBtn("💾  Save Attendance", UIUtils.ACCENT_GREEN);
        saveBtn.setPrefWidth(260);
        saveBtn.setPrefHeight(44);
        saveBtn.setOnAction(e -> {
            if (subjectCombo.getValue() == null) {
                showAlert("Please select a subject before saving.");
                return;
            }
            if (statusMap.isEmpty()) {
                showAlert("No students to mark attendance for.");
                return;
            }
            String date = datePicker.getValue().toString();
            String subject = subjectCombo.getValue();
            String clsVal = "All Classes".equals(classCombo.getValue()) ? null : classCombo.getValue();
            String markedBy = SessionService.getInstance().getCurrentUser().getName();

            // Only mark for loaded students
            String searchText = searchField.getText().trim();
            List<Student> toMark = ds.searchStudents(searchText, clsVal, null);
            Map<String, String> filtered = new HashMap<>();
            for (Student s : toMark) filtered.put(s.getId(), statusMap.getOrDefault(s.getId(), "Present"));

            ds.markAttendance(date, subject, clsVal != null ? clsVal : "all", filtered, markedBy);

            // Success feedback
            saveBtn.setText("✅  Saved Successfully!");
            saveBtn.setStyle(
                "-fx-background-color: " + UIUtils.ACCENT_GREEN + ";" +
                "-fx-text-fill: #050508; -fx-font-weight: bold; -fx-font-size: 13px;" +
                "-fx-background-radius: 10px; -fx-padding: 10px 24px; -fx-cursor: hand;"
            );
            PauseTransition pt = new PauseTransition(Duration.millis(2000));
            pt.setOnFinished(ev -> {
                saveBtn.setText("💾  Save Attendance");
                saveBtn.setStyle(
                    "-fx-background-color: " + UIUtils.ACCENT_GREEN + ";" +
                    "-fx-text-fill: #050508; -fx-font-weight: bold; -fx-font-size: 13px;" +
                    "-fx-background-radius: 10px; -fx-padding: 10px 24px; -fx-cursor: hand;" +
                    "-fx-effect: dropshadow(gaussian, " + UIUtils.ACCENT_GREEN + "66, 16, 0, 0, 4);"
                );
            });
            pt.play();
        });

        saveBar.getChildren().add(saveBtn);
        listCard.getChildren().addAll(toolbar, summaryBar, scroll, saveBar);

        tab.getChildren().addAll(sessionCard, listCard);
        return tab;
    }

    private HBox buildAttendanceStudentRow(Student st, Map<String, String> statusMap, Runnable onUpdate) {
        HBox row = new HBox(0);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 10px 16px; -fx-border-color: rgba(255,255,255,0.03); -fx-border-width: 0 0 1px 0;");
        row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: rgba(255,255,255,0.02); -fx-padding: 10px 16px; -fx-border-color: rgba(255,255,255,0.03); -fx-border-width: 0 0 1px 0;"));
        row.setOnMouseExited(e -> row.setStyle("-fx-padding: 10px 16px; -fx-border-color: rgba(255,255,255,0.03); -fx-border-width: 0 0 1px 0;"));

        // Avatar
        String curStatus = statusMap.getOrDefault(st.getId(), "Present");
        String statusColor = getStatusColor(curStatus);

        StackPane avatar = new StackPane();
        avatar.setPrefSize(36, 36); avatar.setMinSize(36, 36);
        Circle avBg = new Circle(18);
        avBg.setFill(Color.web(statusColor + "22"));
        Label avTxt = new Label(st.getInitials());
        avTxt.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + statusColor + ";");
        avatar.getChildren().addAll(avBg, avTxt);

        VBox info = new VBox(2);
        info.setMinWidth(220);
        Label nameLabel = new Label(st.getName());
        nameLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_PRIMARY + ";");
        Label rollLabel = new Label(st.getRollNumber() + " · " + st.getClassSection());
        rollLabel.setStyle("-fx-font-size: 10.5px; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");
        info.getChildren().addAll(nameLabel, rollLabel);

        Region sp = UIUtils.spacer();

        // Status buttons
        HBox statusBtns = new HBox(6);
        statusBtns.setAlignment(Pos.CENTER_RIGHT);

        Map<String, String> statColors = Map.of(
            "Present", UIUtils.ACCENT_GREEN,
            "Absent",  UIUtils.ACCENT_RED,
            "Late",    UIUtils.ACCENT_YELLOW
        );

        for (String status : new String[]{"Present", "Absent", "Late"}) {
            String sColor = statColors.get(status);
            boolean isActive = status.equals(statusMap.getOrDefault(st.getId(), "Present"));

            Button btn = new Button(status.substring(0, 1));
            btn.setPrefSize(30, 30);
            btn.setMinSize(30, 30);
            setStatusBtnStyle(btn, sColor, isActive);

            btn.setOnAction(e -> {
                statusMap.put(st.getId(), status);
                // Update all buttons in this row
                statusBtns.getChildren().forEach(child -> {
                    if (child instanceof Button b) {
                        String s = switch (statusBtns.getChildren().indexOf(child)) {
                            case 0 -> "Present"; case 1 -> "Absent"; default -> "Late";
                        };
                        setStatusBtnStyle(b, statColors.get(s), s.equals(status));
                    }
                });
                // Update avatar color
                String newColor = getStatusColor(status);
                avBg.setFill(Color.web(newColor + "22"));
                avTxt.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + newColor + ";");
                if (onUpdate != null) onUpdate.run();
            });
            statusBtns.getChildren().add(btn);
        }

        HBox.setMargin(avatar, new Insets(0, 12, 0, 0));
        HBox.setMargin(info, new Insets(0, 0, 0, 0));
        row.getChildren().addAll(avatar, info, sp, statusBtns);
        return row;
    }

    private void setStatusBtnStyle(Button btn, String color, boolean active) {
        if (active) {
            btn.setStyle(
                "-fx-background-color: " + color + "22;" +
                "-fx-text-fill: " + color + ";" +
                "-fx-border-color: " + color + "66;" +
                "-fx-border-radius: 8px;" +
                "-fx-background-radius: 8px;" +
                "-fx-font-size: 11px; -fx-font-weight: bold; -fx-cursor: hand;"
            );
        } else {
            btn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: rgba(255,255,255,0.15);" +
                "-fx-border-color: rgba(255,255,255,0.06);" +
                "-fx-border-radius: 8px; -fx-background-radius: 8px;" +
                "-fx-font-size: 11px; -fx-cursor: hand;"
            );
        }
    }

    private void updateSummary(List<Student> students, Map<String, String> statusMap,
                                Label total, Label present, Label absent, Label late) {
        long p = 0, a = 0, l = 0;
        for (Student s : students) {
            String st = statusMap.getOrDefault(s.getId(), "Present");
            if ("Present".equals(st)) p++;
            else if ("Absent".equals(st)) a++;
            else l++;
        }
        total.setText(students.size() + " students");
        present.setText(p + " Present");
        absent.setText(a + " Absent");
        late.setText(l + " Late");
    }

    // ─── HISTORY TAB ────────────────────────────────────────────────────────

    private VBox buildHistoryTab() {
        VBox tab = new VBox(16);

        // Filter bar
        HBox filterBar = new HBox(10);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setStyle(
            "-fx-background-color: " + UIUtils.BG_CARD + ";" +
            "-fx-border-color: rgba(255,255,255,0.06);" +
            "-fx-border-radius: 14px;" +
            "-fx-background-radius: 14px;" +
            "-fx-padding: 14px 16px;"
        );

        DatePicker filterDate = new DatePicker();
        filterDate.setPromptText("Filter by date");
        filterDate.setStyle("-fx-background-color: #0a0a14; -fx-border-color: rgba(255,255,255,0.07); -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-font-size: 13px;");
        filterDate.setPrefWidth(160);

        ComboBox<String> statusFilter = UIUtils.comboBox("All Status", "Present", "Absent", "Late");
        statusFilter.setValue("All Status"); statusFilter.setPrefWidth(130);

        ComboBox<String> classFilter = UIUtils.comboBox("All Classes", "10", "11", "12");
        classFilter.setValue("All Classes"); classFilter.setPrefWidth(130);

        Button clearBtn = UIUtils.ghostBtn("✕ Clear");

        Region sp = UIUtils.spacer();
        Button exportBtn = UIUtils.ghostBtn("📤 Export CSV");

        filterBar.getChildren().addAll(filterDate, statusFilter, classFilter, clearBtn, sp, exportBtn);

        // Table
        VBox tableCard = new VBox(0);
        tableCard.setStyle(
            "-fx-background-color: " + UIUtils.BG_CARD + ";" +
            "-fx-border-color: rgba(255,255,255,0.06);" +
            "-fx-border-radius: 16px;" +
            "-fx-background-radius: 16px;"
        );

        // Table header
        HBox tableHeader = new HBox(0);
        tableHeader.setStyle("-fx-background-color: rgba(255,255,255,0.025); -fx-padding: 12px 16px; -fx-border-color: rgba(255,255,255,0.04); -fx-border-width: 0 0 1px 0;");
        String[] cols = {"Student", "Date", "Subject", "Status", "Class", "Marked By"};
        double[] widths = {220, 120, 160, 100, 80, 160};
        for (int i = 0; i < cols.length; i++) {
            Label lbl = new Label(cols[i].toUpperCase());
            lbl.setMinWidth(widths[i]); lbl.setPrefWidth(widths[i]);
            lbl.setStyle("-fx-font-size: 9.5px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_MUTED + "; -fx-letter-spacing: 1.2px;");
            tableHeader.getChildren().add(lbl);
        }

        VBox tableBody = new VBox(0);
        ScrollPane scroll = new ScrollPane(tableBody);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(420);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        // Load records
        Runnable loadRecords = () -> {
            String dateVal = filterDate.getValue() != null ? filterDate.getValue().toString() : null;
            String statusVal = "All Status".equals(statusFilter.getValue()) ? null : statusFilter.getValue();
            String classVal = "All Classes".equals(classFilter.getValue()) ? null : classFilter.getValue();

            List<Attendance> records = ds.getAttendances(dateVal, statusVal, classVal);
            tableBody.getChildren().clear();

            if (records.isEmpty()) {
                VBox empty = new VBox(8);
                empty.setAlignment(Pos.CENTER);
                empty.setStyle("-fx-padding: 50px;");
                Label emptyLabel = new Label("📭  No attendance records found");
                emptyLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");
                empty.getChildren().add(emptyLabel);
                tableBody.getChildren().add(empty);
                return;
            }

            // Limit display
            List<Attendance> display = records.subList(0, Math.min(100, records.size()));
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy");

            for (int i = 0; i < display.size(); i++) {
                Attendance a = display.get(i);
                HBox row = new HBox(0);
                boolean even = i % 2 == 0;
                row.setStyle(
                    "-fx-background-color: " + (even ? "transparent" : "rgba(255,255,255,0.012)") + ";" +
                    "-fx-padding: 11px 16px;" +
                    "-fx-border-color: rgba(255,255,255,0.03);" +
                    "-fx-border-width: 0 0 1px 0;"
                );
                row.setOnMouseEntered(ev -> row.setStyle(
                    "-fx-background-color: rgba(255,255,255,0.035);" +
                    "-fx-padding: 11px 16px;" +
                    "-fx-border-color: rgba(255,255,255,0.03);" +
                    "-fx-border-width: 0 0 1px 0;"
                ));
                row.setOnMouseExited(ev -> row.setStyle(
                    "-fx-background-color: " + (even ? "transparent" : "rgba(255,255,255,0.012)") + ";" +
                    "-fx-padding: 11px 16px;" +
                    "-fx-border-color: rgba(255,255,255,0.03);" +
                    "-fx-border-width: 0 0 1px 0;"
                ));

                // Student name + roll
                VBox nameCell = new VBox(2);
                nameCell.setMinWidth(220); nameCell.setPrefWidth(220);
                Label nameLabel = new Label(a.getStudentName() != null ? a.getStudentName() : "—");
                nameLabel.setStyle("-fx-font-size: 12.5px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_PRIMARY + ";");
                Label rollLabel = new Label(a.getRollNumber() != null ? a.getRollNumber() : "—");
                rollLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");
                nameCell.getChildren().addAll(nameLabel, rollLabel);

                // Date
                Label dateLabel = mkCell(formatDate(a.getDate(), fmt), 120);

                // Subject
                Label subjectLabel = mkCell(a.getSubject() != null ? a.getSubject() : "—", 160);

                // Status badge
                HBox statusCell = new HBox(0);
                statusCell.setMinWidth(100); statusCell.setPrefWidth(100);
                statusCell.setAlignment(Pos.CENTER_LEFT);
                statusCell.getChildren().add(UIUtils.statusBadge(a.getStatus() != null ? a.getStatus() : "—"));

                // Class
                Label classLabel = mkCell(a.getClassName() != null ? "Class " + a.getClassName() : "—", 80);

                // Marked by
                Label markedByLabel = mkCell(a.getMarkedBy() != null ? a.getMarkedBy() : "System", 160);
                markedByLabel.setStyle("-fx-font-size: 11.5px; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");

                row.getChildren().addAll(nameCell, dateLabel, subjectLabel, statusCell, classLabel, markedByLabel);
                tableBody.getChildren().add(row);
            }

            if (records.size() > 100) {
                Label moreLabel = new Label("  Showing 100 of " + records.size() + " records. Use filters to narrow results.");
                moreLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + UIUtils.TEXT_MUTED + "; -fx-padding: 10px 16px;");
                tableBody.getChildren().add(moreLabel);
            }
        };

        filterDate.setOnAction(e -> loadRecords.run());
        statusFilter.setOnAction(e -> loadRecords.run());
        classFilter.setOnAction(e -> loadRecords.run());
        clearBtn.setOnAction(e -> { filterDate.setValue(null); statusFilter.setValue("All Status"); classFilter.setValue("All Classes"); loadRecords.run(); });

        exportBtn.setOnAction(e -> {
            List<Attendance> all = ds.getAttendances(null, null, null);
            exportAttendanceCSV(all);
        });

        loadRecords.run();
        tableCard.getChildren().addAll(tableHeader, scroll);
        tab.getChildren().addAll(filterBar, tableCard);
        return tab;
    }

    private Label mkCell(String text, double width) {
        Label l = new Label(text);
        l.setMinWidth(width); l.setPrefWidth(width);
        l.setStyle("-fx-font-size: 12.5px; -fx-text-fill: " + UIUtils.TEXT_SECONDARY + ";");
        return l;
    }

    private Label mkFieldLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 9.5px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_MUTED + "; -fx-letter-spacing: 1px;");
        return l;
    }

    private String getStatusColor(String status) {
        return switch (status) {
            case "Present" -> UIUtils.ACCENT_GREEN;
            case "Absent"  -> UIUtils.ACCENT_RED;
            case "Late"    -> UIUtils.ACCENT_YELLOW;
            default        -> UIUtils.TEXT_MUTED;
        };
    }

    private String formatDate(String date, DateTimeFormatter fmt) {
        if (date == null) return "—";
        try { return LocalDate.parse(date).format(fmt); }
        catch (Exception e) { return date; }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attention"); alert.setHeaderText(null); alert.setContentText(msg);
        alert.showAndWait();
    }

    private void exportAttendanceCSV(List<Attendance> records) {
        StringBuilder csv = new StringBuilder("Student,Roll Number,Date,Subject,Status,Class,Marked By\n");
        for (Attendance a : records) {
            csv.append(String.join(",",
                safe(a.getStudentName()), safe(a.getRollNumber()), safe(a.getDate()),
                safe(a.getSubject()), safe(a.getStatus()), safe(a.getClassName()), safe(a.getMarkedBy())
            )).append("\n");
        }
        try {
            java.io.FileWriter fw = new java.io.FileWriter(System.getProperty("user.home") + "/attendance_export.csv");
            fw.write(csv.toString()); fw.close();
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Export Successful"); info.setHeaderText("CSV exported");
            info.setContentText("File: ~/attendance_export.csv"); info.showAndWait();
        } catch (Exception e) { System.err.println("Export error: " + e.getMessage()); }
    }

    private String safe(String s) { return s != null ? s.replace(",", ";") : ""; }

    public VBox getRoot() { return root; }
}
