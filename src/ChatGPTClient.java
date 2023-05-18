public class ChatGPTClient {
    public String criarPergunta(String openai_api_key, String assunto, String tipo, String dificuldade, String perguntaExemplo) throws Exception{
        //Montar prompt
        //Text Block
        String prompt="""
            Elabore uma questão sobre %s
            Do tipo %s%s
            Nível de dificuldade %s
            %s%s
        """.formatted(assunto, tipo, tipo.equalsIgnoreCase("alternativa") ? " com 4 alternativas" : "", dificuldade, perguntaExemplo == null ? "" : "Use a seguinte pergunta como exemplo", perguntaExemplo == null ? "" : perguntaExemplo);
        System.out.println(prompt);
        return prompt;
    }
}
