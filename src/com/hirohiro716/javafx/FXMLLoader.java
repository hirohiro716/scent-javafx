package com.hirohiro716.javafx;

import java.io.IOException;
import java.net.URL;

import javafx.scene.layout.Pane;

/**
 * FXMLからPaneとControllerを生成する.
 * @author hiro
 */
public class FXMLLoader {

    /**
     * コンストラクタ.
     * @param fxmlURL FXMLのURL
     * @throws IOException
     */
    public FXMLLoader(URL fxmlURL) throws IOException {
        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(fxmlURL);
        try {
            this.paneRoot = loader.load();
        } catch (IOException exception) {
            this.paneRoot = null;
            this.controller = null;
            throw exception;
        }
        this.controller = loader.getController();
    }

    /**
     * コンストラクタ.(予めコントローラーを指定)
     * @param fxmlURL FXMLのURL
     * @param controller コントローラー
     * @throws IOException
     */
    public FXMLLoader(URL fxmlURL, Object controller) throws IOException {
        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(fxmlURL);
        try {
            this.controller = controller;
            loader.setController(controller);
            this.paneRoot = loader.load();
        } catch (IOException exception) {
            this.paneRoot = null;
            this.controller = null;
            throw exception;
        }
    }

    private Pane paneRoot;

    /**
     * 生成されたPaneを取得する.
     * @return Paneインスタンス
     */
    public Pane getPaneRoot() {
        return this.paneRoot;
    }

    private Object controller;

    /**
     * 生成されたControllerを取得する.
     * @return Controllerインスタンス
     */
    public Object getController() {
        return this.controller;
    }

    /**
     * FXMLをロードしコントローラーインスタンスと関連付ける.
     * @param fxmlURL FXMLのURL
     * @param controller コントローラー
     * @return 生成されたPaneインスタンス
     * @throws IOException
     */
    public static Pane load(URL fxmlURL, Object controller) throws IOException {
        FXMLLoader instance = new FXMLLoader(fxmlURL, controller);
        return instance.getPaneRoot();
    }

}
