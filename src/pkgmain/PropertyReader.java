package pkgmain;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

public class PropertyReader {

	private static final String sFileName = "ama_keeper.properties";

    private static String sDirSeparator = System.getProperty("file.separator");

    private static Properties props = new Properties(); 

    public static Properties GetProp() {

        // определяем текущий каталог    	

        File currentDir = new File(".");       
        FileInputStream ins = null;
        try { 
            // определяем полный путь к файлу

            String sFilePath = currentDir.getCanonicalPath() + sDirSeparator + sFileName;

            // создаем поток для чтения из файла

            ins = new FileInputStream(sFilePath);

            // загружаем свойства

            props.load(ins);

            // выводим значение для свойства mykey

            //System.out.println(props.getProperty("mykey"));
               
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
            e.printStackTrace();
            props = null;
        } catch (IOException e) {
            System.out.println("IO Error!");
            e.printStackTrace();
            props = null;            
        } finally {
        	try {
            	if (ins != null){
            		ins.close();
            	}	        		
        	} catch (Exception e) {
        		//nothing 
        	}        	
        }        
        return props;		       
    }

}


