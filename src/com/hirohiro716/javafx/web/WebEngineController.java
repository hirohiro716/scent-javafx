package com.hirohiro716.javafx.web;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLFrameElement;

import javafx.scene.web.WebEngine;

import static com.hirohiro716.StringConverter.*;

/**
 * WebEngineの操作を補助するクラス.
 * @author hiro
 */
public class WebEngineController {

    /**
     * コンストラクタ.
     * @param webEngine
     */
    public WebEngineController(WebEngine webEngine) {
        this.webEngine = webEngine;
    }

    private WebEngine webEngine;
    
    /**
     * WebEngineインスタンスを取得する.
     * @return WebEngine
     */
    public WebEngine getWebEngine() {
        return this.webEngine;
    }
    
    private ArrayList<Element> selectedElementsList = new ArrayList<>();
    
    /**
     * 指定のElementを選択状態にする.
     * @param element
     */
    public void addSelectedElement(Element element) {
        if (element instanceof HTMLFrameElement) {
            HTMLFrameElement frame = (HTMLFrameElement) element;
            this.selectedElementsList.add(frame.getContentDocument().getDocumentElement());
        }
        this.selectedElementsList.add(element);
    }
    
    /**
     * Elementsの選択状態をクリアする.
     */
    public void clearSelectedElements() {
        this.selectedElementsList.clear();
    }
    
    /**
     * Elementが選択されているかを確認する.
     * @return boolean
     */
    public boolean isSelectedElement() {
        return this.selectedElementsList.size() > 0;
    }
    
    /**
     * 選択済みのElementsを取得する.
     * @return Element[]
     */
    public Element[] getSelectedElements() {
        if (this.isSelectedElement()) {
            return this.selectedElementsList.toArray(new Element[] {});
        }
        return new Element[] {this.webEngine.getDocument().getDocumentElement()};
    }
    
    /**
     * 選択済みのElementを取得する.
     * @return Element
     */
    public Element getSelectedElement() {
        if (this.isSelectedElement() == false) {
            return null;
        }
        return this.getSelectedElements()[0];
    }
    
    /**
     * ElementをID属性を元に検索して選択する. すでに選択済みのElementがある場合はその内部から検索する.
     * @param id
     */
    public void selectElementById(String id) {
        Element[] elements = this.getSelectedElements();
        this.clearSelectedElements();
        for (Element element: elements) {
            if (element instanceof Document) {
                Document document = (Document) element;
                Element findedElement = document.getElementById(id);
                if (findedElement != null) {
                    this.addSelectedElement(findedElement);
                }
            }
        }
    }
    
    /**
     * Elementをタグ名を元に検索して選択する. すでに選択済みのElementがある場合はその内部から検索する.
     * @param tagName
     */
    public void selectElementsByTagName(String tagName) {
        Element[] elements = this.getSelectedElements();
        this.clearSelectedElements();
        for (Element element: elements) {
            NodeList nodeList = element.getElementsByTagName(tagName);
            for (int i = 0; i < nodeList.getLength(); i++) {
                this.addSelectedElement((Element) nodeList.item(i));
            }
        }
    }
    
    /**
     * Elementのタグ名とテキストを元に正規表現で検索して選択する. すでに選択済みのElementがある場合はその内部から検索する.
     * @param tagName
     * @param textCompareRegex テキストと比較する正規表現
     */
    public void selectElementsByTagName(String tagName, String textCompareRegex) {
        Element[] elements = this.getSelectedElements();
        this.clearSelectedElements();
        for (Element element: elements) {
            NodeList nodeList = element.getElementsByTagName(tagName);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element findedElement = (Element) nodeList.item(i);
                String elementText = findedElement.getTextContent();
                if (elementText != null && elementText.matches(textCompareRegex)) {
                    this.addSelectedElement(findedElement);
                }
            }
        }
    }
    
    /**
     * Elementを属性値を元に正規表現で検索して選択する. すでに選択済みのElementがある場合はその内部から検索する.
     * @param attributeName 属性名
     * @param valueCompareRegex 属性値と比較する正規表現
     */
    public void selectElementsByAttribute(String attributeName, String valueCompareRegex) {
        Element[] elements = this.getSelectedElements();
        this.clearSelectedElements();
        for (Element element: elements) {
            for (Node node: this.getChildNodeList(element)) {
                if (node instanceof Element) {
                    Element findedElement = (Element) node;
                    String value = findedElement.getAttribute(attributeName);
                    if (value != null && value.matches(valueCompareRegex)) {
                        System.out.println(node.getNodeName());
                        this.addSelectedElement(findedElement);
                    }
                }
            }
        }
    }
    
    /**
     * 再帰的に子要素を取得する.
     * @param targetNode
     * @return ArrayList<Node>
     */
    private ArrayList<Node> getChildNodeList(Node targetNode) {
        ArrayList<Node> result = new ArrayList<>();
        NodeList nodeList = targetNode.getChildNodes();
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node temporaryNode = nodeList.item(i);
                result.add(temporaryNode);
                ArrayList<Node> temporaryNodes = this.getChildNodeList(temporaryNode);
                for (Node node: temporaryNodes) {
                    result.add(node);
                }
            }
        }
        return result;
    }
    
    /**
     * JavaScriptを利用して選択されている最初のElementをクリックする.
     */
    public void click() {
        if (this.isSelectedElement()) {
            clickElement(this.webEngine, this.selectedElementsList.get(0));
        }
    }
    
    private final static String TARGET_CLASS_NAME = "com-hirohiro716-javafx-web-webenginecontroller-target";
    
    /**
     * JavaScriptを利用してElementをクリックする.
     * @param webEngine
     * @param element
     */
    public static void clickElement(WebEngine webEngine, Element element) {
        if (element == null) {
            return;
        }
        String originalClass = nullReplace(element.getAttribute("class"), "");
        element.setAttribute("class", join(originalClass, " ", TARGET_CLASS_NAME));
        webEngine.executeScript(join("for (var i = 0; i < frames.length; i++) { var elements = frames[i].document.getElementsByClassName('", TARGET_CLASS_NAME, "'); if (elements.length > 0) { elements[0].click(); } }"));
        element.setAttribute("class", originalClass);
    }
    
}
