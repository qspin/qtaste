/*
    Copyright 2007-2009 QSpin - www.qspin.be

    This file is part of QTaste framework.

    QTaste is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    QTaste is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with QTaste. If not, see <http://www.gnu.org/licenses/>.
*/

package com.qspin.qtaste.tcom;

import com.thoughtworks.selenium.Selenium;

/**
 * WebBrowser is just a Selenium Wrapper
 * @author lvboque
 */
public class WebBrowser implements Selenium {

    protected Selenium selenium;

    protected void setSelenium(Selenium instance) {
        this.selenium = instance;
    }

    @Override
    public void setExtensionJs(String extensionJs) {
        selenium.setExtensionJs(extensionJs);
    }

    @Override
    public void start() {
        selenium.start();
    }

    @Override
    public void start(String optionsString) {
        selenium.start(optionsString);
    }

    @Override
    public void start(Object optionsObject) {
        selenium.start(optionsObject);
    }

    @Override
    public void stop() {
        selenium.stop();
    }

    @Override
    public void showContextualBanner() {
        selenium.showContextualBanner();
    }

    @Override
    public void showContextualBanner(String className, String methodName) {
        selenium.showContextualBanner(className, methodName);
    }

    @Override
    public void click(String locator) {
        selenium.click(locator);
    }

    @Override
    public void doubleClick(String locator) {
        selenium.doubleClick(locator);

    }

    @Override
    public void contextMenu(String locator) {
        selenium.contextMenu(locator);

    }

    @Override
    public void clickAt(String locator, String coordString) {
        selenium.clickAt(locator, coordString);

    }

    @Override
    public void doubleClickAt(String locator, String coordString) {
        selenium.doubleClickAt(locator, coordString);

    }

    @Override
    public void contextMenuAt(String locator, String coordString) {
        selenium.contextMenuAt(locator, coordString);

    }

    @Override
    public void fireEvent(String locator, String eventName) {
        selenium.fireEvent(locator, eventName);
    }

    @Override
    public void focus(String locator) {
        selenium.focus(locator);

    }

    @Override
    public void keyPress(String locator, String keySequence) {
        selenium.keyPress(locator, keySequence);

    }

    @Override
    public void shiftKeyDown() {
        selenium.shiftKeyDown();

    }

    @Override
    public void shiftKeyUp() {
        selenium.shiftKeyUp();

    }

    @Override
    public void metaKeyDown() {
        selenium.metaKeyDown();

    }

    @Override
    public void metaKeyUp() {
        selenium.metaKeyUp();

    }

    @Override
    public void altKeyDown() {
        selenium.altKeyDown();

    }

    @Override
    public void altKeyUp() {
        selenium.altKeyUp();

    }

    @Override
    public void controlKeyDown() {
        selenium.controlKeyDown();

    }

    @Override
    public void controlKeyUp() {
        selenium.controlKeyUp();

    }

    @Override
    public void keyDown(String locator, String keySequence) {
        selenium.keyDown(locator, keySequence);

    }

    @Override
    public void keyUp(String locator, String keySequence) {
        selenium.keyUp(locator, keySequence);

    }

    @Override
    public void mouseOver(String locator) {
        selenium.mouseOver(locator);

    }

    @Override
    public void mouseOut(String locator) {
        selenium.mouseOut(locator);

    }

    @Override
    public void mouseDown(String locator) {
        selenium.mouseDown(locator);

    }

    @Override
    public void mouseDownRight(String locator) {
        selenium.mouseDownRight(locator);

    }

    @Override
    public void mouseDownAt(String locator, String coordString) {
        selenium.mouseDownAt(locator, coordString);

    }

    @Override
    public void mouseDownRightAt(String locator, String coordString) {
        selenium.mouseDownRightAt(locator, coordString);

    }

    @Override
    public void mouseUp(String locator) {
        selenium.mouseUp(locator);

    }

    @Override
    public void mouseUpRight(String locator) {
        selenium.mouseUpRight(locator);

    }

    @Override
    public void mouseUpAt(String locator, String coordString) {
        selenium.mouseUpAt(locator, coordString);

    }

    @Override
    public void mouseUpRightAt(String locator, String coordString) {
        selenium.mouseUpRightAt(locator, coordString);

    }

    @Override
    public void mouseMove(String locator) {
        selenium.mouseMove(locator);

    }

    @Override
    public void mouseMoveAt(String locator, String coordString) {
        selenium.mouseMoveAt(locator, coordString);

    }

    @Override
    public void type(String locator, String value) {
        selenium.type(locator, value);

    }

    @Override
    public void typeKeys(String locator, String value) {
        selenium.typeKeys(locator, value);

    }

    @Override
    public void setSpeed(String value) {
        selenium.setSpeed(value);

    }

    @Override
    public String getSpeed() {
        return selenium.getSpeed();

    }

    @Override
    public void check(String locator) {
        selenium.check(locator);

    }

    @Override
    public void uncheck(String locator) {
        selenium.uncheck(locator);

    }

    @Override
    public void select(String selectLocator, String optionLocator) {
        selenium.select(selectLocator, optionLocator);

    }

    @Override
    public void addSelection(String locator, String optionLocator) {
        selenium.addSelection(locator, optionLocator);

    }

    @Override
    public void removeSelection(String locator, String optionLocator) {
        selenium.removeSelection(locator, optionLocator);

    }

    @Override
    public void removeAllSelections(String locator) {
        selenium.removeAllSelections(locator);

    }

    @Override
    public void submit(String formLocator) {
        selenium.submit(formLocator);

    }

    @Override
    public void open(String url) {
        selenium.open(url);

    }

    @Override
    public void openWindow(String url, String windowID) {
        selenium.openWindow(url, windowID);

    }

    @Override
    public void selectWindow(String windowID) {
        selenium.selectWindow(windowID);

    }

    @Override
    public void selectPopUp(String windowID) {
        selenium.selectPopUp(windowID);

    }

    @Override
    public void deselectPopUp() {
        selenium.deselectPopUp();

    }

    @Override
    public void selectFrame(String locator) {
        selenium.selectFrame(locator);

    }

    @Override
    public boolean getWhetherThisFrameMatchFrameExpression(String currentFrameString, String target) {
        return selenium.getWhetherThisFrameMatchFrameExpression(currentFrameString, target);

    }

    @Override
    public boolean getWhetherThisWindowMatchWindowExpression(String currentWindowString, String target) {
        return selenium.getWhetherThisWindowMatchWindowExpression(currentWindowString, target);

    }

    @Override
    public void waitForPopUp(String windowID, String timeout) {
        selenium.waitForPopUp(windowID, timeout);

    }

    @Override
    public void chooseCancelOnNextConfirmation() {
        selenium.chooseCancelOnNextConfirmation();

    }

    @Override
    public void chooseOkOnNextConfirmation() {
        selenium.chooseOkOnNextConfirmation();

    }

    @Override
    public void answerOnNextPrompt(String answer) {
        selenium.answerOnNextPrompt(answer);

    }

    @Override
    public void goBack() {
        selenium.goBack();

    }

    @Override
    public void refresh() {
        selenium.refresh();

    }

    @Override
    public void close() {
        selenium.close();

    }

    @Override
    public boolean isAlertPresent() {
        return selenium.isAlertPresent();

    }

    @Override
    public boolean isPromptPresent() {
        return selenium.isPromptPresent();

    }

    @Override
    public boolean isConfirmationPresent() {
        return selenium.isConfirmationPresent();

    }

    @Override
    public String getAlert() {
        return selenium.getAlert();

    }

    @Override
    public String getConfirmation() {
        return selenium.getConfirmation();

    }

    @Override
    public String getPrompt() {
        return selenium.getPrompt();

    }

    @Override
    public String getLocation() {
        return selenium.getLocation();

    }

    @Override
    public String getTitle() {
        return selenium.getTitle();

    }

    @Override
    public String getBodyText() {
        return selenium.getBodyText();

    }

    @Override
    public String getValue(String locator) {
        return selenium.getValue(locator);

    }

    @Override
    public String getText(String locator) {
        return selenium.getText(locator);

    }

    @Override
    public void highlight(String locator) {
        selenium.highlight(locator);

    }

    @Override
    public String getEval(String script) {
        return selenium.getEval(script);

    }

    @Override
    public boolean isChecked(String locator) {
        return selenium.isChecked(locator);

    }

    @Override
    public String getTable(String tableCellAddress) {
        return selenium.getTable(tableCellAddress);

    }

    @Override
    public String[] getSelectedLabels(String selectLocator) {
        return selenium.getSelectedLabels(selectLocator);

    }

    @Override
    public String getSelectedLabel(String selectLocator) {
        return selenium.getSelectedLabel(selectLocator);

    }

    @Override
    public String[] getSelectedValues(String selectLocator) {
        return selenium.getSelectedValues(selectLocator);

    }

    @Override
    public String getSelectedValue(String selectLocator) {
        return selenium.getSelectedValue(selectLocator);

    }

    @Override
    public String[] getSelectedIndexes(String selectLocator) {
        return selenium.getSelectedIndexes(selectLocator);

    }

    @Override
    public String getSelectedIndex(String selectLocator) {
        return selenium.getSelectedIndex(selectLocator);

    }

    @Override
    public String[] getSelectedIds(String selectLocator) {
        return selenium.getSelectedIds(selectLocator);

    }

    @Override
    public String getSelectedId(String selectLocator) {
        return selenium.getSelectedId(selectLocator);

    }

    @Override
    public boolean isSomethingSelected(String selectLocator) {
        return selenium.isSomethingSelected(selectLocator);

    }

    @Override
    public String[] getSelectOptions(String selectLocator) {
        return selenium.getSelectOptions(selectLocator);

    }

    @Override
    public String getAttribute(String attributeLocator) {
        return selenium.getAttribute(attributeLocator);

    }

    @Override
    public boolean isTextPresent(String pattern) {
        return selenium.isTextPresent(pattern);

    }

    @Override
    public boolean isElementPresent(String locator) {
        return selenium.isElementPresent(locator);

    }

    @Override
    public boolean isVisible(String locator) {
        return selenium.isVisible(locator);

    }

    @Override
    public boolean isEditable(String locator) {
        return selenium.isEditable(locator);

    }

    @Override
    public String[] getAllButtons() {
        return selenium.getAllButtons();

    }

    @Override
    public String[] getAllLinks() {
        return selenium.getAllLinks();

    }

    @Override
    public String[] getAllFields() {
        return selenium.getAllFields();

    }

    @Override
    public String[] getAttributeFromAllWindows(String attributeName) {
        return selenium.getAttributeFromAllWindows(attributeName);

    }

    @Override
    public void dragdrop(String locator, String movementsString) {
        selenium.dragdrop(locator, movementsString);

    }

    @Override
    public void setMouseSpeed(String pixels) {
        selenium.setMouseSpeed(pixels);

    }

    @Override
    public Number getMouseSpeed() {
        return selenium.getMouseSpeed();

    }

    @Override
    public void dragAndDrop(String locator, String movementsString) {
        selenium.dragAndDrop(locator, movementsString);

    }

    @Override
    public void dragAndDropToObject(String locatorOfObjectToBeDragged, String locatorOfDragDestinationObject) {
        selenium.dragAndDropToObject(locatorOfObjectToBeDragged, locatorOfDragDestinationObject);

    }

    @Override
    public void windowFocus() {
        selenium.windowFocus();

    }

    @Override
    public void windowMaximize() {
        selenium.windowMaximize();

    }

    @Override
    public String[] getAllWindowIds() {
        return selenium.getAllWindowIds();

    }

    @Override
    public String[] getAllWindowNames() {
        return selenium.getAllWindowNames();

    }

    @Override
    public String[] getAllWindowTitles() {
        return selenium.getAllWindowTitles();

    }

    @Override
    public String getHtmlSource() {
        return selenium.getHtmlSource();

    }

    @Override
    public void setCursorPosition(String locator, String position) {
        selenium.setCursorPosition(locator, position);

    }

    @Override
    public Number getElementIndex(String locator) {
        return selenium.getElementIndex(locator);

    }

    @Override
    public boolean isOrdered(String locator1, String locator2) {
        return selenium.isOrdered(locator1, locator2);

    }

    @Override
    public Number getElementPositionLeft(String locator) {
        return selenium.getElementPositionLeft(locator);

    }

    @Override
    public Number getElementPositionTop(String locator) {
        return selenium.getElementPositionTop(locator);

    }

    @Override
    public Number getElementWidth(String locator) {
        return selenium.getElementWidth(locator);

    }

    @Override
    public Number getElementHeight(String locator) {
        return selenium.getElementHeight(locator);

    }

    @Override
    public Number getCursorPosition(String locator) {
        return selenium.getCursorPosition(locator);

    }

    @Override
    public String getExpression(String expression) {
        return selenium.getExpression(expression);

    }

    @Override
    public Number getXpathCount(String xpath) {
        return selenium.getXpathCount(xpath);

    }

    @Override
    public void assignId(String locator, String identifier) {
        selenium.assignId(locator, identifier);

    }

    @Override
    public void allowNativeXpath(String allow) {
        selenium.allowNativeXpath(allow);

    }

    @Override
    public void ignoreAttributesWithoutValue(String ignore) {
        selenium.ignoreAttributesWithoutValue(ignore);

    }

    @Override
    public void waitForCondition(String script, String timeout) {
        selenium.waitForCondition(script, timeout);

    }

    @Override
    public void setTimeout(String timeout) {
        selenium.setTimeout(timeout);

    }

    @Override
    public void waitForPageToLoad(String timeout) {
        selenium.waitForPageToLoad(timeout);

    }

    @Override
    public void waitForFrameToLoad(String frameAddress, String timeout) {
        selenium.waitForFrameToLoad(frameAddress, timeout);

    }

    @Override
    public String getCookie() {
        return selenium.getCookie();

    }

    @Override
    public String getCookieByName(String name) {
        return selenium.getCookieByName(name);

    }

    @Override
    public boolean isCookiePresent(String name) {
        return selenium.isCookiePresent(name);

    }

    @Override
    public void createCookie(String nameValuePair, String optionsString) {
        selenium.createCookie(nameValuePair, optionsString);

    }

    @Override
    public void deleteCookie(String name, String optionsString) {
        selenium.deleteCookie(name, optionsString);

    }

    @Override
    public void deleteAllVisibleCookies() {
        selenium.deleteAllVisibleCookies();

    }

    @Override
    public void setBrowserLogLevel(String logLevel) {
        selenium.setBrowserLogLevel(logLevel);

    }

    @Override
    public void runScript(String script) {
        selenium.runScript(script);

    }

    @Override
    public void addLocationStrategy(String strategyName, String functionDefinition) {
        selenium.addLocationStrategy(strategyName, functionDefinition);

    }

    @Override
    public void captureEntirePageScreenshot(String filename, String kwargs) {
        selenium.captureEntirePageScreenshot(filename, kwargs);

    }

    @Override
    public void rollup(String rollupName, String kwargs) {
        selenium.rollup(rollupName, kwargs);

    }

    @Override
    public void addScript(String scriptContent, String scriptTagId) {
        selenium.addScript(scriptContent, scriptTagId);

    }

    @Override
    public void removeScript(String scriptTagId) {
        selenium.removeScript(scriptTagId);

    }

    @Override
    public void useXpathLibrary(String libraryName) {
        selenium.useXpathLibrary(libraryName);

    }

    @Override
    public void setContext(String context) {
        selenium.setContext(context);

    }

    @Override
    public void attachFile(String fieldLocator, String fileLocator) {
        selenium.attachFile(fieldLocator, fileLocator);

    }

    @Override
    public void captureScreenshot(String filename) {
        selenium.captureScreenshot(filename);

    }

    @Override
    public String captureScreenshotToString() {
        return selenium.captureScreenshotToString();

    }

    @Override
    public String captureNetworkTraffic(String type) {
        return selenium.captureNetworkTraffic(type);

    }

    @Override
    public String captureEntirePageScreenshotToString(String kwargs) {
        return selenium.captureEntirePageScreenshotToString(kwargs);

    }

    @Override
    public void shutDownSeleniumServer() {
        selenium.shutDownSeleniumServer();

    }

    @Override
    public String retrieveLastRemoteControlLogs() {
        return selenium.retrieveLastRemoteControlLogs();

    }

    @Override
    public void keyDownNative(String keycode) {
        selenium.keyDownNative(keycode);

    }

    @Override
    public void keyUpNative(String keycode) {
        selenium.keyUpNative(keycode);

    }

    @Override
    public void keyPressNative(String keycode) {
        selenium.keyPressNative(keycode);

    }

    @Override
    public void addCustomRequestHeader(String s, String s2) {
        selenium.addCustomRequestHeader(s, s2);
    }
}
