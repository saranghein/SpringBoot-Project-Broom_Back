package com.kwhackathon.broom.chat.service;

import com.kwhackathon.broom.chat.dto.ChatAckRequest;

public interface ChatAckService {

    void sendAckMessage(ChatAckRequest.Request chatAckRequest);
}
