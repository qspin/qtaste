package com.qspin.qtaste.sutuidemo;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

final class ModelBuilder {

    public static ComboBoxModel<Person> getComboBoxModel() {
        DefaultComboBoxModel<Person> model = new DefaultComboBoxModel<>();
        getPeople().forEach(model::addElement);
        return model;
    }

    public static ListModel<Person> getListModel() {
        DefaultListModel<Person> model = new DefaultListModel<>();
        getPeople().forEach(model::addElement);
        return model;
    }

    public static TreeModel getTreeModel() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Person");
        DefaultMutableTreeNode old = new DefaultMutableTreeNode("Old");
        DefaultMutableTreeNode young = new DefaultMutableTreeNode("Young");
        root.add(old);
        root.add(young);

        for (Person p : getPeople()) {
            DefaultMutableTreeNode personNode = new DefaultMutableTreeNode(p);
            if (p.getAge() < 50) {
                young.add(personNode);
            } else {
                old.add(personNode);
            }
        }
        return new DefaultTreeModel(root);
    }

    public static TableModel getTableModel() {
        return new CustomTableModel(getPeople());
    }

    private static List<Person> getPeople() {
        List<Person> people = new ArrayList<>();
        people.add(new Person("Mickey", "Mouse", 70, "Castel of Disney land"));
        people.add(new Person("Tintin", "Milou", 40, "Moulinsart"));
        people.add(new Person("Louis", "XVII", 30, "Versaille"));
        people.add(new Person("Elisabeth", "II", 80, "London"));
        people.add(new Person("Milou", "Tintin", 40, "Moulinsart"));
        return people;
    }
}
