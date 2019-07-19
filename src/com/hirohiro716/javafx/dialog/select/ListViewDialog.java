package com.hirohiro716.javafx.dialog.select;

import java.io.IOException;
import java.util.HashMap;
import com.hirohiro716.javafx.FXMLLoader;
import com.hirohiro716.javafx.LayoutHelper;
import com.hirohiro716.javafx.control.EnterFireButton;
import com.hirohiro716.javafx.dialog.AbstractDialog;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

/**
 * HashMapの値をユーザーに選択させるダイアログを表示するクラス.
 * @author hiro
 * @param <E> 選択するItemの型
 */
public class ListViewDialog<E> extends AbstractDialog<E> {

    @FXML
    private AnchorPane paneRoot;

    @FXML
    private Label labelTitle;

    @FXML
    private AnchorPane paneMessage;

    @FXML
    private ListView<E> listView;

    @FXML
    private EnterFireButton buttonOk;

    @FXML
    private EnterFireButton buttonCancel;

    @Override
    protected Label getLabelTitle() {
        return this.labelTitle;
    }

    @Override
    protected Pane createContentPane() {
        ListViewDialog<E> dialog = this;
        // Paneの生成
        FXMLLoader fxmlLoader;
        try {
            fxmlLoader = new FXMLLoader(this.getClass().getResource(this.getClass().getSimpleName() + ".fxml"), this);
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
        // ボタンのイベント定義
        this.buttonOk.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (dialog.listView.getSelectionModel().getSelectedItem() != null) {
                    dialog.setResult(dialog.listView.getSelectionModel().getSelectedItem());
                    dialog.close();
                }
            }
        });
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
        return fxmlLoader.getPaneRoot();
    }

    @Override
    public void breforeShowPrepare() {
        ListViewDialog<E> dialog = this;
        // 選択用のListViewを作成
        this.listView.setCellFactory(new Callback<ListView<E>, ListCell<E>>() {
            @Override
            public ListCell<E> call(ListView<E> param) {
                return new ListCell<E>() {
                    @Override
                    protected void updateItem(E item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(dialog.items.get(item));
                    }
                };
            }
        });
        for (E key: this.items.keySet()) {
            this.listView.getItems().add(key);
        }
        this.listView.setPlaceholder(new Label("選択できるアイテムがありません"));
        // キャンセル可能かどうか
        if (this.isCancelable) {
            this.buttonCancel.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    dialog.setResult(null);
                    dialog.close();
                }
            });
        } else {
            HBox hboxButton = (HBox) this.buttonCancel.getParent();
            hboxButton.getChildren().remove(this.buttonCancel);
        }
    }

    @Override
    public boolean isClosableAtStackPaneClicked() {
        return this.isCancelable;
    }

    /**
     * メッセージ内容をセットする.
     * @param message
     */
    public void setMessage(String message) {
        this.paneMessage.getChildren().clear();
        Label label = new Label(message);
        label.setWrapText(true);
        this.paneMessage.getChildren().add(label);
        LayoutHelper.setAnchor(label, 0, 0, 0, 0);
    }

    /**
     * メッセージに代わるNodeをセットする.
     * @param node
     */
    public void setMessageNode(Node node) {
        this.paneMessage.getChildren().clear();
        this.paneMessage.getChildren().add(node);
    }

    private HashMap<E, String> items;

    /**
     * 並び替えるアイテムを指定する.
     * @param items
     */
    public void setItems(HashMap<E, String> items) {
        this.items = items;
    }

    /**
     * 並び替えるアイテムを取得する.
     * @return items
     */
    public HashMap<E, String> getItems() {
        return this.items;
    }

    private boolean isCancelable = true;

    /**
     * キャンセル可能かを設定する. 初期値はtrue.
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
