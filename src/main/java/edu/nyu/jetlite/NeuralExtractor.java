//package edu.nyu.jetlite;
//
//import opennlp.maxent.BasicEventStream;
//import opennlp.maxent.GIS;
//import opennlp.maxent.GISModel;
//import opennlp.maxent.PlainTextByLineDataStream;
//import opennlp.maxent.io.GISModelWriter;
//import opennlp.maxent.io.SuffixSensitiveGISModelWriter;
//import opennlp.model.EventStream;
//
//import java.io.File;
//import java.io.FileReader;
//
///**
// * Created by xuanwang on 10/12/17.
// */
//
//public class NeuralExtractor {
//    public NeuralExtractor() {
//
//    }
//
//    /**
//     *  Build a maximum entropy model from the training data on file 'events',
//     *  discarding features which occur fewer than 'cutoff' times..
//     */
//
//    public static void buildModel (String modelFileName, int cutoff) {
//        try {
//            // read events with blank-separated features
//            FileReader datafr = new FileReader(new File("events"));
//            EventStream es = new BasicEventStream(new PlainTextByLineDataStream(datafr), " ");
//            // train model using NUM_ITERATIONS iterations, including all events (no low-freq cutoff)
//            GISModel model = GIS.trainModel(es, NUM_ITERATIONS, cutoff, USE_SMOOTHING, true);
//            // save model
//            File outputFile = new File(modelFileName);
//            GISModelWriter writer = new SuffixSensitiveGISModelWriter(model, outputFile);
//            writer.persist();
//        } catch (Exception e) {
//            System.out.print("Unable to create model due to exception: ");
//            System.out.println(e);
//            e.printStackTrace();
//        }
//    }
//}
