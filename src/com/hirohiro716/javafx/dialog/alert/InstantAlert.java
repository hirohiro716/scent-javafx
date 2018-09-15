package com.hirohiro716.javafx.dialog.alert;

import com.hirohiro716.javafx.CSSHelper;

import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * インスタントメッセージを表示するクラス.
 * @author hiro
 */
public class InstantAlert {

    private final Pane parent;

    /**
     * コンストラクタ.
     * @param parent
     */
    public InstantAlert(Parent parent) {
        this.parent = (Pane) parent.getScene().getRoot();
        this.label = new Label();
        this.label.setWrapText(true);
        this.label.setStyle("-fx-background-color:rgba(33,33,33,0.8); -fx-font-size:14px; -fx-text-fill:#fff; -fx-background-radius:1.5em; -fx-padding:0.5em 1em;");
    }

    private Label label;

    /**
     * 表示するラベルを取得する.
     * @return メッセージLabel
     */
    public Label getLabel() {
        return this.label;
    }

    /**
     * 表示するメッセージをセットする.
     * @param text
     */
    public void setText(String text) {
        this.label.setText(text);
    }

    /**
     * フォントサイズをセットする.
     * @param size
     */
    public void setFontSize(double size) {
        this.label.setStyle(CSSHelper.updateStyleValue(this.label.getStyle(), "-fx-font-size", size + "px"));
    }

    private double x;

    /**
     * 表示位置をセットする.
     * @param x
     */
    public void setX(double x) {
        this.x = x;
    }

    private double y;

    /**
     * 表示位置をセットする.
     * @param y
     */
    public void setY(double y) {
        this.y = y;
    }

    private Pos alignment = null;

    /**
     * 表示位置をセットする.
     * @param alignment
     */
    public void setAlignment(Pos alignment) {
        this.alignment = alignment;
    }

    private int millisecond = 3000;

    /**
     * 表示されている時間をミリ秒でセットする.
     * @param millisecond
     */
    public void setMillisecond(int millisecond) {
        this.millisecond = millisecond;
    }

    /**
     * メッセージを表示する.
     */
    public void show() {
        // 親と同じサイズのVBoxを生成
        VBox vbox = new VBox();
        vbox.setPrefSize(this.parent.getWidth(), this.parent.getHeight());
        vbox.setMouseTransparent(true);
        // ラベルに対するマウスイベントを透過
        this.label.setMouseTransparent(true);
        // VBoxにラベルをセット
        vbox.getChildren().add(this.label);
        // 配置
        if (this.alignment == null) {
            this.label.setLayoutX(this.x);
            this.label.setLayoutY(this.y);
        } else {
            vbox.setAlignment(this.alignment);
        }
        this.parent.getChildren().add(vbox);
        // 指定時間表示してフェードアウト
        FeadOut fadeOut = new FeadOut(this.millisecond, vbox);
        vbox.opacityProperty().bind(fadeOut.progressProperty());
        Thread thread = new Thread(fadeOut);
        thread.start();
    }

    /**
     * 簡易メッセージを表示する.
     * @param parent 表示対象Pane
     * @param text 表示文字
     * @param x 位置1
     * @param y 位置2
     * @param millisecond 表示時間（ミリ秒）
     */
    public static void show(Parent parent, String text, double x, double y, int millisecond) {
        InstantAlert instantAlert = new InstantAlert(parent);
        instantAlert.setText(text);
        instantAlert.setX(x);
        instantAlert.setY(y);
        instantAlert.setMillisecond(millisecond);
        instantAlert.show();
    }

    /**
     * 簡易メッセージを表示する.
     * @param parent 表示対象Pane
     * @param text 表示文字
     * @param pos 位置
     * @param millisecond 表示時間（ミリ秒）
     */
    public static void show(Parent parent, String text, Pos pos, int millisecond) {
        InstantAlert instantAlert = new InstantAlert(parent);
        instantAlert.setText(text);
        instantAlert.setAlignment(pos);
        instantAlert.setMillisecond(millisecond);
        instantAlert.show();
    }

    /**
     * メッセージをフェードアウトして破棄する.
     */
    private class FeadOut extends Task<Void> {
        @Override
        protected void succeeded() {
            InstantAlert.this.parent.getChildren().remove(this.vbox);
            super.succeeded();
        }

        private int mmsecond;
        private VBox vbox;

        public FeadOut(int millisecond, VBox vbox) {
            this.mmsecond = millisecond;
            this.vbox = vbox;
        }

        @Override
        protected Void call() throws Exception {
            updateProgress(10, 10);
            try {
                Thread.sleep(this.mmsecond);
            } catch (InterruptedException exception) {
                // nop
            }
            for (int i = 9; i >= 0; i--) {
                updateProgress(i, 10);
                Thread.sleep(50);
            }
            return null;
        }
    }
}
