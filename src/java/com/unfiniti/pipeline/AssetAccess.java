/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unfiniti.pipeline;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author John Bainbridge
 */
public class AssetAccess
{

    private static Map <String, String>  collectionOfAssets = new ConcurrentHashMap<>();
    private static String pathToFileInputStream;
    
    public static void setPathToFile(String path){
        pathToFileInputStream = path;
        populateMap();
    }
    
    public static void clearMap(){
       collectionOfAssets =  new ConcurrentHashMap<>();
       populateMap();
    }
    
    private static void populateMap()
    {
        Properties props = new Properties();
        if(pathToFileInputStream == null || pathToFileInputStream.isEmpty()){
            pathToFileInputStream = System.getenv("pathToAssetManifest");
        }
        try (InputStream in = new FileInputStream(pathToFileInputStream))
        {
            props.load(in);
            collectionOfAssets = new ConcurrentHashMap<>(props.size());
            Enumeration listofEntries = props.elements();
            
            while(listofEntries.hasMoreElements()){
                try{
                Map.Entry stuff = (Map.Entry) listofEntries.nextElement();
                collectionOfAssets.put( (String)stuff.getKey(), (String) stuff.getValue());
                } catch (java.lang.ClassCastException ex){
                    continue;
                }
                
            }
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static final String ASSET_URL(String assetPath)
    {
        if( collectionOfAssets.isEmpty()){
            populateMap();
        }
        String hashAsset = collectionOfAssets.get(assetPath);
        if (hashAsset != null)
        {
            return hashAsset;
        }
        return assetPath;
    }

    public static final String JS_TAG(String assetPath)
    {
        return "<script type=\"text/javascript\" src=\"" + ASSET_URL(assetPath) + "\"></script>";
    }

    public static final String CSS_TAG(String assetPath)
    {
        return "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + ASSET_URL(assetPath) + "\" />";
    }

    public static final String IMG_TAG(String assetPath, String... extras)
    {
        StringBuilder returnString = new StringBuilder();
        returnString.append("<img src=\"").append(ASSET_URL(assetPath)).append( "\" ");
        for (int i = 0; extras != null && i < extras.length; ++i)
        {
            returnString.append(extras[i]).append(" ");
        }
        returnString.append("/>");
        return returnString.toString();
    }
   
}
