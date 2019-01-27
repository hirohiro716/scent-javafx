package com.hirohiro716.javafx.web;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLFrameElement;

import com.hirohiro716.StringConverter;
import javafx.scene.web.WebEngine;

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
        return this.selectedElementsList.toArray(new Element[] {});
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
     * 選択済みのElementsがあればそれを なければRootDocumentのElementを取得する.
     * @return Element[]
     */
    private Element[] getSelectedElementOrRoot() {
        if (this.isSelectedElement()) {
            return this.getSelectedElements();
        }
        return new Element[] {this.webEngine.getDocument().getDocumentElement()};
    }
    
    /**
     * ElementをID属性を元に検索して選択する. すでに選択済みのElementがある場合はその内部から検索する.
     * @param idCompareRegex IDと比較する正規表現
     */
    public void selectElementById(String idCompareRegex) {
        this.selectElementsByAttribute("id", idCompareRegex);
    }
    
    /**
     * Elementをタグ名を元に検索して選択する. すでに選択済みのElementがある場合はその内部から検索する.
     * @param tagName
     */
    public void selectElementsByTagName(String tagName) {
        Element[] elements = this.getSelectedElementOrRoot();
        this.clearSelectedElements();
        for (Element element: elements) {
            NodeList nodeList = element.getElementsByTagName(tagName);
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i) instanceof Element) {
                    this.addSelectedElement((Element) nodeList.item(i));
                }
            }
        }
    }
    
    /**
     * Elementのタグ名とテキストを元に正規表現で検索して選択する. すでに選択済みのElementがある場合はその内部から検索する.
     * @param tagName
     * @param textCompareRegex テキストと比較する正規表現
     */
    public void selectElementsByTagName(String tagName, String textCompareRegex) {
        Element[] elements = this.getSelectedElementOrRoot();
        this.clearSelectedElements();
        for (Element element: elements) {
            NodeList nodeList = element.getElementsByTagName(tagName);
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i) instanceof Element) {
                    Element findedElement = (Element) nodeList.item(i);
                    String elementText = findedElement.getTextContent();
                    Pattern pattern = Pattern.compile(textCompareRegex, Pattern.DOTALL);
                    if (elementText != null && pattern.matcher(elementText).find()) {
                        this.addSelectedElement(findedElement);
                    }
                }
            }
        }
    }

    /**
     * Elementをタグ名と属性値を元に正規表現で検索して選択する. すでに選択済みのElementがある場合はその内部から検索する.
     * @param tagName
     * @param valueCompareRegex 属性値と比較する正規表現
     */
    public void selectElementsByTagNameAndAttributeValue(String tagName, String valueCompareRegex) {
        Element[] elements = this.getSelectedElementOrRoot();
        this.clearSelectedElements();
        for (Element element: elements) {
            NodeList nodeList = element.getElementsByTagName(tagName);
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i) instanceof Element) {
                    Element findedElement = (Element) nodeList.item(i);
                    for (int attributeIndex = 0; attributeIndex < findedElement.getAttributes().getLength(); attributeIndex++) {
                        String value = findedElement.getAttributes().item(attributeIndex).getNodeValue();
                        Pattern pattern = Pattern.compile(valueCompareRegex, Pattern.DOTALL);
                        if (value != null && pattern.matcher(value).find()) {
                            this.addSelectedElement(findedElement);
                        }
                    }
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
        Element[] elements = this.getSelectedElementOrRoot();
        this.clearSelectedElements();
        for (Element element: elements) {
            for (Node node: this.getChildNodeList(element)) {
                if (node instanceof Element) {
                    Element findedElement = (Element) node;
                    String value = findedElement.getAttribute(attributeName);
                    Pattern pattern = Pattern.compile(valueCompareRegex, Pattern.DOTALL);
                    if (value != null && pattern.matcher(value).find()) {
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
     * すべての子要素を取得する.
     * @return Element[]
     */
    public Element[] getAllChildElements() {
        ArrayList<Element> result = new ArrayList<>();
        ArrayList<Node> nodeList = this.getChildNodeList(this.webEngine.getDocument().getDocumentElement());
        for (Node node: nodeList) {
            if (node instanceof Element) {
                result.add((Element) node);
            }
        }
        return result.toArray(new Element[] {});
    }
    
    /**
     * JavaScriptを利用して選択されている最初のElementをクリックする.
     */
    public void click() {
        if (this.isSelectedElement()) {
            clickElement(this.webEngine, this.selectedElementsList.get(0));
        }
    }
    
    /**
     * JavaScriptを利用してElementをクリックする.
     * @param webEngine WebEngineオブジェクト
     * @param element クリック対象
     */
    public static void clickElement(WebEngine webEngine, Element element) {
        if (element == null) {
            return;
        }
        String backupClass = element.getAttribute("class");
        String flagClass = WebEngineController.class.getName().replaceAll("\\.", "_").toUpperCase() + "_FLAG_FOR_CLICK";
        String newClass = StringConverter.join(backupClass, " ", flagClass);
        element.setAttribute("class", newClass);
        StringBuilder script = new StringBuilder("document.getElementsByClassName('");
        script.append(flagClass);
        script.append("')[0].click();");
        webEngine.executeScript(script.toString());
        element.setAttribute("class", backupClass);
    }
    
}
