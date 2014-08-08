/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.UvA.MLC.Settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author Mosi
 */
public class Config {
    public static Properties configFile = new Properties();
    public static File cFile;
    static{
        try {
//            	ClassLoader loader = Thread.currentThread().getContextClassLoader();  
	        cFile = new File("Config.properties");
                InputStream stream = new FileInputStream(cFile);            
//	      InputStream stream = Config.class.getResourceAsStream("Config.properties");
          configFile.load(stream);  
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
