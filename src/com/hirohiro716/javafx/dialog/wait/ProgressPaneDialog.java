package com.hirohiro716.javafx.dialog.wait;

import java.io.IOException;
import java.util.concurrent.Callable;

import com.hirohiro716.javafx.FXMLLoader;
import com.hirohiro716.javafx.LayoutHelper;
import com.hirohiro716.javafx.dialog.AbstractPaneDialog;
import com.hirohiro716.thread.Task;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

/**
 * 進捗状況の画面を表示するクラス.
 * @author hiro
 * @param <T>
 */
public class ProgressPaneDialog<T> extends AbstractPaneDialog<T> {

    @FXML
    private AnchorPane paneRoot;

    @FXML
    private Label labelTitle;

    @FXML
    private AnchorPane paneMessage;

    @FXML
    private ProgressBar progressBar;
    
    @FXML
    private Button buttonCancel;
    
    /**
     * コンストラクタ.
     * @param parentPane
     */
    public ProgressPaneDialog(Pane parentPane) {
        super(parentPane);
    }

    @Override
    public AnchorPane getContentPane() {
        return this.paneRoot;
    }

    @Override
    public void show() {
        ProgressPaneDialog<T> dialog = this;
        // ダイアログ表示
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ProgressDialog.class.getResource(ProgressDialog.class.getSimpleName() + ".fxml"), this);
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
        // キャンセル処理
        if (this.isCancelable) {
            this.buttonCancel.setVisible(true);
            this.buttonCancel.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    dialog.isCanceled = true;
                }
            });
        }
        // タスクの実行
        if (this.task != null) {
            this.task.start();
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

    boolean isCancelable = false;
    
    /**
     * ダイアログがキャンセル可能かをセットする. 初期値はfalse.
     * @param isCancelable キャンセル可能
     */
    public void setCancelable(boolean isCancelable) {
        this.isCancelable = isCancelable;
    }

    private Task<T> task;

    /**
     * 内部で実行する処理内容をセットする.
     * @param callable
     */
    public void setCallable(Callable<T> callable) {
        ProgressPaneDialog<T> dialog = this;
        this.task = new Task<>(new Callable<T>() {
            @Override
            public T call() throws Exception {
                try {
                    dialog.setResult(callable.call());
                    return dialog.getResult();
                } catch (Exception exception) {
                    dialog.exception = exception;
                    throw exception;
                }
            }
        });
    }

    /**
     * 進捗状況を更新する.
     * @param progress 現在の進捗
     * @param maxProgress 最大の進捗
     */
    public void updateProgress(double progress, double maxProgress) {
        ProgressPaneDialog<T> dialog = this;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                dialog.progressBar.setProgress(progress / maxProgress);
            }
        });
    }
    
    boolean isCanceled = false;
    
    /**
     * ダイアログがキャンセルされているかどうかを取得する.
     * @return キャンセルされているかどうか
     */
    public boolean isCanceled() {
        return this.isCanceled;
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

    @Override
    public boolean isClosableAtStackPaneClicked() {
        return false;
    }

}
