package com.hirohiro716.javafx.control.table.cell;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.Labeled;
import javafx.scene.control.TableCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * コントロールを常に表示するセル抽象クラス.
 * @author hiro
 *
 * @param <S> TableViewの型
 * @param <T> 値の型
 */
public abstract class AbstractLiveControlTableCell<S, T> extends TableCell<S, T> {

    /**
     * AbstractLiveControlTableCell
     */
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
        // フォーカスコントロール
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // セルの選択が無効ならフォーカスさせない
                if (cell.getTableView().getSelectionModel().isCellSelectionEnabled() == false) {
                    cell.getControl().setFocusTraversable(false);
                }
                // テーブルビューが編集できない場合はフォーカスさせない
                if (cell.getTableView().isEditable() == false) {
                    cell.getControl().setFocusTraversable(false);
                    if (cell.getControl() instanceof Labeled == false) {
                        cell.getControl().setDisable(true);
                    }
                }
            }
        });
    }

    /**
     * 内部のコントロールでTabかEnterが押された場合にセルを更新する処理とフォーカス移動を行うKeyEventを取得する.
     * @return KeyPressedEvent
     */
    protected EventHandler<KeyEvent> getKeyPressedEventAtControl() {
        return this.keyPressedEventAtControl;
    }

    private EventHandler<KeyEvent> keyPressedEventAtControl = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {
            AbstractLiveControlTableCell<S, T> cell = AbstractLiveControlTableCell.this;
            if (cell.getTableView().isEditable() == false) {
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
            }
        }
    };

    /**
     * 内部のコントロール直接クリックした際に行やセルを選択状態にするMouseEventを取得する.
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
     * 内部に配置するコントロールを生成するコールバック関数. スーパークラスのコンストラクタで自動的に実行される.
     */
    protected abstract void createControlCallback();

    /**
     * 内部のコントロールを取得する.
     * @return Control
     */
    public abstract Control getControl();

    /**
     * 内部のコントロールにフォーカスする.
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
     * 内部のコントロールからセルに対して値を入れる処理.
     */
    public abstract void commitEdit();

}
