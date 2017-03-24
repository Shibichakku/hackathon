package com.syntel.springboot;
 
import java.io.InputStream;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.websystique.springboot.controller.TemplateParser;
import com.syntel.springboot.model.User;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
 

public class SpringBootRestTestClient {
 
    public static final String REST_SERVICE_URI = "http://localhost:8080/SpringBootRestApi/api";
    
//     GET 
    @SuppressWarnings("unchecked")
    private static void listAllUsers(){
    
    http://localhost:8080/SpringBootRestApi/api/listAllUsers
        System.out.println("Testing listAllUsers API-----------");
         
        RestTemplate restTemplate = new RestTemplate();
        List<LinkedHashMap<String, Object>> usersMap = restTemplate.getForObject(REST_SERVICE_URI+"/user/", List.class);
         
        if(usersMap!=null){
            for(LinkedHashMap<String, Object> map : usersMap){
                System.out.println("User : id="+map.get("id")+", Name="+map.get("name")+", Age="+map.get("age")+", Salary="+map.get("salary"));;
            }
        }else{
            System.out.println("No user exist----------");
        }
    }
     
     /*   GET 
    private static void getUser(){
        System.out.println("Testing getUser API----------");
        RestTemplate restTemplate = new RestTemplate();
        User user = restTemplate.getForObject(REST_SERVICE_URI+"/user/1", User.class);
        System.out.println(user);
    }
     
     POST 
    private static void createUser() {
        System.out.println("Testing create User API----------");
        RestTemplate restTemplate = new RestTemplate();
        User user = new User(0,"Sarah",51,134);
        URI uri = restTemplate.postForLocation(REST_SERVICE_URI+"/user/", user, User.class);
        System.out.println("Location : "+uri.toASCIIString());
    }
 
     PUT 
    private static void updateUser() {
        System.out.println("Testing update User API----------");
        RestTemplate restTemplate = new RestTemplate();
        User user  = new User(1,"Tomy",33, 70000);
        restTemplate.put(REST_SERVICE_URI+"/user/7", user);
        System.out.println(user);
    }
 
     DELETE 
    private static void deleteUser() {
        System.out.println("Testing delete User API----------");
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.delete(REST_SERVICE_URI+"/user/8");
    }
 
 
     DELETE 
    private static void deleteAllUsers() {
        System.out.println("Testing all delete Users API----------");
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.delete(REST_SERVICE_URI+"/user/");
    }*/
 
    /* DELETE */
    private static void testSampleTemlate() {
        System.out.println("Testing testSampleTemlate  API----------");
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getForObject(REST_SERVICE_URI+"/template/",User.class);
    }
 
    /* getSample */
    private static void testGetSample() {
        System.out.println("Testing testSampleTemlate  API----------");
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> templates = restTemplate.getForObject(REST_SERVICE_URI+"/getSample/",Map.class);
        
        for(String key :templates.keySet()){
        	System.out.println(templates.get(key));
        }
    }
 
    private static void uploadTemplate() {
        System.out.println("Testing testSampleTemlate  API----------");
        RestTemplate restTemplate = new RestTemplate();
        InputStream fileName = TemplateParser.class.getResourceAsStream("Template.txt");
        
        Resource resource = new FileSystemResource("Template.txt");
    MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
    parts.add("Content-Type", "text/plain-bas");
    parts.add("file", resource);
//    parts.add("id_product", productId);

    restTemplate.exchange(REST_SERVICE_URI+"/uploadTemplate/", HttpMethod.GET,
                new HttpEntity<MultiValueMap<String, Object>>(parts),
                String.class); 
        
//        restTemplate.getForObject(REST_SERVICE_URI+"/template/",User.class);
    }
 
    
    
//    getSample
    
    public static void main(String args[]){
    	/* getUser();
        listAllUsers();
        deleteAllUsers();
        createUser();
        listAllUsers();
        updateUser();
        listAllUsers();
        deleteUser();
        listAllUsers();
        listAllUsers();
        testSampleTemlate();
        listAllUsers();*/
        
    	listAllUsers();
        
    }
}