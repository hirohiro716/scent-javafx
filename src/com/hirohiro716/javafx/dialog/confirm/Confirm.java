package com.hirohiro716.javafx.dialog.confirm;

import java.io.IOException;

import com.hirohiro716.javafx.FXMLLoader;
import com.hirohiro716.javafx.LayoutHelper;
import com.hirohiro716.javafx.control.EnterFireButton;
import com.hirohiro716.javafx.dialog.AbstractDialog;
import com.hirohiro716.javafx.dialog.DialogResult;
import javafx.application.Platform;
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
 * 確認メッセージ(OK/Cancel)を表示するクラス。
 *
 * @author hiro
 */
public class Confirm extends AbstractDialog<DialogResult> {

    @FXML
    private AnchorPane paneRoot;

    @FXML
    private Label labelTitle;

    @FXML
    private AnchorPane paneMessage;

    @FXML
    private EnterFireButton buttonOk;

    @FXML
    private EnterFireButton buttonCancel;

    @Override
    protected Label getLabelTitle() {
        return this.labelTitle;
    }

    @Override
    protected Pane createContentPane() {
        Confirm dialog = this;
        // Paneの生成
        FXMLLoader fxmlLoader;
        try {
            fxmlLoader = new FXMLLoader(this.getClass().getResource(this.getClass().getSimpleName() + ".fxml"), this);
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
        // ボタンのイベント定義
        this.setResult(DialogResult.CANCEL);
        this.buttonOk.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dialog.setResult(DialogResult.OK);
                dialog.close();
            }
        });
        this.buttonCancel.setOnAction(new EventHandler<ActionEvent>() {
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
                    dialog.buttonOk.fire();
                    break;
                case C:
                    dialog.buttonCancel.fire();
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
        Confirm dialog = this;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (dialog.defaultButton != null) {
                    switch (dialog.defaultButton) {
                    case OK:
                        dialog.buttonOk.requestFocus();
                        break;
                    case CANCEL:
                        dialog.buttonCancel.requestFocus();
                        break;
                    default:
                        break;
                    }
                }
            }
        });
    }

    @Override
    public boolean isClosableAtStackPaneClicked() {
        return true;
    }

    /**
     * メッセージ内容をセットする。
     *
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
     * メッセージに代わるNodeをセットする。
     *
     * @param node
     */
    public void setMessageNode(Node node) {
        this.paneMessage.getChildren().clear();
        this.paneMessage.getChildren().add(node);
    }

    private DialogResult defaultButton;

    /**
     * Enterキー押下時に作用するボタンを指定する。
     *
     * @param dialogResult
     */
    public void setDefaultButton(DialogResult dialogResult) {
        this.defaultButton = dialogResult;
    }

    /**
     * ダイアログを表示する。
     *
     * @param title タイトル
     * @param message メッセージ
     * @param closeEvent 閉じる際の処理
     */
    public static void show(String title, String message, CloseEventHandler<DialogResult> closeEvent) {
        Confirm dialog = new Confirm();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCloseEvent(closeEvent);
        dialog.show(null);
    }

    /**
     * ダイアログを表示する。
     *
     * @param title タイトル
     * @param message メッセージ
     * @param owner 親Stage
     * @param closeEvent 閉じる際の処理
     */
    public static void show(String title, String message, Stage owner, CloseEventHandler<DialogResult> closeEvent) {
        Confirm dialog = new Confirm();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCloseEvent(closeEvent);
        dialog.show(owner);
    }

    /**
     * ダイアログを表示する。
     *
     * @param title タイトル
     * @param message メッセージ
     * @return 結果
     */
    public static DialogResult showAndWait(String title, String message) {
        Confirm dialog = new Confirm();
        dialog.setTitle(title);
        dialog.setMessage(message);
        return dialog.showAndWait(null);
    }

    /**
     * ダイアログを表示する。
     *
     * @param title タイトル
     * @param message メッセージ
     * @param owner 親Stage
     * @return 結果
     */
    public static DialogResult showAndWait(String title, String message, Stage owner) {
        Confirm dialog = new Confirm();
        dialog.setTitle(title);
        dialog.setMessage(message);
        return dialog.showAndWait(owner);
    }

    /**
     * ダイアログを表示する。
     *
     * @param <T> javafx.scene.layout.Paneを継承したクラスオブジェクト
     * @param title タイトル
     * @param message メッセージ
     * @param parent 表示対象Pane
     * @param closeEvent 閉じる際の処理
     */
    public static <T extends Pane> void showOnPane(String title, String message, T parent, CloseEventHandler<DialogResult> closeEvent) {
        Confirm dialog = new Confirm();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCloseEvent(closeEvent);
        dialog.showOnPane(parent);
    }
}
