package common;

/**
 * Error codes for the application.
 * All constants are implicitly public, static, and final.
 */
public interface Error {

    int OK = 200;
    int BAD_REQUEST = 400;
    int INTERNAL_SERVER_ERROR = 500;
    int SYSTEM_ERROR = 999;
    int DATENBANK_ERROR = 202;
}
