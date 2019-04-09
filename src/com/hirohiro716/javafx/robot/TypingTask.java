package com.hirohiro716.javafx.robot;

import java.awt.AWTException;
import java.util.Collection;

import com.hirohiro716.robot.InterfaceTypingTask;

/**
 * JavaFXで自動キー入力を行うクラス. // TODO にする予定
 * @author hiro
 */
public class TypingTask implements InterfaceTypingTask<Integer> {
    
    /**
     * コンストラクタ.
     */
    public TypingTask() {
        try {
            this.typingTask = new com.hirohiro716.awt.TypingTask();
        } catch (AWTException e) {
        }
    }
    
    private com.hirohiro716.awt.TypingTask typingTask;

    @Override
    public String makeTaskDefinitionString() {
        return this.typingTask.makeTaskDefinitionString();
    }

    @Override
    public void importFromTaskDefinitionString(String taskDefinitionString) {
        this.typingTask.importFromTaskDefinitionString(taskDefinitionString);
    }

    @Override
    public void addKeyTypeTask(KeyCode... keyCodes) {
        this.typingTask.addKeyTypeTask(keyCodes);
    }

    @Override
    public void addSleepTask(long milliseconds) {
        this.typingTask.addSleepTask(milliseconds);
    }

    @Override
    public Integer findTypingKeyCode(KeyCode keyCode) {
        return this.typingTask.findTypingKeyCode(keyCode);
    }

    @Override
    public KeyCode findTaskKeyCode(Integer keyCode) {
        return this.typingTask.findTaskKeyCode(keyCode);
    }

    @Override
    public void keyType(Collection<Integer> keyCodes) {
        this.typingTask.keyType(keyCodes);
    }

    @Override
    public void execute() {
        this.typingTask.execute();
    }

}
