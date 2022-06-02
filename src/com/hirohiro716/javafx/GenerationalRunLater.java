package com.hirohiro716.javafx;

import javafx.application.Platform;

/**
 * Platform.runLaterを繰り返して指定された実行世代のrunLaterになったら処理を行うクラス。
 * @author hiro
 */
public class GenerationalRunLater {
    
    private int runGeneration;
    
    /**
     * コンストラクタで実行世代を指定する。
     *
     * @param runGeneration 1を指定すると通常のPlatform.runLaterと同じ動作
     */
    public GenerationalRunLater(int runGeneration) {
        this.runGeneration = runGeneration;
    }
    
    private int runNumber = 1;
    
    /**
     * 実行世代までPlatform.runLaterを繰り返してから引数のRunnableを実行する。
     *
     * @param runnable 実行する処理
     */
    public void runLater(Runnable runnable) {
        if (this.runGeneration <= this.runNumber) {
            Platform.runLater(runnable);
            return;
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                GenerationalRunLater generationalRunLater = GenerationalRunLater.this;
                generationalRunLater.runNumber++;
                generationalRunLater.runLater(runnable);
            }
        });
    }
    
    /**
     * 実行世代までPlatform.runLaterを繰り返してから引数のRunnableを実行する。
     *
     * @param runGeneration 実行世代
     * @param runnable 実行する処理
     */
    public static void runLater(int runGeneration, Runnable runnable) {
        new GenerationalRunLater(runGeneration).runLater(runnable);
    }}
