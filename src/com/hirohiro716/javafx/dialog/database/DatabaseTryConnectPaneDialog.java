package com.hirohiro716.javafx.dialog.database;

import java.sql.SQLException;

import com.hirohiro716.StringConverter;
import com.hirohiro716.database.AbstractDatabase;
import com.hirohiro716.javafx.dialog.DialogResult;
import com.hirohiro716.javafx.dialog.InterfaceDialog.CloseEventHandler;
import com.hirohiro716.javafx.dialog.question.QuestionPane;

import javafx.scene.layout.Pane;

/**
 * データベース接続を繰り返すダイアログ.
 * @author hiro
 * @param <D> データベースの型
 */
public class DatabaseTryConnectPaneDialog<D extends AbstractDatabase> implements InterfaceDatabaseTryConnectDialog<D> {
    
    private D database;
    
    private ConnectCallback<D> connectCallback;
    
    private Pane parentPane;
    
    /**
     * コンストラクタ.
     * @param database 接続対象Database
     * @param connectCallback 接続処理コールバック
     * @param parentPane ダイアログ表示対象のPane
     */
    public DatabaseTryConnectPaneDialog(D database, ConnectCallback<D> connectCallback, Pane parentPane) {
        this.database = database;
        this.connectCallback = connectCallback;
        this.parentPane = parentPane;
    }
    
    private QuestionDialogCallback questionDialogCallback;
    
    @Override
    public void setQuestionDialogCallback(QuestionDialogCallback questionDialogCallback) {
        this.questionDialogCallback = questionDialogCallback;
    }

    private SuccessCallback successCallback;
    
    @Override
    public void setSuccessCallback(SuccessCallback successCallback) {
        this.successCallback = successCallback;
    }
    
    private FailureCallback failureCallback;
    
    @Override
    public void setFailureCallback(FailureCallback failureCallback) {
        this.failureCallback = failureCallback;
    }
    
    @Override
    public void connect() {
        DatabaseTryConnectPaneDialog<D> dialog = this;
        try {
            this.connectCallback.call(this.database);
            if (this.successCallback != null) {
                this.successCallback.call();
            }
        } catch (SQLException exception) {
            QuestionPane question = new QuestionPane(this.parentPane);
            question.setTitle(AbstractDatabase.ERROR_DIALOG_TITLE);
            String message = StringConverter.join("再試行しますか？", StringConverter.LINE_SEPARATOR, exception.getMessage());
            question.setMessage(message);
            question.setCloseEvent(new CloseEventHandler<DialogResult>() {
                @Override
                public void handle(DialogResult resultValue) {
                    if (resultValue == DialogResult.YES) {
                        dialog.connect();
                    } else {
                        if (dialog.failureCallback != null) {
                            dialog.failureCallback.call();
                        }
                    }
                }
            });
            question.show();
            if (this.questionDialogCallback != null) {
                this.questionDialogCallback.call(question);
            }
        }

    }

}
