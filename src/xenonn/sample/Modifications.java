package xenonn.sample;

import xenonn.math.Tensor;
import xenonn.nn.layer.Dense;
import xenonn.nn.layer.modify.Merge1D;
import xenonn.nn.layer.recurrent.GRU;
import xenonn.nn.layer.recurrent.LSTM;
import xenonn.nn.learningrule.BackPropagationMemory;
import xenonn.nn.learningrule.GradientBasedOptimizer;
import xenonn.nn.loss.MSELoss;
import xenonn.nn.model.Model;
import xenonn.nn.model.Sequential;
import xenonn.nn.optimizer.*;

import java.util.ArrayList;
import java.util.List;

public class Modifications {

    private static List<Tensor> trainIn;
    private static List<Tensor> trainOut;
    private static final int LENGTH = 5;

    public static void main(String[] args) {
        trainIn = new ArrayList<>();
        trainOut = new ArrayList<>();

        double[] sums = new double[LENGTH];
        for (int i = 0; i < 5; i++) {
            double[] in = new double[LENGTH];
            for (int j = 0; j < LENGTH; j++) {
                in[j] = 1 / 5D;
                sums[j] += in[j];
            }
            trainIn.add(new Tensor(in, 1, LENGTH));
            trainOut.add(new Tensor(sums.clone(), 1, LENGTH));
        }


        Merge1D recurrent0 = new Merge1D(new LSTM(3), new GRU(3), 2);
        Merge1D dense0 = new Merge1D(new Dense(3), new Dense(4), 4);
        Model model = new Sequential(new int[]{1, LENGTH},
                recurrent0,
                dense0,
                new Dense(LENGTH));

        model.initialize();
        System.out.println(model.summary());

        Optimizer optimizer = new Adam(model, 1e-3, 0.9, 0.99);
        GradientBasedOptimizer backPropagation = new BackPropagationMemory(model, optimizer, new MSELoss());

        backPropagation.setTrainSet(trainIn, trainOut);


        for (int i = 0; i < trainIn.size(); i++) {
            System.out.println("out: ");
            System.out.println(model.feedForward(trainIn.get(i)));
            System.out.println("expected: ");
            System.out.println(trainOut.get(i));
        }
        recurrent0.resetMemory();
        System.out.println(backPropagation.trainLoss(1));
        recurrent0.resetMemory();

        for (int i = 0; i < 10000; i++) {
            backPropagation.trainIterations(100, 1);
            recurrent0.resetMemory();
            System.out.println(backPropagation.trainLoss(1));
            recurrent0.resetMemory();
        }

        System.out.println("result: ");
        for (int i = 0; i < trainIn.size(); i++) {
            System.out.println("out: ");
            System.out.println(model.feedForward(trainIn.get(i)));
            System.out.println("expected: ");
            System.out.println(trainOut.get(i));
        }

    }
}
