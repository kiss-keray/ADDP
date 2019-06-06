package com.nix.jingxun.addp.web.service.socket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author keray
 * @date 2019/06/06 12:32
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketMsg<T> {
    private String type;
    private T data;
    public static  <M>  WebSocketMsg<M> of(String type,M m) {
        return (WebSocketMsg<M>) WebSocketMsg.builder()
                .type(type)
                .data(m)
                .build();
    }
}
