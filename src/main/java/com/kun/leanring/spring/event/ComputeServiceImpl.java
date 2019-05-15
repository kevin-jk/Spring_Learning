package com.kun.leanring.spring.event;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @author: jrjiakun
 * Created on 2019/5/15 11:16
 */
@Service
public class ComputeServiceImpl implements ComputeService {
    @Override
    public int compute(int a, int b, String oper) {
        if (null != oper) {
            switch (oper) {
                case "+":
                    return a + b;

                default:
                    return a + b;

            }
        }
        return a + b;
    }
}
