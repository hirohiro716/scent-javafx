package com.hirohiro716.javafx.dialog.alert;

import java.io.IOException;

import com.hirohiro716.javafx.FXMLLoader;
import com.hirohiro716.javafx.LayoutHelper;
import com.hirohiro716.javafx.control.EnterFireButton;
import com.hirohiro716.javafx.dialog.AbstractDialog;
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
import javafx.stage.Stage;

/**
 * メッセージを表示するクラス.
 * @author hiro
 */
public class Alert extends AbstractDialog<DialogResult> {

    @FXML
    private AnchorPane paneRoot;

    @FXML
    private Label labelTitle;

    @FXML
    private AnchorPane paneMessage;

    @FXML
    private EnterFireButton buttonOk;

    @Override
    protected Label getLabelTitle() {
        return this.labelTitle;
    }
    
    @Override
    protected Pane createContentPane() {
        Alert dialog = this;
        // Paneの生成
        FXMLLoader fxmlLoader;
        try {
            fxmlLoader = new FXMLLoader(this.getClass().getResource(this.getClass().getSimpleName() + ".fxml"), this);
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
        // タイトルのセット
        this.labelTitle.setText(this.getTitle());
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
        return fxmlLoader.getPaneRoot();
    }

    @Override
    public void breforeShowPrepare() {
    }

    @Override
    public boolean isClosableAtStackPaneClicked() {
        return false;
    }

    /**
     * メッセージ内容をセットする.
     * @param message
     */
    public void setMessage(String message) {
        this.paneMessage.getChildren().clear();
        Label label = new Label(message);
        label.setWrapText(true);
        label.setAlignment(Pos.TOP_LEFT);
        this.paneMessage.getChildren().add(label);
        LayoutHelper.setAnchor(label, 30, 0, 0, 0);
    }

    /**
     * メッセージに代わるNodeをセットする.
     * @param node
     */
    public void setMessageNode(Node node) {
        this.paneMessage.getChildren().clear();
        this.paneMessage.getChildren().add(node);
    }

    /**
     * ダイアログを表示する.
     * @param title タイトル
     * @param message メッセージ
     * @param closeEvent 閉じる際の処理
     */
    public static void show(String title, String message, CloseEventHandler<DialogResult> closeEvent) {
        Alert dialog = new Alert();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCloseEvent(closeEvent);
        dialog.show(null);
    }

    /**
     * ダイアログを表示する.
     * @param title タイトル
     * @param message メッセージ
     * @param owner 親Stage
     * @param closeEvent 閉じる際の処理
     */
    public static void show(String title, String message, Stage owner, CloseEventHandler<DialogResult> closeEvent) {
        Alert dialog = new Alert();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCloseEvent(closeEvent);
        dialog.show(owner);
    }

    /**
     * ダイアログを表示する.
     * @param title タイトル
     * @param message メッセージ
     */
    public static void show(String title, String message) {
        Alert dialog = new Alert();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.show(null);
    }

    /**
     * ダイアログを表示する.
     * @param title タイトル
     * @param message メッセージ
     * @param owner 親Stage
     */
    public static void show(String title, String message, Stage owner) {
        Alert dialog = new Alert();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.show(owner);
    }

    /**
     * ダイアログを表示する.
     * @param title タイトル
     * @param message メッセージ
     * @return 結果
     */
    public static DialogResult showAndWait(String title, String message) {
        Alert dialog = new Alert();
        dialog.setTitle(title);
        dialog.setMessage(message);
        return dialog.showAndWait(null);
    }

    /**
     * ダイアログを表示する.
     * @param title タイトル
     * @param message メッセージ
     * @param owner 親Stage
     * @return 結果
     */
    public static DialogResult showAndWait(String title, String message, Stage owner) {
        Alert dialog = new Alert();
        dialog.setTitle(title);
        dialog.setMessage(message);
        return dialog.showAndWait(owner);
    }

    /**
     * ダイアログを表示する.
     * @param <T> javafx.scene.layout.Paneを継承したクラスオブジェクト
     * @param title タイトル
     * @param message メッセージ
     * @param parent 表示対象Pane
     */
    public static <T extends Pane> void showOnPane(String title, String message, T parent) {
        Alert dialog = new Alert();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.showOnPane(parent);
    }

    /**
     * ダイアログを表示する.
     * @param <T> javafx.scene.layout.Paneを継承したクラスオブジェクト
     * @param title タイトル
     * @param message メッセージ
     * @param parent 表示対象Pane
     * @param closeEvent 閉じる際の処理
     */
    public static <T extends Pane> void showOnPane(String title, String message, T parent, CloseEventHandler<DialogResult> closeEvent) {
        Alert dialog = new Alert();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCloseEvent(closeEvent);
        dialog.showOnPane(parent);
    }

}
