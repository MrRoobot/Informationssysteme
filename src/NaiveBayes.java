import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.core.converters.ConverterUtils;

import java.io.File;

public class NaiveBayes {

    public String bayesResult="";
    public  void naiveBayes() throws Exception {
        // load data
        ArffLoader loader = new ArffLoader();
        loader.setFile(new File("TrainingsDaten.arff"));
        Instances structure = loader.getStructure();
        structure.setClassIndex(structure.numAttributes()-1);
        ConverterUtils.DataSource source2 = new ConverterUtils.DataSource("TestDaten.arff");
        Instances test = source2.getDataSet();

        if (test.classIndex() == -1)
            test.setClassIndex(structure.numAttributes() - 1);


        // train NaiveBayes
        NaiveBayesUpdateable nb = new NaiveBayesUpdateable();
        nb.buildClassifier(structure);
        Instance current;
        while ((current = loader.getNextInstance(structure)) != null) {
            nb.updateClassifier(current);
        }
        double label = nb.classifyInstance(test.instance(0));
        test.instance(0).setClassValue(label);

        // output generated model
        System.out.println(test.instance(0).stringValue(4));
        bayesResult=test.instance(0).stringValue(4);
    }
}