package com.hirohiro716.javafx.dialog.question;

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
 * 確認メッセージを表示するクラス.
 * @author hiro
 */
public class FullQuestion extends AbstractDialog<DialogResult> {

    @FXML
    private Label labelTitle;

    @FXML
    private AnchorPane paneMessage;

    @FXML
    private EnterFireButton buttonYes;

    @FXML
    private EnterFireButton buttonNo;

    @FXML
    private EnterFireButton buttonCancel;

    /**
     * コンストラクタ
     */
    public FullQuestion() {
        super();
    }

    /**
     * コンストラクタ
     * @param parentStage
     */
    public FullQuestion(Stage parentStage) {
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
        this.buttonYes.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FullQuestion.this.setResult(DialogResult.YES);
                FullQuestion.this.close();
                event.consume();
            }
        });
        this.buttonNo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FullQuestion.this.setResult(DialogResult.NO);
                FullQuestion.this.close();
                event.consume();
            }
        });
        this.buttonCancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FullQuestion.this.setResult(DialogResult.CANCEL);
                FullQuestion.this.close();
                event.consume();
            }
        });
        // キーボードイベント定義
        this.getDialogPane().addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                case Y:
                    FullQuestion.this.buttonYes.fire();
                    event.consume();
                    break;
                case N:
                    FullQuestion.this.buttonNo.fire();
                    event.consume();
                    break;
                case C:
                    FullQuestion.this.buttonCancel.fire();
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
            case YES:
                this.buttonYes.requestFocus();
                break;
            case NO:
                this.buttonNo.requestFocus();
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
            FXMLLoader fxmlHelper = new FXMLLoader(this.getClass().getResource("FullQuestion.fxml"), this);
            this.show(fxmlHelper.getPaneRoot());
        } catch (IOException exception) {
        }
    }

    @Override
    public DialogResult showAndWait() {
        try {
            FXMLLoader fxmlHelper = new FXMLLoader(this.getClass().getResource("FullQuestion.fxml"), this);
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
        FullQuestion dialog = new FullQuestion();
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
        FullQuestion dialog = new FullQuestion(parentStage);
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
        FullQuestion dialog = new FullQuestion();
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
        FullQuestion dialog = new FullQuestion(parentStage);
        dialog.setTitle(title);
        dialog.setMessage(message);
        return dialog.showAndWait();
    }

}
