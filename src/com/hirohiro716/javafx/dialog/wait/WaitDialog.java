package com.hirohiro716.javafx.dialog.wait;

import java.io.IOException;
import java.util.concurrent.Callable;

import com.hirohiro716.javafx.FXMLLoader;
import com.hirohiro716.javafx.LayoutHelper;
import com.hirohiro716.javafx.dialog.AbstractDialog;
import com.hirohiro716.thread.Task;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

/**
 * 待機画面を表示するクラス.
 * @author hiro
 * @param <T>
 */
public class WaitDialog<T> extends AbstractDialog<T> {

    @FXML
    private AnchorPane paneRoot;

    @FXML
    private Label labelTitle;

    @FXML
    private AnchorPane paneMessage;

    @Override
    protected Label getLabelTitle() {
        return this.labelTitle;
    }

    @Override
    protected Pane createContentPane() {
        // Paneの生成
        FXMLLoader fxmlLoader;
        try {
            fxmlLoader = new FXMLLoader(this.getClass().getResource(this.getClass().getSimpleName() + ".fxml"), this);
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
        return fxmlLoader.getPaneRoot();
    }

    @Override
    public void breforeShowPrepare() {
        WaitDialog<T> dialog = this;
        // タスクの実行
        if (this.task != null) {
            this.task.start();
        } else {
            if (this.isAutoClose) {
                dialog.close();
            }
        }
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
        this.paneMessage.getChildren().add(label);
        LayoutHelper.setAnchor(label, 0, 0, 0, 0);
    }

    /**
     * メッセージに代わるNodeをセットする.
     * @param node
     */
    public void setMessageNode(Node node) {
        this.paneMessage.getChildren().clear();
        this.paneMessage.getChildren().add(node);
    }

    private boolean isAutoClose = true;
    
    /**
     * タスク終了後に自動的にダイアログを閉じるかどうかを指定する. 初期値はtrue.
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
                    if (dialog.isAutoClose) {
                        dialog.close();
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

    /**
     * 内部処理で発生した例外としてセットする.
     * @param exception 例外
     */
    public void setException(Exception exception) {
        this.exception = exception;
    }

}
