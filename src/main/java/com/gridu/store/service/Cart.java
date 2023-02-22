package com.gridu.store.service;

import java.io.Serializable;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@Getter
@Setter
@SessionScope
public class Cart implements Serializable {
    // <productId, quantity>
    private HashMap<Long, Long> itemsList;

}
