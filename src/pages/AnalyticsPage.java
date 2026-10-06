package pages;

import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.util.Duration;
import models.Attendance;
import models.Grade;
import models.Student;
import services.DataService;
import utils.UIUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AnalyticsPage - Visual analytics and insights dashboard
 */
public class AnalyticsPage {
    private VBox root;
    private DataService ds = DataService.getInstance();

    public AnalyticsPage() {
        root = new VBox(20);
        root.setStyle("-fx-padding: 24px; -fx-background-color: " + UIUtils.BG_DARK + ";");
        buildUI();
        UIUtils.fadeIn(root);
    }

    private void buildUI() {
        // Header
        VBox header = new VBox(4);
        Label title = UIUtils.heading("📊  Analytics");
        Label sub = UIUtils.subheading("Deep insights into attendance trends, performance metrics, and student data");
        header.getChildren().addAll(title, sub);
        root.getChildren().add(header);

        // Top metric cards
        root.getChildren().add(buildMetricCards());

        // Charts row 1
        root.getChildren().add(buildChartsRow1());

        // Charts row 2
        root.getChildren().add(buildChartsRow2());

        // Student performance table
        root.getChildren().add(buildTopPerformersSection());
    }

    private HBox buildMetricCards() {
        HBox row = new HBox(14);
        List<Student> students = ds.getStudents();
        List<Attendance> allAtt = ds.getAttendances();
        List<Grade> allGrades = ds.getGrades();

        // Overall attendance rate
        long totalPresent = allAtt.stream().filter(a -> "Present".equals(a.getStatus())).count();
        double attRate = allAtt.isEmpty() ? 0 : (totalPresent * 100.0) / allAtt.size();

        // Average GPA
        double avgGpa = allGrades.stream().mapToDouble(Grade::getGpaPoints).average().orElse(0);

        // Pass rate
        long passed = allGrades.stream().filter(Grade::isPassed).count();
        double passRate = allGrades.isEmpty() ? 0 : (passed * 100.0) / allGrades.size();

        // At-risk students (attendance < 75%)
        long atRisk = students.stream()
            .filter(s -> ds.getStudentAttendanceRate(s.getId()) < 75.0)
            .count();

        Object[][] metrics = {
            {"📈", "Attendance Rate",  String.format("%.1f%%", attRate), UIUtils.ACCENT_GREEN,  attRate},
            {"⭐", "Average GPA",      String.format("%.2f", avgGpa),    UIUtils.ACCENT_YELLOW, avgGpa * 25},
            {"✅", "Pass Rate",        String.format("%.1f%%", passRate), UIUtils.ACCENT_BLUE,   passRate},
            {"⚠️", "At-Risk Students", String.valueOf(atRisk),           UIUtils.ACCENT_RED,    (atRisk * 100.0) / Math.max(1, students.size())},
        };

        for (Object[] m : metrics) {
            VBox card = buildMetricCard(m);
            HBox.setHgrow(card, Priority.ALWAYS);
            row.getChildren().add(card);
        }
        return row;
    }

    private VBox buildMetricCard(Object[] m) {
        String icon = (String) m[0];
        String label = (String) m[1];
        String value = (String) m[2];
        String color = (String) m[3];
        double pct = (double) m[4];

        VBox card = new VBox(10);
        card.setStyle(
            "-fx-background-color: " + UIUtils.BG_CARD + ";" +
            "-fx-border-color: " + color + "33;" +
            "-fx-border-radius: 16px; -fx-background-radius: 16px;" +
            "-fx-padding: 18px;" +
            "-fx-effect: dropshadow(gaussian, " + color + "22, 16, 0, 0, 4);"
        );

        HBox topRow = new HBox(0);
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 22px;");
        Region sp = UIUtils.spacer();
        // Mini arc indicator
        Label pctLabel = new Label(String.format("%.0f%%", Math.min(100, pct)));
        pctLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: " + color + "; -fx-font-weight: bold;" +
            "-fx-background-color: " + color + "15; -fx-background-radius: 20px; -fx-padding: 3px 8px;");
        topRow.getChildren().addAll(iconLabel, sp, pctLabel);

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        Label lblLabel = new Label(label);
        lblLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + UIUtils.TEXT_MUTED + "; -fx-font-weight: bold;");

        // Progress bar
        StackPane barPane = new StackPane();
        barPane.setPrefHeight(4); barPane.setMinHeight(4);
        barPane.setAlignment(Pos.CENTER_LEFT);
        Rectangle track = new Rectangle(Double.MAX_VALUE, 4, Color.web("rgba(255,255,255,0.06)"));
        track.setArcWidth(4); track.setArcHeight(4);
        Rectangle fill = new Rectangle(0, 4, Color.web(color));
        fill.setArcWidth(4); fill.setArcHeight(4);
        barPane.getChildren().addAll(track, fill);
        StackPane.setAlignment(fill, Pos.CENTER_LEFT);

        // Animate bar
        card.widthProperty().addListener((obs, ov, nw) -> {
            double barWidth = nw.doubleValue() - 36;
            track.setWidth(barWidth);
            Timeline t = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(fill.widthProperty(), 0)),
                new KeyFrame(Duration.millis(800), new KeyValue(fill.widthProperty(), barWidth * pct / 100.0, Interpolator.EASE_OUT))
            );
            t.play();
        });

        card.getChildren().addAll(topRow, valueLabel, lblLabel, barPane);
        UIUtils.addCardHover(card);
        return card;
    }

    private HBox buildChartsRow1() {
        HBox row = new HBox(16);

        // Attendance trend line chart (30 days)
        VBox lineCard = new VBox(14);
        lineCard.setStyle(
            "-fx-background-color: " + UIUtils.BG_CARD + ";" +
            "-fx-border-color: rgba(255,255,255,0.06);" +
            "-fx-border-radius: 18px; -fx-background-radius: 18px;" +
            "-fx-padding: 20px;"
        );
        HBox.setHgrow(lineCard, Priority.ALWAYS);

        Label lineTitle = new Label("📈  30-Day Attendance Trend");
        lineTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_PRIMARY + ";");

        NumberAxis xAxis = new NumberAxis(1, 30, 5);
        NumberAxis yAxis = new NumberAxis(0, 40, 5);
        xAxis.setLabel("Day");
        yAxis.setLabel("Count");
        xAxis.setStyle("-fx-tick-label-fill: #5a5a7a;");
        yAxis.setStyle("-fx-tick-label-fill: #5a5a7a;");

        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setAnimated(true);
        lineChart.setCreateSymbols(false);
        lineChart.setStyle("-fx-background-color: transparent;");
        lineChart.setPrefHeight(220);
        lineChart.setLegendVisible(true);

        XYChart.Series<Number, Number> serPresent = new XYChart.Series<>(); serPresent.setName("Present");
        XYChart.Series<Number, Number> serAbsent  = new XYChart.Series<>(); serAbsent.setName("Absent");

        List<Attendance> allAtt = ds.getAttendances();
        for (int d = 29; d >= 0; d--) {
            String date = LocalDate.now().minusDays(d).toString();
            long dayNum = 30 - d;
            long present = allAtt.stream().filter(a -> a.getDate().equals(date) && "Present".equals(a.getStatus())).count();
            long absent  = allAtt.stream().filter(a -> a.getDate().equals(date) && "Absent".equals(a.getStatus())).count();
            serPresent.getData().add(new XYChart.Data<>(dayNum, present));
            serAbsent.getData().add(new XYChart.Data<>(dayNum, absent));
        }

        lineChart.getData().addAll(serPresent, serAbsent);
        lineChart.sceneProperty().addListener((obs, old, scene) -> {
            if (scene != null) {
                scene.getStylesheets().add("data:text/css," +
                    ".default-color0.chart-series-line { -fx-stroke: #a8f000; -fx-stroke-width: 2.5px; }" +
                    ".default-color1.chart-series-line { -fx-stroke: #ff3d4a; -fx-stroke-width: 2px; }" +
                    ".chart-plot-background { -fx-background-color: transparent; }" +
                    ".chart-vertical-grid-lines { -fx-stroke: rgba(255,255,255,0.03); }" +
                    ".chart-horizontal-grid-lines { -fx-stroke: rgba(255,255,255,0.03); }" +
                    ".axis { -fx-tick-label-fill: #5a5a7a; -fx-tick-mark-stroke: transparent; }" +
                    ".chart-legend { -fx-background-color: transparent; }" +
                    ".chart-legend-item { -fx-text-fill: #5a5a7a; }" +
                    ".default-color0.chart-legend-item-symbol { -fx-background-color: #a8f000; }" +
                    ".default-color1.chart-legend-item-symbol { -fx-background-color: #ff3d4a; }"
                );
            }
        });

        lineCard.getChildren().addAll(lineTitle, lineChart);

        // Grade distribution pie
        VBox pieCard = buildGradeDistPie();
        pieCard.setPrefWidth(270); pieCard.setMinWidth(250);

        row.getChildren().addAll(lineCard, pieCard);
        return row;
    }

    private VBox buildGradeDistPie() {
        VBox card = new VBox(14);
        card.setStyle(
            "-fx-background-color: " + UIUtils.BG_CARD + ";" +
            "-fx-border-color: rgba(255,255,255,0.06);" +
            "-fx-border-radius: 18px; -fx-background-radius: 18px;" +
            "-fx-padding: 20px;"
        );

        Label title = new Label("🎓  Grade Distribution");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_PRIMARY + ";");

        List<Grade> grades = ds.getGrades();
        Map<String, Long> dist = new LinkedHashMap<>();
        dist.put("A+ / A", grades.stream().filter(g -> g.getLetterGrade() != null && g.getLetterGrade().startsWith("A")).count());
        dist.put("B+ / B", grades.stream().filter(g -> g.getLetterGrade() != null && g.getLetterGrade().startsWith("B")).count());
        dist.put("C+ / C", grades.stream().filter(g -> g.getLetterGrade() != null && g.getLetterGrade().startsWith("C")).count());
        dist.put("D",      grades.stream().filter(g -> "D".equals(g.getLetterGrade())).count());
        dist.put("F",      grades.stream().filter(g -> "F".equals(g.getLetterGrade())).count());

        String[] pieColors = {UIUtils.ACCENT_GREEN, UIUtils.ACCENT_BLUE, UIUtils.ACCENT_YELLOW, UIUtils.ACCENT_ORANGE, UIUtils.ACCENT_RED};

        PieChart pieChart = new PieChart();
        pieChart.setStyle("-fx-background-color: transparent;");
        pieChart.setLegendVisible(false);
        pieChart.setPrefHeight(180);

        int ci = 0;
        for (Map.Entry<String, Long> e : dist.entrySet()) {
            if (e.getValue() > 0) {
                pieChart.getData().add(new PieChart.Data(e.getKey(), e.getValue()));
            }
            ci++;
        }

        // CSS for pie colors
        pieChart.sceneProperty().addListener((obs, old, scene) -> {
            if (scene != null) {
                scene.getStylesheets().add("data:text/css," +
                    ".default-color0.chart-pie { -fx-pie-color: " + UIUtils.ACCENT_GREEN  + "; }" +
                    ".default-color1.chart-pie { -fx-pie-color: " + UIUtils.ACCENT_BLUE   + "; }" +
                    ".default-color2.chart-pie { -fx-pie-color: " + UIUtils.ACCENT_YELLOW + "; }" +
                    ".default-color3.chart-pie { -fx-pie-color: " + UIUtils.ACCENT_ORANGE + "; }" +
                    ".default-color4.chart-pie { -fx-pie-color: " + UIUtils.ACCENT_RED    + "; }" +
                    ".chart-pie-label { -fx-fill: #5a5a7a; -fx-font-size: 10px; }"
                );
            }
        });

        // Legend
        VBox legend = new VBox(6);
        int i = 0;
        for (Map.Entry<String, Long> e : dist.entrySet()) {
            HBox row = new HBox(8);
            row.setAlignment(Pos.CENTER_LEFT);
            Circle dot = new Circle(5, Color.web(pieColors[i % pieColors.length]));
            Label lbl = new Label(e.getKey() + ": " + e.getValue());
            lbl.setStyle("-fx-font-size: 11.5px; -fx-text-fill: " + UIUtils.TEXT_SECONDARY + ";");
            row.getChildren().addAll(dot, lbl);
            legend.getChildren().add(row);
            i++;
        }

        card.getChildren().addAll(title, pieChart, legend);
        return card;
    }

    private HBox buildChartsRow2() {
        HBox row = new HBox(16);

        // Per-class attendance bar chart
        VBox classCard = buildClassAttendanceCard();
        HBox.setHgrow(classCard, Priority.ALWAYS);

        // Subject-wise grade chart
        VBox subjectCard = buildSubjectGradeCard();
        HBox.setHgrow(subjectCard, Priority.ALWAYS);

        row.getChildren().addAll(classCard, subjectCard);
        return row;
    }

    private VBox buildClassAttendanceCard() {
        VBox card = new VBox(14);
        card.setStyle(
            "-fx-background-color: " + UIUtils.BG_CARD + ";" +
            "-fx-border-color: rgba(255,255,255,0.06);" +
            "-fx-border-radius: 18px; -fx-background-radius: 18px;" +
            "-fx-padding: 20px;"
        );

        Label title = new Label("🏫  Attendance by Class");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_PRIMARY + ";");

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis(0, 100, 10);
        yAxis.setLabel("%");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setAnimated(true);
        barChart.setStyle("-fx-background-color: transparent;");
        barChart.setPrefHeight(200);
        barChart.setLegendVisible(false);
        barChart.setBarGap(4);
        barChart.setCategoryGap(20);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Attendance %");

        List<Attendance> allAtt = ds.getAttendances();
        List<String> classes = ds.getClasses();

        for (String cls : classes) {
            List<Attendance> clsAtt = allAtt.stream().filter(a -> cls.equals(a.getClassName())).collect(Collectors.toList());
            double rate = clsAtt.isEmpty() ? 0 :
                (clsAtt.stream().filter(a -> "Present".equals(a.getStatus())).count() * 100.0) / clsAtt.size();
            series.getData().add(new XYChart.Data<>("Class " + cls, rate));
        }

        barChart.getData().add(series);
        barChart.sceneProperty().addListener((obs, old, scene) -> {
            if (scene != null) {
                scene.getStylesheets().add("data:text/css," +
                    ".default-color0.chart-bar { -fx-bar-fill: linear-gradient(to top, #60aaff, #b48aff); }" +
                    ".chart-plot-background { -fx-background-color: transparent; }" +
                    ".chart-vertical-grid-lines { -fx-stroke: rgba(255,255,255,0.03); }" +
                    ".chart-horizontal-grid-lines { -fx-stroke: rgba(255,255,255,0.03); }" +
                    ".axis { -fx-tick-label-fill: #5a5a7a; -fx-tick-mark-stroke: transparent; }"
                );
            }
        });

        card.getChildren().addAll(title, barChart);
        return card;
    }

    private VBox buildSubjectGradeCard() {
        VBox card = new VBox(14);
        card.setStyle(
            "-fx-background-color: " + UIUtils.BG_CARD + ";" +
            "-fx-border-color: rgba(255,255,255,0.06);" +
            "-fx-border-radius: 18px; -fx-background-radius: 18px;" +
            "-fx-padding: 20px;"
        );

        Label title = new Label("📚  Avg Score by Subject");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_PRIMARY + ";");

        List<Grade> allGrades = ds.getGrades();
        Map<String, List<Grade>> bySubject = allGrades.stream()
            .filter(g -> g.getSubjectName() != null)
            .collect(Collectors.groupingBy(Grade::getSubjectName));

        VBox bars = new VBox(10);
        String[] colors = {UIUtils.ACCENT_GREEN, UIUtils.ACCENT_BLUE, UIUtils.ACCENT_PURPLE, UIUtils.ACCENT_YELLOW, UIUtils.ACCENT_ORANGE, UIUtils.ACCENT_CYAN, UIUtils.ACCENT_PINK};
        int ci = 0;

        for (Map.Entry<String, List<Grade>> entry : bySubject.entrySet()) {
            String subject = entry.getKey();
            double avg = entry.getValue().stream().mapToDouble(Grade::getFinalPercentage).average().orElse(0);
            String color = colors[ci % colors.length];

            HBox barRow = new HBox(10);
            barRow.setAlignment(Pos.CENTER_LEFT);

            Label subjectLabel = new Label(subject);
            subjectLabel.setMinWidth(120); subjectLabel.setPrefWidth(120);
            subjectLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + UIUtils.TEXT_SECONDARY + ";");

            StackPane barPane = new StackPane();
            barPane.setPrefHeight(10); barPane.setMinHeight(10);
            barPane.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(barPane, Priority.ALWAYS);

            Rectangle track = new Rectangle(Double.MAX_VALUE, 10, Color.web("rgba(255,255,255,0.04)"));
            track.setArcWidth(10); track.setArcHeight(10);
            Rectangle fill = new Rectangle(0, 10, Color.web(color));
            fill.setArcWidth(10); fill.setArcHeight(10);
            barPane.getChildren().addAll(track, fill);
            StackPane.setAlignment(fill, Pos.CENTER_LEFT);

            // Animate
            barPane.widthProperty().addListener((obs, ov, nw) -> {
                track.setWidth(nw.doubleValue());
                Timeline t = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(fill.widthProperty(), 0)),
                    new KeyFrame(Duration.millis(700), new KeyValue(fill.widthProperty(), nw.doubleValue() * avg / 100.0, Interpolator.EASE_OUT))
                );
                t.play();
            });

            Label pctLabel = new Label(String.format("%.1f%%", avg));
            pctLabel.setMinWidth(50);
            pctLabel.setStyle("-fx-font-size: 11.5px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

            barRow.getChildren().addAll(subjectLabel, barPane, pctLabel);
            bars.getChildren().add(barRow);
            ci++;
        }

        card.getChildren().addAll(title, bars);
        return card;
    }

    private VBox buildTopPerformersSection() {
        VBox section = new VBox(14);
        section.setStyle(
            "-fx-background-color: " + UIUtils.BG_CARD + ";" +
            "-fx-border-color: rgba(255,255,255,0.06);" +
            "-fx-border-radius: 18px; -fx-background-radius: 18px;" +
            "-fx-padding: 20px;"
        );

        Label title = new Label("🏆  Student Performance Overview");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_PRIMARY + ";");

        // Table header
        HBox header = new HBox(0);
        header.setStyle("-fx-padding: 8px 12px; -fx-background-color: rgba(255,255,255,0.02); -fx-background-radius: 8px;");
        String[] cols = {"Student", "Class", "Avg Score", "Letter Grade", "Attendance", "Status"};
        double[] widths = {220, 80, 120, 120, 120, 100};
        for (int i = 0; i < cols.length; i++) {
            Label lbl = new Label(cols[i].toUpperCase());
            lbl.setMinWidth(widths[i]); lbl.setPrefWidth(widths[i]);
            lbl.setStyle("-fx-font-size: 9.5px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_MUTED + "; -fx-letter-spacing: 1px;");
            header.getChildren().add(lbl);
        }

        VBox rows = new VBox(4);
        List<Student> students = ds.getStudents();

        // Build per-student aggregated data
        List<Student> sorted = students.stream()
            .sorted(Comparator.comparingDouble(s -> -ds.getStudentAttendanceRate(s.getId())))
            .collect(Collectors.toList());

        String[] medals = {"🥇", "🥈", "🥉"};

        for (int i = 0; i < Math.min(8, sorted.size()); i++) {
            Student st = sorted.get(i);
            double attRate = ds.getStudentAttendanceRate(st.getId());
            List<Grade> stGrades = ds.getGradesByStudent(st.getId());
            double avgScore = stGrades.stream().mapToDouble(Grade::getFinalPercentage).average().orElse(0);
            String topGrade = stGrades.stream()
                .max(Comparator.comparingDouble(Grade::getFinalPercentage))
                .map(Grade::getLetterGrade).orElse("N/A");
            String gradeColor = stGrades.stream()
                .max(Comparator.comparingDouble(Grade::getFinalPercentage))
                .map(Grade::getGradeColor).orElse(UIUtils.TEXT_MUTED);

            HBox row = new HBox(0);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-padding: 10px 12px; -fx-background-radius: 10px; -fx-cursor: hand;");
            row.setOnMouseEntered(e -> row.setStyle("-fx-padding: 10px 12px; -fx-background-color: rgba(255,255,255,0.03); -fx-background-radius: 10px; -fx-cursor: hand;"));
            row.setOnMouseExited(e -> row.setStyle("-fx-padding: 10px 12px; -fx-background-radius: 10px; -fx-cursor: hand;"));

            // Name with medal
            HBox nameCell = new HBox(8);
            nameCell.setMinWidth(220); nameCell.setPrefWidth(220);
            nameCell.setAlignment(Pos.CENTER_LEFT);

            if (i < 3) {
                Label medal = new Label(medals[i]);
                medal.setStyle("-fx-font-size: 14px;");
                nameCell.getChildren().add(medal);
            } else {
                Label num = new Label((i + 1) + ".");
                num.setStyle("-fx-font-size: 12px; -fx-text-fill: " + UIUtils.TEXT_MUTED + "; -fx-min-width: 20px;");
                nameCell.getChildren().add(num);
            }

            StackPane av = new StackPane();
            av.setPrefSize(30, 30); av.setMinSize(30, 30);
            Circle avBg = new Circle(15);
            avBg.setFill(Color.web(st.getGenderColor() + "22"));
            Label avTxt = new Label(st.getInitials());
            avTxt.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + st.getGenderColor() + ";");
            av.getChildren().addAll(avBg, avTxt);

            VBox nameInfo = new VBox(1);
            Label nameLbl = new Label(st.getName());
            nameLbl.setStyle("-fx-font-size: 12.5px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_PRIMARY + ";");
            Label rollLbl = new Label(st.getRollNumber());
            rollLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");
            nameInfo.getChildren().addAll(nameLbl, rollLbl);
            nameCell.getChildren().addAll(av, nameInfo);

            Label classCell = mkSmallCell("Class " + st.getClassName() + "-" + st.getSection(), 80);
            Label scoreCell = mkSmallCell(String.format("%.1f%%", avgScore), 120);
            scoreCell.setStyle("-fx-font-size: 12.5px; -fx-font-weight: bold; -fx-text-fill: " + gradeColor + "; -fx-min-width: 120px; -fx-pref-width: 120px;");

            HBox gradeCell = new HBox(0);
            gradeCell.setMinWidth(120); gradeCell.setPrefWidth(120);
            gradeCell.setAlignment(Pos.CENTER_LEFT);
            Label gradeBadge = UIUtils.badge(topGrade, gradeColor);
            gradeCell.getChildren().add(gradeBadge);

            // Attendance mini-bar
            HBox attCell = new HBox(6);
            attCell.setMinWidth(120); attCell.setPrefWidth(120);
            attCell.setAlignment(Pos.CENTER_LEFT);
            String attColor = attRate >= 80 ? UIUtils.ACCENT_GREEN : attRate >= 60 ? UIUtils.ACCENT_YELLOW : UIUtils.ACCENT_RED;
            Label attLabel = new Label(String.format("%.0f%%", attRate));
            attLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + attColor + ";");
            attCell.getChildren().add(attLabel);

            HBox statusCell = new HBox(0);
            statusCell.setMinWidth(100); statusCell.setPrefWidth(100);
            statusCell.setAlignment(Pos.CENTER_LEFT);
            boolean isAtRisk = attRate < 75;
            Label statusBadge = UIUtils.badge(isAtRisk ? "At Risk" : "Good", isAtRisk ? UIUtils.ACCENT_RED : UIUtils.ACCENT_GREEN);
            statusCell.getChildren().add(statusBadge);

            row.getChildren().addAll(nameCell, classCell, scoreCell, gradeCell, attCell, statusCell);
            rows.getChildren().add(row);
        }

        section.getChildren().addAll(title, header, rows);
        return section;
    }

    private Label mkSmallCell(String text, double width) {
        Label l = new Label(text != null ? text : "—");
        l.setMinWidth(width); l.setPrefWidth(width);
        l.setStyle("-fx-font-size: 12.5px; -fx-text-fill: " + UIUtils.TEXT_SECONDARY + ";");
        return l;
    }

    public VBox getRoot() { return root; }
}
