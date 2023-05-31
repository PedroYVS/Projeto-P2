import java.util.ArrayList;
import com.google.gson.Gson;
import lombok.Setter;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Setter
public class ChatGPTClient {
    private TipoPergunta opc;
    
    public ArrayList<String> criarPergunta(String OPENAI_API_KEY, String entrada) throws Exception{
        //Montar prompt
        //Text Block
        String prompt="";
        switch(opc){
            case Traducao:{
                prompt="""
                    Traduza o texto a seguir para o português: %s
                """.formatted(entrada);
                break;
            }
            case GerarEmojis:{
                prompt="""
                    Gere somente 3 emojis com base no filme %s
                """.formatted(entrada);
                break;
            }
            case ExplicacaoParaCriancas:{
                prompt="""
                    Responda a seguinte pergunta de forma que uma criança pequena entenda, usando um máximo de 30 palavras: %s
                """.formatted(entrada);
                break;
            }
        }
        var requisicao = new ChatGPTRequest("text-davinci-003", prompt, 100, opc==TipoPergunta.Traducao ? 2 : 1);
        var gson = new Gson();
        String requisicaoJSON = gson.toJson(requisicao);
        RequestBody corpoRequisicao = RequestBody.create(requisicaoJSON, MediaType.parse("application/json"));
        OkHttpClient httpClient = new OkHttpClient();
        Request req = new Request.Builder().url("https://api.openai.com/v1/completions").addHeader("Media-Type", "application.json").addHeader("Authorization", "Bearer " + OPENAI_API_KEY).post(corpoRequisicao).build();
        Response response = httpClient.newCall(req).execute();
        ChatGPTResponse respostaCHATPGT = gson.fromJson(response.body().string(), ChatGPTResponse.class);
        var completions = new ArrayList<String>();
        for(int i=0; i<(opc==TipoPergunta.Traducao ? 2 : 1); i++){
            completions.add(respostaCHATPGT.getChoices().get(i).getText().trim());
        }
        //System.out.println(completions);
        return completions;
    }
}
