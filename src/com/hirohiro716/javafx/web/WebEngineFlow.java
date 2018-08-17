package com.hirohiro716.javafx.web;

import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.scene.web.WebEngine;

/**
 * WebEngineの処理を順番に行うクラス.
 * @author hiro
 */
public class WebEngineFlow {
    
    /**
     * コンストラクタ.
     * @param engine
     */
    public WebEngineFlow(WebEngine engine) {
        this.controller = new WebEngineController(engine);
        this.engine = engine;
    }
    
    private WebEngineController controller;
    
    /**
     * WebEngineのコントロールを補助するWebEngineControllerインスタンスを取得する.
     * @return Controller
     */
    public WebEngineController getWebEngineController() {
        return this.controller;
    }
    
    private WebEngine engine;
    
    /**
     * WebEngineインスタンスを取得する.
     * @return WebEngine
     */
    public WebEngine getWebEngine() {
        return this.engine;
    }
    
    private ArrayList<Task> tasks = new ArrayList<>();
    
    /**
     * 処理を追加する.
     * @param task
     */
    public void addTask(Task task) {
        this.tasks.add(task);
    }
    
    /**
     * URLを読み込む処理を追加する.
     * @param url
     */
    public void addTaskLoadURL(String url) {
        this.tasks.add(new Task() {
            @Override
            public void execute(WebEngineController controller) {
                controller.getWebEngine().load(url);
            }
        });
    }
    
    private int sleepMillisecond = 200;
    
    /**
     * 処理後の待機時間をセットする. 初期値は200ミリ秒.
     * @param millisecond
     */
    public void setSleepMillisecond(int millisecond) {
        this.sleepMillisecond = millisecond;
    }
    
    private int currentIndex = -1;
    
    /**
     * 処理を順番に実行する.
     */
    public void execute() {
        executeTaskToUntilReachTheAsyncTask();
        WebEngineFlow flow = this;
        this.engine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
                if (newValue == State.SUCCEEDED) {
                    flow.executeTaskToUntilReachTheAsyncTask();
                }
            }
        });
    }
    
    /**
     * 非同期タスクに到達するまでタスクを実行し続ける.
     */
    private void executeTaskToUntilReachTheAsyncTask() {
        do {
            try {
                Thread.sleep(this.sleepMillisecond);
            } catch (InterruptedException exception) {
            }
            this.currentIndex++;
            if (this.currentIndex < this.tasks.size()) {
                this.tasks.get(this.currentIndex).execute(this.controller);
            } else {
                break;
            }
        } while (this.engine.getLoadWorker().getState() == State.SUCCEEDED);
    }
    
    /**
     * WebEngineFlowの処理タスククラス.
     * @author hiro
     */
    public abstract static class Task {
        
        /**
         * 処理タスクを実行する.
         * @param controller WebEngineのコントロールを補助するWebEngineControllerインスタンス
         */
        public abstract void execute(WebEngineController controller);
        
    }
    
}
