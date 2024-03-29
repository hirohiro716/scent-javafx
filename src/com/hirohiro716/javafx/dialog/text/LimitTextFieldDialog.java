package com.hirohiro716.javafx.dialog.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import com.hirohiro716.StringConverter;
import com.hirohiro716.javafx.FXMLLoader;
import com.hirohiro716.javafx.IMEHelper;
import com.hirohiro716.javafx.LayoutHelper;
import com.hirohiro716.javafx.control.EnterFireButton;
import com.hirohiro716.javafx.control.LimitTextField;
import com.hirohiro716.javafx.dialog.AbstractDialog;
import com.hirohiro716.robot.InterfaceTypingRobotJapanese.IMEMode;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

/**
 * TextField入力ダイアログを表示するクラス。
 *
 * @author hiro
 *
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

    @Override
    protected Label getLabelTitle() {
        return this.labelTitle;
    }

    @Override
    protected Pane createContentPane() {
        LimitTextFieldDialog dialog = this;
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
                dialog.setResult(StringConverter.nullReplace(dialog.limitTextField.getText(), ""));
                dialog.close();
            }
        });
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
        return fxmlLoader.getPaneRoot();
    }

    @Override
    public void breforeShowPrepare() {
        LimitTextFieldDialog dialog = this;
        // テキストの入力制限を追加する
        for (int index = 0; index < this.permitRegexPattern.size(); index++) {
            this.limitTextField.addPermitRegex(this.permitRegexPattern.get(index), this.permitRegexReverse.get(index));
        }
        // IMEモードをセットする
        IMEHelper.apply(this.limitTextField, this.imeMode);
        // 文字配置をセット
        this.limitTextField.setAlignment(this.pos);
        // 初期値を入力
        this.limitTextField.setText(this.defaultValue);
        this.limitTextField.selectAll();
        // テキストフィールドのイベント定義
        this.limitTextField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dialog.buttonOk.fire();
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
        // 初期フォーカス
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                dialog.buttonCancel.requestFocus();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        dialog.limitTextField.requestFocus();
                    }
                });
            }
        });
    }

    @Override
    public boolean isClosableAtStackPaneClicked() {
        return this.isCancelable;
    }

    /**
     * メッセージ内容をセットする。
     *
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
     * メッセージに代わるNodeをセットする。
     *
     * @param node
     */
    public void setMessageNode(Node node) {
        this.paneMessage.getChildren().clear();
        this.paneMessage.getChildren().add(node);
    }

    private ArrayList<Pattern> permitRegexPattern = new ArrayList<>();
    
    private ArrayList<Boolean> permitRegexReverse = new ArrayList<>();

    /**
     * テキストの入力制限定義を追加する。
     *
     * @param permitRegex 正規表現パターン
     * @param isReverse 条件を逆転するか
     */
    public void addPermitRegex(Pattern permitRegex, boolean isReverse) {
        this.permitRegexPattern.add(permitRegex);
        this.permitRegexReverse.add(isReverse);
    }
    
    private IMEMode imeMode = null;
    
    /**
     * IMEモードをセットする。
     *
     * @param imeMode
     */
    public void setIMEMode(IMEMode imeMode) {
        this.imeMode = imeMode;
    }
    
    private Pos pos = Pos.CENTER_LEFT;
    
    /**
     * テキストフィールドの文字の配置をセットする。
     *
     * @param pos
     */
    public void setTextFieldAlignment(Pos pos) {
        this.pos = pos;
    }

    private String defaultValue;

    /**
     * テキストフィールドの初期値をセットする。
     *
     * @param defaultValue
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    private boolean isCancelable = true;

    /**
     * キャンセル可能かを設定する。初期値はtrue。
     *
     * @param isCancelable
     */
    public void setCancelable(boolean isCancelable) {
        this.isCancelable = isCancelable;
    }

    /**
     * キャンセル可能かを取得する。
     *
     * @return キャンセル可能か
     */
    public boolean isCancelable() {
        return this.isCancelable;
    }
}
