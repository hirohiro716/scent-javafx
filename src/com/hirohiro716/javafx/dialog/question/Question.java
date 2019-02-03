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
public class Question extends AbstractDialog<DialogResult> {

    @FXML
    private AnchorPane paneRoot;

    @FXML
    private Label labelTitle;

    @FXML
    private AnchorPane paneMessage;

    @FXML
    private EnterFireButton buttonYes;

    @FXML
    private EnterFireButton buttonNo;

    /**
     * コンストラクタ.
     */
    public Question() {
        super();
    }

    /**
     * コンストラクタ.
     * @param parentStage
     */
    public Question(Stage parentStage) {
        super(parentStage);
    }

    @Override
    public AnchorPane getContentPane() {
        return this.paneRoot;
    }

    @Override
    protected void preparationCallback() {
        Question dialog = this;
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
                dialog.setResult(DialogResult.YES);
                dialog.close();
            }
        });
        this.buttonNo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dialog.setResult(DialogResult.NO);
                dialog.close();
            }
        });
        // キーボードイベント定義
        this.getStackPane().addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                case Y:
                    dialog.buttonYes.fire();
                    break;
                case N:
                    dialog.buttonNo.fire();
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
            default:
                break;
            }
        }
    }

    @Override
    public void show() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource(this.getClass().getSimpleName() + ".fxml"), this);
            this.show(fxmlLoader.getPaneRoot());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public DialogResult showAndWait() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource(this.getClass().getSimpleName() + ".fxml"), this);
            return this.showAndWait(fxmlLoader.getPaneRoot());
        } catch (IOException exception) {
            exception.printStackTrace();
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

    @Override
    public boolean isClosableAtStackPaneClicked() {
        return false;
    }

    /**
     * ダイアログを表示する.
     * @param title タイトル
     * @param message メッセージ
     * @param closeEvent 閉じる際の処理
     */
    public static void show(String title, String message, CloseEventHandler<DialogResult> closeEvent) {
        Question dialog = new Question();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCloseEvent(closeEvent);
        dialog.show();
    }

    /**
     * ダイアログを表示する.
     * @param title タイトル
     * @param message メッセージ
     * @param parentStage 親Stage
     * @param closeEvent 閉じる際の処理
     */
    public static void show(String title, String message, Stage parentStage, CloseEventHandler<DialogResult> closeEvent) {
        Question dialog = new Question(parentStage);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCloseEvent(closeEvent);
        dialog.show();
    }

    /**
     * ダイアログを表示する.
     * @param title タイトル
     * @param message メッセージ
     * @return 結果
     */
    public static DialogResult showAndWait(String title, String message) {
        Question dialog = new Question();
        dialog.setTitle(title);
        dialog.setMessage(message);
        return dialog.showAndWait();
    }

    /**
     * ダイアログを表示する.
     * @param title タイトル
     * @param message メッセージ
     * @param parentStage 親Stage
     * @return 結果
     */
    public static DialogResult showAndWait(String title, String message, Stage parentStage) {
        Question dialog = new Question(parentStage);
        dialog.setTitle(title);
        dialog.setMessage(message);
        return dialog.showAndWait();
    }

}
