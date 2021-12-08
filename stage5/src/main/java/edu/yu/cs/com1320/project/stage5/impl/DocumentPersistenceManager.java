package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import edu.yu.cs.com1320.project.BTree;
import edu.yu.cs.com1320.project.impl.BTreeImpl;
import edu.yu.cs.com1320.project.stage5.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import javax.xml.bind.DatatypeConverter;

/**
 * created by the document store and given to the BTree via a call to BTree.setPersistenceManager
 */
public class DocumentPersistenceManager implements PersistenceManager<URI, Document> {

    private String baseDir;
    
    public DocumentPersistenceManager(File baseDir){
        if(baseDir == null){
            this.baseDir = System.getProperty("user.dir");
        }else{
            this.baseDir = baseDir.toString();
        }
    }
    class DocumentSerializer implements JsonSerializer<Document>{
        Gson gson = new Gson();
        @Override
        public JsonElement serialize(Document src, Type typeOfSrc, JsonSerializationContext context) {
            JsonElement mapAsJson = gson.toJsonTree(src.getWordMap()); 
            JsonObject docSerializer = new JsonObject();
            if(src.getDocumentTxt() == null){ //Binary document
                byte[] bytes = src.getDocumentBinaryData();
                String base64Encoded = encodeBytes(bytes);
                docSerializer.addProperty("binary", base64Encoded);
            } else{ //Text document
                docSerializer.addProperty("text", src.getDocumentTxt());
            }
            docSerializer.addProperty("uri", src.getKey().toString()); //Not sure about this
            docSerializer.add("wordCountMap", mapAsJson);
            return docSerializer;
        }
    }
    class DocumentDeserializer implements JsonDeserializer<Document>{
        Gson gson = new Gson();
        @Override
        public Document deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            URI uri = null;
            try {
                uri = new URI((jsonObject).get("uri").getAsString());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            boolean isText = true;
            //Document document = gson.fromJson(json, Document.class);
            // Add in check to see if binary or text document
            String text = "";
            try{
                text = (jsonObject).get("text").getAsString();
            } catch(Exception e){
                isText = false;
            }
            if(isText){ //Text doucment
                Document doc = new DocumentImpl(uri , text);
                Map<String,Integer> map = (Map<String,Integer>)(gson.fromJson(jsonObject.get("wordCountMap"), Map.class));
                HashMap<String,Integer> wordMap = new HashMap<>();
                wordMap.putAll(map);
                doc.setWordMap(wordMap);
                return doc;
            } else{ //Binary document
                byte[] binaryData = decodeString(text);// CHECK
                Document doc = new DocumentImpl(uri , binaryData);
                //doc.setWordMap((HashMap<String, Integer>)gson.fromJson("wordCountMap", HashMap.class));
                return doc;
            }
        }
    }
    @Override
    public void serialize(URI uri, Document val) throws IOException {
        DocumentSerializer docSerializer = new DocumentSerializer();
        Gson customGson = new GsonBuilder().registerTypeAdapter(DocumentImpl.class, docSerializer).create();
        String uriAsString = uri.toString();
        if(uri.getScheme() != null){
            uriAsString = uriAsString.replace(uri.getScheme(), "");
        }
        String parsedUri = uriAsString.replace("://", File.separator) + ".json";
        //parsedUri = parsedUri.replaceAll("/", File.separator);
        String path = "";
        String name = "";
        File directories;
        if(parsedUri.contains(File.separator)){
            path = parsedUri.substring(0, parsedUri.lastIndexOf(File.separator));
            name = parsedUri.substring(parsedUri.lastIndexOf(File.separator), parsedUri.length());
            directories = new File(this.baseDir + path);
            directories.mkdirs();
            File file = new File(this.baseDir + path + name);
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            customGson.toJson(val, writer);
            writer.close();
        } else{
            name = parsedUri;
            File file = new File(this.baseDir + name);
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            customGson.toJson(val, writer);
            //writer.write(contentsOfDoc);
            writer.close();
        }
    }

    private String encodeBytes(byte[] bytes){
        return DatatypeConverter.printBase64Binary(bytes);
    }

    @Override
    public Document deserialize(URI uri) throws IOException {
        DocumentDeserializer dd = new DocumentDeserializer();
        Gson customGson = new GsonBuilder().registerTypeAdapter(DocumentImpl.class, dd).create();
        String uriAsString = uri.toString();
        if(uri.getScheme() != null){
            uriAsString = uriAsString.replace(uri.getScheme(), "");
        }
        String parsedUri = uriAsString.replace("://", File.separator) +".json";
        // String parsedUri = uri.toString().replace(uri.getScheme(), "").replace(":/", "") + ".json";
        if(parsedUri.contains(File.separator)){
            String path = parsedUri.substring(0, parsedUri.lastIndexOf(File.separator));
            String name = parsedUri.substring(parsedUri.lastIndexOf(File.separator), parsedUri.length());
            File file = new File(this.baseDir + path + name);
            if(!file.exists()){
                return null;
            }
            FileReader reader = new FileReader(this.baseDir + path + name);
            Document doc = customGson.fromJson(reader, DocumentImpl.class);
            reader.close();
            delete(uri);
            return doc;
        } 
        File file = new File(this.baseDir + parsedUri);
        if(!file.exists()){
            return null;
        }
        FileReader reader = new FileReader(this.baseDir + parsedUri);
        Document doc = customGson.fromJson(reader, DocumentImpl.class);
        reader.close();
        delete(uri);
        return doc;
    }

    private byte[] decodeString(String encodedString){
        return DatatypeConverter.parseBase64Binary(encodedString);
    }

    /**
     * delete the file stored on disk that corresponds to the given key
     * @param key
     * @return true or false to indicate if deletion occured or not
     * @throws IOException
     */
    // think of delete as purely a "utility" method .deserialize should call delete to remove it from 
    // disk once it has been deserialized. for get, choice #1 -"/Move doc to memory, 
    // delete it from storage, and return it"
    @Override
    public boolean delete(URI key) throws IOException {
        String uriAsString = key.toString();
        if(key.getScheme() != null){
            uriAsString = uriAsString.replace(key.getScheme(), "");
        }
        String parsedUri = uriAsString.replace("://", File.separator) +".json";
        // String parsedUri = key.toString().replace(key.getScheme(), "").replace(":/", "") + ".json";
        if(parsedUri.contains(File.separator)){
            String path = parsedUri.substring(0, parsedUri.lastIndexOf(File.separator));
            String name = parsedUri.substring(parsedUri.lastIndexOf(File.separator), parsedUri.length());
            File file = new File(this.baseDir + path + name);
            return file.delete();
        }else{
            File file = new File(this.baseDir + parsedUri);
            return file.delete();
        }
        // if(!file.exists()){

        // }
    }

    // public static void main(String[] args) throws Exception{
    //     String txt = "Let's hope this works";
    //     URI uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
    //     DocumentStore docStore = new DocumentStoreImpl(null);
    //     docStore.putDocument(new ByteArrayInputStream(txt.getBytes()), uri1, DocumentStore.DocumentFormat.TXT);
    //     BTree bTree = new BTreeImpl<>();
    //     DocumentPersistenceManager pm = new DocumentPersistenceManager(null);
    //     bTree.setPersistenceManager(pm);
    //     bTree.moveToDisk(uri1);
    // }
}