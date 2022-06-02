package com.hirohiro716.javafx.control;

import com.hirohiro716.javafx.IMEHelper;
import com.hirohiro716.robot.InterfaceTypingRobotJapanese.IMEMode;

import javafx.application.Platform;
import javafx.scene.control.CheckBox;

/**
 * フォーカスした際にIMEをOFFにするCheckBox。
 *
 * @author hiro
 */
public class IMEOffCheckBox extends CheckBox {

    /**
     * コンストラクタ。
     *
     */
    public IMEOffCheckBox() {
        this("");
    }

    /**
     * コンストラクタ。
     *
     * @param text ラベルテキスト
     */
    public IMEOffCheckBox(String text) {
        super(text);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                IMEHelper.apply(IMEOffCheckBox.this, IMEMode.OFF);
            }
        });
    }
}
