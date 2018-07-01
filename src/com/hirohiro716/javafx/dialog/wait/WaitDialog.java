package com.hirohiro716.javafx.dialog.wait;

import java.io.IOException;
import java.util.concurrent.Callable;

import com.hirohiro716.javafx.FXMLLoader;
import com.hirohiro716.javafx.LayoutHelper;
import com.hirohiro716.javafx.dialog.AbstractDialog;
import com.hirohiro716.thread.Task;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * 待機画面を表示するクラス.
 * @author hiro
 * @param <T>
 */
public class WaitDialog<T> extends AbstractDialog<T> {

    @FXML
    private Label labelTitle;

    @FXML
    private AnchorPane paneMessage;

    /**
     * コンストラクタ
     */
    public WaitDialog() {
        super();
    }

    /**
     * コンストラクタ
     * @param parentStage
     */
    public WaitDialog(Stage parentStage) {
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
        // タスクの実行
        if (this.task == null) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    WaitDialog.this.close();
                }
            });
            return;
        }
        this.task.start();
    }

    @Override
    public void show() {
        try {
            FXMLLoader fxmlHelper = new FXMLLoader(this.getClass().getResource("Wait.fxml"), this);
            this.show(fxmlHelper.getPaneRoot());
        } catch (IOException exception) {
        }
    }

    @Override
    public T showAndWait() {
        try {
            FXMLLoader fxmlHelper = new FXMLLoader(this.getClass().getResource("Wait.fxml"), this);
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

    private Task<T> task;

    /**
     * 内部で実行する処理内容をセットする.
     * @param callable
     */
    public void setCallable(Callable<T> callable) {
        WaitDialog<T> dialog = this;
        this.task = new Task<>(new Callable<T>() {
            @Override
            public T call() throws Exception {
                try {
                    dialog.setResult(callable.call());
                    return dialog.getResult();
                } catch (Exception exception) {
                    dialog.exception = exception;
                    throw exception;
                } finally {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            dialog.close();
                        }
                    });
                }
            }
        });
    }

    private Exception exception;

    /**
     * 内部処理で例外が発生した場合に取得できる.
     * @return Exception
     */
    public Exception getException() {
        return this.exception;
    }

    /**
     * ダイアログを表示
     * @param <T> Taskで処理された戻り値型
     * @param title タイトル
     * @param message メッセージ
     * @param parentStage 親Stage
     * @param callable 処理内容
     * @return 結果
     */
    public static <T> T showAndWait(String title, String message, Stage parentStage, Callable<T> callable) {
        WaitDialog<T> dialog = new WaitDialog<>(parentStage);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCallable(callable);
        return dialog.showAndWait();
    }

}
