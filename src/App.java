import java.io.FileInputStream;
import java.util.Properties;

public class App {
    public static void main(String[] args) throws Exception {
        var properties = new Properties();
        properties.load(new FileInputStream("src/app.properties"));
        String openai_api_key = properties.getProperty("openai_api_key");
        
    }
}
