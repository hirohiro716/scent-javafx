package com.hirohiro716.javafx.dialog.wait;

import java.io.IOException;
import java.util.concurrent.Callable;

import com.hirohiro716.javafx.FXMLLoader;
import com.hirohiro716.javafx.LayoutHelper;
import com.hirohiro716.javafx.dialog.AbstractPaneDialog;
import com.hirohiro716.thread.Task;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

/**
 * 待機画面を表示するクラス.
 * @author hiro
 * @param <T>
 */
public class WaitPaneDialog<T> extends AbstractPaneDialog<T> {

    @FXML
    private Label labelTitle;

    @FXML
    private AnchorPane paneMessage;

    /**
     * コンストラクタ.
     * @param parentPane
     */
    public WaitPaneDialog(Pane parentPane) {
        super(parentPane);
    }

    @Override
    public void show() {
        // ダイアログ表示
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(WaitDialog.class.getResource(WaitDialog.class.getSimpleName() + ".fxml"), this);
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
        // タスクの実行
        if (this.task != null) {
            this.task.start();
        } else {
            if (this.isAutoClose) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        WaitPaneDialog.this.close();
                    }
                });
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

    private boolean isAutoClose = true;
    
    /**
     * タスク終了後に自動的にダイアログを閉じるかどうかを指定する.
     * @param isAutoClose
     */
    public void setAutoClose(boolean isAutoClose) {
        this.isAutoClose = isAutoClose;
    }

    private Task<T> task;

    /**
     * 内部で実行する処理内容をセットする.
     * @param callable
     */
    public void setCallable(Callable<T> callable) {
        WaitPaneDialog<T> dialog = this;
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
                    if (dialog.isAutoClose) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                dialog.close();
                            }
                        });
                    }
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

}
