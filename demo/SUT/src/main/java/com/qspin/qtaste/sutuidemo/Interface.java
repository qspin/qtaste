package com.qspin.qtaste.sutuidemo;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

final class Interface extends JFrame {

    public Interface() {
        super("SUT GUI Demonstration controlled by QTaste");
        setName("MAIN_FRAME");

        genUI();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void genUI() {
        setLayout(new BorderLayout());
        int index = 0;
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setName("TABBED_PANE");
        tabbedPane.setToolTipText("ToolTip from the JTabbedPane");
        System.out.println("insert " + DocumentPanel.COMPONENT_NAME + " at " + index);
        tabbedPane.insertTab(DocumentPanel.COMPONENT_NAME, null, new DocumentPanel(), null, index++);
        System.out.println("insert " + ChoosePanel.COMPONENT_NAME + " at " + index);
        tabbedPane.insertTab(ChoosePanel.COMPONENT_NAME, null, new ChoosePanel(), null, index++);
        System.out.println("insert " + SelectionPanel.COMPONENT_NAME + " at " + index);
        tabbedPane.insertTab(SelectionPanel.COMPONENT_NAME, null, new SelectionPanel(), null, index++);
        System.out.println("insert " + Tree_ListComponentsPanel.COMPONENT_NAME + " at " + index);
        tabbedPane.insertTab(Tree_ListComponentsPanel.COMPONENT_NAME, null, new Tree_ListComponentsPanel(), null, index++);
        System.out.println("insert " + TablePanel.COMPONENT_NAME + " at " + index);
        tabbedPane.insertTab(TablePanel.COMPONENT_NAME, null, new TablePanel(), null, index++);
        System.out.println("insert UNAMED COMPONENTS at " + index);
        tabbedPane.insertTab("UNAMED COMPONENTS", null, new UnamedPanel(), null, index++);
        System.out.println("insert " + DialogPanel.COMPONENT_NAME + " at " + index);
        tabbedPane.insertTab(DialogPanel.COMPONENT_NAME, null, new DialogPanel(), null, index++);
        System.out.println("insert " + MiscellaneousPane.COMPONENT_NAME + " at " + index);
        tabbedPane.insertTab(MiscellaneousPane.COMPONENT_NAME, null, new MiscellaneousPane(), null, index++);
        System.out.println("insert " + JavaComplexComponent.COMPONENT_NAME + " at " + index);
        tabbedPane.insertTab(JavaComplexComponent.COMPONENT_NAME, null, new JavaComplexComponent(), null, index++);
        tabbedPane.setSelectedIndex(-1);

        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("Starting SUT GUI");
            new Interface();
            System.out.println("SUT GUI started");
        });
    }

}
