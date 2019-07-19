package com.hirohiro716.javafx.dialog.menu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import com.hirohiro716.javafx.FXMLLoader;
import com.hirohiro716.javafx.LayoutHelper;
import com.hirohiro716.javafx.dialog.AbstractDialog;
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
public class SubMenu extends AbstractDialog<Void> {

    @FXML
    private AnchorPane paneRoot;

    @FXML
    private Label labelTitle;

    @FXML
    private Label labelClose;

    @Override
    protected Label getLabelTitle() {
        return this.labelTitle;
    }

    @Override
    protected Pane createContentPane() {
        SubMenu dialog = this;
        // Paneの生成
        Pane pane;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SubMenu.class.getResource(SubMenu.class.getSimpleName() + ".fxml"), this);
            pane = fxmlLoader.getPaneRoot();
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
        // FlowPane生成
        this.flowPaneNodes = new FlowPane();
        this.flowPaneNodes.setVgap(10);
        this.flowPaneNodes.setHgap(10);
        LayoutHelper.setAnchor(this.flowPaneNodes, 90, 30, 30, 30);
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
        return pane;
    }

    @Override
    public void breforeShowPrepare() {
        // 画面サイズ
        this.getContentPane().setPrefSize(this.width, this.height);
        this.getContentPane().setMaxSize(this.width, this.height);
        this.getContentPane().setMinSize(this.width, this.height);
        // Nodeの表示
        this.getContentPane().getChildren().add(this.flowPaneNodes);
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
    }

    @Override
    public boolean isClosableAtStackPaneClicked() {
        return true;
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

}
