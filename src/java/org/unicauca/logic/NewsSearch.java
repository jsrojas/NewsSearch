/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unicauca.logic;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author JuanSebastian
 */
public class NewsSearch {

    public static String url1 = "http://api.newsinapp.io/topics/search/v1/?"; //url necesaria para realizar la busqueda de noticias
    public static String url2 = "http://api.newsinapp.io/topics/";//url necesaria para realizar la busqueda de temas relacionados a un arreglo de noticias. Si no existe el tema el servicio despliega una vista en blanco
    public static final String apiKey = "hlZPQOA78nXruXQVsPwaMvpPQZXw7BAuloFszLttb4g";// api Key del API Newsinapp
    public static String body, youtube;// String body recorre el arreglo de noticias y extrae posicion a posicion del mismo. youtube obtiene la clave distintiva de un video de youtube relacionado con la noticia
    public static ArrayList<String> noticias = new ArrayList<String>();// noticias es el arreglo que contiene las noticias luego de realizado el parsing JSON a JAVA y es enviado al cliente web

    //getID es el metodo encargado de obtener la respuesta JSON del API luego de digitar el tema de interes
    //posteriormente se extrae un id de la estructura JSON que identifica un arreglo de noticias
    public static String getID(String query) {
        HttpClient client = new DefaultHttpClient();
        String Responseid = null;


        HttpGet requestid = new HttpGet(url1 + "auth_api_key=" + apiKey + "&query=" + query + "&page=1");
        try {
            HttpResponse response = client.execute(requestid);
            Responseid = EntityUtils.toString(response.getEntity());
        } catch (IOException ex) {
            Logger.getLogger(NewsSearch.class.getName()).log(Level.SEVERE, null, ex);
        }
        return JSONIDparsing(Responseid);
    }
    
    //getNews se encarga de obtener la respuesta JSON del API que contiene el arreglo de noticias
    // a traves del id extraido por el metodo getID
    public static List<String> getNews(String query) {
        String id = getID(query);
        String newId = id.substring(1, id.length() - 1);
        HttpClient client = new DefaultHttpClient();
        String ResponseNews = null;


        HttpGet request = new HttpGet(url2 + newId + "/news/v1/?page=1&auth_api_key=" + apiKey);
        try {
            HttpResponse response = client.execute(request);
            ResponseNews = EntityUtils.toString(response.getEntity());
        } catch (IOException ex) {
            Logger.getLogger(NewsSearch.class.getName()).log(Level.SEVERE, null, ex);
        }
        return JSONNewsparsing(ResponseNews);
    }
    
    //JSONIDparsing realiza el parsing JSON a JAVA y extrae el id necesario para posteriormente
    //recibir el arreglo que contiene a las noticias
    public static String JSONIDparsing(String Responseid) {

        JsonParser parser = new JsonParser();
        JsonObject obj = (JsonObject) parser.parse(Responseid);
        JsonObject response = (JsonObject) obj.get("response");
        JsonArray topics = response.getAsJsonArray("topics");
        JsonElement topic = topics.get(0);
        JsonObject tpc = (JsonObject) parser.parse(topic.toString());
        return tpc.get("id").toString();

    }
    
    //JSONNewsparsing se encarga del parsing JSON a JAVA de la estructura que trae el JsonArray
    //que contiene la información de las noticias. Se toma este JsonArray y se pasa a una lista de
    //Strings que posteriormente es enviada al cliente web, donde se procesa para ser presentada correctamente.
    public static List<String> JSONNewsparsing(String ResponseNews) {

        JsonParser parser = new JsonParser();
        JsonObject obj = (JsonObject) parser.parse(ResponseNews);
        JsonObject resp = (JsonObject) obj.get("response");
        JsonArray news = (JsonArray) resp.getAsJsonArray("news");
        int i, j;
        noticias.clear();
        if (news.size() != 0) {
            for (i = 0; i < news.size(); i++) {
                JsonElement Nw = news.get(i);
                JsonObject rep = (JsonObject) parser.parse(Nw.toString());
                String title = rep.get("title").toString();
                String subtitle = rep.get("subTitle").toString();
                String source = rep.get("source").toString();
                String published = rep.get("published").toString();
                JsonArray properties = rep.getAsJsonArray("properties");
                if (properties.size() != 0) {
                    for (j = 0; j < properties.size(); j++) {
                        JsonElement vid = properties.get(j);
                        JsonObject value = (JsonObject) parser.parse(vid.toString());
                        if (value.get("name").toString().contains("youtube:video_id")) {
                            String video = value.get("value").toString();
                            youtube = ("http://www.youtube.com/watch?v=" + video.substring(1, video.length() - 1));
                        } else {
                            youtube = ("Video no disponible");
                        }
                    }
                } else {
                    youtube = ("Video no disponible");
                }

                body = title + "♣" + subtitle + "♣" + youtube + "♣" + published;
                noticias.add(body);
            }
        } else {
            ArrayList<String> Nonw = new ArrayList<String>();
            Nonw.add("No se han encontrado noticias disponibles");
            return Nonw;
        }
        System.out.println("CICLO TERMINADO");
        return noticias;
    }
}
