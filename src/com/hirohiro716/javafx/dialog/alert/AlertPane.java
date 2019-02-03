package com.hirohiro716.javafx.dialog.alert;

import java.io.IOException;

import com.hirohiro716.javafx.FXMLLoader;
import com.hirohiro716.javafx.LayoutHelper;
import com.hirohiro716.javafx.control.EnterFireButton;
import com.hirohiro716.javafx.dialog.AbstractPaneDialog;
import com.hirohiro716.javafx.dialog.DialogResult;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

/**
 * メッセージを表示するクラス.
 * @author hiro
 */
public class AlertPane extends AbstractPaneDialog<DialogResult> {

    @FXML
    private AnchorPane paneRoot;

    @FXML
    private Label labelTitle;

    @FXML
    private AnchorPane paneMessage;

    @FXML
    private EnterFireButton buttonOk;

    /**
     * コンストラクタ.
     * @param parentPane
     */
    public AlertPane(Pane parentPane) {
        super(parentPane);
    }

    @Override
    public AnchorPane getContentPane() {
        return this.paneRoot;
    }

    @Override
    public void show() {
        AlertPane dialog = this;
        // ダイアログ表示
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Alert.class.getResource(Alert.class.getSimpleName() + ".fxml"), this);
            this.show(fxmlLoader.getPaneRoot());
        } catch (IOException exception) {
            exception.printStackTrace();
            return;
        }
        // タイトルのセット
        this.labelTitle.setText(this.title);
        // メッセージのセット
        if (this.message != null) {
            Label label = new Label(this.message);
            label.setWrapText(true);
            label.setAlignment(Pos.TOP_LEFT);
            this.paneMessage.getChildren().add(label);
            LayoutHelper.setAnchor(label, 30, 0, 0, 0);
        }
        // メッセージNodeのセット
        if (this.messageNode != null) {
            this.paneMessage.getChildren().add(this.messageNode);
        }
        // ボタンのイベント定義
        this.setResult(DialogResult.OK);
        this.buttonOk.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dialog.close();
            }
        });
        // キーボードイベント定義
        this.getStackPane().addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                case O:
                    dialog.close();
                    break;
                default:
                    break;
                }
            }
        });
    }

    private String title;

    /**
     * タイトルをセットする.
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    private String message;

    /**
     * メッセージ内容をセットする.
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    private Node messageNode;

    /**
     * メッセージに代わるNodeをセットする.
     * @param node
     */
    public void setMessageNode(Node node) {
        this.messageNode = node;
    }

    @Override
    public boolean isClosableAtStackPaneClicked() {
        return false;
    }

    /**
     * ダイアログを表示する.
     * @param <T> javafx.scene.layout.Paneを継承したクラスオブジェクト
     * @param title タイトル
     * @param message メッセージ
     * @param parentPane 表示対象Pane
     */
    public static <T extends Pane> void show(String title, String message, T parentPane) {
        AlertPane dialog = new AlertPane(parentPane);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.show();
    }

    /**
     * ダイアログを表示する.
     * @param <T> javafx.scene.layout.Paneを継承したクラスオブジェクト
     * @param title タイトル
     * @param message メッセージ
     * @param parentPane 表示対象Pane
     * @param closeEvent 閉じる際の処理
     */
    public static <T extends Pane> void show(String title, String message, T parentPane, CloseEventHandler<DialogResult> closeEvent) {
        AlertPane dialog = new AlertPane(parentPane);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCloseEvent(closeEvent);
        dialog.show();
    }

}
