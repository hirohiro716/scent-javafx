package com.hirohiro716.javafx.data;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.concurrent.Callable;

import com.hirohiro716.LayoutSetting;
import com.hirohiro716.RudeArray;
import com.hirohiro716.database.AbstractBindTable;
import com.hirohiro716.database.AbstractDatabase;
import com.hirohiro716.database.WhereSet;
import com.hirohiro716.javafx.StageBuilder;
import com.hirohiro716.javafx.dialog.InterfaceDialog.CloseEventHandler;
import com.hirohiro716.javafx.dialog.DialogResult;
import com.hirohiro716.javafx.dialog.alert.AlertPane;
import com.hirohiro716.javafx.dialog.confirm.ConfirmPane;
import com.hirohiro716.javafx.dialog.wait.WaitPaneDialog;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * 情報をデータベースから検索するフォームの抽象クラス.
 * @author hiro
 * @param <T> 情報の検索処理を行うクラス
 */
public abstract class AbstractDatabaseSearcher<T extends AbstractBindTable> {

    /**
     * 検索結果を表示するTableViewを取得する.
     * @return TableView
     */
    protected abstract TableView<RudeArray> getTableView();

    /**
     * 検索を実行する.
     */
    protected abstract void search();

    /**
     * 詳細検索を実行する.
     */
    protected abstract void searchDetail();
    
    /**
     * 検索処理を行うコールバックメソッド. searchingWithWaitViewメソッドから自動的に呼び出される.
     * @param afterSQL WHERE句の後に付与するオプションSQL
     * @param whereSets 検索条件(複数指定するとOR検索になる)
     * @return 検索結果
     * @throws SQLException
     */
    protected abstract RudeArray[] searchExecute(String afterSQL, WhereSet... whereSets) throws SQLException;

     /**
      * 検索を待機画面を表示しながら実行する.
      * @param afterSQL WHERE句の後に付与するオプションSQL
      * @param whereSets 検索条件(複数指定するとOR検索になる)
      */
    protected void searchingWithWaitView(String afterSQL, WhereSet... whereSets) {
        AbstractDatabaseSearcher<T> searcher = AbstractDatabaseSearcher.this;
        Pane parentPane = (Pane) searcher.getStage().getScene().getRoot();
        searcher.getTableView().getItems().clear();
        WaitPaneDialog<RudeArray[]> dialog = new WaitPaneDialog<>(parentPane);
        dialog.setTitle("検索処理中");
        dialog.setMessage("ただいま検索中です。しばらくお待ちください。");
        dialog.setCallable(new Callable<RudeArray[]>() {
            @Override
            public RudeArray[] call() throws Exception {
                return searcher.searchExecute(afterSQL, whereSets);
            }
        });
        dialog.setCloseEvent(new CloseEventHandler<RudeArray[]>() {
            @Override
            public void handle(RudeArray[] resultValue) {
                if (resultValue != null) {
                    searcher.getTableView().getItems().addAll(resultValue);
                    searcher.getTableView().refresh();
                    searcher.afterSearchProcessing();
                } else {
                    AlertPane.show(AbstractDatabase.ERROR_DIALOG_TITLE, dialog.getException().getMessage(), parentPane);
                }
            }
        });
        dialog.show();
    }
    
    private EventHandler<ActionEvent> searchExecuteActionEventHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            AbstractDatabaseSearcher.this.search();
        }
    };

    /**
     * 検索を実行するActionEventHandlerを取得する.
     * @return ActionEventHandler
     */
    public EventHandler<ActionEvent> getSearchExecuteActionEventHandler() {
        return this.searchExecuteActionEventHandler;
    }

    private EventHandler<ActionEvent> searchDetailExecuteActionEventHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            AbstractDatabaseSearcher.this.searchDetail();
        }
    };

    /**
     * 詳細検索を実行するActionEventHandlerを取得する.
     * @return ActionEventHandler
     */
    public EventHandler<ActionEvent> getSearchDetailExecuteActionEventHandler() {
        return this.searchDetailExecuteActionEventHandler;
    }

    /**
     * 検索条件と検索結果をクリアする.
     */
    protected abstract void clear();

    private EventHandler<ActionEvent> clearActionEventHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            AbstractDatabaseSearcher.this.clear();
        }
    };

    /**
     * 検索をクリアするActionEventHandlerを取得する.
     * @return ActionEventHandler
     */
    public EventHandler<ActionEvent> getClearActionEventHandler() {
        return this.clearActionEventHandler;
    }

    /*
     * 選択モード
     */
    private boolean isSelectMode = false;

    /**
     * 選択モードになっている場合はTrueを返す.
     * @return isSelectMode
     */
    protected boolean isSelectMode() {
        return this.isSelectMode;
    }

    private SelectCallback selectCallback;

    /**
     * 選択モードで検索画面を表示する.
     * @param selectCallback 選択後の処理
     * @throws Exception
     */
    public void showSelectMode(SelectCallback selectCallback) throws Exception {
        this.isSelectMode = true;
        this.selectCallback = selectCallback;
        this.showAndWait();
    }

    /**
     * 選択モードで検索画面を表示する.
     * @param selectCallback 選択後の処理
     * @param owner 親Stage
     * @throws Exception
     */
    public void showSelectMode(Window owner, SelectCallback selectCallback) throws Exception {
        this.isSelectMode = true;
        this.selectCallback = selectCallback;
        this.showAndWait(owner);
    }

    /**
     * 検索結果内の行を選択するインターフェース.
     * @author hiro
     */
    public static interface SelectCallback {

        /**
         * 実際に検索結果内の行が選択された場合の処理.
         * @param selectedRow
         */
        public void select(RudeArray selectedRow);

    }

    /**
     * 選択行を指定する処理. この処理を行うと本画面も閉じる.
     */
    protected void selectRow() {
        if (this.isSelectMode == false) {
            return;
        }
        RudeArray row = this.getSelectedRow();
        if (row != null) {
            this.selectCallback.select(row);
            this.close();
        }
    }

    /**
     * 選択されている行を取得する. 未選択の場合はnullを返す.
     * @return 現在選択されている行データ
     */
    protected abstract RudeArray getSelectedRow();

    /*
     * 詳細検索で作成した条件を保持しておく
     */
    private WhereSet[] whereSetDetails;

    /**
     * 詳細検索で作成した条件を内部にセットする.
     * @param whereSets
     */
    protected void setWhereSetDetails(WhereSet[] whereSets) {
        this.whereSetDetails = whereSets;
    }

    /**
     * 前回詳細検索で作成した条件を取得する.
     * @return WhereSet配列
     */
    protected WhereSet[] getWhereSetDetails() {
        return this.whereSetDetails;
    }

    /**
     * 検索完了後の後処理を行う.
     */
    protected abstract void afterSearchProcessing();

    /**
     * 新しいデータを追加する.
     */
    protected abstract void add();

    private EventHandler<ActionEvent> addActionEventHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            AbstractDatabaseSearcher.this.add();
        }
    };

    /**
     * 新しいデータを追加するActionEventHandlerを取得する.
     * @return ActionEventHandler
     */
    public EventHandler<ActionEvent> getAddActionEventHandler() {
        return this.addActionEventHandler;
    }

    /**
     * 検索結果の選択されているデータを編集する.
     */
    protected abstract void edit();

    private EventHandler<ActionEvent> editActionEventHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            AbstractDatabaseSearcher.this.edit();
        }
    };

    /**
     * 選択されているデータを編集するActionEventHandlerを取得する.
     * @return ActionEventHandler
     */
    public EventHandler<ActionEvent> getEditActionEventHandler() {
        return this.editActionEventHandler;
    }

    private EventHandler<ActionEvent> selectActionEventHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            AbstractDatabaseSearcher.this.selectRow();
        }
    };

    /**
     * 選択されているデータを選択するActionEventHandlerを取得する.
     * @return ActionEventHandler
     */
    public EventHandler<ActionEvent> getSelectActionEventHandler() {
        return this.selectActionEventHandler;
    }

    /**
     * 検索結果の選択されているデータを削除する.
     */
    protected abstract void delete();

    private EventHandler<ActionEvent> deleteActionEventHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            Node node = (Node) event.getSource();
            ConfirmPane confirm = new ConfirmPane((Pane) node.getScene().getRoot());
            confirm.setTitle(AbstractEditor.CONFIRM_DIALOG_TITLE_DELETE);
            confirm.setMessage("選択中のデータを削除します。");
            confirm.setDefaultButton(DialogResult.CANCEL);
            confirm.setCloseEvent(new CloseEventHandler<DialogResult>() {
                @Override
                public void handle(DialogResult resultValue) {
                    if (resultValue == DialogResult.OK) {
                        AbstractDatabaseSearcher.this.delete();
                    }
                }
            });
            confirm.show();
        }
    };

    /**
     * 選択されているデータを削除するActionEventHandlerを取得する.
     * @return ActionEventHandler
     */
    public EventHandler<ActionEvent> getDeleteActionEventHandler() {
        return this.deleteActionEventHandler;
    }

    /**
     * Nodeに対してショートカットキーイベントをセットする.
     * @param nodes
     */
    protected void setShortcutKeyEventHandler(Node... nodes) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (Node node: nodes) {
                    node.setOnKeyReleased(AbstractDatabaseSearcher.this.shortcutKeyEventHandler);
                }
            }
        });
    }

    private EventHandler<KeyEvent> shortcutKeyEventHandler = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {
            AbstractDatabaseSearcher<?> searcher = AbstractDatabaseSearcher.this;
            switch (event.getCode()) {
            case ESCAPE:
                searcher.clear();
                event.consume();
                break;
            case F1:
                searcher.add();
                event.consume();
                break;
            case F2:
                searcher.edit();
                event.consume();
                break;
            case F3:
                searcher.selectRow();
                event.consume();
                break;
            case F5:
                searcher.search();
                event.consume();
                break;
            case F12:
                Node node = (Node) event.getSource();
                ConfirmPane confirm = new ConfirmPane((Pane) node.getScene().getRoot());
                confirm.setMessage("選択中のデータを削除します。");
                confirm.setDefaultButton(DialogResult.CANCEL);
                confirm.setCloseEvent(new CloseEventHandler<DialogResult>() {
                    @Override
                    public void handle(DialogResult resultValue) {
                        if (resultValue == DialogResult.OK) {
                            AbstractDatabaseSearcher.this.delete();
                        }
                    }
                });
                confirm.show();
                event.consume();
                break;
            default:
                break;
            }
        }
    };

    /**
     * 検索用フォーム表示前の準備処理. showまたはshowAndWaitメソッドを呼び出した際に自動実行される.
     * @throws Exception
     */
    protected abstract void beforeShowDoPreparation() throws Exception;

    /**
     * 初期表示されるデータをセットする.
     * @param rows
     */
    public void setDefaultRows(RudeArray[] rows) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (RudeArray row: rows) {
                    AbstractDatabaseSearcher<?> searcher = AbstractDatabaseSearcher.this;
                    searcher.getTableView().getItems().add(row);
                    searcher.getTableView().refresh();
                }
            }
        });
    }

    /**
     * 検索用フォームを表示する.
     * @throws Exception
     */
    public void show() throws Exception {
        this.show(null);
    }

    /**
     * 検索用フォームを表示する.
     * @param owner 親Stage
     * @throws Exception
     */
    public void show(Window owner) throws Exception {
        this.beforeShowDoPreparation();
        this.getStage().initOwner(owner);
        this.getStage().show();
    }

    /**
     * 検索用フォームを表示し閉じるまで待機する.
     * @throws Exception
     */
    public void showAndWait() throws Exception {
        this.showAndWait(null);
    }

    /**
     * 検索用フォームを表示し閉じるまで待機する.
     * @param owner 親Stage
     * @throws Exception
     */
    public void showAndWait(Window owner) throws Exception {
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
    }

    private T bindTableInstance;

    /**
     * データ処理を行うインスタンスを取得する.
     * @return データ処理を行うインスタンス
     */
    public T getBindTableInstance() {
        return this.bindTableInstance;
    }

    /**
     * データ処理を行うインスタンスをセットする.
     * @param bindTableInstance
     */
    public void setBindTableInstance(T bindTableInstance) {
        this.bindTableInstance = bindTableInstance;
    }

    /**
     * ウインドウが表示されている画面を取得する.
     * @return Screen
     */
    public Screen getDisplayedScreen() {
        return this.stageBuilder.getDisplayedScreen();
    }
    
    /**
     * 現在の画面サイズ・位置情報からLayoutSettingを作成する.
     * @return LayoutSetting
     */
    protected LayoutSetting createWindowLayoutSetting() {
        LayoutSetting layoutSetting = new LayoutSetting();
        Stage stage = this.getStage();
        layoutSetting.setAll(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
        return layoutSetting;
    }

    /**
     * 画面サイズ・位置情報にLayoutSettingを適用する.
     * @param layoutSetting
     */
    protected void applyWindowLayoutSetting(LayoutSetting layoutSetting) {
        this.applyWindowLayoutSetting(layoutSetting, this.getDisplayedScreen());
    }
    
    /**
     * 画面サイズ・位置情報にLayoutSettingを適用する.
     * @param layoutSetting
     * @param screen 表示対象画面
     */
    protected void applyWindowLayoutSetting(LayoutSetting layoutSetting, Screen screen) {
        Stage stage = this.getStage();
        if (screen.getVisualBounds().getWidth() <= layoutSetting.getWidth() && screen.getVisualBounds().getHeight() <= layoutSetting.getHeight()) {
            stage.setMaximized(true);
        } else {
            stage.setX(layoutSetting.getLayoutX());
            stage.setY(layoutSetting.getLayoutY());
            stage.setWidth(layoutSetting.getWidth());
            stage.setHeight(layoutSetting.getHeight());
        }
    }
    
    /**
     * Searcherを閉じる.
     */
    public void close() {
        try {
            this.bindTableInstance.getDatabase().close();
        } catch (Exception exception) {
            // nop
        }
        this.stageBuilder.close();
    }

}
