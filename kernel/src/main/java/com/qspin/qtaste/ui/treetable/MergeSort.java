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

package com.qspin.qtaste.ui.treetable;

/**
 * An implementation of MergeSort, needs to be subclassed to provide a
 * comparator.
 *
 * @author vdubois
 */
public abstract class MergeSort {
    protected Object toSort[];
    protected Object swapSpace[];

    public void sort(Object array[]) {
        if (array != null && array.length > 1) {
            int maxLength;

            maxLength = array.length;
            swapSpace = new Object[maxLength];
            toSort = array;
            this.mergeSort(0, maxLength - 1);
            swapSpace = null;
            toSort = null;
        }
    }

    public abstract int compareElementsAt(int beginLoc, int endLoc);

    protected void mergeSort(int begin, int end) {
        if (begin != end) {
            int mid;

            mid = (begin + end) / 2;
            this.mergeSort(begin, mid);
            this.mergeSort(mid + 1, end);
            this.merge(begin, mid, end);
        }
    }

    protected void merge(int begin, int middle, int end) {
        int firstHalf, secondHalf, count;

        firstHalf = count = begin;
        secondHalf = middle + 1;
        while ((firstHalf <= middle) && (secondHalf <= end)) {
            if (this.compareElementsAt(secondHalf, firstHalf) < 0) {
                swapSpace[count++] = toSort[secondHalf++];
            } else {
                swapSpace[count++] = toSort[firstHalf++];
            }
        }
        if (firstHalf <= middle) {
            while (firstHalf <= middle) {
                swapSpace[count++] = toSort[firstHalf++];
            }
        } else {
            while (secondHalf <= end) {
                swapSpace[count++] = toSort[secondHalf++];
            }
        }
        for (count = begin; count <= end; count++) {
            toSort[count] = swapSpace[count];
        }
    }
}
