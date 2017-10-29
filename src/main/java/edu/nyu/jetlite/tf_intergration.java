package edu.nyu.jetlite;
/**
 * Created by xuanwang on 10/17/17.
 */
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.tensorflow.DataType;
import org.tensorflow.Graph;
import org.tensorflow.Output;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.TensorFlow;

public class tf_intergration {
    public static void main(String[] args) {
        String modelDir = "./src/main/resources/";


        byte[] graphDef = readAllBytesOrExit(Paths.get(modelDir, "frozen_trained_model.pb"));
        int[][] input = {{1, 2}, {3, 4}};

        // preprocess the image to feed into inception model
        int[][] output = executeGraph(graphDef, createInput(input));
        System.out.println(output[0][0]);
        System.out.println(output[0][1]);
        System.out.println(output[1][0]);
        System.out.println(output[1][1]);

    }

    private static Tensor createInput(int[][] input) {
        try (Graph g = new Graph()) {
            GraphBuilder b = new GraphBuilder(g);
            // Since the graph is being constructed once per execution here, we can use a constant for the
            // input image. If the graph were to be re-used for multiple input images, a placeholder would
            // have been more appropriate.
            final Output output = b.constant("input", input);
            try (Session s = new Session(g)) {
                return s.runner().fetch(output.op().name()).run().get(0);
            }
        }
    }

    private static int[][] executeGraph(byte[] graphDef, Tensor image) {
        try (Graph g = new Graph()) {
            g.importGraphDef(graphDef);
            try (Session s = new Session(g);
                 Tensor result = s.runner().feed("input", image).fetch("output").run().get(0)) {
                return result.copyTo(new int[2][2]);
            }
        }
    }

    private static byte[] readAllBytesOrExit(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            System.err.println("Failed to read [" + path + "]: " + e.getMessage());
            System.exit(1);
        }
        return null;
    }

    // In the fullness of time, equivalents of the methods of this class should be auto-generated from
    // the OpDefs linked into libtensorflow_jni.so. That would match what is done in other languages
    // like Python, C++ and Go.
    static class GraphBuilder {
        GraphBuilder(Graph g) {
            this.g = g;
        }

        Output constant(String name, Object value) {
            try (Tensor t = Tensor.create(value)) {
                return g.opBuilder("Const", name)
                        .setAttr("dtype", t.dataType())
                        .setAttr("value", t)
                        .build()
                        .output(0);
            }
        }

        private Graph g;
    }
}
