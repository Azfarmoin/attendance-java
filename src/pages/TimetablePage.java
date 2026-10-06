package pages;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import utils.UIUtils;

/**
 * TimetablePage - Weekly class schedule display
 */
public class TimetablePage {
    private VBox root;

    // Timetable data: [day][period] = {subject, teacher, room}
    private final String[][][] SCHEDULE = {
        // Monday
        {{"Mathematics","Ms. Sarah","R-101"},{"Physics","Mr. Ali","R-201"},{"Break","—","—"},{"Chemistry","Ms. Sarah","Lab-1"},{"English","Mr. Ali","R-101"},{"Urdu","—","R-202"},{"Computer Sc.","Mr. Ali","Lab-2"}},
        // Tuesday
        {{"Physics","Mr. Ali","R-201"},{"Mathematics","Ms. Sarah","R-101"},{"Break","—","—"},{"English","Mr. Ali","R-101"},{"Chemistry","Ms. Sarah","Lab-1"},{"Computer Sc.","Mr. Ali","Lab-2"},{"Biology","—","Lab-3"}},
        // Wednesday
        {{"Chemistry","Ms. Sarah","Lab-1"},{"Computer Sc.","Mr. Ali","Lab-2"},{"Break","—","—"},{"Mathematics","Ms. Sarah","R-101"},{"Physics","Mr. Ali","R-201"},{"Biology","—","Lab-3"},{"Urdu","—","R-202"}},
        // Thursday
        {{"English","Mr. Ali","R-101"},{"Urdu","—","R-202"},{"Break","—","—"},{"Physics","Mr. Ali","R-201"},{"Mathematics","Ms. Sarah","R-101"},{"Chemistry","Ms. Sarah","Lab-1"},{"Computer Sc.","Mr. Ali","Lab-2"}},
        // Friday
        {{"Computer Sc.","Mr. Ali","Lab-2"},{"Biology","—","Lab-3"},{"Break","—","—"},{"Urdu","—","R-202"},{"English","Mr. Ali","R-101"},{"Mathematics","Ms. Sarah","R-101"},{"Physics","Mr. Ali","R-201"}},
    };

    private final String[] DAYS    = {"Monday","Tuesday","Wednesday","Thursday","Friday"};
    private final String[] PERIODS = {"8:00","9:00","10:00","Break","11:00","12:00","1:00","2:00"};
    private final String[] SUBJECT_COLORS = {
        UIUtils.ACCENT_BLUE, UIUtils.ACCENT_GREEN, UIUtils.ACCENT_PURPLE,
        UIUtils.ACCENT_YELLOW, UIUtils.ACCENT_ORANGE, UIUtils.ACCENT_CYAN,
        UIUtils.ACCENT_PINK, UIUtils.ACCENT_RED
    };

    public TimetablePage() {
        root = new VBox(20);
        root.setStyle("-fx-padding: 24px; -fx-background-color: " + UIUtils.BG_DARK + ";");
        buildUI();
        UIUtils.fadeIn(root);
    }

    private void buildUI() {
        // Header
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        VBox left = new VBox(4);
        Label title = UIUtils.heading("⏰  Timetable");
        Label sub = UIUtils.subheading("Weekly class schedule — Academic Year 2024");
        left.getChildren().addAll(title, sub);
        Region sp = UIUtils.spacer();

        // Current day badge
        String today = java.time.LocalDate.now().getDayOfWeek().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH);
        boolean isWeekday = !today.equals("Saturday") && !today.equals("Sunday");
        Label todayBadge = UIUtils.badge("Today: " + today, isWeekday ? UIUtils.ACCENT_GREEN : UIUtils.TEXT_MUTED);

        header.getChildren().addAll(left, sp, todayBadge);
        root.getChildren().add(header);

        // Legend
        HBox legend = new HBox(14);
        legend.setAlignment(Pos.CENTER_LEFT);
        String[] subjects = {"Mathematics","Physics","Chemistry","English","Urdu","Computer Sc.","Biology"};
        String[] lColors  = {UIUtils.ACCENT_BLUE, UIUtils.ACCENT_GREEN, UIUtils.ACCENT_PURPLE, UIUtils.ACCENT_ORANGE, UIUtils.ACCENT_CYAN, UIUtils.ACCENT_PINK, UIUtils.ACCENT_YELLOW};
        for (int i = 0; i < subjects.length; i++) {
            HBox item = new HBox(5);
            item.setAlignment(Pos.CENTER_LEFT);
            Circle dot = new Circle(5, Color.web(lColors[i]));
            Label lbl = new Label(subjects[i]);
            lbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");
            item.getChildren().addAll(dot, lbl);
            legend.getChildren().add(item);
        }
        root.getChildren().add(legend);

        // Timetable grid
        ScrollPane scroll = new ScrollPane(buildGrid());
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        root.getChildren().add(scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);
    }

    private GridPane buildGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.setPadding(new Insets(8));

        // Time column header (top-left corner)
        Label cornerLabel = new Label("Day / Time");
        cornerLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: " + UIUtils.TEXT_MUTED + "; -fx-padding: 10px 12px;");
        grid.add(cornerLabel, 0, 0);

        // Period headers
        String[] displayPeriods = {"8:00–9:00","9:00–10:00","10:00–10:30","10:30–11:30","11:30–12:30","12:30–1:30","1:30–2:30"};
        for (int p = 0; p < displayPeriods.length; p++) {
            VBox ph = new VBox(2);
            ph.setAlignment(Pos.CENTER);
            ph.setStyle("-fx-padding: 8px 6px;");
            boolean isBreak = displayPeriods[p].equals("10:00–10:30");
            Label periodLabel = new Label(isBreak ? "BREAK" : "P" + (p + (p >= 2 ? 0 : 1)));
            periodLabel.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: " + (isBreak ? UIUtils.ACCENT_YELLOW : UIUtils.TEXT_MUTED) + "; -fx-letter-spacing: 1px;");
            Label timeLabel = new Label(displayPeriods[p]);
            timeLabel.setStyle("-fx-font-size: 10.5px; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");
            ph.getChildren().addAll(periodLabel, timeLabel);
            grid.add(ph, p + 1, 0);
        }

        // Day rows
        String todayName = java.time.LocalDate.now().getDayOfWeek().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH);

        for (int d = 0; d < DAYS.length; d++) {
            boolean isToday = DAYS[d].equalsIgnoreCase(todayName);

            // Day label
            VBox dayBox = new VBox(4);
            dayBox.setAlignment(Pos.CENTER);
            dayBox.setPrefWidth(90); dayBox.setMinWidth(90);
            dayBox.setStyle(
                "-fx-background-color: " + (isToday ? UIUtils.ACCENT_GREEN + "18" : UIUtils.BG_CARD) + ";" +
                "-fx-border-color: " + (isToday ? UIUtils.ACCENT_GREEN + "44" : "rgba(255,255,255,0.06)") + ";" +
                "-fx-border-radius: 12px; -fx-background-radius: 12px;" +
                "-fx-padding: 12px 8px;"
            );
            Label dayLabel = new Label(DAYS[d]);
            dayLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + (isToday ? UIUtils.ACCENT_GREEN : UIUtils.TEXT_PRIMARY) + ";");
            dayBox.getChildren().add(dayLabel);
            if (isToday) {
                Label todayMark = new Label("Today");
                todayMark.setStyle("-fx-font-size: 9px; -fx-text-fill: " + UIUtils.ACCENT_GREEN + ";");
                dayBox.getChildren().add(todayMark);
            }
            grid.add(dayBox, 0, d + 1);

            // Period cells
            for (int p = 0; p < SCHEDULE[d].length; p++) {
                String[] cell = SCHEDULE[d][p];
                String subject = cell[0];
                String teacher = cell[1];
                String room    = cell[2];

                VBox cellBox = buildCell(subject, teacher, room, isToday);
                grid.add(cellBox, p + 1, d + 1);
            }
        }

        // Column constraints
        for (int i = 0; i < 8; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setMinWidth(i == 0 ? 90 : 140);
            cc.setPrefWidth(i == 0 ? 90 : 160);
            grid.getColumnConstraints().add(cc);
        }

        return grid;
    }

    private VBox buildCell(String subject, String teacher, String room, boolean isToday) {
        VBox cell = new VBox(4);
        cell.setAlignment(Pos.TOP_LEFT);
        cell.setPrefHeight(80); cell.setMinHeight(80);

        boolean isBreak = "Break".equals(subject);
        String color = isBreak ? UIUtils.ACCENT_YELLOW : getSubjectColor(subject);

        cell.setStyle(
            "-fx-background-color: " + (isBreak ? UIUtils.ACCENT_YELLOW + "0a" : color + "12") + ";" +
            "-fx-border-color: " + (isBreak ? UIUtils.ACCENT_YELLOW + "30" : color + "33") + ";" +
            "-fx-border-radius: 12px; -fx-background-radius: 12px;" +
            "-fx-padding: 10px 10px;"
        );

        if (isBreak) {
            Label breakLabel = new Label("☕ Break");
            breakLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + UIUtils.ACCENT_YELLOW + "; -fx-font-weight: bold;");
            cell.getChildren().add(breakLabel);
            cell.setAlignment(Pos.CENTER);
        } else {
            Label subjectLabel = new Label(subject);
            subjectLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
            subjectLabel.setWrapText(true);

            Label teacherLabel = new Label("👤 " + teacher);
            teacherLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");

            Label roomLabel = new Label("📍 " + room);
            roomLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");

            cell.getChildren().addAll(subjectLabel, teacherLabel, roomLabel);
        }

        // Hover effect
        cell.setOnMouseEntered(e -> {
            if (!isBreak) {
                cell.setStyle(
                    "-fx-background-color: " + color + "22;" +
                    "-fx-border-color: " + color + "66;" +
                    "-fx-border-radius: 12px; -fx-background-radius: 12px;" +
                    "-fx-padding: 10px 10px;" +
                    "-fx-effect: dropshadow(gaussian, " + color + "44, 14, 0, 0, 3);"
                );
            }
        });
        cell.setOnMouseExited(e -> {
            cell.setStyle(
                "-fx-background-color: " + (isBreak ? UIUtils.ACCENT_YELLOW + "0a" : color + "12") + ";" +
                "-fx-border-color: " + (isBreak ? UIUtils.ACCENT_YELLOW + "30" : color + "33") + ";" +
                "-fx-border-radius: 12px; -fx-background-radius: 12px;" +
                "-fx-padding: 10px 10px;"
            );
        });

        return cell;
    }

    private String getSubjectColor(String subject) {
        return switch (subject) {
            case "Mathematics"  -> UIUtils.ACCENT_BLUE;
            case "Physics"      -> UIUtils.ACCENT_GREEN;
            case "Chemistry"    -> UIUtils.ACCENT_PURPLE;
            case "English"      -> UIUtils.ACCENT_ORANGE;
            case "Urdu"         -> UIUtils.ACCENT_CYAN;
            case "Computer Sc." -> UIUtils.ACCENT_PINK;
            case "Biology"      -> UIUtils.ACCENT_YELLOW;
            default             -> UIUtils.TEXT_MUTED;
        };
    }

    public VBox getRoot() { return root; }
}
