package com.hirohiro716.javafx.dialog.database;

import java.sql.SQLException;

import com.hirohiro716.database.AbstractDatabase;
import com.hirohiro716.javafx.dialog.InterfaceDialog;

/**
 * データベース接続を繰り返すダイアログのインターフェース.
 * @author hiro
 * @param <D> データベースの型
 */
public interface InterfaceDatabaseTryConnectDialog<D extends AbstractDatabase> {

    /**
     * 接続を再試行するかを確認するダイアログに対する処理を行うコールバックをセットする.
     * @param questionDialogCallback
     */
    public abstract void setQuestionDialogCallback(QuestionDialogCallback questionDialogCallback);

    /**
     * 接続が成功した場合の処理を行うコールバックをセットする.
     * @param successCallback
     */
    public abstract void setSuccessCallback(SuccessCallback successCallback);
    
    /**
     * 接続を諦めた場合に発生する例外を処理するコールバックをセットする.
     * @param failureCallback
     */
    public abstract void setFailureCallback(FailureCallback failureCallback);
    
    /**
     * 接続が成功するまでダイアログを表示して試行する.
     * @throws Exception
     */
    public abstract void connect() throws Exception;

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
        public abstract void call(InterfaceDialog<?> dialog);
        
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
