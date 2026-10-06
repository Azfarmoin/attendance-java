package pages;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import models.Attendance;
import models.Grade;
import models.User;
import services.DataService;
import services.SessionService;
import utils.UIUtils;

import java.util.List;

/**
 * ProfilePage - User profile and system info
 */
public class ProfilePage {
    private VBox root;
    private DataService ds = DataService.getInstance();
    private User user;

    public ProfilePage() {
        root = new VBox(20);
        root.setStyle("-fx-padding: 24px; -fx-background-color: " + UIUtils.BG_DARK + ";");
        user = SessionService.getInstance().getCurrentUser();
        buildUI();
        UIUtils.fadeIn(root);
    }

    private void buildUI() {
        root.getChildren().add(UIUtils.heading("👑  Profile"));

        HBox mainRow = new HBox(20);

        // Left: user card
        mainRow.getChildren().add(buildUserCard());

        // Right: stats + settings
        VBox right = new VBox(16);
        right.getChildren().add(buildQuickStats());
        right.getChildren().add(buildSystemInfo());
        HBox.setHgrow(right, Priority.ALWAYS);
        mainRow.getChildren().add(right);

        root.getChildren().add(mainRow);
        root.getChildren().add(buildEditSection());
    }

    private VBox buildUserCard() {
        VBox card = new VBox(20);
        card.setMinWidth(280); card.setPrefWidth(280);
        card.setAlignment(Pos.CENTER);
        card.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #14143a, #0e0e1a);" +
            "-fx-border-color: " + UIUtils.ACCENT_PURPLE + "44;" +
            "-fx-border-radius: 20px; -fx-background-radius: 20px;" +
            "-fx-padding: 30px 24px;"
        );

        // Avatar
        StackPane avatarPane = new StackPane();
        avatarPane.setPrefSize(90, 90); avatarPane.setMinSize(90, 90);
        Circle avatarBg = new Circle(45);
        avatarBg.setFill(Color.web(UIUtils.ACCENT_PURPLE + "22"));
        avatarBg.setStroke(Color.web(UIUtils.ACCENT_PURPLE));
        avatarBg.setStrokeWidth(2.5);

        // Outer ring animation
        Circle ring = new Circle(48);
        ring.setFill(Color.TRANSPARENT);
        ring.setStroke(Color.web(UIUtils.ACCENT_PURPLE + "44"));
        ring.setStrokeWidth(1.5);
        ring.getStrokeDashArray().addAll(8.0, 4.0);

        Label avatarText = new Label(user != null ? user.getAvatar() : "?");
        avatarText.setStyle("-fx-font-size: 34px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.ACCENT_PURPLE + ";");
        avatarPane.getChildren().addAll(ring, avatarBg, avatarText);

        // Name
        Label nameLabel = new Label(user != null ? user.getName() : "Unknown");
        nameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_PRIMARY + ";");

        // Role badge
        String roleColor = getRoleColor();
        Label roleBadge = UIUtils.badge(user != null ? user.getRoleDisplay() : "User", roleColor);

        // Email
        Label emailLabel = new Label(user != null ? user.getEmail() : "—");
        emailLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");

        // Divider
        Region div = UIUtils.divider();

        // Info items
        VBox infoItems = new VBox(10);
        infoItems.setAlignment(Pos.CENTER_LEFT);
        infoItems.setMaxWidth(Double.MAX_VALUE);

        addInfoItem(infoItems, "🏫", "Institution", "AttendX School");
        addInfoItem(infoItems, "📅", "Joined", "Jan 2024");
        addInfoItem(infoItems, "🔑", "Access Level", user != null ? user.getRoleDisplay() : "User");
        addInfoItem(infoItems, "✅", "Status", "Active");

        card.getChildren().addAll(avatarPane, nameLabel, roleBadge, emailLabel, div, infoItems);
        return card;
    }

    private void addInfoItem(VBox parent, String icon, String label, String value) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 14px;");
        VBox info = new VBox(1);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 9.5px; -fx-text-fill: " + UIUtils.TEXT_MUTED + "; -fx-font-weight: bold; -fx-letter-spacing: 0.8px;");
        Label val = new Label(value);
        val.setStyle("-fx-font-size: 12.5px; -fx-text-fill: " + UIUtils.TEXT_PRIMARY + ";");
        info.getChildren().addAll(lbl, val);
        row.getChildren().addAll(iconLabel, info);
        parent.getChildren().add(row);
    }

    private VBox buildQuickStats() {
        VBox card = new VBox(14);
        card.setStyle(
            "-fx-background-color: " + UIUtils.BG_CARD + ";" +
            "-fx-border-color: rgba(255,255,255,0.06);" +
            "-fx-border-radius: 16px; -fx-background-radius: 16px;" +
            "-fx-padding: 18px;"
        );

        Label title = new Label("⚡  System Overview");
        title.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_PRIMARY + ";");

        List<Attendance> allAtt = ds.getAttendances();
        List<Grade> allGrades = ds.getGrades();

        HBox statsRow = new HBox(0);
        Object[][] stats = {
            {"👤", String.valueOf(ds.getStudents().size()), "Students",   UIUtils.ACCENT_BLUE},
            {"📋", String.valueOf(allAtt.size()),           "Att. Records", UIUtils.ACCENT_GREEN},
            {"🎓", String.valueOf(allGrades.size()),        "Grades",     UIUtils.ACCENT_PURPLE},
            {"📚", String.valueOf(ds.getSubjects().size()), "Subjects",   UIUtils.ACCENT_YELLOW},
        };

        for (Object[] s : stats) {
            VBox statBox = new VBox(4);
            statBox.setAlignment(Pos.CENTER);
            HBox.setHgrow(statBox, Priority.ALWAYS);
            Label icon = new Label((String) s[0]);
            icon.setStyle("-fx-font-size: 18px;");
            Label val = new Label((String) s[1]);
            val.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + s[3] + ";");
            Label lbl = new Label((String) s[2]);
            lbl.setStyle("-fx-font-size: 9.5px; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");
            statBox.getChildren().addAll(icon, val, lbl);
            statsRow.getChildren().add(statBox);
        }

        card.getChildren().addAll(title, UIUtils.divider(), statsRow);
        return card;
    }

    private VBox buildSystemInfo() {
        VBox card = new VBox(14);
        card.setStyle(
            "-fx-background-color: " + UIUtils.BG_CARD + ";" +
            "-fx-border-color: rgba(255,255,255,0.06);" +
            "-fx-border-radius: 16px; -fx-background-radius: 16px;" +
            "-fx-padding: 18px;"
        );

        Label title = new Label("💻  System Info");
        title.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_PRIMARY + ";");

        String dataPath = System.getProperty("user.home") + "/.attendx/data/";
        String javaVer = System.getProperty("java.version");
        String os = System.getProperty("os.name");

        VBox infoList = new VBox(10);
        addSysRow(infoList, "App Version",  "AttendX v2.0 Premium",  UIUtils.ACCENT_GREEN);
        addSysRow(infoList, "Java Version", javaVer,                 UIUtils.ACCENT_BLUE);
        addSysRow(infoList, "OS",           os,                      UIUtils.ACCENT_CYAN);
        addSysRow(infoList, "Data Path",    dataPath,                UIUtils.TEXT_MUTED);
        addSysRow(infoList, "Framework",    "JavaFX 11+",            UIUtils.ACCENT_PURPLE);

        Button saveNowBtn = UIUtils.primaryBtn("💾  Save All Data Now", UIUtils.ACCENT_GREEN);
        saveNowBtn.setOnAction(e -> {
            ds.saveAll();
            saveNowBtn.setText("✅  Saved!");
            javafx.animation.PauseTransition pt = new javafx.animation.PauseTransition(javafx.util.Duration.millis(1500));
            pt.setOnFinished(ev -> saveNowBtn.setText("💾  Save All Data Now"));
            pt.play();
        });

        card.getChildren().addAll(title, UIUtils.divider(), infoList, saveNowBtn);
        return card;
    }

    private void addSysRow(VBox parent, String label, String value, String color) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        Label lbl = new Label(label + ":");
        lbl.setMinWidth(100);
        lbl.setStyle("-fx-font-size: 11.5px; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");
        Label val = new Label(value);
        val.setStyle("-fx-font-size: 11.5px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        val.setWrapText(true);
        row.getChildren().addAll(lbl, val);
        parent.getChildren().add(row);
    }

    private VBox buildEditSection() {
        VBox card = new VBox(16);
        card.setStyle(
            "-fx-background-color: " + UIUtils.BG_CARD + ";" +
            "-fx-border-color: " + UIUtils.ACCENT_PURPLE + "33;" +
            "-fx-border-radius: 16px; -fx-background-radius: 16px;" +
            "-fx-padding: 20px;"
        );

        Label title = new Label("✏️  Update Profile");
        title.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_PRIMARY + ";");

        HBox fields = new HBox(14);
        VBox nameBox = new VBox(6);
        Label nameLbl = new Label("DISPLAY NAME");
        nameLbl.setStyle("-fx-font-size: 9.5px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");
        TextField nameField = UIUtils.input("Full Name");
        nameField.setText(user != null ? user.getName() : "");
        nameField.setPrefWidth(Double.MAX_VALUE);
        nameBox.getChildren().addAll(nameLbl, nameField);
        HBox.setHgrow(nameBox, Priority.ALWAYS);

        VBox emailBox = new VBox(6);
        Label emailLbl = new Label("EMAIL");
        emailLbl.setStyle("-fx-font-size: 9.5px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");
        TextField emailField = UIUtils.input("Email Address");
        emailField.setText(user != null ? user.getEmail() : "");
        emailField.setPrefWidth(Double.MAX_VALUE);
        emailBox.getChildren().addAll(emailLbl, emailField);
        HBox.setHgrow(emailBox, Priority.ALWAYS);

        fields.getChildren().addAll(nameBox, emailBox);

        Label feedback = new Label();
        feedback.setStyle("-fx-font-size: 12px;");
        feedback.setVisible(false);

        Button updateBtn = UIUtils.primaryBtn("Update Profile", UIUtils.ACCENT_PURPLE);
        updateBtn.setOnAction(e -> {
            if (user != null) {
                user.setName(nameField.getText().trim());
                user.setEmail(emailField.getText().trim());
                feedback.setText("✅  Profile updated successfully!");
                feedback.setStyle("-fx-font-size: 12px; -fx-text-fill: " + UIUtils.ACCENT_GREEN + ";");
                feedback.setVisible(true);
                javafx.animation.PauseTransition pt = new javafx.animation.PauseTransition(javafx.util.Duration.millis(2000));
                pt.setOnFinished(ev -> feedback.setVisible(false));
                pt.play();
            }
        });

        card.getChildren().addAll(title, fields, feedback, updateBtn);
        return card;
    }

    private String getRoleColor() {
        if (user == null) return UIUtils.TEXT_MUTED;
        return switch (user.getRole()) {
            case "admin"   -> UIUtils.ACCENT_YELLOW;
            case "teacher" -> UIUtils.ACCENT_BLUE;
            case "student" -> UIUtils.ACCENT_GREEN;
            default        -> UIUtils.TEXT_MUTED;
        };
    }

    public VBox getRoot() { return root; }
}
