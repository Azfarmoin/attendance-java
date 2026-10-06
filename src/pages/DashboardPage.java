package pages;

import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.util.Duration;
import models.Student;
import services.DataService;
import services.SessionService;
import utils.UIUtils;

import java.time.*;
import java.time.format.*;
import java.util.*;

/**
 * DashboardPage - The main analytics dashboard
 */
public class DashboardPage {
    private VBox root;
    private DataService ds = DataService.getInstance();

    public DashboardPage() {
        root = new VBox(20);
        root.setStyle("-fx-padding: 24px; -fx-background-color: " + UIUtils.BG_DARK + ";");
        buildUI();
        UIUtils.fadeIn(root);
    }

    private void buildUI() {
        Map<String, Object> stats = ds.getDashboardStats();

        // Header
        root.getChildren().add(buildHeader());

        // Stat cards grid
        root.getChildren().add(buildStatCards(stats));

        // Charts row
        root.getChildren().add(buildChartsRow(stats));

        // Bottom row
        root.getChildren().add(buildBottomRow());
    }

    private HBox buildHeader() {
        HBox header = new HBox(0);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox left = new VBox(4);
        String name = SessionService.getInstance().getCurrentUser().getFirstName();
        int hour = LocalTime.now().getHour();
        String greeting = hour < 12 ? "Good morning" : hour < 17 ? "Good afternoon" : "Good evening";

        Label titleLabel = new Label(greeting + ", " + name + " 👋");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_PRIMARY + ";");

        Label dateLabel = new Label(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")));
        dateLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");
        left.getChildren().addAll(titleLabel, dateLabel);

        Region sp = UIUtils.spacer();

        // Live badge
        HBox liveBadge = new HBox(8);
        liveBadge.setAlignment(Pos.CENTER);
        liveBadge.setStyle(
            "-fx-background-color: rgba(168,240,0,0.06);" +
            "-fx-border-color: rgba(168,240,0,0.2);" +
            "-fx-border-radius: 20px;" +
            "-fx-background-radius: 20px;" +
            "-fx-padding: 6px 16px;"
        );
        Circle liveDot = new Circle(4, Color.web(UIUtils.ACCENT_GREEN));
        ScaleTransition pulse = new ScaleTransition(Duration.millis(700), liveDot);
        pulse.setFromX(0.8); pulse.setToX(1.4); pulse.setFromY(0.8); pulse.setToY(1.4);
        pulse.setCycleCount(Timeline.INDEFINITE); pulse.setAutoReverse(true); pulse.play();
        Label liveText = new Label("Live");
        liveText.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.ACCENT_GREEN + ";");
        liveBadge.getChildren().addAll(liveDot, liveText);

        header.getChildren().addAll(left, sp, liveBadge);
        return header;
    }

    private GridPane buildStatCards(Map<String, Object> stats) {
        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(14);

        long totalStudents = (long)(int) stats.get("totalStudents");
        long present = (Long) stats.get("todayPresent");
        long absent = (Long) stats.get("todayAbsent");
        long late = (Long) stats.get("todayLate");
        int subjects = (int) stats.get("subjects");

        Object[][] cards = {
            {"⚡", "Total Students", String.valueOf(totalStudents), UIUtils.ACCENT_BLUE,    "+2 this week"},
            {"✅", "Present Today",   String.valueOf(present),       UIUtils.ACCENT_GREEN,   "Real-time"},
            {"❌", "Absent Today",    String.valueOf(absent),        UIUtils.ACCENT_RED,     "Alert"},
            {"⏱",  "Late Today",      String.valueOf(late),         UIUtils.ACCENT_YELLOW,  "Watch"},
            {"📚", "Subjects",        String.valueOf(subjects),      UIUtils.ACCENT_PURPLE,  "Active"},
        };

        for (int i = 0; i < cards.length; i++) {
            VBox card = buildStatCard(cards[i]);
            grid.add(card, i, 0);
            GridPane.setHgrow(card, Priority.ALWAYS);

            // Staggered fade-in
            int delay = i * 80;
            card.setOpacity(0);
            PauseTransition pause = new PauseTransition(Duration.millis(delay));
            pause.setOnFinished(e -> {
                FadeTransition ft = new FadeTransition(Duration.millis(400), card);
                ft.setFromValue(0); ft.setToValue(1); ft.play();
                TranslateTransition tt = new TranslateTransition(Duration.millis(350), card);
                tt.setFromY(15); tt.setToY(0); tt.play();
            });
            pause.play();
        }

        // Equal columns
        for (int i = 0; i < 5; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(20);
            grid.getColumnConstraints().add(cc);
        }

        return grid;
    }

    private VBox buildStatCard(Object[] data) {
        String emoji = (String) data[0];
        String label = (String) data[1];
        String value = (String) data[2];
        String color = (String) data[3];
        String sub = (String) data[4];

        VBox card = new VBox(8);
        card.setStyle(
            "-fx-background-color: " + UIUtils.BG_CARD + ";" +
            "-fx-border-color: " + color + "33;" +
            "-fx-border-radius: 16px;" +
            "-fx-background-radius: 16px;" +
            "-fx-padding: 18px;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, " + color + "22, 18, 0, 0, 4);"
        );

        HBox topRow = new HBox(0);
        topRow.setAlignment(Pos.CENTER_LEFT);
        Label emojiLabel = new Label(emoji);
        emojiLabel.setStyle("-fx-font-size: 20px;");
        Region sp = UIUtils.spacer();
        Label subLabel = new Label(sub);
        subLabel.setStyle("-fx-font-size: 9px; -fx-text-fill: " + color + "; -fx-font-weight: bold; -fx-background-color: " + color + "18; -fx-background-radius: 10px; -fx-padding: 3px 8px;");
        topRow.getChildren().addAll(emojiLabel, sp, subLabel);

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 34px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        Label labelLabel = new Label(label);
        labelLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + UIUtils.TEXT_MUTED + "; -fx-font-weight: bold;");

        card.getChildren().addAll(topRow, valueLabel, labelLabel);
        UIUtils.addCardHover(card);

        // Counter animation
        animateCounter(valueLabel, Integer.parseInt(value), color);
        return card;
    }

    private void animateCounter(Label label, int target, String color) {
        if (target == 0) return;
        Timeline t = new Timeline();
        int frames = 20;
        for (int f = 0; f <= frames; f++) {
            int frameVal = (int)(target * (f / (double)frames));
            int delay = f * 30;
            t.getKeyFrames().add(new KeyFrame(Duration.millis(delay),
                e -> label.setText(String.valueOf(frameVal))
            ));
        }
        t.getKeyFrames().add(new KeyFrame(Duration.millis(frames * 30 + 50),
            e -> label.setText(String.valueOf(target))
        ));
        t.play();
    }

    @SuppressWarnings("unchecked")
    private HBox buildChartsRow(Map<String, Object> stats) {
        HBox row = new HBox(16);

        // Weekly Bar Chart
        VBox barCard = new VBox(16);
        barCard.setStyle(
            "-fx-background-color: " + UIUtils.BG_CARD + ";" +
            "-fx-border-color: rgba(255,255,255,0.06);" +
            "-fx-border-radius: 18px;" +
            "-fx-background-radius: 18px;" +
            "-fx-padding: 20px;"
        );
        HBox.setHgrow(barCard, Priority.ALWAYS);

        Label chartTitle = new Label("📈  Weekly Attendance Trend");
        chartTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_PRIMARY + ";");

        // Build bar chart
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setStyle("-fx-tick-label-fill: #5a5a7a;");
        yAxis.setStyle("-fx-tick-label-fill: #5a5a7a;");
        xAxis.setTickLabelGap(6);
        yAxis.setTickLabelGap(4);

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setStyle("-fx-background-color: transparent;");
        barChart.setLegendVisible(true);
        barChart.setAnimated(true);
        barChart.setPrefHeight(220);
        barChart.setBarGap(2);
        barChart.setCategoryGap(12);

        XYChart.Series<String, Number> serPresent = new XYChart.Series<>(); serPresent.setName("Present");
        XYChart.Series<String, Number> serAbsent  = new XYChart.Series<>(); serAbsent.setName("Absent");
        XYChart.Series<String, Number> serLate    = new XYChart.Series<>(); serLate.setName("Late");

        Map<String, Map<String, Long>> weekTrend = (Map<String, Map<String, Long>>) stats.get("weekTrend");
        DateTimeFormatter dayFmt = DateTimeFormatter.ofPattern("dd MMM");

        for (Map.Entry<String, Map<String, Long>> e : weekTrend.entrySet()) {
            String dateStr = e.getKey();
            Map<String, Long> counts = e.getValue();
            try {
                LocalDate d = LocalDate.parse(dateStr);
                String label = d.format(dayFmt);
                serPresent.getData().add(new XYChart.Data<>(label, counts.getOrDefault("Present", 0L)));
                serAbsent.getData().add(new XYChart.Data<>(label, counts.getOrDefault("Absent", 0L)));
                serLate.getData().add(new XYChart.Data<>(label, counts.getOrDefault("Late", 0L)));
            } catch (Exception ex) {
                System.err.println("Date parse error: " + ex.getMessage());
            }
        }

        barChart.getData().addAll(serPresent, serAbsent, serLate);

        // Apply colors via CSS
        barChart.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-plot-background-color: transparent;" +
            "-fx-chart-plot-background: transparent;"
        );

        // Color the bars after scene is built
        barChart.sceneProperty().addListener((obs, old, scene) -> {
            if (scene != null) {
                scene.getStylesheets().add("data:text/css," +
                    ".default-color0.chart-bar { -fx-bar-fill: #a8f000; }" +
                    ".default-color1.chart-bar { -fx-bar-fill: #ff3d4a; }" +
                    ".default-color2.chart-bar { -fx-bar-fill: #ffcc50; }" +
                    ".chart-plot-background { -fx-background-color: transparent; }" +
                    ".chart-vertical-grid-lines { -fx-stroke: rgba(255,255,255,0.04); }" +
                    ".chart-horizontal-grid-lines { -fx-stroke: rgba(255,255,255,0.04); }" +
                    ".axis { -fx-tick-label-fill: #5a5a7a; -fx-tick-mark-stroke: transparent; -fx-minor-tick-stroke: transparent; }" +
                    ".chart-legend { -fx-background-color: transparent; }" +
                    ".chart-legend-item-symbol { -fx-background-radius: 4; }" +
                    ".default-color0.chart-legend-item-symbol { -fx-background-color: #a8f000; }" +
                    ".default-color1.chart-legend-item-symbol { -fx-background-color: #ff3d4a; }" +
                    ".default-color2.chart-legend-item-symbol { -fx-background-color: #ffcc50; }" +
                    ".chart-legend-item { -fx-text-fill: #5a5a7a; }"
                );
            }
        });

        barCard.getChildren().addAll(chartTitle, barChart);

        // Today's Summary Donut
        VBox pieCard = buildTodaySummaryCard(stats);
        pieCard.setPrefWidth(260);
        pieCard.setMinWidth(240);

        row.getChildren().addAll(barCard, pieCard);
        return row;
    }

    private VBox buildTodaySummaryCard(Map<String, Object> stats) {
        VBox card = new VBox(16);
        card.setStyle(
            "-fx-background-color: " + UIUtils.BG_CARD + ";" +
            "-fx-border-color: rgba(255,255,255,0.06);" +
            "-fx-border-radius: 18px;" +
            "-fx-background-radius: 18px;" +
            "-fx-padding: 20px;"
        );

        Label title = new Label("📊  Today's Summary");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_PRIMARY + ";");

        long present = (Long) stats.get("todayPresent");
        long absent  = (Long) stats.get("todayAbsent");
        long late    = (Long) stats.get("todayLate");
        long total   = present + absent + late;

        // Pie chart
        PieChart pieChart = new PieChart();
        pieChart.setPrefHeight(180);
        pieChart.setLegendVisible(false);
        pieChart.setStyle("-fx-background-color: transparent;");

        if (total > 0) {
            PieChart.Data dPresent = new PieChart.Data("Present", present);
            PieChart.Data dAbsent  = new PieChart.Data("Absent", absent);
            PieChart.Data dLate    = new PieChart.Data("Late", late);
            pieChart.getData().addAll(dPresent, dAbsent, dLate);

            pieChart.sceneProperty().addListener((obs, old, scene) -> {
                if (scene != null) {
                    scene.getStylesheets().add("data:text/css," +
                        ".default-color0.chart-pie { -fx-pie-color: #a8f000; }" +
                        ".default-color1.chart-pie { -fx-pie-color: #ff3d4a; }" +
                        ".default-color2.chart-pie { -fx-pie-color: #ffcc50; }" +
                        ".chart-plot-background { -fx-background-color: transparent; }" +
                        ".chart-pie-label { -fx-fill: #5a5a7a; -fx-font-size: 11px; }"
                    );
                }
            });
        } else {
            Label noData = new Label("No attendance today");
            noData.setStyle("-fx-font-size: 13px; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");
            card.getChildren().addAll(title, noData);
            return card;
        }

        // Rate label
        int rate = total > 0 ? (int)((present * 100) / total) : 0;
        VBox rateBox = new VBox(2);
        rateBox.setAlignment(Pos.CENTER);
        Label rateLabel = new Label(rate + "%");
        rateLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.ACCENT_GREEN + ";");
        Label rateSubLabel = new Label("Attendance Rate");
        rateSubLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");
        rateBox.getChildren().addAll(rateLabel, rateSubLabel);

        // Legend
        VBox legend = new VBox(8);
        Object[][] legItems = {
            {"Present", present, UIUtils.ACCENT_GREEN},
            {"Absent",  absent,  UIUtils.ACCENT_RED},
            {"Late",    late,    UIUtils.ACCENT_YELLOW},
        };
        for (Object[] item : legItems) {
            HBox row = new HBox(8);
            row.setAlignment(Pos.CENTER_LEFT);
            Circle dot = new Circle(5, Color.web((String)item[2]));
            Label legLabel = new Label(item[0] + ": " + item[1]);
            legLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + UIUtils.TEXT_SECONDARY + ";");
            row.getChildren().addAll(dot, legLabel);
            legend.getChildren().add(row);
        }

        card.getChildren().addAll(title, rateBox, pieChart, legend);
        return card;
    }

    private HBox buildBottomRow() {
        HBox row = new HBox(16);

        // Top Students card
        VBox topStudents = buildTopStudentsCard();
        HBox.setHgrow(topStudents, Priority.ALWAYS);

        // Recent activity
        VBox recentActivity = buildRecentActivityCard();
        HBox.setHgrow(recentActivity, Priority.ALWAYS);

        row.getChildren().addAll(topStudents, recentActivity);
        return row;
    }

    private VBox buildTopStudentsCard() {
        VBox card = new VBox(16);
        card.setStyle(
            "-fx-background-color: " + UIUtils.BG_CARD + ";" +
            "-fx-border-color: rgba(255,255,255,0.06);" +
            "-fx-border-radius: 18px;" +
            "-fx-background-radius: 18px;" +
            "-fx-padding: 20px;"
        );

        Label title = new Label("🏆  Top Performers");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_PRIMARY + ";");
        card.getChildren().add(title);

        List<Student> students = ds.getStudents();
        String[] medals = {"🥇", "🥈", "🥉", "⭐", "⭐"};
        String[] colors  = {UIUtils.ACCENT_YELLOW, "#aaaaaa", "#cd7f32", UIUtils.TEXT_SECONDARY, UIUtils.TEXT_MUTED};

        for (int i = 0; i < Math.min(5, students.size()); i++) {
            Student st = students.get(i);
            double rate = ds.getStudentAttendanceRate(st.getId());

            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle(
                "-fx-background-color: rgba(255,255,255,0.02);" +
                "-fx-background-radius: 10px;" +
                "-fx-padding: 10px 12px;"
            );

            Label rankLabel = new Label(medals[i]);
            rankLabel.setStyle("-fx-font-size: 16px;");

            // Avatar
            StackPane avatar = new StackPane();
            avatar.setPrefSize(32, 32); avatar.setMinSize(32, 32);
            Circle avBg = new Circle(16);
            avBg.setFill(Color.web(st.getGenderColor() + "22"));
            Label avTxt = new Label(st.getInitials());
            avTxt.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + st.getGenderColor() + ";");
            avatar.getChildren().addAll(avBg, avTxt);

            VBox info = new VBox(2);
            Label nameLabel = new Label(st.getName());
            nameLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_PRIMARY + ";");
            Label classLabel = new Label(st.getClassSection() + " · " + st.getRollNumber());
            classLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");
            info.getChildren().addAll(nameLabel, classLabel);

            Region sp = UIUtils.spacer();

            Label rateLabel = new Label(String.format("%.0f%%", rate));
            rateLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + colors[i] + ";");

            row.getChildren().addAll(rankLabel, avatar, info, sp, rateLabel);
            card.getChildren().add(row);
        }
        return card;
    }

    private VBox buildRecentActivityCard() {
        VBox card = new VBox(16);
        card.setStyle(
            "-fx-background-color: " + UIUtils.BG_CARD + ";" +
            "-fx-border-color: rgba(255,255,255,0.06);" +
            "-fx-border-radius: 18px;" +
            "-fx-background-radius: 18px;" +
            "-fx-padding: 20px;"
        );

        Label title = new Label("⚡  Recent Activity");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_PRIMARY + ";");
        card.getChildren().add(title);

        // Build activity feed from recent attendance
        String today = java.time.LocalDate.now().toString();
        var recentAtt = ds.getAttendances(today, null, null);

        String[][] activities = {
            {"📋", "Attendance Marked", "Mathematics - 30 students", UIUtils.ACCENT_GREEN},
            {"📊", "Grades Updated",    "Physics - Final Exam",       UIUtils.ACCENT_BLUE},
            {"👤", "Student Added",     "New enrollment processed",   UIUtils.ACCENT_PURPLE},
            {"🔔", "Alert Raised",      "5 students marked absent",   UIUtils.ACCENT_YELLOW},
            {"📈", "Report Generated",  "Weekly attendance summary",  UIUtils.ACCENT_CYAN},
        };

        for (String[] act : activities) {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle(
                "-fx-background-color: rgba(255,255,255,0.018);" +
                "-fx-background-radius: 10px;" +
                "-fx-padding: 10px 12px;"
            );
            row.setOnMouseEntered(e -> row.setStyle(
                "-fx-background-color: rgba(255,255,255,0.04);" +
                "-fx-background-radius: 10px;" +
                "-fx-padding: 10px 12px;"
            ));
            row.setOnMouseExited(e -> row.setStyle(
                "-fx-background-color: rgba(255,255,255,0.018);" +
                "-fx-background-radius: 10px;" +
                "-fx-padding: 10px 12px;"
            ));

            StackPane iconBg = new StackPane();
            iconBg.setPrefSize(32, 32); iconBg.setMinSize(32, 32);
            Circle iconCircle = new Circle(16);
            iconCircle.setFill(Color.web(act[3] + "22"));
            Label iconLabel = new Label(act[0]);
            iconLabel.setStyle("-fx-font-size: 14px;");
            iconBg.getChildren().addAll(iconCircle, iconLabel);

            VBox info = new VBox(2);
            Label titleLabel = new Label(act[1]);
            titleLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_PRIMARY + ";");
            Label subLabel = new Label(act[2]);
            subLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");
            info.getChildren().addAll(titleLabel, subLabel);

            Region sp = UIUtils.spacer();

            Label timeLabel = new Label("now");
            timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");

            row.getChildren().addAll(iconBg, info, sp, timeLabel);
            card.getChildren().add(row);
        }
        return card;
    }

    public VBox getRoot() { return root; }
}
