package ui;

import app.Main;
import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.util.Duration;
import models.User;
import services.DataService;
import services.SessionService;
import utils.UIUtils;

/**
 * LoginView - Futuristic, immersive login screen
 */
public class LoginView {
    private BorderPane root;

    public LoginView() {
        root = new BorderPane();
        buildUI();
    }

    private void buildUI() {
        root.setStyle("-fx-background-color: " + UIUtils.BG_DARK + ";");

        // Left side - branding / visual
        VBox leftPanel = buildLeftPanel();
        leftPanel.setPrefWidth(520);

        // Right side - login form
        VBox rightPanel = buildRightPanel();

        root.setLeft(leftPanel);
        root.setCenter(rightPanel);
        UIUtils.fadeIn(root);
    }

    private VBox buildLeftPanel() {
        VBox panel = new VBox(0);
        panel.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #0d0d1f, #0a0a18, #0f0a1a);" +
            "-fx-border-color: rgba(255,255,255,0.05);" +
            "-fx-border-width: 0 1px 0 0;"
        );
        panel.setAlignment(Pos.CENTER);

        // Animated background circles
        StackPane visualArea = new StackPane();

        // Glow circles
        Circle c1 = new Circle(200);
        c1.setFill(Color.TRANSPARENT);
        c1.setStroke(Color.web("#b48aff22"));
        c1.setStrokeWidth(1);

        Circle c2 = new Circle(150);
        c2.setFill(Color.web("#b48aff08"));
        c2.setStroke(Color.web("#60aaff22"));
        c2.setStrokeWidth(1);

        Circle c3 = new Circle(90);
        c3.setFill(Color.web("#60aaff10"));
        c3.setStroke(Color.web("#a8f00030"));
        c3.setStrokeWidth(1.5);

        // Pulse c1
        ScaleTransition pulse1 = new ScaleTransition(Duration.millis(3000), c1);
        pulse1.setFromX(1.0); pulse1.setToX(1.06);
        pulse1.setFromY(1.0); pulse1.setToY(1.06);
        pulse1.setCycleCount(Timeline.INDEFINITE); pulse1.setAutoReverse(true); pulse1.play();

        ScaleTransition pulse2 = new ScaleTransition(Duration.millis(2200), c2);
        pulse2.setFromX(0.95); pulse2.setToX(1.05);
        pulse2.setFromY(0.95); pulse2.setToY(1.05);
        pulse2.setCycleCount(Timeline.INDEFINITE); pulse2.setAutoReverse(true); pulse2.play();

        // Center logo area
        VBox logoBox = new VBox(8);
        logoBox.setAlignment(Pos.CENTER);

        // Logo icon
        Label logoIcon = new Label("⚡");
        logoIcon.setStyle("-fx-font-size: 52px;");

        // Pulse logo
        ScaleTransition logoPulse = new ScaleTransition(Duration.millis(1800), logoIcon);
        logoPulse.setFromX(1.0); logoPulse.setToX(1.08);
        logoPulse.setFromY(1.0); logoPulse.setToY(1.08);
        logoPulse.setCycleCount(Timeline.INDEFINITE); logoPulse.setAutoReverse(true); logoPulse.play();

        Label appName = new Label("AttendX");
        appName.setStyle(
            "-fx-font-size: 42px; -fx-font-weight: bold;" +
            "-fx-text-fill: linear-gradient(to right, #a8f000, #60aaff);" +
            "-fx-effect: dropshadow(gaussian, #a8f00066, 30, 0, 0, 0);"
        );

        Label tagline = new Label("Futuristic Student Management");
        tagline.setStyle("-fx-font-size: 14px; -fx-text-fill: #5a5a7a;");

        logoBox.getChildren().addAll(logoIcon, appName, tagline);
        visualArea.getChildren().addAll(c1, c2, c3, logoBox);

        // Features list
        VBox features = new VBox(14);
        features.setAlignment(Pos.CENTER_LEFT);
        features.setStyle("-fx-padding: 0 60px;");
        features.setTranslateY(30);

        String[][] feats = {
            {"⚡", "Real-time attendance tracking"},
            {"📊", "Smart analytics dashboard"},
            {"🎓", "Advanced grade management"},
            {"🔔", "Instant notifications"},
            {"🔒", "Role-based access control"},
        };

        for (String[] feat : feats) {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            Label icon = new Label(feat[0]);
            icon.setStyle("-fx-font-size: 18px;");
            Label txt = new Label(feat[1]);
            txt.setStyle("-fx-font-size: 13px; -fx-text-fill: #5a5a7a;");
            row.getChildren().addAll(icon, txt);
            features.getChildren().add(row);
        }

        panel.getChildren().addAll(visualArea, features);
        VBox.setVgrow(visualArea, Priority.ALWAYS);
        VBox.setMargin(features, new Insets(0, 0, 60, 0));

        return panel;
    }

    private VBox buildRightPanel() {
        VBox panel = new VBox(0);
        panel.setAlignment(Pos.CENTER);
        panel.setStyle("-fx-background-color: " + UIUtils.BG_DARK + "; -fx-padding: 60px;");

        VBox formBox = new VBox(24);
        formBox.setMaxWidth(420);
        formBox.setAlignment(Pos.TOP_LEFT);

        // Header
        VBox header = new VBox(6);
        Label welcome = new Label("Welcome back 👋");
        welcome.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_PRIMARY + ";");
        Label sub = new Label("Sign in to your AttendX account");
        sub.setStyle("-fx-font-size: 14px; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");
        header.getChildren().addAll(welcome, sub);

        // Quick login buttons
        VBox quickLogin = new VBox(10);
        Label qlLabel = new Label("QUICK LOGIN");
        qlLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_MUTED + "; -fx-letter-spacing: 2px;");

        HBox quickBtns = new HBox(8);
        String[][] roles = {
            {"Admin", UIUtils.ACCENT_PURPLE, "admin@school.com", "admin123"},
            {"Teacher", UIUtils.ACCENT_BLUE, "sarah@school.com", "teacher123"},
        };

        TextField emailField = UIUtils.input("Email address");
        PasswordField passField = UIUtils.passwordInput("Password");
        emailField.setPrefWidth(420);
        passField.setPrefWidth(420);

        for (String[] role : roles) {
            Button btn = new Button(role[0]);
            btn.setStyle(
                "-fx-background-color: " + role[1] + "15;" +
                "-fx-text-fill: " + role[1] + ";" +
                "-fx-border-color: " + role[1] + "40;" +
                "-fx-border-radius: 8px;" +
                "-fx-background-radius: 8px;" +
                "-fx-padding: 6px 14px;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
            );
            String email = role[2], pass = role[3];
            btn.setOnAction(e -> {
                emailField.setText(email);
                passField.setText(pass);
            });
            btn.setOnMouseEntered(ev -> btn.setStyle(
                "-fx-background-color: " + role[1] + "30;" +
                "-fx-text-fill: " + role[1] + ";" +
                "-fx-border-color: " + role[1] + "70;" +
                "-fx-border-radius: 8px;" +
                "-fx-background-radius: 8px;" +
                "-fx-padding: 6px 14px;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
            ));
            btn.setOnMouseExited(ev -> btn.setStyle(
                "-fx-background-color: " + role[1] + "15;" +
                "-fx-text-fill: " + role[1] + ";" +
                "-fx-border-color: " + role[1] + "40;" +
                "-fx-border-radius: 8px;" +
                "-fx-background-radius: 8px;" +
                "-fx-padding: 6px 14px;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
            ));
            quickBtns.getChildren().add(btn);
        }
        quickLogin.getChildren().addAll(qlLabel, quickBtns);

        // Divider
        HBox divRow = new HBox(10);
        divRow.setAlignment(Pos.CENTER);
        Region l1 = new Region(); l1.setPrefHeight(1); l1.setStyle("-fx-background-color: rgba(255,255,255,0.07);"); HBox.setHgrow(l1, Priority.ALWAYS);
        Label orLabel = new Label("or sign in manually"); orLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");
        Region l2 = new Region(); l2.setPrefHeight(1); l2.setStyle("-fx-background-color: rgba(255,255,255,0.07);"); HBox.setHgrow(l2, Priority.ALWAYS);
        divRow.getChildren().addAll(l1, orLabel, l2);

        // Fields
        VBox emailBox = new VBox(6);
        Label emailLabel = new Label("EMAIL");
        emailLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_MUTED + "; -fx-letter-spacing: 1px;");
        emailBox.getChildren().addAll(emailLabel, emailField);

        VBox passBox = new VBox(6);
        Label passLabel = new Label("PASSWORD");
        passLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.TEXT_MUTED + "; -fx-letter-spacing: 1px;");
        passBox.getChildren().addAll(passLabel, passField);

        // Error label
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + UIUtils.ACCENT_RED + ";");
        errorLabel.setVisible(false);

        // Login button
        Button loginBtn = new Button("Sign In →");
        loginBtn.setPrefWidth(420);
        loginBtn.setPrefHeight(50);
        loginBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #a8f000, #78c800);" +
            "-fx-text-fill: #050508;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 15px;" +
            "-fx-background-radius: 12px;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, #a8f00088, 25, 0, 0, 6);"
        );
        loginBtn.setOnMouseEntered(e -> loginBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #c8ff30, #a8f000);" +
            "-fx-text-fill: #050508;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 15px;" +
            "-fx-background-radius: 12px;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, #a8f000aa, 35, 0, 0, 8);"
        ));
        loginBtn.setOnMouseExited(e -> loginBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #a8f000, #78c800);" +
            "-fx-text-fill: #050508;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 15px;" +
            "-fx-background-radius: 12px;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, #a8f00088, 25, 0, 0, 6);"
        ));

        // Login action
        Runnable doLogin = () -> {
            String email = emailField.getText().trim();
            String pass = passField.getText();
            if (email.isEmpty() || pass.isEmpty()) {
                errorLabel.setText("Please enter email and password");
                errorLabel.setVisible(true);
                return;
            }
            User user = DataService.getInstance().authenticate(email, pass);
            if (user == null) {
                errorLabel.setText("Invalid credentials. Try again.");
                errorLabel.setVisible(true);
                // Shake animation
                TranslateTransition shake = new TranslateTransition(Duration.millis(50), formBox);
                shake.setByX(10); shake.setCycleCount(6); shake.setAutoReverse(true); shake.play();
                return;
            }
            errorLabel.setVisible(false);
            SessionService.getInstance().setCurrentUser(user);
            Main.showMainScreen();
        };

        loginBtn.setOnAction(e -> doLogin.run());
        passField.setOnAction(e -> doLogin.run());
        emailField.setOnAction(e -> passField.requestFocus());

        // Info
        Label infoLabel = new Label("Demo: admin@school.com / admin123");
        infoLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + UIUtils.TEXT_MUTED + ";");

        formBox.getChildren().addAll(
            header,
            quickLogin,
            divRow,
            emailBox,
            passBox,
            errorLabel,
            loginBtn,
            infoLabel
        );

        panel.getChildren().add(formBox);
        UIUtils.slideInLeft(formBox);
        return panel;
    }

    public BorderPane getRoot() { return root; }
}
