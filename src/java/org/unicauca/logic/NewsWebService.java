/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unicauca.logic;

import java.util.List;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 *
 * @author JuanSebastian
 */
@WebService(serviceName = "NewsWebService")
public class NewsWebService {

    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "getNews")
    public List<String> getNews(@WebParam(name = "query") String query) {
        String newQuery = query.replaceAll(" ", "%20");
        return NewsSearch.getNews(newQuery);
    }
}
