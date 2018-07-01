package com.hirohiro716.javafx.dialog.confirm;

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
import javafx.stage.Stage;

/**
 * 確認メッセージ（OK/Cancel）を表示するクラス.
 * @author hiro
 */
public class Confirm extends AbstractDialog<DialogResult> {

    @FXML
    private Label labelTitle;

    @FXML
    private AnchorPane paneMessage;

    @FXML
    private EnterFireButton buttonOk;

    @FXML
    private EnterFireButton buttonCancel;

    /**
     * コンストラクタ
     */
    public Confirm() {
        super();
    }

    /**
     * コンストラクタ
     * @param parentStage
     */
    public Confirm(Stage parentStage) {
        super(parentStage);
    }

    @Override
    protected void preparationCallback() {
        // タイトルのセット
        this.getStage().setTitle(this.title);
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
        this.buttonOk.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Confirm.this.setResult(DialogResult.OK);
                Confirm.this.close();
                event.consume();
            }
        });
        this.buttonCancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Confirm.this.setResult(DialogResult.CANCEL);
                Confirm.this.close();
                event.consume();
            }
        });
        // キーボードイベント定義
        this.getDialogPane().addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                case O:
                    Confirm.this.buttonOk.fire();
                    event.consume();
                    break;
                case C:
                    Confirm.this.buttonCancel.fire();
                    event.consume();
                    break;
                default:
                    break;
                }
            }
        });
        // 初期カーソル
        if (this.defaultButton != null) {
            switch (this.defaultButton) {
            case OK:
                this.buttonOk.requestFocus();
                break;
            case CANCEL:
                this.buttonCancel.requestFocus();
                break;
            default:
            }
        }
    }

    @Override
    public void show() {
        try {
            FXMLLoader fxmlHelper = new FXMLLoader(this.getClass().getResource("Confirm.fxml"), this);
            this.show(fxmlHelper.getPaneRoot());
        } catch (IOException exception) {
        }
    }

    @Override
    public DialogResult showAndWait() {
        try {
            FXMLLoader fxmlHelper = new FXMLLoader(this.getClass().getResource("Confirm.fxml"), this);
            return this.showAndWait(fxmlHelper.getPaneRoot());
        } catch (IOException exception) {
            return null;
        }
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

    private DialogResult defaultButton;

    /**
     * Enterキー押下時に作用するボタンを指定する.
     * @param dialogResult
     */
    public void setDefaultButton(DialogResult dialogResult) {
        this.defaultButton = dialogResult;
    }

    /**
     * ダイアログを表示
     * @param title タイトル
     * @param message メッセージ
     * @param closeEvent 閉じる際の処理
     */
    public static void show(String title, String message, CloseEventHandler<DialogResult> closeEvent) {
        Confirm dialog = new Confirm();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCloseEvent(closeEvent);
        dialog.show();
    }

    /**
     * ダイアログを表示
     * @param title タイトル
     * @param message メッセージ
     * @param parentStage 親Stage
     * @param closeEvent 閉じる際の処理
     */
    public static void show(String title, String message, Stage parentStage, CloseEventHandler<DialogResult> closeEvent) {
        Confirm dialog = new Confirm(parentStage);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCloseEvent(closeEvent);
        dialog.show();
    }

    /**
     * ダイアログを表示
     * @param title タイトル
     * @param message メッセージ
     * @return 結果
     */
    public static DialogResult showAndWait(String title, String message) {
        Confirm dialog = new Confirm();
        dialog.setTitle(title);
        dialog.setMessage(message);
        return dialog.showAndWait();
    }

    /**
     * ダイアログを表示
     * @param title タイトル
     * @param message メッセージ
     * @param parentStage 親Stage
     * @return 結果
     */
    public static DialogResult showAndWait(String title, String message, Stage parentStage) {
        Confirm dialog = new Confirm(parentStage);
        dialog.setTitle(title);
        dialog.setMessage(message);
        return dialog.showAndWait();
    }

}
