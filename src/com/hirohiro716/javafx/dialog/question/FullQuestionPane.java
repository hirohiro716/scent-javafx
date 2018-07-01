package com.hirohiro716.javafx.dialog.question;

import java.io.IOException;

import com.hirohiro716.javafx.FXMLLoader;
import com.hirohiro716.javafx.LayoutHelper;
import com.hirohiro716.javafx.control.EnterFireButton;
import com.hirohiro716.javafx.dialog.AbstractDialog.CloseEventHandler;
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
 * 確認メッセージを表示するクラス.
 * @author hiro
 */
public class FullQuestionPane extends AbstractPaneDialog<DialogResult> {

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
     * @param parentPane
     */
    public FullQuestionPane(Pane parentPane) {
        super(parentPane);
    }

    @Override
    public void show() {
        // ダイアログ表示
        try {
            FXMLLoader fxmlHelper = new FXMLLoader(this.getClass().getResource("FullQuestion.fxml"), this);
            this.show(fxmlHelper.getPaneRoot());
        } catch (IOException exception) {
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
        this.buttonYes.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FullQuestionPane.this.setResult(DialogResult.YES);
                FullQuestionPane.this.close();
                event.consume();
            }
        });
        this.buttonNo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FullQuestionPane.this.setResult(DialogResult.NO);
                FullQuestionPane.this.close();
                event.consume();
            }
        });
        this.buttonCancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FullQuestionPane.this.setResult(DialogResult.CANCEL);
                FullQuestionPane.this.close();
                event.consume();
            }
        });
        // キーボードイベント定義
        this.getDialogPane().addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                case Y:
                    FullQuestionPane.this.buttonYes.fire();
                    event.consume();
                    break;
                case N:
                    FullQuestionPane.this.buttonNo.fire();
                    event.consume();
                    break;
                case C:
                    FullQuestionPane.this.buttonCancel.fire();
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
     * @param <T> javafx.scene.layout.Paneを継承したクラスオブジェクト
     * @param title タイトル
     * @param message メッセージ
     * @param parentPane 表示対象Pane
     * @param closeEvent 閉じる際の処理
     */
    public static <T extends Pane> void show(String title, String message, T parentPane, CloseEventHandler<DialogResult> closeEvent) {
        FullQuestionPane dialog = new FullQuestionPane(parentPane);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCloseEvent(closeEvent);
        dialog.show();
    }

}
