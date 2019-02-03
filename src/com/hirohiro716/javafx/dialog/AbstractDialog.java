package com.hirohiro716.javafx.dialog;

import com.hirohiro716.javafx.CSSHelper;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 * ダイアログを表示するための抽象クラス.
 * @author hiro
 * @param <T> ダイアログのResultタイプ
 */
public abstract class AbstractDialog<T> implements InterfaceDialog<T> {

    private Stage parentStage;

    private Stage stage;

    /**
     * コンストラクタで指定したダイアログ表示対象Paneを取得する.
     * @return Pane
     */
    public Stage getStage() {
        return this.stage;
    }

    private StackPane dialogPane = new StackPane();

    @Override
    public StackPane getStackPane() {
        return this.dialogPane;
    }

    /**
     * コンストラクタ.
     */
    public AbstractDialog() {
        super();
    }

    /**
     * コンストラクタで親Stageを指定.
     * @param parentStage
     */
    public AbstractDialog(Stage parentStage) {
        this.parentStage = parentStage;
    }

    private void preparation(Pane dialogContentPane) {
        AbstractDialog<T> dialog = this;
        // Stageを生成
        this.stage = new Stage();
        // 表示するScreenを計算
        Screen screen = Screen.getPrimary();
        if (this.parentStage != null) {
            if (this.parentStage != null) {
                for (Screen loopScreen: Screen.getScreensForRectangle(this.parentStage.getX(), this.parentStage.getY(), this.parentStage.getWidth(), this.parentStage.getHeight())) {
                    screen = loopScreen;
                }
            }
            this.parentStage.getScene().getRoot().setDisable(true);
            this.parentStage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this.nonCloseEvent);
            this.parentStage.getScene().getWindow().focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (newValue) {
                        dialog.stage.requestFocus();
                    }
                }
            });
        }
        this.dialogPane.setPrefSize(screen.getVisualBounds().getWidth(), screen.getVisualBounds().getHeight());
        // StackPaneの背景セット（アルファ値が0.5以下だとクリックイベント拾わない）
        this.dialogPane.setStyle(CSSHelper.updateStyleValue(this.dialogPane.getStyle(), "-fx-background-color", "rgba(180,180,180,0.51)"));
        // StackPaneをクリックしたら閉じる処理
        if (this.isClosableAtStackPaneClicked()) {
            this.dialogPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    dialog.close();
                }
            });
            dialogContentPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    event.consume();
                }
            });
        }
        // ダイアログの拡大縮小
        dialogContentPane.setScaleX(this.scale);
        dialogContentPane.setScaleY(this.scale);
        // ダイアログのサイズを設定する
        dialogContentPane.setMinWidth(dialogContentPane.getPrefWidth());
        dialogContentPane.setMaxWidth(dialogContentPane.getPrefWidth());
        dialogContentPane.setMinHeight(dialogContentPane.getPrefHeight());
        dialogContentPane.setMaxHeight(dialogContentPane.getPrefHeight());
        this.dialogPane.getChildren().add(dialogContentPane);
        // sceneをセット
        Scene scene = new Scene(this.dialogPane);
        scene.setFill(null);
        // stageを設定
        this.stage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this.nonCloseEvent);
        this.stage.initStyle(StageStyle.TRANSPARENT);
        // FullScreenで表示する
        this.stage.setX(screen.getVisualBounds().getMinX());
        this.stage.setY(screen.getVisualBounds().getMinY());
        this.stage.setFullScreen(true);
        this.stage.setFullScreenExitHint("");
        this.stage.setFullScreenExitKeyCombination(new KeyCharacterCombination("disabled"));
        // アイコンセット
        if (this.parentStage == null || this.parentStage.getIcons().size() == 0) {
            this.stage.getIcons().add(new Image(AbstractDialog.class.getResourceAsStream("icon16.png")));
            this.stage.getIcons().add(new Image(AbstractDialog.class.getResourceAsStream("icon32.png")));
            this.stage.getIcons().add(new Image(AbstractDialog.class.getResourceAsStream("icon48.png")));
            this.stage.getIcons().add(new Image(AbstractDialog.class.getResourceAsStream("icon64.png")));
            this.stage.getIcons().add(new Image(AbstractDialog.class.getResourceAsStream("icon128.png")));
        } else {
            this.stage.getIcons().setAll(this.parentStage.getIcons());
        }
        this.stage.setScene(scene);
    }

    /**
     * ダイアログを表示する前の準備処理. show()またはshowAndWait()を呼び出した際に自動実行される.
     */
    protected abstract void preparationCallback();

    /**
     * ダイアログを表示する. ダイアログを表示すると親Paneは操作不可になる.
     * @param dialogContentPane ダイアログ内容
     */
    protected void show(Pane dialogContentPane) {
        this.preparation(dialogContentPane);
        this.preparationCallback();
        this.stage.show();
    }

    /**
     * ダイアログを表示して終了まで待機する.
     * @param dialogContentPane
     */
    protected T showAndWait(Pane dialogContentPane) {
        this.preparation(dialogContentPane);
        this.preparationCallback();
        this.stage.showAndWait();
        return this.getResult();
    }

    // 閉じられないようにするEventHander
    private EventHandler<WindowEvent> nonCloseEvent = new EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent event) {
            event.consume();
        }
    };

    @Override
    public abstract void show();

    /**
     * ダイアログを表示する.
     * @return 結果
     */
    public abstract T showAndWait();

    @Override
    public void close() {
        if (this.parentStage != null) {
            this.parentStage.getScene().getRoot().setDisable(false);
            this.parentStage.removeEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this.nonCloseEvent);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    AbstractDialog.this.parentStage.requestFocus();
                }
            });
        }
        if (this.closeEvent != null) {
            this.closeEvent.handle(this.getResult());
        }
        this.stage.removeEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this.nonCloseEvent);
        this.stage.close();
    }

    private CloseEventHandler<T> closeEvent;

    @Override
    public void setCloseEvent(CloseEventHandler<T> closeEvent) {
        this.closeEvent = closeEvent;
    }

    private T result;

    @Override
    public void setResult(T resultValue) {
        this.result = resultValue;
    }

    @Override
    public T getResult() {
        return this.result;
    }

    private double scale = 1;

    @Override
    public void setScale(double scale) {
        this.scale = scale;
    }

}
