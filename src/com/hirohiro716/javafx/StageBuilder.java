package com.hirohiro716.javafx;

import java.io.IOException;
import java.net.URL;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

/**
 * JavaFXのStage構成を補助するクラス。
 *
 * @author hiro
 */
public class StageBuilder {

    /**
     * コンストラクタで新規Stageを構築する。
     */
    public StageBuilder() {
        this.stage = new Stage();
    }

    /**
     * コンストラクタでFXMLを指定してStageレイアウトを構成する。
     *
     * @param fxmlURL
     * @throws IOException
     */
    public StageBuilder(URL fxmlURL) throws IOException {
        this.stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL);
        this.paneRoot = fxmlLoader.getPaneRoot();
        this.scene = new Scene(this.paneRoot);
        this.scene.setFill(null);
        this.stage.setScene(this.scene);
        this.controller = fxmlLoader.getController();
    }

    /**
     * コンストラクタでFXMLを指定してStageレイアウトを構成する。
     *
     * @param stage
     * @param fxmlURL
     * @throws IOException
     */
    public StageBuilder(Stage stage, URL fxmlURL) throws IOException {
        this.stage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL);
        this.paneRoot = fxmlLoader.getPaneRoot();
        this.scene = new Scene(this.paneRoot);
        this.scene.setFill(null);
        this.stage.setScene(this.scene);
        this.controller = fxmlLoader.getController();
    }

    /**
     * コンストラクタでFXMLとControllerを指定してStageレイアウトを構成する。
     *
     * @param fxmlURL
     * @param controller
     * @throws IOException
     */
    public StageBuilder(URL fxmlURL, Object controller) throws IOException {
        this.stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL, controller);
        this.paneRoot = fxmlLoader.getPaneRoot();
        this.controller = fxmlLoader.getController();
        this.scene = new Scene(this.paneRoot);
        this.scene.setFill(null);
        this.stage.setScene(this.scene);
    }

    /**
     * コンストラクタでFXMLとControllerを指定してStageレイアウトを構成する。
     *
     * @param stage
     * @param fxmlURL
     * @param controller
     * @throws IOException
     */
    public StageBuilder(Stage stage, URL fxmlURL, Object controller) throws IOException {
        this.stage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL, controller);
        this.paneRoot = fxmlLoader.getPaneRoot();
        this.controller = fxmlLoader.getController();
        this.scene = new Scene(this.paneRoot);
        this.scene.setFill(null);
        this.stage.setScene(this.scene);
    }

    /**
     * コンストラクタでFXMLLoaderインスタンスからStageレイアウトを構成する。
     *
     * @param fxmlLoader
     * @throws IOException
     */
    public StageBuilder(FXMLLoader fxmlLoader) throws IOException {
        this.stage = new Stage();
        this.paneRoot = fxmlLoader.getPaneRoot();
        this.controller = fxmlLoader.getController();
        this.scene = new Scene(this.paneRoot);
        this.scene.setFill(null);
        this.stage.setScene(this.scene);
    }

    /**
     * コンストラクタでFXMLLoaderインスタンスからStageレイアウトを構成する。
     *
     * @param stage
     * @param fxmlLoader
     * @throws IOException
     */
    public StageBuilder(Stage stage, FXMLLoader fxmlLoader) throws IOException {
        this.stage = stage;
        this.paneRoot = fxmlLoader.getPaneRoot();
        this.controller = fxmlLoader.getController();
        this.scene = new Scene(this.paneRoot);
        this.scene.setFill(null);
        this.stage.setScene(this.scene);
    }

    private Stage stage;

    /**
     * Stageを取得する。
     *
     * @return Stage
     */
    public Stage getStage() {
        return this.stage;
    }

    private Scene scene;

    /**
     * Sceneを取得する。
     *
     * @return Scene
     */
    public Scene getScene() {
        return this.scene;
    }

    Pane paneRoot;

    /**
     * PaneインスタンスをStageに描画する。
     *
     * @param pane
     */
    public void setPane(Pane pane) {
        this.scene = new Scene(pane);
        this.scene.setFill(null);
        this.stage.setScene(this.scene);
        this.paneRoot = pane;
    }

    /**
     * Parentインスタンスを取得する。
     *
     * @return parent
     */
    public Pane getPaneRoot() {
        return this.paneRoot;
    }

    private Object controller;

    /**
     * FXMLを指定してStageレイアウトを構成した後にControllerインスタンスを取得する。
     *
     * @return Controllerインスタンス
     */
    public Object getController() {
        return this.controller;
    }

    /**
     * Stageを表示する。
     */
    public void show() {
        this.stage.show();
    }

    /**
     * Stageを表示する。
     */
    public void showAndWait() {
        this.stage.showAndWait();
    }

    /**
     * Stageを表示する。
     *
     * @param owner 親
     */
    public void show(Window owner) {
        this.stage.initOwner(owner);
        this.stage.show();
    }

    /**
     * Stageを表示する。
     *
     * @param owner 親
     */
    public void showAndWait(Window owner) {
        this.stage.initOwner(owner);
        this.stage.showAndWait();
    }

    /**
     * Stageを終了する。
     */
    public void close() {
        this.stage.close();
    }

    // 閉じられないようにするEventHander
    private EventHandler<WindowEvent> nonCloseEventHandler = new EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent event) {
            event.consume();
        }
    };

    /**
     * Stageを閉じる事ができるかどうかをセットする。
     *
     * @param isCloseable
     */
    public void setCloseable(boolean isCloseable) {
        if (isCloseable == false) {
            this.stage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this.nonCloseEventHandler);
        } else {
            this.stage.removeEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this.nonCloseEventHandler);
        }
    }

    /**
     * Stageが表示されている画面を取得する。
     *
     * @return Screen
     */
    public Screen getDisplayedScreen() {
        Stage stage = this.stage;
        Screen screen = Screen.getPrimary();
        for (Screen loopScreen: Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight())) {
            screen = loopScreen;
        }
        return screen;
    }}
