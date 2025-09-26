package com.SwitchBoard.AccountService.Config;


import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

@Slf4j
public class NanoIdGenerator implements IdentifierGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        log.debug("NanoIdGenerator : generate : Generating new NanoId for entity - {}", object.getClass().getSimpleName());
        String generatedId = NanoIdUtils.randomNanoId(
                NanoIdUtils.DEFAULT_NUMBER_GENERATOR,
                NanoIdUtils.DEFAULT_ALPHABET,
                16
        );
        log.trace("NanoIdGenerator : generate : Generated NanoId - {}", generatedId);
        return generatedId;
    }
}

