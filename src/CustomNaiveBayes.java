import weka.core.Instances;
import weka.core.converters.ArffLoader;

import java.io.File;
import java.util.*;
import java.util.stream.IntStream;



//code by Florian Kaindl, with help from
// https://machinelearningmastery.com/naive-bayes-classifier-scratch-python/
public class CustomNaiveBayes {
    private ArffLoader loader = new ArffLoader();

    private ArrayList<int[]> trainSet = new ArrayList<int[]>();
    private ArrayList<int[]> testSet = new ArrayList<int[]>();
    private ArrayList<ArrayList<double[]>> summaries;

    public void loadLearningData() throws Exception{
        ArffLoader loader = new ArffLoader();
        loader.setFile(new File("TrainingsDaten.arff"));
        Instances dataSet = loader.getDataSet();
        Object data [] = dataSet.toArray();
        print("Loaded training data file with " + String.valueOf(data.length) + " rows" );
        for(int i = 0; i < data.length; i++){
            trainSet.add(convertToIntArray(data[i]));
        }
        summaries = summarizeByClass(trainSet);


        /*
        * commented code was for testing of accuracy function
        * it splits the training data into randomized training and test data,
        * runs the predictions and checks the accuracy
        *


        for(int i = 0; i < data.length; i++){
            print(data[i].toString());
            testSet.add(convertToIntArray(data[i]));
        }

        splitDataset(data, 0.5);
        ArrayList<ArrayList<int[]>> separated = separateByClass(trainSet);
        for(int i = 0; i < separated.size(); i++) {
            print("Class " + (i+1) + ":" +"(" + separated.get(i).size() +")");
            for(int j = 0; j < separated.get(i).size(); j++){
                print(Arrays.toString(separated.get(i).get(j)));
            }
        }
        ArrayList<ArrayList<double[]>> summaries = summarizeByClass(trainSet);
        print(String.valueOf("Summaries size: " + summaries.size()));
        for(int i = 0; i < summaries.size(); i++) {
            print(String.valueOf("Summary " + i + " size: " + summaries.get(i).size()));
            for(int j = 0; j < summaries.get(i).size(); j++) {
                print(String.valueOf("Summary "+ i + "-" + + j + ": " + Arrays.toString(summaries.get(i).get(j))));
            }
        }

        int[] predictions = getPredictions(summaries, testSet);
        double accuracy = getAccuracy(testSet, predictions);
        print("Predictions: " + Arrays.toString(predictions));
        */
    }

    public int makePredictionFromValueArray(int[] val){
        testSet.clear();
        testSet.add(val);
        return getPredictions(summaries, testSet)[0];
    }

    private int[] convertToIntArray(Object values){
        int [] r = new int[5];
        String[] list = values.toString().split(",");
        for(int i = 0; i < 5; i++){
            r[i] = Integer.parseInt(list[i]);
        }
        return r;
    }

    public void splitDataset(Object[] dataSet, double splitRatio) {
        int trainSize = (int) (dataSet.length * splitRatio);
        Random r = new Random();
        while(trainSet.size() < trainSize) {
            int index = r.nextInt(testSet.size());
            trainSet.add(testSet.get(index));
            testSet.remove(index);
        }
        print("testSet: ");
        for(int i = 0; i < testSet.size(); i++){
            print(Arrays.toString(testSet.get(i)));
        }
        print("trainSet: ");
        for(int i = 0; i < trainSet.size(); i++){
            print(Arrays.toString(trainSet.get(i)));
        }
    }

    private ArrayList<ArrayList<int[]>> separateByClass(ArrayList<int[]> data){
        ArrayList<ArrayList<int[]>> r = new ArrayList<ArrayList<int[]>>();
        for(int i = 0; i < 5; i++){
            ArrayList<int[]> entry = new ArrayList<int[]>();
            r.add(entry);
        }
        for(int i = 0; i< data.size(); i++){
            int[] currentSet = data.get(i);
            int classIndex = currentSet[4]-1;
            r.get(classIndex).add(Arrays.copyOf(currentSet, currentSet.length-1));
        }
        return r;
    }

    private double mean(int[] numbers){
        return((double)IntStream.of(numbers).sum()/ (double) numbers.length);
    }

    private double stdev(int[] numbers) {
        double mean = mean(numbers);
        double variance;
        double sum = 0;
        for(int i = 0; i < numbers.length; i++){
            sum += Math.pow(numbers[i] - mean, 2.0);
        }
        variance = (sum / (numbers.length-1));
        return Math.sqrt(variance);
    }

    private ArrayList<double[]> summarize(ArrayList<int[]> data) {
        ArrayList<double[]> r = new ArrayList<>();
        for(int i = 0; i < data.get(0).length; i++) {
            double[] summary = new double[2];
            int[] values = new int[data.size()];
            for(int j = 0; j < data.size(); j++){
                values[j] = data.get(j)[i];
            }
            summary[0] = mean(values);
            summary[1] = stdev(values);
            r.add(summary);
        }
        return r;
    }

    private ArrayList<ArrayList<double[]>> summarizeByClass(ArrayList<int[]> data){
        ArrayList<ArrayList<int[]>> separated = separateByClass(data);
        ArrayList<ArrayList<double[]>> summaries = new ArrayList<>();
        for(int i = 0; i < separated.size(); i++){
            summaries.add(summarize(separated.get(i)));
        }
        return summaries;
    }


    private double calculateProbability(int x, double mean, double stdev){
        double r = 0;
        double divider = 2*Math.pow(stdev,2);
        if(divider == 0) return 1;
        double exponent = Math.exp(-(Math.pow(x-mean,2)/divider));
        r = (1 / (Math.sqrt(2*Math.PI) * stdev)) * exponent;
        return r;
    }

    private HashMap<Integer, Double> calculateClassProbabilities(ArrayList<ArrayList<double[]>> summaries, int[] inputVector){
        HashMap <Integer, Double> probabilities = new HashMap<>();
        for(int i = 0; i < summaries.size(); i++){
            probabilities.put(i, 1.0);
            for(int j = 0; j < summaries.get(i).size(); j++){
                double mean = summaries.get(i).get(j)[0];
                double stdev = summaries.get(i).get(j)[1];
                int x = inputVector[j];
                double newEntry = probabilities.get(i) * calculateProbability(x, mean, stdev);
                probabilities.put(i, newEntry);
            }
            print("probability for class " + i + ": " + probabilities.get(i));
        }

        return probabilities;
    }

    private int predict(ArrayList<ArrayList<double[]>> summaries, int[] inputVector) {
        HashMap<Integer, Double> probabilities = calculateClassProbabilities(summaries, inputVector);
        int bestLabel = -1;
        double bestProb = -1;
        for(int i = 0; i < probabilities.size(); i++){
            double probability = probabilities.get(i);
            if(bestLabel == -1 || probability > bestProb){
                bestProb = probability;
                bestLabel = i;
            }
        }
        return bestLabel;
    }

    private int[] getPredictions(ArrayList<ArrayList<double[]>> summaries, ArrayList<int[]> testSet){
        int[] predictions = new int[testSet.size()];
        for(int i = 0; i < testSet.size(); i++){
            int result = predict(summaries, testSet.get(i));
            predictions[i] = result;
        }
        return predictions;
    }

    private double getAccuracy(ArrayList<int[]> testSet, int[] predictions){
        int correct = 0;
        int length = testSet.get(0).length;
        for (int i = 0; i < testSet.size(); i++){
            if(testSet.get(i)[length-1] == predictions[i]+1){
                correct++;
            }
        }
        return (correct/(double)(testSet.size()) * 100);
    }

    private void print(String text){
        System.out.println(text);
    }

}
