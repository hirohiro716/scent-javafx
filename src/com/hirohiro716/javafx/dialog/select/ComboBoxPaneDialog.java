package com.hirohiro716.javafx.dialog.select;

import java.io.IOException;

import com.hirohiro716.StringConverter;
import com.hirohiro716.javafx.FXMLLoader;
import com.hirohiro716.javafx.LayoutHelper;
import com.hirohiro716.javafx.control.EnterFireButton;
import com.hirohiro716.javafx.dialog.AbstractDialog.CloseEventHandler;
import com.hirohiro716.javafx.dialog.AbstractPaneDialog;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

/**
 * コンボボックス入力ダイアログを表示するクラス.
 * @author hiro
 */
public class ComboBoxPaneDialog extends AbstractPaneDialog<String> {

    @FXML
    private Label labelTitle;

    @FXML
    private AnchorPane paneMessage;

    @FXML
    private ComboBox<String> comboBox;

    @FXML
    private EnterFireButton buttonOk;

    @FXML
    private EnterFireButton buttonCancel;

    /**
     * コンストラクタ
     * @param parentPane
     */
    public ComboBoxPaneDialog(Pane parentPane) {
        super(parentPane);
    }

    @Override
    public void show() {
        // ダイアログ表示
        try {
            FXMLLoader fxmlHelper = new FXMLLoader(this.getClass().getResource("ComboBoxDialog.fxml"), this);
            this.show(fxmlHelper.getPaneRoot());
        } catch (IOException exception) {
            return;
        }
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
        // コンボボックスアイテムのセット
        this.comboBox.setItems(this.items);
        // コンボボックスの初期値をセット
        if (this.defaultValue != null) {
            this.comboBox.setValue(this.defaultValue);
        }
        // ボタンのイベント定義
        this.buttonOk.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ComboBoxPaneDialog.this.setResult(StringConverter.nullReplace(ComboBoxPaneDialog.this.comboBox.getValue(), ""));
                ComboBoxPaneDialog.this.close();
                event.consume();
            }
        });
        if (this.isCancelable) {
            this.buttonCancel.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    ComboBoxPaneDialog.this.setResult(null);
                    ComboBoxPaneDialog.this.close();
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
                if (event.isAltDown() == false) {
                    return;
                }
                switch (event.getCode()) {
                case O:
                    ComboBoxPaneDialog.this.buttonOk.fire();
                    event.consume();
                    break;
                case C:
                    ComboBoxPaneDialog.this.buttonCancel.fire();
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

    private ObservableList<String> items;

    /**
     * コンボボックスのアイテムを指定する.
     * @param items
     */
    public void setItems(ObservableList<String> items) {
        this.items = items;
    }

    /**
     * コンボボックスのアイテムを取得する.
     * @return items
     */
    public ObservableList<String> getItems() {
        return this.items;
    }

    private String defaultValue;

    /**
     * コンボボックスの初期値をセットする.
     * @param defaultValue
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
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
     * @param <T> javafx.scene.layout.Paneを継承したクラスオブジェクト
     * @param title タイトル
     * @param message メッセージ
     * @param items コンボボックスのアイテム
     * @param parentPane 表示対象Pane
     * @param closeEvent 閉じる際の処理
     */
    public static <T extends Pane> void show(String title, String message, ObservableList<String> items, T parentPane, CloseEventHandler<String> closeEvent) {
        ComboBoxPaneDialog dialog = new ComboBoxPaneDialog(parentPane);
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
