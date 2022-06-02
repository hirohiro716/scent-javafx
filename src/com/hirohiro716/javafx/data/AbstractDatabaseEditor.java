package com.hirohiro716.javafx.data;

import java.sql.SQLException;

import com.hirohiro716.ExceptionHelper;
import com.hirohiro716.database.AbstractBindTable;
import com.hirohiro716.database.AbstractDatabase;
import com.hirohiro716.database.DataNotFoundException;
import com.hirohiro716.javafx.dialog.DialogResult;
import com.hirohiro716.javafx.dialog.AbstractDialog.CloseEventHandler;
import com.hirohiro716.javafx.dialog.AbstractDialog.DialogCallback;
import com.hirohiro716.javafx.dialog.alert.Alert;
import com.hirohiro716.javafx.dialog.database.DatabaseTryConnectDialog;
import com.hirohiro716.javafx.dialog.database.DatabaseTryConnectDialog.ConnectCallback;
import com.hirohiro716.javafx.dialog.database.DatabaseTryConnectDialog.FailureCallback;
import com.hirohiro716.javafx.dialog.database.DatabaseTryConnectDialog.SuccessCallback;
import com.hirohiro716.javafx.dialog.question.Question;

import javafx.scene.layout.Pane;

/**
 * データベースの情報を編集するエディターの抽象クラス。
 *
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
     * Databaseのインスタンスを作成する。
     *
     * @return 作成されたDatabaseのインスタンス
     */
    protected abstract D createDatabase();
    
    /**
     * Databaseのインスタンスを取得する。
     *
     * @return 現在使用中のDatabaseのインスタンス
     */
    protected abstract D getDatabase();
    
    /**
     * Databaseの接続処理をする。
     *
     * @param database 対象のDatabase
     * @throws SQLException 
     */
    protected abstract void connectDatabase(D database) throws SQLException;
    
    /**
     * 編集するデータの読み込みや排他ロック処理。
     *
     * showまたはshowAndWaitメソッドを呼び出した際 beforeShowPrepareメソッドの前に自動実行される。
     *
     * @param database 接続済みDatabase
     * @throws SQLException 
     * @throws DataNotFoundException 
     */
    protected abstract void editDataController(D database) throws SQLException, DataNotFoundException;

    /**
     * データベースに接続できるまでダイアログを表示して何度も試行する。
     *
     * @param successRunnable 接続成功時の処理
     */
    public abstract void tryConnectDatabaseWithDialog(Runnable successRunnable);
    
    /**
     * データベースに接続できるまでダイアログを表示して何度も試行する。
     *
     * @param successRunnable 接続成功時の処理
     * @param questionDialogCallback 確認
     */
    public void tryConnectDatabaseWithDialog(Runnable successRunnable, DialogCallback<Question> questionDialogCallback) {
        AbstractDatabaseEditor<D, T> editor = this;
        D database = this.createDatabase();
        DatabaseTryConnectDialog<D> dialog = new DatabaseTryConnectDialog<D>(database, new ConnectCallback<D>() {
            @Override
            public void call(D database) throws SQLException {
                editor.connectDatabase(database);
            }
        });
        dialog.setQuestionDialogCallback(questionDialogCallback);
        dialog.setSuccessCallback(new SuccessCallback() {
            @Override
            public void call() {
                editor.tryEditAgainDataControllerWithDialog(database, successRunnable);
            }
        });
        dialog.setFailureCallback(new FailureCallback() {
            @Override
            public void call() {
                editor.close();
            }
        });
        dialog.connect((Pane) this.getStage().getScene().getRoot());
    }
    
    /**
     * 情報を再度編集状態にするまでダイアログを表示して何度も試行する。
     *
     * @param database 接続済みDatabase
     * @param successRunnable 編集成功時の処理
     */
    public abstract void tryEditAgainDataControllerWithDialog(D database, Runnable successRunnable);

    /**
     * "情報を再編集することができませんでした。再試行します。" というダイアログ用の文字列。
     *
     */
    public static final String ERROR_DIALOG_MESSAGE_EDIT_AGAIN_FAILURE = "情報を再編集することができませんでした。再試行します。";
    
    /**
     * 情報を再度編集状態にするまでダイアログを表示して何度も試行する。
     *
     * @param database Database
     * @param successRunnable 編集成功時の処理
     * @param alertDialogCallback エラーメッセージを表示する前のダイアログに対する処理
     */
    public void tryEditAgainDataControllerWithDialog(D database, Runnable successRunnable, DialogCallback<Alert> alertDialogCallback) {
        AbstractDatabaseEditor<D, T> editor = this;
        try {
            this.editDataController(database);
            if (successRunnable != null) {
                successRunnable.run();
            }
        } catch (SQLException exception) {
            Alert alert = new Alert();
            alert.setTitle(AbstractDatabase.ERROR_DIALOG_TITLE);
            alert.setMessage(ExceptionHelper.createDetailMessage(ERROR_DIALOG_MESSAGE_EDIT_AGAIN_FAILURE, exception));
            alert.setCloseEvent(new CloseEventHandler<DialogResult>() {
                @Override
                public void handle(DialogResult resultValue) {
                    D database = editor.createDatabase();
                    editor.tryConnectDatabaseWithDialog(new Runnable() {
                        @Override
                        public void run() {
                            editor.tryEditAgainDataControllerWithDialog(database, successRunnable, alertDialogCallback);
                        }
                    });
                }
            });
            alert.showOnPane((Pane) this.getStage().getScene().getRoot());
            if (alertDialogCallback != null) {
                alertDialogCallback.call(alert);
            }
        } catch (DataNotFoundException exception) {
            exception.printStackTrace();
            this.close();
        }
    }}
