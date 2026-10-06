package pages;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import models.Subject;
import services.DataService;
import utils.UIUtils;

import java.util.List;

/**
 * SubjectsPage - View and manage subjects
 */
public class SubjectsPage {
    private VBox root;
    private DataService ds = DataService.getInstance();

    public SubjectsPage() {
        root = new VBox(20);
        root.setStyle("-fx-padding: 24px; -fx-background-color: " + UIUtils.BG_DARK + ";");
        buildUI();
        UIUtils.fadeIn(root);
    }

    private void buildUI() {
        VBox header = new VBox(4);
        Label title = UIUtils.heading("📚  Subjects");
        Label sub = UIUtils.subheading("Manage curriculum subjects and course details");
        header.getChildren().addAll(title, sub);
        root.getChildren().add(header);

        // Subject cards grid
        List<Subject> subjects = ds.getSubjects();
        FlowPane grid = new FlowPane(14, 14);
        grid.setPrefWrapLength(Double.MAX_VALUE);

        String[] colors = {UIUtils.ACCENT_BLUE, UIUtils.ACCENT_GREEN, UIUtils.ACCENT_PURPLE,
            UIUtils.ACCENT_YELLOW, UIUtils.ACCENT_ORANGE, UIUtils.ACCENT_CYAN, UIUtils.ACCENT_PINK};
        String[] emojis = {"➕", "⚡", "🧪", "🧬", "📖", "✍️", "💻"};

        for (int i = 0; i < subjects.size(); i++) {
            Subject s = subjects.get(i);
            String color = colors[i % colors.length];
            String emoji = emojis[i % emojis.length];
            VBox card = buildSubjectCard(s, color, emoji);
            grid.getChildren().add(card);
        }

        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        root.getChildren().add(scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);
    }

    private VBox buildSubjectCard(Subject s, String color, String emoji) {
        VBox card = new VBox(14);
        card.setPrefWidth(260);
        card.setStyle(
            "-fx-background-color: " + UIUtils.BG_CARD + ";" +
            "-fx-border-color: " + color + "33;" +
            "-fx-border-radius: 18px; -fx-background-radius: 18px;" +
            "-fx-padding: 22px;" +
            "-fx-effect: dropshadow(gaussian, " + color + "22, 16, 0, 0, 4);"
        );

        // Icon circle
        StackPane iconPane = new StackPane();
        iconPane.setPrefSize(54, 54); iconPane.setMinSize(54, 54);
        Circle iconBg = new Circle(27);
        iconBg.setFill(Color.web(color + "22"));
        iconBg.setStroke(Color.web(color + "55")); iconBg.setStrokeWidth(1.5);
        Label iconLabel = new Label(emoji);
        iconLabel.setStyle("-fx-font-size: 24px;");
        iconPane.getChildren().addAll(iconBg, iconLabel);

        Label nameLabel = new Label(s.getName());
        nameLabel.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_PRIMARY + ";");
        nameLabel.setWrapText(true);

        Label codeLabel = UIUtils.badge(s.getCode(), color);

        HBox statsRow = new HBox(10);
        VBox creditBox = new VBox(2);
        Label creditVal = new Label(s.getCreditHours() + " hrs");
        creditVal.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        Label creditLbl = new Label("Credit Hours");
        creditLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");
        creditBox.getChildren().addAll(creditVal, creditLbl);

        // Grade stats
        long gradeCount = ds.getGrades().stream().filter(g -> s.getName().equals(g.getSubjectName())).count();
        VBox gradesBox = new VBox(2);
        Label gradesVal = new Label(String.valueOf(gradeCount));
        gradesVal.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.ACCENT_CYAN + ";");
        Label gradesLbl = new Label("Grade Records");
        gradesLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");
        gradesBox.getChildren().addAll(gradesVal, gradesLbl);
        statsRow.getChildren().addAll(creditBox, UIUtils.spacer(), gradesBox);

        card.getChildren().addAll(iconPane, nameLabel, codeLabel, UIUtils.divider(), statsRow);
        UIUtils.addCardHover(card);
        return card;
    }

    public VBox getRoot() { return root; }
}
