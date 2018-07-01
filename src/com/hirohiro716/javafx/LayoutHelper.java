package com.hirohiro716.javafx;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * JavaFXのレイアウトを補助する.
 * @author hiro
 */
public class LayoutHelper {

    /**
     * AnchorPaneのアンカーを一括でセットする.
     * @param node 対象
     * @param top Topのオフセット
     * @param right Rightのオフセット
     * @param bottom Bottomのオフセット
     * @param left Leftのオフセット
     */
    public static void setAnchor(Node node, double top, double right, double bottom, double left) {
        AnchorPane.setTopAnchor(node, top);
        AnchorPane.setRightAnchor(node, right);
        AnchorPane.setBottomAnchor(node, bottom);
        AnchorPane.setLeftAnchor(node, left);
    }

    /**
     * AnchorPaneのアンカーを一括でセットする.
     * @param node 対象
     * @param top Topのオフセット
     * @param right Rightのオフセット
     * @param bottom Bottomのオフセット
     * @param left Leftのオフセット
     */
    public static void setAnchor(Node node, Double top, Double right, Double bottom, Double left) {
        AnchorPane.setTopAnchor(node, top);
        AnchorPane.setRightAnchor(node, right);
        AnchorPane.setBottomAnchor(node, bottom);
        AnchorPane.setLeftAnchor(node, left);
    }

    /**
     * 幅と高さに収まるフォントを作成する.
     * @param string 文字列
     * @param width 幅（point）
     * @param height 高さ（point）
     * @param fontFamilyName 初期フォントファミリー名
     * @param defaultFontSize 初期フォントサイズ
     * @return 調整されたフォント
     */
    public static Font createFontAccordingToFrame(String string, double width, double height, String fontFamilyName, double defaultFontSize) {
        Text text = new Text(string);
        Font font = Font.font(fontFamilyName, defaultFontSize);
        text.setFont(font);
        while (text.getLayoutBounds().getWidth() > width || text.getLayoutBounds().getHeight() > height) {
            font = new Font(fontFamilyName, font.getSize() - 0.5);
            text.setFont(font);
        }
        return font;
    }

    /**
     * テキストの自動折り返しをしつつ幅と高さに収まるフォントを作成する.
     * @param string 文字列
     * @param width 幅（point）
     * @param height 高さ（point）
     * @param fontFamilyName 初期フォントファミリー名
     * @param defaultFontSize 初期フォントサイズ
     * @return 調整されたフォント
     */
    public static Font createFontAccordingToFrameAndTextWrap(String string, double width, double height, String fontFamilyName, double defaultFontSize) {
        Text text = new Text(string);
        Font font = Font.font(fontFamilyName, defaultFontSize);
        text.setFont(font);
        text.setWrappingWidth(width);
        while (text.getLayoutBounds().getHeight() > height) {
            font = new Font(fontFamilyName, font.getSize() - 0.5);
            text.setFont(font);
        }
        return font;
    }

}
