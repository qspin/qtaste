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

/*

*/
package com.qspin.qtaste.ui.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.ImageIcon;

public class Utils {

    /**
     * @return ImageIcon, or null if the path was invalid.
     */
    static public ImageIcon setImageIcon(ImageIcon icon, String description) {
        if (icon == null) {
            return icon;
        }
        icon.setDescription(description);
        return icon;
    }

    static boolean deleteDirectoryContent(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectoryContent(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    static void deleteDirectoryContent(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            deleteDirectoryContent(file);
        }
    }

    public static void copyFiles(String srcPath, String destPath) throws IOException {
        File srcFile = new File(srcPath);
        File destFile = new File(destPath);
        destFile.mkdirs();
        if (srcFile.isDirectory() && destFile.isDirectory()) {
            copyFiles(srcFile, destFile);
        }
    }

    // The method copyFiles being defined
    public static void copyFiles(File src, File dest) throws IOException {
        if (src.isDirectory()) {
            dest.mkdirs();
            String list[] = src.list();
            for (int i = 0; i < list.length; i++) {
                String dest1 = dest.getPath() + "/" + list[i];
                String src1 = src.getPath() + "/" + list[i];
                // i.e: avoid .svn directory
                File src1_ = new File(src1);
                File dest1_ = new File(dest1);
                if (!src1_.isHidden()) {
                    copyFiles(src1_, dest1_);
                }
            }
        } else {

            FileInputStream fin = new FileInputStream(src);
            FileOutputStream fout = new FileOutputStream(dest);
            int c;
            while ((c = fin.read()) >= 0) {
                fout.write(c);
            }
            fin.close();
            fout.close();
        }
    }

    /**
     * Expands all nodes in a JTree.
     *
     * @param tree The JTree to expand.
     * @param depth The depth to which the tree should be expanded.  Zero
     * will just expand the root node, a negative value will
     * fully expand the tree, and a positive value will
     * recursively expand the tree to that depth.
     */
    public static void expandJTree(javax.swing.JTree tree, int depth) {
        javax.swing.tree.TreeModel model = tree.getModel();
        expandJTreeNode(tree, model, model.getRoot(), 0, depth);
    } // expandJTree()

    /**
     * Expands all nodes in a JTree.
     *
     * @param tree The JTree to expand.
     * @param depth The depth to which the tree should be expanded.  Zero
     * will just expand the root node, a negative value will
     * fully expand the tree, and a positive value will
     * recursively expand the tree to that depth.
     */
    public static void collapseJTree(javax.swing.JTree tree, int depth) {
        javax.swing.tree.TreeModel model = tree.getModel();
        collapseJTreeNode(tree, model, model.getRoot(), 0, depth);
    } // expandJTree()

    /**
     * Expands a given node in a JTree.
     *
     * @param tree The JTree to expand.
     * @param model The TreeModel for tree.
     * @param node The node within tree to expand.
     * @param row The displayed row in tree that represents
     * node.
     * @param depth The depth to which the tree should be expanded.
     * Zero will just expand node, a negative
     * value will fully expand the tree, and a positive
     * value will recursively expand the tree to that
     * depth relative to node.
     */
    public static int expandJTreeNode(javax.swing.JTree tree, javax.swing.tree.TreeModel model, Object node, int row, int depth) {
        if (node != null && !model.isLeaf(node)) {
            tree.expandRow(row);
            if (depth != 0) {
                for (int index = 0; row + 1 < tree.getRowCount() && index < model.getChildCount(node); index++) {
                    row++;
                    Object child = model.getChild(node, index);
                    if (child == null) {
                        break;
                    }
                    javax.swing.tree.TreePath path;
                    while ((path = tree.getPathForRow(row)) != null && path.getLastPathComponent() != child) {
                        row++;
                    }
                    if (path == null) {
                        break;
                    }
                    row = expandJTreeNode(tree, model, child, row, depth - 1);
                }
            }
        }
        return row;
    } // expandJTreeNode()

    /**
     * Expands a given node in a JTree.
     *
     * @param tree The JTree to expand.
     * @param model The TreeModel for tree.
     * @param node The node within tree to expand.
     * @param row The displayed row in tree that represents
     * node.
     * @param depth The depth to which the tree should be expanded.
     * Zero will just expand node, a negative
     * value will fully expand the tree, and a positive
     * value will recursively expand the tree to that
     * depth relative to node.
     */
    public static int collapseJTreeNode(javax.swing.JTree tree, javax.swing.tree.TreeModel model, Object node, int row, int
          depth) {
        if (node != null && !model.isLeaf(node)) {
            tree.collapseRow(row);
            if (depth != 0) {
                for (int index = 0; row + 1 < tree.getRowCount() && index < model.getChildCount(node); index++) {
                    row++;
                    Object child = model.getChild(node, index);
                    if (child == null) {
                        break;
                    }
                    javax.swing.tree.TreePath path;
                    while ((path = tree.getPathForRow(row)) != null && path.getLastPathComponent() != child) {
                        row++;
                    }
                    if (path == null) {
                        break;
                    }
                    row = collapseJTreeNode(tree, model, child, row, depth - 1);
                }
            }
        }
        return row;
    } // expandJTreeNode()
}
