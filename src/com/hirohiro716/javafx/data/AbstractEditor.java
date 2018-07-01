package com.hirohiro716.javafx.data;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

import com.hirohiro716.javafx.StageBuilder;
import com.hirohiro716.javafx.dialog.AbstractDialog.CloseEventHandler;
import com.hirohiro716.javafx.dialog.alert.InstantAlert;
import com.hirohiro716.javafx.dialog.DialogResult;
import com.hirohiro716.javafx.dialog.confirm.ConfirmPane;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

/**
 * 情報を編集するエディターの抽象クラス.
 * @author hiro
 * @param <T> 編集する情報をコントロールするクラス
 */
public abstract class AbstractEditor<T> {

    /**
     * "編集画面の表示失敗" というダイアログタイトル用の文字列
     */
    public static final String ERROR_DIALOG_TITLE_OPEN_FAILURE = "編集画面の表示失敗";

    /**
     * "データに問題" というダイアログタイトル用の文字列
     */
    public static final String ERROR_DIALOG_TITLE_VALIDATION = "データに問題";

    /**
     * "保存失敗" というダイアログタイトル用の文字列
     */
    public static final String ERROR_DIALOG_TITLE_SAVE = "保存失敗";

    /**
     * "閉じる確認" というダイアログタイトル用の文字列
     */
    public static final String CONFIRM_DIALOG_TITLE_CLOSE = "閉じる確認";

    /**
     * "削除の確認" というダイアログタイトル用の文字列
     */
    public static final String CONFIRM_DIALOG_TITLE_DELETE = "削除の確認";
    
    /**
     * "このデータは削除されているため上書きできませんでした。\n" というダイアログ用の文字列
     */
    public static final String ERROR_DIALOG_MESSAGE_SAVE_NOTFOUND = "このデータは削除されているため上書きできませんでした。\n";

    /**
     * 編集用フォームを表示する.
     * @throws Exception
     */
    public void show() throws Exception {
        this.show(null);
    }
    
    /**
     * 編集用フォームを表示する.
     * @param owner 親Stage
     * @throws Exception
     */
    public void show(Window owner) throws Exception {
        this.editDataController();
        this.beforeShowDoPreparation();
        this.getStage().initOwner(owner);
        this.getStage().show();
    }
    
    /**
     * 編集用フォームを表示し閉じるまで待機する.
     * @throws Exception
     */
    public void showAndWait() throws Exception {
        this.showAndWait(null);
    }
    
    /**
     * 編集用フォームを表示し閉じるまで待機する.
     * @param owner 親Stage
     * @throws Exception
     */
    public void showAndWait(Window owner) throws Exception {
        this.editDataController();
        this.beforeShowDoPreparation();
        this.getStage().initOwner(owner);
        this.getStage().showAndWait();
    }
    
    private StageBuilder stageBuilder;
    
    /**
     * Stageを取得する.
     * @return Stage
     */
    public Stage getStage() {
        return this.stageBuilder.getStage();
    }
    
    /**
     * Stageを閉じる事ができるかどうかをセットする.
     * @param isCloseable
     */
    public void setCloseable(boolean isCloseable) {
        this.stageBuilder.setCloseable(isCloseable);
    }
    
    /**
     * フォームを操作不能にする.
     * @param isDisable
     */
    public void setDisable(boolean isDisable) {
        this.setCloseable(!isDisable);
        this.stageBuilder.getPaneRoot().setDisable(isDisable);
    }
    
    /**
     * FXMLをセットする.
     * @param fxmlURL
     * @throws IOException
     */
    public void setFxml(URL fxmlURL) throws IOException {
        this.stageBuilder = new StageBuilder(fxmlURL, this);
        this.getStage().addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, this.closeEvent);
    }

    /**
     * 編集するデータの読み込みや排他ロック処理.<br>
     * showまたはshowAndWaitメソッドを呼び出した際 beforeShowDoPreparationメソッドの前に自動実行される.
     * @throws SQLException
     */
    protected abstract void editDataController() throws Exception;
    
    private T dataController;
    
    /**
     * データ処理を行うインスタンスを取得する.
     * @return データ処理を行うインスタンス
     */
    public T getDataController() {
        return this.dataController;
    }
    
    /**
     * データ処理を行うインスタンスをセットする.
     * @param dataController
     */
    public void setDataController(T dataController) {
        this.dataController = dataController;
    }
    
    /**
     * 編集用フォーム表示前の準備処理.<br>
     * showまたはshowAndWaitメソッドを呼び出した際 dataControllerEditメソッドの後に自動実行される.
     * @throws Exception
     */
    protected abstract void beforeShowDoPreparation() throws Exception;
    
    /**
     * フォームからDataControllerに値を取り込む. このメソッドは自動では呼び出されない.
     */
    protected abstract void importDataFromForm();
    
    private boolean isCloseAgree = false;
    
    /**
     * 閉じる直前に行う処理. closeメソッドを呼び出した際に自動実行される.
     */
    protected abstract void beforeCloseDoPreparation() throws Exception;

    /**
     * Editorを閉じる.
     */
    public void close() {
        try {
            this.beforeCloseDoPreparation();
            this.isCloseAgree = true;
            this.stageBuilder.close();
        } catch (Exception exception) {
            InstantAlert.show(this.getStage().getScene().getRoot(), exception.getMessage(), Pos.CENTER, 1000);
        }
    }

    private boolean isCloseDialogShown = false;

    /**
     * 画面を閉じる際の確認
     */
    private EventHandler<WindowEvent> closeEvent = new EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent event) {
            event.consume();
            if (AbstractEditor.this.isCloseAgree == false && AbstractEditor.this.isCloseDialogShown == false) {
                AbstractEditor.this.isCloseDialogShown = true;
                ConfirmPane.show(CONFIRM_DIALOG_TITLE_CLOSE, "この画面を閉じます。", AbstractEditor.this.stageBuilder.getPaneRoot(), new CloseEventHandler<DialogResult>() {
                    @Override
                    public void handle(DialogResult resultValue) {
                        if (resultValue == DialogResult.OK) {
                            AbstractEditor.this.close();
                        }
                        AbstractEditor.this.isCloseDialogShown = false;
                    }
                });
            }
        }
    };

}
