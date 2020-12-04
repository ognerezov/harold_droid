package net.okhotnikov.harald.model.web;

public enum Error {
    CodeException(-6),
    CanceledPurchase (-5),
    UnParsableResponse (-4),
    WaitForEmail (-3),
    TimeOut (-2),
    Processing (-1),
    NoNetwork (0),
    PasswordsNotTheSame (77),
    NoException (200),
    WrongRequest (400),
    Unauthorized (401),
    PaymentNotVerified (402),
    Forbidden (403),
    NotFound (404),
    UnsupportedMethod (405),
    Conflict (409),
    Gone (410),
    DisabledUser (418),
    WrongTempToken (422),
    FailedDependency (424),
    ModificationProhibited (451),
    ServerException (500);

    public final int code;
    public String message;

    Error(int code) {
        this.code = code;
    }

    public static Error of(int code){
        for(Error error: Error.values()){
            if(error.code == code)
                return error;
        }
        return NoException;
    }

    public static Error of(int code, String message){
        Error error = Error.of(code);
        error.message = message;
        return error;
    }


    @Override
    public String toString() {
        return "Error{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
