package com.hirohiro716.javafx.web;

import java.util.ArrayList;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.scene.web.WebView;

/**
 * WebEngineの処理を順番に行うクラス.
 * @author hiro
 */
public class WebEngineFlow {
    
    /**
     * コンストラクタ.
     * @param webView
     */
    public WebEngineFlow(WebView webView) {
        this.controller = new WebEngineController(webView.getEngine());
        this.webView = webView;
        
    }
    
    private WebEngineController controller;
    
    /**
     * WebEngineのコントロールを補助するWebEngineControllerインスタンスを取得する.
     * @return Controller
     */
    public WebEngineController getWebEngineController() {
        return this.controller;
    }
    
    private WebView webView;
    
    /**
     * WebViewインスタンスを取得する.
     * @return WebView
     */
    public WebView getWebView() {
        return this.webView;
    }

    private int sleepMillisecond = 200;
    
    /**
     * 処理後の待機時間をセットする. 初期値は200ミリ秒.
     * @param millisecond
     */
    public void setSleepMillisecond(int millisecond) {
        this.sleepMillisecond = millisecond;
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
    
    /**
     * 指定のElementが読み込まれるまで待機する.
     * @param id
     */
    public void addTaskWaitForLoadElementById(String id) {
        WebEngineFlow flow = this;
        this.tasks.add(new Task() {
            @Override
            public void execute(WebEngineController controller) {
                controller.clearSelectedElements();
                controller.selectElementById(id);
                if (controller.isSelectedElement() == false && flow.webView.getScene().getWindow().isShowing()) {
                    flow.currentIndex--;
                }
            }
        });
    }
    
    /**
     * 指定のElementが読み込まれるまで待機する.
     * @param tagName
     */
    public void addTaskWaitForLoadElementByTagName(String tagName) {
        WebEngineFlow flow = this;
        this.tasks.add(new Task() {
            @Override
            public void execute(WebEngineController controller) {
                controller.clearSelectedElements();
                controller.selectElementsByTagName(tagName);
                if (controller.isSelectedElement() == false && flow.webView.getScene().getWindow().isShowing()) {
                    flow.currentIndex--;
                }
            }
        });
    }
    
    /**
     * 指定のElementが読み込まれるまで待機する.
     * @param tagName
     * @param textCompareRegex テキストと比較する正規表現
     */
    public void addTaskWaitForLoadElementByTagName(String tagName, String textCompareRegex) {
        WebEngineFlow flow = this;
        this.tasks.add(new Task() {
            @Override
            public void execute(WebEngineController controller) {
                controller.clearSelectedElements();
                controller.selectElementsByTagName(tagName, textCompareRegex);
                if (controller.isSelectedElement() == false && flow.webView.getScene().getWindow().isShowing()) {
                    flow.currentIndex--;
                }
            }
        });
    }
    
    /**
     * 指定のElementが読み込まれるまで待機する.
     * @param attributeName 属性名
     * @param valueCompareRegex 属性値と比較する正規表現
     */
    public void addTaskWaitForLoadElementByAttribute(String attributeName, String valueCompareRegex) {
        WebEngineFlow flow = this;
        this.tasks.add(new Task() {
            @Override
            public void execute(WebEngineController controller) {
                controller.clearSelectedElements();
                controller.selectElementsByAttribute(attributeName, valueCompareRegex);
                if (controller.isSelectedElement() == false && flow.webView.getScene().getWindow().isShowing()) {
                    flow.currentIndex--;
                }
            }
        });
    }
    
    private int currentIndex = -1;
    
    /**
     * 処理を順番に実行する.
     */
    public void execute() {
        WebEngineFlow flow = this;
        this.webView.getEngine().getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
                if (newValue == State.SUCCEEDED) {
                    flow.executeTaskToUntilReachTheAsyncTask();
                }
            }
        });
        executeTaskToUntilReachTheAsyncTask();
    }
    
    /**
     * 非同期タスクに到達するまでタスクを実行し続ける.
     */
    private void executeTaskToUntilReachTheAsyncTask() {
        WebEngineFlow flow = this;
        try {
            Thread.sleep(flow.sleepMillisecond);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
        if (this.webView.getScene().getWindow().isShowing() == false) {
            return;
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                flow.currentIndex++;
                if (flow.currentIndex < flow.tasks.size()) {
                    try {
                        flow.tasks.get(flow.currentIndex).execute(flow.controller);
                    } catch (Exception exception) {
                        flow.handleException(exception);
                        flow.currentIndex = flow.tasks.size();
                        return;
                    }
                    flow.controller.clearSelectedElements();
                    State state = flow.webView.getEngine().getLoadWorker().getState();
                    switch (state) {
                    case SUCCEEDED:
                    case READY:
                        flow.executeTaskToUntilReachTheAsyncTask();
                        break;
                    case RUNNING:
                    case SCHEDULED:
                    case CANCELLED:
                    case FAILED:
                        break;
                    }
                }
            }
        });
    }

    /**
     * 例外を処理する.
     * @param exception 発生例外
     */
    protected void handleException(Exception exception) {
        exception.printStackTrace();
    }
    
    /**
     * 実行するタスクを１つ前に戻す.
     */
    public void turnBack() {
        this.currentIndex--;
    }
    
    /**
     * 実行するタスクを指定数前に戻す.
     * @param numberOfTurnBack 戻す回数
     */
    public void turnBack(int numberOfTurnBack) {
        this.currentIndex -= numberOfTurnBack;
    }

    /**
     * 実行するタスクを１つスキップする.
     */
    public void skip() {
        this.currentIndex++;
    }
    
    /**
     * 実行するタスクを指定数スキップする.
     * @param numberOfSkip スキップする回数
     */
    public void skip(int numberOfSkip) {
        this.currentIndex += numberOfSkip;
    }

    /**
     * WebEngineFlowの処理タスククラス.
     * @author hiro
     */
    public abstract static class Task {
        
        /**
         * 処理タスクを実行する.
         * @param controller WebEngineのコントロールを補助するWebEngineControllerインスタンス
         * @throws Exception 
         */
        public abstract void execute(WebEngineController controller) throws Exception;
        
    }
    
}
