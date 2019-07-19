package com.hirohiro716.javafx.dialog.database;

import java.sql.SQLException;

import com.hirohiro716.StringConverter;
import com.hirohiro716.database.AbstractDatabase;
import com.hirohiro716.javafx.dialog.AbstractDialog.CloseEventHandler;
import com.hirohiro716.javafx.dialog.AbstractDialog;
import com.hirohiro716.javafx.dialog.DialogResult;
import com.hirohiro716.javafx.dialog.question.Question;

import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * データベース接続を繰り返すダイアログ.
 * @author hiro
 * @param <D> データベースの型
 */
public class DatabaseTryConnectDialog<D extends AbstractDatabase> {
    
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
    
    private QuestionDialogCallback questionDialogCallback;

    /**
     * 接続を再試行するかを確認するダイアログに対する処理を行うコールバックをセットする.
     * @param questionDialogCallback
     */
    public void setQuestionDialogCallback(QuestionDialogCallback questionDialogCallback) {
        this.questionDialogCallback = questionDialogCallback;
    }
    
    private SuccessCallback successCallback;

    /**
     * 接続が成功した場合の処理を行うコールバックをセットする.
     * @param successCallback
     */
    public void setSuccessCallback(SuccessCallback successCallback) {
        this.successCallback = successCallback;
    }
    
    private FailureCallback failureCallback;

    /**
     * 接続を諦めた場合に発生する例外を処理するコールバックをセットする.
     * @param failureCallback
     */
    public void setFailureCallback(FailureCallback failureCallback) {
        this.failureCallback = failureCallback;
    }

    /**
     * 接続が成功するまでダイアログを表示して試行する.
     * @param owner 親Stage
     */
    public void connect(Stage owner) {
        DatabaseTryConnectDialog<D> dialog = this;
        try {
            this.connectCallback.call(this.database);
            if (this.successCallback != null) {
                this.successCallback.call();
            }
        } catch (SQLException exception) {
            Question question = new Question();
            question.setTitle(AbstractDatabase.ERROR_DIALOG_TITLE);
            String message = StringConverter.join("再試行しますか？", StringConverter.LINE_SEPARATOR, exception.getMessage());
            question.setMessage(message);
            question.setCloseEvent(new CloseEventHandler<DialogResult>() {
                @Override
                public void handle(DialogResult resultValue) {
                    if (resultValue == DialogResult.YES) {
                        dialog.connect(owner);
                    } else {
                        if (dialog.failureCallback != null) {
                            dialog.failureCallback.call();
                        }
                    }
                }
            });
            question.show(owner);
            if (this.questionDialogCallback != null) {
                this.questionDialogCallback.call(question);
            }
        }

    }
    
    /**
     * 接続が成功するまでダイアログを表示して試行する.
     * @param owner 親Stage
     * @throws SQLException
     */
    public void connectAndWait(Stage owner) throws SQLException {
        boolean state = false;
        while (state == false) {
            try {
                this.connectCallback.call(this.database);
                if (this.successCallback != null) {
                    this.successCallback.call();
                }
                state = true;
            } catch (SQLException exception) {
                String message = StringConverter.join("再試行しますか？", StringConverter.LINE_SEPARATOR, exception.getMessage());
                if (Question.showAndWait(AbstractDatabase.ERROR_DIALOG_TITLE, message, owner) == DialogResult.NO) {
                    if (this.failureCallback != null) {
                        this.failureCallback.call();
                    }
                    throw exception;
                }
            }
        }
    }

    /**
     * 接続が成功するまでダイアログを表示して試行する.
     * @param parent 親Pane
     */
    public void connect(Pane parent) {
        DatabaseTryConnectDialog<D> dialog = this;
        try {
            this.connectCallback.call(this.database);
            if (this.successCallback != null) {
                this.successCallback.call();
            }
        } catch (SQLException exception) {
            Question question = new Question();
            question.setTitle(AbstractDatabase.ERROR_DIALOG_TITLE);
            String message = StringConverter.join("再試行しますか？", StringConverter.LINE_SEPARATOR, exception.getMessage());
            question.setMessage(message);
            question.setCloseEvent(new CloseEventHandler<DialogResult>() {
                @Override
                public void handle(DialogResult resultValue) {
                    if (resultValue == DialogResult.YES) {
                        dialog.connect(parent);
                    } else {
                        if (dialog.failureCallback != null) {
                            dialog.failureCallback.call();
                        }
                    }
                }
            });
            question.showOnPane(parent);
            if (this.questionDialogCallback != null) {
                this.questionDialogCallback.call(question);
            }
        }

    }

    /**
     * 接続処理を行うコールバッククラス.
     * @author hiro
     * @param <D> データベースの型
     */
    public abstract static class ConnectCallback<D extends AbstractDatabase> {
        
        /**
         * 接続処理を行うコールバック関数.
         * @param database 接続対象データベースクラス.
         * @throws SQLException
         */
        public abstract void call(D database) throws SQLException;
        
    }
    
    /**
     * 接続を再試行するかを確認するダイアログに対する処理を行うコールバック.
     * @author hiro
     */
    public abstract static class QuestionDialogCallback {
        
        /**
         * 接続を再試行するかを確認するダイアログに対する処理を行うコールバック関数.
         * @param dialog 接続を再試行するかを確認するダイアログ
         */
        public abstract void call(AbstractDialog<?> dialog);
        
    }
    
    /**
     * 接続が成功した場合の処理を行うコールバック.
     * @author hiro
     */
    public abstract static class SuccessCallback {
        
        /**
         * 接続が成功した場合の処理を行うコールバック関数.
         */
        public abstract void call();
    }

    /**
     * 接続を諦めた場合の処理を行うコールバッククラス.
     * @author hiro
     */
    public abstract static class FailureCallback {
        
        /**
         * 接続を諦めた場合の処理を行うコールバック関数.
         */
        public abstract void call();
        
    }
    
}
