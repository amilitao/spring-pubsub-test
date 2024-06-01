package br.com.amilitao.springpubsubtest.controller.dto;


import java.util.List;


public class LoteMensagemDto {
    private List<MensagemDto> mensagens;

    public List<MensagemDto> getMensagens() {
        return mensagens;
    }

    public void setMensagens(List<MensagemDto> mensagens) {
        this.mensagens = mensagens;
    }
}
