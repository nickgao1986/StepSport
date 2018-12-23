package com.example.tutorial;

public class TipInfo extends Tip {
    private boolean isMarked = false;

    public boolean isMarked() {
        return isMarked;
    }

    public void setMarked(boolean isMarked) {
        this.isMarked = isMarked;
    }

    public void setTip(Tip tip) {
        this.setId(tip.getId());
        this.setViewId(tip.getViewId());
        this.setScreen(tip.getScreen());
        this.setText(tip.getText());
        this.setBrand(tip.getBrand());
        this.setPermission(tip.getPermission());
        this.setHotPadding(tip.getHotPadding());
        this.setHotZoomIn(tip.isHotZoomIn());
        this.setEventName(tip.getEventName());
        this.setFragment(tip.isFragment());
    }

    @Override
    public boolean equals(Object o) {
        boolean isEqual = false;
        do {
            if (o == null) {
                break;
            }

            if (!(o instanceof TipInfo)) {
                break;
            }

            TipInfo t = (TipInfo) o;
            if (t.getId() != this.getId()) {
                break;
            }

            isEqual = true;

        } while (false);

        return super.equals(o);
    }
}
