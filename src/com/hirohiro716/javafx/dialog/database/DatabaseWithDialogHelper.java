package com.hirohiro716.javafx.dialog.database;

import java.sql.SQLException;

import com.hirohiro716.StringConverter;
import com.hirohiro716.database.AbstractDatabase;
import com.hirohiro716.javafx.dialog.DialogResult;
import com.hirohiro716.javafx.dialog.question.Question;

/**
 * データベースの抽象クラス.
 * @author hiro
 */
public class DatabaseWithDialogHelper {
    
    /**
     * 接続を成功するまでダイアログを表示して試行する.
     * @param <D> 接続を試行するAbstractDatabaseを継承したデータベースクラス.
     * @param database 接続対象データベースクラス.
     * @param connectCallback 再帰的に接続を試行するコールバック関数.
     * @throws SQLException
     * @throws ClassNotFoundException 
     */
    public static <D extends AbstractDatabase> void tryConnectWithDialog(D database, ConnectCallback<D> connectCallback) throws SQLException, ClassNotFoundException {
        boolean state = false;
        while (state == false) {
            try {
                connectCallback.connect(database);
                state = true;
            } catch (SQLException exception) {
                String message = StringConverter.join("再試行しますか？", StringConverter.LINE_SEPARATOR, exception.getMessage());
                if (Question.showAndWait(AbstractDatabase.ERROR_DIALOG_TITLE, message) == DialogResult.NO) {
                    throw exception;
                }
            }
        }
    }
    
    /**
     * 接続処理を行うコールバッククラス.
     * @author hiro
     * @param <D> 接続を試行するAbstractDatabaseを継承したデータベースクラス.
     */
    public abstract static class ConnectCallback<D extends AbstractDatabase> {
        
        /**
         * 接続処理を行うコールバック関数.
         * @param database 接続対象データベースクラス.
         * @throws SQLException
         * @throws ClassNotFoundException
         */
        public abstract void connect(D database) throws SQLException, ClassNotFoundException;
        
    }

}
