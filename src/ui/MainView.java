package ui;

import app.Main;
import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.util.Duration;
import models.User;
import pages.*;
import services.SessionService;
import utils.UIUtils;

/**
 * MainView - The main application shell
 * Contains the sidebar navigation and content area
 */
public class MainView {
    private BorderPane root;
    private VBox sidebar;
    private StackPane contentArea;
    private String activeNav = "dashboard";

    // Navigation items: [id, emoji, label, color]
    private final String[][] NAV_ITEMS = {
        {"dashboard",  "⚡", "Dashboard",   UIUtils.ACCENT_GREEN},
        {"students",   "👤", "Students",    UIUtils.ACCENT_BLUE},
        {"attendance", "📋", "Attendance",  UIUtils.ACCENT_PURPLE},
        {"grades",     "🎓", "Grades",      UIUtils.ACCENT_YELLOW},
        {"analytics",  "📊", "Analytics",   UIUtils.ACCENT_CYAN},
        {"subjects",   "📚", "Subjects",    UIUtils.ACCENT_ORANGE},
        {"timetable",  "⏰", "Timetable",   UIUtils.ACCENT_PINK},
        {"profile",    "👑", "Profile",     UIUtils.ACCENT_PURPLE},
    };

    public MainView() {
        root = new BorderPane();
        buildUI();
    }

    private void buildUI() {
        root.setStyle("-fx-background-color: " + UIUtils.BG_DARK + ";");

        sidebar = buildSidebar();
        contentArea = new StackPane();
        contentArea.setStyle("-fx-background-color: " + UIUtils.BG_DARK + ";");

        // Header
        HBox header = buildHeader();

        VBox rightPanel = new VBox(0);
        rightPanel.getChildren().addAll(header, contentArea);
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        root.setLeft(sidebar);
        root.setCenter(rightPanel);

        // Load default page
        navigateTo("dashboard");
        UIUtils.fadeIn(root);
    }

    private VBox buildSidebar() {
        VBox sb = new VBox(0);
        sb.setPrefWidth(230);
        sb.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #09090f, #0a0a16, #09090d);" +
            "-fx-border-color: rgba(255,255,255,0.06);" +
            "-fx-border-width: 0 1px 0 0;"
        );

        // Logo area
        VBox logoArea = new VBox(4);
        logoArea.setStyle("-fx-padding: 24px 20px 20px 20px;");
        logoArea.setAlignment(Pos.CENTER_LEFT);

        HBox logoRow = new HBox(10);
        logoRow.setAlignment(Pos.CENTER_LEFT);
        Label logoIcon = new Label("⚡");
        logoIcon.setStyle("-fx-font-size: 24px;");
        Label logoText = new Label("AttendX");
        logoText.setStyle(
            "-fx-font-size: 20px; -fx-font-weight: bold;" +
            "-fx-text-fill: linear-gradient(to right, #a8f000, #60aaff);"
        );
        logoRow.getChildren().addAll(logoIcon, logoText);

        Label version = new Label("v2.0 — Premium Edition");
        version.setStyle("-fx-font-size: 10px; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");

        logoArea.getChildren().addAll(logoRow, version);

        // Live status indicator
        HBox liveRow = new HBox(6);
        liveRow.setAlignment(Pos.CENTER_LEFT);
        liveRow.setStyle("-fx-padding: 0 20px 16px 20px;");
        Circle dot = new Circle(4);
        dot.setFill(javafx.scene.paint.Color.web(UIUtils.ACCENT_GREEN));
        ScaleTransition dotPulse = new ScaleTransition(Duration.millis(800), dot);
        dotPulse.setFromX(1.0); dotPulse.setToX(1.5);
        dotPulse.setFromY(1.0); dotPulse.setToY(1.5);
        dotPulse.setCycleCount(Timeline.INDEFINITE); dotPulse.setAutoReverse(true); dotPulse.play();
        Label liveLabel = new Label("System Online");
        liveLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + UIUtils.ACCENT_GREEN + "; -fx-font-weight: bold;");
        liveRow.getChildren().addAll(dot, liveLabel);

        // Divider
        Region div1 = UIUtils.divider();
        div1.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-padding: 0;");

        // Nav section label
        Label navLabel = new Label("NAVIGATION");
        navLabel.setStyle(
            "-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";" +
            "-fx-letter-spacing: 2px; -fx-padding: 16px 20px 8px 20px;"
        );

        // Nav items
        VBox navItems = new VBox(2);
        navItems.setStyle("-fx-padding: 0 10px;");

        for (String[] item : NAV_ITEMS) {
            Button navBtn = createNavButton(item[0], item[1], item[2], item[3]);
            navItems.getChildren().add(navBtn);
        }

        // Spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // User profile at bottom
        VBox userArea = buildSidebarUserArea();

        sb.getChildren().addAll(logoArea, liveRow, div1, navLabel, navItems, spacer, userArea);
        return sb;
    }

    private Button createNavButton(String id, String emoji, String label, String color) {
        Button btn = new Button();
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setGraphicTextGap(10);

        HBox content = new HBox(10);
        content.setAlignment(Pos.CENTER_LEFT);

        Label emojiLabel = new Label(emoji);
        emojiLabel.setStyle("-fx-font-size: 17px; -fx-min-width: 24px;");

        Label textLabel = new Label(label);
        textLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 600;");

        content.getChildren().addAll(emojiLabel, textLabel);
        btn.setGraphic(content);
        btn.setText("");

        updateNavBtnStyle(btn, textLabel, id.equals(activeNav), color);

        btn.setOnAction(e -> navigateTo(id));
        btn.setOnMouseEntered(e -> {
            if (!id.equals(activeNav)) {
                btn.setStyle(
                    "-fx-background-color: rgba(255,255,255,0.04);" +
                    "-fx-background-radius: 10px;" +
                    "-fx-border-radius: 10px;" +
                    "-fx-padding: 10px 14px;" +
                    "-fx-cursor: hand;"
                );
                textLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: " + UIUtils.TEXT_SECONDARY + ";");
            }
        });
        btn.setOnMouseExited(e -> {
            if (!id.equals(activeNav)) {
                updateNavBtnStyle(btn, textLabel, false, color);
            }
        });

        btn.setId("nav-" + id);
        return btn;
    }

    private void updateNavBtnStyle(Button btn, Label textLabel, boolean active, String color) {
        if (active) {
            btn.setStyle(
                "-fx-background-color: " + color + "18;" +
                "-fx-background-radius: 10px;" +
                "-fx-border-radius: 10px;" +
                "-fx-border-color: " + color + "44;" +
                "-fx-border-width: 1px;" +
                "-fx-padding: 10px 14px;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, " + color + "33, 12, 0, 0, 0);"
            );
            textLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        } else {
            btn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background-radius: 10px;" +
                "-fx-border-radius: 10px;" +
                "-fx-padding: 10px 14px;" +
                "-fx-cursor: hand;"
            );
            textLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");
        }
    }

    private VBox buildSidebarUserArea() {
        User user = SessionService.getInstance().getCurrentUser();
        VBox area = new VBox(0);
        area.setStyle(
            "-fx-border-color: rgba(255,255,255,0.05);" +
            "-fx-border-width: 1px 0 0 0;" +
            "-fx-padding: 16px 14px;"
        );

        HBox userRow = new HBox(10);
        userRow.setAlignment(Pos.CENTER_LEFT);

        // Avatar
        StackPane avatar = new StackPane();
        avatar.setPrefSize(36, 36); avatar.setMinSize(36, 36);
        Circle avatarBg = new Circle(18);
        avatarBg.setFill(javafx.scene.paint.Color.web(UIUtils.ACCENT_PURPLE + "33"));
        avatarBg.setStroke(javafx.scene.paint.Color.web(UIUtils.ACCENT_PURPLE + "66"));
        avatarBg.setStrokeWidth(1.5);
        Label avatarText = new Label(user != null ? user.getAvatar() : "?");
        avatarText.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.ACCENT_PURPLE + ";");
        avatar.getChildren().addAll(avatarBg, avatarText);

        VBox userInfo = new VBox(2);
        Label nameLabel = new Label(user != null ? user.getName() : "Unknown");
        nameLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_PRIMARY + ";");
        Label roleLabel = new Label(user != null ? user.getRoleDisplay() : "");
        roleLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");
        userInfo.getChildren().addAll(nameLabel, roleLabel);

        Region sp = UIUtils.spacer();

        Button logoutBtn = new Button("⏏");
        logoutBtn.setStyle(
            "-fx-background-color: rgba(255,61,74,0.1);" +
            "-fx-text-fill: #ff3d4a;" +
            "-fx-background-radius: 8px;" +
            "-fx-padding: 6px 8px;" +
            "-fx-cursor: hand;" +
            "-fx-font-size: 14px;"
        );
        Tooltip logoutTip = new Tooltip("Logout");
        Tooltip.install(logoutBtn, logoutTip);
        logoutBtn.setOnAction(e -> Main.logout());
        logoutBtn.setOnMouseEntered(ev -> logoutBtn.setStyle(
            "-fx-background-color: rgba(255,61,74,0.25);" +
            "-fx-text-fill: #ff3d4a;" +
            "-fx-background-radius: 8px;" +
            "-fx-padding: 6px 8px;" +
            "-fx-cursor: hand;" +
            "-fx-font-size: 14px;"
        ));
        logoutBtn.setOnMouseExited(ev -> logoutBtn.setStyle(
            "-fx-background-color: rgba(255,61,74,0.1);" +
            "-fx-text-fill: #ff3d4a;" +
            "-fx-background-radius: 8px;" +
            "-fx-padding: 6px 8px;" +
            "-fx-cursor: hand;" +
            "-fx-font-size: 14px;"
        ));

        userRow.getChildren().addAll(avatar, userInfo, sp, logoutBtn);
        area.getChildren().add(userRow);
        return area;
    }

    private HBox buildHeader() {
        HBox header = new HBox(0);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPrefHeight(56);
        header.setStyle(
            "-fx-background-color: rgba(8,8,15,0.95);" +
            "-fx-border-color: rgba(255,255,255,0.05);" +
            "-fx-border-width: 0 0 1px 0;" +
            "-fx-padding: 0 24px;"
        );

        // Breadcrumb
        Label breadcrumb = new Label("AttendX / Dashboard");
        breadcrumb.setStyle("-fx-font-size: 12px; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");
        breadcrumb.setId("breadcrumb");

        Region sp = UIUtils.spacer();

        // Date/time display
        Label dateLabel = new Label();
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");
        updateDateTime(dateLabel);

        // Update time every minute
        Timeline timer = new Timeline(new KeyFrame(Duration.minutes(1), e -> updateDateTime(dateLabel)));
        timer.setCycleCount(Timeline.INDEFINITE); timer.play();

        // Notification bell
        Button notifBtn = new Button("🔔");
        notifBtn.setStyle(
            "-fx-background-color: rgba(255,255,255,0.05);" +
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 8px;" +
            "-fx-padding: 6px 10px;" +
            "-fx-cursor: hand;"
        );

        header.getChildren().addAll(breadcrumb, sp, dateLabel, notifBtn);
        HBox.setMargin(notifBtn, new Insets(0, 0, 0, 12));
        return header;
    }

    private void updateDateTime(Label label) {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("EEE, dd MMM yyyy  HH:mm");
        label.setText(now.format(fmt));
    }

    public void navigateTo(String id) {
        activeNav = id;

        // Update nav button styles
        sidebar.lookupAll("Button").forEach(node -> {
            if (node.getId() != null && node.getId().startsWith("nav-")) {
                Button btn = (Button) node;
                String navId = btn.getId().replace("nav-", "");
                HBox content = (HBox) btn.getGraphic();
                Label textLabel = (Label) content.getChildren().get(1);
                String color = getNavColor(navId);
                updateNavBtnStyle(btn, textLabel, navId.equals(id), color);
            }
        });

        // Update breadcrumb
        Label breadcrumb = (Label) root.lookup("#breadcrumb");
        if (breadcrumb != null) {
            String pageName = getPageName(id);
            breadcrumb.setText("AttendX / " + pageName);
        }

        // Load page
        javafx.scene.Node page = switch (id) {
            case "dashboard"  -> new DashboardPage().getRoot();
            case "students"   -> new StudentsPage().getRoot();
            case "attendance" -> new AttendancePage().getRoot();
            case "grades"     -> new GradesPage().getRoot();
            case "analytics"  -> new AnalyticsPage().getRoot();
            case "subjects"   -> new SubjectsPage().getRoot();
            case "timetable"  -> new TimetablePage().getRoot();
            case "profile"    -> new ProfilePage().getRoot();
            default           -> new DashboardPage().getRoot();
        };

        contentArea.getChildren().setAll(page);
        UIUtils.fadeIn(page);
    }

    private String getNavColor(String id) {
        for (String[] item : NAV_ITEMS) {
            if (item[0].equals(id)) return item[3];
        }
        return UIUtils.ACCENT_GREEN;
    }

    private String getPageName(String id) {
        for (String[] item : NAV_ITEMS) {
            if (item[0].equals(id)) return item[2];
        }
        return "Dashboard";
    }

    public BorderPane getRoot() { return root; }
}
