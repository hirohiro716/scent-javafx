package com.hirohiro716.javafx.barcode;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

/**
 * javafxでQRコードを生成するクラス。
 *
 * 
 * @author hiro
 *
 */
public class QRCodeHelper extends com.hirohiro716.awt.QRCodeHelper {
    
    /**
     * QRコードのjavafx.scene.image.Imageオブジェクトを作成する。
     *
     * @param contents 内容
     * @param width 幅ピクセル
     * @param height 高さピクセル
     * @return Image
     * @throws Exception
     */
    public static Image createImage(String contents, int width, int height) throws Exception {
        return SwingFXUtils.toFXImage(createBufferedImage(contents, width, height), null);
    }}
