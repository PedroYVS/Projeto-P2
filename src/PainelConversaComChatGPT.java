import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;

@Getter
@AllArgsConstructor
enum TipoPergunta{
    Traducao("Traduzir frases"),
    GerarEmojis("Gerar emojis com base em filmes"),
    ExplicacaoParaCriancas("Explicações para crianças");

    private final String descricao;
}

public class PainelConversaComChatGPT extends JFrame {
    private final ChatGPTClient clienteChatGPT = new ChatGPTClient();
    private final ButtonGroup grupoOpcoes = new ButtonGroup();
    private final JRadioButton opTraducao = new AlternativasDePerguntas(TipoPergunta.Traducao);
    private final JRadioButton opGerarEmojis = new AlternativasDePerguntas(TipoPergunta.GerarEmojis);
    private final JRadioButton opExplicarParaCriancas = new AlternativasDePerguntas(TipoPergunta.ExplicacaoParaCriancas);
    private final JButton enviar = new JButton("ENVIAR");
    private final GroupLayout layout = new GroupLayout(getContentPane());
    private final TextField entrada = new TextField();
    private final JTextPane saida;
    private final JScrollPane saidaComScroll;
    private final SimpleAttributeSet alinhamentoDireita = new SimpleAttributeSet();
    private final SimpleAttributeSet alinhamentoEsquerda = new SimpleAttributeSet();
    private final String OPENAI_API_KEY;
    private final StyledDocument doc = new DefaultStyledDocument();
    private final JPanel painelOpcoes = new JPanel();

    PainelConversaComChatGPT() throws Exception {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Conversa com ChatGPT");
        setMinimumSize(new Dimension(1100, 500));

        setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);  

        var properties = new Properties();
        properties.load(new FileInputStream("app.properties"));
        OPENAI_API_KEY = properties.getProperty("OPENAI_API_KEY");
        
        saida = new JTextPane(doc);
        saida.setEditable(false);
        saidaComScroll = new JScrollPane(saida, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        opTraducao.setSelected(true);

        painelOpcoes.setLayout(new BoxLayout(painelOpcoes, BoxLayout.Y_AXIS));
        painelOpcoes.add(opTraducao);
        painelOpcoes.add(opGerarEmojis);
        painelOpcoes.add(opExplicarParaCriancas);

        setLayout();
        setFormatacaoPainelSaida();

        enviar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                acaoBotaoEnviar(); 
            }
        });

        entrada.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enviar.doClick();
            }
        });

    }

    private void insereNoPainel(SimpleAttributeSet tipoAlinhamento, String mensagem){
        int tamanhoDoc = doc.getLength();
        try{
            doc.insertString(tamanhoDoc, mensagem, tipoAlinhamento);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        doc.setParagraphAttributes(tamanhoDoc, mensagem.length(), tipoAlinhamento, true);
    }

    private void acaoBotaoEnviar(){
        if(!(entrada.getText().equals(""))){
            TipoPergunta op = TipoPergunta.valueOf(grupoOpcoes.getSelection().getActionCommand());
            clienteChatGPT.setOpc(op);
            String ent = String.format("Usuário:\n%s\n\n", entrada.getText());
            insereNoPainel(alinhamentoEsquerda, ent);
            entrada.setText(null);;
            ArrayList<String> textoSaida = null;
            try{
                textoSaida = clienteChatGPT.criarPergunta(OPENAI_API_KEY, ent);
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
            String res = null;
            if(op==TipoPergunta.Traducao){
                res = String.format("ChatGPT:\n1ªPossibilidade: %s\n2ªPossibilidade: %s\n\n", textoSaida.get(0), textoSaida.get(1));
            }
            else res = String.format("ChatGPT:\n%s\n\n", textoSaida.get(0));
            insereNoPainel(alinhamentoDireita, res);
        }
    }

    private void setLayout(){
        layout.setHorizontalGroup(
            layout.createSequentialGroup()
            .addGroup(
                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(saidaComScroll)
                .addComponent(entrada)
            )
            .addGroup(
                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(painelOpcoes)
                .addComponent(enviar)
            )
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
            .addGroup(
                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(saidaComScroll)
                .addComponent(painelOpcoes)
            )
            .addGroup(
                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(entrada,  GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(enviar)
            )
        );
    }

    private void setFormatacaoPainelSaida(){
        StyleConstants.setAlignment(alinhamentoDireita, StyleConstants.ALIGN_RIGHT);
        StyleConstants.setForeground(alinhamentoDireita, Color.RED);
        StyleConstants.setFontSize(alinhamentoDireita, 20);
        StyleConstants.setAlignment(alinhamentoEsquerda, StyleConstants.ALIGN_LEFT);
        StyleConstants.setForeground(alinhamentoEsquerda, Color.BLUE);
        StyleConstants.setFontSize(alinhamentoEsquerda, 20);
    }

    private class AlternativasDePerguntas extends JRadioButton{
        AlternativasDePerguntas(TipoPergunta tp){
            super(tp.getDescricao());
            setActionCommand(tp.name());
            grupoOpcoes.add(this);
        }
    }
}

