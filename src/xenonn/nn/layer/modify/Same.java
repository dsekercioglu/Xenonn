package xenonn.nn.layer.modify;

import xenonn.math.Tensor;
import xenonn.nn.layer.Layer;

public class Same extends Layer {


    private int[] outDimensions;

    public void initializeWeights() {
    }

    @Override
    public void setInDimensions(int[] inDimensions) {
        outDimensions = inDimensions.clone();
    }

    @Override
    public int[] getOutDimensions() {
        return outDimensions;
    }

    @Override
    public Tensor feedForward(Tensor in) {
        return in;
    }

    @Override
    public Tensor getDelta(Tensor outputError) {
        return outputError;
    }
}
