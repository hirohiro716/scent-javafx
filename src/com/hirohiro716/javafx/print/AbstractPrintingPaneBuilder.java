package com.hirohiro716.javafx.print;

import static com.hirohiro716.print.PrintHelper.*;

import javax.print.PrintException;

import com.hirohiro716.StringConverter;
import com.hirohiro716.javafx.CSSHelper;
import com.hirohiro716.javafx.LayoutHelper;
import com.hirohiro716.javafx.barcode.JAN13Helper;
import com.hirohiro716.javafx.barcode.NW7Helper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Dimension2D;
import javafx.geometry.VPos;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * JavaFXで印刷するために最適化されたPaneの抽象クラス。
 *
 * @author hiro
 */
public abstract class AbstractPrintingPaneBuilder {

    private Pane paneCanvas = new Pane();

    /**
     * 内部で作成されたPaneを取得する。
     *
     * @return Pane
     */
    public Pane getPane() {
        return this.paneCanvas;
    }

    private Font font = new Font(12);
    private String fontFamilyName = this.font.getFamily();

    /**
     * 印刷に使用するフォントを取得する。
     *
     * @return 印刷に使用するフォント
     */
    public Font getFont() {
        return this.font;
    }

    /**
     * 印刷に使用するフォントを指定する。
     *
     * @param font
     */
    public void setFont(Font font) {
        this.font = font;
    }

    /**
     * 印刷に使用するフォントを指定する。
     *
     * @param familyName
     * @param fontSize
     */
    public void setFont(String familyName, double fontSize) {
        this.fontFamilyName = familyName;
        this.font = Font.font(familyName, fontSize);
    }

    /**
     * 印刷に使用するフォントサイズを指定する。
     *
     * @param fontSize
     */
    public void setFontSize(double fontSize) {
        this.font = Font.font(this.fontFamilyName, fontSize);
    }

    /**
     * 印刷に使用するフォント名を指定する。
     *
     * @param familyName
     */
    public void setFontFamilyName(String familyName) {
        this.fontFamilyName = familyName;
        this.font = Font.font(familyName, this.font.getSize());
    }

    private double textLineSpacingRateToFontSize = 0;

    /**
     * テキストの行間をフォントサイズに対する比率で指定する。初期値は0。
     *
     * @param spacingRate フォントサイズに対する比率
     */
    public void setTextLineSpacingRateToFontSize(double spacingRate) {
        this.textLineSpacingRateToFontSize = spacingRate;
    }

    private Color color = Color.BLACK;
    
    /**
     * 使用する色を取得する。
     *
     * @return Color
     */
    public Color getColor() {
        return this.color;
    }
    
    /**
     * 使用する色を指定する。
     *
     * @param color
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * 使用する色をカラーコードで指定する。
     *
     * @param colorString HTMLまたはCSSで使用する16進数6桁カラーコード(#000000)
     */
    public void setColorString(String colorString) {
        this.color = Color.web(colorString);
    }

    private double strokeWidth = 1;

    /**
     * 使用する線の太さを指定する。
     *
     * @param strokeWidth
     */
    public void setStrokeWidth(double strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    private ObservableList<Double> strokeDashArray = FXCollections.observableArrayList();
    
    /**
     * 線の印刷に使用する破線セグメントの長さを取得する。
     *
     * @return 破線セグメントの長さ(mm)の配列
     */
    public Double[] getStrokeDashArray() {
        return this.strokeDashArray.toArray(new Double[] {});
    }

    /**
     * 線の印刷に使用する破線セグメントの長さを指定する。
     *
     * @param millimeterDashes 破線セグメントの長さ(mm)
     */
    public void setStrokeDashArray(Double... millimeterDashes) {
        this.strokeDashArray.clear();
        for (Double dash: millimeterDashes) {
            if (dash != null) {
                this.strokeDashArray.add(millimeterToPoint(dash));
            }
        }
    }

    private VPos textOriginVPos = VPos.BASELINE;

    /**
     * 文字列を描画する際のBaseLine(上下基準位置)を取得する。
     *
     * @return vPos
     */
    public VPos getTextOriginVPos() {
        return this.textOriginVPos;
    }

    /**
     * 文字列を描画する際のBaseLine(上下基準位置)を指定する。
     *
     * @param vPos
     */
    public void setTextOriginVPos(VPos vPos) {
        this.textOriginVPos = vPos;
    }
    
    /**
     * 印刷処理。
     *
     * @throws PrintException
     */
    protected abstract void build() throws PrintException;

    /**
     * 文字列を描画する。
     *
     * @param string 描画する文字列
     * @param millimeterLayoutX 左位置(mm)
     * @param millimeterLayoutY 上位置(mm)
     * @param style インラインCSSスタイル(javafx.scene.text.Textクラスに使えるプロパティ)
     * @return 描画したサイズ(mm)
     */
    public Dimension2D printText(String string, double millimeterLayoutX, double millimeterLayoutY, String style) {
        Text text = new Text(string);
        text.setFont(this.font);
        text.setX(millimeterToPoint(millimeterLayoutX));
        text.setY(millimeterToPoint(millimeterLayoutY));
        text.setTextOrigin(this.textOriginVPos);
        text.setLineSpacing(text.getFont().getSize() * this.textLineSpacingRateToFontSize); // FIXME 公式によればピクセルで指定しないとダメらしいんだけど嘘臭い
        text.setStyle(style);
        text.setStyle(CSSHelper.updateStyleValue(text.getStyle(), "-fx-fill", CSSHelper.colorToRGBA(this.color)));
        this.paneCanvas.getChildren().add(text);
        return new Dimension2D(pointToMillimeter(text.getBoundsInLocal().getWidth()) , pointToMillimeter(text.getBoundsInLocal().getHeight()));
    }

    /**
     * 文字列を描画する。
     *
     * @param string 描画する文字列
     * @param millimeterLayoutX 左位置(mm)
     * @param millimeterLayoutY 上位置(mm)
     * @return 描画したサイズ(mm)
     */
    public Dimension2D printText(String string, double millimeterLayoutX, double millimeterLayoutY) {
        return this.printText(string, millimeterLayoutX, millimeterLayoutY, null);
    }

    /**
     * 文字列を描画する。
     *
     * @param string 描画する文字列
     * @param millimeterWidth 文字列を描画する幅(mm)
     * @param millimeterLayoutX 左位置(mm)
     * @param millimeterLayoutY 上位置(mm)
     * @param style インラインCSSスタイル(javafx.scene.text.Textクラスに使えるプロパティ)
     * @return 描画したサイズ(mm)
     */
    public Dimension2D printTextRight(String string, double millimeterWidth, double millimeterLayoutX, double millimeterLayoutY, String style) {
        Text text = new Text(string);
        text.setFont(this.font);
        text.setX(millimeterToPoint(millimeterLayoutX + millimeterWidth) - text.getLayoutBounds().getWidth());
        text.setY(millimeterToPoint(millimeterLayoutY));
        text.setTextOrigin(this.textOriginVPos);
        text.setLineSpacing(text.getFont().getSize() * this.textLineSpacingRateToFontSize); // FIXME 公式によればピクセルで指定しないとダメらしいんだけど嘘臭い
        text.setStyle(style);
        text.setStyle(CSSHelper.updateStyleValue(text.getStyle(), "-fx-fill", CSSHelper.colorToRGBA(this.color)));
        this.paneCanvas.getChildren().add(text);
        return new Dimension2D(pointToMillimeter(text.getBoundsInLocal().getWidth()) , pointToMillimeter(text.getBoundsInLocal().getHeight()));
    }

    /**
     * 文字列を右寄せで描画する。
     *
     * @param string 描画する文字列
     * @param millimeterWidth 文字列を描画する幅(mm)
     * @param millimeterLayoutX 左位置(mm)
     * @param millimeterLayoutY 上位置(mm)
     * @return 描画したサイズ(mm)
     */
    public Dimension2D printTextRight(String string, double millimeterWidth, double millimeterLayoutX, double millimeterLayoutY) {
        return this.printTextRight(string, millimeterWidth, millimeterLayoutX, millimeterLayoutY, null);
    }

    /**
     * 文字列を中央寄せで描画する。
     *
     * @param string 描画する文字列
     * @param millimeterWidth 文字列を描画する幅(mm)
     * @param millimeterLayoutX 左位置(mm)
     * @param millimeterLayoutY 上位置(mm)
     * @param style インラインCSSスタイル(javafx.scene.text.Textクラスに使えるプロパティ)
     * @return 描画したサイズ(mm)
     */
    public Dimension2D printTextCenter(String string, double millimeterWidth, double millimeterLayoutX, double millimeterLayoutY, String style) {
        Text text = new Text(string);
        text.setFont(this.font);
        text.setX(millimeterToPoint(millimeterLayoutX + millimeterWidth / 2) - text.getLayoutBounds().getWidth() / 2);
        text.setY(millimeterToPoint(millimeterLayoutY));
        text.setTextOrigin(this.textOriginVPos);
        text.setLineSpacing(text.getFont().getSize() * this.textLineSpacingRateToFontSize); // FIXME 公式によればピクセルで指定しないとダメらしいんだけど嘘臭い
        text.setStyle(style);
        text.setStyle(CSSHelper.updateStyleValue(text.getStyle(), "-fx-fill", CSSHelper.colorToRGBA(this.color)));
        this.paneCanvas.getChildren().add(text);
        return new Dimension2D(pointToMillimeter(text.getBoundsInLocal().getWidth()) , pointToMillimeter(text.getBoundsInLocal().getHeight()));
    }

    /**
     * 文字列を中央寄せで描画する。
     *
     * @param string 描画する文字列
     * @param millimeterWidth 文字列を描画する幅(mm)
     * @param millimeterLayoutX 左位置(mm)
     * @param millimeterLayoutY 上位置(mm)
     * @return 描画したサイズ(mm)
     */
    public Dimension2D printTextCenter(String string, double millimeterWidth, double millimeterLayoutX, double millimeterLayoutY) {
        return this.printTextCenter(string, millimeterWidth, millimeterLayoutX, millimeterLayoutY, null);
    }

    /**
     * 文字列の描画サイズを計算する。
     *
     * @param string 対象文字列
     * @param fontFamilyName フォントファミリー
     * @param fontSize フォントサイズ
     * @return 文字列の描画サイズ
     */
    public Dimension2D calculateStringSize(String string, String fontFamilyName, double fontSize) {
        Dimension2D dimension2d = LayoutHelper.calculateStringSize(string, fontFamilyName, fontSize);
        return new Dimension2D(pointToMillimeter(dimension2d.getWidth()), pointToMillimeter(dimension2d.getHeight()));
    }
    
    /**
     * 文字列の描画サイズを計算する。
     *
     * @param string 対象文字列
     * @return 文字列の描画サイズ
     */
    public Dimension2D calculateStringSize(String string) {
        return this.calculateStringSize(string, this.fontFamilyName, this.font.getSize());
    }

    /**
     * 文字列をフォントサイズを調整して描画する。
     *
     * @param string 描画する文字列
     * @param millimeterWidth 幅(mm)
     * @param millimeterHeight 高さ(mm)
     * @param millimeterLayoutX 左位置(mm)
     * @param millimeterLayoutY 上位置(mm)
     * @param style インラインCSSスタイル(javafx.scene.text.Textクラスに使えるプロパティ)
     * @return 描画したサイズ(mm)
     */
    public Dimension2D printTextAccordingToFrame(String string, double millimeterWidth, double millimeterHeight, double millimeterLayoutX, double millimeterLayoutY, String style) {
        Text text = new Text(string);
        text.setFont(createFontAccordingToFrame(string, millimeterWidth, millimeterHeight, this.fontFamilyName, this.font.getSize()));
        text.setX(millimeterToPoint(millimeterLayoutX));
        text.setY(millimeterToPoint(millimeterLayoutY));
        text.setTextOrigin(this.textOriginVPos);
        text.setLineSpacing(text.getFont().getSize() * this.textLineSpacingRateToFontSize); // FIXME 公式によればピクセルで指定しないとダメらしいんだけど嘘臭い
        text.setStyle(style);
        text.setStyle(CSSHelper.updateStyleValue(text.getStyle(), "-fx-fill", CSSHelper.colorToRGBA(this.color)));
        this.paneCanvas.getChildren().add(text);
        return new Dimension2D(pointToMillimeter(text.getBoundsInLocal().getWidth()) , pointToMillimeter(text.getBoundsInLocal().getHeight()));
    }

    /**
     * 文字列をフォントサイズを調整して描画する。
     *
     * @param string 描画する文字列
     * @param millimeterWidth 幅(mm)
     * @param millimeterHeight 高さ(mm)
     * @param millimeterLayoutX 左位置(mm)
     * @param millimeterLayoutY 上位置(mm)
     * @return 描画したサイズ(mm)
     */
    public Dimension2D printTextAccordingToFrame(String string, double millimeterWidth, double millimeterHeight, double millimeterLayoutX, double millimeterLayoutY) {
        return this.printTextAccordingToFrame(string, millimeterWidth, millimeterHeight, millimeterLayoutX, millimeterLayoutY, null);
    }

    /**
     * 文字列をフォントサイズを調整しつつ折り返して描画する。
     *
     * @param string 描画する文字列
     * @param millimeterWidth 幅(mm)
     * @param millimeterHeight 高さ(mm)
     * @param millimeterLayoutX 左位置(mm)
     * @param millimeterLayoutY 上位置(mm)
     * @param style インラインCSSスタイル(javafx.scene.text.Textクラスに使えるプロパティ)
     * @return 描画したサイズ(mm)
     */
    public Dimension2D printWrapTextAccordingToFrame(String string, double millimeterWidth, double millimeterHeight, double millimeterLayoutX, double millimeterLayoutY, String style) {
        Text text = new Text(string);
        text.setWrappingWidth(millimeterToPoint(millimeterWidth));
        text.setFont(createFontAccordingToFrameAndTextWrap(string, millimeterWidth, millimeterHeight, this.fontFamilyName, this.font.getSize()));
        text.setX(millimeterToPoint(millimeterLayoutX));
        text.setY(millimeterToPoint(millimeterLayoutY));
        text.setTextOrigin(this.textOriginVPos);
        text.setLineSpacing(text.getFont().getSize() * this.textLineSpacingRateToFontSize); // FIXME 公式によればピクセルで指定しないとダメらしいんだけど嘘臭い
        text.setStyle(style);
        text.setStyle(CSSHelper.updateStyleValue(text.getStyle(), "-fx-fill", CSSHelper.colorToRGBA(this.color)));
        this.paneCanvas.getChildren().add(text);
        return new Dimension2D(pointToMillimeter(text.getBoundsInLocal().getWidth()) , pointToMillimeter(text.getBoundsInLocal().getHeight()));
    }

    /**
     * 文字列をフォントサイズを調整しつつ折り返して描画する。
     *
     * @param string 描画する文字列
     * @param millimeterWidth 幅(mm)
     * @param millimeterHeight 高さ(mm)
     * @param millimeterLayoutX 左位置(mm)
     * @param millimeterLayoutY 上位置(mm)
     * @return 描画したサイズ(mm)
     */
    public Dimension2D printWrapTextAccordingToFrame(String string, double millimeterWidth, double millimeterHeight, double millimeterLayoutX, double millimeterLayoutY) {
        return this.printWrapTextAccordingToFrame(string, millimeterWidth, millimeterHeight, millimeterLayoutX, millimeterLayoutY, null);
    }
    
    /**
     * 幅と高さに収まるフォントを作成する。
     *
     * @param string 文字列
     * @param millimeterWidth 幅(mm)
     * @param millimeterHeight 高さ(mm)
     * @param fontFamilyName 初期フォントファミリー名
     * @param defaultFontSize 初期フォントサイズ
     * @return 調整されたフォント
     */
    public static Font createFontAccordingToFrame(String string, double millimeterWidth, double millimeterHeight, String fontFamilyName, double defaultFontSize) {
        double width = millimeterToPoint(millimeterWidth);
        double height = millimeterToPoint(millimeterHeight);
        return LayoutHelper.createFontAccordingToFrame(string, width, height, fontFamilyName, defaultFontSize);
    }

    /**
     * 幅と高さに収まるフォントを作成する。
     *
     * @param string 文字列
     * @param millimeterWidth 幅(mm)
     * @param millimeterHeight 高さ(mm)
     */
    public void applyFontAccordingToFrame(String string, double millimeterWidth, double millimeterHeight) {
        this.font = createFontAccordingToFrame(string, millimeterWidth, millimeterHeight, this.fontFamilyName, this.font.getSize());
    }

    /**
     * テキストの自動折り返しをしつつ幅と高さに収まるフォントを作成する。
     *
     * @param string 文字列
     * @param millimeterWidth 幅(mm)
     * @param millimeterHeight 高さ(mm)
     * @param fontFamilyName 初期フォントファミリー名
     * @param defaultFontSize 初期フォントサイズ
     * @return 調整されたフォント
     */
    public static Font createFontAccordingToFrameAndTextWrap(String string, double millimeterWidth, double millimeterHeight, String fontFamilyName, double defaultFontSize) {
        double width = millimeterToPoint(millimeterWidth);
        double height = millimeterToPoint(millimeterHeight);
        return LayoutHelper.createFontAccordingToFrameAndTextWrap(string, width, height, fontFamilyName, defaultFontSize);
    }

    /**
     * テキストの自動折り返しをしつつ幅と高さに収まるフォントに変更する。
     *
     * @param string 文字列
     * @param millimeterWidth 幅(mm)
     * @param millimeterHeight 高さ(mm)
     */
    public void applyFontAccordingToFrameAndTextWrap(String string, double millimeterWidth, double millimeterHeight) {
        this.font = createFontAccordingToFrameAndTextWrap(string, millimeterWidth, millimeterHeight, this.fontFamilyName, this.font.getSize());
    }

    /**
     * 四角の枠線を描画する。
     *
     * @param millimeterWidth 幅(mm)
     * @param millimeterHeight 高さ(mm)
     * @param millimeterLayoutX 左位置(mm)
     * @param millimeterLayoutY 上位置(mm)
     * @param style インラインCSSスタイル(javafx.scene.shape.Rectangleクラスに使えるプロパティ)
     */
    public void printRectangleLine(double millimeterWidth, double millimeterHeight, double millimeterLayoutX, double millimeterLayoutY, String style) {
        Rectangle rectangle = new Rectangle(millimeterToPoint(millimeterWidth), millimeterToPoint(millimeterHeight), null);
        rectangle.setLayoutX(millimeterToPoint(millimeterLayoutX));
        rectangle.setLayoutY(millimeterToPoint(millimeterLayoutY));
        rectangle.setStroke(this.color);
        rectangle.setStrokeWidth(this.strokeWidth);
        rectangle.getStrokeDashArray().addAll(this.strokeDashArray);
        rectangle.setStyle(style);
        this.paneCanvas.getChildren().add(rectangle);
    }

    /**
     * 四角の枠線を描画する。
     *
     * @param millimeterWidth 幅(mm)
     * @param millimeterHeight 高さ(mm)
     * @param millimeterLayoutX 左位置(mm)
     * @param millimeterLayoutY 上位置(mm)
     * @param millimeterArc 角の丸みを表す円の直径(mm)
     */
    public void printRectangleLine(double millimeterWidth, double millimeterHeight, double millimeterLayoutX, double millimeterLayoutY, double millimeterArc) {
        double pointArc = millimeterToPoint(millimeterArc);
        String style = StringConverter.join("-fx-arc-width:", pointArc, ";-fx-arc-height:", pointArc, ";");
        this.printRectangleLine(millimeterWidth, millimeterHeight, millimeterLayoutX, millimeterLayoutY, style);
    }

    /**
     * 四角の枠線を描画する。
     *
     * @param millimeterWidth 幅(mm)
     * @param millimeterHeight 高さ(mm)
     * @param millimeterLayoutX 左位置(mm)
     * @param millimeterLayoutY 上位置(mm)
     */
    public void printRectangleLine(double millimeterWidth, double millimeterHeight, double millimeterLayoutX, double millimeterLayoutY) {
        this.printRectangleLine(millimeterWidth, millimeterHeight, millimeterLayoutX, millimeterLayoutY, null);
    }

    /**
     * 四角を塗りつぶして描画する。
     *
     * @param millimeterWidth 幅(mm)
     * @param millimeterHeight 高さ(mm)
     * @param millimeterLayoutX 左位置(mm)
     * @param millimeterLayoutY 上位置(mm)
     * @param style インラインCSSスタイル(javafx.scene.shape.Rectangleクラスに使えるプロパティ)
     */
    public void printRectangleFill(double millimeterWidth, double millimeterHeight, double millimeterLayoutX, double millimeterLayoutY, String style) {
        Rectangle rectangle = new Rectangle(millimeterToPoint(millimeterWidth), millimeterToPoint(millimeterHeight), this.color);
        rectangle.setLayoutX(millimeterToPoint(millimeterLayoutX));
        rectangle.setLayoutY(millimeterToPoint(millimeterLayoutY));
        rectangle.setStrokeWidth(this.strokeWidth);
        rectangle.getStrokeDashArray().addAll(this.strokeDashArray);
        rectangle.setStyle(style);
        this.paneCanvas.getChildren().add(rectangle);
    }

    /**
     * 四角を塗りつぶして描画する。
     *
     * @param millimeterWidth 幅(mm)
     * @param millimeterHeight 高さ(mm)
     * @param millimeterLayoutX 左位置(mm)
     * @param millimeterLayoutY 上位置(mm)
     * @param millimeterArc 角の丸みを表す円の直径(mm)
     */
    public void printRectangleFill(double millimeterWidth, double millimeterHeight, double millimeterLayoutX, double millimeterLayoutY, double millimeterArc) {
        double pointArc = millimeterToPoint(millimeterArc);
        String style = StringConverter.join("-fx-arc-width:", pointArc, ";-fx-arc-height:", pointArc, ";");
        this.printRectangleFill(millimeterWidth, millimeterHeight, millimeterLayoutX, millimeterLayoutY, style);
    }

    /**
     * 四角を塗りつぶして描画する。
     *
     * @param millimeterWidth 幅(mm)
     * @param millimeterHeight 高さ(mm)
     * @param millimeterLayoutX 左位置(mm)
     * @param millimeterLayoutY 上位置(mm)
     */
    public void printRectangleFill(double millimeterWidth, double millimeterHeight, double millimeterLayoutX, double millimeterLayoutY) {
        this.printRectangleFill(millimeterWidth, millimeterHeight, millimeterLayoutX, millimeterLayoutY, null);
    }

    /**
     * 楕円の枠線を描画する。
     *
     * @param millimeterWidth 幅(mm)
     * @param millimeterHeight 高さ(mm)
     * @param millimeterLayoutX 左位置(mm)
     * @param millimeterLayoutY 上位置(mm)
     * @param style インラインCSSスタイル(javafx.scene.shape.Ellipseクラスに使えるプロパティ)
     */
    public void printEllipseLine(double millimeterWidth, double millimeterHeight, double millimeterLayoutX, double millimeterLayoutY, String style) {
        double pointHalfWidth = millimeterToPoint(millimeterWidth / 2);
        double pointHalfHeight = millimeterToPoint(millimeterHeight / 2);
        Ellipse ellipse = new Ellipse(pointHalfWidth, pointHalfHeight);
        ellipse.setFill(null);
        ellipse.setLayoutX(millimeterToPoint(millimeterLayoutX) + pointHalfWidth);
        ellipse.setLayoutY(millimeterToPoint(millimeterLayoutY) + pointHalfHeight);
        ellipse.setStroke(this.color);
        ellipse.setStrokeWidth(this.strokeWidth);
        ellipse.getStrokeDashArray().addAll(this.strokeDashArray);
        ellipse.setStyle(style);
        this.paneCanvas.getChildren().add(ellipse);
    }

    /**
     * 楕円の枠線を描画する。
     *
     * @param millimeterWidth 幅(mm)
     * @param millimeterHeight 高さ(mm)
     * @param millimeterLayoutX 左位置(mm)
     * @param millimeterLayoutY 上位置(mm)
     */
    public void printEllipseLine(double millimeterWidth, double millimeterHeight, double millimeterLayoutX, double millimeterLayoutY) {
        this.printEllipseLine(millimeterWidth, millimeterHeight, millimeterLayoutX, millimeterLayoutY, null);
    }

    /**
     * 楕円を塗りつぶして描画する。
     *
     * @param millimeterWidth 幅(mm)
     * @param millimeterHeight 高さ(mm)
     * @param millimeterLayoutX 左位置(mm)
     * @param millimeterLayoutY 上位置(mm)
     * @param style インラインCSSスタイル(javafx.scene.shape.Ellipseクラスに使えるプロパティ)
     */
    public void printEllipseFill(double millimeterWidth, double millimeterHeight, double millimeterLayoutX, double millimeterLayoutY, String style) {
        double pointHalfWidth = millimeterToPoint(millimeterWidth / 2);
        double pointHalfHeight = millimeterToPoint(millimeterHeight / 2);
        Ellipse ellipse = new Ellipse(pointHalfWidth, pointHalfHeight);
        ellipse.setFill(this.color);
        ellipse.setLayoutX(millimeterToPoint(millimeterLayoutX) + pointHalfWidth);
        ellipse.setLayoutY(millimeterToPoint(millimeterLayoutY) + pointHalfHeight);
        ellipse.setStrokeWidth(this.strokeWidth);
        ellipse.getStrokeDashArray().addAll(this.strokeDashArray);
        ellipse.setStyle(style);
        this.paneCanvas.getChildren().add(ellipse);
    }

    /**
     * 楕円を塗りつぶして描画する。
     *
     * @param millimeterWidth 幅(mm)
     * @param millimeterHeight 高さ(mm)
     * @param millimeterLayoutX 左位置(mm)
     * @param millimeterLayoutY 上位置(mm)
     */
    public void printEllipseFill(double millimeterWidth, double millimeterHeight, double millimeterLayoutX, double millimeterLayoutY) {
        this.printEllipseFill(millimeterWidth, millimeterHeight, millimeterLayoutX, millimeterLayoutY, null);
    }

    /**
     * 線を描画する。
     *
     * @param millimeterStartX 始点X位置(mm)
     * @param millimeterStartY 始点Y位置(mm)
     * @param millimeterEndX 終点X位置(mm)
     * @param millimeterEndY 終点Y位置(mm)
     */
    public void printLine(double millimeterStartX, double millimeterStartY, double millimeterEndX, double millimeterEndY) {
        Line line = new Line();
        line.setStartX(millimeterToPoint(millimeterStartX));
        line.setStartY(millimeterToPoint(millimeterStartY));
        line.setEndX(millimeterToPoint(millimeterEndX));
        line.setEndY(millimeterToPoint(millimeterEndY));
        line.setStroke(this.color);
        line.setStrokeWidth(this.strokeWidth);
        line.getStrokeDashArray().addAll(this.strokeDashArray);
        this.paneCanvas.getChildren().add(line);
    }

    /**
     * 横線を描画する。
     *
     * @param millimeterLayoutX 左位置(mm)
     * @param millimeterLayoutY 上位置(mm)
     * @param millimeterWidth 幅(mm)
     */
    public void printHorizontalLine(double millimeterLayoutX, double millimeterLayoutY, double millimeterWidth) {
        this.printLine(millimeterLayoutX, millimeterLayoutY, millimeterLayoutX + millimeterWidth, millimeterLayoutY);
    }

    /**
     * 縦線を描画する。
     *
     * @param millimeterLayoutX 左位置(mm)
     * @param millimeterLayoutY 上位置(mm)
     * @param millimeterHeight 高さ(mm)
     */
    public void printVerticalLine(double millimeterLayoutX, double millimeterLayoutY, double millimeterHeight) {
        this.printLine(millimeterLayoutX, millimeterLayoutY, millimeterLayoutX, millimeterLayoutY + millimeterHeight);
    }

    /**
     * 画像を印刷する。
     *
     * @param image
     * @param millimeterWidth 幅(mm)
     * @param millimeterHeight 高さ(mm)
     * @param millimeterLayoutX 左位置(mm)
     * @param millimeterLayoutY 上位置(mm)
     */
    public void printImage(Image image, double millimeterWidth, double millimeterHeight, double millimeterLayoutX, double millimeterLayoutY) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(millimeterToPoint(millimeterWidth));
        imageView.setFitHeight(millimeterToPoint(millimeterHeight));
        imageView.setLayoutX(millimeterToPoint(millimeterLayoutX));
        imageView.setLayoutY(millimeterToPoint(millimeterLayoutY));
        this.paneCanvas.getChildren().add(imageView);
    }

    /**
     * 画像を横幅に合わせて印刷する。
     *
     * @param image
     * @param millimeterWidth 幅(mm)
     * @param millimeterLayoutX 左位置(mm)
     * @param millimeterLayoutY 上位置(mm)
     */
    public void printImageWithFitWidth(Image image, double millimeterWidth, double millimeterLayoutX, double millimeterLayoutY) {
        double rate = image.getWidth() / millimeterWidth;
        this.printImage(image, millimeterWidth, image.getHeight() / rate, millimeterLayoutX, millimeterLayoutY);
    }

    /**
     * 画像を高さに合わせて印刷する。
     *
     * @param image
     * @param millimeterHeight 高さ(mm)
     * @param millimeterLayoutX 左位置(mm)
     * @param millimeterLayoutY 上位置(mm)
     */
    public void printImageWithFitHeight(Image image, double millimeterHeight, double millimeterLayoutX, double millimeterLayoutY) {
        double rate = image.getHeight() / millimeterHeight;
        this.printImage(image, image.getWidth() / rate, millimeterHeight, millimeterLayoutX, millimeterLayoutY);
    }

    /**
     * Canvasを描画する。
     *
     * @param millimeterWidth 幅(mm)
     * @param millimeterHeight 高さ(mm)
     * @param millimeterLayoutX 左位置(mm)
     * @param millimeterLayoutY 上位置(mm)
     * @param scale 画質(1.0が等倍)
     * @param canvasCallback 実際の描画処理コールバック
     */
    public void printCanvas(double millimeterWidth, double millimeterHeight, double millimeterLayoutX, double millimeterLayoutY, double scale, CanvasCallback canvasCallback) {
        Canvas canvas = new Canvas(millimeterToPoint(millimeterWidth * scale), millimeterToPoint(millimeterHeight * scale));
        canvas.setLayoutX(millimeterToPoint(millimeterLayoutX));
        canvas.setLayoutY(millimeterToPoint(millimeterLayoutY));
        canvasCallback.call(canvas);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        Image image = canvas.snapshot(params, null);
        this.printImage(image, millimeterWidth, millimeterHeight, millimeterLayoutX, millimeterLayoutY);
    }

    /**
     * NW7バーコードを描画する。
     *
     * @param barcode 描画する情報
     * @param millimeterWidth 幅(mm)
     * @param millimeterHeight 高さ(mm)
     * @param millimeterLayoutX 左位置(mm)
     * @param millimeterLayoutY 上位置(mm)
     * @param scale 画質(1.0が等倍)
     */
    public void printNW7(String barcode, double millimeterWidth, double millimeterHeight, double millimeterLayoutX, double millimeterLayoutY, double scale) {
        this.printCanvas(millimeterWidth, millimeterHeight, millimeterLayoutX, millimeterLayoutY, scale, new CanvasCallback() {
            @Override
            public void call(Canvas canvas) {
                NW7Helper.drawBarcode(barcode, millimeterToPoint(millimeterWidth * scale), millimeterToPoint(millimeterHeight * scale), 0, 0, canvas.getGraphicsContext2D());
            }
        });
    }

    /**
     * JAN13バーコードを描画する。
     *
     * @param barcode 描画する情報
     * @param millimeterWidth 幅(mm)
     * @param millimeterHeight 高さ(mm)
     * @param millimeterLayoutX 左位置(mm)
     * @param millimeterLayoutY 上位置(mm)
     * @param scale 画質(1.0が等倍)
     */
    public void printJAN13(String barcode, double millimeterWidth, double millimeterHeight, double millimeterLayoutX, double millimeterLayoutY, double scale) {
        this.printCanvas(millimeterWidth, millimeterHeight, millimeterLayoutX, millimeterLayoutY, scale, new CanvasCallback() {
            @Override
            public void call(Canvas canvas) {
                JAN13Helper.drawBarcode(barcode, millimeterToPoint(millimeterWidth * scale), millimeterToPoint(millimeterHeight * scale), 0, 0, canvas.getGraphicsContext2D());
            }
        });
    }

    /**
     * Canvasを作成した際の実際の描画処理を記述するコールバック。
     *
     * @author hiro
     */
    public static interface CanvasCallback {

        /**
         * Canvasに対して図形を描画するコールバック関数。
         *
         * @param canvas
         */
        public void call(Canvas canvas);

    }
}
