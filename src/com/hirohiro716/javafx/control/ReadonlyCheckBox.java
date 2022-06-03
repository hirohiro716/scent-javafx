package com.hirohiro716.javafx.control;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * 読み取り専用にできるCheckBox。
 *
 * @author hiro
 *
 */
public class ReadonlyCheckBox extends IMEOffCheckBox {

    /**
     * コンストラクタ。
     */
    public ReadonlyCheckBox() {
        this("");
    }

    /**
     * コンストラクタ。
     *
     * @param text ラベルテキスト
     */
    public ReadonlyCheckBox(String text) {
        super(text);
        this.addEventHandler(MouseEvent.MOUSE_CLICKED, this.readonlyMouseEvent);
        this.addEventHandler(KeyEvent.KEY_RELEASED, this.readonlyKeyEvent);
    }

    private EventHandler<MouseEvent> readonlyMouseEvent = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (ReadonlyCheckBox.this.isReadonly()) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            ReadonlyCheckBox.this.setSelected(ReadonlyCheckBox.this.fixValue);
                        }
                    });
                }
            }
        }
    };

    private EventHandler<KeyEvent> readonlyKeyEvent = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {
            if (ReadonlyCheckBox.this.isReadonly()) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        ReadonlyCheckBox.this.setSelected(ReadonlyCheckBox.this.fixValue);
                    }
                });
            }
        }
    };

    private boolean fixValue;

    /**
     * 読み取り専用プロパティ
     */
    public final BooleanProperty readonly = new SimpleBooleanProperty();

    /**
     * 読み取り専用プロパティを取得する。
     *
     * @return BooleanProperty
     */
    public BooleanProperty readonlyProperty() {
        return this.readonly;
    }

    /**
     * 読み取り専用プロパティに値をセットする。
     *
     * @param isReadonly
     */
    public void setReadonly(boolean isReadonly) {
        this.readonly.set(isReadonly);
        this.fixValue = this.isSelected();
    }

    /**
     * 読み取り専用かを取得する。
     *
     * @return 読み取り専用かどうか
     */
    public boolean getReadonly() {
        return this.readonly.get();
    }

    /**
     * 読み取り専用かを取得する。
     *
     * @return 読み取り専用かどうか
     */
    public boolean isReadonly() {
        return this.readonly.getValue();
    }
}
