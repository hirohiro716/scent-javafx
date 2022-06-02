package com.hirohiro716.javafx.control.table.cell;

import com.hirohiro716.javafx.control.IMEOffCheckBox;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;

/**
 * TableViewのIMEOffCheckBoxセル
 * @author hiro
 * @param <S> TableViewの型
 */
public abstract class IMEOffCheckBoxTableCell<S> extends AbstractLiveControlTableCell<S, Boolean> {

    private IMEOffCheckBox checkBox;

    /**
     * コンストラクタ。
     *
     */
    public IMEOffCheckBoxTableCell() {
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
        IMEOffCheckBoxTableCell<S> cell = this;
        this.checkBox = new IMEOffCheckBox();
        this.checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                cell.commitEdit();
            }
        });
    }

    /**
     * 内部のIMEOffCheckBoxを取得する。
     *
     * @return IMEOffCheckBox
     */
    public IMEOffCheckBox getIMEOffCheckBox() {
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
