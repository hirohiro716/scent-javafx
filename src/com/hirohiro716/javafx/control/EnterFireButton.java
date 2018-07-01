package com.hirohiro716.javafx.control;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * EnterキーでActionイベントが発生するボタン.
 * @author hiro
 */
public class EnterFireButton extends ImeOffButton {

    /**
     * コンストラクタ
     */
    public EnterFireButton() {
        this("", null);
    }

    /**
     * コンストラクタ
     * @param text
     */
    public EnterFireButton(String text) {
        this(text, null);
    }

    /**
     * コンストラクタ
     * @param text
     * @param graphic
     */
    public EnterFireButton(String text, Node graphic) {
        super(text, graphic);
        this.addEventFilter(KeyEvent.KEY_PRESSED, this.keyPressedEventHandler);
        this.addEventFilter(KeyEvent.KEY_RELEASED, this.keyReleasedEventHandler);
    }

    private boolean isPressed = false;

    private EventHandler<KeyEvent> keyPressedEventHandler = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {
            if (event.getCode() == KeyCode.ENTER) {
                EnterFireButton.this.isPressed = true;
                event.consume();
            }
        }
    };

    private EventHandler<KeyEvent> keyReleasedEventHandler = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {
            if (event.getCode() == KeyCode.ENTER && EnterFireButton.this.isPressed) {
                EnterFireButton.this.fire();
                event.consume();
            }
            EnterFireButton.this.isPressed = false;
        }
    };

}
