package com.hirohiro716.javafx.control.table.cell;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * コントロールを常に表示するセル抽象クラス。
 *
 * @author hiro
 *
 * @param <S> TableViewの型
 * @param <T> 値の型
 */
public abstract class AbstractLiveControlTableCell<S, T> extends TableCell<S, T> {

    /**
     * AbstractLiveControlTableCell
     */
    @SuppressWarnings("rawtypes")
    public AbstractLiveControlTableCell() {
        AbstractLiveControlTableCell<S, T> cell = AbstractLiveControlTableCell.this;
        cell.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        cell.createControlCallback();
        // KeyPressedイベントは内部のコントロールで処理するので何もさせない
        this.addEventHandler(KeyEvent.KEY_PRESSED,  new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.isAltDown() || event.isControlDown() || event.isMetaDown() || event.isShiftDown() || event.isShortcutDown()) {
                    return;
                }
                event.consume();
            }
        });
        // 内部のコントロールに直接フォーカスが当たった場合にセルをアクティブにする
        cell.getControl().focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    cell.getTableView().getSelectionModel().select(cell.getIndex(), cell.getTableColumn());
                    if (cell.isEditable()) {
                        cell.startEdit();
                    }
                } else {
                    cell.commitEdit();
                }
            }
        });
        // EnterキーとTabキーのフォーカス移動制御
        cell.getControl().addEventHandler(KeyEvent.KEY_PRESSED, this.keyPressedEventAtControl);
        // セルにフォーカスが当たった場合に内部のコントロールに移す
        cell.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                   cell.focusControl();
                }
            }
        });
        // コントロールを直接クリックすると行やセルが選択されないので実装
        cell.getControl().setOnMousePressed(this.mousePressedEventAtControl);
        // 編集可能かどうかによってコントロールも変更させる
        ChangeListener<Boolean> editableChangeListener = new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                cell.processOfSetEditable(cell.isEditableAll());
            }
        };
        cell.editableProperty().addListener(editableChangeListener);
        cell.tableViewProperty().addListener(new ChangeListener<TableView<S>>() {
            @Override
            public void changed(ObservableValue<? extends TableView<S>> observable, TableView<S> oldValue, TableView<S> newValue) {
                if (newValue != null) {
                    newValue.editableProperty().addListener(editableChangeListener);
                }
            }
        });
        cell.tableRowProperty().addListener(new ChangeListener<TableRow>() {
            @Override
            public void changed(ObservableValue<? extends TableRow> observable, TableRow oldValue, TableRow newValue) {
                if (newValue != null) {
                    newValue.editableProperty().addListener(editableChangeListener);
                }
            }
        });
        // フォーカスコントロール
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // セルの選択が無効ならフォーカスさせない
                if (cell.getTableView().getSelectionModel().isCellSelectionEnabled() == false) {
                    cell.getControl().setFocusTraversable(false);
                }
                // テーブルビューが編集できない場合はフォーカスさせない
                if (cell.getTableView().isEditable() && cell.getTableRow().isEditable() && cell.isEditable()) {
                    cell.getControl().setFocusTraversable(false);
                }
            }
        });
    }
    
    /**
     * TableViewとTableRowとTableCellがすべて編集可能かどうかを取得する。
     *
     * @return 結果
     */
    public boolean isEditableAll() {
        boolean result = true;
        if (this.getTableView() != null && this.getTableView().isEditable() == false) {
            result = false;
        }
        if (this.getTableRow() != null && this.getTableRow().isEditable() == false) {
            result = false;
        }
        if (this.isEditable() == false) {
            result = false;
        }
        return result;
    }
    
    /**
     * セルが編集可能かどうかの設定値が変更された際の処理。
     *
     * @param isEditable 
     */
    public abstract void processOfSetEditable(boolean isEditable);

    /**
     * 内部のコントロールでTabかEnterが押された場合にセルを更新する処理とフォーカス移動を行うKeyEventを取得する。
     *
     * @return KeyPressedEvent
     */
    protected EventHandler<KeyEvent> getKeyPressedEventAtControl() {
        return this.keyPressedEventAtControl;
    }

    private EventHandler<KeyEvent> keyPressedEventAtControl = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {
            AbstractLiveControlTableCell<S, T> cell = AbstractLiveControlTableCell.this;
            if (cell.getTableView().isEditable() == false || cell.getTableRow().isEditable() == false) {
                return;
            }
            switch (event.getCode()) {
            case TAB:
            case ENTER:
                event.consume();
                if (cell.isEditable()) {
                    cell.commitEdit();
                }
                if (event.isShiftDown() == false) {
                    cell.getTableView().getFocusModel().focusRightCell();
                } else {
                    cell.getTableView().getFocusModel().focusLeftCell();
                }
                break;
            default:
                break;
            }
        }
    };

    /**
     * 内部のコントロール直接クリックした際に行やセルを選択状態にするMouseEventを取得する。
     *
     * @return MousePressedEvent
     */
    protected EventHandler<MouseEvent> getMousePressedEventAtControl() {
        return this.mousePressedEventAtControl;
    }

    private EventHandler<MouseEvent> mousePressedEventAtControl = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if (event.getButton() == MouseButton.PRIMARY) {
                AbstractLiveControlTableCell<S, T> cell = AbstractLiveControlTableCell.this;
                if (cell.getTableView().getSelectionModel().isCellSelectionEnabled() == false) {
                    cell.getTableView().getSelectionModel().select(cell.getIndex());
                    cell.getControl().requestFocus();
                } else {
                    cell.getTableView().getSelectionModel().select(cell.getIndex(), cell.getTableColumn());
                    cell.getControl().requestFocus();
                }
            }
        }
    };

    /**
     * 内部に配置するコントロールを生成するコールバック関数。スーパークラスのコンストラクタで自動的に実行される。
     */
    protected abstract void createControlCallback();

    /**
     * 内部のコントロールを取得する。
     *
     * @return Control
     */
    public abstract Control getControl();

    /**
     * 内部のコントロールにフォーカスする。
     */
    protected void focusControl() {
        if (this.getTableView().getSelectionModel().isCellSelectionEnabled() == false) {
            return;
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                AbstractLiveControlTableCell<S, T> cell = AbstractLiveControlTableCell.this;
                if (cell.isFocused() && cell.getControl().isFocused() == false) {
                    cell.getControl().requestFocus();
                }
            }
        });
    }

    @Override
    protected final void updateItem(T item, boolean isEmpty) {
        super.updateItem(item, isEmpty);
        this.updateLiveItem(item, isEmpty);
        this.processOfSetEditable(this.isEditableAll());
    }
    
    /**
     * 内部にコントロールを表示する。
     *
     * @param item
     * @param isEmpty
     */
    protected abstract void updateLiveItem(T item, boolean isEmpty);
    
    @Override
    public void updateSelected(boolean selected ) {
        super.updateSelected(selected);
        if (selected) {
            this.focusControl();
        }
    }

    @Override
    public void startEdit() {
        super.startEdit();
        this.getTableView().edit(this.getIndex(), this.getTableColumn());
    }

    /**
     * 内部のコントロールからセルに対して値を入れる処理。
     */
    public abstract void commitEdit();

}
