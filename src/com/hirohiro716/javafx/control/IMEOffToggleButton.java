package com.hirohiro716.javafx.control;

import com.hirohiro716.javafx.IMEHelper;
import com.hirohiro716.robot.InterfaceTypingRobotJapanese.IMEMode;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;

/**
 * フォーカスした際にIMEをOFFにするボタン。
 *
 * @author hiro
 *
 */
public class IMEOffToggleButton extends ToggleButton {
    
    /**
     * コンストラクタ。
     *
     */
    public IMEOffToggleButton() {
        this("", null);
    }
    
    /**
     * コンストラクタ。
     *
     * @param text
     */
    public IMEOffToggleButton(String text) {
        this(text, null);
    }
    
    /**
     * コンストラクタ。
     *
     * @param text
     * @param graphic
     */
    public IMEOffToggleButton(String text, Node graphic) {
        super(text, graphic);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                IMEHelper.apply(IMEOffToggleButton.this, IMEMode.OFF);
            }
        });
    }}
