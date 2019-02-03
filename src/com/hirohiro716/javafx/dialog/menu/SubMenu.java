package com.hirohiro716.javafx.dialog.menu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import com.hirohiro716.javafx.FXMLLoader;
import com.hirohiro716.javafx.LayoutHelper;
import com.hirohiro716.javafx.dialog.AbstractPaneDialog;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

/**
 * サブメニューを表示するクラス.
 * @author hiro
 */
public class SubMenu extends AbstractPaneDialog<Void> {

    @FXML
    private AnchorPane paneRoot;

    @FXML
    private Label labelTitle;

    @FXML
    private Label labelClose;
    
    /**
     * コンストラクタ.
     * @param parentPane
     */
    public SubMenu(Pane parentPane) {
        super(parentPane);
        this.flowPaneNodes = new FlowPane();
        this.flowPaneNodes.setVgap(10);
        this.flowPaneNodes.setHgap(10);
        LayoutHelper.setAnchor(this.flowPaneNodes, 90, 30, 30, 30);
    }

    @Override
    public AnchorPane getContentPane() {
        return this.paneRoot;
    }

    @Override
    public void show() {
        SubMenu dialog = this;
        // ダイアログ表示
        Pane pane;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SubMenu.class.getResource(SubMenu.class.getSimpleName() + ".fxml"), this);
            pane = fxmlLoader.getPaneRoot();
            this.show(pane);
        } catch (IOException exception) {
            exception.printStackTrace();
            return;
        }
        // タイトルのセット
        this.labelTitle.setText(this.title);
        // 画面サイズ
        pane.setMaxSize(this.width, this.height);
        pane.setMinSize(this.width, this.height);
        // Nodeの表示
        pane.getChildren().add(this.flowPaneNodes);
        long sleepTime = 100;
        for (Node node: this.nodes) {
            node.setOpacity(0);
            this.flowPaneNodes.getChildren().add(node);
            long partSleepTime = sleepTime;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(partSleepTime);
                        for (double opacity = 0; opacity <= 1; opacity += 0.1) {
                            double partOpacity = opacity;
                            Thread.sleep(10);
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    node.setOpacity(partOpacity);
                                }
                            });
                        }
                    } catch (InterruptedException exception) {
                    }
                }
            });
            thread.start();
            sleepTime += 100;
        }
        // 閉じるイベント定義
        this.labelClose.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                dialog.close();
            }
        });
        this.labelClose.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                dialog.labelClose.setOpacity(0.5);
            }
        });
        this.labelClose.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                dialog.labelClose.setOpacity(1);
            }
        });
    }

    private String title;

    /**
     * タイトルをセットする.
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    private double width = 400;
    
    /**
     * ダイアログの幅を指定する. 初期値は400.
     * @param width
     */
    public void setWidth(double width) {
        this.width = width;
    }
    
    private double height = 300;
    
    /**
     * ダイアログの高さを指定する. 初期値は300.
     * @param height
     */
    public void setHeight(double height) {
        this.height = height;
    }
    
    /**
     * ダイアログのサイズを指定する. 初期値は幅400:高さ300.
     * @param width
     * @param height
     */
    public void setSize(double width, double height) {
        this.width = width;
        this.height = height;
    }
    
    private ArrayList<Node> nodes = new ArrayList<>();
    
    /**
     * FlowPaneに表示するNodeを追加する.
     * @param nodes
     */
    public void addNodes(Node... nodes) {
        for (Node node: nodes) {
            this.nodes.add(node);
        }
    }
    
    /**
     * FlowPaneに表示するNodeを追加する.
     * @param nodes
     */
    public void addNodes(Collection<Node> nodes) {
        this.nodes.addAll(nodes);
    }

    private FlowPane flowPaneNodes;
    
    /**
     * FlowPaneを取得する.
     * @return FlowPane
     */
    public FlowPane getFlowPane() {
        return this.flowPaneNodes;
    }

    @Override
    public boolean isClosableAtStackPaneClicked() {
        return true;
    }

}
