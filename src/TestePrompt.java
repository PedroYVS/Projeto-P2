public class TestePrompt {
    public static void main(String[] args) throws Exception{
        var cliente = new ChatGPTClient();
        //Sem pergunta exemplo
        cliente.criarPergunta(null, "java", "dissertativa", "difícil", null);
        
    }
}
