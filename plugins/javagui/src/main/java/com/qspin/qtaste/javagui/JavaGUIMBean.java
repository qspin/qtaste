/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qspin.qtaste.javagui;

/**
 *
 * @author lvboque
 */
public interface JavaGUIMBean {
   public void takeSnapShot(String componentName, String fileName);
   public boolean keyPressedOnComponent(String componentName, int vkEvent);
   public String [] listComponents();
   public boolean isEnabled(String componentName);
   public boolean clickOnButton(String componentName);
   public boolean clickOnButton(String componentName, int pressTime);
   public String getText(String componentName);
   public boolean setText(String componentName, String value);
   public boolean selectComponant(String componentName, boolean value);
   public boolean selectValue(String componentName, String value);
   public boolean selectIndex(String componentName, int index);
   public boolean selectNode(String componentName, String nodeName, String nodeSeparator);
}
