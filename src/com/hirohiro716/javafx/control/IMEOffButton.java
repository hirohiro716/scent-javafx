package com.hirohiro716.javafx.control;

import com.hirohiro716.javafx.IMEHelper;
import com.hirohiro716.robot.InterfaceTypingRobotJapanese.IMEMode;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;

/**
 * フォーカスした際にIMEをOFFにするボタン。
 *
 * @author hiro
 *
 */
public class IMEOffButton extends Button {

    /**
     * コンストラクタ。
     */
    public IMEOffButton() {
        this("", null);
    }

    /**
     * コンストラクタ。
     *
     * @param text
     */
    public IMEOffButton(String text) {
        this(text, null);
    }

    /**
     * コンストラクタ。
     *
     * @param text
     * @param graphic
     */
    public IMEOffButton(String text, Node graphic) {
        super(text, graphic);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                IMEHelper.apply(IMEOffButton.this, IMEMode.OFF);
            }
        });
    }
}
