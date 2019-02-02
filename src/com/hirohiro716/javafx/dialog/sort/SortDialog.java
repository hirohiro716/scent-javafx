package com.hirohiro716.javafx.dialog.sort;

import java.io.IOException;
import java.util.LinkedHashMap;

import com.hirohiro716.StringConverter;
import com.hirohiro716.javafx.FXMLLoader;
import com.hirohiro716.javafx.LayoutHelper;
import com.hirohiro716.javafx.control.EnterFireButton;
import com.hirohiro716.javafx.dialog.AbstractDialog;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * LinkedHashMapの順番をユーザーに並び替えさせるダイアログを表示するクラス.
 * @author hiro
 * @param <E> ソートするItemの型
 */
public class SortDialog<E> extends AbstractDialog<LinkedHashMap<E, String>> {

    @FXML
    private AnchorPane paneRoot;

    @FXML
    private Label labelTitle;

    @FXML
    private AnchorPane paneMessage;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox vbox;

    @FXML
    private EnterFireButton buttonOk;

    @FXML
    private EnterFireButton buttonCancel;

    /**
     * コンストラクタ.
     * @param sortableItems 並び替えるItem
     */
    public SortDialog(LinkedHashMap<E, String> sortableItems) {
        super();
        this.sortableItems = sortableItems;
    }

    /**
     * コンストラクタ.
     * @param sortableItems 並び替えるItems
     * @param parentStage
     */
    public SortDialog(LinkedHashMap<E, String> sortableItems, Stage parentStage) {
        super(parentStage);
        this.sortableItems = sortableItems;
    }

    private LinkedHashMap<E, String> sortableItems;

    @Override
    public AnchorPane getContentPane() {
        return this.paneRoot;
    }

    @Override
    protected void preparationCallback() {
        SortDialog<E> dialog = SortDialog.this;
        // タイトルのセット
        this.getStage().setTitle(this.title);
        this.labelTitle.setText(this.title);
        // メッセージのセット
        if (this.message != null) {
            Label label = new Label(this.message);
            label.setWrapText(true);
            this.paneMessage.getChildren().add(label);
            LayoutHelper.setAnchor(label, 0, 0, 0, 0);
        }
        // メッセージNodeのセット
        if (this.messageNode != null) {
            this.paneMessage.getChildren().add(this.messageNode);
        }
        // 並べ替え用のVBoxを作成
        createItems(this.scrollPane, this.vbox, this.sortableItems);
        // ボタンのイベント定義
        this.buttonOk.setOnAction(new EventHandler<ActionEvent>() {
            @SuppressWarnings("unchecked")
            @Override
            public void handle(ActionEvent event) {
                dialog.sortableItems.clear();
                for (Node node: dialog.vbox.getChildren()) {
                    Label label = (Label) node;
                    dialog.sortableItems.put((E) label.getUserData(), label.getText());
                }
                dialog.setResult(dialog.sortableItems);
                dialog.close();
            }
        });
        if (this.isCancelable) {
            this.buttonCancel.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    dialog.setResult(null);
                    dialog.close();
                }
            });
        } else {
            this.buttonCancel.setVisible(false);
            LayoutHelper.setAnchor(this.buttonOk, null, 20d, 20d, null);
        }
        // キーボードイベント定義
        this.getStackPane().addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                case O:
                    dialog.buttonOk.fire();
                    break;
                case C:
                    dialog.buttonCancel.fire();
                    break;
                default:
                    break;
                }
            }
        });
    }

    /**
     * マウスのドラッグアンドドロップで順番変更を可能にするイベントをセットする.
     * @param scrollPane
     * @param vbox
     * @param items
     */
    protected static <E> void createItems(ScrollPane scrollPane, VBox vbox, LinkedHashMap<E, String> items) {
        for (E key: items.keySet()) {
            final Label label = new Label(items.get(key));
            label.setUserData(key);
            vbox.getChildren().add(label);
            scrollPane.widthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    if (newValue != null) {
                        label.setPrefWidth(newValue.doubleValue() * 0.88);
                    }
                }
            });
            label.setStyle("-fx-background-color: #eee; -fx-background-radius: 5px;");
            label.setPadding(new Insets(5, 10, 5, 10));
            // 移動用の指針ラベルを作成
            final Label dummy = new Label(StringConverter.join("→ ", label.getText()));
            dummy.setOpacity(0.5);
            dummy.setPadding(new Insets(5, 10, 5, 10));
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    dummy.setPrefWidth(scrollPane.getWidth() * 0.88);
                }
            });
            // マウス押下時イベント
            label.setOnDragDetected(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    // カーソルを変えて半透明に
                    label.setCursor(Cursor.CLOSED_HAND);
                    label.setOpacity(0.5);
                    event.consume();
                }
            });
            label.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        Node node = event.getPickResult().getIntersectedNode();
                        Label draggedLabel = null;
                        if (event.getPickResult().getIntersectedNode().getParent() instanceof Label) {
                            draggedLabel = (Label) node.getParent();
                        } else if (event.getPickResult().getIntersectedNode() instanceof Label) {
                            draggedLabel = (Label) node;
                        }
                        if (draggedLabel != null && vbox.getChildren().contains(draggedLabel) && dummy != draggedLabel) {
                            if (scrollPane.getViewportBounds().getMinY() + draggedLabel.getBoundsInParent().getMinY() < 5
                                    || scrollPane.getViewportBounds().getHeight() < draggedLabel.getBoundsInParent().getMaxY() + 5) {
                                double vValue = draggedLabel.getBoundsInParent().getMinY() / vbox.getHeight();
                                scrollPane.setVvalue(vValue);
                            }
                            vbox.getChildren().remove(dummy);
                            int draggedIndex = vbox.getChildren().indexOf(draggedLabel);
                            int originIndex = vbox.getChildren().indexOf(label);
                            if (label != draggedLabel) {
                                // 一番上に持ってくる場合
                                if (draggedIndex == 0) {
                                    vbox.getChildren().add(0, dummy);
                                    return;
                                }
                                // 一番下に持ってくる場合
                                if (draggedIndex == items.size() - 1) {
                                    vbox.getChildren().add(items.size(), dummy);
                                    return;
                                }
                                // 移動先が移動元より下の場合
                                if (draggedIndex > originIndex) {
                                    vbox.getChildren().add(draggedIndex + 1, dummy);
                                    return;
                                }
                                // それ以外
                                vbox.getChildren().add(draggedIndex, dummy);
                            }
                        }
                    }
                    event.consume();
                }
            });
            label.setOnMouseReleased(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    // マウスのボタンを離したらdummyと入れ替える
                    int dummyIndex = vbox.getChildren().indexOf(dummy);
                    if (dummyIndex > -1) {
                        vbox.getChildren().remove(label);
                        vbox.getChildren().add(dummyIndex, label);
                        vbox.getChildren().remove(dummy);
                    }
                    // マウスカーソルと透明を戻す
                    label.setCursor(Cursor.DEFAULT);
                    label.setOpacity(1);
                    event.consume();
                }
            });
            label.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (event.isPrimaryButtonDown() == false) {
                        // マウスカーソルで移動可能をアピール
                        label.setCursor(Cursor.OPEN_HAND);
                    }
                }
            });
            label.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (event.isPrimaryButtonDown() == false) {
                        // マウスカーソルを戻す
                        label.setCursor(Cursor.DEFAULT);
                    }
                }
            });
        }
    }

    @Override
    public void show() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource(this.getClass().getSimpleName() + ".fxml"), this);
            this.show(fxmlLoader.getPaneRoot());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public LinkedHashMap<E, String> showAndWait() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource(this.getClass().getSimpleName() + ".fxml"), this);
            return this.showAndWait(fxmlLoader.getPaneRoot());
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    private String title;

    /**
     * タイトルをセットする.
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    private String message;

    /**
     * メッセージ内容をセットする.
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    private Node messageNode;

    /**
     * メッセージに代わるNodeをセットする.
     * @param node
     */
    public void setMessageNode(Node node) {
        this.messageNode = node;
    }

    private boolean isCancelable = true;

    /**
     * キャンセル可能かを設定する.
     * @param isCancelable
     */
    public void setCancelable(boolean isCancelable) {
        this.isCancelable = isCancelable;
    }

    /**
     * キャンセル可能かを取得する.
     * @return キャンセル可能か
     */
    public boolean isCancelable() {
        return this.isCancelable;
    }

}
