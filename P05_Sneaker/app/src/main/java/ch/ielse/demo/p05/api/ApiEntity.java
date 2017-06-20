package ch.ielse.demo.p05.api;

import ch.ielse.demo.p05.BaseEntity;

public abstract class ApiEntity extends BaseEntity {
    static final int ERR_OK = 0;
    static final int ERR_EXPIRED_SESSION = 3;

    protected int err;
    protected String errMsg;

    protected abstract Object extraReal() throws Exception;
}
