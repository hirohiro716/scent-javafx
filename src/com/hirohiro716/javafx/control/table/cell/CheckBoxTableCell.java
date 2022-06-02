package com.hirohiro716.javafx.control.table.cell;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;

/**
 * TableViewのCheckBoxセル
 * @author hiro
 * @param <S> TableViewの型
 */
public abstract class CheckBoxTableCell<S> extends AbstractLiveControlTableCell<S, Boolean> {

    private CheckBox checkBox;

    /**
     * コンストラクタ。
     */
    public CheckBoxTableCell() {
        this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        this.setAlignment(Pos.CENTER);
    }

    @Override
    public void updateLiveItem(Boolean item, boolean isEmpty) {
        this.setText(null);
        if (isEmpty == false) {
            setGraphic(this.checkBox);
            this.checkBox.setSelected(item != null && item);
        } else {
            this.setGraphic(null);
        }
    }

    @Override
    protected void createControlCallback() {
        CheckBoxTableCell<S> cell = this;
        this.checkBox = new CheckBox();
        this.checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                cell.commitEdit();
            }
        });
    }

    /**
     * 内部のCheckBoxを取得する。
     *
     * @return CheckBox
     */
    public CheckBox getCheckBox() {
        return this.checkBox;
    }

    @Override
    public Control getControl() {
        return this.checkBox;
    }

    @Override
    public void processOfSetEditable(boolean isEditable) {
        this.checkBox.setDisable(isEditable == false);
    }
}
