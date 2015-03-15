/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package servlets;

import cnnct.Cnnct;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author c0646395
 */

@Path("/products")
public class SampleServlet{
    private Object jobjct;
    

@GET
@Produces("application/json; charset=UTF-8")
public Response get() 
{
    return Response.ok(results("select * from product")).build();
    
    }
private Response oneRes(@PathParam("id") String id) 
{
    return Response.ok(results("select * from product where productID = ?", id)).build();
    }
 private String results(String query, String... params) 
 {
    StringBuilder strngbldr = new StringBuilder();
    JsonObject jobjct = null;
    try (Connection con = Cnnct.getConnection()) 
    {
        PreparedStatement prprdstmnt = con.prepareStatement(query);
        for (int i = 1; i <= params.length; i++) 
        {
            prprdstmnt.setString(i, params[i - 1]);
        }
        ResultSet rs = prprdstmnt.executeQuery();
        
        while (rs.next()) 
        {
            jobjct = Json.createObjectBuilder()
                    .add("ProductID", rs.getInt("productid"))
                    .add("Name", rs.getString("name"))
                    .add("Description", rs.getString("description"))
                    .add("Quantity", rs.getInt("quantity"))
                    .build();

                strngbldr.append(jobjct.toString());
        }
        
        } 
    catch (SQLException ex) 
    {
        Logger.getLogger(SampleServlet.class.getName()).log(Level.SEVERE, null, ex);
    }
    return strngbldr.toString();
}

 @POST
@Path("{ProductID}")
public Response post(JsonObject jobjct)
{
  
  String Name = jobjct.getString("name");
  String Quantity = String.valueOf(jobjct.getInt("quantity"));
  String Description = jobjct.getString("description");
  
  update("insert into product (name,quantity,desription) VALUES (?,?,?)", Name, Quantity, Description);

  return Response.ok(jobjct).build();
}
    


private int insert(String query, String Name, String Description, long Quantity) 
{
    int num = 0;
    ArrayList params = new ArrayList();
    params.add(Name);
    params.add(Description);
    params.add(Quantity);
    try (Connection con = Cnnct.getConnection()) 
    {
        PreparedStatement prprdstmnt = con.prepareStatement(query);
        for (int i = 1; i <= params.size(); i++) 
        {
            prprdstmnt.setString(i, params.get(i - 1).toString());
        }
        num = prprdstmnt.executeUpdate();
    } 
    catch (SQLException ex) 
    {
        Logger.getLogger(SampleServlet.class.getName()).log(Level.SEVERE, null, ex);
    }
    return num;
    }
@DELETE
@Path("{ProductID}")
public void dlt(@PathParam("ProductID") int id) throws IOException, SQLException {
    Connection con = Cnnct.getConnection();
    String qry = "delete from product where ProductID =" + id;
    PreparedStatement prprdstmnt = con.prepareStatement(qry);
    prprdstmnt.execute();
}

private int delete(String query, int id) 
{
    int num = 0;
    try (Connection con = Cnnct.getConnection()) 
    {
        PreparedStatement prprdstmnt = con.prepareStatement(query);
        prprdstmnt.setLong(1, id);
        num = prprdstmnt.executeUpdate();
    } 
    catch (SQLException ex) 
    {
        System.out.println(ex);
        Logger.getLogger(SampleServlet.class.getName()).log(Level.SEVERE, null, ex);
    }
    return num;
}

 
}
