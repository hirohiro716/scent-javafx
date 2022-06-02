package com.hirohiro716.javafx.control.table.cell;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;

/**
 * TableViewのボタンセル
 * @author hiro
 * @param <S> TableViewの型
 */
public class ButtonTableCell<S> extends AbstractLiveControlTableCell<S, Void> {

    private Button button;

    /**
     * コンストラクタでボタン押下時のイベントを指定する。
     *
     * @param buttonText
     * @param actionEvent
     */
    public ButtonTableCell(String buttonText, EventHandler<ActionEvent> actionEvent) {
        this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        this.setAlignment(Pos.CENTER);
        this.getButton().setText(buttonText);
        this.getButton().setOnAction(actionEvent);
    }

    @Override
    public void updateLiveItem(Void item, boolean isEmpty) {
        if (isEmpty) {
            this.setGraphic(null);
        } else {
            setGraphic(this.button);
        }
    }

    @Override
    protected void createControlCallback() {
        this.button = new Button();
    }

    /**
     * 内部のボタンを取得する。
     *
     * @return Button
     */
    public Button getButton() {
        return this.button;
    }

    @Override
    public Control getControl() {
        return this.button;
    }

    @Override
    public void processOfSetEditable(boolean isEditable) {
    }

    @Override
    public void commitEdit() {
    }
}
