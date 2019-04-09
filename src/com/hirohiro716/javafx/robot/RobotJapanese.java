package com.hirohiro716.javafx.robot;

import java.awt.AWTException;

import com.hirohiro716.robot.InterfaceTypingRobotJapanese;

/**
 * JavaFXのRobotクラスに日本語用の機能を足したクラス. // TODO にする予定。JavaFXのRobotクラスが使えるようになったら変更する。
 * @author hiro
 */
public class RobotJapanese implements InterfaceTypingRobotJapanese<Integer> {
    
    /**
     * コンストラクタ.
     */
    public RobotJapanese() {
        try {
            this.awtRobot = new com.hirohiro716.awt.RobotJapanese();
        } catch (AWTException exception) {
        }
    }
    
    private com.hirohiro716.awt.RobotJapanese awtRobot;

    @Override
    public void keyType(Integer... keyCodes) {
        if (this.awtRobot == null) {
            return;
        }
        this.awtRobot.keyType(keyCodes);
    }
    
    @Override
    public void changeImeOff() {
        this.awtRobot.changeImeOff();
    }

    @Override
    public void changeImeHiragana() {
        this.awtRobot.changeImeHiragana();
    }

    @Override
    public void changeImeKatakanaWide() {
        this.awtRobot.changeImeKatakanaWide();
    }

    @Override
    public void changeImeKatakanaNarrow() {
        this.awtRobot.changeImeKatakanaNarrow();
    }
    
}
