package com.hirohiro716.javafx.dialog.select;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import com.hirohiro716.javafx.FXMLLoader;
import com.hirohiro716.javafx.LayoutHelper;
import com.hirohiro716.javafx.control.EnterFireButton;
import com.hirohiro716.javafx.dialog.AbstractDialog.CloseEventHandler;
import com.hirohiro716.javafx.dialog.AbstractPaneDialog;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

/**
 * HashMapの値をユーザーに選択させるダイアログを表示するクラス.
 * @author hiro
 * @param <E> 選択するItemの型
 */
public class ListViewPaneDialog<E> extends AbstractPaneDialog<E> {

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

    /**
     * コンストラクタ
     * @param parentPane
     */
    public ListViewPaneDialog(Pane parentPane) {
        super(parentPane);
    }

    @Override
    public void show() {
        // ダイアログ表示
        try {
            FXMLLoader fxmlHelper = new FXMLLoader(this.getClass().getResource("ListViewDialog.fxml"), this);
            this.show(fxmlHelper.getPaneRoot());
        } catch (IOException exception) {
            return;
        }
        ListViewPaneDialog<E> dialog = ListViewPaneDialog.this;
        // タイトルのセット
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
        Iterator<E> iterator = this.items.keySet().iterator();
        while (iterator.hasNext()) {
            E key = iterator.next();
            this.listView.getItems().add(key);
        }
        this.listView.setPlaceholder(new Label("選択できるアイテムがありません"));
        // ボタンのイベント定義
        this.buttonOk.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dialog.setResult(dialog.listView.getSelectionModel().getSelectedItem());
                dialog.close();
                event.consume();
            }
        });
        if (this.isCancelable) {
            this.buttonCancel.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    dialog.setResult(null);
                    dialog.close();
                    event.consume();
                }
            });
        } else {
            this.buttonCancel.setVisible(false);
            LayoutHelper.setAnchor(this.buttonOk, null, 20d, 20d, null);
        }
        // キーボードイベント定義
        this.getDialogPane().addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                case O:
                    dialog.buttonOk.fire();
                    event.consume();
                    break;
                case C:
                    dialog.buttonCancel.fire();
                    event.consume();
                    break;
                default:
                    break;
                }
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

    /**
     * ダイアログを表示
     * @param <E> ListViewのItem型
     * @param <T> javafx.scene.layout.Paneを継承したクラスオブジェクト
     * @param title タイトル
     * @param message メッセージ
     * @param items コンボボックスのアイテム
     * @param parentPane 表示対象Pane
     * @param closeEvent 閉じる際の処理
     */
    public static <E, T extends Pane> void show(String title, String message, HashMap<E, String> items, T parentPane, CloseEventHandler<E> closeEvent) {
        ListViewPaneDialog<E> dialog = new ListViewPaneDialog<>(parentPane);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setItems(items);
        dialog.setCloseEvent(closeEvent);
        dialog.show();
    }

    @Override @Deprecated
    public void setWidth(double width) {
    }

    @Override @Deprecated
    public void setHeight(double height) {
    }

}
