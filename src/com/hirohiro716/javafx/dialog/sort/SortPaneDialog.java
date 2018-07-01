package com.hirohiro716.javafx.dialog.sort;

import java.io.IOException;
import java.util.LinkedHashMap;

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
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * LinkedHashMapの順番をユーザーに並び替えさせるダイアログを表示するクラス.
 * @author hiro
 * @param <E> ソートするItemの型
 */
public class SortPaneDialog<E> extends AbstractPaneDialog<LinkedHashMap<E, String>> {

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
     * コンストラクタ
     * @param parentPane
     */
    public SortPaneDialog(Pane parentPane) {
        super(parentPane);
    }

    @Override
    public void show() {
        // ダイアログ表示
        try {
            FXMLLoader fxmlHelper = new FXMLLoader(this.getClass().getResource("SortDialog.fxml"), this);
            this.show(fxmlHelper.getPaneRoot());
        } catch (IOException exception) {
            return;
        }
        SortPaneDialog<E> dialog = SortPaneDialog.this;
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
        // 並べ替え用のVBoxを作成
        SortDialog.createItems(this.scrollPane, this.vbox, this.items);
        // ボタンのイベント定義
        this.buttonOk.setOnAction(new EventHandler<ActionEvent>() {
            @SuppressWarnings("unchecked")
            @Override
            public void handle(ActionEvent event) {
                dialog.items.clear();
                for (Node node: dialog.vbox.getChildren()) {
                    Label label = (Label) node;
                    dialog.items.put((E) label.getUserData(), label.getText());
                }
                dialog.setResult(dialog.items);
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

    private LinkedHashMap<E, String> items;

    /**
     * 並び替えるアイテムを指定する.
     * @param items
     */
    public void setItems(LinkedHashMap<E, String> items) {
        this.items = items;
    }

    /**
     * 並び替えるアイテムを取得する.
     * @return items
     */
    public LinkedHashMap<E, String> getItems() {
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
     * @param <E> 並び替えるデータの型
     * @param <T> javafx.scene.layout.Paneを継承したクラスオブジェクト
     * @param title タイトル
     * @param message メッセージ
     * @param items コンボボックスのアイテム
     * @param parentPane 表示対象Pane
     * @param closeEvent 閉じる際の処理
     */
    public static <E, T extends Pane> void show(String title, String message, LinkedHashMap<E, String> items, T parentPane, CloseEventHandler<LinkedHashMap<E, String>> closeEvent) {
        SortPaneDialog<E> dialog = new SortPaneDialog<>(parentPane);
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
