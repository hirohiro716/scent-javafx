package com.hirohiro716.javafx.dialog.database;

import java.sql.SQLException;

import com.hirohiro716.StringConverter;
import com.hirohiro716.database.AbstractDatabase;
import com.hirohiro716.javafx.dialog.DialogResult;
import com.hirohiro716.javafx.dialog.InterfaceDialog.CloseEventHandler;
import com.hirohiro716.javafx.dialog.question.Question;
import javafx.stage.Stage;

/**
 * データベース接続を繰り返すダイアログ.
 * @author hiro
 * @param <D> データベースの型
 */
public class DatabaseTryConnectDialog<D extends AbstractDatabase> implements InterfaceDatabaseTryConnectDialog<D> {
    
    private D database;
    
    private ConnectCallback<D> connectCallback;
    
    /**
     * コンストラクタ.
     * @param database 接続対象Database
     * @param connectCallback 接続処理コールバック
     */
    public DatabaseTryConnectDialog(D database, ConnectCallback<D> connectCallback) {
        this.database = database;
        this.connectCallback = connectCallback;
    }
    
    private Stage parentStage;
    
    /**
     * ParentStageをセットする.
     * @param parentStage
     */
    public void setParentStage(Stage parentStage) {
        this.parentStage = parentStage;
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
        DatabaseTryConnectDialog<D> dialog = this;
        try {
            this.connectCallback.call(this.database);
            this.successCallback.call();
        } catch (SQLException exception) {
            Question question = new Question(this.parentStage);
            question.setTitle(AbstractDatabase.ERROR_DIALOG_TITLE);
            String message = StringConverter.join("再試行しますか？", StringConverter.LINE_SEPARATOR, exception.getMessage());
            question.setMessage(message);
            question.setCloseEvent(new CloseEventHandler<DialogResult>() {
                @Override
                public void handle(DialogResult resultValue) {
                    if (resultValue == DialogResult.YES) {
                        dialog.connect();
                    } else {
                        dialog.failureCallback.call();
                    }
                }
            });
            question.show();
            this.questionDialogCallback.call(question);
        }

    }
    
    /**
     * 接続が成功するまでダイアログを表示して試行する.
     * @throws SQLException
     */
    public void connectAndWait() throws SQLException {
        boolean state = false;
        while (state == false) {
            try {
                this.connectCallback.call(this.database);
                this.successCallback.call();
                state = true;
            } catch (SQLException exception) {
                String message = StringConverter.join("再試行しますか？", StringConverter.LINE_SEPARATOR, exception.getMessage());
                if (Question.showAndWait(AbstractDatabase.ERROR_DIALOG_TITLE, message) == DialogResult.NO) {
                    this.failureCallback.call();
                    throw exception;
                }
            }
        }
    }
    
}
