package cn.itscloudy.propray;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrContext {
    private PrContext() {
    }

    @Getter
    private static final PrContext instance = new PrContext();

    private ControlBox focusedControlBox = null;

    public void unsetFocusedControlBox(ControlBox focusedControlBox) {
        if (focusedControlBox == this.focusedControlBox) {
            this.focusedControlBox = null;
        }
    }


}
