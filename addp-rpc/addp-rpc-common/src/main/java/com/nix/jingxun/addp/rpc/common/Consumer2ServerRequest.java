package com.nix.jingxun.addp.rpc.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author keray
 * @date 2018/12/09 14:04
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Consumer2ServerRequest {
    private String interfaceKey;
}
