package com.hirohiro716.javafx.dialog.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import com.hirohiro716.StringConverter;
import com.hirohiro716.javafx.FXMLLoader;
import com.hirohiro716.javafx.LayoutHelper;
import com.hirohiro716.javafx.control.EnterFireButton;
import com.hirohiro716.javafx.control.LimitTextArea;
import com.hirohiro716.javafx.dialog.AbstractPaneDialog;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

/**
 * TextArea入力ダイアログを表示するクラス.
 * @author hiro
 */
public class LimitTextAreaPaneDialog extends AbstractPaneDialog<String> {

    @FXML
    private Label labelTitle;

    @FXML
    private AnchorPane paneMessage;

    @FXML
    private LimitTextArea limitTextArea;

    @FXML
    private EnterFireButton buttonOk;

    @FXML
    private EnterFireButton buttonCancel;

    /**
     * コンストラクタ.
     * @param parentPane
     */
    public LimitTextAreaPaneDialog(Pane parentPane) {
        super(parentPane);
    }

    @Override
    public void show() {
        LimitTextAreaPaneDialog dialog = LimitTextAreaPaneDialog.this;
        // ダイアログ表示
        try {
            FXMLLoader fxmlHelper = new FXMLLoader(LimitTextAreaDialog.class.getResource(LimitTextAreaDialog.class.getSimpleName() + ".fxml"), this);
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
        // テキストの入力制限を追加する
        for (int index = 0; index < this.permitRegexPattern.size(); index++) {
            this.limitTextArea.addPermitRegex(this.permitRegexPattern.get(index), this.permitRegexReverse.get(index));
        }
        this.limitTextArea.setPermitTab(this.buttonOk);
        // 初期値を入力
        this.limitTextArea.setText(this.defaultValue);
        // ボタンのイベント定義
        this.buttonOk.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dialog.setResult(StringConverter.nullReplace(dialog.limitTextArea.getText(), ""));
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
        this.getDialogPane().addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.isAltDown() == false) {
                    return;
                }
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
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                dialog.buttonCancel.requestFocus();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        dialog.limitTextArea.requestFocus();
                        dialog.limitTextArea.selectEnd();
                    }
                });
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

    private ArrayList<Pattern> permitRegexPattern = new ArrayList<>();
    private ArrayList<Boolean> permitRegexReverse = new ArrayList<>();

    /**
     * テキストの入力制限定義を追加する.
     * @param permitRegex 正規表現パターン
     * @param isReverse 条件を逆転するか
     */
    public void addPermitRegex(Pattern permitRegex, boolean isReverse) {
        this.permitRegexPattern.add(permitRegex);
        this.permitRegexReverse.add(isReverse);
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

}
