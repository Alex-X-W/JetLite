package edu.nyu.jetlite;

import edu.nyu.jet.aceJet.*;
import edu.nyu.jetlite.tipster.Annotation;
import edu.nyu.jetlite.tipster.Document;
import edu.nyu.jetlite.tipster.Span;

import java.io.*;
import java.util.*;

/**
 * Created by xuanwang on 10/13/17.
 * 
 * Make data set for training use.
 * 
 * returns:
 * 1) doc file
 * 2) event file
 * 3) entity file
 * 4) 
 * 
 */
public class DatasetMaker {
    public DatasetMaker () {
    }
    
    public static void main (String[] args) throws IOException {
        String trainDocListFileName = args[0];
        String testDocListFileName = args[1];
        String docDir = args[2];
        String pourDir = args[3]; // where data outputs

        DatasetMaker maker = new DatasetMaker();
        maker.generate(docDir, trainDocListFileName, pourDir + "/train");
        maker.generate(docDir, testDocListFileName, pourDir + "/test");
    }

    /**
     *  Train the event tagger.
     *
     *  @param  docDir           directory containing training documents
     *  @param  docListFileName  file containing list of training documents
     */

    public void generate (String docDir, String docListFileName, String pourDir) throws IOException {
        BufferedReader docListReader = new BufferedReader (new FileReader(docListFileName));
        int docCount = 0;
        String line;
        while ((line = docListReader.readLine()) != null) {
            generateOneDocument (docDir + "/" + line.trim(), pourDir);
            docCount++;
            if (docCount % 5 == 0) System.out.print(".");
        }
    }

    /**
     *  Acquire training data from one Document in the training corpus.
     *
     *  @param  docFileName  the name of the document file
     */

    void generateOneDocument (String docFileName, String pourDir) throws IOException {
        PrintWriter docWriter = new PrintWriter (new FileWriter (makeFile(pourDir, docFileName, "doc")));
        PrintWriter sentWriter = new PrintWriter (new FileWriter (makeFile(pourDir, docFileName, "sentences")));
        PrintWriter eventWriter = new PrintWriter (new FileWriter (makeFile(pourDir, docFileName, "event")));
        PrintWriter argumentsWriter = new PrintWriter (new FileWriter (makeFile(pourDir, docFileName, "arguments")));

        File docFile = new File(docFileName);
        Document doc = new Document(docFile);
        doc.setText(EntityTagger.eraseXML(doc.text()));
        String apfFileName = docFileName.replace("sgm" , "apf.xml");
        AceDocument aceDoc = new AceDocument(docFileName, apfFileName);
        // --- tokenize and split
        Properties config = new Properties();
        config.setProperty("annotators", "token sentence");
        doc = Hub.processDocument(doc, config);
        // ---
        findEventMentions (aceDoc);
        findEntityMentions (aceDoc);
        findArgumentMentions(aceDoc);
        // loop over tokens
        Span span = Hub.getTEXTspan(doc);
        int posn = span.start();
        posn = doc.skipWhitespace(posn, span.end());

        int tokIdx = 0;

        // pour doc
        docWriter.print(doc.text());

        // pour events and arguments
        // only eventType != other got poured
        while (posn < span.end()) {
            Annotation tokenAnnotation = doc.tokenAt(posn);
            if (tokenAnnotation == null) {
                ++posn;
                continue;
            }
            String eventType = eventMentionMap.get(posn);
            ArrayList arguments = argumentsMentionMap.get(posn);

            if (eventType != null) {
                eventWriter.print(posn + "<_s_>" + eventType + "<_s_>" + doc.text(tokenAnnotation) + "<_s_>\n");
                if (!arguments.isEmpty()) {
                    argumentsWriter.print(posn + "<_s_>" + arguments + "<_s_>" + doc.text(tokenAnnotation) + "<_s_>\n");
                }
            }
            posn = tokenAnnotation.end();
            
            ++tokIdx;
        }

        // pour sentences
        Vector<Annotation> sentAnnotations = doc.annotationsOfType("sentence");

        for (Annotation sent : sentAnnotations) {
            sentWriter.print(doc.text(sent.span()));
            sentWriter.print("<_s_>\n");
        }

        docWriter.close();
        sentWriter.close();
        eventWriter.close();
        argumentsWriter.close();


    }

    File makeFile(String pourDir, String docFileName, String fileType) {
        String path = pourDir + "/" + docFileName.replace("sgm", fileType);
        File file = new File(path);
        File parent_directory = file.getParentFile();

        if (null != parent_directory)
        {
            parent_directory.mkdirs();
        }

        return file;
    }


    /**
     *  A map from the position of the entity to the type of the event.
     */

    static Map<Integer, AceEntityMention> entityMentionMap;

    static void findEntityMentions (AceDocument aceDoc) {
        entityMentionMap = new HashMap<Integer, AceEntityMention>();
        ArrayList entities = aceDoc.entities;
        for (int i=0; i<entities.size(); i++) {
            AceEntity entity = (AceEntity) entities.get(i);
            ArrayList mentions = entity.mentions;
            for (int j=0; j<mentions.size(); j++) {
                AceEntityMention mention = (AceEntityMention) mentions.get(j);
                entityMentionMap.put(mention.jetHead.start(), mention);
            }
        }
    }



    /**
     *  A map from the position of the event trigger to the type of the event.
     */

    static Map<Integer, String> eventMentionMap;

    static void findEventMentions (AceDocument aceDoc) {
        eventMentionMap = new HashMap<Integer, String>();
        List<AceEvent> events = aceDoc.events;
        for (AceEvent event : events) {
            String subtype = event.subtype;
            List<AceEventMention> mentions = event.mentions;
            for (AceEventMention mention : mentions) {
                eventMentionMap.put(mention.anchorJetExtent.start(), subtype);
            }
        }
    }

    /**
     *  TODO: A map from the position of the event trigger to its arguments.
     *
     */

    static Map<Integer, ArrayList<AceEventMentionArgument>> argumentsMentionMap;

    static void findArgumentMentions (AceDocument aceDoc) {
        argumentsMentionMap = new HashMap<Integer, ArrayList<AceEventMentionArgument>>();
        List<AceEvent> events = aceDoc.events;
        for (AceEvent event : events) {
            List<AceEventMention> mentions = event.mentions;
            for (AceEventMention mention : mentions) {
                argumentsMentionMap.put(mention.anchorJetExtent.start(), mention.arguments);
            }
        }
    }

}