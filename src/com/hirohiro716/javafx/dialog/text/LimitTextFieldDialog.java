package com.hirohiro716.javafx.dialog.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import com.hirohiro716.StringConverter;
import com.hirohiro716.javafx.FXMLLoader;
import com.hirohiro716.javafx.LayoutHelper;
import com.hirohiro716.javafx.control.EnterFireButton;
import com.hirohiro716.javafx.control.LimitTextField;
import com.hirohiro716.javafx.dialog.AbstractDialog;

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
 * TextField入力ダイアログを表示するクラス.
 * @author hiro
 */
public class LimitTextFieldDialog extends AbstractDialog<String> {

    @FXML
    private AnchorPane paneRoot;

    @FXML
    private Label labelTitle;

    @FXML
    private AnchorPane paneMessage;

    @FXML
    private LimitTextField limitTextField;

    @FXML
    private EnterFireButton buttonOk;

    @FXML
    private EnterFireButton buttonCancel;

    /**
     * コンストラクタ.
     */
    public LimitTextFieldDialog() {
        super();
    }

    /**
     * コンストラクタ.
     * @param parentStage
     */
    public LimitTextFieldDialog(Stage parentStage) {
        super(parentStage);
    }

    @Override
    public AnchorPane getContentPane() {
        return this.paneRoot;
    }

    @Override
    protected void preparationCallback() {
        LimitTextFieldDialog dialog = this;
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
            this.limitTextField.addPermitRegex(this.permitRegexPattern.get(index), this.permitRegexReverse.get(index));
        }
        // 初期値を入力
        this.limitTextField.setText(this.defaultValue);
        // テキストフィールドのイベント定義
        this.limitTextField.addEventHandler(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                String character = event.getCharacter();
                if (character.equals("\n") || character.equals("\r")) {
                    dialog.buttonOk.fire();
                }
            }
        });
        // ボタンのイベント定義
        this.buttonOk.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dialog.setResult(StringConverter.nullReplace(dialog.limitTextField.getText(), ""));
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
     * コンボボックスの初期値をセットする.
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
