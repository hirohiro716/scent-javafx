package com.hirohiro716.javafx.dialog;

import java.util.ArrayList;

import com.hirohiro716.javafx.CSSHelper;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 * ダイアログを表示するための抽象クラス。
 *
 * @author hiro
 * @param <T> ダイアログのResultタイプ
 */
public abstract class AbstractDialog<T> {

    /**
     * コンストラクタ。
     */
    public AbstractDialog() {
        this.contentPane = this.createContentPane();
    }
    
    private String title;
    
    /**
     * ダイアログのタイトルを取得する。
     *
     * @return タイトル
     */
    protected String getTitle() {
        return this.title;
    }
    
    /**
     * ダイアログのタイトルをセットする。
     *
     * @param title タイトル
     */
    public void setTitle(String title) {
        this.title = title;
        if (this.getLabelTitle() != null) {
            this.getLabelTitle().setText(title);
        }
    }
    
    /**
     * タイトルを表示するLabelを取得する。
     *
     * @return Label
     */
    protected abstract Label getLabelTitle();

    private StackPane stackPane = new StackPane();

    /**
     * ダイアログのStackPaneを取得する。
     *
     * @return StackPane
     */
    public StackPane getStackPane() {
        return this.stackPane;
    }

    private Pane contentPane;

    /**
     * ダイアログのPaneを取得する。
     *
     * @return Pane
     */
    public Pane getContentPane() {
        return this.contentPane;
    }
    
    private boolean isShowed = false;

    /**
     * ダイアログを表示するPaneを作成する。show()またはshowAndWait()を呼び出した際に自動実行される。
     *
     * @return 作成したPane
     */
    protected abstract Pane createContentPane();
    
    private Stage owner = null;
    
    private Stage stage;
    
    /**
     * ダイアログのStageを取得する。
     *
     * @return Stage
     */
    public Stage getStage() {
        return this.stage;
    }
    
    private void craeteStage(Stage owner) {
        AbstractDialog<T> dialog = this;
        // 親Stageを保持
        this.owner = owner;
        // Stageを生成
        this.stage = new Stage();
        // タイトル
        this.stage.setTitle(this.getTitle());
        // 表示するScreenを計算
        Screen screen = Screen.getPrimary();
        if (this.owner != null) {
            if (this.owner != null) {
                for (Screen loopScreen: Screen.getScreensForRectangle(this.owner.getX(), this.owner.getY(), this.owner.getWidth(), this.owner.getHeight())) {
                    screen = loopScreen;
                }
            }
            this.owner.getScene().getRoot().setDisable(true);
            this.owner.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this.nonCloseEvent);
        }
        this.stackPane.setPrefSize(screen.getVisualBounds().getWidth(), screen.getVisualBounds().getHeight());
        // StackPaneの背景セット(アルファ値が0.5以下だとクリックイベント拾わない)
        this.stackPane.setStyle(CSSHelper.updateStyleValue(this.stackPane.getStyle(), "-fx-background-color", "rgba(180,180,180,0.51)"));
        // StackPaneをクリックしたら閉じる処理
        if (this.isClosableAtStackPaneClicked()) {
            this.stackPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    dialog.close();
                }
            });
            this.contentPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    event.consume();
                }
            });
        }
        // ダイアログのサイズを設定する
        this.contentPane.setMinWidth(this.contentPane.getPrefWidth());
        this.contentPane.setMaxWidth(this.contentPane.getPrefWidth());
        this.contentPane.setMinHeight(this.contentPane.getPrefHeight());
        this.contentPane.setMaxHeight(this.contentPane.getPrefHeight());
        this.stackPane.getChildren().add(this.contentPane);
        // sceneをセット
        Scene scene = new Scene(this.stackPane);
        scene.setFill(null);
        // stageを設定
        this.stage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this.nonCloseEvent);
        this.stage.initStyle(StageStyle.TRANSPARENT);
        this.stage.initOwner(owner);
        // FullScreenで表示する
        this.stage.setX(screen.getVisualBounds().getMinX());
        this.stage.setY(screen.getVisualBounds().getMinY());
        // アイコンセット
        if (this.owner == null || this.owner.getIcons().size() == 0) {
            this.stage.getIcons().add(new Image(AbstractDialog.class.getResourceAsStream("icon16.png")));
            this.stage.getIcons().add(new Image(AbstractDialog.class.getResourceAsStream("icon32.png")));
            this.stage.getIcons().add(new Image(AbstractDialog.class.getResourceAsStream("icon48.png")));
            this.stage.getIcons().add(new Image(AbstractDialog.class.getResourceAsStream("icon64.png")));
            this.stage.getIcons().add(new Image(AbstractDialog.class.getResourceAsStream("icon128.png")));
        } else {
            this.stage.getIcons().setAll(this.owner.getIcons());
        }
        this.stage.setScene(scene);
    }

    private EventHandler<WindowEvent> nonCloseEvent = new EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent event) {
            event.consume();
        }
    };

    /**
     * ダイアログを表示する前に行う処理。
     */
    public abstract void breforeShowPrepare(); 
    
    /**
     * ダイアログを表示する。ダイアログを表示するとOwnerのStageは操作不可になる。
     *
     * @param owner 親Stage
     */
    public void show(Stage owner) {
        if (this.isShowed == true) {
            return;
        }
        this.isShowed = true;
        this.craeteStage(owner);
        this.breforeShowPrepare();
        this.stage.show();
    }

    /**
     * ダイアログを表示して終了まで待機する。
     *
     * @param owner 親Stage
     * @return 結果
     */
    public T showAndWait(Stage owner) {
        if (this.isShowed == true) {
            return null;
        }
        this.isShowed = true;
        this.craeteStage(owner);
        this.breforeShowPrepare();
        this.stage.showAndWait();
        return this.getResult();
    }
    
    private ArrayList<Node> disableChangeNodes = new ArrayList<Node>();
    
    private Pane parent;
    
    /**
     * 親Paneを取得する。
     *
     * @return 親Pane
     */
    public Pane getParentPane() {
        return this.parent;
    }
    
    /**
     * ダイアログを表示する。ダイアログを表示すると親Paneは操作不可になる。
     *
     * @param parent 親Pane
     */
    public void showOnPane(Pane parent) {
        AbstractDialog<T> dialog = this;
        if (parent == null || this.isShowed == true) {
            return;
        }
        // 親Pane内の子をすべて使用不可にする
        this.parent = parent;
        for (Node node: this.parent.getChildren()) {
            if (node.isDisabled() == false) {
                this.disableChangeNodes.add(node);
                node.setDisable(true);
            }
        }
        // StackPaneを設定
        this.stackPane.setPrefSize(this.parent.getWidth(), this.parent.getHeight());
        this.stackPane.setStyle(CSSHelper.updateStyleValue(this.stackPane.getStyle(), "-fx-background-color", "rgba(180,180,180,0.5)"));
        // StackPaneをクリックしたら閉じる処理
        if (this.isClosableAtStackPaneClicked()) {
            this.stackPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    dialog.close();
                }
            });
            this.contentPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    event.consume();
                }
            });
        }
        // ダイアログのサイズを設定する
        this.contentPane.setMinWidth(this.contentPane.getPrefWidth());
        this.contentPane.setMaxWidth(this.contentPane.getPrefWidth());
        this.contentPane.setMinHeight(this.contentPane.getPrefHeight());
        this.contentPane.setMaxHeight(this.contentPane.getPrefHeight());
        this.stackPane.getChildren().add(this.contentPane);
        // 親Paneリサイズ時にダイアログも同期
        this.parent.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                dialog.stackPane.setPrefWidth(newValue.doubleValue());
            }
        });
        this.parent.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                dialog.stackPane.setPrefHeight(newValue.doubleValue());
            }
        });
        // ダイアログ表示前の処理
        this.isShowed = true;
        this.breforeShowPrepare();
        this.parent.getChildren().add(this.stackPane);
    }

    /**
     * ダイアログを閉じる。
     */
    public void close() {
        AbstractDialog<T> dialog = this;
        // Stageを表示していた場合
        if (this.stage != null) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if (dialog.owner != null) {
                        dialog.owner.getScene().getRoot().setDisable(false);
                        dialog.owner.removeEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, dialog.nonCloseEvent);
                        dialog.owner.requestFocus();
                    }
                    if (dialog.closeEvent != null) {
                        dialog.closeEvent.handle(dialog.getResult());
                    }
                    dialog.stage.removeEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, dialog.nonCloseEvent);
                    dialog.stage.close();
                }
            });
        }
        // Paneの中にダイアログを表示していた場合
        if (this.parent != null) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    dialog.parent.getChildren().remove(dialog.stackPane);
                    for (Node node: dialog.disableChangeNodes) {
                        node.setDisable(false);
                    }
                    if (dialog.closeEvent != null) {
                        dialog.closeEvent.handle(dialog.getResult());
                    }
                }
            });
        }
    }

    /**
     * ダイアログのStackPaneをクリックした場合に閉じることができるかどうか。
     *
     * @return 結果
     */
    public abstract boolean isClosableAtStackPaneClicked();
    
    private CloseEventHandler<T> closeEvent;

    /**
     * ダイアログを閉じた時のイベントをセットする。
     *
     * @param closeEvent
     */
    public void setCloseEvent(CloseEventHandler<T> closeEvent) {
        this.closeEvent = closeEvent;
    }

    private T result;

    /**
     * ダイアログの結果をセットする。
     *
     * @param resultValue
     */
    public void setResult(T resultValue) {
        this.result = resultValue;
    }

    /**
     * ダイアログの結果を取得する。
     *
     * @return 結果
     */
    public T getResult() {
        return this.result;
    }
    
    /**
     * 閉じる際の処理インターフェース。
     *
     * @author hiro
     * @param <T> ダイアログのResultタイプ
     */
    public interface CloseEventHandler<T> {

        /**
         * ダイアログを閉じる際の処理
         * @param resultValue
         */
        public abstract void handle(T resultValue);

    }
    
    /**
     * ダイアログに対する処理を行うコールバック。
     *
     * @author hiro
     * @param <D> ダイアログ型
     */
    public interface DialogCallback<D extends AbstractDialog<?>> {
        
        /**
         * ダイアログに対する処理。
         *
         * @param dialog
         */
        public abstract void call(D dialog);
        
    }}
