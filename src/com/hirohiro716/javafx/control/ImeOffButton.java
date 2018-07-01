package com.hirohiro716.javafx.control;

import com.hirohiro716.awt.RobotJapanese.ImeMode;
import com.hirohiro716.javafx.IMEHelper;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;

/**
 * フォーカスした際にIMEをOFFにするボタン.
 * @author hiro
 *
 */
public class ImeOffButton extends Button {

    /**
     * コンストラクタ
     */
    public ImeOffButton() {
        this("", null);
    }

    /**
     * コンストラクタ
     * @param text
     */
    public ImeOffButton(String text) {
        this(text, null);
    }

    /**
     * コンストラクタ
     * @param text
     * @param graphic
     */
    public ImeOffButton(String text, Node graphic) {
        super(text, graphic);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                IMEHelper.apply(ImeOffButton.this, ImeMode.OFF);
            }
        });
    }

}
