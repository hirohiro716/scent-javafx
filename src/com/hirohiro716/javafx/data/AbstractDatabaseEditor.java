package com.hirohiro716.javafx.data;

import java.sql.SQLException;

import com.hirohiro716.database.AbstractBindTable;
import com.hirohiro716.database.AbstractDatabase;
import com.hirohiro716.database.DataNotFoundException;
import com.hirohiro716.javafx.dialog.database.DatabaseTryConnectPaneDialog;
import com.hirohiro716.javafx.dialog.database.InterfaceDatabaseTryConnectDialog.ConnectCallback;
import com.hirohiro716.javafx.dialog.database.InterfaceDatabaseTryConnectDialog.FailureCallback;
import com.hirohiro716.javafx.dialog.database.InterfaceDatabaseTryConnectDialog.QuestionDialogCallback;
import com.hirohiro716.javafx.dialog.database.InterfaceDatabaseTryConnectDialog.SuccessCallback;

import javafx.scene.layout.Pane;

/**
 * データベースの情報を編集するエディターの抽象クラス.
 * @author hiro
 * @param <D> データベースの型
 * @param <T> 編集する情報の型
 */
public abstract class AbstractDatabaseEditor<D extends AbstractDatabase, T extends AbstractBindTable> extends AbstractEditor<T> {

    @Override
    protected void editDataController() throws SQLException, DataNotFoundException {
        D database = this.createDatabase();
        this.connectDatabase(database);
        this.editDataController(database);
    }
    
    /**
     * Databaseのインスタンスを作成する.
     * @return 作成されたDatabaseのインスタンス
     */
    protected abstract D createDatabase();
    
    /**
     * Databaseの接続処理をする.
     * @param database 対象のDatabase
     * @throws SQLException 
     */
    protected abstract void connectDatabase(D database) throws SQLException;
    
    /**
     * 編集するデータの読み込みや排他ロック処理.
     * showまたはshowAndWaitメソッドを呼び出した際 beforeShowPrepareメソッドの前に自動実行される.
     * @param database 接続済みDatabase
     * @throws SQLException 
     * @throws DataNotFoundException 
     */
    protected abstract void editDataController(D database) throws SQLException, DataNotFoundException;
    
    /**
     * データベースに接続できるまでダイアログを表示して何度も試行する.
     * @param database 接続対象のDatabase
     * @param questionDialogCallback 確認
     */
    public void tryConnectDatabaseWithDialog(D database, QuestionDialogCallback questionDialogCallback) {
        AbstractDatabaseEditor<D, T> editor = this;
        DatabaseTryConnectPaneDialog<D> dialog = new DatabaseTryConnectPaneDialog<D>(database, new ConnectCallback<D>() {
            @Override
            public void call(D database) throws SQLException {
                editor.connectDatabase(database);
            }
        }, (Pane) this.getStage().getScene().getRoot());
        dialog.setQuestionDialogCallback(questionDialogCallback);
        dialog.setSuccessCallback(new SuccessCallback() {
            @Override
            public void call() {
                try {
                    editor.editDataController(database);
                } catch (Exception exception) {
                    editor.close();
                }
            }
        });
        dialog.setFailureCallback(new FailureCallback() {
            @Override
            public void call() {
                editor.close();
            }
        });
        dialog.connect();
    }
    
}
