package com.hirohiro716.javafx.dialog.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import com.hirohiro716.StringConverter;
import com.hirohiro716.javafx.FXMLLoader;
import com.hirohiro716.javafx.LayoutHelper;
import com.hirohiro716.javafx.control.EnterFireButton;
import com.hirohiro716.javafx.control.LimitTextArea;
import com.hirohiro716.javafx.dialog.AbstractDialog;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * TextArea入力ダイアログを表示するクラス.
 * @author hiro
 */
public class LimitTextAreaDialog extends AbstractDialog<String> {

    @FXML
    private AnchorPane paneRoot;

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
     */
    public LimitTextAreaDialog() {
        super();
    }

    /**
     * コンストラクタ.
     * @param parentStage
     */
    public LimitTextAreaDialog(Stage parentStage) {
        super(parentStage);
    }

    @Override
    public AnchorPane getContentPane() {
        return this.paneRoot;
    }

    @Override
    protected void preparationCallback() {
        LimitTextAreaDialog dialog = this;
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
        // テキストの入力制限を追加する
        for (int index = 0; index < this.permitRegexPattern.size(); index++) {
            this.limitTextArea.addPermitRegex(this.permitRegexPattern.get(index), this.permitRegexReverse.get(index));
        }
        this.limitTextArea.setPermitTab(this.buttonOk);
        // 初期値を入力
        this.limitTextArea.setText(this.defaultValue);
        this.limitTextArea.selectAll();
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
            HBox hboxButton = (HBox) this.buttonCancel.getParent();
            hboxButton.getChildren().remove(this.buttonCancel);
        }
        // キーボードイベント定義
        this.getStackPane().addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
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
    public String showAndWait() {
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
     * テキストフィールドの初期値をセットする.
     * @param defaultValue
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
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

    @Override
    public boolean isClosableAtStackPaneClicked() {
        return this.isCancelable;
    }

}
