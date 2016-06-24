package com.qspin.qtaste.sutfxdemo;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class Interface {

    @FXML
    private ComboBox<Person> m_combo;
    @FXML
    private Slider m_slider;
    @FXML
    private Spinner<Integer> m_spinner;
    @FXML
    private ListView<Person> m_list;

    public Interface() {
    }

    @FXML
    private void initialize() {
        m_combo.setItems(Person.DEFAULT_DATA);
        m_combo.setConverter(new StringConverter<Person>() {

            @Override
            public String toString(Person p) {
                return p.getFirstName().get() + " " + p.getLastName().get() + " (" + p.getAge().get() + ")";
            }

            @Override
            public Person fromString(String string) {
                for (Person p : m_combo.getItems()) {
                    String current = p.getFirstName().get() + " " + p.getLastName().get() + " (" + p.getAge().get() + ")";
                    if (current.equals(string)) {
                        return p;
                    }
                }
                return null;
            }
        });

        m_slider.setMin(0);
        m_slider.setMax(100);
        m_slider.setValue(50);

        m_spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0, 1));

        m_list.setItems(Person.DEFAULT_DATA);
        m_list.setCellFactory(new Callback<ListView<Person>, ListCell<Person>>() {

            @Override
            public ListCell<Person> call(ListView<Person> p) {

                ListCell<Person> cell = new ListCell<Person>() {

                    @Override
                    protected void updateItem(Person p, boolean bln) {
                        super.updateItem(p, bln);
                        if (p != null) {
                            String current = p.getFirstName().get() + " " + p.getLastName().get() + " (" + p.getAge().get() + ")";
                            setText(current);
                        }
                    }

                };
                return cell;
            }
        });
    }
}
