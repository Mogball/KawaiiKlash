package kawaiiklash;

/**
 *
 * @author Jeff Niu
 */
public class RectangleSorter {

    private Rect[] boxes;
    private int length;
    
    public void sortHorAsc(final Rect[] values) {
        if (values == null || values.length == 0) {
            return;
        }
        boxes = values;
        length = values.length;
        qsHorAsc(0, length - 1);
    }

    public void sortHorDes(final Rect[] values) {
        if (values == null || values.length == 0) {
            return;
        }
        boxes = values;
        length = values.length;
        qsHorDes(0, length - 1);
    }

    public void sortVerAsc(final Rect[] values) {
        if (values == null || values.length == 0) {
            return;
        }
        boxes = values;
        length = values.length;
        qsVerAsc(0, length - 1);
    }

    public void sortVerDes(final Rect[] values) {
        if (values == null || values.length == 0) {
            return;
        }
        boxes = values;
        length = values.length;
        qsVerDes(0, length - 1);
    }

    private void qsHorAsc(final int low, final int high) {
        int i = low;
        int j = high;
        final Rect pivot = boxes[low + (high - low) / 2];
        while (i <= j) {
            while (boxes[i].getX() < pivot.getX()) {
                i++;
            }
            while (boxes[j].getX() > pivot.getX()) {
                j--;
            }
            if (i <= j) {
                exchange(i, j);
                i++;
                j--;
            }
        }
        if (low < j) {
            qsHorAsc(low, j);
        }
        if (i < high) {
            qsHorAsc(i, high);
        }
    }

    private void qsHorDes(final int low, final int high) {
        int i = low;
        int j = high;
        final Rect pivot = boxes[low + (high - low) / 2];
        while (i <= j) {
            while (boxes[i].getX() + boxes[i].getWidth() > pivot.getX() + pivot.getWidth()) {
                i++;
            }
            while (boxes[j].getX() + boxes[j].getWidth() < pivot.getX() + pivot.getWidth()) {
                j--;
            }
            if (i <= j) {
                exchange(i, j);
                i++;
                j--;
            }
        }
        if (low < j) {
            qsHorDes(low, j);
        }
        if (i < high) {
            qsHorDes(i, high);
        }
    }

    private void qsVerAsc(final int low, final int high) {
        int i = low;
        int j = high;
        final Rect pivot = boxes[low + (high - low) / 2];
        while (i <= j) {
            while (boxes[i].getY() < pivot.getY()) {
                i++;
            }
            while (boxes[j].getY() > pivot.getY()) {
                j--;
            }
            if (i <= j) {
                exchange(i, j);
                i++;
                j--;
            }
        }
        if (low < j) {
            qsVerAsc(low, j);
        }
        if (i < high) {
            qsVerAsc(i, high);
        }
    }

    private void qsVerDes(final int low, final int high) {
        int i = low;
        int j = high;
        final Rect pivot = boxes[low + (high - low) / 2];
        while (i <= j) {
            while (boxes[i].getY() + boxes[i].getHeight() > pivot.getY() + pivot.getHeight()) {
                i++;
            }
            while (boxes[j].getY() + boxes[j].getHeight() < pivot.getY() + pivot.getHeight()) {
                j--;
            }
            if (i <= j) {
                exchange(i, j);
                i++;
                j--;
            }
        }
        if (low < j) {
            qsVerDes(low, j);
        }
        if (i < high) {
            qsVerDes(i, high);
        }
    }

    private void exchange(final int i, final int j) {
        final Rect r = boxes[i];
        boxes[i] = boxes[j];
        boxes[j] = r;
    }

}
