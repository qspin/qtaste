package com.qspin.qtaste.sutuidemo;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public final class DialogPanel extends JPanel {

    DialogPanel() {
        super();
        setName(COMPONENT_NAME);

        genUI();
    }

    private void genUI() {
        prepareComponent();
        FormLayout layout = new FormLayout("3dlu:grow, pref, 3dlu:grow", "3dlu:grow, pref, 3dlu:grow");
        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();

        builder.add(mStart, cc.xy(2, 2));
        setLayout(new BorderLayout());
        add(builder.getPanel(), BorderLayout.CENTER);
    }

    private void prepareComponent() {
        mStart = new JButton("Start");
        mStart.setName("START_BUTTON");
        mStart.addActionListener(new StartDialogProcess());
    }

    JButton mStart;

    private static final int NUMBER_OF_COMPONENT = 1;
    public static final String COMPONENT_NAME = "DIALOG_PANEL";

    private static class StartDialogProcess implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String result = JOptionPane.showInputDialog(null, "How many popup do you want to display", "Number of popup?",
                  JOptionPane.QUESTION_MESSAGE);
            if (result != null) {
                int numberOfPopup = 0;
                try {
                    numberOfPopup = Integer.parseInt(result);
                } catch (NumberFormatException ex) {
                    return;
                }

                int sureAnswer = JOptionPane.showConfirmDialog(null,
                      "Are you sure you want to display " + numberOfPopup + " popup(s)?", "Are you sure?",
                      JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (sureAnswer == JOptionPane.YES_OPTION) {
                    for (int i = 0; i < numberOfPopup; i++) {
                        final String msg = "Popup number " + (i + 1);
                        Thread t = new Thread(new Runnable() {

                            public void run() {
                                JOptionPane.showMessageDialog(null, msg, "POPUP", JOptionPane.INFORMATION_MESSAGE);
                            }
                        });
                        t.start();
                    }
                }
            }
        }
    }
}
