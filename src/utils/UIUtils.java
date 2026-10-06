package utils;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.util.Duration;

/**
 * UI Utilities - helpers for building the funky UI
 */
public class UIUtils {

    // ─── COLORS ─────────────────────────────────────────────────────────────
    public static final String BG_DARK        = "#08080f";
    public static final String BG_CARD        = "#0e0e1a";
    public static final String BG_CARD2       = "#12121f";
    public static final String BG_HOVER       = "#1a1a2e";
    public static final String TEXT_PRIMARY   = "#f0f0f8";
    public static final String TEXT_SECONDARY = "#8888aa";
    public static final String TEXT_MUTED     = "#3d3d5a";
    public static final String ACCENT_GREEN   = "#a8f000";
    public static final String ACCENT_BLUE    = "#60aaff";
    public static final String ACCENT_PURPLE  = "#b48aff";
    public static final String ACCENT_PINK    = "#ff8aaa";
    public static final String ACCENT_ORANGE  = "#ff8c42";
    public static final String ACCENT_YELLOW  = "#ffcc50";
    public static final String ACCENT_RED     = "#ff3d4a";
    public static final String ACCENT_CYAN    = "#40d8e8";
    public static final String BORDER         = "rgba(255,255,255,0.07)";

    // ─── STYLED TEXT ─────────────────────────────────────────────────────────

    public static Label heading(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_PRIMARY + ";");
        return l;
    }

    public static Label subheading(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 13px; -fx-text-fill: " + TEXT_MUTED + ";");
        return l;
    }

    public static Label label(String text, String color, int size) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: " + size + "px; -fx-text-fill: " + color + ";");
        return l;
    }

    // ─── CARDS ───────────────────────────────────────────────────────────────

    public static VBox card() {
        VBox card = new VBox(12);
        card.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 16px;" +
            "-fx-background-radius: 16px;" +
            "-fx-padding: 20px;"
        );
        addCardHover(card);
        return card;
    }

    public static VBox glowCard(String accentColor) {
        VBox card = new VBox(12);
        card.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-border-color: " + accentColor + "33;" +
            "-fx-border-radius: 16px;" +
            "-fx-background-radius: 16px;" +
            "-fx-padding: 20px;" +
            "-fx-effect: dropshadow(gaussian, " + accentColor + "22, 20, 0, 0, 0);"
        );
        return card;
    }

    public static void addCardHover(Region node) {
        node.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), node);
            st.setToX(1.01); st.setToY(1.01); st.play();
        });
        node.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), node);
            st.setToX(1.0); st.setToY(1.0); st.play();
        });
    }

    // ─── BUTTONS ─────────────────────────────────────────────────────────────

    public static Button primaryBtn(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: #050508;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 10px 24px;" +
            "-fx-background-radius: 10px;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, " + color + "66, 16, 0, 0, 4);"
        );
        btn.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(100), btn);
            st.setToX(1.04); st.setToY(1.04); st.play();
        });
        btn.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(100), btn);
            st.setToX(1.0); st.setToY(1.0); st.play();
        });
        return btn;
    }

    public static Button ghostBtn(String text) {
        Button btn = new Button(text);
        btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + TEXT_SECONDARY + ";" +
            "-fx-font-size: 12px;" +
            "-fx-padding: 8px 16px;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 8px;" +
            "-fx-background-radius: 8px;" +
            "-fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: #ffffff0a;" +
            "-fx-text-fill: " + TEXT_PRIMARY + ";" +
            "-fx-font-size: 12px;" +
            "-fx-padding: 8px 16px;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 8px;" +
            "-fx-background-radius: 8px;" +
            "-fx-cursor: hand;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + TEXT_SECONDARY + ";" +
            "-fx-font-size: 12px;" +
            "-fx-padding: 8px 16px;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 8px;" +
            "-fx-background-radius: 8px;" +
            "-fx-cursor: hand;"
        ));
        return btn;
    }

    public static Button dangerBtn(String text) {
        Button btn = new Button(text);
        btn.setStyle(
            "-fx-background-color: rgba(255,61,74,0.12);" +
            "-fx-text-fill: #ff3d4a;" +
            "-fx-font-size: 12px;" +
            "-fx-padding: 8px 16px;" +
            "-fx-border-color: rgba(255,61,74,0.3);" +
            "-fx-border-radius: 8px;" +
            "-fx-background-radius: 8px;" +
            "-fx-cursor: hand;"
        );
        return btn;
    }

    // ─── INPUTS ──────────────────────────────────────────────────────────────

    public static TextField input(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(
            "-fx-background-color: #0a0a14;" +
            "-fx-text-fill: " + TEXT_PRIMARY + ";" +
            "-fx-prompt-text-fill: " + TEXT_MUTED + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 10px;" +
            "-fx-background-radius: 10px;" +
            "-fx-padding: 10px 14px;" +
            "-fx-font-size: 13px;"
        );
        tf.focusedProperty().addListener((obs, old, focused) -> {
            if (focused) {
                tf.setStyle(
                    "-fx-background-color: #0a0a14;" +
                    "-fx-text-fill: " + TEXT_PRIMARY + ";" +
                    "-fx-prompt-text-fill: " + TEXT_MUTED + ";" +
                    "-fx-border-color: " + ACCENT_BLUE + "88;" +
                    "-fx-border-radius: 10px;" +
                    "-fx-background-radius: 10px;" +
                    "-fx-padding: 10px 14px;" +
                    "-fx-font-size: 13px;" +
                    "-fx-effect: dropshadow(gaussian, " + ACCENT_BLUE + "44, 12, 0, 0, 0);"
                );
            } else {
                tf.setStyle(
                    "-fx-background-color: #0a0a14;" +
                    "-fx-text-fill: " + TEXT_PRIMARY + ";" +
                    "-fx-prompt-text-fill: " + TEXT_MUTED + ";" +
                    "-fx-border-color: " + BORDER + ";" +
                    "-fx-border-radius: 10px;" +
                    "-fx-background-radius: 10px;" +
                    "-fx-padding: 10px 14px;" +
                    "-fx-font-size: 13px;"
                );
            }
        });
        return tf;
    }

    public static PasswordField passwordInput(String prompt) {
        PasswordField pf = new PasswordField();
        pf.setPromptText(prompt);
        pf.setStyle(
            "-fx-background-color: #0a0a14;" +
            "-fx-text-fill: " + TEXT_PRIMARY + ";" +
            "-fx-prompt-text-fill: " + TEXT_MUTED + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 10px;" +
            "-fx-background-radius: 10px;" +
            "-fx-padding: 10px 14px;" +
            "-fx-font-size: 13px;"
        );
        return pf;
    }

    public static ComboBox<String> comboBox(String... items) {
        ComboBox<String> cb = new ComboBox<>();
        cb.getItems().addAll(items);
        cb.setStyle(
            "-fx-background-color: #0a0a14;" +
            "-fx-text-fill: " + TEXT_PRIMARY + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 10px;" +
            "-fx-background-radius: 10px;" +
            "-fx-padding: 2px;" +
            "-fx-font-size: 13px;"
        );
        return cb;
    }

    // ─── BADGES ──────────────────────────────────────────────────────────────

    public static Label badge(String text, String color) {
        Label b = new Label(text);
        b.setStyle(
            "-fx-background-color: " + color + "1a;" +
            "-fx-text-fill: " + color + ";" +
            "-fx-border-color: " + color + "44;" +
            "-fx-border-radius: 20px;" +
            "-fx-background-radius: 20px;" +
            "-fx-padding: 3px 10px;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;"
        );
        return b;
    }

    // ─── ANIMATIONS ──────────────────────────────────────────────────────────

    public static void fadeIn(Node node) {
        node.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(400), node);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
    }

    public static void slideInLeft(Node node) {
        node.setTranslateX(-30);
        node.setOpacity(0);
        TranslateTransition tt = new TranslateTransition(Duration.millis(350), node);
        tt.setToX(0); tt.play();
        FadeTransition ft = new FadeTransition(Duration.millis(350), node);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
    }

    public static void pulseAnimation(Node node) {
        ScaleTransition st = new ScaleTransition(Duration.millis(800), node);
        st.setFromX(1.0); st.setFromY(1.0);
        st.setToX(1.05); st.setToY(1.05);
        st.setCycleCount(Timeline.INDEFINITE);
        st.setAutoReverse(true); st.play();
    }

    public static Timeline glowPulse(Node node, String fromColor, String toColor) {
        Timeline t = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.styleProperty(), node.getStyle() + "-fx-effect: dropshadow(gaussian," + fromColor + ",10,0,0,0);")),
            new KeyFrame(Duration.millis(1000),
                new KeyValue(node.styleProperty(), node.getStyle() + "-fx-effect: dropshadow(gaussian," + toColor + ",25,0,0,0);"))
        );
        t.setCycleCount(Timeline.INDEFINITE);
        t.setAutoReverse(true);
        return t;
    }

    // ─── DIVIDER ─────────────────────────────────────────────────────────────

    public static Region divider() {
        Region r = new Region();
        r.setPrefHeight(1);
        r.setStyle("-fx-background-color: " + BORDER + ";");
        return r;
    }

    // ─── STATUS BADGE ────────────────────────────────────────────────────────

    public static Label statusBadge(String status) {
        String color = switch (status) {
            case "Present" -> ACCENT_GREEN;
            case "Absent"  -> ACCENT_RED;
            case "Late"    -> ACCENT_YELLOW;
            case "A+", "A" -> ACCENT_GREEN;
            case "B+", "B" -> ACCENT_BLUE;
            case "C+", "C" -> ACCENT_YELLOW;
            case "D"       -> ACCENT_ORANGE;
            case "F"       -> ACCENT_RED;
            default        -> TEXT_SECONDARY;
        };
        return badge(status, color);
    }

    // ─── STAT MINI CARD ──────────────────────────────────────────────────────

    public static VBox miniStatCard(String label, String value, String color) {
        VBox card = new VBox(6);
        card.setStyle(
            "-fx-background-color: " + color + "0d;" +
            "-fx-border-color: " + color + "33;" +
            "-fx-border-radius: 14px;" +
            "-fx-background-radius: 14px;" +
            "-fx-padding: 16px;"
        );

        Label valLabel = new Label(value);
        valLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        Label lblLabel = new Label(label);
        lblLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + "; -fx-font-weight: bold;");

        card.getChildren().addAll(valLabel, lblLabel);
        return card;
    }

    // ─── SEPARATOR ───────────────────────────────────────────────────────────

    public static Region spacer() {
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        VBox.setVgrow(r, Priority.ALWAYS);
        return r;
    }

    // ─── TOAST NOTIFICATION ─────────────────────────────────────────────────

    public static void showToast(Pane parent, String message, String type) {
        String color = type.equals("success") ? ACCENT_GREEN : type.equals("error") ? ACCENT_RED : ACCENT_BLUE;
        Label toast = new Label(message);
        toast.setStyle(
            "-fx-background-color: " + BG_CARD2 + ";" +
            "-fx-text-fill: " + color + ";" +
            "-fx-border-color: " + color + "44;" +
            "-fx-border-radius: 10px;" +
            "-fx-background-radius: 10px;" +
            "-fx-padding: 12px 20px;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-effect: dropshadow(gaussian, " + color + "44, 20, 0, 0, 4);"
        );
        toast.setLayoutX(parent.getWidth() / 2 - 100);
        toast.setLayoutY(parent.getHeight() - 80);
        parent.getChildren().add(toast);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), toast);
        fadeIn.setFromValue(0); fadeIn.setToValue(1);

        PauseTransition pause = new PauseTransition(Duration.millis(2500));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), toast);
        fadeOut.setFromValue(1); fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> parent.getChildren().remove(toast));

        new SequentialTransition(fadeIn, pause, fadeOut).play();
    }
}
