package com.qspin.qtaste.sutfxdemo;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class PersonComboBox {

    @FXML
    private ComboBox<Person> m_combo;

    private Main m_main;

    public PersonComboBox() {
    }

    public void setMainApp(Main m) {
        m_main = m;

        m_combo.setItems(Person.DEFAULT_DATA);
    }
}
