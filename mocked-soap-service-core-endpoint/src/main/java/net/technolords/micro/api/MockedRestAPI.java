package net.technolords.micro.api;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;

/**
 * Created by Technolords on 2016-Jun-24.
 */
@WebService
public class MockedRestAPI {

    @WebMethod
    @WebResult
    public String mock() {
        return "Hello world";
    }
}
