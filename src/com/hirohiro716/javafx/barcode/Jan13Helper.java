package com.hirohiro716.javafx.barcode;

import static com.hirohiro716.StringConverter.stringToInteger;

import javafx.scene.canvas.GraphicsContext;

/**
 * JavaFXでバーコードを生成するクラス.
 * @author hiro
 */
public class Jan13Helper extends com.hirohiro716.barcode.JAN13Helper {

    /**
     * JAN13バーコードを描画します.
     * @param barcode バーコード
     * @param width 描画幅
     * @param height 描画高さ
     * @param x 描画位置x
     * @param y 描画位置y
     * @param graphicsContext GraphicsContextインスタンス
     */
    public static void drawBarcode(String barcode, double width, double height, double x, double y, GraphicsContext graphicsContext) {
        // モジュール単位を算出
        double moduleOne = width / 115d;
        if (moduleOne <= 0) {
            return;
        }
        // バーコードの整合性をチェック
        if (isValid(barcode) == false) {
            return;
        }
        // 描画位置を設定
        double drawingX = x;
        // ホワイトスペース
        drawingX += (moduleOne * 10);
        // スタートコード
        graphicsContext.fillRect(drawingX, y, moduleOne, height);
        drawingX += (moduleOne * 2);
        graphicsContext.fillRect(drawingX, y, moduleOne, height);
        drawingX += moduleOne;
        // バーコード最初の1文字を取得する
        int firstChar = stringToInteger(barcode.substring(0, 1));
        // 左側を描画
        int[] leftParityType = LEFT_PARITIES_TYPES[firstChar];
        for (int charNumber = 1; charNumber <= 6; charNumber++) {
            int type = leftParityType[charNumber - 1];
            int printChar = stringToInteger(barcode.substring(charNumber, charNumber + 1));
            int[] printParity = LEFT_PARITIES[type][printChar];
            for (int p : printParity) {
                if (p == 1) {
                    graphicsContext.fillRect(drawingX, y, moduleOne, height);
                }
                drawingX += moduleOne;
            }
        }
        // センターコード
        drawingX += moduleOne;
        graphicsContext.fillRect(drawingX, y, moduleOne, height);
        drawingX += (moduleOne * 2);
        graphicsContext.fillRect(drawingX, y, moduleOne, height);
        drawingX += (moduleOne * 2);
        // 右側を描画
        for (int charNumber = 7; charNumber <= 12; charNumber++) {
            int printChar = stringToInteger(barcode.substring(charNumber, charNumber + 1));
            int[] printParity = RIGHT_PARITIES[printChar];
            for (int p : printParity) {
                if (p == 1) {
                    graphicsContext.fillRect(drawingX, y, moduleOne, height);
                }
                drawingX += moduleOne;
            }
        }
        // ストップコード
        graphicsContext.fillRect(drawingX, y, moduleOne, height);
        drawingX += (moduleOne * 2);
        graphicsContext.fillRect(drawingX, y, moduleOne, height);
    }

}
