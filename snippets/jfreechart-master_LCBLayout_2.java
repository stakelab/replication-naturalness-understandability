@Override
    public Dimension minimumLayoutSize(Container parent) {

        synchronized (parent.getTreeLock()) {
            Insets insets = parent.getInsets();
            int ncomponents = parent.getComponentCount();
            int nrows = ncomponents / COLUMNS;
            for (int c = 0; c < COLUMNS; c++) {
                for (int r = 0; r < nrows; r++) {
                    Component component = parent.getComponent(r * COLUMNS + c);
                    Dimension d = component.getMinimumSize();
                    if (this.colWidth[c] < d.width) {
                        this.colWidth[c] = d.width;
                    }
                    if (this.rowHeight[r] < d.height) {
                        this.rowHeight[r] = d.height;
                    }
                }
            }
            int totalHeight = this.vGap * (nrows - 1);
            for (int r = 0; r < nrows; r++) {
                totalHeight = totalHeight + this.rowHeight[r];
            }
            int totalWidth = this.colWidth[0] + this.labelGap 
                + this.colWidth[1] + this.buttonGap + this.colWidth[2];
            return new Dimension(
                insets.left + insets.right + totalWidth + this.labelGap 
                + this.buttonGap,
                insets.top + insets.bottom + totalHeight + this.vGap
            );
        }

    }